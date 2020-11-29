<%@page import="com.hjsj.hrms.actionform.kq.options.sign_point.KqSignPointForm"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	boolean version = false;
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
		}
	KqSignPointForm kspf = (KqSignPointForm)session.getAttribute("kqSignPointForm");
%>
<hrms:themes></hrms:themes>
<script type="text/javascript" src="../../../ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="../../../ext/ext-all.js"></script>
<script type="text/javascript" src="../../../ext/rpc_command.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script> 
<script language="javascript" src="/kq/options/signpoint/kqSignPoint.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=1.4"></script>

<style>
BODY
{
	margin: 0px 0px 0px 0px;
}

 .div-a{ position: absolute;z-index: 100;width:100%;height:37px;
     /*-moz-box-shadow: 0px 5px 3px #F4F2EE;
    -webkit-box-shadow: 0px 5px 3px #F4F2EE;
    box-shadow: 0px 5px 3px #F4F2EE;
   横向偏移0素，纵向偏移5像素，模糊3像素，颜色 */
    
 }
 .div-b{background:url(../../../images/new-nav-bg_80abdf2.png) repeat-x 0 0;position: absolute;z-index: 101;width:100%;height:30px;
 filter:alpha(Opacity=90);-moz-opacity:0.9;opacity: 0.9;-webkit-box-shadow: #666 0px 0px 5px;-moz-box-shadow: #666 0px 0px 5px;
 box-shadow: #666 0px 0px 5px;} 
 /* CSS注释说明：这里对CSS代码换行是为了让代码在此我要中显示完整，换行后CSS效果不受影响 */
 </style>
</head>
<body style="overflow: hidden;"> 
  <td>
    <div class="div-a">
    <div class="div-b">
 		<table border=0 width="100%" height="100%">   
           <tr >
           <td height="20" width="85%">
            <input type="text" id="searchbox" class="text4" style="margin-bottom: 10px;width:300px;font-size:10pt;" onkeydown="checkKeys(event)">
            <input onclick="search();" style="margin-bottom: 8px;" class="mybutton" type="button" value="搜索">  
           </td>
           <td class="RecordRow" align="right" style="border: none;padding-bottom: 5px;" nowrap="nowrap"> 姓名    </td>
            <td align="left" style="padding-right:10px;vertical-align: middle;">
        <input type="text" id="personBox"  value=""  class="text4 editor" style="width:100px;font-size:10pt;text-align:left;margin-bottom: 10px" id="selectname" onkeyup="showDateSelectBox(this);" title='可以输入"姓名","身份证号","拼音简码"进行查询'>
     	</td>
      </tr>
       </table> 
 		</div>
 		</div>
       
        <div id="baidumap"  style="width: 100%;height:100%; z-index: 0">
        </div>
     </td>

    <div id="searchResultPanel"  style="border:1px solid #C0C0C0;width:150px;height:auto;position: absolute;top:30px;left:10px;display:none;"></div>
   
    <div id="date_panel" style="display:block;z-index:999;">
		<select id="date_box" name="contenttype"  onblur="document.getElementById('date_panel').style.display='none';" style="width:254" size="6" ondblclick="checkPersonPoint(this);">
        </select>
	</div>
	
 <html:form action="/kq/options/sign_point/setsign_point">
 </html:form>  
<script type="text/javascript">
var pointRadius = Number('${kqSignPointForm.pointRadius}');//范围半径
var selectedA0100 = '${kqSignPointForm.selectedA0100}';//选中的人
try {
  var icon = new BMap.Icon("/images/seftMarker.png",new BMap.Size(22,25),{anchor:new BMap.Size(10,25)});
} catch(e) {
	alert(KQ_BAIDU_MAP_NOT_AVAILABLE);
}
var point_city;//事件点所在的城市
var AddFlag; //保存事件标识
var privFlag=0;
<hrms:priv func_id="2703701">  
  privFlag = 1;
</hrms:priv>

// 百度地图API功能
var baiduMap = new BMap.Map("baidumap",{enableMapClick:false});            // 创建Map实例
baiduMap.enableScrollWheelZoom();
var initStr = "baiduMap.centerAndZoom";
var center = '${kqSignPointForm.arealevel}';
if(center.indexOf("P")!=-1){
	center = center.substr(1).split(",");
	center = new BMap.Point(center[0], center[1]);
	initStr+="(center,16);";
}else if(center.indexOf("C")!=-1){
	center = center.substr(1);
	initStr+="(center);";
}else{
	center = new BMap.Point(106.321805,38.423991);
	initStr+="(center,5);";
}
eval(initStr);

var opts = {anchor: BMAP_ANCHOR_TOP_LEFT, offset: new BMap.Size(0, 50)};   
baiduMap.addControl(new BMap.NavigationControl(opts));  //添加默认缩放平移控件
baiduMap.addControl(new BMap.MapTypeControl({mapTypes: [BMAP_NORMAL_MAP,BMAP_HYBRID_MAP],anchor: BMAP_ANCHOR_TOP_RIGHT, offset: new BMap.Size(10, 50)}));     //2D图，卫星图

//通过坐标点反解析坐标的省市县描述
var geocode = new BMap.Geocoder();

if(privFlag == '1'){
	//自定义右键按钮
	var menu = new BMap.ContextMenu();
	//menu.addEventListener("open",function(event){document.getElementById("locations").value=event.point.lng+","+event.point.lat;});
	menu.addItem(new BMap.MenuItem('添加移动考勤点',function(menuLocation){addMenuMarker(menuLocation);},100));
	baiduMap.addContextMenu(menu);
}

var obj = parent.frames['mil_menu'].personObj;
if(parent.frames['mil_menu'].personObj != undefined)
   document.getElementById('personBox').value = parent.frames['mil_menu'].personObj[0];
   
initSearchBox();
</script>

<!-- 在地图上添加 考勤点marker -->
<logic:iterate id="point" name="kqSignPointForm" property="signPoints">
   <script>
   initPointMarker('${point.pid}','${point.location}','${point.isAdded}','${point.name}');
   </script>
</logic:iterate>
</body>
