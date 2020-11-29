package com.hjsj.hrms.transaction.report.report_state;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 28, 2006:2:06:13 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportStateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
		
			String uc = (String) this.getFormHM().get("unitCode");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if("null".equals(uc))
			{
				this.frowset=dao.search("select unitcode from operuser where username='"+this.userView.getUserName()+"'");
				if(this.frowset.next())
					uc=this.frowset.getString(1);
			}
			
			
			
		    //报表检索状态标识(-2 全部,-1 未填 ,0 正在编辑,1 已上报 ,2 打回 ,3 封存)
			String statu = (String)this.getFormHM().get("reportStateSearchFlag");
			
			StringBuffer sql_str = new StringBuffer();
			StringBuffer where_str = new StringBuffer();
			
			int dbserver = Sql_switcher.searchDbServer();
			//默认为mssql
			String jia = "+";
			if(dbserver == 2){ //oracle
				jia = "||";
			}
			
			sql_str.append("  select t.unitcode ");
			sql_str.append(jia);
			sql_str.append("'/'");
			sql_str.append(jia);
			sql_str.append(" ");
			sql_str.append(Sql_switcher.numberToChar("t.tabid"));
			//sql_str.append("as id ,tt.unitname,tn.name,t.description, ");
			
			sql_str.append(" id ,('('");
			sql_str.append(jia);
			sql_str.append(" tt.unitcode ");
			sql_str.append(jia);	
			sql_str.append("')'");
			sql_str.append(jia);
			sql_str.append(" tt.unitname)  unitname ,( '('" );
			sql_str.append(jia);
			sql_str.append(Sql_switcher.numberToChar("tn.tabid"));
			sql_str.append(jia);
			sql_str.append( " ')'");
			sql_str.append(jia);
			sql_str.append(" tn.name)  name,t.description, ");
			
			
			sql_str.append("case ");
			sql_str.append(Sql_switcher.isnull("t.status","-1"));
			sql_str.append(" when -1 then '"+ResourceFactory.getProperty("edit_report.status.wt")+"'  when 0  then '"+ResourceFactory.getProperty("edit_report.status.zzbj")+"' when 1 then '"+ResourceFactory.getProperty("edit_report.status.ysb")+"' when 2  then '"+ResourceFactory.getProperty("edit_report.status.dh")+"' when 3  then '"+ResourceFactory.getProperty("edit_report.status.fc")+"' end	 as a_status ");
			
			where_str.append("  from treport_ctrl  t inner join tt_organization   tt on t.unitcode = tt.unitcode  ");
			where_str.append(" inner join Tname  tn on t.tabid = tn.TabId ");
			
			//填报单位编码为空--首次进入
			if(uc == null || "".equals(uc)){
				//不是超级管理员
				if(userView.isSuper_admin()){
					//显示顶级填报单位对应状态表信息
					String sql = "select unitcode  from tt_organization where unitcode = parentid order by unitid";
					
					try {
						this.frowset = dao.search(sql.toString());
						if (this.frowset.next()) {
							uc = this.frowset.getString("unitcode");
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
					where_str.append(" WHERE t.unitcode ='" +uc +"'");
	
					if(statu == null || "".equals(statu)|| "-2".equals(statu)){
					}else{
						where_str.append(" and t.status='"+statu+"'");
					}
					//where_str.append(" WHERE t.unitcode in(select unitcode from tt_organization where unitcode = parentid)");
	
				}else{
					TTorganization ttorganization=new TTorganization();
					RecordVo selfVo=ttorganization.getSelfUnit3(userView.getUserName());
					uc = selfVo.getString("unitcode");	
					where_str.append(" WHERE t.unitcode ='" +uc +"'");
					if(statu == null || "".equals(statu)|| "-2".equals(statu)){
					}else{
						where_str.append("  and t.status='"+statu+"'");
					}
					/*
					where_str.append("WHERE (t.unitcode <> '");
					where_str.append(uc);
					where_str.append("' and tt.parentid = '");
					where_str.append(uc);
					where_str.append("')");
				*/
				
				}
			}else{
				where_str.append(" WHERE t.unitcode ='" +uc +"'");
				if(statu == null || "".equals(statu)|| "-2".equals(statu)){
				}else{
					where_str.append(" and t.status='"+statu+"'");
				}
				/*
				where_str.append("WHERE (t.unitcode <> '");
				where_str.append(uc);
				where_str.append("' and tt.parentid = '");
				where_str.append(uc);
				where_str.append("')");
				*/
			}
			String reportTypes="";
			String report="";
			StringBuffer buf=new StringBuffer();
		//dml 
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 
			String sql ="select reporttypes,report from tt_organization where unitcode='"+uc+"'" +ext_sql;
			this.frowset =dao.search(sql);
			if(this.frowset.next()){
				reportTypes=Sql_switcher.readMemo(this.frowset, "reporttypes");
				report=Sql_switcher.readMemo(this.frowset, "report");		
			}
			if(reportTypes!=null&&reportTypes.trim().length()!=0){
				reportTypes=reportTypes.substring(0,reportTypes.length()-1);
				sql="select tabid from tname where tname.tsortid in("+reportTypes+")";
				this.frowset=dao.search(sql);
				while(this.frowset.next()){
					String[] temp=report.split(",");
					String tabid=this.frowset.getString("tabid");
					boolean flag=false;
					for(int i=0;i<temp.length;i++){
						if(temp[i]!=null&&temp[i].trim().length()!=0){
							if(tabid.equalsIgnoreCase(temp[i])){
								flag=true;
								break;
							}
						}
					}
					if(!flag){
						if(userView.isHaveResource(IResourceConstant.REPORT,tabid))
							buf.append(tabid+",");
					}
				}
				buf.setLength(buf.length()-1);
				report=buf.toString();
				report = report.replace(",,", ",");
				if (report.length()>0&&report.charAt(report.length() - 1) == ',') {
					report = report.substring(0, report.length() - 1);
				}
				while(report.indexOf(",,")!=-1){
					report = report.replace(",,", ",");
				}
				if(report.endsWith(",")){
					report = report.substring(0,report.length()-1);
				}
				if(report.startsWith(",")){
					report = report.substring(1,report.length());
				}
				if(report.trim().length()>0)
					where_str.append("  and tn.tabid in ("+report+") ");
			}else{//dml
				report=this.getReport();
				while(report.indexOf(",,")!=-1){
					report = report.replace(",,", ",");
				}
				if(report.endsWith(",")){
					report = report.substring(0,report.length()-1);
				}
				if(report.startsWith(",")){
					report = report.substring(1,report.length());
				}
				if(report.trim().length()>0)
					where_str.append("  and tn.tabid in ("+report+") ");
			}
			//System.out.println(sql_str.toString() + where_str.toString());
		
			this.getFormHM().put("sql_str",sql_str.toString());
			TnameBo tnamebo  = new TnameBo(this.getFrameconn());
			HashMap scopeMap = tnamebo.getScopeMap();
			java.util.Iterator it = scopeMap.entrySet().iterator();
			String tabids = "";
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String keys = (String) entry.getKey();
				tabids+= keys+",";
				
			}
			if(tabids.length()>0)
				tabids=tabids.substring(0,tabids.length()-1);
			if(tabids.length()>0)
				where_str.append(" and tn.tabid not in("+tabids+")");
			this.getFormHM().put("where_str",where_str.toString());
			((HashMap)(this.getFormHM().get("requestPamaHM"))).put("code",uc);
			this.getFormHM().put("statu",statu);
			this.getFormHM().put("unitcode",uc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**

dml获得资源件中的报表*/
	public String getReport(){
		String report="";
		StringBuffer sql=new StringBuffer();
		String reportTypes2="";
		sql.append("select tabid  from tname");
		try {
			ContentDAO dao=new ContentDAO(this.frameconn);
			this.frowset=dao.search(sql.toString());
			
			while(this.frowset.next()){
				if(userView.isHaveResource(IResourceConstant.REPORT,this.frowset.getString("tabid")))
				{
					report+=this.frowset.getString("tabid")+",";
				}
			}
			if (report.length()>0&&report.charAt(report.length() - 1) == ',') {
				report = report.substring(0, report.length() - 1);
			}
			if (report.length()>0&&report.charAt(0) == ',') {
				report = report.substring(1, report.length());
			}
			}catch (Exception e) {
				e.printStackTrace();
			}
		return report;
	}

}
