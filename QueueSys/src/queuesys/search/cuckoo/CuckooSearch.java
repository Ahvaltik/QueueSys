package queuesys.search.cuckoo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import queuesys.ICostFunction;
import queuesys.Result;

/**
 * Class representing CuckooSearch algorithm
 * 
 * @author Anna Rutka, Daniel Bryła
 * 
 */
public abstract class CuckooSearch {
	private static ArrayList<Nest> nests = new ArrayList<Nest>();

	/**
	 * algorithm for finding minimum of function by cuckoo search
	 * 
	 * @param nestsNumber
	 *            - (> 0)
	 * @param iterations
	 *            - iterations limit (> 0)
	 * @param pa
	 *            - probability of abandoning nest [0,1]
	 * @param stepSize
	 *            - for Levy flights (> 0)
	 * @param N
	 *            - limit places in queue
	 * @param function
	 *            - function which minimum will be calculated
	 * @return best solution
	 * @throws Exception
	 *             for giving bad parameters
	 */
	public static Result optymalization(int nestsNumber, int iterations,
			double pa, double stepSize, int N, ICostFunction function)
			throws Exception {
		if (nestsNumber <= 0 || iterations <= 0 || pa < 0 || pa > 1
				|| stepSize <= 0 || N <= 0 || function == null)
			throw new Exception("Bad parameters.");

		EggsComparator eggsComparator = new EggsComparator(function);
		Random generator = new Random();
		// generate an initial population of host nests
		for (int i = 0; i < nestsNumber; ++i)
			nests.add(new Nest(new Egg(N)));

		Result result = new Result();
		
		for (int i = 0; i < iterations; ++i) {
			// get a cuckoo randomly replace its solution by random walk
			Cuckoo cuckoo = nests.get(generator.nextInt(nestsNumber - 1) + 1)
					.getCuckoo(stepSize, N);

			// choose a nest randomly
			Nest nest = nests.get(generator.nextInt(nestsNumber - 1) + 1);

			// if cuckoo's egg is better then replace

			if (eggsComparator.compare(nest, cuckoo) < 0)
				nest.layEgg(cuckoo.getEgg());

			// rank the solutions/nests
			Collections.sort(nests, eggsComparator);

			// a fraction (pa) of the worse nests are abandoned and new ones
			// are built
			abandonAndBuildNewNest(pa, N);

			// find the current best
			result.add(nests.get(0).getEgg());
		}
		return result;
	}

	private static void abandonAndBuildNewNest(double pa, int N) {
		if (Math.random() <= pa)
			nests.set(nests.size() - 1, new Nest(new Egg(N)));
	}

}
