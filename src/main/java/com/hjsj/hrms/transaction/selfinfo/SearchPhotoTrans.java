/*
 * Created on 2005-11-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.utils.sys.CreateTempFile;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.servlet.http.HttpSession;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchPhotoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String userbase=(String)this.getFormHM().get("userbase");//人员库
		String A0100=(String)this.getFormHM().get("a0100");
		if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
			A0100=userView.getUserId();
		String filename="";
	    HttpSession session=(HttpSession)this.getFormHM().get("session");
		try{
			filename=new CreateTempFile().createPicture(userbase+ "A00",A0100,"P",session,this.getFrameconn());
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			this.getFormHM().put("photoname",filename);	    
		}
	}

}
