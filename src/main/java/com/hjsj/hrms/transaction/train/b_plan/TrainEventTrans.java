package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class TrainEventTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String id = (String)hm.get("id");
		id=id!=null?id:"";
		hm.remove("id");
		
		String classid = (String) hm.get("classid");
		if(classid != null && classid.length() > 0)
		    classid = PubFunc.decrypt(SafeCode.decode(classid));
		
		String content = "";
		try {
		    if (!this.userView.isSuper_admin()) {
                String where = "";
                where = TrainCourseBo.getUnitIdByBusiStrWhere(this.userView);

                String msg = TrainClassBo.checkclass(classid, this.frameconn, where);

                if (msg.length() > 1) {
                    String mes = msg + ResourceFactory.getProperty("train.job.class.nopiv");
                    throw GeneralExceptionHandler.Handle(new Exception(mes));
                }
            }
		    
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			if(content.trim().length()<1){
				StringBuffer sqlstr = new StringBuffer();
				sqlstr.append("select ");
				sqlstr.append(id);
				sqlstr.append(" from r31");
				sqlstr.append(" where r3101='" + classid + "'");

				this.frowset = dao.search(sqlstr.toString());

				if(this.frowset.next()){
					content = this.frowset.getString(1);
					content=content!=null?content:"";
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("contentEv", content);
		this.getFormHM().put("contentid", id);
	}

}
