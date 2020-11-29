<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.OrgRegisterForm" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="java.util.HashMap" %>
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
<SCRIPT LANGUAGE="javascript" src="/js/xtree.js"></SCRIPT>
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
<html:form action="/kq/register/collect_orgsumdata">
<script language="javascript">
   //验证是否报批成功 
   function showValidate(orgsumvali){
     if(orgsumvali=="true"){
        alert("考勤数据已报批");
     }else if(orgsumvali=="false"){
        alert("考勤数据报批失败");
     }
   }
   function MusterInitData()
  {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
  }
  function couser()
   {
      orgRegisterForm.action="/kq/register/history/sumorgbrowsedata.do?b_search=link&code=${orgRegisterForm.code}&kind=${orgRegisterForm.kind}";
      orgRegisterForm.submit();
   }
    function gosum()
   {
      orgRegisterForm.action="/kq/register/browse_orgregister.do?b_search=link&action=search_orgregisterdata.do&target=mil_body";
      orgRegisterForm.target="il_body";
      orgRegisterForm.submit();
   }
   function godaily()
   {
      orgRegisterForm.action="/kq/register/search_orgdailyregister.do?b_search=link&action=search_orgdailyregisterdata.do&target=mil_body";
      orgRegisterForm.target="il_body";
      orgRegisterForm.submit();
   }
    function daily_collect()
   {
        var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
        orgRegisterForm.action="/kq/register/collect_orgdailydata.do?b_orgdaily=link&action=collect_orgdailydata.do&target=mil_body&a_inforkind=1";
        orgRegisterForm.target="il_body";        
        orgRegisterForm.submit();
   } 
    function sum_collect()
   {
        var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
        orgRegisterForm.action="/kq/register/collect_orgsumdata.do?b_orgsum=link&action=collect_orgsumdata.do&target=mil_body&a_inforkind=1";
        orgRegisterForm.target="il_body";
        orgRegisterForm.submit();
   } 
   function goback()
   {
      orgRegisterForm.action="/kq/register/browse_orgregister.do?b_search=link&action=search_orgregisterdata.do&target=mil_body&flag=noself";
      orgRegisterForm.target="il_body"
      orgRegisterForm.submit();
   }
    function search_amb()
   {
        orgRegisterForm.action="/kq/register/ambiquity/orgsearch_ambiquity.do?b_search=link&action=orgsearch_ambiquitydata.do&target=mil_body&a_inforkind=1";
        orgRegisterForm.target="il_body";
        orgRegisterForm.submit();
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
 function changeys(dd)
{
	if(dd==2){
 		orgRegisterForm.action="/kq/register/collect_orgsumdata.do?b_search=link&selectys=2&code=${orgRegisterForm.code}&kind=${orgRegisterForm.kind}";
    	orgRegisterForm.submit();
 	}else if(dd==1){
 		orgRegisterForm.action="/kq/register/collect_orgsumdata.do?b_search=link&selectys=1";
    	orgRegisterForm.submit();
 	}
} 
   function go_must(){
	    var returnURL = "${orgRegisterForm.returnURL}";
	    var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${orgRegisterForm.nprint}&closeWindow=1";
	    	urlstr+="&returnURL="+returnURL; 
	    window.showModalDialog(urlstr,1, 
	      "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
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
</script><hrms:themes /> <!-- 7.0css -->
<table>
 <tr>
  <td>
   <table width="60%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr> 
       <td align="left">
           <table border="0" cellspacing="0"  align="left" cellpadding="0"><tr><td>
            <hrms:menubar menu="menu1" id="menubar1" target="mil_body">                  
              <hrms:menuitem name="def" label="部门数据处理">
              <hrms:menuitem name="mitem1" label="生成部门日汇总" icon="/images/write.gif"  url="javascript:daily_collect();" command="" function_id="2702120"/>
              <hrms:menuitem name="mitem2" label="生成部门月汇总" icon="/images/sort.gif"  url="javascript:sum_collect();" command="" function_id="2702121"/> 
              <hrms:menuitem name="mitem5" label="导出Excel" icon="/images/export.gif" url="javascript:excecuteExcel();" function_id="2702123"/>
              <hrms:menuitem name="mitem4" label="打印" icon="/images/print.gif" url="javascript:go_must();" function_id="2702124"/>              
              </hrms:menuitem>              
           </hrms:menubar>
           </td></tr></table>
        </td> 
        <td align= "left" nowrap>&nbsp;&nbsp;
        <select name="showstate" size="1" onchange="show_state(this)">
          <option value="0">日明细</option>
          <option value="1" selected >月汇总</option>
          <option value="2">不定期</option>
        </select>&nbsp;&nbsp;
       </td>   
    <td align= "left" nowrap>&nbsp;&nbsp;
        <hrms:kqcourse></hrms:kqcourse>
        <html:hidden name="orgRegisterForm" property="codesetid" styleClass="text"/> 
        <html:hidden name="orgRegisterForm" property="code" styleClass="text"/>        
        <html:hidden name="orgRegisterForm" property="kind" styleClass="text"/>                  
      </td>
    </tr>
    </table>
  </td>
 </tr>
   <tr><td height="3px"></td></tr>
 <tr>
  <td>  
 <%int i=0;
   String name=null;
   int num_s=0;
 %>
 <div class="fixedDiv2">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
      <thead>
         <tr>
            <logic:iterate id="element"    name="orgRegisterForm"  property="fielditemlist"> 
               <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" nowrap style="border-left:none;border-top:none;">
                  <bean:write  name="element" property="itemdesc"/>&nbsp; 
                 </td>
               </logic:equal>
           </logic:iterate>           	        
           </tr>
        </thead>        
       	 <hrms:paginationdb id="element" name="orgRegisterForm" sql_str="orgRegisterForm.sqlstr" table="" where_str="orgRegisterForm.strwhere" columns="orgRegisterForm.columns" order_by="orgRegisterForm.orderby" pagerows="18" page_id="pagination">
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
            <logic:iterate id="info" name="orgRegisterForm"  property="fielditemlist">  
             
              <%
               		OrgRegisterForm orgRegisterForm=(OrgRegisterForm)session.getAttribute("orgRegisterForm");
               		FieldItem item=(FieldItem)pageContext.getAttribute("info");
               		name=item.getItemid(); 
              %> 
             <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                       <logic:notEqual name="info" property="codesetid" value="0">
                            <td align="left" class="RecordRow" style="border-left:none" nowrap> 
                               <%if(r==0)
                               {
                               %>
                                  &nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
                                  <bean:write name="codeitem" property="codename" />
                                    <hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" uplevel="${orgRegisterForm.uplevel }" scope="page"/>  	      
                                  <bean:write name="codeitem" property="codename" />
                                  
                               <%}else{%>
                                  <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                                  &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                               <%}%>                        
                              
                              
                               
                            
                            </td>  
                       </logic:notEqual>
                       <logic:equal name="info" property="codesetid" value="0">
                            <td align="left" class="RecordRow" style="border-left:none" nowrap>
                               &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                            </td> 
                       </logic:equal>
                   </logic:equal>
                   <!--数字-->
                   <logic:equal name="info" property="itemtype" value="N">
                       <td align="center" class="RecordRow" style="border-left:none" nowrap>
                       <logic:notEqual name="orgRegisterForm" property="selectys" value="2">
                         <logic:greaterThan name="element" property="${info.itemid}" value="0">
                            <bean:write name="element" property="${info.itemid}"/>
                         </logic:greaterThan>
                       </logic:notEqual>
                       <logic:equal name="orgRegisterForm" property="selectys" value="2">
                       		<%
                         num_s++;
                         request.setAttribute("num_s",num_s+""); 
                         HashMap infoMap=(HashMap)orgRegisterForm.getKqItem_hash();
                         //out.println("d = "+abean.get(name));
                       %>
                       <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${info.itemid}" value='<%=abean.get(name)+""%>'/>
                       <%
                                inNum++;
                        %>  
                       </logic:equal>
                       </td>
                   </logic:equal>
                   
                   <logic:notEqual name="info" property="itemtype" value="A">
                   	<logic:notEqual name="info" property="itemtype" value="N">
                       <td align="center" class="RecordRow" style="border-left:none" nowrap>
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
      <table  width="100%" align="center" class="RecordRowP">
       <tr>
          <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
             第<bean:write name="pagination" property="current" filter="true" />页
             共<bean:write name="pagination" property="count" filter="true" />条
             共<bean:write name="pagination" property="pages" filter="true" />页
	  </td>
	  <td  width="80%" align="right" nowrap class="tdFontcolor">
	     <p align="right"><hrms:paginationdblink name="orgRegisterForm" property="pagination" nameId="orgRegisterForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>
     </table>
     <!-- 
     <tr>
	   <td width="60%" align="center"  nowrap style="height:35px;">	       
	       <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="goback();" class="mybutton">						      
           </td>
	   <td width="40%"></td>
        </tr>
         -->
   </td>
 </tr>
</table>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
            <td class="td_style common_background_color" height=24><bean:message key="classdata.isnow.wiat"/></td>
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
<iframe name="mysearchframe" style="display: none;"></iframe>
<script language="javascript">   
   showValidate('<bean:write name="orgRegisterForm"  property="orgsumvali"/>');
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
  MusterInitData();
</script>