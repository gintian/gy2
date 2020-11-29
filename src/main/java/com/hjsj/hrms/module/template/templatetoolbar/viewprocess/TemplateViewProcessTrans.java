/**
 * 
 */
package com.hjsj.hrms.module.template.templatetoolbar.viewprocess;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
* @Description: 审批环节查询
* @author gaohy
* @date Jan 30, 2016 4:06:42 PM
* @version V7x
 */
public class TemplateViewProcessTrans extends IBusiness {
	 
	@Override
    public void execute() throws GeneralException {
		
	    TemplateFrontProperty frontProperty =new TemplateFrontProperty(this.getFormHM());            
	    String taskid = frontProperty.getTaskId();         
		String tabid=(String)this.getFormHM().get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		
		/**调用的模块标识、返回模块标识
		 * 1：返回待办任务界面
		 * 2：返回已办任务界面
		 * 3：返回我的申请界面
		 * 4：返回任务监控界面
		 * 5:返回业务申请界面
		 * 6:返回到业务分类界面
		 * 。7-10。暂时保留
		 * 11.首页待办
		 * 12、首页待办列表
		 * 13、关闭（来自第三方系统或邮件），提交后自动关闭
		 * 14、无关闭、返回按钮，提交后不跳转
		*/
		
		String return_flag=(String)this.getFormHM().get("return_flag"); 
		
		//String taskid=PubFunc.decrypt((String)this.getFormHM().get("task_id"));//PubFunc.decrypt((String)this.getFormHM().get("taskid"));
		taskid=taskid!=null&&taskid.trim().length()>0?taskid:"";
		if(taskid.indexOf(",")!=-1)
			taskid=taskid.split(",")[0]; 
		String infor_type = (String)this.getFormHM().get("infor_type");  //1：人员 2： 单位 3： 岗位 后台根据模板类型判断
		infor_type=infor_type!=null&&infor_type.trim().length()>0?infor_type:"";
		   
		String isDelete = (String)this.getFormHM().get("isDelete"); 
		String 	sqlwhere = " and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";	
		if("4".equals(return_flag)|| "3".equals(return_flag))
			sqlwhere="";
		 
		String ins_id="";
		StringBuffer strsql=new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String task_id_pro=""; 
			this.frowset=dao.search("select task_id_pro,flag,ins_id from t_wf_task where task_id="+taskid);
			if(this.frowset.next())
			{
				ins_id=this.frowset.getString("ins_id");
				if(this.frowset.getString("task_id_pro")!=null&&this.frowset.getString("task_id_pro").trim().length()>0)
					task_id_pro=this.frowset.getString("task_id_pro"); 
			}
			String tableName="";
			String names=""; 
			this.frowset=dao.search("select template_table.name from template_table where tabid="+tabid);
			if(this.frowset.next()){
					tableName=this.frowset.getString("name");
			}
			this.getFormHM().put("tableName", tableName);
		 
			String nodes ="";
			TemplateTableBo bo=new TemplateTableBo(this.frameconn,Integer.parseInt(tabid),this.userView);
		 
			int mode = bo.getSp_mode();/**审批模式=0自动流转，=1手工指派*/
			boolean def_flow_self=false; //是否是自定义审批流程
			if(mode==0){
				WorkflowBo workflowBo=new WorkflowBo(this.getFrameconn(),Integer.parseInt(tabid),this.getUserView());  
				task_id_pro = workflowBo.getSubedTaskids(Integer.parseInt(taskid), Integer.parseInt(ins_id),return_flag,isDelete); 
			}
			else {
				//手工指派
				StringBuffer sb = new StringBuffer("");
				sb.append("select task_id from t_wf_task where ins_id="+ins_id+" and task_state='5'");
				this.frowset=dao.search(sb.toString());
				int i=0;
				String task_id_pro_hand = "";
				while(this.frowset.next()){
					String taskid_ = this.frowset.getString("task_id");
					if(i==0)
						task_id_pro_hand+=taskid_;
					else
						task_id_pro_hand+=","+taskid_;
					i++;
				}
				if(task_id_pro_hand.length()>0)
					task_id_pro = task_id_pro_hand;
				if(bo.isDef_flow_self(Integer.parseInt(taskid)))
					def_flow_self=true;
			}
			int n=0;
			/**
			 * 调入模板非调入模板 lis 20160906
			 */
			String name = null;
			if("2".equals(infor_type)|| "3".equals(infor_type)){//岗位和单位
				if(bo.getOperationtype() == 5) {//调入模板
					name = "codeitemdesc_2";
				} else {
					name = "codeitemdesc_1";
				}
			}else{//人事异动
				if(bo.getOperationtype() == 0) {//调入模板
					name = "a0101_2";
				} else {
					name = "a0101_1";
				}
			}
			switch(Sql_switcher.searchDbServer()){
				 case Constant.MSSQL:
			      { 
			    		this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
			    		while(this.frowset.next()){
							if(n>4)
								names+="......";
			    			/*if(infor_type.equals("2")||infor_type.equals("3")){
			    				if (bo.getOperationtype() == 5) {
			    					name = "codeitemdesc_2";
			    				} else {
			    					name = "codeitemdesc_1";
			    				}
								names+=","+this.frowset.getString(name);
							}else{
								
								names+=","+this.frowset.getString("a0101_1");
							}*/
			    			names+=","+this.frowset.getString(name);
							n++;
						}
						break;
			      }
				  case Constant.ORACEL:
				  case Constant.DB2:
				  {  
				    	this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
				    	while(this.frowset.next()){
				    		if(n>4)
								names+="......";
				    		/*if("2".equals(infor_type)||"3".equals(infor_type)){
								names+="，"+this.frowset.getString("codeitemdesc_1");
							}else{
								names+="，"+this.frowset.getString("a0101_1");
							}*/
				    		names+=","+this.frowset.getString(name);
				    		n++;
						}	
						break;
				  }
				 
				  default: 
						  this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
						  while(this.frowset.next()){
							   if(n>4)
									names+="......";
							    /*if(infor_type.equals(2)||infor_type.equals(3)){
									names+=","+this.frowset.getString("codeitemdesc_1");
								}else{
									names+=","+this.frowset.getString("a0101_1");
								}*/
							    names+=","+this.frowset.getString(name);
								n++;
							}	
							break;
			}
			
			if("".equals(names)){
				this.frowset=dao.search("select * from templet_"+tabid+" where task_id="+taskid );
				while(this.frowset.next()){
					if(n>4)
							names+="......";
					/*if(infor_type.equals(2)||infor_type.equals(3)){
						names+=","+this.frowset.getString("codeitemdesc_1");
					}else{
						names+=","+this.frowset.getString("a0101_1");
					}*/
					names+=","+this.frowset.getString(name);
					n++;
				}
			} 
			if(names.length()>0)
				names=names.substring(1);
			this.getFormHM().put("a0101s", names);
			
			
			String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				format_str="yyyy-MM-dd hh24:mi"; 
			if(task_id_pro.length()==0)
			{	
				strsql.append("select task_id,a0101 ,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname,nodename,bs_flag,task_type,t_wf_task.node_id from t_wf_task,t_wf_node where t_wf_task.node_id=t_wf_node.node_id  and   ins_id=");
				strsql.append(ins_id);
				strsql.append(" and (task_type='2' or task_type='1')  order by task_id,end_date");
			}
			else
			{
				if(mode==0){//自动才走这里
					strsql.append(" select task_id from t_wf_task where ( pri_task_id in (select pri_task_id from t_wf_task where task_id in ("+task_id_pro.substring(1)+")) "
							+" and task_id not in ("+task_id_pro.substring(1)+") )");
					strsql.append(" union select task_id from t_wf_task where (ins_id="+ins_id+" and bs_flag='3') ");
					this.frowset=dao.search(strsql.toString());
					while(this.frowset.next())
					{
						task_id_pro=","+this.frowset.getString("task_id")+task_id_pro;
					}
				}
				strsql.setLength(0);
				task_id_pro = task_id_pro.startsWith(",")?task_id_pro.substring(1):task_id_pro;
				if(def_flow_self)
				{
					strsql.append("select task_id,a0101,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname ,bs_flag,task_type,node_id  from t_wf_task  where  ins_id=");
					strsql.append(ins_id);
					strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro+")  order by task_id,end_date");
				}
				else
				{
                    //与t_wf_node 使用leftjoin，已删除的节点也可以查到，节点名称显示actorname wangrd 20160723 
                    strsql.append("select task_id,a0101,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname,nodename,bs_flag,task_type,t_wf_task.node_id from t_wf_task left join t_wf_node on  t_wf_task.node_id=t_wf_node.node_id  where ins_id=");
                    strsql.append(ins_id);
					strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro+") ");
					strsql.append(" and bs_flag<>'4' "); //20170523 邓灿 审批流程不应包含空节点
					strsql.append(" order by task_id,end_date");
				}
			}
			
			this.frowset=dao.search(strsql.toString());
			ArrayList splist=new ArrayList();//审批过程人员信息
			int i=0;
			int  beginNodeid=0;
			HashMap endtimemap = new HashMap();
			 
			while(this.frowset.next())
			{
				if(this.frowset.getString("end_date")==null){//没审批时 不显示
					continue;
				}
				String end_date="";
				String task_type=this.frowset.getString("task_type");
				int node_id=this.frowset.getInt("node_id");
				if("1".equals(task_type))
					beginNodeid=this.frowset.getInt("node_id");
				String bs_flag=this.frowset.getString("bs_flag")!=null?this.frowset.getString("bs_flag"):"1"; //1：待批 2：加签 3报备
				RecordVo task_vo=new RecordVo("t_wf_task");
				task_vo.setString("bs_flag",bs_flag);
				String task_id=this.frowset.getString("task_id");
				if(this.frowset.getString("a0101")!=null&&this.frowset.getString("a0101").trim().length()>0){
					task_vo.setString("a0101",this.frowset.getString("a0101"));
				}else{
					task_vo.setString("a0101",this.frowset.getString("actorname"));
				}
				
				if(def_flow_self)
				{
					if(beginNodeid==node_id)
						task_vo.setString("appuser","发起人");
					else if("3".equals(bs_flag))
						task_vo.setString("appuser","报备人");
					else
						task_vo.setString("appuser","审批人");
				}
				else
				{
					if(mode==0&&beginNodeid!=node_id&&this.frowset.getString("nodename")!=null&&this.frowset.getString("nodename").length()>0)
						task_vo.setString("appuser","  "+this.frowset.getString("nodename")+"");
					else if(beginNodeid==node_id)
						task_vo.setString("appuser","发起人");
                    else if(mode==0&&beginNodeid!=node_id&&this.frowset.getString("actorname")!=null&&this.frowset.getString("actorname").length()>0)
                        task_vo.setString("appuser","  "+this.frowset.getString("actorname")+"");
                    else
						task_vo.setString("appuser","审批人");
				}
				
				task_vo.setString("task_state",this.frowset.getString("task_state"));
				if(this.frowset.getString("task_state")!=null&&"4".equals(this.frowset.getString("task_state"))){
					if("08".equals(this.frowset.getString("state"))){
						task_vo.setString("appuser","系统报批");	
					}else if("07".equals(this.frowset.getString("state"))){
						task_vo.setString("appuser","系统驳回");	
					}else
						task_vo.setString("appuser","");
				}else{
				task_vo.setString("sp_yj",this.frowset.getString("sp_yj"));
				}
 
				task_vo.setString("content",Sql_switcher.readMemo(this.frowset,"content").replace("\n", "<br>").replace(" ", "&nbsp;"));
				String temp=null; 
				if(this.frowset.getString("end_date")!=null)
					task_vo.setString("end_date",this.frowset.getString("end_date"));
				else
					task_vo.setString("end_date","");
				
					if(Sql_switcher.searchDbServer()==Constant.ORACEL){
						end_date=PubFunc.FormatDate(this.frowset.getTimestamp ("start_date"),"yyyy-MM-dd HH:mm");
					}
					else{
						end_date=PubFunc.FormatDate(this.frowset.getDate("start_date"),"yyyy-MM-dd HH:mm");
					} 
				if(i!=0&&(this.frowset.getString("state")==null||this.frowset.getString("state").trim().length()==0))
					continue;
				 if(end_date!=null&&end_date.length()>0)
						endtimemap.put(""+i, end_date);
				if(i!=0&&((this.frowset.getString("a0101")==null||this.frowset.getString("a0101").trim().length()==0)&&!"4".equals(this.frowset.getString("task_state"))))
					continue;
				 //最后一个审批人不显示：状态不为结束，别且结束日期不为空。
				if(this.frowset.isLast()){
					if(!"5".equals(this.frowset.getString("task_state"))&&task_vo.getString("end_date").length()<=0)
						continue;
				}
				i++;
	            splist.add(task_vo);
	          
			}
			ArrayList spData = new ArrayList();
			
				for(int j =0;j<splist.size();j++){
					RecordVo vo  = (RecordVo)splist.get(j);
					String bs_flag = vo.getString("bs_flag")==null?"": vo.getString("bs_flag");
					String appuser = vo.getString("appuser")==null?"": vo.getString("appuser");
					String a0101 = vo.getString("a0101")==null?"": vo.getString("a0101");
					String end_date = vo.getString("end_date")==null?"": vo.getString("end_date");
					String content = vo.getString("content")==null?"": vo.getString("content");
					ArrayList spItems = new ArrayList();
					if(j == 0)
						bs_flag = ResourceFactory.getProperty("rsbd.wf.applyemp");
					else if("2".equals(bs_flag))
						bs_flag = ResourceFactory.getProperty("rsbd.wf.jqemp");
					else if("3".equals(bs_flag))
						bs_flag = ResourceFactory.getProperty("rsdb.wf.bbemp");
					else
						bs_flag = ResourceFactory.getProperty("rsbd.task.applyemp");
						
					spItems.add(bs_flag);
					spItems.add(a0101);
					spItems.add(appuser);
					spItems.add(end_date);
					spItems.add(content);
					spData.add(spItems);
				}
			this.getFormHM().put("spData", spData);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
