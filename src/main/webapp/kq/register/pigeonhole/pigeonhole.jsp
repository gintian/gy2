<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->

<script language="javascript">
    /**初化数据*/
    var mappingChanged = false;
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
		AjaxBind.bind(dailyRegisterForm.setlist,/*$('setlist')*/setlist);
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
		
		mappingChanged = false;
	}
	function searchFieldList()
	{
	
	   var tablename=$F('setlist');
	   var len=document.dailyRegisterForm.elements.length;
           var i;
           for (i=0;i<len;i++)
           {
              if (document.dailyRegisterForm.elements[i].type=="text")
              {
                 document.dailyRegisterForm.elements[i].value="";
              }              
           }
	   table_n=tablename;
	   if(tablename == "${dailyRegisterForm.destfld}")
       { 
			window.location.href="/kq/register/pigeonhole.do?b_search=link";
       }	
	      	   
	}
	function openCondCodeDialog(intype,mytarget,hidden_id)
	{
	    var codevalue,thecodeurl,hidden_name,hiddenobj,oldFldName;	
	        
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

      oldFldName = mytarget.value;             
      var theArr=new Array(table_n,intype,mytarget,hiddenobj);
      thecodeurl="/kq/register/pigeonhole/filedselect.jsp?setname="+table_n+"&intype="+intype+""; 
      var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");

      if (mytarget.value != oldFldName)
    	  mappingChanged = true;
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
       dailyRegisterForm.action="/kq/register/pigeonhole.do?b_save=link";       
       dailyRegisterForm.submit();  
	}
	function pigeonholeSave()
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

           if (mappingChanged)
           {
               alert("方案已修改！请保存后再归档。");
               return false;
           }
           var waitInfo=eval("wait");	
	       waitInfo.style.display="block";   
           dailyRegisterForm.action="/kq/register/pigeonhole.do?b_pige=link";           
           dailyRegisterForm.submit();                   
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
     function checkspace(checkstr) 
     {
           var str = '';
           for(i = 0; i < checkstr.length; i++)
           {
              str = str + ' ';
           }
           return (str == checkstr);
     }

     function mappingChanging() {
         mappingChanged = true;
     }
</script>
<%
int i=0;
int r=0;
%>
<div class="fixedDiv3">
<html:form action="/kq/register/pigeonhole">

<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
  <td align="left">
  	  目标子集
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
<tr>
  <td style="position:relative;" height="360" align="left">
  <br>
    <div style="height:350px;width:100%;overflow: auto;position:absolute;top:0;left:1; border-style: solid;border-width :thin ; border-width:1px; " class="common_border_color">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    	<tr>   
       <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap><bean:message key="kq.pigeonhole.srcfldname"/></td>
       <td align="center" class="TableRow" style="border-right: none;border-top: none;" nowrap><bean:message key="kq.pigeonhole.destfldname"/></td>
        <html:hidden name="dailyRegisterForm" property="temp_table"/> 
        <html:hidden name="dailyRegisterForm" property="bytesid"/> 
       </tr>   
      <hrms:paginationdb id="element" name="dailyRegisterForm" sql_str="dailyRegisterForm.po_sqlstr" table="" where_str="dailyRegisterForm.po_wherestr" columns="dailyRegisterForm.po_column" order_by="" page_id="pagination" pagerows="1000" distinct="" indexes="indexes">
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
             
              <td align="center" class="RecordRow" style="border-left:none;" nowrap>
                &nbsp;<bean:write name="element" property="srcfldname" filter="true"/>&nbsp;
              </td> 
               <html:hidden name="element" property="srcfldid"/> 
               <html:hidden name="dailyRegisterForm" property='<%="pagination.curr_page_list["+r+"].destfldid"%>'/> 
               <html:hidden name="element" property="srcfldtype"/> 
               <td align="center" class="RecordRow" style="border-right:none;" nowrap>                
                <logic:notEqual name="s_f" value="true">
                  <html:text name="dailyRegisterForm" readonly="true" styleId='<%="destfldname"+r%>' property='<%="pagination.curr_page_list["+r+"].destfldname"%>' size="30" maxlength="50" styleClass="text4" disabled="true"/>
                </logic:notEqual> 
                <logic:equal name="s_f" value="true">
                 <html:text name="dailyRegisterForm" styleId='<%="destfldname"+r%>' property='<%="pagination.curr_page_list["+r+"].destfldname"%>' size="30" maxlength="50" styleClass="text4"
                   onchange="mappingChanging();"/>
                 <hrms:priv func_id="27020251">
                  <span style="vertical-align: middle;"><img src="/images/code.gif" onclick="openCondCodeDialog('<bean:write name="element" property="srcfldtype" filter="true"/>','<%="pagination.curr_page_list["+r+"].destfldname"%>','<%="pagination.curr_page_list["+r+"].destfldid"%>');"/></span> 
                 </hrms:priv>
                </logic:equal>
               </td>                             
          </tr>
           <%r++;%>  
       </hrms:paginationdb>
      </table>
    </div>  
  </td>
</tr>

<tr>
  <td align="center">
     <hrms:priv func_id="27020251">   
      <input type="button" name="btnreturn" value='&nbsp;<bean:message key="button.save"/>&nbsp;' onclick="save();" class="mybutton">       
     </hrms:priv>
     <hrms:priv func_id="27020250">   
      <input type="button" name="btnreturn" value='&nbsp;<bean:message key="kq.pigeonhole.submit"/>&nbsp;' onclick="pigeonholeSave();" class="mybutton">
     </hrms:priv>	   
      <input type="button" name="btnreturn" value='&nbsp;<bean:message key="button.close"/>&nbsp;' onclick="window.close();" class="mybutton">						      
  </td>
</tr>
</table>
</html:form>
<div id='wait' style='position:absolute;top:200;left:150;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style common_background_color" height=24><bean:message key="classdata.isnow.wiat"/></td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
</div>
<script language="javascript">
   MusterInitData('<bean:write name="dailyRegisterForm"  property="infor_Flag"/>','<bean:write name="dailyRegisterForm"  property="destfld"/>'); 
</script>