<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
%>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
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
	Font-family:;
	font-size:12px;
	height:12px;
	align:"center"
}
-->
</style>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
	<script language="JavaScript">
	function selectOne(obj)	
	{
	    var one,two;
	    two=obj.options[obj.selectedIndex].value;
	    one=$F('selTwo');
	    if(one==two&&one!="#")
	      {
	          alert("<bean:message key="planar.mess.yportrait"/>");
	          obj.selectedIndex=0;
	      }
	}
	function selectTwo(obj)	
	{
	    var one,two;
	    two=obj.options[obj.selectedIndex].value;
	    one=$F('selOne');
	    if(one==two&&one!="#")
	      {
	          alert("<bean:message key="planar.mess.yacross"/>");
	          obj.selectedIndex=0;
	      }
	}
	function submitcheck()	
	{
	      var one,two;
	      one=$F('selOne');
	      two=$F('selTwo');
	      if(one=="#"){
	      	alert("横向统计条件不能为空");
	      	return false;
	      }
	      if(two=="#"){
	      	alert("纵向统计条件不能为空");
	      	return false;
	      }
	      if(one==two)
	      {
	          alert("<bean:message key="planar.mess.nosameness"/>");
	          return false;
	      }
	}
	
	function submits(str,stre)
	{
	  var tem,tems,vis,vos;
          var m=0;
          vos= document.getElementsByName(str);
          vis= document.getElementsByName(stre);
          tem=vos[0];
          tems=vis[0];
          var temp=new Array();
          for(i=0;i<tem.options.length;i++)
          {
        	if(tem.options[i].selected==true)
        	{
         	 var no = new Option();
         	 temp[m]=new Array(tem.options[i].text)
         	 m++;
         	}
   	     }
   	     for(i=0;i<tems.options.length;i++)
         { 
   	        if(tems.options[i].selected==true)
         	{
            	var no = new Option();
         	    temp[m]=new Array(tems.options[i].text);
         	    m++;
         	}
         }       
   	    staticFieldForm.mess.value=temp;
	}
	
	function showSetList(outparamters)
	{
		 var slist=outparamters.getValue("logiclist");
		 AjaxBind.bind(staticFieldForm.selOne,slist);
	
		 var slistd=outparamters.getValue("logiclist");
		 AjaxBind.bind(staticFieldForm.selTwo,slistd);
	     var mess=staticFieldForm.mess.value;	
	     if(mess!=""&&mess.length>0)
	     {
	         var arymess=mess.split(",");
	         if(arymess.length==2)
	         {
	             var vos= document.getElementsByName("selOne");
                 var vis= document.getElementsByName("selTwo");
                 var tem=vos[0];
                 var tems=vis[0];
                 var temp=new Array();
                 for(var i=0;i<tem.options.length;i++)
                 {
                      if(tem.options[i].text==arymess[0])
        	          {
         	              tem.options[i].selected=true;
         	          }
         	          if(tems.options[i].text==arymess[1])
         	          {
          	              tems.options[i].selected=true;
         	          }
   	             }
	        }
	    }
	}
	
 function TwoDimInitData(infor)
	{
	     var pars="base="+infor;
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'05301010013'});
	}
	function get_common_query(infor,query_type)
    {
   		//alert(infor);
   		//alert(dbpre);
   		//alert(query_type);
   		var dbpre=$F('userbase');   		
        var dbpre_arr=new Array();
        dbpre_arr[0]=dbpre;
		var objlist=common_query(infor,dbpre_arr,query_type);
		if(objlist && objlist.length >0){
			var hashvo=new ParameterSet();
		    hashvo.setValue("info",infor);
		    hashvo.setValue("dbpre",dbpre); 
		    hashvo.setValue("objlist",objlist);		    
		   	var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,functionId:'0550000007'},hashvo);		
		}else{
			//alert("无相关数据");
		}
   } 
   function get_common_query_new(infor)
   {
   var dbpre="";
   <logic:equal name="staticFieldForm" property="infor_Flag" value="1"> 
      dbpre=$F('userbases');  
      if(dbpre.length==0){
      	alert('人员库不能为空');
      	return false;
      }
      </logic:equal>
      var dw=750,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
       /**
        add by sunming 2015-06-30 ie6下显示滚动条
        */
       if (isIE6()) {
          dh=380;
       }
      var thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type="+infor+"&a_code=UN&tablename="+$URL.encode(dbpre);;
      if(getBrowseVersion()){  
	      var return_vo= window.showModalDialog(thecodeurl, "", 
	              	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");
      }else{
    		var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
    		var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
    		window.open(thecodeurl,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
      }
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
    resultuser+="<tr><td align='center' style='height:35px'><input onclick=\"selectcheckbox();document.getElementById('dict').style.display='none'\" value='确定' type='button' class='mybutton'/>&nbsp;&nbsp;<input onclick=\"document.getElementById('dict').style.display='none'\" value='取消' type='button' class='mybutton'/></td></tr></table>";
    document.getElementById("dict").innerHTML=resultuser;
    document.getElementById('dict').style.display = "block";
    document.getElementById('dict').style.width=$('viewuserbases').offsetWidth+aTag.offsetWidth;
    var pos=getAbsPosition(aTag);
    document.getElementById('dict').style.position="absolute";	
	document.getElementById('dict').style.left=pos[0]-$('viewuserbases').offsetWidth;
    document.getElementById('dict').style.top=pos[1]+aTag.offsetHeight;
    if(navigator.appName.indexOf("Microsoft")!= -1){
	    var objdiv=document.getElementById("dict");
	    var w = objdiv.offsetWidth;
		var h = objdiv.offsetHeight;
		var ifrm = document.createElement('iframe');
		ifrm.src = 'javascript:false';
		ifrm.style.cssText = 'position:absolute; visibility:inherit; top:0px; left:0px; width:' + w + 'px; height:' + h + 'px; z-index:-1; filter: \'progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)\'';
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
function querynext(){
<logic:equal name="staticFieldForm" property="infor_Flag" value="1"> 
	if($F('userbases').length==0){
		alert('人员库不能为空');
		return false;
	}else {
		submits('selOne','selTwo');
		return true;
	}
</logic:equal>
<logic:notEqual name="staticFieldForm" property="infor_Flag" value="1"> 
	submits('selOne','selTwo');
	return true;
</logic:notEqual>
} 
//重置操作，没有对人员库操作
function stat_reset(){
	var viewuserbases =document.getElementsByName('viewuserbases')[0];
	viewuserbases.value='${staticFieldForm.init_viewuserbases}';
	var userbases =document.getElementsByName('userbases')[0];
	userbases.value='${staticFieldForm.init_userbases}';
	var selOne = document.getElementsByName('selOne')[0];
	selOne.value='#';
	var selTwo = document.getElementsByName('selTwo')[0];
	selTwo.value='#';
}

</script>
<hrms:themes />
<%if("hl".equalsIgnoreCase(bosflag)){ %>
<style>
.ListTableF {
	margin-top:10px;
}
</style>
<%}%>
<html:form action="/general/static/two_dim_static" onsubmit="return submitcheck();">
<table width="700" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
	<tr height="20">
 		<td  align="left" style="margin-left: 5px" class="TableRow"><bean:message key="planar.two.title"/></td>
    </tr> 
  <tr>
   <td align="center">
      <table width="90%" border="0" cellspacing="1"  align="center" cellpadding="1">
         	<logic:equal name="staticFieldForm" property="infor_Flag" value="1">  
	           <tr style="display: none">
				      <td  align="right" nowrap class="tdFontcolor"><bean:message key="static.stor"/></td>
				      <td align="left" nowrap class="tdFontcolor">       
				      	<html:select name="staticFieldForm" property="userbase" size="1" >
	                  <html:optionsCollection property="alist" value="dataValue" label="dataName"/> 
	                </html:select> 
	             </td>
	            </tr>
	            <tr>
		       		<td align="right" width="25%" height="30" nowrap>
		     	    	<bean:message key="menu.base"/>
		     	    </td>
		     	    <td align="left" nowrap>
		       			<input name=viewuserbases style="width: 250px;height:21px;vertical-align: middle;" value='${staticFieldForm.viewuserbases }' readonly="readonly"><img id=imgid style="cursor:pointer; vertical-align: middle;" src="/images/select.jpg" onmouseover="this.src='/images/selected.jpg'" onmouseout="this.src='/images/select.jpg'" onclick="addDict(this,event,'hidcategories');">
       					<input name=userbases type="hidden" value='${staticFieldForm.userbases }' />
		       		</td>
		       </tr>
            </logic:equal>  
        <tr > 
          <td align="right" nowrap valign="center"><bean:message key="planar.stat.across"/></td>
           <td align="left"  nowrap valign="center">
            <html:select name="staticFieldForm" property="selOne" size="1" onchange="selectOne(this);" style="width: 268px;">
              </html:select> 
			      </td>
          </tr>
         <tr> 
          <td align="right" nowrap valign="center"><bean:message key="planar.stat.portrait"/></td>
           <td align="left"  nowrap valign="center" >
			       <html:select name="staticFieldForm" property="selTwo" size="1" onchange="selectTwo(this);"  style="width: 268px;">
              </html:select> 
			     </td>
         <tr> 
          <td align="center" height="30" nowrap colspan="2">
            <!-- <logic:equal name="userView" property="status" value="0"> -->
            <logic:equal value="1" name="staticFieldForm" property="result">
            	<input type=checkbox checked="checked" onclick=changevalue(0) />&nbsp;<bean:message key="planar.stat.result"/>&nbsp;&nbsp;&nbsp;&nbsp;
            </logic:equal>
            <logic:notEqual value="1" name="staticFieldForm" property="result">
            	<input type=checkbox onclick=changevalue(1) />&nbsp;<bean:message key="planar.stat.result"/>&nbsp;&nbsp;&nbsp;&nbsp;
            </logic:notEqual>
            <html:hidden name="staticFieldForm" property="result" styleId="resultid" />
            <!--  </logic:equal>	 -->
            <html:hidden name="staticFieldForm" property="mess"/>
         </tr>
     </table>
   </td>
  </tr>
</table>
<table width="700" border="0" cellspacing="0"  align="center" cellpadding="0">
	<tr>
		<td height="5px"></td>
	</tr>
	<tr>
	   	<td align="center">
	     	<logic:equal name="userView" property="status" value="0">
	     	<input type="button" name="b_query" value="<bean:message key="button.query"/>" class="mybutton" onclick='get_common_query_new("${staticFieldForm.infor_Flag}");'>
	     	</logic:equal>
	     	    <hrms:priv func_id="2602302">
			     	<hrms:submit styleClass="mybutton" property="b_save" onclick="submits('selOne','selTwo');">
			           	<bean:message key="button.save"/>
			        </hrms:submit>
		        </hrms:priv>
		      	<html:button styleClass="mybutton" property="reset" onclick="stat_reset();">
		        	<bean:message key="button.clear"/>
		       	</html:button>
	         	<hrms:submit styleClass="mybutton" property="b_next" onclick="return querynext();">
	            	<bean:message key="static.next"/>
		        </hrms:submit> 
	      <hrms:tipwizardbutton flag="emp" target="il_body" formname="staticFieldForm"/> 
	   </td>
  	</tr>
</table>
</html:form>
<script language="javascript">
   TwoDimInitData('<bean:write name="staticFieldForm"  property="infor_Flag"/>');
   <logic:equal name="staticFieldForm" property="infor_Flag" value="1"> 
   if($('userbase').options.length<2){
   		$('imgid').style.display='none';
   }
   </logic:equal>
   
   function changevalue(v){
   	document.getElementById('resultid').value=v;
   }
   function selectReturn(){
	   window.close();
   }

   setTimeout(function(){
   	var buttons= document.getElementsByTagName('input');
   	for( var i = 0 ; i < buttons.length ; i++){
   		var type = buttons[i].getAttribute('type');
   		if(type == 'submit' ){
   			buttons[i].focus();
   			buttons[i].blur();
   		}
   	}
   },100);
   
</script>
<div id="dict" class='div_table'  style="display:none;z-index:+999;position:absolute;height:170px;overflow:hidden;background-color:#FFF"></div>
