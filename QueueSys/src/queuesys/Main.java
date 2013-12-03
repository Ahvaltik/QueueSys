/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys;

/**
 *
 * @author Paweł
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Cockroaches cockroaches = new Cockroaches();
        double lambda = 19;
        double mu = 1;
        int N = 15;
        int m = 7;
        double c1 = 4;
        double c2 = 12;
        ICostFunction queueCostFunction = new QueueCostFunction(m, N, lambda, mu, c1, c2);
        for(int i = 1; i <= N; i ++){
            System.out.println("value " + i + " = " + queueCostFunction.cost(i));
        }
        cockroaches.setCostFunction(queueCostFunction);
        cockroaches.setDisperseStepSize(3.0);
        cockroaches.setSwarmStepSize(5.0);
        cockroaches.setN(15);

        int solution = cockroaches.solve(100, 100);
        System.out.printf("solution is: %d\n", solution);
    }
}
