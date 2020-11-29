package com.hjsj.hrms.transaction.report.edit_report.send_receive_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class send_receive_view extends IBusiness {

	public send_receive_view() {
		super();
	}

	public void execute() throws GeneralException {
		try {
			ArrayList list = new ArrayList();
			UserView userView = getUserView();
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String dxt = (String)hm.get("returnvalue");
			if(dxt!=null&&!"dxt".equals(dxt))
				hm.remove("returnvalue");
			if(dxt==null)
				dxt="";
			ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer 	strsql= new StringBuffer();
		String reportTypes="";
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
		strsql.append("select reporttypes,report,unitcode from tt_organization where unitcode = (select unitcode from operuser where username = '");
		strsql.append(this.userView.getUserName());
		strsql.append("') "+ext_sql+"");		
			this.frowset = dao.search(strsql.toString());	
			if(this.frowset.next())
				reportTypes = (String) this.frowset.getString("reporttypes");
			if(StringUtils.isEmpty(reportTypes))//liuy 2015-1-16 6763:没有给业务用户划分表类，业务用户点击报表汇总/表式收发：报错 
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info11")+"！"));
			if (reportTypes.length()>0&&reportTypes.charAt(reportTypes.length() - 1) == ',') {
				reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
			}
			String sql = "select name,tabid from tname";
			String tabid = "";
			TnameBo tnamebo  = new TnameBo(this.getFrameconn());
			HashMap scopeMap = tnamebo.getScopeMap();
			java.util.Iterator it = scopeMap.entrySet().iterator();
			String tabids = "";
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String keys = (String) entry.getKey();
				tabids+= keys+",";
				
			}
			if(tabids.length()>0)
				tabids=tabids.substring(0,tabids.length()-1);
			sql=sql+" where 1=1 ";
			if(tabids.length()>0)
			sql=sql+" and tabid not in("+tabids+")";
			if(reportTypes.length()>0)
				sql=sql+" and tsortid in("+reportTypes+") order by tabid ";
			String name = "";
			frowset = dao.search(sql.toString());
			while (this.frowset.next()) {
				tabid = frowset.getString("tabid");
				name = frowset.getString("name");

				if (userView.isHaveResource(//是否有权限
						com.hrms.hjsj.sys.IResourceConstant.REPORT, tabid)) {
					CommonData cd = new CommonData();
					cd.setDataName("("+tabid+") "+name);
					cd.setDataValue(tabid);
					list.add(cd);
				}

			}
			this.getFormHM().put("list", list);
			this.getFormHM().put("returnflag", dxt);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}

	}

}
