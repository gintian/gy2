<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="javascript">
  function saveCode()
  {
     var hashvo=new ParameterSet();          
     var vos= document.getElementsByName("code_fields"); 
     if(vos==null || vos[0].length==0)
     {
  	return; 
     }
     var codevo=vos[0];      
     var code_fields=new Array();        
     for(var i=0;i<codevo.options.length;i++)
     {
          var valueS=codevo.options[i].value;          
          code_fields[i]=valueS;
     }       
    
     hashvo.setValue("code_fields",code_fields);        
     hashvo.setValue("table","${adjustcodeFrom.table}");   
     hashvo.setValue("isSave","${adjustcodeFrom.isSave}");           	
     var request=new Request({method:'post',onSuccess:returninfo,functionId:'15204110033'},hashvo);
     
   }
   function returninfo(outparamters)
   {
      var types=outparamters.getValue("types");          
      if(types=="ok")
      {
        alert("操作成功!");
        window.returnValue="ok";
       window.close();
      }else
      {
        alert("操作失败");
      }
   }
   function return_brack()
   {
       adjustcodeFrom.action="/kq/options/adjustcode/adjustcode.do?br_search=link";
       adjustcodeFrom.submit();  
   }
</script>
<html:form action="/kq/options/adjustcode/adjustcode">
	<div class="fixedDiv2" style="height: 100%;border: none">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		 调整指标顺序&nbsp;&nbsp;
		 <html:hidden name="adjustcodeFrom" property="table" styleClass="text"/> 
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>
                 <td align="center"  width="90%">
                   <table align="center" width="100%">               
                   <tr>
                       <td align="center">
                        <html:select name="adjustcodeFrom" property="code_fields" multiple="multiple" size="10"  style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="field_list" value="dataValue" label="dataName"/>   		      
 		        </html:select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                 <td width="4%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('code_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('code_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>
                                </tr>
              </table>             
            </td>
          </tr>
            
          <tr>
          <td align="center" class="RecordRow" nowrap style="height:35px;border: none">
          <input type="button" name="btnreturn" value='确定' class="mybutton" onclick=" saveCode('${adjustcodeFrom.flag}');">
	      <logic:notEqual name="adjustcodeFrom" property="flag" value="1">
	      <input type="button" name="btnreturn" value='返回' class="mybutton" onclick=" return_brack();">
          </logic:notEqual>
          <logic:equal name="adjustcodeFrom" property="flag" value="1">
           <input type="button" name="tdf" value="<bean:message key="button.close"/>"  class="mybutton" onclick="window.close();">
          </logic:equal>  
          </td>
          </tr>   
</table>
</div>
</html:form>