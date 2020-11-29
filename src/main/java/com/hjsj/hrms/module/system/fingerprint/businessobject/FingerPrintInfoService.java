package com.hjsj.hrms.module.system.fingerprint.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public interface FingerPrintInfoService {
    /**
     * 数据初始化
     */
    Map initData() throws SQLException, GeneralException;
    /**
     * 保存人脸数据
     *
     * @return
     */
    boolean saveFaceData(String faceData, String jobNumber) throws Exception;

    /**
     * 保存指纹数据
     * @param fingerDataMap 相关参数
     * @return
     * @throws SQLException
     * @throws GeneralException
     */
    boolean saveFingerData(Map<String, String> fingerDataMap, String jobNumber) throws Exception;

    /**
     * 单个指纹比对
     * @param jobNumber
     * @param featureFinger
     * @param finger
     * @return
     * @throws Exception
     */
    boolean checkFingerData(String jobNumber, String featureFinger, String finger) throws Exception;

    /**
     * 指纹审核
     * @param fingerDataMap 相关参数
     * @return
     * @throws SQLException
     * @throws GeneralException
     */
    boolean revieFingerData(Map<String, String> fingerDataMap, String jobNumber) throws Exception;

    /**
     * 重置指纹
     * @return
     * @throws SQLException
     * @throws GeneralException
     */
    boolean resetFingerData(String jobNumber) throws Exception;
}
