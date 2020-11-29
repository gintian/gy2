<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.hjsj.sys.VersionControl"%>


<%
	String infor=(String)session.getAttribute("infor");
	if(request.getParameter("infor")!=null)
	{			
		infor = request.getParameter("infor");
	}
	session.setAttribute("infor", infor);
%>

<hrms:themes></hrms:themes>
<script language="javascript">
function amend(infor){
    var customdesc = "";
    var name = "";
    var code = "";
    if (!getBrowseVersion() || getBrowseVersion() == 10) {//谷歌ie11
        customdesc = document.getElementsByName('customdesc')[0].value;
        name = document.getElementsByName('name')[0].value;
        code = document.getElementsByName('code')[0].value;
    } else {
        customdesc = document.getElementById("customdesc").value;
        name = document.getElementById("name").value;
        code = document.getElementById("code").value;
    }
	// var customdesc = document.getElementById("customdesc").value;
	// var name = document.getElementById("name").value;
	if ( doValidate(customdesc) && doValidate(name)) {	
		// var code = document.getElementById("code").value;
		var hashvo=new ParameterSet();
		hashvo.setValue("fieldsetdesc",name);
		hashvo.setValue("customdesc",customdesc);
		hashvo.setValue("code",code);
		hashvo.setValue("infor",infor);
		var request=new Request({method:'post',asynchronous:false,onSuccess:check_ok,functionId:'1020010122'},hashvo);
	}
}
function check_ok(outparameter){
	var msg = outparameter.getValue("msg");
	var infor = outparameter.getValue("infor");
	if(msg=='1')
    {
       amends(infor);
   }
   else
   {
     alert(KJG_ZBTX_INF33);
     return;
   }
}

/**
 *过滤特殊字符，特殊字符不允许在子集名称中出现。
 *
 */
function doValidate(value) { 
	var vkeyWords="%$\#@\!~^&*()+\"'`";  
	   if(value==null || value=="") 
	   { 
	    alert("指标集名称不能为空！"); 
	    return false; 
	   } 
	   var flag = true;
	   for(var i=0;i<value.length;i++){
		   var s = value.charAt(i);
		   if(vkeyWords.indexOf(s)!=-1) 
		   { 
			   flag = false;
		       break;
		   }
	   }
	    if(!flag)
	    	alert("指标集名称不允许包含特殊字符!"); 
	   return flag; 
  } 


function amends(infor){
    var customdesc = "";
    var name = "";
    var code = "";
    var useflag = "";
    if (!getBrowseVersion() || getBrowseVersion() == 10) {//谷歌ie11
        customdesc = document.getElementsByName('customdesc')[0].value;
        name = document.getElementsByName('name')[0].value;
        code = document.getElementsByName('code')[0].value;
        useflag = document.getElementsByName('useflag')[0].value;
    } else {
        customdesc = document.getElementById("customdesc").value;
        name = document.getElementById("name").value;
        code = document.getElementById("code").value;
        useflag = document.getElementById("useflag").value;
    }
		// var customdesc = document.getElementById("customdesc").value;
		// var name = document.getElementById("name").value;
		// var code = document.getElementById("code").value;
		// var useflag = document.getElementById("useflag").value;
		var obj = document.getElementsByName("qobj");
		var setexplain = document.getElementById("setexplain").value;
		var objlength = obj.length;
		for (var i = 0; i < objlength; i++)
		{
			if(document.getElementsByName("qobj")[i].checked)
  			{
				var qobj=i
   				break;
  			}
		}
		if(customdesc==null||customdesc==""){
			alert(KJG_ZBTX_INF34);
			return;
		}
		var mff="0";
		if (document.getElementsByName("mff")[0] != null)
			if(document.getElementsByName("mff")[0].checked){
				mff=1;
			}else{
				mff=0;
			}
		var hashvo=new ParameterSet();
		hashvo.setValue("name",getEncodeStr(name));	
		hashvo.setValue("customdesc",getEncodeStr(customdesc));	
		hashvo.setValue("qobj",qobj);	
		hashvo.setValue("code",code);
		hashvo.setValue("mff",mff);
		hashvo.setValue("infor",infor);
		hashvo.setValue("setexplain",setexplain);
		var request=new Request({method:'post',asynchronous:false,onSuccess:saveOk,functionId:'1020010109'},hashvo);
}
function saveOk(outparamters){
		//window.returnValue="aaaaa";
		//window.close();
		//传值子集前缀
		var infor = outparamters.getValue("infor")
		document.dbinitForm.action="/system/dbinit/fieldsetlist.do?b_query1=link&infor="+infor;
	    document.dbinitForm.submit();
	    var currnode=parent.frames['mil_menu'].Global.selectedItem;
		if(currnode!=null){
		if(currnode.load){
					while(currnode.childNodes.length){
						//alert(currnode.childNodes[0].uid);
						currnode.childNodes[0].remove();
					}
					currnode.load=true;
					currnode.loadChildren();
					currnode.reload(1);
				}
		}
}
function estop(e)
{
	//alert(e);
	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
	if(navigator.appName.indexOf("Microsoft")!= -1)
		return e.keyCode!=34&&e.keyCode!=39;
	else
		return e.which!=34&&e.which!=39;
}
function reback(infor){
	dbinitForm.action="/system/dbinit/fieldsetlist.do?b_query=bank&infor="+infor;
	dbinitForm.submit();
}

	//过滤输入的指标名称，将长度大于50字节的部分截取    jingq   add   2014.5.7
	function CutStr(tid){
     	var str = document.getElementById(tid).value;
        var curStr = "";
        for(var i = 0;i<str.length;i++){
        	curStr += str.charAt(i);
            if(GetStrLength(curStr)>50){
            	document.getElementById(tid).value = str.substring(0,i);
                return;
            }
        }
     }
     //求输入的字符串长度
	function GetStrLength(str){
		var slength = 0;
	    for(var i = 0;i<str.length;i++){
	    	if(str.charCodeAt(i) >255){
	        	slength += 2;
	        } else {
	        	slength += 1;
	        }
	    }
	    return slength;
	}
</script>

<html:form action="/system/dbinit/fieldsetlist">
	<html:hidden name="dbinitForm" property="useflag"/>
	<table width="400" border="0" cellpadding="1" cellspacing="0"
		align="center">
		<tr height="20">
			<!-- td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
				
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="500"></td> -->
			
			<td align="left" colspan="4" class="TableRow">
				<bean:message key="kjg.title.xgzbj"/>
			</td>
		</tr>
		<tr>
			<td colspan="4" class="framestyle3" style="padding-right:20px;">
				<table border="0" cellpmoding="0" cellspacing="5"
					class="DetailTable" cellpadding="0">
					<tr>
						<td align="right" nowrap valign="middle">
			<bean:message key="kjg.title.zbjdh"/>
						</td>
						<td align="left" nowrap valign="middle">
						<html:text readonly="true" property="code" name="dbinitForm" maxlength="3" disabled="true" styleClass="text4" style="width:200px;"/>
							
						</td>
					</tr>
					<tr>
						<td align="right" nowrap valign="middle">
							<bean:message key="kjg.title.frontname"/>
						</td>
						<td align="left" nowrap valign="middle">
							<html:text styleId="cname" property="name" onkeypress="return estop(event);" name="dbinitForm" onkeyup="CutStr('cname');" styleClass="text4" style="width:200px;"/> <!-- 限制指标集名称长度，防止名称过长无法存入数据库  jingq add 2014.5.6 -->
						</td>
					</tr>
					<tr>
						<td align="right" nowrap valign="middle">
							 <bean:message key="kjg.title.backname"/>
						</td>
						<td align="left" nowrap valign="middle">
							<html:text styleId="cdesc" property="customdesc" onkeypress="return estop(event);" name="dbinitForm" onkeyup="CutStr('cdesc');" styleClass="text4" style="width:200px;"/> 
						</td>
					</tr>
					<tr>
						<td align="right" nowrap valign="middle">
							 <bean:message key="kjg.title.type"/>
						</td>
						<td>
							<logic:equal name="dbinitForm" property="useflag" value="1">
								<html:radio name="dbinitForm" property="qobj" value="0" disabled="true">
									<bean:message key="kjg.title.ybzj"/>
								</html:radio>
								<html:radio name="dbinitForm" property="qobj" value="1" disabled="true">
									<bean:message key="kjg.title.aybh"/>
								</html:radio>
								<html:radio name="dbinitForm" property="qobj" value="2" disabled="true">
									<bean:message key="kjg.title.anbh"/>
								</html:radio>
							</logic:equal>
							<logic:notEqual name="dbinitForm" property="useflag" value="1">
								<html:radio name="dbinitForm" property="qobj" value="0">
									<bean:message key="kjg.title.ybzj"/>
								</html:radio>
								<html:radio name="dbinitForm" property="qobj" value="1">
									<bean:message key="kjg.title.aybh"/>
								</html:radio>
								<html:radio name="dbinitForm" property="qobj" value="2">
									<bean:message key="kjg.title.anbh"/>
								</html:radio>
							</logic:notEqual>
						</td>
					</tr>
		    <%
			VersionControl vc = new VersionControl();
			if(vc.searchFunctionId("03040110")){ 
			%>
				<%
				if(infor.equals("A")){
				%>
					<tr>
						<td align="right" nowrap valign="right">
						</td>
						<td align="left" nowrap valign="left">
							<logic:notEqual name="dbinitForm" property="multimedia_file_flag" value="1">
								<input type="checkbox" name="mff" value="true">
							</logic:notEqual>
							<logic:equal name="dbinitForm" property="multimedia_file_flag" value="1">
								<input type="checkbox" name="mff" value="true" checked>
							</logic:equal>
							<bean:message key="kjg.title.support.multimedia"/>&nbsp;
						</td>
					</tr>
				<%} %>			
		  	<%}
		  	%>   
		  			<!-- 添加指标集解释 输入项（setexplain） guodd 2018-04-24 -->
		  			<tr>
		  				<td align="right" nowrap valign="top">
							 <bean:message key="kjg.title.zbjexplain"/>
						</td>
						<td align="left" nowrap valign="middle">
							 <html:textarea styleId="setexplain" property="setexplain" name="dbinitForm" style="width:300px;height:100px"></html:textarea>
						</td>
		  			</tr>
					<tr>
						<td height="10"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table align="center">
		<tr class="list3">
						<td align="center" colspan="4" nowrap height="35px;">
						<input type="button" class="mybutton" value="<bean:message key="button.save" />" onClick='amend("${sessionScope.infor}")' />
							<input type='button' 
								value='<bean:message key="button.return"/>'
								class="mybutton" onclick='reback("${sessionScope.infor}");' >
						</td>
					</tr>
	</table>
</html:form>
