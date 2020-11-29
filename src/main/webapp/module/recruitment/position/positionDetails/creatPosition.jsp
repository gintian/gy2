<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>软件</title>
<script language="JavaScript" src="../../../../../js/function.js"></script>

<script language="JavaScript" src="../../../../../module/utils/js/template.js"></script>
<script language="JavaScript" src="../../../../../components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="../../../../../components/codeSelector/deepCodeSelector.js"></script>
<script language="JavaScript" src="../../../../../components/tableFactory/tableFactory.js"></script>
<script language="JavaScript" src="../../../../../components/personPicker/PersonPicker.js"></script>
<script language="JavaScript" src="../../../../../components/dateTimeSelector/dateTimeSelector.js"></script>
<script language='JavaScript' src="../../../../../components/extWidget/proxy/TransactionProxy.js"></script>
<script language="JavaScript" src="../../../module/recruitment/recruitment_resource_zh_CN.js"></script>
<script language="JavaScript" src="../../../../../module/recruitment/position/positionDetails/creatPosition.js"></script>

<link href="../../../../../module/recruitment/css/style.css" rel="stylesheet" />
<link href="../../../../../components/personPicker/PersonPicker.css" rel="stylesheet" type="text/css"><link >
</head>
<style>
body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,img,div,dl,dt,dd,span,table,tr,td{margin:0;padding:0; border:none;}
input{
	font-family:"微软雅黑";
	font-size:12px !important;
	color:#333;
}
</style>
<body>
<input type="hidden" id="iscontinuehidden" value="${positionForm.iscontinue}"/>
<div id="funcDiv" style="display: none;">
    <div class="hj-wzm-zp-all">
        <div class="hj-cj-all" id="divf">
            <div class="hj-cj-all-title" id="difS"></div>
            <div class="hj-zm-cj-two" id="choosePesron" style="display:${positionForm.display}">
                <h2>　招聘人员</h2>
                <div class="hj-zm-cj-zwmc">
                    <table width="100%" border="1" cellpadding="0" cellspacing="0">
                      
                      <tr>
                        <td class="verticall" width="1%"><div>&nbsp;招聘负责人&nbsp;&nbsp;</div></td>
                        <td align="left" width="500">
                            <div class="hj-nmd-dl">
                                <dl>
                                    <dt id="responsTitle" title="${positionForm.responsPosiName}"><a href="javascript:void(0)" style="cursor: default;"><img class="img-circle" id="responsPosiPic" src="${positionForm.photosrc}" /></a></dt>
                                    <dd id ="responsPosiName">${positionForm.responsPosiName}</dd>
                                </dl>
                                <input type="hidden" id="responsPosiId" value="${positionForm.reponsA0100}"/>
                            </div>
                            <a href="javascript:void(0)" style=" line-height:50px;" onclick="Global.pickPerson(this,1)">转给他人负责</a>
                        </td>
                        <td align="left"></td>
                      </tr>
                      
                      <tr>
                        <td class="verticall" width="1%">&nbsp;招聘成员&nbsp;&nbsp;</td>
                        <td id="addTd1" align="left" width="500">
                            <a id="addA1" href="javascript:void(0)" style=" line-height:50px;white-space: nowrap;" onclick="Global.addPerson(this,1)">添加招聘成员</a>
                        </td>
                        <td align="left"></td>
                      </tr>
                      
                      <tr>
                        <td class="verticall" width="1%">&nbsp;部门负责人&nbsp;&nbsp;</td>
                        <td id="addTd2" align="left" width="500">
                            <a id="addA2" href="javascript:void(0)" style=" line-height:50px;" onclick="Global.addPerson(this,2)">添加部门负责人</a>
                        </td>
                        <td align="left"></td>
                      </tr>
                     
                    
                    </table>

                </div>
            </div>
             <div class="bh-space"></div>
        </div>
    </div>
    </div>
</body>
<script type="text/javascript">
     var jsonStr = ${positionForm.jsonStr};
     var ispublish = "${positionForm.isPublish}";
     var privChannel = ${positionForm.privChannel};
     var responsPosiName = "${positionForm.responsPosiName}";
     if(responsPosiName.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
         var char_length = 0;
         for (var i = 0; i < responsPosiName.length; i++){
             var son_str = responsPosiName.charAt(i);
             encodeURI(son_str).length > 2 ? char_length += 1 : char_length += 0.5;
             if (char_length >= 3){
                 var sub_len = char_length == 3 ? i+1 : i;
                 responsPosiName = responsPosiName.substr(0, sub_len);
             }
         }
    }
     document.getElementById("responsPosiName").innerText =responsPosiName ;
     privChannel = eval(privChannel);
     var zp_pos_apply_start_field = "${positionForm.zp_pos_apply_start_field}";
     var zp_pos_apply_end_field = "${positionForm.zp_pos_apply_end_field}";
     
     /** 文本域高度自适应 */
     var adapt = adapt || {
         minHeight: 90,
         adaptTextareaHeight: function(t) { // 文本域高度自适应
             var areas = [];
             
             if (t) { // 指定对某一个文本域自适应
                 areas[0] = t;
             } else { // 对所有的文本域自适应
                 areas = document.getElementsByTagName("textarea");
             }

             var _area = "z0361,z0363";
             
             for (var i = 0; i < areas.length; i++) {
                 // 不对总结之外的文本域做修改
                 if (_area.indexOf(areas[i].id) < 0 || !areas[i].id) {continue;}
                 
                 var btw = adapt.style(areas[i]).borderTopWidth;
                 var bbw = adapt.style(areas[i]).borderBottomWidth;
                 
                 var iBtw = parseInt(btw.substring(0, btw.length - 2)) || 0;
                 var iBbw = parseInt(bbw.substring(0, bbw.length - 2)) || 0;
                 
                 areas[i].style.height = adapt.minHeight + "px";
                 
                 var adaptHeight = areas[i].scrollHeight + iBtw + iBbw;
                 adaptHeight = adaptHeight < adapt.minHeight ? adapt.minHeight : adaptHeight;
                 
                 areas[i].style.height = adaptHeight + "px";
             }
         },
         style: function(elmt) { // 获取元素计算后的样式
             if (elmt.currentStyle) {
                 return elmt.currentStyle;
             } else {
                 return window.getComputedStyle(elmt);
             }
         },
         bind: function(fn, thisObj) { // 创建闭包环境,用于参数传递
             if (!fn || typeof fn !== "function") {return null;}
             
             var args = [];
             if (arguments[2]) {
                 for (var i = 2; i < arguments.length; i++) {
                     args[args.length] = arguments[i];
                 }
             }
             
             return (function() {
                 fn.apply(thisObj, args);
             });
         }
         
     };

     /** 给所有的文本域添加事件，让其能够根据内容自适应 */
     Ext.onReady(function() {
    	 Global.createpageHtml(jsonStr);
    	 Ext.widget('viewport',{
             layout:'border',
             padding:"0 5 0 5",
             style:'backgroundColor:white',
             items:[{
                       xtype:'panel',
                       id:'view_panel',
                       title:"<div style='float:left'>创建职位</div><div id='titilPanel' ></div>",
                       html:"<div id='topPanel'></div>",
                       region:'center',
                       border:false
                     }]
         });
    	 Ext.get("view_panel-headingEl").destroy();
         document.getElementById('titilPanel').appendChild(document.getElementById('operation'));
         document.getElementById('titilPanel').style.marginLeft=document.body.clientWidth-350+"px";
         document.getElementById('topPanel').appendChild(document.getElementById('funcDiv'));
         document.getElementById('funcDiv').style.display="block";
         var view_panel = Ext.getCmp('view_panel');
         view_panel.setAutoScroll(true);
         var winHeight =parent.document.body.clientHeight;
         view_panel.setHeight(winHeight);
    	 
         var areas = document.getElementsByTagName("textarea");

         var _area = Global.textAreas;
         
         for (var i = 0; i < areas.length; i++) {
             // 不对总结之外的文本域做修改
             if (_area.indexOf(areas[i].id) < 0) {continue;}
             
             if ("oninput" in areas[i]) { // W3C标准浏览器
                 areas[i].oninput = adapt.bind(adapt.adaptTextareaHeight, null, areas[i]);
             } else { // IE
                 function adapt4IE(t) {
                     t.style.height = (t.scrollHeight > adapt.minHeight ? t.scrollHeight : adapt.minHeight) + "px";
                 }
                 areas[i].onpropertychange = adapt.bind(adapt4IE, null, areas[i]);
                 areas[i].onkeyup = adapt.bind(adapt4IE, null, areas[i]);
             }
         }

         adapt.adaptTextareaHeight();
         
     });
     
</script>
</html>