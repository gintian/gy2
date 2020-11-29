package com.hjsj.hrms.module.vuesupport.user.transaction;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hrms.hjsj.hjadmin.api.ResponseCodeEnum;
import com.hrms.hjsj.hjadmin.api.RetResult;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetUserParamsTrans extends IBusiness {

    /**
     * 所有的交易的子类须实现的方法
     *
     * @throws GeneralException
     */
    @Override
    public void execute() throws GeneralException {
        try {
            this.getFormHM().put("passwordLength", SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORDLENGTH));
            this.getFormHM().put("retrievingPassword", SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.RETRIEVING_PASSWORD));
            this.getFormHM().put("loginFirstChangPwd", SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_FIRST_CHANG_PWD));
            this.getFormHM().put("passwordRule", SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORDRULE));
            this.getFormHM().put("password_trans_encrypt", SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORD_TRANS_ENCRYPT));

        } catch (Exception e) {
            e.printStackTrace();
            this.getFormHM().put("msg", new RetResult(e.getMessage(), ResponseCodeEnum.businessException));
        }
    }
}
