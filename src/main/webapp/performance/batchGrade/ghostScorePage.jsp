<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.constant.SystemConfig,
				 com.hjsj.hrms.actionform.performance.batchGrade.BatchGradeForm" %>

<html>  
  <head>
   <%
   		BatchGradeForm batchGradeForm = (BatchGradeForm)session.getAttribute("batchGradeForm");
    	ArrayList object_idList = batchGradeForm.getObject_idList();
   
    %> 
   
  </head>
  
<style>
.keyMatterDiv 
{ 
	overflow:auto; 
	height:200;
	width:100%;	
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
}
.MyListTable 
{
	border:0px solid #C4D8EE;
	border-collapse:collapse; 
	BORDER-BOTTOM: medium none; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    margin-top:-1px;
    margin-left:-1px;
    margin-right:-1px;
}
.TableRowScore {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	height:30px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}   
</style>
<script language='javascript' >
function enter()
{ 	
  	///var hashvo = new ParameterSet();		
    var tabids = "";
    var objectNames = document.getElementsByName("objectName");
    for(var i=0;i<objectNames.length;i++)
    {
    	if(objectNames[i].checked)
    		tabids+="/"+objectNames[i].value;
    }
    if(tabids.length<=0)
    {
    	alert("请选择考核对象！");
    	return;
    }else
    	tabids = tabids.substring(1);   
    var objvalue=document.getElementsByName("grade_id")[0].value;
    if(objvalue==null || objvalue=="null")
    {
    	alert("请选择评分标度！");
    	return;
    }
    var thevo = new Object();
	thevo.flag = "true";
	thevo.scoreObject = tabids;
	thevo.grade_id = objvalue;
	//window.returnValue = thevo;
	//改为open弹窗获取父页面方式 window.opener 获取方法 和关闭
	window.opener.openFlag(thevo);
	window.parent.close();
    
    
//    hashvo.setValue("objectName",tabids);	
//    var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfo,functionId:'90100150008'},hashvo);
} 
// 全选
function allSet()
{
	var a_value=true;
  	if(!document.batchGradeForm.all_init.checked)
  		a_value=false;
  	var temps=document.getElementsByName("objectName");
  	for(var i=0;i<temps.length;i++)
  		temps[i].checked=a_value;
  		
}
  
  
function returnInfo(outparamters)
{
  	alert(P_I_INF16+"!")
}
    	/** 禁用鼠标滚轮 **/
	function stop_onmousewheel(){
		for(var i=0;i<document.batchGradeForm.getElementsByTagName('select').length;i++){
			document.batchGradeForm.getElementsByTagName('select')[i].onmousewheel = function (){
			return false;}
		}
	}
</script>
<hrms:themes></hrms:themes> 
<body  onload="stop_onmousewheel();">
   <html:form action="/selfservice/performance/batchGrade"> 
     
   <table width="770px" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
   	  <thead>
           <tr>
            <td align="left" class="TableRowScore common_background_color common_border_color" nowrap>
		     &nbsp;<input type="checkbox" name="all_init" value="1" onclick="allSet();" id="status"> <bean:message key="jx.datacol.khobj" />	
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
          <td width="100%" align="center" nowrap>
          <div class="keyMatterDiv common_border_color">
            <table align="center" width="100%">
             <tr>
              <td>
                                
                 <table border="0" cellspacing="0"  align="center" cellpadding="2" width="100%">
                 
                 <% for(int i=0;i<object_idList.size();i++)
                 	{ 
                 		if(i==0)
                 			out.print("<tr>");
                 		if(i!=0&&i%4==0)
                 			out.print("</tr><tr>");
                 		LazyDynaBean abean=(LazyDynaBean)object_idList.get(i);
                 		String object_id = (String)abean.get("object_id");
                 		String status = (String)abean.get("status");
                 		String a0101 = (String)abean.get("a0101");
                 		if(status.equalsIgnoreCase("4"))
                 		    continue;
                 		
                 		if(status!=null && status.trim().length()>0 && status.equalsIgnoreCase("1")) // 正评价
                 		{
                 			if(SystemConfig.getPropertyValue("clientName")!=null && SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("gjkhxt")) 
							{
                 %>                                
			                   	<td align="left" nowrap valign="left">        
			                    	<input type="checkbox" name="objectName" value="<%=object_id%>" > <font color='red'> <%=a0101%></font>        
			                   	</td>
                  <%	
                  			}else
                  			{
                  %>
                  				<td align="left" nowrap valign="left">        
			                    	<input type="checkbox" name="objectName" value="<%=object_id%>" > <strong><font size='2'> <%=a0101%>(已评)</font></strong>        
			                   	</td>                  			
                  <%				
                  			}
                  		}else
                  		{
                  %>
                  			<td align="left" nowrap valign="left">        
		                    	<input type="checkbox" name="objectName" value="<%=object_id%>" > <%=a0101%>        
		                   	</td>                  
                  <%	}
                  	} %>
                  </tr>                   
                </table>       
	           
              </td>
             </tr>
            
            </table> 
            </div>
          </td>
        </tr>
        <tr>
          <td style="height:35px">
            评分标度
        	<html:select name="batchGradeForm" property="grade_id" size="1"  >
			  	  <html:optionsCollection property="gradeList" value="dataValue" label="dataName"/>
			</html:select> 
		 </td>
        </tr>
 </table>	
 
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<tr>                   
    	<td align="center" style="height:35px">                      
	    	<input type="button" name="b_save" value="<bean:message key="button.save" />"  onclick="enter();" class="mybutton">
	        <input type="button" name="b_cancel" value="<bean:message key="button.cancel" />"  onclick="window.parent.close();" class="mybutton">  
        </td>
	</tr>
</table>  
   <script type="text/javascript">
   var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
   var isOpera = userAgent.indexOf("Opera") > -1; //判断是否Opera浏览器 
   var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera; //判断是否IE浏览
   	if(!isIE){//非IE浏览器  弹窗样式修改  wangb 20171206
   		var batchGradeForm = document.getElementsByName('batchGradeForm')[0];
   		var tempTd = batchGradeForm.parentNode;
   		tempTd.style.overflow = '';
   	}
   </script>
   </html:form>
  </body>
</html>
