package com.hjsj.hrms.transaction.pos.posreport;

import com.hjsj.hrms.businessobject.general.orgmap.ParameterBo;
import com.hjsj.hrms.businessobject.general.orgmap.ReportRelationOrgMap;
import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 *<p>Title:CreateReportRelationsMapPDFTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 21, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class CreateReportRelationsMapPDFTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	private float sortnumber;
	
	
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	String code=(String)this.getFormHM().get("code");
	String constant=(String)this.getFormHM().get("constant");
		if(code==null || code.trim().length()==0)
			code=userView.getManagePrivCodeValue();
		ReportRelationOrgMap orgmapbo=new ReportRelationOrgMap();
		StringBuffer sql=new StringBuffer();
		String dbnameini="usr";
		ArrayList Dblist=userView.getPrivDbList();
		if(Dblist!=null)
			dbnameini=Dblist.get(0).toString();
		HashMap paramehashmap=new SetOrgOptionParameter().ReadOutParameterXml("ORG_MAPOPTION",true,dbnameini,userView); 
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
//		sql.append("select a0000 from organization where A0000 is null or A0000 ='' or  EXISTS(select * from organization group by A0000 having count(A0000)>1)");
//		List rs1=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
//		sql.delete(0,sql.length());

		if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
		{
			sql.delete(0,sql.length());
			sql.append("drop table ");
			sql.append(this.userView.getUserName()+this.userView.getUserName());
			sql.append("organization");
//			System.out.println(sql.toString());
			try{
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
			}catch(Exception e)
			{
				//e.printStackTrace();
			}
			sql.delete(0,sql.length());
			sql.append("CREATE TABLE ");
			sql.append(this.userView.getUserName()+this.userView.getUserName());
			sql.append("organization (codesetid varchar (2),");
			sql.append("codeitemid varchar (100) not null,");
			sql.append("codeitemdesc varchar (200),");
			sql.append("parentid varchar (100),");
			sql.append("childid varchar (100),");
			sql.append("state varchar (10),");
			sql.append("a0000 int null,");
			sql.append(""+constant+" varchar (100),");
			sql.append("sort float ,");
			sql.append("flag int ,");
			sql.append("leafagechilds int ,");
			sql.append("grade int)");
//			System.out.println(sql.toString());
			
			try{
				  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
				  sql.delete(0,sql.length());
				  sql.append("ALTER TABLE ");
				  sql.append(this.userView.getUserName()+this.userView.getUserName());
				  sql.append("organization ADD PRIMARY KEY (codeitemid)");
//				  System.out.println(sql.toString());
				  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());					  
		    }catch(Exception e)
			{
				e.printStackTrace();
			}	
		    sql.delete(0,sql.length());
		    sql.append("INSERT INTO ");
		    sql.append(this.userView.getUserName()+this.userView.getUserName());
		    sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000,"+constant+")");
		    sql.append("select '@K' as codesetid, K01.E01A1 as codeitemid, Org.codeitemdesc, K01."+constant+" as parentid, Org.childid, Org.state, Org.A0000, K01."+constant+" ");
		    sql.append("from K01 K01, organization org  where K01.E01A1 = org.CodeItemId and codeitemid like '");
		    sql.append(code);
		    sql.append("%' and org.codesetid = '@K' ");
//		    System.out.println(sql.toString());

		    try{
		     dao.insert(sql.toString(),new ArrayList());
		     sql.delete(0,sql.length());			  
		     sql.append("update ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append("set grade = 1 ");
		     sql.append("where "+constant+" = ''");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());		     
		     sql.delete(0,sql.length());	
		     
//		     sql.append("update ");
//		     sql.append(this.userView.getUserName()+this.userView.getUserName());
//		     sql.append("organization ");
//		     sql.append("set parentid = "+Sql_switcher.substr("codeitemid","0","6")+" ");
//		     sql.append("where grade = 1 ");
//		     System.out.println(sql.toString());
//		     dao.update(sql.toString());		     
//		     sql.delete(0,sql.length());		
		     
		     sql.append("insert into ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000)");
		     sql.append("select 'zz',");
		     sql.append("a.e01a1 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append("a.a0100,a.a0101,a.e01a1,a.e01a1 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,'',");
		     sql.append("(select a0000 from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization b where b.codeitemid=a.e01a1) from ");
		     sql.append(dbname);
		     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1 and c.codeitemid like '");
		     sql.append(code);
		     sql.append("%' and c.codesetid = '@K' ");
		     dao.insert(sql.toString(),new ArrayList());
//		     System.out.println(sql.toString());
		     
		     sql.delete(0,sql.length());
		     sql.append("insert into ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000)");
		     sql.append("select 'zz',a.e0122 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,'',");
		     sql.append("(select a0000 from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization b where b.codeitemid=a.e0122) from ");
		     sql.append(dbname);
		     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
		     sql.append(code);
		     sql.append("%' and c.codesetid = '@K' ");
		     dao.insert(sql.toString(),new ArrayList());
//		     System.out.println(sql.toString());
		     
		     sql.delete(0,sql.length());
		     sql.append("insert into ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000)");
		     sql.append("select 'zz',a.b0110 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,'',");
		     sql.append("(select a0000 from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization b where b.codeitemid=a.b0110) from ");
		     sql.append(dbname);
		     sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
		     sql.append(code);
		     sql.append("%' and c.codesetid = '@K' ");
		     dao.insert(sql.toString(),new ArrayList());
//		     System.out.println(sql.toString());
		     sql.delete(0,sql.length());
		     
		     int i = 1;
		     int number = this.updateTempTable(i,sql,constant,dao);
		     i++;
		     while(number>0)
		     {
		    	 number = this.updateTempTable(i,sql,constant,dao);
		    	 i++;
		     }
		     		     
		     sql.append(" select * from  ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append(" where grade = 1 ");
//		     System.out.println(sql.toString());
		     ArrayList templist = dao.searchDynaList(sql.toString());
		     sql.delete(0,sql.length());
		     for(int t=0;t<templist.size();t++)
		     {
		    	 float sortid = (t+1)*10000;
		    	 this.setSortnumber((t+1)*10000);
		    	 int flag = t+1;
		    	 DynaBean dynabean=(DynaBean)templist.get(t);
		    	 String codeitemid = (String)dynabean.get("codeitemid");
			     this.updateSort(codeitemid,this.getSortnumber(),flag,dao);				    
			     this.searchChild( codeitemid, this.getSortnumber(),flag, dao);
		     }			     
		     
		     sql.append("update ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization ");
    	     sql.append("set grade = c.grade+1 ");
    	     sql.append("from ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization, ");	  
    	     sql.append("(select a.codeitemdesc,a.codeitemid,b.grade ");
    	     sql.append("from ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization a ");			     
    	     sql.append("left join  ");
    	     sql.append("(select codeitemid,grade,codeitemdesc ");	    
    	     sql.append("from ");	    
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization ");	    
    	     sql.append("where codesetid='@k' ");	    
    	     sql.append(") b on a.parentid=b.codeitemid ");	    
    	     sql.append("where a.codesetid='zz' ");	    
    	     sql.append(") c ");	    
    	     sql.append("where ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization.codeitemid=c.codeitemid ");	   	     
//    	     System.out.println(sql.toString());
    	     dao.update(sql.toString());

    	     
		     sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization d");
		     sql.append(" WHERE d.parentid = ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());
		     
		     sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization SET leafagechilds = c.leafagechilds from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ,");
		     sql.append(" (select codeitemid, ");
		     StringBuffer temp = new StringBuffer();
			 temp.append("(SELECT b.acount FROM ");
			 temp.append("(select a.codeitemid,a.codeitemdesc,count(b.codeitemid) acount  from ");
			 temp.append(this.userView.getUserName()+this.userView.getUserName());
			 temp.append("organization a,");
			 temp.append(this.userView.getUserName()+this.userView.getUserName());
			 temp.append("organization b ");
			 temp.append("where  a.codeitemid=b.parentid   group by a.codeitemid,a.codeitemdesc ) b ");
			 temp.append("where b.codeitemid = a.codeitemid )");
			 sql.append(Sql_switcher.isnull(temp.toString(),"0"));
			 sql.append("-1  AS leafagechilds ");
		     sql.append("from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization a ) c where ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid = c.codeitemid ");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());
		     
		     sql.delete(0,sql.length());			  
		     sql.append("delete ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append("where "+Sql_switcher.sqlNull("grade",0)+" =0 ");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());		     
		     sql.delete(0,sql.length());	
		     
		    
		     
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
					sql.append(this.userView.getUserName()+this.userView.getUserName());
					sql.append("organization where codeitemid='");
					sql.append(code);
					sql.append("') as grade");
				}			
				sql.append(", A0000,");
				sql.append("(SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND b.codeitemid = b.childid and b.codesetid='zz') +  (SELECT COUNT(*)");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codesetid='zz' and codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS personcount,");
				sql.append("leafagechilds,");
				sql.append("(SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
				sql.append("(SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization e");
				sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
		        sql.append(" (SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization m WHERE   m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) ");
		        sql.append(SqlDifference.getJoinSymbol());
		        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
//		        sql.append(" 0 AS firstchildscount,");
				sql.append("(SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization g");
				sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
		        sql.append(" (SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) ");
		        sql.append(SqlDifference.getJoinSymbol());
		        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
//		        sql.append(" 0 AS lastchildscount,");
				sql.append(" (SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds,");
				StringBuffer temp = new StringBuffer();
				temp.append("(SELECT b.acount FROM ");
				temp.append("(select a.codeitemid,a.codeitemdesc,sum(b.leafagechilds) acount  from ");
				temp.append(this.userView.getUserName()+this.userView.getUserName());
				temp.append("organization a,");
				temp.append(this.userView.getUserName()+this.userView.getUserName());
				temp.append("organization b ");
				temp.append(" where   b.leafagechilds>0 ");
				temp.append(" and a.leafagechilds>-1");
				temp.append(" and a.flag = b.flag ");
				temp.append(" and a.sort<b.sort");
				temp.append(" group by a.codeitemid,a.codeitemdesc ) b ");
				temp.append("where b.codeitemid = a.codeitemid )");
				sql.append(Sql_switcher.isnull(temp.toString(),"0"));
				sql.append("  AS myleafagechilds ");	
//		        sql.append(" 0 AS errorchilds ");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization a ");
				sql.append("order by a.sort ");
//				System.out.println(sql.toString());
				List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
				sql.delete(0,sql.length());
				sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts AS childs,");
				sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
				sql.append("(SELECT COUNT(*) counts from ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization) aa,(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
				sql.append("(SELECT COUNT(*) as counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
				sql.append(" (NOT EXISTS (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,");
				sql.append("(SELECT COUNT(*) counts");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND");
				sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
				sql.append(" (SELECT MIN(codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%') AND  (NOT EXISTS (SELECT *");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
				sql.append("(SELECT COUNT(*) counts");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%') AND (NOT EXISTS");
				sql.append(" (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
				sql.append(" (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName()); 
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
//				System.out.println(sql.toString());
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
			sql.append(this.userView.getUserName()+this.userView.getUserName());
			sql.append("organization");
//			System.out.println(sql.toString());
			try{
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
			}catch(Exception e)
			{
				//e.printStackTrace();
			}
			sql.delete(0,sql.length());
			sql.append("CREATE TABLE ");
			sql.append(this.userView.getUserName()+this.userView.getUserName());
			sql.append("organization (codesetid varchar (2),");
			sql.append("codeitemid varchar (100) not null,");
			sql.append("codeitemdesc varchar (200),");
			sql.append("parentid varchar (100),");
			sql.append("childid varchar (100),");
			sql.append("state varchar (10),");
			sql.append("a0000 int null,");
			sql.append(""+constant+" varchar (100),");
			sql.append("sort float ,");
			sql.append("flag int ,");
			sql.append("leafagechilds int ,");
			sql.append("grade int)");
//			System.out.println(sql.toString());
			
			try{
				  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
				  sql.delete(0,sql.length());
				  sql.append("ALTER TABLE ");
				  sql.append(this.userView.getUserName()+this.userView.getUserName());
				  sql.append("organization ADD PRIMARY KEY (codeitemid)");
//				  System.out.println(sql.toString());
				  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());					  
		    }catch(Exception e)
			{
				e.printStackTrace();
			}	
		    sql.delete(0,sql.length());
		    sql.append("INSERT INTO ");
		    sql.append(this.userView.getUserName()+this.userView.getUserName());
		    sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000,"+constant+")");
		    sql.append("select '@K' as codesetid, K01.E01A1 as codeitemid, Org.codeitemdesc, K01."+constant+" as parentid, Org.childid, Org.state, Org.A0000, K01."+constant+" ");
		    sql.append("from K01 K01, organization org  where K01.E01A1 = org.CodeItemId and codeitemid like '");
		    sql.append(code);
		    sql.append("%' and org.codesetid = '@K' ");
//		    System.out.println(sql.toString());
		    
//		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		    try{
		     dao.insert(sql.toString(),new ArrayList());
		     sql.delete(0,sql.length());			  
		     sql.append("update ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append("set grade = 1 ");
		     sql.append("where "+constant+" = ''");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());		     
		     sql.delete(0,sql.length());	
		    //
		    
		     sql.append("insert into ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000)");
		     sql.append("select 'zz',");
		     sql.append("a.e01a1 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append("a.a0100,a.a0101,a.e01a1,a.e01a1 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,'',");
		     sql.append("(select a0000 from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization b where b.codeitemid=a.e01a1) from ");
		     sql.append(dbname);
		     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1 and c.codeitemid like '");
		     sql.append(code);
		     sql.append("%' and c.codesetid = '@K' ");
		     dao.insert(sql.toString(),new ArrayList());
//		     System.out.println(sql.toString());
		   
		     //
		     
		     sql.delete(0,sql.length());
		     sql.append("insert into ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000)");
		     sql.append("select 'zz',a.e0122 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,'',");
		     sql.append("(select a0000 from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization b where b.codeitemid=a.e0122) from ");
		     sql.append(dbname);
		     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
		     sql.append(code);
		     sql.append("%' and c.codesetid = '@K' ");
		     dao.insert(sql.toString(),new ArrayList());
//		     System.out.println(sql.toString());
		     
		     sql.delete(0,sql.length());
		     sql.append("insert into ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000)");
		     sql.append("select 'zz',a.b0110 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" ' ' ");
		     sql.append(SqlDifference.getJoinSymbol());
		     sql.append(" a.a0100,'',");
		     sql.append("(select a0000 from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization b where b.codeitemid=a.b0110) from ");
		     sql.append(dbname);
		     sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
		     sql.append(code);
		     sql.append("%' and c.codesetid = '@K' ");
		     dao.insert(sql.toString(),new ArrayList());
//		     System.out.println(sql.toString());
		     sql.delete(0,sql.length());
		     
		     int i = 1;
		     int number = this.updateTempTable(i,sql,constant,dao);
		     i++;
		     while(number>0)
		     {
		    	 number = this.updateTempTable(i,sql,constant,dao);
		    	 i++;
		     }
		     		     
		     sql.append(" select * from  ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append(" where grade = 1 ");
		     System.out.println(sql.toString());
		     ArrayList templist = dao.searchDynaList(sql.toString());
		     sql.delete(0,sql.length());
		     for(int t=0;t<templist.size();t++)
		     {
		    	 float sortid = (t+1)*10000;
		    	 this.setSortnumber((t+1)*10000);
		    	 int flag = t+1;
		    	 DynaBean dynabean=(DynaBean)templist.get(t);
		    	 String codeitemid = (String)dynabean.get("codeitemid");
			     this.updateSort(codeitemid,this.getSortnumber(),flag,dao);				    
			     this.searchChild( codeitemid, this.getSortnumber(),flag, dao);
		     }			     
		     
		     sql.append("update ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization ");
    	     sql.append("set grade = c.grade+1 ");
    	     sql.append("from ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization, ");	  
    	     sql.append("(select a.codeitemdesc,a.codeitemid,b.grade ");
    	     sql.append("from ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization a ");			     
    	     sql.append("left join  ");
    	     sql.append("(select codeitemid,grade,codeitemdesc ");	    
    	     sql.append("from ");	    
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization ");	    
    	     sql.append("where codesetid='@k' ");	    
    	     sql.append(") b on a.parentid=b.codeitemid ");	    
    	     sql.append("where a.codesetid='zz' ");	    
    	     sql.append(") c ");	    
    	     sql.append("where ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization.codeitemid=c.codeitemid ");	   	     
//    	     System.out.println(sql.toString());
    	     dao.update(sql.toString());
    	     sql.delete(0,sql.length());
    	     

		     
		     sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization d");
		     sql.append(" WHERE d.parentid = ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());
		     
		     sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization SET leafagechilds = c.leafagechilds from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ,");
		     sql.append(" (select codeitemid, ");
		     StringBuffer temp = new StringBuffer();
			 temp.append("(SELECT b.acount FROM ");
			 temp.append("(select a.codeitemid,a.codeitemdesc,count(b.codeitemid) acount  from ");
			 temp.append(this.userView.getUserName()+this.userView.getUserName());
			 temp.append("organization a,");
			 temp.append(this.userView.getUserName()+this.userView.getUserName());
			 temp.append("organization b ");
			 temp.append("where  a.codeitemid=b.parentid   group by a.codeitemid,a.codeitemdesc ) b ");
			 temp.append("where b.codeitemid = a.codeitemid )");
//			 temp.append("");
			 sql.append(Sql_switcher.isnull(temp.toString(),"0"));
			 sql.append("-1  AS leafagechilds ");
		     sql.append("from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization a ) c where ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid = c.codeitemid ");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());

		     sql.delete(0,sql.length());			  
		     sql.append("delete ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append("where "+Sql_switcher.sqlNull("grade",0)+" =0 ");
		     System.out.println(sql.toString());
		     dao.update(sql.toString());		     
		     sql.delete(0,sql.length());	
		     
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
					sql.append(this.userView.getUserName()+this.userView.getUserName());
					sql.append("organization where codeitemid='");
					sql.append(code);
					sql.append("') as grade");
				}			
				sql.append(", A0000,");
				//personcount
				sql.append("leafagechilds,");
				sql.append("(SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
				sql.append("(SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization e");
				sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
		        sql.append(" (SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization m WHERE   m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) ");
		        sql.append(SqlDifference.getJoinSymbol());
		        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
//		        sql.append(" 0 AS firstchildscount,");
				sql.append("(SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization g");
				sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
		        sql.append(" (SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) ");
		        sql.append(SqlDifference.getJoinSymbol());
		        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
//		        sql.append(" 0 AS lastchildscount,");
				sql.append(" (SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds,");
				StringBuffer temp = new StringBuffer();
				temp.append("(SELECT b.acount FROM ");
				temp.append("(select a.codeitemid,a.codeitemdesc,sum(b.leafagechilds) acount  from ");
				temp.append(this.userView.getUserName()+this.userView.getUserName());
				temp.append("organization a,");
				temp.append(this.userView.getUserName()+this.userView.getUserName());
				temp.append("organization b ");
				temp.append(" where   b.leafagechilds>0 ");
				temp.append(" and a.leafagechilds>-1");
				temp.append(" and a.flag = b.flag ");
				temp.append(" and a.sort<b.sort");
				temp.append(" group by a.codeitemid,a.codeitemdesc ) b ");
				temp.append("where b.codeitemid = a.codeitemid )");
				sql.append(Sql_switcher.isnull(temp.toString(),"0"));
				sql.append("  AS myleafagechilds ");
//		        sql.append(" 0 AS errorchilds ");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization a ");
				sql.append("order by a.sort ");
//				System.out.println(sql.toString());
				List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
				
				
				
				sql.delete(0,sql.length());
				sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.counts AS childs,'ren' AS subhead,");
				sql.append("ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts as  lastchildscount,ax.counts AS errorchilds from ");
				sql.append("(SELECT COUNT(*) AS counts from ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization) aa,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
				sql.append("(SELECT COUNT(*) as counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
				sql.append(" (NOT EXISTS (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization c  WHERE c.codeitemid = c.parentid) dd, (SELECT COUNT(*) counts");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND");
				sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
				sql.append("(SELECT COUNT(*) as counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
				sql.append(" (SELECT MIN(codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%') AND  (NOT EXISTS (SELECT *");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,(SELECT COUNT(*) as counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
				sql.append("(SELECT COUNT(*) counts");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%') AND (NOT EXISTS");
				sql.append(" (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
				sql.append("(SELECT COUNT(*) as counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
				sql.append(" (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
				List rootnode=null;
				if(code==null || code.length()<=0)
				      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
				String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
				url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
				
			}	
				deleteTempTable(sql);
			
		
		}
		else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
		{	
			sql.delete(0,sql.length());
			sql.append("drop table ");
			sql.append(this.userView.getUserName()+this.userView.getUserName());
			sql.append("organization");
//			System.out.println(sql.toString());
			try{
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
			}catch(Exception e)
			{
				//e.printStackTrace();
			}
			sql.delete(0,sql.length());
			sql.append("CREATE TABLE ");
			sql.append(this.userView.getUserName()+this.userView.getUserName());
			sql.append("organization (codesetid varchar (2),");
			sql.append("codeitemid varchar (100) not null,");
			sql.append("codeitemdesc varchar (200),");
			sql.append("parentid varchar (100),");
			sql.append("childid varchar (100),");
			sql.append("state varchar (10),");
			sql.append("a0000 int null,");
			sql.append(""+constant+" varchar (100),");
			sql.append("sort float ,");
			sql.append("flag int ,");
			sql.append("leafagechilds int ,");
			sql.append("grade int)");
//			System.out.println(sql.toString());
			
			try{
				  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
				  sql.delete(0,sql.length());
				  sql.append("ALTER TABLE ");
				  sql.append(this.userView.getUserName()+this.userView.getUserName());
				  sql.append("organization ADD PRIMARY KEY (codeitemid)");
//				  System.out.println(sql.toString());
				  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());					  
		    }catch(Exception e)
			{
				e.printStackTrace();
			}	
		    sql.delete(0,sql.length());
		    sql.append("INSERT INTO ");
		    sql.append(this.userView.getUserName()+this.userView.getUserName());
		    sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000,"+constant+")");
		    sql.append("select '@K' as codesetid, K01.E01A1 as codeitemid, Org.codeitemdesc, K01."+constant+" as parentid, Org.childid, Org.state, Org.A0000, K01."+constant+" ");
		    sql.append("from K01 K01, organization org  where K01.E01A1 = org.CodeItemId and codeitemid like '");
		    sql.append(code);
		    sql.append("%' and org.codesetid = '@K' ");
//		    System.out.println(sql.toString());

		    try{
		     dao.insert(sql.toString(),new ArrayList());
		     sql.delete(0,sql.length());			  
		     sql.append("update ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append("set grade = 1 ");
		     sql.append("where "+constant+" = ''");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());		     
		     sql.delete(0,sql.length());	

		     int i = 1;
		     int number = this.updateTempTable(i,sql,constant,dao);
		     i++;
		     while(number>0)
		     {
		    	 number = this.updateTempTable(i,sql,constant,dao);
		    	 i++;
		     }
		     		     
		     sql.append(" select * from  ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append(" where grade = 1 ");
		     System.out.println(sql.toString());
		     ArrayList templist = dao.searchDynaList(sql.toString());
		     sql.delete(0,sql.length());
		     for(int t=0;t<templist.size();t++)
		     {
		    	 float sortid = (t+1)*10000;
		    	 this.setSortnumber((t+1)*10000);
		    	 int flag = t+1;
		    	 DynaBean dynabean=(DynaBean)templist.get(t);
		    	 String codeitemid = (String)dynabean.get("codeitemid");
			     this.updateSort(codeitemid,this.getSortnumber(),flag,dao);				    
			     this.searchChild( codeitemid, this.getSortnumber(),flag, dao);
		     }			     
		     
		     sql.append("update ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization ");
    	     sql.append("set grade = c.grade+1 ");
    	     sql.append("from ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization, ");	  
    	     sql.append("(select a.codeitemdesc,a.codeitemid,b.grade ");
    	     sql.append("from ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization a ");			     
    	     sql.append("left join  ");
    	     sql.append("(select codeitemid,grade,codeitemdesc ");	    
    	     sql.append("from ");	    
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization ");	    
    	     sql.append("where codesetid='@k' ");	    
    	     sql.append(") b on a.parentid=b.codeitemid ");	    
    	     sql.append("where a.codesetid='zz' ");	    
    	     sql.append(") c ");	    
    	     sql.append("where ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization.codeitemid=c.codeitemid ");	   	     
//    	     System.out.println(sql.toString());
    	     dao.update(sql.toString());
    	     sql.delete(0,sql.length());
    	     

		     
		     sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization d");
		     sql.append(" WHERE d.parentid = ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());
		     
		     sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization SET leafagechilds = c.leafagechilds from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ,");
		     sql.append(" (select codeitemid, ");
		     StringBuffer temp = new StringBuffer();
			 temp.append("(SELECT b.acount FROM ");
			 temp.append("(select a.codeitemid,a.codeitemdesc,count(b.codeitemid) acount  from ");
			 temp.append(this.userView.getUserName()+this.userView.getUserName());
			 temp.append("organization a,");
			 temp.append(this.userView.getUserName()+this.userView.getUserName());
			 temp.append("organization b ");
			 temp.append("where  a.codeitemid=b.parentid   group by a.codeitemid,a.codeitemdesc ) b ");
			 temp.append("where b.codeitemid = a.codeitemid )");
//			 temp.append("");
			 sql.append(Sql_switcher.isnull(temp.toString(),"0"));
			 sql.append("-1  AS leafagechilds ");
		     sql.append("from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization a ) c where ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid = c.codeitemid ");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());
		     
		     sql.delete(0,sql.length());			  
		     sql.append("delete ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append("where "+Sql_switcher.sqlNull("grade",0)+" =0 ");
		     System.out.println(sql.toString());
		     dao.update(sql.toString());		     
		     sql.delete(0,sql.length());	
		     
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
					sql.append(this.userView.getUserName()+this.userView.getUserName());
					sql.append("organization where codeitemid='");
					sql.append(code);
					sql.append("') as grade");
				}		
				
				sql.append(", A0000,");
				sql.append("(SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND b.codeitemid = b.childid and b.codesetid='zz') +  (SELECT COUNT(*)");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codesetid='zz' and codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS personcount,");
				sql.append("leafagechilds,");
				sql.append("(SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
				sql.append("(SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization e");
				sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
		        sql.append(" (SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization m WHERE   m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) ");
		        sql.append(SqlDifference.getJoinSymbol());
		        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
//		        sql.append(" 0 AS firstchildscount,");
				sql.append("(SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization g");
				sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
		        sql.append(" (SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) ");
		        sql.append(SqlDifference.getJoinSymbol());
		        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
//		        sql.append(" 0 AS lastchildscount,");
				sql.append(" (SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds,");
				StringBuffer temp = new StringBuffer();
				temp.append("(SELECT b.acount FROM ");
				temp.append("(select a.codeitemid,a.codeitemdesc,sum(b.leafagechilds) acount  from ");
				temp.append(this.userView.getUserName()+this.userView.getUserName());
				temp.append("organization a,");
				temp.append(this.userView.getUserName()+this.userView.getUserName());
				temp.append("organization b ");
				temp.append(" where   b.leafagechilds>0 ");
				temp.append(" and a.leafagechilds>-1");
				temp.append(" and a.flag = b.flag ");
				temp.append(" and a.sort<b.sort");
				temp.append(" group by a.codeitemid,a.codeitemdesc ) b ");
				temp.append("where b.codeitemid = a.codeitemid )");
				sql.append(Sql_switcher.isnull(temp.toString(),"0"));
				sql.append("  AS myleafagechilds ");			
//		        sql.append(" 0 AS errorchilds ");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization a ");
				sql.append("order by a.sort ");
//				System.out.println(sql.toString());
				List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
				
				
				
				sql.delete(0,sql.length());
				sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts AS childs,");
				sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
				sql.append("(SELECT COUNT(*) counts from ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization) aa,(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
				sql.append("(SELECT COUNT(*) as counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
				sql.append(" (NOT EXISTS (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,");
				sql.append("(SELECT COUNT(*) counts");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND");
				sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
				sql.append(" (SELECT MIN(codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%') AND  (NOT EXISTS (SELECT *");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
				sql.append("(SELECT COUNT(*) counts");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%') AND (NOT EXISTS");
				sql.append(" (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
				sql.append(" (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName()); 
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
//				System.out.println(sql.toString());

				List rootnode=null;
				if(code==null || code.length()<=0)
				      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
				String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
				url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
			}
		}
		else
		{	
			sql.delete(0,sql.length());
			sql.append("drop table ");
			sql.append(this.userView.getUserName()+this.userView.getUserName());
			sql.append("organization");
//			System.out.println(sql.toString());
			try{
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
			}catch(Exception e)
			{
				//e.printStackTrace();
			}
			sql.delete(0,sql.length());
			sql.append("CREATE TABLE ");
			sql.append(this.userView.getUserName()+this.userView.getUserName());
			sql.append("organization (codesetid varchar (2),");
			sql.append("codeitemid varchar (100) not null,");
			sql.append("codeitemdesc varchar (200),");
			sql.append("parentid varchar (100),");
			sql.append("childid varchar (100),");
			sql.append("state varchar (10),");
			sql.append("a0000 int null,");
			sql.append(""+constant+" varchar (100),");
			sql.append("sort float ,");
			sql.append("flag int ,");
			sql.append("leafagechilds int ,");
			sql.append("grade int)");
//			System.out.println(sql.toString());
			
			try{
				  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
				  sql.delete(0,sql.length());
				  sql.append("ALTER TABLE ");
				  sql.append(this.userView.getUserName()+this.userView.getUserName());
				  sql.append("organization ADD PRIMARY KEY (codeitemid)");
//				  System.out.println(sql.toString());
				  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());					  
		    }catch(Exception e)
			{
				e.printStackTrace();
			}	
		    sql.delete(0,sql.length());
		    sql.append("INSERT INTO ");
		    sql.append(this.userView.getUserName()+this.userView.getUserName());
		    sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, A0000,"+constant+")");
		    sql.append("select '@K' as codesetid, K01.E01A1 as codeitemid, Org.codeitemdesc, K01."+constant+" as parentid, Org.childid, Org.state, Org.A0000, K01."+constant+" ");
		    sql.append("from K01 K01, organization org  where K01.E01A1 = org.CodeItemId and codeitemid like '");
		    sql.append(code);
		    sql.append("%' and org.codesetid = '@K' ");
//		    System.out.println(sql.toString());

		    try{
		     dao.insert(sql.toString(),new ArrayList());
		     sql.delete(0,sql.length());			  
		     sql.append("update ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append("set grade = 1 ");
		     sql.append("where "+constant+" = ''");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());		     
		     sql.delete(0,sql.length());	

		     int i = 1;
		     int number = this.updateTempTable(i,sql,constant,dao);
		     i++;
		     while(number>0)
		     {
		    	 number = this.updateTempTable(i,sql,constant,dao);
		    	 i++;
		     }
		     		     
		     sql.append(" select * from  ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append(" where grade = 1 ");
		     //System.out.println(sql.toString());
		     ArrayList templist = dao.searchDynaList(sql.toString());
		     sql.delete(0,sql.length());
		     for(int t=0;t<templist.size();t++)
		     {
		    	 float sortid = (t+1)*10000;
		    	 this.setSortnumber((t+1)*10000);
		    	 int flag = t+1;
		    	 DynaBean dynabean=(DynaBean)templist.get(t);
		    	 String codeitemid = (String)dynabean.get("codeitemid");
			     this.updateSort(codeitemid,this.getSortnumber(),flag,dao);				    
			     this.searchChild( codeitemid, this.getSortnumber(),flag, dao);
		     }			     
		     
		     sql.append("update ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization ");
    	     sql.append("set grade = c.grade+1 ");
    	     sql.append("from ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization, ");	  
    	     sql.append("(select a.codeitemdesc,a.codeitemid,b.grade ");
    	     sql.append("from ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization a ");			     
    	     sql.append("left join  ");
    	     sql.append("(select codeitemid,grade,codeitemdesc ");	    
    	     sql.append("from ");	    
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization ");	    
    	     sql.append("where codesetid='@k' ");	    
    	     sql.append(") b on a.parentid=b.codeitemid ");	    
    	     sql.append("where a.codesetid='zz' ");	    
    	     sql.append(") c ");	    
    	     sql.append("where ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization.codeitemid=c.codeitemid ");	   	     
//    	     System.out.println(sql.toString());
    	     dao.update(sql.toString());
    	     sql.delete(0,sql.length());
    	     

		     
		     sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization d");
		     sql.append(" WHERE d.parentid = ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());
		     
		     sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization SET leafagechilds = c.leafagechilds from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ,");
		     sql.append(" (select codeitemid, ");
		     StringBuffer temp = new StringBuffer();
			 temp.append("(SELECT b.acount FROM ");
			 temp.append("(select a.codeitemid,a.codeitemdesc,count(b.codeitemid) acount  from ");
			 temp.append(this.userView.getUserName()+this.userView.getUserName());
			 temp.append("organization a,");
			 temp.append(this.userView.getUserName()+this.userView.getUserName());
			 temp.append("organization b ");
			 temp.append("where  a.codeitemid=b.parentid   group by a.codeitemid,a.codeitemdesc ) b ");
			 temp.append("where b.codeitemid = a.codeitemid )");
//			 temp.append("");
			 sql.append(Sql_switcher.isnull(temp.toString(),"0"));
			 sql.append("-1  AS leafagechilds ");
		     sql.append("from ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization a ) c where ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization.codeitemid = c.codeitemid ");
//		     System.out.println(sql.toString());
		     dao.update(sql.toString());
		     
		     sql.delete(0,sql.length());			  
		     sql.append("delete ");
		     sql.append(this.userView.getUserName()+this.userView.getUserName());
		     sql.append("organization ");
		     sql.append("where "+Sql_switcher.sqlNull("grade",0)+" =0 ");
		     //System.out.println(sql.toString());
		     dao.update(sql.toString());		     
		     sql.delete(0,sql.length());	
		     
		     
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
					sql.append(this.userView.getUserName()+this.userView.getUserName());
					sql.append("organization where codeitemid='");
					sql.append(code);
					sql.append("') as grade");
				}		
				
				sql.append(", A0000,");
				sql.append("leafagechilds,");
				sql.append("(SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
				sql.append("(SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization e");
				sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
		        sql.append(" (SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization m WHERE   m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) ");
		        sql.append(SqlDifference.getJoinSymbol());
		        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
//		        sql.append(" 0 AS firstchildscount,");
				sql.append("(SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization g");
				sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
		        sql.append(" (SELECT COUNT(*) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) ");
		        sql.append(SqlDifference.getJoinSymbol());
		        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
		        sql.append(this.userView.getUserName()+this.userView.getUserName());
		        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
//		        sql.append(" 0 AS lastchildscount,");
				sql.append(" (SELECT COUNT(*) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds,");
				StringBuffer temp = new StringBuffer();
				temp.append("(SELECT b.acount FROM ");
				temp.append("(select a.codeitemid,a.codeitemdesc,sum(b.leafagechilds) acount  from ");
				temp.append(this.userView.getUserName()+this.userView.getUserName());
				temp.append("organization a,");
				temp.append(this.userView.getUserName()+this.userView.getUserName());
				temp.append("organization b ");
				temp.append(" where   b.leafagechilds>0 ");
				temp.append(" and a.leafagechilds>-1");
				temp.append(" and a.flag = b.flag ");
				temp.append(" and a.sort<b.sort");
				temp.append(" group by a.codeitemid,a.codeitemdesc ) b ");
				temp.append("where b.codeitemid = a.codeitemid )");
				sql.append(Sql_switcher.isnull(temp.toString(),"0"));
				sql.append("  AS myleafagechilds ");			
//		        sql.append(" 0 AS errorchilds ");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization a ");
				sql.append("order by a.sort ");
//				System.out.println(sql.toString());
				List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
				
				
				
				sql.delete(0,sql.length());
				sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts AS childs,");
				sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
				sql.append("(SELECT COUNT(*) counts from ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization) aa,(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
				sql.append("(SELECT COUNT(*) as counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
				sql.append(" (NOT EXISTS (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,");
				sql.append("(SELECT COUNT(*) counts");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND");
				sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
				sql.append(" (SELECT MIN(codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%') AND  (NOT EXISTS (SELECT *");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
				sql.append("(SELECT COUNT(*) counts");
				sql.append(" FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
				sql.append(SqlDifference.getJoinSymbol());
				sql.append("'%') AND (NOT EXISTS");
				sql.append(" (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
				sql.append("(SELECT COUNT(*) counts FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName());
				sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
				sql.append(" (SELECT * FROM ");
				sql.append(this.userView.getUserName()+this.userView.getUserName()); 
				sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
//				System.out.println(sql.toString());
				
				
				List rootnode=null;
				if(code==null || code.length()<=0)
				      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
				String rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
				url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false);
			}
		}
	
		this.getFormHM().put("url",url);

	}
	private void deleteTempTable(StringBuffer sql) {
		sql.delete(0,sql.length());
		sql.append("drop table ");
		sql.append(this.userView.getUserName()+this.userView.getUserName());
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
		sql.append(this.userView.getUserName()+this.userView.getUserName());
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
			  sql.append(this.userView.getUserName()+this.userView.getUserName());
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
			  sql.append(this.userView.getUserName()+this.userView.getUserName());
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
			  sql.append(this.userView.getUserName()+this.userView.getUserName());
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
			  sql.append(this.userView.getUserName()+this.userView.getUserName());
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
			  sql.append(this.userView.getUserName()+this.userView.getUserName());
			  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000 from organization where codeitemid like '");
			  sql.append(code);
			  sql.append("%' order by a0000,codeitemid,codesetid");				
			  break;
		  }
		  case Constant.DB2:
		  {
			  sql.delete(0,sql.length());
			  sql.append("INSERT INTO ");
			  sql.append(this.userView.getUserName()+this.userView.getUserName());
			  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000 from organization where codeitemid like '");
			  sql.append(code);
			  sql.append("%' order by a0000,codeitemid,codesetid");		
			  break;
		  }
		  case Constant.ORACEL:
		  {
			  sql.delete(0,sql.length());
			  sql.append("INSERT INTO ");
			  sql.append(this.userView.getUserName()+this.userView.getUserName());
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
    		 A0000sql.append(this.userView.getUserName()+this.userView.getUserName());
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
    
    public int updateTempTable(int i,StringBuffer sql,String constant,ContentDAO dao)
    {
    	 int number = 0;
    	 int gradevalue=i+1;
    	 try
    	 {
    		 sql.append("update ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization ");
    	     sql.append("set grade = "+gradevalue+" ");
    	     sql.append("where "+constant+" in ");
    	     sql.append("(select codeitemid  ");
    	     sql.append("from ");
    	     sql.append(this.userView.getUserName()+this.userView.getUserName());
    	     sql.append("organization ");			     
    	     sql.append("where ");
    	     sql.append("grade = "+i+" ");
    	     //sql.append("and codesetid = '@K')");
    	     System.out.println(sql.toString());
    	     number = dao.update(sql.toString());
//    	     System.out.println(number);
    	     sql.delete(0,sql.length());
    	     
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }	     
	     return number;
    }
   
    
    public void updateSort(String inputcodeitemid,float sortid,int inputflag,ContentDAO dao)
    {
    	StringBuffer sql  = new StringBuffer();
    	 sql.append("update ");
	     sql.append(this.userView.getUserName()+this.userView.getUserName());
	     sql.append("organization ");
	     sql.append("set sort = "+sortid+" ,flag="+inputflag+" ");
	     sql.append("where ");
	     sql.append(" codeitemid = '"+inputcodeitemid+"'");
	     //System.out.println(sql.toString());
	     try
	     {
	    	 dao.update(sql.toString());
	     }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }	
	     
    }
    
    public void searchChild(String inputcodeitemid,float sortid,int inputflag,ContentDAO dao)
    {
    	StringBuffer sql  = new StringBuffer();
    	 sql.append(" select * from  ");
	     sql.append(this.userView.getUserName()+this.userView.getUserName());
	     sql.append("organization ");
	     sql.append(" where parentid = '"+inputcodeitemid+"' ");
	     //System.out.println(sql.toString());	    
	     try
	     {
	    	 ArrayList templist = dao.searchDynaList(sql.toString());
	    	 for(int t=0;t<templist.size();t++)
    	     {
    	    	 this.setSortnumber(this.getSortnumber()+1);
    	    	 DynaBean dynabean=(DynaBean)templist.get(t);
    	    	 String codeitemid = (String)dynabean.get("codeitemid");
    		     this.updateSort(codeitemid,this.getSortnumber(),inputflag,dao);
    		     this.searchChild(codeitemid,this.getSortnumber(),inputflag,dao);
    	     }
	     }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }	
	     
    }
	public float getSortnumber() {
		return sortnumber;
	}
	public void setSortnumber(float sortnumber) {
		this.sortnumber = sortnumber;
	}
}
