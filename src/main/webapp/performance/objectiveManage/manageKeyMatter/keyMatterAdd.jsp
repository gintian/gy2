<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm,
				com.hjsj.hrms.actionform.performance.objectiveManage.manageKeyMatter.KeyMatterForm,
				com.hrms.hjsj.utils.Sql_switcher,
				com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant,
				com.hrms.struts.constant.SystemConfig,
				com.hrms.hjsj.sys.EncryptLockClient,
				org.apache.commons.beanutils.LazyDynaBean "%>

<%
	KeyMatterForm manageKeyMatterForm=(KeyMatterForm)session.getAttribute("manageKeyMatterForm");
	String orgCode=(String)manageKeyMatterForm.getCode();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
%>	

<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript" src="/performance/objectiveManage/manageKeyMatter/keyMatter.js"></script> 

<hrms:themes />
<script>

var logoSign = 'false';
//  查询
function queryKeyMatter()
{
	 var objecType = $F('object_Types');
   	 var objectName=$F('objectA0101');   	     	 
   	 var kind="${manageKeyMatterForm.kind}"; 
   	 var code="${manageKeyMatterForm.code}";       
   	  	 
   	 var hashvo=new ParameterSet();	
     hashvo.setValue("objectName",getEncodeStr(objectName));				
     hashvo.setValue("objecType",objecType);
     hashvo.setValue("kind",kind);
     hashvo.setValue("code",code);
     var request=new Request({asynchronous:false,onSuccess:showA0101,functionId:'9028000508'},hashvo); 
}
function showA0101(outparamters)
{
	var objlist=outparamters.getValue("objlist");
	var objecType=outparamters.getValue("objecType");
	
	if(objlist!=null)
	{		
		if(objlist.length>0)
		{
			logoSign = 'false';
			Element.show('a0101_box');   
			AjaxBind.bind($('a0101_box'),objlist);				  
		}else
		{
			logoSign = 'true';
			Element.hide('a0101_box');         
		}		  
	}		
}
	
//  控制查询出的数量
function showSelectBox(srcobj)
{
	Element.show('a0101_pnl');   
	
    var pos=getAbsPosition(srcobj);
	with($('a0101_pnl'))
	{
		style.position="absolute";
    	style.left=pos[0]-1+'px';
 		style.top=pos[1]-1+srcobj.offsetHeight+'px';		   
		style.width=(srcobj.offsetWidth<100)?100:srcobj.offsetWidth+1;
    }                 
} 

function setSelectValue()
{
	var objecType = $F('object_Types');
	var objid,i;
    var obj=$('a0101_box');
   	for(i=0;i<obj.options.length;i++)
    {
    	if(obj.options[i].selected)
        	objid=obj.options[i].value
    }       
    if(objid)
    {    
    	document.manageKeyMatterForm.sign.value = "true";
    		   
		if(objecType=='2')
		{
			var aTmp = objid.split("`"); 
			document.manageKeyMatterForm.objectA0100.value = aTmp[0];  
			document.manageKeyMatterForm.objectB0110.value = aTmp[1];  
			document.manageKeyMatterForm.objectE0122.value = aTmp[2];  
			document.manageKeyMatterForm.objectName.value = getEncodeStr(aTmp[3]);  
			
			var mark = "true";		
			manageKeyMatterForm.action="/performance/objectiveManage/manageKeyMatter/keyMatterAdd.do?b_add=link&mark="+mark+"&ObjectName="+getEncodeStr(aTmp[3]);
			manageKeyMatterForm.submit();
		
		}else 
		{
			var bTmp = objid.split("`"); 
			document.manageKeyMatterForm.objectB0110.value = bTmp[0];  
			document.manageKeyMatterForm.objectE0122.value = bTmp[0]; 
			
			var mark = "true";		
			manageKeyMatterForm.action="/performance/objectiveManage/manageKeyMatter/keyMatterAdd.do?b_add=link&mark="+mark+"&ObjectName="+getEncodeStr(bTmp[1]);
			manageKeyMatterForm.submit();
		}						
    }   
    Element.hide('a0101_pnl');         
}

function cancelFunc()
{
	myClose();
	
	if(parent && parent.parent && parent.parent.Ext && parent.parent.addeditWinClose){
		parent.parent.addeditWinClose();
	} else {
		parent.window.close();
	}
}
function save(type)
{	 
	var objecType = $F('object_Types');		  
	if($F('objectA0101')=='')
	{
		if(objecType=='2')		
	   		alert("<bean:message key='performance.keymater.nonull4' />!");
	   	else
	   		alert("<bean:message key='performance.keymater.nonull5' />!");
	   	return;
	}
	
	var objectB0110="${manageKeyMatterForm.objectB0110}";  
	var sign="${manageKeyMatterForm.sign}";  
	var act="${manageKeyMatterForm.act}"; 
	var orgCode="${manageKeyMatterForm.code}"; 
	
//	alert(logoSign);
//	alert(sign);
				
	if((logoSign=='false') && (sign==null || sign=='' || sign.length<=0 || sign=='false'))
	{
		if((act!=null && act!='' && act=='edit') || ((act!=null && act!='' && act=='add') && (orgCode!=null && orgCode!='' && orgCode.length>0)))
		{}
		else
		{
			alert("请在下拉框中双击需新建事件的人员或团队！");
			return;
		}
	}
	
	if(logoSign=='true')
	{	
		alert("没有该人员或团队，或您没有该人员或团队的操作权限，请重新输入！");
		return;
	}
	
	if(objectB0110==null || objectB0110=='' || objectB0110.length<=0)
	{
		alert("该人员的单位信息不完整，请检查！");
		return;
	}			
	
/*			
	var objectB0110="${manageKeyMatterForm.objectB0110}";    	   
	if(objectB0110==null || objectB0110=='' || objectB0110.length<=0)
	{
		if(objecType=='2')		
	   		alert("<bean:message key='performance.keymater.nonull6' />");
	   	else
	   		alert("<bean:message key='performance.keymater.nonull7' />");
	   	return;
	}
*/	
	if($F('key_event')=='')
	{
	   	alert("<bean:message key='performance.keymater.nonull1' />!");
	   	return;
	}
	   
	var dateVal = $F('editor1');
	if(dateVal=='')
	{
	    alert("<bean:message key='performance.keymater.nonull2' />!");
	   	return;
	}   
	   
	var score = $F('score');
	if(score=='')
	{
	   	alert("<bean:message key='performance.keymater.nonull3' />!");
	   	return;
	}
	document.getElementById('busi_date').value=$F('editor1');
    manageKeyMatterForm.action='/performance/objectiveManage/manageKeyMatter/keyMatterAdd.do?b_save=link&type='+type+'&dateSQ='+$F('editor1');
    manageKeyMatterForm.target="_self";
	manageKeyMatterForm.submit();
	if(type=='save')
	{				 
		if(parent && parent.parent && parent.parent.Ext && parent.parent.addedit_ok){
			parent.parent.addedit_ok('true');
		} else {
			var thevo=new Object();
			thevo.flag="true";
			window.returnValue=thevo;
			parent.window.close();
		}
	}
	   
}

function myClose()
{
	if("${param.type}"=="save_continue")
	{
		var thevo=new Object();
		thevo.flag="true";
		window.returnValue=thevo;
	}
}
	
function selectPoint()
{
	var infos=new Array();
	infos[0]='';
	infos[1]='33';
	var thecodeurl="/performance/kh_system/kh_template/init_kh_item.do?br_selectpoint2=query`objectid="+infos[0]+"`objectType="+infos[1]; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl); 
	
	// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
    if(/msie/i.test(navigator.userAgent)){
		var pointid = window.showModalDialog(iframe_url, infos, "dialogWidth:420px; dialogHeight:330px;resizable:no;center:yes;scroll:no;status:no");	
		if(pointid)
		{
			if(pointid=='undefined'||pointid=='')						
		    	return;
			else
			{
				var hashvo=new ParameterSet();
				hashvo.setValue("pointid",pointid.substring(1));
				document.getElementById("point_id").value=pointid.substring(1);
				var request=new Request({asynchronous:false,onSuccess:getPointName,functionId:'9028000505'},hashvo); 
			}
		}	
		return ;
	} else {
	    function openWin(){
		    Ext.create("Ext.window.Window",{
		    	id:'selectpoint_win',
		    	width:440,
		    	height:370,
		    	title:'请选择',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
		    }).show();	
		}
		
		if(typeof window.Ext == 'undefined'){
			insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
				insertFile("/ext/ext6/ext-all.js","js" ,openWin);
			});
			
		} else {
			openWin();
		}
	}
}
function selectPoint_ok(pointid){
	if(pointid)
	{
		if(pointid=='undefined'||pointid=='')						
	    	return;
		else
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("pointid",pointid.substring(1));
			document.getElementById("point_id").value=pointid.substring(1);
			var request=new Request({asynchronous:false,onSuccess:getPointName,functionId:'9028000505'},hashvo); 
		}
		selectpointWinClose();
	}	
}
function selectpointWinClose(){
	Ext.getCmp('selectpoint_win').close();
}
function getPointName(outparameters)
{
   	var pointName =outparameters.getValue("PointName");
  	manageKeyMatterForm.pointName.value=pointName;
  	document.getElementById("key_event").value=pointName+document.getElementById("key_event").value;
}

</script>
<body onbeforeunload="myClose();">
<html:form action="/performance/objectiveManage/manageKeyMatter/keyMatterAdd">

	<html:hidden name="manageKeyMatterForm" property="objecType" styleId="object_Types" />
	<html:hidden name="manageKeyMatterForm" styleId="logo" property="logo"/>
	<html:hidden name="manageKeyMatterForm" styleId="sign" property="sign"/>
	
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>


	<fieldset align="center" style="width:500px; border-bottom-style:none;padding:0px 0px;">
	<legend align="center">
		<bean:message key="jx.jf.key_matter" />
	</legend>
		<br>
		<table border="0" cellspacing="0" align="center" cellpadding="2" width="100%" class="listTable">		
		
			<html:hidden name="manageKeyMatterForm" styleId="event_id" property="keyEventVo.string(event_id)"/>			
			<html:hidden name="manageKeyMatterForm" styleId="nbase" property="keyEventVo.string(nbase)"/>
			<html:hidden name="manageKeyMatterForm" styleId="point_id" property="keyEventVo.string(point_id)"/>
			<html:hidden name="manageKeyMatterForm" styleId="object_type" property="keyEventVo.string(object_type)"/>
		<%
//			if(orgCode!=null && orgCode.trim().length()>0)
//			{						
		%>	   			
			<html:hidden name="manageKeyMatterForm" styleId="b0110" property="keyEventVo.string(b0110)"/>
			<html:hidden name="manageKeyMatterForm" styleId="e0122" property="keyEventVo.string(e0122)"/>
			<html:hidden name="manageKeyMatterForm" styleId="a0101" property="keyEventVo.string(a0101)"/>
			<html:hidden name="manageKeyMatterForm" styleId="a0100" property="keyEventVo.string(a0100)"/>			
		<%
//			}else{						
		%>	
			<html:hidden name="manageKeyMatterForm" styleId="objectB0110" property="objectB0110"/>
			<html:hidden name="manageKeyMatterForm" styleId="objectE0122" property="objectE0122"/>
			<html:hidden name="manageKeyMatterForm" styleId="objectName" property="objectName"/>
			<html:hidden name="manageKeyMatterForm" styleId="objectA0100" property="objectA0100"/>			
		<%
//			}						
		%>	
		
			<tr>
				<td align="right" nowrap class="RecordRow_right">				
					&nbsp;
					<logic:equal name="manageKeyMatterForm" property="objecType" value="2">     
   	  	 				<bean:message key="label.title.name"/>
   	  	 			</logic:equal>
			   	  	<logic:equal name="manageKeyMatterForm" property="objecType" value="1">     
			   	  	 	<bean:message key="jx.key_event.objectName"/>
			   	  	</logic:equal>
				</td>
				<td align="left" nowrap class="RecordRow_left">				
						
						
					<html:text name="manageKeyMatterForm" styleId="objectA0101" property="objectName" 
							  style="width:150px;font-size:10pt;text-align:left" onkeyup='queryKeyMatter();showSelectBox(this);' styleClass="inputtext"
					/>	
						
<%-- 											
					<input type="text" name="objectA0101" value=""  class="editor" style="width:150px;font-size:10pt;text-align:left" 
					id="objectA0101" onkeyup='queryKeyMatter();showSelectBox(this);'>
--%>					
				</td>  
			</tr> 
			
			<tr>
				<td align="right" nowrap class="RecordRow_right">
					&nbsp;<bean:message key='jx.jifen.period' />
				</td>
				<td align="left" nowrap class="RecordRow_left">
					<input type="text" name="date" extra="editor" style="width:150px;font-size:10pt;text-align:left"
						id="editor1" dropDown="dropDownDate" onchange=" if(!validate(this,'属期')) { this.value='';}">				
					<html:hidden name="manageKeyMatterForm" styleId="busi_date" property="keyEventVo.string(busi_date)"/>
				</td>  
			</tr> 
			
			<tr>
				<td align="right" nowrap class="RecordRow_right">
					&nbsp;<bean:message key='jx.key_event.type' />
				</td>								
				<td align="left" nowrap class="RecordRow_left">
					<html:select name="manageKeyMatterForm" styleId="eventType" property="eventType" size="1" style="width:260px" >
  					 	<html:optionsCollection property="eventTypeList" value="dataValue" label="dataName"/>
					</html:select>
				</td>  
			</tr>
			<tr>
				<td align="right" nowrap class="RecordRow_right" valign="top">
					&nbsp;<bean:message key='jx.jifen.matter' />
				</td>
				<td align="left" nowrap class="RecordRow_left">
					<html:text name="manageKeyMatterForm" styleId="pointName" property="pointName" style="width:240px;margin:1px 0 1px 0;" readonly="true"  styleClass="inputtext"/>
					<img  src="/images/code.gif" onclick='javascript:selectPoint();' align="absmiddle" />&nbsp;<br>
					<html:textarea name="manageKeyMatterForm" styleId="key_event" property="keyEventVo.string(key_event)" cols="37" rows="6" style="width:240px;margin-bottom:1px;"></html:textarea>								
				</td>
			</tr>			
			<!--  <tr>
				<td align="right" nowrap class="RecordRow_right">
					
				</td>								
				<td align="left" nowrap class="RecordRow_left">
					<html:textarea name="manageKeyMatterForm" styleId="key_event" property="keyEventVo.string(key_event)" cols="35" rows="6"></html:textarea>
				</td>  
			</tr>
			-->
			<tr>
				<td align="right" nowrap class="RecordRow_right">
					&nbsp;<bean:message key='lable.performance.singleGrade.value' />
				</td>
				<td align="left" nowrap class="RecordRow_left">
					<!--this.focus();导致关掉提示框后无限触发失去焦点事件而重复弹出提示信息 haosl 2018-3-26 -->
					<html:text name="manageKeyMatterForm" styleId="score" property="keyEventVo.string(score)" style="width:150px" onblur="if(this.value!='' && !isNumber(this.value)){ alert('请输入数值类型的值！');}" styleClass="inputtext"/>
				</td>
			</tr>
		</table>
	</fieldset>
		<table border="0" cellspacing="0" align="center" cellpadding="2">	

			<tr>  
				<td align="center" style="height:35px">
					<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save('save')" />
					<logic:equal name="manageKeyMatterForm" property="act" value="add">
						<input type="button" value="<bean:message key='button.save'/>&<bean:message key='edit_report.continue'/>" onclick="save('save_continue');" Class="mybutton"> 
		  			</logic:equal>
					<input type="button" class="mybutton" value="<bean:message key='button.cancel'/>" onClick="cancelFunc()">  					
				</td>
			</tr>
		</table>
		
	<div id="a0101_pnl" style="border-style:nono">
  		<select name="a0101_box" multiple="multiple" size="10" class="dropdown_frame"  ondblclick='setSelectValue();'></select>
    </div> 
  
	<script>
		Element.hide('a0101_pnl');
		document.forms[0].date.value=replaceAll($F('busi_date'),'.','-');
	</script>
</html:form>
</body>
