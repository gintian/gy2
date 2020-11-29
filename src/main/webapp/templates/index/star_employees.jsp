<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.HomeForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<hrms:themes></hrms:themes>
<script type="text/javascript" src="/jquery/jquery-3.5.1.min.js"></script>
<html:form action="/templates/index/star_employees">
<br>
<%
	HomeForm homeForm=(HomeForm)session.getAttribute("homeForm");
	ArrayList peoplenews = homeForm.getPeopledesc();
	ArrayList photourl = homeForm.getPhotourl();
	String dbpre=homeForm.getDbpre();
	int peoplenumber=homeForm.getPeopleNumber();
	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag ="";
    if(userView != null)
    {
        bosflag =userView.getBosflag();
    }
    String portalid=(String)request.getParameter("id");
	%>
<script language="javascript">
$(window).resize(function (){
	homeForm.action="/templates/index/star_employees.do?b_search=link&id=<%=portalid%>";
	homeForm.submit();
});
var bodywidth=document.body.clientWidth;
var bodyheight=document.body.clientHeight;
/** 修改缺陷2561 zgd 2014-7-16*/
var iedom=document.all||document.getElementById
var sliderheight="";
var sliderwidth="";
var slidespeed=1
var copyspeed=slidespeed
var actualheight=''
var actualwidth=''
var cross_slide,cross_slide2
var cross_slide3,cross_slide4
var topdownslide=new Array()
var leftrightslide=new Array()
if(document.body.clientWidth >= document.body.clientHeight){
	gowidth();
}else{
	goheight();
}
function goheight(){
	if(document.body.clientWidth>=150){
		sliderwidth=document.body.clientWidth-2;
	}else{
		sliderwidth=120;
	}
	sliderheight=document.body.clientHeight;
	topdownslide=new Array()
	slidebgcolor="#fff"
	var finalslide=''
	
	topdownslide[0]='<table border="0"  cellspacing="0" cellpadding="0" height="<%=150*peoplenumber%>px" style="margin-top:0px;">';
	<%for(int m=0;m<peoplenumber;m++){%>
			  //主页展示模块，明星员工照片背景色   jingq add 2014.10.17
	          topdownslide[0]+='<tr class="starphoto">';
	          topdownslide[0]+='<td align="center" nowrap style="padding-left:5px;">';
	          topdownslide[0]+='<div class="photo1">';
	          topdownslide[0]+='<a href="###" >';
			  topdownslide[0]+='<img src=<%=photourl.get(m)%>  height="85" width="60" border=0	onclick=winhref("/templates/index/star_data.do?br_search_data=link")>';
			  topdownslide[0]+='</a>';
			  topdownslide[0]+='</div>';
	          topdownslide[0]+='</td>';
	          topdownslide[0]+='<td width="10">';
	          topdownslide[0]+='</td>';
	          topdownslide[0]+='<td>';
	          topdownslide[0]+='<%=peoplenews.get(m)%>';
	          topdownslide[0]+='</td>';
	          topdownslide[0]+='</tr>';
	<%}%>
	topdownslide[0]+='</table>';
	topdownslide='<nobr>'+topdownslide.join(" ")+'</nobr>'
		document.write('<span id="temp" style="visibility:hidden;position:absolute;top:0;left:0">'+topdownslide+'</span>')
	
	window.onload=fillup
		with (document){
			document.write('<table border="0" cellspacing="0" cellpadding="0" style="position:absolute;left:0;top:0">')
			if (iedom){
				//write('<tr><td height="15" align="center">')
				//write('<div id="leftbuttom" onclick="" onMouseover="copyspeed=0" onmouseup="cross_slide.style.top=parseInt(cross_slide.style.top)-'+sliderheight/2+';cross_slide2.style.top=parseInt(cross_slide2.style.top)-'+sliderheight/2+'"  onMouseout="copyspeed=slidespeed" style="position:relative;float:top;cursor:pointer;width:30px;height:15px;background:url(/images/hcm/themes/default/icon/icon7_hover.png) no-repeat;top:10px;"></div>')
				//write('</td></tr>')
				write('<tr><td>')
				write('<div style="position:relative;width:'+sliderwidth+';height:'+sliderheight+';overflow:hidden;">')
				write('<div style="position:absolute;width:'+sliderwidth+';height:'+sliderheight+';background-color:'+slidebgcolor+'" onMouseover="copyspeed=0;" onMouseout="copyspeed=slidespeed;">')
				write('<div id="test2" style="height:<%=150*peoplenumber%>;position:absolute;left:0;top:'+bodyheight+'"></div>')
				write('<div id="test3" style="position:absolute;left:0;top:'+bodyheight+';height:<%=150*peoplenumber%>;"></div>')
				write('</div></div>')
				write('</td></tr>')
				//write('<tr><td height="10" align="center">')
				//write('<div id="rightbuttom" onclick="" onMouseover="copyspeed=0" onmouseup="cross_slide.style.top=parseInt(cross_slide.style.top)-<%=150*peoplenumber%>+'+sliderheight/2+';cross_slide2.style.top=parseInt(cross_slide2.style.top)-<%=150*peoplenumber%>+'+sliderheight/2+'"  onMouseout="copyspeed=slidespeed" style="position:relative;cursor:pointer;width:25px;height:10px;background:url(/images/hcm/themes/default/icon/icon7.png) no-repeat;"></div>')
				//write('</td></tr>')
			}
			document.write('</table>')
		}
}
function fillup(){
		cross_slide=document.getElementById? document.getElementById("test2") : document.all.test2
		cross_slide2=document.getElementById? document.getElementById("test3") : document.all.test3
		<%if(peoplenumber>3){%>
			cross_slide.innerHTML=cross_slide2.innerHTML=topdownslide
		<%}else{%>
			cross_slide.innerHTML=topdownslide
		<%}%>
		actualheight=document.all? cross_slide.offsetHeight : document.getElementById("temp").offsetHeight
		cross_slide2.style.top=actualheight-5+bodyheight
	lefttime=setInterval("slideleft()",30)
}

function slideleft(){
		if (parseInt(cross_slide.style.top)>(actualheight*(-1)+8))
			cross_slide.style.top=parseInt(cross_slide.style.top)-copyspeed
		else{
			<%if(peoplenumber>3){%>
				cross_slide.style.top=parseInt(cross_slide2.style.top)+actualheight-5
			<%}else{%>
				cross_slide.style.top=parseInt(cross_slide2.style.top)+5+bodyheight
			<%}%>
		}
		if (parseInt(cross_slide2.style.top)>(actualheight*(-1)+8))
			cross_slide2.style.top=parseInt(cross_slide2.style.top)-copyspeed
		else
			cross_slide2.style.top=parseInt(cross_slide.style.top)+actualheight-5
}
function gowidth(){
	if(document.body.clientWidth>=150){
		sliderwidth=document.body.clientWidth-2;
	}else{
		sliderwidth=120;
	}
	sliderheight=document.body.clientHeight;
	leftrightslide=new Array()
	slidebgcolor2="#fff"
	var finalslide=''
	
	leftrightslide[0]='<table border="0"  cellspacing="0" cellpadding="0" width="<%=270*peoplenumber%>px" style="margin-top:0px;">';
	leftrightslide[0]+='<br>';
	leftrightslide[0]+='<tr>';
	<%for(int n=0;n<peoplenumber;n++){%>
		      //主页展示模块，明星员工照片背景色   jingq add 2014.10.17
			  leftrightslide[0]+='<td class="starphoto">';
			  leftrightslide[0]+='<table>';
			  leftrightslide[0]+='<tr>';
	          leftrightslide[0]+='<td align="center" nowrap>';
	          leftrightslide[0]+='<div class="photo1">';
	          leftrightslide[0]+='<a href="###" >';
			  leftrightslide[0]+='<img src=<%=photourl.get(n)%>  height="85" width="60" border=0	onclick=winhref("/templates/index/star_data.do?br_search_data=link")>';
			  leftrightslide[0]+='</a>';
			  leftrightslide[0]+='</div>';
	          leftrightslide[0]+='</td>';
	          leftrightslide[0]+='<td width="10">';
	          leftrightslide[0]+='</td>';
	          leftrightslide[0]+='<td width="185">';
	          leftrightslide[0]+='<%=peoplenews.get(n)%>';
	          leftrightslide[0]+='</td>';
	          leftrightslide[0]+='<td width="10">';
	          leftrightslide[0]+='</td>';
	          leftrightslide[0]+='</tr>';
	          leftrightslide[0]+='</table>';
			  leftrightslide[0]+='</td>';
	<%}%>         
	leftrightslide[0]+='</tr>';
	leftrightslide[0]+='</table>';
	leftrightslide='<nobr>'+leftrightslide.join(" ")+'</nobr>'
		document.write('<span id="temp2" style="visibility:hidden;position:absolute;top:0;left:0">'+leftrightslide+'</span>')
	window.onload=fillup2
		with (document){
			document.write('<table border="0" cellspacing="0" cellpadding="0" style="position:absolute;left:0;top:0">')
			if (iedom){
				write('<tr><td>')
				write('<div style="position:relative;width:'+sliderwidth+';height:'+sliderheight+';overflow:hidden;">')
				write('<div style="position:absolute;width:'+sliderwidth+';height:'+sliderheight+';background-color:'+slidebgcolor2+'" onMouseover="copyspeed=0;" onMouseout="copyspeed=slidespeed;">')
				write('<div id="test4" style="height:<%=150*peoplenumber%>;position:absolute;left:'+bodywidth+';top:0"></div>')
				write('<div id="test5" style="position:absolute;left:'+bodywidth+';top:0;height:<%=150*peoplenumber%>;"></div>')
				write('</div></div>')
				write('</td></tr>')
			}
			document.write('</table>')
		}
}
function fillup2(){
		cross_slide3=document.getElementById? document.getElementById("test4") : document.all.test4
		cross_slide4=document.getElementById? document.getElementById("test5") : document.all.test5
		<%if(peoplenumber>3){%>
			cross_slide3.innerHTML=cross_slide4.innerHTML=leftrightslide
		<%}else{%>
			cross_slide3.innerHTML=leftrightslide
		<%}%>
		actualwidth=document.all? cross_slide3.offsetWidth : document.getElementById("temp2").offsetWidth
		cross_slide4.style.left=actualwidth-5+bodywidth/** test5也需要相应的右移bodywidth距离*/
	lefttime=setInterval("slideleft2()",30)/** 每30毫秒执行一次方法*/
}

function slideleft2(){
		if (parseInt(cross_slide3.style.left)>(actualwidth*(-1)+8))/** test4还未滚动完*/
			cross_slide3.style.left=parseInt(cross_slide3.style.left)-copyspeed/** test4从div中定义的left的距离bodywidth处开始滚动*/
		else{
			<%if(peoplenumber>3){%>
				cross_slide3.style.left=parseInt(cross_slide4.style.left)+actualwidth-5/**+bodywidth  多个照片时，每两组间不用间隔*/
			<%}else{%>
				cross_slide3.style.left=parseInt(cross_slide4.style.left)+5+bodywidth/** 重新滚动时，也要加上div中的left的距离bodywidth*/
			<%}%>
		}
		if (parseInt(cross_slide4.style.left)>(actualwidth*(-1)+8))
			cross_slide4.style.left=parseInt(cross_slide4.style.left)-copyspeed
		else
			cross_slide4.style.left=parseInt(cross_slide3.style.left)+actualwidth-5
}

function winhref(url)
{
	while(url.indexOf("`")!=-1){
   		url = url.replace("`","&");
   	}
	window.open(url,"_blank","left=0,top=0,width="+(screen.availWidth-10)+",height="+(screen.availHeight-40)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}
<%if("hcm".equals(bosflag)){%>
parent.removeElementsByClassName('x-tool-right',parent.Ext.getCmp('tol<%=portalid%>'));/** 缺陷3350 明星员工:打开明星员工网页左下角有错误，需要去掉more zgd 2014-7-26*/
<%}%>
</script>
</html:form>
