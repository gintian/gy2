/**
 * 
 */
package com.hjsj.hrms.transaction.sys.dataimport;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.dataimport.DataImportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title:DataImportTrans
 * </p>
 * <p>
 * Description:展现参数设置列表
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-07-02
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class DataImportTrans extends IBusiness {

	public void execute() throws GeneralException {

		// 获得需要导入的ID
		String ids = (String) this.getFormHM().get("id");
		String[] id = ids.split(",");

		// 导入是否成功
		boolean flag = false;

		// 获取所有参数
		ConstantXml constantXml = new ConstantXml(this.frameconn, "IMPORTINFO",
				"params");
		DataImportBo bo = new DataImportBo(this.frameconn);
		try {
			for (int i = 0; i < id.length; i++) {
				Map connMap = new HashMap();
				// 名称
				String name = constantXml.getTextValue("/params/param[@id='"
						+ id[i] + "']/name");

				// 人员库
				String nbase = constantXml.getTextValue("/params/param[@id='"+id[i]+"']/nbase");
				
				// 数据库类型
				String dbType = constantXml.getTextValue("/params/param[@id='"
						+ id[i] + "']/dbtype");
				connMap.put("dbType", dbType);

				// 数据库URL
				String dbUrl = constantXml.getTextValue("/params/param[@id='"
						+ id[i] + "']/dburl");
				connMap.put("dbUrl", dbUrl);

				// 数据库用户名
				String userName = constantXml
						.getTextValue("/params/param[@id='" + id[i] + "']/dbuser");
				connMap.put("userName", userName);

				// 数据库密码
				String password = constantXml
						.getTextValue("/params/param[@id='" + id[i] + "']/dbpwd");
				connMap.put("password", password);

				// 作业类
				String jobClass = constantXml
						.getTextValue("/params/param[@id='" + id[i]
								+ "']/jobclass");

				// 是否启用
				String enable = constantXml.getTextValue("/params/param[@id='"
						+ id[i] + "']/enable");

				// ehr表名
				String ehrTable = constantXml
						.getTextValue("/params/param[@id='" + id[i]
								+ "']/mappings/ehrtable");

				// 外部表名
				String extTable = constantXml
						.getTextValue("/params/param[@id='" + id[i]
								+ "']/mappings/exttable");

				// hr关联指标
				String hrRelation = constantXml.getNodeAttributeValue(
						"/params/param[@id='" + id[i] + "']/mappings/relation",
						"hr");

				// 外部系统关联指标
				String extRelation = constantXml.getNodeAttributeValue(
						"/params/param[@id='" + id[i] + "']/mappings/relation",
						"ext");

				// 外部系统过滤条件
				String srcTabCond = constantXml
						.getTextValue("/params/param[@id='" + id[i]
								+ "']/mappings/srctabcond");
				/* xml里保存公式<>符号是经过转码的，此处需要转回来，否则sql报错 guodd 2019-05-08*/
				srcTabCond = PubFunc.toReplaceStr(srcTabCond);
				
				// hr数据保护条件
				String tagTabCond = constantXml
						.getTextValue("/params/param[@id='" + id[i]
								+ "']/mappings/tagtabcond");
				/* xml里保存公式<>符号是经过转码的，此处需要转回来，否则sql报错 guodd 2019-05-08*/
				tagTabCond = PubFunc.toReplaceStr(tagTabCond);
				
				// 指标关联
				List list = constantXml.getAllChildren("/params/param[@id='"
						+ id[i] + "']/mappings");

				// 字段关联关系
				Map refMap = new HashMap();
				// 主键列表
				ArrayList keyList = new ArrayList();
				// ehr字段集合
				ArrayList ehrFields = new ArrayList();
				// 外部系统字段集合
				ArrayList extFields = new ArrayList();
				// 主键集合
				Map keyMap = new HashMap();
				// 默认值
				Map defMap = new HashMap();
				
				ArrayList ehrAllFields = new ArrayList();

				for (int j = 0; j < list.size(); j++) {
					Element el = (Element) list.get(j);
					if ("fieldref".equals(el.getName())) {
						String hrfield = el.getAttributeValue("hrfield");
						String extfield = el.getAttributeValue("extfield");
						String ispk = el.getAttributeValue("ispk");
						String defvalue = el.getAttributeValue("defaultvalue");
						
						ehrAllFields.add(hrfield);
						
						if (extfield != null && extfield.trim().length() > 0) {
							refMap.put(extfield, hrfield);
							extFields.add(extfield);
							ehrFields.add(hrfield);
						}
						
						if ((defvalue != null && defvalue.trim().length() > 0)) {
							ehrFields.add(hrfield);
						}

						if ("1".equals(ispk)) {
							keyMap.put(hrfield, ispk);
							keyList.add(hrfield);
						}

						if (defvalue != null && defvalue.trim().length() > 0) {
							defMap.put(hrfield, defvalue);
						}

					}
				}

				// 创建外部系统数据库连接
				if ("1".equals(enable)) {
					//需要给flag 参数值 为 dataImport  不给值会覆盖临时表 b0110 e0122 e01a1 值，导致同步值不对  wangb 20191107
					bo.importData(nbase, connMap, ehrTable, extTable,
							hrRelation, extRelation, srcTabCond, tagTabCond,
							refMap, keyList, ehrFields, extFields, keyMap,
							defMap, ehrAllFields, "dataImport", null);

				}

			}

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;

		}
		if (flag) {
			this.getFormHM().put("flag", "true");
		} else {
			this.getFormHM().put("flag", "false");
		}

	}
}
