package com.hjsj.hrms.module.certificate.manage.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CertificateManageBo {
	private Connection conn;
	private UserView userView;

	public CertificateManageBo(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}

	/**
	 * 获取表头显示列
	 * 
	 * @param subModuleId
	 * @return
	 */
	public ArrayList<ColumnsInfo> getColumns(String subModuleId) {
		CertificateConfigBo bo = new CertificateConfigBo(this.conn, this.userView);
		ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
		TableFactoryBO tableBo = new TableFactoryBO(subModuleId, this.userView, this.conn);
		HashMap scheme = tableBo.getTableLayoutConfig();
		String coulumns = ",";
		if (scheme != null && scheme.size() > 0) {
			Integer schemeId = (Integer) scheme.get("schemeId");
			ArrayList<ColumnConfig> columnConfigList = tableBo.getTableColumnConfig(schemeId);

			for (int i = 0; i < columnConfigList.size(); i++) {
				ColumnConfig column = columnConfigList.get(i);
				if (null == column)
					continue;

				FieldItem fi = DataDictionary.getFieldItem(column.getItemid(), bo.getCertSubset());
				if (null == fi)
					continue;

				ColumnsInfo info = this.getCertificateColumn(column, fi);
				columnList.add(info);
				coulumns += column.getItemid() + ",";
			}
		}

		if (!coulumns.contains(",b0110,")) {
			FieldItem fi = DataDictionary.getFieldItem("b0110", "A01");
			ColumnsInfo info = this.getCertificateColumn(null, fi);
			columnList.add(info);
		}
		
		if (!coulumns.contains(",e0122,")) {
			FieldItem fi = DataDictionary.getFieldItem("e0122", "A01");
			ColumnsInfo info = this.getCertificateColumn(null, fi);
			columnList.add(info);
		}
		
		if (!coulumns.contains(",e01a1,")) {
			FieldItem fi = DataDictionary.getFieldItem("e01a1", "A01");
			ColumnsInfo info = this.getCertificateColumn(null, fi);
			columnList.add(info);
		}
		
		if (!coulumns.contains(",a0101,")) {
			FieldItem fi = DataDictionary.getFieldItem("a0101", "A01");
			ColumnsInfo info = this.getCertificateColumn(null, fi);
			columnList.add(info);
		}
		
		ArrayList<String> onlyFieldList = getOnlyFieldItem();
		String onlyField = onlyFieldList.get(0);
		if (!coulumns.contains("," + onlyField + ",")) {
			FieldItem fi = DataDictionary.getFieldItem(onlyField, "A01");
			ColumnsInfo info = this.getCertificateColumn(null, fi);
			columnList.add(info);
		}

		ArrayList<FieldItem> fieldItemList = DataDictionary.getFieldList(bo.getCertSubset(), Constant.USED_FIELD_SET);
		for (int i = 0; i < fieldItemList.size(); i++) {
			FieldItem fi = fieldItemList.get(i);
			if (null == fi)
				continue;

			if (coulumns.contains("," + fi.getItemid() + ","))
				continue;

			ColumnsInfo info = this.getCertificateColumn(null, fi);
			columnList.add(info);
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
	 * @return
	 */
	private ColumnsInfo getCertificateColumn(ColumnConfig column, FieldItem fieldItem) {

		ColumnsInfo info = new ColumnsInfo();
		if (null == column) {
			// 栏目设置没有私有方案时column为null
			info.setColumnId(fieldItem.getItemid());
			info.setColumnDesc(fieldItem.getItemdesc());
			info.setColumnType(fieldItem.getItemtype());
			info.setColumnWidth(100);
			info.setFieldsetid(fieldItem.getFieldsetid());
			info.setColumnLength(fieldItem.getItemlength());
			info.setSortable(true);
			info.setCodesetId(fieldItem.getCodesetid());
		} else {
			// column不为null说明该表格栏目设置有私有方案
			info.setColumnId(column.getItemid());
			info.setColumnDesc(
					StringUtils.isEmpty(column.getItemdesc()) ? fieldItem.getItemdesc() : column.getItemdesc());
			info.setColumnType(column.getItemtype());
			info.setColumnWidth(100);
			info.setColumnLength(fieldItem.getItemlength());
			info.setFieldsetid(column.getFieldsetid());
			info.setSortable(true);
			info.setCodesetId(fieldItem.getCodesetid());
			info.setTextAlign(column.getAlign() + "");
		}
		// a0100单独处理
		if ("a0100".equalsIgnoreCase(fieldItem.getItemid())) {
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			info.setEncrypted(true);
		} else
			info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);

		if ("N".equalsIgnoreCase(fieldItem.getItemtype())) {
			info.setDecimalWidth(fieldItem.getDecimalwidth());
			if (null == column)
				info.setTextAlign("right");
		}
		// 渲染姓名链接函数
		// if("A0101".equalsIgnoreCase(fieldItem.getItemid()))
		// info.setRendererFunc("");

		return info;
	}

	/**
	 * 获导出的模板中的列头
	 * 
	 * @param itemList
	 * @return
	 */
	public ArrayList<LazyDynaBean> getTemplateHeadList(ArrayList<ColumnsInfo> itemList,
			String certOrg) {
		CertificateConfigBo bo = new CertificateConfigBo(this.conn, this.userView);
		// 获取证书子集
		String fieldSetId = bo.getCertSubset();
		String certNOItemId = bo.getCertNOItemId();
		String certName = bo.getCertName();
		String certCategoryItemId = bo.getCertCategoryItemId();
		String certBorrowState = bo.getCertBorrowState();
		String certStatus = bo.getCertStatus();
		
		String items = "," + certNOItemId + "," + certCategoryItemId + "," + certName + "," 
				+ certStatus + "," + certBorrowState + ",";
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
		for (int i = 0; i < itemList.size(); i++) {
			ColumnsInfo columnsInfo = (ColumnsInfo) itemList.get(i);
			if(ColumnsInfo.LOADTYPE_ONLYLOAD == columnsInfo.getLoadtype()
					|| certOrg.equalsIgnoreCase(columnsInfo.getColumnId())
					|| StringUtils.isEmpty(columnsInfo.getColumnId()))
				continue;
					
			ArrayList<ColumnsInfo> childColumns = columnsInfo.getChildColumns();
			// 合并列在表头中为hashmap，非hashmap的都为非合并列
			if (!childColumns.isEmpty() && childColumns.size() > 0) {
				// 获取合并列中包含的指标
				headList.addAll(getTemplateHeadList(childColumns, certOrg));
			} else {
				ColumnsInfo info = (ColumnsInfo) itemList.get(i);
				LazyDynaBean bean = new LazyDynaBean();
				String itemid = info.getColumnId();
				if ("a0100".equalsIgnoreCase(itemid) || 4 == info.getLoadtype())
					continue;
				
				FieldItem fi = DataDictionary.getFieldItem(itemid);
				bean.set("itemid", itemid);
				bean.set("comment", itemid);
				if(fieldSetId.equalsIgnoreCase(info.getFieldsetid()) 
						&& (items.toLowerCase().contains(itemid.toLowerCase()) || fi.isFillable()))
					bean.set("content", info.getColumnDesc() + "(必填)");
				else
					bean.set("content", info.getColumnDesc() + "");
					
				bean.set("codesetid", info.getCodesetId());
				bean.set("colType", info.getColumnType());
				bean.set("decwidth", info.getDecimalWidth() + "");
				bean.set("displayWidth", info.getColumnWidth() + "");
				bean.set("colLength", info.getColumnLength() + "");

				String onlyField = this.getOnlyFieldItem().get(0);
				if(onlyField.equalsIgnoreCase(itemid))
				    headList.add(0, bean);
				else
				    headList.add(bean);
				    
			}
		}

		return headList;
	}

	/**
	 * 获取代码型 数据 下拉列表数据集合
	 * 
	 * @param fieldCodeSetId
	 * @return desclist 下拉列表数据集合
	 */
	public ArrayList<String> getItemDescList(String fieldCodeSetId) throws GeneralException {
		ArrayList<String> descList = new ArrayList<String>();
		RowSet rs = null;
		try {
			String tableName = "";
			if ("UN".equalsIgnoreCase(fieldCodeSetId) || "UM".equalsIgnoreCase(fieldCodeSetId)
					|| "@K".equalsIgnoreCase(fieldCodeSetId))
				tableName = "organization";
			else
				tableName = "codeitem";

			StringBuffer sql = new StringBuffer("");
			sql.append("select codeitemdesc from ").append(tableName);
			sql.append(" where codesetid=?");
			sql.append(" and ").append(Sql_switcher.isnull("invalid", "1")).append("='1'");
			sql.append(" and " + Sql_switcher.isnull(Sql_switcher.dateToChar("end_date", "yyyy-MM-dd"), "'9999-12-31'"));
			sql.append(">='" + DateUtils.format(new Date(), "yyyy-MM-dd") + "'");

			ContentDAO dao = new ContentDAO(conn);
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(fieldCodeSetId);
			rs = dao.search(sql.toString(), paramList);
			while (rs.next()) {
				String codeitemdesc = rs.getString("codeitemdesc");
				descList.add(codeitemdesc);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return descList;
	}
	/**
	 * 获取系统设置的唯一性指标
	 * @return
	 */
	public ArrayList<String> getOnlyFieldItem() {
		ArrayList<String> valueList = new ArrayList<String>();
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
		String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");// 身份证
		chk = StringUtils.isEmpty(chk) ? "" : chk;
		String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "valid");
		chkvalid = StringUtils.isEmpty(chkvalid) ? "0" : chkvalid;
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");// 唯一性指标
		onlyname = StringUtils.isEmpty(onlyname) ? "0" : onlyname;
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
		uniquenessvalid = StringUtils.isEmpty(uniquenessvalid) ? "0" : uniquenessvalid;

		String dbprimarykey = "a0101";// 默认为姓名
		if (!("".equals(onlyname)) && !"0".equalsIgnoreCase(uniquenessvalid))
			dbprimarykey = onlyname;
		else if (!("".equals(chk)) && !"0".equalsIgnoreCase(chkvalid))
			dbprimarykey = chk;

		StringBuffer setdb = new StringBuffer();
		if (!"0".equalsIgnoreCase(uniquenessvalid)) {
			String onlynameValue = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "db");
			onlynameValue = StringUtils.isEmpty(onlynameValue) ? "all" : onlynameValue.toLowerCase();
			setdb.append(onlynameValue);
		} else if (!"0".equalsIgnoreCase(chkvalid)) {
			String chkValue = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "db");
			chkValue = StringUtils.isEmpty(chkValue) ? "all" : chkValue.toLowerCase();
			setdb.append(chkValue);
		} else {
			setdb.append("all");
		}
		
		valueList.add(dbprimarykey);
		valueList.add(setdb.toString());
		return valueList;
	}
	/**
	 * 校验数据
	 * @param fieldSetId	子集
	 * @param itemIds		必填项id
	 * @param valueMap		要保存的数据
	 * @return
	 * @throws GeneralException
	 * @author linbz
	 * @date 2020年6月11日下午1:45:00
	 */
	public String checkData(String fieldSetId, String itemIds, MorphDynaBean valueMap) throws GeneralException{
		String msg = "";
		try{
			HashMap<String, Object> hashMap = PubFunc.DynaBean2Map(valueMap);
			for(Map.Entry<String,Object> entry:hashMap.entrySet()){
				String itemId = entry.getKey();
				if(itemIds.contains(","+itemId+",")){
					msg = this.checkParamValue(fieldSetId, itemId, (String)entry.getValue());
					if(StringUtils.isNotBlank(msg)){
						return msg;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return msg;
	}
	/**
	 * 校验保存的值
	 * @param fieldSetId	子集
	 * @param itemId	指标id
	 * @param value		值
	 * @return
	 * @throws GeneralException
	 * @author linbz
	 * @date 2020年6月11日下午1:45:00
	 */
	public String checkParamValue(String fieldSetId, String itemId, String value) throws GeneralException{
		String msg = "";
		try{
			// 增加正则校验空白字符
			Pattern p = Pattern.compile("\\s|\\t|\\r|\\n");
			FieldItem fi = DataDictionary.getFieldItem(itemId, fieldSetId);
			if (StringUtils.isBlank(value)) {
				msg = fi.getItemdesc() + "的值不能为空！";
			} else if(p.matcher(value).find()){
				msg = fi.getItemdesc() + "的值不能有空白字符！";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return msg;
	}
	
}
