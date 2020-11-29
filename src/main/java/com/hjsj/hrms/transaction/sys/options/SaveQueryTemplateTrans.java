/**
 * 
 */
package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:SaveQueryTemplateTrans</p>
 * <p>Description:保存查询模板</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-3-1:12:57:42</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveQueryTemplateTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
	    String[] str_valueList=(String[])this.getFormHM().get("right_fields");
	    String classpre=(String)this.getFormHM().get("classpre");
	    /**=A人员,=B单位,=K职位,=H基准岗位
	     *=5(Y) 党组织
	     *=6(V) 团组织
	     *=7(W) 工会组织	    
	     */
	    if(classpre==null|| "".equals(classpre))
	    	classpre="A";
		StringBuffer strsql=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    List paramList=new ArrayList();
		try
		{
			 if("K".equals(classpre))
				 strsql.append("delete from constant where constant='SS_KQUERYTEMPLATE'");
			 else if("B".equals(classpre))
				 strsql.append("delete from constant where constant='SS_BQUERYTEMPLATE'");	
			 else if("Y".equals(classpre))
				 strsql.append("delete from constant where constant='SS_YQUERYTEMPLATE'");	
			 else if("V".equals(classpre))
				 strsql.append("delete from constant where constant='SS_VQUERYTEMPLATE'");	
			 else if("W".equals(classpre))
				 strsql.append("delete from constant where constant='SS_WQUERYTEMPLATE'");				 
			 else if("H".equals(classpre))
				 strsql.append("delete from constant where constant='SS_HQUERYTEMPLATE'");				 
			 else 
				 strsql.append("delete from constant where constant='SS_QUERYTEMPLATE'");
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
			 if(str_valueList!=null)
			 {
			 	for(int i=0;i<str_valueList.length;i++)
			 	{
			 		str.append(str_valueList[i].toUpperCase());       //List中的对象转换成字符串
			 		if(i<str_valueList.length-1)
			 			str.append(",");
			 	}
			 }
			 if("K".equals(classpre))
			 {
				 strsql.append("insert into constant(constant,type,str_value,Describe) values(?,?,?,?)");
			     paramList.add("SS_KQUERYTEMPLATE");
			     paramList.add("K");
			 }
			 else if("B".equals(classpre))
			 {
				 strsql.append("insert into constant(constant,type,str_value,Describe) values(?,?,?,?)");
			     paramList.add("SS_BQUERYTEMPLATE");
			     paramList.add("B");				 
			 }
			 else if("Y".equals(classpre))
			 {
				 strsql.append("insert into constant(constant,type,str_value,Describe) values(?,?,?,?)");
			     paramList.add("SS_YQUERYTEMPLATE");
			     paramList.add("Y");				 
			 }
			 else if("V".equals(classpre))
			 {
				 strsql.append("insert into constant(constant,type,str_value,Describe) values(?,?,?,?)");
			     paramList.add("SS_VQUERYTEMPLATE");
			     paramList.add("V");				 
			 }
			 else if("W".equals(classpre))
			 {
				 strsql.append("insert into constant(constant,type,str_value,Describe) values(?,?,?,?)");
			     paramList.add("SS_WQUERYTEMPLATE");
			     paramList.add("W");				 
			 }
			 
			 else if("H".equals(classpre)) {
					 strsql.append("insert into constant(constant,type,str_value,Describe) values(?,?,?,?)");
				     paramList.add("SS_HQUERYTEMPLATE");
				     paramList.add("H");				 
			}
			else
					 
			 {
				 strsql.append("insert into constant(constant,type,str_value,Describe) values(?,?,?,?)");
			     paramList.add("SS_QUERYTEMPLATE");
			     paramList.add("A");				 
			 }
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
		this.getFormHM().put("classpre", "A");
	}

}
