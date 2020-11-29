<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>

    
<hrms:themes />
     
     </head>
     <script type="text/javascript">
     function filterByUm(obj)
     {
        var len=obj.options.length;
        var id="";
        for(var i=0;i<len;i++)
        {
           if(obj.options[i].selected)
           {
              id=obj.options[i].value;
              break;
           }
        }
        var nf=document.getElementById("nf").value;
       
        
        inSupportCommendForm.action="/performance/commend/insupportcommend/everyYearAnalyseVote.do?b_init=init&type=2&um="+id+"&year="+nf;
        inSupportCommendForm.submit();
        
     }
     function getYearlist(a0100,nbase)
     {
        var theUrl="/performance/commend/insupportcommend/query_vote.do?b_query=query`a0100="+a0100+"`nbase="+nbase;
        var url="/performance/commend/insupportcommend/vote_iframe.jsp?src="+theUrl;
        var objlist =window.showModalDialog(url,null,"dialogWidth=400px;dialogHeight=400px;resizable=yes;status=no;"); 
    
     }
     function filterByYear(obj)
     {
         var len=obj.options.length;
        var year="";
        for(var i=0;i<len;i++)
        {
           if(obj.options[i].selected)
           {
              year=obj.options[i].value;
              break;
           }
        }
        var um=document.getElementById("bm").value;
          inSupportCommendForm.action="/performance/commend/insupportcommend/everyYearAnalyseVote.do?b_init=init&type=2&um="+um+"&year="+year;
        inSupportCommendForm.submit();
     }
    function showScan(p0201,a0100,nbase){
		//显示票
		var theurl="/performance/commend/insupportcommend/candidateVindicate.do?b_scan=link&p0201="+p0201+"&a0100="+a0100+"&nbase="+nbase;
		var returnValue=window.showModalDialog(theurl,null, 
			        "dialogWidth:650px; dialogHeight:700px;resizable:yes;center:yes;scroll:no;status:no");
		if(returnValue!=null&&returnValue.scanArray!=null&&returnValue.scanArray.length>0){
			//scanFlag=1删除图像 scanArray=图像列表
			var In_paramters="scanFlag=1&scanArray="+returnValue.scanArray;
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:null,functionId:'9010030029'},null);
		}
	}
     </script>
  <body>
    <html:form action="/performance/commend/insupportcommend/everyYearAnalyseVote">
	<Br>
	<br><!-- 【5802】干部考察：结果分析/后备推荐结果分析和投票状况分析显示界面线太粗    jingq upd 2014.12.22 -->
	<table width="80%" border="1" cellspacing="0"  align="center" cellpadding="0" class="ListTable complex_border_color">
   	  <thead>
   	  <tr class="TableRow">
   	  <td colspan="6" align="left" nowrap>
   	  按部门筛选
   	  <hrms:optioncollection name="inSupportCommendForm" property="umList" collection="list" />
			<html:select styleId="bm" name="inSupportCommendForm" property="um" size="1" onchange="filterByUm(this);" >
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>&nbsp;
			  按年筛选
			  <hrms:optioncollection name="inSupportCommendForm" property="yearList" collection="list" />
			<html:select styleId="nf" name="inSupportCommendForm" property="year" size="1" onchange="filterByYear(this);" >
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>&nbsp;
   	  </td>
   	
   	  </tr>
	 <tr>
            <td align="center" class="TableRow" nowrap>
            <bean:message key="label.commend.i_name"/>
             </td>
            <td align="center" class="TableRow" nowrap>
	        <bean:message key="label.commend.p_name"/>
	   		 </td>
            <td align="center" class="TableRow" nowrap>
	         <bean:message key="label.commend.um"/>
	    	</td>  
	    	<td align="center" class="TableRow" nowrap>
	           显示卡片
	    	</td>     
		    <td align="center" class="TableRow" nowrap style="border-right:none;">
			<bean:message key="label.commend.vote"/>
		    </td>
		     	        	        
         </tr>
   	  </thead>
   	  <% int i=0;%>
   	   <logic:iterate id="element" name="inSupportCommendForm" property="analyseVoteList"  offset="0"> 
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
            &nbsp;<bean:write name="element" property="p0203" />&nbsp; 
	         
	   	 	</td>
            <td align="left" class="RecordRow" nowrap>
          <a href="javascript:getYearlist('<bean:write name="element" property="a0100"/>','<bean:write name="element" property="nbase"/>');"><bean:write name="element" property="a0101" /></a>
	    	</td>            
		    <td align="left" class="RecordRow" nowrap>
			&nbsp;<bean:write name="element" property="e0122" />&nbsp;
		    </td>
		    <td align="center" class="RecordRow" nowrap>
			&nbsp;<a href="javascript:showScan('<bean:write name="element" property="p0201" />','<bean:write name="element" property="a0100"/>','<bean:write name="element" property="nbase"/>');" >&nbsp;<img src='/images/view.gif' border=0/>&nbsp;</a >&nbsp;
		    </td>	
		    <td align="right" class="RecordRow" nowrap style="border-right:none;">
			&nbsp;<bean:write name="element" property="p0304" />&nbsp;
		    </td>	             
         </tr>
   	     
   	     </logic:iterate>
   	</table> 
   	  </html:form>
    
  </body>
</html>