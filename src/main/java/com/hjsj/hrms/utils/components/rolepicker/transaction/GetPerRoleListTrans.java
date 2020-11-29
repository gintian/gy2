package com.hjsj.hrms.utils.components.rolepicker.transaction;

import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 项目名称 ：ehr
 * 类名称：GetPerRoleListTrans
 * 类描述：角色选择
 * 创建人： lis
 * 创建时间：2016-4-25
 */
public class GetPerRoleListTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		try {
			String sql ="select role_id,role_name,role_desc,norder from t_sys_role where valid=1  "; 
			ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
        
            // 角色编号
            ColumnsInfo role_id = getColumnsInfo("role_id", "角色编号", 80, "N");
            role_id.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            role_id.setKey(true);
            role_id.setEncrypted(true);
            columnTmp.add(role_id);

            // 角色名称
            ColumnsInfo role_name = getColumnsInfo("role_name", "名称", 245, "A"); 
            columnTmp.add(role_name);

            // 角色描述
            ColumnsInfo role_desc = getColumnsInfo("role_desc", "描述", 245, "A");
            columnTmp.add(role_desc);	
            
            ArrayList buttonList = getButtonList();
            
            TableConfigBuilder builder = new TableConfigBuilder("rsyd_perrole_00001", columnTmp, "perrole", this.userView, this.getFrameconn());
			builder.setDataSql(sql);
			builder.setOrderBy(" order by norder");
//			builder.setAutoRender(true);
			builder.setSelectable(true);
//			builder.setEditable(true);
			builder.setPageSize(20);
			builder.setTableTools(buttonList);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
		} catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
	}
		
	private ArrayList getButtonList() {
		ArrayList buttonList  = new ArrayList();
		ButtonInfo queryBox = new ButtonInfo();
		queryBox.setType(queryBox.TYPE_QUERYBOX);
		queryBox.setText("请输入角色名称、描述");
		queryBox.setFunctionId("ZJ100000152");
		queryBox.setShowPlanBox(false);
		buttonList.add(queryBox);
		return buttonList;
	}

	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type) {

	        ColumnsInfo columnsInfo = new ColumnsInfo();
	        columnsInfo.setColumnId(columnId);
	        columnsInfo.setColumnDesc(columnDesc);
	        //columnsInfo.setCodesetId("");// 指标集
	        columnsInfo.setColumnType(type);// 类型N|M|A|D
	        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
	        if ("A".equals(type)) {
	            columnsInfo.setCodesetId("0");
	        }
	        columnsInfo.setDecimalWidth(0);// 小数位

	        // 数值和日期默认居右
	        if ("D".equals(type) || "N".equals(type))
	            columnsInfo.setTextAlign("right");

	        return columnsInfo;
	    }
}
