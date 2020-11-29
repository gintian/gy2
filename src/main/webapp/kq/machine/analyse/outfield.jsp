<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 height: 200px;
 /*line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
 margin:2px;*/

}

.fixedDiv_3 
{ 
  overflow: auto;
  position:absolute;top:10px;left:10px;width:100%;height:300px;
}

</STYLE>
<script language="javascript">
  function saveCode()
  {
     var mes=$F('outfieldsname');
     if(mes==null||mes.length<=0)
     {
       　alert("请选择指标！");
        return false;      
     }      
     var thevo=new Object();
	 thevo.flag="true";
	 thevo.fields=mes;
     window.returnValue=thevo;
	 window.close();  
  }
  var checkflag = "false";
  function selAll()
  {
      var len=document.dataAnalyseForm.elements.length;
       var i;
       
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.dataAnalyseForm.elements[i].type=="checkbox")
            {
              document.dataAnalyseForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.dataAnalyseForm.elements[i].type=="checkbox")
          {
            document.dataAnalyseForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  } 
</script>
<html:form action="/kq/machine/analyse/analyse_result">
<% int i=0; %>
<div class="fixedDiv_3 RecordRow" style="height:300px">
<table width="80%" cellspacing="1"  align="center" cellpadding="1" style="border-collapse: collapse;">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" colspan="2" nowrap>		
            　　导出指标
            </td>            	        	        	        
           </tr>
   	  </thead>

          <logic:iterate id="element" name="dataAnalyseForm"  property="outfieldlist" indexId="index">                                
                     <%
          if(i%2==0){ 
          %>
          <tr class="" onClick="">
          <%
          }else{
          %>
          <tr class="" onClick="">
          <%}i++; 
          %>  
           <td align="center" class="RecordRow" style="border-right: none;border-top: none;">
              <input type="checkbox" name="outfieldsname" value="${element.dataValue}">
           </td>
           <td class="RecordRow" style="border-top: none;"> 
              <bean:write name="element" property="dataName"/>
           </td>
          </tr>
         </logic:iterate>
  </table>
  </div>
  <div align="center" style="margin-top:5px;position:absolute;top:320px;left:180px;">
    <input type="button" name="b_delete" value='<bean:message key="label.query.selectall"/>' class="mybutton" onclick="selAll()">
    <input type="button" name="btnreturn" value='确定' class="mybutton" onclick=" saveCode();">
    <input type="button" name="tdf" value="<bean:message key="button.close"/>"  class="mybutton" onclick="window.close();">
  </div>
</html:form>