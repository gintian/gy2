<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>

<html:form action="/kq/register/sing_oper/singcollectdata">
<script language="javascript">  
   //验证是否报批成功 
   function showValidate(validate)
   {
     if(validate=="true"){
        alert("考勤数据已上报");
     }else if(validate=="false"){
        alert("考勤数据上报失败");
     }
   } 
   function goback()
   {
      dailyRegisterForm.action="/kq/register/daily_register.do?b_search=link&action=daily_registerdata.do";
      dailyRegisterForm.target="il_body"
      dailyRegisterForm.submit();
   } 
   function refer()
   {
      dailyRegisterForm.action="/kq/register/sing_oper/singcollectdata.do?b_audit=link&flag=2";
      dailyRegisterForm.submit();
   } 
   function audit()
   {
      dailyRegisterForm.action="/kq/register/sing_oper/singcollectdata.do?b_audit=link&flag=8";
      dailyRegisterForm.submit();
   }  
</script><hrms:themes /> <!-- 7.0css -->
<logic:equal name="dailyRegisterForm" property="error_flag" value="0">
<table>
 <tr>
  <td>
   <table width="60%" border="0" cellspacing="1"  align="left" cellpadding="1">
    <tr>
      <td align= "left" nowrap> 
         <table>
         <tr>
         <td>
         <hrms:menubar menu="menu1" id="menubar1" target="mil_body">
           <hrms:menuitem name="file" label="日明细数据">  
           <hrms:menuitem name="mitem1" label="明细数据录入" icon="/images/add_del.gif" url="javascript:goback();" function_id="2702011"/>              
           </hrms:menuitem>
           <hrms:menuitem name="rec" label="报审报批" >
            <hrms:menuitem name="mitem1" label="汇总数据报批" icon="/images/quick_query.gif" url="javascript:refer();" function_id="2702071"/>  
            <hrms:menuitem name="mitem1" label="汇总数据报审" icon="/images/quick_query.gif" url="javascript:audit()" function_id="2702081"/>   
          </hrms:menuitem>   
         </hrms:menubar>
       </td>
       </tr>
      </table>
      </td>      
      <td align= "left" nowrap>
       
        <bean:message key="kq.register.daily.menu"/>
        <bean:write name="dailyRegisterForm" property="kq_duration"/>   
        <html:hidden name="dailyRegisterForm" property="kq_duration"/>     
      </td>
      
    </tr>
    </table>
  </td>
 </tr>
 <tr>
  <td>
 <%int i=0;%>
    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" >
      <thead>
         <tr>
        
            <logic:iterate id="element"    name="dailyRegisterForm"  property="fieldlist"> 
               <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" nowrap>
                  <bean:write  name="element" property="itemdesc"/>&nbsp; 
                 </td>
              </logic:equal>
           </logic:iterate>         	        
           </tr>
      </thead> 
      <hrms:paginationdb id="element" name="dailyRegisterForm" sql_str="dailyRegisterForm.s_strsql" table="" where_str="" columns="dailyRegisterForm.s_columns" order_by="" pagerows="18" page_id="pagination">
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow">
          <%
          }else{
          %>
          <tr class="trDeep">
          <%}i++; 
          
          %>           
            <logic:iterate id="info" name="dailyRegisterForm"  property="fieldlist">  
                <logic:equal name="info" property="visible" value="false">
                  <html:hidden name="element" property="${info.itemid}"/>  
                </logic:equal>
                <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                       <logic:notEqual name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap>
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                          </td>  
                         </logic:notEqual>
                         <logic:equal name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap>
                               &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                           </td> 
                         </logic:equal>
                      </logic:equal>
                   <!--数字-->
                      <logic:equal name="info" property="itemtype" value="N">
                      <td align="center" class="RecordRow" nowrap> 
                        <logic:notEqual name="element" property="${info.itemid}" value="0">
                           <bean:write name="element" property="${info.itemid}"/>
                        </logic:notEqual> 
                        
                      </td>
                  </logic:equal>
                 </logic:equal> 
               <!---->               
            </logic:iterate>   
          </tr>
        </hrms:paginationdb>    	                           	    		        	        	        
      </table>
     </td>
   </tr> 
   <tr>
   <td>
     <table  width="70%" align="center">
       <tr>
          <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
             第<bean:write name="pagination" property="current" filter="true" />页
             共<bean:write name="pagination" property="count" filter="true" />条
             共<bean:write name="pagination" property="pages" filter="true" />页
	  </td>
	  <td  width="80%" align="left" nowrap class="tdFontcolor">
	     <p align="left"><hrms:paginationdblink name="dailyRegisterForm" property="pagination" nameId="dailyRegisterForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>
	<tr>
	   <td width="60%" align="center"  nowrap>	       	                 
               <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">						      
           </td>
	   <td width="40%"></td>
        </tr>
     </table>
   </td>
 </tr>
</table>
</logic:equal>
<logic:notEqual name="dailyRegisterForm" property="error_flag" value="0">
<script language="javascript">
var error_str=kqErrorProcess('<bean:write name="dailyRegisterForm"  property="error_flag"/>','<bean:write name="dailyRegisterForm"  property="error_message"/>','<bean:write name="dailyRegisterForm"  property="error_return"/>');
document.write(error_str);
</script>
</logic:notEqual>
</html:form>
<script language="javascript">   
   showValidate('<bean:write name="dailyRegisterForm"  property="validate"/>');
</script>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
  
</script>