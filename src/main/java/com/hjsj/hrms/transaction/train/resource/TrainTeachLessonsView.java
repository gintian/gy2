package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 查询教师教授的课程
 * @author Administrator
 *
 */
public class TrainTeachLessonsView extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");

		String teacherid = (String) hm.get("teacherid");
		hm.remove("teacherid");

		if(teacherid != null && teacherid.length() > 0 && !"null".equalsIgnoreCase(teacherid))
		    teacherid = PubFunc.decrypt(SafeCode.decode(teacherid));
		
		StringBuffer sql = new StringBuffer();
		ArrayList fieldlist = new ArrayList();
		ArrayList fields = new ArrayList();
		String columns = "";

		FieldItem fielditem = DataDictionary.getFieldItem("r3130", "r31");
		fields.add(fielditem);
		sql.append("select r31.r3130 r3130");
		columns += fielditem.getItemid();
		fieldlist = DataDictionary.getFieldList("r41", 1);
		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItem field = (FieldItem) fieldlist.get(i);
            if ("r4105".equals(field.getItemid()) || "r4108".equals(field.getItemid())
                    || "r4110".equals(field.getItemid()) || "r4112".equals(field.getItemid())) {
				fields.add(field);
				sql.append("," + field.getItemid());
				columns += "," + field.getItemid();
			}
		}

		sql.append(" from r41 join r31 on r41.r4103=r3101 where exists(");
		sql.append(" select 1 from r04 where r41.r4106=r04.r0401");
		sql.append(" ) and r41.r4106='");
		sql.append(teacherid);
		sql.append("'");

		String teachername = "";
		ContentDAO dao = new ContentDAO(this.frameconn);

		try {
			this.frowset = dao.search("select r0402 from r04 where r0401='" + teacherid + "'");
			if (this.frowset.next())
				teachername = this.frowset.getString("r0402");
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.getFormHM().put("fields", fields);
		this.getFormHM().put("strsql", sql.toString());
		this.getFormHM().put("strwhere", "");
		this.getFormHM().put("columns", columns);
		this.getFormHM().put("primaryField", "r4103");
		this.getFormHM().put("teachername", teachername);
	}

}
