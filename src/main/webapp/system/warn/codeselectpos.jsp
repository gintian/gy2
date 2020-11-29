<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<%
   	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
<link href="<%=css_url%>" rel="stylesheet" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   
   <script language="JavaScript">
        var input_code_id;
        var selfForm;
        
	   	function saveOrgCode(){
			var hiddenCodes,showValues;
			var xtreeArray;
			hiddenCodes="";
			showValues="";
			var checkitems=document.getElementsByName("treeItem-check");
			for(var i=0;i<checkitems.length;i++){
				currnode=checkitems[i];
				if(currnode.checked){
					
					//alert(currnode.value + ";"+currnode.value.substring(2));
					// 组织机构编码
					if( currnode.value=='root'){
						hiddenCodes='UN';
					}else{
						hiddenCodes=hiddenCodes+currnode.value+",";
					}
	
					// 组织机构名称				
					var checkboxId = currnode.id;
					checkboxId = checkboxId.substring(15);// 因为命名格式为：chkbox.id="treeItem-check-" + this.id;
					//alert( "checkboxId =" + checkboxId );
					var textSpan=document.getElementsByName("treeItem-text-" + checkboxId);
					showValues=showValues + textSpan[0].title + ",";
				}
			}

			if( hiddenCodes.length>2){
				hiddenCodes=hiddenCodes.substring(0,hiddenCodes.length-1);//去掉最后一个逗号
			}
			if( showValues.length>2){
				showValues=showValues.substring(0,showValues.length-1);//去掉最后一个逗号
			}
			// 保存组织的编码
			_targetHiddenCode.value = hiddenCodes;
			//_targetHiddenCode.fireEvent("onchange");
			
			// 保存组织的名称
			_targetShowValues.value=showValues;
			
   		}
   		   		
   </SCRIPT>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
	
   <SCRIPT LANGUAGE=javascript>
             var _codeSetId, _codeValue, _name, _flag;
             var _targetShowValues, _targetHiddenCode;
             _codeSetId = dialogArguments[0];
             _codeValue = dialogArguments[1];
             _targetShowValues=dialogArguments[2];//显示代码描述的隐藏域
             _targetHiddenCode=dialogArguments[3];//代码值对象文本框
             _flag=dialogArguments[4];
			 Global.checkvalue=_targetHiddenCode.value;
   </SCRIPT> 
 	 <hrms:orgtree flag="1" showroot="false" selecttype="1"/>	
   <br> 
   
    <input type="button" name="btnok" value="确定" class="mybutton" onclick="saveOrgCode();window.close();">
    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="window.close();">    
<!--    <input type="button" name="btnok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="saveOrgCode();window.close();">
    <input type="button" name="btncancel" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">    
-->    
<BODY>
</HTML>


