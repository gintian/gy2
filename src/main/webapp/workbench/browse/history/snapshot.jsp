<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
%>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript">
Array.prototype.remove=function(dx)
{
  if(isNaN(dx) || dx>this.length)
     return false;
  for(var i=0,n=0;i<this.length;i++)
  {
     if(this[i]!=this[dx])
     {
        this[n++]=this[i]
     }
  }
  this.length-=1
}
function save(){
	var return_item="";
	var return_value="";
	var rights=document.getElementById("right_fields");
	for(var i=0,n=0;i<rights.length;i++)
    {
    	return_item+=rights[i].value+",";
    	return_value+=rights[i].text+"、";
    }
	//19/3/22 xus 浏览器兼容 返回参数
	if(window.showModalDialog){
	   	top.returnValue=return_item+"#"+return_value;
	}else{
		if(parent.opener.getsnapshot_callbackfunc)
			parent.opener.getsnapshot_callbackfunc(return_item+"#"+return_value);
	}
	windowClose();
	/* 
    if(navigator.appName.indexOf("Microsoft")!= -1){
    	returnValue=return_item+"#"+return_value;
    	window.close();
    }else{
    	top.returnValue=return_item+"#"+return_value;
    	top.close();
    }
     */
}
//19/3/22 xus 浏览器兼容 关闭窗口
function windowClose(){
	if(window.showModalDialog){
		parent.window.close();
	}else{
		parent.top.close();
	}
}
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
                var resultlist=new Array();
                for(var i=0,n=0;i<fieldlist.length;i++)
                {
                   var obj=fieldlist[i];
                  
                      resultlist[n]=fieldlist[i];
                      n++;
                   
                }
		AjaxBind.bind(personHistoryForm.left_fields,resultlist);		
	}
				
	function searchFieldList()
	{
	   var tablename=$F('setlist');	
	   var In_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'0201001206'});
	}	


function additems(sourcebox_id,targetbox_id)
{
  var left_vo,right_vo,vos,voss,i;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  left_vo=vos[0];
  var nofield=true;

  voss= document.getElementsByName(targetbox_id);  
  right_vo=voss[0];
  for(i=0;i<left_vo.options.length;i++)
  {
   if(left_vo.options[i].selected)
   {
    if(right_vo.options.length==0)
     {
        var no = new Option();
        no.value=left_vo.options[i].value;
        no.text=left_vo.options[i].text;
        right_vo.options[right_vo.options.length]=no;
     }else{
        for(var j=0;j<right_vo.options.length;j++)
        {
           if(right_vo.options[j].value==left_vo.options[i].value)
           {
               nofield=false; 
           }         
        }
          if(nofield)
           {
              var no = new Option();
              no.value=left_vo.options[i].value;
              no.text=left_vo.options[i].text;
              right_vo.options[right_vo.options.length]=no;
           }
           nofield=true;
    }
   }
 }
 return true;	  	
}

function sumtype(e,itemid){
	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
	var key = e.button;
	if(key==2){
		var hashvo=new ParameterSet();
   		hashvo.setValue("itemid",itemid);
   		hashvo.setValue("type","judge");	
   		var request=new Request({asynchronous:false,onSuccess:issumtype,functionId:'0201001235'},hashvo);
		function issumtype(outparamters){
			var msg=outparamters.getValue("msg"); 
			if("no"!=msg){
				var thecodeurl="/workbench/browse/history/parameters_deploy.do?b_sumtype=link&type=query&msg="+msg+"&itemid="+itemid;
       			var dw=300,dh=150,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
       			var  return_vo= window.showModalDialog(thecodeurl, "", 
              		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");     
				if(return_vo){
					var hashvo=new ParameterSet();
			   		hashvo.setValue("itemid",itemid);
			   		hashvo.setValue("sumtype",return_vo);	
			   		hashvo.setValue("type","save");	
			   		var request=new Request({asynchronous:false,onSuccess:null,functionId:'0201001235'},hashvo);
				}
			}
		}
	}
}
</script>
<hrms:themes />
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.snapshotTable {
	width:expression(document.body.clientWidth-10);
	height:expression(document.body.clientHeight-20);
	margin-left:-4px;
}
</style>
<%}else{ %>
<style>
.snapshotTable {
	margin-top:10px;
	margin-left:-4px;
	width:expression(document.body.clientWidth-10);
	height:expression(document.body.clientHeight-20);
}
</style>
<%} %>
<html:form action="/workbench/browse/history/parameters_deploy">
	<table width="100%" align="center" border="0" cellpadding="0"
		cellspacing="0" class="snapshotTable">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="RecordRow">
					<thead>
						<tr>
							<td class="TableRow" nowrap colspan="3"><bean:message key="label.query.selectfield" /></td>
						</tr>
					</thead>
					<tr>
						<td align="center" nowrap>
							<table width="100%" border="0" cellspacing="0" align="center"
								cellpadding="0" style="border-top: 0;">
								<tr>
									<td align="center" width="46%">
										<table align="center" width="100%" border="0" cellspacing="0"
											cellpadding="0" style="margin-top:-2px;">
											<tr>
												<td align="left"><bean:message key="selfservice.query.queryfield" /></td>
											</tr>
											<tr>
												<td>
													<hrms:optioncollection name="personHistoryForm"
														property="setlist" collection="sed" />
													<html:select name="personHistoryForm" property="setlist"
														size="1" onchange="searchFieldList();" style="width:100%;">
														<html:options collection="sed" property="dataValue"
															labelProperty="dataName" />
													</html:select>
												</td>
											</tr>
											<tr><td height="6px"></td>
											</tr>
											<tr>
												<td align="center">
													<select name="left_fields" multiple="multiple"
														ondblclick="additems('left_fields','right_fields');"
														style="height: 210px; width: 100%; font-size: 9pt">
													</select>
												</td>
											</tr>
										</table>
									</td>
									<td width="8%" align="center">
										<table>
											<tr><td align="center">
												<html:button styleClass="mybutton" property="b_addfield"
													onclick="additems('left_fields','right_fields');">
													<bean:message key="button.setfield.addfield" />
												</html:button>
											</td></tr>
											<tr><td height="30px"></td></tr>
											<tr><td align="center">
												<html:button styleClass="mybutton" property="b_delfield"
													onclick="removeitem('right_fields');">
													<bean:message key="button.setfield.delfield" />
												</html:button>
											</td></tr>
										</table>
									</td>
									<td width="46%" align="center">
										<table align="center" width="100%" border="0" cellspacing="0"
											cellpadding="0" style="margin-top:5px;">
											<tr>
												<td width="100%" align="left">
													<bean:message key="static.ytarget" /></td>
											</tr>
											<tr>
												<td width="100%" align="left">
													<hrms:optioncollection name="personHistoryForm"
														property="rightlist" collection="list" />
													<html:select name="personHistoryForm"
														styleId="right_fields" property="right_fields"
														multiple="multiple" size="10"
														ondblclick="removeitem('right_fields');"
														style="height:240px;width:100%;font-size:9pt"
														onmouseup="sumtype(event,this.value);">
														<html:options collection="list" property="dataValue"
															labelProperty="dataName" />
													</html:select>

												</td>
											</tr>
										</table>
									</td>
									<td width="8%" align="center">
										<table>
											<tr><td align="center">
												<html:button styleClass="mybutton" property="b_up"
													onclick="upItem($('right_fields'));">
													<bean:message key="button.previous" />
												</html:button>
											</td></tr>
											<tr><td height="30px"></td></tr>
											<tr><td align="center">
												<html:button styleClass="mybutton" property="b_down"
													onclick="downItem($('right_fields'));">
													<bean:message key="button.next" />
												</html:button>
											</td></tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr height="35">
						<td align="center" nowrap colspan="3" class="RecordRow">
							<input type="button" name="btnsave"
								value='<bean:message key="button.ok" />'
								onclick="setselectitem('right_fields');save();" class="mybutton">
							<input type="button" name="btnreturn"
								value='<bean:message key="button.cancel"/>'
								onclick="windowClose();" class="mybutton">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script>
	searchFieldList();
    if(!getBrowseVersion() || getBrowseVersion()==10){ //非IE浏览器兼容性   wangb 20180127
            //修改样式  wangbs
            var outForm = document.getElementsByTagName("form")[0];
            outForm.style.width = "99%";

            var outTable = outForm.children[0];
            outTable.style.marginLeft = "0px";
    }
</script>