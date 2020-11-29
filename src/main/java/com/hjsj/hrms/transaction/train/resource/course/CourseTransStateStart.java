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
 * Title:CourseTransStateStart
 * </p>
 * <p>
 * Description:发布培训课程记录
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
public class CourseTransStateStart extends IBusiness {

	/**
	 * 
	 */
	public CourseTransStateStart() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
//		String table_name = (String) hm.get("data_table_table");
//		List selectedRecordVos = (List) hm.get("data_table_record");
//		StringBuffer sb = new StringBuffer();
//		RecordVo rv = null;
//		if (selectedRecordVos.size() > 0) {
//			for (int i = 0; i < selectedRecordVos.size() - 1; i++) {
//				rv = (RecordVo) selectedRecordVos.get(i);
//				sb.append(rv.getString("r5000") + ",");
//			}
//			rv = (RecordVo)selectedRecordVos.get(selectedRecordVos.size()-1);
//			sb.append(rv.getString("r5000"));
//		}
		String flag = "ok";
		String sel=(String)hm.get("sel");
		sel = PubFunc.keyWord_reback(sel);
		String[] sels = sel.split(",");
		String id = "";
		int n = 0;
		ArrayList list = new ArrayList();
		for(int i =0; i<sels.length; i++){
            if(n>0)
                id += ",";
            id += PubFunc.decrypt(SafeCode.decode(sels[i]));
            n++;
            if(n == 1000){
                list.add(id);
                id = "";
                n=0;
            }
            
        }
        
        if(n > 0){
            list.add(id);
        }
		ContentDAO cd = new ContentDAO(this.getFrameconn());
		String tmp = checkCourse(list);
		ArrayList sqlList = new ArrayList();
		for(int i = 0; i < list.size(); i++){
		    String ids = (String)list.get(i);
		String sql = "update r50 set r5022='04' where r5000 in("+ids+")";
		sqlList.add(sql);
		}
		try {
			if(tmp==null||tmp.length()<1)
				cd.batchUpdate(sqlList);
			else
				flag = SafeCode.encode(tmp);
		} catch (SQLException e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally{
			this.getFormHM().put("flag", flag);
		}
	}

    private String checkCourse(ArrayList list) {
        StringBuffer buffer = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        for (int i = 0; i < list.size(); i++) {
            String sel = (String) list.get(i);
            String sql = "select r5003,count(r5100) s,r5022,r5037 from r50 left join r51 on r51.r5000=r50.r5000 where r50.r5000 in (" + sel + ") group by r5003,r5022,r5037";
            try {
                this.frowset = dao.search(sql);
                while (this.frowset.next()) {
                    int num = this.frowset.getInt("s");
                    String spflag = this.frowset.getString("r5022");
                    String diyFlag = this.frowset.getString("r5037");
                    if (num < 1) {
                        buffer.append("\n[" + this.frowset.getString("r5003") + "]内没有课件不能发布！\n");
                    } else if (!("03".equals(spflag) || "09".equals(spflag)) && "1".equals(diyFlag)) {
                        buffer.append("只能发布已批或暂停的DIY课程！");
                    }

                    if (buffer.length() > 0)
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "发布失败！";
            }
        }
        return buffer.toString();
    }

}
