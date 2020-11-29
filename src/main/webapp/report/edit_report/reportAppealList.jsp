<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page
	import="com.hjsj.hrms.actionform.report.edit_report.EditReportForm,
	com.hjsj.hrms.utils.PubFunc"%>
	<script language="JavaScript" src="/report/edit_report/editReport.js"></script>
<%
	EditReportForm editReportForm = (EditReportForm) session
			.getAttribute("editReportForm");
    //add by wangchaoqun on 2014-9-26 begin
    String encryptParam = PubFunc.encrypt("tabid=" + request.getParameter("tabid") + "&status=" + request.getParameter("status"));
    String encryptParam1 = "unitcode=" + editReportForm.getAppealUnitCode() + "&unitname=" +
    		editReportForm.getUnitName() + "&existunicode=" + editReportForm.getExistunicode();
    //add by wangchaoqun on 2014-9-26 end
%>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language='javascript'>
var isApproveflag = "${editReportForm.isApproveflag}";
var username = "${editReportForm.username}";
var unitcode1 = "${editReportForm.unitcode1}";
var obj1 = "${editReportForm.obj1}";
var tabid = "${editReportForm.tabid}";
var operateObject = "${editReportForm.operateObject}";
function showPage(code,status)
{
	var operateObject="${editReportForm.operateObject}";
	if(operateObject=='1')
		editReportForm.action="/report/edit_report/reportSettree.do?b_query=link&code="+code+"&operateObject=1&status="+status;
	else
		editReportForm.action="/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code=${editReportForm.selfUnitcode}&operateObject=2&tabid="+code;	
	editReportForm.submit();
}

function returnInfo(outparamters)
{
	var info=outparamters.getValue("info");
	var operateObject="${editReportForm.operateObject}";
	alert(info);
	if(operateObject=='1'){
	//	editReportForm.action="/report/edit_report/reportSettree.do?b_query=link&code=<%=(request.getParameter("tabid"))%>&operateObject=1&status=<%=(request.getParameter("status"))%>";
	//	editReportForm.submit();
	
	var href = ""+parent.mil_menu.document.location;
	if(href.indexOf(".jsp?")<0){
	
	href = href+"?selectuid=<%=(request.getParameter("tabid"))%>";
	}else{
	href = href.substring(0,href.indexOf(".jsp?")+5)+"selectuid=<%=(request.getParameter("tabid"))%>";
	}
	
	parent.mil_menu.document.location=href;
	}else{
		editReportForm.action="/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code=${editReportForm.selfUnitcode}&operateObject=2";
	//parent.mil_menu.document.location.reload();
	editReportForm.submit();
	}

}


//报表上报
function appeal_2(outparamters)
{
	var returninfo=outparamters.getValue("returnInfo");
	var tabid_str=outparamters.getValue("tabid_str");
	
	var waitInfo=eval("wait");
	waitInfo.style.display="none";
	
	if(returninfo!="success")
	{
			if(returninfo=='failed1')
			{
				var errorInfo=getDecodeStr(outparamters.getValue("errorInfo"));
				alert(errorInfo);
			}
			else if(returninfo=='failed2')
			{
				var errorInfo=getDecodeStr(outparamters.getValue("errorInfo"));
				alert("\r\n校验错误,不予上报!\r\n"+errorInfo);
			}
			else
				alert(REPORT_INFO31);
			<logic:equal name="editReportForm" property="operateObject" value="1"> 
	<logic:notEqual name="editReportForm" property="existunicode" value="0">
	document.getElementsByName("b_add")[0].disabled="";
	</logic:notEqual>
	<logic:notEqual  name="editReportForm" property="sortId" value="all" >
	document.getElementsByName("b_add2")[0].disabled="";
     </logic:notEqual>
	</logic:equal>
	<logic:equal name="editReportForm" property="operateObject" value="2">   
	<logic:equal name="editReportForm" property="unitcode" value="${editReportForm.selfUnitcode}">   
	document.getElementsByName("b_add")[0].disabled="";
             <logic:notEqual  name="editReportForm" property="sortId" value="all" >
              document.getElementsByName("b_add2")[0].disabled="";
            </logic:notEqual>
	</logic:equal>
</logic:equal>	
			return; 
	}
	if(tabid_str.length==0)
	{
		alert(REPORT_INFO11+"！");
		<logic:equal name="editReportForm" property="operateObject" value="1"> 
	<logic:notEqual name="editReportForm" property="existunicode" value="0">
	document.getElementsByName("b_add")[0].disabled="";
	</logic:notEqual>
	<logic:notEqual  name="editReportForm" property="sortId" value="all" >
	document.getElementsByName("b_add2")[0].disabled="";
     </logic:notEqual>
	</logic:equal>
	<logic:equal name="editReportForm" property="operateObject" value="2">   
	<logic:equal name="editReportForm" property="unitcode" value="${editReportForm.selfUnitcode}">   
	document.getElementsByName("b_add")[0].disabled="";
             <logic:notEqual  name="editReportForm" property="sortId" value="all" >
              document.getElementsByName("b_add2")[0].disabled="";
            </logic:notEqual>
	</logic:equal>
</logic:equal>
		return;
	}
	
	var appealUnit=eval("document.editReportForm.appealUnitCode");
	//alert(appealUnit.value);
	var hashvo=new ParameterSet();
	hashvo.setValue("sortId","${editReportForm.sortId}");
	hashvo.setValue("tabids",tabid_str);
	hashvo.setValue("operateObject","${editReportForm.operateObject}");
	hashvo.setValue("appealUnitcode",appealUnit.value);
	hashvo.setValue("changStatus","1");
	var In_paramters="flag=1"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03020000003'},hashvo);			
}


//进行报表校验
function appeal_1()
{
	var tabid_str="";
	for(var i=0;i<document.editReportForm.elements.length;i++)
	{
		if(document.editReportForm.elements[i].type=="checkbox"&&document.editReportForm.elements[i].name!='selbox')
		{
			if(document.editReportForm.elements[i].checked==true)
			{
				tabid_str+="/"+document.editReportForm.elements[i].value;
			}
		}
	}
	if(tabid_str.length==0)
	{
		alert(REPORT_INFO12+"！");
		<logic:equal name="editReportForm" property="operateObject" value="1"> 
			<logic:notEqual name="editReportForm" property="existunicode" value="0">
				document.getElementsByName("b_add")[0].disabled="";
			</logic:notEqual>
			<logic:notEqual  name="editReportForm" property="sortId" value="all" >
				document.getElementsByName("b_add2")[0].disabled="";
			</logic:notEqual>
		</logic:equal>
		<logic:equal name="editReportForm" property="operateObject" value="2">   
			<logic:equal name="editReportForm" property="unitcode" value="${editReportForm.selfUnitcode}">   
				document.getElementsByName("b_add")[0].disabled="";
				<logic:notEqual  name="editReportForm" property="sortId" value="all" >
					document.getElementsByName("b_add2")[0].disabled="";
				</logic:notEqual>
			</logic:equal>
		</logic:equal>	
		return;
	}
	if(!confirm(REPORT_INFO32+"！"))
		return;
	var hashvo=new ParameterSet();
	<logic:equal name="editReportForm" property="operateObject" value="1"> 
		<logic:notEqual name="editReportForm" property="existunicode" value="0">
			document.getElementsByName("b_add")[0].disabled="true";
		</logic:notEqual>
		<logic:notEqual  name="editReportForm" property="sortId" value="all" >
			document.getElementsByName("b_add2")[0].disabled="true";
		</logic:notEqual>
	</logic:equal>
	<logic:equal name="editReportForm" property="operateObject" value="2">   
		<logic:equal name="editReportForm" property="unitcode" value="${editReportForm.selfUnitcode}">   
			document.getElementsByName("b_add")[0].disabled="true";
			<logic:notEqual  name="editReportForm" property="sortId" value="all" >
				document.getElementsByName("b_add2")[0].disabled="true";
			</logic:notEqual>
		</logic:equal>
	</logic:equal>
	
	var waitInfo=eval("wait");
	waitInfo.style.display="block";
	hashvo.setValue("tabids",tabid_str.substring(1));
	hashvo.setValue("operateObject","${editReportForm.operateObject}");
	hashvo.setValue("unitcode","${editReportForm.unitcode}");
	
	var appealUnit=eval("document.editReportForm.appealUnitCode");
	hashvo.setValue("appealUnitcode",appealUnit.value);
	
	var In_paramters="flag=1"; 
	var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:appeal_2,functionId:'03020000009'},hashvo);			
}


		function change()
		{
			editReportForm.action="/report/edit_report/editReport.do?b_searchAppeal=appeal&encryptParam=<%=encryptParam %>";
			editReportForm.submit();
		}

		var returnValue;
		function appeal_3()
		{	  
	  		var info='';
	  		var tabid_str = "";
			for(var i=0;i<document.editReportForm.elements.length;i++)
			{
				if(document.editReportForm.elements[i].type=="checkbox"&&document.editReportForm.elements[i].name!='selbox')
				{
					if(document.editReportForm.elements[i].checked==true)
					{
						tabid_str+="/"+document.editReportForm.elements[i].value;
					}
				}
			}
			if(!tabid_str){
				alert("请选择报表！")
				return;
			}
	 		username=getDecodeStr(username);
	  		var user = $URL.encode(getEncodeStr(username));
			var thecodeurl="/report/report_isApprove/reportIsApprove.do?b_isApprove=link&username="+user;	
			var config = {
					width:420,
					height:300,
					title:'',
					theurl:thecodeurl,
					id:'appealWin'
				}
			openWin(config);
			Ext.getCmp("appealWin").addListener('close',function(){
				if(returnValue){
					if(confirm("确定要报批吗？")){
		    			var hashvo=new ParameterSet();
						hashvo.setValue("mainbody_id",returnValue);
						hashvo.setValue("tabid",tabid_str);
						hashvo.setValue("unitcode1",unitcode1);
						var request=new Request({method:'post',asynchronous:false,onSuccess:appeal_5,functionId:'03020000098'},hashvo);	
		    		}
				}
			});
	  }
		function appeal_5(){
			if(operateObject=="2"){  
				var href="/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code="+unitcode1+"&tabid="+tabid;
				window.location=href;
	  		}else if(operateObject=="1"){
  				var href = ""+parent.mil_menu.document.location;
				if(href.indexOf(".jsp?")<0){
					href = href+"?selectuid="+tabid;
				}else{
					href = href.substring(0,href.indexOf(".jsp?")+5)+"selectuid="+tabid;
				}
				parent.mil_menu.document.location=href;	
	  		}
	  }
</script>
<hrms:themes></hrms:themes>
<html:form action="/report/edit_report/editReport" style="margin-top:0px;">	
<table width="75%" height='20' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">

   	  <thead>
	     <tr id='sss' style="display=block;">
		      <td colspan="5" class="RecordRow">
		      <div> 
		      <bean:message key="report.reportlist.reportsort"/>
		      <hrms:optioncollection name="editReportForm" property="tsortList" collection="list" />
              <html:select name="editReportForm" property="sortId" size="1" onchange="change();" style="vertical-align: middle;">
	             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	       	  </html:select>
	       	  </div>
		        </td>           
		 </tr>
		
		
			  <tr id='unit' style="display=block;">
			      <td colspan="5" class="RecordRow">
			   	  <div> 
			     	 <bean:message key="report.appealUnit"/>
			       <hrms:optioncollection name="editReportForm" property="subUnitList" collection="list" />
			             <html:select name="editReportForm" property="appealUnitCode" size="1" onchange="change();" style="vertical-align: middle;" >
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				       	 </html:select>
			      </div>
			      </td>           
			 </tr>
		
		 
           <tr>
            <td align="center" class="TableRow" nowrap width="10%">
				<input type="checkbox" name="selbox" onclick="batch_select(this,'tabid');" title='<bean:message key="label.query.selectall"/>'>
	    	</td>         
            <td align="center" class="TableRow" nowrap width="20%">
				<bean:message key="report.reportlist.reportid"/>&nbsp;
	   		 </td>
            <td align="center" class="TableRow" nowrap width="60%">
				<bean:message key="report.reportlist.reportname"/>&nbsp;
	  		  </td>
            <td align="center" class="TableRow" nowrap width="60%">
				&nbsp;&nbsp;<bean:message key="column.warn.valid"/>&nbsp;&nbsp;
	  		  </td>
            <td align="center" class="TableRow" nowrap width="60%">
				<bean:message key="label.consult"/>&nbsp;
	  		  </td>                		        	        	        
           </tr>
   	  </thead>
          
         <% 
         int i=0; 
         
         %> 
        <logic:iterate id="element" name="editReportForm" property="appealInfoList"  > 
        
           
          <tr class="<%=(i%2==0?"trShallow":"trDeep")%>">
            
            <td align="center" class="RecordRow" nowrap>
            <logic:equal name="element" property="app" value="1">
            	<logic:equal name="element" property="status" value="-1">
   					<input type="checkbox" name="tabid" value="<bean:write name="element" property="tabid" />">
   				</logic:equal>
   				<logic:equal name="element" property="status" value="0">
   					<input type="checkbox" name="tabid" value="<bean:write name="element" property="tabid" />">
   				</logic:equal>
   				<logic:equal name="element" property="status" value="2">
   					<input type="checkbox" name="tabid" value="<bean:write name="element" property="tabid" />">
   				</logic:equal>
   				<logic:equal name="element" property="status" value="4">
   					<input type="checkbox" name="tabid" value="<bean:write name="element" property="tabid" />">
   				</logic:equal>
   			</logic:equal>
	   		</td>            
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="tabid" />&nbsp;
	    	</td>
         
            <td align="left" class="RecordRow" wrap>
                   <bean:write name="element" property="name" />&nbsp;
            </td>
            <td align="left" class="RecordRow" wrap>
                   <bean:write name="element" property="statusname" />&nbsp;
            </td>
            <td align="center" class="RecordRow" nowrap> <a href="javascript:showPage('<bean:write name="element" property="tabid" />','<bean:write name="element" property="status" />')"><img src="../../images/edit.gif" width="11" height="17" border=0></a> 
            </td>      
          </tr>
			<% i++; %>
          </logic:iterate>
</table>

<logic:equal name="editReportForm" property="operateObject" value="1">   
<table  width="50%" align="center" style="margin-top: 1px;">
	 <tr>
	 	<%if(editReportForm.getIsApproveflag().equals("2")){ %>
	 		<td align="center"> 
            	<logic:notEqual name="editReportForm" property="existunicode" value="0">
         	  <input type="button" name="b_add" value="报批" class="mybutton" onClick="appeal_3();">
              </logic:notEqual>
            </td>
	 	<%}else{ %>
	 		 <td align="center"> 
            	<logic:notEqual name="editReportForm" property="existunicode" value="0">
         	  <input type="button" name="b_add" value="<bean:message key="reportManager.appeal"/>" class="mybutton" style="margin-left: -2px;" onClick="appeal_1();">
              </logic:notEqual>
              <logic:notEqual  name="editReportForm" property="sortId" value="all" >
              <input type="button" name="b_add2" value="<bean:message key="reportManager.executeAppealInfo"/>" class="mybutton" style="margin-left: -2px;" onClick="upDisk()">	
              </logic:notEqual>
            </td>
	 	<%} %>

          </tr>   
</table>   
</logic:equal>

<logic:equal name="editReportForm" property="operateObject" value="2">   
	<logic:equal name="editReportForm" property="unitcode" value="${editReportForm.selfUnitcode}">   
<table  width="50%" align="center" style="margin-top: 1px;">

          <tr>
          	 	<%if(editReportForm.getIsApproveflag().equals("2")){ %>
	 		<td align="center"> 
            	<logic:notEqual name="editReportForm" property="existunicode" value="0">
         	  <input type="button" name="b_add" value="报批" class="mybutton" onClick="appeal_3();">
              </logic:notEqual>
            </td>
	 	<%}else{ %>
            <td align="center"> 

         	  <input type="button" name="b_add" value="<bean:message key="reportManager.appeal"/>" class="mybutton" style="margin-left: -2px;" onClick="appeal_1();">
             <logic:notEqual  name="editReportForm" property="sortId" value="all" >
              <input type="button" name="b_add2" value="<bean:message key="reportManager.executeAppealInfo"/>" class="mybutton" style="margin-left: -2px;" onClick="upDisk()">	
            </logic:notEqual>
            </td>
        <%} %>
          </tr>          
</table>
	</logic:equal>
</logic:equal>




<div id='wait' style='position:absolute;top:180;left:230;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					<bean:message key="edit_report.info1"/>......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
<script>

	var unitsize=${editReportForm.subUnitSize};
	if(unitsize==1)
	{
		var aa=eval('unit')
		aa.style.display="none"; 
	}
	var aaa=${editReportForm.isApproveflag};
	if(aaa==2)
	{
		var aa=eval('unit')
		aa.style.display="none"; 
		var bb=eval('sss')
		bb.style.display="none"; 
	}

    function upDisk() {
        var tabid_str="";
		for(var i = 0;i < document.editReportForm.elements.length; i++)
		{
			if(document.editReportForm.elements[i].type=="checkbox"&&document.editReportForm.elements[i].name!='selbox')
			{
				if(document.editReportForm.elements[i].checked==true)
				{
					tabid_str+="/"+document.editReportForm.elements[i].value;
				}
			}
		}	
		var unitName = "";
		for(var i = 0;i < document.editReportForm.appealUnitCode.options.length; i++)
		{
			if(document.editReportForm.appealUnitCode.options[i].selected==true)
			{
			    unitName = document.editReportForm.appealUnitCode.options[i].text;
			}
		}	
    	if(tabid_str.length==0)
    	{
	    	alert(REPORT_INFO12+"！");
	    			<logic:equal name="editReportForm" property="operateObject" value="1"> 
	<logic:notEqual name="editReportForm" property="existunicode" value="0">
	document.getElementsByName("b_add")[0].disabled="";
	</logic:notEqual>
	<logic:notEqual  name="editReportForm" property="sortId" value="all" >
	document.getElementsByName("b_add2")[0].disabled="";
     </logic:notEqual>
	</logic:equal>
	<logic:equal name="editReportForm" property="operateObject" value="2">   
	<logic:equal name="editReportForm" property="unitcode" value="${editReportForm.selfUnitcode}">   
	document.getElementsByName("b_add")[0].disabled="";
             <logic:notEqual  name="editReportForm" property="sortId" value="all" >
              document.getElementsByName("b_add2")[0].disabled="";
            </logic:notEqual>
	</logic:equal>
</logic:equal>	
	    	return;
    	}
    	
    	//alert("生成报盘的报表="+tabid_str+" 当前用户对应的填表单位编码=" + unitcode+" 当前用户名="+unitName);
    	var width=430;
    	var height=210;
    	var iTop = (window.screen.availHeight-30-height)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-10-width)/2; //获得窗口的水平位置;
        target_url="/report/edit_report/dialog/upDiskDialog.do?tabids=" + tabid_str + "&unitcode=" + unitcode + "&unitname=" + $URL.encode(unitName)+"&existunicode=${editReportForm.existunicode}";
    	newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+iTop+',left='+iLeft+',width='+width+',height='+height);
    }
    var a = eval("document.editReportForm.appealUnitCode");
    var unitcode=a.value;
</script>