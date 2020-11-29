<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes /> <!-- 7.0css -->
<script type="text/javascript" src="/kq/kq.js"></script>
<%@ page import="com.hjsj.hrms.actionform.kq.app_check_in.AppForm" %>
<%@ page import="java.util.ArrayList" %>
<%
	int i = 0;
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
   //批量批准 
      function batchapproval()
   {
   	  var len=document.appForm.elements.length;
       var uu;
       for (i=0;i<len;i++)
       {
           if (document.appForm.elements[i].type=="checkbox")
            {
              if(document.appForm.elements[i].checked==true)
              {
                uu="dd";
               
               }
            }
        }
       if(uu=="dd")
       {
          if(confirm("您确定要批准所选人员本考勤期间的加班申请吗？"))
         {
	       appForm.action="/kq/app_check_in/sumapproval.do?b_batchapproval=link";
           appForm.submit();
          }
       }else
       {
         alert("请先选择人员！");
         return false;
       }
   
   }
      //审批一人
     function approval(i,nbase,a0100){
         var len=document.appForm.elements.length;
         for (j=0;j<len;j++)
         {
             if (document.appForm.elements[j].type=="checkbox"&&j==i)
              {
               document.appForm.elements[i].checked=true;
              }
          }
         if(confirm("您确定要批准所选人员本考勤期间的加班申请吗？"))
         {
	       appForm.action="/kq/app_check_in/sumapproval.do?b_batchapproval=link";
           appForm.submit();
          }
          }
   function change()
   {
      appForm.action="/kq/app_check_in/sumapproval.do?b_query=link";
      appForm.submit();
   }
   function toallapp(table)
   {
    	parent.menuc.toggleCollapse(true);
    	window.location.href="/kq/app_check_in/all_app_data.do?b_search=link&root=0&jump=1";
   }
   
    var checkflag = "false";
   function selAll()
   {
      var len=document.appForm.elements.length;
       var i;
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.appForm.elements[i].type=="checkbox")
            {
              document.appForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.appForm.elements[i].type=="checkbox")
          {
            document.appForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  }
	//导出excel
  	function export_excel(){
  		var hashvo=new ParameterSet();
		hashvo.setValue("sql","${appForm.sql_str}");
		hashvo.setValue("addtypenamelist","${appForm.addtypenamelist}");
		hashvo.setValue("addtypeidlist","${appForm.addtypeidlist}");
		var request = new Request({method:'post',onSuccess:showExportInfo,functionId:'1510011004'},hashvo);
    }

  	function showExportInfo(outparamters){
  		if(outparamters){
  			var name=outparamters.getValue("name");
  				window.location.target="_blank";
  				window.location.href="/servlet/vfsservlet?fileid="+name+"&fromjavafolder=true";
  		}
  	}
	function selectToName(){
		var selectByName = document.getElementById("selectByName");
		var select_type = document.getElementById("select_type").value;
	    appForm.action="/kq/app_check_in/sumapproval.do?b_query=link&byname="+getEncodeStr(selectByName.value) + "&select_type=" + select_type;
	    appForm.submit();
	}
	//查看 
	function openapproval(nbase,a0100,b0110)
	{
   		var target_url="/kq/app_check_in/sumapproval.do?b_showapproval=link&a0100="+a0100+"&nbase="+nbase+"&b0110="+b0110;
        return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:640px; dialogHeight:440px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	}
</script>
<html:form action="/kq/app_check_in/sumapproval">
	<br>
	<% AppForm appForm = (AppForm)request.getSession().getAttribute("appForm");
	   ArrayList addtypenamelist = appForm.getAddtypenamelist();
	   ArrayList addtypeidlist = appForm.getAddtypeidlist();
	 %>
	<table width="80%" border="0" cellspacing="1" align="center"
		cellpadding="0">
		<tr>
				<td align="left" nowrap>
					
					<hrms:optioncollection name="appForm" property="dblist"
						collection="list" />
					<html:select name="appForm" property="dbpre" size="1"
						onchange="change();">
						<html:options collection="list" property="dataValue"
							labelProperty="dataName" />
					</html:select>
					<html:select name="appForm" property="select_type"  size="1">
		            	<html:option value="0">姓名</html:option>                      
		           	 	<html:option value="1">工号</html:option>
		            	<html:option value="2">考勤卡号</html:option>
		          	</html:select>
					 <input type="text"  id="selectByName">&nbsp;<input type="button" value="查询" class="mybutton" onclick="selectToName();">
					 &nbsp;&nbsp;&nbsp;单位：小时 &nbsp;&nbsp;
							<hrms:kqcourse />
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
		              <bean:message key="label.view"/>      	
	              </td>
	              <td align="center" class="TableRow" style="white-space: nowrap">
		              <bean:message key="jx.param.objectdegree2"/>      	
	              </td>
	              <td align="center" class="TableRow" style="white-space: nowrap">
		              	人员库
		              	&nbsp;     	
	              </td>
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="B0110"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="E0122"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="E01A1"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="A0101"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="gno"
						fielditem="fielditem" />
					工号
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="cardno"
						fielditem="fielditem" />
					考勤卡号
					&nbsp;
				</td>
				 <td align="center" class="TableRow" style="white-space: nowrap">
		             加班总时长 &nbsp;     	
	              </td>
	               <td align="center" class="TableRow" style="white-space: nowrap">
		            已批时长 &nbsp;     	
	              </td>
	               <td align="center" class="TableRow" style="white-space: nowrap">
		           未批时长 &nbsp;      	
	              </td>
	              
	              <%  for(int i2 = 0 ; i2 < addtypenamelist.size() ;i2++){
	            	  %>
	             <td align="center" class="TableRow" style="white-space: nowrap">
		             <%= addtypenamelist.get(i2)%>&nbsp;  	
	              </td>
	           <% }  %>
	        
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="appForm"
			sql_str="appForm.sql_str" table="" where_str="appForm.cond_str"
			columns="${appForm.columns}" order_by="appForm.cond_order"
			pagerows="${appForm.pagerows}" page_id="pagination" indexes="indexes">
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
					<hrms:checkmultibox name="appForm" property="pagination.select"
						value="true" indexes="indexes" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<a href="###" onclick="openapproval('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>','<bean:write name="element" property="b0110" filter="true"/>');">
               	       <img src="/images/view.gif" border="0">
					 </a>
				</td>
				<td align="center" class="RecordRow" nowrap>
					<a href="###" onclick="approval('<%= i+4 %>','<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>')">
	                    		  <img src="/images/edit.gif" border="0">
	                </a>
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
						codeitem="codeitem" scope="page" uplevel="${appForm.uplevel}" />
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
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="gno" filter="false" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="cardno" filter="false" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="q1" filter="false" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="q2" filter="false" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="q3" filter="false" />
					&nbsp;
				</td>
				  
	              <%  for(int i3 = 0 ; i3 < addtypeidlist.size() ;i3++){
	            	  String nid = "q"+addtypeidlist.get(i3);
	            	  %>
	             <td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="<%= nid %>" filter="false" />
					&nbsp;
				</td>
	           <% }  %>
			
			</tr>
		</hrms:paginationdb>
		<tr>
			<td colspan="<%= 13+addtypeidlist.size() %>">
				<table width="100%" align="center" class="RecordRowTop0">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="appForm" pagerows="${appForm.pagerows}"
								property="pagination" scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="appForm" property="pagination"
									nameId="appForm" scope="page">
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
					<input type="button" name="b_select" value="批量审批" class="mybutton" onclick="batchapproval();">
					 <input type="button" name="prot_excel" value='导出EXCEL' class="mybutton" onclick="export_excel();"> 
					<html:button styleClass="mybutton" property="b_return"
							onclick="toallapp('Q11');">
							<bean:message key="button.return" />
						</html:button>
				
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
hide_nbase_select('dbpre');
</script>