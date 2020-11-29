<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.actionform.general.query.QuickQueryForm"%>
<%
 int i=0;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getManagePrivCodeValue();  
	QuickQueryForm form=(QuickQueryForm)session.getAttribute("quickQueryForm");
	/**
	* 由先前的按人员管理范围控制改成按如下规则进行控制:
	* 人员、单位和岗位按业务范围-操作单位-人员管理范围优先级进行控制 
	* cmq changed at 2012-09-29
	*/	
	if(form.getType().equalsIgnoreCase("1")||form.getType().equalsIgnoreCase("2")||form.getType().equalsIgnoreCase("3"))	
	{
		manager=userView.getUnitIdByBusi("4");
	}
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
//end.
%>

<script language="javascript">
   function validate_dbase(infor)
   {
     hideDbase('${quickQueryForm.type}')
     if(infor=="1"&&(!$F('dbpre')))
     {
       alert('<bean:message key="errors.static.notdbname"/>');
       return false;
     }
     return true;
   }
   
   /*只有一个库时,对库进行隐藏*/
   function hideDbase(infor)
   {
     if(infor!="1")
       return;
     var elements=$('dbpre');
     if(!(elements instanceof Array))
     {
       elements.checked=true;
       Element.hide('dbase');
     }
   }
   function openCodeDialog(codeid,mytarget,managerstr,flag) 
    {
        var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
        if(mytarget==null)
          return;
        var oldInputs=document.getElementsByName(mytarget);
        oldobj=oldInputs[0];
        //根据代码显示的对象名称查找代码值名称	
        target_name=oldobj.name;
        hidden_name=target_name.replace(".viewvalue",".value"); 
        hidden_name=hidden_name.replace(".hzvalue",".value");
        hidden_name=hidden_name.replace("name1","namevalue");
        var hiddenInputs=document.getElementsByName(hidden_name);
        if(hiddenInputs!=null&&hiddenInputs.length>0)
        {
        	hiddenobj=hiddenInputs[0];
        	codevalue=managerstr;
        }else{
        	hiddenobj=document.getElementById(hidden_name);
        	codevalue=managerstr;
        }
        var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag);
        var type='${quickQueryForm.type}'; 
        if(type == '1') 
       	    thecodeurl="/org/orgpre/getorgcode.jsp?ctrl_type=2&levelctrl=0";
       	else
       	    thecodeurl="/org/orgpre/getorgcode.jsp?ctrl_type=1&levelctrl=0";
        var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
        var popwin= window.showModalDialog(thecodeurl, theArr, 
            "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    }   
</script>
<%
	if ("hcm".equalsIgnoreCase(bosflag)) {
%>
<style>
.quickqueryTable {
	width: expression(document.body.clientWidth-10);
}
</style>
<%
	} else {
%>
<style>
.quickqueryTable {
	margin-top: 10px;
	width: expression(document.body.clientWidth-10);
}
</style>
<%
	}
%>
<base id="mybase" target="_self">
<html:form action="/general/query/quick/quick_query">
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="quickqueryTable">
          <tr height="20">
       		<!-- td width="10" valign="top" class="tableft"></td>
       		<td width="130" align=center class="tabcenter"></td>
       		<td width="10" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>   -->
       		<td align="left" colspan="4" class="TableRow"><bean:message key="label.query.inforquery"/></td>            	      
          </tr> 
          <tr>
            <td colspan="4" class="framestyle3">
               <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" align="center" >     
       	             <logic:equal name="quickQueryForm" property="type" value="1">
                      <tr id="dbase">
                	      <td align="right" nowrap ><bean:message key="label.query.dbpre"/></td>
                	      <td align="left" nowrap>
                	       <logic:iterate id="element" name="quickQueryForm"  property="dblist" indexId="index"> 
                	         <html:multibox name="quickQueryForm" property="dbpre" value="${element.dataValue}"/><bean:write  name="element" property="dataName" filter="true"/>
				 	`			<% ++i;if(i%4==0){ %>
                                   <br>
                                 <%}%>
                	       </logic:iterate>
                              </td>
                      </tr>
                      </logic:equal> 
                      <logic:iterate id="element" name="quickQueryForm"  property="fieldlist" indexId="index"> 
                      <tr>           
                          <td align="right" class="tdFontcolor" nowrap >                
                            <bean:write  name="element" property="itemdesc" filter="true"/>
                          </td>
                          <!--日期型 -->                            
                          <logic:equal name="element" property="itemtype" value="D">
                            <td align="left" class="tdFontcolor" nowrap>                
                               <html:text name="quickQueryForm" property='<%="fieldlist["+index+"].value"%>' size="13" maxlength="10" styleClass="TEXT4"/>
                               <bean:message key="label.query.to"/>
                               <html:text name="quickQueryForm" property='<%="fieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleClass="TEXT4"/>
			       <!-- 没有什么用，仅给用户与视觉效果-->
			       <INPUT type="radio" name="${element.itemid}" checked=true><bean:message key="label.query.day"/><INPUT type="radio" name="${element.itemid}"><bean:message key="label.query.age"/>			                         	                                            
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="itemtype" value="M">
                            <td align="left" class="tdFontcolor" nowrap>                
                               <html:text name="quickQueryForm" property='<%="fieldlist["+index+"].value"%>' size="32" maxlength='<%="fieldlist["+index+"].itemlength"%>' styleClass="TEXT4"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                            <td align="left" class="tdFontcolor" nowrap>
                              <logic:notEqual name="element" property="codesetid" value="0">
                                <html:hidden name="quickQueryForm" property='<%="fieldlist["+index+"].value"%>' styleClass="text"/>                               
                                <html:text name="quickQueryForm" property='<%="fieldlist["+index+"].viewvalue"%>' size="32" maxlength="50" styleClass="TEXT4" onchange="fieldcode(this,2);"/>
                                   <logic:equal name="element" property="itemid" value="b0110"> 
                                     <img src="/images/code.gif" onclick='openCodeDialog("UN","<%="fieldlist["+index+"].viewvalue"%>","<%=manager %>",1);' align="middle"/>
                                   </logic:equal> 
                                   <logic:notEqual name="element" property="itemid" value="b0110">   
                                  	<logic:equal name="element" property="itemid" value="e0122"> 
                                    		<img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("UM","<%="fieldlist["+index+"].viewvalue"%>","<%=manager%>",1);' align="middle"/>
                                  	</logic:equal>
                                  	<logic:notEqual name="element" property="itemid" value="e0122"> 
	                                  	<logic:equal name="element" property="itemid" value="e01a1"> 
	                                    		<img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("@K","<%="fieldlist["+index+"].viewvalue"%>","<%=manager%>",1);' align="middle"/>
	                                  	</logic:equal>
	                                  	<logic:notEqual name="element" property="itemid" value="e01a1"> 
	                        					<img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="fieldlist["+index+"].viewvalue"%>");' align="middle"/> 
	                                    </logic:notEqual>                                   	
                                    </logic:notEqual>                                          	                                                                                   
                                   </logic:notEqual>                                  
                              </logic:notEqual> 
                              <logic:equal name="element" property="codesetid" value="0">
                                <html:text name="quickQueryForm" property='<%="fieldlist["+index+"].value"%>' size="32" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">
                            <td align="left" class="tdFontcolor" nowrap>                
                               <html:text name="quickQueryForm" property='<%="fieldlist["+index+"].value"%>' size="32" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>                            
                       </logic:iterate>
                       <tr><td height="5"></td></tr> 
                    <tr >
                      <td colspan="2" align="center">
          	       <html:checkbox name="quickQueryForm" property="like" value="1"><bean:message key="label.query.like"/></html:checkbox>
          	       <html:checkbox name="quickQueryForm" property="result" value="1"><bean:message key="hmuster.label.search_result"/></html:checkbox>
                       <html:checkbox name="quickQueryForm" property="history" value="1"><bean:message key="label.query.history"/></html:checkbox>	       
                      </td>
                    </tr>  
                     <tr id="lert"><td colspan="5" align="left">提示：字符型、代码型指标可使用通配符 "*" 或 "?" 辅助查询</td></tr>                                           
	       </table>	            	
            </td>
          </tr>
          <tr class="list3">
            <td colspan="4" height="35px" align="center">
               <hrms:submit styleClass="mybutton"  property="b_query" onclick="return validate_dbase('${quickQueryForm.type}');">
                    <bean:message key="button.query"/>
	       </hrms:submit>
              <html:reset styleClass="mybutton" property="bc_clear" >
                    <bean:message key="button.clear"/>
	       </html:reset> 
            </td>
          </tr>          
  </table>
 </html:form>
<script language="javascript">
   hideDbase('${quickQueryForm.type}');
</script>