<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.hire.ZppersonForm"%>
<%@ page import="java.util.HashMap"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";	 
	}
%>
<script language="javascript">
function exeButtonAction()
{
  if(document.zppersonForm.pos_id.value != '#' && document.zppersonForm.valid_date.value != '#' && document.zppersonForm.domain_value.value != '---工作地点---' && document.zppersonForm.domain_value.value != null && document.zppersonForm.domain_value.value != ''){
     target_url="/hire/zp_person/search_pos_cond.do?b_query=link&pos_id="+document.zppersonForm.pos_id.value+"&valid_date="+document.zppersonForm.valid_date.value+"&domain_value="+document.zppersonForm.domain_value.value;
  }else if(document.zppersonForm.pos_id.value != '#' && document.zppersonForm.valid_date.value != '#'){
     target_url="/hire/zp_person/search_pos_cond.do?b_query=link&pos_id="+document.zppersonForm.pos_id.value+"&valid_date="+document.zppersonForm.valid_date.value+"&domain_value="+document.zppersonForm.hidden_domain.value;
  }else if(document.zppersonForm.valid_date.value != '#' && document.zppersonForm.domain_value.value != '---工作地点---' && document.zppersonForm.domain_value.value != null && document.zppersonForm.domain_value.value != ''){
     target_url="/hire/zp_person/search_pos_cond.do?b_query=link&valid_date="+document.zppersonForm.valid_date.value+"&domain_value="+document.zppersonForm.domain_value.value+"&pos_id="+document.zppersonForm.hidden_pos_id.value;
  }else if(document.zppersonForm.pos_id.value != '#' && document.zppersonForm.domain_value.value != '---工作地点---' && document.zppersonForm.domain_value.value != null && document.zppersonForm.domain_value.value != ''){
     target_url="/hire/zp_person/search_pos_cond.do?b_query=link&pos_id="+document.zppersonForm.pos_id.value+"&domain_value="+document.zppersonForm.domain_value.value+"&valid_date="+document.zppersonForm.hidden_valid_date.value;
  }else if(document.zppersonForm.pos_id.value != '#'){
     target_url="/hire/zp_person/search_pos_cond.do?b_query=link&pos_id="+document.zppersonForm.pos_id.value+"&valid_date="+document.zppersonForm.hidden_valid_date.value+"&domain_value="+document.zppersonForm.hidden_domain.value;
  }else if(document.zppersonForm.valid_date.value != '#'){
     target_url="/hire/zp_person/search_pos_cond.do?b_query=link&valid_date="+document.zppersonForm.valid_date.value+"&pos_id="+document.zppersonForm.hidden_pos_id.value+"&domain_value="+document.zppersonForm.hidden_domain.value;
  }else if(document.zppersonForm.domain_value.value != '---工作地点---' && document.zppersonForm.domain_value.value != null && document.zppersonForm.domain_value.value != ''){
     target_url="/hire/zp_person/search_pos_cond.do?b_query=link&domain_value="+document.zppersonForm.domain_value.value+"&pos_id="+document.zppersonForm.hidden_pos_id.value+"&valid_date="+document.zppersonForm.hidden_valid_date.value;
  }else{
     target_url="/hire/zp_person/search_pos_cond.do?b_query=link&pos_id="+document.zppersonForm.hidden_pos_id.value+"&valid_date="+document.zppersonForm.hidden_valid_date.value+"&domain_value="+document.zppersonForm.hidden_domain.value;
  }
  window.open(target_url,'mil_body'); 
}
</script>
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_person/search_pos_cond">
<html:hidden name="zppersonForm" property="hidden_domain" value="0"/>
<html:hidden name="zppersonForm" property="hidden_valid_date" value="a"/>
<html:hidden name="zppersonForm" property="hidden_pos_id" value="0"/> 
   <table border="0" cellspacing="1"  align="center" cellpadding="1" width="100%" class="ListTable">
         <tr class="trDeep">
  	    <td colspan="2" height="20" align="center"><img src="/images/forumme.gif"><bean:message key="hire.search.position"/><br></td>
      </tr>
      <tr>
        <td colspan="2" align="left" class="RecordRowinvestigate">
            <html:text name="zppersonForm" property="domain_value" value = "<bean:message key="hire.work.site"/>"/>
        </td> 
     </tr>               
     <tr>
          <td colspan="2" align="left" class="RecordRowinvestigate">
          <html:select name="zppersonForm" property="valid_date" style="width:150px">
           <html:option value="#"><bean:message key="hire.release.date"/></html:option>
           <html:option value="0"><bean:message key="hire.latest.one"/></html:option>
           <html:option value="1"><bean:message key="hire.latest.two"/></html:option>
           <html:option value="2"><bean:message key="hire.latest.three"/></html:option>
        </html:select> 
     </td> 
     </tr> 
     <tr>
       <td colspan="2" align="left" class="RecordRowinvestigate">
       <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="true"  paraValue="1"
             sql="select codeitemid,codeitemdesc from organization where codeitemid in (select pos_id from zp_position) and 1=? " collection="list" scope="page"/> 
             <html:select name="zppersonForm" property="pos_id" size="1" style="width:150px"> 
                <html:option value="#"><bean:message key="hire.position.position"/></html:option>
            	<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            </html:select>
      </td>
    </tr>
    <tr>
    <td align="center" colspan="2" class="RecordRowinvestigate">
       <input type="button" name="searchbutton"  value="<bean:message key="lable.law_base_file_search.search"/>" onclick="exeButtonAction()">
    </td>
 </tr>
 </table>
 <table border="0" cellspacing="1" width="100%" align="center" cellpadding="1" class="ListTable">
  <tr class="trDeep">
     <td colspan="2" height="20" align="center"><img src="/images/forumme.gif"><bean:message key="hire.urgent.hire"/><br></td>
  </tr>
  <%
      ZppersonForm zppersonForm=(ZppersonForm)session.getAttribute("zppersonForm");
      for(int i=0;i<zppersonForm.getUrgentzpposlist().size();i++){
         HashMap hm = (HashMap)zppersonForm.getUrgentzpposlist().get(i);
         String codeitemid = (String)hm.get("codeitemid"); 
         String codeitemdesc = (String)hm.get("codeitemdesc"); 
  %>
  
  <tr>
     <td colspan="2" align="left" class="RecordRowinvestigate">
        <a href="/hire/zp_person/search_pos_template.do?a0100=<%=codeitemid%>" target="mil_body"><%=codeitemdesc%></a>
     </td>
 </tr>
 <%}%>
</table>
</html:form>
