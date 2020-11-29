package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SealDurationTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String flag = (String) this.getFormHM().get("flag");
            ArrayList durationlist = (ArrayList) this.getFormHM().get("selectedlist");
            if (durationlist == null || durationlist.size() == 0)
                return;
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            StringBuffer ssql = null;
            String kq_year = "";
            String fin = "";
            String kq_duration = "";
            
            RecordVo vo = null;
            if(!"0".equals(flag))
                vo = (RecordVo) durationlist.get(0);
            else
                vo = (RecordVo) durationlist.get(durationlist.size()-1);
            fin = vo.getString("finished");
            kq_year = vo.getString("kq_year");
            kq_duration = vo.getString("kq_duration");
    
            if ("0".equals(fin) && "1".equals(flag))
                return;
    
            if ("1".equals(fin) && "0".equals(flag))
                return;
        
            ssql = new StringBuffer();
            ssql.append("UPDATE kq_duration");
            ssql.append(" SET finished=");
            //解封：所选期间及其后的已封存期间都要解封
            if("0".equals(fin)) {
                ssql.append("1");
                ssql.append(" WHERE finished=0");
                ssql.append(" AND kq_year").append(Sql_switcher.concat()).append("kq_duration<='").append(kq_year + kq_duration).append("'");
            } else { //封存：所选期间及其前的未封存期间都要封存
                ssql.append("0");
                ssql.append(" WHERE finished=1");
                ssql.append(" AND kq_year").append(Sql_switcher.concat()).append("kq_duration>='").append(kq_year + kq_duration).append("'");
            }
            dao.update(ssql.toString());
        } catch (Exception exx) {
            exx.printStackTrace();
            throw GeneralExceptionHandler.Handle(exx);
        }
    }

}
