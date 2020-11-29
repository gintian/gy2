<%@ page contentType="text/html; charset=UTF-8"%>
<div align="right"> 
<%@taglib uri="/tags/struts-bean" prefix="bean"%> 
<%@taglib uri="/tags/struts-html" prefix="html"%> 
<%@taglib uri="/tags/struts-logic" prefix="logic"%> 
<%@taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%> 
<%@page import="com.hrms.struts.valueobject.UserView"%> 
<%@page import="com.hrms.struts.constant.WebConstant"%> 
<%@page import="com.hrms.hjsj.sys.FieldItem"%>
<%@page import="com.hrms.struts.constant.SystemConfig,java.util.List"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	//String manager=userView.getManagePrivCodeValue();
	String manager=userView.getUnitIdByBusi("4");
	int i=0;
%>
<%// 在标题栏显示当前用户和日期 2004-5-10 
		
%> 
 
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"> 
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
<script type="text/javascript" src="/js/wz_tooltip.js"></script> 
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script> 
<script language="JavaScript" src="/module/utils/js/template.js"></script>
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
function change()
{
      personHistoryForm.action="/workbench/browse/history/showinfodata.do?b_search=link";
      personHistoryForm.submit();
}
function query(query)
{
   personHistoryForm.action="/workbench/browse/history/showinfodata.do?b_search=link&query="+query;
   personHistoryForm.submit();
} 
function resetQuery()
{
    var vo=document.getElementById("query");
    var inps=vo.getElementsByTagName("input") ;
    for(i=0;i<inps.length;i++)
    {
      if(inps[i].type=="hidden"||inps[i].type=="text")
        inps[i].value="";
      else if(inps[i].type=="checkbox")      
         inps[i].checked=false;
      
    }   
    var sels=document.getElementsByTagName("select") ;
    for(i=0;i<sels.length;i++)
    {
     sels[i].options[0].selected=true ;
    }
     var o_obj=document.getElementById('factor');
     o_obj.value="";
     o_obj=document.getElementById('expr');          
     o_obj.value="";
     o_obj=document.getElementById('strQuery');
     o_obj.value="";
     query("");
}
 function changesort()
{
   personHistoryForm.action="/workbench/browse/showinfodata.do?b_search=link&code=${personHistoryForm.code}&kind=${personHistoryForm.kind}";
   personHistoryForm.submit();
}
function executeOutFile(){
	var dh="380px";
	var dw="600px";
	if(navigator.appVersion.indexOf('MSIE 6') != -1){
		dh="410px";
	}
	var strUrl="/workbench/browse/history/showinfodata.do?b_file=link";
	//19/3/14 xus 浏览器兼容 导出excel按钮
	var config = {id:'histroy_exportExcel',width:dw,height:dh,title:"选择指标"};
	modalDialog.showModalDialogs(strUrl,'',config,executeOutFile_callbackfunc);
}
//19/3/14 xus 浏览器兼容 导出excel按钮
function executeOutFile_callbackfunc(returnvalues){
	if(returnvalues&&returnvalues!=null){
		var hashvo=new ParameterSet();
		hashvo.setValue("outfilefields",getEncodeStr(returnvalues));
		hashvo.setValue("cond_str","${personHistoryForm.cond_str} order by a0000");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'0201001208'},hashvo);
	}
}

function showFieldList(outparamters){
	var outName=outparamters.getValue("outName");
	window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"xls");
}
function openwin(url)
{
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-100)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}
function selectQ()
   {
       var code="${personHistoryForm.code}";
       var kind="${personHistoryForm.kind}";
       var tablename="${personHistoryForm.userbase}";
       var a_code="UN";
       if(kind=="2")
       {
          a_code="UN"+code;
       }else if(kind=="1")
       {
          a_code="UM"+code;
       }else if(kind=="1")
       {
          a_code="@K"+code;
       }else
       {
          a_code="UN"+code;
       }
         
       //var thecodeurl="/workbench/browse/history/showinfodata.do?b_grade=link&type=1&a_code="+a_code+"&tablename="+tablename;
       var thecodeurl="/workbench/browse/history/showinfodata.do?b_grade=link&tablename="+tablename;
       var dw=700,dh=380,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
       //19/3/23 xus 浏览器兼容 历史时点-查询-高级 谷歌不弹窗
       var config = {id:'selectQ_showModalDialogs',width:dw,height:dh};
   	   modalDialog.showModalDialogs(thecodeurl,'',config,selectQ_callbackfunc);
       //19/3/23 xus 浏览器兼容 历史时点-查询-高级 谷歌不弹窗
       /* 
       var  return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:700px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no"); 
      if(return_vo!=null){
            o_obj=document.getElementById('strQuery');
            o_obj.value=return_vo;
            personHistoryForm.action="/workbench/browse/history/showinfodata.do?b_search=link";
            personHistoryForm.submit();
      } 
        */            
   }
	//19/3/23 xus 浏览器兼容 历史时点-查询-高级 谷歌不弹窗 回调函数
   function selectQ_callbackfunc(return_vo){
	   if(return_vo!=null){
           o_obj=document.getElementById('strQuery');
           o_obj.value=return_vo;
           personHistoryForm.action="/workbench/browse/history/showinfodata.do?b_search=link";
           personHistoryForm.submit();
       } 
   }
	//19/3/23 xus 浏览器兼容 历史时点-查询-高级 谷歌不弹窗 关闭窗口
   function closeExtWin(){
		if(Ext.getCmp('selectQ_showModalDialogs'))
			Ext.getCmp('selectQ_showModalDialogs').close();
	}
   function clearQ()
   {
       personHistoryForm.action="/workbench/browse/showinfodata.do?b_search=link&code=${personHistoryForm.code}&kind=${personHistoryForm.kind}&check=no&query=0";
       personHistoryForm.submit();
   }
   function viewPhoto()
   {
       personHistoryForm.action="/workbench/browse/showinfodata.do?b_view_photo=link&code=${personHistoryForm.code}&kind=${personHistoryForm.kind}";
       personHistoryForm.target="nil_body";
       personHistoryForm.submit();
   }  
   function turn()
{
   parent.menupnl.toggleCollapse(false);
} 
   function winhrefOT(a0100,target)
{
   //if(a0100=="")
      //return false;
   var uniqueitem='<bean:write name="personHistoryForm" property="uniqueitem" />';
   //alert(uniqueitem);
   if(uniqueitem=='a0100'){
   	   if(a0100=="")
      		return false;
	   var returnvalue="188";     
	   var strUrl="/workbench/browse/showselfinfo.do?b_search=link&userbase=${personHistoryForm.userbase}&a0100="+a0100+"&flag=notself&returnvalue="+returnvalue;
	   turn();
	   window.location.href=strUrl;
   }else{
  		if(a0100==""){
  			alert("唯一性指标值为空人员不予查看信息!");
  			return false;
  		}
   		var hashvo = new ParameterSet();
		hashvo.setValue("uniqueitem",uniqueitem);
		hashvo.setValue("a0100",a0100);
		hashvo.setValue("target",target);
		var request=new Request({method:"post",asynchronous:false,onSuccess:winlocation,functionId:"0201001191"},hashvo);
   }
}
function winlocation(outparamters){
	var newa0100=outparamters.getValue("a0100");
		var nbase=outparamters.getValue("nbase");
		var msg=outparamters.getValue("msg");
		if("ok"==msg){
			var returnvalue="188";
		turn();
	   var strUrl="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&a0100="+newa0100+"&flag=notself&returnvalue="+returnvalue;
	   window.location.href=strUrl;
   }else if(msg!='error'){
   		alert(msg);
   }
}
function showOrClose()
{
		var obj=eval("aa");
	    var obj3=eval("vieworhidd");
		//var obj2=eval("document.personHistoryForm.isShowCondition");
	    if(obj.style.display=='none')
	    {
    		obj.style.display=''
        	obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 查询隐藏 </a>";
    	}
    	else
	    {
	    	obj.style.display='none';
	    	obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 查询显示 </a>";
	    	
    	}
}
function fieldCheckBox(hiddenname,id,obj)
{
   if(obj.checked==true)
   {
      var vo=document.getElementById(hiddenname);
      var iv=obj.value;
      var value=vo.value;
      value="`"+value+"`";
      if(value.indexOf("`"+iv+"`")==-1)
      {
         vo.value=vo.value+"`"+iv;
      }
   }else
   {
      var vo=document.getElementById(hiddenname);
      var voID=document.getElementsByName(id);      
      var len=voID.length;    
      var value="";
      for (i=0;i<len;i++)
      {
         if(voID[i].checked)
          {
             
            value=value+"`"+voID[i].value;
          }
       }
       vo.value=value;
   }
}
function selectCheckBox(obj,hiddname)
{
   if(obj.checked==true)
   {
      var vo=document.getElementById(hiddname);
      //var Info=eval("info_cue1");	
	  // Info.style.display="block";
      if(vo)
         vo.value="1";
   }else
   {
         var vo=document.getElementById(hiddname);
        // var Info=eval("info_cue1");	
	   //Info.style.display="none";
      if(vo)
         vo.value="0";
   }

}/**
function MusterInitData()
{
	   var vo=document.getElementsByName("querlike2");
	   var obj=vo[0];
	   if(obj.checked==true)
	   {
          
          var Info=eval("info_cue1");	
	      Info.style.display="block";
          
       }else
       {
         
         var Info=eval("info_cue1");	
	     Info.style.display="none";
         
   }
}*/
function returnTOWizard()
  {
     personHistoryForm.action="/templates/attestation/police/wizard.do?br_postwizard=link";
     personHistoryForm.target="il_body";
     personHistoryForm.submit();
  }
  function static(){
		personHistoryForm.target="il_body";
		personHistoryForm.action="/general/static/commonstatic/history/statshow.do?b_ini=link&infokind=1&backdate=${personHistoryForm.backdate }&userbase=${personHistoryForm.userbase }&uniqueitem=${personHistoryForm.uniqueitem }";
		personHistoryForm.submit();
	}
</script> 
<style type="text/css">
    .RecordRow_top {
	border: inset 1px #94B6E6;	
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

	}	
 </style>
</div><% i=0;%>
<hrms:themes />
<html:form action="/workbench/browse/history/showinfodata">
<html:hidden name="personHistoryForm" property="factor" styleId="factor" styleClass="text"/>
<html:hidden name="personHistoryForm" property="expr" styleId="expr" styleClass="text"/>  
<html:hidden name="personHistoryForm" property="strQuery" styleId="strQuery" styleClass="text"/> 
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-left:5px;margin-top: 5px;">
<tr>
    <td align="left"  nowrap>
      <table  border="0" cellspacing="0"  cellpadding="0">
     <tr>
    <td align="left"  nowrap>      
	       <logic:notEmpty  name="personHistoryForm" property="code">
	          <bean:message key="system.browse.info.currentorg"/>:
	          <hrms:codetoname codeid="UN" name="personHistoryForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="${personHistoryForm.uplevel}"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  <hrms:codetoname codeid="UM" name="personHistoryForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="${personHistoryForm.uplevel}"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  <hrms:codetoname codeid="@K" name="personHistoryForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="${personHistoryForm.uplevel}"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  &nbsp;&nbsp;
	       </logic:notEmpty>
	    </td>   
	    <td nowrap>
             <table  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                <tr>
                       <td nowrap>&nbsp;[&nbsp;
                       </td>
                       <td nowrap id="vieworhidd"> 
                          <a href="javascript:showOrClose();"> 
                              <logic:equal name="personHistoryForm" property="isShowCondition" value="none" >查询显示</logic:equal>   
                              <logic:equal name="personHistoryForm" property="isShowCondition" value="block" >查询隐藏</logic:equal>   
                          </a>
                       </td>                       
                       <td nowrap>&nbsp;]&nbsp;&nbsp;&nbsp;&nbsp;
                       </td>
                    </tr>
             </table>
      </td>  
      <td nowrap>
          <logic:equal name="personHistoryForm" property="orglike" value="1">
                     <input type="checkbox" id='orglikeid1' name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid1'),'orglike');change();" checked>
          </logic:equal>
          <logic:notEqual name="personHistoryForm" property="orglike" value="1">
                     <input type="checkbox" id='orglikeid2' name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid2'),'orglike');change();">
          </logic:notEqual>                 
      </td>
      <td nowrap>
           <html:hidden name="personHistoryForm" styleId="orglike" property='orglike' styleClass="text"/>                 
           <bean:message key="system.browse.info.viewallpeople" />&nbsp;&nbsp;&nbsp;&nbsp;        
      </td>
      <td nowrap>
      	<table>
      		<tr>
      			<td>
      	   			<bean:message key="system.browse.info.history"/>:
      			</td>
      			<td>
      				<bean:write name="personHistoryForm" property="backname" filter="true" />
      			</td>
      			<td>
      				(<bean:write name="personHistoryForm" property="backdate" filter="true" />)
      			</td>
      		</tr>
      	</table>
      </td>      
   </tr>
   </table>
    </td>
</tr>
<tr>
   <td nowrap>
<%
	
	int flag=0;
	int j=0;
	int n=0;
%>
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:${personHistoryForm.isShowCondition}'>
  <tr>
   <td>
     <!-- 查询开始 -->
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow" id="query">
       <!--
       <tr>
          <td align="center" colspan="4" height='20' class="RecordRow" nowrap>
            <bean:message key="label.query.inforquery"/> 请选择查询条件! 
          </td>
       </tr>
       -->
       <tr>
       <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="personHistoryForm.dbcond" collection="list" scope="page"/>
       <%
           List list=(List) pageContext.getAttribute("list");
           if(list!=null&&list.size()>1){
         %>
         <td align="right" height='28' style="padding-right: 5px;" nowrap>
             <bean:message key="label.dbase"/>
         </td>
         <td align="left"  nowrap><!-- 人员库 -->
              <html:select name="personHistoryForm" property="userbase" onchange="change();" size="1">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select>
         </td>
         <%} else{
         flag=1;
         }
         %>
         
         
         <td align="right" height='28' style="padding-right: 5px;" nowrap><!-- 姓名 -->
            <bean:message key="label.title.name"/>
         </td>
         <td align="left"  nowrap>
           <input type="text" name="select_name" value="${personHistoryForm.select_name}" size="30" maxlength="31" class="text4" >
         </td>
         <%if(list!=null&&list.size()>1){ %>
       </tr>
       <%} %>
       <logic:iterate id="element" name="personHistoryForm"  property="queryfieldlist" indexId="index">            
           <!-- 时间类型 -->
          <logic:equal name="element" property="itemtype" value="D">
               <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
               %>  
              <td align="right" height='28' style="padding-right: 5px;" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
              </td>
              <td align="left"  nowrap>
                  <html:text name="personHistoryForm" property='<%="queryfieldlist["+index+"].value"%>' size="12" maxlength="10" styleClass="text4" title="输入格式：2008.08.08" onclick=""/>
                  <bean:message key="label.query.to"/>
                  <html:text name="personHistoryForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="12" maxlength="10" styleClass="text4" title="输入格式：2008.08.08"  onclick=""/>
			          <!-- 没有什么用，仅给用户与视觉效果-->
			      <!--  
			      <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>	
			      <INPUT type="radio" name="${element.itemid}" id="day"><bean:message key="label.query.day"/>
			      -->    
              </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %>   
          </logic:equal>
          <logic:equal name="element" property="itemtype" value="M">
               <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
              %> 
              <td align="right" height='28' style="padding-right: 5px;" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
              </td>
              <td align="left"  nowrap>
                  <html:text name="personHistoryForm" property='<%="queryfieldlist["+index+"].value"%>' size="30" maxlength='<%="queryfieldlist["+index+"].itemlength"%>' styleClass="text4"/>
              </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
          </logic:equal> 
           <logic:equal name="element" property="itemtype" value="N">   
              <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
              %> 
              <td align="right" height='28' style="padding-right: 5px;" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
              </td>
             <td align="left"  nowrap> 
              <html:text name="personHistoryForm" property='<%="queryfieldlist["+index+"].value"%>' size="30" maxlength="${element.itemlength}" styleClass="text4"/> 
             </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
              
           </logic:equal>
           <logic:equal name="element" property="itemtype" value="A">
              <logic:notEqual name="element" property="codesetid" value="0">  
              <%String delFuntion =  "deleteData(this,'queryfieldlist["+index+"].value');"; %>            
                  <logic:equal name="element" property="codesetid" value="UN">
                     <%
                       if(flag==0)
                       {
                           out.println("<tr>");
                           flag=1;          
                       }else{
                            flag=0;           
                       }
                      %> 
                      <td align="right" height='28' style="padding-right: 5px;" nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>
                     </td>
                     <td align="left" nowrap>
                       <html:hidden name="personHistoryForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="personHistoryForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="b0110"> 
                            <img src="/images/code.gif" onclick="openCodeCustomReportDialog('UN','<%="queryfieldlist["+index+"].viewvalue"%>','<%="queryfieldlist["+index+"].value"%>','0');" align="absmiddle"/>
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="b0110">                                         
                            <img src="/images/code.gif" onclick="openCodeCustomReportDialog('${element.codesetid}','<%="queryfieldlist["+index+"].viewvalue"%>','<%="queryfieldlist["+index+"].value"%>','0');" align="absmiddle"/>
                      </logic:notEqual>   
                    </td>
                     <%
                       if(flag==0)
        	         out.println("</tr>");
                     %>                                  
                   </logic:equal>                          
                   <logic:equal name="element" property="codesetid" value="UM">
                       <%
                       if(flag==0)
                       {
                           out.println("<tr>");
                           flag=1;          
                       }else{
                            flag=0;           
                       }
                      %>  
                      <td align="right" height='28' style="padding-right: 5px;" nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>
                      </td>
                      <td align="left" nowrap>
                       <html:hidden name="personHistoryForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="personHistoryForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="e0122"> 
                            <img src="/images/code.gif" onclick="openCodeCustomReportDialog('UM','<%="queryfieldlist["+index+"].viewvalue"%>','<%="queryfieldlist["+index+"].value"%>','0');" align="absmiddle"/>
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="e0122">                                         
                            <img src="/images/code.gif" onclick="openCodeCustomReportDialog('${element.codesetid}','<%="queryfieldlist["+index+"].viewvalue"%>','<%="queryfieldlist["+index+"].value"%>','0');" align="absmiddle"/>
                      </logic:notEqual>   
                    </td>
                     <%
                       if(flag==0)
        	         out.println("</tr>");
                     %>           
                   </logic:equal>
                   <logic:equal name="element" property="codesetid" value="@K">
                       <%
                       if(flag==0)
                       {
                           out.println("<tr>");
                           flag=1;          
                       }else{
                            flag=0;           
                       }
                      %>  
                      <td align="right" height='28' style="padding-right: 5px;" nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>
                      </td>
                      <td align="left" nowrap>
                       <html:hidden name="personHistoryForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="personHistoryForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="e01a1"> 
                            <img src="/images/code.gif" onclick="openCodeCustomReportDialog('@K','<%="queryfieldlist["+index+"].viewvalue"%>','<%="queryfieldlist["+index+"].value"%>','0');" align="absmiddle"/>
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="e01a1"> 
                       <img src="/images/code.gif" onclick="openCodeCustomReportDialog('${element.codesetid}','<%="queryfieldlist["+index+"].viewvalue"%>','<%="queryfieldlist["+index+"].value"%>','0');" align="absmiddle"/>
                    	</logic:notEqual>
                    </td>
                     <%
                       if(flag==0)
        	         out.println("</tr>");
                     %>           
                   </logic:equal>
                   <logic:notEqual name="element" property="codesetid" value="UN">
                      <logic:notEqual name="element" property="codesetid" value="UM">
                         <logic:notEqual name="element" property="codesetid" value="@K">
                             <logic:greaterThan name="element" property="itemlength" value="20">
                               <!-- 大于 -->
                                <%
                                 if(flag==0)
                                 {
                                   out.println("<tr>");
                                   flag=1;          
                                 }else{
                                   flag=0;           
                                 }
                                %>  
                                <td align="right" height='28' style="padding-right: 5px;" nowrap>
                                  <bean:write  name="element" property="itemdesc" filter="true"/>
                                </td>
                                <td align="left" nowrap>
                                  <html:hidden name="personHistoryForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                                  <html:text name="personHistoryForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                                  <img src="/images/code.gif" onclick="openCodeCustomReportDialog('${element.codesetid}','<%="queryfieldlist["+index+"].viewvalue"%>','<%="queryfieldlist["+index+"].value"%>','0');" align="absmiddle"/>
                                </td>
                               <%
                                if(flag==0)
        	                    out.println("</tr>");
                                %>         
                             </logic:greaterThan>
                             <logic:lessEqual  name="element" property="itemlength" value="20">
                               <!-- 小于等于 -->
                                 <%
                                   if(flag==1)
    				    {
    				      out.println("<td colspan=\"2\">");
                                      out.println("</td>");
                                      out.println("</tr>");
    				    }
    				%>		
    				<tr>
    				  <td align="right" height='28' style="padding-right: 5px;" nowrap>       
        	             	    <bean:write  name="element" property="itemdesc" filter="true"/>
        	             	    <html:hidden name="personHistoryForm" styleId='<%="queryfieldlist["+index+"].value"%>' property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
        	             	 </td> 
       	             	        <td align="left" colspan="3" nowrap>
       	             	           <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
       	             	             <tr>
       	             	              <td>
       	             	                 <!--checkbox-->       	             	                 
       	             	                 <hrms:codesetmultiterm codesetid="${element.codesetid}" itemid="${element.itemid}" itemvalue="${element.value}" rownum="6" hiddenname='<%="queryfieldlist["+index+"].value"%>'/>
    				       </td>
                                    </tr> 
        	             	    </table> 
        	             	</td>
        	             	</tr>
        	             	 <%flag=0;%>
                             </logic:lessEqual>
                         </logic:notEqual>
                      </logic:notEqual>
                   </logic:notEqual>
              </logic:notEqual>
              <logic:equal name="element" property="codesetid" value="0">
              
                                                              
               <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
              %> 
              <td align="right" height='28' style="padding-right: 5px;" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
              </td>
              <td align="left"  nowrap>
               <html:text name="personHistoryForm" property='<%="queryfieldlist["+index+"].value"%>' size="30" maxlength="${element.itemlength}" styleClass="text4"/>
              </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
            </logic:equal>             
         </logic:equal>
       </logic:iterate>
        <%
         if(flag==1)
    	{
    		 out.println("<td colspan=\"2\">");
             out.println("</td>");
             out.println("</tr>");
    	}
    	%> 
    	<tr>
    	  <td align="right" height='20' style="padding-right: 5px;" nowrap>
    	    
    	     <bean:message key="label.query.like"/> 
    	    
    	  </td>
    	  <td align="left" colspan="3" height='20' nowrap>
    	    <table width="100%" border="0" cellspacing="0" cellpadding="0" >
    	      <tr>
    	        <td>
    	           <logic:equal name="personHistoryForm" property="querylike" value="1">
    	            <input type="checkbox" id='querylikeid1' name="querlike2" value="true" onclick="selectCheckBox(document.getElementById('querylikeid1'),'querylike');" checked>
    	          </logic:equal>  
    	          <logic:notEqual name="personHistoryForm" property="querylike" value="1">
    	            <input type="checkbox" id='querylikeid2' name="querlike2" value="true" onclick="selectCheckBox(document.getElementById('querylikeid2'),'querylike');">
    	          </logic:notEqual>
    	           <html:hidden name="personHistoryForm" styleId="querylike" property='querylike' styleClass="text"/>
    	        </td>
    	        <td>    	        
    	          
    	             <!--  <div  id="info_cue1" style='display:none;' class="query_cue1">
    	               <bean:message key="infor.menu.query.cue1"/>
    	              </div>
    	           -->
    	          
    	        </td>
    	      </tr>
    	    </table>
    	  </td>    	  
    	</tr>
    	
     </table>
   </td>
  </tr>
    <tr>
      <td height="4">
      </td>
    </tr>
    <tr>
    	  <td align="center" colspan="4" height='20'  nowrap>    	   
    	    <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick="query('1');" class='mybutton' /> 
    	    <Input type='button' value="<bean:message key="button.sys.cond"/>" onclick='selectQ();' class='mybutton' />
    	 	<Input type='button' value="<bean:message key="button.clear"/>" onclick=' resetQuery();' class='mybutton' />
    	  </td>
    </tr>
 </table>
     <!-- 查询结束 -->
   </td>
</tr>
<tr>
     <td align='center' height="2px"></td>
</tr>
<tr>
    <td width="100%" nowrap>
     <div class="fixedDiv2" id="divId">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
           <tr class="fixedHeaderTr">        
           <logic:iterate id="info"    name="personHistoryForm"  property="browsefields">   
              <logic:equal name="info" property="visible" value="true">
              <td align="center" class="TableRow_right fixedHeaderTr" style="border-top: none;" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>              
              </td>
              </logic:equal>
             </logic:iterate> 	  	    	    		        	        	        
           </tr>
           <hrms:paginationdb id="element" allmemo="1" name="personHistoryForm" sql_str="personHistoryForm.strsql" table="" where_str="personHistoryForm.cond_str" columns="personHistoryForm.columns" order_by="order by a0000" page_id="pagination" pagerows="${personHistoryForm.pagerows}" keys="">
          <%
          if(i%2==0)
          {	
          %>
          <tr class="trShallow"  onMouseOver="javascript:tr_onclick(this,'')">
          <%}
          else
          {%> 
          <tr class="trDeep"  onMouseOver="javascript:tr_onclick(this,'DDEAFE')">
          <%
          }
          i++;          
          %>
          <bean:define id="a0100" name="element" property="${personHistoryForm.pageShowOnly }"></bean:define>    
	     <logic:iterate id="info"    name="personHistoryForm"  property="browsefields">   
	     	   	     		
	     	   <logic:equal name="info" property="visible" value="true">
	     	   
                  <logic:equal  name="info" property="itemtype" value="M">               
                    <td  align="left" style="word-break:break-all;border-top: none;"   class="RecordRow_right" onmouseout='UnTip();' onmouseover="Tip('<bean:write  name="element" property="${info.itemid}" filter="false"/>',STICKY ,true);" nowrap>        
                  </logic:equal>
                  <logic:equal  name="info" property="itemtype" value="A">               
                    <td align="left" class="RecordRow_right" style="border-top: none;" nowrap>        
                  </logic:equal>
                  <logic:equal  name="info" property="itemtype" value="D">               
                    <td align="left" class="RecordRow_right" style="border-top: none;" nowrap>        
                  </logic:equal>
                  <logic:equal name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow_right" style="border-top: none;" nowrap>        
                  </logic:equal>
                      &nbsp;
                  <logic:equal  name="info" property="codesetid" value="0">   
                   <logic:notEqual name="info"   property="itemid" value="a0101">  
                   <logic:equal  name="info" property="itemtype" value="M">     
                  		<span    style="width:200px; height:15px; overflow:hidden;text-overflow:ellipsis;" ><bean:write  name="element" property="${info.itemid}" filter="false"/> </span>                 
				   </logic:equal>
                  <logic:notEqual  name="info" property="itemtype" value="M">               
                    	<bean:write  name="element" property="${info.itemid}" filter="false"/> 
                  </logic:notEqual>      
                     
                   </logic:notEqual>
                      <logic:equal name="info"   property="itemid" value="a0101">  
          	   			 <a href="###" onclick="winhrefOT('${a0100}','nil_body');"> 
          	   			 <bean:write name="element" property="a0101" filter="true"/>
          	   			  </a>
          	   			
          	   	 	</logic:equal>
                  </logic:equal>
                 <logic:notEqual  name="info" property="codesetid" value="0">  
                 <logic:notEqual  name="info"   property="itemid" value="e01a1">  
                  <logic:notEqual  name="info"   property="itemid" value="a0101">  
                   <logic:equal name="info" property="codesetid" value="UM">
                        <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${personHistoryForm.uplevel}"/>  	      
          	            <!-- 
          	            	//tianye update start
							//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
          	            	-->
          	            	<logic:notEqual  name="codeitem" property="codename" value="">
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:notEqual>
          	          		<logic:equal  name="codeitem" property="codename" value="">
          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${personHistoryForm.uplevel}"/>  
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:equal>   
          	           		<!-- end -->           	           
                   </logic:equal>
                   <logic:notEqual name="info" property="codesetid" value="UM">
                      <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	   <bean:write name="codeitem" property="codename" />  
                   </logic:notEqual>
          	     </logic:notEqual>
          	    </logic:notEqual>          	    
          	    <logic:equal name="info"   property="itemid" value="e01a1"> 
                     <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	         <bean:write name="codeitem" property="codename" />           	                        
                   
          	   </logic:equal>
          	       
                </logic:notEqual>  
              &nbsp;</td>
              </logic:equal>
             </logic:iterate> 
		    	 
   	    		        	        	        
          </tr>
        </hrms:paginationdb>        
</table>
</div>
<div id='pageDiv' style="padding: 0px;">
<table width="100%" id="pageTable" align="center" cellspacing="0" cellpadding="0" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            
					<hrms:paginationtag name="personHistoryForm"
								pagerows="${personHistoryForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="personHistoryForm" property="pagination" nameId="browseForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</div>
<table  align="center" style="margin-top: 2px" >
          <tr>
            <td align="left">
            <hrms:priv func_id="260125">
	 	    	<input type="button" value="<bean:message key="goabroad.collect.educe.excel"/>" onclick="executeOutFile();" class="mybutton">
            </hrms:priv>
            <logic:empty name="personHistoryForm" property="returnvalue">
            <hrms:priv func_id="260123">
	 	    	<input type="button" name="addbutton"  value="<bean:message key="leaderteam.leaderframe.statisticanalyse"/>" class="mybutton" onclick='static();' >
         	</hrms:priv>
         	</logic:empty>
         	<logic:notEmpty name="personHistoryForm" property="returnvalue">
         		<input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnQ('${personHistoryForm.scantype}');" >  	
         	</logic:notEmpty>
            </td>
          </tr>          
</table>
    </td>
</tr>
</table>


</html:form>
<script language="javascript">
	function returnQ(flag)
{
   parent.menupnl.toggleCollapse(true);
   if(flag=="scan")
   		window.location.href="/workbench/orginfo/searchorginfodata.do?b_query=link";
   else if(flag=="scanduty") 
   	  window.location.href="/workbench/dutyinfo/searchdutyinfodata.do?b_query=link";  
   else
     return ;
}

	function setDivStyle(){
		document.getElementById("divId").style.height = (document.body.clientHeight-150) + "px";
	    document.getElementById("divId").style.width = (document.body.clientWidth-15) + "px"; 
	    document.getElementById("pageDiv").style.width = (document.body.clientWidth-15) + "px"; 
	    if(!getBrowseVersion()) {
	    	document.getElementById("divId").style.width = (document.body.clientWidth-19) + "px";
	    	document.getElementById("pageTable").style.marginLeft = "-1px";
	    }
	}
	window.onresize = function(){
		setDivStyle();
	}	
	
	setTimeout("setDivStyle()", 100);
</script>