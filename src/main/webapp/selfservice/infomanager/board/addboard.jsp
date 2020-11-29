<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@page import="com.hjsj.hrms.actionform.board.BoardForm"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig,com.hrms.hjsj.sys.ConstantParamter"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.*"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link> 
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7;">
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/ckEditor/CKEditor.js"></script>
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
<script language="javascript" src="/js/constant.js"></script>
<%
String viewunit="1";
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null)
{
    if(userView.getStatus()==4||userView.isSuper_admin())
      viewunit="0";
    else{
    	String codeall = userView.getUnit_id();
		if(codeall==null||codeall.length()<2)
			viewunit="0";
    }
    
    userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
String uploadfilemaxsize = SystemConfig.getPropertyValue("uploadfilemaxsize");
try{
	Integer.parseInt(uploadfilemaxsize);
}catch(Exception e){
	uploadfilemaxsize = "20";
}
pageContext.setAttribute("uploadfilemaxsize", uploadfilemaxsize);
String corpid = ConstantParamter.getAttribute("wx", "corpid");
String dingTalk = ConstantParamter.getAttribute("DINGTALK","corpid");
%>
<hrms:themes></hrms:themes>
<style>

	.titletd{
		width: 80px;
	}
</style>
<script language="javascript">
var ViewProperties=new ParameterSet();
  function IsDigit() 
  { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
  }
  
  function check(){
  	var msg = document.getElementById('msg').value;
  	var content = document.getElementById('boardvo.string(content)').value;
  	var topic = document.getElementById('boardvo.string(topic)').value;
  	if("" == msg || null == msg){	
  		validate( 'R','boardvo.string(topic)','标题','R','boardvo.string(content)','内容');
  	}
  }
  
  function deletFile(){
	  if(confirm("确定要删除附件吗？"))
      {   
	      var ids = document.getElementById('boardvo.int(id)').value;
          var exts = document.getElementById('boardvo.string(ext)').value;
		  var obj = new ParameterSet();
          obj.setValue("state","deletFile");
          obj.setValue("id", ids);
          obj.setValue("ext", exts);
          var request=new Request({method:'post',onSuccess:function (){window.location.reload();},functionId:'1030060004'},obj);
      }
	  
  }

  function deFileClear(){
	  //window.location.reload();
	  var obj = document.getElementById('fileid');
      obj.outerHTML=obj.outerHTML; 
      return;
	  }

  </script>
<%try {
        String opt=request.getParameter("opt");
        String announce = "";
        String type = "";
        BoardForm bs=(BoardForm)session.getAttribute("boardForm");
        HashMap hm = (HashMap)bs.getFormHM().get("requestPamaHM");
        opt=bs.getOpt();
        announce = bs.getAnnounce();
        type = bs.getType();
        hm.remove("type");
        if(opt==null){
            opt="1";
        }
%>
<html:form action="/selfservice/infomanager/board/addboard"
	enctype="multipart/form-data" onsubmit="return filevalidate();">
	<html:hidden name="boardForm" property="chflag"/>
    <html:hidden name="boardForm" property="opt"/>
    <html:hidden name="boardForm" property="announce"/>
    <html:hidden name="boardForm" property="type"/>
     <html:hidden name="boardForm" property="boardvo.int(id)" styleId="boardvo.int(id)"/>
      <html:hidden name="boardForm" property="boardvo.string(ext)" styleId="boardvo.string(ext)"/>
    <html:hidden name="boardForm" property="tmpnbase" styleId="tmpnbaseid"/>
    <center>
	<table width="1100px" border="0" cellpadding="0" cellspacing="0"
		align="center" class="ftable" style="margin:6px auto 0 auto;">

		<tr height="20">
			<td align="left" colspan="2" class="TableRow" style="margin-left:5px;">
				<bean:message key="lable.board.manager" />
			</td>
		</tr>
		<tr>
			<td align="right" class="titletd" nowrap valign="center">
				<bean:message key="conlumn.board.topic" />
			</td>
			<td align="left">
				<html:text name="boardForm" maxlength='50' size="70"
					property="boardvo.string(topic)" styleClass="text4" style="width:400px;"/>
			</td>
		</tr>

		<tr>

			<td align="right" class="titletd" nowrap valign="center">
				<bean:message key="conlumn.board.content" />
			</td>

			<td div id="tableEdit" align="left" nowrap>
				<html:textarea name="boardForm" property="boardvo.string(content)"
					cols="80" rows="20" style="display:none;" />
				<%if(!opt.equalsIgnoreCase("2")){%>
				<script type="text/javascript">
				var oldInputs = document.getElementsByName('boardvo.string(content)');                             
	              var CKEditor = Ext.create('EHR.ckEditor.CKEditor',{
		              id:'ckeditorid',
		              functionType:"standard",         
		              width:'100%',
			      	  height:'100%'      
		            });  
	          	
	          	 var Panel = Ext.create('Ext.panel.Panel', {
	    			 id:'ckeditorPanel',			 
	                 border: false,
	                 width: '100%',
		             height: 400,
	    			 items: [CKEditor],	 			  
	    			 renderTo: "tableEdit"
	    			});
	          	 
	          	var oEditor = Ext.getCmp("ckeditorid");
	          	oEditor.setValue(oldInputs[0].value);
                   </script>
				<%}else{ %>
				<script type="text/javascript">
	                      			  var oldInputs = document.getElementsByName('boardvo.string(content)');                             
						              var CKEditor = Ext.create('EHR.ckEditor.CKEditor',{
							              id:'ckeditorid',
							              functionType:"standard",         
							              width:'100%',
								      	  height:'100%'      
							            });  
						          	
						          	 var Panel = Ext.create('Ext.panel.Panel', {
						    			 id:'ckeditorPanel',			 
						                 border: false,
						                 width: '100%',
							             height: 400,
						    			 items: [CKEditor],	 			  
						    			 renderTo: "tableEdit"
						    			});
						          	 
						          	var oEditor = Ext.getCmp("ckeditorid");
						          	oEditor.setValue(oldInputs[0].value);
	           
	                       </script>
				<%} %>
			</td>
		</tr>
		<tr>
			<td align="right" class="titletd" height="30" nowrap>
				<bean:message key="conlumn.board.period" />
			</td>

			<td align="left">
				<html:text name="boardForm" property="boardvo.string(period)"
					maxlength="8" styleClass="text4" size="70" style="width:400px;"/>
			</td>
		</tr>
		<tr>
			<td align="right" class="titletd" nowrap height="30">
				<bean:message key="conlumn.board.priority" />
			</td>
			<td align="left">
				<html:text name="boardForm" style="width:400px;" property="boardvo.string(priority)" size="70"
					maxlength="4" onkeypress="event.returnValue=IsDigit();" styleClass="text4"/>
				&nbsp;&nbsp;
				<br><bean:message key="priority.explain" /><br/>
			</td>
		</tr>
		<%if("2".equals(announce)||"1".equals(type)){ %> <!-- 招聘公告显示附件上传 -->
			<tr>
			<td align="right" class="titletd" height="30" nowrap>
				附件上传
			</td>
			<td align="left" nowrap>

				<html:file name="boardForm" property="file" style="width:465px;" styleClass="TEXT4"/>
				<logic:notEqual name="boardForm" property="boardvo.string(ext)" value="">
				    <input id='delefile' type="button" value="删除附件" onclick="deletFile();" style="background-color:#F0F0F0;height:22px;border:1pt solid #C4D8EE;font-family:微软雅黑;font-size:12px;vertical-align:middle;"  />
				</logic:notEqual>
			</td>
		</tr>
		<%} %>
		<%if(!opt.equalsIgnoreCase("2")){ %>
		<tr>
			<td align="right" class="titletd" height="30" nowrap>
				附件上传
			</td>
			<td align="left" nowrap>

				<html:file styleId='fileid' name="boardForm" style="width:400px;" property="file" styleClass="text6" size="49"  onchange="checkFileMaxSize(this,${uploadfilemaxsize })"/>
				<logic:notEqual name="boardForm" property="boardvo.string(ext)" value="">
				    <input id='delefile' type="button" value="删除附件" onclick="deletFile();" style="background-color:#F0F0F0;height:22px;border:1pt solid #C4D8EE;font-family:微软雅黑;font-size:12px;vertical-align:middle;"  />
				</logic:notEqual>
				
			</td>
		</tr>
		<tr>
			<td align="right" class="titletd" height="30" nowrap>
				通知对象
			</td>
			<td align="left">
				<hrms:optioncollection name="boardForm" property="spersonlist"
					collection="list" />
				<html:select name="boardForm" property="sperson"
					onchange="selectobject(this);" style="width:60">
					<html:options collection="list" property="dataValue"
						labelProperty="dataName" />
				</html:select>
				<html:text name="boardForm" property="noticeperson" styleId="noticeperson"
					styleClass="TEXT" size="56" maxlength="200" style="width:336px;"/>
				<img src='/images/del.gif' title='清空通知对象' onclick='clearObjs()' />
				<html:hidden name="boardForm" property="selectPerson" styleId="selectPerson"/>
			</td>
		</tr>
		<logic:equal name="boardForm" property="chflag" value="1">
			<tr>
				<td align="right" height="30" class="titletd" nowrap>
					邮件/短信<%if(StringUtils.isNotEmpty(corpid)){ %>/微信<%} if(StringUtils.isNotEmpty(dingTalk)){%>/钉钉<%}%>模板
				</td>
				<td>
					<html:select name="boardForm" property="msg" size="1"
						style="width:120px;">
						<html:option value=""></html:option>
						<html:optionsCollection name="boardForm" property="msgList"
							label="dataName" value="dataValue" />
					</html:select>
				</td>
			</tr>
		</logic:equal>
		<%} %>
		<tr>
			<td align="center" colspan="2" style="height: 35px">
				<%if(!opt.equalsIgnoreCase("2")){ %>
				<logic:equal name="boardForm" property="chflag" value="1">
					<!--  <input type="button" value="发送邮件" onclick="sendEmail();" class="mybutton"/>-->
					<!--  <input type="button" value="发送短信" onclick="sendSms();" class="mybutton"/>-->
					<hrms:submit styleClass="mybutton" property="b_sendEmail"
						onclick="showView();document.boardForm.target='_self';check(); return (document.returnValue && ifqrtj()); ">
						发送邮件
					</hrms:submit>
					<hrms:submit styleClass="mybutton" property="b_sendSms"
						onclick="showView();document.boardForm.target='_self';check();return (document.returnValue && ifqrtj());">
						发送短信
					</hrms:submit>
					<% if(StringUtils.isNotEmpty(corpid)){ %>
						<hrms:submit styleClass="mybutton" property="b_sendWeiXin"
							onclick="showView();document.boardForm.target='_self';check(); return (document.returnValue && ifqrtj()); ">
							发送微信
						</hrms:submit>
					<%}
					  if(StringUtils.isNotEmpty(dingTalk)){ %>
						<input type="button" value="发送钉钉" onclick="sendmsg();"
						class="mybutton">
							
					<%} %>
					<hrms:submit styleClass="mybutton" property="b_psave"
						onclick="showView();document.boardForm.target='_self';validate( 'R','boardvo.string(topic)','公告标题','RZ','boardvo.string(period)','公告天数','Z','boardvo.string(priority)','优先级');return (document.returnValue && ifqrtj());">
						发布公告
					</hrms:submit>
					<html:reset styleClass="mybutton" property="reset">
						<bean:message key="button.clear" />
					</html:reset>
					<input type="button" value="关闭" onclick="window.close();"
						class="mybutton">
				</logic:equal>
				<%} %>
				<logic:notEqual name="boardForm" property="chflag" value="1">
					<hrms:submit styleClass="mybutton" property="b_save"
						onclick="showView();document.boardForm.target='_self';validate( 'RL|1-50|','boardvo.string(topic)','公告标题','RZ','boardvo.string(period)','公告天数','Z','boardvo.string(priority)','优先级');return (document.returnValue && ifqrbc());validateUploadFilePath(document.getElementsByName('file')[0].value);">
						<bean:message key="button.save" />
					</hrms:submit>
					<html:reset styleClass="mybutton" property="reset" onclick="myClear()">
						<bean:message key="button.clear" />
					</html:reset>
					<!-- <hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="button.return" />
					</hrms:submit> -->
					<input type="button" class="mybutton" onclick="returnrul();"
						value='<bean:message key="button.return"/>'>
				</logic:notEqual>
				<input type="hidden" name="announce" value='${boardForm.announce}' />
				<input type="hidden" name="op" value='${boardForm.op}' />
				<input type="hidden" name="trainid" value='${boardForm.trainid}' />
			</td>
		</tr>

	</table>
	</center>
</html:form>
<%} catch (Exception ex) {%>
<script>
	alert('添加不成功');
</script>
<%}%>
<logic:equal name="boardForm" property="chflag" value="1">
	<logic:equal name="boardForm" property="boardvo.string(approve)"
		value="1">
		<script>
	window.close();
</script>
	</logic:equal>
</logic:equal>
<logic:equal name="boardForm" property="chflag" value="a">
	<script>
   window.close();
</script>
</logic:equal>
<script>
//组织机构树如果显示人员，则先显示人员库
function select_org_emp_dialog22(flag,selecttype,dbtype,priv,isfilter,loadtype)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
	 <logic:equal name="boardForm" property="chflag" value="1">
	 var tmpnbase=document.getElementById("tmpnbaseid");
	 if(tmpnbase.value==null||tmpnbase.value==""){
		alert(TRAIN_CLASS_NOTICR_TMPNBASE);
		return;
	 }
      	dbtype=-2;
      </logic:equal>
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`showDb=1`selecttype="+selecttype+"`dbtype="+dbtype+"`viewunit=<%=viewunit %>"+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype;
      <logic:equal name="boardForm" property="chflag" value="1">
      	theurl+="`nmodule=6";
      </logic:equal>
      var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;  
      
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
function isIE() { //ie?  
    if (!!window.ActiveXObject || "ActiveXObject" in window)  
        return true;  
    else  
        return false;  
 }
function showView() {
	//if(isIE()){ haosl	delete 20170327  目前Fckeditor经过修改已经可以支持非IE浏览器，所以这块代码就被俺给注掉了-.-|||
		var oEditor = Ext.getCmp("ckeditorid");
		var oldInputs = document.getElementsByName('boardvo.string(content)');
		var tmpvalue=oEditor.getHtml();
		/* while(tmpvalue.indexOf("&nbsp;")!=-1)
			tmpvalue=tmpvalue.replace("&nbsp;","?"); */ //haosl delete 20170327 将空格替换为"？"是为什么,页面显示空格都是问号。暂时注掉
		oldInputs[0].value = tmpvalue;
	//}else{
	//	document.getElementsByName('boardvo.string(content)')[0].value = document.getElementsByName("FCKeditor1")[0].value;
	//}
}
function selectobject(obj){
	 var objecttype=obj.value;
	 if(objecttype==null||objecttype=="00")
	 	return ;
	 if(objecttype=="01"){
		 
	     	var theurl="/system/logonuser/org_employ_tree.do?flag=1`showDb=1`selecttype=1`dbtype=1`viewunit=0"+
	                "`priv=1`isfilter=1`loadtype=1`cascade=true`checklevel=4";
	     	theurl = $URL.encode(theurl);
	      	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;  
	      
			var iTop = (window.screen.height-30-400)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.width-10-300)/2;  //获得窗口的水平位置;
			window.type='orgEmp6';
			window.open(iframe_url,1,'height=400, width=300,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
     	
	 }else if(objecttype=="02"){
	
		var theurl="/general/template/select_role_dialog.do?b_query=link";
		theurl = $URL.encode(theurl);
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl; 
		var iTop = (window.screen.height-30-440)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.width-10-520)/2;  //获得窗口的水平位置;
		window.open(iframe_url,1,'height=440, width=520,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
	 }else if(objecttype=="03"){
	        
	        var theurl="/system/logonuser/org_employ_tree.do?flag=0`selecttype=1`dbtype=0"+
            			"`priv=1`isfilter=0`loadtype=1";
	        theurl = $URL.encode(theurl);
 			var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
			var iTop = (window.screen.height-30-400)/2; //获得窗口的垂直位置;
			var iLeft = (window.screen.width-10-300)/2;  //获得窗口的水平位置;
			window.type='orgEmp';
			window.open(iframe_url,1,'height=400, width=300,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
	       
	 }
		 
}
//open弹窗回调方法 liujx 20190318
function openRoleHistoryReturn(return_vo){
  	if(return_vo&&return_vo.length>0){
   		for(var i=0;i<return_vo.length;i++){
   			var rolevo=return_vo[i];
   			document.getElementById("noticeperson").value+=rolevo.role_name+",";
   			document.getElementById("selectPerson").value+="1:"+rolevo.role_id+","; 
   		}        	   
   }
}
//open弹窗回调方法 liujx 20190318
function openEmpHistoryReturn6(return_vo){
	if(return_vo){
	 	var a_temps=return_vo.title.split(",");
 		var temps=return_vo.content.split(",");
 		for(var i=0;i<temps.length;i++){
 			if(temps[i].length>0&&temps[i].substr(0,2)!='UN'
 			&&temps[i].substr(0,2)!='UM'
 			&&temps[i].substr(0,2)!='@K'){
 				document.getElementById("noticeperson").value+=a_temps[i]+",";
 				document.getElementById("selectPerson").value+="4:"+temps[i]+",";
 			}
 		}
 	}
}
function openEmpHistoryReturn(ret_vo){
	if(ret_vo)
    {
        var re=/,/g;   
        document.getElementById("noticeperson").value+=ret_vo.title;
        document.getElementById("selectPerson").value+=ret_vo.content.replace(re,'`')+",";
    }
}
function clearObjs(){
	document.getElementById("noticeperson").value="";
	document.getElementById("selectPerson").value="";
}
function filevalidate(){
	    var fileObj = document.getElementsByName("file");
		var filePath = fileObj.length>0?fileObj[0].value:null;
		if(filePath!=null&&filePath.length!=0){
			var flag=validateUploadFilePath(filePath);
				if(!flag){
					return false;
				}
		}
		return true;
}

function returnrul(){
	boardForm.action="/selfservice/infomanager/board/addboard.do?br_return=link";
	boardForm.submit();
}
function myClear(){ 
	var oEditor = Ext.getCmp("ckeditorid");
	//oEditor.EditorDocument.body.innerText="";
	oEditor.setValue("");
}

function sendmsg(){
	boardForm.action="/selfservice/infomanager/board/addboard.do?b_sendDingTalk=link";
	boardForm.submit();
}
</script>
