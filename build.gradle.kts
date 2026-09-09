import java.util.zip.ZipFile

plugins {
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
    `maven-publish`
}

group = property("maven_group") as String
version = "${property("mod_version")}+fabric-mc${property("minecraft_version")}"

base {
    archivesName.set(property("archives_base_name") as String)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
    maven("https://maven.shedaniel.me/") {
        content {
            includeGroup("me.shedaniel.cloth")
            includeGroup("me.shedaniel.cloth.api")
        }
    }
}

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

    compileOnly("maven.modrinth:rei:${property("rei_version")}")
    compileOnly("maven.modrinth:architectury-api:${property("architectury_version")}")
    compileOnly("me.shedaniel.cloth:basic-math:${property("basic_math_version")}")
    compileOnly(files(fileTree("../../create-rei/CreateReiViewer-Fly/build/libs") {
        include("CreateReiViewer-*.jar")
        exclude("*-sources.jar")
    }.files.maxByOrNull { it.lastModified() } ?: error("No Create Fly Recipe Viewer jar in ../../create-rei/CreateReiViewer-Fly/build/libs")))
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

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    inputs.property("version", version)
    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version.toString(),
            "minecraft_dependency_version" to project.property("minecraft_dependency_version") as String,
            "fabric_loader_version" to project.property("fabric_loader_version") as String,
            "create_fabric_version_range" to project.property("create_fabric_version_range") as String,
        )
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
