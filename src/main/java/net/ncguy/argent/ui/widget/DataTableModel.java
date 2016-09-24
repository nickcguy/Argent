package net.ncguy.argent.ui.widget;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 22/09/2016.
 */
public class DataTableModel {

    Object[] headers;
    List<Object[]> rows;

    public DataTableModel(int columnCount) {
        this(new Object[columnCount]);
    }

    public DataTableModel(Object... headers) {
        this.headers = headers;
        this.rows = new ArrayList<>();
    }

    public Object[] getHeaders() { return headers; }
    public List<Object[]> getRows() { return rows; }

    public int getColumnCount() { return headers.length; }
    public int getRowCount() { return rows.size(); }

    public Object[] getRow(int index) {
        return rows.get(MathUtils.clamp(index, 0, rows.size()-1));
    }

    public void addRow(Object... row) {
        rows.add(row);
    }
}
