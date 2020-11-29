<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/tabpane.css" type="text/css"></link>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="javascript" src="/js/codetree.js"></script>
<script language="JavaScript">        
function setCurrent(tab){
	var nodes,currnode;
	currnode=document.getElementById("current");
	if(currnode==null)
		  return;
	currnode.id="";
	nodes=tab.parent;
	if(nodes==null)
		  return;
	nodes.id="current";
		
}
      
/*
*树形菜单控制,功能菜单
*/
function show(div_id){
   var oDiv;
   oDiv=document.getElementById(div_id);
   if(oDiv==null)
      return;
    for(var i=0;i<oDiv.childNodes.length;i++){
      if(oDiv.childNodes[i].tagName=='DIV'){
      	if(oDiv.childNodes[i].style.display=="none"){
      		oDiv.childNodes[i].style.display="block";
      	}else{
      		oDiv.childNodes[i].style.display="none";	  
        }
     }
   }		
}
            
/**
*组装表权限或字段权限串，最后通过document.exportForm.field_set_str
*隐藏域传到后台．
*/
function combinePrivString(){
   var tablevos,thecontent,tmp,tablename,tabname;
   thecontent="";
   tabname=document.exportForm.current_tab.value;

   if(tabname=="dbpriv"){
   		tablevos=document.getElementsByTagName("input");
      	for(var i=0;i<tablevos.length;i++){
	     	if(tablevos[i].type=="checkbox"){
	     		if(!tablevos[i].checked){
	      			continue;
	      		}
	     		if(tablevos[i].value=="true"){
	      			continue;
	      		}
	      		if(tablevos[i].value=="false"){
	      			continue;
	      		}	
	     		thecontent +=tablevos[i].value+",";
			}
      	}
     	document.exportForm.field_set_str.value=thecontent; 
   }else if(tabname=="tablepriv"){
	  tablevos=document.getElementsByTagName("input");
	  for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	      	tmp=tablevos[i].value;
	      	if(tmp=="0")
	      		continue;
	      	if(!tablevos[i].checked)  
	      		continue;
	      	tmp=tmp+",";
	      	thecontent=thecontent+tmp;
	      }
	  }
      thecontent=","+thecontent;
      document.exportForm.field_set_str.value=thecontent; 
   }else if(tabname=="fieldpriv"){
      var constent="";
      var constent_show="";
      var constent_must="";
      var field_vo=eval("document.exportForm.func");
      for(var i=0;i<field_vo.length;i++){
      	if(field_vo[i].checked){    				
      		constent=constent+field_vo[i].value+',';
      	}
      }
      		
      document.exportForm.field_set_str.value=constent; 
   }
   var code = "false";
   if(exportForm.code.checked){
   		code = exportForm.code.value;
   }
   
   var field = "false";
   if(exportForm.field.checked){
   		field = exportForm.field.value;
   }
     var transcode = "false";
   if(exportForm.transcode.checked){
   		transcode = exportForm.transcode.value;
   }
   
   var strtoutf = "false";
   if(exportForm.strtoutf.checked){
   		strtoutf = exportForm.strtoutf.value;
   }
   document.exportForm.action="/system/export/export.do?b_save=save&a_tab="+tabname+"&code="+code+"&field="+field+"&transcode="+transcode+"&strtoutf="+strtoutf;
   document.exportForm.submit();
}   
function derived(){
   document.exportForm.action="/system/export/export.do?b_export=link";
   document.exportForm.submit();
}	
function allselect(){
   	var tablevos=document.getElementsByTagName("input");
   	for(var i=0;i<tablevos.length;i++){
	  	if(tablevos[i].type=="checkbox"){
	     	tablevos[i].checked = true;
	 	}
	}
}
function clearselect(){
	var tablevos=document.getElementsByTagName("input");
   	for(var i=0;i<tablevos.length;i++){
	  	if(tablevos[i].type=="checkbox"){
	     	tablevos[i].checked = false;
	 	}
	}
}	

function outputcode_ok(path){
	window.location.href="/servlet/OutputCode?path="+path;
}

function jindu(){
	var x=document.body.scrollLeft+event.clientX-180;
    var y=document.body.scrollTop+event.clientY-50; 
	var waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
	toggle("export");
}
function savejindu(){
	var x=document.body.scrollLeft+event.clientX-120;
    var y=document.body.scrollTop+event.clientY-50; 
	var waitInfo=eval("savewait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}		
function toggle(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		if (target.style.display == "none") {
			target.style.display = "";
		}else {
			target.style.display = "none";
		}
	}
} 					
</script>
<html:form action="/system/export/export">
<div id='savewait' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" style="border-collapse: collapse" bgcolor="#F7FAFF" height="87" align="center">
			<tr>
				<td bgcolor="#057AFC" style="font-size:12px;color:#ffffff" height=24>
					正在保存,请稍候...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee style="border:1px solid #000000" direction="right" width="180" scrollamount="5" scrolldelay="10" bgcolor="#ECF2FF">
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
<div id='wait' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					<bean:message key="sys.export.run"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="180" scrollamount="5" scrolldelay="10">
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
  <!--保存计算过的需要递交的子集或指标内容 -->
  <html:hidden name="exportForm" property="field_set_str"/>
  <html:hidden name="exportForm" property="current_tab"/>
  <html:hidden name="exportForm" property="org"/> 
  <br>
  <table width="85%"  border="0" cellpadding="1" cellspacing="1">
  <tr>
  	<td width="2%">&nbsp;</td>
  	<td>
  <table width="100%"  border="0" cellpadding="1" cellspacing="1" class="framestyle">
   <tr>
     <td>
         <div id="header">
          <ul>
            <li><a href="/system/export/export.do?b_query=link&a_tab=dbpriv" onclick="setCurrent(this);"><bean:message key="sys.export.main"/></a></li>            
            <li><a href="/system/export/export.do?b_query=link&a_tab=tablepriv" onclick="setCurrent(this);"><bean:message key="menu.table"/></a></li>
            <li><a href="/system/export/export.do?b_query=link&a_tab=fieldpriv" onclick="setCurrent(this);"><bean:message key="menu.field"/></a></li>
          </ul>
        </div>
      </td>
      <tr>
      	<td>	
            <bean:write  name="exportForm" property="script_str" filter="false"/>	 		 	      	  	 	      	    		
      	</td>
      </tr>
 </table>
  </td>
 </tr>
</table>
<table  width="80%">
  <tr>
	<td>&nbsp;&nbsp;
     <html:checkbox name="exportForm" property="code" value="true"><bean:message key="sys.export.code"/></html:checkbox> 
	 <html:checkbox name="exportForm" property="field" value="true"><bean:message key="sys.export.field"/></html:checkbox>
	 <html:checkbox name="exportForm" property="transcode" value="true"><bean:message key="sys.export.transcode"/></html:checkbox> 
	 <html:checkbox name="exportForm" property="strtoutf" value="true"><bean:message key="sys.export.strtoutf"/></html:checkbox>
	</td>
  </tr>          
</table> 
<table  width="80%" align="center">
   <tr>
     <td>&nbsp;</td>
  </tr>          
</table>
<table  width="80%" align="center">
          <tr>
            <td align="left">
                 <input type="button" name="all" class="mybutton" value="<bean:message key="label.query.selectall"/>" onClick="allselect()" >
	  			<input type="button" name="all" class="mybutton" value="<bean:message key="label.query.clearall"/>" onclick="clearselect()">
            	 		 	                            	 		 	        
	 			<Input type='button' value='<bean:message key="button.save" />'  onclick="savejindu();combinePrivString()"  class="mybutton" /> 
	 			<html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>
	 			<span id='export'>
	 				<Input type='button' value='<bean:message key="sys.export.derived"/>'  onclick="jindu();derived();"  class="mybutton" /> 	       
            	</span>
            	<bean:define id="dev_flag" name="exportForm" property="path"/>
				<logic:notEqual name="dev_flag" value="no" >
					<Input type='button' value='下载'  onclick="outputcode_ok('${dev_flag}');"  class="mybutton" /> 	       
				</logic:notEqual>       
            </td>
          </tr>          
</table>
</html:form>
