/*
 * Created on 2005-8-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:SaveBaseOptionsTrans</p>
 * <p>Description:保存人才库参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SaveBaseOptionsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String userBase=(String)this.getFormHM().get("userBase");
		String[] fieldsetvalue=(String[])this.getFormHM().get("fieldsetvalue");
		StringBuffer strsql=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    List paramList=new ArrayList();
	    List list=new ArrayList();
		try
		{
			 strsql.append("delete from constant where constant='ZP_DBNAME'");
		     dao.delete(strsql.toString(),paramList);    //删除常量表中的查询设值得的项
		     strsql.delete(0,strsql.length());
		     strsql.append("delete from constant where constant='ZP_SUBSET_LIST'");
		     dao.delete(strsql.toString(),paramList);    //删除常量表中的查询设值得的项
		     strsql.delete(0,strsql.length());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			
		}
		try
		{
			 StringBuffer str=new StringBuffer();
			 strsql.append("insert into constant(constant,type,str_value,Describe) values(?,'',?,?)");
		     paramList.add("ZP_DBNAME");
		     paramList.add(userBase);
		     paramList.add("人才库");
		     dao.insert(strsql.toString(),paramList);            //添加纪录在常量表中
		     strsql.delete(0,strsql.length());
		     StringBuffer fieldsetstr = new StringBuffer();
		     if(fieldsetvalue!=null)
			 {
			 	for(int i=0;i<fieldsetvalue.length;i++)
			 	{
			 		fieldsetstr.append(fieldsetvalue[i].toUpperCase());       //List中的对象转换成字符串
			 		if(i<fieldsetvalue.length-1)
			 			fieldsetstr.append(",");
			 	}
			 }
		     strsql.append("insert into constant(constant,type,str_value,Describe) values('ZP_SUBSET_LIST','','"+fieldsetstr.toString()+"','子集')");
		     dao.insert(strsql.toString(),list); //添加纪录在常量表中
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
