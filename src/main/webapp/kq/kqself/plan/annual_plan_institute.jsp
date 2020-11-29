<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.kqself.plan.AnnualPlanForm" %>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript">
 function change()
   {
      annualPlanForm.action="/kq/kqself/plan/annual_plan_institute.do?b_query=link";
      annualPlanForm.submit();
   }
   function issue()
   {
     if(isSubmit())
     {
       if(confirm("确定发布选择的项目？"))
       {
        annualPlanForm.action="/kq/kqself/plan/annual_plan_institute.do?b_transact=link&status=04";
        annualPlanForm.submit();
       }
     }
     
   } 
   function selectcheckeditem()
   {
      	var a=0;
	var b=0;
	var selectid=new Array();
	var a_IDs=eval("document.forms[0].planid");	
	var a_names=eval("document.forms[0].planname");	
	var a_29z5s=eval("document.forms[0].z5");	
	var nums=0;		
	for(var i=0;i<document.forms[0].elements.length;i++)
	{			
	   if(document.forms[0].elements[i].type=='checkbox'&&document.annualPlanForm.elements[i].name!="aa")
	   {		   			
		nums++;
	   }
        }
	if(nums>1)
	{
	    for(var i=0;i<document.forms[0].elements.length;i++)
	    {			
		if(document.forms[0].elements[i].type=='checkbox'&&document.annualPlanForm.elements[i].name!="aa")
		{	
		   if(document.forms[0].elements[i].checked==true)
		   {
			   selectid[a++]=a_IDs[b].value+"`"+a_names[b].value+"`"+a_29z5s[b].value;						
		   }
		   b++;
		}
	    }
	}
	if(nums==1)
	{
	   for(var i=0;i<document.forms[0].elements.length;i++)
	   {			
	      if(document.forms[0].elements[i].type=='checkbox'&&document.annualPlanForm.elements[i].name!="aa")
	      {	
		  if(document.forms[0].elements[i].checked==true)
		  {
			  selectid[a++]=a_IDs.value+"`"+a_names.value+"`"+a_29z5s.value;						
		  }
	      }
	   }
	}

	return selectid;	
   }  
   function appeal()
   {
     if(isSubmit())
     {
    	 var hashvo=new ParameterSet();
         hashvo.setValue("planid", selectcheckeditem());
         hashvo.setValue("year",document.annualPlanForm.year.value);
         var request=new Request({method:'post',onSuccess:appealre,functionId:'15502110019'},hashvo);
     }
   } 
    function appealre(outparamters)
   {
		var warn=outparamters.getValue("warn");
	   if(confirm(warn))
       {
        annualPlanForm.action="/kq/kqself/plan/annual_plan_institute.do?b_transact=link&status=02";
        annualPlanForm.submit();
       }
   }
   function apply()
   { 
      if(isSubmit())
     {
       if(confirm("确定报审选择的项目？"))
       {
        annualPlanForm.action="/kq/kqself/plan/annual_plan_institute.do?b_transact=link&status=08";
        annualPlanForm.submit();
       }
     }
   }
    function update()
   {
      
       alert("该计划已发布执行不可以修改！");
     
   }
   function approve()
   {
      if(isSubmit())
      {
        if(confirm("确定批准选择的项目？"))
        {
          annualPlanForm.action="/kq/kqself/plan/annual_plan_institute.do?b_transact=link&status=00";
          annualPlanForm.submit();
          var target_url;
          target_url="/kq/kqself/plan/annual_plan_institute.do?b_approve=link";
          var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
          var newwindow=window.showModalDialog(iframe_url,'rr','dialogWidth:420px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes'); 
          location = location;
        }
      }
      
   }
   function isSubmit()
   {
       var len=document.annualPlanForm.elements.length;
       var isCorrect=false;
       for (i=0;i<len;i++)
       {
           if (document.annualPlanForm.elements[i].type=="checkbox")
            {
              if( document.annualPlanForm.elements[i].checked==true)
                isCorrect=true;
            }
       }
       if(!isCorrect)
       {
          alert("请选择计划！");
          return false;
       }
       return isCorrect;
   }
   function ifdelete(){
	   var sss = document.annualPlanForm.elements.length;
		var bbb = false;
		for(var i=0;i<sss;i++){
			if(document.annualPlanForm.elements[i].type=="checkbox"){
				if(document.annualPlanForm.elements[i].checked == true && 
						document.annualPlanForm.elements[i].name != "aa")
					bbb = true;
			}
		}
		if(bbb){
			if(confirm("确定删除选择的项目？"))
			{
				return true;
			}
		}else{
			alert("请选择需要删除的项目！");
			return false;
		}
   }
 </script>  
 <SCRIPT LANGUAGE="JavaScript">
  var checkflag = "false";
  function selAll()
   {
      var len=document.annualPlanForm.elements.length;
       var i;
    if(checkflag == "false")
    {
        for (i=0;i<len;i++)
        {
         if (document.annualPlanForm.elements[i].type=="checkbox")
          {
             
            document.annualPlanForm.elements[i].checked=true;
          }
        }
        checkflag = "true";
    }else
    {
        for (i=0;i<len;i++)
        {
          if (document.annualPlanForm.elements[i].type=="checkbox")
          {
             
            document.annualPlanForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    }      
  } 
   function read_sp_result()
   {
      <%
        AnnualPlanForm annualPlanForm=(AnnualPlanForm)session.getAttribute("annualPlanForm");
        String sp_result= annualPlanForm.getSp_result();         
        if(sp_result!=null&&sp_result.length()>0&&!sp_result.equals("xxx"))
        {
       %>
        alert("<%=sp_result%>"); 
        document.annualPlanForm.action="/kq/kqself/plan/annual_plan_institute.do?b_query=link&table=q29";
	    document.annualPlanForm.submit();
        <%         
        }
        annualPlanForm.setSp_result(""); 
        session.setAttribute("annualPlanForm",annualPlanForm);     
      %>
   }
</script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->

<%
int i=0;
%>
<html:form action="/kq/kqself/plan/annual_plan_institute" >
<table  width="100%" align="center">
		 <tr >
          <td align="left" nowrap valign="center">        
           <bean:message key="kq.deration_details.kqnd"/>        
           <hrms:optioncollection name="annualPlanForm" property="slist" collection="list" />
	          <html:select name="annualPlanForm" property="year" size="1" onchange="change();">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
             </html:select> 
           </td>
         </tr>
  </table>
  <div class="fixedDiv2">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>
    <tr>
     <td align="center" class="TableRow" style="border-top:none;border-left:none;" nowrap>
		<input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;
      </td> 
      <logic:iterate id="element" name="annualPlanForm"  property="tlist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center" class="TableRow" style="border-top:none;" nowrap>
                <bean:write name="element" property="itemdesc" />&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
        <bean:message key="conlumn.infopick.detailinfo"/>            	
      </td>
      <hrms:priv func_id="0C3606,2704406"> 
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
        <bean:message key="label.view"/>            	
      </td>
      </hrms:priv>
      <hrms:priv func_id="0C3607,2704407">  
      <td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap>
        <bean:message key="label.edit"/>            	
      </td>
      </hrms:priv>
    </tr>  
  </thead>  
<hrms:paginationdb id="element" name="annualPlanForm" sql_str="annualPlanForm.sql" table="" where_str="annualPlanForm.where" columns="${annualPlanForm.com}" order_by="annualPlanForm.order"  page_id="pagination"  indexes="indexes">
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
           <td align="center" class="RecordRow" style="border-left:none;" nowrap>
               <hrms:checkmultibox name="annualPlanForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
          </td>  
                         <input type="hidden" name="planid" value="<bean:write  name="element" property="q2901" filter="true"/>">  
                         <input type="hidden" name="planname" value="<bean:write  name="element" property="q2905" filter="true"/>">  
                         <input type="hidden" name="z5" value="<bean:write  name="element" property="q29z5" filter="true"/>">  
          <logic:iterate id="tlist" name="annualPlanForm"  property="tlist" indexId="index">
             <logic:equal name="tlist" property="visible" value="true">

                     <td align="left" class="RecordRow" valign="middle" nowrap>
                        <logic:notEqual name="tlist" property="codesetid" value="0">
                           <hrms:codetoname codeid="${tlist.codesetid}" name="element" codevalue="${tlist.itemid}" codeitem="codeitem" scope="page"/>  	      
                           &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                                              
                        </logic:notEqual>
                        <logic:equal name="tlist" property="codesetid" value="0">
                            &nbsp;<bean:write name="element" property="${tlist.itemid}" filter="false"/>&nbsp;                 
                        </logic:equal>                   
                     </td>
                        
            </logic:equal>    
          </logic:iterate>          
          <bean:define id="q29011" name='element' property="q2901"/>
          <bean:define id="q29z51" name='element' property="q29z5"/>
          <%
          	//参数加密
          	String str1 = "plan_id="+q29011+"&dtable=q31";
            String str2 = "plan_id="+q29011+"&status="+q29z51+"&dtable=q31&param=view";
            String str3 = "plan_id="+q29011+"&status="+q29z51+"&dtable=q31&param=update";
          %>
          <hrms:priv func_id="0C3606,2704406"> 
             <td align="center" class="RecordRow" nowrap>
               <a href="/kq/kqself/plan/searchone.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/view.gif" border=0></a>
	     </td>
	  </hrms:priv>	
	     <td align="center" class="RecordRow" nowrap>
            	<a href="/kq/kqself/plan/annual_plan_institute.do?b_view=link&encryptParam=<%=PubFunc.encrypt(str2) %>"><img src="/images/view.gif" border=0></a>
	     </td> 
	     <hrms:priv func_id="0C3607,2704407">  
	        <td align="center" class="RecordRow" style="border-right:none;" nowrap>
	       
	         <logic:equal name="element" property="q29z5" value="01">
            	<a href="/kq/kqself/plan/annual_plan_institute.do?b_update1=link&encryptParam=<%=PubFunc.encrypt(str3) %>"><img src="/images/edit.gif" border=0></a>
	         </logic:equal>
	         <logic:notEqual name="element" property="q29z5" value="01">
            	   <a href="###" onclick="update();"><img src="/images/edit.gif" border=0></a>
	         </logic:notEqual>	   
	     </td>
	    </hrms:priv>
         </tr>
    </hrms:paginationdb>
</table>   
</div> 
<div style="width:expression(document.body.clientWidth-10);">
<table  width="100%" class="RecordRowP" align="center">
		<tr>			
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	      <td  align="right" nowrap class="tdFontcolor">
		      <p align="right"><hrms:paginationdblink name="annualPlanForm" property="pagination" nameId="annualPlanForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>               
  </table>
  </div>
<table width="100%"  align="center">
  <tr>
	            <td>
	              
	               <hrms:submit styleClass="mybutton" property="b_add" function_id="0C3600,2704400"><bean:message key="button.insert"/></hrms:submit>
	               <hrms:submit styleClass="mybutton" property="b_delete" onclick="document.annualPlanForm.target='_self';validate('R','','');return (document.returnValue && ifdelete());" function_id="0C3601,2704401">
	               <bean:message key="button.delete"/></hrms:submit>
	             
                     <hrms:priv func_id="0C3602,2704402"> 
	               <input type="button" name="br_issue" value='<bean:message key="button.issue"/>' class="mybutton" onclick="issue();"> 
	             </hrms:priv> 
	             <hrms:priv func_id="0C3604,2704404"> 
	               <input type="button" name="br_appeal" value='审核' class="mybutton" onclick="appeal();"> 
	             </hrms:priv>  
                     <hrms:priv func_id="0C3605,2704405"> 
	               <input type="button" name="br_approve" value='<bean:message key="button.approve"/>' class="mybutton" onclick="approve();"> 
	             </hrms:priv> 
    
	               
	            </td>
	       </tr>
</table>

</html:form>
<script language="javascript">
   read_sp_result();
</script>