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
	String bosflag = "";
	if (userView != null) {
		bosflag = userView.getBosflag();
	}
%>
<%
	String org_m = request.getParameter("org_m");
	org_m = org_m != null ? org_m : "";
%>
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
    	return_value+=rights[i].text+"`";
    }
	//19/3/14 xus 浏览器兼容 导出excel按钮
	// 兼容谷歌、ie wanbgs 20190318
	if(getBrowseVersion()){
        if(parent.Ext){
            var operateTarget = parent.Ext.getCmp('org_leaderParameter');
            if(operateTarget){
                operateTarget.msg = return_item+"#"+return_value;
            }else{
                top.returnValue=return_item+"#"+return_value;
            }
        }else{
            top.returnValue=return_item+"#"+return_value;
        }
	}else{
	    if(parent.executeOutFile_callbackfunc){
            parent.executeOutFile_callbackfunc(return_item+"#"+return_value);
        }else{
	        if(parent.Ext){
	            var operateTarget = parent.Ext.getCmp('org_leaderParameter');
                if(operateTarget){
                    operateTarget.msg = return_item+"#"+return_value;
				}
            }else{
            	//19/3/22 xus 浏览器兼容 历史时点-配置-谷歌 选择指标按钮 不弹窗
            	if(parent.opener.getquerynorm_callbackfunc)
            		parent.opener.getquerynorm_callbackfunc(return_item+"#"+return_value);
            }
		}
	}
	windowClose();
}
//19/3/14 xus 浏览器兼容 导出excel按钮 关闭窗口方法
function windowClose(){
    if(parent.Ext){
        if(parent.Ext.getCmp('histroy_exportExcel')){
            parent.Ext.getCmp('histroy_exportExcel').close();
        }else if(parent.Ext.getCmp('org_leaderParameter')){
            parent.Ext.getCmp('org_leaderParameter').close();
        }else{
            parent.window.close();
		}
    }else{
        parent.window.close();
	}
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
        left_vo.options.remove(i);
        i--;
        right_vo.options[right_vo.options.length]=no;
     }else{
        for(var j=0;j<right_vo.options.length;j++)
        {
           if(right_vo.options[j].text==left_vo.options[i].text)
           {
               nofield=false; 
           }         
        }
          
              var no = new Option();
              no.value=left_vo.options[i].value;
              no.text=left_vo.options[i].text;
              left_vo.options.remove(i);
              i--;
              if(nofield)
              {   
              right_vo.options[right_vo.options.length]=no;
              }
           nofield=true;
     }
   }
 }
 return true;	  	
}
</script>
<hrms:themes />
<%
	if ("hcm".equalsIgnoreCase(bosflag)) {
%>
<style>
.querynormTable {
	width: expression(document . body . clientWidth-10);
	height: expression(document . body . clientHeight-20);
}
</style>
<%
	} else {
%>
<style>
.querynormTable {
	margin-top: 10px;
	padding-left: 5px;
	width: expression(document . body . clientWidth-10);
	height: expression(document . body . clientHeight-20);
}
</style>
<%
	}
%>
<html:form action="/system/options/info_param">
	<table width="100%" align="center" border="0" cellpadding="0"
		cellspacing="0" class="querynormTable" >
		<thead>
			<tr height="10">
				<td colspan="3">
				</td>
			</tr>
		</thead>
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="RecordRow">
					<tr>
						<td align="center" nowrap style="border-right: 1px solid #C4D8EE;">
							<table width="100%" border="0" cellspacing="0" align="center"
								cellpadding="0" style="border-top: 0;">
								<tr>
									<td align="center" width="46%" style="padding-left: 4px;">
										<table align="center" width="100%" border="0" cellspacing="0"
											cellpadding="0">
											<tr>
												<td align="left">
													<%
														if (org_m.length() > 0) {
													%>
													<bean:message key="system.param.sysinfosort.bsubset" />
													<%
														} else {
													%>
													<bean:message key="selfservice.query.queryfield" />
													<%
														}
													%>
												</td>
											</tr>
											<tr>
												<td align="center">
													<hrms:optioncollection name="personHistoryForm"
														property="leftlist" collection="lel" />
													<html:select name="personHistoryForm"
														property="left_fields" multiple="multiple" size="10"
														ondblclick="additems('left_fields','right_fields');"
														style="height:230px;width:100%;font-size:9pt">
														<html:options collection="lel" property="dataValue"
															labelProperty="dataName" />
													</html:select>
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
													onclick="additems('right_fields','left_fields');">
													<bean:message key="button.setfield.delfield" />
												</html:button>
											</td></tr>
										</table>
									</td>
									<td width="46%" align="center">
										<table align="center" width="100%" border="0" cellspacing="0"
											cellpadding="0">
											<tr>
												<td width="100%" align="left">
													<%
														if (org_m.length() > 0) {
													%>
													<bean:message key="system.param.sysinfosort.ysubset" />
													<%
														} else {
													%>
													<bean:message key="selfservice.query.queryfieldselected" />
													<%
														}
													%>
												</td>
											</tr>
											<tr>
												<td width="100%" align="left">
													<hrms:optioncollection name="personHistoryForm"
														property="rightlist" collection="list" />
													<html:select name="personHistoryForm"
														property="right_fields" styleId="right_fields"
														multiple="multiple" size="10"
														ondblclick="additems('right_fields','left_fields');"
														style="height:230px;width:100%;font-size:9pt">
														<html:options collection="list" property="dataValue"
															labelProperty="dataName" />
													</html:select>

												</td>
											</tr>
										</table>
									</td>
									<td width="8%" align="center" >
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
						<td align="center" class="RecordRow" nowrap colspan="3">
							<input type="button" name="savereturn"
								value='<bean:message key="button.ok"/>'
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
    if(!getBrowseVersion() || getBrowseVersion()==10){ //非IE浏览器兼容性   wangb 20180127
            //修改样式  wangbs
            var outForm = document.getElementsByTagName("form")[0];
            outForm.style.width = "99%";

            var outTable = outForm.children[0];
            outTable.style.marginLeft = "0px";
    }
</script>