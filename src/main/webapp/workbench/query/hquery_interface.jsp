<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	String a_query=request.getParameter("a_query");
 %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
   function change()
   {
	  var queryFields = "";
      setselectitem('right_fields');
      var vos = document.getElementsByName('right_fields')[0];
      if(vos.options) {
		   for(var i = 0; i < vos.options.length; i++){
			   queryFields += vos.options[i].value + ",";
		   }
      }
      highQueryForm.action="/workbench/query/hquery_interface.do?b_addfield=link&queryFields=" + queryFields;
      highQueryForm.submit();
   }
   function brReture(){
   	  highQueryForm.action="/workbench/query/query_interface.do?b_query=link?a_inforking=1&home=0";
      highQueryForm.submit();
   }
   
   /*屏蔽弹框内鼠标覆盖按钮提示连接信息*/
   document.onmouseover=function(){
	   window.status="";
	   return true;
   }
</script> 
<hrms:themes />
<html:form action="/workbench/query/hquery_interface"> 
<input type="hidden" name ="a_query" value="<%=a_query %>" />
  <table width="700px" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top: 6px;margin-left:-5px;">
   	  <thead>
           <tr height="30">
            <td style="padding: 5px" align="left" class="TableRow1" nowrap colspan="3">
		<bean:message key="label.query.selectfield"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" class="RecordRow" nowrap>
              <table width="100%" cellpadding="0" cellspacing="0">
                <tr>
                 <td align="left" width="310px">
                   <table width="100%" align="left" cellpadding="0" cellspacing="0" style="padding-top: 3px;margin-bottom: -8px">
                    <tr>
                    <td>
                     <bean:message key="selfservice.query.queryfield"/>
                    </td>
                    </tr>
                    <tr>
                    <td>
                      <hrms:fieldsetlist name="highQueryForm" usedflag="usedflag" domainflag="domainflag"  collection="setlist" scope="session"/>
                      <html:select name="highQueryForm" property="setname" size="1" styleId="fieldsetId" onchange="change();" style="width:100%" >
                          <html:options collection="setlist" property="dataValue" labelProperty="dataName"/>
                      </html:select>
                    </td>
                    </tr>
                   <tr>
                    <td align="center">
                      <hrms:fielditemlist  name="highQueryForm" usedflag="usedflag" setname="setname" collection="list" scope="session" memo="false"/>
                      <span style="width:100%;margin-right: -1px;margin-bottom: -1px;">
                      <html:select property="left_fields" multiple="true" style="height:227px;width:100%;font-size:9pt;" ondblclick="additem('left_fields','right_fields');">
                           <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                      </html:select>
                      </span>
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="40px" align="center" valign="middle" style="padding-right: 5px">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button>
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="removeitem('right_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>
	           </html:button>	     
                </td>         
                
                
                <td align="right" width="310px">
                 
                 
                 <table width="100%" cellpadding="0" cellspacing="0" style="padding-top: 3px;margin-bottom: -7px">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="selfservice.query.queryfieldselected"/>
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="right">
                  <hrms:optioncollection name="highQueryForm" property="fieldlist" collection="selectedlist"/> 
     	             <span style="width:100%;">
     	             <html:select property="right_fields" size="10" name="highQueryForm" multiple="true" style="height:250px;width:100%;font-size:9pt;" ondblclick="removeitem('right_fields');">
                        <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     </html:select>   
                     </span>
                  </td>
                  </tr>
                  </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRowP"  colspan="3" style="height:35px">
              <logic:notEqual name="highQueryForm" property="query_type" value="3">  
                 <logic:equal name="highQueryForm" property="home" value="0">               
                    <hrms:submit styleClass="mybutton" property="br_return" onclick="brReture();">
            		      <bean:message key="button.query.pre"/>
	                </hrms:submit> 	 
                 </logic:equal>  	                     
              </logic:notEqual>  
          	                    
              <hrms:submit styleClass="mybutton" property="b_next" onclick="setselectitem('right_fields');">
            		      <bean:message key="button.query.next"/>
	          </hrms:submit> 
	          <logic:equal name="highQueryForm" property="home" value="1">  
	             <hrms:tipwizardbutton flag="emp" target="il_body" formname="highQueryForm"/> 
	          </logic:equal>
          </td>
          </tr>
	</table>
	<script>
		if(!getBrowseVersion() || getBrowseVersion() == 10){//兼容非IE浏览器  wangb 20180125 and ie11 不加兼容视图   wangb 20190307
			var form =document.getElementsByName('highQueryForm')[0];
			var firstTable = form.getElementsByTagName('table')[0];
			firstTable.style.marginLeft='';
			var firstTd = firstTable.getElementsByClassName('RecordRow')[0]; 
			firstTd.setAttribute('valign','top');
			firstTd.style.height = '282px';
			
			document.getElementById('fieldsetId').style.marginBottom = "5px";
		}
	</script>
</html:form>
