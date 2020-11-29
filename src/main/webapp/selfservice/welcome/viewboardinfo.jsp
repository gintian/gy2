<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.welcome.*"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    /**登录版本不同,界面*/
    String bosflag=userView.getBosflag();
	String flag=request.getParameter("flag");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<head>
<link href="./style.css" rel="stylesheet" />
</head> 

<script language="javascript">
function createFile(id,ext){
	
	var hashvo=new ParameterSet();   
    hashvo.setValue("id",id);
    hashvo.setValue("ext",ext);
    var request=new Request({method:'post',asynchronous:false,onSuccess:showFile,functionId:'10300130024'},hashvo);      
}
//xus vfs改造
function downloadFile(fileid){
	window.location.href = "/servlet/vfsservlet?fileid="+fileid;
}

 function setKeyinfor()
 {
    var obj=document.getElementById('SetIE'); 
     var strIp ="";
    if (obj != null)
    {
      strIp= obj.GetIP();
    }
    obj=document.getElementById('topic');
    var topic="";
    if(obj!=null)
      topic=getEncodeStr(obj.value);    
    var hashvo=new ParameterSet();   
    hashvo.setValue("address",strIp);
    hashvo.setValue("content",topic);
    hashvo.setValue("content_type","0");
    var request=new Request({method:'post',asynchronous:false,functionId:'10300130100'},hashvo);
 }
 function showSussess(outparamters)
 {
      var flag=outparamters.getValue("flag");
      var mess=outparamters.getValue("mess");
      if(flag!="ok")
      {
        if(mess!="")
          alert("保存失败,"+mess+"！");
        else
          alert("保存失败！");
      }        
      else
        alert("保存成功！");
 }
 function showLog(id)
 {
    var height=window.screen.height-85;
    var strurl="/selfservice/welcome/keylog_tree.do?b_query=init`id="+id;
    var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
    window.showModalDialog(iframe_url,null,"dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");  
 }
 function revert(id)
 {
     var target_url="/selfservice/welcome/welcome.do?br_revert=link";
     var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:500px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      if(return_vo==null)
       return false;      
      if(return_vo.save!="1")
        return false;      
      if(return_vo.text!="")
      {
        //保存
         var hashvo=new ParameterSet();
         var obj=document.getElementById('topic');         
         var topic="";
         if(obj!=null)
             topic=getEncodeStr(obj.value);
         hashvo.setValue("content",topic);
         hashvo.setValue("opinion",return_vo.text);
         hashvo.setValue("content_type","0");
         var request=new Request({method:'post',asynchronous:false,onSuccess:showSussess,functionId:'10300130022'},hashvo);      
      }
       return true;
 }
</script>
<body>
<html:form action="/selfservice/welcome/welcome">
<center>
	<!-- 公告展示效果优化：标题在左上角、时间/公告期/附件/返回在右上角  chent 20170504 start-->
	<div class="bh-wzm-index-all">
		<div class="hj-wzm-din" style='z-index:2;'>
			<bean:message key="conlumn.board.createtime"/>：<bean:write  name="welcomeForm" property="boardvo.string(createtime)" filter="true"/>&nbsp;&nbsp;公告期：<bean:write  name="welcomeForm" property="boardvo.string(period)" filter="true"/>天
			<logic:notEqual name="welcomeForm" property="boardvo.string(ext)" value="">
				<bean:define id="boardid" name="welcomeForm" property="boardvo.string(id)"/>
				<bean:define id="ext" name="welcomeForm" property="boardvo.string(ext)"/>
				<bean:define id="topic" name="welcomeForm" property="boardvo.string(topic)"/>
				<bean:define id="fileid" name="welcomeForm" property="boardvo.string(fileid)"/>
				<%
					String encryptParam = PubFunc.encrypt("id="+PubFunc.encrypt(boardid.toString())+"&ext="+ext+"&topic="+topic);
				%>
				<a style='cursor:pointer;font-weight:bold;color:#1B4A98;margin-left:10px;' onclick="downloadFile('<%=fileid %>')" target="_blank"><bean:message key="conlumn.baord.accessoriesview"/></a>	
			</logic:notEqual> 
			<%if(bosflag.equalsIgnoreCase("hl")&&!"flag".equals(flag)){%>
		      <%-- <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="window.location.href='/templates/index/portal.do?b_query=link';"> --%>
		      <a style='cursor:pointer;font-weight:bold;color:#1B4A98;margin-left:10px;' onclick="window.location.href='/templates/index/portal.do?b_query=link';">返回</a> 
		   	<%}else if(bosflag.equalsIgnoreCase("hcm")&&!"flag".equals(flag)){%>  
		      <%-- <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="window.location.href='/templates/index/hcm_portal.do?b_query=link';"> --%>
		      <a style='cursor:pointer;font-weight:bold;color:#1B4A98;margin-left:10px;' onclick="window.location.href='/templates/index/hcm_portal.do?b_query=link';">返回</a>  
		   	<%}else{%>  
		      <%-- <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="var url ='/templates/cclose.jsp';newwin=window.open(url,'_parent','toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no','true');"> --%>
		      <a style='cursor:pointer;font-weight:bold;color:#1B4A98;margin-left:10px;' onclick="var url ='/templates/cclose.jsp';newwin=window.open(url,'_parent','toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no','true');">返回</a>   
			<%} %>
		</div>
    	<h2><bean:write name="welcomeForm" property="boardvo.string(topic)" filter="true"/></h2>
		<span>
			${welcomeForm.ext_content}
		</span>
		<div class="bh-clear"></div>
	</div>		
	<!-- 公告展示效果优化：标题在左上角、时间/公告期/附件/返回在右上角  chent 20170504 end-->
      <table><tr class="list3">
            <td align="center" colspan="2" height="35px;">
	            <logic:notEqual name="welcomeForm" property="boardvo.string(flag)" value="11">
		            <hrms:istablesave tablename="t_keyinfor_log">
		            <hrms:priv func_id="1115"> 
		             <input type="button" name="returnbutton"  value="<bean:message key="lable.boardinfo.revert"/>" class="mybutton" onclick="revert('<bean:write  name="welcomeForm" property="boardvo.string(id)" filter="true"/>');">
		             </hrms:priv>
		              <hrms:priv func_id="1114"> 
		               <input type="button" name="returnbutton"  value="<bean:message key="lable.boardinfo.recodeview"/>" class="mybutton" onclick="showLog('<bean:write  name="welcomeForm" property="boardvo.string(id)" filter="true"/>');">
		             </hrms:priv>
		            </hrms:istablesave>
	            </logic:notEqual>
            </td>
          </tr>     
          </table>
          </center>
</html:form>
</body>
 <div id='axc' style='display:none'/>
 
 <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 <script type="text/javascript">
 function InitAx()
 {
     if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
           return;
 }
 InitAx();
 </script>
<script language="javascript">
 setKeyinfor();
</script>