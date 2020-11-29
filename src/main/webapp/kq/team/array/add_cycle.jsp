<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript"> 
   function add_cycle()
   {
      var valuer=document.getElementById("cycle_name").value;
      
      if(valuer!=null&&valuer.length>0)
      {
        kqClassArrayForm.action="/kq/team/array/cycle_array_data.do?b_trans=link&cycle_flag=add";       
        kqClassArrayForm.submit();
      }else
      {
        alert("周期班次不能为空！");
        
      }
   }
   function closeT()
   {
        kqClassArrayForm.action="/kq/team/array/cycle_array_data.do?b_cycle=link";       
        kqClassArrayForm.submit();
   }
   function splitLeng(){
		var o = document.getElementById('cycle_name');
		var str = o.value;
		if(str.length <= 10){ 
			return; 
		}
		var strMaxLeng = 20;
		var tempStr = "";
		var strLeng = 0;
		
		for(var i = 0;i < str.length;i++){
			if(str.charCodeAt(i)>255) {
				strLeng += 2;
			}else{
				strLeng++;
			}
			if(strLeng > 20){
				o.value = tempStr;
			}else{
				tempStr += str.charAt(i);
			}
		}
   }
</script> 
<body> 
<html:form action="/kq/team/array/cycle_array_data">
<table width="270" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr height="20">
       		<!--  <td width="10" valign="top" class="tableft"></td>
       		
          <td width="180" align=center class="tabcenter">新增周期班次</td>
       		<td width="10" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> --> 
       		<td  align=center class="TableRow">新增周期班次</td>            	      
          </tr> 
          <tr>
            <td  class="framestyle9">

               <br>
               <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" align="center" >
              <tr> 
                <td align="right" class="tdFontcolor" nowrap > 周期班名称 &nbsp; </td>
                
                <td align="left" class="tdFontcolor" nowrap> 
                 <html:text name="kqClassArrayForm" property="cycle_name" size="20" styleId="cycle_name" 
                 				styleClass="inputtext" maxlength="20" onkeyup="splitLeng()"/>&nbsp; 
                
                </td>
                
              </tr>
            </table>	            	
            </td>
          </tr>
             <br>
          <tr class="list3">
            <td align="left">&nbsp;
            
            </td>
          </tr>            
      
          
        <tr align="center" class="list3"> 
          <td style="height:35px;"> 
            <input type="button" name="b_mquery" value="确认" onclick="add_cycle();" class="mybutton">
               <input type="button" name="bc_clear" value="取消" class="mybutton" onclick="closeT();">
          </td>
          </tr>  
  </table>
  </html:form>
  </body>