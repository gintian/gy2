<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
%>
<script language="javascript">
        /**查询指标*/
	function searchFieldList()
	{
	   var tablename=$F('ps_set');
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'18010000011'});
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(posCodeParameterForm.ps_workfixed,fieldlist);
	}
	function refresh(){
		posCodeParameterForm.submit();
	}
	function changeResh(){
		document.getElementById("savebtn").disabled=true;//liuy 2015-4-13 8716：岗位参数设置，连接点击确定按钮，界面容易出错
		posCodeParameterForm.action="/pos/posparameter/ps_parameter.do?b_save=link&flag=${posCodeParameterForm.flag}";
   		posCodeParameterForm.submit(); 
	}
	
	function changejob(v,id){
	   var in_paramters="codesetid="+v;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showJobFieldList,functionId:'18010000021'});
		function showJobFieldList(outparamters){
			var fieldlist=outparamters.getValue("fieldlist");
			AjaxBind.bind(eval("posCodeParameterForm."+id),fieldlist);
		}
	}
</script>
<html:form action="/pos/posparameter/ps_parameter">

<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="pos.posparameter.ps_parameter"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <logic:notEqual value="unit" name="posCodeParameterForm" property="flag">
    <tr>
    <td align="center">
     <fieldset align="center" style="width:90%;padding-top: 10px;">
         <legend ><bean:message key="pos.posparameter.ps_code"/></legend>
          <table align="center"  width="100%"> 
	         <tr>
	            <td align="right" nowrap width="30%">
	     	       <bean:message key="pos.posparameter.selectps_code"/>
	     	       </td>
	     	       <td align="left" nowrap>
	    	           <hrms:importgeneraldata showColumn="codesetdesc" valueColumn="codesetid" flag="true" paraValue="" 
	                      sql="select codesetid,codesetdesc from codeset where codesetid<>'@K' and codesetid<>'UN' and codesetid<>'UM' order by codesetid" collection="list" scope="page"/>
	                   <html:select name="posCodeParameterForm" onchange="changejob(this.value,'ps_job')" property="ps_code" size="1">
	                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
	                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	                </html:select>
		    	</td>                  	        	        
	        </tr> 
			<tr>
	            <td align="right" nowrap>
	     	       <bean:message key="pos.posparameter.selectps_level_code"/>
	     	       </td>
	     	       <td align="left" nowrap>
	     	       		<hrms:importgeneraldata showColumn="codesetdesc" valueColumn="codesetid" flag="true" paraValue="" 
	                      sql="select codesetid,codesetdesc from codeset where codesetid<>'@K' and codesetid<>'UN' and codesetid<>'UM' order by codesetid" collection="list2" scope="page"/>
	                   <html:select name="posCodeParameterForm" onchange="" property="ps_level_code" size="1">
	                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
	                   <html:options collection="list2" property="dataValue" labelProperty="dataName"/>
	                </html:select>
		    	</td>                  	        	        
	        </tr>  
	        <tr>
              <td align="right"  nowrap>
     	       <bean:message key="pos.posparameter.ps_posset"/>
	    </td>  
	    <td align="left"  nowrap>
    	           <hrms:importgeneraldata showColumn="itemdesc" valueColumn="itemid" flag="true" paraValue="" 
                      sql="posCodeParameterForm.sqlstr" collection="list1" scope="page"/>
                   <html:select name="posCodeParameterForm" property="ps_job" size="1">
                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                   <html:options collection="list1" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                       	        	        
           </tr>         
        </table>
    </fieldset>  
    </td>
   </tr> 
   <tr>
    <td align="center">
     <fieldset align="center" style="width:90%;padding-top: 10px;">
         <legend ><bean:message key="pos.posparameter.ps_c_code"/></legend>
          <table align="center"  width="100%" border=0> 
	         <tr>
	            <td align="right" nowrap width="30%">
	     	       <bean:message key="pos.posparameter.selectps_c_code"/>
	     	       </td>
	     	       <td align="left" nowrap>
	                   <html:select name="posCodeParameterForm" onchange="changejob(this.value,'ps_c_job')" property="ps_c_code" size="1">
	                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
	                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	                </html:select>
	                <logic:equal value="N" name="posCodeParameterForm" property="ps_c_codeflag">
	                     <script>
	                          //document.getElementById("ps_c_code").disabled='true';
	                          if(document.getElementById("ps_c_code") != null)
	                          	document.getElementById("ps_c_code").style.backgroundColor="#E4E4E4";
	                     </script>
	                     (修改此选项将清空基准岗位数据，请谨慎操作)
	                </logic:equal>
		    	</td>                  	        	        
	        </tr> 
	        <tr>
                <td align="right"  nowrap>
     	           <bean:message key="pos.posparameter.ps_c_posset"/>
			    </td>  
			    <td align="left"  nowrap>
		    	           <hrms:importgeneraldata showColumn="itemdesc" valueColumn="itemid" flag="true" paraValue="" 
		                      sql="posCodeParameterForm.sqlstrc" collection="list" scope="page"/>
		                   <html:select name="posCodeParameterForm" property="ps_c_job" size="1" >
		                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
		                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		                </html:select>
			    </td>                       	        	        
           </tr> 
			<tr>
	            <td align="right" nowrap>
	     	       <bean:message key="pos.posparameter.selectps_c_level_code"/>
	     	       </td>
	     	       <td align="left" nowrap>
	                   <html:select name="posCodeParameterForm" onchange="" property="ps_c_level_code" size="1">
	                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
	                   <html:options collection="list2" property="dataValue" labelProperty="dataName"/>
	                </html:select>
		    	</td>                  	        	        
	        </tr>
	          
        </table>
    </fieldset>  
    </td>
   </tr> 
  <tr>
    <td align="center">
     <fieldset align="center" style="width:90%;padding-top: 10px;">
         <legend ><bean:message key="pos.posparameter.ps_reportrelation"/></legend>
          <table align="center" width="100%"> 
           <tr>
              <td align="right"  nowrap width="30%">
     	       <bean:message key="pos.posparameter.ps_directness"/>    	          
	    </td>   
	     <td align="left"  nowrap>
     	            <hrms:importgeneraldata showColumn="itemdesc" valueColumn="itemid" flag="true" paraValue="" 
                      sql="select itemid,itemdesc from fielditem where useflag='1' and  fieldsetid='K01' and codesetid='@K'" collection="list" scope="page"/>
                   <html:select name="posCodeParameterForm" property="ps_superior" size="1">
                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                     	        	        
           </tr>  
                    
       </table>
    </fieldset>  
    </td>
   </tr>
  
   <tr>
    <td align="center">
     <fieldset align="center" style="width:90%;padding-top: 10px;">
         <legend ><bean:message key="pos.posparameter.unit_pos_code_field"/></legend>
          <table align="center" width="100%"> 
	         <tr>
	            <td align="right" nowrap width="30%">
	     	       <bean:message key="pos.posparameter.selectunit_code_field"/>
	     	       </td>
	     	       <td align="left" nowrap>
	     	       		<html:hidden name="posCodeParameterForm" property="oldUnits" value="${posCodeParameterForm.unit_code_field}"/>
	     	       		<hrms:optioncollection name="posCodeParameterForm" property="unit_code_fieldlist" collection="unit_list"/>
	                   <html:select name="posCodeParameterForm" onchange="" property="unit_code_field" size="1">
	                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
	                   <html:options collection="unit_list" property="dataValue" labelProperty="dataName"/>
	                </html:select>
	                <bean:message key="pos.posparameter.selectunit_code_field.note"/>
		    	</td>                  	        	        
	        </tr> 
			<tr>
	            <td align="right" nowrap>
	     	       <bean:message key="pos.posparameter.selectpos_code_field"/>
	     	       </td>
	     	       <td align="left" nowrap>
	     	       <html:hidden name="posCodeParameterForm" property="oldPosts" value="${posCodeParameterForm.pos_code_field}"/>
	     	       <hrms:optioncollection name="posCodeParameterForm" property="pos_code_fieldlist" collection="pos_list"/>
	                   <html:select name="posCodeParameterForm" onchange="" property="pos_code_field" size="1">
	                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
	                   <html:options collection="pos_list" property="dataValue" labelProperty="dataName"/>
	                </html:select>
	                <bean:message key="pos.posparameter.selectpos_code_field.note"/>
	               
		    	</td>                  	        	        
	        </tr>
	        <tr style="padding-top:5px;"><td align="center" colspan="2">说明:重新指定单位及岗位代码指标时,指定的指标值会被清空,重新赋值!</td></tr> 
        </table>
    </fieldset>  
    </td>
   </tr>  	   

   </logic:notEqual>
   <!--  yuxiaochun add unit set-->
   <logic:notEqual value="pos" name="posCodeParameterForm" property="flag">
    <tr>
    <td align="center">
     <fieldset align="center" style="width:90%;padding-top:10px;">
         <legend ><bean:message key="unit.posparameter.unitworkout"/></legend>
          <table align="center" > 
           <tr>
              <td align="right"  nowrap>
     	       <bean:message key="unit.posparameter.unitfixedset"/>    	          
	    </td>   
	     <td align="left"  nowrap>
     	            <hrms:importgeneraldata showColumn="fieldsetdesc" valueColumn="fieldsetid" flag="true" paraValue="" 
                      sql="select fieldsetid,fieldsetdesc from fieldset where fieldsetid like 'B%'" collection="blist" scope="page"/>
                   <html:select name="posCodeParameterForm" property="unit_set" size="1" onchange="posCodeParameterForm.submit();">
                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                   <html:options collection="blist" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                     	        	        
           </tr>  
            <tr>
              <td align="right"  nowrap>
     	       <bean:message key="unit.posparameter.unitfixed"/>
	    </td>  
	    <td align="left"  nowrap>
    	          <hrms:importgeneraldata showColumn="itemdesc" valueColumn="itemid" flag="true" paraValue="" 
                      sql="select itemid,itemdesc from fielditem where useflag='1' and itemtype='N' and fieldsetid='${posCodeParameterForm.unit_set}'" collection="list" scope="page"/>
                   <html:select name="posCodeParameterForm" property="plan_num" size="1">
                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                       	        	        
           </tr> 
            <tr>
              <td align="right"  nowrap>
     	       <bean:message key="unit.posparameter.unitexist"/>
	    </td>  
	    <td align="left"  nowrap>
    	           <hrms:importgeneraldata showColumn="itemdesc" valueColumn="itemid" flag="true" paraValue="" 
                      sql="select itemid,itemdesc from fielditem where useflag='1' and itemtype='N' and  fieldsetid='${posCodeParameterForm.unit_set}'" collection="list" scope="page"/>
                   <html:select name="posCodeParameterForm" property="true_num" size="1">
                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                       	        	        
           </tr>  
       <tr>
              <td align="right"  nowrap>
     	       <bean:message key="pos.posparameter.whetherneedcontrolunit"/>
	    </td>  
	    <td align="left"  nowrap>
    	           <html:select name="posCodeParameterForm" property="UNITValid" size="1">
                       <html:option value="false"><bean:message key="kq.emp.change.no"/></html:option>
                       <html:option value="true"><bean:message key="kq.emp.change.yes"/></html:option>
                </html:select>
	    </td>                       	        	        
           </tr>   
                     
       </table>
    </fieldset>  
    </td>
   </tr>
   </logic:notEqual>
   <tr height="5"><td>&nbsp;</td></tr>
</table>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" style="padding-top:5px;">
<tr>
        <td align="center"  nowrap>
           &nbsp;&nbsp;<input type="button" id="savebtn" name="b_save" class="mybutton" onclick="changeResh();" value="&nbsp;<bean:message key='button.ok'/>&nbsp;">
        </td>
   </tr> 
</table>

</html:form>
