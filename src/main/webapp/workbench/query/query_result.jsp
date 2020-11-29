<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>

<style>
.myfixedDiv {  
    overflow:auto; 
    height:expression(document.body.clientHeight-130);
    width:expression(document.body.clientWidth-30); 
    BORDER-BOTTOM: #99BBE8 1pt solid; 
    BORDER-LEFT: #99BBE8 1pt solid; 
    BORDER-RIGHT: #99BBE8 1pt solid; 
    BORDER-TOP: #99BBE8 1pt solid;
}
.fixedtab { 
	overflow:auto; 
	BORDER-BOTTOM: 0; 
    BORDER-LEFT: 0pt solid; 
    BORDER-RIGHT: 0pt solid; 
    BORDER-TOP: 0pt solid ; 	
}
</style>
<%
int i = 0;
String ver = (String)request.getParameter("ver");
ver=ver!=null&&ver.length()>0?ver:"";
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String url="";
	if(userView != null)
	{
	    url=userView.getBosflag();
	}
 	String bosflag="";
    if(userView!=null){
     bosflag = userView.getBosflag();
    }
	
%>
<script language="javascript">
  function winhref(url,target)
  {
   if(url=="")
      return false;
   if(!target)
      target="";
   if(target=="")
      target="il_body";   
   queryInterfaceForm.action=url;
   queryInterfaceForm.target=target;
   queryInterfaceForm.submit();
  }  
  function winopen(nbase,a0100,a0100_encrypt)
  {
     if(a0100=="")
      return false;
    var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
   //queryInterfaceForm.action="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase="+nbase+"&inforkind=${queryInterfaceForm.type}&tabid=${queryInterfaceForm.tabid}&multi_cards=-1";
   //queryInterfaceForm.target="_blank";
   //queryInterfaceForm.submit();
   var url="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase="+nbase+"&a0100="+a0100_encrypt+"&inforkind=${queryInterfaceForm.type}&tabid=${queryInterfaceForm.tabid}&multi_cards=-1";
      url = url.replace(/&/g,"`");
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
      window.open(iframe_url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-50)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
  }

  document.oncontextmenu = function() {return false;}
  
  function submitChange()
  {

	  //bug 34393 add 自助服务兼容性  不跳转    wangb 20180207
	   <logic:equal name="queryInterfaceForm" property="home" value="5">	  
          queryInterfaceForm.action="/workbench/query/gquery_interface.do?b_query=link";
	      queryInterfaceForm.submit();	 
      </logic:equal>
      <logic:equal name="queryInterfaceForm" property="home" value="10">	  
	      queryInterfaceForm.action="/workbench/query/query_interface.do?b_mquery=link&Switch=true";
		  queryInterfaceForm.submit();	 
	  </logic:equal>
	  <logic:equal name="queryInterfaceForm" property="home" value="3"> 	  
	     queryInterfaceForm.action="/workbench/query/gquery_interface.do?b_query=link";
	     queryInterfaceForm.submit();
	  </logic:equal>
	  <logic:equal name="queryInterfaceForm" property="home" value="0"> 	  
	 	 queryInterfaceForm.action="/workbench/query/gquery_interface.do?b_query=link";
     	 queryInterfaceForm.submit();
      </logic:equal>
  }  
  
  function winopo(home)
  {
     queryInterfaceForm.action="/workbench/query/query_result.do?b_view_photo=link";
     if(!home=="4")
       queryInterfaceForm.target="il_body";
     else
       queryInterfaceForm.target="_self";
     queryInterfaceForm.submit()
  }
  function OutFile(infokind,dbpre)
  {
	  var isQuery = "false";
	  var url = window.location.pathname;
	  if(url.indexOf("/workbench/query/query_interface.do") > -1 
			  || url.indexOf("/workbench/query/gquery_interface.do") > -1)
		  isQuery = "true";
	  
     var thecodeurl="/workbench/query/query_interface.do?br_field=field`infokind="+infokind+"`dbpre="+dbpre+"`isQuery="+isQuery+"`callback=closeAction"; 
     //防止恶意注入输入 通过iframe 过滤 wangb 20180201
     var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	 var dw=540,dh=460,dl=(screen.width-dw)/2;
	 var dt=(screen.height-dh)/2;
	 window.open(iframe_url,'_blank',"width=540,height=460,top="+screen.height/3+"px,left="+screen.width/3+"px,toolbar=no,location=no,resizable=no");
  }
  function closeAction(outparameters){//add by xiegh on date20171125 修改自助服务-员工信息：浏览器兼容问题
		var viewPhoto = document.getElementById("viewPhoto");
		if(viewPhoto)
			viewPhoto.disabled = true;
		 		 
		var exportExcel = document.getElementById("exportExcel");
		if(exportExcel)
			exportExcel.disabled = true;
		 		 
		var returnButton = document.getElementById("returnButton");
		if(returnButton)
			returnButton.disabled = true;
		 	 
		var wait = document.getElementById("wait");
		if(wait)
			wait.style.display="block";
		 	 
		setTimeout("getExportFileName()", 1000);
	      
	    var hashVo=new HashMap();
	    hashVo.put("dbpre",outparameters[4]);
	    hashVo.put("ids",outparameters[0]);
	    hashVo.put("infokind",outparameters[3]);
	    hashVo.put("strwhere",outparameters[2]);
	    hashVo.put("querytype","1");
	    hashVo.put("isQuery",outparameters[5]);
	    hashVo.put("selectDate",outparameters[6]);
	    hashVo.put("times",outparameters[7]);
	    hashVo.put("selectField",outparameters[8]);
	    hashVo.put("where",outparameters[9]);
	    Rpc({method:'post',asynchronous:true,functionId:'0202001021',success:function () {}},hashVo);
	}

  function getExportFileName(){
	  var hashVo=new HashMap();
	  hashVo.put("exportFlag","1");
	  Rpc({method:'post',asynchronous:true,functionId:'0202001021',success:exportFile},hashVo);
  }

  function exportFile(outparameters){
	  var map = Ext.decode(outparameters.responseText);
	  var msg = map.msg;
	  if("ok" != msg) {
		  var wait = document.getElementById("wait");
		  if(wait)
			  wait.style.display="none";
			
	  	  alert(msg);
	  	  var viewPhoto = document.getElementById("viewPhoto");
	   	  if(viewPhoto)
	   		  viewPhoto.disabled = false;
	   		 
	   	  var exportExcel = document.getElementById("exportExcel");
	   	  if(exportExcel)
	   		  exportExcel.disabled = false;
	   		 
	   	  var returnButton = document.getElementById("returnButton");
	   	  if(returnButton)
	   		  returnButton.disabled = false;
	   	
		  return false;
	  }
	  	
	  var outName = map.exportEmployeFileName;
	  
	  if(outName) {
		  var wait = document.getElementById("wait");
		  if(wait)
			 wait.style.display="none";
			 
		  document.getElementById("exportRows").innerHTML="…";
		  var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
		  var viewPhoto = document.getElementById("viewPhoto");
		  if(viewPhoto)
		      viewPhoto.disabled = false;
		   		 
		  var exportExcel = document.getElementById("exportExcel");
		  if(exportExcel)
		  	  exportExcel.disabled = false;
		   		 
		  var returnButton = document.getElementById("returnButton");
		  if(returnButton)
		  	  returnButton.disabled = false;
		  
	  } else {
		  var exportRows = map.exportRows;
	   	  var totalRows = map.totalRows;
	   	  if(totalRows)
	   		  document.getElementById("exportRows").innerHTML="，已导出" + exportRows + "/" + totalRows + "条数据…";
	   		
	   	  setTimeout("getExportFileName()",3000);
	  }
  }
	
function returnH(url)
{
    queryInterfaceForm.action=url;    
    queryInterfaceForm.submit();
}
function blackMaint(checkflag){
   	if(checkflag=='hcm'){
   		queryInterfaceForm.action="/templates/index/hcm_portal.do?b_query=link";
    	queryInterfaceForm.submit();
   	}else if(checkflag=='hl'){
   		queryInterfaceForm.action="/templates/index/portal.do?b_query=link";
    	queryInterfaceForm.submit();
   	}
}
function multimediahref(dbname,a0100){
	var thecodeurl =""; 
	var return_vo=null;
	var setname = "A01";
	var dw=800,dh=500,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setname+"&a0100="+a0100+"&nbase="+dbname+"&dbflag=A&canedit=false";
  	//19/3/13 xus 员工管理 查询页面 查看附件 浏览器兼容
  	if(Ext.isIE)
  		return_vo= window.showModalDialog(thecodeurl, "", 
  			  	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
    else
	      Ext.create('Ext.window.Window',{
	    	  id:'multimediahref',
	    	  title:'查看附件',
	    	  height:dh,
	    	  width:dw,
	    	  resizeable:'no',
	    	  modal:true,
	    	  autoScroll:false,
	    	  autoShow:true,
	    	  autoDestroy:true,
	    	  html:'<iframe style="background-color:#ffffff;" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>'
	      }).show();
  	
}

function returnFun () {
	if('${param.b_query}' == 'link')
		window.location.replace('/workbench/query/query_interface.do?b_gquery=link&a_inforkind=1&ver=5&home=0');
	else
		window.location.replace('/workbench/query/query_result.do?br_return=link');
}
<logic:equal name="queryInterfaceForm" property="type" value="1">
function setDivStyle(){
    document.getElementById("fixedDiv").style.height = document.body.clientHeight-130;
    document.getElementById("fixedDiv").style.width = document.body.clientWidth-15; 
    document.getElementById("pageDiv").style.width = document.body.clientWidth-15; 
}
window.onresize = function(){
    setDivStyle();
}
</logic:equal>
</script>
<hrms:themes />
<!--zgd 2014-7-9 信息列表中岗位中有兼职情况的特殊处理。partdescdiv在ParttimeTag中写入-->
<style>
<%if("hcm".equals(bosflag)){%>
.partdescdiv{           
	margin-top:-14px;
}
<%}%>
</style>
<html:form action="/workbench/query/query_result">
<%if("hcm".equals(bosflag)){ %>
<div class="fixedtab">
<%}else{ %>
<div class="fixedtab" style="margin-top: 10px">
<%} %>
<table border="0" cellspacing="0"  cellpadding="5">
<hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" sql="queryInterfaceForm.dbcond" collection="list" scope="page"/>
<bean:size id="length" name="list" scope="page"/>
    <tr <logic:lessThan value="2" name="length">style="display: none"</logic:lessThan>>
        <logic:notEmpty name="queryInterfaceForm" property="lexprName">
	        <td align="right" nowrap class="tdFontcolor">
	           <font style="font-weight: bold;">【${queryInterfaceForm.lexprName }】<bean:message key="infor.menu.query.data"/></font>
	        </td>
        </logic:notEmpty>
        <td align="right" nowrap class="tdFontcolor"><bean:message key="label.query.dbpre"/></td>
        <td align="left" nowrap class="tdFontcolor">
            <html:select name="queryInterfaceForm" onchange="submitChange()" property="dbpre" size="1" >
                <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                <html:option value="All">全部人员库</html:option>                                  
            </html:select>
        </td>
    </tr>
</table>
<logic:equal name="queryInterfaceForm" property="type" value="1">
<input type="hidden" name="a0100" id="a0100">
<div class="myfixedDiv" id='fixedDiv' style="padding: 0;">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
           <tr style="position:relative;" class="fixedHeaderTr">
            <logic:iterate id="element" name="queryInterfaceForm"  property="resultlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">            
               <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	       	   </td> 
 	          </logic:notEqual>                       
            </logic:iterate> 
         	<logic:notEqual name="queryInterfaceForm" property="tabid" value="-1">
            <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
		     	<bean:message key="tab.base.info"/>          	
		    </td>	 
		    </logic:notEqual>               
            <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
		     	<bean:message key="tab.synthesis.info"/>     	
		    </td>
		    <logic:equal name="queryInterfaceForm" property="multimedia_file_flag" value="1">
	 		      <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					<bean:message key="conlumn.resource_list.name"/>             	
				  </td> 
			  </logic:equal>	    		        	        	        
           </tr>
   	  </thead>   	  
   	   <hrms:paginationdb id="element" name="queryInterfaceForm" sql_str="queryInterfaceForm.strsql" table="" where_str="queryInterfaceForm.strwhere" columns="queryInterfaceForm.columns" order_by="queryInterfaceForm.order" page_id="pagination" pagerows="20" distinct="${queryInterfaceForm.distinct}" keys="${queryInterfaceForm.keys}">
             	  
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
          
          <%
          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
   	   	  String a0100_encrypt=(String)abean.get("a0100");              	            	   
          request.setAttribute("a0100_encrypt",PubFunc.encrypt(a0100_encrypt)); 
          
          %>
          
	        <bean:define id="a0100" name="element" property="a0100"/><!-- 获取兼职内容在ParttimeTag类内 -->          
	        <bean:define id="nbase" name="element" property="nbase"/>
            <hrms:parttime a0100="${a0100}" nbase="${nbase}" part_map="${queryInterfaceForm.part_map}" name="element" scope="page" code="" kind="" uplevel="${queryInterfaceForm.uplevel}"  b0110_desc="b0110_desc" e0122_desc="e0122_desc" part_desc="part_desc" descOfPart="descOfPart"/>
            <logic:iterate id="fielditem"  name="queryInterfaceForm"  property="resultlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0"> 
                 <logic:equal value="N" name="fielditem" property="itemtype"><td align="right" class="RecordRow" style="border-left: none;border-top: none;" nowrap></logic:equal>   
                 <logic:notEqual value="N" name="fielditem" property="itemtype"><td align="left" class="RecordRow" style="border-left: none;border-top: none;" nowrap></logic:notEqual>          
                 <logic:notEqual  name="fielditem" property="codesetid" value="0">  
	                   <logic:notEqual  name="fielditem"   property="itemid" value="e0122">  
	                    <logic:notEqual  name="fielditem"   property="itemid" value="b0110">  
	                     <logic:notEqual  name="fielditem"   property="itemid" value="e01a1"> 
	                      <logic:equal name="fielditem" property="codesetid" value="UM">
	                       <hrms:codetoname codeid="UM" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="${queryInterfaceForm.uplevel}"/>  	      
	          	          		<!-- 
	          	            	//tianye update start
								//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
	          	            	-->
	          	          		<logic:notEqual  name="codeitem" property="codename" value="">
	          	           			  <bean:write name="codeitem" property="codename" filter="true" /> 
	          	           		</logic:notEqual>
	          	          		<logic:equal  name="codeitem" property="codename" value="">
	          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="${queryInterfaceForm.uplevel}"/>  
	          	           			  <bean:write name="codeitem" property="codename" /> 
	          	           		</logic:equal>
	          	           		<!-- end -->
	                      </logic:equal>
	                      <logic:notEqual name="fielditem" property="codesetid" value="UM">                      
	                        <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
	          	    	     <bean:write name="codeitem" property="codename" />  
	                        
	                      </logic:notEqual>                
	                     </logic:notEqual>
	                    </logic:notEqual>
	                   </logic:notEqual>
	                   <logic:equal name="fielditem"   property="itemid" value="b0110">
	                      ${b0110_desc}     
	                   </logic:equal>
	                   <logic:equal name="fielditem"   property="itemid" value="e0122">
	                         ${e0122_desc}   
	                   </logic:equal>
	                   <logic:equal name="fielditem"   property="itemid" value="e01a1">
	                        <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
	          	             <bean:write name="codeitem" property="codename" />  
	          	             <logic:empty name="codeitem" property="codename">${descOfPart}</logic:empty>  
          	                 <logic:notEmpty name="codeitem" property="codename">${part_desc}</logic:notEmpty>
	                   </logic:equal>
	          	     </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <logic:equal name="fielditem" property="itemid" value="a0101">
                     <logic:equal name="queryInterfaceForm" property="dbpre" value="All">   
                	<logic:equal name="queryInterfaceForm" property="home" value="4">            	
            	    <%if(url!=null&&url.equals("hl4")){ %>
            	      <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=46&isUserEmploy=1')"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>
            	     <%}else{ %>
		     	      <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=6&isUserEmploy=1','i_body');"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>            	          	
 	                <%} %> 	             
 	              </logic:equal>	
            	  <logic:equal name="queryInterfaceForm" property="home" value="1">    
            	  <%if(url!=null&&url.equals("ul")){ %> 
				   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=333&isUserEmploy=1','i_body');"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>            	
            	  <%}else{ %>
            	    <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=3&isUserEmploy=1');"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>
            	  <%} %> 	
            	  </logic:equal>     
            	<logic:notEqual name="queryInterfaceForm" property="home" value="4">
            	  <logic:notEqual name="queryInterfaceForm" property="home" value="1">    
				   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=3&isUserEmploy=1');"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>            	
            	  </logic:notEqual>     
            	</logic:notEqual>
               </logic:equal>	
              <logic:notEqual name="queryInterfaceForm" property="dbpre" value="All">   
            	<logic:equal name="queryInterfaceForm" property="home" value="4">            	
            	 <%if(url!=null&&url.equals("hl4")){ %>
            	   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=46&isUserEmploy=1')"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>
            	 <%}else{ %>
		     	   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=6&isUserEmploy=1','i_body');"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>            	          	
 	             <%} %> 	             
 	            </logic:equal>	
 	            <logic:equal name="queryInterfaceForm" property="home" value="1">     
				  <%if(url!=null&&url.equals("ul")){ %> 
				   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=333&isUserEmploy=1','i_body');"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>            	
            	  <%}else{ %>
            	    <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=3&isUserEmploy=1');"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>
            	  <%} %>             	
            	</logic:equal>     
            	<logic:notEqual name="queryInterfaceForm" property="home" value="4">
            	  <logic:notEqual name="queryInterfaceForm" property="home" value="1">     
				   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=3&isUserEmploy=1');"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>            	
            	  </logic:notEqual>     
            	</logic:notEqual>
              </logic:notEqual>	 
                     
                   </logic:equal>
                   <logic:notEqual name="fielditem" property="itemid" value="a0101">
                      <bean:write name="element" property="${fielditem.itemid}" filter="true"/>
                   </logic:notEqual>                 
                 </logic:equal>                                
	      &nbsp;</td> 
              </logic:notEqual>	                            
            </logic:iterate>                
         	<logic:notEqual name="queryInterfaceForm" property="tabid" value="-1">
              <td align="center" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
               <logic:notEqual name="queryInterfaceForm" property="dbpre" value="All">  
               		<a href="###" onclick="winopen('<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>','${a0100_encrypt }')"><img src="../../images/table.gif" border="0"></a>
  		        </logic:notEqual>
  		        <logic:equal name="queryInterfaceForm" property="dbpre" value="All">   
  		           <a href="###" onclick="winopen('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>','${a0100_encrypt }')"><img src="../../images/table.gif" border="0"></a>
  		        </logic:equal>
  		      </td>	                
            </logic:notEqual>	                		
            <td align="center" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
               <logic:equal name="queryInterfaceForm" property="dbpre" value="All">   
                	<logic:equal name="queryInterfaceForm" property="home" value="4">            	
            	    <%if(url!=null&&url.equals("hl4")){ %>
            	      <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=46')"><img src="../../images/view.gif" border="0"></a>
            	     <%}else{ %>
		     	      <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=6','i_body');"><img src="../../images/view.gif" border="0"></a>            	          	
 	                <%} %> 	             
 	              </logic:equal>	
            	  <logic:equal name="queryInterfaceForm" property="home" value="1">    
            	  <%if(url!=null&&url.equals("ul")){ %> 
				   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=333','i_body');"><img src="../../images/view.gif" border="0"></a>            	
            	  <%}else{ %>
            	    <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=3');"><img src="../../images/view.gif" border="0"></a>
            	  <%} %> 	
            	  </logic:equal>     
            	<logic:notEqual name="queryInterfaceForm" property="home" value="4">
            	  <logic:notEqual name="queryInterfaceForm" property="home" value="1">    
				   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=3');"><img src="../../images/view.gif" border="0"></a>            	
            	  </logic:notEqual>     
            	</logic:notEqual>
               </logic:equal>	
              <logic:notEqual name="queryInterfaceForm" property="dbpre" value="All">   
            	<logic:equal name="queryInterfaceForm" property="home" value="4">            	
            	 <%if(url!=null&&url.equals("hl4")){ %>
            	   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=46')"><img src="../../images/view.gif" border="0"></a>
            	 <%}else{ %>
		     	   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=6','i_body');"><img src="../../images/view.gif" border="0"></a>            	          	
 	             <%} %> 	             
 	            </logic:equal>	
 	            <logic:equal name="queryInterfaceForm" property="home" value="1">     
				  <%if(url!=null&&url.equals("ul")){ %> 
				   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=333','i_body');"><img src="../../images/view.gif" border="0"></a>            	
            	  <%}else{ %>
            	    <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=3');"><img src="../../images/view.gif" border="0"></a>
            	  <%} %>             	
            	</logic:equal>     
            	<logic:notEqual name="queryInterfaceForm" property="home" value="4">
            	  <logic:notEqual name="queryInterfaceForm" property="home" value="1">     
				   <a href="###" onclick="winhref('/workbench/browse/showselfinfo.do?b_search=link&home=<bean:write name="queryInterfaceForm" property="home" filter="true"/>&userbase=<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=3');"><img src="../../images/view.gif" border="0"></a>            	
            	  </logic:notEqual>     
            	</logic:notEqual>
              </logic:notEqual>	 	            	     	   
		    </td>
		    <logic:equal name="queryInterfaceForm" property="multimedia_file_flag" value="1">
             	<td align="center" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
            		<a href="###"  onclick='multimediahref("<bean:write name="element" property="nbase" filter="true"/>","<bean:write name="element" property="a0100" filter="true"/>");'><img src="/images/muli_view.gif" border=0></a>
	      		</td>
	      	</logic:equal>	   	    		        	        	        
          </tr>
        </hrms:paginationdb>
   </table>
   </div>
</logic:equal>
<logic:equal name="queryInterfaceForm" property="type" value="2">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
   	  <thead>
           <tr style="">
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="queryInterfaceForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
            </td>           
            <logic:iterate id="element"    name="queryInterfaceForm"  property="resultlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">            
               <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	       </td> 
	      </logic:notEqual>                       
            </logic:iterate> 	    
	    		        	        	        
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="queryInterfaceForm" sql_str="queryInterfaceForm.strsql" table="" where_str="queryInterfaceForm.strwhere" columns="queryInterfaceForm.columns" order_by=" order by b01.b0110" page_id="pagination" pagerows="21" distinct="${queryInterfaceForm.distinct}">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
            <td align="left" class="RecordRow" nowrap>
            
               <a href="/workbench/query/browser.do?b_query=link&keyid=<bean:write name="element" property="b0110" filter="true"/>">        
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />
          	<hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />  
	      </a>            	
	    </td>            

            <logic:iterate id="fielditem"  name="queryInterfaceForm"  property="resultlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0">  
                <td align="left" class="RecordRow" nowrap>
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
          	       <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	       <bean:write name="codeitem" property="codename" />                    
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="true"/>                 
                 </logic:equal>                      
	            </td> 
              </logic:notEqual>	                            
            </logic:iterate>                
   	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
   </table>
</logic:equal>
<logic:equal name="queryInterfaceForm" property="type" value="3">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
   	  <thead>
           <tr style="">
            <td align="center" class="TableRow" nowrap>
            
             <hrms:fieldtoname name="queryInterfaceForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
	    </td>         
            <logic:iterate id="element"    name="queryInterfaceForm"  property="resultlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">            
               <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	       </td> 
	      </logic:notEqual>                       
            </logic:iterate> 	    
	    		        	        	        
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="queryInterfaceForm" sql_str="queryInterfaceForm.strsql" table="" where_str="queryInterfaceForm.strwhere" columns="queryInterfaceForm.columns" order_by=" order by k01.e01a1" page_id="pagination" pagerows="21" distinct="${queryInterfaceForm.distinct}">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
            <td align="left" class="RecordRow" nowrap>
               <a href="/workbench/query/browser.do?b_query=link&keyid=<bean:write name="element" property="e01a1" filter="true"/>">          
          	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />
               </a>
	    </td>            

            <logic:iterate id="fielditem"  name="queryInterfaceForm"  property="resultlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0">              
              <td align="left" class="RecordRow" nowrap>
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
          	   <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   <bean:write name="codeitem" property="codename" />                    
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="true"/>                 
                 </logic:equal>                                
	      </td> 
              </logic:notEqual>	                            
            </logic:iterate>                
   	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
   </table>
</logic:equal>
</div>
<logic:equal name="queryInterfaceForm" property="type" value="1">
<div id='pageDiv' style="padding: 0;">
</logic:equal>
<table  width="100%" align="center" class="RecordRowP" style="">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		           <p align="right"><hrms:paginationdblink name="queryInterfaceForm" property="pagination" nameId="queryInterfaceForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
<logic:equal name="queryInterfaceForm" property="type" value="1">
</div>
</logic:equal>
<html:hidden property="home"/>  
<table  width="70%" align="center">
		  <tr>
		  	<td height="0px"></td>
		  </tr>
          <tr>
            <td align="center">
               <logic:equal name="queryInterfaceForm" property="type" value="1"> 
	 	        <html:button styleClass="mybutton" styleId="viewPhoto" property="b_view_photo" onclick="winopo('${queryInterfaceForm.home}');">
					<bean:message key="button.query.viewphoto"/>
				</html:button>
               </logic:equal>	 
               <hrms:priv func_id="2601007,0303013"> 
               <html:button styleClass="mybutton" styleId="exportExcel" property="excel" onclick="OutFile('${queryInterfaceForm.type}','${queryInterfaceForm.dbpre}');">
					<bean:message key="report.actuarial_report.exportExcel"/>
				</html:button>   
				</hrms:priv>        
            <%request.setAttribute("url",url);
            if(url!=null&&(url.equals("hl")||("hcm".equalsIgnoreCase(url)))){ %>
              <logic:equal name="queryInterfaceForm" property="home" value="0">                   	               
	 	         <input type='button' class="mybutton" id="returnButton" value='返回' onclick="returnFun()" />
              </logic:equal>
              
              <logic:equal name="queryInterfaceForm" property="home" value="10">   
                 <html:button styleClass="mybutton" property="br_home" styleId="returnButton" onclick="window.location.replace('/workbench/query/query_interface.do?b_query=link&a_inforkind=1&home=10');">
            		    <bean:message key="button.return"/>
	 	              </html:button>
              </logic:equal>
              <logic:notEqual name="queryInterfaceForm" property="returnvalue" value="dxt">
              <logic:equal name="queryInterfaceForm" property="home" value="1">
         	       <html:button styleClass="mybutton" styleId="returnButton" property="br_home" onclick="blackMaint('${url}');">
            		<bean:message key="button.return"/>
	 	           </html:button>
              </logic:equal>
              </logic:notEqual>
              <logic:equal name="queryInterfaceForm" property="home" value="3">             	 	       
         	       <html:button styleClass="mybutton" styleId="returnButton" property="br_home" onclick="window.location.replace('/workbench/query/query_interface.do?b_gquery=link&ver=5&home=3');">
            		<bean:message key="button.return"/>
	 	           </html:button>
              </logic:equal>
              <logic:equal name="queryInterfaceForm" property="home" value="5"> 
	 	           	<html:button styleClass="mybutton" styleId="returnButton" property="addbutton" onclick="blackMaint('${url}');">
            		<bean:message key="button.return"/>
	 	           </html:button>
              </logic:equal>
              <logic:equal name="queryInterfaceForm" property="home" value="6"> 
         	       <html:button styleClass="mybutton" styleId="returnButton" property="addbutton" onclick="returnH('/workbench/query/query_interface.do?b_gquery=link&ver=5&home=6');">
            		<bean:message key="button.return"/>
	 	           </html:button>
              </logic:equal>
              <logic:equal name="queryInterfaceForm" property="returnvalue" value="dxt">	
              <logic:equal name="queryInterfaceForm" property="home" value="dxt">             	 	       
         	      <!--  <hrms:tipwizardbutton flag="emp" target="il_body" formname="queryInterfaceForm"/>  -->
         	       <html:button styleClass="mybutton" styleId="returnButton" property="br_home" onclick="window.location.replace('/workbench/query/query_interface.do?b_gquery=link');">
            		<bean:message key="button.return"/>
	 	           </html:button>
              </logic:equal>
              <logic:equal name="queryInterfaceForm" property="home" value="1">             	 	       
         	      <!--  <hrms:tipwizardbutton flag="emp" target="il_body" formname="queryInterfaceForm"/>  -->
         	       <html:button styleClass="mybutton" styleId="returnButton" property="br_home" onclick="window.location.replace('/workbench/query/query_interface.do?b_query=link');">
            		<bean:message key="button.return"/>
	 	           </html:button>
              </logic:equal>
              </logic:equal>	
		        
		    <%}else{%>
		        <logic:equal name="queryInterfaceForm" property="home" value="0">                   	               
	 	         <input type='button' class="mybutton" id="returnButton" value='返回' onclick="window.location.replace('/workbench/query/query_result.do?br_return=link');" />
                </logic:equal>
                <logic:equal name="queryInterfaceForm" property="home" value="1">                   	               
         	      <html:button styleClass="mybutton" styleId="returnButton" property="br_home" onclick="window.location.replace('/workbench/query/query_interface.do?home=1&b_query=link');">
            		<bean:message key="button.return"/>
	 	           </html:button>
                </logic:equal>            
                <logic:equal name="queryInterfaceForm" property="home" value="3">                   	               
         	      <hrms:submit styleClass="mybutton" styleId="returnButton" property="br_greturn">
            		<bean:message key="button.return"/>
	 	          </hrms:submit>
                </logic:equal>            
                <logic:equal name="queryInterfaceForm" property="home" value="4">             	 	       
         	       <html:button styleClass="mybutton" styleId="returnButton" property="br_home" onclick="window.location.replace('/system/home.do?b_query=link');">
            		<bean:message key="button.return"/>
	 	           </html:button>
                </logic:equal>	 
                <%if(!"bi".equals(url)){ %><!-- 暂时用于解决总裁桌面配置了常用查询填出结果页面不出现返回按钮 -->
                <logic:equal name="queryInterfaceForm" property="home" value="5">             	 	       
         	       <html:button styleClass="mybutton" styleId="returnButton" property="br_home" onclick="window.location.replace('/templates/index/portal.do?b_query=link');">
            		<bean:message key="button.return"/>
	 	           </html:button>
                </logic:equal>	
                <%} %>
                <logic:equal name="queryInterfaceForm" property="returnvalue" value="dxt">             	 	       
         	       <hrms:tipwizardbutton flag="emp" target="il_body" formname="queryInterfaceForm"/> 
                </logic:equal>	
                <logic:equal name="queryInterfaceForm" property="home" value="10">   
                   <logic:equal name="queryInterfaceForm" property="type" value="1">               	               
         	          <html:button styleClass="mybutton" styleId="returnButton" property="br_home" onclick="window.location.replace('/workbench/query/query_interface.do?b_query=link&a_inforkind=1&home=10');">
            		    <bean:message key="button.return"/>
	 	              </html:button>
	 	           </logic:equal>
	 	           <logic:notEqual name="queryInterfaceForm" property="type" value="1">  
	 	                <html:button styleClass="mybutton" styleId="returnButton" property="br_home" onclick="window.location.replace('/workbench/query/query_interface.do?b_query=link&a_inforkind=${queryInterfaceForm.type}&home=10');">
            		       <bean:message key="button.return"/>
	 	                </html:button>
	 	           </logic:notEqual>
                </logic:equal>  
		    <%} %>	      	 	    
            </td>
          </tr>          
</table>
</html:form>
<div id='wait' style='position:absolute;top:45%;left:35%;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height="24">正在导出数据<span id='exportRows'>…</span></td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
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
<logic:equal name="queryInterfaceForm" property="type" value="1">
<script type="text/javascript">
setDivStyle();
</script>
</logic:equal>