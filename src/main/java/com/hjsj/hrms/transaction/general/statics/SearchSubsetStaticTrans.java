package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;

public class SearchSubsetStaticTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList list = new ArrayList();
		ArrayList rightlist = new ArrayList();
		String setname = (String) this.getFormHM().get("tablename");
		if ("hr_emp_hisdata".equals(setname)) {
			try {
				ContentDAO dao = new ContentDAO(this.frameconn);
				StringBuffer sql = new StringBuffer();
				sql.append("select * from hr_emp_hisdata where 1=2");
				this.frowset = dao.search(sql.toString());
				ResultSetMetaData rsmd = this.frowset.getMetaData();
				int size = rsmd.getColumnCount();
				for (int i = 1; i <= size; i++) {
					String itemid = rsmd.getColumnName(i).toUpperCase();
					if ("ID".equals(itemid) || "NBASE".equals(itemid)||"A0100".equals(itemid))
						continue;
					if ("0"
							.equals(this.userView.analyseFieldPriv(itemid)))
						continue;
					FieldItem fi = DataDictionary.getFieldItem(itemid);
					if (fi != null) {
						if ("M".equalsIgnoreCase(fi.getItemtype()))
							continue;
						CommonData obj = new CommonData(fi.getItemid(), fi
								.getItemdesc());
						list.add(obj);
					}
				}
				this.getFormHM().put("fieldlist", list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String dis = setname.substring(1, 3);
			SaveInfo_paramXml sp = new SaveInfo_paramXml(this.getFrameconn());
			String rightstr = sp.getInfo_paramNode("browser");
			String[] rightss = rightstr.split(",");
			for (int m = 0; m < rightss.length; m++) {
				if (rightss[m].length() > 0) {
					FieldItem fi = DataDictionary.getFieldItem(rightss[m]);
					CommonData obj = new CommonData(fi.getItemid(), fi
							.getItemdesc());
					rightlist.add(obj);
				}
			}
			// System.out.println(rightlist);
			ArrayList fielditemlist = DataDictionary.getFieldList(setname,
					Constant.USED_FIELD_SET);
			try {
				for (int i = 0; i < fielditemlist.size(); i++) {
					FieldItem fielditem = (FieldItem) fielditemlist.get(i);
					if ("0"
							.equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
						continue;
					if ("M".equalsIgnoreCase(fielditem.getItemtype()))
						continue;
					CommonData dataobj = new CommonData();
					dataobj = new CommonData(fielditem.getItemid(), fielditem
							.getItemdesc());
					list.add(dataobj);
				}
				this.getFormHM().clear();
				this.getFormHM().put("fieldlist", list);
				this.getFormHM().put("rightlist", rightlist);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			} finally {
				if ("01".equals(dis)) {
					this.getFormHM().put("disp", "1");
					// System.out.println("--s-"+dis);
				} else {
					this.getFormHM().put("disp", "0");
					// System.out.println("--0-"+dis);
				}
			}
		}

	}

}
