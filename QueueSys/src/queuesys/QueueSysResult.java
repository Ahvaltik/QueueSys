package queuesys;

public class QueueSysResult {
    public final double cost;
    public final double averageSystemCalls;
    public final double averageQueueCalls;
    public final double averageSystemTime;
    public final double averageQueueTime;
    public final double averageOccupiedServicePoints;

    QueueSysResult(double cost,
                   double averageSystemCalls,
                   double averageQueueCalls,
                   double averageSystemTime,
                   double averageQueueTime,
                   double averageOccupiedServicePoints) {
        this.cost = cost;
        this.averageSystemCalls = averageSystemCalls;
        this.averageQueueCalls = averageQueueCalls;
        this.averageSystemTime = averageSystemTime;
        this.averageQueueTime = averageQueueTime;
        this.averageOccupiedServicePoints = averageOccupiedServicePoints;
    }
}
