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
	var old_a0101="";
    function addShiftEmployee(obj)
    {
        var a0101=$F('a0101');        
        if(a0101=="")
          closee();
        if(a0101==old_a0101)
           return;        
        var targetobj,hiddenobj;
        var currnode=Global.selectedItem;	
        if(currnode==null)
    	  return;  
    	showSelectBox(obj);
        var id = currnode.uid;
            
   	    var hashvo=new ParameterSet();	
        hashvo.setValue("name",getEncodeStr(a0101));	
        hashvo.setValue("a_code",id);
        hashvo.setValue("flag","3");
        hashvo.setValue("dbname",info[1]); 		
        var request=new Request({asynchronous:false,onSuccess:showA0101,functionId:'3020071012'},hashvo); 
    }
    function showA0101(outparamters)
    {
		var objlist=outparamters.getValue("namelist");		
		
		if(objlist!=null)
		  AjaxBind.bind($('a0101_box'),objlist);		
    }
 	function showSelectBox(srcobj)
    {
      Element.show('a0101_pnl');   
      Element.show('closee'); 
      var pos=getAbsPosition(srcobj);
	  with($('a0101_pnl'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
 		    style.posTop=pos[1]-1+srcobj.offsetHeight;
		    style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
      }                 
    }  
	function setSelectValue()
    {
       var objid,i;
       var objid_text="";
       var obj=$('a0101_box');       
   	   for(i=0;i<obj.options.length;i++)
       {
          if(obj.options[i].selected)
          {
             objid=obj.options[i].value;
             objid_text=obj.options[i].text;
          }
       } 
       if(objid_text=="")
          return false;
       var vos= document.getElementsByName('right_fields');
       var emp_vo=vos[0];
       var isC=true;
       for(i=0;i<emp_vo.options.length;i++)
       {
         var select_ob=emp_vo.options[i];
         if(select_ob.value==objid)
         {
          isC=false;
         }
      }   
      var no = new Option();
      no.value=objid;
      //var kh=objid_text.split("/");
      //objid_text = kh[kh.length - 1];
      no.text=objid_text;
      if(isC)
      {
         emp_vo.options[emp_vo.options.length]=no;
      }       
    }  
    function closee()
    {
       Element.hide('a0101_pnl');
       Element.hide('closee');
       var obj=document.getElementById("a0101");
       obj.value="";
    }
</script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<body>
<html:form action="/gz/gz_accounting/gz_table">
<br>
<table>
<tr>
	<td>&nbsp;</td>
	<td>
	<fieldset align="center" style="width:90%;">
    		<legend ><bean:message key="wd.lawbase.standbypersonnel"/></legend>
    		
    		<table width='100%'  >
    		<tr><td valign="middle">
    		 <bean:message key="columns.archive.name"/>:&nbsp;<Input type='text' name='a_name' id='a0101'  size='20' onkeyup="addShiftEmployee(this);"/>
    		 </td><td width='20%' >
				<div id="closee">
					<a href="###" onclick="closee();"><font color="red">关闭</font>
					</a>
				</div>
    		  <!-- <a href='javascript:query()' > <img  src="/images/code.gif"  border=0 /></a> -->
    		</td></tr>
    		<tr><td colspan='2' align='left' >
		     <div id="treemenu"></div> 
		     </td></tr></table>
		                  
	</fieldset>
	</td>
	<td>
	
		  &nbsp;<html:button  styleClass="mybutton" property="b_addfield"  onclick='addMen()'  >
            		     <bean:message key="button.setfield.addfield"/> 
	            </html:button >&nbsp;
	            <br>
	            <br>
	           &nbsp;<html:button  styleClass="mybutton" property="b_delfield" onclick='delMen()'  >
            		     <bean:message key="button.setfield.delfield"/>    
	            </html:button >	
				&nbsp;
	</td>
	<td>
	<fieldset align="center" style="width:90%;">
    		<legend ><bean:message key="wd.lawbase.alreadypickpersonnel"/></legend>
			<table><tr><td>
			
		    <select name="right_fields" multiple="multiple" size="10"   style="height:330px;width:200px;font-size:9pt">
             </select>
             
            </td></tr></table>
		    
		                 
	</fieldset>
	</td>

</tr>
<tr>
	<td colspan='4' align=right > <Br>
		 <html:button  styleClass="mybutton" property="enter" onclick='sub()'  >
            		     <bean:message key="button.ok"/>
	            </html:button >	
	      &nbsp; &nbsp;<html:button  styleClass="mybutton" property="cancel" onclick='goback()'  >
            		     <bean:message key="button.cancel"/>
	            </html:button >	 &nbsp;
	</td>
</tr>

</table>
</html:form>
<div id="a0101_pnl" style="border-style: nono">
	<select name="a0101_box" multiple="multiple" size="10"
		class="dropdown_frame" style="width: 180"
		ondblclick="setSelectValue();">
	</select>
</div>
<script language="javascript">
Element.hide('a0101_pnl');
Element.hide('closee');
</script>
</body>
<script language='javascript' >
    var m_sXMLFile	= "/gz/gz_accounting/importMen/handImportMen_tree2.jsp?flag=-1&id=0&nbase="+info[1];		
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
		 var str='';
		if(document.accountingForm.right_fields.options.length==0)
		{
			alert("请选择需要引入的人员！");
			return;
		}
		 vos= document.getElementsByName("right_fields");
		  if(vos==null)
		  	return false;
		  right_vo=vos[0];
		  for(i=right_vo.options.length-1;i>=0;i--)
		  {
		  	var temp = right_vo.options[i].value.split('/');
		  	str=str+','+temp[1]+temp[0];
		  }
		  var thevo=new Object();
		  thevo.content=str;
		  window.returnValue=thevo;
		  window.close();
	}

	
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
		var objpnl = $('a0101_pnl')
		if(objpnl.style.display=="none"){
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
		} else {
	       	var objid;
	       	var objid_text="";
	       	var obj=$('a0101_box');  
	       	var vos= document.getElementsByName('right_fields');     
	   	   	for(var i=0;i<obj.options.length;i++)
	       	{
	       		if(obj.options[i].selected)
	          	{
	             	objid=obj.options[i].value;
	             	objid_text=obj.options[i].text;
	             	if(objid_text=="")
	          			continue;
			       	var emp_vo=vos[0];
			       	var isC=true;
			       	for(var j=0;j<emp_vo.options.length;j++)
			       	{
			         	var select_ob=emp_vo.options[j];
			         	if(select_ob.value==objid){
			          		isC=false;
			         	}
			       	}   
			       	var no = new Option();
			       	no.value=objid;
			       	no.text=objid_text;
			      	if(isC){
			          	emp_vo.options[emp_vo.options.length]=no;
			       	} 
	          	}
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
		window.close();
	}
</script>


</html>