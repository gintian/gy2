package com.hjsj.hrms.transaction.pos.posreport;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 
 *<p>Title:SearchReportRelationsTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 11, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SearchReportRelationsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String code=(String)hm.get("code"); 
		String kind=(String)hm.get("kind");
		String dbnames=(String)hm.get("dbnames");
		String constant=(String)hm.get("constant");
		String namesinglecell=(String)hm.get("namesinglecell");
		String isshowpersonconut=(String)hm.get("isshowpersonconut");
		String isshowpersonname=(String)hm.get("isshowpersonname");
		String backdate = (String)hm.get("backdate");
		String isshowposup = (String)hm.get("isshowposup");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		StringBuffer loadpersonsql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String strwhere=getWhereINSql(this.userView,dbnames);
		String orgCodeitemid = "";    //权限控制    2014-11-2 wangchaoqun
		if(userView != null&&!userView.isSuper_admin())
	  	{
			orgCodeitemid = userView.getUnitPosWhereByPriv("org.CodeItemId");
		}
		try{
			if(code!=null && code.length()>0)
			{
				StringBuffer sqlstr=new StringBuffer();
				if("true".equalsIgnoreCase(isshowpersonconut) && dbnames!=null && dbnames.length()==3)
				{					
//					sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc  " + ConverDBsql(dbnames) + " as codeitemdesc,organization.childid,");
					
//					sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc  " + ConverDBsql(dbnames) + " as codeitemdesc,organization.parentid,organization.childid,");
//	                sqlstr.append(" organization.grade,count(");
//	                sqlstr.append(dbnames);
//	                sqlstr.append("A01.a0100) as personcount,'org' as infokind from ");
//					
//					sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc  " + ConverDBsql(dbnames) + " as codeitemdesc,");
//					sqlstr.append("(select top 1 K01.E01A1  from K01 K01, organization org    where K01.E01A1 = org.CodeItemId  ");
//					sqlstr.append(" and K01."+constant+" = (select K01.E01A1  from K01 K01, organization org  where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '"+code+"' )) as childid"  );
//					
					sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc  " + ConverDBsql(dbnames) + " as codeitemdesc,'"+code+"' as childid,");
					
					sqlstr.append("(select distinct K01."+constant+"  from K01 K01, organization org  ");
					sqlstr.append("where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '"+code+"' ) as parentid,");
					sqlstr.append(" organization.grade,");
//					sqlstr.append(" 1 as grade,");
//	                sqlstr.append(" 1 as personcount,'org' as infokind from ");
	                sqlstr.append(" organization.grade,count(");
	                sqlstr.append(dbnames);
	                sqlstr.append("A01.a0100) as personcount,'org' as infokind from ");
	                sqlstr.append(dbnames);
	                sqlstr.append("A01 RIGHT OUTER JOIN");
	                sqlstr.append(" organization ON ");
	                sqlstr.append(Sql_switcher.substr(dbnames + "A01.B0110","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid OR ");
					sqlstr.append(Sql_switcher.substr(dbnames + "A01.e0122","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid OR ");
					sqlstr.append(Sql_switcher.substr(dbnames + "A01.e01a1","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid");
					sqlstr.append(" where organization.codeitemid in (select K01.E01A1  from K01 K01, organization org  ");
//					if(dbserver.equalsIgnoreCase("oracle"))
//						sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" is null )");
//					else
					if(!"".equals(orgCodeitemid)){
						sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '"+code+"' and "+orgCodeitemid+" )");
					}else{
						sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '"+code+"' )");
					}
					
					sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
//					sqlstr.append(" where organization.codeitemid<>'");
//					sqlstr.append(code);
//					sqlstr.append("' and organization.parentid='");
//					sqlstr.append(code);
//					sqlstr.append("'");
	                sqlstr.append(" GROUP BY organization.codesetid,organization.A0000,organization.codeitemid,organization.codeitemdesc,organization.parentid,organization.childid,");
	                sqlstr.append(" organization.grade");    
	                sqlstr.append(" order by organization.codeitemid,organization.A0000");
				}
				else {
					
//					sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc  as codeitemdesc,organization.parentid,organization.childid,");
					sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc  as codeitemdesc,'"+code+"' as childid,");
					
					//解决岗位汇报关系图最下级节点依然有展开按钮问题  guodd 14-04-02
					sqlstr.append(" (select count(1) from k01 where "+constant+"=organization.codeitemid ) as childsnum,");
					
					sqlstr.append("(select distinct K01."+constant+"  from K01 K01, organization org  ");
					sqlstr.append("where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '"+code+"' ) as parentid,");
	                sqlstr.append(" organization.grade,count(");
	                sqlstr.append(dbnames);
	                sqlstr.append("A01.a0100) as personcount,'org' as infokind from ");
	                sqlstr.append(dbnames);
	                sqlstr.append("A01 RIGHT OUTER JOIN");
	                sqlstr.append(" organization ON ");
	                sqlstr.append(Sql_switcher.substr(dbnames + "A01.B0110","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid OR ");
					sqlstr.append(Sql_switcher.substr(dbnames + "A01.e0122","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid OR ");
					sqlstr.append(Sql_switcher.substr(dbnames + "A01.e01a1","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid");
					sqlstr.append(" where organization.codeitemid in (select K01.E01A1  from K01 K01, organization org  ");
//					if(dbserver.equalsIgnoreCase("oracle"))
//						sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" is null )");
//					else
					if(!"".equals(orgCodeitemid)){
						sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '"+code+"' and "+orgCodeitemid+" )");
					}else{
						sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '"+code+"' )");
					}
						sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
					//sqlstr.append("and "+dbnames+"A01.a0100 in(select a0100 "+strwhere+")");
//					sqlstr.append(" where organization.codeitemid<>'");
//					sqlstr.append(code);
//					sqlstr.append("' and organization.parentid='");
//					sqlstr.append(code);
//					sqlstr.append("'");
	                sqlstr.append(" GROUP BY organization.codesetid,organization.A0000,organization.codeitemid,organization.codeitemdesc,organization.parentid,organization.childid,");
	                sqlstr.append(" organization.grade");	
	                sqlstr.append(" order by organization.codeitemid,organization.A0000");
				
				}	
				//System.out.println(sqlstr.toString());
				ArrayList templist = new ArrayList();
//				List rs=templist;
			    List rs=ExecuteSQL.executeMyQuery(sqlstr.toString());
			    if(isshowposup!=null && "true".equals(isshowposup)){
				    for(int i=0;i<rs.size();i++){
				    	LazyDynaBean rec=(LazyDynaBean)rs.get(i);
				    	String codeitemid = (String)rec.get("codeitemid");
				    	String desc = getUnitAndDept(codeitemid);
				    	rec.set("codeitemdesc", desc+"/"+rec.get("codeitemdesc"));
				    }
			    }
			    if(!rs.isEmpty())
			    {
			   	  LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			   	  this.getFormHM().put("grades",rec.get("grade"));
//			   	  this.getFormHM().put("grades","1");
			    }		  
			    if("true".equalsIgnoreCase(isshowpersonname) && dbnames!=null && dbnames.length()==3)
			    {			    	
			        loadpersonsql.append("SELECT b.a0100, a.codeitemid, b.A0101, b.A0000 from organization a, ");
				    loadpersonsql.append(dbnames);
				    loadpersonsql.append("A01 b ");
				    loadpersonsql.append(" where (a.codeitemid='");
				    loadpersonsql.append(code);
				    loadpersonsql.append("')  and (((b.E0122= a.codeitemid) and ((b.E01A1 is null) or (b.e01a1=' ') or (" + Sql_switcher.trim("b.E01A1") + " =");
				    loadpersonsql.append("''))) or ");   // 有部门，无职位
				    loadpersonsql.append("(b.E01A1=a.codeitemid) or ");  // 有职位
				         // 有单位，无部门职位				    
				    loadpersonsql.append("((b.B0110=a.codeitemid) and ((b.E0122 is null) or (b.E0122=' ') or (" + Sql_switcher.trim("b.E0122") + "='')) and ((b.E01A1 is null) or (b.E01A1=' ') or  (" + Sql_switcher.trim("b.E01A1") + "='')))");
				    loadpersonsql.append(")");
				    sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date ");
				    
			    }
//			    System.out.println(loadpersonsql.toString());
			    if(loadpersonsql!=null && loadpersonsql.length()>0)
			    {
			     	this.frowset = dao.search(loadpersonsql.toString());
			    	while(this.frowset.next())
			    	{
			    		LazyDynaBean personbean = new LazyDynaBean();
						personbean.set("grade", "");
						personbean.set("codeitemid", this.frowset.getString("a0100"));
						personbean.set("codeitemdesc", this.frowset.getString("a0101")!=null?this.frowset.getString("a0101"):"");
						personbean.set("parentid", "parentid");
						personbean.set("childid", this.frowset.getString("a0100"));
						personbean.set("personcount", "0");
						personbean.set("infokind", "1");
						rs.add(personbean);
			    	}
			    }
//			    if(sqlstr!=null && sqlstr.length()>0)
//			    {
//			     	this.frowset = dao.search(sqlstr.toString());
//			    	while(this.frowset.next())
//			    	{
//			    		LazyDynaBean personbean = new LazyDynaBean();
//						personbean.set("grade", "1");
//						personbean.set("codeitemid", this.frowset.getString("codeitemid"));
//						personbean.set("codeitemdesc", this.frowset.getString("codeitemdesc")!=null?this.frowset.getString("codeitemdesc"):"");
//						personbean.set("parentid", "parentid");
//						personbean.set("childid", this.frowset.getString("codeitemid"));
//						personbean.set("personcount", "1");
//						personbean.set("infokind", "1");
//						rs.add(personbean);
//			    	}
//			    }

			    this.getFormHM().put("childslist",rs);
		    }
			else  // 根节点
			{

				StringBuffer sqlstr=new StringBuffer();
				if("true".equalsIgnoreCase(isshowpersonconut) && dbnames!=null && dbnames.length()==3)
				{					
					sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc  " + ConverDBsql(dbnames) + " as codeitemdesc,organization.parentid,0 as childid,");
//	                sqlstr.append(" organization.grade,count(");
//	                sqlstr.append(dbnames);
//	                sqlstr.append("A01.a0100) as personcount,'org' as infokind from ");
	                sqlstr.append(" organization.grade,");
//	                sqlstr.append(" 1 as grade,");
	                sqlstr.append(" 1 as personcount,'org' as infokind from ");
	                sqlstr.append(dbnames);
	                sqlstr.append("A01 RIGHT OUTER JOIN");
	                sqlstr.append(" organization ON ");
	                sqlstr.append(Sql_switcher.substr(dbnames + "A01.B0110","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid OR ");
					sqlstr.append(Sql_switcher.substr(dbnames + "A01.e0122","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid OR ");
					sqlstr.append(Sql_switcher.substr(dbnames + "A01.e01a1","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid");
					sqlstr.append(" where organization.codeitemid in (select K01.E01A1  from K01 K01, organization org  ");
					if(this.userView.isSuper_admin())
					{
						if(Sql_switcher.searchDbServer()== Constant.ORACEL)
							sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" is null )");
						else
							sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '' )");
					}
					else
					{
						/**
						 * cmq changed at 20121003 for 单位和岗位的权限范围控制规则变更
						 * 业务范围-操作单位-人员范围
						 */
						if(Sql_switcher.searchDbServer()== Constant.ORACEL)
						{
							//sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+"  not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and org.CodeItemId like '"+this.userView.getManagePrivCodeValue()+"%') and org.CodeItemId like '"+this.userView.getManagePrivCodeValue()+"%')");
							sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+"  not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and "+this.userView.getUnitPosWhereByPriv("org.CodeItemId")+") and "+this.userView.getUnitPosWhereByPriv("org.CodeItemId")+")");							
						}
						else
						{
							sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+"  not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and "+this.userView.getUnitPosWhereByPriv("org.CodeItemId")+") and "+this.userView.getUnitPosWhereByPriv("org.CodeItemId")+")");
						}
					}
					sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
					/*if(dbserver.equalsIgnoreCase("oracle"))						
						sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" is null )");
					else
						sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '' )");*/
//					sqlstr.append(code);
//					sqlstr.append("' and organization.parentid='");
//					sqlstr.append(code);
//					sqlstr.append("'");
	                sqlstr.append(" GROUP BY organization.codesetid,organization.A0000,organization.codeitemid,organization.codeitemdesc,organization.parentid,organization.childid,");
	                sqlstr.append(" organization.grade");    
	                sqlstr.append(" order by organization.codeitemid,organization.A0000");
				}
				else {
//					sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc  as codeitemdesc,organization.parentid,organization.childid,");
					sqlstr.append("select organization.codesetid,organization.codeitemid,organization.codeitemdesc  as codeitemdesc,organization.parentid,");
					sqlstr.append(" 0 as childid,");
					//解决岗位汇报关系图最下级节点依然有展开按钮问题  guodd 14-04-02
					sqlstr.append(" (select count(1) from k01 where "+constant+"=organization.codeitemid ) as childsnum,");
	                
	                sqlstr.append(" organization.grade,count(");
	                sqlstr.append(dbnames);
	                sqlstr.append("A01.a0100) as personcount,'org' as infokind from ");
	                sqlstr.append(dbnames);
	                sqlstr.append("A01 RIGHT OUTER JOIN");
	                sqlstr.append(" organization ON ");
	                sqlstr.append(Sql_switcher.substr(dbnames + "A01.B0110","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid OR ");
					sqlstr.append(Sql_switcher.substr(dbnames + "A01.e0122","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid OR ");
					sqlstr.append(Sql_switcher.substr(dbnames + "A01.e01a1","1",Sql_switcher.length("organization.codeitemid")));
					sqlstr.append("= organization.codeitemid");
//					sqlstr.append(" where organization.codeitemid in (select distinct k.e01a1 from k01 k) ");
					sqlstr.append(" where organization.codeitemid in (select K01.E01A1  from K01 K01, organization org  ");
					if(this.userView.isSuper_admin())
					{
						if(Sql_switcher.searchDbServer()== Constant.ORACEL)
							sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" is null )");
						else
							sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '' )");
					}
					else
					{
						/**
						 * cmq changed at 20121003 for 单位和岗位的权限范围控制规则变更
						 * 业务范围-操作单位-人员范围
						 */
						if(Sql_switcher.searchDbServer()== Constant.ORACEL)
						{
							//sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and org.CodeItemId like '"+this.userView.getManagePrivCodeValue()+"%') and org.CodeItemId like '"+this.userView.getManagePrivCodeValue()+"%')");
							sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and "+this.userView.getUnitPosWhereByPriv("org.CodeItemId")+") and "+this.userView.getUnitPosWhereByPriv("org.CodeItemId")+")");
						}
						else
						{
							//sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and org.CodeItemId like '"+this.userView.getManagePrivCodeValue()+"%') and org.CodeItemId like '"+this.userView.getManagePrivCodeValue()+"%')");
							sqlstr.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and "+this.userView.getUnitPosWhereByPriv("org.CodeItemId")+") and "+this.userView.getUnitPosWhereByPriv("org.CodeItemId")+")");							
						}
					}
					sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
//					sqlstr.append(code);
//					sqlstr.append("' and organization.parentid='");
//					sqlstr.append(code);
//					sqlstr.append("'");
	                sqlstr.append(" GROUP BY organization.codesetid,organization.A0000,organization.codeitemid,organization.codeitemdesc,organization.parentid,organization.childid,");
	                sqlstr.append(" organization.grade");	
	                sqlstr.append(" order by organization.codeitemid,organization.A0000");
				
				}	
//				System.out.println(sqlstr.toString());
			    List rs=ExecuteSQL.executeMyQuery(sqlstr.toString());
			    
			    if(isshowposup!=null && "true".equals(isshowposup)){
				    for(int i=0;i<rs.size();i++){
				    	LazyDynaBean rec=(LazyDynaBean)rs.get(i);
				    	String codeitemid = (String)rec.get("codeitemid");
				    	String desc = getUnitAndDept(codeitemid);
				    	rec.set("codeitemdesc", desc+"/"+rec.get("codeitemdesc"));
				    }
			    }
			    
			    if(!rs.isEmpty())
			    {
			   	  LazyDynaBean rec=(LazyDynaBean)rs.get(0);
//			   	  this.getFormHM().put("grades",rec.get("grade"));
			   	  this.getFormHM().put("grades","1");
			    }		  
			    if("true".equalsIgnoreCase(isshowpersonname) && dbnames!=null && dbnames.length()==3)
			    {			    	
			        loadpersonsql.append("SELECT b.a0100, a.codeitemid, b.A0101, b.A0000 from organization a, ");
				    loadpersonsql.append(dbnames);
				    loadpersonsql.append("A01 b ");
				    loadpersonsql.append(" where (a.codeitemid='");
				    loadpersonsql.append(code);
				    loadpersonsql.append("')  and (((b.E0122= a.codeitemid) and ((b.E01A1 is null) or (b.e01a1=' ') or (" + Sql_switcher.trim("b.E01A1") + " =");
				    loadpersonsql.append("''))) or ");   // 有部门，无职位
				    loadpersonsql.append("(b.E01A1=a.codeitemid) or ");  // 有职位
				         // 有单位，无部门职位
				    
				    loadpersonsql.append("((b.B0110=a.codeitemid) and ((b.E0122 is null) or (b.E0122=' ') or (" + Sql_switcher.trim("b.E0122") + "='')) and ((b.E01A1 is null) or (b.E01A1=' ') or  (" + Sql_switcher.trim("b.E01A1") + "='')))");
				    loadpersonsql.append(")");
				    sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date ");
			    }
//			    if(loadpersonsql!=null && loadpersonsql.length()>0)
//			    {
//			     	this.frowset = dao.search(loadpersonsql.toString());
//			    	while(this.frowset.next())
//			    	{
//			    		LazyDynaBean personbean = new LazyDynaBean();
//						personbean.set("grade", "");
//						personbean.set("codeitemid", this.frowset.getString("a0100"));
//						personbean.set("codeitemdesc", this.frowset.getString("a0101")!=null?this.frowset.getString("a0101"):"");
//						personbean.set("parentid", "parentid");
//						personbean.set("childid", this.frowset.getString("a0100"));
//						personbean.set("personcount", "0");
//						personbean.set("infokind", "1");
//						rs.add(personbean);
//			    	}
//			    }

			    this.getFormHM().put("childslist",rs);
		    
			}
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
	/**
	 * 权限控制
	 * @param userView
	 * @param userbase
	 * @return
	 */
	public static String getWhereINSql(UserView userView,String userbase){
		 String strwhere="";	 
		 String kind="";
		 if(!userView.isSuper_admin())
		 {
		           String expr="1";
		           String factor="";
				if("UN".equals(userView.getManagePrivCode()))
				{
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`B0110=`";
					  expr="1+2";
					}
				}
				else if("UM".equals(userView.getManagePrivCode()))
				{
					factor="E0122="; 
				    kind="1";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E0122=`";
					  expr="1+2";
					}
				}
				else if("@K".equals(userView.getManagePrivCode()))
				{
					factor="E01A1=";
					kind="0";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					{
						  factor+=userView.getManagePrivCodeValue();
						  factor+="%`";
					}
					else
					{
					  factor+="%`E01A1=`";
					  expr="1+2";
					}
				}
				else
				{
					 expr="1+2";
					factor="B0110=";
				    kind="2";
					if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
						factor+=userView.getManagePrivCodeValue();
					factor+="%`B0110=`";
				}			
				 ArrayList fieldlist=new ArrayList();
			     try
			     {        
			        	strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);	        	
			     }catch(Exception e){
			          e.printStackTrace();	
			     }
			    /*if(strwhere!=null&&strwhere.length()>0)
			    {
			    	strwhere=strwhere.substring(strwhere.indexOf("WHERE")+5);  
			    }	*/		    	      
		}else{
			StringBuffer wheresql=new StringBuffer();
			wheresql.append(" from ");
			wheresql.append(userbase);
			wheresql.append("A01 ");
			kind="2";
			strwhere=wheresql.toString();
		}		
		 return strwhere; 
	}
	
	private String getUnitAndDept(String posId){
		
		String codeitemid = "";
		String codesetid = "";
		String codeitemdesc = "";
		String sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid = (select e0122 from k01 where e01a1='"+posId+"')";
		try{
			ContentDAO dao = new ContentDAO(frameconn);
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				codeitemid = this.frowset.getString("codeitemid");
				codesetid = this.frowset.getString("codesetid");
				codeitemdesc = this.frowset.getString("codeitemdesc");
				
				if("UM".equals(codesetid)){
					sql = "select codeitemdesc from organization where codesetid='UN' and  codeitemid ="+Sql_switcher.substr("'"+codeitemid+"'", "1", Sql_switcher.length("codeitemid"))+"order by codeitemid desc";
					this.frowset = dao.search(sql);
					if(this.frowset.next())
						codeitemdesc = this.frowset.getString("codeitemdesc")+"/"+codeitemdesc;
					
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return codeitemdesc;
	}
}
