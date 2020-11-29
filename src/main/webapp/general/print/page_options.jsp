<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/page_options_color.js"></SCRIPT> 
<SCRIPT LANGUAGE=javascript src="/js/page_options.js"></SCRIPT> 
<script language=JavaScript> 
var selectElement="";
if(isCompatibleIE())
	document.attachEvent('onclick', function(e){closeSelect(e)});
else
	document.addEventListener('click', function(e){closeSelect(e)});
function closeSelect(evt) {
	var clickId = (evt.target)?evt.target.id:evt.srcElement.id;
	if ("object_panel1" != clickId && "object_panel2" != clickId&&selectElement!=clickId){
		Element.hide("object_panel1");
		Element.hide("object_panel2");
	}
}
Object.extend(Element, {
	hide:function(id){
		var element = document.getElementById(id);
		if(element)
			element.style.display = 'none'
	},
	show:function(id){
		var element = document.getElementById(id);
		if(element)
			element.style.display = ''
	}
});
function IsDigit() 
{ 
  return ((event.keyCode >= 46) && (event.keyCode <= 57)&&(event.keyCode!=47)); 
} 
function IsDigit2() 
{ 
  return ((event.keyCode >= 46) && (event.keyCode <= 57)&&(event.keyCode!=47)); 
} 
function show()
{
	var bb=eval("b");
	bb.style.display="block";
}
function closes()
{
	var bb=eval("b");
	bb.style.display="none"; 

}
function SetDownMsg(s) {
   var pageMode, w_str,h_str;
   pageMode = s.options[s.selectedIndex].value;
   
   if (pageMode == "A3") 
   {
     document.pageOptionsForm.widthP.readOnly=true;
     document.pageOptionsForm.heightP.readOnly=true;
     w_str="297";
     h_str="420";
   }
   if (pageMode == "A4") 
   {
     document.pageOptionsForm.widthP.readOnly=true;
     document.pageOptionsForm.heightP.readOnly=true;
     w_str="210";
     h_str="297";
   }
   if (pageMode == "A5") 
   {   
     document.pageOptionsForm.widthP.readOnly=true;
     document.pageOptionsForm.heightP.readOnly=true; 
     w_str="148";
     h_str="210";
   }
   if (pageMode == "B5") 
   {
     document.pageOptionsForm.widthP.readOnly=true;
     document.pageOptionsForm.heightP.readOnly=true;
     w_str="182";
     h_str="257";
   }
   if (pageMode == "16开") 
   {
     document.pageOptionsForm.widthP.readOnly=true;
     document.pageOptionsForm.heightP.readOnly=true;
     w_str="184";
     h_str="260";
   }
   if (pageMode == "32开") 
   {
     document.pageOptionsForm.widthP.readOnly=true;
     document.pageOptionsForm.heightP.readOnly=true;
     w_str="130";
     h_str="184";
   }   
   if (pageMode == "self") 
   {
     document.pageOptionsForm.widthP.readOnly=false;
     document.pageOptionsForm.heightP.readOnly=false;
     w_str="";
     h_str="";
   }
   if (s.selectedIndex != 0)
   {
     
     document.pageOptionsForm.widthP.value = w_str;
     document.pageOptionsForm.heightP.value = h_str;
   }else
   {
     document.pageOptionsForm.widthP.readOnly=false;
     document.pageOptionsForm.heightP.readOnly=false;
     document.pageOptionsForm.widthP.value = "";
     document.pageOptionsForm.heightP.value = "";
   }
   return 1;
}
</script> 
<script language=JavaScript>     
    var iscorrect=true;
   function greateXML(state,tid,flag)
   {  	  
     var width_str=$F('parsevo.width');
     var hieght_str=$F('parsevo.height');
     if(width_str=="")
     {
        width_str=210;
     }
     if(hieght_str=="")
     {
       hieght_str=297;
     }
     var l_width=parseInt($F('parsevo.left'));
     var r_width=parseInt($F('parsevo.right'));
     var rl_w=l_width+r_width;
     var t_h=parseInt($F('parsevo.top'));
     var b_h=parseInt($F('parsevo.bottom'));
     var tb_h=t_h+b_h;
     if('init'!=flag){
	     if(parseInt(width_str)<50)
	     {
	        alert("页面设置页面的宽不能小于50！");
	        return false;
	     }
	     if(parseInt(hieght_str)<70)
	     {
	        alert("页面设置页面的高不能小于70！");
	        return false;
	     }
	     if(rl_w>=parseInt(width_str))
	     {
	        alert("页面设置左右页面边距不能大于页面的宽！");
	        return false;
	     }else if(tb_h>=parseInt(hieght_str))
	     {
	        alert("页面设置上下页面边距不能大于页面的高！");
	        return false;
	     }  
     }
     
     var hashvo=new ParameterSet(); 
     hashvo.setValue("state",state);  
     hashvo.setValue("flag",flag);  
     hashvo.setValue("id",tid);  
     hashvo.setValue("pagetype",$F('parsevo.pagetype')); 
     hashvo.setValue("orientation",$F('parsevo.orientation'));    

     hashvo.setValue("top",$F('parsevo.top'));
     hashvo.setValue("bottom",$F('parsevo.bottom'));
     hashvo.setValue("left",$F('parsevo.left'));
     hashvo.setValue("right",$F('parsevo.right'));
     hashvo.setValue("width",$F('parsevo.width'));
     hashvo.setValue("height",$F('parsevo.height'));
     
               
     hashvo.setValue("title_fb",$F('parsevo.title_fb'));
     hashvo.setValue("title_fi",$F('parsevo.title_fi'));
     hashvo.setValue("title_fu",$F('parsevo.title_fu'));
     hashvo.setValue("title_fn",$F('parsevo.title_fn'));
     hashvo.setValue("title_fz",$F('parsevo.title_fz')); 
     hashvo.setValue("title_fw",$F('parsevo.title_fw'));      
      if($F('parsevo.title_fw').length>50)
      {
          alert("标题内容的字数不能超过50个字!");
          return false;
      }else if($F('title_fw').indexOf("\"")!=-1)
      {
            alert("标题内容不能包含双引号!");
            return false;
      }
     hashvo.setValue("head_fb",$F('parsevo.head_fb')); 
     hashvo.setValue("head_fi",$F('parsevo.head_fi'));
     hashvo.setValue("head_fu",$F('parsevo.head_fu'));
     hashvo.setValue("head_fn",$F('parsevo.head_fn'));
     hashvo.setValue("head_fz",$F('parsevo.head_fz'));
              
     
     hashvo.setValue("tile_fb",$F('parsevo.tile_fb'));
     hashvo.setValue("tile_fi",$F('parsevo.tile_fi'));
     hashvo.setValue("tile_fu",$F('parsevo.tile_fu'));
     hashvo.setValue("tile_fn",$F('parsevo.tile_fn'));
     hashvo.setValue("tile_fz",$F('parsevo.tile_fz'));
     
     hashvo.setValue("body_fb",$F('parsevo.body_fb'));
     hashvo.setValue("body_fi",$F('parsevo.body_fi'));
     hashvo.setValue("body_fu",$F('parsevo.body_fu'));
     hashvo.setValue("body_fn",$F('parsevo.body_fn'));
     hashvo.setValue("body_fz",$F('parsevo.body_fz'));        
     if(state=="0")
     {
   
       if($F('parsevo.tile_fw').length>50)
       {
          alert("表尾内容的字数不能超过50个字!");
          return false;
       }else if($F('parsevo.head_fw').length>50)
       {
          alert("表头内容的字数不能超过50个字!");
          return false;
       }
       if($F('parsevo.tile_fw').indexOf("\"")!=-1)
       {
            alert("表尾内容不能包含双引号!");
            return false;
       }else if($F('parsevo.head_fw').indexOf("\"")!=-1)
       {
            alert("表头内容不能包含双引号!");
            return false;
       }
       hashvo.setValue("title_h",$F('parsevo.title_h'));
       hashvo.setValue("unit",$F('parsevo.unit')); 
       hashvo.setValue("head_p",$F('parsevo.head_p'));
       hashvo.setValue("head_c",$F('parsevo.head_c'));
       hashvo.setValue("head_e",$F('parsevo.head_e'));
       hashvo.setValue("head_u",$F('parsevo.head_u'));
       hashvo.setValue("head_d",$F('parsevo.head_d')); 
       hashvo.setValue("head_t",$F('parsevo.head_t'));
       hashvo.setValue("head_h",$F('parsevo.head_h')); 
       hashvo.setValue("tile_p",$F('parsevo.tile_p'));
       hashvo.setValue("tile_c",$F('parsevo.tile_c'));
       hashvo.setValue("tile_e",$F('parsevo.tile_e'));
       hashvo.setValue("tile_u",$F('parsevo.tile_u'));
       hashvo.setValue("tile_d",$F('parsevo.tile_d'));
       hashvo.setValue("tile_t",$F('parsevo.tile_t')); 
       hashvo.setValue("tile_h",$F('parsevo.tile_h'));  
       hashvo.setValue("tile_fw",$F('parsevo.tile_fw'));       
       hashvo.setValue("body_pr",$F('parsevo.body_pr'));
       hashvo.setValue("body_rn",$F('parsevo.body_rn'));
       hashvo.setValue("head_fw",$F('parsevo.head_fw'));       
     
     }else
     {
         if($F('parsevo.head_flw').length>50)
         {
            alert("表头上左内容的字数不能超过50个字!");
            return false;
         }else if($F('parsevo.head_fmw').length>50)
         {
            alert("表头上中内容的字数不能超过50个字!");
            return false;
         }else if($F('parsevo.head_frw').length>50)
         {
            alert("表头上右内容的字数不能超过50个字!");
            return false;
         }else if($F('parsevo.tile_flw').length>50)
         {
            alert("表尾下左内容的字数不能超过50个字!");
            return false;
         }else if($F('parsevo.tile_fmw').length>50)
         {
            alert("表尾下中内容的字数不能超过50个字!");
            return false;
         }else if($F('parsevo.tile_frw').length>50)
         {
            alert("表尾右中内容的字数不能超过50个字!");
            return false;
         }  
         //
         if($F('parsevo.head_flw').indexOf("\"")!=-1)
         {
            alert("表头上左内容不能包含双引号!");
            return false;
         }else if($F('parsevo.head_fmw').indexOf("\"")!=-1)
         {
            alert("表头上中内容不能包含双引号!");
            return false;
         }else if($F('parsevo.head_frw').indexOf("\"")!=-1)
         {
            alert("表头上右内容不能包含双引号!");
            return false;
         }else if($F('parsevo.tile_flw').indexOf("\"")!=-1)
         {
            alert("表尾下左内容不能包含双引号!");
            return false;
         }else if($F('parsevo.tile_fmw').indexOf("\"")!=-1)
         {
            alert("表尾下中内容不能包含双引号!");
            return false;
         }else if($F('parsevo.tile_frw').indexOf("\"")!=-1)
         {
            alert("表尾右中内容不能包含双引号!");
            return false;
         }
       hashvo.setValue("title_fs",$F('parsevo.title_fs'));  
       hashvo.setValue("head_fs",$F('parsevo.head_fs')); 
       hashvo.setValue("tile_fs",$F('parsevo.tile_fs'));
       hashvo.setValue("title_fc",$F('parsevo.title_fc'));
       hashvo.setValue("tile_flw",$F('parsevo.tile_flw'));       
       hashvo.setValue("tile_fmw",$F('parsevo.tile_fmw'));       
       hashvo.setValue("tile_frw",$F('parsevo.tile_frw'));       
       hashvo.setValue("tile_fc",$F('parsevo.tile_fc'));
       hashvo.setValue("body_fc",$F('parsevo.body_fc'));
       hashvo.setValue("head_flw",$F('parsevo.head_flw'));
       hashvo.setValue("head_fmw",$F('parsevo.head_fmw'));
       hashvo.setValue("head_frw",$F('parsevo.head_frw'));
       
       hashvo.setValue("head_fc",$F('parsevo.head_fc'));
       hashvo.setValue("rsid","${pageOptionsForm.rsid}");
       hashvo.setValue("rsdtlid","${pageOptionsForm.rsdtlid}");
       if(state=='4')
       {
          hashvo.setValue("thead_fb",$F('parsevo.thead_fb'));
          hashvo.setValue("thead_fi",$F('parsevo.thead_fi'));
          hashvo.setValue("thead_fu",$F('parsevo.thead_fu'));
          hashvo.setValue("thead_fn",$F('parsevo.thead_fn'));
          hashvo.setValue("thead_fz",$F('parsevo.thead_fz'));  
          hashvo.setValue("thead_fc",$F('parsevo.thead_fc'));             
       }
     } 
     if(!iscorrect)
     {
       return false;
     }     
     var str="是否确认保存？";
     if(flag=="init")
        str="是否初始化设置？如果是则原有设置将被清除！";
     if(confirm(str))
     {
    	 
        var request=new Request({method:'post',onSuccess:showSelect,functionId:'15391110001'},hashvo);
     }  
   }
  function showSelect(outparamters)
  { 
	  for (var i=0;i< outparamters._parameters.length;i++){
		  if("init"==outparamters._parameters[i].value){
			  window.location.reload();
		  }
	  }
     var types=outparamters.getValue("xmltype");     
     if(types=="ok")
     {
        alert("编辑成功");
        closeWindow()
        //window.close();
     }else
     {
        alert("编辑失败");
     }     
  }  
    var date_desc;
    function showObjectSelectBox(srcobj,id)
    {
          date_desc=srcobj;
          selectElement = srcobj.id;
          Element.show(id);
          var pos=getAbsPosition(srcobj);         
	      with(document.getElementById(id))
	      {
	        style.position="absolute";
    		style.posLeft=(pos[0]-1)+"px";
    		style.left=(pos[0]-1)+"px";
		    style.posTop=(pos[1]-1+srcobj.offsetHeight)+"px";
		    style.top=(pos[1]-1+srcobj.offsetHeight)+"px";
		   //style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
         }  
   } 
   function setSelectValue(obj_select)
   {
      var values=obj_select.options[obj_select.selectedIndex].value;  
      //date_desc.value=values;
      Element.hide('object_panel1');
      //event.srcElement.releaseCapture();
       var expr_editor=date_desc;
       expr_editor.focus();
       if(!isCompatibleIE()){
    	   expr_editor.value=values;
       }else{
	       var element = document.selection;
			if (element!=null) 
			{
				var rge = element.createRange();
				if (rge!=null)	
					rge.text=values;
			}
       }
      for(var i=0;i<obj_select.options.length-1;i++){
    	  obj_select.options[i].selected = false;
      }
   }
</script>
<style>
fieldset{
margin:0px auto;
}
.framestyle{
border:none;
padding:14px 0;
}
</style>

<%
    String _width="76%";
    String _height="335px";
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){//得到系统的版本号 hcm 7.0}
        _width="700px";
        _height="345px";
%>
    <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
<%
    }
%>
<html:form action="/general/print/page_options" style='<%="width:"+_width%>'> <br>	
 
<hrms:tabset name="pageset" width="<%=_width%>" height="<%=_height%>" type="false"> 

<hrms:tab name="tab1" label="页面设置" visible="true">
 <table width="100%" align="center"> 
<tr> <td class="framestyle" valign="top">
     <table  width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">   
        <tr>
	  <td valign="top" width="100%">
		<fieldset align="center" style="width:90%;">
    		<legend ><bean:message key="report.parse.page"/></legend>
		                      <table width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      	<tr>
		                          <td width="100%" height="30" >
		                           <table>
		                	   <tr>
		                	     <td>
		                	      &nbsp;<bean:message key="report.parse.pagetype"/>：&nbsp;
		                	     </td>
		                	     <td align="left">
						  <html:select name="pageOptionsForm" property="parsevo.pagetype" size="1" onchange="SetDownMsg(this)">
                                                  <html:optionsCollection property="parsevo.pagetypelist" value="dataValue" label="dataName"/>
                                                  </html:select>                        				                	     
                                              </td>
		                	     <tr>
		                	   </table>  
		                                    
		                	  </td>
		                      	</tr>
		                      	<tr>
		                	  <td width="100%" height="30" >
		                	  <table>
		                	   <tr>
		                	     <td>
		                	      &nbsp; <bean:message key="report.parse.pagewidth"/>：<html:text name="pageOptionsForm" property="parsevo.width" styleId="widthP" size="5" styleClass="text" onkeypress="event.returnValue=IsDigit();"/>&nbsp;mm 
		                	     </td>
		                	     <td>
		                	      &nbsp; <bean:message key="report.parse.pageheight"/>：<html:text name="pageOptionsForm" property="parsevo.height" styleId="heightP" size="5" styleClass="text" onkeypress="event.returnValue=IsDigit();"/>&nbsp; mm
		                	     </td>
		                	     <tr>
		                	   </table>  
		                	  </td>
		                	</tr>
		                	<tr>
		                	 <td width="100%" height="20">
		                	  <table>
		                	   <tr>
		                	     <td width="50%">	
		                	       &nbsp;<bean:message key="report.parse.orientation"/>：
		                	       
                                               <logic:equal name="pageOptionsForm" property="parsevo.orientation" value="0">                    
                                                          &nbsp;<html:radio name="pageOptionsForm" property="parsevo.orientation" value="${pageOptionsForm.parsevo.orientation}"/> <bean:message key="report.parse.orientation.erect"/> 
                                               </logic:equal>
                                               <logic:notEqual name="pageOptionsForm" property="parsevo.orientation" value="0">                    
                                                          &nbsp;<html:radio name="pageOptionsForm"  property="parsevo.orientation" value="0"/><bean:message key="report.parse.orientation.erect"/> 
                                               </logic:notEqual>
                                                            	     
		                	       
		                	     </td>
		                	      <td width="50%">
		                	      <logic:equal name="pageOptionsForm" property="parsevo.orientation" value="1">                    
                                                 &nbsp;<html:radio name="pageOptionsForm" property="parsevo.orientation" value="${pageOptionsForm.parsevo.orientation}"/><bean:message key="report.parse.orientation.across"/>
                                               </logic:equal>
                                               <logic:notEqual name="pageOptionsForm" property="parsevo.orientation" value="1">                    
                                                  &nbsp;<html:radio name="pageOptionsForm" property="parsevo.orientation" value="1"/><bean:message key="report.parse.orientation.across"/> 
                                               </logic:notEqual>
		                	     </td>
		                	     <tr>
		                	   </table> 		                	     
			                  </td>
		                      	</tr>
		                     </table>
		                   </fieldset>
		       </td>
		   </tr>
		   <tr>
	            <td>
	              &nbsp;
	            </td>
	           </tr>
		    <tr>
		       <td >
		            <fieldset align="center" style="width:90%;">
    				  <legend ><bean:message key="report.parse.pageborder.name"/></legend>
		                      <table width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      	<tr>
		                          <td width="100%" height="30" >
		                           <table>
		                	   <tr>
		                	     <td>
		                	        <bean:message key="report.parse.pageborder.top"/>：<html:text name="pageOptionsForm" property="parsevo.top" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit2();"/>mm&nbsp; &nbsp;
		                	     </td>
		                	     <td  align="left">
		                	        <bean:message key="report.parse.pageborder.bottom"/>：<html:text name="pageOptionsForm" property="parsevo.bottom" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit2();"/>mm&nbsp; &nbsp;
                                              </td>
		                	     <tr>
		                	   </table> 		                                    
		                	  </td>
		                      	</tr>
		                      	<tr>
		                	  <td width="100%" height="30" >
		                	  <table>
		                	   <tr>
		                	    <td>
		                	       <bean:message key="report.parse.pageborder.left"/>：<html:text name="pageOptionsForm" property="parsevo.left" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit2();"/>mm&nbsp; &nbsp;
		                	     </td>
		                	     <td align="left">
		                	       <bean:message key="report.parse.pageborder.right"/>：<html:text name="pageOptionsForm" property="parsevo.right" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit2();"/>mm&nbsp; &nbsp;
		                	     </td>
		                	     <tr>
		                	   </table>  
		                	  </td>
		                	</tr>	
		                	<logic:equal name="pageOptionsForm" property="state" value="0">  
		                	<tr>
		                	 <td width="100%" height="20">
		                	  <table>
		                	  
		                	   <tr>
		                	     <td width="50%">	
		                	       <logic:equal name="pageOptionsForm" property="parsevo.unit" value="px">                    
                                                    &nbsp;<html:radio name="pageOptionsForm" property="parsevo.unit" value="${pageOptionsForm.parsevo.unit}"/> <bean:message key="report.parse.px"/>
                                              </logic:equal>
                                              <logic:notEqual name="pageOptionsForm" property="parsevo.unit" value="px">                    
                                                     &nbsp;<html:radio name="pageOptionsForm" property="parsevo.unit" value="px"/> <bean:message key="report.parse.px"/>
                                              </logic:notEqual>
                                                   	     
		                	      
		                	     </td>
		                	      <td width="50%">
		                	      <logic:equal name="pageOptionsForm" property="parsevo.unit" value="mm">                    
                                                 &nbsp;<html:radio name="pageOptionsForm" property="parsevo.unit" value="${pageOptionsForm.parsevo.unit}"/><bean:message key="report.parse.mm"/>
                                               </logic:equal>
                                               <logic:notEqual name="pageOptionsForm" property="parsevo.unit" value="mm">                    
                                                  &nbsp;<html:radio name="pageOptionsForm" property="parsevo.unit" value="mm"/> <bean:message key="report.parse.mm"/>
                                               </logic:notEqual>
		                	     </td>
		                	     <tr>
		                	   </table> 		                	     
			                  </td>
		                      	</tr>	
		                      </logic:equal>	                	
		                     </table>
		                   </fieldset>
		</td>
              </tr>
           </table>
    </td>
    </tr>
  </table>
  </hrms:tab>
  <hrms:tab name="tab2" label="标题" visible="true">

	    <table  width="100%"  border="0"  cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top" align="center">   
                     <tr>
		       <td width="100%" class="framestyle">
		          <fieldset align="center" style="width:90%;">
    			     <legend ><bean:message key="report.parse.title.name"/></legend>
		                <table width="100%"  border="0"  cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                   <tr>
		                      <td width="100%" height="40" >
		                        <table width="100%">
		                         <tr>
		                           <td>
                                            <table width="100%">
                                             <tr>
		                               <td width="15%">
		                                 <bean:message key="report.parse.title.name"/>：
		                               </td>
		                               <td align="left">     
		                               <logic:equal value="4" name="pageOptionsForm" property="state">
		                                <html:textarea styleId='title_fw' name="pageOptionsForm" property="parsevo.title_fw"  onclick="showObjectSelectBox(this,'object_panel2');" onblur="Element.hide('object_panel2');" cols="50" rows="20" style="${pageOptionsForm.sytle_title}"/>
		                               </logic:equal>
		                               	<logic:notEqual value="4" name="pageOptionsForm" property="state">
		                               	
		                               	  <html:textarea styleId='title_fw' name="pageOptionsForm" property="parsevo.title_fw" cols="50" rows="20" style="${pageOptionsForm.sytle_title}"/>
		                               	</logic:notEqual>                                
		                                 
		                               </td>
		                             </tr>
                                            </table>
                                           </td>
		                          </tr>
		                	  <tr>
		                	  <td>
                                           <table width="100%">
                                             <tr>
		                	      <td height="30">
		                	        <bean:message key="report.parse.fn"/>：
		                	        <html:select name="pageOptionsForm" property="parsevo.title_fn" size="1" styleId='title_fw_fn' onchange='title_fw_n()'>
                                                <html:optionsCollection property="parsevo.fnlist" value="dataValue" label="dataName"/>
                                                </html:select>&nbsp;
		                	        <bean:message key="report.parse.fz"/>：
		                	        <logic:equal name="pageOptionsForm" property="parsevo.title_fz" value="">
		                	        	<html:select name="pageOptionsForm" property="parsevo.title_fz" size="1" styleId='title_fw_fz' onchange='title_fw_z()' value="12">
                                        	<html:optionsCollection property="parsevo.fzlist" value="dataValue" label="dataName"/>
                                        </html:select>
		                	        </logic:equal>
		                	        <logic:notEqual name="pageOptionsForm" property="parsevo.title_fz" value="">
		                	        	<html:select name="pageOptionsForm" property="parsevo.title_fz" size="1" styleId='title_fw_fz' onchange='title_fw_z()'>
                                        	<html:optionsCollection property="parsevo.fzlist" value="dataValue" label="dataName"/>
                                        </html:select>
		                	        </logic:notEqual>
                                              </td>                                                                                        	
                                            </tr>
                                           </table>
                                          </td>
                                          </tr> 
                                          <logic:equal name="pageOptionsForm" property="state" value="0"> 
                                            <tr>
                                              <td>
                                                <table><tr>
                                                  <td>
		                	          <bean:message key="report.parse.th"/>：<html:text name="pageOptionsForm" property="parsevo.title_h" size="8" styleClass="text"  onkeypress="event.returnValue=IsDigit();"/> &nbsp;
                                                   </td></tr></table>   
                                              </td>
                                            </tr>
                                          </logic:equal>  
                                          
                                          <tr>
                                           <td>
                                            <table>
                                              <tr>
                                               <td height="30">
		                	         <bean:message key="report.parse.fb"/>：<html:multibox name="pageOptionsForm" property="parsevo.title_fb" value="#fb[1]" styleId='title_fb' onclick="title_fw_b(this.form.title_fb)"/>&nbsp;
                                               </td>
                                               <td>
		                	         <bean:message key="report.parse.fu"/>：<html:multibox name="pageOptionsForm" property="parsevo.title_fu" value="#fu[1]" styleId='title_fu' onclick="title_fw_u(this.form.title_fu)"/>&nbsp;
                                               </td>
                                               <td>
		                	         <bean:message key="report.parse.fi"/>：<html:multibox name="pageOptionsForm" property="parsevo.title_fi" value="#fi[1]" styleId='title_fi' onclick="title_fw_i(this.form.title_fi)"/> &nbsp;
                                               </td>
                                               <logic:notEqual name="pageOptionsForm" property="state" value="0">
                                                 <td height="30">
		                	         <bean:message key="report.parse.fs"/>：<html:multibox name="pageOptionsForm" property="parsevo.title_fs" value="#fs[1]" styleId='title_fs' onclick="title_fw_s(this.form.title_fs)"/> &nbsp;
                                                </td>
                                               </logic:notEqual>
                                              </tr>
                                             </table>
                                            </td>
		                	         </tr>
		                	   <logic:notEqual name="pageOptionsForm" property="state" value="0">  
		                	    <tr>
                                           <td>
                                            <table>
                                              <tr>                                               
                                                 <td height="30">
                                                 <bean:message key="kq.item.color"/>：
           	                                  <logic:equal name="pageOptionsForm" property="parsevo.title_fc" value="">
                                              <input type="text" name="parsevo.title_fc" size="6" value="#000000" onchange="title_fw_c()" change="title_fw_c()" readonly="readonly" id="title_fw_fc" style="BACKGROUND-COLOR:#000000" class="textColorWrite" alt="clrDlg" autocomplete="off">
                                              </logic:equal>
                                              <logic:notEqual name="pageOptionsForm" property="parsevo.title_fc" value="">
                                              <input type="text" name="parsevo.title_fc" size="6" value="${pageOptionsForm.parsevo.title_fc}" onchange="title_fw_c()" change="title_fw_c()" readonly="readonly" id="title_fw_fc" style="BACKGROUND-COLOR:${pageOptionsForm.parsevo.title_fc}" class="textColorWrite" alt="clrDlg" autocomplete="off">
                                              </logic:notEqual>
           	                                </td>
                                              
                                              </tr>
                                             </table>
                                            </td>
		                	   </tr>
		                	    </logic:notEqual>
		                	 </table> 		                                    
		                      </td>
		                   </tr>		                      			                	
		                 </table>
		               </fieldset>
		           </td>
		        </tr>
		     </table>	                 
  </hrms:tab>
  <hrms:tab name="tab3" label="页头内容" visible="true">
       <table  width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top" align="center">   
                     <tr>
		       <td width="100%" class="framestyle">
		          <fieldset align="center" style="width:90%;">
    			     <legend ><bean:message key="report.parse.head.name"/></legend>
		                <table width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0" >                   	
		                   <tr>
		                     <td width="100%" > 
		                       <!--考勤报表-->
		                        <table width="100%" >  
		                        <logic:equal name="pageOptionsForm" property="state" value="0">  
		                          <tr>
		                             <td width="15%">
		                             &nbsp;<bean:message key="report.conter"/>：
		                             </td>
		                             <td>
		                             <html:textarea name="pageOptionsForm" property="parsevo.head_fw" styleId='head_fw' cols="50" rows="10" style="height:70px;width:70%;font-size:9pt"/>
		                             </td>
		                           </tr>
		                         </logic:equal>
		                         <!--综合报表-->
		                         <logic:notEqual name="pageOptionsForm" property="state" value="0">                    
		                             <tr>
		                             <td width="15%">
		                             &nbsp;<bean:message key="report.head.left.conter"/>：
		                             </td>
		                             <td>
		                             <html:textarea name="pageOptionsForm" property="parsevo.head_flw" styleId='head_flw' onclick="showObjectSelectBox(this,'object_panel1');"  cols="50" rows="10" style="${pageOptionsForm.sytle_head}"/>
		                             </td>
		                             </tr>
		                             <tr>
		                             <td width="15%">
		                             &nbsp;<bean:message key="report.head.middle.conter"/>：
		                             </td>
		                             <td>
		                             <html:textarea name="pageOptionsForm" property="parsevo.head_fmw" styleId='head_fmw' onclick="showObjectSelectBox(this,'object_panel1');"  cols="50" rows="10" style="${pageOptionsForm.sytle_head}"/>
		                             </td>
		                             </tr>
		                             <tr>
		                             <td width="15%">
		                             &nbsp;<bean:message key="report.head.right.conter"/>：
		                             </td>
		                             <td>
		                             <html:textarea name="pageOptionsForm" property="parsevo.head_frw" styleId='head_frw' onclick="showObjectSelectBox(this,'object_panel1');"  cols="50" rows="10" style="${pageOptionsForm.sytle_head}"/>
		                             </td>
		                             </tr>
		                          </logic:notEqual>
		                          </table>
		                      </td>
		                   </tr>
		                   <tr>
		                      <td width="100%" height="40" >
		                         <table>                  
		                          <tr>
		                	  <td>
                                           <table>
                                            <tr>
		                	     <td height="30">
		                	        <bean:message key="report.parse.fn"/>：
		                	        <html:select name="pageOptionsForm" property="parsevo.head_fn" size="1" styleId='head_fw_fn' onchange='head_fw_n(${pageOptionsForm.state})'>
                                                <html:optionsCollection property="parsevo.fnlist" value="dataValue" label="dataName"/>
                                                </html:select>&nbsp;
		                	     </td>
		                	     <td>
		                	        <bean:message key="report.parse.fz"/>：
		                	        <html:select name="pageOptionsForm" property="parsevo.head_fz" size="1"  styleId='head_fw_fz' onchange='head_fw_z(${pageOptionsForm.state})'>
                                                <html:optionsCollection property="parsevo.fzlist" value="dataValue" label="dataName"/>
                                                </html:select>
		                	         &nbsp;
                                             </td>                                             
                                            </tr>
                                           </table>
                                          </td>
                                          </tr> 
                                           <logic:equal name="pageOptionsForm" property="state" value="0"> 
                                            <tr>
                                              <td>
                                                <table><tr>
                                                  <td>
		                	         <bean:message key="report.parse.th"/>：<html:text name="pageOptionsForm" property="parsevo.head_h" size="8" styleClass="text"  onkeypress="event.returnValue=IsDigit();"/> &nbsp;
                                                   </td></tr></table>   
                                              </td>
                                            </tr>
                                          </logic:equal> 
                                          <tr>
                                           <td>
                                            <table>
                                             <tr>
                                            <td height="30">
		                	       <bean:message key="report.parse.fb"/>： <html:multibox name="pageOptionsForm" property="parsevo.head_fb" value="#fb[1]" styleId='head_fb' onclick="head_fw_b(${pageOptionsForm.state},this.form.head_fb)"/>&nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.fu"/>： <html:multibox name="pageOptionsForm" property="parsevo.head_fu" value="#fu[1]" styleId='head_fu' onclick="head_fw_u(${pageOptionsForm.state},this.form.head_fu)"/>&nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.fi"/>： <html:multibox name="pageOptionsForm" property="parsevo.head_fi" value="#fi[1]" styleId='head_fi' onclick="head_fw_i(${pageOptionsForm.state},this.form.head_fi)"/>&nbsp;
                                             </td>   
                                             <logic:notEqual name="pageOptionsForm" property="state" value="0">  
                                               <td height="30">
		                	         <bean:message key="report.parse.fs"/>：<html:multibox name="pageOptionsForm" property="parsevo.head_fs" value="#fs[1]" styleId='head_fs' onclick="head_fw_s(${pageOptionsForm.state},this.form.head_fs)"/> &nbsp;
                                               </td>  
                                              </logic:notEqual>                                          
		                	     </tr>
		                	    </table>
                                           </td>                                           
		                	  </tr>
		                	   <tr>
                                           <td>
                                            <table>
                                             <tr>
                                             <logic:notEqual name="pageOptionsForm" property="state" value="0">  
                                               <td height="30" >
                                                 <bean:message key="kq.item.color"/>：
                                                  <logic:equal name="pageOptionsForm" property="parsevo.head_fc" value="">
                                                      <html:text  name="pageOptionsForm" property="parsevo.head_fc" alt="clrDlg" styleId='head_fw_fc' onchange='head_fw_c()' size="6" style="BACKGROUND-COLOR:#000000;"  styleClass="textColorWrite" readonly="true"/>
                                                  </logic:equal>
                                                  <logic:notEqual name="pageOptionsForm" property="parsevo.head_fc" value="">
           	                                          <html:text  name="pageOptionsForm" property="parsevo.head_fc" alt="clrDlg" styleId='head_fw_fc' onchange='head_fw_c()' size="6" style="BACKGROUND-COLOR:${pageOptionsForm.parsevo.head_fc}"  styleClass="textColorWrite" readonly="true"/>
           	                                      </logic:notEqual>
           	                               </td>
           	                             </logic:notEqual>
           	                             <logic:equal name="pageOptionsForm" property="state" value="0">  
                                             <td>
		                	       <bean:message key="report.parse.d"/>： <html:multibox name="pageOptionsForm" property="parsevo.head_d" value="#d"/>&nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.t"/>： <html:multibox name="pageOptionsForm" property="parsevo.head_t" value="#t"/>&nbsp;
		                	     </td>
		                	     <td >
		                	       <bean:message key="report.parse.p"/>： <html:multibox name="pageOptionsForm" property="parsevo.head_p" value="#p"/>&nbsp;
		                	     </td>
		                	     </logic:equal>
		                	     </tr>
		                	    </table>
                                           </td>                                           
		                	  </tr>
		                	  <logic:equal name="pageOptionsForm" property="state" value="0">  
		                	  <tr>
                                           <td>
                                            <table>
                                            <tr>                                              
		                	     <td height="30">
		                	       <bean:message key="report.parse.c"/>：<html:multibox name="pageOptionsForm" property="parsevo.head_c" value="#c"/> &nbsp;
		                	     </td>
                                             <td>
		                	       <bean:message key="report.parse.e"/>：<html:multibox name="pageOptionsForm" property="parsevo.head_e" value="#e"/> &nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.u"/>：<html:multibox name="pageOptionsForm" property="parsevo.head_u" value="#u"/> &nbsp;
                                             </td>
                                            </tr>
                                           </table>
                                          </td>
		                	 </tr>
		                	 </logic:equal>
		                        </table> 		                                    
		                      </td>
		                   </tr>		                      			                	
		                 </table>
		               </fieldset>
		           </td>
		        </tr>
		     </table>   
  
  </hrms:tab>  
  <hrms:tab name="tab4" label="页尾内容" visible="true">
       <table  width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top" height="90%">   
                     <tr>
		       <td width="100%" class="framestyle">
		          <fieldset align="center" style="width:90%;">
    			     <legend ><bean:message key="report.parse.teil.name"/></legend>
		                <table width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                  <table width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0" >                   	
		                   <tr>
		                     <td width="100%" > 
		                       <!--考勤报表-->
		                        <table width="100%" >  
		                        <logic:equal name="pageOptionsForm" property="state" value="0">  
		                          <tr>
		                             <td width="15%">
		                             &nbsp;<bean:message key="report.conter"/>：
		                             </td>
		                             <td>
		                             <html:textarea name="pageOptionsForm" property="parsevo.tile_fw" styleId='tile_fw'  cols="50" rows="10" style="height:70px;width:70%;font-size:9pt"/>
		                             </td>
		                           </tr>
		                         </logic:equal>
		                         <!--综合报表-->
		                         <logic:notEqual name="pageOptionsForm" property="state" value="0">                    
		                             <tr>
		                             <td width="15%">
		                             &nbsp;<bean:message key="report.tile.left.conter"/>：
		                             </td>
		                             <td>
		                             <html:textarea name="pageOptionsForm" property="parsevo.tile_flw" styleId='tile_flw' onclick="showObjectSelectBox(this,'object_panel1');" cols="50" rows="10" style="${pageOptionsForm.sytle_tile}"/>
		                             </td>
		                             </tr>
		                             <tr>
		                             <td width="15%">
		                             &nbsp;<bean:message key="report.tile.middle.conter"/>：
		                             </td>
		                             <td>
		                             <html:textarea name="pageOptionsForm" property="parsevo.tile_fmw"  styleId='tile_fmw' onclick="showObjectSelectBox(this,'object_panel1');"  cols="50" rows="10" style="${pageOptionsForm.sytle_tile}"/>
		                             </td>
		                             </tr>
		                             <tr>
		                             <td width="15%">
		                             &nbsp;<bean:message key="report.tile.right.conter"/>：
		                             </td>
		                             <td>
		                             <html:textarea name="pageOptionsForm" property="parsevo.tile_frw" styleId='tile_frw' onclick="showObjectSelectBox(this,'object_panel1');" cols="50" rows="10" style="${pageOptionsForm.sytle_tile}"/>
		                             </td>
		                             </tr>
		                          </logic:notEqual>
		                          </table>
		                      </td>
		                   </tr>
		                   <tr>
		                      <td width="100%" height="40" >
		                         <table>		                         
		                	  <tr>
		                	  <td>
                                           <table>
                                            <tr>
		                	      <td height="30">
		                	         <bean:message key="report.parse.fn"/>：
		                	         <html:select name="pageOptionsForm" property="parsevo.tile_fn" size="1" styleId='tile_fw_fn' onchange='tile_fw_n(${pageOptionsForm.state})'>
                                                 <html:optionsCollection property="parsevo.fnlist" value="dataValue" label="dataName"/>
                                                 </html:select>&nbsp;
		                	       </td>
		                	       <td>
		                	         <bean:message key="report.parse.fz"/>：
		                	         <html:select name="pageOptionsForm" property="parsevo.tile_fz" size="1" styleId='tile_fw_fz' onchange='tile_fw_z(${pageOptionsForm.state})'>
                                                 <html:optionsCollection property="parsevo.fzlist" value="dataValue" label="dataName"/>
                                                 </html:select>
		                	          &nbsp;
                                               </td>                                               
                                            </tr>
                                           </table>
                                          </td>
                                          </tr> 
                                          <logic:equal name="pageOptionsForm" property="state" value="0"> 
                                            <tr>
                                              <td>
                                                <table>
                                                  <tr>
                                                    <td>
		                	              <bean:message key="report.parse.th"/>：<html:text name="pageOptionsForm" property="parsevo.tile_h" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit();"/> &nbsp;
                                                    </td>
                                                  </tr>
                                                 </table>   
                                              </td>
                                            </tr>
                                          </logic:equal> 
                                          <tr>
                                           <td>
                                            <table>
                                             <tr>
                                              <td height="30">
		                	       <bean:message key="report.parse.fb"/>：<html:multibox name="pageOptionsForm" property="parsevo.tile_fb" styleId='tile_fb' value="#fb[1]" onclick="tile_fw_b(${pageOptionsForm.state},this.form.tile_fb)"/>&nbsp;
                                              </td>
                                              <td>
		                	       <bean:message key="report.parse.fu"/>：<html:multibox name="pageOptionsForm" property="parsevo.tile_fu" styleId='tile_fu' value="#fu[1]" onclick="tile_fw_u(${pageOptionsForm.state},this.form.tile_fu)"/>&nbsp;
                                              </td>
                                              <td>
		                	       <bean:message key="report.parse.fi"/>：<html:multibox name="pageOptionsForm" property="parsevo.tile_fi" styleId='tile_fi' value="#fi[1]" onclick="tile_fw_i(${pageOptionsForm.state},this.form.tile_fi)"/>&nbsp;
                                              </td> 
                                              <logic:notEqual name="pageOptionsForm" property="state" value="0">  
                                                <td height="30">
		                	          <bean:message key="report.parse.fs"/>：<html:multibox name="pageOptionsForm" property="parsevo.tile_fs" styleId='tile_fs' value="#fs[1]" onclick="tile_fw_s(${pageOptionsForm.state},this.form.tile_fs)"/>&nbsp;
                                                </td>   
                                              </logic:notEqual>                                         
		                	     </tr>
		                	    </table>
                                           </td>                                           
		                	  </tr>
		                	  <tr>
                                           <td>
                                            <table>
                                             <tr>
                                             <logic:notEqual name="pageOptionsForm" property="state" value="0">  
                                               <td height="30" >
                                                 <bean:message key="kq.item.color"/>：
                                                <logic:equal name="pageOptionsForm" property="parsevo.tile_fc" value="">
                                                    <html:text  name="pageOptionsForm" property="parsevo.tile_fc" alt="clrDlg" styleId='tile_fw_fc' onchange='tile_fw_c()' size="6" style="BACKGROUND-COLOR:#000000;"  styleClass="textColorWrite" readonly="true"/>
                                                </logic:equal>
                                                <logic:notEqual name="pageOptionsForm" property="parsevo.tile_fc" value="">
           	                                        <html:text  name="pageOptionsForm" property="parsevo.tile_fc" alt="clrDlg" styleId='tile_fw_fc' onchange='tile_fw_c()' size="6" style="BACKGROUND-COLOR:${pageOptionsForm.parsevo.tile_fc}"  styleClass="textColorWrite" readonly="true"/>
                                                </logic:notEqual>
           	                               </td>
           	                             </logic:notEqual>
           	                             <logic:equal name="pageOptionsForm" property="state" value="0">  
                                             <td>
		                	       <bean:message key="report.parse.d"/>： <html:multibox name="pageOptionsForm" property="parsevo.tile_d" value="#d"/>&nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.t"/>： <html:multibox name="pageOptionsForm" property="parsevo.tile_t" value="#t"/>&nbsp;
		                	     </td>
		                	     <td>
		                	       <bean:message key="report.parse.p"/>：<html:multibox name="pageOptionsForm" property="parsevo.tile_p" value="#p"/>&nbsp;
		                	     </td>
		                	     </logic:equal>
		                	     </tr>
		                	    </table>
                                           </td>                                           
		                	  </tr>
		                	  <logic:equal name="pageOptionsForm" property="state" value="0">  
		                	  <tr>
                                           <td>
                                            <table>
                                            <tr>                                              
		                	     <td height="30">
		                	       <bean:message key="report.parse.c"/>：<html:multibox name="pageOptionsForm" property="parsevo.tile_c" value="#c"/>&nbsp;
		                	     </td>
                                             <td>
		                	       <bean:message key="report.parse.e"/>：<html:multibox name="pageOptionsForm" property="parsevo.tile_e" value="#e"/>&nbsp;
                                             </td>
                                             <td>
		                	       <bean:message key="report.parse.u"/>：<html:multibox name="pageOptionsForm" property="parsevo.tile_u" value="#u"/>&nbsp;
                                             </td>
                                            </tr>
                                           </table>
                                          </td>
		                	 </tr>
		                	 </logic:equal>
		                        </table> 		                                    
		                      </td>
		                   </tr>		    		                      			                	
		                 </table>
		               </fieldset>
		           </td>
		        </tr>
		     </table>   
  
  </hrms:tab>  
  <hrms:tab name="tab5" label="正文" visible="true">
		 <table  width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">   
                 <tr>
		       <td width="100%" class="framestyle">
		       <table  width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">  	     
		       
		       
		       
		       
		        <logic:equal name="pageOptionsForm" property="state" value="4">
		         <tr>
		       <td width="100%">
		       <fieldset align="center" style="width:90%;">
    			     <legend >表头信息</legend>
		                <table width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                   <tr>
		                      <td width="100%" height="40" >
		                         <table>
		                	  <tr>
		                	    <td height="30">
		                	        <bean:message key="report.parse.fn"/>：<html:select name="pageOptionsForm" property="parsevo.thead_fn" size="1">
                                                <html:optionsCollection property="parsevo.fnlist" value="dataValue" label="dataName"/>
                                                </html:select>&nbsp;
		                	     </td>
		                	     <td>
		                	        <bean:message key="report.parse.fz"/>：
		                	        <html:select name="pageOptionsForm" property="parsevo.thead_fz" size="1">
                                                <html:optionsCollection property="parsevo.fzlist" value="dataValue" label="dataName"/>
                                                </html:select>
		                	         &nbsp;
                                             </td>                                              
                                           </tr>
                                           <tr>
                                            <td height="30" colspan="2">
		                	       <bean:message key="report.parse.fb"/>：<html:multibox name="pageOptionsForm" property="parsevo.thead_fb" value="#fb[1]"/>&nbsp;
                                            
		                	       <bean:message key="report.parse.fu"/>：<html:multibox name="pageOptionsForm" property="parsevo.thead_fu" value="#fu[1]"/>&nbsp;
                                             
		                	       <bean:message key="report.parse.fi"/>：<html:multibox name="pageOptionsForm" property="parsevo.thead_fi" value="#fi[1]"/> &nbsp;
                                             </td>                                            
		                	   <tr>
		                	   
                                              <tr> <td height="30" >
                                                 <bean:message key="kq.item.color"/>：
           	                                  <html:text  name="pageOptionsForm" property="parsevo.thead_fc" alt="clrDlg" size="6" style="BACKGROUND-COLOR:${pageOptionsForm.parsevo.thead_fc}"  styleClass="textColorWrite" readonly="true"/>
           	                               </td></tr>
           	                            
		                	 </table> 		                                    
		                      </td>
		                   </tr>		                      			                	
		                 </table>
		               </fieldset>
                  </td></tr>
		         <tr>
	            <td>
	              <hr>
	            </td>
	           </tr>
		       </logic:equal>	       
		         <tr>
		       <td width="100%">
		          <fieldset align="center" style="width:90%;">
    			     <legend ><bean:message key="report.parse.body.name"/></legend>
		                <table width="100%"  border="0"   cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                   <tr>
		                      <td width="100%" height="40" >
		                         <table>
		                	  <tr>
		                	    <td height="30">
		                	        <bean:message key="report.parse.fn"/>：<html:select name="pageOptionsForm" property="parsevo.body_fn" size="1">
                                                <html:optionsCollection property="parsevo.fnlist" value="dataValue" label="dataName"/>
                                                </html:select>&nbsp;
		                	     </td>
		                	     <td>
		                	        <bean:message key="report.parse.fz"/>：
		                	        <html:select name="pageOptionsForm" property="parsevo.body_fz" size="1">
                                                <html:optionsCollection property="parsevo.fzlist" value="dataValue" label="dataName"/>
                                                </html:select>
		                	         &nbsp;
                                             </td>                                              
                                           </tr>
                                           <tr>
                                            <td height="30" colspan="2">
		                	       <bean:message key="report.parse.fb"/>：<html:multibox name="pageOptionsForm" property="parsevo.body_fb" value="#fb[1]"/>&nbsp;
                                            
		                	       <bean:message key="report.parse.fu"/>：<html:multibox name="pageOptionsForm" property="parsevo.body_fu" value="#fu[1]"/>&nbsp;
                                             
		                	       <bean:message key="report.parse.fi"/>：<html:multibox name="pageOptionsForm" property="parsevo.body_fi" value="#fi[1]"/> &nbsp;
                                             </td>                                            
		                	   <tr>
		                	   <logic:equal name="pageOptionsForm" property="state" value="0">  
		                	   <tr>
                                             <td height="30" colspan="3">
                                               <table>
                                                 <tr>
                                                   <td>
                                                     <bean:message key="report.parse.body.rownum"/> ：&nbsp;
                                                   </td>
                                                   <td>
                                                       <logic:equal name="pageOptionsForm" property="parsevo.body_pr" value="#pr[0]">                    
                                                          &nbsp;<html:radio name="pageOptionsForm" property="parsevo.body_pr" value="${pageOptionsForm.parsevo.body_pr}" onclick="closes()"/>
                                                       </logic:equal>
                                                       <logic:notEqual name="pageOptionsForm" property="parsevo.body_pr" value="#pr[0]">                    
                                                          &nbsp;<html:radio name="pageOptionsForm" property="parsevo.body_pr" value="#pr[0]" onclick="closes()"/>
                                                       </logic:notEqual>
		                	             <bean:message key="report.parse.body.isAutorow"/>
                                                   </td>
                                                   <td>                                                                                                      
                                                     <logic:equal name="pageOptionsForm" property="parsevo.body_pr" value="#pr[1]">                    
                                                       &nbsp;<html:radio name="pageOptionsForm" property="parsevo.body_pr" value="${pageOptionsForm.parsevo.body_pr}" onclick="show()"/>
                                                     </logic:equal>
                                                     <logic:notEqual name="pageOptionsForm" property="parsevo.body_pr" value="#pr[1]">                    
                                                        &nbsp;<html:radio name="pageOptionsForm" property="parsevo.body_pr" value="#pr[1]" onclick="show()"/>
                                                     </logic:notEqual>
		                	             <bean:message key="report.parse.body.isUserrow"/> ：&nbsp;
                                                   </td>
                                                   <td>
                                                      <logic:equal name="pageOptionsForm" property="parsevo.body_pr" value="#pr[1]">    
                                                         <div id="b" style="display:none;" >             
                                                           &nbsp;<html:text name="pageOptionsForm" property="parsevo.body_rn" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit();"/>
                                                        </div>
                                                         <script language="javascript"> 
                                                          show();
                                                         </script> 
                                                      </logic:equal>
                                                      <logic:notEqual name="pageOptionsForm" property="parsevo.body_pr" value="#pr[1]"> 
                                                        <div id="b" style="display:none;" >
                                                            <html:text name="pageOptionsForm" property="parsevo.body_rn" size="8" styleClass="text" onkeypress="event.returnValue=IsDigit();"/>
                                                        </div>
                                                      </logic:notEqual>
                                                      
                                                   </td>
                                                 </tr>
                                               </table>          	          
                                             </td>                                              
		                	   </tr>
		                	   </logic:equal>
		                	   <logic:notEqual name="pageOptionsForm" property="state" value="0">  
                                              <tr> <td height="30" >
                                                 <bean:message key="kq.item.color"/>：
                                               <logic:equal name="pageOptionsForm" property="parsevo.body_fc" value="">
                                                   <html:text  name="pageOptionsForm" property="parsevo.body_fc" alt="clrDlg" size="6" style="BACKGROUND-COLOR:#000000;"  styleClass="textColorWrite" readonly="true"/>
                                               </logic:equal>
                                               <logic:notEqual name="pageOptionsForm" property="parsevo.body_fc" value="">
           	                                       <html:text  name="pageOptionsForm" property="parsevo.body_fc" alt="clrDlg" size="6" style="BACKGROUND-COLOR:${pageOptionsForm.parsevo.body_fc}"  styleClass="textColorWrite" readonly="true"/>
                                               </logic:notEqual>
           	                               </td></tr>
           	                             </logic:notEqual>
		                	 </table> 		                                    
		                      </td>
		                   </tr>		                      			                	
		                 </table>
		               </fieldset>
		            </td>
		          </tr>
		        </table> 
  </td>
  </tr>
  </table>
  </hrms:tab> 
</hrms:tabset>   
    <table width="50%" align="center" valign="top" class="contabinBTTable" style="margin-top:3px;">
      <tr>
        <td align="center">     
        <html:hidden name="pageOptionsForm" property="rsid"/>   
        <html:hidden name="pageOptionsForm" property="rsdtlid"/>     
             <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="javascript:greateXML('<bean:write name="pageOptionsForm" property="state" filter="true"/>','<bean:write name="pageOptionsForm" property="id" filter="true"/>','setin');" class="mybutton">
             <input type="button" name="btnreturn" value='初始化' onclick="javascript:greateXML('<bean:write name="pageOptionsForm" property="state" filter="true"/>','<bean:write name="pageOptionsForm" property="id" filter="true"/>','init');" class="mybutton">
             <input type="button" name="btnreturn" value='<bean:message key="button.cancel"/>' onclick="closeWindow()" class="mybutton">
        </td>
      </tr>
    </table>    
    <div id="object_panel1">         
   	<select name="object_rate" multiple="multiple" size="6"  style="width:200" onchange="setSelectValue(this);">    
		<option value="&[页码]">&nbsp;&[页码]&nbsp;</option>
		<option value="&[总人数]">&nbsp;&[总人数]&nbsp;</option>	 
		<option value="&[制作人]">&nbsp;&[制作人]&nbsp;</option>
		<option value="&[日期]">&nbsp;&[日期]&nbsp;</option>	
		<option value="&[YYYY年YY月]">&nbsp;&[YYYY年YY月]&nbsp;</option>	
		<option value="&[时间]">&nbsp;&[时间]&nbsp;</option>
	</select>
   </div>  
    <div id="object_panel2">         
   	<select name="object_rate2" multiple="multiple" size="5"  style="width:200" onchange="setSelectValue(this);">    
		<option value="&[年月]">&nbsp;&[年月]&nbsp;</option>
		<option value="&[YYYY年YY月]">&nbsp;&[YYYY年YY月]&nbsp;</option>	
		<option value="&[单位名称]">&nbsp;&[单位名称]&nbsp;</option>	 
		<option value="&[报表名称]">&nbsp;&[报表名称]&nbsp;</option>	 
	</select>
   </div>  		
</html:form>
<div id="colorpanel" style="position:absolute;display:none;width:253px;height:177px;z-index:3"></div> 
</body>
<script language="javascript">
	
	function closeWindow(){
		// 
		if(parent.parent.Ext&&parent.parent.Ext.getCmp('pagesetup_window')){
			parent.parent.Ext.getCmp('pagesetup_window').close();
		}else{
			top.window.close();
		}
	}

    Element.hide('object_panel1');
    Element.hide('object_panel2');
    var state = "${pageOptionsForm.state}";
    if(state=='4'){
       title_fw_b(document.getElementById("title_fb"));
       title_fw_z();
    }
</script>
<%
    if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){//得到系统的版本号)
%>
<script type="text/javascript">
        var forms=document.getElementsByTagName("form");
        var form;
        if(forms){
            for(var i=0;i<forms.length;i++){
                if(forms[i].name=="pageOptionsForm"){
                    form=forms[i]
                    break;
                }
            }
        }
</script>
<%
   }
%>