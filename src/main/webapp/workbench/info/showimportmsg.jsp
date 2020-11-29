<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm,com.hrms.struts.valueobject.PaginationForm,org.apache.commons.beanutils.LazyDynaBean"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	SelfInfoForm selfInfoForm = (SelfInfoForm)session.getAttribute("selfInfoForm");
	PaginationForm msgPageForm = selfInfoForm.getMsgPageForm();
	ArrayList l=selfInfoForm.getMsgList();//all list
	//ArrayList l=msgPageForm.getList();//当前页的list
	if(l==null)
		l=new ArrayList();
	String RepeatPrimaryKey = selfInfoForm.getRepeatPrimaryKey();
	int i=0;
	int c=msgPageForm.getPagination().getCurrent();
	int size=selfInfoForm.getPagerows();
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
.TableRowm1 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: medium none;
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRowm2 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: medium none;
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: medium none;
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRowm3 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: medium none;
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
</style>
<script language="javascript">

	function importdata(){
		var obj = document.getElementById("importId");
		if(obj)
			obj.disabled = true;
		
		var x=document.body.clientWidth/2-300;
        var y=document.body.clientHeight/2-125;
        var waitInfo=eval("wait");
        waitInfo.style.top=y;
        waitInfo.style.left=x;
        waitInfo.style.display="block";
		selfInfoForm.action="/workbench/info/showinfodata1.do?b_exedata=link&way=importdate";//&isroot=1&code=&kind=2&jump=1
		selfInfoForm.submit();
	}


function returnback(){
    selfInfoForm.action="/workbench/info/showinfodata.do?b_selectfile=link";
	selfInfoForm.submit();
}

function markupdate(obj){
	var _updatestr=document.all.updatestr;
	_updatestr.value=_updatestr.value+'`'+obj.value; 
}
var arr=new Array();
function downmsg()
{
	<%
		for(int n=0;n<l.size();n++){
		LazyDynaBean ldb = (LazyDynaBean)l.get(n);
		String keyid=(String)ldb.get("keyid");
		String content=(String)ldb.get("content");
		content = content.replace("\"", "\\\"");
%>	
		var obj=new Object();
		var keyid=getEncodeStr("<%=keyid %>");
		var content=getEncodeStr("<%=content %>");
		obj.keyid=keyid;
		obj.content=content;
		arr.push(obj);
<%
	}
%>
	var hashvo=new ParameterSet();	
	hashvo.setValue("arr",arr);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'0201001093'},hashvo);
}
function showfile()
{
	var outName='${selfInfoForm.outName}';
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
	return false;
}
</script>
<style>
.TableRow_left{
	BORDER-TOP: 0pt solid;
}
</style>
<html:form action="/workbench/info/showinfodata1" method="post">
<html:hidden name="selfInfoForm" property="userbase" />
<html:hidden name="selfInfoForm" property="seconditems" />
<html:hidden name="selfInfoForm" property="selectitems" />
<html:hidden name="selfInfoForm" property="updatestr" />
<html:hidden name="selfInfoForm" property="isupdate" />
<%if(l != null && l.size() > 0){ %>
<table border="0" cellspacing="0"  align="center" cellpadding="0">
<tr><td>
<logic:equal value="1" name="selfInfoForm" property="isupdate">
<table width="598" border="0" cellpadding="0" cellspacing="0">
<tr height="20">
    <!-- td width=10 valign="top" class="tableft"></td>
    <td width=100 align=center class="tabcenter" id="topic"></td>
    <td width=10 valign="top" class="tabright"></td>
    <td valign="top" class="tabremain" width="500"></td> -->
    <td align="left" class="TableRow_lrt">&nbsp;<bean:message key='workbench.info.msg.lebal' />
    <logic:equal name="selfInfoForm" property="issameunique" value="0">
    	<font size="12px" style="font-weight:normal;font-size: 12px">(当前系统设置的唯一性指标与模版中唯一性指标不一致，请慎重操作)</font>
    </logic:equal>
    </td> 
    <%
     if("0".equals(RepeatPrimaryKey)){
    %>       
    <td width=100 align="right" class="TableRow_rt"><input name=isupdatecheckbox type=checkbox onclick="onisupdate();"/><bean:message key='workbench.info.recordupdate.lebal' />&nbsp;</td>  	      
    <%
      }
    %>
</tr> 
</table>
</logic:equal>
<logic:notEqual value="1" name="selfInfoForm" property="isupdate">
<table width="598" border="0" cellpadding="0" cellspacing="0">
<tr height="20">
    <!-- td width=10 valign="top" class="tableft"></td>
    <td width=100 align=center class="tabcenter" id="topic"></td>
    <td width=10 valign="top" class="tabright"></td>
    <td valign="top" class="tabremain" width="600"></td> 
    <td><input type=checkbox style="display: none;"/></td>	 -->  
    
    <td align="left" class="TableRow_lrt"><bean:message key='workbench.info.msg.lebal' />
    <logic:equal name="selfInfoForm" property="issameunique"  value="0">
    	<font style="font-weight: bold;font-size: 12px">(当前系统设置的唯一性指标与模版中唯一性指标不一致，请慎重操作)</font>
    </logic:equal>
    </td>    
</tr> 
</table>
</logic:notEqual>
</td></tr>
<tr><td>
<!-- 【5435】员工管理：批量导入，提示信息页面列表线颜色不一致。 jingq add 2014.12.01 -->
<div class="fixedDiv7 common_border_color"> 
<table  style='width:100%;' style='position:absolute' border=0 cellspacing=0  align=center cellpadding=0 class=ListTable style=margin-top:0>
<thead><tr class=fixedHeaderTr><td align=center class=TableRow_top width=35 nowrap><bean:message key='train.evaluationStencil.no'/></td><td align=center class=TableRow_left width="150" nowrap>${selfInfoForm.primarykeyLabel }</td><td align=center class=TableRow_left nowrap><bean:message key='workbench.info.content.lebal'/></td></tr></thead>
	<hrms:extenditerate id="element" name="selfInfoForm" property="msgPageForm.list" indexes="indexes"  pagination="msgPageForm.pagination" pageCount="${selfInfoForm.pagerows}" scope="session">
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
            <td align="center" class="RecordRow_right" nowrap>
            <%=i+(c-1)*size%>
	    </td>
            <td align="left" class="RecordRow" 	style="word-break:break-all;" width="150"  nowrap>
                   &nbsp;<bean:write name="element" property="keyid"/>   	   	             	            	              	              	            	               	             	             	             	             	             	             	               
	    </td>
        <td align="left" class="RecordRow_left" 	style="word-break:break-all;"  nowrap>
                    <bean:write name="element" property="content" filter="false"/>  
	    </td>
          </tr>
      </hrms:extenditerate>
</table>
</div>
<div class="fixedDiv8" style="width:598px;"> 
<table  width="100%" align="center" class="RecordRowP" cellpadding="0" cellspacing="0">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <hrms:paginationtag name="selfInfoForm" pagerows="${selfInfoForm.pagerows}" property="msgPageForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	        <td align="right" nowrap class="tdFontcolor">
				 <p align="right"><hrms:paginationlink name="selfInfoForm" property="msgPageForm.pagination" nameId="msgPageForm">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</div>
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
	<tr><td height="5px"></td></tr>
	<tr>
        <td align="center" nowrap>
        <%
			if("0".equals(RepeatPrimaryKey)){
		%>       
        	 <html:button property="b_abolish" styleId="importId" styleClass="mybutton" onclick="importdata();"><bean:message key='button.import'/>&nbsp;</html:button>
        <%
			}
		%>
        	 <html:button property="b_abolish" styleClass="mybutton" onclick="javascript:showfile();"><bean:message key='workbench.info.import.error.down.label'/></html:button>
        	 <html:button property="b_abolish" styleClass="mybutton" onclick="returnback();"><bean:message key='button.return'/></html:button>
        </td>
   	</tr> 
</table>
</td>
</tr>
</table>
<%} %>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
      <tr>

        <td class="td_style" height="24">正在导入,请稍候...</td>

      </tr>
      <tr>
        <td style="font-size:12px;line-height:200%" align=center>
          <marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
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
<script type="text/javascript">
	function onisupdate(){
		var isupdatecheckbox=$('isupdatecheckbox');
		var isupdate=document.all.isupdate;
		if(isupdatecheckbox.checked){
			isupdate.value='t';
			//batch_select(isupdatecheckbox,'selectflag');
		}else{
			isupdate.value='1';
			//batch_select(isupdatecheckbox,'selectflag');
		}
	}
	onisupdate();
</script>
</html:form>
<%
	if(l.size()==0){
%>
<script type="text/javascript">
window.onload=function (){
	importdata();
}
</script>
<%
}		
%>

