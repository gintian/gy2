package com.hjsj.hrms.transaction.report.report_state;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;


public class ReportStateSortTrans extends IBusiness {

	//报表分类表显示
	public void execute() throws GeneralException {	
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		//查询填报单位表中的报表类别信息//get方式传递的参数，填报单位编码
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String unitCode = (String) hm.get("code");
		
		
		
		//超级管理员传入null
		if(unitCode == null || "".equals(unitCode) || "null".equalsIgnoreCase(unitCode)){
			unitCode = this.getUnitCode();
		}
		
		//System.out.println("unitCode  =  "  + unitCode);
		
		ArrayList list = new ArrayList();
		String reporttypes = this.getReportTypes(unitCode);
		if(reporttypes == null || "".equals(reporttypes)){//填报单位未负责任何报表类
			
		}else{
			String sql = "select * from tsort where tsortid in("+reporttypes+")";
			try{
				this.frowset = dao.search(sql);
				while(this.frowset.next()){
					RecordVo vo = new RecordVo("tname");
					vo.setString("name" , this.frowset.getString("name"));
					String temp = String.valueOf(this.frowset.getInt("tsortid"));
					vo.setString("tsortid",temp);
					vo.setString("fontname" ,this.frowset.getString("sdes"));	
					String status ="";
					if(reporttypes == null){
					}else{
						status = this.getStatus(unitCode,temp);
						vo.setString("cbase",status);
					}	
					list.add(vo);			
				}		
				
			}catch(Exception e){
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
		}
		
		this.getFormHM().put("reporttypelist",list);
		String dxt = (String)hm.get("returnvalue");
		if(dxt!=null&&!"dxt".equals(dxt))
			hm.remove("returnvalue");
		if(dxt==null)
			dxt="";
		this.getFormHM().put("returnflag", dxt);
		
	}
	
	public String getUnitCode() {	
		String unitcode = "";
		if(userView.isSuper_admin()){
			String sql="select min(unitcode) unitcode  from tt_organization";
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" where ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 
			//System.out.println(sql);
			sql+=ext_sql.toString();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					unitcode = this.frowset.getString("unitcode");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			//获取当前用户对应的填报单位编码，生成SQL语句的where部分
			TTorganization ttorganization=new TTorganization();
			RecordVo selfVo=ttorganization.getSelfUnit3(userView.getUserName());
			unitcode = selfVo.getString("unitcode");
		}
		return unitcode;
	}
	
	/**
	 * 返回填报单位表中的报表类别字符串（数组形式）
	 * @param unitCode
	 * @return
	 * @throws GeneralException
	 */
	private String  getReportTypes(String unitCode)throws GeneralException{	
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String temp = null;
		String [] reporttypes =null;
		StringBuffer reportsql = new StringBuffer();
		reportsql.append("select reporttypes  from tt_organization where unitcode = '");
		reportsql.append(unitCode);
		reportsql.append("'");
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
		reportsql.append(ext_sql.toString());
		try {
			this.frowset = dao.search(reportsql.toString());
			if (this.frowset.next()) {
				 temp = this.frowset.getString("reporttypes");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if(temp != null){
			StringTokenizer st = new StringTokenizer(temp , ",");
			reporttypes = new String[st.countTokens()];	
			for(int i = 0; i < reporttypes.length ; i++){
				reporttypes[i] = (String)st.nextElement();
			}
		}
		StringBuffer result = new StringBuffer();
		String reportTypes2="";
		if(reporttypes == null || reporttypes.length ==0){
				//dml 撤销单位后按资源文件搜索表类
				result.append(this.getReportTypes());
			
		}else{
			for(int i=0; i<reporttypes.length; i++){
				String tt = reporttypes[i];
				if(tt == null || "".equals(tt)){
					continue;
				}
				result.append("'");
				result.append(tt);
				result.append("'");
				result.append(",");
			}
			
			if(result.length() == 0 ){
			}else{
				result.delete(result.length()-1,result.length());
			}
		}
		return result.toString();
	}

	/**
	 * 填报单位状态
	 * 	
	 *	填报单位树提示填报单位报表状态
	 *	报表状态：
			 未填     所负责的的报表 均为 未填则填报单位提示未填
		     正在编辑  所负责的报表 只要有一个 为正在编辑则填报单位提示正在编辑（比驳回小） 
			 已上报   所负责的报表 都上报 则填报单位提示已上报
			 驳回	 所负责的报表 只要有一个 为驳回则填报单位提示驳回
			 封存     所负责的报表 都封存 则填报单位提示封存
		=-1， 未填=0,  正在编辑=1,  已上报=2,  驳回=3,  封存（基层单位的数据不让修改）
	 * @param unitCode
	 * @return
	 */
	public boolean getReportStatus(String unitcode ,String tsortid , String whereSql){
		boolean b = false;
		String sql="select tr.status  from treport_ctrl tr " +
				" inner join tname tn  on tr.tabid = tn.tabid " +
				"where tr.unitcode = '"+unitcode+"' "+whereSql+
				" and tr.tabid in( select ts.tabid from tname ts where ts.tsortid='"+tsortid+"')";
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs  = dao.search(sql);
			if (rs.next()) {
				b = true;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return b;
	}

	
	public String getStatus(String unitcode,String temp){
		String status = "";
		
		if(this.getReportStatus(unitcode ,temp ,"and tr.status ='2'")){//驳回
			status ="("+ResourceFactory.getProperty("info.appleal.state2")+")";
			return status;
		}
		
		if(this.getReportStatus(unitcode ,temp ," and tr.status ='0' ")){//正在编辑
			status ="("+ResourceFactory.getProperty("edit_report.status.zzbj")+")";
			return status;
		}
		
		
		boolean wt = this.getReportStatus(unitcode ,temp ," and tr.status  = '-1' "); //存在未填
		boolean ysb = this.getReportStatus(unitcode ,temp ," and tr.status = '1' "); //存在已上报
		boolean fc = this.getReportStatus(unitcode ,temp ," and tr.status = '3' ");  //存在封存

		if(wt && ! ysb && !fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.wt")+")";
			return status;
		}
		
		if(!wt && ysb && !fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.ysb")+")";
			return status;
		}
		
		if(!wt && !ysb && fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.fc")+")";
			return status;
		}
		
		if(!wt && ysb && fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.ysb")+","+ResourceFactory.getProperty("edit_report.status.fc")+")";
			return status;
		}
		if(wt && !ysb && fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.wt")+","+ResourceFactory.getProperty("edit_report.status.fc")+")";
			return status;
		}
		if(wt && ysb && !fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.wt")+","+ResourceFactory.getProperty("edit_report.status.ysb")+")";
			return status;
		}
		
		
		if(wt && ysb && fc){
			status ="("+ResourceFactory.getProperty("edit_report.status.wt")+","+ResourceFactory.getProperty("edit_report.status.ysb")+","+ResourceFactory.getProperty("edit_report.status.fc")+")";
			return status;
		}
		
		//系统默认为未填
		status ="("+ResourceFactory.getProperty("edit_report.status.wt")+")";
		
		return status;
	}
	/**
	 * 
	 * dml 获得资源文件中报表类型*/
	public String getReportTypes(){
		StringBuffer sql=new StringBuffer();
		String reportTypes2="";
		sql.append("select tabid  from tname");
		try {
			ContentDAO dao=new ContentDAO(this.frameconn);
			this.frowset=dao.search(sql.toString());
			String report="";
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
			}if(userView.isSuper_admin()){
				sql.delete(0, sql.length());
				
				sql.append("select tabid,name,paper,tsortid  from tname  ");
			
			}else{
				report = report.replace(" ", "");
				report = report.replace("R", "");
				while(report.indexOf(",,")!=-1){
					report = report.replace(",,", ",");
				}
				sql.delete(0, sql.length());
				sql.append("select tabid,name,paper,tsortid  from tname where tabid in ("+report+") order by tsortid");
			}
			if(report.length()==0&&!userView.isSuper_admin()){
				
				
			}else{
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next()){
					if(reportTypes2.indexOf(""+this.frowset.getInt("tsortid"))==-1)
						reportTypes2+=this.frowset.getInt("tsortid")+",";
				}
			}
			sql.delete(0, sql.length());
			sql.append("select tsortid,name from tsort where ");
			if (!"".equals(reportTypes2)&&reportTypes2.charAt(reportTypes2.length() - 1) == ',') {
				reportTypes2 = reportTypes2.substring(0, reportTypes2.length() - 1);
			}
	
		}catch (Exception e) {
			e.printStackTrace();
		}
		return reportTypes2;
	
	}
	}
