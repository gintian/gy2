/**
 * 
 */
package com.hjsj.hrms.actionform.help;

import com.hrms.struts.action.FrameForm;

/**
 * <p>Title:HRPHelpForm</p>
 * <p>Description:系统帮助</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 13, 2006:3:40:32 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class HRPHelpForm extends FrameForm {

	private String url;
	
	@Override
    public void outPutFormHM() {
		this.setUrl((String)this.getFormHM().get("url"));
	}


	@Override
    public void inPutTransHM() {
		this.setUrl("");
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}

	
}
