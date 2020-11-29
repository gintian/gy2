<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag = userView.getBosflag();
	String showtitle=request.getParameter("showtitle");
 %>
<script type="text/javascript">
	function getCodesetInfo()
	{
	   var cflag=codeMaintenceForm.cflag.value;//判断是开发商模式还是用户模式
	   var vflag=codeMaintenceForm.vflag.value;//判断是修改还是新增 0 新增 1 修改
       var codesetvo=new Object();	
       codesetvo.codesetid=$F('codesetvo.string(codesetid)');
       if(codesetvo.codesetid=="")
       {
         alert("<bean:message key="codemaintence.codeset.id.error"/>");
         return false;
       }
       if(codesetvo.codesetid.length!=2)
       {
         alert("代码类代号长度需为2位！");
         return false;
       }
       if(cflag!="1"&&vflag=="0")
       {
    	       var reg = /^[l-wL-W][a-zA-Z]$/; //用户模式下代码类为纯字母并且第一位在L~W之间 guodd 2015-08-03 
    	       if(!reg.test(codesetvo.codesetid)){
    	    	   		alert("第一位必须在 L~W 范围之内并且第二位必须为字母！");
    	    	   		return false;
    	       }
    	    	      
       	   if(!window.dialogArguments)
	       if(!IsLetter(codesetvo.codesetid))
	       { 
	       	alert("<bean:message key="codemaintence.code.addcodesetid.error"/>");
	       	return false;
	       }else
	       	codesetvo.status='0';
	   }
	   if(cflag=="1"&&vflag=="0")
       {
		   var codetype = document.getElementById("hidcategoriesselect").value;
		   
		   if(codetype=="国家标准" && !(/^[A-H][A-Z]$/.test(codesetvo.codesetid))){
			   alert("请检查代码类代号！规则为 [A-H]+[A-Z]。");
			   return false;
		   }else if(codetype=="系统代码" && !((/^[0-9][0-9]$/.test(codesetvo.codesetid)) && codesetvo.codesetid!='00')){
			   alert("请检查代码类代号！规则为01-99。");
			   return false;
		   }else if(codetype!="国家标准" && codetype!="系统代码" && !(/^[X-Z][A-Z]$/.test(codesetvo.codesetid) || /^[IJK][A-K0-9]$/.test(codesetvo.codesetid))){
			   alert("请检查代码类代号！规则为 [X-Z]+[A-Z] 或者[I-K]+[A-K或者0-9]。");
			   return false;
		   }
			   
		   
		   /* if(reg.test(codesetvo.codesetid) || reg2.test(codesetvo.codesetid)){
			//   alert("第一位不能为L-W并且不能为数字！");
			//   return false;
		   } */
           if(!window.dialogArguments)
	       {
		       //if(${codeMaintenceForm.status }==1)
		       //{
			       if(IsLetter(codesetvo.codesetid))
				   { 
					    codesetvo.status='0';
				   }else if(IsInteger(codesetvo.codesetid))
					{ 
						codesetvo.status='1';
					}else{
						//alert("<bean:message key="codemaintence.codeset.inputnewerror"/>");
						//return false;
						codesetvo.status='0';
					}
				 // if(!IsInteger(codesetvo.codesetid))
					//{ 
				//		alert("<bean:message key="codemaintence.codeset.inputerror"/>");
				//		return;
				//	}
		      // }
		      // else
		       //{
			       	
		       //}
		   }
	   }
       codesetvo.codesetdesc=$F('codesetvo.string(codesetdesc)');
       if(codesetvo.codesetdesc==""){
       	 alert("<bean:message key="codemaintence.codeset.desc.error"/>");
         return false; 
       }
       
       codesetvo.maxlength=$F('codesetvo.string(maxlength)');
       if(codesetvo.maxlength==""){
       		alert("<bean:message key="codemaintence.codeset.maxlength.error"/>");
            return false;
       }
       if(codesetvo.maxlength=='0'){
       		alert('宽度必须大于0');
       		return false;
       		}
       if(checknumber(codesetvo.maxlength)){
       		
       		alert('<bean:message key="codemaintence.codeset.maxlength.perror"/>');
       		return false;
       }
       
      	if(!beforesave())
      		return false;
      	
      if(vflag=="0"){
      }else{
      	if(cflag=="0")
       		codesetvo.status='<bean:write name="codeMaintenceForm" property="codesetvo.string(status)" />';
       	else
       		codesetvo.status=document.getElementsByName("codesetvo.string(status)")[0].value;
       }
       var categories=$F('hidcategories');
       var return_vo=codesetvo;
       var codesetvo=new Object();
       codesetvo.categories=categories;
     codesetvo.codesetid=return_vo.codesetid;
     codesetvo.codesetdesc=return_vo.codesetdesc;
     codesetvo.maxlength=return_vo.maxlength;
     codesetvo.status=return_vo.status;
     <logic:equal value="1" name="userView" property="version_flag">
     codesetvo.validateflag=document.getElementById('validateflag').checked? "1":"0";
     </logic:equal>
     codesetvo.leaf_node = document.getElementById("leaf_node_id").checked?"1":"0";
     
     //alert(codesetvo.categories+"qqqq"+codesetvo.codesetid+"qqqq"+codesetvo.codesetdesc+"qqqq"+codesetvo.maxlength+"qqqq"+codesetvo.status+"qqqq"+codesetvo.validateflag);
     //var leafnodeobj = document.getElementById("leaf_node_id");
     //codesetvo.leaf_node = document.getElementById("leaf_node_id").checked?"1":"0";
    	
     if(vflag=="0"){//新增
	     var hashvo=new ParameterSet();
	     hashvo.setValue("codesetvo",codesetvo);
	     hashvo.setValue("flag","0");
	     hashvo.setValue("codestname",codesetvo.codesetdesc);
	     hashvo.setValue("codesetid",codesetvo.codesetid);
	     var request=new Request({asynchronous:false,onSuccess:add_codeset_ok,functionId:'1010050008'},hashvo);    
	 }else if(vflag=="1"){//修改
	     var hashvo=new ParameterSet();
	     hashvo.setValue("codesetvo",codesetvo);
	     hashvo.setValue("flag",'1');
	     var request=new Request({asynchronous:false,onSuccess:update_codeset_ok,functionId:'1010050008'},hashvo);        
	  
  	 }
	}
	function update_codeset_ok(outparamters)
  {
  	alert("修改成功！");
  	var codesetvo=new Object();
  	codesetvo.codesetid=outparamters.getValue('codesetid');
	codesetvo.codesetdesc=outparamters.getValue('codesetdesc');
	codesetvo.status=outparamters.getValue('states');
	codesetvo.flag=outparamters.getValue('flag');//标示是否属于系统代码或用户代码类改变
	codesetvo.categories=outparamters.getValue('categories');
  	// window.returnValue=codesetvo;
  	// window.close();
      parent.return_vo = codesetvo;
      closewin();
  	
  	
  }
	function add_codeset_ok(outparamters)
  	{
  		var codesetvo=new Object();
  		codesetvo.codesetid=outparamters.getValue('codesetid');
	     codesetvo.codesetdesc=outparamters.getValue('codesetdesc');
	     codesetvo.status=outparamters.getValue('states');
	     codesetvo.categories=outparamters.getValue('categories');
  		// window.returnValue=codesetvo;
  		// window.close();
  		if(parent && parent.Ext && parent.Ext.getCmp('add_codeset')){
          	parent.Ext.getCmp('add_codeset').return_vo = codesetvo;
	  	}else{
			parent.return_vo = codesetvo;
			window.returnValue = codesetvo;
	  	}
		closewin();
  	}
	
	function checknumber(String) 
	{   
   	var Letters = "1234567890"; 
   	var i; 
   	var c; 
   	for( i = 0; i < String.length; i ++ ) 
  	{ 
  		c = String.charAt( i ); 
  		if (Letters.indexOf( c ) ==-1) 
  		{ 
  		return true; 
  		} 
  	} 
 		 return false; 
  	} 
	function checkString(string){
	 	if(!checknumber(string)){
	 	  alert('<bean:message key="codemaintence.code.addcodesetid.error"/>');
	 	  return;
	 	}
	
	}
	
function IsLetter(str)      
{      
        if(str.length!=0){     
        reg=/^[A-Z]+$/;      
        if(!reg.test(str)){     
            return false;   
        }     
        } 
        return true;    
}      
      
function IsInteger(str)      
{        
        if(str.length!=0){     
        reg=/^[-+]?\d{2}$/;      
        if(!reg.test(str)){     
            return false; 
        }     
        }
        return true;     
}      
function estop()
{
	return event.keyCode!=34&&event.keyCode!=39;
}

document.body.onkeydown=function(){
	if(window.event.keyCode=='13'){
		getCodesetInfo();
	}else
		window.returnValue=true;
}

function changevalidateflagvalue(obj){
	//alert(obj.checked)
	if(!obj.checked)
		obj.value="0";
	else
		obj.value="1";
	//alert(obj.value);
}

  function toBig(obj){
  	var v=obj.value;
  	var str=v.substring(v.length-1,v.length);
  	if(/^[a-z]*$/.test(str)){
  		//obj.value=v.substring(0,v.length-1)+str.toUpperCase();
  		obj.value=v.toUpperCase();
  	}
  }
  
  function beforesave(){
  	var codeitemdesc=$F("codesetvo.string(codesetdesc)");
  	var reg=/^[\\`~!#\$%^&\*\+\{\}\|:"<>\?=,']*$/;
  	for(var i=0;i<codeitemdesc.length;i++){
		 var c=codeitemdesc.substring(i,i+1);
		 if(reg.test(c)){
		 	alert('名称不能是特殊字符!\n\`~!#$%^&*+{}|\\:"<>?=,\'');
		 	return false;
		 }
  	}
  	return true;
  }
  
  function closewin(){
  	// window.returnValue=undefined;
  	// window.close();
	  if(parent && parent.Ext && parent.Ext.getCmp('add_codeset')){
          parent.Ext.getCmp('add_codeset').close();
	  }else{
	  	window.close();
	  }

  }
  
  function screen(obj,flag)
{
  var targetvalue=document.getElementById(flag); 
  targetvalue.value=obj.value; 
}
function   addDict(obj,event,flag)
{ 
   var evt = event ? event : (window.event ? window.event : null);
   var np=   evt.keyCode; 
   if(np==38||np==40){ 
   
   } 
   var textv=obj.value;
   var aTag;
   	aTag = obj.offsetParent; 
   if(textv==null||textv=="")
	   return false;
   textv=textv.toLowerCase();  
   var un_vos=document.getElementsByName(flag+"_value");
   if(!un_vos)
		return false;
   var unStrs=un_vos[0].value;	
   var unArrs=unStrs.split(",");
   var   c=0;
   var   rs   =new   Array();
   for(var i=0;i<unArrs.length;i++)
   {
		 var un_str=unArrs[i];
		 if(un_str)
		 {
		     if(un_str.indexOf(textv)!=-1)
	         {
			     rs[c]="<tr id='tv' name='tv'><td id='al"+c+"'  onclick=\"onV("+c+",'"+flag+"')\"  style='height:15;cursor:pointer' onmouseover='alterBg("+c+",0)' onmouseout='alterBg("+c+",1)' nowrap class=tdFontcolor>"+un_str+"</td></tr>"; 
                 c++;
		     }
		 
		 }
        
	}
    resultuser=rs.join("");
    if(textv.length==0){ 
       resultuser=""; 
    } 
    document.getElementById("dict").innerHTML="<table width='200' class='div_table'  cellpadding='0' border='0'  bgcolor='#FFFFFF'   cellspacing='0'>"+resultuser+"</table>";//???????????????? 
    document.getElementById('dict').style.display = "block";
    document.getElementById('dict').style.position="absolute";	
    /* 搜索框用的relative定位，无法通过搜索框直接找到绝对位置。通过table元素定位，解决弹框大小不一致时定位错乱问题 guodd 2018-09-01 */
    var table = document.getElementById("mainTable");
	document.getElementById('dict').style.left=table.offsetLeft+111;
   	document.getElementById('dict').style.top=table.offsetTop+165;
} 
function onV(j,flag){
   var  o =   document.getElementById('al'+j).innerHTML; 
   document.getElementById(flag).value=o; 
   document.getElementById(flag+"select").value=o;
   document.getElementById('dict').style.display = "none";
} 
function   alterBg(j,i){
    var   o   =   document.getElementById('al'+j); 
    if(i==0) 
       o.style.backgroundColor   ="#3366cc"; 
    else   if(i==1) 
       o.style.backgroundColor   ="#FFFFFF"; 
}
function hiddendict(){
	document.getElementById('dict').style.display = 'none';
}
//-->
</script>
<style>
<!--
.div_table{
    border-width: 1px;
    BORDER-BOTTOM: #aeac9f 1pt solid; 
    BORDER-LEFT: #aeac9f 1pt solid; 
    BORDER-RIGHT: #aeac9f 1pt solid; 
    BORDER-TOP: #aeac9f 1pt solid ; 
}
.tdFontcolor{
	text-decoration: none;
	Font-family:????;
	font-size:12px;
	height=20px;
	align="center"
}
-->
</style>
<hrms:themes></hrms:themes>
<body onclick="hiddendict();">
<html:form action="/system/codemaintence/add_edit_codeset">
	<table id="mainTable" width="490" border="0" cellpadding="0" cellspacing="0" align="center">

		<tr height="0">
			<!-- td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
				
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="350"></td> -->
			
			<td align="left" colspan="4">
			<%--
				<logic:equal value="0" name="codeMaintenceForm" property="vflag">
				<bean:message key="codemaintence.codeset.add" /></logic:equal>
				<logic:equal value="1" name="codeMaintenceForm" property="vflag">
				<bean:message key="codemaintence.codeset.update" /></logic:equal>
				&nbsp;
			--%>
			</td>
			
		</tr>

		<tr >
			<td colspan="4" class="framestyle3" style="border-top:#C4D8EE 1pt solid;">
				<table border="0" cellpmoding="0" cellspacing="0" style="margin-top:10px;margin-bottom:10px;" class="DetailTable" cellpadding="0">
					<tr class="list3" style="height:30px;">
						<td align="right" nowrap width="100">
							<bean:message key="codemaintence.codeset.id" />
						</td>
						<td align="left" nowrap style="padding-left:5px;">
							<html:text name="codeMaintenceForm" property="codesetvo.string(codesetid)"  size="20" styleClass="text" maxlength="2" onkeyup="toBig(this);" style="width:200px;"></html:text>
						</td>
						</tr>
						<tr style="height:30px;">
						<td align="right" nowrap>
							<bean:message key="codemaintence.codeset.desc" />
						</td>
						<td align="left" nowrap style="padding-left:5px;">
							<html:text name="codeMaintenceForm" property="codesetvo.string(codesetdesc)" maxlength="25" size="20" styleClass="text" onkeypress="event.returnValue=estop(this)" style="width:200px;"></html:text>
						</td>
					</tr>
					<tr class="list3" style="height:30px;">
						<td align="right" nowrap>
							<bean:message key="codemaintence.codeset.maxlength" />
						</td>
						<td align="left" nowrap style="padding-left:5px;">
							<html:text name="codeMaintenceForm" property="codesetvo.string(maxlength)" maxlength="3" size="20" styleClass="text" style="width:200px;"></html:text>
						</td>
					</tr>
					<tr >
                <td align="right" style="height:30px;">
                &nbsp;&nbsp;&nbsp;&nbsp;分类名称
                </td>
                <td width="200px;" style="padding-left:5px;">
                <%if(flag.equals("hcm")){ %>
                <!-- 
	                <div style="position:absolute;z-index:1;width:200px;top:145px;height:20px;left:111px">
	                <div style="overflow:hidden;border-left:none;width:199px;height:21px;margin-top:-1;" class="complex_border_color">
	                  <html:select name="codeMaintenceForm" property='categories' styleId="hidcategoriesselect" style="position:absolute;width:198px;height:20px;clip:rect(0 198 20 179)"  onchange="screen(this,'hidcategories');" onfocus=''>   
						<option value=""></option>
						<html:optionsCollection property="catelist" value="dataValue" label="dataName" />
		            	</html:select>
		            	</div>
		            	</div>
		    			<div style="position:absolute;z-index:2;top:145px;width: 200px; height:20px;left:112px">    
		            	<input name=categories id='hidcategories' style="position:absolute;width: 182px; height:20px;margin-left:-1" value='${codeMaintenceForm.categories }'  onkeyup="addDict(this,event,'hidcategories');" class="inputtext">
		            	</div>
		            	上面为旧代码，显示效果不理想，当窗口大小不一致时，absolute定位会错乱。使用下面新代码，注意还修改了搜索结果框的定位方式，请看addDict方法 guodd 2018-09-01
            	 	-->
	            	<div style="height:22px; overflow:hidden">
	            		<html:select name="codeMaintenceForm" property='categories' styleId="hidcategoriesselect" style="width:200px;height:20px;z-index:1;"  onchange="screen(this,'hidcategories');" onfocus=''>
							<option value=""></option>
							<html:optionsCollection property="catelist" value="dataValue" label="dataName" />
	            			</html:select>
	            			<input name=categories id='hidcategories' style="position:relative;top:-22px;left:1px;z-index:2;width:180px;height:20px;border:none;" value='${codeMaintenceForm.categories }'  onkeyup="addDict(this,event,'hidcategories');">
	            	</div>
            	<%}else{ %>
            	<div style="position:absolute;z-index:1;width:200px;top:127px;height:20px;left:111px">
            	<div style="overflow:hidden;border-left:none;width:199px;height:21px;margin-top:-1;border:1px solid #C4D8EE;"> 
                  <html:select name="codeMaintenceForm" property='categories' styleId="hidcategoriesselect" style="position:absolute;width:198px;height:20px;clip:rect(0 198 20 179)"  onchange="screen(this,'hidcategories');" onfocus=''>   
					<option value=""></option>
					<html:optionsCollection property="catelist" value="dataValue" label="dataName" />
            	</html:select>
            	</div>
            	</div>
    			<div style="position:absolute;z-index:2;top:127px;width: 200px; height:20px;left:112px">    
            	<input name=categories id='hidcategories' style="position:absolute;width: 182px; height:20px;margin-left:-1" value='${codeMaintenceForm.categories }'  onkeyup="addDict(this,event,'hidcategories');" class="inputtext">
            	</div>
            	<%} %>
            	<input type="hidden" name="hidcategories_value" value='${codeMaintenceForm.hidcategories }' />
               </td>
             </tr>
             <tr>
				
			</tr>
             <logic:equal value="1" name="codeMaintenceForm" property="cflag">
						<logic:equal value="1" name="codeMaintenceForm" property="vflag">
					<tr style="height: 30px">
						<td align="right" nowrap>
						
							<bean:message key="codemaintence.codeset.status" />
						</td>
						<td align="left" nowrap style="padding-left:5px;">
							<logic:equal value="1" name="codeMaintenceForm" property="cflag">
								<logic:equal value="1" name="codeMaintenceForm" property="vflag">
									<html:select name="codeMaintenceForm" property="codesetvo.string(status)" style="width: 200px;">
										<logic:notEqual name="codeMaintenceForm" property="codesetvo.string(status)" value="1">
											<logic:notEqual name="codeMaintenceForm" property="codesetvo.string(status)" value="2">
												<html:option value="0">用户代码</html:option>
											</logic:notEqual>
										</logic:notEqual>
										<html:option value="2">系统代码</html:option>
										<html:option value="1">非系统代码</html:option>
									</html:select>
								</logic:equal>
								<logic:equal value="0" name="codeMaintenceForm" property="vflag">
									<logic:equal value="1" name="codeMaintenceForm" property="status">
										<html:hidden name="codeMaintenceForm" styleId="statusid" property="codesetvo.string(status)" value="0"/>
									</logic:equal>
									<logic:notEqual value="1" name="codeMaintenceForm" property="status">
										<html:hidden name="codeMaintenceForm" styleId="statusid" property="codesetvo.string(status)" value="1"/>
									</logic:notEqual>
								</logic:equal>
							</logic:equal>
							<logic:equal value="0" name="codeMaintenceForm" property="cflag">
								<logic:equal value="0" name="codeMaintenceForm" property="vflag">
									<html:hidden name="codeMaintenceForm" styleId="statusid" property="codesetvo.string(status)" value="0"/>
								</logic:equal>
								<logic:equal value="1" name="codeMaintenceForm" property="vflag">
									<html:hidden name="codeMaintenceForm" styleId="statusid" property="codesetvo.string(status)"/>
								</logic:equal>
							</logic:equal>
						</td>
					</tr>
					</logic:equal>
						</logic:equal>
					<logic:equal value="1" name="userView" property="version_flag">
					<tr>
						<td align="right" nowrap>
							记录历史
						</td>
						<td align="left" nowrap style="padding-left:3px;">
							<logic:equal value="" name="codeMaintenceForm" property="codesetvo.string(validateflag)">
								<input id='validateflag' type="checkbox"/>
							</logic:equal>
							<logic:equal value="0" name="codeMaintenceForm" property="codesetvo.string(validateflag)">
								<input id='validateflag' type="checkbox"/>
							</logic:equal>
							<logic:equal value="1" name="codeMaintenceForm" property="codesetvo.string(validateflag)">
								<input id='validateflag' type="checkbox" checked="checked"/>
							</logic:equal> 
						</td>
					</tr>
					</logic:equal>
					<tr>
						<td align="right" nowrap>
							仅末级代码可选
						</td>
						<td align="left" nowrap style="padding-left:3px;"><!--1:选中  0：未选中  -->
							<logic:equal value="" name="codeMaintenceForm" property="codesetvo.string(leaf_node)"><!-- 兼容处理当leaf_node在表中为null的情况 add by xiegh   -->
								<input id='leaf_node_id' type="checkbox"  />
							</logic:equal>
							<logic:equal value="0" name="codeMaintenceForm" property="codesetvo.string(leaf_node)">
								<%-- <html:checkbox property="codesetvo.string(leaf_node)"  styleId='leaf_node_id' name="codeMaintenceForm" value="1" " >1111</html:checkbox> --%>
								<input id='leaf_node_id' type="checkbox"  />
							</logic:equal>
							<logic:equal value="1" name="codeMaintenceForm" property="codesetvo.string(leaf_node)">
								<input id='leaf_node_id' type="checkbox" checked="checked" />
							</logic:equal> 
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr class="list3">
			<td align="center" colspan="2" height="35px;">
				<html:button styleClass="mybutton" property="b_save" onclick="getCodesetInfo();">
					<bean:message key="button.save" />
				</html:button>
				<html:button styleClass="mybutton" property="br_return" onclick="closewin();">
					<bean:message key="button.close" />
				</html:button>
			</td>
		</tr>
	</table>
	<html:hidden name="codeMaintenceForm" property="cflag" />
	<html:hidden name="codeMaintenceForm" property="vflag" />
</html:form>
<div id="dict" style="display:none;z-index:+999;position:absolute;width:250px;overflow:auto;bgcolor='#FFFFFF';"></div>
<script type="text/javascript">
<!--
	if(window.dialogArguments)
	{
		Element.readonly('codesetvo.string(codesetid)');
	}
//-->
</script>
<script language="JavaScript">
	if(!getBrowseVersion() || getBrowseVersion() =='10'){
		document.getElementById('hidcategories').style.top ='-19px';
		document.getElementById('hidcategories').style.height = '18px';
	}
</script>
</body>