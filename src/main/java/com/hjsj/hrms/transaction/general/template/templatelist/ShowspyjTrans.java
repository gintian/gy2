package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:显示审批过程</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 13, 2010 2:58:05 PM</p> 
 *@author dengc
 *@version 5.0
 */
public class ShowspyjTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
				StringBuffer strsql=new StringBuffer();		
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				String taskid=(String)this.getFormHM().get("task_id");
				String ins_id=(String)this.getFormHM().get("ins_id");
				String tabid=(String)this.getFormHM().get("tabid");
				String type="";
				String infor_type = (String)this.getFormHM().get("infor_type");
				if(infor_type!=null&& "1".equals(infor_type))
					type="1";
				if(infor_type!=null&& "2".equals(infor_type))
					type="10";
				if(infor_type!=null&& "3".equals(infor_type))
					type="11";
				String task_id_pro="";
				String flag="";
				this.frowset=dao.search("select task_id_pro,flag from t_wf_task where task_id="+taskid);
				if(this.frowset.next())
				{
					if(this.frowset.getString("task_id_pro")!=null&&this.frowset.getString("task_id_pro").trim().length()>0)
						task_id_pro=this.frowset.getString("task_id_pro");
					if(this.frowset.getString("flag")!=null)
						flag=this.frowset.getString("flag");
				}
				String nodes ="";
				TemplateTableBo bo=new TemplateTableBo(this.frameconn,Integer.parseInt(tabid),this.userView);
				int mode = bo.getSp_mode();
				boolean def_flow_self=false; //是否是自定义审批流程
				if(bo.isBsp_flag()&&mode==0){
					WorkflowBo workflowBo=new WorkflowBo(this.getFrameconn(),Integer.parseInt(tabid),this.getUserView()); 
					HashMap map = workflowBo.getAllPreWFNode(Integer.parseInt(taskid), Integer.parseInt(ins_id));
					
					if(map!=null&&map.size()>0){
					Set set =	map.keySet();
					for(Iterator it=set.iterator();it.hasNext(); ){
						nodes += ","+it.next();
					}
					if(nodes.length()>0){
						this.frowset=dao.search("select task_id from t_wf_task where  ins_id="+ins_id+" and  node_id in ("+nodes.substring(1)+")");
					}
					nodes = "";
					while(this.frowset.next()){
						nodes+=","+this.frowset.getString("task_id");
					}
					task_id_pro = nodes;
					}
				}
				else if(mode==1&&bo.isDef_flow_self(Integer.parseInt(taskid)))
				{
					def_flow_self=true;
				}
				
				
				if(task_id_pro.length()==0)
				{	
					strsql.append("select task_id,a0101,end_date,sp_yj,content,state,task_state,actorname,bs_flag from t_wf_task where ins_id=");
					strsql.append(ins_id);
					strsql.append(" and (task_type='2' or task_type='1')  order by task_id,end_date");
				}
				else
				{
					
					if(def_flow_self)
					{
						strsql.append(" select task_id from t_wf_task where (pri_task_id in (select pri_task_id from t_wf_task where task_id in ("+task_id_pro.substring(1)+")) "
								+" and task_id not in ("+task_id_pro.substring(1)+")) or (ins_id="+ins_id+" and bs_flag='3') ");
						this.frowset=dao.search(strsql.toString());
						while(this.frowset.next())
						{
							task_id_pro=","+this.frowset.getString("task_id")+task_id_pro;
						}
						strsql.setLength(0);
						strsql.append("select task_id,a0101,  end_date,sp_yj,content,state,task_state,actorname,bs_flag  from t_wf_task  where  ins_id=");
						strsql.append(ins_id);
						strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro.substring(1)+")  order by task_id,end_date");
					}
					else
					{
						if("1".equals(flag))
						{
							strsql.append("select task_id,a0101,end_date,sp_yj,content,state,task_state,actorname,bs_flag from t_wf_task where ins_id=");
							strsql.append(ins_id);
							strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro.substring(1)+")  order by task_id,end_date");
						}
						else
						{
							strsql.append("select task_id,a0101,end_date,sp_yj,content,state,task_state,actorname,bs_flag from t_wf_task where ins_id=");
							strsql.append(ins_id);
							strsql.append(" and (task_type='2' or task_type='1')  and ( (task_id in ("+task_id_pro.substring(1)+") or task_id_pro like '%"+task_id_pro+"%') and task_id<="+taskid+" )  order by task_id,end_date");
						}
					}
				}
				String tableName="";
			String names="";
			  this.frowset=dao.search("select tabid from t_wf_instance where ins_id=(select ins_id from t_wf_task where task_id="+taskid+")");
			if( this.frowset.next())
				tabid= this.frowset.getString("tabid");
			 this.frowset=dao.search("select template_table.name from template_table where tabid="+tabid);
			if( this.frowset.next()){
					tableName= this.frowset.getString("name");
			}
			this.getFormHM().put("tableName", tableName);
//			 this.frowset=dao.search("select * from templet_"+tabid+" where task_id="+taskid );
//			while( this.frowset.next()){
//				if(type!=null&&(type.equals("10")||type.equals("11"))){
//					names+=","+ this.frowset.getString("codeitemdesc_1");
//				}else{
//					names+=","+ this.frowset.getString("a0101_1");
//				}
//			}
			String sqlwhere = " and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";
			
				switch(Sql_switcher.searchDbServer()){
				 case Constant.MSSQL:
			      {
//			    	  if(mode==0)
			    		  this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
//			    	  else
//			    	   this.frowset=dao.search("select * from templet_"+tabid+" where task_id in (select task_id from t_wf_task where task_id_pro+',' like '%,"+taskid+",%')");
			    	  while( this.frowset.next()){
							if(type!=null&&("10".equals(type)|| "11".equals(type))){
								names+=","+ this.frowset.getString("codeitemdesc_1");
							}else{
								names+=","+ this.frowset.getString("a0101_1");
							}
						}
						break;
			      }
				  case Constant.ORACEL:
				  case Constant.DB2:
				  { 
//					  if(mode==0)
			    		  this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
//			    	  else
//					   this.frowset=dao.search("select * from templet_"+tabid+" where task_id in (select task_id from t_wf_task where task_id_pro||',' like '%,"+taskid+",%')");
					  while( this.frowset.next()){
							if(type!=null&&("10".equals(type)|| "11".equals(type))){
								names+=","+ this.frowset.getString("codeitemdesc_1");
							}else{
								names+=","+ this.frowset.getString("a0101_1");
							}
						}	
						break;
				  }
				 
				  default:
					//  if(mode==0)
			    		  this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
//			    	  else
//					 this.frowset=dao.search("select * from templet_"+tabid+" where task_id in (select task_id from t_wf_task where task_id_pro+',' like '%,"+taskid+",%')");
				  while( this.frowset.next()){
						if(type!=null&&("10".equals(type)|| "11".equals(type))){
							names+=","+ this.frowset.getString("codeitemdesc_1");
						}else{
							names+=","+ this.frowset.getString("a0101_1");
						}
					}	
					break;
				}
				if("".equals(names)){
					this.frowset=dao.search("select * from templet_"+tabid+" where task_id="+taskid );
					while(this.frowset.next()){
						if(type!=null&&("10".equals(type)|| "11".equals(type))){
							names+=","+this.frowset.getString("codeitemdesc_1");
						}else{
							names+=","+this.frowset.getString("a0101_1");
						}
					}
				}
			if(names.length()>0)
				names=names.substring(1);
			this.getFormHM().put("a0101s", names);
			
			HashMap endtimemap = new HashMap();
				 this.frowset=dao.search(strsql.toString());
				ArrayList splist=new ArrayList();
				int i=0;
				while( this.frowset.next())
				{
					RecordVo task_vo=new RecordVo("t_wf_task");
					String task_id= this.frowset.getString("task_id");
					String bs_flag=this.frowset.getString("bs_flag")!=null?this.frowset.getString("bs_flag"):"1"; //1：待批 2：加签 3报备
					if(this.frowset.getString("a0101")!=null&&this.frowset.getString("a0101").trim().length()>0){
						task_vo.setString("a0101",this.frowset.getString("a0101"));
					}else{
						task_vo.setString("a0101",this.frowset.getString("actorname"));
					}
					task_vo.setString("bs_flag",bs_flag);
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
					task_vo.setString("content",Sql_switcher.readMemo( this.frowset,"content"));
					String temp=null;
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						temp=PubFunc.FormatDate( this.frowset.getTimestamp ("end_date"),"yyyy-MM-dd HH:mm");
					else
						temp=PubFunc.FormatDate(this.frowset.getDate("end_date"),"yyyy-MM-dd HH:mm");
					task_vo.setString("end_date",temp);

						temp=PubFunc.FormatDate(this.frowset.getDate("end_date"),"yyyy-MM-dd HH:mm");
					task_vo.setString("end_date",temp);


					//task_vo.setString("end_date",temp);
					if(i!=0&&( this.frowset.getString("state")==null|| this.frowset.getString("state").trim().length()==0))
						continue;
					 if(temp!=null&&temp.length()>0)
							endtimemap.put(""+i, temp);
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
				
				if(endtimemap.size()!=i){
					for(int j =0;j<splist.size();j++){
						RecordVo vo  = (RecordVo)splist.get(j);
						String end_date = vo.getString("end_date")==null?"": vo.getString("end_date");
						if(end_date.length()<=0){
							if(endtimemap!=null&&endtimemap.get((j+1)+"")!=null)
							vo.setString("end_date",""+endtimemap.get(j+1+""));
						}
					}
				}
				this.getFormHM().put("splist", splist);
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
