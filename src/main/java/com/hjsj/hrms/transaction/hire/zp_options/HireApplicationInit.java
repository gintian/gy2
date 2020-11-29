package com.hjsj.hrms.transaction.hire.zp_options;

import javax.servlet.ServletContext;
import java.util.Enumeration;

public class HireApplicationInit extends java.util.TimerTask{

	private ServletContext context; 


    public HireApplicationInit(ServletContext context){ 
        this.context = context; 
    } 
    /**
     * 每天凌晨清空错误登录信息
     */
	public void run() {
	// TODO Auto-generated method stub
		this.context.getAttributeNames();
		Enumeration enumSessionId = this.context.getAttributeNames();
		String attributeName="";
		while(enumSessionId.hasMoreElements()){
			attributeName=(String) enumSessionId.nextElement();
			if(attributeName.indexOf("@")!=-1&&(attributeName.indexOf(".com")!=-1||attributeName.indexOf(".net")!=-1)&&this.context.getAttribute(attributeName)!=null){
				//System.out.println(attributeName);
				this.context.removeAttribute(attributeName);
			}				
		}
	}

}



