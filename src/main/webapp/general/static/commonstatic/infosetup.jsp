<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
.TableRow {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRow1 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRow2 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid;  
	BORDER-RIGHT: 0pt solid;  
	BORDER-TOP: 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.RecordRow1 {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}
.RecordRow2 {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: 0pt; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}
</style>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="javascript">
var titlename='';
function editinfo(id,name){
    titlename=name;//参数传递到弹框  wangb 20180804 bug 39386
	var theurl="/general/static/commonstatic/statshowsetup.do?b_setup=link&id="+id+"&name="+$URL.encode(name)+"&infokind=${statForm.infokind}&type=1";
	var return_vo;
	var dw=650,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    if(getBrowseVersion()){/*add by xiegh date20180307 bug35252  */
    	return_vo= window.showModalDialog(theurl,titlename,'dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:500px;dialogWidth:650px;center:yes;scroll:yes;help:no;resizable:no;status:no;');
		infoSetupForm.target="_self";
		infoSetupForm.action="/general/static/commonstatic/statshowsetup.do?b_query=link&inforkind=${statForm.infokind}";
		infoSetupForm.submit();//刷新父窗体显示
    }else{
    	//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
		var iTop = (window.screen.availHeight - 30 - 500) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - 650) / 2; //获得窗口的水平位置
    	window.open(theurl,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top='+iTop+',left='+iLeft+',width=650,height=500');
    }
    
	//parent.close();
	//theurl="/general/static/commonstatic/statshowsetup.do?b_query=link&inforkind=${statForm.infokind}&statid=";
    //var return_vo;
    //return_vo= window.showModalDialog(theurl,'_blank','dialogHeight:500px;dialogWidth:650px;center:yes;scroll:no;help:no;resizable:no;status:no;');
	//window.open(theurl,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=600,height=500');
}
//参数 无效 非IE浏览器  弹窗调用方法   wangb 20180127
function openReturn(vo,count){
	infoSetupForm.target="_self";
	infoSetupForm.action="/general/static/commonstatic/statshowsetup.do?b_query=link&inforkind=${statForm.infokind}";
	infoSetupForm.submit();//刷新父窗体显示
}

</script>
<hrms:themes/>
<style>
/* .fixedDiv2{
	width: expression(document.body.clientWidth-10);
	height: expression(document.body.clientHeight-80);
	border-top:0;
} */
.RecordRowP{
	width: expression(document.body.clientWidth-10);
	margin-left:-4px;
}
.ListTableF{
	border-left:0;
	border-right:0;
}
</style>
<html:form action="/general/static/commonstatic/statshowsetup">
<div>
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
   	  <thead>
           <tr>
        <td align="center" class="TableRow_rt" nowrap>
		<bean:message key="stat.info.setup.name"/>
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="stat.info.setup.archive_set"/>
	    </td>	    
        <td align="center" class="TableRow" nowrap>
		<bean:message key="stat.info.setup.archive_type"/>
	    </td>
          <td align="center" class="TableRow_left" nowrap>
		<bean:message key="stat.info.setup.edit"/>
	    </td>  	        		        	        	        
           </tr>
   	  </thead>
   	  <hrms:extenditerate id="element" name="infoSetupForm" property="infoSetupForm.list" indexes="indexes"  pagination="infoSetupForm.pagination" pageCount="${infoSetupForm.pagerows}" scope="session">
           <!-- hrms:paginationdb id="element" name="infoSetupForm" sql_str="infoSetupForm.strsql" table="" where_str="infoSetupForm.cond_str" columns="infoSetupForm.columns" order_by="infoSetupForm.order_by" pagerows="${infoSetupForm.pagerows}" page_id="pagination"-->
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
            
            <td align="left" class="RecordRow_right" nowrap>
                   <bean:write name="element" property="name" filter="true"/>
	    </td>
            <td align="left" class="RecordRow" 	style="word-break:break-all;">
                   <bean:write name="element" property="archive_set_name" filter="true"/>       	   	             	            	              	              	            	               	             	             	             	             	             	             	               
	    </td>
		
            <td align="left" class="RecordRow" 	style="word-break:break-all;">
                    <bean:write  name="element" property="archive_type_name" filter="true"/>
	    </td>
            
            <td align="center" class="RecordRow_left" nowrap>
            	<img src="/images/edit.gif" style="cursor:pointer" border=0 onclick="editinfo('<bean:write  name="element" property="id" filter="false"/>','<bean:write  name="element" property="name" filter="false"/>')">
	    </td>
           	        		        	        	        
          </tr>
           </hrms:extenditerate>
       <!-- /hrms:paginationdb> -->
	</table>
</div>
<div style="overflow:auto;margin-left:-1px!important;">
	<table width="100%" align="center" class="RecordRowP" border="0" cellspacing="0" cellpadding="0">
		<tr>
		    <td valign="middle" class="tdFontcolor">
		            <hrms:paginationtag name="infoSetupForm" pagerows="${infoSetupForm.pagerows}" property="infoSetupForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	        <td  align="right" nowrap class="tdFontcolor">
				 <p align="right"><hrms:paginationlink name="infoSetupForm" property="infoSetupForm.pagination" nameId="infoSetupForm">
				</hrms:paginationlink>
			</td>
		</tr>
	</table>
</div>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr><td height="5px"></td></tr>
<tr>
        <td align="center"  nowrap><!--update by xiegh on date 20180314 bug35373 打开港式改成window.open 需要获取父窗口来关闭  -->
            <html:button property="b_close" styleClass="mybutton" onclick="parent.close();"><bean:message key='button.close'/></html:button>
        </td>
   </tr> 
</table>
</html:form>
<script>
infoSetupForm.target="_self";
var form = document.getElementsByName('infoSetupForm')[0];
if(getBrowseVersion()){//ie浏览器下 样式问题修改   wangb 2019429 bug 46153
	var table1 = form.getElementsByTagName('table')[1];
	table1.style.width='';
}
var table0 = form.getElementsByTagName('table')[0];
table0.style.borderLeft='1px solid #C4D8EE';
table0.style.borderRight='1px solid #C4D8EE';
</script>