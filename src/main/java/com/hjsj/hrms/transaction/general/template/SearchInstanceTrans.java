/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:SearchInstanceTrans</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 2, 200611:15:34 AM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchInstanceTrans extends IBusiness {

	private String getInsFilterWhere(String sp_flag)
	{
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		
		/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
		if(userid==null||userid.length()==0)
			userid="-1";
		if("1".equalsIgnoreCase(sp_flag))
		{
		/**人员列表*/
			strwhere.append( " ( upper(U.actorid) in ('");
			strwhere.append(userid.toUpperCase());
			strwhere.append("','");
			strwhere.append(this.userView.getUserName().toUpperCase());
			strwhere.append("'))");
		}
		else if("3".equalsIgnoreCase(sp_flag))//已批，列出自己参与的流程实例中的任务
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				_withNoLock=" WITH(NOLOCK) ";
			strwhere.append(" U.ins_id in (select ins_id from t_wf_task "+_withNoLock+" where  ");
			strwhere.append( " upper(actorid) in ('");
			strwhere.append(userid.toUpperCase());
			strwhere.append("','");
			strwhere.append(this.userView.getUserName().toUpperCase());
			strwhere.append("') ");
			
			if(this.userView.getRolelist().size()>0)
			{
				strwhere.append(" or  ( actor_type=2 and  upper(actorid) in ( ");
				String str="";
				for(int i=0;i<this.userView.getRolelist().size();i++)
				{
					str+=",'"+(String)this.userView.getRolelist().get(i)+"'";
				}
				strwhere.append(str.substring(1));
				strwhere.append(" ) )");
			}
			strwhere.append(")");
			
		}
		return strwhere.toString();
	}


	
	public void execute() throws GeneralException {
		String sp_flag=(String)this.getFormHM().get("sp_flag");
		String query_type=(String)this.getFormHM().get("query_type");
		String query_method=(String)this.getFormHM().get("query_method");
		String templateId=(String)this.getFormHM().get("templateId");
		String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");
		String days=(String)this.getFormHM().get("days");
		if(query_method==null || query_method.trim().length()==0)
			query_method="1";
		if(query_type==null || query_type.trim().length()==0)
			query_type="1";
		StringBuffer strsql=new StringBuffer();
		StringBuffer strsql2=new StringBuffer();  //模板查询sql
		HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		String fromflag=(String)map.get("fromflag"); 
		String spn = (String)map.get("name");
		String titlename = "";  
		String type=(String)map.get("type");  //1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整  23考勤业务  24：非考勤业务(业务申请不包含考勤信息)
		if(spn!=null&&spn.length()!=0){
			titlename = (String)this.getFormHM().get("titlename");
		    titlename=titlename.trim();
		}
		if(titlename==null) {
			titlename="";
		}
		String _static="static";
		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			_static="static_o";
		}
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				_withNoLock=" WITH(NOLOCK) ";
			strsql2.append("select distinct U.tabid,template_table.name  from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table "+_withNoLock);
			strsql2.append("  where T.ins_id=U.ins_id  and template_table.tabid=U.tabid ");
			if(!("2".equals(query_method) || "3".equals(query_method))) //结束状态  兼容旧程序  2014-04-01 dengcan
				strsql2.append(" and  task_topic not like '%共0人%' and  task_topic not like '%共0条%' ");
			/**查询任务实例*/
			
			
			String format_str="yyyy-MM-dd HH:mm";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				format_str="yyyy-MM-dd hh24:mi";
		//	strsql.append("select U.ins_id,T.task_topic name,U.tabid,U.actorname fullname,a0101, task_state finished ,U.start_date,T.end_date,T.actorname,T.task_id from t_wf_task T,t_wf_instance U ");
			
			//结束状态  兼容旧程序  2014-04-01 dengcan   case when T.task_topic like '%共0%' then U.name  else T.task_topic end name
			strsql.append("select U.ins_id,case when T.task_topic like '%共0%' then U.name  else T.task_topic end name,U.tabid,U.actorname fullname,(select o.codeitemdesc from organization o "+_withNoLock+" where o.codeitemid=U.b0110) unitname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" as ins_start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" as ins_end_date,T.actor_type,T.actorname,T.task_id from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table tt "+_withNoLock);
			strsql.append(" where T.ins_id=U.ins_id ");
			
			if(!("2".equals(query_method) || "3".equals(query_method))) //结束状态  兼容旧程序  2014-04-01 dengcan
			{
				strsql.append(" and  task_topic not like '%共0人%' and  task_topic not like '%共0条%' ");
			}
			if(titlename.length()>0)
			{
				strsql.append(" and task_topic like '%"+titlename+"%'");
			//	strsql2.append(" and task_topic like '%"+titlename+"%'");
			}
			
			if(type!=null&&type.charAt(0)=='t') // 例：type=t48  ，只能显示某模板的任务（个性化）
			{
				
			}
			else if(type!=null&&("10".equals(type)|| "11".equals(type)))
			{
				strsql.append(" and   U.tabid=tt.tabid and tt."+_static+"="+type+"   ");
				strsql2.append(" and   U.tabid=template_table.tabid and template_table."+_static+"="+type+"  ");
			}
			else
			{
				strsql.append("  and  U.tabid=tt.tabid and tt."+_static+"!=10 and tt."+_static+"!=11  ");
				strsql2.append("  and  U.tabid=template_table.tabid and template_table."+_static+"!=10 and template_table."+_static+"!=11 ");
			}
			
			TemplateTableParamBo tp=new TemplateTableParamBo(this.frameconn); 
	    	String tabids=tp.getAllDefineKqTabs(0); 
			if(tabids.length()==0)
				tabids+=",-1000"; 
			if(type!=null&&type.charAt(0)=='t') // 例：type=t48  ，只能显示某模板的任务（个性化）
			{
				strsql.append(" and tt.tabid in ("+type.substring(1)+")" );
				strsql2.append(" and    template_table.tabid in ("+type.substring(1)+")" );
			}
			else if(type!=null&& "23".equals(type)){ //考勤业务办理
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
			
			
			
			if("1".equals(query_method)) //&&!sp_flag.equals("1"))//运行中。。。,并且当前任务处理等待状态中,记录过滤有些问题
			{
				strsql.append(" and task_type='2' and finished='2' and ( task_state='3'  or task_state='6' )");//=3等待状态 =6暂停
				strsql2.append(" and task_type='2' and finished='2' and ( task_state='3'  or task_state='6' )");
			} 
			else if("2".equals(query_method)) //&&!sp_flag.equals("1"))//结束
			{
				strsql.append(" and ( T.task_type='9' and  T.task_state='5' )");//Finished T.task_type='2' and T.flag=1//task_type='2' and task_state='5' and state<>'07'
				strsql2.append(" and (T.task_type='9' and T.task_state='5' )");
			}else if("3".equals(query_method)) //&&!sp_flag.equals("1"))//终止
			{
				strsql.append(" and ( T.task_type='9' and  T.task_state='4' )");//Finished T.task_type='2' and T.flag=1//task_type='2' and task_state='5' and state<>'07'
				strsql2.append(" and (T.task_type='9' and T.task_state='4' )");
			}
	 
			if("1".equals(sp_flag)) //我的申请
			{
				strsql.append(" and (");
				strsql.append(getInsFilterWhere(sp_flag));
				strsql.append(")");	
			}
			else if("3".equals(sp_flag))//已批任务,仅列出自己参与任务
			{
				strsql.append(" and (");
				strsql.append(getInsFilterWhere(sp_flag));
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
						strsql2.append(" and 1=2");
					}
					else
					{
						strsql.append(" and tt.tabid in (");
						strsql.append(tmp);
						strsql.append(")");
						
						strsql2.append(" and template_table.tabid in (");
						strsql2.append(tmp);
						strsql2.append(")");
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
                            
                            strsql2.append(" and ");
                            strsql2.append(strB0110Where);
                                         
                        }                      	
	                }	                
	            }
				
				if(templateId!=null&&!"-1".equals(templateId)&&templateId.length()>0)
				{
					strsql.append(" and tt.tabid="+templateId);
				}
				
			}
			
			//1：审批任务 2：加签任务 3：报备任务  4：空任务
			strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  ");
			strsql2.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1' ");
			
			//增加时间查询
			if("1".equals(query_type)&&(sp_flag==null||!"1".equals(sp_flag)))//最近多少天
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
					
					String temp_str=" and ( 1=1 ";
					if(validateDate(start_date))
					{
						temp_str+=PubFunc.getDateSql(">=","U.start_date",start_date);
					}
					if(validateDate(end_date))
					{
						temp_str+=PubFunc.getDateSql("<=","U.start_date",end_date);
					}
					temp_str+=" )";
					strsql.append(temp_str);
					strsql2.append(temp_str);
				}
			}
			String order_sql="";
			if("1".equals(query_method))  //运行中
				order_sql=" order by U.start_date DESC";
			
			if("2".equals(query_method) || "3".equals(query_method) || "3".equals(sp_flag))//结束
				order_sql=" order by T.end_date DESC";
			
			if("2".equals(sp_flag))
			{
				ArrayList templateList= getTemplateList(strsql2.toString());
				this.getFormHM().put("templateList",templateList);
			}
			
			if(map.get("init")!=null&& "1".equals((String)map.get("init")))
			{
				//this.getFormHM().put("titlename","");    // 流程名称查询刘，出来多条后，点打印预演，返回，第一次是对的，第二个人再点返回就不对了,因为返回时重新进入页面将这个值清空了，故注释掉，liuzy 20150714
				map.remove("init");
			}
			//else
			//	this.getFormHM().put("titlename",titlename);
			this.getFormHM().put("order_sql",order_sql);
			if(type==null)
				type="";
			this.getFormHM().put("type",type);
			this.getFormHM().put("strsql",strsql.toString());
			this.getFormHM().put("columns","tabid,ins_id,name,a0101,finished,ins_start_date,ins_end_date,actor_type,actor_type,actorname,task_id,fullname,unitname");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
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
		
		//String tmp=Arrays.toString(bdarr);
		String tmp=StringUtils.join(bdarr, ',');
		//tmp=tmp.substring(1,tmp.length()-1);
		tmp = tmp.replace("r", "");
		tmp = tmp.replace("R", "");
		tmp = tmp.replace(" ", "");
		tmp = tmp.replace(",,", ",");
		return tmp;
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
