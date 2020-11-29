package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
		  CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
		  statid=checkPrivSafeBo.checkResource(IResourceConstant.STATICS, statid);
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  RowSet rs = null;
		  StringBuffer msgStr = new StringBuffer();
		  if("0".equals(type))
		  {
			  try {
				    StringBuffer sql=new StringBuffer();
				    //删除一维统计项前，判断是否被二维或多维使用   wangb 2019-12-11 bug 56278
				    if(Sql_switcher.searchDbServer() == Constant.ORACEL){
				    	sql.append("select name from sname where ','|| HV || ',' like ? ");
				    }else{
				    	sql.append("select name from sname where ',' + HV + ',' like ? ");
				    }
				    rs = dao.search(sql.toString(),Arrays.asList("%,"+statid+",%"));
				    while(rs.next()){
				    	String name = rs.getString("name");
				    	msgStr.append("【"+name+"】、");
				    }
				    if(msgStr.length() > 0 ){
				    	msgStr.setLength(msgStr.length()-1);
				    	String msg = "该统计项被"+msgStr.toString()+"统计项使用，删除失败！";
				    	throw GeneralExceptionHandler.Handle(new GeneralException(msg));
				    }
				    sql.setLength(0);
				    sql.append("delete from SLegend  where id='"+statid+"'");
					dao.delete(sql.toString(), new ArrayList());
					sql.setLength(0);
					sql.append("delete from sname where id='"+statid+"'");
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
			  sql.append("delete from SLegend  where id="+statid+"");
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
