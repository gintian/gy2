<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<script language="JavaScript" src="/js/meizzDate.js"></script>
	<SCRIPT LANGUAGE=javascript>
	
	function check()
	{
		
		<% int n=0;  %>
		<logic:iterate  id="element"    name="generalQueryForm"  property="selectedFieldList" indexId="index"> 
			<% n++; %>
			<logic:equal name="element" property="itemtype" value="N">
					var a<%=n%>=document.getElementsByName("aa<%=n%>.value")
					if(a<%=n%>[0].value!='')
					{
					 var myReg =/^(-?\d+)(\.\d+)?$/
					 if(!myReg.test(a<%=n%>[0].value)) 
					 {
						alert("<bean:write  name="element" property="itemdesc"/>请输入数字！");
						return;
					 }
					 }
			</logic:equal>
			
		</logic:iterate>

		var relation=new Array();
		var fielditemid=new Array();
		var operate=new Array();
		var values=new Array();
		var a=0;		
		if(document.generalQueryForm.relation.length)
		{
			for(var i=0;i<document.generalQueryForm.relation.length;i++)
			{
			
				var a_relation=document.generalQueryForm.relation[i].value;
				var a_fielditemid=document.generalQueryForm.itemid[i].value;
				var a_operate=document.generalQueryForm.operate[i].value;
				var name=$("aa"+(i+1)+".value");	
						
				//if(name.value!=''&&name.value!=' ')
				{
					relation[a]=a_relation;
					fielditemid[a]=a_fielditemid;
					operate[a]=a_operate;
					values[a]=getEncodeStr(name.value);
					a++;
				}
			}
		}
		else
		{
				var a_relation=document.generalQueryForm.relation.value;
				var a_fielditemid=document.generalQueryForm.itemid.value;
				var a_operate=document.generalQueryForm.operate.value;
				var name=$("aa1.value");	
						
			//	if(name.value!=''&&name.value!=' ')
				{
					relation[a]=a_relation;
					fielditemid[a]=a_fielditemid;
					operate[a]=a_operate;
					values[a]=getEncodeStr(name.value);
					a++;
				}
		
		}	
		if(relation.length==0)
		{
			alert("请选择条件！");
			return;
		}
		
		
		var hashvo=new ParameterSet();
		 hashvo.setValue("relation",relation);
		 hashvo.setValue("fielditemid",fielditemid);
		 hashvo.setValue("operate",operate);
		 hashvo.setValue("values",values);
		 
		 hashvo.setValue("tableName","${generalQueryForm.tableName}");
		 In_paramters='flag=1';
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'0202011010'},hashvo);		
	}
	
	
	
	function returnInfo(outparamters)
	{
		var sql=outparamters.getValue("sql");				
		returnValue=sql; 
		window.close();
		
	}
	
	//上一步
	function pre_phase()
	{
		document.generalQueryForm.action="/general/query/general/initGeneralQuery.jsp";
		document.generalQueryForm.submit();
	}
	
	
	
	
	</SCRIPT>

<base id="mybase" target="_self">
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    
%>		
<html:form action="/general/query/general/generalQuery">	
<%if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<br>
<br>
<% 
 }
%>	
<div width="550px">
    <fieldset align="center" style="width:100%;">
        <legend ><bean:message key="button.c.query"/></legend>
        <table border="0" cellspacing="0" width="100%"  align="center" cellpadding="0" style="margin-top:10px;" >
          
          <tr > 
            <td> 
               <table border="0"  cellspacing="0" width="100%" class="ListTable"  cellpadding="0" align="center">
                <tr> 
                  <td colspan="4"> 
                  <table border="0"  cellspacing="0" width="97%" class="ListTable1"  cellpadding="2" align="center">
                      <tr> 
                        <td width="16%" align="center" nowrap class="TableRow"><bean:message key="label.query.logic"/></td>
                        <td width="29%" align="center" nowrap class="TableRow"><bean:message key="kq.formula.parameter"/></td>
                        <td width="13%" align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                        <td width="42%" align="center" nowrap class="TableRow"><bean:message key="edit_report.compareValue"/></td>
                      </tr>
          				<% int i=0; %>
          				<logic:iterate id="element" name="generalQueryForm" property="selectedFieldList"  > 
               	 
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
                        <td align="center" class="RecordRow" nowrap > <input type='hidden' name='itemid' value='<bean:write name="element" property="itemid" />§§<bean:write name="element" property="itemtype" />§§<bean:write name="element" property="itemsetid" />' />      <bean:write name="element" property="itemdesc" /></td>
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
                          		<input type='text' name="aa<%=i%>.value" extra="editor" value="" style="width:153px;font-size:10pt;text-align:left" id="last_begin"  dropDown="dropDownDate" />                                 
                          </logic:equal>                     
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                           
                              <logic:notEqual name="element" property="itemsetid" value="0">
                              		  <input type='hidden' name="aa<%=i%>.value" />
                        	   		  <input type="text" name="aa<%=i%>.hzvalue"   size="24" value="" readOnly style="width:153px;" class="TEXT4"> 
                        	   		  <span>
                       				  <img  src="/images/code.gif"  onclick='openCondCodeDialogsx("<bean:write name="element" property="itemsetid" />","aa<%=i%>.hzvalue");' style="position:relative;top:5px;"  />
                       				  </span>                                                                                                
                              </logic:notEqual> 
                              <logic:equal name="element" property="itemsetid" value="0">
                                     <input type='text' name="aa<%=i%>.value" size="20" style="width:153px;" class="TEXT4"/>                             
                              </logic:equal>                                                                               
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">                                    
                             <input type='text' name="aa<%=i%>.value" size="20" style="width:153px;" class="TEXT4"/>                                                                                      
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
              </table>	            				
			</td>
          </tr>		  
          <tr> 
            <td align="center">
            <input type="button" value="<bean:message key="button.query.pre"/>" class="mybutton" onclick='pre_phase()'  style="margin-top:5px;margin-bottom:5px;"/>
            <input type="reset" value="<bean:message key="button.clear"/>" class="mybutton" style="margin-top:5px;margin-bottom:5px;">          
            <input type="button" name="b_update" value="<bean:message key="button.ok"/>"  onclick='check()'  class="mybutton" style="margin-top:5px;margin-bottom:5px;"> 
              </td>
          </tr>
        </table>
	</fieldset>
</div>

<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>


</html:form>