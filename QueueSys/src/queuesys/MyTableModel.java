package queuesys;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Andrzej i Marcin
 */

public class MyTableModel extends AbstractTableModel {
    private Result result = new Result();
    private CostFunction costFunction;
    private static final String[] COLUMN_NAMES = new String[] {
            "Iteration",
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
        this.costFunction = costFunction;
    }

    public void add(int value) {
        result.add(value);
        fireTableRowsInserted((int)result.size() - 1, (int)result.size() - 1);
    }

    @Override
    public int getRowCount() {
        return (int)result.size();
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
        switch (columnIndex) {
        case 0:
            return rowIndex + 1;
        case 1:
            return result.getValue(rowIndex);
        case 2:
            return String.format("%.2f", costFunction.result(result.getValue(rowIndex)).cost);
        case 3:
            return String.format("%.2f", costFunction.result(result.getValue(rowIndex)).averageSystemTime);
        case 4:
            return String.format("%.2f", costFunction.result(result.getValue(rowIndex)).averageSystemCalls);
        case 5:
            return String.format("%.2f", costFunction.result(result.getValue(rowIndex)).averageQueueTime);
        case 6:
            return String.format("%.2f", costFunction.result(result.getValue(rowIndex)).averageQueueCalls);
        case 7:
            return result.getTimeOffset(rowIndex);
        default:
            throw new RuntimeException("invalid row index");
        }
    }

    public Result getResult() {
        return result;
    }
}
