package queuesys;

/**
 * Created by dex on 1/12/14.
 */
public class OldQueueCostFunction extends QueueCostFunction {
    public OldQueueCostFunction(int N, double l, double u, double c1, double c2) {
        super(N, l, u, c1, c2);
    }

    public QueueSysResult calculate(int servicePointNumber){
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
        double averageSystemCalls = p0;
        averageSystemCalls *= factorial(N);
        double temp = 0;
        for(int i = servicePointNumber+1; i <= N ; i++){
            temp += Math.pow(ro, i)*i/(factorial(N-i)*factorial(servicePointNumber)*Math.pow(servicePointNumber, i-servicePointNumber));
        }
        double averageQueueCalls = p0 * factorial(N) * temp;
        for(int i = 0; i <= servicePointNumber ; i++){
            temp += Math.pow(ro, i)*i/(factorial(N-i)*factorial(i));
        }
        averageSystemCalls *= temp;
        double averageSystemTime = averageSystemCalls/(lambda*(N-averageSystemCalls));
        //double averageOccupiedServicePoints = (N-averageSystemCalls)*ro;
        double averageQueueTime = averageQueueCalls/(lambda*(N-averageSystemCalls));
        double averageOccupiedServicePoints = averageSystemCalls-averageQueueCalls;

        double value = c1*servicePointNumber + c2*averageSystemCalls;

        QueueSysResult result = new QueueSysResult(value, averageSystemCalls, averageQueueCalls, averageSystemTime, averageQueueTime, averageOccupiedServicePoints);
        cachedResults.put(servicePointNumber, result);

        return result;
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
}
