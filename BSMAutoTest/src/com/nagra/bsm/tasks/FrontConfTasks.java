/**
 * @author tetang
 *
 */
package com.nagra.bsm.tasks;

import com.nagra.bsm.ui.FrontConfPage;

public class FrontConfTasks extends AdministrationTasks{
	private FrontConfPage frontPage;
	
	public FrontConfTasks(FrontConfPage frontPage){
		this.frontPage = frontPage;
	}
	
	public void setFrontPageImage(String file){				 						
		frontPage.setBackGroundImg(file);
		frontPage.clickUploadForBgImg();
		logger.info("Set Front page image");
	}
	
	public void setLoginBoxPosition(int x, int y){						
		frontPage.inputLeftMarginValue(new Integer(x).toString());
		frontPage.inputTopMarginValue(new Integer(x).toString());
		frontPage.clickSave();
		logger.info("Set Login Box Position");
	}
	
	public void setHeaderImage(String file){
		frontPage.setHeaderImg(file);
		frontPage.clickUploadForHeaderImg();
		logger.info("Set Header Image");		
	}
	
	public String getHeaderImg(){
		String img = frontPage.getHdImgInfo();
//		img = img.lastIndexOf(arg0, arg1);
		System.out.print(img);
		return img;	
	}
}

