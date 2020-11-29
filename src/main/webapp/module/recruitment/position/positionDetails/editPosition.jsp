<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>职位详情浏览页面</title>
<script language="JavaScript" src="../../../../../js/function.js"></script>
<script language="JavaScript" src="../../../../../components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="../../../../../components/codeSelector/deepCodeSelector.js"></script>
<script language="JavaScript" src="../../../../../components/tableFactory/tableFactory.js"></script>
<script language="JavaScript" src="../../../../../components/personPicker/PersonPicker.js"></script>
<script language="JavaScript" src="../../../../../components/dateTimeSelector/dateTimeSelector.js"></script>
<script language="JavaScript" src="../../../../../module/recruitment/position/positionDetails/editPosition.js"></script>
<script language="JavaScript" src="../../../module/recruitment/recruitment_resource_zh_CN.js"></script>
<link href="../../../../../components/personPicker/PersonPicker.css" rel="stylesheet" type="text/css"><link >
<link href="../../../../../module/recruitment/css/style.css" rel="stylesheet" />
</head>
<style>
body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,img,div,dl,dt,dd,span,table,tr,td{margin:0;padding:0; border:none;}
.hj-zm-cj-gzzzz{width:97%;height:90px; line-height:18px; border:0; color:#333;margin-top:8px; overflow:hidden;font-family:"微软雅黑";font-size:12px !important;}
input{
	font-family:"微软雅黑";
	font-size:12px !important;
	color:#333;
}
.hj-zm-xq-one {
    margin-top: 0px;
    line-height: 36px;
}
</style>
<body> 
<input type="hidden" value="<bean:write name="positionForm" property="havaPerson"/>" id="personNum">
    <div class="hj-wzm-xq-all">
        <div class="hj-cj-all" id="divf">
            <div class="hj-zm-xq-two" id="choosePesron" style="display:${positionForm.display}">
                <h2 style="padding-left: 25px;">　招聘人员</h2>
                <div class="hj-zm-cj-zwmc">
                    <table width="100%" border="1" cellpadding="0" cellspacing="0">
                      
                      <tr>
                      <td style="width: 25px;"></td>
                        <td class="verticall" style="height:60px">招聘负责人&nbsp;&nbsp;</td>
                        <td align="left" width="500">
                            <div class="hj-nmd-dl">
                                <dl>
                                 	<dt id="responsTitle"><a href="javascript:void(0)" style="cursor: default;"><img class="img-circle" id="responsPosiPic" src="${positionForm.photosrc}" /></a></dt>
                                      <dd id ="responsPosiName">${positionForm.responsPosiName}</dd>
                                </dl>
                                <input type="hidden" id="responsPosiId" value="${positionForm.reponsA0100}"/>
                            </div>
                            <logic:notEqual name="positionForm" property="z0319" value="04">
                                <a href="javascript:void(0)" style=" line-height:50px;" onclick="Global.pickPerson(this,1)">转给他人负责</a>
                           </logic:notEqual>
                        </td>
                        <td align="left"></td>
                      </tr>
                      
                      <tr>
                      <td style="width: 25px;"></td>
                        <td class="verticall" style="height:60px">招聘成员&nbsp;&nbsp;</td>
                        <td id="addTd1" align="left" width="500">
                        <logic:notEqual name="positionForm" property="z0319" value="04">
                        <a id="addA1" href="javascript:void(0)" style=" line-height:50px;white-space: nowrap;" onclick="Global.addPerson(this,1)">添加招聘成员</a>
                        </logic:notEqual>
                        </td>
                        <td align="left"></td>
                      </tr>
                      
                      <tr>
                      <td style="width: 25px;"></td>
                        <td class="verticall" style="height:60px">部门负责人&nbsp;&nbsp;</td>
                        <td id="addTd2" align="left" width="500">                         
                             
                               <logic:notEqual name="positionForm" property="z0319" value="04">
                            <a id="addA2" href="javascript:void(0)" style=" line-height:50px;" onclick="Global.addPerson(this,2)">添加部门负责人</a>
                            </logic:notEqual>
                        </td>
                        <td align="left"></td>
                      </tr>
                     
                    
                    </table>

                </div>
            </div>
             <div class="bh-space"></div>
         <input type="hidden" id="from" value="${positionForm.from}" />
        </div>
    </div>
</body>
<script type="text/javascript">
     var pageDesc = "${positionForm.pageDesc}";
     var z0319 = "${positionForm.z0319}";
     var jsonStr = ${positionForm.jsonStr};
     var privChannel = ${positionForm.privChannel};
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
     Ext.onReady(function() {
    	 toInnitTextArea();
     });
	//文本域显示自适应
     function resize(id) {
         	var text = Ext.getDom(id);
    		var parentElement = text.parentNode;
    		if(120 < text.scrollTop + text.scrollHeight){
    	    text.style.height = text.scrollTop + text.scrollHeight+'px';
    		parentElement.style.height = 40+text.scrollTop + text.scrollHeight+'px';
    		//text.style.height = 'auto';
    		}
    		else{
    			//parentElement.style.height = "155px";
    			//parentElement.style.fontSize="12px,!important";
    			//parentElement.style.fontFamily="微软雅黑";
    			text.style.height="120px";
    		}
    }

     /** 给所有的文本域添加事件，让其能够根据内容自适应 */
     function toInnitTextArea() {
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
     };
     
     Global.createdatapageHtml(jsonStr);
</script>
</html>
