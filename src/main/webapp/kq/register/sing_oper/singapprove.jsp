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
   function go_back2()
   {
      browseRegisterForm.action="/kq/register/audit_registerdata.do?b_query=link&code=${browseRegisterForm.code}&kind=${browseRegisterForm.kind}";
      browseRegisterForm.submit();
   }
   function go_back3()
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
      browseRegisterForm.action="/kq/register/sing_oper/singapprovedata.do?b_approve=link";
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
<script language="javascript">
  function getApp()
   {  	  
     var request=new Request({method:'post',onSuccess:showApp,functionId:'15301110034'});
   }
   function showApp(outparamters)
   {
     var tes=outparamters.getValue("notapptag");
     
     if(tes=="seal")
     {
        browseRegisterForm.action="/kq/register/browse_registerdata.do?b_useal=link";
        browseRegisterForm.submit();
     }else if(tes=="noseal")
     {
        var q03=outparamters.getValue("notapp_list");
        var q07=outparamters.getValue("notQ07_list");
        var q09=outparamters.getValue("notQ09_list");
        if(q03!=null)
        {
           var target_url;
           var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
           target_url="/kq/register/notapp.do?b_search=link";
           newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=140,left=180,width=596,height=354'); 
        }
        if(q07!=null)
        {
           var target_url;
           var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
           target_url="/kq/register/notq07.do?b_search=link";
           newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
        }
        if(q09!=null)
        {
           var target_url;
           var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
           target_url="/kq/register/notq09.do?b_search=link";
           newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=200,left=250,width=596,height=354'); 
        }
     }
   }
</script><hrms:themes /> <!-- 7.0css -->
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
              <hrms:menuitem name="recc" label="考勤簿" >              
              <hrms:menuitem name="mitem2" label="返回明细数据" icon="/images/add_del.gif" url="javascript:go_search();" function_id="2702033"/>     
              </hrms:menuitem>   
              <hrms:menuitem name="rec" label="个别处理" >              
              <hrms:menuitem name="mitem1" label="数据归档" icon="/images/quick_query.gif" url="javascript:sing_pigeon();" function_id="270234"/>       
              </hrms:menuitem>  
           </hrms:menubar>
           </td></tr></table>
        </td>
      </tr>
    </table>
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
           <logic:iterate id="element"    name="browseRegisterForm"  property="fieldlist" indexId="index"> 
               <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" nowrap>
                  <bean:write  name="element" property="itemdesc"/>&nbsp; 
                 </td>
               </logic:equal>
           </logic:iterate>         	        
         </tr>
      </thead>             
      <hrms:paginationdb id="element" name="browseRegisterForm" sql_str="browseRegisterForm.s_strsql" table="" where_str="" columns="browseRegisterForm.s_columns" order_by="" pagerows="18" page_id="pagination" indexes="indexes">
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
            <logic:iterate id="info" name="browseRegisterForm"  property="fieldlist" indexId="index">  
              
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
                                <bean:write name="element" property="${info.itemid}"/>&nbsp;
                             </logic:notEqual>
                             <logic:equal name="info" property="itemid" value="a0101">                               
                                <a href="/kq/register/browse_single.do?b_browse=link&code=${browseRegisterForm.code}&userbase=<bean:write name="element" property="nbase" filter="true"/>&kind=${browseRegisterForm.kind}&&start_date=${browseRegisterForm.start_date}&end_date=${browseRegisterForm.end_date}&A0100=<bean:write name="element" property="a0100" filter="true"/>">
                                 <bean:write name="element" property="${info.itemid}" filter="true"/></a>&nbsp;
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
	       <logic:equal name="browseRegisterForm" property="flag" value="2">
	          <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="go_back2();" class="mybutton">						      
               </logic:equal>
                <logic:equal name="browseRegisterForm" property="flag" value="3">
	          <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="go_back3();" class="mybutton">						      
               </logic:equal>
               
           </td>
	   <td width="40%"></td>
        </tr>
     </table>
   </td>
 </tr>
</table>
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
