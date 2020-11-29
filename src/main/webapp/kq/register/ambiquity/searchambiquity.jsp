<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.AmbiquityFrom" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<link href="/kq/kq_tableLocked.css" rel="stylesheet" type="text/css"> 
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
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
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
<html:form action="/kq/register/ambiquity/select_ambiquitydata">
<logic:equal name="ambiquityFrom" property="error_flag" value="0">
<script language="javascript">
   function change()
   {
      ambiquityFrom.action="/kq/register/ambiquity/select_ambiquitydata.do?b_search=link";
      ambiquityFrom.submit();
   }  
   function go_daily()
   {
      ambiquityFrom.action="/kq/register/daily_register.do?b_search=link&action=daily_registerdata.do&target=mil_body&flag=noself&privtype=kq&viewPost=kq";
      ambiquityFrom.target="il_body";
      ambiquityFrom.submit();
   } 
   function showCollect()
   {
      ambiquityFrom.action="/kq/register/select_collect.do?b_search=link&action=select_collectdata.do&target=mil_body&flag=noself&privtype=kq&viewPost=kq";
      ambiquityFrom.target="il_body";
      ambiquityFrom.submit();
   }
   function show_state(state)
   {
     //var state = s.options[s.selectedIndex].value;     
     if(state==0)
     {
       go_daily();
     }else if(state==1)
     {
        showCollect();
     }else if(state==2)
     {
       
      }
   }
   function ambiquity()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       target_url="/kq/register/ambiquity/select_ambiquity.do?b_select=link";
       var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:360px; dialogHeight:260px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(!return_vo)
		   return false;	   
	   if(return_vo.flag=true)
	   {
		    var stat_end=return_vo.stat_end;
		    var stat_start=return_vo.stat_start;
		    //var waitInfo=eval("wait");	   
	        //waitInfo.style.display="block";	
	        ambiquityFrom.action="/kq/register/ambiquity/select_ambiquity.do?b_stat=link&action=select_ambiquitydata.do&target=mil_body&stat_start="+stat_start+"&stat_end="+stat_end;
		    ambiquityFrom.target="il_body";
		    ambiquityFrom.submit();
	  }  
   }
   function indicator()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       target_url="/kq/register/indicator/indicator.do?b_query=link&re_flag=3";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
       var newwindow=window.showModalDialog(iframe_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(newwindow)
       {
    	   ambiquityFrom.action="/kq/register/indicator/indicator.do?b_save3=link";
    	   ambiquityFrom.target="mil_body";
           ambiquityFrom.submit(); 
       }
   } 
   function kqreport()
   {
   delChecked();
      ambiquityFrom.action="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&report_id=2&userbase=${ambiquityFrom.userbase}&code=${ambiquityFrom.code}&coursedate=${ambiquityFrom.duration}&kind=${ambiquityFrom.kind}&privtype=kq";
      ambiquityFrom.target="il_body";
      ambiquityFrom.submit();
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
   function go_must()
   {
       var returnURL = getEncodeStr("${ambiquityFrom.returnURL}");
      var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${ambiquityFrom.relatTableid}&closeWindow=1";
      urlstr+="&returnURL="+returnURL + "&kqpre=${ambiquityFrom.select_pre}";
       window.showModalDialog(urlstr,1, 
        "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
   } 
   function selectKq()
   {
		var winFeatures = "dialogWidth:734px; dialogHeight:375px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes"
		var target_url = "/kq/query/searchfiled.do?b_init=link`table=q03";
		if($URL)
			target_url = $URL.encode(target_url);
		var iframe_url = "/general/query/common/iframe_query.jsp?src="+target_url;
		var return_vo= window.showModalDialog(iframe_url,1,winFeatures);   
		if(return_vo){
		   ambiquityFrom.action="/kq/register/ambiquity/select_ambiquitydata.do?b_search=link&select_flag=2&selectResult="+ $URL.encode(return_vo);
           ambiquityFrom.submit();
		}
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
       ambiquityFrom.action="/kq/register/ambiquity/select_ambiquitydata.do?b_search=link&select_flag=0";
       ambiquityFrom.submit();
   }
   function selectflag()
   {
      ambiquityFrom.action="/kq/register/ambiquity/select_ambiquitydata.do?b_search=link&select_flag=1";
      ambiquityFrom.submit();
   } 
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
</script>
<hrms:themes />
<table width="95%">
 <tr>
  <td>
   <table border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>  
       <td>
     <table border="0" cellspacing="0"  align="left" cellpadding="0">
      <tr>
      <td>
       <hrms:menubar menu="menu1" id="menubar1" target="mil_body">
        <hrms:menuitem name="text" label="文件" function_id="270201,0C310">
        <hrms:menuitem name="mitem1" label="简单花名册" icon="/images/view.gif" url="javascript:kqreport();" function_id="2702019,0C3103"/>       
        <hrms:menuitem name="mitem2" label="高级花名册" icon="/images/add_del.gif" url="javascript:go_must();" function_id="2702014,0C3104"/>  
        </hrms:menuitem> 
        <hrms:menuitem name="view" label="显示" function_id="270201,0C311">
        <hrms:menuitem name="mitem1" label="查询" icon="/images/write.gif" url="javascript:selectKq();" function_id="2702012,0C3114"/>  
        <hrms:menuitem name="mitem3" label="显示&隐藏指标" icon="/images/add_del.gif" url="javascript:indicator();" function_id="2702015"/>  
        </hrms:menuitem> 
        <hrms:menuitem name="aiw" label="业务处理" function_id="270202,0C311,0C312">
        <hrms:menuitem name="mitem2" label="不定期汇总" icon="/images/sort.gif" url="javascript:ambiquity();" function_id="2702023,0C3123"/>       
               
        </hrms:menuitem> 
        <hrms:menuitem name="rec2" label="浏览" function_id="270203,0C311"> 
        <%
         AmbiquityFrom ambiquityFrom=(AmbiquityFrom)session.getAttribute("ambiquityFrom");
         String flg = ambiquityFrom.getFlag();
         //<hrms:menuitem name="mitem3" label="不定期" icon="/images/write.gif" url="javascript:show_state('2');" function_id="2702031,0C3124"/>
         if(flg!=null&&flg.equals("2")){
         %>  
        <hrms:menuitem name="mitem1" label="日明细" icon="/images/add_del.gif" url="javascript:show_state('0');" function_id="2702032,0C3113"/>        
        <hrms:menuitem name="mitem2" label="月汇总" icon="/images/sort.gif" url="javascript:show_state('1');" function_id="2702030,0C3110"/>
       <%}else{ %>
        <hrms:menuitem name="mitem1" label="日明细" icon="/images/add_del.gif" url="javascript:show_state('0');" function_id="2702032,0C3113"/>        
        <hrms:menuitem name="mitem2" label="月汇总" icon="/images/sort.gif" url="javascript:show_state('1');" function_id="2702030,0C3110"/>
        <hrms:menuitem name="mitem3" label="不定期" icon="/images/write.gif" url="javascript:show_state('2');" function_id="2702031,0C3124"/>
       <%} %>
        </hrms:menuitem>           
       </hrms:menubar>
      </td>
    </tr>
  </table>
       </td>     
       <td align= "left" nowrap>
          &nbsp; <html:select name="ambiquityFrom" property="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
            </html:select>
       </td> 
      <td align= "left" nowrap>
           &nbsp;按&nbsp;
           </td> 
      <td align= "left" nowrap>
      <html:select name="ambiquityFrom" property="select_type"  size="1">
            	<html:option value="0"><bean:message key="label.title.name" /></html:option>                      
                <html:option value="1">工号</html:option>
                <html:option value="2">考勤卡号</html:option>
           </html:select>&nbsp;
           <input type="text" name="select_name" class="inputtext" style="width:100px;font-size:10pt;text-align:left">&nbsp;
           <button extra="button" onclick="javascript:selectflag();">查询</button> 
         
      </td>
      
      <td align= "left" nowrap>
		&nbsp;
        <bean:message key="kq.register.ambiquity.duration"/>&nbsp; 
        <logic:notEqual name="ambiquityFrom" property="kq_period" value="">
          <bean:write name="ambiquityFrom" property="kq_period" />   
        </logic:notEqual>

        <html:hidden name="ambiquityFrom" property="code" styleClass="text"/>
        <html:hidden name="ambiquityFrom" property="userbase" styleClass="text"/>
        <html:hidden name="ambiquityFrom" property="kind" styleClass="text"/>     
        <html:hidden name="ambiquityFrom" property="returnURL" styleClass="text"/>
      </td>
      
    </tr>
    </table>
  </td>
 </tr>
 <tr><td height="3px"></td></tr>
 <tr>
  <td>
 <%int i=0;%>
<div class="fixedDiv2" style="width:expression(document.body.clientWidth-15);">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" >
      <thead>
         <tr>
        
            <logic:iterate id="element"    name="ambiquityFrom"  property="fielditemlist"> 
               <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" nowrap style="border-top:none">
                  &nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp; 
                 </td>
              </logic:equal>
           </logic:iterate>         	        
           </tr>
      </thead>       
      <hrms:paginationdb id="element" name="ambiquityFrom" sql_str="ambiquityFrom.sqlstr" table="" where_str="ambiquityFrom.strwhere" columns="ambiquityFrom.columns" order_by="ambiquityFrom.orderby" pagerows="18" page_id="pagination">
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
            <logic:iterate id="info" name="ambiquityFrom"  property="fielditemlist">  
                <logic:equal name="info" property="visible" value="false">
                  <html:hidden name="element" property="${info.itemid}"/>  
                </logic:equal>
                <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                       <logic:notEqual name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap>
                          	<logic:notEqual name="info" property="itemid" value="e01a1">
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${dailyRegisterForm.uplevel}"/>  	      
                              &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                             <html:hidden name="element" property="${info.itemid}"/>   
                            </logic:notEqual>                         
                            <logic:equal name="info" property="itemid" value="e01a1">                          
                          		<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>
                          		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                          		<html:hidden name="element" property="${info.itemid}"/>                            	
                            </logic:equal>
                          </td>  
                         </logic:notEqual>
                         <logic:equal name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap>
                             <logic:equal name="info" property="itemid" value="overrule">                               
                               &nbsp;
                               <img src="/images/edit.gif" border="0" alt="点击察看意见" onclick="showOverrule('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>','${ambiquityFrom.kq_duration}');">
                               &nbsp;                               
                             </logic:equal>
                             <logic:notEqual name="info" property="itemid" value="overrule">
                               &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                             </logic:notEqual>
                           </td> 
                         </logic:equal>
                      </logic:equal>
                   <!--数字-->
                      <logic:equal name="info" property="itemtype" value="N">
                      <td align="right" class="RecordRow" nowrap> 
                         <logic:greaterThan name="element" property="${info.itemid}" value="0">
                            <bean:write name="element" property="${info.itemid}"/>&nbsp;
                         </logic:greaterThan> 
                      </td>
                  </logic:equal>
                 </logic:equal> 
               <!---->               
            </logic:iterate>   
          </tr>
        </hrms:paginationdb>    	                           	    		        	        	        
      </table>
	</div>
     </td>
   </tr> 
   <tr>
   <td>
     <table  style="width:expression(document.body.clientWidth-15);" class="RecordRowP">
       <tr>
       	<td width="60%" valign="bottom" align="left" height="30" nowrap>
	             第<bean:write name="pagination" property="current" filter="true" />页
	             共<bean:write name="pagination" property="count" filter="true" />条
	             共<bean:write name="pagination" property="pages" filter="true" />页
	  	</td>
	    <td  width="40%" valign="bottom" align="right" nowrap>
	      <p align="right"><hrms:paginationdblink name="ambiquityFrom" property="pagination" nameId="ambiquityFrom" scope="page">
             </hrms:paginationdblink>
	    </td>
	  </tr>	
     </table>
   </td>
 </tr>
 <tr>
  <td  valign="bottom" align="left"  class="tdFontcolor" nowrap>
  	<table>
  		<tr>
  			<td>
			    <input type="button" name="b_quit" value='<bean:message key="button.leave"/>' onclick="go_daily();" class="mybutton"> 
  			</td>
  		</tr>
  	</table>
  </td>
 </tr>
</table>
</logic:equal>
<logic:notEqual name="ambiquityFrom" property="error_flag" value="0">
<script language="javascript">
var error_str=kqErrorProcess('<bean:write name="ambiquityFrom"  property="error_flag"/>','<bean:write name="ambiquityFrom"  property="error_message"/>','<bean:write name="ambiquityFrom"  property="error_return"/>');
document.write(error_str);
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
  hide_nbase_select('select_pre');
   var view_select_flag="${ambiquityFrom.select_flag}";	
 if(view_select_flag=="1")
   Element.show('datepnl');
 else
   Element.hide('datepnl'); 
</script>