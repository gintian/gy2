package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.options.SelfSalaryInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChangeFlagTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap map =((HashMap)(this.getFormHM().get("requestPamaHM")));
		
		String flag = (String)map.get("flag");
		String a0100 = (String)map.get("a0100");
		String empPre = (String)map.get("emppre");
		String prv_flag=(String)this.getFormHM().get("prv_flag");
		if("infoself".equals(prv_flag)){
			a0100=this.userView.getA0100();
			empPre = this.userView.getDbname();
			
		}else{
			CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
			empPre=checkPrivSafeBo.checkDb(empPre);
			a0100=checkPrivSafeBo.checkA0100("", empPre,a0100, "");
		}
		
		
		
		String fieldsetid=(String)this.getFormHM().get("fieldsetid");
		String title=(String)this.getFormHM().get("title"); 
		ArrayList infoList = new ArrayList();
		ArrayList showColumnList = new ArrayList();
		ArrayList columnList = new ArrayList();
		
		String year ="";
		String quarter ="";
		String month = "";
		String startdate = "";
		String enddate="";
		String changeflag="";
		String query_field="";
		if("1".equals(flag)){//年
			year = (String)map.get("year");
			SelfSalaryInfo info = new SelfSalaryInfo(this.getFrameconn(),a0100,empPre,
					"1",year,"","","","",this.userView,fieldsetid,title,prv_flag);
			info.init_Fieldsetid(this.getFrameconn());
			infoList = info.execute();
			showColumnList = info.showColumnList();
			columnList = info.columnList();
			changeflag=info.getChangeflag();
			query_field=info.getQuery_field();
		}else if("2".equals(flag)){//季度
			year = (String)map.get("year");
			quarter = (String)map.get("quarter");
			SelfSalaryInfo info = new SelfSalaryInfo(this.getFrameconn(),a0100,empPre,
					"2",year,"",quarter,"","",this.userView,fieldsetid,title,prv_flag);
			info.init_Fieldsetid(this.getFrameconn());
			infoList = info.execute();
			showColumnList = info.showColumnList();
			columnList = info.columnList();
			changeflag=info.getChangeflag();
			query_field=info.getQuery_field();
		}else if("3".equals(flag)){//月
			year = (String)map.get("year");
			month = (String)map.get("month");
			SelfSalaryInfo info = new SelfSalaryInfo(this.getFrameconn(),a0100,empPre,
					"3",year,month,"","","",this.userView,fieldsetid,title,prv_flag);
			info.init_Fieldsetid(this.getFrameconn());
			infoList = info.execute();
			showColumnList = info.showColumnList();
			columnList = info.columnList();
			changeflag=info.getChangeflag();
			query_field=info.getQuery_field();
		}else if("4".equals(flag)){//区间
			startdate = (String) map.get("startdate");
			enddate = (String) map.get("enddate");
			if(startdate == null || "".equals(startdate)){
				startdate = this.getCurrentDate();
			}
			if(enddate== null || "".equals(enddate)){
				enddate = this.getCurrentDate();
			}
			SelfSalaryInfo info = new SelfSalaryInfo(this.getFrameconn(),a0100,empPre,
					"4","","","",startdate,enddate,this.userView,fieldsetid,title,prv_flag);
			info.init_Fieldsetid(this.getFrameconn());
			infoList = info.execute();
			showColumnList = info.showColumnList(); 
			columnList = info.columnList();
			changeflag=info.getChangeflag();
			query_field=info.getQuery_field();
		}

		this.getFormHM().put("flag",flag);
		this.getFormHM().put("yearflag",year);
		this.getFormHM().put("quarterflag",quarter);
		this.getFormHM().put("monthflag",month);
		this.getFormHM().put("startdateflag",startdate);
		this.getFormHM().put("enddateflag",enddate);
		this.getFormHM().put("query_field",query_field);
		this.getFormHM().put("query_name",getQueryFieldName(query_field));
		this.getFormHM().put("changeflag",changeflag);
		
		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("empPre",empPre);
		this.getFormHM().put("fieldsetid",fieldsetid);
		this.getFormHM().put("columnlist",columnList);
		this.getFormHM().put("showcolumnList",showColumnList);
		this.getFormHM().put("infoList",infoList);
		
	}

	/**
	 * 获得系统当前时间
	 * @return
	 */
	public  String getCurrentDate(){
		Date currentTime = new Date(); 
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		return sdf.format(currentTime); 
	}
	/**
	 * 获得指标名称
	 * @param itemid
	 * @return
	 */
	public String getQueryFieldName(String itemid){
		if(itemid==null||itemid.length()<=0)
			return "";
		String itemdesc = "";
		String sql = "select itemdesc  from fielditem where itemid = '"+itemid.trim()+"'";		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			 RowSet rs = dao.search(sql);
			if (rs.next()) {
				itemdesc = rs.getString("itemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemdesc;
	}
	
}
