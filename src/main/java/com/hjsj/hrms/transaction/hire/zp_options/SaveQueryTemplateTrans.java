/*
 * Created on 2005-8-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:SaveQueryTemplateTrans</p>
 * <p>Description:保存查询指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SaveQueryTemplateTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String[] str_valueList=(String[])this.getFormHM().get("right_fields");
		StringBuffer strsql=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    List paramList=new ArrayList();
		try
		{
			 strsql.append("delete from constant where constant='ZP_QUERY_TEMPLATE'");
		     dao.delete(strsql.toString(),paramList);    //删除常量表中的查询设值得的项
		     strsql.delete(0,strsql.length());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			 // this.getFormHM().put("str_valuelist",str_valueList);
		}
		try
		{
			 StringBuffer str=new StringBuffer();
			 if(str_valueList!=null)
			 {
			 	LabelValueView labelValueView;
			 	for(int i=0;i<str_valueList.length;i++)
			 	{
			 		str.append(str_valueList[i].toUpperCase());       //List中的对象转换成字符串
			 		if(i<str_valueList.length-1)
			 			str.append(",");
			 	}
			 }
			 strsql.append("insert into constant(constant,type,str_value,Describe) values(?,?,?,?)");
		     paramList.add("ZP_QUERY_TEMPLATE");
		     paramList.add("1");
		     paramList.add(str.toString());
		     paramList.add("查询模板");
		     dao.insert(strsql.toString(),paramList);            //添加纪录在常量表中
		     strsql.delete(0,strsql.length());
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			String succeedinfo=ResourceFactory.getProperty("label.queryset.succeedinfo");
			this.getFormHM().put("succeedinfo",succeedinfo);
		}

	}

}
