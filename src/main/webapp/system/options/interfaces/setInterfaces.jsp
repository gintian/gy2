<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  </head>
  <hrms:themes></hrms:themes>
  <script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<script type="text/javascript" language="javascript">
function getbasefield(mytarget){
    return_vo ='';
    var theUrl = '/system/options/otherparam/global_employeeitemtree.do?b_query=link&param=root&froms=db&name=usr&input=2';
    Ext.create('Ext.window.Window', {
        id:'global_employeeitemtree',
        height: 430,
        width: 300,
		resizable:false,
		modal:true,
		autoScroll:false,
		autoShow:true,
		// closeAction:'destory',
		html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
		renderTo:Ext.getBody(),
		listeners:{
            'close':function () {
                if (return_vo) {
                    var codeitemid, codetext;
                    codeitemid = return_vo.codeitemid;
                    codetext = return_vo.codetext;
                    var oldInputs = document.getElementsByName(mytarget);
                    var oldobj = oldInputs[0];
                    target_name = oldobj.name;
                    var hidden_name = target_name.replace(".viewvalue", ".value");
                    var hiddenInputs = document.getElementsByName(hidden_name);
                    if (hiddenInputs != null) {
                        hiddenobj = hiddenInputs[0];
                        hiddenobj.value = codeitemid;
                    }
                    oldobj.value = codetext;
                }
		}}

    }).show();
	// var return_vo= window.showModalDialog("/system/options/otherparam/global_employeeitemtree.do?b_query=link&param=root&froms=db&name=usr&input=2", false,
     //    "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");

}
function save()
{
//setInterfacesForm.action="/system/options/interfaces/employInterfaces.do?b_save=link";
//setInterfacesForm.submit();
setInterfacesForm.action="/system/options/interfaces/setInterfaces.do?b_save=link";
setInterfacesForm.submit();
}
function checkBoximpmode(obj) { 
    if(obj.checked){
    	document.setInterfacesForm.impmode.value="1";
    }else{
    	document.setInterfacesForm.impmode.value="0";
    }
}
function checkBoxexpmode(obj) { 
    if(obj.checked){
    	document.setInterfacesForm.expmode.value="1";
    }else{
    	document.setInterfacesForm.expmode.value="0";
    }
}
function checkMarker(obj) {
    if(obj.checked){
    	document.setInterfacesForm.marker.value="1";
    }else{
    	document.setInterfacesForm.marker.value="0";
    }
}
function clement(value)
{
	if(value=="1")
	{
		if(value1.style.display=="none")
		{
			value1.style.display="block";
			if(value6.style.display=="none")
		{
			value6.style.display="block";
			value7.style.display="none";
			return;
		}else if(value6.style.display=="block")
		{
			value6.style.display="none";
			value7.style.display="none";
			return;
		}
			return;
		}else if(value1.style.display=="block")
		{
			value1.style.display="none";
			if(value6.style.display=="none")
			{
				value6.style.display="block";
				value7.style.display="none";
				return;
			}else if(value6.style.display=="block")
			{
				value6.style.display="none";
				value7.style.display="block";
				return;
			}
			return;
		}
	}
	if(value=="2")
	{
		if(value2.style.display=="none")
		{
			value2.style.display="block";
			if(value8.style.display=="none")
				{
				value8.style.display="block";
				value9.style.display="none";
				return;
				}else if(value8.style.display=="block")
				{
				value8.style.display="none";
				value9.style.display="none";
				return;
			}
			return;
		}else if(value2.style.display=="block")
		{
			value2.style.display="none";
			if(value8.style.display=="none")
			{
				value8.style.display="block";
				value9.style.display="none";
				return;
			}else if(value8.style.display=="block")
			{
				value8.style.display="none";
				value9.style.display="block";
				return;
			}
			return;
		}
	}
	if(value=="5")
	{
		if(value1.style.display=="none")
		{
			value1.style.display="block";
				if(value6.style.display=="none")
				{
				value6.style.display="block";
				value7.style.display="none";
				return;
				}else if(value6.style.display=="block")
				{
				value6.style.display="none";
				value7.style.display="none";
				return;
			}
			return;
		}else if(value1.style.display=="block")
		{
			value1.style.display="none";
			if(value6.style.display=="none")
			{
				value6.style.display="block";
				value7.style.display="none";
				return;
			}else if(value6.style.display=="block")
			{
				value6.style.display="none";
				value7.style.display="block";
				return;
			}
			return;
		}
	}
	if(value=="6")
	{
		if(value2.style.display=="none")
		{
			value2.style.display="block";
				if(value8.style.display=="none")
				{
				value8.style.display="block";
				value9.style.display="none";
				return;
				}else if(value9.style.display=="block")
				{
				value8.style.display="none";
				value9.style.display="none";
				return;
			}
			return;
		}else if(value2.style.display=="block")
		{
			value2.style.display="none";
			if(value8.style.display=="none")
			{
				value8.style.display="block";
				value9.style.display="none";
				return;
			}else if(value8.style.display=="block")
			{
				value8.style.display="none";
				value9.style.display="block";
				return;
			}
			return;
		}
	}
}
function getorgbasefield(mytarget)
{

    return_vo ='';
    var theUrl = '/system/options/interfaces/orgInterfaces.do?b_tree=link&param=root&froms=db&name=org&input=2';
    Ext.create('Ext.window.Window', {
        id:'orgInterfaces',
        height: 430,
        width: 300,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:Ext.getBody(),
        listeners:{
            'close':function () {
                if (return_vo) {
                    var codeitemid,codetext;
                    codeitemid=return_vo.codeitemid;
                    codetext=return_vo.codetext;
                    var oldInputs=document.getElementsByName(mytarget);
                    var oldobj=oldInputs[0];
                    target_name=oldobj.name;
                    var hidden_name=target_name.replace(".viewvalue",".value");
                    var hiddenInputs=document.getElementsByName(hidden_name);
                    if(hiddenInputs!=null)
                    {
                        hiddenobj=hiddenInputs[0];
                        hiddenobj.value=codeitemid;
                    }
                    oldobj.value=codetext;
                }
            }}

    }).show();
	// var return_vo= window.showModalDialog("/system/options/interfaces/orgInterfaces.do?b_tree=link&param=root&froms=db&name=org&input=2", false,
   //      "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
   //  if(return_vo==null){
   //   return;
   // }else
   // {
   //   var codeitemid,codetext;
   //   codeitemid=return_vo.codeitemid;
   //   codetext=return_vo.codetext;
   //   var oldInputs=document.getElementsByName(mytarget);
   //   var oldobj=oldInputs[0];
   //   target_name=oldobj.name;
   //   var hidden_name=target_name.replace(".viewvalue",".value");
   //   var hiddenInputs=document.getElementsByName(hidden_name);
   //   if(hiddenInputs!=null)
   //   {
   //  	hiddenobj=hiddenInputs[0];
   //  	hiddenobj.value=codeitemid;
   //   }
   //   oldobj.value=codetext;
   // }
}
function changeTrColor(id)
 {
    var ob=document.getElementById("tb");
    var j=ob.rows.length;
    for(var i=0;i<j-1;i++)
    {
         var o="a_"+i;
         var obj=document.getElementById(o);
         if(o==id)
         {
           if(o!=null)
           {
               obj.className="selectedBackGroud";
           }
         }
         else
         {
           if(i%2==0)
           {
              if(o!=null)
              {
                obj.className="trShallow";
                
              }
           }
           else
           {
               if(o!=null)
               {
                  obj.className="trDeep";
               }
           }
         }
    }
      
 } 
</script>
  <body>
 <html:form action="/system/options/interfaces/setInterfaces">
    <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable common_border_color" style="border-top:#C4D8EE solid 1px;">
    <tr>
    	<td align="left" class="TableRow_lr" colspan="2" nowrap>
    		
    		<div id="value6" style="display:block;">
    			<table border="0" cellspacing="0"  cellpadding="0" >
    			<tr><td valign='bottom' >
		        <img src="/images/tree_collapse.gif" align="absmiddle" border="0" alt="人员指标" onclick="clement('5');">
		        </td><td>
		        &nbsp;&nbsp;人员指标
		   		</td></tr>
		   		</table>
		    </div>
		    <div id="value7" style="display:none;">
		    	<table border="0" cellspacing="0"   cellpadding="0" >
		    	<tr><td valign='bottom' >
		        <img src="/images/tree_expand.gif" align="absmiddle" border="0" alt="人员指标" onclick="clement('1');" >
		        </td><td>
		        &nbsp;&nbsp;人员指标
		        </td></tr>
		   		</table>
		    </div>
		    
	    </td>
    </tr>
    <tr>
    	<td align="left"  nowrap>
    		<div id="value1" style="display:block;">
    		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
   	       <tr class="trDeep">
                <%
							int j = 0;
							%>        
            <td align="center" class="RecordRow" style="width:230px;"  nowrap>
		        &nbsp;人员关联指标&nbsp;
	         </td> 
	         <td align="left" class="RecordRow"   nowrap>
		        &nbsp;&nbsp;<html:select name="setInterfacesForm" property="chitemid" size="0" style="width:300px;">
								<html:optionsCollection property="chklist" value="dataValue" label="dataName"/>
							</html:select>
	         </td>   	    		        	        	        
           </tr>
      
           <logic:iterate id="element" name="setInterfacesForm"  property="fielditemlist" indexId="index">
           	<%
										if (j % 2 == 0)
										{
								%>
								<tr class="trShallow">
									<%
											} else
											{
									%>
								
								<tr class="trDeep">
									<%
											}
											j++;
									%> 
                 <td align="center" class="RecordRow_lrt" nowrap>                 
                  &nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;                   
                 </td>
                 <td align="left" class="RecordRow_lrt" nowrap>         
                 &nbsp;
                 <html:text name="setInterfacesForm" property='<%="fielditemlist["+index+"].viewvalue"%>'  styleClass="textColorWrite" style="width:300px;"/>   
                 <html:hidden name="setInterfacesForm" property='<%="fielditemlist["+index+"].value"%>' />     
                  <img src="/images/code.gif" align="absmiddle" onclick='javascript:getbasefield("<%="fielditemlist["+index+"].viewvalue"%>");' />
                  
                  &nbsp;                   
                 </td>
           </logic:iterate> 
           </table>
    </div>
    	</td>
    </tr>
    <tr>
    	<td align="left" class="TableRow_lrt" colspan="2" nowrap>
    		 <div id="value8" style="display:block;">
    		 <table border="0" cellspacing="0"  cellpadding="0" >
    			<tr><td valign='bottom' >
		        <img src="/images/tree_collapse.gif" align="absmiddle" border="0" alt="单位指标" onclick="clement('6');">
		     	 </td><td>
		     	 &nbsp;&nbsp;单位指标
		     	 </td></tr>
		     	 </table>
		     </div>
		     <div id="value9" style="display:none;">
		     <table border="0" cellspacing="0"  cellpadding="0" >
    			<tr><td valign='bottom' >
		        <img src="/images/tree_expand.gif" align="absmiddle" border="0" alt="单位指标" onclick="clement('2');">
		     	</td><td>
		     	&nbsp;&nbsp;单位指标
		     	</td></tr>
		     	</table>
		     </div> 
	    </td>
    </tr>
    <tr>
    	<td align="left"  nowrap>
    		<div id="value2" style="display:block;">
    			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    				<%
					int i = 0;
					%>
           			<logic:iterate id="element" name="setInterfacesForm"  property="orgitemlist" indexId="index" length="maxindex"> 
               		
								<%
										if (i % 2 == 0)
										{
								%>
								<tr class="trShallow">
									<%
											} else
											{
									%>
								
								<tr class="trDeep">
									<%
											}
											i++;
									%>
              
                 <td align="center" class="RecordRow_lrt"  style="width:230px;" nowrap>
                  &nbsp;<bean:write  name="element" property="itemdesc"/>1&nbsp;
                 </td>
                 <td align="left" class="RecordRow_lrt" nowrap>         
                 &nbsp;
                 <html:text name="setInterfacesForm" property='<%="orgitemlist["+index+"].viewvalue"%>'  styleClass="TEXT6" style="width:300px;"/>   
                 <html:hidden name="setInterfacesForm" property='<%="orgitemlist["+index+"].value"%>' />     
                 <img src="/images/code.gif" align="absmiddle" onclick='javascript:getorgbasefield("<%="orgitemlist["+index+"].viewvalue"%>");' /> 
                  &nbsp;                   
                 </td>
               
            </tr>
           </logic:iterate>
    			</table>
    		</div>
    	</td>
    </tr>
            <tr class="trDeep">
            <td  align="left" class="RecordRow_lrt" colspan="2" nowrap>
		       &nbsp;&nbsp;其他参数
	         </td>    	    		        	        	        
           </tr>
           <tr>                      
            <td  align="left"  class="RecordRow" colspan="2" nowrap>
		        &nbsp;&nbsp;
		       <logic:equal name="setInterfacesForm" property="impmode" value="1">
                   			<input type="checkbox" name="boximpmode" onclick="checkBoximpmode(this);" checked/> 
                   		</logic:equal>
                   		<logic:notEqual name="setInterfacesForm" property="impmode" value="1">
                   			<input type="checkbox" name="boximpmode" onclick="checkBoximpmode(this);"/> 
                   		</logic:notEqual> 导入时进行代码转换
                   		<html:hidden name="setInterfacesForm" property="impmode"/>
                   		&nbsp;&nbsp;<logic:equal name="setInterfacesForm" property="expmode" value="1">
                   			<input type="checkbox" name="boxexpmode" onclick="checkBoxexpmode(this);" checked/> 
                   		</logic:equal>
                   		<logic:notEqual name="setInterfacesForm" property="expmode" value="1">
                   			<input type="checkbox" name="boxexpmode" onclick="checkBoxexpmode(this);"/> 
                   		</logic:notEqual>
                   		<html:hidden name="setInterfacesForm" property="expmode"/>
	                      导出时进行代码转换
	           <logic:equal name="setInterfacesForm" property="marker" value="1">
                    <input type="checkbox" name="boxmarker" onclick="checkMarker(this);" checked/> 
               </logic:equal>
               <logic:notEqual name="setInterfacesForm" property="marker" value="1">
                   	<input type="checkbox" name="boxmarker" onclick="checkMarker(this);"/> 
               </logic:notEqual> 返回新增人员对应关联指标值
               <html:hidden name="setInterfacesForm" property="marker"/>       		        	        	        
           </tr>
      </table>
      <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0">
         <tr>
           <td height="35px;">
              <input type="button" name="returnbutton"  value="<bean:message key="button.save"/>" class="mybutton" onclick="save();">                 
                       
           </td>
         </tr>
       </table>
  </html:form>
  
  </body>
</html>