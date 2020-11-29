<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<html:form action="/kq/machine/search_card_data">  
<div  class="fixedDiv2" style="height: 100%;border: none">
   <table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="RecordRow"  style="border-style:solid;border-width:1px;">
		<thead>
			<tr>
				<td align="center" class="TableRow" colspan="2" nowrap style="border-top-width:0px;border-left-width:0px;border-right-width:0px;">
					&nbsp;刷卡信息&nbsp;
				</td>
			</tr>
		</thead>	
		<tr>
		      <td width="20%" height="30" align="right" class="" nowrap >
		       姓名&nbsp;
		      </td>
		      <td  class="" nowrap >&nbsp;
		           <bean:write name="kqCardDataForm" property="view_vo.string(a0101)" filter="true"/>
		      </td>  
	        </tr>	
		<tr>
		      <td width="20%" height="30" align="right" class="" nowrap >
		        刷卡日期&nbsp;
		      </td>
		      <td  class="" nowrap >&nbsp;
		           <bean:write name="kqCardDataForm" property="view_vo.string(work_date)" filter="true"/>
		      </td>  
	        </tr>
	        <tr>
		      <td height="30" align="right" class="" nowrap >
		        刷卡时间&nbsp;
		      </td>
		      <td  class="" nowrap >&nbsp;
		        <bean:write name="kqCardDataForm" property="view_vo.string(work_time)" filter="true"/>
		      </td>  
	        </tr>
	        <tr>
		      <td  height="30" align="right" class="" nowrap >
		        出入类型&nbsp;
		      </td>
		      <td  class="" nowrap >&nbsp;
		         <logic:equal name="kqCardDataForm" property="view_vo.string(inout_flag)" value="-1">
		         出
		         </logic:equal>
		          <logic:equal name="kqCardDataForm" property="view_vo.string(inout_flag)" value="0">
		         不限
		         </logic:equal>
		          <logic:equal name="kqCardDataForm" property="view_vo.string(inout_flag)" value="1">
		         进
		         </logic:equal>
	        </tr>
	      <logic:notEqual name="kqCardDataForm" property="view_vo.string(oper_time)" value="">
	        <tr>
		      <td  height="30" align="right" class="" nowrap >
		        补刷操作员&nbsp;
		      </td>
		      <td  class="" nowrap >&nbsp;
		          <bean:write name="kqCardDataForm" property="view_vo.string(oper_user)" filter="true"/>
		      </td>  
	        </tr>
	        <tr>
		      <td  height="30" align="right" class="" nowrap >
		        补刷时间&nbsp;
		      </td>
		      <td  class="" nowrap >&nbsp;
		          <bean:write name="kqCardDataForm" property="view_vo.string(oper_time)" filter="true"/>
		      </td>  
	        </tr>
	        <tr>
		      <td  height="30" align="right" class="" nowrap >
		        补刷原因&nbsp;
		      </td>
		      <td  class="" nowrap >&nbsp;
		          <bean:write name="kqCardDataForm" property="view_vo.string(oper_cause)" filter="true"/>
		      </td>  
	        </tr>
	      </logic:notEqual>
	      <logic:notEqual name="kqCardDataForm" property="view_vo.string(sp_time)" value="">
	        <tr>
		      <td  height="30" align="right" class="" nowrap >
		        审批操作员&nbsp;
		      </td>
		      <td  class="" nowrap >&nbsp;
		          <bean:write name="kqCardDataForm" property="view_vo.string(sp_user)" filter="true"/>
		      </td>  
	        </tr>
	        <tr>
		      <td  height="30" align="right" class="" nowrap >
		        审批时间&nbsp;
		      </td>
		      <td  class="" nowrap >&nbsp;
		          <bean:write name="kqCardDataForm" property="view_vo.string(sp_time)" filter="true"/>
		      </td>  
	        </tr>
	        <tr>
		      <td  height="30" align="right" class="" nowrap >
		        审批结果&nbsp;
		      </td>
		      <td  class="" nowrap >&nbsp;
		          <hrms:codetoname codeid="23" name="kqCardDataForm" codevalue="view_vo.string(sp_flag)" codeitem="codeitem"/>  	      
                   <bean:write name="codeitem" property="codename" />		         
		      </td>  
	        </tr>
	      </logic:notEqual> 
		
	</table>
<table  width="95%" align="center">
          <tr>
            <td align="center" style="height:35px;">
		      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick="window.close();">&nbsp;
            </td>
          </tr>          
</table>      
</div>      	
</html:form>
<script type="text/javascript">

</script>


