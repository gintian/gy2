<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
 // VersionControl control=new VersionControl();
  boolean valid = false;
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  if(userView != null){
	   if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		valid = true;
		}
  }
	
 int i=0;
%>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/js/showModalDialog.js"></script>
<script language="javascript">

	function searchfiled()
	{
		posCodeParameterForm.action = "/pos/posparameter/ps_parameter.do?b_search_unit=link";
		posCodeParameterForm.submit();
	}
	
	
var arr = new Array();
var tabid="";
var tablestr="";
var trid=0;
var tridArr = new Array();
var count=0;
function configId(id)
{
     trid=id;
}	
function addRow()
{
   if($F('ps_set')=='0')
   {
   		alert(CHOICE_UNIT_WEAVE_SUBSET);
   		return;
   }
   var tab=document.getElementById("tt");
   tablestr=tablestr.replace("</table>","").replace("</TABLE>","");
   var tabstr="";
   var v="";
   arr.push(" # # # # # # ");
   var newRow=tab.insertRow(tab.rows.length);
    newRow.id="cc"+count;
    newRow.onclick="configId('"+count+"');";
    myNewCell=newRow.insertCell(0);
    myNewCell.align="center";
    myNewCell.className="RecordRow";
    tabstr+="<tr onclick=\"configId('"+count+"');\">";
         tabstr+="";
         tabstr+="<div id=\"a_"+count+"_0\">";
         tabstr+="<input type=\"text\" name=\"aa_"+count;
         tabstr+="_0_0\" class=\"text4\" style=\"width:130px;text-align:left;\" onclick=\"change('a_"+count+"_0');\" value=\"\"/>";
		 tabstr+="</div>";		
		 tabstr+="<input type=\"hidden\" name=\"plan\" id=\"a_"+(count)+"_0_0\" value=\"\"/>";
		 tabstr+="";
myNewCell.innerHTML =tabstr;
tabstr="";
myNewCell=newRow.insertCell(1);
myNewCell.align="center";
myNewCell.className="RecordRow";
tabstr+="";
		 tabstr+="<div id=\"a_"+(count)+"_1\">";		
		 tabstr+="<input type=\"text\" name=\"aa_";
		 tabstr+=i+"_1_0\" class=\"text4\" style=\"width:130px;text-align:left;\" onclick=\"change('a_"+count+"_1');\" value=\"\"/>";
		 tabstr+="</div>";
		 tabstr+="<input type=\"hidden\" name=\"real\" id=\"a_"+(count)+"_1_0\" value=\"\"/>";
	     tabstr+="";
myNewCell.innerHTML =tabstr;
tabstr="";
myNewCell=newRow.insertCell(2);
myNewCell.align="center";
myNewCell.className="RecordRow";
	     tabstr+="<div id=\"a_"+(count)+"_2\">";
		 tabstr+="<input type=\"text\" class=\"text4\" style=\"width:150px;text-align:left;\" onclick=\"change1('a_"+(count)+"_2');\" value=\"\"/>";		
		 tabstr+="</div>";	
		 tabstr+="<input type=\"hidden\" name=\"static\" id=\"a_"+(count)+"_2_0\" value=\"\"/>";
myNewCell.innerHTML =tabstr;
tabstr="";
myNewCell=newRow.insertCell(3);
myNewCell.align="center";
myNewCell.className="RecordRow";
	     tabstr+="<div id=\"a_"+(count)+"_4\" align=\"center\">";
		 tabstr+="<select name=\"method\" onchange=\"changePos(this,";
		 tabstr+=(count-1)+");\"><option value=\"0\">个数</option>";
		 tabstr+="<option value=\"1\">百分比</option></select>";
		 tabstr+="</div>";
myNewCell.innerHTML=tabstr;	 
tabstr="";
myNewCell=newRow.insertCell(4);
myNewCell.align="center";
myNewCell.className="RecordRow";
	     tabstr+="<div id=\"a_"+(count)+"_5\" align=\"center\">";
		 tabstr+="<input type=\"text\" class=\"textColorWrite\" name=\"message\" value=\"\" />";
		 tabstr+="</div>";
myNewCell.innerHTML=tabstr;
tabstr="";
myNewCell=newRow.insertCell(5);
myNewCell.align="center";
myNewCell.className="RecordRow";
	     tabstr+="<div id=\"a_"+(count)+"_6\" align=\"center\">";
		 tabstr+="<img src=\"/images/edit.gif\" onclick=\"selectCtrlOrg('a_"+(count)+"_6_0');\" style=\" cursor: hand;\"/>";
		 tabstr+="<input type=\"hidden\" name=\"ctrlorg\" id=\"a_"+(count)+"_6_0\" value=\"\"/>";
		 tabstr+="<input type=\"hidden\" name=\"nextorg\" id=\"a_"+(count)+"_6_0_1\" value=\"0\"/>";
		 tabstr+="</div>";
myNewCell.innerHTML=tabstr;
tabstr="";
myNewCell=newRow.insertCell(6);
myNewCell.align="center";
myNewCell.className="RecordRow";
  tabstr+="<div id=\"a_"+(count)+"_3\" align=\"center\">";
		 tabstr+="<input type=\"checkbox\" name=\"flag\" value=\"0\"";
		 tabstr+="/></div></tr>";
myNewCell.innerHTML=tabstr;	
    count++;
}
var oildid = "";
function change(id)
{
	beforeChange();
	tr_bgcolor(id);
   var arr = id.split("_");
   var obj=document.getElementById(id);
   var tabstr = posCodeParameterForm.table.value.replace("#",id);
   if(arr!=null&&arr.length==3){
		if(arr[2]=="1"){
			var methodArr = document.getElementsByName("method");
			var method = methodArr[rowindex-1].value;
			if(method=="1"){
				tabstr+="/<span onclick=\"exebolishsubmit(";
				tabstr+=(arr[1]-1)+");\" style=\"cursor:hand;color:#0033FF\">计算公式</span>";
				tabstr+="<input type=\"hidden\" name=\"cond\" id=\"";
				tabstr+=id+"_b\">";
			}
		}   		
   }
   obj.innerHTML=tabstr;
   var temp=id.split("_");
   tabid = temp[1];
   trid=temp[1];
   document.getElementsByName("selectid")[0].value=document.getElementById(id+"_0").value;
   document.getElementsByName("selectid")[0].style.width="130px";
   document.getElementsByName("selectid")[0].focus();	
}
function change1(id)
{
	beforeChange();
   oldid = id;
   var obj=document.getElementById(id);
   obj.innerHTML=posCodeParameterForm.expr.value.replace("#",id);
   tr_bgcolor(id);
   var temp=id.split("_");
   tabid = temp[1];
   trid=temp[1];
   document.getElementsByName("selectid")[0].value=document.getElementById(id+"_0").value;
   document.getElementsByName("selectid")[0].style.width="150px";
   document.getElementsByName("selectid")[0].focus();	
}

var rowindex = 0;
function tr_bgcolor(nid){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    var cvalue = tablevos[i];
	    var td = cvalue.parentNode.parentNode;
	    td.style.backgroundColor = '';
    }
	var c = document.getElementById(nid);
	var tr = c.parentNode.parentNode;
	rowindex = tr.rowIndex;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#FFF8D2' ;
	}
}


//解决同时存在多个下拉框时id相同无法选择问题
function beforeChange(){
	var c = document.getElementsByName("selectid");
	for(var i =0;i<c.length;i++){
		index = c[i].options.selectedIndex;
		c[i].click();
		c[i].onblur();
	}
}

function onLeave(id)
{
    var obj=document.getElementsByName("selectid")[0];
    //obj.focus();
    var itemid="";
    var text ="";
    var name;
     var temp=id.split("_");
    tabid = temp[1];
	document.getElementsByName("selectid")[0].onblur=function(){
	for(var i=0;i<obj.options.length;i++)
	{
	   if(obj.options[i].selected)
	   {
	   	  itemid = obj.options[i].value;
	      document.getElementById(id+"_0").value=itemid;
	      text=obj.options[i].text;
	   }
	}
	if(text == "undefined")
	{
	   text = "";
	}
	var arr=id.split("_");
	var methodArr = document.getElementsByName("method"); 
	var method = methodArr[rowindex-1].value;
	if(arr[2] == '0'){
	 	name="plan";
	}else if(arr[2] =='1'){
		 var planArr=document.getElementsByName("plan");
		 itemid = planArr[rowindex-1].value;
	 	name="real";
	}else{
	 	name="static";
	 }
	var t="<input type=\"text\" name=\"aa_"+arr[1]+"_"+arr[2]+"_0\" value=\""+text+"\" onclick=\"";
	t+="change('"+id+"');\" style=\"width:130px;text-align:left;\">";

	if(method==1&&name=="real"){
		t+="/<span onclick=\"exebolishsubmit(";
		t+=(arr[1]-1)+");\" style=\"cursor:hand;color:#0033FF\">计算公式</span>";
		t+="<input type=\"hidden\" name=\"cond\" id=\"";
		t+=id+"_b\">";
	}
	document.getElementById(id).innerHTML=t;
	}	
}
function onLeave1(id)
{
    var obj=document.getElementsByName("selectid")[0];
    //obj.focus();
    var itemid;
    var text ="";
    var name;
     var temp=id.split("_");
    tabid = temp[1];
	document.getElementsByName("selectid")[0].onblur=function(){
	for(var i=0;i<obj.options.length;i++)
	{
	   if(obj.options[i].selected)
	   {
	   	  if(obj.options[i].value=="0"){
	   	  	return;
	   	  }
	      document.getElementById(id+"_0").value=obj.options[i].value; 
	      text=obj.options[i].text;
	   }
	}
	if(text == "undefined")
	{
	   text = "";
	}
	var arr=id.split("_");
	if(arr[2] == '0')
	 name="plan";
	 else if(arr[2] =='1')
	 name="real";
	 else
	 name="static";
	var t="<input type=\"text\" name=\"aa_"+arr[1]+"\" value=\""+text+"\" onclick=\"";
	t+="change1('"+id+"');\" style=\"width:150px;text-align:left;\">";
	
	document.getElementById(id).innerHTML=t;
	}	
}
function addexpr()
{	
	var obj=document.getElementsByName("selectid")[0];
	for(var i=0;i<obj.options.length;i++)
	{
	   if(obj.options[i].selected)
	   {
	   	  if(obj.options[i].value=="0"){
	   	  	thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type=1&a_code=UN&tablename=Usr&ps_flag=1&bzsearch=0";//bzsearch 为了区分从此处进入
		    var ps;
			if(getBrowseVersion()) //IE浏览器
				ps = parent.frames[0];
			else //非IE
				ps = parent.frames['center_iframe'].contentWindow;
	
			if(ps.Ext.getCmp("ps_parameter")){
				ps.Ext.getCmp("ps_parameter").close(); //防止再次点击
    		}
		    ps.Ext.create('Ext.window.Window',{
				id:'ps_parameter',
				title:'新增',
				width:720,
				height:450,
				resizable:false,
				modal:true,
				autoScroll:false,
				autoShow:true,
				autoDestroy:true,
				html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
				renderTo:ps.Ext.getBody(),
				listeners:{
					'close':function(){
						if(this.return_vo!=null)
		    			{
		    				document.getElementById(oldid+"_0").value=getSelectId(this.return_vo);
		    				var hashvo=new ParameterSet();
		    				var request=new Request({method:'post',asynchronous:false,onSuccess:refexpr,functionId:'18010000015'},hashvo);
		    			}else{
		    				change1(oldid);
		    			}
					}
				}
		
			});
	   	  }
	   }
	}
}
function getSelectId(ids){
	var idArr = ids.split(":");
	if(idArr.length==2)
		return idArr[0];
	else
		return "";
	
}
function refexpr(outparamters)
{
	posCodeParameterForm.expr.value = getDecodeStr(outparamters.getValue("expr"));
	change1(oldid);
}
function save()
{
	if("true" == document.getElementsByName("zwvalid")[0].value){
		var psZwSet = document.getElementsByName("zw_set")[0].value;
		if(psZwSet=="#" || psZwSet=="") {
            alert(CHOICE_PS_SET);
            document.getElementsByName("zw_set")[0].focus();
            return;
        }
        
		var psWorkfixed = document.getElementsByName("ps_workfixed")[0].value;
		if(psWorkfixed=="#" || psWorkfixed=="") {
			alert(CHOICE_PS_WORKFIXED);
			document.getElementsByName("ps_workfixed")[0].focus();
			return;
		}

		var psWorkexist = document.getElementsByName("ps_workexist")[0].value;
		if(psWorkexist=="#" || psWorkexist=="") {
			alert(CHOICE_PS_WORKEXIST);
			document.getElementsByName("ps_workexist")[0].focus();
			return;
		}
	}
	
<%if(valid)
{%>
  var plan=document.getElementsByName("plan");
  var real = document.getElementsByName("real");
  var balance = document.getElementsByName("static");
  var flag = document.getElementsByName("flag");
  var method = document.getElementsByName("method");
   var cond=document.getElementsByName("cond");
   var message = document.getElementsByName("message");
   var ctrlorg = document.getElementsByName("ctrlorg");
   var nextorg = document.getElementsByName("nextorg");
  var spflagid=posCodeParameterForm.sp_flag.value;

  if($F('ps_set')!='0'){
	  if(spflagid=="0")
	  {
	    alert(CHOICE_APPROVE_STATE_TARGET);
	    return;
	  }
  }
  var planitem="";
  var realitem="";
  var balanceitem="";
  var flagitem="";
  var methoditem="";
  var conditem="";
  var messitem = "";
  var ctrlorgitem = "";
  var nextorgitem = "";
  var n=0;
  for(var j=0;j<plan.length;j++)
  {
     if(flag[j].checked)
     {
        flag[j].value='1';
     }
     else
     {
        flag[j].value='0';
     }
     if(plan[j].value==null||plan[j].value=='new'||plan[j].value=='')
        {
          alert(POS_POSPARAMETER_INFO01+(j+1)+POS_POSPARAMETER_INFO02);
          return;
        }
        if(real[j].value==null||real[j].value=='new'||real[j].value=='')
        {
          alert(POS_POSPARAMETER_INFO01+(j+1)+POS_POSPARAMETER_INFO03);
          return;
        }
        
        if(plan[j].value==real[j].value)
        {
           alert(POS_POSPARAMETER_INFO04+(j+1)+POS_POSPARAMETER_INFO05);
           return;
        }
       
         for(var i=0;i<plan.length;i++)
         {
            if(j!=i&&plan[j].value==plan[i].value)
            {
               alert(POS_POSPARAMETER_INFO06+(j+1)+POS_POSPARAMETER_INFO07+(i+1)+POS_POSPARAMETER_INFO08);
               return;
            }
            if(j!=i&&real[j].value==real[i].value)
            {
              alert(POS_POSPARAMETER_INFO09+(j+1)+POS_POSPARAMETER_INFO07+(i+1)+POS_POSPARAMETER_INFO08);
              return;
            }
            
            if(plan[j].value==real[i].value)
            {
                 alert(POS_POSPARAMETER_INFO06+(j+1)+POS_POSPARAMETER_INFO10+(i+1)+POS_POSPARAMETER_INFO08);
                 return;
            }
         }
     planitem+="/"+plan[j].value;
     realitem+="/"+real[j].value;
     balanceitem+="/"+balance[j].value;
     flagitem+="/"+flag[j].value;
     methoditem+="/"+method[j].value;
     messitem += "/"+message[j].value;
     ctrlorgitem += "/"+ctrlorg[j].value;
     nextorgitem += "/"+nextorg[j].value;
     n++;
  }
  if(n>0)
  {
      var tep = planitem.substring(1).split("/");
      var tep2 = planitem.substring(1).split("/");
      for(var i=0;i<tep.length;i++)
      {
          for(var j=0;j<tep2.length;j++)
          {
             
              if(tep[i]==tep2[j])
              {
                 if(i==j)
                 {
                    continue;
                 }
                 else
                 {
                   alert(POS_POSPARAMETER_INFO11+(i+1)+POS_POSPARAMETER_INFO12+(j+1)+POS_POSPARAMETER_INFO13);
                   return;
                 }
              }
          }
      }
  }
  if(n>0)
  {
  	 for(var j=0;j<cond.length;j++){
  	 	conditem+="/"+cond[j].value;
  		
  	 }
     posCodeParameterForm.planitem.value = planitem.substring(1);
     posCodeParameterForm.realitem.value= realitem.substring(1);
     posCodeParameterForm.staticitem.value = balanceitem.substring(1);
     posCodeParameterForm.flagitem.value=flagitem.substring(1);
     posCodeParameterForm.methoditem.value=methoditem.substring(1);
     posCodeParameterForm.conditem.value=conditem.substring(1);
     posCodeParameterForm.messitem.value=messitem.substring(1);
     posCodeParameterForm.ctrlorgitem.value=ctrlorgitem.substring(1);
     posCodeParameterForm.nextorgitem.value=nextorgitem.substring(1);
  }
  var hashvo=new ParameterSet();
	hashvo.setValue("planitem",planitem.substring(1));
	hashvo.setValue("staticitems",balanceitem.substring(1));
	var request=new Request({asynchronous:false,onSuccess:addsuccess,functionId:'18010000061'},hashvo); 
	function addsuccess(outparamters){
		var msg=outparamters.getValue("msg");
		msg=getDecodeStr(msg);
		if('ok'==msg){
			posCodeParameterForm.action = "/pos/posparameter/ps_parameter.do?b_save_unit=link&org_flag=${posCodeParameterForm.org_flag}";
  			posCodeParameterForm.submit();
  			<logic:equal name="posCodeParameterForm" property="org_flag" value="1">
  				returnOk();
  			</logic:equal>
		}else if("error"!=msg){
			alert(msg);
		}
	}
<%}else{%>
	posCodeParameterForm.action = "/pos/posparameter/ps_parameter.do?b_save_unit=link&org_flag=${posCodeParameterForm.org_flag}";
  			posCodeParameterForm.submit();
  			<logic:equal name="posCodeParameterForm" property="org_flag" value="1">
  				returnOk();
  			</logic:equal>
	<%}%>
}

function deleterow(){
	var tab=document.getElementById("tt");
	if(rowindex>0)
	  tab.deleteRow(rowindex);
}

function returnOk()
{
	returnValue="aaaa";
	window.close();
}

function setDb()
{
    var dw = 350;
	var dh = 280;
	if(isIE6()){
		dw += 20;
		dh += 15;
	}
	var dbPre = posCodeParameterForm.dbpre.value;
    var target_url = '/pos/posparameter/ps_parameter.do?b_setdb=link`dbpre=' + dbPre;
    var iframe_url = '/general/query/common/iframe_query.jsp?src=' + $URL.encode(target_url);
	var obj;
	if(getBrowseVersion()) //IE浏览器
		obj = parent.frames[0];
	else //非IE
		obj = parent.frames['center_iframe'].contentWindow;
	
	if(obj.Ext.getCmp("ps_parameter")){
		obj.Ext.getCmp("ps_parameter").close(); //防止再次点击
    }
	obj.Ext.create('Ext.window.Window',{
		id:'ps_parameter',
		title:'人员库设置',
		width:dw,
		height:dh,
		resizable:false,
		modal:true,
		autoScroll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+iframe_url+'"></iframe>',
		renderTo:obj.Ext.getBody(),
		listeners:{
			'close':function(){
				if(this.msg != null){
					posCodeParameterForm.dbpre.value = this.msg.mess;
			    }
			}
		}
		
	});
    
}
function changePos(obj,id){
	var method = obj.value;
	var idname = "a_"+(id+1)+"_1"
	var descname = "aa_"+(id+1)+"_1_0"
	var desc = document.getElementsByName(descname)[0].value;	
	var t="";
	t="<input type=\"text\" name=\""+descname+"\" value=\""+desc+"\" onclick=\"";
	t+="change('"+idname+"');\" style=\"width:130px;text-align:left\">";
	if(method=='1'){
		t+="/<span onclick=\"exebolishsubmit(";
		t+=id+");\" style=\"cursor:hand;color:#0033FF\">计算公式</span><input type=\"hidden\" name=\"cond\" id=\"";
		t+=idname+"_b\">";
	}
	if(idname != ""){
		document.getElementById(idname).innerHTML=t;
	}
}
function exebolishsubmit(id){
	var idname = "a_"+(id+1)+"_1_b"
	var values = document.getElementById(idname).value;
	var url="/gz/gz_amount/complexquery.do?b_query=link`flag=1`itemid=${posCodeParameterForm.numitemid}`formula="+values; 
	var url = '/general/query/common/iframe_query.jsp?src=' + $URL.encode(url);
	var parameter = '';
	var ps;
	if(getBrowseVersion()) //IE浏览器
		ps = parent.frames[0];
	else //非IE
		ps = parent.frames['center_iframe'].contentWindow;
	
	if(ps.Ext.getCmp("ps_parameter")){
		ps.Ext.getCmp("ps_parameter").close(); //防止再次点击
    }
	
    if (!window.showModalDialog){
        ps.Ext.create('Ext.window.Window',{
		id:'ps_parameter',
		title:'<bean:message key="kq.item.count"/>',
		width:570,
		height:450,
		resizable:false,
		modal:true,
		autoScroll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+url+'"></iframe>',
		renderTo:ps.Ext.getBody(),
		listeners:{
			'close':function(){
				if(this.obj)
					document.getElementById(idname).value=getEncodeStr(this.obj);
			}
		}}).show();
    } else {
    	var return_vo= window.showModalDialog(url, "", 
	    "dialogWidth:585px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
	    if(return_vo)
	    	document.getElementById(idname).value=getEncodeStr(return_vo);
    }
}

function selectCtrlOrg(id){
	var obj = document.getElementById(id);
	var options = document.getElementById('ctrlUM').options;
	
	var ctrlUM = options[document.getElementById('ctrlUM').selectedIndex].value;
	var nextorg = $(id+"_1").value;
	var ctrlorg = obj.value;
	var url = "/pos/posparameter/ps_parameter.do?b_select_org=link`ctrlorg="+ctrlorg+"`nextorg="+nextorg+"`ctrlUM="+ctrlUM;
	var iframe = "/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
	var dw = 535;
	var dh = 270;
    if(isIE6()){
    	dh += 15;
    }
    var obj;
	if(getBrowseVersion()) //IE浏览器
		obj = parent.frames[0];
	else //非IE
		obj = parent.frames['center_iframe'].contentWindow;
	
	if(obj.Ext.getCmp("org_selectCtrlOrg")){
		obj.Ext.getCmp("org_selectCtrlOrg").close(); //防止再次点击
    }
	obj.Ext.create('Ext.window.Window',{
		id:'org_selectCtrlOrg',
		title:'归属单位',
		width:dw,
		height:dh,
		resizable:false,
		modal:true,
		autoScroll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+iframe+'"></iframe>',
		renderTo:obj.Ext.getBody(),
		listeners:{
			'close':function(){
				if(this.msg == null)
					return;
				nextorg = this.msg.substr(0,1);
				ctrlorg = this.msg.substr(2,this.msg.length);
				ctrlorg = ctrlorg.replaceAll("`",",");
				$(id).value = ctrlorg;
				$(id+"_1").value = nextorg
			    return false;
			}
		}
		
	});
}

String.prototype.replaceAll = function(s1,s2) { 
    return this.replace(new RegExp(s1,"gm"),s2); 
}

</script>
<html:form action="/pos/posparameter/ps_parameter">
<html:hidden name="posCodeParameterForm" property="table"/>
<html:hidden name="posCodeParameterForm" property="expr"/>
<html:hidden name="posCodeParameterForm" property="dbpre"/>
<!--linbz 20160908 缺陷22583，注释 ,并把width调成80% 同其他参数设置页面"<div style="overflow-y:auto;height:480px; width: 1080px; ">zhangcq  设置页面下拉框 2016/7/14" -->
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow"  >
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="pos.posparameter.ps_parameter"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
  <tr>
    <td align="center" height="13">  
    </td>
   </tr>
<%
if(valid){ %>  
    <tr>
    <td align="center">
     <fieldset align="center" style="width:95%;" class="complex_border_color">
         <legend ><bean:message key="unit.posparameter.unitworkout"/></legend>
          <table align="left" width="90%" > 
           <tr>
              <td align="right"  nowrap>
     	       		<bean:message key="unit.posparameter.unitfixedset"/>   	 
     	      </td>
     	      <td align="left"  nowrap>         
                <html:select name="posCodeParameterForm" property="ps_set" size="1" onchange="searchfiled()">
                 	<html:optionsCollection property="fieldsetlist" value="dataValue" label="dataName" />
                </html:select>
	          </td>                     
		      <td align="right"  nowrap>
	     	       <bean:message key="label.gz.sp"/>    
	     	  </td>
		      <td align="left"  nowrap>	          
	                <html:select name="posCodeParameterForm" property="sp_flag" size="1">
	                 	<html:optionsCollection property="spflaglist" value="dataValue" label="dataName" />
	                </html:select>
	                <font color="black"> &nbsp;&nbsp;<bean:message key="pos.posparameter.info01"/></font>
		      </td>
		      <td>
		    	<hrms:priv func_id="2306201">
	           		<input type="button" name="btnreturn" value="<bean:message key='leaderteam.leaderparam.dbsetting'/>" class="mybutton"  onclick="setDb();">
	        	</hrms:priv>
	          </td>                     
       </tr>  
      <tr>
         <td align="right"  nowrap>
     	       <bean:message key="pos.posparameter.controldeptweave"/>
	     </td>
	     <td align="left"  nowrap>
    	           <html:select name="posCodeParameterForm" property="PSValid" size="1" styleId="ctrlUM">
    	               <html:option value="1"><bean:message key="pos.posparameter.need"/></html:option>
                       <html:option value="0"><bean:message key="pos.posparameter.needless"/></html:option>
                </html:select>
	     </td>
	     <td align="right"  nowrap>
    	     <bean:message key="pos.posparameter.whethercontrolorgan"/> 
    	 </td>
	     <td align="left"  nowrap>
    	      <html:select name="posCodeParameterForm" property="nextlevel" size="1">
                       <html:option value="1"><bean:message key="kq.emp.change.yes"/></html:option>
                       <html:option value="0"><bean:message key="kq.emp.change.no"/></html:option>
                </html:select>
        </td>
        <td nowrap="nowrap">
     	      超编控制方式
    	           <html:select name="posCodeParameterForm" property="mode" size="1">
                       <html:option value="force">强制控制</html:option>
                       <html:option value="warn">预警提示</html:option>
                </html:select>
	    </td>                              	        	        
           </tr> 
        <tr>
           <td align="right" nowrap="nowrap">
              <bean:message key="pos.posparameter.headcountcontrolitem"/>
           </td>
     	   <td align="left" colspan="4"  nowrap>
              <html:select name="posCodeParameterForm" property="controlitemid" size="1" style="width:100px">
                <html:optionsCollection name="posCodeParameterForm" property="controlitemids" label="dataName" value="dataValue"/>
              </html:select>
              &nbsp;&nbsp;<bean:message key="pos.posparameter.controlitemid"/>
           </td>
        </tr>     
       </table>
    </fieldset>  
    </td>
   </tr>
 
   <tr>
	<td style="padding-top:10px;" align="center">
	<fieldset align="center" style="width:95%;">
	<legend><bean:message key="pos.posparameter.itemandexpressionssetting"/></legend>
		<table width="99%" id ="tt" align="center" border="0" cellspacing="0" cellpadding="0" class="ListTable" style="margin:10px,0px,0px,0px;">
			 <thead>
			    <tr class="TableRow">
				   <td align="center" class="TableRow" width="15%">
					 <bean:message key="pos.posparameter.weavenumber"/>
					  </td>
					  <td align="center" class="TableRow" width="15%">
					 <bean:message key="pos.posparameter.facthumans"/>
					 </td>
					 <td align="center" class="TableRow" width="25%">
					 <bean:message key="pos.posparameter.statformula"/>
					 </td>
					  <td align="center" class="TableRow" width="10%">
					 	控制方法
					 </td>
					 <td align="center" class="TableRow" width="20%" nowrap="nowrap">
					    <bean:message key="workdiary.message.message"/>
					 </td>
					 <td align="center" class="TableRow" width="10%" nowrap="nowrap">
					    <bean:message key="lable.lawfile.ascriptionunit"/>
					 </td>
					 <td align="center" class="TableRow" width="5%" nowrap="nowrap">
					 <bean:message key="kh.field.yx"/>
					 </td>
				 </tr>
			 </thead>
				<logic:iterate id="element" name="posCodeParameterForm" property="list" indexId="index">
				  <tr onclick="configId('<%=(i+1)%>');">
				  		<bean:define name="element" property="realitemdesc" id="realitemdesc"/>
				  		<bean:define name="element" property="planitem" id="planitem"/>
				  		
				        <td width="" align="center" class="RecordRow" nowrap>
				         <div id="<%="a_"+(i+1)+"_0"%>">
				         <input type="text" class="textColorWrite" name="<%="aa_"+(i+1)+"_0_0"%>" style="text-align:left;width:130px" onclick="change('<%="a_"+(i+1)+"_0"%>');" value="<bean:write name="element" property="planitemdesc"/>"/>
						 </div>		
						 <input type="hidden" name="plan" id="<%="a_"+(i+1)+"_0_0"%>" value="${planitem}"/>
						 </td>
						 <td width="" align="center" class="RecordRow" nowrap>
						 <div id="<%="a_"+(i+1)+"_1"%>">		
						 <input type="text" class="textColorWrite" name="<%="aa_"+(i+1)+"_1_0"%>" style="text-align:left;width:130px" onclick="change('<%="a_"+(i+1)+"_1"%>');" value="${realitemdesc}"/>
						 <logic:notEqual name="element" property="method" value="0">
						 	<bean:define name="element" property="cond" id="cond"/>
						 	/<span onclick="exebolishsubmit(<%=i+""%>);" style="cursor:hand;color:#0033FF">计算公式</span>
						 	<input type="hidden" id='<%="a_"+(i+1)+"_1_b"%>' name='cond' value="${cond}"/>
						 </logic:notEqual>
						 </div>
						 <input type="hidden" name="real" id="<%="a_"+(i+1)+"_1_0" %>" value="<bean:write name="element" property="realitem"/>"/>
					     </td>
					     <td width="" align="center" class="RecordRow" nowrap>
					     <div id="<%="a_"+(i+1)+"_2"%>">
						 <input type="text" class="textColorWrite" style="text-align:left;width:150px" onclick="change1('<%="a_"+(i+1)+"_2"%>');" value="<bean:write name="element" property="staticitemdesc"/>"/>		
						 </div>	
						 <input type="hidden" name="static" id="<%="a_"+(i+1)+"_2_0"%>" value="<bean:write name="element" property="staticitem"/>"/>	
						 </td>
						 <td width="" align="center" class="RecordRow" nowrap>
					     <div id="<%="a_"+(i+1)+"_4"%>">
					     	<select name="method" onchange="changePos(this,<%=i%>);">
					     		<logic:equal name="element" property="method" value="0">
					     		<option value="0" selected>个数</option>
					     		<option value="1">百分比</option>
					     		</logic:equal>
					     		<logic:notEqual name="element" property="method" value="0">
					     		<option value="0">个数</option>
					     		<option value="1" selected>百分比</option>
					     		</logic:notEqual>
					     	</select>
					     </div>
						 </td>
						 <td width="" align="center" class="RecordRow" nowrap>
						    <div id="<%="a_"+(i+1)+"_5"%>">
						       <input type="text" class="textColorWrite" name="message" value="<bean:write name="element" property="message"/>"/>
						    </div>
						 </td>
						 <td width="" align="center" class="RecordRow" nowrap>
						     <div id="<%="a_"+(i+1)+"_6"%>">
						        <img src="/images/edit.gif" onclick="selectCtrlOrg('<%="a_"+(i+1)+"_6_0"%>');" style="cursor: hand;"/>
						        <input type="hidden" name="ctrlorg" id="<%="a_"+(i+1)+"_6_0"%>" value="<bean:write name="element" property="ctrlorg"/>"/>
						        <input type="hidden" name="nextorg" id="<%="a_"+(i+1)+"_6_0_1"%>" value="<bean:write name="element" property="nextorg"/>"/>
						    </div>
						 </td>
						 <td width="" align="center" class="RecordRow" nowrap>
					     <div id="<%="a_"+(i+1)+"_3"%>" align="center">
					     <logic:equal name="element" property="flag" value="1">
						 	<input type="checkbox" name="flag" value="0" checked/>
						 </logic:equal>
						 <logic:notEqual name="element" property="flag" value="1">
						 	<input type="checkbox" name="flag" value="0"/>
						 </logic:notEqual>
						 </div>
						 </td>
					</tr>
				
				<% i++; %>
				</logic:iterate>
		</table>
		<div id="d">
			&nbsp;&nbsp;&nbsp;
		</div>
	</fieldset>
	</td>
	</tr>
	<tr>
		<td align="center" style="padding-top:5px;">
			<hrms:priv func_id="2306202">
				<input type="button" class="mybutton" name="add" value="<bean:message key="button.insert"/>" onclick="addRow();"/>
			</hrms:priv>
			<hrms:priv func_id="2306203">
				<input type="button" class="mybutton" name="del" value="<bean:message key="button.delete"/>" onclick="deleterow()"/>
			</hrms:priv>
		</td>
	</tr>
<%} %>
	<tr>
    <td align="center" style="padding-top:5px;">
     <fieldset align="center" style="width:95%;">
         <legend ><bean:message key="pos.posparameter.ps_posworkout"/></legend>
          <table align="center" > 
           <tr>
              <td align="right"  nowrap>
     	       <bean:message key="pos.posparameter.ps_posfixedset"/>    	          
	    </td>   
	     <td align="left"  nowrap>
     	            <hrms:importgeneraldata showColumn="customdesc" valueColumn="fieldsetid" flag="true" paraValue="" 
                      sql="select fieldsetid,customdesc from fieldset where fieldsetid like 'K%' and fieldsetid<>'K01'" collection="list" scope="page"/>
                   <html:select name="posCodeParameterForm" property="zw_set" size="1" onchange="searchfiled()">
                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                     	        	        
           </tr>  
            <tr>
              <td align="right"  nowrap>
     	       <bean:message key="pos.posparameter.ps_posfixed"/>
	    </td>  
	    <td align="left"  nowrap>
    	          <hrms:importgeneraldata showColumn="itemdesc" valueColumn="itemid" flag="true" paraValue="" 
                      sql="select itemid,itemdesc from fielditem where useflag='1' and itemtype='N' and fieldsetid='${posCodeParameterForm.zw_set}'" collection="list" scope="page"/>
                   <html:select name="posCodeParameterForm" property="ps_workfixed" size="1">
                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                       	        	        
           </tr> 
            <tr>
              <td align="right"  nowrap>
     	       <bean:message key="pos.posparameter.ps_posexist"/>
	    </td>  
	    <td align="left"  nowrap>
                   <html:select name="posCodeParameterForm" property="ps_workexist" size="1">
                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                       	        	        
           </tr> 
         <logic:equal value="1" name="posCodeParameterForm" property="ps_parttime">
            <tr>
              <td align="right"  nowrap>
     	       岗位兼职人数
	    </td>  
	    <td align="left"  nowrap>
                   <html:select name="posCodeParameterForm" property="ps_workparttime" size="1">
                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                       	        	        
           </tr>  
           </logic:equal>   
          <tr>
              <td align="right"  nowrap>
     	       <bean:message key="pos.posparameter.whetherneedcontrolpost"/>
	    </td>  
	    <td align="left"  nowrap>
    	           <html:select name="posCodeParameterForm" property="zwvalid" size="1">
                       <html:option value="true"><bean:message key="kq.emp.change.yes"/></html:option>
                       <html:option value="false"><bean:message key="kq.emp.change.no"/></html:option>
                </html:select>
	    </td>                       	        	        
           </tr>  
       </table>
    </fieldset>  
    <br>
    </td>
   </tr>
   <tr height="5"><td>&nbsp;</td></tr>
</table>

<table width="80%" align="center" border="0" cellspacing="0" cellpadding="0" style="padding-top:5px;">
<tr>
<td align="center">
	<hrms:priv func_id="23062">
		<input type="button" class="mybutton" name="sav" value="<bean:message key="button.save"/>" onclick="beforeChange();save()"/>
	</hrms:priv>
<logic:equal name="posCodeParameterForm" property="org_flag" value="1">
	<input type="button" class="mybutton" name="sav" value="<bean:message key="button.close"/>" onclick="window.close();"/>
</logic:equal>
 <hrms:tipwizardbutton flag="org" target="il_body" formname="posCodeParameterForm"/> 

</td>
</tr>
</table>
<!-- </div> -->
<input type="hidden" name="planitem" value=""/>
<input type="hidden" name="realitem" value=""/>
<input type="hidden" name="staticitem" value=""/>
<input type="hidden" name="flagitem" value=""/>
<input type="hidden" name="methoditem" value=""/>
<input type="hidden" name="conditem" value=""/>
<input type="hidden" name="messitem" value=""/>
<input type="hidden" name="ctrlorgitem" value=""/>
<input type="hidden" name="nextorgitem" value=""/>
</html:form>
<script>
   count=parseInt("<%=i%>")+1;
</script>