<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style>
<!--
.RecordRow_blt {
    border: inset 1px #C4D8EE;
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: 0pt; 
    BORDER-TOP: #C4D8EE 1pt solid;
    font-size: 12px;
    border-collapse:collapse; 
    height:22;
}
-->
</style>
<hrms:themes></hrms:themes>
<%
	UserView userView = (UserView)session.getAttribute(WebConstant.userView);
	String bosflag = userView.getBosflag();
%>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language=JavaScript>   
function setField(field_falg)
{
    var target_url="/general/deci/leader/param.do?b_setfeild=link`field_falg="+field_falg;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    var dw=540,dh=390,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    /*
    var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null)
    {
	  var in_obj=document.getElementById(field_falg);  
	  in_obj.innerHTML=return_vo.mess;
    }else
    {
	  var in_obj=document.getElementById(field_falg);  
    }
    */
    //改用ext 弹窗显示  wangb 20190318
    var win = Ext.create('Ext.window.Window',{
			id:'select_field',
			title:'请选择',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					 if(this.return_vo!=null)
    				 {
	  					 var in_obj=document.getElementById(field_falg);  
	  					 in_obj.innerHTML=this.return_vo.mess.replace(/<br>/,'');
    				 }else
    				 {
	  					 var in_obj=document.getElementById(field_falg);  
    				 }
				}
			}
	});
}
function setSname()
{
    var target_url="/general/deci/leader/param.do?b_sname=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var dw=540,dh=390,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    /*
    var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null)
    {
	  var in_obj=document.getElementById('gcond');  
	  in_obj.innerHTML=return_vo.mess;
    }else
    {
	  var in_obj=document.getElementById("gcond");  
    }
    */
    //改用ext 弹窗显示  wangb 20190319
	var win = Ext.create('Ext.window.Window',{
			id:'select_sname',
			title:'请选择',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo!=null)
    				{
	  					var in_obj=document.getElementById('gcond');  
	  					in_obj.innerHTML=this.return_vo.mess.replace(/<br>/,'');
    				}else
    				{
	  					var in_obj=document.getElementById("gcond");  
    				}
				}
			}
	}); 
}
function setRname()
{
    var target_url="/general/deci/leader/param.do?b_rname=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var dw=540,dh=390,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    /*
    var return_vo= window.showModalDialog(target_url,1, 
		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null)
    {
	  var in_obj=document.getElementById('card');  
	  in_obj.innerHTML=return_vo.mess;
    }else
    {
	  var in_obj=document.getElementById("card");  
    }
    */
    //改用ext 弹窗显示  wangb 20190319
	var win = Ext.create('Ext.window.Window',{
			id:'select_rname',
			title:'请选择',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo!=null)
    				{
	  					var in_obj=document.getElementById('card');  
	  					in_obj.innerHTML=this.return_vo.mess.replace(/<br>/,'');
    				}else
    				{
	  					var in_obj=document.getElementById("card");  
    				}
				}
			}
	}); 
}
function searchFieldList(idv)
{
           var setname="";
	   if(idv==="bz")
	      setname="bz_fieldsetid";
	   else if(idv=="hb")
	      setname="hb_fieldsetid";
	   else if(idv=="unit")
	      setname="unit_fieldsetid";
	   var tablename=$F(setname);		   
	   var hashvo=new ParameterSet();   
	   hashvo.setValue("tablename",tablename);
	   hashvo.setValue("idv",idv);
   	   var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'05603000010'},hashvo);   	   
}
function showFieldList(outparamters)
{
      var codesetlist=outparamters.getValue("codesetlist");
      var idv=outparamters.getValue("idv"); 
      var obj;
      if(idv==="bz")
        obj=leaderParamForm.bz_codesetid;
      else if(idv=="hb")
        obj=leaderParamForm.hb_codesetid; 
      else if(idv=="unit")
        obj=leaderParamForm.unit_codesetid;
      AjaxBind.bind(obj,codesetlist);	
      var tablename=outparamters.getValue("tablename");
      getSetnameValue(idv,tablename)
}
 function getSetnameValue(idv,tablename)
 {
     var setfiledname="";
     if(idv==="bz")
	setfiledname="bz_codesetid";
     else if(idv=="hb")
	setfiledname="hb_codesetid";
	else if(idv=="unit")
	setfiledname="unit_codesetid";
     var filedname=$F(setfiledname);
     var hashvo=new ParameterSet();
     hashvo.setValue("tablename",tablename);
     hashvo.setValue("filedname",filedname);
     hashvo.setValue("idv",idv);
     var request=new Request({method:'post',asynchronous:false,onSuccess:getCodeList,functionId:'05603000011'},hashvo);
 }
 function getCodeList(outparamters)
 {
      var codeitemlist=outparamters.getValue("codeitemlist");
      var idv=outparamters.getValue("idv");
      var obj;
      if(idv==="bz")
        obj=leaderParamForm.bz_codeitemid;
      else if(idv=="hb")
        obj=leaderParamForm.hb_codeitemid; 
      else if(idv=="unit")
        obj=leaderParamForm.unit_codeitemid;
      AjaxBind.bind(obj,codeitemlist);	
 }
 //取得指标代码
 function searchCodeList(idv)
 {
     var setname="";
     var setfiledname="";
     if(idv=="bz")
     {
       setname="bz_fieldsetid";
       setfiledname="bz_codesetid";
     }	     
     else if(idv=="hb")
     {
       setname="hb_fieldsetid";
       setfiledname="hb_codesetid";
     } 	
     else if (idv=="unit")
     {
     	setname="unit_fieldsetid";
     	setfiledname="unit_codesetid";
     }
     var tablename=$F(setname);	      
     var filedname=$F(setfiledname);
     var hashvo=new ParameterSet();
     hashvo.setValue("tablename",tablename);
     hashvo.setValue("filedname",filedname);
     hashvo.setValue("idv",idv);
     var request=new Request({method:'post',asynchronous:false,onSuccess:getCodeList,functionId:'05603000011'},hashvo);
	
 }
 function saveCode()
 {
    leaderParamForm.action="/general/deci/leader/param.do?b_save=link";
    leaderParamForm.submit();
 }
 
 function setDb(field_falg)
{ 
    var target_url="/general/deci/leader/param.do?b_setdb=link`field_falg="+field_falg;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    /*
    var return_vo= window.showModalDialog(iframe_url,null, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no;");
    if(return_vo!=null)
    {
	  var in_obj=document.getElementById(field_falg);  
	  in_obj.innerHTML=return_vo.mess;
    }else
    {
	  var in_obj=document.getElementById(field_falg);  
    }
    */
    //改用ext 弹窗显示  wangb 20190318
    var win = Ext.create('Ext.window.Window',{
			id:'select_db',
			title:'请选择',
			width:360,
			height:300,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" resizable=no center=yes scroll=yes status=no frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo!=null)
    				{
	  					var in_obj=document.getElementById(field_falg);  
	  					in_obj.innerHTML='&nbsp;'+this.return_vo.mess+'&nbsp;';
    				}else
    				{
	  					var in_obj=document.getElementById(field_falg);  
    				}
				}
			}
	});
}
 function setLoadtype()
{
    var target_url="/general/deci/leader/param.do?b_setloadtype=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var return_vo= window.showModalDialog(iframe_url,null, 
        "dialogWidth:340px; dialogHeight:290px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
    if(return_vo!=null)
    {
	  var in_obj=document.getElementById('loadtype');  
	  in_obj.innerHTML=return_vo.mess;
    }else
    {
	  var in_obj=document.getElementById('loadtype');  
    }
    
}
 function setLoadtypes()
{
    //alert(document.getElementById('load').value);
    var hashvo=new ParameterSet();
    var id=document.getElementById('load').value;
    hashvo.setValue("id",id);
	var request=new Request({method:'post',onSuccess:null,functionId:'05603000022'},hashvo);
}
</script> 
<html:form action="/general/deci/leader/param"><!-- 系统管理，参数设置，hr页面，内容与顶部之间间隔10px   jingq  add 2014.12.23 -->
<table width="1000px" border="0" cellspacing="0"   align="center" cellpadding="0" class="ListTable common_border_color" style="BORDER-RIGHT: #C4D8EE 1pt solid" <%if(!"hcm".equals(bosflag)){ %>style="margin-top:10px;"<%} %>>
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="6">
		<bean:message key="leaderteam.leaderframe.leaderteam"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>

   	   <td width="10%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="leaderteam.leaderparam.teamidentifier"/>&nbsp;
   	   </td>
   	   <td width="60%" class="RecordRow" colspan="2" nowrap>
   	      <hrms:optioncollection name="leaderParamForm" property="user_field_list" collection="list" />
	      &nbsp;<html:select name="leaderParamForm" property="bz_fieldsetid" styleId="bz_fieldsetid"  onchange="searchFieldList('bz');">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select> 
             ->
              <hrms:optioncollection name="leaderParamForm" property="bz_codesetlist" collection="list" />
	      <html:select name="leaderParamForm" property="bz_codesetid" styleId="bz_codesetid" onchange="searchCodeList('bz');">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select>
			->
			<hrms:optioncollection name="leaderParamForm" property="bz_codeitemlist" collection="list" />
	      <html:select name="leaderParamForm" property="bz_codeitemid" styleId="bz_codeitemid">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select> &nbsp;
           </td>
           <td width="20%" class="RecordRow" id="bz">
           		&nbsp;<bean:write  name="leaderParamForm" property="bz_mess" filter="false"/>&nbsp;
           </td>
           <td width="10%" class="RecordRow_lt" >
           	<input type="button" name="btnreturn" value='<bean:message key="leaderteam.leaderparam.dbsetting"/>' class="mybutton"  onclick="setDb('bz');">
           </td>
          </tr>
        <tr>
   	   <td width="10%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="leaderteam.leaderparam.insupportcadreidentifier"/>&nbsp;
   	   </td>
   	   <td width="60%" class="RecordRow" colspan="2">
   	      <hrms:optioncollection name="leaderParamForm" property="user_field_list" collection="list" />
	      &nbsp;<html:select name="leaderParamForm" property="hb_fieldsetid"  onchange="searchFieldList('hb');">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select> 
              ->
              <hrms:optioncollection name="leaderParamForm" property="hb_codesetlist" collection="list" />
	      <html:select name="leaderParamForm" property="hb_codesetid" onchange="searchCodeList('hb');">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select> 
              ->
            <hrms:optioncollection name="leaderParamForm" property="hb_codeitemlist" collection="list" />
	      <html:select name="leaderParamForm" property="hb_codeitemid">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select> 
           </td>
           <td width="20%" class="RecordRow" id="hb">
           		&nbsp;<bean:write  name="leaderParamForm" property="hb_mess" filter="false"/>&nbsp;
           </td>
           <td width="10%" class="RecordRow_lt" >
           	<input type="button" name="btnreturn" value='<bean:message key="leaderteam.leaderparam.dbsetting"/>' class="mybutton"  onclick="setDb('hb');">
           </td>
          </tr>
          <tr>
   	   <td width="20%" class="RecordRow" height="35" align="right">
   	     <bean:message key="leaderteam.leaderparam.organizationdisplay"/>&nbsp;
   	   </td>
   	   <td width="30%" class="RecordRow_lt" id="loadtype" colspan="4">
		   &nbsp;<html:select styleId="load" name="leaderParamForm" property="loadtype_mess" size="1" onchange="setLoadtypes();">
		    <html:option value="1"><bean:message key="leaderteam.leaderparam.unitanddepartment"/></html:option>
		    <html:option value="2"><bean:message key="leaderteam.leaderparam.unit"/></html:option>
		   </html:select>
       </td>   
   	        
          </tr>     
<!--         
			<tr>
   	   <td width="20%" class="RecordRow" height="35" align="center">
   	     单位子集表
   	   </td>
   	   <td width="20%" class="RecordRowC">
   	      <hrms:optioncollection name="leaderParamForm" property="unit_field_list" collection="list" />
	      <html:select name="leaderParamForm" property="unit_fieldsetid"   onchange="searchFieldList('unit');">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select> 
           </td>
           <td width="20%" class="RecordRowC">
              <hrms:optioncollection name="leaderParamForm" property="unit_codesetlist" collection="list" />
	      <html:select name="leaderParamForm" property="unit_codesetid"  onchange="searchCodeList('unit');">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select> 
           </td>
           <td width="20%" class="RecordRowC">
            <hrms:optioncollection name="leaderParamForm" property="unit_codeitemlist" collection="list" />
	      <html:select name="leaderParamForm" property="unit_codeitemid" >
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select> 
         <td width="20%" class="RecordRowC" colspan="2">
          </tr>
-->               
         <tr>
   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="leaderteam.leaderparam.basicinstanceidentifier"/>&nbsp;
   	   </td>
   	   <td width="70%" class="RecordRow" id="output" colspan="3">
   	       <bean:write  name="leaderParamForm" property="output_mess" filter="false"/>
           </td>     
           <td width="20%" class="RecordRow_lt" colspan="1">
   	       &nbsp;<input type="button" name="btnreturn" value='<bean:message key="button.orgmapset"/>' class="mybutton" onclick="setField('output');">
           </td>       
          </tr>
          <tr>
   	   <td width="10%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="leaderteam.leaderparam.browserdisplaytarget"/>&nbsp;
   	   </td>
   	   <td width="70%" class="RecordRow" id="display" colspan="3">
   	     <bean:write  name="leaderParamForm" property="display_mess" filter="false"/>
           </td>     
           <td width="20%" class="RecordRow_lt" colspan="1">
   	       &nbsp;<input type="button" name="btnreturn" value='<bean:message key="button.orgmapset"/>' class="mybutton" onclick="setField('display');">
           </td>       
          </tr> 
          <tr>
   	   <td width="10%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="condi.leaderparam.browserdisplaytarget"/>&nbsp;
   	   </td>
   	   <td width="70%" class="RecordRow" id="condi_display" colspan="3">
   	     <bean:write  name="leaderParamForm" property="condi_display_mess" filter="false"/>
           </td>     
           <td width="20%" class="RecordRow_lt" colspan="1">
   	       &nbsp;<input type="button" name="btnreturn" value='<bean:message key="button.orgmapset"/>' class="mybutton" onclick="setField('condi_display');">
           </td>       
          </tr>  
          <tr>
   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="leaderteam.leaderparam.teamanalyseterm"/>&nbsp;
   	   </td>
   	   <td width="70%" class="RecordRow" id="gcond" colspan="3">
   	     <bean:write  name="leaderParamForm" property="gcond_mess" filter="false"/>
           </td>     
           <td width="10%" class="RecordRow_lt" colspan="1">
   	       &nbsp;<input type="button" name="btnreturn" value='<bean:message key="button.orgmapset"/>' class="mybutton" onclick="setSname();">
           </td>       
          </tr> 
           <tr>
   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="leaderteam.leaderparam.unitnumbertable"/>&nbsp;
   	   </td>
   	   <td width="70%" class="RecordRow" id="card" colspan="3">
   	      <bean:write  name="leaderParamForm" property="unit_mess" filter="false"/>
           </td>     
           <td width="10%" class="RecordRow_lt" colspan="1">
   	       &nbsp;<input type="button" name="btnreturn" value='<bean:message key="button.orgmapset"/>' class="mybutton" onclick="setRname();">
           </td>       
          </tr>
          <tr height="35">

   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="leaderteam.leaderparam.unitnumbersubset"/>&nbsp;
   	   </td>
   	   <td width="70%" class="RecordRow" id="unitfile" colspan="3">
   	       <bean:write  name="leaderParamForm" property="unitfile_mess" filter="false"/>
           </td>     
           <td width="10%" class="RecordRow_lt" colspan="1">
   	       &nbsp;<input type="button" name="btnreturn" value='<bean:message key="button.orgmapset"/>' class="mybutton" onclick="setField('unitfile');">
           </td>       
          </tr>
          <!-- 
           <tr>
   	   <td width="20%" class="RecordRow" height="35" align="center">
   	     组织结构树显示<bean:message key="button.orgmapset"/>
   	   </td>
   	   <td width="40%" class="RecordRow" id="loadtype" colspan="4">
   	      <bean:write  name="leaderParamForm" property="loadtype_mess" filter="false"/>
           </td>     
           <td width="40%" class="RecordRow" colspan="1">
   	       <input type="button" name="btnreturn" value='<bean:message key="button.orgmapset"/>' class="mybutton" onclick="setLoadtype();">
           </td>       
          </tr>
          -->
        <tr>
          <td align="center" class="RecordRow_blt common_border_color" nowrap style="height: 35px" colspan="5">
          <input type="button" name="btnreturn" value='<bean:message key="button.save"/>' class="mybutton" onclick=" saveCode();">
          </td>
          </tr>   
</table>
</html:form>
<script>
if(!getBrowseVersion()){// 非ie浏览器 样式修改  wangb 20190319
	var table1 = document.getElementsByTagName('table')[2];
	var trs = table1.getElementsByTagName('tr');
	for(var i = 1 ; i < trs.length; i ++){
		var tds = trs[i].getElementsByTagName('td');
		tds[tds.length-1].style.borderRight='#C4D8EE 1pt solid';
	}
}

</script>
