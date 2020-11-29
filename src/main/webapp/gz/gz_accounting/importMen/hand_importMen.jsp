<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
<title>Insert title here</title>
</head>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<style>
div#treemenu {
background-color: #FFFFFF;
BORDER-BOTTOM:#94B6E6 1pt inset; 
BORDER-COLLAPSE: collapse;
BORDER-LEFT: #94B6E6 1pt inset; 
BORDER-RIGHT: #94B6E6 1pt inset; 
BORDER-TOP: #94B6E6 1pt inset; 
width: 250px;
height: 295px;
overflow: auto;
}

</style>
<script language='javascript' >
var info=dialogArguments;   // [0]: tablename  [1]:nbase



</script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<body>


<html:form action="/gz/gz_accounting/gz_table">
<table style="margin-top:-6px;width:690px;margin-left:-3px;">
<tr>

	<td>
	<fieldset align="center" style="width:100%;">
    		<legend ><bean:message key="wd.lawbase.standbypersonnel"/></legend>
    		
    		<table width='100%'  >
    		<tr><td align="right">
    		 <bean:message key="columns.archive.name"/>&nbsp;<Input type='text' name='a_name'  size='20' id="selectname" onkeyup="showDateSelectBox('selectname')" class="inputtext"/>
    		 </td><td width='35%' align="left" style="position:relative">
    		 <span style="position: absolute;top:4px;">
    		  <a href='javascript:query()' > <img  src="/images/code.gif"  border=0 /></a>
    		  </span>
    		</td></tr>
    		<tr><td colspan='2' align='left' >
		     <div id="treemenu" style="width:300px;font-size:9pt;" class="complex_border_color"></div> 
		     </td></tr></table>
		                  
	</fieldset>
	</td>
	<td>
	
		  &nbsp;<html:button  styleClass="mybutton" property="b_addfield"  onclick='addMen()'  >
            		     <bean:message key="button.setfield.addfield"/> 
	            </html:button >
	            <br>
	            <br>
	           &nbsp;<html:button  styleClass="mybutton" property="b_delfield" onclick='delMen()'  >
            		     <bean:message key="button.setfield.delfield"/>    
	            </html:button >	
				
	</td>
	<td style="padding-top:9px; ">
	<fieldset align="center" style="width:100%;">
    		<legend ><bean:message key="wd.lawbase.alreadypickpersonnel"/></legend>
			<table><tr><td>
			
		    <select name="right_fields" multiple="multiple" size="10"   style="height:330px;width:300px;font-size:9pt">
             </select>
             
            </td></tr></table>
		    
		                 
	</fieldset>
	</td>

</tr>
<tr>
	<td colspan='4' align=center >
		 <html:button  styleClass="mybutton" property="enter" onclick='sub()'  >
            		     <bean:message key="button.ok"/>
	            </html:button >	
	      <html:button  styleClass="mybutton" property="cancel" onclick='goback()'  >
            		     <bean:message key="button.cancel"/>
	            </html:button >	 
	</td>
</tr>

</table>
<div id="date_panel" style="display:none;">

		<select id="date_box" name="contenttype"  onblur="Element.hide('date_panel');"  multiple="multiple" size="6" style="width:270" ondblclick="setSelectValue();">
        </select>
	 </div>
</body>
<script language='javascript' >
    var m_sXMLFile	= "/gz/gz_accounting/importMen/handImportMen_tree.jsp?flag=-1&id=0&nbase="+info[1]+"&tablename="+info[0]+"&isSalaryManager="+info[2];		
	var newwindow;
	var root=new xtreeItem("root","选择人员","","mil_body","选择人员","/images/add_all.gif",m_sXMLFile);
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
		if(document.accountingForm.right_fields.options.length==0)
		{
			alert("请选择需要引入的人员！");
			return;
		}
		setselectitem('right_fields');
		
		document.getElementsByName("enter")[0].disabled=true;
		document.accountingForm.action="/gz/gz_accounting/gz_table.do?b_handImportMen=import&importtype=0";
		document.accountingForm.submit();
	}
	
	<%
	if(request.getParameter("b_handImportMen")!=null&&request.getParameter("b_handImportMen").equals("import")){
	%>
		returnValue="1";
		window.close();
	<%	
	}	
	%>
	
	
	
	//删除人员
	function delMen()
	{
		  vos= document.getElementsByName("right_fields");
		  if(vos==null)
		  	return false;
		  right_vo=vos[0];
		  for(i=right_vo.options.length-1;i>=0;i--)
		  {
		    if(right_vo.options[i].selected)
		    {
		    	//alert(i);
				right_vo.options.remove(i);
		    }
		  }
	}
	
    //添加人员
	function addMen()
	{
		if(root.getSelected()=="")
		{
				alert("请选择人员！");
				return;
		}	
		var temp_str=root.getSelected();
		var temps=temp_str.split(",");
		for(var i=0;i<temps.length;i++)
		{
			if(temps[i].length>0&&temps[i].split("/")[temps[i].split("/").length-1]!='p')
			{
				alert("只能选择人员信息!");
				return;
			}
		}
		
		for(i=0;i<temps.length;i++)
		{
		    if(temps[i].length>0)
		    {
		    	var isExist=0;
		    	for(var j=0;j<document.accountingForm.right_fields.options.length;j++)
		    	{
		    		if(document.accountingForm.right_fields.options[j].value==temps[i])
		    		{	isExist=1;
		    			break;
		    		}
		    	}
		    	if(isExist==1)
		    		continue;
		        var no = new Option();
		    	no.value=temps[i];
		    	no.text=temps[i].split("/")[2];
		    	document.accountingForm.right_fields.options[document.accountingForm.right_fields.options.length]=no;
		    }
		}
	}
	function setSelectValue()
	{
		var temps=document.getElementById("date_box");
		
		for(i=0;i<temps.options.length;i++)
		{
		   
		       if(temps.options[i].selected)
		       {
		    	    var isExist=0;
		        	for(var j=0;j<document.accountingForm.right_fields.options.length;j++)
		    	    {
		    		    if(document.accountingForm.right_fields.options[j].value==temps.options[i].value)
		    		    {	
		    		        isExist=1;
		    			    break;
		    		     }
		         	}
		    	    if(isExist==1)
		    		     continue;
		            var no = new Option();
		    	    no.value=temps.options[i].value;
		    	    no.text=temps.options[i].value.split("/")[2];
		    	    document.accountingForm.right_fields.options[document.accountingForm.right_fields.options.length]=no;
		    	}
		}
	}
	
	
	/** 查询 */
	function query()
	{
		if(document.accountingForm.a_name.value.length==0)
		{
			alert("请输入姓名信息!");
			return;
		}
		 var hashVo=new ParameterSet();
		 hashVo.setValue("name",getEncodeStr(document.accountingForm.a_name.value));
		 hashVo.setValue("dbname",info[1]);
		 hashVo.setValue("flag","1");
		 var request=new Request({method:'post',asynchronous:false,onSuccess:return_ok,functionId:'3020071012'},hashVo);			
	}
	
	
	function return_ok(outparameters)
	{
		var orgLink = outparameters.getValue("orgLink");
		if(orgLink.length==0)
		{
			alert("找不到"+document.accountingForm.a_name.value);
		}
		else
		{
			//alert(orgLink);
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
						break;
					}
				}
			}
			obj.expand();
		}
	}
	
	
	function goback()
	{
	
		returnValue="0";
		window.close();
	}
	 function remove()
    {
    	Element.hide('date_panel');
    }
function showDateSelectBox(srcobj)
   {
   		if($F('selectname')=="")
   		{
   			Element.hide('date_panel');
   			return false ;
   		}
      date_desc=document.getElementById(srcobj);
      Element.show('date_panel');
      var pos=getAbsPosition(date_desc);
	  with($('date_panel'))
	  {
        style.position="absolute";
		style.posLeft=pos[0];
		style.posTop=pos[1]-date_desc.offsetHeight+42;
		style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
      }
      var hashVo = new ParameterSet();
        hashVo.setValue("name",getEncodeStr(document.accountingForm.a_name.value));
		 hashVo.setValue("dbname",info[1]);
		 hashVo.setValue("flag","2");
      var request=new Request({method:'post',onSuccess:shownamelist,functionId:'3020071012'},hashVo);
   }
   function shownamelist(outparamters)
   {
   		var namelist=outparamters.getValue("namelist");
		if(namelist.length==0){
			Element.hide('date_panel');
		}
		else{
			AjaxBind.bind(accountingForm.contenttype,namelist);
			var OptionLength=0;
			var obj=document.getElementById("contenttype");
			for(var i=0;i<obj.options.length;i++)
			{
				var length = strlen(obj.options[i].text);
				if(length>OptionLength)
				{
					OptionLength=length;
				}
			}
			document.getElementById("contenttype").style.width=OptionLength*7;
		}
   }
   function strlen(str){  
    var len = 0;  
    for (var i=0; i<str.length; i++) {   
     var c = str.charCodeAt(i);   
    //单字节加1    
     if ((c >= 0x0001 && c <= 0x007e) || (0xff60<=c && c<=0xff9f)) {   
       len++;   
     }   
     else {   
      len+=2;   
     }   
    }   
    return len;  
}  

</script>

</html:form>
</html>