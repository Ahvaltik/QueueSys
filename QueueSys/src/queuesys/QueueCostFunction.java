/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
            p0 += (Math.pow(ro, i))/(factorial(N-i)*factorial(i));
        }
        for(int i = servicePointNumber+1; i <= N ; i++){
            p0 += (Math.pow(ro, i))/(factorial(N-i)*factorial(servicePointNumber)*Math.pow(servicePointNumber, i-servicePointNumber));
        }
        p0 *= factorial(N);
        p0 = 1/p0;
        //System.out.println("P0 for m = " + servicePointNumber + " is " + p0);
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

    public void calculate_new() {
        double p0 = 0.0;
        double rho = lambda / mu;
        int m = servicePointNumber;

        double[] rho_pow = new double[N + 1];
        rho_pow[0] = 1.0;
        for (int i = 1; i <= N; ++i) {
            rho_pow[i] = rho_pow[i] * rho;
        }

        double[] m_pow = new double[N + 1];
        m_pow[0] = 1.0;
        for (int i = 1; i <= N; ++i) {
            m_pow[i] = m_pow[i] * m;
        }

        for (int i = 0; i <= m; ++i) {
            p0 += rho_pow[i] * factorialsQuotient(new int[] { N }, new int[] { i, N - i });
        }
        for (int i = m + 1; i <= N; ++i) {
            p0 += rho_pow[i] * factorialsQuotient(new int[] { N }, new int[] { N - i, m }) * m_pow[i - m];
        }
        p0 = 1.0 / p0;
    }
    
    private double factorial(int n){
        if(n == 0) {
            return 1;
        }
        double j = 1;
        for(int i = 1; i <= n;i++){
            j *= i;
        }
        return j;
    }

    private static ArrayList<Integer> primes = new ArrayList<>();

    // 0-indexed
    private int nthPrime(int n) {
        while (primes.size() <= n) {
            int candidate;

            if (primes.size() == 0) {
                candidate = 2;
            } else {
                candidate = primes.get(primes.size() - 1);
            }

            boolean isPrime;

            do {
                candidate = candidate + 1;
                isPrime = true;

                int i = 0;

                while (isPrime && i < primes.size()) {
                    int prime = primes.get(i);

                    if (prime > (int)Math.sqrt(candidate)) {
                        break;
                    }

                    if (candidate % prime == 0) {
                        isPrime = false;
                    }

                    ++i;
                }
            } while (!isPrime);
        }

        return primes.get(n);
    }

    private ArrayList<Integer> factor(long n) {
        ArrayList<Integer> ret = new ArrayList<>();
        int i = 0;

        while (n > 1) {
            int prime = nthPrime(i);

            while (n % i == 0) {
                ret.add(prime);
                n /= prime;
            }
        }

        return ret;
    }

    private ArrayList<Integer> mergeSortedLists(ArrayList<Integer> first, ArrayList<Integer> second) {
        ArrayList<Integer> ret = new ArrayList<>();

        int fstIdx = 0;
        int sndIdx = 0;

        while (fstIdx < first.size() && sndIdx < second.size()) {
            while (first.get(fstIdx) < second.get(sndIdx)) {
                ret.add(first.get(fstIdx++));
            }
            while (first.get(fstIdx) > second.get(sndIdx)) {
                ret.add(first.get(sndIdx++));
            }
        }

        while (fstIdx < first.size()) {
            ret.add(first.get(fstIdx));
        }
        while (sndIdx < second.size()) {
            ret.add(second.get(sndIdx));
        }

        return ret;
    }

    // (x1! * x2! * ...) / (y1! * y2! * ...)
    private double factorialsQuotient(int[] dividends, int[] divisors) {
        ArrayList<Integer> dividendFactors = new ArrayList<>();
        ArrayList<Integer> divisorFactors = new ArrayList<>();

        for (int n: dividends) {
            dividendFactors = mergeSortedLists(dividendFactors, factor(n));
        }
        for (int n: divisors) {
            divisorFactors = mergeSortedLists(divisorFactors, factor(n));
        }

        double dividend = 1.0;
        double divisor = 1.0;
        int dividendIdx = 0;
        int divisorIdx = 0;

        while (dividendIdx < dividendFactors.size() && divisorIdx < divisorFactors.size()) {
            if (dividendFactors.get(dividendIdx) < divisorFactors.get(divisorIdx)) {
                dividend *= dividendFactors.get(dividendIdx);
                ++dividendIdx;
            } else if (dividendFactors.get(dividendIdx) > divisorFactors.get(divisorIdx)) {
                divisor *= divisorFactors.get(divisorIdx);
                ++divisorIdx;
            } else {
                ++dividendIdx;
                ++divisorIdx;
            }
        }

        return dividend / divisor;
    }

    @Override
    public double cost(int m){
        this.setServicePointNumber(m);
        calculate();
        return this.getResult();
    }
    
}
