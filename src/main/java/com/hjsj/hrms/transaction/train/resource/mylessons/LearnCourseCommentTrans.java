package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * 
 * <p>
 * Title: LearnCourseCommentTrans
 * </p>
 * <p>
 * Description: 在线学习的评论笔记的数据准备
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2015-7-17 下午6:29:53
 * </p>
 * 
 * @author liuyang
 * @version 1.0
 */
public class LearnCourseCommentTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {

        // state: 0-评论， 1-笔记
        String state = (String) this.getFormHM().get("flag");
        ArrayList commentList = new ArrayList();

        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            StringBuffer sql = new StringBuffer();

            String courseid = (String) this.getFormHM().get("courseid");
            courseid = PubFunc.decrypt(SafeCode.decode(courseid));

            sql.append("select id,nbase,a0100,a0101,createtime,comments");
            sql.append(" from tr_course_comments");
            sql.append(" where state=?");
            sql.append(" and r5100=?");
            if ("1".equals(state)) {
                sql.append(" and a0100='").append(this.userView.getA0100()).append("'");
            }
            // 按照id的倒序排列
            sql.append(" order by id desc");

            ArrayList params = new ArrayList();
            params.add(state);
            params.add(courseid);
            this.frowset = dao.search(sql.toString(), params);
            while (this.frowset.next()) {
                ArrayList list = new ArrayList();

                if ("0".equals(state)) {
                    String a0101 = this.frowset.getString("a0101");
                    list.add(a0101);

                    String a0100 = this.frowset.getString("a0100");
                    PhotoImgBo imgBo = new PhotoImgBo(getFrameconn());
                    String url = imgBo.getPhotoPath(this.frowset.getString("nbase"), a0100);
                    list.add(url);
                    // 确定是不是当前人的评论
                    if (userView.getA0100().equals(a0100)) {
                        list.add("yes");
                    } else {
                        list.add("no");
                    }
                }
                Timestamp date = this.frowset.getTimestamp("createtime");
                if (date != null) {
                    String createtime = DateUtils.format(date, "yyyy.MM.dd  HH:mm");
                    list.add(createtime);
                }

                String comments = this.frowset.getString("comments");
                comments = comments == null ? "" : comments;
                list.add(comments);

                String id = frowset.getString("id");
                list.add(SafeCode.encode(PubFunc.encrypt(id)));

                list.add(SafeCode.encode(PubFunc.encrypt(courseid)));

                commentList.add(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("commentList", commentList);
            this.getFormHM().put("flag", state);
        }
    }

}
