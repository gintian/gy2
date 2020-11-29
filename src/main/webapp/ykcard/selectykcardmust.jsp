<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.ykcard.CardConstantForm" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<script language="JavaScript" src="/js/function.js"></script>
<%
                         CardConstantForm cardConstantForm=(CardConstantForm)session.getAttribute("cardConstantForm");
                         ArrayList mustmesslist=cardConstantForm.getMustmesslist();
                         String showtitle=request.getParameter("showtitle");
                      %>
<script language=JavaScript> 

  function saveCode()
  {  	  
     
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");
     if(vos==null)
     {
       alert("已选花名册不能为空！");
       return false;
     }else
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
        }       
     }
     hashvo.setValue("code_fields",code_fields);        
     hashvo.setValue("codeitemid","${cardConstantForm.codeitemid}"); 
     hashvo.setValue("codesetname","${cardConstantForm.codesetname}");      
     hashvo.setValue("mustflag","${cardConstantForm.mustflag}");
     hashvo.setValue("codename","${cardConstantForm.codename}");   
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1010031003'},hashvo);
   }	
   function showSelect(outparamters)
   { 
     var types=outparamters.getValue("types");          
     if(types=="ok")
     {
        alert("编辑成功");
        var mess=outparamters.getValue("mess");
        var thevo=new Object();
		thevo.mess=mess;
		//window.returnValue=thevo;
		//window.close();      
		winclose(thevo);  
     }else
     {
        alert("编辑失败");
     }     
   }  
   //关闭弹窗回方法  wangb 20190318
	function winclose(return_vo){
		if(parent.Ext){
			var win = parent.Ext.getCmp('selectSalaryTable');
			win.return_vo = return_vo;
			win.close();
		}else{
			window.returnValue = return_vo;
        	window.close();
		}
	}
   </script> 
<html:form action="/ykcard/mustconstantset">
<table width="490" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    <%if(StringUtils.isBlank(showtitle)||"1".equals(showtitle)){%>
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		选择花名册&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
    <%}%>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                        备选花名册&nbsp;&nbsp;
                    </td>
                    </tr>                    
                   <tr>
                       <td align="center">                      
                       
 		        <html:select name="cardConstantForm" property="left_fields" multiple="multiple" size="10" ondblclick="additem2('left_fields','code_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="hmusterlist" value="dataValue" label="dataName"/>   		      
 		       </html:select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="additem2('left_fields','code_fields');">
            		 <bean:message key="button.setfield.addfield"/> 
	           </html:button>
	           <br>
	           <br>
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="removeitem('code_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button>	     
                </td>         
                
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     已选花名册&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     <html:select name="cardConstantForm" property="code_fields" styleId="right"  multiple="multiple" size="10" ondblclick="removeitem('code_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="mustfieldlist" value="dataValue" label="dataName"/>   		      
 		        </html:select>
                  </td>
                  </tr>
                  </table>             
                </td>
                
                <td width="4%" align="center">
                  
	             <html:button  styleClass="smallbutton" property="b_up" onclick="upItem($('code_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button>
	           <br>
	           <br>
	           <html:button  styleClass="smallbutton" property="b_down" onclick="downItem($('code_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button>
	           	     
                </td>                                
                </tr>
              </table>             
            </td>
          </tr>
            
          <tr>
          <td align="center" class="RecordRow" nowrap style="height:35px;">
          
             <input type="button" name="btnreturn" value='确定' class="mybutton" onclick=" saveCode();">
	     <input type="button" name="btnreturn" value='关闭' class="mybutton" onclick="winclose();">
          </td>
          </tr>   
</table>
</html:form>
<script language=JavaScript> 
// linbz 20160909 缺陷22618 在重新选择花名册时，清空之前选择的花名册
function init(){
	var mustmesslist = "<%=mustmesslist%>";
	mustmesslist = replaceAll( mustmesslist, ",", "" );
	mustmesslist = replaceAll( mustmesslist, "[", "" );
	mustmesslist = replaceAll( mustmesslist, "]", "" );
	mustmesslist = replaceAll( mustmesslist, " ", "" );
	if(mustmesslist.length==0 && mustmesslist==""){
		document.getElementById("right").innerHTML="";
	}
}
init();
</script>
