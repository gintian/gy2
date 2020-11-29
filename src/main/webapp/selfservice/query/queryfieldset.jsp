<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/selfservice/query/queryfieldset">
  <br>
  <br>
  <br>   
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	     <td width="100%" align="center" class="RecordRow" nowrap>
   	         <bean:write name="queryConstantForm" property="succeedinfo"/>
   	     </td>
   	  </tr>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                     <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;
                    </td>
                    </tr>
                    <tr>
                    <td align="center">
                      <hrms:fieldsetlist name="queryConstantForm" usedflag="usedflag" domainflag="domainflag"  collection="setlist" scope="session"/>
                      <html:select name="queryConstantForm" property="setname" size="1"  onchange="javascript:this.document.queryConstantForm.submit()" style="width:100%">
                           <html:options collection="setlist" property="dataValue" labelProperty="dataName"/>
                      </html:select>
                    </td>
                    </tr>
                   <tr>
                    <td align="center">
                      <hrms:fielditemlist  name="queryConstantForm" usedflag="usedflag" setname="setname" collection="list" scope="session"/>
                      <html:select property="left_fields" multiple="true" style="height:209px;width:100%;font-size:9pt" ondblclick="additem('left_fields','right_fields');">
                           <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                      </html:select>
                    </td>
                    </tr>
                   </table>
                </td>
                <td width="8%" align="center">
                   <hrms:submit styleClass="mybutton" property="b_addfield"  onclick="document.queryConstantForm.target='_self';return additemfield('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </hrms:submit>
	           <br>
	           <br>
	           <hrms:submit styleClass="mybutton" property="b_delfield" onclick="document.queryConstantForm.target='_self'">
            		     <bean:message key="button.setfield.delfield"/>    
	           </hrms:submit>	     
                </td>         
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
                   <hrms:optioncollection name="queryConstantForm" property="fieldlist" collection="selectedlist"/>
     	             <html:select property="right_fields" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt" ondblclick="removeitem('right_fields');">
                        <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     </html:select>   
                  </td>
                   <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>  
                  </tr>
                  </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3">
               <hrms:submit styleClass="mybutton" property="b_save" onclick="setselectitem('right_fields');">
            		      <bean:message key="button.ok"/>
	        </hrms:submit> 	         
          </td>
          </tr>   
</table>
</html:form>
