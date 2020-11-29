<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%> 
<%@taglib uri="/tags/struts-html" prefix="html"%> 
<%@taglib uri="/tags/struts-logic" prefix="logic"%> 
<%@taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%> 
<%@ page import="java.util.*,com.hjsj.hrms.actionform.performance.commend_table.CommendTableForm,
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.struts.constant.SystemConfig" %>
				
<% 
	CommendTableForm commendTableForm=(CommendTableForm)session.getAttribute("commendTableForm");
	ArrayList recommend_person_list=commendTableForm.getRecommend_person_list();
	String table1_status=commendTableForm.getTable1_status();
	String disabled="disabled";
	if(!table1_status.equals("2"))
		disabled="";
    int size=commendTableForm.getRecommend_person_list().size();
 %>
				
				
<html>
<head>
<hrms:themes />
<style type="text/css">
.ff1 {
	font-family: "宋体";
	font-size: 30px;
	font-style: normal;
	line-height: normal;
	font-weight: 600;
	text-decoration: none;
}
.RecordRowC {
	border: inset 1px #000000;
	BORDER-BOTTOM: #000000 1pt solid; 
	BORDER-LEFT: #000000 1pt solid; 
	BORDER-RIGHT: #000000 1pt solid; 
	BORDER-TOP: #000000 1pt solid;
	font-size: 12px;
	background-color:#ffffff;
}

.RecordRowC2 {
	border: inset 1px #000000;
	BORDER-BOTTOM: #000000 1pt solid; 
	BORDER-LEFT: #000000 0pt solid; 
	BORDER-RIGHT: #000000 1pt solid; 
	BORDER-TOP: #000000 0pt solid;
	font-size: 12px;
	background-color:#ffffff;
}
.RecordRowC3 {
	border: inset 1px #000000;
	BORDER-BOTTOM: #000000 1pt solid; 
	BORDER-LEFT: #000000 0pt solid; 
	BORDER-RIGHT: #000000 1pt solid; 
	BORDER-TOP: #000000 1pt solid;
	font-size: 12px;
	background-color:#ffffff;
}
.tt2 {
	font-family:  楷体_GB2312;
	font-size: 21px;
	text-decoration: none;
}
.tt3 {
	font-family: 楷体_GB2312;
	font-size: 24px;
	font-weight: 600;
	text-decoration: none;
}
.di {
	font-size: 14px;
	color: #FF0000;
	font-weight: 900;
}
.TEXT {
	BACKGROUND-COLOR:transparent;
	font-size: 15px;
	BORDER-BOTTOM: medium none; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
}
 .mybuttonBig{
	border:1px solid #84ADC9;
	background-image:url(/images/button.jpg);
	background-repeat:repeat-x;
	background-position:right;
	font-size:16px;
	line-height:18px;
	padding-left:1px;
	padding-right:1px;
	/*margin-left:1px;*/
	color:#36507E;
	background-color: transparent;	
	cursor: hand ; 	
 }
 .TableRowCommend {
	BACKGROUND-COLOR: #D7E9FF; 
	font-family:楷体_GB2312;
	font-size: 21px;  
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	height:50;
	font-weight: bold;	
	valign:middle;
}
.tt5{
  font-family:楷体_GB2312;
  font-size: 23px;
}
.RecordRowFONT {
	border: inset 1px #94B6E6;
	font-family:楷体_GB2312;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 23px;
	border-collapse:collapse; 
	height:38;
}
</style>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language='javascript' >
	var recommend_unit='${commendTableForm.recommend_unit}'
	
	
	function delete_obj(num)
	{
		document.getElementsByName('commended_'+num)[0].value='';
		document.getElementsByName('a0100_'+num)[0].value='';
	}
	
	function save(flag)
	{
		if(trim(recommend_unit).length==0)
		{
			alert("用户信息中推荐单位指标没有数据，无法操作!");
			return;
		}
		var hashvo=new ParameterSet();
		var limitNum=${commendTableForm.limitNum};
		 <% int n=0;%>
   var num=0;
   var sum=0;
   var arr = "";
   <logic:iterate id="element" name="commendTableForm" property="recommend_person_list" indexId="index"> 
      var obj = document.getElementsByName("recommend_person_list[<%=n%>].C11");
      if(obj)
      {
        if(obj[0].checked)
        {
           sum++;
           obj[0].value='1';
           arr+="`"+"<bean:write name="element" property="a0101_1"/>/<bean:write name="element" property="a0100_1"/>";
        }
        else
        {
          num++;
        }
      }
   <%n++;%>
   </logic:iterate>
   if(sum>limitNum)
   {
      alert("推荐人数不能超过班子目标人数！");
      return;
   }
		if(sum<limitNum)
		{
		     var msg="保存";
             if(flag=='1')
             {
                msg="完成";
             }
             if(flag=='2')
             {
               msg="提交";
             }
             if(!confirm("尚有空项，是否确认"+msg+"。"))
               return;
		}
		if(flag=='2'&&sum==limitNum)
		{
		    if(!confirm("请确认是否完成。"))
               return;
		}
		var arrlength=0;
		if(sum>0)
		{
		    var aff=arr.substring(1).split("`");
	    	for(var i=0;i<aff.length;i++)
	    	{
	    	    arrlength++;
		    	hashvo.setValue("commended_"+i,aff[i]);
	    	}
	    }
		hashvo.setValue("flag",flag);
		hashvo.setValue("limitNum",arrlength);
		hashvo.setValue("recommend_unit","${commendTableForm.recommend_unit}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfo,functionId:'30200710245'},hashvo);	
	}
	
	function returnInfo(outparamters)
	{
		var flag=outparamters.getValue("flag");
		if(flag=='1')
		{
			window.opener.location="/templates/attestation/unicom/performance.do?b_query=link";
			window.close();
			
		}
		else if(flag=='2')
		{
		  window.opener.parent.parent.document.location= "/templates/index/emlogon4.jsp";
		  window.close();
		}
		else
		{
			alert("保存成功!");
		}
		
	}
	
	function select_obj(index)
	{
		  var return_vo=select_org_emp_dialog4(1,2,1,1,1,1,"${commendTableForm.recommend_flag_item}","UN${commendTableForm.recommend_unit}","Usr","false");	
		  if(return_vo)
		  {
		 	 var obj_name="commended_"+index;
			 var obj_name2="a0100_"+index;
			 document.getElementsByName(obj_name)[0].value=return_vo.title
			 document.getElementsByName(obj_name2)[0].value=return_vo.content.substring(3);	
		  }
	}
	
	function returnBack()
	{
		    window.opener.location="/templates/attestation/unicom/performance.do?b_query=link";
			window.close();
	}
	
	

</script>

</head>
<body>
<html:form action="/performance/commend_table/commend_table">
<br>
 <table width="95%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
 <tr>
 <td colspan="5">
 <table  width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
  <thead>
   <tr>
  <td  class="TableRowCommend"  valign="middle" align="center" colspan="5">
  <font class="tt5">  民&nbsp;&nbsp;主&nbsp;&nbsp;推&nbsp;&nbsp;荐&nbsp;&nbsp;表（<logic:equal value="2" name="commendTableForm" property="recommend_time">第</logic:equal>二<logic:equal value="2" name="commendTableForm" property="recommend_time">轮</logic:equal>）</font>
  
  </td>
  </tr> 
   <tr>
  <td  class="TableRowCommend"  valign="middle" align="left" colspan="5">
  <br>
  <logic:equal value="1" name="commendTableForm" property="recommend_time">
  &nbsp;&nbsp;&nbsp;请您根据分公司发展及领导班子结构需要，推荐本公司领导班子成员人选。<br><br>
  &nbsp;&nbsp;&nbsp;推荐范围：省级分公司现班子成员、省级分公司部门和市级分公司正职。<br><br>
  &nbsp;&nbsp;&nbsp;推荐人数：不超过班子目标职数（${commendTableForm.limitNum}人），无合适人选可空缺。<br><br>
  </logic:equal>
  <logic:equal value="2" name="commendTableForm" property="recommend_time">
   &nbsp;&nbsp;&nbsp;现根据《民主推荐表（二）》推荐结果，进行第二轮推荐：<br><br>
  &nbsp;&nbsp;&nbsp;一 请在表中所列人员范围内推荐本单位领导班子成员人选；<br><br>
  &nbsp;&nbsp;&nbsp;二 推荐人数不超过本单位班子（正、副职）目标职数（${commendTableForm.limitNum}人），无合适人选可空缺。<br><br>
  </logic:equal>
  </td>
  </tr> 
  <tr>
  <td  class="TableRowCommend"  valign="middle" align="left" colspan="5">
  <br>
  &nbsp;&nbsp;&nbsp;说明：请您在认为合适的人选前打勾。<br><br>
  </td>
  </tr> 
  </thead>
  </table>
  </td>
  </tr>
  <% int i=0; %>
   <logic:iterate id="element" name="commendTableForm" property="recommend_person_list" indexId="index"> 
     <%if(i==0){%>
         <tr class="trShallow">
         <td align="left" class="RecordRowFONT" valign="middle">
         &nbsp;&nbsp;
         <logic:equal name="element" property="C11" value="1">
             <input type="checkbox" name="<%="recommend_person_list["+index+"].C11"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="table1_status" value="2">disabled</logic:equal>/>	 
        </logic:equal>
        <logic:equal name="element" property="C11" value="0">  
           <input  type="checkbox" name="<%="recommend_person_list["+index+"].C11"%>" value="1" <logic:equal name="commendTableForm" property="table1_status" value="2">disabled</logic:equal>/>	 
         </logic:equal>
         <bean:write name="element" property="a0101_1"/>
         </td>
     <%}else if(i%5==0&&i!=size){%>
     </tr>
        <%
        if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          
          <%} %>
           <td align="left" class="RecordRowFONT" valign="middle">
           &nbsp;&nbsp;
         <logic:equal name="element" property="C11" value="1">
             <input type="checkbox" name="<%="recommend_person_list["+index+"].C11"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="table1_status" value="2">disabled</logic:equal>/>	 
        </logic:equal>
        <logic:equal name="element" property="C11" value="0">
       
           <input  type="checkbox" name="<%="recommend_person_list["+index+"].C11"%>" value="1" <logic:equal name="commendTableForm" property="table1_status" value="2">disabled</logic:equal>/>	 
         </logic:equal>
         <bean:write name="element" property="a0101_1"/>
         </td>
     <%}else if((i+1)==size){%>
     
      <td align="left" class="RecordRowFONT" valign="middle">
       &nbsp;&nbsp;
         <logic:equal name="element" property="C11" value="1">
             <input type="checkbox" name="<%="recommend_person_list["+index+"].C11"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="table1_status" value="2">disabled</logic:equal>/>	 
        </logic:equal>
        <logic:equal name="element" property="C11" value="0">
           <input  type="checkbox" name="<%="recommend_person_list["+index+"].C11"%>" value="1" <logic:equal name="commendTableForm" property="table1_status" value="2">disabled</logic:equal>/>	 
         </logic:equal>
         <bean:write name="element" property="a0101_1"/>
         </td>
      </tr>
     <%}
     else{
     %>
      
        <td align="left" class="RecordRowFONT" valign="middle">
       &nbsp;&nbsp;
         <logic:equal name="element" property="C11" value="1">
             <input type="checkbox" name="<%="recommend_person_list["+index+"].C11"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="table1_status" value="2">disabled</logic:equal>/>	 
        </logic:equal>
        <logic:equal name="element" property="C11" value="0">
       
           <input  type="checkbox" name="<%="recommend_person_list["+index+"].C11"%>" value="1" <logic:equal name="commendTableForm" property="table1_status" value="2">disabled</logic:equal>/>	 
         </logic:equal>
        <bean:write name="element" property="a0101_1"/>
         </td>
      
     <% 
     }
     i++;
      %>
   </logic:iterate>
   <logic:equal value="1" name="commendTableForm" property="recommend_time">
 <tr >
           					<td align='left' colspan='5' > <Br>
           					<% if(!table1_status.equals("2")){%>
		           			<input type='button' value=' 保存 ' onclick="save('0')"  class='mybuttonBig'  >
		           			<input type='button' value=' 完成 ' onclick="save('1')"  class='mybuttonBig'  >
		           		<% } %>
		           		<input type='button' value=' 返回 ' onclick="returnBack()"  class='mybuttonBig'  >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
           					</td>
           				</tr>
           				</logic:equal>
           				<logic:equal value="2" name="commendTableForm" property="recommend_time">
           				<tr >
           					<td align='center' colspan='5' > <Br>
           					<% if(!table1_status.equals("2")){%>
           				<input type='button' value=' 确认提交 ' onclick="save('2')"  class='mybuttonBig'  >
           				<% } %>
           				</td>
           				</tr>
           				</logic:equal>
  </table>

</html:form>
</body>
</html>