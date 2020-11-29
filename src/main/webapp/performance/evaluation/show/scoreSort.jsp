<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes />
<link rel="stylesheet" type="text/css" href="../../ajax/skin.css"></link>
<link href="../../css/xtree.css" rel="stylesheet" type="text/css" >
<SCRIPT LANGUAGE=javascript src="../../js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="../../js/validate.js"></SCRIPT>  
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 200px;height: 100px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
  border: inset 1px #C4D8EE;
 BORDER-BOTTOM: #C4D8EE 1pt solid; 
 BORDER-LEFT: #C4D8EE 1pt solid; 
 BORDER-RIGHT: #C4D8EE 1pt solid; 
 BORDER-TOP: #C4D8EE 1pt solid;
 

 

 
}
</STYLE>
<hrms:themes />
<script>
var COLOR_INIT="#ffffff";//#ffffff
var COLOR_OVER="#e3e3Df";
var COLOR_SELECTED="#fff8d2";
var obj_tr;
var pointSel;
var pointDisp;
var type;
function closeWin() {
    if(window.showModalDialog){
        parent.window.close();
    }else {
        var win = parent.parent.Ext.getCmp('sort_win');
        if(win) {
            win.close();
        }
    }
}
function init_color(obj) { 
        for(var i=0;i<obj.length;i++){         
       		 obj[i].style.backgroundColor  =  COLOR_INIT;
             obj[i].className = "";
        }
}
function getSelPoint(code,name,type1)
{
	pointSel = code;
	pointDisp = name;
	type=type1;
}
function creat_event(obj) {
        for(var i=0;i<obj.length;i++){
       // obj[i].onmouseover  =  overdo;
       // obj[i].onmouseout   =  outdo;
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
        var table=document.getElementById('righttable');
        obj_tr=table.getElementsByTagName('tr');      
        init_color(obj_tr);
        creat_event(obj_tr);
}

//添加
function additemmy()
{	/*if(type!='point')
	{
		alert("<bean:message key='performance.implement.pleaseSel'/>");
		return;
	}*/
	var pd = document.getElementsByName('shpid');
    for(j=0;j<pd.length;j++)
    {
		if(pointSel==pd[j].value)
		{
			alert("<bean:message key='performance.implement.haveAdd'/>");
			return;
		}		   
	}
	if((pointSel==null) || (pointSel=="undefined"))
		return;

	createChild(document.getElementById('righttable'),pointDisp,pointSel);	
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
		    myNewCell.className = "RecordRow_right";
   			myNewCell.innerHTML = "&nbsp;&nbsp;&nbsp;"+leng+"&nbsp;&nbsp;&nbsp;";
		    
		    myNewCell=tr.insertCell(1);
		    myNewCell.className = "RecordRow";
   			myNewCell.innerHTML = "<span id=spid name=spid >"+psvalue+"</span><input type='hidden' name='shpid' id='shpid' value='"+phvalue+"'/>";
		    
		    myNewCell=tr.insertCell(2);
		    myNewCell.className = "RecordRow_left";
   			myNewCell.innerHTML = "<select id='pxfield' name='reffield'><option value='1'>升序</option><option value='0'>降序</option></select>";
		}
function updiv(obj) 
{
    var n=0;  
    for(var i=0;i<obj.length;i++)
    {
        if(obj[i].className.indexOf("mySelectedTr")>-1)
          { change_tr(i-1,i); n+=1;return;}               
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
function colorRGB2Hex(color) {
    var rgb = color.split(',');
    var r = parseInt(rgb[0].split('(')[1]);
    var g = parseInt(rgb[1]);
    var b = parseInt(rgb[2].split(')')[0]);
 
    var hex = "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
    return hex;
 }
//删除功能
function deleteTR(obj)
{
   var n=0; 
        for(var i=0;i<obj.length;i++)
        { 
        	var bac = obj[i].style.backgroundColor;
        	if(!window.showModalDialog){
        		bac = colorRGB2Hex(bac);
        	}
            if(bac==COLOR_SELECTED)
            {
                var table=document.getElementById('righttable');
                table.deleteRow(i);  
                n+=1; 
                break;
           }             
        }
        if(n==0) alert("<bean:message key='performance.implement.selectOne'/>");
        else
        {	
        	var table=document.getElementById('righttable');
            var tr = table.rows;
            var h=0;
            for(var t=1;t<tr.length;t++)
            {
         		 var tds = tr[t].cells;
         		 if(tds[0]!=null)        
             		tds[0].innerHTML='&nbsp;&nbsp;&nbsp;'+(t-h)+'&nbsp;&nbsp;&nbsp;';     
          		 else        
            		 h++;     
      	    }           
        }
}
function change_tr(x,y) 
{
    if(x<1){ alert(CANNOT_UP);return;}
    if(x>=obj_tr.length){ alert(CANNOT_DOWN);return;}   
    
    var obj_td_x=obj_tr[x].getElementsByTagName('td'); 
    var obj_td_y=obj_tr[y].getElementsByTagName('td'); 
    var a_1  =  obj_td_x[0].innerHTML;
    var a_2  =  obj_td_x[1].innerHTML;
    var a_3  =  obj_td_x[2].innerHTML;
    var b_1  =  obj_td_y[0].innerHTML;   
    var b_2  =  obj_td_y[1].innerHTML;  
    var b_3  =  obj_td_y[2].innerHTML;  
    //obj_td_x[0].innerHTML=b_1;
    obj_td_x[1].innerHTML=b_2;
    obj_td_x[2].innerHTML=b_3;
    //obj_td_y[0].innerHTML=a_1;   
    obj_td_y[1].innerHTML=a_2;
    obj_td_y[2].innerHTML=a_3;
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
    var px = document.getElementsByName('reffield');//pxfield 
    var pfields=document.getElementsByName('shpid');
    var j;
  //  var sxsql='';
   // var jxsql='';
    var orderSql='';
   	for(j=0;j<px.length;j++)
   	{
   	/*	if(px[j].value=='1'){
   			if(sxsql==''){
   				sxsql=pfields[j].value;
   			}else
			sxsql+=" ASC,"+pfields[j].value;
		
   		}else if(px[j].value=='0'){
   			if(jxsql==''){
   				jxsql=pfields[j].value;
   			}else
   			jxsql+=" DESC,"+pfields[j].value;
   			
   		}*/
   		
   		if(px[j].value=='1')   		   			
			orderSql+=","+pfields[j].value+" ASC";		
   		else if(px[j].value=='0')
   			orderSql+=","+pfields[j].value+" DESC";		
   	}
    if(orderSql!='')
		orderSql=' order by '+orderSql.substring(1);
	else if(orderSql==''){
		alert("<bean:message key='performance.implement.orderinfo'/>");
		orderSql=' order by a0000';
	}
	returnValue=orderSql;
	
	if(window.showModalDialog){
        parent.window.returnValue=returnValue;
	}else {
		parent.parent.sort_ok(returnValue);
	}
	closeWin();
}
</script>

<base id="mybase" target="_self">

<html:form action="/performance/evaluation/performanceEvaluation">
	<table width="640px;" align="center" border="0" cellpadding="0" height="100%"
		cellspacing="0">
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTable">
						<tr>
							<td align="center" class="TableRow" nowrap >
								<bean:message key="label.zp_exam.sort" />
								&nbsp;&nbsp;
							</td>
						</tr>
					<tr>
						<td width="90%" align="center" class="RecordRow" >
							<table border="0">
								<tr>
									<td align="center" width="41%">
										<table width="100%">
											<tr>
												<td width="100%" align="center">
													<bean:message key="selfservice.query.queryfield" />
													&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td align="left">
													<div id="treemenu"
														style="overflow:auto;width:230px;height:220px;"
														class="div2"></div>
												</td>
											</tr>
										</table>
									</td>

									<td width="9%" align="center">
										<html:button styleClass="mybutton" property="b_addfield"
											onclick="additemmy();">
											<bean:message key="button.setfield.addfield" />
										</html:button>
										<br>
										<br>
										<html:button styleClass="mybutton" property="b_delfield"
											onclick="deleteTR(obj_tr);">
											<bean:message key="button.setfield.delfield" />
										</html:button>
									</td>
									<td width="41%" align="center">
										<table align="center" width="100%" border="0" cellspacing="0"
											cellpadding="0">
											<tr>
												<td align="center">
													<table cellSpacing=0 cellPadding=0 width="90%" border=0>
														<thead>
															<tr>
																<TD align="center" width="100%" valign="top" height="20">
																	<bean:message key="label.query.selectedsortfield" />
																</TD>
															</tr>
														</thead>
													</table>
												</td>
											</tr>
											<tr>
												<td align="center">
													<div id="tbl_container" class="div2 common_border_color"
														style="overflow:auto;width:230px;height:220px;">
														<table cellSpacing=0 cellPadding=0 width="100%" border=0 class="ListTable"
															id="righttable">
															<TR>
																<TD align="center" style="width:35px;height:22px;border-top:0px;" class="TableRow_right" valign="center">
																	<bean:message key="recidx.label" />								
																</TD>
																<TD align="center"  style="width:125px;height:22px;border-top:0px;" 
																	class="TableRow" valign="center">
																	<bean:message key="static.target"/>
																</TD>
																<TD align="center"  style="width:50px;height:22px;border-top:0px;"
																	class="TableRow_left" valign="center">
																	<bean:message key="label.query.baseDesc" />
																</TD>
															</TR>
														</table>
												</td>
                                            </tr>
											</tr>
										</table>
									</td>
									<td width="9%" align="center">
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
						<td align="center" class="RecordRow" nowrap style="height:35">
							<input type='button'
								value='<bean:message key="kq.formula.true"/>' onclick='sub()'
								class="mybutton" />
							<input type="button" value="<bean:message key="button.cancel"/>"
								onclick="closeWin();" class="mybutton">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">  
  init_event();
  
  var m_sXMLFile	= "/performance/evaluation/show/scoreSortTree.jsp?planID=${param.planid}&computeFashion=${param.computeFashion}&busitype=${evaluationForm.busitype}";		
  var root=new xtreeItem("root","所有指标","javascript:void(0)","_self","所有指标","/images/add_all.gif",m_sXMLFile);
  Global.showroot=false;	
  Global.closeAction="additemmy();";
  root.setup(document.getElementById("treemenu"));  
</script>
