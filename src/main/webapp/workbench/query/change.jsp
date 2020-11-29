<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
  function screen(obj,flag)
{
  var targetvalue=document.getElementById(flag); 
  targetvalue.value=obj.value; 
}
function   addDict(obj,event,flag)
{ 
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
	document.getElementById('dict').style.left=aTag.offsetLeft;
   	document.getElementById('dict').style.top=aTag.offsetTop+20;
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


function changedcategories(){
	if(getBrowseVersion()){//IE浏览器返回数据
		top.returnValue=document.getElementById("hidcategories").value;
		top.close();
	}else{//非IE浏览器 返回数据  wangb 20180205
		parent.parent.returnValue(document.getElementById("hidcategories").value);
	}
	
}
//-->
</script>
<style>
<!--
.div_table{
    border-width: 1px;
    BORDER-BOTTOM: #aeac9f 1pt solid; 
    BORDER-LEFT: #aeac9f 1pt solid; 
    BORDER-RIGHT: #aeac9f 1pt solid; 
    BORDER-TOP: #aeac9f 1pt solid ; 
}
.tdFontcolor{
	text-decoration: none;
	Font-family:????;
	font-size:12px;
	height=20px;
	align="center"
}
-->
</style>
<body onclick="hiddendict();">
<html:form action="/workbench/query/gcondlist">  
  <table width="320" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<td align=left class="TableRow">分类名称</td>
          </tr> 
          <tr>
            <td class="framestyle9">
               <br>
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" >    
               <tr style="height:80px">
                <td >
            	<div style="position:absolute;z-index:1;width:300px;top:45px;height:20px;left:25px"> 
                  <html:select name="highQueryForm" property='categories' styleId="hidcategoriesselect" style="position:absolute;width:300px;height:20px;clip:rect(0 300 22 282)"  onchange="screen(this,'hidcategories');" onfocus=''>   
					<option value=""></option>
					<html:optionsCollection property="catelist" value="dataValue" label="dataName" />
            	</html:select>
            	</div>  
    			<div   style="position:absolute;z-index:2;top:45px;width: 282px; height:20px;left:25px">    
            	<input name=categories id='hidcategories' style="position:absolute;width: 283px; height:20px;" value='${highQueryForm.categories }'  onkeyup="addDict(this,event,'hidcategories');" onblur="" class="inputtext">
            	</div>
            	<input type="hidden" name="hidcategories_value" value='${highQueryForm.hidcategories }' />
               </td>
               </tr>
               </table>
               </td>
          </tr>           
          <tr class="list3">
            <td align="center" style="height:35px;">
	       &nbsp;<button type="button" onclick="changedcategories();" class="mybutton" >确定</button>
	       &nbsp;<button type="button" onclick="winClose();" class="mybutton" ><bean:message key="button.close"/></button>
	
            </td>
          </tr>  
  </table>
<script>
//关闭窗口 兼容非IE浏览器   wangb 20180205
function winClose(){
	if(getBrowseVersion()){
		top.close();
	}else{
		parent.parent.closeWin();
	}
}
</script>
</html:form>
<div id="dict" style="display:none;z-index:+999;position:absolute;width:300px;overflow:auto;bgcolor='#FFFFFF';"></div>
</body>