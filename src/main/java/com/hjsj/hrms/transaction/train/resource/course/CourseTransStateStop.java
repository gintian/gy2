/**
 * 
 */
package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:CourseTransStateStop
 * </p>
 * <p>
 * Description:暂停培训课程记录
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
public class CourseTransStateStop extends IBusiness {

    /**
	 * 
	 */
    public CourseTransStateStop() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */

    public void execute() throws GeneralException {
        HashMap hm = this.getFormHM();
        // String table_name = (String) hm.get("data_table_table");
        // List selectedRecordVos = (List) hm.get("data_table_record");
        // StringBuffer sb = new StringBuffer();
        // RecordVo rv = null;
        // if (selectedRecordVos.size() > 0) {
        // for (int i = 0; i < selectedRecordVos.size() - 1; i++) {
        // rv = (RecordVo) selectedRecordVos.get(i);
        // sb.append(rv.getString("r5000") + ",");
        // }
        // rv = (RecordVo)selectedRecordVos.get(selectedRecordVos.size()-1);
        // sb.append(rv.getString("r5000"));
        // }
        String sel = (String) hm.get("sel");
        String[] sels = PubFunc.keyWord_reback(sel).split(",");
        ArrayList list = new ArrayList();
        String r5000 = "";
        int n = 0;
        for(int i =0; i<sels.length; i++){
            if(n>0)
                r5000 += ",";
            r5000 +=  PubFunc.decrypt(SafeCode.decode(sels[i]));
            n++;
            if(n == 1000){
                list.add(r5000);
                r5000 = "";
                n=0;
            }
            
        }
        
        if(n > 0){
            list.add(r5000);
        }
        ContentDAO cd = new ContentDAO(this.getFrameconn());
        ArrayList sqlList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            String ids = (String) list.get(i);
            String sql = "update r50 set r5022='09' where r5000 in(" + ids + ")";
            sqlList.add(sql);
        }
        
        try {
            cd.batchUpdate(sqlList);
        } catch (SQLException e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        } finally {

        }
    }

}
