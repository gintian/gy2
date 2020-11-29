<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc,com.hjsj.hrms.actionform.general.deci.leader.LeaderForm,java.util.List"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	LeaderForm leaderForm=(LeaderForm)session.getAttribute("leaderForm");
	String sformula=leaderForm.getSformula();
	double[][] statdoublevaluess=null;
	List harraylist =null;
	List varraylist=null;
	int decimal =0;
	if(sformula!=null){
		statdoublevaluess=leaderForm.getStatdoublevaluess();
		harraylist = leaderForm.getHarraylist();
		varraylist = leaderForm.getVarraylist();
		decimal = Integer.parseInt(leaderForm.getDecimal());
	}
%>
<script LANGUAGE=javascript>
   function changestatid()
   {
      leaderForm.action="/general/deci/leader/candi_stat.do?b_query=link&statid=" + $F('statid');
      leaderForm.submit();
   }
   
   function testchart(e)
   {
      var name=e.name;
      if(name!="")
      {
         	//name=getEncodeStr(name);
         	leaderForm.action="/general/deci/leader/analysedata.do?b_one=link&showLegend="+$URL.encode(name)+"&a_code=${leaderForm.a_code}";
         	leaderForm.submit();
      }
   }
</script> 
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/general/deci/leader/candi_stat">
    <table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
      <tr>
        <td align="left"  nowrap>
        <bean:message key="label.query.dbpre"/>
     	    <html:select name="leaderForm" property="dbpre" size="1"  onchange="javascript:changestatid()">
                           <html:optionsCollection property="dbprelist" value="dataValue" label="dataName"/>
                  </html:select>  
             <bean:message key="makeupanalyse.stat"/>
                  <html:select name="leaderForm" property="statid" size="1"  onchange="javascript:changestatid()">
                           <html:optionsCollection property="statlist" value="dataValue" label="dataName"/>
                  </html:select>
            <logic:equal name="leaderForm" property="isonetwostat" value="1">
            <%-- <a href="/general/deci/leader/candi_stat.do?char_type=12"><bean:message key="leaderteam.setdb.solidsquaremap"/></a> --%>
            <a href="/general/deci/leader/candi_stat.do?char_type=11"><bean:message key="leaderteam.setdb.planesquaremap"/></a>
            <%-- <a href="/general/deci/leader/candi_stat.do?char_type=5"><bean:message key="leaderteam.setdb.solidsparmap"/></a> --%>
            <a href="/general/deci/leader/candi_stat.do?char_type=20"><bean:message key="leaderteam.setdb.planesparmap"/></a>
            </logic:equal>  
               
   	</td>      
      </tr>
   </table>   
    <logic:equal name="leaderForm" property="isonetwostat" value="1">
   <table  align="center">
          <tr>
            <td align="center" nowrap colspan="5">         
       	 	<hrms:chart name="leaderForm" title="${leaderForm.snamedisplay}" scope="session" legends="datalist" data="" width="670" height="530" chart_type="${leaderForm.char_type}" numDecimals="${leaderForm.decimal }" pointClick="testchart" xangle="${leaderForm.xangle }">
	 	</hrms:chart>
            </td>
          </tr>          
      </table> 
       </logic:equal>
       <logic:equal name="leaderForm" property="isonetwostat" value="2">
      <table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
        <tr>
          <td align="center"  nowrap>
       	       <bean:write name="leaderForm" property="snamedisplay" />&nbsp;(<bean:message key="workbench.stat.stattotalvalue"/><bean:write name="leaderForm" property="totalvalue" />)
           </td>                	    	    	    		        	        	        
         </tr>      
      </table>
      <table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
             <tr>
                 <td align="center" class="TableRow" nowrap>
                 </td> 
                 <logic:iterate id="element" name="leaderForm" property="varraylist">
                     <td align="center" class="TableRow" nowrap>
                         <bean:write name="element" property="legend"/>
                     </td>   
                 </logic:iterate>                      	    	    	    		        	        	        
              </tr>
   	  </thead>
   	  <logic:empty name="leaderForm" property="sformula">
   	   <logic:iterate id="element" name="leaderForm" property="harraylist" indexId="indexh">
   	      <tr>
                <td align="center" class="TableRow" nowrap>
                    <bean:write name="element" property="legend"/> 
                 </td> 
                 <logic:iterate id="helement" name="leaderForm" property="varraylist" indexId="indexv">
                   <td align="center" class="RecordRow" nowrap>
                       <a href="/general/deci/leader/analysedata.do?b_double=link&a_code=${leaderForm.a_code}&v=${indexv}&h=${indexh}">${leaderForm.statdoublevalues[indexv][indexh]}</a>
                   </td> 
                 </logic:iterate>          	    	    	    		        	        	        
              </tr> 
           </logic:iterate>  
           </logic:empty>
<logic:notEmpty  name="leaderForm" property="sformula">
	<%for(int h=0;h<harraylist.size();h++){
		LazyDynaBean element = (LazyDynaBean)harraylist.get(h);
	 %>
   	      <tr>
                <td align="center" class="TableRow" nowrap>
                    <%=element.get("legend").toString() %>
                 </td> 
                 <%for(int v=0;v<varraylist.size();v++){
				 %>
					 <td align="center" class="RecordRow" nowrap>
                       <a href="/general/deci/leader/analysedata.do?b_double=link&a_code=${leaderForm.a_code}&v=<%=v %>&h=<%=h %>"><%=PubFunc.formatDecimals(statdoublevaluess[v][h],decimal) %></a>
                   </td> 
               <%} %>        	    	    	    		        	        	        
              </tr> 
 <%} %> 
</logic:notEmpty>         
       </table>
    </logic:equal>
</html:form>
