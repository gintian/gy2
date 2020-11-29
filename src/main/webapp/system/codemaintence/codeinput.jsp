<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.codemaintence.CodeMaintenceForm"%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<%
	//【60969】hotfixes系统管理：代码体系点击【导入】，弹窗空白，无法导入代码
	CodeMaintenceForm codeMaintenceForm = (CodeMaintenceForm) session.getAttribute("codeMaintenceForm");
	codeMaintenceForm.setUpflag("0");
%>
<script type="text/javascript" language="javascript">
<!--
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	function inputcode_ok(outparamters){
	
	}
	function inputcode(){
      var hashvo=new ParameterSet();
      var file=codeMaintenceForm.file;
      hashvo.setValue("upfile",file);
      var request=new Request ({asynchronous:false,onSuccess:inputcode_ok,functionId:'1010050011'},hashvo);
  }
  	function updatecodeitem(){
  	  	codeMaintenceForm.upflag.value="1";
  	 	//jindu();
  	 	//alert("您选择了‘是’，将覆盖已有数据！");
  	 	alert("数据导入成功!");
  	 	// window.returnValue='seccess';
        codeMaintenceForm.action="/system/codemaintence/codeinput.do?b_input=link";
        codeMaintenceForm.submit();
        setTimeout(function(){
            parent.parent.return_vo = 'seccess';
            winClose();
		},1000);
            // window.close();

  	}
  	function addcodeitem(){
  		codeMaintenceForm.upflag.value="0";
  		//jindu();
  		//alert("您选择了‘否’，导入数据失败！");
  		alert("数据导入失败!");
  		//window.close();
		//open('/system/codemaintence/codetree.do?b_query=link','il_body');
  	}
	function getAbsolutpath(){
	var filepath=codeMaintenceForm.file.value;
	var temp=filepath.split(".");
	var filetype=temp[temp.length-1];
	var ftempname=filepath.split("\\");
	var filename=ftempname[ftempname.length-1];
	var filen=filename.split(".");
	var finalname=filen[0];
	var cflag=codeMaintenceForm.cflag.value;

	if(filetype.toLowerCase()!="cod"/*&&filetype.toLowerCase()!="txt"*/){
	   alert('<bean:message key="cocemaintence.code.selfiletype"/>');
 	   return;
	}  
	document.getElementsByName("upflag")[0].value="";
	codeMaintenceForm.filevalue.value=filepath;
	codeMaintenceForm.action="/system/codemaintence/codeinput.do?b_input=link";
	codeMaintenceForm.submit();
	}
	function jindu(){
	var waitInfo=eval("wait");
	waitInfo.style.display="block";
	}
	function winClose() {
		if(parent.parent.Ext.getCmp('inputcode')){
            parent.parent.Ext.getCmp('inputcode').close();
		}
    }
	<%
		String flag = request.getParameter("flag");
		flag=flag==null?"":flag;
		if(!flag.equals("open")){
	%>
	<logic:equal  value="success" name="codeMaintenceForm" property="filevalue">
		alert("导入数据已完成！");
		// window.returnValue='seccess';
		// window.close();
		parent.parent.return_vo = 'seccess';
		winClose();
	</logic:equal>
	<%
		codeMaintenceForm.setFilevalue("");
		}
	%>
//-->
</script>
<html:form action="/system/codemaintence/codeinput" enctype="multipart/form-data">
	<div id='wait' style='position:absolute;top:10;left:50;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					<bean:message key="codemaintence.code.inputmessage"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style"  direction="right" width="180" scrollamount="5" scrolldelay="10">
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
	
	<table width="460" border="0" cellspacing="0" align="center" cellpadding="0">
	<logic:notEqual  value="exist" name="codeMaintenceForm" property="filevalue">
	<%--
		<tr>
			<td class="TableRow_lrt" width="100px" align="left">
							<bean:message key="codemaintence.code.input" />
							&nbsp;
						</td>
		</tr>
	--%>
		<tr>
			<td align="center" class="framestyle" nowrap height="100px" width="100px">
				<input type="file" name="file" size='35' class="textborder common_border_color" style="width:400px;margin-left:10px;"/>
			</td>
		</tr>
		<tr>
		<TD align="center" height="35px;">
				<html:button styleClass="mybutton" property="br_return" onclick="getAbsolutpath();">
					<bean:message key="lable.fileup" />
				</html:button>&nbsp;
				<%--<html:button styleClass="mybutton" property="br_return" onclick="window.close();">--%>
					<%--<bean:message key="button.close" />--%>
				<%--</html:button>--%>
			<html:button styleClass="mybutton" property="br_return" onclick="winClose();">
				<bean:message key="button.close" />
			</html:button>
		</TD>
		</tr>
		</logic:notEqual>
	   <logic:equal value="exist" name="codeMaintenceForm" property="filevalue">
		<tr>
			<td>
				<TABLE border="0" cellspacing="0" cellpadding="0" align="center">
					<tr>
						<td>
							<bean:message key="codemaintence.code.iscovered"/>
						</td>
					</tr>
					<tr>
						<td align="center" height="35px;">
							<html:button styleClass="mybutton" property="b_input" onclick="updatecodeitem();">
								&nbsp;<bean:message key="datestyle.yes"/>&nbsp;
							</html:button>
							&nbsp;
							<hrms:submit styleClass="mybutton" property="b_input" onclick="addcodeitem();">
								&nbsp;<bean:message key="datesytle.no"/>&nbsp;
							</hrms:submit>
						</td>
					</tr>
				</TABLE>
			</td>
		</tr>
		</logic:equal>
		<html:hidden name="codeMaintenceForm" property="upflag" />
		<html:hidden name="codeMaintenceForm" property="filevalue"/>
	</table>
	<html:hidden name="codeMaintenceForm" property="cflag" />
</html:form>
