package cc.unknown.utils.client;

import static cc.unknown.ui.EditHudPositionScreen.arrayListX;
import static cc.unknown.ui.EditHudPositionScreen.arrayListY;

import cc.unknown.utils.Loona;

public enum FuckUtil implements Loona {
	instance;
	
	private PositionMode positionMode;

	private int waifuX = 340;
	private int waifuY = 135;
	public final String WaifuX = "WaifuX:";
	public final String WaifuY = "WaifuY:";

	public PositionMode getPostitionMode(int marginX, int marginY, double height, double width) {
		int halfHeight = (int) (height / 4);
		int halfWidth = (int) width;
		PositionMode positionMode = null;

		if (marginY < halfHeight) {
			if (marginX < halfWidth) {
				positionMode = PositionMode.UPLEFT;
			}
			if (marginX > halfWidth) {
				positionMode = PositionMode.UPRIGHT;
			}
		}

		if (marginY > halfHeight) {
			if (marginX < halfWidth) {
				positionMode = PositionMode.DOWNLEFT;
			}
			if (marginX > halfWidth) {
				positionMode = PositionMode.DOWNRIGHT;
			}
		}

		return positionMode;
	}

	public enum PositionMode {
		UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT
	}
	
	public void setArrayListX(int x) {
	    arrayListX.set(x);
	}
	
	public void setArrayListY(int x) {
		arrayListY.set(x);
	}

	public int getArrayListX() {
		return arrayListX.get();
	}

	public int getArrayListY() {
		return arrayListY.get();
	}

	public void setWaifuX(int x) {
		waifuX = x;
	}

	public void setWaifuY(int y) {
		waifuY = y;
	}

	public int getWaifuX() {
		return waifuX;
	}

	public int getWaifuY() {
		return waifuY;
	}

	public PositionMode getPositionMode() {
		return positionMode;
	}

	public void setPositionMode(PositionMode x) {
		positionMode = x;
	}

}
