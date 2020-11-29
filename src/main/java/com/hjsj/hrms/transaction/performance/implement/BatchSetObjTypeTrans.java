package com.hjsj.hrms.transaction.performance.implement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:BatchSetObjTypeTrans.java</p>
 * <p>Description:批量设置考核对象类型</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class BatchSetObjTypeTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		try
		{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String body_id = (String) hm.get("objTypeId");
			hm.remove("objTypeId");
			
			String planid=(String)this.getFormHM().get("planid");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sqlString=(String)this.getFormHM().get("sqlString"); // 得到登录用户范围			
			
		/*	String[] objectIDs=(String[])this.getFormHM().get("objectIDs");			
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<objectIDs.length;i++)
			{
				if(objectIDs[i].trim().length()>0)
					whl.append(",'"+objectIDs[i]+"'");
			}
			if(body_id.trim().length()==0)
				body_id="null";
			if(whl.length()>0)	*/
			{
			//	dao.update("update per_object set body_id="+body_id+" where plan_id="+planid+" and object_id in ("+whl.substring(1)+")");	
				
				if(sqlString!=null && sqlString.trim().length()>0)
					dao.update("update per_object set body_id="+body_id+" where plan_id="+planid+" and "+sqlString );
				else
					dao.update("update per_object set body_id="+body_id+" where plan_id="+planid+" ");
				
				//如果绩效结果表存在的话也在此更新绩效结果表中的考核对象类型    JinChunhai  2011.03.17 修改
				DbWizard dbWizard = new DbWizard(this.getFrameconn());
				String tablename = "per_result_"+planid;
				Table table = new Table(tablename);
				if(dbWizard.isExistTable(tablename, false))
    			{
					if (!dbWizard.isExistField(tablename, "Body_id",false))
					{
					    Field obj = new Field("Body_id");
					    obj.setDatatype(DataType.INT);
						obj.setKeyable(false);
					    table.addField(obj);
					    dbWizard.addColumns(table);// 更新列
					}
					
					StringBuffer buf = new StringBuffer();
				    if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				    {
				    	buf.append("update per_result_"+planid +" set body_id=(select body_id from per_object where ");
				    	buf.append("per_result_"+planid +".object_id=per_object.object_id and per_object.plan_id="+planid+")");
				    }else  if (Sql_switcher.searchDbServer() == Constant.MSSQL)
				    {
				    	buf.append("update per_result_"+planid +" set body_id=per_object.body_id from per_object where per_result_"+planid +".object_id=per_object.object_id and per_object.plan_id="+planid);
				    }					
					dao.update(buf.toString());
    			}				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
