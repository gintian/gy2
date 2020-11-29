package com.hjsj.hrms.module.system.personalsoduku.boxreport.transaction;

import com.hjsj.hrms.module.system.personalsoduku.boxreport.businessobject.BoxReportBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
* <p>Title:GetBoxReportData </p>
* <p>Description: 盒式报表表格显示</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Dec 9, 2015 4:07:25 PM
 */
public class GetBoxReportDataTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		try {
			BoxReportBo bo = new BoxReportBo(this.frameconn,this.userView);
			//获取表头
			ArrayList<ColumnsInfo> columnList =bo.getColumnList();
			StringBuffer datasql =  new StringBuffer();
			datasql.append("SELECT t.box_id,t.name,t.data_from ");
			datasql.append(" ,(select itemdesc from t_hr_busifield where itemid=t.h_field and fieldsetid=t.data_from) as h_field ");
			datasql.append(" ,t.h_field_desc ");
			datasql.append(" ,(select itemdesc from t_hr_busifield where itemid=t.v_field and fieldsetid=t.data_from) as v_field ");
			datasql.append(" ,t.v_field_desc,(select itemdesc from t_hr_busifield where itemid=t.time_dim_field and fieldsetid=t.data_from) as time_dim_field,t.time_dim_type,t.static_ids ");
			datasql.append(" ,t.status,t.show_percent ");
			datasql.append("FROM t_sys_box_report t ");
		
			/** 获取操作按钮*/
    		ArrayList buttonList = bo.getButtonList();
    		String editflag = "0";
    		if (this.userView.isSuper_admin() || this.userView.hasTheFunction("3001H03")){
    			editflag = "1";
    		}
			TableConfigBuilder builder = new TableConfigBuilder("zj_boxreport_00001", columnList, "boxreport", userView, this.getFrameconn());
			builder.setDataSql(datasql.toString());
			builder.setOrderBy("order by box_id");
			builder.setAutoRender(true);
			builder.setTitle("盒式报表");
			builder.setSetScheme(false);
//			builder.setSearchConfig("ZC00002008","按名称、单位、部门查询...");
			builder.setSelectable(true);
//			builder.setEditable(true);
			builder.setPageSize(20);
			builder.setTableTools(buttonList);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("editflag", editflag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
