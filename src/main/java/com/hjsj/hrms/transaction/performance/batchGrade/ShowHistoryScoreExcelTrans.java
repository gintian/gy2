package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;

public class ShowHistoryScoreExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		String planid = (String)this.getFormHM().get("plan_id");
		LoadXml loadxml = new LoadXml(this.getFrameconn(), planid);
		Hashtable params = loadxml.getDegreeWhole();
		String KeepDecimal = (String) params.get("KeepDecimal");
		double decimal = Double.parseDouble(KeepDecimal);
		String timeFw = (String)this.getFormHM().get("timeInterval");
		String startTime = (String)this.getFormHM().get("startDate");
		String endTime = (String)this.getFormHM().get("endDate");
		ArrayList planNames = new ArrayList();
		ArrayList objectNames = new ArrayList();
		ArrayList planList = getPlanids(planid,timeFw,startTime,endTime,planNames);
		HashMap dataMap = new HashMap();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList objectAvg = new ArrayList();
		ArrayList objectTotal = new ArrayList();
		/* 得到某计划某人的考评对象集合 */
		ArrayList objectList = this.getObjectList(this.getFrameconn(),
				this.userView, planid,objectNames);
		
		if(planList!=null && !"".equals(planList)){
			for(int i=0;i<planList.size();i++){
				StringBuffer strsql = new StringBuffer("");
				ArrayList dataList = new ArrayList();
				for(int j=0;j<objectList.size();j++) {
					double score = 0;
					strsql.setLength(0);
					strsql.append(" select score from per_result_"+planList.get(i)+" where object_id='"+objectList.get(j)+"' ");
					try {
						this.frowset = dao.search(strsql.toString());
						while(this.frowset.next()){
							score = frowset.getDouble("score");
							score = score*Math.pow(10,decimal);
							score = Math.round(score)/Math.pow(10,decimal);
						}
						dataList.add(zeroToNull(score));
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
				dataMap.put(i+"", dataList);
			}
		}
		for(int i=0;i<objectList.size();i++){
			double avg = 0;
			int n = 0;
			for(int j=0;j<planList.size();j++){
				if(!"".equals((String)((ArrayList)dataMap.get(j+"")).get(i))){
					avg += Double.parseDouble(( (String)((ArrayList)dataMap.get(j+"")).get(i)) );
					avg = Math.round(avg*Math.pow(10,decimal))/Math.pow(10,decimal);
				}
				
				else
					n++;
			}
			objectTotal.add(zeroToNull(avg));
			if(planList.size()>n){
				avg = avg/(planList.size()-n);
				avg = Math.round(avg*10)/10.0;
			}
			objectAvg.add(zeroToNull(avg));
		}
		ArrayList column = new ArrayList();//中文字段名
    	ArrayList infolist = new ArrayList();//各列数值
    	ArrayList columnlist = new ArrayList();//列名
    	
    	column.add("考核对象");
    	for(int i=0;i<planNames.size();i++)
    		column.add(planNames.get(i));
    	column.add("合计得分");
    	column.add("平均得分");
    	
    	for(int j=0;j<objectNames.size();j++){
    		LazyDynaBean bean = new LazyDynaBean();
    		bean.set("objectName", objectNames.get(j));
    		for(int k=0;k<planNames.size();k++){
    			String score = "";
	  			score = ( (String)((ArrayList)dataMap.get(k+"")).get(j)) ;
    			bean.set("planNames_"+k, score);//"planNames_k"--->"planNames_"+k  zhaoxg  2014-4-3 历史问题
    		}
    		bean.set("totalscore", objectTotal.get(j));
    		bean.set("avgscore", objectAvg.get(j));
    		infolist.add(bean);
    	}
    	
    	columnlist.add("objectName");
    	for(int k=0;k<planNames.size();k++){
    		columnlist.add("planNames_"+k);//"planNames_k"--->"planNames_"+k  zhaoxg  2014-4-3 历史问题
		}
    	columnlist.add("totalscore");
    	columnlist.add("avgscore");
    	
    	ShowExcel excel = new ShowExcel();
    	String filename = excel.creatExcel(column,infolist,columnlist);
    	filename = PubFunc.encrypt(filename);
    	//20/3/6 xus vfs改造
//		filename = SafeCode.encode(filename);
		this.getFormHM().put("excelfile",filename);
	}
	public String zeroToNull(double num){
		if(num==0)
			return "";
		else
			return num+"";
	}
	public ArrayList getObjectList(Connection conn, UserView userView,
			String planid,ArrayList objectNames) {
		ArrayList objectList = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer str = new StringBuffer("");
		str.append(" select pm.object_id,po.a0101 from per_mainbody pm,per_object po,per_plan pp where pm.object_id=po.object_id and pm.plan_id=po.plan_id and pm.plan_id=pp.plan_id and pp.plan_id="
				+ planid + " and pm.mainbody_id=" + userView.getA0100() + " ");
		ResultSet rs = null;
		try {
			rs = dao.search(str.toString());
			while (rs.next()) {
				objectList.add(rs.getString("object_id"));
				objectNames.add(rs.getString("a0101"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return objectList;
	}
	//取得所有满足要求的计划号
	private ArrayList getPlanids(String planid,String timeFw,String startTime,String endTime,ArrayList planNames){
		ArrayList planids = new ArrayList();
		StringBuffer whlSql = new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if(timeFw == null || "".equals(timeFw)) {
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				whlSql.append(" select plan_id,name from per_plan where status=7 and template_id=");
			else
				whlSql.append(" select top 2 plan_id,name from per_plan where status=7 and template_id=");
		} else {
			whlSql.append(" select plan_id,name from per_plan where status=7 and template_id=");
		}
		whlSql.append(" (select template_id from per_plan where plan_id="+planid+") ");
		// 时间范围查询
		if (timeFw != null && !"".equals(timeFw))
		{
		    if ("1".equals(timeFw))
		    	whlSql.append("and (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+" OR ( "+Sql_switcher.year("start_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.year("end_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+")) ");
		    if ("2".equals(timeFw))
		    {  
				int jd = this.getCurrentJD();
				switch (jd)
				{
					case 1:
					    whlSql.append("and (( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND (thequarter = '01') ) OR ( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND themonth IN ('01', '02', '03') ) OR ( "+Sql_switcher.year("start_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.year("end_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.month("start_date")+">=1 AND "+Sql_switcher.month("end_date")+"<=3))  ");
					    break;
					case 2:
					    whlSql.append("and (( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND (thequarter = '02') ) OR ( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND themonth IN ('04', '05', '06') ) OR ( "+Sql_switcher.year("start_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.year("end_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.month("start_date")+">=4 AND "+Sql_switcher.month("end_date")+"<=6))  ");
					    break;
					case 3:
					    whlSql.append("and (( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND (thequarter = '03') ) OR ( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND themonth IN ('07', '08', '09') ) OR ( "+Sql_switcher.year("start_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.year("end_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.month("start_date")+">=7 AND "+Sql_switcher.month("end_date")+"<=9))  ");
					    break;
					default:
					    whlSql.append("and (( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND (thequarter = '04') ) OR ( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND themonth IN ('10', '11', '12') ) OR ( "+Sql_switcher.year("start_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.year("end_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.month("start_date")+">=10 AND "+Sql_switcher.month("end_date")+"<=12))  ");
				}
		    }
		    if("3".equals(timeFw))
		    {
		    	Calendar calendar = Calendar.getInstance();
				int month = calendar.get(Calendar.MONTH);
				month = month + 1;
		    	whlSql.append("and (((theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND (themonth = '"+month+"') )  ) ");//OR (  "+Sql_switcher.month("start_date")+"="+Sql_switcher.month(Sql_switcher.sqlNow())+" AND "+Sql_switcher.month("end_date")+"="+Sql_switcher.month(Sql_switcher.sqlNow())+")   任星让改  bug437
		    }
		    if ("4".equals(timeFw))//如果是不定期计划，需满足考核周期前后至少有一个时间是在查询范围内,考核周期开始日期<查询结束日期；并且考核周期结束日期>查询开始日期
		    {
				if(startTime!=null && !"".equals(startTime))
				{
				    StringBuffer buf = new StringBuffer();
				    buf.append(Sql_switcher.year("start_date")+ "<"+ getDatePart(endTime,"y") +" or ");
				    buf.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(endTime,"y")+" and ");
				    buf.append(Sql_switcher.month("start_date")+ "<"+ getDatePart(endTime,"m") +") or ");
				    buf.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(endTime,"y")+" and ");
				    buf.append(Sql_switcher.month("start_date")+ "="+ getDatePart(endTime,"m") +" and ");
				    buf.append(Sql_switcher.day("start_date")+ "<="+ getDatePart(endTime,"d") +")");
				    whlSql.append(" and ("+buf.toString()+") ");
				}		   
				if(endTime!=null && !"".equals(endTime))
				{
				    StringBuffer buf = new StringBuffer();
				    buf.append(Sql_switcher.year("end_date")+ ">"+ getDatePart(startTime,"y") +" or ");
				    buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(startTime,"y")+" and ");
				    buf.append(Sql_switcher.month("end_date")+ ">"+ getDatePart(startTime,"m") +") or ");
				    buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(startTime,"y")+" and ");
				    buf.append(Sql_switcher.month("end_date")+ "="+ getDatePart(startTime,"m") +" and ");
				    buf.append(Sql_switcher.day("end_date")+ ">="+ getDatePart(startTime,"d") +")");
				    whlSql.append(" and ("+buf.toString()+") ");
				}		    		
		    }
		} else {
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				whlSql.append(" and rownum<=2 order by plan_id desc ");
			else
				whlSql.append(" order by plan_id desc ");
		}
		try {
			this.frowset = dao.search(whlSql.toString());
			while(frowset.next()){
				planids.add(this.frowset.getString("plan_id"));
				planNames.add(this.frowset.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return planids;
	}
	
	/**
	 * 取得当前的季度
	 * 
	 * @return
	 */
	public int getCurrentJD()
	{
		int jd = 1;
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		month = month + 1;
		switch (month)
		{
			case 1:
			case 2:
			case 3:
				jd = 1;
				break;
			case 4:
			case 5:
			case 6:
				jd = 2;
				break;
			case 7:
			case 8:
			case 9:
				jd = 3;
				break;
			default:
				jd = 4;
		}
		return jd;
	}
	
	public String getDatePart(String mydate, String datepart)
	{
		String str = "";
		if ("y".equalsIgnoreCase(datepart))
			str = mydate.substring(0, 4);
		else if ("m".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(5, 6)))
				str = mydate.substring(6, 7);
			else
				str = mydate.substring(5, 7);
		} else if ("d".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(8, 9)))
				str = mydate.substring(9, 10);
			else
				str = mydate.substring(8, 10);
		}
		return str;
	}
}
