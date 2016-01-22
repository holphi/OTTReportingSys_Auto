package com.nagra.bsm.util;

import java.net.UnknownHostException;
import java.sql.Connection;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.nagra.bsm.tasks.RptScheduleMgrTasks;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class DBUtil {
	protected static final Logger logger = Logger.getLogger(DBUtil.class);
	private static Connection dbConnection = null;
	private static PreparedStatement preStatement = null;
	private static ResultSet rsSet = null;
	private static Mongo mg = null;
	private static DB db; // mongo db name for test
	private static DBCollection dbcollection;
	private static Connection oraConnection = null;
	

	public static Connection getConnection()
	{
		try
		{
			Class.forName("org.hsqldb.jdbcDriver");
			String url = String.format("jdbc:hsqldb:hsql://%s:9002/bsmdb.hsqldb", CommonUtil.getPropertyValue("host_name"));
			String user = CommonUtil.getPropertyValue("db_username");
			String password = CommonUtil.getPropertyValue("db_password");
			dbConnection = DriverManager.getConnection(url, user, password);
		//	logger.info("Connect to hsqldb");			
		}catch(ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}catch(Exception ex) 
		{
			logger.error("Error in getting DB connection:" + ex.getMessage());
			ex.printStackTrace();
		}
		return dbConnection;
	}

	private static PreparedStatement getPreparedStatement(String sql) {
		try {
			preStatement = getConnection().prepareStatement(sql);			
		} catch (Exception ex) {
			System.err.println("Error in getting pare" + ex.getMessage());
			ex.printStackTrace();
		}
		return preStatement;
	}
	
	private static ResultSet getStatement(String sql){
		ResultSet rs = null;
		try {
			rs = getConnection().createStatement().executeQuery(sql);
		} catch (SQLException ex) {
			logger.error("Fail to execute query");
			ex.printStackTrace();
		}
		return rs;
	}

	public static int executeSQL(String sql) {
		try {
			rsSet = null;
			return getPreparedStatement(sql).executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error in executing SQL" + e.getMessage());
			return 0;
		} finally {
			closeDBRes();
		}
	}

	public static ResultSet executeQuery(String sql) {
		try {
			rsSet = null;
			rsSet = getPreparedStatement(sql).executeQuery();
		} catch (SQLException e) {
			System.err.println("Error in query db" + e.getMessage());
		}
		return rsSet;
	}
	
	public static int getIdMaxValue(String table, String idColumn){
		int max = -1;
		String sql =String.format( "select %s as no from %s order by no desc", idColumn, table);		
		try {
			PreparedStatement ps = getPreparedStatement(sql);								
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				max = rs.getInt(1);
			}else{
				logger.error("failed to get ID max value");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return max;
	}

	public static void closeDBRes() {
		try {
			closeResultSet();
			closePreparedStatement();
			closeConn();
		} catch (SQLException sqlEx) {
			System.err.println(sqlEx.getMessage());
		}
	}

	private static void closeResultSet() throws SQLException {
		try {
			if (rsSet != null) {
				rsSet.close();
				rsSet = null;
			}
		} catch (SQLException sqlEx) {
			throw new SQLException("Exception " + sqlEx.getMessage());
		}
	}

	private static void closePreparedStatement() throws SQLException {
		try {
			if (preStatement != null) {
				preStatement.close();
				preStatement = null;
			}
		} catch (SQLException sqlEx) {
			throw new SQLException(
					"Exception happened in closing PreparedStatement. "
							+ sqlEx.getMessage());
		}
	}

	public static void closeConn() throws SQLException {
		try {
			if (dbConnection != null && (!dbConnection.isClosed())) {
				dbConnection.close();
			}
		} catch (SQLException sqlEx) {
			throw new SQLException("Execption happened in closing Connection. "
					+ sqlEx.getMessage());
		}
	}

	public static int getRowCountOfTable(String tableName) throws SQLException {
		StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM %S;");
		String sql = String.format(sqlBuilder.toString(), tableName);
		ResultSet rs = DBUtil.executeQuery(sql);
		int count = 0;
		try {
			if (rs.next())
				count = rs.getInt(1);
		} catch (SQLException ex) {
			throw ex;
		} finally {
			DBUtil.closeDBRes();
		}
		return count;
	}

	public static int getRecordCountOfSql(String sql) {
		int count = -1;
		try {
			ResultSet rs = executeQuery(sql);
			rs.next();
			count = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	finally {
			DBUtil.closeDBRes();
		}
		return count;
	}
	
	public static String getExtEventID(String eventText){
		String sql = String
				.format("Select EVENT_ID from BSM_SCHEDULER_EVENT WHERE EVENT_TYPE = 2 AND EVENT_TEXT='%s'",
						eventText);
		ResultSet rs = executeQuery(sql);
		try {
			if (rs.next()) {
				return rs.getString(1);
			} else
				return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	

	public static void getMongoConnection() {
		try {
			mg = new Mongo(CommonUtil.getPropertyValue("host_name"), 27017);
			db = mg.getDB("test");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Boolean isMongoCollectionExist(String collection) {
		getMongoConnection();
		boolean exists;
		exists = db.collectionExists(collection);
		closeMongoConnection();
		return exists;		
	}

	public static Set<String> getMongoCollections() {
		return db.getCollectionNames();
	}

	public static void removeMongoCollection(String... collections) {
		getMongoConnection();		
		for (String coll : collections) {
			if(db.collectionExists(coll)){
				db.getCollection(coll).drop();
			}			
		}
		closeMongoConnection();
	}

	public static void closeMongoConnection() {
		db.requestDone();
	}
	
	public static int getMongoCollectionRecordCount(String collection){
		int count = -1;
		getMongoConnection();
		dbcollection = db.getCollection(collection);
		count = (int)dbcollection.getCount();
		closeMongoConnection();
		return count;
	}
	
	public static int findInMongoCollection(String collection,String name, Object value){
		int count = -1;
		getMongoConnection();
		dbcollection = db.getCollection(collection);
		BasicDBObject query = new BasicDBObject();
		query.put(name, value);
		DBCursor cursor = dbcollection.find(query);
		count = cursor.count();
		closeMongoConnection();
		return count;
	}		
	
	public static Connection getOracleConnection(){		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String oraurl = "jdbc:oracle:thin:@172.22.2.93:1521:xe";
		String orauser= "SDP_NMP_DEMO_2_12_1_RC3_2_O";
		String orapwd = "SDP_NMP_DEMO_2_12_1_RC3_2_O";
		try {
			 oraConnection = DriverManager.getConnection(oraurl, orauser, orapwd);
			logger.debug("Connect to Oracle");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return oraConnection;
	}
	
	public static void closeOraConnection(){
		try {
			oraConnection.close();
		} catch (SQLException e) {
			logger.error("Fail to close connection");
			e.printStackTrace();
		}
	}
	
	private static PreparedStatement getOraPreparedStatement(String sql){
		try {
			PreparedStatement oraPreStatement = getOracleConnection().prepareStatement(sql);
			return oraPreStatement;
		} catch (SQLException e) {
			logger.error("fail to get Oracle preparestatement");
			e.printStackTrace();
			return null;
		}		
	}	
	
	private static int getRowCountFromResultSet(ResultSet rs){
		int count =-1;		
		try {
			if(rs.next()){
				count = rs.getInt(1);
				rs.close();
			}
		}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}		
		return count;
	}
	
	
	public static int getOraTableRowCount(String table){		
		int count = -1;
		String sql = String.format("Select count(*) from %s", table);
		PreparedStatement ps = getOraPreparedStatement(sql);
		try {					
			ResultSet rs = ps.executeQuery();
			count = getRowCountFromResultSet(rs);
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeOraConnection();					
		}
		return count;
	}
	
	public static ResultSet executeQureyInOra(String sql){
		PreparedStatement ps = getOraPreparedStatement(sql);
		ResultSet rs = null;
		try {
			 rs = ps.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	public static void executeSqlInOra(String sql){
		PreparedStatement ps = getOraPreparedStatement(sql);
		try {
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeOraConnection();
		}
	}
	
	public static void insertIntoOra(String sql, int value){
		PreparedStatement ps = getOraPreparedStatement(sql);
		try {
			ps.setInt(1, value);
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeOraConnection();
		}
	}
	
	public static void cleanOraTable(String table){
		String sql = String.format("delete from %s", table);
		PreparedStatement ps = getOraPreparedStatement(sql);
		try {						
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeOraConnection();
		}
	}
	

/*	public static void main(String[] args){		
	//	cleanOraTable("cor_device_test_des");
		removeMongoCollection("cor_device_test_des");
		System.out.println( isMongoCollectionExist("cor_device_test_des"));
	}
*/	
}