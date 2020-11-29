<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.hrms.frame.dao.utility.DateUtils" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<%
 String severDate=DateUtils.format(new Date(),"yyyy.MM.dd");
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
%>
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
<script language="javascript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
	
	var www="";
	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		var date=outparamters.getValue("data");
		$('amount2').value=date;
		AjaxBind.bind(staticForm.setlist,/*$('setlist')*/setlist);
		if($('setlist').options.length>0)
		{
		  $('setlist').options[0].selected=true;
		  
		  try{
    	   if (navigator.appName.indexOf("Microsoft")!= -1) { 
				$('setlist').fireEvent('onchange');
		         //ie  
		    }else{ 
		        $('setlist').onchange();
		    }  
			}catch(e){
			}
		}
		Element.hide('b_view');
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(staticForm.left_fields,fieldlist);
		Element.hide('b_view');		
	}

				
	/**查询指标*/
	function searchFieldList()
	{
	   var tablename=$F('setlist');
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'0620000002'});
	}
	/**初化数据*/	
	function MusterInitData(infor)
	{  
	   if(infor == '1'){<%--重置操作，要还原隐藏人员库--%>
		   var userbases = document.getElementsByName('userbases')[0];
		   userbases.value = '${staticForm.userbases}'; 
	   }
	   var pars="base="+infor;
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'0620000001'});
	}
	
	function setcondition()
	{
	      if($('select3').value=="1")
	      {
	      $('compute').innerHTML="<bean:message key="kq.formula.average"/>&nbsp;";
	      Element.hide('b_view');
	      }
	      if($('select3').value=="2")
	      {
	      $('compute').innerHTML="<bean:message key="kq.formula.min"/>&nbsp;";
	      Element.hide('b_view');
	      } 
	      if($('select3').value=="3")
	      {
	      $('compute').innerHTML="<bean:message key="kq.formula.max"/>&nbsp;";
	      Element.hide('b_view');
	      } 
	      if($('select3').value=="4")
	      {
	      $('compute').innerHTML="<bean:message key="static.single.sum"/>&nbsp;";
	      $('number').innerHTML="<bean:message key="lable.hiremanage.subamount"/>&nbsp;";
	      Element.hide('b_view');
	      }  	  
	}
	function compute()
	{
	
		var dbpre=$F('userbases');
		<logic:equal name="staticForm" property="infor_Flag" value="1">
		if(dbpre.length==0){
			alert('人员库不能为空');
			return false
		}
		</logic:equal>
	 var tav=$F('find');
	 
	 if(tav==null)
	 tav="";
	 
	 if(www=="1")
	 {
	  if(!validate())
	    return;
	  }
	if($('left_fields').value!="")
	{
	   var flag="";
	   if(staticForm.like.checked==true)
	     flag="Y";
	   else
	     flag="N";
	   if(staticForm.infor_Flag.value=="1")
	      {
	       var bbb="userbases="+dbpre+"&dbpre="+staticForm.dbpre.value + "&setname=" + $('setlist').value + "&select=" + $('select3').value + "&fieldname=" +$('left_fields').value + "&time=" + $('amount2').value + "&flag=" + flag+"&find="+tav;
	      }
	   else
	      {
	       var bbb="userbases=&dbpre=" + "&setname=" + $('setlist').value + "&select=" + $('select3').value + "&fieldname=" +$('left_fields').value + "&time=" + $('amount2').value + "&flag=" + flag+"&find="+tav;
	      }
	  var request=new Request({method:'post',asynchronous:false,parameters:bbb,onSuccess:setvalue,functionId:'0620000003'});
	}
	}
	function setvalue(outparamters)
	{	      	     
	        var datavalue=outparamters.getValue("datavalue");
	        //alert(datavalue);
	        $('amount1').value=datavalue;
	        var count=outparamters.getValue("count");
	        $('amount').value=count;
	        var realdata=outparamters.getValue("realdata");
	        $('amount3').value=realdata;
	      	 if($('select3').value=="2"||$('select3').value=="3")
	      	 {
	      	  Element.show('b_view');
	      	 }         	  
	}
	function setpage()
	{
	 var kkk="fieldname="+$('left_fields').value
	 var request=new Request({method:'post',asynchronous:false,parameters:kkk,onSuccess:showpage,functionId:'0620000004'}); 
	}
	function showpage(outparamters)
	{
	 $('amount1').value="";
	 $('amount').value="";
	 var itemtype=outparamters.getValue("itemtype");
	  Element.hide('b_view');

	 if(itemtype=="D")
	   {
	        www="1";
	         if($('select3').value=="1")
	         {
	           $('compute').innerHTML="平均年限&nbsp;";
	           Element.hide('b_view');
	        }
	        if($('select3').value=="2")
	        {
	           $('compute').innerHTML="最小年限";
	           Element.hide('b_view');
	        } 
	        if($('select3').value=="3")
	        {
	           $('compute').innerHTML="最大年限";
	           Element.hide('b_view');
	        } 
	        if($('select3').value=="4")
	        {
	          $('compute').innerHTML="年限<bean:message key="static.single.sum"/>";
	          $('number').innerHTML="<bean:message key="lable.hiremanage.subamount"/>";
	          Element.hide('b_view');
	        }  	
	        Element.show('time');
	        Element.show('set');
	        Element.show('text');	        
	   }
	 else
	   {
	     www="2";
	      
	      if($('select3').value=="1")
	         {
	           $('compute').innerHTML="<bean:message key="kq.formula.average"/>&nbsp;";
	           Element.hide('b_view');
	        }
	        if($('select3').value=="2")
	        {
	           $('compute').innerHTML="<bean:message key="kq.formula.min"/>&nbsp;";
	           Element.hide('b_view');
	        } 
	        if($('select3').value=="3")
	        {
	           $('compute').innerHTML="<bean:message key="kq.formula.max"/>&nbsp;";
	           Element.hide('b_view');
	        } 
	        if($('select3').value=="4")
	        {
	          $('compute').innerHTML="<bean:message key="static.single.sum"/>&nbsp;";
	          $('number').innerHTML="<bean:message key="lable.hiremanage.subamount"/>&nbsp;";
	          Element.hide('b_view');
	        }  
	      
	        Element.hide('time');
	        Element.hide('set');
	        Element.hide('text');
	      }     
	 
	}
	function showdata(datavalue,fieldname,setname,select,time,realdata)
	{
	 var dbpre="";
	 if(staticForm.infor_Flag.value=="1")
	      {
	        dbpre=staticForm.dbpre.value;
	      }
	 if(staticForm.infor_Flag.value=="2")
	      {
	        dbpre="b";
	      }
	 if(staticForm.infor_Flag.value=="3")
	      {
	        dbpre="k";
	      }
	 var flag="";
	   if(staticForm.like.checked==true)
	     flag="Y";
	   else
              flag="N"; 
	 var find="";
	 var fi = document.getElementById("find");
	 if (fi != null && typeof(fi) != 'undefined') {
	   if(staticForm.find.checked==true)
	     find="1";
	   else
	     find="0";
	 } else {
	 	find="0";
	 } 
	           
	 var url="/general/static/singlestatic/showspecial.do?b_show=link&datavalue=" + datavalue + "&fieldname=" + fieldname + "&dbpre=" + dbpre + "&setname=" + setname + "&select=" + select + "&time=" + time + "&flag=" + flag + "&realdata=" + realdata+"&find="+find+"&userbases="+$F('userbases');

	 window.open(url); 
	}

	function validate()
	{
          var tag=false;
          var dd,cc,bb;
          dd=$('amount2').value;
          cc=dd.replace(".","-");
          bb=cc.replace(".","-");
         
          tag= checkDat(bb);   
	  if(tag==false)
	  {
	    return false;
	  }
	  return tag;
	}
	 function checkDat(str)
	  {
	    var ret=false;
	      var mm="";
	      var dd="";
	      var tem="";
	      var cc=0;
        var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/; 
        var r = str.match(reg); 
          
        if(r==null)
        {
           alert("<bean:message key="kq.rest.rmach"/>");
           return false; 
        }
         var d=new Date(r[1], r[3]-1,r[4]);
            dd=""+d.getDate();
            cc=d.getMonth()+1;
            mm=""+cc;
        if(mm.length==1&&dd.length==2)
        {
          tem=d.getFullYear()+r[2]+("0"+(d.getMonth()+1))+r[2]+d.getDate();
        }
         if(dd.length==1&&mm.length==2)
        {
          tem=d.getFullYear()+r[2]+(d.getMonth()+1)+r[2]+("0"+d.getDate());
        }
        if(dd.length==1&&mm.length==1)
        {
          tem=d.getFullYear()+r[2]+("0"+(d.getMonth()+1))+r[2]+("0"+d.getDate());
        }
        if(dd.length==2&&mm.length==2)
        {
         tem=d.getFullYear()+r[2]+(d.getMonth()+1)+r[2]+d.getDate();
        }
         if(tem==str)
         {
             ret=true;
         }else{
            alert("<bean:message key="kq.rest.rmach"/>");
            return false;
         }
       return ret;
     } 
		function computes()
		{ 
		  alert("ss");
		}
    function changeview()
    {
       Element.hide('b_view');
    }
	function get_common_query(infor,query_type)
    {
   		//alert(infor);
   		//alert(dbpre);
   		//alert(query_type);
   		var dbpre=$F('dbpre');   		
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
      //var dbpre=$F('dbpre');  
      var dbpre=$F('userbases'); 
      <logic:equal name="staticForm" property="infor_Flag" value="1"> 
      if(dbpre.length==0){
			alert('人员库不能为空');
			return false
		}
		</logic:equal>
      var dw=700,dh=450;
      var dl=(screen.width-dw)/2;
      var dt=(screen.height-dh)/2;
      if (isIE6())
          dh = 360;
      var thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type="+$URL.encode(infor)+"&a_code=UN&tablename="+$URL.encode(dbpre);
      //19/3/13 xus 非ie模式下浏览器兼容
      if(window.showModalDialog)
	      var return_vo= window.showModalDialog(thecodeurl, "", 
	              	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:700px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");
      else
	      Ext.create('Ext.window.Window',{
	    	  id:'common_query_new',
	    	  title:'常用查询',
	    	  height:dh,
	    	  width:dw,
	    	  resizeable:'no',
	    	  modal:true,
	    	  autoScroll:false,
	    	  autoShow:true,
	    	  autoDestroy:true,
	    	  html:'<iframe style="background-color:#ffffff;" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>'
	      }).show();
   }
//19/3/27 xus 浏览器兼容 单项统计 关闭子窗口
function closeExtWin(){
	if(Ext.getCmp('common_query_new'))
		Ext.getCmp('common_query_new').close();
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
      	   function   addDict(obj,event,flag)
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
   var un_vos=document.getElementsByName("dbpre")[0];
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
    resultuser+="<tr><td align='center' style='height:35px'><input onclick=\"selectcheckbox();document.getElementById('dict').style.display='none'\" value='确定' type='button' class='mybutton'/>&nbsp;&nbsp;<input onclick=\"document.getElementById('dict').style.display='none'\" class='mybutton' type='button' value='取消' /></td></tr></table>";
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

</script>
<%if("hl".equalsIgnoreCase(bosflag)){ %>
<style>
.ListTableF {
	margin-top:10px;
}
</style>
<%}%>
<html:form action="/general/static/singlestatic/single_static">
<html:hidden property="infor_Flag"/>
<table width="700" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" style="border-collapse: separate;">
	<tr height="20">
 		<td  align="left" style="margin-left: 5px;border-left: none;border-top: none;border-right: none;" class="TableRow"><bean:message key="static.single.dxtj"/></td>
    </tr> 
<tr>
<td align="center">
<table width="75%" border="0" cellspacing="0"  align="center" cellpadding="0">
    <logic:equal name="staticForm" property="infor_Flag" value="1">
	   <tr style="display: none">
	                <td align="left" colspan="2" nowrap>
	                   <bean:message key="label.query.dbpre"/>
	     	           <hrms:optioncollection name="staticForm" property="dblist" collection="list"  />
	                       <html:select name="staticForm" property="dbpre" size="1" onchange="">
	                       <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	                   </html:select>   
	                 </td>
	   </tr>
	    <tr>
       		<td colspan="2" align="left" height="30" style="" valign="bottom" nowrap>
     	    <bean:message key="menu.base"/>
       			<input name=viewuserbases style="height:20px;width:190px;vertical-align: middle;line-height: 20px;margin: 0px;padding: 0px; " value='${staticForm.viewuserbases }' readonly="readonly"  class="inputtext"><img id='imgid' style="cursor:pointer; vertical-align:bottom" src="/images/select.jpg" onmouseover="this.src='/images/selected.jpg'" onmouseout="this.src='/images/select.jpg'" onclick="addDict(this,event,'hidcategories');">
       			<input name=userbases type="hidden" value='${staticForm.userbases }' class="inputtext"/>
      			
       		</td>
       </tr>
       <tr>
       		<td colspan="2" height="5px"></td>
       </tr>  
   </logic:equal>  
   <tr >
      <td width="" height="22" align="left" valign="top" nowrap>
         <select name="setlist" size="1"  style="width:100%" onchange="searchFieldList();">    
           <option value="1111">#</option>
         </select>
      </td>
      <td width="50%" align="left" valign="top" style="padding-left:10px;z-index:1" nowrap> 
      	<bean:message key="kq.formula.fashion"/>
          <select name="select3" onChange="setcondition();">
            <option value="1" selected><bean:message key="kq.formula.average"/>
            <option value="2"><bean:message key="kq.formula.min"/></option>
            <option value="3"><bean:message key="kq.formula.max"/></option>
            <option value="4"><bean:message key="static.single.sum"/></option>
          </select>
        </td>
   </tr>
   <tr>
   		<td height="5px"></td>
   </tr>
   <tr > 
      <td rowspan="2" align="left" valign="center" nowrap>
         <select name="left_fields" onChange="setpage();" multiple="multiple"  style="height:209px;width:100%;font-size:9pt;margin-bottom:-2px;z-index:1;margin-right:-2px;">
         </select>
      </td>
      <td height="78" align="left" valign="top" style="padding-left:10px;" nowrap>
	 <div id="Layer1" style="position:relative; left:0px; top:0px; width:0px; height:0px; z-index:1"> 
          <fieldset align="center" style="width:267">
          <legend><bean:message key="static.single.tjjg"/></legend>
          <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
            <tr> 
              <td align="right" width="20%" nowrap valign="center" id="number"><bean:message key="lable.hiremanage.subamount"/>&nbsp;</td>
              <td align="left"  nowrap valign="center"><input name="amount" type="text" value="" class="inputtext"></td>
            </tr>
            <tr> 
              <td align="right" nowrap valign="center" id="compute"><bean:message key="kq.formula.average"/>&nbsp;</td>
              <td align="left"  nowrap valign="center"><input name="amount1" type="text" value="" class="inputtext"></td>
              <td align="left"  nowrap valign="center"><input name="amount3" type="hidden" value="" class="inputtext"></td>
            </tr>  
          </table>
          </fieldset>
        </div>
        </td>
    </tr>   
    <tr > 
      <td align="left" style="padding-left:10px;" nowrap valign="center">
	 <div id="Layer1" style="position:relative; left:0px; top:0px; width:0px; height:0px; z-index:1"> 
          <fieldset align="center" id="set" style="width:267">
          <legend id="text"><bean:message key="kq.wizard.edate"/></legend>
          <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" id="time">
            <tr> 
              <td align="right" width="20%" nowrap valign="center"><bean:message key="kq.wizard.riqi"/>&nbsp;</td>
              <td align="left"  nowrap valign="center">
               <!-- 
              <input name="amount2" id="time" type="text" value="<%=severDate%>" onfocus="inittime(false);setday(this);" onchange="changeview()">
                -->
                <input type="text" name="amount2" id="amount2" extra="editor"  dropDown="dropDownDate">
              </td>
            </tr>
            <tr> 
              <td align="left" width="20%" nowrap valign="center" > </td>
              <td align="left" nowrap valign="center"><input type="checkbox" name="like" onclick="changeview()">
                <bean:message key="static.single.jjdr"/> </td>
            </tr>    
          </table>
          </fieldset>
        </div>	  
      </td>
    </tr>  
    <tr> 
     <td align="center" height="30" nowrap colspan="2"> 
     <logic:equal name="userView" property="status" value="0">
          <html:multibox name="staticForm" property="find" value="1"/><bean:message key="hmuster.label.search_result"/>
     </logic:equal>
      </td>
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
	      	<input type="button" name="b_query" value="<bean:message key="button.query"/>" class="mybutton" onclick='get_common_query_new("${staticForm.infor_Flag}");'><input type="button" name="b_view" id="abc" value="<bean:message key="label.view"/>" class="mybutton" id="view" onclick="showdata($('amount1').value,$('left_fields').value,$('setlist').value,$('select3').value,$('amount2').value,$('amount3').value);">
	      	<input type="button" name="b_save" value="<bean:message key="button.computer"/>" class="mybutton" onclick="compute();">
		  	<html:reset styleClass="mybutton" property="b_clear" onclick="MusterInitData('${staticForm.infor_Flag}');">
	      		<bean:message key="button.clear"/>
	 	 	</html:reset> 
		  	<hrms:tipwizardbutton flag="emp" target="il_body" formname="staticForm"/> 
   		</td>
 	</tr>
</table>
</html:form>
<script language="javascript">
   //var ViewProperties=new ParameterSet();
   MusterInitData('<bean:write name="staticForm"  property="infor_Flag"/>');
   <logic:equal name="staticForm" property="infor_Flag" value="1">
    if($('dbpre').options.length<2){
   		$('imgid').style.display='none';
   }
   </logic:equal>
   if(!getBrowseVersion() || getBrowseVersion() == 10){//非IE浏览器样式兼容性问题  wangb 20180802 bug 39346 and ie11 非兼容视图下     wangb 20190307
     var fieldset = document.getElementsByTagName('fieldset')[1];
     fieldset.parentNode.style.top='-120px';
   }
   
</script>
<div id="dict" class='div_table'  style="display:none;z-index:+999;position:realtive;height:170px;overflow:hidden;background-color:#FFF"></div>
