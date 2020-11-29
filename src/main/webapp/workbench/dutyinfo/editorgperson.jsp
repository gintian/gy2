<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				com.hrms.struts.taglib.CommonData,
				com.hjsj.hrms.actionform.duty.DutyInfoForm"%>
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
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="javascript">
   function openOrgTreeDialog()
   {
        var thecodeurl="/org/orginfo/searchtarorgtree.do?b_query=link&nmodule=4"; 
        var oldobj=dutyInfoForm.tarorgname;
        var hiddenobj=dutyInfoForm.tarorgid;
            var theArr=new Array(oldobj,hiddenobj); 
        var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
        //var popwin= window.showModalDialog(thecodeurl, theArr, 
        //"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
        //ie弹窗 改为Ext弹窗 兼容多浏览器   wangb 20190314
	    var win = Ext.create('Ext.window.Window',{
			id:'chooseorgtree',
			title:'选择机构',
			width:dw,
			height:dh,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
			renderTo:Ext.getBody()
		});
		win.theArr = theArr;
   }
	/**显示人员*/
	function showPersonList(outparamters)
	{
		var personlist=outparamters.getValue("personlist");
		AjaxBind.bind(dutyInfoForm.left_fields,personlist);
	}			
	/**查询人员*/
	function searchPersonList()
	{
	   //var orgid=$F('searchbolishorglist');
	   //alert(dutyInfoForm.bolishorgname.value);
	     var movedpersons = $('right_fields');
	   var movedpersonsstr="";
	   for(var i=0;i<movedpersons.options.length;i++){
	   		movedpersonsstr+=movedpersons.options[i].value+",";
	   }
	   var in_paramters="orgid="+dutyInfoForm.bolishorgname.value + "&dbpre=" + dutyInfoForm.dbpre.value+"&movedpersonsstr="+movedpersonsstr;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showPersonList,functionId:'16010000014'});
	}
	/**显示目标机构*/
	function showTarOrgList(outparamters)
	{
	   var tarorglist=outparamters.getValue("tarorglist");
	   AjaxBind.bind(dutyInfoForm.right_fields,tarorglist);
	}	
	/**查询目标机构*/
	function changepos()
	{
	   var in_paramters="orgid="+dutyInfoForm.tarorgid.value;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showTarOrgList,functionId:'16010000016'});
	}
	function movepersonsubmit()
	{
	    if(confirm("<bean:message key="label.org.moveperson"/>?"))
          {
             dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_moveperson=link";
             dutyInfoForm.submit();
          }
    }
    function refreshperson()
	{
	    dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?br_nullify=link";
        dutyInfoForm.submit();
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
    function exebolishsubmit()
    {
    <%if(version){%>
    	 if(!volidatestart()){
			return false;
		}
		<%}%>
		
		<%if("true".equals(SystemConfig.getPropertyValue("bolishorg_eporg"))){%>
        var messageText="<bean:message key="label.duty.nocheckpersonmessage"/>";
        <%}else{%>
           var messageText="<bean:message key="label.duty.nocheckpersonmessage.one"/>";
        <%}%>
    	if(!validate()){
    		return false;
    	}
  	   //IE下name当id用  wangbs 20190315
 	   // var personlist = document.getElementById("left_fields").options;
 	   var personlist = document.getElementsByName("left_fields")[0].options;

       var value_s="";
      // if("<bean:write name="dutyInfoForm" property="ishavepersonmessage"/>"=="")
      // {
       <%if("true".equals(SystemConfig.getPropertyValue("bolishorg_eporg"))){%>
	       if(personlist.length>0){
	           if(!confirm(messageText))
	           {
	        	   return;
	           }
		   }          
               dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_bolishs=link";
               dutyInfoForm.submit();
                    
       <% }else{ %>
    	   if(personlist.length>0){
	           if(!confirm(messageText))
	           {
	        	   return;
	           }
	        }
       
           dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_bolishs=link";
           dutyInfoForm.submit();
       <% } %>
     //  }
    }
    // 验证时间 
  	function validate(){
  		var tag=true;    
  		<logic:iterate id="element" name="dutyInfoForm" property="childfielditemlist" indexId="index">
  	        <bean:define id="desc" name="element" property="itemdesc"/>
  	        var valueInputs=document.getElementsByName("<%="childfielditemlist["+index+"].value"%>");
  	        var dobj=valueInputs[0];       
  	        <logic:equal name="element" property="itemtype" value="D"> 
  	        if(dobj.value.trim().length<=0){
  	          	alert("${desc}"+'必须填写！');
  	          	return false;
  	        }
  	       
  	          var valueInputs=document.getElementsByName('<%="childfielditemlist["+index+"].value"%>');
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
	function deleter(code)
    {
			var currnode=parent.frames['mil_menu'].Global.selectedItem;
			if(currnode==null)
					return;
			if(currnode.load)
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if(code.toUpperCase()==currnode.childNodes[i].uid.toUpperCase())
					currnode.childNodes[i].remove();
			}
			//currnode.expand();
   }
function back()
{
	dutyInfoForm.action = "/workbench/dutyinfo/searchdutyinfodata.do?b_return=link";
	dutyInfoForm.submit();
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
function isNumber(obj)
{
  		var checkOK = "-0123456789.";
 		var checkStr = obj.value;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return;
  		var count = 0;
  		var theIndex = 0;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		if(ch=='-')
    		{
    			count=count+1;
    			theIndex=i+1;
    		}
    		for (j = 0;  j < checkOK.length;  j++)
     	    if (ch == checkOK.charAt(j))
       			 break;
    		if (j == checkOK.length)
   		    {
  			   allValid = false;
   			   break;
  		    }
    		if (ch == ".")
    		{
     			 allNum += ".";
     			 decPoints++;
  			 }
    	  else if (ch != ",")
      		allNum += ch;
  		}
  	if(count>1 || (count==1 && theIndex>1))
  			allValid=false;
  	if (decPoints > 1 || !allValid) 
  	{
  		//alert(INPUT_NUMBER_VALUE+'!');
  		obj.value=''; 
  	    obj.focus();
  	}  	   
}
//ie兼容trim方法
if(!String.prototype.trim) {
    String.prototype.trim = function () {
        return this.replace(/^\s+|\s+$/g,'');
    };
}
</script>
<html:form action="/workbench/dutyinfo/searchdutyinfodata">
<div id="first" style="filter:alpha(Opacity=100);display=block;">
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
		<bean:message key="label.duty.bolishorg"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	   <td align="center"  width="100%"  colspan="3" <logic:notEmpty name="dutyInfoForm" property="ishavepersonmessage">class="RecordRow_lr"</logic:notEmpty>>
   	      <bean:write name="dutyInfoForm" property="ishavepersonmessage"/>                                
           </td>                    
   	  </tr>
   	  <tr>
   	   <td align="left"  width="100%" nowrap colspan="3" style="padding-left:9px;"  class="RecordRow_lr">
   	      <html:select name="dutyInfoForm" property="dbpre" size="1" onchange="searchPersonList();">   
                   <html:optionsCollection property="dbprelist" value="dataValue" label="dataName"/>
              </html:select> 
              
           </td>                    
   	  </tr>
   	   <tr>
            <td width="100%" align="top" class="RecordRowTop0" nowrap colspan="3">
              <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                 <td align="center"  width="42%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                        <bean:message key="label.org.bolishorg"/>&nbsp;&nbsp;
                    </td>
                    </tr>
                    <tr>
                      <td align="left"  nowrap>
                        <html:select name="dutyInfoForm" property="bolishorgname" size="1" onchange="searchPersonList();">   
                          <html:optionsCollection property="bolishlist" value="dataValue" label="dataName"/>
                         </html:select>  
                         &nbsp;&nbsp;
                         <%
             	//版本号大于等于50才显示这些功能
             	//xuj 2009-10-30 在organazition（vorganization虚拟机构）表中增加“有效日期起”、“有效日期止”两个字段
             	if(version){	             	
					
              %>
           &nbsp;&nbsp;<bean:message key="conlumn.codeitemid.end_date"/>&nbsp;<input type="text" name="end_date" class="textColorWrite" value="<%=date %>" maxlength="50" style="BACKGROUND-COLOR:#F8F8F8;width:100px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='<%=date %>'; }" />                                            
           <%} %>                                       
                        </td>  
                    </tr>
                   <tr>
                       <td align="center">
                        <html:select name="dutyInfoForm" property="left_fields" multiple="multiple"  size="10"  style="height:209px;width:100%;font-size:9pt">
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
                     <bean:message key="label.org.persontarorg"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                   <td width="100%" align="left">
                      <html:hidden name="dutyInfoForm" property="tarorgid"/>
                     <html:text name="dutyInfoForm" property="tarorgname" readonly="false"  styleClass="textColorWrite" onchange="changepos()"/> 
                     <img align=absmiddle  src="/images/code.gif" onclick='javascript:openOrgTreeDialog();'/>
                   </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     <html:select name="dutyInfoForm" property="right_fields"  size="10"  style="height:209px;width:100%;font-size:9pt">
 		          <html:optionsCollection property="movepersons" value="dataValue" label="dataName"/>    
 		     </html:select>
                  </td>
                  </tr>
                  </table>             
                </td>                                           
                </tr>
                <logic:equal value="yes" name="dutyInfoForm" property="changemsg">
               <tr>
               <td width="100%" align="top" class="" nowrap colspan="3">
               <table width="100%">
                <tr><td align="left"  nowrap valign="center" class="RecordRowHr">
                        <span style="display:block;" id="show" onclick="show(this);">变动历史记录<font color="blue">&nbsp;&nbsp;[显示]</font></span>
                        <span style="display:none;"  id="hid" onclick="hid(this);">变动历史记录<font color="blue">&nbsp;&nbsp;[隐藏]</font></span>
                    </td><td align="left"  nowrap valign="center">
                    </td>
                </tr>
              
              <tr>
                <td align="center" nowrap valign="center"  colspan="2" ><div id="changehis" style="display:none"><table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"> 
                        
                        <logic:iterate id="element" name="dutyInfoForm"
                            property="childfielditemlist" indexId="index">
                            <%
                                FieldItem abean = (FieldItem) pageContext
                                                        .getAttribute("element");
                                                boolean isFillable1 = abean.isFillable();
                            %>
                        
                    <tr>
                        <logic:notEqual name="element" property="itemtype" value="M">
                            <td width="18%">&nbsp;</td><td align="right" nowrap valign="center" align=absmiddle class="RecordRowHr">
                                <bean:write name="element" property="itemdesc" filter="true" />
                            </td><td align="left" nowrap class="RecordRowHr">
                                <logic:equal name="element" property="codesetid" value="0">
                                    <logic:notEqual name="element" property="itemtype" value="D">
                                        <logic:equal name="element" property="itemtype" value="N">
                                            <logic:equal name="element" property="decimalwidth" value="0">
                                                <html:text maxlength="50" size="30" 
                                                styleClass="textColorWrite"
                                                style="BACKGROUND-COLOR:#F8F8F8;width:250px"
                                                    onkeypress="event.returnValue=IsDigit2(this);"
                                                    onblur='isNumber(this);' name="dutyInfoForm"
                                                    styleId="${element.itemid}"
                                                    property='<%="childfielditemlist["
                                                        + index + "].value"%>' />
                                            </logic:equal>
                                            <logic:notEqual name="element" property="decimalwidth"
                                                value="0">
                                                <html:text maxlength="50" size="30" style="BACKGROUND-COLOR:#F8F8F8;width:250px"
                                                    styleClass="textColorWrite"
                                                    onkeypress="event.returnValue=IsDigit(this);"
                                                    onblur='isNumber(this);' name="dutyInfoForm"
                                                    styleId="${element.itemid}"
                                                    property='<%="childfielditemlist["
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
                                                name="dutyInfoForm" styleId="${element.itemid}"
                                                property='<%="childfielditemlist[" + index
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
                                        <input type="text" name='<%="childfielditemlist[" + index
                                                + "].value"%>'
                                            maxlength="50" size="29" id="${element.itemid}"
                                            extra="editor" style="BACKGROUND-COLOR:#F8F8F8;width:250px"
                                            style="font-size: 10pt; text-align: left"
                                            class="textColorWrite"
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
                                        <html:hidden name="dutyInfoForm"
                                            property='<%="childfielditemlist[" + index
                                                + "].value"%>'
                                            onchange="fieldcode2(this)" />
                                        <html:text maxlength="50" size="30" style="BACKGROUND-COLOR:#F8F8F8;width:250px"
                                            name="dutyInfoForm"
                                            styleClass="textColorWrite"
                                            property='<%="childfielditemlist[" + index
                                                + "].viewvalue"%>'
                                            onchange="fieldcode(this,2)" />
                                        <img  align=absmiddle src="/images/code.gif"
                                            onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="childfielditemlist[" + index
                                                + "].viewvalue"%>","","1");' />&nbsp;
  <%
    if (isFillable1) {
  %> &nbsp;<font color='red'>*</font>&nbsp;<%
    }
 %>
                                    </logic:equal>
                                        <logic:notEqual name="element" property="itemid" value="b0110">
                                        <html:hidden name="dutyInfoForm"
                                            property='<%="childfielditemlist[" + index
                                                + "].value"%>' />
                                        <html:text maxlength="50" size="30" style="BACKGROUND-COLOR:#F8F8F8;width:250px"
                                            name="dutyInfoForm"
                                            styleClass="textColorWrite"
                                            property='<%="childfielditemlist[" + index
                                                + "].viewvalue"%>'
                                            onchange="fieldcode(this,2)" />
                                        <img align=absmiddle src="/images/code.gif"
                                            onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="childfielditemlist[" + index
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
                            <td width=""></td><td align="right" nowrap valign="middle" class="RecordRowHr">
                                <bean:write name="element" property="itemdesc" filter="true" />
                                </td><td align="left" nowrap  class="RecordRowHr">
                                <html:textarea name="dutyInfoForm"
                                    styleClass="textColorWrite"
                                    property='<%="childfielditemlist[" + index
                                            + "].value"%>' cols="90"
                                    rows="6" style="BACKGROUND-COLOR:#F8F8F8;width:250px"></html:textarea>
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
               <tr height="40">
                 <td align="center"  nowrap valign="center">
                    
                 </td>
              </tr>
              </table>             
            </td>
            </tr>    
            <tr>
               <td width="100%" align="center" nowrap colspan="3"  style="height: 35px">
               <hrms:submit styleClass="mybutton"  property="b_checkperson">
                    <bean:message key="button.checkperson"/>
	         </hrms:submit>
                 <input type="button" name="savebutton"  value="<bean:message key="button.exebolish"/>" class="mybutton" onclick='exebolishsubmit();'>   
                 <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick='back();'>
               </td>
            </tr>     
</table>
</div>

</html:form>
<script language="javascript">
   //var ViewProperties=new ParameterSet();
   searchPersonList();
   if(!getBrowseVersion() || getBrowseVersion() == 10){
   	var tarorgname = document.getElementsByName('tarorgname')[0];
   	tarorgname.style.width='194px';
   }
</script>