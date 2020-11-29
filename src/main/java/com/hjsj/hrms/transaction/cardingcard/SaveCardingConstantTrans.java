/*
 * Created on 2005-5-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.cardingcard;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveCardingConstantTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
        {
		   StringBuffer strsql=new StringBuffer();
           ContentDAO dao=new ContentDAO(this.frameconn);
	       RecordVo constant_vo=(RecordVo)this.getFormHM().get("constant_vo");
		   if(constant_vo!=null)      
		   {	 
		   	strsql.append("select Tabid,FlagA from Rname where tabid=");
		   	strsql.append(constant_vo.getString("str_value"));
		   	this.frowset=dao.search(strsql.toString());
		   	strsql.delete(0,strsql.length());
		   	if(this.frowset.next())
		   		constant_vo.setString("type",this.frowset.getString("flaga"));
			dao.updateValueObject(constant_vo);
			this.getFormHM().put("constant_vo",constant_vo);
		   }
	       else                                        //如果没有设置薪酬表就添加一条纪录
	       {
			  
			  ArrayList cardValueList=new ArrayList();
			  strsql.append("insert into constant(constant,type,str_value,Describe)values(?,?,?,?)");
			  cardValueList.add("SS_CALLINGCARD");
			  cardValueList.add("-");
			  cardValueList.add("-1");
			  cardValueList.add("名片夹");
			  dao.insert(strsql.toString(),cardValueList);
			  this.getFormHM().put("constant_vo",constant_vo);
	       }
        }catch(Exception e){
           e.printStackTrace();
        }

	}

}
