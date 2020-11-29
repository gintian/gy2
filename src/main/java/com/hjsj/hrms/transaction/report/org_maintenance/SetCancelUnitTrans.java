package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;

public class SetCancelUnitTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String end_date=(String)this.getFormHM().get("end_date");
			String goal_unit=(String)this.getFormHM().get("goal_unit");
			String unit=(String)this.getFormHM().get("unit");
			String transfer_date = (String)this.getFormHM().get("transfer_date");
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(goal_unit!=null&&goal_unit.trim().length()>0)
			{
				this.frowset=dao.search("select * from tt_organization where parentid='"+goal_unit+"' and unitcode<>parentid");
				if(this.frowset.next())
					throw GeneralExceptionHandler.Handle(new Exception("人员移动目标机构必须为基层单位!"));
			}
			ArrayList list=new ArrayList();
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			this.frowset=dao.search("select * from tt_organization where unitcode like '"+unit+"%' "+ext_sql+"");
			while(this.frowset.next())
			{
				String _unit=this.frowset.getString("unitcode");
				RecordVo vo=new RecordVo("tt_organization");
				vo.setString("unitcode",_unit);
				vo.setDate("end_date",end_date);
				vo.setString("d_unitcode",goal_unit);
				dao.updateValueObject(vo);
				list.add(_unit);
			}	
			
			
			
			
			if(goal_unit!=null&&goal_unit.trim().length()>0)
			{
				DbWizard dbWizard=new DbWizard(this.getFrameconn());
				if(dbWizard.isExistTable("tt_cycle",false))
				{
					//划转单位
					if(transfer_date!=null&&transfer_date.trim().length()>0&&"1".equals(transfer_date.trim())){
						ActuarialReportBo arbo=new ActuarialReportBo(this.getFrameconn(),this.userView);
						arbo.autoTransferCompute(unit,goal_unit);
					}else{//撤销单位
					this.frowset=dao.search("select id from tt_cycle where status='04' ");
					if(this.frowset.next())
					{
						for(int i=0;i<list.size();i++)
						{
							String _unit=(String)list.get(i);
						
							ActuarialReportBo arbo=new ActuarialReportBo(this.getFrameconn(),this.userView);
							arbo.autoCompute(_unit,goal_unit,this.frowset.getString("id"));
						}
					}
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
