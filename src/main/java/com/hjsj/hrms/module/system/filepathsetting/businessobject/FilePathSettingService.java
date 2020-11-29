package com.hjsj.hrms.module.system.filepathsetting.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.sql.SQLException;

public interface FilePathSettingService {
    /**
     * 获取文件配置，
     * 若远程端选择了远程服务器或FTP服务器remoteparam仅回传必填项即可
     * @return
     */
    String getFilePathSetting() throws SQLException, GeneralException;

    /**
     * 保存远程文件配置
     * @param param 相关参数
     * @return
     * @throws SQLException
     * @throws GeneralException
     */
    boolean saveFilePathSetting(String param) throws Exception;
}
