<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script type="text/javascript" src="/kq/kq.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
<%@ page import="com.hjsj.hrms.actionform.kq.options.sign_point.KqSignPointForm" %>
<%@ page import="java.util.ArrayList" %>
<%
	int i = 0;
KqSignPointForm kqSignPointForm = (KqSignPointForm)request.getSession().getAttribute("kqSignPointForm");
String pid=kqSignPointForm.getPid();
pid=pid.substring(1);
%>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
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
   
//删除人员
function del()
{
	  var len=document.kqSignPointForm.elements.length;
 var uu;
 for (i=0;i<len;i++)
 {
     if (document.kqSignPointForm.elements[i].type=="checkbox")
      {
        if(document.kqSignPointForm.elements[i].checked==true)
        {
          uu="dd";
         }
      }
  }
 if(uu=="dd")
 {
    if(confirm("您确定要撤销该人员吗？"))
   {
     kqSignPointForm.action="/kq/options/sign_point/person_point.do?b_del=link";
     kqSignPointForm.submit();
    }
 }else
 {
   alert("请先选择人员！");
   return false;
 }

}
function search()
{
   
   var hashvo=new ParameterSet();
   var persons=null;
      persons=common_query_comroww("1","1",'100');
  
   if(persons!=null && persons.length>0){
	   hashvo.setValue("pid",<%=pid %>);
     hashvo.setValue("persons",persons);
     var request=new Request({method:'post',onSuccess:isok,functionId:'151211001136'},hashvo);
   }
}
function isok(){
	window.location="/kq/options/sign_point/person_point.do?b_searchperson=link&pid=p"+<%= pid%>;
}


   function ret()
   {
    	//parent.menuc.toggleCollapse(true);
    	window.location.href="/kq/options/sign_point/setsign_point.do?b_showpoint=link";
   }
   
    var checkflag = "false";
   function selAll()
   {
      var len=document.kqSignPointForm.elements.length;
       var i;
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.kqSignPointForm.elements[i].type=="checkbox")
            {
              document.kqSignPointForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.kqSignPointForm.elements[i].type=="checkbox")
          {
            document.kqSignPointForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  }
	
	function selectToName(){
		var selectByName = document.getElementById("selectByName");
	    kqSignPointForm.action="/kq/options/sign_point/person_point.do?b_searchperson=link&byname="+getEncodeStr(selectByName.value);
	    kqSignPointForm.submit();
	}
	
</script>
<html:form action="/kq/options/sign_point/person_point">
	<table width="80%" border="0" cellspacing="1" align="center" cellpadding="0" style="margin-tOp:5px;">
		<tr>
				<td align="left" nowrap>
					姓名&nbsp;
					 <input type="text" class="inputtext" title="可以输入'姓名','拼音简码','唯一性标识'进行查询"  id="selectByName">&nbsp;<input type="button" value="查询" class="mybutton" onclick="selectToName();">
				</td>
			
		</tr>
	</table>
	<table width="80%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap>
					<input type="checkbox" name="selbox"
						onclick="batch_select(this,'pagination.select');"
						title='<bean:message key="label.query.selectall"/>'>
					&nbsp;
				</td>
				 
	              
	              <td align="center" class="TableRow" style="white-space: nowrap">
		              	人员库
		              	&nbsp;     	
	              </td>
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="kqSignPointForm" fieldname="B0110"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="kqSignPointForm" fieldname="E0122"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="kqSignPointForm" fieldname="E01A1"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="kqSignPointForm" fieldname="A0101"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="kqSignPointForm"
			sql_str="kqSignPointForm.sql_str" table="" where_str="kqSignPointForm.cond_str"
			columns="kqSignPointForm.columns" order_by="kqSignPointForm.cond_order"
			pagerows="${kqSignPointForm.pagerows}" page_id="pagination" indexes="indexes">
			<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow">
				<%
					} else {
				%>
			
			<tr class="trDeep">
				<%
					}
								i++;
				%>
				<td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="kqSignPointForm" property="pagination.select"
						value="true" indexes="indexes" />
					&nbsp;
				</td>
				
				
				<td align="left" class="RecordRow" nowrap>
					<hrms:codetoname codeid="@@" name="element" codevalue="nbase"
						codeitem="codeitem" scope="page"  />
					&nbsp;
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					<hrms:codetoname codeid="UN" name="element" codevalue="b0110"
						codeitem="codeitem" scope="page" />
               	          			&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                 
					
				</td>
				<td align="left" class="RecordRow" nowrap>

					<hrms:codetoname codeid="UM" name="element" codevalue="e0122"
						codeitem="codeitem" scope="page" uplevel="${kqSignPointForm.uplevel}" />
					&nbsp;
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					<hrms:codetoname codeid="@K" name="element" codevalue="e01a1"
						codeitem="codeitem" scope="page" />
					&nbsp;
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="a0101" filter="false" />
					&nbsp;
				</td>
			
			</tr>
		</hrms:paginationdb>
		<tr>
			<td colspan="6">
				<table width="100%" align="center" class="RecordRowP">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="kqSignPointForm" pagerows="${kqSignPointForm.pagerows}"
								property="pagination" scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="kqSignPointForm" property="pagination"
									nameId="kqSignPointForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table width="80%" align="center">
		<tr>
			<td align="center">
				
					<!-- 
					<input type="button" name="alll"
						value='<bean:message key="label.query.selectall"/>'
						class="mybutton" onclick="selAll()">
						 -->
					<hrms:priv func_id="2703705" module_id="">
					<input type="button" name="b_select" value="添加人员" class="mybutton" onclick="search();">
					</hrms:priv>
					<hrms:priv func_id="2703706" module_id="">
					 <input type="button" name="prot_excel" value='撤销人员' class="mybutton" onclick="del();"> 
					</hrms:priv>
					<html:button styleClass="mybutton" property="b_return"
							onclick="ret();">
							<bean:message key="button.return" />
						</html:button>
				
			</td>
		</tr>
	</table>
</html:form>
