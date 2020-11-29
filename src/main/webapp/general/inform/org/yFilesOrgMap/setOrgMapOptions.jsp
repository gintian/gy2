<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<html>
<head>

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
</style>
<hrms:themes />
     <div  >
     <html:form action="/general/inform/org/map/setyFilesOrgMap" >
        <fieldset align="center" style="width:90%;">
           <legend>视图设置</legend>
            <table width="100%" class="viewTable" border=0>
               
               <tr>
                  <td width="100%" colspan=3>
                    <table border=0 width="100%" class="styleTable">
                      <tr>
                         <td width="40%">
		                      <html:radio name="orgMapForm" property="graphaspect" value="true" onclick="changeaspect()"><bean:message key="general.inform.org.graphvaspect"/></html:radio> 
							  <html:radio name="orgMapForm" property="graphaspect" value="false" onclick="changeaspect()"><bean:message key="general.inform.org.graphhaspect"/></html:radio> 
		                  </td>
		                  <td >
		                      配色方案
	                        <html:select property="maptheme" name="orgMapForm"   onchange="resetTheme()" styleClass="inputs">
	                           <html:option value="0">&nbsp;&nbsp;蓝天白云&nbsp;&nbsp;</html:option>
                               <html:option value="1">&nbsp;&nbsp;绿意盎然&nbsp;&nbsp;</html:option>
                               <html:option value="2">&nbsp;&nbsp;红色岁月&nbsp;&nbsp;</html:option>
                               <html:option value="3">&nbsp;&nbsp;金碧辉煌&nbsp;&nbsp;</html:option>
                               <html:option value="4">&nbsp;&nbsp;灰度空间&nbsp;&nbsp;</html:option>
                             </html:select>
		                  </td>
		                  <td width="20%">&nbsp;</td>
                      </tr>
                    </table>
                  
                    <fieldset >
                      <legend>单元格属性</legend>
                      <table border=0 width="100%" class="styleTable">
			                 <tr>
			                    <td width="40%">
			                                              主颜色
			                       <html:text  name="orgMapForm" property="cellcolor"  alt="clrDlg"  style="BACKGROUND-COLOR:${orgMapForm.cellcolor}" styleClass="inputs text4" readonly="true" />
			                    </td>
			                    <td >
					               过度色
			                       <html:text  name="orgMapForm" property="transitcolor"  alt="clrDlg" size="6" style="BACKGROUND-COLOR:${orgMapForm.transitcolor}" styleClass="inputs text4" readonly="true"/>
					            </td>
					            <td rowspan="6" width="20%">&nbsp;</td>
			                 </tr>
			                 
			                 <tr>
			                   <td >
			                      边框颜色
			                      <html:text  name="orgMapForm" property="bordercolor"   alt="clrDlg" size="6" style="BACKGROUND-COLOR:${orgMapForm.bordercolor}" styleClass="inputs text4" readonly="true"/>
			                   </td>
			                   <td >
			                                              边框宽度
			                      <html:select property="borderwidth" name="orgMapForm" styleClass="inputs" >
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
			                      <html:text  name="orgMapForm" property="linecolor"  alt="clrDlg" size="6" style="BACKGROUND-COLOR:${orgMapForm.linecolor}" styleClass="inputs text4" readonly="true"/> 
			                    </td>
			                    <td>
			                    连接线宽 
			                      <html:select property="linewidth" name="orgMapForm" styleClass="inputs" styleId="lineType">
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
			                    同级间距 <html:text   name="orgMapForm" property="cellhspacewidth"  size="6" styleClass="inputs text4" onkeypress="event.returnValue=IsDigit();"/>
			                    </td>
			                    <td>
			                    上级间距 <html:text   name="orgMapForm" property="cellvspacewidth"  size="6" styleClass="inputs text4" onkeypress="event.returnValue=IsDigit();"/>
			                    </td>
			                 </tr>
			                 <tr>
			                    <td>
			                        阴影效果<html:checkbox name="orgMapForm" property="isshowshadow" value="true" ></html:checkbox>
			                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			                    </td>
			                    <td>&nbsp;</td>
			                 </tr>
			           </table>
			         </fieldset>
			         <fieldset style="margin-top: 20px;">
			           <legend>字体设置</legend>
			           <table border=0 width="100%" class="styleTable"> 
			              <tr>
			                   <td width="40%"> 
			                   <bean:message key="general.inform.org.fontcolor"/>   
			                   <html:text  name="orgMapForm" property="fontcolor" alt="clrDlg" size="6" style="BACKGROUND-COLOR:${orgMapForm.fontcolor}" styleClass="inputs text4" readonly="true"/>
			                   
			                   </td>
			                   <td>
			                     <bean:message key="general.inform.org.fontsize"/> 
			                      <html:select name="orgMapForm" property="fontsize" styleClass="inputs">
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
			                   <td rowspan="5" width="20%">&nbsp;</td>
			                 </tr>
			                 <tr>
			                   <td>
			                      <bean:message key="general.inform.org.fontfamily"/>
			                       <html:select name="orgMapForm" property="fontfamily" styleClass="inputs">
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
				                  <html:select name="orgMapForm" property="fontstyle" styleClass="inputs">
				                      <html:option value="general"><bean:message key="font_style.general"/></html:option>
				                      <html:option value="italic"><bean:message key="font_style.italic"/></html:option>
				                      <html:option value="thick"><bean:message key="font_style.thick"/></html:option>
				                      <html:option value="italicthick"><bean:message key="font_style.italicthick"/></html:option>
				                  </html:select>              
			                   </td>
			                 </tr>
			           </table>
			         </fieldset>
                  </td>
               </tr>
            </table>
           <br>
        </fieldset><br/>

 <fieldset align="center" style="width:90%;padding-top: 20px;" id="contentSet">
         <legend >内容设置</legend>
         <table width="100%" class="viewTable" border=0>
          <logic:equal name="orgMapForm" property="report_relations" value="yes">
             <tr>
                <td>
                    <fieldset>
				         <legend >其他</legend>
				         <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
				           <tr>
				              <td align="left"  nowrap>
				                  <html:checkbox name="orgMapForm" property="isshowposup" value="true" >显示单位和部门 </html:checkbox>
				               </td>
				           </tr>
				         </table>
					   </fieldset> 
                </td>
             </tr>
          </logic:equal>
          <logic:notEqual name="orgMapForm" property="report_relations" value="yes">
           <logic:notEqual name="orgMapForm" property="ishistory" value="true">
            <tr>
               <td td width="60%" >
                   <fieldset>
				         <legend ><bean:message key="general.inform.org.person"/></legend>
				         <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
				          <tr>
				              <td align="left"  nowrap colspan=3>
				                   <bean:message key="label.query.dbpre"/>
				                     <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
				                     sql="orgMapForm.dbcond" collection="list" scope="page"/>
				                   <html:select name="orgMapForm" property="dbnames" size="1" style="width:150px;" styleClass="inputs">                   
				                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				                   </html:select>&nbsp;
                   
				               </td>
				               
				           </tr>   
				           <tr>
				              <td align="left"  nowrap>
				                  <html:checkbox name="orgMapForm" property="isshowpersonconut" value="true" ><bean:message key="general.inform.org.isshowpersonconut"/></html:checkbox>
				                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(垂直布局时此选项不可用)  
				               </td>
				               </tr>
				               <tr>
				               <td align="left"  nowrap>
				                  <html:checkbox name="orgMapForm" property="isshowpersonname" value="true" >显示人员</html:checkbox>     
				               </td>
				               </tr>
				               <tr>
				               <td align="left"  nowrap>
				                  <html:checkbox name="orgMapForm" property="isshowphoto" value="true" >显示照片</html:checkbox>     
				               </td>
				               <tr>
				               <td align="left"  nowrap>
				                  <html:checkbox name="orgMapForm" property="isshowpartjobperson" value="true" >显示兼职人员</html:checkbox>     
				                  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				                  	兼职人员颜色
			                      <html:text  name="orgMapForm" property="partjobpersoncolor"  alt="clrDlg" size="6" style="BACKGROUND-COLOR:${orgMapForm.partjobpersoncolor}" styleClass="inputs text4" readonly="true"/> 
			                    
				               </td>
				               </tr>
				           </tr>
				         </table>
					</fieldset> 
					
					<fieldset style="margin-top: 20px;">
				         <legend >机构</legend>
				         <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
				          <tr>
				               <td align="left" nowrap="nowrap">
				                  <html:checkbox name="orgMapForm" property="isshoworgconut" value="true" >显示下一级机构个数</html:checkbox> 
				                  (垂直布局时此选项不可用)  
				               </td>
				            </tr>
				           <!--   <tr>
				           <td>
				              <html:checkbox name="orgMapForm" property="isshowdeptname" value="true" >显示部门</html:checkbox>
				           </td>
				         </tr>
				         -->
				          <tr>
				           <td align="left"  nowrap="nowrap" >
				           
				               	<html:checkbox name="orgMapForm" property="isshowposname" value="true" >显示岗位  </html:checkbox>
				               	  
				               </td>   
				          </tr>
				          
					  </table>
					</fieldset>
	               </td>
	            </tr>
	            </logic:notEqual>
            </logic:notEqual>
            <tr>
				            <td>&nbsp;</td>
			</tr>
         </table>
 </fieldset>
     <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top: 5px;">
           <tr>
             <td align="center"  nowrap>
                   <button type="button" class="mybutton" onclick="preview()">预览</button>&nbsp;
                   <button type="button" class="mybutton" onclick="resetTheme()">恢复默认</button>&nbsp;
	               <button type="button" class="mybutton" onclick="save()">保存</button>&nbsp;      		
         		   <button type="button" class="mybutton" onclick="reback()">返回</button>
               </td> 
           </tr>
         </table>            
     </html:form>
     
      <div id="colorpanel" style="position:absolute;display:none;width:253px;height:177px;z-index:3"><iframe  src=javascript:true; 
frameborder=0 marginheight=0 marginwidth=0 hspace=0 vspace=0 scrolling=no></iframe></div>    
     </div>
   <SCRIPT LANGUAGE=javascript src="/js/color.js"></SCRIPT> 
     <logic:equal value="true" name="orgMapForm" property="ishistory">
        <script> document.getElementById("contentSet").style.display='none'; </script>
     </logic:equal>
</html>
<script type="text/javascript">


  var option = {};
    function save(){
    	if('${orgMapForm.report_relations}' == 'yes'){
    	    orgMapForm.action="/pos/posreport/pos_relation_parameter.do?b_save=link";
    	    var isshowposup = getByName("isshowposup");
    	    if('${orgMapForm.isshowposup}' != isshowposup){
    	    	parent.frames['mil_menu'].reloadflag = true;
    	    }
    	}
    	else
    		orgMapForm.action="/general/inform/org/map/setyFilesOrgMap.do?b_save=link";	
	    orgMapForm.submit();
    }
    
    function IsDigit() 
    { 
      return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
    } 
    
    function reback(){
    	if('${orgMapForm.report_relations}' == 'yes')
    	    orgMapForm.action="/pos/posreport/show_relations_map.do";
    	else
    	    orgMapForm.action="/general/inform/org/map/showyFilesOrgMap.do";
        orgMapForm.submit();
    }
    
    
    function preview(){
    	
    	option.graphaspect = getByName("graphaspect");
    	option.isshowshadow = getByName("isshowshadow");
    	option.cellcolor = getByName("cellcolor");
    	option.transitcolor = getByName("transitcolor");
    	option.bordercolor = getByName("bordercolor");
    	option.borderwidth = getByName("borderwidth");
    	option.linecolor = getByName("linecolor");
    	option.linewidth = getByName("linewidth");
    	option.cellhspacewidth = getByName("cellhspacewidth");
    	option.cellvspacewidth = getByName("cellvspacewidth");
    	
    	option.fontfamily = getByName("fontfamily");
    	option.fontstyle = getByName("fontstyle");
    	option.fontsize = getByName("fontsize");
    	option.fontcolor = getByName("fontcolor");
    	
    	option.isshowpersonconut = getByName("isshowpersonconut");
    	option.isshowpersonname = getByName("isshowpersonname");
    	option.isshowphoto = getByName("isshowphoto");
    	option.isshoworgconut = getByName("isshoworgconut");
    	option.isshowposname = getByName("isshowposname");
    	
    	if(getBrowseVersion()){//IE浏览器 下弹窗打卡
    		var iframe_url="/general/query/common/iframe_query.jsp?src=/general/inform/org/yFilesOrgMap/preview.jsp"; 
    		showModalDialog(iframe_url,option,'dialogHeight:400px;dialogWidth:450px;center:yes;help:no;resizable:no;status:no;');
    	}else{
    		//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20180226 bug 348127
			var iTop = (window.screen.availHeight - 30 - 400) / 2;  //获得窗口的垂直位置
			var iLeft = (window.screen.availWidth - 10 - 450) / 2; //获得窗口的水平位置
			var iframe_url="/general/query/common/iframe_query.jsp?src=/general/inform/org/yFilesOrgMap/preview.jsp"; 
			window.open(iframe_url,"","width=450px,height=400px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
    	}
    }
    
    function resetTheme(){
    	var maptheme = getByName("maptheme");
    	var param = "maptheme="+maptheme;
    	var request=new Request({method:'post',asynchronous:false,parameters:param,onSuccess:setValue,functionId:'0406000003'});

    }
    
    function changecolor(obj){alert(1);
    	//obj.style.backgroundColor = obj.value;
    }
    var setValue = function(out){
    	
    	setByName("isshowshadow",out.getValue("isshowshadow"));
    	orgMapForm.cellcolor.value = out.getValue("cellcolor");
    	orgMapForm.cellcolor.style.backgroundColor = out.getValue("cellcolor");
    	orgMapForm.transitcolor.value = out.getValue("transitcolor");
    	orgMapForm.transitcolor.style.backgroundColor = out.getValue("transitcolor");
    	orgMapForm.bordercolor.value = out.getValue("bordercolor");
    	orgMapForm.bordercolor.style.backgroundColor = out.getValue("bordercolor");
    	setByName("borderwidth",out.getValue("borderwidth"));
    	orgMapForm.linecolor.value = out.getValue("linecolor");
    	orgMapForm.linecolor.style.backgroundColor = out.getValue("linecolor");
    	setByName("linewidth",out.getValue("linewidth"));
    	orgMapForm.cellhspacewidth.value = out.getValue("cellhspacewidth");
    	orgMapForm.cellvspacewidth.value = out.getValue("cellvspacewidth");
    	setByName("fontfamily",out.getValue("fontfamily"));
    	setByName("fontstyle",out.getValue("fontstyle"));
    	setByName("fontsize",out.getValue("fontsize"));
    	orgMapForm.fontcolor.value = out.getValue("fontcolor");
    	orgMapForm.fontcolor.style.backgroundColor = out.getValue("fontcolor");
    	//V77组织机构：岗位管理/岗位设置/汇报关系，点击“设置”视图设置下的全部设置都变为默认设置
    	setByName("isshowposup",'false');
    };
    
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
    
    function setByName(name,value){
    	var obj = document.getElementsByName(name);
    	if(obj.length==1)
    		if(obj[0].type.indexOf('select')>-1){
    			for(var i=0;i<obj[0].options.length;i++){
    				if(obj[0].options[i].value == value){
    					obj[0].options[i].selected = true;
    					break;
    				}
    					
    			}
    		}else if(obj[0].type == 'checkbox'){
    			if(value == "true")
    				obj[0].checked = true;
    			else
    				obj[0].checked = false;
    		}else{
    			obj.value = value;
    		}
    	else if(obj.length==2){
    		if(obj[0].value == value)
    			obj[0].checked = true;
    		else
    			obj[1].checked = true;
    	}else{
    		
    	}
    }
    
    function changeaspect(){
    	
    	if('${orgMapForm.report_relations}' == 'yes' || '${orgMapForm.ishistory}' == 'true')
    		return;
    	
    	
    	var  graphaspect = getByName("graphaspect");
    	   if(graphaspect=='true'){
    		   document.getElementsByName('isshoworgconut')[0].checked = false;
    		   document.getElementsByName('isshoworgconut')[0].disabled = true;
    		   document.getElementsByName('isshowpersonconut')[0].checked = false;
    		   document.getElementsByName('isshowpersonconut')[0].disabled = true;
    	   }else{
    		   document.getElementsByName('isshoworgconut')[0].disabled = false;
    		   document.getElementsByName('isshowpersonconut')[0].disabled = false;
    	   }
    }
    
   changeaspect();

   function showCurColor() {
	   orgMapForm.cellcolor.style.backgroundColor = '${orgMapForm.cellcolor}';
	   orgMapForm.transitcolor.style.backgroundColor = '${orgMapForm.transitcolor}';
	   orgMapForm.bordercolor.style.backgroundColor = '${orgMapForm.bordercolor}';
	   orgMapForm.linecolor.style.backgroundColor = '${orgMapForm.linecolor}';
	   orgMapForm.fontcolor.style.backgroundColor = '${orgMapForm.fontcolor}';
   }
   showCurColor();
</script>
