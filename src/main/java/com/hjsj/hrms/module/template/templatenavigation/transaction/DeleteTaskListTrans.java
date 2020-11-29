package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 项目名称 ：ehr
 * 类名称：DeleteTaskListTrans
 * 类描述：代办任务和监控任务删除
 * 创建人： lis
 * 创建时间：2016-4-20
 */
public class DeleteTaskListTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		//Boolean doSelectAll=(Boolean) this.getFormHM().get("doSelectAll");//全选
		//doSelectAll为false时是勾选数据，doSelectAll是true时是未勾选数据
		ArrayList selectedlist=(ArrayList) this.getFormHM().get("deletedata");
		String tableName = (String) this.getFormHM().get("tablekey");//标示是代办还是监控
		if(!"dbtask".equals(tableName))
			tableName = PubFunc.decrypt(SafeCode.decode(tableName));
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap insmap=new HashMap();
		HashMap tabidmap=new HashMap();
		HashMap taskIdMap=new HashMap();
		HashMap fullnamemap=new HashMap();
		try{
 
				try
				{   	
					boolean isHasNotice = false;
					//同步删除考勤数据
					for(int i=0;i<selectedlist.size();i++)
					{					
						DynaBean rec=(DynaBean)selectedlist.get(i);  
						String task_id="";
						if("dbtask".equals(tableName))
							task_id=PubFunc.decrypt((String)rec.get("task_id"));
						else
							task_id=PubFunc.decrypt((String)rec.get("task_id_e"));
						if("0".equals(task_id)){
							isHasNotice = true;
							break;
						}
						String tab_id = null;
						String ins_id = null;
						String task_state = null;
						tab_id = (String)rec.get("tabid");
						ins_id = (String)rec.get("ins_id");
						
						if(!PubFunc.validateNum(ins_id,3)||!PubFunc.validateNum(tab_id,3)||!PubFunc.validateNum(task_id,3)) //防止SQL注入
								continue;
						
						taskIdMap.put(task_id, ins_id);
						if(!"dbtask".equals(tableName)){
							task_state = (String)rec.get("finished");//任务监控	
						}
						//删除考勤数据
						this.deleteKqApply(task_id, tab_id, ins_id, task_state, dao);
						TemplateParam paramBo=new TemplateParam(this.frameconn,this.userView,Integer.parseInt(tab_id));
						if(paramBo.getIsAotuLog()||paramBo.getIsRejectAotuLog()){//删除调用变动日志，删除对应单子的变动信息
							TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
							chgLogBo.deleteChangeInfoInProcess(task_id, tab_id);
						}
					}
					if(isHasNotice){
						String hinttext = ResourceFactory.getProperty("template_new.canNotDelete");//起草状态通知单不可删除，请重新选择
						this.getFormHM().put("hinttext", hinttext);
						return;
					}
				}
				catch(Exception sqle)
				{
					sqle.printStackTrace();
				}
				
				HashMap parallelTaskMap=getParallelTaskMap(taskIdMap);		
				StringBuffer delsql=new StringBuffer();
				String task_state = "";
				//删除任务
				delsql.append("delete from t_wf_task where task_id in (-1");
				int j=1;
				for(int i=0;i<selectedlist.size();i++)
				{
					DynaBean rec=(DynaBean)selectedlist.get(i);  
					String task_id="";
					if("dbtask".equals(tableName))
						task_id=PubFunc.decrypt((String)rec.get("task_id"));
					else {//ctrltask
						task_id=PubFunc.decrypt((String)rec.get("task_id_e"));
						if("".equals(task_state)){
							task_state = (String)rec.get("finished");//任务监控
							task_state = task_state.split("`")[0];//终止 4 结束 5
						}
					}
					String fullname = (String)rec.get("fullname");
					if("0".equals(task_id))
						continue;
					
					if(!PubFunc.validateNum(task_id,3)) //防止SQL注入
						continue;
					
					delsql.append(",");
					/**安全平台改造,将加密后的参数界面回来**/
					delsql.append(task_id);
					
					if(i==900*j){//oracle in 限制1000之内
						j++;
						delsql.append(")");
						delsql.append(" or task_id in (-1");
					}
					String tab_id = (String)rec.get("tabid");
					insmap.put(rec.get("ins_id"), rec.get("ins_id"));
					tabidmap.put(rec.get("ins_id"), tab_id);
					fullnamemap.put(rec.get("ins_id"), fullname);
				}
				delsql.append(")");
				
				/**对选中的任务进行删除*/
				dao.delete(delsql.toString(),new ArrayList());
				
				/** 删除其它系统的待办任务 */
				this.deletePending(taskIdMap);
				
				/** 删除 t_wf_task_objlink  中的数据 */
				this.deleteObjLink(parallelTaskMap, dao);
				
				
				/**如流程实例进行删除，如果流程实例下还有没有正在处理的任务时，则可以删除此实例*/
				this.deleteOtherTask(insmap, dao);
				
				/**任务监控删除结束终止的任务临时数据*/
				this.deleteTempletData(insmap,dao,task_state,tableName,tabidmap,fullnamemap);
			//}
		
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(this.frowset);
		}
	}
	/**
	 * 任务监控删除结束终止的任务临时数据
	 * @param insmap
	 * @param dao
	 * @param task_state
	 * @param tableName
	 * @param tabidmap 
	 * @param fullnamemap 
	 */
	private void deleteTempletData(HashMap insmap, ContentDAO dao, String task_state, String tableName, HashMap tabidmap, HashMap fullnamemap) {
		try {
			if("ctrltask".equals(tableName)) {
				Iterator ins=insmap.entrySet().iterator();
				StringBuffer sql = new StringBuffer();
				while(ins.hasNext())
				{
					Entry entry=(Entry)ins.next();
					String ins_id=entry.getKey().toString();
					if(!isHaveTask(ins_id))
					{
						String fullname = (String) fullnamemap.get(ins_id);
						if("4".equals(task_state)||"5".equals(task_state)||"二维码进入".equals(fullname)||fullname.indexOf("临时人员_")!=-1) {
							/**删除对应实例下的临时数据*/
							String tabid = (String) tabidmap.get(ins_id);
							sql.setLength(0);
							sql.append("delete from templet_"+tabid+" where ins_id=");
							sql.append(ins_id);
							dao.update(sql.toString());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author lis
	 * @Description: 得到所有当前任务相关处理人的记录
	 * @date 2016-4-20
	 * @param taskIdMap
	 * @return
	 * @throws GeneralException
	 */
	private HashMap  getParallelTaskMap(HashMap taskIdMap) throws GeneralException
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());		
			StringBuffer sql=new StringBuffer("");
			Iterator tasks=taskIdMap.entrySet().iterator();
			while(tasks.hasNext())
			{
				Entry entry=(Entry)tasks.next();
				String task_id=entry.getKey().toString();
				String ins_id=entry.getValue().toString();
				
				if(!PubFunc.validateNum(task_id,3)||!PubFunc.validateNum(ins_id,3)) //防止SQL注入
					continue;
				
				/**安全平台改造，将加密的参数解密回来**/
				sql.append(" or  (ins_id=" + ins_id + " and task_id=" + task_id + ")");
			}
			if(StringUtils.isNotBlank(sql.toString())){
				RowSet rowSet=dao.search("select * from t_wf_task_objlink where "+sql.substring(3));
				while(rowSet.next())
					map.put(rowSet.getString("ins_id")+"/"+rowSet.getString("task_id"),"1");
				PubFunc.closeDbObj(rowSet);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	
	/**
	 * @author lis
	 * @Description: 判断该流程实例下还有没有正在处理的任务
	 * @date 2016-4-20
	 * @param ins_id 实例id
	 * @return boolean
	 * @throws GeneralException
	 */
	private boolean isHaveTask(String ins_id) throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		boolean bflag=false;
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());				
			buf.append("select task_id from t_wf_task where ins_id=");
			buf.append(ins_id);
			//删除任务实例 先查询流程是否存在运行中或等待状态的任务  有则不删除任务实例  
			//去掉报备任务  20180629  update
			buf.append(" and task_type='2' and "+Sql_switcher.isnull("bs_flag","'1'")+"<>'3' and ((task_state='3') or (task_state='2'))");
		    rset=dao.search(buf.toString());
			if(rset.next())
				bflag=true;
			
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);				
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}
		return bflag;
	}

	/**
	 * @author lis
	 * @Description: 同步删除考勤数据
	 * @date 2016-4-20
	 * @param task_id 任务id
	 * @param tab_id 模板id
	 * @param ins_id 实例id
	 * @param task_state 状态
	 * @param dao
	 */
	private void deleteKqApply(String task_id,String tab_id,String ins_id,String task_state,ContentDAO dao){
		try
		{   		
			if (task_state==null){//在“我的待办”中删除
				RecordVo vo = new RecordVo("t_wf_task");
				vo.setInt("task_id", Integer.parseInt(task_id));
				vo = dao.findByPrimaryKey(vo);
				task_state = vo.getString("task_state");
				ins_id=vo.getString("ins_id");
			}else{
				task_state = task_state.split("`")[0];
			}
			
			if ("3".equals(task_state)&&PubFunc.validateNum(tab_id,3)&&PubFunc.validateNum(ins_id,3)){//正在运行的单据
				WF_Instance ins=new  WF_Instance(Integer.parseInt(tab_id),this.getFrameconn(),this.userView); 
				String strsql="select * from templet_"+tab_id+" where ins_id ="+ins_id;    
				ins.insertKqApplyTable(strsql,tab_id,"0","10","templet_"+tab_id); //往考勤申请单中写入报批记录
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @author lis
	 * @Description: 删除其它系统的待办任务
	 * @date 2016-4-20
	 * @param task_id
	 * @param pendingType
	 * @param imip
	 */
	private void deletePending(HashMap taskIdMap){
		try {
			PendingTask imip=new PendingTask();
			String pendingType="业务模板";
			Iterator tasks=taskIdMap.entrySet().iterator();
			while(tasks.hasNext())
			{
				Entry entry=(Entry)tasks.next();
				String task_id=entry.getKey().toString();
				
				task_id=PubFunc.encrypt(task_id);
				imip.updatePending("T","HRMS-"+task_id,100,pendingType,this.userView);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author lis
	 * @Description: 删除 t_wf_task_objlink  中的数据
	 * @date 2016-4-20
	 * @param parallelTaskMap
	 * @param dao
	 */
	public void deleteObjLink(HashMap parallelTaskMap,ContentDAO dao){
		try {
			Iterator ains=parallelTaskMap.entrySet().iterator();
			StringBuffer delsql = new StringBuffer();
			while(ains.hasNext())
			{
				Entry entry=(Entry)ains.next();
				String temp_str=entry.getKey().toString();
				String[] temps=temp_str.split("/");
				delsql.setLength(0);
				delsql.append("delete from t_wf_task_objlink where ins_id=");
				delsql.append(temps[0]+" and task_id="+temps[1]);
				dao.update(delsql.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author lis
	 * @Description: 如流程实例进行删除，如果流程实例下还有没有正在处理的任务时，则可以删除此实例
	 * @date 2016-4-20
	 * @param insmap
	 * @param dao
	 */
	public void deleteOtherTask(HashMap insmap,ContentDAO dao){
		try {
			Iterator ins=insmap.entrySet().iterator();
			StringBuffer sql = new StringBuffer();
			while(ins.hasNext())
			{
				Entry entry=(Entry)ins.next();
				String ins_id=entry.getKey().toString();
				if(!isHaveTask(ins_id))
				{
					/**删除对应实例下的任务*/
					sql.setLength(0);
					sql.append("delete from t_wf_task where ins_id=");
					sql.append(ins_id);
					dao.update(sql.toString());
					
					
					RecordVo ins_vo=new RecordVo("t_wf_instance");
					ins_vo.setInt("ins_id", Integer.parseInt(ins_id));
					dao.deleteValueObject(ins_vo);
					
					sql.setLength(0);
					sql.append("delete from t_wf_task_objlink where ins_id=");
					sql.append(ins_id);
					dao.update(sql.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
