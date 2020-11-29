<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language='javascript'>
function checkSelect()
{
	var a=0;
	for(var i=0;i<document.musterForm.tabid.options.length;i++)
	{
		if(document.musterForm.tabid.options[i].selected )
			a++;
	}
	return a;
}
function blackMaint(checkflag){
	if(checkflag=='2'){
		musterForm.action="/templates/index/portal.do?b_query=link";
		musterForm.submit(); 
	}else{
		musterForm.action="/system/home.do?b_query=link";
		musterForm.submit(); 
	}
}
function sub(flag)
{
	var num=checkSelect();
	if(num==0)
	{
		alert(SELECT_ROSTER);	
		return;
	}
	else if(num>1)
	{
		alert(ONLY_SELECT_ONE_ITEM);
		return;
	}
	else
	{
		var checkflag = "${musterForm.checkflag}";
		if(parseInt(checkflag)>0)
			checkflag=parseInt(checkflag)+2;
		if(flag==1)
		{
			musterForm.action="/general/muster/muster_list.do?b_open=query&checkflag="+checkflag;
			musterForm.submit();
		}
		else if(flag==2)
		{
			musterForm.action="/general/muster/muster_list.do?b_fillout=query&checkflag="+checkflag;
			musterForm.submit();
		}
	}
}
function deleleRecord(){
	if(!ifmsdel()){
		return;
	}

	musterForm.action="/general/muster/muster_list.do?b_delete=link&checkflag=${musterForm.checkflag}";
	musterForm.submit();
}
</script>




<html:form action="/general/muster/muster_list">
  <br>
  <br>
  <br>  
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter"><bean:message key="label.muster.title"/></td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>  -->  
       		<td  align=center class="TableRow"><bean:message key="label.muster.title"/></td>          	      
          </tr> 
          <tr>
            <td  class="framestyle9">
               <br>
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" >      
               	    <logic:equal name="musterForm" property="infor_Flag" value="1">
               	      <tr>
                        <td align="right"  nowrap>
                        
            		<bean:message key="menu.base"/>&nbsp;&nbsp;            
                        </td>
                        <td align="left"  nowrap>
                            <html:select name="musterForm" property="dbpre" size="1">
                              <html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
                            </html:select>    
                        </td>            
                      </tr>
                     </logic:equal>
        	     <html:hidden name="musterForm" property="infor_Flag"/>             
                      <tr><td height="10" colspan="2"></td></tr>   
                      <tr> 
                         <td></td>
                         <td>
                            <html:select name="musterForm" property="tabid" size="1" multiple="false"  style="height:209px;">
                              <html:optionsCollection property="musterlist" value="string(tabid)" label="string(hzname)"/>
                            </html:select>   
                         </td>
                      </tr> 
	       </table>	            	
            </td>
          </tr>            
          <tr class="list3">
            <td align="center" style="height:35px;">
            	<html:button  styleClass="mybutton" property="b_open" onclick="sub(1)">
            		 <bean:message key="button.open"/>
	 			</html:button>   
            
            	<html:button  styleClass="mybutton" property="b_fillout" onclick="sub(2)">
            		 <bean:message key="button.fillout"/>
	 			</html:button>  
         		<input type="button" value="<bean:message key='button.delete'/>" class="mybutton" onclick="deleleRecord();">
	          <logic:notEqual name="musterForm" property="checkflag" value="0">
	          	<input type="button" value="<bean:message key='kq.search_feast.back'/>" class="mybutton" onclick="blackMaint('${musterForm.checkflag}');">
	          </logic:notEqual>      	
            </td>
          </tr>  
  </table>
 
</html:form>
