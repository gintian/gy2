<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes /> <!-- 7.0css -->
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
<script language="javascript">
   
 
      
</script>


<html:form action="/kq/app_check_in/select/appquery">
<script language="javascript">
  
   function change()
   {
      selectAppFieldForm.action="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=${selectAppFieldForm.table}";
      selectAppFieldForm.target="il_body";
      selectAppFieldForm.submit();
   }  
   function selectKq()
   {
       var target_url;
       var winFeatures = "dialogHeight:600px; dialogLeft:450px;"; 
        target_url="/kq/app_check_in/select/selectfiled.do?b_init=link&table=${appForm.table}";
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=596,height=354'); 
   }     
</script>
<table>
<tr>
  <td>
   <table border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>
    <td>
     <hrms:menubar menu="menu1" id="menubar1" target="mil_body">          
     <hrms:menuitem name="aiw" label="浏览查询" >    
     <hrms:menuitem name="mitem3" label="申请查询" icon="/images/view.gif" url="javascript:selectKq();" function_id=""/>   
     <hrms:menuitem name="mitem4" label="返回" icon="/images/add_del.gif" url="javascript:change();" function_id=""/>    
     </hrms:menuitem>          
     </hrms:menubar>
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
          
            <logic:iterate id="element" name="selectAppFieldForm"  property="fielditemlist" indexId="index"> 
          
              <logic:equal name="element" property="visible" value="true">
                <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc"/>&nbsp; 
                </td>
              </logic:equal>
           </logic:iterate>     
           <hrms:priv func_id="270104,270114,270124,0C3414,0C3424,0C3434" module_id="">
             <td align="center" class="TableRow" nowrap>
	       <bean:message key="kq.strut.more"/>            	
             </td>
           </hrms:priv>              	        
         </tr>
      </thead>        
      <hrms:paginationdb id="element" name="selectAppFieldForm" sql_str="selectAppFieldForm.sqlstr_s" table="" where_str="selectAppFieldForm.wherestr_s" columns="selectAppFieldForm.columnstr_s" order_by="selectAppFieldForm.ordeby_s" pagerows="19" page_id="pagination">
        
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
            <logic:iterate id="info" name="selectAppFieldForm"  property="fielditemlist" indexId="index"> 
            
                
                <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                       <logic:notEqual name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap>
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                             <bean:write name="codeitem" property="codename" />&nbsp;  
                              
                          </td>  
                       </logic:notEqual>
                        <logic:equal name="info" property="codesetid" value="0">
                            <td align="left" class="RecordRow" nowrap> &nbsp;
                               <bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                            </td>                          
                       </logic:equal>                                            
                    </logic:equal>
                    <logic:equal name="info" property="itemtype" value="D">
                       <td align="center" class="RecordRow" nowrap>
                           <bean:write name="element" property="${info.itemid}" filter="false"/>&nbsp;   
                       </td>
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
               <!---->               
            </logic:iterate> 
            <logic:equal name="selectAppFieldForm" property="table" value="Q11">   
             <hrms:priv func_id="270104,0C3414"> 
               <td align="center" class="RecordRow" nowrap>          
                <a href="/kq/app_check_in/view_app.do?b_query=link&bill_id=<bean:write name="element" property="q1101" filter="true"/>&dbpre=<bean:write name="element" property="dbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>"><img src="/images/edit.gif" border=0></a>
               </td> 
             </hrms:priv>
            </logic:equal>
            <logic:equal name="selectAppFieldForm" property="table" value="Q15"> 
              <hrms:priv func_id="270114,0C3424"> 
               <td align="center" class="RecordRow" nowrap>
                 <a href="/kq/app_check_in/view_app.do?b_query=link&bill_id=<bean:write name="element" property="q1501" filter="true"/>&dbpre=<bean:write name="element" property="dbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>"><img src="/images/edit.gif" border=0></a>
               </td> 
              </hrms:priv>  
            </logic:equal>
            <logic:equal name="selectAppFieldForm" property="table" value="Q13"> 
              <hrms:priv func_id="270124,0C3434"> 
               <td align="center" class="RecordRow" nowrap>
                 <a href="/kq/app_check_in/view_app.do?b_query=link&bill_id=<bean:write name="element" property="q1301" filter="true"/>&dbpre=<bean:write name="element" property="dbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>"><img src="/images/edit.gif" border=0></a>
               </td> 
              </hrms:priv> 
           </logic:equal>      
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
	     <p align="left"><hrms:paginationdblink name="selectAppFieldForm" property="pagination" nameId="selectAppFieldForm" scope="page">
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
