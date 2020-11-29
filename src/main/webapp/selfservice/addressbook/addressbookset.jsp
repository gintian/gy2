<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
function setselectitem(listbox)
{
  var vos,right_vo,i;
  vos= document.getElementsByName(listbox);
  if(vos==null || vos[0].length==0)
  {
  	return; 
  }
  //设为要可选状态
  right_vo=vos[0]; 
  var items=[]; 
  for(i=0;i<right_vo.options.length;i++)
  {
      right_vo.options[i].selected=true;
      items[i]=right_vo.options[i].value;
  } 
      var hashvo=new ParameterSet();
      hashvo.setValue("items",items);
      new Request({method:'post',asynchronous:false,onSuccess:itemsave,functionId:'1020060001'},hashvo);
      	
}

function itemsave(outparamters){
	var state=outparamters.getValue("state");
	if(state=='ok')
		alert("设置成功");
}

function additem(sourcebox_id,targetbox_id)
{
  var left_vo,right_vo,vos,i,r;
  vos= document.getElementsByName(sourcebox_id);

  if(vos==null)
  	return false;
  left_vo=vos[0];
  vos= document.getElementsByName(targetbox_id);  
  if(vos==null)
  	return false;
  right_vo=vos[0];
  for(i=0;i<left_vo.options.length;i++)
  {
    if(left_vo.options[i].selected)
    {
        var no = new Option();
    	no.value=left_vo.options[i].value;
    	no.text=left_vo.options[i].text;
    	for(r=0;r<right_vo.options.length;r++)
    	{
    	    if(no.value==right_vo.options[r].value)
    	      return false;
    	}
    	right_vo.options[right_vo.options.length]=no;
    }
  }
  
  //设为要可选状态
  /*
  for(i=0;i<right_vo.options.length;i++)
  {
     right_vo.options[i].selected=true;
  }
 	*/
 return true;	  	
}
</script>
<html:form action="/selfservice/addressbook/addressbookset">
<table width="700" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap >
		<bean:message key="selfservice.addressbook.addressbooksettitle"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
           <tr>
            <td width="100%" align="center" class="RecordRow" nowrap >
              <table cellspacing="0" cellpadding="0">
                <tr>
                 <td align="left"  width="46%">
                   <table align="center" width="100%" cellspacing="0" cellpadding="0">
                   <tr><td align="left" valign="top">
                   <bean:message key="static.target" />
                   </td></tr>
                   <tr>
                    <td align="center" valign="top">                      
                      <hrms:fielditemlist  name="addressBookConstantForm" usedflag="usedflag" setname="setname" collection="list" scope="session" memo="false"/>
                      <html:select property="left_fields" multiple="true" style="height:230px;width:100%;font-size:9pt" ondblclick="additem('left_fields','right_fields');">
                           <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                      </html:select>
                    </td>
                    </tr>
                   </table>
                </td>
                <td width="8%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button>
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="removeitem('right_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>
	           </html:button>     
                </td>         
                <td width="46%" align="right">
                <table align="center" width="100%" cellspacing="0" cellpadding="0">
                   <tr>
                    <td align="left" valign="top"> 
                    	<bean:message key="static.ytarget" />
                    </td>
                    </tr>
                    <tr><td valign="top">
                  <hrms:optioncollection name="addressBookConstantForm" property="str_valuelist" collection="selectedlist"/>
     	          <html:select  property="right_fields" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt" ondblclick="removeitem('right_fields');">
                        <html:options collection="selectedlist"  property="value" labelProperty="label"/>
                  </html:select>   
                  </td></tr>
                  </table>           
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap style="height:35px;" >
              &nbsp;&nbsp;
              <%--button默认是submit类型会提交页面 改为button类型 浏览器兼容 wangbs 20190321--%>
              <button type="button" class="mybutton" onclick="setselectitem('right_fields');"><bean:message key="button.ok"/></button>
          </td>
          </tr>   
</table>
</html:form>
