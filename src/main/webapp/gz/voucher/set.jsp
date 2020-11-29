<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<script language="Javascript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--
var value="";
	function changeSalarySet(flag){
	    var hashvo=new ParameterSet();
	    hashvo.setValue("salaryid",flag.value);
		var request=new Request({method:'post',asynchronous:false,
		parameters:null,onSuccess:changeSalarySetOk,functionId:'3020073035'},hashvo);
	}
	function changeSalarySetOk(outparameters)
	{
	   var leftList=outparameters.getValue("leftList");
	   AjaxBind.bind(financial_voucherForm.left_fields,leftList);
	}
	function ok(){
	 var vos= document.getElementById("right_fields");       
     if(vos.length!=0)
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
          for(var j=i+1;j<vos.length;j++)
          {
          	if(valueS.toUpperCase()==vos.options[j].value.toUpperCase())
          	{
          		alert(SAME_SUBSET_RE_SELECT);
          		return false;
          	}
          }
        }       
     }
    var code_fields="";  
    var code_fields1="";        
    for(var i=0;i<vos.length;i++)
    {
      var valueS=vos.options[i].value;   
      var valueS1=vos.options[i].text;         
      code_fields+=valueS+",";
      code_fields1+=valueS1+",";
    }
    var code_fields2=new Array();  
    code_fields2[0]=code_fields.substring(0,code_fields.length-1);
    code_fields2[1]=code_fields1.substring(0,code_fields1.length-1);
    window.returnValue=code_fields2;
	window.close(); 
	}
//-->
</script>
<base id="mybase" target="_self">
<html:form action="/gz/voucher/financial_voucher">
<table width="490px;" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
<THEAD>
<tr>
<td class="TableRow_lrt">
分组汇总指标
</td>
</tr>
</THEAD>
<tr>
<td class="RecordRow">
<table>
<tr>
<td width="20%">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
               <tr>
                    <td align="left">
                      	备选项目
                    </td>
                    </tr>
                   <tr>
                    <td align="left">
                      	<hrms:optioncollection name="financial_voucherForm" property="salaryList" collection="list" />
							<html:select property="salaryid" style="width:100%;font-size:9pt"
								onchange="changeSalarySet(this);">
								<html:options collection="list" property="dataValue"
								labelProperty="dataName" />
						</html:select>
                    </td>
                    </tr>
                   <tr>
                     <td align="center">                 
                      <hrms:optioncollection name="financial_voucherForm" property="leftList" collection="list"/>
		              <html:select name="financial_voucherForm" size="10" property="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:250px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        	</html:select>		
                  </td>
              </tr>
</table>
</td>
<td width="5%" align="center">
<html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
</td>
<td width="20%" valign="top">
<bean:message key="gz.bankdisk.selectedfield"/>&nbsp;&nbsp;
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
                  <tr>
                  <td width="100%" align="left">
                     
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
                   <hrms:optioncollection name="financial_voucherForm" property="rightList" collection="list"/>
		              <html:select name="financial_voucherForm" size="10" property="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:270px;width:100%;font-size:9pt">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
                  </td>
                  </tr>
                  </table> 
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td  class="RecordRowP" nowrap align="center" style="height:35px;">
<input type="button" name="open" class="mybutton" value="确定" onclick="ok();"/>
<input type="button" name="cancel" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close()"/>
</td>
</tr>
</table>
</html:form>