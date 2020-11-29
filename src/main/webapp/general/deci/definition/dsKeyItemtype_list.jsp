<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript" src="/ajax/common.js"></script>

<script language='javascript' >
  function adds()
  {
    	   target_url="/general/deci/definition/statCutline/searchItemtype.do?br_add=link";
    	   var dw=330,dh=150,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	    	   var return_vo=window.showModalDialog(target_url,1,
	    	   		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	    	   if(return_vo==null){
	    	   		return;
	    	   }
	    	   var sel = return_vo.sel;
	    	   var type = return_vo.type;
	    	   var hashvo = new ParameterSet();
	    	   hashvo.setValue("name",type);
	    	   hashvo.setValue("status",sel);
	    	   var request = new Request({asynchronous:false,onSuccess:adds_ok,functionId:'05601000013'},hashvo);
	  
  }
  
  function adds_ok(outparameters){
	  		dsKeyItemtypeForm.action="/general/deci/definition/statCutline/searchItemtype.do?b_query=link";
	  		dsKeyItemtypeForm.submit();
  }
  
  function edit(str)
  {
    	   target_url="/general/deci/definition/statCutline/searchItemtype.do?b_edit=link&type_id="+str;
	       var dw=330,dh=150,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	       var return_vo=window.showModalDialog(target_url,1,
	       		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	    	  if(return_vo==null){
	    	   	   return;
	    	  }
	    	   var sel = return_vo.sel;
	    	   var type = return_vo.type;
	    	   var typeid = return_vo.typeid;
	    	   var hashvo = new ParameterSet();
	    	   hashvo.setValue("name",type);
	    	   hashvo.setValue("status",sel);
	    	   hashvo.setValue("typeid",typeid);
	    	   var request = new Request({asynchronous:false,onSuccess:adds_ok,functionId:'05601000013'},hashvo);
	  
  }
  
  function returns()
  {
  			dsKeyItemtypeForm.action="/general/deci/definition/statCutline/searchStatCutline.do?b_query=link";      
            dsKeyItemtypeForm.submit();
  }
  
  
  function checkdelete(){
			var hashvo=new ParameterSet();
			var str="";
			for(var i=0;i<document.dsKeyItemtypeForm.elements.length;i++)
			{
				if(document.dsKeyItemtypeForm.elements[i].type=="checkbox")
				{
					if(document.dsKeyItemtypeForm.elements[i].checked==true)
					{
						str+=document.dsKeyItemtypeForm.elements[i+1].value+"/";
					}
				}
			}
			if(str.length==0)
			{
				alert("请选择图例类别！");
				return;
			}else{
				//alert(str);
			    hashvo.setValue("typeids",str);
			   	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:checkresult,functionId:'05601000014'},hashvo);			
			}
	  }
			
	  function checkresult(outparamters){
		   var info = outparamters.getValue("info");
		   //alert(info);
		   if(info == "true"){
		   		//alert("true");
		  	    dsKeyItemtypeForm.action="/general/deci/definition/statCutline/searchItemtype.do?b_query=link";
      			dsKeyItemtypeForm.submit();
		   }else{
		   		var arrays = info.split("/");
		   		var message = "图例类别：";
				for(var i=0 ; i<arrays.length; i++){
					message += arrays[i];
					message += " ";
				}
		   	   message +="存在关联数据，无法删除！";
		   	   alert(message);
		   }
	  }
   
		
</script>
<html:form  action="/general/deci/definition/statCutline/searchItemtype">
<style type="text/css">
    .tdbottom{
    border-left:#C4D8EE 0pt solid;
    border-top:#C4D8EE 0pt solid;
    border-bottom:#C4D8EE 1pt solid;
    border-right: #C4D8EE 0pt solid; 
    white-space:nowrap;
    overflow: hidden;
    }
     .tdbottomright{
    border-left:#C4D8EE 0pt solid;
    border-top:#C4D8EE 0pt solid;
    border-bottom:#C4D8EE 1pt solid;
    border-right: #C4D8EE 1pt solid; 
    white-space:nowrap;
    overflow: hidden;
    }
    .tabletop{
    border-left:#C4D8EE 1pt solid;
    border-top:#C4D8EE 1pt solid;
    border-bottom:#C4D8EE 0pt solid;
    border-right: #C4D8EE 1pt solid; 
    }
    .toptr{
       background-position : center left;
       font-size: 12px;
       height:22px;
	   font-weight: bold;
	   background-color:#f4f7f7;   
       valign:middle; 
    }
</style>
<hrms:themes></hrms:themes>
	<%int i = 0;%>
<table width="60%" border="1" cellspacing="0"  align="center" cellpadding="0" class="tabletop common_border_color">
   	  <thead>
           <tr class="toptr common_background_color">
            <td align="center" class="tdbottomright common_border_color" nowrap>
              <bean:message key="column.select"/>&nbsp;
             </td>
            <td align="center" class="tdbottomright common_border_color" nowrap>
            	<bean:message key="general.defini.cutlineSort"/>&nbsp;
            </td>       

	        <td align="center" class="tdbottomright common_border_color" nowrap>
	               <bean:message key="general.defini.isOk"/>&nbsp;
		    </td>
	        <td align="center" class="tdbottom common_border_color" nowrap>
	            <bean:message key="kq.item.edit"/>&nbsp;          	
		    </td>  	    		        	        	        
           </tr>
   	  </thead>

   	  <hrms:paginationdb id="element" name="dsKeyItemtypeForm" sql_str="dsKeyItemtypeForm.sql_select"  table=""  where_str="dsKeyItemtypeForm.sql_whl"  columns="typeid,name,status"  page_id="pagination" pagerows="15" indexes="indexes">
	  <tr>
            <td align="center" class="tdbottomright common_border_color" nowrap>
               	<hrms:checkmultibox name="dsKeyItemtypeForm" property="pagination.select" value="true"    indexes="indexes"/>&nbsp;
 			<INPUT type="hidden" name="<%=i%>" value='<bean:write name="element" property="typeid" filter="true"/>'>           
            </td>
	        <td align="center" class="tdbottomright common_border_color" nowrap>
	            <bean:write name="element" property="name" filter="false"/>&nbsp;
		    </td> 
		    <td align="center" class="tdbottomright common_border_color" nowrap>	    
		        <logic:equal name="element" property="status" value="1">
		    		<bean:message key="column.law_base.status"/>
		    	</logic:equal>
		    	<logic:equal name="element" property="status" value="0">
		    		<bean:message key="lable.lawfile.invalidation"/>
		    	</logic:equal>
		    </td> 
		    <td align="center" class="tdbottom common_border_color" nowrap>
            	<a href="javascript:edit('<bean:write name="element" property="typeid" filter="true"/>')" ><img src="/images/edit.gif" border=0></a>
            	
	         </td>  	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
</table>
<table  width="60%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="dsKeyItemtypeForm" property="pagination" nameId="dsKeyItemtypeForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table> 



<table  width="60%" align="center">
          <tr>
            <td align="center" height="35px;">
             <input type="button" name="b_saveb" value="<bean:message key="button.insert"/>" class="mybutton" onclick="adds()">     
	   	     <input type="button" name="b_delete" value="<bean:message key="button.delete"/>" class="mybutton" onclick="checkdelete()">      	     
	   	     <input type="button" name="b_return" value="<bean:message key="button.return"/>" class="mybutton" onclick="returns()">   
	   	     
            </td>
          </tr>          
</table>







</html:form>