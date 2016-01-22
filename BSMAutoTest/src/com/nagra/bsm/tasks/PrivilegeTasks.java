/**
 * 
 */
package com.nagra.bsm.tasks;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import com.nagra.bsm.ui.RptCenterPage;
import com.nagra.bsm.util.CommonUtil;
import com.nagra.bsm.util.DBUtil;

/**
 * @author tetang
 * 
 */
public class PrivilegeTasks {
	protected static final Logger logger = Logger
			.getLogger(PrivilegeTasks.class);
	private final String getEncryptedPwd(String rawPwd) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] results = md.digest(rawPwd.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < results.length; i++) {
				String hex = Integer.toHexString(results[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {			
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}


	public void insertUser(String user, String pwd , boolean isActive){
		int user_id =DBUtil.getIdMaxValue("BSM_USER", "USER_ID")+1;
		int status = isActive?1:0;
		String encryptedPwd = getEncryptedPwd(pwd);
		String sql = "insert into BSM_USER values(?,?,?,?)";		
		try {
			PreparedStatement ps =DBUtil.getConnection().prepareStatement(sql);
			ps.setInt(1, user_id);
			ps.setString(2, user.toLowerCase());
			ps.setString(3, encryptedPwd);
			ps.setInt(4, status);
			ps.execute();
			grantRoleToUser(user_id,0);
			logger.info(String.format("Insert user %s to BSM DB", user));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private int getUserId(String user){
		int user_id = -1;
		String sql = "select user_id from BSM_USER where user_name = ?";
		try {
			PreparedStatement ps =DBUtil.getConnection().prepareStatement(sql);				
			ps.setString(1, user.toLowerCase());						
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				user_id = rs.getInt(1);				
			}else{
				logger.error("Fail to get user_id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return user_id;
	}
	
	public void insertRole(String role, String comment){
		int role_id = DBUtil.getIdMaxValue("BSM_ROLE", "ROLE_ID")+1;
		String sql = "insert into BSM_ROLE values(?,?,?)";
		try {
			PreparedStatement ps =DBUtil.getConnection().prepareStatement(sql);
			ps.setInt(1, role_id);
			ps.setString(2, role);
			ps.setString(3, comment);			
			ps.execute();
			logger.info(String.format("Insert role %s to BSM DB", role));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private int getRoleId(String role){
		int role_id = -1;
		String sql = "select role_id from BSM_ROLE where role_name = ?";
		try {
			PreparedStatement ps =DBUtil.getConnection().prepareStatement(sql);			
			ps.setString(1, role);						
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				role_id = rs.getInt(1);				
			}else{
				logger.info("Fail to find role_id.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return role_id;
	}
	
	private void grantRoleToUser(int user_id, int role_id){
		int id = DBUtil.getIdMaxValue("BSM_USER_ROLE", "ID")+1;
		String sql = "insert into BSM_USER_ROLE values(?,?,?)";
		try {
			PreparedStatement ps =DBUtil.getConnection().prepareStatement(sql);
			ps.setInt(1, id);
			ps.setInt(2,user_id);
			ps.setInt(3, role_id);
			ps.execute();			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void grantRolesToUser(String user, String ...roles){
		int user_id = getUserId(user);
		int rid; //role_id
		for(String r : roles){
			rid = getRoleId(r);
			grantRoleToUser(user_id,rid);
			logger.info(String.format("Grant role %s to user %s", r, user));
		}		
	}
	
	private String getPermissionID(String permission_name){
		String permission_id = null;
		String sql = "select permission_id from BSM_PERMISSION where lower(permission_name) = ?";
		PreparedStatement ps;
		try {
			ps = DBUtil.getConnection().prepareStatement(sql);
			ps.setString(1, permission_name.trim().toLowerCase());
			ResultSet rs = ps.executeQuery();				
			if(rs.next()){
				permission_id = rs.getString(1);				
			}else{
				logger.info(String.format("Cannot find permission id of %s", permission_name));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return permission_id;
	}
	
	private String getPermissionParentID(String permission_id){
		String permission_parent_id = null;
		String sql = "select permission_parent_id from BSM_PERMISSION where permission_id = ?";
		PreparedStatement ps;
		try {
			ps = DBUtil.getConnection().prepareStatement(sql);
			ps.setString(1, permission_id);
			ResultSet rs = ps.executeQuery();			
			if(rs.next()){
				permission_parent_id = rs.getString(1);				
			}else{
				logger.error(String.format("Cannot find permission parent id of %s", permission_id));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return permission_parent_id;
	}
	
	private List<String> getSubPermissionIDs(String permission_id){
		List<String> subIds = new ArrayList<String>();
		String sql = "select permission_id from BSM_PERMISSION where permission_parent_id = ?";		
		PreparedStatement ps;
		try {
			ps = DBUtil.getConnection().prepareStatement(sql);
			ps.setString(1, permission_id);
			ResultSet rs = ps.executeQuery();			
			while(rs.next()){
				subIds.add(rs.getString(1)) ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return subIds;
	}	
	
	private boolean roleHasPermission(int role_id, String permission_id){
		String sql = "select * from BSM_ROLE_PERMISSION where role_id = ? and permission_id = ? ";
		PreparedStatement ps;
		try {
			ps = DBUtil.getConnection().prepareStatement(sql);
			ps.setInt(1, role_id);
			ps.setString(2, permission_id);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	public void addPermissionToRole(String role, String ...permission_names){					
		int id = DBUtil.getIdMaxValue("BSM_ROLE_PERMISSION", "ID")+1;			
		int role_id = getRoleId(role);
		String permission_id = null;
		String permission_parent_id = null;
		String subPermission = null;
		String sql = "insert into BSM_ROLE_PERMISSION values(?,?,?)";
		List<String> subPermissions = new ArrayList<String>();
		List<String> subofSub = new ArrayList<String>();
		int size = 0;
		try {
			PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql);
			ps.setInt(2, role_id);
			for(String pname: permission_names){
				System.out.println(pname);
				permission_id = getPermissionID(pname);
				if(permission_id == null){
					logger.error(String.format("Fail to get permission id for permission: %s", pname));
					break;
				}
				ps.setString(3, permission_id);	
				ps.setInt(1, id);
				ps.execute();
				id = id + 1;
				permission_parent_id = permission_id;
				do{
					permission_parent_id = getPermissionParentID(permission_parent_id);	
					if(roleHasPermission(role_id,permission_parent_id)){
						break;
					}else{
						ps.setString(3, permission_parent_id);
						ps.setInt(1, id);
						ps.execute();
						id = id + 1;
					}
				}while(!permission_parent_id.equals("0"));
				subPermissions=getSubPermissionIDs(permission_id);
				do{										
					size = subPermissions.size();
					for(int i = 0; i < size; i++){
						subPermission = subPermissions.get(i);						
						ps.setString(3, subPermission);
						ps.setInt(1, id);
						ps.execute();
						id = id + 1;
						List<String> subPerm = getSubPermissionIDs(subPermission);												
						subofSub.addAll(subPerm);
					}
					if (subofSub.isEmpty()){
						break;			
					}
					subPermissions.clear();
					subPermissions.addAll(subofSub);					
					subofSub.clear();					
				}while(size>0);											
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void removePermissionFromRole(String role,String ...permission_names){
		int role_id = getRoleId(role);
		String sql = "delete from BSM_ROLE_PERMISSION where ROLE_ID = ? and PERMISSION_ID = ?";
		try {
			PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql);
			ps.setInt(1, role_id);
			for(String pname: permission_names){				
				ps.setString(2, getPermissionID(pname));
				ps.execute();
				logger.info(String.format("Add permission %s to user %s",pname, role));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				DBUtil.closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	public void removeRole(String role){
		int role_id = getRoleId(role);
		String sql ;
		Connection coon = DBUtil.getConnection();
		PreparedStatement ps;
		try {
			sql = "delete from BSM_ROLE_PERMISSION where ROLE_ID = ?";
			ps = coon.prepareStatement(sql);
			ps.setInt(1, role_id);
			ps.execute();
			sql = "delete from BSM_USER_ROLE where role_id = ?";
			ps = coon.prepareStatement(sql);
			ps.setInt(1, role_id);
			ps.execute();
			sql = "delete from BSM_ROLE where role_id = ?";
			ps = coon.prepareStatement(sql);
			ps.setInt(1, role_id);
			ps.execute();
			logger.info(String.format("Remove role %s",role));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				coon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	public void removeUser(String user){
		int user_id = getUserId(user);
		String sql ;
		Connection coon = DBUtil.getConnection();
		PreparedStatement ps;
		try{
			sql = "delete from BSM_USER_ROLE where USER_ID = ?";
			ps = coon.prepareStatement(sql);
			ps.setInt(1, user_id);
			ps.execute();
			sql = "delete from BSM_USER where user_id = ?";
			ps = coon.prepareStatement(sql);
			ps.setInt(1, user_id);
			ps.execute();
			logger.info(String.format("Remove role %s",user));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				coon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	private String[] getTextFromWebElement(List<WebElement> weList){
		Iterator<WebElement> it = weList.iterator();
		String[] weText = new String[weList.size()];
		String text;
		System.out.println("Visiable element:");
		int i = 0;
		while(it.hasNext()){
			text = it.next().getText();			
			if(text == null || text.isEmpty()){
				
			}else{
				weText[i] = text;				
				System.out.print(text+"\t");
			}			
			i++;
		}
		return weText;
	}
	
	public boolean verifyExpectElementTextDisplay(String[] expect, List<WebElement> weList){
		String[] actual = getTextFromWebElement(weList);
		boolean equal = true;
		if (expect.length != actual.length){
			equal = false;
			return equal;
		}
		for(int i = 0; i< expect.length; i++){
			if(!expect[i].equalsIgnoreCase(actual[i])){
				equal = false;
				return equal;
			}
		}
		return equal;
	}
	
	public List<WebElement> getMainTabs(RptCenterPage rptCenterPage){
		return rptCenterPage.getMainTabs();
	}
	
/*	public static void main(String[] args) {
		PrivilegeTasks p = new PrivilegeTasks();
		p.insertRole("testersingle", "test single privilege");
		p.addPermissionToRole("testersingle", "security");
		p.insertUser("test1", "admin", true);
		p.grantRolesToUser("test1", "testersingle");
	//	p.removeRole("testersingle");
	//	p.removeUser("test1");
	
		
	}
*/
}
