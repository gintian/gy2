<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem" %>
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
 %>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/org/orgdata/orgedit.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language="javascript">
   function openOrgTreeDialog()
   {
   	<logic:equal value="1" name="orgInfoForm" property="busi_have" >
       //加参数ishavedept，因为走此链接需要从form里拿ishavedept，而此链接跟机构编码是共用的form，从此处进入拿不到值（这里是机构维护进入）   guodd 2017-07-23 解决树加载不对的问题
	var thecodeurl="/org/orginfo/searchtransferorgtree.do?b_query=link&nmodule=4&ishavedept=${orgInfoForm.ishavedept}";
</logic:equal>  
<logic:notEqual value="1" name="orgInfoForm" property="busi_have" >
//加参数ishavedept，因为走此链接需要从form里拿ishavedept，而此链接跟机构编码是共用的form，从此处进入拿不到值（这里是机构维护进入）   guodd 2017-07-23 解决树加载不对的问题
	var thecodeurl="/org/orginfo/searchtransferorgtree.do?b_query=link&ishavedept=${orgInfoForm.ishavedept}";
</logic:notEqual>
        var oldobj=orgInfoForm.tarorgname;
        var hiddenobj=orgInfoForm.transfercodeitemid;
            var theArr=new Array(oldobj,hiddenobj); 
        // var popwin= window.showModelessDialog(thecodeurl, theArr,
        // "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");

	   //兼容各个浏览器  改用Extwindow对象  wangbs 2019年3月14日10:50:31
        chooseOrgWin(theArr,thecodeurl);
   }
   function chooseOrgWin(theArr,thecodeurl){
       var dw=300,dh=400;
       Ext.create('Ext.window.Window',{
           id:'chooseorgtree',
           title:'选择机构',
           width:dw,
           height:dh,
           theArr:theArr,
           resizable:false,
           modal:true,
           autoScroll:false,
           autoShow:true,
           autoDestroy:true,
           html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
           renderTo:Ext.getBody()
       });
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
  		      tag= checkDate(dobj) && tag;      
  			  if(tag==false)
  			  {
  			    dobj.focus();
  			    return false;
  			  }
  	        </logic:equal> 
  	     </logic:iterate>
  		return tag;
  	}
  	
 function check()
 {
 <%if(version){%>
 if(!volidatestart()){
			return false;
	}
<%}%>	

	if(!validate()){
		return false;
	}
	
 	var length=orgInfoForm.transfercodeitemid.value.length;
 	if(length==0)
	 	return false;
 	else if(!checkChangems()){
 		var _div=document.getElementById("changehis");
		_div.style.display="block";
		return false;
 	}
	else if(confirm("确定要划转组织机构吗？")){
		var waitInfo=eval("wait");	
	  	waitInfo.style.display="block";
		return true;
	}else{
		return false;
	}
 }
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
   </script>
<html:form action="/workbench/orginfo/searchorginfodata">
<table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr align="center">
		<td valign="center" class="TableRow"  colspan="2">
		  &nbsp;<bean:message key="label.org.selecttarorg"/>&nbsp;
		</td>
	 </tr> 
	 <tr><td class="framestyle3"><table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center">
                <%
             	//版本号大于等于50才显示这些功能
             	//xuj 2009-10-30 在organazition（vorganization虚拟机构）表中增加“有效日期起”、“有效日期止”两个字段
   	  	if(version){
	             	
					
              %>
        	<tr>
                 <td align="right"  nowrap valign="center"  class="RecordRowHr" width="40%">
            	    <bean:message key="conlumn.codeitemid.end_date"/>
            	    </td><td align="left"  nowrap valign="center"  class="RecordRowHr">
            	    <input type="text" name="end_date" class="textColorWrite" value="<%=date %>" maxlength="50" style="BACKGROUND-COLOR:#F8F8F8;width:250px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='<%=date %>'; }"/>                                            
                 </td>
              </tr> 
              <%} %>
            <tr>
                 <td align="right"  nowrap valign="center"  class="RecordRowHr" width="40%">
            	     <html:hidden name="orgInfoForm" property="transfercodeitemid"/> 
                  目标机构名
            	    </td><td align="left"  nowrap valign="center"  class="RecordRowHr">
                <html:text name="orgInfoForm" property="tarorgname" readonly="false" styleClass="textColorWrite" style="BACKGROUND-COLOR:#F8F8F8;width:250px"/> 
                 <img  src="/images/code.gif" align="absmiddle" onclick='javascript:openOrgTreeDialog();'/>  
                 </td>
              </tr>
               <tr height="8">
                 <td align="center"  nowrap valign="center" colspan="2">
            	    
                 </td>
              </tr> 
              <logic:equal value="yes" name="orgInfoForm" property="changemsg">
              	<tr><td align="right"  nowrap valign="center">
              			<span style="display:block;" id="show" onclick="show(this);">变动历史记录<font color="#0000FF">&nbsp;&nbsp;[显示]&nbsp;&nbsp;</font></span>
              			<span style="display:none;"  id="hid" onclick="hid(this);">变动历史记录<font color="#0000FF">&nbsp;&nbsp;[隐藏]&nbsp;&nbsp;</font></span>
              		</td><td align="left"  nowrap valign="center">
              		</td>
              	</tr>
              <tr>
              	<td align="center" nowrap valign="center"  colspan="2" ><div id="changehis" style="display:none"><table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"> 
              			
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
											class="textColorWrite"
											extra="editor" style="BACKGROUND-COLOR:#F8F8F8;width:250px"
											style="font-size: 10pt; text-align: left"
											dropDown="dropDownDate" value="<%=date3 %>"
											itemlength=${element.itemlength}
											dataType="simpledate"
											/>
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
								</td>
								<td align="left" nowrap  style="padding-left:5px;height: 60px; ">
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
            <td align="center" height="35px">
         	  <hrms:submit styleClass="mybutton"  property="b_exetransfer" onclick="return check()">
                  <bean:message key="button.transfer"/>
	     </hrms:submit> 
	     <input type="button" name="b_return" value="<bean:message key="button.return"/>" class="mybutton" onclick="window.history.back(-1);">
            </td>
          </tr>          
    </table>
</html:form>
<div id='wait' style='position:absolute;top:200;left:150;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height=24><bean:message key="classdata.isnow.wiat"/></td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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