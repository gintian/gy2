<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.hire.demandPlan.EngagePlanForm,com.hrms.hjsj.sys.FieldItem" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%
	String operate=request.getParameter("operate");
    String z0101=PubFunc.decrypt(request.getParameter("z0101"));
 %>


<html>
<head>
<title></title>
</head>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<script language="JavaScript" src="../../js/constant.js"></script>
<script language='javascript'>
	function add()
	{
		var num=0;
		var ids="";
		for(var i=0;i<engagePlanForm.elements.length;i++)
		{
			if(engagePlanForm.elements[i].type=='checkbox'&&engagePlanForm.elements[i].checked==true)
				num++;
				ids+="/"+engagePlanForm.elements[i].value;
		}
		if(num==0)
		{
			alert(PLEASE_SELECT_HIRE_POSITION+"！");
			return;
		}
		
		engagePlanForm.action="/hire/demandPlan/engagePlan.do?b_appointPlan=point&z0101=<%=PubFunc.encrypt(z0101)%>";
		engagePlanForm.submit();
	}
function openPosition(z0301,posState)
{
   var src="/hire/demandPlan/positionDemand/positionDemandTree.do?b_browse=edit`entertype=4`codeset=<%=request.getParameter("codeset")%>`code=<%=(request.getParameter("code"))%>`operate=browse`from=employPosition`posState="+posState+"`z0301="+z0301+"`isClose=0";
   var iframe_url="/general/query/common/iframe_query.jsp?src="+src;
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:"+(window.screen.width)+"px; dialogHeight:"+(window.screen.height-200)+"px;resizable:no;center:yes;scroll:yes;status:no");			
	//window.open("/hire/demandPlan/positionDemand/positionDemandTree.do?b_browse=edit&entertype=1&codeset=<%=request.getParameter("codeset")%>&code=<%=(request.getParameter("code"))%>&operate=browse&from=employPosition&posState="+posState+"&z0301="+z0301,"_blank","width="+(window.screen.width-40)+",left=15,height="+(window.screen.height-180)+",scrollbars=yes, resizable=yes");
   if(values)
   {
      var obj= new Object();
      obj.refresh=values.refresh;
      if(obj.refresh=='1')
      {
        engagePlanForm.action="/hire/demandPlan/engagePlan.do?b_queryPosition=query&z0101=<%=z0101%>";
		engagePlanForm.submit();
      }
   }
} 
function checkPosition(z0301,posState,z0311)
{
   var hashVo=new ParameterSet();
   hashVo.setValue("z0301",z0301);
   hashVo.setValue("z0311",z0311);
   hashVo.setValue("type","6");
   hashVo.setValue("posState",posState);
   var In_parameters="opt=1";
   var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:check_ok,functionId:'1010021116'},hashVo);			
		
}
function check_ok(outparameters)
{
   var msg=outparameters.getValue("msg");
   if(msg=='0')
   {
       alert("该岗位在组织机构中已被删除！");
       return; 
   }
   else
   {
     var z0301=outparameters.getValue("z0301");
     var posState=outparameters.getValue("posState");
     openPosition(z0301,posState);
   }
}  
   function allSelect(obj)
   {
        var arr=document.getElementsByName('selectIDs');
        if(arr)
        {
             for(var i=0;i<arr.length;i++)
             {
               if(obj.checked)
                   arr[i].checked=true;
                else
                   arr[i].checked=false;
             }
        }
   }
</script>
<hrms:themes></hrms:themes>
<body>
    <%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    if(bosflag!=null&&!bosflag.equalsIgnoreCase("hcm")){
    %>
    <br>
    <%
    }
    %>
	<html:form action="/hire/demandPlan/engagePlan">
	<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
           <%  if(operate==null){ %>
            <td align="center" class="TableRow" nowrap>
              <input type="checkbox" name="selectAll" onclick="allSelect(this);"/>
             </td>
           <% } %>
            <td align="center" class="TableRow" nowrap>
              <bean:message key="label.serialnumber"/>&nbsp;<!-- 序号 -->
             </td>
             </td>
             <logic:iterate id="element" name="engagePlanForm" property="tableHeadNameList"  offset="0"> 
	            <td align="center" class="TableRow" nowrap>
    	        	&nbsp;&nbsp;<bean:write name="element" filter="false"/>&nbsp;&nbsp;
        	    </td>           
             </logic:iterate>        	        
         </tr>
   	  </thead>

	 <% int i=0; String className="trShallow"; %>	
	  <logic:iterate id="element" name="engagePlanForm" property="dateList"  offset="0"> 
			  <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
			 <tr class='<%=className%>' >  	
			 	<%  if(operate==null){ %>
			 
	  				<td align="center" class="RecordRow" nowrap>
	  					<logic:equal name="element" property="z0101" value='<%=z0101%>'   >
             	  	    	<input type="checkbox" name="selectIDs" value="<bean:write  name="element" property="z0301" filter="true"/>" checked> 
           		    	</logic:equal>
           		    	<logic:notEqual name="element" property="z0101" value="<%=z0101%>"   >
           		    		<input type="checkbox" name="selectIDs" value="<bean:write  name="element" property="z0301" filter="true"/>"> 
           		    	</logic:notEqual>
           		    </td>
           		    <%
           		   	 }%>
           		   	 <td align="center" class="RecordRow" nowrap>
	           		    	<%=i%>
	           		    </td>
           		   	 <%
           		   	 EngagePlanForm engagePlanForm=(EngagePlanForm)session.getAttribute("engagePlanForm");
           		   	 ArrayList fieldList=engagePlanForm.getFieldList();
           		   	 for(int a=0;a<fieldList.size();a++)
           		   	 {
           		   	 	FieldItem item=(FieldItem)fieldList.get(a);
           		   	 	String itemid=item.getItemid();
           		   	 	if(a==0)
           		   	 	{
           		    %>
           		    <td align="center" class="RecordRow" nowrap>
	           		    	<a href="javascript:checkPosition('<bean:write name="element" property="z0301"/>','<bean:write name="element" property="z0319a"/>','<bean:write name="element" property="z0311a"/>');"><img src="/images/view.gif" border="0"></a>
	           		    </td>
	           		     <td align="center" class="RecordRow" nowrap>&nbsp;
	           		    	<bean:write  name="element" property="<%=itemid%>" filter="true"/>
	           		    </td>
           		    
           		    <%}else { %>
	           		    <td align="center" class="RecordRow" nowrap>&nbsp;
	           		    	<bean:write  name="element" property="<%=itemid%>" filter="true"/>
	           		    </td>
           		    
           		    <%
           		    }
           		      }
           		    %>
           		    
	  
	  		 </tr>
	  </logic:iterate>
	  </table>
	  
	  
	<table  width="70%" align="left">
	          <tr>
	            <td align="left"> 
	            <%  if(operate==null){ %>
	            	<hrms:priv func_id="310135">
	              <input type="button" name="b_save" value=" <bean:message key="button.save"/> " class="mybutton" onClick="add()">
	            	</hrms:priv>
	            <% } %>
	              <input type="button" name="b_return" value=" <bean:message key="button.return"/> " onclick="history.back();" class="mybutton" > 
	            </td>
	          </tr>          
	</table>
	  


	</html:form>

</body>
</html>