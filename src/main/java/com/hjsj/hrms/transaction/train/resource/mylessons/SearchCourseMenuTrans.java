package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.businessobject.train.MediaServerParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>
 * Title:CourseTransAdd
 * </p>
 * <p>
 * Description:在线学习查询课件目录
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-06-30
 * </p>
 * 
 * @author chenxg
 * @version 1.0
 * 
 */
public class SearchCourseMenuTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String lessonid = (String) this.getFormHM().get("lessonid");
            String classes = (String) this.getFormHM().get("classes");
            String filepath = (String) this.getFormHM().get("filepath");
            filepath = PubFunc.decrypt(SafeCode.decode(filepath));
            String opt = (String) this.getFormHM().get("opt");

            lessonid = PubFunc.decrypt(SafeCode.decode(lessonid));
            ArrayList courselist = new ArrayList();
            String isdown = "";
            ContentDAO dao = new ContentDAO(this.frameconn);
            StringBuffer buff = new StringBuffer();
            if ("me".equalsIgnoreCase(opt)) {
                buff.append("select t.r5100,r.r5103,r.r5105,r.r5113,r.r5117,r.R5119,r.fileid,t.state,t.lprogress from");
                buff.append(" tr_selected_course t left join r51 r on ");
                buff.append("t.r5100=r.r5100 where t.a0100='");
                buff.append(userView.getA0100());
                buff.append("' and t.nbase='");
                buff.append(userView.getDbname());
                buff.append("' and r.r5000=");
                buff.append(lessonid);
            } else {
                buff.append("select t.r5100,t.r5103,t.r5105,t.r5113,t.r5117,t.r5119,t.fileid from r50 r,r51 t where r.r5000 = t.r5000 and t.r5000 = '");
                buff.append(lessonid);
                buff.append("'");
            }

            buff.append(" order by t.r5100 desc");

            this.frowset = dao.search(buff.toString());

            while (this.frowset.next()) {
                ArrayList list = new ArrayList();
                String r5100 = this.frowset.getString("r5100");
                r5100 = r5100 == null ? "" : r5100;
                r5100 = SafeCode.encode(PubFunc.encrypt(r5100));
                list.add(r5100);

                String r5103 = this.frowset.getString("r5103");
                r5103 = r5103 == null ? "" : r5103;
                list.add(r5103);

                String r5105 = this.frowset.getString("r5105");
                r5105 = r5105 == null ? "" : r5105;
                list.add(r5105);

                String r5113 = this.frowset.getString("r5113");
                r5113 = r5113 == null ? "" : r5113;

                if ((r5113.startsWith("/") || r5113.startsWith("\\")) && (filepath.endsWith("/") || filepath.endsWith("\\")))
                    r5113 = r5113.substring(1);

                String fileid = this.frowset.getString("fileid");
                fileid = fileid == null ? "" : fileid;
                list.add(fileid);

                String r5117 = this.frowset.getString("r5117");
                r5117 = r5117 == null ? "" : r5117;
                list.add(r5117);

                String R5119 = this.frowset.getString("r5119");
                R5119 = R5119 == null ? "" : R5119;
                list.add(R5119);

                if ("me".equalsIgnoreCase(opt)) {
                    String state = this.frowset.getString("state");
                    state = state == null ? "" : state;
                    list.add(state);

                    String lprogress = this.frowset.getString("lprogress");
                    lprogress = (lprogress == null || "".equals(lprogress)) ? "0" : lprogress;
                    list.add(lprogress);
                }

                if ("1".equals(r5105)) {
                    String ext = "";
                    if (r5113 != null && r5113.length() > 0)
                        ext = r5113.substring(r5113.lastIndexOf(".") + 1);

                    list.add(ext.toLowerCase());
                }

                courselist.add(list);

            }

            isdown = MediaServerParamBo.getIsDownload1(SafeCode.encode(PubFunc.encrypt(lessonid)));
            String finishpiv = "0";
            
            if(this.userView.hasTheFunction("09090501"))
                finishpiv = "1";

            this.getFormHM().put("isdown", isdown);
            this.getFormHM().put("courselist", courselist);
            this.getFormHM().put("classes", classes);
            this.getFormHM().put("lessonid", SafeCode.encode(PubFunc.encrypt(lessonid)));
            this.getFormHM().put("finishpiv", finishpiv);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
