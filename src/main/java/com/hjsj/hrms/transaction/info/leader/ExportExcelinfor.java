package com.hjsj.hrms.transaction.info.leader;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * 领导班子选择导出指标查询不同子集中的读写权限指标
 */
public class ExportExcelinfor extends IBusiness {

	public void execute() throws GeneralException {
		String fieldsetid = (String) this.getFormHM().get("fieldsetid");
		
		ArrayList fieldList = new ArrayList();
		ArrayList fieldlist = new ArrayList();
		Connection conn = this.getFrameconn();
		try {
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select fieldsetdesc from fieldSet where fieldsetid='" + fieldsetid + "'";// 获取指标集编码对应的名称
			RowSet rs = dao.search(sql);
			if (rs.next()) {
			}
			fieldsetid = fieldsetid != null && fieldsetid.length() > 0 ? fieldsetid : "A01";
			fieldList = this.userView.getPrivFieldList(fieldsetid);
			if (fieldList != null)
				for (int i = 0; i < fieldList.size(); i++) {
					FieldItem fi = (FieldItem) fieldList.get(i);
					//liuy 2015-1-30 7231：组织机构-领导班子-领导班子-班子成员-导出Excel（主集和子集下面的指标都没有列出来） start
					//if ("1".equalsIgnoreCase(this.userView.analyseFieldPriv(fi.getItemid()))) {// 读权限
					if ("0".equalsIgnoreCase(this.userView.analyseFieldPriv(fi.getItemid()))) {// 是读权限或者写权限的时候才加载子集
						continue;
					}
					//liuy 2015-1-30 end
					CommonData cd = new CommonData(fi.getItemid(), fi.getItemdesc());
					fieldlist.add(cd);
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("fieldlist", fieldlist);
		}
	}

}
