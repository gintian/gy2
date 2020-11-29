<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    String callBackFunc = "";
    if(request.getParameter("callBackfunc")!=null){
        callBackFunc = request.getParameter("callBackfunc");
    }
%>
<html>
  <head>
 
  </head>
  <script language='javascript'>
  	function del()
  	{
  		//在删除时判断是否有选中的选项
  		var num=0;
    	for(var i=0;i<document.commonQueryForm.right_fields.options.length;i++)
    	{
    		if(document.commonQueryForm.right_fields.options[i].selected==true)
    		{
    			num++;
    		}
    	}
    	
    	if(num == 0)
    	{
    		alert("请选择一个常用条件进行删除");
    		return;
    	}
  		if(confirm("确认要删除吗？"))
  		{
  			document.commonQueryForm.action="/general/query/common/complexCondition.do?b_delCondition=del&callBackfunc=symbol";
  			document.commonQueryForm.submit();
  		}
  	}
    
    
    function enter()
    {
    	var num=0;
    	var id="";
    	for(var i=0;i<document.commonQueryForm.right_fields.options.length;i++)
    	{
    		if(document.commonQueryForm.right_fields.options[i].selected==true)
    		{
    			id=document.commonQueryForm.right_fields.options[i].value;
    			num++;
    		}
    	}
    	
    	if(num>1)
    	{
    		alert("只能选择一个常用条件");
    		return;
    	}
    	if(num==1)
    	{
    		var In_paramters="id="+id; 	
			var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:returnInfo,functionId:'0202011015'});		
	    	   
    	
    //		document.commonQueryForm.action="/general/query/common/complexCondition.do?b_delCondition=select";
  	//		document.commonQueryForm.submit();
  			
  			
    	}
    }
    
    function returnInfo(outparamters)
    {
    	var info = outparamters.getValue("expr");
        if(window.showModalDialog){
            parent.window.returnValue=getDecodeStr(info);
        }else{
            eval(parent.opener.window.<%=callBackFunc%>)(getDecodeStr(info));
        }
        parent.window.close();
    }
    
    
   
    
  
  </script>
  <body>
<html:form action="/general/query/common/complexCondition"> 
   <table width='100%'>
   
   <tr><td align="center">
   <table width="290" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
        <tr>
         <td align="left"  class="TableRow" nowrap>
        	&nbsp; 常用条件
         </td>
        </tr>
      </thead>
      
      <tr class="trShallow">
            <td align="center"   class="RecordRow" nowrap>
            <html:select name="commonQueryForm" property="right_fields"  multiple="multiple"  style="margin-top:5px;height:280px;width:100%;font-size:9pt" >
			 	<html:optionsCollection property="selectedCondlist" value="dataValue" label="dataName" />
			</html:select>
            
            </td>
      </tr>
      <tr class="trShallow">
            <td align="center"  style="padding-top:3px;padding-bottom:3px;" class="RecordRow" nowrap>
   				<Input type='button' value='确定'  class="mybutton" onclick='enter()' />&nbsp;
   				<Input type='button' value='删除'  class="mybutton" onclick='del()' />&nbsp;
   				<Input type='button' value='取消'  class="mybutton" onclick='parent.window.close()' />
            </td>
      </tr>
   </table>
   
   </td></tr></table>
</html:form>
  </body>
</html>
