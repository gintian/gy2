<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/kq/kq.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<%
	int i=0;
%>
<script language="javascript">
   function change()
   {
      arrayGroupSelectForm.action="/kq/team/array_group/selectfiled.do?b_query=link";
      arrayGroupSelectForm.submit();
   }
   
   
    var checkflag = "false";

 function selAll()
  {
      var len=document.arrayGroupSelectForm.elements.length;
       var i;

    
  
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.arrayGroupSelectForm.elements[i].type=="checkbox")
            {
              document.arrayGroupSelectForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.arrayGroupSelectForm.elements[i].type=="checkbox")
          {
            document.arrayGroupSelectForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  } 
  
  function goback()
  {
    var len=document.arrayGroupSelectForm.elements.length;
    var i;
    for (i=0;i<len;i++)
    {
           if (document.arrayGroupSelectForm.elements[i].type=="checkbox")
            {
              document.arrayGroupSelectForm.elements[i].checked=false;
            }
    }
    arrayGroupSelectForm.action="/kq/team/array_group/search_array_emp_data.do?b_search=link&group_id=${arrayGroupSelectForm.group_id}";
    arrayGroupSelectForm.submit();
  }
  function getHardEmploy()
  {
  	    var checkflag=false;
  	    var len=document.arrayGroupSelectForm.elements.length;
        for (var i=0;i<len;i++)
        {
            var obj = document.arrayGroupSelectForm.elements[i]
            if (obj.type=="checkbox"){
               if(obj.checked==true && obj.name!="aa"){
              	  checkflag=true;
               }
            }
         }
        if(!checkflag){
        	alert("请选择人员！");
        	return false;
        }
  		var t_url="/kq/team/array_group/load_zidong_class.do?b_query=link";
     	var iframe_url="/general/query/common/iframe_query.jsp?src="+t_url;
     	var return_vo= window.showModalDialog(iframe_url,'rr', 
       			"dialogWidth:620px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       	if(!return_vo)
	     	return false;
	    if(return_vo.flag=="true")
	    {
	    	var start_date = return_vo.start_date;
	    	var end_date = return_vo.end_date;
	    	var flag = return_vo.flag;
	    	var zhji = return_vo.zhji;
	    	arrayGroupSelectForm.action="/kq/team/array_group/selectfiled.do?b_add=link&start_date="+start_date+"&end_date="+end_date+"&flag="+flag+"&zhji="+zhji;
    		arrayGroupSelectForm.submit();
	    }else
	    {
	    	var start_date = return_vo.start_date;
	    	var end_date = return_vo.end_date;
	    	var flag = return_vo.flag;
	    	var zhji = return_vo.zhji;
	    	arrayGroupSelectForm.action="/kq/team/array_group/selectfiled.do?b_add=link&start_date="+start_date+"&end_date="+end_date+"&flag="+flag+"&zhji="+zhji;
    		arrayGroupSelectForm.submit();
	    }
  }
</script>
<html:form action="/kq/team/array_group/selectfiled">
<html:hidden name="arrayGroupSelectForm" property="like"/>
<table width="80%" cellspacing="0"  align="center" cellpadding="0">
 <tr>

    <td align="left" nowrap>
       	<hrms:optioncollection name="arrayGroupSelectForm" property="dblist" collection="list" />
             <html:select name="arrayGroupSelectForm" property="dbpre" size="1" onchange="change();">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select>   
    </td> 
 </tr>
</table>

<table width="80%" cellspacing="0"  align="center" cellpadding="0" border="0" class="ListTable1">
  <thead>
           <tr>
             <td align="center" class="TableRow" nowrap>
              <input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;
             </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
	    <logic:notEqual value="1" name="arrayGroupSelectForm" property="isPost">
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
	    </logic:notEqual>
        <td align="center" class="TableRow" nowrap style="border-right:0px;">
             <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="A0101" fielditem="fielditem"/>
	         <bean:write name="fielditem" property="dataValue" />&nbsp;   
	    </td> 
	   </tr>
   	  </thead>    	  
   	  <hrms:paginationdb id="element" name="arrayGroupSelectForm" sql_str="arrayGroupSelectForm.sqlstr_s" table="" where_str="arrayGroupSelectForm.wherestr_s" columns="${arrayGroupSelectForm.columnstr_s}" order_by="arrayGroupSelectForm.ordeby_s" page_id="pagination" pagerows="${arrayGroupSelectForm.pagerows}"  indexes="indexes">
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
            <td align="center" class="RecordRow" nowrap>
               <hrms:checkmultibox name="arrayGroupSelectForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;            
	    </td>
	    <logic:notEqual value="1" name="arrayGroupSelectForm" property="isPost">
            <td align="left" class="RecordRow" nowrap>
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;     
	    </td>
	    </logic:notEqual>
            <td align="left" class="RecordRow" nowrap style="border-right:0px;">
                 &nbsp;<bean:write name="element" property="a0101" filter="false"/>&nbsp;
	    </td>  
	  </tr>
        </hrms:paginationdb>
   <tr>
     <td colspan="5" height="22" >
       <table  width="100%" align="center" class="RecordRow_l common_border_color" style="BORDER-BOTTOM: 1pt solid;">
		<tr>
		    <td valign="middle" class="tdFontcolor">
					<hrms:paginationtag name="arrayGroupSelectForm" pagerows="${arrayGroupSelectForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="arrayGroupSelectForm" property="pagination" nameId="arrayGroupSelectForm" scope="page">
				</hrms:paginationdblink>
			</td>
		  </tr>
        </table>
     </td>
   </tr>  
</table>



<table  width="80%" align="center">
          <tr>
            <td align="center">             
	      <!--<hrms:submit styleClass="mybutton" property="b_add">
            	   添加
	      </hrms:submit>-->
	      <input type="button" name="tianjia" value="添加"  class="mybutton" onclick="getHardEmploy();">
	      <input type="button" name="alll" value='<bean:message key="button.return"/>' class="mybutton" onclick="goback();"> 
	    </td>
          </tr>          
</table>
</html:form>

<script language="javascript">
hide_nbase_select('dbpre');
</script>