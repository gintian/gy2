<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
<!--
.ListTable_self 
{
    BACKGROUND-COLOR: #FFFFFF;
    BORDER-BOTTOM: 1px ; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: 1px; 
    BORDER-RIGHT:1px; 
    BORDER-TOP: 1px; 
    
} 
.TableRowTRank 
{
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.Input_self
{                                                                    
  	font-size:   12px;                                              
  	font-weight:   bold;                                                          
  	background-color:   #FFFFFF;         
  	letter-spacing:   1px;                      
  	text-align:   right;                        
                                    
  	width:   90%;                                    
  	border:   1px   solid   #94B6E6;           
  	cursor:   hand;  
                                
}     
.div2
{
 	overflow:auto; 
 	width: 470px;height: 250px;
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
-->
</style>
<script type="text/javascript">
<!--
var beforvalue=0;
function checkKeyCode()
{
   var code=window.event.keyCode;
    var ret=true;
    if(code==8||code==46||code==9||code==190||code==110||code==13||(code==229))
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
        else
        {
           window.event.returnValue=false;
        }
     
   }
   //onBlur:当失去输入焦点后产生该事件
   //onFocus:当输入获得焦点后，产生该文件
   
}
function checkCode(index)
{
    var code=window.event.keyCode;
    beforvalue=document.getElementById(index+"_rank").value;
    var ret=true;
    if(code==8||code==46||code==9||code==190||code==110||code==13||(code==229))
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
        else
        {
           window.event.returnValue=false;
        }
     
   }
}
function checkValue(index)
{
   var val=document.getElementById(index+"_rank").value;
   if(trim(val)!=''&&parseInt(val)>100)
   {
     document.getElementById(index+"_rank").value=beforvalue;
   }
}
function save(index)
{
    var myReg =/^(-?\d+)(\.\d+)?$/
    for(var i=0;i<index;i++)
    {
         var score=document.getElementById(i+"_score");
         var rank=document.getElementById(i+"_rank");
         if(!myReg.test(score.value)) 
         {
            alert("第"+(i+1)+"行分值请输入数字！");
            return;
         }
         if(!myReg.test(rank.value)) 
         {
            alert("第"+(i+1)+"行项目权重请输入数字！");
            return;
         }
    }
   khTemplateForm.action="/performance/kh_system/kh_template/init_kh_item.do?b_save=link&isrefresh=2";
   khTemplateForm.submit();
    closeWindow();
}
function closeWindow()
{
   <%if(request.getParameter("isrefresh")!=null&&request.getParameter("isrefresh").equals("2")){%>
    parent.returnValue="1";
    <%}else{%>
    parent.returnValue=null;
    <%}%>
    if(window.showModalDialog) {
        parent.window.close();
    }else{
        parent.parent.window.template_item_rank_ok(parent.returnValue);
    }
}
//-->
</script>
<html:form action="/performance/kh_system/kh_template/init_kh_item">
<table width="90%" class="ListTable" align="center">
<tr>
<td align="left">
<strong>${khTemplateForm.infos}</strong>
</td>
</tr>
<tr>
<td>
<div class='div2 common_border_color ' >
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr height="20">
<td align="center" class="TableRowTRank common_background_color common_border_color" style="border-left:0px;">
<bean:message key="lable.kh.khitem"/>
</td>
<td align="center" class='TableRowTRank common_background_color common_border_color'>
<bean:message key="kh.field.scorevalue" />
</td>
<td align="center" class="TableRowTRank common_background_color common_border_color" style="border-right:0;">
<bean:message key="lable.kh.itemrank"/>(%)
</td>
</tr>
<%int i=0; %>
<logic:iterate id="element" name="khTemplateForm" property="itemList" indexId="index">
<tr>
<td class="RecordRow" align="left" style="border-left:0;">
&nbsp;<bean:write name="element" property="itemdesc"/>
</td>
<td class="RecordRow" align="right">
<input type="text" class="Input_self common_border_color" id="<%=index+"_score"%>" name="<%="itemList["+index+"].score"%>"  onkeydown="checkKeyCode();" value="<bean:write name="element" property="score"/>" maxlength='10'/>
</td>
<td class="RecordRow" align="right" style="border-right:0;">
<input type="text" class="Input_self common_border_color" id="<%=index+"_rank"%>" name="<%="itemList["+index+"].rank"%>"  onkeyup="checkValue('<%=index%>');" onkeydown="checkCode('<%=index%>');" value="<bean:write name="element" property="rank"/>" maxlength='10'/>
</td>
</tr>
<%i++; %>
</logic:iterate>
</table>
</div>
</td>
</tr>
<tr>
<td align='center'>
<input type="button" onclick='save("<%=i%>");' value="<bean:message key="button.ok"/>" class="mybutton"/>&nbsp;<input type="button" onclick="closeWindow();" class="mybutton" value="<bean:message key="button.close"/>"/>
</td>
</tr>
</table>
</html:form>