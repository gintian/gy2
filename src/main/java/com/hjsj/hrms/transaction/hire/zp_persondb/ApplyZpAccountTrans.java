/*
 * Created on 2005-11-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_persondb;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ApplyZpAccountTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		RecordVo constantuser_vo=ConstantParamter.getRealConstantVo("SS_LOGIN_USER_PWD");
		if(constantuser_vo!=null){
    	    String usernamefield=constantuser_vo.getString("str_value");
			if(usernamefield !=null && usernamefield.indexOf(",")>0)
			  usernamefield=usernamefield.substring(0,usernamefield.indexOf(","));
			else
			  usernamefield="username";
	    	cat.debug("------usernamefield--->" + usernamefield);
			ArrayList zpfieldlist=new ArrayList();
			try
			{
				FieldItem item=DataDictionary.getFieldItem(usernamefield);
				if(item!=null)
			    {				   
				   this.getFormHM().put("usermaxlenth",String.valueOf(item.getItemlength()));	
				}
				else
				{
				  this.getFormHM().put("usermaxlenth","50");						
				}						
			}catch(Exception e){
			   e.printStackTrace();
			}finally{				
			   this.getFormHM().put("a0100","");
			   this.getFormHM().put("i9999","");
			   this.getFormHM().put("actiontype","update");
    	       this.getFormHM().put("existusermessage","");		
	       }
	    }
	  }
}
