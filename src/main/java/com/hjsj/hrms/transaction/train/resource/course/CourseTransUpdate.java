/**
 * 
 */
package com.hjsj.hrms.transaction.train.resource.course;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:CourseTransUpdate
 * </p>
 * <p>
 * Description:更新培训课程记录
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
public class CourseTransUpdate extends IBusiness {

	/**
	 * 
	 */
	public CourseTransUpdate() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		List selectedRecordVos = (List)hm.get("data_table_record");
		ContentDAO cd = new ContentDAO(this.getFrameconn());
		try {
			cd.updateValueObject(selectedRecordVos);
		} catch (SQLException e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally{
			
		}
	}

}
