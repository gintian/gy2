<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm" %>
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
DailyRegisterForm daily=(DailyRegisterForm)session.getAttribute("dailyRegisterForm");
String pigeonhole_flag=daily.getPigeonhole_flag();
if(pigeonhole_flag!=null&&pigeonhole_flag.equals("save_true"))
{
  out.print("<script>alert('保存成功');</script>");
  daily.setPigeonhole_flag("");
  session.setAttribute("dailyRegisterForm",daily);
}else if(pigeonhole_flag!=null&&pigeonhole_flag.equals("save_false"))
{
  out.print("<script>alert('保存失败');</script>");
  daily.setPigeonhole_flag("");
  session.setAttribute("dailyRegisterForm",daily);
}else if(pigeonhole_flag!=null&&pigeonhole_flag.equals("pige_true"))
{
  out.print("<script>alert('归档成功');window.close();</script>");
  daily.setPigeonhole_flag("");
  session.setAttribute("dailyRegisterForm",daily);
}else if(pigeonhole_flag!=null&&pigeonhole_flag.equals("pige_false"))
{
  out.print("<script>alert('归档失败');</script>");
  daily.setPigeonhole_flag("");
  session.setAttribute("dailyRegisterForm",daily);
}

%>

<script language="javascript">
        /**初化数据*/
        var table_n;
	function MusterInitData(infor,destfld)
	{
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
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
		AjaxBind.bind(dailyRegisterForm.setlists,/*$('setlist')*/setlist);
		if($('setlists').options.length>0)
		{
		  
		  	 $('setlists').options[r].selected=true;
		}	
	}
	function searchFieldList()
	{
	
	   var tablename=$F('setlists');
	   var len=document.dailyRegisterForm.elements.length;
           var i;
           for (i=0;i<len;i++)
           {
              if (document.dailyRegisterForm.elements[i].type=="text")
              {
                 document.dailyRegisterForm.elements[i].value="";
              }              
           }
       document.getElementById("setlist").value = tablename;   
	   table_n=tablename;	   	   
       if(tablename == "${dailyRegisterForm.destfld}")
       { 
			window.location.href="/kq/register/sing_oper/sing_operation.do?b_operation=link";
       }
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
             if(table_n=="")
               table_n=$F('setlists');             
             target_name=mytarget.name;             
             var theArr=new Array(table_n,intype,mytarget,hiddenobj);
             thecodeurl="/kq/register/pigeonhole/filedselect.jsp?setname="+table_n+"&intype="+intype+""; 
             var popwin= window.showModalDialog(thecodeurl, theArr, 
               "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	}
	function checkspaceroot(checkstr)
     {
           if(checkstr=="指标项目")
           {
              alert("不能用指标项目为目的指标！");
              return true;
           }else
           {
             return false;
           }
     }
	function save()
    {
           if(checkspace(document.dailyRegisterForm.destfldname0.value))
           {
             alert("考勤期间必须对应！");
             return false;
           }              
           if(checkspaceroot(document.dailyRegisterForm.destfldname0.value))
           {
             return false;
           } 
           var waitInfo=eval("wait");	
	       waitInfo.style.display="block";   
           dailyRegisterForm.action="/kq/register/sing_oper/sing_operation.do?b_save=link";           
           dailyRegisterForm.submit();                   
     }
	function pigeonholeSave()
        {
           if(checkspace(document.dailyRegisterForm.destfldname0.value))
           {
             alert("考勤期间必须对应！");
             return false;
           }
           if (document.getElementById("setlistid")) {
           	document.getElementById("setlistid").value = document.getElementById("setlistlist").value;
           }
           var waitInfo=eval("wait");	   
           waitInfo.style.display="block";           
           dailyRegisterForm.action="/kq/register/sing_oper/sing_operation.do?b_pigeonhole=link";
           dailyRegisterForm.target="mil_body";
           dailyRegisterForm.submit();                   
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
       function b_cancel()
       {
           dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_search=link";
           dailyRegisterForm.submit();
       }

</script>
<html:form action="/kq/register/pigeonhole">
<%
int i=0;
int r=0;
%>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
  <td align="center">
      <logic:equal name="s_f" value="true">
         <select name="setlists" size="1"  style="width:80%" onchange="searchFieldList();" id="setlistlist">    
			    <option value="1111">#</option>
         </select>
         <input type="hidden" name="setlist" id="setlistid" value="${dailyRegisterForm.destfld}"/>
       </logic:equal>
       <logic:equal name="s_f" value="false">
         <select name="setlists" size="1"  style="width:80%" disabled="true" id="setlistlist">    
			    <option value="1111">#</option>
         </select>
         <input type="hidden" name="setlist" id="setlistid" value="${dailyRegisterForm.destfld}"/>
       </logic:equal>      
  </td>
</tr>
</table>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    	
     <tr>   
       <td align="center" class="TableRow" nowrap style="border-right:none"><bean:message key="kq.pigeonhole.srcfldname"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.pigeonhole.destfldname"/></td>
        <html:hidden name="dailyRegisterForm" property="temp_table"/> 
        <html:hidden name="dailyRegisterForm" property="bytesid"/> 
     </tr>   
    <hrms:paginationdb id="element" name="dailyRegisterForm" sql_str="dailyRegisterForm.po_sqlstr" table="" where_str="dailyRegisterForm.po_wherestr" columns="dailyRegisterForm.po_column" order_by="" page_id="pagination" pagerows="50" distinct="" indexes="indexes">
       <%
          //LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
          //String destfldid=(String)abean.get("destfldid");
          //String destfldname=(String)abean.get("destfldname");
          if(i%2==0){ 
          %>
          <tr class="trShallow">
          <%
          }else{
          %>
          <tr class="trDeep">
          <%}i++;            
          %>  
             
              <td align="center" class="RecordRow" nowrap style="border-right:none;border-top:none">
                &nbsp;<bean:write name="element" property="srcfldname" filter="true"/>&nbsp;
              </td>               
               <html:hidden name="element" property="srcfldid"/> 
               <html:hidden name="dailyRegisterForm" property='<%="pagination.curr_page_list["+r+"].destfldid"%>'/> 
               <html:hidden name="element" property="srcfldtype"/> 
                <td align="center" class="RecordRow" nowrap style="border-top:none">  
                <logic:notEqual name="s_f" value="true">              
                  <html:text name="dailyRegisterForm" styleId='<%="destfldname"+r%>' property='<%="pagination.curr_page_list["+r+"].destfldname"%>' size="30" maxlength="50" styleClass="text4"/>
                  <script>
                  	document.getElementById("destfldname<%=r%>").readOnly="true";
                  </script>
                </logic:notEqual>
                <logic:equal name="s_f" value="true">
                <html:text name="dailyRegisterForm" styleId='<%="destfldname"+r%>' property='<%="pagination.curr_page_list["+r+"].destfldname"%>' size="30" maxlength="50" styleClass="text4" readonly="true"/>
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
     <hrms:priv func_id="27020250,0C3123">           
      <input type="button" name="btnreturn" value='&nbsp;<bean:message key="kq.pigeonhole.submit"/>&nbsp;' onclick="pigeonholeSave();" class="mybutton">	   
     </hrms:priv>
      <input type="button" name="btnreturn" value='&nbsp;<bean:message key="button.return"/>&nbsp;' onclick="history.back(-1);" class="mybutton">						      
      
    </td>
  </tr>
 </table>
 <div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24><bean:message key="classdata.isnow.wiat"/></td>
          </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div> 
</html:form>
<script language="javascript">
   MusterInitData('<bean:write name="dailyRegisterForm"  property="infor_Flag"/>','<bean:write name="dailyRegisterForm"  property="destfld"/>'); 
</script>