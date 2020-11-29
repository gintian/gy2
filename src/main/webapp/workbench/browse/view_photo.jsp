<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.browse.BrowseForm"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="java.util.HashMap" %> 
<%
	int i=0;
	BrowseForm browseForm=(BrowseForm)session.getAttribute("browseForm");
	  String inputchinfor = browseForm.getInputchinfor();
	  String approveflag = browseForm.getApproveflag();	  
	HashMap partMap=(HashMap)browseForm.getPart_map();
%>
<body>
<script src="/phone-app/jquery/jquery-3.5.1.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/js/bigphoto.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script> 

<script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="/ext/ext-all.js"></script>
<link rel="stylesheet" type="text/css" href="/ext/ext6/resources/ext-theme.css"></link> 
<script type="text/javascript" src="/ext/rpc_command.js"></script>
<script type="text/javascript" src="/js/constant.js"></script>
<script type="text/javascript" src="/js/showModalDialog.js"></script>
<script type="text/javascript">


function photoexport(){
	 var thecodeurl="/workbench/browse/view_photo.do?b_photoname=link";
     var dw=350,dh=200;
     if(!window.showModalDialog)
     	dh=250;
     
 	var config = {
    	 width:dw,
         height:dh,
         id:'setPhotoNameWin',
         title:SET_PHOTOFILE_NAME
     }
        
    	modalDialog.showModalDialogs(thecodeurl,null,config,returnFun);
}
/*非IE浏览器弹窗调用方法   bug 35013  20180227 wangb*/
function returnFun(return_vo){
	if(return_vo){
			document.all.ly.style.display="block";   
			document.all.ly.style.width=document.body.scrollWidth;   
			document.all.ly.style.height=document.body.scrollHeight;
			var waitInfo=document.getElementById("wait");
			waitInfo.style.display='block';
			var map = new HashMap();
			var formulastr=return_vo.photoname;
			while(formulastr.indexOf("+")!=-1)
				formulastr=formulastr.replace("+","``#");
	    	map.put("userbase", return_vo.userbase);
	    	map.put("where_n", return_vo.where_n);
	    	map.put("formula", formulastr);
	   		Rpc({functionId:'0201001007',success:searchok,timeout:'30000000'},map); 
	  		function searchok(response){
				var value=response.responseText;
				var map=Ext.decode(value);
				if(map.succeed.toString()=='false'){
					waitInfo.style.display="none";
					alert(map.message);
				}else{
			    	var hasData=map.hasData;
					var outName=map.outName;
					var name=outName;//+".zip";
					if (hasData=='true'){//tiany add 添加hasData标记 记录是否有照片
						var notphotoname=map.notphotoname;
						var notext=map.notext;
						var pNumber=map.pNumber;
						if(notphotoname!=0||notext!=0){
							/*if(!confirm("有"+notphotoname+"人由于用户名指标为空未能导出照片，有"+notext+"人没有照片，有"+(pNumber-notphotoname-notext)+"人已导出照片。")){
								return;
							}*/
							alert(notphotoname+WORKBENCH_BROWSE_PHOTODOWNLOAD_REASON+notext+WORKBENCH_BROWSE_PHOTODOWNLOAD_SUCCEED+(pNumber-notphotoname-notext)+WORKBENCH_BROWSE_PHOTODOWNLOAD_FAIL);
						}
						window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"zip");
	                    waitInfo.style.display="none";
	                    document.all.ly.style.display="none";
					}else{
						waitInfo.style.display="none";
	                	document.all.ly.style.display="none";
				    	alert(NOT_PHOTO_EXPORT);
					}
				
				
				}
			}
   		}
}

</script>
<script language="javascript">
function winhref(a0100,target)
{
   if(a0100=="")
      return false;
    <%if(inputchinfor.equals("1")&&approveflag.equals("1")) {%>
    	browseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100="+a0100+"&flag=notself&returnvalue=1&fromphoto=1";
    <%} else {%>
    browseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100="+a0100+"&flag=notself&returnvalue=111";
    <%}%>
    browseForm.target=target;
    browseForm.submit();
   
      
}
  //document.oncontextmenu = function() {return false;}
function returnShow()
{
   browseForm.action="/workbench/browse/showinfodata.do?br_query=link&query=${browseForm.query}&isphotoview=";   
   browseForm.submit();
}
//$("#photo_tab").on("onload", checkScreenWidth());
$(checkScreenWidth);
/* 
 * 根据屏幕宽度动态修改照片墙的样式  
 */
function checkScreenWidth(){
	// 59142 非ie浏览器,margin-left与table的center冲突，暂不设置
	if(getBrowseVersion()<=0)
		return;

	if(window.screen.width < 1300) {
		$("#photo_tab").css({'margin-left':'-4px'});
	} else {
		$("#photo_tab").css({'margin-left':'15px'});
	}
}

</script>

<hrms:themes />
<html:form action="/workbench/browse/view_photo" style="width:100%;">
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div>
<div id='wait' style='position:absolute;top:350;left:200;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在导出照片，请稍候...
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
<!-- 新版照片墙 xiaoyun 2014-6-11 start -->
<table id="photo_tab" width="80%" border="0" cellspacing="0" align="center" cellpadding="0" style="margin-top: -2px;" >	
          <hrms:paginationdb indexes="index" id="element" name="browseForm" sql_str="browseForm.strsql" table="" where_str="browseForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName," order_by="browseForm.order_by" pagerows="${browseForm.pagerows}" page_id="pagination" keys="">
           <%
          if(i%4==0)
          {
          %>
          <tr valign="middle" align="center">
          <%
          }
          %>             
          <%
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String a0100=(String)abean.get("a0100"); 
                request.setAttribute("name",a0100); 
          %>
         <!-- 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-17 start -->
         <hrms:parttime a0100="${name}" nbase="${browseForm.userbase}" part_map="<%=partMap%>" name="element" scope="page" code="${browseForm.code}" kind="${browseForm.kind}" uplevel="${browseForm.uplevel}"  b0110_desc="b0110_desc" e0122_desc="e0122_desc" part_desc="part_desc" partInfo="partInfo" deptCode="deptCode" unitCode="unitCode"/>
          <td align="center" nowrap>
          	<logic:equal name="browseForm" property="photolength" value="">
          		<ul class="photos">
          		<li>
          			<hrms:ole name="element"  photoWall="true" dbpre="browseForm.userbase" href="###" target="nil_body" a0100="a0100" div="" scope="page" height="120" width="85" onclick="winhref('${name}','nil_body')" />
          			<div class="detail">
          				<p><a href="###" onclick="winhref('${name}','nil_body')"><strong><bean:write name="element" property="a0101" filter="true"/></strong></a></p>
          				<p class="linehg">
          				<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>
          				<!-- 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-18 start -->
          				<hrms:photoviewInfo name="element" a0100="" nbase="" itemid="" isNotSetQuota="true" jobCode="${codeitem.codeitem}" partInfo="${partInfo}" deptCode="${deptCode}" unitCode="${unitCode}" isInfoView="true"  scope="page"/>
          				<!-- 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-18 end -->
                		</p>
          			</div>
          		</li>
          		</ul>
          	</logic:equal>
          	
          	<logic:notEqual name="browseForm" property="photolength" value="">
          		<bean:define id="isHref" value="true"></bean:define>
          		<ul class="photos">
          			<li>
          			<hrms:ole name="element" photoWall="true" dbpre="browseForm.userbase" href="###" target="nil_body" a0100="a0100" div="" scope="page" height="120" width="85" onclick="winhref('${name}','nil_body')" />
          			<div class="detail"> 	
          				<p>
                			<hrms:photoviewInfo name="element" a0100="a0100" params="${name},nil_body"  nbase="${browseForm.userbase}" itemid="browseForm.photo_other_view" scope="page"/>
                		</p>
	          		</div>
		          </li>
	          	</ul>
	          	
          	</logic:notEqual>
          </td> 
          <%
          if((i+1)%4==0)
          {%>
          </tr>
          <%
          }
          i++;          
          %>         
        </hrms:paginationdb>
</table>
<!-- 新版照片墙 xiaoyun 2014-6-11 end -->
<table  width="72%" align="center">
		<tr>
		    <td align="left" valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="browseForm" property="pagination" nameId="browseForm" scope="page">
				</hrms:paginationdblink>
		       </td>
		</tr>
</table>
<table  width="72%" align="center">
          <tr>
            <td align="center">       
               <html:button styleClass="mybutton" property="br_return" onclick="returnShow();">
					<bean:message key="workbench.browse.displayinfo"/>
			   </html:button>
			   <hrms:priv func_id="260113">
			   <html:button styleClass="mybutton" property="br_return" onclick="photoexport();">
					<bean:message key="workbench.browse.photoexport"/>
			   </html:button> 
			   </hrms:priv>     			      	   
            </td>
          </tr>          
</table>
</html:form>
</body>