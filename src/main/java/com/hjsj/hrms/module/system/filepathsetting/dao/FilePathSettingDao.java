package com.hjsj.hrms.module.system.filepathsetting.dao;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;

import java.sql.SQLException;

/**
 * @author zhangh
 */
public interface FilePathSettingDao {
    /**
     * 获取文件配置
     * @return
     */
    RecordVo getFilePathSetting();

    /**
     * 更新文件配置
     * @param recordvo
     * @return
     */
    boolean saveFilePathSetting(RecordVo recordvo) throws SQLException, GeneralException;
}
