<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page import="com.hrms.struts.valueobject.UserView,
				 java.util.*,
				 com.hrms.struts.constant.SystemConfig,
				com.hjsj.hrms.actionform.performance.singleGrade.SingleGradeForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
<hrms:themes />
<%

	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag=(String)userView.getHm().get("gradeFashion");   //1:下拉框方式  2：平铺方式
	 
	SingleGradeForm singleGradeForm=(SingleGradeForm)session.getAttribute("singleGradeForm");    
	String objType="1";
	ArrayList itemidList=singleGradeForm.getItemidList();
	if(itemidList.size()>1)
	{
		LazyDynaBean abean=(LazyDynaBean)itemidList.get(1);
		String _itemid=(String)abean.get("itemid");
		if(_itemid.equalsIgnoreCase("e0122"))
			objType="2";
	}
	
%>
<style>
.fixedtab 
{ 
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 	
}
.ListTable_self {
    BACKGROUND-COLOR: #FFFFFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
 }   
 .cell_locked2 {
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #f4f7f7;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22;
	font-weight: bold;	
	valign:middle;
	
	z-index: 20;
}
.t_cell_locked {
	border: inset 1px #C4D8EE;
	BACKGROUND-COLOR: #ffffff;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
	background-position : center left;
	z-index: 10;	
}
/** 修改 IEonly 解决火狐下没有边框的问题 haosl 2018-2-4*/
<!--[if IE]> 
.cell_locked2_{
	left: expression(document.getElementById("a_table_div").scrollLeft); /*IE5+ only*/
	top: expression(document.getElementById("a_table_div").scrollTop); /*IE5+ only*/ 
	position: relative;
}
<![endif]-->
/** 修改 IEonly 解决火狐下没有边框的问题 haosl 2018-2-4**/
<!--[if IE]> 
.t_cell_locked_{
 	left: expression(document.getElementById("a_table_div").scrollLeft); /*IE5+ only*/
	position: relative;
 }
<![endif]-->
</style>
<script language='javascript'>
 
function sub()
{
	<logic:notEqual name="singleGradeForm" property="operate" value="3">
		if(singleGradeForm.perCompare&&singleGradeForm.perCompare.checked==false)
		{
			singleGradeForm.perCompare.value="0";
			singleGradeForm.perCompare.checked=true;
		}
	</logic:notEqual>
	singleGradeForm.action="/selfservice/performance/singleGrade.do?b_individual=search";
	singleGradeForm.submit();
}

function returns()
{
	var operate=${singleGradeForm.operate}
	if(operate==1)
	{
		document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_query2=b_query2&operate=selfgrade";	
		document.singleGradeForm.submit();
	}
	if(operate==2)
	{
		document.singleGradeForm.action="/selfservice/performance/singleGrade.do?b_query2=b_query2";	
		document.singleGradeForm.submit();
	}
	if(operate==3)
	{
		<% if(flag!=null&&flag.equals("1") ){%>
		document.singleGradeForm.action="/selfservice/performance/batchGrade.do?b_query=link&b_Desc=query";
		document.singleGradeForm.submit();
		<% }else { %>
		window.parent.document.forms[0].action='/selfservice/performance/batchGrade.do?b_tileFrame=link&operate=aaa${singleGradeForm.dbpre}';	
		window.parent.document.forms[0].submit();
		<% } %>
	}
}


function setFashion()
{
	var aa_month=eval('a_month');
	var aa_count=eval('a_count');
	var aa_quarter=eval('a_quarter');
	var aa_halfYear=eval('a_halfYear');
	var aa_startDate=eval('a_startDate');
	var aa_endDate=eval('a_endDate')
	if(singleGradeForm.statMethod.value==1)
	{
		aa_month.style.display="none"; 
		aa_count.style.display="block";
		aa_quarter.style.display="none"; 
		aa_halfYear.style.display="none"; 
		aa_startDate.style.display="none";
		aa_endDate.style.display="none";
	}
	else if(singleGradeForm.statMethod.value==2)
	{
		aa_month.style.display="block"; 
		aa_count.style.display="block";
		aa_quarter.style.display="none"; 
		aa_halfYear.style.display="none"; 
		aa_startDate.style.display="none";
		aa_endDate.style.display="none";
	}
	else if(singleGradeForm.statMethod.value==3)
	{
		aa_month.style.display="none"; 
		aa_count.style.display="none";
		aa_quarter.style.display="block"; 
		aa_halfYear.style.display="none"; 
		aa_startDate.style.display="none";
		aa_endDate.style.display="none";
	}
	else if(singleGradeForm.statMethod.value==4)
	{
		aa_month.style.display="none"; 
		aa_count.style.display="none";
		aa_quarter.style.display="none"; 
		aa_halfYear.style.display="block"; 
		aa_startDate.style.display="none";
		aa_endDate.style.display="none";
	}
	else if(singleGradeForm.statMethod.value==9)
	{
		aa_month.style.display="none"; 
		aa_count.style.display="none";
		aa_quarter.style.display="none"; 
		aa_halfYear.style.display="none";
		aa_startDate.style.display="block";
		aa_endDate.style.display="block";
		 
	}
	<logic:notEqual name="singleGradeForm" property="operate" value="3">
		if(singleGradeForm.perCompare&&singleGradeForm.perCompare.checked==false)
		{
			singleGradeForm.perCompare.value="0";
			singleGradeForm.perCompare.checked=true;
		}
	</logic:notEqual>
//	singleGradeForm.action="/selfservice/performance/singleGrade.do?b_individual=search&operates=<%=(request.getParameter("operates"))%>&plan_id=<%=((String)request.getParameter("plan_id"))%>&object_id=<%=((String)request.getParameter("object_id"))%>&mainbody_id=<%=((String)request.getParameter("mainbody_id"))%>";
	singleGradeForm.action="/selfservice/performance/singleGrade.do?b_individual=search";
	singleGradeForm.submit();

}

</script>
<%
	String url="/selfservice/performance/singleGrade.do?b_individual=search&operates="+(request.getParameter("operates"))+"&plan_id="+((String)request.getParameter("plan_id"))+"&object_id="+((String)request.getParameter("object_id"))+"&mainbody_id="+((String)request.getParameter("mainbody_id"));
%>


<html:form action="/selfservice/performance/singleGrade">
 

<table>
<tr><td align='left'>


 <input type='hidden' name='object_id' value="${singleGradeForm.object_id}" />
 
 
 <logic:equal name="singleGradeForm" property="statCustomMode" value="True"  >
 <table><tr>
 
 
 <logic:notEqual name="singleGradeForm" property="changFlag" value="0">
 		<td>
 		<bean:message key="kq.formula.fashion"/>
		 <select name='statMethod' onchange="setFashion()">
		 	<option value='1'><bean:message key="lable.performance.accordingYearStat"/></option>
		 	<logic:equal name="singleGradeForm" property="changFlag" value="1">
			 	<option value='2'><bean:message key="lable.performance.accordingMonthStat"/></option>
			 	<option value='3'><bean:message key="lable.performance.accordingQuarterStat"/></option>
			 	<option value='4'><bean:message key="lable.performance.accordingHalfyearStat"/></option>
			 	<option value='9'><bean:message key="lable.performance.accordingTimeStat"/></option>
		 	</logic:equal>
		 </select>
		 &nbsp;&nbsp;
 		</td>
 
 
 		<td>
 		<div id="a_year" style="display:block;">
       <hrms:optioncollection name="singleGradeForm" property="years" collection="list" />
             <html:select name="singleGradeForm" property="year" size="1" onchange="sub()">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select>&nbsp;<bean:message key="datestyle.year"/>
        </div>
        </td>
        <td>
        <div id="a_month" style="display:none;">
       
	        <hrms:optioncollection name="singleGradeForm" property="months" collection="list" />
	             <html:select name="singleGradeForm" property="month" size="1" onchange="sub()">
	             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	        </html:select>&nbsp;<bean:message key="datestyle.month"/>
       
        </div>
        </td>
        <td>
        <div id="a_count" style="display:block;">
        <hrms:optioncollection name="singleGradeForm" property="counts" collection="list" />
             <html:select name="singleGradeForm" property="count" size="1" onchange="sub()">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select>&nbsp;<bean:message key="hmuster.label.count"/>
        </div>
        </td>
       <td>
        <div id="a_quarter" style="display:none;">
        <hrms:optioncollection name="singleGradeForm" property="quarters" collection="list2" />
             <html:select name="singleGradeForm" property="quarter" size="1" onchange="sub()">
             <html:options collection="list2" property="dataValue" labelProperty="dataName"/>
        </html:select>
        </div>
        </td>
        <td>
        <div id="a_halfYear" style="display:none;">
        <hrms:optioncollection name="singleGradeForm" property="halfYears" collection="list3" />
             <html:select name="singleGradeForm" property="halfYear" size="1" onchange="sub()">
             <html:options collection="list3" property="dataValue" labelProperty="dataName"/>
        </html:select>
        </div>
        </td>
        
        
        <td>
        <div id="a_startDate" style="display:none;"><bean:message key="kq.strut.start"/>
        	<Input type='text' value="${singleGradeForm.statStartDate}"  name='statStartDate' size='10' onclick='popUpCalendar(this,this, dateFormat,"","",true,false)' readOnly />
        </div>
        </td>
         <td>
        <div id="a_endDate" style="display:none;">&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="kq.strut.end"/>
	        <Input type='text' value="${singleGradeForm.statEndDate}" name='statEndDate' size='10' onclick='popUpCalendar(this,this, dateFormat,"","",true,false)' readOnly />
            <html:button  styleClass="mybutton" property="b_next" onclick="sub()">
            		<bean:message key="button.query"/>
	 			</html:button>  
      		
        </div>
        </td>
        
        
        
        
 </logic:notEqual>  
 
 		<TD>    
        <logic:notEqual name="singleGradeForm" property="operate" value="3">
         <logic:equal name="singleGradeForm" property="perSetShowMode" value="1">
        &nbsp; &nbsp;<html:checkbox name="singleGradeForm"   property="perCompare"  value="1" onclick="sub()" /><bean:message key="lable.performance.compare"/>
		 </logic:equal>
		</logic:notEqual>
		</TD>
		
		
	</tr></table>	
		
</logic:equal>		
		
		
</td></tr>
</table>
 
 

<script language='javascript' >
		    var theHeight=document.body.clientHeight-110; 
			document.write("<div class=\"fixedtab common_border_color\" id=\"a_table_div\" style='position:absolute;left:5;height:"+theHeight+";width:99%'  >");
 </script> 
 		
<table  border="0" cellspacing="0"   style="margin-top:-1"  align="left" cellpadding="0" class="ListTable">
  <thead>  
	<tr>  
	    <% int n=0; %>
   		<logic:iterate id="element" name="singleGradeForm" property="reportTitles"  offset="0"> 
   		
   		    <%
   		      String class_str="TableRow common_border_color";
               if((objType.equals("2")&&(n==0||n==1||n==2))||(objType.equals("1")&&n==0))
                 class_str="cell_locked2 cell_locked2_ common_background_color common_border_color";
              n++;  		    
   		     %>
      		<td align="center"  class="<%=class_str%>" nowrap>
      		&nbsp;&nbsp;&nbsp;<bean:write name="element" filter="false"/>&nbsp;&nbsp;&nbsp;
      		</td>
        </logic:iterate>      	  
   </tr>    
  </thead> 
  <hrms:extenditerate id="element" name="singleGradeForm" property="performanceListform.list" indexes="indexes"  pagination="performanceListform.pagination" pageCount="20" scope="session">
        
          <tr >
          			<% int a=0; String name=""; %>
          	 		<logic:iterate id="a_info"    name="singleGradeForm"   property="itemidList">
							
							<% 
							 LazyDynaBean _bean=(LazyDynaBean)pageContext.getAttribute("a_info");
							 String itemtype=(String)_bean.get("itemtype");
							 String _algin="left";
							 if(itemtype!=null&&itemtype.equalsIgnoreCase("N"))
							 	_algin="right";
							 String _str="";	
							 String class_str="RecordRow"; 
							 if((objType.equals("2")&&(a==0||a==1||a==2))||(objType.equals("1")&&a==0))
							 {
							 	_str="nowrap";
							    class_str="t_cell_locked t_cell_locked_ common_border_color";	
							  }
							 name="a"+a; %>
							 <td align="<%=_algin%>" <%=_str%>  class="<%=class_str%>" >
	            		 		&nbsp;<bean:write  name="element" property="<%=name%>" filter="true"/>&nbsp;
	            		     </td>
	            		     <% a++; %>  
					</logic:iterate>
			 
	            	 
	          
          </tr>
   </hrms:extenditerate> 
 </table>

</div>

 <script language='javascript' >		
 <logic:equal name="singleGradeForm" property="statCustomMode" value="True"  >
 	<logic:equal name="singleGradeForm" property="changFlag" value="0"> 	
	 	if('${singleGradeForm.operate}'!="3" && '${singleGradeForm.perSetShowMode}'=="1")
	 	{
	 		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-75)+";width:99%'  >");
	 	}else
	 	{
	 		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-95)+";width:99%'  >");
	 	}		
	</logic:equal>
 	<logic:notEqual name="singleGradeForm" property="changFlag" value="0">
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-74)+";width:99%'  >");
	</logic:notEqual>
 </logic:equal>
 <logic:notEqual name="singleGradeForm" property="statCustomMode" value="True"  >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-100)+";width:99%'  >");
 </logic:notEqual>
 		
	</script>
	<table    class='RecordRowP'  align='center' width="100%" >
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="singleGradeForm" property="performanceListform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="singleGradeForm" property="performanceListform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="singleGradeForm" property="performanceListform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	       <td  align="right" nowrap class="tdFontcolor">
	               
		          <p align="right">
		          <hrms:paginationlink name="singleGradeForm" property="performanceListform.pagination"
				nameId="performanceListform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
   </table>
   </div>



<script language='javascript' >
	<logic:equal name="singleGradeForm" property="statCustomMode" value="True"  >
	 	<logic:equal name="singleGradeForm" property="changFlag" value="0">
	 		if('${singleGradeForm.operate}'!="3" && '${singleGradeForm.perSetShowMode}'=="1")
		 	{
		 		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-40)+";width:99%'  >");
		 	}else
		 	{
		 		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-60)+";width:99%'  >");
		 	}			
		</logic:equal>
	 	<logic:notEqual name="singleGradeForm" property="changFlag" value="0">
			document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-40)+";width:99%'  >");
		</logic:notEqual>		
	</logic:equal>
 	<logic:notEqual name="singleGradeForm" property="statCustomMode" value="True"  >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-35)+";width:99%'  >");
 	</logic:notEqual>	
</script>
<table>
<%
String clientname = SystemConfig.getPropertyValue("clientName");
if("gs".equalsIgnoreCase(clientname)){ ///如果是国税局
%>
<html:button styleClass="mybutton" property="br_home" onclick="javascript:window.close();">
关闭
</html:button>
<%
}else{
%>	
<logic:equal name="singleGradeForm" property="operate" value="1"  >
<tr><td align='left' >
<html:button styleClass="mybutton" property="br_home" onclick="returns()">
            		<bean:message key="button.return"/>
</html:button>
</td></tr>
</logic:equal>

<logic:equal name="singleGradeForm" property="operate" value="2"  >
<tr><td align='left' >
<html:button styleClass="mybutton" property="br_home" onclick="returns()">
            		<bean:message key="button.return"/>
</html:button>
</td></tr>
</logic:equal>
<logic:equal name="singleGradeForm" property="operate" value="3"  >
<tr><td align='left' >
<html:button styleClass="mybutton" property="br_home" onclick="returns()">
            		<bean:message key="button.return"/>
</html:button>
</td></tr>
</logic:equal>
<%
}
%>

</table>
</div>



<script language='javascript'>
	
 <logic:equal name="singleGradeForm" property="statCustomMode" value="True"  >
	init();
 </logic:equal>
	function init()
	{
		var a_statMethod="${singleGradeForm.statMethod}";
		var a_changFlag="${singleGradeForm.changFlag}";
		
		if(a_changFlag!='0')
		{
			var aa_year=eval('a_year');
			var aa_month=eval('a_month');
			var aa_count=eval('a_count');
			var aa_quarter=eval('a_quarter');
			var aa_halfYear=eval('a_halfYear');
			var aa_startDate=eval('a_startDate');
			var aa_endDate=eval('a_endDate')
			if(a_changFlag=='2'||a_statMethod=='1')
			{
				aa_year.style.display="block";
				aa_month.style.display="none"; 
				aa_count.style.display="block";
				aa_quarter.style.display="none"; 
				aa_halfYear.style.display="none"; 
				aa_startDate.style.display="none";
				aa_endDate.style.display="none";
			}
			else if(a_statMethod=='2')
			{
				aa_year.style.display="block";
				aa_month.style.display="block"; 
				aa_count.style.display="block";
				aa_quarter.style.display="none"; 
				aa_halfYear.style.display="none"; 
				aa_startDate.style.display="none";
				aa_endDate.style.display="none";
			}
			else if(a_statMethod=='3')
			{
				aa_year.style.display="block";
				aa_month.style.display="none"; 
				aa_count.style.display="none";
				aa_quarter.style.display="block"; 
				aa_halfYear.style.display="none"; 
				aa_startDate.style.display="none";
				aa_endDate.style.display="none";
			}
			else if(a_statMethod=='4')
			{
				aa_year.style.display="block";
				aa_month.style.display="none"; 
				aa_count.style.display="none";
				aa_quarter.style.display="none"; 
				aa_halfYear.style.display="block"; 
				aa_startDate.style.display="none";
				aa_endDate.style.display="none";
			}			
			else if(a_statMethod=='9')
			{
				aa_year.style.display="none";
				aa_month.style.display="none"; 
				aa_count.style.display="none";
				aa_quarter.style.display="none"; 
				aa_halfYear.style.display="none";
				aa_startDate.style.display="block";
				aa_endDate.style.display="block";
				 
			}
			for(var i=0;i<singleGradeForm.statMethod.options.length;i++)
			{
				//alert(singleGradeForm.statMethod.options[i].value+"  "+a_statMethod)
				if(singleGradeForm.statMethod.options[i].value==a_statMethod)
				{
					singleGradeForm.statMethod.options[i].selected=true;
					break;
				}	
			}
			
			
		}
	}

</script>



</html:form>