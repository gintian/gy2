<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.util.ArrayList"%>
<html>
<head>
<title>Insert title here</title>
</head>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<style>
div#treemenu {
 overflow:auto; 
width: 250px;
height: 304px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
 
 border: inset 1px #C4D8EE;
 BORDER-BOTTOM: #C4D8EE 1pt solid; 
 BORDER-LEFT: #C4D8EE 1pt solid; 
 BORDER-RIGHT: #C4D8EE 1pt solid; 
 BORDER-TOP: #C4D8EE 1pt solid; 
}

</style>
<script language='javascript' >
var info=dialogArguments;   // [0]: tablename  [1]:nbase



</script>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String s="";
	ArrayList db= userView.getPrivDbList();
	for (int i=0;i<db.size();i++)
	{
		if (s.equals("")){
			s= (String)db.get(i);
		}
		else {
			s= s+","+(String)db.get(i);	
		}			
	}
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<hrms:themes />
<body>
<html:form action="/gz/gz_accounting/piecerate/handselpeople">
<table>
<tr>
	<td></td>
	<td>
	<fieldset align="center" style="width:255px">
    		<legend ><bean:message key="lable.performance.preparePerMainBody"/></legend>    		
    		<table width='100%'  >
 			<tr>
 				<td>
 					 <bean:message key="columns.archive.name"/>&nbsp;<html:text name="pieceRateDetailForm" styleClass="text4" property="objName" size='20'/>
 		 			 <span style="vertical-align: bottom;"><a href='javascript:query()' > <img style="vertical-align: -24%;" src="/images/code.gif"  border=0 /></a></span>
 				</td>
 			</tr>   	
    		<tr>    

    			<td align='left' >
		     		<div id="treemenu"></div> 
		     	</td>
		     </tr>		     
		     
		     </table>
		                  
	</fieldset>
	</td>
	<td>
	
	 
	            
	            &nbsp;<input type='button' class='mybutton' value="<bean:message key="button.setfield.addfield"/>"  onclick="addMen();" />&nbsp;
	            <br>
	            <br>
	            &nbsp;<input type='button' class='mybutton' value="<bean:message key="button.setfield.delfield"/>"  onclick="delMen();" />&nbsp;
	       
	</td>
	<td>
	<fieldset align="center" style="width:255px">
    		<legend ><bean:message key="lable.performance.selectedMenPerMainBody"/></legend>
			<table><tr><td>			
    	   	   <select name="right_fields"  multiple="multiple" size="10"   style="height:330px;width:250px;font-size:9pt">   
            </select>             
            
                       
            </td></tr></table>	    
		                 
	</fieldset>
	</td>

</tr>
<tr>
	<td colspan='4' align="center" height="30px" valign="bottom" >             
	      	<input type='button' class='mybutton' value="<bean:message key='button.ok'/>" onclick="sub();" /> &nbsp; &nbsp;     
	        <input type='button' class='mybutton' value="<bean:message key='button.cancel'/>" onclick="goback();" />&nbsp;             
	</td>
</tr>

</table>
<html:hidden name="pieceRateDetailForm" property="topOrgDesc" />
</html:form>
</body>
<script language='javascript' >	
	//flag:-1 从人员库开始显示 0从顶层机构开始显示
    var m_sXMLFile	= "/gz/gz_accounting/piecerate/handImportPeople.jsp?flag=-1&id=0&nbase=x&s0100="+info[0];	
	var newwindow;
	var topOrgDesc = document.getElementById("topOrgDesc").value;
	var root=new xtreeItem("root",topOrgDesc,"","mil_body","组织机构","/images/unit.gif",m_sXMLFile);
	Global.defaultInput=1;
	Global.showroot=false;
	root.setup(document.getElementById("treemenu"));	
	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	function sub()
	{
		var objlist=new Array(); 		        
		if(document.pieceRateDetailForm.right_fields.options.length==0)
		{
			alert("请选择需要引入的对象！");
			return;
		}
		 vos= document.getElementsByName("right_fields");
		  if(vos==null)
		  	return false;
		  right_vo=vos[0];
		  for(i=right_vo.options.length-1;i>=0;i--)
		  {
		  	objlist.push(right_vo.options[i].value);
		  }
		  window.returnValue=objlist;
		  window.close();
	}

	
	//删除人员
	function delMen()
	{
		  vos= document.getElementsByName("right_fields");
		  if(vos==null)
		  	return false;
		  right_vo=vos[0];
		  var flag = 0;//标记为0，如果一直为0就是没有被选择的
		  for(i=right_vo.options.length-1;i>=0;i--)
		  {
		    if(right_vo.options[i].selected)
		    {
		    	//alert(i);
		    	flag = 1;
				right_vo.options.remove(i);
		    }
		  }
		  if(flag == 0)
		  {
				alert("请选择要删除的数据！");
				return;
		  }
	}
	
    //添加人员
	function addMen()
	{
		if(root.getSelected()=="")
		{
				alert("请选择！");
				return;
		}
			
		var temp_str=root.getSelected();
		temp_str=getDecodeStr(temp_str);
		var temps=temp_str.split(",");
		
		for(i=0;i<temps.length;i++)
		{
		    if(temps[i].length>0)
		    {
		    	var isExist=0;
		    	for(var j=0;j<document.pieceRateDetailForm.right_fields.options.length;j++)
		    	{
		    		if(document.pieceRateDetailForm.right_fields.options[j].value==temps[i].split("`")[1]+"."+temps[i].split("`")[0])
		    		{	isExist=1;
		    			break;
		    		}
		    	}
		    	if(isExist==1)
		    		continue;
		        var no = new Option();	
		    	no.value=temps[i].split("`")[1]+"."+temps[i].split("`")[0];
		    	no.text=temps[i].split("`")[2];
		    	document.pieceRateDetailForm.right_fields.options[document.pieceRateDetailForm.right_fields.options.length]=no;
		    }
		}
	}
	
	
	/** 查询 */
	function query()
	{
		if(document.pieceRateDetailForm.objName.value.length==0)
		{
			alert("请输入姓名信息!");
			return;
		}
		 var hashVo=new ParameterSet();
		 hashVo.setValue("name",getEncodeStr(document.pieceRateDetailForm.objName.value));
		 hashVo.setValue("dbname",'<%=s%>');
		 hashVo.setValue("planid",info[0]);
		 hashVo.setValue("opt",info[1]);
		 if(info[1]==5 || info[1]==8)
			hashVo.setValue("khObjCopyed",info[2]);
		 if(info[1]==2)
			hashVo.setValue("oldPlan_id",info[2]);			
		 else if(info[1]==1 || info[1]==9)
		 {
		 	 if(document.getElementById('accordPrivBox')!=null)
			 	hashVo.setValue("accordPriv",document.getElementById('accordPrivBox').checked+'');	
			 else
			 	hashVo.setValue("accordPriv",'false');	
		 }   	 		 
			 
		 var request=new Request({method:'post',asynchronous:false,onSuccess:return_ok,functionId:'9024000028'},hashVo);			
	}
	
	
	function return_ok(outparameters)
	{
		var orgLinks = outparameters.getValue("orgLinks");
		if(orgLinks.length==0)
		{
			alert("待选对象中没有找到"+document.pieceRateDetailForm.objName.value+"!");	
		}
		else
		{
			var findNode = false;
			for(var k=0;k<orgLinks.length;k++)
			{
				var orgLink = orgLinks[k];
				var temps=orgLink.split("/");
				var obj=root;
				for(var i=temps.length;i>=0;i--)
				{
					obj.expand();
					for(var j=0;j<obj.childNodes.length;j++)
					{
						if(obj.childNodes[j].text==temps[i])
						{
							obj=obj.childNodes[j];
							findNode = true;
							break;
						}
					}
				}
				obj.expand();				
			}
			if(findNode == false)	
				alert("待选对象中没有找到"+document.pieceRateDetailForm.objName.value+"!");	
		}
	}
	
	
	function goback()
	{	
		window.close();
	}

</script>


</html>