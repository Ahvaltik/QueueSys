package queuesys.search.cuckoo;

import java.util.Random;

/**
 * Class for holding actual solution
 * 
 * @author Anna Rutka, Daniel Bryła
 * 
 */
public class Nest {

	protected Egg egg;

	public Nest(Egg egg) {
		this.egg = egg;
	}

	public int getEgg() {
		return egg.m;
	}

	public void layEgg(int egg) {
		this.egg.m = egg;
	}

	public Cuckoo getCuckoo(double stepSize, int N) {
		// TODO: performing Lévy flights; maybe someday...
		Random generator = new Random();
		int direction;
		if (generator.nextInt(2) == 0)
			direction = 1;
		else
			direction = -1;
		this.egg.m += direction * Math.round(stepSize * generator.nextFloat());
		if (egg.m <= 0)
			egg.m = 1;
		else if (egg.m > N)
			egg.m = N;
		return new Cuckoo(egg);
	}

}
