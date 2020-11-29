/*
 * Created on 2006-3-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchOrgmapsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	private boolean isgrades=false;
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String code=(String)hm.get("code"); 
		String kind=(String)hm.get("kind");
		String dbnames=(String)hm.get("dbnames");
		String orgtype=(String)hm.get("orgtype");
		String namesinglecell=(String)hm.get("namesinglecell");
		String isshowpersonconut=(String)hm.get("isshowpersonconut");
		String isshoworgconut=(String)hm.get("isshoworgconut");
		String isshowpersonname=(String)hm.get("isshowpersonname");
		String isshowposname=(String)hm.get("isshowposname");
		String isshowdeptname=(String)hm.get("isshowdeptname");
		String backdate = (String)hm.get("backdate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		StringBuffer loadpersonsql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		boolean bShowPos=true;
		boolean bShowDept=true;
		if(isshowposname==null||isshowposname.length()<=0|| "false".equals(isshowposname))
			bShowPos=false;
		if(isshowdeptname==null||isshowdeptname.length()<=0|| "false".equals(isshowdeptname))
			bShowDept=false;
		String tableA01="";		
		String deleteSql="";
		/*if(!this.userView.isSuper_admin())
		{
			String whereRen=InfoUtils.getWhereINSql(this.userView,dbnames);
			tableA01="(select * from "+dbnames+"A01 where "+dbnames+"A01.a0100 in(select "+dbnames+"A01.a0100 ";
			tableA01=tableA01+" "+whereRen+")) "+dbnames+"A01 ";
		}else*/
		{
			tableA01=" "+dbnames+"A01 ";
		}			
		try{
			    if(code!=null && code.length()>0)
				{
					StringBuffer sqlstr=new StringBuffer();
					
					if("true".equalsIgnoreCase(isshowpersonconut) && dbnames!=null && dbnames.length()==3)
					{
						sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc"+Sql_switcher.concat()+"(case when(codesetid<>'@K') then "+("true".equals(isshoworgconut)?ConverORGsql(backdate,bShowPos):"''")+" else '' end)"+ ConverDBsql(dbnames) + " as codeitemdesc,organization.parentid,organization.childid,");
		                sqlstr.append(" organization.grade,count(");
		                sqlstr.append(dbnames);
		                sqlstr.append("A01.a0100) as personcount,'org' as infokind from ");
		                
		                //sqlstr.append(dbnames+"A01");
		                sqlstr.append("(select * from "+tableA01+" where e01a1 not in (select codeitemid from organization where view_chart='1')) ");
		                sqlstr.append(tableA01);
		                sqlstr.append(" RIGHT OUTER JOIN");
		                sqlstr.append(" organization ON ");
		                sqlstr.append(Sql_switcher.substr(dbnames + "A01.B0110","1",Sql_switcher.length("organization.codeitemid")));
						sqlstr.append("= organization.codeitemid OR ");
						sqlstr.append(Sql_switcher.substr(dbnames + "A01.e0122","1",Sql_switcher.length("organization.codeitemid")));
						sqlstr.append("= organization.codeitemid OR ");
						sqlstr.append(Sql_switcher.substr(dbnames + "A01.e01a1","1",Sql_switcher.length("organization.codeitemid")));
						sqlstr.append("= organization.codeitemid");
						sqlstr.append(" where organization.codeitemid<>'");
						sqlstr.append(code);
						sqlstr.append("' and organization.parentid='");
						sqlstr.append(code);
						sqlstr.append("'");
						/*if(whereRen!=null&&whereRen.length()>0)
							sqlstr.append(" and "+dbnames+"A01.a0100 in(select a0100 "+whereRen+")");*/
						if(!bShowPos)
							sqlstr.append(" and organization.codesetid<>'@K'");
						sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
		                sqlstr.append(" and (organization.view_chart<>1 or organization.view_chart is null)");
						sqlstr.append(" GROUP BY organization.codesetid,organization.A0000,organization.codeitemid,organization.codeitemdesc,organization.parentid,organization.childid,");
		                sqlstr.append(" organization.grade");    
		                sqlstr.append(" order by organization.A0000,organization.codeitemid");
					}
					else {
						String dtname = userView.getUserName()+"dorgs";
						if(Sql_switcher.searchDbServer() == Constant.ORACEL && !bShowPos){
							sqlstr.append("create table "+dtname+" as ");
						}
						sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc"+Sql_switcher.concat()+"(case when(codesetid<>'@K') then "+("true".equals(isshoworgconut)?ConverORGsql(backdate,bShowPos):"''")+" else '' end)  as codeitemdesc,organization.parentid,organization.childid,");
		                sqlstr.append(" organization.grade,count(");
		                sqlstr.append(dbnames);
		                sqlstr.append("A01.a0100) as personcount,'org' as infokind ");
		                if(Sql_switcher.searchDbServer() != Constant.ORACEL && !bShowPos){
							sqlstr.append(" into "+dtname);
						}
		                sqlstr.append(" from "+tableA01);
		                //sqlstr.append(dbnames+"A01");
		                sqlstr.append("RIGHT OUTER JOIN");
		                sqlstr.append(" organization ON ");
		                sqlstr.append(Sql_switcher.substr(dbnames + "A01.B0110","1",Sql_switcher.length("organization.codeitemid")));
						sqlstr.append("= organization.codeitemid OR ");
						sqlstr.append(Sql_switcher.substr(dbnames + "A01.e0122","1",Sql_switcher.length("organization.codeitemid")));
						sqlstr.append("= organization.codeitemid OR ");
						sqlstr.append(Sql_switcher.substr(dbnames + "A01.e01a1","1",Sql_switcher.length("organization.codeitemid")));
						sqlstr.append("= organization.codeitemid");
						sqlstr.append(" where organization.codeitemid<>'");
						sqlstr.append(code);
						sqlstr.append("' and organization.parentid='");
						sqlstr.append(code);
						sqlstr.append("'");		
						/*if(whereRen!=null&&whereRen.length()>0)
							sqlstr.append(" and "+dbnames+"A01.a0100 in(select a0100 "+whereRen+")");*/
						if(!bShowPos)
							sqlstr.append(" and organization.codesetid<>'@K'");
						sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
						sqlstr.append(" and (organization.view_chart<>1 or organization.view_chart is null)");
						sqlstr.append(" and usrA01.b0110 not in (select codeitemid from organization where view_chart='1') and usrA01.e01a1 not in (select codeitemid from organization where view_chart='1') and usrA01.e01a1 not in (select codeitemid from organization where view_chart='1')");
						sqlstr.append(" GROUP BY organization.codesetid,organization.A0000,organization.codeitemid,organization.codeitemdesc,organization.parentid,organization.childid,");
		                sqlstr.append(" organization.grade");	
		                sqlstr.append(" order by organization.A0000,organization.codeitemid");
		                if(!bShowPos){
			                dao.update(sqlstr.toString());
			                sqlstr.delete(0, sqlstr.length());
			                sqlstr.append("update "+dtname+" ");
			                sqlstr.append(" set childid = codeitemid where childid in (select codeitemid from organization where codesetid='@K')");
			                dao.update(sqlstr.toString());
			                sqlstr.delete(0, sqlstr.length());
			                sqlstr.append("select * from "+dtname);
			                deleteSql = "drop table "+dtname;
		                }
					
					}	
			        //System.out.println(sqlstr.toString());
				    List rs=ExecuteSQL.executeMyQuery(sqlstr.toString());
					if(deleteSql.length()>0)
				        dao.update(deleteSql);
				    if(!rs.isEmpty())
				    {
				   	  LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				   	  this.isgrades=true;
				   	  this.getFormHM().put("grades",rec.get("grade"));
				    }		
				    
				    getVorgTreeXml(rs,dbnames,code,isshowpersonconut,isshowpersonname,backdate);
				    
				    if("true".equalsIgnoreCase(isshowpersonname) && dbnames!=null && dbnames.length()==3)
				    {
				    	
				        loadpersonsql.append("SELECT b.a0100, a.codeitemid, b.A0101, b.A0000 from organization a, ");
					    loadpersonsql.append(dbnames);
					    loadpersonsql.append("A01 b ");
					    loadpersonsql.append(" where (a.codeitemid='"+code+"')");
					    /*if(bShowPos)
					       loadpersonsql.append(" where (a.codeitemid='"+code+"')");
					    else
					       loadpersonsql.append(" where (a.codeitemid like'"+code+"%')");*/
					    loadpersonsql.append("  and (((b.E0122= a.codeitemid) and ((b.E01A1 is null) or (b.e01a1=' ') or (" + Sql_switcher.trim("b.E01A1") + " =");
					    loadpersonsql.append("''))) or ");   // 有部门，无职位
					    loadpersonsql.append("(b.E01A1=a.codeitemid) or ");  // 有职位
					         // 有单位，无部门职位
					    
					    loadpersonsql.append("((b.B0110=a.codeitemid) and ((b.E0122 is null) or (b.E0122=' ') or (" + Sql_switcher.trim("b.E0122") + "='')) and ((b.E01A1 is null) or (b.E01A1=' ') or  (" + Sql_switcher.trim("b.E01A1") + "='')))");
					    loadpersonsql.append(")");
					    if(!this.userView.isSuper_admin())
						{
							String whereRen=InfoUtils.getWhereINSql(this.userView,dbnames);
							loadpersonsql.append(" and b.a0100 in(select "+dbnames+"A01.a0100 "+whereRen+")");
						}
					    loadpersonsql.append(" and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date ");
					    loadpersonsql.append(" and (a.view_chart<>1 or a.view_chart is null)");
					    loadpersonsql.append(" order by b.a0000");
				    }
				    //System.out.println(loadpersonsql);
				    if(loadpersonsql!=null && loadpersonsql.length()>0)
				    {
				     	this.frowset = dao.search(loadpersonsql.toString());
				    	while(this.frowset.next())
				    	{
				    		LazyDynaBean personbean = new LazyDynaBean();
							personbean.set("grade", "");
							personbean.set("codeitemid", this.frowset.getString("a0100"));
							personbean.set("codeitemdesc", this.frowset.getString("a0101")!=null&&this.frowset.getString("a0101").length()>0?this.frowset.getString("a0101").replaceAll("\"",""):"");
							personbean.set("parentid", "parentid");
							personbean.set("childid", this.frowset.getString("a0100"));
							personbean.set("personcount", "0");
							personbean.set("infokind", "1");
							rs.add(personbean);
				    	}
				    	if(!bShowPos)
				    	{
				    		loadpersonsql.delete(0, loadpersonsql.length());
				    		loadpersonsql.append("SELECT b.a0100, a.codeitemid, b.A0101, b.A0000 from organization a, ");
						    loadpersonsql.append(dbnames);
						    loadpersonsql.append("A01 b ");
						    loadpersonsql.append(" where (a.codeitemid like'"+code+"%')");
						    loadpersonsql.append(" and (a.codesetid='@K') and (a.parentid='"+code+"')");
						    loadpersonsql.append("  and (((b.E0122= a.codeitemid) and ((b.E01A1 is null) or (b.e01a1=' ') or (" + Sql_switcher.trim("b.E01A1") + " =");
						    loadpersonsql.append("''))) or ");   
						    loadpersonsql.append("(b.E01A1=a.codeitemid) or ");  
						    loadpersonsql.append("((b.B0110=a.codeitemid) and ((b.E0122 is null) or (b.E0122=' ') or (" + Sql_switcher.trim("b.E0122") + "='')) and ((b.E01A1 is null) or (b.E01A1=' ') or  (" + Sql_switcher.trim("b.E01A1") + "='')))");
						    loadpersonsql.append(")");
						    loadpersonsql.append(" and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date ");
						    loadpersonsql.append(" and (a.view_chart<>1 or a.view_chart is null)");
						    loadpersonsql.append(" order by b.a0000");
						    this.frowset = dao.search(loadpersonsql.toString());
					    	while(this.frowset.next())
					    	{
					    		LazyDynaBean personbean = new LazyDynaBean();
								personbean.set("grade", "");
								personbean.set("codeitemid", this.frowset.getString("a0100"));
								personbean.set("codeitemdesc", this.frowset.getString("a0101")!=null?this.frowset.getString("a0101").replaceAll("\"",""):"");
								personbean.set("parentid", "parentid");
								personbean.set("childid", this.frowset.getString("a0100"));
								personbean.set("personcount", "0");
								personbean.set("infokind", "1");
								rs.add(personbean);
					    	}
				    	}
				    	
				    }			    
				    
				    this.getFormHM().put("childslist",rs);
			    }
				else
				{
					StringBuffer sqlstr=new StringBuffer();
					if("true".equalsIgnoreCase(isshowpersonconut) && dbnames!=null && dbnames.length()==3)
					{ 
						/*" + ConverDBsql(dbname) + "*/
						sqlstr.append("select organization.grade,organization.codesetid,organization.codeitemid,organization.codeitemdesc "+Sql_switcher.concat()+"(case when(codesetid<>'@K') then "+("true".equals(isshoworgconut)?ConverORGsql(backdate,bShowPos):"''")+" else '' end)"  + ConverDBsql(dbnames)  + " as codeitemdesc,organization.parentid,organization.childid,");
		                sqlstr.append(" count(");
		                sqlstr.append(dbnames);
		                sqlstr.append("A01.a0100) as personcount,'org' as infokind from ");
		                sqlstr.append("(select * from "+tableA01+" where e01a1 not in (select codeitemid from organization where view_chart='1')) ");
		                sqlstr.append(tableA01);
		                //sqlstr.append(dbnames+"A01");
		                sqlstr.append(" RIGHT OUTER JOIN");
		                sqlstr.append(" organization ON ");
		                sqlstr.append(Sql_switcher.substr(dbnames + "A01.B0110","1",Sql_switcher.length("organization.codeitemid")));
						sqlstr.append("= organization.codeitemid OR ");
						sqlstr.append(Sql_switcher.substr(dbnames + "A01.e0122","1",Sql_switcher.length("organization.codeitemid")));
						sqlstr.append("= organization.codeitemid OR ");
					    sqlstr.append(Sql_switcher.substr(dbnames + "A01.e01a1","1",Sql_switcher.length("organization.codeitemid")));
						sqlstr.append("= organization.codeitemid");
						if(userView.isSuper_admin()){
							sqlstr.append(" where parentid=codeitemid ");
						}else{
							String busi=this.getBusi_org_dept(userView);
							if(busi.length()>2){
								if(busi.indexOf("`")!=-1){
									StringBuffer sb = new StringBuffer();
									String[] tmps=busi.split("`");
									for(int i=0;i<tmps.length;i++){
										String a_code=tmps[i];
										if(a_code.length()>2){
											sb.append("','"+a_code.substring(2));
										}else if(a_code.length()==2){
											sb.append(" where parentid=codeitemid");
										}
									}
									if(sb.indexOf("where")!=-1){
										sqlstr.append(sb);
									}else if(sb.length()>3)
										sqlstr.append(" where codeitemid in('"+sb.substring(3)+"') ");
									else
										sqlstr.append(" where 1=2 ");
								}else{
									sqlstr.append(" where codeitemid='"+busi.substring(2)+"' ");
								}
							}else{
								sqlstr.append(" where 1=2 ");
							}
						}
		                sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
		                sqlstr.append(" GROUP BY organization.codesetid,organization.A0000,organization.codeitemid,organization.codeitemdesc,organization.parentid,organization.childid,organization.grade");
		                sqlstr.append(" order by organization.A0000,organization.codeitemid");
					}
					else
					{
					   sqlstr.append("select codesetid,codeitemid,codeitemdesc"+Sql_switcher.concat()+"(case when(codesetid<>'@K') then "+("true".equals(isshoworgconut)?ConverORGsql(backdate,bShowPos):"''")+" else '' end) as codeitemdesc,grade,childid,'org' as infokind from organization ");
					   if(userView.isSuper_admin()){
							sqlstr.append(" where parentid=codeitemid ");
						}else{
							String busi=this.getBusi_org_dept(userView);
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
										sqlstr.append(" where codeitemid in('"+sb.substring(3)+"') ");
									else
										sqlstr.append(" where 1=2 ");
								}else{
									sqlstr.append(" where codeitemid='"+busi.substring(2)+"' ");
								}
							}else{
								sqlstr.append(" where 1=2 ");
							}
						}
					   sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
					   sqlstr.append(" and (organization.view_chart<>1 or organization.view_chart is null)");
					   sqlstr.append(" order by organization.A0000,organization.codeitemid");
					}
				    List rs=ExecuteSQL.executeMyQuery(sqlstr.toString());
				    if(!rs.isEmpty())
				    {
				   	  LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				   	  this.isgrades=true;
				   	  this.getFormHM().put("grades",rec.get("grade"));
				    }			  
				    this.getFormHM().put("childslist",rs);			 
				}
			//}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}	
	}
	private String ConverDBsql(String dbname)
	{
		String resultsql="";
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			resultsql=" + '(' + Convert(Varchar,count(" + dbname + "A01.a0100)) + '人)'" ;
			break;
		}
		case Constant.DB2: {
			resultsql=" + '(' + To_Char(count(" + dbname + "A01.a0100)) + '人)'" ;
			break;
		}
		case Constant.ORACEL: {
			resultsql=" || '(' ||count(" + dbname + "A01.a0100) || '人)'" ;
			break;
		}
		}
		return resultsql;
	}
	private String ConverORGsql(String backdate,boolean bShowPos)
	{
		String resultsql="";
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			resultsql=" + '[' +Convert(Varchar,((select count(codeitemid) from organization org where org.parentid<>org.codeitemid and org.parentid=organization.codeitemid and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null) "+(!bShowPos?"and org.codesetid<>'@K'":"")+") +( select count(codeitemid) from vorganization vorg where vorg.parentid<>vorg.codeitemid and  vorg.parentid=organization.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between vorg.start_date and vorg.end_date and (vorg.view_chart<>1 or vorg.view_chart is null)"+(!bShowPos?"and vorg.codesetid<>'@K'":"")+"))) + ']'" ;
			break;
		}
		case Constant.DB2: {
			resultsql=" + '[' +To_Char(((select count(codeitemid) from organization org where org.parentid<>org.codeitemid and org.parentid=organization.codeitemid and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)"+(!bShowPos?"and org.codesetid<>'@K'":"")+") +( select count(codeitemid) from vorganization vorg where vorg.parentid<>vorg.codeitemid and vorg.parentid=organization.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between vorg.start_date and vorg.end_date and (vorg.view_chart<>1 or vorg.view_chart is null)"+(!bShowPos?"and vorg.codesetid<>'@K'":"")+"))) + ']'" ;
			break;
		}
		case Constant.ORACEL: {
			resultsql=" '[' ||((select count(codeitemid) from organization org where org.parentid<>org.codeitemid and org.parentid=organization.codeitemid and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)"+(!bShowPos?"and org.codesetid<>'@K'":"")+") +( select count(codeitemid) from vorganization vorg where vorg.parentid<>vorg.codeitemid and vorg.parentid=organization.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between vorg.start_date and vorg.end_date and (vorg.view_chart<>1 or vorg.view_chart is null)"+(!bShowPos?"and vorg.codesetid<>'@K'":"")+")) || ']'" ;
			break;
		}
		}
		return resultsql;
	}
	
	/**
	 * 虚拟表结构SQL
	 * @param params
	 * @param issuperuser
	 * @param parentid
	 * @param manageprive
	 * @return
	 */
	private String getLoadVorgTreeQueryString(String dbnames,String code,String isshowpersonconut,String backdate)
	{
		StringBuffer strsql=new StringBuffer();
		if("true".equalsIgnoreCase(isshowpersonconut))
		{
			strsql.append("SELECT vorganization.codesetid,vorganization.codeitemdesc "  + ConverDBsql(dbnames) + " as codeitemdesc,vorganization.codeitemid,vorganization.parentid,vorganization.childid, ");
			strsql.append(" vorganization.grade,count(");
			strsql.append(dbnames);
			strsql.append("A01.a0100) as personcount,'vorg' as infokind from ");
			strsql.append(dbnames);
			strsql.append("A01 RIGHT OUTER JOIN");			
			strsql.append("  t_vorg_staff ON ");
			strsql.append(""+dbnames+"A01.a0100=t_vorg_staff.a0100 ");
			strsql.append(" and  t_vorg_staff.state=1  and Upper(t_vorg_staff.dbase)='"+dbnames.toUpperCase()+"'");
	        strsql.append(" RIGHT OUTER join vorganization ON ");
	        strsql.append(" t_vorg_staff.b0110=vorganization.codeitemid ");	 
	        strsql.append(" where vorganization.codeitemid<>'");
	        strsql.append(code);
	        strsql.append("' and vorganization.parentid='");
	        strsql.append(code);
	        strsql.append("'");	        
	        strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between vorganization.start_date and vorganization.end_date ");
	        strsql.append(" and (vorganization.view_chart<>1 or vorganization.view_chart is null)");
	        strsql.append(" GROUP BY vorganization.codesetid,vorganization.A0000,vorganization.codeitemid,vorganization.codeitemdesc,vorganization.parentid,vorganization.childid,");
			strsql.append(" vorganization.grade");	
			strsql.append(" order by vorganization.A0000,vorganization.codeitemid");
		}else
		{
			strsql.append("SELECT vorganization.codesetid,vorganization.codeitemdesc,vorganization.codeitemid,vorganization.parentid,vorganization.childid,'vorg' as infokind, ");
			strsql.append(" vorganization.grade,count(");
			strsql.append(dbnames);
			strsql.append("A01.a0100) as personcount,'vorg' as infokind from ");
			strsql.append(dbnames);
			strsql.append("A01 RIGHT OUTER JOIN");			
			strsql.append("  t_vorg_staff ON ");
			strsql.append(""+dbnames+"A01.a0100=t_vorg_staff.a0100 ");
			strsql.append(" and  t_vorg_staff.state=1  and Upper(t_vorg_staff.dbase)='"+dbnames.toUpperCase()+"'");
	        strsql.append(" RIGHT OUTER join vorganization ON ");
	        strsql.append(" t_vorg_staff.b0110=vorganization.codeitemid ");	 
	        strsql.append(" where vorganization.codeitemid<>'");
	        strsql.append(code);
	        strsql.append("' and vorganization.parentid='");
	        strsql.append(code);
	        strsql.append("'");	
	        strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between vorganization.start_date and vorganization.end_date ");
	        strsql.append(" and (vorganization.view_chart<>1 or vorganization.view_chart is null)");
	        strsql.append(" GROUP BY vorganization.codesetid,vorganization.A0000,vorganization.codeitemid,vorganization.codeitemdesc,vorganization.parentid,vorganization.childid,");
			strsql.append(" vorganization.grade");	
			strsql.append(" order by vorganization.A0000,vorganization.codeitemid");
		}
		return strsql.toString();		
	}
	private void  getVorgTreeXml(List rs,String dbnames,String code,String isshowpersonconut,String isshowpersonname,String backdate)throws Exception
	{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		List v_rs=ExecuteSQL.executeMyQuery(getLoadVorgTreeQueryString(dbnames,code,isshowpersonconut,backdate)); 
	    if(!v_rs.isEmpty())
	    {
	    	if(!this.isgrades)
	    	{
	    		LazyDynaBean rec=(LazyDynaBean)v_rs.get(0);
	  	   	    this.isgrades=true;
	  	   	    this.getFormHM().put("grades",rec.get("grade"));
	    	}
	    	for(int i=0;i<v_rs.size();i++)
	    	{
	    		 DynaBean rec=(DynaBean)v_rs.get(i);
	    		 rs.add(rec);
	    	}
	    }
	    
	    StringBuffer loadpersonsql=new StringBuffer();
    	if("true".equalsIgnoreCase(isshowpersonname))
	    {
    		loadpersonsql.append("SELECT b.a0100, a.codeitemid, b.A0101, b.A0000 from vorganization a, ");
		    loadpersonsql.append(dbnames);
		    loadpersonsql.append("A01 b,t_vorg_staff c ");
		    loadpersonsql.append("where a.codeitemid='"+code+"' and c.B0110= a.codeitemid ");//  
		    loadpersonsql.append("  and c.state=1  and Upper(c.dbase)='"+dbnames.toUpperCase()+"' and c.A0100=b.a0100");	
		    loadpersonsql.append(" and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date ");
		    loadpersonsql.append(" and (a.view_chart<>1 or a.view_chart is null)");
	    }	    	
    	if(loadpersonsql!=null && loadpersonsql.length()>0)
	    {
	     	this.frowset = dao.search(loadpersonsql.toString());
	    	while(this.frowset.next())
	    	{
	    		LazyDynaBean personbean = new LazyDynaBean();
				personbean.set("grade", "");
				personbean.set("codeitemid", this.frowset.getString("a0100"));
				personbean.set("codeitemdesc", this.frowset.getString("a0101")!=null&&this.frowset.getString("a0101").length()>0?this.frowset.getString("a0101").replaceAll("\"",""):"");
				personbean.set("parentid", "parentid");
				personbean.set("childid", this.frowset.getString("a0100"));
				personbean.set("personcount", "0");
				personbean.set("infokind", "1");
				rs.add(personbean);
	    	}
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
