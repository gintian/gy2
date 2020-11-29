package com.hjsj.hrms.module.frreport.servlet;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FrReportAddOrgTreeServlet extends HttpServlet {

	private  Category cat = Category.getInstance(this.getClass());
	
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { 
    	StringBuffer sbXml = new StringBuffer();
        String params=req.getParameter("params");
        String showCode = req.getParameter("showCode");
        String issuperuser=req.getParameter("issuperuser");
        String parentid=req.getParameter("parentid");
        String privType = req.getParameter("privType");
        String action=req.getParameter("action");
        action = PubFunc.keyWord_reback(action);
        action = PubFunc.decrypt(action);
//      action +="&amp;op=write&amp;__cutpage__=v";
        String target=req.getParameter("target");
        String treetype=req.getParameter("treetype");
        String orgtype=req.getParameter("orgtype");
        
        // 考勤组织机构树是否显示岗位
    	KqParameter para = new KqParameter();
    	boolean isPost = true;
    	if ("1".equalsIgnoreCase(para.getKq_orgView_post())) {
    		isPost = false;
    	} else {
    		isPost = true;
    	}
        
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = req.getParameter("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
        if(orgtype==null||orgtype.length()<=0)
        	orgtype="org";
        String nmodule = req.getParameter("nmodule");
        nmodule = nmodule==null?"":nmodule;
        privType = (privType==null?"U":privType);
        if(privType!=null&& "U".equalsIgnoreCase(privType)){
        	nmodule = "null";
        }else if(privType!=null&& "O".equalsIgnoreCase(privType)){
        	nmodule = "null";
        }else if(privType!=null){
        	nmodule = privType;
        }
        UserView userView = null;
        if(params!=null && "root".equals(params)){
        	userView = (UserView)req.getSession().getAttribute("userView");
        }
		try {
			sbXml.append(loadOrgItemNodes(privType,params,issuperuser,parentid,action,target,treetype,orgtype,backdate,nmodule,userView, isPost,showCode));
			
		} catch (Exception e) {
			System.out.println(e);
		}
		cat.debug("catalog xml" + sbXml.toString());
		resp.setContentType("text/xml;charset=gb2312");
		resp.getWriter().write(sbXml.toString());
		resp.getWriter().close();
    }
    
    private String  loadOrgItemNodes(String privType,String params,String issuperuser,String parentid,String action,String target,String treetype,String orgtype,String backdate,String nmodule,UserView userView, boolean isPost,String showCode) throws Exception
    {
    	StringBuffer strXml=new StringBuffer();
    	List rs=new ArrayList();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	if(orgtype==null||orgtype.length()<=0||!"vorg".equalsIgnoreCase(orgtype))
 	  rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(privType,params,issuperuser,parentid,backdate,nmodule,userView, isPost)); 
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
 		     String codeitemdesc=rec.get("codeitemdesc")!=null?com.hrms.frame.codec.SafeCode.encode(rec.get("codeitemdesc").toString()):"";
 		     String codesetid=rec.get("codesetid")!=null?rec.get("codesetid").toString():"";
 		     treeitem.setName(codesetid+codeitemid);
 		     treeitem.setText(codeitemdesc);
 	         treeitem.setTitle(codeitemdesc);  
 	         treeitem.setTarget(target);
 	        // System.out.println("ddd" + treetype);
 	         if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
 	         {
 	            if(!codeitemid.equalsIgnoreCase(rec.get("childid")!=null?rec.get("childid").toString():"")) {
 	            	treeitem.setXml("/servlet/FrReportAddOrgTreeServlet?orgCode="+codeitemid+"&amp;params=child&amp;treetype="+ treetype + "&amp;parentid="  + codeitemid + "&amp;kind=2&amp;issuperuser=" + issuperuser + "&amp;action=" +PubFunc.encrypt(action) + "&amp;target=" + target+"&amp;backdate="+backdate+"&amp;jump=1");
 	            }
 	         	    if("vorg".equals(org_type)&&!"vorg".equals(orgtype))
 			    	 image="/images/admin.gif";
 	            else{
 	            	if(sdf.parse(sdf.format(new Date())).compareTo(sdf.parse((String)rec.get("end_date")))<=0){
 	            		image="/images/unit.gif";
 	            	}else{
 	            		image="/images/b_unit.gif";
 	            	}
 	            }
 	            treeitem.setIcon(image);
 	            if("javascript:void(0)".equals(action))
 	            	treeitem.setAction(action);			
 	            else if("duty".equals(treetype))
 	            	treeitem.setAction("showerrorinfo.do?encryptParam="+PubFunc.encrypt("encryptParam="+PubFunc.encrypt("code=" + codeitemid + "&amp;kind=2&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1&amp;query=&amp;idordesc=")));
 	            else
 	                treeitem.setAction(action + "&amp;orgCode="+PubFunc.encrypt("UN"+codeitemid)+"&amp;encryptParam="+PubFunc.encrypt("code=" + codeitemid + "&amp;kind=2&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1&amp;query=&amp;idordesc="));
 	            strXml.append(treeitem.toChildNodeJS() + "\n");
 	         }else if(rec.get("codesetid")!=null && "UM".equals(rec.get("codesetid")) && !"noum".equals(treetype)){
 	        	//String childid=rec.get("childid")!=null?rec.get("childid").toString():"";
 	         	//if(!codeitemid.equalsIgnoreCase(childid))
 	        	   treeitem.setXml("/servlet/FrReportAddOrgTreeServlet?deptCode="+codeitemid+"&amp;params=child&amp;treetype="+ treetype + "&amp;parentid=" + codeitemid +	"&amp;kind=1&amp;issuperuser=" + issuperuser + "&amp;action=" + PubFunc.encrypt(action) + "&amp;target=" + target+"&amp;backdate="+backdate+"&amp;jump=1");
 	        	   if("vorg".equals(org_type)&&!"vorg".equals(orgtype))
 			    	 image="/images/admin.gif";
 	         	else{
 	            	if(sdf.parse(sdf.format(new Date())).compareTo(sdf.parse((String)rec.get("end_date")))<=0){
 	            		image="/images/dept.gif";
 	            	}else{
 	            		image="/images/b_dept.gif";
 	            	}
 	            }
 	         	treeitem.setIcon(image);
 	            if("javascript:void(0)".equals(action))
 	            	treeitem.setAction(action);	
 	            else if("duty".equals(treetype))
 	            	treeitem.setAction("showerrorinfo.do?b_search=link&amp;encryptParam="+PubFunc.encrypt("code=" + codeitemid + "&amp;kind=1&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1&amp;query=&amp;idordesc="));
 	            else
 	                treeitem.setAction(action + "&amp;orgCode="+PubFunc.encrypt("UM"+codeitemid)+"&amp;encryptParam="+PubFunc.encrypt("code=" + codeitemid + "&amp;kind=1&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1&amp;query=&amp;idordesc="));
 	            strXml.append(treeitem.toChildNodeJS() + "\n");
 	         }else if(rec.get("codesetid")!=null && "@K".equals(rec.get("codesetid")) && (!"org".equals(treetype) && !"noum".equals(treetype))){
 	         	//treeitem.setXml("/servlet/FrReportAddOrgTreeServlet?params=child&amp;treetype="+ treetype + "&amp;parentid=" + codeitemid + "&amp;kind=0&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive  + "&amp;action=" + action + "&amp;target=" + target);
 	        	 if(sdf.parse(sdf.format(new Date())).compareTo(sdf.parse((String)rec.get("end_date")))<=0){
 	        		 treeitem.setIcon("/images/pos_l.gif");
 	        	 }else{
 	        		 treeitem.setIcon("/images/b_pos_l.gif");
 	        	 }
 	            if("javascript:void(0)".equals(action))
 	            	treeitem.setAction(action);			        
 	            else
 	                treeitem.setAction(action + "&amp;orgCode="+PubFunc.encrypt("@K"+codeitemid)+"&amp;encryptParam="+PubFunc.encrypt("code=" + codeitemid + "&amp;kind=0&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1&amp;query=&amp;idordesc="));
 	            strXml.append(treeitem.toChildNodeJS() + "\n");
 	         }
 		}
 		strXml.append("</TreeNode>\n");
 		//System.out.println(strXml.toString());
 		return strXml.toString();
 	}
 	return strXml.toString();

}
	  /* 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
            throws ServletException, IOException {
        doPost(arg0, arg1);
    }
	private String getLoadTreeQueryString(String privType,String params,String isSuperuser,String parentid,String backdate,String nmodule,UserView userView, boolean isPost) {
		StringBuffer strsql=new StringBuffer();
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'org' as orgtype,end_date ");
		strsql.append(" FROM organization "); 
    	if(params!=null && "root".equals(params)){
    		if("1".equals(isSuperuser))
    		{
    			strsql.append(" WHERE codeitemid=parentid ");
    		}
    		else
    		{
    			String busi = "";
    			if(nmodule.length()>0 && !"null".equals(nmodule)){
    				busi = userView.getUnitIdByBusi(nmodule);
    				busi = com.hjsj.hrms.utils.PubFunc
					.getTopOrgDept(busi);
    			}
    			if(busi!=null&&busi.length()>0){
    			    if(!"UN`".equalsIgnoreCase(busi)){
    	    			strsql.append(" WHERE 1=2");
    					String[] org_depts = busi.split("`");
    					for(int i=0;i<org_depts.length;i++){
    						String org_dept = org_depts[i];
    						if(org_dept.length()>2){
    							strsql.append(" or (codesetid='"+org_dept.substring(0,2)+"' and codeitemid='"+org_dept.substring(2)+"')");
    						}
    					}
    			    }
    			    else
    			        strsql.append(" WHERE codeitemid=parentid ");
    			}else{
    				String managepriv = "";
    				if("U".equalsIgnoreCase(privType)){
    					managepriv = userView.getManagePrivCode() +  userView.getManagePrivCodeValue() + "`";
    				}
    				else if("O".equalsIgnoreCase(privType)){
    					
    					managepriv = userView.getUnit_id();//操作单位
    					
    				}
    				String searchCodes = "";
    				String[] temp=managepriv.split("`");
    				//查找出最上级的权限内代码
    				HashMap map = getPrivMange(temp);
    				for(int i=0;i<temp.length;i++)
    				{
    					String orgid = temp[i].substring(2);
    					if(map.containsKey(orgid))
    						continue;
    					searchCodes+="'"+orgid+"',";
    							
    				}
    				searchCodes+="'code'";
					strsql.append("where codeitemid in (");
					strsql.append(searchCodes);
					strsql.append(")");
    			}
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
    	
    	// 不显示岗位
    	if (!isPost) {
    		strsql.append(" and codesetid<>'@K' ");
    	}
    	
    	strsql.append(" ORDER BY a0000,codeitemid ");
		return strsql.toString();
	}
	
	private HashMap getPrivMange(String[] temp)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer("");
			for(int i=0;i<temp.length;i++)
			{
				String str=temp[i].substring(2);
				buf.append("`"+str);
				for(int j=0;j<temp.length;j++)
				{
					String str2=temp[j].substring(2);;
					if(!str2.equalsIgnoreCase(str)&&str2.startsWith(str))
					{
						map.put(str2, str2);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

}
