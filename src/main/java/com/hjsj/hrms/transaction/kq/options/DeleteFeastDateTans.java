package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.valueobject.common.MonthDayView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DeleteFeastDateTans extends IBusiness {

    public void execute() throws GeneralException {
        String feast_id = (String) this.getFormHM().get("feast_id");
        if (feast_id == null || "".equals(feast_id))
            return;

        ArrayList feastlist = (ArrayList) this.getFormHM().get("selectedlist");
        
        RowSet rs = null;
        
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer sb = new StringBuffer();
        StringBuffer sbs = new StringBuffer();
        try {
            for (int i = 0; i < feastlist.size(); i++) {
                String feastd = "";
                
                MonthDayView md = (MonthDayView) feastlist.get(i);
                if (md.getFyear() == null || "".equals(md.getFyear())) {
                    feastd = md.getFmonth() + "-" + md.getFday();
                } else {
                    feastd = md.getFyear() + "-" + md.getFmonth() + "-" + md.getFday();
                }

                sb.append("select * from kq_feast where feast_id='");
                sb.append(feast_id);
                sb.append("'");
                
                rs = dao.search(sb.toString());
                
                String temp = "";
                String str = null;
                String[] array = null;
                
                for (; rs.next();) {
                    str = Sql_switcher.readMemo(rs, "feast_dates");
                    array = StringUtils.split(str, ",");
                    for (int j = 0; j < array.length; j++) {
                        if (feastd.equals(array[j])) {
                            array[j].replaceAll(feastd, "");
                        } else {
                            temp += (array[j] + ",");
                        }
                    }
                    
                    sbs.append("update kq_feast set feast_dates =");
                    if (!(temp == null || "".equals(temp))) {
                        sbs.append("'");
                        sbs.append(temp);
                        sbs.append("'");
                    } else {
                        temp = null;
                        sbs.append(temp);
                    }

                    sbs.append(" where feast_id ='");
                    sbs.append(feast_id);
                    sbs.append("'");
                    dao.update(sbs.toString());
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
    }

}
