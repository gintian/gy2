
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
 <SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
 <script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
 <!-- 引入ext框架      wangb 20180803 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
 <SCRIPT LANGUAGE=javascript>
    	function statset()
    	{
    	   var target_url="/general/static/select_code.do?b_search=link";
    	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    	   var dw=500,dh=350,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    	   //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=356,height=328'); 
           if(getBrowseVersion()){
           		var return_vo=window.showModalDialog(iframe_url,arguments,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth="+dw+"px;dialogHeight="+dh+"px;resizable=no;status=no;");  
	           if(return_vo)
    	       {
        	        if(return_vo.flag=="true")
            	    {
                	  	staticFieldForm.action="/general/static/static_general_result.do?b_setcode=link&querycond="+$URL.encode(return_vo.querycond)+"&userbases="+$URL.encode(return_vo.userbase)+"&viewuserbases="+return_vo.viewuserbase;    	      
                  		staticFieldForm.submit();
                	}else
                	{
                  		return false;
                 	}
           	  }  
           }else{//非IE浏览器 使用Ext弹窗 wangb 20180803 bug 39379
           		var dialog=[];dialog.dw=dw;dialog.dh=dh+50;dialog.iframe_url=iframe_url;
            	openWin(dialog);
           }
        }
        
  //ext window 弹窗 方法
 function openWin(dialog){
		    Ext.create("Ext.window.Window",{
		    	id:'statistical',
		    	width:dialog.dw,
		    	height:dialog.dh,
		    	title:'请设置',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+dialog.iframe_url+"'></iframe>"
		    }).show();	
}
//关闭ext window 弹窗   wangb 20180207 
function winClose(){
       		Ext.getCmp('statistical').close();
}
//ext 弹窗返回数据调用方法   wangb 20180207 bug 34602
function returnValue(return_vo){
       		winClose();
       		if(return_vo)
    	       {
        	        if(return_vo.flag=="true")
            	    {
                	  	staticFieldForm.action="/general/static/static_general_result.do?b_setcode=link&querycond="+$URL.encode(return_vo.querycond)+"&userbases="+$URL.encode(return_vo.userbase)+"&viewuserbases="+return_vo.viewuserbase;    	      
                	  	staticFieldForm.submit();
                	}else
                	{
                  		return false;
                 	}
           	  }  
}       
 function testchart(e)
 {
     var name=e.name;    
     if(name!='')
     {		
    	 name = getEncodeStr(name);
    	 name = $URL.encode(name);
      	 staticFieldForm.action="/general/static/static_data.do?b_data=data&&showLegend="+name+"&stat_type=general&result=${staticFieldForm.result}&history=${staticFieldForm.history}&querycond="+$URL.encode('${staticFieldForm.querycond}');
      	 staticFieldForm.submit();
     }
}
</SCRIPT>
<br> 
<hrms:themes />
<table  align="center">
<tr align="left">  
	 <logic:equal name="staticFieldForm" property="infor_Flag" value="1"> 
    <td valign="top"  nowrap>
        <a href="###" onclick="javascript:statset();">设置统计范围</a>  
    </td>
  </logic:equal>
 <%--    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/static_general_result.do?chart_type=12&result=${staticFieldForm.result}&history=${staticFieldForm.history}">立体直方图</a>
    </td> --%>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/static_general_result.do?chart_type=11&result=${staticFieldForm.result}&history=${staticFieldForm.history}">平面直方图</a>
    </td>
  <%--   <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/static_general_result.do?chart_type=5&result=${staticFieldForm.result}&history=${staticFieldForm.history}">立体圆饼图</a>
    </td> --%>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/static_general_result.do?chart_type=20&result=${staticFieldForm.result}&history=${staticFieldForm.history}">平面圆饼图</a>
    </td>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/general/static/static_general_result.do?chart_type=1000&result=${staticFieldForm.result}&history=${staticFieldForm.history}">平面折线图</a>
    </td>
  </tr>
  </table>
  <html:form action="/general/static/static_general_result">
 <table  align="center" width="100%">
     <html:hidden property="statid"/>
           <tr>
            <td align="center" nowrap colspan="5">
            <logic:equal  name="staticFieldForm" property="chart_type"  value="1000">
              <hrms:chart name="staticFieldForm" title="${staticFieldForm.snamedisplay}" xangle="${staticFieldForm.xangle }" scope="session" legends="jfreemap" data="" width="-1" height="530" chart_type="1000" pointClick="testchart">
	    	     </hrms:chart>
            </logic:equal>
             <logic:notEqual  name="staticFieldForm" property="chart_type"  value="1000">
	 	         <hrms:chart name="staticFieldForm" title="${staticFieldForm.snamedisplay}" xangle="${staticFieldForm.xangle }" scope="session" legends="list" data="" width="-1" height="530" chart_type="${staticFieldForm.chart_type}" pointClick="testchart">
	    	     </hrms:chart>
	    	 </logic:notEqual>    
            </td>
          </tr>  
          <tr>
            <td align="center" nowrap colspan="5">
             
            </td>
          </tr>         
</table>
<div style="position:relative; width:50px; margin-top:; margin-top:;left:50%;margin-left:-0px; ">
 <hrms:submit styleClass="mybutton" property="b_back">
                <bean:message key="static.back"/>
	             </hrms:submit>
</div>
</html:form>
<script type="text/javascript">
function refresh(){
	document.getElementById("___CONTAINER___Nchart__0").style.position="absolute";
	document.getElementById("___CONTAINER___Nchart__0").style.left="50%";
	document.getElementById("___CONTAINER___Nchart__0").style.margin="0 0 0 -335px";
	document.getElementById("___CONTAINER___Nchart__0").style.top="60px";
}
if(navigator.appName.indexOf("Microsoft")== -1)
refresh();
</script>
