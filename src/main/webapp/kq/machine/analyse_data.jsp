<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript">
function countdata()
{
	var count_type;
	
	
	      var count_start=document.dataAnalyseForm.start_date.value;
	      var count_end=document.dataAnalyseForm.end_date.value;
	      if(count_start=="")
	      {
	         alert("请选择计算开始时间！");
                 return;
	      }else if(count_end=="")
	      {
	          alert("请选择计算结束时间！");
                  return;
	      }else
	      {
	          var thevo=new Object();
			  thevo.start=count_start;
              thevo.end=count_end;
			  window.returnValue=thevo;
              window.close();
	      }	 
	      
}
function MusterInitData()
{
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
}
</script>
<html:form action="/kq/machine/analyse_card">  
<div  class="fixedDiv2" style="height: 100%;border: none">
 <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center" valign="middle" >   
                   <tr height="20">
       		       <!-- <td width=1 valign="top" class="tableft1"></td>
       		       <td width=130 align=center class="tabcenter"><bean:message key="kq.countdate.width"/></td>   
       		       <td width=10 valign="top" class="tabright"></td>
       		       <td valign="top" class="tabremain" width="300"></td> --> 
       		       <td align=center class="TableRow"><bean:message key="kq.countdate.width"/></td>         		           	      
                   </tr> 				  
                   <tr>
				   <td width="100%" height="100"   class="framestyle9" align="center">				   
		           <table>
					<tr>
				     <td height="50">
					      <fieldset align="center" style="width:100%;">
    		                <legend >提示</legend>
                            <table border="0" cellspacing="0"  cellpadding="0" width="90%">
                             <tr>
                              <td>
							     如果要分析的数据量很大时，请尽量在数据库使用<br>
								 人数较少的时候进行，这样可以加快分析的速度！
							  </td>
							  </tr>
							  </table>
                          </fieldset>
					  </td>
				     </tr>
		             <tr>
		               <td>
		                 <html:hidden name="dataAnalyseForm" property="a_code" styleClass="text"/> 
						 <html:hidden name="dataAnalyseForm" property="nbase" styleClass="text"/> 
		                 <bean:message key="label.query.from"/>&nbsp;
		                  &nbsp;<html:text name="dataAnalyseForm" property='start_date' size="10"  styleClass="text4"  onfocus="setday(this);" readonly="true"/> 
		                   &nbsp;<bean:message key="label.query.to"/>&nbsp;
		                 <html:text name="dataAnalyseForm" property='end_date' size="10"  styleClass="text4"  onfocus="setday(this);" readonly="true"/>                             
		                </td>
		                <tr>
		              </table> 
		         </tr>
		         <tr>
		            <td  height="40" align="center" >		                
	                         <input type="button" name="btnreturn" value='<bean:message key="kq.register.kqduration.ok"/>' onclick="countdata();" class="mybutton">
		                <input type="button" name="btnreturn" value='<bean:message key="kq.register.kqduration.cancel"/>' onclick="window.close();" class="mybutton">
		             </td>
		         </tr>
		       </table>    
		       </div>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正在接收数据请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" style="border:1px solid #000000" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
 MusterInitData();	
</script>