<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language='javascript'>
	
	
	var info=dialogArguments;
	function sub()
	{
		if(document.f1.right_fields.options.length==0)
		{
			alert(SELECT_SORT_FIELD);
			return;
		}
		
		var right_fields=$('right_fields');
		for(var i=0;i<right_fields.options.length;i++)
		{
			var a_value=right_fields.options[i].value;
			var n=0;
			var a_text="";
			for(var j=0;j<right_fields.options.length;j++)
			{
				if(right_fields.options[j].value==a_value)
				{
					n++;
					a_text=right_fields.options[j].text;
				}
			}
			if(n>1)
			{
				alert(a_text+FIELD_NOT_REPEAT);
				return;
			}
		}
		
		
		
		
		var selectedFields=new Array();
		var fasion="1";
		for(var i=0;i<document.f1.right_fields.options.length;i++)
		{
			selectedFields[i]=document.f1.right_fields.options[i].value;			
		}
		var a_fashion=eval("document.f1.fashion");
		for(var i=0;i<a_fashion.length;i++)
		{
			if(a_fashion[i].checked==true)
				fasion=a_fashion[i].value;
		}
		var hashvo=new ParameterSet();
	    hashvo.setValue("selectedFields",selectedFields); 
		hashvo.setValue("fasion",fasion); 
		var In_paramters="tableName="+info[0]; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000105'},hashvo);			
	}
	

	function returnInfo(outparamters)
	{
		var orderSql=outparamters.getValue("orderSql");
		returnValue=orderSql;
		window.close();
	
	}

function additem2(sourcebox_id,targetbox_id)//dml 2011-04-15
{
  var left_vo,right_vo,vos,i;
  vos= document.getElementsByName(sourcebox_id);

  if(vos==null)
  	return false;
  left_vo=vos[0];
  vos= document.getElementsByName(targetbox_id);  
  if(vos==null)
  	return false;
  right_vo=vos[0];
  var n=0,tt=0;
  
  for(i=0;i<left_vo.options.length;i++)
  {
  	var flag=false;
    if(left_vo.options[i].selected)
    {	tt++;
    	if(right_vo.options.length!=0){
	    	for(var k=0;k<right_vo.options.length;k++){
	    		if(right_vo.options[k].value==left_vo.options[i].value){
	    			n++;
	    			flag=true;
	    			break;
	    		}
	    	}
    	}
    	
    	if(flag){
    		continue;
    	}
        var no = new Option();
    	no.value=left_vo.options[i].value;
    	no.text=left_vo.options[i].text;
    	right_vo.options[right_vo.options.length]=no;
    }
  }
  if(tt>1){
    		if(n>=1&&n<tt){
    			alert("自动过滤相同指标！");
    			return;
    		}
   }
    if(tt==1){
    	if(n==1){
    		alert("不能重复添加相同指标！");
    		return;
    	}
    }
 }
  
</script>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%>       
<base id="mybase" target="_self">
<hrms:themes></hrms:themes>
<form name='f1' >
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top" align="center"  >  &nbsp;
    </td>
    <td>
  <%if(bosflag!=null&&!bosflag.equals("hcm")){
  %>
    <br>
  <% 
    }
  %> 
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="1" class="RecordRow">
   	  <thead>
       <tr>
        <td align="center" class="TableRow" nowrap colspan="3"><bean:message key="label.zp_exam.sort"/>&nbsp;&nbsp;
         </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
   
        <td width="100%" align="center" class="RecordRow" style="border-right: none;" nowrap>
          <table>
            <tr>
             <td align="center"  width="46%">
              <table width="100%">
                 <tr>
                  <td width="100%" align="left">
                      <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                
                <tr>
                 <td align="center">
                  <select name="left_fields" multiple="multiple" ondblclick="additem2('left_fields','right_fields');" style="height:230px;width:100%;font-size:9pt">
                   </select>
                   </td>
                  </tr>
                 </table>
                </td>
               
               <td width="8%" align="center">  
	            <html:button  styleClass="mybutton" property="b_addfield" onclick="additem2('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	            </html:button>
	            <br>
	            <br>
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	            </html:button>	
                </td>         
                <td width="46%" align="center">

                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                  
                     <bean:message key="label.query.selectedsortfield"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	            
 		     		<select name="right_fields" multiple="multiple" size="10"  ondblclick="removeitem('right_fields');" style="height:185px;width:100%;font-size:9pt">
                     </select>            
 		                 
                   </td>
                  </tr>
                  <tr >
                  	<td width='100%' align="left" >
                  		<fieldset align="center" style="width:100%;">
    					<legend ><bean:message key="label.query.sortFashion"/></legend>
                  	
	                  		<input name="fashion" type="radio" value="1" checked><bean:message key="label.query.sortBase"/>&nbsp;&nbsp;
	      				    <input type="radio" name="fashion" value="0"><bean:message key="label.query.sortDesc"/>
      				    
      				    </fieldset>	
                  		
                  	</td>
                  </tr>
                 </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>      
          <td align="center" class="RecordRow" nowrap  colspan="3">

	  			
            	<input type='button'  value='<bean:message key="kq.formula.true"/>' onclick='sub()' class="mybutton"  />
	         
       
         </td>
        </tr>   
     </table>
     
     
   </td>
  </tr>
</table>

<input type='hidden' name='tableName' />

</form>

<script language="JavaScript">
  var infos=dialogArguments; 
  init();
  
  function init()
  {
  	var a_select=eval('document.f1.left_fields')
  	document.f1.tableName.value=infos[0];
  	for(var i=0;i<infos[1].length;i++)
  	{
  		var oOption = document.createElement("OPTION");
		oOption.text=infos[1][i][1];
		oOption.value=infos[1][i][0];
		a_select.add(oOption);
  	}
  }
</script>