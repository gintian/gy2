<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript" src="/js/validate.js"></script>
<%@ page import="com.hjsj.hrms.actionform.kq.register.BrowseRegisterForm" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String s_f="true";
if(!userView.hasTheFunction("27020251"))
{
   s_f="false";
}
request.setAttribute("s_f",s_f);
BrowseRegisterForm daily=(BrowseRegisterForm)session.getAttribute("browseRegisterForm");
String pigeonhole_flag=daily.getPigeonhole_flag();
if(pigeonhole_flag!=null&&pigeonhole_flag.equals("save_true"))
{
  out.print("<script>alert('保存成功');</script>");
  daily.setPigeonhole_flag("");
  session.setAttribute("browseRegisterForm",daily);
}else if(pigeonhole_flag!=null&&pigeonhole_flag.equals("save_false"))
{
  out.print("<script>alert('保存失败');</script>");
  daily.setPigeonhole_flag("");
  session.setAttribute("browseRegisterForm",daily);
}else if(pigeonhole_flag!=null&&pigeonhole_flag.equals("pige_true"))
{
  out.print("<script>alert('归档成功');window.close();</script>");
  daily.setPigeonhole_flag("");
  session.setAttribute("browseRegisterForm",daily);
}else if(pigeonhole_flag!=null&&pigeonhole_flag.equals("pige_false"))
{
  out.print("<script>alert('归档失败');</script>");
  daily.setPigeonhole_flag("");
  session.setAttribute("browseRegisterForm",daily);
}

%>
<html:form action="/kq/register/pigeonhole/sing_pigeonhole">
<logic:equal name="browseRegisterForm" property="error_flag" value="0">
<script language="javascript">
        /**初化数据*/
        var table_n;
	function MusterInitData(infor,destfld)
	{
	   var pars="base="+infor;	 
	   table_n=destfld;	   
	   var hashvo=new ParameterSet();
	   hashvo.setValue("destfld",destfld);
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'15302110001'},hashvo);
	}
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
	    var setlist=outparamters.getValue("setlist");
	    var r=outparamters.getValue("r_num");
		AjaxBind.bind(browseRegisterForm.setlist,/*$('setlist')*/setlist);
		if($('setlist').options.length>0)
		{
		   table_n= $('setlist').options[r].value;		   
		   $('setlist').options[r].selected=true;
		   <logic:equal name="s_f" value="false">
		     var no = new Option();
	  	     for(var i=0;i<$('setlist').options.length;i++)
             {
               if($('setlist').options[i].selected)
               {
                  no.value=$('setlist').options[i].value;
		    	  no.text=$('setlist').options[i].text;
    	          break;
               }
             }
             while(true)
             {
                if($('setlist').options.length>0)
		        {
		           $('setlist').options.remove(0);
		        }else
		           break;
             }
             $('setlist').options[0]=no;
	      </logic:equal>
		}		 	
	}
	function searchFieldList()
	{
	
	   var tablename=$F('setlist');
	   var len=document.browseRegisterForm.elements.length;
           var i;
           for (i=0;i<len;i++)
           {
              if (document.browseRegisterForm.elements[i].type=="text")
              {
                 document.browseRegisterForm.elements[i].value="";
              }              
           }
	   table_n=tablename;	   	   
	}
	function openCondCodeDialog(intype,mytarget,hidden_id)
	{   
	    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;	  
            if(mytarget==null)
              return;
             if(typeof mytarget!="object")
             {
              var oldInputs=document.getElementsByName(mytarget);
              mytarget=oldInputs[0];                  	       	
             }
             var hiddenInputs=document.getElementsByName(hidden_id);
             if(hiddenInputs!=null)
             {
    	         hiddenobj=hiddenInputs[0];
    	          codevalue="";
             }
             target_name=mytarget.name;             
             var theArr=new Array(table_n,intype,mytarget,hiddenobj);
             thecodeurl="/kq/register/pigeonhole/filedselect.jsp?setname="+table_n+"&intype="+intype+""; 
             var popwin= window.showModelessDialog(thecodeurl, theArr, 
               "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	}
	function save()
    {
           if(checkspace(document.dailyRegisterForm.destfldname0.value))
           {
             alert("考勤期间必须对应！");
             return false;
           }              
           if(checkspaceroot(document.browseRegisterForm.destfldname0.value))
           {
             return false;
           } 
           var waitInfo=eval("wait");	
	       waitInfo.style.display="block";   
           dailyRegisterForm.action="/kq/register/pigeonhole/sing_pigeonhole.do.do?b_save=link";           
           dailyRegisterForm.submit();                   
     }
	function pigeonholeSave()
        {
           if(checkspace(document.browseRegisterForm.destfldname0.value))
           {
             alert("考勤期间必须对应！");
             return false;
           }
           browseRegisterForm.action="/kq/register/pigeonhole/sing_pigeonhole.do?b_pigeonhole=link";
           browseRegisterForm.target="mil_body";
           browseRegisterForm.submit(); 
           window.close();         
        }
        function checkspace(checkstr) 
        {
           var str = '';
           for(i = 0; i < checkstr.length; i++)
           {
              str = str + ' ';
           }
           return (str == checkstr);
       }
       function backapp()
       
       {
          browseRegisterForm.action="/kq/register/browse_registerdata.do?b_query=link&kind=2";
          browseRegisterForm.target="mil_body";
          browseRegisterForm.submit();
  
       }
</script>
<%
int i=0;
int r=0;
%>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
  <td align="left">
      <logic:equal name="s_f" value="true">
         <select name="setlist" size="1"  style="width:80%" onchange="searchFieldList();">    
			    <option value="1111">#</option>
         </select>
       </logic:equal>
       <logic:equal name="s_f" value="false">
         <select name="setlist" size="1"  style="width:80%">    
			    <option value="1111">#</option>
         </select>
       </logic:equal>  
  </td>
</tr>
</table>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    	
     <tr>   
       <td align="center" class="TableRow" nowrap><bean:message key="kq.pigeonhole.srcfldname"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.pigeonhole.destfldname"/></td>
        <html:hidden name="browseRegisterForm" property="temp_table"/>
        <html:hidden name="browseRegisterForm" property="bytesid"/> 
     </tr>     
    <hrms:paginationdb id="element" name="browseRegisterForm" sql_str="browseRegisterForm.po_sqlstr" table="" where_str="browseRegisterForm.po_wherestr" columns="browseRegisterForm.po_column" order_by="" page_id="pagination" pagerows="50" distinct="" indexes="indexes">
       <%
          
          if(i%2==0){ 
          %>
          <tr class="trShallow">
          <%
          }else{
          %>
          <tr class="trDeep">
          <%}i++;            
          %>  
             
              <td align="center" class="RecordRow" nowrap>
                &nbsp;<bean:write name="element" property="srcfldname" filter="true"/>&nbsp;
              </td> 
               <html:hidden name="element" property="srcfldid"/> 
               <html:hidden name="browseRegisterForm" property='<%="pagination.curr_page_list["+r+"].destfldid"%>'/> 
               <html:hidden name="element" property="srcfldtype"/> 
                <td align="center" class="RecordRow" nowrap>   
                 <logic:notEqual name="s_f" value="true">      
                   <html:text name="browseRegisterForm" styleId='<%="destfldname"+r%>' property='<%="pagination.curr_page_list["+r+"].destfldname"%>' size="30" maxlength="50" styleClass="text4" disabled="true"/>        
                </logic:notEqual>
                <logic:equal name="s_f" value="true">             
                <html:text name="browseRegisterForm" styleId='<%="destfldname"+r%>' property='<%="pagination.curr_page_list["+r+"].destfldname"%>' size="30" maxlength="50" styleClass="text4"/>
                 <img src="/images/code.gif" onclick="openCondCodeDialog('<bean:write name="element" property="srcfldtype" filter="true"/>','<%="pagination.curr_page_list["+r+"].destfldname"%>','<%="pagination.curr_page_list["+r+"].destfldid"%>');"/>
               </logic:equal>
              </td>              
          </tr>
           <%r++;%>  
    </hrms:paginationdb>
   
  </table>
  <table width="70%" align="center">   
    <tr>
    <td align="center" colspan="2"> 
    <hrms:priv func_id="27020251">   
      <input type="button" name="btnreturn" value='&nbsp;<bean:message key="button.save"/>&nbsp;' onclick="save();" class="mybutton">       
     </hrms:priv>
     <hrms:priv func_id="27020250">              
      <input type="button" name="btnreturn" value='<bean:message key="kq.pigeonhole.submit"/>' onclick="pigeonholeSave();" class="mybutton">	   
    </hrms:priv>
      <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">						      
      
    </td>
  </tr>
 </table>
 <script language="javascript">
   MusterInitData('<bean:write name="browseRegisterForm"  property="infor_Flag"/>','<bean:write name="browseRegisterForm"  property="destfld"/>'); 
</script>
</logic:equal>
 <logic:notEqual name="browseRegisterForm" property="error_flag" value="0">
<script language="javascript">
var error_str=kqErrorProcess('<bean:write name="browseRegisterForm"  property="error_flag"/>','<bean:write name="browseRegisterForm"  property="error_message"/>','<bean:write name="browseRegisterForm"  property="error_return"/>');
document.write(error_str);
</script>
</logic:notEqual>
</html:form>
