<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<script language="JavaScript" src="/js/meizzDate.js"></script>
	<SCRIPT LANGUAGE=javascript>
	
	function check()
	{
		
		var relation=new Array();
		var fielditemid=new Array();
		var operate=new Array();
		var values=new Array();
		var a=0;		
		if(document.employActualizeForm.relation.length)
		{
			for(var i=0;i<document.employActualizeForm.relation.length;i++)
			{
			
				var a_relation=document.employActualizeForm.relation[i].value;
				var a_fielditemid=document.employActualizeForm.itemid[i].value;
				var a_operate=document.employActualizeForm.operate[i].value;
				var name=$("aa"+(i+1)+".value");	
						
				//if(name.value!=''&&name.value!=' ')
				{
					relation[a]=a_relation;
					fielditemid[a]=a_fielditemid;
					operate[a]=a_operate;
					values[a]=name.value;
					a++;
				}
			
			}
		}
		else
		{
				var a_relation=document.employActualizeForm.relation.value;
				var a_fielditemid=document.employActualizeForm.itemid.value;
				var a_operate=document.employActualizeForm.operate.value;
				var name=$("aa1.value");	
						
				//if(name.value!=''&&name.value!=' ')
				{
					relation[a]=a_relation;
					fielditemid[a]=a_fielditemid;
					operate[a]=a_operate;
					values[a]=name.value;
					a++;
				}
				
		
		}	
		if(relation.length==0)
		{
			alert(SELECTCONDITION+"！");
			return;
		}
		
		
		var hashvo=new ParameterSet();
		 hashvo.setValue("relation",relation);
		 hashvo.setValue("fielditemid",fielditemid);
		 hashvo.setValue("operate",operate);
		 hashvo.setValue("values",values);
		 In_paramters='flag=1';
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000145'},hashvo);		
	}
	
	
	
	function returnInfo(outparamters)
	{
	
		
		var sql=outparamters.getValue("sql");	
		employActualizeForm.sql.value=sql;
		
		var columns_str=outparamters.getValue("columns_str");
		employActualizeForm.column_str.value=columns_str;
		employActualizeForm.action="/hire/employActualize/personnelFilter/personnelFilterTree.do?b_query2=query";
		employActualizeForm.submit();
		
	}
	
	//上一步
	function pre_phase()
	{
	
		employActualizeForm.action='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_condition=link';
        employActualizeForm.submit();
	
	}
	
	
	
	
	</SCRIPT>
	
<html:form action="/hire/employActualize/personnelFilter/personnelFilterTree">	




   <br>
	<br>	
    <fieldset align="center" style="width:70%;">
        <legend ><bean:message key="button.c.query"/></legend>
        <table border="0" cellspacing="0" width="100%"  align="center" cellpadding="0" >
          
          <tr > 
            <td> 
               <table border="0"  cellspacing="0" width="100%" class="ListTable"  cellpadding="2" align="center">
                <tr> 
                  <td colspan="4"> 
                  <br>
                  <table border="0"  cellspacing="0" width="97%" class="ListTable1"  cellpadding="2" align="center">
                      <tr> 
                        <td width="16%" align="center" nowrap class="TableRow"><bean:message key="label.query.logic"/></td>
                        <td width="29%" align="center" nowrap class="TableRow"><bean:message key="kq.formula.parameter"/></td>
                        <td width="13%" align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                        <td width="42%" align="center" nowrap class="TableRow"><bean:message key="edit_report.compareValue"/></td>
                      </tr>
                      
                      
          				<% int i=0; %>
          				<logic:iterate id="element" name="employActualizeForm" property="selectedFieldList"  > 
               	 
                      <tr> 
                        <td align="center" class="RecordRow" nowrap > 
                         <% if(i++==0){ %>
                         	<input type='hidden' name='relation' value='*' />
                         <% } else { %>
                          <select name="relation" size="1">
                            <option value="*" selected="selected"><bean:message key="kq.wizard.even"/></option>
                            <option value="+"><bean:message key="kq.wizard.and"/></option>
                          </select> 
                          <% } %>&nbsp;
                          </td>
                        <td align="center" class="RecordRow" nowrap > <input type='hidden' name='itemid' value='<bean:write name="element" property="itemid" />§§<bean:write name="element" property="itemtype" />§§<bean:write name="element" property="itemsetid" />§§<bean:write name="element" property="fieldsetid" />' />      <bean:write name="element" property="itemdesc" /></td>
                        <td align="center" class="RecordRow" nowrap > <select name="operate" size="1" style="width:100%">
                            <option value="=" selected="selected">=</option>
                            <option value="&gt;">&gt;</option>
                            <option value="&gt;=">&gt;=</option>
                            <option value="&lt;">&lt;</option>
                            <option value="&lt;=">&lt;=</option>
                            <option value="&lt;&gt;">&lt;&gt;</option>
                          </select> </td>
                       
                        <td align="left" class="RecordRow" nowrap>
                         <!--日期型 -->                            
                          <logic:equal name="element" property="itemtype" value="D">     
                          		<input type='text' name="aa<%=i%>.value" size="20" maxlength="10" onfocus='inittime(false);setday(this);' readOnly />                                                                 
                          </logic:equal>                     
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                           
                              <logic:notEqual name="element" property="itemsetid" value="0">
                              		  <input type='hidden' name="aa<%=i%>.value" />
                        	   		  <input type="text" name="aa<%=i%>.hzvalue"   size="24" value="" readOnly class="TEXT4"> 
                       				  <img  src="/images/code.gif"  onclick='openCondCodeDialog("<bean:write name="element" property="itemsetid" />","aa<%=i%>.hzvalue");'   />                                                                                                
                              </logic:notEqual> 
                              <logic:equal name="element" property="itemsetid" value="0">
                                     <input type='text' name="aa<%=i%>.value" size="20" />                             
                              </logic:equal>                                                                               
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">                                    
                             <input type='text' name="aa<%=i%>.value" size="20" />                                                                                      
                          </logic:equal> 
                          </td>
                       
                      </tr>
                    
                    	 </logic:iterate>
                    
                      <!-- 查询定义才出现此选项 -->
                      <tr> 
                        <td align="center" nowrap class="RecordRow" colspan="4">&nbsp;</td>
                      </tr>
                    </table></td>
                </tr>
                <tr> 
                  <td height="15" colspan="4"></td>
                </tr>
              </table>	            				
			</td>
          </tr>		  
          <tr> 
            <td align="center"> <br>
            <input type="button" value="<bean:message key="button.query.pre"/>" class="mybutton" onclick='pre_phase()'  />             
            <input type="reset" value="<bean:message key="button.clear"/>" class="mybutton">          
            <input type="button" name="b_update" value="<bean:message key="button.ok"/>"  onclick='check()'  class="mybutton"> 
              </td>
          </tr>
        </table>
	</fieldset>


<input type='hidden' name='sql' value='' />
<input type='hidden' name='column_str' value='' />

</html:form>