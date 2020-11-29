<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	int i=0;
%>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="javascript">
<!--
	function editsort(name,select_id,index,operationid)
	{
		var type="1";
		if(operationid=="34")
		   type="2";
		if(operationid=="39")
		   type="8";	
		if(operationid=="56")
		   type="10";
		   if(operationid=="57")
		   type="11";
		  if(operationid=="51")
		   type="3";
		   if(operationid=="52")
		   type="6"; 	
		    if(operationid=="53")
		   type="4"; 	
		    if(operationid=="54")
		   type="5"; 	
		if(operationid=="44")
		   type="11";
		if(operationid=="51")
		   type="3";   
		if(operationid=="52")
		   type="6";   
		if(operationid=="53")
		   type="4";   
		if(operationid=="54")
		   type="5";   		   	   
	    var t_url="/system/warn/config_maintenance.do?b_template=link&select_id="+select_id+"&type="+type+"&dr=1&module=1";
	    var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	     /*
	       var return_vo= window.showModalDialog(t_url,'rr', 
             "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
          if(!return_vo)
            return false;
         else
          {
             if(return_vo.flag=="true")
             {
                var text=return_vo.title;
                var ids=return_vo.content;
                var hashvo=new ParameterSet();
                hashvo.setValue("text",text);
                hashvo.setValue("ids",ids);
                hashvo.setValue("index",index);
                hashvo.setValue("operationid",'${operationsortForm.operationid}');
                hashvo.setValue("sortname",name);
			    var request=new Request({method:'post',onSuccess:editsort_ok,functionId:'1012010015'},hashvo);
             }
          }     
          */
          //改用ext 弹窗显示  wangb 20190319
          var win = Ext.create('Ext.window.Window',{
			id:'search_org',
			title:'请选择',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+t_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(!this.return_vo)
            			return false;
         			else
          			{
             			if(this.return_vo.flag=="true")
             			{
                			var text=this.return_vo.title;
                			var ids=this.return_vo.content;
                			var hashvo=new ParameterSet();
                			hashvo.setValue("text",text);
                			hashvo.setValue("ids",ids);
                			hashvo.setValue("index",index);
                			hashvo.setValue("operationid",'${operationsortForm.operationid}');
                			hashvo.setValue("sortname",name);
			    			var request=new Request({method:'post',onSuccess:editsort_ok,functionId:'1012010015'},hashvo);
             			}
          			}   
				}
			}
		 });  
          
          
	}
	function editsort_ok(outparamters)
	{
		//var index = outparamters.getValue("index");
		//var text = outparamters.getValue("text");
		//var ids = outparamters.getValue("ids");
		//if(text!=""&&text.length>0)
        //{
        //   var at=text.split(",");
        //   var tabids=ids.split(",");
        //   text="";
        //   for(var i=0;i<at.length;i++)                   
        //   {
        //      if(tabids[i]!="")
        //        text=text+tabids[i]+":"+at[i]+"\r\n";
        //   }
		//   var in_obj=document.getElementById(index);  
		//   in_obj.innerHTML=text;
        //}
        var operationid = outparamters.getValue("operationid");
        window.location.href="/system/param/operationsort.do?b_sortinfo=link&operationid="+operationid;
	}
	function deletesort()
	{
		var len=document.operationsortForm.elements.length;
		var uu;
		for (var i=0;i<len;i++)
		{
		   if (document.operationsortForm.elements[i].type=="checkbox")
		   {
		      if(document.operationsortForm.elements[i].checked==true)
		      {
		        uu="dd";
		        break;
		       }
		   }
		}
		if(uu!="dd")
		{
		  alert("没有选择记录！");
		  return false;
		}
		if(confirm("确定要删除吗？")){
			operationsortForm.action="/system/param/operationsort.do?b_delete=link";
			operationsortForm.submit();
		}
	}
	function rework(reworkoldname)
	{
		/*
		var reworkname=showModalDialog('/system/options/param/reworktag.jsp','rework','dialogHeight:200px;dialogWidth:300px;center:yes;help:no;resizable:no;status:no;'); 		
		if(reworkname!=null && reworkname.length>0)
        {
          operationsortForm.action="/system/param/operationsort.do?b_rework=link&reworkname=" +getEncodeStr(reworkname)+"&reworkoldname="+getEncodeStr(reworkoldname);
          operationsortForm.submit(); 
        }
        */
        //改用ext 弹窗显示  wangb 20190319
        var url = '/system/options/param/reworktag.jsp';
        var win = Ext.create('Ext.window.Window',{
			id:'select_field',
			title:'请输入',
			width:320,
			height:220,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.reworkname!=null && this.reworkname.length>0)
			        {
          				operationsortForm.action="/system/param/operationsort.do?b_rework=link&reworkname=" +$URL.encode(getEncodeStr(this.reworkname))+"&reworkoldname="+$URL.encode(getEncodeStr(reworkoldname));
          				operationsortForm.submit(); 
        			}
				}
			}
		}); 
	}
	function check()
	{
		var text = document.getElementById("sortname");
		if(text.value.length<=0)
		{
			alert("请输入分类名称");
			return false;
		}
		var theText=replaceAll(text.value, "'", "‘");
		theText=replaceAll(theText, '\"', '”');
		document.getElementById("sortname").value=theText;
	}
	function isadd(mess)
	{
		if(mess=="11")
			alert("存在相同标识名，请重新命名！");
	}
	function estop()
	{
		return (((event.keyCode > 47) && (event.keyCode <= 57))|| ((event.keyCode >= 65)&& (event.keyCode <= 90))|| ((event.keyCode >= 97)&& (event.keyCode <= 122))|| (event.keyCode == 95));
	}
	function replaceAll(str, sptr, sptr1)
    {
		while (str.indexOf(sptr) >= 0)
		{	
   			str = str.replace(sptr, sptr1);
		}
		return str;
   } 
//-->
</script>
<html:form action="/system/param/operationsort">
	<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	 	 <THEAD>
   	 	 	<tr>
   	 	 		<td align="center" class="TableRow" width="5%">
   	 	 			<bean:message key="column.select" />
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="11%">
   	 	 			<bean:message key="system.operation.type" />
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="38%">
   	 	 			<bean:message key="system.operation.template" />
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="7%">
   	 	 			<bean:message key="kq.shift.group.update.name" />
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="7%">
   	 	 			<bean:message key="button.orgmapset" />
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="32%">
   	 	 			<bean:message key="sys.options.param.type" />
   	 	 		</td>
   	 	 	</tr>
   	 	 </THEAD>
   	 	 <hrms:extenditerate id="element" name="operationsortForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="300" scope="session">
	 	 	<%
	 	 		if(i%2==0){
	 	 	%>
	 	 		<tr class="trDeep">
	 	 	<%
	 	 		}else{
	 	 	%>
	 	 		<tr class="trShallow">
	 	 	<%
	 	 		}
	 	 		i++;
	 	 	%>
		   		<td align="center" class="RecordRow" width="5%">
		   			<hrms:checkmultibox name="operationsortForm" property="roleListForm.select"  value="true" indexes="indexes"/>&nbsp;
			    </td>            
		   		<td align="left" class="RecordRow" width="11%">
		   			<bean:write name="element" property="sortname" filter="false"/>&nbsp;
			    </td>
			    <td align="left" class="RecordRow" width="38%" id="<%=i%>">
			    	<bean:write name="element" property="sortvalue" filter="false"/>&nbsp;
			    </td>
			    <td align="center" class="RecordRow" width="7%">
			    	<img src="/images/edit.gif" onclick="rework('<bean:write name="element" property="sortname" filter="false"/>')" style="cursor:hand"/>
			    </td> 
			    <td align="center" class="RecordRow" width="7%">
			    	<img src="/images/edit.gif" onclick="editsort('<bean:write name="element" property="sortname" filter="false"/>','<bean:write name="element" property="select_id" filter="false"/>','<%=i%>','<bean:write name="operationsortForm" property="operationid" filter="false"/>')" style="cursor:hand"/>
			    </td>            
			     <td align="center" class="RecordRow" width="32%">
			      <logic:iterate id="type" name="element" property="type_list">
                	<input type="checkbox" name="typestr" value='<bean:write name="type" property="value" />' <bean:write name="type" property="check" /> > 
	                	<bean:write name="type" property="name" />
                </logic:iterate>
			    </td>
			</tr> 
   	 	 </hrms:extenditerate>
   	 	 <tr>
   	 		<td class="RecordRow" colspan="6" style="height: 35px">
   	 		<table><tr><td>
   	 			业务分类名称<html:text name="operationsortForm" property="sortname" size="20"  styleClass="text4" styleId="sortname" onkeypress="event.returnValue=estop(this)" style="margin-left:5px;"/>
   	 			</td><td>
		   	 	<html:submit styleClass="mybutton" property="b_add" onclick="return check();"><bean:message key="button.insert"/></html:submit>
		   	 	</td><td>
		   	 	<input type="button" name="delete" value='<bean:message key="button.delete"/>' class="mybutton" onclick="deletesort()">
		   	 	</td><td>
		   	 	<html:submit styleClass="mybutton" property="b_save"><bean:message key="button.save"/></html:submit>
		   	 	</td><td>
		   	 	<html:submit styleClass="mybutton" property="b_return"><bean:message key="button.return"/></html:submit>
		   	 	</td></tr></table>
		   	</td>
   	 	</tr>
   	 </table>
   	 
</html:form>

<script language="javascript">
<!--
	isadd('<bean:write name="operationsortForm" property="errmes"/>');
//-->
</script>