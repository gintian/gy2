package com.hjsj.hrms.transaction.gz.premium.param;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.ht.ContractBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p> Title:SearchBonusParamTrans.java </p>
 * <p> Description:奖金参数 </p>
 * <p> Company:hjsj </p>
 * <p> create time:2009-07-02 13:00:00 </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchParamTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ContractBo bo = new ContractBo(this.frameconn, this.userView);
		ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
		CommonData noItem = new CommonData("", "");
		// 人员信息集已构库的子集
		// 奖金子集
		ArrayList setidList = new ArrayList();
		setidList.add(noItem);
		// 获得奖金分配子集
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		for (int i = 0; i < list.size(); i++) {
			FieldSet fieldset = (FieldSet) list.get(i);
			if ("0".equalsIgnoreCase(fieldset.getUseflag()))
				continue;
			if ("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if ("B01".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if (!"1".equalsIgnoreCase(fieldset.getChangeflag()))
				continue;
			ArrayList checklist = this.userView.getPrivFieldList(fieldset.getFieldsetid(), Constant.USED_FIELD_SET);
			if (checklist.size() < 1)
				continue;
			String fieldsetid = fieldset.getFieldsetid();

			// if (map.size() > 0 && map.get("51") != null && map.get("49") !=
			// null && map.get("50") != null && map.get("45") != null &&
			// map.get("N") != null && map.get("D") != null)
			// {
			CommonData temp = new CommonData(fieldset.getFieldsetid(), fieldset.getCustomdesc());
			setidList.add(temp);
			// }
		}
		// 奖金子集
		String setid = "";
		setid = hm.get("fieldsetid") == null ? "" : (String) hm.get("fieldsetid");
		hm.remove("fieldsetid");
		if ("".equals(setid)) {
			setid = xml.getNodeAttributeValue("/Params/BONUS_SET", "setid");
		} else if ("-1".equals(setid)) {
			setid = "";
		}
		// 单位登记表号
		String cardid = xml.getNodeAttributeValue("/Params/BONUS_SET", "cardid");
		String sql = "select tabid,name from rname where upper(flaga) like 'B%' order by tabid";
		ArrayList cardidList = new ArrayList();
		cardidList.add(noItem);
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				CommonData temp = new CommonData("" + this.frowset.getInt("tabid"), this.frowset.getString("name").toString());
				cardidList.add(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 人员月奖表共享工资类别
		String salaryid = xml.getNodeAttributeValue("/Params/BONUS_SET", "salaryid");
		ArrayList salaryidList = new ArrayList();
		salaryidList.add(noItem);
		StringBuffer buf = new StringBuffer();
		SalaryCtrlParamBo ctrlparam = null;
		try {
			buf.append("select salaryid,cname,cbase,cond,seq from salarytemplate ");
			buf.append(" where (cstate is null or cstate='')");// 薪资类别
			RowSet rset = dao.search(buf.toString() + " order by seq");
			while (rset.next()) {
				/** 加上权限过滤 */
				if (!this.userView.isHaveResource(IResourceConstant.GZ_SET, rset.getString("salaryid")))
					continue;
				CommonData temp = new CommonData("" + rset.getString("salaryid"), rset.getString("cname"));
				ctrlparam = new SalaryCtrlParamBo(this.frameconn, rset.getInt("salaryid"));
				String manager = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user"); // 工资管理员，对共享类别有效

				/** 升级薪资表 */
				if (manager.length() == 0)// 不共享
					continue;
				else
					// 共享
					salaryidList.add(temp);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		if ("".equals(setid)) {
			this.getFormHM().put("setid", setid);
			this.getFormHM().put("dist_field", "");
			this.getFormHM().put("rep_field", "");
			this.getFormHM().put("keep_save_field", "");
			this.getFormHM().put("bonus_sum_field", "");
			this.getFormHM().put("dist_sum_field", "");
			this.getFormHM().put("surplus_field", "");
			this.getFormHM().put("setidList", setidList);
			this.getFormHM().put("dist_fieldList", new ArrayList());
			this.getFormHM().put("cardid", "");
			this.getFormHM().put("salaryid", "");
			this.getFormHM().put("rep_fieldList", new ArrayList());
			this.getFormHM().put("keep_save_fieldList", new ArrayList());
			this.getFormHM().put("bonus_sum_fieldList", new ArrayList());
			this.getFormHM().put("dist_sum_fieldList", new ArrayList());
			this.getFormHM().put("surplus_fieldList", new ArrayList());
			this.getFormHM().put("cardidList", new ArrayList());
			this.getFormHM().put("salaryidList", new ArrayList());

			// 人员库
			this.getFormHM().put("stat_dbpre", "");
			String stat_dbpreStr = xml.getNodeAttributeValue("/Params/BONUS_SET", "stat_dbpre");
			String[] stat_dbpreArray = stat_dbpreStr.split(",");
			HashMap stat_dbpreMap = new HashMap();
			for (int i = 0; i < stat_dbpreArray.length; i++) {
				// if (stat_dbpreArray[i].trim().length() > 0)
				// stat_dbpreMap.put(stat_dbpreArray[i].toLowerCase(),
				// stat_dbpreArray[i]);
			}
			ArrayList stat_dbpreList = bo.searchNbase(stat_dbpreMap);
			this.getFormHM().put("stat_dbpre", stat_dbpreList);
		} else {
			this.getFormHM().put("setid", setid);
			ArrayList fieldlist = setidList;
			// 下发标识指标
			String dist_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "dist_field");
			this.getFormHM().put("dist_field", dist_field);
			// 上报标识指标
			String rep_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "rep_field");
			this.getFormHM().put("rep_field", rep_field);
			// 封存标示指标
			String keep_save_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "keep_save_field");
			this.getFormHM().put("keep_save_field", keep_save_field);
			// 奖金总额指标
			String bonus_sum_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "bonus_sum_field");
			this.getFormHM().put("bonus_sum_field", bonus_sum_field);
			// 下发奖金总额指标
			String dist_sum_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "dist_sum_field");
			this.getFormHM().put("dist_sum_field", dist_sum_field);
			// 节余指标
			String surplus_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "surplus_field");
			this.getFormHM().put("surplus_field", surplus_field);
			// 奖金核算单位标识指标
			String checkUn_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "checkUn_field");
			this.getFormHM().put("checkUn_field", checkUn_field == null ? "" : checkUn_field);

			this.getFormHM().put("cardid", cardid);
			this.getFormHM().put("salaryid", salaryid);

			ArrayList dist_fieldList = new ArrayList();
			ArrayList rep_fieldList = new ArrayList();
			ArrayList keep_save_fieldList = new ArrayList();
			ArrayList bonus_sum_fieldList = new ArrayList();
			ArrayList dist_sum_fieldList = new ArrayList();
			ArrayList surplus_fieldList = new ArrayList();

			dist_fieldList.add(noItem);
			rep_fieldList.add(noItem);
			keep_save_fieldList.add(noItem);
			bonus_sum_fieldList.add(noItem);
			dist_sum_fieldList.add(noItem);
			surplus_fieldList.add(noItem);
			// for (int i = 0; i < fieldlist.size(); i++)
			// {
			// CommonData item = (CommonData) fieldlist.get(i);
			String fieldsetid = setid;
			if (!"".equals(fieldsetid)) {
				ArrayList fieldList2 = DataDictionary.getFieldList(fieldsetid, Constant.USED_FIELD_SET);
				if (fieldList2 != null) {
					for (int j = 0; j < fieldList2.size(); j++) {
						FieldItem fieldItem = (FieldItem) fieldList2.get(j);
						String itemid = fieldItem.getItemid();
						String itemtype = fieldItem.getItemtype();
						String codesetId = fieldItem.getCodesetid();
						if (!"45".equals(codesetId)) {
							if (!"N".equalsIgnoreCase(itemtype)) {
								continue;
							}
							CommonData temp = new CommonData(fieldItem.getItemid(), fieldItem.getItemdesc());
							bonus_sum_fieldList.add(temp);
							dist_sum_fieldList.add(temp);
							surplus_fieldList.add(temp);
							// continue;
						} else {
							CommonData temp = new CommonData(fieldItem.getItemid(), fieldItem.getItemdesc());
							dist_fieldList.add(temp);
							rep_fieldList.add(temp);
							keep_save_fieldList.add(temp);
							bonus_sum_fieldList.add(temp);
							dist_sum_fieldList.add(temp);
							surplus_fieldList.add(temp);
						}
					}
				}

			}
			// 人员库
			String stat_dbpreStr = xml.getNodeAttributeValue("/Params/BONUS_SET", "stat_dbpre");
			String[] stat_dbpreArray = stat_dbpreStr.split(",");
			HashMap stat_dbpreMap = new HashMap();
			for (int i = 0; i < stat_dbpreArray.length; i++) {
				if (stat_dbpreArray[i].trim().length() > 0)
					stat_dbpreMap.put(stat_dbpreArray[i].toLowerCase(), stat_dbpreArray[i]);
			}

			ArrayList stat_dbpreList = bo.searchNbase(stat_dbpreMap);
			this.getFormHM().put("stat_dbpre", stat_dbpreList);
			this.getFormHM().put("setidList", setidList);
			this.getFormHM().put("dist_fieldList", dist_fieldList);
			this.getFormHM().put("rep_fieldList", rep_fieldList);
			this.getFormHM().put("keep_save_fieldList", keep_save_fieldList);
			this.getFormHM().put("bonus_sum_fieldList", bonus_sum_fieldList);
			this.getFormHM().put("dist_sum_fieldList", dist_sum_fieldList);
			this.getFormHM().put("surplus_fieldList", surplus_fieldList);
			this.getFormHM().put("cardidList", cardidList);
			this.getFormHM().put("salaryidList", salaryidList);
		}
	}
}
