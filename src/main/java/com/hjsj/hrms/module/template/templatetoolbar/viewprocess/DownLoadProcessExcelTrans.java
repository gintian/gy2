package com.hjsj.hrms.module.template.templatetoolbar.viewprocess;

import com.hjsj.hrms.businessobject.general.template.ShowExcel;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:ShowExcelTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 26, 2008:2:25:49 PM</p> 
 *@author xgq
 *@version 1.0
 */
public class DownLoadProcessExcelTrans extends IBusiness {


	@Override
    public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
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
		
		String taskid=PubFunc.decrypt((String)this.getFormHM().get("task_id"));//PubFunc.decrypt((String)this.getFormHM().get("taskid"));
		taskid=taskid!=null&&taskid.trim().length()>0?taskid:"";
		
		if(taskid.indexOf(",")!=-1)
			taskid=taskid.split(",")[0]; 
		String infor_type = (String)this.getFormHM().get("infor_type");  //1：人员 2： 单位 3： 岗位 后台根据模板类型判断
		infor_type=infor_type!=null&&infor_type.trim().length()>0?infor_type:"";
		
		
		
		StringBuffer strsql=new StringBuffer();
		String ins_id="";
		HashMap map=null;
		String tableName="";
		String names="";
		int mode =0;
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			/**查询任务实例*/
			String task_id_pro="";
			String flag="";
			 this.frowset=dao.search("select task_id_pro,flag,ins_id from t_wf_task where task_id="+taskid);
			if(this.frowset.next())
			{
				if(this.frowset.getString("task_id_pro")!=null&&this.frowset.getString("task_id_pro").trim().length()>0)
					task_id_pro=this.frowset.getString("task_id_pro");
				if(this.frowset.getString("flag")!=null)
					flag=this.frowset.getString("flag");
				ins_id=this.frowset.getString("ins_id");
			}
			
			 
			this.frowset=dao.search("select template_table.name from template_table where tabid="+tabid);
			if(this.frowset.next()){
					tableName=this.frowset.getString("name");
			}
			  
			TemplateTableBo bo=new TemplateTableBo(this.frameconn,Integer.parseInt(tabid),this.userView);
		 
			boolean def_flow_self=false; //是否是自定义审批流程
			mode = bo.getSp_mode();
			if(mode==0){
				WorkflowBo workflowBo=new WorkflowBo(this.getFrameconn(),Integer.parseInt(tabid),this.getUserView()); 
				task_id_pro = workflowBo.getSubedTaskids(Integer.parseInt(taskid), Integer.parseInt(ins_id)); 
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
			String 	sqlwhere = " and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";	
			if("4".equals(return_flag)|| "3".equals(return_flag))
				sqlwhere="";
		
			int n=0;
			switch(Sql_switcher.searchDbServer()){
				 case Constant.MSSQL:
			      { 
			    		  this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )"); 
			    	      while(this.frowset.next()){
			    	    	  if(n>4){
			    	    		  names+="......";
			    	    		  break;
			    	    	  }
		    	    	      if("2".equals(infor_type)||"3".equals(infor_type)){
								  if(bo.getOperationtype()==5)
									  names+=","+this.frowset.getString("codeitemdesc_2");
								  else
									  names+=","+this.frowset.getString("codeitemdesc_1");
							  }else{
								  names+=","+this.frowset.getString("a0101_1");
							  }
			    	    	  n++;
						}
						break;
			      }
				  case Constant.ORACEL:
				  case Constant.DB2:
				  {  
			    		  this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
			    		  while(this.frowset.next()){
			    			    if(n>4){
				    	    		  names+="......";
				    	    		  break;
				    	    	  }
								if("2".equals(infor_type)||"3".equals(infor_type)){
									if(bo.getOperationtype()==5)
										names+=","+this.frowset.getString("codeitemdesc_2");
									else
										names+=","+this.frowset.getString("codeitemdesc_1");
								}else{
									names+=","+this.frowset.getString("a0101_1");
								}
								n++;
						}	
						break;
				  }
				 
				  default: 
			    		  this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
						  while(this.frowset.next()){
							  if(n>4){
			    	    		  names+="......";
			    	    		  break;
			    	    	  }
							  	if("2".equals(infor_type)||"3".equals(infor_type)){
							  		if(bo.getOperationtype()==5)
										names+=","+this.frowset.getString("codeitemdesc_2");
									else
										names+=","+this.frowset.getString("codeitemdesc_1");
								}else{
									names+=","+this.frowset.getString("a0101_1");
								}
								n++;
							}	
							break;
				}
				
				
			if("".equals(names)){
				this.frowset=dao.search("select * from templet_"+tabid+" where task_id="+taskid );
				while(this.frowset.next()){
					  if(n>4)
					  {
							names+="......";
							break;
					  }
					if("2".equals(infor_type)||"3".equals(infor_type)){
						if(bo.getOperationtype()==5)
							names+=","+this.frowset.getString("codeitemdesc_2");
						else
							names+=","+this.frowset.getString("codeitemdesc_1");
					}else{
						names+=","+this.frowset.getString("a0101_1");
					}
					n++;
				}
			}
			
			if(task_id_pro.length()==0)
			{	
				strsql.append("select task_id,a0101,end_date,start_date,sp_yj,content,state,task_state,actorname,nodename,task_type,t_wf_task.node_id,bs_flag from t_wf_task,t_wf_node where t_wf_task.node_id=t_wf_node.node_id  and  ins_id=");
				strsql.append(ins_id);
				strsql.append(" and (task_type='2' or task_type='1')   order by task_id,end_date");
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
					strsql.append("select task_id,a0101,end_date,start_date,sp_yj,content,state,task_state,actorname ,bs_flag,task_type,node_id  from t_wf_task  where  ins_id=");
					strsql.append(ins_id);
					strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro+")  order by task_id,end_date");
				}
				else
				{
                    //与t_wf_node 使用leftjoin，已删除的节点也可以查到，节点名称显示actorname wangrd 20160723 
                    strsql.append("select task_id,a0101,end_date,start_date,sp_yj,content,state,task_state,actorname,nodename,task_type,t_wf_task.node_id,bs_flag from t_wf_task left join t_wf_node on  t_wf_task.node_id=t_wf_node.node_id  where ins_id=");
                    strsql.append(ins_id);
				    strsql.append(" and (task_type='2' or task_type='1')  and task_id in ("+task_id_pro+")  ");
				    strsql.append(" and bs_flag<>'4' "); //20170523 邓灿 审批流程不应包含空节点
					strsql.append(" order by task_id,end_date");
				}
			
			} 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		ShowExcel show= new ShowExcel(this.getFrameconn());
		ArrayList column = new ArrayList();
		column.add("报送类型");
		column.add("姓名");
		column.add("审批节点");
		column.add("审批时间");
	//	column.add("意见");
		column.add("审批意见");

		ArrayList Infolist = getInfolist(strsql.toString(),map,mode);
		ArrayList columnlist = new ArrayList();
		columnlist.add("bs_flag");
		columnlist.add("a0101");
		columnlist.add("nodename");
		columnlist.add("end_date");
//		columnlist.add("sp_yj");
		columnlist.add("content");
		String type="1";
		if("2".equals(infor_type))
			type="10";
		else if("3".equals(infor_type))
			type="11";
		String excelfile=show.creatOpinionExcel(column,Infolist,columnlist,this.userView.getUserName(),tableName,names,type);
		/**安全平台改造，将导出的文件名进行加密，防止任意文件下载漏洞**/
		excelfile=PubFunc.encrypt(excelfile);
		//excelfile=SafeCode.encode(excelfile);
		
		this.getFormHM().put("excelfile",excelfile);

	}
	private ArrayList getInfolist(String sql,HashMap map,int mode){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		try {
		
			
			RowSet rs = dao.search(sql);
			int i=0;
			int  beginNodeid=0;
			HashMap endtimemap = new HashMap();
			while(rs.next()){
				String end_date="";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL){//liuyz 审批过程 没审批时 不显示
					if(rs.getTimestamp("end_date")==null){
						continue;
					}
				}
				else
				{
					if(rs.getDate("end_date")==null)
					{
						continue;
					}
				}
				LazyDynaBean bean = new LazyDynaBean();
				RecordVo task_vo=new RecordVo("t_wf_task");
				String task_id=rs.getString("task_id");
				String task_type=rs.getString("task_type");
				String bs_flag=rs.getString("bs_flag");
				int node_id=rs.getInt("node_id");
				if("1".equals(task_type))
					beginNodeid=rs.getInt("node_id");
				String bs_flagName = "";
				if(rs.isFirst())
					bs_flagName = ResourceFactory.getProperty("rsbd.wf.applyemp");
				else if("2".equals(bs_flag))
					bs_flagName = ResourceFactory.getProperty("rsbd.wf.jqemp");
				else if("3".equals(bs_flag))
					bs_flagName = ResourceFactory.getProperty("rsdb.wf.bbemp");
				else
					bs_flagName = ResourceFactory.getProperty("rsbd.task.applyemp");
				bean.set("bs_flag",bs_flagName);
				/*String desc="";
				if(bs_flag!=null&&bs_flag.equals("3"))
					desc="(报备)";
				else if(bs_flag!=null&&bs_flag.equals("2"))
					desc="(加签)";*/
				if(rs.getString("a0101")!=null&&rs.getString("a0101").trim().length()>0){
					//bean.set("a0101",rs.getString("a0101")+desc);
					bean.set("a0101",rs.getString("a0101"));
				}else{
					//bean.set("a0101",rs.getString("actorname")+desc);
					bean.set("a0101",rs.getString("actorname"));
				}
				
				
				
				if(mode==0&&beginNodeid!=node_id&&rs.getString("nodename")!=null&&rs.getString("nodename").length()>0)
					bean.set("nodename",rs.getString("nodename"));
				else if(beginNodeid==node_id)
                    bean.set("nodename","发起人");
                else if(mode==0&&beginNodeid!=node_id&&rs.getString("actorname")!=null&&rs.getString("actorname").length()>0)
                    bean.set("nodename",rs.getString("actorname")+"");
				else
				{
					if(mode==1)
					{
						if(bs_flag!=null&& "3".equals(bs_flag))
							bean.set("nodename","报备人");
						else
							bean.set("nodename","审批人");
					}
					else
						bean.set("nodename","");
				}
				//bean.set("end_date",PubFunc.FormatDate(rs.getDate("end_date"),"yyyy.MM.dd HH:mm"));
				//AdminCode.getCodeName("30",PubFunc.nullToStr(rs.getString("sp_yj")));
				if(Sql_switcher.searchDbServer()==Constant.ORACEL){
					bean.set("end_date",PubFunc.FormatDate(rs.getTimestamp("end_date"),"yyyy.MM.dd HH:mm"));
					if(rs.getTimestamp("start_date")!=null&&rs.getTimestamp("start_date").toString().length()>0){
						end_date = PubFunc.FormatDate(rs.getTimestamp("start_date"),"yyyy.MM.dd HH:mm");
					}
					
				}
				else{
					bean.set("end_date",PubFunc.FormatDate(rs.getDate("end_date"),"yyyy.MM.dd HH:mm"));
					if(rs.getTimestamp("start_date")!=null&&rs.getTimestamp("start_date").toString().length()>0){
						end_date = PubFunc.FormatDate(rs.getDate("start_date"),"yyyy.MM.dd HH:mm");
					}
				}
				if(rs.getString("task_state")!=null&&"4".equals(rs.getString("task_state"))){
					if("08".equals(rs.getString("state"))){
						bean.set("sp_yj","系统报批");
					}else if("07".equals(rs.getString("state"))){
						bean.set("sp_yj","系统驳回");
					}else
						bean.set("sp_yj",AdminCode.getCodeName("30",PubFunc.nullToStr(rs.getString("sp_yj"))));
				}else{
					bean.set("sp_yj",AdminCode.getCodeName("30",PubFunc.nullToStr(rs.getString("sp_yj"))));
				}
				bean.set("content",PubFunc.nullToStr(rs.getString("content")));
				if(i!=0&&(rs.getString("state")==null||rs.getString("state").trim().length()==0))
					continue;
				if(end_date!=null&&end_date.length()>0)
					endtimemap.put(""+i, end_date);
				if(i!=0&&((rs.getString("a0101")==null||rs.getString("a0101").trim().length()==0)&&!"4".equals(rs.getString("task_state"))))
					continue;
				 //最后一个审批人不显示：状态不为结束，别且结束日期不为空。
				if(rs.isLast()){
					if(!"5".equals(rs.getString("task_state"))&&bean.get("end_date")!=null&&bean.get("end_date").toString().length()<=0)
						continue;
				}
				i++;
				list.add(bean);
				
			}
			if(endtimemap.size()!=i){
				for(int j =0;j<list.size();j++){
					LazyDynaBean bean  = (LazyDynaBean)list.get(j);
					String end_date = bean.get("end_date")==null?"": (String)bean.get("end_date");
					if(end_date.length()<=0&&endtimemap.get(j+1+"")!=null)
						bean.set("end_date",""+endtimemap.get(j+1+""));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	 

}
