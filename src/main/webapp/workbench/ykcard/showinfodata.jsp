<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hjsj.hrms.actionform.ykcard.CardTagParamForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>
<%@ page import="java.util.HashMap" %> 
<%@page import="java.util.List"%>

<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			int status=userView.getStatus();
			String manager=userView.getManagePrivCodeValue();
			String way=request.getParameter("way");
			way=way==null?"":way;
			CardTagParamForm selfInfoForm = (CardTagParamForm)session.getAttribute("cardTagParamForm");
			//int llen=selfInfoForm.getLlen(); 
			int pagerows = selfInfoForm.getPagerows();
			int fflag=1;
			String webserver=SystemConfig.getPropertyValue("webserver");
			if(webserver.equalsIgnoreCase("websphere"))
				fflag=2;
			String inforquery_extend=SystemConfig.getPropertyValue("inforquery_extend");			
	        //HashMap partMap=(HashMap)selfInfoForm.getPart_map();
%>
<%int i=0;%>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/ajax/basic.js"></SCRIPT>
<hrms:linkExtJs/>
<script language='JavaScript' src='/components/codeSelector/codeSelector.js'></script><!--员工薪酬-处理浏览器兼容问题：代码树选择  -->
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<hrms:themes/>
<html:form action="/workbench/ykcard/showinfodata">
<script language="javascript">
   function change()
   {
      cardTagParamForm.action="/workbench/ykcard/showinfodata.do?b_search=link&code=${cardTagParamForm.code}&kind=${cardTagParamForm.kind}";
      cardTagParamForm.submit();
   }
   function winhref(a0100,b0110)
{
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
    //cardTagParamForm.action="/system/options/cardsalaryshow.do?b_cardshow=link&b0110="+b0110+"&pre=${cardTagParamForm.userbase}&flag=noself&payment=payment";
    var pre = '${cardTagParamForm.userbase}';
    cardTagParamForm.action="/module/gz/mysalary/MySalaryMain.html?b_query=link&pre="+encodeURI(pre)+"&a0100="+encodeURI(a0100);
    cardTagParamForm.target="mil_body";
    cardTagParamForm.submit();
}

function showOrClose()
{
		var obj=document.getElementById("aa");
	    var obj3=document.getElementById("vieworhidd");
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
         var Info=eval("info_cue1");	
	   Info.style.display="";
   }else
   {
      var Info=eval("info_cue1");	
	  Info.style.display="none";
      var vo=document.getElementById(hiddname);
      if(vo)
         vo.value="0";
   }

}

function selectQ()
   {
       var code="${cardTagParamForm.code}";
       var kind="${cardTagParamForm.kind}";
       var tablename="${cardTagParamForm.userbase}";
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
         
       //var thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type=1&a_code="+a_code+"&tablename="+tablename;
       var thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type=1&a_code="+a_code+"&tablename="+tablename+"&fieldsetid=A01";
       var dw=700,dh=430,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
       var  return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
      if(return_vo!=null){
            var expr= return_vo.expr;
            var factor=return_vo.factor;
            var history=return_vo.history;
            var o_obj=document.getElementById('factor');
            o_obj.value=factor;
            o_obj=document.getElementById('expr');
            o_obj.value=expr;
            o_obj=document.getElementById('history');
            o_obj.value=history;
            document.getElementsByName('likeflag')[0].value=return_vo.likeflag;
            cardTagParamForm.action="/workbench/ykcard/showinfodata.do?b_search=link&code=${cardTagParamForm.code}&kind=${cardTagParamForm.kind}&check=ok&isAdvance=0&query=1";
			//zgd 2014-1-13 在任意页进入某人员信息后，点返回，都跳到第一页；通过修改isAdvance参数为0，返回后就还在先前所在页。
            cardTagParamForm.submit();
      } 
   }
   function searchinfo(query)
{
      cardTagParamForm.action="/workbench/ykcard/showinfodata.do?b_search=link&code=${cardTagParamForm.code}&kind=${cardTagParamForm.kind}&query=1&isAdvance=0";
      cardTagParamForm.submit();
}
  document.oncontextmenu = function() {return false;}
  //var pre = '${cardTagParamForm.userbase}';
  //alert(pre);
  
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
     o_obj=document.getElementById('history');
     o_obj.value="";
     searchinfo("");
}
  function showPhoto() {
	  cardTagParamForm.action="/workbench/ykcard/showinfodata.do?b_view_photo=link";
	  cardTagParamForm.submit();
  }
</script>
<style>
.TableRow_right{
	BORDER-TOP: 0pt solid;
}
</style>
 <table width="100%" border="0" cellspacing="0"   cellpadding="0">
 <html:hidden name="cardTagParamForm" property="factor" styleId="factor" styleClass="text"/>
<html:hidden name="cardTagParamForm" property="expr" styleId="expr" styleClass="text"/> 
<html:hidden name="cardTagParamForm" property="history" styleId="history" styleClass="text"/>   
<html:hidden name="cardTagParamForm" property="orgparentcode" />
<html:hidden name="cardTagParamForm" property="deptparentcode" />
<html:hidden name="cardTagParamForm" property="posparentcode" />
<html:hidden name="cardTagParamForm" property="likeflag" />
 <input type="hidden" name="a0100" id="a0100">
 <tr>
   <td aling="left" >
     <table border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td align="left"  nowrap>
              <logic:notEmpty  name="cardTagParamForm" property="code">
	          <bean:message key="system.browse.info.currentorg"/>:
	          <hrms:codetoname codeid="UN" name="cardTagParamForm" codevalue="code" codeitem="codeitem" scope="session"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  <hrms:codetoname codeid="UM" name="cardTagParamForm" codevalue="code" codeitem="codeitem" scope="session"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  <hrms:codetoname codeid="@K" name="cardTagParamForm" codevalue="code" codeitem="codeitem" scope="session"/>  	      
          	  <bean:write name="codeitem" property="codename" />
	         </logic:notEmpty>
	    </td> 
	     <td nowrap>
             <table  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                <tr>
                       <td nowrap>&nbsp;[&nbsp;
                       </td>
                       <td nowrap id="vieworhidd"> 
                          <a href="javascript:showOrClose();"> 
                              <logic:equal name="cardTagParamForm" property="isShowCondition" value="none" >查询显示</logic:equal>   
                              <logic:equal name="cardTagParamForm" property="isShowCondition" value="block" >查询隐藏</logic:equal>   
                          </a>
                       </td>                       
                       <td nowrap>&nbsp;]&nbsp;&nbsp;&nbsp;&nbsp;
                       </td>
                    </tr>
             </table>
         </td>  
         <td nowrap>
                 <logic:equal name="cardTagParamForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid1" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid1'),'orglike');change();" checked>
                 </logic:equal>
                 <logic:notEqual name="cardTagParamForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid2" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid2'),'orglike');change();">
                 </logic:notEqual>                 
                 <html:hidden name="cardTagParamForm" property='orglike' styleId="orglike" styleClass="text"/>                 
                 <bean:message key="system.browse.info.viewallpeople"/>
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
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:${cardTagParamForm.isShowCondition}'>
  <tr>
   <td>    
     <!-- 查询开始 -->
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id="query" class="RecordRow">
     <!-- 
       <tr class="trShallow1">
          <td align="center" colspan="4" height='20' class="RecordRow" nowrap>
            <bean:message key="label.query.inforquery"/>请选择查询条件! 
          </td>
       </tr>
       -->
       <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="cardTagParamForm.dbcond" collection="list" scope="page"/>
       <%
           List list=(List) pageContext.getAttribute("list");
           if(list!=null&&list.size()>1){
       %>
         <tr>
           <td align="right" height='28' nowrap>
             &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="label.dbase"/>&nbsp;
           </td>
           <td align="left"  nowrap><!-- 人员库 -->
           
              <html:select name="cardTagParamForm" property="userbase" size="1" onchange="javascript:change()">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select>
              <!-- 人员分类 -->
           </td>
           <td align="right" height='28' nowrap><!-- 姓名 -->
            <bean:message key="label.title.name"/>&nbsp;
           </td>
           <td align="left"  nowrap>
             <input type="text" name="select_name" value="${cardTagParamForm.select_name}" size="31" maxlength="31" class="text4">
            </td>
          </tr>
        <%}else{ %>         
           
                <tr>
                  <td align="right" height='28' nowrap><!-- 姓名 -->
                     <bean:message key="label.title.name"/>&nbsp;
                  </td>
                  <td align="left"  nowrap>
                        <input type="text" name="select_name" value="${cardTagParamForm.select_name}" size="31" maxlength="31" class="text4">
                  </td>
                  <%flag=1; %>
         <%} %>       
       <logic:iterate id="element" name="cardTagParamForm"  property="queryfieldlist" indexId="index">            
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
                  <html:text name="cardTagParamForm" property='<%="queryfieldlist["+index+"].value"%>' size="13" maxlength="10" style="width=91px" styleClass="text4" title="输入格式：2008.08.08" onclick=""/>
                  <bean:message key="label.query.to"/>
                  <html:text name="cardTagParamForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" style="width=90px" styleClass="text4" title="输入格式：2008.08.08"  onclick=""/>
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
                  <html:text name="cardTagParamForm" property='<%="queryfieldlist["+index+"].value"%>' size="31" maxlength='<%="queryfieldlist["+index+"].itemlength"%>' styleClass="text4"/>
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
              <html:text name="cardTagParamForm" property='<%="queryfieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="text4"/> 
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
                       <html:hidden name="cardTagParamForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="cardTagParamForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="b0110"> 
                            <%-- <img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("UN","<%="queryfieldlist["+index+"].viewvalue"%>","<%=manager%>",1);' align="absmiddle"/> --%>
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='UN' nmodule='4' ctrltype='3' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="b0110">                                         
                            <%-- <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="queryfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/> --%>
                            <img src="/images/code.gif"  plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='false' align="absmiddle" />
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
                       <html:hidden name="cardTagParamForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="cardTagParamForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="e0122"> 
                            <%-- <img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("UM","<%="queryfieldlist["+index+"].viewvalue"%>","<%=manager%>",1);' align="absmiddle"/> --%>
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='UM' nmodule='4' ctrltype='3' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="e0122">                                         
                            <%-- <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="queryfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/> --%>
                            <img src="/images/code.gif"  plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='false' align="absmiddle" />
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
                       <html:hidden name="cardTagParamForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="cardTagParamForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <%-- <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="queryfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/> --%>
                       <img src="/images/code.gif"  plugin="codeselector" codesetid="${element.codesetid}" nmodule='4' ctrltype='3' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='false' align="absmiddle" />
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
                                  <html:hidden name="cardTagParamForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                                  <html:text name="cardTagParamForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                                  <%-- <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="queryfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/> --%>
                                  <img src="/images/code.gif"  plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='false' align="absmiddle" />
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
        	             	    <html:hidden name="cardTagParamForm" styleId='<%="queryfieldlist["+index+"].value"%>' property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
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
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
              <td align="left"  nowrap>
               <html:text name="cardTagParamForm" property='<%="queryfieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="text4"/>
              </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
            </logic:equal>             
         </logic:equal>
       </logic:iterate>
 		<%
                 if(flag!=0){
        			out.println("<td colspan=\"2\">");
                                      out.println("</td>");
                                      out.println("</tr>");
                                      }
              %> 
    	<tr>
    	  <td align="right" height='20'  nowrap>
    	     <bean:message key="label.query.like"/>&nbsp;
    	  </td>
    	  <td align="left" colspan="3" height='20'  nowrap>
    	    <table width="100%" border="0" cellspacing="0" cellpadding="0" >
    	      <tr>
    	        <td>
    	             <logic:equal name="cardTagParamForm" property="querylike" value="1">
    	              <input type="checkbox" name="querlike2" value="true" onclick="selectCheckBox(this,'querylike');" checked>
    	             </logic:equal>  
    	             <logic:notEqual name="cardTagParamForm" property="querylike" value="1">
    	              <input type="checkbox" name="querlike2" value="true" onclick="selectCheckBox(this,'querylike');">
    	              </logic:notEqual>    	   
    	               <html:hidden name="cardTagParamForm" property='querylike' styleClass="text"/>
    	                &nbsp;&nbsp;
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
    </td>
    </tr>
    <tr>
      <td height="5">
      </td>
    </tr>
    <tr>
    <td align="center">
            <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick="searchinfo('1');" class='mybutton' />  &nbsp;&nbsp; 
    	    <!-- 
    	    <%if(status!=4)
    	    { %>
              <html:button styleClass="mybutton" property="bc_btn1" onclick="selectQ();"><bean:message key="button.sys.cond"/></html:button>&nbsp;&nbsp;
	         <%}else{ %>  
	         	<hrms:priv func_id="2601008,0303014">
    	     		<Input type='button' value="<bean:message key="button.sys.cond"/>" onclick='selectQ();' class='mybutton' />  &nbsp;&nbsp;
    	     	</hrms:priv> 
	         <%} %>
	          -->
	         <Input type='button' value="<bean:message key="button.clear"/>" onclick=' resetQuery();' class='mybutton' />  &nbsp;&nbsp;
  	  </td>
    </tr>
 </table>   
   <!-- 查询结束 --> 
 </td>
</tr>
<tr>
     <td height="5px" align='center'>
 </td>
</tr>
 <tr>
   <td width="100%" nowrap>
   <!--2016/1/5 wangjl加入style解决显示照片前和显示照片返回后显示不一致问题 -->
   <div class="fixedDiv2"  id='fixedDiv'> 
	<table width="100%" border="0" cellspacing="0"  cellpadding="0" class="ListTable">
          <!--<tr>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="cardTagParamForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="cardTagParamForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="cardTagParamForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="cardTagParamForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;   
	    </td> 	     	    	    		        	        	        
           </tr>-->
           <tr class=""><!--update by xiegh on date 20180314  bug35025-->
           <logic:iterate id="info"    name="cardTagParamForm"  property="browsefields">   
              <td align="center" class="TableRow_right" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>
              </td>
          </logic:iterate> 	
          </tr>
          <hrms:paginationdb id="element" name="cardTagParamForm" sql_str="cardTagParamForm.strsql" table="" where_str="cardTagParamForm.cond_str" columns="cardTagParamForm.columns"  order_by="cardTagParamForm.order_by" pagerows="20" page_id="pagination">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" >
          <%}
          else
          {%>
          <tr class="trDeep" >
          <%
          }
          i++;          
          %>  
         <!--     <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />
	    </td>            
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />            
	    </td>
            <td align="left" class="RecordRow" nowrap>
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />     
	    </td>
            <td align="left" class="RecordRow" nowrap>
                <a href="/system/options/cardsalaryshow.do?b_cardshow=link&a0100=<bean:write name="element" property="a0100" filter="true"/>&b0110=<bean:write name="element" property="b0110" filter="true"/>&flag=name&pre=<bean:write name="cardTagParamForm" property="userbase" filter="true"/>"
                target="mil_body"> <bean:write name="element" property="a0101" filter="true"/></a>
	    </td>
	            	    	    	    		        	        	        
          </tr>-->
          <logic:iterate id="info"    name="cardTagParamForm"  property="browsefields">   
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="RecordRow_right" nowrap>        
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow_right" nowrap>        
                  </logic:equal>    
                  <logic:equal  name="info" property="codesetid" value="0">   
                      <logic:notEqual name="info"   property="itemid" value="a0101">        
                   	<bean:write  name="element" property="${info.itemid}" filter="true"/>
                      </logic:notEqual>
                      <logic:equal name="info"   property="itemid" value="a0101"> 
                   <a href="###" onclick="winhref('<bean:write name="element" property="a0100" filter="true"/>','<bean:write name="element" property="b0110" filter="true"/>');"> <bean:write name="element" property="a0101" filter="true"/></a>
                      </logic:equal>
                  </logic:equal>
                  <logic:notEqual  name="info" property="codesetid" value="0">  
                      <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    <bean:write name="codeitem" property="codename" />    
                  </logic:notEqual>
                 
              </td>
             </logic:iterate> 
             </tr>
        </hrms:paginationdb>
        
</table>
</div>
<div id="pageDiv">
<table width="100%"  align="center" class="RecordRowP" border="0" cellspacing="0" cellpadding="0">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="cardTagParamForm" property="pagination" nameId="cardTagParamForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</div>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td height="5px"></td></tr>
          <tr>
            <td  align="center">
            <input type="button" class="mybutton" value="<bean:message key="button.query.viewphoto"/>" onclick="showPhoto()">
            </td>
          </tr>          
</table>
   </td>
 </tr>
 </table>
 <script>
	window.onresize = function(){
		setDivStyle();
	}

	function setDivStyle(){
		document.getElementById("fixedDiv").style.height = document.body.clientHeight-150;
	    document.getElementById("fixedDiv").style.width = document.body.clientWidth-15; 
	    document.getElementById("pageDiv").style.width = document.body.clientWidth-15; 
	}
	setDivStyle();
 </script>
</html:form>
