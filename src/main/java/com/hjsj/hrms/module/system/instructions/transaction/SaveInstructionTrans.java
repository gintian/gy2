package com.hjsj.hrms.module.system.instructions.transaction;

import com.hjsj.hrms.module.system.instructions.businessobject.InstructionsService;
import com.hjsj.hrms.module.system.instructions.businessobject.impl.InstructionsServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;
/**
 * @Description 保存说明书相关信息
 * @Author sheny
 * @Date 2020/5/9 13:32
 * @Version V1.0
 **/
public class SaveInstructionTrans extends IBusiness {

    @Override
    public void execute() {
        String return_code = "success";
        String return_msg = "";
        Map return_data = new HashMap();
        try {
            //岗位说明书是否显示附加
            String positionAccessoryFlag = (String) this.formHM.get("positionAccessoryFlag");
            //基准岗位说明书是否显示附件
            String standardAccessoryFlag = (String) this.formHM.get("standardAccessoryFlag");
            //部门职责说明书模板信息
            String departmentValue = (String) this.formHM.get("departmentValue");
            //岗位职责说明书模板信息
            String positionValue = (String) this.formHM.get("positionValue");
            //基准岗位说明书模板信息
            String standardValue = (String) this.formHM.get("standardValue");

            InstructionsService instruction = new InstructionsServiceImpl(this.frameconn,this.userView);
            instruction.saveInstrucion("department", "",departmentValue);
            instruction.saveInstrucion("position",positionAccessoryFlag,positionValue);
            instruction.saveInstrucion("standard",standardAccessoryFlag,standardValue);
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
