package com.hjsj.hrms.businessobject.kq;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.KQ_Parameter;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.HashMap;

/**
 * 
 * <p>Title:</p>
 * <p>Description:考勤参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-6:10:07:49</p>
 * @author kf-1
 * @version 1.0
 *
 */
public class KqParameter {
    private HashMap    FormHM   = null;
    private UserView   userView = null;
    private String     b0110;
    private Connection conn;

    public KqParameter() {
    }

    /**
     * class得初始化
     * @param HashMap FormHM=IBusiness.getFormHM();
     * @param UserView userView=IBusiness.userView
     * @param b0110 单位编号
     * @param Connection conn=IBusiness.getFrameconn()
     * **/
    public KqParameter(HashMap FormHM, UserView userView, String b0110, Connection conn) {
        this.FormHM = FormHM;
        this.userView = userView;
        this.conn = conn;
        
        if (this.userView.isSuper_admin()) {
            this.b0110 = "UN";
        } else {
            if (b0110 != null && b0110.length() > 0) {
                this.b0110 = b0110;
            } else {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, conn);
                this.b0110 = managePrivCode.getUNB0110();
            }
        }
    }

    /**
     * 按用户管理范围权限创建参数类
     * @param userView
     * @param conn
     */
    public KqParameter(UserView userView, Connection conn) {
        this(userView, "", conn);
    }

    public KqParameter(UserView userView, String b0110, Connection conn) {
        this.FormHM = new HashMap();
        this.userView = userView;
        this.conn = conn;
        if (this.userView.isSuper_admin()) {
            this.b0110 = "UN";
        } else {
            if (b0110 != null && b0110.length() > 0) {
                this.b0110 = b0110;
            } else {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, conn);
                this.b0110 = managePrivCode.getUNB0110();
            }
        }
    }

    /**
     * class得初始化
     * @param HashMap FormHM=IBusiness.getFormHM();
     * @param UserView userView=IBusiness.userView
     * @param b0110 单位编号
     * @param Connection conn=IBusiness.getFrameconn()
     * **/
    public KqParameter(HashMap FormHM, Connection conn) {
        this.FormHM = FormHM;
        this.b0110 = "UN";
        this.conn = conn;
    }

    /**
     * 得到一个考勤参数的HashMap
     * */
    public HashMap getKqParamterMap() {
        KQ_Parameter kq_paramter = new KQ_Parameter();
        HashMap hashmap = new HashMap();
        try {
            hashmap = kq_paramter.getParameter(this.b0110, this.userView, this.conn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.FormHM.put("Kq_Paramter_HashMap", hashmap);

        return hashmap;
    }

    public String getB0100() {
        HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("b0110");
    }

    public void setB0100(String b0110) {
        HashMap hashmap = getKqParamterMap();
        hashmap.put("b0110", b0110);
        KQ_Parameter kq_paramter = new KQ_Parameter();
        kq_paramter.WriteOutParameterXml(hashmap, this.userView, this.conn);
        this.FormHM.put("Kq_Paramter_HashMap", hashmap);
    }

    public String getCardno() {
        HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("cardno");
    }

    public void setCardno(String cardno) {
        HashMap hashmap = getKqParamterMap();
        hashmap.put("cardno", cardno);
        KQ_Parameter kq_paramter = new KQ_Parameter();
        kq_paramter.WriteOutParameterXml(hashmap, this.userView, this.conn);
        this.FormHM.put("Kq_Paramter_HashMap", hashmap);
    }

    public String getG_no() {
        HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("g_no");
    }

    public void setG_no(String g_no) {
        HashMap hashmap = getKqParamterMap();
        hashmap.put("g_no", g_no);
        KQ_Parameter kq_paramter = new KQ_Parameter();
        kq_paramter.WriteOutParameterXml(hashmap, this.userView, this.conn);
        this.FormHM.put("Kq_Paramter_HashMap", hashmap);
    }

    /**
     * 组织机构职位是否隐藏，0为不隐藏，1为隐藏
     * @return
     */
    public String getKq_orgView_post() {
        return KqParam.getInstance().getKqOrgViewPost();
    }

    public String getKq_type() {
        HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("kq_type");
    }

    public void setKq_type(String kq_type) {
        HashMap hashmap = getKqParamterMap();
        hashmap.put("kq_type", kq_type);
        KQ_Parameter kq_paramter = new KQ_Parameter();
        kq_paramter.WriteOutParameterXml(hashmap, this.userView, this.conn);
        this.FormHM.put("Kq_Paramter_HashMap", hashmap);
    }

    public String getNbase() {
        HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("nbase");
    }

    public void setNbase(String nbase) {
        HashMap hashmap = getKqParamterMap();
        hashmap.put("nbase", nbase);
        KQ_Parameter kq_paramter = new KQ_Parameter();
        kq_paramter.WriteOutParameterXml(hashmap, this.userView, this.conn);
        this.FormHM.put("Kq_Paramter_HashMap", hashmap);
    }

    public String getWhours() {
        HashMap hashmap = getKqParamterMap();
        return (String) hashmap.get("whours");
    }

    public void setWhours(String whours) {
        HashMap hashmap = getKqParamterMap();
        hashmap.put("whours", whours);
        KQ_Parameter kq_paramter = new KQ_Parameter();
        kq_paramter.WriteOutParameterXml(hashmap, this.userView, this.conn);
        this.FormHM.put("Kq_Paramter_HashMap", hashmap);
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public void setParams(HashMap map) {
        KQ_Parameter kq_paramter = new KQ_Parameter();
        kq_paramter.WriteOutParameterXml(map, this.userView, this.conn);
    }
}
