/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys.search.cockroaches;

import java.util.ArrayList;
import java.util.Random;
import queuesys.ICostFunction;
import queuesys.MyTableModel;
import queuesys.Result;
import queuesys.TestCostFunction;

/**
 *
 * @author Andrzej, Marcin
 */
public class Cockroaches {
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

    class Cockroach {
        public int m;

        public Cockroach(int m) {
            this.m = m;
        }

        public void moveTowards(Cockroach target) {
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

        public void eat(Cockroach food) {
            food.m = m;
        }
    }

    class Solution {
        public Cockroach cockroach;
        public double cost;

        public Solution(Cockroach cockroach) {
            this.cost = costFunction.cost(cockroach.m);
            this.cockroach = cockroach;
        }

        public boolean isBetterThan(Solution another) {
            return cost < another.cost;
        }
    }

    private Solution findOptimum(ArrayList<Cockroach> cockroaches) {
        Solution optimum = new Solution(cockroaches.get(0));

        for (int i = 1; i < cockroaches.size(); ++i) {
            Solution next = new Solution(cockroaches.get(1));
            if (next.isBetterThan(optimum)) {
                optimum = next;
            }
        }

        return optimum;
    }

    public int solve(MyTableModel model, int cockroachesCount, int iterations) {
        ArrayList<Cockroach> cockroaches = new ArrayList<Cockroach>();

        for (int i = 0; i < cockroachesCount; ++i) {
            cockroaches.add(new Cockroach(random.nextInt(N) + 1));
            System.out.printf("cockroach at: %d\n", cockroaches.get(i).m);
        }

        Solution globalOptimum = findOptimum(cockroaches);

        for (int i = 0; i < iterations; ++i) {
            // swarm
            for (int first = 0; first < cockroaches.size(); ++first) {
                Solution firstSolution = new Solution(cockroaches.get(first));

                for (int second = first + 1; second < cockroaches.size(); ++second) {
                    Solution secondSolution = new Solution(cockroaches.get(second));

                    if (firstSolution.isBetterThan(secondSolution)) {
                        cockroaches.get(second).moveTowards(cockroaches.get(first));

                        secondSolution = new Solution(cockroaches.get(second));
                        if (secondSolution.isBetterThan(globalOptimum)) {
                            globalOptimum = secondSolution;
                        }
                    } else if (secondSolution.isBetterThan(firstSolution)) {
                        cockroaches.get(first).moveTowards(cockroaches.get(second));

                        firstSolution = new Solution(cockroaches.get(first));
                        if (firstSolution.isBetterThan(globalOptimum)) {
                            globalOptimum = firstSolution;
                        }
                    }
                }
            }

            // disperse
            for (int c = 0; c < cockroaches.size(); ++c) {
                if (cockroaches.get(c) != globalOptimum.cockroach)
                    cockroaches.get(c).moveRandomly();
            }
            globalOptimum = findOptimum(cockroaches);

            // ruthless behavior
            globalOptimum.cockroach.eat(cockroaches.get(random.nextInt(cockroaches.size())));

            model.add(globalOptimum.cockroach.m);
            System.out.printf("iteration %d: optimum = %d (%f)\n", i, globalOptimum.cockroach.m, globalOptimum.cost);
        }

        return globalOptimum.cockroach.m;
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
    }*/
}
