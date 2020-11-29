 /*
 * Created on 2006-3-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.servlet.inform.org;

 import com.hjsj.hrms.businessobject.general.orgmap.CompTextOnecell;
 import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
 import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
 import com.hjsj.hrms.utils.ResourceFactory;
 import com.hjsj.hrms.utils.SqlDifference;
 import com.hjsj.hrms.valueobject.database.ExecuteSQL;
 import com.hrms.frame.utility.AdminDb;
 import com.hrms.hjsj.sys.Constant;
 import com.hrms.hjsj.utils.Sql_switcher;
 import com.hrms.struts.constant.WebConstant;
 import com.hrms.struts.valueobject.UserView;
 import org.apache.commons.beanutils.LazyDynaBean;
 import org.apache.log4j.Category;

 import javax.servlet.ServletException;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.sql.RowSet;
 import java.io.IOException;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
	public class GetSVGMapServlet extends HttpServlet {
		   /* 
	     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	     */
		private  Category cat = Category.getInstance(this.getClass());
		private String parentenodestr="g00000000000000";
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	            throws ServletException, IOException {
			String code=req.getParameter("code");
			String orgtype=req.getParameter("orgtype");
			String dbnameini="usr";
			UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);
			if(!(userview.hasTheFunction("23051")||userview.hasTheFunction("050102"))){//如果没机构图的权限 跳出
				return ;
			}
			if(code==null || code!=null && code.length()==0 && !userview.isSuper_admin())
			{
				String busi=this.getBusi_org_dept(userview);
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
			ArrayList Dblist=userview.getPrivDbList();
			if(Dblist!=null&&Dblist.size()>0)
				dbnameini=Dblist.get(0).toString();	
			String catalog_id=req.getParameter("catalog_id");
			String kind=req.getParameter("kind");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String backdate = req.getParameter("backdate");
			backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
			
		    HashMap paramehashmap=new SetOrgOptionParameter().ReadOutParameterXml("ORG_MAPOPTION",true,dbnameini,userview);    		
			String dbpre = paramehashmap.get("dbnames").toString();
			if(!userview.hasTheDbName(dbpre)){
				paramehashmap.put("dbnames", dbnameini);
			}
		    StringBuffer sql=new StringBuffer();			
            try{
		        StringBuffer svgos=new StringBuffer();
		        svgos.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		        svgos.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
		        if("true".equals(paramehashmap.get("graphaspect").toString()))
		        	svgos.append("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"100%\"  height=\"100%\"  viewBox=\"0 0 633 140\" id=\"mainview\">");
		        else
		            svgos.append("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"100%\"  height=\"100%\"  viewBox=\"0 0 100 500\" id=\"mainview\">");
		        
		        svgos.append("<script><![CDATA[");
		        svgos.append("]]></script>");
		        svgos.append("<script type=\"text/javascript\" xlink:href=\"/ajax/basic.js\"></script>");
		        svgos.append("<script type=\"text/javascript\" xlink:href=\"/ajax/common.js\"></script>");
		        svgos.append("<script type=\"text/javascript\" xlink:href=\"/ajax/command.js\"></script>");
		        svgos.append("<script type=\"text/javascript\" xlink:href=\"/js/svg.js\"></script>");
		        if(catalog_id!=null && catalog_id.length()>0)
		        {
		        	svgos.append("<script type=\"text/javascript\" xlink:href=\"/js/svg-historyextends.js\"></script>");
		        	if(code!=null && code.length()>0)
					{
						sql.append("select grade,codeitemdesc,codeitemid,codesetid from hr_org_history where codeitemid='");
						sql.append(code);
						sql.append("' and catalog_id='");
						sql.append(catalog_id);
						sql.append("'");
						List rs=ExecuteSQL.executeMyQuery("select codeitemdesc from hr_org_history where parentid='" + code + "' and parentid<>codeitemid");
						boolean ishavechilds=false;
						if(rs !=null && rs.size()>0)
							ishavechilds=true;
							
						
						printSVGCellCodeCatalog_id(code, catalog_id, sql, svgos,paramehashmap,ishavechilds);						
			        }
			        else
			        {
			        		
						/*StringBuffer hostsql=new StringBuffer();
						hostsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,catalog_id");
						hostsql.append(" FROM hr_org_history ");    	
						hostsql.append(" WHERE codesetid='UN' AND codeitemid=parentid and catalog_id='" + catalog_id + "'");
						hostsql.append(" ORDER BY a0000,codeitemid ");
						List rs=ExecuteSQL.executeMyQuery(hostsql.toString());
						boolean ishavechilds=false;
						if(rs !=null && rs.size()>0)
						{
							LazyDynaBean rec=(LazyDynaBean)rs.get(0);
							code=(String)rec.get("codeitemid");
							if(code!=null&&code.length()>0)
							{
								sql.append("select grade,codeitemdesc,codeitemid,codesetid from hr_org_history where codeitemid='");
								sql.append(code);
								sql.append("' and catalog_id='");
								sql.append(catalog_id);
								sql.append("'");
								rs=ExecuteSQL.executeMyQuery("select codeitemdesc from hr_org_history where parentid='" + code + "' and parentid<>codeitemid");
								if(rs !=null && rs.size()>0)
									ishavechilds=true;
								printSVGCellCodeCatalog_id(code, catalog_id, sql, svgos,paramehashmap,ishavechilds);	
							}else
							{
								sql.append("select name from hr_org_catalog where catalog_id='");
								sql.append(catalog_id);
								sql.append("'");
								printSVGCellCodeCatalog_id(code, catalog_id, sql, svgos,paramehashmap,true);
							}
						}else
						{
							sql.append("select name from hr_org_catalog where catalog_id='");
							sql.append(catalog_id);
							sql.append("'");
							printSVGCellCodeCatalog_id(code, catalog_id, sql, svgos,paramehashmap,true);
						}*/			        	
			            sql.append("select name from hr_org_catalog where catalog_id='");
						sql.append(catalog_id);
						sql.append("'");
			        	printHistorySVGCellRoot(catalog_id,sql, svgos,paramehashmap);
			        }
		        }
		        else
		        {
		        	svgos.append("<script type=\"text/javascript\" xlink:href=\"/js/svg-extends.js\"></script>");
		        	if(code!=null && code.length()>0)
					{
		        		boolean ishavechilds=false;
		        		String dbname=paramehashmap.get("dbnames").toString();
		        		if(orgtype!=null&& "vorg".equalsIgnoreCase(orgtype))
		        		{
		        			sql.append("select vorganization.codesetid,vorganization.codeitemid,vorganization.grade");
						    sql.append(" from vorganization  where vorganization.parentid='");
							sql.append(code);
							sql.append("' and vorganization.parentid<>vorganization.codeitemid");
							sql.append(" and "+Sql_switcher.dateValue(backdate)+" between vorganization.start_date and vorganization.end_date ");
							sql.append(" and (vorganization.view_chart<>1 or vorganization.view_chart is null)");
							if(!"true".equalsIgnoreCase(paramehashmap.get("isshowposname").toString()))
							{
								sql.append(" and vorganization.codesetid<>'@K'");
							}
							List rs=ExecuteSQL.executeMyQuery(sql.toString());
							if(rs !=null && rs.size()>0)
								ishavechilds=true;
							sql.delete(0,sql.length());	
							if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
							{
								sql.append("SELECT b.a0100, a.codeitemid, b.A0101, b.A0000 from vorganization a, ");
								sql.append(dbname);
								sql.append("A01 b,t_vorg_staff c ");
								sql.append("where a.codeitemid='"+code+"' and c.B0110= a.codeitemid ");//  
								sql.append("  and c.state=1  and Upper(c.dbase)='"+dbname.toUpperCase()+"' and c.A0100=b.a0100");	
								sql.append(" and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date ");
								sql.append(" and (a.view_chart<>1 or a.view_chart is null)");
								sql.append(" order by b.a0000");
								rs=ExecuteSQL.executeMyQuery(sql.toString());
								if(rs !=null && rs.size()>0)
									ishavechilds=true;
							}
                            sql.delete(0,sql.length());		
			        		if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
			        		{
							    sql.append("select vorganization.codesetid,vorganization.codeitemid,vorganization.grade,vorganization.codeitemdesc ");
							    sql.append(SqlDifference.getJoinSymbol());
							    sql.append(" " + ConverDBsql(dbname) + "  as codeitemdesc,");
							}
							else
								sql.append("select vorganization.codesetid,vorganization.codeitemid,vorganization.grade,vorganization.codeitemdesc  as codeitemdesc,");
			        		sql.append("count(" + dbname);
			        		sql.append("A01.a0100) as personcount,'vorg' as infokind from ");
			        		sql.append(dbname);
			        		sql.append("A01 RIGHT OUTER JOIN");			
			        		sql.append("  t_vorg_staff ON ");
			        		sql.append(""+dbname+"A01.a0100=t_vorg_staff.a0100 ");
			        		sql.append(" and  t_vorg_staff.state=1  and Upper(t_vorg_staff.dbase)='"+dbname.toUpperCase()+"'");
			        		sql.append(" RIGHT OUTER join vorganization ON ");
			        		sql.append(" t_vorg_staff.b0110=vorganization.codeitemid ");	 
			        		sql.append(" where vorganization.codeitemid='");
			        		sql.append(code);
			        		/*sql.append("' and vorganization.parentid='");
			        		sql.append(code);*/
			        		sql.append("'");
			        		sql.append(" and "+Sql_switcher.dateValue(backdate)+" between vorganization.start_date and vorganization.end_date ");
			        		sql.append(" and (vorganization.view_chart<>1 or vorganization.view_chart is null)");
			        		sql.append(" GROUP BY vorganization.codesetid,vorganization.A0000,vorganization.codeitemid,vorganization.codeitemdesc,vorganization.parentid,vorganization.childid,");
			        		sql.append(" vorganization.grade");	
			        		sql.append(" order by vorganization.A0000,vorganization.codeitemid");
			        		//printSVGCell(code, catalog_id, sql, svgos,paramehashmap,ishavechilds);
		        		}else
		        		{
		        			
		        			sql.append("select organization.codesetid,organization.codeitemid,organization.grade");
						    sql.append(" from organization  where organization.parentid='");
							sql.append(code);
							sql.append("' and organization.parentid<>organization.codeitemid");
							sql.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
							sql.append(" and (organization.view_chart<>1 or organization.view_chart is null) ");
							if(!"true".equalsIgnoreCase(paramehashmap.get("isshowposname").toString()))
							{
								sql.append(" and organization.codesetid<>'@K'"); 
							}
							
							sql.append("union all select vorganization.codesetid,vorganization.codeitemid,vorganization.grade");
						    sql.append(" from vorganization  where vorganization.parentid='");
							sql.append(code);
							sql.append("' and vorganization.parentid<>vorganization.codeitemid");
							sql.append(" and "+Sql_switcher.dateValue(backdate)+" between vorganization.start_date and vorganization.end_date ");
							sql.append(" and (vorganization.view_chart<>1 or vorganization.view_chart is null)");
							if(!"true".equalsIgnoreCase(paramehashmap.get("isshowposname").toString()))
							{
								sql.append(" and vorganization.codesetid<>'@K'");
							}
							List rs=ExecuteSQL.executeMyQuery(sql.toString());
							if(rs !=null && rs.size()>0)
								ishavechilds=true;
							sql.delete(0,sql.length());		
							if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
							{
								if(!"true".equalsIgnoreCase(paramehashmap.get("isshowposname").toString()))
								{
									sql.append("SELECT  b.A0101 from organization a, ");
									sql.append(dbname);
									sql.append("A01 b ");
									sql.append(" where (a.codeitemid='");
									sql.append(code);
									sql.append("')  and ((b.E0122= a.codeitemid)");
									sql.append(" or ");   // 有部门，无职位
									         // 有单位，无部门职位
									if(kind!=null&& "0".equals(kind))
									   sql.append("((b.B0110=a.codeitemid) and ((b.E0122 is null) or (b.E0122='')) or ((b.E01A1=a.codeitemid) and (b.E01A1='"+code+"')))");
									else
									  sql.append("((b.B0110=a.codeitemid) and ((b.E0122 is null) or (b.E0122='')) and ((b.E01A1 is null) or (b.E01A1='')))");
									sql.append(")");
									sql.append(" and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date ");
									sql.append(" and (a.view_chart<>1 or a.view_chart is null)");
									sql.append("order by b.a0000");
									rs=ExecuteSQL.executeMyQuery(sql.toString());
									if(rs !=null && rs.size()>0)
										ishavechilds=true;
								}else
								{
									sql.append("SELECT  b.A0101 from organization a, ");
									sql.append(dbname);
									sql.append("A01 b ");
									sql.append(" where (a.codeitemid='");
									sql.append(code);
									sql.append("')  and (((b.E0122= a.codeitemid) and ((b.E01A1 is null) or (b.E01A1 =");
									sql.append("''))) or ");   // 有部门，无职位
									sql.append("(b.E01A1=a.codeitemid) or ");  // 有职位
									         // 有单位，无部门职位
									sql.append("((b.B0110=a.codeitemid) and ((b.E0122 is null) or (b.E0122='')) and ((b.E01A1 is null) or (b.E01A1='')))");
									sql.append(")");
									sql.append(" and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date ");
									sql.append(" and (a.view_chart<>1 or a.view_chart is null)");
									sql.append("order by b.a0000");
									rs=ExecuteSQL.executeMyQuery(sql.toString());
									if(rs !=null && rs.size()>0)
										ishavechilds=true;
								}
							}
							
							
							sql.delete(0,sql.length());	
							String isshoworgconut=paramehashmap.get("isshoworgconut")==null?"false":paramehashmap.get("isshoworgconut").toString();
							boolean bShowPos=true;
							String isshowposname=(String)paramehashmap.get("isshowposname");
							if(isshowposname==null||isshowposname.length()<=0|| "false".equals(isshowposname))
								bShowPos=false;
			        		if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
			        		{
							    sql.append("select organization.codesetid,organization.codeitemid,organization.grade,organization.codeitemdesc ");
							    sql.append(Sql_switcher.concat()+"(case when(codesetid<>'@K') then "+("true".equals(isshoworgconut)?ConverORGsql(backdate,bShowPos):"''")+" else '' end)");
							    sql.append(SqlDifference.getJoinSymbol());
							    sql.append(" " + ConverDBsql(dbname) + "  as codeitemdesc,");
							}
							else{
								sql.append("select organization.codesetid,organization.codeitemid,organization.grade,organization.codeitemdesc ");
			        			sql.append(Sql_switcher.concat()+"(case when(codesetid<>'@K') then "+("true".equals(isshoworgconut)?ConverORGsql(backdate,bShowPos):"''")+" else '' end)");
			        			sql.append(" as codeitemdesc,");
			        		}
				       		sql.append("count(" + dbname);
							sql.append("A01.a0100) as personcount,'org' as infokind from  ");
							sql.append("(select * from "+dbname+"A01 where e01a1 not in (select codeitemid from organization where view_chart='1')) ");
				            sql.append(dbname);
							sql.append("A01 RIGHT OUTER JOIN");
		                    sql.append(" organization ON ");
		                    sql.append(Sql_switcher.substr(dbname + "A01.B0110","1",Sql_switcher.length("organization.codeitemid"))); 	                    
		                    sql.append("= organization.codeitemid OR ");
							sql.append(Sql_switcher.substr(dbname + "A01.e0122","1",Sql_switcher.length("organization.codeitemid")));
							sql.append("= organization.codeitemid OR ");
							sql.append(Sql_switcher.substr(dbname + "A01.e01a1","1",Sql_switcher.length("organization.codeitemid")));
							sql.append("= organization.codeitemid");			
						    sql.append(" where organization.codeitemid='");
							sql.append(code);
							sql.append("'");
							sql.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
							sql.append(" group by organization.grade,organization.codeitemdesc,organization.codeitemid,organization.codesetid");
		        		}
		 				
						//System.out.println(sql.toString());
						
						printSVGCell(code, catalog_id, sql, svgos,paramehashmap,ishavechilds,orgtype);
			        }
			        else
			        {
			        	printSVGCellRoot(svgos,paramehashmap);
			        }
		        }	        
		        //System.out.println(svgos.toString());
     	        resp.setContentType( "image/svg+xml;charset=UTF-8" );  
		        //resp.setContentType( "text/xml;charset=UTF-8" );
     	        /**用getOutputStream在tomcat6环境下中文出错*/
     	        resp.getWriter().println( svgos.toString());
		        //resp.getOutputStream().println( svgos.toString()); 
		        //resp.getWriter().flush();                
	           }catch(Exception e)
	           {
	           	e.printStackTrace();
	           }	
	    }  
	    private StringBuffer createShadow(float x,float y,HashMap paramehashmap,String codesetid)
	    {
	        float _x1=Float.parseFloat(paramehashmap.get("cellheight").toString())/6;
	        /*左边阴影*/
	    	StringBuffer shadowsvg=new StringBuffer();
	    	shadowsvg.append("<path d=\"M ");
	    	shadowsvg.append(x + Float.parseFloat(paramehashmap.get("cellwidth").toString()));
	    	shadowsvg.append(",");
	    	shadowsvg.append(y);
	    	shadowsvg.append(" v ");
	    	shadowsvg.append(paramehashmap.get("cellheight"));
	    	shadowsvg.append(" l ");
	    	shadowsvg.append(_x1);
	    	shadowsvg.append(",-");
	    	shadowsvg.append(_x1);
	    	shadowsvg.append(" v -");
	    	shadowsvg.append(paramehashmap.get("cellheight"));
	    	shadowsvg.append(" l -");
	    	shadowsvg.append(_x1);
	    	shadowsvg.append(",");
	    	shadowsvg.append(_x1);
	    	shadowsvg.append(" z\" style=\"fill:");
	    	shadowsvg.append(getShadowColor(paramehashmap.get("cellcolor").toString(),codesetid));
	    	shadowsvg.append(";fill-opacity:0.5\"/>");
	    	/*上边阴影*/	    	 
	    	shadowsvg.append("<path d=\"M ");
	    	shadowsvg.append(x);
	    	shadowsvg.append(",");
	    	shadowsvg.append(y);
	    	shadowsvg.append(" h ");
	    	shadowsvg.append(paramehashmap.get("cellwidth"));
	    	shadowsvg.append(" l ");
	    	shadowsvg.append(_x1);
	    	shadowsvg.append(",-");
	    	shadowsvg.append(_x1);
	    	shadowsvg.append(" h -");
	    	shadowsvg.append(paramehashmap.get("cellwidth"));
	    	shadowsvg.append(" l -");
	    	shadowsvg.append(_x1);
	    	shadowsvg.append(",");
	    	shadowsvg.append(_x1);
	    	shadowsvg.append(" z\" style=\"fill:");
	    	shadowsvg.append(getShadowColor(paramehashmap.get("cellcolor").toString(),codesetid));
	    	shadowsvg.append(";fill-opacity:0.5\"/>");
			return shadowsvg;
	    }	   
	    private String getShadowColor(String orgcolor,String infokind)
	    {
	       if("UN".equals(infokind))
	            return orgcolor;
	       else if("UM".equals(infokind))
	            return orgcolor;
	       else if("@K".equals(infokind))
	            return orgcolor;
	       else
	    	return orgcolor;
	    }
	    /**
		 * @param dbname
		 * @param svgos
		 */
		private void printSVGCellRoot(StringBuffer svgos,HashMap paramehashmap) {
			String width=paramehashmap.get("cellwidth").toString();
			String height=paramehashmap.get("cellheight").toString();
			String rectwidth=paramehashmap.get("rectwidth").toString();
			String dbname=paramehashmap.get("dbnames").toString();
			String font_family=paramehashmap.get("fontfamily").toString();
			String fontsize=paramehashmap.get("fontsize").toString();
			String font_style=paramehashmap.get("font_style").toString();	
			String cellvspacewidth=paramehashmap.get("cellvspacewidth").toString();
			String rootname="";
			Connection conn = null;
			try {
				 conn = AdminDb.getConnection();
				 Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
				 rootname=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
				 if(rootname==null||rootname.length()<=0)
				 {
					 rootname=ResourceFactory.getProperty("tree.orgroot.orgdesc");
				 }
				 rootname=rootname!=null&&rootname.length()>0?rootname:"";
			}catch(Exception e){
				e.printStackTrace();
			}finally {	            
	            if (conn != null)
	            {
	            	try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
	            }
	        }
			CompTextOnecell compTextOnecell=new CompTextOnecell();	
			//String aTextarr[]=compTextOnecell.oneLineWordCountSvg(width,fontsize,height,rootname.length());
			//String aTextarr[]=compTextOnecell.oneTableWordCountSvg(Integer.parseInt(width,10), Integer.parseInt(height,10), rootname, Integer.parseInt(fontsize));
			HashMap map=compTextOnecell.oneTableTextLineSvg(Integer.parseInt(width,10), Integer.parseInt(height,10), rootname, Integer.parseInt(fontsize));
			fontsize=(String)map.get("fize");
			int iFize=Integer.parseInt(fontsize);
			ArrayList wordList=(ArrayList)map.get("wordList");
			int _colscount=0;
			if(wordList!=null&&wordList.size()>0)
			   _colscount=wordList.size();
			StringBuffer str=new StringBuffer();			
			float x=20;
			float y=17;
			float line1x1=(20 + Integer.parseInt(width,10));
			float line1x2=(20 + Integer.parseInt(width,10));
			float line2x1=(20 + Integer.parseInt(width,10)-Integer.parseInt(rectwidth,10)/2);
			float line2x2=(20+ Integer.parseInt(width,10) + Integer.parseInt(rectwidth,10)/2);
			float rectx=(20 + Integer.parseInt(width,10)-Integer.parseInt(rectwidth,10)/2);
			if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
			{
				line1x1+=Integer.parseInt(height,10)/8;
				line1x2+=Integer.parseInt(height,10)/8;
				line2x1+=Integer.parseInt(height,10)/8;
				line2x2+=Integer.parseInt(height,10)/8;
				rectx+=Integer.parseInt(height,10)/8;
			}			
			svgos.append("<g id=\""+parentenodestr+"\">");
			svgos.append("<g id=\""+parentenodestr+"p\" name=\""+parentenodestr+"p0\">");
			svgos.append("<rect  x=\"20\" y=\"17\" width=\"" + width + "\" height=\"" + height + "\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),"UN") + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
			if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
			    svgos.append(createShadow(x,y,paramehashmap,"UN").toString());
			if("true".equals(paramehashmap.get("graphaspect").toString()))
			{
				svgos.append("<line  id=\"ll00000000000000\" x1=\"" + line1x1 + "\" y1=\"" + (17 + Integer.parseInt(height,10)/2 -Integer.parseInt(rectwidth,10)/2)+"\" x2=\"" + line1x2 + "\" y2=\"" + (17 + Integer.parseInt(height,10)/2+ Integer.parseInt(rectwidth,10)/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				svgos.append("<line  id=\"l00000000000000\" x1=\"" + line2x1+ "\" y1=\"" + (17 + Integer.parseInt(height,10)/2) + "\" x2=\"" + line2x2 + "\" y2=\"" + (17 + Integer.parseInt(height,10)/2) + "\" style=\"stroke:#000000\"/>");
//				svgos.append("<rect  x=\"" + rectx + "\" y=\"" + (17 + Integer.parseInt(height,10)/2 -Integer.parseInt(rectwidth,10)/2) + "\" width=\"" + rectwidth + "\"  height=\"" + rectwidth + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),"UN") + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'','" + dbname  + "',true)\"  onclick=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'','" + dbname  + "',true)\"/>");
				//点击组织机构 第一个方块 加号的 颜色 #000000  垂直
				svgos.append("<rect  x=\"" + rectx + "\" y=\"" + (17 + Integer.parseInt(height,10)/2 -Integer.parseInt(rectwidth,10)/2) + "\" width=\"" + rectwidth + "\"  height=\"" + rectwidth + "\" fill-opacity=\"0.5\" style=\"fill:#ff0000;stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'','" + dbname  + "',true)\"  onclick=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'','" + dbname  + "',true)\"/>");
				//svgos.append("<text x=\"34\" y=\"42\" font-family=\""+font_family+"\" "+font_style+" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),"UN") + ";font-size:"+fontsize+";\">"+rootname+"</text>");
			}else
			{
				svgos.append("<line  id=\"l00000000000000\" x1=\"" + (20 + Integer.parseInt(width,10)/2) + "\" y1=\"" + (Integer.parseInt(height,10) + 17- Integer.parseInt(rectwidth,10)/2) + "\" x2=\"" + (20 + Integer.parseInt(width,10)/2) + "\" y2=\"" + (Integer.parseInt(height,10) + 17+ Integer.parseInt(rectwidth,10)/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				svgos.append("<line  id=\"ll00000000000000\" x1=\"" + (20+ Integer.parseInt(width,10)/2-Integer.parseInt(rectwidth,10)/2) + "\" y1=\"" + (17 + Integer.parseInt(height,10)) + "\" x2=\"" + (20+ Integer.parseInt(width,10)/2+Integer.parseInt(rectwidth,10)/2) + "\" y2=\"" + (17 + Integer.parseInt(height,10)) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
//				svgos.append("<rect  x=\"" + (20+ Integer.parseInt(width,10)/2-Integer.parseInt(rectwidth,10)/2) + "\" y=\"" + (Integer.parseInt(height,10) + 17- Integer.parseInt(rectwidth,10)/2) + "\" width=\"" + rectwidth + "\" height=\"" + rectwidth + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),"UN") + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'','" + dbname  + "',false)\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'','" + dbname  + "',false)\"/>");
//				水平 点击组织机构 第一个方块 加号的 颜色 #000000
				svgos.append("<rect  x=\"" + (20+ Integer.parseInt(width,10)/2-Integer.parseInt(rectwidth,10)/2) + "\" y=\"" + (Integer.parseInt(height,10) + 17- Integer.parseInt(rectwidth,10)/2) + "\" width=\"" + rectwidth + "\" height=\"" + rectwidth + "\" fill-opacity=\"0.5\" style=\"fill:#ff0000;stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'','" + dbname  + "',false)\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'','" + dbname  + "',false)\"/>");
				
				//svgos.append("<text x=\"34\" y=\"42\" font-family=\""+font_family+"\" "+font_style+"  style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),"UN") + ";font-size:"+fontsize+";\">"+rootname+"</text>");
			}	
			int _cellvspacewidth=cellvspacewidth!=null&&cellvspacewidth.length()>0?Integer.parseInt(cellvspacewidth):0;
			int dx=(int)(20+Integer.parseInt(width,10)/4);//20是第一个格子和边框的距离
			if(_colscount>0)     //显示字体
		    { 
				svgos.append("<text x=\""+dx+"\" y=\"20\" font-family=\""+font_family+"\" "+font_style+" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),"UN") + ";font-size:"+iFize+"px;\">");
				  for(int j=0;j<wordList.size();j++)
				  {
					//int dx= (int)(10/2*3 + _cellvspacewidth*2);
					//int dx= (int)(Integer.parseInt(width,10)/2);
					/*if(j==_colscount)
			      	{
						svgos.append(" <tspan x=\""+dx+"\" dy=\""+(iFize+2)+"\">");
						svgos.append(rootname.substring((j)*_wordcount,rootname.length()));
						svgos.append("</tspan>");
			      	}else if(j==0)
			      	{
			      		int hh=Integer.parseInt(height)/(_colscount*4);
			      		svgos.append(" <tspan x=\""+dx+"\" dy=\""+hh+"\">");//dy高=0，是文本框的中轴线
			      		svgos.append(rootname.substring(j*_wordcount,(j+1)*_wordcount));
						svgos.append("</tspan>");
			      	}else
			      	{
			      		svgos.append(" <tspan x=\""+dx+"\" dy=\""+(iFize+2)+"\">");
			      		svgos.append(rootname.substring(j*_wordcount,(j+1)*_wordcount));
			      		svgos.append("</tspan>");
			      	}*/
					int dy=(iFize+2);
					if(j==0)
				    {
				    	  float fh=Integer.parseInt(height,10)/(Float.parseFloat(_colscount+"")*2);
			      		  dy=Math.round(fh);
				    }
					svgos.append(" <tspan x=\""+dx+"\" dy=\""+dy+"\" style=\"fill:#000000;font-weight:bold;font-size:12px;\">");
		      		svgos.append(wordList.get(j));
		      		svgos.append("</tspan>");
		        }	
				svgos.append("</text>");
		    }else
		    {
		    	svgos.append("<text x=\""+dx+"\" y=\"35\" font-family=\""+font_family+"\" "+font_style+"  style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),"UN") + ";font-size:"+iFize+"px;\">"+rootname+"</text>");
		    }
			svgos.append("</g>");
			svgos.append("<g id=\""+parentenodestr+"c\" style=\"visibility:hidden\"></g>"); 
			svgos.append("<g id=\"g00000000010000\" name=\""+parentenodestr+"\"></g>"); 
			svgos.append("<g id=\"gchildswidth\" name=\"g100\"></g>"); 
			svgos.append("</g>");
			svgos.append("</svg>");
			
		}
		/**
		 * 历史结构
		 * @param code
		 * @param catalog_id
		 * @param sql
		 * @param svgos
		 * @param paramehashmap
		 * @param ishavechilds
		 */
		private void printHistorySVGCellRoot(String catalog_id, StringBuffer sql, StringBuffer svgos,HashMap paramehashmap) {
			List rs=ExecuteSQL.executeMyQuery(sql.toString());
			String fontsize=paramehashmap.get("fontsize").toString();
			String font_style=paramehashmap.get("font_style").toString();
			String font_family=paramehashmap.get("fontfamily").toString();
			String rectwidth=paramehashmap.get("rectwidth").toString();
			String dbname=paramehashmap.get("dbnames").toString();
			int width=Integer.parseInt(paramehashmap.get("cellwidth").toString());
			int height=Integer.parseInt(paramehashmap.get("cellheight").toString());
			int iFize=Integer.parseInt(fontsize);
			String cellvspacewidth=paramehashmap.get("cellvspacewidth").toString();
			String rootname="";
			if(!rs.isEmpty())
			{
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				rootname=(String)rec.get("name");
			}
			String codesetid="";		
			float x=20;
			float y=17;
			float line1x1=(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString()));
			float line1x2=(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString()));
			float line2x1=(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString()) -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2);
			float line2x2=(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString()) + Integer.parseInt(paramehashmap.get("rectwidth").toString())/2);	
			float rectx=(20 + width-Integer.parseInt(rectwidth,10)/2);
			if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
			{
				line1x1+=height/8;
				line1x2+=height/8;
				line2x1+=height/8;
				line2x2+=height/8;
				rectx+=height/8;
			}
			CompTextOnecell compTextOnecell=new CompTextOnecell();						
			HashMap map=compTextOnecell.oneTableTextLineSvg(width, height, rootname, Integer.parseInt(fontsize));
			fontsize=(String)map.get("fize");
			iFize=Integer.parseInt(fontsize);
			ArrayList wordList=(ArrayList)map.get("wordList");
			int _colscount=0;
			if(wordList!=null&&wordList.size()>0)
			   _colscount=wordList.size();
			svgos.append("<g id=\""+parentenodestr+"\">");
			svgos.append("<g id=\""+parentenodestr+"p\" name=\""+parentenodestr+"p0\">");
			svgos.append("<rect  x=\"20\" y=\"17\" width=\"" + width + "\" height=\"" + height + "\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),"UN") + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
			if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
			    svgos.append(createShadow(x,y,paramehashmap,"UN").toString());
			if("true".equals(paramehashmap.get("graphaspect").toString()))
			{
				svgos.append("<line  id=\"ll00000000000000\" x1=\"" + line1x1 + "\" y1=\"" + (17 + height/2 -Integer.parseInt(rectwidth,10)/2)+"\" x2=\"" + line1x2 + "\" y2=\"" + (17 + height/2+ Integer.parseInt(rectwidth,10)/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				svgos.append("<line  id=\"l00000000000000\" x1=\"" + line2x1+ "\" y1=\"" + (17 + height/2) + "\" x2=\"" + line2x2 + "\" y2=\"" + (17 + height/2) + "\" style=\"stroke:#000000\"/>");
				svgos.append("<rect  x=\"" + rectx + "\" y=\"" + (17 + height/2 -Integer.parseInt(rectwidth,10)/2) + "\" width=\"" + rectwidth + "\"  height=\"" + rectwidth + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),"UN") + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'" + catalog_id + "','n',true)\"  onclick=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'" + catalog_id + "','n',true)\"/>");
			}else
			{
				svgos.append("<line  id=\"l00000000000000\" x1=\"" + (20 + width/2) + "\" y1=\"" + (height+ 17- Integer.parseInt(rectwidth,10)/2) + "\" x2=\"" + (20 + width/2) + "\" y2=\"" + (height+ 17+ Integer.parseInt(rectwidth,10)/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				svgos.append("<line  id=\"ll00000000000000\" x1=\"" + (20+ width/2-Integer.parseInt(rectwidth,10)/2) + "\" y1=\"" + (17 + height) + "\" x2=\"" + (20+ width/2+Integer.parseInt(rectwidth,10)/2) + "\" y2=\"" + (17 + height) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				svgos.append("<rect  x=\"" + (20+ width/2-Integer.parseInt(rectwidth,10)/2) + "\" y=\"" + (height + 17- Integer.parseInt(rectwidth,10)/2) + "\" width=\"" + rectwidth + "\" height=\"" + rectwidth + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),"UN") + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'" + catalog_id + "','" + dbname + "',false)\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'" + catalog_id + "','" + dbname + "',false)\"/>");
			}	
			int dx=(int)(20+width/8);//20是第一个格子和边框的距离
			if(_colscount>1)     //显示字体
		    { 
				svgos.append("<text x=\""+dx+"\" y=\"20\" font-family=\""+font_family+"\" "+font_style+" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),"UN") + ";font-size:"+iFize+"px;\">");
				for(int j=0;j<wordList.size();j++)
		        {
					/*if(j==_colscount)
			      	{
						svgos.append(" <tspan x=\""+dx+"\" dy=\""+(iFize+2)+"\">");
						svgos.append(rootname.substring((j)*_wordcount,rootname.length()));
						svgos.append("</tspan>");
			      	}else if(j==0)
			      	{
			      		int hh=height/(_colscount*4);
			      		svgos.append(" <tspan x=\""+dx+"\" dy=\""+hh+"\">");//dy高=0，是文本框的中轴线
			      		svgos.append(rootname.substring(j*_wordcount,(j+1)*_wordcount));
						svgos.append("</tspan>");
			      	}else
			      	{
			      		svgos.append(" <tspan x=\""+dx+"\" dy=\""+(iFize+2)+"\">");
			      		svgos.append(rootname.substring(j*_wordcount,(j+1)*_wordcount));
			      		svgos.append("</tspan>");
			      	}*/
					int dy=(iFize+2);
					if(j==0)
				      {
				    	  float fh=height/(Float.parseFloat(_colscount+"")*2);
			      		  dy=Math.round(fh);
				      }
					svgos.append(" <tspan x=\""+dx+"\" dy=\""+dy+"\">");
		      		svgos.append(wordList.get(j));
		      		svgos.append("</tspan>");
		        }	
				svgos.append("</text>");
		    }else
		    {
		    	svgos.append("<text x=\""+dx+"\" y=\"35\" font-family=\""+font_family+"\" "+font_style+"  style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),"UN") + ";font-size:"+iFize+"px;\">"+rootname+"</text>");
		    }
			svgos.append("</g>");
			svgos.append("<g id=\""+parentenodestr+"c\" style=\"visibility:hidden\"></g>"); 
			svgos.append("<g id=\"g00000000010000\" name=\""+parentenodestr+"\"></g>"); 
			svgos.append("<g id=\"gchildswidth\" name=\"g100\"></g>"); 
			svgos.append("</g>");
			svgos.append("</svg>");
		}
		/**
		 * @param code
		 * @param catalog_id
		 * @param sql
		 * @param svgos
		 */
		private void printSVGCell(String code, String catalog_id, StringBuffer sql, StringBuffer svgos,HashMap paramehashmap,boolean ishavechilds,String orgtype) {
			int width=Integer.parseInt(paramehashmap.get("cellwidth").toString());
			int height=Integer.parseInt(paramehashmap.get("cellheight").toString());
			int rectwidth=Integer.parseInt(paramehashmap.get("rectwidth").toString());
			List rs=ExecuteSQL.executeMyQuery(sql.toString());
			String isshowpersoncount=paramehashmap.get("isshowpersonconut").toString();
			String isshowname=paramehashmap.get("isshowpersonname").toString();
			String fontsize=paramehashmap.get("fontsize").toString();
			String font_style=paramehashmap.get("font_style").toString();
			String font_family=paramehashmap.get("fontfamily").toString();
			String cellvspacewidth=paramehashmap.get("cellvspacewidth").toString();
			String grade="0";
			String codeitemdesc="";
			String codesetid="";
			String codeitemid="";
			String personcount="";
			float line1x1=(20 + width);
			float line1x2=(20 + width);
			float line2x1=(20 + width-rectwidth/2);
			float line2x2=(20+ width + rectwidth/2);	
			if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
			{
				line1x1+=height/8;
				line1x2+=height/8;
				line2x1+=height/8;
				line2x2+=height/8;
			}
			if(!rs.isEmpty()){
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				grade=(String)rec.get("grade");
				codeitemdesc=(String)rec.get("codeitemdesc");
				codesetid=(String)rec.get("codesetid");
				codeitemid=(String)rec.get("codeitemid");
				personcount=(String)rec.get("personcount");
			}else
				return;
			/*int _wordcount=OneLineWordCount(width);
			int _colscount=codeitemdesc.length()/_wordcount;*/			
			float x=20;
			float y=17;
			svgos.append("<g id=\""+parentenodestr+"\">");
			svgos.append("<g id=\""+parentenodestr+"p\" name=\""+parentenodestr+"p0\">");
		    String url="";
		    String onclickStr="";
			if(codesetid!=null && "@K".equalsIgnoreCase(codesetid))
			{
				svgos.append("<a target=\"_blank\" xlink:href=\"/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=3&amp;orgtype="+orgtype+"\">");
				url="/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=3&amp;orgtype="+orgtype;
				onclickStr="onclick=\"javascript:viewmess('"+url+"');\"";
			}   
			else
			{
				svgos.append("<a  target=\"_blank\" xlink:href=\"/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=2&amp;orgtype="+orgtype+"\">");
				url="/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=2&amp;orgtype="+orgtype;
				onclickStr="onclick=\"javascript:viewmess('"+url+"');\"";
			}			
			svgos.append("<rect  x=\"20\" y=\"17\" width=\"" + width + "\" height=\"" + height + "\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" "+onclickStr+"/>");
			if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
			   svgos.append(createShadow(x,y,paramehashmap,codesetid).toString());
			if(ishavechilds || ishavechilds && personcount!=null && Integer.parseInt(personcount)>0 && "true".equals(isshowname))
				if("true".equals(paramehashmap.get("graphaspect").toString()))
				{
					svgos.append("<line  id=\"ll00000000000000\" x1=\"" + line1x1 + "\" y1=\"" + (17 + height/2 - rectwidth/2) + "\" x2=\"" +line1x2 + "\" y2=\"" + (17 + height/2+rectwidth/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
					svgos.append("<line  id=\"l00000000000000\" x1=\"" + line2x1 + "\" y1=\"" + (17+ height/2) + "\" x2=\"" + line2x2 + "\" y2=\"" + (17 + height/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				}
			    else
			    {
			    	svgos.append("<line  id=\"l00000000000000\" x1=\"" +(20 + width/2) + "\" y1=\"" + (17 + height -rectwidth/2) + "\" x2=\"" +(20 + width/2) + "\" y2=\"" + (17 + height + rectwidth/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
					svgos.append("<line  id=\"ll00000000000000\" x1=\"" + (20 + width/2-rectwidth/2) + "\" y1=\"" + (17 + height) + "\" x2=\"" +(20 + width/2 + rectwidth/2) + "\" y2=\"" + (17 + height) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
			    }		
			CompTextOnecell compTextOnecell=new CompTextOnecell();	
			/*String aTextarr[]=compTextOnecell.oneTableWordCountSvg(width, height, codeitemdesc, Integer.parseInt(fontsize));
			fontsize=aTextarr[0];
			int iFize=Integer.parseInt(fontsize);
			String wordcount=aTextarr[1];
			String colscount=aTextarr[2];
			int _colscount=colscount!=null&&colscount.length()>0?Integer.parseInt(colscount):0;
			int _wordcount=wordcount!=null&&wordcount.length()>0?Integer.parseInt(wordcount):0;*/
			HashMap map=compTextOnecell.oneTableTextLineSvg(width, height, codeitemdesc, Integer.parseInt(fontsize));
			fontsize=(String)map.get("fize");
			int iFize=Integer.parseInt(fontsize);
			ArrayList wordList=(ArrayList)map.get("wordList");
			int _colscount=0;
			if(wordList!=null&&wordList.size()>0)
			   _colscount=wordList.size();
			int dx=(int)(20+width/8);//20是第一个格子和边框的距离
			svgos.append("<text x=\""+dx+"\" y=\"20\" font-family=\""+font_family+"\" "+font_style+" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+iFize+"px;\" "+onclickStr+">");
			if(_colscount>1)     
			{ 
				
			    for(int j=0;j<wordList.size();j++)
			    {
			      /*if(j==_colscount)
			  	  {			  	     	    
			    	   svgos.append("<tspan x=\""+dx+"\" dy=\""+(iFize+3)+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+iFize+";\">");
			    	   svgos.append(codeitemdesc.substring((j)*_wordcount,codeitemdesc.length()));
			    	   svgos.append("</tspan>");
			   	  }else if(j==0)
			   	  {
			   		
		      		  float fh=height/(Float.parseFloat(_colscount+"")*4);
		      		  int hh=Math.round(fh);
			   		  svgos.append("<tspan x=\""+dx+"\" dy=\""+hh+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+iFize+";\">");
			   		  svgos.append(codeitemdesc.substring(j*_wordcount,(j+1)*_wordcount));	
			   		  svgos.append("</tspan>");
			   	  }else
			  	  {
			   		
			   		  svgos.append("<tspan x=\""+dx+"\" dy=\""+(iFize+3)+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+iFize+";\">");
			   		  svgos.append(codeitemdesc.substring(j*_wordcount,(j+1)*_wordcount));	
			   		  svgos.append("</tspan>");
			  	  } */
			      int dy=iFize+3;
			      if(j==0)
			      {
			    	  float fh=height/(Float.parseFloat(_colscount+"")*2);
		      		  dy=Math.round(fh);
			      }
			 	  svgos.append("<tspan x=\""+dx+"\" dy=\""+dy+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+iFize+"px;\">");
			   	  svgos.append(wordList.get(j));	
			   	  svgos.append("</tspan>");
			 	  
				}
			}
			else
			{
				svgos.append("<tspan x=\""+dx+"\" dy=\""+(int)(height/2)+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:11;\">");
				svgos.append(codeitemdesc);
				svgos.append("</tspan>");
				
			}
			/*if("true".equalsIgnoreCase(isshowpersoncount))
			{
				svgos.append("<tspan x=\"30\" dy=\"15\" style=\"fill:#000000;font-weight:bold;font-size:11;\">");
				svgos.append(personcount);
				svgos.append("</tspan>");
			}*/
			svgos.append("</text>");
			svgos.append("</a>");
			if(ishavechilds || ishavechilds && personcount!=null && Integer.parseInt(personcount)>0 && "true".equals(isshowname))
		        getClickRect(code, catalog_id, svgos, grade, codesetid,paramehashmap);
			svgos.append("</g>");
			svgos.append("<g id=\""+parentenodestr+"c\" style=\"visibility:hidden\"></g>"); 
			svgos.append("<g id=\"g00000000010000\" name=\""+parentenodestr+"\"></g>");
			svgos.append("<g id=\"gchildswidth\" name=\"g100\"></g>"); 
			svgos.append("</g>");
			svgos.append("</svg>");			
		}
		/**
		 * @param code
		 * @param catalog_id
		 * @param sql
		 * @param svgos
		 */
		private void printSVGCellCodeCatalog_id(String code, String catalog_id, StringBuffer sql, StringBuffer svgos,HashMap paramehashmap,boolean ishavechilds) {
			List rs=ExecuteSQL.executeMyQuery(sql.toString());
			String fontsize=paramehashmap.get("fontsize").toString();
			String font_style=paramehashmap.get("font_style").toString();
			String font_family=paramehashmap.get("fontfamily").toString();
			int width=Integer.parseInt(paramehashmap.get("cellwidth").toString());
			int height=Integer.parseInt(paramehashmap.get("cellheight").toString());
			int iFize=Integer.parseInt(fontsize);
			String cellvspacewidth=paramehashmap.get("cellvspacewidth").toString();
			String grade="0";
			String codeitemdesc="";
			String codesetid="";
			String codeitemid="";
			float x=20;
			float y=17;
			float line1x1=(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString()));
			float line1x2=(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString()));
			float line2x1=(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString()) -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2);
			float line2x2=(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString()) + Integer.parseInt(paramehashmap.get("rectwidth").toString())/2);	
			if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
			{
				line1x1+=Integer.parseInt(paramehashmap.get("cellheight").toString())/8;
				line1x2+=Integer.parseInt(paramehashmap.get("cellheight").toString())/8;
				line2x1+=Integer.parseInt(paramehashmap.get("cellheight").toString())/8;
				line2x2+=Integer.parseInt(paramehashmap.get("cellheight").toString())/8;
			}
			if(!rs.isEmpty() && code!=null && code.length()>0)
            {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				grade=(String)rec.get("grade");
				codeitemdesc=(String)rec.get("codeitemdesc");
				codesetid=(String)rec.get("codesetid");
				codeitemid=(String)rec.get("codeitemid");
	
			}
			else
			{
				//LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				//codeitemdesc=(String)rec.get("name");
				codeitemdesc="";
				ishavechilds=false;
				
			}
			CompTextOnecell compTextOnecell=new CompTextOnecell();			
			//String aTextarr[]=compTextOnecell.oneLineWordCountSvg(width+"",fontsize,height+"",codeitemdesc.length());
			HashMap map=compTextOnecell.oneTableTextLineSvg(width, height, codeitemdesc, Integer.parseInt(fontsize));
			fontsize=(String)map.get("fize");
			iFize=Integer.parseInt(fontsize);
			ArrayList wordList=(ArrayList)map.get("wordList");
			int _colscount=0;
			if(wordList!=null&&wordList.size()>0)
			   _colscount=wordList.size();
			svgos.append("<g id=\""+parentenodestr+"\">");
			svgos.append("<g id=\""+parentenodestr+"p\" name=\""+parentenodestr+"p0\">");
			String onclick="";
			String url="";		
			if(codesetid!=null && "@K".equalsIgnoreCase(codesetid))
			{
				svgos.append("<a target=\"_blank\" xlink:href=\"/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=3\">");	
				url="/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=3";
				onclick="onclick=\"javascript:viewmess('"+url+"');\"";
			}   
			else
			{
				svgos.append("<a  target=\"_blank\" xlink:href=\"/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=2\">");
				url="/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=2";
				onclick="onclick=\"javascript:viewmess('"+url+"');\"";
			}
				
			
			svgos.append("<rect  x=\"20\" y=\"17\" width=\"" + Integer.parseInt(paramehashmap.get("cellwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("cellheight").toString()) + "\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" "+onclick+"/>");
			if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
			   svgos.append(createShadow(x,y,paramehashmap,codesetid).toString());
			if(ishavechilds)
				if("true".equals(paramehashmap.get("graphaspect")))
				{
					svgos.append("<line  id=\"ll00000000000000\" x1=\"" + line1x1 + "\" y1=\"" + (17 + Integer.parseInt(paramehashmap.get("cellheight").toString())/2 - Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" x2=\"" +line1x2 + "\" y2=\"" + (17 + Integer.parseInt(paramehashmap.get("cellheight").toString())/2+Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
					svgos.append("<line  id=\"l00000000000000\" x1=\"" + line2x1 + "\" y1=\"" + (17+ Integer.parseInt(paramehashmap.get("cellheight").toString())/2) + "\" x2=\"" + line2x2 + "\" y2=\"" + (17 + Integer.parseInt(paramehashmap.get("cellheight").toString())/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				}
			    else
			    {
			    	svgos.append("<line  id=\"l00000000000000\" x1=\"" +(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2) + "\" y1=\"" + (17 + Integer.parseInt(paramehashmap.get("cellheight").toString()) -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" x2=\"" +(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2) + "\" y2=\"" + (17 + Integer.parseInt(paramehashmap.get("cellheight").toString()) + Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
					svgos.append("<line  id=\"ll00000000000000\" x1=\"" + (20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2-Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" y1=\"" + (17 + Integer.parseInt(paramehashmap.get("cellheight").toString())) + "\" x2=\"" +(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2 + Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" y2=\"" + (17 + Integer.parseInt(paramehashmap.get("cellheight").toString())) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
			    }	
			svgos.append("<text x=\"30\" y=\"20\" font-family=\""+font_family+"\" "+font_style+" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-size:"+fontsize+";\" "+onclick+">");
			if(_colscount>1)     
			{ 
				 
				int _cellvspacewidth=cellvspacewidth!=null&&cellvspacewidth.length()>0?Integer.parseInt(cellvspacewidth):0;
			    for(int j=0;j<wordList.size();j++)
			    {
			      int dx= (int)(width/2+_cellvspacewidth);
				  int dy=(iFize+3);
				  if(j==0)
			      {
			    	  float fh=height/(Float.parseFloat(_colscount+"")*2);
		      		  dy=Math.round(fh);
			      }
				  svgos.append("<tspan x=\""+dx+"\" dy=\""+dy+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+iFize+"px;\">");
		   		  svgos.append(wordList.get(j));	
		   		  svgos.append("</tspan>");
				}
			}
			else
			{
				svgos.append("<tspan x=\"30\" dy=\"15\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+fontsize+";\">");
				svgos.append(codeitemdesc);
				svgos.append("</tspan>");
				
			}		
			svgos.append("</text>");
			svgos.append("</a>");
			if(ishavechilds)
		        getClickRect(code, catalog_id, svgos, grade, codesetid,paramehashmap);
			
			svgos.append("</g>");
			svgos.append("<g id=\""+parentenodestr+"c\" style=\"visibility:hidden\"></g>"); 
			svgos.append("<g id=\"g00000000010000\" name=\""+parentenodestr+"\"></g>");
			svgos.append("<g id=\"gchildswidth\" name=\"g100\"></g>"); 
			svgos.append("</g>");
			svgos.append("</svg>");
		}

		/**
		 * @param code
		 * @param catalog_id
		 * @param svgos
		 * @param grade
		 * @param codesetid
		 */
		private void getClickRect(String code, String catalog_id, StringBuffer svgos, String grade, String codesetid,HashMap paramehashmap) {
			String dbname=paramehashmap.get("dbnames").toString();
			int height=Integer.parseInt(paramehashmap.get("cellheight").toString());
			float rectx=(20 + Integer.parseInt(paramehashmap.get("cellwidth").toString()) -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2);
			if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
				rectx+= height/8;
			if(catalog_id!=null && catalog_id.length()>0)
		     {
				if(code!=null && code.length()>0)
				{
					if("true".equals(paramehashmap.get("graphaspect")))
					{
						svgos.append("<rect  x=\"" +rectx + "\" y=\"" + (17 + height/2 -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\"  style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'" + catalog_id + "','n',true)\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'" + catalog_id + "','n',true)\"/>");
				    }
					else
					{
						svgos.append("<rect  x=\"" + (20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2-Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" y=\"" + (17 + height -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\"  style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'" + catalog_id + "','" + dbname + "',false)\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'" + catalog_id + "','" + dbname + "',false)\"/>");
					}
				}
				else
				{
					
					if("true".equals(paramehashmap.get("graphaspect")))                                                                                                                                                                                                                                                                                                                                                                                                                   
					    svgos.append("<rect  x=\"" +rectx + "\" y=\"" + (17 + height/2 -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'" + catalog_id + "','" + dbname  + "',true)\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'" + catalog_id + "','" + dbname  + "',true)\"/>");
				    else
				    	svgos.append("<rect  x=\"" + (20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2-Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" y=\"" + (17 + height -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'" + catalog_id + "','" + dbname + "',false)\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','','UN',0,'" + catalog_id + "','" + dbname + "',false)\"/>");    	
				}
			 }else
			 {
			 	if(code!=null && code.length()>0)
				{
			 		if("true".equals(paramehashmap.get("graphaspect")))
//			 			svgos.append("<rect  x=\"" +rectx + "\" y=\"" + (17 + height/2 -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'','" + dbname  + "',true)\" onload=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'','" + dbname  + "',true)\"/>");
//			 			点击 除了组织机构 意外的 第一个框体 加号颜色 改为 #000000  垂直
			 			svgos.append("<rect  x=\"" +rectx + "\" y=\"" + (17 + height/2 -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:#ff0000;stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'','" + dbname  + "',true)\" onload=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'','" + dbname  + "',true)\"/>");
			 		else
			 			//水平 点击 除了组织机构 意外的 第一个框体 加号颜色 改为 #000000
//			 			svgos.append("<rect  x=\"" + (20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2-Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" y=\"" + (17 + height -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\"  height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'','" + dbname  + "',false)\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'','" + dbname  + "',false)\"/>");
			 			svgos.append("<rect  x=\"" + (20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2-Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" y=\"" + (17 + height -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\"  height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:#ff0000;stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'','" + dbname  + "',false)\" onclick=\"loadChildNode(evt,'"+parentenodestr+"','" + code + "','" + codesetid + "'," + grade + ",'','" + dbname  + "',false)\"/>");
				}			
			 }
		}


		/* 
	     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	     */
	    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
	            throws ServletException, IOException {
	        doPost(arg0, arg1);
	    }
	    private int OneLineWordCount(int width)
	    {
	    	   return width*6/80;
	    }
	    private String ConverDBsql(String dbname)
		{
			String resultsql="";
			switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL: {
				resultsql="'(' + Convert(Varchar,count(" + dbname + "A01.a0100)) + '人)'" ;
				break;
			}
			case Constant.DB2: {
				resultsql="'(' + To_Char(count(" + dbname + "A01.a0100)) + '人)'" ;
				break;
			}
			case Constant.ORACEL: {
				resultsql="'(' || count(" + dbname + "A01.a0100) || '人)'" ;
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
	    
	    private String getCellColor(String orgcolor,String infokind)
	    {
	    	if("UN".equalsIgnoreCase(infokind))
	    		return orgcolor;
	    	else if("UM".equalsIgnoreCase(infokind))
	    		return orgcolor;
	    	else if("@K".equalsIgnoreCase(infokind))
	    		return orgcolor;
	    	else
	    		return orgcolor;
	    }
	    private String getFontColor(String fontcolor,String infokind)
	    {
	    	return fontcolor;
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
