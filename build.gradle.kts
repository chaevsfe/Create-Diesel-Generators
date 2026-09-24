import java.util.zip.ZipFile
import javax.imageio.ImageIO

plugins {
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
    `maven-publish`
}

group = property("maven_group") as String
version = "${property("mod_version")}+fly-mc${property("mc_line")}"

base {
    archivesName.set(property("archives_base_name") as String)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
}

// The recipe viewer is nested into this jar and compiled against: CI downloads its release
// jar into libs/, a local checkout uses the sibling repo's build output.
repositories {
    flatDir {
        dirs("libs", "../../create-rei/CreateReiViewer-Fly/build/libs", "../../create-struts/StrutYourStuff-Fly/build/libs")
    }
}
val recipeViewer = ":CreateReiViewer:${property("createreiviewer_version")}+fabric-mc${property("minecraft_version")}"
val struts = ":StrutYourStuff:${property("struts_version")}+fabric-mc${property("minecraft_version")}"

loom {
    accessWidenerPath = file("src/main/resources/createdieselgenerators.accesswidener")
    mods {
        create("createdieselgenerators") {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets {
    main {
        resources.srcDir("src/generated/resources")
        java {
            exclude(rootProject.file("deferred/excludes.txt").readLines()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") })
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    implementation("maven.modrinth:create-fly:${property("create_fabric_version")}")

    compileOnly(recipeViewer)
    include(recipeViewer)
    compileOnly(struts)
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "10000", "-Xmaxwarns", "1000"))
}

tasks.withType<AbstractCopyTask>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val generatedConnectedTextureResources = layout.buildDirectory.dir("generated/connected-texture-resources")

val crossTileIndexes = (1..15).toList()
val rectangleTileIndexes = (0..11).toList() + (13..15).toList()

val connectedTextureSheets = mapOf(
    "assets/createdieselgenerators/textures/block/diesel_engine_big_connected.png" to (4 to crossTileIndexes),
    "assets/createdieselgenerators/textures/block/bulk_fermenter/*_connected.png" to (4 to rectangleTileIndexes),
    "assets/createdieselgenerators/textures/block/distillation_tower/*_connected.png" to (4 to rectangleTileIndexes),
    "assets/createdieselgenerators/textures/block/oil_barrel/**/*_connected.png" to (4 to rectangleTileIndexes),
)
val connectedTextureSheetsAlsoUsedByModels = listOf(
    "assets/createdieselgenerators/textures/block/bulk_fermenter/*_connected.png",
    "assets/createdieselgenerators/textures/block/distillation_tower/*_connected.png",
)

val generateConnectedTextureSprites = tasks.register("generateConnectedTextureSprites") {
    val resourceRoot = file("src/main/resources")
    inputs.files(fileTree(resourceRoot) { include(connectedTextureSheets.keys) })
        .withPropertyName("connectedTextureSheets")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.property("connectedTextureLayout", "create-fly-26.2-v1")
    outputs.dir(generatedConnectedTextureResources)
    doLast {
        val outputRoot = generatedConnectedTextureResources.get().asFile
        delete(outputRoot)
        var sheetCount = 0
        var spriteCount = 0
        connectedTextureSheets.forEach { (pattern, layout) ->
            val (gridSize, tileIndexes) = layout
            fileTree(resourceRoot) { include(pattern) }.files.sortedBy { it.invariantSeparatorsPath }.forEach { sheetFile ->
                val sheet = ImageIO.read(sheetFile) ?: throw GradleException("Could not decode $sheetFile")
                if (sheet.width != sheet.height || sheet.width % gridSize != 0) {
                    throw GradleException("$sheetFile must be a $gridSize x $gridSize grid of square tiles, but is ${sheet.width} x ${sheet.height}")
                }
                val tileSize = sheet.width / gridSize
                val relativeSheet = resourceRoot.toPath().relativize(sheetFile.toPath()).toString()
                val spriteDirectory = outputRoot.resolve(relativeSheet.removeSuffix(".png"))
                spriteDirectory.mkdirs()
                tileIndexes.forEachIndexed { index, sourceTileIndex ->
                    val tile = sheet.getSubimage(sourceTileIndex % gridSize * tileSize, sourceTileIndex / gridSize * tileSize, tileSize, tileSize)
                    if (!ImageIO.write(tile, "png", spriteDirectory.resolve("${index + 1}.png"))) {
                        throw GradleException("No PNG writer is available for $spriteDirectory")
                    }
                    spriteCount++
                }
                sheetCount++
            }
        }
        logger.lifecycle("Generated $spriteCount connected-texture sprites from $sheetCount sheets")
    }
}

tasks.processResources {
    dependsOn(generateConnectedTextureSprites)
    from(generatedConnectedTextureResources)
    exclude(connectedTextureSheets.keys - connectedTextureSheetsAlsoUsedByModels.toSet())
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val modMetadata = mapOf(
        "version" to project.version.toString(),
        "mc_line" to project.property("mc_line") as String,
        "minecraft_dependency_version" to project.property("minecraft_dependency_version") as String,
        "fabric_loader_version" to project.property("fabric_loader_version") as String,
        "create_fabric_version_range" to project.property("create_fabric_version_range") as String,
    )
    inputs.properties(modMetadata)
    filesMatching("fabric.mod.json") {
        expand(modMetadata)
    }
}

tasks.jar {
    from("LICENSE")
    from("NOTICE")
}

tasks.named<Jar>("sourcesJar") {
    from("LICENSE")
    from("NOTICE")
}

val ownedSourceRoots = listOf("com/jesz/createdieselgenerators/")
val allowedResourceRoots = listOf(
    "assets/createdieselgenerators/",
    "data/createdieselgenerators/",
    "data/c/",
    "data/create/",
    "data/minecraft/",
    "data/railways/",
    "data/farmersdelight/",
    "data/immersiveengineering/"
)
val allowedRootFiles = listOf(
    "fabric.mod.json",
    "createdieselgenerators.mixins.json",
    "createdieselgenerators.client.mixins.json",
    "createdieselgenerators.accesswidener",
    "icon.png",
    "LICENSE",
    "NOTICE",
    "pack.mcmeta"
)

fun foreignEntries(archive: File): List<String> {
    val bad = mutableListOf<String>()
    ZipFile(archive).use { zip ->
        val entries = zip.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.isDirectory) continue
            val name = entry.name
            if (name.startsWith("META-INF/")) continue
            if (name in allowedRootFiles) continue
            if (allowedResourceRoots.any { name.startsWith(it) }) continue
            if (name.endsWith(".class") || name.endsWith(".java")) {
                if (ownedSourceRoots.none { name.startsWith(it) }) bad.add(name)
                continue
            }
            bad.add(name)
        }
    }
    return bad
}

val checkNamespaces = tasks.register("checkNamespaces") {
    description = "Fails if the mod jar or the sources jar carries an entry outside the mod's own namespace."
    group = "verification"
    dependsOn(tasks.named("jar"), tasks.named("sourcesJar"))
    doLast {
        val archives = listOf(
            tasks.getByName<Jar>("jar").archiveFile.get().asFile,
            tasks.getByName<Jar>("sourcesJar").archiveFile.get().asFile
        )
        val problems = archives.flatMap { archive ->
            foreignEntries(archive).map { "${archive.name}: $it" }
        }
        if (problems.isNotEmpty()) {
            throw GradleException(
                "Foreign entries in the published artifacts:\n" + problems.joinToString("\n")
            )
        }
        logger.lifecycle("checkNamespaces: ${archives.size} archives clean")
    }
}

tasks.named("check") {
    dependsOn(checkNamespaces)
}

tasks.register("printCompileClasspath") {
    val cp = sourceSets.main.get().compileClasspath
    val out = layout.projectDirectory.file(".classpath.txt")
    doLast {
        out.asFile.writeText(cp.files.joinToString("\n") { it.absolutePath } + "\n")
    }
}
