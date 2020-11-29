package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.org.autostatic.confset.DataCondBo;
import com.hjsj.hrms.businessobject.org.autostatic.confset.DataSynchroBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Category;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
/**
 * 数据联动自动计算后台作业
 * @author xuj
 * 2013-7-20
 */
public class DataLinkageJob implements Job {
	private Category log = Category.getInstance(DataLinkageJob.class.getName());
	@Override
    public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String password = "";
			rs = dao.search("select Password from operuser where UserName='su'");
			String check="0";
			if(rs.next()){
				password = rs.getString("Password");
				password=password!=null?password:"";
				check="1";
			}
			UserView uv = new UserView("su",password,conn);
			uv.canLogin();
			if(uv!=null&& "1".equals(check))
			{
				//参与计算的人员库
				PosparameXML pos = new PosparameXML(conn);
				String view_scan = pos.getNodeAttributeValue("/params/view_scan","datascan"); 
				view_scan=view_scan!=null&&view_scan.trim().length()>1?view_scan:"Usr,";
				// 作业类id
				String jobId = context.getJobDetail().getName();
				RecordVo jobsvo=new RecordVo("t_sys_jobs");
				jobsvo.setString("job_id", jobId);
				jobsvo=dao.findByPrimaryKey(jobsvo);
				if(jobsvo!=null){
					//参与后台作业自动计算的年月变化子集
					String job_param = jobsvo.getString("job_param");
					log.info("job_param:"+job_param);
					if(job_param.length()>0){
						String[] setids = job_param.split(",");
						Calendar cdate = Calendar.getInstance();
						String getyear=cdate.get(Calendar.YEAR)+"";
						String getmonth=(cdate.get(Calendar.MONTH) + 1)+"";
						for(int i=0;i<setids.length;i++){
							String set = setids[i];
							if(set.length()==3){
								FieldSet fs = DataDictionary.getFieldSetVo(set);
								if(fs==null) {
									return;
								}
								String changeflag = fs.getChangeflag();
								if(!("1".equals(changeflag)||"2".equals(changeflag))) {
									return;
								}
								if("2".equals(changeflag)) {
									getmonth="0";
								}
								//引入上期非计算型指标数据
								String sqlstr = "select itemid,itemdesc,expression from fielditem where fieldsetid='"+set+"' and useflag=1";
								ArrayList dylist = dao.searchDynaList(sqlstr);
								StringBuffer fieldsb=new StringBuffer();
								for(Iterator it=dylist.iterator();it.hasNext();){
									DynaBean dynabean=(DynaBean)it.next();
									
									String expr = (String)dynabean.get("expression");
									expr=expr!=null&&expr.trim().length()>0?expr:"";
									
									String itemid = (String)dynabean.get("itemid");
									String itemdesc = (String)dynabean.get("itemdesc");
									
									if(expr.length()<2&&!itemdesc.equals(ResourceFactory.getProperty("hmuster.label.nybs"))
											&&!itemdesc.equals(ResourceFactory.getProperty("hmuster.label.counts"))){
										fieldsb.append(","+itemid);
									}
									if("K".equalsIgnoreCase(set.substring(0,1))&&expr.trim().length()>1
											&& "3".equals(expr.substring(0,1))){
										fieldsb.append(","+itemid);
									}
								}
								String fieldstr="";
								if(fieldsb.length()>1) {
									fieldstr=fieldsb.substring(1);
								}
								log.info("set:"+set+" view_scan:"+view_scan+" getyear:"+getyear+" getmonth:"+getmonth+" fieldstr:"+fieldstr);	
								if(fieldstr.length()>1){
									DataSynchroBo dsbo = new DataSynchroBo(uv, set, dao, view_scan.substring(0,view_scan.length()-1), getyear, getmonth, changeflag); 
									int num=0;
									if(fieldstr.trim().length()>0){
										num = dsbo.loadPrevData(fieldstr, getyear, getmonth, changeflag);
									}
									DataCondBo databo = new DataCondBo(uv,conn,set,
											view_scan,getyear,getmonth,changeflag);
									databo.runCond("1","UN");
									int num2=0;
									if(fieldstr.trim().length()>0){
										num2 = dsbo.loadPrevData(fieldstr, getyear, getmonth, changeflag);
									}
									if(num2!=num){
										databo.runCond("1","UN");
									}
								}else{
									DataCondBo databo = new DataCondBo(uv,conn,set,
											view_scan,getyear,getmonth,changeflag);
									databo.runCond("1","UN");
								}
							}
							
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
