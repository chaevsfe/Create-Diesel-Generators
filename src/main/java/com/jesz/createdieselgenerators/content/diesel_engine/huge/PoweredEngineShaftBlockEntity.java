package com.jesz.createdieselgenerators.content.diesel_engine.huge;

import com.mojang.serialization.Codec;
import com.zurrtum.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.zurrtum.create.catnip.data.Couple;
import com.zurrtum.create.catnip.data.Pair;
import com.zurrtum.create.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.jesz.createdieselgenerators.content.diesel_engine.huge.HugeDieselEngineBlock.FACING;
import static com.zurrtum.create.content.kinetics.base.RotatedPillarKineticBlock.AXIS;

public class PoweredEngineShaftBlockEntity extends GeneratingKineticBlockEntity {
    float stressCapacity;
    float speed;
    int movementDirection;
    BlockPos lastKnownPos = worldPosition;

    public PoweredEngineShaftBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        movementDirection = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!worldPosition.equals(lastKnownPos)) {
            BlockPos offset = worldPosition.subtract(lastKnownPos);
            lastKnownPos = worldPosition;

            for (int i = 0; i < engines.size(); i++) {
                Pair<BlockPos, Couple<Float>> engine = engines.get(i);
                if (engine != null)
                    engines.set(i, Pair.of(engine.getFirst().offset(offset), engine.getSecond()));
            }
        }
    }

    public boolean isEngineForConnectorDisplay(BlockPos pos ) {

        Direction.Axis axis = getBlockState().getValue(AXIS);
        for (Direction d : List.of(axis == Direction.Axis.Z ? Direction.UP : Direction.NORTH, axis == Direction.Axis.Z ? Direction.DOWN : Direction.SOUTH, axis == Direction.Axis.X ? Direction.UP : Direction.EAST, axis == Direction.Axis.X ? Direction.DOWN : Direction.WEST)) {
            BlockState st = getLevel().getBlockState(getBlockPos().relative(d, 2));
            if(st.getBlock() instanceof HugeDieselEngineBlock && st.getValue(FACING) == d.getOpposite())
                return(getBlockPos().relative(d, 2).equals(pos));
        }
        return false;
    }

    public List<Pair<BlockPos, Couple<Float>>> engines = new ArrayList<>(4);

    public void update(BlockPos sourcePos, int direction, float stress, float speed) {
        Pair<BlockPos, Couple<Float>> found = null;
        for (Pair<BlockPos, Couple<Float>> engine : engines)
            if (engine.getFirst().equals(sourcePos)) {
                found = engine;
                break;
            }

        List<Pair<BlockPos, Couple<Float>>> newEngines = new ArrayList<>(engines);
        if (found != null) {
            Couple<Float> status = found.getSecond();
            if (status.getFirst() == stress && status.getSecond() == speed)
                return;
            newEngines.remove(found);
        }
        newEngines.add(Pair.of(sourcePos, Couple.create(stress, speed)));
        engines = newEngines;


        AtomicReference<Float> maxSpeed = new AtomicReference<>(0f);

        for (Pair<BlockPos, Couple<Float>> engine : engines) {
            if (engine.getSecond().getSecond() > maxSpeed.get())
                maxSpeed.set(engine.getSecond().getSecond());
        }

        this.speed = maxSpeed.get();
        this.movementDirection = direction;

        reActivateSource = true;
    }

    public void removeGenerator(BlockPos sourcePos) {
        List<Pair<BlockPos, Couple<Float>>> newEngines = new ArrayList<>(engines);
        boolean removed = newEngines.removeIf(p -> p.getFirst().equals(sourcePos));
        engines = newEngines;

        if (engines.isEmpty()) {
            movementDirection = 0;
            speed = 0;
            stressCapacity = 0;
        }
        if (removed)
            reActivateSource = true;
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);

        tag.putInt("Direction", movementDirection);
        ValueOutput.ValueOutputList engineList = tag.childrenList("Engines");
        for (Pair<BlockPos, Couple<Float>> engine : List.copyOf(engines)) {
            ValueOutput engineTag = engineList.addChild();
            engineTag.putFloat("Capacity", engine.getSecond().getFirst());
            engineTag.putFloat("Speed", engine.getSecond().getSecond());
            engineTag.store("Pos", BlockPos.CODEC, engine.getFirst());
        }
        tag.putFloat("GeneratedSpeed", speed);
        if (lastKnownPos != null)
            tag.store("LastKnownPos", BlockPos.CODEC, lastKnownPos);
    }

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        movementDirection = tag.getIntOr("Direction", 0);

        List<Pair<BlockPos, Couple<Float>>> newEngines = new ArrayList<>();
        for (ValueInput engineTag : tag.childrenListOrEmpty("Engines")) {
            Optional<BlockPos> enginePos = readBlockPos(engineTag, "Pos");
            if (enginePos.isEmpty())
                continue;
            newEngines.add(Pair.of(enginePos.get(),
                    Couple.create(engineTag.getFloatOr("Capacity", 0f), engineTag.getFloatOr("Speed", 0f))));
        }
        engines = newEngines;

        speed = tag.getFloatOr("GeneratedSpeed", 0f);
        lastKnownPos = readBlockPos(tag, "LastKnownPos").orElse(worldPosition);
    }

    private static Optional<BlockPos> readBlockPos(ValueInput tag, String key) {
        return tag.read(key, Codec.PASSTHROUGH).flatMap(pos -> BlockPos.CODEC.parse(pos).result()
                .or(() -> pos.get("X").asNumber().result()
                        .map(x -> new BlockPos(x.intValue(), pos.get("Y").asInt(0), pos.get("Z").asInt(0)))));
    }

    @Override
    public float getGeneratedSpeed() {
        return movementDirection * speed;
    }

    @Override
    public float calculateAddedStressCapacity() {
        if(movementDirection == 0)
            return 0;
        float capacity = 0;
        for (Pair<BlockPos, Couple<Float>> engine : engines)
            capacity += engine.getSecond().getFirst();
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    @Override
    public int getRotationAngleOffset(Direction.Axis axis) {
        int combinedCoords = axis.choose(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        return super.getRotationAngleOffset(axis) + (combinedCoords % 2 == 0 ? 180 : 0);
    }
}
