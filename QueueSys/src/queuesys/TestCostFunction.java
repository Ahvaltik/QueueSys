/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys;

/**
 *
 * @author Andrzej, Marcin
 */
public class TestCostFunction implements ICostFunction {

    @Override
    public double cost(int m) {
        return 100.0 * m;
    }
    
}
