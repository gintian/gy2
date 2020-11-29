<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="javascript" src="/general/sprelationmap/relationMap.js"></script>
<style type="text/css">
   .viewTable td{
      padding:0px,10px,0px,10px;
   }
   .styleTable td{
     padding:0px,10px,0px,10px;
     text-align: right;
     white-space:nowrap; 
   }
   .previewTable td{
      text-align: left;
      padding:0px,0px,0px,0px;
   }
   .inputs{
    width:100px;
    border: 1pt solid #C4D8EE;
   }
	.selectDiv{
		padding-top:5px; padding-left:5px;
		padding-right:5px;padding-bottom:5px;
	}	
</style>
<hrms:themes />

<div align="center">
  <html:form action="/general/sprelationmap/show_report_map">
	  <fieldset align="center" style="width:90%;">
	           <legend>视图</legend>
	            <table width="100%" class="viewTable" border=0>
	               
	               <tr>
	                  <td width="100%" >
	                  
	                       <table border=0 width="100%" class="styleTable">
		                      <tr>
		                         <td width="45%">
				                      <html:radio name="relationMapForm" property="chartParam.direction" value="1" ><bean:message key="general.inform.org.graphvaspect"/></html:radio> 
									  <html:radio name="relationMapForm" property="chartParam.direction" value="2"><bean:message key="general.inform.org.graphhaspect"/></html:radio> 
				                  </td>
				                  <td >
				                     		 配色方案
			                        <html:select property="chartParam.theme" name="relationMapForm"   onchange="resetTheme()" styleClass="inputs text4">
			                           <html:option value="0">&nbsp;&nbsp;蓝天白云&nbsp;&nbsp;</html:option>
		                               <html:option value="1">&nbsp;&nbsp;绿意盎然&nbsp;&nbsp;</html:option>
		                               <html:option value="2">&nbsp;&nbsp;红色岁月&nbsp;&nbsp;</html:option>
		                               <html:option value="3">&nbsp;&nbsp;金碧辉煌&nbsp;&nbsp;</html:option>
		                               <html:option value="4">&nbsp;&nbsp;灰度空间&nbsp;&nbsp;</html:option>
		                             </html:select>
				                  </td>
				                  <td width="15%">&nbsp;</td>
		                      </tr>
		                    </table>
		                    
		                    <fieldset >
		                      <legend>单元格属性</legend>
		                      <div class="selectDiv">
		                      <table border=0 width="100%" class="styleTable">
					                 <tr>
					                    <td width="45%">
					                                              主颜色
					                       <html:text  name="relationMapForm" property="chartParam.bgColor"  alt="clrDlg"  style="BACKGROUND-COLOR:${relationMapForm.chartParam.bgColor}" styleClass="inputs text4" readonly="true" />
					                    </td>
					                    <td >
							               过度色
					                       <html:text  name="relationMapForm" property="chartParam.transitcolor"  alt="clrDlg" size="6" style="BACKGROUND-COLOR:${relationMapForm.chartParam.transitcolor}" styleClass="inputs text4" readonly="true"/>
							            </td>
							            <td rowspan="6" width="15%">&nbsp;</td>
					                 </tr>
					                 
					                 <tr>
					                   <td >
					                      边框颜色
					                      <html:text  name="relationMapForm" property="chartParam.border_color"   alt="clrDlg" size="6" style="BACKGROUND-COLOR:${relationMapForm.chartParam.border_color}" styleClass="inputs text4" readonly="true"/>
					                   </td>
					                   <td >
					                                              边框宽度
					                      <html:select property="chartParam.border_width" name="relationMapForm" styleClass="inputs" >
					                        <html:option value="1">&nbsp;&nbsp;1&nbsp;&nbsp;</html:option>
			                                <html:option value="2">&nbsp;&nbsp;2&nbsp;&nbsp;</html:option>
			                                <html:option value="3">&nbsp;&nbsp;3&nbsp;&nbsp;</html:option>
			                                <html:option value="4">&nbsp;&nbsp;4&nbsp;&nbsp;</html:option>
			                                <html:option value="5">&nbsp;&nbsp;5&nbsp;&nbsp;</html:option>
			                                <html:option value="6">&nbsp;&nbsp;6&nbsp;&nbsp;</html:option>
			                                <html:option value="7">&nbsp;&nbsp;7&nbsp;&nbsp;</html:option>
			                              </html:select>
					                    </td>
					                 </tr>
					                 <tr>
					                    <td>
					                       连接线颜色
					                      <html:text  name="relationMapForm" property="chartParam.linecolor"  alt="clrDlg" size="6" style="BACKGROUND-COLOR:${relationMapForm.chartParam.linecolor}" styleClass="inputs text4" readonly="true"/> 
					                    </td>
					                    <td>
					                    连接线宽 
					                      <html:select property="chartParam.linewidth" name="relationMapForm" styleClass="inputs" styleId="lineType">
					                        <html:option value="1">&nbsp;&nbsp;1&nbsp;&nbsp;</html:option>
			                                <html:option value="2">&nbsp;&nbsp;2&nbsp;&nbsp;</html:option>
			                                <html:option value="3">&nbsp;&nbsp;3&nbsp;&nbsp;</html:option>
			                                <html:option value="4">&nbsp;&nbsp;4&nbsp;&nbsp;</html:option>
			                                <html:option value="5">&nbsp;&nbsp;5&nbsp;&nbsp;</html:option>
			                                <html:option value="6">&nbsp;&nbsp;6&nbsp;&nbsp;</html:option>
			                                <html:option value="7">&nbsp;&nbsp;7&nbsp;&nbsp;</html:option>
			                              </html:select>
					                    </td>
					                 </tr>
					                 <tr>
					                    <td>
					                    同级间距 <html:text   name="relationMapForm" property="chartParam.lr_spacing"  size="6" styleClass="inputs text4" onkeypress="event.returnValue=IsDigit();"/>
					                    </td>
					                    <td>
					                    上级间距 <html:text   name="relationMapForm" property="chartParam.tb_spacing"  size="6" styleClass="inputs text4" onkeypress="event.returnValue=IsDigit();"/>
					                    </td>
					                 </tr>
					                 <tr>
					                    <td>
					                        阴影效果<html:checkbox name="relationMapForm" property="chartParam.isshowshadow" />
					                        
					                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					                         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					                         
					                    </td>
					                    <td>&nbsp;</td>
					                 </tr>
					           </table>
					           </div>
					         </fieldset>
					         
					         <fieldset style="margin-top: 10px;">
					           <legend>字体设置</legend>
					           <div class="selectDiv">
					           <table border=0 width="100%" class="styleTable"> 
					              <tr>
					                   <td width="45%"> 
					                   <bean:message key="general.inform.org.fontcolor"/>   
					                   <html:text  name="relationMapForm" property="chartParam.fontcolor" alt="clrDlg" size="6" style="BACKGROUND-COLOR:${relationMapForm.chartParam.fontcolor}" styleClass="inputs text4" readonly="true"/>
					                   
					                   </td>
					                   <td>
					                     <bean:message key="general.inform.org.fontsize"/> 
					                      <html:select name="relationMapForm" property="chartParam.fontSize" styleClass="inputs">
						                      <html:option value="16">16</html:option>
						                      <html:option value="18">18</html:option>
						                      <html:option value="20">20</html:option>
						                      <html:option value="22">22</html:option>
						                      <html:option value="24">24</html:option>
						                      <html:option value="26">26</html:option>
						                      <html:option value="28">28</html:option>
						                      <html:option value="36">36</html:option>
						                      <html:option value="48">48</html:option>
						                      <html:option value="72">72</html:option>
						                  </html:select>  
					                      
					                   </td>
					                   <td rowspan="2" width="15%">&nbsp;</td>
					                 </tr>
					                 <tr>
					                   <td>
					                      <bean:message key="general.inform.org.fontfamily"/>
					                       <html:select name="relationMapForm" property="chartParam.fontName" styleClass="inputs">
						                      <html:option value="song"><bean:message key="font_family.song"/></html:option>
						                      <html:option value="kaiti"><bean:message key="font_family.kaiti"/></html:option>
						                      <html:option value="xinsong">新宋体</html:option>
						                      <html:option value="fangsong">仿宋</html:option>
						                      <html:option value="heiti">黑体</html:option>
						                      <html:option value="yahei">微软雅黑</html:option>
						                      <html:option value="lishu"><bean:message key="font_family.lishu"/></html:option>
						                      <html:option value="youyuan"><bean:message key="font_family.youyuan"/></html:option>
						                  </html:select>  
					                   </td>
					                   <td>
					                      <bean:message key="general.inform.org.fontstyle"/>
						                  <html:select name="relationMapForm" property="chartParam.fontstyle" styleClass="inputs">
						                      <html:option value="general"><bean:message key="font_style.general"/></html:option>
						                      <html:option value="italic"><bean:message key="font_style.italic"/></html:option>
						                      <html:option value="thick"><bean:message key="font_style.thick"/></html:option>
						                      <html:option value="italicthick"><bean:message key="font_style.italicthick"/></html:option>
						                  </html:select>              
					                   </td>
					                 </tr>
					           </table>
					           </div>
					         </fieldset>
	                  </td>
	               </tr>
	            </table>
	            <br/>
	  </fieldset><br/>
  
  
      <fieldset align="center" style="width:90%;padding-top: 10px;" id="contentSet">
		         <legend >内容设置</legend>
		         <table width="100%"  border=0>
		            <tr>
						<td align="right"  width="23%" valign="top"><bean:message key="general.sprelation.nodedescription"/></td>
						<td align="left">
						  <html:textarea property="chartParam.hint_items_desc" styleId="hint_items_desc" readonly="true"  cols="46" rows="3"></html:textarea>
						  &nbsp;<button type="button" class="mybutton" onclick="selectItem('hint_items');">...</button>
						  <html:hidden styleId="hint_items" property="chartParam.hint_items"/>
						</td>
					</tr>
					<tr style="display:none;">
					    <td align="right">显示照片</td>
					    <td align="left" style="padding:0px;">
					      <html:checkbox name="relationMapForm" property="chartParam.show_pic"/>
					    </td>
					</tr>
		         </table>
		         <br>
		 </fieldset>
		 <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top: 5px;">
           <tr>
             <td align="center"  nowrap>
             	   <button type="button" class="mybutton" onclick="saveParam()"><bean:message key="button.save"/></button>&nbsp;    
                   <button type="button" class="mybutton" onclick="resetTheme()">恢复默认</button>&nbsp;
         		<%--    <button class="mybutton" onclick="window.close()"><bean:message key="button.close"/></button> --%>
         		   <button type="button" class="mybutton" onclick="parent.window.close();"><bean:message key="button.close"/></button>
               </td> 
           </tr>
         </table> 
  </html:form>

      <div id="colorpanel" style="position:absolute;display:none;width:253px;height:177px;z-index:3"><iframe  src=javascript:true; 
frameborder=0 marginheight=0 marginwidth=0 hspace=0 vspace=0 scrolling=no></iframe></div>    
     </div>
   <SCRIPT LANGUAGE=javascript src="/js/color.js"></SCRIPT>
</div>
<script>
   function changevalue(obj){
	   var valueObjId = "isshowshadow_value";
	   if(obj.id == "show_pic")
		   valueObjId = "show_pic_value";
	   
	   if(obj.checked == true)
		   document.getElementById(valueObjId).value="true";
	   else
		   document.getElementById(valueObjId).value="false";
   }
   
   function resetTheme(){
   	var maptheme = getByName("chartParam.theme");
   	var hashvo = new ParameterSet();
   	hashvo.setValue("maptheme",maptheme);
   	hashvo.setValue("isPersonReport",'1');
   	var request=new Request({method:'post',asynchronous:false,onSuccess:setValue,functionId:'0406000003'},hashvo);
   }
   
   var setValue = function(out){
   	
   	setByName("chartParam.isshowshadow",out.getValue("isshowshadow"));
   	setByName("chartParam.bgColor",out.getValue("bgColor"));
   	setByName("chartParam.transitcolor",out.getValue("transitcolor"));
   	setByName("chartParam.border_color",out.getValue("border_color"));
   	setByName("chartParam.border_width",out.getValue("border_width"));
   	setByName("chartParam.linecolor",out.getValue("linecolor"));
   	setByName("chartParam.linewidth",out.getValue("linewidth"));
   	setByName("chartParam.lr_spacing",out.getValue("lr_spacing"));
   	setByName("chartParam.tb_spacing",out.getValue("tb_spacing"));
   	setByName("chartParam.fontName",out.getValue("fontName"));
   	setByName("chartParam.fontSize",out.getValue("fontSize"));
   	setByName("chartParam.fontstyle",out.getValue("fontstyle"));
   	setByName("chartParam.fontcolor",out.getValue("fontcolor"));
   };
   
   function setByName(name,value){
   	var obj = document.getElementsByName(name);
   	if(obj.length==1){
   		if(obj[0].type.indexOf('select')>-1){
   			for(var i=0;i<obj[0].options.length;i++){
   				if(obj[0].options[i].value == value){
   					obj[0].options[i].selected = true;
   					break;
   				}
   					
   			}
   		}else if(obj[0].type == 'text' || obj[0].type == 'hidden'){
   			obj[0].value = value;
   			if(name.toLowerCase().indexOf("color") != -1){
   				obj[0].style.backgroundColor = value;
   			}else if(name.indexOf("isshowshadow") != -1){
   				if(value == "true")
   				    document.getElementById("isshowshadow").checked = true;
   				else
   					document.getElementById("isshowshadow").checked = false;
   			}
   		}
   	}else if(obj.length==2){
   		if(obj[0].value == value)
   			obj[0].checked = true;
   		else
   			obj[1].checked = true;
   	}
   }
   
   function getByName(name){
   	var obj = document.getElementsByName(name);
   	if(obj.length==1)
   		if(obj[0].type.indexOf('select')>-1){
   			return obj[0].options[obj[0].selectedIndex].value;
   		}else if(obj[0].type == 'checkbox'){
   			if(obj[0].checked)
   				return "true";
   			else
   				return "false";
   		}else{
   			return obj[0].value;
   		}
   	else if(obj.length==2){
   		if(obj[0].checked == true)
   			return obj[0].value;
   		else
   			return obj[1].value;
   	}else
   		return "";
   }
   
   function saveParam(){
	   var hashvo = new ParameterSet();
	   hashvo.setValue("direction",getByName("chartParam.direction"));
	   hashvo.setValue("isshowshadow",getByName("chartParam.isshowshadow"));
	   hashvo.setValue("bgColor",getByName("chartParam.bgColor"));
	   hashvo.setValue("transitcolor",getByName("chartParam.transitcolor"));
	   hashvo.setValue("border_color",getByName("chartParam.border_color"));
	   hashvo.setValue("border_width",getByName("chartParam.border_width"));
	   hashvo.setValue("linecolor",getByName("chartParam.linecolor"));
	   hashvo.setValue("linewidth",getByName("chartParam.linewidth"));
	   hashvo.setValue("lr_spacing",getByName("chartParam.lr_spacing"));
	   hashvo.setValue("tb_spacing",getByName("chartParam.tb_spacing"));
	   hashvo.setValue("theme",getByName("chartParam.theme"));
	   hashvo.setValue("fontName",getByName("chartParam.fontName"));
	   hashvo.setValue("fontSize",getByName("chartParam.fontSize"));
	   hashvo.setValue("fontstyle",getByName("chartParam.fontstyle"));
	   hashvo.setValue("fontcolor",getByName("chartParam.fontcolor"));
	   hashvo.setValue("hint_items",getByName("chartParam.hint_items"));
	   hashvo.setValue("show_pic",getByName("chartParam.show_pic"));
	   hashvo.setValue("relationType",'${relationMapForm.relationType}');
	   
	   var saveRe = function(out){
	       parent.window.returnValue="1";
           parent.window.opener.configParam_ok("1");
	       parent.window.close();
	   };
	   var request=new Request({method:'post',asynchronous:false,onSuccess:saveRe,functionId:'302001020704'},hashvo);
   }
</script>