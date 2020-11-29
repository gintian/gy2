package com.hjsj.hrms.servlet.agent;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgentEmpTreeServlet extends HttpServlet {
	
	private static ThreadLocal threadLocal = new ThreadLocal(); 
    /**首次加载,控制首次不加人员*/
    private boolean bfirst=false;   
    private String chitemid=""; /**中国联通推荐表专用*/
    
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map paramMap = new HashMap();
		threadLocal.set(paramMap);
		/** 参数串 */
		paramMap.put("params", req.getParameter("params"));
		/**  执行jsp文件 */
		paramMap.put("action", req.getSession().getAttribute("Agent_ORG_ACTION"));
		 /**  目标窗口 */
		paramMap.put("target", req.getParameter("target"));
		 /**人员还是对组织*/
		paramMap.put("flag", req.getParameter("flag"));
		/**加载应用库标识
	     * =0权限范围内的库
	     * =1权限范围内的登录库
	     * */
		paramMap.put("dbtype", req.getParameter("dbtype"));
		String id=(String)req.getParameter("id");	
		 /**权限过滤标识
	     * =0， 不进行权限过滤
	     * =1  进行权限过滤
	     * */
		String priv=(String)req.getParameter("priv");
		priv=priv!=null&&priv.trim().length()>0?priv:"1";
		paramMap.put("priv", priv);
		
		/**加载选项
	     * =0（单位|部门|职位）
	     * =1 (单位|部门)
	     * =2 (单位)
	     * */
		String loadtype=(String)req.getParameter("loadtype");
		loadtype=loadtype!=null&&loadtype.trim().length()>0?loadtype:"0";
		paramMap.put("loadtype", loadtype);		
		/**是否加其他过滤条件*/
		paramMap.put("isfilter", req.getParameter("isfilter"));
		
		String dbpre=(String)req.getParameter("dbpre");
		 /** 是否显示人员库 */
		String showDbName=req.getParameter("showDbName")!=null?"1":"0";
		if(req.getParameter("showDb")!=null)
			showDbName=req.getParameter("showDb");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = req.getParameter("backdate");
		backdate=backdate!=null&&backdate.length()>=10?backdate:sdf.format(new Date());
		/** 只列本人所在单位节点  0:不显示  1：显示  author:dc*/
		String showSelfNode=(String)req.getParameter("showSelfNode");
		showSelfNode=showSelfNode!=null&&showSelfNode.trim().length()>0?showSelfNode:"0";
		paramMap.put("showSelfNode", showSelfNode);
		
		String orgcode=(String)req.getParameter("orgcode");
		orgcode=orgcode!=null&&orgcode.trim().length()>0?orgcode:"";
		String umlayer = req.getParameter("umlayer");
		umlayer = umlayer!=null&&umlayer.trim().length()>0?umlayer:"0";
		
		String parent_id=req.getParameter("parent_id");
		UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);		
		
		showSelfNode="0";
		String first=(String)req.getParameter("first");
		if(first==null|| "".equalsIgnoreCase(first))
			this.bfirst=false;
		else
			this.bfirst=true;
		try
		{
		   String xmlc=outOrgEmployTree(userview,id,dbpre,umlayer,parent_id,backdate); 
		   resp.setContentType("text/xml;charset=UTF-8");
		   resp.getWriter().println(xmlc);   
		}
		catch(Exception ee)
		{
	      ee.printStackTrace();
		}
	}
	
	 public String outOrgEmployTree(UserView userview,String id,String dbpre,String umlayer,String parent_id,String backdate)throws GeneralException {
		 	HashMap paramMap = (HashMap) threadLocal.get();
	    	String params = (String) paramMap.get("params");
	    	String action = (String) paramMap.get("action");
	    	String target = (String) paramMap.get("target");
	    	String flag = (String) paramMap.get("flag");
	    	String dbtype = (String) paramMap.get("dbtype");
 	    	String priv = (String) paramMap.get("priv");
	    	String loadtype = (String) paramMap.get("loadtype");	    	
	    	String isfilter = (String) paramMap.get("isfilter");	
	    	String showSelfNode = (String) paramMap.get("showSelfNode");
	    	StringBuffer xmls = new StringBuffer();	        
	        ResultSet rset = null;
	        Connection conn = AdminDb.getConnection();
	        Element root = new Element("TreeNode");
	        root.setAttribute("id","00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","organization");
	        Document myDocument = new Document(root);
	        String theaction=null;	
	        String strsql=getLoadTreeQueryString(params,"0",parent_id,id,backdate);    
	        try
	        {
	            ContentDAO dao = new ContentDAO(conn);
		          rset = dao.search(strsql.toString());
		          String codeid=null;
		          /**加载组织机构树*/
		          while (rset.next())
		          {
		        	codeid=rset.getString("codesetid"); 
		            if("2".equalsIgnoreCase(loadtype))
		            {
		            	if("@K".equalsIgnoreCase(codeid)|| "UM".equalsIgnoreCase(codeid))
		            		continue;            	
		            }
		            if("1".equalsIgnoreCase(loadtype))
		            {
		            	if("@K".equalsIgnoreCase(codeid))
		            		continue;
		            }
		        	Element child = new Element("TreeNode");
		            child.setAttribute("id", rset.getString("codesetid")+rset.getString("codeitemid"));
		            child.setAttribute("text", rset.getString("codeitemdesc"));
		            child.setAttribute("title", rset.getString("codeitemdesc"));
		            if(!(action==null|| "".equals(action)))
		            {
				        if(action.indexOf('?')==0){
				        	theaction=action+"?a_code="+rset.getString("codesetid")+rset.getString("codeitemid"); 
				        }
				        else{
				        	theaction=action+"&a_code="+rset.getString("codesetid")+rset.getString("codeitemid");
				        }
		            }
		            if(theaction==null|| "".equals(theaction))
		            	child.setAttribute("href", "javascript:void(0)");            	
		            else{
		            	child.setAttribute("href", theaction);	
		            }
		            child.setAttribute("target", target);
		    		String url="/agent/agent_tree?dbpre=" + dbpre + "&isfilter=" + isfilter 
		    				+ "&target="+target+"&flag="+flag+"&dbtype="+dbtype
		    				+"&priv="+priv+"&loadtype="+loadtype+"&chitemid="+this.chitemid
		    				+"&umlayer="+umlayer;
		    		//如果不级联显示，加上限制
		    		url=url+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'";
		    		url=url+"&id="+rset.getString("codesetid")+rset.getString("codeitemid");
		    		if("1".equals(showSelfNode))
		    				url=url+"&showSelfNode="+showSelfNode;		    		
		    		url=url+"&vg="+rset.getString("orgtype");
		    		url+="&parent_id="+rset.getString("codeitemid");
		    		child.setAttribute("xml", url);		     		
		            if("UN".equals(rset.getString("codesetid")))
		            {
		             
		                child.setAttribute("icon","/images/unit.gif");
		               
		            }
		            if("UM".equals(rset.getString("codesetid")))
		            {
		              
		                child.setAttribute("icon","/images/dept.gif");
		               
		            }
		            root.addContent(child);
		          }
		          /**加载当前机构下的人员*/
		          if("1".equals(flag)&&!this.bfirst)
		          {
		        	  dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"Usr";
		        	  getEmploys(userview,id,root,conn,dbpre);
		          }
		          XMLOutputter outputter = new XMLOutputter();
		          Format format=Format.getPrettyFormat();
		          format.setEncoding("UTF-8");
		          outputter.setFormat(format);
		          xmls.append(outputter.outputString(myDocument));
	        }
	        catch (Exception ee)
	        {
	          ee.printStackTrace();	         
	        }
	        finally
	        {
	            com.hjsj.hrms.utils.PubFunc.closeResource(rset);
	            com.hjsj.hrms.utils.PubFunc.closeResource(conn);
	        }
	        return xmls.toString();        
	    }

		private int currentlayer=1;
		/**
		 * 判断部门是第几层
		 * @param codeitemid
		 * @return
		 */
		private int currentlayer(String codeitemid){
			String sql = "select * from organization where codesetid='UM' and codeitemid=(select parentid from organization where codesetid='UM' and codeitemid='"+codeitemid+"')";
			/**加载虚拟组织节点*/
	        
	        ResultSet rset = null;
	        Connection conn = null;
	        try {
				conn = AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				rset = dao.search(sql);
				while(rset.next()){
					++currentlayer;
					currentlayer(rset.getString("codeitemid"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
			    com.hjsj.hrms.utils.PubFunc.closeResource(rset);
			    com.hjsj.hrms.utils.PubFunc.closeResource(conn);
			}
	        
			return currentlayer;
		}
		
		private void getEmploys(UserView userview,String parentid,Element root,Connection conn,String dbpre)
	    {
			HashMap paramMap = (HashMap) threadLocal.get();
	    	String action = (String) paramMap.get("action");
	    	String target = (String) paramMap.get("target");
	      String strsql=getPrivSql(userview, parentid,conn,dbpre);
	      if("".equals(strsql))
	    	  return;
	      //System.out.println(strsql);
	      String theaction=null;
	      ContentDAO dao=new ContentDAO(conn);
	      RowSet rset=null;
	      try
	      {
	    	  rset=dao.search(strsql);
	    	  while(rset.next())
	    	  {
	    		  String nbase=rset.getString("dbase");
	    		  String a0100=rset.getString("a0100");
	    		  String a0101=rset.getString("a0101");
	              Element child = new Element("TreeNode");
	              child.setAttribute("id", nbase+a0100);
	              if(a0101==null)
	            	  a0101="";
	              if(!(action==null|| "".equals(action)))
	              {
	  		        if(action.indexOf('?')==0)
	  		        	theaction=action+"?a_code="+nbase+rset.getString("a0100");
	  		        else
	  		        	theaction=action+"&a_code="+nbase+rset.getString("a0100");
	              }
	              if(theaction==null|| "".equals(theaction))
	              	child.setAttribute("href", "javascript:void(0)");            	
	              else
	              	child.setAttribute("href", theaction);
	              child.setAttribute("text", a0101);
	              child.setAttribute("title", a0101);
	              child.setAttribute("target", target);
	              //child.setAttribute("xml", "javascript:void(0)");
	              child.setAttribute("icon","/images/man.gif");
	              root.addContent(child);
	    	  }
	      }
	      catch(Exception ex)
	      {
	    	  ex.printStackTrace();
	      }
	    }
		/**
	     * 取得人员权限过滤条件
	     * @param userview
	     * @param parentid
	     * @param conn
	     * @param dbpre
	     * @return
	     */
		private String getPrivSql(UserView userview, String parentid,Connection conn,String dbpre) 
		{
			DbNameBo dbbo=new DbNameBo(conn);				
			ArrayList logdblist=new ArrayList();;
			try {
				logdblist = dbbo.getAllLoginDbNameList();
			} catch (GeneralException e) {
				e.printStackTrace();
			}
			if(logdblist.size()==0)
				return "";
			String codeid=parentid.substring(0,2);
			String codevalue=parentid.substring(2);
			if("@K".equals(codeid))
				return "";
			StringBuffer strSql=new StringBuffer();	
			for(int i=0;i<logdblist.size();i++)
			{
				RecordVo vo=(RecordVo)logdblist.get(i);
				String nbase=vo.getString("pre");
				String strSelect=getSelectString(nbase);
				strSql.append(strSelect+" from "+nbase+"A01");
				strSql.append(" where ");
				if("UN".equals(codeid))
				{
					strSql.append(" b0110='"+codevalue+"' ");
					strSql.append( " and "+Sql_switcher.isnull("e0122", "'##'")+"='##'");
					strSql.append( " and "+Sql_switcher.isnull("e01a1", "'##'")+"='##'");
				}else if("UM".equals(codeid))
				{
					strSql.append( " e0122='"+codevalue+"'");					
				}
				strSql.append(" union ");	
				//strlog.append(",");
			}
			strSql.setLength(strSql.length()-7);
			//System.out.println(strSql);
			return strSql.toString() ;		  
		}
		
	    private String getSelectString(String dbpre)
	    {
	        	StringBuffer strsql=new StringBuffer();
		        strsql.append("select distinct a0000,");
		        strsql.append(dbpre);
		        strsql.append("a01.a0100 ,'");
		        strsql.append(dbpre);
		        strsql.append("' as dbase,");
		        strsql.append(dbpre);        
		        strsql.append("a01.b0110 b0110,e0122,");
		        strsql.append(dbpre);
		        strsql.append("a01.e01a1 e01a1,a0101 ");           	
		        strsql.append(" ");
		        return strsql.toString();
	    }	
	   
	    private String getLoadTreeQueryString(String params,String isSuperuser,String parentid,String managepriv,String backdate) {
			StringBuffer strsql=new StringBuffer();
			strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'org' as orgtype,a0000 ");
			strsql.append(" FROM organization ");   
	    	if(params!=null && "root".equals(params)){
	    		
	    		if((managepriv !=null && managepriv.trim().length()==2))
    			{
    				strsql.append(" WHERE codeitemid=parentid ");
    			}else if((managepriv !=null && managepriv.trim().length()>=2))
    			{
    				managepriv=managepriv.substring(2,managepriv.length());
	    			strsql.append(" WHERE codeitemid='");
	    			strsql.append(managepriv);
	    			strsql.append("'");  
    			}else
    			{
    				strsql.append(" WHERE 1=2");	    		
    			}
	    		
	    		
	    	}
	    	else
	    	{
	    		strsql.append(" WHERE parentid='");
	    		strsql.append(parentid);
	    		strsql.append("'");
	    		strsql.append(" AND codeitemid<>parentid ");
	    	}
	    	strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");	
	    	strsql.append(" and (codesetid='UN' or codesetid='UM') ");	
	    	strsql.append(" ORDER BY orgtype,a0000,codeitemid ");
	    	//System.out.println(strsql.toString());
			return strsql.toString();
		}
}
