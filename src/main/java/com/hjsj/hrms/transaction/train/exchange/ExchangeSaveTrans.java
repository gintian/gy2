package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ExchangeSaveTrans extends IBusiness {

	public void execute() throws GeneralException {
		//HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		List itemlist = (List) this.getFormHM().get("itemList");
		
		FieldItemView fieldItem = null;
		RecordVo rv = new RecordVo("r57");
		for (int i = 0; i < itemlist.size(); i++) {
			fieldItem = (FieldItemView) itemlist.get(i);
			if ("D".equals(fieldItem.getItemtype())) {
				rv.setDate(fieldItem.getItemid(), fieldItem.getValue());
			} else if ("N".equals(fieldItem.getItemtype())){
				String temp=fieldItem.getValue();
				temp=temp==null||temp.length()<1?"0":temp;
				if(!"".equals(fieldItem.getItemid()) && null != fieldItem.getItemid()){					
					rv.setDouble(fieldItem.getItemid(), Double.parseDouble(temp));
				}
			} else {
				//if(!"".equals(fieldItem.getItemid()) && null != fieldItem.getItemid()){					
					rv.setString(fieldItem.getItemid(), fieldItem.getValue());
				//}else{
				//	rv.setString("b0110", fieldItem.getValue());
				//}
			}
		}
		ContentDAO contentDAO = new ContentDAO(this.getFrameconn());
		String r5701 = (String) this.getFormHM().get("r5701");
		this.getFormHM().remove("r5701");
		if (r5701 != null && r5701.trim().length() > 0) {
			rv.setString("r5701", r5701);
			try {
				contentDAO.updateValueObject(rv);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String str = idg.getId("R57.R5701");
			rv.setString("r5713","01");//课程状态
			rv.setInt("r5701", Integer.parseInt(str));
			rv.setDate("createtime", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss.SSS"));
			rv.setString("createuser", this.userView.getA0100());
			contentDAO.addValueObject(rv);
		}
	}
	
}
