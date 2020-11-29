package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 删除考核对象
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 28, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class DelObjTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		try
		{
			String[] objectIDs=(String[])this.getFormHM().get("objectIDs");
			String planid=(String)this.getFormHM().get("planid");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer whl=new StringBuffer("");
			StringBuffer str = new StringBuffer();
			for(int i=0;i<objectIDs.length;i++)
			{
				if(objectIDs[i].trim().length()>0){
					whl.append(",'"+objectIDs[i]+"'");
					str.append(",'Usr"+objectIDs[i]+"'");
				}
			}
			if(whl.length()>0)
			{
				dao.delete("delete from per_object where plan_id="+planid+" and  object_id in ("+whl.substring(1)+")",new ArrayList());
				dao.delete("delete from per_mainbody where plan_id="+planid+" and object_id in ("+whl.substring(1)+")",new ArrayList());
				DbWizard dbWizard=new DbWizard(this.getFrameconn());
				if(dbWizard.isExistTable("per_pointpriv_"+planid,false))
					dao.delete("delete from per_pointpriv_"+planid+" where object_id in ("+whl.substring(1)+") ",new ArrayList());
				if(dbWizard.isExistTable("per_table_"+planid,false))
					dao.delete("delete from per_table_"+planid+" where object_id in ("+whl.substring(1)+")  ",new ArrayList());
				if(dbWizard.isExistTable("per_result_"+planid,false))
					dao.delete("delete from per_result_"+planid+" where object_id in ("+whl.substring(1)+")  ",new ArrayList());
				if(dbWizard.isExistTable("per_gather_score_"+planid,false))//业绩数据录入里用到的表
					dao.delete("delete from per_gather_score_"+planid+" where gather_id in (select gather_id  from per_gather_"+planid+" where object_id in ("+whl.substring(1)+")) ",new ArrayList());
				if(dbWizard.isExistTable("per_gather_"+planid,false))
					dao.delete("delete from per_gather_"+planid+" where  object_id in ("+whl.substring(1)+") ",new ArrayList());
				
				PendingTask pt = new PendingTask();
				RowSet rs=dao.search("select * from t_hr_pendingtask where  receiver in ("+str.substring(1)+") and ext_flag like 'PERZD_"+planid+"%' and pending_status<>'1' and pending_type='33'");
				while(rs.next()){					
					pt.updatePending("P", "PER"+rs.getString("pending_id"), 100, "对象删除", this.userView);
				}
				dao.delete("delete from t_hr_pendingtask where receiver in ("+str.substring(1)+") and ext_flag like 'PERZD_"+planid+"%' and pending_status<>'1' and pending_type='33'", new ArrayList());//当删除考核对象的时候，把对应的制定状态下的待办删除  zhaoxg add 20104-11-3
				
				
				 dao.delete("DELETE FROM per_interview WHERE plan_id = "+planid+" AND NOT (object_id IN (SELECT object_id FROM per_object WHERE plan_id = "+planid+"))" ,new ArrayList());
				
				/**JinChunhai  add*/
				dao.delete("DELETE FROM per_target_evaluation WHERE plan_id = "+planid+" AND NOT (object_id IN (SELECT object_id FROM per_object WHERE plan_id = "+planid+"))" ,new ArrayList());
				dao.delete("DELETE FROM per_article WHERE plan_id = "+planid+" AND NOT (A0100  IN (SELECT object_id FROM per_object WHERE plan_id = "+planid+"))" ,new ArrayList());
				if("2".equals(getPerPlanVo(planid).getString("method")))//目标管理计划
				{
					if(getPerPlanVo(planid).getInt("object_type")==2)					
						dao.delete("DELETE FROM P04 WHERE plan_id = "+planid+" AND NOT (A0100 IN (SELECT object_id FROM per_object WHERE plan_id = "+planid+"))" ,new ArrayList());
					else						
						dao.delete("DELETE FROM P04 WHERE plan_id = "+planid+" AND NOT (B0110 IN (SELECT object_id FROM per_object WHERE plan_id = "+planid+"))" ,new ArrayList());
				   
				    dao.delete("DELETE FROM PER_ITEMPRIV_"+planid+" where object_id not in (SELECT object_id FROM per_object WHERE plan_id = "+planid+")", new ArrayList());
				}	
				//删除动态项目权重表的数据
				String sql = "DELETE FROM  per_dyna_item where plan_id="+planid+" and body_id not in (select body_id from per_object where plan_id = "+planid+")";
				dao.delete(sql, new ArrayList());
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}	
	public RecordVo getPerPlanVo(String planid)
	{

		RecordVo vo = new RecordVo("per_plan");
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			vo.setInt("plan_id", Integer.parseInt(planid));
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	} 
}
