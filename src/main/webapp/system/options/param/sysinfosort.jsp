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
		var dw=540,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
		var target_url="/system/param/sysinfosort.do?b_addsubclass=link&tagname="+tag;
	    /*
	    var return_vo= window.showModalDialog(target_url,1, 
	         "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	    if(return_vo!=null)
	    {
		  var in_obj=document.getElementById(index);  
		  in_obj.innerHTML=return_vo.mess;
	    }else
	    {
		  var in_obj=document.getElementById(index);  
	    }
	    */
	    //改用ext 弹窗显示  wangb 20190319
	    var win = Ext.create('Ext.window.Window',{
			id:'select_field',
			title:'请选择',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+target_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					 if(this.return_vo!=null)
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
		/**许硕16/09/13
		判断选项是否未选中
		**/
		var obj=document.getElementsByTagName('input')
		var isChecked=false;
		for(var i=2;i<obj.length;i++){
			if(obj[i].type=='checkbox'){
				if(obj[i].checked){
					isChecked = true;
					break;
				}
			}
		}
		/**
		判断
		**/
		if(!isChecked){
			alert('未选中数据');
		}else{
			if(confirm(DEL_INFO)){
				sysinfosortForm.action="/system/param/sysinfosort.do?b_delete=link";
				sysinfosortForm.submit();
			}
		}
	}
	function isadd(mess)
	{
		if(mess=="11")
			alert("名称与子集或已有分类相同，请重新命名！");
	}
	function addsort(setid)
	{
		sysinfosortForm.action="/system/param/sysinfosort.do?b_addsort=link&tag="+setid;
		sysinfosortForm.submit();
	}
	function rework(reworktag,reworkoldname)
	{
		/*
		var reworkname=showModalDialog('/system/options/param/reworktag.jsp','rework','dialogHeight:200px;dialogWidth:300px;center:yes;help:no;resizable:no;status:no;'); 		
		if(reworkname!=null && reworkname.length>0)
        {
          sysinfosortForm.action="/system/param/sysinfosort.do?b_rework=link&reworkname=" +reworkname+"&reworktag="+reworktag+"&reworkoldname="+reworkoldname ;
          sysinfosortForm.submit(); 
        }*/
        //改用ext 弹窗显示  wangb 20190319
        var url = '/system/options/param/reworktag.jsp'
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
					if(this.reworkname!=null && this.reworkname.length>0)
        			{
          				sysinfosortForm.action="/system/param/sysinfosort.do?b_rework=link&reworkname=" +$URL.encode(this.reworkname)+"&reworktag="+reworktag+"&reworkoldname="+$URL.encode(reworkoldname) ;
          				sysinfosortForm.submit(); 
        			}
				}
			}
		 }); 
	}
	function tagtaxis()
	{
		//window.open ('/system/param/sysinfosort.do?b_taxis=link', 'tagtaxis', 'top=170,left=220,width=500,height=404, toolbar=no, menubar=no, scrollbars=yes, resizable=no,location=no, status=no');
		var dw=400,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
		var iframe_url="/general/query/common/iframe_query.jsp?src=/system/param/sysinfosort.do?br_taxis=link";
		/*
		showModalDialog(iframe_url, 'tagtaxis', "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
		*/
		//改用ext 弹窗显示  wangb 20190517
	    var win = Ext.create('Ext.window.Window',{
			id:'paixu',
			title:'',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					/*只有排序页面是保存操作才刷新页面*/
					if(this.beReload){
					//修改子集排序后，刷新页面
					window.location.href="/system/param/sysinfosort.do?b_query=link";
					}
				}
			}
		 }); 
	}
	function addset()
	{
	    var obj=document.getElementsByName('sortname'); 
	    if(obj)
	    {
	      var objj=obj[0];	     
	      var value=objj.value;
	      if(value!="")
	      {
	         sysinfosortForm.action="/system/param/sysinfosort.do?b_add=link";
		     sysinfosortForm.submit();
	      }else
	      {
	         alert('<bean:message key="system.param.sysinfosort.subsetsortname"/>不能为空！');
	      }
	      
	    }
	    
	}
//-->
</script>
<html:form action="/system/param/sysinfosort">
	<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
	   	  <thead>
	           <tr>
	            <td align="left" colspan="5" style="padding-bottom:3px;">
					<html:radio name="sysinfosortForm" property="tag" value="set_a" onclick="choosesort()"><bean:message key="system.param.sysinfosort.subsetsort"/></html:radio>
					<html:radio name="sysinfosortForm" property="tag" value="set_b" onclick="choosesort()"><bean:message key="system.param.sysinfosort.pointsort"/></html:radio>&nbsp;&nbsp;
	            </td>            	        	        	        
	           </tr>
   	 	 </thead>
   	 	 <logic:equal name="sysinfosortForm" property="tag" value="set_a">
   	 	 <THEAD>
   	 	 	<tr>
   	 	 		<td align="center" class="TableRow" width="10%">
   	 	 			<bean:message key="column.select"/>
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="10%">
   	 	 			<bean:message key="system.param.sysinfosort.sortname"/>
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="60%">
   	 	 			<bean:message key="system.param.sysinfosort.subset"/>
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="10%">
   	 	 			<bean:message key="system.param.sysinfosort.updatesortname"/>
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="10%">
   	 	 			<bean:message key="button.orgmapset"/>
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
				    <td align="center" class="RecordRow" width="10%">
			   			<img src="/images/edit.gif" onclick="rework('<bean:write name="sysinfosortForm" property="tag" filter="true"/>','<bean:write name="element" property="tagname" filter="true"/>')" style="cursor:hand"/>
				    </td> 
				    <td align="center" class="RecordRow" width="10%">
			   			<img src="/images/edit.gif" onclick="editsort('<bean:write name="element" property="tagname" filter="true"/>','<%=i%>')" style="cursor:hand"/>
				    </td>            
				</tr> 
			</hrms:extenditerate>
		<tr>
		   <td colspan="5" class="RecordRow">
		     <table width="100%" align="center"> 
             <tr>
               <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
                 <bean:write name="sysinfosortForm" property="roleListForm.pagination.current" filter="true" />
                 <bean:message key="label.page.sum"/>
                 <bean:write name="sysinfosortForm" property="roleListForm.pagination.count" filter="true" />
                 <bean:message key="label.page.row"/>
                 <bean:write name="sysinfosortForm" property="roleListForm.pagination.pages" filter="true" />
                 <bean:message key="label.page.page"/>
               </td>
               <td  align="right" nowrap class="tdFontcolor">
                  <p align="right">
                   <hrms:paginationlink name="sysinfosortForm" property="roleListForm.pagination"
                   nameId="roleListForm">
                  </hrms:paginationlink>
              </td>
           </tr>  
           </table>
		   </td>
		</tr>
   	 	<tr>
   	 		<td class="RecordRow" valign="middle" colspan="5" style="height: 35px;">
   	 			<table><tr><td>
   	 			<bean:message key="system.param.sysinfosort.subsetsortname"/>
   	 			</td><td>
   	 			<html:text name="sysinfosortForm" property="sortname" size="20"  styleClass="text4" style="margin-left:5px;"/>
		   	 	</td><td>
		   	 	<input type="button" name="delete" value='<bean:message key="button.insert"/>' class="mybutton" onclick="addset();">
		   	 	</td><td>
		   	 	<input type="button" name="delete" value='<bean:message key="button.delete"/>' class="mybutton" onclick="deletesort()">
		   	 	</td><td align="left">
		   	 	<input type="button" name="taxis" value='<bean:message key="kq.item.change"/>' class="mybutton" onclick="tagtaxis()">
		   	 	</td></tr></table>
   	 		</td>
   	 	</tr>
   	 	</logic:equal>
   	 	<logic:equal name="sysinfosortForm" property="tag" value="set_b">
   	 		<THEAD>
	   	 	 	<tr>
	   	 	 		
	   	 	 		<td align="center" class="TableRow" width="20%">
	   	 	 			<bean:message key="system.param.sysinfosort.sortname"/>
	   	 	 		</td>
	   	 	 		<td align="center" class="TableRow" width="70%" colspan="3">
	   	 	 			<bean:message key="system.param.sysinfosort.point"/>
	   	 	 		</td>
	   	 	 		<td align="center" class="TableRow" width="10%">
	   	 	 			&nbsp;
	   	 	 		</td>
	   	 	 	</tr>
	   	 	 </THEAD>
	   	 	 	<hrms:extenditerate id="element" name="sysinfosortForm" property="filesetListForm.list" indexes="indexes"  pagination="filesetListForm.pagination" pageCount="10" scope="session">
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
				   		     
				   		<td align="center" class="RecordRow" width="20%" >
				   			<bean:write name="element" property="setname" filter="false"/>&nbsp;
					    </td>
					    <td align="left" class="RecordRow" width="70%" id="<%=i%>" colspan="3">
				   			<bean:write name="element" property="allsetvalue" filter="false"/>&nbsp;
					    </td>
					    <td align="center" class="RecordRow" width="10%">
				   			<img src="/images/edit.gif" onclick="addsort('<bean:write name="element" property="setid" filter="true"/>')" style="cursor:hand"/>
					    </td>            
					</tr> 
				</hrms:extenditerate>
	   	<table  width="90%" align="center" class="RecordRowP" style="margin-bottom:5px;">
		  <tr>
		      <td valign="bottom" class="tdFontcolor" ><bean:message key="label.page.serial"/>
		     <bean:write name="sysinfosortForm" property="filesetListForm.pagination.current" filter="true" />
		     <bean:message key="label.page.sum"/>
		     <bean:write name="sysinfosortForm" property="filesetListForm.pagination.count" filter="true" />
		     <bean:message key="label.page.row"/>
		     <bean:write name="sysinfosortForm" property="filesetListForm.pagination.pages" filter="true" />
		     <bean:message key="label.page.page"/>
		   </td>
		   <td  align="right" nowrap class="tdFontcolor">
		         <p align="right">
		         <hrms:paginationlink name="sysinfosortForm" property="filesetListForm.pagination"
		                  nameId="filesetListForm" propertyId="filesetlistsProperty">
		         </hrms:paginationlink>
		   </td>
		  </tr>
		</table>
	   	
   	 	</logic:equal>
   	 </table>
   	 
</html:form>
<script language="javascript">
<!--
	isadd('<bean:write name="sysinfosortForm" property="errmes"/>');
//-->
</script>
