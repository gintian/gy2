package com.hjsj.hrms.transaction.kq.app_check_in.exchange_class;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Date;
public class SelectEmpClassTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String a0100=(String)this.getFormHM().get("a0100");
			String nbase=(String)this.getFormHM().get("nbase");
			String date=(String)this.getFormHM().get("date");
			String z1 = (String)this.getFormHM().get("z1");
			String z1str = (String) this.getFormHM().get("z1str");
			String z3str = (String) this.getFormHM().get("z3str");
			String class_name="";
			String class_id="";
			if(a0100==null|| "".equals(a0100)||nbase==null||a0100.length()<=0)
			{
				this.getFormHM().put("class_name",class_name);
				this.getFormHM().put("class_id",class_id);
				return;
			}				
			if(date==null|| "".equals(date))
			{
				this.getFormHM().put("class_name",class_name);
				this.getFormHM().put("class_id",class_id);
				return;
			}
			date=date.replaceAll("-","\\.");
			StringBuffer sql=new StringBuffer();
			sql.append("select "+Sql_switcher.isnull("class_id","''")+" as class_id from kq_employ_shift");
			sql.append(" where nbase='"+nbase+"'");
			sql.append(" and a0100='"+a0100+"'");
			sql.append(" and q03z0='"+date+"'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			
			if(this.frowset.next())
			{
				class_id=this.frowset.getString("class_id");
			}
			if(class_id==null||class_id.length()<=0)
			{
				this.getFormHM().put("class_name",class_name);
				this.getFormHM().put("class_id","");
				return;
			}else
			{
				sql=new StringBuffer();
				sql.append("select name from kq_class");
				sql.append(" where class_id='"+class_id+"'");				
				this.frowset=dao.search(sql.toString());				
				if(this.frowset.next())
				{
					class_name=this.frowset.getString("name");
				}
			}
			this.getFormHM().put("class_name",class_name);
			this.getFormHM().put("class_id",class_id);
		
		String flag=(String)this.getFormHM().get("flag");
		this.getFormHM().put("flag",flag);
		
		// 验证时间是否在当前考勤期间之前
		String resultStr = "ok";
		String temp = "";
		if (z1 != null && z1.length() > 0) {
			if (! KqUtilsClass.comparentWithKqDuration(z1)) {
				temp = temp + z1str + "所在考勤期间已封存！";
			}
		}
		
		if (date != null && date.length() > 0) {
			if (! KqUtilsClass.comparentWithKqDuration(date)) {
				temp = temp + "\r\n"+z3str + "所在考勤期间已封存！";
			}
		}
		if (date != null)
        {
		    Date startDate = OperateDate.strToDate(date.replace(".", "-")+" "+"12:00", "yyyy-MM-dd HH:mm");
		    Date endDate = OperateDate.strToDate(date.replace(".", "-")+" "+"12:00", "yyyy-MM-dd HH:mm");
		    String sqlstr = "select * from " + nbase + "a01 where a0100='"+a0100+"'";
		    String a0101 = "";
		    try
		    {
		        this.frowset = dao.search(sqlstr);
		        while (this.frowset.next())
		        {
		            a0101 = this.frowset.getString("a0101");
		        }
		    }
		    catch (SQLException e)
		    {
		        e.printStackTrace();
		    }
		    
		    AnnualApply annualApply = new AnnualApply(this.userView,this.frameconn);
		    if(!annualApply.getKqDataState(nbase,a0100,startDate,endDate))
		    {
		        throw GeneralExceptionHandler.Handle(new GeneralException("",a0101 + "申请的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！","",""));
		    } 
        }
		
		if (temp.length() > 0) {
			resultStr = temp;
		}
		
		this.getFormHM().put("resultStr", SafeCode.encode(resultStr));
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
