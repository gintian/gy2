package com.hjsj.hrms.module.org.virtualorg.trans;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.org.virtualorg.bo.VirturalRoleTransBo;
import com.hjsj.hrms.transaction.param.GetFieldBySetNameTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author liubq
 * @since 2016-11-21 13:30:29
 * @version  1.0
 * 虚拟机构记录录入
 * */
public class GetVirturalMemberTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		
		String virtualAxx = SystemConfig.getPropertyValue("virtualOrgSet");
		
		HashMap hm = this.getFormHM();
		String code=(String)this.getFormHM().get("code");
		code=PubFunc.decrypt(code.split("=")[1]);
		code = code.split("=")[1];
		//初始化BO类
		VirturalRoleTransBo vrbo = new VirturalRoleTransBo(this.frameconn,this.userView);
		//初始化列头集合
		ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
		
		columnsInfo = vrbo.getColunmList(code);
		
		ArrayList buttonList = new ArrayList();
		ButtonInfo exportExcel = new ButtonInfo("Excel导出", "");
		exportExcel.setFunctype(ButtonInfo.FNTYPE_EXPORT);
		buttonList.add(exportExcel);
		
		ButtonInfo newButton = new ButtonInfo("新增成员", "globalVirOrg.newMember");
		newButton.setId("newMember");
		buttonList.add(newButton);
		
		buttonList.add(new ButtonInfo("保存",ButtonInfo.FNTYPE_SAVE,"ORG0000002"));
		
		ButtonInfo delButton = new ButtonInfo("撤销", "globalVirOrg.cancelMember");
		buttonList.add(delButton);
		
		//ButtonInfo virtualRole = new ButtonInfo("虚拟角色", "globalVirOrg.virtualRole");
		//buttonList.add(virtualRole);
		
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name","1");
		String onlyViewName = "";
		if(onlyname!=null && onlyname.length()>0){
			GetFieldBySetNameTrans gf = new GetFieldBySetNameTrans();
			ArrayList<CommonData> chklist = gf.getUsedFieldBySetNameTrans("A01",this.userView);
			for(CommonData comDa:chklist){
				if(comDa.getDataValue().equals(onlyname)){ 
					onlyViewName = (comDa.getDataName()).split(":")[1];break;
				}
			}
			if(onlyViewName.length()>0) onlyViewName = "、"+onlyViewName;
		}
		
		ButtonInfo searchBox = new ButtonInfo();
		searchBox.setFunctionId("ORG0000015");//查询所走的交易号
		if(onlyViewName.indexOf("姓名")>-1)//当唯一性指标为姓名时不重复添加姓名
			onlyViewName="";
		searchBox.setText("请输入姓名"+onlyViewName);//blank text
		searchBox.setType(ButtonInfo.TYPE_QUERYBOX);//类型 查询框
		searchBox.setShowPlanBox(false);//不显示查询方案
		buttonList.add(searchBox);
		
		String sql = "";
		TableConfigBuilder builder = new TableConfigBuilder("virtual_record_00001", columnsInfo, "virtualRecord", userView, this.getFrameconn());
		builder.setDataSql(vrbo.getVirturalColumsql(code,true));
		builder.setOrderBy(" order by A0000");
		builder.setAutoRender(true);
		builder.setTitle("虚拟组织成员");
		builder.setTableTools(buttonList);
		builder.setSelectable(true);
		builder.setPageSize(20);
		builder.setEditable(true);
		String config = builder.createExtTableConfig();
		this.getFormHM().put("tableConfig", config.toString());
	}
}
