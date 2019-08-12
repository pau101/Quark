package vazkii.quark.tweaks.module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.moduleloader.LoadModule;
import vazkii.quark.base.moduleloader.Module;
import vazkii.quark.base.moduleloader.ModuleCategory;
import vazkii.quark.base.moduleloader.SubscriptionTarget;

@LoadModule(category = ModuleCategory.TWEAKS, subscriptions = SubscriptionTarget.BOTH_SIDES)
public class DirtToPath extends Module {

	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		PlayerEntity player = event.getPlayer();
		BlockPos pos = event.getPos();
		Direction facing = event.getFace();
		World world = event.getWorld();
		Hand hand = event.getHand();
		ItemStack itemstack = player.getHeldItem(hand);
		BlockState state = world.getBlockState(pos);

		if(/*itemstack.getItem() == Pickarang.pickarang || */!itemstack.getItem().getToolTypes(itemstack).contains(ToolType.SHOVEL) && itemstack.getDestroySpeed(state) > 0)
			return;

		if(facing != null && player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
			Block block = state.getBlock();

			if(facing != Direction.DOWN && world.getBlockState(pos.up()).getMaterial() == Material.AIR && block == Blocks.DIRT) {
				BlockState pathState = Blocks.GRASS_PATH.getDefaultState();
				world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

				if(!world.isRemote) {
					world.setBlockState(pos, pathState, 11);
					itemstack.damageItem(1, player, (p) -> {
		                p.sendBreakAnimation(hand);
		             });
				}

				event.setCanceled(true);
				event.setCancellationResult(ActionResultType.SUCCESS);
			}
		}
	}
	
}