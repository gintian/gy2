<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>
<script type='text/javascript' src='../../module/utils/js/template.js' ></script>
<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
</style>
<script language="javascript">
/*
function window.onload()
{
	var d1 = new Array(Layer1);
	
	for( var m = 0;m < d1.length;m++)
	{
		var shtml=d1[m].innerHTML;
		var ifm=document.createElement("<iframe src=javascript:false; frameborder=0 marginheight=0 marginwidth=0 hspace=0 vspace=0 scrolling=no></iframe>")
		ifm.style.width=d1[m].offsetWidth
		ifm.style.height=d1[m].offsetHeight
		ifm.name=ifm.uniqueID
		d1[m].innerHTML=""
		d1[m].appendChild(ifm)
		window.frames[ifm.name].document.write(iframeCss.outerHTML+"<body leftmargin=0 topmargin=0>"+shtml+"</body>")
	}
}

function show(eventdiv,showdiv)
{
	with(eventdiv)
	{
		x=offsetLeft;
		y=offsetTop;
		objParent=offsetParent;
		while(objParent.tagName.toUpperCase()!= "BODY")
		{
			x+=objParent.offsetLeft;
			y+=objParent.offsetTop;
			objParent = objParent.offsetParent;
		}
		y+=offsetHeight-1;
		
	}

	with(showdiv.style)
	{
		pixelLeft=x+20;
		pixelTop=y;
		visibility='';
	}
}
function hide(hidediv)
{
	hidediv.style.visibility='hidden';
}
*/
function subs(sub){
	var name = sub;
	if(name==null){
		return;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("usename",name);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnExportOk,functionId:'1010010072'},hashvo);
	
}
function returnExportOk(outparameters)
	{
		var outName=outparameters.getValue("outName");
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
	}
function guideexcel(){
	var theURL ="/system/security/rolesearch.do?br_importexcel=link&encryptParam=<%=PubFunc.encrypt("opt=0")%>";
	var iframe_url="/gz/templateset/tax_table/iframe_tax.jsp?src="+theURL;
	var dw=400,dh=180,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	
	modalDialog.showModalDialogs(iframe_url,"导入",{id:'extDialogs',width:dw,height:dh},commonWinSuccess);
	return;
	var objlist =window.showModalDialog(iframe_url,null,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	if(objlist==null)
		return;
	else{
		window.location.href="/system/security/rolesearch.do?b_query=link";
	}
}

function check()
{
	var a = $F('ordername');
	var b = $F('ordertype');
	sysForm.action =  "/system/security/rolesearch.do?b_query=link&order_name="+a+"&order_type="+b;
	sysForm.submit();

}
function query()
{
	sysForm.action ="/system/security/rolesearch.do?b_query=link&encryptParam=<%=PubFunc.encrypt("isquery=1")%>";
	sysForm.submit();
}
function adjust_order(){
	//【7105】角色管理中，按照角色特称排序后，调整顺序界面显示不对。 jingq upd 2015.01.29
	//var order_name = $F('order_name');
	//var order_type = $F('order_type');
	target_url="/system/security/rolesearch.do?b_order=link";//`order_name="+order_name+"`order_type="+order_type;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url; 

	var config={id:'extDialogs',
		width:440,
		height:380,
		type:3,
		title:'调整显示顺序'
	};
	
	modalDialog.showModalDialogs(iframe_url,"排序",config,commonWinSuccess);
	
	
	//var return_vo= window.showModalDialog(iframe_url,null, 
	//"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	//if(return_vo!=null){
	//	window.location.href="/system/security/rolesearch.do?b_query=link";
	//}
}

function commonWinSuccess(return_vo){
	if(return_vo!=null){
		window.location.href="/system/security/rolesearch.do?b_query=link";
	}
}

function closeWin(){
	Ext.getCmp('extDialogs').close();
}

function purview()
{
	var str="";
	for(var i=0; i<document.sysForm.elements.length;i++){
		if(document.sysForm.elements[i].type=="checkbox"){
			if(document.sysForm.elements[i].checked==true)
			{
					if(document.sysForm.elements[i].name=="selbox")
						continue;
					str+=document.sysForm.elements[i].value+"/";
			}
		}
	}
	if(str.length==0){
		alert("请选择导出角色!");
		return;
	}else{
		var hashvo=new ParameterSet();
		hashvo.setValue("role",str);
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnExportOk1,functionId:'1010010076'},hashvo);
	}
}
function returnExportOk1(outparameters)
	{
		var outName=outparameters.getValue("outName");
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
	}
function other_save()
{
    var selected=0;
	for(var i=0; i<document.sysForm.elements.length;i++){
		if(document.sysForm.elements[i].type=="checkbox"){
			if(document.sysForm.elements[i].checked==true)
			{
					if(document.sysForm.elements[i].name=="selbox")
						continue;
					selected++;
			}
		}
	}
	if(selected<=0)
	{
	   alert("请选择角色,作为另存为对象！");
	   return false;
	}else if(selected>1)
	{
	  alert("只能选择一个角色,作为另存为对象！");
	  return false;
	}	
	target_url="/system/security/rolesearch.do?br_othername=link";
	//newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=300,width=596,height=354'); 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url; 

	modalDialog.showModalDialogs(iframe_url,"另存",{id:'extDialogs',width:350,height:150,type:'3',title:'角色另存为'},other_save_success);

	//var return_vo= window.showModalDialog(iframe_url,null, 
	//"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	//if(return_vo!=null){
	//   sysForm.action =  "/system/security/rolesearch.do?b_othersave=link&other_name="+return_vo.name;
	//   sysForm.submit();
	//}	
}

function other_save_success(return_vo){
	if(return_vo!=null){
		   sysForm.action =  "/system/security/rolesearch.do?b_othersave=link&other_name="+$URL.encode(return_vo.name);
		   sysForm.submit();
	}	
}
function roledetailed(roleid,rolename){
	//sysForm.action="/system/security/rolesearch.do?b_detailed=link&roleid="+roleid;
	//sysForm.submit();
	//var theurl="/system/security/roledetail.do?b_detailed=link&roleid="+roleid+"&rolename="+rolename;
	rolename=getEncodeStr(rolename);
	var theurl="/system/security/roledetail.do?b_detailed=link`roleid="+roleid+"`rolename="+rolename;
	theurl = $URL.encode(theurl);
	var return_vo;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	/*弹框浏览器兼容 guodd 2019-03-23*/
	if(window.showModalDialog){
		var dw=660,dh=520,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
    	return_vo= window.showModalDialog(iframe_url,1, 
        	  "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	}else{
		var dw=660,dh=530,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
		window.open(iframe_url,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+',left='+dl+',width='+dw+',height='+dh);
	}
}

document.body.onkeydown = function(e) {
	if (13 == e.keyCode) {
		e.preventDefault ? e.preventDefault() : e.returnValue = false;
	};
}
</script>
<html:form action="/system/security/rolesearch">
<!--
<div id=Layer1 onmouseover="style.visibility=''" onmouseout="style.visibility='hidden'" style="visibility:hidden;HEIGHT: 11px; POSITION: absolute; WIDTH: 160px; Z-INDEX: 1; top: -1px; left:0pt">
  <table width="100%"  border="0" cellpadding="1" cellspacing="1" >
    <tr>
      <td bgcolor="#EFEFEF">
	<table width="100%" border="0" cellpadding="０" cellspacing="0" class="ListTable">
          <tr class="TableRow">
            <td height="20">&nbsp;&nbsp;<hrms:link href="/system/security/assignpriv.do?b_query=link"  function_id="070101" styleClass="head" target="_parent"><bean:message key="menu.function"/></hrms:link></td>
          </tr>
          <tr class="TableRow">
            <td height="20">&nbsp;&nbsp;<hrms:link href="/system/security/assignpriv.do?b_query=link"  function_id="070102" styleClass="head" target="_parent"><bean:message key="menu.base"/></hrms:link></td>
          </tr>
          <tr class="TableRow">
            <td height="20">&nbsp;&nbsp;<hrms:link href="/system/security/assignpriv.do?b_query=link"  function_id="070102" styleClass="head" target="_parent"><bean:message key="menu.manage"/></hrms:link></td>
          </tr>
          <tr class="TableRow">
            <td height="20">&nbsp;&nbsp;<hrms:link href="/system/security/assignpriv.do?b_query=link"  function_id="070102" styleClass="head" target="_parent"><bean:message key="menu.table"/></hrms:link></td>
          </tr>
          <tr class="TableRow">
            <td height="20">&nbsp;&nbsp;<hrms:link href="/system/security/assignpriv.do?b_query=link"  function_id="070102" styleClass="head" target="_parent"><bean:message key="menu.field"/></hrms:link></td>
          </tr>
          <tr class="TableRow">
            <td height="20">&nbsp;&nbsp;<hrms:link href="/system/security/assignpriv.do?b_query=link"  function_id="070102" styleClass="head" target="_parent"><bean:message key="menu.rule"/></hrms:link></td>
          </tr>
       </table>
     </td>
    </tr>
  </table>
</div>
--><!-- 【6726】进入角色管理模块，界面显示缺线，不对。 jingq upd 2015.01.15 -->
<table width="80%" border="0" align="center" cellpadding="0" cellspacing="0">
		<tr align="right" style="height:30px;">
			<td align="left"  class="tdFontcolor" colspan="5">
				名称
				<input type=text name='qname' size='15' class="inputtext">
				&nbsp;角色特征
				<html:select name="sysForm" property="qroleproperty">
                 	<html:optionsCollection property="propertylist" value="dataValue" label="dataName"/>                              	   
                </html:select>
				&nbsp;
				<input type=button value="查询" class="mybutton" onclick="query();"/>
			</td>
			<td align="right"  class="tdFontcolor" colspan="3">
				<FONT color='black'>按</FONT>
				<bean:define id="selectordername" name="sysForm" property="order_name"/>
				<bean:define id="selectordertype" name="sysForm" property="order_type"/>
				<select  name="ordername" size="1"  style="width:75">
					<option value="" ></option>
					<option value="role_name" <%if(selectordername.equals("role_name")){ %> selected<%} %> ><bean:message key="column.name" /></option>
					<option value="role_property" <%if(selectordername.equals("role_property")){ %> selected<%} %> ><bean:message key="label.role.property" /></option>
					<option value="role_desc" <%if(selectordername.equals("role_desc")){ %> selected<%} %>><bean:message key="column.desc" /></option>
				</select>
				<select name="ordertype" size="1"  style="width:50">
					<option value="asc" <%if(selectordertype.equals("asc")){ %> selected<%} %>><bean:message key="label.query.sortBase" /></option>
					<option value="desc" <%if(selectordertype.equals("desc")){ %> selected<%} %>><bean:message key="label.query.sortDesc" /></option>
				</select>
				<input type="button" name="b_quickorder" class="mybutton" value="<bean:message key="label.zp_exam.sort" />" onclick="check()" />
			</td>
		</tr>
	</table>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
				<input type="checkbox" name="selbox" onclick="batch_select(this,'roleListForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.role.property"/>&nbsp;
	    </td>	    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.desc"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
        <td align="center" class="TableRow" nowrap>
			<bean:message key="label.manage"/>            	
	    </td>	
        <td align="center" class="TableRow" nowrap>
			<bean:message key="button.resource.assign"/>            	
	    </td>		    
		<hrms:priv func_id="30035,080102">  
            <td align="center" class="TableRow" nowrap>
				<bean:message key="label.priv.mx"/>            	
	   		</td>				
		</hrms:priv>		        		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="sysForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="${sysForm.pagerows}" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap>
            <!--
                 <logic:equal name="element" property="string(status)" value="1">
     		   <hrms:checkmultibox name="sysForm" property="roleListForm.select" value="true" indexes="indexes"/>&nbsp;
            	 </logic:equal> 
            	 -->
            	 <bean:define id="role_id" name='element' property='string(role_id)'/>    	
    		 <hrms:checkmultibox name="sysForm" property="roleListForm.select" value="${role_id}" indexes="indexes"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<a href="###" onclick="roledetailed('${role_id}','<bean:write name="element" property="string(role_name)" filter="true"/>');"><bean:write name="element" property="string(role_name)" filter="true"/></a>&nbsp;
	    </td>
            <td align="left" class="RecordRow" 	style="word-break:break-all;">
                 <logic:equal name="element" property="string(role_property)" value="-1">              
                    &nbsp;<bean:message key="label.role.general"/>&nbsp;
            	 </logic:equal>     
                 <logic:equal name="element" property="string(role_property)" value="0">              
                    &nbsp;<bean:message key="label.role.sys"/>&nbsp;
            	 </logic:equal> 
                 <logic:equal name="element" property="string(role_property)" value="1">              
                    &nbsp;<bean:message key="label.role.leader"/>&nbsp;
            	 </logic:equal> 
                 <logic:equal name="element" property="string(role_property)" value="2">              
                    &nbsp;<bean:message key="label.role.train"/>&nbsp;
            	 </logic:equal> 
                 <logic:equal name="element" property="string(role_property)" value="3">              
                    &nbsp;<bean:message key="label.role.kq"/>&nbsp;
            	 </logic:equal> 
                 <logic:equal name="element" property="string(role_property)" value="4">              
                    &nbsp;<bean:message key="label.role.per"/>&nbsp;
            	 </logic:equal> 
                 <logic:equal name="element" property="string(role_property)" value="5">              
                    &nbsp;<bean:message key="label.role.employ"/>&nbsp;
            	 </logic:equal>  
                 <logic:equal name="element" property="string(role_property)" value="6">              
                    &nbsp;<bean:message key="label.role.uleader"/>&nbsp;
            	 </logic:equal>
                 <logic:equal name="element" property="string(role_property)" value="7">              
                    &nbsp;<bean:message key="label.role.gleader"/>&nbsp;
            	 </logic:equal>  
                 <logic:equal name="element" property="string(role_property)" value="8">              
                    &nbsp;<bean:message key="label.role.zp"/>&nbsp;
            	 </logic:equal> 
                 <logic:equal name="element" property="string(role_property)" value="9">              
                    &nbsp;<bean:message key="label.role.fleader"/>&nbsp;
            	 </logic:equal>  
                 <logic:equal name="element" property="string(role_property)" value="10">              
                    &nbsp;<bean:message key="label.role.sleader"/>&nbsp;
            	 </logic:equal>
                 <logic:equal name="element" property="string(role_property)" value="11">              
                    &nbsp;<bean:message key="label.role.tleader"/>&nbsp;
            	 </logic:equal>
                 <logic:equal name="element" property="string(role_property)" value="12">              
                    &nbsp;<bean:message key="label.role.ffleader"/>&nbsp;
            	 </logic:equal>            
                 <logic:equal name="element" property="string(role_property)" value="13">              
                    &nbsp;<bean:message key="label.role.allleader"/>&nbsp;
            	 </logic:equal> 
                 <logic:equal name="element" property="string(role_property)" value="14">              
                    &nbsp;<bean:message key="label.role.self"/>&nbsp;
            	 </logic:equal> 
            	 <logic:equal name="element" property="string(role_property)" value="15">              
                    &nbsp;<bean:message key="label.role.sycrecy"/>&nbsp;
            	 </logic:equal> 
            	 <logic:equal name="element" property="string(role_property)" value="16">              
                    &nbsp;<bean:message key="label.role.auditor"/>&nbsp;
            	 </logic:equal>             	              	   	             	            	              	              	            	               	             	             	             	             	             	             	               
	    </td>
		
            <td align="left" class="RecordRow" 	style="word-break:break-all;">
                    &nbsp;<bean:write  name="element" property="string(role_desc)" filter="false"/>&nbsp;
	    </td>
        <%
        	RecordVo vo = (RecordVo)pageContext.getAttribute("element");
        	String roleid = vo.getString("role_id");
        	String role_name = vo.getString("role_name");
        	String role_property = vo.getString("role_property");
         %>    
            <td align="center" class="RecordRow" nowrap>
            	<a href="/system/security/viewrole.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_roleid="+roleid)%>"><img src="/images/edit.gif" border=0></a>
	    </td>
            <td align="center" class="RecordRow" nowrap>
            <!-- 
		<a href="/system/security/assignpriv.do?b_query=link&a_flag=1&a_tab=funcpriv&role_id=<bean:write name="element" property="string(role_id)" filter="true"/>"><img src="/images/assign_priv.gif" border=0></a>
		 -->
			<a href="/system/security/assignpriv_tab.do?br_query=link&encryptParam=<%=PubFunc.encrypt("user_flag=1&a_tab=funcpriv&role_id="+roleid+"&rp="+role_property+"&role_name="+role_name)%>"><img src="/images/assign_priv.gif" border=0></a>
	    </td>	
        <td align="center" class="RecordRow" nowrap>
		<a href="/system/security/assign_resource.do?encryptParam=<%=PubFunc.encrypt("fromflag=1&flag=1&roleid="+roleid+"&role_name="+role_name)%>"><img src="/images/book.gif" border=0></a>
	    </td>		    
		<hrms:priv func_id="30035,080102">  
            <td align="center" class="RecordRow" nowrap>
       		   <a href="/system/options/userpopedom.do?b_query=link&encryptParam=<%=PubFunc.encrypt("operatorflag=2&role_id="+roleid+"&role_flag=1")%>&callback=true"><img src="/images/viewpriv.gif" border=0></a>            	
	   		</td>				
		</hrms:priv>		        		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="80%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">

		            <hrms:paginationtag name="sysForm" pagerows="${sysForm.pagerows}" property="roleListForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
					
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="sysForm" property="roleListForm.pagination"
				nameId="roleListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="80%" align="center">
          <tr>
            <td align="center" height="35px;">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifmsdel();">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	<input type="button" name="b_osave" class="mybutton" value=另存为 onclick="other_save()" />
	 	<input type="button" name="b_order" class="mybutton" value=<bean:message key="button.movenextpre"/> onclick="adjust_order()" />
        	<input type='button' value=<bean:message key="kjg.title.quanxianmuban"/> onclick='subs("excel")' class="mybutton">
        	<input type='button' value=<bean:message key="kjg.title.daochuquanxian"/> onclick='purview()' class="mybutton" />
        	<input type='button' value=<bean:message key="kjg.title.daoruquanxian"/> onclick='guideexcel()' class="mybutton">
            </td>
          </tr>
</table>

</html:form>
