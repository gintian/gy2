package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:LogonUserServlet</p>
 * <p>Description:查询用户对象</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-6:15:36:08</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class LogonUserServlet extends HttpServlet {
	
	private static ThreadLocal threadLocal = new ThreadLocal(); 
	
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map paramMap = new HashMap();
		threadLocal.set(paramMap);
		StringBuffer treexml = new StringBuffer();	
        String groupid=req.getParameter("groupid");
        String level0=req.getParameter("level0");
        /**控制定义用户还是选择用户,在对应的交易里用通过传参数*/
        String flag=req.getParameter("flag");
        if(flag==null|| "".equals(flag))
        	flag="1";
        req.setCharacterEncoding("GBK");
        String username=req.getParameter("username");
        
        String treeselecttype = req.getParameter("treeselecttype");
        if(treeselecttype == null){
        	treeselecttype="";
        }
        paramMap.put("treeselecttype", treeselecttype);
        
        //树节点是否显示全名 0：不显示  1：显示
        if(req.getParameter("isShowFullName")!=null)
        	paramMap.put("isShowFullName", req.getParameter("isShowFullName"));
        else
        	paramMap.put("isShowFullName", "0");      
        //薪资类别id   薪资报批时用到，只显示有当前薪资类别权限的用户
        String salaryid = "";
        if(req.getParameter("salaryid")!=null&&!"undefined".equalsIgnoreCase(salaryid))
        	salaryid=req.getParameter("salaryid");
        paramMap.put("salaryid", salaryid);      
        
        String selfusername=req.getParameter("selfusername");
        if(selfusername==null)
        	selfusername="-1";
        paramMap.put("selfusername", selfusername);
        //时间间隔在account_logon_interval=xx单位分钟内连续登录做登录锁定控制，默认不做登录锁定控制
    	String account_logon_interval=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.ACCOUNT_LOGON_INTERVAL);
    	String password_lock_days=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORD_LOCK_DAYS);
    	paramMap.put("account_logon_interval", account_logon_interval);
    	paramMap.put("password_lock_days", password_lock_days);
    	try 
		{
			if("0".equals(flag)) //定义用户时用
				treexml.append(loadUserNodes(groupid,level0));				
			else //选择用户
				treexml.append(loadUserNodes(groupid,username,level0));

		} catch (Exception e) {
			e.printStackTrace();
		}			
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(treexml.toString());        
	}
	/**
	 * 分析组下是否有用户
	 * @param groupid
	 * @return
	 */
	private boolean isHaveUser(String groupid,Connection conn)
	{
		boolean flag=false;
		StringBuffer buf=new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			buf.append("select username from operuser where groupid=");
			buf.append(groupid);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				flag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return flag;
	}
	/**
	 * 选择用户时，不用加链接操作和权限过滤
	 * @param groupid
	 * @param level0
	 * @return
	 */
	private String loadUserNodes(String groupid,String level0) {
		HashMap paramMap = (HashMap) threadLocal.get();
    	String treeselecttype = (String) paramMap.get("treeselecttype");
    	String selfusername = (String) paramMap.get("selfusername");
    	String isShowFullName = (String) paramMap.get("isShowFullName");
    	String salaryid = (String) paramMap.get("salaryid");
    	
		StringBuffer strcontent=new StringBuffer();
		StringBuffer strsql=new StringBuffer();
		Connection conn = null;	
		RowSet rset=null;
		RowSet rset2=null;
		String mygroupid=null;
		
		if(level0==null|| "1".equals(level0))
		{
			/**对超级用户组特殊处理*/
			if("1".equals(groupid))
			{
				strsql.append("select a0100,nbase,username,fullname,a.groupid as groupid, b.groupid as sss,userflag,roleid from operuser a left join usergroup b ");
				strsql.append(" on a.username=b.groupname ");
				strsql.append( "where a.groupid=");
				strsql.append(groupid);
				strsql.append(" and userflag='10'");
				if(!"-1".equals(selfusername))
					strsql.append(" and username<>'"+selfusername+"'");
				strsql.append(" order by sss,InGrpOrder");				
			}
			else
			{
				strsql.append("select a0100,nbase,username,fullname,a.groupid as groupid, b.groupid as sss,userflag,roleid from operuser a left join usergroup b ");
				strsql.append(" on a.username=b.groupname ");
				strsql.append( "where a.groupid=");
				strsql.append(groupid);
				if(!"-1".equals(selfusername))
					strsql.append(" and username<>'"+selfusername+"'");
				strsql.append(" order by sss,InGrpOrder");
			}
		}
		else
		{
			if("1".equals(groupid))
			{
				strsql.append("select a0100,nbase,username,fullname,a.groupid as groupid,b.groupid as sss,userflag,roleid from operuser a left join usergroup b ");
				strsql.append(" on a.username=b.groupname ");		
				strsql.append(" where roleid=1 and a.groupid="+groupid+" order by sss,InGrpOrder");
			}
			else
			{
				strsql.append("select a0100,nbase,username,fullname,a.groupid as groupid,b.groupid as sss,userflag,roleid from operuser a left join usergroup b ");
				strsql.append(" on a.username=b.groupname ");		
				strsql.append(" where roleid=1 and b.groupid="+groupid+" order by sss,InGrpOrder");				
			}
		}			
		try
		{
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			rset=dao.search(strsql.toString());
			int nroleid=0;
			String userflag=null;
	        Element root = new Element("TreeNode");
	        root.setAttribute("id","$$00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","root");
	        Document myDocument = new Document(root);
			while(rset.next())
			{
				nroleid=rset.getInt("roleid");
				String fullname=rset.getString("fullname")==null?"":rset.getString("fullname");
		//		if(isShowFullName.equals("1"))
		//			fullname=rset.getString("username")==null?"":rset.getString("username");
				//处理 ~ | & ^ 不显示问题    jingq add   2014.6.3
				fullname = fullname.replace("~", "～");
				fullname = fullname.replace("|", "l");
				fullname = fullname.replace("&", "＆");
				fullname = fullname.replaceAll("\\^", "︿");
	            Element child = new Element("TreeNode");
	            String un = rset.getString("username");
	            //child.setAttribute("id", rset.getString("username"));
	            String userName="";
	            if("1".equals(isShowFullName))
	            {
	            	userName = this.getA0101(rset.getString("nbase"), rset.getString("a0100"), dao);
	            	if(userName.length()==0){
			            if(rset.getString("fullname")!=null&&rset.getString("fullname").trim().length()>0){
			            	userName=rset.getString("fullname");
			            	userName = userName.replace("~", "～");
			            	userName = userName.replace("|", "l");
			            	userName = userName.replace("&", "＆");
			            	userName = userName.replaceAll("\\^", "︿");
			            }
			            else if(rset.getString("username")!=null&&rset.getString("username").trim().length()>0)
			            	userName=rset.getString("username");
	            	}
		           
	            }
	            else
	            {
	            	if(rset.getString("username")!=null&&rset.getString("username").trim().length()>0)
		            	userName=rset.getString("username");
	            }
	            
	            child.setAttribute("text",userName);
	            
	            child.setAttribute("title",fullname);
	            
	            if("1".equals(treeselecttype) && nroleid == 1){//复选且为用户组
	        	    child.setAttribute("id", "@"+un);
	            }else{
	            	child.setAttribute("id", un);
	            }
	            
	            mygroupid=rset.getString("sss");
	            userflag=rset.getString("userflag");
	            if("1".equals(rset.getString("roleid"))&&isHaveUser(mygroupid,conn))
	            	child.setAttribute("xml","/system/logonuser/search_user_servlet?encryptParam="+PubFunc.encrypt("level0=1&groupid="+mygroupid+"&treeselecttype="+treeselecttype+"&flag=0&salaryid="+salaryid+"&isShowFullName="+isShowFullName+"&selfusername="+selfusername));
	            /**超级用户享有全权*/
	            child.setAttribute("target", "mil_body");		            
	    		if(nroleid==1)
	    		{
	    			child.setAttribute("icon","/images/groups.gif");
	    			child.setAttribute("type","false");	    			
	    		}
	    		else
	    		{
	    			if(userflag==null|| "12".equals(userflag))
		    			child.setAttribute("icon","/images/not_admin.gif");	    				
	    			else
		    			child.setAttribute("icon","/images/admin.gif");	    				
	    		}	    
	    		
	    		
	    		boolean flag=true;
	    		//薪资报批中调用 只显示有该薪资类别 的用户。
	    		if(salaryid!=null&&salaryid.trim().length()>0&&nroleid!=1&&!"undefined".equalsIgnoreCase(salaryid))
	    		{
	    			String cstate="0";  //薪资
	    			rset2=dao.search("select cstate from salarytemplate where salaryid="+salaryid);
	    			if(rset2.next())
	    			{
	    				if(rset2.getString("cstate")!=null&& "1".equals(rset2.getString("cstate")))
	    					cstate="1";
	    			}
	    			UserView userView=new UserView(rset.getString("username"),conn);
	    			userView.canLogin(false);
	    			if("0".equals(cstate))
	    			{
	    				if(!userView.isHaveResource(IResourceConstant.GZ_SET, salaryid))
	    					flag=false;
	    			}
	    			else
	    			{
	    				if(!userView.isHaveResource(IResourceConstant.INS_SET, salaryid))
	    					flag=false;
	    			}
	    			
	    		}
	    		
	    		if(flag)
	    			root.addContent(child);
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			strcontent.append(outputter.outputString(myDocument));		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
				if(rset2!=null)
					rset2.close();
				if(conn!=null&&(!conn.isClosed()))
					conn.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return strcontent.toString();
	}	
	/**
	 * loading 用户组
	 * @param groupid
	 * @return
	 */
	private String loadUserNodes(String groupid,String username,String level0 ) {
		HashMap paramMap = (HashMap) threadLocal.get();
    	String isShowFullName = (String) paramMap.get("isShowFullName");
    	String salaryid = (String) paramMap.get("salaryid");
    	String account_logon_interval = (String) paramMap.get("account_logon_interval");
    	String password_lock_days = (String) paramMap.get("password_lock_days");
		StringBuffer strcontent=new StringBuffer();
		StringBuffer strsql=new StringBuffer();
		Connection conn = null;	
		RowSet rset=null;
		RowSet rset2=null;
		String mygroupid=null;
		boolean bflag=false;
		if(level0==null|| "1".equals(level0))
		{
			/**对超级用户组特殊处理*/
			if("1".equals(groupid))
			{
				strsql.append("select a0100,nbase,username,fullname,a.groupid as groupid, b.groupid as sss,userflag,roleid,state from operuser a left join usergroup b ");
				strsql.append(" on a.username=b.groupname ");
				strsql.append( "where a.groupid=");
				strsql.append(groupid);
				strsql.append(" and userflag='10'");
				strsql.append(" order by sss,InGrpOrder");				//ingrporder->sss
			}
			else
			{
				strsql.append("select a0100,nbase,username,fullname,a.groupid as groupid, b.groupid as sss,userflag,roleid,state from operuser a left join usergroup b ");
				strsql.append(" on a.username=b.groupname ");
				strsql.append( "where a.groupid=");
				strsql.append(groupid);
				strsql.append(" and  username<>'");
				strsql.append(username);
				strsql.append("'");
				
				strsql.append(" order by sss,InGrpOrder");
			}
		}
		else
		{
			bflag=true;
			if("1".equals(groupid))
			{
				strsql.append("select a0100,nbase,username,fullname,a.groupid as groupid,b.groupid as sss,userflag,roleid,state from operuser a left join usergroup b ");
				strsql.append(" on a.username=b.groupname ");		
				strsql.append(" where roleid=1 and a.groupid="+groupid+" order by sss,InGrpOrder");
			}
			else
			{
				strsql.append("select a0100,nbase,username,fullname,a.groupid as groupid,b.groupid as sss,userflag,roleid,state from operuser a left join usergroup b ");
				strsql.append(" on a.username=b.groupname ");		
				strsql.append(" where roleid=1 and b.groupid="+groupid+" order by sss,InGrpOrder");				
			}
		}
		try
		{
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			rset=dao.search(strsql.toString());
			int nroleid=0;
			String userflag=null;
	        Element root = new Element("TreeNode");
	        root.setAttribute("id","$$00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","root");
	        Document myDocument = new Document(root);
			while(rset.next())
			{
				nroleid=rset.getInt("roleid");
				String fullname=rset.getString("fullname")==null?"":rset.getString("fullname");
				Element child = new Element("TreeNode");
	            child.setAttribute("id", rset.getString("username"));
	            
	            String userName="";
	            int state = rset.getInt("state");
	            if("1".equals(isShowFullName))
	            {
	            	userName = this.getA0101(rset.getString("nbase"), rset.getString("a0100"), dao);
	            	if(userName.length()==0){
			            if(rset.getString("fullname")!=null&&rset.getString("fullname").trim().length()>0)
			            	userName=rset.getString("fullname");
			            else if(rset.getString("username")!=null&&rset.getString("username").trim().length()>0)
			            	userName=rset.getString("username");
	            	}
	            }
	            else
	            {
	            	if(rset.getString("username")!=null&&rset.getString("username").trim().length()>0)
		            	userName=rset.getString("username");
	            }
	            
	            child.setAttribute("text",userName);
	            child.setAttribute("title", fullname);
	            mygroupid=rset.getString("sss");
	            userflag=rset.getString("userflag");
	            if("1".equals(rset.getString("roleid"))/*&&isHaveUser(mygroupid,conn)*/)
	            	child.setAttribute("xml","/system/logonuser/search_user_servlet?encryptParam="+PubFunc.encrypt("level0=1&groupid="+mygroupid+"&salaryid="+salaryid+"&isShowFullName="+isShowFullName+"&username="+rset.getString("username")));
	            /**超级用户享有全权*/
	            child.setAttribute("target", "mil_body");		            
	            if((mygroupid!=null&& "1".equals(mygroupid)))
	            	child.setAttribute("href", "/system/logonuser/su_info.do");
	            else if(userflag!=null&& "10".equals(userflag))
	            	child.setAttribute("href", "/system/logonuser/su_info.do");	 
	            /**不允许修改所在组的权限*/
	            //bug 16391 根据不同权限提示不同的信息  update by hej  2016/2/4
	            else if(bflag&&mygroupid!=null&&mygroupid.equals(groupid))
	            	child.setAttribute("href", "/system/logonuser/su_info.do?flag=nogroup");	
	            /**不允许修改自己的权限*/
	            else if(rset.getString("username").equalsIgnoreCase(username))
	            	child.setAttribute("href", "/system/logonuser/su_info.do?flag=noself");		            
	            else{
	            	//child.setAttribute("href", "/system/security/assignpriv.do?b_query=link&a_flag=0&a_tab=funcpriv&role_id=" + rset.getString("username"));
	            
            		child.setAttribute("href", "/system/security/assignpriv_tab.do?br_query=link&encryptParam="+PubFunc.encrypt("rp=0&user_flag=0&a_tab=funcpriv&role_id=" + rset.getString("username")+"&role_name="+userName));
	            }
	    		if(nroleid==1)
	    		{
	    			child.setAttribute("icon","/images/groups.gif");
	    			child.setAttribute("type","false");
	    		}
	    		else
	    		{
	    			if(userflag==null|| "12".equals(userflag)){
	    				if(state==0&&(account_logon_interval.length()>0||password_lock_days.length()>0)){
	    					child.setAttribute("icon","/images/admin_lock.gif");
	    				}else{
	    					child.setAttribute("icon","/images/not_admin.gif");	
	    				}
	    			}else{
	    				if(state==0&&(account_logon_interval.length()>0||password_lock_days.length()>0)){
	    					child.setAttribute("icon","/images/admin_lock.gif");
	    				}else{
	    					child.setAttribute("icon","/images/admin.gif");	 
	    				}
	    			}
	    		}	 
	    		
	    		boolean flag=true;
	    		//薪资报批中调用 只显示有该薪资类别 的用户。
	    		if(salaryid!=null&&salaryid.trim().length()>0&&nroleid!=1&&!"undefined".equalsIgnoreCase(salaryid))
	    		{
	    			String cstate="0";  //薪资
	    			rset2=dao.search("select cstate from salarytemplate where salaryid="+salaryid);
	    			if(rset2.next())
	    			{
	    				if(rset2.getString("cstate")!=null&& "1".equals(rset2.getString("cstate")))
	    					cstate="1";
	    			}
	    			UserView userView=new UserView(rset.getString("username"),conn);
	    			userView.canLogin(false);
	    			if("0".equals(cstate))
	    			{
	    				if(!userView.isHaveResource(IResourceConstant.GZ_SET, salaryid))
	    					flag=false;
	    			}
	    			else
	    			{
	    				if(!userView.isHaveResource(IResourceConstant.INS_SET, salaryid))
	    					flag=false;
	    			}
	    			
	    		}
	    		if(flag)
	    			root.addContent(child);
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			strcontent.append(outputter.outputString(myDocument));		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
				if(rset2!=null)
					rset2.close();
				if(conn!=null&&(!conn.isClosed()))
					conn.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return strcontent.toString();
	}
	
	/**
	 * 关联自助用户，先显示自助人员姓名，再显示全称，再用户名
	 * @param nbase
	 * @param a0100
	 * @param dao
	 * @return
	 */
	private String getA0101(String nbase,String a0100,ContentDAO dao){
		String a0101="";
		if(!(nbase!=null&&nbase.length()==3))
			return a0101;
		RowSet rs = null;
		try{
			rs = dao.search("select a0101 from "+nbase+"a01 where a0100='"+a0100+"'");
			if(rs.next()){
				a0101=rs.getString("a0101");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return a0101;
	}
}
