package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.*;

public class SearchYpInstanceTrans extends IBusiness {

	
	private String getInsFilterWhere(String othername)
	{
		String _withNoLock="";
		if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
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
		
	//	if(this.userView.getUserPosId()!=null&&this.userView.getUserPosId().trim().length()>0)
		{
	//		strwhere.append(" or  ( "+othername+"actor_type=3 and upper("+othername+"a0100) in ('"+userid.toUpperCase()+"','"+this.userView.getUserName().toUpperCase()+"') and upper("+othername+"actorid)='@K"+this.userView.getUserPosId().trim()+"' ) ");
		
			String a0100=this.userView.getDbname()+this.userView.getA0100();
			if(a0100==null||a0100.length()==0)
				a0100=this.userView.getUserName();
			/**组织元*/
			strwhere.append(" or (T.actor_type='3' and T.a0100='"+a0100+"')"); 
		}
		
		return " ( "+strwhere.toString()+" ) ";
	}
	
	
	

	public void execute() throws GeneralException {
		
		String templateId=(String)this.getFormHM().get("templateId");
		StringBuffer strsql2=new StringBuffer();  //模板查询sql
		String sp_flag=(String)this.getFormHM().get("sp_flag");
		StringBuffer strsql=new StringBuffer(); 
		String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");
		String days=(String)this.getFormHM().get("days");
		String query_type=(String)this.getFormHM().get("query_type");
		if(query_type==null || query_type.trim().length()==0)
			query_type="1";
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				_withNoLock=" WITH(NOLOCK) ";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String fromflag=(String)map.get("fromflag"); 
			String bs_flag="1";  //1：审批任务 2：加签任务 3：报备任务  4：空任务
			if(map.get("bs_flag")!=null)
			{
				bs_flag=(String)map.get("bs_flag");
				map.remove("bs_flag");
			}
			
			String type=(String)map.get("type");  //1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整   23：考勤业务办理  24：非考勤业务(业务申请不包含考勤信息)
			map.remove("type");
			String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				format_str="yyyy-MM-dd hh24:mi";
			String static_="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				static_="static_o";
			}
			
			strsql.append("select U.ins_id,T.task_topic,U.tabid,U.actorname fullname,(select o.codeitemdesc from organization o "+_withNoLock+" where o.codeitemid=U.b0110) unitname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" end_date,");
			strsql.append("T.actorname,T.task_id,T.flag,U.tabid,tt."+static_+",U.finished insfinished   from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table tt "+_withNoLock+"");
			strsql.append(" where  T.ins_id=U.ins_id  and U.tabid=tt.tabid and ((task_type='2' ) and  (task_state='5'  or task_state='6' ) ) ");
			
			
			strsql2.append("select distinct U.tabid,template_table.name  from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table "+_withNoLock+"");
			strsql2.append("  where T.ins_id=U.ins_id  and template_table.tabid=U.tabid and ((task_type='2' ) and  (task_state='5'  or task_state='6' ) )  ");
			//strsql2.append("and  task_topic not like '%共0人%' and  task_topic not like '%共0条%'");
			if(type!=null&&("10".equals(type)|| "11".equals(type))){
				strsql.append(" and tt."+static_+"="+type );
				strsql2.append(" and   U.tabid=template_table.tabid and template_table."+static_+"="+type+"  ");
			}
			else
			{
				if(userView.getStatus()!=4){
					strsql.append(" and tt."+static_+"!=10 and tt."+static_+"!=11 ");
					strsql2.append(" and template_table."+static_+"!=10 and template_table."+static_+"!=11 ");
				}
			}
			
			TemplateTableParamBo tp=new TemplateTableParamBo(this.frameconn); 
	    	String tabids=tp.getAllDefineKqTabs(0); 
			if(tabids.length()==0)
				tabids+=",-1000";
			if(type!=null&& "23".equals(type)){ //考勤业务办理
				strsql.append(" and tt.tabid in ("+tabids.substring(1)+")" );
				strsql2.append(" and    template_table.tabid in ("+tabids.substring(1)+")" );
			}
			else
			{
				if(fromflag==null||!"6".equalsIgnoreCase(fromflag)||("6".equals(fromflag)&&type!=null&& "24".equals(type)))
				{
					strsql.append(" and tt.tabid not in ("+tabids.substring(1)+")" );
					strsql2.append(" and    template_table.tabid not in ("+tabids.substring(1)+")" );
				}
			}
			
			
			//1：审批任务 2：加签任务 3：报备任务  4：空任务
			strsql.append(" and  "+Sql_switcher.isnull("T.bs_flag","'1'")+"='"+bs_flag+"' ");
			strsql2.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='"+bs_flag+"' ");
			 
			
			
//			if(this.userView.getA0100()!=null&&this.userView.getA0100().trim().length()>0){
//				strsql.append(" and  upper(U.actorid)!='"+this.userView.getDbname().toUpperCase()+this.userView.getA0100().toUpperCase()+"' ");
//			}
			//strsql.append(" and  upper(U.actorid)!='"+this.userView.getUserName().toUpperCase()+"' ");
			strsql.append(" and  T.task_type!=1 ");
			//strsql.append(" and (task_topic not like '%共0人%' and task_topic not like '%共0条%'  ) ");
			strsql.append(" and (( (task_topic not like '%共0人%' and task_topic not like '%共0条%'  ) and ");
			strsql.append(" ( T.flag=1 and U.ins_id in (select ins_id from t_wf_task "+_withNoLock+" where "+getInsFilterWhere("")+" and  (task_state='5'  or task_state='6' ) and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("content"),"' '")+"<>'重新分派' )) )");
			strsql.append(" or ( ");
			
			strsql.append(" ("+getInsFilterWhere("T.")+" and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"),"' '")+"<>'重新分派'    )"); 
			strsql.append(" and   U.ins_id not in ( ");
			strsql.append("  select ins_id from t_wf_task "+_withNoLock+" where  ( task_topic not like '%共0人%' and  task_topic not like '%共0条%'  ) and (task_type='2' )  and  (task_state='5'  or task_state='6' )  and flag=1 ");
			strsql.append("   and   ins_id in (select ins_id from t_wf_task "+_withNoLock+" where  "+getInsFilterWhere("")+"   and  (task_state='5'  or task_state='6' ) and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("content"),"' '")+"<>'重新分派'  ) ) ");
			strsql.append(" )");
			strsql.append(")");
			strsql2.append(" and  T.task_type!=1 ");
			//strsql.append(" and (task_topic not like '%共0人%' and task_topic not like '%共0条%'  ) ");
			strsql2.append(" and (( (task_topic not like '%共0人%' and task_topic not like '%共0条%'  ) and ");
			strsql2.append(" ( T.flag=1 and U.ins_id in (select ins_id from t_wf_task "+_withNoLock+" where "+getInsFilterWhere("")+" and  (task_state='5'  or task_state='6' ) and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("content"),"' '")+"<>'重新分派' )) )");
			strsql2.append(" or ( ");
			
			strsql2.append(" ("+getInsFilterWhere("T.")+" and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"),"' '")+"<>'重新分派'    )"); 
			strsql2.append(" and   U.ins_id not in ( ");
			strsql2.append("  select ins_id from t_wf_task "+_withNoLock+" where  ( task_topic not like '%共0人%' and  task_topic not like '%共0条%'  ) and (task_type='2' )  and  (task_state='5'  or task_state='6' )  and flag=1 ");
			strsql2.append("   and   ins_id in (select ins_id from t_wf_task "+_withNoLock+" where  "+getInsFilterWhere("")+"   and  (task_state='5'  or task_state='6' ) and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("content"),"' '")+"<>'重新分派'  ) ) ");
			strsql2.append(" )");
			strsql2.append(")");
			if(templateId!=null&&!"-1".equals(templateId)&&templateId.length()>0)
			{
				strsql.append(" and tt.tabid="+templateId);
			}
			
			//增加时间查询,姓名
			
			if("3".equals(sp_flag)){
				if("1".equals(query_type))//最近多少天
				{
					if(validateNum(days)){
					String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
					strsql.append(" and U.start_date>=");
					strsql.append(strexpr);	
					strsql2.append(" and U.start_date>=");
					strsql2.append(strexpr);	
					}
								
				}else{
					if("2".equals(query_type)){
					strsql.append(" and ( 1=1 ");
					strsql2.append(" and ( 1=1 ");
					if(validateDate(start_date)){
						strsql.append(PubFunc.getDateSql(">=","U.start_date",start_date));
						strsql2.append(PubFunc.getDateSql(">=","U.start_date",start_date));
					}
					if(validateDate(end_date)){
						strsql.append(PubFunc.getDateSql("<=","U.start_date",end_date));
						strsql2.append(PubFunc.getDateSql("<=","U.start_date",end_date));
					}
					strsql.append(" )");
					strsql2.append(" )");
					}
				}
			}
			
			
			 
			
			boolean isSource=false;
			if(this.userView.isHavetemplateid(IResourceConstant.RSBD)||this.userView.isHavetemplateid(IResourceConstant.ORG_BD)||this.userView.isHavetemplateid(IResourceConstant.POS_BD)||this.userView.isHavetemplateid(IResourceConstant.GZBD)||this.userView.isHavetemplateid(IResourceConstant.INS_BD)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_FG)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_GX)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_JCG))
				isSource=true;
			if(this.userView.isSuper_admin())
				isSource=true;
			if(!isSource)
			{	
				strsql.append(" and 1=2 ");
				strsql2.append(" and 1=2 ");
			}
			
			this.getFormHM().put("bs_flag",bs_flag);
			ArrayList bs_flag_list=new ArrayList();
			bs_flag_list.add(new CommonData("1",ResourceFactory.getProperty("tab.label.bptask")));
		//	bs_flag_list.add(new CommonData("2",ResourceFactory.getProperty("tab.label.jqtask")));
			bs_flag_list.add(new CommonData("3",ResourceFactory.getProperty("tab.label.bbtask")));
			this.getFormHM().put("bs_flag_list",bs_flag_list);
			
			//获得各实例下当前审批人sql
			int index=strsql.toString().indexOf("from");
			String subsql=strsql.toString().substring(index+4, strsql.toString().length());  //为了得到第二个from的位置，先将第一个from的位置之前的部分去掉
			index=subsql.indexOf("from");  //得到第二个from的位置，即可根据index截取要查询的sql liuzy 20151112
			String sql="";
			if(index!=-1)
				sql=" select ins_id,actorname from t_wf_task "+_withNoLock+" where ins_id in (select  U.ins_id "+subsql.substring(index)+") and task_type='2' and  "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='3' order by ins_id";
			
			strsql.append(" order by T.end_date DESC");
			if("3".equals(sp_flag))
			{
				ArrayList templateList= getTemplateList(strsql2.toString());
				this.getFormHM().put("templateList",templateList);
			}
			ArrayList dataList=new ArrayList();
			this.frowset=dao.search(strsql.toString());
			
			//获得各实例下当前审批人
			HashMap ins_CurrentSpInfo=new HashMap(); // 
			if(sql.length()>0)
			{ 
				RowSet rowSet=dao.search(sql);
				int ins_id=0;
				String actorname="";
				while(rowSet.next())
				{
					if(ins_id==0)
						ins_id=rowSet.getInt("ins_id");
					if(ins_id==rowSet.getInt("ins_id"))
						actorname+=","+rowSet.getString("actorname");
					else
					{
						ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1));
						ins_id=rowSet.getInt("ins_id");
						actorname=","+rowSet.getString("actorname");
					} 
				}
				if(ins_id!=0&&actorname.length()>0)
					ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1));
			}
			
			
			LazyDynaBean abean=null;
			HashMap operationTypeMap=new HashMap();
			HashMap tableNameMap=new HashMap();
			HashSet tabidSet=new HashSet();
			while(this.frowset.next())
			{
				abean=new LazyDynaBean();
				String tabid=this.frowset.getString("tabid");
				//tabidSet.add(tabid);
				String task_id=this.frowset.getString("task_id");
				String ins_id=this.frowset.getString("ins_id");
				String flag=this.frowset.getString("flag")!=null?this.frowset.getString("flag"):"";
				String task_topic=this.frowset.getString("task_topic");
				String _static=this.frowset.getString(static_);
				if("".equals(flag))
				{
					String operationType="";
					if(operationTypeMap.get(tabid)==null)
					{
						operationType=findOperationType(tabid);
						operationTypeMap.put(tabid, operationType);
					}
					else
						operationType=(String)operationTypeMap.get(tabid);
					
					
					String tabName="";
					if(tableNameMap.get(tabid)==null)
					{
						tabName=findTabName(tabid);
						tableNameMap.put(tabid,tabName);
					}
					else
					{
						tabName=(String)tableNameMap.get(tabid);
					}
					String topic="";
		
					if(userView.getStatus()!=4)
						topic=getTopic(task_id,"templet_"+tabid,Integer.parseInt(operationType),tabid,type);
					else
					{
						if(_static!=null&&("10".equals(_static)|| "11".equals(_static)))
						{
							topic=getTopic(task_id,"templet_"+tabid,Integer.parseInt(operationType),tabid,_static);
						} 
						else
							topic=getTopic(task_id,"templet_"+tabid,Integer.parseInt(operationType),tabid,type);
					}
					
					if(topic.indexOf(",共0")!=-1) //撤销任务主题
					{
						topic=getRecordBusiTopicByState(Integer.parseInt(task_id),3,"templet_"+tabid,dao,Integer.parseInt(operationType), type);
					}
					
					task_topic=tabName+topic;
				}
				if(ins_CurrentSpInfo.get(ins_id)!=null)
					abean.set("sp_info",(String)ins_CurrentSpInfo.get(ins_id));
				else
					abean.set("sp_info","");
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
				//abean.set("task_id",this.frowset.getString("task_id"));
				if("5".equals(this.frowset.getString("insfinished"))){
					abean.set("flag", "结束");
				}else if("6".equals(this.frowset.getString("insfinished"))){//lis 20160419
					abean.set("flag", "终止");
				}else{
					abean.set("flag", "等待");
				}
				
				dataList.add(abean);
			}
           /* if(sp_flag.equals("3"))//此处会导致项目bug:22598 
            {
                ArrayList templateList=getTemplateList2(tabidSet);
                this.getFormHM().put("templateList",templateList);
            }*/
			if(type==null)
				type="";			
			this.getFormHM().put("type",type);
			this.getFormHM().put("taskList",dataList);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

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
	 * @return
	 */
	public String getRecordBusiTopicByState(int task_id,int state,String tabname,ContentDAO dao,int operationtype,String type)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		try
		{ 
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				_withNoLock=" WITH(NOLOCK) ";
			StringBuffer strsql=new StringBuffer(); 
			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}
			
			if(type!=null&&("10".equals(type)|| "11".equals(type)))
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
			strsql.append(tabname +_withNoLock );
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
			strsql.append(tabname +_withNoLock );
			strsql.append(strWhere.toString());
		
			rset=dao.search(strsql.toString());
			if(rset.next())
				nmax=rset.getInt("nmax");
			//if(nmax!=i)
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			//stopic.append(ResourceFactory.getProperty("hmuster.label.total"));			
			stopic.append(nmax);
			 
			if(type!=null&&("10".equals(type)|| "11".equals(type)))
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
	
	
	
	public String getTopic(String task_id,String tabname,int operationtype,String tab_id,String type)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				_withNoLock=" WITH(NOLOCK) ";
			ContentDAO dao=new ContentDAO(this.frameconn);
			
			String a0101="a0101_1";
	/*		DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
			dbmodel.reloadTableModel(tabname.toLowerCase());	
			*/
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
				a0101="a0101_2";
			}
			
			if(type!=null&&("10".equals(type)|| "11".equals(type)))
			{
				a0101="codeitemdesc_1";
				if(operationtype==5)
					a0101="codeitemdesc_2";
			}
			String sql="";
			String seqnum="1";
			RowSet rset=null;
			rset=dao.search("select seqnum from "+tabname+" "+_withNoLock+" where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )");
			if(rset.next())
				seqnum=rset.getString(1)!=null?rset.getString(1):"";
			
			if(seqnum.length()>0)
			{
				sql=" select "+a0101+" from "+tabname +_withNoLock+",t_wf_task_objlink two "+_withNoLock+" where "+tabname+".seqnum=two.seqnum and "+tabname+".ins_id=two.ins_id "
						  +" and two.task_id="+task_id+" and two.tab_id="+tab_id +" and ( "+Sql_switcher.isnull("two.state","0")+"<>3 )  and ("+Sql_switcher.isnull("two.special_node","0")+"=0  or ( "+Sql_switcher.isnull("two.special_node","0")+"=1 and (lower(two.username)='"+this.userView.getUserName().toLowerCase()+"' or lower(two.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
			}
			else
			{
				sql=" select "+a0101+" from "+tabname+""+_withNoLock+"  where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )";
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
					sql=" select count(*) from "+tabname +_withNoLock+"  where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )";  
				rset=dao.search(sql);
					if(rset.next())
						nmax=rset.getInt(1);
			}
			else
				nmax=i;
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));		
			stopic.append(nmax);
			if(type!=null&&("10".equals(type)|| "11".equals(type)))
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
	public ArrayList getTemplateList(String sql)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search(sql);
			CommonData dt=new CommonData("-1","全部");
			list.add(dt);
			while(rowSet.next())
			{
				list.add(new CommonData(rowSet.getString("tabid"),rowSet.getString("name")));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	   
    public ArrayList getTemplateList2(HashSet tabidSet)
    {
        ArrayList list=new ArrayList();
        HashMap map2 = new HashMap();
        try
        {
            ContentDAO dao=new ContentDAO(this.getFrameconn());
            CommonData dt=new CommonData("-1","全部");
            list.add(dt);
            StringBuffer sql2=new StringBuffer();
            StringBuffer tabidStr=new StringBuffer("");
            for(Iterator t=tabidSet.iterator();t.hasNext();)
            {
                tabidStr.append(","+(String)t.next());
            }
            
            sql2.append("select   tabid,  name  from   Template_table   ");
            if(tabidStr.length()>0)
            	sql2.append(" where tabid in ("+tabidStr.substring(1)+") ");
            else 
            	sql2.append(" where tabid in (0) ");
            RowSet rowSet=dao.search(sql2.toString());
            while(rowSet.next())
            {
                 
                list.add(new CommonData(rowSet.getString("tabid"),rowSet.getString("name")));
                 
            } 
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return list;
    }
    
	
	
	/**
	 * 校验日期是否正确
	 * @return
	 */
	private boolean validateDate(String datestr)
	{
		boolean bflag=true;
		if(datestr==null|| "".equals(datestr))
			return false;
		try
		{
			Date date=DateStyle.parseDate(datestr);
			if(date==null)
				bflag=false;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}
	/**
	 * 校验天数
	 * @return
	 */
	private boolean validateNum(String date)
	{
		boolean bflag=true;
		if(date==null|| "".equals(date))
			return false;
		try
		{
			String  valide="0123456789.";
			if(date.startsWith("."))
				bflag=false;
			for(int i=0;i<date.length();i++){
				if(valide.indexOf(date.charAt(i))==-1){
					bflag = false;
				}
			}
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}
}
