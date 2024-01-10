package tech.mania.core.util;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockData {

    private final BlockPos pos;
    private final EnumFacing direction;

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getDirection() {
        return direction;
    }

    public AxisAlignedBB toAxisAlignedBB() {
        final BlockPos pos = this.pos;
        switch (this.direction) {
            case DOWN: return new AxisAlignedBB(pos, new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1));
            case NORTH: return new AxisAlignedBB(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ()));
            case EAST: return new AxisAlignedBB(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()), new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
            case SOUTH: return new AxisAlignedBB(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1), new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
            case UP: return new AxisAlignedBB(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
            case WEST: return new AxisAlignedBB(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ() + 1));
        }
        return null;
    }

    public BlockData(BlockPos pos, EnumFacing direction) {
        this.pos = pos;
        this.direction = direction;
    }
}
