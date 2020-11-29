/*
 * Created on 2005-5-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.addressbook;

import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**你背着我爱别人。一声声被撕裂。最爱你的人却伤你最深。
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveAddressBookFieldTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	    //List str_valueList=(List)this.getFormHM().get("str_valuelist");
		ArrayList fields=(ArrayList)this.getFormHM().get("items");
		StringBuffer strsql=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());

	    List paramList=new ArrayList();
		try
		{
			 strsql.append("delete from constant where constant='SS_ADDRESSBOOK'");
		     dao.delete(strsql.toString(),paramList);  //删除常量表中的通讯录纪录
		     strsql.delete(0,strsql.length());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			  //this.getFormHM().put("str_valuelist",str_valueList);
		}
		try
		{
			 StringBuffer str=new StringBuffer();
			 
			 if(fields!=null)                //吧不为空的List中的所有对象转换成字符串
			 {
			 	LabelValueView labelValueView;
			 	for(int i=0;i<fields.size();i++)
			 	{
			 	   String fieldname=fields.get(i).toString();
			       if(fieldname==null|| "".equals(fieldname))
			             continue;
			 		str.append(fieldname);
			 		if(i<fields.size()-1)
			 			str.append(",");
			 	}
			 }
			 strsql.append("insert into constant(constant,type,str_value,Describe) values(?,?,?,?)");
		     paramList.add("SS_ADDRESSBOOK");
		     paramList.add("A");
		     paramList.add(str.toString());
		     paramList.add("通讯录");
		     dao.insert(strsql.toString(),paramList);               //添加纪录
		     strsql.delete(0,strsql.length());
		     this.getFormHM().put("state", "ok");
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new GeneralException("保存失败，请稍后再试");
		}finally{
			 // this.getFormHM().put("str_valuelist",str_valueList);
		}

	}

}
