package com.hjsj.hrms.businessobject.general.template.workflow;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.module.template.templatenavigation.businessobject.TemplateNavigationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TemplatePendingTaskBo {
	private UserView userview;
	private Connection conn=null;
	
	public TemplatePendingTaskBo(Connection conn,UserView userview){
		this.conn = conn;
		this.userview=userview;
	}
	
	
	
    
    
    /**
     * 获得人事异动的待审批任务
     * @param LazyDynaBean paramBean
     *   start_date //开始时间
     *   end_date 结束时间
     *   days 最近几天
     *   query_type 按日期or按时间段 1 or other  
     *   tabid 模板号
     *   module_id 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审 12、证照管理
     *   bs_flag 任务类型 1：审批任务 2：加签任务 3：报备任务  4：空任务 10：首页待办任务,包含审批任务和报备任务
     * @param userView
     * @return ArrayList
     */
    public ArrayList getDBList(LazyDynaBean paramBean,UserView userView)
    {
    	ArrayList list=new ArrayList();
    	try
    	{
    		TemplateNavigationBo bo = new TemplateNavigationBo(this.conn,userView);
    		HashSet tabidSet=new HashSet();
    		String module_id=(String)paramBean.get("module_id");
    		String kq_tabids="";
    		this.isHaveIndex();
			if(module_id==null||!"9".equals(module_id)) //业务申请待办无需过滤考勤模板 
			{
			//	kq_tabids=bo.getKqTabIds(module_id);//liuyz 考勤支持业务模版 
				TemplateTableParamBo tp=new TemplateTableParamBo(this.conn); 
		        kq_tabids=tp.getAllDefineKqTabs(0);
			}
			paramBean.set("kq_tabids", kq_tabids);
    		LazyDynaBean sqlRelationParam=getSqlRelationParam(paramBean,userView);//获得查询待办的SQL
    	 
    		if(!"3".equals(((String)paramBean.get("bs_flag")))){ //1：审批任务 2：加签任务 3：报备任务  4：空任务
    			list.addAll(getTmessageList(paramBean,bo,tabidSet,userView));
			}
    		
    		ArrayList dataList=new ArrayList();
    		dataList.addAll(getRecordListByUser(sqlRelationParam ,tabidSet,userView)); //获得报批给人员|用户|机构 审批节点的待办任务
    		if(!this.userview.isBThreeUser()){
				dataList.addAll(getRecordlistByRole(((StringBuffer)sqlRelationParam.get("from_where_sql_role")).toString(),(ArrayList)sqlRelationParam.get("valueList"),tabidSet,userView)); //获得报批给角色审批节点的待办任务
			}
    		dataList.addAll(getRecordListBySelf(sqlRelationParam ,paramBean,tabidSet,userView));
    		
    		Collections.sort(dataList,new Comparator () {
                @Override
                public int compare(Object o1, Object o2) {
                    if(o1 instanceof LazyDynaBean && o2 instanceof LazyDynaBean){
                    	SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                		Date d=new Date();
                		String current_str=simpleformat.format(d);
                    	LazyDynaBean e1 = (LazyDynaBean) o1;
                    	LazyDynaBean e2 = (LazyDynaBean) o2;
                    	String e1_start_date=e1.get("start_date")!=null?(String)e1.get("start_date"):current_str;
                    	String e2_start_date=e2.get("start_date")!=null?(String)e2.get("start_date"):current_str;
                    	Date e1_date =d; 
                    	Date e2_date =d;  
                    	try
                    	{
                    		e1_date=simpleformat.parse(e1_start_date);
                    		e2_date=simpleformat.parse(e2_start_date);
                    	}
                    	catch(Exception e)
                    	{
                    		
                    	}
                    	int value=1;
                    	if(e1_date.getTime()>e2_date.getTime()) {
							value=-1;
						} else if(e1_date.getTime()==e2_date.getTime())//liuyz bug31898
						{
							value=0;
						}
                        return value;
                    }
                    throw new ClassCastException("排序出错");
                }
            });
    		list.addAll(dataList);
    		paramBean.set("tabidSet", tabidSet);
    	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	
    	return list;
    }
    
    
    
    
    
    
    
    /**
     *  获得查询待办的SQL
     *  @return LazyDynaBean <select_sql:查询人员|用户|机构,  from_where_sql：查询人员|用户|机构  ，from_where_sql_self：查询本人 ，from_where_sql_role：角色 >
     */
    public LazyDynaBean getSqlRelationParam(LazyDynaBean paramBean,UserView userView)
    {
    	LazyDynaBean sqlRelationParam=new LazyDynaBean();
    	try
    	{
    		String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
			{
				_withNoLock=" WITH(NOLOCK) ";
			}
			String static_="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				static_="static_o";
			}
	    	String select_sql="";
			StringBuffer from_where_sql=new StringBuffer(); //查询人员|用户|机构
			StringBuffer from_where_sql_self=new StringBuffer(); //查询本人
			StringBuffer from_where_sql_role=new StringBuffer(); //角色
			ArrayList valueList=new ArrayList(); 
	    	
	    	
	    	String  start_date=paramBean.get("start_date")!=null?(String)paramBean.get("start_date"):""; //开始时间
	    	String  end_date=paramBean.get("end_date")!=null?(String)paramBean.get("end_date"):"";// 结束时间
	    	String  days=paramBean.get("days")!=null?(String)paramBean.get("days"):""; //最近几天
	    	String  query_type=paramBean.get("query_type")!=null?(String)paramBean.get("query_type"):"";// 按日期or按时间段 1 today 今天、week 本周、year 本年、quarter 本季 or other  
	    	String  tabid=paramBean.get("tabid")!=null?(String)paramBean.get("tabid"):"";// 模板号
	    	String  module_id=paramBean.get("module_id")!=null?(String)paramBean.get("module_id"):"";// 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审     100：不分模块（首页待办任务）
	    	String  bs_flag=paramBean.get("bs_flag")!=null?(String)paramBean.get("bs_flag"):"";// 任务类型  1：审批任务 2：加签任务 3：报备任务  4：空任务  10：首页待办任务,包含审批任务和报备任务  11 ：首页报备任务
	    	String kq_tabids=(String)paramBean.get("kq_tabids"); //liuyz 考勤支持业务模版 
	    	String topic_info=paramBean.get("topic_info")!=null?(String)paramBean.get("topic_info"):"";// 按照主题发送人 筛选功能
	    	
	    	TemplateNavigationBo bo = new TemplateNavigationBo(this.conn,userView);
	    	String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
				format_str="yyyy-MM-dd hh24:mi";
			}
			 
			select_sql="select U.tabid,a0101_1,task_topic ,state states,"+Sql_switcher.dateToChar("T.start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" end_date,U.b0110  unitname,task_pri,bread,bfile,task_id ,T.ins_id,U.template_type,T.actor_type,T.actorid,T.node_id,"+Sql_switcher.isnull("T.bs_flag","'1'")+" bs_flag ";
			from_where_sql.append(" from t_wf_task T"+_withNoLock+",t_wf_instance U"+_withNoLock+",template_table tt "+_withNoLock+""); 
			from_where_sql.append(" where T.ins_id=U.ins_id and ( task_topic not like '%,共0人)' and task_topic not like '%,共0条记录)' )  "); //20080825解决审批时，把当前审批表的中人员删除掉，这种任务暂不列不出,处理方式有点问题。
	
			if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
				from_where_sql.append(" and U.tabid=tt.tabid and tt."+static_+"=?   ");
				if("7".equals(module_id)){
					valueList.add(new Integer(10));
				}
				else{
					valueList.add(new Integer(11));
				}
			}
			else
			{
				from_where_sql.append(" and U.tabid=tt.tabid   ");
				if(!"100".equals(module_id)&&(userView.getStatus() != 4 || !"9".equals(module_id))) //自助用户在自助服务中的待办中可以看到所有
				{
					from_where_sql.append(" and tt."+static_+"!=10 and tt."+static_+"!=11   "); 
				}
			}
			
			if(tabid!=null&&!"-1".equals(tabid)&&tabid.length()>0)
			{
				from_where_sql.append(" and tt.tabid=? " );
				valueList.add(new Integer(tabid));
			}
			String zz_tabids = bo.getBusinessTabid("12");
			if(module_id!=null&& "10".equals(module_id)){ //考勤业务办理
				if(kq_tabids.length()==0){
					from_where_sql.append(" and 1=2 ");
				}
				else{
					from_where_sql.append(" and U.tabid in ("+kq_tabids.substring(1)+")  and tt.tabid in ("+kq_tabids.substring(1)+")  ");
				}
			}
			else if(module_id!=null&&"12".equals(module_id)) {//证照管理
				if(zz_tabids.length()==0){
					from_where_sql.append(" and 1=2 ");
				}
				else{
					from_where_sql.append(" and U.tabid in ("+zz_tabids+")  and tt.tabid in ("+zz_tabids+")  ");
				}
			}
			else if ((kq_tabids.length()>0||zz_tabids.length()>0)&&!"100".equals(module_id))
			{ 
				if(kq_tabids.length()>0) {
					from_where_sql.append(" and U.tabid not in ("+kq_tabids.substring(1)+")  and tt.tabid  not in ("+kq_tabids.substring(1)+")   ");
				}
				if(zz_tabids.length()>0) {
					from_where_sql.append(" and U.tabid not in ("+zz_tabids+")  and tt.tabid  not in ("+zz_tabids+")   ");
				}
			}
			
			
			//1：审批任务 2：加签任务 3：报备任务  4：空任务   10：首页待办任务,包含审批任务和报备任务
			if("10".equals(bs_flag))
			{
				  //我的任务出现报备的待办信息(未阅读), 2014-05-06  dengcan
				from_where_sql.append(" and ( ("+Sql_switcher.isnull("T.bs_flag","'1'")+"='1' and U.finished='2' ) or ( "+Sql_switcher.isnull("T.bs_flag","'1'")+"='3' and bread=0  )  )  ");      
			}
			else
			{
				from_where_sql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"=?  ");
				if("11".equals(bs_flag))
				{
					from_where_sql.append(" and bread=0  ");
					valueList.add("3");
				}
				else {
					valueList.add(bs_flag);
				}
				if(!"2".equals(bs_flag)&&!"3".equals(bs_flag)&&!"11".equals(bs_flag))//审批任务或空任务或者首页报备任务
				{
					from_where_sql.append(" and U.finished='2' ");//=2:运行中
				}
			}
			from_where_sql.append(" and T.task_type='2' and T.task_state='3'");
			
			from_where_sql.append(" and (");
			from_where_sql_self.append(from_where_sql);
			from_where_sql_role.append(from_where_sql);
			
			from_where_sql.append(getTaskFilterWhere(0,userView));
			from_where_sql_self.append(" T.actor_type='5'  ) "); 
			from_where_sql_role.append(getTaskFilterWhere(1,userView).substring(3));
			
			from_where_sql.append(")");
			from_where_sql_role.append(" ) ");
			StringBuffer where_cond_str=new StringBuffer("");
			if("1".equals(query_type))//最近多少天
			{
				if(bo.validateNum(days)){
					String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
					where_cond_str.append(" and T.start_date>="); 
					where_cond_str.append(strexpr);	
				 
				}
			}
			else if("today".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
			{
				where_cond_str.append(PubFunc.getDateSql(">=","T.start_date",new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
			}else if("week".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
			{
				where_cond_str.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getWeekStart()));
			}else if("year".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
			{
				where_cond_str.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getYearStart()));
			}else if("quarter".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
			{
				where_cond_str.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getCurrentQuarterStartTime()));
			}else
			{
				where_cond_str.append(" and ( 1=1 "); 
				if(bo.validateDate(start_date)){
					where_cond_str.append(PubFunc.getDateSql(">=","T.start_date",start_date)); 
				}
				if(bo.validateDate(end_date)){
					where_cond_str.append(PubFunc.getDateSql("<=","T.start_date",end_date));  
				}
				where_cond_str.append(" )"); 
			}
			// 按照主题发送人 筛选功能
			if(StringUtils.isNotBlank(topic_info)){
				from_where_sql.append(" and (a0101_1 like '%"+topic_info+"%' or task_topic like '%"+topic_info+"%') " );
			}
			/*  去掉模板资源权限判断
			boolean isSource=false;
			if(bo.hasTemplateResource()) //是否有模板资源权限
				isSource=true;
			if(userView.isSuper_admin())
				isSource=true;
			if(!isSource){
				where_cond_str.append(" and 1=2 "); 
			}*/
			from_where_sql.append(where_cond_str.toString());
			from_where_sql_self.append(where_cond_str.toString());
			from_where_sql_role.append(where_cond_str.toString());
			
		 
			sqlRelationParam.set("valueList", valueList);
			sqlRelationParam.set("select_sql", select_sql);
			sqlRelationParam.set("from_where_sql", from_where_sql);
			sqlRelationParam.set("from_where_sql_self", from_where_sql_self);
			sqlRelationParam.set("from_where_sql_role", from_where_sql_role);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return sqlRelationParam;
    }
    
    private String getTaskFilterWhere(int flag,UserView userView)
	{
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=userView.getDbname(); //库前缀
		String userid=dbpre+userView.getA0100();//人员编号
		String orgid="UN"+userView.getUserOrgId();//单位编码
		String deptid="UM"+userView.getUserDeptId();//部门编码
		String posid="@K"+userView.getUserPosId();//  getUserOrgId();//职位编码
		/**组织元*/
		
		if(flag==0) 
		{
			strwhere.append("(T.actor_type='3' and T.actorid in ('");//=3:组织单元
			strwhere.append(orgid.toUpperCase());
			strwhere.append("','");
			strwhere.append(deptid.toUpperCase());
			strwhere.append("','");
			strwhere.append(posid.toUpperCase());
			strwhere.append("'))");
			/**人员列表*/
			strwhere.append( " or ((T.actor_type='1' or T.actor_type='4') and lower(T.actorid) in ('");//=1:人员  =4:业务用户
			strwhere.append(userid.toLowerCase());
			strwhere.append("','");
			strwhere.append(userView.getUserName().toLowerCase());
			strwhere.append("'))");
		}
		else if(flag==1) 
		{
			/**角色ID列表*/ 
		 	ArrayList rolelist= userView.getRolelist();//角色列表
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
		 		strwhere.append(" or (T.actor_type='2' and T.actorid in ("); //角色
		 		strwhere.append(strrole.toString());
		 		strwhere.append("))");
		 	}
		 	else {
				strwhere.append(" or 1=2 ");
			}
		}
		return strwhere.toString();
	}
    
    
    /**
	 * 获得消息通知单  
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getTmessageList(LazyDynaBean paramBean,TemplateNavigationBo bo,HashSet tabidSet,UserView userview) throws GeneralException
	{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
			{
				_withNoLock=" WITH(NOLOCK) ";
			}
			
			String static_="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				static_="static_o";
			}
			
			String  start_date=paramBean.get("start_date")!=null?(String)paramBean.get("start_date"):""; //开始时间
			String  end_date=paramBean.get("end_date")!=null?(String)paramBean.get("end_date"):"";// 结束时间
			String  days=paramBean.get("days")!=null?(String)paramBean.get("days"):""; //最近几天
			String  query_type=paramBean.get("query_type")!=null?(String)paramBean.get("query_type"):"";// 按日期or按时间段 1 or other  
			String  templateid=paramBean.get("tabid")!=null?(String)paramBean.get("tabid"):"";// 模板号
			String  module_id=paramBean.get("module_id")!=null?(String)paramBean.get("module_id"):"";// 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审
			String  bs_flag=paramBean.get("bs_flag")!=null?(String)paramBean.get("bs_flag"):"";// 任务类型  1：审批任务 2：加签任务 3：报备任务  4：空任务
			String kq_tabids=(String)paramBean.get("kq_tabids"); //liuyz 考勤支持业务模版
			String topic_info=paramBean.get("topic_info")!=null?(String)paramBean.get("topic_info"):"";// 按照主题发送人 筛选功能
			
			RowSet rs=null;
			ArrayList list0=new ArrayList();
			try {
				StringBuffer sql=new StringBuffer();
				StringBuffer sql2=new StringBuffer();
				sql.append("select DISTINCT Noticetempid,Template_table.name as name"); //  ,State");
				sql.append(" from tmessage "+_withNoLock+" left join Template_table "+_withNoLock+" on tmessage.Noticetempid=Template_table.tabid ");
				sql.append(" where (State='0' or State='1')");
				
				sql2.append(sql);
				ArrayList valueList=new ArrayList();

				if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
					sql.append(" and Template_table."+static_+"=?   ");
					if("7".equals(module_id)){
						valueList.add(new Integer(10));
						sql2.append(" and Template_table."+static_+"=10 ");
					}
					else{
						valueList.add(new Integer(11));
						sql2.append(" and Template_table."+static_+"=11 ");
					}
				}
				else{
					sql.append(" and Template_table."+static_+"!=10 and Template_table."+static_+"!=11 ");
				}
			
				
				if(templateid!=null&&!"-1".equals(templateid)&&templateid.length()>0)
				{
					sql.append(" and Template_table.tabid=? ");
					valueList.add(new Integer(templateid));
				}
				String zz_tabids = bo.getBusinessTabid("12");
				if(module_id!=null&& "10".equals(module_id)){ //考勤业务办理
					if(kq_tabids.length()>0){
						sql.append(" and tmessage.Noticetempid in ("+kq_tabids.substring(1)+ " ) "); 
						sql2.append(" and tmessage.Noticetempid in ("+kq_tabids.substring(1)+ " ) "); 
					}
					else{
						sql.append(" and 1=2 ");
						sql2.append(" and 1=2 ");
					}
				}
				else if(module_id!=null&&"12".equals(module_id)) {//证照管理
					if(zz_tabids.length()==0){
						sql.append(" and 1=2 ");
						sql2.append(" and 1=2 ");
					}
					else{
						sql.append(" and tmessage.Noticetempid in ("+zz_tabids+ " ) "); 
						sql2.append(" and tmessage.Noticetempid in ("+zz_tabids+ " ) "); 
					}
				}
				else if((kq_tabids.length()>0||zz_tabids.length()>0)&&!"100".equals(module_id))
				{
					if(kq_tabids.length()>0) {
						sql.append(" and tmessage.Noticetempid not in ("+kq_tabids.substring(1)+ " ) "); 
						sql2.append(" and tmessage.Noticetempid not in ("+kq_tabids.substring(1)+ " ) "); 
					}
					if(zz_tabids.length()>0) {
						sql.append(" and tmessage.Noticetempid not in ("+zz_tabids+ " ) "); 
						sql2.append(" and tmessage.Noticetempid not in ("+zz_tabids+ " ) "); 
					}
				}
				CommonData cData = null;
				 String herf="";
				ContentDAO dao=new ContentDAO(this.conn);
				
			
				DbWizard dbw=new DbWizard(this.conn);
				if(!dbw.isExistField("tmessage","b0110_self",false))
				{
					Table table = new Table("tmessage");
					Field field=new Field("B0110_Self","B0110_Self");
					field.setDatatype(DataType.STRING);
					field.setLength(30);
					table.addField(field);	
					dbw.addColumns(table);
				}
				
				ArrayList list=new ArrayList();
				ArrayList list2=new ArrayList();
				 
				//lis add 20160722
				if("1".equals(query_type))//最近多少天
				{
					if(bo.validateNum(days)){
						String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
						sql.append(" and tmessage.receive_time>=");
						sql.append(strexpr);	
						sql2.append(" and tmessage.receive_time>=");
						sql2.append(strexpr);	
				}
				}
				else if("today".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
					sql.append(PubFunc.getDateSql(">=","tmessage.receive_time",new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
					sql2.append(PubFunc.getDateSql(">=","tmessage.receive_time",new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
				}else if("week".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
					sql.append(PubFunc.getDateSql(">=","tmessage.receive_time",PubFunc.getWeekStart()));
					sql2.append(PubFunc.getDateSql(">=","tmessage.receive_time",PubFunc.getWeekStart()));
				}else if("year".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
					sql.append(PubFunc.getDateSql(">=","tmessage.receive_time",PubFunc.getYearStart()));
					sql2.append(PubFunc.getDateSql(">=","tmessage.receive_time",PubFunc.getYearStart()));
				}else if("quarter".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
					sql.append(PubFunc.getDateSql(">=","tmessage.receive_time",PubFunc.getCurrentQuarterStartTime()));
					sql2.append(PubFunc.getDateSql(">=","tmessage.receive_time",PubFunc.getCurrentQuarterStartTime()));
				}else{
					sql.append(" and ( 1=1 ");
					sql2.append(" and ( 1=1 ");
					if(bo.validateDate(start_date)){
						sql.append(PubFunc.getDateSql(">=","tmessage.receive_time",start_date));
						sql2.append(PubFunc.getDateSql(">=","tmessage.receive_time",start_date));
					}
					if(bo.validateDate(end_date)){
						sql.append(PubFunc.getDateSql("<=","tmessage.receive_time",end_date)); 
						sql2.append(PubFunc.getDateSql("<=","tmessage.receive_time",end_date)); 
					}
					sql.append(" )");
					sql2.append(" )");
				}
				
				if(valueList.size()==0) {
					rs = dao.search(sql.toString());
				} else {
					rs = dao.search(sql.toString(),valueList);
				}
				String tabid="";
				boolean isCorrect=false;
				HashMap map=new HashMap();
				HashMap map2=new HashMap();
				while(rs.next())
				{
					tabid=rs.getString("Noticetempid");
					if(tabid==null||tabid.length()<=0) {
						continue;
					}
					isCorrect=isHaveTemplateid(tabid,userview); 
					if(isCorrect&&map.get(tabid)==null)
					{
						cData=new CommonData();
						String str=getMessageTopic(rs.getString("Noticetempid"),module_id,map2,start_date,end_date,days,bo,query_type,userview);
						if(!"0".equals(str))
						{
							cData.setDataName(subText(rs.getString("name"))+str+" _通知");
							if(StringUtils.isEmpty(topic_info)){
								
							}else{
								String Noticetempid = rs.getString("Noticetempid");
								// 按照主题发送人 筛选功能
								if(((cData.getDataName().indexOf(topic_info)==-1)&&(map2==null||map2.get(Noticetempid)==null||((HashMap)map2.get(Noticetempid)).get("a0101_1")==null||((String)((HashMap)map2.get(Noticetempid)).get("a0101_1")).indexOf(topic_info)==-1))){
									continue;
								}
							}
							herf=tabid;
							cData.setDataValue(herf);
							list.add(cData);
							list2.add(rs.getString("Noticetempid"));
							map.put(tabid,"1");
						}
					}
				} 
				CommonData dt=new CommonData();
				LazyDynaBean abean=null;
	            
				for(int i=0;i<list.size();i++)
				{
					dt=(CommonData)list.get(i);
					String Noticetempid = (String)list2.get(i);
					abean=new LazyDynaBean();
					
					if(map2!=null&&map2.get(Noticetempid)!=null){
								HashMap map3 = (HashMap)map2.get(Noticetempid);
 								if(map3!=null)
								{
									if(map3.get("a0101_1")!=null){ 
											abean.set("a0101_1",(String)map3.get("a0101_1"));
									} 
									if(map3.get("start_date")!=null){
										abean.set("start_date",(String)map3.get("start_date"));
								    } 
									if(map3.get("bread")!=null&& "0".equals((String)map3.get("bread"))) {
										abean.set("bread", "0");
									} else {
										abean.set("bread","1");
									}
								}
					} 
					abean.set("states","01");
					abean.set("task_topic",dt.getDataName());
					abean.set("tabid",dt.getDataValue());
					/**安全平台改造,将涉及到的信息加密begin**/
					abean.set("task_id","0");
					abean.set("taskid_noEncrypt", "0");
					/**安全平台改造,将涉及到的信息加密end**/
					abean.set("ins_id","0");
					abean.set("template_type","");
					abean.set("ismessage","1");
					abean.set("unitname","");
					abean.set("task_pri","0"); 
					abean.set("bs_flag","0");
					abean.set("time_limit","");
			//		abean.set("bread", ""); 
					//abean.set("bfile", "");
					list0.add(abean); 
					tabidSet.add(dt.getDataValue());
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}finally{
				try
				{
					if(rs!=null) {
						rs.close();
					}
				}
				catch(Exception ee)
				{
					
				}
			}
			return list0;		
		}
    
	
	
	/**
	 * 获得报批给本人的待办任务
	 * @param sqlRelationParam
	 * @param tabidSet
	 * @param userView
	 * @return
	 */
	public ArrayList getRecordListBySelf(LazyDynaBean sqlRelationParam ,LazyDynaBean paramBean,HashSet tabidSet,UserView userView)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		RowSet rowSet2=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
			{
				_withNoLock=" WITH(NOLOCK) ";
			}
			String static_="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				static_="static_o";
			}
			String  start_date=paramBean.get("start_date")!=null?(String)paramBean.get("start_date"):""; //开始时间
			String  end_date=paramBean.get("end_date")!=null?(String)paramBean.get("end_date"):"";// 结束时间
			String  days=paramBean.get("days")!=null?(String)paramBean.get("days"):""; //最近几天
			String  query_type=paramBean.get("query_type")!=null?(String)paramBean.get("query_type"):"";// 按日期or按时间段 1 or other  
			String  templateid=paramBean.get("tabid")!=null?(String)paramBean.get("tabid"):"";// 模板号
			String  module_id=paramBean.get("module_id")!=null?(String)paramBean.get("module_id"):"";// 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审
			String  bs_flag=paramBean.get("bs_flag")!=null?(String)paramBean.get("bs_flag"):"";// 任务类型  1：审批任务 2：加签任务 3：报备任务  4：空任务
			String kq_tabids=(String)paramBean.get("kq_tabids"); //liuyz 考勤支持业务模版
			String topic_info=paramBean.get("topic_info")!=null?(String)paramBean.get("topic_info"):"";// 按照主题发送人 筛选功能
			String orderby = " order by start_date desc";
			
			ArrayList valueList=(ArrayList)sqlRelationParam.get("valueList");
			String select_sql=(String)sqlRelationParam.get("select_sql");
			StringBuffer from_where_sql_self=(StringBuffer)sqlRelationParam.get("from_where_sql_self");
			String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
				format_str="yyyy-MM-dd hh24:mi";
			}
			if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0)
			{ 
				TemplateNavigationBo bo = new TemplateNavigationBo(this.conn,userView);
				String sql="select distinct U.tabid "+from_where_sql_self.toString();
				rowSet = dao.search(sql.toString(),valueList);//为了获取模板列表，该sql在这里执行  不传进表格工具去执行了
				StringBuffer strsql=new StringBuffer("");
				LazyDynaBean bean=null;
				while(rowSet.next())
				{
					valueList=new ArrayList();
					int _tabid=rowSet.getInt("tabid");
	            	strsql.setLength(0);
	            	strsql.append("select U.tabid,T.a0101_1,task_topic ,T.state states,"+Sql_switcher.dateToChar("T.start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" end_date,U.b0110  unitname,task_pri,bread,bfile,T.task_id ,T.ins_id,U.template_type,T.actor_type,T.actorid,T.node_id,TWB.username,TWB.seqnum,tt.name,"+Sql_switcher.isnull("T.bs_flag","'1'")+" bs_flag  from t_wf_task T"+_withNoLock+",t_wf_instance U"+_withNoLock+"");
	    			strsql.append(",template_table tt "+_withNoLock+" ,t_wf_task_objlink TWB"+_withNoLock+",templet_"+_tabid+" templet "+_withNoLock+""); 
	    			strsql.append(" where T.ins_id=U.ins_id  and T.task_id=TWB.task_id  and  TWB.seqnum=templet.seqnum and TWB.ins_id=templet.ins_id  and U.tabid="+_tabid);
	    			strsql.append(" and ( task_topic not like '%,共0人)' and task_topic not like '%,共0条记录)' )  "); //20080825解决审批时，把当前审批表的中人员删除掉，这种任务暂不列不出,处理方式有点问题。
	    		
	    			if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
	    				strsql.append(" and U.tabid=tt.tabid and tt."+static_+"=?   ");
	    				if("7".equals(module_id)){
	    					valueList.add(new Integer(10)); 
	    				}
	    				else{
	    					valueList.add(new Integer(11)); 
	    				}
	    			}
	    			else
	    			{
	    				strsql.append(" and U.tabid=tt.tabid   ");
	    				if(!"100".equals(module_id)&&(userView.getStatus() != 4 || !"9".equals(module_id))) //自助用户在自助服务中的待办中可以看到所有
	    				{
	    					strsql.append(" and tt."+static_+"!=10 and tt."+static_+"!=11   "); 
	    				}
	    			}
	    			 
	    			strsql.append(" and tt.tabid=? " );
	    			valueList.add(new Integer(_tabid));
	    			String zz_tabids = bo.getBusinessTabid("12");
	    			if(module_id!=null&& "10".equals(module_id)){ //考勤业务办理
	    				if(kq_tabids.length()==0){
	    					strsql.append(" and 1=2 ");
	    				}
	    				else{
	    					strsql.append(" and U.tabid in ("+kq_tabids.substring(1)+")  and tt.tabid in ("+kq_tabids.substring(1)+")  ");
	    				}
	    			}
	    			else if(module_id!=null&&"12".equals(module_id)) {//证照管理
						if(zz_tabids.length()==0){
	    					strsql.append(" and 1=2 ");
	    				}
	    				else{
	    					strsql.append(" and U.tabid in ("+zz_tabids+")  and tt.tabid in ("+zz_tabids+")  ");
	    				}
					}
	    			else if ((kq_tabids.length()>0||zz_tabids.length()>0)&&!"100".equals(module_id))
	    			{ 
	    				if(kq_tabids.length()>0) {
							strsql.append(" and U.tabid not in ("+kq_tabids.substring(1)+")  and tt.tabid  not in ("+kq_tabids.substring(1)+")   ");
						}
	    				if(zz_tabids.length()>0) {
							strsql.append(" and U.tabid not in ("+zz_tabids+")  and tt.tabid  not in ("+zz_tabids+")   ");
						}
	    			}
	    			//1：审批任务 2：加签任务 3：报备任务  4：空任务
	    	//		strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"=?  ");
	    	//		valueList.add(bs_flag);
	    			if("10".equals(bs_flag))
	    			{
	    				  //我的任务出现报备的待办信息(未阅读), 2014-05-06  dengcan
	    				strsql.append(" and ( ("+Sql_switcher.isnull("T.bs_flag","'1'")+"='1' and U.finished='2' ) or ( "+Sql_switcher.isnull("T.bs_flag","'1'")+"='3' and bread=0  )  )  ");      
	    			}
	    			else
	    			{
	    				strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"=?  ");
	    				if("11".equals(bs_flag))
	    				{
	    					strsql.append(" and bread=0  ");
	    					valueList.add("3");
	    				}
	    				else {
							valueList.add(bs_flag);
						}
	    			} 
	    			strsql.append(" and T.task_type='2' and T.task_state='3'");
	    			strsql.append(" and ( T.actor_type='5' )   ");
	                strsql.append(" and "+Sql_switcher.isnull("TWB.state","0")+"=0 and templet.a0100='"+userView.getA0100()+"' and lower(templet.basepre)='"+userView.getDbname().toLowerCase()+"'");        
	    			if(!"2".equals(bs_flag)&&!"3".equals(bs_flag))//审批任务或空任务
	    			{
	    				strsql.append(" and U.finished='2' ");//=2:运行中
	    			}
	    			if("1".equals(query_type))//最近多少天
	    			{
	    				if(bo.validateNum(days)){
	    					String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
	    					strsql.append(" and T.start_date>=");
	    					strsql.append(strexpr);	
	    				}
	    			}else if("today".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
	    			{
	    				strsql.append(PubFunc.getDateSql(">=","T.start_date",new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
	    			}else if("week".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
	    			{
	    				strsql.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getWeekStart()));
	    			}else if("year".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
	    			{
	    				strsql.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getYearStart()));
	    			}else if("quarter".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
	    			{
	    				strsql.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getCurrentQuarterStartTime()));
	    			}
	    			else
	    			{
	    				strsql.append(" and ( 1=1 ");
	    				if(bo.validateDate(start_date)){
	    					strsql.append(PubFunc.getDateSql(">=","T.start_date",start_date));
	    				}
	    				if(bo.validateDate(end_date)){
	    					strsql.append(PubFunc.getDateSql("<=","T.start_date",end_date)); 
	    				}
	    				strsql.append(" )");
	    			}
	    			// 按照主题发送人 筛选功能
	    			if(StringUtils.isNotBlank(topic_info)){
	    				strsql.append(" and (a0101_1 like '%"+topic_info+"%' or tt.name"+Sql_switcher.concat()+"("+userView.getUserFullName()+",共1人) like '%"+topic_info+"%') " );
	    			}
	    			HashMap<String, String> timeListMap=new HashMap<String, String>();
	    			rowSet2 = dao.search(strsql.toString()+orderby,valueList);//为了获取模板列表，该sql在这里执行  不传进表格工具去执行了
	    			while(rowSet2.next()){  
	    				String task_id=rowSet2.getString("task_id");
	    				String ins_id=rowSet2.getString("ins_id");
	    				String actor_type=rowSet2.getString("actor_type");
	    				String node_id=rowSet2.getString("node_id");
	    				String seqnum=rowSet2.getString("seqnum");
	    				String username=rowSet2.getString("username")!=null?rowSet2.getString("username"):"";
	    				String tabname=rowSet2.getString("name");
	    				bean = new LazyDynaBean();
	    				//增加时间期限 字段
	    				if(timeListMap.containsKey(_tabid+"_"+node_id)){
	    					bean.set("time_limit", timeListMap.get(_tabid+"_"+node_id));
	    				}else{
	    					String time_limit=getTimeLimint(rowSet2.getString("tabid"), node_id);
	    					bean.set("time_limit", time_limit);
	    					timeListMap.put(_tabid+"_"+node_id, time_limit);
	    				}
	    			    bean.set("task_topic",tabname+"("+userView.getUserFullName()+",共1人)");
	    				bean.set("tabid", rowSet2.getString("tabid"));
	    				bean.set("states", rowSet2.getString("states")==null?"":rowSet2.getString("states"));
	    				bean.set("task_pri", rowSet2.getString("task_pri")==null?"":rowSet2.getString("task_pri"));
	    				bean.set("bread", rowSet2.getString("bread")==null?"":rowSet2.getString("bread"));
	    				//bean.set("bfile", rs.getString("bfile")==null?"":rs.getString("bfile"));
	    				bean.set("a0101_1", rowSet2.getString("a0101_1")==null?"":rowSet2.getString("a0101_1")); 
	    				bean.set("start_date", rowSet2.getString("start_date")==null?"":rowSet2.getString("start_date"));
	    				bean.set("end_date", rowSet2.getString("end_date")==null?"":rowSet2.getString("end_date"));
	    				//userd_time 为空
	    				bean.set("used_time", "");
	    				bean.set("unitname", rowSet2.getString("unitname")==null?"":rowSet2.getString("unitname"));
	    				bean.set("ins_id", rowSet2.getString("ins_id")==null?"":rowSet2.getString("ins_id"));
	    				bean.set("task_id", PubFunc.encrypt(rowSet2.getString("task_id")==null?"":rowSet2.getString("task_id")));
	    				bean.set("taskid_noEncrypt", rowSet2.getString("task_id")==null?"":rowSet2.getString("task_id"));
	    				bean.set("ismessage","0"); 
	    				bean.set("bs_flag",rowSet2.getString("bs_flag")); //任务类型  1：审批任务 2：加签任务 3：报备任务  4：空任务
	    				bean.set("node_id",node_id); 
	    				if(username==null||username.trim().length()==0)
						{
							// bug9992 本人都能看到，不需要加usrname标志。wangrd 2015-06-10
							dao.update("update t_wf_task_objlink set special_node=1,username='"+userView.getDbname().toUpperCase()+userView.getA0100()+"' where seqnum='"+rowSet2.getString("seqnum")+"' and task_id="+rowSet2.getString("task_id"));
						}
	    				list.add(bean);
	    				tabidSet.add(rowSet2.getString("tabid"));
	    			}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			{
				if(rowSet!=null) {
					rowSet.close();
				}
				if(rowSet2!=null) {
					rowSet2.close();
				}
			}
			catch(Exception ee)
			{
				
			}
		}
		return list;
	}
	
	
	
	/**
	 * 获得报批给人员|用户|机构 审批节点的待办任务
	 * @param sqlRelationParam
	 * @param tabidSet
	 * @param userView
	 * @return
	 */
	public ArrayList getRecordListByUser(LazyDynaBean sqlRelationParam ,HashSet tabidSet,UserView userView)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList valueList=(ArrayList)sqlRelationParam.get("valueList");
			String select_sql=(String)sqlRelationParam.get("select_sql");
			StringBuffer from_where_sql=(StringBuffer)sqlRelationParam.get("from_where_sql");
			String orderby = " order by start_date desc";
			
			HashMap<String, String> timeListMap=new HashMap<String, String>();
			LazyDynaBean bean = null; 
			RowSet rs = dao.search(select_sql+from_where_sql.toString()+orderby,valueList);//为了获取模板列表，该sql在这里执行  不传进表格工具去执行了 
			while(rs.next()){ 
				String _tabid=rs.getString("tabid");
				String task_id=rs.getString("task_id");
				String ins_id=rs.getString("ins_id");
				String actor_type=rs.getString("actor_type");
				String node_id=rs.getString("node_id");
				bean = new LazyDynaBean(); 
				if(timeListMap.containsKey(_tabid+"_"+node_id)){
					bean.set("time_limit", timeListMap.get(_tabid+"_"+node_id));
				}else{
					String time_limit=getTimeLimint(_tabid, node_id);
					bean.set("time_limit", time_limit);
					timeListMap.put(_tabid+"_"+node_id, time_limit);
				}
				 
			    bean.set("task_topic", rs.getString("task_topic")==null?"":rs.getString("task_topic"));
				bean.set("tabid", rs.getString("tabid"));
				bean.set("states", rs.getString("states")==null?"":rs.getString("states"));
				bean.set("task_pri", rs.getString("task_pri")==null?"":rs.getString("task_pri"));
				bean.set("bread", rs.getString("bread")==null?"":rs.getString("bread"));
				//bean.set("bfile", rs.getString("bfile")==null?"":rs.getString("bfile"));
				bean.set("a0101_1", rs.getString("a0101_1")==null?"":rs.getString("a0101_1")); 
				bean.set("start_date", rs.getString("start_date")==null?"":rs.getString("start_date"));
				bean.set("end_date", rs.getString("end_date")==null?"":rs.getString("end_date"));
				//userd_time 为空
				bean.set("used_time", "");
				bean.set("unitname", rs.getString("unitname")==null?"":rs.getString("unitname"));
				bean.set("ins_id", rs.getString("ins_id")==null?"":rs.getString("ins_id"));
				bean.set("task_id", PubFunc.encrypt(rs.getString("task_id")==null?"":rs.getString("task_id")));
				bean.set("taskid_noEncrypt", rs.getString("task_id")==null?"":rs.getString("task_id"));
				bean.set("ismessage","0"); 
				bean.set("bs_flag",rs.getString("bs_flag")); //任务类型  1：审批任务 2：加签任务 3：报备任务  4：空任务
				bean.set("node_id",node_id); 
				list.add(bean);
				tabidSet.add(rs.getString("tabid"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			{
				if(rowSet!=null) {
					rowSet.close();
				}
			}
			catch(Exception ee)
			{
				
			}
		}
		return list;
	}
	
	
	
    
    /**
     * 获得报批给角色审批节点的待办任务
     * @param from_where_sql_role
     * @param valueList
     * @param tabidSet
     * @param userView
     * @return
     */
	public ArrayList getRecordlistByRole(String from_where_sql_role,ArrayList valueList,HashSet tabidSet,UserView userView)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		RowSet rowSet2=null;
		RowSet rowSet3=null;
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
			{
				_withNoLock=" WITH(NOLOCK) ";
			}
			String static_="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				static_="static_o";
			}
			//人事异动抢单超时时间
			String timeoutUration = SystemConfig.getPropertyValue("timeoutUration");
			if(StringUtils.isBlank(timeoutUration)) {
				timeoutUration = "30";
			}
			if(StringUtils.isNotBlank(timeoutUration)&&Integer.parseInt(timeoutUration)<10) {
				timeoutUration = "10";
			}
			Document doc=null;
			Element element=null;
			HashMap _rolePropertyMap=new HashMap();
			ContentDAO dao=new ContentDAO(this.conn); 
			//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
			ArrayList usernameList=PubFunc.SearchOperUserOrSelfUserName(this.userview);
			String sql_tab="";
			sql_tab="select distinct U.tabid,T.node_id,T.actorid,tt."+static_+",tt.operationcode "+from_where_sql_role.toString();
			rowSet=dao.search(sql_tab,valueList); 
			while(rowSet.next())
			{
				String tabid=rowSet.getString("tabid");
				String actor_id =rowSet.getString("actorid");
				String node_id=rowSet.getString("node_id");
				String _static=rowSet.getString(static_);//10：单位  11：职位
				String operationcode=rowSet.getString("operationcode"); 
				int operationtype=findOperationType(operationcode);
				String task_idforin = "";
				String seqnumforin = "";
				StringBuffer sqlForRole = new StringBuffer("");
				sqlForRole.append("select twt.task_id,twt.seqnum,tt.state,twt.username from t_wf_task tt"+_withNoLock+",t_wf_task_objlink twt"+_withNoLock+" where tt.task_id=twt.task_id and ((");
				sqlForRole.append(Sql_switcher.isnull("twt.state","0")+"=0  and  (( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username='"+userView.getUserName()+"'  ");
				//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
				if(usernameList.size()>0){
					for(int i=0;i<usernameList.size();i++){
						sqlForRole.append(" or twt.username='"+usernameList.get(i)+"' ");
					}
				}
				sqlForRole.append( " ) ");
				sqlForRole.append(" or (("+Sql_switcher.diffMinute(Sql_switcher.sqlNow() ,Sql_switcher.isnull("twt.locked_time",Sql_switcher.sqlNow()))+">="+Integer.parseInt(timeoutUration)+" or twt.locked_time is null) and ");//diffMinute参数不能为null。
				sqlForRole.append(" ( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username<>'"+userView.getUserName()+"'   ))) ");
				sqlForRole.append(" ) or ("+Sql_switcher.isnull("twt.state","0")+"=1 and twt.task_type=3) )");//此处由于报备的记录在t_wf_task_objlink中生成时插入的state是1,导致此处查不出发送到角色的报备信息,所以要对报备的特殊处理一下
				sqlForRole.append(" and twt.tab_id="+tabid+" and twt.node_id="+node_id);
				rowSet2 = dao.search(sqlForRole.toString());
				int ii=0;
				while(rowSet2.next()){
					String state = rowSet2.getString("state");
					if(state!=null&&"07".equals(state)){
						String username = rowSet2.getString("username");
						if(StringUtils.isNotBlank(username)&&!username.equals(this.userview.getUserName())) {
							continue;
						}
					}
					String task_id = rowSet2.getString("task_id");
					String seqnum = rowSet2.getString("seqnum");
					if(ii>0){
						if(task_idforin.indexOf(task_id)==-1) {
							task_idforin+=","+task_id;
						}
						if(seqnumforin.indexOf(seqnum)==-1) {
							seqnumforin+=",'"+seqnum+"'";
						}
					}else{
						task_idforin+=task_id;
						seqnumforin+="'"+seqnum+"'";
					}
					ii++;
				}
				
				String scope_field="";
				String containUnderOrg="0"; //包含下属机构
				rowSet2=dao.search("select * from t_wf_node where tabid="+tabid+" and node_id="+node_id);
				String ext_param="";
				String time_limit="";//时间期限字段
				if(rowSet2.next()) {
					ext_param=Sql_switcher.readMemo(rowSet2,"ext_param");
				}
				if(ext_param!=null&&ext_param.trim().length()>0)
				{
						doc=PubFunc.generateDom(ext_param);; 
						String xpath="/params/scope_field";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);
						if(childlist.size()==0){
							xpath="/param/scope_field";
							 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							 childlist=findPath.selectNodes(doc);
						}
						if(childlist!=null&&childlist.size()>0)
						{
							for(int i=0;i<childlist.size();i++)
							{
								element=(Element)childlist.get(i);
								if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
								{
									scope_field=element.getText().trim();
									if(element.getAttribute("flag")!=null&& "1".equals(element.getAttributeValue("flag").trim())) {
										containUnderOrg="1";
									}
								}
							}
						}
						
						//取得时间期限字段
						xpath="/params/time_limit";
						findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List timeChildlist=findPath.selectNodes(doc);
						if(timeChildlist.size()>0){
							for(int i=0;i<timeChildlist.size();i++){
								Element item = (Element)timeChildlist.get(i);
								//time_limit valid="true" value="160.10.0"
								String value = item.getAttributeValue("value");
								String valid = item.getAttributeValue("valid");
								if("true".equalsIgnoreCase(valid)&&StringUtils.isNotEmpty(value)&&value.indexOf(".")!=-1){
									String[] timelimitArr=value.split("\\.");
									time_limit=""+(Integer.valueOf(timelimitArr[0])*24+Integer.valueOf(timelimitArr[1])+Integer.valueOf(timelimitArr[2])/60.0);
								}
							}
						}
				}
				if(scope_field==null) {
					scope_field="";
				}
				
				String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
				//如果角色特征为单位领导或部门领导，则根据直接根据角色特征过滤一下 不走业务范围 1：部门领导 6：单位领导 
				if (actor_id!=null && actor_id.length()>0){
					String role_property="";//角色特征
					if(_rolePropertyMap!=null&&_rolePropertyMap.get(actor_id)==null)
					{ 
						StringBuffer sql = new StringBuffer("select role_property from t_sys_role where role_id= '"+actor_id+"'");
						RowSet rset = dao.search(sql.toString());
						if (rset.next()){    	
							role_property= rset.getString("role_property");
						}
						_rolePropertyMap.put(actor_id,role_property);
					}
					else
					{
						role_property=(String)_rolePropertyMap.get(actor_id);
					}
						
					String filterField="";
					if ("1".equals(role_property)){//部门领导
						String e0122=this.userview.getUserDeptId();
						if (e0122!=null &&e0122.length()>0){
							//operOrg="UN"+e0122;//不知道为什么要写成UN
							operOrg="UM"+e0122;//改成UM，应该是对的 20170930
						}
						else {
							operOrg="";
						}
					}
					else if ("6".equals(role_property)){//单位领导
						String b0110=this.userview.getUserOrgId();
						if (b0110!=null &&b0110.length()>0){
							operOrg="UN"+b0110;
						}
						else {
							operOrg="";
						}
					}
					
				} 
				
				
				
				if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1||"parentid_2".equals(scope_field)|| "parentid_1".equals(scope_field)) //单独处理
				{
					list.addAll(getRecordlistBySpecialRole(from_where_sql_role,valueList,tabidSet,userView,scope_field,
							containUnderOrg,tabid,node_id,operOrg,_static,operationtype,usernameList,time_limit));
				}else{
					int index=from_where_sql_role.indexOf("where T.ins_id=U.ins_id");
					
					String from_where_sql=from_where_sql_role.substring(0,index)+",templet_"+tabid+" "+_withNoLock+"  where  T.ins_id=U.ins_id ";
					from_where_sql+=" and U.ins_id=templet_"+tabid+".ins_id ";
					from_where_sql+=from_where_sql_role.substring(index+23)+" and U.tabid="+tabid+" and T.node_id="+node_id+" and T.actorid='"+actor_id+"'";
					//from_where_sql+=" and "+Sql_switcher.isnull("twt.state","0")+"=0  and ( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username='"+userView.getUserName()+"'   ) ";
					if("UN`".equalsIgnoreCase(operOrg)||scope_field.trim().length()==0)
					{
							
					}
					else
					{
							String[] temps=scope_field.split("_");
							String itemid=temps[0].toLowerCase(); 
							FieldItem _item=DataDictionary.getFieldItem(itemid);
							String codesetid=_item.getCodesetid();
							if(operOrg!=null && operOrg.length() > 3)
							{
								StringBuffer tempSql = new StringBuffer(""); 
								String[] temp = operOrg.split("`");
								for (int i = 0; i < temp.length; i++) {
									if("1".equals(containUnderOrg))
									{
										tempSql.append(" or templet_"+tabid+"."+scope_field+" like '" + temp[i].substring(2)+ "%'");		
									}
									else
									{
										if ("UN".equalsIgnoreCase(codesetid)&& "UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
											tempSql.append(" or templet_"+tabid+"."+scope_field+"='" + temp[i].substring(2)+ "'");
										} else if ("UM".equalsIgnoreCase(codesetid)&& "UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
											tempSql.append(" or templet_"+tabid+"."+scope_field+" like '" + temp[i].substring(2)+ "%'");
										}
									}
								}
								
								if(tempSql.length()==0)
								{
									if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制 2014-04-01 dengcan
									{
										if("UN".equalsIgnoreCase(codesetid))
										{
											if("1".equals(containUnderOrg)) {
												tempSql.append(" or templet_"+tabid+"."+scope_field+" like '"+userView.getUserOrgId()+"%'");
											} else {
												tempSql.append(" or templet_"+tabid+"."+scope_field+"='"+userView.getUserOrgId()+"'");
											}
										}
										else if ("UM".equalsIgnoreCase(codesetid)){
										    if(userView.getUserDeptId()!=null&&userView.getUserDeptId().trim().length()>0){
										        tempSql.append(" or templet_"+tabid+"."+scope_field+" like '"+userView.getUserDeptId()+"%'");
										    }else{
										        tempSql.append(" or 1=2 ");
										    }
											
										}
									}
								}
								
								if(tempSql.toString().trim().length()==0) {
									tempSql.append(" or 1=2 ");
								}
								
								from_where_sql+=" and ( " + tempSql.substring(3) + " ) ";
							}
							else
							{
								if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) // 2014-04-01 dengcan
								{
									if("UN".equalsIgnoreCase(codesetid))
									{
										if("1".equals(containUnderOrg)) {
											from_where_sql+=" and templet_"+tabid+"."+scope_field+" like '"+userView.getUserOrgId()+"%'";
										} else {
											from_where_sql+=" and templet_"+tabid+"."+scope_field+"='"+userView.getUserOrgId()+"'";
										}
									}
									else if ("UM".equalsIgnoreCase(codesetid)){
									    if(userView.getUserDeptId()!=null&&userView.getUserDeptId().trim().length()>0){
									    	from_where_sql+=" and templet_"+tabid+"."+scope_field+" like '"+userView.getUserDeptId()+"%'";
									    }else{
									    	from_where_sql+=" and 1=2 ";
									    }
									}
								}
								else {
									from_where_sql+=" and 1=2 ";
								}
							}
					} 
					if(task_idforin.length()>0){
						if(ii<=99){
							from_where_sql+=" and t.task_id in ("+task_idforin+") ";
							from_where_sql+=" and templet_"+tabid+".seqnum in ("+seqnumforin+") ";
						}else{
							String sql1__ = "select twt.task_id from t_wf_task_objlink twt "+_withNoLock+" where "
									+Sql_switcher.isnull("twt.state","0")+"=0  and ( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username='"+userView.getUserName()+"' ";
							//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
							if(usernameList.size()>0){
								for(int i=0;i<usernameList.size();i++){
									sql1__+=" or twt.username='"+usernameList.get(i)+"' ";
								}
							}	
							sql1__ +="  ) and twt.tab_id="+tabid+" and twt.node_id="+node_id;
							String sql2__ = "select twt.seqnum from t_wf_task_objlink twt "+_withNoLock+" where "
									+Sql_switcher.isnull("twt.state","0")+"=0  and ( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username='"+userView.getUserName()+"' ";
							//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
							if(usernameList.size()>0){
								for(int i=0;i<usernameList.size();i++){
									sql2__+=" or twt.username='"+usernameList.get(i)+"' ";
								}
							}	
							sql2__+="  ) and twt.tab_id="+tabid+" and twt.node_id="+node_id;
							from_where_sql+=" and  t.task_id in  ("+sql1__+") ";
							from_where_sql+=" and  templet_"+tabid+".seqnum in ("+sql2__+") ";
						}
					}else {
						from_where_sql+=" and 1=2 ";
					}
					
					from_where_sql+=" order by T.task_id desc";
					String format_str="yyyy-MM-dd HH:mm";
					if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
						format_str="yyyy-MM-dd hh24:mi";
					}
					String select="select U.tabid,T.a0101_1,task_topic ,T.state states,"+Sql_switcher.dateToChar("T.start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" end_date,U.b0110  unitname,task_pri,bread,bfile,T.task_id ,T.ins_id,U.template_type,T.actor_type,T.actorid,T.node_id,tt.name,"+Sql_switcher.isnull("T.bs_flag","'1'")+" bs_flag ";
					if("10".equalsIgnoreCase(_static)||"11".equalsIgnoreCase(_static)) //单位||职位
					{
						if (operationtype == 5) {//调入型
							select+=",templet_"+tabid+".codeitemdesc_2 namedesc";
	        			} else {
	        				select+= ",templet_"+tabid+".codeitemdesc_1 namedesc";
	        			}
					}
					else  
					{
						if (operationtype == 0) {//人员调入型 
	        				select+=",templet_"+tabid+".a0101_2 namedesc";
	        			} else {
	        				select+=",templet_"+tabid+".a0101_1 namedesc";
	        			}
					} 
					rowSet2=dao.search(select+from_where_sql,valueList);
					LazyDynaBean bean = null;
					ArrayList tempList=new ArrayList();
					while(rowSet2.next())
					{
						String _tabid=rowSet2.getString("tabid");
						String task_id=rowSet2.getString("task_id");
						String ins_id=rowSet2.getString("ins_id");
						String name=rowSet2.getString("name");
						bean = new LazyDynaBean(); 
						bean.set("task_topic", rowSet2.getString("task_topic")==null?"":rowSet2.getString("task_topic"));
						bean.set("tabid", rowSet2.getString("tabid"));
						bean.set("states", rowSet2.getString("states")==null?"":rowSet2.getString("states"));
						bean.set("task_pri", rowSet2.getString("task_pri")==null?"":rowSet2.getString("task_pri"));
						bean.set("bread", rowSet2.getString("bread")==null?"":rowSet2.getString("bread")); 
						bean.set("a0101_1", rowSet2.getString("a0101_1")==null?"":rowSet2.getString("a0101_1")); 
						bean.set("start_date", rowSet2.getString("start_date")==null?"":rowSet2.getString("start_date"));
						bean.set("end_date", rowSet2.getString("end_date")==null?"":rowSet2.getString("end_date"));
						//userd_time 为空
						bean.set("used_time", "");
						bean.set("unitname", rowSet2.getString("unitname")==null?"":rowSet2.getString("unitname"));
						bean.set("ins_id", rowSet2.getString("ins_id")==null?"":rowSet2.getString("ins_id"));
						bean.set("task_id", PubFunc.encrypt(rowSet2.getString("task_id")==null?"":rowSet2.getString("task_id")));
						bean.set("taskid_noEncrypt", rowSet2.getString("task_id")==null?"":rowSet2.getString("task_id"));
						bean.set("ismessage","0");
						bean.set("tabname",name);
						bean.set("namedesc", rowSet2.getString("namedesc")==null?"":rowSet2.getString("namedesc"));
						bean.set("bs_flag", rowSet2.getString("bs_flag")); //任务类型  1：审批任务 2：加签任务 3：报备任务  4：空任务
						bean.set("node_id", node_id);
						//增加时间期限 字段
						bean.set("time_limit", time_limit);
						tempList.add(bean);
					}
					Set task_idSet=new HashSet();
					if(tempList.size()>0)
					{
						String ori_taskid="";
						int count=0;
						String namedesc_str="";
						LazyDynaBean pre_bean=null;
						for(Iterator t=tempList.iterator();t.hasNext();)
						{
							bean=(LazyDynaBean)t.next();
							String namedesc=(String)bean.get("namedesc");
							String task_id=(String)bean.get("task_id");
							String _tabid=(String)bean.get("tabid");
							if(ori_taskid.length()==0)
							{
								ori_taskid=task_id;
								count=0;
							}
							
							if(!ori_taskid.equals(task_id))
							{ 
								if(!task_idSet.contains(ori_taskid)){//可能返回的task_id不是按照顺序，导致首页代办和进入表单查询的单据数量不一致报错。
									task_idSet.add(ori_taskid);
									String task_topic=(String)pre_bean.get("tabname")+"("+namedesc_str;
									
									if("10".equalsIgnoreCase(_static)||"11".equalsIgnoreCase(_static)){//如果是单位管理机构调整 或 岗位管理机构调整
										task_topic= task_topic+ResourceFactory.getProperty("hmuster.label.total")+count+"条)";
									}else{
										task_topic= task_topic+ResourceFactory.getProperty("hmuster.label.total")+count+"人)";
									}
									pre_bean.set("task_topic", task_topic);
									
									list.add(pre_bean);
									ori_taskid=task_id;
									count=0;
									namedesc_str="";
								}
							}
							tabidSet.add(_tabid);
							if(count<4) {
								namedesc_str+=namedesc+",";
							}
							count++;
							pre_bean=bean;
						}
						if(!task_idSet.contains(ori_taskid)){
							String task_topic=(String)pre_bean.get("tabname")+"("+namedesc_str;
							if("10".equalsIgnoreCase(_static)||"11".equalsIgnoreCase(_static)){//如果是单位管理机构调整 或 岗位管理机构调整
								task_topic= task_topic+ResourceFactory.getProperty("hmuster.label.total")+count+"条)";
							}else{
								task_topic= task_topic+ResourceFactory.getProperty("hmuster.label.total")+count+"人)";
							}
							pre_bean.set("task_topic", task_topic);
							list.add(pre_bean);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			{
				if(rowSet!=null) {
					rowSet.close();
				}
			}
			catch(Exception ee)
			{
				
			}
			try
			{
				if(rowSet2!=null) {
					rowSet2.close();
				}
				if(rowSet3!=null) {
					rowSet3.close();
				}
			}
			catch(Exception ee)
			{
				
			}
		}
		return list;
	}

	/**
     * 获得报批给角色审批节点的待办任务(特殊权限控制：报批人、发起人)
     * @param from_where_sql_role
     * @param valueList
     * @param tabidSet
     * @param userView
	 * @param time_limit 
     * @return
     */
	private ArrayList getRecordlistBySpecialRole(String from_where_sql_role,ArrayList valueList,HashSet tabidSet,UserView userView,String scope_field,
			String containUnderOrg,String tabid,String node_id,String operOrg,String _static,int operationtype,ArrayList operUserArray, String time_limit)
	{
		ArrayList recordList=new ArrayList();
		RowSet rowSet=null;
		RowSet rowSet2=null;
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
			{
				_withNoLock=" WITH(NOLOCK) ";
			}
			ContentDAO dao=new ContentDAO(this.conn); 
			//定义时间格式化
			String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
				format_str="yyyy-MM-dd hh24:mi";
			}
			//人事异动抢单超时时间
			String timeoutUration = SystemConfig.getPropertyValue("timeoutUration");
			if(StringUtils.isBlank(timeoutUration)) {
				timeoutUration = "30";
			}
			if(StringUtils.isNotBlank(timeoutUration)&&Integer.parseInt(timeoutUration)<10) {
				timeoutUration = "10";
			}
			
			String sql_tab="select T.task_id "+from_where_sql_role.toString()+" and T.node_id="+node_id+" and tt.tabid="+tabid;
			rowSet=dao.search(sql_tab,valueList); 
			while(rowSet.next())
			{
				String scope_field_bak=scope_field;
				String task_id=rowSet.getString("task_id");
				
				String sql0="select t_wf_task.task_id,t_wf_task.state,t_wf_task.pri_task_id,t_wf_task.bs_flag,t_wf_task.bread,t_wf_task.a0101_1,t_wf_task.ins_id,"
						+Sql_switcher.dateToChar("t_wf_task.start_date",format_str)+" start_date";
				if ("parentid_2".equals(scope_field)|| "parentid_1".equals(scope_field)){
					sql0=sql0+ ",t."+scope_field;
				}
				
				if("10".equalsIgnoreCase(_static)||"11".equalsIgnoreCase(_static)) //单位||职位
				{
					if (operationtype == 5) {//调入型
						sql0+=",t.codeitemdesc_2 namedesc";
        			} else {
        				sql0+= ",t.codeitemdesc_1 namedesc";
        			}
				}
				else  
				{
					if (operationtype == 0) {//人员调入型 
						sql0+=",t.a0101_2 namedesc";
        			} else {
        				sql0+=",t.a0101_1 namedesc";
        			}
				} 
				
				sql0=sql0+ ",tt.tabid,tt.name,U.B0110 unitname  from  t_wf_task_objlink twt "+_withNoLock+",templet_"+tabid+" t "+_withNoLock+",t_wf_task"+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table tt "+_withNoLock+""; 
				sql0=sql0+ " where   twt.seqnum=t.seqnum and twt.ins_id=t.ins_id and twt.task_id=t_wf_task.task_id and t_wf_task.ins_id=U.ins_id   and U.tabid=tt.tabid  ";
				sql0+=" and twt.task_id="+task_id+" and ";
				sql0+=Sql_switcher.isnull("twt.state","0")+"=0  and  (( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username='"+userView.getUserName()+"'  ";
				//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
				if(operUserArray.size()>0){
					for(int i=0;i<operUserArray.size();i++){
						sql0+=" or twt.username='"+operUserArray.get(i)+"' ";
					}
				}	
				sql0+= " ) ";
				sql0+=" or (("+Sql_switcher.diffMinute(Sql_switcher.sqlNow() ,"twt.locked_time")+">="+Integer.parseInt(timeoutUration)+" or twt.locked_time is null) and";
				sql0+=" ( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username<>'"+userView.getUserName()+"'   ))) ";
				
				String codesetid="";
				boolean noSql=true;
				if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
				{ 
					if(scope_field.toUpperCase().indexOf("E0122")!=-1)
					{
						codesetid="UM";
						String value=getSubmitTaskInfo(task_id,"UM");
						if(value.length()>0)
						{ 
							scope_field_bak="'"+value+"'"; 
						}
						else
						{
							noSql=false;
							 
						}
					}
					else if(scope_field.toUpperCase().indexOf("B0110")!=-1)
					{
						codesetid="UN";  
						String value=getSubmitTaskInfo(task_id,"UN");
						if(value.length()>0)
						{
							scope_field_bak="'"+value+"'"; 
						}
						else
						{
							noSql=false;
							 
						}
					}
				}
				else if ("parentid_2".equals(scope_field)|| "parentid_1".equals(scope_field)){//岗位与单位， 取得上级组织机构的代码类
					//随机取出一条记录，取出上级组织值，主要为了得到当前上级组织类型。20160819
					rowSet2=dao.search(sql0);
					codesetid="UN";
					if (rowSet2.next()){
						String value=rowSet2.getString(scope_field);
						CodeItem codeItem = AdminCode.getCode("UN", value);
						if (codeItem==null){
							codesetid="UM";
						}
						
					}
                }
				
				if("UN`".equalsIgnoreCase(operOrg))
				{
					
				}
				else if(noSql)
				{
					
					if(operOrg!=null && operOrg.length() > 3)
					{
						StringBuffer tempSql = new StringBuffer(""); 
						String[] temp = operOrg.split("`");
						for (int i = 0; i < temp.length; i++) {
							if("1".equals(containUnderOrg))
							{
								tempSql.append(" or "+scope_field_bak+" like '" + temp[i].substring(2)+ "%'");		
							}
							else
							{
								if ("UN".equalsIgnoreCase(codesetid)&& "UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
									tempSql.append(" or "+scope_field_bak+"='" + temp[i].substring(2)+ "'");
								} else if ("UM".equalsIgnoreCase(codesetid)&& "UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
									tempSql.append(" or "+scope_field_bak+" like '" + temp[i].substring(2)+ "%'");
								}
							}
						}
						
						if(tempSql.length()==0)
						{
							if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制 2014-04-01 dengcan
							{
								if("UN".equalsIgnoreCase(codesetid))
								{
									if("1".equals(containUnderOrg)) {
										tempSql.append(" or "+scope_field_bak+" like '"+userView.getUserOrgId()+"%'");
									} else {
										tempSql.append(" or "+scope_field_bak+"='"+userView.getUserOrgId()+"'");
									}
								}
								else if ("UM".equalsIgnoreCase(codesetid)){
								    if(userView.getUserDeptId()!=null&&userView.getUserDeptId().trim().length()>0){
								        tempSql.append(" or "+scope_field_bak+" like '"+userView.getUserDeptId()+"%'");
								    }else{
								        tempSql.append(" or 1=2 ");
								    }
									
								}
							}
						}
						
						if(tempSql.toString().trim().length()==0) {
							tempSql.append(" or 1=2 ");
						}
						
						sql0+=" and ( " + tempSql.substring(3) + " ) ";
					}
					else
					{
						if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) // 2014-04-01 dengcan
						{
							if("UN".equalsIgnoreCase(codesetid))
							{
								if("1".equals(containUnderOrg)) {
									sql0+=" and "+scope_field_bak+" like '"+userView.getUserOrgId()+"%'";
								} else {
									sql0+=" and "+scope_field_bak+"='"+userView.getUserOrgId()+"'";
								}
							}
							else if ("UM".equalsIgnoreCase(codesetid)){
							    if(userView.getUserDeptId()!=null&&userView.getUserDeptId().trim().length()>0){
							        sql0+=" and "+scope_field_bak+" like '"+userView.getUserDeptId()+"%'";
							    }else{
							        sql0+=" and 1=2 ";
							    }
							}
						}
						else {
							sql0+=" and 1=2 ";
						}
					}
				}
				else {
					sql0+=" and 1=2 ";
				}
			 
			
				rowSet2=dao.search(sql0); 
				int count=0;
				String namedesc_str="";
				while(rowSet2.next())
				{
					String namedesc=rowSet2.getString("namedesc");
					
					if(count<4) {
						namedesc_str+=namedesc+",";
					}
					count++;
				}
				rowSet2.first();
				LazyDynaBean bean=null;
				if(count>0)
				{
					String tabname=rowSet2.getString("name");
					String _tabid=rowSet2.getString("tabid"); 
					String ins_id=rowSet2.getString("ins_id"); 
					bean = new LazyDynaBean(); 
					
					String task_topic=tabname+"("+namedesc_str;
					if("10".equalsIgnoreCase(_static)||"11".equalsIgnoreCase(_static)){//如果是单位管理机构调整 或 岗位管理机构调整
						task_topic= task_topic+ResourceFactory.getProperty("hmuster.label.total")+count+"条)";
					}else{
						task_topic= task_topic+ResourceFactory.getProperty("hmuster.label.total")+count+"人)";
					} 
					bean.set("task_topic", task_topic);
					bean.set("tabid",tabid);
					bean.set("states", rowSet2.getString("state")==null?"":rowSet2.getString("state"));
					bean.set("task_pri", rowSet2.getString("pri_task_id")==null?"":rowSet2.getString("pri_task_id"));
					bean.set("bs_flag", rowSet2.getString("bs_flag")==null?"":rowSet2.getString("bs_flag"));
					bean.set("bread", rowSet2.getString("bread")==null?"":rowSet2.getString("bread")); 
					bean.set("a0101_1", rowSet2.getString("a0101_1")==null?"":rowSet2.getString("a0101_1")); 
					bean.set("start_date", rowSet2.getString("start_date")==null?"":rowSet2.getString("start_date"));
					bean.set("unitname", rowSet2.getString("unitname")==null?"":rowSet2.getString("unitname"));
					bean.set("ins_id", rowSet2.getString("ins_id")==null?"":rowSet2.getString("ins_id"));
					bean.set("task_id", PubFunc.encrypt(rowSet2.getString("task_id")==null?"":rowSet2.getString("task_id")));
					bean.set("taskid_noEncrypt", rowSet2.getString("task_id")==null?"":rowSet2.getString("task_id"));
					bean.set("ismessage","0");
					bean.set("tabname",tabname); 
					bean.set("node_id",node_id);
					//增加时间期限字段
					bean.set("time_limit",time_limit); 
					recordList.add(bean);
					tabidSet.add(tabid);
				}
				
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return recordList;
	}
	

	   /**
		  * @Title: findOperationType
		  * @Description: 业务类型 对人员调入的业务单独处理
		  *               =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3内部调动, =4系统内部调动
		  *               =10其它不作特殊处理的业务 如果目标库未指定的话，则按源库进行处理
		  * @param operationcode
		  * @return
		  * @throws int
		  */
		 private int findOperationType(String operationcode) {
		     // TODO Auto-generated method stub
		     RowSet rset = null;
		     StringBuffer strsql = new StringBuffer();
		     strsql.append("select operationtype from operation where operationcode='");
		     strsql.append(operationcode);
		     strsql.append("'");
		     ContentDAO dao = new ContentDAO(this.conn);
		     int flag = -1;
		     try {
		         rset = dao.search(strsql.toString());
		         if (rset.next()) {
					 flag = rset.getInt("operationtype");
				 }
		     } catch (Exception ex) {
		         ex.printStackTrace();
		     } finally {
		         if (rset != null) {
		             try {
		                 rset.close();
		             } catch (SQLException e) {
		                 // TODO Auto-generated catch block
		                 e.printStackTrace();
		             }
		         }
		     }
		     return flag;
		 }
		 
		 /**
			 * 判断是否此模板的有权限
			 * @param list
			 * @return
			 */
		public boolean isHaveTemplateid(String tabid,UserView userView)
		{
				boolean b=false;
		        if (userView.isHaveResource(IResourceConstant.RSBD, tabid)){
		        	b=true;
		        }	
		        else if (userView.isHaveResource(IResourceConstant.GZBD, tabid)){
		        	b=true;
		        }        
		        else if (userView.isHaveResource(IResourceConstant.INS_BD, tabid)){
		        	b=true;
		        }        
		        else if (userView.isHaveResource(IResourceConstant.PSORGANS, tabid)){
		        	b=true;
		        }
		        else if (userView.isHaveResource(IResourceConstant.PSORGANS_FG, tabid)){
		        	b=true;
		        }
		        else if (userView.isHaveResource(IResourceConstant.PSORGANS_GX, tabid)){
		        	b=true;
		        }
		        else if (userView.isHaveResource(IResourceConstant.PSORGANS_JCG, tabid)){
		        	b=true;
		        }
		        else if (userView.isHaveResource(IResourceConstant.ORG_BD, tabid)){
		        	b=true;
		        }
		        else if (userView.isHaveResource(IResourceConstant.POS_BD, tabid)){
		        	b=true;
		        }
		        return b;
		}
		
		private String subText(String text)
		{
				if(text==null||text.length()<=0) {
					return "";
				}
				if(text.length()<18) {
					return text;
				}
				text=text.substring(0,18)+"...";
				return text;
		}
		
		/**
		 * 求实际的业务数,本次模板做了多少人的业务
		 * @return
		 */
		public String getMessageTopic(String Noticetempid,String  module_id,HashMap map2,String start_date,String end_date,String days,TemplateNavigationBo bo,String query_type,UserView userView)
		{
			int nmax=0;
			StringBuffer stopic=new StringBuffer();
			stopic.append("(");
			try
			{
				String _withNoLock="";
				if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
				{
					_withNoLock=" WITH(NOLOCK) ";
				}
				ContentDAO dao=new ContentDAO(this.conn);			
				DbWizard dbw=new DbWizard(this.conn);
				String sql="select distinct a0101 from tmessage "+_withNoLock+"  where  Noticetempid="+Noticetempid+" and state='0' ";
				if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
					sql="select distinct organization.codeitemdesc from tmessage "+_withNoLock+",organization "+_withNoLock+"  where  tmessage.b0110=organization.codeitemid  and Noticetempid="+Noticetempid+" and tmessage.state='0' ";
				}
				String filter_by_manage_priv="0"; //接收通知单数据方式：0接收全部数据，1接收管理范围内数据
				String include_suborg="1"; //0不包括下属单位, 1包括(默认值)

				try
				{
					RowSet frowset=dao.search("select ctrl_para from template_table "+_withNoLock+" where tabid="+Noticetempid);
					if(frowset.next())
					{
						String sxml=Sql_switcher.readMemo(frowset,"ctrl_para");        //vo.getString("ctrl_para");
						Document doc=null;
						Element element=null;
						if(sxml!=null&&sxml.trim().length()>0)
						{
							doc=PubFunc.generateDom(sxml);;
							String xpath="/params/receive_notice";
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							List childlist=findPath.selectNodes(doc);			
							if(childlist!=null&&childlist.size()>0)
							{
								element=(Element)childlist.get(0);
								 filter_by_manage_priv=(String)element.getAttributeValue("filter_by_manage_priv");
								 if(element.getAttributeValue("include_suborg")!=null) {
									 include_suborg=(String)element.getAttributeValue("include_suborg");
								 }
							}
							
						}
					}
				}
				catch(Exception e)
				{
					return "0";
				}
				
				if(!userView.isSuper_admin()&& "1".equals(filter_by_manage_priv))//不是超级用户，并且要按照权限范围接收数据
				{
					 sql+=getMessageByPriv(include_suborg,userView);
				}
				if(dbw.isExistField("tmessage", "receivetype", false)){
					sql+=" and (nullif(username,'') is null or (lower(username)='"+userView.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ";
					if(this.getRoleArr(userView).length()>0) {
						sql+=" or (username in("+this.getRoleArr(userView)+") and receivetype='2'))";
					} else {
						sql+=" )";
					}
				}else {
					sql+=" and (nullif(username,'') is null or lower(username)='"+userView.getUserName().toLowerCase()+"')";
				}
				
				if(module_id!=null&& "7".equals(module_id)) {
					sql+=" and object_type=2 ";
				} else if(module_id!=null&& "8".equals(module_id)) {
					sql+=" and object_type=3 ";
				} else {
					sql+=" and ( object_type is null or object_type=1 ) ";
				}
				
				if(dbw.isExistTable(userView.getUserName()+"templet_"+Noticetempid, false)){
					if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
							sql+=" union select codeitemdesc_1 codeitemdesc from "+userView.getUserName()+"templet_"+Noticetempid+" "+_withNoLock+" where state=1 "; 
					}else {
						sql+=" union select a0101_1 a0101 from "+userView.getUserName()+"templet_"+Noticetempid+" "+_withNoLock+"  where state=1 ";
					}
				}
				RowSet rset=dao.search(sql); 
				int i=0;
				while(rset.next())
				{
					if(i>2) {
						break;
					}
					if(i!=0) {
						stopic.append(",");
					}
					stopic.append(rset.getString(1)==null?"":rset.getString(1)); 
					i++;
				}
				
				StringBuffer sqlstr  = new StringBuffer();//查找消息库中的发送人、接收日期、审阅状态
				String sqlstr2  = "";//查找临时表关联消息库中的发送人、接收日期、审阅状态
				String receive_time="receive_time";
				if(Sql_switcher.searchDbServer()!=Constant.ORACEL){
					}else{
						receive_time="receive_time";
					}
				sql="select count(*) a from ( " ;
				String format_str="yyyy-MM-dd HH:mm";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
					format_str="yyyy-MM-dd HH24:mi";
				}
				if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){
					sql+="select distinct b0110 from tmessage "+_withNoLock+"  where  Noticetempid="+Noticetempid+" and state='0'  " ;
					sqlstr.append("select  b0110,bread,send_user,"+Sql_switcher.dateToChar(receive_time,format_str)+" receive_time,state from tmessage "+_withNoLock+"  where Noticetempid="+Noticetempid+"   " );
				}
				else{	
				 	sql+="select distinct a0100,lower(db_type) db_type  from tmessage "+_withNoLock+"  where  Noticetempid="+Noticetempid+" and state='0'  " ; 
					sqlstr.append("select  a0100,bread,send_user,"+Sql_switcher.dateToChar(receive_time,format_str)+" receive_time,state from tmessage  "+_withNoLock+" where  Noticetempid="+Noticetempid+"  " );
				}
				if(!userView.isSuper_admin()&& "1".equals(filter_by_manage_priv))
				{
					 sql+=getMessageByPriv(include_suborg,userView);
					 sqlstr.append(getMessageByPriv(include_suborg,userView));
				}
				if(dbw.isExistField("tmessage", "receivetype", false)){
					sql+=" and (nullif(username,'') is null or (lower(username)='"+userView.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ";
					sqlstr.append(" and (username is null or username='' or (lower(username)='"+userView.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null))");
					if(this.getRoleArr(userView).length()>0) {
						sql+=" or (username in("+this.getRoleArr(userView)+") and receivetype='2'))";
						sqlstr.append(" or (username in("+this.getRoleArr(userView)+") and receivetype='2'))");
					}else {
						sql+=" )";
						sqlstr.append(" )");
					}
				}else {
					sql+=" and (username is null or username='' or lower(username)='"+userView.getUserName().toLowerCase()+"')";
					sqlstr.append(" and (username is null or username='' or lower(username)='"+userView.getUserName().toLowerCase()+"')");
				}
				if(module_id!=null&& "7".equals(module_id)){
					sql+=" and object_type=2 ";
					sqlstr.append(" and object_type=2 ");
				}
				else if(module_id!=null&& "8".equals(module_id)){
					sql+=" and object_type=3 ";
					sqlstr.append(" and object_type=3 ");
				}
				else{
					sql+=" and ( object_type is null or object_type=1 ) "; 
					sqlstr.append(" and ( object_type is null or object_type=1 ) "); 
				}
				if(dbw.isExistTable(userView.getUserName()+"templet_"+Noticetempid, false)){
					if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
					    if("7".equals(module_id)){//如果是部门或者单位的话 查询的应该是b0110
					        sql+=" union select  distinct b0110  from "+userView.getUserName()+"templet_"+Noticetempid+" "+_withNoLock+"  where state=1  ";
					        sqlstr2="  select b0110 from "+userView.getUserName()+"templet_"+Noticetempid+"  "+_withNoLock+" where state=1  ";
					    }else{//如果操作的是岗位的话,查询的就应该是e01a1
					        sql+=" union select  distinct e01a1   from "+userView.getUserName()+"templet_"+Noticetempid+" "+_withNoLock+"  where state=1  ";
					        sqlstr2="  select e01a1 from "+userView.getUserName()+"templet_"+Noticetempid+" "+_withNoLock+"  where state=1  ";
					    }
					}else{
						sql+=" union select distinct a0100,lower(basepre) db_type from "+userView.getUserName()+"templet_"+Noticetempid+" "+_withNoLock+" where state=1  ";
						sqlstr2="  select a0100 from "+userView.getUserName()+"templet_"+Noticetempid+" "+_withNoLock+"  where state=1  ";
					}
				} 
				sql+=" ) aa";
				 rset=dao.search(sql);
				while(rset.next()) {
					nmax+=rset.getInt(1);
				}
				stopic.append(",");
				stopic.append(ResourceFactory.getProperty("hmuster.label.total"));			
				stopic.append(nmax);
				if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
					stopic.append("条)");	
				}else {
					stopic.append("人)");
				}
				
				
				if(nmax==0)
				{
					stopic.setLength(0);
					stopic.append("0");
				}else{
					String str ="";
					if(sqlstr2.length()>0){
						rset=dao.search(sqlstr2);
						while(rset.next()){
							str += rset.getString(1)+",";
						}
					}
					
					//lis add 20160722
					if("1".equals(query_type))//最近多少天
					{
						if(bo.validateNum(days)){
							String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
							sqlstr.append(" and tmessage.receive_time>=");
							sqlstr.append(strexpr);	
					}
					}else{
						sqlstr.append(" and ( 1=1 ");
						if(bo.validateDate(start_date)) {
							sqlstr.append(PubFunc.getDateSql(">=","tmessage.receive_time",start_date));
						}
						if(bo.validateDate(end_date)) {
							sqlstr.append(PubFunc.getDateSql("<=","tmessage.receive_time",end_date));
						}
						sqlstr.append(" )");
					}
					//lis end 20160722
					
					rset=dao.search(sqlstr.toString());
					HashMap map = new HashMap();
					String send_users =",";
					String receive_times =",";
					HashMap map4 = new HashMap();//一个发送人对应一个接收时间
					while(rset.next()){
						String a0100 = rset.getString(1);
						String state = rset.getString("state");
						if("0".equals(state)){
							map.put("bread", "0");
							if(rset.getString("send_user")!=null&&send_users.indexOf(rset.getString("send_user"))==-1){
								send_users+=rset.getString("send_user")+",";
								
							}
							if(rset.getString("receive_time")!=null){
							String time  = rset.getString("receive_time");
								receive_times+=time+",";
								if(rset.getString("send_user")!=null) {
									map4.put(rset.getString("send_user"), time);
								}
							}
						}else{
							if(str.length()>0&&a0100!=null&&a0100.length()>0&&str.indexOf(a0100+",")!=-1){
								if(rset.getString("send_user")!=null&&send_users.indexOf(rset.getString("send_user"))==-1){
									send_users+=rset.getString("send_user")+",";
								}
								if(rset.getString("receive_time")!=null){
								String time  = rset.getString("receive_time");
									receive_times+=time+",";
									if(rset.getString("send_user")!=null) {
										map4.put(rset.getString("send_user"), time);
									}
								}
							
							}
						}
					}
					send_users = send_users.replace(",,", ",");
					receive_times = receive_times.replace(",,", ",");
					while(send_users.startsWith(",")) {
						send_users = send_users.substring(1,send_users.length());
					}
					while(send_users.endsWith(",")) {
						send_users = send_users.substring(0,send_users.length()-1);
					}
					
					String temp[] =send_users.split(",");
					receive_times ="";
					for(int d=0;d<temp.length;d++){
						if(map4!=null&&map4.get(temp[d])!=null) {
							receive_times += map4.get(temp[d])+",";
						}
					}
					while(receive_times.endsWith(",")) {
						receive_times = receive_times.substring(0,receive_times.length()-1);
					}
					map.put("start_date", receive_times);
					map.put("a0101_1", send_users);
					map2.put(Noticetempid, map);
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return stopic.toString();
			}
			return stopic.toString();
		}
		
		private  String getMessageByPriv(String include_suborg,UserView userView)
		{
			String sql="";
			String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
			if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
			{
				sql+=" and ( ";
				
				if(operOrg!=null && operOrg.length() >3)
				{
					StringBuffer tempSql = new StringBuffer(""); 
					String[] temp = operOrg.split("`");
					for (int j = 0; j < temp.length; j++) { 
						 if (temp[j]!=null&&temp[j].length()>0)
						 {
							 if("0".equalsIgnoreCase(include_suborg))//不包含下属单位
							 {
								 if("UN".equalsIgnoreCase(temp[j].substring(0,2)))
								 {
									 tempSql.append(" or  tmessage.b0110_self ='" + temp[j].substring(2)+ "'");
								 }
								 else {
									 tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
								 }
							 }
							 else {
								 tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
							 }
						 }
					}
					if(tempSql.length()>0)
					{
						sql+=tempSql.substring(3);
					}
					else {
						sql+=" tmessage.b0110='##'";
					}
				}
				else {
					sql+=" tmessage.b0110='##'";
				}
				
				sql+=" or nullif(tmessage.b0110,'') is null )";
			}
			
			return sql;
		}
			
			
		
		/**
		 * 获得报批人所在的单位  或 部门
		 * @param task_id
		 * @param orgFlag UN:单位  UM：部门
		 * @return
		 */
		private String getSubmitTaskInfo(String task_id,String orgFlag)
		{
			String info="";
			ContentDAO dao=new ContentDAO(this.conn);
			String fielditem="e0122";
			if("UN".equalsIgnoreCase(orgFlag)) {
				fielditem="b0110";
			}
			RowSet rset=null;
			try
			{
				String _withNoLock="";
				if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
				{
					_withNoLock=" WITH(NOLOCK) ";
				}
				int ins_id=0;
				int node_id=0;
				String state="";
				String a0100="";//报批人的人员编号
			//	rset=dao.search("select a0100_1 from t_wf_task where task_id="+task_id);
				rset=dao.search("select ins_id,state,node_id from t_wf_task "+_withNoLock+" where task_id="+task_id);
				if(rset.next())
				{
					node_id=rset.getInt("node_id");
					ins_id=rset.getInt("ins_id");
					state=rset.getString("state");
				}
				if("07".equals(state))  //驳回
				{ 
					rset=dao.search("select a0100_1 from t_wf_task "+_withNoLock+"  where node_id="+node_id+" and ins_id="+ins_id+" and state='08' and "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='5' order by task_id desc");
				}
				else {
					rset=dao.search("select a0100_1 from t_wf_task "+_withNoLock+"  where task_id="+task_id);
				}
				if(rset.next()) {
					a0100=rset.getString(1);
				}
				if(a0100!=null&&a0100.trim().length()>0)
				{
					if(a0100.length()>3)
					{
						String dbpre=a0100.substring(0,3);
						boolean flag=false;
						ArrayList dblist=DataDictionary.getDbpreList();
						for(int i=0;i<dblist.size();i++)
						{
							if(((String)dblist.get(i)).equalsIgnoreCase(dbpre)) {
								flag=true;
							}
						}
						if(flag)
						{
							rset=dao.search("select "+fielditem+" from "+dbpre+"a01 "+_withNoLock+"  where a0100='"+a0100.substring(3)+"' ");
							if(rset.next())
							{
								info=rset.getString(1);
							}
						} 
					}
					
					if(info.length()==0)
					{
						rset=dao.search("select a0100,nbase from operuser "+_withNoLock+"  where username='"+a0100+"'");
						if(rset.next())
						{
							String _a0100=rset.getString("a0100");
							String _nbase=rset.getString("nbase");
							if(_a0100!=null&&_a0100.length()>0&&_nbase!=null&&_nbase.length()>0)
							{
								a0100 = _nbase+_a0100;
								rset=dao.search("select "+fielditem+" from "+_nbase+"a01 where a0100='"+_a0100+"' ");
								if(rset.next())
								{
									info=rset.getString(1);
								}
							}
							
						}
						
					}
					if(info.length()==0&&"UM".equalsIgnoreCase(orgFlag)) {
						String dbpre=a0100.substring(0,3);
						fielditem="b0110";
						rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
						if(rset.next())
						{
							info=rset.getString(1);
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
				try
				{
					if(rset!=null) {
						rset.close();
					}
				}
				catch(Exception e)
				{
					
				}
			}
			return info;
		}
		public void isHaveIndex() {
			StringBuffer sb = new StringBuffer("");
			try {
				ContentDAO dao=new ContentDAO(this.conn); 
				DbWizard dbwizard=new DbWizard(this.conn);
				boolean isExistsIndex = dbwizard.isExistIndex("t_wf_task_objlink", "T_WF_TASK_OBJLINKINDEX3");
				if(!isExistsIndex){
					sb.append("create index T_WF_TASK_OBJLINKINDEX3 on T_WF_TASK_OBJLINK (task_id,node_id,tab_id,seqnum,state,username)");
					dao.update(sb.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		private String getRoleArr(UserView userView) {
			ArrayList rolelist= userView.getRolelist();//角色列表
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
		 	}
			return strrole.toString();
		}
		/**
		 * 获取当前表单，当前节点的 时间期限字段  ，默认为空
		 * @param tabid ：当前表单
		 * @param node_id：当前节点
		 * @return
		 */
		public String getTimeLimint(String tabid,String node_id){
			RowSet rowSet2=null;
			Document doc=null;
			Element element=null;
			String time_limit="";//时间期限字段
			try {
				ContentDAO dao=new ContentDAO(this.conn); 
				rowSet2=dao.search("select * from t_wf_node where tabid="+tabid+" and node_id="+node_id);
				String ext_param="";
				if(rowSet2.next()) {
					ext_param=Sql_switcher.readMemo(rowSet2,"ext_param");
				}
				if(ext_param!=null&&ext_param.trim().length()>0)
				{
					doc=PubFunc.generateDom(ext_param);; 
					//取得时间期限字段
					String xpath="/params/time_limit";
					XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					List timeChildlist=findPath.selectNodes(doc);
					if(timeChildlist.size()>0){
						for(int i=0;i<timeChildlist.size();i++){
							Element item = (Element)timeChildlist.get(i);
							//time_limit valid="true" value="160.10.0"
							String value = item.getAttributeValue("value");
							String valid = item.getAttributeValue("valid");
							if("true".equalsIgnoreCase(valid)&&StringUtils.isNotEmpty(value)&&value.indexOf(".")!=-1){
								String[] timelimitArr=value.split("\\.");
								time_limit=""+(Integer.valueOf(timelimitArr[0])*24+Integer.valueOf(timelimitArr[1])+Integer.valueOf(timelimitArr[2])/60.0);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rowSet2);
			}
			return time_limit;
		}
}
