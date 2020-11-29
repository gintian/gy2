<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
	
}

   function submitsave()
   {
     var orgdate=orgPigeonholeForm.archive_date.value;//zhangcq 2016-5-16 日期校验
        if(!checkTime(orgdate))
         { 
           alert("请输入正确的日期格式!");
           return  false;     
          }; 
      var in_paramters="catalog_id="+ orgPigeonholeForm.archive_date.value;
      var name=$F('historyorgname');
      if(name.length==0){
      	alert(ORG_HISNAME_EMPTY);
      	return;
      }
      var description = $F('description'); 
      if(description.length>200){
      	alert("输入说明内容太多，请控制在200字符以内");
      	return;
      }
      var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:checkifexist,functionId:'0405050014'});
   }
  
function checkifexist(outparamters)
{
   var isexist=outparamters.getValue("isexist"); 
   var  key = window.event.keyCode;
   //ie浏览器非兼容模式兼容：鼠标点击时非兼容模式获取的值为undefined xus/19/3/5
   if(!key || key==13 || key==0)
   { 
     if(isexist=="true")
     {
       if(confirm("<bean:message key="general.inform.org.iscoverexist"/>"))
        {
          document.orgPigeonholeForm.target='_self';
          orgPigeonholeForm.action="/general/inform/org/org_pigeonhole.do?b_save=link";
          orgPigeonholeForm.submit();    
        }
        else
        {
          return;
        }       
     } 
     else
     {
          document.orgPigeonholeForm.target='_self';
          orgPigeonholeForm.action="/general/inform/org/org_pigeonhole.do?b_save=link";
          orgPigeonholeForm.submit();    
     } 
   }   
 
}
function back()
{
	orgPigeonholeForm.action = "/org/orginfo/searchorglist.do?b_return=link";
	orgPigeonholeForm.submit();
}
</script>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<hrms:themes></hrms:themes>
<html:form action="/general/inform/org/org_pigeonhole">
<br>
<br>
  <table width="600" border="0" cellpadding="0" cellspacing="0" align="center" >
          <tr height="20">
       		<td  align="center" valign="top" colspan="4"><bean:write name="orgPigeonholeForm" property="scceeddesc"/>
       		<br></td>      		          	      
          </tr> 
          <tr height="20">
       		<!-- td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="general.inform.org.org_pigeonhole"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="415"></td>  -->
       		<td align="left" colspan="1" class="TableRow">&nbsp;<bean:message key="general.inform.org.org_pigeonhole"/>&nbsp;</td>           	             	                              	      
          </tr> 
          <tr>
            <td colspan="4" class="framestyle3">
               <table border="0" cellpmoding="0" cellspacing="0"   cellpadding="0">            
                     <tr>
                	  <td align="right" nowrap class="tdFontcolor">
                	     <bean:message key="general.inform.org.historyorgname"/>&nbsp;                                                                          	      	
                          </td>
                          <td align="left" nowrap class="tdFontcolor">
                	     <html:text name="orgPigeonholeForm" property="historyorgname" styleClass="textColorWrite"   style="width:333px"/>                                                                      	      	
                          </td>
                      </tr>
                      <tr>
                	  <td align="right" nowrap class="tdFontcolor">
                	     <bean:message key="general.inform.org.historyorgdate"/>&nbsp;                                                                        	      	
                          </td>
                          <td align="left" nowrap class="tdFontcolor">${orgPigeonholeForm.archive_date }
                	     <!--<html:text name="orgPigeonholeForm" property="archive_date"  readonly="true" onfocus="setday(this);" size="46"/> -->
                	     <input type="text" class="textColorWrite" name="archive_date" value="${orgPigeonholeForm.archive_date }" maxlength="50" style="width:333px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'时间点')) {this.focus(); this.value='${orgPigeonholeForm.archive_date }'; }"/>
                          </td>
                      </tr>
                      <tr>
                	  <td align="right" valign="top" nowrap class="tdFontcolor">
                	     <bean:message key="general.inform.org.historyorgdesc"/>&nbsp;                                                                          	      	
                          </td>
                          <td align="left" nowrap class="tdFontcolor" style="padding-bottom: 5px;">
                	     <html:textarea name="orgPigeonholeForm" style="width:333px!important;" property="description" cols="52" rows="6"/>                                                                      	      	
                          </td>
                      </tr>
	       </table>	            	
            </td>
          </tr>                           
          <tr class="list3">
            <td colspan="4" align="center" style="padding-top: 5px;">
              <input type="button" Class="mybutton" name="b_save"  value="<bean:message key='button.save'/>" onClick="submitsave()" onKeyDown="if (event.keyCode==13)  submitsave();" />                 
	      <input type="button" name="tranferbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick='back();'>
	    </td>
          </tr>  
  </table>
 
</html:form>
