<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				com.hrms.struts.taglib.CommonData,
				com.hjsj.hrms.actionform.org.OrgInfoForm"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem,com.hrms.struts.constant.SystemConfig" %>				
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
	var thecodeurl="/org/orginfo/searchtarorgtree.do?b_query=link&nmodule=4";
</logic:equal>  
<logic:notEqual value="1" name="orgInfoForm" property="busi_have" >
	var thecodeurl="/org/orginfo/searchtarorgtree.do?b_query=link";
</logic:notEqual> 
         
        var oldobj=orgInfoForm.tarorgname;
        var hiddenobj=orgInfoForm.tarorgid;
		var theArr=new Array(oldobj,hiddenobj);

		//兼容各个浏览器  改用Extwindow对象  wangbs 2019年3月14日10:50:31
        // var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
        // var popwin= window.showModalDialog(thecodeurl, theArr,
        // "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
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
	/**显示人员*/
	function showPersonList(outparamters)
	{
		var personlist=outparamters.getValue("personlist");
		AjaxBind.bind(orgInfoForm.left_fields,personlist);
	}
	
    function serachPersons(outparamters)
	{
		var personsize=outparamters.getValue("personsize");
		var messageText = "<bean:message key="label.duty.nocheckpersonmessage.one"/>";
		if(personsize > 0){
		 if(!confirm(messageText))
		 
               {
                	   return;
                }
		}
	}		
	/**查询人员*/
	function searchPersonList()
	{
	   //var orgid=$F('searchbolishorglist');
	   //alert(orgInfoForm.bolishorgname.value);
	   
	     var movedpersons = $('right_fields');
	   var movedpersonsstr="";
	   for(var i=0;i<movedpersons.options.length;i++){
	   		movedpersonsstr+=movedpersons.options[i].value+",";
	   }
	   var in_paramters="orgid="+orgInfoForm.bolishorgname.value + "&dbpre=" + orgInfoForm.dbpre.value+"&movedpersonsstr="+movedpersonsstr+"&form=1";
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showPersonList,functionId:'16010000014'});
	}
	/**显示目标机构*/
	function showTarOrgList(outparamters)
	{
	   var tarorglist=outparamters.getValue("tarorglist");
	   AjaxBind.bind(orgInfoForm.right_fields,tarorglist);
	}	
	/**查询目标机构*/
	function changepos()
	{
	   var in_paramters="orgid="+orgInfoForm.tarorgid.value;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showTarOrgList,functionId:'16010000016'});
	}
	function movepersonsubmit()
	{
	  if(confirm("<bean:message key="label.org.moveperson"/>?"))
          {
             orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_moveperson=link";
             orgInfoForm.submit();
          }
        }
        function refreshperson()
	{
	    orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?br_initbolish=link";
            orgInfoForm.submit();
        }
        function volidatestart(){
		var obj=$('end_date');
		var maxstartdate='<%=request.getParameter("maxstartdate") %>';
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
    	
        function exebolishsubmit()
        {
        	<%if(version){%>
        	if(!volidatestart()){
				return false;
			}
			<%}%>
        	var value_s="";
        	var dbpres = "";
        	var personlist ="";
        	 var person = new Array();
        	<%
        	    OrgInfoForm oif = (OrgInfoForm)session.getAttribute("orgInfoForm");
				ArrayList bolishlist = oif.getBolishlist();
				ArrayList  dblist = oif.getDbprelist();
        	%>
        	   // zhangcq  2016/7/15 查询各个人员库的对应的人数
        	    <%
				for(int i=0;i<dblist.size();i++){
					CommonData dbdata = (CommonData)dblist.get(i);
				%>
				  dbpres+="<%=dbdata.getDataValue().toString()%>"+",";
				<%
				}
				%>
				var movedpersons = $('right_fields');
	             var movedpersonsstr="";
	            for(var i=0;i<movedpersons.options.length;i++){
	   		   movedpersonsstr+=movedpersons.options[i].value+",";
	     }
	  
	var in_paramters="orgid="+orgInfoForm.bolishorgname.value + "&dbpres=" +dbpres+"&movedpersonsstr="+movedpersonsstr+"&form=";
   	var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:serachPersons,functionId:'16010000014'});
        	<%if("true".equals(SystemConfig.getPropertyValue("bolishorg_eporg"))){%>
            var messageText="<bean:message key="label.duty.nocheckpersonmessage"/>";
	        <%}else{%>
	           var messageText="<bean:message key="label.duty.nocheckpersonmessage.one"/>";
	        <%}%>
		   if(!validate()){
				return false;
		   }
		   //无用代码影响现有代码的运行  注释掉  wangbs 20190315
     	   // var personlist = document.getElementById("left_fields").options;
           if("<bean:write name="orgInfoForm" property="ishavepersonmessage"/>"=="")
           {
           	  /* if(personlist.length>0){
                   if(!confirm(messageText))
                   {
                	   return;
                   }
        	   }*/
              orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_bolishs=link";
               orgInfoForm.submit();
               <%
				for(int y=0;y<bolishlist.size();y++){
					CommonData data = (CommonData)bolishlist.get(y);
				%>
				value_s="<%=data.getDataValue().toString()%>";
				deleter(value_s);
				value_s = "";
				<%
				}
				
				%>
                       
           }
           else
           {
        	  /* if(personlist.length>0){
                   if(!confirm(messageText))
                   {
                	   return;
                   }
        	   }*/
             orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_bolishs=link";
             orgInfoForm.submit();
             <%
				for(int y=0;y<bolishlist.size();y++){
					CommonData data = (CommonData)bolishlist.get(y);
				%>
				value_s="<%=data.getDataValue().toString()%>";
				deleter(value_s);
				value_s = "";
				<%
				}
				
				%>
           }
        }
	function deleter(code)
   {
   			//alert(code);
			var currnode=parent.frames['nil_menu'].Global.selectedItem;
			//alert(currnode);
			if(currnode==null)
					return;
			if(currnode.load){
				if(code.toUpperCase()==currnode.uid.toUpperCase())
						currnode.remove();
				for(var i=0;i<=currnode.childNodes.length-1;i++){
					if(code.toUpperCase()==currnode.childNodes[i].uid.toUpperCase())
						currnode.childNodes[i].remove();
				}
			}
			//currnode.expand();
   }
function back()
{
	orgInfoForm.action = "/workbench/orginfo/searchorginfodata.do?b_search=link";
	orgInfoForm.submit();
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
</script>
<html:form action="/workbench/orginfo/searchorginfodata">
<div id="first" style="filter:alpha(Opacity=100);display=block;">
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
		<bean:message key="label.org.bolishorg"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr><td class="framestyle3"><table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center">
   	  <tr>
   	   <td align="center"  width="100%" colspan="3" <logic:notEmpty name="orgInfoForm" property="ishavepersonmessage">class="RecordRowHr"</logic:notEmpty> >
   	      <bean:write name="orgInfoForm" property="ishavepersonmessage"/>                                
           </td>                    
   	  </tr>
   	  <tr>
   	   <td align="left"  width="100%"  nowrap colspan="3"  class="RecordRowHr">
   	      <html:select name="orgInfoForm" property="dbpre" size="1" onchange="searchPersonList();">   
                   <html:optionsCollection property="dbprelist" value="dataValue" label="dataName"/>
              </html:select> 
              
           </td>                    
   	  </tr>
   	   <tr>
            <td width="100%" align="top" class="" nowrap colspan="3"  class="RecordRowHr">
              <table width="100%">
                <tr>
                 <td align="center"  width="42%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                        <bean:message key="label.org.bolishorg"/>
                    </td>
                    </tr>
                    <tr>
                      <td align="left"  nowrap>
                        <html:select name="orgInfoForm" property="bolishorgname" size="1" onchange="searchPersonList();">   
                          <html:optionsCollection property="bolishlist" value="dataValue" label="dataName"/>
                         </html:select>  
                         &nbsp;&nbsp;
                         <%
             	//版本号大于等于50才显示这些功能
             	//xuj 2009-10-30 在organazition（vorganization虚拟机构）表中增加“有效日期起”、“有效日期止”两个字段
             	if(version){
					
              %>
           &nbsp;&nbsp;<bean:message key="conlumn.codeitemid.end_date"/>&nbsp;<input type="text" name="end_date" class="textColorWrite" value="<%=date %>" maxlength="50" style="BACKGROUND-COLOR:#F8F8F8;width:100px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='<%=date %>'; }"/>                                            
           <%} %>                                       
                        </td>  
                    </tr>
                   <tr>
                       <td align="center">
                        <html:select name="orgInfoForm" property="left_fields" multiple="multiple"  size="10"  style="height:209px;width:100%;font-size:9pt">
                        </html:select>
                       </td>
                    </tr>
                   </table>
                </td> 
                <td width="8%" align="center">
                    <input type="button" name="transferbutton"  value="<bean:message key="button.moveperson"/>" class="mybutton" onclick='movepersonsubmit();'>  
                </td>                     
                <td width="42%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="label.org.persontarorg"/>
                  </td>
                  </tr>
                  <tr>
                   <td width="100%" align="left">
                      <html:hidden name="orgInfoForm" property="tarorgid"/> 
                     <html:text name="orgInfoForm" property="tarorgname" readonly="false"  styleClass="textColorWrite" onchange="changepos()"/> 
                     <img align=absmiddle src="/images/code.gif" onclick='javascript:openOrgTreeDialog();'/>&nbsp;
                   </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     <html:select name="orgInfoForm" property="right_fields"  size="10"  style="height:209px;width:100%;font-size:9pt">
 		          <html:optionsCollection property="movepersons" value="dataValue" label="dataName"/>    
 		     </html:select>
                  </td>
                  </tr>
                  </table>             
                </td>                                           
                </tr>
              </table>             
            </td>
            </tr> 
               <logic:equal value="yes" name="orgInfoForm" property="changemsg">
               <tr>
               <td width="100%" align="top" class="" nowrap colspan="3">
               <table width="100%">
              	<tr><td align="left"  nowrap valign="center"  class="RecordRowHr">
              			<span style="display:block;" id="show" onclick="show(this);">变动历史记录<font color="#0000FF">&nbsp;&nbsp;[显示]</font></span>
              			<span style="display:none;"  id="hid" onclick="hid(this);">变动历史记录<font color="#0000FF">&nbsp;&nbsp;[隐藏]</font></span>
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
							<td width="18%">&nbsp;</td><td align="right" nowrap valign="center"  class="RecordRowHr">
								<bean:write name="element" property="itemdesc" filter="true" />
							</td><td align="left" nowrap  class="RecordRowHr">
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
							<td width=""></td><td align="right" nowrap  valign="top" style="padding-top: 3px;" class="RecordRowHr">
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
           		</table>
           		</td>
              </tr>
              </logic:equal>  
            </table>
            </td>
            </tr>  
</table>
<table  width="100%" align="center">
 
          <tr>
               <td width="100%" align="center" nowrap colspan="3" height="35px">
               <hrms:submit styleClass="mybutton"  property="b_checkperson">
                    <bean:message key="button.checkperson"/>
	         </hrms:submit>
                 <input type="button" name="savebutton"  value="<bean:message key="button.exebolish"/>" class="mybutton" onclick='exebolishsubmit();'>   
                 <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick='back()'>
               </td>
            </tr>         
    </table>
</div>

</html:form>
<script language="javascript">
   //var ViewProperties=new ParameterSet();
   searchPersonList();
</script>