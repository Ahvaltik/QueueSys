package queuesys.search.cuckoo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import queuesys.ICostFunction;

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
	public static int optymalization(int nestsNumber, int iterations, double pa,
			double stepSize, int N, ICostFunction function) throws Exception {
		if (nestsNumber <= 0 || iterations <= 0 || pa < 0 || pa > 1
				|| stepSize <= 0 || N <= 0 || function == null)
			throw new Exception("Bad parameters.");

		EggsComparator eggsComparator = new EggsComparator(function);
		Random generator = new Random();
		int currentBest = 0;
		// Generate an initial population of n host nests;
		for (int i = 0; i < nestsNumber; ++i)
			nests.add(new Nest(new Egg(N)));

		for (int i = 0; i < iterations; ++i) {
			// Get a cuckoo randomly (say, i) and replace its solution by random
			// walk
			Cuckoo cuckoo = nests.get(generator.nextInt(nestsNumber - 1) + 1)
					.getCuckoo(stepSize, N);

			// Choose a nest among n (say, j) randomly;
			Nest nest = nests.get(generator.nextInt(nestsNumber - 1) + 1);

			// if (F_i>F_j) then Replace j by the new solution;

			if (eggsComparator.compare(nest, cuckoo) < 0)
				nest.layEgg(cuckoo.getEgg());

			// Rank the solutions/nests
			Collections.sort(nests, eggsComparator);

			// A fraction (p_a) of the worse nests are abandoned and new ones
			// are built;
			abandonAndBuildNewNest(pa, N);

			// find the current best
			currentBest = nests.get(0).getEgg();
			System.out.printf("iteration %d: optimum = %d (%f)\n", i+1,
					currentBest, function.cost(currentBest));
		}

		return currentBest;
	}

	private static void abandonAndBuildNewNest(double pa, int N) {
		if (Math.random() <= pa)
			nests.set(nests.size() - 1, new Nest(new Egg(N)));
	}

}
