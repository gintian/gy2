<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_analyse.GzAnalyseForm,java.util.*"%>
	<link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />
	<script type="text/javascript" src="../../ext/ext-all.js"></script>
	<script type="text/javascript" src="../../ext/ext-lang-zh_CN.js"></script>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
	GzAnalyseForm gzAnalyseForm=(GzAnalyseForm)session.getAttribute("gzAnalyseForm"); 
	boolean flag=false;
	if(userView.isSuper_admin()){
		flag=true;
	}

%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
   function savefield()
  {  
  	 var faNeme=Ext.getCmp('ssff').getValue(); 
  	 if(faNeme==""){
		returnValue="00";
		window.close();
		return;	
  	 }
  	 if(faNeme=="00"){
		returnValue="00";
		window.close();
		return;
  	 }	  
  	 var bb=faNeme.split(".")[1];
  	 if(faNeme!="add"&&faNeme.split(":")[2].split(".")[0]=="g"&&<%=!flag%>){
  	 	returnValue=faNeme;
		window.close();
		return;
  	 }
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");     
     if(vos.length!=0)
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
          for(var j=i+1;j<vos.length;j++)
          {
          	if(valueS.toUpperCase()==vos.options[j].value.toUpperCase())
          	{
          		alert("有相同的指标存在！");
          		return false;
          	}
          }
        }       
     }else{
     	alert("请选择指标！");
     	return;
     }
    var code_fields="";        
    for(var i=0;i<vos.length;i++)
    {
      var valueS=vos.options[i].value;  
      if(i==vos.length-1){
      		code_fields+=valueS;      
      }else{
            code_fields+=valueS+",";
      }        
    }
	var iframe_url="/gz/gz_analyse/saveHighgrade.jsp?name="+bb;
	var xmltype= window.showModalDialog(iframe_url, null,"dialogWidth:350px; dialogHeight:150px;resizable:no;center:yes;scroll:yes;status:no"); 
	if(xmltype&&xmltype[0]=="ssss"){
		return;
	}
	if(typeof(xmltype)=="undefined"){
		return;
	}
	var name="";
	var scope="";
	if(xmltype!= null){
		name=xmltype[0];
		scope=xmltype[1];
	}
	var item=document.getElementById("itemid").value;
    hashvo.setValue("values",code_fields); 
    hashvo.setValue("name",name); 
    hashvo.setValue("scope",scope); 
    hashvo.setValue("classify_item",item); 
    hashvo.setValue("faNeme",faNeme);
    hashvo.setValue("flag","save");
	var request=new Request({method:'post',asynchronous:true,onSuccess:showSelect,functionId:'3020130057'},hashvo);
   }	
   function showSelect(outparamters)
   { 
   	 window.returnValue=outparamters.getValue("faNeme");
	 window.close(); 
   }
    function showSelectOk(outparamters)
   { 
		alert(RELATED_SUBSET_SET_OK); 
   }
   function closeOk()
   { 
     	 var faNeme=Ext.getCmp('ssff').getValue(); 
	  	 if(faNeme==""){
			returnValue="00";
			window.close();
			return;	
	  	 }
		window.close();
   }  
   	function getCodeValue()
	{
  		var item=document.getElementById("itemid").value;
		var in_paramters="itemid="+item;
   		var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020130056'});
	}
	
	function showCodeFieldList(outparamters)
	{
		var value=outparamters.getValue("codelist");
		var nullvalue=outparamters.getValue("nulllist");	
		var str_value=getDecodeStr(outparamters.getValue("str_value"));
		if(value.length>0)
		{
			var elem=gzAnalyseForm._left_fields;
			AjaxBind.bind(elem,value);		
			var elem1=gzAnalyseForm._right_fields;
			AjaxBind.bind(elem1,nullvalue);	
				
			if(typeof(str_value)!="undefined"&&str_value.length>0)
			{ 
				var objs=str_value.split("`");
				var _objs= new Array();
				for(var i=0;i<objs.length;i++)
				{
					var temp=objs[i].split("~");
					_objs["_"+temp[0]]=temp[1];
				}
				
				var obj=document.getElementsByName("_left_fields");
				for(var i=0;i<obj[0].options.length;i++)
	  			{
	  				var _value=obj[0].options[i].value;
	  				var desc=_objs["_"+_value];
	  				if(typeof(desc)!="undefined")
	  				{
	  					obj[0].options[i].title=desc;
	  				}
	  			}
			}			
		}else{
			var elem=gzAnalyseForm._left_fields;
			AjaxBind.bind(elem,nullvalue);		
			var elem1=gzAnalyseForm._right_fields;
			AjaxBind.bind(elem1,nullvalue);	
		}
	}	
	function Merge(){
		var fromvo,tovo,vos,i;

  		vos= document.getElementsByName("_left_fields");

  		if(vos==null)
  			return false;
  		fromvo=vos[0];
  		tovo=document.getElementsByName("_right_fields")[0];
  		var flag = false;
  		var no = new Option();
  		for(i=0;i<fromvo.options.length;i++)
  		{
    		if(fromvo.options[i].selected)
    		{   
    			if(i==fromvo.options.length-1){
    				no.value+=fromvo.options[i].value;
    			}else{
    			    no.value+=fromvo.options[i].value+"`";
    			}   
    			flag=true;		
    		}
  		}
  		if(flag==false){
  			alert("请选择要合并的项目！");
  			return;
  		}
  		var name=window.prompt("请输入合并后的名称","");
		if(name==null){
			return;
		}
		if(name.length=="0"){
			alert("名字不能为空！");
			return;
		}
  		no.value+=":"+name;
  		no.text=name;
  		tovo.options[tovo.options.length]=no;
	}
	function totel(){
		var fromvo,tovo,vos,i;
  		vos= document.getElementsByName("_left_fields");
  		if(vos==null)
  			return false;
  		fromvo=vos[0];
  		tovo=document.getElementsByName("_right_fields")[0];
  		var flag = false;
  		var no = new Option();
  		for(i=0;i<fromvo.options.length;i++)
  		{
    		flag=true;		
  		}
  		if(flag==false){
  			alert("没有指标，无需总计！");
  			return;
  		}

	  	var name=window.prompt("请输入合并后的名称","");
		if(name==null){
			return;
		}
		var tovo=document.getElementsByName("_right_fields")[0];
		var no = new Option();
		no.value="all:"+name;
  		no.text=name;
		tovo.options[tovo.options.length]=no;
	}
	function setfaName(obj){
		var aa=obj.value;
		var bb=obj.options[obj.selectedIndex].text;
		var right=gzAnalyseForm._right_fields;
		var left=gzAnalyseForm._left_fields;
		var hebing= document.getElementById("hebing");  
		var zongji= document.getElementById("zongji");  
		var xuan= document.getElementById("xuan");  
		var che= document.getElementById("che");  
		var del= document.getElementById("del");  
		if(aa=="00"){

			right.options.length=0;
			left.disabled=true;
			hebing.disabled=true;
			zongji.disabled=true;
			xuan.disabled=true;
			che.disabled=true;
			del.disabled=true;
			return;
		}else if(aa!="add"&&aa.split(":")[2].split(".")[0]=="g"&&<%=!flag%>){
			left.disabled=true;
			right.disabled=true;
			hebing.disabled=true;
			zongji.disabled=true;
			xuan.disabled=true;
			che.disabled=true;
			del.disabled=true;
		}else{
			left.disabled=false;
			right.disabled=false;
			hebing.disabled=false;
			zongji.disabled=false;
			xuan.disabled=false;
			che.disabled=false;
			del.disabled=false;
		}
		
		if(aa=="add"){
			right.options.length=0;
			del.disabled=true;
			return;
		}
		var hashvo=new ParameterSet(); 
		hashvo.setValue("faName",aa);
		var request=new Request({method:'post',asynchronous:true,onSuccess:setOk,functionId:'3020130058'},hashvo);
	}
	function setOk(outparamters){
		var value=outparamters.getValue("list");
		var item=outparamters.getValue("item");
		var str_value=getDecodeStr(outparamters.getValue("str_value"));
		var obj=document.getElementById("itemid");
		obj.value=item;
		getCodeValue();
		if(value.length>0)
		{
			var elem=gzAnalyseForm._right_fields;
			AjaxBind.bind(elem,value);	

				
			if(typeof(str_value)!="undefined"&&str_value.length>0)
			{ 
				var objs=str_value.split("`");
				var _objs= new Array();
				for(var i=0;i<objs.length;i++)
				{
					var temp=objs[i].split("~");
					_objs["_"+temp[0]]=temp[1];
				}
				
				var obj=document.getElementsByName("_right_fields");
				for(var i=0;i<obj[0].options.length;i++)
	  			{
	  				var _value=obj[0].options[i].value;
	  				var desc=_objs["_"+_value];
	  				if(typeof(desc)!="undefined")
	  				{
	  					obj[0].options[i].title=desc;
	  				}
	  			}
			}									
		}
	}
	function delfield(){
		if(!confirm("确定执行删除操作？")){
			return;
		}
		var faNeme=document.getElementById("faNeme").value;
		var hashvo=new ParameterSet(); 
		hashvo.setValue("faNeme",faNeme);
    	hashvo.setValue("flag","del");
		var request=new Request({method:'post',asynchronous:true,onSuccess:delOk,functionId:'3020130057'},hashvo);
	}
	function delOk(outparamters){
		var faNeme=outparamters.getValue("faNeme");
		var obj = Ext.getCmp("ssff").getStore();

		var com = Ext.getCmp("ssff");
		com.deleteSelect();
		com.setValue("");
		
		var aa="00";
		var right=gzAnalyseForm._right_fields;
		var left=gzAnalyseForm._left_fields;
		var hebing= document.getElementById("hebing");  
		var zongji= document.getElementById("zongji");  
		var xuan= document.getElementById("xuan");  
		var che= document.getElementById("che");  
		var del= document.getElementById("del");  
		if(aa=="00"){

			right.options.length=0;
			left.disabled=true;
			hebing.disabled=true;
			zongji.disabled=true;
			xuan.disabled=true;
			che.disabled=true;
			del.disabled=true;
			return;
		}
	}
	function setTitle(name,value){
	
		if(value.length>0)
		{			
			if(typeof(value)!="undefined"&&value.length>0)
			{ 
				var objs=value.split("`");
				var _objs= new Array();
				
				for(var i=0;i<objs.length;i++)
				{
					var temp=objs[i].split("~");
					_objs["_"+temp[0]]=temp[1];
				}
				var obj=document.getElementById(name)
				
				for(var i=0;i<obj.options.length;i++)
	  			{
	  				var _value=obj.options[i].value;
	  				var desc=_objs["_"+_value];
	  				if(typeof(desc)!="undefined")
	  				{
	  					obj.options[i].title=desc;
	  				}
	  			}
			}												
		}
	}
	function setTitle1(name){		
		var obj=document.getElementById(name)			
		for(var i=0;i<obj.options.length;i++)
	  	{
	  			var _value=obj.options[i].text;
	  			obj.options[i].title=_value;
	  	}											
	}
	window.onbeforeunload=function(event){ 
	     var faNeme=Ext.getCmp('ssff').getValue(); 
	  	 if(faNeme==""){
			returnValue="00";
	  	 }
	} 
</script>
<html:form action="/gz/gz_analyse/getHighgrade">
	<br>
	<table width="550" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
		<tr>
			<td><table>
					<tr>
						<td>方案名称</td>
						<td>
							<html:select name="gzAnalyseForm" styleId="faNemebox" property="faNeme"  onchange="setfaName(this)" style="width:500px;">
	                  			<html:optionsCollection property="nameList" value="dataValue" label="dataName" />				    		
							</html:select>
						</td>
					</tr>
				</table>				
			</td>
		</tr>
		<tr>
			<td align="center" class="TableRow_lrt" nowrap colspan="3">
				项目分类
			</td>
		</tr>
		<tr>
			<td width="100%" align="center" nowrap>
				<table width="100%" align="center" border="0" cellspacing="0"  cellpadding="0" class="RecordRow_lrt">
					<tr>
						<td align="center" width="46%">
							<table align="center" width="100%">
								<tr>
									<td align="left" height="32" style="padding-left: 10px;">
										薪资项目：

							<html:select name="gzAnalyseForm" property="itemid" size="1" onchange="getCodeValue();" style="width:150;font-size:9pt">
	                              <html:optionsCollection property="gzProjectList" value="dataValue" label="dataName"/>				    		
				    		</html:select>
									</td>
								</tr>
								<tr>
									<td align="center" style="padding-left: 5px;padding-bottom: 5px;">
										<hrms:optioncollection name="gzAnalyseForm"
											property="_subclasslist" collection="selectedlist3" />
										<html:select property="_left_fields" size="10" multiple="true"
											style="height:230px;width:97%;font-size:9pt"
											ondblclick="additem('_left_fields','_right_fields');setTitle1('_right_fields');">
											<html:options collection="selectedlist3" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>

								</tr>

							</table>
						</td>

						<td width="8%" align="center">
							<input type="button" Class="mybutton"  id="zongji" value="总计" onclick="totel()">
								
							<br>
							
							<input type="button" Class="mybutton"  id="hebing" value="合并" onclick="Merge()">
								
							<br>
	
							<input type="button" Class="mybutton"  id="xuan" value="选择" onclick="additem('_left_fields','_right_fields');setTitle1('_right_fields');">								

							<br>
				
							<input type="button" Class="mybutton"  id="che" value="撤选" onclick="removeitem('_right_fields');">
								
						</td>


						<td width="46%" align="center">


							<table width="100%">
								<tr>
									<td width="100%" align="left" height="32">

									</td>
								</tr>
								<tr>
									<td width="100%" align="left" style="padding-bottom: 5px;">
										<hrms:optioncollection name="gzAnalyseForm"
											property="_selectsubclass" collection="selectedlist4" />
										<html:select property="_right_fields" size="10" multiple="true"
											style="height:230px;width:97%;font-size:9pt" styleId="right"
											ondblclick="removeitem('_right_fields');">
											<html:options collection="selectedlist4" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="8%" align="center" style="padding-right: 5px;">
							<html:button styleClass="mybutton" property="b_up"
								onclick="upItem($('_right_fields'));">
								<bean:message key="button.previous" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_down"
								onclick="downItem($('_right_fields'));">
								<bean:message key="button.next" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" style="padding-top: 2px;padding-bottom: 1px;height: 35px;" nowrap colspan="3">
					<input type="button" name="btnreturn"
						value='<bean:message key="button.ok"/>' class="mybutton"
						onclick="savefield();">
					<input type="button" name="btnreturn" id="del"
						value='<bean:message key="button.delete"/>' class="mybutton"
						onclick="delfield();">
					<input type="button" name="btnreturn"
						value='<bean:message key="button.close"/>' class="mybutton"
						onclick="closeOk();">
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
getCodeValue();
var _item=document.getElementById("faNeme")
setfaName(_item);
setTitle("faNeme",getDecodeStr("${gzAnalyseForm.faNameStr}"));
setTitle("itemid",getDecodeStr("${gzAnalyseForm.gzprojectStr}"));
var combo = Ext.create('Ext.form.field.ComboBox',
	{
		emptyText:'请选择',
		mode:'local',
		triggerAction:'all',
		transform:'faNemebox',
		valueField:'iddd',
		width:200,
		id:'ssff',
		listeners:{
		   select:function(a,records){
		      a.selectRecord = records[0];
		   }
		},
		deleteSelect:function(){
		   if(this.selectRecord)
		     this.getStore().remove(this.selectRecord);
		},
//		matchFieldWidth:false,
		hiddenName:'faNeme'	
	}
);
combo.on('select',function(comboBox){
		var aa=comboBox.getValue();
		var right=gzAnalyseForm._right_fields;
		var left=gzAnalyseForm._left_fields;
		var hebing= document.getElementById("hebing");  
		var zongji= document.getElementById("zongji");  
		var xuan= document.getElementById("xuan");  
		var che= document.getElementById("che");  
		var del= document.getElementById("del");  
		if(aa=="00"){

			right.options.length=0;
			left.disabled=true;
			hebing.disabled=true;
			zongji.disabled=true;
			xuan.disabled=true;
			che.disabled=true;
			del.disabled=true;
			return;
		}else if(aa!="add"&&aa.split(":")[2].split(".")[0]=="g"&&<%=!flag%>){
			left.disabled=true;
			right.disabled=true;
			hebing.disabled=true;
			zongji.disabled=true;
			xuan.disabled=true;
			che.disabled=true;
			del.disabled=true;
		}else{
			left.disabled=false;
			right.disabled=false;
			hebing.disabled=false;
			zongji.disabled=false;
			xuan.disabled=false;
			che.disabled=false;
			del.disabled=false;
		}
		
		if(aa=="add"){
			right.options.length=0;
			del.disabled=true;
			return;
		}
		var hashvo=new ParameterSet(); 
		hashvo.setValue("faName",aa);
		var request=new Request({method:'post',asynchronous:true,onSuccess:setOk,functionId:'3020130058'},hashvo);
}
);
</script>