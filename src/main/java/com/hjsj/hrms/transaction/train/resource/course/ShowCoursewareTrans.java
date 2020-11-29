/**
 * 
 */
package com.hjsj.hrms.transaction.train.resource.course;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:ShowCoursewareTrans
 * </p>
 * <p>
 * Description:浏览培训课程课件
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class ShowCoursewareTrans extends IBusiness {

	/**
	 * 
	 */
	public ShowCoursewareTrans() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String r5100 = (String) hm.get("r5100");
		r5100 = r5100 != null ? r5100 : "";
		hm.remove("r5100");
		List itemlist = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("r51");
			vo.setString("r5100", r5100);
			vo = dao.findByPrimaryKey(vo);
			List fieldList = DataDictionary.getFieldList("R51",
					Constant.ALL_FIELD_SET);
			for (int i = 0; i < fieldList.size(); i++) {
				FieldItem fielditem = (FieldItem) fieldList.get(i);
				if ("r5105".equalsIgnoreCase(fielditem.getItemid())) {
					RecordVo rv = new RecordVo("codeitem");
					rv.setString("codesetid", "57");
					rv.setString("codeitemid", vo.getString(fielditem.getItemid()));
					try{
					rv = dao.findByPrimaryKey(rv);
					fielditem.setValue(rv.getString("codeitemdesc"));
					}catch(Exception e){
						fielditem.setValue("");
					}
					itemlist.add(fielditem);
				} else {
					fielditem.setValue(vo.getString(fielditem.getItemid()));
					itemlist.add(fielditem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("itemlist", itemlist);
		}
	}

}
