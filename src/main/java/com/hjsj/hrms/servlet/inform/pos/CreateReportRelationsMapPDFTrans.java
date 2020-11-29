package com.hjsj.hrms.servlet.inform.pos;

import com.hjsj.hrms.businessobject.general.orgmap.OrgMapBo;
import com.hjsj.hrms.businessobject.general.orgmap.ParameterBo;
import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateReportRelationsMapPDFTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	String code=(String)this.getFormHM().get("code");
		if(code==null || code.trim().length()==0)
			code=userView.getManagePrivCodeValue();
		OrgMapBo orgmapbo=new OrgMapBo();
		StringBuffer sql=new StringBuffer();
		String dbnameini="usr";
		ArrayList Dblist=userView.getPrivDbList();
		if(Dblist!=null)
			dbnameini=Dblist.get(0).toString();
		HashMap paramehashmap=new SetOrgOptionParameter().ReadOutParameterXml("POS_MAPOPTION",true,dbnameini,userView); 
		ParameterBo parameterbo=new ParameterBo();
		parameterbo.setCellaspect(paramehashmap.get("cellaspect").toString());
		parameterbo.setCellcolor(paramehashmap.get("cellcolor").toString());
		parameterbo.setCellheight(paramehashmap.get("cellheight").toString());
		parameterbo.setCellhspacewidth(paramehashmap.get("cellhspacewidth").toString());
		parameterbo.setCellletteraligncenter(paramehashmap.get("cellletteraligncenter").toString());
		parameterbo.setCellletteralignleft(paramehashmap.get("cellletteralignleft").toString());
		parameterbo.setCellletteralignright(paramehashmap.get("cellletteralignright").toString());
		parameterbo.setCellletterfitline(paramehashmap.get("cellletterfitline").toString());
		parameterbo.setCellletterfitsize(paramehashmap.get("cellletterfitsize").toString());
		parameterbo.setCelllettervaligncenter(paramehashmap.get("celllettervaligncenter").toString());
		parameterbo.setCelllinestrokewidth(paramehashmap.get("celllinestrokewidth").toString());
		parameterbo.setCellshape(paramehashmap.get("cellshape").toString());
		parameterbo.setCellvspacewidth(paramehashmap.get("cellvspacewidth").toString());
		parameterbo.setCellwidth(paramehashmap.get("cellwidth").toString());
		parameterbo.setFontcolor(paramehashmap.get("fontcolor").toString());
		parameterbo.setFontfamily(paramehashmap.get("fontfamily").toString());
		parameterbo.setFontsize(paramehashmap.get("fontsize").toString());
		parameterbo.setFontstyle(paramehashmap.get("fontstyle").toString());
		parameterbo.setGraph3d(paramehashmap.get("graph3d").toString());
		parameterbo.setGraphaspect(paramehashmap.get("graphaspect").toString());
		parameterbo.setIsshowpersonconut(paramehashmap.get("isshowpersonconut").toString());
		parameterbo.setIsshowpersonname(paramehashmap.get("isshowpersonname").toString());
		parameterbo.setNamesinglecell(paramehashmap.get("namesinglecell").toString());
		parameterbo.setDbname(paramehashmap.get("dbnames").toString());
		String dbname=paramehashmap.get("dbnames").toString();
		String url="";
		sql.append("select a0000 from organization where A0000 is null or A0000 ='' or  EXISTS(select * from organization group by A0000 having count(A0000)>1)");
		List rs1=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
		sql.delete(0,sql.length());
		if(!rs1.isEmpty() && rs1.size()>0)
		{
			if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
			{
				sql.delete(0,sql.length());
				sql.append("drop table ");
				sql.append(this.userView.getUserName());
				sql.append("organization");
				try{
				  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
				}catch(Exception e)
				{
					//e.printStackTrace();
				}
				sql.delete(0,sql.length());
				sql.append("CREATE TABLE ");
				sql.append(this.userView.getUserName());
				sql.append("organization (codesetid varchar (2),");
				sql.append("codeitemid varchar (100) not null,");
				sql.append("codeitemdesc varchar (200),");
				sql.append("parentid varchar (100),");
				sql.append("childid varchar (100),");
				sql.append("state varchar (10),");
				sql.append("a0000 int null,");
				sql.append("grade int)");
				try{
					  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
					  sql.delete(0,sql.length());
					  sql.append("ALTER TABLE ");
					  sql.append(this.userView.getUserName());
					  sql.append("organization ADD PRIMARY KEY (codeitemid)");
					  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());					  
			    }catch(Exception e)
				{
					e.printStackTrace();
				}	
			    sql.delete(0,sql.length());
			    sql.append("INSERT INTO ");
			    sql.append(this.userView.getUserName());
			    sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000 from organization where codeitemid like '");
			    sql.append(code);
			    sql.append("%'");
			    ContentDAO dao=new ContentDAO(this.getFrameconn());
			    
			    try{
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',");
			     sql.append("a.e01a1 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append("a.a0100,a.a0101,a.e01a1,a.e01a1 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e01a1) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1 and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.e0122 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e0122) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.b0110 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.b0110) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("UPDATE ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization d");
			     sql.append(" WHERE d.parentid = ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
			     sql.append(" WHERE  EXISTS (SELECT * FROM ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization c");
			     sql.append(" WHERE c.parentid = ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
			     dao.update(sql.toString());
			    }catch(Exception e)
				{
			    	e.printStackTrace();
			    }
			    
			    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
				parameterbo.setPageheight(pagesize[1]);
				parameterbo.setPagespaceheight(40);
				parameterbo.setPagespacewidth(40);
				parameterbo.setPagewidth(pagesize[0]);
			    
				if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
				{
					url=orgmapbo.createOrgMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
					
				}else
				{
		            sql.delete(0,sql.length());
					sql.append("SELECT distinct codesetid, codeitemid, codeitemdesc AS text, parentid, childid, a.state, grade");
					if(code!=null && code.length()>0)
					{
						sql.append(" - ");
						sql.append("(select grade from ");
						sql.append(this.userView.getUserName());
						sql.append("organization where codeitemid='");
						sql.append(code);
						sql.append("') as grade");
					}			
					sql.append(", A0000,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND b.codeitemid = b.childid and b.codesetid='zz') +  (SELECT COUNT(*)");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codesetid='zz' and codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS personcount,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
			        sql.append(" FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e");
					sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization m WHERE   m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g");
					sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
					sql.append(" (SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization a  ");
					sql.append(",K01 k");
					sql.append(" where ( k.E01A1 = a.CodeItemId or a.codesetid ='zz')");
					sql.append(" order by a.codeitemid,a.codesetid ");
					
					////
					List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					sql.delete(0,sql.length());
					sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts AS childs,");
					sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
					sql.append("(SELECT COUNT(*) counts from ");
					sql.append(this.userView.getUserName());
					sql.append("organization) aa,(SELECT COUNT(*) counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
					sql.append(" (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,");
					sql.append("(SELECT COUNT(*) counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,");
					sql.append("(SELECT COUNT(*) counts");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND");
					sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
					sql.append("(SELECT COUNT(*) counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
					sql.append(" (SELECT MIN(codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND  (NOT EXISTS (SELECT *");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
					sql.append("(SELECT COUNT(*) counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
					sql.append("(SELECT COUNT(*) counts");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
					sql.append("(SELECT COUNT(*) counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
					List rootnode=null;
					if(code==null || code.length()<=0)
					      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
					url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
				}
				
				deleteTempTable(sql);
			}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
			{
			
				sql.delete(0,sql.length());
				sql.append("drop table ");
				sql.append(this.userView.getUserName());
				sql.append("organization");
				try{
				  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
				}catch(Exception e)
				{
					//e.printStackTrace();
				}
				sql.delete(0,sql.length());
				sql.append("CREATE TABLE ");
				sql.append(this.userView.getUserName());
				sql.append("organization (codesetid varchar (2),");
				sql.append("codeitemid varchar (100) not null,");
				sql.append("codeitemdesc varchar (200),");
				sql.append("parentid varchar (100),");
				sql.append("childid varchar (100),");
				sql.append("state varchar (10),");
				sql.append("a0000 int null,");
				sql.append("grade int)");
				try{
					  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
					  sql.delete(0,sql.length());
					  sql.append("ALTER TABLE ");
					  sql.append(this.userView.getUserName());
					  sql.append("organization ADD PRIMARY KEY (codeitemid)");
					  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());					  
			    }catch(Exception e)
				{
					e.printStackTrace();
				}	
			    sql.delete(0,sql.length());
			    sql.append("INSERT INTO ");
			    sql.append(this.userView.getUserName());
			    sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000 from organization where codeitemid like '");
			    sql.append(code);
			    sql.append("%'");
			    ContentDAO dao=new ContentDAO(this.getFrameconn());
			    
			    try{
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.e01a1 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.e01a1,a.e01a1 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
	
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e01a1) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1 and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.e0122 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e0122) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.b0110 ");			     
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.b0110) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.b0110 is not null and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("UPDATE ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization d");
			     sql.append(" WHERE d.parentid = ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
			     sql.append(" WHERE  EXISTS (SELECT * FROM ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization c");
			     sql.append(" WHERE c.parentid = ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
			     dao.update(sql.toString());
			    }catch(Exception e)
				{
			    	e.printStackTrace();
			    }
			    
			    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
				parameterbo.setPageheight(pagesize[1]);
				parameterbo.setPagespaceheight(40);
				parameterbo.setPagespacewidth(40);
				parameterbo.setPagewidth(pagesize[0]);
				if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
				{
					url=orgmapbo.createOrgMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
					
				}else
				{
				    
	                sql.delete(0,sql.length());
					sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
					if(code!=null && code.length()>0)
					{
						sql.append(" - ");
						sql.append("(select grade from ");
						sql.append(this.userView.getUserName());
						sql.append("organization where codeitemid='");
						sql.append(code);
						sql.append("') as grade");
					}			
					sql.append(", A0000,(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
			        sql.append(" FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e");
					sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization m WHERE  m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g");
					sql.append(" WHERE  g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND  a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
					sql.append(" (SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization a order by a.codeitemid,a.codesetid");
					List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					sql.delete(0,sql.length());
					sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.counts AS childs,'ren' AS subhead,");
					sql.append("ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts as  lastchildscount,ax.counts AS errorchilds from ");
					sql.append("(SELECT COUNT(*) AS counts from ");
					sql.append(this.userView.getUserName());
					sql.append("organization) aa,");
					sql.append("(SELECT COUNT(*) counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
					sql.append(" (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,(SELECT COUNT(*) counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization c  WHERE c.codeitemid = c.parentid) dd, (SELECT COUNT(*) counts");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND");
					sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
					sql.append(" (SELECT MIN(codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND  (NOT EXISTS (SELECT *");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
					sql.append("(SELECT COUNT(*) counts");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
					List rootnode=null;
					if(code==null || code.length()<=0)
					      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
					url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
					
				}	
					deleteTempTable(sql);
				
			
			}
			else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString())){				
				int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
				parameterbo.setPageheight(pagesize[1]);
				parameterbo.setPagespaceheight(40);
				parameterbo.setPagespacewidth(40);
				parameterbo.setPagewidth(pagesize[0]);	
				if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
				{
					url=orgmapbo.createOrgMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
					
				}else
				{
					checkorg("organization");
					sql.append("SELECT codesetid, codeitemid, codeitemdesc  " + orgmapbo.ConverDBsql(dbname) + " AS text, parentid, childid, a.state, grade");
					if(code!=null && code.length()>0)
					{
						sql.append(" - ");
						sql.append("(select grade from organization where codeitemid='");
						sql.append(code);
						sql.append("') as grade");
					}			
					sql.append(",(SELECT COUNT(*) FROM organization b WHERE b.parentid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
			        sql.append(" FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
					sql.append("(SELECT COUNT(*) FROM organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM organization e");
					sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
			        sql.append(" (SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM organization m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
					sql.append("(SELECT COUNT(*) FROM organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM organization g");
					sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
			        sql.append(" (SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM organization n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
					sql.append(" (SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
					sql.append(" From ");
					sql.append(dbname);
					sql.append("A01 RIGHT OUTER JOIN");
					sql.append(" organization a ON ");
					sql.append(Sql_switcher.substr(dbname + "A01.B0110","1",Sql_switcher.length("a.codeitemid"))); 	              
					sql.append("= a.codeitemid OR ");
					sql.append(Sql_switcher.substr(dbname + "A01.e0122","1",Sql_switcher.length("a.codeitemid"))); 	    
					sql.append("= a.codeitemid OR ");
					sql.append(Sql_switcher.substr(dbname + "A01.e01a1","1",Sql_switcher.length("a.codeitemid"))); 	    
					sql.append("= a.codeitemid");
					sql.append(" where a.codeitemid like '");
					sql.append(code);
					sql.append("%' GROUP BY a.codesetid,a.codeitemid,a.codeitemdesc,a.parentid,a.childid");
					sql.append(",a.state, a.grade");	
					sql.append(" order by a.codeitemid");
				
					List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					sql.delete(0,sql.length());
					/*sql.append("SELECT COUNT(*) AS nodecount,(SELECT COUNT(*) FROM organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid)-1 + (SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
					sql.append(" (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS leafagechilds,(SELECT COUNT(*) FROM organization c  WHERE c.codeitemid = c.parentid) AS childs, 'ren' AS subhead,(SELECT COUNT(*)");
					sql.append(" FROM organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM organization e WHERE e.parentid = e.codeitemid) + '%' AND");
					sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) + (SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
					sql.append(" (SELECT MIN(codeitemid) FROM organization m WHERE m.parentid = m.codeitemid) + '%') AND  (NOT EXISTS (SELECT *");
					sql.append(" FROM organization orge  WHERE orge.codeItemId = org.childId))) AS firstchildscount,(SELECT COUNT(*) FROM organization f WHERE f.parentid LIKE ");
					sql.append(" (SELECT MAX(g.codeitemid) FROM organization g WHERE g.codeitemid = g.parentid) + '%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) + (SELECT COUNT(*)");
					sql.append(" FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM organization n WHERE n.codeitemid = n.parentid) + '%') AND (NOT EXISTS");
					sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,(SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
					sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds FROM organization");
	               */
	
					sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
					sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
					sql.append("(SELECT COUNT(*) AS nodecount from organization where codeitemid like '");
					sql.append(code);
					sql.append("%') aa,");
					sql.append("(SELECT COUNT(*) as counts FROM organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
					sql.append(code);
					sql.append("%') bb,");
					sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
					sql.append(" (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') cc,");
					sql.append("(SELECT COUNT(*) as counts FROM organization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
					sql.append(code);
					sql.append("%') dd,");
					sql.append("(SELECT COUNT(*) as counts ");
					sql.append(" FROM organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM organization e WHERE e.parentid = e.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append(" '%' AND");
					sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
					sql.append(code);
					sql.append("%') ae,");
					sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
					sql.append(" (SELECT MIN(codeitemid) FROM organization m WHERE m.parentid = m.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND  (NOT EXISTS (SELECT *");
					sql.append(" FROM organization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') ad,");
					sql.append("(SELECT COUNT(*)  as counts FROM organization f WHERE f.parentid LIKE ");
					sql.append("(SELECT MAX(g.codeitemid) FROM organization g WHERE g.codeitemid = g.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
					sql.append(code);
					sql.append("%') be,");
					sql.append("(SELECT COUNT(*) as counts");
					sql.append(" FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM organization n WHERE n.codeitemid = n.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND (NOT EXISTS");
					sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') bd,");
					sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
					sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') ce");
					
					
					List rootnode=null;
					if(code==null || code.length()<=0)
					      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
					url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
				}
			}
			else
			{
				int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
				parameterbo.setPageheight(pagesize[1]);
				parameterbo.setPagespaceheight(40);
				parameterbo.setPagespacewidth(40);
				parameterbo.setPagewidth(pagesize[0]);
				if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
				{
					url=orgmapbo.createOrgMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
					
				}else
				{
					checkorg("organization");
					sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
					if(code!=null && code.length()>0)
					{
						sql.append(" - ");
						sql.append("(select grade from organization where codeitemid='");
						sql.append(code);
						sql.append("') as grade");
					}		
					sql.append(", A0000,(SELECT COUNT(*) FROM organization b WHERE b.parentid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
			        sql.append(" FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
					sql.append("(SELECT COUNT(*) FROM organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM organization e");
					sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
			        sql.append(" (SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM organization m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
					sql.append("(SELECT COUNT(*) FROM organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM organization g");
					sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
			        sql.append(" (SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM organization n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
					sql.append(" (SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
					sql.append(" FROM organization a where a.codeitemid like '");
					sql.append(code);
					sql.append("%' order by a.codeitemid");
					List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					sql.delete(0,sql.length());
					/*sql.append("SELECT COUNT(*) AS nodecount,(SELECT COUNT(*) FROM organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid)-1 + (SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
					sql.append(" (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS leafagechilds,(SELECT COUNT(*) FROM organization c  WHERE c.codeitemid = c.parentid) AS childs, 'ren' AS subhead,(SELECT COUNT(*)");
					sql.append(" FROM organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM organization e WHERE e.parentid = e.codeitemid) + '%' AND");
					sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) + (SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
					sql.append(" (SELECT MIN(codeitemid) FROM organization m WHERE m.parentid = m.codeitemid) + '%') AND  (NOT EXISTS (SELECT *");
					sql.append(" FROM organization orge  WHERE orge.codeItemId = org.childId))) AS firstchildscount,(SELECT COUNT(*) FROM organization f WHERE f.parentid LIKE ");
					sql.append(" (SELECT MAX(g.codeitemid) FROM organization g WHERE g.codeitemid = g.parentid) + '%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) + (SELECT COUNT(*)");
					sql.append(" FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM organization n WHERE n.codeitemid = n.parentid) + '%') AND (NOT EXISTS");
					sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,(SELECT COUNT(*) FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
					sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds FROM organization");
	               */
	
					sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
					sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
					sql.append("(SELECT COUNT(*) AS nodecount from organization where codeitemid like '");
					sql.append(code);
					sql.append("%') aa,");
					sql.append("(SELECT COUNT(*) as counts FROM organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
					sql.append(code);
					sql.append("%') bb,");
					sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
					sql.append(" (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') cc,");
					sql.append("(SELECT COUNT(*) as counts FROM organization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
					sql.append(code);
					sql.append("%') dd,");
					sql.append("(SELECT COUNT(*) as counts ");
					sql.append(" FROM organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM organization e WHERE e.parentid = e.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append(" '%' AND");
					sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
					sql.append(code);
					sql.append("%') ae,");
					sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
					sql.append(" (SELECT MIN(codeitemid) FROM organization m WHERE m.parentid = m.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND  (NOT EXISTS (SELECT *");
					sql.append(" FROM organization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') ad,");
					sql.append("(SELECT COUNT(*)  as counts FROM organization f WHERE f.parentid LIKE ");
					sql.append("(SELECT MAX(g.codeitemid) FROM organization g WHERE g.codeitemid = g.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
					sql.append(code);
					sql.append("%') be,");
					sql.append("(SELECT COUNT(*) as counts");
					sql.append(" FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM organization n WHERE n.codeitemid = n.parentid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND (NOT EXISTS");
					sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') bd,");
					sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
					sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') ce");
					
					List rootnode=null;
					if(code==null || code.length()<=0)
					      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
					url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
				}
			}
		}
		else
		{
			if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
			{	
				
				
			    createTempTable(code, sql);
			    ContentDAO dao=new ContentDAO(this.getFrameconn());
			    
			    try{			   
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.e01a1 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.e01a1,a.e01a1 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e01a1) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.e01a1 is not null and a.e01a1=c.codeitemid and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.e0122 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e0122) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.b0110 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.b0110) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.b0110 is not null and a.b0110=c.codeitemid  and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("UPDATE ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization d");
			     sql.append(" WHERE d.parentid = ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
			     sql.append(" WHERE  EXISTS (SELECT * FROM ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization c");
			     sql.append(" WHERE c.parentid = ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
			     //System.out.println(sql.toString());
			     dao.update(sql.toString());
			    }catch(Exception e)
				{
			    	e.printStackTrace();
			    }
			    
			    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
				parameterbo.setPageheight(pagesize[1]);
				parameterbo.setPagespaceheight(40);
				parameterbo.setPagespacewidth(40);
				parameterbo.setPagewidth(pagesize[0]);
				if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
				{
					url=orgmapbo.createOrgMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
					
				}else
				{
				   // System.out.println(pagesize[1]);
				   // System.out.println(pagesize[0]);
		            sql.delete(0,sql.length());
					sql.append("SELECT codesetid, codeitemid, codeitemdesc  AS text, parentid, childid, state, grade");
					if(code!=null && code.length()>0)
					{
						sql.append(" - ");
						sql.append("(select grade from ");
						sql.append(this.userView.getUserName());
						sql.append("organization where codeitemid='");
						sql.append(code);
						sql.append("') as grade");
					}			
					sql.append(", A0000,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND b.codeitemid = b.childid and b.codesetid='zz') +  (SELECT COUNT(*)");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codesetid='zz' and codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS personcount,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
			        sql.append(" FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
			       
					//sql.append("'%' AND b.codeitemid = b.childid)-1 AS leafagechilds,(SELECT COUNT(*) FROM ");
			       
					sql.append(this.userView.getUserName());
			        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid like (SELECT min(codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e");
					sql.append(" WHERE  e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) AND ee.codeitemid <> a.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(a0000) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization m WHERE  m.parentid = a.codeitemid AND m.codeitemid <> m.parentid) AND ee.codeitemid <> a.codeitemid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid like (SELECT min(codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization gg WHERE gg.a0000 in(SELECT MAX(g.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g");
					sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid) AND gg.codeitemid <> a.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)  AND nn.codeitemid <> a.codeitemid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
					sql.append(" (SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization a order by a.A0000, a.codeitemid,a.codesetid");
					
					
					//System.out.println("c" + sql.toString());
					List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					sql.delete(0,sql.length());
					sql.append("SELECT aa.counts as nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.counts AS childs,'ren' AS subhead,");
					sql.append("ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
					sql.append("(select COUNT(*) counts from ");
					sql.append(this.userView.getUserName());
					sql.append("organization) aa,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
					sql.append(" (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,(SELECT COUNT(*) as counts");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid LIKE (SELECT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid)  AND ee.codesetid <> 'zz') ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND");
					sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 in");
					sql.append(" (SELECT MIN(a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid)  AND ee.codesetid <> 'zz') ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND  (NOT EXISTS (SELECT *");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
					sql.append("(SELECT COUNT(*) counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid LIKE (SELECT codeitemid");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization gg WHERE gg.a0000 in (SELECT MAX(g.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid)  AND gg.codesetid <> 'zz') ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
					sql.append("(SELECT COUNT(*) as counts");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) AND nn.codesetid <> 'zz') ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");				
					///System.out.println(sql.toString());
					List rootnode=null;
					if(code==null || code.length()<=0)
					      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
					url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
					
				}	
					deleteTempTable(sql);
				
			}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
			{				
				createTempTable(code, sql);
			   
			    ContentDAO dao=new ContentDAO(this.getFrameconn());	
			    
			try{
			  		   
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.e01a1 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.e01a1,a.e01a1 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e01a1) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1 and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     dao.insert(sql.toString(),new ArrayList());
			     //System.out.println(sql.toString());
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.e0122 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.e0122) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.e0122 is not null and c.codeitemid=a.e0122 and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     ///System.out.println(sql.toString());
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("insert into ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
			     sql.append("select 'zz',a.b0110 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" ' ' ");
			     sql.append(SqlDifference.getJoinSymbol());
			     sql.append(" a.a0100,'',(select grade from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization b where b.codeitemid=a.b0110) from ");
			     sql.append(dbname);
			     sql.append("a01 a,organization c where a.b0110 is not null and c.codeitemid=a.b0110 and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
			     sql.append(code);
			     sql.append("%'");
			     //System.out.println("a0100" + sql.toString());
			     dao.insert(sql.toString(),new ArrayList());
			     sql.delete(0,sql.length());
			     sql.append("UPDATE ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization d");
			     sql.append(" WHERE d.parentid = ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
			     sql.append(" WHERE  EXISTS (SELECT * FROM ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization c");
			     sql.append(" WHERE c.parentid = ");
			     sql.append(this.userView.getUserName());
			     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
			     dao.update(sql.toString());
			     //this.getFrameconn().commit();
			    }catch(Exception e)
				{
			    	e.printStackTrace();
			    }
			    
			    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
				parameterbo.setPageheight(pagesize[1]);
				parameterbo.setPagespaceheight(40);
				parameterbo.setPagespacewidth(40);
				parameterbo.setPagewidth(pagesize[0]);
				if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
				{
					url=orgmapbo.createOrgMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
					
				}else
				{
				    
	                sql.delete(0,sql.length());
					sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
					if(code!=null && code.length()>0)
					{
						sql.append(" - ");
						sql.append("(select grade from ");
						sql.append(this.userView.getUserName());
						sql.append("organization where codeitemid='");
						sql.append(code);
						sql.append("') as grade");
					}			
					sql.append(", A0000,(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
			        sql.append(" FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid like (SELECT min(codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e");
					sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) AND ee.codeitemid <> a.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(a0000) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization m WHERE  m.parentid = a.codeitemid AND m.codeitemid <> m.parentid) AND ee.codeitemid <> a.codeitemid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid like (SELECT min(codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization gg WHERE gg.a0000 in(SELECT MAX(g.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g");
					sql.append(" WHERE  g.parentid = a.codeitemid AND g.codeitemid <> g.parentid) AND gg.codeitemid <> a.codeitemid) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND  a.codesetid<>'zz' and f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)  AND nn.codeitemid <> a.codeitemid) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
					sql.append(" (SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization a order by a.A0000,a.codeitemid,a.codesetid");
					//System.out.println("wwwnames" + sql.toString());
					List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					sql.delete(0,sql.length());
					sql.append("select aa.nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.childs as childs,");
					sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
					sql.append("(SELECT COUNT(*) AS nodecount from ");
					sql.append(this.userView.getUserName());
					sql.append("organization) aa,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
					sql.append(" (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,");
					sql.append("(SELECT COUNT(*) as childs FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,(SELECT COUNT(*) as counts");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid LIKE (SELECT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid)  AND ee.codesetid <> 'zz') ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND");
					sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
					sql.append("(SELECT COUNT(*) as counts FROM ");				
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 in");
					sql.append(" (SELECT MIN(a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid)  AND ee.codesetid <> 'zz') ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND  (NOT EXISTS (SELECT *");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid LIKE (SELECT codeitemid");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization gg WHERE gg.a0000 in (SELECT MAX(g.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid)  AND gg.codesetid <> 'zz') ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
					sql.append("(SELECT COUNT(*) as counts");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) AND nn.codesetid <> 'zz') ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
					List rootnode=null;
					if(code==null || code.length()<=0)
					      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
					url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
					//System.out.println("wwwname" + sql.toString());
				}
					deleteTempTable(sql);				
			
			}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
			{
				int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
				
				parameterbo.setPageheight(pagesize[1]);
				parameterbo.setPagespaceheight(40);
				parameterbo.setPagespacewidth(40);
				parameterbo.setPagewidth(pagesize[0]);				
				if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
				{
					url=orgmapbo.createOrgMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
					
				}else
				{
					
					
					createTempTable(code, sql);
					
					
					checkorg(this.userView.getUserName() + "organization");
					sql.delete(0,sql.length());
					sql.append("SELECT a.codesetid, a.codeitemid, a.codeitemdesc " + orgmapbo.ConverDBsql(dbname) + " AS text, a.parentid, a.childid, a.state, a.grade");
					if(code!=null && code.length()>0)
					{
						sql.append(" - ");
						sql.append("(select grade from ");
						sql.append(this.userView.getUserName());
						sql.append("organization where codeitemid='");
						sql.append(code);
						sql.append("') as grade");
					}		
					sql.append(", a.A0000,(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
			        sql.append(" FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid like (SELECT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e");
					sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(a0000) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid)) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid like (SELECT DISTINCT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization gg WHERE gg.a0000 =(SELECT MAX(g.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g");
					sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
					sql.append(" (SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
					sql.append(" From ");
					sql.append(dbname);
					sql.append("A01 RIGHT OUTER JOIN ");
					sql.append(this.userView.getUserName());
					sql.append("organization a ON ");
					sql.append(Sql_switcher.substr(dbname + "A01.B0110","1",Sql_switcher.length("a.codeitemid"))); 	              
					sql.append("= a.codeitemid OR ");
					sql.append(Sql_switcher.substr(dbname + "A01.e0122","1",Sql_switcher.length("a.codeitemid"))); 	    
					sql.append("= a.codeitemid OR ");
					sql.append(Sql_switcher.substr(dbname + "A01.e01a1","1",Sql_switcher.length("a.codeitemid"))); 	    
					sql.append("= a.codeitemid where a.codeitemid like '");
					sql.append(code);
					sql.append("%'");
					sql.append(" GROUP BY a.codesetid,a.codeitemid,a.codeitemdesc,a.parentid,a.childid");
					sql.append(",a.state, a.grade,a.a0000");	
					sql.append(" order by a.A0000,a.codeitemid,a.codesetid");
					//System.out.println(sql.toString());
					List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					sql.delete(0,sql.length());
					sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
					sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
					sql.append("(SELECT COUNT(*) AS nodecount from ");
					sql.append(this.userView.getUserName());
					sql.append("organization where codeitemid like '");
					sql.append(code);
					sql.append("%') aa,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
					sql.append(code);
					sql.append("%') bb,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
					sql.append(" (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') cc,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
					sql.append(code);
					sql.append("%') dd,");
					sql.append("(SELECT COUNT(*) as counts ");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid LIKE (SELECT DISTINCT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e WHERE e.parentid = e.codeitemid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append(" '%' AND");
					sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
					sql.append(code);
					sql.append("%') ae,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 =");
					sql.append(" (SELECT MIN(a0000) FROM ");
					sql.append("organization m WHERE m.parentid = m.codeitemid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND  (NOT EXISTS (SELECT *");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') ad,");
					sql.append("(SELECT COUNT(*)  as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid LIKE (SELECT DISTINCT codeitemid");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization gg WHERE gg.a0000 = (SELECT MAX(g.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g WHERE g.codeitemid = g.parentid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
					sql.append(code);
					sql.append("%') be,");
					sql.append("(SELECT COUNT(*) as counts");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization n WHERE n.codeitemid = n.parentid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') bd,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') ce");
					List rootnode=null;
					if(code==null || code.length()<=0)
					      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
					url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
					deleteTempTable(sql);	
				}
			}else
			{
				
				createTempTable(code, sql);		
				checkorg(this.userView.getUserName() + "organization");
				sql.delete(0,sql.length());
				int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName());
				parameterbo.setPageheight(pagesize[1]);
				parameterbo.setPagespaceheight(40);
				parameterbo.setPagespacewidth(40);
				parameterbo.setPagewidth(pagesize[0]);
				
				if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
				{
					url=orgmapbo.createOrgMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
					
				}else
				{
					sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
					if(code!=null && code.length()>0)
					{
						sql.append(" - ");
						sql.append("(select grade from ");
						sql.append(this.userView.getUserName());
						sql.append("organization where codeitemid='");
						sql.append(code);
						sql.append("') as grade");
					}		
					sql.append(", A0000,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
			        sql.append(" FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
					sql.append(" (SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid like (SELECT min(codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e");
					sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append(" '%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(a0000) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid)) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
					sql.append("(SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid like (SELECT max(codeitemid) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization gg WHERE gg.a0000 =(SELECT MAX(g.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g");
					sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append(" '%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
			        sql.append(" (SELECT COUNT(*) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT max(codeitemid) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)) ");
			        sql.append(SqlDifference.getJoinSymbol());
			        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
			        sql.append(this.userView.getUserName());
			        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
					sql.append(" (SELECT COUNT(*) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization a where codeitemid like '");
					sql.append(code);
					sql.append("%' order by a.A0000,a.codeitemid,a.codesetid");
					//System.out.println(sql.toString());
					List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					sql.delete(0,sql.length());
					sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
					sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
					sql.append("(SELECT COUNT(*) AS nodecount from ");
					sql.append(this.userView.getUserName());
					sql.append("organization where codeitemid like '");
					sql.append(code);
					sql.append("%') aa,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
					sql.append(code);
					sql.append("%') bb,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
					sql.append(" (NOT EXISTS (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') cc,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
					sql.append(code);
					sql.append("%') dd,");
					sql.append("(SELECT COUNT(*) as counts ");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization d WHERE d.parentid LIKE (SELECT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization e WHERE e.parentid = e.codeitemid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append(" '%' AND");
					sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
					sql.append(code);
					sql.append("%') ae,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization ee WHERE ee.a0000 =");
					sql.append(" (SELECT MIN(a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization m WHERE m.parentid = m.codeitemid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND  (NOT EXISTS (SELECT *");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') ad,");
					sql.append("(SELECT COUNT(*)  as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization f WHERE f.parentid LIKE (SELECT codeitemid");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization gg WHERE gg.a0000 = (SELECT MAX(g.a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization g WHERE g.codeitemid = g.parentid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
					sql.append(code);
					sql.append("%') be,");
					sql.append("(SELECT COUNT(*) as counts");
					sql.append(" FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization n WHERE n.codeitemid = n.parentid)) ");
					sql.append(SqlDifference.getJoinSymbol());
					sql.append("'%') AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') bd,");
					sql.append("(SELECT COUNT(*) as counts FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
					sql.append(" (SELECT * FROM ");
					sql.append(this.userView.getUserName());
					sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
					sql.append(code);
					sql.append("%') ce");
					List rootnode=null;
					if(code==null || code.length()<=0)
					      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
					url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
					//deleteTempTable(sql);	
				}
			}
		}
		this.getFormHM().put("url",url);

	}
	private void deleteTempTable(StringBuffer sql) {
		sql.delete(0,sql.length());
		sql.append("drop table ");
		sql.append(this.userView.getUserName());
		sql.append("organization");
		try{
		  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void createTempTable(String code, StringBuffer sql) {
		sql.delete(0,sql.length());
		sql.append("drop table ");
		sql.append(this.userView.getUserName());
		sql.append("organization");
		try{
		  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
		}catch(Exception e)
		{
			//e.printStackTrace();
		}
		
		switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			  sql.delete(0,sql.length());
			  sql.append("CREATE TABLE ");
			  sql.append(this.userView.getUserName());
			  sql.append("organization (codesetid varchar (2),");
			  sql.append("codeitemid varchar (100) not null,");
			  sql.append("codeitemdesc varchar (200),");
			  sql.append("parentid varchar (100),");
			  sql.append("childid varchar (100),");
			  sql.append("state varchar (10),");
			  sql.append("a0000 int null,");
			  sql.append("seqId Int IDENTITY(1,1),");
			  sql.append("grade int)");					
			  break;
		  }
		  case Constant.DB2:
		  {
			  sql.delete(0,sql.length());
			  sql.append("CREATE TABLE ");
			  sql.append(this.userView.getUserName());
			  sql.append("organization (codesetid varchar (2),");
			  sql.append("codeitemid varchar (100) not null,");
			  sql.append("codeitemdesc varchar (200),");
			  sql.append("parentid varchar (100),");
			  sql.append("childid varchar (100),");
			  sql.append("state varchar (10),");
			  sql.append("a0000 int null,");
			  sql.append("seqId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),");
			  sql.append("grade int)");		
			  break;
		  }
		  case Constant.ORACEL:
		  {
			  sql.delete(0,sql.length());
			  sql.append("CREATE TABLE ");
			  sql.append(this.userView.getUserName());
			  sql.append("organization (codesetid varchar (2),");
			  sql.append("codeitemid varchar (100) not null,");
			  sql.append("codeitemdesc varchar (200),");
			  sql.append("parentid varchar (100),");
			  sql.append("childid varchar (100),");
			  sql.append("state varchar (10),");
			  sql.append("a0000 int null,");
			  sql.append("seqId int,");
			  sql.append("grade int)");		
			  //sql.append("CREATE TABLE ");					
			  break;
		  }
		
		}
		
		try{
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
			  sql.delete(0,sql.length());
			  sql.append("ALTER TABLE ");
			  sql.append(this.userView.getUserName());
			  sql.append("organization ADD PRIMARY KEY (codeitemid)");
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());					  
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			  sql.delete(0,sql.length());
			  sql.append("INSERT INTO ");
			  sql.append(this.userView.getUserName());
			  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000 from organization where codeitemid like '");
			  sql.append(code);
			  sql.append("%' order by a0000,codeitemid,codesetid");				
			  break;
		  }
		  case Constant.DB2:
		  {
			  sql.delete(0,sql.length());
			  sql.append("INSERT INTO ");
			  sql.append(this.userView.getUserName());
			  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000 from organization where codeitemid like '");
			  sql.append(code);
			  sql.append("%' order by a0000,codeitemid,codesetid");		
			  break;
		  }
		  case Constant.ORACEL:
		  {
			  sql.delete(0,sql.length());
			  sql.append("INSERT INTO ");
			  sql.append(this.userView.getUserName());
			  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, RowNum from organization where codeitemid like '");
			  sql.append(code);
			  sql.append("%' order by a0000,codeitemid,codesetid");				
			  break;
		  }
		
		}
   
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		try{
		 dao.insert(sql.toString(),new ArrayList());
		 //更新临时表中的A0000大排序
		
		 switch(Sql_switcher.searchDbServer())
			{
			  case Constant.MSSQL:
			  {
				  UpdateTempA0000(dao);
				  break;
			  }
			  case Constant.DB2:
			  {
				  UpdateTempA0000(dao);
				  break;
			  }
			  case Constant.ORACEL:
			  {
				 break;
			  }
			
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
    private void UpdateTempA0000(ContentDAO dao)
    {
    	 try{
    		 StringBuffer A0000sql=new StringBuffer();
    		 A0000sql.append("update ");
    		 A0000sql.append(this.userView.getUserName());
    		 A0000sql.append("organization set A0000=seqId");
		     dao.update(A0000sql.toString());
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    }
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
}

