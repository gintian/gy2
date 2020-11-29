<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">

  function saves()
  {
    var ms=$F('mss');  
    var tav="";
   if(ms=='4')
     tav="sss";
   else
     tav=$F('code');
    
    var tas=$F('name');   
    var childlen="${kqItemForm.childlen}";
    if(tav.length==0||tas.length==0)
    {
       alert("输入值不能为空！");
       return false;
     }
     
     if (!tav.match(/^([0-9a-zA-z])*$/)) {
     	alert("代码值只能为数字或字母！");
       	return false;
     }
     
     if (!tas.match(/^([\w|\u4e00-\u9fa5])*$/)) {
     	alert("名称不能包含特殊字符，只能是汉字、字母、数字、下划线！");
       	return false;
     }

     if(IsOverStrLength(tas,50))
     {
         alert("名称长度不能超过50个字符或25个汉字！");
         return false;
     }
 	
	if(childlen!="")
     {
       var mess="${kqItemForm.mes}";
       if(mess!="4")
       {
       	  if(parseInt(childlen)!=28)
       	  {
			  //if(tav.length!=parseInt(childlen))
			  if(tav.length>parseInt(childlen))
	          {
	            //alert("输入代码的长度必须等于"+childlen+"位！");
	            alert("输入代码的长度不能大于"+childlen+"位！");
	            return false;
	          }else{     
	            //kqItemForm.action="/kq/options/add_item_type.do?b_save=link"; 
	            //kqItemForm.target="il_body";  	 
	            //kqItemForm.submit();
	            var codesetvo=new Object();
	            codesetvo.code = $F("code");
	            codesetvo.name = $F("name");
	            codesetvo.flag = "1";
		        codesetvo.codeitemid = "${kqItemForm.codeitemid}";
		        codesetvo.mes = "${kqItemForm.mes}";
	            window.returnValue=codesetvo;
	            window.close();
	           }
       	  }else
       	  {
       	  	var codesetvo=new Object();
            codesetvo.code = $F("code");
            codesetvo.name = $F("name");
            codesetvo.flag = "1";
	        codesetvo.codeitemid = "${kqItemForm.codeitemid}";
	        codesetvo.mes = "${kqItemForm.mes}";
            window.returnValue=codesetvo;
            window.close();
       	  }
          
       }else
       {
            //kqItemForm.action="/kq/options/add_item_type.do?b_save=link"; 
            //kqItemForm.target="il_body";  	 
            //kqItemForm.submit();
            var codesetvo=new Object();
            codesetvo.code = $F("code");
            codesetvo.name = $F("name");
            codesetvo.flag = "1";
	        codesetvo.codeitemid = "${kqItemForm.codeitemid}";
	        codesetvo.mes = "${kqItemForm.mes}";
            window.returnValue=codesetvo;
            window.close();
       }
       
     }else
     {
     	var codesetvo=new Object();
        codesetvo.code = $F("code");
        codesetvo.name = $F("name");
        codesetvo.flag = "1";
        codesetvo.codeitemid = "${kqItemForm.codeitemid}";
        codesetvo.mes = "${kqItemForm.mes}";
        window.returnValue=codesetvo;
        window.close();
        //alert("请选择要添加考勤项目的根目录。");
     }   
 }
    
    function toUp(obj) {
    	var va = obj.value;
    	var values = "";
    	for (i = 0; i < va.length; i++) { 
    		var nl = va.substr(i,1);
    		if (nl.match(/^[a-z]$/)) {
    			values += nl.toUpperCase();
    		} else {
    			values += nl;
    		}
    	}
    	
    	obj.value = values;
    }  
         
  </script>

<html:form action="/kq/options/add_item_type">
	<div class="fixedDiv2" style="height: 100%;border: none">
  <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
    <tr height="20">
      <!-- <td width=10 valign="top" class="tableft"></td>
      <td width=130 align=center class="tabcenter">&nbsp;<bean:message key="kq.addItem.sort"/>&nbsp;</td>
      <td width=10 valign="top" class="tabright"></td>
      <td valign="top" class="tabremain" width="250"></td>  --> 
      <td align=center class="TableRow">&nbsp;<bean:message key="kq.addItem.sort"/>&nbsp;</td>            	      
     </tr> 
    <tr>
     <td  class="framestyle9" align="center">
      <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="centers" >     
      	<tr  align="center" class="list3">
              <td colspan="2">
                <br>
                <logic:equal name="kqItemForm" property="mes" value="4">    
                </logic:equal>
                <logic:notEqual name="kqItemForm" property="mes" value="4">   
                 <div align="center"><bean:write name="kqItemForm" property="mess"  filter="true"/></div>
                 </logic:notEqual>
                <br>
              </td>
             </tr>
          <tr class="list3">
            <td align="right" nowrap ><bean:message key="kq.addItem.code.value"/>&nbsp;</td>
            <logic:equal name="kqItemForm" property="mes" value="4">          
               <td align="left"  nowrap valign="center">
                  <html:text styleClass="inputtext" name="kqItemForm" property="code" disabled="true"></html:text>
           	   </td>
           </logic:equal>
           <logic:notEqual name="kqItemForm" property="mes" value="4">   
            <td align="left" nowrap >
              <html:text styleClass="inputtext" name="kqItemForm" property="code" maxlength="${kqItemForm.codelen}" onkeyup="toUp(this)"/>    	      
             </td>
             </logic:notEqual>
            </tr>
           <tr class="list3">
             <td align="right" nowrap ><bean:message key="kq.addItem.name"/>&nbsp; </td>
             <td align="left" nowrap >
               <html:text styleClass="inputtext" name="kqItemForm" property="name" maxlength="50"/>
              </td>
            </tr>                    
            <tr>
            	<td colspan="2">
            		<br/>
            	</td>
            </tr>
          </table>     
        </td>
       </tr>                                                     
       <tr class="list3">
         <td align="center" style="height:35px;" >
	        <input type="button" name="br_return" value="<bean:message key="button.save"/>" class="mybutton" onclick="saves();">     
	       <input type="button" name="br_return" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();">     
        </td>
       </tr>          
    </table>
    </div>
</html:form>