/*
 * Created on 2005-12-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Administrator
 *
 */
public class SearchOrgListTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String code=(String)hm.get("code"); 
		String kind=(String)hm.get("kind");
		String root=(String)hm.get("root");
		ArrayList orglist=new ArrayList();
		String vflag = (String)this.getFormHM().get("vflag");
		StringBuffer strsql=new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = (String)this.getFormHM().get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		String query = (String)this.getFormHM().get("query");
		/**
		 * 虚拟机构是否为可编辑
		 * xus 16/11/21
		 */
		String virtualOrgSet="";
		virtualOrgSet=SystemConfig.getPropertyValue("virtualOrgSet")==null?"":SystemConfig.getPropertyValue("virtualOrgSet");
		
		String addFuncFlag= "1";
		
		//20180608 sql参数化，避免注入漏洞
        ArrayList sqlParams = new ArrayList();
        
		if("1".equals(query)){
			String idordesc = (String)this.getFormHM().get("idordesc");
			idordesc = com.hrms.frame.codec.SafeCode.decode(idordesc);
			strsql.append("select codesetid,codeitemid,corcode,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,start_date,end_date");
            strsql.append(" from organization");
            strsql.append(" where codeitemdesc like ?");
            strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
            sqlParams.add(idordesc);
            /*if(!(this.userView.getManagePrivCode().equalsIgnoreCase("UN")&&(this.userView.getManagePrivCodeValue()==null||this.userView.getManagePrivCodeValue().length()<=0)))
			{
				strsql.append(" and codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'");
			}*/
			String busi = getBusi_org_dept(this.userView);
			if(!this.userView.isSuper_admin()){
				if(busi.length()>=2){
					if(busi.indexOf("`")!=-1){
						strsql.append(" and (");
						String[] tmps=busi.split("`");
						for(int i=0;i<tmps.length;i++){
							String a_code=tmps[i];
							if(a_code.length()>=2){
								strsql.append(" codeitemid like '"+a_code.substring(2)+"%' or");
							}
						}
						strsql.append(" 1=2) ");
					}else{
						strsql.append(" and codeitemid like '"+busi.substring(2)+"%' ");
					}
				}else{
					strsql.append(" and 1=2 ");
				}
			}
			strsql.append(" union ");
			strsql.append(" select codesetid,codeitemid,corcode,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'vorg' as flag,start_date,end_date");
            strsql.append(" from vorganization");
            strsql.append(" where codeitemdesc like ?");
            strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
            sqlParams.add(idordesc);
            /*if(!(this.userView.getManagePrivCode().equalsIgnoreCase("UN")&&(this.userView.getManagePrivCodeValue()==null||this.userView.getManagePrivCodeValue().length()<=0)))
			{
				strsql.append(" and codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'");
			}*/
			if(!this.userView.isSuper_admin()){
				if(busi.length()>=2){
					if(busi.indexOf("`")!=-1){
						strsql.append(" and (");
						String[] tmps=busi.split("`");
						for(int i=0;i<tmps.length;i++){
							String a_code=tmps[i];
							if(a_code.length()>=2){
								strsql.append(" codeitemid like '"+a_code.substring(2)+"%' or");
							}
						}
						strsql.append(" 1=2) ");
					}else{
						strsql.append(" and codeitemid like '"+busi.substring(2)+"%' ");
					}
				}else{
					strsql.append(" and 1=2 ");
				}
			}
			strsql.append(" order by codeitemid,flag,A0000");
		}else{
		if(code!=null && code.trim().length()>0){
		    strsql.append("select codesetid,codeitemid,corcode,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,start_date,end_date");
            strsql.append(" from organization");
            strsql.append(" where parentid=?");
            sqlParams.add(code);
            strsql.append(" and codeitemid<>parentid ");
            strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
            if("1".equalsIgnoreCase(vflag)){
                strsql.append(" union");
                strsql.append(" select codesetid,codeitemid,corcode,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'vorg' as flag,start_date,end_date");
                strsql.append(" from vorganization");
                strsql.append(" where parentid=?");
                sqlParams.add(code);
                strsql.append(" and codeitemid<>parentid ");
            }
            strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
            strsql.append(" order by A0000,codeitemid,flag");
		}
		else
		{
			
			if(!this.userView.isSuper_admin()){
				
				/*if(this.userView.getManagePrivCode().equalsIgnoreCase("UN")&&(this.userView.getManagePrivCodeValue()==null||this.userView.getManagePrivCodeValue().length()<=0))
				{
					strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,start_date,end_date from organization where codeitemid=parentid ");
					strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				}else
				{
					if(root!=null&&root.equals("1"))
					{
						strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,start_date,end_date from organization where 1=1 ");
						strsql.append(" and codeitemid='"+this.userView.getManagePrivCodeValue()+"'");
						strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					}else
					{
						strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,start_date,end_date from organization where 1=1 ");
						strsql.append(" and parentid='"+this.userView.getManagePrivCodeValue()+"'");
						strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					}
				}*/
				strsql.append("select codesetid,codeitemid,corcode,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,start_date,end_date from organization where 1=1 ");
				strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				String busi = getBusi_org_dept(this.userView);
				if(busi.length()>2){
					if(busi.indexOf("`")!=-1){
						StringBuffer sb = new StringBuffer();
						String[] tmps=busi.split("`");
						for(int i=0;i<tmps.length;i++){
							String a_code=tmps[i];
							if(a_code.length()>2){
								sb.append("','"+a_code.substring(2));
							}
						}
						if(sb.length()>3)
							strsql.append(" and codeitemid in('"+sb.substring(3)+"') ");
						else
							if("UN".equalsIgnoreCase(busi)|| "UN`".equalsIgnoreCase(busi)){
								strsql.append(" and codeitemid=parentid ");
							}else{
								strsql.append("1=2");
							}
					}else{
						strsql.append(" and codeitemid='"+busi.substring(2)+"' ");
					}
				}else if("UN".equalsIgnoreCase(busi)|| "UN`".equalsIgnoreCase(busi)){
					strsql.append(" and codeitemid=parentid");
				}else{
					strsql.append(" and 1=2 ");
				}
				
				
				if(busi.indexOf("UN`")==-1)
					addFuncFlag = "0";
			}else{
				strsql.append("select codesetid,codeitemid,corcode,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,start_date,end_date from organization where codeitemid=parentid ");
				strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
			}
			if("1".equalsIgnoreCase(vflag)){
				if(!this.userView.isSuper_admin()){
					/*if(root!=null&&root.equals("1"))
					{
						strsql.append(" union select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'vorg' as flag,start_date,end_date from vorganization where 1=1 ");
						strsql.append(" and codeitemid='"+this.userView.getManagePrivCodeValue()+"'");
						strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					}else
					{
						strsql.append(" union select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'vorg' as flag,start_date,end_date from vorganization where 1=1 ");
						strsql.append(" and parentid='"+this.userView.getManagePrivCodeValue()+"'");
						strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					}*/
					strsql.append(" union select codesetid,codeitemid,corcode,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,start_date,end_date from vorganization where 1=1 ");
					strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					String busi = getBusi_org_dept(this.userView);
					if(busi.length()>2){
						if(busi.indexOf("`")!=-1){
							StringBuffer sb = new StringBuffer();
							String[] tmps=busi.split("`");
							for(int i=0;i<tmps.length;i++){
								String a_code=tmps[i];
								if(a_code.length()>2){
									sb.append("','"+a_code.substring(2));
								}
							}
							if(sb.length()>3)
								strsql.append(" and codeitemid in('"+sb.substring(3)+"') ");
							else
								if("UN".equalsIgnoreCase(busi)|| "UN`".equalsIgnoreCase(busi)){
									strsql.append(" and codeitemid=parentid ");
								}else{
									strsql.append("1=2");
								}
						}else{
							strsql.append(" and codeitemid='"+busi.substring(2)+"' ");
						}
					}else if("UN".equalsIgnoreCase(busi)|| "UN`".equalsIgnoreCase(busi)){
						strsql.append(" and codeitemid=parentid");
					}else{
						strsql.append(" and 1=2 ");
					}
				}else{
					strsql.append(" union select codesetid,codeitemid,corcode,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'vorg' as flag,start_date,end_date from vorganization where codeitemid=parentid ");
					strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				}
				
			}
			//机构名称优先显示归档的历史数据的名称
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT org.codesetid,case when " + Sql_switcher.isnull("his.codeitemdesc", "'#'"));
			sql.append("<>'#' then his.codeitemdesc else org.codeitemdesc end codeitemdesc,");
			sql.append("org.codeitemid,org.corcode,org.parentid,org.childid,'org' as flag,org.a0000,org.end_date,org.state,org.grade,org.groupid,org.start_date from (");
			sql.append(strsql);
			sql.append(") org left join (");
			sql.append("select * from hr_org_history where catalog_id = (");
			sql.append("select MIN(catalog_id) from hr_org_history where catalog_id > '" + backdate.replace("-", "") + "')) his");
			sql.append(" on org.codeitemid=his.codeitemid and org.codesetid=his.codesetid");
			sql.append(" ORDER BY org.a0000,org.codeitemid,org.flag");
			strsql = sql;
		}
		}
	//System.out.println(strsql);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			this.frowset=dao.search(strsql.toString(), sqlParams);
			while(this.frowset.next())
			{
				RecordVo organizationvo=new RecordVo("organization");
				organizationvo.setString("codesetid",this.frowset.getString("codesetid"));
				organizationvo.setString("codeitemid",this.frowset.getString("codeitemid"));
				//2020-06-17 贵银 加机构编码
				organizationvo.setString("corcode",this.frowset.getString("corcode"));
				organizationvo.setString("codeitemdesc",this.frowset.getString("codeitemdesc"));
				organizationvo.setString("parentid",this.frowset.getString("parentid"));
				organizationvo.setString("childid",this.frowset.getString("childid"));
				organizationvo.setString("state",this.frowset.getString("state"));
				organizationvo.setInt("grade",this.frowset.getInt("grade"));
				organizationvo.setInt("a0000",this.frowset.getInt("a0000"));
				organizationvo.setInt("groupid",this.frowset.getInt("groupid"));
				organizationvo.setString("flag",this.frowset.getString("flag"));
				Date start_date = this.frowset.getDate("start_date");
				Date end_date = this.frowset.getDate("end_date");
				organizationvo.setString("start_date",String.valueOf(start_date!=null?com.hjsj.hrms.utils.PubFunc.FormatDate(start_date,"yyyy-MM-dd"):""));
				organizationvo.setString("end_date",String.valueOf(end_date!=null?com.hjsj.hrms.utils.PubFunc.FormatDate(end_date,"yyyy-MM-dd"):""));
				orglist.add(organizationvo);
			}
		
			this.getFormHM().put("orglist",orglist);	
			this.getFormHM().put("addFuncFlag", addFuncFlag);
			this.getFormHM().put("virtualOrgSet", virtualOrgSet);
			//this.getFormHM().put("isrefresh","yes");
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}		
	}
	
	private String getBusi_org_dept(UserView userView) {
		String busi = "";
				String busi_org_dept = "";
				Connection conn = null;
				RowSet rs = null;
				try {
					
					busi_org_dept = userView.getUnitIdByBusi("4");
					if (busi_org_dept.length() > 0) {
						busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
					}else{
						busi=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					if (conn != null)
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}
		return busi;
	}
	
	
}
