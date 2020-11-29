package com.hjsj.hrms.module.certificate.manage.businessobject;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.utils.CertificatePrivBo;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CertificateBorrowedListBo {
	private Connection conn;
	private UserView userView;
	private String onlyField;
	private String certBorrowSubset;

	public CertificateBorrowedListBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}

	/**
	 * 获取表头显示列
	 * 
	 * @return
	 */
	public ArrayList<ColumnsInfo> getColumns(String subModuleId) {
		ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
		CertificateConfigBo certificateConfigBo = new CertificateConfigBo(this.conn, this.userView);
		this.certBorrowSubset = certificateConfigBo.getCertBorrowSubset();
		CertificateManageBo cmbo = new CertificateManageBo(this.userView, this.conn);
		// 获取系统设置的唯一性指标
		this.onlyField = cmbo.getOnlyFieldItem().get(0);
		
		TableFactoryBO tableBo = new TableFactoryBO(subModuleId, this.userView, conn);
		HashMap scheme = tableBo.getTableLayoutConfig();
		String coulumns = ",";
		if (scheme != null) {
			Integer schemeId = (Integer) scheme.get("schemeId");
			ArrayList<ColumnConfig> columnConfigList = tableBo.getTableColumnConfig(schemeId);

			for (int i = 0; i < columnConfigList.size(); i++) {
				ColumnConfig column = columnConfigList.get(i);
				if (null == column)
					continue;

				FieldItem fi = DataDictionary.getFieldItem(column.getItemid(), column.getFieldsetid());
				if (null == fi)
					continue;

				ColumnsInfo info = this.getCertColumn(column, fi, fi.getFieldsetid());
				columnList.add(info);
				coulumns += column.getItemid() + ",";
			}
		}
		
		ArrayList<FieldItem> fieldItemList = DataDictionary.getFieldList(this.certBorrowSubset, Constant.USED_FIELD_SET);
		for (int i = 0; i < fieldItemList.size(); i++) {
			FieldItem fi = fieldItemList.get(i);
			if (null == fi)
				continue;
			
			if(coulumns.contains("," + fi.getItemid() + ","))
				continue;
				
			ColumnsInfo info = this.getCertColumn(null, fi, this.certBorrowSubset);
			columnList.add(info);
			coulumns += fi.getItemid() + ",";
		}
		
		FieldItem fi = DataDictionary.getFieldItem("b0110", "A01");
		if(fi != null && !"0".equals(fi.getUseflag()) && !coulumns.contains("," + fi.getItemid() + ",")) {
			ColumnsInfo info = this.getCertColumn(null, fi, "A01");
			columnList.add(0, info);
			coulumns += fi.getItemid() + ",";
		}
		
		fi = DataDictionary.getFieldItem("e0122", "A01");
		if(fi != null && !"0".equals(fi.getUseflag()) && !coulumns.contains("," + fi.getItemid() + ",")) {
			ColumnsInfo info = this.getCertColumn(null, fi, "A01");
			columnList.add(1, info);
			coulumns += fi.getItemid() + ",";
		}
		
		fi = DataDictionary.getFieldItem("e01a1", "A01");
		if(fi != null && !"0".equals(fi.getUseflag()) && !coulumns.contains("," + fi.getItemid() + ",")) {
			ColumnsInfo info = this.getCertColumn(null, fi, "A01");
			columnList.add(2, info);
			coulumns += fi.getItemid() + ",";
		}
		
		fi = DataDictionary.getFieldItem("a0101", "A01");
        if(fi != null && !"0".equals(fi.getUseflag()) && !coulumns.contains("," + fi.getItemid() + ",")) {
            ColumnsInfo info = this.getCertColumn(null, fi, "A01");
            columnList.add(3, info);
            coulumns += fi.getItemid() + ",";
        }
        
		if (StringUtils.isNotEmpty(this.onlyField)) {
			fi = DataDictionary.getFieldItem(this.onlyField, "A01");
			if(fi != null && !"0".equals(fi.getUseflag()) && !coulumns.contains("," + fi.getItemid() + ",")) {
				ColumnsInfo info = this.getCertColumn(null, fi, "A01");
				columnList.add(4, info);
			}
		}
		
		return columnList;
	}
	/**
	 * 获取有栏目设置保存过的列头对象
	 * 
	 * @param column
	 *            表格栏目设置列头对象
	 * @param fieldItem
	 *            业务字典获取的指标对象
	 * @param table
	 *            证书借阅记录子集
	 * @return
	 */
	private ColumnsInfo getCertColumn(ColumnConfig column, FieldItem fieldItem, String table) {
		ColumnsInfo info = new ColumnsInfo();
		String itemid = ",A0101,B0110,E0122,E01A1," + this.onlyField.toUpperCase() + ",";
		if (null == column) {
			// 栏目设置没有私有方案时column为null
			info.setColumnDesc(getItemDesc(fieldItem, table));
			info.setColumnId(fieldItem.getItemid());
			info.setColumnType(fieldItem.getItemtype());
			info.setColumnWidth(100);
			info.setFieldsetid(fieldItem.getFieldsetid());
			info.setColumnLength(fieldItem.getItemlength());
			info.setSortable(true);
			info.setCodesetId(fieldItem.getCodesetid());
			if ("N".equalsIgnoreCase(fieldItem.getItemtype())) {
				info.setDecimalWidth(fieldItem.getDecimalwidth());
				info.setTextAlign("right");
			}
			
			if(itemid.contains(fieldItem.getItemid().toUpperCase()))
			    info.setLocked(true);
			
		} else {
			info.setColumnId(column.getItemid());
			String itemDesc = column.getDisplaydesc();
			if(StringUtils.isEmpty(itemDesc))
				itemDesc = getItemDesc(fieldItem, table);
			
			info.setColumnDesc(itemDesc);
			info.setColumnType(column.getItemtype());
			info.setColumnWidth(column.getDisplaywidth());
			info.setFieldsetid(column.getFieldsetid());
			info.setColumnLength(fieldItem.getItemlength());
			info.setSortable(true);
			info.setCodesetId(fieldItem.getCodesetid());
			info.setTextAlign(column.getAlign() + "");
			info.setLocked(Boolean.valueOf(column.getIs_lock()));
		}
		
		if(("A01".equalsIgnoreCase(table) && itemid.contains(fieldItem.getItemid().toUpperCase())) 
		        || this.certBorrowSubset.equalsIgnoreCase(table))
		    info.setRemovable(false);
		else
		    info.setRemovable(true);
		
		info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
		return info;
	}

	/**
	 * 获取有栏目设置保存过的列头对象
	 * 
	 * @param column
	 *            表格栏目设置列头对象
	 * @param fieldItem
	 *            业务字典获取的指标对象
	 * @param table
	 *            证书借阅记录子集
	 * @return
	 */
	public String getBorrowSql(ArrayList<ColumnsInfo> columnList) {
		StringBuffer sql = new StringBuffer();
		try {
			CertificateConfigBo bo = new CertificateConfigBo(this.conn, this.userView);
			ArrayList dbnames = bo.getCertNbase();
			String certBorrowSubset = bo.getCertBorrowSubset();
			String certSubset = bo.getCertSubset();
			String certCategoryItemId = bo.getCertCategoryItemId();
			String certNOItemId = bo.getCertNOItemId();
			String certOrg = bo.getCertOrganization();
			String certCategoryCode = bo.getCertCategoryCode();
			StringBuffer sqlColumns = new StringBuffer();
			StringBuffer subsetColumns = new StringBuffer();
			for (int i = 0; i < columnList.size(); i++) {
				ColumnsInfo column = columnList.get(i);
				if(certBorrowSubset.equalsIgnoreCase(column.getFieldsetid()))
					sqlColumns.append(certBorrowSubset + "." + column.getColumnId() + ",");
				else if("A01".equalsIgnoreCase(column.getFieldsetid()))
					sqlColumns.append("A01." + column.getColumnId() + ",");
				else if(certSubset.equalsIgnoreCase(column.getFieldsetid())) {
					sqlColumns.append(certSubset + "." + column.getColumnId() + ",");
					subsetColumns.append(column.getColumnId() + ",");
				}
			}
			
			if(!subsetColumns.toString().contains(certCategoryItemId))
				subsetColumns.append(certCategoryItemId + ",");
			
			if(!subsetColumns.toString().contains(certNOItemId))
				subsetColumns.append(certNOItemId + ",");
			
			if(!subsetColumns.toString().contains(certOrg))
				subsetColumns.append(certOrg + ",");
				
			subsetColumns.setLength(subsetColumns.length() - 1);	
				
			CertificatePrivBo privBo = new CertificatePrivBo();
			String whereSql = privBo.getCertOrgWhere(conn, userView, certSubset);
			sqlColumns.setLength(sqlColumns.length() - 1);
			
			StringBuffer subsetSql = new StringBuffer();
			for (int i = 0; i < dbnames.size(); i++) {
				String dbname = (String) dbnames.get(i);
				String CertSet = dbname + certSubset;
				if (StringUtils.isNotEmpty(subsetSql.toString()))
					subsetSql.append(" union all ");
				
				subsetSql.append("select " + subsetColumns);
				subsetSql.append(" from " + CertSet);
			}
			
			for (int i = 0; i < dbnames.size(); i++) {
				String dbname = (String) dbnames.get(i);
				String nbaseCert = dbname + certBorrowSubset;
				String mianSet = dbname + "A01";
				if (StringUtils.isNotEmpty(sql.toString()))
					sql.append(" union all ");
				
				sql.append("select " + sqlColumns);
				sql.append(" from " + nbaseCert + " " + certBorrowSubset);
				sql.append(" left join (" + subsetSql + ") " + certSubset);
				sql.append(" on " + certSubset + "." + certCategoryItemId);
				sql.append("=" + certBorrowSubset + "." + certBorrowSubset + "01");
				sql.append(" and " + certSubset + "." + certNOItemId);
				sql.append("=" + certBorrowSubset + "." + certBorrowSubset + "03");
				sql.append(" left join " + mianSet + " A01");
				sql.append(" on " + certBorrowSubset + ".a0100=A01.a0100");
				sql.append(" where " + whereSql);
			}
			
			String date = DateUtils.format(new Date(), "yyyy-MM-dd");
			sql.insert(0, "select * from (");
			sql.append(") temp where " + certBorrowSubset + "01 in (");
			sql.append("select codeitemid from codeitem");
			sql.append(" where " + Sql_switcher.isnull(Sql_switcher.dateToChar("end_date", "yyyy-MM-dd"), "'9999-12-31'"));
			sql.append(">='" + date + "'");
			sql.append(" and codesetid='" + certCategoryCode + "'");
			sql.append(")");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sql.toString();

	}

	private String getItemDesc(FieldItem fi, String table) {
		String itemDesc = "";
		String itemId = fi.getItemid();
		if(itemId.equalsIgnoreCase(this.certBorrowSubset + "01"))
			itemDesc = "借阅证书类别";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "03"))
			itemDesc = "借阅证书编号";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "05"))
			itemDesc = "借阅证书名称";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "07"))
			itemDesc = "持证人姓名";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "09"))
			itemDesc = "借用日期";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "11"))
			itemDesc = "预计归还日期";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "13"))
			itemDesc = "借阅事由";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "15"))
			itemDesc = "实际归还日期";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "17"))
			itemDesc = "归还人姓名";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "19"))
			itemDesc = "审批状态";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "21"))
			itemDesc = "审批（退回）意见";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "23"))
			itemDesc = "归还标识";
		else if(itemId.equalsIgnoreCase(this.certBorrowSubset + "25"))
			itemDesc = "归还说明";
		else if("A01".equalsIgnoreCase(table) && "a0101".equalsIgnoreCase(itemId))
			itemDesc = "借阅人姓名";
		else if("A01".equalsIgnoreCase(table) && "b0110".equalsIgnoreCase(itemId))
			itemDesc = "借阅人所在单位";
		else if("A01".equalsIgnoreCase(table) && "e0122".equalsIgnoreCase(itemId))
			itemDesc = "借阅人所在部门";
		else if("A01".equalsIgnoreCase(table) && "e01a1".equalsIgnoreCase(itemId))
			itemDesc = "借阅人所在岗位";
		else if("A01".equalsIgnoreCase(table) && itemId.equalsIgnoreCase(this.onlyField))
			itemDesc = "借阅人" + fi.getItemdesc();
		else
			itemDesc = fi.getItemdesc();
		
		return itemDesc;
	}
}
