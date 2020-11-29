package com.hjsj.hrms.bankgz.transction;

import cfca.sadk.control.sip.api.SIPDecryptionBuilder;
import cfca.sadk.control.sip.api.SIPDecryptor;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;

public class GzBankGetCfcaRandomTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_random = "";
        try{
            String sm2PfxFile = SystemConfig.getPropertyValue("sm2PfxFile") + File.separator + "sm2Encrypt.sm2";
            SIPDecryptor decryptor = SIPDecryptionBuilder.sm2().config(sm2PfxFile, "111111");
            return_random = decryptor.generateServerRandom();
        }catch (Exception e){
            e.printStackTrace();
            return_code = "fail";
        }finally {
            this.getFormHM().put("return_code",return_code);
            this.getFormHM().put("return_random",return_random);
        }
    }
}
