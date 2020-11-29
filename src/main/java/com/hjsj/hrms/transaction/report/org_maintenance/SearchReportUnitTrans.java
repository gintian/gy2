
package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;


public class SearchReportUnitTrans extends IBusiness {


	public void execute() throws GeneralException {
		
		StringBuffer sql = new StringBuffer();
		
		//树视图中的单击传入父填报单位编码
		HashMap hm =(HashMap)(this.getFormHM().get("requestPamaHM"));
		String parentidcode = (String)hm.get("code");
		if(parentidcode==null || parentidcode.trim().length()<=0)
			parentidcode = null;
//		String backdate =(String)hm.get("backdate");
		//update by wangchaoqun on 2014-9-22 从formHm中获取backdate，避免树与内容不一致
		String backdate =(String)this.getFormHM().get("backdate");  
		String analysereportinitflag = (String)hm.get("analysereportinitflag");
		
		if(analysereportinitflag!=null&& "1".equals(analysereportinitflag)){
			hm.remove("analysereportinitflag");
			if(this.getFormHM().get("codeFlag")!=null&&this.getFormHM().get("codeFlag").toString().length()>0)
				parentidcode = (String)this.getFormHM().get("codeFlag");
		}
		//子节点编号
		if(!this.userView.isSuper_admin()&&parentidcode==null){
			TTorganization ttorganization=new TTorganization();
			RecordVo selfVo=ttorganization.getSelfUnit3(this.userView.getUserName());
			String uc = selfVo.getString("unitcode");
			if(!"".equals(uc))
				parentidcode=uc;
				
		}
		String unitCodeFalg = (String)this.getFormHM().get("unitCodeFalg");
		String flag = null;
		
		String delflag = (String) this.getFormHM().get("delflag");
		
		if(delflag == null && unitCodeFalg == null){
			//parentidcode = null;
			flag = null;
		}else{
			if(delflag == null){
				flag = this.getParentID(unitCodeFalg);
			}else{
				flag = delflag;
			}
				
		}
		String dxt = (String)hm.get("returnvalue");
		if(dxt!=null&& "dxt".equals(dxt)){
			hm.remove("returnvalue");
			this.getFormHM().put("returnflag", dxt);
		//	this.getFormHM().put("returnflag", "");
			this.getFormHM().put("analysereportflag", "");
			this.getFormHM().put("codeFlag" ,"");
			hm.remove("analysereportflag");
		}else if(dxt!=null){
			hm.remove("returnvalue");
			this.getFormHM().put("returnflag", dxt);
		//	this.getFormHM().put("returnflag", "");
			this.getFormHM().put("analysereportflag", "");
			this.getFormHM().put("codeFlag" ,"");
			hm.remove("analysereportflag");
		}else{
			String analysereportflag = (String)hm.get("analysereportflag");
			this.getFormHM().put("analysereportflag", analysereportflag);
		}
		TTorganization ttorganization=new TTorganization(this.getFrameconn());
		HashMap reportmap =  ttorganization.getReportTsort();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		//第一次进入
		if(parentidcode == null && flag == null){		
			if(userView.isSuper_admin()){//系统管理员 显示全部
				sql.append("select unitid , unitname , unitcode ,reporttypes,b0110,a0000,start_date,end_date,analysereports from tt_organization where unitcode = parentid");
			}else{//一般用户 显示用户对应的子填报单位
				 ttorganization=new TTorganization(this.getFrameconn());
				RecordVo selfVo=ttorganization.getSelfUnit(userView.getUserName());
				String uc =null;
				if(selfVo!=null)
					uc=selfVo.getString("unitcode");
				if(uc == null || "".equals(uc)){
					Exception e = new Exception(ResourceFactory.getProperty("edit_report.info10")+"！");
					throw GeneralExceptionHandler.Handle(e);
				}else{
					String params="where parentid='"+selfVo.getString("unitcode")+"'  and unitcode <> parentid ";
					sql.append("select unitid , unitname , unitcode ,reporttypes , b0110,a0000,start_date,end_date,analysereports  from tt_organization " + params);
				}
			}
			
		}else{
			//填报单位信息变动后的页面导向
			if(parentidcode == null){
				sql.append("select unitid , unitname , unitcode  ,reporttypes,b0110,a0000,start_date,end_date,analysereports from tt_organization where parentid = '");
				sql.append(flag);
				sql.append("' and unitcode <> parentid ");
			}else{
				//通过树视图操纵
				sql.append("select unitid ,  unitname , unitcode ,reporttypes,b0110,a0000,start_date,end_date,analysereports  from tt_organization where parentid = '");
				sql.append(parentidcode );
				sql.append("' and unitcode <> parentid ");
			} 
		}

		//System.out.println(sql.toString());
		
		if(sql == null || "".equals(sql.toString())){
			Exception e = new Exception(ResourceFactory.getProperty("edit_report.info10")+"！");
			throw GeneralExceptionHandler.Handle(e);
		}
		ArrayList list = new ArrayList();
		list.clear();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			 
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			if(backdate!=null&&backdate.trim().length()>0&&!"null".equalsIgnoreCase(backdate))
			{
				d.setTime(Date.valueOf(backdate));
				yy=d.get(Calendar.YEAR);
				mm=d.get(Calendar.MONTH)+1;
				dd=d.get(Calendar.DATE);
			}
			
			sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			
			sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
			 
			
			this.frowset=dao.search(sql.toString()+" order by a0000");	
			this.userView.getHm().put("sql_filter", sql.toString()+" order by a0000");//将sql放入userView，防止页面上能看到sql   zhaoxg 2014-2-10
			this.getFormHM().put("sql", sql.toString()+" order by a0000");
			LazyDynaBean abean=null;
			ArrayList sortidList=ttorganization.getTsortId();
			while(this.frowset.next()){
				abean=new LazyDynaBean();
				
				RecordVo vo = new RecordVo("tt_organization");
				String uid = String.valueOf(this.frowset.getInt("unitid"));
				vo.setString("unitid",uid);
				abean.set("unitid", uid);
				StringBuffer temp = new StringBuffer(this.frowset.getString("unitname"));
				/*if(temp.length()>=25){
					temp.insert(25,"<br>");
				}*/
				vo.setString("unitname" ,temp.toString());
				abean.set("unitname", temp.toString());
				String unitcode = this.frowset.getString("unitcode");
				vo.setString("unitcode" , unitcode );
				abean.set("unitcode",unitcode);
				String analysereportflag = (String)this.getFormHM().get("analysereportflag");
				String reporttypes = Sql_switcher.readMemo(this.frowset, "reporttypes");
				String copy_reporttypes=",";
				if(StringUtils.isNotEmpty(reporttypes)) {
					String[] sortidarry=(","+reporttypes).split(",");
					for(int i=0;i<sortidarry.length;i++) {
						if(StringUtils.isNotEmpty(sortidarry[i])&&sortidList.contains(sortidarry[i])) {
							copy_reporttypes+=sortidarry[i]+",";
						}
					}
					if(copy_reporttypes.length()>1) {
						reporttypes=copy_reporttypes.substring(1);
					}
				}
				if(analysereportflag!=null&& "1".equals(analysereportflag)){
					String analysereports =Sql_switcher.readMemo(this.frowset, "analysereports");
					ArrayList templist = new ArrayList();
					if(analysereports!=null&&analysereports.length()>0){
					String reports [] =	analysereports.split(",");
					String temptypes =",";
					for(int i=0;i<reports.length;i++){
						if(reports[i].trim().length()>0&&temptypes.indexOf(","+reportmap.get(reports[i].trim())+",")==-1&&reportmap.get(reports[i].trim())!=null){
							temptypes+=reportmap.get(reports[i].trim())+",";
							templist.add(reportmap.get(reports[i].trim()));
						}
					}
					if(temptypes.length()>1){
						reporttypes= "";
						Collections.sort(templist);
						for(int i=0;i<templist.size();i++){
							reporttypes+= templist.get(i)+",";
						}
					}
					}else{//一个表都没选  赵旭光 2013-4-10
						reporttypes = null;
					}
					
				}
				
				//System.out.println("reporttypes=" + reporttypes);
				
				if(reporttypes == null || "".equals(reporttypes)){
					vo.setString("reporttypes" , "");
					abean.set("reporttypes","");
				}else{
					vo.setString("reporttypes","("+reporttypes.substring(0,reporttypes.length()-1)+")");
					abean.set("reporttypes","("+reporttypes.substring(0,reporttypes.length()-1)+")");
				}
				String un = this.getUserName(unitcode);
				
				//System.out.println("un=" + un);
				
				if(un==null|| "".equals(un)){
					vo.setString("b0110","");
					abean.set("b0110","");
				}else{
					vo.setString("b0110","("+un.substring(0,un.length()-1)+")");
					abean.set("b0110","("+un.substring(0,un.length()-1)+")");
				}
				String a0000 = this.frowset.getString("a0000");
				
				//System.out.println("reporttypes=" + reporttypes);
				
				if(a0000 == null || "".equals(a0000)){
					vo.setString("a0000" , "");
					abean.set("a0000","");
				}else{
					vo.setString("a0000",a0000);
					abean.set("a0000",a0000);
				}
				abean.set("start_date", df.format(this.frowset.getDate("start_date")));
				abean.set("end_date", df.format(this.frowset.getDate("end_date")));
				 
				list.add(abean);
			//	list.add(vo);				
			}		
		}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
		}
		//设置填报单位显示集合
		this.getFormHM().put("reportUnitList",list);
		
		//设置页面显示列表的的父填报单位的单位编码
		if(parentidcode != null){
			this.getFormHM().put("codeFlag" , parentidcode );
		}else{
			this.getFormHM().put("codeFlag" ,flag);
		}

		//清空参数
		hm.remove("code");
	
		
		this.getFormHM().remove("delflag");
		this.getFormHM().remove("unitCodeFalg");
	}

	public String getUserName(String uid){
		StringBuffer userName = new StringBuffer();;
		String sql="select * from operuser   where unitcode='"+uid+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		RowSet rs;
		try {
			rs = dao.search(sql);
			String un="";
			while(rs.next()){
				if(rs.getString("fullname")==null||rs.getString("fullname").trim().length()==0){
					un=rs.getString("username");
				}else{
					un = rs.getString("fullname");
				}
				userName.append(un);
				userName.append(",");
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userName.toString();
	}
	
	
	//获取父填报单位编码
	//参数为填报单位编码
	public String getParentID(String unitcode) throws GeneralException{
		String temp=null;
		StringBuffer strsql = new StringBuffer();
		strsql.delete(0,strsql.length());	
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());		
			//SQL
			strsql.append("select parentid from  tt_organization where  unitcode= '");
			strsql.append(unitcode);
			strsql.append("'");
			//执行SQL
			this.frowset=dao.search(strsql.toString());
			if(this.frowset.next()){
				temp=(String)this.frowset.getString("parentid");
				if(temp.equals(unitcode)){
					temp = null;
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return temp;
	}

}
