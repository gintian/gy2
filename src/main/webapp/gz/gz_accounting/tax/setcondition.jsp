<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="Javascript" src="/gz/salary.js"/></script>
<%int i=0;
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}

%>
<style type="text/css">
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 21px; 
 PADDING-BOTTOM: 21px;
 BORDER-BOTTOM: #7b9ebd 1px solid
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 5px; 
 PADDING-BOTTOM: 5px;
 BORDER-BOTTOM: #7b9ebd 1px solid
}
</style>
<script language="javascript">
	
	
	 function get_tax_sql(){
		var sql = document.getElementById("condtionsql").value;
   		if(sql!="")
   		{
 			window.returnValue = sql; 
   			window.close();
   		}else{	
   			taxTableForm.action="/gz/gz_accounting/tax/setcondition.do?b_sub=link";
			taxTableForm.submit(); 	

   		}	
	}	

	function init()
   	{
   		var sql = document.getElementById("condtionsql").value;
   		if(sql!="")
   		{
   			window.returnValue = sql;
   			window.close();
   		}
   		
   	}
	function symbol(cal){
		if(document.getElementById("expression").pos!=null){
			document.getElementById("expression").pos.text=cal;
		}else{
			alert("请将光标移向编辑区");
		}
	}
	function sub()
	{
		get_tax_sql();
	}
	
	function b_cancel(){
		window.returnValue = "cancel"; 
   		window.close();
	}

	var date_desc;
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
     }
   }
   function showDateSelectBox(srcobj){
       //if(event.button==2)
       //{
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
		  with($('date_panel'))
		  {
		        style.position="absolute";
	    		style.posLeft=pos[0]-1;
				style.posTop=pos[1]-1+srcobj.offsetHeight;
				style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
	      }                 
       //}
   }
   
   
</script>
<html:form action="/gz/gz_accounting/tax/setcondition">

<html:hidden name="taxTableForm" property="expre"/>
<html:hidden name="taxTableForm" property="condtionsql" />
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
<div class="fixedDiv3">
<div style="width:100%;height:340px;overflow: auto;" class="complex_border_color">
<table border="0"  cellspacing="0" width="100%"  cellpadding="2" align="center">
   <tr class="fixedHeaderTr">   
      <td align="center" nowrap class="TableRow" style="border-top:none;border-left:none;"><bean:message key="label.query.number"/></td>                                        	      
      <td align="center" nowrap class="TableRow" style="border-top:none;border-left:none;"><bean:message key="label.query.field"/></td>
      <td align="center" nowrap class="TableRow" style="border-top:none;border-left:none;"><bean:message key="label.query.relation"/></td>
      <td align="center" nowrap class="TableRow" style="border-top:none;border-left:none;border-right:none;"><bean:message key="label.query.value"/></td>          	
   </tr>
   <logic:iterate id="element" name="taxTableForm"  property="factorlist" indexId="index">
   <tr>
      <td align="center" class="RecordRow"style="border-top:none;border-left:none;" nowrap >&nbsp;
      	 <%
         	if(i!=0)
         	{
         %>
	       <hrms:optioncollection name="taxTableForm" property="logiclist" collection="list"/>
           <html:select name="taxTableForm" property='<%="factorlist["+index+"].log"%>' size="1">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
           </html:select>
         <%
           }
         %>&nbsp;
	  </td>
      <td align="center" class="RecordRow" style="border-top:none;border-left:none;" nowrap >
         <bean:write name="element" property="hz" />&nbsp;
      </td>  
      <td align="center" class="RecordRow" style="border-top:none;border-left:none;" nowrap >
         <hrms:optioncollection name="taxTableForm" property="operlist" collection="list"/>
            <html:select name="taxTableForm" property='<%="factorlist["+index+"].oper"%>' size="1">
               <html:options collection="list" property="dataValue" labelProperty="dataName"/>
            </html:select>
      </td>
      <!--日期型 -->                            
      <logic:equal name="element" property="fieldtype" value="D">
      <td align="left" class="RecordRow"style="border-top:none;border-left:none;border-right:none;" nowrap>                
	  		<html:text name="taxTableForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="10" styleClass="text4" ondblclick="showDateSelectBox(this);"  onblur="Element.hide('date_panel');" />
      </td>                           
      </logic:equal>
      <!--备注型 -->                              
      <logic:equal name="element" property="fieldtype" value="M">
      <td align="left" class="RecordRow"style="border-top:none;border-left:none;border-right:none;" nowrap>                
         <html:text name="taxTableForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' styleClass="text4"/>                               
      </td>                           
      </logic:equal>
      <!--字符型 -->                                                    
      <logic:equal name="element" property="fieldtype" value="A">
      <td align="left" class="RecordRow"style="border-top:none;border-left:none;border-right:none;" nowrap>
         <logic:notEqual name="element" property="codeid" value="0">
            <html:hidden name="taxTableForm" property='<%="factorlist["+index+"].value"%>' styleClass="text4"/>                               
            <html:text name="taxTableForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/>
            <span style="vertical-align: bottom;">
            <logic:notEqual name="element" property="codeid" value="UN">     
            	<logic:equal name="element" property="codeid" value="@@">                                                  
            	</logic:equal>      
            	<logic:notEqual name="element" property="codeid" value="@@">                        
                	<img src="/images/code.gif" style="vertical-align: -20%;" onclick='openCondCodeDialogsx("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
                 </logic:notEqual> 
            </logic:notEqual>   
            <logic:equal name="element" property="codeid" value="UN">
                   <img src="/images/code.gif" style="vertical-align: -20%;" onclick='openCondCodeDialogsx("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>                                   
            </logic:equal> 
            </span>                                                                                                      
         </logic:notEqual> 
         <logic:equal name="element" property="codeid" value="0">
              <html:text name="taxTableForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
         </logic:equal>                            
      </td>                           
      </logic:equal> 
      <!--数据值-->                            
      <logic:equal name="element" property="fieldtype" value="N">
      <td align="left" class="RecordRow" style="border-top:none;border-left:none;border-right:none;" nowrap>                
          <html:text name="taxTableForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="10" styleClass="text4"/>                               
      </td>                           
      </logic:equal>                 
   </tr>  
    <%i++;%>
   </logic:iterate>
</table>
</div>
<table border="0"  cellspacing="0" width="85%"   cellpadding="2" align="center">
  <tr>
  	<td width="75%">
  		
  	</td>	
  </tr>
  <tr>
  	<td align="center">
  		<input type="button" name="button1"  value='上一步' onclick="window.close();" Class="mybutton" > 
  		<input type="button" name="button2"  value=' 确定 ' onclick="sub();" Class="mybutton" > 
  		<input type="button" name="button3"  value='取消' onclick="b_cancel();" Class="mybutton" > 
  		<html:reset styleClass="mybutton">
                    <bean:message key="button.clear"/>
	       </html:reset> 
  	</td>
  </tr>
  </table>
  <div id="date_panel">
   			<select name="date_box" multiple="multiple" size="10"  style="width:200" onchange="setSelectValue();">    
			    <option value="$YRS[10]">年限</option>
			    <option value="当年">当年</option>
			    <option value="当月">当月</option>
			    <option value="当天">当天</option>				    
			    <option value="今天">今天</option>
			    <option value="截止日期">截止日期</option>
                            <option value="1992.4.12">1992.4.12</option>	
                            <option value="1992.4">1992.4</option>	
                            <option value="1992">1992</option>			    
			    <option value="????.??.12">????.??.12</option>
			    <option value="????.4.12">????.4.12</option>
			    <option value="????.4">????.4</option>			    			    		    
                        </select>
                    </div>
</div>             
</html:form>
<script language="javascript">
   Element.hide('date_panel');
    init();
</script>