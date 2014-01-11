package queuesys;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Andrzej i Marcin
 */

public class MyTableModel extends AbstractTableModel {
    private Result result = new Result();
    private ICostFunction costFunction;

    public MyTableModel(ICostFunction costFunction) {
        reset(costFunction);
    }

    public MyTableModel() {
        this(null);
    }

    public void reset(ICostFunction costFunction) {
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
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        return new String[] {
                "Iteration",
                "Value",
                "Cost",
                "Time"
        }[column];
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case 0:
            return rowIndex + 1;
        case 1:
            return result.getValue(rowIndex);
        case 2:
            return costFunction.cost(result.getValue(rowIndex));
        case 3:
            return result.getTimeOffset(rowIndex);
        default:
            throw new RuntimeException("invalid row index");
        }
    }

    public Result getResult() {
        return result;
    }
}
