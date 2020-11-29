<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
 
  </head>
  <script language='javascript'>
  	function del()
  	{
  		if(confirm("您真的希望删除选中的方案吗?"))
  		{
  			var ids="";
	    	for(var i=0;i<document.accountingForm.schemeList.options.length;i++)
	    	{
	    		if(document.accountingForm.schemeList.options[i].selected==true)
	    			ids=ids+"/"+document.accountingForm.schemeList.options[i].value;
	    	}
	    	if(ids.length>0)
	    	{
	    		var hashvo=new ParameterSet();	
		    	hashvo.setValue("ids",ids.substring(1));
				var In_paramters="opt=del";
   	  			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3020071006'},hashvo);
  			}
  		}
  	}
    
    function returnInfo(outparamters)
    {
    	var opt=outparamters.getValue("opt");	
    	if(opt=="del")
    	{
    		document.accountingForm.action="/gz/gz_accounting/in_out.do?br_getSchemeList=link";
			document.accountingForm.submit();
    	}
    	else
    	{
    		var relationItemList=outparamters.getValue("relationItemList")
    		var oppositeItemList=outparamters.getValue("oppositeItemList")
    		var arr=new Array();
    		arr[0]=decodeArray(oppositeItemList);
    		arr[1]=decodeArray(relationItemList);
    		returnValue=arr;
		  
		    window.close();
    	}
    	
    }
    
    function decodeArray(theArray)
    {
    	var itemlist = new Array();
    	for(var i=0;i<theArray.length;i++)
  		{
  			itemlist[i]=getDecodeStr(theArray[i]);
  		}
  		return itemlist;
    }
    
    function enter()
    {	
    	var num=0;
    	var id="";
    	for(var i=0;i<document.accountingForm.schemeList.options.length;i++)
    	{
    		if(document.accountingForm.schemeList.options[i].selected==true)
    		{
    			num++;
    			id=document.accountingForm.schemeList.options[i].value;
    		}
    	}
    	
    	if(num>1)
    	{
    		alert("只能选择一个!");
    		return;
    	}
    	if(num==1)
    	{
    			var hashvo=new ParameterSet();	
		    	hashvo.setValue("id",id);
				var In_paramters="opt=enter";
   	  			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3020071006'},hashvo);
    	}
    }
    function updateTitle()
    {
    	var num=0;
    	var id="";
    	var theOldText = '';
    	for(var i=0;i<document.accountingForm.schemeList.options.length;i++)
    	{
    		if(document.accountingForm.schemeList.options[i].selected==true)
    		{
    			num++;
    			id=document.accountingForm.schemeList.options[i].value;
    			theOldText=document.accountingForm.schemeList.options[i].text;
    		}
    	}

    	if(num>1)
    	{
    		alert("只能选择一个!");
    		return;
    	}
   		if(num==1)
    	{
    		//var name=window.prompt("请输入新的方案名称：",theOldText.substring(id.length+1));
    		
    		var arguments=new Array();
			arguments[0]=theOldText.substring(id.length+1);
			arguments[1]="更名";
			var strurl="/gz/gz_accounting/in_out/rename.jsp";
	    	var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
			var name=window.showModalDialog(iframe_url,arguments,"dialogWidth=300px;dialogHeight=190px;resizable=yes;scroll=no;status:no;");  
    		

    		
			if(name&&trim(name).length>0)
			{
				for(var i=0;i<document.accountingForm.schemeList.options.length;i++)
    			{
    				if(document.accountingForm.schemeList.options[i].selected==true)
    				{
    					document.accountingForm.schemeList.options[i].text=id+'.'+name;
    					var hashvo=new ParameterSet();	
		    			hashvo.setValue("id",id);
						hashvo.setValue("newTitle",getEncodeStr(name));
						hashvo.setValue("oper","0");
   	  					var request=new Request({method:'post',asynchronous:false,functionId:'3020071023'},hashvo);
    					break;
    				}
    			}
			}
    	}
    }
function setSeq(selectbox,theAct)
{
  if(selectbox==null)
     return;
   var idx=getSelectedIndex(selectbox);
   if(idx==-1)
     return;
   if(idx==0)
     return;
   var currvalue=selectbox.options[idx].value;
   var currtext=selectbox.options[idx].text;
   var hashvo=new ParameterSet();	
   hashvo.setValue("id",currvalue);
   hashvo.setValue("act",theAct);
   hashvo.setValue("oper","1");
   var request=new Request({method:'post',asynchronous:false,functionId:'3020071023'},hashvo);
   
   
}
  </script>
  <body>
<html:form action="/gz/gz_accounting/in_out"> 
   <table width='100%' border='0'>
   
   <tr><td>&nbsp; </td><td width="90%">
   <table width="100%"   border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
        <tr>
         <td align="left"  class="TableRow" nowrap>
        	&nbsp; 方案名称
         </td>
        </tr>
      </thead>
      
      <tr class="trShallow">
            <td align="center" class="RecordRow" style="padding-top: 3px;border-top: none;padding-bottom: 0px;" nowrap>
   
            <select  name="schemeList" onchange='fireButton()' multiple="multiple"  style="height:280px;width:100%;font-size:9pt" >
			 						
			</select>
            
            </td>
      </tr>
      <tr class="trShallow">
            <td align="center" class="RecordRow" style="border-top:none;padding-top: 2px;padding-bottom: 2px;" nowrap>
            	<Input type='button' name='updateName' value='更名'  class="mybutton" onclick='updateTitle()' disabled />&nbsp;
            	<Input type='button' name='del0' value='删除'  class="mybutton" onclick='del()' disabled />&nbsp;
   				<Input type='button' name='add' value='确定'  class="mybutton" onclick='enter()' disabled />&nbsp;
   				<Input type='button' value='取消'  class="mybutton" onclick='window.close()' />
            </td>
      </tr>
   </table>
   
   </td>
   <td width="5%" >
   		<table width="100%" >
   		<tr>
    					<td align="center">
							<html:button  styleClass="mybutton" property="b_up" onclick="upItem($('schemeList'));setSeq($('schemeList'),'up');">
            		     		<bean:message key="button.previous"/> 
	           				</html:button >
						</td>
    				</tr>
    			<tr>
    					<td height="20">
							&nbsp;
						</td>
    				</tr>	
    				<tr>
    					<td align="center">
							<html:button  styleClass="mybutton" property="b_down" onclick="downItem($('schemeList'));setSeq($('schemeList'),'down');">
            		     		<bean:message key="button.next"/>    
	           				</html:button >	 
						</td>
    				</tr>
   		</table>
   </td>
   </tr></table>
   <script language='javascript'>
   		init();
   	
   	    function fireButton()
   		{
   			var obj=eval("document.accountingForm.del0");
   			obj.disabled=false;
   			obj=eval("document.accountingForm.add");
   			obj.disabled=false;
   			obj=eval("document.accountingForm.updateName");
   			obj.disabled=false;
   		}
   		
   		function init()
   		{
   	  		 var request=new Request({method:'post',asynchronous:false,onSuccess:showSchemeList,functionId:'3020071005'});
   		}
   		
   		function showSchemeList(outparamters)
   		{
   			var schemelist=outparamters.getValue("schemeList");	
	 	 /*   var obj=eval("document.accountingForm.schemeList");
	 	    for(var i=0;i<schemelist.length;i++)
	 	    {
	 	    	var temp=schemelist[i].split("/");
	 	    	var newOption = new Option(temp[0]+"."+temp[1],temp[0]);
	 	    	obj.options[i]=newOption;
	 	    	obj.options[i].attachEvent ('onclick',fireButton);
	 	    	
	 	    }*/
	    	AjaxBind.bind(accountingForm.schemeList,schemelist);
   		}
   		
   		
   </script>
</html:form>
  </body>
</html>