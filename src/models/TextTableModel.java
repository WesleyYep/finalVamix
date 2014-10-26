package models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class TextTableModel extends AbstractTableModel {
	private List<String[]> data = new ArrayList<String[]>();
	private List<Object[]> fullData = new ArrayList<Object[]>();

	/**
	 * The table will always have 3 columns
	 */
    public int getColumnCount() {
        return 3;
    }

    /**
     * This method is used to reset all the data in the table
     */
    public void initialiseData(){
    	data = new ArrayList<String[]>();
    	fullData = new ArrayList<Object[]>();
    }
    
    /**
     * This gets the number of text entries in the table
     */
    public int getRowCount() {
        return data.size();
    }

    /**
     * Get the name of the table columns
     */
    public String getColumnName(int col) {
    	if (col==0){
    		return "text";
    	}else if (col==1){
    		return "startTime";
    	}else {
    		return "endTime";
    	}
    }
    
    /**
     * Get full data for each row
     * @param i the row number
     * @return an object array will all the data for that row
     */
    public Object[] getFullData(int i){
    	return fullData.get(i);
    }

    //compulsory methods
    public Object getValueAt(int row, int col) {
        return data.get(row)[col];
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    /**
     * Add text
     * @param the text to add
     * @param start the start time in hh:mm:ss
     * @param end the end time in hh:mm:ss
     */
    public void add(String text, String start, String end){
    	data.add(new String[]{text, start + "", end + ""});
    }
    
    /**
     * Edit existing text
     * @param i the row to edit
     */
    public void edit(int i, String text, String start, String end){
    	data.set(i, new String[]{text, start + "", end + ""});
    }
    
    /**
     * Add full data to the model, for a particular text entry
     */
    public void addFullData(String text, long start, long end, int x, int y, String fontPath, String colour, int size, int fontOption){
    	fullData.add(new Object[]{text, start, end, x, y, fontPath, colour, size, fontOption});
    }
    
    /**
     * Edit the full data at a particular text entry
     */
    public void editFullData(int i, String text, long start, long end, int x, int y, String fontPath, String colour, int size, int fontOption){
    	fullData.set(i, new Object[]{text, start, end, x, y, fontPath, colour, size, fontOption});
    }
    
    /**
     * Remove the data and full data from a certain row
     * @param i the row number
     */
    public void remove(int i){
    	data.remove(i);
    	fullData.remove(i);
    }
}
