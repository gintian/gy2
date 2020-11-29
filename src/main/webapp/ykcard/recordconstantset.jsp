<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<style type="text/css">
.RecordRowC {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
}
</style>
<hrms:themes></hrms:themes>
<script language="javascript">
 function gosearch(flag)
 {
    if(flag==0)
    {
       cardConstantForm.action="/ykcard/cardconstantset.do?b_cardset0=set";
       cardConstantForm.submit(); 
    }
    else if(flag==1)
    {
      cardConstantForm.action="/ykcard/mustconstantset.do?b_must=set";
      cardConstantForm.submit(); 
    }
 }
</script>
<html:form action="/ykcard/recordconstantset">
	<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="left" style="margin-left:5px;" class="TableRow" nowrap>
					薪酬表列表方式设置&nbsp;
				</td>
			</tr>
		</thead>		
		<tr>
		      <td height="50" class="RecordRowC common_border_color" nowrap >
		         <table width="90%" border="0" cellpadding="0" cellspacing="0">
		           <tr>
		             <td  width="15%" nowrap height="10">
		               &nbsp;
		              </td>
		              <td width="20%" align="right">
		               
		              </td>
			       <td width="45%" nowrap>
				
				</td>
				<td width="20%">
			         
			      </td>			       
		           </tr>
		           <tr>
		             <td  width="15%" nowrap height="30">
		               &nbsp;
		              </td>
		              <td width="20%" align="right">
		                &nbsp;选择显示的方式&nbsp;
		              </td>
			       <td width="45%" nowrap>
				<html:radio name="cardConstantForm" property="recardconstant"  value="0"/>&nbsp; 单层表头显示
				</td>
				<td width="20%">
			         <input type="button" name="b_1" class="mybutton" value="设置" onclick="gosearch(0)">
			      </td>			       
		           </tr>
		            <tr>
		             <td  width="15%" nowrap height="30">
		               &nbsp;
		              </td>
		              <td width="20%" align="right">	&nbsp;	                
		              </td>
			       <td width="45%" nowrap>
				<html:radio name="cardConstantForm" property="recardconstant"  value="1"/>&nbsp; 高级花名册显示
				</td>
				<td width="20%">
			         <input type="button" name="b_1" class="mybutton" value="设置" onclick="gosearch(1)">
			      </td>			       
		           </tr>
		           <tr>
		             <td  width="15%" nowrap height="10">
		               &nbsp;
		              </td>
		              <td width="20%" align="right">
		               
		              </td>
			       <td width="45%" nowrap>
				
				</td>
				<td width="20%">
			         
			      </td>			       
		           </tr>
		         </table>
		        </td>
		        
	        </tr>		
		<tr>
			<td align="center" style="height: 35px"  class="RecordRow" nowrap>
				&nbsp;&nbsp;
				<input type="submit" name="b_save" class="mybutton" value="&nbsp;保存&nbsp;">
			</td>
		</tr>
	</table>

</html:form>