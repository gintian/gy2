/*
 * Created on 2005-5-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddQueryFieldTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 List str_valueList=(List)this.getFormHM().get("str_valuelist");
		 String str_valueini=(String)this.getFormHM().get("str_valueini");
		try
        {
		   LabelValueView labelvalueview;		  
		   boolean isnotExist=true;       //不存在
		   if(!str_valueList.isEmpty())
		   {
		   	for(int i=0;i<str_valueList.size();i++)                //判断List中是否有该指标项
		   	{
		   		labelvalueview=(LabelValueView)str_valueList.get(i);
		   		if(labelvalueview.getValue().equals(str_valueini)){
		   			isnotExist=false;
		   			break;
		   		}
		   	}
		   }
		   if(isnotExist)                                    //如果不存在则加入List
		   {
		     StringBuffer strsql=new StringBuffer();
		     strsql.append("select itemid,itemdesc from fielditem where fieldsetid='");
			 strsql.append(this.getFormHM().get("mainset"));
			 strsql.append("' and useflag='1' and itemid='");
		     strsql.append(str_valueini);
		     strsql.append("'");
		     ContentDAO dao=new ContentDAO(this.getFrameconn());            
		     this.frowset=dao.search(strsql.toString());                     //取得该指标项的描述
		     strsql.delete(0,strsql.length());
		     if(this.frowset.next())
		     	str_valueList.add(new LabelValueView(this.frowset.getString("itemid"),this.frowset.getString("itemdesc")));
		   }
       }catch(Exception e){
          e.printStackTrace();
       }finally{
       	this.getFormHM().put("str_value",str_valueini);
       	this.getFormHM().put("str_valuelist",str_valueList);
       }
	}

}
