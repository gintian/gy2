<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,				 
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<%
	  int i=0;
	  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<html>
<head>
<title></title>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<script type="text/javascript" src="../../../components/personPicker/PersonPicker.js"></script>
</head>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<script language="javascript">
function returns()
{
	//performanceImpForm.action="/selfservice/performance/performancePointPrivImplement.do?br_return=return";
	performanceImpForm.action="/selfservice/performance/performanceImplement.do?b_query=link&a_code=${performanceImpForm.a_code}";
	performanceImpForm.submit();
}
   function change()
   {
      performanceImpForm.action="/selfservice/performance/performanceMainBodyImplement.do?b_query=link";
      performanceImpForm.submit();
   }
   
   function deleterec()
   {
	   Ext.showConfirm("确认删除？",function(flag){
		   if(flag=="yes"){
			    performanceImpForm.action="/selfservice/performance/performanceMainBodyImplement.do?b_delete=delete";
     	   		performanceImpForm.submit();
		   }
	   });
   }

    function handworkSelect()
   {
   /*
   	var right_fields="";
   	var obj_value=handwork_selectObject("1","Usr")
   	if(obj_value.length>0)
   	{
	   	for(var i=0;i<obj_value.length;i++)
	   	{
	   		right_fields+="/"+obj_value[i];
	   	}
	   	alert(right_fields);
	   	performanceImpForm.action="/selfservice/performance/performanceImplement.do?b_save2=save&right_fields="+right_fields;     
	        performanceImpForm.submit();
	}*/
	var mainBodyID="${performanceImpForm.mainBodyID}";
	if(mainBodyID=="-all")//主体类别是全部
	{
		alert("请选择具体主体类别!");
		return;
	}
	/* var right_fields="";	
	var aplanid="${performanceImpForm.dbpre}";
	var opt = 1;
	var infos=new Array();
	infos[0]=aplanid;
	infos[1]=opt;

    var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt;
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
	var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=600px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
    if(objList==null)
		return false;	

	if(objList.length>0)
	{
		for(var i=0;i<objList.length;i++)		   	
		    right_fields+="/"+objList[i];		   		
		   		
	   	performanceImpForm.action="/selfservice/performance/performanceImplement.do?b_save2=save&right_fields="+right_fields;     
	    performanceImpForm.submit();
	} */
	
	//手工选择使用选人控件 来支持跨浏览器
	new PersonPicker({
	    titleText:"选择人员",
	    callback:function(persons){
	        var right_fields="";
	        for(var i=0;i<persons.length;i++){
	            right_fields += "/"+persons[i].id;
	        }
	        if(right_fields.length>0){
	            performanceImpForm.action="/selfservice/performance/performanceImplement.do?b_save2=save&isEncode=true&right_fields="+right_fields.substring(1);     
		        performanceImpForm.submit(); 
	        }
	    }
	},this).open();
   }	

    function conditionSearch(href)
   {
   	var mainBodyID="${performanceImpForm.mainBodyID}";
	if(mainBodyID=="-all")//主体类别是全部
	{
		alert("请选择具体主体类别!");
		return;
	}
   	var a=href+performanceImpForm.mainBodyID.value;
   	performanceImpForm.action=href+performanceImpForm.mainBodyID.value;
       
        performanceImpForm.submit();
   
   }



   
</script>

<body>
<html:form action="/selfservice/performance/performanceMainBodyImplement">
<%if("hl".equals(hcmflag)){%>
	<br>
<% } %>
<table width="85%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:10px">
 <tr>
    <td align="left" nowrap style="height:20px">       
        <bean:message key="lable.appraisemutual.examineobject"/>: <bean:write name="performanceImpForm" property="khobjname" filter="false"/>&nbsp;
    </td> 
 <tr>
    <td align="left" nowrap style="height:20px">  
        <bean:message key="lable.performance.perMainBodySort"/>
     	
     
     	<hrms:optioncollection name="performanceImpForm" property="mainBodySortList" collection="list" />
             <html:select name="performanceImpForm" property="mainBodyID" size="1" onchange="change();">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select>
        

    </td>         
 </tr>
</table>
<html:hidden property="flag" value="2" />
<table width="85%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
           <logic:equal name="performanceImpForm" property="status" value="3">
	            <td align="center" class="TableRow" nowrap>
	              <input type="checkbox" name="selbox"
											onclick="batch_select(this, 'pagination.select');">&nbsp;
	             </td>
           </logic:equal>
         
           <logic:equal name="performanceImpForm" property="status" value="5">
	            <td align="center" class="TableRow" nowrap>
	              <input type="checkbox" name="selbox"
											onclick="batch_select(this, 'pagination.select');">&nbsp;
	             </td>
           </logic:equal>  
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;          	
	    </td>
            
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.performance.perMainBody"/>            	
	    </td>
	   
	      	    	    		        	        	        
           </tr>
   	  </thead>
	<%
						int j = 0;
						%>
   	  <hrms:paginationdb id="element" name="performanceImpForm" sql_str="performanceImpForm.sql_str2" table="" where_str="performanceImpForm.where_str2" columns="id,B0110,E0122,E01A1,A0101,name"  page_id="pagination" pagerows="15" indexes="indexes">
	   		<%
									if (i % 2 == 0)
									{
							%>
							<tr class="trShallow">
							
								<%
										} else
										{
								%>
							
							<tr class="trDeep"	>							
								<%
										}
										i++;
								%>
		 <logic:equal name="performanceImpForm" property="status" value="3">
            <td align="center" class="RecordRow" nowrap>
          		 <hrms:checkmultibox name="performanceImpForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
            </td>
         </logic:equal>
         <logic:equal name="performanceImpForm" property="status" value="5">
            <td align="center" class="RecordRow" nowrap>
          		 <hrms:checkmultibox name="performanceImpForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
            </td>
         </logic:equal>
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          &nbsp;	<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;            
	    </td>
            <td align="left" class="RecordRow" nowrap>
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          &nbsp;	<bean:write name="codeitem" property="codename" />&nbsp;     
	    </td>
            <td align="left" class="RecordRow" nowrap>
                     &nbsp; <bean:write name="element" property="a0101" filter="false"/>&nbsp;
	    </td>  
	     <td align="left" class="RecordRow" nowrap>
            	&nbsp;<bean:write name="element" property="name" filter="false"/>&nbsp;
	    </td> 
	             	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
        
</table>
<table  width="85%" align="center"  class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="performanceImpForm" property="pagination" nameId="performanceImpForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table> 

<table  width="85%" align="center">
          <tr>
            <td align="center" style="height:20px"> 
             <logic:equal name="performanceImpForm" property="status" value="3">
                 <logic:notEqual name="performanceImpForm" property="mainBodyID" value="5">	
                
              	  <html:button  styleClass="mybutton" property="br_handworkSelect" onclick="handworkSelect()" >
            		     <bean:message key="lable.performance.handworkselect"/> 
	          </html:button>
              	
              	 <input type="button" name="addbutton"  value="<bean:message key="kq.wizard.term"/><bean:message key="column.select"/>" class="mybutton" onclick="conditionSearch('/selfservice/performance/hquery_interface.do?b_query2=link&a_inforkind=1&plan_id=${performanceImpForm.dbpre}&object_id=${performanceImpForm.objectID}&flag=2&body_id=')">  
                 <input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick="deleterec()">  
             	</logic:notEqual>
             </logic:equal>
             <logic:equal name="performanceImpForm" property="status" value="5">
                 <logic:notEqual name="performanceImpForm" property="mainBodyID" value="5">
                 	
              	  <html:button  styleClass="mybutton" property="br_handworkSelect" onclick="handworkSelect()" >
            		     <bean:message key="lable.performance.handworkselect"/> 
	          </html:button>
              	
              	 <input type="button" name="addbutton"  value="<bean:message key="kq.wizard.term"/><bean:message key="column.select"/>" class="mybutton" onclick="conditionSearch('/selfservice/performance/hquery_interface.do?b_query2=link&a_inforkind=1&plan_id=${performanceImpForm.dbpre}&object_id=${performanceImpForm.objectID}&flag=2&body_id=')">  
                 <input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick="deleterec()">  
             	 </logic:notEqual>
             </logic:equal>
             
             
             <input type="button" name="mybutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returns()">    
             <!--<hrms:submit styleClass="mybutton" property="br_return" ><bean:message key="button.return"/></hrms:submit>-->
            </td>
          </tr>          
</table>





</html:form>
<script>
	//bug 34690 表格上边线与下拉框重叠     wangb 20180208
	var form = document.getElementsByName('performanceImpForm')[0];
	var table1 = form.getElementsByTagName('table')[0];
	table1.style.marginBottom = '4px';
</script>

</body>
</html>