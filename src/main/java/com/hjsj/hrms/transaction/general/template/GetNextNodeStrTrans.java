package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;


public class GetNextNodeStrTrans  extends IBusiness {
	
	public void execute() throws GeneralException {
	
		try
		{
			 
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sp_mode="1";  //手工流转
			if(this.getFormHM().get("sp_mode")!=null)
				sp_mode=(String)this.getFormHM().get("sp_mode");
			String tabid=(String)this.getFormHM().get("tabid"); 
			TemplateTableBo	tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			
			
			if("1".equals(sp_mode))
			{
				String objecttype=(String)this.getFormHM().get("objecttype"); //对送对象类型  =1,具体审批人,=2角色 =3组织单位
				String name=(String)this.getFormHM().get("name");
				String specialRoleRoleId="";
				if("2".equals(objecttype))//如果是角色
				{
					this.frowset=dao.search("select * from t_sys_role where role_id='"+name+"'");
					if(this.frowset.next())
					{
						int role_property=this.frowset.getInt("role_property");
						if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13)
						{ 
							specialRoleRoleId=name+"`"+role_property;
							
							int n=0;
							StringBuffer singleStr=new StringBuffer("");
							
							RecordVo t_wf_relationVo=new RecordVo("t_wf_relation"); 
							if(tablebo.getRelation_id()==null||tablebo.getRelation_id().length()==0)
								throw new GeneralException("该业务流程没有定义审批关系!"); 
							
							
							if("gwgx".equalsIgnoreCase(tablebo.getRelation_id())) //标准岗位关系
							{
								WorkflowBo wkbo=new WorkflowBo(this.getFrameconn(),this.userView);
								LazyDynaBean _abean=new LazyDynaBean();
								_abean.set("type","@K");
								_abean.set("value",this.userView.getUserPosId());
								_abean.set("from_nodeid","reportNode"); 
								ArrayList tempList=wkbo.getSuperPos_userList(_abean,"human",String.valueOf(role_property));
							//	if(!(role_property==10||role_property==11||role_property==12))
								if(role_property==13)
								{
									throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info7")+"!");
								} 
								if(!(role_property==13))
								{ 
									if(tempList.size()==1)
									{
										n=1;
										LazyDynaBean abean=(LazyDynaBean)tempList.get(0);
										singleStr.append(",human:"+(String)abean.get("mainbody_id")+"`"+(String)abean.get("a0101"));
									}
								}
								
							}
							else
							{
							
								t_wf_relationVo.setInt("relation_id", Integer.parseInt(tablebo.getRelation_id()));
								t_wf_relationVo=dao.findByPrimaryKey(t_wf_relationVo);
								String sql="";
								if("1".equals(t_wf_relationVo.getString("actor_type")))  //自助用户
								{
									sql="select * from t_wf_mainbody where Relation_id="+tablebo.getRelation_id()+"  and lower(Object_id)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"'";
								}
								else if("4".equals(t_wf_relationVo.getString("actor_type"))) //业务用户
								{
									sql="select *  from t_wf_mainbody where Relation_id="+tablebo.getRelation_id()+"  and lower(Object_id)='"+this.userView.getUserName()+"'";
								} 
								if(role_property!=13)
									sql+=" and SP_GRADE="+role_property+" " ;
								else
									sql+=" and SP_GRADE in (9,10,11,12) " ;
								this.frowset=dao.search(sql);
								while(this.frowset.next())
								{
									n++;
									String a0101=this.frowset.getString("a0101");
									if("4".equals(t_wf_relationVo.getString("actor_type")))
									{
										if(a0101==null||a0101.trim().length()==0)
											a0101=this.frowset.getString("Mainbody_id"); 
									}
									singleStr.append(",human:"+this.frowset.getString("mainbody_id")+"`"+a0101);
								}
							}
							 
							 
							if(n==1&&singleStr.length()>0)
							{
								specialRoleRoleId="$$"+singleStr.substring(1);
							} 
							
						}
					}
					
					
				}
				this.getFormHM().put("specialRoleRoleId",specialRoleRoleId);
			} //如果是手工审批
			else //如果是自动流转
			{
				
				String task_id=(String)this.getFormHM().get("task_id");
				/**安全平台,改造判断taskid是否在后台存在**/
				HashMap templateMap =(HashMap) this.userView.getHm().get("templateMap");
				String sp_batch=(String)this.getFormHM().get("sp_batch");
				/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
				if(templateMap!=null&&!templateMap.containsKey(task_id)&&!"1".equals(sp_batch)){//批量审批的时候不能在这里简单的判断task_id是否存在
					throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
				}
				*/
				String ins_id=(String)this.getFormHM().get("ins_id");
				
				String batch_task=(String)this.getFormHM().get("batch_task");
				String selfapply=(String)this.getFormHM().get("selfapply");
				String type=(String)this.getFormHM().get("type");
				if(selfapply==null||selfapply.trim().length()==0)
					selfapply = "0";
				if(task_id==null)
					task_id="0";
				if(ins_id==null)
					ins_id="0";
				if(sp_batch!=null&& "1".equals(sp_batch))
				{
					String[] temps=batch_task.split(",");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i]!=null&&temps[i].trim().length()>0)
						{
							ArrayList valueList=new ArrayList();
							valueList.add(new Integer(temps[i]));
							valueList.add(new Integer(tabid));
							this.frowset=dao.search("select ins_id from t_wf_task_objlink where task_id=? and submitflag=1 and tab_id=? and state<>3 ",valueList); ////20160630 dengcan
							if(this.frowset.next())
							{
								task_id=temps[i];
								ins_id=this.frowset.getString("ins_id");
								break;
							}
						}
						
					}
					/*for(int i=0;i<temps.length;i++)
					{
						if(temps[i]!=null&&temps[i].trim().length()>0)
							task_id=temps[i];
					}
					this.frowset=dao.search("select ins_id from t_wf_task where task_id="+task_id);
					if(this.frowset.next())
						ins_id=this.frowset.getString("ins_id");*/
					
				}
				
				WorkflowBo wbo=new WorkflowBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
				if(type!=null&& "2".equals(type)){
					this.getFormHM().put("nextNodeStr","");
				}else{
					if("0".equals(ins_id)&&tablebo.getSplit_data_model()!=null&&tablebo.getSplit_data_model().trim().length()>0&&("superior".equalsIgnoreCase(tablebo.getSplit_data_model())|| "groupfield".equalsIgnoreCase(tablebo.getSplit_data_model())))
					{
						this.getFormHM().put("nextNodeStr","");
					}
					else
					{
						if("1".equalsIgnoreCase(selfapply))
							tablebo.setBEmploy(true);
					    autoValidate(task_id,ins_id,sp_batch,batch_task,selfapply,tablebo); 
						String nextNodeStr=wbo.getNextNodeStr(Integer.parseInt(task_id),Integer.parseInt(ins_id),selfapply);
						this.getFormHM().put("nextNodeStr",nextNodeStr);
					}
				}
				this.getFormHM().put("flag",(String)this.getFormHM().get("flag"));
				this.getFormHM().put("type",(String)this.getFormHM().get("type"));
			} //如果是自动流转
			 
			
		} //try
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	/**
	 * 自动校验
	 * @param task_id
	 * @param ins_id
	 * @param sp_batch
	 * @param batch_task
	 * @param selfapply
	 * @param tablebo
	 * @throws GeneralException
	 */
	private void  autoValidate(String task_id,String ins_id,String sp_batch,String batch_task,String selfapply,TemplateTableBo	tablebo) throws GeneralException 
	{
		try
		{
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList taskList=new ArrayList();
			taskList.add(task_id+"`"+ins_id);
			if(sp_batch!=null&& "1".equals(sp_batch))
			{
				taskList=new ArrayList();
				String[] temps=batch_task.split(",");
				String _task_id="";
				String _ins_id="";
				for(int i=0;i<temps.length;i++)
				{
					_task_id="";
					_ins_id="";
					if(temps[i]!=null&&temps[i].trim().length()>0)
						_task_id=temps[i];
					this.frowset=dao.search("select ins_id from t_wf_task where task_id="+_task_id);
					if(this.frowset.next())
						_ins_id=this.frowset.getString("ins_id");
					if(_task_id.length()>0&&_task_id.length()>0)
						taskList.add(_task_id+"`"+_ins_id);
				}
				
				
			}
			
			ArrayList fieldlist=tablebo.getAllFieldItem();
			
//--------------------------------------------------------------------------------------------------------------------			
			StringBuffer ins_ids=new StringBuffer("");
			for(int i=0;i<taskList.size();i++)
			{
				String[] temp=((String)taskList.get(i)).split("`");
				ins_id=temp[1]; 
				task_id=temp[0];
				ins_ids.append(","+ins_id);
			}
			
			boolean isjx=false;
			//自动计算
			Boolean bCalc=false;
			if("0".equals(task_id)||"".equals(task_id) || "1".equals(tablebo.isStartNode(task_id))){
				if(tablebo.getAutoCaculate().length()==0){
					if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
						bCalc=true;
					}
				}
				else if("1".equals(tablebo.getAutoCaculate())){
					bCalc=true;
				}	
			}else {
				if(tablebo.getSpAutoCaculate().length()==0){
					if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
						bCalc=true;
					}
				}
				else if("1".equals(tablebo.getSpAutoCaculate())){
					bCalc=true;
				}
			}
			if(bCalc){//发起时才自动计算
				tablebo.batchComputeMidvariable(ins_ids.substring(1));
				isjx=true;
				tablebo.batchCompute(ins_ids.substring(1));
			}
			String srcTab="templet_"+tablebo.getTabid();
			if("0".equals(task_id))
			{
					if("1".equals(selfapply))
						srcTab="g_templet_"+tablebo.getTabid();
					else
						srcTab=this.userView.getUserName()+"templet_"+tablebo.getTabid();
			}
			if(!isjx)
					tablebo.batchComputeMidvariable(ins_ids.substring(1));
			 
//--------------------------------------------------------------------------------------------------------------------				
			//是否控制人员编制
			String controlHeadCount="0";   // 1:控制人员编制
			for(int i=0;i<taskList.size();i++)
			{
				String[] temp=((String)taskList.get(i)).split("`");
				ins_id=temp[1]; 
				task_id=temp[0];
				
				if(tablebo.isHeadCountControl(Integer.parseInt(task_id))){//报批和提交时验证超编
					controlHeadCount="1";
				}
				/*
				boolean isjx=false;
				//自动计算
				if(tablebo.getAutoCaculate().length()==0)
				{
					if(SystemConfig.getPropertyValue("templateAutoCompute")!=null&&SystemConfig.getPropertyValue("templateAutoCompute").equalsIgnoreCase("true"))
					{
						tablebo.batchComputeMidvariable(ins_id);
						isjx=true;
						tablebo.batchCompute(ins_id);
					}
				}else if(tablebo.getAutoCaculate().equals("1"))
				{
					tablebo.batchComputeMidvariable(ins_id);
					isjx=true;
					tablebo.batchCompute(ins_id);
				}
				String srcTab="templet_"+tablebo.getTabid();
				if(task_id.equals("0"))
				{
					if(selfapply.equals("1"))
						srcTab="g_templet_"+tablebo.getTabid();
					else
						srcTab=this.userView.getUserName()+"templet_"+tablebo.getTabid();
				}
				if(!isjx)
					tablebo.batchComputeMidvariable(ins_id);
					
				*/
				tablebo.checkMustFillItem(srcTab,fieldlist,Integer.parseInt(task_id));
				
				String noCheckTemplateIds=SystemConfig.getPropertyValue("noCheckTemplateIds");  //system.properties -->  noCheckTemplateIds=12,88
				//20141210  dengcan  汉口银行行长在批准单据时嫌速度太慢，与刘红梅商量针对行长的单据在审批时无需审核，提高程序执行效率
				if(noCheckTemplateIds!=null&&Integer.parseInt(task_id)>0&&(","+noCheckTemplateIds+",").indexOf(","+tablebo.getTabid()+",")!=-1)  
				{
					
				}
				else
					tablebo.checkLogicExpress(srcTab, Integer.parseInt(task_id), fieldlist);  
				
				
			 
			}
			tablebo.setComputeVar(false);
			this.getFormHM().put("controlHeadCount",controlHeadCount);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	
	
	
	
	
}
