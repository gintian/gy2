package com.hjsj.hrms.module.questionnaire.plan;

import com.hjsj.hrms.module.questionnaire.template.businessobject.TemplateBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchMyQuestionnaireTrans extends IBusiness {

	
	@Override
    public void execute() throws GeneralException {
		
		try {
			TemplateBo bo = new TemplateBo(this.getFrameconn());
			bo.searchExpirePlan();
			
			/** 列头 begin*/
			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
			
			/**隐藏的列头 begin*/
			ColumnsInfo qnid = new ColumnsInfo();
			qnid.setColumnId("qnid");
			qnid.setColumnDesc("问卷号");
			qnid.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
			columnsInfo.add(qnid);
			
			ColumnsInfo planid = new ColumnsInfo();
			planid.setColumnId("planid");
			planid.setKey(true);
			planid.setColumnDesc("计划号");
			planid.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
			columnsInfo.add(planid);
			
			ColumnsInfo connNumber = new ColumnsInfo();
			connNumber.setColumnId("connnumber");
			connNumber.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
			columnsInfo.add(connNumber);
			/**隐藏的列头 end*/
				
				
			ColumnsInfo questionnaireId = getColumnsInfo("planid", "计划号", 260);
			questionnaireId.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsInfo.add(questionnaireId);
			
			ColumnsInfo questionnairename = getColumnsInfo("planname", "名称", 260);
			questionnairename.setRendererFunc("QN_global.plannameRenderFunc");//添加名称列超链接
			questionnairename.setSortable(true);//每一列的列头添加  changxy 20160625
			columnsInfo.add(questionnairename);
			
			ColumnsInfo questionnairestatus = getColumnsInfo("status", "状态", 60);
			questionnairestatus.setColumnType("A");
			questionnairestatus.setRendererFunc("QN_global.statusRender");
			questionnairestatus.setSortable(true);
			columnsInfo.add(questionnairestatus);
			
			ColumnsInfo questionnairecopies = getColumnsInfo("recoverycount", "收集份数", 70);
			questionnairecopies.setTextAlign("right");
			questionnairecopies.setSortable(true);
			columnsInfo.add(questionnairecopies);
			
			ColumnsInfo qncreteperson = getColumnsInfo("createuser", "创建人", 80);
			qncreteperson.setSortable(true);
			columnsInfo.add(qncreteperson);
			
			ColumnsInfo qncratetime = getColumnsInfo("qn_createtime", "创建时间", 140);
			qncratetime.setTextAlign("right");
			qncratetime.setColumnLength(18);
			qncratetime.setSortable(true);
			qncratetime.setColumnType("D");
			columnsInfo.add(qncratetime);
			
			ColumnsInfo qnpubtime = getColumnsInfo("pubtime", "发布时间", 140);
			qnpubtime.setTextAlign("right");
			qnpubtime.setColumnLength(18);
			qnpubtime.setColumnType("D");
			qnpubtime.setSortable(true);
			columnsInfo.add(qnpubtime);
			
			FieldItem b0110 = DataDictionary.getFieldItem("b0110");
			b0110 = (FieldItem)b0110.cloneItem();
			b0110.setItemdesc(ResourceFactory.getProperty("hire.belong.organazition"));
			
			ColumnsInfo qnaffiliatedunit = new ColumnsInfo(b0110);
			qnaffiliatedunit.setSortable(true);
			columnsInfo.add(qnaffiliatedunit);
			
			ColumnsInfo qnaction = new ColumnsInfo();
			qnaction.setColumnDesc("操作");
			qnaction.setRendererFunc("QN_global.actionRender");
			qnaction.setColumnWidth(300);
			columnsInfo.add(qnaction);
			
			
			
			/** 列头 end*/

			/**获取数据*/
			String datasql ="select planid,planname,status,(case when recoverycount is null then 0 else recoverycount end) recoverycount,createuser,createtime qn_createtime,pubtime,b0110,qnid,options,(select count(1) from qn_template where qnid = qn_plan.qnid) connnumber from qn_plan ";

			
			/** 加载表格 */
			ArrayList buttonList = new ArrayList(); 
			
			//选人控件优先级是业务范围>操作单位>人员范围，默认展示的数据也需要按照这个顺序来
			String func="";
			String busiv = userView.getUnitIdByBusi("4");
			//如果busiv的值是UN`，说明拥有整个组织机构的权限
			if("UN`".equalsIgnoreCase(busiv)){
				//【57923】V77问卷调查：在问卷设计页面。点击【存为模板】按钮，弹框中所属机构默认显示一个机构，登录用户的权限为组织机构时，所属机构为空白了
				func = bo.getFunc();
			}else{
				String [] busivArr = null;
				if(busiv.contains("`")){
					busivArr = busiv.split("`");
					busiv = busivArr[0];
				}
				String codesetid = busiv.substring(0,2);
				String codeitemid = busiv.replace(codesetid,"");
				CodeItem codeitem=AdminCode.getCode(codesetid,codeitemid);
				if(codeitem !=null){
					func = codeitemid+"`"+codeitem.getCodename();
				}
			}
			HashMap<String, Integer> map=new HashMap<String, Integer>();
			//菜单创建权限
			if(this.userView.hasTheFunction("4001")){
				ButtonInfo newButton = new ButtonInfo("创建", "QN_global.createNewQN");
				newButton.setParameter("funcpriv", func);
				buttonList.add(newButton);
			}
			
			if(this.userView.hasTheFunction("4002")){
				ButtonInfo deleteButton = new ButtonInfo("删除","QN_global.deleteOrCleanPlan");	
				deleteButton.setGetData(true);
				deleteButton.setParameter("action","delete");
				deleteButton.setId("deletePlan");
				buttonList.add(deleteButton);
				map.put("deletePlan", 1);
			}else{
				map.put("deletePlan", 0);
			}
			if(this.userView.hasTheFunction("4003")){
				ButtonInfo cleanButton = new ButtonInfo("清空", "QN_global.deleteOrCleanPlan");
				cleanButton.setGetData(true);
				cleanButton.setParameter("action","clean");
				cleanButton.setId("cleanPlan");
				buttonList.add(cleanButton);
				map.put("cleanPlan", 1);
			}else{
				map.put("cleanPlan", 0);
			}
			if(this.userView.hasTheFunction("4011")){
				ButtonInfo seeTemplate=new ButtonInfo("查看模板","QN_global.seeTemplates");
				seeTemplate.setParameter("funcpriv", func);
				buttonList.add(seeTemplate);
			}
	        //创建操作列内权限控制
			/**
			 * 
			 * */
			if(this.userView.hasTheFunction("4010"))//开始/发布
				map.put("starts", 1);
			else
				map.put("starts", 0);
			
			/*if(this.userView.hasTheFunction("30025004"))//取消预览权限控制
				map.put("previewTemplate", 1);
			else
				map.put("previewTemplate", 0);*/
			if(this.userView.hasTheFunction("4005"))//收集配置
				map.put("recoverycount", 1);
			else
				map.put("recoverycount", 0);
			if(this.userView.hasTheFunction("4006"))//分析
				map.put("analysisPlanData", 1);
			else
				map.put("analysisPlanData", 0);
			if(this.userView.hasTheFunction("4007"))//暂停
				map.put("purse", 1);
			else
				map.put("purse", 0);
			if(this.userView.hasTheFunction("4008"))//结束
				map.put("stops", 1);
			else
				map.put("stops", 0);
			if(this.userView.hasTheFunction("4009"))//设计
				map.put("designTemplate", 1);
			else
				map.put("designTemplate", 0);
			
			
			
			TableConfigBuilder builder = new TableConfigBuilder("qn_template_00001", columnsInfo, "questionnaire", userView, this.getFrameconn());
			builder.setDataSql(datasql);
			builder.setOrderBy("order by qn_createtime desc");
			builder.setAutoRender(false);
			builder.setTitle("我的问卷");
			builder.setSetScheme(false);
			builder.setSearchConfig("QN10000005","按名称查询...",false);
			builder.setSelectable(true);
			builder.setPageSize(20);
			builder.setColumnFilter(true);//添加过滤 changxy
			builder.setTableTools(buttonList);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("funcpriv", func);
			this.getFormHM().put("Renderconfig", map);//操作配置
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 列头ColumnsInfo对象初始化
	 * @param columnId id
	 * @param columnDesc 名称
	 * @param columnDesc 显示列宽
	 * @return
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth){
		
		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setCodesetId("");// 指标集
		columnsInfo.setColumnType("M");// 类型N|M|A|D
		columnsInfo.setColumnWidth(columnWidth);//显示列宽
		columnsInfo.setColumnLength(100);// 显示长度 
		columnsInfo.setDecimalWidth(0);// 小数位
		columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
		columnsInfo.setReadOnly(true);// 是否只读
		columnsInfo.setFromDict(false);// 是否从数据字典里来
		columnsInfo.setLocked(false);//是否锁列
		
		return columnsInfo;
	}
	
}
