<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.parameterSet.ParameterForm,java.util.ArrayList,com.hrms.struts.taglib.CommonData"%>
<%@page import="com.hrms.struts.constant.SystemConfig,com.hrms.frame.codec.SafeCode"%>
<html>
<hrms:themes></hrms:themes>
  <head>
  <%
        ParameterForm parameterForm2 = (ParameterForm)session.getAttribute("parameterForm2");
        String path=parameterForm2.getPath();
        if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
       {
      	  path=session.getServletContext().getResource("/UserFiles").getPath();//.substring(0);
          if(path.indexOf(':')!=-1)
      	  {
	    	 path=path.substring(1);   
      	  }
     	  else
      	  {
	    	 path=path.substring(0);      
      	  }
          int nlen=path.length();
    	  StringBuffer buf=new StringBuffer();
     	  buf.append(path);
  	      buf.setLength(nlen-1);
   	      path=buf.toString();
   	      path=SafeCode.encode(path);
      }
        UserView userView = (UserView)session.getAttribute(WebConstant.userView);
    	String bosflag = userView.getBosflag();
   %>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/module/utils/js/createWindow.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
  <SCRIPT language="javascript">
  function isIE() { //ie?  
	    if (!!window.ActiveXObject || "ActiveXObject" in window)  
	        return true;  
	    else  
	        return false;  
	 }
	 
  function getInfo(){
  var ret = new Object();
  for(var i=0;i<parameterForm2.orgFieldIDs.options.length;i++){
  if(parameterForm2.orgFieldIDs.options[i].selected){
    ret.orgFieldIDs = parameterForm2.orgFieldIDs.options[i].value;
    ret.orgFieldIDsView = parameterForm2.orgFieldIDs.options[i].text;
  }
  }
 for(var j=0;j<parameterForm2.contentType.options.length;j++){
  if(parameterForm2.contentType.options[j].selected){
    ret.contentType = parameterForm2.contentType.options[j].value;
    ret.contentTypeView = parameterForm2.contentType.options[j].text;
    }
    }
  	window.returnValue=ret;
  	
  	if(window.parent.parent.me){//针对新招聘
	  	window.parent.parent.me.setCallBack({returnValue:ret});
	    window.parent.parent.Ext.getCmp('window').close();
   	}else{
		window.close();
   	}
  }
  function showEditPage(n,id){
        if(parameterForm2.orgFieldIDs.value==''||parameterForm2.contentType.value==''){
        alert(NOUNIT_CONTENT_FIELD+"!");
        return;
        }
        var emt = document.getElementById(id);
        if(emt.options.length==0||emt.options.length==1)
        {
            //alert("系统未建立内容形式指标");
           // return;
        }
        var type="";
        for(var i=0;i<emt.options.length;i++)
        {
           if(emt.options[i].selected)
           {
              type=emt.options[i].value;
              break;
           }
        }
        if(type.length==0)
        {
           alert(SELECT_CONTENT_TYPE+"!");
           return;
        }
        var dw="";
        var dh = "";
        if(type=='0')
        {
             dw="540px";
             dh="200px";
        }
        if(type=='1')
        {
             dw="800px";
             dh="750px";
        }
        var dl=(window.screen.width-parseInt(dw))/2;
        var dt=(window.screen.height-parseInt(dh))/2;
        var thecodeurl="/hire/parameterSet/configureParameter.do?b_edit=edit`codeitemid="+n+"`type="+type; 
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
         var values="";
         if(Ext.isChrome){//chrome浏览器
  		    values=window.open(iframe_url, "", 
  	               "width="+dw+"px,height="+dh+"px,top="+dt+",left="+dl+",resizable=no,center=yes,scroll=yes,location=no,status=no");
  	   	 }else if(!isIE()&&!Ext.isChrome){//非ie和chrome  主要针对火狐和safari中弹窗的位置
  		    values=window.open(iframe_url, "", 
  	               "width="+dw+"px,height="+dh+"px,screenY="+dt+",screenX="+dl+",resizable=no,center=yes,scroll=yes,location=no,status=no");
  	   	 }else{
  		   values=window.showModalDialog(iframe_url,null,"dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
  		   setEditValue(values);
  	   	 }
         /*me.openWindow({
            id:'window1',
 			title:'单位内容编辑',
 			width:dw,
 			height:dh,
 			url:thecodeurl,
 			callBack:"setEditValue()"
 		});*/	
}
  function setEditValue(values){
	  if(values!=null)
      {
        parameterForm2.action="/hire/parameterSet/configureParameter.do?b_orgIntro=inti";
        parameterForm2.submit();
      }
  }
function save(type){
var orgFieldIDs;
var contentType;
 for(var i=0;i<parameterForm2.orgFieldIDs.options.length;i++){
  if(parameterForm2.orgFieldIDs.options[i].selected){
   if(parameterForm2.orgFieldIDs.options[i].value ==""||parameterForm2.orgFieldIDs.options[i].value =="       "){
     if(type=="2")
     {
          alert(SELECT_UNIT_FIELD+"!");
     }
     return;
    }
    orgFieldIDs=parameterForm2.orgFieldIDs.options[i].value;
    }
    }
  for(var j=0;j<parameterForm2.contentType.options.length;j++){
  if(parameterForm2.contentType.options[j].selected){
  if(parameterForm2.contentType.options[j].value =="" || parameterForm2.contentType.options[j].value =="       "){
  if(type=="2")
  {
      alert(SELECT_CONTENT_TYPE+"!");
  }
  return;
  }
  contentType=parameterForm2.contentType.options[j].value;
  }
  }
  parameterForm2.action="/hire/parameterSet/configureParameter/orgIntroList.do?b_save=save&orgFieldIDs="+orgFieldIDs+"&contentType="+contentType;
  parameterForm2.submit();
  
  }
  function col(){
	  if(window.parent.parent.me)
	  	  window.parent.parent.Ext.getCmp('window').close();
	  else
		  window.close();
  }
  function query(type)
  {  
    
        parameterForm2.action="/hire/parameterSet/configureParameter.do?b_orgIntro=inti&isVisible=0&type="+type;
        parameterForm2.submit();
  }
  
  
  
	function backup()
	{
	  jinduo(1);
	  var hashvo=new ParameterSet();
      hashvo.setValue("path","<%=path%>");
	
     var request=new Request({method:'post',asynchronous:true,onSuccess:backup_ok,functionId:'3000000189'},hashvo);			   	
	}
	function backup_ok(outparameters)
   {
     closejinduo(1);
     var outName=outparameters.getValue("filename");
     if(outName=="1")
     {
         alert(BACKUP_FILE_NOT_EXIST+"!");
         return;
     }
     var name=outName;
     //xus 20/4/29 vfs 改造 
     var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"zip");
//      var win=open("/servlet/DisplayOleContent?filename="+name,"zip");
    }
	function Reduction()
	{
		/**
		*许硕 传值判断创建的是否为window对象（iswindow="+isWindow）
		*16/09/23
		**/
	   var theurl="/hire/parameterSet/configureParameter/select_reduction_file.do?b_reduction=init`isclose=1";
	   var iframe_url="/gz/templateset/tax_table/iframe_tax.jsp?src="+$URL.encode(theurl);
      // var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=350px;dialogHeight=150px;resizable=yes;status=no;");  
		/*
       var gloleft = (window.screen.availWidth-540-10)/2;//计算窗口距离屏幕左侧的间距  540是窗口宽度，10是边框大小
  	   var glotop = (window.screen.availHeight-400-30)/2;//窗口距离屏幕上方的间距         400是窗口高度，30是边框和标题栏大小(20)

  	   var values="";
  	   var width=450;
  	   var height=150;
  	   var aleft = Ext.isEmpty(width)?gloleft:(2*gloleft-width+540)/2;
  	   var atop = Ext.isEmpty(height)?glotop:(2*glotop-height+400)/2;
  	   if(Ext.isChrome){//chrome浏览器
  		   values=window.open(iframe_url, "", 
  	               "width="+width+"px,height="+height+"px,top="+atop+",left="+aleft+",resizable=no,center=yes,scroll=no,location=no,status=no");
  	   }else if(!isIE()&&!Ext.isChrome){//非ie和chrome  主要针对火狐和safari中弹窗的位置
  		   values=window.open(iframe_url, "", 
  	               "width="+width+"px,height="+height+"px,screenY="+atop+",screenX="+aleft+",resizable=no,center=yes,scroll=no,location=no,status=no");
  	   }else{
  		   values=window.showModalDialog(iframe_url,null,"dialogWidth:350px; dialogHeight:150px;resizable:no;center:yes;scroll:no;status:no");
  	   }
  	   */
  	   //改用EXT 弹窗 wangb 20190522 bug 48173
  	   var iHeight='100%';
  	   if(getBrowseVersion() || getBrowseVersion()!=10)
  	       iHeight='90%';	  
  	   Ext.create("Ext.window.Window",{
		    	id:'huanyuan',
		    	width:470,
		    	height:230,
		    	title:'',
		    	//resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='"+iHeight+"' width='100%' src='"+iframe_url+"'></iframe>"
	   }).show();	
	  //window.open(theurl);
	  
	   //if(objlist == null)
	  // return;
	  // var obj=new Object();
      // obj.dir=objlist.dir;
      // jinduo(2);
      // var hashVo=new ParameterSet();
       //hashVo.setValue("dir",obj.dir);
       //hashVo.setValue("path","");
      // var In_parameters="flag=1";
      // var request=new Request({method:'post',asynchronous:true,parameters:In_parameters,onSuccess:export_in_tax_ok,functionId:'3000000188'},hashVo);			   	
	}
	function export_in_tax_ok(outparameters)
	{
	   closejinduo(2);
	}
	function closejinduo(type){
	   var waitInfo;
	if(type==1)
	     waitInfo=eval("wait");
	else
	     waitInfo = eval("wait2");
	   waitInfo.style.display="none";
     }
function jinduo(type){
	var x=document.body.scrollLeft+event.clientX-120;
    var y=document.body.scrollTop+event.clientY-300; 
	var waitInfo;
	if(type==1)
	     waitInfo=eval("wait");
	else
	     waitInfo = eval("wait2");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}
  </SCRIPT>
  </head>
  <body>
  <base id="mybase" target="_self">
 <html:form action="/hire/parameterSet/configureParameter/nextPage" style="margin-right: 4px">
 <div   id="wait" style='position:absolute;top:285;left:120;display:none;width:285px;heigth:120px'>
 
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
			
				<td class="td_style" height=24>
					<bean:message key="hire.backuping.waiter"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
		    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
		    </iframe>	
	</div>																							<!-- 系统管理，参数设置，hr页面，内容与顶部之间间隔10px   jingq  add 2014.12.23 -->
    <table style="width:100%;" border='0' align="center" class="RecordRow" cellpadding="0" cellspacing="0" <%if(!"hcm".equals(bosflag)){ %>style="margin-top:10px;"<%} %>>
    <thead>
            <tr> 
              <td align="left"class="TableRow" nowrap><bean:message key="hire.appoint.field"/>&nbsp; </td>
            </tr>
     </thead>
    	<TR><td>
    	<table border='0'>
    	<tr height="30px;">
    	<td align='right' width='25%' style="padding-right:5px;"><bean:message key="hire.select.unitfield"/></td>
    	<td align='left' width='75%' >
    		<hrms:optioncollection name="parameterForm2" property="orgFieldList" collection="list" />
			<html:select name="parameterForm2" property="orgFieldIDs" size="1" style="width:100px"  onchange="save('1');" >
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select><bean:message key="hire.unit.baseset"/>
    	</td>
    	<td width="60%">&nbsp;</td>
    	</TR>
    	<TR height="30px;">
    	<td align='right' width='25%' style="padding-right:5px;"><bean:message key="hire.content.type"/></td>
    	<td align='left' width="75%">  
    		<hrms:optioncollection name="parameterForm2" property="contentTypeList" collection="list" />
			<html:select name="parameterForm2" property="contentType" size="1" style="width:100px"  onchange="save('2');" styleId="aa">
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select><bean:message key="hire.unitbase.43"/>
    	</td><td width="60%">&nbsp;</td>
    	</TR>
    	</table>
    	</td>
    	</TR>
    </table>
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
    <tr>
    <td align="center" style="padding-top:10px;">
    <div style="overflow:auto;width:100%;height:100%;" >
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
	  <thead>
	  <tr>
           			<td colspan="4" align="left" class="RecordRow" nowrap>
           			<table cellpadding="0" cellspacing="0" border="0"><tr><td>
           			<bean:message key="hire.unit.query"/>
           			<input type="text" name="orgName" value="${parameterForm2.orgName}" size="20px" class="text4"/>
           			<input type="hidden" name="orgId" value=""/>
           			<input type="hidden" name="hiddenOrgId" value="${parameterForm2.hiddenOrgId}"/>
           			<img src="/images/code.gif" border="0" align="absmiddle" onclick="openInputCodeDialogText('UM','orgName','orgId');"/>
           			</td><td align="center">
           			<input type="button" class="mybutton" value="查 询" onclick="query('0');" />
           			</td><td align="center">
           			<input type="button" class="mybutton" value="全 部" onclick="query('all');"/>
           			</td></tr></table>
           			</td> 
           			
       </tr>
   	
           <tr>
           			<td align="center" class="TableRow" nowrap> <bean:message key="lable.hiremanage.org_id"/></td> 
           			<td align="center" class="TableRow" nowrap> <bean:message key="hire.content.form"/></td>
           			<td align="center" class="TableRow" nowrap> <bean:message key="lable.channel_detail.content"/></td>
           			<td align="center" class="TableRow" nowrap> <bean:message key="system.infor.oper"/> </td>
           </tr>
      </thead>
      
      <% int i=0; %>
 	 <hrms:extenditerate id="element" name="parameterForm2" property="orgListform.list" indexes="indexes"  pagination="orgListform.pagination" pageCount="10" scope="session">
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
        %>
         <td align="left" class="RecordRow" width="30%" nowrap>
         <logic:equal name="element" property="codesetid" value="UM">
         <img src="/images/dept.gif" border="0"/>
         </logic:equal>
         <logic:equal name="element" property="codesetid" value="UN">
         <img src="/images/unit.gif" border="0"/>
         </logic:equal>
         &nbsp;<bean:write name="element" property="codeitemdesc"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" width="25%" nowrap>
           <hrms:optioncollection name="parameterForm2" property="contentTList" collection="list" />
			<html:select name="element" styleId='<%="a_"+i%>' property="contentTypeValue" size="1" style="width:100px;">
				   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>


         </td>
         <td align="left" class="RecordRow" width="25%" style="word-break:break-all;">
         &nbsp;<bean:write name="element" property="content"/>&nbsp;
         </td>
         <td align="center" class="RecordRow" width="20%" nowrap>
     <img src="/images/edit.gif" onclick="showEditPage('<bean:write name="element" property="codeitemid"/>','<%="a_"+i%>');" style="cursor:hand"/>
         </td>
            </tr>	
            <%
          i++;          
          %> 	    
	</hrms:extenditerate> 
    </table>
    </td>
    </tr>
    <tr>
    <td align="center">
    <table  style="width:100%;" align="center" class="RecordRowP">
		<tr>
		   <td valign="bottom" class="tdFontolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   <bean:write name="parameterForm2" property="orgListform.pagination.current" filter="true"/>
					<bean:message key="label.page.sum"/>
		   <bean:write name="parameterForm2" property="orgListform.pagination.count" filter="true"/>
					<bean:message key="label.page.row"/>
		   <bean:write name="parameterForm2" property="orgListform.pagination.pages" filter="true"/>
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="parameterForm2" property="orgListform.pagination" nameId="orgListform" propertyId="orgListProperty">
		   </hrms:paginationlink>
		   </td>
		</tr>
   	  
</table> 
</div>
</td>
</tr>
<logic:notEqual value="1" name="parameterForm2" property="isVisible">
<tr>
<td align="center">

  <table  style="width:580px;" align="center">
		<tr>
		  <td style="padding-top:3px;" align="center">
		  <input type="button" value="<bean:message key="hire.backup.file"/>" class="mybutton" onclick="backup();"/>
		  <input type="button" value="<bean:message key="hire.reduction.file"/>" class="mybutton" onclick="Reduction();"/>
		   </td>
		</tr>
   	  
</table> 
</td>
</tr>
</logic:notEqual>
</table>
<logic:equal value="1" name="parameterForm2" property="isVisible">
 <table  width="80%" align="center">
 <tr class="list3">
            <td align="center" colspan="2">
         	<html:button styleClass="mybutton" property="b_center" onclick="getInfo();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="col();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>
          </tr>   
  </table>  
  </logic:equal>
  </html:form>  
  </body>
</html>
