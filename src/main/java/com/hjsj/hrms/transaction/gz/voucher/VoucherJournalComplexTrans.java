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
/**
 * 
 * 类名称:VoucherJournalComplexTrans
 * 类描述:处理计算公式和限制条件的交易类
 * 创建人: xucs
 * 创建时间:2013-8-23 上午11:57:40 
 * 修改时间:xucs
 * 修改时间:2013-8-23 上午11:57:40
 * 修改备注:
 * @version
 *
 */
public class VoucherJournalComplexTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String pn_id = (String) this.getFormHM().get("pn_id");//pn_id 凭证id
			String fl_id = (String) this.getFormHM().get("fl_id");// fl_id 凭证分录id
			
			String itemflag = (String) this.getFormHM().get("itemflag");// itemflag 0代表初次访问默认加载第一项薪资类别以及对应的可选项
			itemflag = itemflag != null && itemflag.trim().length() > 0 ? itemflag: "0";
			
			String salaryid = (String) this.getFormHM().get("salaryid");//薪资类别id
			salaryid = salaryid != null && salaryid.trim().length() > 0 ? salaryid: "0";
			
			String clsflag = (String) this.getFormHM().get("clsflag");// clsflag 1代表是要做的计算公式 2代表是限制条件
			String c_itemsql = "";//c_itemsql 计算公式表达式
			String c_where = "";// c_where 限制条件表达式
			ArrayList salarySetList = new ArrayList();//salarySetList 存放薪资类别
			ArrayList salaryItemList = new ArrayList();//salaryItemList 存放薪资类别指标项
			String salarySetValue = "";
			String[] salarySetArray = null;
			/**
			 * 获得界面上的薪资类别
			 * */
			String sql = "select c_scope from gz_warrant where pn_id='" + pn_id+ "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while (frowset.next()) {
				salarySetValue = frowset.getString(1);
			}
			salarySetArray = salarySetValue.split(",");
			for (int i = 0; i < salarySetArray.length; i++) {
				sql = "select distinct cname,salaryid from salarytemplate where  salaryid ='"
						+ salarySetArray[i] + "'";
				this.frowset = dao.search(sql);
				while (frowset.next()) {
					CommonData temp = new CommonData(frowset.getString(2),
							frowset.getString(1));
					salarySetList.add(temp);
				}
				this.getFormHM().put("salarySetList", salarySetList);
			}
			/**
			 * 用来改变可选的指标项
			 * */
			if ("0".equals(itemflag)) {
				if (salarySetList != null && salarySetList.size() > 0)
					salaryid = ((CommonData) salarySetList.get(0))
							.getDataValue();
				String itemsql = "select itemid,itemdesc from salaryset where salaryid='"
						+ salaryid + "'and UPPER(itemid) not in('A0100','A0000')";
				CommonData temp = new CommonData();
				temp = new CommonData("", "");
				salaryItemList.add(temp);

				this.frowset = dao.search(itemsql);
				while (frowset.next()) {
				    FieldItem item = DataDictionary.getFieldItem(frowset.getString(1).toLowerCase());
				    if(item==null|| "0".equals(item.getUseflag())){
				        continue;
				    }
					temp = new CommonData(frowset.getString(1),item.getItemdesc());
					salaryItemList.add(temp);
				}
				this.getFormHM().put("salaryItemList", salaryItemList);
				this.getFormHM().put("fl_id", fl_id);
				this.getFormHM().put("pn_id", pn_id);
			} else {
				String itemsql = "select itemid,itemdesc from salaryset where salaryid='"
						+ salaryid + "'and UPPER(itemid) not in('A0100','A0000')";

				CommonData temp = new CommonData();
				temp = new CommonData("", "");
				salaryItemList.add(temp);

				this.frowset = dao.search(itemsql);
				while (frowset.next()) {
				    FieldItem item = DataDictionary.getFieldItem(frowset.getString(1).toLowerCase());
                    if(item==null|| "0".equals(item.getUseflag())){
                        continue;
                    }
                    temp = new CommonData(frowset.getString(1),item.getItemdesc());
					salaryItemList.add(temp);
				}
				this.getFormHM().put("salaryItemList", salaryItemList);
				this.getFormHM().put("fl_id", fl_id);
				this.getFormHM().put("pn_id", pn_id);
				return;// 如果是改变薪资类别查询指标项那么就不用再次判断是显示计算公式还是限制条件
			}
			/**
			 * 用来判断界面上是显示计算公式还是限制条件
			 * */
			if ("1".equals(clsflag)) {
				String itemsql = "select c_itemsql from gz_warrantlist where pn_id='"
						+ pn_id + "'and fl_id='" + fl_id + "'";
				this.frowset = dao.search(itemsql);
				while (frowset.next()) {
					c_itemsql = frowset.getString(1);
				}
				this.getFormHM().put("c_itemsql", c_itemsql);
			} else if ("2".equals(clsflag)) {
				String wheresql = "select c_where from gz_warrantlist where pn_id='"
						+ pn_id + "'and fl_id='" + fl_id + "'";
				this.frowset = dao.search(wheresql);
				while (frowset.next()) {
					c_where = frowset.getString(1);
				}
				this.getFormHM().put("c_where", c_where);
		    } else if ("3".equals(clsflag)) {//本币计算公式
				String itemsql = "select c_extitemsql from gz_warrantlist where pn_id='"
					+ pn_id + "'and fl_id='" + fl_id + "'";
			    this.frowset = dao.search(itemsql);
				while (frowset.next()) {
					c_itemsql = frowset.getString(1);
				}
				if(c_itemsql==null) c_itemsql="";
				this.getFormHM().put("c_itemsql", c_itemsql);
		}
			this.getFormHM().put("clsflag", clsflag);
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
