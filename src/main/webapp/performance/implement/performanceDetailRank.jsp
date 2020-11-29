<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 java.text.DecimalFormat,
				 com.hjsj.hrms.actionform.performance.implement.ImplementForm,
				 org.apache.commons.beanutils.LazyDynaBean,	
				 com.hjsj.hrms.utils.PubFunc,		 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.hjsj.sys.Des" %>

<%  
		ImplementForm implementForm=(ImplementForm)session.getAttribute("implementForm");
		String object_type=(String)implementForm.getObject_type();	  // 1:部门  2:人员
		String plan_id =(String)implementForm.getPlanid(); // 考核计划号
		String plan_idXXX = PubFunc.encrypt(plan_id);
	    Des des=new Des();
 	    plan_id=des.EncryPwdStr(plan_id);
		plan_id = PubFunc.convertTo64Base(plan_id);
		
		ArrayList objectidTypeList = (ArrayList)implementForm.getObjectidTypeList();  // 取得计划对应的对象类别列表
		ArrayList mainbodyTypeList = (ArrayList)implementForm.getMainbodyTypeList();  // 取得主体类别列表
		HashMap mainbodyMap = (HashMap)implementForm.getMainbodyMap();       // 取得范围内考核对象对应的考核主体
		HashMap mainbodyDefaultRankMap = (HashMap)implementForm.getMainbodyDefaultRankMap(); // 取得设置的主体默认权重
		HashMap mainbodyRankMap = (HashMap)implementForm.getMainbodyRankMap();   // 取得设置的动态主体权重
		
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		
		DecimalFormat myformat1 = new DecimalFormat("##########.#####");
		
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>

<style>

div#tbl-container 
{	
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}

.TEXT_NB 
{
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	
}

</style>

<script type="text/javascript">

var IVersion=getBrowseVersion();

if(IVersion==8)
{
  	document.writeln("<link href=\"/performance/evaluation/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
        document.writeln("<link href=\"/css/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}

</script>

<script type="text/javascript">

// 返回考核实施主页面
function backImplementPage()
{
//  implementForm.action="/performance/markStatus/markStatusList.do?b_search=link";
//	implementForm.submit();
	
	var url = "/performance/implement/performanceImplement.do?b_int=link&plan_id=<%=plan_idXXX %>";					
	window.location=url;
}

</script>
<hrms:themes></hrms:themes>
<html:form action="/performance/markStatus/markStatusList">
	
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:-1px;margin-bottom:8px;">		
		
		<tr><td align="center"> 
			<strong><font size='5'><bean:message key="jx.implement.evaluateRelationSetTable"/></font></strong>  				
		</td></tr>
		
	</table>		
		
	<script language='javascript' >
		document.write("<div id=\"tbl-container\"  style='position:absolute;left:5;height:"+(document.body.clientHeight-124)+";width:99%' >");
	</script>

		<%  // 表头信息  %>
		${implementForm.detailHeadHtml}
	
		
		<%
			int n=0;
			// 表体信息	
			if(objectidTypeList.size()>0 && (mainbodyMap!=null && mainbodyMap.size()>0))
			{	    		    		    		    		    		    
			    for (int i = 0; i < mainbodyTypeList.size(); i++)
			    {
			    	LazyDynaBean abean = (LazyDynaBean) mainbodyTypeList.get(i);
					String body_id = (String) abean.get("body_id");
					String number = (String) abean.get("number");  // 序号
					String name = (String) abean.get("name");     // 考核主体类别
					
					if(n%2==0)
				    {  
				    	out.println("<tr class='trShallow'>");   
				    }else{
				    	out.println("<tr class='trDeep'>");
					}
					if(i == mainbodyTypeList.size()-1){
						out.println("<td align='center' class='cell_locked2 common_background_color common_border_color' nowrap>&nbsp;");
				 		out.print(number);
				  		out.print("</td>");
				  		out.println("<td align='center'  class='cell_locked2 common_background_color common_border_color' nowrap>&nbsp;");
				 		out.print(name);
				  		out.print("</td>");
					}else{
						out.println("<td align='center' class='cell_locked2 common_background_color common_border_color' nowrap>&nbsp;");
				 		out.print(number);
				  		out.print("</td>");
				  		out.println("<td align='center' class='cell_locked2 common_background_color common_border_color' nowrap>&nbsp;");
				 		out.print(name);
				  		out.print("</td>");
					}
					
					
					float defaultBodyRank = ((Float) mainbodyDefaultRankMap.get(body_id)).floatValue(); 														
					
					LazyDynaBean zbean=null;
					for(int j=0;j<objectidTypeList.size();j++)
					{
						float bodyRank = defaultBodyRank;
						String mainbodyRank = "";
						
						zbean=(LazyDynaBean)objectidTypeList.get(j);
						String groupCom=(String)zbean.get("groupCom");
						String b0110=(String)zbean.get("b0110");
						String e0122=(String)zbean.get("e0122");
						String e01a1=(String)zbean.get("e01a1");						
						String object_id=(String)zbean.get("object_id");
						String a0101=(String)zbean.get("a0101");	
						String bodyId=(String)zbean.get("body_id");	
											
						String mainbodyName = (String) mainbodyMap.get(object_id + ":" + body_id); // 考核主体名单	
						String mainbodyNum = "";
						if(mainbodyName!=null && mainbodyName.trim().length()>0)
						{
							String[] items = mainbodyName.split(",");
							mainbodyNum = String.valueOf(items.length);   // 考核主体人数	
						}else
						{
							mainbodyName = "";
						}									
						
						Float rank = (Float) mainbodyRankMap.get(object_id + ":" + body_id);											
						// 岗位
						if(rank==null || rank.toString().trim().length()<=0)						
							rank = (Float) mainbodyRankMap.get(e01a1 + ":" + body_id);							
						// 部门
						if(rank==null || rank.toString().trim().length()<=0)						
							rank = (Float) mainbodyRankMap.get(e0122 + ":" + body_id);
						// 单位
						if(rank==null || rank.toString().trim().length()<=0)						
							rank = (Float) mainbodyRankMap.get(b0110 + ":" + body_id);
						// 某集团公司
						if(rank==null || rank.toString().trim().length()<=0)						
							rank = (Float) mainbodyRankMap.get(groupCom + ":" + body_id);	
						// 对象类别
						if(rank==null || rank.toString().trim().length()<=0)//评价关系及权重界面没有考虑对象类别  chent 20160113
							rank = (Float) mainbodyRankMap.get(bodyId + ":" + body_id);	
												
						if (rank != null && rank.toString().trim().length()>0) //设置了动态主体权重
							bodyRank = rank.floatValue();												
												
						if(Float.toString(bodyRank)!=null && Float.toString(bodyRank).trim().length()>0 && !Float.toString(bodyRank).equalsIgnoreCase("0.0"))	
						{	
							String score = myformat1.format(Double.parseDouble(String.valueOf(bodyRank)));//去掉小数点后面的0
						//	score = PubFunc.round(score, 2);	
							mainbodyRank = PubFunc.round(String.valueOf(Double.parseDouble(score)*100), 2)+"%";																							
						//	mainbodyRank = myformat1.format(Double.parseDouble(score)*100)+"%";	
						}				
							
						if(mainbodyName==null || mainbodyName.trim().length()<=0)
							mainbodyRank = "";
										
						out.println("<td align='center' style='border-left:none;border-top:none;' class='RecordRow' >");
			 			out.print(mainbodyName);
			 			out.print("</td>");	
						out.println("<td align='center' style='border-left:none;border-top:none;' class='RecordRow' nowrap>");
			 			out.print(mainbodyNum);
			 			out.print("</td>");
			 			out.println("<td align='center' style='border-left:none;border-top:none;' class='RecordRow' nowrap>");
			 			out.print(mainbodyRank);
			 			out.print("</td>");
											
					}
						
					out.print("</tr>");
				}
			}
		%>
	
	</table>
	
	<script language='javascript' >
		document.write("</div>");
		document.write("<div style='position:absolute;left:5;top:"+(document.body.clientHeight-70)+";width:99%'  >");
	</script>
	
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">			
			
			<tr><td style="height:35px" align="center">  
			<%-- 			
			  	<hrms:priv> 
			    	<input type="button" name="outExcel" class="mybutton" value="导出Excel" onclick="ecportExcel();"/>
			  	</hrms:priv>
			--%>   	
			  	<hrms:priv> 
			    	<input type="button" name="back" class="mybutton" value="返回" onclick="backImplementPage();"/>
			  	</hrms:priv>
			</td></tr>					
		</table>	
	</div>	
	
	<script>
        //封装一个兼容性的获取元素样式的函数
        //分析:哪个元素,哪个样式
        function getStyle(obj,attr){
            if(obj.currentStyle){
                //IE
                return obj.currentStyle[attr];
            }else{
                //FF
                return getComputedStyle(obj,false)[attr];
            }
        }
	   //整合table边线
			var datatable = document.getElementById("tbl");
			datatable.className="ListTable";
			datatable.style.margin="-1px 1px 0 -1px";
			for(var i=0;i<datatable.rows.length;i++){
				var row = datatable.rows[i];
				var cellfirst = row.cells[0];
				//cellfirst.style.borderWidth="0 1 1 0px";
				var cellLast = row.cells[row.cells.length-1];
				cellLast.style.borderWidth="0 0 1 0px";
			}
			
			// 统一单元格的背景与边框颜色，去掉其余的样式 lium
			var tblContainer = document.getElementById("tbl-container");
			if (tblContainer) {
				var t = tblContainer.getElementsByTagName("table")[0];
				if (t) {
					var rows = t.rows;
                    var bgColor = "";
                    var borderColor = "";
					for (var i = 0; i < rows.length; i++) {
						// 获取当前皮肤下的背景及边框颜色
                        var cells = rows[i].cells;
                        for (var j=0;j<cells.length;j++){
                            bgColor = getStyle(cells[j],"backgroundColor");
                            if (borderColor ==""){
                                borderColor = getStyle(cells[j],"borderColor");
                            }

                            // 清空单元格所有样式
                            cells[j].className = "";
                            cells[j].removeAttribute("style");
                            if (bgColor!=""){
                                cells[j].style.backgroundColor = bgColor;
                            }
                            cells[j].style.border="1px solid " + borderColor;
                        }


					}
				}
			}
	</script>
	
</html:form>

