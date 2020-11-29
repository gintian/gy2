package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.manage.businessobject.CertificateBorrowedListBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CertificateBorrowedListTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try {
			CertificateBorrowedListBo cerBorrowBo = new CertificateBorrowedListBo(this.frameconn, this.userView);
			CertificateConfigBo certificateConfigBo = new CertificateConfigBo(this.frameconn, this.userView);
			String certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
			if (certBorrowSubset == null) {
				this.getFormHM().put("certBorrowSubset", "false");
				return;
			}
			
			ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
			String subModuleId = "borrowedList";
			columnList = cerBorrowBo.getColumns(subModuleId);
			String sql = cerBorrowBo.getBorrowSql(columnList);
			String orderBy = "order by " + certBorrowSubset + "11  desc";
			
			ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();
			ButtonInfo saveButton = new ButtonInfo();
			saveButton.setFunctype(ButtonInfo.FNTYPE_EXPORT);
			saveButton.setText("导出Excel");
			buttonList.add(saveButton);
			
			ButtonInfo returnButton = new ButtonInfo("返回", "certificateManage.closeWin");
			buttonList.add(returnButton);
			
			ButtonInfo querybox = new ButtonInfo();
			querybox.setFunctionId("CF01050007");
			querybox.setType(ButtonInfo.TYPE_QUERYBOX);
			querybox.setText("请输入姓名、证书分类、证书编号、证书名称...");
			buttonList.add(querybox);
			
			/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnList, subModuleId, this.userView,
					this.frameconn);
			
			builder.setLockable(true);
			builder.setDataSql(sql);
			builder.setOrderBy(orderBy);
			builder.setAutoRender(false);
			builder.setTableTools(buttonList);
			builder.setSetScheme(true);
			builder.setTitle("借阅台账");
			builder.setScheme(true);
			builder.setPageSize(20);
			builder.setColumnFilter(true);
			builder.setSelectable(true);
			builder.setEditable(false);
			builder.setScheme(true);
			builder.setItemKeyFunctionId("CF01050015");
			builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TITLE);
			builder.setSchemeSaveCallback("certificateManage.schemeSaveCallback");
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("subModuleId", subModuleId);
			this.getFormHM().put("certBorrowSubset", certBorrowSubset);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
}
