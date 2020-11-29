package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplatePendingTaskBo;
import com.hjsj.hrms.businessobject.performance.WorkPlanTaskBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.RenderRelationBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.DirectUpperPosBo;
import com.hjsj.hrms.businessobject.performance.workplanteam.WorkPlanTeamBo;
import com.hjsj.hrms.businessobject.report.report_isApprove.Report_isApproveBo;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 代办任务
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:May 31, 2008
 * </p>
 * 
 * @author sxin
 * @version 5.0
 */
public class MatterTaskList
{

    private UserView userView;
    private Connection conn;
    public String returnflag;
    public String returnURL;
    public String target;
    VersionControl verControl=null;
    
    private ArrayList beanList= new ArrayList();
    
	public ArrayList getBeanList() {
		return beanList;
	}
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getReturnflag() {
		if(this.returnflag==null||this.returnflag.length()<=0) {
            this.returnflag="7";
        }
		return returnflag;
	}
	
    /**   
     * @Title: getNewReturnflag   
     * @Description:  新人事异动的returnflag与旧的不一样，重新转换值。 
     * @param @return 
     * @return String 
     * @throws   
    */
    public String getNewReturnflag() {
        String returnFlag = getReturnflag();
        if (verControl.searchFunctionId("32027")) {
            if ("10".equals(getReturnflag())) {
                returnFlag = "12";
            } else {
                returnFlag = "11";
            }
        }
        return returnFlag;
     }
	   
	public void setReturnflag(String returnflag) {
		this.returnflag = returnflag;
	}
	public MatterTaskList()
	{
	    verControl = new VersionControl();
	}
	public MatterTaskList(Connection conn,UserView userView)
	{
		this.userView=userView;
		this.conn=conn;
		verControl = new VersionControl();
	}
	
	
	
	
	

	/**
	 * 获得业务模板已批任务
	 * @param type 1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整
	 * @param days  最近多少天
	 * @return
	 */
	public ArrayList getYpTaskList(String type,int days)
	{
		ArrayList list=new ArrayList();
		try
		{
			
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
            {
                _withNoLock=" WITH(NOLOCK) ";
            }
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strsql=new StringBuffer("");
			RowSet rowSet=null;
			String bs_flag="1";  //1：审批任务 2：加签任务 3：报备任务  4：空任务 
			String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
				format_str="yyyy-MM-dd hh24:mi";
			}
			String _static="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				_static="static_o";
			}
			strsql.append("select U.ins_id,T.task_topic,U.tabid,U.actorname fullname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" end_date,");
			strsql.append("T.actorname,T.task_id,T.flag,U.tabid,tt."+_static+",U.finished insfinished   from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table tt "+_withNoLock+"");
			strsql.append(" where  T.ins_id=U.ins_id  and U.tabid=tt.tabid and ((task_type='2' ) and task_state='5' ) ");
			
			 
			//strsql2.append("and  task_topic not like '%共0人%' and  task_topic not like '%共0条%'");
			if(type!=null&&("10".equals(type)|| "11".equals(type))){
				if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
					strsql.append(" and tt.static_o="+type );
				}else {
					strsql.append(" and tt.static="+type );
				}
				 
			}
			else
			{
				if(this.userView.getStatus()!=4) {
					if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
						strsql.append(" and tt.static_o!=10 and tt.static_o!=11 "); 
					}else {
						strsql.append(" and tt.static!=10 and tt.static!=11 "); 
					}
				}
			}
			
			
			//1：审批任务 2：加签任务 3：报备任务  4：空任务
			strsql.append(" and  "+Sql_switcher.isnull("T.bs_flag","'1'")+"='"+bs_flag+"' ");
			strsql.append(" and  T.task_type!=1 "); 
			strsql.append(" and (( (task_topic not like '%共0人%' and task_topic not like '%共0条%'  ) and ");
			strsql.append(" ( T.flag=1 and U.ins_id in (select ins_id from t_wf_task "+_withNoLock+" where "+getInsFilterWhere("")+" and task_state='5' and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("content"),"' '")+"<>'重新分派' )) )");
			strsql.append(" or ( ");
			
			strsql.append(" ("+getInsFilterWhere("T.")+" and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"),"' '")+"<>'重新分派'    )"); 
			strsql.append(" and   U.ins_id not in ( ");
			strsql.append("  select ins_id from t_wf_task "+_withNoLock+" where  ( task_topic not like '%共0人%' and  task_topic not like '%共0条%'  ) and (task_type='2' )  and task_state='5'  and flag=1 ");
			strsql.append("   and   ins_id in (select ins_id from t_wf_task "+_withNoLock+" where  "+getInsFilterWhere("")+"   and task_state='5' and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("content"),"' '")+"<>'重新分派'  ) ) ");
			strsql.append(" )");
			strsql.append(")"); 
			if(days!=0){
					String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
					strsql.append(" and U.start_date>=");
					strsql.append(strexpr);	
			}
			  
			boolean isSource=false;
			if(this.userView.isHavetemplateid(IResourceConstant.RSBD)||this.userView.isHavetemplateid(IResourceConstant.ORG_BD)||this.userView.isHavetemplateid(IResourceConstant.POS_BD)||this.userView.isHavetemplateid(IResourceConstant.GZBD)||this.userView.isHavetemplateid(IResourceConstant.INS_BD)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_FG)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_GX)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_JCG)) {
                isSource=true;
            }
			if(this.userView.isSuper_admin()) {
                isSource=true;
            }
			if(!isSource)
			{	
				strsql.append(" and 1=2 "); 
			}
			 
			strsql.append(" order by T.end_date DESC"); 
			rowSet=dao.search(strsql.toString());
			LazyDynaBean bean=new LazyDynaBean();
			HashMap tableNameMap=new HashMap();
			HashMap operationTypeMap=new HashMap();
			while(rowSet.next())
			{
				 bean=new LazyDynaBean();
				 String _tabid=rowSet.getString("tabid");
				 String tabName="";
				 String task_id=rowSet.getString("task_id");
				 
				 String operationType="";
				 if(operationTypeMap.get(_tabid)==null)
				 {
						operationType=findOperationType(_tabid);
						operationTypeMap.put(_tabid, operationType);
				 }
				 else {
                     operationType=(String)operationTypeMap.get(_tabid);
                 }
				 
				 
				 
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
				 if(userView.getStatus()!=4) {
                     topic=getTopic(task_id,"templet_"+_tabid,Integer.parseInt(operationType),_tabid,type);
                 } else
				 {
						if(type!=null&&("10".equals(type)|| "11".equals(type)))
						{
							topic=getTopic(task_id,"templet_"+_tabid,Integer.parseInt(operationType),_tabid,type);
						} 
						else {
                            topic=getTopic(task_id,"templet_"+_tabid,Integer.parseInt(operationType),_tabid,type);
                        }
				 }
				 String task_topic=tabName+topic; 
				 bean.set("task_topic",task_topic);
				 bean.set("name", rowSet.getString("fullname")!=null?rowSet.getString("fullname"):"");
				 bean.set("start_date",rowSet.getString("start_date"));
				 bean.set("end_date",rowSet.getString("end_date"));
				 String url="/general/template/edit_form.do?b_query=link&businessModel=0&tabid="+rowSet.getString("tabid")+"&ins_id="+rowSet.getString("ins_id")+"&model=yp&taskid="+rowSet.getString("task_id")+"&sp_flag=2&returnflag=8" ;
				 bean.set("url",url);
				list.add(bean);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	private  String getTopic(String task_id,String tabname,int operationtype,String tab_id,String type)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			
			String a0101="a0101_1";
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tabname.toLowerCase());	
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
				a0101="a0101_2";
			}
			
			if(type!=null&&("10".equals(type)|| "11".equals(type)))
			{
				a0101="codeitemdesc_1";
				if(operationtype==5) {
                    a0101="codeitemdesc_2";
                }
			}
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
            {
                _withNoLock=" WITH(NOLOCK) ";
            }
			String sql=" select "+a0101+" from "+tabname+" "+_withNoLock+",t_wf_task_objlink two "+_withNoLock+" where "+tabname+".seqnum=two.seqnum and "+tabname+".ins_id=two.ins_id "
					  +" and two.task_id="+task_id+" and two.tab_id="+tab_id +" and ( "+Sql_switcher.isnull("two.state","0")+"<>3 )  and ("+Sql_switcher.isnull("two.special_node","0")+"=0  or ( "+Sql_switcher.isnull("two.special_node","0")+"=1 and (lower(two.username)='"+this.userView.getUserName().toLowerCase()+"' or lower(two.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
			RowSet rset=dao.search(sql);			
			int i=0;
			while(rset.next())
			{
				if(i>4) {
                    break;
                }
				if(i!=0) {
                    stopic.append(",");
                }
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			
			sql="select count(*)  from t_wf_task_objlink "+_withNoLock+"  where task_id="+task_id+" and tab_id="+tab_id +"  and ( "+Sql_switcher.isnull("state","0")+"<>3 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
			rset=dao.search(sql);
			if(rset.next()) {
                nmax=rset.getInt(1);
            }
			//if(nmax!=i)
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			//stopic.append(ResourceFactory.getProperty("hmuster.label.total"));			
			stopic.append(nmax);
			if(type!=null&&("10".equals(type)|| "11".equals(type))) {
                stopic.append("条记录)");
            } else {
                stopic.append("人)");
            }
			if(rset!=null) {
                rset.close();
            }
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
		
	}
	
	
	private String findOperationType(String tabid)
	{
		String operationType="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select operationtype from operation where operationcode=(select operationcode from template_table where tabid="+tabid+")");
			if(rowSet.next()) {
                operationType=rowSet.getString("operationtype");
            }
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return operationType;
	}
	
	private String findTabName(String tabid)
	{
		String tabName="";
		try
		{
			/*
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select name from template_table where tabid="+tabid);
			if(rowSet.next())
				tabName=rowSet.getString("name");
			if(rowSet!=null)
				rowSet.close(); 
				*/
			RecordVo tabvo=TemplateStaticDataBo.getTableVo(Integer.parseInt(tabid), conn); //20171111 邓灿，采用缓存解决并发下压力过大问题
			if(tabvo!=null) {
                tabName=tabvo.getString("name")!=null?tabvo.getString("name"):"";
            }
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return tabName;
	}
	

	private String getInsFilterWhere(String othername)
	{
		String _withNoLock="";
		if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
        {
            _withNoLock=" WITH(NOLOCK) ";
        }
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
		if(userid==null||userid.length()==0) {
            userid="-1";
        }
		 
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
		
		if(this.userView.getUserPosId()!=null&&this.userView.getUserPosId().trim().length()>0)
		{
			strwhere.append(" or  ( "+othername+"actor_type=3 and upper("+othername+"a0100) in ('"+userid.toUpperCase()+"','"+this.userView.getUserName().toUpperCase()+"') and upper("+othername+"actorid)='@K"+this.userView.getUserPosId().trim()+"' ) ");
		}
		
		return " ( "+strwhere.toString()+" ) ";
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Description: 获取我的工作纪实列表
	 * @Version1.0 
	 * Aug 20, 2012 11:49:39 AM Jianghe created
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getWorkPlanList(ArrayList list) throws GeneralException{
		WorkPlanTeamBo wptb1 = new WorkPlanTeamBo(this.userView,this.conn);
		WorkPlanTaskBo wptb = new WorkPlanTaskBo(this.userView,this.conn);
		wptb.analyseParameter();
		String year=Calendar.getInstance().get(Calendar.YEAR)+"";
		String month=(Calendar.getInstance().get(Calendar.MONTH)+1)+"";
		String season = wptb.getSeason(month);
		String week = wptb.getWeek(new java.util.Date());
		String day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"";
		//String time = Calendar.getInstance().get(Calendar.)
		//自助用户登录
		if(this.userView.getA0100()!=null&&!"".equals(this.userView.getA0100().trim())){
			
			if(this.userView.hasTheFunction("06070101")){
				//获取年纪录
				//list = wptb.getData(year, season, month, week, day, "4", list,this.getReturnURL(),this.getTarget());
				list = wptb.getDataversion2(year, season, month, week, day, "4", list,this.getReturnURL(),this.getTarget());
			}
			if(this.userView.hasTheFunction("06070301")){
				//获取季度纪录
				//list = wptb.getData(year, season, month, week, day, "3", list,this.getReturnURL(),this.getTarget());
				list = wptb.getDataversion2(year, season, month, week, day, "3", list,this.getReturnURL(),this.getTarget());
			}
			
			if(this.userView.hasTheFunction("06070201")){
				//获取月纪录
				//list = wptb.getData(year, season, month, week, day, "2", list,this.getReturnURL(),this.getTarget());
				list = wptb.getDataversion2(year, season, month, week, day, "2", list,this.getReturnURL(),this.getTarget());
			}
			//获取周纪录
			//list = wptb1.getData(year, season, month, week, day, "1", list,this.getReturnURL(),this.getTarget());
			list = wptb.getDataversion2(year, season, month, week, day, "1", list,this.getReturnURL(),this.getTarget());
			//获取日纪录
			//list = wptb1.getData(year, season, month, week, day, "0", list,this.getReturnURL(),this.getTarget());
			list = wptb.getDataversion2(year, season, month, week, day, "0", list,this.getReturnURL(),this.getTarget());
			
		}
		return list;
	}
	
	
	/**
	 * 判断是否此模板的有权限
	 * @param list
	 * @return
	 */
	public boolean isHaveTemplateid(String tabid)
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
	
	
	
	/**
	 * 待批任务
	 * @param list
	 * @return
	 */
	public ArrayList getWaitTaskList(ArrayList list)
	{
		try{
			ArrayList dynabeanlist=new ArrayList();	
			LazyDynaBean paramBean=new LazyDynaBean();
			paramBean.set("start_date", "");
			paramBean.set("end_date", "");
			paramBean.set("days", "");
			paramBean.set("query_type", "");
			paramBean.set("tabid", "");
			paramBean.set("module_id", "100");
			paramBean.set("bs_flag", "10");
			TemplatePendingTaskBo templatePendingTaskBo=new TemplatePendingTaskBo(this.conn,this.userView);
			dynabeanlist=templatePendingTaskBo.getDBList(paramBean,this.userView);
			
			LazyDynaBean abean=null;
			HashMap viewMap=new HashMap();
			HashMap node_idMap = new HashMap();//存放节点id和表单id，用户根据节点id合并首页代办
			for(Iterator t=dynabeanlist.iterator();t.hasNext();)
			{
				abean=(LazyDynaBean)t.next();
				String tabid=(String)abean.get("tabid");
				String ismessage=(String)abean.get("ismessage");
				String ins_id = (String)abean.get("ins_id");
				String task_id = (String)abean.get("task_id");
				String taskid_noEncrypt = (String)abean.get("taskid_noEncrypt");
				String node_id = (String)abean.get("node_id");
				abean.set("isMessage", ismessage);
				String state=(String)abean.get("states");
				String desc = "报批";
            	String bs_flag=(String)abean.get("bs_flag");
                if (state != null && "07".equals(state)) {
                    desc = "驳回";
                }
                if (state != null && "06".equals(state)) {
                    desc = "结束";
                }
                if("3".equals(bs_flag)) {
                    desc="报备";
                } else if("2".equals(bs_flag)) {
                    desc="加签";
                }
                abean.set("desc",desc);
                String businessModel="0";
                if("3".equals(bs_flag)) {
                    businessModel="61";
                } else if("2".equals(bs_flag)) {
                    businessModel="71";
                }
                String pendingCode="HRMS-"+task_id; 

                String view = "";
                if(viewMap.get(tabid)==null){
                	TemplateUtilBo tb=new TemplateUtilBo(this.conn,this.userView);
                    view = tb.getTemplateView(Integer.parseInt(tabid));
                    viewMap.put(tabid, view);
                }else{
                	view=(String)viewMap.get(tabid);
                }
                abean.set("view",view);
                String herf = "";
            	if(view!=null&& "list".equalsIgnoreCase(view)){
             		herf = "/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+ins_id+"&returnflag="+this.getReturnflag()+"&task_id="+taskid_noEncrypt+"&tabid="+tabid+"&index_template=1&businessModel="+businessModel+"";
            	}else {
            		herf = "/general/template/edit_form.do?b_query=link&tabid=" + tabid+ "&pre_pendingID="+pendingCode+"&businessModel="+businessModel+"&ins_id=" + ins_id+ "&taskid=" + task_id + "&sp_flag=1&returnflag=" + this.getReturnflag() + "";
            	}
            	
            	if(PubFunc.isUseNewPrograme(this.userView)){
            	    herf = getUrl((String)abean.get("tabid"),task_id,bs_flag);
                }
            	abean.set("url",herf);
            	if (node_id!=null&&!node_idMap.containsKey(node_id))//bug 35278 从通知单过来起草状态的node_id是null
                {
                    node_idMap.put(node_id,tabid);
                }
			}
    	
			ArrayList combinelist = combineList_new(dynabeanlist,node_idMap);
            list.addAll(combinelist);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }
	private ArrayList combineList_new(ArrayList dynabeanlist,HashMap node_idMap) {
		ArrayList list = new ArrayList();
		try
		{
			TemplateTableBo tablebo = null;
			Set node_idSet=node_idMap.keySet();
			for(Object obj:node_idSet){
				String date_combine="";
				String report_date_combine="";//报备日期
				String reject_date_combine="";//驳回日期
				String apply_date_combine="";//报批日期
				String currentNode_id = (String)obj;
				String currentTabid = (String)node_idMap.get(obj);
				String approval_taskids = "";         //对于报批的任务将它们的task以','分割存储起来
				String reject_taskids = "";           //对于驳回的任务将它们的task以','分割存储起来
				String report_taskids = "";           //对于报备的任务将它们的task以','分割存储起来
				String task_topic = "";
				String desc = "";
				String url = "";
				String approval_topic = "";  //用于存储报批的主题
				int approval_num = 0;        //用于存储报批的人数
				int approval_count = 0;      //用于存储报批的条数
				String approval_url = "";    //报批的跳转路径
				String reject_topic = "";    //用于存储驳回的主题
				int reject_num = 0;          //用于存储驳回的人数
				int reject_count = 0;        //用于存储驳回的条数
				String reject_url = "";      //驳回的跳转路径
				String report_topic = "";    //用于存储报备的主题
				int report_num = 0;          //用于存储报备的人数
				int report_count = 0;        //用于存储报备的条数
				String report_url = "";      //报备的跳转路径
				String view = "";            //模板设置的显示方式
				String unit = "人";           //存储单位名称，用于拼凑报批信息的显示，当是人的时为'人'，单位或岗位时为'条记录'
				String first_task_id = "0";   //用于存储第一个task_id的值
				boolean isCombine = false;
				tablebo = new TemplateTableBo(this.conn,Integer.parseInt(currentTabid),this.userView);
				int index_a = 0;//报批
				int index_r = 0;//驳回
				int index_b = 0;//报备
				int combine_num = PubFunc.isUseNewPrograme(this.userView)?1000:50;//设定的合并的最大数
				SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Calendar cal=Calendar.getInstance();
				
				for(int i=0;i<dynabeanlist.size();i++){
					LazyDynaBean dynabean = (LazyDynaBean) dynabeanlist.get(i);
					String tabid = (String) dynabean.get("tabid");
					String task_id = (String) dynabean.get("taskid_noEncrypt");
					String task_id_Encrypt = (String) dynabean.get("task_id");
					String ins_id = (String) dynabean.get("ins_id");
					String state = (String) dynabean.get("states");
					String bs_flag = (String) dynabean.get("bs_flag");
					String node_id = (String) dynabean.get("node_id");
					if(currentNode_id.equals(node_id)){//根据节点id合并首页代办。
						if(StringUtils.isEmpty(date_combine)) {
							date_combine=(String)dynabean.get("start_date");
						}else {
							cal.setTime(simpleformat.parse(date_combine));
							long last_date=cal.getTimeInMillis();
							String date=(String)dynabean.get("start_date");
							if(StringUtils.isNotEmpty(date)) {
								cal.setTime(simpleformat.parse(date));
								long now_date=cal.getTimeInMillis();
								if(now_date-last_date<0l) {
									date_combine=date;
								}
							}
									
						}
						if("08".equals(state)) {
                            index_a++;
                        }
						if("07".equals(state)) {
                            index_r++;
                        }
						if("3".equals(bs_flag)&&PubFunc.isUseNewPrograme(this.userView)) {
                            index_b++;
                        }
						if(index_a>1||index_r>1||index_b>1){
							isCombine = true;//70以上锁使用
						}
						if(approval_count==combine_num){//70锁以上分1000合并单据70锁以下分50合并，太多的话，超过url最大长度  IE7下100也报错
                            CommonData cData = new CommonData();
                            cData.setDataName(subText(approval_topic)+"共"+ approval_num +unit+ ")_报批");
                            url=approval_url;
                            String pendingCode="HRMS-"+PubFunc.encrypt(approval_taskids); 
                            if(view!=null&& "list".equalsIgnoreCase(view)){
                                url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+1+"&task_id="+first_task_id+"&returnflag="+this.getReturnflag()+"&batch_task="+PubFunc.encrypt(approval_taskids)+"&tasklist_str="+approval_taskids+"&tabid="+currentTabid+"&sp_batch=1&index_template=1&homeflag=1";
                            }else {
                            	url = "/general/template/edit_form.do?b_query=link&tabid=" + currentTabid + "&pre_pendingID="+pendingCode+"&businessModel="+0+"&ins_id=" + 1 + "&batch_task=" + PubFunc.encrypt(approval_taskids) + "&sp_flag=1&sp_batch=1&homeflag=1&returnflag=" + this.getReturnflag() + "";
                            }
                            if(PubFunc.isUseNewPrograme(this.userView)) {
                                url = getUrl_new(currentTabid,approval_taskids,isCombine,index_a-1,"1",combine_num,state,currentNode_id);
                            }
                            cData.setDataValue(url);
                            cData.put("date", apply_date_combine);
                            date_combine="";
                            apply_date_combine="";
                            list.add(cData);
                            approval_count=0;
                            approval_num=0; 
                            approval_topic="";  
                            approval_url="";   
                            unit="人";           
                            first_task_id="0"; 
                            approval_taskids="";
                            task_topic="";
                        }
						if(reject_count==combine_num){//70锁以上分1000合并单据70锁以下分50合并，
							CommonData cData = new CommonData();
							cData.setDataName(subText(reject_topic)+"共"+ reject_num +unit+ ")_驳回");
							url=reject_url;
							String pendingCode="HRMS-"+PubFunc.encrypt(reject_taskids); 
						    if(view!=null&& "list".equalsIgnoreCase(view)){
			             		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+1+"&task_id="+first_task_id+"&returnflag="+this.getReturnflag()+"&batch_task="+PubFunc.encrypt(reject_taskids)+"&tasklist_str="+reject_taskids+"&tabid="+currentTabid+"&sp_batch=1&index_template=1&homeflag=1";
			            	}else {
			            		url = "/general/template/edit_form.do?b_query=link&tabid=" + currentTabid + "&pre_pendingID="+pendingCode+"&businessModel="+0+"&ins_id=" + 1 + "&batch_task=" + PubFunc.encrypt(reject_taskids) + "&sp_flag=1&sp_batch=1&homeflag=1&returnflag=" + this.getReturnflag() + "";
			            	}
						    if(PubFunc.isUseNewPrograme(this.userView)) {
                                url = getUrl_new(currentTabid,reject_taskids,isCombine,index_r-1,"1",combine_num,state,currentNode_id);
                            }
			                cData.setDataValue(url);
			                cData.put("date", reject_date_combine);
                            date_combine="";
                            reject_date_combine="";
			                list.add(cData);
			                reject_count=0;
			                reject_num=0;   
			                reject_topic="";  
			                reject_url="";   
							unit="人";           
							first_task_id="0"; 
							reject_taskids="";
							task_topic="";
						}
						if(report_count==combine_num&&PubFunc.isUseNewPrograme(this.userView)){//70锁以上分1000合并单据70锁以下分50合并
							CommonData cData = new CommonData();
							cData.setDataName(subText(report_topic)+"共"+ report_num +unit+ ")_报备");
							url=report_url;
							/*String pendingCode="HRMS-"+PubFunc.encrypt(report_taskids); 
						    if(view!=null&&view.equalsIgnoreCase("list")){
			             		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+1+"&task_id="+first_task_id+"&returnflag="+this.getReturnflag()+"&batch_task="+PubFunc.encrypt(report_taskids)+"&tasklist_str="+report_taskids+"&tabid="+currentTabid+"&sp_batch=1&index_template=1&homeflag=1";
			            	}else {
			            		url = "/general/template/edit_form.do?b_query=link&tabid=" + currentTabid + "&pre_pendingID="+pendingCode+"&businessModel="+0+"&ins_id=" + 1 + "&batch_task=" + PubFunc.encrypt(report_taskids) + "&sp_flag=1&sp_batch=1&homeflag=1&returnflag=" + this.getReturnflag() + "";
			            	}*/
						    if(PubFunc.isUseNewPrograme(this.userView)) {
                                url = getUrl_new(currentTabid,report_taskids,isCombine,index_b-1,"3",combine_num,state,currentNode_id);
                            }
			                cData.setDataValue(url);
			                cData.put("date", report_date_combine);
                            date_combine="";
                            report_date_combine="";
			                list.add(cData);
			                report_count=0;
			                report_num=0;   
			                report_topic="";  
			                report_url="";   
							unit="人";           
							first_task_id="0"; 
							report_taskids="";
							task_topic="";
						}
						task_topic=(String) dynabean.get("task_topic");
						desc=(String) dynabean.get("desc");
						url=(String) dynabean.get("url");
						view = (String) dynabean.get("view");
						if(approval_count==0) {
                            date_combine=(String)dynabean.get("start_date");
                        }
						
						if("0".equals(first_task_id)){
							first_task_id=task_id;
						}
						if ((/*!PubFunc.isUseNewPrograme(this.userView)&&*/tablebo.isDef_flow_self(Integer.parseInt(task_id)))||"3".equals(bs_flag)|| "2".equals(bs_flag)){  //这里表示当前任务为 报备任务
							String topic = task_topic;
							String name = "";
							String strNum = "";
							int m=task_topic.lastIndexOf("(");
							if(m>-1) {
                                topic=task_topic.substring(0, m);
                            }
                    		int n=task_topic.lastIndexOf(",共");
                    		if(n>-1) {
                                name=task_topic.substring(m+1, n);
                            } else {
                                name = this.getNameByInsId(ins_id,tabid,task_id);
                            }
                    		int l=task_topic.lastIndexOf("人)");
                    		if(l==-1){
                    			l=task_topic.lastIndexOf("条记录)");
                    			unit="条记录";
                    		}
                    		if(l==-1){
                                l=task_topic.lastIndexOf("条)");
                                unit="条";
                            }
                    		
                    		if(l==-1){
                                l=task_topic.lastIndexOf("个");
                                unit="个";
                            }
                    		if(l==-1) {
                                unit="人";
                            }
                    		if(n>-1) {
                                strNum= task_topic.substring(n+2, l);
                            } else {
                                strNum = name.split(",").length+"";
                            }
                    		if(PubFunc.isUseNewPrograme(this.userView)&&("3".equals(bs_flag)|| "2".equals(bs_flag))){
                    			int num=Integer.parseInt(strNum);
                        		report_url=url;
                        		if(report_num==0){
                        			report_topic=topic+"(";
                        		}
                        		report_topic+=name+",";
                        		if(PubFunc.isUseNewPrograme(this.userView)) {
                                    report_taskids+=task_id_Encrypt+",";
                                } else {
                                    report_taskids+=task_id+",";
                                }
                        		report_num+=num;
                        		report_date_combine=date_combine;
                        		report_count++;
                    		}
                    		else{
                    			CommonData cData = new CommonData();
                    			if(!name.endsWith(",")) {
                                    name = name + ",";
                                }
    							cData.setDataName(topic+"("+subText(name) +"共"+ strNum +unit+ ") _" + desc);
    							cData.setDataValue(url);
    							cData.put("date", date_combine);
    	                        date_combine="";
    			                list.add(cData);
                    		}
	                    }else /*if("1".equals(bs_flag)||(PubFunc.isUseNewPrograme(this.userView)&&tablebo.isDef_flow_self(Integer.parseInt(task_id))))*/{//报批  手工自定义审批流程在报批中处理(70以上)
	                    	String topic = task_topic;
							String name = "";
							String strNum = "";
	                    	int m=task_topic.lastIndexOf("(");
	                    	if(m>-1) {
                                topic=task_topic.substring(0, m);
                            }
                    		int n=task_topic.lastIndexOf(",共");
                    		if(n>-1) {
                                name=task_topic.substring(m+1, n);
                            } else {
                                name = this.getNameByInsId(ins_id,tabid,task_id);
                            }
                    		int l=task_topic.lastIndexOf("人)");
                    		if(l==-1){
                    			l=task_topic.lastIndexOf("条记录)");
                    			unit="条记录";
                    		}
                    		if(l==-1){
                                l=task_topic.lastIndexOf("条)");
                                unit="条";
                            }
                    		if(l==-1){
                                l=task_topic.lastIndexOf("个)");
                                unit="个";
                            }
                    		if(l==-1) {
                                unit="人";
                            }
                    		if(n>-1) {
                                strNum= task_topic.substring(n+2, l);
                            } else {
                                strNum = name.split(",").length+"";
                            }
                            int num=Integer.parseInt(strNum);
	                    	if("08".equals(state)){   // State=08表示是报批
		                    	approval_url=url;
	                    		if(approval_num==0){
	                    			approval_topic=topic+"(";
	                    		}
	                    		approval_topic+=name+",";
	                    		if(PubFunc.isUseNewPrograme(this.userView)) {
                                    approval_taskids+=task_id_Encrypt+",";
                                } else {
                                    approval_taskids+=task_id+",";
                                }
	                    		approval_num+=num;
	                    		approval_count++;
	                    		apply_date_combine=date_combine;
	                    	}else if("07".equals(state)){  // State=07表示是驳回
	                    		reject_url=url;
	                    		if(reject_num==0){
	                    			reject_topic=topic+"(";
	                    		}
	                    		reject_topic+=name+",";
	                    		if(PubFunc.isUseNewPrograme(this.userView)) {
                                    reject_taskids+=task_id_Encrypt+",";
                                } else {
                                    reject_taskids+=task_id+",";
                                }
	                    		reject_num+=num;
	                    		reject_count++;
	                    		reject_date_combine=date_combine;
	                    	}else{
	                    	}
	                    }
					}
				}
				if(approval_count>0){
					CommonData cData = new CommonData();
					cData.setDataName(subText(approval_topic)+"共"+ approval_num +unit+ ")_报批");
					url=approval_url;
					if(PubFunc.isUseNewPrograme(this.userView)) {
                        url = getUrl(currentTabid,approval_taskids,"1");
                    }
					if(approval_count>1){
						String pendingCode="HRMS-"+PubFunc.encrypt(approval_taskids); 
					    if(view!=null&& "list".equalsIgnoreCase(view)){
		             		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+1+"&task_id="+first_task_id+"&returnflag="+this.getReturnflag()+"&batch_task="+PubFunc.encrypt(approval_taskids)+"&tasklist_str="+approval_taskids+"&tabid="+currentTabid+"&sp_batch=1&index_template=1&homeflag=1";
		            	}else {
		            		url = "/general/template/edit_form.do?b_query=link&tabid=" + currentTabid + "&pre_pendingID="+pendingCode+"&businessModel="+0+"&ins_id=" + 1 + "&batch_task=" + PubFunc.encrypt(approval_taskids) + "&sp_flag=1&sp_batch=1&homeflag=1&returnflag=" + this.getReturnflag() + "";
		            	}
					    if(PubFunc.isUseNewPrograme(this.userView)){
					    	if(index_a>combine_num&&approval_count<combine_num) {
                                url = getUrl_new(currentTabid,approval_taskids,isCombine,index_a,"1",combine_num,"08",currentNode_id);
                            } else {
                                url = getUrl_new(currentTabid,approval_taskids,isCombine,approval_count,"1",combine_num,"08",currentNode_id);
                            }
					    }
					}
	                cData.setDataValue(url);
	                cData.put("date", apply_date_combine);
                    date_combine="";
	                apply_date_combine="";
	                list.add(cData);
				}
				if(reject_count>0){
					CommonData cData = new CommonData();
					cData.setDataName(subText(reject_topic)+"共"+ reject_num +unit+ ")_驳回");
					url=reject_url;
					if(PubFunc.isUseNewPrograme(this.userView)) {
                        url = getUrl(currentTabid,reject_taskids,"1");
                    }
					if(reject_count>1){
						String pendingCode="HRMS-"+PubFunc.encrypt(reject_taskids); 
					    if(view!=null&& "list".equalsIgnoreCase(view)){
		             		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+1+"&task_id="+first_task_id+"&returnflag="+this.getReturnflag()+"&batch_task="+PubFunc.encrypt(reject_taskids)+"&tasklist_str="+reject_taskids+"&tabid="+currentTabid+"&sp_batch=1&index_template=1&homeflag=1";
		            	}else {
		            		url = "/general/template/edit_form.do?b_query=link&tabid=" + currentTabid + "&pre_pendingID="+pendingCode+"&businessModel="+0+"&ins_id=" + 1 + "&batch_task=" + PubFunc.encrypt(reject_taskids) + "&sp_flag=1&sp_batch=1&homeflag=1&returnflag=" + this.getReturnflag() + "";
		            	}
					    if(PubFunc.isUseNewPrograme(this.userView)){
					    	if(index_r>combine_num&&reject_count<combine_num) {
                                url = getUrl_new(currentTabid,reject_taskids,isCombine,index_r,"1",combine_num,"07",currentNode_id);
                            } else {
                                url = getUrl_new(currentTabid,reject_taskids,isCombine,reject_count,"1",combine_num,"07",currentNode_id);
                            }
					    }
					}
	                cData.setDataValue(url);
	                cData.put("date", reject_date_combine);
                    date_combine="";
	                reject_date_combine="";
	                list.add(cData);
				}
				if(report_count>0&&PubFunc.isUseNewPrograme(this.userView)){
					CommonData cData = new CommonData();
					cData.setDataName(subText(report_topic)+"共"+ report_num +unit+ ")_报备");
					url=report_url;
					if(PubFunc.isUseNewPrograme(this.userView)) {
                        url = getUrl(currentTabid,report_taskids,"3");
                    }
					if(report_count>1){
						/*String pendingCode="HRMS-"+PubFunc.encrypt(reject_taskids); 
					    if(view!=null&&view.equalsIgnoreCase("list")){
		             		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+1+"&task_id="+first_task_id+"&returnflag="+this.getReturnflag()+"&batch_task="+PubFunc.encrypt(reject_taskids)+"&tasklist_str="+reject_taskids+"&tabid="+currentTabid+"&sp_batch=1&index_template=1&homeflag=1";
		            	}else {
		            		url = "/general/template/edit_form.do?b_query=link&tabid=" + currentTabid + "&pre_pendingID="+pendingCode+"&businessModel="+0+"&ins_id=" + 1 + "&batch_task=" + PubFunc.encrypt(approval_taskids) + "&sp_flag=1&sp_batch=1&homeflag=1&returnflag=" + this.getReturnflag() + "";
		            	}*/
					    if(PubFunc.isUseNewPrograme(this.userView)){
					    	if(index_b>combine_num&&report_count<combine_num) {
                                url = getUrl_new(currentTabid,report_taskids,isCombine,index_b,"3",combine_num,"08",currentNode_id);
                            } else {
                                url = getUrl_new(currentTabid,report_taskids,isCombine,report_count,"3",combine_num,"08",currentNode_id);
                            }
					    }
					}
	                cData.setDataValue(url);
	                cData.put("date", report_date_combine);
                    date_combine="";
                    report_date_combine="";
	                list.add(cData);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	private String getUrl_new(String tabid, String approval_taskids,boolean isCombine,int count, String approve_flag, int combine_num, String task_state ,String currentNode_id) {
		String url = "/module/template/templatemain/templatemain.html?b_query=link&encryptParam="+PubFunc.encrypt("other_param=iscombine="
		            +isCombine+"`task_count="+count+"`combine_num="+combine_num+"_"+task_state+"`combine_nodeid="+currentNode_id+"&tab_id="+tabid+"&return_flag="+this.getNewReturnflag()
		            +"&approve_flag="+approve_flag);
	    return url;
	}
	/**
	 * 将首页/我的任务中的待办任务在同一模板中的任务合并 liuziy 20151124
	 * @param dynabeanlist
	 * @return
	 */
	private ArrayList combineList(ArrayList dynabeanlist){
		
		ArrayList list=new ArrayList();
		//Set set=new HashSet();  //定义set集合存储tabid，用于去除重复，方便
	   // set是无须的，无法保证顺序。使用list
		ArrayList tabList = new ArrayList();
		for(int i=0;i<dynabeanlist.size();i++){
			LazyDynaBean dynabean =(LazyDynaBean) dynabeanlist.get(i);
			String tabid=(String) dynabean.get("tabid");
			if (!tabList.contains(tabid)) {
                tabList.add(tabid);
            }
		}
		try
		{
			TemplateTableBo tablebo=null;
			for(Object obj:tabList){
				String currentTabid=(String)obj;
				String approval_taskids="";         //对于报批的任务将它们的task以','分割存储起来
				String reject_taskids="";           //对于驳回的任务将它们的task以','分割存储起来
				String task_topic="";
				String desc="";
				String url="";
				String approval_topic="";  //用于存储报批的主题
				int approval_num=0;        //用于存储报批的人数
				int approval_count=0;      //用于存储报批的条数
				String approval_url="";    //报批的跳转路径
				String reject_topic="";    //用于存储驳回的主题
				int reject_num=0;          //用于存储驳回的人数
				int reject_count=0;        //用于存储驳回的条数
				String reject_url="";      //驳回的跳转路径
				String view="";            //模板设置的显示方式
				String unit="人";           //存储单位名称，用于拼凑报批信息的显示，当是人的时为'人'，单位或岗位时为'条记录'
				String first_task_id="0";   //用于存储第一个task_id的值
				tablebo=new TemplateTableBo(this.conn,Integer.parseInt(currentTabid),this.userView);
				 
				boolean bNewRSYD=false;
				if(PubFunc.isUseNewPrograme(this.userView)){
					bNewRSYD=true;
				}
				int _static= tablebo.get_static();
				for(int i=0;i<dynabeanlist.size();i++){
					LazyDynaBean dynabean =(LazyDynaBean) dynabeanlist.get(i);
					String tabid=(String) dynabean.get("tabid");
					String task_id=(String) dynabean.get("task_id");
					String ins_id=(String) dynabean.get("ins_id");
					String state=(String) dynabean.get("state");
					if(currentTabid.equals(tabid)){
                        if(approval_count==50){//分50合并单据，太多的话，超过url最大长度  IE7下100也报错
                            CommonData cData = new CommonData();
                            cData.setDataName(subText(approval_topic)+"共"+ approval_num +unit+ ")_报批");
                            url=approval_url;
                            if(bNewRSYD){
        					    url = getUrl(currentTabid,approval_taskids,"1");
        					}
                            if(approval_count>1){
                                String pendingCode="HRMS-"+PubFunc.encrypt(approval_taskids); 
                                if(view!=null&& "list".equalsIgnoreCase(view)){
                                    url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+1+"&task_id="+first_task_id+"&returnflag="+this.getReturnflag()+"&batch_task="+PubFunc.encrypt(approval_taskids)+"&tasklist_str="+approval_taskids+"&tabid="+currentTabid+"&sp_batch=1&index_template=1&homeflag=1";
                                }else {
                                    url = "/general/template/edit_form.do?b_query=link&tabid=" + currentTabid + "&pre_pendingID="+pendingCode+"&businessModel="+0+"&ins_id=" + 1 + "&batch_task=" + PubFunc.encrypt(approval_taskids) + "&sp_flag=1&sp_batch=1&homeflag=1&returnflag=" + this.getReturnflag() + "";
                                }
                                if(bNewRSYD){	            	    
        		            	    url = getUrl(currentTabid,approval_taskids,"1");
        					    }
                            }
                            cData.setDataValue(url);
                            list.add(cData);
                            approval_count=0;
                            approval_num=0;        
                            approval_topic="";  
                            approval_url="";   
                            unit="人";           
                            first_task_id="0"; 
                            approval_taskids="";
                            task_topic="";
                        }
						if(reject_num==50){//分50合并单据，太多的话，超过url最大长度  IE7下100也报错
							CommonData cData = new CommonData();
							cData.setDataName(subText(reject_topic)+"共"+ reject_num +unit+ ")_驳回");
							url=reject_url;
							if(bNewRSYD){
							    url = getUrl(currentTabid,reject_taskids,"1");
							}
							if(reject_count>1){
							    String pendingCode="HRMS-"+PubFunc.encrypt(reject_taskids); 
							    if(view!=null&& "list".equalsIgnoreCase(view)){
				             		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+1+"&task_id="+first_task_id+"&returnflag="+this.getReturnflag()+"&batch_task="+PubFunc.encrypt(reject_taskids)+"&tasklist_str="+reject_taskids+"&tabid="+currentTabid+"&sp_batch=1&index_template=1&homeflag=1";
				            	}else {
			                        url = "/general/template/edit_form.do?b_query=link&tabid=" + currentTabid + "&pre_pendingID="+pendingCode+"&businessModel="+0+"&ins_id=" + 1 + "&batch_task=" + PubFunc.encrypt(reject_taskids) + "&sp_flag=1&sp_batch=1&homeflag=1&returnflag=" + this.getReturnflag() + "";
				            	}
							    if(bNewRSYD){
								    url = getUrl(currentTabid,reject_taskids,"1");
								}
							}
			                cData.setDataValue(url);
			                list.add(cData);
			                reject_count=0;
			                reject_num=0;        
			                reject_topic="";  
			                reject_url="";   
							unit="人";           
							first_task_id="0"; 
							reject_taskids="";
							task_topic="";
						}
						task_topic=(String) dynabean.get("task_topic");
						desc=(String) dynabean.get("desc");
						url=(String) dynabean.get("url");
						view=(String) dynabean.get("view");
						String bs_flag=(String) dynabean.get("bs_flag");
						if("0".equals(first_task_id)){
							first_task_id=task_id;
						}
						if (tablebo.isDef_flow_self(Integer.parseInt(task_id)) || "3".equals(bs_flag)|| "2".equals(bs_flag)){  //这里表示当然任务为自定义审批流程 或者 报备任务
							String topic = task_topic;
							String name = "";
							String strNum = "";
							int m=task_topic.lastIndexOf("(");
							if(m>-1) {
                                topic=task_topic.substring(0, m);
                            }
                    		int n=task_topic.lastIndexOf(",共");
                    		if(n>-1) {
                                name=task_topic.substring(m+1, n);
                            } else {
                                name = this.getNameByInsId(ins_id,tabid,task_id);
                            }
                    		int l=task_topic.lastIndexOf("人)");
                    		if(l==-1){
                    			l=task_topic.lastIndexOf("条记录)");
                    			unit="条记录";
                    		}
                    		if(l==-1){
                                l=task_topic.lastIndexOf("条)");
                                unit="条";
                            }
                    		
                    		if(l==-1){
                                l=task_topic.lastIndexOf("个");
                                unit="个";
                            }
                    		if(l==-1) {
                                unit="人";
                            }
                    		if(n>-1) {
                                strNum= task_topic.substring(n+2, l);
                            } else {
                                strNum = name.split(",").length+"";
                            }
							CommonData cData = new CommonData();
							cData.setDataName(topic+"("+subText(name) +"共"+ strNum +unit+ ") _" + desc);
							//显示格式与报备一样  lis 20160809 end
							cData.setDataValue(url);
			                list.add(cData);
	                    }else{
	                    	String topic = task_topic;
							String name = "";
							String strNum = "";
	                    	int m=task_topic.lastIndexOf("(");
	                    	if(m>-1) {
                                topic=task_topic.substring(0, m);
                            }
                    		int n=task_topic.lastIndexOf(",共");
                    		if(n>-1) {
                                name=task_topic.substring(m+1, n);
                            } else {
                                name = this.getNameByInsId(ins_id,tabid,task_id);
                            }
                    		int l=task_topic.lastIndexOf("人)");
                    		if(l==-1){
                    			l=task_topic.lastIndexOf("条记录)");
                    			unit="条记录";
                    		}
                    		if(l==-1){
                                l=task_topic.lastIndexOf("条)");
                                unit="条";
                            }
                    		if(l==-1){
                                l=task_topic.lastIndexOf("个)");
                                unit="个";
                            }
                    		if(l==-1) {
                                unit="人";
                            }
                    		if(n>-1) {
                                strNum= task_topic.substring(n+2, l);
                            } else {
                                strNum = name.split(",").length+"";
                            }
                            int num=Integer.parseInt(strNum);
	                    	if("08".equals(state)){   // State=08表示是报批
		                    	approval_url=url;
	                    		if(approval_num==0){
	                    			approval_topic=topic+"(";
	                    		}
	                    		approval_topic+=name+",";
	                    		if(bNewRSYD){
									approval_taskids+=PubFunc.encrypt(task_id)+",";
								}else{
									approval_taskids+=task_id+",";
								}
	                    		approval_num+=num;
	                    		approval_count++;
	                    	}else if("07".equals(state)){  // State=07表示是驳回
	                    		reject_url=url;
	                    		if(reject_num==0){
	                    			reject_topic=topic+"(";
	                    		}
	                    		reject_topic+=name+",";
	                    		if(bNewRSYD){
									reject_taskids+=PubFunc.encrypt(task_id)+",";
								}else{
									reject_taskids+=task_id+",";
								}
	                    		reject_num+=num;
	                    		reject_count++;
	                    	}else{
	                    	    /* 没用吧
	                    		CommonData cData = new CommonData();
								cData.setDataName(subText(task_topic) + " _" + desc);
								url = "/general/template/templatelist.do?br_query=query&task_id="+task_id+"&tab_id="+currentTabid;
								if(isNewRSYD(_static)){
									//url = "/general/template/templatelist.do?br_templatenavigation=query&task_id="+task_id+"&tab_id="+currentTabid;
								    url = getUrl(currentTabid,task_id,"1");
								}
								cData.setDataValue(url);
				                list.add(cData);
				                */
	                    	}
	                    }
					} // if判断
				}// end for循环
				if(approval_num>0){
					CommonData cData = new CommonData();
					cData.setDataName(subText(approval_topic)+"共"+ approval_num +unit+ ")_报批");
					url=approval_url;
				//	url = "/general/template/templatelist.do?br_query=query&task_id="+approval_taskids+"&tab_id="+currentTabid;
					if(bNewRSYD){
						//url = "/general/template/templatelist.do?br_templatenavigation=query&task_id="+approval_taskids+"&tab_id="+currentTabid;
					    url = getUrl(currentTabid,approval_taskids,"1");
					}
					if(approval_count>1){
					    String pendingCode="HRMS-"+PubFunc.encrypt(approval_taskids); 
					    if(view!=null&& "list".equalsIgnoreCase(view)){
		             		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+1+"&task_id="+first_task_id+"&returnflag="+this.getReturnflag()+"&batch_task="+PubFunc.encrypt(approval_taskids)+"&tasklist_str="+approval_taskids+"&tabid="+currentTabid+"&sp_batch=1&index_template=1&homeflag=1";
		            	}else {
	                        url = "/general/template/edit_form.do?b_query=link&tabid=" + currentTabid + "&pre_pendingID="+pendingCode+"&businessModel="+0+"&ins_id=" + 1 + "&batch_task=" + PubFunc.encrypt(approval_taskids) + "&sp_flag=1&sp_batch=1&homeflag=1&returnflag=" + this.getReturnflag() + "";
		            	}
					    if(bNewRSYD){	            	    
		            	    url = getUrl(currentTabid,approval_taskids,"1");
					    	//url = "/general/template/templatelist.do?br_templatenavigation=query&task_id="+approval_taskids+"&tab_id="+currentTabid;
					    }
					}
	                cData.setDataValue(url);
	                list.add(cData);
				}
				if(reject_num>0){
					CommonData cData = new CommonData();
					cData.setDataName(subText(reject_topic)+"共"+ reject_num +unit+ ")_驳回");
					url=reject_url;
				//	url = "/general/template/templatelist.do?br_query=query&task_id="+reject_taskids+"&tab_id="+currentTabid;
					if(bNewRSYD){
						//url = "/general/template/templatelist.do?br_templatenavigation=query&task_id="+reject_taskids+"&tab_id="+currentTabid;
					    url = getUrl(currentTabid,reject_taskids,"1");
					}
					if(reject_count>1){
					    
					    String pendingCode="HRMS-"+PubFunc.encrypt(reject_taskids); 
					    if(view!=null&& "list".equalsIgnoreCase(view)){
		             		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id="+1+"&task_id="+first_task_id+"&returnflag="+this.getReturnflag()+"&batch_task="+PubFunc.encrypt(reject_taskids)+"&tasklist_str="+reject_taskids+"&tabid="+currentTabid+"&sp_batch=1&index_template=1&homeflag=1";
		            	}else {
		            		url = "/general/template/edit_form.do?b_query=link&tabid=" + currentTabid + "&pre_pendingID="+pendingCode+"&businessModel="+0+"&ins_id=" + 1 + "&batch_task=" + PubFunc.encrypt(reject_taskids) + "&sp_flag=1&sp_batch=1&homeflag=1&returnflag=" + this.getReturnflag() + "";
		            	}
		            	
		            	if(bNewRSYD){
		            	    url = getUrl(currentTabid,reject_taskids,"1");
							//url = "/general/template/templatelist.do?br_templatenavigation=query&task_id="+reject_taskids+"&tab_id="+currentTabid;
						}
					}
	                cData.setDataValue(url);
	                list.add(cData);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 得到单据中被审批的对象
	 * @param ins_id
	 * @param tabid
	 * @param task_id 
	 * @return
	 */
    private String getNameByInsId(String ins_id, String tabid, String task_id) {
    	RowSet rs = null;
    	String _withNoLock="";
		if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
        {
            _withNoLock=" WITH(NOLOCK) ";
        }
    	String name = "";
    	String tablename = "templet_"+tabid;
    	String sql = "select TPT.a0101_1 from t_wf_task_objlink two "+_withNoLock+","+tablename+" tpt "+_withNoLock+" where TWO.seqnum=TPT.seqnum and TWO.ins_id="+ins_id+" and TWO.task_id="+task_id+" and TWO.tab_id="+tabid;
    	ContentDAO dao = new ContentDAO(this.conn);
        try {
			rs = dao.search(sql);
			int i=0;
			while (rs.next()){
				String a0101_1 = rs.getString("a0101_1");
				if(i==0) {
                    name+=a0101_1;
                } else {
                    name+=","+a0101_1;
                }
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
            if (rs != null){
                try{
                    rs.close();
                }catch (Exception e2){
                    e2.printStackTrace();
                }
            }
        }
		return name;
	}
	public String getUrl(String tabid ,String taskId,String approve_flag){
    
        String  url = "/module/template/templatemain/templatemain.html?b_query=link&task_id="
            +taskId+"&tab_id="+tabid+"&return_flag="+this.getNewReturnflag()
            +"&approve_flag="+approve_flag;
        return url;
        
    }
    /**
     * 待批任务
     * 
     * @param list
     * @return
     */
    public ArrayList getWaitTaskBeanList(ArrayList list)
    {

        RowSet rs = null;
        String _withNoLock="";
		if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
        {
            _withNoLock=" WITH(NOLOCK) ";
        }
        StringBuffer strsql = new StringBuffer();
        /** 查询任务 */
        strsql.append("select tabid,a0101_1,task_topic ,state,T.start_date,task_pri,bread,bfile,task_id ,T.ins_id,U.template_type,T.state,T.actor_type,T.actorid,T.node_id,T.bs_flag,U.actorname from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+"");
//		strsql.append(" where T.ins_id=U.ins_id and ");
		strsql.append(" where T.ins_id=U.ins_id and task_topic not like '%共0人%'  and task_topic not like '%共0条%' and "); //20080825解决审批时，把当前审批表的中人员删除掉，这种任务暂不列不出,处理方式有点问题。
        strsql.append(" task_type='2' and task_state='3'");
    //    strsql.append(" and U.finished='2' ");
        strsql.append(" and (");
        strsql.append(getTaskFilterWhere());
        strsql.append(")");
        //我的任务出现报备的待办信息(未阅读), 2014-05-06  dengcan
        strsql.append(" and ( ("+Sql_switcher.isnull("T.bs_flag","'1'")+"='1' and U.finished='2' ) or ( "+Sql_switcher.isnull("T.bs_flag","'1'")+"='3' and bread=0  )  )  ");
        String strexpr = "";
        String clientName=SystemConfig.getPropertyValue("clientName");
	    if(clientName!=null&& "hkyh".equalsIgnoreCase(clientName)) {
            strexpr = Sql_switcher.addDays(Sql_switcher.sqlNow(), "-300"); // "GetDate()"
        } else {
            strexpr = Sql_switcher.addDays(Sql_switcher.sqlNow(), "-30"); // "GetDate()"
        }
        strsql.append(" and T.start_date>=");
        strsql.append(strexpr);
        strsql.append(" order by T.start_date desc");
        try
        {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(strsql.toString());
            String herf = "";
            while (rs.next())
            {
                LazyDynaBean bean = new LazyDynaBean();
                String desc = "报批";
                String state = rs.getString("state");
                if (state != null && "07".equals(state)) {
                    desc = "驳回";
                }
                if (state != null && "06".equals(state))
                {
                    desc = "结束";
                }
                String topic = rs.getString("task_topic");
                String a0101 = rs.getString("a0101_1");
                Date date = rs.getDate("start_date");
                String dateStr = DateUtils.format(date, "yyyy-MM-dd");
                bean.set("taskname", topic + "_" + desc);
                bean.set("a0101", a0101);
                bean.set("dateStr", dateStr);
                bean.set("begindate", dateStr);

                herf = "/general/template/edit_form.do?b_query=link&tabid=" + rs.getString("tabid") + "&businessModel=0&ins_id=" + rs.getString("ins_id") + "&taskid=" + rs.getString("task_id") + "&sp_flag=1&returnflag=" + this.getReturnflag() + "";
                bean.set("link", herf);
                list.add(bean);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }

        return list;
    }

    public ArrayList getInstanceList(ArrayList list)
    {
        RowSet rs = null;
        String _withNoLock="";
		if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
        {
            _withNoLock=" WITH(NOLOCK) ";
        }
        String dbpre = this.userView.getDbname(); // 库前缀
        String userid = dbpre + this.userView.getA0100();// 人员编号
        StringBuffer strsql = new StringBuffer();
        strsql.append("select U.ins_id,T.task_topic name,U.tabid,U.actorname fullname,a0101, task_state finished ,T.start_date,T.end_date,T.actorname,T.task_id from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+"");
        strsql.append(" where T.ins_id=U.ins_id and  task_topic not like '%共0人%'  and task_topic not like '%共0条%' ");
        strsql.append(" and ((task_type='2' or task_type='9') and task_state='5' and flag=1)");// Finished
                                                                                               // //task_type='2'
                                                                                               // and
                                                                                               // task_state='5'
                                                                                               // and
                                                                                               // state<>'07'
        strsql.append(" and (");
        strsql.append(" U.ins_id in (select ins_id from t_wf_task "+_withNoLock+" where  ");
        strsql.append(" actorid in ('");
        strsql.append(userid.toUpperCase());
        strsql.append("','");
        strsql.append(this.userView.getUserName());
        strsql.append("'))");
        ;
        strsql.append(")");
        try
        {
            CommonData cData = null;
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(strsql.toString());
            String herf = "";
            while (rs.next())
            {
                cData = new CommonData();
                cData.setDataName(rs.getString("name"));
                herf = "/general/template/edit_form.do?b_query=link&tabid=" + rs.getString("tabid") + "&businessModel=0&ins_id=" + rs.getString("ins_id") + "&taskid=" + rs.getString("task_id") + "&sp_flag=2&returnflag=" + this.getReturnflag() + "";
                cData.setDataValue(herf);
                list.add(cData);
            }
            if (rs != null) {
                rs.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }

        return list;

    }

    private String subText(String text)
    {
        if (text == null || text.length() <= 0) {
            return "";
        }
        if (text.length() < 36) {
            return text;
        }
        text = text.substring(0, 36) + "...";
        return text;
    }

    private String getTaskFilterWhere()
    {
        StringBuffer strwhere = new StringBuffer();
        /** 用户号 */
        String dbpre = this.userView.getDbname(); // 库前缀
        String userid = dbpre + this.userView.getA0100();// 人员编号
        String orgid = "UN" + this.userView.getUserOrgId();// 单位编码
        String deptid = "UM" + this.userView.getUserDeptId();// 部门编码
        String posid = "@K" + this.userView.getUserPosId();// getUserOrgId();//职位编码
        /** 组织元 */
        strwhere.append("(T.actor_type='3' and T.actorid in ('");
        strwhere.append(orgid.toUpperCase());
        strwhere.append("','");
        strwhere.append(deptid.toUpperCase());
        strwhere.append("','");
        strwhere.append(posid.toUpperCase());
        strwhere.append("'))");
     // strwhere.append(" or ( T.actor_type='5'  )"); 20170613
        /** 人员列表 */
        strwhere.append(" or ((T.actor_type='1' or T.actor_type='4') and T.actorid in ('");
        // strwhere.append(userid.toUpperCase()); //oracle's 大写出错
        strwhere.append(userid);
        strwhere.append("','");
        strwhere.append(this.userView.getUserName());
        strwhere.append("'))");

        /** 角色ID列表 */
        ArrayList rolelist = this.userView.getRolelist();// 角色列表
        StringBuffer strrole = new StringBuffer();
        for (int i = 0; i < rolelist.size(); i++)
        {

            strrole.append("'");
            strrole.append((String) rolelist.get(i));
            strrole.append("'");
            strrole.append(",");
        }
        if (rolelist.size() > 0)
        {
            strrole.setLength(strrole.length() - 1);
            strwhere.append(" or (T.actor_type='2' and T.actorid in (");
            strwhere.append(strrole.toString());
            strwhere.append("))");
        }
        return strwhere.toString();
    }

    public ArrayList getTmessageList(ArrayList list)
    {
    	String _withNoLock="";
		//if(Sql_switcher.searchDbServer()!=2)//针对SQLSERVER 无需考虑锁表
		if(Sql_switcher.searchDbServer()==Constant.MSSQL) {
            _withNoLock=" WITH(NOLOCK) ";
        }
        boolean isSource = false;
        if (this.userView.isHavetemplateid(IResourceConstant.RSBD) || this.userView.isHavetemplateid(IResourceConstant.ORG_BD) || this.userView.isHavetemplateid(IResourceConstant.POS_BD) || this.userView.isHavetemplateid(IResourceConstant.GZBD) || this.userView.isHavetemplateid(IResourceConstant.INS_BD) || this.userView.isHavetemplateid(IResourceConstant.PSORGANS) || this.userView.isHavetemplateid(IResourceConstant.PSORGANS_FG) || this.userView.isHavetemplateid(IResourceConstant.PSORGANS_GX) || this.userView.isHavetemplateid(IResourceConstant.PSORGANS_JCG)) {
            isSource = true;
        }
        if (this.userView.isSuper_admin()) {
            isSource = true;
        }
        if (!isSource) {
            return list;
        }
        StringBuffer sql = new StringBuffer();
        String statickey = "static";
		if(Sql_switcher.searchDbServer()==Constant.KUNLUN) {
			statickey = "\"static\"";
		}else if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			statickey = "static_o";
		}
        sql.append("select DISTINCT Noticetempid,Template_table.name as name,Template_table."+statickey+" as static2"); // ,State");
        sql.append(" from tmessage "+_withNoLock+" left join Template_table "+_withNoLock+" on tmessage.Noticetempid=Template_table.tabid ");
        sql.append(" where (State='0' or State='1')");

        /*
         * if(!this.userView.isSuper_admin()) {
         * sql.append(" and (tmessage.b0110 like '");
         * if((this.userView.getManagePrivCodeValue
         * ()==null||this.userView.getManagePrivCodeValue
         * ().trim().length()==0)&&
         * this.userView.getManagePrivCode().length()==0) sql.append("##"); else
         * sql.append(this.userView.getManagePrivCodeValue());
         * sql.append("%' or tmessage.b0110 is null or tmessage.b0110='')"); }
         */
        CommonData cData = null;
        String herf = "";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try
        {
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
            rs = dao.search(sql.toString());
            String tabid = "";
            boolean isCorrect = false;

            HashMap map = new HashMap();
            while (rs.next())
            {
                tabid = rs.getString("Noticetempid");
                // String State=rs.getString("State"); //=0(未用) =1(正在处理)
                if (tabid == null || tabid.length() <= 0) {
                    continue;
                }
                isCorrect = false;
                if (this.userView.isHaveResource(IResourceConstant.RSBD, tabid))// 人事移动
                {
                    isCorrect = true;
                }
                if (!isCorrect) {
                    if (this.userView.isHaveResource(IResourceConstant.ORG_BD, tabid))// 组织变动
                    {
                        isCorrect = true;
                    }
                }
                if (!isCorrect) {
                    if (this.userView.isHaveResource(IResourceConstant.POS_BD, tabid))// 岗位变动
                    {
                        isCorrect = true;
                    }
                }
                if (!isCorrect) {
                    if (this.userView.isHaveResource(IResourceConstant.GZBD, tabid))// 工资变动
                    {
                        isCorrect = true;
                    }
                }
                if (!isCorrect) {
                    if (this.userView.isHaveResource(IResourceConstant.INS_BD, tabid))// 保险变动
                    {
                        isCorrect = true;
                    }
                }
                if (!isCorrect) {
                    if (this.userView.isHaveResource(IResourceConstant.PSORGANS, tabid)) {
                        isCorrect = true;
                    }
                }
                if (!isCorrect) {
                    if (this.userView.isHaveResource(IResourceConstant.PSORGANS_FG, tabid)) {
                        isCorrect = true;
                    }
                }
                if (!isCorrect) {
                    if (this.userView.isHaveResource(IResourceConstant.PSORGANS_GX, tabid)) {
                        isCorrect = true;
                    }
                }
                if (!isCorrect) {
                    if (this.userView.isHaveResource(IResourceConstant.PSORGANS_JCG, tabid)) {
                        isCorrect = true;
                    }
                }

                /*
                 * if(State.equals("1")&&isCorrect) {
                 * if(dbw.isExistTable(this.userView
                 * .getUserName()+"templet_"+tabid, false)) { RowSet
                 * rs1=dao.search
                 * ("select count(a0100) from "+this.userView.getUserName
                 * ()+"templet_"+tabid); if(rs1.next()) { if(rs1.getInt(1)==0)
                 * isCorrect=false; } } else isCorrect=false; }
                 */

                if (isCorrect && map.get(tabid) == null)
                {
                    cData = new CommonData();
                    String str = getRecordBusiTopic(rs.getString("Noticetempid"), rs.getString("static2"));
                    String dateStr= DateUtils.format(new java.util.Date(), "yyyy-MM-dd");
                    if(str.indexOf("｀")>-1) {
                    	String[] arry = str.split("｀");
                    	str = arry[0];
                    	if(arry.length==2) {
                    		dateStr = arry[1];
                    	}
                    }
                    if (!"0".equals(str))
                    {
                        cData.setDataName(subText(rs.getString("name")) + str + " _通知");
                        // if(userView.getVersion()>=10){
                        // herf="/general/template/templatelist.do?b_init=init&sp_flag=1&ins_id=0&returnflag=listhome&task_id=0&tabid="+tabid;
                        // }
                        // else
                        
                      //在待办任务中判断模板设置的展现方式，以默认显示 20150923 liuzy
                        int tab_id=Integer.parseInt(tabid);
                        TemplateUtilBo tb=new TemplateUtilBo(this.conn,userView);
                        String view = tb.getTemplateView(tab_id);
                    	if(view!=null&& "list".equalsIgnoreCase(view)){
                     		herf="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&ins_id=0&returnflag="+this.getReturnflag()+"&task_id=0&tabid="+tabid+"&warn_id=";
                    	}else {
                    		herf = "/general/template/edit_form.do?b_query=link&sp_flag=1&businessModel=0&ins_id=0&returnflag=" + this.getReturnflag() + "&tabid=" + tabid;
                    	}
                    	
                      //  herf = "/general/template/edit_form.do?b_query=link&sp_flag=1&businessModel=0&ins_id=0&returnflag=" + this.getReturnflag() + "&tabid=" + tabid;
                    	if(PubFunc.isUseNewPrograme(this.userView)){
                    	    herf = getUrl(tabid,"0","1");
                        }                        
                    	cData.setDataValue(herf);
                    	cData.put("date", dateStr);//默认一直在最前
                        list.add(cData);
                        map.put(tabid, "1");
                    }
                }
            }
            if (rs != null) {
                rs.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != rs)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }

        return list;
    }

    public ArrayList getTmessageBeanList(ArrayList list)
    {
        StringBuffer sql = new StringBuffer();
        String _withNoLock="";
		if(Sql_switcher.searchDbServer()!=2) { //针对SQLSERVER 无需考虑锁表
			_withNoLock=" WITH(NOLOCK) ";
		}
		String _static="static";
		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			_static="static_o";
		}
        sql.append("select DISTINCT Noticetempid,Template_table.name as name,Template_table."+_static+" as static2,tmessage.a0101"); // ,State");
        sql.append(" from tmessage "+_withNoLock+" left join Template_table "+_withNoLock+" on tmessage.Noticetempid=Template_table.tabid ");
        sql.append(" where (State='0' or State='1')");

        String herf = "";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try
        {
            rs = dao.search(sql.toString());
            String tabid = "";
            boolean isCorrect = false;

            HashMap map = new HashMap();
            while (rs.next())
            {
                tabid = rs.getString("Noticetempid");
                // String State=rs.getString("State"); //=0(未用) =1(正在处理)
                if (tabid == null || tabid.length() <= 0) {
                    continue;
                }
                isCorrect = false;
                if (this.userView.isHaveResource(IResourceConstant.RSBD, tabid))// 人事移动
                {
                    isCorrect = true;
                }
                if (!isCorrect) {
                    if (this.userView.isHaveResource(IResourceConstant.GZBD, tabid))// 工资变动
                    {
                        isCorrect = true;
                    }
                }
                if (!isCorrect) {
                    if (this.userView.isHaveResource(IResourceConstant.INS_BD, tabid))// 保险变动
                    {
                        isCorrect = true;
                    }
                }

                if (isCorrect && map.get(tabid) == null)
                {
                    String str = getRecordBusiTopic(rs.getString("Noticetempid"), rs.getString("static2"));
                    String dateStr= DateUtils.format(new java.util.Date(), "yyyy-MM-dd");
                    if(str.indexOf("｀")>-1) {
                    	String[] arry = str.split("｀");
                    	str = arry[0];
                    	if(arry.length==2) {
                    		dateStr = arry[1];
                    	}
                    }
                    if (!"0".equals(str))
                    {
                        LazyDynaBean bean = new LazyDynaBean();
                        herf = "/general/template/edit_form.do?b_query=link&sp_flag=1&ins_id=0&businessModel=0&returnflag=" + this.getReturnflag() + "&tabid=" + tabid;
                        bean.set("taskname", rs.getString("name") + str + " _通知");
                        bean.set("a0101", rs.getString("a0101"));
                       // String dateStr = DateUtils.format(new java.util.Date(), "yyyy-MM-dd");
                        bean.set("dateStr", dateStr);
                        bean.set("begindate", dateStr);
                        bean.set("link", herf);
                        list.add(bean);
                        map.put(tabid, "1");
                    }
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != rs)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }

        return list;
    }

    /**
     * 
     * @param planids
     * @return
     */
    public ArrayList getSuperiorCreatePlanList(HashMap objectMap, String _sub_sql, String _img)
    {
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        try
        {
            HashMap khObjLevelMap = new HashMap();
            HashMap noKhObjLevelMap = new HashMap();
            String _str2 = "";
            if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                _str2 = "pms.level_o";
            } else {
                _str2 = "pms.level";
            }
            String sql = "select std.object_id," + _str2 + " from per_mainbody_std std,per_mainbodyset pms where std.body_id=pms.body_id   and std.mainbody_id='" + this.userView.getA0100() + "'";
            rowSet = dao.search(sql);
            while (rowSet.next())
            {
                khObjLevelMap.put(rowSet.getString(1).trim(), rowSet.getString(2));
            }

            sql = "select po.object_id,po.plan_id," + _str2 + " from per_plan pp,per_object po,per_mainbody std,per_mainbodyset pms where pp.plan_id=po.plan_id and std.plan_id=po.plan_id   and  pp.method=2   and pp.status=8  and  po.kh_relations=1 and po.object_id=std.object_id and std.mainbody_id='" + this.userView.getA0100() + "' and std.body_id=pms.body_id ";
            rowSet = dao.search(sql);
            while (rowSet.next())
            {
                noKhObjLevelMap.put(rowSet.getString(1).trim() + "/" + rowSet.getString(2), rowSet.getString(3));
            }

            String perPlanSql = "  select plan_id,name,object_type  from  per_plan where  method=2 " + _sub_sql + "  and status=8  order by " + Sql_switcher.isnull("per_plan.a0000", "999999999") + " asc";
            rowSet = dao.search(perPlanSql);
            CommonData cData = null;
            while (rowSet.next())
            {
                String plan_id = rowSet.getString("plan_id");
                String name = new String(rowSet.getString("name").getBytes(), "GB2312");
                String object_type = rowSet.getString("object_type");
                LoadXml loadxml = null;
                if (BatchGradeBo.getPlanLoadXmlMap().get(plan_id) == null)
                {
                    loadxml = new LoadXml(this.conn, plan_id);
                    BatchGradeBo.getPlanLoadXmlMap().put(plan_id, loadxml);
                }
                else {
                    loadxml = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
                }
                Hashtable planParam = loadxml.getDegreeWhole();

                String allowLeadAdjustCard = (String) planParam.get("allowLeadAdjustCard"); // //允许领导制定及调整目标卡
                                                                                            // 默认为False
                if (allowLeadAdjustCard==null || !"true".equalsIgnoreCase(allowLeadAdjustCard)) {
                    continue;
                }
                String targetMakeSeries = (String) planParam.get("targetMakeSeries"); // 目标卡制订支持几级审批
                                                                                      // 5:本人
                                                                                      // -2：第四级领导
                                                                                      // ,-1：第三级领导,0：主管领导,1：直接上级
                // 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
                String targetAppMode = (String) planParam.get("targetAppMode");
                ;
                String SpByBodySeq = "False";
                if (planParam.get("SpByBodySeq") != null) {
                    SpByBodySeq = (String) planParam.get("SpByBodySeq");
                }
                if ("true".equalsIgnoreCase(SpByBodySeq))
                {
                    SetUnderlingObjectiveBo suob = new SetUnderlingObjectiveBo(this.conn, this.userView);
                    HashMap map = suob.getObjectBySeq(plan_id, 3);
                    if (map.size() > 0)
                    {
                        cData = new CommonData();
                        cData.setDataName(name + "_(设定下属任务) " + _img);
                        String herf = "";
                        if ("2".equals(object_type)) {
                            herf = "/performance/objectiveManage/setUnderlingObjective/underling_objective_tree.do?b_query=link&returnflag=" + this.getReturnflag() + "&entranceType=0&plan_id=" + plan_id;
                        } else {
                            herf = "/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&returnflag=" + this.getReturnflag() + "&opt=1&plan_id=" + plan_id;
                        }
                        cData.setDataValue(herf);
                        list.add(cData);
                    }
                    continue;

                }
                ArrayList _list = getSuperiorCreateObjectList(plan_id, targetAppMode, targetMakeSeries, object_type, objectMap, name, khObjLevelMap, noKhObjLevelMap, _img);
                if (_list.size() > 0)
                {

                    cData = new CommonData();
                    cData.setDataName(name + "_(设定下属任务) " + _img);
                    String herf = "";
                    if ("2".equals(object_type)) {
                        herf = "/performance/objectiveManage/setUnderlingObjective/underling_objective_tree.do?b_query=link&returnflag=" + this.getReturnflag() + "&entranceType=0&plan_id=" + plan_id;
                    } else {
                        herf = "/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&returnflag=" + this.getReturnflag() + "&opt=1&plan_id=" + plan_id;
                    }
                    cData.setDataValue(herf);
                    list.add(cData);
                }
                // list.addAll(_list);

            }

            if (rowSet != null) {
                rowSet.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (rowSet != null)
            {
                try
                {
                    rowSet.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }
        return list;
    }

    public ArrayList getSuperiorCreateObjectList(String planid, String targetAppMode, String targetMakeSeries, String object_type, HashMap objectMap, String planname, HashMap khObjLevelMap, HashMap noKhObjLevelMap, String _img)
    {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        CommonData cData = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            // targetMakeSeries 目标卡制订支持几级审批 5:本人 -2：第四级领导
            // ,-1：第三级领导,0：主管领导,1：直接上级
            String level_str = "";
            if ("1".equals(targetMakeSeries)) {
                level_str = "1";
            } else if ("2".equals(targetMakeSeries)) {
                level_str = "1,0";
            } else if ("3".equals(targetMakeSeries)) {
                level_str = "1,0,-1";
            } else if ("4".equals(targetMakeSeries)) {
                level_str = "1,0,-1,-2";
            }

            String _str = "";
            // String _str2;
            if (Sql_switcher.searchDbServer() == Constant.ORACEL)
            {
                _str = " and pms.level_o in (" + level_str + ") ";
                // _str2="pms.level_o";
            }
            else
            {
                _str = " and pms.level in (" + level_str + ") ";
                // _str2="pms.level";
            }
            if ("2".equals(object_type)) // 人员
            {
                // 处理标准的考核关系对象
                if ("0".equals(targetAppMode))
                {
                    String sql = "select object_id,a0101 from per_object where plan_id=" + planid + " and ( kh_relations is null or kh_relations=0 ) and ( sp_flag='01' or sp_flag is null ) ";
                    sql += "  and per_object.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=" + planid + " and pmb.object_id=per_object.object_id and ( pmb.status=1 or pmb.status=2 ) )";
                    sql += " and object_id in ( select std.object_id from per_mainbody_std std,per_plan_body ppb,per_mainbodyset pms where ppb.plan_id=" + planid + " and ppb.body_id=pms.body_id  and  std.body_id=pms.body_id and std.mainbody_id='" + this.userView.getA0100() + "' " + _str + " ) ";
                    rowSet = dao.search(sql);
                    while (rowSet.next())
                    {
                        String object_id = rowSet.getString("object_id");
                        if (objectMap.get(planid + "/" + object_id) != null) {
                            continue;
                        }
                        String level = (String) khObjLevelMap.get(object_id);
                        String a0101 = rowSet.getString("a0101");
                        cData = new CommonData();
                        cData.setDataName(planname + "_(设定" + a0101 + "目标任务) " + _img);
                        String herf = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=" + this.returnflag + "&entranceType=0&body_id=" + level + "&model=3&opt=1&planid=" + PubFunc.encryption(planid) + "&object_id=" + PubFunc.encryption(object_id);
                        cData.setDataValue(herf);
                        list.add(cData);
                    }
                }
                else if ("1".equals(targetAppMode)) // 汇报关系
                {
                    String e01a1 = this.userView.getUserPosId();
                    if (e01a1 != null && e01a1.trim().length() > 0)
                    {

                        RenderRelationBo bo = new RenderRelationBo(this.conn);
                        ArrayList posIDs = new ArrayList();
                        posIDs.add(e01a1);
                        ArrayList lowerPosList = new ArrayList();
                        bo.getLowerPosInfo(posIDs, targetMakeSeries, lowerPosList);
                        if (lowerPosList.size() > 0)
                        {

                            StringBuffer e01a1_str = new StringBuffer("");
                            for (int i = 0; i < lowerPosList.size(); i++) {
                                e01a1_str.append(",'" + ((String) lowerPosList.get(i)).toLowerCase() + "'");
                            }
                            String sql = "select object_id,a0101 from per_object where plan_id=" + planid + " and ( kh_relations is null or kh_relations=0 ) and ( sp_flag='01' or sp_flag is null ) ";
                            sql += "  and per_object.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=" + planid + " and pmb.object_id=per_object.object_id and ( pmb.status=1 or pmb.status=2 ) )";
                            sql += " and lower(e01a1) in (" + e01a1_str.substring(1) + ")";
                            rowSet = dao.search(sql);
                            while (rowSet.next())
                            {
                                String object_id = rowSet.getString("object_id");
                                if (objectMap.get(planid + "/" + object_id) != null) {
                                    continue;
                                }
                                String level = (String) khObjLevelMap.get(object_id);
                                String a0101 = rowSet.getString("a0101");
                                cData = new CommonData();
                                cData.setDataName(planname + "_(设定" + a0101 + "目标任务) " + _img);
                                String herf = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=" + this.returnflag + "&entranceType=0&body_id=" + level + "&model=3&opt=1&planid=" + PubFunc.encryption(planid) + "&object_id=" + PubFunc.encryption(object_id);
                                cData.setDataValue(herf);
                                list.add(cData);
                            }

                        }
                    }
                }
                // 处理非标准的考核关系对象
                String sql = "select object_id,a0101 from per_object where plan_id=" + planid + " and   kh_relations=1 and ( sp_flag='01' or sp_flag is null ) ";
                sql += "  and per_object.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=" + planid + " and pmb.object_id=per_object.object_id and ( pmb.status=1 or pmb.status=2 ) )";
                sql += " and object_id in ( select std.object_id from per_mainbody std,per_mainbodyset pms where std.plan_id=" + planid + "  and std.mainbody_id='" + this.userView.getA0100() + "' and std.body_id=pms.body_id " + _str + " ) ";
                rowSet = dao.search(sql);
                SetUnderlingObjectiveBo suob = new SetUnderlingObjectiveBo(this.conn);
                while (rowSet.next())
                {
                    String object_id = rowSet.getString("object_id");

                    if (objectMap.get(planid + "/" + object_id) != null) {
                        continue;
                    }
                    if (!suob.isCanSP(planid, object_id, this.userView.getA0100())) {
                        continue;
                    }
                    String level = (String) noKhObjLevelMap.get(object_id + "/" + planid);
                    String a0101 = rowSet.getString("a0101");
                    cData = new CommonData();
                    cData.setDataName(planname + "_(设定" + a0101 + "目标任务) " + _img);
                    String herf = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=" + this.returnflag + "&entranceType=0&body_id=" + level + "&model=3&opt=1&planid=" + PubFunc.encryption(planid) + "&object_id=" + PubFunc.encryption(object_id);
                    cData.setDataValue(herf);
                    list.add(cData);
                }

            }
            else
            {

                // 处理标准的考核关系对象
                if ("0".equals(targetAppMode))
                {
                    String sql = "select po.object_id,po.a0101,pm.mainbody_id from per_object po,per_mainbody pm,per_mainbodyset pms where pm.plan_id=" + planid + " and po.plan_id=" + planid;
                    sql += " and pm.object_id=po.object_id and pm.body_id=pms.body_id ";
                    if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                        sql += " and pms.level_o=5 ";
                    } else {
                        sql += " and pms.level=5 ";
                    }

                    sql += "  and po.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=" + planid + " and pmb.object_id=po.object_id and ( pmb.status=1 or pmb.status=2 ) )";

                    sql += " and ( po.kh_relations is null or po.kh_relations=0 ) and ( po.sp_flag='01' or po.sp_flag is null ) ";
                    sql += " and pm.mainbody_id in ( select std.object_id from per_mainbody_std std,per_plan_body ppb,per_mainbodyset pms where  ppb.plan_id=" + planid + " and ppb.body_id=pms.body_id  and std.body_id=pms.body_id   and std.mainbody_id='" + this.userView.getA0100() + "'   " + _str + " ) ";
                    rowSet = dao.search(sql);
                    while (rowSet.next())
                    {
                        String object_id = rowSet.getString("object_id");
                        String mainbody_id = rowSet.getString("mainbody_id");
                        if (objectMap.get(planid + "/" + object_id) != null) {
                            continue;
                        }
                        String level = (String) khObjLevelMap.get(mainbody_id);
                        String a0101 = rowSet.getString("a0101");
                        cData = new CommonData();
                        cData.setDataName(planname + "_(设定" + a0101 + "目标任务) " + _img);
                        String herf = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=" + this.returnflag + "&entranceType=0&model=1&opt=1&planid=" + PubFunc.encryption(planid) + "&object_id=" + PubFunc.encryption(object_id) + "&body_id=" + level;
                        cData.setDataValue(herf);
                        list.add(cData);
                    }
                }
                else if ("1".equals(targetAppMode)) // 汇报关系
                {

                    String e01a1 = this.userView.getUserPosId();
                    if (e01a1 != null && e01a1.trim().length() > 0)
                    {

                        RenderRelationBo bo = new RenderRelationBo(this.conn);
                        ArrayList posIDs = new ArrayList();
                        posIDs.add(e01a1);
                        ArrayList lowerPosList = new ArrayList();
                        bo.getLowerPosInfo(posIDs, targetMakeSeries, lowerPosList);
                        if (lowerPosList.size() > 0)
                        {

                            StringBuffer e01a1_str = new StringBuffer("");
                            for (int i = 0; i < lowerPosList.size(); i++) {
                                e01a1_str.append(",'" + ((String) lowerPosList.get(i)).toLowerCase() + "'");
                            }

                            String sql = "select po.object_id,po.a0101,pm.mainbody_id from per_object po,per_mainbody pm,per_mainbodyset pms where pm.plan_id=" + planid + " and po.plan_id=" + planid;
                            sql += " and pm.object_id=po.object_id and pm.body_id=pms.body_id ";
                            if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                                sql += " and pms.level_o=5 ";
                            } else {
                                sql += " and pms.level=5 ";
                            }
                            sql += " and po.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=" + planid + " and pmb.object_id=po.object_id and ( pmb.status=1 or pmb.status=2 ) )";
                            sql += " and ( po.kh_relations is null or po.kh_relations=0 ) and ( po.sp_flag='01' or po.sp_flag is null ) ";
                            sql += " and lower(pm.e01a1) in (" + e01a1_str.substring(1) + ") ";
                            rowSet = dao.search(sql);
                            while (rowSet.next())
                            {
                                String object_id = rowSet.getString("object_id");
                                String mainbody_id = rowSet.getString("mainbody_id");
                                if (objectMap.get(planid + "/" + object_id) != null) {
                                    continue;
                                }
                                String level = (String) khObjLevelMap.get(mainbody_id);
                                String a0101 = rowSet.getString("a0101");
                                cData = new CommonData();
                                cData.setDataName(planname + "_(设定" + a0101 + "目标任务) " + _img);
                                String herf = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=" + this.returnflag + "&entranceType=0&model=1&opt=1&planid=" + PubFunc.encryption(planid) + "&object_id=" + PubFunc.encryption(object_id) + "&body_id=" + level;
                                cData.setDataValue(herf);
                                list.add(cData);
                            }

                        }
                    }

                }

                // 处理非标准的考核关系对象

                String sql = "select po.object_id,po.a0101,pm.mainbody_id  from per_object po,per_mainbody pm,per_mainbodyset pms where pm.plan_id=" + planid + " and po.plan_id=" + planid;
                sql += " and pm.object_id=po.object_id and pm.body_id=pms.body_id ";
                sql += "  and po.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=" + planid + " and pmb.object_id=po.object_id and ( pmb.status=1 or pmb.status=2 ) )";
                sql += " and  po.kh_relations=1 and ( po.sp_flag='01' or po.sp_flag is null ) ";
                sql += " and  pm.mainbody_id='" + this.userView.getA0100() + "' " + _str + "   ";
                rowSet = dao.search(sql);
                SetUnderlingObjectiveBo suob = new SetUnderlingObjectiveBo(this.conn);
                while (rowSet.next())
                {
                    String object_id = rowSet.getString("object_id");
                    if (objectMap.get(planid + "/" + object_id) != null) {
                        continue;
                    }
                    if (!suob.isCanSP(planid, object_id, this.userView.getA0100())) {
                        continue;
                    }
                    String a0101 = rowSet.getString("a0101");
                    String level = (String) noKhObjLevelMap.get(object_id + "/" + planid);
                    cData = new CommonData();
                    cData.setDataName(planname + "_(设定" + a0101 + "目标任务) " + _img);
                    String herf = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=" + this.returnflag + "&entranceType=0&model=1&opt=1&planid=" + PubFunc.encryption(planid) + "&object_id=" + PubFunc.encryption(object_id) + "&body_id=" + level;
                    cData.setDataValue(herf);
                    list.add(cData);
                }

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (rowSet != null)
            {
                try
                {
                    rowSet.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }
        return list;
    }
	/**
	 * 我的任务显示待办任务 废弃原来去历史表取的规则  zhaoxg add 2014-7-25
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public ArrayList getPerformancePending(ArrayList matterList) throws UnsupportedEncodingException {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String receiver = this.userView.getA0100();
			String nbase = this.userView.getDbname();
			StringBuffer sql = new StringBuffer();
			sql.append("select Pending_title,Pending_url,Pending_status,bread,ext_flag,"+Sql_switcher.dateToChar("create_time", Sql_switcher.searchDbServer()==Constant.ORACEL?"yyyy-MM-dd HH24:mi":"yyyy-MM-dd HH:mm")+"create_time");
			sql.append(" from t_hr_pendingtask");
			sql.append(" where Pending_type='33'");
			sql.append(" and pending_status='0'");			
			//不能写死Usr，若其他人员库的a0100 和Usr下的a0100 重复的话，会出问题  haosl  bug 39727 
			//sql.append(" and Receiver='Usr" + receiver + "'");
			sql.append(" and Receiver='"+nbase + receiver + "'");
			sql.append("order by create_time desc");//haosl 20170317 update 绩效代办倒叙排序
			CommonData cData=null;
			rs = dao.search(sql.toString());
			String _img="";
			String type="";
			while(rs.next()){
				String temp=rs.getString("ext_flag").split("_")[1];
				type=getObject_Type(temp);
	            if ("1".equals(type))
	            {
	            	 _img = " <img src='/images/unit.gif' height='12px' border='0' align='middle' />";
	            }
	            else if ("3".equals(type))
	            {
	            	_img = " <img src='/images/unit.gif' height='12px' border='0' align='middle' />";
	            }
	            else if ("4".equals(type))
	            {
	            	_img = " <img src='/images/dept.gif' height='12px' border='0' align='middle' />";
	            }else{
	            	_img="";
	            }
                 String name = rs.getString("Pending_title");
                 cData = new CommonData();
                 //解决首页绩效待办中文乱码问题
                 cData.setDataName(name + _img);
                 String herf = rs.getString("Pending_url");
                 cData.setDataValue(herf);
                 String parse_date=rs.getString("create_time");
                 cData.put("date", parse_date);
                 matterList.add(cData);
            }			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
		}
		return matterList;

	}
	/**
	 * 获取绩效待办链接后面的图片模式  zhaoxg add 2014-8-29
	 * @return
	 */
	public String getObject_Type(String planid){
		String type="";
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			String sql="select object_type from per_plan where plan_id="+planid;
			RowSet rs=dao.search(sql);
			if(rs.next()){
				type=rs.getString("object_type");
			}
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		return type;
	}
    /**
     * 取得待评分的记录 360的多人评分 和 目标的单人评分
     * 
     * @param list
     * @return
     */
    public ArrayList getScoreList(ArrayList list)
    {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        RowSet rowSet2 = null;
        CommonData cData = null;
        try
        {
            String _sub_sql = "";
            String img_name = "";
            
           
            for (int i = 1; i <= 4; i++)
            {
                img_name = "";
                if (i == 1)
                {
                    _sub_sql = " and per_plan.object_type=1";
                    img_name = "unit.gif";
                }
                else if (i == 2)
                {
                    _sub_sql = " and per_plan.object_type=3";
                    img_name = "unit.gif";
                }
                else if (i == 3)
                {
                    _sub_sql = " and per_plan.object_type=4";
                    img_name = "dept.gif";
                }
                else if (i == 4) {
                    _sub_sql = " and per_plan.object_type=2";
                }

                String _img = "";
                if (i == 1 || i == 2 || i == 3) {
                    _img = " <img src='/images/" + img_name + "' height='12px' border='0' align='middle' />";
                }
                DirectUpperPosBo bo = new DirectUpperPosBo();
                String flag = bo.getGradeFashion("0"); // 1:下拉框方式 2：平铺方式
                // 360考核计划
                String perPlanSql = "select plan_id,name  from per_plan where ( status=4 or status=6 ) ";
                if (!userView.isSuper_admin()) {
                    perPlanSql += "and plan_id in (select plan_id from per_mainbody where   mainbody_id='" + userView.getA0100() + "' and  "+ Sql_switcher.isnull("status", "0")+"<>4 and  "+ Sql_switcher.isnull("status", "0")+"<>2 and  "+ Sql_switcher.isnull("status", "0")+"<>7 )";
                }
                if (!"USR".equalsIgnoreCase(userView.getDbname())) {
                    perPlanSql += " and 1=2 ";
                }
                perPlanSql += " and " + Sql_switcher.isnull("Method", "1") + "=1  " + _sub_sql + "  order by " + Sql_switcher.isnull("a0000", "999999999") + " asc";
                rowSet = dao.search(perPlanSql);

                while (rowSet.next())
                {
                	
                	 LoadXml aloadxml = null;
                     if (BatchGradeBo.getPlanLoadXmlMap().get(rowSet.getString("plan_id")) == null)
                     {
                         aloadxml = new LoadXml(this.conn, rowSet.getString("plan_id"));
                         BatchGradeBo.getPlanLoadXmlMap().put(rowSet.getString("plan_id"), aloadxml);
                     }
                     else
                     {
                         aloadxml = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(rowSet.getString("plan_id"));
                     }
                     Hashtable htxml = aloadxml.getDegreeWhole();
                	
                    if (filterScorePlan(rowSet.getString("plan_id"), dao,htxml))
                    {
                         String mailTogoLink = (String)htxml.get("MailTogoLink"); // 评分邮件通知、待办任务界面，360默认为1：多人考评界面 2：单人考评界面 3：不发邮件。目标默认为1：目标评分 3：不发邮件                    	
                         cData = new CommonData();
                         cData.setDataName(new String(rowSet.getString("name").getBytes(), "UTF-8") + "_(评分) " + _img);//不要写GB2312  郭峰
                         String herf = "";
                         if(mailTogoLink==null||mailTogoLink.trim().length()==0|| "1".equals(mailTogoLink))
                         {
                       
	                        herf = "/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&linkType=1&returnflag=" + this.getReturnflag() + "&planContext=all&operate=aaa" + rowSet.getString("plan_id");
	                        if ("1".equals(flag)) {
                                herf = "/selfservice/performance/batchGrade.do?b_query=link&model=0&linkType=1&returnflag=" + this.getReturnflag() + "&operate=aaa" + rowSet.getString("plan_id");
                            }
                         }
                         else if("2".equals(mailTogoLink))
                         {
                    		herf = "/selfservice/performance/singleGrade.do?b_query=link&fromModel=frontPanel&returnflag=" + this.getReturnflag() + "&model=0&to_plan_id=" + rowSet.getString("plan_id") + "&bint=int";																														
                         }
                         else {
                             continue;
                         }
	                        
	                    cData.setDataValue(herf);
                        list.add(cData);
                    }
                }
                 
                // 目标卡制定

                rowSet = dao.search("select count(plan_id) from per_plan where method=2 " + _sub_sql + " and ( status=4 or status=6 or status=8)");
                if (rowSet.next())
                {
                    if (rowSet.getInt(1) == 0) {
                        continue;
                    }
                }

                HashMap objectMap = new HashMap();
                perPlanSql = "  select distinct per_plan.plan_id,per_plan.name,per_plan.object_type,per_plan.a0000  from per_object po,per_plan";
                perPlanSql += " where per_plan.plan_id=po.plan_id and per_plan.method=2  and   " + Sql_switcher.isnull("po.sp_flag", "'01'") + "='01' " 
                           + " and po.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=po.plan_id and pmb.object_id=po.object_id "
                           + " and ( pmb.status=1 or pmb.status=2 ) ) " + " and per_plan.object_type=2 " + _sub_sql + "  and per_plan.status=8 and "
                           +"  po.object_id='" + this.userView.getA0100() + "' order by  per_plan.a0000  asc";
                rowSet = dao.search(perPlanSql);
                StringBuffer exist_ids = new StringBuffer("");
                while (rowSet.next())
                {
                    String plan_id = rowSet.getString("plan_id");
                    objectMap.put(plan_id + "/" + this.userView.getA0100(), "");
                    String name = new String(rowSet.getString("name").getBytes(), "GB2312");

                    cData = new CommonData();
                    cData.setDataName(name + "_(设定) " + _img);
                    String herf = "";
                    herf = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&entranceType=0&returnflag=" + this.getReturnflag() + "&body_id=5&model=2&opt=1&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(userView.getA0100());
                    cData.setDataValue(herf);
                    list.add(cData);

                    exist_ids.append("," + plan_id);
                }
                perPlanSql = " select distinct per_plan.plan_id,per_plan.name,per_plan.object_type,po.object_id,per_plan.a0000 " + " from per_object po,per_plan,per_mainbody pm where per_plan.plan_id=po.plan_id and per_plan.plan_id=pm.plan_id  and po.object_id=pm.object_id " + " and po.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=po.plan_id and pmb.object_id=po.object_id and ( pmb.status=1 or pmb.status=2 ) ) " + " and per_plan.method=2  and   " + Sql_switcher.isnull("po.sp_flag", "'01'") + "='01'  " + " and per_plan.object_type<>2 " + _sub_sql + "  and per_plan.status=8 and pm.body_id=-1  and  pm.mainbody_id='" + this.userView.getA0100() + "'  order by  per_plan.a0000  asc";
                rowSet = dao.search(perPlanSql);
                while (rowSet.next())
                {
                    String plan_id = rowSet.getString("plan_id");
                    String name = new String(rowSet.getString("name").getBytes(), "GB2312");
                    String object_id = rowSet.getString("object_id");
                    objectMap.put(plan_id + "/" + object_id, "");
                    cData = new CommonData();
                    cData.setDataName(name + "_(设定) " + _img);
                    String herf = "";
                    herf = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=" + this.getReturnflag() + "&entranceType=0&model=1&opt=1&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(object_id) + "&body_id=5";
                    cData.setDataValue(herf);
                    list.add(cData);

                    exist_ids.append("," + plan_id);
                }

                ArrayList _list = getSuperiorCreatePlanList(objectMap, _sub_sql, _img);
                list.addAll(_list);
 
                
                // 目标审批消息
                perPlanSql = "select  distinct per_plan.plan_id,per_plan.name,per_plan.object_type,per_plan.a0000  from per_object po,per_plan   where per_plan.plan_id=po.plan_id and po.sp_flag='02' and per_plan.method=2 " + " and po.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=po.plan_id and pmb.object_id=po.object_id and ( pmb.status=1 or pmb.status=2 ) ) " + " and per_plan.status=8 " + _sub_sql + "  and   po.currappuser='" + userView.getA0100() + "'  order by  per_plan.a0000  asc";
                rowSet = dao.search(perPlanSql);
                while (rowSet.next())
                {
                    String planid = rowSet.getString("plan_id");
                    String name = new String(rowSet.getString("name").getBytes(), "GB2312");
                    String object_type = rowSet.getString("object_type");
                    cData = new CommonData();
                    cData.setDataName(name + "_(审核) " + _img);
                    String herf = "";
                    if ("2".equals(object_type)) {
                        herf = "/performance/objectiveManage/setUnderlingObjective/underling_objective_tree.do?b_query=link&returnflag=" + this.getReturnflag() + "&entranceType=0&plan_id=" + planid;
                    } else {
                        herf = "/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&returnflag=" + this.getReturnflag() + "&opt=1&plan_id=" + planid;
                    }
                    cData.setDataValue(herf);
                    list.add(cData);

                }
                 
                // 目标评分
                HashMap yp_planMap = new HashMap();
                perPlanSql = "  select distinct per_plan.plan_id,per_plan.name,per_plan.object_type,per_plan.a0000 ";
                perPlanSql += " from per_mainbody,per_object,per_plan,per_mainbodyset pmbs where per_object.plan_id=per_mainbody.plan_id and " + " per_object.object_id=per_mainbody.object_id and per_plan.plan_id=per_mainbody.plan_id and per_mainbody.body_id=pmbs.body_id " + " and   mainbody_id='" + userView.getA0100() + "' and per_mainbody.status<>'2'   and per_object.sp_flag='03' " + " and   per_plan.Method=2  " + _sub_sql + "  and  ( per_plan.status=4 or per_plan.status=6 )  order by  per_plan.a0000  asc";
                rowSet = dao.search(perPlanSql);
                while (rowSet.next())
                {
                    yp_planMap.put(rowSet.getString("plan_id"), "1");
                }

                String TargetDefineItem = "";
                AnalysePlanParameterBo _bo = new AnalysePlanParameterBo(this.conn);
                Hashtable ht_table = _bo.analyseParameterXml();
                if (ht_table != null)
                {
                    if (ht_table.get("TargetDefineItem") != null && ((String) ht_table.get("TargetDefineItem")).trim().length() > 0) {
                        TargetDefineItem = ("," + (String) ht_table.get("TargetDefineItem") + ",").toUpperCase();
                    }
                }

                perPlanSql = "  select distinct per_plan.plan_id,per_plan.name,per_plan.object_type,per_plan.a0000 ";
                perPlanSql += " from per_mainbody,per_object,per_plan,per_mainbodyset pmbs,per_plan_body ppb where ppb.plan_id=per_mainbody.plan_id and nullif(ppb.isgrade,0) is null  and ppb.body_id=per_mainbody.body_id  and per_object.plan_id=per_mainbody.plan_id and " + " per_object.object_id=per_mainbody.object_id and per_plan.plan_id=per_mainbody.plan_id and per_mainbody.body_id=pmbs.body_id " + " and   mainbody_id='" + userView.getA0100() + "' and per_mainbody.status<>'2' "// and
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   // per_object.sp_flag='03'
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   // "
                        + " and   per_plan.Method=2  " + _sub_sql + "  and  ( per_plan.status=4 or per_plan.status=6 )  order by  per_plan.a0000  asc";
                rowSet = dao.search(perPlanSql);
                FieldItem item = DataDictionary.getFieldItem("score_org");
                while (rowSet.next())
                {
                    String plan_id = rowSet.getString("plan_id");
                    String name = new String(rowSet.getString("name").getBytes(), "GB2312");
                    String object_type = rowSet.getString("object_type");

                    LoadXml loadxml = null;
                    if (BatchGradeBo.getPlanLoadXmlMap().get(plan_id) == null)
                    {
                        loadxml = new LoadXml(this.conn, plan_id);
                        BatchGradeBo.getPlanLoadXmlMap().put(plan_id, loadxml);
                    }
                    else {
                        loadxml = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
                    }
                    Hashtable planParam = loadxml.getDegreeWhole();
                    String NoApproveTargetCanScore = (String) planParam.get("NoApproveTargetCanScore"); // 目标卡未审批也允许打分
                                                                                                        // True,
                                                                                                        // False,
                                                                                                        // 默认为
                                                                                                        // False
                    String GradeByBodySeq = (String) planParam.get("GradeByBodySeq"); // 按考核主体顺序号控制评分流程(True,
                                                                                      // False默认为False)
                    String mailTogoLink = (String)planParam.get("MailTogoLink"); // 评分邮件通知、待办任务界面，360默认为1：多人考评界面 2：单人考评界面 3：不发邮件。目标默认为1：目标评分 3：不发邮件
                	
                    if (planParam.get("TargetTraceEnabled") != null && "True".equalsIgnoreCase((String) planParam.get("TargetTraceEnabled")))
                    {
                        if (planParam.get("TargetDefineItem") != null && ((String) planParam.get("TargetDefineItem")).trim().length() > 0) {
                            TargetDefineItem = ("," + ((String) planParam.get("TargetDefineItem")).trim() + ",").toUpperCase(); // 目标卡指标
                        }
                    }

                    boolean isbr = true;
                    if (!"2".equals(object_type) && "False".equalsIgnoreCase(GradeByBodySeq))
                    {
                        String _str = "";
                        if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                            _str = " and pp.level_o=5 ";
                        } else {
                            _str = " and pp.level=5 ";
                        }
                        rowSet2 = dao.search(" select count(pp.body_id) from per_plan_body ppb,per_mainbodyset pp where ppb.body_id=pp.body_id and ppb.plan_id=" + plan_id + "  " + _str);
                        if (rowSet2.next())
                        {
                            if (rowSet2.getInt(1) == 0) {
                                isbr = false;
                            }
                        }

                    }

                    boolean isScoreOrg = false; // 是否考虑考核机构
                    if ("1".equals(item.getState()) && (TargetDefineItem != null && !"".equals(TargetDefineItem.trim()) && TargetDefineItem.indexOf("SCORE_ORG") != -1)) {
                        isScoreOrg = true;
                    }
                    if (isScoreOrg)
                    {

                        /** 本人与团队负责人同其他考核主体一样控制，当一条任务也没有的时候，也不显示了 */
                        String sql0 = "select per_mainbody.object_id from per_mainbody,per_mainbodyset pmbs where  " + " per_mainbody.body_id=pmbs.body_id  and per_mainbody.plan_id=" + plan_id + " and   per_mainbody.mainbody_id='" + userView.getA0100() + "' and   " + Sql_switcher.isnull("per_mainbody.status", "0") + "<>2 ";
                        if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                            sql0 += " and pmbs.level_o=5 ";
                        } else {
                            sql0 += " and pmbs.level=5 ";
                        }
                        rowSet2 = dao.search(sql0);
                        String self_object_id = "";
                        while (rowSet2.next())
                        {
                            self_object_id += ",'" + rowSet2.getString(1) + "'";
                            // count=rowSet2.getInt(1);
                        }

                        if ("false".equalsIgnoreCase(NoApproveTargetCanScore) && isbr)
                        {
                            boolean isself = false;
                            String key = "a0100";
                            if (!"2".equals(object_type)) {
                                key = "b0110";
                            }
                            if (self_object_id.length() > 0)
                            {
                                sql0 = "select count(po.object_id) from per_object po,p04,per_mainbody pm where po.plan_id=" + plan_id + " and pm.plan_id=" + plan_id;
                                sql0 += " and pm.object_id=po.object_id and   " + Sql_switcher.isnull("pm.status", "0") + "<>2  and  pm.mainbody_id='" + userView.getA0100() + "' and  p04.plan_id=" + plan_id + " and po.object_id in ( " + self_object_id.substring(1) + " )";
                                sql0 += " and p04." + key + "=po.object_id and po.sp_flag='03'";
                                rowSet2 = dao.search(sql0);
                                if (rowSet2.next())
                                {
                                    if (rowSet2.getInt(1) > 0) {
                                        isself = true;
                                    }
                                }
                            }
                            if (!isself)
                            {
                                sql0 = "select count(p0400) from p04 where plan_id=" + plan_id + " and  not (state is not null and state=-1 and chg_type is not null and chg_type=3) and  " + key + " in ( ";
                                sql0 += "select pm.object_id from per_mainbody pm,per_object po where po.plan_id=" + plan_id + " and pm.plan_id=" + plan_id;
                                sql0 += " and pm.object_id=po.object_id and po.sp_flag='03' and   " + Sql_switcher.isnull("pm.status", "0") + "<>2  and  pm.mainbody_id='" + userView.getA0100() + "'";
                                sql0 += " ) ";
                                if (this.userView.getUnitIdByBusi("5") != null && this.userView.getUnitIdByBusi("5").length() > 0 && !"UN".equalsIgnoreCase(this.userView.getUnitIdByBusi("5")))// &&(this.plan_vo.getInt("status")!=4&&this.plan_vo.getInt("status")!=6&&this.perObject_vo.getString("sp_flag")!=null&&!this.perObject_vo.getString("sp_flag").equals("")&&!this.perObject_vo.getString("sp_flag").equals("01"))
                                {
                                    String temp = this.userView.getUnitIdByBusi("5");
                                    String[] arr = temp.split("`");
                                    StringBuffer t_buf = new StringBuffer();
                                    for (int j = 0; j < arr.length; j++)
                                    {
                                        if (arr[j] == null || "".equals(arr[j])) {
                                            continue;
                                        }
                                        t_buf.append(" or score_org like '" + arr[j].substring(2) + "%'");
                                    }
                                    t_buf.append(" or score_org is null or score_org =''");
                                    sql0 += " and (" + t_buf.toString().substring(3) + ")";
                                }
                                else
                                {
                                    sql0 += " and (UPPER(score_org)='" + this.userView.getUserOrgId() + "' or UPPER(score_org)='" + this.userView.getUserDeptId() + "'";
                                    sql0 += " or score_org is null or score_org ='')";
                                }
                                rowSet2 = dao.search(sql0);
                                if (rowSet2.next())
                                {
                                    if (rowSet2.getInt(1) == 0) {
                                        continue;
                                    }
                                }

                            }

                        }

                        boolean isSelfRecordPF = false;
                        if (self_object_id.length() > 0)
                        {
                            String key = "a0100";
                            if (!"2".equals(object_type)) {
                                key = "b0110";
                            }
                            String sql = "select count(p0400) from p04 where plan_id=" + plan_id + " and  not (state is not null and state=-1 and chg_type is not null and chg_type=3)  and  " + key + " in ( " + self_object_id.substring(1) + " )";
                            sql += " and " + key + " in (select object_id from per_mainbody where plan_id=" + plan_id + " and   " + Sql_switcher.isnull("status", "0") + "<>2  and  mainbody_id='" + userView.getA0100() + "')";
                            rowSet2 = dao.search(sql);
                            if (rowSet2.next())
                            {
                                if (rowSet2.getInt(1) > 0) {
                                    isSelfRecordPF = true;
                                }
                            }

                        }

                        sql0 = "";
                        /*
                         * if(self_object_id.length()==0||!isSelfRecordPF) {
                         * String key="a0100"; if(!object_type.equals("2"))
                         * key="b0110"; String
                         * sql="select count(p0400) from p04 where plan_id="
                         * +plan_id+" and  "+key+
                         * " in (select pmb.object_id from per_mainbody pmb where pmb.plan_id="
                         * +plan_id+
                         * "  and ( pmb.status is null or  pmb.status<>2 )  and pmb.mainbody_id='"
                         * +userView.getA0100()+"' )"; rowSet2=dao.search(sql);
                         * boolean hasRecord=true; if(rowSet2.next()) {
                         * if(rowSet2.getInt(1)==0) hasRecord=false; }
                         * 
                         * if(this.userView.getUnit_id()!=null&&this.userView.
                         * getUnit_id().length()>0&&!this.userView.getUnit_id().
                         * equalsIgnoreCase
                         * ("UN"))//&&(this.plan_vo.getInt("status"
                         * )!=4&&this.plan_vo
                         * .getInt("status")!=6&&this.perObject_vo
                         * .getString("sp_flag"
                         * )!=null&&!this.perObject_vo.getString
                         * ("sp_flag").equals
                         * ("")&&!this.perObject_vo.getString("sp_flag"
                         * ).equals("01")) { String
                         * temp=this.userView.getUnit_id(); String[]
                         * arr=temp.split("`"); StringBuffer t_buf = new
                         * StringBuffer(); for(int j=0;j<arr.length;j++) {
                         * if(arr[j]==null||arr[j].equals("")) continue;
                         * t_buf.append
                         * (" or score_org like '"+arr[j].substring(2)+"%'"); }
                         * t_buf
                         * .append(" or score_org is null or score_org =''");
                         * sql+=" and ("+t_buf.toString().substring(3)+")";
                         * }else {
                         * sql+=" and (UPPER(score_org)='"+this.userView.
                         * getUserOrgId
                         * ()+"' or UPPER(score_org)='"+this.userView
                         * .getUserDeptId()+"'";
                         * sql+=" or score_org is null or score_org ='')"; }
                         * 
                         * rowSet2=dao.search(sql); if(rowSet2.next()) {
                         * if(rowSet2.getInt(1)==0&&hasRecord) continue; } }
                         */
                        if (self_object_id.length() == 0 || !isSelfRecordPF)
                        {
                            String key = "a0100";
                            if (!"2".equals(object_type)) {
                                key = "b0110";
                            }
                            sql0 = "select count(p0400) from p04 where plan_id=" + plan_id + "  and  not (state is not null and state=-1 and chg_type is not null and chg_type=3)   and  " + key + " in ( ";
                            sql0 += "select object_id from per_mainbody where plan_id=" + plan_id + " and   " + Sql_switcher.isnull("status", "0") + "<>2  and  mainbody_id='" + userView.getA0100() + "'";
                            sql0 += " ) ";
                            if (this.userView.getUnitIdByBusi("5") != null && this.userView.getUnitIdByBusi("5").length() > 0 && !"UN".equalsIgnoreCase(this.userView.getUnitIdByBusi("5")))// &&(this.plan_vo.getInt("status")!=4&&this.plan_vo.getInt("status")!=6&&this.perObject_vo.getString("sp_flag")!=null&&!this.perObject_vo.getString("sp_flag").equals("")&&!this.perObject_vo.getString("sp_flag").equals("01"))
                            {
                                String temp = this.userView.getUnitIdByBusi("5");
                                String[] arr = temp.split("`");
                                StringBuffer t_buf = new StringBuffer();
                                for (int j = 0; j < arr.length; j++)
                                {
                                    if (arr[j] == null || "".equals(arr[j])) {
                                        continue;
                                    }
                                    t_buf.append(" or score_org like '" + arr[j].substring(2) + "%'");
                                }
                                t_buf.append(" or score_org is null or score_org =''");
                                sql0 += " and (" + t_buf.toString().substring(3) + ")";
                            }
                            else
                            {
                                sql0 += " and (UPPER(score_org)='" + this.userView.getUserOrgId() + "' or UPPER(score_org)='" + this.userView.getUserDeptId() + "'";
                                sql0 += " or score_org is null or score_org ='')";
                            }
                            rowSet2 = dao.search(sql0);
                            if (rowSet2.next())
                            {
                                if (rowSet2.getInt(1) == 0) {
                                    continue;
                                }
                            }

                        }
                    }
                    else
                    {
                        rowSet2 = dao.search("select count(object_id) from per_mainbody where plan_id=" + plan_id + " and   " + Sql_switcher.isnull("status", "0") + "<>2  and  mainbody_id='" + userView.getA0100() + "'");
                        if (rowSet2.next())
                        {
                            if (rowSet2.getInt(1) == 0) {
                                continue;
                            }
                        }
                    }

                    if (isbr && NoApproveTargetCanScore != null && "False".equalsIgnoreCase(NoApproveTargetCanScore) && yp_planMap.get(plan_id) == null) {
                        continue;
                    }

                    boolean isadd = true;
                    if ("True".equalsIgnoreCase(GradeByBodySeq)) // 按考核主体顺序号控制评分流程(True,
                                                                 // False默认为False)
                    {
                        isadd = false;
                        StringBuffer ssql = new StringBuffer();
                        ssql.append("  select count(pm.object_id),pm.object_id,po.sp_flag  from per_mainbody pm left join");
                        ssql.append("  per_object po on pm.plan_id=po.plan_id and ");
                        ssql.append("  pm.object_id=po.object_id where pm.plan_id=" + plan_id + " and nullif(seq,0) is null");
                        ssql.append("  and   " + Sql_switcher.isnull("status", "0") + "<>2  and  mainbody_id='" + this.userView.getA0100() + "' ");
                        ssql.append("  group by pm.object_id,po.sp_flag");
                        rowSet2 = dao.search(ssql.toString());
                        while (rowSet2.next())
                        {
                            if (rowSet2.getInt(1) > 0)
                            {
                                String sp_flag = rowSet2.getString("sp_flag");
                                if ("03".equals(sp_flag) || "true".equalsIgnoreCase(NoApproveTargetCanScore)) {
                                    isadd = true;
                                }
                            }
                        }
                        if (!isadd)
                        {
                            StringBuffer sql = new StringBuffer();
                            sql.append("select count(b.object_id),b.object_id,po.sp_flag from per_mainbody b left join per_object po on ");
                            sql.append(" b.plan_id=po.plan_id and b.object_id=po.object_id ");
                            sql.append(" where   b.plan_id=" + plan_id + " ");
                            sql.append(" and   b.status<>2  and  b.mainbody_id='" + userView.getA0100() + "'   and b.object_id not in ( ");
                            sql.append(" select c.object_id from per_mainbody c where c.plan_id=" + plan_id + " and exists ( ");
                            sql.append(" select null from per_mainbody a where  a.object_id=c.object_id and c.seq<a.seq  and  a.plan_id=" + plan_id + " ");
                            sql.append(" and   a.status<>2  and  a.mainbody_id='" + userView.getA0100() + "' ) and c.status<>2  )");
                            sql.append("  group by b.object_id,po.sp_flag");

                            rowSet2 = dao.search(sql.toString());
                            while (rowSet2.next())
                            {
                                if (rowSet2.getInt(1) == 0)
                                {
                                    continue;
                                }
                                else
                                {
                                    String sp_flag = rowSet2.getString("sp_flag") == null ? "01" : rowSet2.getString("sp_flag");
                                    if ("03".equals(sp_flag) || "true".equalsIgnoreCase(NoApproveTargetCanScore))
                                    {
                                        isadd = true;
                                    }
                                }
                            }

                        }
                    }
                    if (isadd)
                    {
                    	
                    	if(mailTogoLink!=null&& "3".equals(mailTogoLink)) {
                            continue;
                        }
                    	
                    	// 若主体要打分的所有考核对象的目标卡都未提交则不出现此计划打分的链接  JinChunhai 2013.01.09                   	
                    	if("false".equalsIgnoreCase(NoApproveTargetCanScore))
                    	{
                    		boolean isCanView = true;
                    		StringBuffer sql = new StringBuffer();
                            sql.append("select po.sp_flag from per_object po,per_mainbody pm,per_plan_body pp ");
                            sql.append(" where pm.plan_id = " + plan_id + " and pm.plan_id = po.plan_id and pm.plan_id = pp.plan_id ");
                            sql.append(" and pm.body_id = pp.body_id and pm.object_id = po.object_id and pm.mainbody_id='" + userView.getA0100() + "' "); 
                            sql.append(" and po.sp_flag='03' and (isgrade is null or isgrade='0') and " + Sql_switcher.isnull("pm.status", "0") + "<>2 ");
                            rowSet2 = dao.search(sql.toString());
                            while (rowSet2.next())
                            {
                                String sp_flag = rowSet2.getString("sp_flag");                               
                                isCanView = false;
                            } 
                            if(isCanView) {
                                continue;
                            }
                    	}                   	                  	
                    	
                        cData = new CommonData();
                        cData.setDataName(name + "_(评分) " + _img);
                        String herf = "";
                        if ("2".equals(object_type)) {
                            herf = "/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&plan_id=" + plan_id + "&returnflag=" + this.getReturnflag() + "&opt=1&entranceType=0&isSort=0";
                        } else {
                            herf = "/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&plan_id=" + plan_id + "&returnflag=" + this.getReturnflag() + "&opt=1";
                        }
                        cData.setDataValue(herf);
                        list.add(cData);
                    }
                }
                 
                // 目标计划被驳回通知
                perPlanSql = " select distinct per_plan.plan_id,per_plan.name,per_plan.a0000 from per_object po,per_plan   where per_plan.plan_id=po.plan_id and po.sp_flag='07' and per_plan.method=2 " + " and po.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=po.plan_id and pmb.object_id=po.object_id and ( pmb.status=1 or pmb.status=2 ) ) " + " and per_plan.object_type=2 " + _sub_sql + "  and per_plan.status=8 and   po.object_id='" + userView.getA0100() + "'  order by  per_plan.a0000  asc";
                rowSet = dao.search(perPlanSql);
                while (rowSet.next())
                {
                    String name = rowSet.getString("name");
                    cData = new CommonData();
                    cData.setDataName(new String(name.getBytes(), "GB2312") + "_(" + ResourceFactory.getProperty("info.appleal.state10") + ") " + _img);
                    String herf = "/performance/objectiveManage/myObjective/my_objective_list.do?b_init=init&returnflag=" + this.getReturnflag() + "&opt=1";
                    cData.setDataValue(herf);
                    list.add(cData);
                }

                perPlanSql = " select distinct per_plan.plan_id,per_plan.name,per_plan.a0000 from per_object po,per_plan,per_mainbody pm ,per_mainbodyset pms " + " where po.object_id=pm.object_id and pm.plan_id=po.plan_id  and per_plan.plan_id=po.plan_id " + " and po.object_id not in (select object_id from per_mainbody pmb where pmb.plan_id=po.plan_id and pmb.object_id=po.object_id and ( pmb.status=1 or pmb.status=2 ) ) " + " and pm.body_id=pms.body_id   and po.sp_flag='07' and per_plan.method=2" + " and per_plan.status=8  " + _sub_sql + " and   pm.mainbody_id='" + userView.getA0100() + "' and per_plan.object_type<>2";
                if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                    perPlanSql += " and pms.level_o=5 ";
                } else {
                    perPlanSql += " and pms.level=5 ";
                }
                rowSet = dao.search(perPlanSql + "  order by  per_plan.a0000  asc");
                while (rowSet.next())
                {
                    String plan_id = rowSet.getString("plan_id");
                    String name = rowSet.getString("name");
                    cData = new CommonData();
                    cData.setDataName(new String(name.getBytes(), "GB2312") + "_(" + ResourceFactory.getProperty("info.appleal.state10") + ") " + _img);
                    String herf = "/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&returnflag=" + this.getReturnflag() + "&opt=1&plan_id=" + plan_id;
                    cData.setDataValue(herf);
                    list.add(cData);
                }
                 

            }
             
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rowSet != null) {
                    rowSet.close();
                }
                if (rowSet2 != null) {
                    rowSet2.close();
                }
            }
            catch (Exception ee)
            {
                ee.printStackTrace();
            }
        }
        return list;
    }

    public ArrayList getScoreBeanList(ArrayList list)
    {
        return list;
    }

    private boolean filterScorePlan(String plan_id, ContentDAO dao,Hashtable htxml)
    {
        boolean flag = true;
        RowSet rowSet = null;
        try
        {
            
            String performanceType = (String) htxml.get("performanceType");

            if (!"0".equals(performanceType)) {
                return false;
            }
            if (!"FALSE".equalsIgnoreCase((String) htxml.get("HandEval"))) {
                return false;
            }
            RecordVo vo = new RecordVo("per_plan");
            vo.setInt("plan_id", Integer.parseInt(plan_id));
            vo = dao.findByPrimaryKey(vo);
            String _str = "";
            if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                _str = "pms.level_o";
            } else {
                _str = "pms.level ";
            }

            String a_mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval");

          //  String sql = "select pm.status from per_mainbody pm,per_mainbodyset pms  where  pm.body_id=pms.body_id   and pm.plan_id=" + plan_id + " and pm.status<>4   and pm.mainbody_id='" + this.userView.getA0100() + "' ";
            String sql = "select count(pm.status) from per_mainbody pm,per_mainbodyset pms  where  pm.body_id=pms.body_id   and pm.plan_id=" + plan_id + " and pm.status<>4   and pm.mainbody_id='" + this.userView.getA0100() + "' ";
            if ("False".equalsIgnoreCase(a_mitiScoreMergeSelfEval))
            { 
                if (vo.getInt("object_type") == 2) // 考核人员
                {
                    sql += " and pm.object_id<>'" + this.userView.getA0100() + "'";
                } else {
                    sql += " and ( " + _str + " is null or " + _str + "<>5 ) ";
                }
            } 
            rowSet = dao.search(sql+" and ( pm.status=2 or pm.status=7 ) ");
            int subNum=0;
            if(rowSet.next()) {
                subNum=rowSet.getInt(1);
            }
            rowSet = dao.search(sql);
            if(rowSet.next())
            {
            	int totalNum=rowSet.getInt(1);
            	if(totalNum==subNum)
            	{
            		rowSet.close();
            		return false;
            	}
            	if(totalNum==0)
            	{
            		rowSet.close();
            		return false;
            	}
            }
            
            
            
            /*
            
            boolean isNoMark = false;
            boolean isMarking = false;
            boolean isMarked = false;
            boolean isFinished = false;
            int n = 0;
            while (rowSet.next())
            {
                n++;
                int a_status = rowSet.getInt("status");
                if (a_status == 0)
                    isNoMark = true;
                else if (a_status == 1)
                    isMarking = true;
                else if (a_status == 2 || a_status == 7)
                    isMarked = true;
                else if (a_status == 8) // 已完成，针对多人打分。
                    isFinished = true;
            }
            if (n == 0)
                return false;

            if (isNoMark && !isMarking && !isMarked && !isFinished)
            {

            }
            else if (!isNoMark && !isMarking && isMarked && !isFinished)
            {
                return false;
            }
            else if (!isNoMark && !isMarking && isFinished)
            {
                // return false;
            }
            else
            {

            }
            
            */
            if (rowSet != null) {
                rowSet.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != rowSet)
            {
                try
                {
                    rowSet.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 求实际的业务数,本次模板做了多少人的业务
     * 
     * @return
     */
    public String getRecordBusiTopic(String Noticetempid, String type)
    {
        int nmax = 0;
        StringBuffer stopic = new StringBuffer();
        stopic.append("(");
        RowSet rset = null;
        RowSet rt2 = null;
        try
        {
        	String _withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
            {
                _withNoLock=" WITH(NOLOCK) ";
            }
            ContentDAO dao = new ContentDAO(this.conn);
            DbWizard dbw = new DbWizard(this.conn);
            String sql = "select a0101 from tmessage "+_withNoLock+"  where  Noticetempid=" + Noticetempid + " and state='0' ";
            if (type != null && ("10".equals(type) || "11".equals(type))) {
                sql = "select organization.codeitemdesc from tmessage "+_withNoLock+",organization "+_withNoLock+" where organization.codeitemid=tmessage.b0110 and Noticetempid=" + Noticetempid + " and tmessage.state='0' ";
            }
            String filter_by_manage_priv = "0"; // 接收通知单数据方式：0接收全部数据，1接收管理范围内数据
            String include_suborg="1"; //0不包括下属单位, 1包括(默认值)
            RowSet rowSet = null;
            try
            {
                rowSet = dao.search("select ctrl_para from template_table "+_withNoLock+" where tabid=" + Noticetempid);

                if (rowSet.next())
                {
                    String sxml = Sql_switcher.readMemo(rowSet, "ctrl_para"); // vo.getString("ctrl_para");
                    Document doc = null;
                    Element element = null;
                    if (sxml != null && sxml.trim().length() > 0)
                    {
                        doc = PubFunc.generateDom(sxml);;
                        String xpath = "/params/receive_notice";
                        XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        List childlist = findPath.selectNodes(doc);
                        if (childlist != null && childlist.size() > 0)
                        {
                            element = (Element) childlist.get(0);
                            filter_by_manage_priv = (String) element.getAttributeValue("filter_by_manage_priv");
                            if(element.getAttributeValue("include_suborg")!=null) {
                                include_suborg=(String)element.getAttributeValue("include_suborg");
                            }
                        }

                    }
                }
                if (rowSet != null) {
                    rowSet.close();
                }
            }
            catch (Exception e)
            {
                return "0";
            }
            finally
            {
                if (null != rowSet)
                {
                    try
                    {
                        rowSet.close();
                    }
                    catch (Exception e2)
                    {
                        e2.printStackTrace();
                    }
                }
            }

            if (!this.userView.isSuper_admin() && "1".equals(filter_by_manage_priv))
            {
                /*
                 * sql+=" and (tmessage.b0110 like '";
                 * if((this.userView.getManagePrivCodeValue
                 * ()==null||this.userView
                 * .getManagePrivCodeValue().trim().length
                 * ()==0)&&this.userView.getManagePrivCode().length()==0)
                 * sql+="##"; else sql+=this.userView.getManagePrivCodeValue();
                 * sql+="%' or tmessage.b0110 is null or tmessage.b0110='')";
                 */
            	String operOrg = this.userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
				if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
				{
            	
	                sql += " and ( "; 
	                if (operOrg != null && operOrg.length() > 3)
	                {
	                    StringBuffer tempSql = new StringBuffer("");
	                    String[] temp = operOrg.split("`");
	                    for (int j = 0; j < temp.length; j++)
	                    {
	                        if (temp[j] != null && temp[j].length() > 0)
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
	                    if (tempSql.length() > 0)
	                    {
	                        sql += tempSql.substring(3);
	                    }
	                    else {
                            sql += " tmessage.b0110='##'";
                        }
	                }
	                else {
                        sql += " tmessage.b0110='##'";
                    }
	
	                sql += " or nullif(tmessage.b0110,'') is null  )";
				}

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
            if (dbw.isExistTable(this.userView.getUserName() + "templet_" + Noticetempid, false))
            {
                if (type != null && ("10".equals(type) || "11".equals(type)))
                {
                    sql += " union select codeitemdesc_1 a0101 from " + this.userView.getUserName() + "templet_" + Noticetempid + " "+_withNoLock+"  where state=1 ";
                }
                else {
                    sql += " union select a0101_1 a0101 from " + this.userView.getUserName() + "templet_" + Noticetempid + " "+_withNoLock+" where state=1 ";
                }
            }

            rset = dao.search(sql);
            int i = 0;
            while (rset.next())
            {
                if (i > 2) {
                    break;
                }
                if (i != 0) {
                    stopic.append(",");
                }
                stopic.append(rset.getString(1) == null ? "" : rset.getString(1));
                i++;
            }
            /**syl add**/
            StringBuffer sqlBf=new StringBuffer();
            sqlBf.append("select count(*) a,"+Sql_switcher.dateToChar("max(receive_time)", "yyyy-MM-dd HH:mm")+" date_b from ( ");
            String stateInfo=" and state='0' ";
            String initSql="";
            sql="";//初始化 sqlBf.append(initSql);
            String uninSql="";
            if (type != null && ("10".equals(type) || "11".equals(type))){

            	initSql += "select  b0110,max(receive_time) receive_time  from tmessage "+_withNoLock+"  where  Noticetempid=" + Noticetempid + "   ";
            }else{
            	initSql += "select distinct a0100,lower(db_type) db_type,max(receive_time) receive_time  from tmessage "+_withNoLock+"   where  Noticetempid=" + Noticetempid + " "; 
            }

            // sql="select count("+Sql_switcher.sqlNull("a0101",0)+") a from tmessage   where  Noticetempid="+Noticetempid+" and state='0'  "
            // ;
            if (!this.userView.isSuper_admin() && "1".equals(filter_by_manage_priv))
            {
                /*
                 * sql+=" and (tmessage.b0110 like '";
                 * if((this.userView.getManagePrivCodeValue
                 * ()==null||this.userView
                 * .getManagePrivCodeValue().trim().length
                 * ()==0)&&this.userView.getManagePrivCode().length()==0)
                 * sql+="##"; else sql+=this.userView.getManagePrivCodeValue();
                 * sql+="%' or tmessage.b0110 is null or tmessage.b0110='')";
                 */
            	String operOrg = this.userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
				if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
				{
	                sql += " and ( "; 
	                if (operOrg != null && operOrg.length() > 3)
	                {
	                    StringBuffer tempSql = new StringBuffer("");
	                    String[] temp = operOrg.split("`");
	                    for (int j = 0; j < temp.length; j++)
	                    {
	                        if (temp[j] != null && temp[j].length() > 0)
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
	                    if (tempSql.length() > 0)
	                    {
	                        sql += tempSql.substring(3);
	                    }
	                    else {
                            sql += " tmessage.b0110='##'";
                        }
	                }
	                else {
                        sql += " tmessage.b0110='##'";
                    }
	
	                sql += "  or nullif(tmessage.b0110,'') is null)";
				}
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
            
            if(type != null && ("10".equals(type) || "11".equals(type))) {
            	sql+="group by b0110";
            }else {
            	sql+="group by a0100,lower(db_type)";
            }
     //       sql += " ) aa";

            if (dbw.isExistTable(this.userView.getUserName() + "templet_" + Noticetempid, false))
            {
                if (type != null && ("10".equals(type) || "11".equals(type)))
                {
                  //sql+= " union select count(" + Sql_switcher.sqlNull("codeitemdesc_1", 0) + ") a from " + this.userView.getUserName() + "templet_" + Noticetempid + "  where state=1  ";
                   if("10".equals(type)){
                	   uninSql+=" union select  distinct b0110,null receive_time  from "+this.userView.getUserName()+"templet_"+Noticetempid+" "+_withNoLock+"  where state=1  ";   
                   }else{
                	   uninSql+=" union select  distinct e01a1,null receive_time  from "+this.userView.getUserName()+"templet_"+Noticetempid+" "+_withNoLock+"  where state=1  ";
                   }
                    
					
                }
                else
                {
                //	sql += " union select count(" + Sql_switcher.sqlNull("a0101_1", 0) + ") a from " + this.userView.getUserName() + "templet_" + Noticetempid + " where state=1 ";
                	uninSql +=" union select distinct a0100,lower(basepre) db_type,null receive_time from "+this.userView.getUserName()+"templet_"+Noticetempid+" "+_withNoLock+"  where state=1  ";
					 
                }
            }
//            sql+=" ) aa";
            //syl 57320 首页下通知的待办的接收时间与发送实际时间不符,显示的是当前系统时间
            StringBuffer resultSql=new StringBuffer();
            resultSql.append(sqlBf);
            resultSql.append(initSql);
            resultSql.append(stateInfo);
            resultSql.append(sql);
            resultSql.append(uninSql);
            resultSql.append(" ) aa");
            rset = dao.search(resultSql.toString());
//            System.out.println(resultSql.toString());
            // rset=dao.search("select count(*) from tmessage   where  Noticetempid="+Noticetempid+" and (State='0' or State='1')");
            String date_max="";//记录通知单最大接收日期
            while (rset.next()) {
            	nmax += rset.getInt(1);
            	date_max = rset.getString(2)==null?"":rset.getString(2);
            	if(StringUtils.isEmpty(date_max)&&nmax>0){
            		 resultSql.setLength(0);
            		 resultSql.append(sqlBf);
            		 resultSql.append(initSql);
                     resultSql.append(sql);
                     resultSql.append(" ) aa");
            		rt2 = dao.search(resultSql.toString());
            		if (rt2.next()) {
                    	date_max = rt2.getString("date_b")==null?"":rt2.getString("date_b");
            		}
            	}
            }
            // if(nmax!=i)
            stopic.append(",");
            stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
            // stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
            stopic.append(nmax);
            if (type != null && ("10".equals(type) || "11".equals(type)))
            {
                stopic.append("条)");
            }
            else {
                stopic.append("人)");
            }

            if (rset != null) {
                rset.close();
            }
            if (nmax == 0)
            {
                stopic.setLength(0);
                stopic.append("0");
            }
            stopic.append("｀"+date_max);
        }
        catch (Exception ex)
        {
            return stopic.toString();
        }
        finally
        {
            if (null != rset)
            {
                try
                {
                    rset.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
            PubFunc.closeDbObj(rt2);
        }
        return stopic.toString();
    }

    
    /**
     * 获得待审批的报表
     * @param list
     * @return
     */
    public ArrayList getApproveList(ArrayList list){
    	try{
    		Report_isApproveBo bo = new Report_isApproveBo(conn,userView);
			ContentDAO dao=new ContentDAO(this.conn);
			String sql = "select * from treport_ctrl where currappuser = '"+this.userView.getUserName()+"' and status = '4' and unitcode in (select unitcode from operuser where UserName in (select username from treport_ctrl where currappuser = '"+this.userView.getUserName()+"'))";
			RowSet rs = dao.search(sql);
			String str = "审核";
			while(rs.next()){
				CommonData cData = new CommonData();
					String tabid = rs.getString("tabid");
					String name = bo.getTname(tabid);
					String username = URLEncoder.encode(SafeCode.encode((rs.getString("username"))));
					boolean isUpapprove = bo.isUpapprove(username);
					cData.setDataName(name+"("+str+")");
					cData.setDataValue("/report/edit_report/reportSettree.do?b_query=link&operateObject=1&status=4&isUpapprove="+isUpapprove+"&username="+username+"&code="+tabid+"&obj1=1&returnType=1");//add by xiegh on 20171101 加返回标识 0：返回主界面；1：返回更多主界面
					list.add(cData);
			}
    	}catch (Exception e)
        {
            e.printStackTrace();
        }
    	
    	return list;
    }
    public ArrayList getReturnList(ArrayList list){
    	try{
    		Report_isApproveBo bo = new Report_isApproveBo(conn,userView);
			ContentDAO dao=new ContentDAO(this.conn);
			String sql = "select * from treport_ctrl where currappuser = '"+this.userView.getUserName()+"' and status = '2' and unitcode=(select unitcode from operuser where UserName = '"+this.userView.getUserName()+"')";
			RowSet rs = dao.search(sql);
			String str = "驳回";
			while(rs.next()){
				CommonData cData = new CommonData();
					String tabid = rs.getString("tabid");
					String name = bo.getTname(tabid);
					String username = SafeCode.encode((rs.getString("username")));
					boolean isUpapprove = bo.isUpapprove(username);
					cData.setDataName(name+"("+str+")");
					cData.setDataValue("/report/edit_report/reportSettree.do?encryptParam="+PubFunc.encrypt("b_query=link&operateObject=1&status=2&isUpapprove="+isUpapprove+"&username="+username+"&code="+tabid+"&obj1=1&returnType=1"));//add by xiegh on 20171101 加返回标识 0：返回主界面；1：返回更多主界面
					list.add(cData);
			}
    	}catch (Exception e)
        {
            e.printStackTrace();
        }
    	
    	return list;
    }
    
   
	/** 
	* @Title: getOKRPending 
	* @Description: 获取OKR的待办信息
	* @param @return
	* @return ArrayList
	*/ 
	public ArrayList getOKRPending() {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String receiver = this.userView.getUserName();
			if (this.userView.getStatus()!=4){//不是自助用户登录
				receiver =this.userView.getS_userName();
			}
			if (receiver==null) {
                receiver=this.userView.getUserName();
            }
			
			StringBuffer sql = new StringBuffer();
			sql.append("select Pending_title,Pending_url,Pending_status,bread,"+Sql_switcher.dateToChar("create_time",Sql_switcher.searchDbServer()==Constant.ORACEL?"yyyy-MM-dd HH24:mi":"yyyy-MM-dd HH:mm")+" create_time");
			sql.append(" from t_hr_pendingtask");
			sql.append(" where Pending_type='58'");
			sql.append(" and (pending_status='0')");			
			sql.append(" and upper(Receiver)='" + receiver.toUpperCase() + "'");
			sql.append(" order by lasttime desc");
			LazyDynaBean abean=null;
			rs = dao.search(sql.toString());
			while(rs.next()){
				abean=new LazyDynaBean();
				abean.set("name",rs.getString("Pending_title"));
				abean.set("url",rs.getString("Pending_url"));
				abean.set("target","i_body");
				String parse_date=rs.getString("create_time");
	            abean.set("date", parse_date);
                list.add(abean);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
		}
		return list;

	}
	
	
	/** 
	* @Title: getPendingTask 
	* @Description: 获取待办信息
	* @param @return
	* @return ArrayList
	*/ 
	public ArrayList getPendingTask() {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String receiver = this.userView.getUserName();
			String receview_S_userName = "";
			if (this.userView.getStatus()!=4){//不是自助用户登录
				receview_S_userName =this.userView.getS_userName();
			}
			StringBuffer sql = new StringBuffer();
			 
			sql.append("select Pending_title,Pending_url,Pending_status,bread,"+Sql_switcher.dateToChar("create_time", Sql_switcher.searchDbServer()==Constant.ORACEL?"yyyy-MM-dd HH24:mi":"yyyy-MM-dd HH:mm")+" create_time");
			sql.append(" from t_hr_pendingtask");
			sql.append(" where ");
			sql.append(" Pending_type not in ('33','34','39','58' ,'32','80')  and  pending_status='0' ");//添加'80'，我的任务不显示问卷调查   wangb 20180731			
			sql.append(" and  ( upper(Receiver)='" + receiver.toUpperCase() + "'");
			
			if(this.userView.getA0100()!=null&&this.userView.getA0100().trim().length()>0)
			{
				sql.append(" or upper(Receiver)='"+this.userView.getDbname().toUpperCase()+this.userView.getA0100()+"'");
			} 
			//查询业务用户关联的自助用户的待办  haosl add
			if(StringUtils.isNotBlank(receview_S_userName)) {
				sql.append(" or upper(Receiver)='"+receview_S_userName.toUpperCase()+"'");
			}
			sql.append(" )  order by lasttime desc");
			CommonData 	cData=new CommonData();
			rs = dao.search(sql.toString());
			while(rs.next()){
				cData=new CommonData();
				cData.setDataName(rs.getString("Pending_title"));
        		cData.setDataValue(rs.getString("Pending_url"));  
        		String parse_date=rs.getString("create_time");
                cData.put("date", parse_date);
                list.add(cData);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;

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

}
