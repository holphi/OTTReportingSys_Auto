package com.nagra.bsm.ui;

import java.util.List;
import java.util.Hashtable;

import org.openqa.selenium.*;

public class LogViewerPage extends AdminPage{

	public LogViewerPage(WebDriver driver)
	{
		super(driver);
		logger.info("LogViewerPage object created.");
	}
	
	private List<WebElement> getTrLogList()
	{
		List<WebElement> tableLst = driver.findElements(By.xpath("//table[@class='obj row20px']"));
		WebElement table = tableLst.get(tableLst.size()-1);
		return table.findElements(By.xpath("./tbody/tr"));
	}
	
	public int getLogCountInCurrPage()
	{
		return getTrLogList().size()-1;
	}
	
	public Hashtable<String, String> getLogItemByIndex(int index)
	{
		if((index==0)||index>getLogCountInCurrPage())
			return null;
		
		WebElement tr = getTrLogList().get(index);

		//Create a hash table instance.
		Hashtable<String, String> result = new Hashtable<String, String>();
		
		//ID
		result.put("ID", tr.findElement(By.xpath("./td[1]")).getText().trim());
		//Action
		result.put("Action", tr.findElement(By.xpath("./td[2]")).getText().trim());
		//Description
		result.put("Description", tr.findElement(By.xpath("./td[3]")).getText().trim());
		//Operator
		result.put("Operator", tr.findElement(By.xpath("./td[4]")).getText().trim());
		//Date
		result.put("Date", tr.findElement(By.xpath("./td[5]")).getText().trim());
		//Status
		result.put("Status", tr.findElement(By.xpath("./td[6]")).getText().trim());
		
		return result;
	}
}
