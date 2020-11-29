<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function sub(){
inSupportCommendForm.action="/performance/commend/insupportcommend/voteStatusAnalyse.do?b_init=init&opt=0"
inSupportCommendForm.submit();
}
function excelfile(){
	var p0201=document.getElementById("p0201").value;
	var In_paramters="p0201="+p0201; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'9010030033'},null);
}
function showfile(outparamters){
	if(outparamters!=null){
		var outName=outparamters.getValue("outName");
		if(outName!=null&&outName.length>0){
			//xus 20/4/30 vfs改造
			var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
		}
	}
}
//-->
</script>
  <html:form action="/performance/commend/insupportcommend/voteStatusAnalyse">
	<Br>
	<br>
	<logic:equal name="inSupportCommendForm" property="have" value="0">
	<p align="center">
	
	暂时没有结束的后备推荐记录
	</p>
	</logic:equal>
	<logic:notEqual name="inSupportCommendForm" property="have" value="0"><!-- 【5802】干部考察：结果分析/后备推荐结果分析和投票状况分析显示界面线太粗 jingq upd 2014.12.29 -->
	<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="padding:2 0 2 0;">
   	  <thead>
   	  <tr>
   	  	<td>
   	  		<input class="mybutton" type="button" value="民主推荐后备干部汇总表" onclick="excelfile()"/>&nbsp;
   	  	</td>
   	  </tr>
   	</table>
   	<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	 <tr>
            <td align="left" class="TableRow" colspan='5' nowrap>
      后备推荐
            <hrms:optioncollection name="inSupportCommendForm" property="finishCommendList" collection="list" />
			<html:select name="inSupportCommendForm" property="p0201" size="1" onchange="sub();" >
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>&nbsp;
	   	 	 </td>
         </tr>
  
           <tr>
           			<td align="center" class="TableRow" nowrap>投票数</td> 
           			<td align="center" class="TableRow" nowrap>人数</td>
           			
           </tr>
      </thead>
   
   <% int i=0; String className="trShallow"; %>
<logic:iterate id="element" name="inSupportCommendForm" property="voteStatusList" offset="0">
      
       
         <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
	   <tr class='<%=className%>' >
          <td align="right" class="RecordRow" width="30%" nowrap>
           <bean:write name="element" property="vote"/>
         </td>
          <td align="right" class="RecordRow" width="25%" nowrap>
           <bean:write name="element" property="personcount"/>
         </td>
        
            </tr>		    
</logic:iterate>
    </table>
</logic:notEqual>
   	  </html:form>