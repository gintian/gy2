package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.ShowExcel;
import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 
 *<p>Title:ShowExcelTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 26, 2008:2:25:49 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class ShowExcelTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		String sp_flag = (String)hm.get("sp_flag");//申请状态
		String query_type=(String)this.getFormHM().get("query_type");
		String query_method=(String)this.getFormHM().get("query_method");
		String start_date=(String)this.getFormHM().get("start_date");
		String fromflag=(String)this.getFormHM().get("fromflag"); 
		String end_date=(String)this.getFormHM().get("end_date");
		String days=(String)this.getFormHM().get("days");
		if(query_method==null || query_method.trim().length()==0)
			query_method="1";
		String type = (String)this.getFormHM().get("type");
		//界面上的记录都是加密的所以, 记录要解密回来,否则导出会出错
		String _records=(String)this.getFormHM().get("_records");
		if(_records!=null&&_records.trim().length()>0){
		    String recordArray[] =_records.split(",");
		    _records = "";
		    for(int i=0;i<recordArray.length;i++){
		        String record = recordArray[i];
		        record = PubFunc.decrypt(record);
		        _records = _records+record+",";
		    }
		    if(_records.trim().length()>0){
		        _records = _records.substring(0, _records.length()-1);
		    }
		}
		StringBuffer strsql=new StringBuffer();

		try
		{
			String static_="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				static_="static_o";
			}
			/**查询任务实例*/
			strsql.append("select U.ins_id,T.task_topic name,U.tabid,U.actorname fullname,a0101, T.task_state finished ,U.start_date,T.end_date,T.actorname,T.actor_type,T.task_id,(select o.codeitemdesc from organization o where o.codeitemid=U.b0110) unitname from t_wf_task T,t_wf_instance U,template_table tt ");//bug 36658 下载的excel中没有发起单位列
			strsql.append(" where T.ins_id=U.ins_id and  task_topic not like '%共0人%' and  task_topic not like '%共0条%' ");
			//strsql.append(" and T.task_type='2' and ((finished='2' and T.task_state='3') or ((T.task_type='2' or T.task_type='9') and T.task_state='5' and T.flag=1))");//task_state='5' and state<>'07';
			if(type!=null&&("10".equals(type)|| "11".equals(type)))
			{
				strsql.append(" and   U.tabid=tt.tabid and tt."+static_+"="+type+"   ");
			}
			else
			{
				strsql.append("  and  U.tabid=tt.tabid and tt."+static_+"!=10 and tt."+static_+"!=11  ");
			}
			
			
			TemplateTableParamBo tp=new TemplateTableParamBo(this.frameconn); 
	    	String tabids=tp.getAllDefineKqTabs(0); 
			if(tabids.length()==0)
				tabids+=",-1000"; 
			if(type!=null&& "23".equals(type)){ //考勤业务办理
				strsql.append(" and tt.tabid in ("+tabids.substring(1)+")" );
				 
			}
			else
			{
				if(fromflag==null||!"6".equalsIgnoreCase(fromflag))
				{
					strsql.append(" and tt.tabid not in ("+tabids.substring(1)+")" ); 
				}
			}
			
			strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  ");
			
			if("1".equals(query_method))//运行中。。。,并且当前任务处理等待状态中,记录过滤有些问题
				strsql.append(" and T.task_type='2' and finished='2' and ( task_state='3'  or task_state='6' )");
			else if("2".equals(query_method))//结束
				strsql.append(" and ( T.task_type='9' and  T.task_state='5' )");//lis 20160421
			//strsql.append(" and ((T.task_type='2' or T.task_type='9') and T.task_state='5' )");//Finished  //task_type='2' and task_state='5' and state<>'07'
			else if("3".equals(query_method)) // 终止lis 20160421
			{
				strsql.append(" and ( T.task_type='9' and  T.task_state='4' )");//Finished T.task_type='2' and T.flag=1//task_type='2' and task_state='5' and state<>'07'
			}else //全部
				strsql.append(" and T.task_type='2' and ((finished='2' and T.task_state='3') or ((T.task_type='2' or T.task_type='9') and T.task_state='5' ))");//task_state='5' and state<>'07';
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
			else
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
				String templateId=(String)this.getFormHM().get("templateId");
				if(templateId!=null&&!"-1".equals(templateId)&&templateId.length()>0)
				{
					strsql.append(" and tt.tabid="+templateId);
				}
				String titlename=(String)this.getFormHM().get("titlename");
				this.getFormHM().remove("titlename");
				if(titlename!=null&&titlename.length()>0)
				{
					titlename=SafeCode.decode(titlename);
					strsql.append(" and task_topic like '%"+titlename+"%'");
				}
			}
			//增加时间查询
			if("1".equals(query_type))//最近多少天
			{
				if(validateNum(days)){
				String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
				strsql.append(" and U.start_date>=");
				strsql.append(strexpr);		
				}
							
			}else{
			if("2".equals(query_type)){
			strsql.append(" and ( 1=1 ");
			if(validateDate(start_date))
				strsql.append(PubFunc.getDateSql(">=","U.start_date",start_date));
			if(validateDate(end_date))
				strsql.append(PubFunc.getDateSql("<=","U.start_date",end_date));
			strsql.append(" )");
			
			}
			}
			
			if(_records!=null&&_records.trim().length()>0)
			{
				strsql.append(" and T.task_id in ("+_records.trim().substring(1)+")");
			}
			
			if("2".equals(query_method)|| "3".equals(sp_flag))//结束
				strsql.append(" order by T.end_date DESC");
			else if("1".equals(query_method))  //运行中
				strsql.append(" order by U.start_date DESC");
			
			//this.getFormHM().put("strsql",strsql.toString());
			//this.getFormHM().put("columns","tabid,ins_id,name,a0101,finished,start_date,end_date,actorname,task_id,fullname");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		ShowExcel show= new ShowExcel(this.getFrameconn());
		ArrayList column = new ArrayList();
		column.add("流程名称");
		column.add("申请人");
		column.add("发起单位");//bug 36658 下载的excel中没有发起单位列
		column.add("申请发起时间");
		column.add("审批结束时间");
		column.add("当前审批人");
		column.add("任务状态");
		ArrayList Infolist = getInfolist(strsql.toString(),query_method);
		ArrayList columnlist = new ArrayList();
		columnlist.add("name");
		columnlist.add("fullname");
		columnlist.add("unitname");//bug 36658 下载的excel中没有发起单位列
		columnlist.add("start_date");
		columnlist.add("end_date");
		columnlist.add("actorname");
		columnlist.add("finished");
		String excelfile=show.creatExcel(column,Infolist,columnlist,this.userView.getUserName());
		//SafeCode.encode(PubFunc.encrypt(outName))
//		this.getFormHM().put("excelfile",SafeCode.encode(PubFunc.encrypt(excelfile)));
		//20/3/18 xus vfs改造
		this.getFormHM().put("excelfile",PubFunc.encrypt(excelfile));

	}
	private ArrayList getInfolist(String sql,String query_method){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			String codeitemsql = "select * from codeitem where codesetid = '38'";
			RowSet rs = dao.search(codeitemsql);
			HashMap map = new HashMap();
			while(rs.next()){
				map.put(rs.getString("codeitemid"),isNull(rs.getString("codeitemdesc")));
			}
			rs = dao.search(sql);
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("name",isNull(rs.getString("name")));
				bean.set("fullname",isNull(rs.getString("fullname")));
				bean.set("unitname",isNull(rs.getString("unitname")));//bug 36658 下载的excel中没有发起单位列
				bean.set("start_date",PubFunc.FormatDate(rs.getTimestamp("start_date"),"yyyy.MM.dd HH:mm"));
				bean.set("end_date",PubFunc.FormatDate(rs.getTimestamp("end_date"),"yyyy.MM.dd HH:mm"));
				
				if("2".equals(query_method))
				{
					String actor_type= rs.getString("actor_type");
					if ("2".equals(actor_type)||"3".equals(actor_type)){//角色 机构才加
						bean.set("actorname",isNull(rs.getString("actorname"))+"-"+isNull(rs.getString("a0101")));
					}
					else {
						bean.set("actorname",isNull(rs.getString("actorname")));
					}
				}
				else
				{
					bean.set("actorname",isNull(rs.getString("actorname")));
				}

				String finished = (String)map.get(rs.getString("finished"));	
			//	System.out.println(finished);
				if(finished==null || finished.trim().length()<=0)
					finished = "";
				bean.set("finished",finished);
				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public String isNull(String str)
    {

    	if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    return "";
		else
		    return str;

    }
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
			strwhere.append( " (U.actorid in ('");
			strwhere.append(userid.toUpperCase());
			strwhere.append("','");
			strwhere.append(this.userView.getUserName());
			strwhere.append("'))");
		}
		else if("3".equalsIgnoreCase(sp_flag))//已批，列出自己参与的流程实例中的任务
		{
			strwhere.append(" U.ins_id in (select ins_id from t_wf_task where  ");
			strwhere.append( " actorid in ('");
			strwhere.append(userid.toUpperCase());
			strwhere.append("','");
			strwhere.append(this.userView.getUserName());
			strwhere.append("'))");
		}
		return strwhere.toString();
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
