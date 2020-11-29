<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,				 
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<html>

  <script language='javascript'>
  //	var info=dialogArguments;

  	function exportFile()
  	{
  		var hashvo=new ParameterSet();
		hashvo.setValue("plan_id",'${evaluationForm.planid}');
  		if(document.form1.namerule[0].checked)
  		{
  			hashvo.setValue("namerule",'index');
  			hashvo.setValue("name",getEncodeStr(document.form1.nameindex.value));
  		}else if(document.form1.namerule[1].checked)
  		{
  			if(trim(document.getElementById('formulaVal').value)=='')
  				document.getElementById('formulaVal').value= getCookie('plansReportFormulaVal');

  			if(trim(document.getElementById('formulaVal').value)=='')
  			{
  				alert(SET_FORMULA_CONTENT);
  				return;
  			}
  			hashvo.setValue("namerule",'formula');
  			hashvo.setValue("name", getEncodeStr(document.getElementById('formulaVal').value));
  		}

		var request=new Request({method:'post',asynchronous:true,onSuccess:returnInfo_export,functionId:'9024000029'},hashvo);
}

 function returnInfo_export(outparamters)
 {
			var outName=outparamters.getValue("outName");
			var info=outparamters.getValue("info");
			alert(info);
			if(outName!='error')
			{
				//xus 20/4/30 vfs改造
				window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
			}
 }
  	function setEnable(theObj)
  	{
  		if(theObj.value=='formula' && theObj.checked)
  		{
  			document.form1.nameindex.disabled=true;
  			document.form1.formulaBt.disabled=false;
  		}
  		else if(theObj.value=='index' && theObj.checked)
  		{
  			document.form1.nameindex.disabled=false;
  			document.form1.formulaBt.disabled=true;
  		}

  	}
  	function formulaDef()
  	{
  		var strurl="/performance/evaluation/performanceEvaluation.do?br_reportFormula=link";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
		//var return_vo=window.showModalDialog(iframe_url,arguments,"dialogWidth=620px;dialogHeight=330px;resizable=yes;scroll=no;status=no;");
        var config = {
            width:620,
            height:330,
            type:'2'
        }
        modalDialog.showModalDialogs(iframe_url,'',config,formulaDef_m_ok);
  	}
  	function formulaDef_m_ok(return_vo){
        if(return_vo!=null && return_vo.ok==1){
            document.getElementById('formulaVal').value=return_vo.formulaVal;
        }
    }
  </script>
   
<hrms:themes />
  <body>
  <form name='form1'>
  	<input type="hidden" id="formulaVal">
    <table width="80%" align="center">
     <tr><td colspan="2" style="height:20px" align="center">
     
     </td>
	 </tr>
    <tr><td>
    	<fieldset style="width:100%" name="filterset">
			<legend>
				<bean:message key="jx.Report.file.namerule" />
			</legend>
			<table width="100%" border="0" align="center">
				<tr>
					<td align="left">
						<INPUT type="radio" name="namerule" value="index" onclick="setEnable(this)" checked/><bean:message key="lable.filename.index"/>
					</td>
					<td>
						<%
		         			FieldItem fielditem = DataDictionary.getFieldItem("E0122");
		         		%>	         			 			
						<select name="nameindex">
  							<option value=<bean:message key="b0110.label"/>>B0110 <bean:message key="b0110.label"/></option>
  							<option value=<%=fielditem.getItemdesc()%>>E0122 <%=fielditem.getItemdesc()%></option>
 						    <option value=<bean:message key="e01a1.label"/>>E01a1 <bean:message key="e01a1.label"/></option>
  							<option value="姓名">A0101 姓名</option>
  							<option value="人员编号">A0100 人员编号</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="left">
						<INPUT type="radio" name="namerule" value="formula" onclick="setEnable(this)"/><bean:message key="hmuster.label.expressions"/>
					</td>
					<td align="left">
						<input type="button" name="formulaBt" value="..." onclick="formulaDef();" disabled="disabled" Class="mybutton">
					</td>
				</tr>
			</table>
		</fieldset>	
    </td></tr>
    <tr>
    <td align='center' >    
     <input type='button' class="mybutton"   onclick='exportFile()' value='<bean:message key="button.export"/>'  />
 	 &nbsp;
 	 <input type='button' class="mybutton"   onclick='parent.window.close();' value='<bean:message key="lable.tz_template.cancel"/>'  />
    </td>
    </tr>    
    </table>
  </form>
  
  
  </body>
</html>
