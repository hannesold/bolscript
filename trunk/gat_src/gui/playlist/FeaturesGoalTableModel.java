package gui.playlist;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import algorithm.composers.kaida.Feature;

public class FeaturesGoalTableModel extends AbstractTableModel {
	
		
		public static String FEATURE_VALUE = 		"measured";	
		public static String DISTANCE_REAL = 		"distance to goal";
		public static String DISTANCE_NORMALISED = 	"distance normalised";
		public static String GOAL_VALUE =			"goal value";
		public static String GOAL_IMPORTANCE = 		"goal importance";
		
		public static String[] featureTypes = new String[]{GOAL_VALUE, DISTANCE_REAL, DISTANCE_NORMALISED,  GOAL_IMPORTANCE};
		
		public static String DEMARKER =				": ";
		
	
        private String[] columnNames = {"feature", "value", "goal", "dist", "norm",  "importance"};
        //private Object[][] data;

        private ArrayList<Feature> features;
        
        private HashMap<String, ArrayList<Double>> tableEntrys;
        private ArrayList<String> names;
        
        private ArrayList<Double> values;
        private ArrayList<Double> goalValues;
        private ArrayList<Double> goalImportances;
        private ArrayList<Double> distancesReal;
        private ArrayList<Double> distancesNormalised;

        
        public FeaturesGoalTableModel(ArrayList<Feature> features) {
        	this.features = new ArrayList<Feature>(features);
        	names = new ArrayList<String>();
        	values = new ArrayList<Double>();
        	tableEntrys = new HashMap<String, ArrayList<Double>>();
        	
        	int i = 0;
        	while (i < features.size()) {
        		Feature f =  features.get(i);
        		String label = f.getLabel();
        		String[] parts = label.split(DEMARKER);
        		
        		if (parts.length == 1) {
        			names.add(label);
        			values.add(f.value);
        			ArrayList<Double> entrys = new ArrayList<Double>();
        			
        			tableEntrys.put(label,entrys);
        			
        			for (int type=0; type < featureTypes.length; type++) {
        				boolean found = false;
        				for (int j=0; j < features.size(); j++) {
            				Feature f2 = features.get(j);
            				String[] f2parts = f2.getMessage().split(DEMARKER);
            				if (f2parts.length>1) {
            					 
            					String f2label = f2parts[1];
            					if ((f2parts[0].equals(featureTypes[type])) &&
            							(f2label.equals(label))) {
            						entrys.add(f2.value);
            						found=true;
            						//features.remove(f2);
            					}
            				}
            			}
        				if (!found) {
        					entrys.add(null);
        				}
        			}
        			
        			
        		} else {
        			//nothing
        		}
        		
        		i++;
        	}
        	//printDebugData();
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return tableEntrys.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
        	if (col==0) {
        		return names.get(row);
        	} else if (col==1){
        		return values.get(row);
        	} else {
        		Double val = tableEntrys.get(names.get(row)).get(col-2);
        		return ((val==null)?new Double(0f):val);
        	}// else return ""
        }
        

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         *
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 2) {
                return false;
            } else {
                return true;
            }
        }*/

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         *
        public void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of "
                                   + value.getClass() + ")");
            }

            data[row][col] = value;
            fireTableCellUpdated(row, col);

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }*/

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                   // System.out.print("  " + data[i][j]);
                	System.out.println("  " + getValueAt(i,j));
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
}
