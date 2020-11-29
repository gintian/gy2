package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.options.sign_point.SignPointValue;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchKqSignPointTrans extends IBusiness {

    public void execute() throws GeneralException {

        String pid = (String) this.formHM.get("pid");
        pid = PubFunc.nullToStr(pid);
        String selectedA0100 = (String) this.formHM.get("selectedA0100");
        selectedA0100 = PubFunc.nullToStr(selectedA0100);
        ContentDAO dao = new ContentDAO(this.frameconn);
        StringBuffer sql = new StringBuffer();
        ArrayList signPoints = new ArrayList();
        String arealevel = "中国";
        String nbaseStr = "";
        try {
            String privCode = RegisterInitInfoData.getKqPrivCode(userView);
            String codeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
            sql.append("select pid,location,name,city ");

            if (selectedA0100.trim().length() > 0) {
                sql.append(" ,(select count('1') from kq_sign_point_emp");
                sql.append(" where pid=A.pid");
                sql.append(" and nbase='").append(selectedA0100.split("`")[0] + "'");
                sql.append(" and a0100='" + selectedA0100.split("`")[1] + "') as num ");
            }

            sql.append(" from kq_sign_point A ");
            
            if (pid.indexOf("P") != -1) {
                sql.append(" where A.pid=" + pid.substring(1));
                arealevel = "17";
            } else if (pid.indexOf("C") != -1) {
                sql.append(" where A.city='" + pid.substring(1) + "'");
            } else {
                arealevel = "5";
            }
            
            if (selectedA0100.trim().length() == 0) {//tiany 添加考勤权限范围
                if (!userView.isSuper_admin()) {
                    if (pid.indexOf("P") == -1 && pid.indexOf("C") == -1) {
                        sql.append("where 1=1 ");
                    }
                    
                    if (privCode != null && privCode.length() != 0) {
                        sql.append(" and ( b0110 like '" + codeValue + "%'");
                        sql.append(" or  b0110 is null or b0110 ='' )");//公共考勤点
                    } else {
                        sql.append(" and 1=2");
                    }
                }
            }//end

            this.frowset = dao.search(sql.toString());
            String location = "";
            String city = "";
            while (this.frowset.next()) {
                SignPointValue point = new SignPointValue();
                location = this.frowset.getString("location");
                city = this.frowset.getString("city");

                point.setPid(this.frowset.getInt("pid") + "");
                point.setLocation(this.frowset.getString("location"));
                point.setName(this.frowset.getString("name"));
                if (selectedA0100.trim().length() > 0) {
                    int num = this.frowset.getInt("num");
                    if (num > 0)
                        point.setIsAdded("1");
                }
                signPoints.add(point);
            }

            if (pid.indexOf("P") != -1)
                arealevel = "P" + location;
            else if (pid.indexOf("C") != -1)
                arealevel = "C" + city;
            else {
                arealevel = "中国";
            }

            //考勤人员库
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
            for (int i = 0; i < kq_dbase_list.size(); i++) {
                nbaseStr += kq_dbase_list.get(i) + ",";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //this.formHM.put("pid", pid);
        this.formHM.put("signPoints", signPoints);
        this.formHM.put("arealevel", arealevel);
        this.formHM.put("nbaseStr", nbaseStr);
    }

}
