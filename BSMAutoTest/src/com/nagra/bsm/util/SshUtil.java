/**
 * 
 */
package com.nagra.bsm.util;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jcraft.jsch.*;

/**
 * @author tetang
 * 
 */
public class SshUtil {
	private static final Logger logger = Logger.getLogger(SshUtil.class);
	private static Session session;

	public static void login() {
		String host = CommonUtil.getPropertyValue("host_name");
		String user = CommonUtil.getPropertyValue("host_username");
		String pw = CommonUtil.getPropertyValue("host_password");
		login(host, user, pw);
	}

	public static void login(String hostname, String username, String password) {
		JSch jsch = new JSch();
		try {
			JSch.setConfig("StrictHostKeyChecking", "no");
			session = jsch.getSession(username, hostname, 22);
			session.setPassword(password);
			session.connect();
			logger.info("Set up SSH connection");
		} catch (JSchException e) {
			logger.info("Fail to set up SSH connection.");
			e.printStackTrace();
		}
	}

	public static void exit() {
		if (session != null) {
			session.disconnect();
		}
		logger.info("End session connection");
	}

	public static void exec(String cmd) {		
		try {			
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(cmd);
			logger.info("Execute command: " + cmd);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			channel.setOutputStream(System.out);
			channel.connect();
			channel.disconnect();
		} catch (JSchException e) {
			logger.error("Fail to connect to sft channel.");
			e.printStackTrace();
		}
	}

	public static void sftpGet(String remotePath, String localPath) {
		logger.info("Download file from server");
		try {
			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect(1000);
			try {
				channel.get(remotePath, localPath);
				logger.info("Download file from sftp: " + remotePath);
			} catch (SftpException e) {
				logger.error("Fail to download from sftp.");
				e.printStackTrace();
			}
			channel.disconnect();
		} catch (JSchException e) {
			logger.error("Fail to connect to sft channel.");
			e.printStackTrace();
		}
	}

	public static void sftpPut(String srcPath, String dstPath) {
		logger.info("Upload file from server");
		try {
			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect(1000);
			try {
				channel.put(srcPath, dstPath);
				logger.info("Upload file from sftp: " + dstPath);
			} catch (SftpException e) {
				logger.error("Fail to upload to sftp.");
				e.printStackTrace();
			}
			channel.disconnect();
		} catch (JSchException e) {
			logger.error("Fail to connect to sft channel.");
			e.printStackTrace();
		}
	}

	// get file name which match the pattern
	public static String getExptFile(String dir, Pattern p) {
		logger.info("Check if file exists on sftp server");
		String expect = null;
		try {
			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect(1000);
			try {
				channel.cd(dir);
				Vector files = channel.ls(dir);
				if (files.isEmpty() == false) {
					Object[] obj = files.toArray();
					Matcher matcher;
					String s;
					for (Object o : obj) {
						s = o.toString();
						matcher = p.matcher(s);
						if (matcher.find()) {
							expect = matcher.group();
							logger.info("Find expected file: " + expect);
						} else {
							// logger.info("File is not expected.");
							continue;
						}
					}
				} else {
					logger.info("Folder is empty: " + dir);
				}
			} catch (SftpException e) {
				logger.error("Fail to find file on sftp.");
				e.printStackTrace();
			} finally {
				channel.disconnect();
			}
		} catch (JSchException e) {
			logger.error("Fail to connect to sft channel.");
			e.printStackTrace();
		}
		return expect;
	}

	// check in dir ,if exist file which file name contains fileName
	public static boolean fileNameExists(String dir, String fileName) {
		logger.info("Check if file exists on sftp server");
		boolean fileExist = false;
		try {
			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect(1000);
			try {
				Vector files = channel.ls(dir);
				if (files.isEmpty() == false) {
					Object[] obj = files.toArray();
					String s;
					for (Object o : obj) {
						s = o.toString();
						if (s.contains(fileName)) {
							fileExist = true;
						}
					}
				} else {
					logger.info("No file in " + dir);
				}
			} catch (SftpException e) {
				logger.error("Fail to find file on sftp.");
				e.printStackTrace();
			} finally {
				channel.disconnect();
			}
		} catch (JSchException e) {
			logger.error("Fail to connect to sft channel.");
			e.printStackTrace();
		}
		return fileExist;
	}

	public static void sftpRemove(String dir, String fileName) {
		try {
			ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect(1000);
			try {
				channel.cd(dir);
				logger.info(new StringBuilder("Current path is: ").append(channel.pwd()));
				Vector files = channel.ls(dir);
				if (files.isEmpty() == false) {
					Object[] obj = files.toArray();
					String s;
					for (Object o : obj) {
						s = o.toString();
						if (s.contains(fileName)) {
							channel.rm(fileName);
							logger.info(new StringBuilder("Remove file: ").append(fileName));
							return;
						}
					}
				} else {
					logger.info(new StringBuilder("No file in ").append(dir));
					return;
				}
			} catch (SftpException e) {
				logger.error("Fail to find file on sftp.");
				e.printStackTrace();
			}finally{
				channel.disconnect();
			}
		} catch (JSchException e) {
			logger.error("Fail to connect to sft channel.");
			e.printStackTrace();
		}
	}

	public static void sfptUpload(String srcPath, String dstPath) {
		login();
		sftpPut(srcPath, dstPath);
		exit();
	}

	public static void sftpDownload(String remotePath, String localPath) {
		login();
		sftpGet(remotePath, localPath);
		exit();
	}

	// check in dir ,if exist file which file name contains fileName
	public static boolean chkfileExistOnSftp(String dir, Pattern p) {
		login();
		String expect = null;
		boolean exist = false;
		int i = 0;
		while (i < 6) {
			i++;
			CommonUtil.sleep(10);
			expect = getExptFile(dir, p);
			if (expect != null) {
				exist = true;
				break;
			}
		}
		exit();
		return exist;
	}

	public static void main(String[] args) {
		SshUtil.login();
		// SshUtil.exec("ls");
		SshUtil.exec("/opt/mongodb-linux-x86_64-2.2.0/bin/mongod &");
		SshUtil.exit();
	}
}
