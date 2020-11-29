package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.sso.ScheduleJobBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class WorkTypeEditTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		
		String job_id=(String) reqhm.get("job_id");
		reqhm.remove("job_id");
		String trigger="";
		if(job_id!=null){
			RecordVo jobsvo=new RecordVo("t_sys_jobs");
			jobsvo=this.jobsvo(dao,jobsvo,job_id);
			hm.put("jobsvo",jobsvo);
			trigger=jobsvo.getString("trigger_flag");
			this.getFormHM().put("trigger", trigger);
		}else{
			RecordVo jobsvo = (RecordVo)hm.get("jobsvo");
			String status_flag=(String)hm.get("status_flag");
			status_flag=status_flag!=null&&status_flag.length()>0?status_flag:"0";
			trigger=(String)this.getFormHM().get("trigger");
			trigger=trigger.trim().length()==0?"0":trigger;
			jobsvo.setInt("trigger_flag",Integer.parseInt(trigger));
			jobsvo.setInt("status",Integer.parseInt(status_flag));
			//特殊字符还原   jingq upd 2014.10.08
			jobsvo.setString("job_time", PubFunc.keyWord_reback(jobsvo.getString("job_time")));
			jobsvo.setString("job_param", PubFunc.keyWord_reback(jobsvo.getString("job_param")));
			try {
			    String jobId = jobsvo.getString("job_id");
                RecordVo oldJobVo = new RecordVo("t_sys_jobs");
                oldJobVo = this.jobsvo(dao, oldJobVo, jobId);
                
                dao.updateValueObject(jobsvo);
                
                //zxj 20170420 修改了生效状态和执行频率后，自动重置任务，不需要重新启动中间件
                if(oldJobVo == null || !oldJobVo.getString("status").equals(jobsvo.getString("status"))
                    || !oldJobVo.getString("job_time").equals(jobsvo.getString("job_time"))) {
                    ScheduleJobBo scheduleJobBo = new ScheduleJobBo(this.getFrameconn());
                        scheduleJobBo.resetJob(jobId);
                }
			} catch (GeneralException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} 
			hm.put("jobsvo",jobsvo);
		}
		
		
	}
	public RecordVo jobsvo(ContentDAO dao,RecordVo jobsvo,String job_id){
		  /*StringBuffer strsql=new StringBuffer();
          strsql.append("select * from  t_sys_jobs where job_id=");
          strsql.append(job_id);*/
          try{
        	  jobsvo.setString("job_id", job_id);
        	  /*RowSet rs = dao.search(strsql.toString());
	          while(rs.next()){
	        	  jobsvo.setInt("job_id",rs.getInt("job_id"));
	        	  jobsvo.setString("description",rs.getString("description"));
	        	  jobsvo.setString("jobclass",rs.getString("jobclass"));
	        	  jobsvo.setString("job_time",rs.getString("job_time"));
	        	  jobsvo.setInt("trigger_flag",rs.getInt("trigger_flag"));
	        	  jobsvo.setInt("status",rs.getInt("status"));
	          }*/
        	  jobsvo=dao.findByPrimaryKey(jobsvo);
	      }catch(Exception sqle){
			   sqle.printStackTrace();	     
		}
          
		return jobsvo;
	}

}
