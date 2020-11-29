<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>

<html>
  <head>
  <script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <script language="javascript" src="/js/function.js"></script>
  <script language='javascript' >
  	var startUpIndex=${gzStandardPackageForm.startUpIndex};
  	
  	function resetPackName()
  	{
  		var selectedIndex=-1;
  		var index=0;
  		for(var i=0;i<document.gzStandardPackageForm.elements.length;i++)
  		{
  			if(document.gzStandardPackageForm.elements[i].type=='checkbox'&&document.gzStandardPackageForm.elements[i].name!='selbox')
  			{
  				if(document.gzStandardPackageForm.elements[i].checked==true)
  				{
  					if(selectedIndex==-1)
	  					selectedIndex=index;
  					else
  					{
  						alert(GZ_TEMPLATESET_INFO2+"！");
  						return;
  					}
  				}
  				index++;
  			}
  		}
  		if(selectedIndex==-1)
  		{
  			alert(GZ_TEMPLATESET_INFO3+"！");
  			return;
  		}
  		var name=prompt(GZ_TEMPLATESET_INPUTNEWNAME,'');
  		if(name)
  		{
  		if(name.length>0)
  		{
  			if(IsOverStrLength(name,30))
  			{
  				alert(GZ_TEMPLATESET_INFO4);
  				return ;
  			}
  			else
  			{
	  			document.gzStandardPackageForm.resetName.value=name;
	  			document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_delPack=del&operate=resetName";
	  			document.gzStandardPackageForm.submit();
	  		}
  		}
  		}
  		
  	}
  	
  	
  	function editPackage()
  	{
  		var selectedIndex=-1;
  		var index=0;
  		for(var i=0;i<document.gzStandardPackageForm.elements.length;i++)
  		{
  			if(document.gzStandardPackageForm.elements[i].type=='checkbox'&&document.gzStandardPackageForm.elements[i].name!='selbox')
  			{
  				if(document.gzStandardPackageForm.elements[i].checked==true)
  				{
  					if(selectedIndex==-1)
	  					selectedIndex=index;
  					else
  					{
  						alert(GZ_TEMPLATESET_INFO5+"！");
  						return;
  					}
  				}
  				index++;
  			}
  		}
  		if(selectedIndex==-1)
  		{
  			alert(GZ_TEMPLATESET_INFO5+"！");
  			return;
  		}
  		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_edit=edit";
  		document.gzStandardPackageForm.submit();
  		
  		
  	}
  	
  	
  	
  
  	
  	
  	function newPackage()
  	{
  		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_newPack=new";
  		document.gzStandardPackageForm.submit();
  	}
  	
  	
  	
  
  	function imports()
  	{
  		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?br_initImport=export";
  		document.gzStandardPackageForm.submit();
  	}
  	
  </script>
  </head>
  <%
	int i=0;
  %>
  <body>
  <html:form action="/gz/templateset/standard/standardPackage">
    <Input type='hidden' value=""  name='startDate'  />
    <Input type='hidden' value=""  name='resetName'  />
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

    
    
	<table width="71%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	   	  <thead>
	           <tr>
	             <td width="7%" align="center" nowrap class="TableRow">
	                 <input type="checkbox" name="selbox" onclick="batch_select(this,'standardPackagelistform.select');" title='<bean:message key="label.query.selectall"/>'>      
	             </td>
	             <td width="44%" align="center" nowrap class="TableRow"><bean:message key="label.gz.gzStandardTable"/></td>
	             <td width="16%" align="center" nowrap class="TableRow"><div align="center"><bean:message key="label.gz.startDate"/></div></td>                  
	             <td width="15%" align="center" nowrap class="TableRow"><div align="center"><bean:message key="lable.lawfile.invalidationdate"/></div></td>  
	             <td width="6%" align="center" nowrap class="TableRow"><bean:message key="conlumn.investigate_point.status"/></td>
	             <td width="12%" align="center" nowrap class="TableRow"><bean:message key="gz.formula.salaries.standart.table"/></td>  
	            </tr>
	   	  </thead>
	          
	           <hrms:extenditerate id="element" name="gzStandardPackageForm" property="standardPackagelistform.list" indexes="indexes"  pagination="standardPackagelistform.pagination" pageCount="18" scope="session">
		           <%
		          
		          if(i%2==0)
		          {
		          %>
		          <tr class="trShallow"  onclick='tr_onclick(this,"#F3F5FC");'  >
		          <%}
		          else
		          {%>
		          <tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");' >
		          <%
		          }
		          i++;          
		          %>  		         
		            <td align="center" class="RecordRow" nowrap>
		                <hrms:checkmultibox name="gzStandardPackageForm" property="standardPackagelistform.select" value="true" indexes="indexes"/>
		           		<input type='hidden' name='pkg_id' value='<bean:write name="element" property="pkg_id" filter="true"/>' />
		            </td>
		            
		            <td align="left" class="RecordRow" nowrap>
		                
		            	&nbsp;<bean:write name="element" property="name" filter="true"/>
		            	<input type='hidden' name='desc' value='<bean:write name="element" property="name" filter="true"/>' />
		            </td>
		             <td align="center" class="RecordRow" nowrap>                
		               <div align="right"><bean:write name="element" property="start_date" filter="true"/></div>
		             </td>
		             <td align="center" class="RecordRow" nowrap>                
		               <div align="right"><bean:write name="element" property="end_date" filter="true"/></div>
		             </td>
		            <td align="center" class="RecordRow" nowrap>
		           		<logic:equal name="element" property="status" value="1" >
			           	 <img src="../../../images/cc1.gif" width="15" height="15" border=0>
						 <%
							out.println("<script languge='javascript' >");
							out.println(" startUpIndex="+(i-1));
							out.println("</script>");
						  %>
						</logic:equal>	            
		            </td>
		            <td align="center" class="RecordRow" nowrap>
		             <a href="/gz/templateset/standard.do?b_query=query&pkg_id=<bean:write name="element" property="pkg_id" filter="true"/>" >
		             <img src="../../../images/edit.gif" width="11" height="17" border=0>
		             </a>
		            </td>   
		          </tr>
	        	</hrms:extenditerate>
	</table>
	
	<table  width="71%" align="center"  class='RecordRowP' >
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="gzStandardPackageForm" property="standardPackagelistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="gzStandardPackageForm" property="standardPackagelistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="gzStandardPackageForm" property="standardPackagelistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	        <td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="gzStandardPackageForm" property="standardPackagelistform.pagination" nameId="standardPackagelistform">
					</hrms:paginationlink>
			</td>
		</tr>
</table>
  
  	<table  width="70%" align="center">
     <tr>
       <td align="center"  nowrap colspan="4">
       <hrms:priv func_id="3241001">
           <input type="button" name="b_add" value=" <bean:message key="kh.field.new"/> " class="mybutton" onClick="newPackage();"> 
       </hrms:priv>    
       <hrms:priv func_id="3241002">     
            <input type="button" name="b_edit" value=" <bean:message key="label.kh.edit"/> " class="mybutton" onClick="editPackage();"> 
	   </hrms:priv>    
	   <hrms:priv func_id="3241003">     
	         <input type="button" name="addbutton"  value=" <bean:message key="label.kh.del"/> " class="mybutton" onClick="del()">  
       </hrms:priv>     
       <hrms:priv func_id="3241005">     
             <input type="button" name="addbutton2"  value="<bean:message key="button.rename"/>" class="mybutton" onClick="resetPackName()">
        </hrms:priv>    
        <hrms:priv func_id="3241006">    
             <input type="button" name="addbutton22"  value=" <bean:message key="column.sys.valid"/> " class="mybutton" onClick="startUp();">
        </hrms:priv>    
        <hrms:priv func_id="3241007">    
             <input type="button" name="addbutton23"  value=" <bean:message key="sys.export.derived"/> " class="mybutton" onClick="exports()">
        </hrms:priv>    
        <hrms:priv func_id="3241007">    
             <input type="button" name="addbutton24"  value=" <bean:message key="menu.gz.import"/> " class="mybutton" onClick="imports()">
        </hrms:priv>     
         <hrms:tipwizardbutton flag="compensation" target="il_body" formname="gzStandardPackageForm"/>    
             </td>
     
     </tr>
	</table>
  
  </html:form>
  
  <SCRIPT langugae='javascript' >
  
  	function startUp()
  	{
  	    
  		var selectedIndex=-1;
  		var index=0;
  		for(var i=0;i<document.gzStandardPackageForm.elements.length;i++)
  		{
  			if(document.gzStandardPackageForm.elements[i].type=='checkbox'&&document.gzStandardPackageForm.elements[i].name!='selbox')
  			{
  				if(document.gzStandardPackageForm.elements[i].checked==true)
  				{
  					if(selectedIndex==-1)
	  					selectedIndex=index;
  					else
  					{
  						alert(GZ_TEMPLATESET_INFO6+"！");
  						return;
  					}
  				}
  				index++;
  			}
  		}
  		if(selectedIndex==-1)
  		{
  			alert(GZ_TEMPLATESTE_INFO7+"！");
  			return;
  		}	
  		if(selectedIndex==startUpIndex)
  		{
  			alert(GZ_TEMPLATESET_INFO8+"！");
  			return;
  		}
  		
  		<% if(i==1){
  			out.print("var infos=document.gzStandardPackageForm.desc.value;");
  			}
  			else
  			{
	  		out.print("var infos=document.gzStandardPackageForm.desc[selectedIndex].value;");
  		    }  %>
  		var thecodeurl="/gz/templateset/standard/standardPackage.do?br_startDate=set"; 
		var return_vo= window.showModalDialog(thecodeurl,infos, 
       		 "dialogWidth:500px; dialogHeight:150px;resizable:no;center:yes;scroll:yes;status:no");
  		
  		if(return_vo==undefined)
  		{
  			return;
  		}
  		document.gzStandardPackageForm.startDate.value=return_vo;
  		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_startPack=start";
  		document.gzStandardPackageForm.submit();
  		
  	}
  
  	function del()
  	{
  		var selectedIndex=-1;
  		var index=0;
  		for(var i=0;i<document.gzStandardPackageForm.elements.length;i++)
  		{
  			if(document.gzStandardPackageForm.elements[i].type=='checkbox'&&document.gzStandardPackageForm.elements[i].name!='selbox')
  			{
  				if(document.gzStandardPackageForm.elements[i].checked==true)
  				{
  					if(selectedIndex==-1)
	  					selectedIndex=index;
  					else
  					{
  						alert(GZ_TEMPLATESET_INFO9+"！");
  						return;
  					}
  				}
  				index++;
  			}
  		}
  		if(selectedIndex==-1)
  		{
  			alert(GZ_TEMPLATESET_INFO10+"！");
  			return;
  		}
  		if(confirm(GZ_REPORT_CONFIRMDELETE))
  		{
  			<% if(i==1){
  			out.print("var pkg_id=document.gzStandardPackageForm.pkg_id.value");
  			}
  			else
  			{
	  		out.print("var pkg_id=document.gzStandardPackageForm.pkg_id[selectedIndex].value");
  		    }  %>
  			
  			var In_paramters="pkg_id="+pkg_id; 		
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3020010103'});			
  		}
  	}
  
    function returnInfo(outparamters)
	{
		var info=outparamters.getValue("info");   // 0:不包含  1:包含
		if(info==1)
		{
			alert(GZ_TEMPLATESET_INFO11);
			return;
		}
		else
		{
			document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_delPack=del&operate=del";
  			document.gzStandardPackageForm.submit();
		}
	}
  
  	function exports()
  	{
  		
  		var selectedIndex=-1;
  		var index=0;
  		for(var i=0;i<document.gzStandardPackageForm.elements.length;i++)
  		{
  			if(document.gzStandardPackageForm.elements[i].type=='checkbox'&&document.gzStandardPackageForm.elements[i].name!='selbox')
  			{	
  				if(document.gzStandardPackageForm.elements[i].checked==true)
  				{
  					if(selectedIndex==-1)
	  				{
	  					selectedIndex=index;
  					}
  					else
  					{
  						alert(GZ_TEMPLATESET_INFO12+"！");
  						return;
  					}
  				}
  				index++;
  			}
  		}
  		if(selectedIndex==-1)
  		{
  			alert(GZ_TEMPLATESET_INFO12+"！");
  			return;
  		}		
  		if(selectedIndex==startUpIndex)
  		{
  			document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_initExport=export";
  		    document.gzStandardPackageForm.submit();
  		}
  		else
  		{
  			alert(GZ_TEMPLATESET_INFO12+"！");
  			return;
  		}
  		
  		
  	}
  
  
  
  
  </SCRIPT>
  
  
  
  </body>
</html>
