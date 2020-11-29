package com.hjsj.hrms.module.system.fingerprint.dao;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangchunyu
 */
public interface FingerPrintInfoDao {
    /**
     * 保存人脸数据
     *
     * @return
     */
    boolean saveFaceData(String faceData, String tabName) throws SQLException;

    /**
     * 保存指纹数据
     *
     * @param fingerDataMap
     * @return
     */
    boolean saveFingerData(Map fingerDataMap, String tabName) throws SQLException;

    /**
     * 保存指纹数据
     *
     * @return
     */
    void searchInitData(String tabName, Map dataMap)throws SQLException, GeneralException;
}
