/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys.search.hybrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import queuesys.ICostFunction;
import queuesys.MyTableModel;
import queuesys.Result;
import queuesys.TestCostFunction;
import queuesys.search.cuckoo.Egg;
import queuesys.search.cuckoo.EggsComparator;
import queuesys.search.cuckoo.Nest;

/**
 * Hybrid (Beta):
 * 
 * Swarm
 * Disperse
 * Abandon/Create new
 * Ruthless behavior
 *
 *
 * @author Krystian
 */

public class Hybrid {
    private Random random = new Random();

    private ICostFunction costFunction;
    public void setCostFunction(ICostFunction costFunction) {
        this.costFunction = costFunction;
    }

    private double swarmStepSize;
    public void setSwarmStepSize(double swarmStepSize) {
        this.swarmStepSize = swarmStepSize;
    }

    private double disperseStepSize;
    public void setDisperseStepSize(double disperseStepSize) {
        this.disperseStepSize = disperseStepSize;
    }

    private int N;
    public void setN(int n) {
        N = n;
    }
    
    private double pa;
    public void setPa(double pa) {
        this.pa = pa;
    }
    
    private double fatality;
    public void setFatality(double fatality) {
        this.fatality = fatality;
    }

    class Mutantroach {
        public int m;

        public Mutantroach(int m) {
            this.m = m;
        }

        public void moveTowards(Mutantroach target) {
            m += Math.round(Math.signum(target.m - m) * random.nextFloat() * swarmStepSize);
            if (m < 1) {
                m = 1;
            } else if (m > N) {
                m = N;
            }
        }

        public void moveRandomly() {
            m += Math.round((random.nextFloat() * 2.0 - 1.0) * disperseStepSize);
            if (m < 1) {
                m = 1;
            } else if (m > N) {
                m = N;
            }
        }

        public void eat(Mutantroach food) {
            food.m = m;
        }
    }

    class Solution {
        public Mutantroach mutantroach;
        public double cost;

        public Solution(Mutantroach mutantroach) {
            this.cost = costFunction.cost(mutantroach.m);
            this.mutantroach = mutantroach;
        }

        public boolean isBetterThan(Solution another) {
            return cost < another.cost;
        }
    }

    private Solution findOptimum(ArrayList<Mutantroach> mutantroaches) {
        Solution optimum = new Solution(mutantroaches.get(0));

        for (int i = 1; i < mutantroaches.size(); ++i) {
            Solution next = new Solution(mutantroaches.get(1));
            if (next.isBetterThan(optimum)) {
                optimum = next;
            }
        }

        return optimum;
    }
    
    // Mutantroach compare mechanism
    class MutantroachComparator implements Comparator<Mutantroach> {

        private ICostFunction function;

        public MutantroachComparator(ICostFunction function) {
                this.function = function;
        }

        @Override
        public int compare(Mutantroach arg0, Mutantroach arg1) {
                return (new Double(function.cost(arg0.m)).compareTo(new Double(
                                function.cost(arg1.m))));

        }

    }

    public int solve(MyTableModel model, int mutantroachesCount, int iterations) {
        ArrayList<Mutantroach> mutantroaches = new ArrayList<Mutantroach>();
        MutantroachComparator mutantroachComparator = new MutantroachComparator(costFunction);
        
        for (int i = 0; i < mutantroachesCount; ++i) {
            mutantroaches.add(new Mutantroach(random.nextInt(N) + 1));
        }

        Solution globalOptimum = findOptimum(mutantroaches);

        for (int i = 0; i < iterations; ++i) {
            // swarm
            for (int first = 0; first < mutantroaches.size(); ++first) {
                Solution firstSolution = new Solution(mutantroaches.get(first));

                for (int second = first + 1; second < mutantroaches.size(); ++second) {
                    Solution secondSolution = new Solution(mutantroaches.get(second));

                    if (firstSolution.isBetterThan(secondSolution)) {
                        mutantroaches.get(second).moveTowards(mutantroaches.get(first));

                        secondSolution = new Solution(mutantroaches.get(second));
                        if (secondSolution.isBetterThan(globalOptimum)) {
                            globalOptimum = secondSolution;
                        }
                    } else if (secondSolution.isBetterThan(firstSolution)) {
                        mutantroaches.get(first).moveTowards(mutantroaches.get(second));

                        firstSolution = new Solution(mutantroaches.get(first));
                        if (firstSolution.isBetterThan(globalOptimum)) {
                            globalOptimum = firstSolution;
                        }
                    }
                }
            }

            // disperse
            for (int c = 0; c < mutantroaches.size(); ++c) {
                if (mutantroaches.get(c) != globalOptimum.mutantroach)
                    mutantroaches.get(c).moveRandomly();
            }
            globalOptimum = findOptimum(mutantroaches);
            
            // abandon/create
            Collections.sort(mutantroaches, mutantroachComparator);
            int deaths = (int) (mutantroaches.size() * fatality);
            for (int it=1; it<=deaths; it++) {
            	if (Math.random() <= pa)
            		mutantroaches.set(mutantroaches.size() - it, new Mutantroach(random.nextInt(N) + 1));
            }

            // ruthless behavior
            globalOptimum.mutantroach.eat(mutantroaches.get(random.nextInt(mutantroaches.size())));

            model.add(globalOptimum.mutantroach.m);
        }

        return globalOptimum.mutantroach.m;
    }
    /*
    public static void main(String[] args) {
        Cockroaches cockroaches = new Cockroaches();

        cockroaches.setCostFunction(new TestCostFunction());
        cockroaches.setDisperseStepSize(3.0);
        cockroaches.setSwarmStepSize(5.0);
        cockroaches.setN(1000);

        int solution = cockroaches.solve(10, 50);
        System.out.printf("solution is: %d\n", solution);
    }
    */
}
