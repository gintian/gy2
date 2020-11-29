<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
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
<html:form action="/kq/register/browse_registerdata">
 <SCRIPT LANGUAGE="JavaScript">
  var checkflag = "false";
  function selAll()
   {
      var len=document.browseRegisterForm.elements.length;
       var i;
    if(checkflag == "false")
    {
        for (i=0;i<len;i++)
        {
         if (document.browseRegisterForm.elements[i].type=="checkbox")
          {
             
            document.browseRegisterForm.elements[i].checked=true;
          }
        }
        checkflag = "true";
    }else
    {
        for (i=0;i<len;i++)
        {
          if (document.browseRegisterForm.elements[i].type=="checkbox")
          {
             
            document.browseRegisterForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    }      
  } 
</script>
<script language="javascript">
   function change()
   {
      browseRegisterForm.action="/kq/register/browse_registerdata.do?b_query=link&code=${browseRegisterForm.code}&kind=${browseRegisterForm.kind}";
      browseRegisterForm.submit();
   }
   function change_print()
   {
       browseRegisterForm.action="/kq/register/select_kqreportdata.do?b_select=link&code=${browseRegisterForm.code}&kind=${browseRegisterForm.kind}&userbase=${browseRegisterForm.userbase}&coursedate=${browseRegisterForm.kq_duration}";
       browseRegisterForm.submit();
   }
   function return_overrule()
   {
       browseRegisterForm.action="/kq/register/browse_registerdata.do?b_saveover=link";
       browseRegisterForm.submit();
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
       target_url="/kq/register/browse_registerdata.do?br_saveover=link";
       newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
   }
   function writeOverrule(userbase,a0100,kq_duration)
   {
       
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
       target_url="/kq/register/browse_registerdata.do?b_searrule=link&userbase="+userbase+"&a0100="+a0100+"&kq_duration="+kq_duration;
       newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
	  
   } 
   function selectKq()
   {
       var target_url;
       var winFeatures = "dialogHeight:600px; dialogLeft:450px;"; 
       target_url="/kq/register/select/selectfiled.do?b_init=link";
       newwindow=window.open(target_url,'mil_body','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
   }     
   function approve_data()
   {
      browseRegisterForm.action="/kq/register/browse_registerdata.do?b_approve=link";
      browseRegisterForm.submit();
   }
   function go_search()
   {
      browseRegisterForm.action="/kq/register/search_register.do?b_search=link&action=search_registerdata.do&target=mil_body";
      browseRegisterForm.target="il_body";
      browseRegisterForm.submit();
   }
    function sing_approve()
   {
      browseRegisterForm.action="/kq/register/sing_oper/singapprovedata.do?b_approve=link&flag=3";
      browseRegisterForm.submit();
   }	
    function sing_pigeon()
   {
        browseRegisterForm.action="/kq/register/pigeonhole/sing_pigeonhole.do?b_search=link&flag=3";
        browseRegisterForm.submit();
        //var target_url;
        //var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
        //target_url="/kq/register/pigeonhole/sing_pigeonhole.do?b_search=link";
        //newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=596,height=354'); 
   
   }   
</script>
<hrms:themes /> <!-- 7.0css -->
<logic:equal name="browseRegisterForm" property="error_flag" value="0">
<table>
 <tr>
  <td>
   <table width="60%" border="0" cellspacing="1"  align="left" cellpadding="1">
    
    <tr> 
     <td>
        <table border="0" cellspacing="0"  align="left" cellpadding="0">
            <tr>            
           <td align="left">
           <table border="0" cellspacing="0"  align="left" cellpadding="0"><tr><td>
            <hrms:menubar menu="menu2" id="menubar1" target="mil_body">  
              <hrms:menuitem name="rec" label="批量审批" >
              <hrms:menuitem name="mitem1" label="批量审批" icon="/images/write.gif" url="javascript:approve_data();" function_id="270231"/>      
              </hrms:menuitem> 
              <hrms:menuitem name="rec3" label="个别审批" >
              <hrms:menuitem name="mitem1" label="个别审批" icon="/images/quick_query.gif" url="javascript:sing_approve();" function_id="270231"/>       
              <hrms:menuitem name="mitem2" label="个别驳回" icon="/images/del.gif" url="javascript:return_overrule();" command="" function_id="270232"/>
              <hrms:menuitem name="mitem1" label="个别归档" icon="/images/sort.gif" url="javascript:sing_pigeon();" function_id="270234"/>       
              </hrms:menuitem>                 
           </hrms:menubar>
           </td></tr></table>
        </td>
      </tr>
    </table>
       </td>     
    <td align= "left" nowrap>
        <bean:message key="kq.register.daily.menu"/>
        <html:select name="browseRegisterForm" property="coursedate" size="1" onchange="javascript:change()">
        <html:optionsCollection property="courselist" value="dataValue" label="dataName"/>
        </html:select> 
        <html:hidden name="browseRegisterForm" property="code" styleClass="text"/>
        <html:hidden name="browseRegisterForm" property="kind" styleClass="text"/>                    
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
         <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
         </td>  
           <logic:iterate id="element"    name="browseRegisterForm"  property="fielditemlist" indexId="index"> 
               <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" nowrap>
                  <bean:write  name="element" property="itemdesc"/>&nbsp; 
                 </td>
               </logic:equal>
           </logic:iterate>         	        
         </tr>
      </thead>              
      <hrms:paginationdb id="element" name="browseRegisterForm" sql_str="browseRegisterForm.sqlstr" table="" where_str="browseRegisterForm.strwhere" columns="browseRegisterForm.columns" order_by="browseRegisterForm.orderby" pagerows="18" page_id="pagination" indexes="indexes">
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}i++; 
           
          %> 
            <td align="center" class="RecordRow" nowrap>
              <logic:notEqual name="element" property="q03z5" value="07">
                <hrms:checkmultibox name="browseRegisterForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
              </logic:notEqual>
            </td> 
            <logic:iterate id="info" name="browseRegisterForm"  property="fielditemlist" indexId="index">  
              
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
                             <logic:notEqual name="info" property="itemid" value="a0101">
                              <logic:notEqual name="info" property="itemid" value="overrule">
                               &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                             </logic:notEqual>
                             </logic:notEqual>
                             <logic:equal name="info" property="itemid" value="a0101">
                               <logic:notEqual name="info" property="itemid" value="overrule">
                                <a href="/kq/register/browse_single.do?b_browse=link&code=${browseRegisterForm.code}&userbase=<bean:write name="element" property="nbase" filter="true"/>&kind=${browseRegisterForm.kind}&&start_date=${browseRegisterForm.start_date}&end_date=${browseRegisterForm.end_date}&A0100=<bean:write name="element" property="a0100" filter="true"/>">
                                <bean:write name="element" property="${info.itemid}" filter="true"/></a>&nbsp;
                               </logic:notEqual>
                             </logic:equal>
                              <logic:equal name="info" property="itemid" value="overrule">
                               <logic:notEqual name="info" property="itemid" value="a0101">
                                &nbsp;
                                 <hrms:priv func_id="270233">  
                                   <img src="/images/edit.gif" border="0" alt="填写审批意见" onclick="writeOverrule('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>','<bean:write name="element" property="q03z0" filter="true"/>');">
                                 </hrms:priv>  
                               </logic:notEqual>
                             </logic:equal>
                          </td> 
                        </logic:equal>
                    </logic:equal>
                   <!--数字-->
                      <logic:equal name="info" property="itemtype" value="N">
                      <td align="center" class="RecordRow" nowrap> 
                         <logic:greaterThan name="element" property="${info.itemid}" value="0">
                            <bean:write name="element" property="${info.itemid}"/>
                         </logic:greaterThan>
                         
                      </td>
                  </logic:equal>
                 </logic:equal>                
            </logic:iterate>  
          </tr>
          
        </hrms:paginationdb>    	                           	    		        	        	        
        	                           	    		        	        	        
      </table>
     </td>
   </tr> 
   <tr>
   <td>
     <table  width="70%" align="left">
       <tr>
          <td align="left">
		&nbsp;&nbsp;<bean:message key="label.query.selectall"/><input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;
          </td>	
          <td width="20%" valign="bottom" align="center" class="tdFontcolor" nowrap>
             第<bean:write name="pagination" property="current" filter="true" />页
             共<bean:write name="pagination" property="count" filter="true" />条
             共<bean:write name="pagination" property="pages" filter="true" />页
	  </td>
	  <td  width="80%" align="left" nowrap class="tdFontcolor">
	     <p align="left"><hrms:paginationdblink name="browseRegisterForm" property="pagination" nameId="browseRegisterForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>
	<tr>
	   <td width="60%" align="center"  nowrap>
	       
	     
           </td>
	   <td width="40%"></td>
        </tr>
     </table>
   </td>
 </tr>
</table>
</logic:equal>
<logic:notEqual name="browseRegisterForm" property="error_flag" value="0">
<script language="javascript">
var error_str=kqErrorProcess('<bean:write name="browseRegisterForm"  property="error_flag"/>','<bean:write name="browseRegisterForm"  property="error_message"/>','<bean:write name="browseRegisterForm"  property="error_return"/>');
document.write(error_str);
</script>
</logic:notEqual>
</html:form>
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
