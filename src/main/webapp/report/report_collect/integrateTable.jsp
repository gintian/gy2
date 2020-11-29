<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
				 java.util.*,
				 com.hrms.hjsj.utils.Sql_switcher,
                 com.hjsj.hrms.actionform.report.report_collect.IntegrateTableForm"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>   
<%@ page import="com.hrms.struts.constant.SystemConfig"%>             
<%
    UserView userView = (UserView) request.getSession().getAttribute(
	WebConstant.userView);
	String username = userView.getUserName();
	String name=userView.getUserFullName();
	 IntegrateTableForm integrateTableForm=(IntegrateTableForm)session.getAttribute("integrateTableForm");	
	 String[] right_fields=integrateTableForm.getRight_fields();
	 StringBuffer temp_str=new StringBuffer("");
	 for(int i=0;i<right_fields.length;i++)
	 {
	 	temp_str.append("~"+right_fields[i]);
	 }
	 /**
	 String aurl = (String)request.getServerName();
	String port=request.getServerPort()+"";
	String prl=request.getProtocol();
	int idx=prl.indexOf("/");
	prl=prl.substring(0,idx);    
	String url_s=prl+"://"+aurl+":"+port;
	**/
	String url_s=SystemConfig.getCsClientServerURL(request);
	//注意：给插件的数据库类型按实际类型传
	String dbtype=String.valueOf(Sql_switcher.searchDbServerFlag());
	String fields=userView.getFieldpriv().toString();
	String tables=userView.getTablepriv().toString();
	EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
  	String license=lockclient.getLicenseCount();
   	int version=userView.getVersion();
  	 if(license.equals("0"))
        version=100+version;
   int usedday=lockclient.getUseddays();
	
%>
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
	<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
	<SCRIPT LANGUAGE=javascript>
	
	function go_left(ite){
	    var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(post+1);
	    if(temp_str1!=0)
	    {
	    	var next_item;
	    	var next_item1=temp_str1-1;
	    	var next_item = "document.editReportForm."+temp_str.substring(0,post+1);
	    	next_item1 = next_item + next_item1;
	    	var new_object=eval(next_item1);	    	
	    	if(new_object!=null&&new_object.type!='hidden')
	    		new_object.focus();
	    }
	  }
	  
	function go_right(ite){
	    var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(post+1);
	    var next_item;
	    var next_item1=parseInt(temp_str1)+1;
	    var next_item = "document.editReportForm."+temp_str.substring(0,post+1);
	    next_item1 = next_item + next_item1;
	    var new_object=eval(next_item1);
	    if(new_object!=null&&new_object.type!='hidden')
	    	new_object.focus();
	 
	  }
	  
	  
	function go_up(ite){
	    var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(1,post);
	    var next_item1=parseInt(temp_str1)-1;
	    var next_item = "document.editReportForm.a"+next_item1+temp_str.substring(post);
	    var new_object=eval(next_item);
	    if(new_object!=null&&new_object.type!='hidden')
	    	new_object.focus();

	  }
	  
	  
	function go_down(ite){
	   var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(1,post);
	    var next_item1=parseInt(temp_str1)+1;
	    var next_item = "document.editReportForm.a"+next_item1+temp_str.substring(post);
	    var new_object=eval(next_item);
	    if(new_object!=null&&new_object.type!='hidden')
	    	new_object.focus();

	  }
	
	  function myPrint() {
		  <%--  newwindow=window.open("/servlet/IntegratePrint?username=<%=username%>",'glWin','toolbar=yes,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=170,left=220,resizable=yes');
	       window.location.target="_blank";
		  window.location.href ="/servlet/IntegratePrint?username=<%=username%>"; --%>
		  //浏览器上没装pdf插件时并不能直接打开pdf，直接改为下载pdf
		  window.location.href ="/servlet/IntegratePrint?username="+$URL.encode('<%=username%>');
	       
	  }
	  
	  
	  function exportExcel()
	 {
	 	var hashvo=new ParameterSet();
	    hashvo.setValue("tabid",'${integrateTableForm.tabid}');
	    hashvo.setValue("unitcode" ,'${integrateTableForm.unitcode}');
	    hashvo.setValue("nums",'${integrateTableForm.nums}');
	    hashvo.setValue("cols",'${integrateTableForm.cols}');
	    hashvo.setValue("temp_str","<%=(temp_str.substring(1))%>");
	    
		var request=new Request({method:'post',asynchronous:false,onSuccess:outFile,functionId:'03030000028'},hashvo);			
	 }
	 
	 
	 
	 function outFile(outparamters)
	 {
		 var outName=outparamters.getValue("outName");
	     window.location.target="_blank";
	     window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
	 }
	  
	  function csPrint()
	{
		var waitInfo=eval("wait");     
        waitInfo.style.display="block";
        if(!AxManager.setup("TJBP", "TJBPreview1", 0, 0, csPrint3, AxManager.tjbpkgName))
            return false;
        csPrint3();
	   
	} 
	 var timecount=0;
	function csPrint3()
	  {
	  
	   var obj = document.getElementById('TJBPreview1');  
	      var aurl="<%=url_s%>";
	      var DBType=<%=dbtype%>;
	      var UserName="su";
	      try{
	       obj.SetURL(aurl);
	       csPrint2();
			}catch(e1){
			timecount=timecount+1;
			if(timecount<20){
			setTimeout("csPrint3()",2000);
			}else{
				 var waitInfo=eval("wait");	   
	 			 waitInfo.style.display="none";
	 			 alert("插件下载失败，请查看网速是否太慢或者插件被禁用！");
			}
			}
	  }
	  
	  function csPrint2()
	  {
	      var obj = document.getElementById('TJBPreview1');  
	       var aurl="<%=url_s%>";
	      var DBType=<%=dbtype%>;
	      var UserName="su"; 
	      obj.SetURL(aurl);
	      obj.SetDBType(DBType);

	      obj.SetUserName("<%=username%>");
	      obj.SetUserFullName("<%=name%>");
	      obj.SetSuperUser(1);  // 1为超级用户,0非超级用户
     	  obj.SetUserMenuPriv("<%=fields%>");  // 指标权限, 逗号分隔, 空表示全权
    	  obj.SetUserTablePriv("<%=tables%>");  // 子集权限, 逗号分隔, 空表示全权
    	  obj.SetHrpVersion("<%=version%>");// 设置版本号40,43,50,+100表示试用版
   		  obj.SetTrialDays("<%=usedday%>","30");// 设置试用天数
   		  
   		  
   		obj.SetParamType(1);   
	    obj.SetReportType(1);  // 综合表    
	    obj.SetTableID(${integrateTableForm.tabid});
	    obj.SetUnitCode("${integrateTableForm.unitcode}");
	 
	    
	    var nums='${integrateTableForm.num2s}';
	    var colRowFlag=1;
	    if(nums.indexOf('b')!=-1)
	    	colRowFlag=0;
	    
	    var atemp=nums.substring(3);
		var temps=atemp.split(",");
		var ColRows="";
		
		var rowSerialNo='${integrateTableForm.rowSerialNo}';
		var colSerialNo='${integrateTableForm.colSerialNo}';
		
		if(rowSerialNo.length>0)
			rowSerialNo=rowSerialNo.substring(0,1);
		if(colSerialNo.length>0)
			colSerialNo=colSerialNo.substring(0,1);
		
		for(var i=0;i<temps.length;i++)
		{
			if(temps[i].length>0)
			{	
				var num=temps[i].substring(1)*1;
				if(colRowFlag==0&&colSerialNo.length>0)
				{
					if(num>colSerialNo*1)
						num++;
				}	
				else if(colRowFlag==1&&rowSerialNo.length>0)
				{
					if(num>rowSerialNo*1)
						num++;
				}
				ColRows+=","+num;
		
			}
		}
		var str="<%=(temp_str.substring(1))%>";
		str=replaceAll( str,'~','`');
	    var param="<ColRowFlag>"+colRowFlag+"</ColRowFlag><ColRows>"+ColRows.substring(1)+"</ColRows>" +
                "<Items>"+str+"</Items>";
       var waitInfo=eval("wait");	   
	  waitInfo.style.display="none";
        obj.SetIntegratedReportParam(param);    
	     obj.SetCellValues("${integrateTableForm.integrateValues}");
	     try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
         obj.ShowReportModal();
		
	  
	  }
	  function goback(){
	     //update by wangchaoqun on 2014-10-8 重新获取数据，以免回退数据对应不上
	     integrateTableForm.action="/report/report_collect/IntegrateTable.do?b_selectTableTerm=search&cols=${integrateTableForm.cols}&unitcode=${integrateTableForm.unitcode}&tabid=${integrateTableForm.tabid}&nums=${integrateTableForm.nums}";
		 integrateTableForm.submit();
	  }
	  
	</SCRIPT>
 <link href="/css/css1_report.css" rel="stylesheet" type="text/css">
 <html:form action="/report/report_collect/IntegrateTable">	
   <div id="menu" style='width:80%'></div>

  <br>
 	${integrateTableForm.html}
 
 </html:form>
 
 <div id="TJBP">
	</div>
	<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style"  height="87" align="center">
           <tr>
             <td class="td_style" height=24>正在加载报表数据....</td>
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
<script>
function newButton(text,items){
	var button = Ext.create('Ext.Button', {
		    text: text,
		    border:0,
		    menu:[{xtype: 'menu',width: 150,floating: false,items:items}]
		});
	return button;
}

function createNewWindow(state,tid){
	tid=getEncodeStr(tid);
	var thecodeurl="/general/print/page_options.do?b_edit=link`state="+state+"`id="+tid; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	Ext.widget('window',{
		title:'<bean:message key="kq.report.pagesetup" />',
		id:'pagesetup_window',
		width:750,
		height:470,
		modal:true,
    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
	}).show();
}

var items = new Array();
items.push({text: '<bean:message key="kq.report.pagesetup" />',icon:"/images/add_del.gif",handler:function(){createNewWindow('1',${integrateTableForm.tabid})}});
items.push({text: '<bean:message key="edit_report.outPDF" />',icon:"/images/print.gif",handler:function(){myPrint()}});
items.push({text: '<bean:message key="general.inform.muster.output.excel" />',icon:"/images/print.gif",handler:function(){exportExcel()}});
if(Ext.isIE && window.navigator.platform!="Win64"){//插件只支持ie32为浏览器
	items.push({text: '<bean:message key="button.print" />',icon:"/images/print.gif",handler:function(){csPrint()}});
}
items.push({text: '<bean:message key="reportcheck.return" />',icon:"/images/prop_ps.gif",handler:function(){goback()}});
var buttons = newButton('<bean:message key="conlumn.mediainfo.filename" />',items);
Ext.create('Ext.panel.Panel', {
    border:false,
    height:30,
    padding:0,
    renderTo: 'menu',
    items:buttons
});
</script>