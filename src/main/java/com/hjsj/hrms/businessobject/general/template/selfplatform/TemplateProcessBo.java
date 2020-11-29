package com.hjsj.hrms.businessobject.general.template.selfplatform;

import com.alibaba.druid.util.StringUtils;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TemplateProcessBo {

	private Connection conn = null;
    private UserView userView = null;
	public TemplateProcessBo(Connection conn,UserView userView)
    {
    	this.conn=conn;
    	this.userView=userView;
    }

	/**
	 * 获取审批意见信息
	 * @param paramMap
	 * @throws GeneralException
	 */
	public ArrayList<HashMap> viewProcess(HashMap paramMap) throws GeneralException {
		ArrayList<HashMap> spData = new ArrayList<HashMap>();
		String tabid=(String)paramMap.get("tabId");
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
		String return_flag=(String)paramMap.get("return_flag");
		String taskid=(String)paramMap.get("taskId");
		taskid=taskid!=null&&taskid.trim().length()>0?taskid:"";
		if(taskid.indexOf(",")!=-1) {
            taskid=taskid.split(",")[0];
        }
		String isDelete = (String)paramMap.get("isDelete");
		String ins_id="";
		StringBuffer strsql=new StringBuffer();
		RowSet rs=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String task_id_pro="";
			//手工指派
			StringBuffer sb = new StringBuffer("");
			rs=dao.search("select task_id_pro,flag,ins_id from t_wf_task where task_id="+taskid);
			if(rs.next())
			{
				ins_id=rs.getString("ins_id");
				if(rs.getString("task_id_pro")!=null&&rs.getString("task_id_pro").trim().length()>0) {
                    task_id_pro=rs.getString("task_id_pro");
                }
			}

			TemplateTableBo bo=new TemplateTableBo(this.conn,Integer.parseInt(tabid),this.userView);
			int mode = bo.getSp_mode();/**审批模式=0自动流转，=1手工指派*/
			boolean def_flow_self=false; //是否是自定义审批流程
			if(mode==0){
				WorkflowBo workflowBo=new WorkflowBo(this.conn,Integer.parseInt(tabid),this.userView);
				task_id_pro = workflowBo.getSubedTaskids(Integer.parseInt(taskid), Integer.parseInt(ins_id),return_flag,isDelete);
			}
			else {
				sb.append("select task_id from t_wf_task where ins_id="+ins_id+" and task_state='5'");
				rs=dao.search(sb.toString());
				int i=0;
				String task_id_pro_hand = "";
				while(rs.next()){
					String taskid_ = rs.getString("task_id");
					if(i==0) {
                        task_id_pro_hand+=taskid_;
                    } else {
                        task_id_pro_hand+=","+taskid_;
                    }
					i++;
				}
				if(task_id_pro_hand.length()>0) {
                    task_id_pro = task_id_pro_hand;
                }
				if(bo.isDef_flow_self(Integer.parseInt(taskid))) {
                    def_flow_self=true;
                }
			}
			String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                format_str="yyyy-MM-dd hh24:mi";
            }
			if(task_id_pro.length()==0)
			{
				strsql.append("select task_id,a0101 ,t_wf_task.a0100,t_wf_task.actorid,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname,nodename,bs_flag,task_type,t_wf_task.node_id from t_wf_task,t_wf_node where t_wf_task.node_id=t_wf_node.node_id  and   ins_id=");
				strsql.append(ins_id);
				strsql.append(" and (task_type='2' or task_type='1')  order by task_id,end_date");
			}
			else
			{
				if(mode==0){//自动才走这里
					strsql.append(" select task_id from t_wf_task where ( pri_task_id in (select pri_task_id from t_wf_task where task_id in ("+task_id_pro.substring(1)+")) "
							+" and task_id not in ("+task_id_pro.substring(1)+") )");
					strsql.append(" union select task_id from t_wf_task where (ins_id="+ins_id+" and bs_flag='3') ");
					rs=dao.search(strsql.toString());
					while(rs.next())
					{
						task_id_pro=","+rs.getString("task_id")+task_id_pro;
					}
				}
				strsql.setLength(0);
				task_id_pro = task_id_pro.startsWith(",")?task_id_pro.substring(1):task_id_pro;
				if(def_flow_self)
				{
					strsql.append("select task_id,a0101,t_wf_task.a0100,t_wf_task.actorid,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname ,bs_flag,task_type,node_id  from t_wf_task  where  ins_id=");
					strsql.append(ins_id);
					strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro+")  order by task_id,end_date");
				}
				else
				{
					//与t_wf_node 使用leftjoin，已删除的节点也可以查到，节点名称显示actorname wangrd 20160723
					strsql.append("select task_id,a0101,t_wf_task.a0100,t_wf_task.actorid,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname,nodename,bs_flag,task_type,t_wf_task.node_id from t_wf_task left join t_wf_node on  t_wf_task.node_id=t_wf_node.node_id  where ins_id=");
					strsql.append(ins_id);
					strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro+") ");
					strsql.append(" and bs_flag<>'4' "); //20170523 邓灿 审批流程不应包含空节点
					strsql.append(" order by task_id,end_date");
				}
			}

			rs=dao.search(strsql.toString());
			int i=0;
			int  beginNodeid=0;
			HashMap endtimemap = new HashMap();

			while(rs.next())
			{
				if(rs.getString("end_date")==null){//没审批时 不显示
					continue;
				}
				String task_type=rs.getString("task_type");
				String task_id=rs.getString("task_id");
				int node_id=rs.getInt("node_id");
				if("1".equals(task_type)) {
                    beginNodeid=rs.getInt("node_id");
                }
				String bs_flag=rs.getString("bs_flag")!=null?rs.getString("bs_flag"):"1"; //1：待批 2：加签 3报备
				HashMap taskMap=new HashMap();
				taskMap.put("bs_flag",bs_flag);
				taskMap.put("task_id",task_id);
				String photoId="";
				//审批人姓名
				if(rs.getString("a0101")!=null&&rs.getString("a0101").trim().length()>0){
					taskMap.put("transactor",rs.getString("a0101"));
					//根据人员信息主键 获取人员照片信息
					//业务用户不考虑，只考虑自助用户。。。。
					String nbasea0100=rs.getString("a0100");
					//Usr00000032
					nbasea0100=getUserAtts(nbasea0100);
					//Usr00000032
					if(!StringUtils.isEmpty(nbasea0100)){
						photoId = getPhotoFieldid(nbasea0100.substring(0,3), nbasea0100.substring(3));
					}
				}else{
					taskMap.put("transactor",rs.getString("actorname"));
					//根据人员信息主键 获取人员照片信息
					//业务用户不考虑，只考虑自助用户。。。。
					String nbasea0100=rs.getString("actorid");
					nbasea0100=getUserAtts(nbasea0100);
					//Usr00000032
					if(!StringUtils.isEmpty(nbasea0100)){
						photoId = getPhotoFieldid(nbasea0100.substring(0,3), nbasea0100.substring(3));
					}
				}
				taskMap.put("photoId",photoId);
				if(def_flow_self)
				{
					if(beginNodeid==node_id) {
                        taskMap.put("node_name","个人申请");
                    } else if("3".equals(bs_flag)) {
                        taskMap.put("node_name","报备人");
                    } else {
                        taskMap.put("node_name","审批人");
                    }
				}
				else
				{
					if(mode==0&&beginNodeid!=node_id&&rs.getString("nodename")!=null&&rs.getString("nodename").length()>0) {
                        taskMap.put("node_name","  "+rs.getString("nodename")+"");
                    } else if(beginNodeid==node_id) {
                        taskMap.put("node_name","个人申请");
                    } else if(mode==0&&beginNodeid!=node_id&&rs.getString("actorname")!=null&&rs.getString("actorname").length()>0) {
                        taskMap.put("node_name","  "+rs.getString("actorname")+"");
                    } else {
                        taskMap.put("node_name","审批人");
                    }
				}
				if(rs.getString("task_state")!=null&&"4".equals(rs.getString("task_state"))){
					if("08".equals(rs.getString("state"))){
						taskMap.put("node_name","系统报批");
					}else if("07".equals(rs.getString("state"))){
						taskMap.put("node_name","系统驳回");
					}else {
                        taskMap.put("node_name","");
                    }
				}else{
					taskMap.put("sp_state",rs.getString("sp_yj"));
				}
				/**
				 * =06(结束)
				 * =07(驳回)
				 * =08(报审)
				 */
				String state=rs.getString("state");
				if("08".equals(state)||StringUtils.isEmpty(state)||"07".equals(state)){
					taskMap.put("sp_state",rs.getString("sp_yj"));
				}else{
					taskMap.put("sp_state",state);
				}

				taskMap.put("comment",Sql_switcher.readMemo(rs,"content").replace("\n", "<br>").replace(" ", "&nbsp;"));
				if(rs.getString("end_date")!=null) {
                    taskMap.put("time",rs.getString("end_date"));
                } else {
                    taskMap.put("time","");
                }
				/**
				 * 同节点类型代码类
				 * =1开始
				 * =2人工
				 * =3自动
				 * =4与发散
				 * =5与汇聚
				 * =6或发散
				 * =7或汇聚
				 * =8哑单元
				 * =9结束
				 */
				taskMap.put("node_type",task_type);
				if(i!=0&&(rs.getString("state")==null||rs.getString("state").trim().length()==0)) {
                    continue;
                }
				if(i!=0&&((rs.getString("a0101")==null||rs.getString("a0101").trim().length()==0)&&!"4".equals(rs.getString("task_state")))) {
                    continue;
                }
				//最后一个审批人不显示：状态不为结束，别且结束日期不为空。
				if(rs.isLast()){
					if(!"5".equals(rs.getString("task_state"))&&((String)taskMap.get("end_date")).length()<=0) {
                        continue;
                    }
				}
				i++;
				spData.add(taskMap);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return spData;

	}
	
	public String getPhotoFieldid(String userbase,String a0100){
		String photoId="";
		if(StringUtils.isEmpty(userbase)||StringUtils.isEmpty(a0100)){
			return photoId;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select fileid from ");
		sql.append(userbase);
		sql.append("a00 where a0100=?");
		sql.append(" and flag='P'");
		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add(a0100);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			rs = dao.search(sql.toString(), paramList);
			if (rs.next()) {
				photoId = rs.getString("fileid");
			} 
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return StringUtils.isEmpty(photoId)?"":photoId;
		
	}
	
	/**
	 * 通过业务用户登录用户名获取关联自助用户：Usr000001
	 * 自助用户，则返回Usr000001
	 * @param value
	 * @return username，password
	 */
	private String getUserAtts(String message)
	{
		String nbaseA0100="";
		if(message==null||message.length()<=0)
		{
			return nbaseA0100;
		}
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			String nbase = "";
			String a0100 = "";
        	rs = dao.search("select nbase,a0100 from operuser where username='"+message+"'");
        	if(rs.next()){
        		nbase = rs.getString("nbase");
        		a0100 = rs.getString("a0100");
        		nbaseA0100=nbase+a0100;
        		return nbaseA0100;
        	}
        	nbase = message.substring(0,3);
        	a0100 = message.substring(3);
        	if(StringUtils.isEmpty(nbase)||"null".equalsIgnoreCase(nbase)){
        	    return "";
        	}
        	List list = dao.searchDynaList("select a0101 from "+nbase+"a01 where a0100='"+a0100+"'");
        	if (list!=null && list.size()>0){
        		nbaseA0100=nbase+a0100;
        	}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
	   return nbaseA0100;
	}
}
