package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hjsj.hrms.businessobject.general.inform.map.CreateOrgMap;
import com.hjsj.hrms.businessobject.general.orgmap.OrgMapExcelBo;
import com.hjsj.hrms.businessobject.general.orgmap.ParameterBo;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * <p>Title:生成组织机构图（EXCEL）</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 16, 2008:9:40:41 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class CreateOrgMapEXCELTrans extends IBusiness{
	
	private boolean bShowPos=true;
	private boolean bShowDept=true;
	private int iDeptlevel=-1;
	private int iUnitlevel=-1;
	private String backdate;
	private String tmpTableNamePrefix;
	private int rownum;
	private String SelectCondition="";
	private ArrayList sqlList = new ArrayList();
	public void execute() throws GeneralException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		backdate=(String)this.getFormHM().get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		tmpTableNamePrefix="t#"+this.userView.getUserName();
		HSSFWorkbook workbook=null;
		FileOutputStream fileOut=null;
		// TODO Auto-generated method stub
		try{
			String code=(String)this.getFormHM().get("code");
			String kind=(String)this.getFormHM().get("kind");
			if(code==null || code.trim().length()==0){
				//code=userView.getManagePrivCodeValue();
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
			if(kind==null || kind.trim().length()==0){
				String codesetid=userView.getManagePrivCode();
				if("UM".equalsIgnoreCase(codesetid))
					kind="1";
				else
					kind="2";
			}
			String orgtype=(String)this.getFormHM().get("orgtype");
			if(orgtype==null||orgtype.length()<=0)
				orgtype="org";
			OrgMapExcelBo orgmapbo = new OrgMapExcelBo();
			orgmapbo.setOrgtype(orgtype);
			StringBuffer sql=new StringBuffer();
			String dbnameini="usr";
			ArrayList Dblist=userView.getPrivDbList(); //得到人员库
			
			workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
			String url="Orgmap_"+PubFunc.getStrg()+ ".xls";
			if(Dblist!=null&&Dblist.size()>0)
				dbnameini=Dblist.get(0).toString();
			else{
				dbnameini="";
			}
			/*
			 * 页面设置属性
			 */
			HashMap paramehashmap=new SetOrgOptionParameter().ReadOutParameterXml("ORG_MAPOPTION",true,dbnameini,userView);
			ParameterBo parameterbo=new ParameterBo();
			parameterbo.setCellaspect(paramehashmap.get("cellaspect").toString()); //单元格水平垂直方向
			parameterbo.setCellcolor(paramehashmap.get("cellcolor").toString());  //单元格颜色
			parameterbo.setCellheight(paramehashmap.get("cellheight").toString());  //单元格高
			parameterbo.setCellhspacewidth(paramehashmap.get("cellhspacewidth").toString());  //左右间距
			parameterbo.setCellletteraligncenter((String)paramehashmap.get("cellletteraligncenter"));  //水平居中
			parameterbo.setCellletteralignleft((String)paramehashmap.get("cellletteralignleft"));  //水平居左
			parameterbo.setCellletteralignright((String)paramehashmap.get("cellletteralignright"));  //水平居右
			parameterbo.setCellletterfitline((String)paramehashmap.get("cellletterfitline"));  //自动换行
			parameterbo.setCellletterfitsize((String)paramehashmap.get("cellletterfitsize")); //自动适应
			parameterbo.setCelllettervaligncenter((String)paramehashmap.get("celllettervaligncenter")); //上下居中
			parameterbo.setCelllinestrokewidth((String)paramehashmap.get("celllinestrokewidth"));  //线宽
			parameterbo.setCellshape((String)paramehashmap.get("cellshape"));  //单元格形状1矩形2..
			parameterbo.setCellvspacewidth((String)paramehashmap.get("cellvspacewidth"));  //上下间距
			parameterbo.setCellwidth((String)paramehashmap.get("cellwidth"));  //单元格宽
			parameterbo.setFontcolor((String)paramehashmap.get("fontcolor"));  //颜色
			parameterbo.setFontfamily((String)paramehashmap.get("fontfamily")); //字体
			parameterbo.setFontsize((String)paramehashmap.get("fontsize"));  //大小
			parameterbo.setFontstyle((String)paramehashmap.get("fontstyle"));  //字形
			parameterbo.setGraph3d((String)paramehashmap.get("graph3d"));  //图形效果
			parameterbo.setGraphaspect((String)paramehashmap.get("graphaspect")); //图形水平方向图形垂直方向*
			parameterbo.setIsshowpersonconut((String)paramehashmap.get("isshowpersonconut"));  //显示人数
			parameterbo.setIsshowpersonname((String)paramehashmap.get("isshowpersonname"));  //显示姓名
			parameterbo.setIsshowposname((String)paramehashmap.get("isshowposname"));  //显示部门
			parameterbo.setNamesinglecell((String)paramehashmap.get("namesinglecell")); //同级人员姓名是否显示在一个单元格中
			parameterbo.setDbname((String)paramehashmap.get("dbnames"));  //人员库类型
			String dbname=paramehashmap.get("dbnames").toString();
			parameterbo.setIsshowdeptname((String)paramehashmap.get("isshowdeptname"));
			parameterbo.setDeptlevel((String)paramehashmap.get("deptlevel"));
			if(dbname==null||dbname.length()<=0)
			{
				paramehashmap.put("isshowpersonconut","false");
				paramehashmap.put("isshowpersonconut","isshowpersonname");
			}
			String isshowdeptname=(String)paramehashmap.get("isshowdeptname");		
			String deptlevel=(String)paramehashmap.get("deptlevel");
			if(deptlevel!=null&&deptlevel.length()>0)
			{
				this.iDeptlevel=Integer.parseInt(deptlevel);
				if(kind!=null&& "1".equals(kind))
					this.iDeptlevel++;
			}	
			String unitlevel=(String)paramehashmap.get("unitlevel");
			if(unitlevel!=null&&unitlevel.length()>0)
			{
				this.iUnitlevel=Integer.parseInt(unitlevel);
				if(kind!=null&& "2".equals(kind))
					this.iUnitlevel++;
			}
				
			if(isshowdeptname==null||isshowdeptname.length()<=0|| "false".equals(isshowdeptname))
				this.bShowDept=false;	
			String isshowposname=(String)paramehashmap.get("isshowposname");		
			if(isshowposname==null||isshowposname.length()<=0|| "false".equals(isshowposname))
				this.bShowPos=false;
			if(!this.bShowDept)
			{
				/*paramehashmap.put("isshowpersonconut","false");
				paramehashmap.put("isshowpersonname","false");
				parameterbo.setIsshowpersonconut("false");
				parameterbo.setIsshowpersonname("false");*/
				paramehashmap.put("isshowpersonname","false");
				parameterbo.setIsshowpersonname("false");
			}
			CreateOrgMap createOrgMap=new CreateOrgMap(this.userView,this.getFrameconn(),bShowDept,bShowPos,iDeptlevel,backdate,iUnitlevel);
			String rootdesc="";
			try {
				
				 Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
				 rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
				 if(rootdesc==null||rootdesc.length()<=0)
				 {
					 rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
				 }
				 rootdesc=rootdesc!=null&&rootdesc.length()>0?rootdesc:"";
			}catch(Exception e)
			{
				
			}
			//根据不同的条件查询出不同的信息
			if(1 == 1){
				/********显示人数,显示姓名***********/
				if(orgtype!=null&& "vorg".equalsIgnoreCase(orgtype)){
					if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
					{
						/*********删除临时表*********/
						sql.delete(0,sql.length());
						sql.append("drop table ");
						sql.append(tmpTableNamePrefix);
						sql.append("organization");
						try{
							KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
							kqUtilsClass.dropTable(tmpTableNamePrefix+"organization");
						  //ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
							//ContentDAO dao=new ContentDAO(this.getFrameconn());
							//dao.update(sql.toString());
						}catch(Exception e)
						{
							//e.printStackTrace();aiwen^521
							
						}
						/*********创建临时表*********/
						sql.delete(0,sql.length());
						sql.append("CREATE TABLE ");
						sql.append(tmpTableNamePrefix);
						sql.append("organization (codesetid varchar (2),");
						sql.append("codeitemid varchar (100) not null,");
						sql.append("codeitemdesc varchar (200),");
						sql.append("parentid varchar (100),");
						sql.append("childid varchar (100),");
						sql.append("state varchar (10),");
						sql.append("a0000 int null,");
						sql.append("grade int)");
						try{
							  /*********建立主键*********/
							  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
							  sql.delete(0,sql.length());
							  sql.append("ALTER TABLE ");
							  sql.append(tmpTableNamePrefix);
							  sql.append("organization ADD PRIMARY KEY (codeitemid)");
							  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());					  
					    }catch(Exception e)
						{
							e.printStackTrace();
						}	
					    sql.delete(0,sql.length());
					    /*********插入code下面的组织*********/
					    sql.append("INSERT INTO ");
					    sql.append(tmpTableNamePrefix);
					    sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)select codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000 from vorganization where codeitemid like '");
					    sql.append(code);
					    sql.append("%'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					    sql.append("and (view_chart<>1 or view_chart is null)");
					    ContentDAO dao=new ContentDAO(this.getFrameconn());
					    
					    try{
					     /*dao.insert(sql.toString(),new ArrayList());
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
					     sql.append("a01 a,vorganization c,t_vorg_staff e where a.e01a1 is not null and c.codeitemid=a.e01a1 ");
					     switch(Sql_switcher.searchDbServer())
						 {
							  case Constant.MSSQL:
							  {
								  sql.append(" and c.codeitemid like '"+code+"%'");
								  break;
							  }
							  case Constant.ORACEL:
							  {
								  sql.append(" and substr(c.codeitemid,0,"+code.length()+")='"+code+"'");
								  break;
							  }
							  case Constant.DB2:
							  {
								  sql.append(" and substr(c.codeitemid,0,"+code.length()+")='"+code+"'");
								  break;
							  }
						 }
					     //System.out.println(sql.toString());
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
					     sql.append("a01 a,vorganization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%'");*/
					     dao.insert(sql.toString(),new ArrayList());
					     sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',e.b0110 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,e.b0110,e.b0110 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=e.b0110)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=e.b0110) from ");
					     sql.append(dbname);
					     sql.append("a01 a,vorganization c,t_vorg_staff e where a.b0110 is not null  and e.b0110=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%' and e.state=1 and a.a0100=e.a0100 and e.dbase='"+dbname+"'");
					     dao.insert(sql.toString(),new ArrayList());
					     sql.delete(0,sql.length());
					     sql.append("UPDATE ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization d");
					     sql.append(" WHERE d.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
					     sql.append(" WHERE  EXISTS (SELECT * FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization c");
					     sql.append(" WHERE c.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
					     dao.update(sql.toString());
					    }catch(Exception e)
						{
					    	e.printStackTrace();
					    }
					    setEmpGradeNull(tmpTableNamePrefix+"organization");
					    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);
					    
						
				            sql.delete(0,sql.length());
							sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}			
							sql.append(", A0000,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid and b.codesetid='zz') +  (SELECT COUNT(*)");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codesetid='zz' and codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS personcount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE   m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization a order by a.codeitemid,a.codesetid");
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts AS childs,");
							sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
							sql.append("(SELECT COUNT(*) counts from ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization) aa,(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,");
							sql.append("(SELECT COUNT(*) counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
							sql.append(" (SELECT MIN(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
							sql.append("(SELECT COUNT(*) counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
						
						
						deleteTempTable(sql);
					}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
					{
						/********显示姓名***********/
						
					    sql.delete(0,sql.length());
					    createTempTable(code, kind,sql,orgtype);
					    ContentDAO dao=new ContentDAO(this.getFrameconn());
					    
					    try{
					     //dao.insert(sql.toString(),new ArrayList());
					     if(this.bShowPos)
					     {
					    	 sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
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
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e01a1) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1 and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     dao.insert(sql.toString(),new ArrayList());
						     sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
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
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     dao.insert(sql.toString(),new ArrayList());
					     }else
					     {
					    	 sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
						     sql.append("select 'zz',e.b0110 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,a.a0101,e.b0110,e.b0110 ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" ' ' ");
						     sql.append(SqlDifference.getJoinSymbol());
						     sql.append(" a.a0100,'',(select grade from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=e.b0110)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=e.b0110) from ");
						     sql.append(dbname);
						     sql.append("a01 a,vorganization c,t_vorg_staff e where a.b0110 is not null  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and e.b0110=c.codeitemid and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%' and e.state=1 and a.a0100=e.a0100 and e.dbase='"+dbname+"'");
						     dao.insert(sql.toString(),new ArrayList());
					     }
					     sql.delete(0,sql.length());
					     sql.append("UPDATE ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization d");
					     sql.append(" WHERE d.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
					     sql.append(" WHERE  EXISTS (SELECT * FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization c");
					     sql.append(" WHERE c.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
					     dao.update(sql.toString());
					    }catch(Exception e)
						{
					    	e.printStackTrace();
					    }
					    setEmpGradeNull(tmpTableNamePrefix+"organization");
					    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);
						
						    
			                sql.delete(0,sql.length());
							sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}			
							sql.append(", A0000,(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE  m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE  g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND  a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization a order by a.codeitemid,a.codesetid");
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.counts AS childs,'ren' AS subhead,");
							sql.append("ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts as  lastchildscount,ax.counts AS errorchilds from ");
							sql.append("(SELECT COUNT(*) AS counts from ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization) aa,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid) dd, (SELECT COUNT(*) counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
							sql.append(" (SELECT MIN(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
							sql.append("(SELECT COUNT(*) counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
						
						
					
					}
					else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
					{				
						/********显示人数***********/
						int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);	
						
							checkorg("vorganization");
							sql.append("SELECT codesetid, codeitemid, codeitemdesc  " + orgmapbo.ConverDBsql(dbname) + " AS text, parentid, childid, a.state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from vorganization where codeitemid='");
								sql.append(code);
								sql.append("' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (view_chart<>1 or view_chart is null)) as grade");
							}			
							sql.append(",(SELECT COUNT(*) FROM vorganization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null) AND (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM vorganization c WHERE a.codeitemid = c.parentid and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM vorganization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM organization e");
							sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid  and "+Sql_switcher.dateValue(backdate)+" between e.start_date and e.end_date and (e.view_chart<>1 or e.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' and "+Sql_switcher.dateValue(backdate)+" between d.start_date and d.end_date and (d.view_chart<>1 or d.view_chart is null) AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM vorganization org WHERE (codeitemid <> childid) and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM vorganization m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and "+Sql_switcher.dateValue(backdate)+" between m.start_date and m.end_date and (m.view_chart<>1 or m.view_chart is null)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM vorganization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM vorganization g");
							sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid  and "+Sql_switcher.dateValue(backdate)+" between g.start_date and g.end_date and (g.view_chart<>1 or g.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' and "+Sql_switcher.dateValue(backdate)+" between f.start_date and f.end_date and (f.view_chart<>1 or f.view_chart is null) AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM vorganization org WHERE (codeitemid <> childid) and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM vorganization n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid  and "+Sql_switcher.dateValue(backdate)+" between n.start_date and n.end_date and (n.view_chart<>1 or n.view_chart is null)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM vorganization org WHERE (codeitemid <> childid) and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" From ");
							sql.append(dbname);
							sql.append("A01 RIGHT OUTER JOIN");
							sql.append("  t_vorg_staff ON ");
			        		sql.append(""+dbname+"A01.a0100=t_vorg_staff.a0100 ");
			        		sql.append(" and  t_vorg_staff.state=1  and Upper(t_vorg_staff.dbase)='"+dbname.toUpperCase()+"'");
							sql.append(" RIGHT OUTER join vorganization a ON ");
							/*sql.append(Sql_switcher.substr(dbname + "A01.B0110","1",Sql_switcher.length("a.codeitemid"))); 	              
							sql.append("= a.codeitemid OR ");
							sql.append(Sql_switcher.substr(dbname + "A01.e0122","1",Sql_switcher.length("a.codeitemid"))); 	    
							sql.append("= a.codeitemid OR ");
							sql.append(Sql_switcher.substr(dbname + "A01.e01a1","1",Sql_switcher.length("a.codeitemid"))); 	    
							sql.append("= a.codeitemid");*/
							sql.append(" t_vorg_staff.b0110=a.codeitemid ");
							sql.append(" where a.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date and (a.view_chart<>1 or a.view_chart is null) GROUP BY a.codesetid,a.codeitemid,a.codeitemdesc,a.parentid,a.childid");
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
							sql.append("(SELECT COUNT(*) AS nodecount from vorganization where codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (view_chart<>1 or view_chart is null)) aa,");
							sql.append("(SELECT COUNT(*) as counts FROM vorganization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between b.start_date and b.end_date and (b.view_chart<>1 or b.view_chart is null)) bb,");
							sql.append("(SELECT COUNT(*) as counts FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) cc,");
							sql.append("(SELECT COUNT(*) as counts FROM vorganization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null)) dd,");
							sql.append("(SELECT COUNT(*) as counts ");
							sql.append(" FROM organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM vorganization e WHERE e.parentid = e.codeitemid and "+Sql_switcher.dateValue(backdate)+" between e.start_date and e.end_date and (e.view_chart<>1 or e.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
							sql.append(code);
							sql.append("%'  and "+Sql_switcher.dateValue(backdate)+" between d.start_date and d.end_date and (d.view_chart<>1 or d.view_chart is null)) ae,");
							sql.append("(SELECT COUNT(*) as counts FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
							sql.append(" (SELECT MIN(codeitemid) FROM vorganization m WHERE m.parentid = m.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between m.start_date and m.end_date and (m.view_chart<>1 or m.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM vorganization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) ad,");
							sql.append("(SELECT COUNT(*)  as counts FROM vorganization f WHERE f.parentid LIKE ");
							sql.append("(SELECT MAX(g.codeitemid) FROM vorganization g WHERE g.codeitemid = g.parentid  and "+Sql_switcher.dateValue(backdate)+" between g.start_date and g.end_date and (g.view_chart<>1 or g.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between f.start_date and f.end_date and (f.view_chart<>1 or f.view_chart is null)) be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM vorganization n WHERE n.codeitemid = n.parentid and "+Sql_switcher.dateValue(backdate)+" between n.start_date and n.end_date and (n.view_chart<>1 or n.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) bd,");
							sql.append("(SELECT COUNT(*) as counts FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) ce");
							
							
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());					
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
						
					}
					else
					{
						int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);//级别
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);//个数
						
							checkorg("organization");
							sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from vorganization where codeitemid='");
								sql.append(code);
								sql.append("' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (view_chart<>1 or view_chart is null)) as grade");
							}		
							sql.append(", A0000,(SELECT COUNT(*) FROM vorganization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' and "+Sql_switcher.dateValue(backdate)+" between b.start_date and b.end_date and (b.view_chart<>1 or b.view_chart is null) AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%')  and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null) AND (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM vorganization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null)) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM vorganization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM vorganization e");
							sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and "+Sql_switcher.dateValue(backdate)+" between e.start_date and e.end_date and (e.view_chart<>1 or e.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND d.parentid <> d.codeitemid and "+Sql_switcher.dateValue(backdate)+" between d.start_date and d.end_date and (d.view_chart<>1 or d.view_chart is null) AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM vorganization m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and "+Sql_switcher.dateValue(backdate)+" between m.start_date and m.end_date and (m.view_chart<>1 or m.view_chart is null)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null) AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM vorganization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM vorganization g");
							sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and "+Sql_switcher.dateValue(backdate)+" between g.start_date and g.end_date and (g.view_chart<>1 or g.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' and "+Sql_switcher.dateValue(backdate)+" between f.start_date and f.end_date and (f.view_chart<>1 or f.view_chart is null) AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM vorganization n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and "+Sql_switcher.dateValue(backdate)+" between n.start_date and n.end_date and (n.view_chart<>1 or n.view_chart is null)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date  AND (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM vorganization org WHERE (codeitemid <> childid)  and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" FROM vorganization a where a.codeitemid like '");
							sql.append(code);
							sql.append("%'  and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date and (a.view_chart<>1 or a.view_chart is null) order by a.codeitemid");
		                    //System.out.println(sql.toString());
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							
							sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
							sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
							sql.append("(SELECT COUNT(*) AS nodecount from vorganization where codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (view_chart<>1 or view_chart is null)) aa,");
							sql.append("(SELECT COUNT(*) as counts FROM vorganization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between b.start_date and b.end_date and (b.view_chart<>1 or b.view_chart is null)) bb,");
							sql.append("(SELECT COUNT(*) as counts FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) cc,");
							sql.append("(SELECT COUNT(*) as counts FROM vorganization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date  and (c.view_chart<>1 or c.view_chart is null)) dd,");
							sql.append("(SELECT COUNT(*) as counts ");
							sql.append(" FROM vorganization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM vorganization e WHERE e.parentid = e.codeitemid and "+Sql_switcher.dateValue(backdate)+" between e.start_date and e.end_date  and (e.view_chart<>1 or e.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between d.start_date and d.end_date  and (d.view_chart<>1 or d.view_chart is null)) ae,");
							sql.append("(SELECT COUNT(*) as counts FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
							sql.append(" (SELECT MIN(codeitemid) FROM vorganization m WHERE m.parentid = m.codeitemid and "+Sql_switcher.dateValue(backdate)+" between m.start_date and m.end_date  and (m.view_chart<>1 or m.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM vorganization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date  and (org.view_chart<>1 or org.view_chart is null)) ad,");
							sql.append("(SELECT COUNT(*)  as counts FROM vorganization f WHERE f.parentid LIKE ");
							sql.append("(SELECT MAX(g.codeitemid) FROM vorganization g WHERE g.codeitemid = g.parentid and "+Sql_switcher.dateValue(backdate)+" between g.start_date and g.end_date  and (g.view_chart<>1 or g.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between f.start_date and f.end_date  and (f.view_chart<>1 or f.view_chart is null)) be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM vorganization n WHERE n.codeitemid = n.parentid and "+Sql_switcher.dateValue(backdate)+" between n.start_date and n.end_date  and (n.view_chart<>1 or n.view_chart is null)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date  and (org.view_chart<>1 or org.view_chart is null)) bd,");
							sql.append("(SELECT COUNT(*) as counts FROM vorganization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM vorganization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) ce");
							//System.out.println(sql.toString());
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
						
					}
				}else{
					if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
					{
						/*********删除临时表*********/
						sql.delete(0,sql.length());
						sql.append("drop table ");
						sql.append(tmpTableNamePrefix);
						sql.append("organization");
						try{
							KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
							kqUtilsClass.dropTable(tmpTableNamePrefix+"organization");
						  //ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
							//ContentDAO dao=new ContentDAO(this.getFrameconn());
							//dao.update(sql.toString());
						}catch(Exception e)
						{
							//e.printStackTrace();aiwen^521
							
						}
						/*********创建临时表*********/
						sql.delete(0,sql.length());
						
						createTempTable(code,kind,sql,orgtype);
						ContentDAO dao=new ContentDAO(this.getFrameconn());
					    sql.delete(0,sql.length());
					    /*********插入code下面的组织*********/
						try{
							sql.delete(0,sql.length());
							if(this.bShowPos){
								sql.append("insert into t#");
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
								sql.append(" a.a0100,'',(select grade from t#");
								sql.append(this.userView.getUserName());
								sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from t#");
								sql.append(this.userView.getUserName());
								sql.append("organization b where b.codeitemid=a.e01a1) from ");
								sql.append(dbname);
								sql.append("a01 a,organization c where a.e01a1 is not null and "
										+ Sql_switcher.dateValue(backdate)
										+ " between c.start_date and c.end_date and (c.view_chart is null or c.view_chart<>1) and c.codeitemid=a.e01a1 ");
								switch (Sql_switcher.searchDbServer()) {
								case Constant.MSSQL: {
									sql.append(" and c.codeitemid like '"
											+ code + "%'");
									break;
								}
								case Constant.ORACEL: {
									// sql.append(" and substr(c.codeitemid,0,"+code.length()+")='"+code+"'");
									sql.append(" and c.codeitemid like '"
											+ code + "%'");
									break;
								}
								case Constant.DB2: {
									sql.append(" and substr(c.codeitemid,0,"
											+ code.length() + ")='" + code
											+ "'");
									break;
								}
								}
								/*
								 * if(!this.userView.isSuper_admin()) { String
								 * whereRen
								 * =InfoUtils.getWhereINSql(this.userView
								 * ,dbname);
								 * sql.append(" and a.a0100 in(select a0100 "
								 * +whereRen+")"); }
								 */
								// System.out.println(sql.toString());
								dao.insert(sql.toString(), new ArrayList());
								sql.delete(0, sql.length());
								sql.append("insert into t#");
								sql.append(this.userView.getUserName());
								sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
								sql.append("select 'zz',a.e0122 ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" 'zz' ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" a.a0100,a.a0101,a.e0122,a.e0122 ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" 'zz' ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" a.a0100,'',(select grade from t#");
								sql.append(this.userView.getUserName());
								sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from t#");
								sql.append(this.userView.getUserName());
								sql.append("organization b where b.codeitemid=a.e0122) from ");
								sql.append(dbname);
								sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='') and "
										+ Sql_switcher.dateValue(backdate)
										+ " between c.start_date and c.end_date and (c.view_chart is null or c.view_chart<>1) and c.codeitemid like '");
								sql.append(code);
								sql.append("%'");
								/*
								 * if(!this.userView.isSuper_admin()) { String
								 * whereRen
								 * =InfoUtils.getWhereINSql(this.userView
								 * ,dbname);
								 * sql.append(" and a.a0100 in(select a0100 "
								 * +whereRen+")"); }
								 */
								dao.insert(sql.toString(), new ArrayList());
								sql.delete(0, sql.length());
								sql.append("insert into t#");
								sql.append(this.userView.getUserName());
								sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
								sql.append("select 'zz',a.b0110 ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" 'zz' ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" a.a0100,a.a0101,a.b0110,a.b0110 ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" 'zz' ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" a.a0100,'',(select grade from t#");
								sql.append(this.userView.getUserName());
								sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from t#");
								sql.append(this.userView.getUserName());
								sql.append("organization b where b.codeitemid=a.b0110) from ");
								sql.append(dbname);
								sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and "
										+ Sql_switcher.dateValue(backdate)
										+ " between c.start_date and c.end_date and (c.view_chart is null or c.view_chart<>1) and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
								sql.append(code);
								sql.append("%'");
								/*
								 * if(!this.userView.isSuper_admin()) { String
								 * whereRen
								 * =InfoUtils.getWhereINSql(this.userView
								 * ,dbname);
								 * sql.append(" and a.a0100 in(select a0100 "
								 * +whereRen+")"); }
								 */
								dao.insert(sql.toString(), new ArrayList());

								sql.delete(0, sql.length());
								sql.append("insert into t#");
								sql.append(this.userView.getUserName());
								sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
								sql.append("select 'xn',e.b0110 ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" ' ' ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" a.a0100,a.a0101,e.b0110,e.b0110 ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" ' ' ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" a.a0100,'',(select grade from t#");
								sql.append(this.userView.getUserName());
								sql.append("organization b where b.codeitemid=e.b0110)+1,(select a0000 from t#");
								sql.append(this.userView.getUserName());
								sql.append("organization b where b.codeitemid=e.b0110) from ");
								sql.append(dbname);
								sql.append("a01 a,vorganization c,t_vorg_staff e where a.b0110 is not null and "
										+ Sql_switcher.dateValue(backdate)
										+ " between c.start_date and c.end_date and (c.view_chart is null or c.view_chart<>1) and e.b0110=c.codeitemid  and c.codeitemid like '");
								sql.append(code);
								sql.append("%' and e.state=1 and a.a0100=e.a0100 and upper(e.dbase)='"
										+ dbname.toUpperCase() + "'");

								dao.insert(sql.toString(), new ArrayList());

								sql.delete(0, sql.length());
								sql.append("delete t#");
								sql.append(this.userView.getUserName());
								sql.append("organization where parentid not in (select codeitemid from t#");
								sql.append(this.userView.getUserName());
								sql.append("organization) and codeitemid=childid and (select count(*) from t#");
								sql.append(this.userView.getUserName());
								sql.append("organization where codesetid<>'zz')>1 ");

								dao.delete(sql.toString(), new ArrayList());
							}else
							{
								//查询数人员数据库+A01中的数据跟插入想对应的suorganization表中，把同一单位的人员都放在单位级别下面
								 sql.delete(0,sql.length());
							     sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
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
							    // sql.append(this.userView.getUserName());
							     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e0122) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     dao.insert(sql.toString(),new ArrayList());
							     sql.delete(0,sql.length());
							     sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
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
							    // sql.append(this.userView.getUserName());
							     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.b0110) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     dao.insert(sql.toString(),new ArrayList());
							}
							 sql.delete(0,sql.length());
						     sql.append("UPDATE "); //更新 下级指标
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization d");
						     sql.append(" WHERE d.parentid = ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
						     sql.append(" WHERE  EXISTS (SELECT * FROM ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization c");
						     sql.append(" WHERE c.parentid = ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
						     dao.update(sql.toString());
						}catch(Exception e){
							e.printStackTrace();
						}
						setEmpGradeNull(tmpTableNamePrefix+"organization");
						int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);
						
							sql.delete(0,sql.length());
							sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0){
								//全部没有走这里
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}
							sql.append(", A0000,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid and ((a.codesetid<>'VO' and  b.codesetid='zz') or (a.codesetid='VO' and b.codesetid='xn'))) +  (SELECT COUNT(*)");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (((a.codesetid<>'VO' and  codesetid='zz') or (a.codesetid='VO' and codesetid='xn')) and codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS personcount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (select min(codeitemid) from "+tmpTableNamePrefix+"organization where a0000=(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) and a.codesetid<>'@K' and codeitemid <> a.codeitemid )");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (select min(codeitemid) from "+tmpTableNamePrefix+"organization where a0000=(SELECT MIN(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE   m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) and a.codesetid<>'@K' and codeitemid <> a.codeitemid )");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (select max(codeitemid) from "+tmpTableNamePrefix+"organization where a0000=(SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) and codesetid<>'zz' and a.codesetid<>'@K' and codeitemid <> a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (select max(codeitemid) from "+tmpTableNamePrefix+"organization where a0000=(SELECT MAX(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) and codesetid<>'zz' and a.codesetid<>'@K' and codeitemid <> a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization a order by a.a0000,a.codeitemid,a.codesetid");
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts AS childs,");
							sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
							sql.append("(SELECT COUNT(*) counts from ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization) aa,(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,");
							sql.append("(SELECT COUNT(*) counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
							sql.append(" (SELECT MIN(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
							sql.append("(SELECT COUNT(*) counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							//写入excel
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
						
						deleteTempTable(sql);
					}else{
						//显示人数并且显示姓名
						if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString())){
							/*********删除临时表*********/
							sql.delete(0,sql.length());
							sql.append("drop table ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization");
							try{
								KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
								kqUtilsClass.dropTable(tmpTableNamePrefix+"organization");
							  //ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
								//ContentDAO dao=new ContentDAO(this.getFrameconn());
								//dao.update(sql.toString());
							}catch(Exception e)
							{
								//e.printStackTrace();aiwen^521
								
							}
							/*********创建临时表*********/
							sql.delete(0,sql.length());
							
							createTempTable(code,kind, sql,orgtype);
							ContentDAO dao=new ContentDAO(this.getFrameconn());
						    sql.delete(0,sql.length());
						    /*********插入code下面的组织*********/
						    try{
						     //dao.insert(sql.toString(),new ArrayList());
						     sql.delete(0,sql.length());
						     if(this.bShowPos)
							 {
						    	 sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
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
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e01a1) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null)");
							     switch(Sql_switcher.searchDbServer())
								 {
									  case Constant.MSSQL:
									  {
										  sql.append(" and c.codeitemid like '"+code+"%'");
										  break;
									  }
									  case Constant.ORACEL:
									  {
										  sql.append(" and substr(c.codeitemid,0,"+code.length()+")='"+code+"'");
										  break;
									  }
									  case Constant.DB2:
									  {
										  sql.append(" and substr(c.codeitemid,0,"+code.length()+")='"+code+"'");
										  break;
									  }
								 }
							      //System.out.println(sql.toString());
							     dao.insert(sql.toString(),new ArrayList());
							     sql.delete(0,sql.length());
							     sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
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
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e0122) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and (a.e01a1 is null or a.e01a1='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     dao.insert(sql.toString(),new ArrayList());
							     sql.delete(0,sql.length());
							     sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
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
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.b0110) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     dao.insert(sql.toString(),new ArrayList());	
							 }else
							 {  //查询数人员数据库+A01中的数据跟插入想对应的suorganization表中，把同一单位的人员都放在单位级别下面
								 sql.delete(0,sql.length());
							     sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
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
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.e0122) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     dao.insert(sql.toString(),new ArrayList());
							     sql.delete(0,sql.length());
							     sql.append("insert into ");
							     sql.append(tmpTableNamePrefix);
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
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization b where b.codeitemid=a.b0110) from ");
							     sql.append(dbname);
							     sql.append("a01 a,organization c where a.b0110 is not null  and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
							     sql.append(code);
							     sql.append("%'");
							     dao.insert(sql.toString(),new ArrayList());
							 }
						     
						     sql.delete(0,sql.length());
						     sql.append("UPDATE "); //更新 下级指标
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization d");
						     sql.append(" WHERE d.parentid = ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
						     sql.append(" WHERE  EXISTS (SELECT * FROM ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization c");
						     sql.append(" WHERE c.parentid = ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
						     dao.update(sql.toString());
						    }catch(Exception e)
							{
						    	e.printStackTrace();
						    }
						    setEmpGradeNull(tmpTableNamePrefix+"organization");
						    //pagesize[0]宽度；pagesize[1]高度
						    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
							parameterbo.setPageheight(pagesize[1]);  //高度
							parameterbo.setPagespaceheight(40);  //页边高度
							parameterbo.setPagespacewidth(40);   //页边宽度
							parameterbo.setPagewidth(pagesize[0]); //宽度
						    
							
					            sql.delete(0,sql.length());
								sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
								if(code!=null && code.length()>0)
								{
									sql.append(" - ");
									sql.append("(select grade from ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization where codeitemid='");
									sql.append(code);
									sql.append("') as grade");
								}			
								sql.append(", A0000,");
								sql.append("(SELECT COUNT(*) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND b.codeitemid = b.childid and b.codesetid='zz') +  (SELECT COUNT(*)");
								sql.append(" FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization org WHERE (codesetid='zz' and codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS personcount,");
								sql.append("(SELECT COUNT(*) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
						        sql.append(" FROM ");
						        sql.append(tmpTableNamePrefix);
						        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
						        sql.append(SqlDifference.getJoinSymbol());
						        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
						        sql.append(tmpTableNamePrefix);
						        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
						        sql.append(tmpTableNamePrefix);
						        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
								sql.append("(SELECT COUNT(*) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization e");
								sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
						        sql.append(" (SELECT COUNT(*) FROM ");
						        sql.append(tmpTableNamePrefix);
						        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM ");
						        sql.append(tmpTableNamePrefix);
						        sql.append("organization m WHERE   m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) ");
						        sql.append(SqlDifference.getJoinSymbol());
						        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
						        sql.append(tmpTableNamePrefix);
						        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
								sql.append("(SELECT COUNT(*) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization g");
								sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
						        sql.append(" (SELECT COUNT(*) FROM ");
						        sql.append(tmpTableNamePrefix);
						        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
						        sql.append(tmpTableNamePrefix);
						        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) ");
						        sql.append(SqlDifference.getJoinSymbol());
						        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
						        sql.append(tmpTableNamePrefix);
						        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
								sql.append(" (SELECT COUNT(*) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
								sql.append(" FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization a order by a.codeitemid,a.codesetid");
								List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
								sql.delete(0,sql.length());
								sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts AS childs,");
								sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
								sql.append("(SELECT COUNT(*) counts from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization) aa,(SELECT COUNT(*) counts FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
								sql.append("(SELECT COUNT(*) as counts FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
								sql.append(" (NOT EXISTS (SELECT * FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,");
								sql.append("(SELECT COUNT(*) counts FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,");
								sql.append("(SELECT COUNT(*) counts");
								sql.append(" FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND");
								sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
								sql.append("(SELECT COUNT(*) counts FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
								sql.append(" (SELECT MIN(codeitemid) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%') AND  (NOT EXISTS (SELECT *");
								sql.append(" FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
								sql.append("(SELECT COUNT(*) counts FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
								sql.append("(SELECT COUNT(*) counts");
								sql.append(" FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%') AND (NOT EXISTS");
								sql.append(" (SELECT * FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
								sql.append("(SELECT COUNT(*) counts FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
								sql.append(" (SELECT * FROM ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
								List rootnode=null;
								if(code==null || code.length()<=0)
								      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
								orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							
							
							deleteTempTable(sql);
						}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString())){ 
							/**只显示姓名，不显示人数**/
							sql.delete(0,sql.length());
							sql.append("drop table ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization");
							try{
							  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
							}catch(Exception e)
							{
								//e.printStackTrace();
							}
							
						    sql.delete(0,sql.length());
//						    System.out.println("---> = "+sql);
						    createTempTable(code,kind, sql,orgtype);
						    ContentDAO dao=new ContentDAO(this.getFrameconn());
						    try{
							    // dao.insert(sql.toString(),new ArrayList());
							     sql.delete(0,sql.length());
							     if(this.bShowPos)
							     {
							    	 sql.append("insert into ");
								     sql.append(tmpTableNamePrefix);
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
								     sql.append(tmpTableNamePrefix);
								     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
								     sql.append(tmpTableNamePrefix);
								     sql.append("organization b where b.codeitemid=a.e01a1) from ");
								     sql.append(dbname);
								     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
								     sql.append(code);
								     sql.append("%'");
								     dao.insert(sql.toString(),new ArrayList());
								     sql.delete(0,sql.length());
								     sql.append("insert into ");
								     sql.append(tmpTableNamePrefix);
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
								     sql.append(tmpTableNamePrefix);
								     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
								     sql.append(tmpTableNamePrefix);
								     sql.append("organization b where b.codeitemid=a.e0122) from ");
								     sql.append(dbname);
								     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
								     sql.append(code);
								     sql.append("%'");
								     dao.insert(sql.toString(),new ArrayList());
								     sql.delete(0,sql.length());
								     sql.append("insert into ");
								     sql.append(tmpTableNamePrefix);
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
								     sql.append(tmpTableNamePrefix);
								     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
								     sql.append(tmpTableNamePrefix);
								     sql.append("organization b where b.codeitemid=a.b0110) from ");
								     sql.append(dbname);
								     sql.append("a01 a,organization c where a.b0110 is not null and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and (a.e01a1 is null or a.e01a1='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
								     sql.append(code);
								     sql.append("%'");
								     dao.insert(sql.toString(),new ArrayList());
							     }else
							     {
							    	 sql.delete(0,sql.length());
								     sql.append("insert into ");
								     sql.append(tmpTableNamePrefix);
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
								     sql.append(tmpTableNamePrefix);
								     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
								     sql.append(tmpTableNamePrefix);
								     sql.append("organization b where b.codeitemid=a.e0122) from ");
								     sql.append(dbname);
								     sql.append("a01 a,organization c where a.e0122 is not null and a.e0122=c.codeitemid and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
								     sql.append(code);
								     sql.append("%'");
								     //System.out.println(sql.toString());
								     dao.insert(sql.toString(),new ArrayList());					     
								     sql.delete(0,sql.length());
								     sql.append("insert into ");
								     sql.append(tmpTableNamePrefix);
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
								     sql.append(tmpTableNamePrefix);
								     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
								     sql.append(tmpTableNamePrefix);
								     sql.append("organization b where b.codeitemid=a.b0110) from ");
								     sql.append(dbname);
								     sql.append("a01 a,organization c where a.b0110 is not null and a.b0110=c.codeitemid and (a.e0122 is null or a.e0122='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
								     sql.append(code);
								     sql.append("%'");
								     dao.insert(sql.toString(),new ArrayList());
							     }				     
							     sql.delete(0,sql.length());
							     sql.append("UPDATE ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization d");
							     sql.append(" WHERE d.parentid = ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
							     sql.append(" WHERE  EXISTS (SELECT * FROM ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization c");
							     sql.append(" WHERE c.parentid = ");
							     sql.append(tmpTableNamePrefix);
							     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
							     dao.update(sql.toString());
							    }catch(Exception e)
								{
							    	e.printStackTrace();
							    }
							    setEmpGradeNull(tmpTableNamePrefix+"organization");
							    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
								parameterbo.setPageheight(pagesize[1]);
								parameterbo.setPagespaceheight(40);
								parameterbo.setPagespacewidth(40);
								parameterbo.setPagewidth(pagesize[0]);
								
								    
					                sql.delete(0,sql.length());
									sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
									if(code!=null && code.length()>0)
									{
										sql.append(" - ");
										sql.append("(select grade from ");
										sql.append(tmpTableNamePrefix);
										sql.append("organization where codeitemid='");
										sql.append(code);
										sql.append("') as grade");
									}			
									sql.append(", A0000,(SELECT COUNT(*) FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
									sql.append(SqlDifference.getJoinSymbol());
									sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
							        sql.append(" FROM ");
							        sql.append(tmpTableNamePrefix);
							        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
							        sql.append(SqlDifference.getJoinSymbol());
							        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
							        sql.append(tmpTableNamePrefix);
							        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
							        sql.append(tmpTableNamePrefix);
							        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
									sql.append("(SELECT COUNT(*) FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization d WHERE d.parentid like (SELECT MIN(e.codeitemid) FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization e");
									sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid and e.codeitemid<>a.codeitemid) ");
									sql.append(SqlDifference.getJoinSymbol());
									sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
							        sql.append(" (SELECT COUNT(*) FROM ");
							        sql.append(tmpTableNamePrefix);
							        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MIN(codeitemid) FROM ");
							        sql.append(tmpTableNamePrefix);
							        sql.append("organization m WHERE  m.parentid = a.codeitemid AND m.codeitemid <> m.parentid and m.codeitemid<>a.codeitemid) ");
							        sql.append(SqlDifference.getJoinSymbol());
							        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
							        sql.append(tmpTableNamePrefix);
							        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
									sql.append("(SELECT COUNT(*) FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization f WHERE f.parentid like (SELECT MAX(g.codeitemid) FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization g");
									sql.append(" WHERE  g.parentid = a.codeitemid AND g.codeitemid <> g.parentid and g.codeitemid<>a.codeitemid) ");
									sql.append(SqlDifference.getJoinSymbol());
									sql.append("'%' AND  a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
							        sql.append(" (SELECT COUNT(*) FROM ");
							        sql.append(tmpTableNamePrefix);
							        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
							        sql.append(tmpTableNamePrefix);
							        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid and n.codeitemid<>a.codeitemid) ");
							        sql.append(SqlDifference.getJoinSymbol());
							        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
							        sql.append(tmpTableNamePrefix);
							        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
									sql.append(" (SELECT COUNT(*) FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
									sql.append(" FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization a order by a.codeitemid,a.codesetid");
									//System.out.println(sql.toString());
									List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
									sql.delete(0,sql.length());
									sql.append("select aa.counts as nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.counts AS childs,'ren' AS subhead,");
									sql.append("ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts as  lastchildscount,ax.counts AS errorchilds from ");
									sql.append("(SELECT COUNT(*) AS counts from ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization) aa,");
									sql.append("(SELECT COUNT(*) counts FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
									sql.append("(SELECT COUNT(*) as counts FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
									sql.append(" (NOT EXISTS (SELECT * FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,(SELECT COUNT(*) counts FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization c  WHERE c.codeitemid = c.parentid) dd, (SELECT COUNT(*) counts");
									sql.append(" FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid) ");
									sql.append(SqlDifference.getJoinSymbol());
									sql.append("'%' AND");
									sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
									sql.append("(SELECT COUNT(*) as counts FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
									sql.append(" (SELECT MIN(codeitemid) FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid) ");
									sql.append(SqlDifference.getJoinSymbol());
									sql.append("'%') AND  (NOT EXISTS (SELECT *");
									sql.append(" FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,(SELECT COUNT(*) as counts FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization f WHERE f.parentid LIKE (SELECT MAX(g.codeitemid) FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid) ");
									sql.append(SqlDifference.getJoinSymbol());
									sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
									sql.append("(SELECT COUNT(*) counts");
									sql.append(" FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) ");
									sql.append(SqlDifference.getJoinSymbol());
									sql.append("'%') AND (NOT EXISTS");
									sql.append(" (SELECT * FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
									sql.append("(SELECT COUNT(*) as counts FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
									sql.append(" (SELECT * FROM ");
									sql.append(tmpTableNamePrefix);
									sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
									List rootnode=null;
									if(code==null || code.length()<=0)
									      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
									orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
									
								
						}
						else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString())){ 
							/********显示人数***********/
							int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
							parameterbo.setPageheight(pagesize[1]);
							parameterbo.setPagespaceheight(40);
							parameterbo.setPagespacewidth(40);
							parameterbo.setPagewidth(pagesize[0]);	
							
								checkorg("organization");
								createTempTable(code,kind, sql,orgtype);
								sql.delete(0,sql.length());
								String orgtable=tmpTableNamePrefix+"organization";
								sql.append("SELECT codesetid, codeitemid, codeitemdesc  " + orgmapbo.ConverDBsql(dbname) + " AS text, parentid, childid, a.state, grade");
                                if(code!=null && code.length()>0)
								{
									sql.append(" - ");
									sql.append("(select grade from "+orgtable+" where codeitemid='");
									sql.append(code);
									sql.append("') as grade");
								}			
								sql.append(",(SELECT COUNT(*) FROM "+orgtable+" b WHERE b.parentid LIKE a.codeitemid ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
						        sql.append(" FROM "+orgtable+" org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
						        sql.append(SqlDifference.getJoinSymbol());
						        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+orgtable+" orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM "+orgtable+" c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
								sql.append("(SELECT COUNT(*) FROM "+orgtable+" d WHERE d.parentid like "+exportSqlFunctFront("isnull")+"(SELECT MIN(e.codeitemid) FROM "+orgtable+" e");
								sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) ");
								sql.append(exportSqlFunctEnd("isnull"));
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
						        sql.append(" (SELECT COUNT(*) FROM "+orgtable+" org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE "+exportSqlFunctFront("isnull")+"(SELECT MIN(codeitemid) FROM "+orgtable+" m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid) ");
						        sql.append(exportSqlFunctEnd("isnull"));
						        sql.append(SqlDifference.getJoinSymbol());
						        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+orgtable+" orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
								sql.append("(SELECT COUNT(*) FROM "+orgtable+" f WHERE f.parentid like "+exportSqlFunctFront("isnull")+"(SELECT MAX(g.codeitemid) FROM "+orgtable+" g");
								sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid) ");
								sql.append(exportSqlFunctEnd("isnull"));
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
						        sql.append(" (SELECT COUNT(*) FROM "+orgtable+" org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE "+exportSqlFunctFront("isnull")+"(SELECT MAX(codeitemid) FROM "+orgtable+" n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid) ");
						        sql.append(exportSqlFunctEnd("isnull"));
						        sql.append(SqlDifference.getJoinSymbol());
						        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+orgtable+" orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
								sql.append(" (SELECT COUNT(*) FROM "+orgtable+" org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM "+orgtable+" orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
								sql.append(" From ");
								sql.append(dbname);
								sql.append("A01 RIGHT OUTER JOIN");
								sql.append(" "+orgtable+" a ON ");
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
								
				
								sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
								sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
								sql.append("(SELECT COUNT(*) AS nodecount from organization where codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (view_chart<>1 or view_chart is null)) aa,");
								sql.append("(SELECT COUNT(*) as counts FROM organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between b.start_date and b.end_date and (b.view_chart<>1 or b.view_chart is null)) bb,");
								sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
								sql.append(" (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) cc,");
								sql.append("(SELECT COUNT(*) as counts FROM organization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null)) dd,");
								sql.append("(SELECT COUNT(*) as counts ");
								sql.append(" FROM organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM organization e WHERE e.parentid = e.codeitemid and "+Sql_switcher.dateValue(backdate)+" between e.start_date and e.end_date and (e.view_chart<>1 or e.view_chart is null)) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" '%' AND");
								sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between d.start_date and d.end_date and (d.view_chart<>1 or d.view_chart is null)) ae,");
								sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
								sql.append(" (SELECT MIN(codeitemid) FROM organization m WHERE m.parentid = m.codeitemid and "+Sql_switcher.dateValue(backdate)+" between m.start_date and m.end_date and (m.view_chart<>1 or m.view_chart is null)) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%') AND  (NOT EXISTS (SELECT *");
								sql.append(" FROM organization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) ad,");
								sql.append("(SELECT COUNT(*)  as counts FROM organization f WHERE f.parentid LIKE ");
								sql.append("(SELECT MAX(g.codeitemid) FROM organization g WHERE g.codeitemid = g.parentid and "+Sql_switcher.dateValue(backdate)+" between g.start_date and g.end_date and (g.view_chart<>1 or g.view_chart is null)) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between f.start_date and f.end_date and (f.view_chart<>1 or f.view_chart is null)) be,");
								sql.append("(SELECT COUNT(*) as counts");
								sql.append(" FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM organization n WHERE n.codeitemid = n.parentid and "+Sql_switcher.dateValue(backdate)+" between n.start_date and n.end_date and (n.view_chart<>1 or n.view_chart is null)) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%') AND (NOT EXISTS");
								sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) bd,");
								sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
								sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) ce");
								
								
								List rootnode=null;
								if(code==null || code.length()<=0)
								      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
								orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							
						}else{
							/** 姓名，人数都不显示**/
							int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
							parameterbo.setPageheight(pagesize[1]);//级别
							parameterbo.setPagespaceheight(40);
							parameterbo.setPagespacewidth(40);
							parameterbo.setPagewidth(pagesize[0]);//个数
							
								checkorg("organization");
								createTempTable(code,kind, sql,orgtype);
								String orgtable=tmpTableNamePrefix+"organization";
								sql.delete(0, sql.length());
								sql.append(" SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
								if(code!=null && code.length()>0)
								{
									sql.append(" - ");
									sql.append("(select grade from "+orgtable+" where codeitemid='");
									sql.append(code);
									sql.append("') as grade");
								}
								sql.append(", A0000,(SELECT COUNT(*) FROM "+orgtable+" b WHERE b.parentid LIKE a.codeitemid ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
						        sql.append(" FROM "+orgtable+" org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
						        sql.append(SqlDifference.getJoinSymbol());
						        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+orgtable+" orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM "+orgtable+" c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
								sql.append("(SELECT COUNT(*) FROM "+orgtable+" d WHERE d.parentid like "+exportSqlFunctFront("isnull")+"(select min(codeitemid) from " + orgtable+ " where a0000=(SELECT MIN(e.a0000) FROM "+orgtable+" e");
								sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) )");
								sql.append(exportSqlFunctEnd("isnull"));
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
						        sql.append(" (SELECT COUNT(*) FROM "+orgtable+" org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE "+exportSqlFunctFront("isnull")+"(select min(codeitemid) from " + orgtable+ " where a0000=(SELECT MIN(a0000) FROM "+orgtable+" m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid)) ");
						        sql.append(exportSqlFunctEnd("isnull"));
						        sql.append(SqlDifference.getJoinSymbol());
						        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+orgtable+" orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
								sql.append("(SELECT COUNT(*) FROM "+orgtable+" f WHERE f.parentid like "+exportSqlFunctFront("isnull")+"(select max(codeitemid) from "+orgtable+" where a0000=(SELECT MAX(g.a0000) FROM "+orgtable+" g");
								sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid)) ");
								sql.append(exportSqlFunctEnd("isnull"));
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
						        sql.append(" (SELECT COUNT(*) FROM "+orgtable+" org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE "+exportSqlFunctFront("isnull")+"(select max(codeitemid) from "+orgtable+" where a0000=(SELECT MAX(a0000) FROM "+orgtable+" n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)) ");
						        sql.append(exportSqlFunctEnd("isnull"));
						        sql.append(SqlDifference.getJoinSymbol());
						        sql.append("'%') AND (NOT EXISTS (SELECT * FROM "+orgtable+" orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
								sql.append(" (SELECT COUNT(*) FROM "+orgtable+" org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM "+orgtable+" orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
								sql.append(" FROM "+orgtable+" a where a.codeitemid like '");
								sql.append(code);
								sql.append("%' order by a.a0000");
			                    //System.out.println(sql.toString());
								List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
								sql.delete(0,sql.length());
								
				
								sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
								sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
								sql.append("(SELECT COUNT(*) AS nodecount from organization where codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (view_chart<>1 or view_chart is null)) aa,");
								sql.append("(SELECT COUNT(*) as counts FROM organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between b.start_date and b.end_date and (b.view_chart<>1 or b.view_chart is null)) bb,");
								sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
								sql.append(" (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) cc,");
								sql.append("(SELECT COUNT(*) as counts FROM organization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null)) dd,");
								sql.append("(SELECT COUNT(*) as counts ");
								sql.append(" FROM organization d WHERE d.parentid LIKE (SELECT MIN(e.codeitemid) FROM organization e WHERE e.parentid = e.codeitemid and "+Sql_switcher.dateValue(backdate)+" between e.start_date and e.end_date and (e.view_chart<>1 or e.view_chart is null)) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append(" '%' AND");
								sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between d.start_date and d.end_date and (d.view_chart<>1 or d.view_chart is null)) ae,");
								sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE ");
								sql.append(" (SELECT MIN(codeitemid) FROM organization m WHERE m.parentid = m.codeitemid and "+Sql_switcher.dateValue(backdate)+" between m.start_date and m.end_date and (m.view_chart<>1 or m.view_chart is null)) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%') AND  (NOT EXISTS (SELECT *");
								sql.append(" FROM organization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) ad,");
								sql.append("(SELECT COUNT(*)  as counts FROM organization f WHERE f.parentid LIKE ");
								sql.append("(SELECT MAX(g.codeitemid) FROM organization g WHERE g.codeitemid = g.parentid and "+Sql_switcher.dateValue(backdate)+" between g.start_date and g.end_date and (g.view_chart<>1 or g.view_chart is null)) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between f.start_date and f.end_date and (f.view_chart<>1 or f.view_chart is null)) be,");
								sql.append("(SELECT COUNT(*) as counts");
								sql.append(" FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT MAX(codeitemid) FROM organization n WHERE n.codeitemid = n.parentid and "+Sql_switcher.dateValue(backdate)+" between n.start_date and n.end_date and (n.view_chart<>1 or n.view_chart is null)) ");
								sql.append(SqlDifference.getJoinSymbol());
								sql.append("'%') AND (NOT EXISTS");
								sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) bd,");
								sql.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
								sql.append(" (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
								sql.append(code);
								sql.append("%' and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date and (org.view_chart<>1 or org.view_chart is null)) ce");						
								List rootnode=null;
								if(code==null || code.length()<=0)
								      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
								orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							
						}
					}
				}
			}
			else
			{
				//没有记录的
				if(orgtype!=null&& "vorg".equalsIgnoreCase(orgtype))
				{
					if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
					{	
						
						
					    createTempTable(code,kind, sql,orgtype);
					    ContentDAO dao=new ContentDAO(this.getFrameconn());
					    
					    try{			   
					     sql.delete(0,sql.length());
					     
					     sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',e.b0110 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,e.b0110,e.b0110 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=e.b0110)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=e.b0110) from ");
					     sql.append(dbname);
					     sql.append("a01 a,vorganization c,t_vorg_staff e where a.b0110 is not null and e.b0110=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%' and e.state=1 and a.a0100=e.a0100 and upper(e.dbase)='"+dbname.toUpperCase()+"'");
					     //System.out.println(sql.toString());
					     dao.insert(sql.toString(),new ArrayList());
					     sql.delete(0,sql.length());
					     sql.append("UPDATE ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization d");
					     sql.append(" WHERE d.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
					     sql.append(" WHERE  EXISTS (SELECT * FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization c");
					     sql.append(" WHERE c.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
					     //System.out.println(sql.toString());
					     dao.update(sql.toString());
					    }catch(Exception e)
						{
					    	e.printStackTrace();
					    }
					    
					    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);
						
						   // System.out.println(pagesize[1]);
						   // System.out.println(pagesize[0]);
				            sql.delete(0,sql.length());
							sql.append("SELECT codesetid, codeitemid, codeitemdesc  AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}			
							sql.append(", A0000,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid and b.codesetid='zz') +  (SELECT COUNT(*)");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codesetid='zz' and codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS personcount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					       
							//sql.append("'%' AND b.codeitemid = b.childid)-1 AS leafagechilds,(SELECT COUNT(*) FROM ");
					       
							sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (SELECT min(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE  e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) AND ee.codeitemid <> a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE  m.parentid = a.codeitemid AND m.codeitemid <> m.parentid) AND ee.codeitemid <> a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (SELECT min(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 in(SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid) AND gg.codeitemid <> a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)  AND nn.codeitemid <> a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
//							sql.append("organization a order by a.A0000, a.codeitemid,a.codesetid");
							sql.append("organization a order by a.codeitemid,a.A0000,a.codesetid");
							
							//System.out.println("c" + sql.toString());
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							sql.append("SELECT aa.counts as nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.counts AS childs,'ren' AS subhead,");
							sql.append("ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
							sql.append("(select COUNT(*) counts from ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization) aa,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid)  AND ee.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in");
							sql.append(" (SELECT MIN(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid)  AND ee.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT codeitemid");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 in (SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid)  AND gg.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) AND nn.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");				
							//System.out.println(sql.toString());
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							
							
							deleteTempTable(sql);
						
					}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
					{				
						createTempTable(code,kind, sql,orgtype);
					   
					    ContentDAO dao=new ContentDAO(this.getFrameconn());	
					    
					try{
					  		   
					     sql.delete(0,sql.length());					     	
					     sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization(codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000)");
					     sql.append("select 'zz',e.b0110 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,a.a0101,e.b0110,e.b0110 ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" ' ' ");
					     sql.append(SqlDifference.getJoinSymbol());
					     sql.append(" a.a0100,'',(select grade from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=e.b0110)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=e.b0110) from ");
					     sql.append(dbname);
					     sql.append("a01 a,vorganization c,t_vorg_staff e where a.b0110 is not null and e.b0110=c.codeitemid  and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%' and e.state=1 and a.a0100=e.a0100 and e.dbase='"+dbname+"'");
					     
					     dao.insert(sql.toString(),new ArrayList());
					     sql.delete(0,sql.length());
					     sql.append("UPDATE ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization d");
					     sql.append(" WHERE d.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
					     sql.append(" WHERE  EXISTS (SELECT * FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization c");
					     sql.append(" WHERE c.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
					     dao.update(sql.toString());
					     //this.getFrameconn().commit();
					    }catch(Exception e)
						{
					    	e.printStackTrace();
					    }
					    
					    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);
						 
			                sql.delete(0,sql.length());
							sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}			
							sql.append(", A0000,(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (SELECT min(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) AND ee.codeitemid <> a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE  m.parentid = a.codeitemid AND m.codeitemid <> m.parentid) AND ee.codeitemid <> a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (SELECT min(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 in(SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE  g.parentid = a.codeitemid AND g.codeitemid <> g.parentid) AND gg.codeitemid <> a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND  a.codesetid<>'zz' and f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)  AND nn.codeitemid <> a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
//							sql.append("organization a order by a.A0000,a.codeitemid,a.codesetid");
							sql.append("organization a order by a.codeitemid,a.A0000,a.codesetid");
							//System.out.println("wwwnames" + sql.toString());
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							sql.append("select aa.nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.childs as childs,");
							sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
							sql.append("(SELECT COUNT(*) AS nodecount from ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization) aa,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,");
							sql.append("(SELECT COUNT(*) as childs FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid)  AND ee.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
							sql.append("(SELECT COUNT(*) as counts FROM ");				
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in");
							sql.append(" (SELECT MIN(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid)  AND ee.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT codeitemid");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 in (SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid)  AND gg.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) AND nn.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());					
								orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							//System.out.println("wwwname" + sql.toString());
						
							deleteTempTable(sql);				
					
					}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
					{
						int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);				
						
							
							
							createTempTable(code,kind, sql,orgtype);
							
							
							checkorg(tmpTableNamePrefix + "organization");
							sql.delete(0,sql.length());
							sql.append("SELECT a.codesetid, a.codeitemid, a.codeitemdesc " + orgmapbo.ConverDBsql(dbname) + " AS text, a.parentid, a.childid, a.state, a.grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}		
							sql.append(", a.A0000,(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (SELECT DISTINCT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 =(SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" From ");
							sql.append(dbname);
							sql.append("A01 RIGHT OUTER JOIN ");
							sql.append("  t_vorg_staff ON ");
			        		sql.append(""+dbname+"A01.a0100=t_vorg_staff.a0100 ");
			        		sql.append(" and  t_vorg_staff.state=1  and Upper(t_vorg_staff.dbase)='"+dbname.toUpperCase()+"'");
							sql.append(" RIGHT OUTER JOIN "+tmpTableNamePrefix);
							sql.append("organization a ON ");
							/*sql.append(Sql_switcher.substr(dbname + "A01.B0110","1",Sql_switcher.length("a.codeitemid"))); 	              
							sql.append("= a.codeitemid OR ");
							sql.append(Sql_switcher.substr(dbname + "A01.e0122","1",Sql_switcher.length("a.codeitemid"))); 	    
							sql.append("= a.codeitemid OR ");
							sql.append(Sql_switcher.substr(dbname + "A01.e01a1","1",Sql_switcher.length("a.codeitemid"))); 	    
							sql.append("= a.codeitemid where a.codeitemid like '");
							sql.append(code);
							sql.append("%'");*/
							sql.append(" t_vorg_staff.b0110=a.codeitemid ");
							sql.append(" where a.codeitemid='");
			        		sql.append(code);
			        		
			        		sql.append("'");
							sql.append(" GROUP BY a.codesetid,a.codeitemid,a.codeitemdesc,a.parentid,a.childid");
							sql.append(",a.state, a.grade,a.a0000");	
							sql.append(" order by a.A0000,a.codeitemid,a.codesetid");
							//System.out.println(sql.toString());
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
							sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
							sql.append("(SELECT COUNT(*) AS nodecount from ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization where codeitemid like '");
							sql.append(code);
							sql.append("%') aa,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
							sql.append(code);
							sql.append("%') bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') cc,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
							sql.append(code);
							sql.append("%') dd,");
							sql.append("(SELECT COUNT(*) as counts ");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT DISTINCT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.parentid = e.codeitemid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
							sql.append(code);
							sql.append("%') ae,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =");
							sql.append(" (SELECT MIN(a0000) FROM ");
							sql.append("vorganization m WHERE m.parentid = m.codeitemid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') ad,");
							sql.append("(SELECT COUNT(*)  as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT DISTINCT codeitemid");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 = (SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codeitemid = g.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
							sql.append(code);
							sql.append("%') be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codeitemid = n.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') bd,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') ce");
							//System.out.println("----"+sql.toString());
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							deleteTempTable(sql);	
						
					}else
					{
						
						createTempTable(code,kind, sql,orgtype);		
						checkorg(tmpTableNamePrefix + "organization");
						sql.delete(0,sql.length());
						int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);
						
						
							sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}		
							sql.append(", A0000,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  ");//得到自己孩子是根节点的孩子，但为什么-1不知道
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (SELECT min(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (SELECT max(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 =(SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT max(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization a where codeitemid like '");
							sql.append(code);
							sql.append("%' order by a.A0000,a.codeitemid,a.codesetid");
							//System.out.println(sql.toString());
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
							sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
							sql.append("(SELECT COUNT(*) AS nodecount from ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization where codeitemid like '");
							sql.append(code);
							sql.append("%') aa,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
							sql.append(code);
							sql.append("%') bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') cc,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
							sql.append(code);
							sql.append("%') dd,");
							sql.append("(SELECT COUNT(*) as counts ");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.parentid = e.codeitemid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
							sql.append(code);
							sql.append("%') ae,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =");
							sql.append(" (SELECT MIN(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization m WHERE m.parentid = m.codeitemid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') ad,");
							sql.append("(SELECT COUNT(*)  as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT codeitemid");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 = (SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codeitemid = g.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
							sql.append(code);
							sql.append("%') be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codeitemid = n.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') bd,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') ce");
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());					
								orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							deleteTempTable(sql);	
						
					}
				}else//不是虚拟机构的
				{
					if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()) && "true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
					{	
						
						
					    
						createOrgMap.createOrgMapTempTable(false, orgtype, paramehashmap, parameterbo, code, kind);
					    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);
						
						   // System.out.println(pagesize[1]);
						   // System.out.println(pagesize[0]);
				            sql.delete(0,sql.length());
							sql.append("SELECT codesetid, codeitemid, codeitemdesc  AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}			
							sql.append(", A0000,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid and b.codesetid='zz') +  (SELECT COUNT(*)");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codesetid='zz' and codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS personcount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					       
							//sql.append("'%' AND b.codeitemid = b.childid)-1 AS leafagechilds,(SELECT COUNT(*) FROM ");
					       
							sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (SELECT min(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE  e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) AND ee.codeitemid <> a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND  a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE  m.parentid = a.codeitemid AND m.codeitemid <> m.parentid) AND ee.codeitemid <> a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (SELECT min(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 in(SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid) AND gg.codeitemid <> a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND a.codesetid<>'zz' and  f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)  AND nn.codeitemid <> a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization a group by codeitemid,codesetid,codeitemdesc,parentid,childid,state,grade,a0000,a0100  order by a.A0000,a.codesetid");
							
							
							//System.out.println("c" + sql.toString());
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							sql.append("SELECT aa.counts as nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.counts AS childs,'ren' AS subhead,");
							sql.append("ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
							sql.append("(select COUNT(*) counts from ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization) aa,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid)  AND ee.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in");
							sql.append(" (SELECT MIN(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid)  AND ee.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
							sql.append("(SELECT COUNT(*) counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT codeitemid");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 in (SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid)  AND gg.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) AND nn.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");				
							///System.out.println(sql.toString());
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							
							
							deleteTempTable(sql);
						
					}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
					{				
						createTempTable(code,kind, sql,orgtype);
					   
					    ContentDAO dao=new ContentDAO(this.getFrameconn());	
					    
					try{
					  	if(this.bShowPos)
					  	{
					  		 sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
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
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e01a1)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e01a1) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e01a1 is not null and c.codeitemid=a.e01a1 and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
						     dao.insert(sql.toString(),new ArrayList());				     
						     sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
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
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e0122 is not null and c.codeitemid=a.e0122 and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and (a.e01a1 is null or a.e01a1='') and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
					  	}else
					  	{
					  		 sql.delete(0,sql.length());
						     sql.append("insert into ");
						     sql.append(tmpTableNamePrefix);
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
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122)+1,(select a0000 from ");
						     sql.append(tmpTableNamePrefix);
						     sql.append("organization b where b.codeitemid=a.e0122) from ");
						     sql.append(dbname);
						     sql.append("a01 a,organization c where a.e0122 is not null and c.codeitemid=a.e0122 and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and c.codeitemid like '");
						     sql.append(code);
						     sql.append("%'");
					  	}   
					     
					     ///System.out.println(sql.toString());
					     dao.insert(sql.toString(),new ArrayList());
					     sql.delete(0,sql.length());
					     sql.append("insert into ");
					     sql.append(tmpTableNamePrefix);
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
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.b0110)+1,(select a0000 from ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization b where b.codeitemid=a.b0110) from ");
					     sql.append(dbname);
					     sql.append("a01 a,organization c where a.b0110 is not null and c.codeitemid=a.b0110 and (a.e0122 is null or a.e0122='') and "+Sql_switcher.dateValue(backdate)+" between c.start_date and c.end_date and (c.view_chart<>1 or c.view_chart is null) and (a.e01a1 is null or a.e01a1='')and c.codeitemid like '");
					     sql.append(code);
					     sql.append("%'");
					     //System.out.println("a0100" + sql.toString());
					     dao.insert(sql.toString(),new ArrayList());
					     sql.delete(0,sql.length());
					     sql.append("UPDATE ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization d");
					     sql.append(" WHERE d.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid  AND d.parentid <> d.codeitemid)");
					     sql.append(" WHERE  EXISTS (SELECT * FROM ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization c");
					     sql.append(" WHERE c.parentid = ");
					     sql.append(tmpTableNamePrefix);
					     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
					     dao.update(sql.toString());
					     //this.getFrameconn().commit();
					    }catch(Exception e)
						{
					    	e.printStackTrace();
					    }
					    
					    int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);
						
						    
			                sql.delete(0,sql.length());
							sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}			
							sql.append(", A0000,(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (SELECT min(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE   e.parentid = a.codeitemid AND e.codeitemid <> e.parentid) AND ee.codeitemid <> a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND a.codesetid<>'zz' and d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE  m.parentid = a.codeitemid AND m.codeitemid <> m.parentid) AND ee.codeitemid <> a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (SELECT min(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 in(SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE  g.parentid = a.codeitemid AND g.codeitemid <> g.parentid) AND gg.codeitemid <> a.codeitemid) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND  a.codesetid<>'zz' and f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE  n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)  AND nn.codeitemid <> a.codeitemid) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
//							sql.append("organization a order by a.A0000,a.codeitemid,a.codesetid");
							sql.append("organization a order by a.A0000, a.codeitemid,a.codesetid");
							//System.out.println("wwwnames" + sql.toString());
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							sql.append("select aa.nodecount,bb.counts -1 + cc.counts AS leafagechilds,dd.childs as childs,");
							sql.append("'ren' AS subhead,ae.counts + ad.counts AS firstchildscount,be.counts + bd.counts AS lastchildscount,ax.counts AS errorchilds from ");
							sql.append("(SELECT COUNT(*) AS nodecount from ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization) aa,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid) bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) cc,");
							sql.append("(SELECT COUNT(*) as childs FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid) dd,(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.codesetid<>'zz' and e.parentid = e.codeitemid)  AND ee.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid) ae,");
							sql.append("(SELECT COUNT(*) as counts FROM ");				
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 in");
							sql.append(" (SELECT MIN(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization m WHERE m.codesetid<>'zz' and m.parentid = m.codeitemid)  AND ee.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId))) ad,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT codeitemid");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 in (SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codesetid<>'zz' and g.codeitemid = g.parentid)  AND gg.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization nn WHERE nn.a0000 in(SELECT MAX(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codesetid<>'zz' and n.codeitemid = n.parentid) AND nn.codesetid <> 'zz') ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) bd,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) ax");
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							//System.out.println("wwwname" + sql.toString());
						
							deleteTempTable(sql);				
					
					}else if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
					{
						int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);				
						
							
							
							createTempTable(code,kind, sql,orgtype);
							
							
							checkorg(tmpTableNamePrefix + "organization");
							sql.delete(0,sql.length());
							sql.append("SELECT a.codesetid, a.codeitemid, a.codeitemdesc " + orgmapbo.ConverDBsql(dbname) + " AS text, a.parentid, a.childid, a.state, a.grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}		
							sql.append(", a.A0000,(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  (SELECT COUNT(*)");
					        sql.append(" FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (SELECT DISTINCT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 =(SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append("'%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" From ");
							sql.append(dbname);
							sql.append("A01 RIGHT OUTER JOIN ");
							sql.append(tmpTableNamePrefix);
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
							sql.append(tmpTableNamePrefix);
							sql.append("organization where codeitemid like '");
							sql.append(code);
							sql.append("%') aa,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
							sql.append(code);
							sql.append("%') bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') cc,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
							sql.append(code);
							sql.append("%') dd,");
							sql.append("(SELECT COUNT(*) as counts ");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT DISTINCT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.parentid = e.codeitemid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
							sql.append(code);
							sql.append("%') ae,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =");
							sql.append(" (SELECT MIN(a0000) FROM ");
							sql.append("organization m WHERE m.parentid = m.codeitemid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') ad,");
							sql.append("(SELECT COUNT(*)  as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT DISTINCT codeitemid");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 = (SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codeitemid = g.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
							sql.append(code);
							sql.append("%') be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT DISTINCT  codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codeitemid = n.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') bd,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') ce");
							//System.out.println("ce---"+sql.toString());
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							deleteTempTable(sql);	
						
					}else
					{
						
						createTempTable(code,kind, sql,orgtype);		
						checkorg(tmpTableNamePrefix + "organization");
						sql.delete(0,sql.length());
						int[] pagesize=orgmapbo.getOrgMapPageSize(parameterbo,code,this.getFrameconn(),tmpTableNamePrefix,backdate);
						parameterbo.setPageheight(pagesize[1]);
						parameterbo.setPagespaceheight(40);
						parameterbo.setPagespacewidth(40);
						parameterbo.setPagewidth(pagesize[0]);
						
						
							sql.append("SELECT codesetid, codeitemid, codeitemdesc AS text, parentid, childid, state, grade");
							if(code!=null && code.length()>0)
							{
								sql.append(" - ");
								sql.append("(select grade from ");
								sql.append(tmpTableNamePrefix);
								sql.append("organization where codeitemid='");
								sql.append(code);
								sql.append("') as grade");
							}		
							sql.append(", A0000,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE a.codeitemid ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND b.codeitemid = b.childid)-1 +  ");//得到自己孩子是根节点的孩子，但为什么-1不知道
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE a.codeitemid ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeitemid = org.childid))) AS leafagechilds,(SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization c WHERE a.codeitemid = c.parentid AND c.codeitemid <> c.parentid) AS childs,'ren' AS subhead,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid like (SELECT min(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e");
							sql.append(" WHERE e.parentid = a.codeitemid AND e.codeitemid <> e.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND d.parentid <> d.codeitemid AND d.childid = d.codeitemid)  +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT min(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization m WHERE m.parentid = a.codeitemid AND m.codeitemid <> m.parentid)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS firstchildscount,");
							sql.append("(SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid like (SELECT max(codeitemid) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 =(SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g");
							sql.append(" WHERE g.parentid = a.codeitemid AND g.codeitemid <> g.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid) +");
					        sql.append(" (SELECT COUNT(*) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT max(codeitemid) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization n WHERE n.parentid = a.codeitemid AND n.codeitemid <> n.parentid)) ");
					        sql.append(SqlDifference.getJoinSymbol());
					        sql.append(" '%') AND (NOT EXISTS (SELECT * FROM ");
					        sql.append(tmpTableNamePrefix);
					        sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS lastchildscount,");
							sql.append(" (SELECT COUNT(*) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = a.codeitemid) AND (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId))) AS errorchilds");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization a where codeitemid like '");
							sql.append(code);
							sql.append("%' order by a.A0000,a.codeitemid,a.codesetid");  //a.codeitemid,a.A0000,a.codesetid
							//System.out.println(sql.toString());
							List rs=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
							sql.delete(0,sql.length());
							sql.append("select aa.nodecount,bb.counts -1 + cc.counts as leafagechilds,dd.counts,'ren' AS subhead,");
							sql.append("ae.counts + ad.counts as firstchildscount,be.counts + bd.counts as lastchildscount,ce.counts as errorchilds from ");
							sql.append("(SELECT COUNT(*) AS nodecount from ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization where codeitemid like '");
							sql.append(code);
							sql.append("%') aa,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization b WHERE b.parentid LIKE '%' AND b.codeitemid = b.childid and b.codeitemid like '");
							sql.append(code);
							sql.append("%') bb,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '%') AND");
							sql.append(" (NOT EXISTS (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') cc,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization c  WHERE c.codeitemid = c.parentid and c.codeitemid like '");
							sql.append(code);
							sql.append("%') dd,");
							sql.append("(SELECT COUNT(*) as counts ");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization d WHERE d.parentid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =(SELECT MIN(e.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization e WHERE e.parentid = e.codeitemid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append(" '%' AND");
							sql.append(" d.parentid <> d.codeitemid AND d.childid = d.codeitemid and d.codeitemid like '");
							sql.append(code);
							sql.append("%') ae,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization ee WHERE ee.a0000 =");
							sql.append(" (SELECT MIN(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization m WHERE m.parentid = m.codeitemid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND  (NOT EXISTS (SELECT *");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge  WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') ad,");
							sql.append("(SELECT COUNT(*)  as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization f WHERE f.parentid LIKE (SELECT codeitemid");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization gg WHERE gg.a0000 = (SELECT MAX(g.a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization g WHERE g.codeitemid = g.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%' AND f.parentid <> f.codeitemid AND f.childid = f.codeitemid and f.codeitemid like '");
							sql.append(code);
							sql.append("%') be,");
							sql.append("(SELECT COUNT(*) as counts");
							sql.append(" FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE (SELECT codeitemid FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization nn WHERE nn.a0000 =(SELECT MAX(a0000) FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization n WHERE n.codeitemid = n.parentid)) ");
							sql.append(SqlDifference.getJoinSymbol());
							sql.append("'%') AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') bd,");
							sql.append("(SELECT COUNT(*) as counts FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid = org.parentid) AND (NOT EXISTS");
							sql.append(" (SELECT * FROM ");
							sql.append(tmpTableNamePrefix);
							sql.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
							sql.append(code);
							sql.append("%') ce");
							//System.out.println(sql);
							List rootnode=null;
							if(code==null || code.length()<=0)
							      rootnode=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());						
							orgmapbo.createOrgMap(rs,rootnode,rootdesc,parameterbo,this.getFrameconn(),code,false,workbook);
							//deleteTempTable(sql);	
						
					}
				}
				
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+url);
			workbook.write(fileOut);
			fileOut.close();
//			url=url.replace(".xls","#");
			url = PubFunc.encrypt(url);
			this.getFormHM().put("url",url);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
	}
	private void deleteTempTable(StringBuffer sql) {
		sql.delete(0,sql.length());
		sql.append("drop table ");  //drop table (清除表格)
		sql.append(tmpTableNamePrefix);
		sql.append("organization");
		try{
		  //ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void createTempTable(String code,String kind, StringBuffer sql,String orgtype) {
		sql.delete(0,sql.length());
		sql.append("drop table ");
		sql.append(tmpTableNamePrefix);
		sql.append("organization");
		try{
		  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
		}catch(Exception e)
		{
			//e.printStackTrace();
		}
		
			  sql.delete(0,sql.length());
			  sql.append("CREATE TABLE ");
			  sql.append(tmpTableNamePrefix);
			  sql.append("organization (codesetid varchar (2),");
			  sql.append("codeitemid varchar (100) not null,");
			  sql.append("codeitemdesc varchar (200),");
			  sql.append("parentid varchar (100),");
			  sql.append("childid varchar (100),");
			  sql.append("state varchar (10),");
			  sql.append("a0000 int null,");
			  sql.append("seqId int,");
			  sql.append("grade int,layer int)");					
		
		try{
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());
			  sql.delete(0,sql.length());
			  sql.append("ALTER TABLE ");
			  sql.append(tmpTableNamePrefix);
			  sql.append("organization ADD PRIMARY KEY (codeitemid)");
			  ExecuteSQL.createTable(sql.toString(),this.getFrameconn());					  
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		//System.out.println(sql.toString());
		Statement sta = null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		try{			
			
			if (!bShowDept)
				SelectCondition = " and codesetid not in ('UM','@K')";
			else if (!bShowPos)
				SelectCondition = " and codesetid<>'@K'";
			
			// 如果选择 组织机构（code=""） 则手动查出parentid=codeitemid的节点 放进arraylist中
			ArrayList arr = new ArrayList();
			if(code=="" || code.length()<1){
				sql.delete(0, sql.length());
				sql.append("select codeitemid from organization where codeitemid=parentid and ");
				sql.append(Sql_switcher.dateValue(backdate));
				sql.append(" between start_date and end_date and (view_chart<>1 or view_chart is null) order by a0000");
				this.frowset = dao.search(sql.toString());
				while(frowset.next()){
					arr.add(frowset.getString("codeitemid"));
				}
			}else
				arr.add(code);
				
			rownum = 1;
			 sta = this.frameconn.createStatement();
			 this.frameconn.setAutoCommit(false);
			insertData(arr, dao,sta);//向表里插入数据，按seqid排序   
			dbS.open(this.frameconn, sql.toString()); 
			sta.executeBatch();
			this.frameconn.commit();
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
				  UpdateTempA0000(dao);
				  break;
			  }
			
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try {
				dbS.close(this.frameconn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				this.frameconn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PubFunc.closeResource(sta);

		}
		orderTempChildid(tmpTableNamePrefix+"organization");
		filtrateDeptLevel(tmpTableNamePrefix+"organization");
		
	}
    private void UpdateTempA0000(ContentDAO dao)
    {
    	 try{
    		 StringBuffer A0000sql=new StringBuffer();
    		 A0000sql.append("update ");
    		 A0000sql.append(tmpTableNamePrefix);
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
    /**
     * 找孩子
     * @param tablename
     */
    private void orderTempChildid(String tablename)
    {
    	 if(this.bShowPos)
    		return; 
    	 StringBuffer sql =new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 sql.append("select codeitemid,childid from "+tablename+" where codeitemid<>childid");
		 List list=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());	
		 String codeitemid="";
		 String childid="";
		 ArrayList arlist=new ArrayList();
		 ArrayList chlist=new ArrayList();
		 if(!list.isEmpty())
		 {
			 for(int i=0;i<list.size();i++)
			 {
				 LazyDynaBean orgmapbean=(LazyDynaBean)list.get(i);
				 childid=orgmapbean.get("childid").toString();
				 codeitemid=orgmapbean.get("codeitemid").toString();
				 sql.delete(0,sql.length());				 
				 sql.append("select codeitemid from "+tablename+" where codeitemid='"+childid+"'");
				 List childlist=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());	
				 if(childlist.isEmpty()&&childlist.size()<=0)
				 {
					 sql.delete(0,sql.length());					 
					 sql.append("select codeitemid from "+tablename+" where parentid='"+codeitemid+"' order by a0000,codeitemid,codesetid ");
					 List codeitemist=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
					 if(!codeitemist.isEmpty())
					 {
						 LazyDynaBean codeitembean=(LazyDynaBean)codeitemist.get(0);
						 childid=codeitembean.get("codeitemid").toString();
						 chlist=new ArrayList();
						 chlist.add(childid);
						 chlist.add(codeitemid);
						 arlist.add(chlist);
					 }
				 }
			 }
		 }
		 sql.delete(0,sql.length());
		 sql.append("update  "+tablename+" set childid=? where codeitemid=?");
		 try {
			dao.batchUpdate(sql.toString(), arlist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private String exportSqlFunctFront(String functionName)
    {
    	StringBuffer buf=new StringBuffer();
    	switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			 
			  break;
		  }
		  case Constant.DB2:
		  {
			  
			  break;
		  }
		  case Constant.ORACEL:
		  {
			  if(functionName!=null&& "isnull".equals(functionName))
				  buf.append(" nvl(");
			  break;
		  }
		
		}
        return buf.toString();
    	//return "";
    }
    private String exportSqlFunctEnd(String functionName)
    {
    	StringBuffer buf=new StringBuffer();
    	switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
			 
			  break;
		  }
		  case Constant.DB2:
		  {
			  
			  break;
		  }
		  case Constant.ORACEL:
		  {
			  if(functionName!=null&& "isnull".equals(functionName))
				  buf.append(" ,'xzcvbnm')");
			  break;
		  }
		
		}
    	//return "";
    	return buf.toString();
    }
    /**
     * 删除部门多余的层级
     * @param srcTab
     */
    private void filtrateDeptLevel(String srcTab)
    {
    	/*KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
    	String destTab=srcTab+"_UM_Level";
    	String strFldlst="codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade, A0000";
    	String strWhere="codesetid='UM'";
    	String strGroupBy="order by a0000,codeitemid,codesetid";
    	kqUtilsClass.createTempTable(srcTab,destTab,strFldlst,strWhere,strGroupBy);*/
    	/*if(this.iDeptlevel<1)
    		return;
    	String sql="select codeitemid from "+srcTab+" where layer>"+this.iDeptlevel+" and codesetid='UM'";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try {
			RowSet rs=dao.search(sql);
		    while(rs.next())
		    {
		    	sql="delete from "+srcTab+" where codeitemid like '"+rs.getString("codeitemid")+"%'";
		    	dao.delete(sql, new ArrayList());
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	if(this.iDeptlevel<1&&this.iUnitlevel<1)
    		return;
    	String sql="";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	RowSet rs =null;
    	try {
    		if(this.iUnitlevel>0){
    			sql="select codeitemid from "+srcTab+" where layer>"+this.iUnitlevel+" and codesetid='UN'";
				rs=dao.search(sql);
			    while(rs.next())
			    {
			    	sql="delete from "+srcTab+" where codeitemid like '"+rs.getString("codeitemid")+"%'";
			    	dao.delete(sql, new ArrayList());
			    }
    		}
			if(this.iDeptlevel>0){
				sql="select codeitemid from "+srcTab+" where layer>"+this.iDeptlevel+" and codesetid='UM'";
				rs=dao.search(sql);
			    while(rs.next())
			    {
			    	sql="delete from "+srcTab+" where codeitemid like '"+rs.getString("codeitemid")+"%'";
			    	dao.delete(sql, new ArrayList());
			    }
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
    }
    private void setEmpGradeNull(String table)
    {
    	
    	if(this.bShowDept)
    	{
    		if(this.iDeptlevel<1)
        		return;
    		
    		String sql="select Distinct parentid,grade,a0000  from "+table+" where codesetid='zz' and grade is not null order by grade desc";
    		
    		RowSet rs = null;
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
        	try {
    			rs = dao.search(sql);		
    			ArrayList list=new ArrayList();
    		    while(rs.next())
    		    {
    		    	LazyDynaBean bean=new LazyDynaBean();
    		    	bean.set("parentid", rs.getString("parentid"));
    		    	bean.set("grade", rs.getString("grade"));
    		    	bean.set("a0000", rs.getString("a0000")!=null?rs.getString("a0000"):"");
    		    	list.add(bean);
    		    }
    		    for(int i=0;i<list.size();i++)
    		    {
    		    	LazyDynaBean bean=(LazyDynaBean)list.get(i);
    		    	sql="update "+table+" set grade='"+(String)bean.get("grade")+"',a0000='"+(String)bean.get("a0000")+"' where codesetid='zz' and grade is null and parentid like '"+(String)bean.get("parentid")+"%'";
    		        dao.update(sql);
    		    }
    		    sql="delete from "+table+" where codesetid='zz' and grade is null";
    		    dao.delete(sql, new ArrayList());    		    
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}finally{
    		    try{
        		    if(rs != null){
        		        rs.close();
        		    }
    		    }catch(Exception e){
    		        e.printStackTrace();
    		    }
    		}
    	}else
    	{
    		String sql="delete from "+table+" where codesetid='zz' and grade is null";
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
        	try {
    			dao.delete(sql, new ArrayList());		    			
    		   
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
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
    
    public void insertData(ArrayList codeitemids,ContentDAO dao,Statement sta){
    	try{
    		
	    	for(int i=0;i<codeitemids.size();i++){
				String codeitemid = codeitemids.get(i).toString();
				StringBuffer sql = new StringBuffer("INSERT INTO t#"+userView.getUserName()+"organization ");
			    sql.append(" (codesetid, codeitemid, codeitemdesc, parentid, childid, state, grade,layer,seqId) ");
			    sql.append(" select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,layer,"+rownum);
			    sql.append(" from organization where codeitemid='"+codeitemid+"' ");
			    sql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
			    sql.append(" union select 'VO',codeitemid,codeitemdesc,parentid,childid,state,grade,layer,"+rownum);
			    sql.append(" from vorganization where codeitemid='"+codeitemid+"' ");
			    sql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
			    sql.append(" order by codeitemid ");
			    sta.addBatch(sql.toString());
			    rownum++;
			    sql.delete(0, sql.length());
			    sql.append(" select codeitemid,a0000 from organization where parentid='"+codeitemid+"' and parentid<>codeitemid and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (view_chart is null or view_chart<>1) ");
			    sql.append(SelectCondition);
			    sql.append(" union select codeitemid,a0000 from vorganization where parentid='"+codeitemid+"' and parentid<>codeitemid and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (view_chart is null or view_chart<>1) ");
			    sql.append(SelectCondition);
			    sql.append(" order by a0000");
			    frowset = dao.search(sql.toString());
			    
			    ArrayList codeitems1 = new ArrayList();
			    while(frowset.next()){
			    	codeitems1.add(frowset.getString("codeitemid"));
			    }
			    
			       insertData(codeitems1,dao,sta);
			}
	    	
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    }
}

