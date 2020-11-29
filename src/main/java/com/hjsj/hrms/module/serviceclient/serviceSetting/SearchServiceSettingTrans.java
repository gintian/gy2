package com.hjsj.hrms.module.serviceclient.serviceSetting;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.module.serviceclient.serviceSetting.businessobject.ServiceSettingBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;
import java.util.Map;

public class SearchServiceSettingTrans extends IBusiness {

    private enum TransType{
        /**设置*/
        setting,
        /**首页*/
        serve,
        /**是否需要修改密码*/
        needModifyPassword,
        /**保存首次登录时密码的修改*/
        saveNewPassword
    }
    @Override
    public void execute() throws GeneralException {
        try {
            String transType = (String)this.formHM.get("transType");
            if(transType==null || transType.length()<1)
                return;
            ServiceSettingBo bo=new ServiceSettingBo(this.getFrameconn(),this.userView);
            if(transType.equals(TransType.setting.toString())){
                List<Map<String,Object>> groupDatas=bo.getGroupDatas();
                Map priv = bo.getPrivControlData();//获取当前人自助终端配置相关的权限
                this.formHM.put("serviceData", groupDatas);
                this.formHM.put("priv", priv);
            }else if(transType.equals(TransType.serve.toString())){
                String view = (String)this.formHM.get("preview");
                if("preview".equals(view)){
                    List<Map<String,Object>> groupDatas=bo.getGroupDatas();
                    this.formHM.put("serviceData", groupDatas);
                }else {
                    boolean flag = false;
                    String printerIp = (String)this.formHM.get("ip");
                    flag = bo.isExistsIp(printerIp);
                    if(flag) {
                        List<Map<String,Object>> groupDatas=bo.getGroupDatas();
                        bo.groupServiceFilter(groupDatas);
                        this.formHM.put("flag", 1);
                        this.formHM.put("serviceData", groupDatas);
                    }else {
                        this.formHM.put("flag", 0);
                    }
                }
            }else if(transType.equals(TransType.needModifyPassword.toString())){
                //首次登录是否修改密码
                boolean nmpFlag = false;
                if("1".equals(SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_FIRST_CHANG_PWD))){//系统管理--账号规则
                    String passwordrule = SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordrule");//密码强度
                    String passwordlength = SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordlength");//密码长度
                    nmpFlag = bo.needModifyPassword();
                    this.formHM.put("passwordlength", passwordlength);
                    this.formHM.put("passwordrule", passwordrule);
                    this.formHM.put("nmpFlag", nmpFlag);
                }else {
                    this.formHM.put("nmpFlag", nmpFlag);
                }
            }else if(transType.equals(TransType.saveNewPassword.toString())) {
                String oldpwd = (String) this.formHM.get("oldPw");
                String newPassword = (String) this.formHM.get("newPassword");
                String newokpwd=(String) this.formHM.get("newokpwd");
                String accessType = (String) this.formHM.get("accessType");
                String errorFlag = bo.saveNewPassword(oldpwd,newPassword,newokpwd,accessType);
                this.formHM.put("errorFlag",errorFlag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
