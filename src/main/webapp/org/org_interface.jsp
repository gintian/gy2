<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>

<head>
<title></title>
   <link href="/css/css1.css" rel="stylesheet" type="text/css">

</head>
<body topmargin="0" bottommargin="0" class="menuBodySet">
<br>
<br>
   <div align="center">
     <img src="org_flow.gif" BORDER="0" ALT="机构管理" USEMAP="#BusiMap">
     <MAP NAME="BusiMap">
       <AREA SHAPE="rect" id=""  COORDS="108,43,173,87" HREF="/org/orginfo/searchorgtree.do?b_query=link&code=${userView.managePrivCodeValue}" target="il_body">
       <AREA SHAPE="rect"   COORDS="108,152,173,195" HREF='<hrms:link href="/workbench/orginfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=org&kind=2&target=mil_body" target="il_body" function_id="xxx"> </hrms:link>' target="il_body">       
       <AREA SHAPE="rect"   COORDS="108,260,173,303" HREF="" target="il_body">       
       <AREA SHAPE="rect"   COORDS="251,65,316,108" HREF='' nohref target="il_body">       
       <AREA SHAPE="rect"   COORDS="251,238,316,281" HREF='' target="il_body">       
       <AREA SHAPE="rect"   COORDS="403,22,468,65" HREF='' target="il_body">       
       <AREA SHAPE="rect"   COORDS="403,108,468,152" HREF='' target="il_body">       
       <AREA SHAPE="rect"   COORDS="403,195,468,238" HREF='' target="il_body">       
       <AREA SHAPE="rect"   COORDS="403,281,468,325" HREF='' target="il_body">       

     </MAP>
    </div>
</body>
</html>