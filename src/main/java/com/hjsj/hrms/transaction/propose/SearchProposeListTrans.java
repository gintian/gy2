package com.hjsj.hrms.transaction.propose;

import com.hjsj.hrms.businessobject.propose.ProposeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author chenmengqing
 */
public class SearchProposeListTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        this.getFormHM().put("userAdmin", Boolean.toString(userView.isSuper_admin()));

        String date_flag = this.getFormHM().get("flag").toString();
        String start_date = (String) this.getFormHM().get("start_date");
        String end_date = (String) this.getFormHM().get("end_date");
        Date dateNow = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (end_date == null || end_date.length() <= 0 || "0".equals(date_flag)) {
            end_date = df.format(dateNow);
        }
        
        if (start_date == null || start_date.length() <= 0 || "0".equals(date_flag)) {
            dateNow.setMonth(dateNow.getMonth() - 1);
            start_date = df.format(dateNow);
        }

        ProposeBo pbo = new ProposeBo();
        String sql = pbo.getSearchSQL(this.userView, start_date, end_date);

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        try {
            this.frowset = dao.search(sql);
            cat.debug("SQL=" + sql);
            while (this.frowset.next()) {
                RecordVo vo = new RecordVo("suggest");
                vo.setString("id", PubFunc.NullToZero(this.frowset.getString("id")));
                vo.setString("createuser", PubFunc.nullToStr(this.frowset.getString("createuser")));
                vo.setDate("createtime", PubFunc.FormatDate(this.frowset.getDate("createtime")));
                vo.setString("annymous", PubFunc.NullToZero(this.frowset.getString("annymous")));
                String temp = "";
                temp = PubFunc.nullToStr(Sql_switcher.readMemo(this.frowset, "scontent"));
                if (temp.length() > 20) {
                    temp = temp.substring(0, 20) + "...";
                }
                
                vo.setString("scontent", temp);
                vo.setString("replyuser", PubFunc.nullToStr(this.frowset.getString("replyuser")));
                vo.setDate("replytime", PubFunc.FormatDate(this.frowset.getDate("replytime")));
                temp = PubFunc.nullToStr(Sql_switcher.readMemo(this.frowset, "rcontent"));
                if (temp.length() > 20) {
                    temp = temp.substring(0, 20) + "...";
                }
                vo.setString("rcontent", temp);
                vo.setString("flag", PubFunc.NullToZero(this.frowset.getString("flag")));
                vo.setString("bread", PubFunc.NullToZero(this.frowset.getString("bread")));
                list.add(vo);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } catch (Exception ee) {
            ee.printStackTrace();
            throw GeneralExceptionHandler.Handle(ee);
        } finally {
            this.getFormHM().put("start_date", start_date);
            this.getFormHM().put("end_date", end_date);
            this.getFormHM().put("proposelist", list);
            //按时间查询后初始化 date_flag
            this.getFormHM().put("date_flag", "0");
        }
    }

}
