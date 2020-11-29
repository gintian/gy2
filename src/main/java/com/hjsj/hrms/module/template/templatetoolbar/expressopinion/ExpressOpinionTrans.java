package com.hjsj.hrms.module.template.templatetoolbar.expressopinion;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 
 * <p>Title:ExpressOpinionTrans</p>
 * <p>Description:人事异动发表意见</p> 
 * <p>Company:hjsj</p> 
 * create time Jun 20, 2016 11:52:01 AM
 * @author hej
 * @version 1.0
 */
public class ExpressOpinionTrans extends IBusiness{
	String unDealedTaskIds = "";
	@Override
    public void execute() throws GeneralException {
		try
		{
			HashMap hmMap= this.getFormHM();			
			String taskid = TemplateFuncBo.getDecValueFromMap(hmMap,"task_id");
			String topic=(String)this.getFormHM().get("topic");
			String model=(String)this.getFormHM().get("approve_flag"); //3:报备  2 加签
			String tab_id=(String)this.getFormHM().get("tab_id");//模板号
			if(topic!=null)
				topic=SafeCode.decode(topic);
			else
				topic="";
			ArrayList list = new ArrayList();
			if(taskid.contains(",")){
				String [] taskarr = taskid.split(",");
				for(int i=0;i<taskarr.length;i++){
					String task_id = taskarr[i];
					list.add(task_id);
				}
			}else
				list.add(taskid);
			for(int i=0;i<list.size();i++){
				String task_id = (String)list.get(i);
				String opinionstr_ = " select * from templet_"+tab_id+" ";
				opinionstr_+=" where  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id  ";
				opinionstr_+="  and task_id ="+task_id+"  and submitflag=1  and task_type=3 ) ";
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset = dao.search(opinionstr_);
				if(this.frowset.next()){
					String taskid_ = this.frowset.getString("task_id");
					String ins_id=this.frowset.getString("ins_id");
					if(task_id!=null&&!"".equals(task_id)){
						RecordVo task_vo=new RecordVo("t_wf_task");
						task_vo.setInt("task_id",Integer.parseInt(task_id));
						task_vo=dao.findByPrimaryKey(task_vo);
						String sender=null;
						if(this.userView.getStatus()!=0)
							sender=this.userView.getDbname()+this.userView.getA0100();
						else
							sender=this.userView.getUserId(); 
						String fullsender=this.userView.getUserFullName();
						if(fullsender==null|| "".equalsIgnoreCase(fullsender))
							fullsender=this.userView.getUserId();
						task_vo.setString("a0100",sender);//人员编号 实际处理人员编码
						task_vo.setString("a0101",fullsender);//人员姓名 实际处理人员姓名
						task_vo.setString("content",topic);
						task_vo.setDate("end_date",new Date()); 				
						task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));  
						dao.updateValueObject(task_vo);
						
						String pendingCode="HRMS-"+PubFunc.encrypt(taskid_);
						String pendingType="业务模板"; 
						PendingTask imip=new PendingTask();
						imip.updatePending("T",pendingCode,1,pendingType,this.userView);  
					}
					
					/*this.frowset=dao.search("select  tabid,t_wf_task.pri_task_id,t_wf_task.ins_id   from t_wf_task,t_wf_instance where t_wf_task.ins_id=t_wf_instance.ins_id and task_id="+taskid_);
					if(this.frowset.next())
					{
						tabid=this.frowset.getString("tabid");
						pri_task_id=this.frowset.getString("pri_task_id");
						ins_id=this.frowset.getString("ins_id");
					}*/
					TemplateBo templatebo=new TemplateBo(this.getFrameconn(),this.userView,Integer.parseInt(tab_id));
					String opinion_field = templatebo.getParamBo().getOpinion_field();
					if(opinion_field!=null&&opinion_field.trim().length()>0)
					{
						ArrayList fieldlist=templatebo.getAllFieldItem();
						/*
						String opinionstr = " select * from templet_"+tab_id+" ";
						opinionstr+=" where  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id  ";
						opinionstr+="  and task_id="+taskid_+"   and submitflag=1  ) ";*/
						
						for(int j=0;j<fieldlist.size();j++)
						{
							FieldItem fielditem=(FieldItem)fieldlist.get(j);
							String field_name=fielditem.getItemid();
							String opinion_field_temp = opinion_field+"_2";//liuyz bug29192 原来opinion_field_temp = opinion_field+"_2"每次循环opinion_field都会加_2导致后台报错
							if(fielditem.isChangeAfter()&&opinion_field!=null&&opinion_field.length()>0&&opinion_field_temp.equalsIgnoreCase(field_name))
							{
								String nodename="";
								StringBuffer sbuffer = new StringBuffer();
								if("3".equals(model))
									nodename="报备人"; 
								else if("2".equals(model))
									nodename="加签人";
								
								CodeItem UMitem=AdminCode.getCode("UM", this.userView.getUserDeptId(), 20);
						        CodeItem UNitem=AdminCode.getCode("UN", this.userView.getUserOrgId());
						        String value="";
						        if(UNitem!=null){
						        	value=UNitem.getCodename();
						        }else{
						        	value = AdminCode.getCodeName("UN",this.userView.getUserOrgId())!= null ? AdminCode.getCodeName("UN", this.userView.getUserOrgId()): "";
						        }
								if(UMitem!=null)
						    	{
									if(StringUtils.isNotBlank(value)){
										value+="/"+UMitem.getCodename();
									}else{
										value=UMitem.getCodename();
									}
								}
						    	else
						    	{
						    		String value1 = AdminCode.getCodeName("UM",this.userView.getUserDeptId())!= null ? AdminCode.getCodeName("UM", this.userView.getUserDeptId()): "";
						    		if(StringUtils.isNotBlank(value)){
										value+="/"+value1;
									}else{
										value=value1;
									}
						    	}
								sbuffer.append("\r\n");
						        sbuffer.append(value);
						        sbuffer.append("("+nodename+")：");   
						        sbuffer.append("\r\n");  
						        sbuffer.append(""+this.userView.getUserFullName());
						        sbuffer.append("     ");
						        sbuffer.append(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm")+"   ");
						        sbuffer.append("\r\n"); 
								sbuffer.append("意见:"+topic.replace("<p>", "\r\n").replace("</p>", "").replace("&nbsp;", " "));
								TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
								tablebo.updateApproveOpinion( "templet_"+tab_id,ins_id,opinion_field+"_2",sbuffer.toString(),opinionstr_);
								break;
							}
								
						}
					}
				}else
					unDealedTaskIds=unDealedTaskIds+","+PubFunc.encrypt(task_id);
			}
			if(!"".equals(unDealedTaskIds))
				unDealedTaskIds = unDealedTaskIds.substring(1,unDealedTaskIds.length());
			this.getFormHM().put("unDealedTaskIds", unDealedTaskIds);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

		
	}
}
