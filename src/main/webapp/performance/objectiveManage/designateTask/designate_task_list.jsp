<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.objectiveManage.DesignateTaskForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<html>
<% 
  	DesignateTaskForm designateTaskForm=(DesignateTaskForm)session.getAttribute("designateTaskForm");
  	HashMap leafItemLinkMap = (HashMap)designateTaskForm.getLeafItemLinkMap();       // 叶子项目对应的继承关系
  	HashMap itemPointNum = (HashMap)designateTaskForm.getItemPointNum();       // 取得项目拥有的节点数
  	String lay = designateTaskForm.getLay();  // 项目层级
  	int layer = Integer.parseInt(lay);
  	HashMap existWriteItem = new HashMap();  // 放已画过的上级项目、上上级项目
  	
  	String returnURL=designateTaskForm.getReturnURL();
 	//【4889】自助服务：绩效考评/目标考核点击下达任务，然后点击返回按钮，页面报404 jingq add 2014.11.24
  	returnURL = PubFunc.keyWord_reback(returnURL);
  	if(returnURL!=null)
      	returnURL=returnURL.replaceAll("`","&");
    
    String itemdesc = "项目";   
	FieldItem fielditem = DataDictionary.getFieldItem("item_id");
	if(fielditem==null || fielditem.getItemdesc().trim().equalsIgnoreCase("项目号"))
		itemdesc = "项目";
	else
		itemdesc = fielditem.getItemdesc();	
	
	String colsNum = String.valueOf(layer+5);
	
	String className="TaskRecordRow";
%>
<style>

.TaskRecordRow {
	border-top-width: 0pt;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}
</style>
<SCRIPT LANGUAGE="javascript" src="/js/constant.js"></SCRIPT>
<SCRIPT LANGUAGE="javascript" src="/module/utils/js/template.js"></SCRIPT>
<SCRIPT LANGUAGE="javascript" src="/performance/objectiveManage/designateTask/designate.js"></SCRIPT>
<script type="text/javascript">
function returnBack()
{
  document.location="<%=returnURL%>";
}
</script>
<body>
<html:form action="/performance/objectiveManage/designateTask"> <br>
<html:hidden name="designateTaskForm" property="plan_id"/> 
<html:hidden name="designateTaskForm" property="objectid"/> 
<html:hidden name="designateTaskForm" property="returnURL"/> 
<input type="hidden" name="p0407" id="p7" value=""/> 
<input type="hidden" name="p0400" id="p4" value=""/>
<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="ListTable">
<tr>
  <td class="TableRow" align="center" width="15%" colspan=<%=lay %> ><%=itemdesc %></td>
  <td class="TableRow" align="center" width="38%" colspan="2" >${designateTaskForm.itemdesc}</td>
  <td class="TableRow" align="center" width="7%"><bean:message key="kh.field.opt"/></td>
  <td class="TableRow" align="center" width="20%">主办人</td>
  <td class="TableRow" align="center" width="20%">协办人</td>
</tr>
<logic:iterate id="element" name="designateTaskForm" property="kpiList" offset="0" indexId="index">
<tr height="30">

	<%
		LazyDynaBean bean = (LazyDynaBean)pageContext.getAttribute("element");
		String item_id = (String)bean.get("item_id");
		String itemName = (String)bean.get("p0407");
		ArrayList sublist = (ArrayList)bean.get("sublist");
		
		ArrayList linkParentList=(ArrayList)leafItemLinkMap.get(item_id); 
		int e=linkParentList.size()-1;
		int y=linkParentList.size();
		for(int n=0;n<layer;n++)
		{					
			if(e>=0)
			{
				bean=(LazyDynaBean)linkParentList.get(e);
				String itemId=(String)bean.get("item_id");						    	
				if(existWriteItem.get(itemId)!=null)
				{
					e--;
					y--;
					continue;
				}
				existWriteItem.put(itemId,"1");
				String itemDesc=(String)bean.get("itemdesc");
				//画出一个父项目	
				int rowspan=((itemPointNum.get(itemId)==null?0:((Integer)itemPointNum.get(itemId)).intValue()));					
				out.print(" <td align='left' class='RecordRow' rowspan='"+(rowspan)+"' nowrap>"+ itemDesc +"</td>");						    					    					    		
				e--;			    					    			
			}
			y--;
			if(y<0)
			{
				//  画空项目																	
				out.print(" <td align='left' class='RecordRow' rowspan='"+(sublist.size())+"' nowrap> </td>");										
			}
		}		
	%>			
 <td align="left" class="RecordRow" rowspan=<%=(sublist.size()==0?1:sublist.size()) %> width="220" ><bean:write name="element" property="p0407"/>&nbsp;<a href='javascript:newDesignate("<bean:write name="element" property="p0400"/>","<bean:write name="element" property="encodep0407"/>","<bean:write name="element" property="fromflag"/>","<bean:write name="element" property="p0401"/>");' ><img src="/images/pic_e.gif" alt="新增任务" BORDER="0" style="cursor:hand;" /></a></td>
 <% if(sublist.size()<=0){
 	className="RecordRow";
 	out.print("<td align='center' class='RecordRow' width='220' >&nbsp;</td>");    
 	out.print("<td align='center' class='RecordRow' width='220' >&nbsp;</td>");    
 	out.print("<td align='left' class='RecordRow' width='220' >&nbsp;</td>");    
 	out.print("<td align='left' class='RecordRow' width='220' >&nbsp;</td>");    
 	out.print("</tr>");    
 %>
 <% }else{
 	className="TaskRecordRow RecordRow";
 	%>
 <% } %>
 
 <logic:iterate id="subList" name="element" property="sublist" offset="0" indexId="idx">
  <% 
      if(idx != 0) {
  %>
  <tr height="30">
  <%
      }
  %>
    <td align="left" class="<%=className %>" width="220" ><bean:write name="subList" property="p0407"/></td>
    <td align="center" class="<%=className %>" nowrap>
    <logic:equal value="1" name="element" property="fromflag">
    <a href='javascript:editDesignnate("<bean:write name="subList" property="task_id"/>","2");'>编辑</a>&nbsp;/&nbsp;
    </logic:equal>
    <a href='javascript:deleteTask("<bean:write name="subList" property="task_type"/>","<bean:write name="subList" property="task_id"/>","<bean:write name="subList" property="p0400"/>","<bean:write name="subList" property="group_id"/>");'>删除</a>
    </td>
    <td align="center" class="<%=className %>" nowrap>
    <table><tr><td align="left" width="95%">
    
    <% int zaNum = 0;%>
    <table>
    <logic:iterate id="zba0101" name="subList" property="za0101" offset="0" indexId="zindx">
    <% if(zaNum==0 || zaNum%2==0){%>
    <tr>
    <% }%>
    <td align="left">
    &nbsp;<bean:write name="zba0101" property="a0101"/>
    <logic:equal value="1" name="zba0101" property="canDel">
      <a href="javascript:deleteSinglePepole('<bean:write name="zba0101" property="task_id"/>','<bean:write name="zba0101" property="p0400"/>','<bean:write name="zba0101" property="group_id"/>','<bean:write name="zba0101" property="a0101"/>');">[收回]</a>
    </logic:equal>
    </td>
    <% if(zaNum%2!=0){%>
    </tr>
    <% }
       zaNum++;
    %>
    </logic:iterate>    
	</table>

    </td>
    <td width="5%" align="right">
    <img src="/images/appoint_task.gif" border="0" style="cursor:hand" onclick="newDesignateTT('1','<bean:write name="element" property="p0400"/>','<bean:write name="element" property="fromflag"/>','<bean:write name="element" property="p0401"/>','<bean:write name="subList" property="encodep0407"/>','<bean:write name="subList" property="task_type"/>','<bean:write name="subList" property="task_id"/>','<bean:write name="subList" property="group_id"/>');"/></td></tr></table></td>
    <td align="center" class="<%=className %>" nowrap>
    <table><tr><td align="left" width="95%">
    
    <% int xbNum = 0;%>
    <table>
    <logic:iterate id="xba0101" name="subList" property="xa0101" offset="0" indexId="xindx">
    <% if(xbNum==0 || xbNum%2==0){%>
    <tr>
    <% }%>
    <td align="left">
    &nbsp;<bean:write name="xba0101" property="a0101"/>
    <logic:equal value="1" name="xba0101" property="canDel">
      <a href="javascript:deleteSinglePepole('<bean:write name="xba0101" property="task_id"/>','<bean:write name="xba0101" property="p0400"/>','<bean:write name="xba0101" property="group_id"/>','<bean:write name="xba0101" property="a0101"/>');">[收回]</a>
    </logic:equal>
    </td>
    <% if(xbNum%2!=0){%>
    </tr>
    <% }
       xbNum++;
    %>
    </logic:iterate>    
	</table>
	
    </td><td width="5%" align="right">
    <img src="/images/appoint_task.gif" style="cursor:hand" border="0" onclick="newDesignate2('2','<bean:write name="element" property="p0400"/>','<bean:write name="subList" property="encodep0407"/>','<bean:write name="subList" property="task_type"/>','<bean:write name="subList" property="task_id"/>','<bean:write name="subList" property="group_id"/>');"/></td></tr></table></td>
  </tr>
 </logic:iterate>
</logic:iterate>
<tr>
  <td colspan=<%=colsNum %> align=left class="RecordRow">
    <input type="checkbox" name="fp"  value="1" id="qz"/>强制分配任务　　说明：勾选后分配的任务不允许修改和删除
  </td>
</tr>
<script type="text/javascript">
	document.getElementById("qz").checked=true;
</script>
<tr>
  <td colspan=<%=colsNum %> align=left style="padding-top:7px;">
    <input type="button" name="re" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnBack();"/>
  </td>
</tr>
</table>
</html:form>
</body>
</html>