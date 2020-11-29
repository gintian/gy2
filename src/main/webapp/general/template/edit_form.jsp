<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%
	String str="";
	String isEmail="";  //是否是从邮件进入的 
	if(request.getParameter("model")!=null&&request.getParameter("model").equalsIgnoreCase("yp"))
		str="&model=yp";
	if(request.getParameter("model")!=null&&request.getParameter("model").equalsIgnoreCase("myApply"))
		str="&model=myApply";
	request.setAttribute("str",str);
	String showCard="";  //是否从列表点击某条记录进入卡片界面，需定位记录
	if(request.getParameter("showCard")!=null)
		showCard=(String)request.getParameter("showCard");
	if(request.getParameter("isemail")!=null)
		isEmail=(String)request.getParameter("isemail");
	request.setAttribute("email",isEmail);
 %>

<script type="text/javascript">
<!--
	function test()
	{
	  var setname="detail.templet_"+${templateForm.tabid};
	  alert(setname);
	  var dataset=eval(setname);
	  alert(dataset.getFieldCount());
	  alert(dataset.getCurrent());
	}
	
   function pageset_beforeTabChange(TabSettabSet, stringoldName, stringnewName)
   {
	  /* if(stringoldName=="")
	      return ;
	   if(confirm('是否切换？'))
	   {
	       return "y";  //有返回值时，则不切换
	   } */
	   if(stringoldName=="")
	      return ;
	   var saveBt = window.frames('detail').document.getElementById('buttonsave'); 	   
	   if(saveBt!=null)
	   		saveBt.fireEvent("onClick");
   }   
   
   var a0100_="";
   var basepre_="";
   var ins_id_="";
   var showCard="<%=showCard%>";
   //进入卡片方式
   var infor_type = '${templateForm.infor_type}';
   if(infor_type=="1"){
	   a0100_ ='${templateForm.a0100}';
	   basepre_='${templateForm.basepre}';
	   ins_id_='${templateForm.ins_id}';
   }else if(infor_type=="2"){
	   a0100_ ='${templateForm.b0110}';
	   basepre_='${templateForm.b0110}';
	   ins_id_='${templateForm.ins_id}';
   }else if(infor_type=="3"){
       a0100_ ='${templateForm.e01a1}';
  	   basepre_='${templateForm.e01a1}';
  	   ins_id_='${templateForm.ins_id}';
   }
   function test(_basepre,_a0100,_ins_id)
   {
   		 a0100_=_a0100;
   		 basepre_=_basepre;
   		 ins_id_=_ins_id;
   		 showCard="";
   }
  
//-->
</script>
<body >
<html:form action="/general/template/edit_form">
<hrms:tabset name="pageset" width="100%" height="100%" type="true"> 
    <logic:iterate id="pagebo"  name="templateForm"  property="pagelist" indexId="index">
	  <hrms:tab name="${pagebo.pageid}" label="${pagebo.title}" visible="true" url="/general/template/edit_page.do?b_query=link${str}&pageno=${pagebo.pageid}&tabid=${pagebo.tabid}&sp_batch=${templateForm.sp_batch}&isemail=${email}">
      </hrms:tab>	
   </logic:iterate> 

</hrms:tabset>

</html:form>
</body>