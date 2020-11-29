<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.achivement.kpiOriginalData.KpiOriginalDataForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.constant.SystemConfig,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>

<%
	    // 在标题栏显示当前用户和日期 2004-5-10 
	    String userName = null;
	    String css_url = "/css/css1.css";
	    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	    if (userView != null)
	    {
			css_url = userView.getCssurl();
			if (css_url == null || css_url.equals(""))
			    css_url = "/css/css1.css";
	    }
%>
<%  
		KpiOriginalDataForm kpiOriginalDataForm=(KpiOriginalDataForm)session.getAttribute("kpiOriginalDataForm");				
		String refreshKey=(String)kpiOriginalDataForm.getRefreshKey();	     		
		String checkCycle=(String)kpiOriginalDataForm.getCycle();	     //  考核周期	
		String objectType=(String)kpiOriginalDataForm.getObjecType();	 // 对象类别：1 单位 2 人员
		String year=(String)kpiOriginalDataForm.getYear();	 
		String noYearCycle=(String)kpiOriginalDataForm.getNoYearCycle();	 // 非年度考核周期	
		String checkName=(String)kpiOriginalDataForm.getCheckName();	 // 查询姓名
		String unionOrgCode=(String)kpiOriginalDataForm.getUnionOrgCode();	 // 机构树选到的节点
		String hcmflag="";
		if(userView != null)
		  hcmflag=userView.getBosflag();
%>

<style>

.keyMatterDiv 
{ 
	overflow:auto; 
	/* height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10); */
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid ; 
}
.TEXT_NB 
{
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;	
}
.TableRow{
	border-color:#C4D8EE !important;
}
</style>

<script LANGUAGE=javascript src="/js/xtree.js"></script>

<hrms:themes />
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<script language="javascript" src="/performance/achivement/kpiOriginalData/kpiOriginalData.js"></script>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>

<script type="text/javascript">
//haosl 小窗口会出现滚动条  2018-1-31
var down_height=(document.body.clientHeight==0?document.documentElement.clientHeight:document.body.clientHeight)-110+"px";
var down_height1=(document.body.clientHeight==0?document.documentElement.clientHeight:document.body.clientHeight)-131+"px";

//加载上传组件
Ext.Loader.setConfig({
    enabled: true,
    paths: {
        'SYSF':'/components/fileupload'
    }
});
Ext.require('SYSF.FileUpLoad');

// 下载模板
function downTemplate()
{	
	var onlyFild="${kpiOriginalDataForm.onlyFild}";
	var objecType="${kpiOriginalDataForm.objecType}";
	if(objecType!=null && objecType!='undefined' && objecType=='2')
	{
		if(onlyFild==null || onlyFild.length<=0 || onlyFild=='undefined')
		{
			alert("系统没有指定唯一性指标！请指定！");
			return;
		}
	}
	var hashVo=new ParameterSet();
	hashVo.setValue("onlyFild",onlyFild);
	hashVo.setValue("refreshKey","<%=refreshKey%>");
	hashVo.setValue("cycle","<%=checkCycle%>");
	hashVo.setValue("objectType","<%=objectType%>");
	hashVo.setValue("year","<%=year%>");
	hashVo.setValue("noYearCycle","<%=noYearCycle%>");
	hashVo.setValue("checkName","<%=checkName%>");	
	hashVo.setValue("unionOrgCode","<%=unionOrgCode%>"); 	
	var request=new Request({method:'post',asynchronous:false,onSuccess:onSucess,functionId:'9020020412'},hashVo);			
}
function onSucess(outparameters)
{
	var outname=outparameters.getValue("name");
//	var name=outname.substring(0,outname.length-2)+".xls";
//	name=getEncodeStr(name);
	window.location.target="_blank";
//	window.location.href = "/servlet/DisplayOleContent?filename="+outname;
	//20/3/6 xus vfs改造
	window.location.href = "/servlet/vfsservlet?fileid="+outname+"&fromjavafolder=true";
}
	
// 导入数据
function importDataValue()
{	
	var onlyFild="${kpiOriginalDataForm.onlyFild}";
	var objecType="${kpiOriginalDataForm.objecType}";
	
	if(objecType!=null && objecType!='undefined' && objecType=='2')
	{
		if(onlyFild==null || onlyFild.length<=0 || onlyFild=='undefined')
		{
			Ext.showAlert("系统没有指定唯一性指标！请指定！");
			return;
		}
	}
	var uploadObj =  Ext.create("SYSF.FileUpLoad",{
		renderTo:Ext.getBody(),
		emptyText:"请输入文件路径或选择文件",
		//是否为临时文件 true是，false不是
	    isTempFile:true,
	    //关联VfsFiletypeEnum 文件类型 例：VfsFiletypeEnum.doc
	    VfsFiletype:VfsFiletypeEnum.doc,
	    //关联VfsModulesEnum 模块id 例：VfsModulesEnum.CARD
	    VfsModules:VfsModulesEnum.FW,
	    //关联VfsCategoryEnum 文件所属类型 例：VfsCategoryEnum.personnel
	    VfsCategory: VfsCategoryEnum.other,
	    //所属类型guidkey
	    CategoryGuidKey: null,
		upLoadType:1,
		fileExt:"*.xlsx;*.xls;",
		success:function(list){
			var obj = list[0];
			var hashvo=new HashMap();     
   			hashvo.put("fileid",obj.fileid);
			hashvo.put("refreshKey","<%=refreshKey%>");
			hashvo.put("cycle","<%=checkCycle%>");
			hashvo.put("objectType","<%=objectType%>");
			hashvo.put("year","<%=year%>");
			hashvo.put("noYearCycle","<%=noYearCycle%>");
			hashvo.put("checkName","<%=checkName%>");	
			hashvo.put("unionOrgCode","<%=unionOrgCode%>");
    		Rpc({functionId:'9020020413',async:false,success:function(res){
    			Ext.getCmp("importWin").close();
    			var data = Ext.decode(res.responseText);
    			if(data.error=="0"){
    				Ext.showAlert("导入成功!",function(){
		    			kpiOriginalDataForm.action="/performance/achivement/kpiOriginalData/kpiOriginalDataList.do?b_query=checkSearch&refreshKey=saveKey&refreshData=yesOk";
		    			kpiOriginalDataForm.submit(); 
    				});
    			}else{
    				Ext.showAlert(data.message);
    			}
    		}},hashvo);
		}
	});
	Ext.widget("window",{
		id:'importWin',
			title: '导入数据',
        modal:true,
        border:false,
        resizable:false,
        width:380,
			height: 120,
        items:[{
           xtype: 'panel',
           border:false,
    	   layout:{  
             	type:'vbox',  
             	padding:'15 0 0 30', //上，左，下，右 
             	pack:'center',  
              	align:'middle'  
            },
           items:[uploadObj]
       }]
    }).show(); 
}
//用于鼠标触发的某一行
var curObjTr= null;
var oldObjTr_c= "";
function tr_onclick_self(objTr,bgcolor)
{
	if(curObjTr!=null)
	{
		curObjTr.style.background='#FFFFFF'; //oldObjTr_c;
		curObjTr.cells[0].style.background='#FFFFFF'; //oldObjTr_c;
		curObjTr.cells[1].style.background='#FFFFFF'; //oldObjTr_c;
		curObjTr.cells[2].style.background='#FFFFFF'; //oldObjTr_c;
		curObjTr.cells[3].style.background='#FFFFFF'; //oldObjTr_c;
		curObjTr.cells[4].style.background='#FFFFFF'; //oldObjTr_c;
	}
	ori_obj=objTr;
	curObjTr=objTr;
	oldObjTr_c='none';
	curObjTr.style.background='FFF8D2';
	objTr.cells[0].style.background='FFF8D2';
	objTr.cells[1].style.background='FFF8D2';
	objTr.cells[2].style.background='FFF8D2';
	objTr.cells[3].style.background='FFF8D2';
	objTr.cells[4].style.background='FFF8D2';
	
	selectObjectId=objTr.id;
	selectObjectId_extra=objTr.id_s;
	
}
</script>
<body style='margin-top:-5px;'>
<html:form action="/performance/achivement/kpiOriginalData/kpiOriginalDataList">

	<html:hidden name="kpiOriginalDataForm" property="objecType" styleId="objecType" />
	<html:hidden name="kpiOriginalDataForm" property="object_ids" styleId="object_ids" />
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">

		<tr>
			<td align="left" style="height:20px">
			
				&nbsp;
				<bean:message key="kpi.originalData.KpiTargetObjecType" />:
				<html:select name="kpiOriginalDataForm" property="objecType" size="1"
					onchange="refreshTree(this);" style="width:80px">					
					<html:optionsCollection property="objecTypeList" value="dataValue" label="dataName" />
				</html:select>
				
				&nbsp;
				<bean:message key="jx.khplan.cycle" />:
				<html:select name="kpiOriginalDataForm" styleId="cycle" property="cycle" size="1"
					onchange="searchCycle();" style="width:80px">					
					<html:optionsCollection property="cycleList" value="dataValue" label="dataName" />
				</html:select>
				
				&nbsp;
				<bean:message key="jx.khplan.yeardu" />:
				<html:select name="kpiOriginalDataForm" property="year" size="1"
					onchange="checkSearch();" style="width:80px">					
					<html:optionsCollection property="yearList" value="dataValue" label="dataName" />
				</html:select>
				<%
					if((checkCycle.trim().length()>0) && !(checkCycle.equalsIgnoreCase("all")) && !(checkCycle.equalsIgnoreCase("0")))
					{
				%>
					&nbsp;
					<html:select name="kpiOriginalDataForm" styleId="noYearCycle" property="noYearCycle" onchange="checkSearch();" size="1" >
		  				<html:optionsCollection property="noYearCycleList" value="dataValue" label="dataName"/>
					</html:select>			
				<%
					}
				%>		
							
				&nbsp;				
				<logic:equal name="kpiOriginalDataForm" property="objecType" value="1">
					<bean:message key="b0110.label" />:
					<html:text name="kpiOriginalDataForm" property="checkName" styleClass="inputtext"/>
				</logic:equal>
				<logic:equal name="kpiOriginalDataForm" property="objecType" value="2">
					<bean:message key="label.title.name" />:
					<html:text name="kpiOriginalDataForm" property="checkName" styleClass="inputtext"/>
				</logic:equal>
				&nbsp;
				<input type='button' class="mybutton" property="checked" onclick='checkSearch();'
						value='<bean:message key="infor.menu.query"/>' />
			</td>
			<%
			int i = 0;
			%>
		</tr>
		<tr>
			<td style='padding-top:2px;'>
			   <div id='kpidataid' class="keyMatterDiv common_border_color">
				<table width="100%" border="0"  align="center"
					 class="ListTable" cellspacing="0">
					<tr id='trFixed'>
						<td align="center" class="TableRow_right common_background_color common_border_color" style="border-top:0px;" nowrap>
							<input type="checkbox" name="selbox"
										onclick="batch_select(this, 'setlistform.select');">
						</td>
						<td align="center" class="TableRow" width='120px' style="border-top:0px;" nowrap>
							<bean:message key="kpi.originalData.businessTime" />
						</td>
						<td align="center" class="TableRow" style="border-top:0px;" nowrap>							
							<logic:equal name="kpiOriginalDataForm" property="objecType" value="1">
								<bean:message key="org.performance.unorum"/>
							</logic:equal>
							<logic:equal name="kpiOriginalDataForm" property="objecType" value="2">
								<bean:message key="org.performance.unorum"/>
							</logic:equal>							
						</td>
						<logic:equal name="kpiOriginalDataForm" property="objecType"
							value="2">
							<td align="center" class="TableRow" style="border-top:0px;"  nowrap>
								<bean:message key="hire.employActualize.name" />
							</td>
						</logic:equal>
						
						<td align="center" class="TableRow" style="border-top:0px;" nowrap>
							<bean:message key="kpi.originalData.KpiTarget" />
						</td>
						<td align="center" class="TableRow" style="border-top:0px;" nowrap>
							<bean:message key="kpi.originalData.targetDescription" />
						</td>
						<td align="center" class="TableRow" style="border-top:0px;" nowrap>
							<bean:message key="kpi.originalData.targetScore" />
						</td>
						<td align="center" class="TableRow_left common_background_color common_border_color" style="border-top:0px;" nowrap>
							<bean:message key="kpi.originalData.targetStatus" />
						</td>						
													
					</tr>

					<hrms:extenditerate id="element" name="kpiOriginalDataForm"
						property="setlistform.list" indexes="indexes"
						pagination="setlistform.pagination" pageCount="25" scope="session">
						<bean:define id="nid" name="element" property="id" /> 
						<%
							String color="#F3F5FC";
							if (i % 2 == 0)
							{
						%>
						<tr class="trShallow" onClick="javascript:tr_onclick_self(this,'<%=color %>')">
						<%
							}else{
								color="#E4F2FC";
						%>						
						<tr class="trDeep" onClick="javascript:tr_onclick_self(this,'<%=color %>')">
						<%
							}
							i++;
						%>
							<td align="center" class="RecordRow_right" nowrap>
								<Input type='hidden' id="${nid}" />
								<hrms:checkmultibox name="kpiOriginalDataForm"
										property="setlistform.select" value="true" indexes="indexes" />
								<Input type='hidden'
										value='<bean:write name="element" property="id" filter="true"/>' />
							</td>
							
							<td align="right" class="RecordRow" width='120px' nowrap>							
								<bean:write name="element" property="theyear" filter="true" />
								&nbsp;
							</td>
							
							<td align="left" class="RecordRow" nowrap>&nbsp;
								<logic:equal name="kpiOriginalDataForm" property="objecType" value="2">
									<bean:write name="element" property="codeitemdesc" filter="true" />
								<%-- 
										<hrms:codetoname codeid="UN" name="element"
											codevalue="b0110" codeitem="codeitem" scope="page" />
										<bean:write name="codeitem" property="codename" />
										/
										<hrms:codetoname codeid="UM" name="element"
											codevalue="e0122" codeitem="codeitem" scope="page" />
										<bean:write name="codeitem" property="codename" />
								--%>		
								</logic:equal>
								<logic:equal name="kpiOriginalDataForm" property="objecType" value="1">									
									<bean:write name="element" property="codeitemdesc" filter="true" />
								</logic:equal>
							</td>
							
							<logic:equal name="kpiOriginalDataForm" property="objecType" value="2">
								<td align="left" class="RecordRow" nowrap>&nbsp;
									<bean:write name="element" property="a0101" filter="true" />
								</td>
							</logic:equal>
	
							<td align="left" class="RecordRow" nowrap>&nbsp;
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
								
							<bean:define id="event" name="element" property="description" />
								<hrms:showitemmemo showtext="showtext" itemtype="M" setname=""
									tiptext="tiptext" text="${event}"></hrms:showitemmemo>
								<td align="left" class="RecordRow" ${tiptext}  nowrap>&nbsp;
									${showtext}&nbsp;
								</td>														
															
							<td align="center" class="RecordRow" nowrap>
								<logic:notEqual name="element" property="status" value="01">
									<input type='text' id='s_<bean:write name="element" property="id" filter="true" />' 
										   value='<bean:write name="element" property="actual_value" filter="true" />' 
										   onkeypress='return IsDigit_(event);'
										   onkeyup="if(isNaN(value))execCommand('undo')" onafterpaste="if(isNaN(value))execCommand('undo')" 
										   class='TEXT_NB inputtext' size='8' disabled="true"/>							
								</logic:notEqual>							
								<logic:equal name="element" property="status" value="01">
									<input type='text' id='s_<bean:write name="element" property="id" filter="true" />' 
										   value='<bean:write name="element" property="actual_value" filter="true" />' 
										   onkeypress='return IsDigit_(event);'
										   onkeyup="if(isNaN(value))execCommand('undo')" onafterpaste="if(isNaN(value))execCommand('undo')" 
										   class='TEXT_NB inputtext' size='8' />							
								</logic:equal>														
							</td>
								
							<td align="center" class="RecordRow_left" nowrap>&nbsp;															
								<logic:notEqual name="element" property="status" value="01">
									生效
								</logic:notEqual>							
								<logic:equal name="element" property="status" value="01">
									起草
								</logic:equal>	
							</td>														
								
						</tr>
					</hrms:extenditerate>
				</table>
				</div>
			</td>
		</tr>
	</table>
	<table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				第
				<bean:write name="kpiOriginalDataForm"
					property="setlistform.pagination.current" filter="true" />
				页 共
				<bean:write name="kpiOriginalDataForm"
					property="setlistform.pagination.count" filter="true" />
				条 共
				<bean:write name="kpiOriginalDataForm"
					property="setlistform.pagination.pages" filter="true" />
				页
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="kpiOriginalDataForm"
						property="setlistform.pagination" nameId="setlistform"
						propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	<table width="100%" align="center">
		<tr>
			<td align="center" style="height:20px"> 			
					<input type='button' class="mybutton" property="b_save"
						onclick='saveDataValue();'
						value='<bean:message key="button.save"/>' />							
				<hrms:priv func_id="06080304">					  	  					 	 				 	 					  
					<input type='button' class="mybutton" property="b_compare"
						onclick='comBackData("compare");'
						value='<bean:message key="kq.emp.change.compare"/>' />
				</hrms:priv>	
				<hrms:priv func_id="06080305">	
					<input type='button' class="mybutton" property="b_back"
						onclick='comBackData("back");'
						value='<bean:message key="performance.spflag.bh"/>' />
				</hrms:priv>
				<hrms:priv func_id="06080306">		
					<input type='button' class="mybutton" property="b_delete"
						onclick='delDataValue();'
						value='<bean:message key="button.delete"/>' />
				</hrms:priv>								
				<hrms:priv func_id="06080307">		
					<input type='button' class="mybutton" property="b_download"
						onclick='downTemplate();'
						value='<bean:message key="button.download.template"/>' />
				</hrms:priv>
				<hrms:priv func_id="06080308">		
					<input type='button' class="mybutton" property="b_importData"
						onclick='importDataValue();'
						value='<bean:message key="import.tempData"/>' />
				</hrms:priv>	
			</td>
		</tr>
	</table>
	
	<script type="text/javascript">
		<%if("hl".equals(hcmflag)){%>
			document.getElementById("kpidataid").style.height=down_height;
		<%}else{%>
			document.getElementById("kpidataid").style.height=down_height1;
		<%}%>
		if(navigator.appName.indexOf("Microsoft")!=-1)//该样式只在ie下生效
			document.getElementById("trFixed").className="fixedHeaderTr";
	</script>
</html:form>
</body>
