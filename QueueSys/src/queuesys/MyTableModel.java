package queuesys;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 *
 * @author Andrzej i Marcin
 */

public class MyTableModel extends AbstractTableModel {
    private static class ResultGroup {
        public int minIteration;
        public int maxIteration;

        public ResultGroup(int minIteration, int maxIteration) {
            this.minIteration = minIteration;
            this.maxIteration = maxIteration;
        }

        @Override
        public String toString() {
            if (minIteration != maxIteration) {
                return String.format("%d - %d", minIteration, maxIteration);
            } else {
                return new Integer(minIteration).toString();
            }
        }
    }

    private Result result = new Result();
    private ArrayList<ResultGroup> resultGroups = new ArrayList<>();
    private CostFunction costFunction;
    private static final String[] COLUMN_NAMES = new String[] {
            "Iteration(s)",
            "Value",
            "Cost",
            "Avg sys time",
            "Avg sys calls",
            "Avg q time",
            "Avg q calls",
            "Time"
    };

    public MyTableModel(QueueCostFunction costFunction) {
        reset(costFunction);
    }

    public MyTableModel() {
        this(null);
    }

    public void reset(QueueCostFunction costFunction) {
        result = new Result();
        resultGroups = new ArrayList<>();
        this.costFunction = costFunction;
    }

    public void add(int value) {
        if (result.size() > 0 && result.getValue((int)result.size() - 1) == value) {
            resultGroups.get(resultGroups.size() - 1).maxIteration++;
        } else {
            resultGroups.add(new ResultGroup((int)result.size(), (int)result.size()));
        }

        result.add(value);
        fireTableRowsInserted(resultGroups.size() - 1, resultGroups.size() - 1);
    }

    @Override
    public int getRowCount() {
        return resultGroups.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ResultGroup group = resultGroups.get(rowIndex);

        switch (columnIndex) {
        case 0:
            return group.toString();
        case 1:
            return result.getValue(group.minIteration);
        case 2:
            return String.format("%.4f", costFunction.result(result.getValue(group.minIteration)).cost);
        case 3:
            return String.format("%.4f", costFunction.result(result.getValue(group.minIteration)).averageSystemTime);
        case 4:
            return String.format("%.4f", costFunction.result(result.getValue(group.minIteration)).averageSystemCalls);
        case 5:
            return String.format("%.4f", costFunction.result(result.getValue(group.minIteration)).averageQueueTime);
        case 6:
            return String.format("%.4f", costFunction.result(result.getValue(group.minIteration)).averageQueueCalls);
        case 7:
            return result.getTimeOffset(group.minIteration);
        default:
            throw new RuntimeException("invalid row index");
        }
    }

    public Result getResult() {
        return result;
    }
}
