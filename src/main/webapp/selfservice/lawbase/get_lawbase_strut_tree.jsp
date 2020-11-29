
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java"%>
<%@ page import="com.hjsj.hrms.interfaces.lawbase.GetLawbaseDirectoryByXml"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page
	import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.exception.GeneralException"%>
<%@ page import="com.hrms.struts.valueobject.UserView" %>


<%
    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String orgId = userView.getUserOrgId();
    response.setContentType("text/xml;charset=UTF-8");
    String params = request.getParameter("params");
    params = PubFunc.decrypt(params);
    String basetype = request.getParameter("basetype");
    if(basetype!=null){
        /*防止SQL注入，basetype字段是int类型，此处转一下int，如果成功说明数据合法*/
        Integer.parseInt(basetype);
    }
    GetLawbaseDirectoryByXml lawxml = new GetLawbaseDirectoryByXml("mil_body", params);
    lawxml.setUserView(userView);
    if (userView.isSuper_admin())
        orgId = "-1";
    lawxml.setOrgId(orgId);
    lawxml.setBasetype(basetype);
    lawxml.setFlag(request.getParameter("flag"));
    try {
        String xmlc = lawxml.outPutDirectoryXml();
        //out.println(xmlc);
        response.getWriter().write(xmlc);
        response.getWriter().close();
        ;
    } catch (GeneralException ee) {
        ee.printStackTrace();
    }
%>








