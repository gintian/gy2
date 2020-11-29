<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%

    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
           //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<head>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -180;
</SCRIPT>

</head>
<body class=menuBodySet style="margin:0 0 0 0">  
  
<table cellpadding=0 cellspacing=0 width="169" class=menu_table>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:800;filter:alpha(Opacity=100);display=block;"  id=menu1> 
   <html:form action="/templates/menu/zp_person_menu">
   <script language="javascript">
  function exeButtonAction(target_str)
   {
      target_url="/templates/menu/zp_person_menu.do?b_search=link&domain_value=" + document.zppersonForm.domain_value.value + "&valid_date="+document.zppersonForm.valid_date.value+"&pos_id="+document.zppersonForm.pos_id.value;
      window.open(target_url,target_str); 
   }  
</script> 
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">

      <tr class="trDeep1">
  	<td  background="/images/diao.gif" colspan="2" height="30" align="center"><img src="/images/forumme.gif">职位搜索<br> </td>
      </tr>
      <tr>
        <td align="left"  nowrap valign="center">
            <html:text name="zppersonForm" property="domain_value" value = "---工作地点---" styleClass="width:150px"/>
        </td> 
     </tr>
                  
     <tr>
          <td align="left"  nowrap valign="center">
          <html:select name="zppersonForm" property="valid_date" styleClass = "width:200px">
           <html:option value="#">－发布日期－</html:option>
           <html:option value="0">最近一天</html:option>
           <html:option value="1">最近二天</html:option>
           <html:option value="2">最近三天</html:option>
        </html:select> 
     </td> 
     </tr> 
    <tr>
         <td align="left"  nowrap valign="center">
        <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="true"  paraValue=""
            sql="select codeitemid,codeitemdesc from organization where codeitemid in (select pos_id from zp_position)" collection="list" scope="page"/> 
            <html:select name="zppersonForm" property="pos_id" size="1"> 
               <html:option value="#">－职位－</html:option>
               <html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            </html:select>
    </td>
    </tr>
    <tr>
    <td align="center" colspan="2" class="RecordRowinvestigate">
       <input type="button" name="addbutton"  value="搜索" class="mybutton" onclick="exeButtonAction('il_body')">
    </td>
 </tr>
 <tr class="trDeep">
     <td  background="/images/diao.gif" colspan="2" height="30" align="center"><img src="/images/forumme.gif">紧急招聘</td>
  </tr>
  <tr>
     <td colspan="2" align="left" class="RecordRowinvestigate">
        <hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link" target="il_body" function_id="xxx">高级程序分析员</hrms:link>
     </td>
 </tr>
 <tr>
     <td colspan="2" align="left" class="RecordRowinvestigate">
        <hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link" target="il_body" function_id="xxx">营销人员</hrms:link>
     </td>
 </tr>
 <tr>
     <td colspan="2" align="left" class="RecordRowinvestigate">
        <hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link" target="il_body" function_id="xxx">高级培训老师</hrms:link>
     </td>
 </tr>    
          </table>
	</html:form>
   </div>
 </td>
  </tr>
</table>

<script language="javascript">
  parent.frames[1].name= "il_body"; 
</script>  



                                                                                                                                                       