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
    private int N;
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

    public int getN() {
        return N;
    }

    public void setN(int N) {
        this.N = N;
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
        this.N = N;
        this.lambda = l;
        this.mu = u;
        this.c1 = c1;
        this.c2 = c2;
    }
    
    public void calculate(){
        double p0 = 0;
        double ro = lambda/mu;
        for(int i = 0; i <= servicePointNumber ; i++){
            p0 += Math.pow(ro, i)/(factorial(N-i)*factorial(i));
        }
        for(int i = servicePointNumber+1; i <= N ; i++){
            p0 += Math.pow(ro, i)/(factorial(N-i)*factorial(servicePointNumber)*Math.pow(servicePointNumber, i-servicePointNumber));
        }
        p0 *= factorial(N);
        p0 = 1/p0;
        averageSystemCalls = p0;
        averageSystemCalls *= factorial(N);
        double temp = 0;
        for(int i = 0; i <= servicePointNumber ; i++){
            temp += Math.pow(ro, i)*i/(factorial(N-i)*factorial(i));
        }
        for(int i = servicePointNumber+1; i <= N ; i++){
            temp += Math.pow(ro, i)*i/(factorial(N-i)*factorial(servicePointNumber)*Math.pow(servicePointNumber, i-servicePointNumber));
        }
        averageSystemCalls *= temp;
        averageSystemTime = averageSystemCalls/(lambda*(N-averageSystemCalls));
        averageQueueTime = averageSystemTime - 1/mu;
        averageOccupiedServicePoints = (N-averageSystemCalls)*ro;
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
