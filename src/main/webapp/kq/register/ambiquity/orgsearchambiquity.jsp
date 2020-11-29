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

<html:form action="/kq/register/ambiquity/orgselect_ambiquitydata">
<script language="javascript">
    function MusterInitData()
  {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
  }
   function go_search()  
   {
      ambiquityFrom.action="/kq/register/browse_orgregister.do?b_search=link&action=search_orgregisterdata.do&target=mil_body&flag=noself";
      ambiquityFrom.target="il_body";
      ambiquityFrom.submit();
   }
   function change()
   {
      ambiquityFrom.action="/kq/register/search_orgregisterdata.do?b_search=link&code=${orgRegisterForm.code}&kind=${orgRegisterForm.kind}";
      ambiquityFrom.submit();
   
   }
   function gosum()
   {
      
      ambiquityFrom.action="/kq/register/browse_orgregister.do?b_search=link&action=search_orgregisterdata.do&target=mil_body";
      ambiquityFrom.target="il_body";
      ambiquityFrom.submit();
   }
   function godaily()
   {
      ambiquityFrom.action="/kq/register/search_orgdailyregister.do?b_search=link&action=search_orgdailyregisterdata.do&target=mil_body";
      ambiquityFrom.target="il_body";
      ambiquityFrom.submit();
   }
    function daily_collect()
   {
        var waitInfo=eval("wait");	   
        waitInfo.style.display="block"; 
        ambiquityFrom.action="/kq/register/collect_orgdailydata.do?b_orgdaily=link&action=collect_orgdailydata.do&target=mil_body&a_inforkind=1";
        ambiquityFrom.target="il_body";        
        ambiquityFrom.submit();
   } 
    function sum_collect()
   {
        var waitInfo=eval("wait");	   
        waitInfo.style.display="block"; 
        ambiquityFrom.action="/kq/register/collect_orgsumdata.do?b_orgsum=link&action=collect_orgsumdata.do&target=mil_body&a_inforkind=1";
        ambiquityFrom.target="il_body";
        ambiquityFrom.submit();
   } 
   function ambiquity()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       target_url="/kq/register/ambiquity/orgselect_ambiquity.do?b_select=link";
       var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(!return_vo)
		   return false;	   
	   if(return_vo.flag=true)
	   {
		    var stat_end=return_vo.stat_end;
		    var stat_start=return_vo.stat_start;
		    ambiquityFrom.action="/kq/register/ambiquity/orgselect_ambiquity.do?b_stat=link&action=orgselect_ambiquitydata.do&target=mil_body&stat_start="+stat_start+"&stat_end="+stat_end;
		    ambiquityFrom.target="il_body";
		    ambiquityFrom.submit();
	  }  
   }
   function search_amb()
   {
        ambiquityFrom.action="/kq/register/ambiquity/orgsearch_ambiquity.do?b_search=link&action=orgsearch_ambiquitydata.do&target=mil_body&a_inforkind=1";
        ambiquityFrom.target="il_body";
        ambiquityFrom.submit();
   } 
   function show_state(s)
  {
      var state = s.options[s.selectedIndex].value;     
      if(state==0)
      {
         godaily();
      }else if(state==1)
      {
        gosum();//月汇总
      }else if(state==2)
      {
        search_amb();//不定期
      }
  }  
   function MusterInitData()
  {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
  }
  function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
  function excecuteExcel()
  {
	    var hashvo=new ParameterSet();	
		hashvo.setValue("colums","${orgRegisterForm.columns}");
		hashvo.setValue("tablename","Q09");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'15301110120'},hashvo);
  }
  function showfile(outparamters){
		var outName=outparamters.getValue("outName");
		if(outName == "error"){
			alert("导出失败！");
			return;
		}
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fileid=" + outName +"&fromjavafolder=true";
	}
</script>
<hrms:themes /> <!-- 7.0css -->
<logic:equal name="ambiquityFrom" property="error_flag" value="0">
<table>
 <tr>
  <td>
   <table width="30%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>      
      <td align= "left" nowrap>
       <table border="0" cellspacing="0"  align="left" cellpadding="0"><tr><td>
            <hrms:menubar menu="menu1" id="menubar1" target="mil_body">              
              <hrms:menuitem name="def" label="部门数据处理">              
              <hrms:menuitem name="mitem3" label="生成不定期汇总" icon="/images/quick_query.gif" url="javascript:ambiquity();" command="" function_id="2702122"/>
              <hrms:menuitem name="mitem5" label="导出Excel" icon="/images/export.gif" url="javascript:excecuteExcel();" function_id="2702123"/>             
              </hrms:menuitem>              
           </hrms:menubar>
           </td></tr></table>
      </td>
      <td align= "left" nowrap>&nbsp;&nbsp;
        <select name="showstate" size="1" onchange="show_state(this)">
          <option value="0">日明细</option>
          <option value="1" >月汇总</option>
          <option value="2" selected>不定期</option>
        </select>&nbsp;&nbsp;
       </td>   
      <td align= "left" nowrap>
       <logic:notEqual name="ambiquityFrom" property="kq_period" value="">
           <bean:message key="kq.register.daily.menu"/> 
           &nbsp;
           <bean:write name="ambiquityFrom" property="kq_period" />    
        </logic:notEqual>
        <html:hidden name="ambiquityFrom" property="code" styleClass="text"/>
       
        <html:hidden name="ambiquityFrom" property="kind" styleClass="text"/>                     
      </td>
      
    </tr>
    <tr><td height="3px"></td></tr>
    </table>
  </td>
 </tr>
 <tr>
  <td>
 <%int i=0;%>
 <div class="fixedDiv5">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
      <thead>
         <tr>
        
            <logic:iterate id="element"    name="ambiquityFrom"  property="fielditemlist"> 
               <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" nowrap style="border-left:none;border-top:none;">
                  <bean:write  name="element" property="itemdesc"/>&nbsp; 
                 </td>
              </logic:equal>
           </logic:iterate>         	        
           </tr>
      </thead>       
      <hrms:paginationdb id="element" name="ambiquityFrom" sql_str="ambiquityFrom.sqlstr" table="" where_str="ambiquityFrom.strwhere" columns="ambiquityFrom.columns" order_by="ambiquityFrom.orderby" pagerows="18" page_id="pagination">
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow">
          <%
          }else{
          %>
          <tr class="trDeep">
          <%}i++; 
           int r=0;
          %>      
            <logic:iterate id="info" name="ambiquityFrom"  property="fielditemlist">  

                <logic:equal name="info" property="visible" value="false">
                  <html:hidden name="element" property="${info.itemid}"/>  
                </logic:equal>
                <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                       <logic:notEqual name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" style="border-left:none" nowrap>
                              <%if(r==0)
                               {
                               %>
                                  &nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" uplevel="${orgRegisterForm.uplevel}"/>  	      
                                  <bean:write name="codeitem" property="codename" />
                                    <hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" scope="page" uplevel="${orgRegisterForm.uplevel}"/>  	      
                                  <bean:write name="codeitem" property="codename" />&nbsp;
                                  
                               <%}else{%>
                                  <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                                  &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                               <%}%>
                          </td>  
                         </logic:notEqual>
                         <logic:equal name="info" property="codesetid" value="0">
                          <td align="center" class="RecordRow" style="border-left:none" nowrap>
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
                      <td align="center" class="RecordRow" style="border-left:none" nowrap> 
                         <logic:greaterThan name="element" property="${info.itemid}" value="0">
                            <bean:write name="element" property="${info.itemid}"/>
                         </logic:greaterThan> 
                      </td>
                  </logic:equal>
                 </logic:equal> 
               <!----> 
                <% r++;%>              
            </logic:iterate>   
          </tr>
        </hrms:paginationdb>    	                           	    		        	        	        
      </table>
      </div>
     </td>
   </tr> 
   <tr>
   <td>
     <table  width="100%" class="RecordRowP" align="left">
       <tr>
          <td width="40%" valign="bottom"  class="tdFontcolor" nowrap>
             第<bean:write name="pagination" property="current" filter="true" />页
             共<bean:write name="pagination" property="count" filter="true" />条
             共<bean:write name="pagination" property="pages" filter="true" />页
	  </td>
	  <td  width="60%" align="left" nowrap class="tdFontcolor">
	     <p align="right"><hrms:paginationdblink name="ambiquityFrom" property="pagination" nameId="ambiquityFrom" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>	
     </table>
   </td>
 </tr>
 <tr><td height="3px"></td></tr>
 <tr>
   
     <%--<td  valign="bottom" align="left"  class="tdFontcolor" nowrap>
          <input type="button" name="b_quit" value='<bean:message key="button.leave"/>' onclick="quitRe();" class="mybutton"> 
     </td> 不知此返回有何意义--%>
 </tr>
</table>
</logic:equal>
<logic:notEqual name="ambiquityFrom" property="error_flag" value="0">
<script language="javascript">
var error_str=kqErrorProcess('<bean:write name="ambiquityFrom"  property="error_flag"/>','<bean:write name="ambiquityFrom"  property="error_message"/>','<bean:write name="ambiquityFrom"  property="error_return"/>');
document.write(error_str);
</script>
</logic:notEqual>
 <div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正在接收数据请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>
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
  MusterInitData();
</script>