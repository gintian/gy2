package com.hjsj.hrms.transaction.stat.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 删除统计项或是统计条件
 * @author Owner
 *
 */
public class DelStatItemTrans extends IBusiness{
	
	  public void execute() throws GeneralException 
	  {
		  String editid=(String)this.getFormHM().get("norder");
		  String statid=(String)this.getFormHM().get("statid");
		  if(statid==null||statid.length()<=0)
			  throw GeneralExceptionHandler.Handle(new GeneralException("该统计项不存在！"));
		  String type=(String)this.getFormHM().get("type");
		  if(type==null||type.length()<=0)
			  throw GeneralExceptionHandler.Handle(new GeneralException("删除失败！"));
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  if("0".equals(type))
		  {
			  StringBuffer sql=new StringBuffer();
			  sql.append("delete from hr_hisdata_slegend  where id='"+statid+"'");
			  try {
					dao.delete(sql.toString(), new ArrayList());
					sql.setLength(0);
					sql.append("delete from hr_hisdata_sname where id='"+statid+"'");
					dao.delete(sql.toString(), new ArrayList());
					this.getFormHM().put("opflag", "true");
			   } catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.getFormHM().put("opflag", "false");
			  }
		  }else if("1".equals(type))
		  {
			  if(editid==null||editid.length()<=0)
				  throw GeneralExceptionHandler.Handle(new GeneralException("该统计条件不存在！"));
			  StringBuffer sql=new StringBuffer();
			  sql.append("delete from hr_hisdata_slegend  where id="+statid+"");
			  sql.append(" and norder="+editid+"");
			  try {
				dao.update(sql.toString());
				this.getFormHM().put("opflag", "true");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.getFormHM().put("opflag", "false");
			}
		  }
		  
	  }
}
