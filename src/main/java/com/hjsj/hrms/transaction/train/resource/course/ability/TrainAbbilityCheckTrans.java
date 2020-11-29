package com.hjsj.hrms.transaction.train.resource.course.ability;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * @Title:        TrainAbbilityCheckTrans.java 
 * @Description:  检测培训课程是否已过期
 * @Company:      hjsj     
 * @Create time:  2014-5-27 下午05:49:12 
 * @author        chenxg
 * @version       6.x
 */
public class TrainAbbilityCheckTrans extends IBusiness {

    public void execute() throws GeneralException {
        String lessid = (String) this.getFormHM().get("lessonids");
        String msg = "true";
        String lid = "";
        StringBuffer lessonname = new StringBuffer();
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            String[] lessonids = PubFunc.keyWord_reback(lessid).split(";");
            
            HashMap map = new HashMap();
            for (int i = 0; i < lessonids.length; i++) {
                map.clear();
                String lessonid = "";
                String[] lids = PubFunc.keyWord_reback((String) lessonids[i]).split(",");
                for(int m = 0; m < lids.length; m++){
                    if(m > 0)
                        lessonid += ",";
                    lessonid += "'" + PubFunc.decrypt(SafeCode.decode((String)lids[i])) + "'";
                }

                StringBuffer sql = new StringBuffer();
                sql.append("select r5000,R5003");
                sql.append(" from r50");
                sql.append(" where r5000 in (" + lessonid + ")");
                sql.append(" AND R5031<" + Sql_switcher.sqlNow());
                this.frowset = dao.search(sql.toString());
                while (this.frowset.next()) {
                    String r5003 = this.frowset.getString("r5003");
                    lessonname.append("[" + r5003 + "]、");
                    map.put(this.frowset.getString("r5000"), "1");
                }
                
                String[] lessids = lessonids[i].split(",");
                for(int k = 0; k < lessids.length; k++){
                    if(!map.containsKey(PubFunc.decrypt(SafeCode.decode(lessids[k]))))
                        lid += lessids[k] + ",";
                }
            }
            
            if (lessonname!=null&&lessonname.length()>0) {
                msg = ResourceFactory.getProperty("train.course.outdate1")+"\\n\\n";
                msg += lessonname.toString().substring(0, lessonname.length() - 1);
                msg += "\\n\\n" + ResourceFactory.getProperty("train.course.outdate2");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        this.getFormHM().put("msg", msg);
        this.getFormHM().put("lid", lid);
    }
}
