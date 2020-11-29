package com.hjsj.hrms.transaction.pos.posreport;

import com.hjsj.hrms.businessobject.general.orgmap.ParameterBo;
import com.hjsj.hrms.businessobject.general.orgmap.ReportRelationMap;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 12, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class CreateReportMapPDFTrans extends IBusiness {

	private float sortnumber;
	public void execute() throws GeneralException 
	{
		    String code=(String)this.getFormHM().get("code");
		    String constant=(String)this.getFormHM().get("constant");
			if(code==null || code.trim().length()==0)
				code=userView.getManagePrivCodeValue();
			ReportRelationMap orgmapbo=new ReportRelationMap();
			StringBuffer sql=new StringBuffer();
			String dbnameini="usr";
			ArrayList Dblist=userView.getPrivDbList();
			if(Dblist!=null&&Dblist.size()>0)
				dbnameini=Dblist.get(0).toString();
			else{
				dbnameini="";
			}
			HashMap paramehashmap=new SetOrgOptionParameter().ReadOutParameterXml("POS_MAPOPTION",true,dbnameini,userView); 
			ParameterBo parameterbo=new ParameterBo();
			parameterbo.setCellaspect(paramehashmap.get("cellaspect").toString());
			parameterbo.setCellcolor(paramehashmap.get("cellcolor").toString());
			parameterbo.setCellheight(paramehashmap.get("cellheight").toString());
			parameterbo.setCellhspacewidth(paramehashmap.get("cellhspacewidth").toString());
			parameterbo.setCellletteraligncenter((String)paramehashmap.get("cellletteraligncenter"));
			parameterbo.setCellletteralignleft((String)paramehashmap.get("cellletteralignleft"));
			parameterbo.setCellletteralignright((String)paramehashmap.get("cellletteralignright"));
			parameterbo.setCellletterfitline((String)paramehashmap.get("cellletterfitline"));
			parameterbo.setCellletterfitsize((String)paramehashmap.get("cellletterfitsize"));
			parameterbo.setCelllettervaligncenter((String)paramehashmap.get("celllettervaligncenter"));
			parameterbo.setCelllinestrokewidth((String)paramehashmap.get("celllinestrokewidth"));
			parameterbo.setCellshape((String)paramehashmap.get("cellshape"));
			parameterbo.setCellvspacewidth((String)paramehashmap.get("cellvspacewidth"));
			parameterbo.setCellwidth((String)paramehashmap.get("cellwidth"));
			parameterbo.setFontcolor((String)paramehashmap.get("fontcolor"));
			parameterbo.setFontfamily((String)paramehashmap.get("fontfamily"));
			parameterbo.setFontsize((String)paramehashmap.get("fontsize"));
			parameterbo.setFontstyle((String)paramehashmap.get("fontstyle"));
			parameterbo.setGraph3d((String)paramehashmap.get("graph3d"));
			parameterbo.setGraphaspect((String)paramehashmap.get("graphaspect"));
			parameterbo.setIsshowpersonconut((String)paramehashmap.get("isshowpersonconut"));
			parameterbo.setIsshowpersonname((String)paramehashmap.get("isshowpersonname"));
			parameterbo.setNamesinglecell((String)paramehashmap.get("namesinglecell"));
			parameterbo.setDbname((String)paramehashmap.get("dbnames"));
			String dbname=paramehashmap.get("dbnames").toString();
			if(dbname==null||dbname.length()<=0)
			{
				paramehashmap.put("isshowpersonconut","false");
				paramehashmap.put("isshowpersonconut","isshowpersonname");
			}			
			String url="";
			String table=this.userView.getUserName()+this.userView.getUserName()+"organization";
			ContentDAO dao=new ContentDAO(this.getFrameconn());			
			sql.append("select a0000 from organization where A0000 is null or A0000 ='' or  EXISTS(select A0000 from organization group by A0000 having count(A0000)>1)");
			List rs1=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
			sql.delete(0,sql.length());
			//if(!rs1.isEmpty() && rs1.size()>0){
			createTempTable(table,constant,code,dao);//建立数据表	
			boolean isCode=isCode_grade(table,code,dao);
			String rootdesc=getCodeMess(table,code,dao);
			if(!isCode)
				code="";
			if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
			{
				/********显示人数,显示姓名***********/
				insertTempEmp(table,dao,dbname,code);
				    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
					parameterbo.setPageheight(pagesize[1]);
					parameterbo.setPagespaceheight(40);
					parameterbo.setPagespacewidth(40);
					parameterbo.setPagewidth(pagesize[0]);
				    
					if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
					{
						url=orgmapbo.createReportMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
						
					}else
					{
			            sql.delete(0,sql.length());
						sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
						if(code!=null && code.length()>0)
						{
							sql.append(" - ");
							sql.append("(select grade from ");							
							sql.append(table+" where codeitemid_n='");
							sql.append(code);
							sql.append("') as grade");
						}			
						sql.append(", A0000,");
						sql.append("(SELECT COUNT(*) FROM ");						
						sql.append(table+" b WHERE b.parentid_n = a.codeitemid_n ");						
						sql.append(" AND b.codeitemid_n = b.childid_n and b.codesetid='zz') +  (SELECT COUNT(*)");
						sql.append(" FROM ");						
						sql.append(table+" org WHERE (codesetid='zz' and codeitemid_n <> childid_n) AND (org.codeitemid_n = a.codeitemid_n ");
					    sql.append(") AND (NOT EXISTS (SELECT * FROM ");						
						sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS personcount,");
						sql.append("(SELECT COUNT(*) FROM ");						
						sql.append(table+" b WHERE b.parentid_n LIKE a.codeitemid_n ");	
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND b.codeitemid_n = b.childid_n)-1 +  (SELECT COUNT(*)");
				        sql.append(" FROM ");				        
				        sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE a.codeitemid_n ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");				        
				        sql.append(table+" orge WHERE orge.codeitemid_n = org.childid_n))) AS leafagechilds,(SELECT COUNT(*) FROM ");
				        sql.append(table+" c WHERE a.codeitemid_n = c.parentid_n AND c.codeitemid_n <> c.parentid_n) AS childs,'ren' AS subhead,");
						sql.append("(SELECT COUNT(*) FROM ");						
						sql.append(table+" d WHERE d.parentid_n like (SELECT MIN(e.codeitemid_n) FROM ");						
						sql.append(table+" e");
						sql.append(" WHERE   e.parentid_n = a.codeitemid_n AND e.codeitemid_n <> e.parentid_n and e.codeitemid_n<>a.codeitemid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND  a.codesetid<>'zz' and d.parentid_n <> d.codeitemid_n AND d.childid_n = d.codeitemid_n)  +");
				        sql.append(" (SELECT COUNT(*) FROM ");				       
				        sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MIN(codeitemid_n) FROM ");
				        sql.append(table+" m WHERE   m.parentid_n = a.codeitemid_n AND m.codeitemid_n <> m.parentid_n and m.codeitemid_n<>a.codeitemid_n) ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");				        
				        sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS firstchildscount,");
						sql.append("(SELECT COUNT(*) FROM ");						
						sql.append(table+" f WHERE f.parentid_n like (SELECT MAX(g.codeitemid_n) FROM ");						
						sql.append(table+" g");
						sql.append(" WHERE g.parentid_n = a.codeitemid_n AND g.codeitemid_n <> g.parentid_n and g.codeitemid_n<>a.codeitemid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND a.codesetid<>'zz' and  f.parentid_n <> f.codeitemid_n AND f.childid_n = f.codeitemid_n) +");
				        sql.append(" (SELECT COUNT(*) FROM ");				        
				        sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MAX(codeitemid_n) FROM ");
				        sql.append(table+" n WHERE  n.parentid_n = a.codeitemid_n AND n.codeitemid_n <> n.parentid_n and n.codeitemid_n<>a.codeitemid_n) ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");				        
				        sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS lastchildscount,");
						sql.append(" (SELECT COUNT(*) FROM ");						
						sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n = a.codeitemid_n) AND (NOT EXISTS (SELECT * FROM ");
						sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS errorchilds");
						sql.append(" FROM ");						
						sql.append(table+" a order by a.codeitemid_n,a.codesetid");		
						List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
						sql.delete(0,sql.length());
						sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts AS childs,");
						sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
						sql.append("(SELECT COUNT(*) counts from ");						
						sql.append(table+") aa,(SELECT COUNT(*) counts FROM ");						
						sql.append(table+" b WHERE b.parentid_n LIKE '%' AND b.codeitemid_n = b.childid_n) bb,");
						sql.append("(SELECT COUNT(*) as counts FROM ");						
						sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE '%') AND");
						sql.append(" (NOT EXISTS (SELECT * FROM ");						
						sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) cc,");
						sql.append("(SELECT COUNT(*) counts FROM ");						
						sql.append(table+" c  WHERE c.codeitemid_n = c.parentid_n) dd,");
						sql.append("(SELECT COUNT(*) counts");
						sql.append(" FROM ");						
						sql.append(table+" d WHERE d.parentid_n LIKE (SELECT MIN(e.codeitemid_n) FROM ");						
						sql.append(table+" e WHERE e.codesetid<>'zz' and e.parentid_n = e.codeitemid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND");
						sql.append(" d.parentid_n <> d.codeitemid_n AND d.childid_n = d.codeitemid_n) ae,");
						sql.append("(SELECT COUNT(*) counts FROM ");						
						sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE ");
						sql.append(" (SELECT MIN(codeitemid_n) FROM ");						
						sql.append(table+" m WHERE m.codesetid<>'zz' and m.parentid_n = m.codeitemid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%') AND  (NOT EXISTS (SELECT *");
						sql.append(" FROM ");						
						sql.append(table+" orge  WHERE orge.codeItemId_n = org.childId_n))) ad,");
						sql.append("(SELECT COUNT(*) counts FROM ");						
						sql.append(table+" f WHERE f.parentid_n LIKE (SELECT MAX(g.codeitemid_n) FROM ");
						sql.append(table+" g WHERE g.codesetid<>'zz' and g.codeitemid_n = g.parentid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND f.parentid_n <> f.codeitemid_n AND f.childid_n = f.codeitemid_n) be,");
						sql.append("(SELECT COUNT(*) counts");
						sql.append(" FROM ");						
						sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MAX(codeitemid_n) FROM ");
						sql.append(table+" n WHERE n.codesetid<>'zz' and n.codeitemid_n = n.parentid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%') AND (NOT EXISTS");
						sql.append(" (SELECT * FROM ");
					
						sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) bd,");
						sql.append("(SELECT COUNT(*) counts FROM ");						
						sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n = org.parentid_n) AND (NOT EXISTS");
						sql.append(" (SELECT * FROM ");						
						sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) ax");
						List rootnode=null;
						if(code==null || code.length()<=0)
						{
							rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
						}						
						if(rootdesc==null||rootdesc.length()<=0)
						{
							rootdesc="汇报关系";
						} 
						url=orgmapbo.createReportMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
					}
			}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
			{
				/************显示姓名***********/
				    insertTempEmp(table,dao,dbname,code);
				    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
					parameterbo.setPageheight(pagesize[1]);
					parameterbo.setPagespaceheight(40);
					parameterbo.setPagespacewidth(40);
					parameterbo.setPagewidth(pagesize[0]);
					if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
					{
						url=orgmapbo.createReportMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
					}else
					{
					    
		                sql.delete(0,sql.length());
						sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
						if(code!=null && code.length()>0)
						{
							sql.append(" - ");
							sql.append("(select grade from ");							
							sql.append(table+" where codeitemid_n='");
							sql.append(code);
							sql.append("') as grade");
						}			
						sql.append(", A0000,(SELECT COUNT(*) FROM ");						
						sql.append(table+" b WHERE b.parentid_n LIKE a.codeitemid_n ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND b.codeitemid_n = b.childid_n)-1 +  (SELECT COUNT(*)");
				        sql.append(" FROM ");				        
				        sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE a.codeitemid_n ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");				        
				        sql.append(table+" orge WHERE orge.codeitemid_n = org.childid_n))) AS leafagechilds,(SELECT COUNT(*) FROM ");
				        sql.append(table+" c WHERE a.codeitemid_n = c.parentid_n AND c.codeitemid_n <> c.parentid_n) AS childs,'ren' AS subhead,");
						sql.append("(SELECT COUNT(*) FROM ");						
						sql.append(table+" d WHERE d.parentid_n like (SELECT MIN(e.codeitemid_n) FROM ");
						sql.append(table+" e");
						sql.append(" WHERE   e.parentid_n = a.codeitemid_n AND e.codeitemid_n <> e.parentid_n and e.codeitemid_n<>a.codeitemid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND  a.codesetid<>'zz' and d.parentid_n <> d.codeitemid_n AND d.childid_n = d.codeitemid_n)  +");
				        sql.append(" (SELECT COUNT(*) FROM ");				        
				        sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MIN(codeitemid_n) FROM ");
				        sql.append(table+" m WHERE  m.parentid_n = a.codeitemid_n AND m.codeitemid_n <> m.parentid_n and m.codeitemid_n<>a.codeitemid_n) ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
				        sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS firstchildscount,");
						sql.append("(SELECT COUNT(*) FROM ");
						sql.append(table+" f WHERE f.parentid_n like (SELECT MAX(g.codeitemid_n) FROM ");
						sql.append(table+" g");
						sql.append(" WHERE  g.parentid_n = a.codeitemid_n AND g.codeitemid_n <> g.parentid_n and g.codeitemid_n<>a.codeitemid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND  a.codesetid<>'zz' and  f.parentid_n <> f.codeitemid_n AND f.childid_n = f.codeitemid_n) +");
				        sql.append(" (SELECT COUNT(*) FROM ");				       
				        sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MAX(codeitemid_n) FROM ");
				        sql.append(table+" n WHERE  n.parentid_n = a.codeitemid_n AND n.codeitemid_n <> n.parentid_n and n.codeitemid_n<>a.codeitemid_n) ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
				        sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS lastchildscount,");
						sql.append(" (SELECT COUNT(*) FROM ");						
						sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n = a.codeitemid_n) AND (NOT EXISTS (SELECT * FROM ");
						sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS errorchilds");
						sql.append(" FROM ");						
						sql.append(table+" a order by a.codeitemid_n,a.codesetid");						
						List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
						sql.delete(0,sql.length());
						sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.counts AS childs,'ren' AS subhead,");
						sql.append("ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts as  lastchildscount,ax.counts AS errorchilds from ");
						sql.append("(SELECT COUNT(*) AS counts from ");						
						sql.append(table+") aa,");
						sql.append("(SELECT COUNT(*) counts FROM ");						
						sql.append(table+" b WHERE b.parentid_n LIKE '%' AND b.codeitemid_n = b.childid_n) bb,");
						sql.append("(SELECT COUNT(*) as counts FROM ");						
						sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE '%') AND");
						sql.append(" (NOT EXISTS (SELECT * FROM ");						
						sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) cc,(SELECT COUNT(*) counts FROM ");
						sql.append(table+" c  WHERE c.codeitemid_n = c.parentid_n) dd, (SELECT COUNT(*) counts");
						sql.append(" FROM ");						
						sql.append(table+" d WHERE d.parentid_n LIKE (SELECT MIN(e.codeitemid_n) FROM ");
						sql.append(table+" e WHERE e.codesetid<>'zz' and e.parentid_n = e.codeitemid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND");
						sql.append(" d.parentid_n <> d.codeitemid_n AND d.childid_n = d.codeitemid_n) ae,");
						sql.append("(SELECT COUNT(*) as counts FROM ");
						sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE ");
						sql.append(" (SELECT MIN(codeitemid_n) FROM ");						
						sql.append(table+" m WHERE m.codesetid<>'zz' and m.parentid_n = m.codeitemid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%') AND  (NOT EXISTS (SELECT *");
						sql.append(" FROM ");						
						sql.append(table+" orge  WHERE orge.codeItemId_n = org.childId_n))) ad,(SELECT COUNT(*) as counts FROM ");
						sql.append(table+" f WHERE f.parentid_n LIKE (SELECT MAX(g.codeitemid_n) FROM ");
						sql.append(table+" g WHERE g.codesetid<>'zz' and g.codeitemid_n = g.parentid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND f.parentid_n <> f.codeitemid_n AND f.childid_n = f.codeitemid_n) be,");
						sql.append("(SELECT COUNT(*) counts");
						sql.append(" FROM ");
						sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MAX(codeitemid_n) FROM ");
						sql.append(table+" n WHERE n.codesetid<>'zz' and n.codeitemid_n = n.parentid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%') AND (NOT EXISTS");
						sql.append(" (SELECT * FROM ");						
						sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) bd,");
						sql.append("(SELECT COUNT(*) as counts FROM ");
						sql.append(table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n = org.parentid_n) AND (NOT EXISTS");
						sql.append(" (SELECT * FROM ");						
						sql.append(table+" orge WHERE orge.codeItemId_n = org.childId_n))) ax");
						List rootnode=null;
						if(code==null || code.length()<=0||!isCode_grade(table,code,dao))
						      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
						if(rootdesc==null||rootdesc.length()<=0)
						{
							rootdesc="汇报关系";
						} 
						url=orgmapbo.createReportMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
						
					}	
			}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
			{
				/************显示人数的***********/
				int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
				  parameterbo.setPageheight(pagesize[1]);
				  parameterbo.setPagespaceheight(40);
				  parameterbo.setPagespacewidth(40);
				  parameterbo.setPagewidth(pagesize[0]);	
				  if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
				  {
						url=orgmapbo.createReportMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
				  }else
				  {
					  sql.append("SELECT codesetid, codeitemid, codeitemdesc  " + orgmapbo.ConverDBsql(dbname) + " AS text, parentid, childid, a.state, grade");
						if(code!=null && code.length()>0)
						{
							sql.append(" - ");
							sql.append("(select grade from ");							
							sql.append(table+" where codeitemid_n='");
							sql.append(code);
							sql.append("') as grade");
						}			
						sql.append(",(SELECT COUNT(*) FROM "+table+" b WHERE b.parentid_n LIKE a.codeitemid_n ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND b.codeitemid_n = b.childid_n)-1 +  (SELECT COUNT(*)");
				        sql.append(" FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE a.codeitemid_n ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+table+" orge WHERE orge.codeitemid_n = org.childid_n))) AS leafagechilds,(SELECT COUNT(*) FROM "+table+" c WHERE a.codeitemid_n = c.parentid_n AND c.codeitemid_n <> c.parentid_n) AS childs,'ren' AS subhead,");
						sql.append("(SELECT COUNT(*) FROM "+table+" d WHERE d.parentid_n like (SELECT MIN(e.codeitemid_n) FROM "+table+" e");
						sql.append(" WHERE e.parentid_n = a.codeitemid_n AND e.codeitemid_n <> e.parentid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND d.parentid_n <> d.codeitemid_n AND d.childid_n = d.codeitemid_n)  +");
				        sql.append(" (SELECT COUNT(*) FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MIN(codeitemid_n) FROM "+table+" m WHERE m.parentid_n = a.codeitemid_n AND m.codeitemid_n <> m.parentid_n) ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS firstchildscount,");
						sql.append("(SELECT COUNT(*) FROM "+table+" f WHERE f.parentid_n like (SELECT MAX(g.codeitemid_n) FROM "+table+" g");
						sql.append(" WHERE g.parentid_n = a.codeitemid_n AND g.codeitemid_n <> g.parentid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND f.parentid_n <> f.codeitemid_n AND f.childid_n = f.codeitemid_n) +");
				        sql.append(" (SELECT COUNT(*) FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MAX(codeitemid_n) FROM "+table+" n WHERE n.parentid_n = a.codeitemid_n AND n.codeitemid_n <> n.parentid_n) ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS lastchildscount,");
						sql.append(" (SELECT COUNT(*) FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n = a.codeitemid_n) AND (NOT EXISTS (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS errorchilds");
						sql.append(" From ");
						sql.append(dbname);
						sql.append("A01 RIGHT OUTER JOIN");
						sql.append(" "+table+" a ON ");
						sql.append(Sql_switcher.substr(dbname + "A01.B0110","1",Sql_switcher.length("a.codeitemid"))); 	              
						sql.append("= a.codeitemid OR ");
						sql.append(Sql_switcher.substr(dbname + "A01.e0122","1",Sql_switcher.length("a.codeitemid"))); 	    
						sql.append("= a.codeitemid OR ");
						sql.append(Sql_switcher.substr(dbname + "A01.e01a1","1",Sql_switcher.length("a.codeitemid"))); 	    
						sql.append("= a.codeitemid");
						/*sql.append(" where a.codeitemid like '");
						sql.append(code+"%'");*/
						sql.append(" GROUP BY a.codesetid,a.codeitemid,a.codeitemdesc,a.parentid,a.childid");
						sql.append(",a.codeitemid_n,a.parentid_n,a.childid_n,a.state, a.grade");	
						sql.append(" order by a.codeitemid_n");
	                    //System.out.println(sql.toString());
						List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
						sql.delete(0,sql.length());
						
						sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
						sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
						sql.append("(SELECT COUNT(*) AS nodecount from "+table+" ");
						//sql.append(" where codeitemid like '"+code+"%'");
						sql.append(") aa,");
						sql.append("(SELECT COUNT(*) as counts FROM "+table+" b WHERE b.parentid_n LIKE '%' AND b.codeitemid_n = b.childid_n ");
						//sql.append(" and b.codeitemid like '"+code+"%'");
						sql.append(") bb,");
						sql.append("(SELECT COUNT(*) as counts FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid LIKE '%') AND");
						sql.append(" (NOT EXISTS (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n)) ");
						//sql.append(" and org.codeitemid like '"+code+"%'");
						sql.append(") cc,");
						sql.append("(SELECT COUNT(*) as counts FROM "+table+" c  WHERE c.codeitemid_n = c.parentid_n ");
						//sql.append(" and c.codeitemid like '"+code+"%'");
						sql.append(") dd,");
						sql.append("(SELECT COUNT(*) as counts ");
						sql.append(" FROM "+table+" d WHERE d.parentid_n LIKE (SELECT MIN(e.codeitemid_n) FROM "+table+" e WHERE e.parentid_n = e.codeitemid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append(" '%' AND");
						sql.append(" d.parentid_n <> d.codeitemid_n AND d.childid_n = d.codeitemid_n ");
						//sql.append(" and d.codeitemid like '"+code+"%'");
						sql.append(") ae,");
						sql.append("(SELECT COUNT(*) as counts FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE ");
						sql.append(" (SELECT MIN(codeitemid_n) FROM "+table+" m WHERE m.parentid_n = m.codeitemid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%') AND  (NOT EXISTS (SELECT *");
						sql.append(" FROM "+table+" orge  WHERE orge.codeItemId_n = org.childId_n)) ");
						//sql.append(" and org.codeitemid like '"+code+"%'");
						sql.append(") ad,");
						sql.append("(SELECT COUNT(*)  as counts FROM "+table+" f WHERE f.parentid_n LIKE ");
						sql.append("(SELECT MAX(g.codeitemid_n) FROM "+table+" g WHERE g.codeitemid_n = g.parentid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND f.parentid_n <> f.codeitemid_n AND f.childid_n = f.codeitemid_n  ");
						//sql.append(" and f.codeitemid_n like '"+code+"%'");
						sql.append(") be,");
						sql.append("(SELECT COUNT(*) as counts");
						sql.append(" FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MAX(codeitemid_n) FROM "+table+" n WHERE n.codeitemid_n = n.parentid_n) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%') AND (NOT EXISTS");
						sql.append(" (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n)) ");
						//sql.append(" and org.codeitemid like '"+code+"%'");
						sql.append(") bd,");
						sql.append("(SELECT COUNT(*) as counts FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n = org.parentid_n) AND (NOT EXISTS");
						sql.append(" (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n)) ");
						//sql.append(" and org.codeitemid like '"+code+"%'");
						sql.append(") ce");				       		
						List rootnode=null;
						if(code==null || code.length()<=0)
						      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
						if(rootdesc==null||rootdesc.length()<=0)
						{
							rootdesc="汇报关系";
						} 
						url=orgmapbo.createReportMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
				  }
			 }else
			 {
				try{
					  int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
					  parameterbo.setPageheight(pagesize[1]);
					  parameterbo.setPagespaceheight(40);
					  parameterbo.setPagespacewidth(40);
					  parameterbo.setPagewidth(pagesize[0]);	
					  if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
					  {
							url=orgmapbo.createReportMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
							
					  }else
					  {
						    sql.delete(0,sql.length());
							sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");							
								sql.append(table+" where codeitemid_n='");
								sql.append(code);
								sql.append("') as grade");
							}		
							sql.append(", A0000,(SELECT COUNT(*) FROM "+table+" b WHERE b.parentid_n LIKE a.codeitemid_n ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid_n = b.childid_n)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE a.codeitemid_n ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+table+" orge WHERE orge.codeitemid_n = org.childid_n))) AS leafagechilds,");//-----最下面多少个的孩子
					        sql.append("(SELECT COUNT(*) FROM "+table+" c WHERE a.codeitemid_n = c.parentid_n AND c.codeitemid_n <> c.parentid_n) AS childs,");//---自己直接有几个的孩子数
					        sql.append("'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM "+table+" d WHERE d.parentid_n like (SELECT MIN(e.codeitemid_n) FROM "+table+" e");
							sql.append(" WHERE e.parentid_n = a.codeitemid_n AND e.codeitemid_n <> e.parentid_n) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND d.parentid_n <> d.codeitemid_n AND d.childid_n = d.codeitemid_n)  +");
					        sql.append(" (SELECT COUNT(*) FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MIN(codeitemid_n) FROM "+table+" m WHERE m.parentid_n = a.codeitemid_n AND m.codeitemid_n <> m.parentid_n) ");//最小孩子的叶子节点数
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS firstchildscount,");//---第一个孩子，最外面有多少的叶子数
							sql.append("(SELECT COUNT(*) FROM "+table+" f WHERE f.parentid_n like (SELECT MAX(g.codeitemid_n) FROM "+table+" g");
							sql.append(" WHERE g.parentid_n = a.codeitemid_n AND g.codeitemid_n <> g.parentid_n) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid_n <> f.codeitemid_n AND f.childid_n = f.codeitemid_n) +");
					        sql.append(" (SELECT COUNT(*) FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MAX(codeitemid_n) FROM "+table+" n WHERE n.parentid_n = a.codeitemid_n AND n.codeitemid_n <> n.parentid_n) ");//最大孩子的叶子节点数
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS lastchildscount,");//---最后一个孩子，最外面多少的叶子数
							sql.append(" (SELECT COUNT(*) FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n = a.codeitemid_n) AND (NOT EXISTS (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n))) AS errorchilds");
							sql.append(" FROM "+table+" a ");
							sql.append(" where a.codeitemid_n like '"+code+"%'");
							sql.append(" order by a.codeitemid_n");
						     //System.out.println(sql.toString());
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());								
							sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
							sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
							sql.append("(SELECT COUNT(*) AS nodecount from "+table+" ");
							//sql.append(" where codeitemid_n like '"+code+"%'");
							sql.append(") aa,");
							sql.append("(SELECT COUNT(*) as counts FROM "+table+" b WHERE b.parentid_n LIKE '%' AND b.codeitemid_n = b.childid_n ");
							//sql.append("and b.codeitemid_n like '"+code+"%'");
							sql.append(") bb,");
							sql.append("(SELECT COUNT(*) as counts FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n)) ");
							//sql.append("and org.codeitemid_n like '"+code+"%'");
							sql.append(") cc,");
							sql.append("(SELECT COUNT(*) as counts FROM "+table+" c  WHERE c.codeitemid_n = c.parentid_n ");
							//sql.append("and c.codeitemid_n like '"+code+"%'");
							sql.append(") dd,");
							sql.append("(SELECT COUNT(*) as counts ");
							sql.append(" FROM "+table+" d WHERE d.parentid_n LIKE (SELECT MIN(e.codeitemid_n) FROM "+table+" e WHERE e.parentid_n = e.codeitemid_n) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND");
							sql.append(" d.parentid_n <> d.codeitemid_n AND d.childid_n = d.codeitemid_n ");
							//sql.append("and d.codeitemid_n like '"+code+"%'");
							sql.append(") ae,");
							sql.append("(SELECT COUNT(*) as counts FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE ");
							sql.append(" (SELECT MIN(codeitemid_n) FROM "+table+" m WHERE m.parentid_n = m.codeitemid_n) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM "+table+" orge  WHERE orge.codeItemId_n = org.childId_n)) ");
							//sql.append(" and org.codeitemid_n like '"+code+"%'");
							sql.append(") ad,");
							sql.append("(SELECT COUNT(*)  as counts FROM "+table+" f WHERE f.parentid_n LIKE ");
							sql.append("(SELECT MAX(g.codeitemid_n) FROM "+table+" g WHERE g.codeitemid_n = g.parentid_n) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid_n <> f.codeitemid_n AND f.childid_n = f.codeitemid_n ");
							//sql.append("and f.codeitemid_n like '"+code+"%'");
							sql.append(") be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n LIKE (SELECT MAX(codeitemid_n) FROM "+table+" n WHERE n.codeitemid_n = n.parentid_n) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n)) ");
							sql.append(" and org.codeitemid_n like '"+code+"%'");
							sql.append(") bd,");
							sql.append("(SELECT COUNT(*) as counts FROM "+table+" org WHERE (codeitemid_n <> childid_n) AND (org.codeitemid_n = org.parentid_n) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM "+table+" orge WHERE orge.codeItemId_n = org.childId_n)) ");
							//sql.append(" and org.codeitemid_n like '"+code+"%'");
							sql.append(") ce");
							//System.out.println(sql.toString());
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							if(rootdesc==null||rootdesc.length()<=0)
							{
								rootdesc="汇报关系";
							} 
							url=orgmapbo.createReportMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
							
					  }
					  
			    }catch(Exception e)
				{
					e.printStackTrace();
				}	
			}	
			KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
			kqUtilsClass.dropTable(table);
			url = PubFunc.encrypt(url);  
			this.getFormHM().put("url",url);
	}
	/**
	 * 排序
	 * @param i
	 * @param sql
	 * @param table
	 * @param dao
	 * @return
	 */
	private int updateTempTableGrade(int i,StringBuffer sql,String table,ContentDAO dao)
    {
    	 int number = 0;
    	 int gradevalue=i+1;
    	 try
    	 {
    		 sql.append("update ");
    	     sql.append(table);
    	     sql.append(" ");
    	     sql.append("set grade = "+gradevalue+" ");
    	     sql.append("where codeitemid<>parentid and ");
    	     sql.append("parentid in (select codeitemid  ");
    	     sql.append("from ");
    	     sql.append(table);
    	     sql.append(" ");			     
    	     sql.append("where ");
    	     sql.append("grade = "+i+" )");    	     
    	     number = dao.update(sql.toString());
    	     sql.delete(0,sql.length());
    	     
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }	     
	     return number;
    }
	/**
	 * 修改有父亲节点的孩子，重新排列孩子
	 * @param tablename
	 */
	private void  checkorg(String tablename)
	{
		 StringBuffer sql =new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try{
			 sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(tablename);
		     sql.append(" SET childid =(SELECT MIN(codeitemid) FROM ");
		     sql.append(tablename + " d");
		     sql.append(" WHERE d.parentid = ");
		     sql.append(tablename);
			 sql.append(".codeitemid  AND d.parentid <> d.codeitemid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append(tablename);
		     sql.append(" c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append(tablename);
		     sql.append(".codeitemid AND c.parentid <> c.codeitemid)");
		     //System.out.println(sql.toString());
		     dao.update(sql.toString());
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	}	
	/**
	 * 重新排序
	 * @param tablename
	 * @param dao
	 */
	private void reworkNexus(String tablename,ContentDAO dao,String code)
	{
		String sql="select max(grade) as grade from "+tablename;
		int grade=0;
		RowSet rs=null;
		try
		{
			rs=dao.search(sql);
			if(rs.next())
				grade=rs.getInt("grade");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		for(int i=1;i<=grade;i++)
		{
			reworkNexusForGrade(tablename,dao,i,code);
		}	
	    checkorg_N(tablename);
	    updateChildId_NISnull(tablename,dao);
	}
	/**
	 * 按级别新建排序
	 * @param tablename
	 * @param dao
	 * @param grade
	 */
	private void reworkNexusForGrade(String tablename,ContentDAO dao,int grade,String code)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select codeitemid,parentid,childid,codeitemid_n,parentid_n,childid_n from "+tablename+" where grade="+grade);
		try
		{
			RowSet rs=dao.search(sql.toString());
	    	String codeitemid="";
	    	String parentid="";
	    	String childid="";
	    	String codeitemid_n="";
	    	String parentid_n="";
	    	String childid_n="";
	    	int i=1;
	    	String updateSql="update "+tablename+" set codeitemid_n=?,parentid_n=?,childid_n=? where codeitemid=?";;
	    	ArrayList updateList=new ArrayList();
	    	while(rs.next())
	    	{
	    		codeitemid=rs.getString("codeitemid")!=null?rs.getString("codeitemid"):"";
	    		parentid=rs.getString("parentid")!=null?rs.getString("parentid"):"";
	    		childid=rs.getString("childid")!=null?rs.getString("childid"):"";
	    		codeitemid_n=rs.getString("codeitemid_n")!=null?rs.getString("codeitemid_n"):"";
	    		parentid_n=rs.getString("parentid_n")!=null?rs.getString("parentid_n"):"";
	    		childid_n=rs.getString("childid_n")!=null?rs.getString("childid_n"):"";
	    		ArrayList one_list=new ArrayList();
	    		if(parentid.equals(codeitemid)||(code!=null&&codeitemid.equals(code)))
	    		{
	    			codeitemid_n=codeitemid;
                    parentid_n=codeitemid;
                    one_list.add(codeitemid_n);
                    one_list.add(parentid_n);
                    one_list.add("");
                    one_list.add(codeitemid);                   
                    updateList.add(one_list);
	    		}else if(codeitemid.equals(childid))
	    		{
	    			parentid_n=getCodeItemId_NFormParent(tablename,parentid,dao);
	    			codeitemid_n=parentid_n+reItem(i);
	    			one_list.add(codeitemid_n);
                    one_list.add(parentid_n);
                    one_list.add(codeitemid_n);
                    one_list.add(codeitemid);                   
                    updateList.add(one_list);
                    
	    		}else {
	    			parentid_n=getCodeItemId_NFormParent(tablename,parentid,dao);
	    			codeitemid_n=parentid_n+reItem(i);
	    			one_list.add(codeitemid_n);
                    one_list.add(parentid_n);
                    one_list.add("");
                    one_list.add(codeitemid);                   
                    updateList.add(one_list);
	    		}
	    		i++;
	    	}
	    	dao.batchUpdate(updateSql, updateList);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private String reItem(int i)
	{
		String item="00"+i;
		item=item.substring(item.length()-3,item.length());
		return item;
	}
	private String getCodeItemId_NFormParent(String table,String parentid,ContentDAO dao)
	{
		String sql="select codeitemid_n from "+table+" where codeitemid='"+parentid+"'";
		RowSet rs=null;
		String codeitemid="";
		try
		{
			rs=dao.search(sql);
			if(rs.next())
				codeitemid=rs.getString("codeitemid_n");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return codeitemid;
	}
	/**
	 * 修改自己的孩子编号
	 * @param tablename
	 */
	private void  checkorg_N(String tablename)
	{
		 StringBuffer sql =new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try{
			 sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(tablename);
		     sql.append(" SET childid_n =(SELECT MIN(codeitemid_n) FROM ");
		     sql.append(tablename + " d");
		     sql.append(" WHERE d.parentid_n = ");
		     sql.append(tablename);
			 sql.append(".codeitemid_n  AND d.parentid_n <> d.codeitemid_n)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append(tablename);
		     sql.append(" c");
		     sql.append(" WHERE c.parentid_n = ");
		     sql.append(tablename);
		     sql.append(".codeitemid_n AND c.parentid_n <> c.codeitemid_n)");
		     //System.out.println(sql.toString());
		     dao.update(sql.toString());
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	}
	/**
	 * 修孩子为空的的数据
	 * @param table
	 * @param dao
	 */
	private void updateChildId_NISnull(String table,ContentDAO dao)
	{
		StringBuffer up=new StringBuffer();
		up.append("update "+table+" set childid_n=codeitemid_n where childid_n='' or childid_n is null");
		try
	    {
	    	dao.update(up.toString());
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	}
	/**
	 * 建立临时表
	 * @param table
	 * @param constant
	 * @param code
	 * @param dao
	 */
	private void createTempTable(String table,String constant,String code,ContentDAO dao)
	{
		
		StringBuffer sql=new StringBuffer();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = sdf.format(new Date());
		
		sql.append("drop table ");
		sql.append(table);				
		try{
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
		}catch(Exception e)
		{
				
		}
		//建立数据表
		sql.delete(0,sql.length());
		sql.append("CREATE TABLE ");
		sql.append(table);
		sql.append(" (codesetid varchar (2),");
		sql.append("codeitemid varchar (100) not null,");
		sql.append("codeitemdesc varchar (200),");
		sql.append("parentid varchar (100),");
		sql.append("childid varchar (100),");
		sql.append("state varchar (10),");
		sql.append("a0000 int null,");
		sql.append(""+constant+" varchar (100),");				
		sql.append("flag int ,");
		sql.append("leafagechilds int ,");
		sql.append("codeitemid_n varchar (100),");
		sql.append("parentid_n varchar (100),");
		sql.append("childid_n varchar (100),");
		sql.append("grade int)");					
		try{
			  //建立主建
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
			  sql.delete(0,sql.length());
			  sql.append("ALTER TABLE ");
			  sql.append(table);
			  sql.append(" ADD PRIMARY KEY (codeitemid)");
//			  System.out.println(sql.toString());
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());	
			  //插入数据
			  sql.delete(0,sql.length());
			  sql.append("INSERT INTO ");
			  sql.append(table);
			  sql.append(" (codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000,"+constant+")");
			  sql.append("select '@K' as codesetid, K01.E01A1 as codeitemid, Org.codeitemdesc, K01."+constant+" as parentid, Org.childid, Org.state, Org.A0000, K01."+constant+" ");
			  sql.append("from K01 K01, organization org  where K01.E01A1 = org.CodeItemId and ");
			  if(code.equalsIgnoreCase(userView.getManagePrivCodeValue())){
				  sql.append(userView.getUnitPosWhereByPriv("org.CodeItemId"));
			  }else{
				  sql.append(" codeitemid like '"+code+"%'");
			  }
			  sql.append(" and org.codesetid = '@K' ");
			  dao.insert(sql.toString(),new ArrayList());
			  /********************/
			  int insert_n=1;
			  while(insert_n!=0)
			  {
				  sql.delete(0,sql.length());
				  sql.append("INSERT INTO ");
				  sql.append(table);
				  sql.append(" (codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000,"+constant+")");
				  sql.append("select '@K' as codesetid, K01.E01A1 as codeitemid, Org.codeitemdesc, K01."+constant+" as parentid, Org.childid, Org.state, Org.A0000, K01."+constant+" ");
				  sql.append("from K01 K01, organization org  where K01.E01A1 = org.CodeItemId and "+constant+" in (select CodeItemId from "+table+")");
				  sql.append(" and e01a1 not in(select CodeItemId from "+table+")");
				  sql.append(" and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date ");
				  sql.append(" and org.codesetid = '@K' ");
				  
				  insert_n=dao.insert(sql.toString(),new ArrayList());
			  }
			  /***********/
			  //给grade排序
			  sql.delete(0,sql.length());			  
			  sql.append("update ");
			  sql.append(table);
			  sql.append(" ");
			  sql.append("set grade = 1 ");
			  
			  sql.append("where "+constant+" is null or "+constant+"= ''");
			  if(code!=null&&code.length()>0)
				  sql.append(" or codeitemid='"+code+"'");
              int update_n = dao.update(sql.toString());
              if(update_n<1){
            	  sql.delete(0,sql.length());
            	  sql.append("update ");
            	  sql.append(table);
            	  sql.append(" set grade=1 ,");
            	  sql.append(constant+"='' ");
            	  sql.append(" where parentid not in (select codeitemid from ");
            	  sql.append(table);
            	  sql.append(")");
            	  dao.update(sql.toString());
              }
			  sql.delete(0,sql.length());	
			  int i = 1;
			  int number = this.updateTempTableGrade(i,sql,table,dao);
			  i++;
			  while(number>0)
			  {
			     number = this.updateTempTableGrade(i,sql,table,dao);
			     i++;
			  }
			  sql.delete(0,sql.length());
			  sql.append("update "+table+" set parentid=codeitemid where parentid='' or parentid is null");						  
			  dao.update(sql.toString());	
			  sql.delete(0,sql.length());
			  sql.append("update "+table+" set parentid_n = codeitemid_n where parentid_n='' or parentid_n is null");
			  dao.update(sql.toString());
			  //insertTopParentid(table,dao);
			  //sql.delete(0,sql.length());
			  checkorg(table);//修改有父亲节点的孩子，重新排列孩子
			  //updateTopParentChild(table,dao);
			  reworkNexus(table, dao,code);
			  sql.delete(0,sql.length());
			  sql.append("update "+table+" set parentid_n = codeitemid_n where parentid_n='' or parentid_n is null");
			  dao.update(sql.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 给临时表添加人员记录
	 * @param table
	 * @param dao
	 * @param dbname
	 * @param code
	 */
	private void insertTempEmp(String table,ContentDAO dao,String dbname,String code)
	{
		
		try{
             StringBuffer sql=new StringBuffer();
		     sql.append("insert into ");
		     sql.append(table);
		     sql.append("(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,codeitemid_n,parentid_n,childid_n)");
		     sql.append("select 'zz',a.e01a1 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,a.a0101,a.e01a1,a.e01a1 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");		
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,'',(select grade from ");				     
		     sql.append(table+" b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
		     sql.append(table+" b where b.codeitemid=a.e01a1) ,");
		     sql.append("(select  b.codeitemid_n ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100 from "+table+" b where b.codeitemid=a.e01a1),");//codeitemid_n
		     sql.append("(select  b.codeitemid_n ");				     
		     sql.append(" from "+table+" b where b.codeitemid=a.e01a1),");//parentid_n
		     sql.append("(select  b.codeitemid_n ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100 from "+table+" b where b.codeitemid=a.e01a1) ");//childid_n
		     sql.append(" from  "+dbname);
		     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1 ");
		     //sql.append(" and c.codeitemid like '"+code+"%'");	
		     sql.append(" and c.codeitemid in(select CodeItemId from "+table+")");
		     dao.insert(sql.toString(),new ArrayList());
		     sql.delete(0,sql.length());
		     sql.append("insert into ");
		     sql.append(table+"(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,codeitemid_n,parentid_n,childid_n)");
		     sql.append("select 'zz',a.e0122 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,'',(select grade from ");				     
		     sql.append(table+" b where b.codeitemid=a.e0122)+1,(select a0000 from ");
		     sql.append(table+" b where b.codeitemid=a.e0122),");
		     sql.append("(select  b.codeitemid_n ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100 from "+table+" b where b.codeitemid=a.e01a1),");//codeitemid_n
		     sql.append("(select  b.codeitemid_n ");				     
		     sql.append(" from "+table+" b where b.codeitemid=a.e01a1),");//parentid_n
		     sql.append("(select  b.codeitemid_n ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100 from "+table+" b where b.codeitemid=a.e01a1) ");//childid_n
		     sql.append(" from "+dbname);
		     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='')");
		     //sql.append(" and c.codeitemid like '"+code+"%'");		    
		     sql.append(" and c.codeitemid in(select CodeItemId from "+table+")");
		     dao.insert(sql.toString(),new ArrayList());
		     sql.delete(0,sql.length());
		     sql.append("insert into ");
		     sql.append(table);
		     sql.append("(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000,codeitemid_n,parentid_n,childid_n)");
		     sql.append("select 'zz',a.b0110 ");			     
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,'',(select grade from ");				     
		     sql.append(table+" b where b.codeitemid=a.b0110)+1,(select a0000 from ");				    
		     sql.append(table+" b where b.codeitemid=a.b0110), ");//
		     sql.append("(select  b.codeitemid_n ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100 from "+table+" b where b.codeitemid=a.e01a1),");//codeitemid_n
		     sql.append("(select  b.codeitemid_n ");				     
		     sql.append(" from "+table+" b where b.codeitemid=a.e01a1),");//parentid_n
		     sql.append("(select  b.codeitemid_n ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100 from "+table+" b where b.codeitemid=a.e01a1) ");//childid_n
		     sql.append(" from "+dbname);
		     sql.append("a01 a,organization c where a.b0110 is not null and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='') ");
		     //sql.append(" and c.codeitemid like '"+code+"%'");
		     sql.append(" and c.codeitemid in(select CodeItemId from "+table+")");
		     dao.insert(sql.toString(),new ArrayList());
		     sql.delete(0,sql.length());
		     sql.append("UPDATE ");				     
		     sql.append(table+" SET childid =(SELECT MIN(codeitemid) FROM ");				     
		     sql.append(table+" d");
		     sql.append(" WHERE d.parentid = ");				     
		     sql.append(table+".codeitemid  AND d.parentid <> d.codeitemid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append(table+" c");
		     sql.append(" WHERE c.parentid = ");				   
		     sql.append(table+".codeitemid AND c.parentid <> c.codeitemid)");
		     dao.update(sql.toString());
		     checkorg_N(table);
		     sql.delete(0,sql.length());
		     sql.append("delete  from "+table+" where grade='' or grade is null");
		     dao.delete(sql.toString(),new ArrayList());
		    }catch(Exception e)
			{
		    	e.printStackTrace();
		    }
	}
	private boolean isCode_grade(String table,String code,ContentDAO dao)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select grade from ");							
		sql.append(table+" where codeitemid_n='");
		sql.append(code+"'");
		boolean isCorrect=false;
		try
		{
			RowSet rs=dao.search(sql.toString());
			if(rs.next())
				isCorrect=true;
		}catch(Exception e)
		{
			
		}
		return isCorrect;
	}
	private String  getCodeMess(String table,String code,ContentDAO dao)
	{
		if(code==null||code.length()<=0)
			return "";
		
		StringBuffer sql=new StringBuffer();
		
		sql.append("select codeitemdesc from organization");							
		sql.append(" where codeitemid='");
		sql.append(code+"' and codesetid='@K'");
		String desc="";		
		try
		{
			RowSet rs=dao.search(sql.toString());
			if(rs.next())
			{
			  desc=rs.getString(1);	
			}
		}catch(Exception e)
		{
			
		}
		return desc;
	}
}
