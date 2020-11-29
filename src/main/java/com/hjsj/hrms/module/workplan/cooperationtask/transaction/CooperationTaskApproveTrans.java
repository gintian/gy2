package com.hjsj.hrms.module.workplan.cooperationtask.transaction;

import com.hjsj.hrms.module.workplan.cooperationtask.businessobject.CooperationTaskBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;


/**
 * @Title:        CooperationTaskApproveTrans.java
 * @Description:  项协办任务审批交易类
 * @Company:      hjsj     
 * @Create time:  2016-6-8 10:17:39
 * @author        liubq
 */
public class CooperationTaskApproveTrans extends IBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public void execute() throws GeneralException {
		
		ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
		ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("p10", Constant.USED_FIELD_SET);
		CooperationTaskBo bo = new CooperationTaskBo(this.frameconn,this.userView);
		columnsInfo = bo.getCoopColumnList(fieldList);
		
		String datasql ="SELECT P1001,P0800,P1003,P1005,GUIDKE_CREATER,P1007,P1009,GUIDKE_CREATER_SP,P1011,P1013,P1015,GUIDKE_OWNER,GUIDKE_OWNER_SP,P1017,case when p10.P1019='01' then '待批' when p10.p1019='02' then '已批' else '已退回' end p1019,P1021  FROM P10 WHERE 1=1 AND P1019='01'  AND GUIDKE_OWNER_SP = '"+bo.getGuidKey(this.userView.getA0100(),this.userView.getDbname())+"'";
		
		TableConfigBuilder builder = new TableConfigBuilder("workplan_apprv_p10", columnsInfo, "appr_p10", userView, this.getFrameconn());
		builder.setDataSql(datasql);
		builder.setOrderBy("order by P1001 desc");
		builder.setColumnFilter(true);
		builder.setAutoRender(true);
		builder.setTitle("协办任务审批表");
		builder.setConstantName("workplan/cooperationtask");
		builder.setTableTools(bo.getCoopButtonList());
		builder.setScheme(true);
		builder.setSelectable(true);
		builder.setPageSize(20);
		String config = builder.createExtTableConfig();
		this.getFormHM().put("tableConfig", config.toString());
	}
}
