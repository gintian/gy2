package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class AddExamCourseTrans extends IBusiness {

    public void execute() throws GeneralException {
        String exams = (String) this.getFormHM().get("exams");

        String sels = (String) this.getFormHM().get("sels");
        sels = sels != null && sels.length() > 0 ? sels.substring(0, sels.length() - 1) : "";
        String allsels = (String) this.getFormHM().get("allsels");
        allsels = allsels != null && allsels.length() > 0 ? allsels.substring(0, allsels.length() - 1) : "-1";
        ArrayList list = new ArrayList();
        ArrayList slist = new ArrayList();
        if (!"-1".equalsIgnoreCase(allsels)) {
            list = getList(allsels);
        }

        if (!"-1".equalsIgnoreCase(sels)) {
            slist = getList(sels);
        }

        this.getFormHM().put("flag", "ok");

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            for (int m = 0; m < list.size(); m++) {
                if (exams != null && exams.indexOf(",") == -1) {// 当为单个课程关联时  先把操作前关联的课程删除  再做重新关联
                    String examid = PubFunc.decrypt(SafeCode.decode(exams));
                    dao.delete("delete tr_lesson_paper where r5300=" + examid + " and r5000 in (" + list.get(m).toString() + ")", new ArrayList());
                }
            }
            /** 进行保存关联 */
            for (int m = 0; m < slist.size(); m++) {
                String tmpexams[] = exams.split(",");
                String tmpsel[] = slist.get(m).toString().split(",");
                for (int i = 0; i < tmpsel.length; i++) {
                    if (tmpsel[i] == null || tmpsel[i].length() < 1)
                        continue;
                    for (int j = 0; j < tmpexams.length; j++) {
                        // this.frowset =
                        // dao.search("select 1 from tr_lesson_paper where r5000="+tmpsel[i]+" and r5300="+tmpexams[j]);
                        // if(this.frowset.next())
                        // continue;
                        dao.delete("delete tr_lesson_paper where r5000=" + tmpsel[i], new ArrayList());// 删除该课程关联的所以试卷
                        dao.insert("insert into tr_lesson_paper(r5300,r5000) values(" + PubFunc.decrypt(SafeCode.decode(tmpexams[j])) + "," + tmpsel[i] + ")", new ArrayList());
                    }
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.getFormHM().put("flag", "error");
        }

        // System.out.println(sels);
    }

    public ArrayList getList(String value) {
        ArrayList list = new ArrayList();
        int n = 0;
        String r5000 = "";
        if (!"-1".equalsIgnoreCase(value)) {
            String[] allsel = value.split(",");
            for (int i = 0; i < allsel.length; i++) {
                if (n > 0)
                    r5000 += ",";
                r5000 += PubFunc.decrypt(SafeCode.decode(allsel[i]));
                n++;

                if (n == 1000) {
                    list.add(r5000);
                    r5000 = "";
                    n = 0;
                }

            }

            if (r5000.length() > 0)
                list.add(r5000);
        }
        return list;
    }
}
