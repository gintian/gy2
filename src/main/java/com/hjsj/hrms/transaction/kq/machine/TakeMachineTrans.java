package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 接收考勤机数据
 * <p>Title:TakeMachineTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 7, 2007 1:40:24 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class TakeMachineTrans extends IBusiness {

    public void execute() throws GeneralException {
        String machine_num = (String) this.getFormHM().get("machine_num"); //考勤机编号
        String machine_data = (String) this.getFormHM().get("machine_data");//考勤数据
        String cardno_len = (String) this.getFormHM().get("cardno_len"); //最开始指定中控指纹机卡号长度的 是否是5位考勤机的
        StringBuffer sql = new StringBuffer();
        sql.append("select type_id,location,card_len,machine_no from kq_machine_location");
        sql.append(" where location_id='" + machine_num + "'");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String type_id = "";
        String flag = "ok";
        String location = "";
        String card_len = "";
        String machine_no = "";
        try {
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next()) {
                type_id = this.frowset.getString("type_id");
                location = this.frowset.getString("location");
                card_len = this.frowset.getString("card_len");
                machine_no = this.frowset.getString("machine_no");

                if (card_len == null || card_len.length() <= 0 || "0".equals(card_len)) {
                    card_len = cardno_len;
                }
            } else {
                flag = "error";
                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory
                        .getProperty("kq.machine.type.rule.error"), "", ""));
            }

            if (type_id != null && !"0".equals(type_id)) {
                machine_no = "";
            }
        } catch (Exception e) {
            flag = "error";
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        KqCardData kqCardData = new KqCardData(this.userView, this.getFrameconn(), "Machine", location);
        HashMap hashM = kqCardData.getFile_Rule(type_id, cardno_len, card_len);
        ArrayList filelist = new ArrayList();
        if (hashM == null) {
            flag = "error";
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory
                    .getProperty("kq.machine.type.rule.error"), "", ""));
        }

        String[] datas = machine_data.split("`");
        if (datas == null || datas.length <= 0) {
            flag = "error";
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory
                    .getProperty("kq.machine.take.data.error"), "", ""));
        }

        for (int i = 0; i < datas.length; i++) {
            try {
                String line = datas[i];

                if (line == null || line.length() <= 0)
                    continue;

                if (kqCardData.CheckCardData(line, hashM))
                    filelist.add(kqCardData.getFileValue(line, hashM, machine_no));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String a_code = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
        String kind = "";
        String code = "";
        if (a_code == null || a_code.length() <= 0) {
            String privcode = RegisterInitInfoData.getKqPrivCode(userView);
            if ("UN".equalsIgnoreCase(privcode))
                kind = "2";
            else if ("UM".equalsIgnoreCase(privcode))
                kind = "1";
            else if ("@K".equalsIgnoreCase(privcode))
                kind = "0";
            code = RegisterInitInfoData.getKqPrivCodeValue(userView);
        } else {
            if (a_code.indexOf("UN") != -1) {
                kind = "2";
            } else if (a_code.indexOf("UM") != -1) {
                kind = "1";
            } else if (a_code.indexOf("@K") != -1) {
                kind = "0";
            }
            code = a_code.substring(2);
        }
        
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
        ArrayList dblist = kqUtilsClass.setKqPerList(code, kind);
        ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
        String org_id = managePrivCode.getPrivOrgId();
        KqParameter kq_paramter = new KqParameter(this.getFormHM(), this.userView, org_id, this.getFrameconn());
        String cardno_field = kq_paramter.getCardno();
        
        if (cardno_field == null || cardno_field.length() < 0) {

            flag = "error";
            throw GeneralExceptionHandler.Handle(new GeneralException("",
                    ResourceFactory.getProperty("kq.card.nocreate.card_no"), "", ""));
        }
        
        kqCardData.insert_kq_originality_data(filelist, dblist, cardno_field);
        this.getFormHM().put("flag", flag);
    }

}
