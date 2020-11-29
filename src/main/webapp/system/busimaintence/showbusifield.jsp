<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hjsj.hrms.actionform.sys.busimaintence.BusiMaintenceForm"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<%
         BusiMaintenceForm busiMaintenceForm = (BusiMaintenceForm)session.getAttribute("busiMaintenceForm");
         String usertype=busiMaintenceForm.getUserType();
 %>
 <hrms:themes></hrms:themes>
<script type="text/javascript">
<!--
//  fi[0]=fieldsetid,fi[1]=itemid,f[2]=ownflag,f[3]=dev_flag,f[4]=keyflag,f[5]=displayid,f[6]=useflag,fi[7]=state
function additem(){
	var fieldsetid=document.getElementById("fid").value;
	var displayid=busiMaintenceForm.displayid.value;
	 var theurl = "/system/busimaintence/showbusifield.do?b_add=link&query=1&fieldsetid="+fieldsetid+"&displayid="+displayid+"&isclose=0";
	 busiMaintenceForm.action=theurl;
	 busiMaintenceForm.submit();
  //var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
  //var return_vo= window.showModalDialog(iframe_url, arguments, 
   //     "dialogWidth:600px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:yes");
	//busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_add=link&query=1&fieldsetid="+fieldsetid+"&displayid="+displayid;
	//busiMaintenceForm.submit();
	/*if(return_vo)
	{
	    var obj = new Object();
	    obj.type=return_vo.type;
	    if(obj.type=="1")
	    {
	      busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_query=links&param=child&fieldsetid="+fieldsetid;
	      busiMaintenceForm.submit();
	    }
	}*/
}
function insertitem(){
	var fielditem=busiMaintenceForm.ss.value;
	if(fielditem.length<1)
	{
	alert(KJG_YWZD_INFO17);
	return;
	}
	var fi=fielditem.split("/");
	var theurl = "/system/busimaintence/showbusifield.do?b_add=link&query=link&faffa=222&fieldsetid="+fi[0]+"&itemid="+fi[1]+"&displayid="+fi[5];
	 busiMaintenceForm.action=theurl;
	 busiMaintenceForm.submit();
	
	/*var theurl = "/system/busimaintence/showbusifield.do?b_add=link`query=1`fieldsetid="+fi[0]+"`itemid="+fi[1]+"`displayid="+fi[5];
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
    var return_vo= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:600px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:yes");
	if(return_vo)
	{
	    var obj = new Object();
	    obj.type=return_vo.type;
	    if(obj.type=="1")
	    {	
	    	var fieldsetid=document.getElementById("fid").value;
	        //var fieldsetid=busiMaintenceForm.filedid.value;
	        busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_query=link&param=child&fieldsetid="+fieldsetid;
	        busiMaintenceForm.submit();
	    }
	}*/
	//busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_add=link&query=1&fieldsetid="+fi[0]+"&itemid="+fi[1]+"&displayid="+fi[5];
	//busiMaintenceForm.submit();
}
function delitem(){
	//var fielditem=busiMaintenceForm.ss.value;
	//if(fielditem.length<1)
	//{
	//alert("请选择删除指标！");
	//return;
	//}
	
	//var fi=fielditem.split("/");
	//if(fi[6]=="1"){
	//	alert(fi[1]+"指标已经构库，不能删除！");
	// 	return;
	//}
	//if(fi[3]==0&&fi[2]==1){
	//	alert("不能删除系统指标"+fi[1]+"！");
	//	return;
	//}
	//if(confirm("确定删除？"))
	//{
	//  busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_del=link&fieldsetid="+fi[0]+"&itemid="+fi[1]+"&displayid="+fi[5];
	//  busiMaintenceForm.submit();
	//}
	var fieldsetid = document.getElementById("fiid").value;
	var str="";
	for(var i=0;i<document.busiMaintenceForm.elements.length;i++)
			{
				if(document.busiMaintenceForm.elements[i].type=="checkbox")
				{
					if(document.busiMaintenceForm.elements[i].checked==true)
					{
						if(document.busiMaintenceForm.elements[i].name=="selbox")
							continue;
							str+=document.busiMaintenceForm.elements[i].value+"/";
					}
				}
			}
	if(str.length==0){
		alert(PLEASE_SEL);
		return;
	}else{
		if(confirm("<bean:message key="workbench.info.isdelete"/>?")){
				var nodes = str.substring(0).split('/');
				
						 for(var j=0;j<nodes.length;j++)
						 {
						 var currnode=parent.frames['mil_menu'].Global.selectedItem;
						 if(currnode==null)
						 	return;
						 if(currnode.load)
						 for(var i=0;i<=currnode.childNodes.length-1;i++)
						 {
						 var s = currnode.childNodes[i].uid;
						 var rtu = s.substring(0,s.length-2);
							if(nodes[j]==rtu)
							currnode.childNodes[i].remove();
			  			}
			  			}
			  			busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_del=link&str="+str+"&fieldsetid="+fieldsetid;
			  			busiMaintenceForm.submit();	
		}
	}
}
function updateitem(fieldsetid,itemid){
	//var fielditem=busiMaintenceForm.ss.value;
	if(itemid.length<1)
	{
	alert(KJG_YWZD_INFO18);
	return;
	}	
	//var fi=fielditem.split("/");
  	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_update=link&fieldsetid="+fieldsetid+"&itemid="+itemid;
	busiMaintenceForm.submit();
}
function onclicksel(selvalue){
	busiMaintenceForm.ss.value=selvalue;
}
function configshow(){
	var fielditem=busiMaintenceForm.ss.value;
	if(fielditem.length<1)
	{
	alert(KJG_YWZD_INFO19);
	return;
	}	
	var fi=fielditem.split("/");
	if(fi[7]=="1"){
		return;
	}
  	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_config=link&fieldsetid="+fi[0]+"&itemid="+fi[1]+"&state=1";
	busiMaintenceForm.submit();
}
function confignoshow(){
	var fielditem=busiMaintenceForm.ss.value;
	if(fielditem.length<1)
	{
	alert(KJG_YWZD_INFO19);
	return;
	}	
	var fi=fielditem.split("/");
	if(fi[7]=="0"){
	return;
	}
  	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_config=link&fieldsetid="+fi[0]+"&itemid="+fi[1]+"&state=0";
	busiMaintenceForm.submit();
}
function configkeyflag(){
	var fielditem=busiMaintenceForm.ss.value;
	if(fielditem.length<1)
	{
	alert(KJG_YWZD_INFO19);
	return;
	}	
	var fi=fielditem.split("/");
	if(fi[4]=="1"){
	return;
	}
  	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_config=link&fieldsetid="+fi[0]+"&itemid="+fi[1]+"&keyflag=1";
	busiMaintenceForm.submit();
}
function confignokeyflag(){
	var fielditem=busiMaintenceForm.ss.value;
	if(fielditem.length<1)
	{
	alert(KJG_YWZD_INFO19);
	return;
	}	
	var fi=fielditem.split("/");
	if(fi[4]=="0"){
	return;
	}
  	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_config=link&fieldsetid="+fi[0]+"&itemid="+fi[1]+"&keyflag=0";
	busiMaintenceForm.submit();
}
function configownflag(){
	var fielditem=busiMaintenceForm.ss.value;
	if(fielditem.length<1)
	{
	alert(KJG_YWZD_INFO19);
	return;
	}	
	var fi=fielditem.split("/");
	if(fi[2]=="0"){
	return;
	}
  	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_config=link&fieldsetid="+fi[0]+"&itemid="+fi[1]+"&ownflag=0";
	busiMaintenceForm.submit();
}
function confignownflag(){
	var fielditem=busiMaintenceForm.ss.value;
	if(fielditem.length<1)
	{
	alert(KJG_YWZD_INFO19);
	return;
	}	
	var fi=fielditem.split("/");
	if(fi[2]=="1"){
	return;
	}
  	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_config=link&fieldsetid="+fi[0]+"&itemid="+fi[1]+"&ownflag=1";
	busiMaintenceForm.submit();
}
function updateclass(){
	var fieldsetid=busiMaintenceForm.fieldsetid.value;
  	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_updateclass=link&fieldsetid="+fieldsetid;
	busiMaintenceForm.submit();
}
function inputitem(){
	 var fieldsetid= document.getElementById("fid").value;
	 var theurl="/system/busimaintence/showbusifield.do?b_input=link`query=query`fieldsetid="+fieldsetid;
	 theurl = $URL.encode(theurl);
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	 var dw=616,dh=530,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     // var return_vo= window.showModalDialog(iframe_url, arguments,
     //    "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	// if(!return_vo)
	// 	return false;
	// if(return_vo.flag=="true"){
	// 	clearCheckbox();
	// 	reflesh();
	// }
    return_vo ='';
    var theUrl = iframe_url;
    Ext.create('Ext.window.Window', {
        id:'inputitem',
        height: 550,
        width: 630,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:Ext.getBody(),
        listeners: {
            'close': function () {
                if (return_vo.flag =='true') {
                    clearCheckbox();
                    reflesh();
                }
            }
        }

    });
}
function editmakefield(){
	
  	busiMaintenceForm.action="/system/busimaintence/editbusitablemake.do?b_query=link";
	busiMaintenceForm.submit();
}
function freshok(){
	alert(KJG_YWZD_INFO20);
}
function refreshdiction(){
	if(confirm(KJG_YWZD_INFO21)){
  	var request=new Request({method:'post',asynchronous:false,onSuccess:freshok,functionId:'1010060011'});
	}else{
	return;
	}
}
function addrelatingcode(){
	busiMaintenceForm.action="/system/busimaintence/showrelatingcode.do?b_query=link";
	busiMaintenceForm.submit();
}
function orderitem()
{
	// var fieldsetid = document.getElementById("filedid").value;
	var fieldsetid = document.getElementsByName("filedid")[0].value;

	var thecodeurl="/system/busimaintence/showbusifield.do?b_sorting=link&fieldsetid="+fieldsetid;
	var dw=300,dh=350,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	// var return_vo= window.showModalDialog(thecodeurl, "",
	// 		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	// if(return_vo!=null){
	// 	reflesh();
	// }
    return_vo ='';
    var theUrl = thecodeurl;
    Ext.create('Ext.window.Window', {
        id:'orderitem',
        height: 400,
        width: 370,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:Ext.getBody(),
        listeners: {
            'close': function () {
                if (return_vo) {
                    reflesh();
                }
            }
        }

    });
}
function reflesh()
{
		// var fieldsetid = document.getElementById("filedid").value;
		var fieldsetid = document.getElementsByName("filedid")[0].value;
   		document.busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_query=link&fieldsetid="+fieldsetid;
	    document.busiMaintenceForm.submit();
}
function setSelectValue(obj)
{
  var setvalue=obj.value;
  if(setvalue=="0")
    configshow();//显示
  else if(setvalue=="1")
    confignoshow();//隐藏
  else if(setvalue=="2")
    configkeyflag();//主键
  else if(setvalue=="3")
    confignokeyflag();//非主键
  else if(setvalue=="4")
    configownflag();//用户型
  else if(setvalue=="5")
    confignownflag();//系统型
  Element.hide('date_panel');
}
function changeTrColor(id)
 {
    var ob=document.getElementById("tb");
    var j=ob.rows.length;
    for(var i=0;i<j-1;i++)
    {
         var o="a_"+i;
         var obj=document.getElementById(o);
         if(o==id)
         {
           if(o!=null)
           {
               obj.className="selectedBackGroud";
           }
         }
         else
         {
           if(i%2==0)
           {
              if(o!=null)
              {
                obj.className="trShallow";
                
              }
           }
           else
           {
               if(o!=null)
               {
                  obj.className="trDeep";
               }
           }
         }
    }
 }
 
 function  clearCheckbox()
{
   var len=document.busiMaintenceForm.elements.length;
       var i;
     
        for (i=0;i<len;i++)
        {
         if (document.busiMaintenceForm.elements[i].type=="checkbox")
          {
             
            document.busiMaintenceForm.elements[i].checked=false;
          }
        }
}
//-->
</script>

<html:form action="/system/busimaintence/showbusifield">	
<table id="tableContainer" width="100%"  align="left" border="0" cellspacing="0"  cellpadding="0" class="ListTable">

<tr>
<td nowrap="nowrap">
<input type="hidden" name="ss"/>
	<table  width="100%" align="left" border="0" id="tb" cellspacing="0"  cellpadding="0" class="ListTable">
	<THEAD>
		<TR>
				<td class="TableRow" align="center">
				<!--<bean:message key="column.select"/>-->
				<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
				</td>
				<td class="TableRow" align="center">
					<bean:message key="column.sys.status"/>
				</td>
				<logic:equal value="1" name="busiMaintenceForm" property="userType">
				<td  class="TableRow" align="center">
					<bean:message key="system.infor.key"/>
				</td>
				</logic:equal>
				<td class="TableRow" align="center">
					<bean:message key="menu.gz.view"/>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="kh.field.field_n"/>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="kjg.title.zhibiaotaihao"/>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="label.org.type_org"/>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="report.parse.len"/>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="kq.wizard.decimal"/>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="kh.field.code"/>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="kjg.title.table"/>
				</td>
				<hrms:priv func_id="3007311">
				<td class="TableRow" align="center">
					<bean:message key="system.infor.oper"/>
				</td>
				</hrms:priv>
			</TR>
	</THEAD>
		<bean:define id="dev_flag" name="busiMaintenceForm" property="userType"/>
		<%int i=0;%>
		<hrms:paginationdb id="element" name="busiMaintenceForm" sql_str="busiMaintenceForm.sql" table="" where_str="busiMaintenceForm.where" columns="busiMaintenceForm.column" order_by="busiMaintenceForm.orderby" pagerows="15" page_id="pagination" indexes="indexes">
			<%if(i%2==0){ %>
	     <tr class="trShallow" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
	     <%} else { %>
	     <tr class="trDeep" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
	     <%}%>
		<bean:define id="ownflag" name="element" property="ownflag"></bean:define>
		<bean:define id="codeflag" name="element" property="codeflag"></bean:define>
		<bean:define id="itemtype" name="element" property="itemtype"></bean:define>
		<bean:define id="codesetid" name="element" property="codesetid"></bean:define>
		<bean:define id="fieldsetid" name="element" property="fieldsetid"></bean:define>
		<bean:define id="itemid" name="element" property="itemid"></bean:define>
		<bean:define id="keyflag" name="element" property="keyflag"></bean:define>
		<bean:define id="displayid" name="element" property="displayid"></bean:define>
		<bean:define id="useflag" name="element" property="useflag"></bean:define>
		<bean:define id="displayid" name="element" property="displayid"/>
		<bean:define id="state" name="element" property="state"/>
		<!--
		<td class="RecordRow">
		<input name="selradio" type="radio" value="${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}" onclick="onclicksel(this.value)"/>
		</td>
		-->
		  <td align="center" class="RecordRow" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")' nowrap>
		  	<logic:equal name="busiMaintenceForm" property="userType" value="1">
            	<logic:equal name="element" property="useflag" value="0">     		  
     		   		<hrms:checkmultibox name="busiMaintenceForm" property="pagination.select" value="${itemid}" indexes="indexes"/>&nbsp;
	    		</logic:equal> 
	    	</logic:equal>
	    	<logic:equal name="busiMaintenceForm" property="userType" value="0">
	    		<logic:equal name="element" property="useflag" value="0">
	    			<logic:equal name="element" property="ownflag" value="0">
	    				<hrms:checkmultibox name="busiMaintenceForm" property="pagination.select" value="${itemid}" indexes="indexes"/>&nbsp;
	    			</logic:equal>
	    		</logic:equal> 
	    	</logic:equal>
	      </td>
		<td class="RecordRow" align="center" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")'>
		<logic:equal value="1" name="ownflag">
		<img src="/images/unlock.gif" width='12' heigth='10'/>
		</logic:equal>
		</td>
		<logic:equal value="1" name="busiMaintenceForm" property="userType">
		<td class="RecordRow" align="center" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")'>
		<logic:equal value="1" name="element" property="keyflag">
		<bean:message key="kq.emp.change.yes"/>
		</logic:equal>
		<logic:equal value="0" name="element" property="keyflag">
	
		</logic:equal>
		</td>
		</logic:equal>
		<td class="RecordRow" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")'>
		<logic:equal value="1" name="element" property="state">
		&nbsp;<bean:message key="infor.menu.view"/>
		</logic:equal>
		<logic:equal value="0" name="element" property="state">
		&nbsp;<bean:message key="lable.channel.hide"/>
		</logic:equal>
		</td>
		<td class="RecordRow" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")'>
		&nbsp;<bean:write name="element" property="itemdesc"/>
		</td>
		<td class="RecordRow" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")'>
		&nbsp;<bean:write name="element" property="itemid"/>
		</td>
		<td class="RecordRow" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")'>
		<logic:equal value="A" name="element" property="itemtype">
		<logic:equal value="0" name="element" property="codeflag">
		<logic:equal value="0" name="element" property="codesetid">
		&nbsp;<bean:message key="kjg.title.zifuxing"/>
		</logic:equal>
		<logic:notEqual value="0" name="element" property="codesetid">
		&nbsp;<bean:message key="kjg.title.daimaxing"/>
		</logic:notEqual>
		</logic:equal>
		<logic:equal value="1" name="element" property="codeflag">
		&nbsp;<bean:message key="kjg.title.table"/>
		</logic:equal>
		</logic:equal>
		<logic:equal value="D" name="element" property="itemtype">
		&nbsp;<bean:message key="kjg.title.date"/>
		</logic:equal>
		<logic:equal value="N" name="element" property="itemtype">
		&nbsp;<bean:message key="kq.formula.countt"/>
		</logic:equal>
		<logic:equal value="M" name="element" property="itemtype">
		&nbsp;<bean:message key="kjg.title.remark"/>
		</logic:equal>
		</td>
		<td class="RecordRow" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")'>
		&nbsp;<bean:write name="element" property="itemlength"/>
		</td>
		<td class="RecordRow" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")'>
		<logic:notEqual value="0" name="element" property="decimalwidth">
		&nbsp;<bean:write name="element" property="decimalwidth"/>
		</logic:notEqual>
		</td>
		<td class="RecordRow" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")'>
		<logic:notEqual value="0" name="element" property="codesetid">
		<logic:notEqual value="1" name="element" property="codeflag">
		&nbsp;<bean:write name="element" property="codesetid"/>
		</logic:notEqual>
		</logic:notEqual>
		</td>
		<td nowrap="nowrap" class="RecordRow" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")'>
		<logic:equal value="1" name="element" property="codeflag">
		&nbsp;<bean:write name="element" property="codesetid"/>
		<hrms:relatingcode codesetid="${codesetid}"></hrms:relatingcode>
		</logic:equal>
		</td>
		<hrms:priv func_id="3007311">
		<td align="center" class="RecordRow" onclick='onclicksel("${fieldsetid}/${itemid}/${ownflag}/${dev_flag}/${keyflag}/${displayid}/${useflag}/${state}")' nowrap>
			<a href='javascript:updateitem("${fieldsetid}","${itemid}")' ><bean:message key="label.edit"/></a>&nbsp;
		</td>
		</hrms:priv>
		</tr>
		<% i++; %>
		</hrms:paginationdb>
		<html:hidden name="busiMaintenceForm" property="filedid"/>
	</table>
	</td>
	</tr>
	<TR>
	<td>
	  <input type="hidden" name="displayid" value="${displayid}"/>
	  <input type="hidden" Id="fiid" name="fieldsetid" value="${fieldsetid}"/>
	  <html:hidden styleId="fid" name="busiMaintenceForm" property="filedid"/>
	  <table width="100%" align="center" class="RecordRowP">
			<tr>
				<td  valign="bottom" class="tdFontcolor" >
					<bean:message key="label.page.serial" />
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum" />
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row" />
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page" />
				</td>
				<td align="right" nowrap class="tdFontcolor">
					<p align="right"><hrms:paginationdblink name="busiMaintenceForm" property="pagination" nameId="browseRegisterForm" scope="page">
						</hrms:paginationdblink>
				</td>
			</tr>
			
		</table>
	</td>
	</tr>
	<tr>
	 <td>
	    <table width="80%" align="center">
			<tr>
				<td  valign="bottom" align="center" class="tdFontcolor" nowrap>
				<logic:notEqual value="50" name="busiMaintenceForm" property="id">
				<hrms:priv func_id="3007309">
					<button name='and'type="button" class="mybutton" onclick='additem();'><bean:message key="menu.gz.new" /></button>&nbsp;
				</hrms:priv>
				<hrms:priv func_id="3007310">
					<button name='inset' type="button" class="mybutton" onclick='insertitem();'><bean:message key="button.new.insert" /></button>&nbsp;
				</hrms:priv>
				<hrms:priv func_id="3007308">
					<button type="button" type="button" name='inp' class="mybutton" onclick='inputitem();'><bean:message key="menu.gz.import" /></button>&nbsp;
				</hrms:priv>
				<hrms:priv func_id="3007312">
					<button name='del' type="button" class="mybutton" onclick='delitem();'><bean:message key="menu.gz.delete" /></button>&nbsp;
					</hrms:priv>
				</logic:notEqual>
					<hrms:priv func_id="3007313">
					<button type="button" type="button" name='ord' class="mybutton" onclick='orderitem();'><bean:message key="kh.field.sort" /></button>&nbsp;
					</hrms:priv>
					<hrms:priv func_id="3007314">
					<button type="button" type="button" name='set' class="mybutton"  onclick="showDateSelectBox(this);"	 class="mybutton" /><bean:message key="kh.field.config" /></button>
				</hrms:priv>
				</td>
			</tr>
			
		</table>
	 </td>
	</tr>
</table>
<div id="date_panel" style="width:90px; height:120px; overflow: hidden; border: 1px solid #94B6E6;display: none" class="common_border_color"  >
		<select name="date_box" multiple="multiple" size="6" style="position:absolute; left:-2; top:-2; font-size:12px;border-width:0px;"
			onchange="setSelectValue(this);" onblur="Element.hide('date_panel');">
			<option value="0"><bean:message key="lable.channel.visible" /></option>
			<option value="1"><bean:message key="lable.channel.hide" /></option>
			<logic:equal value="1" name="busiMaintenceForm" property="userType">
			<option value="2"><bean:message key="system.infor.key" /></option>
			<option value="3"><bean:message key="kjg.title.noprimarykey" /></option>
			<option value="4"><bean:message key="kjg.title.usertype" /></option>
			<option value="5"><bean:message key="kjg.title.systemtype" /></option>
			</logic:equal>
		</select>
</div>
</html:form>

<script type="text/javascript">
Element.hide('date_panel');
function showDateSelectBox(srcobj)
   {
      var date_desc=srcobj;
      for(var i=0;i<document.busiMaintenceForm.date_box.options.length;i++)
  	  {
  	  	document.busiMaintenceForm.date_box.options[i].selected=false;
  	  }
      var pos=getAbsPosition(srcobj);
	  with($("date_panel"))
	  {
	     if(isIE6()){
	     if(${busiMaintenceForm.userType}==0){
	           style.position="absolute";
                 if(getBrowseVersion() =='10'){
                     style.posLeft=pos[0];
                     style.posTop=pos[1]+srcobj.offsetHeight;
                 }else {
                     style.left=pos[0];
                     style.top=pos[1]+srcobj.offsetHeight;
                 }
			   style.width=35+"px";
			   style.height=33+"px";
	        }else{
	           style.position="absolute";
                 if(getBrowseVersion() =='10'){
                     style.posLeft=pos[0];
                     style.posTop=pos[1]+srcobj.offsetHeight;
                 }else {
                     style.left=pos[0];
                     style.top=pos[1]+srcobj.offsetHeight;
                 }
			   style.width=43+"px";
			   style.height=86+"px";
	        }
	     }else{
	       if(${busiMaintenceForm.userType}==0){
	           style.position="absolute";
			   if(getBrowseVersion() =='10'){
                   style.posLeft=pos[0];
                   style.posTop=pos[1]+srcobj.offsetHeight;
			   }else {
                   style.left=pos[0];
                   style.top=pos[1]+srcobj.offsetHeight;
			   }
			   style.width=36+"px";
			   style.height=42+"px";
	        }else{
	           style.position="absolute";
                 if(getBrowseVersion() =='10'){
                     style.posLeft=pos[0];
                     style.posTop=pos[1]+srcobj.offsetHeight;
                 }else {
                     style.left=pos[0];
                     style.top=pos[1]+srcobj.offsetHeight;
                 }
			   style.width=46+"px";
	        }
	     } 
      }            
      Element.show('date_panel');
	  $("date_panel").children[0].focus();
   }
</script>