package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;

/**
 * <p>Title:DeleteFieldTrans.java</p>
 * <p>Description:删除考核指标</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-1-11 下午04:09:47</p>
 * @author JinChunhai
 * @version 4.0
 */

public class DeleteFieldTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			String ids = (String)this.getFormHM().get("ids");
			String pointsetid = (String)this.getFormHM().get("pointsetid");
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			String opt=(String)this.getFormHM().get("opt");
			UserView userView = this.userView;
			if("1".equals(opt))
			{
		    	KhFieldBo  bo = new KhFieldBo(this.getFrameconn());
		    	String msg = bo.isUsed(ids,subsys_id);
		    	String duxie = bo.isduxie(ids,userView);
		    	this.getFormHM().put("msg",msg);
				this.getFormHM().put("pointsetid",pointsetid);
				this.getFormHM().put("subsys_id",subsys_id);
				this.getFormHM().put("ids",ids);
				this.getFormHM().put("duxie",duxie);
			}
			else
			{
				String [] temp = ids.replaceAll("／", "/").split("/");
				StringBuffer buf = new StringBuffer();
				for(int i=0;i<temp.length;i++)
				{
					buf.append(",'");
					buf.append(temp[i]);
					buf.append("'");
				}
				KhFieldBo  bo = new KhFieldBo(this.getFrameconn());
				ContentDAO dao = new ContentDAO(this.frameconn);
				StringBuffer context = new StringBuffer();
				context.append("删除指标：");
				String sql = "select pointname from per_point where point_id in("+buf.toString().substring(1)+") and pointsetid ='"+pointsetid+"'";
				RowSet rs = dao.search(sql);
				while(rs.next()){
					context.append(rs.getString("pointname")+",");
				}
				bo.deleteField(buf.toString().substring(1), pointsetid);
				if(context.length()>0){
					this.getFormHM().put("@eventlog", context.toString());
				}
			}
			this.getFormHM().put("pointsetid",pointsetid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
