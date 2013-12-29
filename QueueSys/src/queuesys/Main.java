/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys;

import java.util.logging.Level;
import java.util.logging.Logger;
import queuesys.search.cockroaches.Cockroaches;

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
        double mu = 10;
        int N = 15;
        int m = 1;
        double c1 = 4;
        double c2 = 12;
        ICostFunction queueCostFunction = new QueueCostFunction(m, N, lambda, mu, c1, c2);
        for(int i = 1; i <= N; i ++){
            System.out.println("value " + i + " = " + queueCostFunction.cost(i));
        }
        cockroaches.setCostFunction(queueCostFunction);
        cockroaches.setDisperseStepSize(2.0);
        cockroaches.setSwarmStepSize(3.0);
        cockroaches.setN(15);

        //int solution = cockroaches.solve(100, 1000);
        //System.out.printf("solution is: %d\n", solution);
        try {
            System.out.println("Cuckoo Search solutions:");
            queuesys.search.cuckoo.CuckooSearch.optymalization(20, 1000, 0.25, 0.1, 1000, queueCostFunction);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
