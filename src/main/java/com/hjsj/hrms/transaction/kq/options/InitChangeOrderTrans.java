package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class InitChangeOrderTrans extends IBusiness {

    public void execute() throws GeneralException {

        String item_id = (String) this.getFormHM().get("item_ids");

        StringBuffer strsql = new StringBuffer();
        strsql.append("select * from kq_item");
        
        //zxj 20130722 与CS保持一致，排序显示全部指标，进行大排队 
//        if (item_id == null || item_id.equals("")) 
//            item_id=""; strsql.append("where item_id like '");
//        strsql.append(item_id +"%'");
        
        strsql.append(" order by displayorder");

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        try {
            this.frowset = dao.search(strsql.toString());
            while (this.frowset.next()) {
                CommonData ordervo = new CommonData(this.frowset.getString("item_id"), this.frowset.getString("item_name"));
                list.add(ordervo);
            }

        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
        this.getFormHM().put("orlist", list);
    }

}
