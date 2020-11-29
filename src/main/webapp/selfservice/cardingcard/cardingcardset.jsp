<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/selfservice/cardingcard/cardingcardset">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="selfservice.cardingcard.cardsettitle"/>&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
          <tr>
            <td align="center" class="RecordRow" nowrap>
     	     <bean:message key="selfservice.cardingcard.selectcard"/>
    	        <hrms:importgeneraldata showColumn="name" valueColumn="tabid" flag="true" paraValue="" 
                  sql="select tabid,name from rname where flagA='A'" collection="list" scope="page"/>
                <html:select name="cardingcardConstantForm" property="constant_vo.string(str_value)" size="1">
                     <html:option value="#">请选择...</html:option>
                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                  	        	        
          </tr>  
          <td align="center" class="RecordRow" nowrap>
              &nbsp;&nbsp;
              <hrms:submit styleClass="mybutton" property="b_cardset">
            		<bean:message key="button.ok"/>
	      </hrms:submit>         
                          
          </td>
          </tr>   
</table>
</html:form>
