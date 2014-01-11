/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys;

/**
 *
 * @author Andrzej, Marcin
 */
public abstract class CostFunction {
    public abstract QueueSysResult result(int m);

    public double cost(int m) { return result(m).cost; }
}
