<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.gz.gz_accounting.ImportPersonnelForm,
                 org.apache.commons.beanutils.LazyDynaBean,com.hrms.struts.valueobject.PaginationForm" %>

<html>
  <head>
   

  </head>
  <style>
	div#data {
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-COLLAPSE: collapse;
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
	width: 770px;
	height: 350px;
	overflow: auto;

	}
	
	
	
	
  </style>
  <hrms:themes />
  <body>
   <html:form action="/gz/gz_accounting/importMen">
   <table align="center" >
	   <tr>

	   <td  > 
	   		<html:select name="importPersonnelForm" property="fieldItemId" size="1" onchange="init(1)" >
			   <html:optionsCollection property="fieldItemList" value="dataValue" label="dataName"/>
			</html:select>  
	   </td>
	   <td id='var_value' nowrap >
  
	   </td>
	   <td align='right'    ><input type='button'  value='高级' onclick="hquery();"  class="mybutton" >  </td>
	   </tr>	   
	   <tr>
	   <td colspan='3' >
	   
	   <div id="data" class="common_border_color">
	    <%
           	ImportPersonnelForm importPersonnelForm=(ImportPersonnelForm)session.getAttribute("importPersonnelForm");
           	ArrayList tableHeadList=importPersonnelForm.getTableHeadList();
           	int flag=0;
           	if(importPersonnelForm.getTableDataListForm().getAllList().size()>0)
           		flag=2;
           	int colspan=tableHeadList.size()+1;
        %>
	   
	   
	   <table   border="0" cellspacing="0"   align="left" cellpadding="0" class="ListTable">
	   	  <thead>
          <tr  class="fixedHeaderTr" >
          	 <td align="center" width='40' class="TableRow" nowrap style="border-top:0px;border-left:0px;">
				 <input type="checkbox" name="allselect" value="0" onclick="allSelectClear(this);"/>
	         </td>         
		   	<logic:iterate id="element" name="importPersonnelForm" property="tableHeadList"  >
		   	  <td align="center"    class="TableRow" nowrap style="border-top:0px;">
				 <bean:write name="element" property="itemdesc" filter="true"/>
	          </td> 
		 	 </logic:iterate>
	   	   </tr>
	   	   </thead>
	   	   <% int i=0; %>
	   	   <hrms:extenditerate id="element" name="importPersonnelForm" property="tableDataListForm.list" indexes="indexes" pagination="tableDataListForm.pagination" pageCount="20" scope="session">
	   	   		<% i++;
		   	  	   if(i%2==1){ %>
		   	  	   <tr class='trShallow' >
		   	  	   <% } else { %>	   
		   	  	   	<tr class='trDeep'  >
		   	  	   <% } %>
	   	   		<td align="center" class="RecordRow" nowrap><input type='checkbox'  value='<bean:write name="element" property="a0100" filter="true"/>/<bean:write name="element" property="nbase" filter="true"/>'  name='right_fields'  /></td>
	   	   		<%  for(int j=0;j<tableHeadList.size();j++){
	   	   				LazyDynaBean a_bean=(LazyDynaBean)tableHeadList.get(j);
						String itemid=(String)a_bean.get("itemid");
						String itemtype=(String)a_bean.get("itemtype");
						String align="left";
						if(itemtype.equals("N"))
							align="right";
						%>
					<td align="<%=align%>" class="RecordRow_right" nowrap>	
					<bean:write name="element" property="<%=itemid%>" filter="true"/>
					</td>	
			    	<% 
	   	   			}
	   	   		  %>
	   	   		  </tr>
	   	   
	   	   </hrms:extenditerate>
	   	   
	   	   <tr>
			  <td colspan="<%=colspan%>" class="RecordRow_right">
			  <table  width="99%" align="center">
			  <tr>
			  <td valign="bottom" class="tdFontcolor" nowrap>
				<bean:message key="hmuster.label.d"/>
				<bean:write name="importPersonnelForm" property="tableDataListForm.pagination.current" filter="true" />
				<bean:message key="hmuster.label.paper"/>
				<bean:message key="hmuster.label.total"/>
				<bean:write name="importPersonnelForm" property="tableDataListForm.pagination.count" filter="true" />
				<bean:message key="label.every.row"/>
				<bean:message key="hmuster.label.total"/>
				<bean:write name="importPersonnelForm" property="tableDataListForm.pagination.pages" filter="true" />
				<bean:message key="hmuster.label.paper"/>
				</td>
			     <td align="right" class="tdFontcolor" nowrap>
			     <p align="right">
					<hrms:paginationlink name="importPersonnelForm" property="tableDataListForm.pagination" nameId="tableDataListForm">
					</hrms:paginationlink>
					</p>
					</td>
			</td>
			</tr>
			</table>
			</td>
		</tr>
	   </table>
	   </div> 
	   
	    </td>
	   </tr>
	   <tr>
	   	<td colspan='3' align='center' >
	   	<hrms:priv func_id="324021202,325021202,327021202,327121202">
	   	   <input type='button'  value='<bean:message key="label.gz.setOrgSet"/>' onclick="setGzParam('${importPersonnelForm.salaryid}')"  class="mybutton" >
	    </hrms:priv>
	   		<input type='button'  id='enter'  value='引入选中人员' onclick='sub("0")'  class="mybutton" />
	   		<input type='button'  id='henter'  value='引入全部人员' onclick='sub("1")'  class="mybutton" />
	   		<input type='button'  value='<bean:message key="button.cancel"/>'  class="mybutton" onclick='window.close()' />
	   	 </td>
	   	 <html:hidden styleId="fac" property="factor" name="importPersonnelForm"/>
	   	  <html:hidden styleId="exp" property="expr" name="importPersonnelForm"/>
	   	 <html:hidden styleId="_right" property="allRightField" name="importPersonnelForm"/>
	   	 <html:hidden name="importPersonnelForm" property="isSalaryManager"/>
	   	 <html:hidden name="importPersonnelForm" property="isHistory"/>
	   	 <html:hidden name="importPersonnelForm" property="querytype"/>
	   </tr>
	   
   </table>
   <script language='javascript' >
   <% 
   	  if(request.getParameter("flag")!=null&&request.getParameter("flag").equals("1"))
   	  {
   	  	out.println("returnValue='1';");
   	  	out.println("window.close();");
   	  }
   	  
   	  if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("1")){
   			out.print("init(2);");
   	  }
   	  else
   	  {
   	  		out.print("init(0);");
   	  }
    %>
   	
   	
   	
   	
   	
   
   	function init(opt)
   	{
   		var value="";
   		if(opt==0)
   		{
   		   if(document.importPersonnelForm.fieldItemId.options[0])
	   	    	value=document.importPersonnelForm.fieldItemId.options[0].value;
	   	    else
	   	       return;
	   	}
   		else 
   		{
   		    if(document.importPersonnelForm.fieldItemId)
      			value=document.importPersonnelForm.fieldItemId.value;
      		else
      		   return;
   	    }
   		
   		
   		var temps=value.split("/");
   		var str="";
   		if(temps[1]=='A'&&temps[2]!='0')
   		{
   			str="<table><tr><td valign='middle'>请选择&nbsp;<input type='hidden' class='text4' name='p_value' "; // modify by xiaoyun 去掉冒号，改为空格 2014-9-2
   			if(opt==2)
	        	str+=" value='${importPersonnelForm.p_value}' ";
   			str+=" value='' >";
	        str+="<input type='text' class='text4' name='p_viewvalue' "
	        if(opt==2)
	        	str+=" value='${importPersonnelForm.p_viewvalue}' ";
	         str+=" size='20' >";
	        if(temps[2].toUpperCase()=='UN'||temps[2].toUpperCase()=='UM'||temps[2].toUpperCase()=='@K')
	        {
	           str+="&nbsp;</td><td><img  src='/images/code.gif' onclick='javascript:openInputCodeDialogOrgInputPos4(\""+temps[2]+"\",\"p_viewvalue\",\"${importPersonnelForm.mangerCodeValue}\",1);'/>";
	        }
	        else
	        {
               str+="&nbsp;</td><td><img  src='/images/code.gif' onclick='javascript:openInputCodeDialogText(\""+temps[2]+"\",\"p_viewvalue\",\"p_value\");'/>";
            }
   			str+="<input type='hidden' name='n_value' value='' ></td>";
   			str+="<td>&nbsp;&nbsp;&nbsp;<input type='button'  value='查询' onclick='search()'  class='mybutton' /></td></tr></table>";
   		}
   		if((temps[1]=='A'&&temps[2]=='0')||temps[1]=='M')
   		{
   			str="请输入&nbsp;<input type='text' name='p_value' ";
   			if(opt==2)
	        	str+=" value='${importPersonnelForm.p_value}' ";
   			str+="  size='20'  ><input type='hidden' name='n_value' value='' >";
   		}
   		if(temps[1]=='N')
   		{
   			str="请输入&nbsp;<input type='text' name='p_value' ";
   			if(opt==2)
	        	str+=" value='${importPersonnelForm.p_value}' ";
   			str+=" size='20'  ><input type='hidden' name='n_value' value='' >";
   		}
   		if(temps[1]=='D')
   		{
   			str+="开始日期&nbsp;";
   			str+="<input type='text'  name='p_value' size='10'  ";
   			if(opt==2)
	        	str+=" value='${importPersonnelForm.p_value}' ";
   		//	str+="  id='editor4'  extra='editor' dropDown='dropDownDate' /> "
   			str+="  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' /> ";
   			str+="结束日期&nbsp;";
   			str+="<input type='text'  name='n_value'  size='10' ";
   			if(opt==2)
	        	str+=" value='${importPersonnelForm.n_value}' ";
   			str+="  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' /> ";
   		//	str+="  id='editor5'  extra='editor' dropDown='dropDownDate' /> "
   		}
   		if(!(temps[1]=='A'&&temps[2]!='0'))
   			str+="&nbsp;&nbsp;&nbsp;<input type='button'  value='查询' onclick='search()'  class='mybutton' />";
	   	document.getElementById("var_value").innerHTML=str;
   		
   		
   	//	document.getElementById("var_value").innerHTML=value
   	
   	}
   	
   	
   	function search()
   	{
   		var value=document.importPersonnelForm.fieldItemId.value;
   		var temps=value.split("/");
   		if(temps[1]=='A'&&temps[2]!='0')
   		{
   			if(trim(document.importPersonnelForm.p_viewvalue.value).length==0)
   					document.importPersonnelForm.p_value.value="";	
   		}
   		if(temps[1]=='A'&&temps[2]=='0')
   		{
   		
   		}
   		if(temps[1]=='N')
   		{
   			var myReg =/^(-?\d+)(\.\d+)?$/
			if(!myReg.test(document.importPersonnelForm.p_value.value)) 
			{
						alert("请输入数字！");
						return;
			}
   		}
   		if(temps[1]=='D'&&(trim(document.importPersonnelForm.p_value.value).length>0||trim(document.importPersonnelForm.n_value.value).length>0))
   		{
   			var a=document.importPersonnelForm.p_value.value;
   			var b=document.importPersonnelForm.n_value.value;
   			if(!IsDate("开始日期",a))
   			{
   				document.importPersonnelForm.p_value.value="";
   				return;
   			}
   			if(!IsDate("结束日期",b))
   			{
   				document.importPersonnelForm.n_value.value="";
   				return;
   			}
   		}
   		document.importPersonnelForm.action="/gz/gz_accounting/importMen.do?b_query=query&opt=1&querytype=0&value="+value;
   		document.importPersonnelForm.submit();
   	}
  function IsDate(sm,mystring){  
       var reg = /^(\d{4})-(\d{1,2})-(\d{1,2})$/;  
       var str = mystring;  
       var arr = reg.exec(str);  
       if(str=="")   return   true;  
       if(!reg.test(str)||RegExp.$2>12||RegExp.$3>31){  
         alert("请保证"+sm+"中输入的日期格式为yyyy-mm-dd或正确的日期!");  
         return false;  
       }  
         return true;  
  }   
   	
   	
   	
   	function sub(type)
   	{
   	   if(type=='0')
   	   {
   	      var obj = document.getElementsByName("right_fields");
   		  if(obj)
   		  {
	   		  var num=0;
	   		  for(var i=0;i<obj.length;i++)
	   		  {
	   			  if(obj[i].checked)
	   				  num++;
	   		  }
	   		  if(num==0)
	   		  {
	   		    	alert("请选择需导入的人员!");
	   			   return;
	   		  }
	   	   }
	   	 }
	   	 else
	   	 {
	   	     var obj = document.getElementById("_right");
	   	     if(obj.value=="")
	   	         return;
	   	 }
	   document.getElementById("henter").disabled=true;
	   document.getElementById("enter").disabled=true;
	   document.importPersonnelForm.action="/gz/gz_accounting/importMen.do?b_import=import&flag=1&importtype="+type;
	   document.importPersonnelForm.submit();
   	}
   	 function allSelectClear(obj)
  {
     var selectObj=document.getElementsByName("right_fields");
     for(var i=0;i<selectObj.length;i++)
     {
        if(obj.checked)
           selectObj[i].checked=true;
        else
           selectObj[i].checked=false;
     }
  }
  function hquery()
  {
 // /general/inform/search/gmsearch.do?b_query=link&type="+type+"&a_code="+a_code+"&tablename="+tablename;
     var setid="${importPersonnelForm.fieldSetId}";
     var isSM="${importPersonnelForm.isSalaryManager}";
     var privflag="1";
     if(isSM=="N")
         privflag="0";
     thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type=1&ps_flag=2&a_code=all&privflag="+privflag+"&tablename=usr&fieldsetid=${importPersonnelForm.fieldSetId}";
      return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
     if(return_vo)
     {
         var obj = new Object();
         obj.expr=return_vo.expr;
	     obj.factor=return_vo.factor;
	     document.getElementById("exp").value=obj.expr;
	     document.getElementById("fac").value=obj.factor;
	    // document.getElementById("enter").disabled=true;
	     //document.getElementById("henter").disabled=true;
	     var ishistory="0";
	     if(setid!='a01' && setid !='A01')
	     {
	        if(confirm("从历史记录中查询请按\"确定\"，从当前记录查询请按\"取消\"！"))
	        {
	           ishistory="1";
	        }
	     }
	     document.getElementById("isHistory").value=ishistory;
	     document.importPersonnelForm.action="/gz/gz_accounting/importMen.do?b_query=query&opt=1&querytype=1&ishistory="+ishistory;
   	     document.importPersonnelForm.submit();
   	     
     }
     
  }
   	
   	
   </script>
   </html:form>
  </body>
</html>
