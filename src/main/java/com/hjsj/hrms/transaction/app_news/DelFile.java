package com.hjsj.hrms.transaction.app_news;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

public class DelFile extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList list = (ArrayList)this.getFormHM().get("affixsellist");
		ContentDAO dao = new ContentDAO(this.frameconn);
		RecordVo vo = new RecordVo("appoint_news_ext_file");
		try {
			for(int i=0;i<list.size();i++){
				LazyDynaBean bean =(LazyDynaBean)list.get(i);
				String file_id = (String)bean.get("ext_file_id");
				vo.setString("ext_file_id",file_id);
				vo = dao.findByPrimaryKey(vo);
				dao.deleteValueObject(vo);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
