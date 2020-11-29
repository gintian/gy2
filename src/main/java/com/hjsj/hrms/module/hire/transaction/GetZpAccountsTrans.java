package com.hjsj.hrms.module.hire.transaction;

import com.hjsj.hrms.module.hire.businessobject.GetZpAccountsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 查询注册帐号
 * 
 * @Title: GetZpAccountsTrans.java
 * @Description: 按照姓名、电话/手机号、唯一性指标等查询招聘外网的对应的注册帐号注册帐号
 * @Company: hjsj
 * @Create time: 2016-12-1 下午05:31:20
 * @author chenxg
 * @version 1.0
 */
/*没办法获取验证码改用servlet*/
@Deprecated
public class GetZpAccountsTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String info = "true";
        try {
            String nameValue = (String) this.getFormHM().get("nameValue");
            String phoneValue = (String) this.getFormHM().get("phoneValue");
            String onlyValue = (String) this.getFormHM().get("onlyValue");
            String codeValue = (String) this.getFormHM().get("codeValue");
            String data = (String) this.getFormHM().get("dataCode");
            data = SafeCode.decode(PubFunc.decrypt(data));
            boolean flag = false;
            // 判断验证码
            if (!codeValue.toUpperCase().equalsIgnoreCase(data)) {
                info = "验证码错误!";
                flag = true;
            }
            
            if(!flag) {
                RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
                String dbname=vo.getString("str_value");
                
                GetZpAccountsBo bo = new GetZpAccountsBo(this.frameconn);
                String phoneField = bo.getPhoneField();
                String onlyField = bo.getOnlynField();
                
                StringBuffer sql = new StringBuffer();
                sql.append("SELECT USERNAME FROM ");
                sql.append(dbname + "A01 WHERE ");
                sql.append(" A0101=?");
                sql.append(" AND " + phoneField + "=?");
                sql.append(" AND " + onlyField + "=?");
                
                ArrayList<String> valueList = new ArrayList<String>();
                valueList.add(nameValue);
                valueList.add(phoneValue);
                valueList.add(onlyValue);
                
                String userName = "";
                String msg = "";
                ContentDAO dao = new ContentDAO(this.frameconn);
                this.frowset = dao.search(sql.toString(), valueList);
                if(this.frowset.next()) {
                    userName = this.frowset.getString("USERNAME");
                    msg = "<P style='margin-top: 20px;'>您的注册帐号是：" + userName + "，请牢记！</P>";
                    msg += "<P style='margin-top: 20px;'>如忘记密码，可以重新设置密码，请点击<a href='###' style='font-size:18px; font-family:\"微软雅黑\";'"
                        + "onclick='zpAccounts.resetPassword(\"" + PubFunc.encrypt(userName) + "\");'>重设密码</a>。</P>";
                } else {
                    info = "false";
                    msg = "<p style='margin-top: 20px;'>没有找到您的注册信息！请确认填写信息是否有误。</p>";
                }
                
                this.getFormHM().put("msg", msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.getFormHM().put("info", info);
        }
    }
}

