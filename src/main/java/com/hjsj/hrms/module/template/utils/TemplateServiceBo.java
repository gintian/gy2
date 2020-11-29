package com.hjsj.hrms.module.template.utils;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Title:TemplateUtilBo.java</p>
 * <p>Description>:模板接口服务类，供其他模块调用相关接口</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-08-23 上午10:36:32</p>
 * <p>@version: 7.0</p>
 */
public class TemplateServiceBo {
	private Connection conn=null;
    private UserView userView;
    
	public TemplateServiceBo(Connection conn,UserView userview) {
		this.conn = conn;
		this.userView = userview;
	}
	
	/**
	 * 获得服务大厅里显示的业务模板,按模板类型存储
	 * @return
	 */
	public ArrayList<LazyDynaBean> getServiceTemplate()
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			UserView userView =this.userView;
			//业务用户关联自助用户 按自助用户走
			if(userView.getS_userName()!=null&&userView.getS_userName().length()>0&&userView.getStatus()==0&&userView.getBosflag()!=null){
				userView=new UserView(userView.getS_userName(), userView.getS_pwd(), this.conn);
				try {
					userView.canLogin();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			
			/* 装载业务模板节点,分业务类型加载模板
			 * =1,国家机关
			 * =2,事业单位
			 * =3,企业单位
			 * =4,军队使用
			 * =5,其    它 */
			String unit_type=null;
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
			if(unit_type==null|| "".equals(unit_type))
				unit_type="3";
			 
			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer strsql = new StringBuffer();
			String statickey = "static";
			if(Sql_switcher.searchDbServer()==Constant.KUNLUN) {
				statickey = "\"static\"";
			}else if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				statickey="static_o";
			}
			strsql.append("select a.TabId,a.Name,a.operationcode,b.operationname,b."+statickey+" "+statickey+",a.icon,a.customname from ");
			strsql.append("template_table a ,operation b where a.operationcode=b.operationcode ");
			
			//考虑单位性质
			strsql.append(" and (");
			String[] units =unit_type.split(",");
			for(int i=0;i<units.length;i++)
			{
				if(units[i]==null||units[i].trim().length()==0)
					continue;
				strsql.append("a.flag ="+Integer.parseInt(units[i]));
				if(i<units.length-1)
					strsql.append(" or ");
			}			
			strsql.append(")");
			
			
			strsql.append(" and b.operationtype <> 0 and b.operationtype <> 5"); //不包含新增人员、新增机构模板
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				strsql.append(" ORDER BY b.static_o,a.operationcode,a.tabid");//liuyz 修正服务大厅模版排序，与cs里相同
			}else {
				strsql.append(" ORDER BY b.static,a.operationcode,a.tabid");//liuyz 修正服务大厅模版排序，与cs里相同
			}
			rowSet=dao.search(strsql.toString());
			String operationcode="";
			String operationname="";
			ArrayList subList=new ArrayList();
			String tabids=""; 
			while(rowSet.next())
			{
				String  tabid=rowSet.getString("tabid");
				String	_static="";
				if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
					_static=rowSet.getString("static_o")!=null?rowSet.getString("static_o"):"";
				}else {
					_static=rowSet.getString("static")!=null?rowSet.getString("static"):"";
				}
				String  _operationcode=rowSet.getString("operationcode");
				String  _operationname=rowSet.getString("operationname");
				String name=rowSet.getString("customname");
				if(name==null||name.trim().length()==0)
					name=rowSet.getString("Name");
				String icon=rowSet.getString("icon")!=null?rowSet.getString("icon"):"";
				if(operationcode.length()==0)
				{
					operationcode=_operationcode;
					operationname=_operationname;
				}
				// 权限控制
				if("1".equals(_static))
			          if (!userView.isHaveResource(IResourceConstant.RSBD,rowSet.getString("tabid")))
				        continue;
				if("2".equals(_static))
				      if (!userView.isHaveResource(IResourceConstant.GZBD,tabid))
					    continue;
				if("8".equals(_static))
				      if (!userView.isHaveResource(IResourceConstant.INS_BD,tabid))
					    continue;
				if("3".equals(_static))
				      if (!userView.isHaveResource(IResourceConstant.PSORGANS,tabid))
					    continue;
				if("4".equals(_static))
				      if (!userView.isHaveResource(IResourceConstant.PSORGANS_FG, tabid))
				    	continue;
				if("5".equals(_static))
				      if (!userView.isHaveResource(IResourceConstant.PSORGANS_GX,tabid))
					    continue;
				if("6".equals(_static))
				      if (!userView.isHaveResource(IResourceConstant.PSORGANS_JCG,tabid))
					    continue;
				if("10".equals(_static)|| "11".equals(_static))  //不包含单位、职位模板
					   continue;
				tabids+=","+tabid;
				LazyDynaBean templateBean=new LazyDynaBean();
				templateBean.set("tabid",tabid);
				templateBean.set("tabname",name);
				templateBean.set("icon",icon);
				templateBean.set("applyedNum","0");
				templateBean.set("rejectingTasks","");
				templateBean.set("ins_id","");
				templateBean.set("task_id","");
				if(!operationcode.equals(_operationcode))
				{
					LazyDynaBean busiBean=new LazyDynaBean();
					busiBean.set("name", operationname);
					busiBean.set("temps", subList);
					list.add(busiBean);
					subList=new ArrayList();
				}	
				subList.add(templateBean);
				operationcode=_operationcode;
				operationname=_operationname;
			}
			if(subList.size()>0)
			{
				LazyDynaBean busiBean=new LazyDynaBean();
				busiBean.set("name", operationname);
				busiBean.set("temps", subList);
				list.add(busiBean);
			}
			if(tabids.length()>0)
				getApplyedNum(tabids,list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return list;
	}
	
	/**
	 * 获得各模板我申请的单据个数和是否有驳回的待办任务
	 * @param tabids 模板ids
	 * @return
	 */
	private void getApplyedNum(String tabids,ArrayList templateList)
	{
		
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select tabid,count(ins_id) as vcount from t_wf_instance where tabid in ("+tabids.substring(1)+") ");
			String dbpre=this.userView.getDbname(); //库前缀
			String userid=dbpre+this.userView.getA0100();//人员编号
			/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
			if(userid==null||userid.length()==0)
				userid="-1";
			/**人员列表*/
			sql.append( " and    upper(actorid) in ('");
			sql.append(userid.toUpperCase());
			sql.append("') group by tabid"); 
			rowSet=dao.search(sql.toString());
			HashMap map=new HashMap(); //各表单申请单据数
			while(rowSet.next())
			{
				map.put(rowSet.getString("tabid"),rowSet.getString("vcount"));
			}
			
			sql.setLength(0);
			sql.append("select ti.tabid,t.task_id,t.actor_type,t.node_id,t.ins_id,t.actorid from t_wf_task t,t_wf_instance ti where t.ins_id=ti.ins_id and ti.tabid  in ("+tabids.substring(1)+") ");
			sql.append(" and "+Sql_switcher.isnull("t.bs_flag","'1'")+"=1 and t.task_type='2' and t.task_state='3' and t.state='07' and ( ");
		    sql.append(getTaskFilterWhere()+" ) order by ti.tabid");
		    rowSet=dao.search(sql.toString());
		    String _tabid="";
		    String _taskid="";
		    HashMap map2=new HashMap(); //退回的单据
		    LazyDynaBean paramBean=null;
		    WorkflowBo wb=new WorkflowBo(this.conn,this.userView);
			while(rowSet.next())
			{
				String tabid=rowSet.getString("tabid");
				String task_id=rowSet.getString("task_id");
				String ins_id=rowSet.getString("ins_id");
				String actor_type=rowSet.getString("actor_type");
				String node_id=rowSet.getString("node_id");
				String actorid=rowSet.getString("actorid");
				if("5".equals(actor_type))//本人
				{
					paramBean=new LazyDynaBean();
					paramBean.set("tabid",tabid);
					paramBean.set("task_id",task_id);
					paramBean.set("ins_id",ins_id);
					paramBean.set("actor_type",actor_type);
					paramBean.set("node_id",node_id);
					ArrayList listrecord = wb.getRecordList(paramBean,this.userView);
					if(listrecord.size()==0)
						continue;
				}
				else if("2".equals(actor_type))//角色
				{
					paramBean=new LazyDynaBean();
					paramBean.set("tabid",tabid);
					paramBean.set("task_id",task_id);
					paramBean.set("ins_id",ins_id);
					paramBean.set("actor_type",actor_type);
					paramBean.set("node_id",node_id);
					paramBean.set("actorid",actorid); 
					ArrayList listrecord = wb.getRecordList(paramBean,this.userView);
					if(listrecord.size()==0)
						continue;
					 
				} 
				
				if(_tabid.length()==0)
				{
					_tabid=tabid;
				}
				
				if(!tabid.equals(_tabid))
				{
					map2.put(_tabid,_taskid.substring(1));
					_taskid="";
					
				}
				_taskid+=","+task_id;
				_tabid=tabid;
			}
			if(_taskid.length()>0)
				map2.put(_tabid,_taskid.substring(1));
		    
			for(Iterator t=templateList.iterator();t.hasNext();)
			{
				LazyDynaBean busiBean=(LazyDynaBean)t.next();
				ArrayList temps=(ArrayList)busiBean.get("temps");
				for(Iterator tt=temps.iterator();tt.hasNext();)
				{
					LazyDynaBean templateBean=(LazyDynaBean)tt.next();
					String tabid=(String)templateBean.get("tabid"); 
					if(map.get(tabid)!=null)
					{
						int count=Integer.parseInt((String)map.get(tabid));
						if(count>0)
						{
							templateBean.set("applyedNum",""+count);
							if(map2.get(tabid)!=null)
								templateBean.set("rejectingTasks",(String)map2.get(tabid)); //退回的待办任务ID
							else
							{
								TemplateParam paramBo = new TemplateParam(conn, this.userView, Integer.parseInt(tabid));
								String unique_check=paramBo.getUnique_check(); //员工发启业务申请，已报批（有单子在途时）不允许再次申请,0:否(默认值)
								if(unique_check!=null&&"1".equals(unique_check))
								{
									String task_ins_value=getRuningTask(Integer.parseInt(tabid));
									if(!StringUtils.isEmpty(task_ins_value))
									{
										templateBean.set("task_id", task_ins_value.split(",")[0]);
										templateBean.set("ins_id", task_ins_value.split(",")[1]);
									}
									
								}
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{ 
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		
	}
	
	
	/**
	 * 获得自己申请未结束的单据信息
	 * @param tabid
	 * @return
	 */
	private String getRuningTask(int tabid)
	{
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql="select max(task_id) as task_id ,max(ins_id) as ins_id from t_wf_task where ins_id in (select ins_id from t_wf_instance where tabid=? and upper(actorid)='"+this.userView.getDbname().toUpperCase()+this.userView.getA0100()+"' and actor_type=1 and finished='2' ) " + 
					"       and task_type='2'    and end_date is null and  ( task_state='3'  or task_state='6' )  and "+Sql_switcher.isnull("bs_flag","'1'")+"='1'   ";			 
			rowSet=dao.search(sql,Arrays.asList(new Object[]{tabid}));
			if(rowSet.next())
			{
				String value=rowSet.getInt("task_id")+","+rowSet.getInt("ins_id");
				if("0,0".equals(value))
					value="";
				return value;
			}
		}
		catch(Exception e)
		{ 
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}	
			
		return "";
	}
	
	
	
	private String getTaskFilterWhere()
	{
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		String orgid="UN"+this.userView.getUserOrgId();//单位编码
		String deptid="UM"+this.userView.getUserDeptId();//部门编码
		String posid="@K"+this.userView.getUserPosId();//  getUserOrgId();//职位编码
		/**组织元*/
		strwhere.append("(t.actor_type='3' and t.actorid in ('");//=3:组织单元
		strwhere.append(orgid.toUpperCase());
		strwhere.append("','");
		strwhere.append(deptid.toUpperCase());
		strwhere.append("','");
		strwhere.append(posid.toUpperCase());
		strwhere.append("'))");
		strwhere.append(" or ( t.actor_type='5'  )"); //本人
		/**人员列表*/
		strwhere.append( " or ((t.actor_type='1' or t.actor_type='4') and lower(t.actorid) in ('");//=1:人员  =4:业务用户
		strwhere.append(userid.toLowerCase());
		strwhere.append("','");
		strwhere.append(this.userView.getUserName().toLowerCase());
		strwhere.append("'))");
		
		/**角色ID列表*/
	 	ArrayList rolelist= this.userView.getRolelist();//角色列表
	 	StringBuffer strrole=new StringBuffer();
	 	for(int i=0;i<rolelist.size();i++)
	 	{
	 		strrole.append("'");
	 		strrole.append((String)rolelist.get(i));
	 		strrole.append("'");
 			strrole.append(",");	 		
	 	}
	 	if(rolelist.size()>0)
	 	{
	 		strrole.setLength(strrole.length()-1);
	 		strwhere.append(" or (t.actor_type='2' and t.actorid in ("); //角色
	 		strwhere.append(strrole.toString());
	 		strwhere.append("))");
	 	}
		return strwhere.toString();
	}
	/**
	 * 通过单号和模板id查找对应的单据信息
	 * @param appId
	 * @param tabid
	 * @return
	 */
	public LazyDynaBean getTemplateInfoForKq(String appId,int tabid) throws GeneralException{
		LazyDynaBean templateldb = new LazyDynaBean();
		RowSet rowSet=null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			TemplateTableParamBo tp=new TemplateTableParamBo(tabid,this.conn);
			// 获得模板对应考勤申请单序号指标
			String filename = this.getKqParam(5, tp);
			String tablename = "templet_"+tabid;
			StringBuffer sql = new StringBuffer("");
			sql.append("select twt.* from ").append(tablename).append(" t,t_wf_task twt");
			sql.append(" where t.ins_id=twt.ins_id and t.").append(filename).append("='").append(appId).append("' ");
			sql.append(" and twt.task_type='2' ");
			sql.append(" and ").append(Sql_switcher.isnull("twt.bs_flag","'1'")).append("='1' ");
			sql.append(" and twt.task_state=3 and twt.state='07' ");
			sql.append(" order by twt.task_id desc");
			rowSet = dao.search(sql.toString());
			if(rowSet.next()) {
				int task_id = rowSet.getInt("task_id");
				int ins_id = rowSet.getInt("ins_id");
				String actor_type = rowSet.getString("actor_type");//参与者类型
				String actorid = rowSet.getString("actorid");//参与者名称
				String actorname = rowSet.getString("actorname");//参与者名称
				Date start_date = rowSet.getDate("start_date");//任务开始时间
				Date end_date = rowSet.getDate("end_date");//任务结束时间
				String a0100_1 = rowSet.getString("a0100_1");//发件人编号
				String a0101_1 = rowSet.getString("a0101_1");//发件人姓名
				templateldb.set("task_id", task_id);
				templateldb.set("task_id_e", PubFunc.encrypt(task_id+""));
				templateldb.set("ins_id", ins_id);
				templateldb.set("actor_type", actor_type);
				templateldb.set("actorname", actorname);
				templateldb.set("actorid", actorid);
				templateldb.set("start_date", start_date);
				templateldb.set("end_date", end_date);
				templateldb.set("a0100_1", a0100_1);
				templateldb.set("a0101_1", a0101_1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return templateldb;
	}
	/**
	 * 获得模板设置的考勤参数
	 * @param flag 1:模板与申请单指标对应关系  2：考勤申请单表名  3：考勤申请单序号 4：申请单序号生成串  5：模板对应考勤申请单序号指标
	 * @return
	 */
	private String getKqParam(int flag,TemplateTableParamBo tp)
	{
		String param_str="";
		if(flag==1)
			param_str=tp.getKq_field_mapping();
		else if(flag==2)
		{
			param_str=tp.getKq_setid();
		}
		else if(flag==3)
		{
			String kqTab=tp.getKq_setid();
			if(kqTab.length()>0)
				param_str=kqTab+"01";
		}
		else if(flag==4){
			String kqTab=tp.getKq_setid();
			String kq_id_str="";
			if("Q11".equalsIgnoreCase(kqTab)) //加班
				kq_id_str="Q11.Q1101";
			else if("Q13".equalsIgnoreCase(kqTab)) //公出
				kq_id_str="Q13.Q1301";
			else if("Q15".equalsIgnoreCase(kqTab)) //请假
				kq_id_str="Q15.Q1501";
			param_str=kq_id_str;
		}
		else if(flag==5)
		{
			String mb_seqnum_id="";
			String mapping=tp.getKq_field_mapping(); 
			String kqTab=tp.getKq_setid();
			String kq_seqnum_id=kqTab+"01"; 
			if(kqTab.length()>0&&mapping.length()>0)
			{
				String[] temps=mapping.toLowerCase().split(","); 
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						String[] temp=temps[i].toLowerCase().split(":");
						if(temp[0].equalsIgnoreCase(kq_seqnum_id))
						{
							mb_seqnum_id=temp[1];
							break;
						}
					}
				}
				
			}
			param_str=mb_seqnum_id;
		}
		return param_str;
	}

	
}
