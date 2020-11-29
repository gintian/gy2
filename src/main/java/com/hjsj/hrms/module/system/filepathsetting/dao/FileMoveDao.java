package com.hjsj.hrms.module.system.filepathsetting.dao;

import java.util.List;
import java.util.Map;

public interface FileMoveDao {
    /**
     * 获取需要迁移的文件信息列表，一个表一个表的查
     * map 的key包括：
     * guidkey
     * filename 文件名
     * filepath 文件路径
     * input 文件流
     * 表的主键（不定项）
     * @param  sql 需要执行的sql语句
     * @param keyList 表的主键列表，回写文件id的时候会用到
     * @return
     */
    List<Map<String,Object>> getMoveFileList(String sql, List<String> keyList) throws Exception;

    /**
     * 将原有文件路径，更新为新的文件id
     * @param sql
     * @return
     */
    boolean updateFileId(String sql) throws Exception;

    /**
     * 新增文件记录到文件迁移表
     * @param fileid 文件id
     * @param filepath 文件路径
     * @param pathcolumn 文件路径字段名
     * @param tablename 表名
     * @param fileName 文件名
     * @param keycolumn 主键字段名
     * @param keyvalue 主键值
     * @param filetype 文件类型，0：原表中保存路径；1：原表中保存二进制数据；2：无规则
     * @param status 成功失败状态 0：成功；1：失败
     * @param message 异常信息
     * @return
     * @throws Exception
     */
    boolean addFileMove(String fileid, String filepath, String pathcolumn, String tablename, String fileName, String keycolumn, String keyvalue, String filetype, String status, String message)throws Exception;

    /**
     * 删除文件映射表中失败的记录
     * @return
     */
    boolean delFileMapping() throws Exception;
    /**
     * 获取所有的人员库前缀
     *
     * @return
     */
    List getAllLoginDbNameList() throws  Exception;

    /**
     * 删除文件迁移指定的数据
     * @param tablename
     * @param keyvalue
     * @param pathcolumn
     * @return
     */
    boolean delFileMove(String tablename, String keyvalue, String pathcolumn) throws Exception;

    /**
     * 获取人事异动归档的年份列表
     */
    List<String> getYearList() throws Exception;

    /**
     * 获取迁移失败的文件信息
     * @return
     * @param type
     * @throws Exception
     */
    List<Map<String, String>> getFailList(String type) throws Exception;

    /**
     * 一键还原
     * @return
     * @param username
     * @throws Exception
     */
    String fileRecovery(String username) throws Exception;

    /**
     * 从文件迁移表获取已经迁移的有规则的文件、加上迁移成功的无规则的文件
     * @return
     * @param rootPath
     */
    List<String> getRegularFileList(String rootPath) throws Exception;
}
