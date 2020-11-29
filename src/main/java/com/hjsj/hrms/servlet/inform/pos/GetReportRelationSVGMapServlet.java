package com.hjsj.hrms.servlet.inform.pos;

import com.hjsj.hrms.businessobject.general.orgmap.CompTextOnecell;
import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 
 *<p>Title:GetReportRelationSVGMapServlet.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 10, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class GetReportRelationSVGMapServlet extends HttpServlet {
	   /* 
  * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
  */
	private  Category cat = Category.getInstance(this.getClass());
 protected void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
		// TODO Auto-generated method stub
		String code=req.getParameter("code");
		String dbnameini="usr";
		UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);
		if(code==null || code!=null && code.length()==0)
		{
//			code=userview.getManagePrivCodeValue();
			ArrayList Dblist=userview.getPrivDbList();
			if(Dblist.size()>0)
				dbnameini=Dblist.get(0).toString();
			
		}
		String constant=req.getParameter("constant");	
		if(constant==null||constant.length()<=0|| "null".equals(constant))
		{
			return;
		}
		if(DataDictionary.getFieldItem(constant)==null || "0".equals(DataDictionary.getFieldItem(constant).getUseflag())){
			return;
		}
		String catalog_id=req.getParameter("catalog_id");
		String kind=req.getParameter("kind");	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = req.getParameter("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
	    HashMap paramehashmap=new SetOrgOptionParameter().ReadOutParameterXml("POS_MAPOPTION",true,dbnameini,userview);    		
		StringBuffer sql=new StringBuffer();			
     try{
	        StringBuffer svgos=new StringBuffer();
	        svgos.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	        svgos.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">");
	        if("true".equals(paramehashmap.get("graphaspect").toString()))
	        	svgos.append("<svg width=\"100%\"  height=\"100%\"  viewBox=\"0 0 633 140\" id=\"mainview\">");
	        else
	            svgos.append("<svg width=\"100%\"  height=\"100%\"  viewBox=\"0 0 100 500\" id=\"mainview\">");
	        svgos.append("<script><![CDATA[");
	        svgos.append("]]></script>");
	        if(code!=null && code.length()>0)
			{
        		boolean ishavechilds=false;
        		String dbname=paramehashmap.get("dbnames").toString();
 				sql.append("select K01.E01A1  from K01 K01, organization org ");
			    sql.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '");
				sql.append(code);
				sql.append("' ");
				sql.append(" and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date ");
				List rs=ExecuteSQL.executeMyQuery(sql.toString());
				if(rs !=null && rs.size()>0)
					ishavechilds=true;
				sql.delete(0,sql.length());		
				if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
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
					rs=ExecuteSQL.executeMyQuery(sql.toString());
					if(rs !=null && rs.size()>0)
						ishavechilds=true;
				}
				
				
				sql.delete(0,sql.length());						
				
        		if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
        		{
				    sql.append("select organization.codesetid,organization.codeitemid,organization.grade,organization.codeitemdesc ");
				    sql.append(SqlDifference.getJoinSymbol());
				    sql.append(" " + ConverDBsql(dbname) + "  as codeitemdesc,");
				}
				else
					sql.append("select organization.codesetid,organization.codeitemid,organization.grade,organization.codeitemdesc  as codeitemdesc,");
	       		sql.append("count(" + dbname);
				sql.append("A01.a0100) as personcount,'org' as infokind from  ");
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
				printSVGCell(code, catalog_id, sql, svgos,paramehashmap,ishavechilds,constant);
	        }
	        else  //  根节点，汇报关系
	        {
	        	boolean ishavechilds=false;
        		String dbname=paramehashmap.get("dbnames").toString();
 				sql.append("select K01.E01A1, Org.codeItemDesc  from K01 K01, organization org ");
			    sql.append("  where K01.E01A1 = org.CodeItemId  order by org.A0000 ");
//			    System.out.println(sql.toString());
				List rs=ExecuteSQL.executeMyQuery(sql.toString());
				if(rs !=null && rs.size()>0)
					ishavechilds=true;
				sql.delete(0,sql.length());		
				if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonname").toString()))
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
//					System.out.println(sql.toString());
					rs=ExecuteSQL.executeMyQuery(sql.toString());
					if(rs !=null && rs.size()>0)
						ishavechilds=true;
				}
				
				
				sql.delete(0,sql.length());						
				
        		if("true".equalsIgnoreCase(paramehashmap.get("isshowpersonconut").toString()))
        		{
				    sql.append("select organization.codesetid,organization.codeitemid,organization.grade,organization.codeitemdesc ");
				    sql.append(SqlDifference.getJoinSymbol());
				    sql.append(" " + ConverDBsql(dbname) + "  as codeitemdesc,");
				}
				else
					sql.append("select organization.codesetid,organization.codeitemid,organization.grade,organization.codeitemdesc  as codeitemdesc,");
        		

	       		sql.append("count(" + dbname);
				sql.append("A01.a0100) as personcount,'org' as infokind from  ");				
				sql.append(dbname);
				sql.append("A01 RIGHT OUTER JOIN");
                sql.append(" organization ON ");
                sql.append(Sql_switcher.substr(dbname + "A01.B0110","1",Sql_switcher.length("organization.codeitemid"))); 	                    
                sql.append("= organization.codeitemid OR ");
				sql.append(Sql_switcher.substr(dbname + "A01.e0122","1",Sql_switcher.length("organization.codeitemid")));
				sql.append("= organization.codeitemid OR ");
				sql.append(Sql_switcher.substr(dbname + "A01.e01a1","1",Sql_switcher.length("organization.codeitemid")));
				sql.append("= organization.codeitemid");	
				UserView userView = (UserView)req.getSession().getAttribute("userView");
				if(userView.isSuper_admin()){
				    sql.append(" where organization.codeitemid in (select K01.E01A1  from K01 K01, organization org  ");
				    sql.append("  where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '' )");
				}else{
					code=userView.getManagePrivCodeValue();
					sql.append(" where organization.codeitemid not in (select K01.E01A1  from K01 K01, organization org  ");
				    sql.append("  where K01.E01A1 = org.CodeItemId  and org.CodeItemId like '"+code+"%' )");
				    sql.append("  and organization.CodeItemId like '"+code+"%'");
				}
			    sql.append(" and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
//				sql.append(code);
//				sql.append("'");
			    
				sql.append(" group by organization.grade,organization.codeitemdesc,organization.codeitemid,organization.codesetid");
//				System.out.println(sql.toString());
//				List coders=ExecuteSQL.executeMyQuery(sql.toString());
//				if(!coders.isEmpty())
//				{
				printSVGCellRoot("", catalog_id, sql, svgos,paramehashmap,ishavechilds,constant);
//				}
				
	        }    
	        resp.setContentType( "image/svg+xml;charset=UTF-8" );
	        //System.out.println(svgos.toString());
	        resp.getWriter().println( svgos.toString()); 
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
	 * @param code
	 * @param catalog_id
	 * @param sql
	 * @param svgos
	 */
	private void printSVGCell(String code, String catalog_id, StringBuffer sql, StringBuffer svgos,HashMap paramehashmap,boolean ishavechilds,String constant) {
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
			//grade=(String)rec.get("grade");
			grade="0";
			codeitemdesc=(String)rec.get("codeitemdesc");
			codesetid=(String)rec.get("codesetid");
			codeitemid=(String)rec.get("codeitemid");
			personcount=(String)rec.get("personcount");
			if(paramehashmap.containsKey("isshowposup") &&  "true".equals(paramehashmap.get("isshowposup").toString()))
				codeitemdesc = getUnitAndDept(codeitemid)+"/"+codeitemdesc;
		}
		CompTextOnecell compTextOnecell=new CompTextOnecell();			
		//String aTextarr[]=compTextOnecell.oneLineWordCountSvg(width+"",fontsize,height+"",codeitemdesc.length());
		HashMap map=compTextOnecell.oneTableTextLineSvg(width, height, codeitemdesc, Integer.parseInt(fontsize));
		fontsize=(String)map.get("fize");
		int iFize=Integer.parseInt(fontsize);
		ArrayList wordList=(ArrayList)map.get("wordList");
		int _colscount=0;
		if(wordList!=null&&wordList.size()>0)
		   _colscount=wordList.size();
		float x=20;
		float y=17;		
		svgos.append("<g id=\"g000000000000000000\">");
		svgos.append("<g id=\"g000000000000000000p\" name=\"g000000000000000000p0\">");
		String url="";
	    String onclickStr="";
		if(codesetid!=null && "@K".equalsIgnoreCase(codesetid))
		{
			svgos.append("<a target=\"_blank\" xlink:href=\"/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=3\">");
			url="/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=3";
			onclickStr="onclick=\"javascript:viewmess('"+url+"');\"";
		}   
		else
		{
			svgos.append("<a  target=\"_blank\" xlink:href=\"/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=2\">");
			url="/general/inform/org/map/showorgmap.do?b_showinfo=link&amp;org_id=" + codeitemid + "&amp;infokind=2";
			onclickStr="onclick=\"javascript:viewmess('"+url+"');\"";
		}	
		svgos.append("<rect  x=\"20\" y=\"17\" width=\"" + width + "\" height=\"" + height + "\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" "+onclickStr+"/>");
		if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
		   svgos.append(createShadow(x,y,paramehashmap,codesetid).toString());
		if(ishavechilds || ishavechilds && personcount!=null && Integer.parseInt(personcount)>0 && "true".equals(isshowname))
			if("true".equals(paramehashmap.get("graphaspect").toString()))
			{
				svgos.append("<line  id=\"ll000000000000000000\" x1=\"" + line1x1 + "\" y1=\"" + (17 + height/2 - rectwidth/2) + "\" x2=\"" +line1x2 + "\" y2=\"" + (17 + height/2+rectwidth/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				svgos.append("<line  id=\"l000000000000000000\" x1=\"" + line2x1 + "\" y1=\"" + (17+ height/2) + "\" x2=\"" + line2x2 + "\" y2=\"" + (17 + height/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
			}                             
		    else
		    {
		    	svgos.append("<line  id=\"l000000000000000000\" x1=\"" +(20 + width/2) + "\" y1=\"" + (17 + height -rectwidth/2) + "\" x2=\"" +(20 + width/2) + "\" y2=\"" + (17 + height + rectwidth/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				svgos.append("<line  id=\"ll000000000000000000\" x1=\"" + (20 + width/2-rectwidth/2) + "\" y1=\"" + (17 + height) + "\" x2=\"" +(20 + width/2 + rectwidth/2) + "\" y2=\"" + (17 + height) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
		    }		
		int dx=(int)(20+width/8);//20是第一个格子和边框的距
		svgos.append("<text x=\""+dx+"\" y=\"20\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+fontsize+";\">");
		if(_colscount>1)     
		{ 
			int _cellvspacewidth=cellvspacewidth!=null&&cellvspacewidth.length()>0?Integer.parseInt(cellvspacewidth):0;
		    for(int j=0;j<wordList.size();j++)
		    {
		       //int dx= (int)(10/2*3 + _cellvspacewidth*2);
		    	//int dx= (int)(width/2+_cellvspacewidth);
		      /* if(j==_colscount)
		  	  {			  	     	    
		    	   svgos.append("<tspan x=\""+dx+"\" dy=\""+(iFize+3)+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+iFize+";\">");
		    	   svgos.append(codeitemdesc.substring((j)*_wordcount,codeitemdesc.length()));
		    	   svgos.append("</tspan>");
		   	  }else if(j==0)
		   	  {
		   		  int hh=height/(_colscount*4);
		   		  svgos.append("<tspan x=\""+dx+"\" dy=\""+hh+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+iFize+";\">");
		   		  svgos.append(codeitemdesc.substring(j*_wordcount,(j+1)*_wordcount));	
		   		  svgos.append("</tspan>");
		   	  }else
		  	  {
		   		
		   		  svgos.append("<tspan x=\""+dx+"\" dy=\""+(iFize+3)+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+iFize+";\">");
		   		  svgos.append(codeitemdesc.substring(j*_wordcount,(j+1)*_wordcount));	
		   		  svgos.append("</tspan>");
		  	  } */
		      int dy=(iFize+3);
		      if(j==0)
			    {
			    	  float fh=(height-Float.parseFloat(_colscount+"")*iFize)/2+iFize/2;   //height/(Float.parseFloat(_colscount+"")*2);
		      		  dy=Math.round(fh);
			    }
		      svgos.append("<tspan x=\""+dx+"\" dy=\""+dy+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+iFize+";\">");
	   		  //String str=codeitemdesc=changeCharset((String)wordList.get(j),"UTF-8","iso-8859-1");
		      svgos.append((String)wordList.get(j));	
	   		  svgos.append("</tspan>");
			}
		}
		else
		{
			svgos.append("<tspan x=\""+dx+"\" dy=\""+height/2+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+fontsize+";\">");
			//codeitemdesc=changeCharset(codeitemdesc,"UTF-8","iso-8859-1");
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
	        getClickRect(code, catalog_id, svgos, grade, codesetid,paramehashmap,constant);
		svgos.append("</g>");
		svgos.append("<g id=\"g000000000000000000c\" style=\"visibility:hidden\"></g>"); 
		svgos.append("<g id=\"g000000000100000000\" name=\"g000000000000000000\"></g>");
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
	private void printSVGCellRoot(String code, String catalog_id, StringBuffer sql, StringBuffer svgos,HashMap paramehashmap,boolean ishavechilds,String constant) 
	{
		int width=Integer.parseInt(paramehashmap.get("cellwidth").toString());
		int height=Integer.parseInt(paramehashmap.get("cellheight").toString());
		int rectwidth=Integer.parseInt(paramehashmap.get("rectwidth").toString());
		List rs=ExecuteSQL.executeMyQuery(sql.toString());
		String fontsize=paramehashmap.get("fontsize").toString();
		String font_family=paramehashmap.get("fontfamily").toString();
		String font_style=paramehashmap.get("font_style").toString();	
		String cellvspacewidth=paramehashmap.get("cellvspacewidth").toString();
		String isshowpersoncount=paramehashmap.get("isshowpersonconut").toString();
		String isshowname=paramehashmap.get("isshowpersonname").toString();
		String grade="0";
		String codeitemdesc=ResourceFactory.getProperty("tree.reportrelation.posdesc");
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
//			grade=(String)rec.get("grade");
			grade="0";
			codeitemdesc=ResourceFactory.getProperty("tree.reportrelation.posdesc");
			codesetid=(String)rec.get("codesetid");
			codeitemid=(String)rec.get("codeitemid");
			personcount=(String)rec.get("personcount");
//			personcount="1";
		}
		//codeitemdesc=changeCharset(codeitemdesc,"iso-8859-1","UTF-8");
		/*try {
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//int _wordcount=OneLineWordCount(width);
		//int _colscount=codeitemdesc.length()/_wordcount;
		float x=20;
		float y=17;
		svgos.append("<g id=\"g000000000000000000\">");
		svgos.append("<g id=\"g000000000000000000p\" name=\"g000000000000000000p0\">");
			
		svgos.append("<rect  x=\"20\" y=\"17\" width=\"" + width + "\" height=\"" + height + "\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
		CompTextOnecell compTextOnecell=new CompTextOnecell();	
		HashMap map=compTextOnecell.oneTableTextLineSvg(width, height, codeitemdesc, Integer.parseInt(fontsize));
		fontsize=(String)map.get("fize");
		int iFize=Integer.parseInt(fontsize);
		ArrayList wordList=(ArrayList)map.get("wordList");
		int _colscount=0;
		if(wordList!=null&&wordList.size()>0)
		   _colscount=wordList.size();
		if("true".equalsIgnoreCase(paramehashmap.get("graph3d").toString()))
		   svgos.append(createShadow(x,y,paramehashmap,codesetid).toString());
		if(ishavechilds || ishavechilds && personcount!=null && Integer.parseInt(personcount)>0 && "true".equals(isshowname))
			if("true".equals(paramehashmap.get("graphaspect").toString()))
			{
				svgos.append("<line  id=\"ll000000000000000000\" x1=\"" + line1x1 + "\" y1=\"" + (17 + height/2 - rectwidth/2) + "\" x2=\"" +line1x2 + "\" y2=\"" + (17 + height/2+rectwidth/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				svgos.append("<line  id=\"l000000000000000000\" x1=\"" + line2x1 + "\" y1=\"" + (17+ height/2) + "\" x2=\"" + line2x2 + "\" y2=\"" + (17 + height/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
			}
		    else
		    {
		    	svgos.append("<line  id=\"l000000000000000000\" x1=\"" +(20 + width/2) + "\" y1=\"" + (17 + height -rectwidth/2) + "\" x2=\"" +(20 + width/2) + "\" y2=\"" + (17 + height + rectwidth/2) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
				svgos.append("<line  id=\"ll000000000000000000\" x1=\"" + (20 + width/2-rectwidth/2) + "\" y1=\"" + (17 + height) + "\" x2=\"" +(20 + width/2 + rectwidth/2) + "\" y2=\"" + (17 + height) + "\" style=\"stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\"/>");
		    }		
		svgos.append("<text x=\"30\" y=\"20\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+fontsize+";\">");
		if(_colscount>1)     
		{ 
			int _cellvspacewidth=cellvspacewidth!=null&&cellvspacewidth.length()>0?Integer.parseInt(cellvspacewidth):0;
		    for(int j=0;j<wordList.size();j++)
		    {
		    	//int dx= (int)(10/2*3 + _cellvspacewidth*2);	
		    	int dx=(int)(20+width/4);//20是第一个格子和边框的距离
				/*if(j==_colscount)
		      	{
					svgos.append(" <tspan x=\""+dx+"\" dy=\""+(iFize+3)+"\">");
					svgos.append(codeitemdesc.substring((j)*_wordcount,codeitemdesc.length()));
					svgos.append("</tspan>");
		      	}else if(j==0)
		      	{
		      		int hh=height/(_colscount*4);
		      		svgos.append(" <tspan x=\""+dx+"\" dy=\""+hh+"\">");
		      		svgos.append(codeitemdesc.substring(j*_wordcount,(j+1)*_wordcount));
					svgos.append("</tspan>");
		      	}else
		      	{
		      		svgos.append(" <tspan x=\""+dx+"\" dy=\""+(iFize+3)+"\">");
		      		svgos.append(codeitemdesc.substring(j*_wordcount,(j+1)*_wordcount));
		      		svgos.append("</tspan>");
		      	}*/
		    	int dy=(iFize+3);
		    	if(j==0)
			    {
			    	  float fh= (height-Float.parseFloat(_colscount+"")*iFize)/2+iFize/2;//height/(Float.parseFloat(_colscount+"")*2);
		      		  dy=Math.round(fh);
			    }
		    	svgos.append(" <tspan x=\""+dx+"\" dy=\""+dy+"\">");
	      		svgos.append(wordList.get(j));
	      		svgos.append("</tspan>");
			}
		}
		else
		{
			svgos.append("<tspan x=\"32\" dy=\""+(int)(height/2)+"\" style=\"fill:" + getFontColor(paramehashmap.get("fontcolor").toString(),codesetid) + ";font-weight:bold;font-size:"+fontsize+";\">");
			//codeitemdesc=changeCharset(codeitemdesc,"UTF-8","iso-8859-1");
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
//		svgos.append("</a>");
		if(ishavechilds || ishavechilds && personcount!=null && Integer.parseInt(personcount)>0 && "true".equals(isshowname))
	        getClickRect(code, catalog_id, svgos, grade, codesetid,paramehashmap,constant);
		svgos.append("</g>");
		svgos.append("<g id=\"g000000000000000000c\" style=\"visibility:hidden\"></g>"); 
		svgos.append("<g id=\"g000000000100000000\" name=\"g000000000000000000\"></g>");
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
	private void getClickRect(String code, String catalog_id, StringBuffer svgos, String grade, String codesetid,HashMap paramehashmap,String constant) {
//		System.out.println(grade);
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
					svgos.append("<rect  x=\"" +rectx + "\" y=\"" + (17 + height/2 -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\"  style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'" + catalog_id + "','n',true)\" onclick=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'" + catalog_id + "','n',true)\"/>");
			    }
				else
				{
					svgos.append("<rect  x=\"" + (20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2-Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" y=\"" + (17 + height -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\"  style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'" + catalog_id + "','" + dbname + "',false)\" onclick=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'" + catalog_id + "','" + dbname + "',false)\"/>");
				}
			}
			else
			{
				
				if("true".equals(paramehashmap.get("graphaspect")))                                                                                                                                                                                                                                                                                                                                                                                                                   
				    svgos.append("<rect  x=\"" +rectx + "\" y=\"" + (17 + height/2 -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'g000000000000000000','','UN',0,'" + catalog_id + "','" + dbname  + "',true)\" onclick=\"loadChildNode(evt,'g000000000000000000','','UN',0,'" + catalog_id + "','" + dbname  + "',true)\"/>");
			    else
			    	svgos.append("<rect  x=\"" + (20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2-Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" y=\"" + (17 + height -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'g000000000000000000','','UN',0,'" + catalog_id + "','" + dbname + "',false)\" onclick=\"loadChildNode(evt,'g000000000000000000','','UN',0,'" + catalog_id + "','" + dbname + "',false)\"/>");    	
			}
		 }else
		 {
		 	if(code!=null && code.length()>0)
			{
		 		if("true".equals(paramehashmap.get("graphaspect")))
		 			svgos.append("<rect  x=\"" +rectx + "\" y=\"" + (17 + height/2 -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onclick=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'','"+constant+"','" + dbname  + "',true)\" onload=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'','"+constant+"','" + dbname  + "',true)\"/>");
		 		else
		 			svgos.append("<rect  x=\"" + (20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2-Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" y=\"" + (17 + height -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\"  height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'','"+constant+"','" + dbname  + "',false)\" onclick=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'','"+constant+"','" + dbname  + "',false)\"/>");
		    }else
		    {
		 		if("true".equals(paramehashmap.get("graphaspect")))
		 			svgos.append("<rect  x=\"" +rectx + "\" y=\"" + (17 + height/2 -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onclick=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'','"+constant+"','" + dbname  + "',true)\" onload=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'','"+constant+"','" + dbname  + "',true)\"/>");
		 		else
		 			svgos.append("<rect  x=\"" + (20 + Integer.parseInt(paramehashmap.get("cellwidth").toString())/2-Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" y=\"" + (17 + height -Integer.parseInt(paramehashmap.get("rectwidth").toString())/2) + "\" width=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\"  height=\"" + Integer.parseInt(paramehashmap.get("rectwidth").toString()) + "\" fill-opacity=\"0.5\" style=\"fill:" + getCellColor(paramehashmap.get("cellcolor").toString(),codesetid) + ";stroke:#000000;stroke-width:" + paramehashmap.get("celllinestrokewidth").toString() + "\" onload=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'','"+constant+"','" + dbname  + "',false)\" onclick=\"loadChildNode(evt,'g000000000000000000','" + code + "','" + codesetid + "'," + grade + ",'','"+constant+"','" + dbname  + "',false)\"/>");
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
 public String changeCharset(String str,String oldCharset, String newCharset)
 {
     if (str != null)
     { /* 用默认字符编码解码字符串。*/
       
         try {
        	byte[] bs = str.getBytes(oldCharset); /* 用新的字符编码生成字符串*/
			return new String(bs, newCharset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
     return "";
}
 
 private String getUnitAndDept(String posId){
		
		String codeitemid = "";
		String codesetid = "";
		String codeitemdesc = "";
		String sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid = (select e0122 from k01 where e01a1='"+posId+"')";
			List rs = ExecuteSQL.executeMyQuery(sql);
			if(rs.size()>0){
				LazyDynaBean ldb=(LazyDynaBean)rs.get(0);
				codeitemid = (String)ldb.get("codeitemid");
				codesetid = (String)ldb.get("codesetid");
				codeitemdesc = (String)ldb.get("codeitemdesc");
				
				if("UM".equals(codesetid)){
					sql = "select codeitemdesc from organization where codesetid='UN' and  codeitemid ="+Sql_switcher.substr("'"+codeitemid+"'", "1", Sql_switcher.length("codeitemid"))+"order by codeitemid desc";
					rs = ExecuteSQL.executeMyQuery(sql);
					if(rs.size()>0)
						codeitemdesc = ((LazyDynaBean)rs.get(0)).get("codeitemdesc")+"/"+codeitemdesc;
				}
			}
		return codeitemdesc;
	}

}

