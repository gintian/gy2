<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<script language="JavaScript" src="/js/popcalendar.js"></script>
	<script language="JavaScript" src="/js/constant.js"></script>
	<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
	<style>
		.TEXT_NB {
			BACKGROUND-COLOR:transparent;
			BORDER-BOTTOM: #94B6E6 1pt solid; 
			BORDER-LEFT: medium none; 
			BORDER-RIGHT: medium none; 
			BORDER-TOP: medium none;
		}
		#calendar{
			position:absolute;
			left:120 !important;
			top:50 !important;
			z-index:999;
		}
   </style>
<html>
	<head>
	<title><bean:message key="menu.gz.batch.update"/></title>
	<SCRIPT LANGUAGE=javascript>
	
	function sub()
	{
		var objarr=new Array();
		
		var obj=document.getElementsByName("data.value");		
		if(trim(obj[0].value).length==0)
		{
			alert(PLEASE_CONFIG_VALUE+"!");
			return;
		}
		
		objarr[0]=obj[0].value;
		var temps=document.positionDemandForm.field.value.split('#');
		if(temps[1]=='N')
		{
			 var myReg =/^(-?\d+)(\.\d+)?$/
			 if(!myReg.test(obj[0].value)) 
			 {
							alert(PLEASE_INPUT_NUMBER+"！");
							return;
			 }
						 
			if(temps[3]=='0')
			{			
				if(obj[0].value.indexOf(".")!=-1)
				{
							 		alert(VALUE_MUST_BE_INTEGER+"！");
									return;
				}
			}
			if(obj[0].value*1>2147483647||obj[0].value*1<-2147483648)
			{
				alert(VALUE_MUST_BETWEEN);
				return;
			}
						
		}
		if(temps[1]=='A'&&temps[4]=='0')
		{
					if(IsOverStrLength(obj[0].value,temps[2]))
					{
								alert(OBJECTCARDINFO12);
								return;
					}
		}
		objarr[1]=temps[0];
		objarr[2]=temps[1];
		objarr[3]=temps[3];
		returnValue=objarr;
		window.close();
	
	}
	
	
	function setValue()
	{
	
    	//itemid#itemtype#itemlength#decimalwidth#codesetid
		var obj=eval('n');
		if(document.positionDemandForm.field.value!='')
		{
			var temps=document.positionDemandForm.field.value.split("#");
			
			if(temps[1]=='A')
			{
				if(temps[4]=='0')
				{
					obj.innerHTML="<input type='text' name='data.value' class='TEXT_NB common_border_color'  size='20' maxlength='"+temps[2]+"'  value='' /> ";
				}
				else
				{
					var context="<input type='hidden' name='data.value' class='TEXT_NB common_border_color'    value='' /> ";
					context=context+"<input type='text' name='data.viewvalue'  value=''  class='TEXT4' size='20'   readonly='true'   /> ";
					context=context+"<span><img  src='/images/code.gif' style='position:relative;top:5px;' onclick='javascript:openInputCodeDialog(\""+temps[4]+"\",\"data.viewvalue\");'/></span>";
					obj.innerHTML=context;
				}
			}
			else if(temps[1]=='D')
			{
				obj.innerHTML="<input type='text' name='data.value' class='TEXT_NB common_border_color'  size='20'   readonly  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'  value='' /> ";
				var calendar = document.getElementById("calendar");
				calendar.style.left = 100;
				calendar.style.top = 10;
			}
			else if(temps[1]=='N')
			{
			
				obj.innerHTML="<input type='text' name='data.value' class='TEXT_NB common_border_color'  size='20'    value='' /> ";
			
			}
		}
		else
		{
			obj.innerHTML="";
		}
	}
	
	
	function initData()
	{
		document.positionDemandForm.field.options[0].selected=true;
		document.positionDemandForm.field.fireEvent("onChange");
	}
	
	
	function clears()
	{
		var obj=document.getElementsByName("data.value");		
		obj[0].value="";
		
		var obj2=document.getElementsByName("data.viewvalue");
		if(obj2&&obj2.length==1)
		{
			
			obj2[0].value="";
		}
	}
	
	</script>
	
	
	</head>
	<hrms:themes></hrms:themes>
	<body>
	<base id="mybase" target="_self">
	<html:form action="/hire/demandPlan/positionDemand/positionDemandTree">
	<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    if(bosflag!=null&&!bosflag.equalsIgnoreCase("hcm")){
	%>
	<br>
	<%
	}
	%>
      <table width="430" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable" style="margin-top:0px;margin-left:0px;">
          <tr height="20">
       		<td class="TableRow"><bean:message key="menu.gz.batch.update"/>&nbsp;</td>         	      
          </tr> 
          <tr>
            <td colspan="4" class="framestyle">
            	
                  <table border="0"  cellspacing="10" width="430"  cellpadding="0" align="center">
                      <tr> 
                        <td valign='bottom' height="35" align="right"  nowrap > 
                       	  <bean:message key="kh.field.field_n"/>&nbsp;&nbsp;
                        </td>
                        <td valign='bottom'  align="left"  nowrap >
                        	<select name='field' onChange='setValue()' >	                           
		                       <logic:iterate id="element" name="positionDemandForm" property="fieldList"  offset="0"> 
		                       			<option value='<bean:write name="element" property="itemid"   filter="false"/>#<bean:write name="element" property="itemtype"  filter="false"/>#<bean:write name="element" property="itemlength"   filter="false"/>#<bean:write name="element"  property="decimalwidth" filter="false"/>#<bean:write name="element" property="codesetid"   filter="false"/>'><bean:write name="element" property="itemdesc"   filter="false"/></option>
		                       </logic:iterate>
                       		</select>
                        </td>
                      </tr>
                      
                      
                      <tr> 
                        <td height="35" valign='top'   align="right" nowrap > 
                       	  <bean:message key="hire.field.value"/>&nbsp;&nbsp;
                        </td>
                        <td align="left" valign='top'  nowrap >
                        	<div id='n'>
								<div id='can'></div>
                        	</div>
                        <!--   <input type='text' name='rqlr' class='TEXT_NB'  size='25'   readonly   onclick='popUpCalendar(this,this, dateFormat,"","",true,false)'  value='' /> &nbsp;  -->
                     
                        </td>
                      </tr>
					   
                     <!--  <tr> 
                        <td align="center" nowrap  colspan="2">&nbsp;</td>
                      </tr>
                      
 -->
                      
                    </table></td>
                </tr>
                       <tr> 
                        <td align="center" nowrap  colspan="2" height="35px;">
                        <input type="button" name="b_update" value=" <bean:message key="button.ok"/> "  onclick='sub()'  class="mybutton" style="margin-top:5px;"> 
           
                         <input type="button" value=" <bean:message key="button.clear"/> "  onclick='clears()' class="mybutton" style="margin-top:5px;">          
                 	     
                        <br>&nbsp;
                        </td>
                      </tr>
              </table>	            				
			


  			</td>
          </tr>          
      </table>
	</html:form>
	</body>
	
	<SCRIPT LANGUAGE='JAVASCRIPT'>
		initData();
	</SCRIPT>
	
</html>