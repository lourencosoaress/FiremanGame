package element;

import utils.Point2D;

public class FuelBarrel extends GameElement implements Burnable {

	private String name = "fuelbarrel";
	private int time = 3;
	private int explosionTime = 1;
	private boolean isBurnt = false;

	public FuelBarrel(Point2D position) {
		super(position);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean getIsBurnt() {
		return isBurnt;
	}

	@Override
	public int getLayer() {
		return 1;
	}

	public void burned() {
		if (explosionTime > 0) {
			explosionTime -= 1;
		} else {
			isBurnt = true;
			name = "burntgrass";
		}
	}

	public void explode() {
		name = "explosion";
	}

	@Override
	public int getTime() {
		return time;
	}

	@Override
	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public void burning() {
		setTime(time - 1);
		if (getTime() <= 0) {
			explode();
		}
	}
}
