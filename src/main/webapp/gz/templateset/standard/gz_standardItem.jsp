<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">

<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.gz.templateset.standard.SalaryStandardForm,
				 com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hjsj.hrms.utils.PubFunc,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant" %>

<%
	 SalaryStandardForm salaryStandardForm=(SalaryStandardForm)session.getAttribute("salaryStandardForm");
	 String title=salaryStandardForm.getTitle();
	 GzStandardItemVo vo=(GzStandardItemVo)salaryStandardForm.getGzStandardItemVo();
	 String hfactor=vo.getHfactor();
	 String vfactor=vo.getVfactor();
	 String s_hfactor=vo.getS_hfactor();
	 String s_vfactor=vo.getS_vfactor();
     int h_bottomColumn_num=vo.getH_bottomColumn_num();   //横
     int v_bottomColumn_num=vo.getV_bottomColumn_num();   //纵
	 int size=vo.getGzItemList().size();
	 
	 String m_standardID=salaryStandardForm.getStandardID();
	 if(m_standardID==null)
	 	m_standardID="";
	 m_standardID=PubFunc.encrypt(m_standardID);
	 
%>

<html>
  <head>
    
  </head>
  <link href="/gz/templateset/standard/tableLocked.css" rel="stylesheet" type="text/css">
  <hrms:themes />
  <script language='javascript' >
  
  var h_num=<%=h_bottomColumn_num%>
  var v_num=<%=v_bottomColumn_num%>
  var size=<%=size%>
  
  function go_left(ite){
	    var temp_str = ite.name;
	    var post = temp_str.indexOf("[");
	    var post2 = temp_str.indexOf("]");
	    var temp_str1 = temp_str.substring(post+1,post2);
	    if(temp_str1*1>0)
	    {
		    var next_item = "gzItemList["+(temp_str1*1-1)+"].value";
		    var new_object=$(next_item);
	    	new_object.focus();
	    }
	  }
	  
 	 function delete_text(ite,event){
		 event.returnValue =false;//屏蔽backspace  xiegh 20170512 bug13952
	     ite.value = '';//按backspace键，将text置空 
	  }
 	 
	function go_right(ite){
		var temp_str = ite.name;
	    var post = temp_str.indexOf("[");
	    var post2 = temp_str.indexOf("]");
	    var temp_str1 = temp_str.substring(post+1,post2);
	    if(temp_str1*1<size-1)
	    {
		    var next_item = "gzItemList["+(temp_str1*1+1)+"].value";
		    var new_object=$(next_item);
	    	new_object.focus();
	    }
	  }
	  
	  
	function go_up(ite){
	 	var temp_str = ite.name;
	    var post = temp_str.indexOf("[");
	    var post2 = temp_str.indexOf("]");
	    var temp_str1 = temp_str.substring(post+1,post2);
	    if(h_num==0)
	    	h_num=1;
	    if((temp_str1*1)>=h_num)
	    {
		    var next_item = "gzItemList["+(temp_str1*1-h_num)+"].value";
		    var new_object=$(next_item);
	    	new_object.focus();
	    }
	  }
	  
	  
	function go_down(ite){
	
	 	var temp_str = ite.name;
	    var post = temp_str.indexOf("[");
	    var post2 = temp_str.indexOf("]");
	    var temp_str1 = temp_str.substring(post+1,post2);
		if(h_num==0)
	    	h_num=1;
	    if((temp_str1*1)<size-h_num)
	    {
		    var next_item = "gzItemList["+(temp_str1*1+h_num)+"].value";
		    var new_object=$(next_item);
	    	new_object.focus();
	    }
	  }
  
  
  
  
  
  
  
  
  
  function checkNum(obj,decimalWidth)
  {
  	if(trim(obj.value).length>0)
  	{
	  	 var myReg =/^(-?\d+)(\.\d+)?$/
		 if(!myReg.test(obj.value)) 
		 {
		 	alert(GZ_TEMPLATESET_INPUTNUMBER+"！");
		 	obj.value="";
		 	obj.focus();
		 	return;
		 }
		 
		 if(obj.value.indexOf(".")!=-1&&decimalWidth!=0)
		 {
		 	if(obj.value.substring(obj.value.indexOf(".")+1).length>decimalWidth)
		 	{
		 		alert(GZ_TEMPLATESET_MAINTAIN+decimalWidth+GZ_TEMPLATESET_DECIMAL+"!");
		 		obj.focus();
		 		return;
		 	}
		 }
	}
  }
  
  function setName()
  {
       <logic:equal name="salaryStandardForm" property="opt" value="edit" >
      	  document.salaryStandardForm.action="/gz/templateset/standard.do?b_saveStandard=save";
       </logic:equal>
       <logic:equal name="salaryStandardForm" property="opt" value="new" >
 	       document.salaryStandardForm.action="/gz/templateset/standard.do?br_setName=init";
       </logic:equal>
    
      	document.salaryStandardForm.submit();
  	
  }
  
  function  updateColumn(type)
  {
  		var infos=new Array();
  	    var thecodeurl="/gz/templateset/standard.do?b_updateColumn=update`type="+type;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	    if(isIE6()){
	    var return_value= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:630px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");
	    }else{
	    var return_value= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:600px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");
	    }
        	 
		if(return_value!='1')
		{
  			document.salaryStandardForm.action="/gz/templateset/standard.do?b_initItem=init&optType=1&opt=${salaryStandardForm.opt}&m_standardID=<%=m_standardID%>&standardID=${salaryStandardForm.standardID}";
  			document.salaryStandardForm.submit();
  		}
  } 
  
  
  function up()
  {
	  
	  <logic:equal name="salaryStandardForm" property="opt" value="edit" >
	  	//history.go(-1);//xiegh bug22983 20170515 
		  	document.salaryStandardForm.action="/gz/templateset/standard.do?b_query=query1&pkg_id=<bean:write name="salaryStandardForm" property="pkg_id" filter="true"/>";
	  		document.salaryStandardForm.submit();
	  </logic:equal>
	  <logic:equal name="salaryStandardForm" property="opt" value="new" >	
		  	 document.salaryStandardForm.action="/gz/templateset/standard.do?b_add=query&opt=new";
	  		 document.salaryStandardForm.submit();
	  </logic:equal>
	
  }
  
  
  function produceExcel()
  {
          <%if(request.getParameter("isedit")!=null&&request.getParameter("isedit").equals("0")){ %>
  		document.salaryStandardForm.action="/gz/templateset/standard.do?b_executeExcel=save&opt=edit&isedit=0&standardID=${salaryStandardForm.standardID}";
  		<%}else{%>
  		  document.salaryStandardForm.action="/gz/templateset/standard.do?b_executeExcel=save&opt=edit&standardID=${salaryStandardForm.standardID}";
  		  <%}%>
  		document.salaryStandardForm.submit();
  
  
  }
  
  function importExcel(){
	  var url="/gz/templateset/standard.do?b_importItemsExcel=link"; 
		var parameter = ""; 
		 var iTop = (window.screen.availHeight-30-140)/2;       //获得窗口的垂直位置;
		   var iLeft = (window.screen.availWidth-10-395)/2;           //获得窗口的水平位置;
		openWin("/module/gz/salarystandard/inputStandardItems.html?standardID=${salaryStandardForm.standardID}&pkg_id=<bean:write name="salaryStandardForm" property="pkg_id" filter="true"/>",parameter,"width=395, height=140,top="+iTop+",left="+iLeft+"resizable=no,center=yes,scroll=no,status=no");
  }

  function openWin(url,text,winInfo){  
	    var winObj = window.open(url,text,winInfo);  
}  
  function importExcelReload(text){  
	  if(text=='y'){
		  alert('导入成功');
		  document.salaryStandardForm.action="/gz/templateset/standard.do?b_initItem=init&opt=edit&m_standardID=<%=m_standardID%>&standardID=${salaryStandardForm.standardID}&isedit=1";
	  	  document.salaryStandardForm.submit();
	  }
  }
  
  <%
  if(request.getParameter("b_executeExcel")!=null&&request.getParameter("b_executeExcel").equals("save"))
  {
  %>
  var fieldName = getDecodeStr(${salaryStandardForm.filename});
  var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
  <%
  }
 
  %>
  
  </script>
  
  <body>
  <html:form action="/gz/templateset/standard">
  ${salaryStandardForm.title}
  	
  	<div id="tbl-container"  style='position:absolute;left:5<%=(title!=null&&title.length()>0?"top:10":";top:23")%>'  >
  
  ${salaryStandardForm.gzStandardItemHtml}
 
 	</div>
 
  
  
  <script language='javascript'>
  		
		document.write("<div id='ff' style='position:absolute;left:5;top:"+(document.body.clientHeight*0.92)+"'  >");
  </script>
	  <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
	  <tr>
	    <td align='center' >
	      <input type="button" class="mybutton" value="<bean:message key="static.back"/>" onclick="up()" /> 
	      <%if(request.getParameter("isedit")!=null&&request.getParameter("isedit").equals("0")){ %>
	      <%}else{ %>
	      <input type="button" class="mybutton" value="<bean:message key="kq.emp.button.save"/>" onclick="setName()" />
	      <% 
	       if(hfactor!=null&&hfactor.length()>0)
	      		out.print("<input type='button' class='mybutton' value='"+ResourceFactory.getProperty("gz.formula.adddel.cross.bar")+"' onclick='updateColumn(0)' />");
	       if(s_hfactor!=null&&s_hfactor.length()>0)
	      		out.print("<input type='button' class='mybutton' value='"+ResourceFactory.getProperty("gz.formula.adddel.son.cross.bar")+"' onclick='updateColumn(1)' />");
	       if(vfactor!=null&&vfactor.length()>0)
	      		out.print("<input type='button' class='mybutton' value='"+ResourceFactory.getProperty("gz.formula.adddel.columns")+"' onclick='updateColumn(2)' />");
	       if(s_vfactor!=null&&s_vfactor.length()>0)
	      		out.print("<input type='button' class='mybutton' value='"+ResourceFactory.getProperty("gz.formula.adddel.son.columns")+"' onclick='updateColumn(3)' />");
	      
	      %>
	      <%}  %>
	      <logic:equal name="salaryStandardForm" property="opt" value="edit" >
	      <input type="button" class="mybutton" value="<bean:message key="button.createescel"/>" onclick="produceExcel()" />
	      
	      <%if(request.getParameter("isedit")!=null&&request.getParameter("isedit").equals("0")){ %>
	      <%}else{ %>
	      <input type="button" class="mybutton" value="导入Excel" onclick="importExcel()" />
	       <%} %>
	 	  </logic:equal>
	    </td>
	  </tr>
	</table>
	</div>

  
  </html:form>
  </body>
  <script type="text/javascript">
var aa=document.getElementsByTagName("input");
for(var i=0;i<aa.length;i++){
	if(aa[i].type=="text"){
		aa[i].className="inputtext";
	}
}
</script>  
</hrml>