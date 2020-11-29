<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%@ page import="com.hjsj.hrms.actionform.browse.BrowseForm" %>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<!-- 引入ext框架      wangb 20180208 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  	bosflag=userView.getBosflag();
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	BrowseForm browseForm=(BrowseForm)session.getAttribute("browseForm");
	  String inputchinfor = browseForm.getInputchinfor();
	  String approveflag = browseForm.getApproveflag();
	  String home=(String)session.getAttribute("home");
	  home = home==null?"":home;
	  String crosstabtype=(String)session.getAttribute("crosstabtype");
	  crosstabtype = crosstabtype==null?"":crosstabtype;
	  String subIndex=(String)session.getAttribute("subIndex");
	  subIndex = subIndex==null?"":subIndex;
	  String subStat=(String)session.getAttribute("substat");
	  subStat = subStat==null?"":subStat;
%>

<script language="javascript">
   function turn()
{
   parent.parent.menupnl.toggleCollapse(false);
} 
function exeReturn1(returnStr,target)
{
	turn();
	target_url=returnStr;
	window.open(target_url,target); 
}
function exeReturn(returnStr,target)
{
	target_url=returnStr;
	window.open(target_url,target); 
}
function closePage(){
	var url = "/templates/cclose.jsp";
   	newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
}
function approves(){
   browseForm.action='/workbench/browse/showselfinfodetail.do?b_approve=link&flag=3';
   browseForm.submit();
}
function reject(){
	browseForm.action='/workbench/browse/showselfinfodetail.do?b_reject=link&flag=2';
   browseForm.submit();

}
function change()
{
   browseForm.action='/workbench/browse/showselfinfodetail.do?b_search=link&a0100=${browseForm.a0100}&setname=${browseForm.setname}';
   browseForm.submit();
}
function approveEmp(url,target) {
	browseForm.action='/workbench/browse/appeditselfinfo.do?b_defendother=search&actiontype=update&a0100=${browseForm.a0100}&userbase=${browseForm.userbase}&setname=${browseForm.setname}&isAppEdite=1&isBrowse=1&i9999=i9999&flag=notself';
	browseForm.target=target;
   	browseForm.submit();
}
function multimediahref(dbname,a0100,i9999){
	var thecodeurl =""; 
	var return_vo=null;
	var setname = "${browseForm.setname}";
	var dw=800,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
//  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setname+"&a0100="+a0100+"&nbase="+dbname+"&i9999="+i9999+"&dbflag=A&canedit=false";
  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link`setid="+setname+"`a0100="+a0100+"`nbase="+dbname+"`i9999="+i9999+"`dbflag=A`canedit=false";
  	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
  	if(getBrowseVersion()){
  		return_vo= window.showModalDialog(iframe_url, "", 
  		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
  	}else{//非IE浏览器使用ext 弹窗  wangb 20180208 bug 34710
  		var dialog=[];dialog.dw=dw;dialog.dh=dh;dialog.iframe_url=iframe_url;
  		openWin(dialog);
  	}
  	
}
//兼容非IE浏览器 ext弹窗方法  wangb 20180208 bug 34710
function openWin(dialog){
		    Ext.create("Ext.window.Window",{
		    	id:'showfj',
		    	width:dialog.dw,
		    	height:dialog.dh,
		    	title:'请选择',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+dialog.iframe_url+"'></iframe>"
		    }).show();	
}
//关闭ext弹窗方法  wangb 20180208 bug 34710
function winClose(){
	Ext.getCmp('showfj').close();
}

</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes />
<style>
.notop{
	BORDER-TOP: 0pt solid;
}
</style>
<html:form action="/workbench/browse/showselfinfodetail" >

<%
	int i=0;
String deptDesc = "";
%>
<logic:iterate  id="element"    name="browseForm"  property="infofieldlist" indexId="index"> 
	<logic:equal name="element" property="itemid" value="e0122">
		<hrms:codetoname codeid="UM" name="element" codevalue="viewvalue" codeitem="codeitem" scope="page" uplevel="${browseForm.uplevel}"/>
		<logic:notEmpty name="codeitem" property="codename">  	
			<bean:define id="e0122Value" name="codeitem" property="codename"></bean:define>
			<%deptDesc= e0122Value.toString();%>
		</logic:notEmpty>
	</logic:equal>
</logic:iterate>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
	<tr>
    	<td align="left"  nowrap>
            <logic:equal name="browseForm" property="infosort" value="1">
	            <logic:notEmpty name="browseForm" property="sortSetlist">
	            	<html:select name="browseForm" property="sortname" size="1" onchange="change();">                 
	            		<html:optionsCollection property="sortSetlist" value="dataName" label="dataName"/>	        
	                </html:select>  
	            </logic:notEmpty>
            </logic:equal>
            (
            <logic:notEmpty name="browseForm" property="b0110">
            	<font style="margin-right: 5px;"><bean:message key="label.title.org"/>: <bean:write  name="browseForm" property="b0110" filter="true"/></font>
            </logic:notEmpty>
            <%if(StringUtils.isNotEmpty(deptDesc)){ %>
            <font style="margin-right: 5px;"><bean:message key="label.title.dept"/>: <%=deptDesc %></font>
			<%} %>
            <bean:message key="label.title.name"/>: <bean:write  name="browseForm" property="a0101" filter="true"/>
        	)
    	</td>
   	</tr>
</table>
<% 
	String TableRowClass = "TableRow_right notop"; 
	String RecordRowClass = "RecordRow_right";
%>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">

   <tr>
     <td style="padding-left: 5px;">
<div id="dataBox" class="fixedDiv2" >
       <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
          <thead>
           <tr>
           <!-- 在信息浏览页面没有找到使用复选框的代码，暂时去掉
            <logic:equal name="browseForm" property="isUserEmploy" value="0">
             <td align="center" class="TableRow_right notop" nowrap>
              <bean:message key="column.select"/>
             </td> 
             </logic:equal> 
              -->
             <logic:equal value="1" name="browseForm" property="approveflag">
             <hrms:priv func_id="070904">
              <logic:equal name="browseForm" property="isUserEmploy" value="0">
               <td align="center" class="TableRow_right notop" nowrap>
                   <bean:message key="info.appleal.statedesc"/>
               </td>
              </logic:equal>
             </hrms:priv>
             </logic:equal>
             <logic:iterate id="element"    name="browseForm"  property="infodetailfieldlist" indexId="index"> 
             <logic:equal name="index" value="0">
	           <% TableRowClass = "TableRow_right notop"; %>
             </logic:equal>
             <logic:notEqual name="index" value="0">
	           <% TableRowClass = "TableRow_left notop"; %>
             </logic:notEqual>
              <td align="center" height="22" class="<%=TableRowClass %>" nowrap>
                 <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
              </td>
             </logic:iterate> 
             <logic:equal name="browseForm" property="multimedia_file_flag" value="1">
	 		      <td align="center" class="TableRow_left notop" nowrap>
					<bean:message key="conlumn.resource_list.name"/>             	
				  </td> 
			  </logic:equal>        	        
           </tr>
   	  </thead>
   	 
          <hrms:extenditerate id="element" name="browseForm" property="browseForm.list" indexes="indexes"  pagination="browseForm.pagination" pageCount="${browseForm.num_per_page}" scope="session">
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
          RecordVo vo=(RecordVo)element;   
          pageContext.setAttribute("i9999", vo.getString("i9999"));
          %>  
          <!-- 在信息浏览页面没有找到使用复选框的代码，暂时去掉
           <logic:equal name="browseForm" property="isUserEmploy" value="0">
            <td align="center" class="RecordRow_right" nowrap>
               <logic:notEqual name="element" property="string(state)" value="0">
                <hrms:checkmultibox name="browseForm" property="browseForm.select" value="true" indexes="indexes"/>
               </logic:notEqual>
              </td>
            </logic:equal>
            -->
             <logic:equal value="1" name="browseForm" property="approveflag">
             <hrms:priv func_id="070904">
            <logic:equal name="browseForm" property="isUserEmploy" value="0">
             <td align="center" class="RecordRow_right" nowrap>
               <logic:equal name="element" property="string(state)" value="0">
                  <bean:message key="info.appleal.state0"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="1">
                  <bean:message key="info.appleal.state1"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="2">
                  <bean:message key="info.appleal.state2"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="3">
                  <bean:message key="info.appleal.state3"/>
               </logic:equal>
               <logic:notEqual name="element" property="string(state)" value="0">
                 <logic:notEqual name="element" property="string(state)" value="1">
                   <logic:notEqual name="element" property="string(state)" value="2">
                      <logic:notEqual name="element" property="string(state)" value="3">
                        <bean:message key="info.appleal.state3"/>
                      </logic:notEqual>
                    </logic:notEqual>
                  </logic:notEqual>
               </logic:notEqual>
            </td>
            </logic:equal>
            </hrms:priv>
            </logic:equal>
             <logic:iterate id="info"    name="browseForm"  property="infodetailfieldlist" indexId="index"> 
             <logic:equal name="index" value="0">
	           <% RecordRowClass = "RecordRow_right"; %>
             </logic:equal>
             <logic:notEqual name="index" value="0">
	           <% RecordRowClass = "RecordRow_left"; %>
             </logic:notEqual>  
             <logic:notEqual  name="info" property="itemtype" value="N">
             <logic:notEqual  name="info" property="itemtype" value="M">               
               <td align="left" class="<%=RecordRowClass %>" nowrap>    
               <bean:write  name="element" property="string(${info.itemid})" filter="true"/> 
               </td>   
             </logic:notEqual>
              </logic:notEqual>
              <logic:equal  name="info" property="itemtype" value="N">               
                <td align="right" class="<%=RecordRowClass %>" nowrap> 
                    <bean:write  name="element" property="string(${info.itemid})" filter="true"/> 
               </td>    
              </logic:equal>           
              <logic:equal  name="info" property="itemtype" value="M">  
               <%
                 FieldItem item=(FieldItem)pageContext.getAttribute("info");
                 String tx=vo.getString(item.getItemid());
               %> 
               <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${browseForm.setname}" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>             
                <td class="<%=RecordRowClass %>" ${tiptext} nowrap> 
                    ${showtext}
               </td>    
              </logic:equal> 
             </logic:iterate>
             <logic:equal name="browseForm" property="multimedia_file_flag" value="1">
             	<td align="center" class="RecordRow_left" nowrap>
             		<input type="hidden" value="<bean:write  name="element" property="string(i9999)" filter="true"/>"/>
            		<hrms:browseaffix pertain_to="record" a0100="${browseForm.a0100}" nbase="${browseForm.userbase}" i9999="${i9999}" setId="${browseForm.setname}"></hrms:browseaffix>
	      		</td>
	      	</logic:equal>             	                           	    		        	        	        
          </tr>
        </hrms:extenditerate>
       </table>
       </div>
     </td>
   </tr>
   	 <tr><td style="padding-left: 5px;"> 
   	 <div id="pageDiv" style="border:0;height:35;" class="fixedDiv2" >
		<table align="left" class="RecordRowP" width="100%">
				<tr>
						<td valign="bottom" class="tdFontcolor" >
				            <bean:message key="label.page.serial"/>
							<bean:write name="browseForm" property="browseForm.pagination.current" filter="true" />
							<bean:message key="label.page.sum"/>
							<bean:write name="browseForm" property="browseForm.pagination.count" filter="true" />
							<bean:message key="label.page.row"/>
							<bean:write name="browseForm" property="browseForm.pagination.pages" filter="true" />
							<bean:message key="label.page.page"/>
					</td>
			               <td  align="right" nowrap class="tdFontcolor">
				          <p align="right"><hrms:paginationlink name="browseForm" property="browseForm.pagination"
						nameId="browseForm" propertyId="roleListProperty">
						</hrms:paginationlink></td>			
				</tr>
		</table>
		</div>
     </td></tr>    
</table>

<table width="80%" border="0" cellspacing="0" cellpadding="0">
  <tr><td height="5px"></td></tr>
  <tr>
   <td align="left" style="padding-left: 5px;">
      <logic:notEqual name="browseForm" property="flag" value="infoself">      
     <logic:equal name="browseForm" property="returnvalue" value="1">
       <logic:equal name="browseForm" property="isUserEmploy" value="0">
          <logic:equal value="1" name="browseForm" property="approveflag">
           <logic:notEqual value="1" name="browseForm" property="inputchinfor">
              <logic:notEqual value="3" name="browseForm" property="state">
                  <hrms:submit styleClass="mybutton"  property="b_approve" function_id="03082">
                  <bean:message key="info.appleal.state3"/>
                 </hrms:submit> 
               </logic:notEqual>
               <logic:notEqual value="2" name="browseForm" property="state">
                   <hrms:submit styleClass="mybutton"  property="b_reject" function_id="03081">
                   <bean:message key="info.appleal.state2"/>
                   </hrms:submit> 
                </logic:notEqual>
             </logic:notEqual>
       </logic:equal>
     </logic:equal>
     		<logic:equal value="1" name="browseForm" property="approveflag">
     		<%if(inputchinfor.equals("1")) {%>
     				<logic:notEqual value="${browseForm.setname }" name="browseForm" property="virAxx">
			     		<hrms:priv func_id="260112">
			        	<input type="button" class="mybutton" name="approve" value="<bean:message key="selfinfo.defend"/>" onclick="approveEmp('<bean:write name="browseForm" property="a0100"/>','mil_body');" />
			        	</hrms:priv>
			        </logic:notEqual>
        	<%} %>
        	</logic:equal>
        	<logic:equal value="1" name="browseForm" property="fromphoto">
        		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showinfodata.do?b_view_photo=link&code=${browseForm.code}&kind=${browseForm.kind}','nil_body')">
        	</logic:equal>
        	<logic:notEqual value="1" name="browseForm" property="fromphoto">
	        <%if(bosflag.equalsIgnoreCase("bi")){ %>
	        	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showinfo.do','i_body')">
	        <%}else{ %>
	        	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showinfo.do','il_body')">
	        <%} %>
           </logic:notEqual>                 
      </logic:equal>  
      <logic:equal name="browseForm" property="returnvalue" value="11">
      <logic:equal name="browseForm" property="isUserEmploy" value="0">
      <logic:equal value="1" name="browseForm" property="approveflag">
       <logic:notEqual value="3" name="browseForm" property="state">
        <!--<hrms:submit styleClass="mybutton"  property="b_approve" function_id="03082">
          <bean:message key="info.appleal.state3"/>
        </hrms:submit> -->
        </logic:notEqual>
        <logic:notEqual value="2" name="browseForm" property="state">
        <!--<<hrms:submit styleClass="mybutton"  property="b_reject" function_id="03081">
          <bean:message key="info.appleal.state2"/>
         </hrms:submit>--> 
       </logic:notEqual>
       </logic:equal>
     </logic:equal>  
         <%if(bosflag!=null&&bosflag.equals("hl")){ %>  
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_show_photo=browse','nil_body')">                    
         <%}else{%>
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_show_photo=browse','il_body')"> 
         <%} %>
      </logic:equal>  
      <logic:equal name="browseForm" property="returnvalue" value="191">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/query/query_interface1_photo.do?b_view_photo=link','il_body')">                   
      </logic:equal> 
      <logic:equal name="browseForm" property="returnvalue" value="190">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/query/query_interface1.do','il_body')">                   
      </logic:equal> 
       <logic:equal name="browseForm" property="returnvalue" value="188"><!-- 人员历史时点 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn1('/workbench/browse/history/showinfodata.do?b_search=link','nil_body')">                   
      </logic:equal>
      <logic:equal name="browseForm" property="returnvalue" value="189"><!-- 领导班子 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/info/leader/showinfodata.do?b_leader=link','nil_body')">                   
      </logic:equal>  
      <logic:equal name="browseForm" property="returnvalue" value="dxt">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnshow=browse','nil_body')">                   
      </logic:equal> 
        <logic:equal name="browseForm" property="returnvalue" value="100000">
          <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="parent.top.close();"><!--bug号：38158 -->
       </logic:equal>
       <logic:equal name="browseForm" property="returnvalue" value="100001">
          <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="parent.close();">
       </logic:equal>
       <logic:equal name="browseForm" property="returnvalue" value="111">
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_show_photo=browse','nil_body')">
       </logic:equal>
       <logic:equal name="browseForm" property="returnvalue" value="2">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/stat/showstatinfodata.do?b_returnstat=stat','il_body')">                 
      </logic:equal>  
       <logic:equal name="browseForm" property="returnvalue" value="22">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/stat/showstatinfodata.do?b_stat_photo=stat','il_body')">                 
      </logic:equal>  
        <logic:equal name="browseForm" property="returnvalue" value="3">     
        <%if(bosflag!=null&&bosflag.equals("ul")){ %>  
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnquery=query','il_body')">                 
        <%}else{%>
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnquery=query','il_body')">
       <%} %>
       </logic:equal>  
       <logic:equal name="browseForm" property="returnvalue" value="333">     
        <%if(bosflag!=null&&bosflag.equals("ul")){ %>  
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnquery=query','i_body')">                 
        <%}else{%>
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnquery=query','il_body')">
       <%} %>
       </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="33">
         <%if(bosflag!=null&&bosflag.equals("ul")){ %>  
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_query_photo=query','i_body')">
         <%}else{%>
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_query_photo=query','il_body')">                 
         <%} %>
        </logic:equal> 
        <logic:equal name="browseForm" property="returnvalue" value="6">
        <%if(bosflag!=null&&bosflag.equals("hl")){ %> 
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnquery=query','il_body')">                 
         <%}else{%>
            <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnquery=query','i_body')">                 
         <%} %>
       </logic:equal>  
              <logic:equal name="browseForm" property="returnvalue" value="46">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnquery=query','il_body')">                 
       </logic:equal> 
        <logic:equal name="browseForm" property="returnvalue" value="Y"><!-- 党组织人员管理 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/dtgh/party/person/searchbusinesslist.do?b_search=link&politics=${browseForm.politics }&param=Y&a_code=${browseForm.a_code }','_parent')">                 
      </logic:equal> 
      <logic:equal name="browseForm" property="returnvalue" value="V"><!-- 团组织人员管理 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/dtgh/party/person/searchbusinesslist.do?b_search=link&politics=${browseForm.politics }&param=V&a_code=${browseForm.a_code }','_parent')">                 
      </logic:equal> 
      <logic:equal name="browseForm" property="returnvalue" value="bi"><!-- 总裁桌面的统计图 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_data=data&showflag=1','i_body')">                 
       </logic:equal>  
        <logic:equal name="browseForm" property="returnvalue" value="66">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_query_photo=query','i_body')">                 
        </logic:equal> 
         <logic:equal name="browseForm" property="returnvalue" value="64">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_query_photo=query','il_body')">                 
        </logic:equal> 
         <logic:equal name="browseForm" property="returnvalue" value="4">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnhquery=query','il_body')">                 
       </logic:equal>  
        <logic:equal name="browseForm" property="returnvalue" value="44">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_hquery_photo=query','il_body')">                 
        </logic:equal> 
        <logic:equal name="browseForm" property="returnvalue" value="simple5">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/static_data.do?b_data=data','il_body')">                 
        </logic:equal> 
        <logic:equal name="browseForm" property="returnvalue" value="5">
          <% if("".equals(crosstabtype)){ %>
           	<%if("6".equals(home)){ %>
	          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_returnstat1=stat&home=6','il_body')">
	        <%}else{ %>
	          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_returnstat=stat','il_body')">
	       	<%} %>
       	  <%}else{ %>
           <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="javascript:closePage()">            
	      <%} %>                 
       </logic:equal>  
       <logic:equal name="browseForm" property="returnvalue" value="ht">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/history/statshow.do?b_data=stat','mmil_body')">                 
       </logic:equal> 
       <logic:equal name="browseForm" property="returnvalue" value="statnum">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/org/orgpre/showstatnum.do?b_showstatnum=link','mmil_body')">                 
       </logic:equal> 
        <logic:equal name="browseForm" property="returnvalue" value="55">
          <% if("".equals(crosstabtype)){ %>
	          <%if("6".equals(home)){ %>
	           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_view_photo=link','il_body')"> 
	          <%}else{ %>
	           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','il_body')"> 
	          <%} %>
       	  <%}else{ %>
           <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="javascript:closePage()">            
	      <%} %>
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="fx66">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_view_photo=stat','i_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="34">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/two_dim_result.do?b_result=stat&result=${browseForm.result}','il_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="8">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_returnstat1=stat','i_body')">                 
       </logic:equal>  
        <logic:equal name="browseForm" property="returnvalue" value="81">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_returnstat1=stat','il_body')">                 
       </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="88">
           <!-- 标识：1758 点主页常用统计下的一项，依次点下去，点到照片显示出人员信息后，侧栏没了，依次返回，返回不到主页并且会弹出一个框 xiaoyun 2014-7-10 start -->
           <%if(bosflag!=null&&bosflag.equals("hcm")) {%>
            <!-- 标识：3156 主页：常用统计/学历分布，点人员照片信息再返回，左侧按钮不可用，显示缺少东西 xiaoyun 2014-7-16 start -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_view_photo=link','il_body')">
           <!--
           	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','il_body')">
            -->
            <!-- 标识：3156 主页：常用统计/学历分布，点人员照片信息再返回，左侧按钮不可用，显示缺少东西 xiaoyun 2014-7-16 end -->
           <%}else{ %>
    	        <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','i_body')">                 
           <%} %>
       	   <!-- 标识：1758 点主页常用统计下的一项，依次点下去，点到照片显示出人员信息后，侧栏没了，依次返回，返回不到主页并且会弹出一个框 xiaoyun 2014-7-10 end -->
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="881">
           <!--  <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','il_body')">-->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/view_photo.do','il_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="82">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_double=link','i_body')">                 
        </logic:equal> 
        <logic:equal name="browseForm" property="returnvalue" value="821">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_view_photo=link&home=2','i_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="68">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_data=data','i_body')">                 
        </logic:equal> 
        <logic:equal name="browseForm" property="returnvalue" value="681">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_view_photo=link&home=6','i_body')">                 
        </logic:equal>
       
       <logic:equal name="browseForm" property="returnvalue" value="14">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/query/complex_interface.do?b_search=link&currentpage=yes','il_body')">                 
        </logic:equal>
      <logic:equal name="browseForm" property="returnvalue" value="dddd">
           <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="window.parent.close()">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="tow">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/two_dim_show.do?b_photo=link&result=${browseForm.result}','il_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="74">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','il_body')">                 
        </logic:equal>
         <logic:equal name="browseForm" property="returnvalue" value="75">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','il_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="scan">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/scaninfodata.do?b_query=link','nil_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="scanp">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/scan_photo.do?br_return=link','nil_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="73">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','i_body')">                 
        </logic:equal>
         <logic:equal name="browseForm" property="returnvalue" value="73">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','i_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="complex_p">
        <%if(bosflag!=null&&bosflag.equals("hl")){ %>  
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/query/complex_interface_pho.do?b_query=link','il_body')">                 
        <%}else{%>
            <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/query/complex_interface_pho.do?b_query=link','il_body')">
        <%} %>
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="complex">
          <%if(bosflag!=null&&bosflag.equals("hl")){ %>  
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/query/complex_interface.do?b_search=link&currentpage=yes','il_body')">                 
          <%}else{%>
            <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/query/complex_interface.do?b_search=link&currentpage=yes','il_body')">
          <%} %>
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="jgfx">
          <% if("".equals(crosstabtype)){ %>
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_returnstat1=stat&flag=jgfx','nil_body')">                 
       	  <%}else{ %>
           <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="javascript:closePage()">            
	      <%} %>
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="jgfx_p">
           <% if("".equals(crosstabtype)){ %>
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_view_photo=stat&flag=jgfx','nil_body')">                 
       	  <%}else{ %>
           <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="javascript:closePage()">            
	      <%} %>
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="jgfx_double">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/deci/statics/employmakeupanalyse.do?b_double=link','nil_body')">                 
        </logic:equal>
      </logic:notEqual>
      <logic:equal name="browseForm" property="returnvalue" value="nuclear">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showinfodatanuclear.do?b_search=link','nil_body')">                 
        </logic:equal>
      <logic:equal name="browseForm" property="returnvalue" value="train_no_post"><!-- 培训不符合本岗位的 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/train/postAnalyse/notaccordpost.do?b_search=link','il_body')">                 
      </logic:equal>
      <logic:equal name="browseForm" property="returnvalue" value="train_post"><!-- 培训不符合本岗位的 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/train/postAnalyse/accordpost.do?b_search=link','il_body')">                 
      </logic:equal>
   </td>
  </tr>
 </table> 
</html:form>
<script>
window.onresize = function(){
	setDivStyle();
}

function setDivStyle(){
	document.getElementById("dataBox").style.height = document.body.clientHeight-130;
    if(!getBrowseVersion()) {
    	document.getElementById("dataBox").style.width = document.body.clientWidth-15; 
    	document.getElementById("pageDiv").style.width = document.body.clientWidth-12; 
    } else if(!isCompatibleIE()) {
    	document.getElementById("dataBox").style.width = document.body.clientWidth-10; 
    	document.getElementById("pageDiv").style.width = document.body.clientWidth-24;
    }
}

setDivStyle();
</script>
