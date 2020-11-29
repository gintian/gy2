<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>
<%@page import="java.util.List"%>

<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			int status=userView.getStatus();
			String manager=userView.getManagePrivCodeValue();
			int fflag=1;
			String webserver=SystemConfig.getPropertyValue("webserver");
			if(webserver.equalsIgnoreCase("websphere"))
				fflag=2;
				
				String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+(80==(request.getServerPort())?"":(":"+request.getServerPort()))+path+"/";
				
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>

<script language="javascript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=<%=fflag%>;
	
	
	function tuisong(){
		var a=0;
		var b=0;
		var selectid=new Array();
		var a_IDs=eval("document.forms[0].selectid");	
		var nums=0;		
		for(var i=0;i<document.forms[0].elements.length;i++)
		{			
		   if(document.forms[0].elements[i].type=='checkbox'&&document.courseForm.elements[i].name!="selbox")
		   {		   			
			nums++;
		   }
	        }
		if(nums>1)
		{
		    for(var i=0;i<document.forms[0].elements.length;i++)
		    {			
			if(document.forms[0].elements[i].type=='checkbox'&&document.courseForm.elements[i].name!="selbox")
			{	
			   if(document.forms[0].elements[i].checked==true)
			   {
				   selectid[a++]=a_IDs[b].value;						
			   }
			   b++;
			}
		    }
		}
		if(nums==1)
		{
		   for(var i=0;i<document.forms[0].elements.length;i++)
		   {			
		      if(document.forms[0].elements[i].type=='checkbox'&&document.courseForm.elements[i].name!="selbox")
		      {	
			  if(document.forms[0].elements[i].checked==true)
			  {
				  selectid[a++]=a_IDs.value;						
			  }
		      }
		   }
		}
				
		if(selectid.length==0)
		{
			alert(REPORT_INFO9+"!");
			return ;
		}
		//alert(selectid);
		document.getElementById("wait").style.display="";
		for(var i=0;i<document.forms[0].elements.length;i++)
		{			
		   if(document.forms[0].elements[i].type=='button')
		   {		   			
			document.forms[0].elements[i].disabled=true;
		   }
	    }
		
		var hashvo=new ParameterSet();
        hashvo.setValue("selectids", selectid);
        hashvo.setValue("state", "${courseForm.state}");
        hashvo.setValue("codesetid", "${courseForm.codesetid}");
        hashvo.setValue("basePath","<%=basePath %>")
        var request=new Request({method:'post',asynchronous:true,onSuccess:tuisongok,functionId:'2020030082'},hashvo);
		function tuisongok(outparamters){
			document.getElementById("wait").style.display="none";
			for(var i=0;i<document.forms[0].elements.length;i++)
			{			
			   if(document.forms[0].elements[i].type=='button')
			   {		   			
				document.forms[0].elements[i].disabled=false;
			   }
		    }
			var flag=outparamters.getValue("flag"); 
			if("ok"==flag){
				alert("推送成功!");
			} 
		}
	}
	function outContent(column,rid){
		var hashvo=new ParameterSet();
		hashvo.setValue("table","r50");	
		hashvo.setValue("column",column);
		hashvo.setValue("keys","r5000");
		hashvo.setValue("values",rid);
	   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'2020061000'},hashvo);
	}
	function viewContent(outparamters){
		var content=outparamters.getValue("content");
		config.FontSize='10pt';//hint提示信息中的字体大小
		Tip(getDecodeStr(content),STICKY,true);
	}
</script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%int i=0;%>
<hrms:themes />
<html:form action="/train/resource/course/pos">
<table border="0" cellpadding="0" cellspacing="0">
   <tr>
    <td style="padding-top: 10px;">
     <div class="fixedDiv2"> 
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="border-collapse: collapse;" id="tableid">
           <tr class="fixedHeaderTr">
             <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap width="20">
              <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>
             </td>
             <logic:iterate id="info"    name="courseForm"  property="itemlist">   
              <logic:equal name="info" property="visible" value="true">
              <td align="center" class="TableRow" style="border-top: none;" nowrap>
                   &nbsp;<bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;              
              </td>
              </logic:equal>
             </logic:iterate>
             <td align="center" class="TableRow" style="border-right: none;border-top: none;" nowrap  width="40">
                              操作
             </td>	    	    	    		        	        	        
           </tr>

          <hrms:paginationdb id="element" name="courseForm" sql_str="courseForm.strsql" table="" where_str="courseForm.strwhere" columns="courseForm.columns" order_by="courseForm.order_by" page_id="pagination" pagerows="${courseForm.pagerows}" indexes="indexes">
          <%
          
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
          <%}
          else
          {%>
          <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
          <%
          }
          i++;          
          %>  
          <bean:define name="element" property="job_id" id="job_id"/>
          <bean:define name="element" property="r5000" id="r5000"/>
          <%String jobid = SafeCode.encode(PubFunc.encrypt(job_id.toString()));
          String lessonId = SafeCode.encode(PubFunc.encrypt(r5000.toString()));%>
            <td align="center" class="RecordRow" style="border-left: none;" nowrap>
               <hrms:checkmultibox name="courseForm" property="pagination.select" value="true" indexes="indexes"/>
            	<input type=hidden name=selectid value="<%=jobid %>`<%=lessonId %>" />
            </td>  
           
	         <logic:iterate id="info"    name="courseForm"  property="itemlist">  
	         <logic:equal name="info" property="visible" value="true">	
                  <logic:notEqual  name="info" property="itemtype" value="N">       
                  	<logic:notEqual  name="info" property="itemtype" value="M">               
                    	<td align="left" class="RecordRow" nowrap>
                    </logic:notEqual>   
	                <logic:equal  name="info" property="itemtype" value="M">               
	                    <td align="left" class="RecordRow" onmouseout="UnTip();" onmouseover='outContent("${info.itemid}","<%=lessonId %>");' style="width: 35%;" nowrap>        
	                </logic:equal>   
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow" style="width: 8%;" nowrap>        
                  </logic:equal> 
                  <logic:equal  name="info" property="codesetid" value="0">   
                  <!--  &nbsp; <bean:write  name="element" property="${info.itemid}" filter="true"/>&nbsp; -->
                  <bean:define id="names" name="element" property="${info.itemid}"></bean:define>
                    &nbsp; <%=names.toString() %>&nbsp;
                  </logic:equal>
                 <logic:notEqual  name="info" property="codesetid" value="0">  
                 <logic:equal name="info" property="codesetid" value="UM">
                     <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel=""/>  	      
          	           &nbsp;  <bean:write name="codeitem" property="codename" />&nbsp; 
                   </logic:equal>
                   <logic:notEqual name="info" property="codesetid" value="UM">
                        <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	    &nbsp; <bean:write name="codeitem" property="codename" />&nbsp;  
                   </logic:notEqual>                 
                     
          	     </logic:notEqual>  
              </td>
              </logic:equal>
             </logic:iterate>  
             <td align="center" class="RecordRow" style="border-right: none;" nowrap  width="40">
                 <a href="javascript:learn('<%=lessonId %>')" onclick=""> 
          	   		<img src="/images/view.gif" alt="浏览" border="0">
            	 </a> 
             </td>    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
</div>
</td></tr>
<tr><td>
<table width="100%"  align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            	<hrms:paginationtag name="courseForm"
								pagerows="${courseForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="courseForm" property="pagination" nameId="courseForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</td></tr>
<tr style="height: 35px"><td align="left" style="padding-top: 5px;">
<logic:equal value="1" name="courseForm" property="state">
	<hrms:priv func_id="32306A01" module_id="">
			<input type="button" name="b_retrun" value="关联" class="mybutton" onclick="relcourse();" />
   	</hrms:priv>
   	<hrms:priv func_id="32306A02" module_id="">
   			<input type="button" name="b_retrun" value="撤销" class="mybutton" onclick="javascrip:repcourse();" />
   	</hrms:priv>
   	<hrms:priv func_id="32306A03" module_id="">
   			<input type="button" name="b_retrun" value="推送" class="mybutton" onclick="javascrip:tuisong();" />
	</hrms:priv>
</logic:equal>
<logic:equal value="2" name="courseForm" property="state">
	<hrms:priv func_id="32306B01" module_id="">
			<input type="button" name="b_retrun" value="关联" class="mybutton" onclick="relcourse();" />
   	</hrms:priv>
   	<hrms:priv func_id="32306B02" module_id="">
   			<input type="button" name="b_retrun" value="撤销" class="mybutton" onclick="javascrip:repcourse();" />
   	</hrms:priv>
   	<hrms:priv func_id="32306B03" module_id="">
   			<input type="button" name="b_retrun" value="推送" class="mybutton" onclick="javascrip:tuisong();" />
	</hrms:priv>
</logic:equal>
			
       </td>
     </tr>     
</table>
</html:form>
<div id='wait' style='position:absolute;top:180;left:300;display: none;'>
<table border="1" width="430" cellspacing="0" cellpadding="4" class="table_style" height="150" align="center">
           <tr>

             <td class="td_style" height="40">正在推送课件...</td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%;BORDER: #C4D8EE 1pt solid;" class="common_border_color" align=center>
               <marquee class="marquee_style" direction="right" width="430" scrollamount="7" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
</table>
</div>
<script>
function relcourse(){
	var a_code = "${courseForm.a_code}";
	if(a_code==null||a_code.length<3){
		if("${courseForm.state}"=="1")
			alert("请选择岗位！");
		else
			alert("请选择职务！");
		return;	
	}
	courseForm.action = "/train/resource/course/posrel.do?b_search=link&itemize=&itemizevalue=&coursename=&courseintro=";
	courseForm.submit();
}
function repcourse(){
	var nums=0;		
	for(var i=0;i<document.forms[0].elements.length;i++)
	{			
	   if(document.forms[0].elements[i].type=='checkbox'&&document.courseForm.elements[i].name!="selbox")
	   {		   
	   		if(document.forms[0].elements[i].checked==true)
		    {	
				nums++;
		    }		
	   }
    }
    if(nums<=0)
    	alert("请选择要撤销的课程！");
    else if(confirm("确认要撤销吗？")){
		courseForm.action = "/train/resource/course/pos.do?b_del=link";
		courseForm.submit();
	}
}
//学习
function learn(courseid) {
var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=sss`lesson=" + courseid;
var fram = "/train/resource/mylessons/learniframe.jsp?src="+$URL.encode(url);
//window.showModalDialog(fram, "", "dialogWidth:880px; dialogHeight:700px;resizable:no;center:yes;scroll:yes;status:yes");
window.open(fram,'','fullscreen=yes,left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
}
</script>