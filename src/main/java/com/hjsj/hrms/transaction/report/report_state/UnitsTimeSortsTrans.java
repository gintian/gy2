package com.hjsj.hrms.transaction.report.report_state;

import com.hjsj.hrms.businessobject.report.TgridBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.AnalyseParams;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class UnitsTimeSortsTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String unitcode=(String)hm.get("unitcode");
		String userName=this.userView.getUserName();//dml 2011-04-11
		String reporttypes="";//用户负责报表类型
		String[] reporttype=null;
		ArrayList list=null;
		ArrayList reporttypeList=new ArrayList();
		StringBuffer sql=new StringBuffer();
		//从operuser和tt——organiza表中联合查找出当前用户负责表类
		sql.append("select reporttypes from tt_organization where unitcode=(select unitcode from operuser where UserName='");
		sql.append(userName);
		sql.append("')");
		StringBuffer ext_sql = new StringBuffer();
		Calendar d=Calendar.getInstance();
		int yy=d.get(Calendar.YEAR);
		int mm=d.get(Calendar.MONTH)+1;
		int dd=d.get(Calendar.DATE);
		//要查找有效日期内的
		ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
		ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
		ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
		ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
		ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
		ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 
		sql.append(ext_sql);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next()){
				reporttypes=Sql_switcher.readMemo(this.frowset, "reporttypes");
			}
			if(reporttypes!=null&&reporttypes.length()!=0){
				reporttype=reporttypes.split(",");
			}
			TgridBo tbo=new  TgridBo(this.getFrameconn());
			if(reporttype!=null){
			for(int i=0;i<reporttype.length;i++){
				boolean flag=false;
				list=this.getTabidList(reporttype[i]);
				ArrayList list1=(ArrayList)list.get(0);
				LazyDynaBean bean=(LazyDynaBean)list.get(1);
				if(tbo.isSetOwnerDate(list1)){
					flag=true;
				}
				if(flag){
					bean.set("hasTime", "true");
					String time=this.getTime(reporttype[i]);
					bean.set("time", time);
				}else{
					continue;
				}
				reporttypeList.add(bean);
			}
			}
			this.getFormHM().put("reporttypelist", reporttypeList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList getTabidList(String tabType){
		ArrayList tablist=new ArrayList();
		ArrayList list1=new ArrayList();
		StringBuffer sql=new StringBuffer();
		LazyDynaBean bean=new LazyDynaBean();
		if(tabType!=null&&tabType.length()!=0){
			sql.append("select tabid from tname where tsortId='");
			sql.append(tabType);
			sql.append("'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try {
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next()){
					list1.add(this.frowset.getString("tabid"));
				}
				sql.setLength(0);
				sql.append("select * from tsort where TsortId='");
				sql.append(tabType);
				sql.append("'");
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next()){
					bean.set("name",this.frowset.getString("Name"));
					bean.set("tsortid", tabType);
					bean.set("fontname", this.frowset.getString("sDes"));
				}
				tablist.add(list1);
				tablist.add(bean);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tablist;
	}
	public String getTime(String type){
		String time="";
		
		try {
			AnalyseParams anp=new AnalyseParams(this.getFrameconn());
			if(anp.checkBelongdateSortid(type)){
				HashMap hm=(HashMap)anp.getAttributeSortidValues(type);
				time=(String)hm.get(type);
			}
			
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return time;
	}
}
