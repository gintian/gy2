<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="org.apache.commons.beanutils.DynaBean,
org.apache.commons.beanutils.LazyDynaBean,
com.hjsj.hrms.transaction.sys.bos.func.SaveFuncMainTrans,
com.hjsj.hrms.transaction.sys.bos.func.FindFuncMainTrans,org.jdom.Document,
com.hjsj.hrms.actionform.sys.bos.func.FunctionMainForm,
com.hjsj.hrms.transaction.sys.bos.func.DelFuncMainAllTrans,
com.hjsj.hrms.transaction.sys.bos.func.EditFuncSaveMainTrans,
com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo
"%>
<%@ page import="com.hrms.struts.exception.*" %>
<% 
   // response.setContentType("text/xml;charset=UTF-8");
    	FunctionMainForm functionMainForm=(FunctionMainForm)session.getAttribute("functionMainForm"); 
		Document doc=functionMainForm.getFunction_dom(); 
	String type = request.getParameter("type");
	//type:1查找,2增加,3修改,4删除5,生效,6拖动
	if("1".equals(type)){ 
	
	}
	else if("2".equals(type)){
	
	}
	else if("3".equals(type)){
	
	}
		else if("4".equals(type)){
		String function_id=request.getParameter("function_id")==null?"":request.getParameter("function_id");
		DelFuncMainAllTrans delFuncMainAllTrans = new DelFuncMainAllTrans();
		delFuncMainAllTrans.executeSession(function_id,doc);
	}
	else if("5".equals(type)){
		FuncMainBo bo = new FuncMainBo();
		bo.writeFile(doc);
	}else if("6".equals(type)){
		String fromfunc_id=request.getParameter("fromfunc_id")==null?"":request.getParameter("fromfunc_id");
		String tofunc_id=request.getParameter("tofunc_id")==null?"":request.getParameter("tofunc_id");
		DelFuncMainAllTrans delFuncMainAllTrans = new DelFuncMainAllTrans();
			delFuncMainAllTrans.dragNode(fromfunc_id,tofunc_id,doc);
	}
%>