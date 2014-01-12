/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys;

import java.util.*;

/**
 *
 * @author Paweł
 */
public abstract class QueueCostFunction extends CostFunction {
    protected final int servicePointNumber;
    protected final int N;
    protected final double lambda;
    protected final double mu;
    protected final double c1;
    protected final double c2;
    protected TreeMap<Integer, QueueSysResult> cachedResults = new TreeMap<>();

    public int getServicePointNumber() {
        return servicePointNumber;
    }

    public int getN() {
        return N;
    }

    public double getLambda() {
        return lambda;
    }

    public double getMu() {
        return mu;
    }

    public double getC1() {
        return c1;
    }

    public double getC2() {
        return c2;
    }

    public QueueCostFunction(int m, int N, double l, double u, double c1, double c2) {
        this.servicePointNumber = m;
        this.N = N;
        this.lambda = l;
        this.mu = u;
        this.c1 = c1;
        this.c2 = c2;
    }

    public abstract QueueSysResult calculate(int m);

    @Override
    public QueueSysResult result(int m){
        QueueSysResult result = cachedResults.get(m);
        if (result == null) {
            result = calculate(m);
        }
        return result;
    }
}
