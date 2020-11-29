<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function hiddenText(obj)
{
  var trE=document.getElementById("t");
  if(obj.checked&&obj.value=='0')
  {
     trE.style.display="none";
  }
  else
  {
     trE.style.display="block";
  }
}
function returnBack()
{
   historyDataForm.action="/gz/gz_analyse/historydata/salary_set_list.do?b_query=query&gz_module=<%=request.getParameter("gz_module")%>";
   historyDataForm.submit();
}
function sub()
{

    var obj = document.getElementsByName("type");
    var type="";
    if(obj)
    {
      for(var i=0;i<obj.length;i++)
      {
         if(obj[i].checked)
            type=obj[i].value;
      }
    }
    var sd=historyDataForm.startDate.value;
    var ed =historyDataForm.endDate.value;
    if(type=='1'&&sd=='')
    {
       alert("请填写起始时间!");
       return;
    }
     if(type=='1'&&ed=='')
    {
       alert("请填写结束时间!");
       return;
    }
    if(type=='1')
    {
        if(trim(sd)!=''&&trim(ed)!='')
	   {
	      var xx=sd.split("-");
	      var yy=ed.split("-");
	      var xxD=new Date(xx[0]*1,xx[1]*1,xx[2]*1);
	      var yyD=new Date(yy[0]*1,yy[1]*1,yy[2]*1);
	      if(xx[0]*1>yy[0]*1||(xx[0]*1==yy[0]*1&&xx[1]*1>yy[1]*1)||(xx[0]*1==yy[0]*1&&xx[1]*1==yy[1]*1&&xx[2]*1>yy[2]*1))
	      {
	         alert("起始时间不能大于结束时间！");
	         return;
	      }
	   }
    }
    var ot=document.getElementById("ot").value;
    if(ot=='0'||ot=='2')
    {
      var desc="确定进行归档操作?"; 
      if(ot=='2')
      	 desc="确定进行删除操作?";
      if(confirm(desc))
      {
        jinduo(1);
        var hashvo=new ParameterSet();
        hashvo.setValue("ot",ot);
		hashvo.setValue("type",type);
		hashvo.setValue("startDate",sd); 
		hashvo.setValue("endDate",ed);
		hashvo.setValue("id",historyDataForm.salaryid.value); 
		var request=new Request({method:'post',asynchronous:true,onSuccess:configOk,functionId:'3020130025'},hashvo);
   
       } 
     }
     else
     {
        var hashvo=new ParameterSet();
		hashvo.setValue("ot",ot); 
		hashvo.setValue("type",type);
		hashvo.setValue("startDate",sd); 
		hashvo.setValue("endDate",ed);
		hashvo.setValue("id",historyDataForm.salaryid.value); 
		var request=new Request({method:'post',asynchronous:true,onSuccess:isChange,functionId:'30200710248'},hashvo);
         
     } 
}
function download(fileName)
{
	var fieldName = getDecodeStr(fileName);
	var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
}
function configOk(outparameter)
{
 closejinduo(1);
 var fileName = outparameter.getValue("fileName");
 if(fileName){
 	 download(fileName);
 }else{
 	 returnBack();
 }
}
function isChange(outparameters)
{
   var msg=outparameters.getValue("msg");
   if(msg=='1')
   {
    if(!confirm("薪资历史数据表，结构已经发生变化，进行还原操作，将会丢失部分数据，确定进行还原吗？"))
    {
      return;
      
    }
   }
   else
   {
      if(!confirm("确定进行还原操作？")){
         return;
        }
   }
   jinduo(1);
        var hashvo=new ParameterSet();
        hashvo.setValue("ot",outparameters.getValue("ot"));
		hashvo.setValue("type",outparameters.getValue("type"));
		hashvo.setValue("startDate",outparameters.getValue("startDate")); 
		hashvo.setValue("endDate",outparameters.getValue("endDate"));
		hashvo.setValue("id",outparameters.getValue("id")); 
		var request=new Request({method:'post',asynchronous:true,onSuccess:configOk,functionId:'3020130025'},hashvo);

   
}
function closejinduo(type){
	   var waitInfo;
	   waitInfo=eval("wait");
	   waitInfo.style.display="none";
     }
function jinduo(type){
 	var x=document.body.scrollLeft+event.clientX;
    var y=document.body.scrollTop+event.clientY-100; 
	var waitInfo;
	waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}
function CtrlKeyMethod(obj)
{
   obj.value="";
}
//-->
</script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
<html:form action="/gz/gz_analyse/historydata/salary_set_list">


<table width="700px;" align="center" style="margin-top:60px;">
<tr>
<td width="100%" align="center">
<fieldset align="center">
<%if(request.getParameter("opttype")!=null&&request.getParameter("opttype").equals("0")){ %>
<legend>历史数据归档</legend>
<%}
else if(request.getParameter("opttype")!=null&&request.getParameter("opttype").equals("1")){%>
<legend>归档数据还原</legend>
<%} else if(request.getParameter("opttype")!=null&&request.getParameter("opttype").equals("2")){%>
<legend>删除归档数据</legend>
<%} %>
<table width="100%">
<tr>
<td align="left">
 <input type="radio" name="type" value="0" onclick="hiddenText(this);" checked/>全部
 </td>
</tr>
<tr>
<td align="left">
 <input type="radio" name="type" onclick="hiddenText(this);" value="1"/>时间范围
 </td>
</tr>
<tr id="t" style="display:none">
<td align="left">
<bean:message key="kq.rule.from"/><input type="text" class='complex_border_color' size="20" name="startDate"     onclick="popUpCalendar(this,this, dateFormat,'','',true,false)" />
                
<bean:message key="kq.shift.cycle.dateto"/><input  type="text"  size="20" name="endDate"  class='complex_border_color'  onclick="popUpCalendar(this,this, dateFormat,'','',true,false)""  />
                                           
</td>
</tr>
</table>
</fieldset>
</td>
</tr>
<tr>
<input type="hidden" name="salaryid" value="<%=request.getParameter("id")%>"/>
<input type="hidden" name="optType" id="ot" value="<%=request.getParameter("opttype")%>"/>
<td width="100%" align="center">
<input type="button" name="ok" class="mybutton" value="<bean:message key="button.ok"/>" onclick="sub();"/>
<input type="button" name="cancel" class="mybutton" value="<bean:message key="button.return"/>" onclick="returnBack();"/>
</td>
</tr>
</table>
<div   id="wait" style='position:absolute;top:285;left:120;display:none;width:285px;heigth:120px'>
 
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
			
				<td class="td_style" height=24>
				<%if(request.getParameter("opttype")!=null&&request.getParameter("opttype").equals("0")){ %>
					正在进行数据归档，请稍候...
					<%}
					else if(request.getParameter("opttype")!=null&&request.getParameter("opttype").equals("1")){%>
					正在进行数据还原，请稍候...
					<%} 
					else if(request.getParameter("opttype")!=null&&request.getParameter("opttype").equals("2")){%>
					正在删除归档数据，请稍候...
					<%} %>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
		    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
		    </iframe>	
	</div>
</html:form>