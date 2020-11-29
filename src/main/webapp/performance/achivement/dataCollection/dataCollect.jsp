<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.hjsj.sys.VersionControl,
				 com.hjsj.hrms.actionform.performance.achivement.dataCollection.*"%>

<%
    // 计算按钮添加版本控制  JinChunhai 2011.08.17      	
	VersionControl version = new VersionControl();
%>
				 
<script language="JavaScript" src="/performance/achivement/dataCollection/dataCollect.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<script>
var objectid = "";
var objs='';
var items='';
var theRule="";
var thePlan="";
var thePoint="";
var thePointType="";
var theObjectType="${dataCollectForm.object_type}";
var planId = "${dataCollectForm.planId}";
var point = "${dataCollectForm.point}";
var isShowTargetTrace = "${dataCollectForm.isShowTargetTrace}";
var determine = "${dataCollectForm.determine}";

<logic:notEqual name="dataCollectForm" property="isShowTargetTrace" value="1">
 	theRule="${dataCollectForm.rule}";
 	thePlan="${dataCollectForm.planId}";
 	thePoint="${dataCollectForm.point}";
 	thePointType="${dataCollectForm.pointype}";
 	
	<logic:iterate id="element1" name="dataCollectForm" property="khObjs" indexId="index">
		objs+="<bean:write name="element1" property="object_id" />,";
	</logic:iterate>
	<logic:iterate id="element2" name="dataCollectForm" property="khItems" indexId="index">				
		items+='<bean:write name="element2" property="item" />,';
	</logic:iterate>
</logic:notEqual>
Ext.Loader.setConfig({
    enabled: true,
    paths: {
        'SYSF':'/components/fileupload'
    }
});
Ext.require('SYSF.FileUpLoad');
function myfuc()
{
	save();
}
function outContent(content)
{
	Tip(getDecodeStr(content),STICKY,true);
}

//检验数字类型
function checkValue(obj)
{
  	if(obj.value.length>0)
  	{
  		if(!checkIsNum2(obj.value))
  		{
  			alert('请输入数值');
  			obj.value='';
  			obj.focus();
  		}
  	} 
}
function showDateSelectBox3(srcobj)
 {
	 var date_desc=document.getElementById(srcobj);
	 var date_panel2 = document.getElementById("date_panel2");
	  if(date_desc.value=="")
	  {
		  showOrHideDateDanel2("none");
		return false ;
	  }
     
	  showOrHideDateDanel2("block");
      var pos=getAbsPosition(date_desc);
     
	  with(date_panel2)
	  {
        style.position="absolute";
		style.top = (pos[1]+23)+"px";
		style.left=pos[0]+"px";
		var width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
		style.width = width+"px";
      }
    
      var hashVo = new ParameterSet();
      hashVo.setValue("name",getEncodeStr(date_desc.value));
      hashVo.setValue("plan_id",planId);
      hashVo.setValue("point",point);
      hashVo.setValue("model","4");
      var request=new Request({method:'post',asynchronous:false,onSuccess:shownamelist,functionId:'90100160019'},hashVo);
}
function shownamelist(outparamters)
   {
   		var namelist=outparamters.getValue("namelist");
   		var date_panel2 = document.getElementById("date_panel2");
		if(namelist.length==0){
		
			 showOrHideDateDanel2("none");
		}
		else{
		    if(namelist.length<6){
		      document.getElementById("date_box").size=namelist.length;
		    }else{
		      document.getElementById("date_box").size = 6;
		    }
			AjaxBind.bind(dataCollectForm.contenttype,namelist);
		}
   }
   
function setSelectValue()
	{
	    
		var temps=document.getElementById("date_box");
		var date_panel2 = document.getElementById('date_panel2');
		for(i=0;i<temps.options.length;i++)
		{
		   
	        if(temps.options[i].selected)
	        { 
		    	document.getElementById("searchname").value=temps.options[i].text;
	            objectid = temps.options[i].value;
	            showOrHideDateDanel2("none");
		    }
		}
		
	} 
/** 查询 */
	function query()
	{
		if(document.getElementById('searchname').value.length==0)
		{
			alert("请输入姓名信息!");
			return;
		}
		if(objectid){
		document.getElementById('searchid').value=objectid;
		document.dataCollectForm.action="/performance/achivement/dataCollection/dataCollect.do?b_query=search&planId="+planId;
		document.dataCollectForm.submit();
		
		}
		
	}
</script>
<style>

div#tbl-container 
{
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
	width:99%;
    overflow: auto;
   /* height:expression(document.body.clientHeight-100);*/
}
.Input_self
{  
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;                                                                  
  	font-size:   12px;                                              
  	letter-spacing:   1px;                      
  	text-align:   right;                        
  	height:   90%;                                    
  	width:   80%; 
  	cursor:   hand;                                     
} 
.Input_self2
{  
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: medium none; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;                                                                  
  	font-size:   12px;                                              
  	letter-spacing:   1px;                      
  	text-align:   right;                        
  	height:   90%;                                    
  	width:   90%;                                   
} 
.cell_yellow td{
   BACKGROUND-COLOR: #FFF8D2
}

.cell_Collectlocked2{
	position: static !important;
}
.header_Collectlocked{
	position: static !important;
}
.cell_Collect{
	position: static !important;
}
</style>

<script type="text/javascript">

var IVersion=getBrowseVersion();

if(IVersion==8)
{
  	document.writeln("<link href=\"/performance/evaluation/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
  	document.writeln("<link href=\"../../../css/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}
//【6738】绩效管理：业绩数据录入，切换考核指标，如果按键盘Backspace键，在进行切换，报错 jingq add 2015.02.03
window.onload=function(){//必须含有body元素才可屏蔽backspace
	document.getElementsByTagName("body")[0].onkeydown =function(e){            
        //获取事件对象
        var event = e?e:window.event;
		var elem = event.relatedTarget || event.srcElement || event.target ||event.currentTarget;   
        if(event.keyCode==8){//判断按键为backSpace键  
			//获取按键按下时光标做指向的element  
            var elem = event.srcElement || event.currentTarget;   
            //判断是否需要阻止按下键盘的事件默认传递  
            var name = elem.nodeName;  
            //haosl 2018-3-27 解决通过输入框查找人员时，无法使用backSpace键的问题
            var focusDom = document.activeElement;
            if(focusDom && focusDom.id == 'searchname'){
            	return ;
            }
            if(name!='INPUT' && name!='TEXTAREA'){  
            	return _stopIt(event);  
            }  
            var type_e = elem.type.toUpperCase();  
            if(name=='INPUT' && (type_e!='TEXT' && type_e!='TEXTAREA' && type_e!='PASSWORD' && type_e!='FILE')){  
            	return _stopIt(event);
            }  
            if(name=='INPUT' && (elem.readOnly==true || elem.disabled ==true)){  
            	return _stopIt(event);  
            }  
        }  
    };
};  
function _stopIt(e){  
	if(e.returnValue){  
		e.returnValue = false ;  
    }  
    if(e.preventDefault ){  
        e.preventDefault();  
    }                 
    return false;
}
   function showDateSelectBox(srcobj,point_id)
   {
   		Element.show('date_panel');
      var pos=getAbsPosition(srcobj);
      var hashvo=new ParameterSet();
	  hashvo.setValue("pos0",pos[0]);
	  hashvo.setValue("pos1",pos[1]);
      hashvo.setValue("srcobj_width",srcobj.offsetWidth);
      hashvo.setValue("srcobj_height",srcobj.offsetHeight);      
      var In_paramters="point_id="+point_id; 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'90100160009'},hashvo);
   }
   function showDateSelectBox2(srcobj,point_id)
   {	
   		Element.show('date_panel'); 
	  var dataHtml="";
	  dataHtml+="<table width='500' border='0' cellspacing='0' bgColor='#FFFFFF'  align='center' cellpadding=0' class='ListTable'   > ";
	  dataHtml+="<thead><tr> <td  width='60'  align='center' class='TableRow' nowrap >"+P_STANDPOINT+"</td>";
	  dataHtml+="<td width='260' align='center' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+P_POINTDESC+"&nbsp;&nbsp;&nbsp;</td>";			
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap > &nbsp;&nbsp;&nbsp;"+PERCENT+"&nbsp;&nbsp;&nbsp;</td>";			
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap > &nbsp;&nbsp;"+P_UVALUE+"&nbsp;&nbsp;</td>";
	  dataHtml+="<td width='60' align='center' class='TableRow' nowrap >&nbsp;&nbsp;"+P_BVALUE+"&nbsp;&nbsp;</td> </tr>  </thead>";
	  var gradeList=point_grade['p'+point_id];
	  if(typeof(gradeList)=="undefined")
	  {
	  	alert(P_FRESHDATA2+"!");
	  	return;
	  }
	  for(var i=0;i<gradeList.length;i++)
	  {
	       var temp=gradeList[i];
	  		dataHtml+="<tr";
			if(i%2==0)
				dataHtml+=" background-color: #FFFFFF; ";
			else
				dataHtml+=" class='trDeep' ";
			
			if(temp.subsys_id!="undefined" && temp.subsys_id=='35')
			{
				if(typeof(per_competencedegree[temp.gradecode])=="undefined")
				{
					alert(P_NOSAME+"!");
					return;
				}
			}else
			{
				if(typeof(per_standdegree[temp.gradecode])=="undefined")
				{
					alert(P_NOSAME+"!");
					return;
				}
			}
			if(temp.subsys_id!="undefined" && temp.subsys_id=='35')			
				dataHtml+="><td align='left' class='RecordRow'  nowrap >"+per_competencedegree[temp.gradecode].gradedesc+"</td>";
			else
				dataHtml+="><td align='left' class='RecordRow'  nowrap >"+per_standdegree[temp.gradecode].gradedesc+"</td>";
			dataHtml+="<td align='left'  class='RecordRow' >"+temp.gradedesc+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.gradevalue+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.top_value+"</td>";
			dataHtml+="<td  align='left' class='RecordRow' nowrap >"+temp.bottom_value+"</td></tr>";
	  }
	  dataHtml+="</table>";	
	 
	  dataHtml=replaceAll(dataHtml,"#@#","<br>");	
	  date_panel.innerHTML=dataHtml;
	  date_panel.innerHTML=date_panel.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;"
   			 				+"width:"+date_panel.offsetWidth+"px; height:"+date_panel.offsetHeight+";px " 					    	
   			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";   				
	  var pos=getAbsPosition(srcobj);
	  var pos0=pos[0];
	  var pos1=pos[1];
	  var srcobj_width=srcobj.offsetWidth;
	  var srcobj_height=srcobj.offsetHeight;
      var op=eval('date_panel');	
      with($('date_panel'))
	  {
	        style.position="absolute";
		    style.posTop=srcobj.offsetHeight+pos1+30;
		    style.posLeft=pos[0]+10;
      }
   }
   	function returnInfo2(outparamters)
	{   
			  Element.show('date_panel');   
	      var pos0=outparamters.getValue("pos0")*1;
	      var pos1=outparamters.getValue("pos1")*1;
		  var srcobj_width=outparamters.getValue("srcobj_width")*1;
		  var srcobj_height=outparamters.getValue("srcobj_height")*1;
		var fieldlist=outparamters.getValue("dataList");	
		var dataHtml=getDecodeStr(outparamters.getValue("dataHtml"));	
		dataHtml=replaceAll(dataHtml,"#@#","<br>");
		  date_panel.innerHTML=dataHtml;				
	      var op=eval('date_panel');	
	      with($('date_panel'))
		  {
		        style.position="absolute";
			    style.top=(srcobj_height+pos1)+30+"px";
			    style.left=(pos0+10)+"px";
	      }
	}
	function hiddenPanel()
	{
		Element.hide('date_panel');
	}
</script>
<hrms:themes />
<!--
<link href="../../../css/locked-column-new.css" rel="stylesheet" type="text/css">  
-->
<body>
<div id="date_panel">
   			
</div>
<html:form action="/performance/achivement/dataCollection/dataCollect">
	<html:hidden name="dataCollectForm" property="paramStr" styleId="paramStr" />
	<html:hidden name="dataCollectForm" property="determine" styleId="determine" />
	<html:hidden name="dataCollectForm" property="isReadOnly" styleId="isReadOnly" />
	
	<logic:notEqual name="dataCollectForm" property="isShowTargetTrace" value="1">
		<table width="100%" >
			<tr>
				<td align="left">
					<bean:message key="performance.implement.kh_point" />:
					<html:select name="dataCollectForm" property="point" size="1"
						onchange="changePoint(this.value)">
						<html:optionsCollection property="khPoints" value="dataValue"
							label="dataName" />
					</html:select>
					<input type='button' class="mybutton" onclick='downloadTemplate()'
						value='<bean:message key="button.download.template"/>' />
					<input type='button' class="mybutton" onclick='exportExcel()'
						value='<bean:message key="goabroad.collect.educe.excel"/>' />
					<logic:notEqual name="dataCollectForm" property="isReadOnly"
						value="1">
						<input type='button' class="mybutton"
							onclick='importExcel("${dataCollectForm.planId}","${dataCollectForm.point}")'
							value='<bean:message key="import.tempData"/>' />
						<input type='button' class="mybutton" onclick='myfuc()' id='save'
							value='<bean:message key="button.save"/>' />
					</logic:notEqual>
					
					<%  if(version.searchFunctionId("060803")){   %>
 					<logic:equal name="dataCollectForm" property="pointFormula" value="true">
						<input type='button' class="mybutton" onclick='computeScore();' id='pointFormula'
							value='<bean:message key="button.computer"/>' />
					</logic:equal>
					<%  } %>
					&nbsp;&nbsp;&nbsp;
					<html:text property="searchname" styleId="searchname" onkeyup="showDateSelectBox3('searchname')"></html:text>
					<html:hidden property="searchid" styleId="searchid"/>
			        &nbsp;&nbsp;&nbsp;<input type="button" class="mybutton" value="查找" onclick="query()"/>
				</td>
			</tr>
		</table>
		
		<script language='javascript'>
			var height = document.documentElement.clientHeight;
			var width = document.documentElement.clientWidth;
			document.write("<div id=\"tbl-container\" style='height:"+(height-105)+"px;width:"+(width-20)+"px;'>");
		</script>	
		${dataCollectForm.tableHtml}
		<%
		int i = 0;
		%>
		<hrms:extenditerate id="element" name="dataCollectForm"
			property="setlistform.list" indexes="indexes"
			pagination="setlistform.pagination" pageCount="20" scope="session">
			<%
					if (i % 2 == 0)
					{
			%>
			<tr class="trShallow" onClick="javascript:tr_onclick(this,'#F3F5FC')">
				<%
						} else
						{
				%>
			
			<tr class="trDeep" onClick="javascript:tr_onclick(this,'#E4F2FC')">
				<%
						}
						i++;
				%>
				<td style="display:none" ><bean:write name="element" property="object_id" /></td>
				<logic:notEqual name="dataCollectForm" property="pointype" value="0">
					<logic:notEqual name="dataCollectForm" property="pointype" value="">					
						<logic:equal name="dataCollectForm" property="object_type" value="2">
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
								<bean:write name="element" property="b0110" filter="true" />							
							</td>
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
								<bean:write name="element" property="e0122" filter="true" />							
							</td>
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
								<bean:write name="element" property="e01a1" filter="true" />							
							</td>
						</logic:equal>					
						<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
							<bean:write name="element" property="objName" filter="true" />
							<input type='hidden'
								id='<bean:write name='element' property='object_id' filter='true' />'>
							
						</td>
						<logic:equal name="dataCollectForm" property="object_type" value="2">
							<logic:notEqual name="dataCollectForm" property="onlyname" value="a0101">
								<td align="left" class="RecordRow" style="border-top: none;" nowrap>
									<bean:write name="element" property="onlyname" filter="true" />							
								</td>
							</logic:notEqual >
						</logic:equal>
						<logic:iterate id="element2" name="dataCollectForm"
							property="khItems" indexId="index">
							<%
								LazyDynaBean abean = (LazyDynaBean) pageContext.getAttribute("element2");
								String item = (String) abean.get("item");
							%>
							<td align="center" class="RecordRow" style="border-top: none;" nowrap>
								<input type="text" class="Input_self"
									id="<bean:write name='element' property='object_id' filter='true' />_<bean:write name='element2' property='item' filter='true' />"
									value="<bean:write name='element' property='<%=item%>' filter='true' />"
									onkeypress="event.returnValue=IsDigit(this);" onblur='checkValue(this)'
									onchange="getDF(this)"
									<logic:equal name="dataCollectForm" property="isReadOnly" value="1">readonly="readonly"</logic:equal>
									onkeydown='if (event.keyCode==37) goLeftRight(this,-1);if (event.keyCode==39) goLeftRight(this,1);if (event.keyCode==38) goUpDown(this,-1);if (event.keyCode==40) goUpDown(this,1);'>
							</td>
						</logic:iterate>
						<td align="right" class="RecordRow" style="border-top: none;" nowrap>
							<input type="text" class="Input_self2"
								id="<bean:write name='element' property='object_id' filter='true' />_basicf"
								readonly="true"
								value="<bean:write name='element' property='basicf' filter='true' />">
						</td>
						<td align="right" class="RecordRow" style="border-top: none;" nowrap>
							<input type="text" class="Input_self2"
								id="<bean:write name='element' property='object_id' filter='true' />_cz"
								readonly="true"
								value="<bean:write name='element' property='cz' filter='true' />">
						</td>
						<td align="right" class="RecordRow" style="border-top: none;" nowrap>
							<input type="text" class="Input_self2"
								id="<bean:write name='element' property='object_id' filter='true' />_df"
								readonly="true"
								value="<bean:write name='element' property='df' filter='true' />">
						</td>
					</logic:notEqual>
				</logic:notEqual>
				<logic:equal name="dataCollectForm" property="pointype" value="0">
					<logic:equal name="dataCollectForm" property="rule" value="0">
						<logic:equal name="dataCollectForm" property="object_type" value="2">
							 
							
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
								<bean:write name="element" property="b0110" filter="true" />							
							</td>
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
								<bean:write name="element" property="e0122" filter="true" />							
							</td>
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
								<bean:write name="element" property="e01a1" filter="true" />							
							</td>
								
						</logic:equal>					
						<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
							<bean:write name="element" property="objName" filter="true" />
							<input type='hidden'
								id='<bean:write name='element' property='object_id' filter='true' />'>
						</td>
						<logic:equal name="dataCollectForm" property="object_type" value="2">
							<logic:notEqual name="dataCollectForm" property="onlyname" value="a0101">
								<td align="left" class="RecordRow" style="border-top: none;" nowrap>
									<bean:write name="element" property="onlyname" filter="true" />							
								</td>
							</logic:notEqual >
						</logic:equal>
						<td align="center" class="RecordRow" style="border-top: none;" nowrap>
							<input type="text" class="Input_self"
								id="<bean:write name='element' property='object_id' filter='true' />_fz"
								value="<bean:write name='element' property='fz' filter='true' />"
								onkeypress='event.returnValue=IsDigit2(this);'
								onblur='checkValue(this)'
								<logic:equal name="dataCollectForm" property="isReadOnly" value="1">readonly="readonly"</logic:equal>
								onblur='testVal("<bean:write name='element' property='basicVal' filter='true' />",this);'
								onkeydown="if (event.keyCode==38) goMove(this,'_fz',-1);if (event.keyCode==40) goMove(this,'_fz',1);">
						</td>
					</logic:equal>
					<logic:notEqual name="dataCollectForm" property="rule" value="0">
						<logic:equal name="dataCollectForm" property="object_type" value="2">
							 
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
								<bean:write name="element" property="b0110" filter="true" />							
							</td>
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
								<bean:write name="element" property="e0122" filter="true" />							
							</td>
							<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
								<bean:write name="element" property="e01a1" filter="true" />							
							</td>	
						</logic:equal>
						<td align="left" class="RecordRow_right" style="border-top: none;" nowrap>
							<bean:write name="element" property="objName" filter="true" />
							<input type='hidden'
								id='<bean:write name='element' property='object_id' filter='true' />'>
						</td>
						<logic:equal name="dataCollectForm" property="object_type" value="2">
							<logic:notEqual name="dataCollectForm" property="onlyname" value="a0101">
								<td align="left" class="RecordRow" style="border-top: none;" nowrap>
									<bean:write name="element" property="onlyname" filter="true" />							
								</td>
							</logic:notEqual >
						</logic:equal>
						<td align="right" class="RecordRow" style="border-top: none;" nowrap>
							<input type="text" class="Input_self2"
								id="<bean:write name='element' property='object_id' filter='true' />_standard"
								readonly='true'
								value="<bean:write name='element' property='standard' filter='true' />">
						</td>
						<td align="center" class="RecordRow" style="border-top: none;" nowrap>
							<input type="text" class="Input_self"
								id="<bean:write name='element' property='object_id' filter='true' />_pratical"
								onkeypress='event.returnValue=IsDigit(this);' onblur='checkValue(this)'
								value="<bean:write name='element' property='pratical' filter='true' />"
								onkeydown="if (event.keyCode==38) goMove(this,'_pratical',-1);if (event.keyCode==40) goMove(this,'_pratical',1);"
								<logic:notEqual name="dataCollectForm" property="rule" value="3">
									onchange='if(isNumberz(this)){basicCalcu(this);}'
								</logic:notEqual>
								<logic:equal name="dataCollectForm" property="isReadOnly" value="1">readonly="readonly"</logic:equal>>
						</td>
						<td align="right" class="RecordRow" style="border-top: none;" nowrap>
							<input type="text" class="Input_self2"
								id="<bean:write name='element' property='object_id' filter='true' />_basic"
								readonly='true'
								value="<bean:write name='element' property='basic' filter='true' />">
						</td>
						<td align="right" class="RecordRow" style="border-top: none;" nowrap>
							<input type="text" class="Input_self2"
								id="<bean:write name='element' property='object_id' filter='true' />_add"
								readonly='true'
								value="<bean:write name='element' property='add' filter='true' />">
						</td>
						<td align="right" class="RecordRow" style="border-top: none;" nowrap>
							<input type="text" class="Input_self2"
								id="<bean:write name='element' property='object_id' filter='true' />_deduc"
								readonly='true'
								value="<bean:write name='element' property='deduc' filter='true' />">
						</td>
						<logic:equal name="dataCollectForm" property="rule" value="3">
							<td align="right" class="RecordRow" style="border-top: none;" nowrap>
								<input type="text" class="Input_self2"
									id="<bean:write name='element' property='object_id' filter='true' />_rank"
									readonly='true'
									value="<bean:write name='element' property='rank' filter='true' />">
							</td>
						</logic:equal>
						<td align="right" class="RecordRow" style="border-top: none;" nowrap>
							<input type="text" class="Input_self2"
								id="<bean:write name='element' property='object_id' filter='true' />_df"
								readonly='true'
								value="<bean:write name='element' property='df' filter='true' />">
						</td>
					</logic:notEqual>
				</logic:equal>
			</tr>
		</hrms:extenditerate>
		</table>
		<script language='javascript'>
			document.write("</div>");
		</script>
		<div id="date_panel2" style="display:none;z-index:50">

		   <select id="date_box" name="contenttype"  onblur="javascript:this.style.display='none';"  multiple="multiple"  style="width:200px;" size="6"  ondblclick="setSelectValue();">
           </select>
           </div>
		
		<table id='pageTable' align="left" class="RecordRowP">
			<tr>
				<td valign="bottom" align="left" class="tdFontcolor">
					第
					<bean:write name="dataCollectForm"
						property="setlistform.pagination.current" filter="true" />
					页 共
					<bean:write name="dataCollectForm"
						property="setlistform.pagination.count" filter="true" />
					条 共
					<bean:write name="dataCollectForm"
						property="setlistform.pagination.pages" filter="true" />
					页
				</td>
				<td align="right" nowrap class="tdFontcolor">
					<p align="right">
						<hrms:paginationlink name="dataCollectForm"
							property="setlistform.pagination" nameId="setlistform"
							propertyId="roleListProperty">
						</hrms:paginationlink>
				</td>
			</tr>
		</table>
	</logic:notEqual>
	<logic:equal name="dataCollectForm" property="isShowTargetTrace" value="1">
		<script language='javascript' >
			//document.location="/performance/achivement/dataCollection/dataCollect.do?br_target=link";
			if(theObjectType=="2")//人员
				document.location="/performance/achivement/dataCollection/dataCollect.do?br_target_emp=link";
			else if(theObjectType=="3")//单位
				document.location="/performance/achivement/dataCollection/dataCollect.do?br_target_un=link";
			else//部门和团队
				document.location="/performance/achivement/dataCollection/dataCollect.do?br_target_um=link";
		</script>
	</logic:equal>
	<script>
	var pageTable = document.getElementById("pageTable");
	if(pageTable)
		pageTable.style.width=(width-17)+"px";
	var schname="${dataCollectForm.searchname}";
	var objectid="${dataCollectForm.searchid}"
	if(schname){
		 var nodes = document.getElementById("tbl").childNodes;
		 var tbodyNode = "";
		 for(var i=0;i<nodes.length;i++){
			 if(nodes[i].tagName=="TBODY"){
				 tbodyNode =  nodes[i];
				 break;
			 }
		 }
		 if(!Ext.isEmpty(tbodyNode)){
			 var trs = tbodyNode.childNodes;
			 var zz = 0;
			 for(var i=0;i<trs.length;i++){
				 if(trs[i].tagName=="TR"){
					 if(i>1)
						zz = trs[i].offsetHeight;
					 var td = null;
					 var tds = trs[i].childNodes;
					 for(var j=0; j<tds.length;j++){
						 if(tds[j].tagName=="TD"){
							 td=tds[j];
							 break;
						 }
					 }
					 if(td!=null){
						 if(trs[i].cells.length>0 && td.innerHTML.length>0 && td.innerHTML==objectid){
							 trs[i].className='cell_yellow';
							 document.getElementById("tbl-container").scrollTop=zz-60;
						 }else{
							 trs[i].className='';
						 }
					 }
				 }
			 }
		 }
		}
		

		var aa=document.getElementsByTagName("input");
		for(var i=0;i<aa.length;i++){
			if(aa[i].type=="text"){
				aa[i].className="inputtext";
			}
		}
		function showOrHideDateDanel2(status){
			var date_panel2 = document.getElementById('date_panel2');
			if(date_panel2)
				date_panel2.style.display=status;
		}
</script>
</html:form>
</body>
