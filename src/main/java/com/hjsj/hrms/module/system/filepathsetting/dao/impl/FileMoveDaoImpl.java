package com.hjsj.hrms.module.system.filepathsetting.dao.impl;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.system.filepathsetting.dao.FileMoveDao;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileMoveDaoImpl implements FileMoveDao {
    //还原进度信息
    private static String progressInfo;
    public static String getProgressInfo() {
        if(StringUtils.isBlank(progressInfo)){
            progressInfo = "{\"current\":\"\",\"percent\":\"0\",\"des\":\"\"}";
        }else if(progressInfo.contains("100%")){
            try{
                String path = System.getProperty("java.io.tmpdir");
                path += "/还原失败文件信息.xls";
                path = path.replace("\\","/");
                FileSystemManager fsManager = VFS.getManager();
                FileObject fileObject = fsManager.resolveFile(path);
                if(fileObject.exists()){
                    progressInfo = "{\"current\":\"\",\"percent\":\"1\",\"des\":\"100%\",\"error\":true}";
                }else{
                    progressInfo = "{\"current\":\"\",\"percent\":\"1\",\"des\":\"100%\",\"error\":false}";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            }
        return progressInfo;
    }
    public static void setProgressInfo(String progressInfo) {
        FileMoveDaoImpl.progressInfo = progressInfo;
    }

    /**
     * 获取需要迁移的文件信息列表，一个表一个表的查
     * map 的key包括：
     * guidkey
     * filename 文件名
     * filepath 文件路径
     * input 文件流
     * 表的主键（不定项）
     *
     * @param sql     需要执行的sql语句
     * @param keyList 表的主键列表，回写文件id的时候会用到
     * @return
     */
    @Override
    public List<Map<String,Object>> getMoveFileList(String sql, List<String> keyList) throws Exception{
        Connection conn = null;
        RowSet rowSet = null;
        List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();
        Map<String,Object> map = null;

        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            rowSet = dao.search(sql);
            while(rowSet.next()){
                map = new HashMap<String,Object>();
                for(String key:keyList){
                    //key中包含了:true，说明该字段是二进制流字段，需要和其他字段区别处理
                    if(key.contains(":true")){
                        key = key.replace(":true","");
                        map.put(key, rowSet.getBinaryStream(key));
                    }else{
                        map.put(key,rowSet.getString(key));
                    }
                }
                fileList.add(map);
            }
        } catch (Exception e) {
            throw e;
        }finally {
            PubFunc.closeDbObj(rowSet);
            PubFunc.closeDbObj(conn);
        }
        return fileList;
    }

    /**
     * 将原有文件路径，更新为新的文件id
     *
     * @param sql
     * @return
     */
    @Override
    public boolean updateFileId(String sql) throws Exception{
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            dao.update(sql);
        } catch (Exception e) {
            throw e;
        }finally {
            PubFunc.closeDbObj(conn);
        }
        return true;
    }


    /**
     * 新增文件记录到文件迁移表
     * @param fileid 文件id
     * @param filepath 文件路径
     * @param pathcolumn 文件路径字段名
     * @param tablename 表名
     * @param fileName 文件名
     * @param keycolumn 主键字段名
     * @param Keyvalue 主键值
     * @param filetype 文件类型，0：原表中保存路径；1：原表中保存二进制数据；2：无规则
     * @param status 成功失败状态 0：成功；1：失败
     * @param message 异常信息
     * @return
     * @throws Exception
     */
    @Override
    public boolean addFileMove(String fileid, String filepath,String pathcolumn, String tablename,String fileName,String keycolumn,String Keyvalue,String filetype,String status,String message)throws Exception{
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            String sql = "insert into vfs_file_move(fileid,filepath,pathcolumn,tablename,filename,keycolumn,Keyvalue,filetype,status,message,updatetime)values(?,?,?,?,?,?,?,?,?,?,?)";
            List valueList = new ArrayList();
            valueList.add(fileid);
            valueList.add(filepath);
            valueList.add(pathcolumn);
            valueList.add(tablename);
            valueList.add(fileName);
            valueList.add(keycolumn);
            valueList.add(Keyvalue);
            valueList.add(filetype);
            valueList.add(status);
            valueList.add(message);
            valueList.add(new Timestamp(new Date().getTime()));
            dao.update(sql,valueList);
        } catch (Exception e) {
            throw e;
        }finally {
            PubFunc.closeDbObj(conn);
        }
        return true;
    }




    /**
     * 删除文件映射表中失败的记录
     *
     * @return
     */
    @Override
    public boolean delFileMapping() throws Exception {
        boolean isSuccess = true;
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            int num = dao.update("delete from vfs_file_mapping where " + Sql_switcher.isnull("filelocation","''")+"=' ' ");
            if(num<0){
                isSuccess = false;
            }
        } catch (Exception e) {
            throw e;
        }finally {
            PubFunc.closeDbObj(conn);
        }
        return isSuccess;
    }

    /**
     * 删除文件迁移指定的数据
     * @param tablename
     * @param keyvalue
     * @param pathcolumn
     * @return
     */
    @Override
    public boolean delFileMove(String tablename,String keyvalue,String pathcolumn) throws Exception{
        boolean isSuccess = true;
        Connection conn = null;
        int num = 0;
        String sql = "";
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            List<String> valueList = new ArrayList<String>();
            valueList.add(tablename);
            valueList.add(keyvalue);
            if(StringUtils.isNotBlank(pathcolumn)){
                valueList.add(pathcolumn);
                sql = "delete from vfs_file_move where tablename=? and keyvalue=? and pathcolumn=?";
            }else{
                sql = "delete from vfs_file_move where tablename=? and keyvalue=? ";
            }
            num= dao.update(sql,valueList);
            if(num<0){
                isSuccess = false;
            }
        } catch (Exception e) {
            throw e;
        }finally {
            PubFunc.closeDbObj(conn);
        }
        return isSuccess;
    }

    /**
     * 获取人事异动归档的年份列表
     */
    @Override
    public List<String> getYearList() throws Exception{
        List<String> yearList = new ArrayList<String>();
        Connection conn = null;
        RowSet rs = null;
        String sql = "";
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            if(Sql_switcher.searchDbServer() == 2){
                sql = "select distinct to_char(start_date,'yyyy') year from t_instance_archive ";
            }else{
                sql = "select distinct datename(yyyy,start_date) year from t_instance_archive ";
            }
            rs = dao.search(sql);
            while (rs.next()){
                yearList.add(rs.getString("year"));
            }
        } catch (Exception e) {
            throw e;
        }finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(conn);
        }
        return  yearList;
    }

    /**
     * 获取迁移失败的文件信息
     * @return
     * @param type
     * @throws Exception
     */
    @Override
    public List<Map<String, String>> getFailList(String type) throws Exception {
        List<Map<String, String>> errorList = new ArrayList<Map<String,String>>();
        Connection conn = null;
        RowSet rowSet = null;
        String sql = "";
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            if("move".equalsIgnoreCase(type)){
                sql = "select tablename,filename,filepath,message,updateTime from vfs_file_move where status='1'";
            }else {
                sql = "select tablename,filename,filepath,message,updateTime from vfs_file_move where status='2'";
            }
            rowSet = dao.search(sql);
            while(rowSet.next()){
                Map<String, String> map = new HashMap<String,String>();
                map.put("tablename",rowSet.getString("tablename"));
                map.put("filename",rowSet.getString("filename"));
                map.put("filepath",rowSet.getString("filepath"));
                map.put("message",rowSet.getString("message"));
                Timestamp time = rowSet.getTimestamp("updateTime");
                Format simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                map.put("updateTime",simpleDateFormat.format(time));
                errorList.add(map);
            }
        } catch (Exception e) {
            throw e;
        }finally {
            PubFunc.closeDbObj(rowSet);
            PubFunc.closeDbObj(conn);
        }
        return  errorList;
    }

    /**
     * 一键还原
     *
     * @return
     * @param username
     * @throws Exception
     */
    @Override
    public String fileRecovery(String username) throws Exception {
        String result = "";
        Connection conn=null;
        String sql = "";
        String whereSql = "";
        //表名
        String tablename = "";
        //存放路径的字段
        String pathcolumn = "";
        //路径值
        String filepath = "";
        String keycolumn = "";
        String keyvalue = "";
        String filetype = "";
        String fileid = "";
        RowSet rowSet = null;
        try {
            conn = (Connection) AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            //先把之前迁移失败的文件记录删除掉
            sql = "delete from vfs_file_move where status='1'";
            dao.update(sql);
            sql = "select fileid,filepath,pathcolumn,tablename,keycolumn,keyvalue,filetype from vfs_file_move where status<>'1' order by (0+fileid)asc";
            rowSet = dao.search(sql);
            while (rowSet.next()){
                try{
                    fileid = rowSet.getString("fileid");
                    filepath = rowSet.getString("filepath");
                    pathcolumn = rowSet.getString("pathcolumn");
                    tablename = rowSet.getString("tablename");
                    keycolumn = rowSet.getString("keycolumn");
                    keyvalue = rowSet.getString("keyvalue");
                    filetype = rowSet.getString("filetype");
                    updateProgress(tablename,"","");
                    if("zc_condition".equalsIgnoreCase(tablename)){
                        List<VfsFileEntity> fileEntityList = VfsService.getFileEntityGroup(keyvalue, VfsModulesEnum.valueOf("ZC"));
                        if(VfsService.deleteFileGroup(username,keyvalue, VfsModulesEnum.valueOf("ZC"))){
                            dao.update("delete from vfs_file_move where fileid='"+fileid+"'");
                            for(VfsFileEntity fileEntity :fileEntityList){
                                dao.update("delete from vfs_file_mapping where id='"+PubFunc.decrypt(fileEntity.getFileid())+"'");
                            }
                        }
                    }else{
                        //如果是联合主键
                        if(keycolumn.contains(",")){
                            String [] keyArr = keycolumn.split(",");
                            String [] valueArr = keyvalue.split(",");
                            whereSql = " where ";
                            for(int x=0;x<keyArr.length-1;x++){
                                whereSql += keyArr[x] + "='"+valueArr[x]+"' and ";
                            }
                            whereSql += keyArr[keyArr.length-1] + "='"+valueArr[keyArr.length-1]+"' ";
                        }else{
                            whereSql = " where " + keycolumn + "='"+keyvalue+"' ";
                        }
                        if("0".equalsIgnoreCase(filetype)){
                            sql = "update " + tablename + " set " + pathcolumn + "='"+filepath+"' " + whereSql;
                        }else if("1".equalsIgnoreCase(filetype)){
                            sql = "update " + tablename + " set " + pathcolumn + "='' " + whereSql;
                        }
                        //删除已经上传到VFS的文件
                        if(VfsService.deleteFile(username,PubFunc.encrypt(fileid))){
                            if("2".equalsIgnoreCase(filetype)){
                                //迁移表以及映射表删除还原成功的文件
                                dao.update("delete from vfs_file_mapping where id=" +fileid);
                                dao.update("delete from vfs_file_move where fileid='"+fileid+"'");
                                //还原文件路径
                            }else if(dao.update(sql)>0){
                                //迁移表以及映射表删除还原成功的文件
                                dao.update("delete from vfs_file_mapping where id=" +fileid);
                                dao.update("delete from vfs_file_move where fileid='"+fileid+"'");
                            }
                        }
                    }
                }catch (Exception e){
                    //还原过程中出现问题，记录还原状态
                    dao.update("update vfs_file_move set status='3',message='"+e.getMessage()+"' where fileid = '"+fileid+"'");
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            updateProgress("","1","100%");
            PubFunc.closeDbObj(rowSet);
            PubFunc.closeDbObj(conn);
        }
        return result;
    }

    /**
     * 从文件迁移表获取已经迁移的有规则的文件、加上迁移成功的无规则的文件
     * @param rootPath
     * @return
     */
    @Override
    public List<String> getRegularFileList(String rootPath) throws Exception{
        List<String> regularFileList = new ArrayList<String>();
        Connection conn = null;
        RowSet rowSet = null;
        String sql = "";
        String filepath = "";
        String tablename = "";
        String filename = "";
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            sql = "select distinct filepath,tablename,filename from vfs_file_move where (filetype in('0','1') or (filetype='2' and status ='0')) and filepath is not null";
            rowSet = dao.search(sql);
            while(rowSet.next()){
                tablename = rowSet.getString("tablename");
                filename = rowSet.getString("filename");
                filepath = rowSet.getString("filepath").replace("\\","/");
                if("per_attachment".equalsIgnoreCase(tablename)){
                    if(filepath.startsWith(rootPath)){
                        filepath = filepath + "/" + filename;
                    }else{
                        filepath = "/doc/" + filepath + "/" + filename;
                    }
                }else if("hr_multimedia_file".equalsIgnoreCase(tablename)){
                    filepath = "/multimedia/" + filepath + "/" + filename;
                }else if("t_wf_file".equalsIgnoreCase(tablename)){
                    if(!filepath.startsWith(rootPath)){
                        filepath = "/multimedia/" + filepath;
                    }
                } else if("zp_attachment".equalsIgnoreCase(tablename)){
                    filepath = filepath + "/" + filename;
                }
                if(filepath.startsWith(rootPath)){
                    filepath = filepath.replaceFirst(rootPath,"");
                }
                if(!filepath.startsWith("/")){
                    filepath = "/" + filepath;
                }
                regularFileList.add(filepath);
            }
        } catch (Exception e) {
            throw e;
        }finally {
            PubFunc.closeDbObj(rowSet);
            PubFunc.closeDbObj(conn);
        }
        return  regularFileList;
    }

    /**
     * 更新文件还原进度信息
     * @param tableName 迁移表
     * @param percent 百分比
     * @param des 描述信息
     */
    private static void updateProgress(String tableName, String percent, String des){
        String current = "";
        if(StringUtils.isBlank(tableName)){
            if("0".equalsIgnoreCase(percent)){
                current = "当前正在还原per_attachment表 ";
            }else if("1".equalsIgnoreCase(percent)){
                current = "还原完成";
            }else{
                current = "当前正在还原无规则文件 ";
                percent = "0.95";
                des = "95%";
            }
        }else{
            current = "当前正在还原"+tableName+"表 ";
            tableName = tableName.toLowerCase();
            if(tableName.contains("t_data")){
                tableName = "t_data_year";
            }else if(tableName.contains("a00")){
                tableName = "a00";
            }
            switch (tableName){
                case "per_attachment":
                    percent = "0.01";
                    des = "1%";
                    break;
                case "hr_multimedia_file":
                    percent = "0.05";
                    des = "5%";
                    break;
                case "t_wf_file":
                    percent = "0.1";
                    des = "10%";
                    break;
                case "zp_attachment":
                    percent = "0.15";
                    des = "15%";
                    break;
                case "announce":
                    percent = "0.2";
                    des = "20%";
                    break;
                case "r51":
                    percent = "0.25";
                    des = "25%";
                    break;
                case "zc_condition":
                    percent = "0.3";
                    des = "30%";
                    break;
                case "w01":
                    percent = "0.35";
                    des = "35%";
                    break;
                case "t_data_year":
                    percent = "0.4";
                    des = "40%";
                    break;
                case "a00":
                    percent = "0.45";
                    des = "45%";
                    break;
                case "b00":
                    percent = "0.5";
                    des = "50%";
                    break;
                case "k00":
                    percent = "0.55";
                    des = "55%";
                    break;
                case "h00":
                    percent = "0.6";
                    des = "60%";
                    break;
                case "law_ext_file":
                    percent = "0.65";
                    des = "65%";
                    break;
                case "law_base_file":
                    percent = "0.7";
                    des = "70%";
                    break;
                case "tr_res_file":
                    percent = "0.75";
                    des = "75%";
                    break;
                case "email_attach":
                    percent = "0.8";
                    des = "80%";
                    break;
                case "per_article":
                    percent = "0.85";
                    des = "85%";
                    break;
                case "resource_list":
                    percent = "0.9";
                    des = "90%";
                    break;
            }
        }
        progressInfo = "{\"current\":\""+current + "\","+"\"percent\":\""+percent+"\",\"des\":\""+des+"\"}";
    }

    /**
     * 获取所有的人员库前缀
     *
     * @return
     */
    @Override
    public List getAllLoginDbNameList() throws  Exception{
        List nbaselist = null;
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            DbNameBo dbbo = new DbNameBo(conn);
            nbaselist = dbbo.getAllLoginDbNameList();
        } catch (Exception e) {
            throw e;
        }finally {
            PubFunc.closeDbObj(conn);
        }
        return  nbaselist;
    }
}
