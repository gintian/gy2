package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.List;

public class SaveExamStudentTrans extends IBusiness {


	public void execute() throws GeneralException {
		List itemlist = (List) this.getFormHM().get("fieldlist");
		FieldItemView fieldItem = null;
		RecordVo rv = new RecordVo("r55");
		for (int i = 0; i < itemlist.size(); i++) {
			fieldItem = (FieldItemView) itemlist.get(i);
			if ("D".equals(fieldItem.getItemtype())) {
				rv.setDate(fieldItem.getItemid(), fieldItem.getValue());
			} else if ("N".equals(fieldItem.getItemtype())){
				String temp=fieldItem.getValue();
				//temp=temp==null||temp.length()<1?"0":temp;
				if(temp!=null&&temp.length()>0)
					rv.setDouble(fieldItem.getItemid(), Double.parseDouble(temp));
			} else {
				rv.setString(fieldItem.getItemid(), fieldItem.getValue());
			}
		}
		ContentDAO contentDAO = new ContentDAO(this.getFrameconn());
		try {
			contentDAO.updateValueObject(rv);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
