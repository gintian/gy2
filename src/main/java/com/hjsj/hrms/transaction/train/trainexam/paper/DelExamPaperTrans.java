/**
 * 删除试卷 LiWeichao 2011-10-25 17:08:50
 */
package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class DelExamPaperTrans extends IBusiness {

    public void execute() throws GeneralException {
        String sels = (String) this.getFormHM().get("sels");
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            if (sels != null && sels.length() > 0)
                sels = sels.substring(0, sels.length() - 1);

            if (checkIsDel(dao, sels)) {
                ArrayList list = getList(sels);
                if (list == null || list.size() < 1)
                    return;
                ArrayList sqlList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    String value = (String) list.get(i);
                    // 删除对应的试题
                    String sql = "delete from tr_exam_paper where r5300 in(" + value + ")";
                    sqlList.add(sql);
                    // 删除对应的试卷类型
                    sql = "delete from tr_exam_question_type where r5300 in(" + value + ")";
                    sqlList.add(sql);
                    // 删除试卷
                    sql = "delete from r53 where r5300 in(" + value + ")";
                    sqlList.add(sql);
                }
                dao.batchUpdate(sqlList);
                this.getFormHM().put("flag", "ok");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkIsDel(ContentDAO dao, String sels) throws GeneralException {
        boolean tmpFlag = true;
        StringBuffer tmpstring = new StringBuffer();
        ArrayList list = getList(sels);
        if (list == null || list.size() < 1)
            return tmpFlag;

        try {
            for (int i = 0; i < list.size(); i++) {
                String value = (String) list.get(i);
                String sql = "select r5301,r5311 from r53 where r5300 in (" + value + ")";
                this.frowset = dao.search(sql);
                while (this.frowset.next()) {
                    String r5311 = this.frowset.getString("r5311");
                    if ("04".equals(r5311)) {
                        tmpstring.append("\n[" + this.frowset.getString("r5301") + "]");
                        tmpstring.append("为已发布状态，不能删除，只能删除起草、暂停状态的记录!\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (tmpstring.length() > 0) {
            this.getFormHM().put("mess", SafeCode.encode(tmpstring.toString()));
            this.getFormHM().put("flag", "error");
            tmpFlag = false;
        }
        // throw GeneralExceptionHandler.Handle(new
        // Exception(tmpstring.toString()));
        return tmpFlag;
    }
    /**
     * id解密并判断id是否超过1000个，生成list
     * @param sels
     * @return
     */
    private ArrayList getList(String sels) {
        ArrayList list = new ArrayList();
        if (sels == null || sels.length() < 1)
            return list;

        String[] sel = sels.split(",");
        String id = "";
        int a = 0;
        for (int i = 0; i < sel.length; i++) {
            String value = PubFunc.decrypt(SafeCode.decode(sel[i]));
            if (a > 0)
                id += ",";

            id += value;
            a++;

            if (a == 1000) {
                list.add(id);
                id = "";
                a = 0;
            }
        }

        if (id != null && id.length() > 0)
            list.add(id);

        return list;
    }

}
