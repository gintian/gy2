package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.options.KqItem;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchKqItemTrans extends IBusiness {

    private String dec1Hex(int in) {
        String res = null;
        for (int m = 0; m < 16; m++) {
            switch (in) {
            // case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                res = String.valueOf(in);
                break;
            case 10:
                res = "A";
                break;
            case 11:
                res = "B";
                break;
            case 12:
                res = "C";
                break;
            case 13:
                res = "D";
                break;
            case 14:
                res = "E";
                break;
            case 15:
                res = "F";
                break;
            default:
                res = "0";
                break;
            }
        }

        return res;
    }

    public String dec2Hex(int dec) {
        int cc, mm;
        String tem, tes;

        cc = dec / 16;
        mm = dec % 16;

        tem = this.dec1Hex(cc);
        tes = this.dec1Hex(mm);

        return tem + tes;

    }

    public void execute() throws GeneralException {
        /*** 添加离岗时间 **/
        KqItem kqItem = new KqItem(this.getFrameconn());
        kqItem.initSysItem();
        kqItem.leavePost();
        /*** 结束 **/
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String mkey = (String) hm.get("codeitemid");
        hm.remove("codeitemid");
        String flag = (String) hm.get("flag");
        this.getFormHM().put("gw_flag", flag);
        StringBuffer stsql = new StringBuffer();
        StringBuffer cq = new StringBuffer();
        String cid = ""; // cid 作为判断有没有子接点
        // this.getLong(mkey);

        String returnFlag = (String) hm.get("returnFlag");
        if (!"".equals(mkey) && !"null".equals(returnFlag))
            mkey = returnFlag;

        String mess = (String) this.getFormHM().get("sys");
        String mess2 = (String) this.getFormHM().get("sys2");
        String init = (String) this.getFormHM().get("init");
        if (mess == null || mess.length() <= 0)
            mess = "";

        if (mess2 == null || mess2.length() <= 0)
            mess2 = "";

        if (init == null || init.length() <= 0)
            init = "";

        if ("2".equals(mess) && "0".equals(init) && "1".equals(mess2)) {
            mess = "";
            this.getFormHM().put("init", "1");
        }

        if (mess == null || "0".equals(mess))
            this.getFormHM().put("sys", "0");
        else if ("2".equals(mess)) {
            this.getFormHM().put("init", "0");
            this.getFormHM().put("sys", "2");
            this.getFormHM().put("sys2", "1");
        } else {
            this.getFormHM().put("sys", "0");
        }

        int len = 30;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        ArrayList klist = new ArrayList();
        StringBuffer st = new StringBuffer();
        try {
            if ((mkey == null || "".equals(mkey)))
                mkey = "";
            
            stsql.append("select A.*,B.a0000 from kq_item A LEFT JOIN codeitem B");
            stsql.append(" ON A.item_id=B.codeitemid and B.codesetid='27'");
            stsql.append(" where A.item_id like '");
            stsql.append((mkey + "%").toString());
            stsql.append("' order by B.a0000,A.item_id");
            
            String col = "#FF3300";
            this.frowset = dao.search(stsql.toString());
            while (this.frowset.next()) {
                RecordVo vo = new RecordVo("kq_item");
                vo.setString("item_id", this.getFrowset().getString("item_id"));
                vo.setString("item_name", this.getFrowset().getString("item_name"));
                vo.setString("has_rest", this.getFrowset().getString("has_rest"));
                vo.setString("has_feast", this.getFrowset().getString("has_feast"));
                vo.setString("want_sum", this.getFrowset().getString("want_sum"));
                if (this.getFrowset().getString("item_color").length() == 10) {
                    int cos = Integer.parseInt(this.getFrowset().getString("item_color").substring(1, 4));
                    int cos1 = Integer.parseInt(this.getFrowset().getString("item_color").substring(4, 7));
                    int cos2 = Integer.parseInt(this.getFrowset().getString("item_color").substring(7, 10));
                    col = "#" + this.dec2Hex(cos) + this.dec2Hex(cos1) + this.dec2Hex(cos2);
                }
                vo.setString("item_color", col);
                vo.setString("item_symbol", this.getFrowset().getString("item_symbol"));
                vo.setString("item_unit", this.getFrowset().getString("item_unit"));
                vo.setString("sdata_src", this.getFrowset().getString("sdata_src"));
                
                String fielditemid = this.getFrowset().getString("fielditemid");
                if (StringUtils.isNotBlank(fielditemid)) {
                    FieldItem item = DataDictionary.getFieldItem(fielditemid, "Q03");
                    if (item == null || !"1".equals(item.getUseflag()))
                        fielditemid = "";
                }
                vo.setString("fielditemid", fielditemid);
                
                // vo.setString("s_expr",this.getFrowset().getString("s_expr"));
                vo.setString("c_expr",this.getFrowset().getString("c_expr"));//日^月公式
                vo.setString("other_param",this.getFrowset().getString("other_param"));//导入指标
                vo.setInt("displayorder", this.getFrowset().getInt("displayorder"));
                list.add(vo);
            }
            cq.append("select * from codeitem where codesetid='27' and parentid='");
            cq.append(mkey.toString());
            cq.append("' and parentid<>codeitemid");
            this.frowset = dao.search(cq.toString());
            if (this.frowset.next())
                cid = this.frowset.getString("codeitemid");
            CommonData datav = new CommonData("", "");
            klist.add(datav);
            st.append("select fieldsetid,fieldsetdesc from t_hr_busitable where id='30'");
            this.frowset = dao.search(st.toString());
            while (this.frowset.next()) {
                if ("Q11".equals(this.frowset.getString("fieldsetid")) || "Q13".equals(this.frowset.getString("fieldsetid")) || "Q15".equals(this.frowset.getString("fieldsetid"))) {
                    CommonData datavo = new CommonData(this.frowset.getString("fieldsetid"), this.frowset.getString("fieldsetdesc"));
                    klist.add(datavo);
                }
            }
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } finally {
            this.getFormHM().put("itemlist", list);
            this.getFormHM().put("codeitemid", mkey.toString());
            this.getFormHM().put("mes", "8");
            this.getFormHM().put("code", "");
            this.getFormHM().put("name", "");
            this.getFormHM().put("sige", "");
            this.getFormHM().put("klist", klist);
            if (mkey == null || "".equals(mkey)) {
                len = 30;
                this.getFormHM().put("codelen", String.valueOf(1));
                this.getFormHM().put("mess", ResourceFactory.getProperty("label.item.lmessage") + 1);
                this.getFormHM().put("childlen", "");
            } else if (!(cid == null || "".equals(cid))) {
                len = cid.length() - mkey.length();
                this.getFormHM().put("codelen", String.valueOf(len));
                this.getFormHM().put("childlen", String.valueOf(len));
                this.getFormHM().put("mess", ResourceFactory.getProperty("label.item.lmessage") + len);
            } else {
                len = 30 - mkey.length();
                this.getFormHM().put("codelen", String.valueOf(len));
                this.getFormHM().put("childlen", String.valueOf(len));
                this.getFormHM().put("mess", ResourceFactory.getProperty("edit_report.info8") + len);
            }
        }

    }

}
