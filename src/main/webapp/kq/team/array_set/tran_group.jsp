<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript" src="/js/function.js"></script>
<script language="javascript">
  function submitSAVE()
  {
      var obj= document.getElementsByName("name");
      var n_str=obj[0].value;     
      if(n_str==null||n_str.length<=0)
      {
         alert("班组名称不能为空！");
         return false;
      }else
      {
        arrayGroupForm.action="/kq/team/array_set/search_array_data.do?b_save=link";
        arrayGroupForm.submit();
      }      
  }  
  function getSelectedEmploy()
	{
	  var return_vo=select_org_dialog11(0,2,1); 
	  if(return_vo==null)
	    return false;
	  var fObj=document.getElementById("n1");
	  if(fObj && return_vo.content.indexOf("@K")==-1){
	     fObj.value=return_vo.content;
	  }else{
		 alert("所属部门不能选择岗位！");
		 return false;
	  }
	  fObj=document.getElementById("n2");
	  if(fObj)
	     fObj.value=return_vo.title;  
    }
function select_org_dialog11(flag,selecttype,dbtype,priv,isfilter,loadtype)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_tree.do?flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`privtype=kq";
     if($URL)
    	 theurl = $URL.encode(theurl);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
     var dw=310,dh=400,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
     if (isIE6())
         dh=430;
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
</script>

<html:form action="/kq/team/array_set/search_array_data">
<table border="0" cellspacing="0"  align="left" cellpadding="0" width="100%" >
 <tr>
   <td> 
   
      <table width="270" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr height="20">
       		<!--  <td width="10" valign="top" class="tableft"></td>
       		<td width="180" align=center class="tabcenter">
       		 <logic:equal name="arrayGroupForm" property="save_flag" value="add">
       		<bean:message key="kq.shift.add.group"/>
       		</logic:equal>
            <logic:equal name="arrayGroupForm" property="save_flag" value="update">
                修改班组
            </logic:equal>
       		</td>
       		<td width="10" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="150"></td> --> 
		<td align=center class="TableRow">
       		 <logic:equal name="arrayGroupForm" property="save_flag" value="add">
       		<bean:message key="kq.shift.add.group"/>
       		</logic:equal>
            <logic:equal name="arrayGroupForm" property="save_flag" value="update">
                修改班组
            </logic:equal>
       		</td>           	      
         </tr> 
          <tr>
            <td  class="framestyle9" >
              <br>
               <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" align="center" >
                <tr> 
                <td align="right" class="tdFontcolor" nowrap > <bean:message key="kq.shift.group.name"/>&nbsp; </td>
                 <td align="left" class="tdFontcolor" nowrap> 
                 <html:hidden name="arrayGroupForm" property="a_code"/>   
                 <html:hidden name="arrayGroupForm" property="save_flag"/>   
                 <logic:equal name="arrayGroupForm" property="save_flag" value="add">
                  <html:text name="arrayGroupForm" styleClass="inputtext" property="name"  size="20" value=""/>&nbsp; 
                 </logic:equal>
                 <logic:equal name="arrayGroupForm" property="save_flag" value="update">
                  <html:text name="arrayGroupForm" styleClass="inputtext" property="name"  size="20"/>&nbsp; 
                 </logic:equal>
                </td>              
               </tr>
               <tr> 
                <td align="right" class="tdFontcolor" nowrap > 所属部门&nbsp; </td>
                 <td align="left" class="tdFontcolor" nowrap>               
                  
                 <logic:equal name="arrayGroupForm" property="save_flag" value="add">
                   <html:hidden name="arrayGroupForm" styleId="n1" property="org_id" value="" />  
                   <html:text name="arrayGroupForm" styleClass="inputtext" property="org_name" styleId="n2" size="20"  readonly="true"  value=""/>&nbsp; 
                 </logic:equal>
                 <logic:equal name="arrayGroupForm" property="save_flag" value="update">
                   <html:hidden name="arrayGroupForm" styleId="n1" property="org_id"/>  
                   <html:text name="arrayGroupForm" styleClass="inputtext" property="org_name" styleId="n2" readonly="true"  size="20"/>&nbsp; 
                 </logic:equal>
                 <img src="/images/code.gif" onclick='javascript:getSelectedEmploy();' align="middle" />&nbsp;
                </td>              
               </tr>
              </table>	            	
             </td>
           </tr>
             <br> 
        <tr align="center" class="list3"> 
          <td style="height:35px;"> 
               <input type="button" name="b_save" value='<bean:message key="kq.emp.button.save"/>' onclick="submitSAVE();" class="mybutton">  
                <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">
          </td>
       </tr>  
  </table>  
    <td>
 </td>
 <tr>   
 </table>    
</html:form>