<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %> 
<head>
<style>/*add by xiegh ondate 20180308 bug35299   */
	input:disabled{
	    border: 1px solid #DDD;
	    background-color: #F5F5F5;
	    color:#ACA899;
	}
</style>
<script type="text/javascript" src="/ext/ext6/ext-all.js"></script>
<script type="text/javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/js/constant.js"></script>
<script language="javascript">
function photoexport(){
	var photoname=$F('definephotoname');
	if($('22').checked)
		photoname=$F('photoname');
	if(photoname.length==0){
	    //判断是否是ie浏览器 
	    if (window.ActiveXObject || "ActiveXObject" in window)
            alert(PHOTO_NAME_CANNOT_EMPTY);
         else
         	Ext.showAlert(PHOTO_NAME_CANNOT_EMPTY);
		return;
	}
	
	var obj=new Object();
	obj.userbase="${browseForm.userbase}";
	obj.where_n="${browseForm.ensql}";
	obj.photoname=photoname;
	if(getBrowseVersion()){
		parent.window.returnValue=obj;
	}else{
		if(parent.returnFun) {
			parent.returnFun(obj);
			parent.Ext.getCmp("setPhotoNameWin").close();
		} else
			parent.opener.openReturn(obj);
	}
	parent.window.close();
}

function closeWin(){
	if(parent.returnFun) {
		parent.Ext.getCmp("setPhotoNameWin").close();
	} else
		parent.window.close();
}
function ablethis(name1,name2){
	document.getElementsByName(name1)[0].disabled=false;
	document.getElementsByName(name2)[0].disabled=true;
}

function getformula(){
	 var thecodeurl="/workbench/browse/view_photo.do?b_getformula=link`formula="+getEncodeStr($F('photoname'));
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl); 
     var dw=500,dh=230,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    if(getBrowseVersion()){
    	if(getBrowseVersion()==10){
    		dw=520;dh=320;
    	}
    	var  return_vo= window.showModalDialog(iframe_url, "", 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");     
		if(return_vo){
			$('photoname').value=return_vo;
		}
    }else{
    	//非IE浏览器使用open弹窗    bug 35013  20180227 wangb
    	dw=520;
    	dh=250;
    	var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
    }
}
/*非IE浏览器弹窗调用方法   bug 35013  20180227 wangb*/
function openReturn(return_vo){
	if(return_vo){
		$('photoname').value=return_vo;
	}
}
function setTitle(obj){
	var index = obj.selectedIndex; // 选中索引

	var text = obj.options[index].text; // 选中文本
	obj.title = text;
}
</script>

<hrms:themes />
<title><bean:message key="workbench.browse.photoexport"/></title>
</head>
<div id='wait' style='position:absolute;top:70;left:20;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在导出，请稍候...
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
<html:form action="/workbench/browse/view_photo">
<table width="100%" border="0" style="margin-top: 2px;" cellspacing="0" align="center" cellpadding="0">
        <tr>
          <td>
          <fieldset align="center" style="width:96%;">
         <legend >照片文件命名规则</legend>
          <table align="center" style="width:96%;" > 
          	<tr style="height: 15px;"><td colspan="2">&nbsp;</td></tr>
	         <tr>
	            <td align="left" valign="top" nowrap width="30%">
	     	       <input type="radio" id=11 name="aa" checked="checked" onclick="ablethis('definephotoname','formula')"><label for="11" id=44 >文件名指标</label>
		    	</td>  
		    	<td align="left" nowrap >
	     	       <hrms:optioncollection name="browseForm" property="stringfieldlist" collection="list"/>
	               <html:select name="browseForm" property="definephotoname" size="1" style="width:100%;" onchange="setTitle(this)" >
	                   <html:options collection="list" property="dataValue" labelProperty="dataName" />
	               </html:select>
	            </td>                	        	        
	        </tr> 
	        <tr style="height: 15px;"><td colspan="2">&nbsp;</td></tr>
			<tr>
	            <td align="left" nowrap>
	     	       <input type="radio" id=22 name="aa" onclick="ablethis('formula','definephotoname')"><label for="22">公式</label>
	     	    </td>  
		    	<td align="left" nowrap>
		    		<input type="hidden" id=photoname />
	     	       <html:button styleClass="mybutton" property="formula" onclick="getformula();"  disabled="true" >...</html:button> 
		    	</td>                	        	        
	        </tr>
	        <tr style="height: 15px;"><td colspan="2">&nbsp;</td></tr>
        </table>
    </fieldset>  
		  </td>
		</tr>
</table>
<table  width="100%">
          <tr style="height: 35px;">
            <td align="center">       
               <html:button styleClass="mybutton" property="br_return" onclick="photoexport();">
					<bean:message key="sys.export.derived"/>
			   </html:button>
			   
			   <html:button styleClass="mybutton" property="br_return" onclick="closeWin();">
					<bean:message key="button.cancel"/>
			   </html:button>         	   
            </td>
          </tr>          
</table>
</html:form>
<script>
if(!getBrowseVersion() || getBrowseVersion()==10){//非IE浏览器 样式修改    wangb  20180227
	var fieldset = document.getElementsByTagName('fieldset')[0];
	fieldset.style.width ='92%';
}
</script>
