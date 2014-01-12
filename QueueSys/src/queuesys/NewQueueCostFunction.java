package queuesys;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author Marcin
 */
public class NewQueueCostFunction extends QueueCostFunction {
    public NewQueueCostFunction(int m, int N, double l, double u, double c1, double c2) {
        super(m, N, l, u, c1, c2);
    }

    public QueueSysResult calculate(int m) {
        double p0 = 0.0;
        double rho = lambda / mu;

        double[] rho_pow = new double[N + 1];
        rho_pow[0] = 1.0;
        for (int i = 1; i < rho_pow.length; ++i) {
            rho_pow[i] = rho_pow[i - 1] * rho;
        }

        double[] m_pow = new double[N + 1 - m];
        m_pow[0] = 1.0;
        for (int i = 1; i < m_pow.length; ++i) {
            m_pow[i] = m_pow[i - 1] * m;
        }

        double[] sum_components = new double[N + 1];

        for (int i = 0; i <= m; ++i) {
            sum_components[i] = rho_pow[i] * factorialsQuotient(new int[] { N }, new int[] { i, N - i });
            p0 += sum_components[i];
        }
        for (int i = m + 1; i <= N; ++i) {
            sum_components[i] = rho_pow[i] / m_pow[i - m] * factorialsQuotient(new int[] { N }, new int[] { N - i, m });
            p0 += sum_components[i];
        }

        p0 = 1.0 / p0;

        double averageSystemCalls = 0.0;
        for (int i = 0; i < N; ++i) {
            averageSystemCalls += sum_components[i] * i;
        }
        averageSystemCalls *= p0;

        double averageSystemTime = averageSystemCalls / (lambda * (N - averageSystemCalls));
        double averageQueueTime = averageSystemTime - 1 / mu;
        double averageQueueCalls = 0.0;
        double averageOccupiedServicePoints = (N - averageSystemCalls) * rho;

        double value = c1 * m + c2 * averageSystemCalls;

        QueueSysResult result = new QueueSysResult(value, averageSystemCalls, averageQueueCalls, averageSystemTime, averageQueueTime, averageOccupiedServicePoints);
        cachedResults.put(m, result);

        return result;
    }

    private static ArrayList<Integer> primes = new ArrayList<>();

    // 0-indexed
    private int nthPrime(int n) {
        while (primes.size() <= n) {
            int candidate;

            if (primes.size() == 0) {
                candidate = 1;
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

            primes.add(candidate);
        }

        return primes.get(n);
    }

    private ArrayList<Integer> factor(long n) {
        ArrayList<Integer> ret = new ArrayList<>();
        int i = 0;

        while (n > 1) {
            int prime = nthPrime(i);

            while (n % prime == 0) {
                ret.add(prime);
                n /= prime;
            }

            ++i;
        }

        return ret;
    }

    private ArrayList<Integer> mergeSortedLists(ArrayList<Integer> first, ArrayList<Integer> second) {
        ArrayList<Integer> ret = new ArrayList<>();

        int fstIdx = 0;
        int sndIdx = 0;

        while (fstIdx < first.size() && sndIdx < second.size()) {
            if (first.get(fstIdx) < second.get(sndIdx)) {
                do {
                    ret.add(first.get(fstIdx));

                    ++fstIdx;
                    if (fstIdx >= first.size()) {
                        break;
                    }
                } while (first.get(fstIdx) < second.get(sndIdx));
            } else {
                do {
                    ret.add(second.get(sndIdx));

                    ++sndIdx;
                    if (sndIdx >= second.size()) {
                        break;
                    }
                } while (first.get(fstIdx) > second.get(sndIdx));
            }
        }

        while (fstIdx < first.size()) {
            ret.add(first.get(fstIdx));
            ++fstIdx;
        }
        while (sndIdx < second.size()) {
            ret.add(second.get(sndIdx));
            ++sndIdx;
        }

        return ret;
    }

    private static TreeMap<Integer, ArrayList<Integer>> factorialFactors = new TreeMap<Integer, ArrayList<Integer>>();

    private ArrayList<Integer> mergeWithFactorialFactors(ArrayList<Integer> list, int n) {
        ArrayList<Integer> ret = list;

        if (factorialFactors.containsKey(n)) {
            ret = mergeSortedLists(ret, factorialFactors.get(n));
        } else {
            for (int i = 2; i <= n; ++i) {
                ret = mergeSortedLists(ret, factor(i));
            }

            factorialFactors.put(n, ret);
        }

        return ret;
    }

    // (x1! * x2! * ...) / (y1! * y2! * ...)
    private double factorialsQuotient(final int[] dividends, final int[] divisors) {
        ArrayList<Integer> dividendFactors = new ArrayList<>();
        ArrayList<Integer> divisorFactors = new ArrayList<>();

        for (int n: dividends) {
           dividendFactors = mergeWithFactorialFactors(dividendFactors, n);
        }
        for (int n: divisors) {
            divisorFactors = mergeWithFactorialFactors(divisorFactors, n);
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

        while (dividendIdx < dividendFactors.size()) {
            dividend *= dividendFactors.get(dividendIdx++);
        }
        while (divisorIdx < divisorFactors.size()) {
            divisor *= divisorFactors.get(divisorIdx++);
        }

        return dividend / divisor;
    }

}
