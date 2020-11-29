package com.hjsj.hrms.module.system.filepathsetting.dao.impl;

import com.hjsj.hrms.module.system.filepathsetting.dao.FilePathSettingDao;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;

import java.sql.Connection;
import java.sql.SQLException;

public class FilePathSettingDaoImpl implements FilePathSettingDao {
    private Connection connection;
    public FilePathSettingDaoImpl(Connection connection){
        this.connection = connection;
    }
    /**
     * 获取文件配置
     *
     * @return
     */
    @Override
    public RecordVo getFilePathSetting() {
        RecordVo recordVo= ConstantParamter.getConstantVo("FILEPATH_PARAM");
        return recordVo;
    }

    /**
     * 更新文件配置
     *
     * @param recordvo
     * @return
     */
    @Override
    public boolean saveFilePathSetting(RecordVo recordvo) throws SQLException, GeneralException {
        ContentDAO dao = new ContentDAO(this.connection);
        int num = dao.updateValueObject(recordvo);
        return num == 1?true:false;
    }
}
