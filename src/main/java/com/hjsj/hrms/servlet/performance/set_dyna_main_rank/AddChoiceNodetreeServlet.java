package com.hjsj.hrms.servlet.performance.set_dyna_main_rank;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddChoiceNodetreeServlet extends HttpServlet {

	private Category cat = Category.getInstance(this.getClass());
	private String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	private static ThreadLocal threadLocal = new ThreadLocal(); 
	
    /** 获得管理权限范围内的人员 */
    public String getPrivEmpStr(UserView u)
    {

	StringBuffer buf = new StringBuffer(" and object_id in (");
	String priStrSql = InfoUtils.getWhereINSql(u, "Usr");
	buf.append("select usra01.A0100 ");
	if (priStrSql.length() > 0)
	    buf.append(priStrSql);
	else
	    buf.append(" from usra01");	
	buf.append(")");
	return buf.toString();
    }
	
	
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { 
    	Map paramMap = new HashMap();
		threadLocal.set(paramMap);
		paramMap.put("b0110set", new HashSet());
    	StringBuffer sbXml = new StringBuffer();
        String params=req.getParameter("params");
        String parentid=req.getParameter("parentid");
        String action=req.getParameter("action");
        String target=req.getParameter("target");
        String planid = req.getParameter("planid");
        String object_type = req.getParameter("object_type");
        String codesetid = req.getParameter("codesetid");
        String employ = req.getParameter("employ")==null?"":req.getParameter("employ");
        String codeitemid = req.getParameter("codeitemid");
        String template_id = req.getParameter("template_id");
        String privCode = req.getParameter("privCode"); 
        paramMap.put("privCode", privCode);
        String username = req.getParameter("username");
        paramMap.put("username", username);
		String password = req.getParameter("password");
		paramMap.put("password", password);
//	     privCodeStr = getPrivCodeStr(privCode);
		  
		 username =  SafeCode.decode(username);
		 Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			UserView u = new UserView(username, password, conn);
			u.canLogin();
			String privCodeStr = this.getPrivEmpStr(u);
			paramMap.put("privCodeStr", privCodeStr);
			username =  SafeCode.encode(username);
			
			if("2".equals(object_type)){
				List rs=new ArrayList();
				String object_sql = "select object_id from per_object where plan_id = '"+planid+"' "+privCodeStr;
				rs=ExecuteSQL.executeMyQuery(object_sql);
				if(rs.isEmpty())
					sbXml.append(loadRootItemNodes("root",parentid,action,target,planid,object_type,codesetid,template_id));
				else{
					if("3".equals(employ)|| "".equals(employ))
						sbXml.append(loadOrgItemNodes(params,parentid,action,target,planid,object_type,codesetid,template_id));
					else if("1".equals(employ))
						sbXml.append(getEmploys(planid,codeitemid,action,template_id));
					else if("2".equals(employ))//部门下有部门也有人员
					    sbXml.append(getEmploysAndUM(codeitemid,params,parentid,action,target,planid,object_type,codesetid,template_id));
				}
			}
			else{
				 privCodeStr = getPrivCodeStr(privCode);
				sbXml.append(loadOrgItemNodes(params,parentid,action,target,planid,object_type,codesetid,template_id));
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}finally{
			try {
				if(conn!=null)//关闭数据库链接   zhaoxg  2014-4-18
					conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		  cat.debug("catalog xml" + sbXml.toString());
		resp.setContentType("text/xml");
		resp.getWriter().println(sbXml.toString());
    }
    public String getPrivCodeStr(String privCode)
    {
	StringBuffer sql = new StringBuffer();
	if (privCode != null && privCode.trim().length() > 0)
	{
//	    String codesetid = privCode.substring(0, 2);
	    String value = privCode.substring(2);
	    if (value.length() > 0)
	    {
//		if (codesetid.equalsIgnoreCase("UN"))
//		{
//		    sql.append(" and b0110 like '");
//		    sql.append(value);
//		    sql.append("%' ");
//		} else if (codesetid.equalsIgnoreCase("UM"))
//		{
//		    sql.append(" and e0122 like '");
//		    sql.append(value);
//		    sql.append("%' ");
//		} else if (codesetid.equalsIgnoreCase("@K"))
//		{
//		    sql.append(" and e01a1 like '");
//		    sql.append(value);
//		    sql.append("%' ");
//		}
		    sql.append(" and object_id like '");
		    sql.append(value);
		    sql.append("%' ");
	    }
	}
	return sql.toString();
    }
    
    private String  getEmploysAndUM(String codeitemid1,String params,String parentid,String action,String target,String planid,String object_type,String codesetido,String template_id) throws Exception
    {
    	HashMap paramMap = (HashMap) threadLocal.get();
    	HashSet b0110set = (HashSet) paramMap.get("b0110set");
    	String privCodeStr = (String) paramMap.get("privCodeStr");
    	String privCode = (String) paramMap.get("privCode");
    	String username = (String) paramMap.get("username");
    	String password = (String) paramMap.get("password");
    	StringBuffer strXml=new StringBuffer();
    	List rs=new ArrayList();
 	rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,parentid,planid,object_type,codesetido)); 
 	strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
    	if(!rs.isEmpty())
 	{
 		for(int i=0;i<rs.size();i++)
 		{
 		     TreeItemView treeitem=new TreeItemView();
 		     DynaBean rec=(DynaBean)rs.get(i);
 		     String org_type=rec.get("orgtype")!=null?rec.get("orgtype").toString():"";
 		     String image="admin.gif";
 		     
 		     String codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
 		     String codeitemdesc=rec.get("codeitemdesc")!=null?new String(rec.get("codeitemdesc").toString().replaceAll("&",	"&amp;").getBytes("GBK"),"ISO-8859-1"):"";
 		     String codesetid=rec.get("codesetid")!=null?rec.get("codesetid").toString():"";
 		     treeitem.setName(codesetid+codeitemid);
 		     treeitem.setText(codeitemdesc);
 	         treeitem.setTitle(codeitemdesc);  
 	         treeitem.setTarget(target);
 	         CodeItem item = AdminCode.getCode("UN",rec.get("childid").toString());
 	         if(item==null)
 	        	 item = AdminCode.getCode("UM",rec.get("childid").toString());
 	         if(item==null)
 	        	 item = AdminCode.getCode("@K",rec.get("childid").toString());
 	         if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
 	         {
 	            //if(!codeitemid.equalsIgnoreCase(rec.get("childid")!=null?rec.get("childid").toString():""))
 	         	    treeitem.setXml("/performance/implement/kh_mainbody/PremainTree?params=child&amp;parentid="  + codeitemid + "&amp;kind=2&amp;action=" + action + "&amp;target=" + target+"&amp;object_type="+object_type+"&amp;planid="+planid+"&amp;codesetid="+item.getCodeid()+"&amp;template_id="+template_id+"&amp;privCode="+privCode+"&amp;username="+username+"&amp;password="+password);
 	            if("vorg".equals(org_type))
 			    	 image="/images/admin.gif";
 	            else
 	            	image="/images/unit.gif";
 	            treeitem.setIcon(image);
 	            treeitem.setAction(action + "?b_search=link&amp;planid=" + planid+"&amp;codeid="+codesetid+codeitemid+"&amp;template_id="+template_id);
 	            strXml.append(treeitem.toChildNodeJS() + "\n");
             	Iterator it = b0110set.iterator();
             	while(it.hasNext()){
             		String ss = it.next().toString();
 	    			if(parentid.equalsIgnoreCase(ss)){
 	    				strXml.append(getBEmploys(planid,ss,action,template_id));
 	    			}
             	}
 	         }else if(rec.get("codesetid")!=null && "UM".equals(rec.get("codesetid")) /*&& !"noum".equals(treetype)*/){
 	        	 String xmlUrl ="/performance/implement/kh_mainbody/PremainTree?params=child&amp;parentid=" + codeitemid +	"&amp;kind=1&amp;action=" + action + "&amp;target=" + target+"&amp;object_type="+object_type+"&amp;planid="+planid+"&amp;codesetid="+item.getCodeid()+"&amp;codeitemid="+rec.get("codeitemid")+"&amp;template_id="+template_id+"&amp;privCode="+privCode+"&amp;username="+username+"&amp;password="+password;
 	        	 if(isHaveChildUM(codeitemid,planid) && isHavePeople(codeitemid,planid))//部门下有部门也有人员
 	        	     xmlUrl+="&amp;employ=2";
 	        	 else if(isHavePeople(codeitemid,planid))//部门下只有人员
 	        	     xmlUrl+="&amp;employ=1";
 	        	 else if(isHaveChildUM(codeitemid,planid) && !isHavePeople(codeitemid,planid))//部门下有部门没有人员
 	        	     xmlUrl+="&amp;employ=3";
 	        	 
 	        	 treeitem.setXml(xmlUrl);
 	         	if("vorg".equals(org_type))
 			    image="/images/admin.gif";
 	         	else
 	         	    image="/images/dept.gif";
 	         	treeitem.setIcon(image);
 	            treeitem.setAction(action + "?b_search=link&amp;planid=" + planid+"&amp;codeid="+codesetid+codeitemid+"&amp;template_id="+template_id);
 	            strXml.append(treeitem.toChildNodeJS() + "\n");
 	            Iterator it = b0110set.iterator();
             	while(it.hasNext()){
             		String ss = it.next().toString();
 	    			if(parentid.equalsIgnoreCase(ss)){
 	    				strXml.append(getBEmploys(planid,ss,action,template_id));
 	    			}
             	}
 	         }
 		}
// 		strXml.append("</TreeNode>\n");
// 		return strXml.toString();
 	}
    	else{
 		Iterator it = b0110set.iterator();
 		while(it.hasNext()){
 			String ss = it.next().toString();
 			if(parentid.equalsIgnoreCase(ss)){
 				strXml.append(getBEmploys(planid,ss,action,template_id));
 			}
 		}
    	}
    	//加人员
    	rs=new ArrayList();
	String object_sql = "select object_id from per_object where plan_id = '"+planid+"' and e0122 ='"+codeitemid1+"' "+privCodeStr;
	rs=ExecuteSQL.executeMyQuery(object_sql);
	ArrayList useridlist = new ArrayList();
	for(int i=0;i<rs.size();i++){
		DynaBean rec=(DynaBean)rs.get(i);
		useridlist.add(rec.get("object_id"));
	}
	StringBuffer strsql= new StringBuffer();
	strsql.append(" select a0100,a0101 from UsrA01 where a0100 in(");
	for(int i=0;i<useridlist.size();i++){
		strsql.append("'"+useridlist.get(i)+"',");
	}
	strsql.setLength(strsql.length()-1);
	strsql.append(")");
	rs.clear();
	rs=ExecuteSQL.executeMyQuery(strsql.toString());
	
	if(!rs.isEmpty())
	{

		for(int i=0;i<rs.size();i++)
		{
			TreeItemView treeitem=new TreeItemView();
			DynaBean rec=(DynaBean)rs.get(i);
			String title = new String(rec.get("a0101").toString().getBytes("GBK"),"ISO-8859-1");
			treeitem.setName("Usr"+rec.get("a0100"));
			treeitem.setText(title);
			treeitem.setTitle(title);
			treeitem.setTarget("mil_body");
			treeitem.setIcon("/images/man.gif");
			treeitem.setAction(action+"?b_search=link&amp;planid=" + planid+"&amp;codeid="+rec.get("a0100")+"&amp;template_id="+template_id);
			strXml.append(treeitem.toChildNodeJS() + "\n");
		}    	
	}    	
    	
     strXml.append("</TreeNode>\n");
 	return strXml.toString();

 }
    private String loadRootItemNodes(String params,String parentid,String action,String target,String planid,String object_type,String codesetido,String template_id)throws Exception
    {
    	StringBuffer strXml=new StringBuffer();
    	List rs=new ArrayList();
    	rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,parentid,planid,object_type,codesetido)); 
       	if(!rs.isEmpty())
    	{
    		strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
    		for(int i=0;i<rs.size();i++)
    		{
    		     TreeItemView treeitem=new TreeItemView();
    		     DynaBean rec=(DynaBean)rs.get(i);
    		     String org_type=rec.get("orgtype")!=null?rec.get("orgtype").toString():"";
    		     String image="admin.gif";
    		     
    		     String codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
    		     String codeitemdesc=rec.get("codeitemdesc")!=null?new String(rec.get("codeitemdesc").toString().replaceAll("&",	"&amp;").getBytes("GBK"),"ISO-8859-1"):"";
    		     String codesetid=rec.get("codesetid")!=null?rec.get("codesetid").toString():"";
    		     treeitem.setName(codesetid+codeitemid);
    		     treeitem.setText(codeitemdesc);
    	         treeitem.setTitle(codeitemdesc);  
    	         treeitem.setTarget(target);
    	         if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
    	         {
    	            
    	            image="/images/unit.gif";
    	            treeitem.setIcon(image);
    	            treeitem.setAction(action + "?b_search=link&amp;planid=" + planid+"&amp;codeid="+codesetid+codeitemid+"&amp;template_id="+template_id);
    	            strXml.append(treeitem.toChildNodeJS() + "\n");
    	         }
    		}
    		strXml.append("</TreeNode>\n");
    		return strXml.toString();
    	}
    	return strXml.toString();
    }
    
   private String  loadOrgItemNodes(String params,String parentid,String action,String target,String planid,String object_type,String codesetido,String template_id) throws Exception
   {
		HashMap paramMap = (HashMap) threadLocal.get();
	   	HashSet b0110set = (HashSet) paramMap.get("b0110set");
	   	String privCode = (String) paramMap.get("privCode");
	   	String username = (String) paramMap.get("username");
	   	String password = (String) paramMap.get("password");
	   	StringBuffer strXml=new StringBuffer();
	   	List rs=new ArrayList();
	rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,parentid,planid,object_type,codesetido)); 
	strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
   	if(!rs.isEmpty())
	{
		//strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
		for(int i=0;i<rs.size();i++)
		{
		     TreeItemView treeitem=new TreeItemView();
		     DynaBean rec=(DynaBean)rs.get(i);
		     String org_type=rec.get("orgtype")!=null?rec.get("orgtype").toString():"";
		     String image="admin.gif";
		     
		     String codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
		     String codeitemdesc=rec.get("codeitemdesc")!=null?new String(rec.get("codeitemdesc").toString().replaceAll("&",	"&amp;").getBytes("GBK"),"ISO-8859-1"):"";
		     String codesetid=rec.get("codesetid")!=null?rec.get("codesetid").toString():"";
		     treeitem.setName(codesetid+codeitemid);
		     treeitem.setText(codeitemdesc);
	         treeitem.setTitle(codeitemdesc);  
	         treeitem.setTarget(target);
	         CodeItem item = AdminCode.getCode("UN",rec.get("childid").toString());
	         if(item==null)
	        	 item = AdminCode.getCode("UM",rec.get("childid").toString());
	         if(item==null)
	        	 item = AdminCode.getCode("@K",rec.get("childid").toString());
	         if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
	         {
	            String itemcodeid = item.getCodeid();
	            boolean isHaveChildUN = isHaveChildUN(planid,codeitemid);
	            boolean isHaveChildUM=this.isHaveChildUM2(codeitemid, planid);
	            
	            if(isHaveChildUN && isHaveChildUM)
	        	itemcodeid="UMUN";
	            else if(isHaveChildUN)
	        	itemcodeid="UN";
	            else if (isHaveChildUM)
	        	itemcodeid="UM";
	            treeitem.setXml("/performance/implement/kh_mainbody/PremainTree?params=child&amp;parentid="  + codeitemid 
	            		+ "&amp;kind=2&amp;action=" + action + "&amp;target=" + target+"&amp;object_type="+object_type+"&amp;planid="
	            		+planid+"&amp;codesetid="+itemcodeid+"&amp;template_id="+template_id+"&amp;privCode="+privCode+"&amp;username="
	            		+username+"&amp;password="+password);
	            if("vorg".equals(org_type))
			    	 image="/images/admin.gif";
	            else
	            	image="/images/unit.gif";
	            treeitem.setIcon(image);
	            treeitem.setAction(action + "?b_search=link&amp;planid=" + planid+"&amp;codeid="+codesetid+codeitemid+"&amp;template_id="+template_id);
	            strXml.append(treeitem.toChildNodeJS() + "\n");
            	Iterator it = b0110set.iterator();
            	while(it.hasNext()){
            		String ss = it.next().toString();
	    			if(parentid.equalsIgnoreCase(ss)){
	    				strXml.append(getBEmploys(planid,ss,action,template_id));
	    			}
            	}
	         }else if(rec.get("codesetid")!=null && "UM".equals(rec.get("codesetid")) /*&& !"noum".equals(treetype)*/){
	        	 String xmlUrl ="/performance/implement/kh_mainbody/PremainTree?params=child&amp;parentid=" + codeitemid
	        			 + "&amp;kind=1&amp;action=" + action + "&amp;target=" + target+"&amp;object_type="+object_type
	        			 +"&amp;planid="+planid+"&amp;codesetid="+item.getCodeid()+"&amp;codeitemid="+rec.get("codeitemid")
	        			 +"&amp;template_id="+template_id+"&amp;privCode="+privCode+"&amp;username="+username+"&amp;password="+password;
	        	 if(isHaveChildUM(codeitemid,planid) && isHavePeople(codeitemid,planid))//部门下有部门也有人员
	        	     xmlUrl+="&amp;employ=2";
	        	 else if(isHavePeople(codeitemid,planid))//部门下只有人员
	        	     xmlUrl+="&amp;employ=1";
	        	 else if(isHaveChildUM(codeitemid,planid) && !isHavePeople(codeitemid,planid))//部门下有部门没有人员
	        	     xmlUrl+="&amp;employ=3";
	        	 
	        	 treeitem.setXml(xmlUrl);
	         	if("vorg".equals(org_type))
			    image="/images/admin.gif";
	         	else
	         	    image="/images/dept.gif";
	         	treeitem.setIcon(image);
	            treeitem.setAction(action + "?b_search=link&amp;planid=" + planid+"&amp;codeid="+codesetid+codeitemid+"&amp;template_id="+template_id);
	            strXml.append(treeitem.toChildNodeJS() + "\n");
	            Iterator it = b0110set.iterator();
            	while(it.hasNext()){
            		String ss = it.next().toString();
	    			if(parentid.equalsIgnoreCase(ss)){
	    				strXml.append(getBEmploys(planid,ss,action,template_id));
	    			}
            	}
	         }/*else if(rec.get("codesetid")!=null && rec.get("codesetid").equals("@K") && (!"org".equals(treetype) && !"noum".equals(treetype))){		     	
	         	//treeitem.setXml("/common/org/loadtree?params=child&amp;treetype="+ treetype + "&amp;parentid=" + codeitemid + "&amp;kind=0&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive  + "&amp;action=" + action + "&amp;target=" + target);
	        	 if(object_type.equals("2"))
	        		 treeitem.setXml("/performance/implement/kh_mainbody/PremainTree?params=child&amp;parentid=" + codeitemid +	"&amp;kind=1&amp;action=" + action + "&amp;target=" + target+"&amp;object_type="+object_type+"&amp;planid="+planid+"&amp;employ=1"+"&amp;codesetid="+item.getCodeid());
	            treeitem.setIcon("/images/pos_l.gif");
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);			        
	            else
	                treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=0");
	            strXml.append(treeitem.toChildNodeJS() + "\n");
	         }*/
		}
		strXml.append("</TreeNode>\n");
		return strXml.toString();
	}
   	else{
		Iterator it = b0110set.iterator();
		while(it.hasNext()){
			String ss = it.next().toString();
			if(parentid.equalsIgnoreCase(ss)){
				strXml.append(getBEmploys(planid,ss,action,template_id));
			}
		}
   	}
    strXml.append("</TreeNode>\n");
	return strXml.toString();

}

    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
            throws ServletException, IOException {
        doPost(arg0, arg1);
    }
	private String getLoadTreeQueryString(String params,String parentid,String planid,String object_type,String codesetid) {
		HashMap paramMap = (HashMap) threadLocal.get();
    	HashSet b0110set = (HashSet) paramMap.get("b0110set");
    	String privCodeStr = (String) paramMap.get("privCodeStr");
		StringBuffer strsql=new StringBuffer();
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'org' as orgtype ");
		strsql.append(" FROM organization WHERE ");
		strsql.append(Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
		
    	if(params!=null && "root".equals(params)){
    		strsql.append(" and codeitemid=parentid ");
    		List rs2=new ArrayList();
    		rs2=ExecuteSQL.executeMyQuery("select "+Sql_switcher.length("min(codeitemid)")+" b0110len from organization where codesetid='UN' and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
			int length = 0;
			if(!rs2.isEmpty()){
				DynaBean rec2=(DynaBean)rs2.get(0);
				String b0110len = rec2.get("b0110len").toString();
				length = Integer.parseInt(b0110len);
			}
    		List rs=new ArrayList();
    		String object_sql = "select * from per_object where plan_id = '"+planid+"' "+privCodeStr;
    		rs=ExecuteSQL.executeMyQuery(object_sql);
    		if(!rs.isEmpty()){
    			strsql.append(" and (");
    			HashMap temp = new HashMap();
    			for(int i=0;i<rs.size();i++){
    				DynaBean rec=(DynaBean)rs.get(i);
    				String b0110 = rec.get("b0110").toString();
    				if(b0110.length()>=length)
    					b0110 = b0110.substring(0,length);
    				if(temp.get(b0110)==null)
    				{
    					strsql.append("codeitemid='"+b0110+"' or ");
    					temp.put(b0110, b0110);
    				}
    				
    				if(rec.get("e0122").toString().length()<=0){
    					b0110set.add(rec.get("b0110").toString());
    				}
    			}
    			strsql.setLength(strsql.length()-3);
    			strsql.append(")");
    		}else
    		{
    			strsql.append(" and 1=2");
    		}
    	}
    	else
    	{
    		strsql.append(" and parentid='");
    		strsql.append(parentid);
    		strsql.append("'");
    		strsql.append(" AND codeitemid<>parentid ");
    		List rs=new ArrayList();
    		int length = parentid.length();
    		int childlength=0;
    		String object_sql = "select * from per_object where plan_id = '"+planid+"' "+privCodeStr;
    		rs=ExecuteSQL.executeMyQuery("select childid from organization where codeitemid ='"+parentid+"' and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
    		if(!rs.isEmpty()){
    				DynaBean rec=(DynaBean)rs.get(0);
    				String childid = rec.get("childid").toString();
    				childlength = childid.length();
    			}
    		rs=ExecuteSQL.executeMyQuery(object_sql);
    		
    		if(!"2".equals(object_type)){
	    		if(!rs.isEmpty()){
	    			strsql.append(" and (");
	    			HashMap temp = new HashMap();
	    			for(int i=0;i<rs.size();i++){
	    				DynaBean rec=(DynaBean)rs.get(i);
	    				String object_id = (String)rec.get("object_id");
	    				
	    			         CodeItem item = AdminCode.getCode("UN",object_id);
	    			         if(item==null)
	    			        	 item = AdminCode.getCode("UM",object_id);
	    			        
	    			     if("UN".equalsIgnoreCase(codesetid))
	    			     {	    				
	    				    String b0110 = (String)rec.get("b0110");
    	    				    if(b0110.length()>length)
    	    					   b0110 = b0110.substring(0,childlength);
    	    				    
    	    					if(temp.get(b0110)==null)
    	        				{
    	        					strsql.append("codeitemid='"+b0110+"' or ");
    	        					temp.put(b0110, b0110);
    	        				}
    	    				    
    	    				    while("UN".equalsIgnoreCase(this.getCodeSetId(this.getParentId(b0110))))
    	    				    {
    	    					b0110=this.getParentId(b0110);
    	    					if(temp.get(b0110)==null)
    	        				{
    	        					strsql.append("codeitemid='"+b0110+"' or ");
    	        					temp.put(b0110, b0110);
    	        				}
    	    				    }	    				
	    				
	    			     }else if("UM".equalsIgnoreCase(codesetid))
	    			     {
	    				 if("UM".equalsIgnoreCase(item.getCodeid()))
	    				 {
	    				     String e0122 = (String)rec.get("e0122");
	    				 	if(temp.get(e0122)==null)
	        				{
	        					strsql.append("codeitemid='"+e0122+"' or ");
	        					temp.put(e0122, e0122);
	        				}
	    	    				while("UM".equalsIgnoreCase(this.getCodeSetId(this.getParentId(e0122))))
	    	    				{
	    	    				    e0122=this.getParentId(e0122);
	    	    					if(temp.get(e0122)==null)
	    	        				{
	    	        					strsql.append("codeitemid='"+e0122+"' or ");
	    	        					temp.put(e0122, e0122);
	    	        				}
	    	    				}
	    				 }
	    			     }else if("UMUN".equalsIgnoreCase(codesetid))
	    			     {
	    				 String b0110 = (String)rec.get("b0110");
 	    				    if(b0110.length()>length)
 	    					   b0110 = b0110.substring(0,childlength);
 	    					if(temp.get(b0110)==null)
	        				{
	        					strsql.append("codeitemid='"+b0110+"' or ");
	        					temp.put(b0110, b0110);
	        				}
 	    				    while("UN".equalsIgnoreCase(this.getCodeSetId(this.getParentId(b0110))))
 	    				    {
 	    					b0110=this.getParentId(b0110);
 	    					if(temp.get(b0110)==null)
	        				{
	        					strsql.append("codeitemid='"+b0110+"' or ");
	        					temp.put(b0110, b0110);
	        				}
 	    				    }
 	    				    
 	    				 if("UM".equalsIgnoreCase(item.getCodeid()))
	    				 {
	    				     String e0122 = (String)rec.get("e0122");
	    				 	if(temp.get(e0122)==null)
	        				{
	        					strsql.append("codeitemid='"+e0122+"' or ");
	        					temp.put(e0122, e0122);
	        				}
	    	    				while("UM".equalsIgnoreCase(this.getCodeSetId(this.getParentId(e0122))))
	    	    				{
	    	    				    e0122=this.getParentId(e0122);
	    	    					if(temp.get(e0122)==null)
	    	        				{
	    	        					strsql.append("codeitemid='"+e0122+"' or ");
	    	        					temp.put(e0122, e0122);
	    	        				}
	    	    				}
	    				 } 	    				    
	    			     }
	    			 	if(temp.get(rec.get("object_id"))==null)
        				{
        					strsql.append("codeitemid='"+rec.get("object_id")+"' or ");
        					temp.put(rec.get("object_id"), rec.get("object_id"));
        				}
	    				
	    			}
	    			strsql.setLength(strsql.length()-3);
	    			strsql.append(")");
	    		}else
	    		{
	    			strsql.append(" and 1=2");
	    		}
    		}else if("2".equals(object_type)){
    			if(!rs.isEmpty()){
    				HashMap temp = new HashMap();
    				strsql.append(" and (");
    				if("UN".equalsIgnoreCase(codesetid)){
    					for(int i=0;i<rs.size();i++){
    	    				DynaBean rec=(DynaBean)rs.get(i);
    	    				String b0110 = rec.get("b0110").toString();
    	    				if(b0110.length()>length)
    	    					b0110 = b0110.substring(0,childlength);
    	    				
    	    				if(temp.get(b0110)==null)
	        				{
	        					strsql.append("codeitemid='"+b0110+"' or ");
	        					temp.put(b0110, b0110);
	        				}

    	    			      while("UN".equalsIgnoreCase(this.getCodeSetId(this.getParentId(b0110))))
	    				{
    	    			    	 b0110=this.getParentId(b0110);
    	    			 		if(temp.get(b0110)==null)
    	        				{
    	        					strsql.append("codeitemid='"+b0110+"' or ");
    	        					temp.put(b0110, b0110);
    	        				}
	    				}    	    				
    	    			}
    					strsql.setLength(strsql.length()-3);
    	    			strsql.append(")");
    				}else if("UM".equalsIgnoreCase(codesetid)){
    					for(int i=0;i<rs.size();i++){
    	    				DynaBean rec=(DynaBean)rs.get(i);
    	    				String e0122 = rec.get("e0122").toString();
    	    				
    	    				if(temp.get(e0122)==null)
	        				{
	        					strsql.append("codeitemid='"+e0122+"' or ");
	        					temp.put(e0122, e0122);
	        				}
    	    				
    	    				while("UM".equalsIgnoreCase(this.getCodeSetId(this.getParentId(e0122))))
    	    				{
    	    				    e0122=this.getParentId(e0122);
    	    					if(temp.get(e0122)==null)
    	        				{
    	        					strsql.append("codeitemid='"+e0122+"' or ");
    	        					temp.put(e0122, e0122);
    	        				}
    	    				}
    	    			}
    					strsql.setLength(strsql.length()-3);
    	    			strsql.append(")");
    				}else if("UMUN".equalsIgnoreCase(codesetid)){
					for(int i=0;i<rs.size();i++){
	    	    				DynaBean rec=(DynaBean)rs.get(i);
	    	    				String e0122 = rec.get("e0122").toString();
	    	    				if(temp.get(e0122)==null)
		        				{
		        					strsql.append("codeitemid='"+e0122+"' or ");
		        					temp.put(e0122, e0122);
		        				}
	    	    				while("UM".equalsIgnoreCase(this.getCodeSetId(this.getParentId(e0122))))
	    	    				{
	    	    				    e0122=this.getParentId(e0122);
	    	    					if(temp.get(e0122)==null)
	    	        				{
	    	        					strsql.append("codeitemid='"+e0122+"' or ");
	    	        					temp.put(e0122, e0122);
	    	        				}
	    	    				}
	    	    			}
					for(int i=0;i<rs.size();i++){
	    	    				DynaBean rec=(DynaBean)rs.get(i);
	    	    				String b0110 = rec.get("b0110").toString();
	    	    				if(b0110.length()>length)
	    	    					b0110 = b0110.substring(0,childlength);
	    	    				if(temp.get(b0110)==null)
		        				{
		        					strsql.append("codeitemid='"+b0110+"' or ");
		        					temp.put(b0110, b0110);
		        				}
	    	    			      while("UN".equalsIgnoreCase(this.getCodeSetId(this.getParentId(b0110))))
		    				{
	    	    			    	 b0110=this.getParentId(b0110);
	    	    			    		if(temp.get(b0110)==null)
	    		        				{
	    		        					strsql.append("codeitemid='"+b0110+"' or ");
	    		        					temp.put(b0110, b0110);
	    		        				}
		    				}    	    				
	    	    			}					
	    					strsql.setLength(strsql.length()-3);
	    	    			strsql.append(")");
	    				}
    				
    				
    				
    				
    				/*else if(codesetid.equalsIgnoreCase("@K")){
    					for(int i=0;i<rs.size();i++){
    	    				DynaBean rec=(DynaBean)rs.get(i);
    	    				String e01a1 = rec.get("e01a1").toString();
    	    				strsql.append("'"+e01a1+"',");
    	    			}
    					strsql.setLength(strsql.length()-1);
    	    			strsql.append(")");
    				}*/
    			}else
        		{
        			strsql.append(" and 1=2");
        		}
    		}
    	}
    	
    	strsql.append(" ORDER BY a0000,codeitemid ");
		return strsql.toString();
	}
	
	    public String getParentId(String codeitemid)
	    {
		Connection conn = null;
		ResultSet rs = null;
		String parentId = "";
		
		String sql = "select parentid from organization where parentid!=codeitemid and  codeitemid='" + codeitemid + "' and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(sql);
		    if (rs.next())
		    {
			String code = rs.getString("parentid");
			if (code != null)
			    parentId = code;
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if(rs!=null)
			rs.close();
			if(conn!=null)
			conn.close();
		    } catch (SQLException e)
		    {
			e.printStackTrace();
		    }
		}
		return parentId;
	    }
	
	    public String getCodeSetId(String codeitemid)
	    {
		Connection conn = null;
		ResultSet rs = null;
		String codesetid = "";
		
		String sql = "select codesetid from organization where  codeitemid='" + codeitemid + "' and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(sql);
		    if (rs.next())		    
			codesetid= rs.getString("codesetid");		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if(rs!=null)
			rs.close();
			if(conn!=null)
			conn.close();
		    } catch (SQLException e)
		    {
			e.printStackTrace();
		    }
		}
		return codesetid;
	    }
	    
	public  boolean  isHaveChildUN(String planid,String unCode)
	{
	    boolean isHave = false;
	    Connection conn = null;
		ResultSet rs = null;
		HashMap paramMap = (HashMap) threadLocal.get();
    	String privCodeStr = (String) paramMap.get("privCodeStr");
		String object_sql = "select * from per_object where plan_id = '"+planid+"' "+privCodeStr;
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(object_sql);
		    while (rs.next())	
		    {
			String b0110 = rs.getString("b0110");	
			    while("UN".equalsIgnoreCase(this.getCodeSetId(this.getParentId(b0110))))
				{
				   b0110=this.getParentId(b0110);
				    if(b0110.equalsIgnoreCase(unCode))
					return true;
				} 			    
		    }
					    
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if(rs!=null)
			rs.close();
			if(conn!=null)
			conn.close();
		    } catch (SQLException e)
		    {
			e.printStackTrace();
		    }
		}
	    return isHave;
	}
	    
	    
	public boolean isHaveChildUM(String umCode,String planid)
	{
	    boolean isHave = false;
	    Connection conn = null;
		ResultSet rs = null;
		HashMap paramMap = (HashMap) threadLocal.get();
    	String privCodeStr = (String) paramMap.get("privCodeStr");
		String object_sql = "select * from per_object where plan_id = '"+planid+"' "+privCodeStr;
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(object_sql);
		    while (rs.next())	
		    {
			String e0122 = rs.getString("e0122");	
			    while("UM".equalsIgnoreCase(this.getCodeSetId(this.getParentId(e0122))))
    				{
    				    e0122=this.getParentId(e0122);
    				    if(e0122.equalsIgnoreCase(umCode))
    					return true;
    				} 			    
		    }
					    
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if(rs!=null)
			rs.close();
		    } catch (SQLException e)
		    {
			e.printStackTrace();
		    }
		}
	    return isHave;
	}
	
	public boolean isHaveChildUM2(String unCode,String planid)
	{
	    boolean isHave = false;
	    Connection conn = null;
		ResultSet rs = null;
		HashMap paramMap = (HashMap) threadLocal.get();
    	String privCodeStr = (String) paramMap.get("privCodeStr");
		String object_sql = "select * from per_object where plan_id = '"+planid+"' "+privCodeStr;
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(object_sql);
		    while (rs.next())	
		    {
			String b0110 = rs.getString("b0110");	
			if(b0110.equalsIgnoreCase(unCode))
			    return true;			    
		    }
					    
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if(rs!=null)
			rs.close();
			if(conn!=null)
			conn.close();
		    } catch (SQLException e)
		    {
			e.printStackTrace();
		    }
		}
	    return isHave;
	}
	
	boolean isHavePeople(String umCode,String planid)
	{
	    boolean isHave = false;
	    Connection conn = null;
		ResultSet rs = null;
		HashMap paramMap = (HashMap) threadLocal.get();
    	String privCodeStr = (String) paramMap.get("privCodeStr");
		String object_sql = "select * from per_object where plan_id = '"+planid+"' and e0122='"+umCode+"' "+privCodeStr;
		try
		{
		    conn = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(conn);
		    rs = dao.search(object_sql);
		    if (rs.next())	
			isHave=true;					    
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
		    try
		    {
			if(rs!=null)
			rs.close();
			if(conn!=null)
			conn.close();
		    } catch (SQLException e)
		    {
			e.printStackTrace();
		    }
		}
	    return isHave;
	}
	
	private String getEmploys(String planid,String codeitemid,String action,String template_id) throws Exception
    {
		HashMap paramMap = (HashMap) threadLocal.get();
    	String privCodeStr = (String) paramMap.get("privCodeStr");
		List rs=new ArrayList();
		String object_sql = "select object_id from per_object where plan_id = '"+planid+"' and e0122 ='"+codeitemid+"' "+privCodeStr;
		rs=ExecuteSQL.executeMyQuery(object_sql);
		ArrayList useridlist = new ArrayList();
		for(int i=0;i<rs.size();i++){
			DynaBean rec=(DynaBean)rs.get(i);
			useridlist.add(rec.get("object_id"));
		}
		StringBuffer strsql= new StringBuffer();
		strsql.append(" select a0100,a0101 from UsrA01 where a0100 in(");
		for(int i=0;i<useridlist.size();i++){
			strsql.append("'"+useridlist.get(i)+"',");
		}
		strsql.setLength(strsql.length()-1);
		strsql.append(")");
		rs.clear();
		rs=ExecuteSQL.executeMyQuery(strsql.toString());
		StringBuffer strXml=new StringBuffer();
		
		if(!rs.isEmpty())
		{
			strXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<TreeNode>\n");
			for(int i=0;i<rs.size();i++)
			{
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				String title = new String(rec.get("a0101").toString().getBytes("GBK"),"ISO-8859-1");
				treeitem.setName("Usr"+rec.get("a0100"));
				treeitem.setText(title);
				treeitem.setTitle(title);
				treeitem.setTarget("mil_body");
				treeitem.setIcon("/images/man.gif");
				treeitem.setAction(action+"?b_search=link&amp;planid=" + planid+"&amp;codeid="+rec.get("a0100")+"&amp;template_id="+template_id);
				strXml.append(treeitem.toChildNodeJS() + "\n");
			}
			strXml.append("</TreeNode>\n");
			return strXml.toString();
  		}
		return strXml.toString();
      }
	private String getBEmploys(String planid,String codeitemid,String action,String template_id) throws Exception
    {
		HashMap paramMap = (HashMap) threadLocal.get();
    	String privCodeStr = (String) paramMap.get("privCodeStr");
		List rs=new ArrayList();
		String object_sql = "select object_id from per_object where plan_id = '"+planid+"' and b0110 ='"+codeitemid+"' and e0122 is null "+privCodeStr;
		rs=ExecuteSQL.executeMyQuery(object_sql);
		ArrayList useridlist = new ArrayList();
		for(int i=0;i<rs.size();i++){
			DynaBean rec=(DynaBean)rs.get(i);
			useridlist.add(rec.get("object_id"));
		}
		StringBuffer strsql= new StringBuffer();
		strsql.append(" select a0100,a0101 from UsrA01 where a0100 in(");
		for(int i=0;i<useridlist.size();i++){
			strsql.append("'"+useridlist.get(i)+"',");
		}
		strsql.setLength(strsql.length()-1);
		strsql.append(")");
		rs.clear();
		rs=ExecuteSQL.executeMyQuery(strsql.toString());
		StringBuffer strXml=new StringBuffer();
		
		if(!rs.isEmpty())
		{
			for(int i=0;i<rs.size();i++)
			{
				TreeItemView treeitem=new TreeItemView();
				DynaBean rec=(DynaBean)rs.get(i);
				String title = new String(rec.get("a0101").toString().getBytes("GBK"),"ISO-8859-1");
				treeitem.setName("Usr"+rec.get("a0100"));
				treeitem.setText(title);
				treeitem.setTitle(title);
				treeitem.setTarget("mil_body");
				treeitem.setIcon("/images/man.gif");
				treeitem.setAction(action+"?b_search=link&amp;planid=" + planid+"&amp;codeid="+rec.get("a0100")+"&amp;template_id="+template_id);
				strXml.append(treeitem.toChildNodeJS() + "\n");
			}
			return strXml.toString();
  		}
		return strXml.toString();
      }
}