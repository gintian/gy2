<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript">
   function toallapp(table)
   {
    appForm.action="/kq/app_check_in/all_app_data.do?b_search2=link&table="+table+"&dotflag=1";
    appForm.submit();
   }
    function validate()
	{
	 var tag=true;  
	 var t=1; 
          <logic:iterate  id="element"    name="appForm"  property="viewlist" indexId="index"> 
            <logic:equal name="element" property="itemtype" value="D">   
               var valueInputs=document.getElementsByName("<%="viewlist["+index+"].value"%>");
               var dobj=valueInputs[0];
               t++;
			if(t!='5'){
                 tag= isDate(dobj.value,"yyyy-MM-dd HH:mm");      
	       if(tag==false)
	         {
	           alert("输入时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
	           return false;
	         }
			}
            </logic:equal> 
          </logic:iterate> 

          <logic:equal name="appForm" property="table" value="Q11">
			var reasonid="q1107";
		</logic:equal>
		<logic:equal name="appForm" property="table" value="Q13">
		var reasonid="q1307";
		</logic:equal>
		<logic:equal name="appForm" property="table" value="Q15">
		var reasonid="q1507";
		</logic:equal>
          var reason=document.getElementById(reasonid);
      	
          if(trim(reason.value) == "")
          {
        	  tag=false;
        	  alert("操作失败，事由不能为空！ ");
	           return false;
              }
         return tag;
	 
	}
	function change()
   {
      appForm.action="/kq/app_check_in/change_app.do?b_change=link";
      appForm.submit();
   }
   function getKqCalendarVar()
   {
     var hashvo=new ParameterSet();       		
     var request=new Request({method:'post',onSuccess:setKqCalendarVar,functionId:'15388800008'},hashvo);
   }
   function setKqCalendarVar(outparamters)
   {
       weeks=outparamters.getValue("weeks");  
       feasts=outparamters.getValue("feasts");  
       turn_dates=outparamters.getValue("turn_dates");  
       week_dates=outparamters.getValue("week_dates");  
   }
   function reSaveFlag()
   {
     var spflag="${appForm.spFlag}";
     if(spflag!="")
     {
        alert(spflag);
        appForm.action="/kq/app_check_in/all_app_data.do?b_search=link&dotflag=1";
        appForm.submit();
     }
   } 
    function save(sp)
   {
      var hashvo=new ParameterSet();
      hashvo.setValue("sp",sp);     
      hashvo.setValue("mess","${appForm.mess}");      
      hashvo.setValue("table","${appForm.table}");
      <logic:iterate id="element" name="appForm"  property="viewlist" indexId="index">       
      var itemid="${element.itemid}";
      var value=document.getElementById(itemid).value;   
      hashvo.setValue(itemid,value);	
      </logic:iterate>
      var request=new Request({method:'post',onSuccess:reSave,functionId:'1510010814'},hashvo);
   }
   function reSave(outparamters)
  { 
     var spFlag=outparamters.getValue("spFlag");    
     if(spFlag!="")
     {
        alert(spFlag);
        appForm.action="/kq/app_check_in/all_app_data.do?b_search=link&dotflag=1";
        appForm.submit();
     }
   }
   function dd()
   {
   		if(confirm(KQ_APP_REBACK))
          return validate();
   		else{
   			return false;
   	   	}
   }
   
   function pizhun()
   {
   		if(confirm(KQ_APP_APPROVED))
	       return validate();
   		else{
   			return false;
   	    }
   }
   
   function shenhe()
   {
   		if(confirm(KQ_APP_CHECKED))
     	   return validate();
   		else{
   			return false;
   	   	}
   }
</script>
<html:form action="/kq/app_check_in/change_app" >
   <br><br>
  <table width="400" border="0" cellpadding="1" cellspacing="0" align="center" >
          <tr height="20">
       	    <!--  <td width=10 valign="top" class="tableft"></td>
       	    <td width=130 align=center class="tabcenter"><bean:message key="lable.overtime"/></td>
       	    <td width=10 valign="top" class="tabright"></td>
       	    <td valign="top" class="tabremain" width="500"></td> --> 
       	    <td align=center class="TableRow"><bean:message key="lable.overtime"/></td>            	      
          </tr> 
          <tr>
          <td colspan="4" class="framestyle9">
            <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <br><br>  
                <logic:iterate id="element" name="appForm"  property="viewlist" indexId="index"> 
                  <logic:equal name="element" property="visible" value="true">
                    <tr>         
                    <!--日期型 -->                            
                      <logic:equal name="element" property="itemtype" value="D"> 
                            <td align="right" class="tdFontcolor" nowrap >                
                              <bean:write  name="element" property="itemdesc" filter="true"/>:
                            </td>
                            <logic:match name="element" property="itemid" value="05">
                              	    <td align="left" class="tdFontcolor" nowrap>                               	                
                                     <html:text name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}'  size="20" maxlength="20" disabled = "true" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
                                     (时间格式：2012-10-01 08:30)
                                    </td>  
                            </logic:match>
                            <logic:match name="element" property="itemid" value="z1">
                                 <td align="left" class="tdFontcolor" nowrap>
                            		<logic:equal name="appForm" property="table" value="Q15"> 
	           							<logic:equal name="appForm" property="z5" value="03"> 
		            						<html:text name="appForm" disabled="true" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}'  size="20" maxlength="20" styleClass="TEXT4"/>
	           							</logic:equal> 
	           							<logic:notEqual name="appForm" property="z5" value="03"> 
		            						<html:text name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}'  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
	           							</logic:notEqual>
	           						</logic:equal>
	           						<logic:notEqual name="appForm" property="table" value="Q15">
	           								<html:text name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}'  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
	           						</logic:notEqual>
                                 </td>
                            </logic:match>
                            <logic:match name="element" property="itemid" value="z3">
                                 <td align="left" class="tdFontcolor" nowrap="nowrap">
                                 	<logic:equal name="appForm" property="table" value="Q15"> 
	           							<logic:equal name="appForm" property="z5" value="03"> 
		            						<html:text name="appForm" disabled="true" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}'  size="20" maxlength="20" styleClass="TEXT4"/>
	           							</logic:equal> 
	           							<logic:notEqual name="appForm" property="z5" value="03"> 
		            						<html:text name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}'  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
	           							</logic:notEqual>
	           						</logic:equal>
	           						<logic:notEqual name="appForm" property="table" value="Q15">
	           								 <html:text name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}'  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
	           						</logic:notEqual>            
                                 </td> 
                            </logic:match>   
                            <logic:match name="element" property="itemid" value="z7">
                              	    <td align="left" class="tdFontcolor" nowrap>                               	                
                                     <html:text name="appForm" property='<%="viewlist["+index+"].value"%>'  size="20" maxlength="20" disabled = "true"  styleClass="TEXT4"/>
                                    </td>  
                             </logic:match>
                             <logic:notMatch name="element" property="itemid" value="05">
	                             <logic:notMatch name="element" property="itemid" value="z1">
		                             <logic:notMatch name="element" property="itemid" value="z3">
			                             <logic:notMatch name="element" property="itemid" value="z7">
			                             <td align="left" class="tdFontcolor" nowrap> 
	                                     <html:text name="appForm" property='<%="viewlist["+index+"].value"%>'  size="20" maxlength="20" disabled = "true"  styleClass="TEXT4"/>
			                             </td>
			                             </logic:notMatch>
		                             </logic:notMatch>
	                             </logic:notMatch>
                             </logic:notMatch>
                         </logic:equal>
                     <!--备注型 -->                              
                     <logic:equal name="element" property="itemtype" value="M">
                        <td align="right" class="tdFontcolor" nowrap >                
                           <bean:write  name="element" property="itemdesc" filter="true"/>:
                        </td>  
                        <td align="left" class="tdFontcolor" nowrap>                
                            <logic:equal name="appForm" property="table" value="Q15"> 
	           					<logic:equal name="appForm" property="z5" value="03"> 
		            				<html:textarea  name="appForm" disabled="true"  property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}' cols="35" rows="4" styleClass="text5"/>
	           					</logic:equal> 
	           					<logic:notEqual name="appForm" property="z5" value="03"> 
		            				<html:textarea name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}' cols="35" rows="4" styleClass="text5"/>
	           					</logic:notEqual>
	           				</logic:equal>
	           				<logic:notEqual name="appForm" property="table" value="Q15">
	           					<html:textarea name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}' cols="35" rows="4" styleClass="text5"/>
	           				</logic:notEqual> 
                        </td>                           
                     </logic:equal>
                     <!--字符型 -->                                                    
                     <logic:equal name="element" property="itemtype" value="A">
                        <td align="right" class="tdFontcolor" nowrap="nowrap" >                
                          <bean:write  name="element" property="itemdesc" filter="true"/>:
                        </td>  
                        <td align="left" class="tdFontcolor" nowrap="nowrap">
                          <logic:notEqual name="element" property="codesetid" value="0">
                             <logic:equal name="element" property="readonly" value="true">
                                <html:text name="appForm" property='<%="viewlist["+index+"].viewvalue"%>' disabled = "true" size="31" maxlength="50" styleClass="TEXT4"/> 
                                <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}'/>  
                             </logic:equal>
                             <logic:equal name="element" property="readonly" value="false">
                             	 <logic:notEqual name="element" property="itemid" value="q1515">
                             	 <logic:notEqual name="element" property="itemid" value="q1115">
                             	 <logic:notEqual name="element" property="itemid" value="q1315">
                             	 <logic:notEqual name="element" property="itemid" value="q1511">
                             	 <logic:notEqual name="element" property="itemid" value="q1111">
                             	 <logic:notEqual name="element" property="itemid" value="q1311">
                             	 <logic:match name="element" property="itemid" value="03">   
                                <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>                               
                                <hrms:optioncollection name="appForm" property="salist" collection="list" />
	                               <html:select name="appForm" property="mess" size="1" disabled = "true"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select>
                                </logic:match>
                                </logic:notEqual>
                                </logic:notEqual>
                                </logic:notEqual>
                                </logic:notEqual>
                                </logic:notEqual> 
                                </logic:notEqual> 
	                              <logic:notMatch name="element" property="itemid" value="03">
	                              	<html:text name="appForm" property='<%="viewlist["+index+"].viewvalue"%>' disabled = "true" size="31" maxlength="50" styleClass="TEXT4"/> 
                                	<html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}'/>
	                              </logic:notMatch>
                              </logic:equal>
                              <logic:equal name="element" property="itemid" value="q1515">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salistko" collection="list" />
	                               <html:select name="appForm" property="mess1" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                              <logic:equal name="element" property="itemid" value="q1115">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salistko" collection="list" />
	                               <html:select name="appForm" property="mess1" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                              <logic:equal name="element" property="itemid" value="q1315">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salistko" collection="list" />
	                               <html:select name="appForm" property="mess1" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                               <logic:equal name="element" property="itemid" value="q1511">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salist11" collection="list" />
	                               <html:select name="appForm" property="mess2" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                              <logic:equal name="element" property="itemid" value="q1111">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salist11" collection="list" />
	                               <html:select name="appForm" property="mess2" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                              <logic:equal name="element" property="itemid" value="q1311">
                              <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleClass="text"/>
                              	 <hrms:optioncollection name="appForm" property="salist11" collection="list" />
	                               <html:select name="appForm" property="mess2" size="1" disabled = "false"  styleId='${element.itemid}'>
                                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                </html:select> 
                              </logic:equal>
                           </logic:notEqual> 
                           <logic:equal name="element" property="codesetid" value="0">
                               <logic:equal name="element" property="readonly" value="false">   
                                 <html:text name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}' size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                               </logic:equal>
                               <logic:equal name="element" property="readonly" value="true">
                                 <html:text name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}' disabled = "true" size="31" maxlength="${element.itemlength}" styleClass="TEXT4"/> 
                              </logic:equal>  
                           </logic:equal>                               
                         </td>                           
                      </logic:equal>
                      <!--数据值-->                            
                      <logic:equal name="element" property="itemtype" value="N">
                      <logic:equal name="element" property="itemid" value="q1104">
                         <td align="right" class="tdFontcolor" nowrap >                
                           <bean:write  name="element" property="itemdesc" filter="true"/>:
                         </td> 
                         <td align="left" class="tdFontcolor" nowrap="nowrap">                
                           <hrms:optioncollection name="appForm" property="class_list" collection="list" />
	                       <html:select name="appForm" property='<%="viewlist["+index+"].value"%>' size="1">
                           <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                           </html:select>
                         </td>   
                      </logic:equal>
                      
                      <logic:notEqual name="element" property="itemid" value="q1104">
                      <logic:notEqual name="element" property="itemid" value="q1304">
                      <logic:notEqual name="element" property="itemid" value="q1504">
                         <td align="right" class="tdFontcolor" nowrap >                
                           <bean:write  name="element" property="itemdesc" filter="true"/>:
                         </td>  
                         <td align="left" class="tdFontcolor" nowrap="nowrap">                
                            <bean:write name="appForm" property='<%="viewlist["+index+"].value"%>' />                              
                         </td>       
                       </logic:notEqual>
                       </logic:notEqual>
                       </logic:notEqual>                     
                       </logic:equal>                           
                     </tr>  
                    </logic:equal>  
                     <logic:equal name="element" property="visible" value="false">
                      <html:hidden name="appForm" property='<%="viewlist["+index+"].value"%>' styleId='${element.itemid}'/>  
                     </logic:equal>                  
                  </logic:iterate>
           <tr><td height="10"></td></tr>    
          <tr><td height="10"></td></tr>         
          </table>	            	
           </td>
           </tr>
           <tr class="list3">
            <td align="center" colspan="4" style="height:35px;">           	    
		    <logic:equal name="appForm" property="table" value="Q11"> 
		       <logic:equal name="appForm" property="z5" value="02"> 
		          <hrms:priv func_id="270102,0C3412">   
		            <hrms:submit styleClass="mybutton" property="b_update" onclick="return pizhun()">
            	        <bean:message key="button.approve"/>    
	                </hrms:submit>
	              </hrms:priv>  
	           </logic:equal> 
	           <logic:equal name="appForm" property="z5" value="08"> 
	              <hrms:priv func_id="27010c,0C341c"> 
		           <hrms:submit styleClass="mybutton" property="b_report" onclick="return shenhe()">
            	        <bean:message key="button.audit"/>    
	               </hrms:submit>
	              </hrms:priv> 
	           </logic:equal>
	           <logic:notEqual name="appForm" property="z5" value="10">
	            <hrms:submit styleClass="mybutton" property="b_reject" onclick="return dd()">
            	        <bean:message key="button.reject"/>    
	            </hrms:submit>
	            </logic:notEqual>
	 	        <html:button  styleClass="mybutton" property="b_return" onclick="toallapp('Q11');">
            	        <bean:message key="button.return"/>    
	            </html:button>
	         </logic:equal>  
	         <logic:equal name="appForm" property="table" value="Q15"> 
	         
	             <logic:equal name="appForm" property="z5" value="02"> 
		          <hrms:priv func_id="270112,0C3422">   
		            <hrms:submit styleClass="mybutton" property="b_update" onclick="return pizhun()">
            	        <bean:message key="button.approve"/>    
	                </hrms:submit>
	              </hrms:priv>  
	           </logic:equal> 
	           <logic:equal name="appForm" property="z5" value="08"> 
	              <hrms:priv func_id="27011c,0C342c"> 
		           <hrms:submit styleClass="mybutton" property="b_report" onclick="return shenhe()">
            	        <bean:message key="button.audit"/>    
	               </hrms:submit>
	              </hrms:priv> 
	           </logic:equal>
	           <logic:notEqual name="appForm" property="isAllow" value="false">
	            <hrms:submit styleClass="mybutton" property="b_reject" onclick="return dd()">
            	        <bean:message key="button.reject"/>    
	            </hrms:submit>
	           </logic:notEqual>
	 	         <html:button  styleClass="mybutton" property="b_return" onclick="toallapp('Q15');">
            	        <bean:message key="button.return"/>    
	              </html:button>
	         </logic:equal>  
	          <logic:equal name="appForm" property="table" value="Q13"> 
	           <logic:equal name="appForm" property="z5" value="02"> 
		          <hrms:priv func_id="270122,0C3432">   
		            <hrms:submit styleClass="mybutton" property="b_update" onclick="return pizhun()">
            	        <bean:message key="button.approve"/>    
	                </hrms:submit>
	              </hrms:priv>  
	           </logic:equal> 
	           <logic:equal name="appForm" property="z5" value="08"> 
	              <hrms:priv func_id="27012c,0C343c"> 
		           <hrms:submit styleClass="mybutton" property="b_report" onclick="return shenhe()">
            	        <bean:message key="button.audit"/>    
	               </hrms:submit>
	              </hrms:priv> 
	           </logic:equal>
	           <logic:notEqual name="appForm" property="z5" value="10">
	              <hrms:submit styleClass="mybutton" property="b_reject" onclick="return dd()">
            	        <bean:message key="button.reject"/>    
	              </hrms:submit>
	              </logic:notEqual>
	 	          <html:button  styleClass="mybutton" property="b_return" onclick="toallapp('Q13');">
            	        <bean:message key="button.return"/>    
	              </html:button>
	         </logic:equal>  
             </td>
             </tr>                   
     </table>
</html:form>
<script language="javascript">
reSaveFlag();
</script> 
