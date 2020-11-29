/**
 * 试卷添加保存 LiWeichao 2011-10-25 16:16:19
 */
package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SaveExamPaperTrans extends IBusiness {


	public void execute() throws GeneralException {
		List itemlist = (List) this.getFormHM().get("itemlist");
		FieldItemView fieldItem = null;
		RecordVo rv = new RecordVo("r53");
		for (int i = 0; i < itemlist.size(); i++) {
			fieldItem = (FieldItemView) itemlist.get(i);
			if ("D".equals(fieldItem.getItemtype())) {
				rv.setDate(fieldItem.getItemid(), fieldItem.getValue());
			} else if ("N".equals(fieldItem.getItemtype())){
				String temp=fieldItem.getValue();
				temp=temp==null||temp.length()<1?"0":temp;
				rv.setDouble(fieldItem.getItemid(), Double.parseDouble(temp));
			} else {
				rv.setString(fieldItem.getItemid(), fieldItem.getValue());
			}
		}
		ContentDAO contentDAO = new ContentDAO(this.getFrameconn());
		String r5300 = (String) this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		this.getFormHM().remove("r5300");
		if (r5300 != null && r5300.trim().length() > 0) {
			rv.setInt("r5300", Integer.parseInt(r5300));
			try {
				contentDAO.updateValueObject(rv);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String str = idg.getId("R53.R5300");
			rv.setInt("r5300", Integer.parseInt(str));
			rv.setInt("norder", getMaxNorder(contentDAO)+1);
			rv.setDate("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
			rv.setString("create_user", this.userView.getA0100());
			contentDAO.addValueObject(rv);
		}
	}

	private int getMaxNorder(ContentDAO dao){
		int i=0;
		String sql = "select max(norder) a from r53";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next())
				i = this.frowset.getInt("a");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
}
