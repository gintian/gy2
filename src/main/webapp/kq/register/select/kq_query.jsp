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
<script language="javascript">
   
 
      
</script>


<html:form action="/kq/register/select/kqquery">
<script language="javascript">
  
   function change()
   {
      selectKqFieldForm.action="/kq/register/search_registerdata.do?b_search=link&code=${selectKqFieldForm.code}&kind=${selectKqFieldForm.kind}";
      selectKqFieldForm.submit();
   }  
   function selectKq()
   {
       var target_url;
       var winFeatures = "dialogHeight:600px; dialogLeft:450px;"; 
       target_url="/kq/register/select/selectfiled.do?b_init=link";
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=596,height=354'); 
   }  
   
   function showCollect()
   {
      selectKqFieldForm.action="/kq/register/select_collect.do?b_search=link&action=select_collectdata.do&target=mil_body&flag=noself&kind=2";
      selectKqFieldForm.target="il_body";
      selectKqFieldForm.submit();
   }
   function ambiquity()
   {
       selectKqFieldForm.action="/kq/register/ambiquity/search_ambiquity.do?b_search=link&action=search_ambiquitydata.do&target=mil_body&flag=noself&kind=2";
       selectKqFieldForm.target="il_body";
       selectKqFieldForm.submit();
   } 	
   
    function daily_collect()
   {
        selectKqFieldForm.action="/kq/register/collect_orgdailydata.do?b_orgdaily=link&action=collect_orgdailydata.do&target=mil_body&a_inforkind=1";
        selectKqFieldForm.target="il_body";        
        selectKqFieldForm.submit();
   } 
    function sum_collect()
   {
        selectKqFieldForm.action="/kq/register/collect_orgsumdata.do?b_orgsum=link&action=collect_orgsumdata.do&target=mil_body&a_inforkind=1";
        selectKqFieldForm.target="il_body";
        selectKqFieldForm.submit();
   }   
     function go_search()
   {
      selectKqFieldForm.action="/kq/register/daily_register.do?b_search=link&action=daily_registerdata.do";
      selectKqFieldForm.target="il_body"
      selectKqFieldForm.submit();
   } 
   function approve()
   {
      selectKqFieldForm.action="/kq/register/audit_register.do?b_search=link&action=audit_registerdata.do&target=mil_body&flag=noself";
      selectKqFieldForm.target="il_body"
      selectKqFieldForm.submit();
   }
   function kqreport()
   {
   	delChecked();
      selectKqFieldForm.action="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&report_id=1&self_flag=select";
      selectKqFieldForm.target="il_body";
      selectKqFieldForm.submit();
   }
   // 去掉勾选
	function delChecked() {
   		var input = document.getElementsByTagName("input");
   		for (i = 0; i < input.length; i++) {
   			if (input[i].type == "checkbox" && input[i].checked == true) {
   				input[i].checked = false;
   			}
   		}
   	}
</script><hrms:themes /> <!-- 7.0css -->
<table>
<tr>
  <td>
   <table border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>
    <td>
     <hrms:menubar menu="menu1" id="menubar1" target="mil_body">          
     <hrms:menuitem name="aiw" label="显示" >
     <hrms:menuitem name="mitem1" label="日明细" icon="/images/add_del.gif" url="javascript:go_search();" function_id="2702032,0C3113"/>    
     <hrms:menuitem name="mitem2" label="月汇总" icon="/images/quick_query.gif" url="javascript:showCollect();" function_id="2702030,0C3110"/>  
     <hrms:menuitem name="mitem3" label="不定期" icon="/images/add_del.gif" url="javascript:ambiquity();" function_id="2702031"/> 
     <hrms:menuitem name="mitem4" label="查询" icon="/images/view.gif" url="javascript:selectKq();" function_id="2702012,0C3114"/>   
     <hrms:menuitem name="mitem5" label="考勤表" icon="/images/quick_query.gif" url="javascript:kqreport();" function_id="2702013,0C3102"/>
     </hrms:menuitem>          
     </hrms:menubar>
     <html:hidden name="selectKqFieldForm" property="wherestr_s" styleClass="text"/>
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
          
            <logic:iterate id="element" name="selectKqFieldForm"  property="fielditemlist" indexId="index"> 
          
              <logic:equal name="element" property="visible" value="true">
                <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc"/>&nbsp; 
                </td>
              </logic:equal>
           </logic:iterate>                   	        
         </tr>
      </thead>      
      <hrms:paginationdb id="element" name="selectKqFieldForm" sql_str="selectKqFieldForm.sqlstr_s" table="" where_str="selectKqFieldForm.wherestr_s" columns="selectKqFieldForm.columnstr_s" order_by="selectKqFieldForm.ordeby_s" pagerows="19" page_id="pagination">
        
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
            <logic:iterate id="info" name="selectKqFieldForm"  property="fielditemlist" indexId="index"> 
            
                
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
	     <p align="left"><hrms:paginationdblink name="selectKqFieldForm" property="pagination" nameId="selectKqFieldForm" scope="page">
             </hrms:paginationdblink>
	  </td>
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
