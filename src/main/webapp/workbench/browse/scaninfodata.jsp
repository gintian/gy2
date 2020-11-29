<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.struts.taglib.CommonData"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.browse.BrowseForm"%>
<%@ page import="java.util.HashMap" %> 
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="../../../components/extWidget/proxy/TransactionProxy.js"></script>
<script language="JavaScript" src="../../../components/codeSelector/codeSelector.js"></script>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getManagePrivCodeValue();
	BrowseForm browseForm=(BrowseForm)session.getAttribute("browseForm");
	HashMap partMap=(HashMap)browseForm.getPart_map();
	int i=0;
	String bosflag="";
    if(userView!=null){
     bosflag = userView.getBosflag();
    }
%>
<script language="javascript">
window.onresize = function(){
	setDivStyle();
}

function setDivStyle(){
    //IE11非兼容模式样式要加px才生效 wangbs 2019年3月12日09:42:52
    document.getElementById("search-div").style.width = document.body.clientWidth - 15+'px';
    document.getElementById("fixedDiv").style.width = document.body.clientWidth-15+'px';

    if(getBrowseVersion()){//IE浏览器下，表格样式不对  wangbs  2019年3月12日09:42:39
        document.getElementById("fixedDiv").style.height=window.screen.height-400+'px';
    }else{
        document.getElementById("fixedDiv").style.height = document.body.clientHeight-150+'px';
    }
    //document.body.clientHeight在IE浏览器下的值不正确 wangbs 2019年3月12日09:42:24
    // document.getElementById("fixedDiv").style.height = document.body.clientHeight-150+'px';
    //document.getElementById("pageDiv").style.height = document.body.clientHeight-150;
    //document.getElementById("pageDiv").style.width = document.body.clientWidth-15;
}

function checkDay(obj,ve)
{
    var o_obj=document.getElementById('day');   
    if(o_obj&&o_obj.checked==true)
    {
       var ttop  = obj.offsetTop;     //TT控件的定位点高
	   var thei  = obj.clientHeight;  //TT控件本身的高
	   var tleft = obj.offsetLeft;    //TT控件的定位点宽
	   var waitInfo=eval("wait")
	   while (obj = obj.offsetParent){ttop+=obj.offsetTop; tleft+=obj.offsetLeft;}
	   waitInfo.style.top=ttop+thei+6;
	   ve=3;
	   if(ve==1)
	      waitInfo.style.left=tleft+326;
	   else if(ve==2)   
	      waitInfo.style.left=tleft+220;
	   else
	      waitInfo.style.left=tleft;
	   waitInfo.style.display="";
	   
    }else
    { 
       var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
    }
}
function checkHide()
{
  Element.hide('wait');
}
function showOrClose()
{
	var obj=eval("aa");
   var obj3=eval("vieworhidd");
//var obj2=eval("document.browseForm.isShowCondition");
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
   
   setDivStyle();
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
      if(vo)
         vo.value="1";
   }else
   {
         var vo=document.getElementById(hiddname);
      if(vo)
         vo.value="0";
   }

}
function  clearCheckbox()
{
   var len=document.browseForm.elements.length;
       var i;
     
        for (i=0;i<len;i++)
        {
         if (document.browseForm.elements[i].type=="checkbox")
          {
             
            document.browseForm.elements[i].checked=false;
          }
        }
}
function openwin(url)
{
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}
function change()
{
      browseForm.action="/workbench/browse/scaninfodata.do?b_search=link";
      browseForm.submit();
}
function winhrefOT(a0100,target)
{
	//alert(a0100);
   if(a0100=="")
      return false;
   <logic:equal value="scanstandardduty" property="returnvalue" name="browseForm">
   		browseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100="+a0100+"&flag=notself&returnvalue=scanstandardduty";
   </logic:equal>
   <logic:notEqual value="scanstandardduty" property="returnvalue" name="browseForm">
   		browseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100="+a0100
   				+"&flag=notself&returnvalue=scan";
   </logic:notEqual>
   browseForm.target=target;
   browseForm.submit();
}
function returnQ(flag)
{
   parent.menupnl.toggleCollapse(true);
   if(flag=="scan")
   		window.location.href="/workbench/orginfo/searchorginfodata.do?b_query=link&code=${browseForm.return_codeid}&kind=${browseForm.kind}&orgtype=${browseForm.orgtype}";
      //browseForm.action="/workbench/orginfo/searchorginfodata.do?b_query=link&code=${browseForm.return_codeid}&kind=${browseForm.kind}&orgtype=${browseForm.orgtype}";
   else if(flag=="scanduty"){ 
   		<logic:equal value="scanstandardduty" property="returnvalue" name="browseForm">
   		window.location.href="/dtgh/party/searchpartybusinesslist.do?b_query=link&a_code=${browseForm.return_codeid}";  
   </logic:equal>
   <logic:notEqual value="scanstandardduty" property="returnvalue" name="browseForm">
   		window.location.href="/workbench/dutyinfo/searchdutyinfodata.do?b_query=link&code=${browseForm.return_codeid}&kind=${browseForm.kind}&orgtype=${browseForm.orgtype}";  
   </logic:notEqual>
      //browseForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_query=link&code=${browseForm.return_codeid}&kind=${browseForm.kind}&orgtype=${browseForm.orgtype}";
   }else
     return ;
   //browseForm.submit();
}
function query(query)
{
	<logic:equal value="scanstandardduty" property="returnvalue" name="browseForm">
		browseForm.target="mil_body";
   </logic:equal>
   browseForm.action="/workbench/browse/scaninfodata.do?b_search=link&query="+query;
   browseForm.submit();
}
function viewPhoto(flag)
{
	<logic:equal value="scanstandardduty" property="returnvalue" name="browseForm">
		browseForm.target="mil_body";
   </logic:equal>
   browseForm.action="/workbench/browse/scan_photo.do?br_photo=link&scantype="+flag;
   browseForm.submit();
}
function selectCheckBox(obj,hiddname)
{
   if(obj.checked==true)
   {
      var vo=document.getElementById(hiddname);
      var Info=eval("info_cue1");	
	  Info.style.display="";
      if(vo)
         vo.value="1";
   }else
   {
         var vo=document.getElementById(hiddname);
         var Info=eval("info_cue1");	
	     Info.style.display="none";
         if(vo)
            vo.value="0";
   }

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
    query("");
}
</script>
<style type="text/css">
.myfixedDiv
{  
    overflow:auto; 
    height:expression(document.body.clientHeight-150);
    width:expression(document.body.clientWidth-30); 
    BORDER-BOTTOM: #99BBE8 0 solid; 
    BORDER-LEFT: #99BBE8 1px solid; 
    BORDER-RIGHT: #99BBE8 1px solid; 
    BORDER-TOP: #99BBE8 0 solid;
}

.searchDiv
{  
    overflow:auto; 
    width:expression(document.body.clientWidth-30); 
    BORDER-BOTTOM: #99BBE8 0 solid; 
    BORDER-LEFT: #99BBE8 0 solid; 
    BORDER-RIGHT: #99BBE8 0 solid; 
    BORDER-TOP: #99BBE8 0 solid;
}

.RecordRow_top {
	border: inset 1px #94B6E6;	
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
}	
	
 </style>
 <hrms:themes />
 <!--zgd 2014-7-9 信息列表中岗位中有兼职情况的特殊处理。-->
<style>
<%if("hcm".equals(bosflag)){%>
.partdescdiv{           
	margin-top:-5px;
}
<%}%>
</style>

 <html:form action="/workbench/browse/scaninfodata">
 <div  style="width:99%;padding-top:5px;padding-left:5px;padding-right:5px;">
  <table width='10%' border="0px" cellpadding="0" cellspacing="0"> 
    <tr>
      <td>
          <table>
             <tr>
               <logic:notEmpty name="browseForm" property="unit_code_mess">
                  <td nowrap>
                  <logic:equal name="browseForm" property="orgflag" value="1">  
                       当前岗位编码：${browseForm.unit_code_mess}&nbsp;&nbsp;&nbsp;&nbsp;
                  </logic:equal>    
                   <logic:equal name="browseForm" property="orgflag" value="2">  
                       当前组织单元编码：${browseForm.unit_code_mess}&nbsp;&nbsp;&nbsp;&nbsp;
                  </logic:equal>   
                  </td>
               </logic:notEmpty>
                <td nowrap>
                  <logic:equal name="browseForm" property="orgflag" value="1">  
                       岗位名称：${browseForm.codemess}&nbsp;&nbsp;&nbsp;&nbsp;
                  </logic:equal>
                   <logic:equal name="browseForm" property="orgflag" value="2">  
                       当前组织单元名称：${browseForm.codemess}&nbsp;&nbsp;&nbsp;&nbsp;
                  </logic:equal>   
                </td>
                <td nowrap>
                  <table  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr>
                       <td nowrap>[&nbsp;
                       </td>
                       <td nowrap id="vieworhidd"> 
                       
                            <a href="javascript:showOrClose();"> 
                           
                            <logic:equal name="browseForm" property="isShowCondition" value="none" >查询显示</logic:equal>   
                         <logic:equal name="browseForm" property="isShowCondition" value="" >查询隐藏</logic:equal>   
                            
                             </a>
                       </td>                       
                       <td nowrap>&nbsp;]&nbsp;&nbsp;&nbsp;&nbsp;
                       </td>
                    </tr>
                  </table>
                </td>               
                <td nowrap>
                 <logic:equal name="browseForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid1" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid1'),'orglike');change();" checked>
                 </logic:equal>
                 <logic:notEqual name="browseForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid2" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid2'),'orglike');change();">
                 </logic:notEqual>
                 
               <html:hidden name="browseForm" property='orglike' styleId="orglike" styleClass="text"/>                 
                     显示当前组织单元下所有机构人员
                </td>
             </tr>
          </table>
      </td>
    </tr>
   <tr>     
     <td>
     <%
	
	int flag=0;
	int j=0;
	int n=0;
%>
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:${browseForm.isShowCondition}'>
  <tr>
   <td>
     <!-- 查询开始 -->
     <div id="search-div" class="searchDiv">
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow" id="query">
       <tr class="trShallow1">
          <td align="center" colspan="4" height='20' class="RecordRow" nowrap>
            <bean:message key="label.query.inforquery"/><!-- 请选择查询条件! -->
          </td>
       </tr>
       <tr>
          <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="browseForm.dbcond" collection="list" scope="page"/>
               <bean:size id="listsize" name="list"/>
               <logic:equal value="1" name="listsize">
                    <%flag=1; %>
               </logic:equal>
                <logic:notEqual value="1" name="listsize">
			         <td align="right" height='28' nowrap>
			             &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="label.dbase"/>&nbsp;
			         </td>
			         <td align="left"  nowrap ><!-- 人员库 -->
				              <html:select name="browseForm" property="userbase" onchange="change();" size="1">
				                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				              </html:select>
			         </td>
               </logic:notEqual>
         <td align="right" height='28' nowrap><!-- 姓名 -->
            <bean:message key="label.title.name"/>&nbsp;
         </td>
         <td align="left"  nowrap>
           <input type="text" name="select_name" value="${browseForm.select_name}" size="31" maxlength="31" class="textColorWrite" >
         </td>
         <logic:notEqual value="1" name="listsize">
          </tr>
         </logic:notEqual>
       <logic:iterate id="element" name="browseForm"  property="scanfieldlist" indexId="index">            
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
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
              <td align="left"  nowrap>
                  <html:text name="browseForm" property='<%="scanfieldlist["+index+"].value"%>' size="13" maxlength="10" styleClass="textColorWrite" title="输入格式：2008.08.08" onclick=""/>
                  <bean:message key="label.query.to"/>
                  <html:text name="browseForm" property='<%="scanfieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleClass="textColorWrite" title="输入格式：2008.08.08"  onclick=""/>
			          <!-- 没有什么用，仅给用户与视觉效果-->
			      <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>	
			      <INPUT type="radio" name="${element.itemid}" id="day"><bean:message key="label.query.day"/>
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
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
              <td align="left"  nowrap>
                  <html:text name="browseForm" property='<%="scanfieldlist["+index+"].value"%>' size="31" maxlength='<%="scanfieldlist["+index+"].itemlength"%>' styleClass="textColorWrite"/>
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
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
             <td align="left"  nowrap> 
              <html:text name="browseForm" property='<%="scanfieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="textColorWrite"/> 
             </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
              
           </logic:equal>
           <logic:equal name="element" property="itemtype" value="A">
              <logic:notEqual name="element" property="codesetid" value="0">              
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
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                     </td>
                     <td align="left" nowrap>
                       <html:hidden name="browseForm" property='<%="scanfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="browseForm" property='<%="scanfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="b0110"> 
                            <%-- <img src="/images/code.gif" onclick="openInputCodeDialogOrgInputPos('UN','<%="scanfieldlist["+index+"].viewvalue"%>','<%=manager%>',1);" align="absmiddle"/> --%>
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='UN'  onlySelectCodeset="true" inputname="<%="scanfieldlist["+index+"].viewvalue"%>" valuename='<%="scanfieldlist["+index+"].value"%>'  align="absmiddle" />
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="b0110">                                         
                            <%-- <img src="/images/code.gif" onclick="openInputCodeDialog('${element.codesetid}','<%="scanfieldlist["+index+"].viewvalue"%>','0');" align="absmiddle"/> --%>
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}'  onlySelectCodeset="false" inputname="<%="scanfieldlist["+index+"].viewvalue"%>" valuename='<%="scanfieldlist["+index+"].value"%>'  align="absmiddle" />
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
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                      </td>
                      <td align="left" nowrap>
                       <html:hidden name="browseForm" property='<%="scanfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="browseForm" property='<%="scanfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="e0122"> 
                            <%-- <img src="/images/code.gif" onclick="openInputCodeDialogOrgInputPos('UM','<%="scanfieldlist["+index+"].viewvalue"%>','<%=manager%>',1);" align="absmiddle"/> --%>
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='UM'  onlySelectCodeset="true" inputname="<%="scanfieldlist["+index+"].viewvalue"%>" valuename='<%="scanfieldlist["+index+"].value"%>'  align="absmiddle" />
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="e0122">                                         
                            <%-- <img src="/images/code.gif" onclick="openInputCodeDialog('${element.codesetid}','<%="scanfieldlist["+index+"].viewvalue"%>','0');" align="absmiddle"/> --%>
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}'  onlySelectCodeset="false" inputname="<%="scanfieldlist["+index+"].viewvalue"%>" valuename='<%="scanfieldlist["+index+"].value"%>'  align="absmiddle" />
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
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                      </td>
                      <td align="left" nowrap>
                       <html:hidden name="browseForm" property='<%="scanfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="browseForm" property='<%="scanfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
                       <%-- <img src="/images/code.gif" onclick="openInputCodeDialog('${element.codesetid}','<%="scanfieldlist["+index+"].viewvalue"%>','0');" align="absmiddle"/> --%>
                       <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}'  onlySelectCodeset="false" inputname="<%="scanfieldlist["+index+"].viewvalue"%>" valuename='<%="scanfieldlist["+index+"].value"%>'  align="absmiddle" />
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
                                <td align="right" height='28' nowrap>
                                  <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                                </td>
                                <td align="left" nowrap>
                                  <html:hidden name="browseForm" property='<%="scanfieldlist["+index+"].value"%>' styleClass="text"/>                               
                                  <html:text name="browseForm" property='<%="scanfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
                                  <%-- <img src="/images/code.gif" onclick="openInputCodeDialog('${element.codesetid}','<%="scanfieldlist["+index+"].viewvalue"%>','0');" align="absmiddle"/> --%>
                                  <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}'  onlySelectCodeset="false" inputname="<%="scanfieldlist["+index+"].viewvalue"%>" valuename='<%="scanfieldlist["+index+"].value"%>'  align="absmiddle" />
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
    				  <td align="right" height='28' nowrap>       
        	             	    <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
        	             	    <html:hidden name="browseForm" styleId='<%="scanfieldlist["+index+"].value"%>' property='<%="scanfieldlist["+index+"].value"%>' styleClass="text"/>                               
        	             	 </td> 
       	             	        <td align="left" colspan="3" nowrap>
       	             	           <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
       	             	             <tr>
       	             	              <td>
       	             	                 <!--checkbox-->       	             	                 
       	             	                 <hrms:codesetmultiterm codesetid="${element.codesetid}" itemid="${element.itemid}" itemvalue="${element.value}" rownum="6" hiddenname='<%="scanfieldlist["+index+"].value"%>'/>
    				                  
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
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
              <td align="left"  nowrap>
               <html:text name="browseForm" property='<%="scanfieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="textColorWrite"/>
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
    	  <td align="right" height='20'  nowrap>
    	    
    	     <bean:message key="label.query.like"/>
    	    
    	  </td>
    	  <td align="left" colspan="3" height='20' nowrap>
    	  <table width="100%" border="0" cellspacing="0" cellpadding="0" >
    	      <tr>
    	        <td>
    	    <logic:equal name="browseForm" property="querylike" value="1">
    	        <input type="checkbox" id="querylikeid1" name="querlike2" value="true" onclick="selectCheckBox(document.getElementById('querylikeid1'),'querylike');" checked>
    	    </logic:equal>  
    	    <logic:notEqual name="browseForm" property="querylike" value="1">
    	        <input type="checkbox" id="querylikeid2" name="querlike2" value="true" onclick="selectCheckBox(document.getElementById('querylikeid2'),'querylike');">
    	    </logic:notEqual>
    	      <html:hidden name="browseForm" property='querylike' styleId="querylike" styleClass="text"/>&nbsp;
    	    </td>
    	    <td> 
    	      <div  id="info_cue1" style='display:none;' class="query_cue1">
    	         <bean:message key="infor.menu.query.cue1"/>
    	     </div>
    	     </td>
    	      </tr>
    	    </table>  
    	  </td>    	  
    	</tr>
    	
     </table>
     </div>
   </td>
  </tr>
    <tr>
      <td height="5">
      </td>
    </tr>
    <tr>
    	  <td align="center" colspan="4" height='20' style="padding-bottom: 5px;" nowrap>    	   
    	    <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick='query(1);' class='mybutton' />  &nbsp;&nbsp;
    	    <Input type='button' value="<bean:message key="button.clear"/>" onclick=' resetQuery();' class='mybutton' />  &nbsp;&nbsp;
    	  </td>
    </tr>
 </table>
     
     </td>
   </tr>
   <tr>
    <td>
     <table  width="100%" border="0" cellspacing="0" align="center" cellpadding="0"  style='display:' >
      <tr>
       <td>
    <div class="myfixedDiv" id='fixedDiv' style="padding: 0;">
         <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
           <tr>
           <logic:iterate id="info"    name="browseForm"  property="browsefields">   
              <logic:equal name="info" property="visible" value="true">
              <td align="center" class="TableRow" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;              
              </td>
              </logic:equal>
             </logic:iterate> 	
		    <logic:notEqual name="browseForm" property="cardid" value="-1">
             <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.base.info"/>          	
             </td>	 
             </logic:notEqual>               
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
	    	</td>     	    	    		        	        	        
           </tr>
           <hrms:paginationdb id="element" name="browseForm" sql_str="browseForm.strsql" table="" where_str="browseForm.cond_str" columns="browseForm.columns" order_by="browseForm.order_by" pagerows="${browseForm.pagerows}" page_id="pagination" keys="${browseForm.userbase}A01.a0100">
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
          
          <%
          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
   	   String a0100_encrypt=(String)abean.get("a0100");              	            	   
         request.setAttribute("a0100_encrypt",PubFunc.encrypt(a0100_encrypt)); 
          %>
          
          <bean:define id="a0100" name="element" property="a0100"/>
          <hrms:parttime a0100="${a0100}" nbase="${browseForm.userbase}" part_map="<%=partMap%>" name="element" scope="page" code="${browseForm.code}" kind="${browseForm.kind}" uplevel="${browseForm.uplevel}"  b0110_desc="b0110_desc" e0122_desc="e0122_desc" part_desc="part_desc" descOfPart="descOfPart"/>
	        <logic:iterate id="info"    name="browseForm"  property="browsefields">   
	     	   <logic:equal name="info" property="visible" value="true">
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="RecordRow" nowrap>&nbsp;        
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow" nowrap> &nbsp;       
                  </logic:equal>    
                  <logic:equal  name="info" property="codesetid" value="0">   
                   <logic:notEqual name="info"   property="itemid" value="a0101">        
                     <bean:write  name="element" property="${info.itemid}" filter="true"/>&nbsp;
                   </logic:notEqual>
                      <logic:equal name="info"   property="itemid" value="a0101">  
          	   			 <a href="###" onclick="winhrefOT('${a0100}','nil_body');"> 
          	   			 <bean:write name="element" property="a0101" filter="true"/>
          	   			  </a>
          	   			&nbsp;
          	   	 	</logic:equal>
                  </logic:equal>
                 <logic:notEqual  name="info" property="codesetid" value="0">  
                  <logic:notEqual  name="info"   property="itemid" value="e01a1">  
                   <logic:notEqual  name="info"   property="itemid" value="a0101">  
                    <logic:notEqual  name="info"   property="itemid" value="e0122">  
                     <logic:notEqual  name="info"   property="itemid" value="b0110">  
                      <logic:equal name="info" property="codesetid" value="UM">
                       <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${browseForm.uplevel}"/>  	      
          	             <bean:write name="codeitem" property="codename" />&nbsp;
                      </logic:equal>
                      <logic:notEqual name="info" property="codesetid" value="UM">
                       <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	   <bean:write name="codeitem" property="codename" />&nbsp;  
                      </logic:notEqual>
                     </logic:notEqual>
                   </logic:notEqual>
          	     </logic:notEqual>
          	    </logic:notEqual>
          	    <logic:equal name="info"   property="itemid" value="b0110"> 
          	          ${b0110_desc}
          	    </logic:equal> 
          	    <logic:equal name="info"   property="itemid" value="e0122">  
          	         ${e0122_desc}
          	    </logic:equal> 	   
          	    <logic:equal name="info"   property="itemid" value="e01a1"> 
          	       <logic:empty name="browseForm" property="ishavepostdesc">
                     <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	         <bean:write name="codeitem" property="codename" />&nbsp; 
                   </logic:empty> 
                   <logic:notEmpty name="browseForm" property="ishavepostdesc">
                     <logic:equal name="browseForm" property="ishavepostdesc" value="true">
                     <%
                     LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element");
                     String e01a1_encrypt=(String)bean.get("e01a1");
                     e01a1_encrypt=PubFunc.encrypt(e01a1_encrypt);
                     request.setAttribute("e01a1_encrypt", e01a1_encrypt);
                     %>
                      <a href="/workbench/browse/showposinfo.do?b_browse=link&infokind=4&returnFlag=4&a0100=${e01a1_encrypt}">
                         <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	          <bean:write name="codeitem" property="codename" /></a>&nbsp; 
          	         </logic:equal>
          	         <logic:equal name="browseForm" property="ishavepostdesc" value="false">
                      <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	           <bean:write name="codeitem" property="codename" />&nbsp; 
          	         </logic:equal>
                   </logic:notEmpty> 
                   <logic:empty name="codeitem" property="codename">${descofpart}</logic:empty>  
   	               <logic:notEmpty name="codeitem" property="codename">${part_desc}</logic:notEmpty>
          	   </logic:equal>
              </logic:notEqual>  
            </td>
            </logic:equal>
          </logic:iterate> 
		  <logic:notEqual name="browseForm" property="cardid" value="-1">
	    			 <td align="center" class="RecordRow" nowrap>&nbsp;
               			<a href="###" onclick='openwin("/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=${browseForm.userbase}&a0100=${a0100_encrypt}&inforkind=1&tabid=${browseForm.cardid}&multi_cards=-1");'>
               			<img src="../../images/table.gif" border="0"></a>
				     </td>	                
             </logic:notEqual>	                		
             <td align="center" class="RecordRow" nowrap>&nbsp;
             	<a href="###" onclick="winhrefOT('${a0100}','nil_body');"> 
          	   		<img src="../../images/view.gif" border="0">
            	 </a>            		 	            	     	   
	      </td>    	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>        
        </table>
        </div>
        </td>
        </tr>
        <tr>
          <td>
           <table width="100%"  align="center" class="RecordRowP">
		     <tr>
		    <td valign="bottom" class="tdFontcolor">
		           <hrms:paginationtag name="browseForm"
								pagerows="${browseForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="browseForm" property="pagination" nameId="browseForm" scope="page">
				</hrms:paginationdblink>
			</td>
		   </tr>
          </table>
         </td>
        </tr>
         </table>
      </td>
   </tr>
   <tr height="35">
     <td nowrap="nowrap">
        <input type="button" name="addbutton"  value="<bean:message key="button.query.viewphoto"/>" class="mybutton" onclick="viewPhoto('${browseForm.scantype}');" >  
        <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnQ('${browseForm.scantype}');" >  	
        
     </td>
   </tr>
  </table>
</div>
</html:form>

<div id='wait' style='display:none;position: absolute; left:0; top:0;'>
   <font color="red">输入格式：2008.08.08</font>
</div> 

<script>
setDivStyle();
</script>