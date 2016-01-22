/**
 * 
 */
package com.nagra.bsm.util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.search.AndTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;


/**
 * @author tetang
 *
 */
public class EmailUtil {
private static Store store;
private static Folder folder;
private static Message[] message;
	
	/**
	 * @param args
	 */
	
	public static void initReceive(){
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "pop3");
		props.setProperty("mail.pop3.host", "antares.hq.k.grp");
		Session session = Session.getInstance(props);
		try {
			store = session.getStore("pop3");
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		session.setDebug(true);
	}
	
	public static Message[] receive(){
		String username = CommonUtil.getPropertyValue("domain_username");
		String password = CommonUtil.getPropertyValue("domain_password");
		try {
			store.connect("pop.sina.com", "bsmnagra", "bsmnagra");
		//	store.connect("antares.hq.k.grp", "hq\\tetang", password);			
			System.out.print(store.isConnected());
			folder = store.getFolder("Inbox");
			folder.open(Folder.READ_WRITE);
			message = folder.getMessages();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.print(message.length);
		return message;
	}
	
	//Search email and mark it as readed
	public static Message[] searchMail(String sub){
		String sender = CommonUtil.getPropertyValue("BSM@nagra.com");
		SearchTerm st = new AndTerm(new FromStringTerm(sender),new SubjectTerm(sub));
		Message[] semsgs = null;
		try {
			 semsgs = folder.search(st);
			 System.out.print(semsgs.length);
			for(Message ms:semsgs){
				ms.setFlag(Flags.Flag.SEEN,true);
				ms.saveChanges();
			}			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print(semsgs.length);
		return semsgs;
	}
	
	public static void close(){
		try {
			folder.close(false);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean doesMailExist(String sub){
		initReceive();
		receive();
		Message[] msgs = searchMail(sub);
		close();
		if (msgs!=null&&msgs.length>0)
			return true;
		else
			return false;
	}
	
	public static void main(String[] args){
		EmailUtil email = new EmailUtil();
		Boolean isexist = false;
		email.doesMailExist("[BSM] Generated report - E_3451");
		System.out.print(isexist);
	}
}	