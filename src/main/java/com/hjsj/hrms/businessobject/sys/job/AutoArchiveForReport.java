package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.report_pigeonhole.ReportPigeonholeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

public class AutoArchiveForReport implements Job{
	@Override
    public void execute(JobExecutionContext context)throws JobExecutionException {
		Connection conn=null;
		RowSet rs =null;
		boolean flag=false;
		try {
			StringBuffer buf=new StringBuffer();
			buf.append("select job_id,description,jobclass,job_time,trigger_flag,status from t_sys_jobs  order by job_id");
			conn=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(buf.toString());
			while(rs.next()){
				String jobclass=rs.getString("jobclass");
				if(jobclass.indexOf(this.getClass().getName())!=-1){
					String status=rs.getString("status");
					if("1".equalsIgnoreCase(status)){
						flag=true;
					}
					break;
				}
			}
			if(flag){
				Calendar  ca = Calendar.getInstance();
				
				buf.setLength(0);
				buf.append("select * from tname where xmlstyle is not null");
				rs=dao.search(buf.toString());
				TimeZone zone=TimeZone.getTimeZone("Asia/Shanghai");
				Calendar cal = Calendar.getInstance(zone);
				//int c = cal.get(cal.WEEK_OF_YEAR);//本年的第几周 这里用不到
				cal.setFirstDayOfWeek(Calendar.MONDAY);
				int week=cal.get(cal.DAY_OF_WEEK_IN_MONTH); //本月的 第几周
				int year=cal.get(cal.YEAR);
				int month=cal.get(cal.MONTH);
				
				while(rs.next()){
					String xml=Sql_switcher.readMemo(rs, "xmlstyle");
					String tabid=rs.getString("tabid");
					String narch=rs.getString("narch");
					if(xml.trim().length()!=0){
						Document doc=null;
						doc=PubFunc.generateDom(xml);
						if(doc!=null){
							XPath xPath = XPath.newInstance("/param/auto_archive");
							Element auto_archiv = (Element) xPath.selectSingleNode(doc);
							Element root = doc.getRootElement();
							if (auto_archiv != null) {
								String auto=auto_archiv.getText();
								if(auto!=null&&auto.trim().length()!=0&& "1".equals(auto)){
									TnameBo tnameBo=new TnameBo(conn,tabid,"","","");
									tnameBo.anaylseReportStruct(tabid);
									Category.getInstance(this.getClass()).debug("auto archive begin");
									ReportPigeonholeBo bo=new ReportPigeonholeBo(conn);
									String info=bo.auto_archive(tabid, narch,String.valueOf(week) , String.valueOf(month), String.valueOf(year));
									if("1".equalsIgnoreCase(info)) {
										Category.getInstance(this.getClass()).debug("auto archive secuess");
									} else{
										Category.getInstance(this.getClass()).debug("auto archive failure");
									}
									
								}
							}else{
								continue;
							}
						}else{
							continue;
						}
					}else{
						continue;
					}
					
				}
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null) {
					rs.close();
				}
				if(conn!=null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
