<%@ page import="com.hjsj.hrms.interfaces.lawbase.GetLawTextDirectoryByXml"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%@ page import="com.hrms.struts.exception.GeneralException"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>


<%
            UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			String orgId = userView.getUserOrgId();
			boolean baction=true;
			response.setContentType("text/xml;charset=UTF-8");
			String params = request.getParameter("params");
			params = PubFunc.decrypt(params);
			String basetype = request.getParameter("basetype");
			if(basetype!=null){
				/*防止SQL注入，basetype字段是int类型，此处转一下int，如果成功说明数据合法*/
				Integer.parseInt(basetype);
			}
			String action= request.getParameter("action");
			if(action==null)
				action="1";
			if(action.equals("0"))
			    baction=false;
			GetLawTextDirectoryByXml lawxml = new GetLawTextDirectoryByXml(
					"mil_body", params);
		    lawxml.setBasetype(basetype);
			lawxml.setOrgId(orgId);
			lawxml.setUserView(userView);
			try 
			{
				String xmlc = lawxml.outPutDirectoryXml(baction); //create xtree.js treeview.
				//out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
			} catch (GeneralException ee) {
				ee.printStackTrace();
			}
		%>




