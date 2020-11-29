package com.hjsj.hrms.module.template.templatetoolbar.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.utils.TemplateInterceptorAdapter;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time: 2016-02-23
 * @author dengcan
 * @version 1.0
 *
 */

public class TemplateDelTrans extends IBusiness {
	private  int recorsionCount;
	@Override
	public void execute() throws GeneralException {
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			HashMap hm=this.getFormHM();
			String tab_id=(String)hm.get("tabid");
	        String task_id=(String)hm.get("task_id");
	        String infor_type=(String)hm.get("infor_type");  //=1时代表人员 =2时代表单位 =3时代表职位
	       
	        String isDelMsg=(String)hm.get("isDelMsg"); //是否删除消息表中的记录
	        
	        StringBuffer task_ids=new StringBuffer("");
	        if(!"0".equalsIgnoreCase(task_id)){//进入了审批流
	        	String dataStr= (String) hm.get("dataStr");  //存储taskid 的集合，用“,”分割
	        	if(dataStr.indexOf(",")!=-1){//多个人
	        		String[] dataArr=dataStr.split(",");
	        		for(int i=0;i<dataArr.length;i++){
	        			if(dataArr[i].trim().length()>0)
	        			{
	        				String taskId = dataArr[i];
	        				if(!"0".equals(taskId))
	        					taskId = PubFunc.decrypt(taskId); 
	        				task_ids.append(","+taskId);
	        			}
	        		}
	        	}else{//单个人
	        		String taskId = dataStr;
	        		if(!"0".equals(taskId))
	        			taskId = PubFunc.decrypt(taskId); 
	        		task_ids.append(","+taskId);
	        	}
	        }
	         
			String updateMsg="delete from tmessage where   object_type=?  and noticetempid=? ";
			ArrayList updateMsgList=new ArrayList();
			String select_str=""; 
			if(isDelMsg==null|| "0".equals(isDelMsg)) //是否同步删除通知单里的记录
				updateMsg="update tmessage set  state=0 where  object_type=?  and noticetempid=?  ";
			
			if("1".equals(infor_type))
			{
				updateMsg+=" and a0100=?  and lower(db_type)=?";
				select_str=" a0100,basepre ";
			
			}
			else if("2".equals(infor_type))
			{
				updateMsg+=" and b0110=? ";
				select_str=" b0110 ";
			
			}
			else if("3".equals(infor_type))
			{
				updateMsg+=" and e01a1=? ";
				select_str=" e01a1 ";
			
			}
			
			 // 根据tab_id和ins_id的取值得到人事异动列表对应的表名
			String setname= this.userView.getUserName()+"templet_"+tab_id; 
			StringBuffer sql=new StringBuffer();
			if(task_id.length()>0 && !"0".equalsIgnoreCase(task_id)){//进入了审批流
				setname="templet_"+tab_id; 
				sql=new StringBuffer("select "+select_str+" from "+setname+" where state=1 ");
				sql.append(" and  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
				sql.append(" and t_wf_task_objlink.submitflag=1 and t_wf_task_objlink.task_id in ("+task_ids.substring(1)+")  and t_wf_task_objlink.state<>3   )");  
				
			}
			else//未进入审批流
			{ 
				sql=new StringBuffer("select "+select_str+" from "+setname+" where state=1 ");
				sql.append(" and  submitflag=1 "); 
			}
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{ 
				ArrayList valueList=new ArrayList();
				valueList.add(new Integer(infor_type));
				valueList.add(new Integer(tab_id));
				if("1".equals(infor_type))
				{
					valueList.add(this.frowset.getString("a0100"));
				    valueList.add(this.frowset.getString("basepre").toLowerCase());
				}
				else if("2".equals(infor_type))
					valueList.add(this.frowset.getString("b0110"));
				else if("3".equals(infor_type))
					valueList.add(this.frowset.getString("e01a1"));
				updateMsgList.add(valueList);
			}  
			if(updateMsgList.size()>0)
			{
				dao.batchUpdate(updateMsg.toString(), updateMsgList); //撤销记录时对通知表的操作 
			}
		
			 boolean isProcessEnd = false;//是否流程终止 lis 20160418
			 TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
			///开始撤销数据
			 TemplateParam paramBo=new TemplateParam(this.getFrameconn(),this.userView,Integer.parseInt(tab_id));
			 TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
			 
			if("0".equals(task_id)){//未进入审批流
			    tablebo.updateWorkCodeState(setname,"submitflag=1");
			    //删除签章
			    StringBuffer signsql=new StringBuffer("select * from "+this.userView.getUserName()+"templet_"+tab_id); 
			    signsql.append(" where  submitflag=1");
			    this.frowset=dao.search(signsql.toString());
			    ArrayList personList=new ArrayList();
			    while(this.frowset.next()){
			    	if("1".equalsIgnoreCase(infor_type)){
			    		String nbase=this.frowset.getString("BasePre");
			    		String a0100=this.frowset.getString("a0100");
			    		ArrayList list=new ArrayList();
			    		list.add(nbase);
			    		list.add(a0100);
			    		personList.add(list);
			    	}else if("2".equalsIgnoreCase(infor_type)){
			    		String nbase=this.frowset.getString("b0110");
			    		ArrayList list=new ArrayList();
			    		list.add(nbase);
			    		personList.add(list);
			    	}else if("3".equalsIgnoreCase(infor_type)){
			    		String nbase=this.frowset.getString("e01a1");
			    		ArrayList list=new ArrayList();
			    		list.add(nbase);
			    		personList.add(list);
			    	}
			    	String signature = this.frowset.getString("signature");
			    	if(signature!=null&&!"".equalsIgnoreCase(signature))//liuyz bug28641
			    	{
			    		delSignatureXml(dao,signature,paramBo);
			    	}
			    	//删除附件 liuyz bug 26890 
					if("1".equals(infor_type)&&(this.frowset.getString("basepre")!=null&&this.frowset.getString("a0100")!=null))
					{
						StringBuffer attarSql=new StringBuffer("delete from t_wf_file where Lower(basepre)=Lower(?) and objectid=? and tabid=? and create_user=? and ins_id=?");
						ArrayList childList=new ArrayList();						
						childList.add(this.frowset.getString("basepre"));
						childList.add(this.frowset.getString("a0100"));
						childList.add(tab_id);
						childList.add(this.userView.getUserName());
						childList.add("0");
						dao.delete(attarSql.toString(), childList);
					}
					else if("2".equals(infor_type)&&(this.frowset.getString("b0110")!=null)){
						StringBuffer attarSql=new StringBuffer("delete from t_wf_file where  objectid=? and tabid=? and create_user=? and ins_id=?");
						ArrayList childList=new ArrayList();						
						childList.add(this.frowset.getString("b0110"));
						childList.add(tab_id);
						childList.add(this.userView.getUserName());
						childList.add("0");
						dao.delete(attarSql.toString(), childList);
					}else if("3".equals(infor_type)&&(this.frowset.getString("e01a1")!=null)){
						StringBuffer attarSql=new StringBuffer("delete from t_wf_file where  objectid=? and tabid=? and create_user=? and ins_id=?");
						ArrayList childList=new ArrayList();						
						childList.add(this.frowset.getString("e01a1"));
						childList.add(tab_id);
						childList.add(this.userView.getUserName());
						childList.add("0");
						dao.delete(attarSql.toString(), childList);
					}
			    }
			    dao.update("delete from "+setname+"  where  submitflag=1 ");
			    chgLogBo.deleteChangeInfoNoInProcess(personList, "0", tab_id, "0", Integer.parseInt(infor_type));//删除变动日志信息。
			}
			else
			{  
				WF_Instance ins=new  WF_Instance(Integer.parseInt(tab_id),this.getFrameconn(),this.userView);
				TemplateParam tableParamBo = new TemplateParam(this.getFrameconn(), this.userView,Integer.parseInt(tab_id));
				int sp_mode = tableParamBo.getSp_mode();
				StringBuffer strsql=new StringBuffer("select * from templet_"+tab_id); 
				strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
				strsql.append(" and  task_id=xxx  and (state is null  or state=0)  and tab_id="+tab_id+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ");
				String[] temps=task_ids.toString().split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{ 
						ins.insertKqApplyTable(strsql.toString().replaceAll("xxx",temps[i]),tab_id,"0","10","templet_"+tab_id); //往考勤申请单中写入报批记录
						chgLogBo.deleteChangeInfoInProcess(strsql.toString().replaceAll("xxx",temps[i]),tab_id,Integer.parseInt(infor_type));//删除变动日志信息。
					}
				}	
				

				 
				{
					StringBuffer t_sql=new StringBuffer("select * from templet_"+tab_id+" where  ");
					t_sql.append("   exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
					t_sql.append(" and t_wf_task_objlink.task_id in ("+task_ids.substring(1)+")  and (t_wf_task_objlink.state is null  or t_wf_task_objlink.state=0)  and t_wf_task_objlink.tab_id="+tab_id+"  ");
					t_sql.append(" and t_wf_task_objlink.submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )   )");  
					ArrayList recordList=dao.searchDynaList(t_sql.toString());
					TemplateInterceptorAdapter.deleteRecords(recordList,new Integer(tab_id).intValue(),paramBo,this.userView); 
				} 
				
				dao.update("update t_wf_task_objlink set state=3   where task_id in ("+task_ids.substring(1)+")  and (state is null  or state=0)  and tab_id="+tab_id+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )");
				
				
				/**结束正在处理的任务*/
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{ 
						int taskid=Integer.parseInt(temps[i]);
						if(ins.isStartNode(taskid+"") && isAllSelectedTaskId(dao, tab_id, taskid)){
							isProcessEnd = true;//流程终止 lis 20160418
							ins.processEnd(Integer.valueOf(taskid), Integer.valueOf(tab_id), userView,0);					
						}else{
							String topic=tablebo.getRecordBusiTopic(taskid,0); 
							if(topic.indexOf(",共0")!=-1)
							{   
								int ins_id = this.isAllPriTask(dao, tab_id, taskid ,sp_mode);
								if(ins_id!=-1){
									//结束流程
									RecordVo ins_vo=new RecordVo("t_wf_instance");
									ins_vo.setInt("ins_id",ins_id);
									ins_vo=dao.findByPrimaryKey(ins_vo);
									if(ins_vo!=null){
										ins_vo.setDate("end_date",new Date());
										ins_vo.setString("finished","6");
										dao.updateValueObject(ins_vo);
									}
								}
								RecordVo task_vo=new RecordVo("t_wf_task");
								task_vo.setInt("task_id",taskid);
								task_vo=dao.findByPrimaryKey(task_vo);
								if(task_vo!=null)
								{
									topic=tablebo.getRecordBusiTopicByState(taskid,3);
									task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());				
									task_vo.setString("task_state",String.valueOf(NodeType.TASK_TERMINATE));
									task_vo.setString("task_topic", topic);
									
									String fullsender=this.userView.getUserFullName();
									if(fullsender==null|| "".equalsIgnoreCase(fullsender))
										fullsender=this.userView.getUserName(); 
									String sender=null;
									if(this.userView.getStatus()!=0)
										sender=this.userView.getDbname()+this.userView.getA0100();
									else
										sender=this.userView.getUserId();
									String appuser=task_vo.getString("appuser")+this.userView.getUserName()+",";
									task_vo.setString("appuser", appuser);
									task_vo.setString("a0100",sender);
									task_vo.setString("a0101",fullsender);
									task_vo.setString("content","撤销记录");
									if(ins_id!=-1){
										task_vo.setString("state", "06");
										task_vo.setString("task_type", String.valueOf(NodeType.END_NODE));
										task_vo.setString("actorname", "");
										task_vo.setString("sp_yj", "02");
									}
									dao.updateValueObject(task_vo);
								}
								/** 删除其它系统的待办任务 */
								PendingTask imip=new PendingTask();
								String pendingType="业务模板";  
								String pendingCode="HRMS-"+PubFunc.encrypt(taskid+""); 
								imip.updatePending("T",pendingCode,100,pendingType,userView);
							}
							else
								dao.update("update t_wf_task set task_topic='"+topic+"' where task_id="+taskid); 
						}
					}
				}
			}
			 //开始删除附件（审批过程中不能删除附件） 暂不支持
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 确认流程走过的节点是否都是当前节点之前一条线上的
	 * @param dao
	 * @param tabid
	 * @param task_id
	 * @param sp_mode 
	 * @return
	 * @throws GeneralException
	 */
	private int isAllPriTask(ContentDAO dao, String tabid, int task_id, int sp_mode) throws GeneralException {
		int ins_id = -1;
		recorsionCount = 0;
		try{
	            String sqlstr = "select * from t_wf_task where ins_id=(select ins_id from t_wf_task where task_id="+task_id+")";
	            this.frowset=dao.search(sqlstr);
	            int insid = 0;
	            int current_node_id = 0;
	            //走过的节点集合
	            HashSet usedNode = new HashSet();
	            while(this.frowset.next())
	            {
	                if(task_id==this.frowset.getInt("task_id")){//当前任务对应的节点
	                	current_node_id = this.frowset.getInt("node_id");
	                	insid = this.frowset.getInt("ins_id");
	                }else
	                	usedNode.add(this.frowset.getInt("node_id"));
	            }
	            if(sp_mode==1) {
	            	ins_id = insid==0?-1:insid;
	            }else {
	            	int current_node_id_ = current_node_id;
		            //其一条线上的节点集合
		            HashSet usedNode_ = new HashSet();
	            	//找出前一个节点 并与走过的节点对比 并把它放入一个set中
		            //递归 超过400次自动结束
	            	this.getpriNode(dao,usedNode_,current_node_id_,usedNode);
	            	if(recorsionCount<=400){
	            		if(usedNode_.size()<usedNode.size()){//证明有不是他一条线的
	    	            }else{
	    	            	//将流程结束
	    	            	ins_id = insid;
	    	            }
	            	}
	            }
	        }
	        catch(Exception ex){
	            ex.printStackTrace();
	            throw GeneralExceptionHandler.Handle(ex);
	        }
		 return ins_id;
	}
	/**
	 * 递归调用查找上一个节点
	 * @param dao
	 * @param usedNode_
	 * @param current_node_id_
	 * @param usedNode 
	 * @throws SQLException
	 */
	private void getpriNode(ContentDAO dao, HashSet usedNode_, int current_node_id_, HashSet usedNode) throws SQLException{
		RowSet rowSet = null;
		int pre_node = 0;
		if(recorsionCount>400){//超过400次自动退出递归
			return;
		}
		recorsionCount++;
		String sql = "select pre_nodeid from t_wf_transition where next_nodeid="+current_node_id_;
    	rowSet = dao.search(sql);
    	while(rowSet.next()){
    		pre_node = rowSet.getInt("pre_nodeid");//前一个节点
    		if(usedNode_.contains(pre_node))
    			return;
    		if(usedNode.contains(pre_node)){
    			current_node_id_ = pre_node;
    			usedNode_.add(pre_node);
    			getpriNode(dao,usedNode_,current_node_id_,usedNode);
    		}
    	}
	}

	/**   
	 * @Title: isSelectedTaskId   
	 * @Description: 判断单据里面的记录是否被选中，如果没有选中的，则后续不处理   
	 * @param @param dao
	 * @param @param tabid
	 * @param @param task_id
	 * @param @return
	 * @param @throws GeneralException 
	 * @return boolean 
	 * @author:wangrd   
	 * @throws   
	*/
	private boolean isAllSelectedTaskId(ContentDAO dao,String tabid,int task_id)throws GeneralException
    {
	    boolean b=false;
        try
        {
            String sqlstr = "select count(*) from templet_"+tabid 
                +" where  exists (select null from t_wf_task_objlink where templet_"
                +tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id "
                +"  and task_id="+task_id+"   and submitflag=0  and (state is null or  state=0 ) and ("
                +Sql_switcher.isnull("special_node","0")+"=0  or ( "
                +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
            this.frowset=dao.search(sqlstr);
            if(this.frowset.next())
            {
                if(this.frowset.getInt(1)==0)
                   b=true; 
            }
                    
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return b;
    }
	/**
	 * 删除签章相关数据
	 * @param dao
	 * @param signature
	 * @param paramBo 
	 */
	private void delSignatureXml(ContentDAO dao,String signature, TemplateParam paramBo) {
    	Document doc = null;
    	try {
    		int signatureType = paramBo.getTemplateModuleParam().getSignatureType();
			doc = PubFunc.generateDom(signature);
			Element ele = doc.getRootElement().getChild("record");
			String documentid = "";
			if(signatureType==1) {
				documentid = "BJCA";
			}else if(signatureType==3) {
				documentid = ele.getAttributeValue("DocuemntID");
			}
			List<Element> list = ele.getChildren();
			for (int i = 0; i < list.size(); i++) {
				Element e = list.get(i);
				if("item".equals(e.getName())){
					String SignatureID = e.getAttributeValue("SignatureID");
					String sql = "delete from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+documentid+"'";
					dao.delete(sql, new ArrayList());
					File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
				    if (!tempFile.exists()) {  
				    	 continue;
				    }  
				    tempFile.getAbsoluteFile().delete();
				}
			}
			
    	}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
