<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<style>
<!--
.div_table{
    border-width: 1px;
    BORDER-BOTTOM: #aeac9f 1pt solid; 
    BORDER-LEFT: #aeac9f 1pt solid; 
    BORDER-RIGHT: #aeac9f 1pt solid; 
    BORDER-TOP: #aeac9f 1pt solid ; 

}
.tdFontcolor{
	text-decoration: none;
	Font-family:;
	font-size:12px;
	height:12px;
	align:"center"
}
-->
</style>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
 <script language="JavaScript">
   	function savecode()
   	{
   		if($F('userbases').length==0){
			alert('人员库不能为空');
			return false
		}
    	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;   
    	   codeitemid=currnode.uid;
    	   if(root.getSelected()=="root,")
    	     staticFieldForm.querycond.value="";
    	   else    	     
    	     staticFieldForm.querycond.value=root.getSelected();
    	//   staticFieldForm.submit();
    	    var thevo=new Object();
            thevo.flag="true";
            thevo.querycond=staticFieldForm.querycond.value;
            //thevo.userbase=staticFieldForm.userbase.value;
            thevo.userbase=$F('userbases');
            thevo.viewuserbase=$URL.encode($F('viewuserbases'));
            if(getBrowseVersion()){
	            if(navigator.appName.indexOf("Microsoft")!= -1){ 
		    	    window.returnValue=thevo;
	    		    window.close();
    	    	}else{
    	    		top.returnValue=thevo;
	    	    	top.close();
    	    	}
            }else{//非IE浏览器返回数据 wangb 20180803 bug 39379
            	 parent.parent.returnValue(thevo);
            }
    	    //staticFieldForm.action="/general/static/static_general_result.do?b_setcode=link";
    	    //staticFieldForm.target="il_body";
            //staticFieldForm.submit();
    	  //window.opener.parent.location.href="/general/static/static_general_result.do?b_setcode=link";
   	}
   
function getAbsPosition(obj, offsetObj){
	var _offsetObj=(offsetObj)?offsetObj:document.body;
	var x=obj.offsetLeft;
	var y=obj.offsetTop;
	var tmpObj=obj.offsetParent;

	while ((tmpObj!=_offsetObj) && tmpObj){
		x += tmpObj.offsetLeft - tmpObj.scrollLeft + tmpObj.clientLeft;
		y += tmpObj.offsetTop - tmpObj.scrollTop + tmpObj.clientTop;
		tmpObj=tmpObj.offsetParent;
	}
	return ([x, y]);
} 	
function addDict(obj,event,flag)
{ 
	var ff=document.getElementById('dict').style.display;
	if('block'==ff){
		document.getElementById('dict').style.display="none";
		return;
	}
   var evt = event ? event : (window.event ? window.event : null);
   var np=   evt.keyCode; 
   if(np==38||np==40){ 
   
   } 
   var aTag;
   	aTag = obj;   
   var un_vos=document.getElementsByName("userbase")[0];
   var userbases=document.getElementsByName("userbases")[0].value;
   if(!un_vos)
		return false;
   var unArrs=un_vos.options;	
   //var unArrs=unStrs.split(",");
   var   c=0;
   var   rs   =new   Array();
   for(var i=0;i<unArrs.length;i++)
   {
		 var un_str=unArrs[i];
		 if(un_str)
		 {
		     if(userbases.indexOf(un_str.value)!=-1){
		     	if(c%2==0)
			     	rs[c]="<tr id='tv' name='tv'><td id='al"+c+"' style='height:10px;width:50%;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+"`"+un_str.text+"' checked=checked />"+un_str.text+"</td>"; 
             	else
             		rs[c]="<td id='al"+c+"' style='height:10px;width:50%;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+"`"+un_str.text+"' checked=checked />"+un_str.text+"</td></tr>"; 
             }else{
             	if(c%2==0)
                 	rs[c]="<tr id='tv' name='tv'><td id='al"+c+"' style='height:10px;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+"`"+un_str.text+"' />"+un_str.text+"</td>"; 
             	else
             		rs[c]="<td id='al"+c+"' style='height:10px;width:50%;cursor:pointer' nowrap class=tdFontcolor><input name=backdatebox type=checkbox onclick='' value='"+un_str.value+"`"+un_str.text+"' />"+un_str.text+"</td></tr>"; 
             }
             c++;
		 }
        
	}
	if(c%2!=0){
		rs[c]="<td id='al"+c+"'  onclick=\"\"  style='height:10px;cursor:pointer' nowrap class=tdFontcolor></td></tr>"; 
		c++;
	}
    resultuser=rs.join("");
    resultuser="<div style='border-width: 1px;BORDER-bottom: #aeac9f 1pt solid;height:80px;width:"+($('viewuserbases').offsetWidth+aTag.offsetWidth-22)+"px;overflow:auto;margin:9 9 9 9'><table width='100%' cellpadding='0' border='0' bgcolor='#FFFFFF' cellspacing='0'>"+resultuser+"</table></div>"; 
    resultuser+="<table style='margin:9 9 9 9' width="+($('viewuserbases').offsetWidth+aTag.offsetWidth-22)+"px cellpadding='0' border='0' bgcolor='#FFFFFF' cellspacing='0'><tr id='tv' name='tv'><td id='al"+c+"' style='width:85%;height:10px;cursor:pointer' nowrap class=tdFontcolor><input name=allbox type=checkbox onclick='selectallcheckbox(this)' value='' />全部</td></tr>";
    resultuser+="<tr><td align='center' style='height:35px'><input type='button' class='mybutton' onclick=\"selectcheckbox();document.getElementById('dict').style.display='none'\" value='确定'/>&nbsp;&nbsp;<input type='button' class='mybutton' onclick=\"document.getElementById('dict').style.display='none'\" value='取消'/></td></tr></table>";
    document.getElementById("dict").innerHTML=resultuser;
    document.getElementById('dict').style.display = "block";
    document.getElementById('dict').style.width=$('viewuserbases').offsetWidth+aTag.offsetWidth;
    var pos=getAbsPosition(aTag);
    document.getElementById('dict').style.position="absolute";	
	document.getElementById('dict').style.left=pos[0]-$('viewuserbases').offsetWidth;
    document.getElementById('dict').style.top=pos[1]+aTag.offsetHeight-1;
    if(navigator.appName.indexOf("Microsoft")!= -1){
	    var objdiv=document.getElementById("dict");
	    var w = objdiv.offsetWidth;
		var h = objdiv.offsetHeight;
		var ifrm = document.createElement('iframe');
		ifrm.src = 'javascript:false';
		ifrm.style.cssText = 'position:absolute; visibility:inherit; top:0px; left:0px; width:' + w + 'px; height:' + h + 'px; z-index:-1; filter: \'progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)\'';
		objdiv.appendChild(ifrm);
	}
} 

function selectallcheckbox(o){
	var backdatebox=document.getElementsByName("backdatebox");
	for(var i=0;i<backdatebox.length;i++){
		var obj=backdatebox[i];
		obj.checked=o.checked;
	}
}
function selectcheckbox(){
	var backdatebox=document.getElementsByName("backdatebox");
	var userbases=document.getElementsByName("userbases")[0];
	userbases.value="";
	var veiwuserbases=document.getElementsByName("viewuserbases")[0];
	veiwuserbases.value="";
	for(var i=0;i<backdatebox.length;i++){
		var obj=backdatebox[i];
		if(obj.checked){
			var tmp=obj.value.split("`");
			var viewuserbasesv=tmp[1];
			var userbasesv=tmp[0];
			if(userbases.value.length>0){
				userbases.value=userbases.value+"`"+userbasesv;
				veiwuserbases.value=veiwuserbases.value+";"+viewuserbasesv;
			}else{
				userbases.value=userbasesv;
				veiwuserbases.value=viewuserbasesv;
			}
		}
	}
}
   </SCRIPT>
   <hrms:themes />
<html:form action="/general/static/select_code"> 
    <table width="96%" border="0" cellspacing="1"  align="center" cellpadding="1" class="RecordRow" style="margin-left: 15px;margin-top: 5px;">
        <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="1">
	          	<bean:message key="workbench.stat.statsettitle"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead> 
   	   <tr style="display: none">
        <td align="center"  nowrap>
     	     <bean:message key="menu.base"/>
    	       <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                sql="staticFieldForm.dbcond" collection="list" scope="page"/>
            <html:select name="staticFieldForm" property="userbase" size="1">
            <html:options collection="list" property="dataValue" labelProperty="dataName"/>
           </html:select>&nbsp;
        <html:hidden name="staticFieldForm" property="querycond"/>  
	    </td>                  	        	        
    </tr>
    <tr>
       		<td align="left" width="35%" height="30"  nowrap>
     	    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="menu.base"/>
       			<input name=viewuserbases style="width: 300px; height:20px;" value='${staticFieldForm.viewuserbases }' readonly="readonly"><img id=imgid style="cursor:pointer; vertical-align:inherit" src="/images/select.jpg" onmouseover="this.src='/images/selected.jpg'" onmouseout="this.src='/images/select.jpg'" onclick="addDict(this,event,'hidcategories');">
       			<input name=userbases type="hidden" value='${staticFieldForm.userbases }' />
       		</td>
       </tr>
     <tr>
      <td align="left"> 
         <div id="treemenu" style="height:180px;width:318px;overflow:auto;border: #aeac9f 1pt solid;margin-left: 83px;"> 
           <SCRIPT LANGUAGE=javascript>    
              <bean:write name="staticFieldForm" property="treeCode" filter="false"/>
           </SCRIPT>
          </div>          
         </td>
         </tr>   
        <tr style="height: 5px;">
         <td align="center"  nowrap>&nbsp;</td>
         </tr>
    </table>
    <table width="96%" border="0" cellspacing="1"  align="center" cellpadding="1">
    	<tr style="height: 35px;">
         <td align="center"  nowrap>
         <input type="button" name="btncance2" value="<bean:message key="button.ok"/>" class="mybutton" onclick="return savecode();"> 
	       <input type="button" name="btncancel" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="winClose();">  
        </td>
       </tr>
    </table>
</html:form>
<script language="JavaScript">
       if($('userbase').options.length<2){
   		$('imgid').style.display='none';
   }
function winClose(){
	if(getBrowseVersion())
		top.close();
	else
		parent.parent.winClose();
}
if(!getBrowseVersion()){//兼容非IE浏览器样式问题  wangb 20180803 bug 39379
	var viewuserbases = document.getElementsByName('viewuserbases')[0];
	viewuserbases.style.height='20px';
	viewuserbases.style.marginTop='1px';
	var imgid = document.getElementById('imgid');
	imgid.style.verticalAlign = 'top';
} 
   
    </script>
<div id="dict" class='div_table'  style="display:none;z-index:+999;position:absolute;height:170px;overflow:hidden;background-color:#FFF"></div>
