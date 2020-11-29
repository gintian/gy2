package com.hjsj.hrms.taglib.general.template;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Assign_Template_SortTag extends BodyTagSupport {
	private String type;//11统计表;10登记表;15高级花名册	
	private String res_flag;
	private String flag;
	private String roleid;
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
	public String getRes_flag() {
		return res_flag;
	}
	public void setRes_flag(String res_flag) {
		this.res_flag = res_flag;
	}	
	private String str_content="";
	public int doEndTag() throws JspException 
	{
		JspWriter out=pageContext.getOut();
		UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		Connection conn = null;	
		RowSet rset=null;
		try {
			String rootdesc=ResourceFactory.getProperty("label.bos.rsbd");
			if("2".equals(type))
			{
				rootdesc=ResourceFactory.getProperty("sys.res.gzbd");
			}
			else if("8".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.ins_bd");
			}
			else if("10".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.card");
			}
			else if("11".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.tjb");
			}
			else if("14".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.muster");
			}
			else if("15".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.hmuster");
			}
			else if("B".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.org");
			}
			else if("K".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.pos");
			}
			else if("22".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.khmodule");
			}
			else if("23".equals(type)){
				rootdesc=ResourceFactory.getProperty("sys.res.khfield");
			}
			if("22".equals(type)|| "23".equals(type)){
				out.append("<TABLE cellSpacing=0 cellPadding=0 width=65% style='font-size: 12px' bgcolor=\"#FFFFFF\"  class=\"RecordRow\">");
				out.append("<TR>"); 
				out.append("<TD style=\"line-height:16px\" width=76%></TD>");
				out.append("<TD style=\"line-height:16px\" width=8%></TD>");
				out.append("<TD style=\"line-height:16px\" width=8%></TD>");
				out.append("<TD style=\"line-height:16px\" width=8%></TD>");    
				
				out.append("</TR>");
				out.append("<TR>");
				out.append("<TD style=\"line-height:22px\" width=\"76%\" height=22 class=\"TopRow\" >");
				//out.append("<IMG src=\"/images/tree_collapse.gif\" border=no>");							//资源分配考核指标,图标位置和间距  jingq  upd 2014.08.18
				out.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-minus-nl.gif\" border=no align=\"absmiddle\" style=\"margin:5 10 5 2\">");			
				
				out.append("<IMG src=\"/ext/resources/images/default/tree/folder-open.gif\" border=no align=\"absmiddle\" style=\"margin-right:8\">");			
				
	            out.append("&nbsp;"+rootdesc+"</TD>"); 
	            out.append("<TD style=\"line-height:22px\" width=8% align=\"center\" nowrap class=\"TopRow\">制作</TD>");
	            out.append("<TD style=\"line-height:22px\" width=8% align=\"center\" nowrap class=\"TopRow\">使用</TD>");
	            out.append("<TD style=\"line-height:22px\" width=8% align=\"center\" nowrap class=\"TopRow\">无</TD>");
				out.append("</TR>");
			}
			else{
				out.append("<TABLE cellSpacing=0 cols=5 cellPadding=0 width=570 style='font-size: 12px' bgcolor=\"#FFFFFF\"  class=\"RecordRow\">");
				out.append("<TR>"); 
				out.append("<TD style=\"line-height:16px\" width=16></TD>");
				out.append("<TD style=\"line-height:16px\" width=16></TD>");
				out.append("<TD style=\"line-height:16px\" width=15></TD>");
				out.append("<TD style=\"line-height:16px\" width=432></TD>");    
				out.append("<TD style=\"line-height:16px\" width=30></TD>");   
				out.append("<TD style=\"line-height:16px\" width=30></TD>");  
				out.append("<TD style=\"line-height:16px\" width=15></TD>");   
				out.append("</TR>");
				out.append("<TR>");
				out.append("<TD style=\"line-height:22px\" width=\"16\" height=22 class=\"TopRow\" >");
				//out.append("<IMG src=\"/images/tree_collapse.gif\" border=no>");
				out.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-minus-nl.gif\" border=no>");			
				out.append("</TD>");
				out.append("<TD style=\"line-height:22px\" width=\"16\" height=22 class=\"TopRow\" >");
				out.append("<IMG src=\"/ext/resources/images/default/tree/folder-open.gif\" border=no>");			
				out.append("</TD>");
	            out.append("<TD style=\"line-height:30px\" colSpan=2 nowrap class=\"TopRow\">&nbsp;"+rootdesc+"　　　　　　　　　　</TD>"); 
	            out.append("<TD style=\"line-height:30px\" width=30 nowrap class=\"TopRow\">制作</TD>");
	            out.append("<TD style=\"line-height:30px\" width=35 nowrap class=\"TopRow\">使用</TD>");
	            out.append("<TD style=\"line-height:30px\" width=25 nowrap class=\"TopRow\">无</TD>");
				out.append("</TR>");
			}
			conn = (Connection) AdminDb.getConnection();
			this.str_content=getContent(conn);
			if("15".equals(type)|| "11".equals(type)|| "10".equals(type))
			{
				loadSortNodes(conn,userView,out);
			}else if("22".equals(type)) {    //22 考核模板
				assessloadSortNodes(conn,userView,out,"培训模块","20",1);
				assessloadSortNodes(conn,userView,out,"绩效模块","33",2);
				assessloadSortNodes(conn,userView,out,"能力素质模板","35",3);
			}else if("23".equals(type)) {    //23 考核指标
				assessloadSortNodes(conn,userView,out,"培训指标","20",1);
				assessloadSortNodes(conn,userView,out,"绩效指标","33",2);
				assessloadSortNodes(conn,userView,out,"能力素质指标","35",3);
			}
			else
			{
				if("B".equals(type))
					type="10";
				if("K".equals(type))
					type="11";
				loadAllNodes(conn,userView,out);
			}
			
			out.append("</table>");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			try
			{
				if(rset!=null)
					rset.close();
				if(conn!=null&&(!conn.isClosed()))
					conn.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return SKIP_BODY;
	}
	private void loadAllNodes(Connection conn,UserView userView,JspWriter out)
	{
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		String unit_type=null;
		StringBuffer strsql=new StringBuffer();
		unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
		if(unit_type==null|| "".equals(unit_type))
			unit_type="3";			
		String operationcode=sysbo.getValue(Sys_Oth_Parameter.GOBROAD,"operationcode");

        Element root = new Element("TreeNode");
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","root");
        Document myDocument = new Document(root);
        String _static="static";
        if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
        	_static="static_o";
        }
		strsql.append("select distinct a.operationcode,b.operationname ,operationid from ");
		strsql.append("template_table a ,operation b where a.operationcode=b.operationcode and b."+_static+"=");
		strsql.append(type);
		strsql.append(" and (");			
		String units[]=unit_type.split(",");
		for(int i=0;i<units.length;i++)
		{
			strsql.append("a.flag ="+Integer.parseInt(units[i]));
			if(i<units.length-1)
				strsql.append(" or ");
		}			
		strsql.append(")");
		strsql.append(" order by operationid");
		ContentDAO dao=new ContentDAO(conn);
		try {
			int i=0;
    		int n=1;
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
			{
				i++;
				out.append("<TR>");
				out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
				/*out.append("<img src=\"/images/");
				if (i==count)
				   out.append("tree_end.gif");
				else
					out.append("tree_split.gif");
				out.append("\" border=no width=\"16\" height=\"16\">");*/
				out.append("&nbsp;");
				out.append("</TD>");					
				out.append("<TD style=\"line-height:16px\" width=\"16\">");
			    out.append("<SPAN id=zhugan"+n+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtree"+n+",document.all.img"+n+");\">");
			    out.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=img"+n+">");
			    out.append("</SPAN></TD>");
			    out.append("<TD style=\"line-height:16px\" colSpan=4 nowrap><A href=\"javascript:void(0)\">"+rset.getString("operationname")+"</A></TD>");
			    out.append("<TD style=\"line-height:16px\" width=\"23\">");
			    out.append("</TD>");
			    out.append("</TR>");
			    out.append("<TR id=\"subtree"+n+"\" style=\"display:none\"><TD style=\"line-height:16px\" colspan=7>");
			    out.append("<TABLE cellSpacing=0 cols=8 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
			    out.append("<tbody>");
			    //孩子
			   // getSortTemplates(sortid,"#",res_flag,userView,i,count,out,conn);
			    getTemplates(type,rset.getString("operationcode"),res_flag,unit_type,userView,i, out,conn);
			    out.append("</tbody>");
			    out.append("</TABLE></TD>");
			    out.append("</TR>"); 
			    n++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private boolean getTemplates(String type,String module,String res_flag,String unit_type,UserView userView,int i,JspWriter out,Connection conn)
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,name from template_table where operationcode='");
		strsql.append(module);
		strsql.append("' and (");
		String units[]=unit_type.split(",");
		for(int r=0;r<units.length;r++)
		{
			strsql.append(" flag ="+Integer.parseInt(units[r]));
			if(r<units.length-1)
				strsql.append(" or ");
		}			
		strsql.append(")");
		RowSet rset=null;
		boolean bhave=false;
		try
		{
			ContentDAO dao=new ContentDAO(conn);			
			rset=dao.search(strsql.toString());	
			int j=0;
			while(rset.next())
			{			
				if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
					continue;	
				/*Element child = new Element("TreeNode");    		
				child.setAttribute("id", rset.getString("tabid"));
	            child.setAttribute("text", rset.getString("tabid")+":"+rset.getString("name"));
	            child.setAttribute("title", rset.getString("name"));
	            child.setAttribute("target", "mil_body");	

				if(!bflag)//加上超链接
				{
					child.setAttribute("href", "/general/template/edit_form.do?b_query=link&returnflag=3&sp_flag=1&ins_id=0&tabid="+rset.getString("tabid"));
					if(isHaveMsg(userView,rset.getString("tabid"),conn))
						child.setAttribute("icon","/images/overview_n_obj.gif");
					else
						child.setAttribute("icon","/images/overview_obj.gif");					
				}
				else
					child.setAttribute("icon","/images/overview_obj.gif");					
				parent.addContent(child);*/
				j++;			
	            out.println("<TR onMouseOver=\"javascript:tr_onclick(this,'')\">"); 
	            out.println("<TD style=\"line-height:16px\" width=\"16\"> ");
	           /* if(i==per_count)
	            {
	            	out.println("&nbsp;");
	            }
                else
                	out.println("<img src=\"/images/tree_vertline.gif\" border=0>");*/
	            out.append("&nbsp;");
	            out.println("</TD>");	            
	            out.println("<TD style=\"line-height:16px\" width=\"16\">");
	            /*out.println("<IMG src=\"/images/");
	            if (j==count)
			       out.println("tree_end.gif");
			    else
			    	out.println("tree_split.gif");
	            out.println("\">");*/
	            out.append("&nbsp;");
	            out.println("</TD>");
	            out.println("<TD style=\"line-height:16px\" width=\"15\" align=\"left\"><img src=\"/images/overview_obj.gif\" border=0 width=\"16\" height=\"16\"></TD>");
	            out.println("<TD style=\"line-height:16px\" width=\"400\"  height=\"20\">");
	            out.println("<A href=\"javascript:void(0);\" onclick=\"javascript:void(0);\">"+rset.getString("tabid")+":"+rset.getString("name")+"</a></TD>");
	            out.println("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");	           
	            //制作
	            boolean ischeck=false;
	            if(this.str_content!=null&&this.str_content.length()>0)
	            {
	            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+",")!=-1)
	            	{
	            		out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\" checked>");
	            		ischeck=true;
	            	}
	            	else
	            		out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\">");
	            }else
	            {	            	
	            	out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\">");
	            }
	            
	            out.println("</TD>");
	            out.println("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
	            //使用
	            if(this.str_content!=null&&this.str_content.length()>0)
	            {
	            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+"R,")!=-1)
	            	{
	            		out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\" checked>");
	            		ischeck=true;
	            	}
	            	else
	            		out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\">");
	            }else
	            {	
	               out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\">");
	            }
	            
	            out.println("</TD>");
	            out.println("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
	            if(!ischeck)
	            {
	            	out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\"\" checked>");
	            }else
	            	out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\"\">");
	            out.println("</TD>");
	            out.println("</TR>");   
				bhave=true;
			}
		}
		catch(Exception ex)
		{
			
		}
		return bhave;
	}
	private void assessloadSortNodes(Connection conn, UserView userView,JspWriter out, String name,String template_setid,int n) {
		ContentDAO dao=new ContentDAO(conn);
		try {
		    String childNodeHtml = assessgetSortTemplates(template_setid,"#",res_flag,userView,n,conn,"");
		    if(!"".equals(childNodeHtml)) {
		        StringBuffer nodesHtml = new StringBuffer();
                nodesHtml.append("<TR>");
                nodesHtml.append("<td align=\"left\" width=\"76%\">");
                nodesHtml.append("<SPAN id=zhugan"+n+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtreetr"+n+",document.all.imgtr"+n+");\">");
                nodesHtml.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=imgtr"+n+" align=\"absmiddle\" style=\"margin:2 8 2 2\">");
                nodesHtml.append("</SPAN>");
                nodesHtml.append("<A href=\"javascript:void(0)\">"+name+"</A></TD>");
                nodesHtml.append("<TD style=\"line-height:16px\" width=\"8%\">");
                nodesHtml.append("</TD>");
                nodesHtml.append("<TD style=\"line-height:16px\" width=\"8%\">");
                nodesHtml.append("</TD>");
                nodesHtml.append("<TD style=\"line-height:16px\" width=\"8%\">");
                nodesHtml.append("</TD>");
                nodesHtml.append("</TR>");
                nodesHtml.append("<TR id=\"subtreetr"+n+"\" style=\"display:none\"><TD style=\"line-height:16px\" width=\"100%\" colspan=\"4\" style=\"padding-left:10\">");
                nodesHtml.append("<TABLE cellSpacing=0 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
                nodesHtml.append("<tbody>");
                //孩子
                nodesHtml.append(childNodeHtml);
                nodesHtml.append("</tbody>");
                nodesHtml.append("</TABLE></TD>");
                nodesHtml.append("</TR>");
                n++;
                
                out.append(nodesHtml.toString());
            }
           
        }catch(Exception e)
        {
        	
        }
	}
	private void loadSortNodes(Connection conn,UserView userView,JspWriter out)
	{
		ContentDAO dao=new ContentDAO(conn);
		StringBuffer strsql=new StringBuffer();
		if("10".equalsIgnoreCase(type)){
        	strsql.append("select sortid,sortname from RSort");
        }else if("11".equalsIgnoreCase(type)){
        	strsql.append("select TSortId sortid,Name sortname from TSort");
        }else if("14".equalsIgnoreCase(type)){
        	strsql.append("select styleid sortid,styledesc sortname from lstyle");
        }else if("15".equalsIgnoreCase(type)){
        	strsql.append("select * from muster_sort");
        }
		 /*存放所有的sortid*/
        ArrayList list = new ArrayList();
        try
        {
        	RowSet rset=dao.search(strsql.toString());
    		while(rset.next())
    		{
    			String sortid = rset.getString("sortid");
    			list.add(sortid);
    		}
    		/**业务分类*/
    		int i=0;
    		int n=1;
    		int count=list.size();
    		ArrayList nmodulelist = new ArrayList();
    		ArrayList notSortList=new ArrayList();
    		/**业务分类*/
    		StringBuffer childUN = new StringBuffer();//单位
    		int countUN=0;
    		int iCN=0;
    		StringBuffer childUM = new StringBuffer();;//职位
    		int iCM=0;
    		int countUM=0;
    		StringBuffer childK = new StringBuffer();;//人员
    		int countK=0;
    		int iK=0;
    		StringBuffer childGZFX = new StringBuffer();;//工资分析名册
    		int countGZFX=0;
    		int iGZFX=0;
    		StringBuffer childGZDY = new StringBuffer();;//工资自定义报表
    		int countGZDY=0;
    		int iGZDY=0;
    		StringBuffer childGZTZ = new StringBuffer();;//工资台帐报表
    		int countGZTZ=0;
    		int iGZTZ=0;
    		StringBuffer childBXFX = new StringBuffer();;//保险分析花名册
    		int countBXFX=0;
    		int iBXFX=0;
    		StringBuffer childBXZDY = new StringBuffer();;//保险自定义花名册
    		int countBXZDY=0;
    		int iBXZDY=0;
    		StringBuffer childBXTZ = new StringBuffer();;//保险台帐
    		int countBXTZ=0;
    		int iBXTZ=0;
    		StringBuffer childGRSDS = new StringBuffer();;//个人所得税花名册
    		int countGRSDS=0;
    		int iGRSDS=0;
    		StringBuffer childKQ = new StringBuffer();;//考勤花名册
    		int countKQ=0;
    		int iKQ=0;
    		StringBuffer childPX = new StringBuffer();;//培训花名册
    		int countPX=0;
    		int iPX=0;
    		StringBuffer childRSYD=new StringBuffer();//人事异动模板
    		int countRSYD=0;
    		int iRSYD=0;
    	    int countHTTZ=0;//2;合同台帐
    	    int iHTTZ=0;
    	    StringBuffer childHK = new StringBuffer();//基准岗位花名册  guodd 2015-11-10
    	    int countHK = 0;
    	    int iHK = 0;
    	    StringBuffer childHTTZ=new StringBuffer();//人事异动模板
    		//查看是否有未分类的	
    		if("15".equalsIgnoreCase(type)){
    			//count=12;
    			countUN=getSortMusterCount("21",conn);
    			countUM=getSortMusterCount("41",conn);
    			countK=getSortMusterCount("3",conn);
    			countGZFX=getSortMusterCount("6",conn);
    			countBXFX=getSortMusterCount("8",conn);
    			countGRSDS=getSortMusterCount("15",conn);
    			countPX=getSortMusterCount("61",conn);
    			countKQ=getSortMusterCount("81",conn);
    			countBXTZ=getSortMusterCount("1",conn);
    			countGZTZ=getSortMusterCount("4",conn);
    			countRSYD=getSortMusterCount("5",conn);
    			countHTTZ=getSortMusterCount("2",conn);
    			countHK = getSortMusterCount("51",conn);//添加基准岗位分类 guodd 2015-11-10
    			countGZDY=getCount("select count(*) aa from salarytemplate where (cstate is null or cstate='')",conn);
    			countBXZDY=getCount("select count(*) aa from salarytemplate where cstate='1'",conn);
    		}else
    		{
    			notSortList=getNotSortTemplates(list,res_flag,userView,conn);
    			if(notSortList!=null&&notSortList.size()>0)
    			   count++;
    		}
    		
    		rset=dao.search(strsql.toString());			
    		/**
			 * xus 17/02/23
			 * 同一个薪资类别下的子节点放一起
			 */
    		StringBuffer strGZDYTemp=new StringBuffer();
    		Map tabidMap = new HashMap();
    		int lastTbody=0;
    		while(rset.next())
    		{
    			String sortid = rset.getString("sortid");
    			i++;
    			if("15".equalsIgnoreCase(type))
    			{
    				RowSet rsetm=null;
    				String nmodule = rset.getString("nmodule");
    				String sortname=rset.getString("sortname");
    				nmodulelist.add(nmodule);
    				if("21".equalsIgnoreCase(nmodule)){
    					iCN++;						
    					childUN.append(getMusterTStr(rset.getString("sortname"),iCN,countUN,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("41".equalsIgnoreCase(nmodule)){//职位
    					
    					iCM++;
    					childUM.append(getMusterTStr(rset.getString("sortname"),iCM,countUM,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("3".equalsIgnoreCase(nmodule)){//人员
    					iK++;
    					childK.append(getMusterTStr(rset.getString("sortname"),iK,countK,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("6".equalsIgnoreCase(nmodule)){
    					iGZFX++;
    					childGZFX.append(getMusterTStr(rset.getString("sortname"),iGZFX,countGZFX,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("14".equalsIgnoreCase(nmodule)){//工资自定义报表
    				    //zxj 20170624 工资自定义表按薪资类别分类显示，不按自定义花名册分类，所以一次即可取完，后续不需要再取
    				    if(strGZDYTemp.length() > 0)
    				        continue;
    				    
    					rsetm = dao.search("select salaryid tabid,cname sortname from salarytemplate where (cstate is null or cstate='')");
    					while(rsetm.next()){
    						iGZDY++;							
    						if (!userView.isHaveResource(IResourceConstant.GZ_SET, rsetm.getString("tabid")))
    							continue;	
    						StringBuffer strGZDY=new StringBuffer();
    						//xus  17/02/23 同一个薪资类别下的子节点放一起
    						if(!tabidMap.containsKey(rsetm.getString("tabid"))){
    						strGZDY.append("<TR>");
    						strGZDY.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
    						//strGZDY.append("<img src=\"/images/tree_vertline.gif\" border=no width=\"16\" height=\"16\">");
    						strGZDY.append("&nbsp;");
    						strGZDY.append("</TD>");
    						strGZDY.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
    						/*strGZDY.append("<img src=\"/images/");
    						if (i==countGZDY)
    							strGZDY.append("tree_end.gif");
    						else
    							strGZDY.append("tree_split.gif");
    						strGZDY.append("\" border=no width=\"16\" height=\"16\">");*/
    						strGZDY.append("&nbsp;");
    						strGZDY.append("</TD>");
    						strGZDY.append("<TD style=\"line-height:16px\" width=\"16\">");
    						strGZDY.append("<SPAN id=zhugancn"+n+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtreecn"+n+",document.all.imgcn"+n+");\">");
    						strGZDY.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=imgcn"+n+">");
    						strGZDY.append("</SPAN></TD>");
    						strGZDY.append("<TD style=\"line-height:16px\" colSpan=2 width='499'><A href=\"javascript:void(0)\">"+rsetm.getString("sortname")+"</A></TD>");
    						strGZDY.append("<TD style=\"line-height:16px\" width=\"23\">");
    						strGZDY.append("</TD>");
    						strGZDY.append("</TR>");
    						}
    						strGZDY.append("<TR id=\"subtreecn"+n+"\"  style=\"display:none\"><TD style=\"line-height:16px\" colspan=9>");
    						strGZDY.append("<TABLE cellSpacing=0 cols=7 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
    						strGZDY.append("<tbody>");
    					    //孩子
    						if(getSortMusterTemplates(sortid,rsetm.getString("tabid"),nmodule,res_flag,userView,iGZDY,countGZDY,conn,strGZDY))
    						{
    							strGZDY.append("</tbody>");
    							strGZDY.append("</TABLE></TD>");
    							strGZDY.append("</TR>");
    							//xus  17/02/23 同一个薪资类别下的子节点放一起
    							strGZDYTemp.append(strGZDY.toString());
    							if(tabidMap.containsKey(rsetm.getString("tabid"))){
    								strGZDYTemp.delete(lastTbody, strGZDYTemp.lastIndexOf("<tbody>")+7);
    							}
    							lastTbody=strGZDYTemp.lastIndexOf("</tbody></TABLE></TD></TR>");
    							tabidMap.put(rsetm.getString("tabid"), n);
    							//childGZDY.append(strGZDY.toString());
    							n++;
    						}	
    					}
    				}else if("8".equalsIgnoreCase(nmodule)){
    					iBXFX++;
    					childBXFX.append(getMusterTStr(rset.getString("sortname"),iBXFX,countBXFX,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("11".equalsIgnoreCase(nmodule)){//保险自定义花名册
    				    //zxj 20170624 保险自定义表按薪资类别分类显示，不按自定义花名册分类，所以一次即可取完，后续不需要再取
                        if(childBXZDY.length() > 0)
                            continue;
                        
    					rsetm = dao.search("select salaryid tabid,cname sortname from salarytemplate where cstate='1'");
    					while(rsetm.next()){
    						iBXZDY++;
    						if (!userView.isHaveResource(IResourceConstant.INS_SET, rsetm.getString("tabid")))
    							continue;								
    						StringBuffer strBXFX=new StringBuffer();
    						strBXFX.append("<TR>");
    						strBXFX.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
    						//strBXFX.append("<img src=\"/images/tree_vertline.gif\" border=no width=\"16\" height=\"16\">");
    						strBXFX.append("&nbsp;");
    						strBXFX.append("</TD>");
    						strBXFX.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
    						/*strBXFX.append("<img src=\"/images/");
    						if (iBXFX==countBXFX)
    							strBXFX.append("tree_end.gif");
    						else
    							strBXFX.append("tree_split.gif");
    						strBXFX.append("\" border=no width=\"16\" height=\"16\">");*/
    						strBXFX.append("&nbsp;");
    						strBXFX.append("</TD>");
    						strBXFX.append("<TD style=\"line-height:16px\" width=\"16\">");
    						strBXFX.append("<SPAN id=zhugancn"+n+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtreecn"+n+",document.all.imgcn"+n+");\">");
    						strBXFX.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=imgcn"+n+">");
    						strBXFX.append("</SPAN></TD>");
    						strBXFX.append("<TD style=\"line-height:16px\" colSpan=2 width='499'><A href=\"javascript:void(0)\">"+rsetm.getString("sortname")+"</A></TD>");
    						strBXFX.append("<TD style=\"line-height:16px\" width=\"23\">");
    						strBXFX.append("</TD>");
    						strBXFX.append("</TR>");
    						strBXFX.append("<TR id=\"subtreecn"+n+"\"  style=\"display:none\"><TD style=\"line-height:16px\" colspan=9>");
    						strBXFX.append("<TABLE cellSpacing=0 cols=7 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
    						strBXFX.append("<tbody>");
    					    //孩子
    						if(getSortMusterTemplates(sortid,rsetm.getString("tabid"),nmodule,res_flag,userView,iBXZDY,countBXFX,conn,strBXFX))
    						{
    							strBXFX.append("</tbody>");
    							strBXFX.append("</TABLE></TD>");
    							strBXFX.append("</TR>");
    							childBXZDY.append(strBXFX.toString());
    							n++;
    						}	
    			             
    					}
    				}else if("15".equalsIgnoreCase(nmodule)){//个人所得税花名册
    				
    					iGRSDS++;
    					childGRSDS.append(getMusterTStr(rset.getString("sortname"),iGRSDS,countGRSDS,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("61".equalsIgnoreCase(nmodule)){//培训花名册
    					iPX++;
    					childPX.append(getMusterTStr(rset.getString("sortname"),iPX,countPX,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("81".equalsIgnoreCase(nmodule)){//考勤花名册
    					iKQ++;
    					childKQ.append(getMusterTStr(rset.getString("sortname"),iKQ,countKQ,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("1".equalsIgnoreCase(nmodule)){//保险台帐
    					iBXTZ++;
    					//childBXTZ.append(getMusterTStr(rset.getString("sortname"),iBXTZ,countBXTZ,n,sortid,nmodule,res_flag,userView,conn));
    		        
    				}else if("4".equalsIgnoreCase(nmodule)){//工资台帐
    					iGZTZ++;
    					childGZTZ.append(getMusterTStr(rset.getString("sortname"),iGZTZ,countGZTZ,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("5".equalsIgnoreCase(nmodule))//人事异动
    				{
    					iRSYD++;
    					childRSYD.append(getMusterTStr(rset.getString("sortname"),iRSYD,countRSYD,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("2".equalsIgnoreCase(nmodule))//合同台账
    				{
    					iHTTZ++;
    		    	    childHTTZ.append(getMusterTStr(rset.getString("sortname"),iRSYD,countRSYD,n,sortid,nmodule,res_flag,userView,conn));
    				}else if("51".equalsIgnoreCase(nmodule)){//添加基准岗位分类 guodd 2015-11-10
    					iHK++;
    					childHK.append(getMusterTStr(rset.getString("sortname"),iHK,countHK,n,sortid,nmodule,res_flag,userView,conn));
    				}
    			}else
    			{
    				out.append("<TR>");
    				out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
    				/*out.append("<img src=\"/images/");
    				if (i==count)
    				   out.append("tree_end.gif");
    				else
    					out.append("tree_split.gif");
    				out.append("\" border=no width=\"16\" height=\"16\">");*/
    				out.append("&nbsp;");
    				out.append("</TD>");					
    				out.append("<TD style=\"line-height:16px\" width=\"16\">");
    			    out.append("<SPAN id=zhugan"+n+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtree"+n+",document.all.img"+n+");\">");
    			    out.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=img"+n+">");
    			    out.append("</SPAN></TD>");
    			    out.append("<TD style=\"line-height:16px\" colSpan=4 nowrap><A href=\"javascript:void(0)\">"+rset.getString("sortname")+"</A></TD>");
    			    out.append("<TD style=\"line-height:16px\" width=\"23\">");
    			    out.append("</TD>");
    			    out.append("</TR>");
    			    out.append("<TR id=\"subtree"+n+"\" style=\"display:none\"><TD style=\"line-height:16px\" colspan=7>");
    			    out.append("<TABLE cellSpacing=0 cols=8 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
    			    out.append("<tbody>");
    			    //孩子
    			    getSortTemplates(sortid,"#",res_flag,userView,i,count,out,conn);
    			    out.append("</tbody>");
    			    out.append("</TABLE></TD>");
    			    out.append("</TR>");   
    			}
    			n++;
    			//break;
    		}
    		//xus 17/02/24 在这加入
    		childGZDY.append(strGZDYTemp.toString());
    		if("15".equalsIgnoreCase(type)){
    			n=1;
    			String sortname="单位";
    			String str="";
    			for(int s=1;s<16;s++)
    			{
    				str="";
    				switch (s) {
    				   case 1: {
    					   sortname="单位";
    					   n=1;
    					   childUN.append(addUnSortMusterNode(notSortList,res_flag,"21",userView,conn,n+12));
    					   str=childUN.toString();						   
    					   break;
    				   }
    				   case 2: {
    					   sortname="岗位";
    					   n=2;
    					   childUM.append(addUnSortMusterNode(notSortList,res_flag,"41",userView,conn,n+12));
    					   str=childUM.toString();//职位		
    					   break;
    				   }
    				   case 3:{//添加基准岗位分类 guodd 2015-11-10
    					   sortname="基准岗位";	
    					   n=3;
    					   childHK.append(addUnSortMusterNode(notSortList,res_flag,"51",userView,conn,n+12));
    					   str=childHK.toString();
    					   break;
    				   }
    				   case 4: {
    					   sortname="人员";						   
    					   n=4;
    					   childK.append(addUnSortMusterNode(notSortList,res_flag,"3",userView,conn,n+12));
    					   str=childK.toString();;//人员
    					   break;
    				   }
    				   case 5: {
    			           n=5;
    			           sortname="薪资分析名册";	
    			           childGZFX.append(addUnSortMusterNode(notSortList,res_flag,"6",userView,conn,n+12));
    			           str=childGZFX.toString();;//工资分析名册
    					   break;
    				   }
    				   case 6: {
                           n=6;                           
                           sortname="薪资自定义报表";
                           childGZDY.append(addUnSortMusterNode2(notSortList,res_flag,"14",userView,conn,n+12));
                           str=childGZDY.toString();//工资自定义报表
    					   break;
    				   }
    				   case 7: {
    					   n=7;//薪资台帐
    					   sortname="薪资台帐";
    					   childGZTZ.append(addUnSortMusterNode(notSortList,res_flag,"4",userView,conn,n+12));
    					   str=childGZTZ.toString();//工资台帐报表						
    					   break;
    				   }
    				   case 8: {
    					   n=8;//保险分析花名册
    					   sortname="保险分析花名册";
    					   childBXFX.append(addUnSortMusterNode(notSortList,res_flag,"8",userView,conn,n+12));
    					   str=childBXFX.toString();//保险分析花名册							
    					   break;
    				   }
    				   case 9: {						  
    			           n=9;//保险自定义花名册				           
    			           sortname="保险自定义花名册";
    			           childBXZDY.append(addUnSortMusterNode2(notSortList,res_flag,"11",userView,conn,n+12));
    			           str=childBXZDY.toString();//保险自定义花名册							
    					   break;
    				   }
    				   case 10: {			  
    			           n=10;//保险台帐
    			           /*sortname="保险台帐";
    			           childBXTZ.append(addUnSortMusterNode(notSortList,res_flag,"1",userView,conn,n+12));
    			           str=childBXTZ.toString();//保险台帐						
*/    					   break;
    				   }
    				   case 11: {
    					   n=11;//个人所得税花名册
    					   sortname="个人所得税花名册";
    					   childGRSDS.append(addUnSortMusterNode(notSortList,res_flag,"15",userView,conn,n+12));
    					   str=childGRSDS.toString();//个人所得税花名册							
    					   break;
    				   }
    				   case 12: {
                           n=12;//培训花名册
                           sortname="培训花名册";
                           childPX.append(addUnSortMusterNode(notSortList,res_flag,"61",userView,conn,n+12));
                           str=childPX.toString();//培训花名册
    			           break;
    				   }
    				   case 13: {
    					   n=13;//考勤花名册						   
    					   sortname="考勤花名册";
    					   childKQ.append(addUnSortMusterNode(notSortList,res_flag,"81",userView,conn,n+12));
    					   str=childKQ.toString();//考勤花名册							
    					   break;
    				   }
    				   case 14: {
    					   n=14;//人事异动花名册					   
    					   sortname="模板花名册";//xiegh 20170627 bug:29045 
    					   childRSYD.append(addUnSortMusterNode(notSortList,res_flag,"5",userView,conn,n+12));
    					   str=childRSYD.toString();//考勤花名册							
    					   break;
    				   }
    				   case 15:
    				   {
    					   n=15;//人事异动花名册					   
    					   sortname="合同台账";
    					   childRSYD.append(addUnSortMusterNode(notSortList,res_flag,"2",userView,conn,n+12));
    					   str=childHTTZ.toString();
    					   break;
    				   }
    				}
    				if(str==null||str.length()<=0)
    					continue;
    				out.append("<TR>");
    				out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
    				/*out.append("<img src=\"/images/");
    				if (s==12)
    				   out.append("tree_end.gif");
    				else
    					out.append("tree_split.gif");
    				out.append("\" border=no width=\"16\" height=\"16\">");*/
    				out.append("&nbsp");
    				out.println("</TD>");
    				out.append("<TD style=\"line-height:16px\" width=\"16\">");
    			    out.append("<SPAN id=zhugan"+n+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtree"+n+",document.all.img"+n+");\">");
    			    out.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=img"+n+">");
    			    out.append("</SPAN></TD>");
    			    out.append("<TD style=\"line-height:16px\" colSpan=4 nowrap><A href=\"javascript:void(0)\">"+sortname+"</A></TD>");
    			    out.append("<TD style=\"line-height:16px\" width=\"23\">");
    			    out.append("</TD>");
    			    out.append("</TR>");
    			    out.append("<TR id=\"subtree"+n+"\" style=\"display:none\"><TD style=\"line-height:16px\" colspan=7>");
    			    out.append("<TABLE cellSpacing=0 cols=7 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
    			    out.append("<tbody>");
    			    out.append(str);
    			    out.append("</tbody>");
    			    out.append("</TABLE></TD>");
    			    out.append("</TR>"); 
    			}
    			
    		}else
    		{
    			if(notSortList!=null&&notSortList.size()>0)
    			{
    				out.append("<TR>");
    				out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
    				//out.append("<img src=\"/images/tree_end.gif\" border=no width=\"16\" height=\"16\">");
    				out.println("&nbsp;");
    				out.append("</TD>");
    				out.append("<TD style=\"line-height:16px\" width=\"16\">");
    			    out.append("<SPAN id=zhugan"+n+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtree"+n+",document.all.img"+n+");\">");
    			    out.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=img"+n+">");
    			    out.append("</SPAN></TD>");
    			    out.append("<TD style=\"line-height:16px\" colSpan=2 nowrap><A href=\"javascript:void(0)\">未分类</A></TD>");
    			    out.append("<TD style=\"line-height:16px\" width=\"23\">");
    			    out.append("</TD>");
    			    out.append("</TR>");
    			    out.append("<TR id=\"subtree"+n+"\" style=\"display:none\"><TD style=\"line-height:16px\" colspan=9>");
    			    out.append("<TABLE cellSpacing=0 cols=7 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
    			    out.append("<tbody>");
    			    showNotSortTemplates(notSortList,out);
    			    out.append("</tbody>");
    			    out.append("</TABLE></TD>");
    			    out.append("</TR>"); 
    			}
    		}
        }catch(Exception e)
        {
        	
        }
		
	}
	private void showNotSortTemplates(ArrayList notSortList,JspWriter out)
	{
		int j=0;
		try
		{
			for(int i=0;i<notSortList.size();i++)
			{		
				j++;
				LazyDynaBean bean=(LazyDynaBean)notSortList.get(i);			
	            out.println("<TR onMouseOver=\"javascript:tr_onclick(this,'')\">"); 
	            out.println("<TD style=\"line-height:16px\" width=\"16\"> ");
	            out.println("&nbsp;");
	            out.println("</TD>");
	            out.println("<TD style=\"line-height:16px\" width=\"16\">");
	            /*out.println("<IMG src=\"/images/");
	            if (j==notSortList.size())
			       out.println("tree_end.gif");
			    else
			    	out.println("tree_split.gif");
	            out.println("\">");*/
	            out.println("&nbsp;");
	            out.println("</TD>");
	            out.println("<TD style=\"line-height:16px\" width=\"15\" align=\"left\"><img src=\"/images/overview_obj.gif\" border=0 width=\"16\" height=\"16\"></TD>");
	            out.println("<TD style=\"line-height:16px\" width=\"400\" height=\"20\">");
	            out.println("<A href=\"javascript:void(0);\" onclick=\"javascript:void(0);\">"+bean.get("text")+"</a></TD>");
	            out.println("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");	           
	            //制作
	            boolean ischeck=false;
	            if(this.str_content!=null&&this.str_content.length()>0)
	            {
	            	if(this.str_content.toUpperCase().indexOf(","+bean.get("id").toString()+",")!=-1)
	            	{
	            		ischeck=true;
	            		out.println("<input type=\"radio\" name=\"no"+i+"_"+j+"\" value=\""+bean.get("id").toString()+"\" checked>");
	            	}else
	            		out.println("<input type=\"radio\" name=\"no"+i+"_"+j+"\" value=\""+bean.get("id").toString()+"\">");
	            }else
	            {	            	
	            	out.println("<input type=\"radio\" name=\"no"+i+"_"+j+"\" value=\""+bean.get("id").toString()+"\">");
	            }
	            
	            out.println("</TD>");
	            out.println("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
	            //使用
	            if(this.str_content!=null&&this.str_content.length()>0)
	            {
	            	if(this.str_content.toUpperCase().indexOf(","+bean.get("id").toString()+"R,")!=-1)
	            	{
	            		ischeck=true;
	            		out.println("<input type=\"radio\" name=\"no"+i+"_"+j+"\" value=\""+bean.get("id").toString()+"R\" checked>");
	            	}
	            	else
	            		out.println("<input type=\"radio\" name=\"no"+i+"_"+j+"\" value=\""+bean.get("id").toString()+"R\">");
	            }else
	            {	
	               out.println("<input type=\"radio\" name=\"no"+i+"_"+j+"\" value=\""+bean.get("id").toString()+"R\">");
	            }
	            out.println("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
	            if(!ischeck)
	            {
	            	out.println("<input type=\"radio\" name=\"no"+i+"_"+j+"\" value=\"\" checked>");
	            }else
	            	out.println("<input type=\"radio\" name=\"no"+i+"_"+j+"\" value=\"\">");
	            out.println("</TD>");
	            out.println("</TD>");
	            out.println("</TR>");               
			}	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
				
	
	}
	/**
	 * 
	 * @param list	存放的是sortid
	 * @param res_flag	资源类型
	 * @param userView	
	 * @param bflag	是否加超链接
	 * @param parent	父节点
	 * @param conn	
	 * @return
	 */
	private ArrayList getNotSortTemplates(ArrayList list,String res_flag,UserView userView,Connection conn)
	{
		RowSet rset=null;
		ArrayList clist=new ArrayList();
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);			

			rset=dao.search(getNotSortResourceSql(list,res_type));
			while(rset.next())
			{			
				if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
					continue;	
				LazyDynaBean bean=new LazyDynaBean();		
				bean.set("id", rset.getString("tabid"));
				bean.set("text", rset.getString("tabid")+":"+rset.getString("name"));
				bean.set("title", rset.getString("name"));
				clist.add(bean);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return clist;
	}
	/**
	 * 
	 * @param list	存放的是sortid
	 * @param res_type	1=统计表Tsort，0=登记表Rsort，4=常用花名册lstyle，5=高级花名册muster_sort
	 * @return
	 */
	private String getNotSortResourceSql(ArrayList list,int res_type)
	{
		StringBuffer strsql=new StringBuffer();
		switch(res_type)
		{
		case 1:
			strsql.append("select tabid,name from tname where tsortid not in(");
			for(int i=0;i<list.size();i++){
				strsql.append("'"+list.get(i)+"',");
			}
			strsql.setLength(strsql.length()-1);
			strsql.append(")");
			break;
		case 0:
			strsql.append("select tabid,name from rname where moduleflag not in(");
			for(int i=0;i<list.size();i++){
				strsql.append("'"+list.get(i)+"',");
			}
			strsql.setLength(strsql.length()-1);
			strsql.append(")");
			break;
		case 4:
			switch(Sql_switcher.searchDbServer())
			{
				case Constant.MSSQL:
				{
					strsql.append("select tabid,hzname name from lname where SubString(moduleflag, 2, 2) =");
					/*for(int i=0;i<list.size();i++){
						strsql.append("'"+list.get(i)+"',");
					}*/
					strsql.append("'00'");
					//strsql.setLength(strsql.length()-1);
					//strsql.append(")");
				}
				break;
				case Constant.DB2:
				{
					strsql.append("select tabid,hzname name from lname where SubStr(moduleflag, 2, 2) =");
					/*for(int i=0;i<list.size();i++){
						strsql.append("'"+list.get(i)+"',");
					}*/
					strsql.append("'00'");
					//strsql.setLength(strsql.length()-1);
					//strsql.append(")");
				}
				break;
				case Constant.ORACEL:
				{
					strsql.append("select tabid,hzname name from lname where SubStr(moduleflag, 2, 2) =");
					/*for(int i=0;i<list.size();i++){
						strsql.append("'"+list.get(i)+"',");
					}*/
					strsql.append("'00'");
					//strsql.setLength(strsql.length()-1);
					//strsql.append(")");
				}
				break;
			}
			break;
		/*case 5:
			strsql.append("select tabid,cname name from muster_name where nmodule not in(");
			for(int i=0;i<list.size();i++){
				strsql.append("'"+list.get(i)+"',");
			}
			strsql.setLength(strsql.length()-1);
			strsql.append(")");
			break;*/
		}

		return strsql.toString();
	}
	private String assessgetSortTemplates(String sortid,String nmodule,String res_flag,UserView userView,int i, Connection conn,String parent_id) throws GeneralException
	{
		RowSet rset=null;
		RowSet rset1=null;
		RowSet rset2=null;
		boolean bhave=false;
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		String sql = "";
		
		StringBuffer out = new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			if("22".equalsIgnoreCase(this.type))
               sql="select template_setid,name,validflag,child_id,parent_id from per_template_set where 1=1 and subsys_id='"+sortid+"'and parent_id is null";
			else if("23".equalsIgnoreCase(this.type))
	           sql="select pointsetid,pointsetname,validflag,child_id from per_pointset where 1=1 and subsys_id='"+sortid+"'and parent_id is null";
            //if(!parent_id.equals(""))
           // sql+=" and parent_id='"+parent_id+"'";
			rset=dao.search(sql);
			int j=0;
			while(rset.next())
			{	
				String template_setid ="" ;
				String name = "";
				
				if("22".equalsIgnoreCase(this.type)){
				template_setid=rset.getString("template_setid");
				name=rset.getString("name");
				}
				else if("23".equalsIgnoreCase(this.type)){
				template_setid=rset.getString("pointsetid");
				name=rset.getString("pointsetname");
				}
				out.append("<TR ><td width=\"76%\">");
 				out.append("&nbsp;&nbsp;&nbsp;");
 			    out.append("<SPAN id=zhugan"+template_setid+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtree"+template_setid+",document.all.img"+template_setid+");\">");
 			    out.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=img"+template_setid+" align=\"absmiddle\" style=\"margin:2 8 2 5\">");
 			    out.append("</SPAN>");
//                out.append("<TD style=\"line-height:16px\" width=\"15\" align=\"left\"><img src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=0 width=\"16\" height=\"16\"></TD>");
 			    out.append("<A href=\"javascript:void(0)\">"+name+"</A></TD>");
 			    out.append("<TD style=\"line-height:16px\" width=\"8%\">");
 			    out.append("</TD>");
 			   out.append("<TD style=\"line-height:16px\" width=\"8%\">");
			    out.append("</TD>");
			    out.append("<TD style=\"line-height:16px\" width=\"8%\">");
 			    out.append("</TD>");
 			    out.append("</TR>");
 			    out.append("<TR id=\"subtree"+template_setid+"\" style=\"display:none\" ><TD style=\"line-height:16px\"  width=\"100%\" colspan=\"4\" style=\"padding-left:10\">");
 			    out.append("<TABLE cellSpacing=0 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
 			    out.append("<tbody>");
 			    
 			    getTemplate(template_setid,dao,out,1);
 			    //孩子
 			    doMethod(template_setid, dao, out,1);
 			   
 			   
 			    out.append("</tbody>");
 			    out.append("</TABLE></TD>");
 			    out.append("</TR>");
 			    
 			    bhave = true;		
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return out.toString();
	}
	public void doMethod(String parent_id,ContentDAO dao,StringBuffer out,int layer)
	{
		RowSet rs = null;
		String setid = "";
		String name = "";
		try{
			boolean flag=true;
			if("22".equalsIgnoreCase(this.type))
			rs = dao.search("select template_setid,name from per_template_set where parent_id='"+parent_id+"'");
			if("23".equalsIgnoreCase(this.type))
			rs = dao.search("select pointsetid,pointsetname from per_pointset where parent_id='"+parent_id+"'");
			layer++;
			while(rs.next()){
				flag=false;
			  if("22".equalsIgnoreCase(this.type)){
				setid=rs.getString("template_setid");
				name=rs.getString("name");
			  }else if("23".equalsIgnoreCase(this.type)){
					setid=rs.getString("pointsetid");
					name=rs.getString("pointsetname");
			  }
			    out.append("<TR><td width=\"70%\">");
			    for(int i=0;i<layer;i++){
					
					out.append("&nbsp;&nbsp;&nbsp;");
			    }
			    out.append("<SPAN id=zhugan"+setid+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtree"+setid+",document.all.img"+setid+");\">");
			    out.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=img"+setid+" align=\"absmiddle\" style=\"margin:2 8 2 5\">");
			    out.append("</SPAN>");
//              out.append("<TD style=\"line-height:16px\" width=\"15\" align=\"left\"><img src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=0 width=\"16\" height=\"16\"></TD>");
			    out.append("<A href=\"javascript:void(0)\">"+name+"</A></TD>");
			    out.append("<TD style=\"line-height:16px\" width=\"8%\">");
			    out.append("</TD>");
			    out.append("<TD style=\"line-height:16px\" width=\"8%\">");
			    out.append("</TD>");
			    out.append("<TD style=\"line-height:16px\" width=\"8%\">");
			    out.append("</TD>");
			    out.append("</TR>");
			    out.append("<TR id=\"subtree"+setid+"\" style=\"display:none\" ><TD style=\"line-height:16px\" width=\"100%\" colspan=4 style=\"padding-left:10\">");
			    out.append("<TABLE cellSpacing=0 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
			    out.append("<tbody>");
 			    //孩子
 			   
 			    doMethod(setid, dao, out,layer);
 			    getTemplate(setid,dao,out,layer);
 			    
 			    out.append("</tbody>");
 			    out.append("</TABLE></TD>");
 			    out.append("</TR>");
			}
			if(flag)
				return;

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void getTemplate(String template_setid,ContentDAO dao,StringBuffer out,int layer){
		RowSet rs = null;
		try{
			String pt_id = "";
			String name = "";
			if("22".equalsIgnoreCase(this.type))
			rs=dao.search("select * from per_template where template_setid="+template_setid);
			if("23".equalsIgnoreCase(this.type))
			rs=dao.search("select point_id,pointname,validflag from per_point where pointsetid='"+template_setid+"' order by seq");	
			while(rs.next()){
			 if("22".equalsIgnoreCase(this.type)){
				pt_id =rs.getString("template_id");
				name = rs.getString("name");
			  }else if("23".equalsIgnoreCase(this.type)){
				pt_id =rs.getString("point_id");
				name = rs.getString("pointname");
		       }
				
				out.append("<TR ><td width=\"76%\">"); 
				for(int i=0;i<=layer;i++){
		            out.append("&nbsp;&nbsp;&nbsp;");
				}
			
	            out.append("<img src=\"/images/overview_obj.gif\" border=0 width=\"16\" height=\"16\" align=\"absmiddle\" style=\"margin:2 8 2 5\">");
	            out.append("<A href=\"javascript:void(0);\" onclick=\"javascript:void(0);\">"+pt_id+":"+name+"</a></TD>");
	            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"8%\">");
            //制作
               boolean ischeck=false;
            if(this.str_content!=null&&this.str_content.length()>0)
            {   
            	String rrString =this.str_content.toUpperCase();
            	if(this.str_content.indexOf(","+pt_id+",")!=-1)
            	{
            		out.append(" <input type=\"radio\" name=\""+pt_id+"\" value=\""+pt_id+"\" checked>");
            		ischeck=true;
            	}
            	else
            		out.append("<input type=\"radio\" name=\""+pt_id+"\" value=\""+pt_id+"\">");
            }else
            {	            	
            	out.append("<input type=\"radio\" name=\""+pt_id+"\" value=\""+pt_id+"\">");
            }
            
            out.append("</TD>");
            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"8%\">");
            //使用
            if(this.str_content!=null&&this.str_content.length()>0)
            {
            	if(this.str_content.indexOf(","+pt_id+"R,")!=-1)
            	{
            		out.append("<input type=\"radio\" name=\""+pt_id+"\" value=\""+pt_id+"R\" checked>");
            		ischeck=true;
            	}
            	else
            		out.append("<input type=\"radio\" name=\""+pt_id+"\" value=\""+pt_id+"R\">");
            }else
            {	
               out.append("<input type=\"radio\" name=\""+pt_id+"\" value=\""+pt_id+"R\">");
            }
            
            out.append("</TD>");
            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"8%\">");
            if(!ischeck)
            {
            	out.append("<input type=\"radio\" name=\""+pt_id+"\" value=\"\" checked>");
            }else
            	out.append("<input type=\"radio\" name=\""+pt_id+"\" value=\"\">");
            out.append("</TD>");
            out.append("</TR>");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 加上子节点
	 * @param sortid	各模板表所对应的分类号
	 * @param nmodule	用来判断是否为高级花名册
	 * @param res_flag	资源类型
	 * @param userView	
	 * @param bflag	是否加超链接
	 * @param parent	父节点
	 * @param conn	
	 * @return
	 */
	private boolean getSortTemplates(String sortid,String nmodule,String res_flag,UserView userView,int i,int per_count,JspWriter out,Connection conn)
	{
		RowSet rset=null;
		boolean bhave=false;
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);			
            String sql=getResourceSql(sortid,nmodule,res_type);
            /*int count=0;
            rset=dao.search(sql);
            while(rset.next())
			{
            	count++;
			}*/
			rset=dao.search(sql);
			int j=0;
			while(rset.next())
			{		
				j++;
				if (!userView.isHaveResource(res_type, rset.getString("tabid")))
					continue;
	            out.println("<TR onMouseOver=\"javascript:tr_onclick(this,'')\">"); 
	            out.println("<TD style=\"line-height:16px\" width=\"16\"> ");
	           /* if(i==per_count)
	            {
	            	out.println("&nbsp;");
	            }
                else
                	out.println("<img src=\"/images/tree_vertline.gif\" border=0>");*/
	            out.append("&nbsp;");
	            out.println("</TD>");	            
	            out.println("<TD style=\"line-height:16px\" width=\"16\">");
	            /*out.println("<IMG src=\"/images/");
	            if (j==count)
			       out.println("tree_end.gif");
			    else
			    	out.println("tree_split.gif");
	            out.println("\">");*/
	            out.append("&nbsp;");
	            out.println("</TD>");
	            out.println("<TD style=\"line-height:16px\" width=\"15\" align=\"left\"><img src=\"/images/overview_obj.gif\" border=0 width=\"16\" height=\"16\"></TD>");
	            out.println("<TD style=\"line-height:16px\" width=\"400\"  height=\"20\">");
	            out.println("<A href=\"javascript:void(0);\" onclick=\"javascript:void(0);\">"+rset.getString("tabid")+":"+rset.getString("name")+"</a></TD>");
	            out.println("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");	           
	            //制作
	            boolean ischeck=false;
	            if(this.str_content!=null&&this.str_content.length()>0)
	            {
	            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+",")!=-1)
	            	{
	            		out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\" checked>");
	            		ischeck=true;
	            	}
	            	else
	            		out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\">");
	            }else
	            {	            	
	            	out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\">");
	            }
	            
	            out.println("</TD>");
	            out.println("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
	            //使用
	            if(this.str_content!=null&&this.str_content.length()>0)
	            {
	            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+"R,")!=-1)
	            	{
	            		out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\" checked>");
	            		ischeck=true;
	            	}
	            	else
	            		out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\">");
	            }else
	            {	
	               out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\">");
	            }
	            
	            out.println("</TD>");
	            out.println("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
	            if(!ischeck)
	            {
	            	out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\"\" checked>");
	            }else
	            	out.println("<input type=\"radio\" name=\""+i+"_"+j+"\" value=\"\">");
	            out.println("</TD>");
	            out.println("</TR>");               
			}			
		}
		catch(Exception ex)
		{
			
		}
		return bhave;
	}
	/**
	 * 消息库中是否存在对此模板的消息
	 * @return
	 */
	private boolean isHaveMsg(UserView userView,String tabid,Connection conn)
	{
		boolean bflag=false;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select count(*) as nmax from tmessage where state=0 and noticetempid=");
			buf.append(tabid);
			buf.append(" and (b0110='");
			buf.append(userView.getUserOrgId());
			buf.append("' or b0110 is null or b0110='')");
			ContentDAO dao=new ContentDAO(conn);
			RowSet rset=dao.search(buf.toString());
			int nrec=0;
			if(rset.next())
				nrec=rset.getInt("nmax");
			if(nrec!=0)
				bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
	}	
	/**
	 * 
	 * @param sortid	在各个sort表中的sortid
	 * @param nmodule	判断是否是高级花名册
	 * @param res_type	1=统计表Tsort，0=登记表Rsort，4=常用花名册lstyle，5=高级花名册muster_sort
	 * @return
	 */
	private String getResourceSql(String sortid,String nmodule,int res_type)
	{
		StringBuffer strsql=new StringBuffer();
		switch(res_type)
		{
		case 1:	//【8564】表格工具，新建一张报表，放到200分类的下面，编号为210，资源分配中看到该报表排在了最前面了，不对  jingq add 2015.04.10
			strsql.append("select tabid,name from tname where tsortid='"+sortid+"' order by tabid");
			break;
		case 0:
			strsql.append("select tabid,name from rname where moduleflag ='"+sortid+"'");
			break;
		case 4:
			switch(Sql_switcher.searchDbServer())
			{
				case Constant.MSSQL:
				{
					strsql.append("select tabid,hzname name from lname where SubString(moduleflag, 2, 2) ='"+sortid+"'");
				}
				break;
				case Constant.DB2:
				{
					strsql.append("select tabid,hzname name from lname where SubStr(moduleflag, 2, 2) ='"+sortid+"'");
				}
				break;
				case Constant.ORACEL:
				{
					strsql.append("select tabid,hzname name from lname where SubStr(moduleflag, 2, 2) ='"+sortid+"'");
				}
				break;
			}
			break;
		case 5:
			if("14".equalsIgnoreCase(nmodule)|| "11".equalsIgnoreCase(nmodule)/*||nmodule.equalsIgnoreCase("5")*/){//工资自定义报表，和保险自定义表,人事异动高级花名册，不用nprint限制吧
				strsql.append("select tabid,cname name from muster_name where nmodule='"+nmodule+"' and nprint ='"+sortid+"'");
			}else{
				strsql.append("select tabid,cname name from muster_name where sortid ='"+sortid+"' and nmodule='"+nmodule+"'" );
			}
			break;
		}

		return strsql.toString();
	}
	private String getContent(Connection conn)
	{
		/**资源查询*/
		if(flag==null|| "".equals(flag))
            flag=GeneralConstant.ROLE;
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		/**采用预警字段作为其资源控制字段*/
		/**当前被授权用户拥有的资源*/
		SysPrivBo privbo=new SysPrivBo(roleid,flag,conn,"warnpriv");
		String res_str=privbo.getWarn_str();
		ResourceParser parser=new ResourceParser(res_str,res_type);
		/**1,2,3*/
		String str_content=","+parser.getContent()+",";
		return str_content;
	}
	private String getSortMusterTemplates(String sortid,String nmodule,String res_flag,UserView userView,int i,int per_count,Connection conn)
	{
		RowSet rset=null;	
		StringBuffer out=new StringBuffer();
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);			
            String sql=getResourceSql(sortid,nmodule,res_type);
            int count=0;
            rset=dao.search(sql);
            while(rset.next())
			{
            	count++;
			}
			rset=dao.search(sql);
			int j=0;
			while(rset.next())
			{		
				j++;
				if (!userView.isHaveResource(res_type, rset.getString("tabid")))
					continue;
	            out.append("<TR onMouseOver=\"javascript:tr_onclick(this,'')\">"); 
	            out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
	           
	          /*  if(nmodule.equals("81"))
	            	out.append("&nbsp;");
	            else
	            	 out.append("<img src=\"/images/tree_vertline.gif\" border=no width=\"16\" height=\"16\">");*/
	            out.append("&nbsp;");
	            out.append("</TD>");
	            out.append("<TD style=\"line-height:16px\" width=\"16\"> ");
	           /* if(i==count)
	            {
	            	out.append("&nbsp;");
	            }
                else
                	out.append("<img src=\"/images/tree_vertline.gif\" border=0>");*/
	            out.append("&nbsp;");
	            out.append("</TD>");
	            out.append("<TD style=\"line-height:16px\" width=\"16\">");
	            /*out.append("<IMG src=\"/images/");
	            if (j==count)
			       out.append("tree_end.gif");
			    else
			    	out.append("tree_split.gif");
	            out.append("\">");*/
	            out.append("&nbsp;");
	            out.append("</TD>");
	            out.append("<TD style=\"line-height:16px\" width=\"15\" align=\"left\"><img src=\"/images/overview_obj.gif\" border=0 width=\"16\" height=\"16\"></TD>");
	            out.append("<TD style=\"line-height:16px\" height=\"20\">");
	            out.append("<A href=\"javascript:void(0);\" onclick=\"javascript:void(0);\">"+rset.getString("tabid")+":"+rset.getString("name")+"</a></TD>");
	            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");	           
	            //制作	      
	            boolean ischeck=false;
	            if(this.str_content!=null&&this.str_content.length()>0)
	            {
	            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+",")!=-1)
	            	{	
	            		ischeck=true;
	            		out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\" checked>");
	            	}else
	            		out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\">");
	            }else
	            {	            	
	            	out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\">");
	            }
	            
	            out.append("</TD>");
	            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
	            //使用
	            if(this.str_content!=null&&this.str_content.length()>0)
	            {
	            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+"R,")!=-1)
	            	{	
	            		out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\" checked>");
	            		ischeck=true;
	            	}
	            	else
	            		out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\">");
	            }else
	            {	
	               out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\">");
	            }	            
	            out.append("</TD>");
	            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
	            //使用
	            if(!ischeck)
	            {
	            	out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\"\" checked>");
	            }else
	            {	
	               out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\"\">");
	            }	            
	            out.append("</TD>");
	            out.append("</TR>");               
			}			
		}
		catch(Exception ex)
		{
			
		}
        return out.toString();
	}
	private int getSortMusterCount(String nmodule,Connection conn)
	{
	    	int count =0;
	    	String sql="select count(*) a  from muster_sort where nmodule='"+nmodule+"'";
	    	ContentDAO dao=new ContentDAO(conn);
	    	try
	    	{
	    		RowSet rs=dao.search(sql);
	    		if(rs.next())
	    			count=rs.getInt("a");
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return count;
	}
	private String getMusterTStr(String sortname,int i,int count,int n,String sortid,String nmodule,String res_flag,UserView userView,Connection conn)
	{
		StringBuffer child=new StringBuffer();		
		child.append("<TR>");
		child.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
		/*if(nmodule.equals("81"))
			child.append("&nbsp;");
		else
		  child.append("<img src=\"/images/tree_vertline.gif\" border=no width=\"16\" height=\"16\">");	*/	
		child.append("&nbsp;");
		child.append("</TD>");
		child.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
		/*child.append("<img src=\"/images/");
		if (i==count)
			child.append("tree_end.gif");
		else
			child.append("tree_split.gif");
		child.append("\" border=no width=\"16\" height=\"16\">");*/
		child.append("&nbsp;");
		child.append("</TD>");
		child.append("<TD style=\"line-height:16px\" width=\"16\">");
		child.append("<SPAN id=zhugancn"+n+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtreecn"+n+",document.all.imgcn"+n+");\">");
		child.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=imgcn"+n+">");
		child.append("</SPAN></TD>");
		child.append("<TD style=\"line-height:16px\" colSpan=3 width='499'><A href=\"javascript:void(0)\">"+sortname+"</A></TD>");
		child.append("<TD style=\"line-height:16px\" width=\"23\">");
		child.append("</TD>");
		child.append("</TR>");
		child.append("<TR id=\"subtreecn"+n+"\" style=\"display:none\"><TD style=\"line-height:16px\" colspan=9>");
		child.append("<TABLE cellSpacing=0 cols=9 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
		child.append("<tbody>");
	    //孩子
		child.append(getSortMusterTemplates(sortid,nmodule,res_flag,userView,i,count,conn));
		child.append("</tbody>");
		child.append("</TABLE></TD>");
		child.append("</TR>");
		return child.toString();
	}
	private boolean getSortMusterTemplates(String sortid,String nprint,String nmodule,String res_flag,UserView userView,int i,int per_count,Connection conn,StringBuffer out)
	{
		RowSet rset=null;
		boolean bhave=false;
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);	
			String sql=getMusterResourceSql(sortid,nprint,nmodule);
			int count=0;
            rset=dao.search(sql);
            while(rset.next())
			{
            	count++;
			}
			rset=dao.search(sql);			
			int j=0;
			while(rset.next())
			{			
				if (!userView.isHaveResource(res_type, rset.getString("tabid")))
					continue;	
				bhave=true;
				j++;
				if (!userView.isHaveResource(res_type, rset.getString("tabid")))
					continue;
	            out.append("<TR onMouseOver=\"javascript:tr_onclick(this,'')\">"); 
	            out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
	            //out.append("<img src=\"/images/tree_vertline.gif\" border=no width=\"16\" height=\"16\">");
	            out.append("&nbsp;");
	            out.append("</TD>");
	            out.append("<TD style=\"line-height:16px\" width=\"16\"> ");
	            out.append("&nbsp;");
	           /* if(i==per_count)
	            {
	            	out.append("&nbsp;");
	            }
                else
                	out.append("<img src=\"/images/tree_vertline.gif\" border=0>");*/
	            out.append("</TD>");
	            out.append("<TD style=\"line-height:16px\" width=\"16\">");
	            /*out.append("<IMG src=\"/images/");
	            if (j==count)
			       out.append("tree_end.gif");
			    else
			    	out.append("tree_split.gif");
	            out.append("\">");*/
	            out.append("&nbsp;");
	            out.append("</TD>");
	            out.append("<TD style=\"line-height:16px\" width=\"15\" align=\"left\"><img src=\"/images/overview_obj.gif\" border=0 width=\"16\" height=\"16\"></TD>");
	            out.append("<TD style=\"line-height:16px\" height=\"20\">");
	            out.append("<A href=\"javascript:void(0);\" onclick=\"javascript:void(0);\">"+rset.getString("tabid")+":"+rset.getString("name")+"</a></TD>");
	            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");	           
	            //制作
	            boolean ischeck=false;
	            if(this.str_content!=null&&this.str_content.length()>0)
	            {
	            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+",")!=-1)
	            	{
	            		out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\" checked>");
	            		ischeck=true;
	            	}
	            	else
	            		out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\">");
	            }else
	            {	            	
	            	out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"\">");
	            }
	            
	            out.append("</TD>");
	            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
	            //使用
	            if(this.str_content!=null&&this.str_content.length()>0)
	            {
	            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+"R,")!=-1)
	            	{	
	            		out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\" checked>");
	            	    ischeck=true;
	            	}
	            	else
	            		out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\">");
	            }else
	            {	
	               out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\""+rset.getString("tabid")+"R\">");
	            }
	            
	            out.append("</TD>");
	            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
	            if(!ischeck)
	            {
	            	out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\" value=\"\" checked>");
	            }else
	            	out.append("<input type=\"radio\" name=\""+nmodule+i+"_"+j+"\"  value=\"\">");
	            out.append("</TD>");
	            out.append("</TR>");    
			}
		}catch(Exception ex)
		{
				
		}
		return bhave;
		
	}
	private String getMusterResourceSql(String sortid,String nprint,String nmodule)
	{
		StringBuffer strsql=new StringBuffer();
		if("14".equalsIgnoreCase(nmodule)|| "11".equalsIgnoreCase(nmodule)|| "5".equalsIgnoreCase(nmodule)){
		    //工资自定义报表，和保险自定义表
		    //zxj 20170624 去掉' and sortid ='"+sortid+"，薪资保险自定义表不按自定义分类显示，按薪资类别显示，一次取全部
			strsql.append("select tabid,cname name from muster_name where nmodule='"+nmodule+"' and nprint ='"+nprint+"'");
			
			//人事异动 按自定义分类取
		    if("5".equalsIgnoreCase(nmodule))
		        strsql.append(" and sortid ='").append(sortid).append("'");
		}else{
			strsql.append("select tabid,cname name from muster_name where sortid ='"+sortid+"' and nmodule='"+nmodule+"'" );
		}
		return strsql.toString();
	}
	private int getCount(String sql,Connection conn)
	{
		int count =0;    	
    	ContentDAO dao=new ContentDAO(conn);
    	try
    	{
    		RowSet rs=dao.search(sql);
    		if(rs.next())
    			count=rs.getInt("aa");
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return count;
	}
	
	private String addUnSortMusterNode(ArrayList list,String res_flag,String nmodule,UserView userView,Connection conn,int n){
		
		StringBuffer out=new StringBuffer();
		out.append("<TR>");
		out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
		//out.append("<img src=\"/images/tree_vertline.gif\" border=no width=\"16\" height=\"16\">");
		out.append("&nbsp;");
		out.append("</TD>");
		out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
		out.append("&nbsp;");
		//out.append("<img src=\"/images/tree_end.gif\" border=no width=\"16\" height=\"16\">");
		out.append("</TD>");
		out.append("<TD style=\"line-height:16px\" width=\"16\">");
	    out.append("<SPAN id=zhugan"+n+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtree"+n+",document.all.img"+n+");\">");
	    out.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=img"+n+">");
	    out.append("</SPAN></TD>");
	    out.append("<TD style=\"line-height:16px\" colSpan=2 nowrap><A href=\"javascript:void(0)\">未分类</A></TD>");
	    out.append("<TD style=\"line-height:16px\" width=\"23\">");
	    out.append("</TD>");
	    out.append("</TR>");
	    out.append("<TR id=\"subtree"+n+"\"  style=\"display:none\"><TD style=\"line-height:16px\" colspan=9>");
	    out.append("<TABLE cellSpacing=0 cols=7 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
	    out.append("<tbody>");
	    if(!getMusterNotSortTemplates(list,res_flag,nmodule,userView,out,conn))
        	return "";
	    out.append("</tbody>");
	    out.append("</TABLE></TD>");
	    out.append("</TR>");       
	    return out.toString();
	}
	private String addUnSortMusterNode2(ArrayList list,String res_flag,String nmodule,UserView userView,Connection conn,int n){
		StringBuffer out=new StringBuffer();		
		out.append("<TR>");
		out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
		//strBXFX.append("<img src=\"/images/tree_vertline.gif\" border=no width=\"16\" height=\"16\">");
		out.append("&nbsp;");
		out.append("</TD>");
		out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
		//out.append("<img src=\"/images/tree_end.gif\" border=no width=\"16\" height=\"16\">");
		out.append("&nbsp;");
		out.append("</TD>");
		out.append("<TD style=\"line-height:16px\" width=\"16\">");
	    out.append("<SPAN id=zhugan"+n+" style=\"CURSOR: hand;\" onclick=\"javascript:display1(document.all.subtree"+n+",document.all.img"+n+");\">");
	    out.append("<IMG src=\"/ext/resources/images/default/tree/elbow-end-plus-nl.gif\" border=no name=img"+n+">");
	    out.append("</SPAN></TD>");
	    out.append("<TD style=\"line-height:16px\" colSpan=2 width='499' nowrap><A href=\"javascript:void(0)\">公用表</A></TD>");
	    out.append("<TD style=\"line-height:16px\" width=\"23\">");
	    out.append("</TD>");
	    out.append("</TR>");
	    out.append("<TR id=\"subtree"+n+"\" style=\"display:none\"><TD style=\"line-height:16px\" colspan=10>");
	    out.append("<TABLE cellSpacing=0 cols=7 cellPadding=0 width=100% border=0 style=\"font-size: 12px\">");
	    out.append("<tbody>");	   
        if(!getMusterNotSortTemplates2(res_flag,nmodule,userView,out,conn))
           return "";
	    out.append("</tbody>");
	    out.append("</TABLE></TD>");
	    out.append("</TR>"); 
	    return out.toString();        	
	}
	/**高级花名册专用
	 * 加入未在模板中的节点
	 */
		private boolean getMusterNotSortTemplates(ArrayList list,String res_flag,String nmodule,UserView userView,StringBuffer out,Connection conn)
		{
			RowSet rset=null;
			boolean bhave=false;
			
			if(res_flag==null|| "".equals(res_flag))
				res_flag="0";
			/**资源类型*/
			int res_type=Integer.parseInt(res_flag);
			try
			{
				
				ContentDAO dao=new ContentDAO(conn);			
                String sql =getMusterNotSortResourceSql(list,nmodule);
                int count=0;
	            rset=dao.search(sql);
	            while(rset.next())
				{
	            	count++;
				}
				int j=0;
				rset=dao.search(sql);
				while(rset.next())
				{			
					j++;
					if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
						continue;
					   bhave=true;
					   out.append("<TR onMouseOver=\"javascript:tr_onclick(this,'')\">"); 
			           out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
			         //out.append("<img src=\"/images/tree_vertline.gif\" border=no width=\"16\" height=\"16\">");
			           out.append("&nbsp;");
			           out.append("</TD>");
			           out.append("<TD style=\"line-height:16px\" width=\"16\"> ");
			           out.append("&nbsp;");
			           out.append("</TD>");
			           out.append("<TD style=\"line-height:16px\" width=\"16\">");
			           /*out.append("<IMG src=\"/images/");
			           if (j==count)
					       out.append("tree_end.gif");
					    else
					    	out.append("tree_split.gif");
			            out.append("\">");*/
			            out.append("&nbsp;");
			            out.append("</TD>");
			            out.append("<TD style=\"line-height:16px\" width=\"15\" align=\"left\"><img src=\"/images/overview_obj.gif\" border=0 width=\"16\" height=\"16\"></TD>");
			            out.append("<TD style=\"line-height:16px\" height=\"20\">");
			            out.append("<A href=\"javascript:void(0);\" onclick=\"javascript:void(0);\">"+rset.getString("tabid")+":"+rset.getString("name")+"</a></TD>");
			            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");	           
			            //制作
			            boolean ischeck=false;
			            if(this.str_content!=null&&this.str_content.length()>0)
			            {
			            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+",")!=-1)
			            	{	
			            		out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"\" checked>");
			            		ischeck=true;
			            	}
			            	else
			            		out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"\">");
			            }else
			            {	            	
			            	out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"\">");
			            }
			            
			            out.append("</TD>");
			            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
			            //使用
			            if(this.str_content!=null&&this.str_content.length()>0)
			            {
			            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+"R,")!=-1)
			            	{	
			            		out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"R\" checked>");
			            		ischeck=true;
			            	}
			            	else
			            		out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"R\">");
			            }else
			            {	
			               out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"R\">");
			            }			            
			            out.append("</TD>");
			            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
			            if(!ischeck)
			            {
			            	out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\"\" checked>");
			            }else
			            	out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\"  value=\"\">");
			            out.append("</TD>");
			            out.append("</TR>");  
				}
				
				
		}
		catch(Exception ex)
		{
			
		}
		return bhave;
	}
		
    private boolean getMusterNotSortTemplates2(String res_flag,String nmodule,UserView userView,StringBuffer out,Connection conn)
    {
    	RowSet rset=null;
		boolean bhave=false;
		
		if(res_flag==null|| "".equals(res_flag))
			res_flag="0";
		/**资源类型*/
		int res_type=Integer.parseInt(res_flag);
		try
		{
			ContentDAO dao=new ContentDAO(conn);			

			rset=dao.search(getMusterNotSortResourceSql2(nmodule));
			int j=0;
			while(rset.next())
			{			
				j++;
				if (!userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
				  continue;
				bhave=true;
				bhave=true;
				   out.append("<TR onMouseOver=\"javascript:tr_onclick(this,'')\">"); 
		           out.append("<TD style=\"line-height:16px\" height=\"20\" width=\"16\">");
		         //out.append("<img src=\"/images/tree_vertline.gif\" border=no width=\"16\" height=\"16\">");
		           out.append("&nbsp;");
		           out.append("</TD>");
		           out.append("<TD style=\"line-height:16px\" width=\"16\"> ");
		           out.append("&nbsp;");
		           out.append("</TD>");
		           out.append("<TD style=\"line-height:16px\" width=\"16\">");
		           /*out.append("<IMG src=\"/images/");
		           if (j==count)
				       out.append("tree_end.gif");
				    else
				    	out.append("tree_split.gif");
		            out.append("\">");*/
		            out.append("&nbsp;");
		            out.append("</TD>");
		            out.append("<TD style=\"line-height:16px\" width=\"15\" align=\"left\"><img src=\"/images/overview_obj.gif\" border=0 width=\"16\" height=\"16\"></TD>");
		            out.append("<TD style=\"line-height:16px\" height=\"20\">");
		            out.append("<A href=\"javascript:void(0);\" onclick=\"javascript:void(0);\">"+rset.getString("tabid")+":"+rset.getString("name")+"</a></TD>");
		            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");	           
		            //制作
		            boolean ischeck=false;
		            if(this.str_content!=null&&this.str_content.length()>0)
		            {
		            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+",")!=-1)
		            	{	
		            		out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"\" checked>");
		            		ischeck=true;
		            	}
		            	else
		            		out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"\">");
		            }else
		            {	            	
		            	out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"\">");
		            }
		            
		            out.append("</TD>");
		            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
		            //使用
		            if(this.str_content!=null&&this.str_content.length()>0)
		            {
		            	if(this.str_content.toUpperCase().indexOf(","+rset.getString("tabid")+"R,")!=-1)
		            	{	
		            		out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"R\" checked>");
		            		ischeck=true;
		            	}
		            	else
		            		out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"R\">");
		            }else
		            {	
		               out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\""+rset.getString("tabid")+"R\">");
		            }			            
		            out.append("</TD>");
		            out.append("<TD style=\"line-height:16px\" align=\"center\" width=\"30\">");
		            if(!ischeck)
		            {
		            	out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\" value=\"\" checked>");
		            }else
		            	out.append("<input type=\"radio\" name=\"nomuster"+nmodule+j+"\"  value=\"\">");
		            out.append("</TD>");
		            out.append("</TR>");  
				
			}
		}
		catch(Exception ex)
		{
			
		}
		return bhave;	
	}	
		/**高级花名册专用
		 * 查询未在模板中的节点语句
		 */
		
   private String getMusterNotSortResourceSql(ArrayList list,String nmodule)
   {
			StringBuffer strsql=new StringBuffer();
			strsql.append("select tabid,cname name from muster_name where nmodule ="+nmodule+" and sortid= ");
			/*for(int i=0;i<list.size();i++){
				strsql.append("'"+list.get(i)+"',");
			}*/
			strsql.append("'0'");
			//strsql.setLength(strsql.length()-1);
			//strsql.append(")");
			return strsql.toString();
	}
   private String getMusterNotSortResourceSql2(String nmodule)
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,cname name from muster_name where nmodule='"+nmodule+"' and nprint ='-1'");
		return strsql.toString();
	}
}
