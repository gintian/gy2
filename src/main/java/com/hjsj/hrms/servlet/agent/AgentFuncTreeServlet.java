package com.hjsj.hrms.servlet.agent;

import com.hjsj.hrms.businessobject.info.AgentsetUtils;
import com.hjsj.hrms.businessobject.sys.FunctionTree;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class AgentFuncTreeServlet extends HttpServlet {
	private EncryptLockClient lock;
	private FunctionTree functionTree;
	//private HashMap warnPrivMap = new HashMap();
	private static ThreadLocal threadLocal = new ThreadLocal();
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
      doPost(req, resp);
    }

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException 
	{
		
//		String role_id = (String) req.getParameter("role_id");
//		String flag = (String) req.getParameter("flag");
		String id=(String)req.getParameter("id");
		String operate = (String)req.getParameter("operate");
		AgentsetUtils agentsetUtils = null;
		/**加载第一层*/
		lock=(EncryptLockClient)this.getServletContext().getAttribute("lock");	
		this.functionTree=new FunctionTree(lock);
		Connection conn = null;
		try {
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buf = new StringBuffer();
			UserView userView = (UserView) req.getSession().getAttribute(
					WebConstant.userView);
			//buf.append("[{id:'1',text:'aaa',leaf:true,checked:true},{id:'2',text:'bbbb',leaf:true,checked:false}]");
			/**当前选中的角色或用户拥有的功能号列表*/
			String tmp = "";
			//SysPrivBo sysPrivBo = new SysPrivBo(role_id, flag, conn,"functionpriv");
	        //String func_str = sysPrivBo.getFunc_str();
	        agentsetUtils=new AgentsetUtils(conn);
	        if("1".equals(operate)){
	        	String agent_func_str=agentsetUtils.getFunctionprivStr(id);	
	        	String func_id = (String) req.getParameter("node");
	            tmp = searchFunctionXmlHtml(userView, conn, /*func_str*/"", func_id,agent_func_str);	 
	        }else if("2".equals(operate)){
	        	String warn_priv_str = agentsetUtils.getWarnprivStr(id);
	        	//修改代理业务授权 分类模板看不到  upd by hej 2015/12/9
	        	String warn_id = SafeCode.keyWord_reback(req.getParameter("node"));
	        	threadLocal.set(agentsetUtils.analyseParameter(warn_priv_str));
	        	tmp = searchWarnXmlHtml(userView,conn,warn_id);
	        }
			resp.setHeader("cache-control", "no-cache");
			resp.setHeader("Pragma", "no-cache");
			resp.setContentType("text/html;charset=UTF-8");

			resp.getWriter().write(tmp);
			resp.getWriter().close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				//if(rset!=null)
				//	rset.close();
				if (conn != null && (!conn.isClosed()))
					conn.close();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}
	
	private String searchWarnXmlHtml(UserView userView,Connection conn,String warn_id){
		StringBuffer buf = new StringBuffer();
		StringBuffer tmp=new StringBuffer();
		RowSet rset=null;
		RowSet rset1=null;
		try{
			ContentDAO dao=new ContentDAO(conn);
			if(warn_id!=null && !"".equals(warn_id) && "0".equals(warn_id)){
			if(userView.isHavetemplateid(IResourceConstant.RSBD)){
				tmp.append("{id:'");
	           tmp.append(-1);
	           tmp.append("',text:'");
	           tmp.append("人事业务");
	           tmp.append("'");
	           tmp.append(",leaf:false");
	           tmp.append(",cls:'first-level'");
	           tmp.append("}");
	           tmp.append(",");
//        		Element child = new Element("TreeNode");
//        	    child.setAttribute("id", "0");
//                child.setAttribute("text", "人事业务");
//                child.setAttribute("title", "人事业务");
//                child.setAttribute("target", "mil_body");	
//    			child.setAttribute("icon","/images/overview_obj.gif");
//    			child.setAttribute("xml","/agent/proxy_source?isRoot=0&isChild=0&type=1&res_flag=7&href=1");
//    			root.addContent(child);
        	}
        	if(userView.isHavetemplateid(IResourceConstant.GZBD)){
        		tmp.append("{id:'");
 	           tmp.append(-2);
 	           tmp.append("',text:'");
 	           tmp.append("薪资变动");
 	           tmp.append("'");
 	           tmp.append(",leaf:false");
 	           tmp.append(",cls:'first-level'");
 	           tmp.append("}");
 	           tmp.append(",");
//        		Element child = new Element("TreeNode");
//	        	child.setAttribute("id", "1");
//	            child.setAttribute("text", "薪资变动");
//	            child.setAttribute("title", "薪资变动");
//	            child.setAttribute("target", "mil_body");	
//    			child.setAttribute("icon","/images/overview_obj.gif");
//    			child.setAttribute("xml","/agent/proxy_source?isRoot=0&isChild=0&type=2&res_flag=8&href=1");
//    			root.addContent(child);
        	}
        	if(userView.isHavetemplateid(IResourceConstant.INS_BD)){
        		tmp.append("{id:'");
  	           tmp.append(-3);
  	           tmp.append("',text:'");
  	           tmp.append("保险变动");
  	           tmp.append("'");
  	           tmp.append(",leaf:false");
  	         tmp.append(",cls:'first-level'");
  	           tmp.append("}");
  	           tmp.append(",");
//        		Element child = new Element("TreeNode");
//    			child.setAttribute("id", "2");
//    			child.setAttribute("text", "保险变动");
//    			child.setAttribute("title", "保险变动");
//    			child.setAttribute("target", "mil_body");	
//    			child.setAttribute("icon","/images/overview_obj.gif");
//    			child.setAttribute("xml","/agent/proxy_source?isRoot=0&isChild=0&type=8&res_flag=17&href=1");
//    			root.addContent(child);
        	}
		}else if("-1".equals(warn_id) || "-2".equals(warn_id) || "-3".equals(warn_id)){
			String type="";
			String res_flag="";
			String res_desc="";
			if("-1".equals(warn_id)){
				type="1";
			    res_flag="7";
			    res_desc="rsbd";
			}else if("-2".equals(warn_id)){
				type="2";
			    res_flag="8";
			    res_desc="gzbd";
			}else if("-3".equals(warn_id)){
				type="8";
			    res_flag="17";
			    res_desc="ins_bd";
			}
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
    		String unit_type=null;
    		StringBuffer strsql=new StringBuffer();
    		unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
    		if(unit_type==null|| "".equals(unit_type))
    			unit_type="3";			
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
			rset=dao.search(strsql.toString());			
			while(rset.next())
			{
				   HashMap warnPrivMap = (HashMap) threadLocal.get();
	   	           String warn_priv_str = (String)warnPrivMap.get(res_desc);
				   tmp.append("{id:'");
	  	           tmp.append(res_flag+"#"+rset.getString("operationcode"));
	  	           tmp.append("',text:'");
	  	           tmp.append(rset.getString("operationname"));
	  	           tmp.append("'");
	  	         if(warn_priv_str!=null && haveTheFunc(warn_priv_str,rset.getString("operationcode")))
	 	           {
	 		          tmp.append(",checked:true");	        	  
	 	           }
	 	           else
	 	           {
	 	        	  tmp.append(",checked:false");
	 	           }
	  	           tmp.append(",leaf:false");
	  	         tmp.append(",cls:'second-level'");
	  	           tmp.append("}");
	  	           tmp.append(",");
//	            Element child = new Element("TreeNode");
//	            System.out.println(userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid")));	
//	            child.setAttribute("id", rset.getString("operationcode"));
//	            child.setAttribute("text", rset.getString("operationcode")+":"+rset.getString("operationname"));
//	            child.setAttribute("title", rset.getString("operationname"));
//	            child.setAttribute("target", "mil_body");	
//    			child.setAttribute("icon","/images/overview_obj.gif");
//    			child.setAttribute("xml","/agent/proxy_source?isRoot=0&isChild=1&operationcode="+rset.getString("operationcode")+"&u_type="+unit_type+"&type="+type+"&res_flag="+res_flag+"&href=1");
//    			root.addContent(child);
	        }
			
		}else if(warn_id.indexOf("#")!=-1){
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
    		String unit_type=null;
    		unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
    		if(unit_type==null|| "".equals(unit_type))
    			unit_type="3";
			String res_flag=warn_id.substring(0, warn_id.indexOf("#"));
			String res_desc = "";
			if("7".equals(res_flag)){
				res_desc = "rsbd";
			}else if("8".equals(res_flag)){
				res_desc = "gzbd";
			}else if("17".equals(res_flag)){
				res_desc = "ins_bd";
			}
			warn_id = warn_id.substring(warn_id.indexOf("#")+1);
			StringBuffer strsql=new StringBuffer();
    		strsql.append("select tabid,name from template_table where operationcode='");
    		strsql.append(warn_id);
    		strsql.append("' and (");
    		String units[]=unit_type.split(",");
    		for(int r=0;r<units.length;r++)
    		{
    			strsql.append(" flag ="+Integer.parseInt(units[r]));
    			if(r<units.length-1)
    				strsql.append(" or ");
    		}			
    		strsql.append(")");
    		rset1=dao.search(strsql.toString());
    		while(rset1.next())
			{
			
	            if(userView.isHaveResource(Integer.parseInt(res_flag),rset1.getString("tabid"))){
	            	tmp.append("{id:'");
	   	           tmp.append(res_desc+"#"+rset1.getString("tabid"));
	   	           tmp.append("',text:'");
	   	           tmp.append(rset1.getString("name"));
	   	           tmp.append("'");
	   	           //warn_priv_str  找到当前xml节点对应的文本
	   	           HashMap warnPrivMap = (HashMap) threadLocal.get();
	   	           String warn_priv_str = (String)warnPrivMap.get(res_desc);
	   	           if(warn_priv_str!=null && haveTheFunc(warn_priv_str,rset1.getString("tabid")))
	 	           {
	 		          tmp.append(",checked:true");	        	  
	 	           }
	 	           else
	 	           {
	 	        	  tmp.append(",checked:false");
	 	           }
	   	           tmp.append(",leaf:true");
	   	           tmp.append(",icon:'/images/overview_obj.gif'");
	   	           tmp.append(",href:'javascript:void(0);'");
	   	           tmp.append("}");
	   	           tmp.append(",");
	            }
//  	         Element child = new Element("TreeNode");
//	            System.out.println(userView.isHaveResource(res_flag,rset1.getString("tabid")));	
//	            child.setAttribute("id", rset1.getString("tabid"));
//	            child.setAttribute("text", rset1.getString("tabid")+":"+rset1.getString("name"));
//	            child.setAttribute("title", rset1.getString("name"));
//	            child.setAttribute("target", "mil_body");	
//    			child.setAttribute("icon","/images/overview_obj.gif");
//    			root.addContent(child);
	        }
		}
			 if(tmp.length()>0)
		        {
		        	tmp.setLength(tmp.length()-1);
		        	buf.append("[");
		        	buf.append(tmp.toString());
		        	buf.append("]");
		        }
	   }catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			;
			try
			{
				if(rset!=null)
					rset.close();
				if(rset1!=null)
					rset1.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		return buf.toString();
    
		
	}
	/**
	 * 从功能授权配置的文件取得所有的功能编码
	 * @param userView
	 * @param conn
	 * @param func_str
	 * @return
	 * @throws GeneralException
	 */
    private String searchFunctionXmlHtml(UserView userView,Connection conn,String func_str,String curr_id,String agent_func_str)throws GeneralException
    {
        InputStream in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/function.xml");
        StringBuffer buf=new StringBuffer();
        try
        {
	        Document doc = PubFunc.generateDom(in);
	        Element root = doc.getRootElement();
	        List list=null;
	        if("root".equalsIgnoreCase(curr_id))
	        {
	        	list = root.getChildren("function");
	        }
	        else
	        {
	        	String xpath = "//function[@id=\"" + curr_id + "\"]";
	        	XPath xpath_ = XPath.newInstance(xpath);
	        	Element ele = (Element) xpath_.selectSingleNode(doc);
	        	if(ele!=null)
	        	  list = ele.getChildren("function");
	        	else
	        	{
	        		xpath = "//function";
		        	xpath_ = XPath.newInstance(xpath);
		        	ele = (Element) xpath_.selectSingleNode(doc);
		        	if(ele!=null)
			        	  list = ele.getChildren("function");
	        	}
	        }
	        /**版本之间的差异控制，市场考滤*/
	        VersionControl ver_ctrl=new VersionControl();
	        ver_ctrl.setVer(this.lock.getVersion());
	        StringBuffer tmp=new StringBuffer();	      
	        if("root".equalsIgnoreCase(curr_id))
	        {
	        	for (int i = 0; i < list.size(); i++)
		        {
	        		   Element node = (Element) list.get(i);
			           String func_id=node.getAttributeValue("id");
			           if(func_id!=null&& "0".equals(func_id))
			           {
			        	   if(!this.functionTree.isMayOut(func_id,userView.getUserName()))
					        	  continue;
					           /**版本控制*/
					       if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
					        	  continue;
					           /**
					           * 支持分布式授权机制
					           */
					         if(!userView.hasTheFunction(node.getAttributeValue("id")))
					              continue;
					           tmp.append("{id:'");
					           tmp.append(node.getAttributeValue("id"));
					           tmp.append("',text:'");
					           tmp.append(node.getAttributeValue("name"));
					           tmp.append("'");
					           if(haveTheFunc(agent_func_str,node.getAttributeValue("id")))
					           {
						          tmp.append(",checked:true");	        	  
					           }
					           else
					           {
					        	  tmp.append(",checked:false");
					           }	
					           tmp.append(",leaf:false");
					           tmp.append("}");
					           tmp.append(",");
			           }
			           
			           
		        }
	        }else if("0".equalsIgnoreCase(curr_id))//如果点开的是自助平台
	        {
	        	for (int i = 0; i < list.size(); i++)
		        {
		           Element node = (Element) list.get(i);
		           String func_id=node.getAttributeValue("id");
		           if(func_id!=null&&("06".equals(func_id)|| "0C".equals(func_id)))//绩效考评，部门考勤
		           {
		        	   if(!this.functionTree.isMayOut(func_id,userView.getUserName()))
				        	  continue;
				           /**版本控制*/
				       if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
				        	  continue;
				           /**
				           * 支持分布式授权机制
				           */
				           if(!userView.hasTheFunction(node.getAttributeValue("id")))
				              continue;
				           tmp.append("{id:'");
				           tmp.append(node.getAttributeValue("id"));
				           tmp.append("',text:'");
				           tmp.append(node.getAttributeValue("name"));
				           tmp.append("'");
				           if(haveTheFunc(agent_func_str,node.getAttributeValue("id")))
				           {
					          tmp.append(",checked:true");	        	  
				           }
				           else
				           {
				        	  tmp.append(",checked:false");
				           }	
				           tmp.append(",leaf:false");
				           tmp.append("}");
				           tmp.append(",");
		           }
		           

		        } 
	        	//增加业务申请模块
	        	tmp.append(getFunc("0107",userView,ver_ctrl,agent_func_str,doc));
	        	  
	        	//增加OKR模块
	        	tmp.append(getFunc("0KR0101",userView,ver_ctrl,agent_func_str,doc));
	        	tmp.append(getFunc("0KR0102",userView,ver_ctrl,agent_func_str,doc));
	        	
	        	
	        	
	        }else
	        {
	        	for (int i = 0; i < list.size(); i++)
		        {
		           Element node = (Element) list.get(i);
		           String func_id=node.getAttributeValue("id");
		           if(!this.functionTree.isMayOut(func_id,userView.getUserName()))
			        	  continue;
			           /**版本控制*/
			       if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
			        	  continue;
			           /**
			           * 支持分布式授权机制
			           */
			       if(!userView.hasTheFunction(node.getAttributeValue("id")))
			            continue;
			       //if(haveTheFunc(func_str,node.getAttributeValue("id")))
			       {
			    	   tmp.append("{id:'");
			           tmp.append(node.getAttributeValue("id"));
			           tmp.append("',text:'");
			           tmp.append(node.getAttributeValue("name"));
			           tmp.append("'");
			           if(haveTheFunc(agent_func_str,node.getAttributeValue("id")))
			           {
				          tmp.append(",checked:true");	        	  
			           }
			           else
			           {
			        	  tmp.append(",checked:false");
			           }	
			           tmp.append(",leaf:false");
			           tmp.append("}");
			           tmp.append(",");
			       }
			           
		        } 
	        }
	        //for i loop end.
	        if(tmp.length()>0)
	        {
	        	tmp.setLength(tmp.length()-1);
	        	buf.append("[");
	        	buf.append(tmp.toString());
	        	buf.append("]");
	        }
	        
        }
        catch(Exception ee)
        {
            throw GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
            PubFunc.closeIoResource(in);
        }
        return buf.toString();

    }
    
    
    private String getFunc(String funcid,UserView userView,VersionControl ver_ctrl,String agent_func_str,Document doc)
    {
    	StringBuffer tmp=new StringBuffer("");
    	try
    	{
	    	String xpath = "//function[@id=\""+funcid+"\"]";
	    	XPath xpath_ = XPath.newInstance(xpath);
	    	Element ele = (Element) xpath_.selectSingleNode(doc);
	    	if(ele!=null) 
	        {
	           Element node =ele;
	           String func_id=node.getAttributeValue("id");
	           
	           {
	        	   if(!this.functionTree.isMayOut(func_id,userView.getUserName()))
	        	   {
	        		   
	        	   }
			       /**版本控制*/
	        	   else if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
	        	   {
	        		   
	        	   }
		           /**
		           * 支持分布式授权机制
		           */
	        	   else if(!userView.hasTheFunction(node.getAttributeValue("id")))
	        	   {
	        		   
	        	   }
	        	   else
	        	   {
			           tmp.append("{id:'");
			           tmp.append(node.getAttributeValue("id"));
			           tmp.append("',text:'");
			           tmp.append(node.getAttributeValue("name"));
			           tmp.append("'");
			           if(haveTheFunc(agent_func_str,node.getAttributeValue("id")))
			           {
				          tmp.append(",checked:true");	        	  
			           }
			           else
			           {
			        	  tmp.append(",checked:false");
			           }	
			           tmp.append(",leaf:false");
			           tmp.append("}");
			           tmp.append(",");
	        	   }
	           }
	        } 
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return tmp.toString();
    }
    
    
    
    
    /**
     * 当前对象是否有
     * @param func_str ，用户已授权的功能串列如 ,2020,30,
     * @param func_id
     * @return
     */
    private boolean haveTheFunc(String func_str,String func_id)
    {
    	if(func_str.indexOf(","+func_id+",")==-1)
    		return false;
    	else
    		return true;
    }
   
}
