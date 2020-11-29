<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes />
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<STYLE type=text/css>
TEXTAREA {
	SCROLLBAR-FACE-COLOR: #b5daff; SCROLLBAR-HIGHLIGHT-COLOR: #ffffff; SCROLLBAR-SHADOW-COLOR: #000000; SCROLLBAR-ARROW-COLOR: #0000ff; SCROLLBAR-BASE-COLOR: #6699ff; scrollbar-dark-shadow-color: #6699ff
}

/*SELECT {
	BORDER-RIGHT: #23365d 1px solid; BORDER-TOP: #97aad0 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #3559a4 1px solid; BORDER-BOTTOM: #031333 1px solid
}*/
/*OPTION {
	BORDER-RIGHT: #23365d 1px solid; BORDER-TOP: #97aad0 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #3559a4 1px solid; BORDER-BOTTOM: #031333 1px solid
}*/
TD {
	FONT-SIZE: 12px
}
.m_frameborder {
	BORDER-RIGHT: #ffffff 1px inset; 
	BORDER-TOP: #d4d0c8 1px inset; 
	FONT-SIZE: 10px; 
	OVERFLOW: hidden; 
	BORDER-LEFT: #d4d0c8 1px inset; 
	WIDTH: 25px; BORDER-BOTTOM: #ffffff 1px inset; 
	FONT-FAMILY: "Tahoma"; 
	HEIGHT: 19px; 
	BACKGROUND-COLOR: transparent; 
	TEXT-ALIGN: right
}
.m_arrow {
	PADDING-LEFT: 2px; 
	FONT-SIZE: 7px; 
	WIDTH: 16px; 
	CURSOR: default; 
	LINE-HEIGHT: 2px; 
	FONT-FAMILY: "Webdings"; 
	HEIGHT: 8px
}
.m_input {
	BORDER-RIGHT: black 0px solid; 
	BORDER-TOP: black 0px solid; 
	FONT-SIZE: 9px; BORDER-LEFT: 
	black 0px solid;
	WIDTH: 18px; 
	BORDER-BOTTOM: black 0px solid; 
	FONT-FAMILY: "Tahoma"; 
	HEIGHT: 14px; 
	TEXT-ALIGN: right
}
INPUT {
	BACKGROUND-COLOR: transparent
}
.TEXT12 {
	BORDER-RIGHT: none; 
	BORDER-TOP: none; 
	BORDER-LEFT: none; 
	BORDER-BOTTOM: none; 
	BACKGROUND-COLOR: #ffffff
}

.RecoRowConition 
{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}

.div2
{
 overflow-x:hidden;
 overflow-y:auto; 
 width: 200px;height: 230px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
 scrollbar-base-color:#ff66ff; 
 scrollbar-face-color:none;
 scrollbar-arrow-color:none;
 scrollbar-track-color:#ffffff;
 scrollbar-3dlight-color:#ffffff;
 scrollbar-darkshadow-color:#ffffff;
 scrollbar-highlight-color:#e5c8e5;
 scrollbar-shadow-color:#e5c8e5"
 SCROLLBAR-DARKSHADOW-COLOR: #ffffff;
 border-bottom: 1px solid #9a9a9a;
}
</STYLE>
<script>
var COLOR_INIT="#ffffff";
var COLOR_OVER="#e3e3Df";
var COLOR_SELECTED="#fff8d2";
var obj_tr;
//右边值背景颜色
function init_color(obj) {
        for(var i=0;i<obj.length;i++){         
       		 obj[i].style.backgroundColor  =  COLOR_INIT;
             obj[i].className = "";
        }
}


function creat_event(obj) {
        for(var i=0;i<obj.length;i++){
//        obj[i].onmouseover  =  overdo;
//        obj[i].onmouseout   =  outdo;
        obj[i].onclick      =  clickdo;
    }
}
function overdo() {
        if(this.className.indexOf("mySelectedTr")==-1)this.style.backgroundColor=COLOR_OVER;
}
function outdo() {
        if(this.className.indexOf("mySelectedTr")==-1)this.style.backgroundColor=COLOR_INIT;
}
function clickdo(obj) {
        init_color(obj_tr);
       
        this.style.backgroundColor=COLOR_SELECTED;
        //用于判断是否选中
        this.className = this.className+" mySelectedTr";
}

function init_event() {
        var table=document.getElementById('righttable');//得到一个对象;
        obj_tr=table.getElementsByTagName('tr');  //方法可返回带有指定标签名的对象的集合
        //alert(obj_tr.length);        
        init_color(obj_tr);
        creat_event(obj_tr);
}

//添加
function additemmy(sourcebox_id)
{
	  var left_vo,right_vo,vos,i,j;
	  vos= document.getElementsByName(sourcebox_id);
	  if(vos==null)
	  		return false;
	  left_vo=vos[0];
	  for(i=0;i<left_vo.options.length;i++)
	  {
		    if(left_vo.options[i].selected)
		    {
		    	var pd = document.getElementsByName('shpid');  //得到一个对象
		    	var flag = 1;
		    	for(j=0;j<pd.length;j++)
		    	{
		    		if(left_vo.options[i].value==pd[j].value)
		    		{
		    			flag=0;
		    			break;
		    		}		    					    		
		    	}
		    	//righttable左边值的DI
		    	if(flag==1)
		    		createChild(document.getElementById('righttable'),left_vo.options[i].text,left_vo.options[i].value);
		    }
	  }
     init_event();   
	 return true;	  	
}


function createChild(tableobjs,psvalue,phvalue) {
		    var tr = tableobjs.insertRow(tableobjs.rows.length);
		    var myNewCell=tr.insertCell(0);
		    var leng = 0;
		    if(tableobjs.childNodes[0].nextSibling) {
		    	leng = tableobjs.childNodes[0].nextSibling.childNodes.length-2
		    }else {
		    	leng = tableobjs.childNodes[0].childNodes.length-1
		    }
		    myNewCell.className = "RecordRow";
   			myNewCell.innerHTML = "&nbsp;&nbsp;&nbsp;"+leng+"&nbsp;&nbsp;&nbsp;";
		    
		    myNewCell=tr.insertCell(1);
		    myNewCell.className = "RecordRow";
   			myNewCell.innerHTML = "<span id=spid name=spid >"+psvalue+"</span><input type='hidden' name='shpid' id='shpid' value='"+phvalue+"'/>";
		    
		    myNewCell=tr.insertCell(2);
		    myNewCell.className = "RecordRow";
   			myNewCell.innerHTML = "<select id='pxfield' name='pxfield'><option value='1'>升序</option><option value='0'>降序</option></select>";
		}
function updiv(obj) {
        var n=0; 
        for(var i=0;i<obj.length;i++){
                if(obj[i].className.indexOf("mySelectedTr")>-1){ change_tr(i-1,i); n+=1; return;}
    }
    if(n==1) alert("<bean:message key='performance.implement.selectOne'/>");
}

function downdiv(obj) {
        var n=0; 
        for(var i=0;i<obj.length;i++){ 
                if(obj[i].className.indexOf("mySelectedTr")>-1){ change_tr(i+1,i); n+=1; return;}
    }
    if(n==0) alert("<bean:message key='performance.implement.selectOne'/>");
}

//删除功能
function deleteTR(obj)
{
   var n=0;
    for(var i=1;i<obj.length;i++){
            if(obj[i].className.indexOf("mySelectedTr")>-1)
            {
             var table=document.getElementById('righttable');
             table.deleteRow(i);
             n+=1;
             return;
            }

       }
    if(n==0) alert(PLEASE_SEL_DELINDEX);
}

function change_tr(x,y) {

    if(x<1){ alert(CANNOT_UP);return;}
    if(x>=obj_tr.length){ alert(CANNOT_DOWN);return;}
               
    var obj_td_x=obj_tr[x].getElementsByTagName('td'); 
   	var obj_td_y=obj_tr[y].getElementsByTagName('td'); 
    var a_1  =  obj_td_x[0].innerHTML;
    var a_2  =  obj_td_x[1].innerHTML;
    var a_3  =  obj_td_x[2].innerHTML;
    //var a_4  =  obj_td_x[3].innerHTML;
    var b_1  =  obj_td_y[0].innerHTML;   
    var b_2  =  obj_td_y[1].innerHTML;  
    var b_3  =  obj_td_y[2].innerHTML; 
    //var b_4  =  obj_td_y[3].innerHTML;   
     
    //obj_td_x[0].innerHTML=b_1;//让序号不变 不要交换
    obj_td_x[1].innerHTML=b_2;
    obj_td_x[2].innerHTML=b_3;
    //obj_td_x[3].innerHTML=b_4;
    //obj_td_y[0].innerHTML=a_1;//让序号不变 不要交换
    obj_td_y[1].innerHTML=a_2;
    obj_td_y[2].innerHTML=a_3;
    //obj_td_y[3].innerHTML=a_4;
    obj_tr[y].style.backgroundColor=COLOR_INIT;
    if (obj_tr[y].className.indexOf("mySelectedTr")>-1){
        var index = obj_tr[y].className.indexOf(" mySelectedTr");
        obj_tr[y].className = obj_tr[y].className.substring(0,index);
    }

    obj_tr[x].style.backgroundColor=COLOR_SELECTED;
    if (obj_tr[x].className.indexOf("mySelectedTr")==-1) {
        obj_tr[x].className = obj_tr[x].className + " mySelectedTr";
    }
}
//传值
function sub(){
    var px = document.getElementsByName('pxfield');
    var pfields=document.getElementsByName('shpid');
    var j;
    var sxsql='';
    var jxsql='';
   	for(j=0;j<px.length;j++){
   		if(px[j].value=='1'){
   			if(sxsql==''){
   				sxsql=pfields[j].value;
   			}else
			sxsql+=" ASC,"+pfields[j].value;
		
   		}else if(px[j].value=='0'){
   			if(jxsql==''){
   				jxsql=pfields[j].value;
   			}else
   			jxsql+=" DESC,"+pfields[j].value;
   			
   		}
   	}
   	   	
      var orderSql='no';//按默认排序这样返回
      if(jxsql!=''&&sxsql!=''){
		orderSql="ORDER BY "+sxsql+" ASC,"+jxsql+" DESC";
	}else if(jxsql==''&&sxsql==''){
		if(confirm("<bean:message key='performance.implement.orderinfo'/>"))
		{
			returnValue="on"
			window.opener = null;
			parent.window.close();
		}else
			return;
	}else if(jxsql==''){
		orderSql="ORDER BY "+sxsql+" ASC";
	}else if(sxsql==''){
		orderSql="ORDER BY "+jxsql+" DESC";
	}
	if (window.showModalDialog){
	    parent.window.returnValue=orderSql;
    }else{
    	parent.window.opener.taxis_ok(orderSql);
    }
	parent.window.close();
}
</script>

<base id="mybase" target="_self">

<html:form action="/performance/implement/performanceImplement">
	<table width="590" align="center" style="" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td>

				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="1" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap colspan="0">
								<bean:message key="label.zp_exam.sort" />
								&nbsp;&nbsp;
							</td>
						</tr>
					</thead>
					<tr>

						<td width="90%" align="center" class="RecoRowConition common_border_color" nowrap>
							<table>
								<tr>
									<td align="center" width="46%">
										<table cellSpacing=0 cellPadding=0 width="90%" border=0>
											<tr>
												<td width="100%" align="left"  valign="top" height="20" >
													<bean:message key="selfservice.query.queryfield" />
													&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td align="left">
													<html:select name="implementForm" property="taxisid"
														size="15" multiple="multiple"
														ondblclick="additemmy('taxisid');"
														style="background-color:#FFFFFF;height:230px;width:200px;font-size:9pt">
														<html:optionsCollection property="taxisList"
															value="dataValue" label="dataName" />
													</html:select>
												</td>
											</tr>
										</table>
									</td>
									<td width="24%" align="center">
										<html:button styleClass="mybutton" property="b_addfield"
											onclick="additemmy('taxisid');">
											<bean:message key="button.setfield.addfield" />
										</html:button>
										<br>
										<br>
										<html:button styleClass="mybutton" property="b_delfield"
											onclick="deleteTR(obj_tr);">
											<bean:message key="button.setfield.delfield" />
										</html:button>
									</td>
									<td width="36%" align="center">
										<table align="center" width="100%" border="0" cellspacing="0"
											cellpadding="0">
											<tr>
												<td align="left">
													<table cellSpacing=0 cellPadding=0 width="90%" border=0>
														<thead>
															<tr>
																<TD align="left" width="50" valign="top" height="20" >
																	<bean:message key="label.query.selectedsortfield" />
																</TD>
															</tr>
														</thead>
													</table>
												</td>
											</tr>
											<tr>
												<td align="left">
													<div id="tbl_container" class="div2 ">
														<table align="center" cellSpacing=0 cellPadding=0 width="101%" border=0
															id="righttable" class="ListTableF" style="margin: -1px -1px 0 -1px;">
															<TR id='fixedHeaderTr' class="fixedHeaderTr">
																<TD align="center" width="30"  height="20" class="TableRow" nowrap style="border-bottom-width:1px;">
																	&nbsp;<bean:message key="recidx.label" />
																	
																</TD>
																<TD align="center" width="70" height="20" class="TableRow" nowrap style="border-bottom-width:1px;">
																	&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="static.target" />
																</TD>
																<TD align="center" width="50" height="20" class="TableRow" nowrap style="border-bottom-width:1px;">
																	&nbsp;&nbsp;<bean:message key="label.query.baseDesc" />
																</TD>
															</TR>
														</table>
												</td>
											</tr>
										</table>
									</td>
									<td width="9%" align="center" style="padding-left: 8px;">
													<input type="button" name="b_addfield"
														value="<bean:message key="kq.shift.cycle.up"/>"
														class="mybutton" onclick="updiv(obj_tr);">
													<br>
													<br>
													<input type="button" name="b_delfield"
														value="<bean:message key="kq.shift.cycle.down"/>"
														class="mybutton" onclick="downdiv(obj_tr)">
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="center"  nowrap style="height:35px;padding-top:5px;">
							<input type='button'
								value='<bean:message key="kq.formula.true"/>' onclick='sub()'
								class="mybutton" />
								<input type="button" 
								value="<bean:message key="button.cancel"/>"
								onclick="parent.window.close();" class="mybutton">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">  
  init_event();
</script>