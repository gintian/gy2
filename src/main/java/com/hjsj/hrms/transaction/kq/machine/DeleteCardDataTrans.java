package com.hjsj.hrms.transaction.kq.machine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 删除刷卡数据
 * <p>
 * Title:DeleteCardDataTrans.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Mar 22, 2007 9:00:31 AM
 * </p>
 * 
 * @author sunxin
 * @version 1.0
 * 
 */
public class DeleteCardDataTrans extends IBusiness {
    
    public void execute() throws GeneralException {
        try {
            ArrayList selectedinfolist = (ArrayList) this.getFormHM().get("selectedinfolist");
            if (selectedinfolist == null || selectedinfolist.size() == 0)
                throw new GeneralException("没有选择刷卡数据！");
            
            ArrayList delCards = new ArrayList();
            String sql = "DELETE FROM kq_originality_data where a0100=? AND nbase=? AND work_date=? AND work_time=?";
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            for (int i = 0; i < selectedinfolist.size(); i++) {
                LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);

                ArrayList cardInfo = new ArrayList();
                cardInfo.add(rec.get("a0100").toString());
                cardInfo.add(rec.get("nbase").toString());
                cardInfo.add(rec.get("work_date").toString());
                cardInfo.add(rec.get("work_time").toString());
                
                delCards.add(cardInfo);
            }
            dao.batchUpdate(sql, delCards);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("删除失败！");
        }
    }

}
