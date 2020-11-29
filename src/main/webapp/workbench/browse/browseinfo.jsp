<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.browse.BrowseForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%@ page import="com.hrms.frame.codec.SafeCode" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap,com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo" %> 
<%
     // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	String themes="default";
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  	bosflag=userView.getBosflag();
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");   
	  	/*xuj added at 2014-4-18 for hcm themes*/
	      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	}
	
	String home=(String)session.getAttribute("home");
	home = home==null?"":home;
	BrowseForm browseForm=(BrowseForm)session.getAttribute("browseForm");
	String crosstabtype=(String)session.getAttribute("crosstabtype");
	crosstabtype = crosstabtype==null?"":crosstabtype;
	String subIndex=(String)session.getAttribute("subIndex");
	subIndex = subIndex==null?"":subIndex;
	String subStat=(String)session.getAttribute("substat");
	subStat = subStat==null?"":subStat;
	
%>
<%
	int i=0;
	int flag=0;
%>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="javascript">
   function turn()
{
   parent.parent.menupnl.toggleCollapse(false);
} 
function exeReturn1(returnStr,target)
{
	turn();
	target_url=returnStr;
	top.open(target_url,target); 
}
function exeReturn(returnStr,target)
{
	if("${browseForm.returnvalue}" == 'bi'){//判断进入入口是从领导桌面进入，直接返回到首页 bug 37350 wangb 20180703
		window.history.go(-1);
	}else{
		target_url=returnStr;
		top.open(target_url,target); 
	}
}
function closePage(){
	//修改原因：浏览器兼容修改后，此段代码不能关闭弹出的窗口
	parent.top.close();
//	var url = "/templates/cclose.jsp";
//   	newwin=top.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
}
function openwin(url)
{
	url = url.replace("returnvalue=scan","returnvalue=no");//从组织机构进入，弹出新窗口不要返回按钮，过滤returnvalue 2014-07-09 guodd
	//使用iframe 嵌套显示 ` 符号不能转& wangb 20180207 bug 34636
//	while(url.indexOf("`")!=-1){
//		url = url.replace("`","&");
//	}
   var iframe_url = "/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
   top.open(iframe_url,"_blank","left=0,top=0,width="+(screen.availWidth-10)+",height="+(screen.availHeight-80)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
   
   //browseForm.action=url;
   //browseForm.target="_blank";
   //browseForm.submit();
    //var iframe_url="/general/query/common/iframe_query.jsp?src="+url;
       /*operuser中用户名*/
	   //newtop=top.open(target_url,'app','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=180,left=350,width=530,height=500');  	
    //var return_vo= top.showModalDialog(iframe_url,"app", 
    //   "dialogWidth:"+screen.availWidth+"; dialogHeight:"+screen.availHeight+";resizable:no;center:yes;scroll:yes;status:no");
}
//  document.oncontextmenu = function() {return false;}
   
 function approveEmp(a0100,target)
{
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
   browseForm.action="/workbench/browse/appeditselfinfo.do?b_edit=edit&actiontype=update&setname=A01&isAppEdite=1&isBrowse=1&flag=${browseForm.flag}";
   browseForm.target=target;
   browseForm.submit();
}
function approveall() {
if(confirm("您确定要整体报批吗？整体报批后将不能修改！")){	
		//selfInfoForm.action="/selfservice/selfinfo/inforchange.do?b_query=link&savEdit=baopi&chg_id="+chg_id;
		browseForm.action= "/workbench/browse/appeditselfinfo.do?b_prove=link&isAppEdite=1&savEdit=appbaopi&a0100=${browseForm.a0100}&userbase=${browseForm.userbase}&flag=${browseForm.flag}";
		browseForm.target = "mil_body";	 
   		browseForm.submit();
   	}
}
//tianye add 20130617 下载照片
function downLoadPhoto(){
	window.open("/servlet/vfsservlet?fileid=${browseForm.photoId}");
}

</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<!-- 
<%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  <%} %>
 -->
<hrms:themes />
<style>
.AddTableRow {
	height:22px;
	valign:middle;
	padding:0 5px 0 5px;
}
.textColorWrite{
	align:middle;
}
</style>
<html:form action="/workbench/browse/showselfinfo">
<% 
	String inputchinfor = browseForm.getInputchinfor();
	  String approveflag = browseForm.getApproveflag();   
if(browseForm.getInfofieldlist().size()>0){%>   
<table width="98%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable3">
 <html:hidden name="browseForm" property="userbase"/> 
 <html:hidden name="browseForm" property="a0100"/>  
 <bean:define id="a0100" name="browseForm" property="a0100"/>
 <% 
 	String a0100tran = "~" + PubFunc.convertTo64Base(a0100.toString());
 	String a0100_encrypt=PubFunc.encrypt(a0100.toString());
 %>
<logic:equal name="browseForm" property="mainsort" value="1">
<%
 List subsort_list=(List)browseForm.getSubsort_list();
 HashMap infoMap=(HashMap)browseForm.getInfoMap();
%>	      
 <hrms:browseinfosort userbase="${browseForm.userbase}" a0100="<%=a0100tran%>" subsort_list="<%=subsort_list %>" infoMap="<%=infoMap%>" userpriv="${browseForm.userpriv}" uplevel="${browseForm.uplevel}"  prv_flag="${browseForm.flag}" returnvalue="${browseForm.returnvalue}"/>
</logic:equal>
<logic:notEqual name="browseForm" property="mainsort" value="1">
<logic:iterate  id="element"    name="browseForm"  property="infofieldlist" indexId="index"> 
    <logic:equal name="element" property="visible" value="true">     
        <logic:equal name="element" property="itemtype" value="D">
         <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
         <td align="right" nowrap valign="middle" class="AddTableRow">        
            <bean:write  name="element" property="itemdesc"/>              
         </td>
         <td align="left" nowrap valign="middle" class="AddTableRow">           
            <html:text name="element" property='fieldvalue' readonly="true" styleClass="textColorWrite"/>
         </td> 
        <%if(flag==0){%>           
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2" class="AddTableRow">
               </td>
               </tr>
            </logic:equal>
        <%}%>        
       </logic:equal>
        <logic:equal name="element" property="itemtype" value="A">
          <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
         <td align="right" nowrap valign="middle" class="AddTableRow">        
             <bean:write  name="element" property="itemdesc"/>      
         </td>
         <td align="left" nowrap valign="middle" class="AddTableRow">
         <logic:equal name="element" property="itemid" value="a0101">            
           <html:text  name="element" property="fieldvalue" readonly="true" styleClass="textColorWrite"/>
            <logic:notEmpty name="element" property="fieldvalue">              
              <a href="###" onclick='openwin("/general/inform/synthesisbrowse/mycard.do?b_mysearch=link`userbase=${browseForm.userbase}`a0100=<%=a0100_encrypt%>`multi_cards=-1`inforkind=1`npage=1`userpriv=${browseForm.userpriv}`flick=1`flag=${browseForm.flag}");'>
               <img src="/images/view.gif" border=0 alt="1" title="员工登记表" align="middle">
              </a>  
             </logic:notEmpty>           
          </logic:equal>  
          <logic:equal name="element" property="itemid" value="e01a1">
           <html:text name="element" property="fieldvalue" readonly="true" styleClass="textColorWrite"/>
            <logic:notEmpty name="element" property="fieldvalue">
               <!-- liuy 2015-3-24 8203：高级花名册，点姓名进去后，浏览人员主集，岗位名称后面的图标，点击后URL中的库及A0100未加密，不安全。 -->
               <bean:define id="viewvalue" name="element" property="viewvalue"/>
               <% String a0100tran1 =PubFunc.encrypt(viewvalue.toString()); /* "~" + SafeCode.encode(PubFunc.convertTo64Base(viewvalue.toString())); */ %>
              <a href="###" onclick='openwin("/workbench/browse/showposinfo.do?b_browse=link`userbase=${browseForm.userbase}`a0100=<%=a0100tran1%>`npage=1`infokind=4`flag=${browseForm.flag}");'>
               <img src="/images/view.gif" border=0 alt="2" title="岗位说明书" align="middle">
              </a>
            </logic:notEmpty>
            <logic:equal name="browseForm" property="ps_card_attach" value="true">
             <hrms:browseaffix pertain_to="post" a0100="${browseForm.a0100}" nbase="${browseForm.userbase}"></hrms:browseaffix>  
            </logic:equal>
          </logic:equal> 
          <logic:equal name="element" property="itemid" value="e0122">
           <hrms:codetoname codeid="UM" name="element" codevalue="viewvalue" codeitem="codeitem" scope="page" uplevel="${browseForm.uplevel}"/>  
           <input type="text" readonly="readonly" value="${codeitem.codename }" class="textColorWrite"/>  
           <hrms:priv func_id="23011">	
           <logic:notEmpty name="element" property="fieldvalue">
              <a href="###" onclick='openwin("/general/inform/org/searchorgbrowse.do?b_search=link`code=${element.viewvalue}`kind=1`orgtype=org`returnvalue=${browseForm.returnvalue}");'>
               <img src="/images/view.gif" border=0 align="middle">
              </a>
            </logic:notEmpty>  
            </hrms:priv>
           <!-- <html:text  name="element" property="fieldvalue" readonly="true" title="${codeitem.codename }" styleClass="textColorWrite"/>	  --> 
          </logic:equal>
          <logic:notEqual name="element" property="itemid" value="e0122">  
          <logic:notEqual name="element" property="itemid" value="a0101"> 
            <logic:notEqual name="element" property="itemid" value="e01a1"> 
            <html:text name="element" property="fieldvalue" readonly="true" styleClass="textColorWrite"/>
            </logic:notEqual> 
          </logic:notEqual>
          </logic:notEqual> 
         </td>
         <%if(flag==0){%>
           </tr>
       <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2" class="AddTableRow">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
       </logic:equal>
        <logic:equal name="element" property="itemtype" value="N">
         <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
           <td align="right" nowrap valign="middle" class="AddTableRow">        
               <bean:write  name="element" property="itemdesc"/>             
           </td>
           <td align="left" nowrap valign="middle" class="AddTableRow">              
            <html:text  name="element" property="fieldvalue" readonly="true" styleClass="textColorWrite"/>
           </td>
         <%if(flag==0){%>
           </tr>
         <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2" class="AddTableRow">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
       </logic:equal>
        <logic:equal name="element" property="itemtype" value="M">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;%>
                 <td align="right" nowrap valign="middle" class="AddTableRow">        
                    <bean:write  name="element" property="itemdesc"/>            
                 </td>
                 <td align="left" valign="middle" colspan="3" class="AddTableRow">  
                 	<!-- style="width:550px;height:100px;" 员工管理模块，文本域样式 jingq add 2014.10.21 -->                
                     <html:textarea name="element" property='fieldvalue' readonly="true" rows="10" cols="66" style="width:550px;height:100px;" styleClass="textColorWrite"/>
                 </td> 
             <%       
             }else{
               flag=0;%>               
              <td colspan="2" class="AddTableRow">
              </td>
              </tr>
               
             <%
            if(flag==0){
              if(i%2==0){
             %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>               
                <td align="right" nowrap valign="middle" class="AddTableRow">        
                   <bean:write  name="element" property="itemdesc"/>          
                </td>
                <td align="left" valign="middle" colspan="3" class="AddTableRow">
                  <html:textarea name="element" property='fieldvalue' readonly="true" rows="10" cols="66" style="width:550px;height:100px;" styleClass="textColorWrite"/>
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
       </logic:equal>        
     </logic:equal>
   </logic:iterate>
</logic:notEqual>

</table>


 <table width="98%" border="0" align="center">

  <tr>
   <td align="center">
   <logic:notEmpty name="browseForm" property="photoId">
   <logic:equal name="browseForm" property="isUserEmploy" value="0"> 
     <logic:equal  name="browseForm" property="a0100" value="<%=userView.getA0100()%>">
       <input type="button" name="returnbutton"  value="下载照片" class="mybutton" onclick="downLoadPhoto()"> 
     </logic:equal>
     </logic:equal>
     <logic:equal name="browseForm" property="isUserEmploy" value="1"> 
     <input type="button" name="returnbutton"  value="下载照片" class="mybutton" onclick="downLoadPhoto()"> 
     </logic:equal>
    </logic:notEmpty>
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
        	<!--员工管理》查询浏览》信息浏览进入    为了保留上页查询状态 链接地址去掉 .do 后面的 b_search=link，  guodd 2014-10-20   -->
           <%if(bosflag.equalsIgnoreCase("bi")){ %>
           	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showinfo.do','i_body')">
           <%}else{ %>
           	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showinfodata.do?b_query=link','nil_body')">
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
          <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="parent.top.close();"> <%--关闭弹窗方法改为 parent.top.close() bug 34351 wangb 20180131 --%>
       </logic:equal>
       <logic:equal name="browseForm" property="returnvalue" value="100001">
          <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="parent.top.close();"> <%--关闭弹窗方法改为 parent.top.close() bug 34351 wangb 20180131 --%>
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
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/dtgh/party/person/searchbusinesslist.do?b_search=link&politics=${browseForm.politics }&param=Y&a_code=${browseForm.a_code }','nil_body')">                 
      </logic:equal> 
      <logic:equal name="browseForm" property="returnvalue" value="V"><!-- 团组织人员管理 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/dtgh/party/person/searchbusinesslist.do?b_search=link&politics=${browseForm.politics }&param=V&a_code=${browseForm.a_code }','nil_body')">                 
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
           <!-- 标识：1758 点主页常用统计下的一项，依次点下去，点到照片显示出人员信息后，侧栏没了，依次返回，返回不到主页并且会弹出一个框 xiaoyun 2014-7-3 start -->
            <%if(bosflag!=null&&bosflag.equals("hcm")){%>
            <!-- 标识：3156 主页：常用统计/学历分布，点人员照片信息再返回，左侧按钮不可用，显示缺少东西 xiaoyun 2014-7-16 start -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_view_photo=link','il_body')">
           <!--
            <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','il_body')">                 
            -->
            <!-- 标识：3156 主页：常用统计/学历分布，点人员照片信息再返回，左侧按钮不可用，显示缺少东西 xiaoyun 2014-7-16 end -->
           <%}else{%>
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','i_body')">
           <%}%>
           <!-- 标识：1758 点主页常用统计下的一项，依次点下去，点到照片显示出人员信息后，侧栏没了，依次返回，返回不到主页并且会弹出一个框 xiaoyun 2014-7-3 end -->
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
           <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="parent.top.close()"> <%--关闭弹窗方法改为 parent.top.close() bug 34351 wangb 20180131 --%>                
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
      <logic:equal name="browseForm" property="returnvalue" value="train_post"><!-- 培训符合本岗位的 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/train/postAnalyse/accordpost.do?b_search=link','il_body')">                 
      </logic:equal>
      <logic:equal name="browseForm" property="returnvalue" value="train_no_post_lesson"><!-- 培训报表分析-->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/train/postAnalyse/notaccordpostwork.do?b_searchlesson=link','il_body')">                 
      </logic:equal>
      <logic:equal name="browseForm" property="returnvalue" value="returnphoto"><!-- 人员机构分析二维统计显示照片 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/deci/statics/employmakeupanalyse.do?br_photo=link','nil_body')">                 
        </logic:equal>
   </td>
  </tr>
 </table> 

<%}else{%>

 <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
 <tr>  
     <td align="center"  nowrap>
         <bean:message key="workbench.info.nomainfield"/>
     </td>
     <td align="center">
     <logic:equal name="browseForm" property="returnvalue" value="1">
     	<%if(bosflag.equalsIgnoreCase("bi")){ %>
           	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnbrowse=browse','i_body')">
        <%}else{ %>
           	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnbrowse=browse','il_body')">
        <%} %>
      </logic:equal> 
      <logic:equal name="browseForm" property="returnvalue" value="dxt">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnbrowse=browse','il_body')">                 
      </logic:equal> 
       <logic:equal name="browseForm" property="returnvalue" value="100000">
          <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="parent.top.close();"> <%--关闭弹窗方法改为 parent.top.close() bug 34351 wangb 20180131 --%>
       </logic:equal>
      <logic:equal name="browseForm" property="returnvalue" value="11">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_browse_photo=browse','i_body')">                    
      </logic:equal>  
       <logic:equal name="browseForm" property="returnvalue" value="2">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/stat/showstatinfodata.do?b_returnstat=stat','il_body')">                 
      </logic:equal>  
       <logic:equal name="browseForm" property="returnvalue" value="22">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/stat/showstatinfodata.do?b_stat_photo=stat','il_body')">                 
      </logic:equal>  
        <logic:equal name="browseForm" property="returnvalue" value="3">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnquery=query','il_body')">                 
       </logic:equal>  
        <logic:equal name="browseForm" property="returnvalue" value="33">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_query_photo=query','il_body')">                 
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
        <logic:equal name="browseForm" property="returnvalue" value="66">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_query_photo=query','i_body')">                 
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
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_returnstat=stat','il_body')">                 
       </logic:equal>  
        <logic:equal name="browseForm" property="returnvalue" value="55">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','il_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="fx66">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_view_photo=stat','i_body')">                  
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="34">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/two_dim_result.do?b_result=stat','il_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="8">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_returnstat1=stat','i_body')">                 
       </logic:equal>  
       <logic:equal name="browseForm" property="returnvalue" value="81">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_returnstat1=stat','il_body')">                 
       </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="88">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','i_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="881">
           <!-- <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','il_body')"> -->                 
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/view_photo.do','il_body')">  
        </logic:equal>
         <logic:equal name="browseForm" property="returnvalue" value="82">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_double=link&home=1','i_body')">                 
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
        <logic:equal name="browseForm" property="returnvalue" value="tow">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/two_dim_show.do?b_photo=link&result=${browseForm.result}','mil_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="74">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','il_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="73">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','i_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="75">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','il_body')">                 
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
        <logic:equal name="browseForm" property="returnvalue" value="scan">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/scaninfodata.do?b_query=link','nil_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="scanp">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/scan_photo.do?br_return=link','nil_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="nuclear">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showinfodatanuclear.do?b_search=link','nil_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="train_no_post"><!-- 培训不符合本岗位的 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/train/postAnalyse/notaccordpost.do?b_search=link','il_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="train_post"><!-- 培训符合本岗位的 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/train/postAnalyse/accordpost.do?b_search=link','il_body')">                 
        </logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="train_no_post_lesson"><!-- 培训报表分析 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/train/postAnalyse/notaccordpostwork.do?b_searchlesson=link','il_body')">                 
      	</logic:equal>
        <logic:equal name="browseForm" property="returnvalue" value="returnphoto"><!-- 人员机构分析二维统计显示照片 -->
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/deci/statics/employmakeupanalyse.do?br_photo=link','nil_body')">                 
        </logic:equal>
   </td>
 </tr>    
 </table> 
 <%}%>
</html:form>
