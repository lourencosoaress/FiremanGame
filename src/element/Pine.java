package element;

import utils.Point2D;

public class Pine extends GameElement implements Burnable {

	private String name = "pine";
	private int time = 10;
	private boolean isBurnt = false;

	public Pine(Point2D p) {
		super(p);
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
			name = "burntpine";
	}

	@Override
	public boolean getIsBurnt() {
		return isBurnt;
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
		burned();
	}
}
