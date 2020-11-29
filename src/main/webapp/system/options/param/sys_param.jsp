<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.hjsj.sys.VersionControl"%>
 <%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
 String bosflag = "";
 if(userView!=null){
	 bosflag = userView.getBosflag();
 }
   String path = request.getSession().getServletContext().getRealPath("/js");
   if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
   {
  	  path=session.getServletContext().getResource("/js").getPath();//.substring(0);
      if(path.indexOf(':')!=-1)
  	  {
		 path=path.substring(1);   
   	  }
  	  else
   	  {
		 path=path.substring(0);      
   	  }
      int nlen=path.length();
  	  StringBuffer buf=new StringBuffer();
   	  buf.append(path);
  	  buf.setLength(nlen-1);
   	  path=buf.toString();
   }
   //System.out.println(buf.toString());
 %>

<script language="javascript"> 
function goback()
{
  document.sysParamForm.action="/system/sys_param_panel.do";
  document.sysParamForm.submit();  
}
function transform()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("type","dialog");
   	var request=new Request({asynchronous:false,onSuccess:dialogOk,functionId:'10200700009'},hashvo); 
}
function dialogOk(outparamters)
{
	var result=outparamters.getValue("result");	
	if(result=="yes")
	{
		var pinyin_field=outparamters.getValue("pinyin_field");
		var result = ifdel(pinyin_field);
		if(result==true)
		{
			jindu();
			var hashvo=new ParameterSet();
			hashvo.setValue("type","transform");			
   			var request=new Request({method:'post',asynchronous:false,onSuccess:transaformOK,functionId:'10200700009'},hashvo); 	
   			//document.sysParamForm.action="/system/options/param/sys_param.do?b_piny=link&type=transform";
   			//document.sysParamForm.submit();
		}
			
	}else
	{
		alert("请选择要转换拼音简码指标或选择转换拼音简码指标未构库");
	}

}
function ifdel(pinyin_field)
{
	return ( confirm('确定要将"姓名"转换成拼音简码,转换后的简码将存放在"'+pinyin_field+'"中？') );	
}
function jindu(){
	var x=document.body.scrollLeft+event.clientX;
    var y=document.body.scrollTop+event.clientY-300; 
	var waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}
function transaformOK(outparamters)
{
	closejindu();
}
function closejindu(){
	var waitInfo=eval("wait");
	waitInfo.style.display="none";
}
function loadDymicParam()
{
    var hashvo=new ParameterSet();
	hashvo.setValue("type","dialog");
   	var request=new Request({asynchronous:false,onSuccess:loadDymicParamOK,functionId:'30200710242'},hashvo); 
}
function loadDymicParamOK(outparameters)
{
   alert("参数加载完毕！");
}
function refresh()
{
    jindu1();
   sysParamForm.action="/system/options/param/sys_param.do?b_refresh=refresh";
   sysParamForm.submit();
   document.getElementById("resh").disabled=true;
}
function jindu1(){
    //新加的，屏蔽整个页面不可操作
    document.all.ly.style.display="";
    document.all.ly.style.width=document.body.clientWidth;
    document.all.ly.style.height=document.body.clientHeight;

    var x=(window.screen.width-700)/2;
    var y=(window.screen.height-500)/2;
    var waitInfo=eval("wait1");
    waitInfo.style.top=y;
    waitInfo.style.left=x;
    waitInfo.style.display="";
}
function disablebutton(id){
	sysParamForm.action="/system/options/param/sys_param.do?b_delete=link";
   	sysParamForm.submit();
	 document.getElementById(id).disabled=true;
}
</script>
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=10); opacity:0.1;background-color:#FFF;z-index:2;left:0px;display:none;"></div>
<div id='wait1' style='position:absolute;top:285;left:80;display:none;'>
    <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
        <tr>
            <td class="td_style" height=24>
                正在刷新数据字典,请稍候......
            </td>
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
<html:form action="/system/options/param/sys_param" style="margin-left:-2px;margin-top:10px;">
<div id='wait' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在转码，请稍候...
				</td>
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
<hrms:tabset name="sys_param" width="100%" height="90%" type="true"> 
      <%        
        if(!userView.isBbos()&&!"hcm".equals(bosflag)){
      %>
      <hrms:tab name="param1" label="电话邮箱" visible="true" url="/selfservice/param/phoneparam.do?b_phone=link" function_id="30061">
      </hrms:tab>
      <hrms:tab name="param5" label="唯一性指标" visible="true" url="/system/options/param/set_sys_param.do?b_query=link">
      </hrms:tab> 
      <%
         }
      %>        

      <%        
        if(userView.isBbos()||"hcm".equals(bosflag)){
      %>
      <hrms:tab name="param5" label="单位性质&自动计算公式" visible="true" url="/system/options/param/set_sys_param.do?b_query=link" function_id="30015B1">
      </hrms:tab>       
      <%
         }
      %>   
       <hrms:tab name="param6" label="单位介绍" visible="true" url="/hire/parameterSet/configureParameter.do?b_orgIntro=inti&isVisible=0&type=1" function_id="31053,30015Y">
      </hrms:tab> 
      <hrms:tab name="param7" label="文档管理" visible="true" url="/selfservice/param/documentparam.do?b_query=link" function_id="300157">
      </hrms:tab>   
      <hrms:tab name="param10" label="system.option.renyuankfw" visible="true" url="/system/options/otherparam/showdbitem.do?b_query=link" function_id="30015Z">
      </hrms:tab>
      <hrms:tab name="param11" label="sysetm.option.renyuanlxfw" visible="true" url="/system/options/otherparam/showsetitem.do?b_query=link" function_id="30015U">
      </hrms:tab>               
      <hrms:tab name="param8" label="子集&指标分类" visible="true" url="/system/param/sysinfosort.do?b_query=link&tag=set_a" function_id="300158">
      </hrms:tab>
      <hrms:tab name="param9" label="业务分类" visible="true" url="/system/param/operationsort.do?b_query=link" function_id="300159">
      </hrms:tab>
      <hrms:tab name="param2" label="兼职" visible="true" url="/system/options/parttimeparamset/initParttimeParamSet.do?b_init=init" function_id="30015B2">
      </hrms:tab>	
      <hrms:tab name="param3" label="领导班子" visible="true" url="/general/deci/leader/param.do?b_query=init" function_id="300155,070904">
      </hrms:tab> 
      <hrms:tab name="param12" label="帐号规则" visible="true" url="/sysconfig/param/sysparam.do?b_sys_param=link&module=SYS_SYS_PARAM" function_id="30015K">
      </hrms:tab>
      <hrms:tab name="param13" label="文件存放目录" visible="true" url="/module/system/filepathsetting/FilePathSetting.html" function_id="30015L">
      </hrms:tab>
      <hrms:tab name="param4" label="其它参数" visible="true" url="/selfservice/param/otherparam.do?b_other=link" function_id="070103,30015X">
      </hrms:tab> 
               
</hrms:tabset> 
<table cellpadding="0" cellspacing="0"><tr><td style="height:35px;padding-left:8px;">
  <input type="hidden" name="path" value="<%=path%>"/>
  <input type="button" name="re" class="mybutton" id="resh" onclick="refresh();" value="<bean:message key="button.refresh"/>"/>
  <input type="button" class="mybutton" id="b_delete" onclick="disablebutton('b_delete');" value="清空临时文件夹"/>
  <input type="button" name="pinyin"  value="生成拼音简码" class="mybutton" onclick='transform()' >  
  <input type="button" name="dymic" value="加载动态参数" class="mybutton" onclick="loadDymicParam();"/> 
  <%        
   if(userView.isBbos()||"hcm".equals(bosflag)){
  %>
  	<logic:equal name="sysParamForm" property="edition" value="4">
<input type="button" name="btnreturn" value='<bean:message key="button.return" />' onclick="goback();" class="mybutton">  
	</logic:equal>
  <%
      }
   %>
   </td></tr></table>
</html:form>
<script language="javascript">
   //function sys_param_afterTabChange(TabSettabSet, stringoldName, stringnewName)
   //{
	// if(confirm('xxxx？'))
   //    return "abort";//返回abort则不进行切换页面
  // }	
  setTimeout(function(){
  	if(!getBrowseVersion() || getBrowseVersion()==10){//非IE兼容模式样式 修改   wangb 20190319
  	 var _tabsetpane_sys_param = document.getElementById('_tabsetpane_sys_param');
  	 _tabsetpane_sys_param.style.height = parseInt(_tabsetpane_sys_param.style.height) - 37 +'px';
  	 var tabs_panels = _tabsetpane_sys_param.children[1];
  	 tabs_panels.style.height =  parseInt(tabs_panels.style.height) - 37 +'px';
  	 var _tabpane_sys_param0 = document.getElementById('_tabpane_sys_param0');
  	 _tabpane_sys_param0.style.height = tabs_panels.style.height;
  	}
  
  },100);
  
</script>
