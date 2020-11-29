package com.hjsj.hrms.transaction.train.resource.myexam;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 培训计划参考人员个人自主重考
 * @Title:        RepeatMyExam.java 
 * @Description:  个人自主重考
 * @Company:      hjsj     
 * @Create time:  2014-7-29 下午05:30:27 
 * @author        chenxg
 * @version       1.0
 */
public class RepeatMyExam extends IBusiness{

    public void execute() throws GeneralException {
        String type = (String) this.getFormHM().get("type");
        String r5300 = (String) this.getFormHM().get("r5300");
        r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
        String r5400 = (String) this.getFormHM().get("r5400");
        r5400 = PubFunc.decrypt(SafeCode.decode(r5400));
        String flag = "false1";
        try{
            ContentDAO dao = new ContentDAO(this.frameconn);
            StringBuffer deletesql = new StringBuffer();
            deletesql.append("delete from tr_exam_answer");
            deletesql.append(" where exam_type=2 AND exam_no='");
            deletesql.append(r5400);
            deletesql.append("' AND nbase='");
            deletesql.append(this.userView.getDbname());
            deletesql.append("' and a0100='");
            deletesql.append(this.userView.getA0100());
            deletesql.append("' and r5300='");
            deletesql.append(r5300 + "'");
            
            StringBuffer updatesql = new StringBuffer();
            updatesql.append("UPDATE R55 SET R5501=NULL,R5503=NULL,R5504=NULL,R5506=NULL,R5507=NULL,");
            updatesql.append("R5509=NULL,R5510=NULL,R5513=-1,R5515=-1,R5517=NULL,");
            updatesql.append("R5519=" + Sql_switcher.isnull("R5519", "0") + "+1");
            updatesql.append(" WHERE R5400='");
            updatesql.append(r5400);
            updatesql.append("' AND NBASE='");
            updatesql.append(this.userView.getDbname());
            updatesql.append("' AND A0100='");
            updatesql.append(this.userView.getA0100() + "'");
            
            int n = dao.update(deletesql.toString());
            int m = dao.update(updatesql.toString());
            
            if(n>0 && m>0)
                flag = "true";
            
            this.getFormHM().put("type", type);
            this.getFormHM().put("r5400", SafeCode.encode(PubFunc.encrypt(r5400)));
            this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(r5300)));
            this.getFormHM().put("flag", flag);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
}
