package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;

public class CertificateFieldSetConfigTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String errorMsg = "";
		RowSet rs = null;
		try {
			CertificateConfigBo bo = new CertificateConfigBo(this.frameconn, this.userView);
			String fieldSetid = bo.getCertBorrowSubset();
			if(StringUtils.isEmpty(fieldSetid)) {
				errorMsg = "请设置证书借阅记录子集！";
				return;
			}
			
			StringBuffer columns = new StringBuffer("[");
			columns.append("'" + fieldSetid + "03',");
			columns.append("'" + fieldSetid + "05',");
			columns.append("'" + fieldSetid + "01',");
			columns.append("'" + fieldSetid + "07',");
			columns.append("'" + fieldSetid + "09',");
			columns.append("'" + fieldSetid + "13',");
			columns.append("'operation'");
			columns.append("]");
			
			StringBuffer columnJson = new StringBuffer("[");
			FieldItem fi = DataDictionary.getFieldItem(fieldSetid + "03", fieldSetid);
			if(fi == null) {
				errorMsg = "证书编号指标不存在！";
				return;
			}
				
			setcolumnJson(columnJson, fi);
			fi = DataDictionary.getFieldItem(fieldSetid + "05", fieldSetid);
			if(fi == null) {
				errorMsg = "证书名称指标不存在！";
				return;
			}
			
			setcolumnJson(columnJson, fi);
			fi = DataDictionary.getFieldItem(fieldSetid + "01", fieldSetid);
			if(fi == null) {
				errorMsg = "证书类别指标不存在！";
				return;
			}
			
			setcolumnJson(columnJson, fi);
			fi = DataDictionary.getFieldItem(fieldSetid + "07", fieldSetid);
			if(fi == null) {
				errorMsg = "证书所有人指标不存在！";
				return;
			}
			
			setcolumnJson(columnJson, fi);
			fi = DataDictionary.getFieldItem(fieldSetid + "09", fieldSetid);
			if(fi == null) {
				errorMsg = "借阅日期指标不存在！";
				return;
			}
			
			setcolumnJson(columnJson, fi);
			fi = DataDictionary.getFieldItem(fieldSetid + "13", fieldSetid);
			if(fi == null) {
				errorMsg = "借阅事由指标不存在！";
				return;
			}
			
			setcolumnJson(columnJson, fi);
			fi = new FieldItem();
			fi.setItemdesc("操作");
			fi.setItemid("operation");
			setcolumnJson(columnJson, fi);
			if(columnJson.toString().endsWith(","))
				columnJson.setLength(columnJson.length() - 1);
			
			columnJson.append("]");
			String nbases = this.userView.getDbpriv().toString();
			if(this.userView.isSuper_admin()) {
				nbases = "";
				ContentDAO dao = new ContentDAO(this.frameconn);
				rs = dao.search("select Pre from DBName");
				while (rs.next()) {
					nbases += rs.getString("Pre") + ",";
				}
			}
				
			this.getFormHM().put("recordFieldSet", fieldSetid.toLowerCase());
			this.getFormHM().put("columns", columns.toString());
			this.getFormHM().put("columnJson", columnJson.toString());
			this.getFormHM().put("nbases", nbases);
		}catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("errorMsg", e.getMessage());
		} finally {
			PubFunc.closeResource(rs);
			this.getFormHM().put("errorMsg", errorMsg);
		}
	}
	
	private void setcolumnJson (StringBuffer columnJson, FieldItem fi) {
		columnJson.append("{text: '" + fi.getItemdesc() + "',");
		columnJson.append("dataIndex: '" + fi.getItemid().toLowerCase() + "',");
		if("operation".equals(fi.getItemid()))
			columnJson.append("renderer:certificateManage.rendererFun,");
		else if(!"0".equalsIgnoreCase(fi.getCodesetid())) 
			columnJson.append("renderer:certificateManage.showCodeitemDesc,");
		
		if("operation".equals(fi.getItemid()))
			columnJson.append("align:'center',");
		else if("N".equalsIgnoreCase(fi.getItemtype())) 
			columnJson.append("align:'right',");
		else
			columnJson.append("align:'left',");
		
		columnJson.append("sortable: false,width:100,menuDisabled:true},");
	}

}
