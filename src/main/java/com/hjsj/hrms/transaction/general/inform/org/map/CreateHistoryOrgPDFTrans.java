package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hjsj.hrms.businessobject.general.orgmap.OrgMapBo;
import com.hjsj.hrms.businessobject.general.orgmap.ParameterBo;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateHistoryOrgPDFTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String code=(String)this.getFormHM().get("code");
		if(code==null || code!=null && code.trim().length()==0 && !this.userView.isSuper_admin()){
				code=userView.getManagePrivCodeValue();
				String busi=this.getBusi_org_dept(this.userView);
				//code=userview.getManagePrivCodeValue();
				if(busi.length()>2){
					if(busi.indexOf("`")!=-1){
						String[] tmps=busi.split("`");
							String a_code=tmps[0];
							if(a_code.length()>2){
								code=a_code.substring(2);
							}
					}else{
						code=busi.substring(2);
					}
				}else{
					code="#";
				}
		}
		String catalog_id=(String)this.getFormHM().get("catalog_id");
		OrgMapBo orgmapbo=new OrgMapBo();
		StringBuffer sql=new StringBuffer();
		String dbnameini="usr";
		ArrayList Dblist=userView.getPrivDbList();
		if(Dblist!=null&&Dblist.size()>0)
			dbnameini=Dblist.get(0).toString();
		else{
			dbnameini="";
		}
		HashMap paramehashmap=new SetOrgOptionParameter().ReadOutParameterXml("ORG_MAPOPTION",true,dbnameini,userView); 
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
		parameterbo.setDbname(paramehashmap.get("dbnames").toString());
		String dbname=paramehashmap.get("dbnames").toString();
		if(dbname==null||dbname.length()<=0)
		{
			paramehashmap.put("isshowpersonconut","false");
			paramehashmap.put("isshowpersonconut","isshowpersonname");
		}
		String url="";
		sql.append("select a0000 from hr_org_history where (A0000 is null or A0000 ='' or EXISTS(select A0000 from hr_org_history group by A0000 having count(A0000)>1)) and catalog_id='");
		sql.append(catalog_id);
		sql.append("'");
		//System.out.println(sql.toString());
		List rs1=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
		sql.delete(0,sql.length());
		try{
			if(!rs1.isEmpty() && rs1.size()>0)
			{
					int[] pagesize=orgmapbo.getHistoryOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName(),catalog_id);
					parameterbo.setPageheight(pagesize[1]);
					parameterbo.setPagespaceheight(40);
					parameterbo.setPagespacewidth(40);
					parameterbo.setPagewidth(pagesize[0]);
					//取消纸张大小限制 guodd 2014-12-01
//					if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)  
//					{
//						url=orgmapbo.createOrgMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
//						
//					}else
					{
						checkorg("hr_org_history",catalog_id);
						sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
						if(code!=null && code.length()>0)
						{
							sql.append(" - ");
							sql.append("(select grade from hr_org_history where catalog_id='");
							sql.append(catalog_id);
							sql.append("' and codeitemid='");
							sql.append(code);
							sql.append("') as grade");
						}		
						sql.append(", A0000,(SELECT COUNT(*) FROM hr_org_history b WHERE b.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and b.parentid LIKE a.codeitemid ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
				        sql.append(" FROM hr_org_history org WHERE org.catalog_id='");
				        sql.append(catalog_id);
				        sql.append("' and codeitemid <> childid AND (org.codeitemid LIKE a.codeitemid ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM hr_org_history orge WHERE orge.catalog_id='");
				        sql.append(catalog_id);
				        sql.append("' and orge.codeItemId = org.childId))) AS leafagechilds,(SELECT COUNT(*) FROM hr_org_history c WHERE c.catalog_id='");
				        sql.append(catalog_id);
				        sql.append("' and a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
						sql.append("(SELECT COUNT(*) FROM hr_org_history d WHERE d.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and d.parentid like (SELECT MIN(e.codeitemid) FROM hr_org_history e");
						sql.append(" WHERE e.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
				        sql.append(" (SELECT COUNT(*) FROM hr_org_history org WHERE org.catalog_id='");
				        sql.append(catalog_id);
				        sql.append("' and (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM hr_org_history m WHERE m.catalog_id='");
				        sql.append(catalog_id);
				        sql.append("' and m.parentid = a.codeitemid AND m.codeitemid <> m.parentid) ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM hr_org_history orge WHERE orge.catalog_id='");
				        sql.append(catalog_id);
				        sql.append("' and orge.codeItemId = org.childId))) AS firstchildscount,");
						sql.append("(SELECT COUNT(*) FROM hr_org_history f WHERE f.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and f.parentid like (SELECT MAX(g.codeitemid) FROM hr_org_history g");
						sql.append(" WHERE g.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and g.parentid = a.codeitemid AND g.codeitemid <> g.parentid) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
				        sql.append(" (SELECT COUNT(*) FROM hr_org_history org WHERE org.catalog_id='");
				        sql.append(catalog_id);
				        sql.append("' and (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM hr_org_history n WHERE n.catalog_id='");
				        sql.append(catalog_id);
				        sql.append("' and n.parentid = a.codeitemid AND n.codeitemid <> n.parentid) ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append("'%') AND (NOT EXISTS (SELECT * FROM hr_org_history orge WHERE orge.catalog_id='");
				        sql.append(catalog_id);
				        sql.append("' and orge.codeItemId = org.childId))) AS lastchildscount,");
						sql.append(" (SELECT COUNT(*) FROM hr_org_history org WHERE org.catalog_id='");
						sql.append(catalog_id);
						sql.append("' AND (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM hr_org_history orge WHERE orge.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and orge.codeItemId = org.childId))) AS errorchilds");
						sql.append(" FROM hr_org_history a where a.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and a.codeitemid like '");
						sql.append(code);
						sql.append("%' order by a.codeitemid");
						//System.out.println(sql.toString());
						List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
						sql.delete(0,sql.length());
					
						sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
						sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
						sql.append("(SELECT COUNT(*) AS nodecount from hr_org_history where catalog_id='");
						sql.append(catalog_id);
						sql.append("' and codeitemid like '");
						sql.append(code);
						sql.append("%') aa,");
						sql.append("(SELECT COUNT(*) as counts FROM hr_org_history b WHERE b.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
						sql.append(code);
						sql.append("%') bb,");
						sql.append("(SELECT COUNT(*) as counts FROM hr_org_history org WHERE org.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
						sql.append(" (NOT EXISTS (SELECT * FROM hr_org_history orge WHERE orge.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and orge.codeItemId = org.childId)) and org.codeitemid like '");
						sql.append(code);
						sql.append("%') cc,");
						sql.append("(SELECT COUNT(*) as counts FROM hr_org_history c  WHERE c.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and c.codeitemid = c.parentid and c.codeitemid like '");
						sql.append(code);
						sql.append("%') dd,");
						sql.append("(SELECT COUNT(*) as counts ");
						sql.append(" FROM hr_org_history d WHERE d.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and d.parentid LIKE (SELECT MIN(e.codeitemid) FROM hr_org_history e WHERE e.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and e.parentid = e.codeitemid) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append(" '%' AND");
						sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
						sql.append(code);
						sql.append("%') ae,");
						sql.append("(SELECT COUNT(*) as counts FROM hr_org_history org WHERE org.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and (codeitemid <> childid) AND (org.codeitemid LIKE ");
						sql.append(" (SELECT MIN(codeitemid) FROM hr_org_history m WHERE m.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and m.parentid = m.codeitemid) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%') AND  (NOT EXISTS (SELECT *");
						sql.append(" FROM hr_org_history orge  WHERE orge.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and orge.codeItemId = org.childId)) and org.codeitemid like '");
						sql.append(code);
						sql.append("%') ad,");
						sql.append("(SELECT COUNT(*)  as counts FROM hr_org_history f WHERE f.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and f.parentid LIKE ");
						sql.append("(SELECT MAX(g.codeitemid) FROM hr_org_history g WHERE g.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and g.codeitemid = g.parentid) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
						sql.append(code);
						sql.append("%') be,");
						sql.append("(SELECT COUNT(*) as counts");
						sql.append(" FROM hr_org_history org WHERE org.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM hr_org_history n WHERE n.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and n.codeitemid = n.parentid) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%') AND (NOT EXISTS");
						sql.append(" (SELECT * FROM hr_org_history orge WHERE orge.catalog_id='");
		                sql.append(catalog_id);
		                sql.append("' and orge.codeItemId = org.childId)) and org.codeitemid like '");
						sql.append(code);
						sql.append("%') bd,");
						sql.append("(SELECT COUNT(*) as counts FROM hr_org_history org WHERE org.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
						sql.append(" (SELECT * FROM hr_org_history orge WHERE orge.catalog_id='");
						sql.append(catalog_id);
						sql.append("' and orge.codeItemId = org.childId)) and org.codeitemid like '");
						sql.append(code);
						sql.append("%') ce");
						
						List rootnode=null;
						if(code==null || code.length()<=0)
						      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
						List rootdesclist=ExecuteSQL.executeMyQuery("select name from hr_org_catalog where catalog_id='"+ catalog_id + "'");
						String rootdesc="";
						for(int i=0;i<rootdesclist.size();i++)
						{
							LazyDynaBean orgmapbean=(LazyDynaBean)rootdesclist.get(0);
							rootdesc=orgmapbean.get("name").toString();
						}	
						url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,true);
				    }
			}
			else
			{
					int[] pagesize=orgmapbo.getHistoryOrgMapPageSize(parameterbo,code,this.getFrameconn(),this.userView.getUserName(),catalog_id);
					parameterbo.setPageheight(pagesize[1]);
					parameterbo.setPagespaceheight(40);
					parameterbo.setPagespacewidth(40);
					parameterbo.setPagewidth(pagesize[0]);
					
					//取消纸张大小限制 guodd 2014-12-01
//					if(parameterbo.getPagewidth()>14400 || parameterbo.getPageheight()>14400)
//					{
//						url=orgmapbo.createOrgMap(new ArrayList(),new ArrayList(),"",parameterbo,this.getFrameconn(),code,false);
//						
//					}else
					{
						deleteTempTable();
						createTempTable(code,catalog_id);
						checkorg(this.userView.getUserName() + "organization","");
						sql.delete(0,sql.length());
						sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
						if(code!=null && code.length()>0)
						{
							sql.append(" - ");
							sql.append("(select grade from ");
							sql.append(this.userView.getUserName());
							sql.append("organization where  codeitemid='");
							sql.append(code);
							sql.append("') as grade");
						}		
						sql.append(", A0000,");
						sql.append("(SELECT COUNT(*) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization b WHERE  b.parentid LIKE a.codeitemid ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
				        sql.append(" FROM ");
				        sql.append(this.userView.getUserName());
						sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
				        sql.append(this.userView.getUserName());
						sql.append("organization  orge WHERE orge.codeItemId = org.childId))) AS leafagechilds,(SELECT COUNT(*) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization  c WHERE  a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
						sql.append(" (SELECT COUNT(*) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization  d WHERE d.parentid like (SELECT codeitemid FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization ee WHERE  ee.a0000 =(SELECT MIN(e.a0000) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization e");
						sql.append(" WHERE  e.parentid = a.codeitemid AND e.codeitemid <> e.parentid)) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append(" '%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
				        sql.append(" (SELECT COUNT(*) FROM ");
				        sql.append(this.userView.getUserName());
						sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization ee WHERE  ee.a0000 =(SELECT MIN(a0000) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization m WHERE  m.parentid = a.codeitemid AND m.codeitemid <> m.parentid)) ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
				        sql.append(this.userView.getUserName());
						sql.append("organization orge WHERE  orge.codeItemId = org.childId))) AS firstchildscount,");
						sql.append("(SELECT COUNT(*) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization f WHERE  f.parentid like (SELECT codeitemid FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization gg WHERE  gg.a0000 =(SELECT MAX(g.a0000) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization g");
						sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid)) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append(" '%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
				        sql.append(" (SELECT COUNT(*) FROM ");
				        sql.append(this.userView.getUserName());
						sql.append("organization org WHERE  (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization nn WHERE  nn.a0000 =(SELECT MAX(a0000) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)) ");
				        sql.append(SqlDifference.getJoinSymbol());
				        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
				        sql.append(this.userView.getUserName());
						sql.append("organization orge WHERE  orge.codeItemId = org.childId))) AS lastchildscount,");
						sql.append(" (SELECT COUNT(*) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization orge WHERE  orge.codeItemId = org.childId))) AS errorchilds");
						sql.append(" FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization a where codeitemid like '");
						sql.append(code);
						//sql.append("%' order by a.A0000,a.codeitemid");
						sql.append("%' order by a.codeitemid");
						//System.out.println(sql.toString());
						List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
						sql.delete(0,sql.length());
						sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
						sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
						sql.append("(SELECT COUNT(*) AS nodecount from ");
						sql.append(this.userView.getUserName());
						sql.append("organization where  codeitemid like '");
						sql.append(code);
						sql.append("%') aa,");
						sql.append("(SELECT COUNT(*) as counts FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization b WHERE  b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
						sql.append(code);
						sql.append("%') bb,");
						sql.append("(SELECT COUNT(*) as counts FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization org WHERE  (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
						sql.append(" (NOT EXISTS (SELECT * FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization orge WHERE  orge.codeItemId = org.childId)) and org.codeitemid like '");
						sql.append(code);
						sql.append("%') cc,");
						sql.append("(SELECT COUNT(*) as counts FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization c  WHERE  c.codeitemid = c.parentid and c.codeitemid like '");
						sql.append(code);
						sql.append("%') dd,");
						sql.append("(SELECT COUNT(*) as counts ");
						sql.append(" FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization d WHERE  d.parentid LIKE (SELECT codeitemid FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization ee WHERE  ee.a0000 =(SELECT MIN(e.a0000) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization e WHERE  e.parentid = e.codeitemid)) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append(" '%' AND");
						sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
						sql.append(code);
						sql.append("%') ae,");
						sql.append("(SELECT COUNT(*) as counts FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization org WHERE  (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization ee WHERE  ee.a0000 =");
						sql.append(" (SELECT MIN(a0000) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization m WHERE  m.parentid = m.codeitemid)) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%') AND  (NOT EXISTS (SELECT *");
						sql.append(" FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization orge  WHERE  orge.codeItemId = org.childId)) and org.codeitemid like '");
						sql.append(code);
						sql.append("%') ad,");
						sql.append("(SELECT COUNT(*)  as counts FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization f WHERE  f.parentid LIKE (SELECT codeitemid");
						sql.append(" FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization gg WHERE  gg.a0000 = (SELECT MAX(g.a0000) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization g WHERE  g.codeitemid = g.parentid)) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
						sql.append(code);
						sql.append("%') be,");
						sql.append("(SELECT COUNT(*) as counts");
						sql.append(" FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization org WHERE  (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization nn WHERE  nn.a0000 =(SELECT MAX(a0000) FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization n WHERE  n.codeitemid = n.parentid)) ");
						sql.append(SqlDifference.getJoinSymbol());
						sql.append("'%') AND (NOT EXISTS");
						sql.append(" (SELECT * FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization orge WHERE  orge.codeItemId = org.childId)) and org.codeitemid like '");
						sql.append(code);
						sql.append("%') bd,");
						sql.append("(SELECT COUNT(*) as counts FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization org WHERE  (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
						sql.append(" (SELECT * FROM ");
						sql.append(this.userView.getUserName());
						sql.append("organization orge WHERE  orge.codeItemId = org.childId)) and org.codeitemid like '");
						sql.append(code);
						sql.append("%') ce");
						List rootnode=null;
						//System.out.println(sql.toString());
						if(code==null || code.length()<=0)
						      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
						List rootdesclist=ExecuteSQL.executeMyQuery("select name from hr_org_catalog where catalog_id='"+ catalog_id + "'");
						String rootdesc="";
						for(int i=0;i<rootdesclist.size();i++)
						{
							LazyDynaBean orgmapbean=(LazyDynaBean)rootdesclist.get(0);
							rootdesc=orgmapbean.get("name").toString();
						}	
						url=orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,true);
						//deleteTempTable();
					}
			 }
			this.getFormHM().put("url",PubFunc.encrypt(url));
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	private void deleteTempTable() {
		StringBuffer sql=new StringBuffer();
		sql.delete(0,sql.length());
		sql.append("drop table ");
		sql.append(this.userView.getUserName());
		sql.append("organization");
		try{
			KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
			kqUtilsClass.dropTable(this.userView.getUserName()+"organization");
		  //ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
			//ContentDAO dao=new ContentDAO(this.getFrameconn());
			//dao.update(sql.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		/*try{
		  //System.out.println(sql.toString());
		  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
		}catch(Exception e)
		{
			e.printStackTrace();
		}*/
	}
	private void createTempTable(String code,String catalog_id) {
		
		StringBuffer sql=new StringBuffer();
		try{
			KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
			kqUtilsClass.dropTable(this.userView.getUserName()+"organization");
		  //ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
			//ContentDAO dao=new ContentDAO(this.getFrameconn());
			//dao.update(sql.toString());
		}catch(Exception e)
		{
			//e.printStackTrace();aiwen^521
			
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
			  /*sql.append("CREATE TABLE ");
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
			  sql.append("CREATE TABLE ");*/		
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
			  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000 from hr_org_history where catalog_id='");
			  sql.append(catalog_id);
			  sql.append("' and codeitemid like '");
			  sql.append(code);
			  sql.append("%' order by a0000,codeitemid,codesetid");				
			  break;
		  }
		  case Constant.DB2:
		  {
			  sql.delete(0,sql.length());
			  sql.append("INSERT INTO ");
			  sql.append(this.userView.getUserName());
			  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000 from hr_org_history where catalog_id='");
			  sql.append(catalog_id);
			  sql.append("' and codeitemid like '");
			  sql.append(code);
			  sql.append("%' order by a0000,codeitemid,codesetid");						
			  break;
		  }
		  case Constant.ORACEL:
		  {
			  sql.delete(0,sql.length());
			  sql.append("INSERT INTO ");
			  sql.append(this.userView.getUserName());
			  sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, RowNum from hr_org_history where catalog_id='");
			  sql.append(catalog_id);
			  sql.append("' and codeitemid like '");
			  sql.append(code);
			  sql.append("%' order by a0000,codeitemid,codesetid");						
			  break;
		  }
		
		}
   
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		try{
			//System.out.println(sql.toString());
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
    private void  checkorg(String tablename,String catalog_id)
	{
		 StringBuffer sql =new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try{
			 sql.delete(0,sql.length());
			 if(catalog_id!=null && catalog_id.length()>0)
			 {
				 sql.append("UPDATE ");
			     sql.append(tablename);
			     sql.append(" SET childid =(SELECT MIN(codeitemid) FROM ");
			     sql.append(tablename + " d");
			     sql.append(" WHERE d.catalog_id='");
			     sql.append(catalog_id);
			     sql.append("' and d.parentid = ");
			     sql.append(tablename);
				 sql.append(".codeitemid AND  d.parentid <> d.codeitemid)");
			     sql.append(" WHERE  EXISTS (SELECT * FROM ");
			     sql.append(tablename);
			     sql.append(" c");
			     sql.append(" WHERE c.catalog_id='");
			     sql.append(catalog_id);
			     sql.append("' and c.parentid = ");
			     sql.append(tablename);
			     sql.append(".codeitemid AND c.parentid <> c.codeitemid)");
			     sql.append(" and ");
			     sql.append(tablename);
			     sql.append(".catalog_id='");
			     sql.append(catalog_id);
			     sql.append("'");
			 }else
			 {
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
			 }
		     //System.out.println(sql.toString());
		     dao.update(sql.toString());
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
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
