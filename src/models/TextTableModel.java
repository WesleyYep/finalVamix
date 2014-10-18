package models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class TextTableModel extends AbstractTableModel {
	private List<String[]> data = new ArrayList<String[]>();
	private List<Object[]> fullData = new ArrayList<Object[]>();

    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
    	if (col==0){
    		return "text";
    	}else if (col==1){
    		return "startTime";
    	}else {
    		return "endTime";
    	}
    }
    
    public Object[] getFullData(int i){
    	return fullData.get(i);
    }

    public Object getValueAt(int row, int col) {
        return data.get(row)[col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    public void add(String text, String start, String end){
    	data.add(new String[]{text, start + "", end + ""});
    }
    
    public void addFullData(String text, long start, long end, int x, int y, String fontPath, String colour, int size, int fontOption){
    	fullData.add(new Object[]{text, start, end, x, y, fontPath, colour, size, fontOption});
    }
}
