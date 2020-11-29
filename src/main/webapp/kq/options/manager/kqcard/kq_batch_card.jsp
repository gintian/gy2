<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<%@ page import="com.hjsj.hrms.actionform.kq.options.manager.KqCardForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<%
	KqCardForm kqCardForm = (KqCardForm) session
			.getAttribute("kqCardForm");
	String flag = kqCardForm.getFlag();
	if (flag != null && flag.equals("ok")) {
		out.println("<script language=\"javascript\">");
		out.println("var thevo=new Object();");
		out.println("thevo.flag=\"true\";");
		out.println("window.returnValue=thevo;");
		out.println("window.close();");
		out.println("</script>");
		kqCardForm.setFlag("");
		session.setAttribute("kqCardForm", kqCardForm);
		return;
	}
%>
<STYLE type=text/css>
TEXTAREA {
	SCROLLBAR-FACE-COLOR: #b5daff;
	SCROLLBAR-HIGHLIGHT-COLOR: #ffffff;
	SCROLLBAR-SHADOW-COLOR: #000000;
	SCROLLBAR-ARROW-COLOR: #0000ff;
	SCROLLBAR-BASE-COLOR: #6699ff;
	scrollbar-dark-shadow-color: #6699ff
}

SELECT {
	BORDER-RIGHT: #23365d 1px solid;
	BORDER-TOP: #97aad0 1px solid;
	FONT-SIZE: 12px;
	BORDER-LEFT: #3559a4 1px solid;
	BORDER-BOTTOM: #031333 1px solid
}

OPTION {
	BORDER-RIGHT: #23365d 1px solid;
	BORDER-TOP: #97aad0 1px solid;
	FONT-SIZE: 12px;
	BORDER-LEFT: #3559a4 1px solid;
	BORDER-BOTTOM: #031333 1px solid
}

TD {
	FONT-SIZE: 12px
}

.m_frameborder {
	BORDER-RIGHT: #ffffff 1px inset;
	BORDER-TOP: #d4d0c8 1px inset;
	FONT-SIZE: 10px;
	OVERFLOW: hidden;
	BORDER-LEFT: #d4d0c8 1px inset;
	WIDTH: 25px;
	BORDER-BOTTOM: #ffffff 1px inset;
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
	FONT-SIZE: 9px;
	BORDER-LEFT: black 0px solid;
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

.div2 {
	overflow: auto;
	width: 200px;
	height: 280px;
	line-height: 15px;
	border :1pt solid #C4D8EE;
}

.div3 {
	overflow: auto;
	width: 200px;
	height: 280px;
	line-height: 15px;
	border-width: 1px;
	border-style: groove;
	border-width: thin;
	scrollbar-base-color: #ff66ff;
	scrollbar-face-color: none;
	scrollbar-arrow-color: none;
	scrollbar-track-color: #ffffff;
	scrollbar-3dlight-color: #ffffff;
	scrollbar-darkshadow-color: #ffffff;
	scrollbar-highlight-color: #e5c8e5;
	scrollbar-shadow-color: #e5c8e5 "     
      SCROLLBAR-DARKSHADOW-COLOR :           #ffffff;
	BORDER-BOTTOM: #cccccc 1px solid;
}
</STYLE>
<script language="javascript"><!--   
  function getemp()
  {
    var targetobj,hiddenobj;
    var currnode=Global.selectedItem;
    
    if(currnode==null)
    	return;  
    var id = currnode.uid;
    var text=currnode.text;
    if(id.indexOf("root")!=-1)
      return;  
    if(id.indexOf("UN")!=-1||id.indexOf("UM")!=-1||id.indexOf("@K")!=-1)
      org_item(id);
    else
    {
        return;
    }
  }
  
  function getemp1(){
  	var targetobj,hiddenobj;
    var currnode=Global.selectedItem;	
    if(currnode==null)
    	return;  
    var id = currnode.uid;
    var text=currnode.text;
    if(id.indexOf("root")!=-1)
      return;  
    if(id.indexOf("UN")!=-1||id.indexOf("UM")!=-1||id.indexOf("@K")!=-1)
      return;
    else
    {
       var nbase=id.substring(0,3);
       var a_code=id.substring(3);       
       org_Emp(nbase,"EP"+a_code)
    }
  }
  function org_item(a_code)
  {
    var fObj=document.getElementById("a_code");
    if (!fObj) return;
    fObj.value=a_code;
  }
  function org_Emp(nbase,a_code)
  {
    var nObj=document.getElementById("nbase");
    if (!nObj) return;
    nObj.value=nbase; 
    var aObj=document.getElementById("a_code");
    if (!aObj) return;
    aObj.value=a_code;   
    var gObj=document.getElementById("kq_gno"); 
    var kqgon=gObj.value; 
    var hashvo=new ParameterSet();
    hashvo.setValue("a_code",a_code); 
    hashvo.setValue("nbase",nbase);  
    hashvo.setValue("kq_gno",kqgon);   
    if(isArray($F('r_code')))
    {
      var forms= new Array();
      forms=$F('r_code');     
      hashvo.setValue("r_code",forms);     
    }
    var request=new Request({method:'post',asynchronous:true,onSuccess:addSetCard,functionId:'15207000021'},hashvo);
  }
  function isArray(obj) 
  { 
      return (obj.constructor.toString().indexOf('Array')!= -1);
  } 
  function batch_Emp()
  {
    //var aObj=document.getElementById("a_code");
    //var a_code=aObj.value;
  	var currnode=Global.selectedItem;
  	var a_code = currnode.uid;
    var gObj=document.getElementById("kq_gno"); 
    var kqgon=gObj.value;
    if(a_code!="root")
    {
      var hashvo=new ParameterSet();
      hashvo.setValue("a_code",a_code); 
      hashvo.setValue("kq_gno",kqgon);
      if(isArray($F('r_code')))
      {
       var forms= new Array();
       forms=$F('r_code');
       hashvo.setValue("r_code",forms);
      }
      var request=new Request({method:'post',asynchronous:true,onSuccess:addSetCard,functionId:'15207000023'},hashvo); 
    }else
    {
       alert("请选定单位或部门!");
    }    
  }
  function addSetCard(outparamters)
  {
     var selected_emp=outparamters.getValue("selected_emp");  
     var g_list=outparamters.getValue("gno_list"); 
     if(selected_emp==null||selected_emp.length<=0)
       return false;
     for(var s=0;s<selected_emp.length;s++)
     {
       var table=document.getElementById('tbl');
       if(table==null)
  	  return false;
        var td_num=table.rows.length;
  	var rowCount=table.rows.length;	  
	var tRow = table.insertRow(rowCount);
	var r_name=selected_emp[s].dataName; 
	var r_code=selected_emp[s].dataValue;
	var r_gno=g_list[s];	
	var hidd_id="<input type='hidden' name='r_code' value="+r_code+">";
	hidd_id=hidd_id+"<input type='hidden' name='r_gno' value='"+r_gno+"'>";
	var hidd_name="<input type='hidden' name='r_name' value='"+r_name+"'>";
	var cell_0="<TD width='150' height='20'>&nbsp;"+r_name+""+hidd_id+"&nbsp;</TD>";
	var text_xl="BORDER-RIGHT: none;BORDER-TOP: none;BORDER-LEFT: none;BORDER-BOTTOM: none;BACKGROUND-COLOR: #ffffff;";
	var cell_1="<TD vAlign='center' align='right' width='50'>"+hidd_name+"&nbsp;</TD>";
	for (i=0;i<2;i++)
        { 
             var newCell=tRow.insertCell(i);
             
             switch (i) 
             { 
               case 0 : newCell.innerHTML=cell_0;newCell.width="150";newCell.height="25";break; 
               case 1 : newCell.innerHTML=cell_1; break;                
             } 
         }
     }
     init_event(); 
  }
  function removeCard(sourcebox_id,valuee)
  {
    var vos,right_vo,i;
    vos= document.getElementsByName(sourcebox_id);
    if(vos==null)
  	return false;
    right_vo=vos[0];
    for(i=right_vo.options.length-1;i>=0;i--)
    {
      if(right_vo.options[i].value==valuee)
      {
    	right_vo.options.remove(i);
      }
    }   
    return true;	  	
  }
  function createCard()
  {
    var request=new Request({method:'post',asynchronous:true,onSuccess:reSetCardList,functionId:'15207000022'});
  
  }
  function reSetCardList(outparamters)
  {
       var card_list=outparamters.getValue("card_list"); 
       var list_vo,select_vo,vos,i;
       vos=card_list;
       if(vos==null||vos.length<=0)
  	  return false;
       list_vo=vos;       
       vos= document.getElementsByName("card_no");  
       if(vos==null)
  	  return false;
       select_vo=vos[0];
       for(var i=0;i<list_vo.length;i++)
       {
         var no = new Option();         
    	 no.value=list_vo[i].dataValue;
    	 no.text=list_vo[i].dataName;
    	 if(validateSetField(select_vo,no.value))
    	 {
    	   select_vo.options[select_vo.options.length]=no;
    	 }
    	    	 
       }     
  }
   function reSetCardNo(card_no)
  {
       var list_vo,select_vo,vos,i;
       vos=card_no;
       if(vos==null||vos.length<=0)
  	  return false;
       list_vo=vos;       
       vos= document.getElementsByName("card_no");  
       if(vos==null)
  	  return false;
       select_vo=vos[0];
       var no = new Option();         
       no.value=card_no;
       no.text=card_no;
       if(validateSetField(select_vo,no.value))
       {
    	   select_vo.options[select_vo.options.length]=no;
       }
    	    	 
            
  }
  function validateSetField(select_vo,value)
    {
        for(var i=0;i<select_vo.length;i++)
       {
         var org_value=select_vo.options[i].value;         
         if(org_value==value)
         {
           return false
         }
       }
       return true;
    }
   

 var COLOR_INIT="#ffffff";
 var COLOR_OVER="#e3e3Df";
 var COLOR_SELECTED="#b0afab";
 COLOR_OVER="#DDEAFE";
 COLOR_SELECTED="#fff8d2";
var obj_tr;
function init_color(obj) { 
        for(var i=0;i<obj.length;i++){ obj[i].style.backgroundColor  =  COLOR_INIT;}
}


function creat_event(obj) {
        for(var i=0;i<obj.length;i++){
        obj[i].onmouseover  =  overdo;
        obj[i].onmouseout   =  outdo;
        obj[i].onclick      =  clickdo;
        obj[i].ondblclick   =  dbdeleteTR;
    }
}
function overdo() {
        if(this.style.backgroundColor!=COLOR_SELECTED)this.style.backgroundColor=COLOR_OVER;
}
function outdo() {
        if(this.style.backgroundColor!=COLOR_SELECTED)this.style.backgroundColor=COLOR_INIT;
}
function clickdo(obj) {
        init_color(obj_tr);
       
        this.style.backgroundColor=COLOR_SELECTED;
}
function dbdeleteTR(){
	this.style.backgroundColor=COLOR_SELECTED;
	deleteTR(obj_tr);
}
function init_event() {
        var table=document.getElementById('tbl');
        obj_tr=table.getElementsByTagName('tr');        
        init_color(obj_tr);
        creat_event(obj_tr);
}
function deleteTR(obj)
{
   var n=0; 
        for(var i=0;i<obj.length;i++)
        { 
           if(obj[i].style.backgroundColor==COLOR_SELECTED)
           {
             var obj_td_x=obj[i].getElementsByTagName('td');
             var td_1=obj_td_x[1].innerHTML.indexOf("<");                   
             var c_str=obj_td_x[1].innerHTML.substring(0,td_1);
             if(c_str!=""&&c_str!="&nbsp;")
             {
              reSetCardNo(c_str); 
             }
             var table=document.getElementById('tbl');
             table.deleteRow(i);  
             n+=1;  
             return;
            }
             
        }
        if(n==0) alert("请先选择一行");
}
function deleteALL(obj)
{
  var tr_num=obj.length;  
  for(var i=0;i<tr_num;i++)
  { 
    var obj_td_x=obj[0].getElementsByTagName('td');
    var td_1=obj_td_x[1].innerHTML.indexOf("<");                   
    var c_str=obj_td_x[1].innerHTML.substring(0,td_1);
    if(c_str!=""&&c_str!="&nbsp;")
    {
      reSetCardNo(c_str); 
    }
    var table=document.getElementById('tbl');
    table.deleteRow(0);
  }                  
}
function saveCard()
{
   var waitInfo=eval("wait");	
	   waitInfo.style.display="block"; 
       kqCardForm.action="/kq/options/manager/batchsendcard.do?b_save=link";       
       kqCardForm.submit();
}
function setOrder(obj_select)
    {
       var order_status=obj_select.options[obj_select.selectedIndex].value;
       var hashvo=new ParameterSet();
       hashvo.setValue("order_status",order_status);       
       if(order_status=="")
       {
         return false;
       }
       
       if(isArray($F('r_code')))
       {
         var forms= new Array();
         forms=$F('r_code');     
         hashvo.setValue("r_code",forms);
       }
       
       if(isArray($F('r_name')))
       {
         var forms= new Array();
         forms=$F('r_name');     
         hashvo.setValue("r_name",forms);
       } 
       
       var gnoSets = document.getElementsByName('r_gno');   
       var arrayGno = new Array();
       if(gnoSets != null){
     	  for(var n = 0;n < gnoSets.length;n++){
          	 arrayGno[n] = gnoSets[n].value;
           }
           hashvo.setValue("r_gno",arrayGno);
       }
       
       var request=new Request({method:'post',asynchronous:true,onSuccess:orderReSetCard,functionId:'15207000027'},hashvo);  
    }
  function orderReSetCard(outparamters)
  {
     var selected_emp=outparamters.getValue("selected_emp"); 
     var g_list=outparamters.getValue("gno_list");         
     if(selected_emp==null||selected_emp.length<=0)
       return false; 
     
     deleteALL(obj_tr)  
     for(var s=0;s<selected_emp.length;s++)
     {
       var table=document.getElementById('tbl');
       if(table==null)
  	  return false;
        var td_num=table.rows.length;
  	var rowCount=table.rows.length;	  
	var tRow = table.insertRow(rowCount);
	var r_name=selected_emp[s].dataName; 
	var r_code=selected_emp[s].dataValue;
	var r_gno=g_list[s];	
	var hidd_id="<input type='hidden' name='r_code' value="+r_code+">";
	hidd_id=hidd_id+"<input type='hidden' name='r_gno' value='"+r_gno+"'>";
	var hidd_name="<input type='hidden' name='r_name' value='"+r_name+"'>";
	var cell_0="<TD width='150' height='20'>&nbsp;"+r_name+""+hidd_id+"&nbsp;</TD>";
	var text_xl="BORDER-RIGHT: none;BORDER-TOP: none;BORDER-LEFT: none;BORDER-BOTTOM: none;BACKGROUND-COLOR: #ffffff;";
	var cell_1="<TD vAlign='center' align='right' width='50'>"+hidd_name+"&nbsp;</TD>";
	for (i=0;i<2;i++)
        { 
             var newCell=tRow.insertCell(i);
             
             switch (i) 
             { 
               case 0 : newCell.innerHTML=cell_0;newCell.width="150";newCell.height="25";break; 
               case 1 : newCell.innerHTML=cell_1; break;                
             } 
         }
     }
     init_event(); 
  }
--></script>

<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/options/manager/sendcard">
<div class="fixedDiv2" style="height: 100%;border: none">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable" >
		<thead>
			<tr>
				<td align="left" class="TableRow" nowrap>
					&nbsp;
					<bean:message key="kq.card.batch.send" />
					&nbsp;
					<html:hidden name="kqCardForm" property="a_code" styleClass="text" />
					<html:hidden name="kqCardForm" property="nbase" styleClass="text" />
					<html:hidden name="kqCardForm" property="kq_cardno"
						styleClass="text" />
					<html:hidden name="kqCardForm" property="kq_gno" styleClass="text" />
				</td>
			</tr>
		</thead>
		<tr>
			<td class="RecordRow">
				<table>
					<tr>
						<td colspan="2">
							<bean:message key="kq.card.no.allot" />
						</td>
						<td>
							<bean:message key="kq.card.emp.name" />
						</td>
					</tr>
					<tr>
						<td valign="top" height="280px;">
							<div id="tbl_containerll" class="div2 common_border_color" style="vertical-align: top;height: 280px;">
								<div id="treemenu" onclick="getemp();" ondblclick="getemp1();">
									<SCRIPT LANGUAGE=javascript>    
                                		<bean:write name="kqCardForm" property="treeCode" filter="false"/>
                              		</SCRIPT>
								</div>
							</div>
						</td>
						<td height="280px;">
							<table height="100%">
								<tr>
									<td height="20%">
									</td>
								</tr>
								<tr>
									<td height="15%">
										<input type="button" style="width: 30px;" name="b_addfield" value="&gt;&gt;"
											class="mybutton" onclick="batch_Emp();">
									</td>
								</tr>
								<tr>
									<td height="15%">
										<input type="button" style="width: 30px;" name="b_delfield"
											value="&gt;"
											class="mybutton" onclick="getemp1();">
									</td>
								</tr>
								<tr>
									<td height="15%">
										<input type="button" style="width: 30px;" name="b_delfield"
											value="&lt;"
											class="mybutton" onclick="deleteTR(obj_tr);">
									</td>
								</tr>
								<tr>
									<td height="15%">
										<input type="button" style="width: 30px;" name="b_delfield" value="&lt;&lt;"
											class="mybutton" onclick="deleteALL(obj_tr);">
									</td>
								</tr>
								<tr>
									<td height="20%">
									</td>
								</tr>
							</table>
						</td>
						<td height="280px;" valign="top">
							<table width="100%" cellSpacing=0 cellPadding=0 border=0>
								<tr>
									<td width="100%" align="left">
										<input type='hidden' name='r_code' value="">
										<input type='hidden' name='r_name' value="">
										<input type='hidden' name='r_gno' value="">
										<div id="tbl_container" class="div2 common_border_color">
											<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0
												id="tbl">
												<TBODY>

												</TBODY>
											</table>
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" height="30" class="RecordRow">
				<table width="96%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="20%">
							<bean:message key="kq.card.send.order" />&nbsp;
						</td>
						<td align="left" valign="middle">
							<hrms:optioncollection name="kqCardForm" property="order_list"
								collection="list" />
							<html:select name="kqCardForm" property="order_status" size="1"
								onchange="setOrder(this);">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
						</td>
						<td valign="middle">
							&nbsp;
							<html:multibox name="kqCardForm" property="order_flag" value="1" />
							&nbsp;
							<bean:message key="kq.card.first.status" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center"  nowrap style="height: 35px;border:none">
				<input type="button" name="btnreturn"
					value='<bean:message key="button.ok"/>' onclick="saveCard();"
					class="mybutton">
				
				<input type="button" name="b_next"
					value="<bean:message key="button.cancel"/>"
					onclick="window.close();" class="mybutton">
			</td>
		</tr>
	</table>
	</div>
</html:form>
<div id='wait'
	style='position: absolute; top: 200; left: 150; display: none;'>
	<table border="1" width="300" cellspacing="0" cellpadding="4"
		class="table_style" height="87" align="center">
		<tr>

			<td class="td_style common_background_color" height=24>
				<bean:message key="classdata.isnow.wiat" />
			</td>

		</tr>
		<tr>
			<td style="font-size: 12px; line-height: 200%" align=center>
				<marquee class="marquee_style" direction="right" width="300"
					scrollamount="5" scrolldelay="10">
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
<script language="javascript">
  init_event();
  window.focus();
   var waitInfo=eval("wait");	
  waitInfo.style.display="none";
</script>