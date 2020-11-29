<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String order =(String)request.getParameter("order");
 %>
<script language="javascript">
   function savefield()
  {  	  
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");
     if(vos.length==0)
     {
       if('<bean:write name="sysinfosortForm" property="tag" filter="true"/>'=='set_a')
       	alert("已选子集项不能为空！");
       else
       	alert("已选指标项不能为空！");
       return false;
     }else
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
          for(var j=i+1;j<vos.length;j++)
          {
          	if(valueS.toUpperCase()==vos.options[j].value.toUpperCase())
          	{
          		if('<bean:write name="sysinfosortForm" property="tag" filter="true"/>'=='set_a')
          			alert("有相同子集存在，请重新选择");
          		else
          			alert("有相同指标存在，请重新选择");
          		return false;
          	}
          }
        }       
     }
    var code_fields=new Array();        
    for(var i=0;i<vos.length;i++)
    {
      var valueS=vos.options[i].value;          
      code_fields[i]=valueS;
    }       
     hashvo.setValue("subclass_value",code_fields); 
     hashvo.setValue("tag",'${sysinfosortForm.tag}');
     hashvo.setValue("tagname",'${sysinfosortForm.tagname}');
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1012010004'},hashvo);
   }	
   function showSelect(outparamters)
   { 
        var mess=outparamters.getValue("mess");        
        var thevo=new Object();
		thevo.mess=mess;
		if(parent.Ext){//弹窗回调    wangb 20190319
			var win = parent.Ext.getCmp('select_field');
			win.return_vo = thevo;
			win.close();
		}else{
			window.returnValue=thevo;
			window.close(); 
		}
   }  
   //关闭弹窗  wangb 20190319
   function winclose(){
   		if(parent.Ext){
   			parent.Ext.getCmp('select_field').close();
   			return;
   		}
  		window.close();
   }
</script>
<html:form action="/system/param/sysinfosort">
<table width="530" border="0" cellspacing="0"  align="center" cellpadding="0" class="">
   	  <thead>
           <tr>
            <td align="left" class="TableRow_lrt" nowrap colspan="3">
            
            <logic:equal name="sysinfosortForm" property="tag" value="set_a">
            	<bean:message key="system.param.sysinfosort.selsubset"/>
            </logic:equal>
            <logic:notEqual name="sysinfosortForm" property="tag" value="set_a">
            	<bean:message key="static.select"/>&nbsp;&nbsp;
            </logic:notEqual>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                    <logic:equal name="sysinfosortForm" property="tag" value="set_a">
		            	<bean:message key="system.param.sysinfosort.bsubset"/>&nbsp;&nbsp;
		            </logic:equal>
		            <logic:notEqual name="sysinfosortForm" property="tag" value="set_a">
		            	<bean:message key="static.target"/>&nbsp;&nbsp;
		            </logic:notEqual>
                    </td>
                    </tr>                   
                   <tr>
                    <td align="center">
                      <hrms:optioncollection name="sysinfosortForm" property="subclasslist" collection="list"/> 
     	              <html:select property="left_fields" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt"  ondblclick="additem('left_fields','right_fields');">
                        <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
                     </html:select> 
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="removeitem('right_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                
                <td width="46%" align="center">
                 
                 
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                  <logic:equal name="sysinfosortForm" property="tag" value="set_a">
		            	<bean:message key="system.param.sysinfosort.ysubset"/>&nbsp;&nbsp;
		            </logic:equal>
		            <logic:notEqual name="sysinfosortForm" property="tag" value="set_a">
		            	<bean:message key="static.ytarget"/>&nbsp;&nbsp;
		            </logic:notEqual>
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
                  <hrms:optioncollection name="sysinfosortForm" property="selectsubclass" collection="selectedlist"/> 
     	             <html:select property="right_fields" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt"  styleId="right" ondblclick="removeitem('right_fields');">
                        <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     </html:select>   
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="4%" align="center">
                        <%if(order==null||!order.equals("no")) {%>
							<html:button styleClass="smallbutton" property="b_up" onclick="upItem($('right_fields'));">
								<bean:message key="button.previous" />
							</html:button>
							<html:button styleClass="smallbutton" property="b_down" onclick="downItem($('right_fields'));" style="margin-top:30px;">
								<bean:message key="button.next" />
							</html:button>
						<%} %>	
						</td>                              
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRowP" nowrap  colspan="3" style="height: 35px">
               <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick=" savefield();">
	     <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick="winclose();">
          </td>
          </tr>
</table>
</html:form>
