<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String portalid = request.getParameter("portalid");
	int i=0;
	
	//能闪烁、滚动的面板 
	String sysid=""; //兼容60锁，60锁隐藏首页待办与我的申请显示我的任务面板
	if(userView.getVersion()<70){
		sysid="0221,0222,0223,0224,0231,0232,0233,0110";
	}else{
		sysid="0221,0222,0223,0224,0231,0232,0233,0234,0110";
	}
 %>
<SCRIPT language="javascript" type="text/javascript" >
  function refreshWarnN()
  {
      var waitInfo=eval("wait");	   
      waitInfo.style.display="block";
      portalTailorForm.action="/system/options/portaltailor.do?b_refresh=link&portalid=<%=portalid %>";
      portalTailorForm.submit();
  }
   function refreshWarn()
  {
      var waitInfo=eval("wait");	   
      waitInfo.style.display="block";
      portalTailorForm.action="/system/options/portaltailor.do?b_refresh=link";
      portalTailorForm.submit();
  }
  function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }
   
   
   
   function initvalue(id,obj){
   		alert(id);
   		var o=document.getElementById(id);
   		alert(o.value);
   		if(obj.checked){
   			o.value='1';
   		}else{
   			o.value='0';
   		}
   		alert(o.value);
   }
</script>
<html:form action="/system/options/portaltailor">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:6px;">
          <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		&nbsp;<bean:message key="system.options.portalitemname"/>&nbsp;
            </td>   
             <td align="center" class="TableRow" nowrap>
		<bean:message key="system.options.twinkle"/>
            </td> 
             <td align="center" class="TableRow" nowrap>
		<bean:message key="system.options.scroll"/>
            </td> 
             <td align="center" class="TableRow" nowrap>
		<bean:message key="system.options.portalitemshow"/>
            </td>              	        	        	        
           </tr>
   	  </thead>
   	  <html:hidden name='portalTailorForm' property="checkvalues"/> 
<logic:iterate name="portalTailorForm" property="showitem" id="item" indexId="index"> 
 <%if(portalid!=null&&!"".equals(portalid)){ 
 		i++;
 		%>
 	 <tr>
     <td align="left" class="RecordRow" nowrap>
     	<bean:define id="itemname" name="item" property="name"></bean:define>
     	<bean:define id="itemid" name="item" property="id"></bean:define>
		&nbsp;<bean:write name="item" property="name"/>&nbsp;
		<%
			String itemn = (String)pageContext.getAttribute("itemname");
			if(itemn.indexOf("预警")!=-1){
		 %>
		 		<hrms:priv func_id="301201">
			       <a href="javascript:refreshWarnN();">
			         <img src="/images/refresh.gif" border=0 title="刷新业务预警">
			       </a>
			  </hrms:priv>
		 <%} %>
     </td>   
      <td align="center" class="RecordRow" nowrap>
         <%
			String itemi = (String)pageContext.getAttribute("itemid");
			if(sysid.indexOf(itemi)!=-1){
		 %>
			<html:checkbox name="item" property="twinkle" value="1" styleId="twinkle${index }"/>
		<%}else{ %>
			<html:checkbox name="item" property="twinkle" value="1" styleId="twinkle${index }" disabled="true"/>
		<%} %>
     </td> 
      <td align="center" class="RecordRow" nowrap>
       <%
			if(sysid.indexOf(itemi)!=-1){
		 %>
		    <html:checkbox name="item" property="scroll" value="1" styleId="scroll${index }"/>
		<%}else{ %>
			<html:checkbox name="item" property="scroll" value="1" styleId="scroll${index }" disabled="true"/>
		<%} %>
     </td> 
      <td align="center" class="RecordRow" nowrap>
	    <html:checkbox name="item" property="show" value="1" styleId="show${index }"/>
     </td>              	        	        	        
    </tr> 
     
 <%}else{ %>
 <logic:equal name="item" property="itemid" value="1">     
 <tr>
     <td align="left" class="RecordRow" nowrap>
	&nbsp;<bean:message key="system.options.itembulletin"/>&nbsp;
     </td>   
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=bulletintwinkle onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="bulletintwinkle">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="bulletintwinkle" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=bulletinscroll onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="bulletinscroll">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="bulletinscroll" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=bulletinshow onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="bulletinshow">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="bulletinshow" />
     </td>              	        	        	        
    </tr>  
 </logic:equal>
 <logic:equal name="item" property="itemid" value="2">
    <tr>
     <td align="left" class="RecordRow" nowrap>
	&nbsp;<bean:message key="system.options.itemwarn"/>&nbsp;&nbsp;
	  <hrms:priv func_id="301201">
	       <a href="javascript:refreshWarn();">
	         <img src="/images/refresh.gif" border=0 title="刷新业务预警">
	       </a>
	  </hrms:priv>
	    
     </td>   
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=warntwinkle onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="warntwinkle">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="warntwinkle" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=warnscroll onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="warnscroll">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="warnscroll" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=warnshow  onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="warnshow">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="warnshow" />
     </td>              	        	        	        
    </tr>  
 </logic:equal>                                              
 <logic:equal name="item" property="itemid" value="3">
    <tr>
     <td align="left" class="RecordRow" nowrap>
	&nbsp;<bean:message key="system.options.itemmuster"/>&nbsp;&nbsp;
     </td>   
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=mustertwinkle onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="mustertwinkle">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="mustertwinkle" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=musterscroll onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="musterscroll">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="musterscroll" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=mustershow onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="mustershow">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="mustershow" />
     </td>              	        	        	        
    </tr>  
     </logic:equal>                                              
     <logic:equal name="item" property="itemid" value="4"><tr>
     <td align="left" class="RecordRow" nowrap>
	&nbsp;<bean:message key="system.options.itemquery"/>&nbsp;&nbsp;
     </td>   
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=querytwinkle onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="querytwinkle">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="querytwinkle" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=queryscroll onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="queryscroll">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="queryscroll" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=queryshow onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="queryshow">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="queryshow" />
     </td>              	        	        	        
    </tr> 
     </logic:equal>                                               
     <logic:equal name="item" property="itemid" value="5">
     <tr>
     <td align="left" class="RecordRow" nowrap>
	&nbsp;<bean:message key="system.options.itemstat"/>&nbsp;&nbsp;
     </td>   
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=stattwinkle  onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="stattwinkle">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="stattwinkle" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=statscroll onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="statscroll">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="statscroll" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=statshow onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="statshow">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="statshow" />
     </td>              	        	        	        
    </tr>
     </logic:equal>                                             
     <logic:equal name="item" property="itemid" value="6">
     <tr>
     <td align="left" class="RecordRow" nowrap>
	&nbsp;<bean:message key="system.options.itemcard"/>&nbsp;&nbsp;
     </td>   
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=cardtwinkle onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="cardtwinkle">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="cardtwinkle" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=cardscroll onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="cardscroll">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="cardscroll" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=cardshow onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="cardshow">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="cardshow" />
     </td>              	        	        	        
    </tr>    
     </logic:equal>                                       
     <logic:equal name="item" property="itemid" value="7">
     <tr>
     <td align="left" class="RecordRow" nowrap>
	&nbsp;<bean:message key="system.options.itemreport"/>&nbsp;&nbsp;
     </td>   
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=reporttwinkle  onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="reporttwinkle">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="reporttwinkle" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=reportscroll onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="reportscroll">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="reportscroll" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=reportshow onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="reportshow">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="reportshow" />
     </td>              	        	        	        
    </tr>  
  </logic:equal>                                       
     <logic:equal name="item" property="itemid" value="8">
     <tr>
     <td align="left" class="RecordRow" nowrap>
	&nbsp;<bean:message key="system.options.itemmatter"/>&nbsp;&nbsp;
     </td>   
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=mattertwinkle onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="mattertwinkle">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="mattertwinkle" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=matterscroll onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="matterscroll">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="matterscroll" />
     </td> 
      <td align="center" class="RecordRow" nowrap>
      <input type="checkbox" value=mattershow  onclick="doclick(this);" <logic:equal value="1" name="portalTailorForm" property="mattershow">checked</logic:equal> />
	<html:hidden name="portalTailorForm" property="mattershow" />
     </td>              	        	        	        
    </tr>  
  </logic:equal> 
  
  <%} %>
</logic:iterate>   
	  <%if(portalid!=null&&!"".equals(portalid)){ %>
 <tr class="list3">
          <td align="center" colspan="4" style="height: 35">
          	<html:button property="b_save" styleClass="mybutton" onclick="save();"><bean:message key="button.save"/></html:button>
         	<html:button property="b_save" styleClass="mybutton" style="margin:0px;padding:0px 10px;" onclick="setbusidate();"><bean:message key="menu.gz.appdate"/></html:button>
         </td>
       </tr> 
 <%}else{ %>
       <tr class="list3">
          <td align="center" colspan="4" style="height: 35">
  	    <hrms:submit styleClass="mybutton" property="b_save">
      	  	<bean:message key="button.save"/>
	    </hrms:submit>
            <hrms:submit styleClass="mybutton" property="b_adjust">
     	 	<bean:message key="button.itemadjust"/>
	    </hrms:submit> 
	    <html:button property="b_save" styleClass="mybutton" onclick="setbusidate();"><bean:message key="menu.gz.appdate"/></html:button>
         </td>
       </tr> 
 <%} %> 	         
      </table>
</html:form>
<div id='wait' style='position:absolute;top:200;left:400;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style"  height="87" align="center">
           <tr>

             <td class="td_style" height=24><bean:message key="classdata.isnow.wiat"/></td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>
<script language="javascript">
 MusterInitData();
 
 function doclick(obj){
 	if(obj.checked)
 		document.getElementsByName(obj.value)[0].value='1';
 	else
 		document.getElementsByName(obj.value)[0].value='0';
 
 }
 function save(){
   		var i='<%=i %>'
   		var v='';
   		for(var n=0;n<i;n++){
   			var o=document.getElementById('twinkle'+n);
	   		if(o.checked){
	   			v+='1,';
	   		}else{
	   			v+='0,';
	   		}
	   		o=document.getElementById('scroll'+n);
	   		if(o.checked){
	   			v+='1,';
	   		}else{
	   			v+='0,';
	   		}
	   		o=document.getElementById('show'+n);
	   		if(o.checked){
	   			v+='1,';
	   		}else{
	   			v+='0,';
	   		}
	   		v=v.substring(0,v.length-1);
	   		v+='`';
   		}
   		v=v.substring(0,v.length-1);
   		//alert(v);
   		document.getElementsByName('checkvalues')[0].value=v;
   		portalTailorForm.action="/system/options/portaltailor.do?b_save=link&portalid=<%=portalid %>";
        portalTailorForm.submit();
   }
   
   function setbusidate(){
   		var thecodeurl ='/gz/gz_accounting/setapp_date.do?b_query=link'; 
   		var dw=300,dh=350,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
   		var return_vo= window.showModalDialog(thecodeurl, '', 'dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no');
   		if(return_vo)
   			alert("业务日期设置成功！");
   }
</script>