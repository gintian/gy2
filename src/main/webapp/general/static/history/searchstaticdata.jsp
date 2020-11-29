<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.stat.StatForm" %>
<%@ page import="java.util.ArrayList" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css"; 
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	StatForm statForm=(StatForm)session.getAttribute("statForm");
	ArrayList orderlist=statForm.getOrderlist();
%>
<style type="text/css">

.m_input {
	width: 45px;
	font-size: 14px;
	text-align: right;
}
</style>
<script language="javascript">
  function change()
   {
      statForm.action="/general/static/history/searchstaticdata.do?b_search=link";
      statForm.submit();
   } 
   function query()
   {
      statForm.action="/general/static/history/searchstaticdata.do?b_query=link";
      statForm.submit();
   } 
   
   function restatic(archive_type,moreun)
   {
      var currnode=parent.frames['nil_menu'].Global.selectedItem;      
      var id = currnode.getSelected();      
      if(id!="")
      {
         
         statForm.action="/general/static/history/searchstaticdata.do?b_search=link&acode="+id;
         statForm.submit();
      }else
      {
         alert("请先选择单位部门，再进行统计！");
      }
      
   }
   this.fObj = null;
   var time_r=0; 
   function setFocusObj(obj,time_vv) 
   {		
	this.fObj = obj;
	time_r=time_vv;		
   }
  function IsInputTimeValue(id,time_vv) 
  {	     
       time_r=time_vv;	
       this.fObj=document.getElementById(id);
       event.cancelBubble = true;
       var fObj=this.fObj;		
       if (!fObj) return;		
       var cmd = event.srcElement.innerText=="5"?true:false;
       if(fObj.value==""||fObj.value.lenght<=0)
	  fObj.value="0";
       var i = parseInt(fObj.value,10);		
       var radix=parseInt(time_r,10)-1;				
       if (i==radix&&cmd) {
           i = 0;
       } else if (i==0&&!cmd) {
	   i = radix;
       } else {
	   cmd?i++:i--;
       }	
       if(i==0)
       {
	  fObj.value = "00"
       }else if(i<10&&i>0)
       {
	  fObj.value="0"+i;
       }else{
	  fObj.value = i;
       }			
       fObj.select();
  } 
  function IsDigit() 
  { 
     return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  } 
</script>

  

<hrms:themes></hrms:themes>
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<link href="/css/css1_report.css" rel="stylesheet" type="text/css">
<!-- <script language="JavaScript" src="/js/popcalendar2.js"></script>  -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<html:form action="/general/static/history/searchstaticdata"> 

<table width="100%"  cellspacing="0" border="0"  align="center" cellpadding="0"> 
   <tr>
     <td height="10">
     </td>
   </tr>
   <tr>
      <td>
         <table width="100%"  border="0" cellspacing="0"  class="DetailTable"  align="left" cellpadding="0">
          <tr>
             <td width="20">
               &nbsp;
             </td>
             <td align="left" width="185" nowrap>
                  统计项：
                  <hrms:optioncollection name="statForm" property="statlist" collection="list" />
								<html:select name="statForm" property="statid" size="1" style="width:120;" onchange="query();">
									<html:options collection="list" property="dataValue" labelProperty="dataName" />
								</html:select>
             </td>
             <td nowrap width="140" align="center"> 图表类型：
			     <hrms:optioncollection name="statForm" property="graph_list" collection="list" />
								<html:select name="statForm" property="graph_style" size="1" onchange="change();">
									<html:options collection="list" property="dataValue" labelProperty="dataName" />
								</html:select>
			 </td>
			  <logic:equal  name="statForm" property="moreun"  value="true">
                   <td nowrap width="240">
                     <table  border="0"  cellspacing="0"  cellpadding="0">
                     <tr>
                        <td>
                             &nbsp;	 时间范围：&nbsp;	
                        </td>
                        <td>
                           <hrms:optioncollection name="statForm" property="yylist" collection="list" />
								<html:select name="statForm" property="cyc_year" styleId='year' size="1">
									<html:options collection="list" property="dataValue" labelProperty="dataName" />
						   </html:select>
						   年
                        </td>
                        <logic:notEqual  name="statForm" property="archive_type"  value="4">                        
                         <td>
                           <hrms:optioncollection name="statForm" property="mmlist" collection="list" />
								<html:select name="statForm" property="cyc_moth"  styleId='month' size="1">
									<html:options collection="list" property="dataValue" labelProperty="dataName" />
						   </html:select>
                              月
                          </td>
                        </logic:notEqual>
                       </tr>                     
                     </table>
			       </td>
			  </logic:equal>
			  <logic:notEqual  name="statForm" property="moreun"  value="true">
			     <logic:equal  name="statForm" property="archive_type"  value="4">
                   <td nowrap width="240">
                     <table  border="0"  cellspacing="0"  cellpadding="0">
                     <tr>
                        <td>
                             &nbsp;	 时间范围：&nbsp;	
                        </td>
                        <td>
                        <hrms:optioncollection name="statForm" property="yylist" collection="list" />
								<html:select name="statForm" property="cyc_year" styleId='year' size="1">
									<html:options collection="list" property="dataValue" labelProperty="dataName" />
						   </html:select>
                           
                        </td>
                        <td width="25" align="center">
                          ~
                        </td>
                        <td>
                           <hrms:optioncollection name="statForm" property="yylist" collection="list" />
								<html:select name="statForm" property="cyc_year_e" size="1">
									<html:options collection="list" property="dataValue" labelProperty="dataName" />
						   </html:select>
                          </td>
                       </tr>                     
                     </table>
			       </td>
                </logic:equal>
                <logic:notEqual  name="statForm" property="archive_type"  value="4">
                  <td nowrap width="250">
						<bean:message key="label.from" />						
					    <input type="text" name="cyc_Sdate" size="10" value="${statForm.cyc_Sdate}" onclick="popUpCalendar(this,this,'','','','',false);"> 
						<bean:message key="label.to" />
						 <input type="text" name="cyc_Edate" size="10" value="${statForm.cyc_Edate}" onclick="popUpCalendar(this,this,'','','','',false);">  
						
						&nbsp;	
			       </td>
                 </logic:notEqual>
			  </logic:notEqual>
              
			<td>		
			  &nbsp;
			   <input type="button" name="br_return" value='查询统计' class="mybutton" onclick="restatic('${statForm.archive_type}','${statForm.moreun}');">
			 
			</td>
			<td>
			   &nbsp;&nbsp;
			</td>
          </tr>
         </table>
      </td>
    </tr>
   
   
   <tr>
     <td align="center" id="chart1">
            <logic:equal  name="statForm" property="graph_style"  value="1">
		     	<hrms:chart name="statForm" title="${statForm.chartTitle}" 
			     scope="session" legends="jfreelist" data="" width="${statForm.chartWidth}" height="500" 
		     	chart_type="${statForm.chartType}"  label_enabled="false"
			    isneedsum="false">
			   </hrms:chart>
		    </logic:equal>
		    <logic:equal  name="statForm" property="graph_style"  value="2">
			   <hrms:chart name="statForm" title="${statForm.chartTitle}" 
			   scope="session" legends="jfreemap" data="" orderlist="<%=orderlist%>" width="${statForm.chartWidth}" height="500" 
		     	chart_type="${statForm.chartType}"
			   isneedsum="false">
			   </hrms:chart>	
		 </logic:equal>	
     </td>
   </tr>
   <tr>
   	<td height='10px'></td>
   <tr>
   <tr>
     <td>
        <bean:write name="statForm" property="reportHtml" filter="false" />
     </td>
   </tr>
</table>
</html:form>
