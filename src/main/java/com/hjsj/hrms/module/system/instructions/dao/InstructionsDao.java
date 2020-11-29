package com.hjsj.hrms.module.system.instructions.dao;

import com.hrms.struts.exception.GeneralException;

import java.util.List;

public interface InstructionsDao {
    String getAccessory(String constant) throws GeneralException;

    String getValue(String constant) throws GeneralException;

    List getData(String flagA) throws GeneralException;

    void deleteInstruct(String constant) throws GeneralException;

    void insertInstruct(String constant, String type, String str_value, String describe) throws GeneralException;
}
