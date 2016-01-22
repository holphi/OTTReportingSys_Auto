/**
 * 
 */
package com.nagra.bsm.util;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * @author tetang
 * 
 */
public class FtpUtil {
	private static final Logger logger = Logger.getLogger(FtpUtil.class);
	private static FTPClient ftp = new FTPClient();

	/**
	 * log on with default port
	 * 
	 * @param args
	 */
	public static boolean login(String ipaddress, String username,
			String password) {
		try {
			ftp.connect(ipaddress);
			logger.info("Connect to FTP server " + ipaddress);
			if (username == null || username.isEmpty()) {
				ftp.login("anonymous", "anonymous");
				logger.info("Connect to FTP server anonymous ");
			} else {
				ftp.login(username, password);
				logger.info(String.format(
						"Connect to FTP server with %s and pw %s ", username,
						password));
			}
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				logger.info("FTP server refused connection.");
				return false;
			} else
				logger.info("Connect FTP server successfully.");
			return true;
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void login() {
		String host = CommonUtil.getPropertyValue("ftp_url");
		String user = CommonUtil.getPropertyValue("ftp_username");
		String pw = CommonUtil.getPropertyValue("ftp_password");
		login(host, user, pw);
	}

	// get all file list in a path
	public static List<String> getFileList(String path) {
		FTPFile[] files;
		List<String> fs = new ArrayList<String>();
		try {
			files = ftp.listFiles(path);
			if (files.length > 0) {
				for (FTPFile f : files) {
					fs.add(f.getName());
				}
			} else
				logger.info("No files in " + path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fs;
	}

	public static String getExptFile(String path, Pattern exptFile) {
		FTPFile[] files;
		String filename;
		Matcher matcher;
		String expect = null;
		try {
			files = ftp.listFiles(path);
			if (files.length > 0) {
				for (FTPFile f : files) {
					filename = f.getName();
					matcher = exptFile.matcher(filename);
					if (matcher.find()) {
						expect = matcher.group();
						logger.info("Find expected file: " + expect);
					} else {
					//	logger.info("File is not expected: " + filename);
						continue;
					}
				}
			} else
				logger.info("No files in " + path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return expect;
	}

	// get files which filename contains keyword
	public static List<String> getFileList(String path, String keyword) {
		FTPFile[] files;
		List<String> fs = new ArrayList<String>();
		String filename;
		try {
			files = ftp.listFiles(path);
			if (files.length > 0) {
				for (FTPFile f : files) {
					filename = f.getName();
					if (filename.contains(keyword)) {
						fs.add(filename);
					}
				}
			} else
				logger.info("No files in " + path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fs;
	}

	public static boolean chkFileExistOnFtp(String dir, Pattern p) {
		String ftpip = CommonUtil.getPropertyValue("ftp_url");
		String username = CommonUtil.getPropertyValue("ftp_username");
		String password = CommonUtil.getPropertyValue("ftp_password");
		return chkFileExistOnFtp(ftpip, dir, username, password, p);
	}

	public static boolean chkFileExistOnFtp(String ftpip, String dir,
			String username, String password, Pattern p) {
		login(ftpip, username, password);
		String expect = null;
		boolean exist = false;
		int i = 0;
		while (i < 3) {
			i++;
			CommonUtil.sleep(10);
			expect = getExptFile(dir, p);
			if (expect != null) {
				exist = true;
				break;
			}
		}
		logout();
		return exist;
	}

	public static void logout() {
		try {
			if (ftp.isConnected()) {
				ftp.logout();
				logger.info("Logout from FTP server ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FtpUtil.chkFileExistOnFtp("vm81653", "/report", "", "", Pattern
				.compile("SDP_NMP_20130603_1335[0-5][0-9]_1370237534249.pdf"));
	}

}
