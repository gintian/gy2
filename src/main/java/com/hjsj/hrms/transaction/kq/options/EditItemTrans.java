package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class EditItemTrans extends IBusiness {

    public void execute() throws GeneralException {
        RecordVo vo = null;
        String co = "#FFFF33";
            
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String flag = (String) this.getFormHM().get("flag");
            String it = (String) hm.get("akq_item");
            if ("0".equals(flag))
                return;
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            
            vo = new RecordVo("kq_item");
            vo.setString("item_id", it);
            vo = dao.findByPrimaryKey(vo);

            String col = vo.getString("item_color");
            String item_symbol = vo.getString("item_symbol");
            item_symbol = item_symbol.replaceAll(" ", "");
            vo.setString("item_symbol", item_symbol);
            if (col.length() == 10) {
                int cos = Integer.parseInt(col.substring(1, 4));
                int cos1 = Integer.parseInt(col.substring(4, 7));
                int cos2 = Integer.parseInt(col.substring(7, 10));
                co = "#" + this.dec2Hex(cos) + this.dec2Hex(cos1) + this.dec2Hex(cos2);
            }
            vo.setString("item_color", "");

            String fielditemid = vo.getString("fielditemid");
            if (fielditemid != null && !"".equals(fielditemid.trim())) {
                vo.setString("fielditemid", fielditemid.toLowerCase());
            }
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } finally {
            this.getFormHM().put("item", vo);
            this.getFormHM().put("display", vo.getString("displayorder"));
            this.getFormHM().put("colo", co);
        }
    }
    
    private String dec1Hex(int in) {
        String res = null;
        for (int m = 0; m < 16; m++) {
            switch (in) {
            //case 0:
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

    private String dec2Hex(int dec) {
        int cc, mm;
        String tem, tes;

        cc = dec / 16;
        mm = dec % 16;

        tem = this.dec1Hex(cc);
        tes = this.dec1Hex(mm);

        return tem + tes;

    }
}
