<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.stat.StatForm"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	
	StatForm statForm=(StatForm)session.getAttribute("statForm");
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
	String isnotie = request.getParameter("isnotie");//非ie浏览器打开弹窗  wangb 20180802
	isnotie = isnotie==null? "":isnotie;
%>
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 318px;height: 230px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
 margin-left:98px;
}
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
</STYLE>
 <script language="JavaScript">
 	if((!getBrowseVersion()) && '<%=isnotie %>'=='true' ){//wangz 2019-03-05
 		loadClose();//非ie浏览器先执行submit后刷新页面，在返回数据关闭弹窗  wangb 20180802
 	}
 	function loadClose(){
 		var vo=new Object();  
    	vo.flag="true";   
    	var href=""; 	    
     	<logic:equal name="statForm" property="isoneortwo" value="1">
    	  	href="/general/static/commonstatic/statshow.do?b_chart=link&statid=${statForm.statid}&statenter=true";
    	    //window.opener.location.href="/general/static/commonstatic/statshow.do?b_chart=link&statid=${statForm.statid}";  
    	</logic:equal>
    	<logic:equal name="statForm" property="isoneortwo" value="2">
    	    //window.opener.location.href="/general/static/commonstatic/statshow.do?b_doubledata=link&statid=${statForm.statid}";  
    	    href="/general/static/commonstatic/statshow.do?b_doubledata=link&statid=${statForm.statid}&statenter=true";
    	</logic:equal>
 		vo.href=href;
 		if(parent.parent.Ext && parent.parent.Ext.getCmp('statistical')){//常用统计 设置统计范围 特殊处理  wangb 20190528 48362
				var win = parent.parent.Ext.getCmp('statistical');
				win.return_vo = vo;
				win.close();
		}else if(window.showModalDialog){
				top.returnValue=vo;
				parent.window.close();
		}else{
				parent.opener.statset_callbackfunc(vo);
				parent.window.close();
		}
 	}
 
   	function savecode()
   	{
   		var dbpre=$F('userbases');
      if(dbpre.length==0){
      	alert("请选择人员库");
      	return false;
      }
   	  var currnode,codeitemid;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;   
    	   codeitemid=currnode.uid;
    	   if(root.getSelected()=="root,")
    	     statForm.querycond.value="";
    	   else    	     
    	     statForm.querycond.value=root.getSelected();   	    
    	  
    	   <logic:equal name="userView" property="status" value="0">  
    	     var curr_vos= document.getElementsByName('curr_id');
             var curr_vo=curr_vos[0];  
             var curr_value="";              
             for(var i=0;i<curr_vo.options.length;i++)
             {
                   if(curr_vo.options[i].selected)
                     curr_value=curr_vo.options[i].value ;         
             } 
             if(statForm.result.checked==true)
                statForm.result.value="1";
             else
               statForm.result.value="0";
             if(curr_value!="#")
             {
                statForm.preresult.value="2";
             }
             else
             {
               statForm.preresult.value="1";
             }             
              statForm.result.checked=true;                               
           </logic:equal>
           statForm.action = statForm.action +"?isnotie=true";	  
    	   statForm.submit();
    	  
    	  if(getBrowseVersion()){ 
    	  //alert(root.getSelected());  
    	 // alert(window.opener.location.toString());
    	    var vo=new Object();  
    	    vo.flag="true";   
    	    var href=""; 	    
    	    <logic:equal name="statForm" property="isoneortwo" value="1">
    	      href="/general/static/commonstatic/statshow.do?b_chart=link&statid=${statForm.statid}&statenter=true";
    	      //window.opener.location.href="/general/static/commonstatic/statshow.do?b_chart=link&statid=${statForm.statid}";  
    	   	</logic:equal>
    	   	<logic:equal name="statForm" property="isoneortwo" value="2">
    	      //window.opener.location.href="/general/static/commonstatic/statshow.do?b_doubledata=link&statid=${statForm.statid}";  
    	      href="/general/static/commonstatic/statshow.do?b_doubledata=link&statid=${statForm.statid}&statenter=true";
    	   	</logic:equal>
    	   	vo.href=href;
			//19/3/15 xus 浏览器兼容返回参数  ie9 下  弹窗套弹窗第二个不显示内容，问题处理 
			if(parent.parent.Ext && parent.parent.Ext.getCmp('statistical')){//常用统计 设置统计范围 特殊处理  wangb 20190528 48362
				var win = parent.parent.Ext.getCmp('statistical');
				win.return_vo = vo;
				win.close();
			}else if(window.showModalDialog){
				top.returnValue=vo;
				parent.window.close();
			}else{
				parent.opener.statset_callbackfunc(vo);
				parent.window.close();
			}
		}
   	}
   	//19/3/15 xus 浏览器兼容 关闭窗口方法
   	function windowClose(){
   		if(parent.parent.Ext && parent.parent.Ext.getCmp('statistical')){//常用统计 设置统计范围 特殊处理  wangb 20190528 48362
			parent.parent.Ext.getCmp('statistical').close();
		}else if(window.showModalDialog){
   			parent.window.close();
   		}else{
   			parent.window.close();
   			/* parent.opener.close(); */
   		}
   	}
   	function preresultclick()
   	{
   	 return;
  	    if(statForm.preresult.checked==true)
             {
                statForm.preresult.value="2";
                 Element.show('querylist');
             }
             else
             {
               statForm.preresult.value="1";
               Element.hide('querylist');
             }
   	}
   	function setDefaultStat_Id(obj)
   	{
   	   return;
   	   var statid=obj.value;
   	   var hashvo=new ParameterSet();
           hashvo.setValue("statid",statid);
           var request=new Request({method:'post',asynchronous:false,functionId:'11080204030'},hashvo);
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
      //var dbpre=$F('userbase');  
      var dbpre=$F('userbases').replace(/`/g,',');
      if(dbpre.length==0){
      	alert("请选择人员库");
      	return;
      }
//      var thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type="+infor+"&a_code=UN&tablename="+dbpre;
      var thecodeurl="/general/inform/search/generalsearch.do?b_query=link`type="+infor+"`a_code=UN`tablename="+dbpre+"`mark=tongji";
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
      var dw=780,dh=410,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
      //var return_vo= window.showModalDialog(thecodeurl, "", 
      //        	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no;");
      //兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20180207
	  var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
	  var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
	  window.open(iframe_url,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
   }

function getAbsPosition(obj, offsetObj){
	var _offsetObj=(offsetObj)?offsetObj:document.body;
	var x=obj.offsetLeft;
	var y=obj.offsetTop;
	var tmpObj=obj.offsetParent;

	while ((tmpObj!=_offsetObj) && tmpObj){
		x += tmpObj.offsetLeft - tmpObj.scrollLeft + tmpObj.clientLeft;
		y += tmpObj.offsetTop - tmpObj.scrollTop + tmpObj.clientTop;
		tmpObj=tmpObj.offsetParent;
	}
	return ([x, y]);
} 
   
function getAbsPosition(obj, offsetObj){
	var _offsetObj=(offsetObj)?offsetObj:document.body;
	var x=obj.offsetLeft;
	var y=obj.offsetTop;
	var tmpObj=obj.offsetParent;

	while ((tmpObj!=_offsetObj) && tmpObj){
		x += tmpObj.offsetLeft - tmpObj.scrollLeft + tmpObj.clientLeft;
		y += tmpObj.offsetTop - tmpObj.scrollTop + tmpObj.clientTop;
		tmpObj=tmpObj.offsetParent;
	}
	return ([x, y]);
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
    resultuser="<div class=\"recordrow\" style='border-width:0 0 1 0;height:80px;width:"+($('viewuserbases').offsetWidth+aTag.offsetWidth-22)+"px;overflow:auto;margin:9 9 9 9'><table width='100%' cellpadding='0' border='0' bgcolor='#FFFFFF' cellspacing='0'>"+resultuser+"</table></div>"; 
    resultuser+="<table style='margin:9 9 9 9' width="+($('viewuserbases').offsetWidth+aTag.offsetWidth-22)+"px cellpadding='0' border='0' bgcolor='#FFFFFF' cellspacing='0'><tr id='tv' name='tv'><td id='al"+c+"' style='width:85%;height:10px;cursor:pointer' nowrap class=tdFontcolor><input name=allbox type=checkbox onclick='selectallcheckbox(this)' value='' />全部</td></tr>";
    resultuser+="<tr><td align='center' style='height:35px'><input type='button' name=\"btnsave\" value='确定' onclick=\"selectcheckbox();document.getElementById('dict').style.display='none'\" class='mybutton'><input type='button' name=\"btnreturn\" value='取消' onclick=\"document.getElementById('dict').style.display='none'\" class='mybutton'></td></tr></table>";
    document.getElementById("dict").innerHTML=resultuser;
    document.getElementById('dict').style.display = "block";
    document.getElementById('dict').style.width=$('viewuserbases').offsetWidth+aTag.offsetWidth;
    var pos=getAbsPosition(aTag);
    document.getElementById('dict').style.position="absolute";	
	document.getElementById('dict').style.left=pos[0]-$('viewuserbases').offsetWidth;
    document.getElementById('dict').style.top=pos[1]+aTag.offsetHeight-1;
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
   </SCRIPT>
   <hrms:themes/>
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.RecordRow{
	width:expression(document.body.clientWidth-10);
}
</style>
<%}else{ %>
<style>
.RecordRow{
	margin-top:10px;
	width:expression(document.body.clientWidth-10);
}
</style>
<%} %>
<html:form action="/general/static/commonstatic/statset"> 
    <table border="0" cellspacing="1"  align="center" cellpadding="1" class="RecordRow">
    	<%--
        <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		      <bean:message key="workbench.stat.statsettitle"/></td>            	        	        	        
           </tr>
   	  </thead>
   	  --%>    	  
   	   <tr style="display: none;vertical-align: middle;">
   	       <td align="right" width="35%" valign="middle" height="30"  nowrap ><bean:message key="menu.base"/></td>
            <td align="left" valign="middle" nowrap>
    	         <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="statForm.dbcond" collection="list" scope="page"/>
                 <html:select name="statForm" property="userbase" size="1" style="width:220px;vertical-align: middle;">                     
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                 </html:select>
            </td>
       </tr> 
       <tr style="vertical-align: middle;">
       		<td align="right" width="20%" height="30" style="vertical-align: middle;"  nowrap><bean:message key="menu.base"/></td>
       		<td align="left" height="30px" valign="middle" nowrap>
       			<!-- modified by xiaoyun 2014-8-12 start -->
       			<input name=viewuserbases class="inputtext" style="width: 300px; height:20px;vertical-align: middle;line-height: 20px;margin: 0px;padding: 0px;" align="middle" value='${statForm.viewuserbases }' readonly="readonly"><img id=imgid style="cursor:pointer;vertical-align: middle;" align="middle" src="/images/select.jpg" onmouseover="this.src='/images/selected.jpg'" onmouseout="this.src='/images/select.jpg'" onclick="addDict(this,event,'hidcategories');">
       			<!-- modified by xiaoyun 2014-8-12 end -->
       			<input name=userbases type="hidden" value='${statForm.userbases }' style="vertical-align: middle;"/>
       		</td>
       </tr>
      <logic:equal name="userView" property="status" value="0">
        <tr style="vertical-align: middle;">
          <td align="right" height="30" nowrap>
               <html:hidden name="statForm" property="preresult"/><bean:message key="workbench.stat.commonfindresult"/>          
          </td>
          <td align="left" valign="middle" nowrap>
          
                   <html:select name="statForm" property="curr_id" size="1" style="width:318px;vertical-align: middle;">
                         <html:option value="">请选择...&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                         <html:optionsCollection property="condlist" value="id" label="name"/>
                   </html:select> 
          </td>
       </tr>
     </logic:equal>
       <tr>
           <td align="left" colspan="2"> 
             <div id="tbl_container"  class="div2 complex_border_color" >
                 <div id="treemenu" > 
                  <SCRIPT LANGUAGE=javascript>    
                   <bean:write name="statForm" property="treeCode" filter="false"/>
                  </SCRIPT>
                 </div> 
             </div>         
           </td>
        </tr>   
       <tr>
       	 <td></td>
         <td align="left"  nowrap>
                   <logic:equal name="userView" property="status" value="0">
                   <html:checkbox name="statForm" property="result" value="1"/>&nbsp;<bean:message key="workbench.stat.preresult"/>
                  </logic:equal>
                           <html:hidden name="statForm" property="querycond"/>    
         </td>
       </tr>
       
    </table>
    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
    	<tr>
          <td align="center" style="height:35px;" nowrap colspan="3">
               <logic:equal name="userView" property="status" value="0">
                 <input type="button" name="btncance2" value="<bean:message key="button.query"/>" class="mybutton" onclick="get_common_query_new('1');">
               </logic:equal>
               <input type="button" name="btncance2" value="<bean:message key="button.ok"/>" class="mybutton" onclick="savecode();"> 
               <input type="button" name="btncance2" value="<bean:message key="button.close"/>" class="mybutton" onclick="windowClose();"> 
	      
          </td>
       </tr>
    </table>
    <script language="JavaScript">
       preresultclick();
       //var tmpuserbases="${statForm.viewuserbases }";
       //if(tmpuserbases.length==0){
       		//$("viewuserbases").value=$("userbase").options[0].text;
      // }
       if($('userbase').options.length<2){
   		$('imgid').style.display='none';
   }
    </script>
</html:form>
<div id="dict" class='recordrow'  style="display:none;z-index:+999;position:absolute;height:170px;overflow:hidden;background-color:#FFF"></div>

<script>
if(!getBrowseVersion() || getBrowseVersion()==10){//兼容非IE浏览器 样式问题  wangb 20180207
	var TableRow = document.getElementsByClassName('TableRow')[0]; // table 最右边边框线不显示  
	//TableRow.setAttribute('colspan','2');
	var tbl_container = document.getElementById('tbl_container');
	tbl_container.style.marginLeft='87px';
}
</script>
