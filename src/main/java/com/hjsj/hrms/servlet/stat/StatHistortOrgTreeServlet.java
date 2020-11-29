package com.hjsj.hrms.servlet.stat;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StatHistortOrgTreeServlet  extends HttpServlet {
	  
	private static final long serialVersionUID = 1L;
  
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
    	UserView userView = (UserView) req.getSession().getAttribute("userView");
    	StringBuffer sbXml = new StringBuffer();
    	 String params=req.getParameter("params");
         String issuperuser=req.getParameter("issuperuser");
         String parentid=req.getParameter("parentid");
         String manageprive=req.getParameter("manageprive"); 	
    	 String target=req.getParameter("target");    	 
    	 String loadtype=req.getParameter("loadtype"); /**加载选项* =0（单位|部门|职位）* =1 (单位|部门)* =2 (单位)* */
    	 loadtype=loadtype!=null?loadtype:"0";
    	 String action=req.getParameter("action");
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
 		 String backdate = req.getParameter("backdate");
 		 backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
 		 if(action==null||action.length()<=0)
 		    action="javascript:void(0)";
 		 try {
			sbXml.append(loadOrgItemNodes(params,issuperuser,parentid,manageprive,action,target,backdate,loadtype, userView));
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(sbXml.toString());
    }
    private String  loadOrgItemNodes(String params,String issuperuser,String parentid,String manageprive,String action,String target,String backdate,String loadtype, UserView userView) throws Exception
    {
    	StringBuffer strXml=new StringBuffer();
    	List rs=new ArrayList();
    	
 	    rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,issuperuser,parentid,manageprive,backdate, userView)); 
    	if(!rs.isEmpty())
 	    {
 		  strXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<TreeNode>\n");
 		  for(int i=0;i<rs.size();i++)
 		  {
 		     TreeItemView treeitem=new TreeItemView();
 		     DynaBean rec=(DynaBean)rs.get(i);
 		   //  String org_type=rec.get("orgtype")!=null?rec.get("orgtype").toString():"";
 		     String image="admin.gif";
 		     
 		     String codeitemid=rec.get("codeitemid")!=null?rec.get("codeitemid").toString():"";
 		     String codeitemdesc=rec.get("codeitemdesc")!=null?rec.get("codeitemdesc").toString().replaceAll("&",	"&amp;"):"";
 		     String codesetid=rec.get("codesetid")!=null?rec.get("codesetid").toString():"";
 		    if("2".equalsIgnoreCase(loadtype))
            {
            	if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid))
            		continue;            	
            }
            if("1".equalsIgnoreCase(loadtype))
            {
            	if("@K".equalsIgnoreCase(codesetid))
            		continue;
            }
 		     treeitem.setName(codesetid+codeitemid);
 		     treeitem.setText(codeitemdesc);
 	         treeitem.setTitle(codeitemdesc);  
 	         treeitem.setTarget(target);
 	         
 	        // System.out.println("ddd" + treetype);
 	         if(rec.get("codesetid")!=null && "UN".equals(rec.get("codesetid")))
 	         {
 	            if(!codeitemid.equalsIgnoreCase(rec.get("childid")!=null?rec.get("childid").toString():""))
 	         	    treeitem.setXml("/stat/history/loadtree?params=child&amp;parentid="  + codeitemid + "&amp;kind=2&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive + "&amp;target=" + target+"&amp;backdate="+backdate+"&amp;jump=1");
 	            
 	            	image="/images/unit.gif";
 	            treeitem.setIcon(image); 	          
	            if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);	
	            else
 	               treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=2&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1");
 	            strXml.append(treeitem.toChildNodeJS() + "\n");
 	         }else if(rec.get("codesetid")!=null && "UM".equals(rec.get("codesetid"))){
 	        	//String childid=rec.get("childid")!=null?rec.get("childid").toString():"";
 	         	//if(!codeitemid.equalsIgnoreCase(childid))
 	        	   treeitem.setXml("/stat/history/loadtree?params=child&amp;parentid=" + codeitemid +	"&amp;kind=1&amp;issuperuser=" + issuperuser + "&amp;manageprive=" + manageprive  + "&amp;action=" + action + "&amp;target=" + target+"&amp;backdate="+backdate+"&amp;jump=1");
 	         	   image="/images/dept.gif";
 	         	treeitem.setIcon(image);
 	         	if("javascript:void(0)".equals(action))
	            	treeitem.setAction(action);	
	            else
 	               treeitem.setAction(action + "?b_search=link&amp;code=" + codeitemid + "&amp;kind=1&amp;root=0"+"&amp;backdate="+backdate+"&amp;jump=1");
 	            strXml.append(treeitem.toChildNodeJS() + "\n");
 	         }
 		  }
 		  strXml.append("</TreeNode>\n");
 		//System.out.println(strXml.toString());
 		  return strXml.toString();
 	    }
 	  return strXml.toString();

    }
    private String getLoadTreeQueryString(String params,String isSuperuser,String parentid,String managepriv,String backdate, UserView userView) {
		StringBuffer strsql=new StringBuffer();
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'org' as orgtype ");
		strsql.append(" FROM organization"); 
		if (params != null && "root".equals(params)) {
			if ("1".equals(isSuperuser)) {
				strsql.append(" WHERE codeitemid=parentid ");
			} else {
				// 临时存储机构编码
				List unitIdList = new ArrayList();
				// 只授权机构标示符
				boolean unitFlag = false;
				// UN11`UN1101`UM110101`UN1102`UN1001003`UN1001011
				String unitIdByBusi = userView.getUnitIdByBusi("4"); 
				// 分解过滤获取要显示的机构编码
				if (unitIdByBusi != null && unitIdByBusi.length() > 0) {
					String[] unitIdByBusiList = unitIdByBusi.split("`");
					StringBuffer tempBuffer = new StringBuffer();
					for (int i = 0; i < unitIdByBusiList.length; i++) {
						unitIdByBusi = unitIdByBusiList[i];
						if (unitIdByBusi.trim().length() == 2) {// 机构
							unitFlag = true;
						} else if (unitIdByBusi.trim().length() > 2) { // 编码
							unitIdByBusi = unitIdByBusi.substring(2, unitIdByBusi.length());
							tempBuffer.append(",'" + unitIdByBusi + "'");
						}
					}
					// 过滤机构编码，是否在b01表中存在
					if (!unitFlag && tempBuffer.length() >= 1) {
						List rs = ExecuteSQL.executeMyQuery("select b0110 from b01 where b0110 in (" + tempBuffer.substring(1) + ")");
						if (rs != null) {
							for (int i = 0; i < unitIdByBusiList.length; i++) {
								DynaBean rec = (DynaBean) rs.get(i);
								unitIdList.add(rec.get("b0110"));
							}
						}
					}
				}
				// 组装sql语句
				strsql.append(" WHERE (");
				if (unitFlag) {// 直接对接机构
					strsql.append(" codeitemid=parentid ");
				} else if (unitIdList.size() >= 1) {
					// 排序
					Collections.sort(unitIdList);
					// sql追加第一个机构编码,并放入临时String中
					String tempStr = (String) unitIdList.get(0);
					strsql.append(" codeitemid='" + tempStr + "'");
					// 思想：如unitIdList=【1101，110101】，则sql拼接时只拼接1101忽略110101，防止组织机构树错位显示
					for (int i = 0; i < unitIdList.size(); i++) {
						unitIdByBusi = (String) unitIdList.get(i);
						if (tempStr.length() <= unitIdByBusi.length()) {
							if (unitIdByBusi.startsWith(tempStr)) {
								continue;
							} else {
								tempStr = unitIdByBusi;
							}
						} else {
							tempStr = unitIdByBusi;
						}
						strsql.append(" OR codeitemid='" + unitIdByBusi + "'");
					}
				} else { // 没有任何机构编码权限
					strsql.append(" 1=2");
				}
				strsql.append(" )");
			}
		} else {
			strsql.append(" WHERE parentid='" + parentid + "'");
			strsql.append(" AND codeitemid<>parentid ");
		}
    	strsql.append(" and codeitemid in(select b0110 from b01)");
    	strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
    	strsql.append(" ORDER BY a0000,codeitemid ");
		return strsql.toString();
	}
    
}
