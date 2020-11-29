<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.*"%>
<%@ page import="com.hjsj.hrms.actionform.org.OrgInfoForm" %>
<%@ page import="com.hrms.struts.taglib.CommonData" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem" %>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/org/orgdata/orgedit.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript">
<!--
	function show(obj){
		obj.style.display="none";
		var _div=document.getElementById("changehis");
		var _span=document.getElementById("hid");
		_div.style.display="block";
		_span.style.display="block";
	}
	function hid(obj){
		obj.style.display="none";
		var _div=document.getElementById("changehis");
		var _span=document.getElementById("show");
		_div.style.display="none";
		_span.style.display="block";
	}
//-->
</script>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
boolean version = false;
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	}
//	int ver_flag=userView.getVersion_flag();
//	if(ver_flag==0)
//		version=false;
	               	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			         Calendar calendar = Calendar.getInstance();
			         String date2 = sdf.format(calendar.getTime());
			         calendar.add(Calendar.DATE, -1);
					 String date = sdf.format(calendar.getTime());
	OrgInfoForm orgInfoForm = (OrgInfoForm)request.getSession().getAttribute("orgInfoForm");
 	ArrayList codeitemlist = orgInfoForm.getCodeitemlist();
 %>

<hrms:themes></hrms:themes>
	<title></title>
	<script language="JavaScript">
	// 验证时间 
	function validate(){
		var tag=true;    
		<logic:iterate id="element" name="orgInfoForm" property="fieldlist" indexId="index">
	        <bean:define id="desc" name="element" property="itemdesc"/>
	        var valueInputs=document.getElementsByName("<%="fieldlist["+index+"].value"%>");
	        var dobj=valueInputs[0];       
	        <logic:equal name="element" property="itemtype" value="D"> 
	        if(dobj.value.length<=0){
	          	alert("${desc}"+'必须填写！');
	          	return false;
	        }
	       
	          var valueInputs=document.getElementsByName('<%="fieldlist["+index+"].value"%>');
	          var dobj=valueInputs[0];
		      tag = checkDate(dobj);      
			  if(tag==false)
			  {
			    dobj.focus();
			    return false;
			  }
	        </logic:equal> 
	     </logic:iterate>
		return tag;
	}
	
	function closedialog()
	{
	    <%if(version){%>
			if(!volidatestart()){
				return false;
			}
		<%}%>	
		var combineorgname=document.orgInfoForm.combineorgname.value;
		//前台过滤特殊字符 wusy 
		//if (/['")-><&\\\/\.()%#?$]/.test(combineorgname)) {
		//	alert("不允许输入特殊字符!");
		//	return;
		//}
		//这里修改成和新增机构时候名称限制一致  sunjian 2017-12-12
		if(combineorgname.trim().length==0){
			alert("合并后名称不能为空");
			return false;
		}
		if(combineorgname.indexOf("\‘")>-1||combineorgname.indexOf("\”")>-1||combineorgname.indexOf("\'")>-1||combineorgname.indexOf("\"")>-1||combineorgname.indexOf(" ")>-1)
       	{	
       		alert("机构名称不能包含空格或\‘或\”或\'或\"");
       		return false;
       	}

		if(!validate()){
			return false;
		}
		var obj = document.getElementById("corcodeid").style.display;
		if(obj=="block"){
			var corcode=(document.getElementsByName("corcode")[0]).value;
			<logic:equal value="@K" name="orgInfoForm" property="codesetid">
               			<logic:equal value="1" name="orgInfoForm" property="posfillable">
                   			if(corcode.length==0){
                   				alert("岗位代码不能为空");
                   				return false;
                   			}
                   		</logic:equal>
                   </logic:equal>
                   <logic:notEqual value="@K" name="orgInfoForm" property="codesetid">
               			<logic:equal value="1" name="orgInfoForm" property="unitfillable">
                   			if(corcode.length==0){
                   				<logic:equal value="UM" name="orgInfoForm" property="codesetid">
                   					alert("部门代码不能为空");
                   				</logic:equal>
                   				<logic:equal value="UN" name="orgInfoForm" property="codesetid">
                   					alert("单位代码不能为空");
                   				</logic:equal>
                   				return false;
                   			}
                   		</logic:equal>
                   </logic:notEqual>
                   if(corcode.length>0){
                   		var hashvo=new ParameterSet();
                   		hashvo.setValue("codesetid","${orgInfoForm.codesetid }");
						hashvo.setValue("corcode",corcode);
						hashvo.setValue("backdate","${orgInfoForm.backdate }");
						var request=new Request({method:'post',asynchronous:false,parameters:'',onSuccess:viewhide_ok,functionId:'0405050033'},hashvo);
						function viewhide_ok(outparamters){
							var msg=outparamters.getValue("msg");
							msg=getDecodeStr(msg);
							if(msg!="ok"){
								alert(msg);
								document.returnValue=false;
							}else{
								document.returnValue=true;
							}
						}
						if(!document.returnValue){
							return;
						}
                   }
		}
		if(!checkChangems()){
			var _div=document.getElementById("changehis");
			_div.style.display="block";
			document.getElementById("show").style.display="none";
			document.getElementById("hid").style.display="block";
			return;
		}
		if(confirm("确定要合并组织机构吗？")){
			var waitInfo=eval("wait");	
	  		waitInfo.style.display="block";
			<%if(version){%>
		   	orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_execombine=del&tarcodeitemdesc=" +$URL.encode(combineorgname)+"&combinecodeitemid="+document.orgInfoForm.combinecodeitemid.value+"&end_date="+document.orgInfoForm.end_date.value+"&selectcodeitemids="+$URL.encode(getEncodeStr("${orgInfoForm.selectcodeitemids}"));
		   	<%}else{
		   	%>
		   	orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_execombine=del&tarcodeitemdesc=" +$URL.encode(combineorgname)+"&combinecodeitemid="+document.orgInfoForm.combinecodeitemid.value+"&selectcodeitemids="+$URL.encode(getEncodeStr("${orgInfoForm.selectcodeitemids}"));
		   	<%}%>
		   	 orgInfoForm.submit();  
	   	 } 
	}
	/*function document.onkeydown(event)                //网页内按下回车触发
	{
        if(event.keyCode==13)
        {
            document.getElementById("b_ok").click();   
            return false;                               
        }
	}*/
	function _onkeydown(e) {
		e=e?e:(window.event?window.event:null);
		if(e.keyCode==13)
        {
            document.getElementById("b_ok").click();   
            return false;                               
        }
	}
	function showcorcode(obj){
		var _corcode=document.getElementById("corcodeid");
		if(obj.value==obj.options[obj.options.length-1].value){
			_corcode.style.display="";
			//谷歌浏览器 样式不正常  wangbs 20190315
			// _corcode.style.display="block";
		}else{
			_corcode.style.display="none";
		}
	}
	function volidatestart(){
		var obj=$('end_date');
		//alert(obj.value);
		var maxstartdate='<%=request.getParameter("maxstartdate") %>';
		//alert(maxstartdate);
		if(maxstartdate!=null&&maxstartdate!=''){
			var v=obj.value;
	           				if(v!=null&&v!=""){
	           					var tnew=(v).replace(/-/g, "/");
	           					var told=(maxstartdate).replace(/-/g, "/");
			   					var dnew=new Date(Date.parse(tnew));
			   					var dold=new Date(Date.parse(told));
			   					if(dnew<dold){
			   						alert("有效日期止不能小于"+maxstartdate+"!");
			   						obj.focus();
			   						obj.value='<%=date %>'; 
			   						return false;
			   					}else{
			   						return true;
			   					}
	           				}
        }else{
        	return true;
        }
	}
	
	function checkChangems()
	{
		var tag=true;    
     <logic:iterate  id="element"    name="orgInfoForm"  property="fieldlist" indexId="index"> 
     <bean:define id="fl" name="element" property="fillable"/>
        <bean:define id="desc" name="element" property="itemdesc"/>
        var valueInputs=document.getElementsByName("<%="fieldlist["+index+"].value"%>");
        var dobj=valueInputs[0];       
           if("${fl}"=='true'&&dobj.value.length<1){
          	alert("${desc}"+'必须填写！');
          	return false;
         }
 //       <logic:equal name="element" property="itemtype" value="D">   
  //        var valueInputs=document.getElementsByName('<%="fieldlist["+index+"].value"%>');
  //        var dobj=valueInputs[0];
  //        tag= checkDate(dobj) && tag;      
	//  if(tag==false)
	//  {
	//    dobj.focus();
	//    return false;
	//  }
    //    </logic:equal> 
        <logic:equal name="element" property="itemtype" value="N"> 
           <logic:lessThan name="element" property="decimalwidth" value="1"> 
             var valueInputs=document.getElementsByName('<%="fieldlist["+index+"].value"%>');
             var dobj=valueInputs[0];
              tag=checkNUM1(dobj) &&  tag ;  
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:lessThan>
	    <logic:greaterThan name="element" property="decimalwidth" value="0"> 
	     var valueInputs=document.getElementsByName('<%="fieldlist["+index+"].value"%>');
             var dobj=valueInputs[0];
               tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth},"${desc}处") &&  tag ;   
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:greaterThan>
	</logic:equal>  
      </logic:iterate>    
     return tag;   
		
		
	}
	//ie兼容trim方法
	if(!String.prototype.trim) {
	    String.prototype.trim = function () {
	        return this.replace(/^\s+|\s+$/g,'');
	    };
	}
	</script>
	
<style>
<!--
.selectPre1 {
	position: absolute;
	left: 30%;
	top:30px;
}
.selectPre2 {
	position: absolute;
	left: 30%;
	top:70px;
}
.selectPre3 {
	position: absolute;
	left: 30%;
	top:80px;
}
-->
</style>
<body onkeydown="_onkeydown(event);">
<html:form action="/workbench/orginfo/searchorginfodata">
<input type="hidden" name="msgb0110" id="msgb0110" value="" />
        <table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr align="center">
		<td valign="center" class="TableRow"  colspan="2">
		  &nbsp;机构合并&nbsp;
		</td>
	 </tr> 
	 <tr><td class="framestyle3"><table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center">

                <%
                //String selectcodeitemids = request.getParameter("selectcodeitemids"); 
               // String [] codeitemids = selectcodeitemids.split("`");
               if(version){
					 
				%>
        	<tr >
                 <td align="right"  nowrap valign="center" class="RecordRowHr" width="40%">
            	    有效日期止
            	    </td><td align="left"  nowrap valign="center" class="RecordRowHr">
            	    <input type="text" name="end_date" size="20"  value="<%=date %>" class="textColorWrite" style="BACKGROUND-COLOR:#F8F8F8;width:250px" extra="editor" dropDown="dropDownDate" onchange="javascript:if(!validate(this,'有效日期止')) {this.focus(); this.value='<%=date %>'; }"/>
                 </td>
              </tr> 
              <%} %>
            <tr>
                 <td align="right"  nowrap valign="center"  class="RecordRowHr" width="40%">
            	    合并后编码
            	    </td><td align="left"  nowrap valign="center"  class="RecordRowHr">
            	    <select name="combinecodeitemid" style="width:250px" onchange="showcorcode(this)">
            	    	<%for(int i=0;i<codeitemlist.size();i++){ 
            	    		CommonData comm = (CommonData)codeitemlist.get(i);
            	    	%>
            	    		<option value="<%=comm.getDataValue() %>"><%=comm.getDataName() %></option>
            	    	<%} %>
            	    </select>
                 </td>
              </tr>
              <tr>
                 <td align="right"  nowrap valign="center"  class="RecordRowHr">
            	   <bean:message key="label.org.combinename"/>
            	    </td><td align="left"  nowrap valign="center"  class="RecordRowHr">
            	    <input type="text" class="textColorWrite" name="combineorgname" size="20" style="BACKGROUND-COLOR:#F8F8F8;width:250px" >
                 </td>
              </tr> 
              	<tr style="display: none" id="corcodeid">
                 <td align="right"  nowrap valign="center"  class="RecordRowHr">
            	   <logic:equal value="UN" name="orgInfoForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.un"/><bean:message key="kh.field.code"/>
                   </logic:equal>
                   <logic:equal value="UM" name="orgInfoForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.um"/><bean:message key="kh.field.code"/>
                   </logic:equal>
                   <logic:equal value="@K" name="orgInfoForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.kk"/><bean:message key="kh.field.code"/>
                   </logic:equal>
            	    </td><td align="left"  nowrap valign="center"  class="RecordRowHr">
            	    <input type="text" class="textColorWrite" name="corcode" size="20" style="BACKGROUND-COLOR:#F8F8F8;width:250px" >
                 	<logic:equal value="@K" name="orgInfoForm" property="codesetid">
               			<logic:equal value="1" name="orgInfoForm" property="posfillable">
                   			<font color="red">*</font>
                   		</logic:equal>
                   </logic:equal>
                   <logic:notEqual value="@K" name="orgInfoForm" property="codesetid">
               			<logic:equal value="1" name="orgInfoForm" property="unitfillable">
                   			<font color="red">*</font>
                   		</logic:equal>
                   </logic:notEqual>
                 </td>
              	</tr> 
              <tr height="8">
                 <td align="center"  nowrap valign="center" colspan="2">
            	    
                 </td>
              </tr> 
              <logic:equal value="yes" name="orgInfoForm" property="changemsg">
              	<tr><td align="right"  nowrap valign="center">
              			<span style="display:block;" id="show" onclick="show(this);">变动历史记录<font color="#0000FF">&nbsp;&nbsp;[显示]&nbsp;</font></span>
              			<span style="display:none;"  id="hid" onclick="hid(this);">变动历史记录<font color="#0000FF">&nbsp;&nbsp;[隐藏]&nbsp;</font></span>
              		</td><td align="left"  nowrap valign="center">
              		</td>
              	</tr>
              
              <tr>
              	<td align="center" nowrap valign="center"  colspan="2" ><div id="changehis" style="display:none">
              	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"> 
              			
						<logic:iterate id="element" name="orgInfoForm"
							property="fieldlist" indexId="index">
							<%
								FieldItem abean = (FieldItem) pageContext
														.getAttribute("element");
												boolean isFillable1 = abean.isFillable();
							%>
						
					<tr>
						<logic:notEqual name="element" property="itemtype" value="M">
							<td align="right" nowrap valign="center"  class="RecordRowHr" width="40%">
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<td align="left" nowrap  class="RecordRowHr">
								<logic:equal name="element" property="codesetid" value="0">
									<logic:notEqual name="element" property="itemtype" value="D">
										<logic:equal name="element" property="itemtype" value="N">
											<logic:equal name="element" property="decimalwidth" value="0">
												<html:text maxlength="50" size="30" 
												style="BACKGROUND-COLOR:#F8F8F8;width:250px"
												styleClass="textColorWrite"
													onkeypress="event.returnValue=IsDigit2(this);"
													onblur='isNumber(this);' name="orgInfoForm"
													styleId="${element.itemid}"
													property='<%="fieldlist["
														+ index + "].value"%>' />
											</logic:equal>
											<logic:notEqual name="element" property="decimalwidth"
												value="0">
												<html:text maxlength="50" size="30" style="BACKGROUND-COLOR:#F8F8F8;width:250px"
													styleClass="textColorWrite"
													onkeypress="event.returnValue=IsDigit(this);"
													onblur='isNumber(this);' name="orgInfoForm"
													styleId="${element.itemid}"
													property='<%="fieldlist["
														+ index + "].value"%>' />
											</logic:notEqual>
											<%
												if (isFillable1) {
											%> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
										</logic:equal>
										<logic:notEqual name="element" property="itemtype" value="N">
											<html:text maxlength="50" size="30" style="BACKGROUND-COLOR:#F8F8F8;width:250px"
												styleClass="textColorWrite"
												name="orgInfoForm" styleId="${element.itemid}"
												property='<%="fieldlist[" + index
													+ "].value"%>' />
											<%
												if (isFillable1) {
											%> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
										</logic:notEqual>
									</logic:notEqual>
									<logic:equal name="element" property="itemtype" value="D">
									    <% 
											SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
											String date3 = sdf1.format(calendar.getTime());
											if(abean.getItemlength()==18)
												date3=date3.substring(0,abean.getItemlength()+1);
											else
												date3=date3.substring(0,abean.getItemlength());  
										%>
										<input type="text" name='<%="fieldlist[" + index
												+ "].value"%>'
											maxlength="50" size="29" id="${element.itemid}"
											extra="editor" style="BACKGROUND-COLOR:#F8F8F8;width:250px"
											class="textColorWrite"
											style="font-size: 10pt; text-align: left"
											dropDown="dropDownDate" value="<%=date3 %>"
											itemlength=${element.itemlength}
											dataType="simpledate"
											>
										<%
											if (isFillable1) {
										%> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
									</logic:equal>
								</logic:equal>

								<logic:notEqual name="element" property="codesetid" value="0">
									<logic:equal name="element" property="itemid" value="b0110">
										<html:hidden name="orgInfoForm"
											property='<%="fieldlist[" + index
												+ "].value"%>'
											onchange="fieldcode2(this)" />
										<html:text maxlength="50" size="30" style="BACKGROUND-COLOR:#F8F8F8;width:250px"
											styleClass="textColorWrite"
											name="orgInfoForm"
											property='<%="fieldlist[" + index
												+ "].viewvalue"%>'
											onchange="fieldcode(this,2)" />
										<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldlist[" + index
												+ "].viewvalue"%>","","1");' />&nbsp;
  <%
  	if (isFillable1) {
  %> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
									</logic:equal>
										<logic:notEqual name="element" property="itemid" value="b0110">
										<html:hidden name="orgInfoForm"
											property='<%="fieldlist[" + index
												+ "].value"%>' />
										<html:text maxlength="50" size="30" style="BACKGROUND-COLOR:#F8F8F8;width:250px"
											styleClass="textColorWrite"
											name="orgInfoForm"
											property='<%="fieldlist[" + index
												+ "].viewvalue"%>'
											onchange="fieldcode(this,2)" />
										<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="fieldlist[" + index
												+ "].viewvalue"%>","","1");' />&nbsp;
			   <%
			   	if (isFillable1) {
			   %> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
									</logic:notEqual>
									
								</logic:notEqual>
								

							</td>
							
						</logic:notEqual>
						<logic:equal name="element" property="itemtype" value="M">
							<td align="right" nowrap  valign="top" style="padding-top: 3px;" class="RecordRowHr" width="40%">
								<bean:write name="element" property="itemdesc" filter="true" />
								</td><td align="left" nowrap style="padding-left:5px;height: 60px; ">
								<html:textarea name="orgInfoForm"
									styleClass="textColorWrite"
									property='<%="fieldlist[" + index
											+ "].value"%>' cols="90"
									rows="6" style="BACKGROUND-COLOR:#F8F8F8;width:250px;height:60px;"></html:textarea>
								<%
									if (isFillable1) {
								%>
								&nbsp;
								<font color='red'>*</font>&nbsp;<%
									}
								%>
							</td>
						</logic:equal>
						</tr>
						</logic:iterate>
              	</table></div></td>
              </tr>
              </logic:equal>  
               <tr height="40">
                 <td align="center"  nowrap valign="center">
            	    
                 </td>
              </tr> 
              </table>
              </td>
              </tr>
          </table>       
     <table  width="100%" align="center">
          <tr>
            <td align="center"  height="35px">
         	  <input id="b_ok"  type="button" name="b_ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="closedialog()">
	          <input type="button" name="br_return" value="<bean:message key="button.return"/>" class="mybutton" onclick="window.history.back(-1);">
            </td>
          </tr>          
    </table>
</html:form >
<div id='wait' style='position:absolute;top:200;left:150;display:none;'>
  <table border="1" width="25%" cellspacing="0" cellpadding="4" class="table_style" height="60" align="center">
           <tr>
             <td class="td_style" height=24>正在处理数据请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div> 
</body>