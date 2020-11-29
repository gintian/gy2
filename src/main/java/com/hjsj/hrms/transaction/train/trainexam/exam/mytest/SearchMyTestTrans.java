package com.hjsj.hrms.transaction.train.trainexam.exam.mytest;

import com.hjsj.hrms.businessobject.train.trainexam.exam.mytest.MyTestBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title:SearchMyTestTrans.java
 * </p>
 * <p>
 * Description:自测考试查询
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-05 14:28:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SearchMyTestTrans extends IBusiness {

    public void execute() throws GeneralException {

        Map map = (HashMap) this.getFormHM().get("requestPamaHM");
        // 课程编号
        String r5000 = (String) map.get("lessonId");
        r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
        // 试卷编号
        MyTestBo bo = new MyTestBo(this.frameconn);
        String r5300 = bo.getR5300ByR5000(r5000);
        // 自考次数
        String testCount = bo.getStringByR5000(r5000, "r5026");
        // where条件
        StringBuffer myTestWhere = new StringBuffer();

        // sql
        String myTestSql = "select " + floatTochar(Sql_switcher.isnull("score", "0.0"), "9999.99")
                + " score,create_time,paper_id ";

        String myTestColumn = "score,create_time,paper_id";

        String myTestOrder = "order by paper_id";

        // 当期用户人员库前缀
        String nbase = this.userView.getDbname();
        // 当前用户人员编号
        String a0100 = this.userView.getA0100();

        if (a0100 == null || a0100.length() <= 0) {
            throw GeneralExceptionHandler.Handle(new GeneralException("当前用户不是自助用户！"));
        }
        // r5315答卷模式，1为整版，2为单体；r5317为是否启用单体即时，1为是，2为否
        myTestWhere.append(" from tr_selfexam_paper ");
        myTestWhere.append(" where nbase='");
        myTestWhere.append(nbase);
        myTestWhere.append("' and a0100='");
        myTestWhere.append(a0100);
        myTestWhere.append("' and r5300=");
        myTestWhere.append(r5300);

        // 查询考试模式
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            String sql = "select r5315,r5317 from r53 where r5300=" + r5300;
            this.frowset = dao.search(sql);
            if (this.frowset.next()) {
                // 答题模式
                String r5315 = this.frowset.getString("r5315");
                // 是否启用单体计时
                String r5317 = this.frowset.getString("r5317");
                r5315 = r5315 == null ? "1" : r5315;
                r5317 = r5317 == null ? "2" : r5317;

                this.getFormHM().put("modelType", r5315);
                this.getFormHM().put("isSingle", r5317);
            } else {
                this.getFormHM().put("modelType", "1");
                this.getFormHM().put("isSingle", "2");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.getFormHM().put("myTestSql", myTestSql);
        this.getFormHM().put("myTestColumn", myTestColumn);
        this.getFormHM().put("myTestOrder", myTestOrder);
        this.getFormHM().put("myTestWhere", myTestWhere.toString());
        this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(r5300)));
        this.getFormHM().put("r5000", SafeCode.encode(PubFunc.encrypt(r5000)));
        this.getFormHM().put("testCount", testCount);
        this.getFormHM().put("state", getstate(r5000));

    }

    private String floatTochar(String itemid, String f) {
        StringBuffer strvalue = new StringBuffer();
        switch (Sql_switcher.searchDbServer()) {
        case 1:
            strvalue.append("CAST(");
            strvalue.append(itemid);
            strvalue.append(" AS NUMERIC(8,1))");
            break;
        case 2:
            strvalue.append("TRIM(TO_CHAR(");
            strvalue.append(itemid);
            strvalue.append(",'" + f + "'))");
            break;
        case 3:
            strvalue.append("CHAR(INT(");
            strvalue.append(itemid);
            strvalue.append("))");
            break;
        }
        return strvalue.toString();
    }
    /**
     * 获取课程状态
     * @param r5000 课程编号
     * @return
     */
    private String getstate(String r5000) {
        String state = "1";
        if (StringUtils.isEmpty(r5000))
            return state;

        try {
            String sql = "select state from TR_SELECTED_LESSON WHERE A0100='"
                    + this.userView.getA0100() + "' and nbase='" + this.userView.getDbname()
                    + "' and r5000=" + r5000;
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search(sql);
            if(this.frowset.next())
                state = this.frowset.getString("state");
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }
}
