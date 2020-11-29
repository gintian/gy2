package com.hjsj.hrms.module.workplan.cooperationtask.transaction;

import com.hjsj.hrms.module.workplan.cooperationtask.businessobject.CooperationTaskBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MyCooperationTaskTrans extends IBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {
		ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
		ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("p10", Constant.USED_FIELD_SET);
		CooperationTaskBo bo = new CooperationTaskBo(this.frameconn,this.userView);
		columnsInfo = bo.getMyCoopColumnList(fieldList);
		String guidKey = bo.getGuidKey(this.userView.getA0100(),this.userView.getDbname());
		
		/**获取数据sql*/
		String datasql ="SELECT p10.p1001 p1001,p10.p0800 p0800,p10.p1003 p1003,p10.p1005 p1005,p10.GUIDKE_CREATER GUIDKE_CREATER,p10.P1007 P1007,p10.P1009 P1009,p10.GUIDKE_CREATER_SP GUIDKE_CREATER_SP";
		datasql+=",p10.P1011 P1011,p10.P1013 P1013,p10.P1015 P1015,p10.GUIDKE_OWNER GUIDKE_OWNER,p10.GUIDKE_OWNER_SP GUIDKE_OWNER_SP,p10.P1017 P1017,case when p10.P1019='01' then '待批' when p10.p1019='02' then '已批' else '已退回' end p1019,p10.P1021 p1021";
		datasql+=","+this.userView.getDbname()+"A01.A0101 A0101 FROM P08,P10,"+this.userView.getDbname()+"A01 WHERE P10.P0800 = P08.P0800 AND (P08.create_user ='"+this.userView.getUserName()+"' OR ((p10.GUIDKE_CREATER = '"+guidKey+"' OR p10.GUIDKE_OWNER = '"+guidKey+"') AND p08.p0845 = '1')) AND "+this.userView.getDbname()+"A01.guidkey = p10.GUIDKE_OWNER_SP AND p10.P1021=2";
			
		TableConfigBuilder builder = new TableConfigBuilder("workplan_my_p10", columnsInfo, "my_p10", userView, this.getFrameconn());
		builder.setDataSql(datasql);
		builder.setOrderBy(" order by P1001 desc");
		builder.setAutoRender(true);
		builder.setColumnFilter(true);
		builder.setTitle("我的协作任务");
		builder.setConstantName("workplan/cooperationtask");
		builder.setTableTools(bo.getMyCoopButtonList());
		builder.setScheme(true);
		builder.setSelectable(true);
		builder.setPageSize(20);
		String config = builder.createExtTableConfig();
		this.getFormHM().put("tableConfig", config.toString());
		
		// 清除‘审批提醒’待办
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			rs =dao.search(datasql);
			while(rs.next()){
				String p0800 = rs.getString("p0800");
				bo.update_cooperationTask("1", p0800);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}
	
}
