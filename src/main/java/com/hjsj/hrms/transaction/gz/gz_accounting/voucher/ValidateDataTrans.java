package com.hjsj.hrms.transaction.gz.gz_accounting.voucher;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;

public class ValidateDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String info="1";
			String type=(String)this.getFormHM().get("type");
			String year=(String)this.getFormHM().get("year");
			String month=(String)this.getFormHM().get("month");
			String count=(String)this.getFormHM().get("count");
			String voucher_date=(String)this.getFormHM().get("voucher_date");
			String deptcode=(String)this.getFormHM().get("deptcode");
			String flag=(String)this.getFormHM().get("flag");
			String voucher_id=(String)this.getFormHM().get("voucher_id");
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sql=new StringBuffer("");
			if("2".equals(type)){
				sql.append("select count(*) from GZ_WarrantData where pn_id="+voucher_id);
				sql.append(" and "+Sql_switcher.year("period")+"="+year);
				sql.append(" and "+Sql_switcher.month("period")+"="+month);
				sql.append(" and unitcode='"+deptcode+"'");
				sql.append(" and status=1");
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next()){
					int num=this.frowset.getInt(1);
					if(num>0)
						info="2";
				}
			}else{
				sql.append("select count(Pz_id) from GZ_WarrantRecord where pn_id='"+voucher_id+"' ");
				sql.append(" and "+Sql_switcher.year("dbill_Date")+"="+year);
				sql.append(" and "+Sql_switcher.month("dbill_Date")+"="+month);
				
				String temps="";
				if("1".equals(flag)|| "3".equals(flag)) //当前选中机构
				{
					temps=",'"+deptcode+"'";
				}
				
				if("2".equals(flag)|| "3".equals(flag)) //下一级机构
				{
					Calendar d=Calendar.getInstance();
					int today=d.get(Calendar.YEAR)*10000+(d.get(Calendar.MONTH)+1)*100+d.get(Calendar.DATE);
					String _sql="select * from organization where ( codesetid='UN' or  codesetid='UM' ) and parentid='"+deptcode+"'";
					_sql+=" and "+Sql_switcher.year("start_date")+"*10000+"+Sql_switcher.month("start_date")+"*100+"+Sql_switcher.day("start_date")+"<="+today;
					_sql+=" and "+Sql_switcher.year("end_date")+"*10000+"+Sql_switcher.month("end_date")+"*100+"+Sql_switcher.day("end_date")+">="+today;
					this.frowset=dao.search(_sql);
					while(this.frowset.next())
					{
						temps+=",'"+this.frowset.getString("codeitemid")+"'";
					}
					
				}
				
				
				if("all".equalsIgnoreCase(count))
					sql.append(" and dbill_times=0 and DeptCode in ("+temps.substring(1)+")");
				else
					sql.append(" and dbill_times="+count+" and  DeptCode in ("+temps.substring(1)+")");
				
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					if(this.frowset.getInt(1)>0)
						info="2";  //已为相关单位生成过计提月份的凭证数据
				}
			}
			this.getFormHM().put("type", type);
			this.getFormHM().put("year", year);
			this.getFormHM().put("month", month);
			this.getFormHM().put("count", count);
			this.getFormHM().put("voucher_date", voucher_date);
			this.getFormHM().put("deptcode", deptcode);
			this.getFormHM().put("info",info);
			this.getFormHM().put("flag",flag);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	} 

}
