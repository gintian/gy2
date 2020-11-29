<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	int i=0;
%>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="javascript">
<!--
	function choosesort()
	{
		sysinfosortForm.action="/system/param/sysinfosort.do?b_query=link";
		sysinfosortForm.submit();
	}
	function editsort(tag,index)
	{
		tag = $URL.encode(tag);
		var target_url="/system/param/sysinfosort.do?b_addsubclass=link&tagname="+tag+"&order=no";
	    /*
	    var return_vo= window.showModalDialog(target_url,1, 
	        "dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	    if(return_vo!=null)
	    {
		  var in_obj=document.getElementById(index);  
		  in_obj.innerHTML=return_vo.mess;
	    }else
	    {
		  var in_obj=document.getElementById(index);  
	    }
	    */
	    //改用ext 弹窗显示  wangb 20190323
        var win = Ext.create('Ext.window.Window',{
			id:'select_field',
			title:'请选择',
			width:560,
			height:420,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+target_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					 if(this.return_vo && this.return_vo!=null)
	    			 {
		  				 var in_obj=document.getElementById(index);  
		  				 in_obj.innerHTML=this.return_vo.mess;
	    			 }else
	    			 {
		  				 var in_obj=document.getElementById(index);  
	    			 }
				}
			}
		}); 
	}
	function deletesort()
	{
		if(confirm("确定要删除吗？")){
			sysinfosortForm.action="/system/param/sysinfosort.do?b_delete2=link";
			sysinfosortForm.submit();
		}
	}
	function isadd(mess)
	{
		if(mess=="11")
			alert("存在相同标识名，请重新增加！");
	}
	function addsort(setid)
	{
		sysinfosortForm.action="/system/param/sysinfosort.do?b_addsort=link";
		sysinfosortForm.submit();
	}
	function rework(reworktag,reworkoldname)
	{
		/*
		var reworkname=showModalDialog('/system/options/param/reworktag.jsp','rework','dialogHeight:200px;dialogWidth:300px;center:yes;help:no;resizable:no;status:no;'); 		
		if(reworkname!=null && reworkname.length>0)
        {
          sysinfosortForm.action="/system/param/sysinfosort.do?b_rework2=link&reworkname=" +reworkname+"&reworktag="+reworktag+"&reworkoldname="+reworkoldname ;
          sysinfosortForm.submit(); 
        }
        */
        //改用ext 弹窗显示  wangb 20190323
        var url = '/system/options/param/reworktag.jsp';
        var win = Ext.create('Ext.window.Window',{
			id:'select_field',
			title:'请选择',
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
					if(this.reworkname&& this.reworkname!=null && this.reworkname.length>0)
        			{
          				sysinfosortForm.action="/system/param/sysinfosort.do?b_rework2=link&reworkname=" +$URL.encode(this.reworkname)+"&reworktag="+reworktag+"&reworkoldname="+$URL.encode(reworkoldname) ;
          				sysinfosortForm.submit(); 
        			}
				}
			}
		}); 
	}
//-->
</script>
<html:form action="/system/param/sysinfosort">
	<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	   	  <thead>
	           <tr>
	            <td align="left" colspan="5" style="padding-left:5px;" class="RecordRow">
					指标分类&nbsp;&nbsp;
	            </td>            	        	        	        
	           </tr>
   	 	 </thead>
   	 	 <THEAD>
   	 	 	<tr>
   	 	 		<td align="center" class="TableRow" width="10%">
   	 	 			选择
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="10%">
   	 	 			分类名
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="60%">
   	 	 			指标
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="13%">
   	 	 			修改分类名
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="7%">
   	 	 			设置
   	 	 		</td>
   	 	 	</tr>
   	 	 </THEAD>
   	 	 	<hrms:extenditerate id="element" name="sysinfosortForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="10" scope="session">
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
			   		<td align="center" class="RecordRow" width="10%">
			   			<hrms:checkmultibox name="sysinfosortForm" property="roleListForm.select"  value="true" indexes="indexes"/>&nbsp;
				    </td>            
			   		<td align="center" class="RecordRow" width="10%" >
			   			<bean:write name="element" property="tagname" filter="false"/>&nbsp;
				    </td>
				    <td align="left" class="RecordRow" width="60%" id="<%=i%>">
			   			<bean:write name="element" property="viewvalue" filter="false"/>&nbsp;
				    </td>
				    <td align="center" class="RecordRow" width="13%">
			   			<img src="/images/edit.gif" onclick="rework('<bean:write name="sysinfosortForm" property="tag" filter="true"/>','<bean:write name="element" property="tagname" filter="true"/>')" style="cursor:hand"/>
				    </td> 
				    <td align="center" class="RecordRow" width="7%">
			   			<img src="/images/edit.gif" onclick="editsort('<bean:write name="element" property="tagname" filter="true"/>','<%=i%>')" style="cursor:hand"/>
				    </td>            
				</tr> 
			</hrms:extenditerate>
   	 	<tr>
   	 		<td class="RecordRow" colspan="5" style="height: 35px">
   	 			<table><tr><td>
   	 			指标分类名称<html:text name="sysinfosortForm" property="sortname" size="20" style="margin-left:5px;" styleClass="text4" />
   	 			</td><td>
		   	 	<html:submit styleClass="mybutton" property="b_add2" onclick="return canempty();"><bean:message key="button.insert"/></html:submit>
		   	 	</td><td>
		   	 	<input type="button" name="delete" value='<bean:message key="button.delete"/>' class="mybutton" onclick="deletesort()">
		   	 	</td><td>
		   	 	<html:submit styleClass="mybutton" property="b_return"><bean:message key="button.return"/></html:submit>
		   	 	</td></tr></table>
   	 		</td>
   	 	</tr>
   	 </table>
   	 
</html:form>
<script language="javascript">
<!--
	isadd('<bean:write name="sysinfosortForm" property="errmes"/>');
	
	function canempty(){
		var obj=document.getElementsByName('sortname');
		if(obj[0].value==""){
			alert("指标分类名称不能为空！");
			return false;
		}
		return true;
	}
//-->
</script>
