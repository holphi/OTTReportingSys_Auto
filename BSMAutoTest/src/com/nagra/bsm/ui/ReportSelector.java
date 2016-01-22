package com.nagra.bsm.ui;

import org.openqa.selenium.*;

public class ReportSelector extends BasePage {

	public ReportSelector(WebDriver driver) {
		super(driver);
		logger.info("Report selector window created.");
	}

	public void selectReport(String... rptArr) {
		for (String rptName : rptArr) {
			WebElement rptgrid = driver.findElement(By.id("schedulerRptGridDiv"));
			WebElement txt_item =rptgrid.findElement(By.xpath(String.format(
					".//td[text()=\"%s\"]", rptName)));
			WebElement img_item = txt_item
					.findElement(By.xpath("../td[1]/img"));
			if (img_item.getAttribute("src").endsWith("chk0.gif")) {
				logger.info(String.format(
						"On Report Select window, check on %s", rptName));
				img_item.click();
			}
		}
	}
	

	public boolean isRptItemChecked(String rptName) {
		WebElement txt_item = driver.findElement(By.xpath(String.format(
				"//td[@title=\"%s\"]", rptName)));
		WebElement img_item = txt_item.findElement(By.xpath("../td[1]/img"));
		if (img_item.getAttribute("src").endsWith("chk0.gif"))
			return false;
		else
			return true;
	}

	public boolean isRptItemPresent(String rptName) {
		return isElementPresent(By.xpath(String.format("//td[text()='%s']",
				rptName)));
	}

	public void clickNavItem(String txtName) {
		driver.findElement(
				By.xpath(String.format("//div[text()='%s']", txtName))).click();
	}

	public boolean isNavItemEnabled(String txtName) {
		WebElement divTxt = driver.findElement(By.xpath(String.format(
				"//div[text()=\"%s\"]", txtName)));
		if (divTxt.findElement(By.xpath("../img")).getAttribute("src")
				.endsWith("dis.gif"))
			return false;
		return true;
	}

	public String getCurrentPagingInfo() {
		return driver.findElement(By.xpath("//div[@class='dhx_toolbar_text']"))
				.getText();
	}

	public void clickOK() {
		// logger.info(String.format("Count number of OK btn %d",
		// driver.findElements(By.xpath("//div[contains(text(),'OK')]")).size()));
		driver.findElements(By.xpath("//div[contains(text(),'OK')]")).get(3)
				.click();
	}

	public int getFileCount() {
		int count = 0;
		int currNum = 0;

		do {
			// Exclude header row
			currNum = driver.findElements(
					By.xpath("//div[@class='objbox']/table/tbody/tr")).size() - 1;
			count = count + currNum;
			this.clickNavItem("Next");
		} while (this.isNavItemEnabled("Next"));
		currNum = driver.findElements(
				By.xpath("//div[@class='objbox']/table/tbody/tr")).size() - 1;
		count = count + currNum;

		this.clickNavItem("First");
		return count;
	}
}
