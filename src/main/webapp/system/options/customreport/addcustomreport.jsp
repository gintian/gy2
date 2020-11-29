<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes></hrms:themes>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<script language="javascript">
	function validateValue(textbox) { 
		var   IllegalString ="\`~!#$%^&*()+{}|\\:\"<>?-=/,;\@%'"; 
        var   textboxvalue=   textbox.value; 
		var str = '';
		for (i = 0; i < textboxvalue.length; i++)  {
	        var   s   =   textbox.value.charAt(i);

	        if(IllegalString.indexOf(s)>=0) {
                str = s;
	        	s   =   textboxvalue.substring(0,i); 
	            textbox.value   =   s;

                break;
	        }
        }
        return str;
    }
     
	function checkfilName() {
		var filetitle = document.getElementsByName("templateFile")[0].value;
		if (filetitle == "") {
			return true;
		}
		var index = filetitle.lastIndexOf(".");
		var filesname = filetitle.substring(index+1,filetitle.length).toLocaleLowerCase();
		if(filesname=="html" || filesname=="htm" || filesname=="xls" || filesname=="xlsx"|| filesname=="xlt" || filesname=="xltx" || filesname=="mht") {
			return true;
		} else{
			alert("报表模板请选择Excel文件或网页文件（.html或.htm）上传！");
			return false;
		}
	}
	
	function checkSqlFile() {
		var filetitle = document.getElementsByName("sqlFile")[0].value;
		if (filetitle == "") {
			return true;
		}
		var index = filetitle.lastIndexOf(".");
		var filesname = filetitle.substring(index+1,filetitle.length).toLocaleLowerCase();
		if(filesname=="xml") {
			return true;
		} else{
			alert("SQL取值条件请选择xml文件上传！");
			return false;
		}
	}
</script>
<script type="text/javascript">
<!--
	function breturn() {
		customReportForm.action="/system/options/customreport.do?b_return=link";
		customReportForm.submit();
	}
	
	function upload() {
//		document.customReportForm.target='_SELF';
		//获得文件类型
		reporttype = document.getElementsByName("reportType")[0].value;
		flag = document.getElementById("flag");
		if (reporttype == "0") {
			if (!checkfilName()) {
				return ;
			}
			if (!checkSqlFile()) {
				return ;
			}
		}
		if(reporttype=="4"){
			if (!checkSqlFile()) {
				return ;
			}
		}
		// 验证报表名称
		var na = document.getElementsByName("name")[0];
		var str=validateValue(na);
		if (str!='') {
			alert("报表名称中含有非法字符\""+str+"\"！");
			return ;
		} else {
			if (trimStr(na.value).length <= 0) {
				alert("报表名称不能为空！");
				return ;
			}
		}
		
		// 验证模板文件
		var templateFile = document.getElementsByName("templateFile")[0];
		var reportType = document.getElementsByName("reportType")[0].value;
		if (reportType == "0") {
			if (trimStr(templateFile.value) == "") {
				/*alert("模板文件不能为空！");
				return ;*/
			} else {
				var filevalue = templateFile.value;
				var erro = 0;
				var indexof = filevalue.lastIndexOf(".");
				if (indexof > 0) {
					var ext = filevalue.substr(indexof + 1);
					if (ext != "htm" && ext != "html" && ext != "xls" 
						&& ext != "xlt" && ext != "xlsx" && ext != "xltx" && ext != "mht") {
							erro = 1;
						}
				} else {
					erro = 1;
				}
				
				if (erro == 1) {
					alert("模板文件类型错误！");
					return ;
				}
			}
		}
		var act = "";
		if (flag && flag.checked) {
			act="/system/options/customreport.do?b_save=link&encryptParam=<%=PubFunc.encrypt("flag=1")%>";
		} else {
			act="/system/options/customreport.do?b_save=link&encryptParam=<%=PubFunc.encrypt("flag=0")%>";
		}
		customReportForm.action=act;
		customReportForm.submit();
		
	}
	
	function typeChange() {
		var type = document.getElementsByName("reportType")[0].value;
		var temm = document.getElementById("temm");
		var sql = document.getElementById("sql");
		var im = document.getElementById("img");
		var name = document.getElementsByName("name")[0];
		if (type == "0") {			
			temm.style.display = "";
			sql.style.display = "";
			im.style.display = "none";
			name.readOnly = false;
		}else if(type == "4"){
			temm.style.display = "none";
			sql.style.display = "";
			im.style.display = "none";
			name.readOnly = false;
		} else {
			temm.style.display = "none";
			sql.style.display = "none";
			im.style.display = "";
			name.readOnly = true;
			
		}
	}
	function clName() {
		var name = document.getElementsByName("name")[0];
		name.value = "";
	}
	
	function openSelectDialog() {
		var type = document.getElementsByName("reportType")[0].value;
		var url = "";
		if (type == "1") {//
			url = "/system/options/customreport.do?b_treeone=link";
//			url = "/report/edit_report/reportSettree.do";
		} else if (type == "2") {
			url = "/system/options/customreport.do?b_treetwo=link";
		} else if (type == "3") {
			url = "/system/options/customreport.do?b_treethree=link";
		}
		// var return_vo;
		// var b = brows();
        // if (b.indexOf("MSIE|6") != -1) {
			// return_vo= window.showModalDialog(url,0,
        // "dialogWidth:430px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no");
        // } else {
        // var dw=480,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
        // 	return_vo= window.showModalDialog(url,0,
        //  "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
        // }
        return_vo ='';
        var theUrl = url;
        Ext.create('Ext.window.Window', {
            id:'customreport',
            height: 470,
            width: 500,
            resizable:false,
            modal:true,
            autoScroll:false,
            autoShow:true,
            html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
            renderTo:Ext.getBody(),
            listeners:{
                'close':function () {
                    if (return_vo) {
                        document.getElementsByName("link_tabid")[0].value = return_vo.content;
                        document.getElementsByName("name")[0].value = return_vo.title;
                    }
                }}

        }).show();
        // if (return_vo != null) {
	     //    document.getElementsByName("link_tabid")[0].value = return_vo.content;
	     //    document.getElementsByName("name")[0].value = return_vo.title;
        // }
//        window.alert(return_vo.content+"---"+return_vo.title);
	}
	
	// 下载模板文件和sql条件
	function downloadReport(id,type) {
		var hashvo=new ParameterSet();
	    hashvo.setValue("id",id);	
	    hashvo.setValue("type",type);
	    var request=new Request({method:'post',asynchronous:false,onSuccess:showdownloadfile,functionId:'10100103412'},hashvo);
	}
	
	function showdownloadfile(outparamters) {
		var filename=outparamters.getValue("filename");
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+filename);
	}
	
	function brows() {
		var userAgent = navigator.userAgent;
		var browserVersion = parseFloat(navigator.appVersion);
		var browserName = navigator.appName;
		if (userAgent.indexOf("Opera") != -1) {//Opera浏览器
			if (navigator.appName == "Opera") {
				return "Opera|" + navigator.appVersion;
			} else {
				var reg = new RegExp("Opera (\\d+\\.\\d+)");
				reg.test(userAgent);
				return "Opera|" + RegExp["$1"];
			}
		} else if (userAgent.indexOf("compatible") != -1 && userAgent.indexOf("MSIE") != -1) {
			var reg = new RegExp("MSIE (\\d+\\.\\d+)");
			reg.test(userAgent);
			return "MSIE|" + RegExp["$1"];
		} else if (userAgent.indexOf("KHTML") != -1 || userAgent.indexOf("Konqueror") != -1 
					|| userAgent.indexOf("AppleWebKit") != -1) {
			if (userAgent.indexOf("AppleWebKit") != -1) {
				var reg = new RegExp("AppleWebKit\\/(\\d+(\\.\\d*)?)");
				reg.test(userAgent);
				return "SAFARI|" + RegExp["$1"];
			}
			if (userAgent.indexOf("Konqueror") != -1) {
				var reg = new RegExp("Konqueror\\/(\\d+(\\.\\d+(\\.\\d+)?)?)");
				reg.test(userAgent);
				return "Konqueror|" + RegExp["$1"];
			}
		} else if (userAgent.indexOf("Gecko") != -1) {
			var reg = new RegExp("rv:(\\d+\\.\\d(\\.\\d+)?)");
			reg.test(userAgent);
			return "Mozilla|" + RegExp["$1"];
		} else if (userAgent.indexOf("Mozilla") == 0 && browserName == "Netscape" 
				&& browserVersion >= 4.0 && browserVersion < 5.0) {
			return "Netscape|" + browserVersion;
		} else {
			return "Other";
		}
	}
function winClose() {
    if(Ext.getCmp('customreport')){
        Ext.getCmp('customreport').close();
    }
}
//-->
</script>
<%int i = 0;%>
<html:form action="/system/options/customreport" enctype="multipart/form-data" target="_self">
	<logic:equal name="customReportForm" property="isEdit" value="1">
		<html:hidden name="customReportForm" property="id"/>
	</logic:equal>
	<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr align="center" nowrap class="trShallow">
			<td align="left" nowrap class="TableRow" colspan="2">
				<bean:message key="system.options.customreport.add.addcutomreport" />				
			</td>
		</tr>		
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	<bean:message key="system.options.customreport.business.module" />	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				<html:select name="customReportForm" property="businessModuleValue" size="1" style="width:450px;">
					<html:optionsCollection property="businessModuleList" value="dataValue" label="dataName" />
				</html:select>
			</td>
		</tr>
		<tr class="trDeep">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	<bean:message key="system.options.customreport.add.reporttype" />   
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				<html:select name="customReportForm" property="reportType" size="1" style="width:450px;" onchange="typeChange();clName();">
					<html:optionsCollection property="reportTypeList" value="dataValue" label="dataName" />
				</html:select>
			</td>
		</tr>
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	<bean:message key="system.options.customreport.add.reportname" />	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				<html:hidden name="customReportForm" property="link_tabid"/>
				<html:text name="customReportForm" property="name" maxlength="100" size="60" styleClass="text4" style="width:450px;"></html:text>
				<img id="img" align="absmiddle" src="/images/code.gif" onclick='javascript:openSelectDialog();' style="cursor: pointer;"/>
				
			</td>
		</tr>
		<tr class="trDeep">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	<bean:message key="system.options.customreport.add.reportdesc" />   
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				<html:textarea name="customReportForm" property="description" cols="60" rows="10" style="width:450px;"></html:textarea>
			</td>
		</tr>
		<tr class="trShallow" id="temm">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	<bean:message key="system.options.customreport.add.templatefile" />	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				<input type="file" name="templateFile" contenteditable="false" class="textborder common_border_color" size="50" style="width:450px;"/>
				<hrms:priv func_id="3001G07">
				<logic:notEmpty name="customReportForm" property="ext">
					<a href="javascript:downloadReport('<bean:write name="customReportForm" property="id"/>','<bean:write name="customReportForm" property="ext"/>')"><bean:message key="system.options.customreport.add.download"/></a>
				</logic:notEmpty>
				</hrms:priv>
				
			</td>
		</tr>
		<tr class="trDeep" id="sql">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	<bean:message key="system.options.customreport.add.sqlfile" />	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				<input type="file" name="sqlFile" contenteditable="false" class="textborder common_border_color" size="50" style="width:450px;"/>
				<hrms:priv func_id="3001G08">
				<logic:notEmpty name="customReportForm" property="sqlfileExist">
				<a href="javascript:downloadReport('<bean:write name="customReportForm" property="id"/>','xml')"><bean:message key="system.options.customreport.add.download"/></a>
				</logic:notEmpty>
				</hrms:priv>
			</td>
		</tr>
		<hrms:priv func_id="3001G02">
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	<bean:message key="system.options.customreport.button.release"/>	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				<!--<html:checkbox name="customReportForm" property="flag" value="1"></html:checkbox>-->
				<logic:equal name="customReportForm" property="flag" value="1">
					<input id="flag" name="flag" type="checkbox" checked="checked"/>
				</logic:equal>
				<logic:notEqual name="customReportForm" property="flag" value="1">
					<input id="flag"  name="flag" type="checkbox" />
				</logic:notEqual>
				<bean:message key="system.options.customreport.button.release" />
			</td>
		</tr>
		</hrms:priv>
	</table>
		
	<table width="85%" align="center">
		<tr>
			<td align="center" height="35px;">
				<input type="button" name="b_save" value="<bean:message key="button.ok"/>" class="mybutton" onclick="upload()"/>	
				<input type="button" name="b_return" value="<bean:message key="button.return" />" class="mybutton" onclick="breturn()"/>
				
			</td>
		</tr>
	</table>
	
<script type="text/javascript">
<!--
	typeChange();
//-->
</script>
</html:form>

