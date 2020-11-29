<%@ page contentType="text/html; charset=UTF-8" %>
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
	function additem1(sourcebox_id,targetbox_id){
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName(sourcebox_id);
		
		  if(vos==null)
		    return false;
		  left_vo=vos[0];
		  vos= document.getElementsByName(targetbox_id);  
		  if(vos==null)
		    return false;
		  right_vo=vos[0];
		  var right_value="";
		  var flag = false;
		  for(i=0;i<right_vo.options.length;i++){
		      var ss = right_vo.options[i].value;
		      right_value=right_value+ss+",";
		  }
		  right_value=","+right_value+",";
		  for(i=0;i<left_vo.options.length;i++)
		  {
		    if(left_vo.options[i].selected)
		    {
		        var ss = left_vo.options[i].value;
		        ss=","+ss+",";
		        if(right_value.indexOf(ss)!=-1){
		          alert(SAME_ITEM_RESET_ITEM);
		          return false;
		        }
		    }
		  }
	     for(i=0;i<left_vo.options.length;i++){
	      if(left_vo.options[i].selected){
               var no = new Option();
               no.value=left_vo.options[i].value;
               no.text=left_vo.options[i].text;
               right_vo.options[right_vo.options.length]=no;
               flag=true;
           }
	    }
	    return flag;
	}
	function remove1(obj){
		var no = document.getElementById("no").value;
		var _no = no.split(",");
		for(var j=0;j<_no.length;j++){
			if(obj.value==_no[j]){
				return;
			}
		}
		var rights = document.getElementById("right_fields").options;
		for(var i=0;i<rights.length;i++){
		    if("c_subject"==obj.value){
		      continue;
		    }
			if(rights[i].value==obj.value){
				rights.remove(i);   				
			}
		}
	}
	function remove2(obj){
	   var selectitem="";
	   var rights=document.getElementById("right_fields").options;
	   var no = document.getElementById("no").value;
       var _no = no.split(",");
       for(var i=0;i<rights.length;i++){
         if(rights[i].selected){
            selectitem=selectitem+rights[i].value+",";
         }
       }
       selectitem=selectitem.substr(0,selectitem.length-1);
       var _selectitem=selectitem.split(",");
       for(var j=0;j<_no.length;j++){
            for(var n=0;n<_selectitem.length;n++){
	            if(_selectitem[n]==_no[j]){
	                alert("所选的指标正在分录中使用,不能移除");
	                return;
	            }
            }
        }
        for(var i=0;i<rights.length;i++){
            for(var n=0;n<_selectitem.length;n++){
                if("c_subject"==_selectitem[n]){
                    continue;
                }
                if(rights[i].value==_selectitem[n]){
                    rights.remove(i);                   
                }
            }
        }
	}
//-->
</script>
<base id="mybase" target="_self">
<html:form action="/gz/voucher/financial_voucher">
<table width="490px" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
<THEAD>
<tr>
<td class="TableRow_lrt">
凭证项目设置
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
                      	备选项目<input type="hidden" name ="no" size="30" value="${financial_voucherForm.no}" />
                    </td>
                    </tr>
                   <tr>
                    <td align="left" >
                      	<hrms:optioncollection name="financial_voucherForm" property="salaryList" collection="list" />
							<html:select property="salaryid" style="width:100%; font-size:9pt"
								onchange="changeSalarySet(this);">
								<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>
                    </td>
                    </tr>
                   <tr>
                     <td align="center">                 
                      <hrms:optioncollection name="financial_voucherForm" property="leftList" collection="list"/>
		              <html:select name="financial_voucherForm" size="10" property="left_fields" multiple="multiple" ondblclick="additem1('left_fields','right_fields');" style="height:250px; width:100%; font-size:9pt;">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        	</html:select>		
                  </td>
              </tr>
</table>
</td>
<td width="5%" align="center">
<html:button  styleClass="mybutton" property="b_addfield" onclick="additem1('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="remove2('right_fields');">
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
		              <html:select name="financial_voucherForm" size="10"  property="right_fields" multiple="multiple" ondblclick="remove1(this);" style="height:270px; width:100%; font-size:9pt;">
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