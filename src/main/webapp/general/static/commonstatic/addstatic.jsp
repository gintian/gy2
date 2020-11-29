<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.statics.StaticFieldForm"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String bosflag = "";
	if (userView != null) {
		bosflag = userView.getBosflag();
	}
	//非IE浏览器中使用属性 用来区分状态    wangb 20180127
	String count = request.getParameter("count")==null? "":request.getParameter("count");
	
	String type = request.getParameter("type")==null? "":request.getParameter("type");//修改统计分类 type参数值为categories wangb 201907904
%>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
<!--
//选择统计图标 wangbs 20190325
var statisticalIconUrl='/images/statistical/';
var selectedIconName = "${staticFieldForm.photo}";
function submitT()
{
  var hashvo=new ParameterSet();
  <%if(type == null || !"categories".equalsIgnoreCase(type)){%>
  var titles=$F('stat_name');
  if(titles==null||titles=="")
  {
     alert("统计条件名称不能为空！");
    return false;
  }
  
  if(IsOverStrLength(titles, 20))
  {
	  alert("统计条件名称长度不能超过10个汉字！");
	  return false;
  }
  
  if(titles.indexOf("\‘")>-1||titles.indexOf("\”")>-1||titles.indexOf("\'")>-1||titles.indexOf("\"")>-1)
  {	
      alert("统计条件名称不能包含\’或\"或\’或\”");
      return false;
  }     
  <%}%>
  var  hidcategories=$F('hidcategories');
  if(IsOverStrLength(hidcategories, 200))
  {
	  alert("分类名称长度不能超过100个汉字！");
	  return false;
  }
  
  if(hidcategories.indexOf("\‘")>-1||hidcategories.indexOf("\”")>-1||hidcategories.indexOf("\'")>-1||hidcategories.indexOf("\"")>-1)
  {	
      alert("分类名称不能包含\’或\"或\’或\”");
      return false;
  } 
   <%if(type == null || !"categories".equalsIgnoreCase(type)){%>
  	hashvo.setValue("stat_name",titles);
  	var vost= document.getElementsByName("findlike");
  	var objt=vost[0];
  	var find="0";
  	if(objt.checked==true)
  	{
                  find="1";
    }
   	hashvo.setValue("find",find);              
   	hashvo.setValue("statid","${staticFieldForm.statid}");
   	hashvo.setValue("infor_Flag","${staticFieldForm.infor_Flag}"); 
   	if(document.getElementById("viewtypeValue")){
   		hashvo.setValue("viewtypeValue",getEncodeStr(document.getElementById("viewtypeValue").value));   
   	}
   <%}%>
   <%if("categories".equalsIgnoreCase(type)){%>
       hashvo.setValue("type","categories");
       var old_categories = '${staticFieldForm.categories }'.replace(/\n/g,'\\n');
       hashvo.setValue("old_categories",getEncodeStr(old_categories));
   <% } %>
   hashvo.setValue("opflag","${staticFieldForm.opflag}"); 
    
   hashvo.setValue("categories",getEncodeStr(hidcategories)); 
   hashvo.setValue("photo",selectedIconName);
   var org_filter = document.getElementsByName('org_filter')[0];
   if(org_filter)
   	  hashvo.setValue("org_filter",org_filter.checked? "1":"0");
   var request=new Request({onSuccess:submitRe,functionId:'11080204056'},hashvo);  
}
function submitRe(outparamters)
{
	
  var opflag=outparamters.getValue("opflag"); 
  if(opflag=="true")
  {
      alert("操作成功！");
      var vo=new Object();
      vo.flag="true";
      var action=outparamters.getValue("action");
      vo.action=action;
      var text=outparamters.getValue("text");
      vo.text=text;
      var uid=outparamters.getValue("uid");
      vo.uid=uid;
      var xml=outparamters.getValue("xml");
      vo.xml=xml;
      var categories=outparamters.getValue("categories");
      vo.categories=getDecodeStr(categories);
      vo.oldcategories='${staticFieldForm.categories }';
      if(getBrowseVersion()){
      	parent.window.returnValue=vo;
      }else{//非IE浏览器  open 回调方法方式 返回数据   wangb  20180127
		parent.opener.openReturn(vo,'<%=count%>');
      }
      parent.window.close();
  }else
  {
     alert("操作失败！");
  }
}
function screen(obj,flag)
{
  var targetvalue=document.getElementById(flag); 
  targetvalue.value=obj.value; 
}
var dictLeft;
var dictTop;
function   addDict(obj,event,flag)
{ 
   var evt = event ? event : (window.event ? window.event : null);
   var np=   evt.keyCode; 
   if(np==38||np==40){ 
   
   } 
   var textv=obj.value;
   var aTag = document.getElementById('groupname').offsetParent;
   //	aTag = obj.offsetParent; 
   if(textv==null||textv=="")
	   return false;
   textv=textv.toLowerCase();  
   var un_vos=document.getElementsByName(flag+"_value");
   if(!un_vos)
		return false;
   var unStrs=un_vos[0].value;	
   var unArrs=unStrs.split(",");
   var   c=0;
   var   rs   =new   Array();
   for(var i=0;i<unArrs.length;i++)
   {
		 var un_str=unArrs[i];
		 if(un_str)
		 {
		     if(un_str.indexOf(textv)!=-1)
	         {
			     rs[c]="<tr id='tv' name='tv'><td id='al"+c+"'  onclick=\"onV("+c+",'"+flag+"')\"  style='height:15;cursor:pointer' onmouseover='alterBg("+c+",0)' onmouseout='alterBg("+c+",1)' nowrap class=tdFontcolor>"+un_str+"</td></tr>"; 
                 c++;
		     }
		 
		 }
        
	}
    resultuser=rs.join("");
    if(textv.length==0){ 
       resultuser=""; 
    } 
    if(resultuser){
    	document.getElementById("dict").innerHTML="<table   width='100%' class='div_table'  cellpadding='2' border='0'  bgcolor='#FFFFFF'   cellspacing='2'>"+resultuser+"</table>";//???????????????? 
    	document.getElementById('dict').style.display = "";
    	document.getElementById('dict').style.position="absolute";
    	if(!getBrowseVersion() && !dictLeft){
    		dictLeft=aTag.offsetLeft +2;dictTop = aTag.offsetTop+30;
    	}else if(!dictLeft){
    		dictLeft=aTag.offsetLeft +61;dictTop =aTag.offsetTop+31;
    	}
		document.getElementById('dict').style.left=dictLeft+10+"px";
   		document.getElementById('dict').style.top=dictTop+"px";
    }
} 
function onV(j,flag){
   var  o =   document.getElementById('al'+j).innerHTML; 
   document.getElementById(flag).value=o; 
   document.getElementById(flag+"select").value=o;
   document.getElementById('dict').style.display = "none";
} 
function   alterBg(j,i){
    var   o   =   document.getElementById('al'+j); 
    if(i==0) 
       o.style.backgroundColor   ="#3366cc"; 
    else   if(i==1) 
       o.style.backgroundColor   ="#FFFFFF"; 
}
function hiddendict(){
	document.getElementById('dict').style.display = 'none';
}
function isSelect(obj) {
	var selectValue = obj.options[obj.selectedIndex].value;//显示value
	//var selectValue2 = obj.options[obj.selectedIndex].innerText;//显示值
	document.getElementById("viewtypeValue").value = selectValue;
}

function showSelectIconWin(){
	var selectIconWin = Ext.create("Ext.window.Window",{
		title:"选择图标",
		id:'selectIconWin',
		resizable:false,//禁止拉伸
		modal : true,//遮罩
		bodyPadding:20,
		height:157,
		width:590
	});
    var IconString = '${staticFieldForm.iconList}';
	IconString = IconString.substring(1,IconString.length-1);
	var iconArray = IconString.split(",");
	for(var i=0;i<iconArray.length;i++){
		iconArray[i] = Ext.String.trim(iconArray[i]);
	}

	//不显示已选择的图标
    var statisticalIconDom = document.getElementById("statisticalIconDom");
	if(statisticalIconDom){
    	var selectedIcon = statisticalIconDom.src;

		for(var i=0;i<iconArray.length;i++){
			if(selectedIcon.indexOf(iconArray[i])>-1){
				Ext.Array.removeAt(iconArray,i);
			}
		}
	}


    //计算需要几组panel存放icon
    var iconlength = iconArray.length;
    var panelCount = parseInt(iconlength/5);
    var iconremiander = iconlength%5;
    if(iconremiander>0){
        panelCount=panelCount+1;
    }
    var panelArray = [];
    for(var i=0;i<panelCount;i++){
        //行panel放置icon
        var rowPanel = Ext.create('Ext.Panel',{
            layout:'hbox',
            border:false
        });
        createIconContainer(rowPanel,i,iconArray,selectIconWin);
        //拼装Carousel组件的item
        panelArray.push(rowPanel);
    }
    //创建轮播图组件
    var carousel = Ext.create('EHR.carousel.Carousel',{
        items:panelArray
    });
    selectIconWin.add(carousel);
    selectIconWin.show();
}
//循环创建icon放入行panel wangbs 20190326
function createIconContainer(rowPanel,i,iconArray,selectIconWin){
    var beginIdex = i*5;
    var endIdex = (i+1)*5;
    if(endIdex>iconArray.length){
        endIdex =iconArray.length;
    }
    for(beginIdex;beginIdex<endIdex;beginIdex++){
        var icon = iconArray[beginIdex];
        var iconSrc = statisticalIconUrl+icon;

        var selectIcon = Ext.create('Ext.Img',{
            margin:'0 25 0 0',
            height:72,
            width:72,
            itemId:icon,
            src:iconSrc,
            listeners:{
                element:'el',
                click:function(){
					selectedIconName = this.component.itemId;
                    var statisticalIconDom = document.getElementById("statisticalIconDom");
                    if(statisticalIconDom){
						statisticalIconDom.src = this.dom.src;
						statisticalIconDom.style.display = "";
					}
                    var selectStatisticalIconDom = document.getElementById("selectStatisticalIcon");
                    if(selectStatisticalIconDom){
                    	selectStatisticalIconDom.style.display = "none";
					}
                    selectIconWin.close();
                }
            }
        });
        rowPanel.add(selectIcon);
    }
}
//-->
</script>
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
	Font-family: ????;
	font-size: 12px;
	height
	=20px;
	align
	="center"
}
-->
</style>
<hrms:themes />
<%
	if ("hcm".equalsIgnoreCase(bosflag)) {
%>
<style>
.StaticTable {
	width: expression(document.body.clientWidth-10);
	margin-left: 1px;
}

.TableRow {
	border-left: medium none;
	border-right: medium none;
}
</style>
<%
	} else {
%>
<style>
.StaticTable {
	margin-top: 10px;
	width: expression(document.body.clientWidth-10);
	margin-left: -5px;
}

.TableRow {
	border-left: medium none;
	border-right: medium none;
}
</style>
<%
	}
%>
<body onclick="hiddendict();">
	<html:form action="/general/static/commonstatic/editstatic">
	<% if(type == null || !"categories".equalsIgnoreCase(type)){%>
		<table border="0" cellspacing="0" align="center" cellpadding="0"
			class="StaticTable">
			<tr>
				<td colspan="2" class="framestyle1">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0">
						<tr align="left">
							<td colspan="2" valign="middle" class="TableRow"
								style="border-left: 0px; border-right: 0px;">
								常用统计
							</td>
						</tr>
						<tr>
							<td colspan="2" height="5"></td>
						</tr>
						<tr align="center">
							<td width="20%" align="right">名称&nbsp;</td>
							<td align="left"><html:text name="staticFieldForm" styleClass="text4"
									style="height:20px;width:222px" property='stat_name' size="30"
									maxlength='30' />
							</td>
						</tr>
						<tr>
							<td colspan="2" height="10"></td>
						</tr>
						<tr align="center">
							<td width="20%" align="right">分类名称&nbsp;</td>
							<td align="left">
								<div id="groupname" style="overflow:hidden;height:22px;">
									<html:select name="staticFieldForm" property='categories'
										styleId="hidcategoriesselect"
										style="width:222px;height:22px;"
										onchange="screen(this,'hidcategories');" onfocus=''>
										<option value=""></option>
										<html:optionsCollection property="catelist" value="dataValue"
											label="dataName" />
									</html:select>
									<input name=categories id='hidcategories' type="text"
										style="position: relative;top:-20px;left:1px;border:none; width: 204px; height: 18px;line-height:20px;"
										value='${staticFieldForm.categories }'
										onkeyup="addDict(document.getElementById('hidcategories'),event,'hidcategories');">
									<input type="hidden" name="hidcategories_value"
										value='${staticFieldForm.hidcategories }' />
								</div>
							</td>
						</tr>

						<!-- ----------------------华丽的分割线------------- -->
						<logic:notEqual name="staticFieldForm" property="type" value="3">
						<tr>
							<td colspan="2" height="10"></td>
						</tr>
						<tr align="center">
							<td width="20%" align="right">默认图形&nbsp;</td>
							<td align="left">
								<html:select name="staticFieldForm" property="viewtype" style="width:222px;" styleId="viewtype" onchange="isSelect(this);">
									<option value=""></option>
									<html:optionsCollection property="viewtypelist" value="dataValue" label="dataName" />
								</html:select>
								<input type="hidden" id="viewtypeValue" value="${staticFieldForm.viewtype}" />
							</td>
						</tr> 
						<!-- ----------------------华丽的分割线------------- -->

						</logic:notEqual>
						<tr>
							<td colspan="2" height="10"></td>
						</tr>
						<tr align="center">
							<td width="20%" align="right">图标&nbsp;</td>
							<td align="left">
								<logic:equal name="staticFieldForm" property="photo" value="">
									<div id="selectStatisticalIcon" onclick="showSelectIconWin();" style="color: #0093c9;cursor: pointer;width: 50px;">选择图标</div>
									<img id="statisticalIconDom" src="" onclick="showSelectIconWin();" style="display:none;cursor:pointer;height:74px;width:74px;"/>
								</logic:equal>
								<logic:notEqual name="staticFieldForm" property="photo" value="">
									<img id="statisticalIconDom" src="" onclick="showSelectIconWin();" style="cursor:pointer;height:74px;width:74px;"/>
								</logic:notEqual>
							</td>
						</tr>
						<!-- ----------------------华丽的分割线------------- -->
						
						<tr height="20">
							<td>
								&nbsp;
							</td>
						</tr>
						<tr align="center" class="list3">
							<td colspan="2">
								<logic:equal name="staticFieldForm" property="findlike"
									value="1">
									<input type="checkbox" name="findlike" value="1" checked>&nbsp;<bean:message
										key="infor.menu.query.data" />&nbsp;&nbsp;
                    </logic:equal>
								<logic:notEqual name="staticFieldForm" property="findlike"
									value="1">
									<input type="checkbox" name="findlike" value="1">&nbsp;<bean:message
										key="infor.menu.query.data" />&nbsp;&nbsp;
                    </logic:notEqual>
                    			<logic:equal name="staticFieldForm" property="org_filter"
									value="1">
									<input type="checkbox" name="org_filter" value="1" checked>&nbsp;按组织机构筛选&nbsp;&nbsp;
                    			</logic:equal>
                    			<logic:notEqual name="staticFieldForm" property="org_filter"
									value="1">
									<input type="checkbox" name="org_filter" value="1">&nbsp;按组织机构筛选&nbsp;&nbsp;
                    			</logic:notEqual>
							</td>
						</tr>
						<tr height="10">
							<td>
								&nbsp;
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<% }else{ %><%--修改统计分类   wangb 20190704 --%>
		<table width="98%" border="0" cellspacing="0" align="center" cellpadding="0" class="StaticTable">
			<tr>
				<td colspan="2" class="framestyle1">
					<table width='100%' border="0" cellspacing="0" align="center" cellpadding="0">
						<tr align="left">
						<td colspan="2" valign="middle" class="TableRow"
								style="border-left: 0px; border-right: 0px;">
								常用统计
						</td>
						</tr>
						<tr align="center">
						<td width="20%" align="right">分类名称&nbsp;</td>
						<td height="80px" valign="middle" align="left">
							<div id="groupname" style="overflow:hidden;height:22px;">
								<input name=categories id='hidcategories' type="text"
									value='${staticFieldForm.categories }'
									onkeyup="addDict(document.getElementById('hidcategories'),event,'hidcategories');">
								<input type="hidden" name="hidcategories_value" value='${staticFieldForm.hidcategories }' />
							</div>
						</td>
						</tr>
					
					</table>
				</td>
			</tr>
		</table>
		
		<% } %>
		
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0">
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td align="center">

					<html:button styleClass="mybutton" property="" onclick="submitT();">
						<bean:message key="button.ok" />
					</html:button>
					<html:button styleClass="mybutton" property=""
						onclick="parent.window.close();">
						<bean:message key="button.close" />
					</html:button>
				</td>
			</tr>
		</table>
	</html:form>
	<div id="dict"
		style="display: none; z-index: +999; position: absolute; height: 100px; width: 222px; overflow: auto;"></div>
</body>
<script type="text/javascript"> 
<% if(type == null || !"categories".equalsIgnoreCase(type)){%>
if(!getBrowseVersion()){//非IE浏览器兼容性问题   wangb 20180127 
 	var form = document.getElementsByName('staticFieldForm')[0];
 	var firsttable = form.getElementsByTagName('table')[0];
 	firsttable.style.width='99%';
 	firsttable.removeAttribute('class');
 	firsttable.setAttribute('align','left');
 	//var selectdiv = document.getElementsByClassName('complex_border_color')[0];
 	//selectdiv.style.border ='0px';
 
 	var stable = form.getElementsByTagName('table')[1];
 	var td = stable.getElementsByTagName('tr')[2].getElementsByTagName('td')[0];
 	td.setAttribute('width','30%');
}else{
	var stat_name = document.getElementsByName('stat_name')[0];
	stat_name.style.height='16px';
	stat_name.style.width='218px';
 	var hidcategories = document.getElementById('hidcategories');
 	//hidcategories.style.display='none';
 	hidcategories.style.position = 'relative';
 	hidcategories.style.top = '-21px';
 	hidcategories.style.width = '200px';
 	hidcategories.style.height = '18px';
 	hidcategories.style.lineHeight = '18px';
 	var groupname = document.getElementById('groupname');
 	groupname.style.height='26px';
} 
<%} %> 
//该统计有图标 更新图片src wangbs  20190327
var photoPath = "../../../images/statistical/";
var photoTarget = document.getElementById("statisticalIconDom");
if(photoTarget){
	photoTarget.src = photoPath+"${staticFieldForm.photo}";
}  
</script>