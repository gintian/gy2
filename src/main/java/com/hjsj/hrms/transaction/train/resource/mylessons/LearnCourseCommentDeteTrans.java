package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * <p>
 * Title: LearnCourseCommentDeteTrans
 * </p>
 * <p>
 * Description: 在线学习删除 笔记/评论内容交易类
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2015-7-18 下午5:37:46
 * </p>
 * 
 * @author liuyang
 * @version 1.0
 */
public class LearnCourseCommentDeteTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String courseid = "";
        String flag = "";
        try {
            courseid = (String) this.getFormHM().get("courseid");
            courseid = PubFunc.decrypt(SafeCode.decode(courseid));
            
            flag = (String) this.getFormHM().get("flag");
            
            String id = (String) this.getFormHM().get("id");
            id = PubFunc.decrypt(SafeCode.decode(id));
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            StringBuffer buff = new StringBuffer();
            if (!"".equals(id) && id.length() > 0) {
                buff.append("delete from tr_course_comments");
                buff.append(" where a0100='").append(this.userView.getA0100()).append("'");
                buff.append(" and nbase='").append(this.userView.getDbname()).append("'");
                buff.append(" and r5100=?");
                buff.append(" and id =?");
                
                ArrayList values = new ArrayList();
                values.add(courseid);
                values.add(id);
                
                dao.delete(buff.toString(), values);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        courseid = SafeCode.encode(PubFunc.encrypt(courseid));
        this.getFormHM().put("courseid", courseid);
        this.getFormHM().put("flag", flag);
    }

}
