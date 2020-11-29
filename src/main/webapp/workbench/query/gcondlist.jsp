<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes />
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
<!-- 引入ext 和代码控件      wangb 20171117 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<script type="text/javascript">
  // 重置的类型
  var firstCategorySelect = null;
 
  function screen(obj,flag){
  	var targetvalue=document.getElementById(flag); 
	targetvalue.value=obj.value; 
	changeCond(targetvalue.value);
	highQueryForm.condname.value='';
  }

window.onload = function() {
	var hidcategoriesselect = document.getElementById("hidcategoriesselect");
	firstCategorySelect = hidcategoriesselect.value;	
}

function changeCond(categories){
	var ids="";
	var texts="";
	var hashVo=new ParameterSet();
	hashVo.setValue("ids",ids);
    hashVo.setValue("categories",categories);
    hashVo.setValue("type","${highQueryForm.type}");
    var request=new Request({method:'post',asynchronous:false,onSuccess:changeCondOk,functionId:'0202001072'},hashVo);
	function changeCondOk(outparameters){
   		var condlist=outparameters.getValue("condlist");
		AjaxBind.bind(highQueryForm.keyid,condlist);
	}		
}
function addDict(obj,event,flag) {
   var evt = event ? event : (window.event ? window.event : null);
   var np=   evt.keyCode; 
   if(np==38||np==40){ 
   
   } 
   var textv=obj.value;
   var aTag;
   	aTag = obj.offsetParent; 
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
    document.getElementById("dict").innerHTML="<table   width='100%' class='div_table'  cellpadding='2' border='0'  bgcolor='#FFFFFF'   cellspacing='2'>"+resultuser+"</table>";//???????????????? 
    document.getElementById('dict').style.display = "block";
    document.getElementById('dict').style.position="absolute";	
	document.getElementById('dict').style.left=obj.offsetLeft;
   	document.getElementById('dict').style.top=obj.offsetTop+20;
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

function getcategories(){
	document.getElementsByName("categories")[0].value=document.getElementsByName("categories")[1].value;
}

function savecond(){
	if(!document.returnValue) {
		return;
	}
	var obj = document.getElementById("cond");
	var name,value;	
	if(obj.selectedIndex != -1) {
			name = obj.options[obj.selectedIndex].text;
			value = name.substring(name.indexOf('.')+1);
			if(confirm('将覆盖常用条件 \"'+ value +'\" ，是否继续？')) {
				highQueryForm.action="/workbench/query/gcondlist.do?b_save=link";
				highQueryForm.submit();
			}		
	} else {
		highQueryForm.action="/workbench/query/gcondlist.do?b_save=link";
		highQueryForm.submit();
	}	
	
}

function resetAll() {
	changeCond(firstCategorySelect);
}



function changecondname(obj){
	if(obj.selectedIndex!=-1){		
		var name=obj.options[obj.selectedIndex].text;
		highQueryForm.condname.value=name.substring(name.indexOf('.')+1);
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
	height:20px;
	align:center;
}
-->
</style>
<body onclick="hiddendict();">
	<html:form action="/workbench/query/gcondlist">
		<table width="700" border="0" cellpadding="0" cellspacing="0"
			align="center" style="margin-top:5px;">
			<tr height="20">
				<td align=left class="TableRow">
					<bean:message key="label.query.gcond" />
				</td>
			</tr>
			<tr>
				<td class="framestyle9">
					<table border="0" cellpmoding="0" cellspacing="0"
						class="DetailTable" cellpadding="0" width="500px" align="center">
						<tr style="height: 30px">
							<td align="right" width="100px">分类名称</td>
							<td>
								<html:select name="highQueryForm" property='categories'
									styleId="hidcategoriesselect"
									style="position:absolute;margin-left:10px;width:360px;height:22px;clip:rect(0 360 25 342);margin-top:-10px;"
									onchange="screen(this,'hidcategories');" onfocus=''>
									<option value=""></option>
									<html:optionsCollection property="catelist" value="dataValue"
										label="dataName" />
								</html:select>
								<input name=categories id='hidcategories'
									style="position: absolute; width: 342px; height: 22px;margin-top:-10px;margin-left:10px;"
									value='${highQueryForm.categories }'
									onkeyup="addDict(this,event,'hidcategories');" onblur="">
								<!--  </div>-->
								<input type="hidden" name="hidcategories_value"
									value='${highQueryForm.hidcategories }' />
							</td>
						</tr>
						<tr>
							<td align="right" nowrap>
								<bean:message key="column.name" />
							</td>
							<td>
								<html:text name="highQueryForm" property="condname"
									maxlength="20" style="width:360px;margin-left:10px;" />
							</td>
						</tr>
						<tr>
							<td height="5"></td>
						</tr>
						<tr>
							<td></td>
							<td align="left" class="tdFontcolor" nowrap>
								<html:select name="highQueryForm" styleId="cond"
									property="keyid" size="1" multiple="false"
									style="height:219px;width:360px;margin-left:5px;"
									ondblclick="changecondname(this);">
									<html:optionsCollection property="condlist" value="id"
										label="name" />
								</html:select>
							</td>
						</tr>
						<tr>
							<td height="5px"></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr class="list3">
				<td align="center" style="height: 35px;">
					<button
						onclick="document.highQueryForm.target='_self';validate('R','condname','条件名称');getcategories();savecond();"
						class="mybutton">
						<bean:message key="button.ok" />
					</button>
					<button  type="button" onclick="changecategories();" class="mybutton">
						条件移至
					</button>
					<!-- 增加重置按钮 xiaoyun 2014-4-29 start -->
					<button type="reset" class="mybutton" onclick="resetAll();">
						重置
					</button>
					<!-- 增加重置按钮 xiaoyun 2014-4-29 end -->
					<hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="button.return" />
					</hrms:submit>
				</td>
			</tr>
		</table>
	</html:form>
	<div id="dict"
		style="display: none; z-index: +999; position: absolute; width: 360px; overflow: auto;"></div>
</body>
<script language="javascript">
	
		var ids="";
		var texts="";
		var hashVo=new ParameterSet();
		hashVo.setValue("ids",ids);
	    hashVo.setValue("categories","${highQueryForm.categories}");
	    hashVo.setValue("type","${highQueryForm.type}");
	    var request=new Request({method:'post',asynchronous:false,onSuccess:changeCondOk,functionId:'0202001072'},hashVo);
		function changeCondOk(outparameters){
	   		var condlist=outparameters.getValue("condlist");
			AjaxBind.bind(highQueryForm.keyid,condlist);
		}
function changecategories(){
	var ids="";
	var texts="";
	var conds=highQueryForm.keyid.options;
	for(var i=conds.length-1;i>=0;i--){
		if(conds[i].selected){
			ids+="','"+conds[i].value;
			texts+="，"+conds[i].text;
		}
	}
	if(ids.length==0){
		alert("请选择要调整分类的常用查询");
		return;
	}else{
		 var theurl="/workbench/query/hquerycond_interface.do?b_change=link";
         var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
         var dw=350,dh=200,dl=(screen.width-dw)/2;
         var dt=(screen.height-dh)/2;
         if(getBrowseVersion()){
          		 var return_vo= window.showModalDialog(iframe_url,0, 
              		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:350px; dialogHeight:200px;resizable:no;center:yes;scroll:no;status:no");
				var categories=document.getElementById('hidcategories').value;
				if(return_vo!=null&&return_vo!=categories){
					var msg="";
					if(return_vo.length==0){
						msg="确认要将["+texts.substring(1)+"]删除分类吗";
					}else{
						msg="确认要将["+texts.substring(1)+"]移动到"+return_vo+"分类下吗";
					}
					if(confirm(msg)){
						var oldCategories = $('hidcategoriesselect').value;
						var hashVo=new ParameterSet();
						hashVo.setValue("ids",ids);
						hashVo.setValue("categories",return_vo);
						hashVo.setValue("oldCategories",oldCategories);
						hashVo.setValue("type","${highQueryForm.type}");
						var request=new Request({method:'post',asynchronous:false,onSuccess:changeCondOk,functionId:'0202001072'},hashVo);
						function changeCondOk(outparameters){
				   			var condlist=outparameters.getValue("condlist");
				   			var oldC=outparameters.getValue("oldCategories");
							AjaxBind.bind(highQueryForm.keyid,condlist);
							$('hidcategories').value=return_vo;
							var conds=$('hidcategoriesselect').options;
							var flag=true;
							for(var i=conds.length-1;i>=0;i--){
								if(conds[i].value==return_vo){
									conds[i].selected=true;
									flag=false;
								}
							}
							if(flag){
								var opp = new Option(return_vo,return_vo);  
								$('hidcategoriesselect').add(opp); 
								$('hidcategoriesselect').value=return_vo;
							}
							
							if(oldC){
								var c_options = $('hidcategoriesselect').options;
								for (var i = 0; i < c_options.length; i++) {
									if(oldC == c_options[i].value) {
										$('hidcategoriesselect').options.remove(i); 
										break;
									}
								}
							}
						}
					}
				}	
          }else{
          		var dialog=[];dialog.dw=dw+10;dialog.dh=dh+30;dialog.iframe_url=iframe_url;
          		openWin(dialog);
          }
	}
}
//非Ie浏览器使用ext 弹窗  wangb 20180205
function openWin(dialog){
		    Ext.create("Ext.window.Window",{
		    	id:'win_sort',
		    	width:dialog.dw,
		    	height:dialog.dh,
		    	title:'请选择',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+dialog.iframe_url+"'></iframe>"
		    }).show();	
}	
//关闭弹窗
function closeWin(){
	Ext.getCmp('win_sort').close();
}
//非IE浏览器弹窗调用方法 wangb 20180205
function returnValue(return_vo){
	var categories=document.getElementById('hidcategories').value;
	if(return_vo!=null&&return_vo!=categories){
			var texts="";
			var conds=highQueryForm.keyid.options;
			for(var i=conds.length-1;i>=0;i--){
				if(conds[i].selected){
					ids+="','"+conds[i].value;
					texts+="，"+conds[i].text;
				}
			}
					var msg="";
					if(return_vo.length==0){
						msg="确认要将["+texts.substring(1)+"]删除分类吗";
					}else{
						msg="确认要将["+texts.substring(1)+"]移动到"+return_vo+"分类下吗";
					}
					if(confirm(msg)){
						var oldCategories = $('hidcategoriesselect').value;
						var hashVo=new HashMap();
						hashVo.put("ids",ids);
						hashVo.put("categories",return_vo);
						hashVo.put("oldCategories",oldCategories);
						hashVo.put("type","${highQueryForm.type}");
						Rpc({functionId:'0202001072',async:false,success:changeCondOk},hashVo);//request 更换成 rpc update by xiegh bug36063
						function changeCondOk(outparameters){
							var result = Ext.decode(outparameters.responseText);
				   			var condlist=result.condlist;
				   			var oldC=result.oldCategories;
							AjaxBind.bind(highQueryForm.keyid,condlist);
							$('hidcategories').value=return_vo;
							var conds=$('hidcategoriesselect').options;
							var flag=true;
							for(var i=conds.length-1;i>=0;i--){
								if(conds[i].value==return_vo){
								conds[i].selected=true;
								flag=false;
							}
						}
						if(flag){
							var opp = new Option(return_vo,return_vo);  
							$('hidcategoriesselect').add(opp); 
							$('hidcategoriesselect').value=return_vo;
						}
						
						if(oldC){
							var c_options = $('hidcategoriesselect').options;
							for (var i = 0; i < c_options.length; i++) {
								if(oldC == c_options[i].value) {
									$('hidcategoriesselect').options.remove(i); 
									break;
								}
							}
						}
						
						closeWin();//条件移动成功后应该关闭当前窗口
					}
				}
			}
}
</script>