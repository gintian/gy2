package com.hjsj.hrms.transaction.hire.jp_contest.param;

import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParam;
import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SaveAppFileTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class SaveAppFileTrans  extends IBusiness {

  
    public void execute() throws GeneralException {
        ArrayList code_fields=(ArrayList)this.getFormHM().get("code_fields");    
        saveRname(code_fields);
        EngageParam ep = new EngageParam(this.frameconn);
		this.getFormHM().put("mess",ep.getAppMesslist(code_fields));
	}
    /**
	  * 通过表编号的到表信息
	  * @param cardno
	  * @return String
	  */
	
	  private boolean saveRname(ArrayList code_fields)throws GeneralException
	  {
		  boolean isCorrect=false;
		  StringBuffer buf=new StringBuffer();
		  if(code_fields==null||code_fields.size()<=0)
			  buf.append("");
		  else
		  {
			  for(int i=0;i<code_fields.size();i++)
			  {
				  buf.append(""+code_fields.get(i).toString()+",");
			  }
			  buf.setLength(buf.length()-1);
		  }
		  EngageParamXML engageParamXML=new EngageParamXML(this.getFrameconn()); 	 
		  isCorrect=engageParamXML.setTextValue(EngageParamXML.APP_VIEW,buf.toString());
		  engageParamXML.saveParameter();
		  return isCorrect;
	  }
}

