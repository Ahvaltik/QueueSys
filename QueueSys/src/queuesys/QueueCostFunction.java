/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys;

/**
 *
 * @author Paweł
 */
public class QueueCostFunction implements ICostFunction {

    private int servicePointNumber;
    private int systemSize;
    private double lambda;
    private double mu;
    private double averageSystemCalls;
    private double averageQueueCalls;
    private double averageSystemTime;
    private double averageQueueTime;
    private double averageOccupiedServicePoints;

    public double getAverageSystemCalls() {
        return averageSystemCalls;
    }

    public double getAverageQueueCalls() {
        return averageQueueCalls;
    }

    public double getAverageSystemTime() {
        return averageSystemTime;
    }

    public double getAverageQueueTime() {
        return averageQueueTime;
    }

    public double getAverageOccupiedServicePoints() {
        return averageOccupiedServicePoints;
    }

    public double getResult() {
        return result;
    }

    public int getServicePointNumber() {
        return servicePointNumber;
    }

    public void setServicePointNumber(int servicePointNumber) {
        this.servicePointNumber = servicePointNumber;
    }

    public int getSystemSize() {
        return systemSize;
    }

    public void setSystemSize(int systemSize) {
        this.systemSize = systemSize;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public double getMu() {
        return mu;
    }

    public void setMu(double mu) {
        this.mu = mu;
    }

    public double getC1() {
        return c1;
    }

    public void setC1(double c1) {
        this.c1 = c1;
    }

    public double getC2() {
        return c2;
    }

    public void setC2(double c2) {
        this.c2 = c2;
    }
    private double c1;
    private double c2;
    private double result;

    public QueueCostFunction(int m, int N, double l, double u, double c1, double c2) {
        this.servicePointNumber = m;
        this.systemSize = N;
        this.lambda = l;
        this.mu = u;
        this.c1 = c1;
        this.c2 = c2;
    }
    
    public void calculate(){
        double p0 = 0;
        double ro = lambda/mu;
        for(int i = 0; i <= servicePointNumber ; i++){
            p0 += Math.pow(ro, i)/(factorial(systemSize-i)*factorial(i));
        }
        for(int i = servicePointNumber+1; i <= systemSize ; i++){
            p0 += Math.pow(ro, i)/(factorial(systemSize-i)*factorial(servicePointNumber)*Math.pow(servicePointNumber, i-servicePointNumber));
        }
        p0 *= factorial(systemSize);
        p0 = 1/p0;
        averageSystemCalls = p0;
        averageSystemCalls *= factorial(systemSize);
        double temp = 0;
        for(int i = 0; i <= servicePointNumber ; i++){
            temp += Math.pow(ro, i)*i/(factorial(systemSize-i)*factorial(i));
        }
        for(int i = servicePointNumber+1; i <= systemSize ; i++){
            temp += Math.pow(ro, i)*i/(factorial(systemSize-i)*factorial(servicePointNumber)*Math.pow(servicePointNumber, i-servicePointNumber));
        }
        averageSystemCalls *= temp;
        averageSystemTime = averageSystemCalls/(lambda*(systemSize-averageSystemCalls));
        averageQueueTime = averageSystemTime - 1/mu;
        averageOccupiedServicePoints = (systemSize-averageSystemCalls)*ro;
        result = c1*servicePointNumber + c2*averageSystemCalls;
    }
    
    private int factorial(int n){
        if(n == 0) {
            return 1;
        }
        int j = 1;
        for(int i = 1; i <= n;i++){
            j *= i;
        }
        return j;
    }
    
    @Override
    public double cost(int m){
        this.setServicePointNumber(m);
        calculate();
        return this.getResult();
    }
    
}
