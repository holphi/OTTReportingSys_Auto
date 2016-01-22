package com.nagra.bsm.util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;

/**
 * 
 */

/**
 * @author tetang
 *
 */
public class WebdavUtil {
	private static final Logger logger = Logger.getLogger(WebdavUtil.class);
	private static Sardine sardine;
	
	public static void begin(){
		String user = CommonUtil.getPropertyValue("http_username");
		String pw = CommonUtil.getPropertyValue("http_password");
		sardine = SardineFactory.begin(user,pw);
		logger.info("Connect to Webdav with username: " + user + " and password " + pw);
	}
	
	public static void beginAnonymous(){
		sardine = SardineFactory.begin();
		logger.info("Connect to Webdav anonymous");
	}
	
	public static void begin(String username, String password){
		sardine = SardineFactory.begin(username,password);
		logger.info("Connect to Webdav with username: " + username + " and password " + password);
	}
	
	
	/**
	 * This returns a List filenames for a directory on a remote dav server. The URL should be properly encoded and must end with a "/" for a directory. .
	 * @param args
	 */
	public static List<String> getFileList(String webdavURL){
		if(!webdavURL.endsWith("/")){
			webdavURL = webdavURL + "/";
		}
		List<String> files = new ArrayList<String>();
		String filename;
		try {
			List<DavResource> resources = sardine.list(webdavURL);
			for (DavResource res : resources){				
				filename = res.getName().toString();
				files.add(filename);
//				System.out.println(filename);
			}
		} catch (IOException e) {
			logger.error("Fail to list files in "+ webdavURL);
			e.printStackTrace();
		}
		return files;
	}
	
	public static String getExptFile(String webdavURL, Pattern exptFile){
		String filename;
		Matcher matcher;
		String expect = null;	
		List<String> files = getFileList(webdavURL);
		//parent folder also in file list ,so size >1
		if(files.size()>1){			
			Iterator<String> i = files.iterator();
			while(i.hasNext()){
				filename = i.next();
				matcher = exptFile.matcher(filename);
				if (matcher.find()) {					
					expect = matcher.group();
					logger.info("Find expected file:" + expect );
				}else{
//					logger.info("File is not expected.");
					continue;							
				}
			}
		}else 
			logger.info("No file in this path.");
		return expect;
	}
	
	public static boolean chkFileExistOnWebDav(String webdavURL, String username, String password, Pattern exptFile){
		if(username == null || username.isEmpty()){
			beginAnonymous();
		}else{
			begin(username, password);
		}
		String expect = null;
		boolean exist = false;
		int i = 0;
		while (i < 3) {
			i++;
			CommonUtil.sleep(10);
			expect = getExptFile(webdavURL, exptFile);
			if (expect != null) {
				exist = true;
				break;
			}
		}		
		return exist;		
	}	
	
	public static boolean chkFileExistOnWebDav(Pattern exptFile){
		String webdavURL = CommonUtil.getPropertyValue("http_url");
		String username = CommonUtil.getPropertyValue("http_username");
		String password = CommonUtil.getPropertyValue("http_password");
		return chkFileExistOnWebDav(webdavURL, username , password, exptFile);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		begin();
		getFileList("http://172.22.2.93/incoming/repo");

	}

}
