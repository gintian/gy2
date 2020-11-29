package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.module.template.templatenavigation.businessobject.TemplateNavigationBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/**
 * 
 * <p>Title:GetYbTaskTrans.java</p>
 * <p>Description>:获取已办数据（表格及查询条件）</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 14, 2016 4:09:50 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class GetYbTaskTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		/* 模块ID
		 * 1、人事异动
		 * 2、薪资管理
		 * 3、劳动合同
		 * 4、保险管理
		 * 5、出国管理
		 * 6、资格评审
		 * 7、机构管理
		 * 8、岗位管理
		 * 9、业务申请（自助）
		 * 10、考勤管理
		 * 11、职称评审
		*/	
		String module_id=(String)this.getFormHM().get("module_id");
		String tabid = (String) this.getFormHM().get("tabid");//模板号
		String bs_flag = (String) this.getFormHM().get("bs_flag");//任务类型
		String days=(String)this.getFormHM().get("days");//最近几天
		String start_date=(String)this.getFormHM().get("start_date");//开始时间
		String end_date=(String)this.getFormHM().get("end_date");//结束时间
		String query_type = (String) this.getFormHM().get("query_type");//按日期or按时间段 1 or other 
		String fromflag=(String)this.getFormHM().get("fromflag"); 
		String flag=(String) this.getFormHM().get("flag");//0:首次进入 1：查询进入
		TemplateNavigationBo bo = new TemplateNavigationBo(this.frameconn,this.userView);
		if(query_type==null || query_type.trim().length()==0)
			query_type="1";
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
				_withNoLock=" WITH(NOLOCK) ";
			
			StringBuffer strsql = new StringBuffer();
			StringBuffer strsql2 = new StringBuffer();
			ArrayList valueList=new ArrayList();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				format_str="yyyy-MM-dd hh24:mi";
			String static_="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				static_="static_o";
			}
			strsql.append("select U.ins_id,T.task_topic,U.tabid,U.actorname fullname,(select o.codeitemid from organization o "+_withNoLock+" where o.codeitemid=U.b0110) unitname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" end_date,");
			strsql.append("T.actorname,T.actor_type,T.task_id,T.flag,U.tabid,tt."+static_+",U.finished insfinished,T.content   from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table tt "+_withNoLock+"");
			strsql.append(" where  T.ins_id=U.ins_id  and U.tabid=tt.tabid and ((task_type='2' and  (task_state='5'  or task_state='6' )) or(task_type='9' and task_state='4' and U.finished='6' and T.task_topic like '%被撤销)') ) ");
			strsql2.append(strsql);
			if(tabid!=null&&!"-1".equals(tabid)&&tabid.length()>0)
			{
				strsql.append(" and tt.tabid=?  " );
				valueList.add(new Integer(tabid));
			}
			
			if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
				strsql.append(" and tt."+static_+"=? " );
				if("7".equals(module_id)){
					valueList.add(new Integer(10)); 
					strsql2.append(" and tt."+static_+"=10 " );
				}
				else if("8".equals(module_id)){
					valueList.add(new Integer(11)); 
					strsql2.append(" and tt."+static_+"=11 " );
				}
			}
			else
			{
				if(userView.getStatus() != 4 || !"9".equals(module_id)){
					strsql.append(" and tt."+static_+"!=10 and tt."+static_+"!=11 ");
					strsql2.append(" and tt."+static_+"!=10 and tt."+static_+"!=11 ");
				}
			}
			
			String kq_tabids="";
			String zz_tabids = bo.getBusinessTabid("12");
			if(module_id==null||!"9".equals(module_id)) //业务申请的待办无需过滤考勤模板
			{
				TemplateTableParamBo tp=new TemplateTableParamBo(this.frameconn); 
		        kq_tabids=tp.getAllDefineKqTabs(0);
			   // kq_tabids=bo.getKqTabIds(module_id);//liuyz 考勤支持业务模版
			   
			}
			
			if(module_id!=null&& "10".equals(module_id)){ //考勤业务办理
				if(kq_tabids.length()==0){
					strsql.append(" and 1=2 ");
					strsql2.append(" and 1=2 ");
				}
				else{
					strsql.append(" and U.tabid in ("+kq_tabids.substring(1)+")  and tt.tabid in ("+kq_tabids.substring(1)+")  ");
					strsql2.append(" and U.tabid in ("+kq_tabids.substring(1)+")  and tt.tabid in ("+kq_tabids.substring(1)+")  ");
				}
			}
			else if(module_id!=null&&"12".equals(module_id)) {//证照管理
				if(zz_tabids.length()==0){
					strsql.append(" and 1=2 ");
					strsql2.append(" and 1=2 ");
				}
				else{
					strsql.append(" and U.tabid in ("+zz_tabids+")  and tt.tabid in ("+zz_tabids+")  ");
					strsql2.append(" and U.tabid in ("+zz_tabids+")  and tt.tabid in ("+zz_tabids+")  ");
				}
			}
			else if (kq_tabids.length()>0||zz_tabids.length()>0)
			{
				if(kq_tabids.length()>0) {
					strsql.append(" and tt.tabid not in ("+kq_tabids.substring(1)+")" );
					strsql2.append(" and tt.tabid not in ("+kq_tabids.substring(1)+")" );
				}
				if(zz_tabids.length()>0) {
					strsql.append(" and tt.tabid not in ("+zz_tabids+")" );
					strsql2.append(" and tt.tabid not in ("+zz_tabids+")" );
				}
			}
			 
			
			//1：审批任务 2：加签任务 3：报备任务  4：空任务 
			strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"=? ");
			strsql2.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"=" + bs_flag);
			valueList.add(bs_flag);
			
 
			strsql.append("  and ( "+getInsFilterWhere2("T."));
			strsql2.append(" and ( "+getInsFilterWhere2("T.")); 
			strsql.append("   and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"),"' '")+"<>'重新分派'  )  ");
			strsql2.append("   and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"),"' '")+"<>'重新分派' ) ");
			 
			
			strsql.append(")  and 1=1 ");
			strsql2.append(")  and 1=1 ");
	 
		
			 
			if("1".equals(query_type))//最近多少天
			{
					if(bo.validateNum(days)){
						String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
						strsql.append(" and U.start_date>=");
						strsql.append(strexpr);	
						strsql2.append(" and U.start_date>=");
						strsql2.append(strexpr);	
					}
			}else{
					if("2".equals(query_type)){
						/*DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						Date startDate = null;
						Date endDate = null;
						if(StringUtils.isNotBlank(start_date)){
							startDate = df.parse(start_date);
						}
						if(StringUtils.isNotBlank(end_date)){
							endDate = df.parse(end_date);
						}
						if(startDate != null && endDate != null){
							if(endDate.getTime() < startDate.getTime())
								throw GeneralExceptionHandler.Handle(new Exception("开始日期不能大于结束日期！"));
						}*/
						strsql.append(" and ( 1=1 ");
						strsql2.append(" and ( 1=1 ");
						if(bo.validateDate(start_date)){
							strsql.append(PubFunc.getDateSql(">=","U.start_date",start_date));
							strsql2.append(PubFunc.getDateSql(">=","U.start_date",start_date));
						}
						if(bo.validateDate(end_date)){
							strsql.append(PubFunc.getDateSql("<=","U.start_date",end_date));
							strsql2.append(PubFunc.getDateSql("<=","U.start_date",end_date));
						}
						strsql.append(" )");
						strsql2.append(" )");
					}
			} 
			
			/*boolean isSource=false;
			if(bo.hasTemplateResource()) //是否有模板资源权限
				isSource=true;
			if(this.userView.isSuper_admin())
				isSource=true;
			if(!isSource)
			{	
				strsql.append(" and 1=2 ");
				strsql2.append(" and 1=2 ");
			}*/

			//获得各实例下当前审批人sql
			int index=strsql.toString().indexOf("from");
			String subsql=strsql.toString().substring(index+4, strsql.toString().length());  //为了得到第二个from的位置，先将第一个from的位置之前的部分去掉
			index=subsql.indexOf("from");  //得到第二个from的位置，即可根据index截取要查询的sql liuzy 20151112
			String sql="";
			if(index!=-1)
				sql=" select ins_id,actorname,actor_type from t_wf_task "+_withNoLock+" where ins_id in (select  U.ins_id "+subsql.substring(index)+") and task_type='2' and  "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='3' order by ins_id";
			
			HashSet tabidSet=new HashSet();
			
			int index2=strsql2.toString().indexOf("from t_wf_task");
			this.frowset=dao.search(" select distinct U.tabid   "+strsql2.toString().substring(index2));
			while(this.frowset.next())
			{
				String _tabid = this.frowset.getString("tabid");
				tabidSet.add(_tabid);
			}
			
			
			strsql.append(" order by T.end_date DESC");
			ArrayList dataList=new ArrayList();
			if(valueList.size()>0)
				this.frowset=dao.search(strsql.toString(),valueList);
			else
				this.frowset=dao.search(strsql.toString());
			//获得各实例下当前审批人
			HashMap ins_CurrentSpInfo=new HashMap(); // 
			if(sql.length()>0)
			{ 
				RowSet rowSet=null;
				if(valueList.size()>0)
					rowSet=dao.search(sql,valueList);
				else
					rowSet=dao.search(sql);
				int ins_id=0;
				String actorname="";
				String actor_type = "";
				while(rowSet.next())
				{
					if(ins_id==0)
						ins_id=rowSet.getInt("ins_id");
					if(ins_id==rowSet.getInt("ins_id")){
						actorname+=","+rowSet.getString("actorname");
						actor_type +=","+ rowSet.getString("actor_type");
					}else
					{
						if(actorname.toString().length()>1&&actor_type.length()>1)//liuyz 排除actor_type和actorname为空值的情况
						{
							ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1)+"`"+actor_type.substring(1));
							ins_id=rowSet.getInt("ins_id");
							actorname=","+rowSet.getString("actorname");
							actor_type =","+ rowSet.getString("actor_type");
						}
					} 
				}
				PubFunc.closeDbObj(rowSet);
				
				if(ins_id!=0&&actorname.length()>1)//liuyz 排除actor_type和actorname为空值的情况
					ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1)+"`"+actor_type.substring(1));
			}
			
			LazyDynaBean abean=null;
			HashMap operationTypeMap=new HashMap();
			HashMap tableNameMap=new HashMap();
			
			HashSet taskSet = new HashSet();
			while(this.frowset.next())
			{
				abean=new LazyDynaBean();
				String _tabid = this.frowset.getString("tabid");
				//tabidSet.add(_tabid);
				String task_id=this.frowset.getString("task_id");
				String ins_id=this.frowset.getString("ins_id");
				String _flag=this.frowset.getString("flag")!=null?this.frowset.getString("flag"):"";
				String task_topic=this.frowset.getString("task_topic");
				String _static=this.frowset.getString(static_);
				String actor_type = this.frowset.getString("actor_type");
				taskSet.add(task_id);
				TemplateParam param = new TemplateParam(this.frameconn,this.userView,Integer.valueOf(_tabid));
				if("".equals(_flag))
				{
					String operationType="";
					if(operationTypeMap.get(_tabid)==null)
					{
						operationType=findOperationType(_tabid);
						operationTypeMap.put(_tabid, operationType);
					}
					else
						operationType=(String)operationTypeMap.get(_tabid);
					String tabName="";
					if(tableNameMap.get(_tabid)==null)
					{
						tabName=findTabName(_tabid);
						tableNameMap.put(_tabid,tabName);
					}
					else
					{
						tabName=(String)tableNameMap.get(_tabid);
					}
					String topic="";
					if(userView.getStatus()!=4)
						topic=getTopic(task_id,"templet_"+_tabid,Integer.parseInt(operationType),_tabid,module_id,param);
					else
					{
						if(_static!=null&&("10".equals(_static)|| "11".equals(_static)))
						{
							topic=getTopic(task_id,"templet_"+_tabid,Integer.parseInt(operationType),_tabid,module_id,param);
						} 
						else
							topic=getTopic(task_id,"templet_"+_tabid,Integer.parseInt(operationType),_tabid,module_id,param);
					}
					if(topic.indexOf(",共0")!=-1) //撤销任务主题
					{
						topic=getRecordBusiTopicByState(Integer.parseInt(task_id),3,"templet_"+_tabid,dao,Integer.parseInt(operationType), module_id,param);
					}
					task_topic = tabName+topic;
					
				}
				if(ins_CurrentSpInfo.get(ins_id)!=null){
					abean.set("sp_info",((String)ins_CurrentSpInfo.get(ins_id)).split("`")[0]);
					abean.set("actor_type", ((String)ins_CurrentSpInfo.get(ins_id)).split("`")[1]);
				}else{
					abean.set("sp_info","");
					abean.set("actor_type","");
				}
				String content=Sql_switcher.readMemo(this.frowset,"content");// 自动流转的任务标题后加【自动审批】标识
				if(content.indexOf("【自动审批】")!=-1)
					abean.set("task_topic",task_topic+" 【自动审批】");
				else
					abean.set("task_topic",task_topic);
				abean.set("ins_id",this.frowset.getString("ins_id"));
				abean.set("tabid",this.frowset.getString("tabid"));
				abean.set("fullname",this.frowset.getString("fullname"));
				String unitname=this.frowset.getString("unitname")==null?"":this.frowset.getString("unitname");
				abean.set("unitname",unitname);
				abean.set("start_date", this.frowset.getString("start_date"));
				abean.set("end_date", this.frowset.getString("end_date"));
				
				/**安全改造，将参数加密**/
				abean.set("task_id",PubFunc.encrypt(this.frowset.getString("task_id")));
				String insfinished = this.frowset.getString("insfinished");
				if("5".equals(insfinished)){
					abean.set("flag", "结束");
				}else if("6".equals(insfinished)){//lis 20160520
					abean.set("flag", "终止");
				}else{
					abean.set("flag", "等待");
				}
				
				dataList.add(abean);
			}
			//获得本人处理的单子
			//this.getSolveByMyselfOrRole(dataList,tabid,module_id,bs_flag,query_type,days,bo,start_date,end_date,tabidSet,taskSet);
			ArrayList templateList = bo.getTemplateList(tabidSet);
			this.getFormHM().put("templatejson", templateList);
			
			if("1".equals(flag)){//页面模糊查询
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("ybtask");
				tableCache.setTableData(dataList);
				this.userView.getHm().put("ybtask", tableCache);
				return;
			}
			ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
			column = bo.getYbColumnsInfo();
			TableConfigBuilder builder = new TableConfigBuilder("ybtask", column, "ybtask1", userView,this.getFrameconn());
			builder.setDataList(dataList);
			builder.setSelectable(true);
			//builder.setLockable(true);//去掉列锁定
			//builder.setColumnFilter(false);
			builder.setPageSize(20);
			builder.setTableTools(bo.getYbTaskButtons());
			String config = builder.createExtTableConfig();
            
			
           
			this.getFormHM().put("tableConfig", config.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeResource(this.frowset);
		}

	}
	/**
	 * 得到本人和普通角色特殊情况已完成任务
	 * @param dataList
	 * @param tabid
	 * @param module_id
	 * @param bs_flag
	 * @param query_type
	 * @param days
	 * @param bo
	 * @param start_date
	 * @param end_date
	 * @param tabidSet
	 * @param taskSet 
	 */
	private void getSolveByMyselfOrRole(ArrayList dataList, String tabid, String module_id, String bs_flag, String query_type, String days, 
			TemplateNavigationBo bo, String start_date, String end_date, HashSet tabidSet, HashSet taskSet) {
		StringBuffer strsql = new StringBuffer();
		ArrayList valueList=new ArrayList();
		RowSet rowset = null;
		try {
			
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
				_withNoLock=" WITH(NOLOCK) ";
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				format_str="yyyy-MM-dd hh24:mi";
			String static_="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				static_="static_o";
			}
			strsql.append("select tt.name,U.ins_id,T.task_topic,U.tabid,U.actorname fullname,(select o.codeitemid from organization o "+_withNoLock+" where o.codeitemid=U.b0110) unitname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" end_date,");
			strsql.append("T.actorname,T.actor_type,T.task_id,T.flag,U.tabid,tt."+static_+",U.finished insfinished   from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table tt "+_withNoLock+",t_wf_task_objlink Two "+_withNoLock+"");
			strsql.append("where T.ins_id=U.ins_id  and U.tabid=tt.tabid and two.task_id=t.task_id and ((t.task_type='2' ) and  (t.task_state='5'  or t.task_state='6' ) ) ");
			if(tabid!=null&&!"-1".equals(tabid)&&tabid.length()>0)
			{
				strsql.append(" and tt.tabid=? " );
				valueList.add(new Integer(tabid));
			}
			
			if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
				strsql.append(" and tt."+static_+"=? " );
				if("7".equals(module_id)){
					valueList.add(new Integer(10)); 
				}
				else if("8".equals(module_id)){
					valueList.add(new Integer(11)); 
				}
			}
			else
			{
				if(userView.getStatus() != 4 || !"9".equals(module_id)){
					strsql.append(" and tt."+static_+"!=10 and tt."+static_+"!=11 ");
				}
			}
			
			String kq_tabids="";
			if(module_id==null||!"9".equals(module_id)) //业务申请的待办无需过滤考勤模板
			{
			    kq_tabids=bo.getKqTabIds(module_id);//liuyz 考勤支持业务模版
			}
			
			if(module_id!=null&& "10".equals(module_id)){ //考勤业务办理
				if(kq_tabids.length()==0){
					strsql.append(" and 1=2 ");
				}
				else{
					strsql.append(" and U.tabid in ("+kq_tabids.substring(1)+") and tt.tabid in ("+kq_tabids.substring(1)+")  ");
				}
			}
			else if (kq_tabids.length()>0)
			{
					strsql.append(" and tt.tabid not in ("+kq_tabids.substring(1)+")" );
			}
			
			//1：审批任务 2：加签任务 3：报备任务  4：空任务 
			strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"=? ");
			valueList.add(bs_flag);
			String a0100=this.userView.getDbname()+this.userView.getA0100();
			if(a0100==null||a0100.length()==0)
				a0100=this.userView.getUserName();
			strsql.append(" and ((t.actor_type='5' and two.username='"+a0100+"') or (t.actor_type='2' and ("+Sql_switcher.isnull("two.special_node","0")+"=0 or ("+Sql_switcher.isnull("two.special_node","0")+"=1 and (lower(two.username)='"+this.userView.getUserName().toLowerCase()+"' "); 
			if(this.userView.getA0100()!=null&& this.userView.getA0100().trim().length()>0)//liuyz bug 32108 业务用户没有关联自助用户this.userView.getDbname().toLowerCase()+this.userView.getA0100()结果为空串，会查出不属于这个人的数据。
				strsql.append(" or lower(two.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ");
			strsql.append(" )))))");
			strsql.append(" and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"),"' '")+"<>'重新分派'  "); 
			strsql.append(" and 1=1 ");
			if("1".equals(query_type))//最近多少天
			{
					if(bo.validateNum(days)){
						String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
						strsql.append(" and U.start_date>=");
						strsql.append(strexpr);	
					}
			}else{
					if("2".equals(query_type)){
						strsql.append(" and ( 1=1 ");
						if(bo.validateDate(start_date)){
							strsql.append(PubFunc.getDateSql(">=","U.start_date",start_date));
						}
						if(bo.validateDate(end_date)){
							strsql.append(PubFunc.getDateSql("<=","U.start_date",end_date));
						}
						strsql.append(" )");
					}
			} 
			int index=strsql.toString().indexOf("from");
			String subsql=strsql.toString().substring(index+4, strsql.toString().length());  //为了得到第二个from的位置，先将第一个from的位置之前的部分去掉
			index=subsql.indexOf("from");  //得到第二个from的位置，即可根据index截取要查询的sql liuzy 20151112
			String sql="";
			if(index!=-1)
				sql=" select ins_id,actorname,actor_type from t_wf_task "+_withNoLock+" where ins_id in (select  U.ins_id "+subsql.substring(index)+") and task_type='2' and  "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='3' order by ins_id";
			HashMap ins_CurrentSpInfo=new HashMap(); // 
			if(sql.length()>0)
			{ 
				RowSet rowSet=null;
				if(valueList.size()>0)
					rowSet=dao.search(sql,valueList);
				else
					rowSet=dao.search(sql);
				int ins_id=0;
				String actorname="";
				String actor_type = "";
				while(rowSet.next())
				{
					if(ins_id==0)
						ins_id=rowSet.getInt("ins_id");
					if(ins_id==rowSet.getInt("ins_id")){
						actorname+=","+rowSet.getString("actorname");
						actor_type +=","+ rowSet.getString("actor_type");
					}else
					{
						if(actorname.toString().length()>1&&actor_type.length()>1)//liuyz 排除actor_type和actorname为空值的情况
						{
							ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1)+"`"+actor_type.substring(1));
							ins_id=rowSet.getInt("ins_id");
							actorname=","+rowSet.getString("actorname");
							actor_type =","+ rowSet.getString("actor_type");
						}
					} 
				}
				PubFunc.closeDbObj(rowSet);
				
				if(ins_id!=0&&actorname.length()>1)//liuyz 排除actor_type和actorname为空值的情况
					ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1)+"`"+actor_type.substring(1));
			}
			
			strsql.append(" order by T.end_date DESC");
			rowset=dao.search(strsql.toString(),valueList);
			HashMap operationTypeMap=new HashMap();
			HashSet set = new HashSet();
			while(rowset.next()) {
				LazyDynaBean abean=new LazyDynaBean();
				String _tabid = rowset.getString("tabid");
				tabidSet.add(_tabid);
				String task_id=rowset.getString("task_id");
				String actor_type=rowset.getString("actor_type");
				if("2".equals(actor_type)&&set.contains(task_id))
					continue;
				set.add(task_id);
				String ins_id=rowset.getString("ins_id");
				String tabname=rowset.getString("name");
				TemplateParam param = new TemplateParam(this.frameconn,this.userView,Integer.valueOf(_tabid));
				String operationType="";
				if(operationTypeMap.get(_tabid)==null)
				{
					operationType=findOperationType(_tabid);
					operationTypeMap.put(_tabid, operationType);
				}
				else
					operationType=(String)operationTypeMap.get(_tabid);
				if(ins_CurrentSpInfo.get(ins_id)!=null){
					abean.set("sp_info",((String)ins_CurrentSpInfo.get(ins_id)).split("`")[0]);
					abean.set("actor_type", ((String)ins_CurrentSpInfo.get(ins_id)).split("`")[1]);
				}else{
					abean.set("sp_info","");
					abean.set("actor_type","");
				}
				String task_topic = "";
				if("5".equals(actor_type)) {
					task_topic = tabname+"("+userView.getUserFullName()+",共1人)";
				}else if("2".equals(actor_type)) {
					task_topic = tabname+getTopic(task_id,"templet_"+_tabid,Integer.parseInt(operationType),_tabid,module_id,param);
				}
				abean.set("task_topic",task_topic);
				abean.set("ins_id",rowset.getString("ins_id"));
				abean.set("tabid",rowset.getString("tabid"));
				abean.set("fullname",rowset.getString("fullname"));
				String unitname=rowset.getString("unitname")==null?"":rowset.getString("unitname");
				abean.set("unitname",unitname);
				abean.set("start_date", rowset.getString("start_date"));
				abean.set("end_date", rowset.getString("end_date")==null?"":rowset.getString("end_date"));
				
				/**安全改造，将参数加密**/
				abean.set("task_id",PubFunc.encrypt(rowset.getString("task_id")));
				String insfinished = rowset.getString("insfinished");
				if("5".equals(insfinished)){
					abean.set("flag", "结束");
				}else if("6".equals(insfinished)){//lis 20160520
					abean.set("flag", "终止");
				}else{
					abean.set("flag", "等待");
				}
				if(!taskSet.contains(task_id)) {
					taskSet.add(task_id);
					dataList.add(abean);
				}
			}
			dataList = PubFunc.sortList("end_date", "DESC", "D", "0", "yyyy-MM-dd HH:mm", dataList);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowset);
		}
	}
	private String getInsFilterWhere(String othername)
	{
		String _withNoLock="";
		if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
			_withNoLock=" WITH(NOLOCK) ";
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
		if(userid==null||userid.length()==0)
		userid="-1";
		strwhere.append(" upper("+othername+"actorid) in ('");
		strwhere.append(userid.toUpperCase());
		strwhere.append("','");
		strwhere.append(this.userView.getUserName().toUpperCase());
		strwhere.append("') ");
		if(this.userView.getRolelist().size()>0)
		{
			strwhere.append(" or  ( "+othername+"actor_type=2 and upper("+othername+"a0100) in ('"+userid.toUpperCase()+"','"+this.userView.getUserName().toUpperCase()+"') and  upper("+othername+"actorid) in ( ");
			String str="";
			for(int i=0;i<this.userView.getRolelist().size();i++)
			{
				str+=",'"+(String)this.userView.getRolelist().get(i)+"'";
			}
			strwhere.append(str.substring(1));
			strwhere.append(" ) )");
			//本人和有范围的角色
			strwhere.append("  or(( T.actor_type=2 or  T.actor_type=5 )and exists (select null from t_wf_task_objlink "+_withNoLock+" where ins_id=U.ins_id and task_id=T.task_id and node_id= T.node_id and tab_id=U.tabid and (state=1 or state=2) and upper(username)='"+this.userView.getUserName().toUpperCase()+"' ))");
		}
		String a0100=this.userView.getDbname()+this.userView.getA0100();
		if(a0100==null||a0100.length()==0)
			a0100=this.userView.getUserName();
		/**组织元*/
		strwhere.append(" or (( T.actor_type='3'  or T.actor_type='5'  ) and T.a0100='"+a0100+"')");   //2016-05-20   dengcan 追加了 actor_type='5' ,解决本人审批看不到问题，这块很乱，得花时间重新梳理
		return " ( "+strwhere.toString()+" ) ";
	}
	private String getInsFilterWhere2(String othername)
	{
		String _withNoLock="";
		if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
			_withNoLock=" WITH(NOLOCK) ";
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
		if(userid==null||userid.length()==0)
			userid="-1";
		strwhere.append(" upper("+othername+"actorid) in ('");
		strwhere.append(userid.toUpperCase());
		strwhere.append("','");
		strwhere.append(this.userView.getUserName().toUpperCase());
		strwhere.append("') ");
		strwhere.append(" or  ( (");
		if(this.userView.getRolelist().size()>0)
		{
			//strwhere.append(" ( "+othername+"actor_type=2 and upper("+othername+"a0100) in ('"+userid.toUpperCase()+"','"+this.userView.getUserName().toUpperCase()+"') and  upper("+othername+"actorid) in ( ");
			strwhere.append(" ( "+othername+"actor_type=2 and  upper("+othername+"actorid) in ( ");
			String str="";
			for(int i=0;i<this.userView.getRolelist().size();i++)
			{
				str+=",'"+(String)this.userView.getRolelist().get(i)+"'";
			}
			strwhere.append(str.substring(1));
			strwhere.append(" )) or ");
		}
		strwhere.append(othername+"actor_type=5 ) ");
		strwhere.append(" and exists (select null from t_wf_task_objlink "+_withNoLock+" where ins_id=U.ins_id and task_id="+othername+"task_id and node_id="+othername+"node_id and tab_id=U.tabid and (state=1 or state=2) and upper(username) in ('"+this.userView.getUserName().toUpperCase()+"','"+userid.toUpperCase()+"') ) " );
		String a0100=this.userView.getDbname()+this.userView.getA0100();
		if(a0100==null||a0100.length()==0)
			a0100=this.userView.getUserName();
		
		/**组织元*/
		strwhere.append(" or ( "+othername+"actor_type='3'   and "+othername+"a0100='"+a0100+"')");   //2016-05-20   dengcan 追加了 actor_type='5' ,解决本人审批看不到问题，这块很乱，得花时间重新梳理
		return " ( "+strwhere.toString()+" ) ";
	}
	public String findTabName(String tabid)
	{
		String tabName="";
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rowSet=dao.search("select name from template_table where tabid="+tabid);
			if(rowSet.next())
				tabName=rowSet.getString("name");
			if(rowSet!=null)
				rowSet.close(); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return tabName;
	}
	
	public String findOperationType(String tabid)
	{
		String operationType="";
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rowSet=dao.search("select operationtype from operation where operationcode=(select operationcode from template_table where tabid="+tabid+")");
			if(rowSet.next())
				operationType=rowSet.getString("operationtype");
			if(rowSet!=null)
				rowSet.close(); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return operationType;
	}
	/**
	 * 求实际的业务数,本次模板做了多少人的业务
	 * @param param 
	 * @return
	 */
	public String getRecordBusiTopicByState(int task_id,int state,String tabname,ContentDAO dao,int operationtype,String type, TemplateParam param)
	{
		String _withNoLock="";
		if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
			_withNoLock=" WITH(NOLOCK) ";
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		try
		{ 
			StringBuffer strsql=new StringBuffer(); 
			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}
			if(param.getInfor_type()==2 || param.getInfor_type()==3)//如果是单位管理机构调整 或 岗位管理机构调整
			{
				a0101="codeitemdesc_1";
				if(operationtype==5)
					a0101="codeitemdesc_2";
			}
			String strWhere=" where "; 
			String strWhere2="";
			if(state==0)
				strWhere2=" and (state is null or  state=0) ";
			else
				strWhere2=" and state="+state+" "; 
			strWhere+=" exists (select null from t_wf_task_objlink "+_withNoLock+" where "+tabname+".seqnum=t_wf_task_objlink.seqnum and task_id="+task_id+" "+strWhere2+"  )";
			strsql.append("select  ");
			strsql.append(a0101);
			strsql.append(" from ");
			strsql.append(tabname+_withNoLock);
			strsql.append(strWhere);
			RowSet rset=dao.search(strsql.toString());			
			int i=0;
			while(rset.next())
			{
				if(i>4)
					break;
				if(i!=0)
					stopic.append(",");
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			strsql.setLength(0);

			strsql.append("select count(*) as nmax from ");
			strsql.append(tabname+_withNoLock);
			strsql.append(strWhere.toString());
		
			rset=dao.search(strsql.toString());
			if(rset.next())
				nmax=rset.getInt("nmax");
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			stopic.append(nmax);
			if(type!=null&&("7".equals(type)|| "8".equals(type)))//如果是单位管理机构调整 或 岗位管理机构调整
				stopic.append("条记录 ");
			else
				stopic.append("人"); 
			if(state==3)
				stopic.append(" 被撤销");
			stopic.append(")");
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
	}
	public String getTopic(String task_id,String tabname,int operationtype,String tab_id,String type, TemplateParam param)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
				_withNoLock=" WITH(NOLOCK) ";
			ContentDAO dao=new ContentDAO(this.frameconn);
			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
				a0101="a0101_2";
			}
            if(param.getInfor_type()==2 || param.getInfor_type()==3){//如果是单位管理机构调整 或 岗位管理机构调整
			//if(type!=null&&(type.equals("7")||type.equals("8")))//如果是单位管理机构调整 或 岗位管理机构调整 
				a0101="codeitemdesc_1";
				if(operationtype==5)
					a0101="codeitemdesc_2";
			}
			String sql="";
			String seqnum="1";
			RowSet rset=null;
			rset=dao.search("select seqnum from "+tabname+""+_withNoLock+" where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )");
			if(rset.next())
				seqnum=rset.getString(1)!=null?rset.getString(1):"";
			if(seqnum.length()>0)
			{
				sql=" select "+a0101+" from "+tabname+""+_withNoLock+",t_wf_task_objlink two "+_withNoLock+" where "+tabname+".seqnum=two.seqnum and "+tabname+".ins_id=two.ins_id "
						  +" and two.task_id="+task_id+" and two.tab_id="+tab_id +" and ( "+Sql_switcher.isnull("two.state","0")+"<>3 )  and ("+Sql_switcher.isnull("two.special_node","0")+"=0  or ( "+Sql_switcher.isnull("two.special_node","0")+"=1 and (lower(two.username)='"+this.userView.getUserName().toLowerCase()+"' or lower(two.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
			}
			else
			{
				sql=" select "+a0101+" from "+tabname+" "+_withNoLock+"  where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )";
			}  
			rset=dao.search(sql);			
			int i=0;
			while(rset.next())
			{
				if(i>4)
					break;
				if(i!=0)
					stopic.append(",");
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			if(i>4)
			{
				if(seqnum.length()>0)
					sql="select count(*)  from t_wf_task_objlink "+_withNoLock+"  where task_id="+task_id+" and tab_id="+tab_id +"  and ( "+Sql_switcher.isnull("state","0")+"<>3 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
		 		else
					sql=" select count(*) from "+tabname+" "+_withNoLock+" where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )";  
				rset=dao.search(sql);
					if(rset.next())
						nmax=rset.getInt(1);
			}
			else
				nmax=i;
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));		
			stopic.append(nmax);
			if(param.getInfor_type()==2 || param.getInfor_type()==3)//如果是单位管理机构调整 或 岗位管理机构调整
				stopic.append("条记录)");
			else
				stopic.append("人)");
			if(rset!=null)
				rset.close();
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
		
	}
}
