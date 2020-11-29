package com.hjsj.hrms.transaction.gz.voucher;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author xuchangshun
 * */
public class SetGroupTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String pn_id = (String) this.getFormHM().get("pn_id");//凭证id
			String fl_id = "";//凭证分录id
			String itemflag = (String) this.getFormHM().get("itemflag");
			itemflag = itemflag != null && itemflag.trim().length() > 0 ? itemflag: "0";//初次进入 取薪资类别id最小的作为当前显示的薪资类别 itemflag=0 初次进入
			if ("0".equals(itemflag)) {
				HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
				fl_id = (String) hm.get("fl_id");
			} else {
				fl_id = (String) this.getFormHM().get("fl_id");
			}
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = salaryid != null && salaryid.trim().length() > 0 ? salaryid: "0";
			
			ArrayList salarySetList = new ArrayList();//分录分组指标中所选的薪资类别
			ArrayList salaryItemList = new ArrayList();//每个薪资类别下所对应的薪资类别指标
			ArrayList cgroupList = new ArrayList();//分录分组指标中已经选定的薪资类别指标
			ArrayList tempList = new ArrayList();//临时做数据处理
			String c_group = "";
			String[] cgroupArray = null;
			String salarySetValue = "";
			String[] salarySetArray = null;
			
			String sql = "select c_scope from gz_warrant where pn_id='" + pn_id+ "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while (frowset.next()) {
				salarySetValue = frowset.getString(1);
			}
			salarySetArray = salarySetValue.split(",");
			for (int i = 0; i < salarySetArray.length; i++) {
				sql = "select distinct cname,salaryid from salarytemplate where salaryid ='"
						+ salarySetArray[i] + "'";
				this.frowset = dao.search(sql);
				while (frowset.next()) {
					CommonData temp = new CommonData(frowset.getString(2),
							frowset.getString(1));
					salarySetList.add(temp);
				}
				this.getFormHM().put("salarySetList", salarySetList);
			}
			sql = "select c_group from gz_warrantlist where pn_id='" + pn_id
					+ "'and fl_id='" + fl_id + "'";
			this.frowset = dao.search(sql);
			while (frowset.next()) {
				c_group = frowset.getString(1);
				if (!("".equals(c_group) || c_group == null)) {
					cgroupArray = c_group.split(",");
					for (int i = 0; i < cgroupArray.length; i++) {
						if (tempList.contains(cgroupArray[i])) {
							continue;
						}
						tempList.add(cgroupArray[i]);
					}
				}
			}
			if ("0".equals(itemflag)) {
				if (salarySetList != null && salarySetList.size() > 0)
					salaryid = ((CommonData) salarySetList.get(0))
							.getDataValue();
				String itemsql = "select itemid from salaryset where itemtype='A' and salaryid='"
						+ salaryid + "' and UPPER(itemid) not in('A0100','A0000')";
				CommonData temp = new CommonData();
				this.frowset = dao.search(itemsql);
				while (frowset.next()) {
					FieldItem item = DataDictionary.getFieldItem(frowset.getString(1).toLowerCase());
					if(item==null){
					    continue;
					}
					temp = new CommonData(frowset.getString(1),item.getItemdesc());
					salaryItemList.add(temp);
				}
				for (int i = 0; i < tempList.size(); i++) {

					String tt=(String)tempList.get(i);
					FieldItem item = DataDictionary.getFieldItem(tt.toLowerCase());
					temp = new CommonData(tt,item.getItemdesc());
					cgroupList.add(temp);
					
				}
				this.getFormHM().put("cgroupList", cgroupList);
				this.getFormHM().put("pn_id", pn_id);
				this.getFormHM().put("fl_id", fl_id);
				this.getFormHM().put("salaryItemList", salaryItemList);
			} else {
				String itemsql = "select itemid,itemdesc from salaryset where itemtype='A' and salaryid='"
						+ salaryid + "'and UPPER(itemid) not in('A0100')";
				CommonData temp = new CommonData();
				this.frowset = dao.search(itemsql);
				while (frowset.next()) {
					FieldItem item = DataDictionary.getFieldItem(frowset.getString(1).toLowerCase());
					temp = new CommonData(frowset.getString(1),item.getItemdesc());
					salaryItemList.add(temp);
				}
				for (int i = 0; i < tempList.size(); i++) {
					String tt=(String)tempList.get(i);
					FieldItem item = DataDictionary.getFieldItem(tt.toLowerCase());
					if(item==null){
                        continue;
                    }
					temp = new CommonData(tt,item.getItemdesc());
					cgroupList.add(temp);
				}
				this.getFormHM().put("cgroupList", cgroupList);
				this.getFormHM().put("pn_id", pn_id);
				this.getFormHM().put("fl_id", fl_id);
				this.getFormHM().put("salaryItemList", salaryItemList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
