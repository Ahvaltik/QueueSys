package queuesys.search.cuckoo;

import java.util.Random;

/**
 * Class represents solution, each egg is representing one solution
 * 
 * @author Anna Rutka, Daniel Bryła
 * 
 */
public class Egg {

	int m;
	public Egg(int n) {
		Random generator = new Random();
		m = generator.nextInt(n) + 1;
	}

}
