<%@ page contentType="text/html; charset=UTF-8"%>
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
<style>
<!--
.divStyle{

	border:1px solid #C4D8EE;border-top:none;margin-top:3px;overflow: auto;left:5;
	height:expression(document.body.clientHeight-97);
	width:expression(document.body.clientWidth-11);
}
.divStyle1{
	overflow: auto;left:5;
	width:expression(document.body.clientWidth-11);
}
-->
</style>
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
<html:form action="/kq/register/history/sumorgbrowsedata">
<script language="javascript">
   //验证是否报批成功 
   function showValidate(orgsumvali){
     if(orgsumvali=="true"){
        alert("考勤数据已报批");
     }else if(orgsumvali=="false"){
        alert("考勤数据报批失败");
     }
   }
   function couser()
   {
      browseHistoryForm.action="/kq/register/history/sumorgbrowsedata.do?b_couser=link&code=${browseHistoryForm.code}&kind=${browseHistoryForm.kind}";
      browseHistoryForm.submit();
   }
    function gosum()
   {
      browseHistoryForm.action="/kq/register/browse_orgregister.do?b_search=link&action=search_orgregisterdata.do&target=mil_body";
      browseHistoryForm.target="il_body";
      browseHistoryForm.submit();
   }
   function godaily()
   {
      browseHistoryForm.action="/kq/register/search_orgdailyregister.do?b_search=link&action=search_orgdailyregisterdata.do&target=mil_body";
      browseHistoryForm.target="il_body";
      browseHistoryForm.submit();
   }
    function daily_collect()
   {
        browseHistoryForm.action="/kq/register/collect_orgdailydata.do?b_orgdaily=link&action=collect_orgdailydata.do&target=mil_body&a_inforkind=1";
        browseHistoryForm.target="il_body";        
        browseHistoryForm.submit();
   } 
    function sum_collect()
   {
        browseHistoryForm.action="/kq/register/collect_orgsumdata.do?b_orgsum=link&action=collect_orgsumdata.do&target=mil_body&a_inforkind=1";
        browseHistoryForm.target="il_body";
        browseHistoryForm.submit();
   } 
   function goback()
   {
      browseHistoryForm.action="/kq/register/browse_orgregister.do?b_search=link&action=search_orgregisterdata.do&target=mil_body&flag=noself";
      browseHistoryForm.target="il_body"
      browseHistoryForm.submit();
   }
   function daily_historyt()
   {
        browseHistoryForm.action="/kq/register/history/dailyorgbrowse.do?b_search=link&action=dailyorgbrowsedata.do&target=mil_body&a_inforkind=1&privtype=kq";
        browseHistoryForm.target="il_body";
        browseHistoryForm.submit();
   }
    function sum_historyt()
   {
        browseHistoryForm.action="/kq/register/history/sumorgbrowse.do?b_search=link&action=sumorgbrowsedata.do&target=mil_body&a_inforkind=1&privtype=kq";
        browseHistoryForm.target="il_body";
        browseHistoryForm.submit();
   }
       function sum_historicalt(obj)
   {
     if(obj.value=="1"){
     	obj.options(0).selected = true;
        browseHistoryForm.action="/kq/register/historical/sumorgbrowse.do?b_search=link&action=sumorgbrowsedata.do&target=mil_body&a_inforkind=1&privtype=kq";
        browseHistoryForm.target="il_body";
        browseHistoryForm.submit();
      }
   }
function change_print(){
	//document.mysearchform.submit();
	var returnURL = getEncodeStr("${browseHistoryForm.returnURL}");
	var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${browseHistoryForm.relatTableid}&closeWindow=1";
 	urlstr+="&returnURL="+returnURL;
    window.showModalDialog(urlstr,1, 
        "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
}
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
   function changeys(dd)
{
	if(dd==2){
 		browseHistoryForm.action="/kq/register/history/sumorgbrowsedata.do?b_search=link&selectys=2&code=${browseHistoryForm.code}&kind=${browseHistoryForm.kind}";
    	browseHistoryForm.submit();
 	}else if(dd==1){
 		browseHistoryForm.action="/kq/register/history/sumorgbrowsedata.do?b_search=link&selectys=1";
    	browseHistoryForm.submit();
 	}
}
</script>
<hrms:themes /> <!-- 7.0css -->
<table>
 <tr>
  <td>
   <table width="60%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr> 
       <td align="left">
           <table border="0" cellspacing="0"  align="left" cellpadding="0"><tr><td>
            <hrms:menubar menu="menu1" id="menubar1" target="mil_body">               
              <hrms:menuitem name="sss" label="数据分类" >
              <hrms:menuitem name="mitem1" label="日明细" icon="/images/quick_query.gif" url="javascript:daily_historyt();" function_id="27051"/>  
              <hrms:menuitem name="mitem2" label="月汇总" icon="/images/add_del.gif" url="javascript:sum_historyt();" function_id="27051"/> 
               <%
               BrowseHistoryForm browseHistoryForm=(BrowseHistoryForm)session.getAttribute("browseHistoryForm");
               String flg=browseHistoryForm.getFlag();
               if(flg.equals("1")){ 
               %>
               <hrms:menuitem name="mitem3" label="打印" icon="/images/sort.gif" url="javascript:change_print();" function_id="27051"/> 
              <%} %>
              </hrms:menuitem> 
           </hrms:menubar>
           </td></tr></table>
        </td>  
        <!--  
        <td align= "left" nowrap>&nbsp;
           <select onchange="sum_historicalt(this)">
           	<option value="0">封存数据</option>
           	<% //UserView userView=(UserView)session.getAttribute(WebConstant.userView);
           		//if(userView.getVersion_flag() == 1){
           	%>
           	<option value="1">归档数据</option>
           	<%//} %>
           </select>
       </td> 
       -->
      <td align= "left" nowrap>&nbsp;
       <bean:message key="kq.register.daily.menu"/>
       <html:select name="browseHistoryForm" property="year" size="0" onchange="javascript:couser();">
          <html:optionsCollection property="yearlist" value="dataValue" label="dataName"/>
          </html:select>&nbsp;年&nbsp; 
        <html:hidden name="browseHistoryForm" property="code" styleClass="text"/>
        <html:hidden name="browseHistoryForm" property="kind" styleClass="text"/>   
        <html:hidden name="browseHistoryForm" property="returnURL" styleClass="text"/>
      </td>
      <td align= "left" nowrap>
       <html:select name="browseHistoryForm" property="duration" size="0" onchange="javascript:couser();">
          <html:optionsCollection property="durationlist" value="dataValue" label="dataName"/>
          </html:select>&nbsp;月&nbsp; 
        <html:hidden name="browseHistoryForm" property="code" styleClass="text"/>
        <html:hidden name="browseHistoryForm" property="kind" styleClass="text"/>   
        <html:hidden name="browseHistoryForm" property="returnURL" styleClass="text"/>
      </td>
     <!-- 
      <td align= "left" nowrap>
      		时间显示方式:
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
 <div class="divStyle common_border_color">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" style="border:none;" >
      <thead>
         <tr>
            <logic:iterate id="element"    name="browseHistoryForm"  property="fielditemlist"> 
               <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" style="border-left:none;" nowrap>
                  <bean:write  name="element" property="itemdesc"/>&nbsp; 
                 </td>
               </logic:equal>
           </logic:iterate>           	        
           </tr>
        </thead>          
       	 <hrms:paginationdb id="element" name="browseHistoryForm" sql_str="browseHistoryForm.sqlstr" table="" where_str="browseHistoryForm.strwhere" columns="browseHistoryForm.columns" order_by="browseHistoryForm.orderby" pagerows="${browseHistoryForm.pagerows}" page_id="pagination">
          <%
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
          <%
               int r=0;
             %>           
            <logic:iterate id="info" name="browseHistoryForm"  property="fielditemlist">  
             
             <%
             	    BrowseHistoryForm browseHistoryForm=(BrowseHistoryForm)session.getAttribute("browseHistoryForm");
               		FieldItem item=(FieldItem)pageContext.getAttribute("info");
               		name=item.getItemid(); 
             %> 
             <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                       <logic:notEqual name="info" property="codesetid" value="0">
                            <td align="left" class="RecordRow" style="border-left:none;" nowrap> 
                               <%if(r==0)
                               {
                               %>
                               <a href="/kq/register/history/orgyearsingle.do?b_browse=link&&sessiondate=${browseHistoryForm.sessiondate}&b0110=<bean:write name="element" property="b0110" filter="true"/>">
                                  &nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
                                  <bean:write name="codeitem" property="codename" />
                                    <hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
                                  <bean:write name="codeitem" property="codename" />
                               </a>   
                               <%}else{%>
                                  <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                                  &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                               <%}%>                        
                              
                              
                               
                            
                            </td>  
                       </logic:notEqual>
                       <logic:equal name="info" property="codesetid" value="0">
                            <td align="left" class="RecordRow" style="border-left:none;" nowrap>
                               &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                            </td> 
                       </logic:equal>
                   </logic:equal>
                   <!--数字-->
                   <logic:equal name="info" property="itemtype" value="N">
                       <td align="center" class="RecordRow" style="border-left:none;" nowrap>
                        <logic:equal name="browseHistoryForm" property="selectys" value="1">
                         <logic:greaterThan name="element" property="${info.itemid}" value="0">
                            <bean:write name="element" property="${info.itemid}"/>
                         </logic:greaterThan>
                         </logic:equal>
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
                   <logic:notEqual name="info" property="itemtype" value="A">
                   	<logic:notEqual name="info" property="itemtype" value="N">
                       <td align="center" class="RecordRow" style="border-left:none;" nowrap>
                       &nbsp;
                       </td>
                       </logic:notEqual>
                   </logic:notEqual>
            </logic:equal> 
            <%
            r++;
            %>                                        
          </logic:iterate>  
          </tr>
        </hrms:paginationdb>                          	    		        	        	        
      </table>
      </div>
     </td>
   </tr> 
   <tr>
   <td>
   <div class="divStyle1">
      <table  width="100%"  align="left">
       <tr>         
          <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
             <hrms:paginationtag name="browseHistoryForm"
								pagerows="${browseHistoryForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
	  </td>
	  <td  width="80%" align="right" nowrap class="tdFontcolor">
	     <hrms:paginationdblink name="browseHistoryForm" property="pagination" nameId="browseHistoryForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>	
     </table>
     </div>
   </td>
 </tr>
 <!-- 
 <tr>
  <td valign="bottom" align="left"  class="tdFontcolor" nowrap>
          <input type="button" name="b_quit" value='<bean:message key="button.leave"/>' onclick="quitRe();" class="mybutton"> 
         </td>
         
 </tr>
  -->
</table>
</html:form>
<iframe name="mysearchframe" style="display: none;"></iframe>
<script language="javascript">   
   showValidate('<bean:write name="browseHistoryForm"  property="orgsumvali"/>');
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