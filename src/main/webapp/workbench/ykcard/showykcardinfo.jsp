<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:linkExtJs frameDegradeId="framedegrade"/>
<script type='text/javascript' src='../../../ext/rpc_command.js'></script>
 <!-- 32151	1号登记表：输出WORD时，进度条的颜色不对 引用ext6css样式 -->
<link id="theme" rel="stylesheet" type="text/css" href="../../components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" />
<style>
.mybuttons{
	border:1px solid #c5c5c5 ;
	padding:2px 4px 2px 4px ;
	background-color:#f9f9f9 ;
	font:12px/16px 微软雅黑, 宋体, tahoma, arial, verdana, sans-serif;
}
</style>
<script language='javascript'>

//添加导出excel功能
function excecuteword(flag,fileFlag)
{
		var hashvo=new HashMap();
        var tab_id="${cardTagParamForm.tabid}";  
        if(tab_id=="")
        {
           tab_id="${re_tabid}";  
        } 
        hashvo.put("nid","${cardTagParamForm.a0100}");
        hashvo.put("b0110","${cardTagParamForm.b0110}");
        hashvo.put("flag","${cardTagParamForm.flag}");
        hashvo.put("tabid",tab_id);
        hashvo.put("cardid","1");
        if(${cardTagParamForm.cardparam.queryflagtype}==1)
        {
        	hashvo.put("cyear","${cardTagParamForm.cardparam.cyear}");
        }else if(${cardTagParamForm.cardparam.queryflagtype}==3)
        {
        	hashvo.put("cyear","${cardTagParamForm.cardparam.csyear}");
        }else if(${cardTagParamForm.cardparam.queryflagtype}==4)
        {
        	hashvo.put("cyear","${cardTagParamForm.cardparam.cyyear}");
        }
           hashvo.put("cmonth","${cardTagParamForm.cardparam.cmonth}");
           hashvo.put("season","${cardTagParamForm.cardparam.season}");
        hashvo.put("userpriv","selfinfo");
        hashvo.put("istype","0");        
       
        
        hashvo.put("ctimes","${cardTagParamForm.cardparam.ctimes}");
        hashvo.put("cdatestart","${cardTagParamForm.cardparam.cdatestart}");
		hashvo.put("cdateend","${cardTagParamForm.cardparam.cdateend}");
		hashvo.put("querytype","${cardTagParamForm.cardparam.queryflagtype}");
		hashvo.put("infokind","1");
		hashvo.put("userbase","${cardTagParamForm.userbase}");
	    hashvo.put("pre","${cardTagParamForm.pre}");
		hashvo.put("fieldpurv","1");
		hashvo.put("fileFlag",fileFlag);
        hashvo.put("flag",flag);
        showWait(true,fileFlag);
  //  var In_paramters="word=word";
  Rpc({functionId:'07020100026',async:true,success:showWord,scope:this},hashvo);
    //var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'07020100026'},hashvo);

}

function showWait(flag,fileFlag){
	 if(flag){
	 	if(fileFlag=='pdf'){
		 Ext.MessageBox.wait("正在执行导出PDF操作，请稍候...", "等待");
	 	}else{
	 	 Ext.MessageBox.wait("正在执行导出WORD操作，请稍候...", "等待");
	 	}
	 }
	 else
		Ext.MessageBox.close(); 	 
	}

function showWord(outparamters)
{
	var res=Ext.decode(outparamters.responseText);// 
	showWait(false);
	if(res.succeed){
		if(!res.errorMsg){
			var url=res.url;
			url=decode(url)
		    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url);
		}else{
			Ext.showAlert(res.errorMsg);
		}
	   	
	}else{
		Ext.showAlert(res.message);
	}
}


//薪酬明细
function salaryInfo(){
	var id = '${cardTagParamForm.a0100}';
	var pre = '${cardTagParamForm.userbase}';
	document.cardTagParamForm.action="/system/options/salaryinfo.do?b_search=link&a0100="+id+"&pre="+pre;
	document.cardTagParamForm.submit();
}

function back(){
	document.cardTagParamForm.target="mil_body";
	document.cardTagParamForm.action="/workbench/ykcard/showinfodata.jsp";
	document.cardTagParamForm.submit();
	
}
function changTabid(obj)
{
    var tabid=obj.value;
    var a0100='${cardTagParamForm.a0100}';
    var pre='${cardTagParamForm.pre}';
    var b0110="${cardTagParamForm.b0110}";
    var flag="${cardTagParamForm.flag}";   
    document.cardTagParamForm.action="/workbench/ykcard/showykcardinfo.do?b_querypage=link&a0100="+a0100+"&flag="+flag+"&pre="+pre+"&b0110="+b0110+"&tabid="+tabid;
    document.cardTagParamForm.submit();
}
</script>
<%
String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";
 %>
<hrms:themes />
<html:form action="/workbench/ykcard/showykcardinfo">
<input type="hidden" id="firstFlag" name="firstFlag" value="${cardTagParamForm.firstFlag}"/>
	<table width="100%">
		<tr width="100%">
			<td width="100%" align="center">
				<hrms:ykcard name="cardTagParamForm" property="cardparam" nid="${cardTagParamForm.a0100}" b0110="${cardTagParamForm.b0110}" nbase="${cardTagParamForm.pre}" tabid="${cardTagParamForm.tabid}" cardtype="SS_SETCARD" disting_pt="javascript:screen.width" userpriv="noinfo" havepriv="1" istype="0" infokind="1" fieldpurv="1"  browser="<%=browser %>"/>
			</td>
		</tr>
	</table>
</html:form>
