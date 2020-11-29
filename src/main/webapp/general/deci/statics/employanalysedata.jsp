<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
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
<script LANGUAGE=javascript>
  
</script> 
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/general/deci/statics/employmakeupanalyse">
  <br>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
   	   <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="makeupAnalyseForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="makeupAnalyseForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="makeupAnalyseForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="makeupAnalyseForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;   
	    </td>          	    	    	    		        	        	        
           </tr>
   	  </thead>
   	
          <hrms:paginationdb id="element" name="makeupAnalyseForm" sql_str="makeupAnalyseForm.strsql" table="" where_str="makeupAnalyseForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName," order_by="makeupAnalyseForm.order_by" pagerows="21" page_id="pagination">
	    <tr>
            <td align="left" class="RecordRow" nowrap>
          	&nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
          	&nbsp;<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;            
	    </td>
            <td align="left" class="RecordRow" nowrap>
             &nbsp;   <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;     
	    </td>
            <td align="left" class="RecordRow" nowrap>
              &nbsp; <a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="makeupAnalyseForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=jgfx_double" target="nil_body"><bean:write name="element" property="a0101" filter="true"/></a>&nbsp;
	    </td>               	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="makeupAnalyseForm" property="pagination" nameId="makeupAnalyseForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="left">
         	    <hrms:submit styleClass="mybutton" property="br_photo">
            		<bean:message key="button.query.viewphoto"/>
	 	    </hrms:submit> 
	 	     <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit> 	       	   
            </td>            
          </tr>          
</table>
</html:form>
