package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.awt.*;
import java.util.HashMap;

public class SaveKqItemTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            RecordVo rv = (RecordVo) this.getFormHM().get("item");
            String disp = (String) this.getFormHM().get("display");
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String item_id = (String) hm.get("item_id");
            rv.setString("item_id", item_id);
            String item_sy = (String) this.getFormHM().get("item_sy");
            item_sy = item_sy.replaceAll(" ", "");

            //zxj 增加判断item_name是否为空，否则新增的考勤项目，编辑后调整顺序，数据会被清空
            if (rv == null || null == rv.getObject("item_name") || "".equals(rv.getString("item_name")))
                return;

            String col = rv.getString("item_color");
            String cole = "";
            if (col != null && col.length() > 0) {
                int r, b, g;
                Color cco = Color.decode(col.replace('＃', '#'));
                r = cco.getRed();
                g = cco.getGreen();
                b = cco.getBlue();
                cole = "1" + this.retString(String.valueOf(r)) + this.retString(String.valueOf(g))
                        + this.retString(String.valueOf(b));
            }

            StringBuffer sb = new StringBuffer();
            StringBuffer sbs = new StringBuffer();
            String flag = (String) this.getFormHM().get("flag");

            ContentDAO dao = new ContentDAO(this.getFrameconn());

            if ("1".equals(flag)) {
                int dip = 0;
                sbs.append("select max(displayorder) as dis from kq_item where item_id like '");
                sbs.append(item_id);
                sbs.append("%'");
                this.frowset = dao.search(sbs.toString());
                if (this.frowset.next()) {
                    int cc = this.frowset.getInt("dis");
                    dip = cc;
                }
                //查找数据库的主键，是否存在，如存在添加， 否则更新
                String mm = "";
                String clo = "";
                String cxpe = null;
                String sxpe = null;
                String str = null;
                sb.append("select * from kq_item where item_id='");
                sb.append(rv.getString("item_id"));
                sb.append("'");
                this.frowset = dao.search(sb.toString());
                while (this.frowset.next()) {
                    mm = this.frowset.getString("item_id");
                    clo = this.frowset.getString("item_color");
                    cxpe = this.frowset.getString("c_expr");
                    sxpe = this.frowset.getString("s_expr");
                    str = this.frowset.getString("sdata_src");
                }
                if (cole != null && cole.length() > 0) {
                    rv.setString("item_color", cole);
                } else {
                    if (clo == null || "".equals(clo))
                        rv.setString("item_color", "1000000255");
                    else
                        rv.setString("item_color", clo);
                }

                if ("".equals(mm) || mm.length() < 0) {
                    if (!CheckItem(rv, mm))
                        throw new GeneralException(ResourceFactory.getProperty("error.kq.existzb"));

                    rv.setInt("displayorder", (dip + 1));
                    dao.addValueObject(rv);
                } else {
                    if (disp == null || "".equals(disp)) {
                        return;
                    }

                    rv.setInt("displayorder", Integer.parseInt(disp));
                    rv.setString("c_expr", cxpe);
                    rv.setString("s_expr", sxpe);
                    rv.setString("sdata_src", str);
                    rv.setString("item_symbol", item_sy);
                    if (!CheckItem(rv, mm))
                        throw new GeneralException(ResourceFactory.getProperty("error.kq.existzb"));

                    dao.updateValueObject(rv);
                }
            }
        } catch (Exception exx) {
            exx.printStackTrace();
            throw GeneralExceptionHandler.Handle(exx);
        }
    }

    private String retString(String str) {
        String ret = "";

        if (str.length() == 1)
            ret = "00" + str;
        else if (str.length() == 2)
            ret = "0" + str;
        else
            ret = str;

        return ret;
    }
    
    private boolean CheckItem(RecordVo rv, String ta) throws GeneralException {
        boolean ret = true;
        StringBuffer stsql = new StringBuffer();
        String str = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            stsql.append("select fielditemid from kq_item where UPPER(fielditemid)='");
            stsql.append(rv.getString("fielditemid").toUpperCase());
            stsql.append("'");
            if (ta != null && !"".equals(ta.trim())){
                stsql.append(" and item_id !='");
                stsql.append(ta);
                stsql.append("'");
            }

            this.frowset = dao.search(stsql.toString());
            if (this.frowset.next()) {
                str = this.frowset.getString("fielditemid");
            }

            if (str == null || "".equals(str))
                ret = true;
            else
                ret = false;

        } catch (Exception exx) {
            exx.printStackTrace();
            throw GeneralExceptionHandler.Handle(exx);
        }

        return ret;
    }

}
