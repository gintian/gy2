<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="org.apache.commons.beanutils.DynaBean,
org.apache.commons.beanutils.LazyDynaBean,
com.hjsj.hrms.transaction.sys.bos.menu.SaveMenuMainTrans,
com.hjsj.hrms.transaction.sys.bos.menu.FindMenuMainTrans,org.jdom.Document,
com.hjsj.hrms.actionform.sys.bos.menu.MenuMainForm,
com.hjsj.hrms.transaction.sys.bos.menu.DelMenuMainAllTrans,
com.hjsj.hrms.transaction.sys.bos.menu.EditMenuSaveMainTrans,
com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo
"%>
<%@ page import="com.hrms.struts.exception.*" %>
<% 
   // response.setContentType("text/xml;charset=UTF-8");
    	MenuMainForm menuMainForm=(MenuMainForm)session.getAttribute("menuMainForm"); 
		Document doc=menuMainForm.getMenu_dom(); 
	String type = request.getParameter("type");
	//type:1查找,2增加,3修改,4删除5,生效,6拖动
	if("1".equals(type)){ 
	
	}
	else if("2".equals(type)){
	
	
	}
	else if("3".equals(type)){
	
	}
		else if("4".equals(type)){
		String menu_id=request.getParameter("menu_id")==null?"":request.getParameter("menu_id");
		DelMenuMainAllTrans delMenuMainAllTrans = new DelMenuMainAllTrans();
		delMenuMainAllTrans.executeSession(menu_id,doc);
	}
	else if("5".equals(type)){
		MenuMainBo bo = new MenuMainBo();
		bo.writeFile(doc);
	}else if("6".equals(type)){
		String frommenu_id=request.getParameter("frommenu_id")==null?"":request.getParameter("frommenu_id");
		String tomenu_id=request.getParameter("tomenu_id")==null?"":request.getParameter("tomenu_id");
		DelMenuMainAllTrans delMenuMainAllTrans = new DelMenuMainAllTrans();
			delMenuMainAllTrans.dragNode(frommenu_id,tomenu_id,doc);
	}
%>