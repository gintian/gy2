<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.ZpExamReportForm"%>
<%
	int i=0;
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
</style>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>  
<script language="javascript">
   function getSelect(columns)
   {  	 
    var element=document.getElementsByName("sum_score");
    if(element.length>0)
    { 
     var i=0;
     var r=0;	    
     var y=0;
     var tag=true;
     var fieldvalue="";
     var forms= new Array();
     var hashvo=new ParameterSet();
     while(i!=-1)
     {		
    
	i=columns.indexOf(",",r);
	
	if(i!=-1){
	   var str=columns.substring(r,i);
	    if(!isArray($F(str))){
	      if(str!="a0101" && str!="sum_score")
	      {
	        var d=new Array();	        
                d=$F(str).split(",");
                 if(str!="a0100"){
                   tag=validatedata(str,1)
                   if(tag==false)
	           {
	             return;
	           }
	         }
                forms[y]=d;  
              }            
	    }else{
             if(str!="a0101"  && str!="sum_score")
	     {
	      forms[y]=$F(str);
	      if(str!="a0100")
	      {
	        tag=validatedata(str,$F(str).length);	       
	        if(tag==false)
	        {
	          return;
	        }
	      }
	     }
	   }
	  if(str!="a0101"  && str!="sum_score")	   	        
	   y++;
	}
        r=i+1;	       	        
     }	   

     hashvo.setValue("forms",forms);
     hashvo.setValue("columns",columns);
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'4400000009'},hashvo);
    }
   }	
  function isArray(obj) 
  { 
      return (obj.constructor.toString().indexOf('Array')!= -1);
  } 
  function showSelect(outparamters)
  { 
    var tes=outparamters.getValue("sumscorelist");
    var element=document.getElementsByName("sum_score");
    for(var i=0;i<tes.length;i++)
    {
      element[i].value=tes[i];
    }    
  }
   function validatedata(fieldname,records)
   {
     var element=document.getElementsByName(fieldname);
     var tag=true;
     for(var i=0;i<records;i++)
     {
         tag=checkNUM2(element[i],7,1) &&  tag ;  
         if(tag==false)
	 {
	    element[i].focus();
	    return false;
	}		  
     }   
   }
</script>
<html:form action="/hire/zp_exam/search_exam_report" >
   <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0">
     <tr> 
        <td align="left"  nowrap valign="top" height="20">
               <html:text name="zpExamReportForm" property="querycondition" size="12" styleClass="text6"/>&nbsp;  
               <hrms:submit styleClass="mybutton" property="b_search">
            		<bean:message key="button.query"/>
	 	</hrms:submit>
	</td>      
     </tr>     
   </table>
   <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
   	  <br>
   	  <br>
           <tr>          
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.title.name"/>
	    </td>  
	           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_exam.read_score"/>
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_exam.written_score"/>            	
	    </td>	    
	    <logic:iterate  id="namelist"    name="zpExamReportForm"  property="nameList" indexId="index">
	    <td align="center" class="TableRow" nowrap>
                   <bean:write name="zpExamReportForm" property="<%="nameList["+index+"]"%>" filter="true"/>
	    </td>
           </logic:iterate> 
           <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_exam.sum_score"/>            	
	    </td>		    		        	        	        
           </tr>
   	  </thead>   	  
          <hrms:paginationdb id="element" name="zpExamReportForm" sql_str="zpExamReportForm.sqlstr" table="" where_str="zpExamReportForm.strwhere" columns="zpExamReportForm.columns" order_by="zpExamReportForm.orderby" page_id="pagination" pagerows="20" indexes="indexes">
	  <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow1">
          <%}
          else
          {%>
          <tr class="trDeep1">
          <%
          }
          i++;          
          %>   
	       <logic:iterate  id="fieldlist"    name="zpExamReportForm"  property="fieldList" indexId="index">
	          <logic:equal name="fieldlist" property="itemid" value="a0101">
	            <td align="right"  nowrap valign="top" height="20">   
	              <bean:write name="element" property="${fieldlist.itemid}"/>&nbsp; 
	              <html:hidden name="element" property="a0100" styleClass="text"/>&nbsp;                  
	            </td>
	          </logic:equal>
	          <logic:notEqual name="fieldlist" property="itemid" value="a0101">
	             <logic:equal name="fieldlist" property="itemid" value="sum_score">
	                <td align="right"  nowrap valign="top" height="20">   
	                    <html:text name="element" property="${fieldlist.itemid}" size="8" styleClass="text6"/>&nbsp;  
	                 </td>
	              </logic:equal>
	             <logic:notEqual name="fieldlist" property="itemid" value="sum_score">
	                <td align="right"  nowrap valign="top" height="20">   
	                 <html:text name="element" property="${fieldlist.itemid}" size="8" styleClass="text6"/>&nbsp;                  
	                </td>
	              </logic:notEqual>
	          </logic:notEqual>
               </logic:iterate> 
             </tr>
          </hrms:paginationdb>        
  </table>
  <table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
				<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="zpExamReportForm" property="pagination" nameId="zpExamReportForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
  </table>
  <table  width="70%" align="center">
          <tr>
            <td align="center">
         	<input type="button" name="b_save" value="<bean:message key="button.save"/>" onclick="getSelect('<bean:write name="zpExamReportForm"  property="columns"/>');" class="mybutton">  	
         	<hrms:submit styleClass="mybutton" property="b_sort">
            		<bean:message key="label.zp_exam.sort"/>
	 	</hrms:submit>
        
            </td>
          </tr>          
  </table>
</html:form>
