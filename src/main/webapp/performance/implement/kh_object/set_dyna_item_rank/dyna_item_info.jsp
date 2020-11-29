<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.implement.ImplementForm,
				 com.hrms.struts.taglib.CommonData" %>
				 
<%
	ImplementForm implementForm = (ImplementForm)session.getAttribute("implementForm");				 
	ArrayList itemList = implementForm.getItemList();
	
%>		
<style>

.ListTable_self 
{
    BACKGROUND-COLOR: #FFFFFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none;     
}   

.RecordRow_self 
{
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
}

.trDeep_self 
{  
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	background-color: #DDEAFE; 
}

.Input_self
{                                                                    
  	font-size: 12px;                                              
  	font-weight: bold;                                                          
  	background-color: #FFFFFF;         
  	letter-spacing: 1px;                      
  	text-align: right;                        
  	height: 90%;                                    
  	width: 90%;                                    
  	border: 1px solid #94B6E6;           
  	cursor: hand;                                     
}   
	
</style>
<script type="text/javascript">
var item_id_editRule = "";
function editRule(item_id)
{
	item_id_editRule = item_id;
	var target_url="/performance/implement/kh_object/dynaitem.do?b_queryRule=link`item_id="+item_id;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
 	var width = 500;
 	if(window.showModalDialog){
		width=350;
	}
 	var config = {
		width:width,
	    height:260,
	    type:'2'
	}

	modalDialog.showModalDialogs(iframe_url,"",config,editRule_ok);
	//var return_vo= window.showModalDialog(iframe_url, "", 
	//              "dialogWidth:320px; dialogHeight:260px;resizable:no;center:yes;scroll:no;status:no");
}

function editRule_ok(return_vo) {
	if(return_vo!=null && return_vo.flag=="true") 	
	{
		var maxValueTitle = "<bean:message key='jx.implement.maxScore' />";
		var minValueTitle = "<bean:message key='jx.implement.minScore' />";
		if('${implementForm.templateStatus}'=='0')
		{
			 maxValueTitle = "<bean:message key='jx.implement.maxScore' />";
			 minValueTitle = "<bean:message key='jx.implement.minScore' />";
		}
		else if('${implementForm.templateStatus}'=='1')
		{
			 maxValueTitle = "<bean:message key='jx.implement.maxRank' />";
			 minValueTitle = "<bean:message key='jx.implement.minRank' />";
		}
	
		/*
		if(return_vo.minTaskCount+''+return_vo.maxTaskCount+''+return_vo.maxScore+''+return_vo.minScore+''+return_vo.scope+''+return_vo.to_scope!='')
		{
			var tt="<bean:message key='jx.implement.minTaskCount' />:"+ return_vo.minTaskCount+" <bean:message key='jx.implement.maxTaskCount' />:"+return_vo.maxTaskCount+" "+minValueTitle+":"+return_vo.minScore+" "+maxValueTitle+":"+return_vo.maxScore;
			if(return_vo.canshow=='true'&&return_vo.cflag=='1')
			{
				tt=tt+" 加扣分:"+return_vo.scope+"~"+return_vo.to_scope;
			}
			document.getElementById('href_'+item_id).innerText=tt;
		}else
			document.getElementById('href_'+item_id).innerText="无";
		*/	
			
		var tt="<bean:message key='jx.implement.minTaskCount' />:"+ return_vo.minTaskCount+" <bean:message key='jx.implement.maxTaskCount' />:"+return_vo.maxTaskCount+" "+minValueTitle+":"+return_vo.minScore+" "+maxValueTitle+":"+return_vo.maxScore;
		if(return_vo.canshow=='true' && return_vo.cflag=='1')
		{
			tt=tt+" 任务得分范围："+return_vo.scope+"~"+return_vo.to_scope;
			document.getElementById('href_'+item_id_editRule).innerText=tt;
		}
		else
		{
			if(return_vo.minTaskCount+''+return_vo.maxTaskCount+''+return_vo.maxScore+''+return_vo.minScore+''+return_vo.scope+''+return_vo.to_scope!='')
				document.getElementById('href_'+item_id_editRule).innerText=tt;
			else
				document.getElementById('href_'+item_id_editRule).innerText="无";
		}							
	}
}

function checkKeyCode()
{
    var code=window.event.keyCode;
    var ret=true;
    if(code==8||code==46||code==9||code==190||code==110||code==13)
    {
        if(code==13)
        window.event.keyCode=9;
    }
    else if(96<=code&&code<=105)
    {      
    }else if(48<=code&&code<=57)
    {
    }
    else
    { 
        if((window.event.shiftKey)&&(code==48||code==49||code==57||code==56||code==187))
        {
        }
        else if(window.event.shiftKey&&code==189)
        {
           window.event.returnValue=false;
        }
        else if(code==189||code==109)
        {
        }
        else
        {
           window.event.returnValue=false;
        }     
   }   
}
function IsDigit(obj) 
{
	if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
	{
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
			return false;
		if((event.keyCode == 46) && (values.length==0))//首位是.
			return false;	
		return true;
	}
	return false;	
}

var beforeValue;
var beforeitemid="-1";
function saveBeforeValue(obj)
{
  beforeValue=obj.value;
}
function checkValue(obj,a_status,item_id)
{
	var value = obj.value;
	var regu = "^[0-9]+\.?[0-9]*$";
	var re = new RegExp(regu);
	if(!re.test(value) || (parseFloat(value)>1 && a_status==1))
     {
          alert("权重值无效!");
          obj.value=beforeValue;
          return;
     }
    var hashvo=new ParameterSet();
	hashvo.setValue("planid",'${implementForm.planid}');
	hashvo.setValue("item_id",item_id);
	hashvo.setValue("objTypeId",'${implementForm.objTypeId}');
	hashvo.setValue("opt",'18');
	hashvo.setValue("theValue",obj.value);
	var request=new Request({method:'post',asynchronous:false,functionId:'9023000003'},hashvo);	
}
function changeColor(id)
{
     if(trim(beforeitemid).length>0&&document.getElementById(beforeitemid)!=null)
     {
          document.getElementById(beforeitemid).className='RecordRow';
     }
     beforeitemid=id;
     var e = document.getElementById(id);
     e.className=e.className+" selectedBackGroud";
}
function delItem()
{
	if(beforeitemid=='-1')
    {
       alert("请选择要删除的项目!");
       return;
    }
    if(confirm('确认删除所选项目吗？'))
    {
    	implementForm.action="/performance/implement/kh_object/dynaitem.do?b_del=link&item_id="+beforeitemid;
		implementForm.submit();
    }    
}
function addItem()
{
	<%if(itemList.size()==0){%>
		alert(KH_IMPLEMENT_INF11);
		return;
	<%}%>
	var target_url="/performance/implement/kh_object/dynaitem.do?br_add=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	var config = {
	    width:400,
        height:200,
        type:2
    }
	modalDialog.showModalDialogs(iframe_url,"",config,additem_ok);

}
function additem_ok(return_vo){
    if(return_vo!=null && return_vo.flag=="true")
    {
        var item_id = return_vo.item_id;
        implementForm.action="/performance/implement/kh_object/dynaitem.do?b_add=link&item_id="+item_id;
        implementForm.submit();
    }
}
</script>
<html:form action="/performance/implement/kh_object/dynaitem">
		<table width="98%" border="0" align="center">
			<tr>
				<td align="center">
					${implementForm.dynaItemHtml}
				</td>
			</tr>
			
		</table>  

<script type="text/javascript">
	var inputElmts = document.getElementsByTagName("input");
	for (var i = 0; i < inputElmts.length; i++) {
		inputElmts[i].className += " common_border_color";
	}
</script>
</html:form>
