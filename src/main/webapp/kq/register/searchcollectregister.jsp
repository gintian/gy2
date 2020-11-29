<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%
DailyRegisterForm daily=(DailyRegisterForm)session.getAttribute("dailyRegisterForm");
String lockedNumStr=daily.getLockedNum();
int lockedNum=Integer.parseInt(lockedNumStr);
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/kq/kq_tableLocked.css" rel="stylesheet" type="text/css">  
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
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->

<html:form action="/kq/register/search_collectdata">
<script language="javascript">
   function change()
   {
      dailyRegisterForm.action="/kq/register/search_collectdata.do?b_search=link";
      dailyRegisterForm.submit();
   }  
    var newwindow=null;
    	function showOverrule(userbase,a0100,kq_duration)
    	{
    	   var target_url;
    	   
    	   
    	   var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
    	   target_url="/kq/register/daily_registerdata.do?b_overrlue=link&userbase="+userbase+"&a0100="+a0100+"&kq_duration="+kq_duration;
    	   
    	    newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
	  
    	}
    function go_daily()
   {
      dailyRegisterForm.action="/kq/register/search_register.do?b_search=link&action=search_registerdata.do&target=mil_body&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&viewPost=kq&select_name=";
      dailyRegisterForm.target="il_body";
      dailyRegisterForm.submit();     
   } 
     function change_print()
   {
       dailyRegisterForm.action="/kq/register/select_kqreportdata.do?b_select=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&userbase=${dailyRegisterForm.userbase}&coursedate=${dailyRegisterForm.kq_duration}&relatTableid=5";
       dailyRegisterForm.submit();
   }
   function getApp()
   {  	if(confirm("您确定要对当前考勤期间进行月末处理吗？"))
       {  
         var request=new Request({method:'post',onSuccess:showApp,functionId:'15301110034'});
       }
   }
   function showApp(outparamters)
   {
     var tes=outparamters.getValue("notapptag");
     var pigeonhole_type=outparamters.getValue("pigeonhole_type");
     if(tes=="seal")
     {
         startSeal(pigeonhole_type);
     }else if(tes=="noseal")
     {
        var q03=outparamters.getValue("notapp_list");
        var q07=outparamters.getValue("notQ07_list");
        var q09=outparamters.getValue("notQ09_list");       
        if(q03=="have")
        {
           alert('请先将所有用户的考勤信息进行月汇总审批！');
           return false;
        }
        var isseal=false;
        if(q07=="have")
        {
           if(confirm("部门考勤信息是否需要进行日汇总！"))
           {
              return false;
           }else
           {
              isseal=true;
           }
        }
        if(q09=="have")
        {
           if(confirm("部门考勤信息是否需要进行月汇总！"))
           {
              return false;
           }else
           {
              isseal=true;              
           }
        }
        if(isseal)
        {
          startSeal(pigeonhole_type);
         }
     }     
   }   
   function startSeal(pigeonhole_type)
   {
       getPigeonhole();
   }
   function getSeal()
   {
      if(confirm("确定要封存本考勤期间吗？"))
      {
           dailyRegisterForm.action="/kq/register/browse_registerdata.do?b_useal=link";
           dailyRegisterForm.target="il_body";
           dailyRegisterForm.submit();
      }  
   }
   function getPigeonhole()
   {
           var target_url;
           var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
           target_url="/kq/register/pigeonhole.do?b_search=link";
           newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=500,height=500'); 
   }
   function read_Pigeonflag(flag)
   {
      if(flag=="true")
      {
         alert("归档成功");  
         getSeal();      
      }else if(flag=="false")
      {
         alert("归档失败，请重试！");
      }
   }
   function showPigeFlag(outparamters)
   {
     var flag=outparamters.getValue("pigeonhole_flag");
     
      var request=new Request({method:'post',asynchronous:false,onSuccess:showPigeFlag,functionId:'15302110004'});
   }
    function show_state(s)
   {
     var state = s.options[s.selectedIndex].value;     
     if(state==0)
     {
       go_daily();
     }else if(state==1)
     {
        
     }
  }
   function gocollect()
   {
        dailyRegisterForm.action="/kq/register/daily_registerdata.do?br_collect=link";
        dailyRegisterForm.target="il_body";
        dailyRegisterForm.submit();
       
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
       dailyRegisterForm.action="/kq/register/search_collectdata.do?b_search=link&select_flag=0";
       dailyRegisterForm.submit();
   }
   function selectflag()
   {
      dailyRegisterForm.action="/kq/register/search_collectdata.do?b_search=link&select_flag=1";
      dailyRegisterForm.submit();
   }
</script>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
 <tr>
  <td>
   <table width="100%"  border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>   
        <td>
  <table border="0" cellspacing="0"  align="left" cellpadding="0">
  <tr>
    <td>
   <hrms:menubar menu="menu1" id="menubar1" target="mil_body">
        <hrms:menuitem name="file" label="业务处理"> 
        <hrms:menuitem name="mitem2" label="打印数据" icon="/images/sort.gif" url="javascript:change_print();" function_id="2702012"/>       
        <hrms:menuitem name="mitem3" label="数据归档" icon="/images/add_del.gif" url="javascript:getApp();" function_id="2702025"/>  
        <hrms:menuitem name="mitem3" label="封存期间" icon="/images/add_del.gif" url="javascript:getSeal();" function_id="2702027"/>  
        </hrms:menuitem> 
       </hrms:menubar>
 </td>
 </tr>
  </table>
       </td> 
        <td align= "left" nowrap>&nbsp; &nbsp; 
        <select name="showstate" size="1" onchange="show_state(this)">
          <option value="0">日明细</option>
          <option value="1" selected>月汇总</option>         
        </select>&nbsp; &nbsp; 
       </td>        
       <td align= "left" nowrap>
           <html:select name="dailyRegisterForm" property="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
            </html:select>
       </td>   
       <td align= "left" nowrap>
         &nbsp;状态&nbsp;
          </td>   
       <td align= "left" nowrap>
         <hrms:optioncollection name="dailyRegisterForm" property="showtypelist" collection="list" />
	   <html:select name="dailyRegisterForm" property="showtype" size="1" onchange="change()">
           <html:options collection="list" property="dataValue" labelProperty="dataName"/>
           </html:select>  
       </td>   
       <td align= "left" nowrap>
           &nbsp;<bean:message key="label.title.name"/> &nbsp;
            </td>   
       <td align= "left" nowrap>
           <input type="text" name="select_name" value="${dailyRegisterForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">
           &nbsp;<button extra="button" onclick="javascript:selectflag();">查询</button> &nbsp;
        
      </td>
      <td align= "left" nowrap>
       
        <hrms:kqcourse/>
        <html:hidden name="dailyRegisterForm" property="kq_duration"/>
        <html:hidden name="dailyRegisterForm" property="code" styleClass="text"/>
        <html:hidden name="dailyRegisterForm" property="userbase" styleClass="text"/>
        <html:hidden name="dailyRegisterForm" property="kind" styleClass="text"/> 
        <html:hidden name="dailyRegisterForm" property="returnURL" styleClass="text"/>
        <!--<html:hidden name="dailyRegisterForm" property="condition" styleClass="text"/>-->                    
      </td>
      
    </tr>
    </table>
  </td>
 </tr>
    <tr><td height="3px"></td></tr>
 <tr>
  <td width="100%">
 <%int i=0;
   int lock=0;
 %>
 <script language='javascript' >
		document.write("<div id=\"tbl-container\"  style='position:absolute;left:5;height:"+(document.body.clientHeight-104)+";width:100%'  >");
 </script> 
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" >
      <thead>
         <tr>
        
            <logic:iterate id="element"    name="dailyRegisterForm"  property="fielditemlist"> 
               <logic:equal name="element" property="visible" value="true">
                 <logic:equal name="element" property="itemtype" value="A">
                    <%if(i<lockedNum) {%>
                        <td align="center" class="TableRow" nowrap style="border-top:none;">
                        <bean:write  name="element" property="itemdesc"/>&nbsp; 
                        </td>
                   <%}else{ %>
                        <td align="center" class="TableRow" nowrap style="border-top:none;">
                        <bean:write  name="element" property="itemdesc"/>&nbsp; 
                        </td>
                  <%}
                   i++;
                   %>
                 </logic:equal>
                 <logic:notEqual name="element" property="itemtype" value="A">
                   <td align="center" class="TableRow" nowrap style="border-top:none;">
                   <hrms:textnewline text="${element.itemdesc}" len="5"></hrms:textnewline>                     
                   </td>
                 </logic:notEqual>
              </logic:equal>
           </logic:iterate>         	        
           </tr>
      </thead> 
      <hrms:paginationdb id="element" name="dailyRegisterForm" sql_str="dailyRegisterForm.sqlstr" table="" where_str="dailyRegisterForm.strwhere" columns="dailyRegisterForm.columns" order_by="dailyRegisterForm.orderby" pagerows="${dailyRegisterForm.pagerows}" page_id="pagination">
                 	<bean:define id="nbase1" name="element" property="nbase"/>
					<bean:define id="a01001" name="element" property="a0100"/>
					<%
					      	String nbase2=PubFunc.encrypt(nbase1.toString());
					       	String a01002=PubFunc.encrypt(a01001.toString());
					%>
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
          <% int  inNum=0;lock=0;%>          
            <logic:iterate id="info" name="dailyRegisterForm"  property="fielditemlist">  
                <logic:equal name="info" property="visible" value="false">
                  <html:hidden name="element" property="${info.itemid}"/>  
                </logic:equal>
                <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                      <%if(lock<lockedNum) {%>
                         <td align="left" class="RecordRow" nowrap>
                       <%}else{ %>
                          <td align="left" class="RecordRow" nowrap>
                       <% }
                       lock++;
                    %>
                       <logic:notEqual name="info" property="codesetid" value="0">
                       <logic:notEqual name="info" property="itemid" value="e01a1">                          
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${dailyRegisterForm.uplevel}"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                          </logic:notEqual>
                          <logic:equal name="info" property="itemid" value="e01a1">                          	                    
                              <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                                &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;      
                          </logic:equal>  
                         </logic:notEqual>
                         <logic:equal name="info" property="codesetid" value="0">
                          
                             <logic:notEqual name="info" property="itemid" value="a0101">
                              <logic:notEqual name="info" property="itemid" value="overrule">
                               &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                             </logic:notEqual>
                             </logic:notEqual>
                             <logic:equal name="info" property="itemid" value="a0101">
                               <logic:notEqual name="info" property="itemid" value="overrule">
                               &nbsp; <a href="/kq/register/single_register.do?b_browse=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&userbase=<%=nbase2 %>&start_date=${dailyRegisterForm.start_date}&end_date=${dailyRegisterForm.end_date}&A0100=<%=a01002 %>">
                                <bean:write name="element" property="${info.itemid}" filter="true"/></a>&nbsp;
                               </logic:notEqual>
                             </logic:equal>
                              <logic:equal name="info" property="itemid" value="overrule">
                               <logic:notEqual name="info" property="itemid" value="a0101">
                                &nbsp;
                                 <img src="/images/edit.gif" border="0" alt="点击查看意见" onclick="showOverrule('<%=nbase2 %>','<%= a01002 %>','${dailyRegisterForm.kq_duration}');">
                                 
                               </logic:notEqual>
                             </logic:equal>  
                         </logic:equal>
                         </td> 
                      </logic:equal>
                   <!--数字-->
                      <logic:equal name="info" property="itemtype" value="N">
	                      <td align="center" class="RecordRow" nowrap> &nbsp;
	                         <logic:greaterThan name="element" property="${info.itemid}" value="0">
	                           &nbsp; <bean:write name="element" property="${info.itemid}"/>&nbsp;
	                         </logic:greaterThan> 
	                      </td>
                      </logic:equal>
                      
                      <!--日期型-->
                      <logic:equal name="info" property="itemtype" value="D">
	                      <td align="center" class="RecordRow" nowrap> &nbsp;                         
	                           &nbsp; <bean:write name="element" property="${info.itemid}"/>&nbsp;                         
	                      </td>
                      </logic:equal>
                 </logic:equal> 
               <!---->               
            </logic:iterate>   
          </tr>
        </hrms:paginationdb>    	                           	    		        	        	        
      </table>
    <script language='javascript' >
	document.write("</div>");
    </script>
     </td>
   </tr> 
   <tr>
   <td>
  
    <script language='javascript' >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-75)+";width:100%'  >");
	</script>
     <table  width="100%" class="RecordRowP" align="center">
       <tr>
          <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
              <hrms:paginationtag name="dailyRegisterForm"
								pagerows="${dailyRegisterForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
	  </td>
	  <td  width="80%" align="left" nowrap class="tdFontcolor">
	     <p align="right"><hrms:paginationdblink name="dailyRegisterForm" property="pagination" nameId="dailyRegisterForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	  <td></td>
	</tr>	
     </table>
   <script language='javascript' >
	document.write("</div>");
    </script>
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
  hide_nbase_select('select_pre');
   var view_select_flag="${dailyRegisterForm.select_flag}";	
 if(view_select_flag=="1")
   Element.show('datepnl');
 else
   Element.hide('datepnl'); 
</script>