<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.hire.employActualize.EmployActualizeForm,org.apache.commons.beanutils.LazyDynaBean" %>
<html>
<head>
<title></title>
</head>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>

<script language="javascript">
	//this.status ="招聘管理 / 人员甑选";
	 //排序
   function taxis() 
   {
   		var In_paramters="tableName=zp_pos_tache"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnTaxis,functionId:'3000000104'});			
   }

	
   function ReturnTaxis(outparamters)
   {
   		var fields_temp=outparamters.getValue("fields");		
   		var fields=new Array();
   		for(var i=0;i<fields_temp.length;i++)
   		{
   			////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
   			var a_field=fields_temp[i].split("<@>");
   			fields[i]=a_field
   		}
    	 var orderSql=taxisDialog("zp_pos_tache",fields);
    	 
    	 if(orderSql)
    	 {
	    	 document.employActualizeForm.orderSql.value=orderSql;
    	 	 employActualizeForm.action='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_query=${employActualizeForm.linkDesc}&model=4';
    	 	 employActualizeForm.submit();
    	 }
    	
   }

	 //查询
   function query()
   {
		var In_paramters="tableName=zp_pos_tache"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnQuery,functionId:'3000000104'});			
   }
   
   function ReturnQuery(outparamters)
   {
   		
   		var fields_temp=outparamters.getValue("fields");		
   		var fields=new Array();
   		for(var i=0;i<fields_temp.length;i++)
   		{
   			////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
   			var a_field=fields_temp[i].split("<@>");
   			fields[i]=a_field
   		}
    	 var extendSql=generalQuery("zp_pos_tache",fields);
    	
    	 if(extendSql)
    	 {
	    	 document.employActualizeForm.extendSql.value=extendSql;
    	 	 employActualizeForm.action='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_query=${employActualizeForm.linkDesc}&model=4';
    	 	 employActualizeForm.submit();
    	 }
  
   }


	function returnInfo(outparamters)
	{
		var info=outparamters.getValue("info");
		if(info=='1')
		{
			 employActualizeForm.action='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_query=${employActualizeForm.linkDesc}&model=4';
    	 	 employActualizeForm.submit();
		}
		
			
	}
	
	
	
	
	
	//评论
	function review()
	{
		var ids="";
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].checked==true)
			{
				ids+="#"+document.forms[0].elements[i].value;
			}
		
		}
		if(ids=="")
		{
			alert(SELECT_PERSON_TO_REMARK+"！");
			return;
		}
	    employActualizeForm.action='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_review=link';
        employActualizeForm.submit();
		
	}

	function executeOutFile(flag)
	{
		var table = document.getElementById('ta');
		var cellCount = table.rows[0].cells.length;
		
		var fieldWidths;
		for(var i=0;i<cellCount-1;i++)
		{
			fieldWidths+="/"+table.rows[0].cells[i].offsetWidth;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("fieldWidths",fieldWidths.substring(10));
		hashvo.setValue("tablename",'zp_pos_tache');
	    hashvo.setValue("codeid","${employActualizeForm.codeid}");
	    hashvo.setValue("extendSql","${employActualizeForm.extendSql}");
	    hashvo.setValue("orderSql","${employActualizeForm.orderSql}");
	    var In_paramters="flag="+flag;  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
		
	}

	 //输出 EXCEL OR PDF
    function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		var flag=outparamters.getValue("flag");
		outName = decode(outName);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
		
	}
	
	
	function setState(obj)
	{
		
		var hashvo=new ParameterSet();
	    hashvo.setValue("state",obj.value);
	    hashvo.setValue("id",obj.name);
	    var In_paramters="flag=1" 
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000123'},hashvo);
		//employActualizeForm.action='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_setState=link&flag=1&state='+state;
       // employActualizeForm.submit();
	
	}
	
	
	//人岗匹配
	function autoFilter()
	{
		
		var In_paramters="codeid=${employActualizeForm.codeid}";  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'3000000140'});
	
	}
	
	function returnInfo2(outparamters)
	{
		 employActualizeForm.action='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_query=${employActualizeForm.linkDesc}&model=4';
    	 employActualizeForm.submit();
	
	}
	
	
	function getSelect(state,name)
	{
		var str="${employActualizeForm.hireStateStr}"
		document.write("<select name='"+name+"'  onchange='setState(this)'> ");
		if(str.length>0)
		{	
			str=str.substring(1);
			var ar_str=str.split("~");
			for(var i=0;i<ar_str.length;i++)
			{
				var temp=ar_str[i];
				var temp_arr=temp.split("&");
				document.write("<option value='"+temp_arr[0]+"' ");
				if(state==temp_arr[0])
					document.write("selected");
				document.write("  >"+temp_arr[1]+"</option>");
				
			
			}
		}
		document.write("</select>");
	
	
	}
	
	//通过条件查询 进行人岗匹配
	function manselect()
	{
		employActualizeForm.action='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_condition=link&&operate2=init';
        employActualizeForm.submit();
	}
	
   function setPageFormat()
   {
   		
   		var param_vo=oageoptions_selete("3","${employActualizeForm.username}");
   
   }
   
   function goback(filk)
   {
   		var hashvo=new ParameterSet();
   		hashvo.setValue("flag",filk);
   		var In_paramters="flag="+filk; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:goback2,functionId:'3000000148'});			

   }
   function goback2(outparamters)
   {	
   		this.status="";
   		window.open("/templates/menu/hire_m_menu2.do?b_query=link&module=7","i_body")
   }
   
   
   function fillInof()
   {
   		employActualizeForm.action='/hire/zp_interface/applyuseraccount.jsp?operate=add&clear=1';
   		employActualizeForm.target="i_body";
   		employActualizeForm.submit();
   }
   
   
   function deleteUser()
   {
   
  	    var ids="";
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].checked==true)
			{
				ids+=document.forms[0].elements[i].value+"#";
			}
		
		}
		if(ids=="")
		{
			alert(SELECT_TO_DELETE_PERSON+"！");
			return;
		}
   		employActualizeForm.action='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_deleteUser=link';
        employActualizeForm.submit();
   }
   
   
   function setStatus(state)
   {
        var ids="";
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].checked==true)
			{
				ids+=document.forms[0].elements[i].value+"#";
			}
		
		}
		if(ids=="")
		{
			alert(SELECT_PERSON+"！");
			return;
		}
   		employActualizeForm.action='/hire/employActualize/personnelFilter/personnelFilterTree.do?b_setState=link&flag=1&state='+state;
        employActualizeForm.submit();
   }
   
   
</script>

<body>
<hrms:themes></hrms:themes>
<html:form action="/hire/employActualize/personnelFilter/personnelFilterTree">
	
<table><tr><td>&nbsp;</td><td>	
	<hrms:menubar menu="menu1" id="menubar1">
	<hrms:menuitem name="file" label="文件" >						
						<hrms:menuitem name="mitem1" label="生成PDF" icon="/images/print.gif" url="executeOutFile(1)"  />
						<hrms:menuitem name="mitem2" label="生成EXCEL" icon="/images/print.gif" url="executeOutFile(2)"  />
						<hrms:menuitem name="mitem3" label="页面设置" icon="/images/prop_ps.gif" url="setPageFormat()"  />
						<hrms:menuitem name="mitem4" label="返回" icon="/images/add_del.gif" url="goback(2)"  />
	</hrms:menuitem>
	<hrms:menuitem name="file" label="编辑" >						
						<hrms:menuitem name="mitem6" label="查询" icon="/images/quick_query.gif" url="query()"  />
						<hrms:menuitem name="mitem6" label="排序" icon="/images/quick_query.gif" url="taxis()"  />
						<hrms:menuitem name="mitem6" label="删除" icon="/images/del.gif" url="deleteUser()"   />
	</hrms:menuitem>
	<hrms:menuitem name="operate" label="操作" >		

						<hrms:menuitem name="mitem6" label="手工填写人员信息" icon="/images/cards.bmp" url="fillInof()"  function_id="310211"  />					
						<hrms:menuitem name="mitem6" label="人岗匹配" icon="/images/cards.bmp" url="autoFilter()"   function_id="310212" />		
						<hrms:menuitem name="mitem6" label="条件选人" icon="/images/sb.gif" url="manselect()"  function_id="310213" />								
						<hrms:menuitem name="mitem6" label="评语" icon="/images/edit.gif" url="review()"  />	

	</hrms:menuitem>
	<hrms:menuitem name="sets" label="状态设置" >						
						<hrms:menuitem name="mitem6" label="已选" icon="/images/readwrite_obj.gif" url="setStatus('12')"  />
						<hrms:menuitem name="mitem6" label="待选" icon="/images/readwrite_obj.gif" url="setStatus('11')"  />
						<hrms:menuitem name="mitem6" label="未选" icon="/images/readwrite_obj.gif" url="setStatus('10')"  />

	</hrms:menuitem>
	
</hrms:menubar>
	</td></tr></table><br>	
	<table id='ta' width="85%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
   	  	   
           <tr>      	
         	<logic:iterate id="element" name="employActualizeForm" property="tableHeadNameList"  offset="0"> 
	      		<td align="center" class="TableRow" nowrap>
	      		&nbsp;&nbsp;&nbsp;<bean:write name="element" filter="false"/>&nbsp;&nbsp;&nbsp;
	      		</td>
            </logic:iterate>
                     
               <td align="center" class="TableRow" nowrap>
	      		&nbsp;&nbsp;<bean:message key="kq.strut.more"/>&nbsp;&nbsp;
	      		</td>    	    
         </tr>
   	  </thead>
   	  
   	  
   	  
   	   <% int i=0; String className="trShallow"; %>
   	   <hrms:paginationdb id="element" name="employActualizeForm" sql_str="${employActualizeForm.select_str}" table="" where_str="${employActualizeForm.from_str}" order_by="${employActualizeForm.orderSql}" columns="${employActualizeForm.columns}"  page_id="pagination" pagerows="15" indexes="indexes">
			  <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
	   <tr class='<%=className%>' >
	   		<Input type='hidden' name='id' value='<bean:write name="element" property="id" />' />
            <td align="center" class="RecordRow" nowrap>
             	 	<hrms:checkmultibox name="employActualizeForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
            </td>
      	    <td align="center" class="RecordRow" nowrap>
            	&nbsp;<bean:write name="element" property="a0101" />&nbsp;
            </td>
            <td align="center" class="RecordRow" nowrap>
            	&nbsp;<bean:write name="element" property="departname" />&nbsp;
            </td>
              <td align="center" class="RecordRow" nowrap>
            	&nbsp;<bean:write name="element" property="codeitemdesc" />&nbsp;
            </td>
            
      
      
                <%
           		   	 
           		   	 EmployActualizeForm employActualizeForm=(EmployActualizeForm)session.getAttribute("employActualizeForm");
           		   	 ArrayList tableColumnsList=employActualizeForm.getTableColumnsList();
           		   	 for(int a=4;a<tableColumnsList.size()-1;a++)
           		   	 {
           		   	 	LazyDynaBean aBean=(LazyDynaBean)tableColumnsList.get(a);
           		   	 	String itemid=(String)aBean.get("itemid");
           		   	 	String codesetid=(String)aBean.get("codesetid");
           		   	 	if(codesetid.equals("0"))
           		   	 	{
           		    %>
	           		    <td align="center" class="RecordRow" nowrap>
	           		    	<bean:write  name="element" property="<%=itemid%>" filter="true"/>
	           		    </td>
           		    
           		    <%
           		    	}
           		    	else
           		    	{
           		    %>
           		    	 <td align="center" class="RecordRow" nowrap>
				          	<hrms:codetoname codeid="<%=codesetid%>" name="element" codevalue="<%=itemid%>" codeitem="codeitem" scope="page"/>  	      
				          	<bean:write name="codeitem" property="codename" />&nbsp;
					        </td> 
           		    	
           		    <%
           		    	}
           		      }
           		    %> 	
           		    <td align="center" class="RecordRow" nowrap>
	            	&nbsp;	<script language='javascript'>
	            			getSelect('<bean:write  name="element" property="state" filter="true"/>','<bean:write  name="element" property="id" filter="true"/>');
	            		</script>&nbsp;
                    </td>
           		     <td align="center" class="RecordRow" nowrap>
           		     
           		     	<a href="/hire/employActualize/personnelFilter/personnelFilterTree.do?b_desc=desc&dbName=${employActualizeForm.dbName}&id=<bean:write  name="element" property="id" filter="true"/>">
		               <img src="/images/edit.gif" border=0>
		                </a>
           		     </td>	        	        	        
          </tr>
        </hrms:paginationdb>
   	  
 	</table>  	
 	
 	
<table  width="80%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="hmuster.label.paper"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="employActualizeForm" property="pagination" nameId="employActualizeForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table> 

	

 <input type='hidden' name='extendSql' value=" " />
<input type='hidden' name='orderSql'  value=" " />	
</html:form>
</body>
</html>