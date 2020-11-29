<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hjsj.hrms.actionform.browse.BrowseForm,org.apache.commons.beanutils.DynaBean" %>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
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
	  String crosstabtype=(String)session.getAttribute("crosstabtype");
	  crosstabtype = crosstabtype==null?"":crosstabtype;
%>
<%
	  int i=0;
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
function approveEmp(a0100,target) {
	browseForm.action="/workbench/media/searchmediainfolist.do?b_search=link&a0100=${browseForm.a0100}&setname=${browseForm.setname}&setprv=2&flag=notself&returnvalue=${selfInfoForm.returnvalue}&userbase=<bean:write name="browseForm" property="userbase"/>&isAppEdite=1";
	browseForm.target=target;
	browseForm.submit();
}
</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes />
<html:form action="/workbench/browse/showmediainfodetail">
 <html:hidden name="browseForm" property="setname"/> 
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top: 10px">
         
   	  <thead>
           <tr>   
            <td align="center" class="TableRow" nowrap>
              <bean:message key="column.select"/>&nbsp;
             </td>
           <hrms:priv func_id="070904">
            <logic:equal name="browseForm" property="isUserEmploy" value="0">
               <td align="center" class="TableRow" nowrap>
                   <bean:message key="info.appleal.statedesc"/>&nbsp;
                </td> 
             </logic:equal>
            </hrms:priv>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.mediainfo.info_title"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.mediainfo.info_sort"/>&nbsp;
             </td>           		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="browseForm" property="browseForm.list" indexes="indexes"  pagination="browseForm.pagination" pageCount="10" scope="session">
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
          <td align="left" class="RecordRow" nowrap>
          
                 <logic:notEqual name="element" property="state" value="1">
                   <logic:notEqual name="element" property="state" value="">
                      <logic:notEqual name="element" property="state" value="3">
               <hrms:checkmultibox name="browseForm" property="browseForm.select" value="true" indexes="indexes"/>&nbsp;
               <bean:write name="element" property="state"/>
           			</logic:notEqual>
           			</logic:notEqual>
           			</logic:notEqual>
            </td>
            <hrms:priv func_id="070904">
            <logic:equal name="browseForm" property="isUserEmploy" value="0">
             <td align="center" class="RecordRow" nowrap>
               <logic:equal name="element" property="state" value="0">
                  <bean:message key="info.appleal.state0"/>&nbsp;
               </logic:equal>
               <logic:equal name="element" property="state" value="1">
                  <bean:message key="info.appleal.state1"/>&nbsp;
               </logic:equal>
               <logic:equal name="element" property="state" value="2">
                  <bean:message key="info.appleal.state2"/>&nbsp;
               </logic:equal>
               <logic:equal name="element" property="state" value="3">
                  <bean:message key="info.appleal.state3"/>&nbsp;
               </logic:equal>
               <logic:notEqual name="element" property="state" value="0">
                 <logic:notEqual name="element" property="state" value="1">
                   <logic:notEqual name="element" property="state" value="2">
                      <logic:notEqual name="element" property="state" value="3">
                        <bean:message key="info.appleal.state3"/>&nbsp;
                      </logic:notEqual>
                    </logic:notEqual>
                  </logic:notEqual>
               </logic:notEqual>
            </td>
           </logic:equal>
          </hrms:priv>
           <td align="left" class="RecordRow" nowrap>                
           		<bean:write  name="element" property="flag" filter="true"/>
            </td>
             <td align="left" class="RecordRow" nowrap>
             <%DynaBean bean = (DynaBean)pageContext.getAttribute("element");

             %>
             	<a href="/workbench/media/showmediainfo?encryptParam=<%=PubFunc.encrypt("usertable="+browseForm.getUserbase()+"A00&usernumber="+(String)bean.get("a0100")+"&i9999="+(String)bean.get("i9999")) %>" target="_self"><bean:write  name="element" property="title" filter="false"/></a>&nbsp;
            </td>                 	                           	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
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
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table width="100%" border="0">
  <tr>
   <td align="center">
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
			     		<hrms:priv func_id="260112">
			        	<input type="button" class="mybutton" name="approve" value="<bean:message key="selfinfo.defend"/>" onclick="approveEmp('<bean:write name="browseForm" property="a0100"/>','mil_body');" />
			        	</hrms:priv>
        	<%} %>
        	</logic:equal>
        	<logic:equal value="1" name="browseForm" property="fromphoto">
        		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showinfodata.do?b_view_photo=link&code=${browseForm.code}&kind=${browseForm.kind}','nil_body')">
        	</logic:equal>
        	<logic:notEqual value="1" name="browseForm" property="fromphoto">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnshow=browse','nil_body')">
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
          <input type="button" name="returnbutton"  value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();">
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
	       <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_returnstat=stat','il_body')">
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
	       <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','il_body')"> 
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
        	<%if(bosflag!=null&&bosflag.equals("hcm")) {%>
            <!-- 缺陷：3263 zgd 2014-7-25 -->
	           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshow.do?b_view_photo=link','il_body')">                 
           <%}else{ %>
	           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/static/commonstatic/statshowinfodata.do?b_stat_photo=stat','i_body')">                 
           <%} %>
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
