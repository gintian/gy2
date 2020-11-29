package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ValidateAppealUnitsTrans extends IBusiness{
	public void execute() throws GeneralException{
		String unitcode=(String)this.getFormHM().get("unitcode");
		String tabid=(String)this.getFormHM().get("tabid");
		
		//add by wangchaoqun on 2014-10-8 
		if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		
		StringBuffer sql=new StringBuffer();
		sql.append("select * from tt_organization tt left join treport_ctrl tr on tt.unitcode=tr.unitcode where  tt.parentid='");
		sql.append(unitcode);
		sql.append("' and tr.tabid='");
		sql.append(tabid);
		sql.append("'and tt.parentid<>tt.unitcode");
		StringBuffer ext_sql = new StringBuffer();
		ArrayList tabids=new ArrayList();
		tabids.add(tabid);
		HashMap sort=new HashMap();
		ReportCollectBo reportCollectBo=new ReportCollectBo(this.getFrameconn());
		String sortid="";
		sort=reportCollectBo.getSortid(tabids);
		if(sort!=null)
			sortid=(String)sort.get(tabid);
		Calendar d=Calendar.getInstance();
		int yy=d.get(Calendar.YEAR);
		int mm=d.get(Calendar.MONTH)+1;
		int dd=d.get(Calendar.DATE);
		ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
		ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
		ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
		ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
		ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
		ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 
		ext_sql.append(" and tt.reporttypes like '%");
		ext_sql.append(sortid);
		ext_sql.append("%' and( tt.report not like '");
		ext_sql.append(tabid);
		ext_sql.append(",%' or tt.report is null )");
		sql.append(ext_sql.toString());
		String subunitcodes="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next()){
				String status=this.frowset.getString("status");
				String subunitdcode=this.frowset.getString("unitcode");
				if(!"1".equals(status)){
					subunitcodes+=subunitdcode+",";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("subunitcodes", subunitcodes);
		this.getFormHM().put("tabid", tabid);
	}
} 
