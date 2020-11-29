<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>

<head>
<%
	UserView userView = (UserView)session.getAttribute(WebConstant.userView);
	String themes = SysParamBo.getSysParamValue("THEMES", userView.getUserName());
	if(themes==null||"".equals(themes)){
		themes = "default";
	}
 %>
<style>
body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,img,div,dl,dt,dd,span,table,tr,td
	{
	margin: 0;
	padding: 0;
	border: none;
}

body {
	color: #747474;
	font-size: 12px;
	font-family: "宋体";
	background: #FAFAFA;
}

ul,ol {
	list-style-type: none;
}

select,input,img {
	vertical-align: middle;
}

a {
	text-decoration: none;
	color: #549FE3;
}

.clearit {
	clear: both;
	font-size: 0;
	height: 0;
	width: 0;
	padding: 0;
	margin: 0;
	border: 0;
}

.bh-clear {
	clear: both;
}

.bh-space {
	height: 23px;
	clear: both;
}

.hj-wzm-jtys-top-sctx,.hj-wzm-jtys-top-bctx {
	background: #529FE5;
}

.hj-wzm-jtys-all {
	width: 700px;
	margin: 0px 15px 0px 0px;
}

.hj-wzm-jtys-left {
	width: 320px;
	float: left;
	margin-left: 15px;
}

 /* .hj-wzm-jtys-left .hj-wzm-jtys-top-sctx {
	width: 80px;
	height: 28px;
	border: none;
	line-height: 28px;
	text-align: center;
	color: #FFF;
	float: left;
	margin-right: 20px
}  */

.hj-wzm-jtys-right {
	float: left;
	margin: 48px 0 0 50px;
}

.hj-wzm-jtys-right-top .hj-jtys-r-top-dl64 {
	width: 64px;
	height: auto;
	float: left;
	margin-right: 50px;
}

.hj-wzm-jtys-right-top .hj-jtys-r-top-dl64 dt img {
	width: 64px;
	height: 64px;
}

.hj-wzm-jtys-right-top .hj-jtys-r-top-dl64 dd {
	color: #999;
	text-align: center;
	margin-top: 10px;
}

.hj-wzm-jtys-right-top .hj-jtys-r-top-dl48 {
	width: 48px;
	height: auto;
	float: left;
	margin-right: 50px;
}

.hj-wzm-jtys-right-top .hj-jtys-r-top-dl48 dt img {
	width: 48px;
	height: 48px;
}

.hj-wzm-jtys-right-top .hj-jtys-r-top-dl48 dd {
	color: #999;
	text-align: center;
	margin-top: 10px;
}

.hj-wzm-jtys-right-top .hj-jtys-r-top-dl24 {
	width: 24px;
	height: auto;
	float: left;
}

.hj-wzm-jtys-right-top .hj-jtys-r-top-dl24 dt img {
	width: 24px;
	height: 24px;
}

.hj-wzm-jtys-right-top .hj-jtys-r-top-dl24 dd {
	color: #999;
	text-align: center;
	margin-top: 10px;
}

.hj-wzm-jtys-right-bottom {
	margin-top: 80px;
}

.hj-wzm-jtys-right-bottom p {
	line-height: 22px;
	color: #999;
}

/* .hj-wzm-jtys-right-bottom .hj-wzm-jtys-top-bctx {
	width: 80px;
	height: 28px;
	border: none;
	line-height: 28px;
	text-align: center;
	color: #FFF;
	margin-top: 20px;
} */
</style>
<script src="./jquery.min.js" type="text/javascript"></script>
<script src="./jquery.Jcrop.js" type="text/javascript"></script>
<link rel="stylesheet" href="./jquery.Jcrop.css" type="text/css" />

<script type="text/javascript">
	function photoset() {
		try {
			var fileEx = selfInfoForm.picturefile.value;
			if (fileEx == "") {
				alert("请选择需上传的头像!");
				return;
			}
			//获取图片的全路径  

			var endIndex = fileEx.lastIndexOf("\\");
			var lastIndex = fileEx.length - endIndex - 1;
			if (endIndex != -1)
				imgFile = fileEx.substr(endIndex + 1, lastIndex);
			else
				imgFile = fileEx;

			var tag = true;
			endIndex = fileEx.lastIndexOf(".");
			if (endIndex == -1)
				tag = false;

			var ImgName = fileEx.substr(endIndex + 1, lastIndex);
			ImgName = ImgName.toUpperCase();

			if (ImgName != "JPG" && ImgName != "PNG" && ImgName != "JPEG") {
				tag = false;
			}
			if (!tag) {
				alert("上传图片的文件类型必须为: *.jpeg,*.jpg,*.png,请重新选择!");
				selfInfoForm.picturefile.value = "";
				return false;
			}

			selfInfoForm.action = "/workbench/info/upphotoinfo.do?b_photoset=link";

			selfInfoForm.submit();

		} catch (e) {
			alert("出错了！");
			alert(e.message);
		}
	}

	function photocut() {
		if ('${ selfInfoForm.photoname}' == "") {
			alert("请先上传头像！");
			return;
		}
		selfInfoForm.action = "/workbench/info/upphotoinfo.do?b_photocut=link";

		selfInfoForm.submit();
	}
</script>
<script type="text/javascript">
	$(function() {

		$('#cutimg').Jcrop(
				{
					onChange : updateView,
					onSelect : updateView,
					aspectRatio : 1,
					setSelect : [ '${ selfInfoForm.x}', '${ selfInfoForm.y}',
							'${ selfInfoForm.height}',
							'${ selfInfoForm.width+selfInfoForm.y}' ],
					handleSize : 9
				//初始化选中区域
				});

		function updateView(cc) {
			if (parseInt(cc.w) > 0) {

				$('#widthValue').val(cc.w); //c.w 裁剪区域的宽
				$('#heightValue').val(cc.h); //c.h 裁剪区域的高
				$('#xValue').val(cc.x); //c.x 裁剪区域左上角顶点相对于图片左上角顶点的x坐标
				$('#yValue').val(cc.y); //c.y 裁剪区域顶点的y坐标</span>
			}
		}
		;
	});
	//本地上传
	function picture() {
		document.getElementById("picture").style.display = "block";
		document.getElementById("btn").style.display = "block";
		document.getElementById("camera").style.display = "none";
		document.getElementById("camera").innerHTML = "";
		document.getElementsByName('picturefile')[0].click();
	}
	//拍照上传
	function camera() {
		document.getElementById("picture").style.display = "none";
		document.getElementById("btn").style.display = "none";
		document.getElementById("camera").style.display = "block";				//下载地址 http://xiaoa7.iteye.com/blog/438610
		document.getElementById("camera").innerHTML = '<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="" width="324" height="275" id="photo" align="middle"><param name="allowScriptAccess" value="sameDomain" /><param name="allowFullScreen" value="false" /><param name="FlashVars" value="servicesUrl=/servlet/SaveCameraServlet" /><param name="movie" value="photo.swf" /><param name="quality" value="high" /><param name="bgcolor" value="#ffffff" /><embed src="photo.swf" quality="high" bgcolor="#ffffff" width="324" height="275" name="photo" align="middle" allowScriptAccess="sameDomain" allowFullScreen="false" type="application/x-shockwave-flash" pluginspage="" /></object>';
	}
	//照片成功上传
	function onUploadSuccess(str) {
		if (str == "OK") {
			alert('<bean:message key="workbench.info.camera.ok"/>');
			document.getElementById("camera").innerHTML = "";
			selfInfoForm.action = "/workbench/info/upphotoinfo.do?b_photosearch=link";
			selfInfoForm.submit();
		}
	}
	
	function promptCamera(str){
		alert("请先拍照后再保存！");
	}
	
	function cameraBusy(str){
		if(str=="camerabusy"){
			alert("摄像头被占用！请关闭占用程序！");
		} else if(str=="muted"){
			alert("您未允许使用摄像头！");
		}
		document.getElementById("picture").style.display = "block";
		document.getElementById("btn").style.display = "block";
		document.getElementById("camera").style.display = "none";
		document.getElementById("camera").innerHTML = "";
	}
	
	 function setImg(img){
		var width = 180;
		var height = 240;
		var iw = img.width;
		var ih = img.height;
		var scale_w = iw/width;
		var scale_h = ih/height;
		var scale = scale_w>scale_h?scale_w:scale_h;
		if(scale<=1){
			return false;
		} else {
			img.width = iw/scale;
			img.height = ih/scale;
		}
		img.style.visibility = "visible";//显示图片
		document.getElementById("scaleValue").value = scale;
	} 
</script>
</head>
<hrms:themes />
<body>
	<form name="selfInfoForm" method="post"
		action="/workbench/info/upphotoinfo.do" enctype="multipart/form-data">
        <bean:define id="photoname" name="selfInfoForm" property="photoname"/>
		<bean:define id="lowimage" name="selfInfoForm" property="lowimage"/>
        <bean:define id="phototype" name="selfInfoForm" property="photoType"/>
		<input type="hidden" name="x" id="xValue" /> 
		<input type="hidden" name="y" id="yValue" /> 
		<input type="hidden" name="width" id="widthValue" /> 
		<input type="hidden" name="height" id="heightValue" /> 
		<input type="hidden" name=photoType value='${ selfInfoForm.photoType}' />
		<input type="hidden" name="scale" id="scaleValue"/>
		<div style="width:100%;padding-bottom:10px;">
			<div style="padding-left:10px;">
				<img src="/images/gray_photopre.jpg"></img>
			</div>
		</div>
		<div class="hj-wzm-jtys-all">
			<div class="hj-wzm-jtys-left">
				<div class="hj-wzm-jtys-left-top">
					<input type="button" value="上传头像" class="mybutton"
						style="position:absolute;left:20px;" onclick="picture();" /> <input
						type="button" value="拍照上传" class="mybutton"
						style="position:absolute;left:110px;" onclick="camera();" /> <input
						type="file" name="picturefile" class="text6" accept="image/*"
						style="width:0;cursor:pointer; height:33px;filter:alpha(opacity=0);-moz-opacity:0;-khtml-opacity: 0;opacity: 0;position:absolute;left:15px;font-size:15px ;z-index: 9999"
						onchange="photoset()"/>
				</div>
				<div class="bh-space"></div>
				<div class="bh-space"></div>


				<div id="picture">
					<logic:notEqual name="selfInfoForm" property="photoname" value="">
						<div class="hj-wzm-jtys-left-bottom"><!-- 先隐藏图片，改变大小后再显示  jingq upd 2014.09.12 -->
							<!--修改为使用VFS下载图片,原有的photoname在后台传的fileid-->
							<img id="cutimg" style="visibility: hidden;width:180px;height:180px;" <%-- 显示头像固定设置为180 *180 wangb upd 2020.06.04 --%>
								src='/servlet/vfsservlet?fileid=<%=photoname%>&randomNum=<%= Math.random()*100%>' onload="javascript:setImg(this);"/>

						</div>
					</logic:notEqual>
					<logic:equal name="selfInfoForm" property="photoname" value="">
						<div style="width:190px;height:240px;border:1px solid #868686;">
						</div>
					</logic:equal>
				</div>
				<div style="display:none;" id="camera" class="hj-wzm-jtys-left">

					
				</div>
			</div>
			<div class="hj-wzm-jtys-right">

				<div class="hj-wzm-jtys-right-top">
					<dl class="hj-jtys-r-top-dl64">
						<dt>

							<logic:notEqual name="selfInfoForm" property="lowimage" value="">
								<logic:notEqual name="selfInfoForm" property="scale" value="false">
								<div class="hj-wzm-jtys-left-bottom">
									<img
										src='/servlet/vfsservlet?fileid=<%=lowimage%>&randomNum=<%= Math.random()*100%>'/>
								</div>
								</logic:notEqual>
							</logic:notEqual>
							<logic:equal name="selfInfoForm" property="lowimage" value="">
								<div style="width:64px;height:64px;border:1px solid #868686;"></div>
							</logic:equal>
							<logic:equal name="selfInfoForm" property="scale" value="false">
								<div style="width:64px;height:64px;border:1px solid #868686;"></div>
							</logic:equal>
						</dt>
						<dd>64*64</dd>
					</dl>
					<dl class="hj-jtys-r-top-dl48">
						<dt>
							<logic:notEqual name="selfInfoForm" property="lowimage" value="">
								<logic:notEqual name="selfInfoForm" property="scale" value="false">
								<div class="hj-wzm-jtys-left-bottom">
									<img
										src='/servlet/vfsservlet?fileid=<%=lowimage%>&randomNum=<%= Math.random()*100%>' />
								</div>
								</logic:notEqual>
							</logic:notEqual>
							<logic:equal name="selfInfoForm" property="lowimage" value="">
								<div style="width:48px;height:48px;border:1px solid #868686;"></div>
							</logic:equal>
							<logic:equal name="selfInfoForm" property="scale" value="false">
								<div style="width:48px;height:48px;border:1px solid #868686;"></div>
							</logic:equal>
						</dt>
						<dd>48*48</dd>
					</dl>
					<dl class="hj-jtys-r-top-dl24">
						<dt>
							<logic:notEqual name="selfInfoForm" property="lowimage" value="">
								<logic:notEqual name="selfInfoForm" property="scale" value="false">
								<div class="hj-wzm-jtys-left-bottom">
									<img
										src='/servlet/vfsservlet?fileid=<%=lowimage%>&randomNum=<%= Math.random()*100%>' />
								</div>
								</logic:notEqual>
							</logic:notEqual>
							<logic:equal name="selfInfoForm" property="lowimage" value="">
								<div style="width:24px;height:24px;border:1px solid #868686;"></div>
							</logic:equal>
							<logic:equal name="selfInfoForm" property="scale" value="false">
								<div style="width:24px;height:24px;border:1px solid #868686;"></div>
							</logic:equal>
						</dt>
						<dd>24*24</dd>
					</dl>
				</div>

				<div class="bh-clear"></div>
				<div class="hj-wzm-jtys-right-bottom" id="btn">
					<p>
						说明：<br /> 1.拖动小边框选择合适区域生成小头像<br /> 2.小边框缩放调整边框大小
					</p>
					<input type="button" value="保存头像" onclick="photocut()" style="margin-top:15px;" class="mybutton" />
				</div>
			</div>
		</div>
	</form>
</body>
</html>
