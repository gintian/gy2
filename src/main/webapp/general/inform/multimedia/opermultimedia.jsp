<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page
	import="com.hjsj.hrms.actionform.general.inform.multimedia.MultiMediaFileForm,java.util.*"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url = "/css/css1.css";
    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    if (userView != null) {
        css_url = userView.getCssurl();
        if (css_url == null || css_url.equals(""))
            css_url = "/css/css1.css";
    }
    MultiMediaFileForm multiMediaFileForm = (MultiMediaFileForm) session.getAttribute("multiMediaFileForm");
    ArrayList li = (ArrayList) multiMediaFileForm.getMultimedialist();
    boolean fK = true;
    if (li.size() > 0) {
        fK = false;
    }

    String code = (String) session.getAttribute("code");
    String agent=request.getHeader("User-Agent").toLowerCase();
    boolean isIE = false;
    if (agent.toLowerCase().indexOf("msie") > -1)
        isIE = true;
%>

<%
    int i = 0;
%>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<script language="javascript">
var modified = '${multiMediaFileForm.modified}';
if(getBrowseVersion()){
	if(modified=='true')
		parent.parent.window.returnValue = modified;
}
function outContent(mediaid){
	if("selfedit"=="${multiMediaFileForm.canEdit}" || "appview"=="${multiMediaFileForm.canEdit}")
		 return;
	var hashvo=new ParameterSet();
	hashvo.setValue("type","outcontent");	
	hashvo.setValue("mediaid",mediaid);	
   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'1010090026'},hashvo);
}
function outTitle(mediaid){
	if("selfedit"=="${multiMediaFileForm.canEdit}" || "appview"=="${multiMediaFileForm.canEdit}")
		 return;
	var hashvo=new ParameterSet();
	hashvo.setValue("type","outtitle");	
	hashvo.setValue("mediaid",mediaid);	
   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'1010090026'},hashvo);
}
function outClass(mediaid){
	if("selfedit"=="${multiMediaFileForm.canEdit}" || "appview"=="${multiMediaFileForm.canEdit}")
		 return;
	var hashvo=new ParameterSet();
	hashvo.setValue("type","outclass");	
	hashvo.setValue("mediaid",mediaid);	
	hashvo.setValue("dbFlag","${multiMediaFileForm.dbFlag}");	
   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'1010090026'},hashvo);
}

function viewContent(outparamters){
	var content=outparamters.getValue("content");
	if (content!=""){	
		config.FontSize='10pt';
		Tip(getDecodeStr(content),STICKY,true);
	}
}

function bdelete()
{
	var strIds="";
	var mychgs = "";
	var isCorrect=false;
    var len=document.multiMediaFileForm.elements.length;
    var i;
    var index = 0;
    var obj = document.getElementsByName("ids");
    for (i=0;i<len;i++)
    {
       if(document.multiMediaFileForm.elements[i].type=="checkbox"&&document.multiMediaFileForm.elements[i].name!='selbox')
       {
           if(document.multiMediaFileForm.elements[i].checked==true)
           {
        	     if(obj[index].getAttribute("mychg")=='true'){
        	    	 	mychgs+=obj[index].value+":" + obj[index].getAttribute("state") + ",";
        	     }else{
             	strIds = strIds + obj[index].value + ",";
             }
             isCorrect=true;
           }
           index++;
       }
   }   
   if(!isCorrect)
   {
      alert("请选择要删除的记录!");
      return false;
   }   
	if(ifdel())
	{
		document.multiMediaFileForm.action="/general/inform/multimedia/opermultimedia.do?b_delete=link&ids="+strIds+"&mychgs="+mychgs;
		document.multiMediaFileForm.submit(); 
	}
}

function relaAdd()
{
	var kind = "6";
	var userbase = "${selfInfoForm.userbase}";
	var setname = "${selfInfoForm.setname}";
	var a0100 = "${multiMediaFileForm.a0100}";
	document.multiMediaFileForm.action="/general/inform/multimedia/opermultimedia.do?b_relevance=link&setname="+setname+"&a0100="+a0100+"&userbase="+userbase+"&kind="+kind;
	document.multiMediaFileForm.submit();
}
function add()
{
	document.multiMediaFileForm.action="/general/inform/multimedia/opermultimedia.do?b_add=link&editflag=false";
	document.multiMediaFileForm.submit(); 
	
}
function edit(pk_id)
{
	document.multiMediaFileForm.action="/general/inform/multimedia/opermultimedia.do?b_add=link&editflag=true&pk_id="+pk_id;
	document.multiMediaFileForm.submit(); 
}
function init()
{
	document.getElementsByName("multimediaflag")[0].value="${multiMediaFileForm.multimediaflag}";  
}


function upItem(rowid,mediaid){
	var _table=document.getElementById("tableid");	
	var _rowid=parseInt(rowid);	
	if(_table.rows.length<2||_rowid<1){
		return;
	}
	var hashvo=new ParameterSet();  
	hashvo.setValue("rowid", _rowid);      
    hashvo.setValue("mediaid", mediaid);
    hashvo.setValue("type", 'sort_up');
    var request=new Request({method:'post',onSuccess:upItemview,functionId:'1010090026'},hashvo);
}

function upItemview(outparamters){
	multiMediaFileForm.action = "/general/inform/multimedia/opermultimedia.do?b_query=refresh";
	multiMediaFileForm.submit();
}
function downItem(rowid,mediaid){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	if(_table.rows.length<3||(_rowid+2)==_table.rows.length){
		return;
	}	
	var hashvo=new ParameterSet();          
	hashvo.setValue("rowid", _rowid);      
    hashvo.setValue("mediaid", mediaid);
    hashvo.setValue("type", 'sort_down');
    var request=new Request({method:'post',onSuccess:downItemview,functionId:'1010090026'},hashvo);
}

function downItemview(outparamters){
	multiMediaFileForm.action = "/general/inform/multimedia/opermultimedia.do?b_query=refresh";
	multiMediaFileForm.submit();
}


function download(mediaid){
	var hashvo=new ParameterSet();       
    hashvo.setValue("mediaid", mediaid);
    hashvo.setValue("type", 'download');
    var request=new Request({method:'post',onSuccess:downloadok,functionId:'1010090026'},hashvo);
}

function downloadok(outparamters){
	var filename=outparamters.getValue("filename");	
	//【8581】员工管理-信息维护-记录录入-点击附件（下载，空白页面） jingq upd 2015.04.08
	filename =getDecodeStr(filename);
	if (filename=="" ) return;
	
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fileid="+filename;
}

function open1(mediaid){
	var hashvo=new ParameterSet();       
    hashvo.setValue("mediaid", mediaid);
    hashvo.setValue("type", 'download');
    var request=new Request({method:'post',onSuccess:open_ok,functionId:'1010090026'},hashvo);
}

function open_ok(outparamters){
	var filename=outparamters.getValue("filename");
	var srcfilename=outparamters.getValue("displayfilename");
	filename =getDecodeStr(filename);//【8850】记录方式，学历子集附件，点附件弹出框中，点附件前面的标题，后台报错，前台出现空白页面，不对。  jingq udp 2015.04.17
	if (filename=="" ) return;
	
	window.location.target="_blank";
    window.location.href = "servlet/vfsservlet?fromjavafolder=false&fileid="+filename;
} 

</script>
<hrms:themes></hrms:themes>
<html:form action="/general/inform/multimedia/opermultimedia">
	<html:hidden name="multiMediaFileForm" property="a0100" />
	<html:hidden name="multiMediaFileForm" property="i9999" />
	<html:hidden name="multiMediaFileForm" property="filetitle" />
	<html:hidden name="multiMediaFileForm" property="multimediaflag" />
	<html:hidden name="multiMediaFileForm" property="isvisible" />
	<table width="100%" border="0" cellspacing="1" align="center" cellpadding="1">
		<tr>
			<td align="left" nowrap>
				(
				<logic:equal value="A" name="multiMediaFileForm" property="dbFlag">
					<bean:message key="label.title.org" />: <bean:write
						name="multiMediaFileForm" property="unit" filter="true" />&nbsp;
        			<bean:message key="label.title.dept" />: <bean:write
						name="multiMediaFileForm" property="pos" filter="true" />&nbsp;
      				<bean:message key="label.title.name" />: <bean:write
						name="multiMediaFileForm" property="a0101" filter="true" />&nbsp;
				</logic:equal>				
				)

			</td>
		</tr>
	</table>

	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable" id="tableid">
		<thead>
			<tr>
			<logic:equal value="true" name="multiMediaFileForm" property="canEdit">
				<td align="center" class="TableRow" width="5%" nowrap>
					<input type=checkbox name=selbox onclick=batch_select(this,'recordListForm.select');
						 title=<bean:message key='label.query.selectall' />
						width=15>
					&nbsp;
				</td>
			</logic:equal>
			<logic:equal value="selfedit" name="multiMediaFileForm" property="canEdit">
				<td align="center" class="TableRow" width="5%" nowrap>
					<input type=checkbox name=selbox onclick=batch_select(this,'recordListForm.select');
						 title=<bean:message key='label.query.selectall' />
						width=15>
					&nbsp;
				</td>
			</logic:equal>

				<td align="center" class="TableRow" width="21%" nowrap>
					<bean:message key="general.mediainfo.title" />
					&nbsp;
				</td>	
				<td align="center" class="TableRow" width="40%" nowrap>
					<bean:message key="label.description" />
					&nbsp;
				</td>
				<logic:equal value="selfedit" name="multiMediaFileForm" property="canEdit">
					<td align="center" class="TableRow" width="8%" nowrap>
                 		<bean:message key="column.sys.status"/>&nbsp;
             		</td>
				</logic:equal>
				<logic:equal value="appview" name="multiMediaFileForm" property="canEdit">
					<td align="center" class="TableRow" width="8%" nowrap>
                 		<bean:message key="column.sys.status"/>&nbsp;
             		</td>
				</logic:equal>
					<td align="center" class="TableRow" width="8%" nowrap>
                 		<bean:message key="conlumn.mediainfo.info_title"/>&nbsp;
             		</td>
				<logic:equal value="true" name="multiMediaFileForm" property="canEdit">
				<td align="center" class="TableRow" width="8%" nowrap>
					<bean:message key="label.edit" />
				</td>
				</logic:equal>
				
				<td align="center" class="TableRow" width="8%" nowrap>
					<bean:message key="conlumn.resource_list.down" />
				</td>
				<logic:equal value="true" name="multiMediaFileForm" property="canEdit" >
				<td align="center" class="TableRow" width="10%" nowrap>
					<bean:message key="label.zp_exam.sort" />
				</td>
				</logic:equal>
			</tr>
		</thead>
	   	       <%
	   	           int pagerows = multiMediaFileForm.getPagerows();
	   	               int currpage = multiMediaFileForm.getRecordListForm().getPagination().getCurrent();
	   	               int counts = multiMediaFileForm.getRecordListForm().getAllList().size();
	   	               int len = 0;
	   	               if (currpage == 1)
	   	                   len = pagerows * currpage > counts ? counts : pagerows;
	   	               else
	   	                   len = pagerows * currpage > counts ? counts - pagerows * (currpage - 1) : pagerows;
	   	       %>
	   	       <bean:define id="canedit" name="multiMediaFileForm" property="canEdit"/>
		<hrms:extenditerate id="element" name="multiMediaFileForm"
			property="recordListForm.list" indexes="indexes"
			pagination="recordListForm.pagination" pageCount="${multiMediaFileForm.pagerows}" scope="session">
			<bean:define id="fileid" name="element" property="path" />
			<bean:define id="mediaid" name="element" property="mediaid" />
			<%
			String encryptMediaid = PubFunc.encrypt(mediaid.toString());
			if(StringUtils.isEmpty(encryptMediaid))
				encryptMediaid = fileid.toString();
						    if (i % 2 == 0) {
						%>
			<tr class="trShallow">
				<%
				    } else {
				%>
			
			<tr class="trDeep">
				<%
				    }
				                i++;
				%>
			<logic:equal value="true" name="multiMediaFileForm" property="canEdit">	
				<td align="center" class="RecordRow"  nowrap>
					<hrms:checkmultibox name="multiMediaFileForm"
						property="recordListForm.select" value="true" indexes="indexes" />
					&nbsp;
				<input type="hidden" name="ids"
						value="<%=encryptMediaid %>">
				</td>
			</logic:equal>
			<logic:equal value="selfedit" name="multiMediaFileForm" property="canEdit">	
				<td align="center" class="RecordRow"  nowrap>
					<hrms:checkmultibox name="multiMediaFileForm"
						property="recordListForm.select" value="true" indexes="indexes" />
					&nbsp;
					
						<logic:equal value="" name="element" property="state">
							<input type="hidden" name="ids" mychg="false"
							value="<%=encryptMediaid %>">
						</logic:equal>
						<logic:notEqual value="" name="element" property="state">
							<bean:define id="state" name="element" property="state"></bean:define>
							<input type="hidden" name="ids" mychg="true" state="<%=state %>" value="<%=encryptMediaid %>">
						</logic:notEqual>
				</td>
			</logic:equal>

				<td align="left"  class="RecordRow" nowrap> 
					<bean:define id="fileName" name="element" property="filename"></bean:define>
					<bean:define id="srcfilename" name="element" property="srcfilename"></bean:define>
					<bean:define id="filePath" name="element" property="path"></bean:define>
					<%
						String encryptFileName = PubFunc.encrypt(srcfilename.toString());
						LazyDynaBean bean = (LazyDynaBean)pageContext.getAttribute("element");
						String state = (String)bean.get("state");
						String insert = (canedit.equals("selfedit") || canedit.equals("appview")) && "new".equals(state)?"true":"false";
						pageContext.setAttribute("isInsert", insert);
					%>
					&nbsp;
					<logic:equal value="true" name="isInsert">
					  	<a href="/servlet/vfsservlet?fileid=<%=filePath%>" target='_self'>
						<bean:write name="element" property="topic" filter="true" />
						</a>
					</logic:equal>
				  <logic:equal value="false" name="isInsert">
					<a href="javaScript:download('<%=encryptMediaid %>');"  target='_self'>
						<bean:write name="element" property="topic" filter="true" />
					</a>
				  </logic:equal>
					&nbsp;
				</td>
				
				<td align="left" onmouseout="tt_HideInit();" onmouseover='outContent("<%=encryptMediaid %>");' class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="description" filter="true" />
					&nbsp;
				</td>
				<logic:equal value="selfedit" name="multiMediaFileForm" property="canEdit">	
					<td align="center" class="RecordRow"  nowrap>
						<logic:equal value="delete" name="element" property="state">
							<bean:message key="button.delete"/>
						</logic:equal>
						<logic:equal value="new" name="element" property="state">
							<bean:message key="button.insert"/>
						</logic:equal>
						<logic:notEqual value="delete" name="element" property="state">
							<logic:notEqual value="new" name="element" property="state">
								<bean:message key="label.hiremanage.status3"/>
							</logic:notEqual>
						</logic:notEqual>
					</td>
				</logic:equal>
				<logic:equal value="appview" name="multiMediaFileForm" property="canEdit">	
					<td align="center" class="RecordRow"  nowrap>
						<logic:equal value="delete" name="element" property="state">
							<bean:message key="button.delete"/>
						</logic:equal>
						<logic:equal value="new" name="element" property="state">
							<bean:message key="button.insert"/>
						</logic:equal>
						<logic:notEqual value="delete" name="element" property="state">
							<logic:notEqual value="new" name="element" property="state">
								<bean:message key="label.hiremanage.status3"/>
							</logic:notEqual>
						</logic:notEqual>
					</td>
				</logic:equal>
				<td align="left" onmouseout="tt_HideInit();" onmouseover='outClass("<%=encryptMediaid %>");' class="RecordRow" nowrap>                
	               	&nbsp;<bean:write  name="element" property="class" filter="true"/>
	            </td>
				<logic:equal value="true" name="multiMediaFileForm" property="canEdit">
				<td align="center" class="RecordRow" nowrap>			
					<hrms:priv func_id="2606503,01030103">
						<a
							href="javascript:edit('<%=encryptMediaid %>')">
							<img src="/images/edit.gif" border=0>
						</a>
					</hrms:priv>
							
				</td>
				</logic:equal>
				<td align="center" valign="middle" class="RecordRow" nowrap>
				  <logic:equal value="true" name="isInsert">
				  	<a href="/servlet/vfsservlet?fileid=<%=filePath%>" target='_self'>
					<img src="/images/download.png" width="16" height="16" border=0></a>
				  </logic:equal>
				  <logic:equal value="false" name="isInsert">
				  	<a href="javaScript:download('<%=encryptMediaid %>')">
					<img src="/images/download.png" width="16" height="16" border=0></a> 
				  </logic:equal>
				</td>
				<logic:equal value="true" name="multiMediaFileForm" property="canEdit">
	           	 <td align="left" class="RecordRow"  nowrap>
                 	<%
                 	    if (i != 1) {
                 	%>
					&nbsp;<a href="javaScript:upItem('<%=(i - 1)%>','<%=encryptMediaid %>')">
					<img src="/images/up01.gif" width="12" height="17" border=0></a> 
					<%
 					    } else {
 					%>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<%
					    }
					%>
				    <%
				        if (len == i) {
				    %>
				    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    <%
				        } else {
				    %>
					&nbsp;<a href="javaScript:downItem('<%=(i - 1)%>','<%=encryptMediaid %>')">
					<img src="/images/down01.gif" width="12" height="17" border=0></a> &nbsp;
					<%
					    }
					%>
			</td>
			</logic:equal>
			</tr>
		</hrms:extenditerate>

	</table>
	<table width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		    <hrms:paginationtag name="multiMediaFileForm" pagerows="${multiMediaFileForm.pagerows}" property="recordListForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="multiMediaFileForm" property="recordListForm.pagination"
				nameId="recordListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
	
		</tr>
	</table>
	

		<table width="100%" align="left">
			<tr>
				<td height="0px"></td>
			</tr>
			<tr>
				<td align=center>
				<logic:equal value="true" name="multiMediaFileForm" property="canEdit">
						<hrms:priv func_id="2606501,01030101">
							<html:button styleClass="mybutton" property="b_add"
							onclick="add()">
							<bean:message key="button.insert" />
						</html:button>
						</hrms:priv>								
						<hrms:priv func_id="2606501,01030101">
							<html:button styleClass="mybutton" property="b_relaadd"
							onclick="relaAdd()">
							<bean:message key="conlumn.resource_list.relevance_add" />
						</html:button>
						</hrms:priv>	
					
		
						<hrms:priv func_id="2606502,01030102">
							<html:button styleClass="mybutton" property="b_delete"
								onclick="bdelete()">
								<bean:message key="menu.gz.delete" />
							</html:button>
						</hrms:priv>
				</logic:equal>
				<logic:equal value="selfedit" name="multiMediaFileForm" property="canEdit">
						<hrms:priv func_id="2606501,01030101">
							<html:button styleClass="mybutton" property="b_add"
							onclick="add()">
							<bean:message key="button.insert" />
						</html:button>
						</hrms:priv>
		
						<hrms:priv func_id="2606502,01030102">
							<html:button styleClass="mybutton" property="b_delete"
								onclick="bdelete()">
								<bean:message key="menu.gz.delete" />
							</html:button>
						</hrms:priv>
				</logic:equal>

					<logic:equal value="0" name="multiMediaFileForm" property="isvisible">
						<html:button styleClass="mybutton" property="b_return"
							onclick="winClose()">
							<bean:message key="button.close" />
						</html:button>
					</logic:equal>
		
				</td>
			</tr>
		</table>

</html:form>
<script language="javascript">
init();
  function exeReturn(returnStr,target)
{
   multiMediaFileForm.action=returnStr;
   multiMediaFileForm.target=target;
   multiMediaFileForm.submit();
}
//兼容非IE浏览器关闭方法   wangb 20180208
function winClose(){
	if(getBrowseVersion()){
		parent.parent.window.close();
	}else{//非IE浏览器关闭窗口
		if(parent.parent.returnFun) {
			parent.parent.returnFun("true");
			parent.parent.Ext.getCmp("multimediaWin").close();
		} else if(parent.parent.parent.Ext){
			try{
				if(parent.parent.parent.returnValue)
					parent.parent.parent.returnValue("true");
				
			    parent.parent.parent.winClose();
			} catch(e){
				parent.parent.window.close();
			}
		}else{
			parent.parent.window.close();
		}
		
	}
}
/* //a标签下载文件时，会先打开空白页面     wangb 20180208
var as = document.getElementsByTagName('a');
for(var i=0;i<as.length;i++){
	as[i].setAttribute('target','_self');
} */

</script>