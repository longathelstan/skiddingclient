package cc.unknown.module.impl.visuals;

import java.awt.Color;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.other.AntiBot;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

public class ESP extends Module {
	private ModeValue mode = new ModeValue("ESP Types", "2D", "2D", "Health", "Box");
	private SliderValue color = new SliderValue("Color [H/S/B]", 0, 0, 350, 10);
	private BooleanValue chestESP = new BooleanValue("ChestESP", false);
	private BooleanValue invi = new BooleanValue("Show invis", true);
	private BooleanValue tim = new BooleanValue("Color team", true);
	private BooleanValue dmg = new BooleanValue("Red on damage", true);

	public ESP() {
		super("ESP", ModuleCategory.Visuals);
		this.registerSetting(mode, color, chestESP, invi, dmg, tim);

	}

	@EventLink
	public void onRender(Render3DEvent e) {
		if (PlayerUtil.inGame()) {
			float hue = (float) (color.getInput() % 360) / 360.0f;
			int rgb = Color.HSBtoRGB(hue, 1.0f, 1.0f);

			mc.theWorld.playerEntities.forEach(en -> {
				if (en == mc.thePlayer || en.deathTime != 0 || (!invi.isToggled() && en.isInvisible())
						|| AntiBot.bot(en)) {
					return;
				}

				int armorColor = getColor(en.getCurrentArmor(2));
				if (tim.isToggled() && armorColor > 0) {
					renderPlayer(en, armorColor);
				} else {
					renderPlayer(en, rgb);
				}
			});

			if (chestESP.isToggled()) {
				mc.theWorld.loadedTileEntityList.forEach(te -> {
					if (te instanceof TileEntityChest || te instanceof TileEntityEnderChest) {
						RenderUtil.drawChestBox(te.getPos(), rgb, true);
					}
				});
			}
		}
	}

	public int getColor(ItemStack x) {
		if (x == null) {
			return -1;
		}
		NBTTagCompound nbt = x.getTagCompound();
		if (nbt != null) {
			NBTTagCompound displayTag = nbt.getCompoundTag("display");
			if (displayTag != null && displayTag.hasKey("color", 3)) {
				return displayTag.getInteger("color");
			}
		}

		return -2;
	}

	private void renderPlayer(Entity en, int rgb) {
		switch (mode.getMode()) {
		case "Box":
			RenderUtil.drawBoxAroundEntity(en, 1, 0.0D, 0.0D, rgb, dmg.isToggled());
			break;
		case "2D":
			RenderUtil.drawBoxAroundEntity(en, 2, 0.0D, 0.0D, rgb, dmg.isToggled());
			break;
		case "Health":
			RenderUtil.drawBoxAroundEntity(en, 3, 0.0D, 0.0D, rgb, dmg.isToggled());
			break;
		}
	}
}