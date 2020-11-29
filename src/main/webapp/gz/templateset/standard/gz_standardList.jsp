<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		int versionFlag = 1;
		//zxj 20160613  薪资标准归属单位不再区分标准版专业版
		//if (userView != null)
		//	versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版	
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
 %>
<html>
  <head>
   
  </head>
  <script language='javascript' >
  	
  	function goback()
  	{
  		document.salaryStandardForm.action="/gz/templateset/standard/standardPackage.do?b_query=query";
  		document.salaryStandardForm.submit();
  	}
  	
  	function newStandard()
  	{
  		document.salaryStandardForm.action="/gz/templateset/standard.do?b_add=query&opt=new";
  		document.salaryStandardForm.submit();
  	}
  	
  	function del()
  	{
  		<logic:equal name="salaryStandardForm" property="pkgIsActive" value="1" >
  		//	alert("薪资标准所属的标准历史沿革处于启用状态,所以不能执行删除操作!");
  		//	return;
  		</logic:equal>
  		var obj2 = document.getElementsByName("eable");
  		var names = document.getElementsByName("names");
  		var j=0;
  		for(var i=0;i<document.salaryStandardForm.elements.length;i++)
  		{
  			if(document.salaryStandardForm.elements[i].type=='checkbox'&&document.salaryStandardForm.elements[i].name!='selbox')
  			{
  				if(document.salaryStandardForm.elements[i].checked==true)
  				{
  					var editable=obj2[j].value;
  					if(editable=='0')
  					{
  					    alert("【"+names[j].value+"】 不是您创建的，您没有删除的权限!");
  					    return;
  					}
  					  
  				}
  				j++;
  			}
  		}
  		if(confirm(GZ_TEMPLATESET_INFO17+'?'))
  		{
  			document.salaryStandardForm.action="/gz/templateset/standard.do?b_del=delete&operate=del";
  		    document.salaryStandardForm.submit();
  		}
  	}
  	
  	
  	function resetName()
  	{
  		var selectedIndex=-1;
  		var index=0;
  		var obj2 = document.getElementsByName("eable");
  		var names = document.getElementsByName("names");
  		for(var i=0;i<document.salaryStandardForm.elements.length;i++)
  		{
  			if(document.salaryStandardForm.elements[i].type=='checkbox'&&document.salaryStandardForm.elements[i].name!='selbox')
  			{
  				if(document.salaryStandardForm.elements[i].checked==true)
  				{
  					if(selectedIndex==-1)
	  					selectedIndex=index;
  					else
  					{
  						alert(GZ_TEMPLATESET_SELECTSINGLESTANDARD+"！");
  						return;
  					}
  				}
  				index++;
  			}
  		}
  		if(selectedIndex==-1)
  		{
  			alert(GZ_TEMPLATESET_INFO18+"！");
  			return;
  		}
  		var eable = obj2[selectedIndex].value;
  		if(eable=='0')
  		{
  			alert("【"+names[selectedIndex].value+"】 不是您创建的，您没有重命名的权限!");
  			return;
  		}
  		var name=prompt(GZ_TEMPLATESET_INFO19,'');
  		if(name)
  		{
  			if(containSpecial(name)){
  				alert("薪资标准表名称中不允许有特殊字符！");
  				return;
  			}
     		if(name.length>0)
     		{
     			var id = document.getElementsByName("ids")[selectedIndex].value;
     			document.salaryStandardForm.gzStandardName.value=name;
     			document.salaryStandardForm.action="/gz/templateset/standard.do?b_del=del&operate=resetName&id="+id;
     			document.salaryStandardForm.submit();
     		}
  		}
  	}
  	function containSpecial( s ) { 
  		var containSpecial = RegExp(/[(\ )(\~)(\!)(\@)(\#) (\$)(\%)(\^)(\&)(\*)(\()(\))(\-)(\_)(\+)(\=) (\[)(\])(\{)(\})(\|)(\\)(\;)(\:)(\')(\")(\,)(\.)(\/) (\<)(\>)(\?)(\)]+/); 
  		return (containSpecial.test(s));
  		 } 
  	
  	function produceExcel()
  	{
  		var ids=new Array();
  		var num=0;
  		var index=0;
  		var id_obj=eval("document.salaryStandardForm.ids");
  		
  		var checkboxNum=0;
  		for(var i=0;i<document.salaryStandardForm.elements.length;i++)
  		{
	  			if(document.salaryStandardForm.elements[i].type=='checkbox'&&document.salaryStandardForm.elements[i].name!='selbox')
	  			{
	  				checkboxNum++;	  				
	  			}
  		}
  		
  		
  		for(var i=0;i<document.salaryStandardForm.elements.length;i++)
  		{
  			if(document.salaryStandardForm.elements[i].type=='checkbox'&&document.salaryStandardForm.elements[i].name!='selbox')
  			{
  				if(document.salaryStandardForm.elements[i].checked==true)
  				{
  					if(checkboxNum>1)
	  					ids[num++]=id_obj[index].value;
  					else
  						ids[num++]=id_obj.value;
  				}
  				index++;
  			}
  		}
  		if(ids.length==0)
  		{
  			alert(GZ_TEMPLATESET_INFO20+"！");
  			return;
  		}
  	    var hashvo = new ParameterSet();
		hashvo.setValue("standardids",ids);
		var In_parameters="pkg_id=${salaryStandardForm.pkg_id}"; 
		var request = new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:returnInfo,functionId:'3020010124'},hashvo);
  	}
  	
  	function returnInfo(outparamters)
  	{	var name = outparamters.getValue("fileName");
  		name = getDecodeStr(name);
  		var win=open("/servlet/vfsservlet?fileid="+name+"&fromjavafolder=true","excel");
  	}
  function selectGSUnit(id,pkg_id,b0110)
  	{
  	  //select_org_emp_dialog(0,1,0,1,0,1);
  	  //select_org_emp_dialog(flag,selecttype,dbtype,priv,isfilter,loadtype)
  	  var viewunit  ='0';
  	  var obj = document.getElementById("isOperOrManage").value;
  	  if(obj=='1')
  	     viewunit="1";
  	   var theurl="/system/logonuser/org_employ_tree.do?flag=0`selecttype=1`dbtype=0`priv=1`isfilter=0`loadtype=1`prompt=1`selectedValues="+b0110+"`viewunit="+viewunit;
     var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);         
     var return_vo= window.showModalDialog(iframe_url,1, "dialogWidth:305px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
	 if(return_vo)
	 {
	    var tmp=return_vo.content;
	    var ttt=return_vo.title;
	    var hashvo = new ParameterSet();
		hashvo.setValue("id",id);
		hashvo.setValue("pkg_id",pkg_id);
		hashvo.setValue("content",getEncodeStr(tmp));
		var request = new Request({method:'post',asynchronous:false,onSuccess:save_ok,functionId:'90100170016'},hashvo);
	    
	 }
  	}
  	function save_ok(outparameters)
  	{
  	   document.salaryStandardForm.action="/gz/templateset/standard.do?b_query=query&pkg_id=${salaryStandardForm.pkg_id}";
  	   document.salaryStandardForm.submit();
  	}
  </script>
  
 
  <body>
    <%
	int i=0;
    %>
    <html:form action="/gz/templateset/standard">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

    <table width='100%' align='center' style="margin-left:-5px;"><tr><td>
    <html:hidden name="salaryStandardForm" property="isOperOrManage"/>
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
	   	  <thead>
	           <tr>
	             <td width="5%" height="25" align="center" nowrap class="TableRow">
	                 <input type="checkbox" name="selbox" onclick="batch_select(this,'standardlistform.select');" title='<bean:message key="label.query.selectall"/>'>             </td>
	             <td width="5%" align="center" nowrap class="TableRow"><bean:message key="gz.templateset.standardNo"/></td>
	             <td width="30%" align="center" nowrap class="TableRow"><bean:message key="gz.formula.standart.tablename"/></td>
	             <%if(versionFlag==1){ %>
	             <td width="14%" align="center" nowrap class="TableRow"><bean:message key="lable.lawfile.ascriptionunit"/></td>
	             <%} %>
	             <td width="14%" align="center" nowrap class="TableRow"><div align="center"><bean:message key="gz.templateset.orientationPoint"/></div></td>                  
	             <td width="13%" align="center" nowrap class="TableRow"><div align="center">
	              <bean:message key="gz.templateset.orientationSubPoint"/>
	               </div></td>  
	             <td width="6%" align="center" nowrap class="TableRow"><bean:message key="gz.templateset.lengthwaysPoint"/></td>
	             <td width="6%" align="center" nowrap class="TableRow"><bean:message key="gz.templateset.lengthwaysSubPoint"/></td>
	             <td width="6%" align="center" nowrap class="TableRow"><bean:message key="label.gz.resultPoint"/></td>
	             <td width="13%" align="center" nowrap class="TableRow"><bean:message key="menu.gz.edit"/></td>  
	            </tr>
	   	  </thead>   
	           <hrms:extenditerate id="element" name="salaryStandardForm" property="standardlistform.list" indexes="indexes"  pagination="standardlistform.pagination" pageCount="10" scope="session">
		           <%
		          if(i%2==0)
		          {
		          %>
		          <tr class="trShallow"  onclick='tr_onclick(this,"#F3F5FC");' >
		          <%}
		          else
		          {%>
		          <tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");'>
		          <%
		          }
		          i++;          
		          %>  		    
	            <td align="center" class="RecordRow" nowrap>
	            
	               <hrms:checkmultibox name="salaryStandardForm" property="standardlistform.select" value="true" indexes="indexes"/>         
	           	   <input type='hidden' name='ids' value='<bean:write name="element" property="id" filter="true"/>'  />
	           	   <input type="hidden" name="eable" value="<bean:write name="element" property="iseditable" filter="true"/>"/>
	           	   <input type="hidden" name="names" value="<bean:write name="element" property="name" filter="true"/>"/>
	           	     </td>
	            <td align="left" class="RecordRow" nowrap>&nbsp;<bean:write name="element" property="id" filter="true"/></td>
	            <td align="left" class="RecordRow" nowrap>&nbsp;<bean:write name="element" property="name" filter="true"/></td>
	            <%if(versionFlag==1){ %>
	            <td align="center" class="RecordRow" style="border-right:#C4D8EE 0pt solid" nowrap>
	            <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	            <tr>
	            <td align="left" width="20%">&nbsp;
	            <logic:equal value="1" name="element" property="iseditable">
	            <img src="../../../images/edit.gif" border="0" style="cursor:hand;" onclick="selectGSUnit('<bean:write name="element" property="id" filter="true"/>','<bean:write name="element" property="pkg_id" filter="true"/>','<bean:write name="element" property="b0110" filter="true"/>');"/>
	            </logic:equal>
	            </td>
	           
	            <td align="left" width="80%" title="<bean:write name="element" property="allunit" filter="true"/>" nowrap>
	                 <bean:write name="element" property="gsunit" filter="true"/>
	                 </td>
	                 </tr>
	                 </table>
                </td>
                 <%} %>
	             <td align="center" class="RecordRow" nowrap>&nbsp;<bean:write name="element" property="hfactor" filter="true"/>&nbsp;</td>
	            <td align="center" class="RecordRow" nowrap>&nbsp;<bean:write name="element" property="s_hfactor" filter="true"/>&nbsp;</td>
	            <td align="center" class="RecordRow" nowrap>&nbsp;<bean:write name="element" property="vfactor" filter="true"/>&nbsp;</td>
	            <td align="center" class="RecordRow" nowrap>&nbsp;<bean:write name="element" property="s_vfactor" filter="true"/>&nbsp;</td>
	            <td align="center" class="RecordRow" nowrap>&nbsp;<bean:write name="element" property="item_result" filter="true"/>&nbsp;</td>
	            <td align="center" class="RecordRow" nowrap> 
	            <% LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
	               String standardID=(String)abean.get("id");
	               String m_standardID=PubFunc.encrypt(standardID);
	             %>
		       	<hrms:priv func_id="3241009">  
		            <a href="/gz/templateset/standard.do?b_initItem=init&opt=edit&m_standardID=<%=m_standardID%>&standardID=<bean:write name="element" property="id" filter="true"/>&isedit=<bean:write  property="iseditable" name="element"/>"  target="il_body">
		            <img src="../../../images/edit.gif" width="11" height="17" border=0>
		            </a>
                </hrms:priv>
                </td>   
	          </tr>
	        </hrms:extenditerate>
	</table>
    
    <table  width="100%" align="center"  class='RecordRowP' >
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="salaryStandardForm" property="standardlistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="salaryStandardForm" property="standardlistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="salaryStandardForm" property="standardlistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	        <td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="salaryStandardForm" property="standardlistform.pagination" nameId="standardlistform">
					</hrms:paginationlink>
			</td>
		</tr>
   </table>
    </td></tr></table>
    
    <table  width="85%" align="center">
     <tr>
       <td align="center"  nowrap colspan="4">
       <hrms:priv func_id="3241008">    
           <input type="button" name="b_add" value="<bean:message key="menu.gz.new"/>" class="mybutton" onClick="newStandard()"> 
	   </hrms:priv>
	   <hrms:priv func_id="3241010">    
	       <input type="button" name="addbutton"  value="<bean:message key="menu.gz.delete"/>" class="mybutton" onClick="del()">  
        </hrms:priv>     
        <hrms:priv func_id="3241011">         
           <input type="button" name="addbutton2"  value="<bean:message key="button.rename"/>" class="mybutton" onClick="resetName()">
        </hrms:priv>     
        <hrms:priv func_id="3241012">         
           <input type="button" name="addbutton2"  value="<bean:message key="report.actuarial_report.exportExcel"/>" class="mybutton" onClick="produceExcel()">
        </hrms:priv>     
             
           <input type="button" name="addbutton222"  value="<bean:message key="kq.emp.button.return"/>" class="mybutton" onClick="goback()">
           <Input type='hidden' name='gzStandardName' value="" />
         
             </td>
     </tr>
</table>
    
  
    
   </html:form>
  </body>
</html>
