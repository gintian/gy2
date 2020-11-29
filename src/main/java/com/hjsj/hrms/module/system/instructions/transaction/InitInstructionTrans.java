package com.hjsj.hrms.module.system.instructions.transaction;

import com.hjsj.hrms.module.system.instructions.businessobject.InstructionsService;
import com.hjsj.hrms.module.system.instructions.businessobject.impl.InstructionsServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;
/**
 * @Description 初始化说明书相关信息
 * @Author sheny
 * @Date 2020/5/9 13:32
 * @Version V1.0
 **/
public class InitInstructionTrans extends IBusiness {

    @Override
    public void execute() {
        String return_code = "success";
        String return_msg = "";
        Map return_data = new HashMap();
        try {
            InstructionsService instruction = new InstructionsServiceImpl(this.frameconn,this.userView);
            return_data = instruction.initInstrucion();
        } catch (GeneralException e){
            return_code = "fail";
            return_msg = e.getErrorDescription();
        } finally {
            this.formHM.put("return_code",return_code);
            this.formHM.put("return_msg",return_msg);
            this.formHM.put("return_data",return_data);
        }

    }

}
