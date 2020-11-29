package com.hjsj.hrms.module.system.instructions.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.Map;

public interface InstructionsService {
    Map initInstrucion() throws GeneralException;

    void saveInstrucion(String flag, String accessoryFlag, String value) throws GeneralException;
}
