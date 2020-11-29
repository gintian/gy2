<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="java.util.*" %>
<%@ page import="com.hjsj.hrms.actionform.kq.kqself.plan.KqPlanInfoForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript">
function update()
   {
      
       alert("该计划已结束执行不可以修改！");
     
   }
   function del()
   {
      
       alert("该计划已报批执行不可以删除！");
     
   }  
   function makeout(id)
   {
      if (confirm('确认签写年假吗?如果填写提交则不能再修改该计划!'))
      {
         kqPlanInfoForm.action="/kq/kqself/plan/my_plan_info.do?b_makeout=link&apply_id="+id+"&param=view";
         kqPlanInfoForm.submit();
      }
   }
   function appupdate(id,start)
   {
      
      if(start!="01"&&start!="02")
      {
         if (confirm('你的计划已经审批,修改后申请将要重新审批！\n您确定修改吗？'))
         {
           kqPlanInfoForm.action="/kq/kqself/plan/my_plan_info.do?b_update=link&apply_id="+id+"&dtable=q31&param=update";
           kqPlanInfoForm.submit();
         }
      }else
      {
        kqPlanInfoForm.action="/kq/kqself/plan/my_plan_info.do?b_update=link&apply_id="+id+"&dtable=q31&param=update";
        kqPlanInfoForm.submit();
      }
      
   }
   function Approval(id,start)
   {
   		if(confirm('你确认要报批计划？')){
   			kqPlanInfoForm.action="/kq/kqself/plan/my_plan_info.do?b_approval=link&apply_id="+id+"&dtable=q31&param=approval";
   			kqPlanInfoForm.submit();
   		}
   	 
   }
 </script>  
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript">
 function change(id)
   {
   	  var year=id.value;
      kqPlanInfoForm.action="/kq/kqself/plan/my_plan_info.do?b_query=link&year2="+year;
      kqPlanInfoForm.submit();
   }
</script>
<%
int i=0;
%>
<html:form action="/kq/kqself/plan/my_plan_info">
<table  width="100%" align="center">
  <tr >
          <td align="left" nowrap valign="middle">        
           <bean:message key="kq.deration_details.kqnd"/>        
           <hrms:optioncollection name="kqPlanInfoForm" property="slist" collection="list" />
	          <html:select name="kqPlanInfoForm" property="year" size="1" onchange="change(this);">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
             </html:select> 
           </td>
         </tr>
</table>
<div class="fixedDiv2">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>  	 
    <tr>
        <logic:iterate id="element" name="kqPlanInfoForm"  property="flist" indexId="index">
          <logic:equal name="element" property="visible" value="true">
             <td align="center" class="TableRow" style="border-top:none;border-left: none;" nowrap>
                <bean:write name="element" property="itemdesc" />&nbsp;
             </td>
         </logic:equal>    
      </logic:iterate>   
      
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
        <bean:message key="label.view"/>            	
      </td>  
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
        <bean:message key="label.edit"/>     
      </td>
       <td align="center" class="TableRow" style="border-top:none;" nowrap>
        <bean:message key="button.delete"/>     
      </td>
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
        报批     
      </td>  
      <hrms:priv func_id="0B110">
      <td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap>
        签假    
      </td> 
      </hrms:priv>
    </tr>  
  </thead>  
<hrms:paginationdb id="element" name="kqPlanInfoForm" sql_str="kqPlanInfoForm.sql" table="" where_str="kqPlanInfoForm.where" columns="${kqPlanInfoForm.com}" page_id="pagination" pagerows="18" indexes="indexes">
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
            <logic:iterate id="flist" name="kqPlanInfoForm"  property="flist" indexId="index">
             <logic:equal name="flist" property="visible" value="true">
                  <logic:notEqual name="flist" property="itemtype" value="D">
                   <logic:equal name="flist" property="itemtype" value="A">
                     <td align="left" class="RecordRow" style="border-left: none;" nowrap>
                        <logic:notEqual name="flist" property="codesetid" value="0">                          
                           <hrms:codetoname codeid="${flist.codesetid}" name="element" codevalue="${flist.itemid}" codeitem="codeitem" scope="page"/>  	      
                           <bean:write name="codeitem" property="codename" />&nbsp;  
                                           
                        </logic:notEqual>
                        <logic:equal name="flist" property="codesetid" value="0">
                            <bean:write name="element" property="${flist.itemid}" filter="false"/>&nbsp;                 
                        </logic:equal>                   
                     </td>
                    </logic:equal>  
                    <logic:notEqual name="flist" property="itemtype" value="A">
                     <td align="left" class="RecordRow" nowrap>
                        <logic:notEqual name="flist" property="codesetid" value="0">  
                                                
                           <hrms:codetoname codeid="${flist.codesetid}" name="element" codevalue="${flist.itemid}" codeitem="codeitem" scope="page"/>  	      
                           <bean:write name="codeitem" property="codename" />&nbsp;  
                           <bean:write name="element" property="${flist.itemid}" filter="false"/>&nbsp;                  
                        </logic:notEqual>
                        <logic:equal name="flist" property="codesetid" value="0">
                            <bean:write name="element" property="${flist.itemid}" filter="false"/>&nbsp;                 
                        </logic:equal>                   
                     </td>
                    </logic:notEqual>  
                    </logic:notEqual>
                    <logic:equal name="flist" property="itemtype" value="D">
                       <td align="center" class="RecordRow" nowrap>
                           <bean:write name="element" property="${flist.itemid}" filter="false"/>&nbsp;   
                       </td>
                    </logic:equal>    
            </logic:equal>    
          </logic:iterate> 
             <td align="center" class="RecordRow" nowrap>              
            	<a href="/kq/kqself/plan/my_plan_info.do?b_view=link&apply_id=<bean:write name="element" property="q3101" filter="true"/>&dtable=q31&param=view"><img src="/images/view.gif" border=0></a>
	     </td>   
            <td align="center" class="RecordRow" nowrap>
              <logic:equal name="element" property="q31z5" value="06">            	
	        <a href="###" onclick="update();"><img src="/images/edit.gif" border=0></a>
	     </logic:equal>
	     <logic:notEqual name="element" property="q31z5" value="06">
            	<a href="###" onclick="appupdate('<bean:write name="element" property="q3101" filter="true"/>','<bean:write name="element" property="q31z5" filter="true"/>');"><img src="/images/edit.gif" border=0></a>
	     </logic:notEqual>
	    </td>         
	    <bean:define id="q31011" name='element' property="q3101"/>
	    <bean:define id="q31z51" name='element' property="q31z5"/>
          <%
          	//参数加密
          	String q31012 = PubFunc.encrypt(q31011.toString());
            String q31z52 = PubFunc.encrypt(q31z51.toString());
          	String str = "apply_id="+q31011+"&dtable=q31";
          %>
            <td align="center" class="RecordRow" nowrap>
             <logic:equal name="element" property="q31z5" value="01">
            	<a href="/kq/kqself/plan/my_plan_info.do?b_delete=link&encryptParam=<%=PubFunc.encrypt(str)%>" onclick="document.kqPlanInfoForm.target='_self';validate('R','','');return (document.returnValue && ifdel());"><img src="/images/del.gif" border=0></a>
	     </logic:equal>
	      <logic:notEqual name="element" property="q31z5" value="01">
            	<a href="###" onclick="del();"><img src="/images/del.gif" border=0></a>
	     </logic:notEqual>
	    </td> 
	     <td align="center" class="RecordRow" nowrap>
	     	<logic:equal name="element" property="q31z5" value="01">
	     		<a href="###" onclick="Approval('<%=q31012 %>','<%=q31z52 %>');"><img src="/images/edit.gif" border=0></a>
	     	</logic:equal>
	     	
	     </td>
	    <hrms:priv func_id="0B110">
	    <td align="center" class="RecordRow" style="border-right: none;" nowrap>
             <logic:equal name="element" property="q31z5" value="06">            	
	        <a href="###" onclick="alert('这个计划已结束不能再次签假!');"><img src="/images/edit.gif" border=0></a>
	     </logic:equal>
	      <logic:notEqual name="element" property="q31z5" value="06">  
	        <logic:notEqual name="element" property="q31z5" value="03">
            	 <a href="###" onclick="alert('必须是已批状态才能签假!');"><img src="/images/edit.gif" border=0></a>
	        </logic:notEqual>
	        <logic:equal name="element" property="q31z5" value="03">
	          <logic:equal name="element" property="q31z0" value="01">
	           <a href="###" onclick="makeout('<%=q31012 %>');"><img src="/images/edit.gif" border=0></a>
	          </logic:equal>
	          <logic:equal name="element" property="q31z0" value="02">
	             <a href="###" onclick="alert('该计划没有审批通过,请修改后重新报批!');"><img src="/images/edit.gif" border=0></a>
	          </logic:equal>
	        </logic:equal>
	      </logic:notEqual> 
	    </td> 
	    </hrms:priv>
	  </tr> 
    </hrms:paginationdb>
    </table>
  	</div>
  	<div style="*width:expression(document.body.clientWidth-10);">
	    	<table  width="100%" align="center" class="RecordRowP">
				<tr>
		    	<td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
		    	</td>
	            <td  align="right" nowrap class="tdFontcolor">
		      	<p align="right"><hrms:paginationdblink name="kqPlanInfoForm" property="pagination" nameId="kqPlanInfoForm" scope="page">
				</hrms:paginationdblink>
		    	</td>
	       		</tr>    
</table>   
</div> 
</html:form>
