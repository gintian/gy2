<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.BrowseHistoryForm" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
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
<script type="text/javascript" src="/kq/kq.js"></script>
<style>
<!--
.divStyle{
	border:1px solid #C4D8EE;overflow: auto;left:5;
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-11);
}
.divStyle1{
	overflow: auto;left:5;
	border:1px solid;
	border-top:none;
	width:expression(document.body.clientWidth-11);
}
-->
</style>
<hrms:themes /> <!-- 7.0css -->
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
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
%>
<html:form action="/kq/register/history/dailybrowsedata">
<script language="javascript">
   function showOverrule(userbase,a0100,kq_duration)
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
       target_url="/kq/register/daily_registerdata.do?b_overrlue=link&userbase="+userbase+"&a0100="+a0100+"&kq_duration="+kq_duration;
       newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
	  
   }
  
   function change()
   {
      browseHistoryForm.action="/kq/register/history/dailybrowsedata.do?b_search=link&code=${browseHistoryForm.code}&kind=${browseHistoryForm.kind}";
      browseHistoryForm.submit();
   } 
   function couser()
   {
      browseHistoryForm.action="/kq/register/history/dailybrowsedata.do?b_couser=link&code=${browseHistoryForm.code}&kind=${browseHistoryForm.kind}";
      browseHistoryForm.submit();
   }  
   function selectKq()
   {
       var target_url;
       var winFeatures = "dialogHeight:600px; dialogLeft:450px;"; 
       target_url="/kq/register/select/selectfiled.do?b_init=link";
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
   }
   function ambiquity()
   {
       browseHistoryForm.action="/kq/register/ambiquity/search_ambiquity.do?b_search=link&action=search_ambiquitydata.do&target=mil_body&flag=noself&kind=2";
       browseHistoryForm.target="il_body";
       browseHistoryForm.submit();
   } 
   function go_search()
   {
      browseHistoryForm.action="/kq/register/search_register.do?b_search=link&action=search_registerdata.do&target=mil_body";
      browseHistoryForm.target="il_body";
      browseHistoryForm.submit();
   }
    function daily_historyt()
   {
        browseHistoryForm.action="/kq/register/history/dailybrowse.do?b_search=link&action=dailybrowsedata.do&target=mil_body&a_inforkind=1&viewPost=kq";
        browseHistoryForm.target="il_body";
        browseHistoryForm.submit();
   }
       function daily_historicalt(obj)
   {
   		if(obj.value=="1"){
   			obj.options(0).selected = true;
	        browseHistoryForm.action="/kq/register/historical/dailybrowse.do?b_search=link&action=dailybrowsedata.do&target=mil_body&a_inforkind=1&viewPost=kq";
	        browseHistoryForm.target="il_body";
	        browseHistoryForm.submit();
        }
   } 
    function sum_historyt()
   {
        browseHistoryForm.action="/kq/register/history/sumbrowse.do?b_search=link&action=sumbrowsedata.do&target=mil_body&a_inforkind=1&viewPost=kq";
        browseHistoryForm.target="il_body";
        browseHistoryForm.submit();
   }
   function change_print()
   {
       browseHistoryForm.action="/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=81&relatTableid=${browseHistoryForm.relatTableid}";
       browseHistoryForm.submit();
   }
   function outPrintApp(){
      var returnURL = getEncodeStr("${browseHistoryForm.returnURL}");
      var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${browseHistoryForm.relatTableid}&closeWindow=1";
      	urlstr+="&returnURL="+returnURL;
      //window.open(urlstr);
      window.showModalDialog(urlstr,1, 
        "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
	}
   var view_pnl=false;
   function selectinfo()
   { 
     if(!view_pnl)
     {
       Element.show('datepnl'); 
       view_pnl=true;
     }else
     {
       Element.hide('datepnl');
       view_pnl=false;
     }
           
   }
   function viewAll()
   {
       Element.hide('datepnl');
       browseHistoryForm.action="/kq/register/history/dailybrowsedata.do?b_search=link&select_flag=0";
       browseHistoryForm.submit();
   }
   function selectflag()
   {
      browseHistoryForm.action="/kq/register/history/dailybrowsedata.do?b_search=link&select_flag=1";
      browseHistoryForm.submit();
   } 
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
   function kqreport()
   {
   		delChecked();
   		var year = document.getElementById("year");
   		var duration = document.getElementById("duration");
      //browseHistoryForm.action="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&report_id=1&userbase=${browseHistoryForm.select_pre}&code=${browseHistoryForm.code}&coursedate=${browseHistoryForm.coursedate}&kind=${browseHistoryForm.kind}&self_flag=hist&privtype=kq";
      browseHistoryForm.action="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&report_id=1&userbase=${browseHistoryForm.select_pre}&code=${browseHistoryForm.code}&coursedate="+year.value+"-"+duration.value+"&kind=${browseHistoryForm.kind}&self_flag=hist&privtype=kq&history=1";
      browseHistoryForm.target="il_body";
      browseHistoryForm.submit();
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
	
function changeys(dd)
{
	if(dd==2){
 		browseHistoryForm.action="/kq/register/history/dailybrowsedata.do?b_search=link&selectys=2&code=${browseHistoryForm.code}&kind=${browseHistoryForm.kind}";
    	browseHistoryForm.submit();
 	}else if(dd==1){
 		browseHistoryForm.action="/kq/register/history/dailybrowsedata.do?b_search=link&selectys=1";
    	browseHistoryForm.submit();
 	}
} 
</script>
<logic:equal name="browseHistoryForm" property="error_flag" value="0">
<table> 

 <tr>
  <td>
  
  <% int s=0;%>
   <table id="layoutTable" width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
   <script language="javascript">
	   /*兼容非IE浏览器页面显示样式问题 非IE浏览器 getBrowseVersion()返回值为0 wangb 20171026*/
	  	if(!getBrowseVersion()){
	   		var temp_table = document.getElementById("layoutTable");
	   		temp_table.style.tableLayout='fixed';
	    }
   </script>
    <tr>
       
       <td>
  <table border="0" cellspacing="0"  align="left" cellpadding="0">
  <tr>
    <td>
   <hrms:menubar menu="menu1" id="menubar1" target="mil_body">     
     <hrms:menuitem name="dan" label="数据分类" >
     <hrms:menuitem name="mitem1" label="日明细" icon="/images/quick_query.gif" url="javascript:daily_historyt();" />  
     <hrms:menuitem name="mitem2" label="月汇总" icon="/images/add_del.gif" url="javascript:sum_historyt();" />   
     <hrms:menuitem name="mitem4" label="考勤表" icon="/images/quick_query.gif" url="javascript:kqreport();"/>  
     <hrms:menuitem name="mitem3" label="打印" icon="/images/sort.gif" url="javascript:outPrintApp();" /> 
     </hrms:menuitem>      
 </hrms:menubar>
 </td>
 </tr>
  </table>
       </td> 
      <!-- 
         <td align= "left" nowrap>&nbsp;
           <select onchange="daily_historicalt(this)">
           	<option value="0">封存数据</option>
           	<%
           	//if(userView.getVersion_flag() == 1){
           	%>
           	<option value="1">归档数据</option>
           	<%//}%>
           </select>
       </td> 
      --> 
       <td align= "left" nowrap>&nbsp;
           <html:select name="browseHistoryForm" property="select_pre" styleId="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
            </html:select>
       </td> 
        <td nowrap>&nbsp;
	  <bean:message key="kq.register.daily.menu"/><html:select name="browseHistoryForm" property="year" size="0" onchange="javascript:couser();" styleId="year">
          <html:optionsCollection property="yearlist" value="dataValue" label="dataName"/>
          </html:select>&nbsp;年&nbsp; 
        </td>
       <td>
	  <html:select name="browseHistoryForm" property="duration" size="0" onchange="javascript:couser();" styleId="duration">
          <html:optionsCollection property="durationlist" value="dataValue" label="dataName"/>
          </html:select>&nbsp;月&nbsp; 
        </td>
      <td align= "left" nowrap>
       
        ${browseHistoryForm.workcalendar} 
        <html:hidden name="browseHistoryForm" property="code" styleClass="text"/>   
        <input type="hidden" name="returnURL" value="/kq/register/history/dailybrowsedata.do?b_search=link" class="text">
      </td>
        <td align= "left" nowrap> &nbsp;
           按<html:select name="browseHistoryForm" property="select_type"  size="1">
            	<html:option value="0">姓名</html:option>                      
                <html:option value="1">工号</html:option>
                <html:option value="2">考勤卡号</html:option>
           </html:select> 
           <input type="text" name="select_name" value="${browseHistoryForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;height:25px;text-align:left">
           &nbsp;<button extra="button" onclick="javascript:selectflag();">查询</button> 
        
      </td>
    <!-- 
      <td align= "left" nowrap> &nbsp;
      	时间显示方式
      	<logic:notEqual name="browseHistoryForm" property="selectys" value="2">
      		<select size="1"   name="selectysf"   onchange="changeys(this.value);">
      		 	<option   value="1">默认</option>   
      		 	<option   value="2">HH:mm</option> 
      		</select>
      		</logic:notEqual>
      		<logic:equal name="browseHistoryForm" property="selectys" value="2">
      		<select size="1"   name="selectysf"   onchange="changeys(this.value);">
      		 	<option   value="2">HH:mm</option> 
      		 	<option   value="1">默认</option>   
      		</select>
      		</logic:equal>    
      </td> 
    -->        
    </tr>
    </table>
  </td>
 </tr>
 <tr>
  <td>
 <%int i=0;
   String name=null;
   int num_s=0;
 %>
  <div class="divStyle common_border_color" style="margin-top:3px;"  >
    <table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTableF" style="border:none;" >
      <thead>
         <tr>
          
            <logic:iterate id="element"    name="browseHistoryForm"  property="fielditemlist" indexId="index"> 
          
              <logic:equal name="element" property="visible" value="true">
                <td align="center" class="TableRow" style="border-left:none;border-top: none;" nowrap>
                 <bean:write  name="element" property="itemdesc"/>&nbsp; 
                </td>
              </logic:equal>
           </logic:iterate>
              <td align="center" class="TableRow" style="border-right:none;border-top: none;" nowrap>
                 审批意见&nbsp; 
              </td>      	        
         </tr>
      </thead>      
      <hrms:paginationdb id="element" name="browseHistoryForm" sql_str="browseHistoryForm.sqlstr" table="" where_str="browseHistoryForm.strwhere" columns="browseHistoryForm.columns" order_by="browseHistoryForm.orderby" pagerows="${browseHistoryForm.pagerows}" page_id="pagination">
          <bean:define id="nbase1" name="element" property="nbase"/>
          <bean:define id="a01001" name="element" property="a0100"/>
        
          <%
          String nbase2=PubFunc.encrypt(nbase1.toString());
          String a01002=PubFunc.encrypt(a01001.toString());
          
          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
          if(i%2==0){ 
          %>
          <tr class="trShallow">
          <%
          }else{
          %>
          <tr class="trDeep">
          <%}i++; 
            
          %>
          <% int  inNum=0;%>            
            <logic:iterate id="info" name="browseHistoryForm"  property="fielditemlist" indexId="index"> 
            	 <%
               		BrowseHistoryForm browseHistoryForm=(BrowseHistoryForm)session.getAttribute("browseHistoryForm");
               		FieldItem item=(FieldItem)pageContext.getAttribute("info");
               		name=item.getItemid(); 
              	 %> 
            	
                <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                       <logic:notEqual name="info" property="codesetid" value="0">
                       <logic:notEqual name="info" property="itemid" value="e0122">
                          <td align="left" class="RecordRow" style="border-left:none;" nowrap>                      
                            <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                          </td>
                          </logic:notEqual>
                          <logic:equal name="info" property="itemid" value="e0122">
                     		<td align="left" class="RecordRow" style="border-left:none;" nowrap>                      
                            <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${browseHistoryForm.uplevel}"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                            </td>
                     	  </logic:equal>     
                       </logic:notEqual>
                       <logic:equal name="info" property="codesetid" value="0">
                          <logic:notEqual name="info" property="itemid" value="a0101">
                             <td align="left" class="RecordRow" style="border-left:none;" nowrap>
                               &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                               
                             </td> 
                          </logic:notEqual>
                          <logic:equal name="info" property="itemid" value="a0101">
                              <td align="left" class="RecordRow" style="border-left:none;" nowrap> &nbsp;
                               <a href="/kq/register/history/showsingle.do?b_sing=link&code=${browseHistoryForm.code}&kind=${browseHistoryForm.kind}&userbase=<%=nbase2 %>&start_date=${browseHistoryForm.start_date}&end_date=${browseHistoryForm.end_date}&A0100=<%=a01002 %>">
                               <bean:write name="element" property="${info.itemid}" filter="true"/></a>&nbsp;                               
                             </td>
                          </logic:equal>  
                       </logic:equal>
                                            
                   </logic:equal>
                   <!--数字-->
                   <logic:equal name="info" property="itemtype" value="N">
                      <td align="center" class="RecordRow" style="border-left:none;" nowrap>
                      	<logic:notEqual name="browseHistoryForm" property="selectys" value="2"> 
                         <logic:greaterThan name="element" property="${info.itemid}" value="0">
                            &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                         </logic:greaterThan>
                         </logic:notEqual>
                         <logic:notEqual name="browseHistoryForm" property="selectys" value="1">
                      		<%
                         num_s++;
                         request.setAttribute("num_s",num_s+""); 
                         HashMap infoMap=(HashMap)browseHistoryForm.getKqItem_hash();
                         //out.println("d = "+abean.get(name));
                       %>
                       <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${info.itemid}" value='<%=abean.get(name)+""%>'/>
                       <%
                                inNum++;
                        %>  
                      </logic:notEqual>                            
                      </td>
                  </logic:equal>
                  <logic:notEqual name="info" property="itemtype" value="N">
                  <logic:notEqual name="info" property="itemtype" value="A">
                  	<td align="left" class="RecordRow" nowrap> &nbsp;
                                              
                     </td>
                     </logic:notEqual>
                  </logic:notEqual>
                </logic:equal>   
               <!---->               
            </logic:iterate>
            <td class="RecordRow" nowrap style="border-right:none;" align="center">
            <img src="/images/view.gif" border="0" alt="点击察看意见" onclick="showOverrule('<%=nbase2 %>','<%=a01002 %>','${browseHistoryForm.kq_duration}');">
            </td>  
          </tr>
          <%
          s++;
          %>
        </hrms:paginationdb> 
                                 	    		        	        	        
      </table>
       </div>
     </td>
   </tr> 
   <tr>
   <td>
   <div class="divStyle1 common_border_color">
     <table  width="100%" border="0" align="left">
       <tr>        
          <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
             <hrms:paginationtag name="browseHistoryForm"
								pagerows="${browseHistoryForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
	  </td>
	  <td  width="60%" align="right" nowrap class="tdFontcolor">
	     <hrms:paginationdblink name="browseHistoryForm" property="pagination" nameId="browseHistoryForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>	
	<!-- 
	<tr>
	   <td width="60%" align="center"  nowrap>
	       <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">						      
           </td>
	   <td width="40%"></td>
        </tr>
        -->
     </table>
    </div>
 </td>
 </tr>
 
</table>
</logic:equal>

<logic:notEqual name="browseHistoryForm" property="error_flag" value="0">
<script language="javascript">
var error_str;
<%if(bosflag.equals("hl4")){%>
	 error_str=kqErrorProcess('<bean:write name="browseHistoryForm"  property="error_flag"/>','<bean:write name="browseHistoryForm"  property="error_message"/>','<bean:write name="browseHistoryForm"  property="error_return"/>');
	 document.write(error_str);
<%}else if(bosflag.equals("hl")){%>
 	error_str=kqErrorProcess('3','<bean:write name="browseHistoryForm"  property="error_message"/>','/general/tipwizard/tipwizard.do?br_workrest=link');
	document.write(error_str);
<%}%>
</script>
</logic:notEqual>

</html:form>
<iframe name="mysearchframe" style="display: none;"></iframe>
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
   var view_select_flag="${browseHistoryForm.select_flag}";	
 if(view_select_flag=="1")
   Element.show('datepnl');
 else
   Element.hide('datepnl'); 
 //hide_nbase_select('select_pre');
</script>
