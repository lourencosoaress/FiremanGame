package element;

import utils.Point2D;

public class Abies extends GameElement implements Burnable {

	private String name = "abies";
	private int time = 20;
	private boolean isBurnt = false;

	public Abies(Point2D position) {
		super(position);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getLayer() {
		return 0;
	}

	@Override
	public void burned() {
		if (isBurnt == true)
			name = "burntabies";
	}

	@Override
	public int getTime() {
		return time;
	}
	
	@Override
	public void setTime(int time){
		this.time = time;
	}
	
	@Override
	public void burning() {
		setTime(time - 1);
		if (getTime() == 0)
			isBurnt = true;
	}

	@Override
	public boolean getIsBurnt() {
		return isBurnt;
	}
}
