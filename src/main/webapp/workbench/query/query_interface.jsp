<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	//String manager=userView.getManagePrivCodeValue();
	String manager=userView.getUnitIdByBusi("4");
	if(userView.isSuper_admin())
	    manager = "";
%>
<script language="javascript">
function checkDay(obj,ve)
{
    var o_obj=document.getElementById('day');   
    if(o_obj&&o_obj.checked==true)
    {
       var ttop  = obj.offsetTop;     //TT控件的定位点高
	   var thei  = obj.clientHeight;  //TT控件本身的高
	   var tleft = obj.offsetLeft;    //TT控件的定位点宽
	   var waitInfo=eval("wait")
	   while (obj = obj.offsetParent){ttop+=obj.offsetTop; tleft+=obj.offsetLeft;}
	   waitInfo.style.top=ttop+thei+6;
	   ve=3;
	   if(ve==1)
	      waitInfo.style.left=tleft+326;
	   else if(ve==2)   
	      waitInfo.style.left=tleft+220;
	   else
	      waitInfo.style.left=tleft;
	   waitInfo.style.display="block";
	   
    }else
    { 
       var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
    }
}
function checkHide()
{
  Element.hide('wait');
}
function selectCheckBox(obj)
{
   if(obj.checked==true)
   {
      var Info=eval("info_cue1");	
	  //Info.style.display="block";
	  Info.style.display="";//非Ie浏览器中 display 显示时直接为空  页面样式等同IE下效果   bug 35095 wangb 20180301
   }else
   {
       var Info=eval("info_cue1");	
	   Info.style.display="none";
      
   }

}

function search(){
	if(!validate('RS','dbpre','人员库'))
		return false;
	
	var searchButton = document.getElementById("searchButton");
	if(searchButton)
		searchButton.disabled = true;
	
	var gqueryButton = document.getElementById("gqueryButton");
	if(gqueryButton)
		gqueryButton.disabled = true;
	
	var bcButton = document.getElementById("bcButton");
	if(bcButton)
		bcButton.disabled = true;
	
	var bcButtons = document.getElementById("bcButtons");
	if(bcButtons)
		bcButtons.disabled = true;
	
	var resetButton = document.getElementById("resetButton");
	if(resetButton)
		resetButton.disabled = true;
	
	queryInterfaceForm.action="/workbench/query/query_interface.do?b_mquery=link";
	queryInterfaceForm.submit();
}

function checkDate(obj){
	var radio = document.getElementById("day");
	if(radio && radio.checked) {
		if(!obj.value){
			return true;
		}
		
		var dateValue = replaceAll(obj.value,".","-");
		var checkFlag = checkDateTime(obj.value);
		if(!checkFlag) {
			obj.value="";
			obj.focus();
			alert(INPUT_FORMAT_DATE);
			return false;
		}
	}
}

function checkDates(itemid){
	var radio = document.getElementById("day");
    if(radio && radio.checked) {
		var flag = checkDate(document.getElementById(itemid + "S"));
    	if(!flag){
    		return false;
    	}
    	
    	flag = checkDate(document.getElementById(itemid + "E"));
        if(!flag){
            return false;
        }
    }
}

</script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language='JavaScript' src='/components/codeSelector/codeSelector.js'></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<hrms:themes />
<style>
.x-btn-default-toolbar-small .x-frame-tl{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-tc{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-tr{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-bl{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-bc{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-br{
	background-color: #c5c5c5;
	background-image: none;
}

</style>
<html:form action="/workbench/query/query_interface" style="margin-top:5px;">
  <html:hidden name="queryInterfaceForm" property="home"/> 
  <table width="700" border="0" cellpadding="0" cellspacing="0" align="center" style=""  class="">
          <tr height="20">
       		<td  align="left" style="margin-left: 5px" class="TableRow_lrt"><bean:message key="label.query.inforquery"/></td>
          </tr> 
          <tr>
            <td class="framestyle">

               <table border="0" cellpmoding="" cellspacing=""  cellpadding="0" width="100%" >
                 <tr>
                 	<td height="10"></td>
                 </tr>
                 <tr>
                   <td align="left">
                    <table border="0" cellpmoding="0" cellspacing="0"   cellpadding="0" align="center" >     
       	             <logic:equal name="queryInterfaceForm" property="type" value="1">
                      <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                               sql="queryInterfaceForm.dbcond" collection="list" scope="page"/>
                      <bean:size id="length" name="list" scope="page"/>
                      <tr  <logic:lessThan value="2" name="length">style="display: none"</logic:lessThan>>
                	      <td align="right" nowrap class="tdFontcolor"><bean:message key="label.query.dbpre"/></td>
                	      <td align="left" nowrap class="tdFontcolor">
                               <html:select name="queryInterfaceForm" property="dbpre" size="1">
                                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                                  <html:option value="All">全部人员库</html:option>                                  
                               </html:select>
                              </td>
                      </tr>
                      </logic:equal> 
                      <logic:iterate id="element" name="queryInterfaceForm"  property="fieldlist" indexId="index"> 
                      <tr>           
                          <td align="right" class="tdFontcolor" nowrap>                
                            <bean:write  name="element" property="itemdesc" filter="true"/>
                          </td>
                          <!--日期型 -->                            
                          <logic:equal name="element" property="itemtype" value="D">
                            <td align="left" class="tdFontcolor" nowrap>    
                               
                               <html:text name="queryInterfaceForm" property='<%="fieldlist["+index+"].value"%>' styleId="${element.itemid}S" onblur="checkDate(this)" size="13" maxlength="10" styleClass="TEXT4" title="输入数字或日期，日期格式：2008.08.08"/>
                               <bean:message key="label.query.to"/>
                               <html:text name="queryInterfaceForm" property='<%="fieldlist["+index+"].viewvalue"%>' styleId="${element.itemid}E" onblur="checkDate(this)" size="13" maxlength="10" styleClass="TEXT4" title="输入数字或日期，日期格式：2008.08.08"/>
			       <!-- 没有什么用，仅给用户与视觉效果-->
			                    <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>	
			                    <INPUT type="radio" name="${element.itemid}" onclick="checkDates('${element.itemid}')" id="day"><bean:message key="label.query.day"/>
			                    	
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="itemtype" value="M">
                            <td align="left" class="tdFontcolor" nowrap>                
                               <html:text name="queryInterfaceForm" property='<%="fieldlist["+index+"].value"%>' size="32" maxlength='<%="fieldlist["+index+"].itemlength"%>' styleClass="TEXT4"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                            <td align="left" class="tdFontcolor" nowrap>
                              <logic:notEqual name="element" property="codesetid" value="0">
                                <html:hidden name="queryInterfaceForm" property='<%="fieldlist["+index+"].value"%>' styleClass="text"/>                               
                              <%String delFuntion =  "deleteData(this,'fieldlist["+index+"].value');"; %>
                                <html:text name="queryInterfaceForm" property='<%="fieldlist["+index+"].viewvalue"%>' size="32" maxlength="50" styleClass="TEXT4" onkeydown="<%=delFuntion %>" onchange="fieldcode(this,2);"/>
                                  <logic:notEqual name="element" property="codesetid" value="UN">  
                                    <logic:equal name="element" property="itemid" value="e0122"> 
                                           <img src="/images/code.gif"  plugin="codeselector" codesetid='UM' nmodule='4' ctrltype='3' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle"  />
                                    </logic:equal>  
                                    <logic:equal name="element" property="itemid" value="e01a1"> 
                                           <img src="/images/code.gif"  plugin="codeselector" codesetid="@K" nmodule='4' ctrltype='3' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle"  />
                                    </logic:equal>  
                                    <logic:notEqual name="element" property="itemid" value="e01a1">  
                                    <logic:notEqual name="element" property="itemid" value="e0122"> 
                                        	<img src="/images/code.gif" ctrltype="0" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='false' align="absmiddle"  />
                                     </logic:notEqual>  
                                     </logic:notEqual>                                                                                                
                                  </logic:notEqual>   
                                  <logic:equal name="element" property="codesetid" value="UN">
                                      <logic:equal name="queryInterfaceForm" property="type" value="2"> 
                                         <logic:equal name="element" property="itemid" value="b0110"> 
                                           <img src="/images/code.gif"  plugin="codeselector" codesetid="UM" nmodule='4' ctrltype='3' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle"  />
                                         </logic:equal> 
                                         <logic:notEqual name="element" property="itemid" value="b0110"> 
                                           <img src="/images/code.gif"  plugin="codeselector" codesetid="UM" nmodule='4' ctrltype='3' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true'align="absmiddle"  />
                                         </logic:notEqual>                                              
                                      </logic:equal> 
                                      <logic:notEqual name="queryInterfaceForm" property="type" value="2">
                                         <logic:equal name="element" property="itemid" value="b0110"> 
                                           <img src="/images/code.gif"  plugin="codeselector" codesetid="UN" nmodule='4' ctrltype='3' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle"  />
                                         </logic:equal> 
                                         <logic:notEqual name="element" property="itemid" value="b0110">                                         
                                        	 <img src="/images/code.gif"  plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle"  />
                                         </logic:notEqual>  
                                      </logic:notEqual>                                       
                                  </logic:equal>                                                                                                       
                              </logic:notEqual> 
                              <logic:equal name="element" property="codesetid" value="0">
                                <html:text name="queryInterfaceForm" property='<%="fieldlist["+index+"].value"%>' size="32" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">
                            <td align="left" class="tdFontcolor" nowrap >                
                               <html:text name="queryInterfaceForm" property='<%="fieldlist["+index+"].value"%>' size="32" maxlength="${element.itemlength}" styleClass="TEXT4"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>                            
                       </logic:iterate>
                       <tr>
                           <td>
                           
                           </td>
                            <td align="left" class="tdFontcolor" nowrap> 
               			<html:checkbox name="queryInterfaceForm" property="like" value="1" onclick="selectCheckBox(this);"><bean:message key="label.query.like"/></html:checkbox>
               			<logic:equal name="userView" property="status" value="0">	       
	         			 <html:checkbox name="queryInterfaceForm" property="result" value="1"><bean:message key="label.query.second"/></html:checkbox>            
               			</logic:equal>                              
                            </td>                  
                       </tr>      
                        <tr id="info_cue1" style='display:none;' class="query_cue1">
                           <td>
                           
                           </td>
                            <td align="left" class="tdFontcolor"> 
                                <div>
    	                                <bean:message key="infor.menu.query.cue2"/>
    	                        </div>
                            </td>
                       </tr>             
	                 </table>	 
                   </td>
                 </tr>
               </table>
                          	
            </td>
          </tr>            
      
          <tr class="list3" height="35px;">
            <td colspan="4" align="center">
       	             <logic:equal name="queryInterfaceForm" property="type" value="2">
	         	<html:radio name="queryInterfaceForm" property="qobj" value="1"><bean:message key="label.query.dept"/></html:radio>            
	         	<html:radio name="queryInterfaceForm" property="qobj" value="2"><bean:message key="label.query.org"/></html:radio>            
	         	<html:radio name="queryInterfaceForm" property="qobj" value="0"><bean:message key="label.query.all"/></html:radio>            
               	     </logic:equal>  	                   
               <html:button styleClass="mybutton" styleId="searchButton" property="b_mquery" onclick="search()">
                    <bean:message key="button.query"/>
	       </html:button>
               <html:reset styleClass="mybutton" styleId="resetButton" property="bc_clear" >
                    <bean:message key="button.clear"/>
	       </html:reset> 
	       
	       <logic:notEqual name="queryInterfaceForm" property="type" value="2">
	       
	       </logic:notEqual>
       	     <logic:equal name="queryInterfaceForm" property="home" value="0">
       	     	<input type="hidden" name=ver value=5 />	       
               <hrms:submit styleClass="mybutton" styleId="gqueryButton" property="b_gquery">
                    <bean:message key="button.g.query"/>
	       	   </hrms:submit>  
	           <html:button styleClass="mybutton" styleId="bcButton" property="bc_btn1" onclick="window.location.replace('/workbench/query/hquery_interface.do?a_query=1&b_query=link&a_inforkind=${queryInterfaceForm.type}');"><bean:message key="button.h.query"/></html:button>
	           <html:button styleClass="mybutton" property="bcButtons" onclick="window.location.replace('/workbench/query/hquery_interface.do?a_query=2&b_query=link&a_inforkind=${queryInterfaceForm.type}');"><bean:message key="button.c.query"/></html:button>
              </logic:equal>  	  
              <hrms:tipwizardbutton flag="emp" target="il_body" formname="queryInterfaceForm"/>          
            </td>
          </tr>  
  </table>
</html:form>
<div id="wait" style="display:none;position: absolute; left:0; top:0;">
   <font color="red">输入格式：2008.08.08</font>
</div> 