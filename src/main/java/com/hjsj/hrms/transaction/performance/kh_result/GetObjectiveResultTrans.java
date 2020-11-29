package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.interview.PerformanceInterviewBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Hashtable;

public class GetObjectiveResultTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String plan_id = PubFunc.decrypt((String)this.getFormHM().get("planid"));
			String object_id = PubFunc.decrypt((String)this.getFormHM().get("object_id"));
			
			String body=(String)this.getFormHM().get("body");
			String oper=(String)this.getFormHM().get("oper");
			String type=(String)this.getFormHM().get("from_flag");
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),plan_id,object_id,this.getUserView(),2,"5");
			if(type!=null&& "1".equals(type))
			{
				bo.setFrom_flag("1");  //绩效评估的绩效面谈
			}else{
				bo.setFrom_flag("0");//考核结果
			}
			String html=bo.getObjectCardHtml();
			String tabids = bo.getTabids(plan_id);
			this.getFormHM().put("isCard", "".equals(tabids.trim())?"0":"1");
			PerformanceInterviewBo ab = new PerformanceInterviewBo(this.getFrameconn());
			ArrayList tabList = ab.getTabids(plan_id);
			this.getFormHM().put("tabList", tabList);
			this.getFormHM().put("tabIDs", tabids);
			this.getFormHM().put("cardHtml",html);
			this.getFormHM().put("body", body);
			this.getFormHM().put("oper",oper);
			this.getFormHM().put("planid",(String)this.getFormHM().get("planid"));
			this.getFormHM().put("object_id", (String)this.getFormHM().get("object_id"));
			this.getFormHM().put("drawId","5");
			AnalysePlanParameterBo abo=new AnalysePlanParameterBo(this.getFrameconn());
			Hashtable ht_table=abo.analyseParameterXml();
			String templet_id="";
			String flag="1";  //1:传统绩效面谈 2：调用模版面谈(操作人：考核主体)  3：调用模版面谈(操作人：考核对象)
			String opt="1";  //0:只读  1：可编辑
			String ins_id="";
			String task_id="";
			String alertMessage="0";
			ArrayList recordsList=new ArrayList();
			if(ht_table!=null)
			{
				if(ht_table.get("interview_template")!=null)
					templet_id=(String)ht_table.get("interview_template");
			}
			if(templet_id.length()>0&&!"-1".equalsIgnoreCase(templet_id))
			{
				RecordVo vo=new RecordVo("Template_table");
				vo.setInt("tabid",Integer.parseInt(templet_id));
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				if(dao.isExistRecordVo(vo))
				{
					vo=dao.findByPrimaryKey(vo);
					String sxml=vo.getString("sp_flag");
					if(sxml==null|| "".equals(sxml)|| "0".equals(sxml))
						alertMessage="0";
					else
						alertMessage="面谈不支持审批方式的模板，请指定非审批方式的模板！";
				}
				else
				{
					alertMessage="面谈模板不存在！";
				}
				if(oper!=null&& "1".equals(oper))  //主体
				{
					flag="2";
					ins_id= getInterviewInsID(plan_id,object_id);
					if(!"0".equals(ins_id))
					{
						opt="0";
						task_id=getTaskIdByIns(ins_id);
					}
				}
				else                  //对象 
				{
					flag="3";
					opt="0";
					recordsList=getRecordsList(plan_id,object_id);
				}
			}
			else
				flag="1";
			this.getFormHM().put("flag", flag);
			this.getFormHM().put("templet_id", templet_id);
			this.getFormHM().put("ins_id", ins_id);
			this.getFormHM().put("task_id",task_id);
			this.getFormHM().put("opt", opt);
			this.getFormHM().put("recordsList", recordsList);
			this.getFormHM().put("alertMessage", alertMessage);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		

	}
	
	
	
	public ArrayList getRecordsList(String plan_id,String object_id)
	{
		ArrayList recordList=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql="select distinct per_mainbody.a0101,per_interview.mainbody_id,per_interview.ins_id,per_interview.status from per_interview,per_mainbody where per_interview.mainbody_id=per_mainbody.mainbody_id and   per_interview.object_id='"+object_id+"'";  
				sql+=" and per_interview.plan_id="+plan_id+" and per_mainbody.plan_id="+plan_id+"";
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String name=rowSet.getString("a0101");
				if(rowSet.getInt("status")==1)
					name+="(已提交)";
				else
					name+="(未提交)";
				String value=rowSet.getString("ins_id");
				CommonData com=new CommonData(value,name);
				recordList.add(com);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return recordList;
	}
	
	
	public String getTaskIdByIns(String ins_id)
	{
		String task_id="0";
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select task_id from t_wf_task  where ins_id="+ins_id+" order by task_id desc");
			if(rowSet.next())
			{
				if(rowSet.getString("task_id")!=null)
					task_id=rowSet.getString("task_id");
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		return task_id;
	}
	
	
	/** 
	 * 判断面谈纪录的操作权限  1：可编辑  2：只读
	 * @param plan_id
	 * @param object_id
	 * @return
	 */
	public String getInterviewInsID(String plan_id,String object_id)
	{
		String ins_id="0";
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select * from per_interview where object_id='"+object_id+"'  and mainbody_id='"+this.userView.getA0100()+"' and status=1 and plan_id="+plan_id);
			while(rowSet.next())
			{
				if(rowSet.getString("ins_id")!=null)
					ins_id=rowSet.getString("ins_id");
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		return ins_id;
	}
	

}
