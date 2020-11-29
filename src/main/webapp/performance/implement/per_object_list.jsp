<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" 
"http://www.w3.org/TR/html4/frameset.dtd">
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
<hrms:themes />
<html>
<head>
<title></title>
<script language="JavaScript"src="../../../js/showModalDialog.js"></script>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<script type="text/javascript" src="../../../components/personPicker/PersonPicker.js"></script>
</head>

<script language="javascript">
	
   function change()
   {
      //performanceImpForm.action="/selfservice/performance/performanceImplement0.do";
      //performanceImpForm.target="il_body"
      performanceImpForm.action="/selfservice/performance/performanceImplement.do?b_query=link&a_code=${performanceImpForm.a_code}";
      performanceImpForm.submit();
   }
   
   function deleterec()
   {
	   //haosl add 20070425 未选中记录时，点击删除 应该给出相应提示 start
	    var isCheck=false;
	   	for(var i=0;i<document.forms[0].elements.length;i++)
	   	{
	   		if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].checked==true)
	   		{	
	   			isCheck=true;
                break;	
	   		}
	   	}

	   	if(!isCheck)
	   	{
	   		Ext.showAlert(SEL_KHOBJECT);
	   		return;
	   	}
	  //haosl add 20070425  未选中记录时，点击删除 应该给出相应提示  end
	 Ext.showConfirm("确认删除？",function(flag){
		 if(flag=="yes"){
			 performanceImpForm.action="/selfservice/performance/performanceImplement.do?b_delete=delete";
	  		 performanceImpForm.submit();
		 }
	 })
   }

	
   //批量选择考核主体
   function batchSelectMainBody()
   {
   	var selectedCount=0;
   	for(var i=0;i<document.forms[0].elements.length;i++)
   	{
   		if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].checked==true)
   		{
   			selectedCount++;
   		}
   	}
   	if(selectedCount==0)
   	{
   		Ext.showAlert(SEL_KHOBJECT);
   		return;
   	}
   	
   	performanceImpForm.action="/selfservice/performance/performanceImplement.do?b_batch=batch";     
        performanceImpForm.target="_parent";
        performanceImpForm.submit(); 
   
   }
   /**
	*选人 haosl 20170213 跨浏览器兼容
	*/
   function handworkSelect()
   {
	    var objectType="${performanceImpForm.objectType}";
	   	var plan_b0110="${performanceImpForm.plan_b0110}";	
	   	var adddepartment = false;
	   	var addpost = false;
	    var	addunit = false;
	   	var right_fields="";
	   	if(objectType=="1"){//团队
	   		adddepartment=true;  
	   		addpost=false;
   			addunit=true;
	   	}else if(objectType=="2"){//人员	
	   		adddepartment=false;  
   			addpost=false;
			addunit=false;
	   	}else if(objectType=="3"){//单位
	  		adddepartment=false;  
			addpost=false;
			addunit=true;
	   	}else if(objectType=="4"){//部门
	  		adddepartment=true;  
   			addpost=false;
			addunit=false;
   		}
    //排除已选的考核对象
    var hashvo=new ParameterSet();     
    hashvo.setValue("object_copy", '');
    hashvo.setValue("objectType",objectType);
    hashvo.setValue("plan_id","${performanceImpForm.dbpre}");
    hashvo.setValue("opt",'38');
	new Request({method:'post',onSuccess:function(outparamters){
		var deprecate = outparamters.getValue("objectids");//选人控件要显示的人员
	    //手工选择使用选人控件 来支持跨浏览器
		new PersonPicker({
		    titleText:objectType=="2"?"选择人员":"选择机构",
		    deprecate:deprecate,
		    adddepartment:adddepartment,  
   			addpost:addpost,
			addunit:addunit,
		    callback:function(persons){
		        var right_fields="";
		        for(var i=0;i<persons.length;i++){
		            right_fields += ("/"+persons[i].id);
		        }
		        if(right_fields.length>0){
			        performanceImpForm.action="/selfservice/performance/performanceImplement.do?b_save=save&isEncode=true&right_fields="+right_fields.substring(1);     
			      	performanceImpForm.target="_parent";
			        performanceImpForm.submit(); 
		        }
		    }
		},this).open();
	},functionId:'9023000003'},hashvo);
   }
   
   function conditionSearch(href)
   {
   	for(var i=0;i<document.performanceImpForm.elements.length;i++)
   	{
   		if(document.performanceImpForm.elements[i].type=="checkbox")
   			document.performanceImpForm.elements[i].checked=false;
   	}
   	performanceImpForm.action=href;
   	performanceImpForm.target="_parent";
        performanceImpForm.submit();
   
   }
  	function exportExcel1()
  	{
  		if("${performanceImpForm.dbpre}"==0){
  			Ext.showAlert('请选择一个考核计划！');
  			return;
  		}
  		var hashvo=new ParameterSet();
		hashvo.setValue("planID","${performanceImpForm.dbpre}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'90100140018'},hashvo);
	}
	function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
//		var name=outName.substring(0,outName.length-1)+".xls";
//		var win=open("/servlet/DisplayOleContent?filename="+outName,"xls");	
		//20/3/5 xus vfs改造
		var win=open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true");	
	}
function setdynamainbodypropotion(planid)
{
	if(planid==0){
		Ext.showAlert('请选择一个考核计划！');
		return;
	}
	var config = {
	      	width:750,
	      	height:550,
			type:'2',
    	}
	var theurl="/performance/implement/kh_mainbody/set_dyna_main_rank/setdynamainbodypropotion.do?b_ini=link`optString=3`planid="+planid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    modalDialog.showModalDialogs(iframe_url,null,config);	
}
function batchSelBodys()
{
	var objectIDs="";
   	for(var i=0;i<document.forms[0].elements.length;i++)
   	{
   		if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].checked==true)
   		{	
   			if(document.forms[0].elements[i+1].name=="objectId")
   					objectIDs+=document.forms[0].elements[i+1].value+"@";	
   		}
   	}

   	if(objectIDs=="")
   	{
   		Ext.showAlert(SEL_KHOBJECT);
   		return;
   	}
   	
    var target_url="/performance/implement/kh_mainbody/mainbodySel.do?b_query=link`objIDs="+objectIDs+"`fenfaPlanId="+performanceImpForm.dbpre.value;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    var config = {
      	width:800,
      	height:600,
      	type:"2"
    }
    modalDialog.showModalDialogs(iframe_url,"mainBodyType",config);
}
function copyBody(object_id,object_type,plan_id)
{
   
   	var hashvo=new ParameterSet();     
    hashvo.setValue("object_copy", object_id);
    hashvo.setValue("plan_id",plan_id);
    hashvo.setValue("opt",'14');
    var request=new Request({method:'post',onSuccess:isHaveBodys,functionId:'9023000003'},hashvo);
}
function isHaveBodys(outparamters)
{
    var info=outparamters.getValue("info");
    copyBody_object_copy=outparamters.getValue("object_copy");
    copyBody_plan_id=outparamters.getValue("plan_id");

	if(info.length==0)
	{
			var opt = 5;
			var infos=new Array();
			infos[0]=copyBody_plan_id;
			infos[1]=opt;
			infos[2]=copyBody_object_copy;
	
   			var strurl="/performance/handSel.do?b_query=link`planid="+copyBody_plan_id+"`opt="+opt;
			var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl); 
			var config = {
			      	width:650,
			      	height:500,
			      	title:'复制考核主体',
			      	dialogArguments:infos,
			      	id:'select_win',
			      	type:'1'
		    	}
			if(!window.showModalDialog)
				window.dialogArguments = infos;
	   		modalDialog.showModalDialogs(iframe_url,'template_win',config,select_ok);
  			
 
	}else	
	    Ext.showAlert(info);
		
}
/**
 *窗口返回值回调
 */
function select_ok(objList){
	if(objList==null)
		return false;	
	var right_fields='';
	if(objList.length>0)
	{
		for(var i=0;i<objList.length;i++)		   	
   			 right_fields+=",'"+objList[i]+"'";	
		Ext.showConfirm("确认把考核主体复制到指定的考核对象吗?",function(flag){
			if(flag='yes'){
				 var hashvo=new ParameterSet(); 
	 			 hashvo.setValue("opt",'15');    
	    		 hashvo.setValue("object_copy", copyBody_object_copy);
	     		 hashvo.setValue("object_past", right_fields);
	    		 hashvo.setValue("plan_id",copyBody_plan_id);
	    		 var request=new Request({method:'post',onSuccess:copytOk,functionId:'9023000003'},hashvo);
			}
		})
	}
}
function selectWinClose(){
	Ext.getCmp("select_win").close();
}
function copytOk(outparamters)
{
	copyBody_object_copy = undefined;
	copyBody_plan_id = undefined;
	var flag=outparamters.getValue("flag");
   	if(flag=="1")
		Ext.showAlert('复制操作完成！');
	else
		Ext.showAlert('复制操作失败！');
}
</script>

<body>
<html:form action="/selfservice/performance/performanceImplement"  >
<html:hidden property="flag" value="1" />

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
 <tr>
    <td align="left" nowrap height='25'>
       &nbsp; <bean:message key="lable.performance.perPlan"/>
     	    
     	<hrms:optioncollection name="performanceImpForm" property="dblist" collection="list" />
             <html:select name="performanceImpForm" property="dbpre" size="1" onchange="change();">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select>
       
    </td>         
 </tr>
</table>
<%if("hl".equals(hcmflag)){%>
	<br>
<% } %>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <%
	         	FieldItem fielditem = DataDictionary.getFieldItem("E0122");
	        %>
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
            
            <logic:equal name="performanceImpForm" property="objectType" value="1">  
            	 <td align="center" class="TableRow" nowrap>
             		<bean:message key="org.performance.unorum"/>    
             	 </td>	
            </logic:equal>  
                       
            <logic:equal name="performanceImpForm" property="objectType" value="2">   
            	<td align="center" class="TableRow" nowrap>
            		<bean:message key="b0110.label"/>
            	</td> 
	        	<td align="center" class="TableRow" nowrap>	            		         
			 		<%=fielditem.getItemdesc()%>
		    	</td>
	     
	            <td align="center" class="TableRow" nowrap>
	            	<bean:message key="e01a1.label"/>
		    	</td>
	        
	            <td align="center" class="TableRow" nowrap>
	            	<hrms:fieldtoname name="accountForm" fieldname="A0101" fielditem="fielditem"/>
		     		<bean:write name="fielditem" property="dataValue" />&nbsp;          	
		    	</td>
	      	</logic:equal>  
	      	<logic:equal name="performanceImpForm" property="objectType" value="3"> 	          
		 		<td align="center" class="TableRow" nowrap>
            	 	<bean:message key="b0110.label"/>
            	</td> 
	      	</logic:equal> 
       	  	<logic:equal name="performanceImpForm" property="objectType" value="4">   
       	  		<td align="center" class="TableRow" nowrap>
            	 	<bean:message key="b0110.label"/>
            	</td>   
		 		<td align="center" class="TableRow" nowrap>
	             	<%=fielditem.getItemdesc()%>
		    	</td>
	      	</logic:equal> 
	      	
	    	<td align="center" class="TableRow" nowrap>
				<bean:message key="lable.performance.perMainBody"/>            	
	    	</td>
	    	<logic:notEqual name="performanceImpForm" property="busitype" value="1">
		    	<td align="center" class="TableRow" nowrap>
			    	 	<logic:notEqual name="performanceImpForm" property="planMthod" value="2">	    
			    			<bean:message key="lable.performance.targetPurview"/>   
			        	</logic:notEqual>
			    		<logic:equal name="performanceImpForm" property="planMthod" value="2">   
			    			<bean:message key="performance.item.priv"/>
			    		</logic:equal>
		    	</td>
	    	</logic:notEqual>
	 		<logic:equal name="performanceImpForm" property="status" value="3">
	   			<td align="center" class="TableRow" nowrap>	  
	   				<bean:message key="reportcyclelist.option"/>
	   			</td>	
	  		</logic:equal>     
	  	 	<logic:equal name="performanceImpForm" property="status" value="5">
	   			<td align="center" class="TableRow" nowrap>	  
	   				<bean:message key="reportcyclelist.option"/>
	   			</td>	
	  		</logic:equal>    		        	        	        
           </tr>
   	  </thead>
   	  <%int x=20;
   	  	if("hcm".equals(hcmflag)){
   	  		x=14;
   	  	}
   	   %>
   	  <hrms:paginationdb id="element" name="performanceImpForm" sql_str="performanceImpForm.sql_str" order_by=" order by a0000" table="" where_str="performanceImpForm.where_str" columns="id,B0110,E0122,E01A1,A0101,object_id"  page_id="pagination" pagerows="<%=x %>" indexes="indexes">
	    <%
					          if(i%2==0)
					          {
					          %>
					          <tr class="trShallow">
					          <%}
					          else
					          {%>
					          <tr class="trDeep">
					          <%
					          }
					          i++;          
					          %>  
		    <logic:equal name="performanceImpForm" property="status" value="3">
	            <td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="performanceImpForm" property="pagination.select" value="true"   indexes="indexes"/>&nbsp;
	            	<input type="hidden" name="objectId" value="<bean:write name="element" property="object_id" filter="true"/>">
	            </td>
            </logic:equal>
            
		    <logic:equal name="performanceImpForm" property="status" value="5">
	            <td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="performanceImpForm" property="pagination.select" value="true"   indexes="indexes"/>&nbsp;
	            	<input type="hidden" name="objectId" value="<bean:write name="element" property="object_id" filter="true"/>">
	            </td>
            </logic:equal> 
            
          <logic:equal value="1" name="performanceImpForm" property="objectType">
      		<td align="left" class="RecordRow" nowrap>
          		  &nbsp;<bean:write name="element" property="a0101" filter="false"/>&nbsp;   
	     	 </td>     			
	   	  </logic:equal>
	      <logic:equal name="performanceImpForm" property="objectType" value="2">   
	      	<td align="left" class="RecordRow" nowrap>
          		<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
	     	 </td>    
	         <td align="left" class="RecordRow" nowrap>
	          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
	          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;            
		    </td>
	     	    
	            <td align="left" class="RecordRow" nowrap>
	                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
	          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;     
		    </td>
		    <td align="left" class="RecordRow" nowrap>
	                    &nbsp;  <bean:write name="element" property="a0101" filter="false"/>&nbsp;
		    </td> 
	    </logic:equal>  
		<logic:equal name="performanceImpForm" property="objectType" value="3">   
	      	<td align="left" class="RecordRow" nowrap>
          		<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
	     	 </td>        
	    </logic:equal>   
	    <logic:equal name="performanceImpForm" property="objectType" value="4">   
	      	<td align="left" class="RecordRow" nowrap>
          		<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
	     	 </td>    
	         <td align="left" class="RecordRow" nowrap>
	          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
	          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;            
		    </td>          
	    </logic:equal> 
	    
	     <td align="center" class="RecordRow" nowrap>            	
            	<a  href="/selfservice/performance/performanceMainBodyImplement.do?b_query=link&objectID=<bean:write name="element" property="object_id" filter="true"/>"  ><img src="/images/edit.gif" border=0></a>&nbsp;
	    </td>
	     <logic:notEqual name="performanceImpForm" property="busitype" value="1">
		    <td align="center" class="RecordRow" nowrap>
		    	 <logic:notEqual name="performanceImpForm" property="planMthod" value="2">	
	            	<a  href="/selfservice/performance/performancePointPrivImplement.do?b_query=link&object_id=<bean:write name="element" property="object_id" filter="true"/>"  ><img src="/images/edit.gif" border=0></a>&nbsp;
	    		 </logic:notEqual>
	             <logic:equal name="performanceImpForm" property="planMthod" value="2">	
	            	<a  href="/performance/implement/performanceImplement.do?b_itempriv=query&objectid=<bean:write name="element" property="object_id" filter="true"/>&template_id=<bean:write name="performanceImpForm" property="templateId" />&planid=<bean:write name="performanceImpForm" property="dbpre" />"  ><img src="/images/edit.gif" border=0></a>&nbsp;
	             </logic:equal>
		    </td>
	    </logic:notEqual>
	    <logic:equal name="performanceImpForm" property="status" value="3">
	        <td align="center" class="RecordRow" nowrap>
	        	<a  href="javascript:copyBody('<bean:write name="element" property="object_id" filter="true"/>','<bean:write name="performanceImpForm" property="objectType" />','<bean:write name="performanceImpForm" property="dbpre" />')"  ><bean:message key="button.copy"/><bean:message key="lable.performance.mainbody"/>到</a>&nbsp;
	         </td> 
	    </logic:equal>        
	    <logic:equal name="performanceImpForm" property="status" value="5">
	        <td align="center" class="RecordRow" nowrap>
	        	<a  href="javascript:copyBody('<bean:write name="element" property="object_id" filter="true"/>','<bean:write name="performanceImpForm" property="objectType" />','<bean:write name="performanceImpForm" property="dbpre" />')"  ><bean:message key="button.copy"/><bean:message key="lable.performance.mainbody"/>到</a>&nbsp;
	         </td> 
	    </logic:equal>    	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
        
</table>
<table  width="100%"   class='RecordRowP'   align="center">
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

<table  width="100%">
          <tr>
            <td align="center" nowrap="nowrap" height='20'>
           
             <logic:equal name="performanceImpForm" property="status" value="3">
             	 
             	  
              	  <html:button  styleClass="mybutton" property="br_handworkSelect"  onclick="handworkSelect()" >
            		     <bean:message key="lable.performance.handworkselect"/> 
	              </html:button>    
		          <logic:equal name="performanceImpForm" property="objectType" value="2">            	 
	              		 <input type="button" name="conditionSearchBt"  value="<bean:message key="kq.wizard.term"/><bean:message key="column.select"/>" class="mybutton" onclick="conditionSearch('/selfservice/performance/hquery_interface.do?b_query2=link&a_inforkind=1&plan_id=${performanceImpForm.dbpre}&flag=1&objectType=${performanceImpForm.objectType}')">                
	                	 <input type="button" name="quickRelationBt"  value="<bean:message key="lable.performance.quickRelation"/>" class="mybutton" onclick="batchSelectMainBody()"  >
	               </logic:equal>
                 <input type="button" name="delBt"  value="<bean:message key="button.delete"/>" class="mybutton" onclick="deleterec()">  
                 <input type="button" name="batchSelBodyBt"  value="<bean:message key="jx.implement.batchSelBodys"/>" class="mybutton" onclick="batchSelBodys()" >                
             </logic:equal>
             <logic:equal name="performanceImpForm" property="status" value="5">
             	
              	  <html:button  styleClass="mybutton" property="br_handworkSelect" onclick="handworkSelect()" >
            		     <bean:message key="lable.performance.handworkselect"/> 
	             </html:button>
	          	  <logic:equal name="performanceImpForm" property="objectType" value="2"> 
              		 <input type="button" name="conditionSearchBt"  value="<bean:message key="kq.wizard.term"/><bean:message key="column.select"/>" class="mybutton" onclick="conditionSearch('/selfservice/performance/hquery_interface.do?b_query2=link&a_inforkind=1&plan_id=${performanceImpForm.dbpre}&flag=1&objectType=${performanceImpForm.objectType}')">                
                	 <input type="button" name="quickRelationBt"  value="<bean:message key="lable.performance.quickRelation"/>" class="mybutton" onclick="batchSelectMainBody()">
                 </logic:equal>
                 <input type="button" name="delBt"  value="<bean:message key="button.delete"/>" class="mybutton" onclick="deleterec()">  
                 <input type="button" name="batchSelBodyBt"  value="<bean:message key="jx.implement.batchSelBodys"/>" class="mybutton" onclick="batchSelBodys()">  
             </logic:equal>
              <input type="button" name="exportExcel"  value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="exportExcel1()">    
              <input type="button" name="setBodyPriv"  value="<bean:message key="menu.performance.setdynamainbodypropotion"/>" class="mybutton" onclick="setdynamainbodypropotion('${performanceImpForm.dbpre}')">    
             
            </td>
          </tr>          
</table>





</html:form>


</body>
</html>