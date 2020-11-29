package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchItemInTrans extends IBusiness {

    public void execute() throws GeneralException {
        ArrayList list = new ArrayList();
        RecordVo vo = null;
        try {
            String mkey = (String) this.getFormHM().get("codeitemid");

            StringBuffer sts = new StringBuffer();
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            
            vo = new RecordVo("kq_item");
            vo.setString("item_color", "");

            CommonData datavo = null;

            if (!(mkey == null || "".equals(mkey))) {
                sts.append("SELECT codeitemid, codeitemdesc  FROM codeitem where parentid ='");
                sts.append(mkey);
                sts.append("' and codesetid ='27' and parentid<>codeitemid ");
            } else {
                sts.append("SELECT codeitemid, codeitemdesc  FROM codeitem where");
                sts.append(" codesetid ='27'");
            }

            this.frowset = dao.search(sts.toString());
            while (this.frowset.next()) {
                datavo = new CommonData(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
                list.add(datavo);
            }

            if (list.size() == 0) {
                sts.delete(0, sts.length());
                sts.append("SELECT codeitemid, codeitemdesc  FROM codeitem where  codeitemid='");
                sts.append(mkey);
                sts.append("'and codesetid ='27'");
                this.frowset = dao.search(sts.toString());
                while (this.frowset.next()) {
                    datavo = new CommonData(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
                    list.add(datavo);
                }
            }
        } catch (Exception exx) {
            exx.printStackTrace();
            throw GeneralExceptionHandler.Handle(exx);
        } finally {
            this.getFormHM().put("klist", list);
            this.getFormHM().put("item", vo);
            this.getFormHM().put("colo", "#FFFF33");
            this.getFormHM().put("fieldlist", getFieldList());
        }
    }

    private ArrayList getFieldList() {
        ArrayList list = new ArrayList();
        CommonData datavo = null;
        datavo = new CommonData("", "");
        list.add(datavo);

        ArrayList filedlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        for (Iterator it = filedlist.iterator(); it.hasNext();) {
            FieldItem item = (FieldItem) it.next();
            String itemid = item.getItemid();
            if (!"a0100".equalsIgnoreCase(itemid) && "1".equalsIgnoreCase(item.getState()) && !"a0101".equalsIgnoreCase(itemid)
                    && !"nbase".equalsIgnoreCase(itemid) && !"e0122".equalsIgnoreCase(itemid)
                    && !"b0110".equalsIgnoreCase(itemid) && !"e01a1".equalsIgnoreCase(itemid)) {
                list.add(new CommonData(item.getItemid(), item.getItemdesc()));
            }
        }
        return list;
    }
}
