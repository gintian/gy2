<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hjsj.hrms.actionform.dtgh.party.person.PersonForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.hjsj.sys.IResourceConstant"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%!
	private String analyseManagePriv(String managed_str){
		if(managed_str.length()<3)
			return "";
		StringBuffer sb = new StringBuffer();
		String[] strS = managed_str.split(",");
 		 String ids="";
 		 for(int i=0;i<strS.length;i++){
 			 String id = strS[i];
 			 if(id!=null&&id.length()>1){
 				 boolean check = true;
 				 for(int j=0;j<strS.length;j++){
 					 String id_s = strS[j];
 					 if(id_s!=null&&id_s.length()>1){
 						 if(id.length()>id_s.length()){
 							if(id.substring(2,id.length()).startsWith(id_s.substring(2,id_s.length()))){
								 check = false;
								 ids=id_s;
								 break;
							 }
 						 }else{
 							 if(id.equalsIgnoreCase(id_s)){
 								 continue;
 							 }
 							 if(id_s.substring(2,id_s.length()).startsWith(id.substring(2,id.length()))){
 								 check = false;
 								ids=id_s;
 								 break;
 							 }
 						 }
 					 }
 				 }
 				 if(check){
 					if(sb.indexOf(id)==-1 && !"64".equalsIgnoreCase(id) && !"65".equalsIgnoreCase(id))
 						sb.append("','"+id.substring(2));
 				 }else{
 					 if(id.length()<ids.length()){
 						if(sb.indexOf(id)==-1 && !"64".equalsIgnoreCase(id) && !"65".equalsIgnoreCase(id))
 							sb.append("','"+id.substring(2));
 					 }
 				 }
 			 }
 		 }
 		if(sb.length()<4)
			return "";
		else
			return sb.substring(3);
	}
 %>
<script language="javascript" src="/js/constant.js"></script>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	PersonForm personForm = (PersonForm)session.getAttribute("personForm");
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
 	int res_type = IResourceConstant.PARTY;
	if("65".equals(personForm.getCodesetid()))
		 res_type = IResourceConstant.MEMBER;
		 
	String codevalue = userView.getResourceString(res_type);
	    	   if(codevalue.length()<3){
	    		   if(userView.isSuper_admin()&&!userView.isBThreeUser())
	    			   codevalue="ALL";
	    		   else{
	    			   if(codevalue.equals("64")||codevalue.equals("65"))
	    				   codevalue="ALL";
	    			   else
	    				   codevalue=""; 
	    		   }
	    	   }else{
	    		   codevalue=this.analyseManagePriv(codevalue);
	    			if(codevalue.length()<1){
	    				codevalue="ALL";
	    			}   
	    		}
%>
<script type="text/javascript">
</script>
<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
</HEAD>
<body style="margin:0px;padding:0px;">
<html:form action="/dtgh/party/person/searchbusinesslist">
	<table width="700" border="0" cellspacing="1"  align="left" cellpadding="1" style="margin:0px; padding:0px;">
		<tr><td>
		<div id="treemenu" style="height: expression(document.body.clientHeight-50);width:expression(document.body.clientWidth);overflow-x: hidden;overflow-y:auto;"></div>
		</td>
		</tr>  
    </table>
    <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
     <SCRIPT LANGUAGE=javascript>
       var m_sXMLFile="/dtgh/party/person/get_code_tree.jsp?codesetid=${personForm.codesetid}&codeitemid=<%=codevalue %>&privflag=1";	 ///dtgh/party/person/searchbusinesslist.do?b_query=link&a_code=${personForm.codesetid}&politics=
       var root=new xtreeItem("root","${personForm.codesetdesc}","javascript:void(0)","nil_body","${personForm.codesetdesc}","/images/spread_all.gif",m_sXMLFile);
       root.setup(document.getElementById("treemenu"));
       var currnode = root.getFirstChild();
       if(currnode){
       		try{
       			Global.selectedItem=currnode;
       			currnode.openURL();
       		}catch(e){
       		}
       }
       //root.openURL();
    </SCRIPT>
 </html:form>
<BODY>
</HTML>
