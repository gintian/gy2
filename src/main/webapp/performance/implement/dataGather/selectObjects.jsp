<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<hrms:themes />
<html>
  <head>
    
  </head>
  <script language="JavaScript" src="/js/validate.js"></script>
  <script language="JavaScript" src="/js/constant.js"></script>
  <script language='javascript' >
  
  function up_down(obj_name,opt)
  {
  	var objs=document.getElementsByName(obj_name);
  	var obj=objs[0];
  	var num=0;
  	var no = new Option();
  	
  	for(var i=0;i<obj.options.length;i++)
  	{
  		if(obj.options[i].selected)
  			num++;
  	}		
  	if(num>1)
  	{
  		alert(P_I_INF11+"!");
  		return;
  	}
  	if(num==0)
  	{
	  	alert(P_I_INF12+"!");
  		return;
  	}
  	
  	for(var i=0;i<obj.options.length;i++)
  	{
  		if(obj.options[i].selected)
  		{
  			if(opt==1&&i>0)
  			{
	  			no.value=obj.options[i-1].value;
		    	no.text=obj.options[i-1].text;
		    	obj.options[i-1].value=obj.options[i].value;
		    	obj.options[i-1].text=obj.options[i].text;
	  			obj.options[i].value=no.value;
		    	obj.options[i].text=no.text;
		    	obj.options[i].selected=false;
		    	obj.options[i-1].selected=true;
		    	break;
		    }
		    else if(opt==2&&i<obj.options.length-1)
		    {
		    	no.value=obj.options[i].value;
		    	no.text=obj.options[i].text;
		    	obj.options[i].value=obj.options[i+1].value;
		    	obj.options[i].text=obj.options[i+1].text;
	  			obj.options[i+1].value=no.value;
		    	obj.options[i+1].text=no.text;
		    	obj.options[i].selected=false;
		    	obj.options[i+1].selected=true;
		    	break;
		    }
  		}
  	}
  }
  
  function enter()
  {
    	var objs=document.getElementsByName("right_fields");
  		var objectids="";
  		for(var i=0;i<objs[0].options.length;i++)
  		{
  			objectids+=","+objs[0].options[i].value;
  		}
  		
  		if(objectids.length>0)
  		{
	        var weighturl="/performance/implement/dataGather/readCard.jsp?readerType=0`objs="+objectids.substring(1);
	 		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(weighturl);
	 		var resultVo= window.showModalDialog(iframe_url, 'pointpowerset_win',"dialogWidth:480px; dialogHeight:305px;resizable:yes;center:yes;scroll:yes;status:no");
	        returnValue=1
	        window.close();	
  		}
  }
  
  
  
  </script>
  <script language="JavaScript" src="/js/validate.js"></script>
  <script language="JavaScript" src="/js/function.js"></script>
  <body>
   <html:form action="/performance/implement/dataGather"> 
    
          
 
 <table width="100%" border="0" >
 <tr>
  <td>&nbsp;</td>
  <td align="left"  nowrap><br>
    <bean:message key="jx.eval.khobj1"/> 
   <br>     
   <select name="left_fields" multiple="multiple" ondblclick="additem2('left_fields','right_fields');" style="height:230px;width:230;font-size:9pt">
   			<logic:iterate  id="element" name="dataGatherForm" property="objectsList" >
   				<option value='<bean:write name="element" property="object_id" filter="true"/>'><bean:write name="element" property="a0101" filter="true"/> <logic:equal name="dataGatherForm" property="objectType"  value="2">(<bean:write name="element" property="e0122" filter="true"/>)</logic:equal></option>
   			</logic:iterate>
   </select>
                
  </td>
  <td  align="center">  
	            <html:button  styleClass="mybutton" property="b_addfield" onclick="additem2('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	            </html:button>
	            <br>
	            <br>
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	            </html:button>	
   </td>         
   <td  align="left"><br>
    <bean:message key="jx.eval.khobj2"/>
    <br>
 	<select name="right_fields" multiple="multiple" size="10"  ondblclick="removeitem('right_fields');" style="height:230px;width:230;font-size:9pt">
    </select>            
 		                     
    </td>  
    
    <td  align="center">  
	            <html:button  styleClass="mybutton" property="b_addfield" onclick="up_down('right_fields',1);">
            		     <bean:message key="kh.field.move_up"/>
	            </html:button>
	            <br>
	            <br>
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="up_down('right_fields',2);">
            		    <bean:message key="kh.field.move_down"/>
	            </html:button>	
   </td>         
                 
   </tr>
   <tr>      
          <td align="center"  nowrap  colspan="5">

	  	
       	   <html:button  styleClass="mybutton" property="b_save" onclick="enter()">
            		    <bean:message key="button.ok"/>
	        </html:button>
       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <html:button  styleClass="mybutton" property="b_save" onclick="javascript:window.close()">
            		    <bean:message key="button.cancel"/>
	        </html:button>
         </td>
    </tr>   
  </table>  
                  
       
    
    
    
    
    
    
    
   </html:form>
  </body>
</html>
