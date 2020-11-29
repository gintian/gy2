package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class DeleteTaskListTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList selectedlist=(ArrayList)this.getFormHM().get("selectedlist");
		String sp_flag=(String) this.getFormHM().get("sp_flag");
		boolean backFlag =true;//是否需要将task_id转换回来，从任务监控中删除时不需要转换
		if(sp_flag!=null&&"2".equals(sp_flag)){
			backFlag=false;
		}
		if(selectedlist==null || selectedlist.size()==0)
			return;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap insmap=new HashMap(); 
		try{
			//同步删除考勤数据
			try
            {   				
				for(int i=0;i<selectedlist.size();i++)
				{					
					LazyDynaBean rec=(LazyDynaBean)selectedlist.get(i);  
					String task_id ="";
					if(backFlag){
						task_id=PubFunc.decrypt((String)rec.get("task_id"));
					}else{
						task_id=(String)rec.get("task_id");
					}
					String tab_id=(String)rec.get("tabid");
					String ins_id=(String)rec.get("ins_id");
					String task_state=(String)rec.get("finished");	
					if (task_state==null){//在“我的待办”中删除
						RecordVo vo = new RecordVo("t_wf_task");
						vo.setInt("task_id", Integer.parseInt(task_id));
						vo=dao.findByPrimaryKey(vo);
						task_state=vo.getString("task_state");
						ins_id=vo.getString("ins_id");
						
					}
				
                	if ("3".equals(task_state)){//正在运行的单据
                		WF_Instance ins=new  WF_Instance(Integer.parseInt(tab_id),this.getFrameconn(),this.userView); 
        				String strsql="select * from templet_"+tab_id+" where ins_id ="+ins_id;    
						ins.insertKqApplyTable(strsql,tab_id,"0","10","templet_"+tab_id); //往考勤申请单中写入报批记录
        			
                	}
			     }
            }
            catch(Exception sqle)
            {
                sqle.printStackTrace();
            }
            
			HashMap parallelTaskMap=getParallelTaskMap( selectedlist,sp_flag);		
			StringBuffer delsql=new StringBuffer();
			delsql.append("delete from t_wf_task where task_id in (-1");
			int j=1;
			for(int i=0;i<selectedlist.size();i++)
			{
				
				LazyDynaBean rec=(LazyDynaBean)selectedlist.get(i);  
				delsql.append(",");
				/**安全平台改造,将加密后的参数界面回来**/
				if(backFlag){
					delsql.append(PubFunc.decrypt((String)rec.get("task_id")));
				}else{
					delsql.append((String)rec.get("task_id"));
				}
			
				if(i==900*j){//oracle in 限制1000之内
					j++;
					delsql.append(")");
					delsql.append(" or task_id in (-1");
				}
				String task_id=(String)rec.get("task_id");
				String ins_id=(String)rec.get("ins_id");
				
				insmap.put(rec.get("ins_id"), rec.get("ins_id"));
			}
			delsql.append(")");
			/**对选中的任务进行删除*/
			dao.delete(delsql.toString(),new ArrayList());
			
			/** 删除其它系统的待办任务 */
			PendingTask imip=new PendingTask();
			String pendingType="业务模板";
			for(int i=0;i<selectedlist.size();i++)
			{
				
				LazyDynaBean rec=(LazyDynaBean)selectedlist.get(i); 
				String taskid=String.valueOf(rec.get("task_id"));
				if (!backFlag){
					taskid=PubFunc.encrypt(taskid);
				}
				imip.updatePending("T","HRMS-"+taskid,100,pendingType,this.userView);
			}
			
			/** 删除 t_wf_task_objlink  中的数据 */
			Iterator ains=parallelTaskMap.entrySet().iterator();
			while(ains.hasNext())
			{
					delsql.setLength(0);
					Entry entry=(Entry)ains.next();
					String temp_str=entry.getKey().toString();
					String[] temps=temp_str.split("/");
					delsql.append("delete from t_wf_task_objlink where ins_id=");
					delsql.append(temps[0]+" and task_id="+temps[1]);
					dao.update(delsql.toString());
			}
			
			
			/**如流程实例进行删除，如果流程实例下还有没有正在处理的任务时，则可以删除此实例*/
			Iterator ins=insmap.entrySet().iterator();
			while(ins.hasNext())
			{
					Entry entry=(Entry)ins.next();
					String ins_id=entry.getKey().toString();
					delsql.setLength(0);
					if(!isHaveTask(ins_id))
					{
						RecordVo ins_vo=new RecordVo("t_wf_instance");
						ins_vo.setInt("ins_id", Integer.parseInt(ins_id));
						dao.deleteValueObject(ins_vo);
						/**删除对应实例下的任务*/
						delsql.append("delete from t_wf_task where ins_id=");
						delsql.append(ins_id);
						dao.update(delsql.toString());
						
						delsql.setLength(0);
						delsql.append("delete from t_wf_task_objlink where ins_id=");
						delsql.append(ins_id);
						dao.update(delsql.toString());
					}
			}
			
			
			
			
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	private HashMap  getParallelTaskMap(ArrayList selectedlist, String spFlag)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());		
			StringBuffer sql=new StringBuffer("");
			boolean backFlag=true;
			if(spFlag!=null&&"2".equals(spFlag)){
				backFlag=false;
			}
			for(int i=0;i<selectedlist.size();i++)
			{
				LazyDynaBean rec=(LazyDynaBean)selectedlist.get(i);  
				/**安全平台改造，将加密的参数解密回来**/
				if(backFlag){
					sql.append(" or  (ins_id="+rec.get("ins_id")+" and task_id="+PubFunc.decrypt((String)rec.get("task_id"))+")");
				}else{
					sql.append(" or  (ins_id="+rec.get("ins_id")+" and task_id="+rec.get("task_id")+")");
				}
				//sql.append(" or  (ins_id="+rec.get("ins_id")+" and task_id="+rec.get("task_id")+")");
			}
			RowSet rowSet=dao.search("select * from t_wf_task_objlink where "+sql.substring(3));
			while(rowSet.next())
				map.put(rowSet.getString("ins_id")+"/"+rowSet.getString("task_id"),"1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	private boolean isHaveTask(String ins_id) throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		boolean bflag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());				
			buf.append("select task_id from t_wf_task where ins_id=");
			buf.append(ins_id);
			buf.append(" and task_type='2' and ((task_state='3') or (task_state='5'))");;
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				bflag=true;
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);				
		}
		return bflag;
	}
	

}
