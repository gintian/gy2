<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/function.js"></script>
<!-- 引入ext 和代码控件      wangb 20180127 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<%
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String manager = userView.getUnitIdByBusi("4");
	int i = 0;
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
%>
<style>
<!--
.div_table {
	border-width: 1px;
	BORDER-BOTTOM: #aeac9f 1pt solid;
	BORDER-LEFT: #aeac9f 1pt solid;
	BORDER-RIGHT: #aeac9f 1pt solid;
	BORDER-TOP: #aeac9f 1pt solid;
}

.tdFontcolor {
	text-decoration: none;
	Font-family: ;
	font-size: 12px;
	height: 12px;
	align: "center"
}
-->
</style>
<script language="javascript">
	
		   var date_desc;
  
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
     }
   }
	 function showDateSelectBox(srcobj)
   {
       
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
	        style.left=pos[0]-1+'px';
			style.top=pos[1]-1+srcobj.offsetHeight+'px';
			style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1+'px';
			
      }                 

   }
	
	
	
        var ccc=0;
        var temp =new Array();
        var b=0;
        var tex;
	function showSelect(outparamters)
	{ 
	  var blist= new Array();
	  var hashvo=new ParameterSet();
	  for (var m=0;m<ccc;m++)
	  {
	      var telist="factorlist["+m+"].hzvalue";
	      if(($(telist).value)==null)
	      {
	          var tlist="factorlist["+m+"].value";
	          blist[m]= $(tlist).value;
	      }else{
	      
	          blist[m]= $(telist).value;
	      
	        //alert(blist[m]);
	      }
	     
	  }
	   tex=outparamters.getValue("texts");
		var texh=tex.replace("+","%2B");
		
      // hashvo.setValue("texts",texh);
    // var  request=new Request({onSuccess:showSelect,functionId:'05301010009'},hashvo);
                var bliststr = blist.join(',');  
                bliststr = getEncodeStr(bliststr);
                target_url="/general/static/add_general.do?b_incept=link&texts="+texh+"&gvalue="+$URL.encode(bliststr);
                var next=$('selects');
                var dw=300,dh=150,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
                /**
                	add by sunming 2015-06-30 ie6下显示滚动条
                */
                if(isIE6()){
                	dh=180;
		       	}
		       	if(getBrowseVersion() && getBrowseVersion()!=10 ){//ie兼容性视图
                    dw-=20;
		        	var legendtitle=showModalDialog(target_url,'glWin',"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth="+dw+"px;dialogHeight="+dh+"px;resizable=no;status=no;");
                	var sss = new Option();
                	if(legendtitle==null)
                	{
                     	return; 
                	}else{
                
                  		sss.value=legendtitle;
                  		sss.text=tex;
                  		next.options[next.options.length]=sss;
                	}
		       	}else{//非IE浏览器使用Ext组件
		       		var dialog=[];dialog.dw=dw+10;dialog.dh=dh+50;dialog.iframe_url=target_url;
            		openWin(dialog);
		       	}
	}
//ext window 弹窗 方法
function openWin(dialog){
	    	var height = "100%";
	    	if(getBrowseVersion()==10){//ie11下高度100%会出现滚动条 所以设置为97% wangz 2019-03-05
				height = "97%";
			}
		    Ext.create("Ext.window.Window",{
		    	id:'statistical',
		    	width:dialog.dw,
		    	height:dialog.dh,
		    	title:'请设置',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height="+height+" width='100%' src='"+dialog.iframe_url+"'></iframe>"
		    }).show();	
}
//关闭ext window 弹窗   wangb 20180207 
function winClose(){
   Ext.getCmp('statistical').close();
}	
//ext 弹窗返回数据调用方法   wangb 20180207 bug 34602
function returnValue(legendtitle){
    winClose();
    var sss = new Option();
    if(legendtitle==null)
    {
       return; 
    }else{
       var next=$('selects');
       sss.value=legendtitle;
       sss.text=tex;
       next.options[next.options.length]=sss;
    }     
}
	
function submits(str)
{
      var tem,vos,i;
      vos= document.getElementsByName(str);
      if(vos==null)
  	  return false;
      tem=vos[0];

        for(i=0;i<tem.options.length;i++)
        {
        	tem.options[i].selected=true;
         	var no = new Option();
         	temp[i]=new Array(tem.options[i].text)
   	}   	
   	if(temp==null||temp.length<=0)
   	{
   	  alert("没有设置条件表达式!");
   	  return false;
   	}else
   	{
   	   staticFieldForm.mess.value=temp;
   	   staticFieldForm.action="/general/static/general_static.do?b_save=link";
       staticFieldForm.submit();
   	}  
}
function checkNext(str)
{
<logic:equal name="staticFieldForm" property="flist" value="1">
	if($F('userbases').length==0){
	alert('人员库不能为空');
	return false
	}
	</logic:equal>
      var tem,vos,i;
      vos= document.getElementsByName(str);
      if(vos==null)
  	  return false;
      tem=vos[0];

        for(i=0;i<tem.options.length;i++)
        {
        	tem.options[i].selected=true;
         	var no = new Option();
         	temp[i]=new Array(tem.options[i].text)
   	}   	
   	if(temp==null||temp.length<=0)
   	{
   	  alert("没有设置条件表达式!");
   	  return false;
   	}else
   	{
   	   staticFieldForm.mess.value=temp;
   	   staticFieldForm.action="/general/static/general_static.do?b_next=link";
       staticFieldForm.submit();
   	}
   	return true; 
} 
 function check()
	{
	   var expr=$F('texts');
	   if(expr==null||expr=="")
               return false
	   var hashvo=new ParameterSet();
	   hashvo.setValue("texts",expr);	
	   hashvo.setValue("sno",ccc);
	   /*var vosId= document.getElementsByName('hz');
	   var vosoper= document.getElementsByName('oper');
	   var vosFieldname=document.getElementsByName("itemid");  */
	   
	   var vosId=new Array();
	   var vosoper= new Array();
	   var vosFieldname= new Array();
	   for(var i=0;i<ccc;i++){
	   	vosId[i]=(document.getElementsByName('factorlist['+i+'].value')[0]);
	   	vosoper[i]=(document.getElementsByName('factorlist['+i+'].oper')[0]);
	   	vosFieldname[i]=(document.getElementsByName(i+'feildname')[0]);
	   }
	   //alert(vosId+":"+vosoper.length+":"+vosFieldname.length);
	   var arr=new Array();	   	   
	   if(vosId&&vosoper&&vosFieldname&&vosFieldname.length==vosId.length&&vosId.length==vosoper.length)
	   {
	       for(var r=0;r<vosId.length;r++)
	       {
	              var objId=vosId[r];
	              var objfieldname=vosFieldname[r];	
	              var value=objId.value;
	              var fieldname=objfieldname.value;
	              var log="";
	                
	              var objOper=vosoper[r];   
	              var oper="";
	              for(var i=0;i<objOper.options.length;i++)
                  {
                      if(objOper.options[i].selected)
                      {
                         oper=objOper.options[i].value;
                         break;
                      }
                  }     
                               
                  var oobj=new Object();
                  oobj.value=value;
                  oobj.oper=oper;
                  oobj.fieldname=fieldname;   
                  oobj.log=log;             
                  arr[r]=oobj;                  
	       }  
	   }		 
       hashvo.setValue("arr",arr); 
       hashvo.setValue("type","${staticFieldForm.infor_Flag}");
	   hashvo.setDataType("sno","string");	
       var  request=new Request({onSuccess:showSelect,functionId:'05301010010'},hashvo);

	}


 function addTxt(strtxt)
	{
		var expr_editor=$('texts');
                expr_editor.focus();
		var element = document.selection;
		if (element!=null) 
		{
		  var rge = element.createRange();
		  if (rge!=null)	
		  	   rge.text=strtxt;
	   }else{
	   			var word = expr_editor.value;
				var _length=strtxt.length;
				var startP = expr_editor.selectionStart;
				var endP = expr_editor.selectionEnd;
				var ddd=word.substring(0,startP)+strtxt+word.substring(endP);
		    	expr_editor.value=ddd;
        		expr_editor.setSelectionRange(startP+_length,startP+_length); 
	   }
	}
	
 function addEpre(strtxt) {
     vos = document.getElementsByName(strtxt);
     if (vos == null) 
         return false;
     
     left_vo = vos[0];
     for (i = left_vo.options.length - 1; i >= 0; i--) {
         if (left_vo.options[i].selected) {
             staticFieldForm.texts.value = left_vo.options[i].text;
             $('selects').options.remove(i);
         }
     }
 }
 	
 function validate()
	{
	    var tag=true; 
     <logic:iterate  id="element"    name="staticFieldForm"  property="factorlist" indexId="index"> 
        <logic:equal name="element" property="fieldtype" value="D">   
            var valueInputs=document.getElementsByName('<%="factorlist[" + index + "].value"%>');
               var dobj=valueInputs[0];
               tag= checkDate(dobj);    
	       if(tag==false)
	        {
	           dobj.focus();
	           return false;
	        }
        </logic:equal> 
     </logic:iterate>       	 
     return tag;

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
    resultuser+="<tr><td align='center' style='height:35px'><input type='button' value='确定' class='mybutton' onclick=\"selectcheckbox();document.getElementById('dict').style.display='none'\" />&nbsp;&nbsp;<input value='取消' class='mybutton' type='button' onclick=\"document.getElementById('dict').style.display='none'\"></td></tr></table>";/*超链接按钮改成button按钮  */
    document.getElementById("dict").innerHTML=resultuser;
    document.getElementById('dict').style.display = "block";
    document.getElementById('dict').style.width=$('viewuserbases').offsetWidth+aTag.offsetWidth;
    var pos=getAbsPosition(aTag);
    document.getElementById('dict').style.position="absolute";	
	document.getElementById('dict').style.left=pos[0]-$('viewuserbases').offsetWidth+'px';
    document.getElementById('dict').style.top=pos[1]+aTag.offsetHeight-1+'px';
    if(navigator.appName.indexOf("Microsoft")!= -1){
	    var objdiv=document.getElementById("dict");
	    var w = objdiv.offsetWidth;
		var h = objdiv.offsetHeight;
		var ifrm = document.createElement('iframe');
		ifrm.src = 'javascript:false';
		ifrm.style.cssText = 'display:none;position:absolute; visibility:inherit; top:0px; left:0px; width:' + w + 'px; height:' + h + 'px; z-index:-1; filter: \'progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)\'';
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
</script>
<hrms:themes />
<style>
<%if("hl".equalsIgnoreCase(bosflag)){ %>
.ListTableF {
	margin-top:10px;
}
<%}%>
.RecordRow_lr{
	border-left: 0;
}
</style>
<html:form action="/general/static/general_static">
	<html:hidden property="infor_Flag" />
	<html:hidden name="staticFieldForm" property="mess" />
	<table width="700px" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td valign="top">
				<table width="100%" height="484" border="0" align="center"
					cellpadding="0" cellspacing="0" class="ListTableF">
					<tr height="20">
						<td align="left" class="TableRow"><bean:message key="static.cstat" /></td>
					</tr>
					<tr>
						<td height="426" class="framestyle" valign="top">
							<table border="0" cellspacing="0" width="70%" class="ListTable"
								cellpadding="2" align="center" style="margin-bottom:3px;">
								<tr>
									<logic:equal name="staticFieldForm" property="flist" value="1">
										<tr style="display: none">
											<td align="left" nowrap class="tdFontcolor">
												<bean:message key="static.stor" />
												<html:select name="staticFieldForm" property="userbase"
													size="1">
													<html:optionsCollection property="alist" value="dataValue"
														label="dataName" />
												</html:select>
											</td>
										</tr>
										<tr>
											<td align="left" width="" height="30" nowrap>
												<bean:message key="menu.base" />
												<input name=viewuserbases
													style="width: 250px; height: 20px;"
													value='${staticFieldForm.viewuserbases }'
													readonly="readonly" class="inputtext"><img id="imgid"
													style="cursor: pointer; vertical-align: bottom;"
													src="/images/select.jpg"
													onmouseover="this.src='/images/selected.jpg'"
													onmouseout="this.src='/images/select.jpg'"
													onclick="addDict(this,event,'hidcategories');" align="middle">
												<input name=userbases type="hidden"
													value='${staticFieldForm.userbases }' />
											</td>
										</tr>
									</logic:equal>
									<logic:notEqual name="staticFieldForm" property="flist"
										value="1">
										<tr>
											<td>
												&nbsp;
											</td>
										</tr>
									</logic:notEqual>
								<tr>
									<td colspan="4">
										<table border="0" cellspacing="0" width="100%"
											class="ListTable1" cellpadding="0" align="center" rows="">
											<tr>
												<td align="center" nowrap class="TableRow">
													<bean:message key="label.query.number" />
													&nbsp;
												</td>
												<td align="center" nowrap class="TableRow">
													<bean:message key="static.target" />
												</td>
												<td align="center" nowrap class="TableRow">
													<bean:message key="static.relation" />
												</td>
												<td align="center" nowrap class="TableRow">
													<bean:message key="static.title" />
												</td>
											</tr>
											<logic:iterate id="element" name="staticFieldForm"
												property="factorlist" indexId="index">
												<script language="javascript"> 
               			ccc++
               		</script>
												<tr>
													<td align="center" class="RecordRow" nowrap>
														<%=i + 1%>
													</td>
													<td align="center" class="RecordRow" nowrap>
														<bean:write name="element" property="hz" />
														&nbsp;
														<input type="hidden" name="${index}feildname" id='itemid'
															value="<bean:write name="element" property="fieldname" />">
													</td>
													<td align="center" class="RecordRow" nowrap>
														<hrms:optioncollection name="staticFieldForm"
															property="operlist" collection="list" />
														<html:select name="staticFieldForm"
															property='<%="factorlist[" + index
												+ "].oper"%>'
															styleId="oper" size="1">
															<html:options collection="list" property="dataValue"
																labelProperty="dataName" />
														</html:select>
													</td>
													<!--日期型 -->
													<logic:equal name="element" property="fieldtype" value="D">
														<td align="left" class="RecordRow" nowrap>
															<html:text name="staticFieldForm"
																property='<%="factorlist[" + index
										+ "].value"%>' size="30"
																maxlength="10" styleId="hz" styleClass="text4"
																ondblclick="showDateSelectBox(this);" />
														</td>
													</logic:equal>
													<!--备注型 -->
													<logic:equal name="element" property="fieldtype" value="M">
														<td align="left" class="RecordRow" nowrap>
															<html:text name="staticFieldForm"
																property='<%="factorlist[" + index
										+ "].value"%>' size="30"
																styleId="hz"
																maxlength='<%="factorlist[" + index
										+ "].itemlen"%>'
																styleClass="text4" />
														</td>
													</logic:equal>
													<!--字符型 -->
													<logic:equal name="element" property="fieldtype" value="A">
														<td align="left" class="RecordRow" nowrap>
															<logic:notEqual name="element" property="codeid"
																value="0">
																<html:hidden name="staticFieldForm"
																	property='<%="factorlist[" + index
											+ "].value"%>'
																	styleId="hz" styleClass="text4" />
																<html:text name="staticFieldForm"
																	property='<%="factorlist[" + index
											+ "].hzvalue"%>'
																	size="30" maxlength="50" styleClass="text4"
																	onchange="fieldcode(this,1)" />
																<logic:equal name="element" property="fieldname"
																	value="b0110">
																	<%--<img src="/images/code.gif"
																		onclick='openInputCodeDialogOrgInputPos("UN","<%="factorlist[" + index
												+ "].hzvalue"%>","<%=manager%>",1);' align="middle"/>--%>
												<!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                        <img src="/images/code.gif" align="absmiddle" editable="true" id="factorlist<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="UN" nmodule="4" ctrltype="3" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:equal>
																<logic:notEqual name="element" property="fieldname"
																	value="b0110">
																	<logic:equal name="element" property="fieldname"
																		value="e0122">
																		<%--<img src="/images/code.gif"
																			onclick='openInputCodeDialogOrgInputPos("UM","<%="factorlist["
													+ index + "].hzvalue"%>","<%=manager%>",1);' align="middle"/>--%>
													<!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                        <img src="/images/code.gif" align="absmiddle" editable="true" id="factorlist<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="UM" nmodule="4" ctrltype="3" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="<%="factorlist["+index+"].value"%>"/>
																	</logic:equal>
																	<logic:equal name="element" property="fieldname"
																		value="e01a1">
																		<%--<img src="/images/code.gif"
																			onclick='openInputCodeDialogOrgInputPos("@K","<%="factorlist["
													+ index + "].hzvalue"%>","<%=manager%>",1);' align="middle"/>--%>
													<!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                        <img src="/images/code.gif" align="absmiddle" editable="true" id="factorlist<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="@K" nmodule="4" ctrltype="3" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="<%="factorlist["+index+"].value"%>"/>
																	</logic:equal>
																	<logic:notEqual name="element" property="fieldname"
																		value="e0122">
																		<logic:notEqual name="element" property="fieldname"
																			value="e01a1">
																			<%--<img src="/images/code.gif"
																				onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["
														+ index + "].hzvalue"%>");' align="middle"/>--%>
														<!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                        <img src="/images/code.gif" align="absmiddle"  id="factorlist<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codeid}" nmodule="4" ctrltype="3" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="<%="factorlist["+index+"].value"%>"/>
																		</logic:notEqual>
																	</logic:notEqual>
																</logic:notEqual>
																<!-- <img src="/images/code.gif" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist[" + index
											+ "].hzvalue"%>");'/> -->
															</logic:notEqual>
															<logic:equal name="element" property="codeid" value="0">
																<html:text name="staticFieldForm"
																	property='<%="factorlist[" + index
											+ "].value"%>'
																	styleId="hz" size="30" maxlength="${element.itemlen}"
																	styleClass="text4" />
															</logic:equal>
														</td>
													</logic:equal>
													<!--数据值-->
													<logic:equal name="element" property="fieldtype" value="N">
														<td align="left" class="RecordRow" nowrap>
															<html:text name="staticFieldForm"
																property='<%="factorlist[" + index
										+ "].value"%>'
																styleId="hz" size="30" maxlength="${element.itemlen}"
																styleClass="text4" />
														</td>
													</logic:equal>
												</tr>
												<%
													++i;
												%>

											</logic:iterate>
											<tr>
												<td align="center" nowrap class="RecordRow" colspan="4">
													<!-- 
                         <html:multibox name="staticFieldForm" property="find" value="1"/>&nbsp;<bean:message key="static.like"/>&nbsp;&nbsp;&nbsp;&nbsp;
                  
                        <html:multibox name="staticFieldForm" property="history" value="1"/>&nbsp;<bean:message key="static.history"/>&nbsp;&nbsp;
                         <logic:equal name="userView" property="status" value="0">
                            <html:multibox name="staticFieldForm" property="result" value="1"/>&nbsp;<bean:message key="hmuster.label.search_result"/>
                         </logic:equal>
                          -->
													<html:hidden name="staticFieldForm" property="find"
														styleId="findid" />
													<logic:equal value="1" name="staticFieldForm"
														property="find">
														<input type=checkbox onclick="changevalue(this,'find')"
															checked="checked" />&nbsp;<bean:message key="static.like" />&nbsp;&nbsp;&nbsp;&nbsp;
                          </logic:equal>
													<logic:notEqual value="1" name="staticFieldForm"
														property="find">
														<input type=checkbox onclick="changevalue(this,'find')" />&nbsp;<bean:message
															key="static.like" />&nbsp;&nbsp;&nbsp;&nbsp;
                          </logic:notEqual>

													<html:hidden name="staticFieldForm" property="history"
														styleId="historyid" />
													<logic:equal value="1" name="staticFieldForm"
														property="history">
														<input type=checkbox onclick="changevalue(this,'history')"
															checked="checked" />&nbsp;<bean:message
															key="static.history" />&nbsp;&nbsp;
                          </logic:equal>
													<logic:notEqual value="1" name="staticFieldForm"
														property="history">
														<input type=checkbox onclick="changevalue(this,'history')" />&nbsp;<bean:message
															key="static.history" />&nbsp;&nbsp;
                          </logic:notEqual>

													<html:hidden name="staticFieldForm" property="result"
														styleId="resultid" />
													<logic:equal value="1" name="staticFieldForm"
														property="result">
														<input type=checkbox onclick="changevalue(this,'result')"
															checked="checked" />&nbsp;<bean:message
															key="hmuster.label.search_result" />
													</logic:equal>
													<logic:notEqual value="1" name="staticFieldForm"
														property="result">
														<input type=checkbox onclick="changevalue(this,'result')" />&nbsp;<bean:message
															key="hmuster.label.search_result" />
													</logic:notEqual>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td height="201" colspan="0" avlign="top">
										<fieldset>
											<legend>
												<bean:message key="kq.wizard.expre" />
											</legend>
											<table border="0" align="center" cellpadding="2"
												cellspacing="0" class="ListTable" style="margin:5px;">
												<tr>
													<td colspan="2" width="57%" align="left" nowrap class="RecordRow_lrt">
														<html:text name="staticFieldForm" property="texts"
															size="75" styleClass="text4" />
													</td>
												</tr>
												<tr height="10" width="57%" align="left" nowrap>
													<td width="18%" align="left" nowrap class="RecordRow_l">
														<input type="button" name="Submit46" value="("
															class="mybutton" onclick="addTxt(this.value)">
														<input type="button" name="Submit462" value=")"
															class="mybutton" onclick="addTxt(this.value)">
														<input type="button" name="Submit463"
															value="<bean:message key="general.mess.and"/>" title="*"
															class="mybutton" onclick="addTxt(this.title)">
														<input type="button" name="Submit464"
															value="<bean:message key="general.mess.or"/>" title="+"
															class="mybutton" onclick="addTxt(this.title)">
													</td>
													<td align="right" nowrap class="RecordRow_lr">
														<input type="button"
															value="<bean:message key="static.incept"/>"
															class="mybutton" name="br_incept" onclick="check();">
														<input type="button"
															value="<bean:message key="button.delete"/>"
															class="mybutton" name="sss" onclick="addEpre('selects');" style="margin:0px;">
													</td>
												</tr>
												<tr>
													<td colspan="2" width="57%" rowspan="2" valign="middle" align="left"
														nowrap class="RecordRow" multiple>
														<logic:equal name="staticFieldForm" property="mes"
															value="3">
															<html:select name="staticFieldForm" property="selects"
																style="height:150px;width:400px;font-size:9pt;margin-top:5px;"
																multiple="multiple" size="20">
																<html:optionsCollection property="rlist"
																	value="dataValue" label="dataName" />
															</html:select>
														</logic:equal>
														<logic:notEqual name="staticFieldForm" property="mes"
															value="3">
															<html:select name="staticFieldForm" property="selects"
																style="height:150px;width:400px;font-size:9pt;margin-top:5px;"
																multiple="multiple" size="20">
															</html:select>
														</logic:notEqual>
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr class="list3">
						<td colspan="4" align="center" height="35px">
						    <hrms:priv func_id="2602302">
								<html:button styleClass="mybutton" property="b_save"
									onclick="return submits('selects');">
									<bean:message key="button.save" />
								</html:button>
							</hrms:priv>
							<hrms:submit styleClass="mybutton" property="br_back">
								<bean:message key="static.back" />
							</hrms:submit>
							<html:button styleClass="mybutton" property="b_next"
								onclick="return checkNext('selects');">
								<bean:message key="static.next" />
							</html:button>
							<html:reset styleClass="mybutton" onclick="stat_reset();">
								<bean:message key="button.clear" />
							</html:reset>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<div id="date_panel">
		<select name="date_box" multiple="multiple" size="10"
			style="width: 200" onchange="setSelectValue();"
			onclick="setSelectValue();">
			<option value="$YRS[10]">
				年限
			</option>
			<option value="当年">
				当年
			</option>
			<option value="当月">
				当月
			</option>
			<option value="当天">
				当天
			</option>
			<option value="今天">
				今天
			</option>
			<option value="截止日期">
				截止日期
			</option>
			<option value="1992.4.12">
				1992.4.12
			</option>
			<option value="1992.4">
				1992.4
			</option>
			<option value="1992">
				1992
			</option>
			<option value="????.??.12">
				????.??.12
			</option>
			<option value="????.4.12">
				????.4.12
			</option>
			<option value="????.4">
				????.4
			</option>
		</select>
	</div>
</html:form>
<script language="javascript">
   Element.hide('date_panel');
   <logic:equal name="staticFieldForm" property="flist" value="1">
   if($('userbase').options.length<2){
   		$('imgid').style.display='none';
   }
   </logic:equal>
   
   function changevalue(obj,name){
   		if(obj.checked){
   			$(name+'id').value='1';
   		}else{
   			$(name+'id').value='0';
   		}
   }
   
if(getBrowseVersion()){//ie浏览器样式问题修改 bug 39387 wangb 20180804
	var viewuserbases = document.getElementsByName('viewuserbases')[0];
	viewuserbases.style.marginTop='1px';
	viewuserbases.style.height='20px';
	viewuserbases.style.paddingTop='0px';
	//if(getBrowseVersion() == 10){//ie11浏览器 样式调整 wangb 20190307
		var imgid = document.getElementById('imgid');
		imgid.style.height='25px';
	//}
}
//重置操作，没有对人员库操作
function stat_reset(){
	setTimeout(function(){
		var viewuserbases =document.getElementsByName('viewuserbases')[0];
		viewuserbases.value='${staticFieldForm.init_viewuserbases}';
		var userbases =document.getElementsByName('userbases')[0];
		userbases.value='${staticFieldForm.init_userbases}';
	},100);
}

</script>
<div id="dict" class='div_table'
	style="display: none; z-index: +999; position: absolute; height: 170px; overflow: hidden; background-color: #FFF"></div>

