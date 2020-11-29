<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.hjsj.sys.FieldSet"%>
<%@ page import="com.hjsj.hrms.actionform.standardduty.ItemOptionForm"%>
<%@ page import="com.hjsj.hrms.businessobject.standarduty.DutyXmlBo"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<hrms:themes></hrms:themes>

<logic:equal value="ok" name="itemOptionForm" property="submitflag">
     <script>
        // alert("保存成功！");
        parent.window.close();
     </script>
</logic:equal>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script>

   function defaultselect(target,index){
	  var selectObj= document.getElementById("select"+index);
	   var options=selectObj.getElementsByTagName("option");
	   for(var key in options){
		      if(options[key].value==target){
			        options[key].selected="selected";
			        return;
		      }
	   }
   }
  function setRelevantItem(f){
	  
	  if(f == 'H00'){
		  var theurl="/system/options/standardduty_duty_item.do?b_setitem=H00";
	  }else{
		  var t=document.getElementById("select"+f).value;
		  if(t=="")
			  return;
		  var theurl="/system/options/standardduty_duty_item.do?b_setitem="+t+"-"+f;
	  }
	  var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	  //Ext window对象替换showModalDialog wangbs  2019年3月11日15:21:19
      setRelevanItemWin(iframe_url);

//       var config = {
//           width:525,
//           height:530,
//           type:'1'
//       };
//       modalDialog.showModalDialogs(iframe_url,'SetFiled_win',config,function () {
//           reload.click();
//       });
//
//
// //	  var return_vo= window.showModalDialog(iframe_url, 'SetFiled_win',
// //				"dialogWidth:525px; dialogHeight:530px;resizable:no;center:yes;scroll:false;status:no");
// //		if(return_vo=="ok"){
// //			reload.click();
// //		}
  }
  function setRelevanItemWin(iframe_url){
      var iTop = (window.screen.height-30-510)/2;       //获得窗口的垂直位置;
	  var iLeft = (window.screen.width-10-530)/2;        //获得窗口的水平位置;
	  window.open(iframe_url,'','height=510, width=530,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
  }
  //open 弹窗回调方法  wangb 20190312
  function setRelevanItemReturn(returnValue){
  	if(returnValue=='ok'){
        reload.click();
    }
  }
  
  function changeitem(index){
	  document.getElementById("showitem"+index).innerText="";
  }

  function saveinfo(){
	   document.itemOptionForm.action="/system/options/standardduty_duty_item.do?b_saveduty=link";
	   document.itemOptionForm.submit();
  }
</script>
 
<body scroll=no >
<html:form action="/system/options/standardduty_duty_item.do" >
<div align="center">
  <a href="/system/options/standardduty_duty_item.do?b_search=reload&submit=" style="display:none" id="reload" >link</a>
	   <div class="Text4" style="width:670px; height: 420px;  margin-top: 5px; margin-bottom: 8px; overflow: auto;padding:0px;border-top:0px;">
	   	    
		   <table width="100%" border="0" cellspacing="0" align="center" cellpadding="0"
				class="ListTable">
		         <tr>
		                <td align="center" style="border-left:0px;" class="TableRow"  nowrap>基准岗位信息集</td>     
		                <td align="center"  class="TableRow"  nowrap>岗位信息集</td>
		                <td align="center"  class="TableRow"  nowrap>指标对应关系</td>
		                <td align="center" style="border-right:0px;" class="TableRow"  nowrap>操作</td>
		         </tr>
		   
		    <%
		        int index=0;
		        FieldSet fs=null;
		    %>
		       
		      <logic:iterate id="var" name="itemOptionForm" property="sduty" >
		                   <%     
		                         fs=(FieldSet)pageContext.getAttribute("var");
		                         String fieldsetid=fs.getFieldsetid();
		                         pageContext.setAttribute("fieldsetid",fieldsetid);
		                   %>
		          <tr>
		            
		           <td class="RecordRow" style="border-left:0px;"  nowrap align="right">
		                 ${var.fieldsetdesc} 
		           </td> 
		            <td class="RecordRow"  nowrap align="center"> 
		                   <html:select property='<%="targetids["+index+"]" %>' name="itemOptionForm" styleId='<%="select"+fieldsetid%>' onchange="changeitem('${fieldsetid}');">
		                        <html:option value=""></html:option>
		                        <html:optionsCollection name="itemOptionForm" property="duty" value="fieldsetid" label="fieldsetdesc"/>
		                   </html:select>
		                   <script type="text/javascript">
		                     defaultselect("<bean:write name="itemOptionForm" property='<%="relevantset."+fieldsetid+".target" %>'/>",'${fieldsetid}');
		                   </script>
		             </td>
		             <td class="RecordRow"  nowrap  align="left"> 
		               <div id='<%="showitem"+fieldsetid %>' >
			             <bean:define id="itemlist" name="itemOptionForm" property='<%="relevantset."+fieldsetid+".field" %>'/>
			             <logic:iterate id="var" name="itemlist">
			                   ${var }<br/>
			             </logic:iterate>
			           </div>
		             </td>
		             <td class="RecordRow" style="border-right: 0px;" nowrap align="center">
		                <a href="###"  onclick="setRelevantItem('${fieldsetid}');">设置</a>
		             </td>
		          </tr>
		          <%index++; %>
		      </logic:iterate>
		      
		           <tr>
		           <td class="RecordRow" style="border-left: 0px;" colspan=2  nowrap align="center">
		                                                         多媒体分类对应设置
		           </td> 
		            
		             <td class="RecordRow" style="border-left: 0px; padding-left:20px;" nowrap  align="left"> 
		               <div id='showitemH00'>
		                   <logic:notEmpty name="itemOptionForm" property="relevantset.H00">
		                       <bean:define id="itemlist" name="itemOptionForm" property="relevantset.H00.field"/>
		                       <logic:iterate id="var" name="itemlist">
				                   ${var }<br/>
				             </logic:iterate>
		                   </logic:notEmpty>
				             
				           </div>
		             </td>
		             <td class="RecordRow" style="border-right: 0px;" nowrap align="center">
		                <a href="###"  onclick="setRelevantItem('H00');">设置</a>
		             </td>
		          </tr>
		      
		   </table>
	    </div>  
	    <center><button class="mybutton" onclick="saveinfo()"><bean:message key="button.save"/></button></center>
</div>
</html:form>

</body>