<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript">
    function validate()
    {
	 var tag=false; 
	  var i=0;
          if($F('sels')=="#")
          {
            alert('请选择假期类型!');
            return false;
          }          
          var app_obj=document.getElementById("app_date"); 
          var z1_obj=document.getElementById("z1"); 
          var z3_obj=document.getElementById("z3"); 
          var start_date=z1_obj.value;
          var end_date=z3_obj.value;
          if(app_obj.value==""||z1_obj.value==""||z3_obj.value=="")
          {
             alert('<bean:message key="kq.rest.unull"/>');
             tag=false;
             return tag;
          }else 
          {
             if(!isDate(app_obj.value,"yyyy-MM-dd HH:mm"))
             {
                alert("申请日期时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                tag=false;
                return tag;
             }else  if(!isDate(z1_obj.value,"yyyy-MM-dd HH:mm"))
             {
                 alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                 tag=false;
                 return tag;
             }else if(!isDate(z3_obj.value,"yyyy-MM-dd HH:mm"))
             {
                 alert("结束时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                 tag=false;
                 return tag;
             }else(start_date.length>0&&end_date.length>0)
             {
               var c="起始时间不能大于或等于终止时间！";           
               if(start_date>=end_date)
               {
                  alert(c);
                  tag=false;
                 }else{
                   tag=true;
                 }
              }
            }  
            return tag; 
          
    }       
    function approve()
    {
       if(validate())
       {
           appForm.action="/kq/app_check_in/cancel_app.do?b_approve=link&smflag=03";
           appForm.submit();
       }
    } 
    function reject()
    {
       if(validate())
       {
           appForm.action="/kq/app_check_in/cancel_app.do?b_reject=link&smflag=07";
           appForm.submit();
       }
    } 	
</script>
<script language="javascript">
   var outObject;
   var weeks="";
   var feasts ="";
   var turn_dates="";
   var week_dates="";
   function getdate(tt,flag)
   {
     outObject=tt;     
     var hashvo=new ParameterSet();     
     hashvo.setValue("date",tt.value);  
     hashvo.setValue("flag",flag);     		
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1510010020'},hashvo);
   }
   function showSelect(outparamters)
   { 
     var tes=outparamters.getValue("re_date");     
     outObject.value=tes;
   }
   function getKqCalendarVar()
   {
     var hashvo=new ParameterSet();       		
     var request=new Request({method:'post',onSuccess:setKqCalendarVar,functionId:'15388800008'},hashvo);
   }
   function setKqCalendarVar(outparamters)
   {
       weeks=outparamters.getValue("weeks");  
       feasts=outparamters.getValue("feasts");  
       turn_dates=outparamters.getValue("turn_dates");  
       week_dates=outparamters.getValue("week_dates");  
   }
</script>
<html:form action="/kq/app_check_in/cancel_app" onsubmit="return validate()">
   <br><br>
 <table width="400" border="0" cellpadding="1" cellspacing="0" align="center">
          <tr height="20">
       	    <td  align=center class="TableRow" colspan="4">
       	    	<logic:equal name="appForm" property="table" value="Q11">
          			 撤销加班申请
	       		</logic:equal>
	       		<logic:equal name="appForm" property="table" value="Q13">
	          		 撤销公出申请
	       		</logic:equal>
	       		<logic:equal name="appForm" property="table" value="Q15">
	          		销假申请
	       		</logic:equal>
       	    </td>            	      
          </tr> 
          <tr>
          <td colspan="4" class="framestyle9">
            <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" >   
                <tr><td height="10"></td></tr>   
                <tr>
                   <td align="right" class="tdFontcolor" nowrap >                
                     	<logic:equal name="appForm" property="table" value="Q11">
			          		 加班类型:
			       		</logic:equal>
			       		<logic:equal name="appForm" property="table" value="Q13">
			          		 公出类型:
			       		</logic:equal>
			       		<logic:equal name="appForm" property="table" value="Q15">
			          		 假期类型:
			       		</logic:equal>
                   </td>  
                   <td align="left" class="tdFontcolor" nowrap>                
                        <hrms:optioncollection name="appForm" property="selist" collection="list" />
		                <logic:equal name="appForm" property="table" value="Q11">
							<html:select styleId="q1503" name="appForm"
								property="cancelvo.string(q1103)" size="1" disabled="true">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q13">
							<html:select styleId="q1503" name="appForm"
								property="cancelvo.string(q1303)" size="1" disabled="true">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q15">
							<html:select styleId="q1503" name="appForm"
								property="cancelvo.string(q1503)" size="1" disabled="true">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
						</html:select>
						</logic:equal>
                    </td> 
                </tr>
                <tr>
                   <td align="right" class="tdFontcolor" nowrap >                
                      申请时间:
                   </td>  
                   <td align="left" class="tdFontcolor" nowrap>                
                        <logic:equal name="appForm" property="table" value="Q11">
							<html:text name="appForm" styleId="app_date" disabled="true"
								property="cancelvo.string(q1105)" size="20" maxlength="20"
								styleClass="TEXT4" />
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q13">
							<html:text name="appForm" styleId="app_date" disabled="true"
								property="cancelvo.string(q1305)" size="20" maxlength="20"
								styleClass="TEXT4" />
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q15">
							<html:text name="appForm" styleId="app_date" disabled="true"
								property="cancelvo.string(q1505)" size="20" maxlength="20"
								styleClass="TEXT4" />
						</logic:equal>
                    </td> 
                </tr>
                <tr>
                   <td align="right" class="tdFontcolor" nowrap >                
                      撤销开始时间:
                   </td>  
                   <td align="left" class="tdFontcolor" nowrap>     
                   		<logic:equal name="appForm" property="table" value="Q11">
							<html:text name="appForm" styleId="z1" disabled = "true" property="cancelvo.string(q11z1)"  
								size="20" maxlength="20"   styleClass="TEXT4"/>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q13">
							<html:text name="appForm" styleId="z1" disabled = "true" property="cancelvo.string(q13z1)"  
								size="20" maxlength="20"   styleClass="TEXT4"/>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q15">
							<html:text name="appForm" styleId="z1" disabled = "true" property="cancelvo.string(q15z1)"  
								size="20" maxlength="20"   styleClass="TEXT4"/>
						</logic:equal>           
                    </td> 
                </tr>
                 <tr>
                   <td align="right" class="tdFontcolor" nowrap >                
                     	撤销结束时间:             
                   </td>  
                   <td align="left" class="tdFontcolor" nowrap> 
                   		<logic:equal name="appForm" property="table" value="Q11">
							<logic:equal name="appForm" property="cancelvo.string(q11z5)" value="03">   
	 	   						<html:text name="appForm" styleId="z3" disabled="true" property="cancelvo.string(q11z3)"  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
	          				</logic:equal>
	          				<logic:notEqual name="appForm" property="cancelvo.string(q11z5)" value="03">
	        					<html:text name="appForm" styleId="z3" property="cancelvo.string(q11z3)"  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
	        				</logic:notEqual>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q13">
							<logic:equal name="appForm" property="cancelvo.string(q13z5)" value="03">   
	 	   						<html:text name="appForm" styleId="z3" disabled="true" property="cancelvo.string(q13z3)"  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
	          				</logic:equal>
	          				<logic:notEqual name="appForm" property="cancelvo.string(q13z5)" value="03">
	        					<html:text name="appForm" styleId="z3" property="cancelvo.string(q13z3)"  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
	        				</logic:notEqual>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q15">
							<logic:equal name="appForm" property="cancelvo.string(q15z5)" value="03">   
	 	   						<html:text name="appForm" styleId="z3" disabled="true" property="cancelvo.string(q15z3)"  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
	          				</logic:equal>
	          				<logic:notEqual name="appForm" property="cancelvo.string(q15z5)" value="03">
	        					<html:text name="appForm" styleId="z3" property="cancelvo.string(q15z3)"  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);' styleClass="TEXT4"/>
	        				</logic:notEqual>
						</logic:equal>
                   </td> 
                </tr>
                 <tr>
                   <td align="right" class="tdFontcolor" nowrap >                
                     	撤销原因:
                   </td>  
                   <td align="left" class="tdFontcolor" nowrap>   
                   		<logic:equal name="appForm" property="table" value="Q11">
							<logic:equal name="appForm" property="cancelvo.string(q11z5)" value="03">   
	 	   						<html:textarea disabled="true" name="appForm" property='cancelvo.string(q1107)'  cols="35" rows="4" styleClass="text5"/>
	          				</logic:equal>
	          				<logic:notEqual name="appForm" property="cancelvo.string(q11z5)" value="03">
	        					<html:textarea name="appForm" property='cancelvo.string(q1107)'  cols="35" rows="4" styleClass="text5"/>
	        				</logic:notEqual>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q13">
							<logic:equal name="appForm" property="cancelvo.string(q13z5)" value="03">   
	 	   						<html:textarea disabled="true" name="appForm" property='cancelvo.string(q1307)'  cols="35" rows="4" styleClass="text5"/>
	          				</logic:equal>
	          				<logic:notEqual name="appForm" property="cancelvo.string(q13z5)" value="03">
	        					<html:textarea name="appForm" property='cancelvo.string(q1307)'  cols="35" rows="4" styleClass="text5"/>
	        				</logic:notEqual>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q15">
							<logic:equal name="appForm" property="cancelvo.string(q15z5)" value="03">   
	 	   						<html:textarea disabled="true" name="appForm" property='cancelvo.string(q1507)'  cols="35" rows="4" styleClass="text5"/>
	          				</logic:equal>
	          				<logic:notEqual name="appForm" property="cancelvo.string(q15z5)" value="03">
	        					<html:textarea name="appForm" property='cancelvo.string(q1507)'  cols="35" rows="4" styleClass="text5"/>
	        				</logic:notEqual>
						</logic:equal>
						             
	 	  				
                    </td> 
                </tr>
                <tr>
                   <td align="right" class="tdFontcolor" nowrap >                
                      审批结果:
                   </td>  
                   <td align="left" class="tdFontcolor" nowrap>      
                   		<logic:equal name="appForm" property="table" value="Q11">
							<hrms:codetoname codeid="30" name="appForm" codevalue='cancelvo.string(q11z0)' codeitem="codeitem"/> 
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q13">
							<hrms:codetoname codeid="30" name="appForm" codevalue='cancelvo.string(q13z0)' codeitem="codeitem"/> 
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q15">
							<hrms:codetoname codeid="30" name="appForm" codevalue='cancelvo.string(q15z0)' codeitem="codeitem"/> 
						</logic:equal>                        
                          <bean:write name="codeitem" property="codename" />  
                    </td> 
                </tr>
                <tr>
                   <td align="right" class="tdFontcolor" nowrap >                
                      审批状态:
                   </td>  
                   <td align="left" class="tdFontcolor" nowrap>  
                   		<logic:equal name="appForm" property="table" value="Q11">
							<hrms:codetoname codeid="23" name="appForm" codevalue='cancelvo.string(q11z5)' codeitem="codeitem"/> 
                         	<logic:equal name="appForm" property="cancelvo.string(q11z5)" value="02"> 
	                         	待批
	                         </logic:equal>
	                         <logic:notEqual name="appForm" property="cancelvo.string(q11z5)" value="02">
	                         	<bean:write name="codeitem" property="codename" />
	                         </logic:notEqual>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q13">
							<hrms:codetoname codeid="23" name="appForm" codevalue='cancelvo.string(q13z5)' codeitem="codeitem"/> 
                         	<logic:equal name="appForm" property="cancelvo.string(q13z5)" value="02"> 
	                         	待批
	                         </logic:equal>
	                         <logic:notEqual name="appForm" property="cancelvo.string(q13z5)" value="02">
	                         	<bean:write name="codeitem" property="codename" />
	                         </logic:notEqual>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q15">
							<hrms:codetoname codeid="23" name="appForm" codevalue='cancelvo.string(q15z5)' codeitem="codeitem"/> 
                         	<logic:equal name="appForm" property="cancelvo.string(q15z5)" value="02"> 
	                         	待批
	                         </logic:equal>
	                         <logic:notEqual name="appForm" property="cancelvo.string(q15z5)" value="02">
	                         	<bean:write name="codeitem" property="codename" />
	                         </logic:notEqual>
						</logic:equal>              
                         
                    </td> 
                </tr>
           <tr><td height="10"></td></tr>    
            <tr class="list3">
             <td align="center" colspan="4" style="height:35px;">
             </td>
             </tr>    
            <tr><td height="10"></td></tr>         
 	    </table>	            	
           </td>
           </tr>
 
                      
      </table>
      
      <table align="center">
      	<tr>
      		<td style="height:35px;">
      			<logic:equal name="appForm" property="table" value="Q11">
					<logic:notEqual name="appForm" property="cancelvo.string(q11z5)" value="03">   
	                    <input type="button" name="btnreturn" value='<bean:message key="button.approve"/>' onclick="approve();" class="mybutton">		 	
				 	    <logic:equal name="appForm" property="cancelvo.string(q11z5)" value="02">   
				 	   		<input type="button" name="btnreturn" value='<bean:message key="button.reject"/>' onclick="reject();" class="mybutton">		 	
		              	</logic:equal>
		        	</logic:notEqual>
				</logic:equal>
				<logic:equal name="appForm" property="table" value="Q13">
					<logic:notEqual name="appForm" property="cancelvo.string(q13z5)" value="03">   
	                    <input type="button" name="btnreturn" value='<bean:message key="button.approve"/>' onclick="approve();" class="mybutton">		 	
				 	    <logic:equal name="appForm" property="cancelvo.string(q13z5)" value="02">   
				 	   		<input type="button" name="btnreturn" value='<bean:message key="button.reject"/>' onclick="reject();" class="mybutton">		 	
		              	</logic:equal>
		        	</logic:notEqual>
				</logic:equal>
				<logic:equal name="appForm" property="table" value="Q15">
					<logic:notEqual name="appForm" property="cancelvo.string(q15z5)" value="03">   
	                    <input type="button" name="btnreturn" value='<bean:message key="button.approve"/>' onclick="approve();" class="mybutton">		 	
				 	    <logic:equal name="appForm" property="cancelvo.string(q15z5)" value="02">   
				 	   		<input type="button" name="btnreturn" value='<bean:message key="button.reject"/>' onclick="reject();" class="mybutton">		 	
		              	</logic:equal>
		        	</logic:notEqual>
				</logic:equal>              
	 			<input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">						      
      		</td>
      	</tr>
      </table>
</html:form>



