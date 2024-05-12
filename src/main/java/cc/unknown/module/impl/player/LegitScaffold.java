package cc.unknown.module.impl.player;

import java.util.function.BooleanSupplier;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.ClickGuiEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldSettings;

@Register(name = "LegitScaffold", category = Category.Player)
public class LegitScaffold extends Module {
	public SliderValue shiftTime = new SliderValue("Shift Time", 140, 0, 200, 5);
    private BooleanValue onlyGround = new BooleanValue("Only Ground", false);
    private BooleanValue holdShift = new BooleanValue("Hold Sneak", false);
    private BooleanValue slotSwap = new BooleanValue("Block Switching", true);
	public BooleanValue blocksOnly = new BooleanValue("Blocks Only", true);
	public BooleanValue backwards = new BooleanValue("Backwards Movement Only", true);

    private boolean shouldBridge = false;
    private Cold shiftTimer = new Cold(0);

	public LegitScaffold() {
		this.registerSetting(shiftTime, onlyGround, holdShift, slotSwap, blocksOnly, backwards);
	}

	@EventLink
	public void onGui(ClickGuiEvent e) {
		this.setSuffix("- [" + shiftTime.getInputToInt() + " ms]");
	}
	
	@Override
	public void onDisable() {
		mc.gameSettings.keyBindSneak.pressed = false;
		if (PlayerUtil.playerOverAir()) {
			mc.gameSettings.keyBindSneak.pressed = false;
		}
	}

	@EventLink
	public void onSuicide(TickEvent e) {
        if (mc.currentScreen != null) return;
        if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) return;
        
        if (PlayerUtil.playerOverAir() && (!onlyGround.isToggled() || mc.thePlayer.onGround) && mc.thePlayer.motionY < 0.1) {
        	shiftTimer.reset();
        }
        
        if (backwards.isToggled() && shouldBridgeCheck()) {
            shouldBridge = false;
            return;
        }
        
        if (blocksOnly.isToggled() && shouldSkipBlockCheck()) return;
        
		shouldBridge = !shiftTimer.reached((long) shiftTime.getInput());

        if (holdShift.isToggled()) {
            mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && shouldBridge;
        } else {
            mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) || shouldBridge;
        }
	}

	@EventLink
	public void onRender(RenderEvent e) {
	    if (PlayerUtil.inGame() && e.is3D()) {
			if (mc.currentScreen != null || mc.thePlayer.getHeldItem() == null) return;
			if ((mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) && slotSwap.isToggled())
				swapToBlock();
	    }
	}
	
	public void swapToBlock() {
		for (int slot = 0; slot <= 8; slot++) {
			ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(slot);
			if (itemInSlot != null && itemInSlot.getItem() instanceof ItemBlock && itemInSlot.stackSize > 0) {
				ItemBlock itemBlock = (ItemBlock) itemInSlot.getItem();
				Block block = itemBlock.getBlock();
				if (mc.thePlayer.inventory.currentItem != slot && block.isFullCube()) {
					mc.thePlayer.inventory.currentItem = slot;
				} else {
					return;
				}
				return;
			}
		}
	}
	
	private boolean shouldSkipBlockCheck() {
	    return ((BooleanSupplier) () -> {
	    	ItemStack heldItem = mc.thePlayer.getHeldItem();
	    	return heldItem == null || !(heldItem.getItem() instanceof ItemBlock);
	    }).getAsBoolean();
	}
	
	private boolean shouldBridgeCheck() {
	    return ((BooleanSupplier) () -> {
	        double moveForward = mc.thePlayer.movementInput.moveForward;
	        double moveStrafe = mc.thePlayer.movementInput.moveStrafe;
	        return (moveForward > 0 && moveStrafe == 0) || moveForward >= 0;
	    }).getAsBoolean();
	}
}
