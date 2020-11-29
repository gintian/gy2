package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;

public class SaveOpinionTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String taskid=(String)this.getFormHM().get("taskid");
			String topic=(String)this.getFormHM().get("topic");
			String model=(String)this.getFormHM().get("model"); //6:报备  7 加签
			if(topic!=null)
				topic=SafeCode.decode(topic);
			else
				topic="";
			String tabid="";
			String pri_task_id="";
			String ins_id="";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select  tabid,t_wf_task.pri_task_id,t_wf_task.ins_id   from t_wf_task,t_wf_instance where t_wf_task.ins_id=t_wf_instance.ins_id and task_id="+taskid);
			if(this.frowset.next())
			{
				tabid=this.frowset.getString("tabid");
				pri_task_id=this.frowset.getString("pri_task_id");
				ins_id=this.frowset.getString("ins_id");
			}
				
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			
			
			RecordVo task_vo=new RecordVo("t_wf_task");
			task_vo.setInt("task_id",Integer.parseInt(taskid));
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
			
			String pendingCode="HRMS-"+PubFunc.encrypt(taskid);
			String pendingType="业务模板"; 
			PendingTask imip=new PendingTask();
			imip.updatePending("T",pendingCode,1,pendingType,this.userView);  
			
			
			String opinion_field = tablebo.getOpinion_field();
			if(opinion_field!=null&&opinion_field.trim().length()>0)
			{
				ArrayList fieldlist=tablebo.getAllFieldItem();
				
				String opinionstr = " select * from templet_"+tabid+" ";
				opinionstr+=" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum ";
				opinionstr+="  and task_id="+pri_task_id+"   and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ";
				
				for(int j=0;j<fieldlist.size();j++)
				{
					FieldItem fielditem=(FieldItem)fieldlist.get(j);
					String field_name=fielditem.getItemid();
					if(fielditem.isChangeAfter()&&opinion_field!=null&&opinion_field.length()>0&&opinion_field.equalsIgnoreCase(field_name))
					{
						String nodename="";
						StringBuffer sbuffer = new StringBuffer();
						if("6".equals(model))
							nodename="报备人"; 
						else if("7".equals(model))
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
						tablebo.updateApproveOpinion( "templet_"+tabid,ins_id,opinion_field+"_2",sbuffer.toString(),opinionstr);
						break;
					}
						
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
