
<%@page import="org.apache.commons.beanutils.DynaBean"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView,java.io.File"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>
<%

	String pajs=request.getSession().getServletContext().getRealPath("/js");
        if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
        {
        	try {
				pajs=request.getSession().getServletContext().getResource("/js").getPath();
			
           if(pajs.indexOf(':')!=-1)
       	  {
        	   pajs=pajs.substring(1);   
        	  }
       	  else
        	  {
       		pajs=pajs.substring(0);      
        	  }
           int nlen=pajs.length();
       	  StringBuffer buf=new StringBuffer();
        	  buf.append(pajs);
       	  buf.setLength(nlen-1);
       	pajs=buf.toString();
        	} catch (Exception e) {
				e.printStackTrace();
			}
        }
        pajs = pajs.replaceAll(File.separator+File.separator,"%5C");
 %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<hrms:themes></hrms:themes>
<script type="text/javascript" language="javascript">
function bak()
{
   busiMaintenceForm.action="/system/busimaintence/showbusiname.do?br_return=return";
   busiMaintenceForm.target="il_body";
   busiMaintenceForm.submit();
}
function RefreshD()
{
	jindu1();
   //刷新
    busiMaintenceForm.action="/system/options/param/sys_param.do?b_refresh=link&path=<%=pajs %>";
    busiMaintenceForm.submit();
}
function build(id)
{
     busiMaintenceForm.action="/system/busimaintence/constructcodeset.do?b_query=return&id="+id;
     busiMaintenceForm.submit();
}
//删除
function voider(obj){
	if(obj==""||obj==null){
		alert(KJG_YWZD_INFO22);
		return;
	}
	var theurl="/system/busimaintence/showbusiname.do?b_delete=link`obj="+obj;
	theurl = $URL.encode(theurl);
	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	var dw=400,dh=370,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	// var return_vo= window.showModalDialog(iframe_url, "",
	// 				"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	// if(return_vo!=null){
	// 	parent.frames["mil_menu"].location.reload();
	// }
    return_vo ='';
    var theUrl = iframe_url;
    Ext.create('Ext.window.Window', {
        id:'voider',
        height: 420,
        width: 500,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:Ext.getBody(),
        listeners: {
            'close': function () {
                if (return_vo) {
                    parent.frames["mil_menu"].location.reload();
                }
            }
        }

    });
}
function changeTrColor(id)
 {
    var ob=document.getElementById("tb");
    var j=ob.rows.length;
    for(var i=0;i<j-1;i++)
    {
         var o="a_"+i;
         var obj=document.getElementById(o);
         if(o==id)
         {
           if(o!=null)
           {
               obj.className="selectedBackGroud";
           }
         }
         else
         {
           if(i%2==0)
           {
              if(o!=null)
              {
                obj.className="trShallow";
                
              }
           }
           else
           {
               if(o!=null)
               {
                  obj.className="trDeep";
               }
           }
         }
    }
      
 }
 function jindu1(){
	//新加的，屏蔽整个页面不可操作
	document.all.ly.style.display="";   
	document.all.ly.style.width=document.body.clientWidth;   
	document.all.ly.style.height=document.body.clientHeight; 
	
	var x=(window.screen.width-700)/2;
    var y=(window.screen.height-500)/2; 
	var waitInfo=eval("wait1");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="";
}

</script>
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=10); opacity:0.1;background-color:#FFF;z-index:2;left:0px;display:none;"></div>
<div id='wait1' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在刷新数据字典,请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
<html:form action="/system/busimaintence/showbusiname">

<table width="80%" border="0" cellspacing="0" id="tb" align="center" cellpadding="0" class="ListTable">

			<tr>
					
					
					<td align="center" class="TableRow" nowrap>
					<bean:message key="kjg.title.ywmodule"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="kq.machine.description"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="kh.field.opt"/>
					</td>					
			</TR>
			<%int i=0;%>
		<hrms:paginationdb id="element" name="busiMaintenceForm" sql_str="busiMaintenceForm.sql" table="" where_str="busiMaintenceForm.where" columns="busiMaintenceForm.column" order_by="busiMaintenceForm.orderby" pagerows="100" page_id="pagination" indexes="indexes">	
			<%if(i%2==0){ %>
	     <tr class="trShallow" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
	     <%} else { %>
	     <tr class="trDeep" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
	     <%}%>
		<bean:define id="ids" name="element" property="id"/>
	
		<%
			DynaBean bean = (DynaBean)pageContext.getAttribute("element");
			String id = (String)bean.get("id");
		 %>
		<td class="RecordRow">
		&nbsp;<a href="/system/busimaintence/ShowSubsys.do?b_query=link&encryptParam=<%=PubFunc.encrypt("id="+id)%>">
		<bean:write name="element" property="name"/></a>
		</td>
		<td class="RecordRow">
		&nbsp;<bean:write name="element" property="description"/>
		</td>
		<td class="RecordRow" align="center">
		<hrms:priv func_id="3007301">
		<logic:equal name="busiMaintenceForm" property="userType"  value="1">
		   <a href="###" onclick="build('${ids}')"><bean:message key="kjg.title.design"/>
		  </a> 
		  <!--<bean:message key="kjg.title.design"/>-->
		</logic:equal>
		<logic:notEqual name="busiMaintenceForm" property="userType"  value="1">
			<logic:notEqual name="element" property="id"  value="35">
		  <!--<bean:message key="kjg.title.design"/>-->
		  </logic:notEqual>
		</logic:notEqual>
		<logic:notEqual name="busiMaintenceForm" property="userType"  value="1">
			<logic:equal name="element" property="id"  value="35">
		   		<a href="###" onclick="build('${ids}')"><bean:message key="kjg.title.design"/>
		  		</a> 
			</logic:equal>
		</logic:notEqual>
		</hrms:priv>
		&nbsp;&nbsp;
		<hrms:priv func_id="3007302">
		<a href="/system/busimaintence/editbusitablemake.do?b_query=link&encryptParam=<%=PubFunc.encrypt("returnvalue=sbn&id="+id)%>"><bean:message key="label.kh.edit"/></a>
		</hrms:priv>
		&nbsp;&nbsp; 
		<hrms:priv func_id="3007303">
		<logic:equal name="busiMaintenceForm" property="userType"  value="1">
		  <a href="javascript:voider('${ids}')" ><bean:message key="label.kh.del"/></a>
		</logic:equal>
		<logic:notEqual name="busiMaintenceForm" property="userType"  value="1">
			<logic:equal name="element" property="id"  value="35">
		  		<a href="javascript:voider('${ids}')" ><bean:message key="label.kh.del"/></a>
			</logic:equal>
		</logic:notEqual>
		<logic:notEqual name="busiMaintenceForm" property="userType"  value="1">
			<logic:notEqual name="element" property="id"  value="35">
		  <!--<bean:message key="label.kh.del"/>-->
		  </logic:notEqual>
		</logic:notEqual>
		</hrms:priv>
		</td>
		</tr>
		<% i++; %>
		</hrms:paginationdb>
	</table>
    <table align="center">
       <tr>
       <td height="35px;">
       <hrms:priv func_id="3007304">
    <input type="button"  value="<bean:message key="button.refresh"/>" class="mybutton" onclick="RefreshD();">
      </hrms:priv>
      </td>
      </tr>
    </table>
</html:form>
