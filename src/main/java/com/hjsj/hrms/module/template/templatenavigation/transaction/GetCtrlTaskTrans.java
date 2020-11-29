package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.module.template.templatenavigation.businessobject.TemplateNavigationBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
/**
 * 
 * <p>Title:GetCtrlTaskTrans.java</p>
 * <p>Description>:任务监控（我的申请）页面数据获取</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 25, 2016 2:32:30 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class GetCtrlTaskTrans extends IBusiness {

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
		String days=(String)this.getFormHM().get("days");//最近几天
		String start_date=(String)this.getFormHM().get("start_date");//开始时间
		String end_date=(String)this.getFormHM().get("end_date");//结束时间
		String query_type = (String) this.getFormHM().get("query_type");//按日期or按时间段 1 or other
		String sp_flag=(String)this.getFormHM().get("sp_flag");// 1:我的申请  2:任务监控
		String fromflag=(String)this.getFormHM().get("fromflag"); //deskTop 从首页进入我的申请
		String flag=(String) this.getFormHM().get("flag");//0:首次进入 1：查询进入
		//liuyz bug26508 
		String titlename="";
		if("1".equals(flag))
		{
			titlename = (String)this.getFormHM().get("titlename");
			if(titlename==null)
				titlename="";
		    titlename=titlename.trim();
		}
		String query_method=(String) this.getFormHM().get("query_method");
		if(query_method==null || query_method.trim().length()==0)
			query_method="1";
		if("0".equals(query_method))
			query_method="";
		if(query_type==null || query_type.trim().length()==0)
			query_type="1";
		StringBuffer strsql=new StringBuffer();
		  
		
		TemplateNavigationBo bo = new TemplateNavigationBo(this.frameconn,this.userView);
		bo.setModule_id(module_id);
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER 
				_withNoLock=" WITH(NOLOCK) ";
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String format_str="yyyy-MM-dd HH:mm:ss";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				format_str="yyyy-MM-dd hh24:mi:ss";
			String _static="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				_static="static_o";
			}
			
			//结束状态  兼容旧程序  2014-04-01 dengcan   case when T.task_topic like '%共0%' then U.name  else T.task_topic end name
			strsql.append("select U.ins_id,case when T.task_topic like '%共0%' then U.name  else T.task_topic end name,U.tabid,U.actorname fullname, U.b0110  unitname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" as ins_start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" as ins_end_date,T.actor_type,T.actorname,T.task_id ");
			if("1".equals(sp_flag)&&("1".equals(query_method)||"".equals(query_method)))
				strsql.append(",U.actor_type actortype,case when (select count(1) from t_wf_task t1  where  t1.task_type='2' and T1.ins_id=u.ins_id and t1.bread=1)>0 then 0  else 1 end  recallflag ");
			strsql.append("from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table tt "+_withNoLock+"");
			strsql.append(" where T.ins_id=U.ins_id ");
			if(!("2".equals(query_method) || "3".equals(query_method))) //结束状态  兼容旧程序  2014-04-01 dengcan
			{
				strsql.append(" and  task_topic not like '%共0人%' and  task_topic not like '%共0条%' ");
			}
			if(titlename.length()>0)
			{
				strsql.append(" and task_topic like '%"+titlename+"%'");
			}
		 
			if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
				
				if("7".equals(module_id))
					strsql.append(" and   U.tabid=tt.tabid and tt."+_static+"=10   ");
				else if("8".equals(module_id))
					strsql.append(" and   U.tabid=tt.tabid and tt."+_static+"=11   ");
				
			} 
			else
			{
				strsql.append("  and  U.tabid=tt.tabid and tt."+_static+"!=10 and tt."+_static+"!=11  ");
			}
			
			String kq_tabids="";
			String zz_tabids = bo.getBusinessTabid("12");
			if(module_id==null||!"9".equals(module_id)) //业务申请的待办无需过滤考勤模板
			{
				//TemplateTableParamBo tp=new TemplateTableParamBo(this.frameconn); 
		        //kq_tabids=tp.getAllDefineKqTabs(0);
		        kq_tabids=bo.getKqTabIds(module_id);//liuyz 考勤支持业务模版
			}
			
			if(module_id!=null&& "10".equals(module_id)){ //考勤业务办理
				if(kq_tabids.length()==0)
					strsql.append(" and 1=2 ");
				else
					strsql.append("   and tt.tabid in ("+kq_tabids.substring(1)+")  ");
			}
			else if(module_id!=null&&"12".equals(module_id)) {//证照管理
				if(zz_tabids.length()==0){
					strsql.append(" and 1=2 ");
				}
				else{
					strsql.append("   and tt.tabid in ("+zz_tabids+")  ");
				}
			}
			else if (kq_tabids.length()>0||zz_tabids.length()>0)
			{
				if(!"deskTop".equalsIgnoreCase(fromflag)) {//从首页进入查看我的申请 不按模块过滤模板
					if(kq_tabids.length()>0)
						strsql.append(" and tt.tabid not in ("+kq_tabids.substring(1)+")" );
					if(zz_tabids.length()>0)
						strsql.append(" and tt.tabid not in ("+zz_tabids+")" );
				}
			}
			
			if("1".equals(query_method))
			{
				strsql.append(" and task_type='2' and finished='2' and ( task_state='3'  or task_state='6' )");//=3等待状态 =6暂停
			} 
			else if("2".equals(query_method))
			{
				strsql.append(" and ( T.task_type='9' and  T.task_state='5' )");//Finished T.task_type='2' and T.flag=1//task_type='2' and task_state='5' and state<>'07'
			}else if("3".equals(query_method)) //&&!sp_flag.equals("1"))//终止
			{
				strsql.append(" and ( T.task_type='9' and  T.task_state='4' )");//Finished T.task_type='2' and T.flag=1//task_type='2' and task_state='5' and state<>'07'
			}else if(StringUtils.isEmpty(query_method)) {
				strsql.append(" and ( task_type='2' and finished='2' and ( task_state='3'  or task_state='6' ) "
						+ " or (T.task_type='9' and  T.task_state='5') "
						+ " or ( T.task_type='9' and  T.task_state='4' ) )");
			}
	 
			if("1".equals(sp_flag)) //我的申请
			{
				strsql.append(" and (");
				strsql.append(getInsFilterWhere(module_id));
				strsql.append(")");	
			} 
			else //2 任务监控
			{
				if(!this.userView.isSuper_admin())
				{
					String tmp = getTemplates();
					if(tmp.length()==0)
					{
						strsql.append(" and 1=2");
					}
					else
					{
						strsql.append(" and tt.tabid in (");
						strsql.append(tmp);
						strsql.append(")");
					}
			        String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
			        if((operOrg!=null)&&(!"UN`".equalsIgnoreCase(operOrg)))
	                {
	                    String strB0110Where="";	                                  
	                    if(operOrg.length() >3)
	                    {
	                        String[] temp = operOrg.split("`");
	                        for (int j = 0; j < temp.length; j++) { 
	                             if (temp[j]!=null&&temp[j].length()>0) {
	                                 strB0110Where =strB0110Where+ 
	                                     " or  U.b0110 like '" + temp[j].substring(2)+ "%'";             
	                             }
	                        }	         
	                    }
	                    strB0110Where=strB0110Where +" or "+Sql_switcher.sqlNull("U.b0110", "##")+"='##'";
	                    if(strB0110Where.length()>0){
                            strB0110Where=strB0110Where.substring(3);
                            strB0110Where = "("+strB0110Where+")";
                            strsql.append(" and ");
                            strsql.append(strB0110Where);             
                        }                      	
	                }	                
	            }
				
			}
			
			String strsql2=strsql.toString();
			//2017-05-25 10:35:51 修改 liubq 
			//if(sp_flag.equals("2")) 
			//{
				if(tabid!=null&&!"-1".equals(tabid)&&tabid.length()>0)
				{ 
					if(bo.validateNum(tabid))
						strsql.append(" and tt.tabid="+tabid ); 
				}
			//}
			
			
			//1：审批任务  
			strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  ");
			strsql2+=" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  ";
			//增加时间查询
			if("2".equals(sp_flag))
			{
				if("1".equals(query_type))//最近多少天
				{
					if(bo.validateNum(days)){
						String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
						strsql.append(" and U.start_date>=");
						strsql.append(strexpr);	
						strsql2+=" and U.start_date>="+strexpr;
					}
				}else if("2".equals(query_type)){
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
						StringBuffer tempSql=new StringBuffer(""); 
						tempSql.append(" and ( 1=1 "); 
						if(bo.validateDate(start_date))
						{
							tempSql.append(PubFunc.getDateSql(">=","U.start_date",start_date));
						}
						if(bo.validateDate(end_date))
						{
							tempSql.append(PubFunc.getDateSql("<=","U.start_date",end_date));
						}
						tempSql.append("  ) ");
						strsql.append(tempSql.toString());
						strsql2+=tempSql.toString();
					}
			}
			String order_sql="";
			if("1".equals(query_method)||"".equals(query_method))  //运行中
				order_sql=" order by ins_start_date DESC";
			if("2".equals(query_method) || "3".equals(query_method))//结束
				order_sql=" order by ins_end_date DESC";
			
			HashSet tabidSet = new HashSet();
			ArrayList dataList=new ArrayList();
		 
			int index = 0;
			if("1".equals(sp_flag)&&("1".equals(query_method)||"".equals(query_method)))
				index=strsql2.toString().indexOf("from t_wf_task T");
			else
				index=strsql2.toString().indexOf("from t_wf_task");
			this.frowset=dao.search(" select distinct U.tabid,tt.name   "+strsql2.toString().substring(index));
			while(this.frowset.next())
			{
				String _tabid = this.frowset.getString("tabid");
				tabidSet.add(_tabid);
				
			}
			String str = "ctrltask";
			if("2".equals(sp_flag)){
				str = "ctrltask";
			}else if("1".equals(sp_flag)){
				str = "myapply";
			}
			
			if("2".equals(sp_flag))
	         {
					ArrayList templateList = bo.getTemplateList(tabidSet);
					this.getFormHM().put("templatejson", templateList);
	         }
			 boolean isShowButton=false;
			 //liuyz 删除按钮权限
			 String runningDel = "40004022,3800912,37012,37112,37212,37312,32012,32112,33101101,33001012,270162,324010201,325010201,2311022902,230672902";//运行中删除
			 String endedDel =   "40004027,3800927,37027,37127,37227,37327,32027,32137,33101102,33001027,270167,324010227,325010227,2311022903,230672903";//结束删除
			 String stopDel =   "40004024,3800941,32041,32138,33101106,33001034,270164,324010205,325010205,2311022906,230672906";//已终止删除
			 if(TemplateFuncBo.haveFunctionIds(runningDel,this.userView)&&("1".equals(query_method)|| "".equals(query_method))){
				 isShowButton=true;
			 }
			 if(TemplateFuncBo.haveFunctionIds(endedDel,this.userView)&& "2".equals(query_method)){
				 isShowButton=true;
			 }
			 if(TemplateFuncBo.haveFunctionIds(stopDel,this.userView)&& "3".equals(query_method)){
				 isShowButton=true;
			 }
			 this.getFormHM().put("showDelButton", isShowButton);
			if("1".equals(flag)){//页面模糊查询
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(SafeCode.encode(PubFunc.encrypt(str)));
				tableCache.setTableSql(strsql.toString());
				this.userView.getHm().put("ctrltask", tableCache);
				return;
			} 
			ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
			column = bo.getCtrlColumnsInfo(sp_flag);

			TableConfigBuilder builder = new TableConfigBuilder(SafeCode.encode(PubFunc.encrypt(str)), column, str+"1", userView,this.getFrameconn());
			//builder.setDataList(dataList);
			builder.setDataSql(strsql.toString());
			builder.setOrderBy(order_sql);
			builder.setSelectable(true);
			builder.setColumnFilter(true);
			builder.setPageSize(20);
			if("2".equals(sp_flag)){
				builder.setTableTools(bo.getCtrlTaskButtons());
			}else if("1".equals(sp_flag)){
				builder.setTableTools(bo.getMyapplyButtons());
			}
			String config = builder.createExtTableConfig();
           
			this.getFormHM().put("tableConfig", config.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
	/**
	 * 求权限范围下的模板串
	 * @return
	 */
	private String getTemplates() {
		StringBuffer mb=new StringBuffer();
		String rsbd=this.userView.getResourceString(IResourceConstant.RSBD);
		mb.append(rsbd);
		mb.append(",");
		
		String orgbd=this.userView.getResourceString(IResourceConstant.ORG_BD);
		mb.append(orgbd);
		mb.append(",");	
		String posbd=this.userView.getResourceString(IResourceConstant.POS_BD);
		mb.append(posbd);
		mb.append(",");	
		
		String gzbd=this.userView.getResourceString(IResourceConstant.GZBD);
		mb.append(gzbd);
		mb.append(",");					
		String bybd=this.userView.getResourceString(IResourceConstant.INS_BD);
		mb.append(bybd);
		mb.append(",");	
		String pso=this.userView.getResourceString(IResourceConstant.PSORGANS);
		mb.append(pso);
		mb.append(",");	
		String fg=this.userView.getResourceString(IResourceConstant.PSORGANS_FG);
		mb.append(fg);
		mb.append(",");	
		String gx=this.userView.getResourceString(IResourceConstant.PSORGANS_GX);
		mb.append(gx);
		mb.append(",");	
		String jcg=this.userView.getResourceString(IResourceConstant.PSORGANS_JCG);
		mb.append(jcg);
		mb.append(",");	
		String[] bdarr=StringUtils.split(mb.toString(),",");
		if(bdarr==null || bdarr.length==0)
			return "";
		
		String tmp=StringUtils.join(bdarr, ',');
		tmp = tmp.replace("r", "");
		tmp = tmp.replace("R", "");
		tmp = tmp.replace(" ", "");
		tmp = tmp.replace(",,", ",");
		return tmp;
	}
	
	private String getInsFilterWhere(String module_id)
	{
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
		if(userid==null||userid.length()==0)
			userid="-1";
		 
		/**人员列表*/
		strwhere.append( " ( upper(U.actorid) in ('");
		strwhere.append(userid.toUpperCase());
		if(("9".equals(module_id)&&this.userView.getStatus()==4)||!"9".equals(module_id)) {
			strwhere.append("','");
			strwhere.append(this.userView.getUserName().toUpperCase());
		}
		strwhere.append("'))"); 
    	return strwhere.toString();
	}

}
