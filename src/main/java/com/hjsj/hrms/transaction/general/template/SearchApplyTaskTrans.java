/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * <p>Title:</p>
 * <p>Description:审批环节查询</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 28, 20062:08:24 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchApplyTaskTrans extends IBusiness {
	 
	
	/**
	 * 分析此实例，是否为当前用户发起的申请
	 * @param ins_id
	 */
/*	private String isStartNode(String task_id,int sp_mode,String tabid)
	{
		String startflag="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer buf=new StringBuffer();
			buf.append("select actorid from t_wf_instance where ins_id=(select ins_id from t_wf_task where task_id="+task_id+")"); 
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
			{
				*//**
				 * 申请人编码
				 * 自助平台用户:应用库前缀+人员编码
				 * 业务平台用户：operuser中的账号
				 *//*
				String applyobj=rset.getString("actorid");//   ins_vo.getString("actorid");
				String a0100=this.userView.getDbname()+this.userView.getA0100();
				String usrname=this.userView.getUserId();
				if(applyobj!=null&&(applyobj.equalsIgnoreCase(a0100)||applyobj.equalsIgnoreCase(usrname)))
					startflag="1";
				if(startflag.equals("1")&&task_id!=null&&!task_id.equals("0")&&task_id.trim().length()>0)//&&sp_mode==0)
				{
					rset=dao.search("select nodetype from t_wf_node where tabid="+tabid+" and node_id=(select node_id from t_wf_task where task_id="+task_id+")");
					if(rset.next())
					{
						 
						if(!rset.getString("nodetype").equals("1")&&!rset.getString("nodetype").equals("9"))
							startflag="0";
					}
				}
			}
			if(rset!=null)
				rset.close();
		}
		catch(Exception ex)
		{
			;
		}
		
		
		return startflag;
	}*/
	
	
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String ins_id=(String)this.getFormHM().get("ins_id");
		String taskid=(String)this.getFormHM().get("taskid");//PubFunc.decrypt((String)this.getFormHM().get("taskid"));
		String tabid=(String)this.getFormHM().get("tabid");
		String type =(String)this.getFormHM().get("type");
        String cur_task_id =(String)hm.get("cur_task_id");
        String cur_ins_id =(String)hm.get("cur_ins_id");
        if (cur_task_id!=null){//批量审批 需要定位当前定位的人员所在单据号。
            hm.remove("cur_task_id");
            if (cur_task_id.length()>0){
                taskid= cur_task_id;
            }
        }
        if (cur_ins_id!=null){//批量审批 需要定位当前定位的人员所在单据号。
            hm.remove("cur_ins_id");
            if (cur_ins_id.length()>0){
                ins_id= cur_ins_id;
            }
        }
		
		if(type==null|| "".equals(type)){
			String infor_type = (String)this.getFormHM().get("infor_type");
			if(infor_type!=null&& "1".equals(infor_type))
				type="1";
			if(infor_type!=null&& "2".equals(infor_type))
				type="10";
			if(infor_type!=null&& "3".equals(infor_type))
				type="11";
		}
		String sqlwhere = " and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";
		boolean fromYQ=true;//表明不是审批过程中点进来的
		if(hm.get("from")!=null&& "rwjk".equalsIgnoreCase((String)hm.get("from")))
		{
			/**安全平台改造，将加密后的参数解密回来任务监控只有从四个页签点击连接进来是才会有加密的taskid**/
			tabid=(String)hm.get("tabid");
			if(hm.get("taskid")!=null){
				if(hm.get("def_flow_self")!=null){//如果是点击自定义审批流程，这个结点中的taskid 是没有加密的taskid
					taskid=(String)hm.get("taskid");
				}else{//从任务监控中点击审批过程  taskid是加密了的
					/**安全平台改造，将加密后的参数解密回来已办任务只有从四个页签点击连接进来是才会有加密的taskid**/
					taskid=PubFunc.decrypt((String)hm.get("taskid"));
					
					HashMap templateMap=new HashMap();
					templateMap.put(taskid, PubFunc.encrypt(taskid)); 
					this.userView.getHm().put("templateMap", templateMap);//将正在使用的task_id放在userview中
					
					fromYQ=false;
				}
			}	
			hm.remove("from");
			sqlwhere="";
		}
		if(hm.get("from")!=null&& "yprw".equalsIgnoreCase((String)hm.get("from")))//来自于已批任务的审批过程
		{
			
			tabid=(String)hm.get("tabid");
			if(hm.get("taskid")!=null){//已办任务,审批过程中的taskid是加密的taskid
					taskid=PubFunc.decrypt((String)hm.get("taskid"));
					
					HashMap templateMap=new HashMap();
					templateMap.put(taskid, PubFunc.encrypt(taskid)); 
					this.userView.getHm().put("templateMap", templateMap);//将正在使用的task_id放在userview中
			}
			fromYQ=false;
			hm.remove("from");
		}
		String sp_batch=(String)this.getFormHM().get("sp_batch");
		/**不是从人事异动四个页签中点进来，那么taskid是没有加密的,那么就需要判断是否在templateMap中，若不存在就是被修改了**/
		/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
		if(fromYQ){
			HashMap tempalteMap=(HashMap) this.userView.getHm().get("templateMap");	
			if(tempalteMap!=null&&!tempalteMap.containsKey(taskid)&&!"1".equals(sp_batch)){//批量审批的话不需要判断是否存在这里面
				throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			}
		}
		*/
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			Pattern pattern = Pattern.compile("[0-9]+");  //20161210 dengcan 安全验证
			if(pattern.matcher(taskid).matches())
			{
				ArrayList valueList=new ArrayList();
				valueList.add(new Integer(taskid));
				this.frowset=dao.search("select ins_id from t_wf_task where task_id=?",valueList);
				if(this.frowset.next())
					ins_id=this.frowset.getInt("ins_id")+"";
				else
					ins_id="-999999";
			}
		}
		catch(Exception ee)
		{
			
		}
		
		
		
		String ins_ids=(String)this.getFormHM().get("ins_ids");
		
		
		StringBuffer strsql=new StringBuffer();
		try
		{
			
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
			String tableName="";
			String names="";
			 this.frowset=dao.search("select tabid from t_wf_instance where ins_id=(select ins_id from t_wf_task where task_id="+taskid+")");
			if(this.frowset.next())
				tabid=this.frowset.getString("tabid");
			this.frowset=dao.search("select template_table.name from template_table where tabid="+tabid);
			if(this.frowset.next()){
					tableName=this.frowset.getString("name");
			}
			this.getFormHM().put("tableName", tableName);
			/*
			this.frowset=dao.search("select * from templet_"+tabid+" where task_id="+taskid );
			while(this.frowset.next()){
				if(type!=null&&(type.equals("10")||type.equals("11"))){
					names+=","+this.frowset.getString("codeitemdesc_1");
				}else{
					names+=","+this.frowset.getString("a0101_1");
				}
			}
			*/
			
			String nodes ="";
			TemplateTableBo bo=new TemplateTableBo(this.frameconn,Integer.parseInt(tabid),this.userView);
			if(bo.get_static()==10||bo.get_static()==11)
				type=String.valueOf(bo.get_static());
			int mode = bo.getSp_mode();/**审批模式=0自动流转，=1手工指派*/
			boolean def_flow_self=false; //是否是自定义审批流程
			if(mode==0){
				WorkflowBo workflowBo=new WorkflowBo(this.getFrameconn(),Integer.parseInt(tabid),this.getUserView()); 
				
				task_id_pro = workflowBo.getSubedTaskids(Integer.parseInt(taskid), Integer.parseInt(ins_id));
			 
			}
			else if(mode==1&&bo.isDef_flow_self(Integer.parseInt(taskid)))
			{
				def_flow_self=true;
			}
		
				switch(Sql_switcher.searchDbServer()){
				 case Constant.MSSQL:
			      { 
			    		this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
			    		while(this.frowset.next()){
							if(type!=null&&("10".equals(type)|| "11".equals(type))){
								names+=","+this.frowset.getString("codeitemdesc_1");
							}else{
								names+=","+this.frowset.getString("a0101_1");
							}
						}
						break;
			      }
				  case Constant.ORACEL:
				  case Constant.DB2:
				  {  
				    	this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
				    	while(this.frowset.next()){
							if(type!=null&&("10".equals(type)|| "11".equals(type))){
								names+=","+this.frowset.getString("codeitemdesc_1");
							}else{
								names+=","+this.frowset.getString("a0101_1");
							}
						}	
						break;
				  }
				 
				  default:
				//	  if(mode==0)
						  this.frowset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum "+sqlwhere+" )");
				 //   else
				 //  	  this.frowset=dao.search("select * from templet_"+tabid+" where task_id in (select task_id from t_wf_task where task_id_pro+',' like '%,"+taskid+",%')");
				  while(this.frowset.next()){
						if(type!=null&&("10".equals(type)|| "11".equals(type))){
							names+=","+this.frowset.getString("codeitemdesc_1");
						}else{
							names+=","+this.frowset.getString("a0101_1");
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
				if(def_flow_self)
				{
					strsql.append(" select task_id from t_wf_task where ( pri_task_id in (select pri_task_id from t_wf_task where task_id in ("+task_id_pro.substring(1)+")) "
							+" and task_id not in ("+task_id_pro.substring(1)+") ) or (ins_id="+ins_id+" and bs_flag='3') ");
					this.frowset=dao.search(strsql.toString());
					while(this.frowset.next())
					{
						task_id_pro=","+this.frowset.getString("task_id")+task_id_pro;
					}
					strsql.setLength(0);
					strsql.append("select task_id,a0101,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname ,bs_flag,task_type,node_id  from t_wf_task  where  ins_id=");
					strsql.append(ins_id);
					strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro.substring(1)+")  order by task_id,end_date");
				}
				else
				{
                    //与t_wf_node 使用leftjoin，已删除的节点也可以查到，节点名称显示actorname wangrd 20160723 
                    strsql.append("select task_id,a0101,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname,nodename,bs_flag,task_type,t_wf_task.node_id from t_wf_task left join t_wf_node on  t_wf_task.node_id=t_wf_node.node_id  where ins_id=");
                    strsql.append(ins_id);
					strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro.substring(1)+")  order by task_id,end_date");
				}
			}
			
			this.frowset=dao.search(strsql.toString());
			ArrayList splist=new ArrayList();
			int i=0;
			int  beginNodeid=0;
			HashMap endtimemap = new HashMap();
			 
			while(this.frowset.next())
			{
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
		/*		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					temp=PubFunc.FormatDate(this.frowset.getTimestamp ("end_date"),"yyyy-MM-dd HH:mm:ss");
				else
					temp=PubFunc.FormatDate(this.frowset.getDate("end_date"),"yyyy-MM-dd HH:mm:ss");
				*/
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
				//task_vo.setString("end_date",temp);
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
			/**  手工指派 是操作人发起的任务，如果是弹出的提示框应将批准按钮去掉   */
			if(ins_id!=null&&!"0".equals(ins_id))
			{
				this.frowset=dao.search("select actor_type,actorid,actorname from t_wf_instance where ins_id="+ins_id);
				if(this.frowset.next())
				{
					String actor_type=this.frowset.getString("actor_type");
					String actorid=this.frowset.getString("actorid");
					if(actor_type!=null&& "1".equals(actor_type)&&actorid!=null&&actorid.trim().length()>3&&actorid.substring(3).equals(this.userView.getA0100()))
					{
						this.getFormHM().put("starttask", "1"); 
					}
					else if(actorid!=null&&actorid.trim().length()>0&&actorid.equalsIgnoreCase(this.userView.getUserName()))
					{
						this.getFormHM().put("starttask", "1"); 
					}
					else
						this.getFormHM().put("starttask", "0"); 
				}
				else
					this.getFormHM().put("starttask", "0");
			}
			else 
				this.getFormHM().put("starttask", "0");
			
			if(taskid!=null&&!"0".equals(taskid))
			{
				if("1".equals(bo.isStartNode(ins_id,taskid,tabid,mode)))
					this.getFormHM().put("startflag","1");  //是起始节点，手工指派
				else
					this.getFormHM().put("startflag","0");
			}
			
			if(ins_ids!=null&&ins_ids.trim().length()>0&&ins_ids.indexOf(",")!=-1&&ins_ids.split(",").length>2)
				this.getFormHM().put("startflag","0");
			
			this.getFormHM().put("curryjlist",splist);
			
			
			ArrayList rejectObjList=getRejectObjList(taskid,dao,ins_id,tabid);
			this.getFormHM().put("rejectObjList", rejectObjList);
			//HashMap templateMap = new HashMap();//存放在userview中，用于确认是否是由后台传向前台的task_id,如果不是，不允许执行
			//templateMap.put(taskid, PubFunc.encrypt(taskid));这里更新templateMap了,批量审批时会出错
			//this.userView.getHm().put("templateMap",templateMap);
			this.getFormHM().put("taskid",taskid);
			this.getFormHM().put("ins_id",ins_id);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	public ArrayList getRejectObjList(String taskid,ContentDAO dao,String ins_id,String tabid)
	{
		ArrayList list=new ArrayList();
//		try
//		{
//			if(taskid!=null&&!taskid.equals("0"))
//			{
//				RecordVo task_vo=new RecordVo("t_wf_task");
//				task_vo.setInt("task_id",Integer.parseInt(taskid));
//				task_vo=dao.findByPrimaryKey(task_vo);
//				int node_id=task_vo.getInt("node_id");
//				String pri_task_id=String.valueOf(task_vo.getInt("pri_task_id"));
//				
//				/**审批模式=0自动流转，=1手工指派*/
//				int sp_mode=0;
//				RecordVo vo=new RecordVo("Template_table");
//				vo.setInt("tabid",Integer.parseInt(tabid));
//				vo=dao.findByPrimaryKey(vo);
//				if(vo!=null)
//				{
//					String sxml=vo.getString("ctrl_para");
//					if(sxml.length()>0)
//					{
//						StringReader reader=new StringReader(sxml);
//						Document doc=saxbuilder.build(reader);
//						
//						String xpath="/params/sp_flag";
//						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
//						List childlist=findPath.selectNodes(doc);			
//						if(childlist!=null&&childlist.size()>0)
//						{
//							Element element=(Element)childlist.get(0);
//							sp_mode=Integer.parseInt((String)element.getAttributeValue("mode"));
//						}
//					}
//				}
//				
//				String sql0="select td.role_id,td.seqnum,tt.pri_task_id from t_wf_task_datalink td,t_wf_task tt where td.task_id=tt.task_id and tt.node_id<>"+node_id+"  and tt.task_id="+pri_task_id+" and td.ins_id="+ins_id+" and td.task_id="+pri_task_id;
//				if(sp_mode==1)
//					sql0="select td.role_id,td.seqnum,tt.pri_task_id from t_wf_task_datalink td,t_wf_task tt where td.task_id=tt.task_id    and tt.task_id="+pri_task_id+" and td.ins_id="+ins_id+" and td.task_id="+pri_task_id;
//				RowSet rowSet=dao.search(sql0);
//				String role_id="";
//				String seqnum="";
//				String p_pri_task_id="";
//				if(rowSet.next())
//				{
//					role_id=rowSet.getString("role_id");
//					seqnum=rowSet.getString("seqnum");
//					p_pri_task_id=rowSet.getString("pri_task_id")!=null?rowSet.getString("pri_task_id"):"";
//				}
//				if(role_id.length()>0&&p_pri_task_id.length()>0)
//				{
//				//	WF_Actor wf_actor=new WF_Actor(role_id,"2");
//				//	int role_property=wf_actor.decideIsKhRelation(wf_actor.getActorid(),wf_actor.getActortype(),this.getFrameconn());
//				//	if(role_property!=0)
//					{
//						//System.out.println("select  distinct t.actorid,t.actorname from t_wf_task t,t_wf_task_datalink  td where t.task_id=td.task_id and td.seqnum='"+seqnum+"' and t.ins_id="+ins_id+" and t.pri_task_id="+p_pri_task_id);
//						rowSet=dao.search("select  distinct t.actorid,t.actorname from t_wf_task t,t_wf_task_datalink  td where t.task_id=td.task_id and td.seqnum='"+seqnum+"' and t.ins_id="+ins_id+" and t.pri_task_id="+p_pri_task_id);
//						LazyDynaBean a_bean=null;
//						while(rowSet.next())
//						{
//							a_bean=new LazyDynaBean();
//							String actorid=rowSet.getString("actorid");
//							String actorname=rowSet.getString("actorname");
//							if(actorid.length()>3)
//							{
//								a_bean.set("a0100",actorid.substring(3));
//								a_bean.set("email","");
//								a_bean.set("phone","");
//								a_bean.set("a0101",actorname);
//								a_bean.set("dbname",actorid.substring(0,3));
//								list.add(a_bean);
//							}
//						}
//						/*
//						String tabid=(String)this.getFormHM().get("tabid");
//						TemplateTableBo bo=new TemplateTableBo(this.frameconn,Integer.parseInt(tabid),this.userView);
//						
//						
//						String a0100="";
//						String basepre="";
//						rowSet=dao.search("select * from templet_"+tabid+" where seqnum='"+seqnum+"'");
//						if(rowSet.next())
//						{
//							list=bo.getObjectApprovers(rowSet.getString("a0100"),rowSet.getString("basepre"),role_property);
//						}
//						*/
//					}
//				}
//			}
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		return list;
	}

}
