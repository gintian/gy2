<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="org.apache.commons.beanutils.DynaBean,
org.apache.commons.beanutils.LazyDynaBean,
com.hjsj.hrms.transaction.sys.bos.portal.SavePortalMainTrans,
com.hjsj.hrms.transaction.sys.bos.portal.FindPortalMainTrans,org.jdom.Document,
com.hjsj.hrms.actionform.sys.bos.portal.PortalMainForm,
com.hjsj.hrms.transaction.sys.bos.portal.DelPortalMainAllTrans,
com.hjsj.hrms.transaction.sys.bos.portal.EditPortalSaveMainTrans,
com.hjsj.hrms.businessobject.sys.bos.portal.PortalMainBo
"%>
<%@ page import="com.hrms.struts.exception.*" %>
<% 
   // response.setContentType("text/xml;charset=UTF-8");
    	PortalMainForm portalMainForm=(PortalMainForm)session.getAttribute("portalMainForm"); 
		Document doc=portalMainForm.getPortal_dom(); 
	String type = request.getParameter("type");
	String opt = request.getParameter("opt");
	//type:1查找,2增加,3修改,4删除5,生效
	if("1".equals(type)){ 
	
	}
	else if("2".equals(type)){
	
	
	}
	else if("3".equals(type)){
	
	}
		else if("4".equals(type)){
		String portal_id=request.getParameter("portal_id")==null?"":request.getParameter("portal_id");
		if (portal_id.contains("＇")){
			String[] str = portal_id.split("＇");
			portal_id = str[0];
		}
		DelPortalMainAllTrans delPortalMainAllTrans = new DelPortalMainAllTrans();
		delPortalMainAllTrans.executeSession(portal_id,doc);
	}
	else if("5".equals(type)){
		PortalMainBo bo = new PortalMainBo();
		bo.writeFile(doc);
	}
%>