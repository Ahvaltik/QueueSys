package queuesys.search.cuckoo;

import java.util.Comparator;

import queuesys.ICostFunction;

/**
 * Comparator for solution
 * 
 * @author Anna Rutka, Daniel Bryła
 * 
 */
public class EggsComparator implements Comparator<Nest> {

	private ICostFunction function;

	public EggsComparator(ICostFunction function) {
		this.function = function;
	}

	@Override
	public int compare(Nest arg0, Nest arg1) {
		return (new Double(function.cost(arg0.getEgg())).compareTo(new Double(
				function.cost(arg1.getEgg()))));

	}

}
