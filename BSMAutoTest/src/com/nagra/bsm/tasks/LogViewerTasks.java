/**
 * 
 */
package com.nagra.bsm.tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.nagra.bsm.util.DBUtil;

/**
 * @author tetang
 *
 */
public class LogViewerTasks {
	public ArrayList<String[]> getLogRecordInDB(){
		String sql = "select OPERATION_ACTIVITY, ACTIVITY_DESCRIPTION,OPERATOR,STATUS from ACTIVITY_LOG";
		ArrayList<String[]> log = new ArrayList<String[]>();
		String[] record = new String[4];			
		try {
			ResultSet rs = DBUtil.executeQuery(sql);
			while(rs.next()){
				record[0] = rs.getString(1);				
				record[1] = rs.getString(2);
				record[2] = rs.getString(3);
				record[3] = String.valueOf(rs.getInt(4));
				log.add(record);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return log;
	}
	
	public boolean verifyLog(String[] expected, String[] actual){
		boolean equal = false;
		for(int i = 0 ; i < expected.length;i++){
			if ((!expected[i].isEmpty())||expected[i]!=null){
				if(expected[i].equalsIgnoreCase(actual[i])){
					equal = true;
				}else{
					equal = false;
					System.out.println(actual[i]);
				}
			}
		}
		return equal;
	}
}
