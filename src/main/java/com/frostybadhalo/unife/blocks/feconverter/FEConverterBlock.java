package com.frostybadhalo.unife.blocks.feconverter;

import com.frostybadhalo.unife.registry.UnifeBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.electricity.base.DirectionalElectricBlock;
import org.patryk3211.powergrid.electricity.base.IDecoratedTerminal;
import org.patryk3211.powergrid.electricity.base.TerminalBoundingBox;
import org.patryk3211.powergrid.electricity.base.terminals.BlockStateTerminalCollection;
import org.patryk3211.powergrid.electricity.deviceconnector.DeviceConnectorBlock;

public class FEConverterBlock extends DirectionalElectricBlock
        implements IBE<FEConverterBlockEntity>, IWrenchable {

    private static final TerminalBoundingBox[] TERMINALS = {
            new TerminalBoundingBox(IDecoratedTerminal.POSITIVE, 13.25, 14, 1.25, 14.75, 16.75, 2.75)
                    .withColor(16726843),
            new TerminalBoundingBox(IDecoratedTerminal.NEGATIVE, 1.25, 14, 1.25, 2.75, 16.75, 2.75)
                    .withColor(3899647),
            new TerminalBoundingBox(IDecoratedTerminal.POSITIVE, 1.25, 14, 13.25, 2.75, 16.75, 14.75)
                    .withColor(16726843),
            new TerminalBoundingBox(IDecoratedTerminal.NEGATIVE, 13.25, 14, 13.25, 14.75, 16.75, 14.75)
                    .withColor(3899647)
    };

    public FEConverterBlock(Properties settings) {
        super(settings);
        setTerminalCollection(
                BlockStateTerminalCollection.builder(this)
                        .forAllStates(state -> BlockStateTerminalCollection.each(
                                TERMINALS, terminal -> {
                                    Direction facing = state.getValue(FACING);
                                    return switch (facing) {
                                        case UP    -> terminal.rotateAroundX(180);
                                        case NORTH -> terminal.rotateAroundZ(90).rotateAroundY(90);
                                        case SOUTH -> terminal.rotateAroundZ(90).rotateAroundY(-90);
                                        case EAST  -> terminal.rotateAroundZ(-90);
                                        case WEST  -> terminal.rotateAroundZ(90);
                                        default    -> terminal;
                                    };
                                }
                        ))
                        .build()
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getClickedFace().getOpposite());
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!level.isClientSide) {
            Direction current = state.getValue(FACING);
            Direction next = current.getClockWise();
            level.setBlock(pos, state.setValue(FACING, next), 3);
            IWrenchable.playRotateSound(level, pos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack drop = new ItemStack(this);

        if (player != null && !player.isCreative()) {
            if (!player.getInventory().add(drop)) {
                player.drop(drop, false);
            }
        }

        level.destroyBlock(pos, false);
        IWrenchable.playRotateSound(level, pos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        ItemStack tool = player.getMainHandItem();
        if (tool.getItem() instanceof AxeItem) {
            return super.getDestroyProgress(state, player, level, pos) * 5.0f;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos neighborPos = pos.relative(facing);
        BlockState neighborState = world.getBlockState(neighborPos);
        return DeviceConnectorBlock.canSupport(world, neighborPos, neighborState, facing.getOpposite())
                || neighborState.isFaceSturdy(world, neighborPos, facing.getOpposite());
    }

    @Override
    public Class<FEConverterBlockEntity> getBlockEntityClass() {
        return FEConverterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FEConverterBlockEntity> getBlockEntityType() {
        return UnifeBlockEntities.FE_CONVERTER.get();
    }
}