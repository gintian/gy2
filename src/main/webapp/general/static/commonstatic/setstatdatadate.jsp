<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%@ page import="com.hjsj.hrms.actionform.stat.StatForm" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.hrms.struts.taglib.CommonData" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.hrms.frame.dao.utility.DateUtils" %>
<% 
    StatForm statForm = (StatForm) session.getAttribute("statForm"); 
    ArrayList valuelist=statForm.getList();
    String severDate=DateUtils.format(new Date(),"yyyy.MM.dd");
    String severTime=DateUtils.format(new Date(),"HH:mm:ss");
    String year=DateUtils.getYear(new Date())+"";
    String month=DateUtils.getMonth(new Date())+"";
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 100%;height: 230px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
}
</STYLE>
<SCRIPT LANGUAGE=javascript>
function IsDigit() 
{ 
  return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
} 
function IsInputValue(textid,maxvalue) {	
		event.cancelBubble = true;
		var fObj=document.getElementById(textid);		
		if(fObj.disabled==true)
		  return false;		
		if (!fObj) return;
		if(fObj.value=="")
		  fObj.value="1";		
		var cmd = event.srcElement.innerText=="5"?true:false;
		var i = parseInt(fObj.value,10);
		var radix = maxvalue;
		if(radix<=0)
		  radix=1;		
		if (i==radix&&cmd) {
			i = 1;
		} else if (i==1&&!cmd) {
			i = radix;
		} else {
			cmd?i++:i--;
		}		
		fObj.value = i;
		fObj.select();
} 
function statsave()
{     
           var hashvo=new ParameterSet();	
           var currnode=Global.selectedItem;
           var userid = currnode.uid;            
           if(userid==""||userid=="root")
           {
             alert("请选择组织机构！");
             return false;
           }
    	   hashvo.setValue("statid","${statForm.statid}");
    	   var archive_type="${statForm.archive_type}";
    	   hashvo.setValue("archive_type",archive_type);
    	   var list=new Array();
           <%
              if(!valuelist.isEmpty())
              {
                 for(int i=0;i<valuelist.size();i++)
                 {
                     CommonData vo=(CommonData)valuelist.get(i);
                     out.println("var vo=new Object();");
                     out.println(" vo.name=\""+vo.getDataName()+"\";");
                     int index=vo.getDataValue().indexOf(".");
                     if(index!=-1){
                     	out.println(" vo.value=\""+vo.getDataValue().substring(0,index)+"\"");
                     }else{
                     	out.println(" vo.value=\""+vo.getDataValue()+"\"");
                     }
                     out.println(" list["+i+"]=vo;");
                     
                 }
              }
           %>
           var year= document.getElementById("year").value;
           hashvo.setValue("year",year);
           var month="";          
           if(archive_type=="1")
           {
              month= document.getElementById("month").value;
           }else
           {
           if(document.getElementById("month") != null){
           	
           
             var vo= document.getElementById("month");             
             for(var i=0;i<vo.options.length;i++)
             {
               if(vo.options[i].selected)
               {
    	          month=vo.options[i].value;
               }
             }
           }  
           }  
           if(month == ""){
           		month = 1;
           }      
           hashvo.setValue("userid",userid);
           hashvo.setValue("month",month);
           hashvo.setValue("list",list);
           var request=new Request({method:'post',asynchronous:false,onSuccess:saveReturn,functionId:'11080204124'},hashvo);
}
        function saveReturn(outparamters)
        {
	        var flag=outparamters.getValue("flag");
	        if(flag==0)
	          alert("操作失败！");
	        else
	        {
	           alert("归档成功！");
	           parent.window.close();
	        }
        
        }
   </SCRIPT>
   <hrms:themes/>
   <html:form action="/general/static/commonstatic/statshow">
    <table width="100%" border="0" cellspacing="1" class="RecordRow" align="center" cellpadding="1" >
        <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="2">
		      选择历史归档时间&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>    	  
   	   <tr>
   	       <td align="left" width="35%" height="30"  nowrap>
     	    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;归档周期类型
    	    </td>
            <td align="left"  nowrap> 按
              <logic:equal name="statForm" property="archive_type" value="1">
                 月报
              </logic:equal>
               <logic:equal name="statForm" property="archive_type" value="2">
                 季报
              </logic:equal>
               <logic:equal name="statForm" property="archive_type" value="3">
                 半年报
              </logic:equal>
              <logic:equal name="statForm" property="archive_type" value="4">
                 年报
              </logic:equal>
    	    </td> 
       </tr> 
     
        <tr>
          <td align="left" height="30" nowrap>
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;选择归档期间
          </td>
          <td align="left"  nowrap>          
                <logic:equal name="statForm" property="archive_type" value="1">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>                      
                      <td valign="middle" width="30"> 
                         <input type="text" name="year" class="TEXT4" size="4" onkeypress="event.returnValue=IsDigit();" id="year" value="<%=year%>">
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="1" cellpadding="0">
                          <tr><td><button id="1_up" type="button" class="m_arrow" onmouseup="IsInputValue('year',9999);">5</button></td></tr>
                          <tr><td><button id="1_down" type="button" class="m_arrow" onmouseup="IsInputValue('year',9999);">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left">
                          年&nbsp;&nbsp;
                      </td>
                      <td valign="middle" width="30"> 
                         <input type="text" name="month" class="TEXT4" size="4" onkeypress="event.returnValue=IsDigit();" id="month" value="<%=month%>">
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="1" cellpadding="0">
                          <tr><td><button id="1_up" type="button" class="m_arrow" onmouseup="IsInputValue('month',12);">5</button></td></tr>
                          <tr><td><button id="1_down" type="button" class="m_arrow" onmouseup="IsInputValue('month',12);">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left">
                         月
                      </td>
                    </tr>
                  </table>
                </logic:equal>
                <logic:equal name="statForm" property="archive_type" value="2">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>                      
                      <td valign="middle" width="30"> 
                         <input type="text" name="year" class="TEXT4" size="4" onkeypress="event.returnValue=IsDigit();" id="year" value="<%=year%>">&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="1" cellpadding="0">
                          <tr><td><button id="1_up" type="button" class="m_arrow" onmouseup="IsInputValue('year',9999);">5</button></td></tr>
                          <tr><td><button id="1_down" type="button" class="m_arrow" onmouseup="IsInputValue('year',9999);">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left">
                          年&nbsp;&nbsp;
                      </td>
                      <td valign="middle" width="100"> 
                             <select name="month" size="1">
                             <option value="1" selected="selected">第一季</option>
                             <option value="2">第二季</option>
                             <option value="3">第三季</option>
                             <option value="4">第四季</option>
                             </select>               
                      </td>                      
                      <td align="left">
                      </td>
                    </tr>
                  </table>
                </logic:equal>
                <logic:equal name="statForm" property="archive_type" value="3">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>                      
                      <td valign="middle" width="30"> 
                         <input type="text" name="year" class="TEXT4" size="4" onkeypress="event.returnValue=IsDigit();" id="year" value="<%=year%>">&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="1" cellpadding="0">
                          <tr><td><button id="1_up" type="button" class="m_arrow" onmouseup="IsInputValue('year',9999);">5</button></td></tr>
                          <tr><td><button id="1_down" type="button" class="m_arrow" onmouseup="IsInputValue('year',9999);">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left">
                          年&nbsp;&nbsp;
                      </td>
                      <td valign="middle" width="100"> 
                             <select name="month" size="1">
                             <option value="1" selected="selected">上半年</option>
                             <option value="2">下半年</option>
                             </select>               
                      </td>  
                    </tr>
                  </table>
                </logic:equal>
                <logic:equal name="statForm" property="archive_type" value="4">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>                      
                      <td valign="middle" width="30"> 
                         <input type="text" name="year" class="TEXT4" size="4" onkeypress="event.returnValue=IsDigit();" id="year" value="<%=year%>" class="textColorWrite">&nbsp;
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="1" cellpadding="0">
                          <tr><td><button id="1_up" type="button" class="m_arrow" onmouseup="IsInputValue('year',9999);">5</button></td></tr>
                          <tr><td><button id="1_down" type="button" class="m_arrow" onmouseup="IsInputValue('year',9999);">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left">
                          年&nbsp;&nbsp;
                      </td>
                      <td valign="middle"> 
                              &nbsp;          
                      </td>  
                    </tr>
                  </table>
                </logic:equal>
          </td>
       </tr>
        <tr>
           <td align="left" colspan="2"> 
             <div id="tbl_container"  class="div2 complex_border_color" >
                 <div id="treemenu"> 
                  <SCRIPT LANGUAGE=javascript>    
                   Global.defaultInput=2;
                   Global.showroot=false;
                   <bean:write name="statForm" property="treeCode" filter="false"/>
                  </SCRIPT>
                 </div> 
             </div>         
           </td>
        </tr>   
       <tr>
            <td align="center" height="5" nowrap colspan="2"></td>            	        	        	        
           </tr>
    </table>
    <table align="center">
        <tr>
            <td align="center" height="5" nowrap colspan="2">
		  
            </td>            	        	        	        
           </tr>
        <tr>
          <td align="center"  nowrap colspan="2">
               <input type="button" name="btncance2" value="<bean:message key="button.ok"/>" class="mybutton" onclick="statsave();"> 
               <input type="button" name="btncance2" value="<bean:message key="button.close"/>" class="mybutton" onclick="parent.window.close();"> 
          </td>
       </tr>
    </table>
    
   </html:form>
<script>
if(!getBrowseVersion() || getBrowseVersion()==10){
	var tblDiv = document.getElementById('tbl_container');
	tblDiv.style.width='99.4%';
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
	if(isFF){//火狐浏览器 样式兼容  wangb 201807102 38753
		var tds = document.getElementsByClassName('m_arrow');
		for(var i =0 ; i < tds.length ; i++){
			var tid = tds[i].getAttribute('id');
			if(!(tid == '1_up' || tid == '1_down'))
				continue;
			if(tid == '1_up')
				tds[i].innerHTML='▲';
			
			if(tid == '1_down')
				tds[i].innerHTML='▼';
				
			tds[i].style.fontSize='7';
			tds[i].style.height ='10px';
		}
	}
	var year = document.getElementsByName('year')[0];
	year.parentNode.style.lineHeight='0px';
}
var index = 0;
var c = setInterval(function(){
 var treemenu = document.getElementById('treemenu');
 var as = treemenu.getElementsByTagName('a');
 for(var i = 0 ; i < as.length ; i++){
  as[i].setAttribute('href','javascript:void(0)');
 }
 if(index >600)
 	clearInterval(c);
 index++;
},1000);
</script>