package com.hjsj.hrms.module.system.filepathsetting.businessobject.impl;

import com.hjsj.hrms.module.system.filepathsetting.businessobject.FileMoveService;
import com.hjsj.hrms.module.system.filepathsetting.dao.FileMoveDao;
import com.hjsj.hrms.module.system.filepathsetting.dao.impl.FileMoveDaoImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileMoveServiceImpl implements FileMoveService {
    private UserView userView;
    //迁移进度信息
    private static String progressInfo;
    //迁移状态信息，0：初次迁移，1：迁移进行中，2：迁移完成
    private static int moveStatus = 0;
    //还原状态信息，0：初次还原，1：还原进行中，2：还原完成
    private static int recoveryStatus = 0;
    public FileMoveServiceImpl(UserView userView){
        this.userView = userView;
    }
    @Override
    public String fileMove() {
        if(moveStatus == 0 || moveStatus == 2){
            moveStatus = 1;
        }else if(moveStatus == 1){
            //当前迁移任务未完成时，直接返回
            return "";
        }
        //初始时，设置迁移进度为0.01
        updateProgress("per_attachment","0.01","1%");
        //需要进行文件迁移的table列表
        List<Map<String,String>> tableList = getTableList();
        String oleColumn = "";
        String sql = "";
        FileMoveDao fileMoveDao = new FileMoveDaoImpl();
        List<String> columnList = null;
        List<Map<String, Object>> fileList;
        try {
            //一个表一个表的去迁移
            for(Map<String,String> map :tableList){
                //更新进度信息
                updateProgress(map.get("tableName"),"","");
                //迁移表中保存文件路径的
                oleColumn = map.get("oleColumn");
                if(StringUtils.isBlank(oleColumn)){
                    if("t_data_year".equalsIgnoreCase(map.get("tableName"))){
                        List<String> yearList = fileMoveDao.getYearList();
                        for(String year:yearList){
                            map.put("tableName","t_data_" + year);
                            columnList = getQueryColumn(map);
                            sql = getQuerySql(map);
                            fileList = fileMoveDao.getMoveFileList(sql,columnList);
                            if(fileList !=null&& fileList.size()>0){
                                fileMove(map,fileList);
                            }
                        }
                    }else{
                        columnList = getQueryColumn(map);
                        sql = getQuerySql(map);
                        fileList = fileMoveDao.getMoveFileList(sql,columnList);
                        if(fileList !=null&& fileList.size()>0){
                            if("zc_condition".equalsIgnoreCase(map.get("tableName"))){
                                ZCfileMove(fileList);
                            }else{
                                fileMove(map,fileList);
                            }
                        }
                    }
                }else{
                    //迁移表中保存二进制流的
                    if("A00".equalsIgnoreCase(map.get("tableName"))){
                        List nbaseList = fileMoveDao.getAllLoginDbNameList();
                        for(int x=0;x<nbaseList.size();x++){
                            RecordVo vo = (RecordVo)nbaseList.get(x);
                            map.put("tableName",vo.getString("pre") + "A00");
                            columnList = getQueryColumn(map);
                            sql = getQuerySql(map);
                            fileList = fileMoveDao.getMoveFileList(sql,columnList);
                            if(fileList !=null&& fileList.size()>0){
                                fileMove(map,fileList);
                            }
                        }
                    }else{
                        columnList = getQueryColumn(map);
                        sql = getQuerySql(map);
                        fileList = fileMoveDao.getMoveFileList(sql,columnList);
                        if(fileList !=null&& fileList.size()>0){
                            fileMove(map,fileList);
                        }
                    }

                }
            }
            //更新进度信息
            updateProgress("","0.95","95%");
            //从文件迁移表获取已经迁移的有规则的文件路径列表
            String rootPath = getRootPath().replace("\\","/");
            List<String> regularFileList = fileMoveDao.getRegularFileList(rootPath);
            //递归遍历原存储路径，获取所有文件的路径列表
            List<FileObject> allFileList = getAllFileList(rootPath);
            String filepath = "";
            for(FileObject fileObject : allFileList){
                filepath = fileObject.getURL().getPath().replace("///","");
                filepath = filepath.replace("\\","/");
                if(filepath.startsWith(rootPath)){
                    filepath = filepath.replaceFirst(rootPath,"");
                }
                //职称评审中的文件都是以文件组的形式存放，需要单独判断
                if(!regularFileList.contains(filepath)&&!filepath.contains("qualifications")){
                    moveIrregularFile(fileMoveDao,filepath,fileObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            //最终进行失败文件回滚
            try {
                rowBack();
                //更新进度信息
                updateProgress("","1","100%");
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //设置状态为迁移完成
                moveStatus = 2;
            }

        }

        return null;
    }




    /**
     * 一键还原
     * @return
     */
    @Override
    public String fileRecovery() throws Exception{
        FileMoveDao fileMoveDao = new FileMoveDaoImpl();
        if(recoveryStatus == 0 || recoveryStatus == 2){
            recoveryStatus = 1;
        }else if(recoveryStatus == 1){
            //正在还原时，直接返回
            return "";
        }
        FileMoveDaoImpl.setProgressInfo("{\"current\":\"当前正在还原per_attachment表\",\"percent\":\"0.01\",\"des\":\"1%\"}");
        String result = "";
        try{
            fileMoveDao.fileRecovery(this.userView.getUserName());
        }catch(Exception e){
            throw e;
        }finally {
            //如果有还原失败的文件，需要记录到Excel表
            List<Map<String, String>> errorList = fileMoveDao.getFailList("recovery");
            exportExcel("recovery",errorList);
            recoveryStatus = 2;
        }
        return result;
    }


    /**
     * 文件迁移，文件复制到VFS中，记录迁移过程
     * @param fileList
     */
    private void fileMove(Map<String,String> map,List<Map<String, Object>> fileList){
        String fileid = "";
        String name = "";
        String ext = "";
        //完整文件名
        String fileName = "";
        //表中保存的路径
        String path = "";
        //文件的绝对路径
        String absolutePath = "";
        String guidkey = "";
        String tableName = map.get("tableName");
        String keyColumn = map.get("keyColumn");
        String pathColumn = "";
        String fileidColumn = "";
        String keyValue = "";
        String [] keyArr = null;
        String username = this.userView.getUserName();
        String type = "0";
        String fileType = map.get("fileType");
        String moduleId = map.get("moduleId");
        String category = map.get("category");
        String filetag = map.get("filetag");
        if(StringUtils.isBlank(filetag)){
            filetag = "";
        }
        String sql = "";
        FileObject fileObject = null;
        FileSystemManager fsManager = null;
        InputStream inputStream = null;
        FileMoveDao fileMoveDao = new FileMoveDaoImpl();
        try{
            //先校验有没有配置存储路径
            if(StringUtils.isBlank(getRootPath())){
                throw new Exception("未正常配置存储路径！");
            }
            fsManager = VFS.getManager();
            for(Map<String,Object> fileMap :fileList){
                try{
                    fileid = "";
                    pathColumn = map.get("pathColumn");
                    fileidColumn = map.get("fileidColumn");
                    name = (String)fileMap.get("name");
                    ext = (String)fileMap.get("ext");
                    path = (String)fileMap.get(pathColumn);
                    guidkey = (String)fileMap.get("guidkey");
                    keyValue = (String)fileMap.get(keyColumn);
                    String idSql = "";
                    if(keyColumn.contains(",")){
                        keyArr = keyColumn.split(",");
                        keyValue = "";
                        for(int x=0;x<keyArr.length-1;x++){
                            keyValue += fileMap.get(keyArr[x]) + ",";
                            idSql += keyArr[x] + "='" + fileMap.get(keyArr[x]) + "' and ";
                        }
                        keyValue += fileMap.get(keyArr[keyArr.length-1]);
                        idSql += keyArr[keyArr.length-1] + "='" + fileMap.get(keyArr[keyArr.length-1]) + "' ";
                    }else{
                        idSql = keyColumn + "='"+fileMap.get(keyColumn)+"'";
                    }
                    if(StringUtils.isBlank(name)){
                        name = "name";
                    }
                    if(StringUtils.isNotBlank(ext)){
                        //表中存的扩展名，有的是带.的，有的不带，需要判断一下
                        if(!ext.contains(".")){
                            ext = "." + ext;
                        }
                    }else{
                        ext = "";
                    }
                    if(StringUtils.isBlank(guidkey)){
                        guidkey = "";
                    }
                    //完整文件名由名称 + 扩展名组成
                    fileName = name + ext;
                    if(StringUtils.isNotBlank(map.get("oleColumn"))){
                        inputStream = (InputStream) fileMap.get("ole");
                    }else{
                        if("announce".equalsIgnoreCase(tableName)){
                            if(!name.contains(".")){
                                name = "." + name;
                            }
                            fileName = keyValue + "_file" + name;
                            path = "/announce/" + keyValue + "/" + fileName;
                        }else if("w01".equalsIgnoreCase(tableName)){
                            fileName = keyValue + ".jpg";
                            path = "/multimedia/jobtitle/qualifications/expert_photo/" + fileName;
                        }else if("r51".equalsIgnoreCase(tableName)){
                            fileName += path.substring(path.lastIndexOf(".")).toLowerCase();
                        }
                        if(!fileName.contains(".")){
                            fileName = path.replace("\\","/");
                            fileName = fileName.substring(fileName.lastIndexOf("/") +1 );
                        }
                        absolutePath = getAbsolutePath(tableName,path,fileName);
                        fileObject = fsManager.resolveFile(absolutePath);
                        if(!fileObject.exists()){
                            //没有找到对应的文件，无法迁移，记录失败信息
                            throw new Exception("路径指向的文件不存在，迁移失败！");
                        }
                        inputStream = fileObject.getContent().getInputStream();
                    }
                    if(StringUtils.isNotBlank(map.get("oleColumn"))||"r51".equalsIgnoreCase(tableName)||(StringUtils.isBlank(pathColumn) && StringUtils.isNotBlank(fileidColumn))){
                        pathColumn = fileidColumn;
                        type = "1";
                    }
                    fileid = VfsService.addFile(username, VfsFiletypeEnum.valueOf(fileType),VfsModulesEnum.valueOf(moduleId), VfsCategoryEnum.valueOf(category),guidkey,inputStream,fileName,filetag,false);
                    //新增文件迁移记录到文件迁移表,只要回传了文件id，就肯定是上传成功了
                    if(StringUtils.isNotBlank(fileid)){
                        writeResult(fileMoveDao,fileid,path,pathColumn,tableName,fileName,keyColumn,keyValue,type,"0","");
                        //回写文件id
                        if(StringUtils.isBlank(fileidColumn)){
                            fileidColumn = pathColumn;
                        }
                        sql = "update "+tableName+" set "+fileidColumn+"='"+fileid+"' where " + idSql;
                        fileMoveDao.updateFileId(sql);
                    }

                }catch (Exception e){
                    writeResult(fileMoveDao,fileid,path,pathColumn,tableName,fileName,keyColumn,keyValue,type,"1",e.getMessage());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeIoResource(inputStream);
        }
    }
    /**
     * 职称评审条件附件迁移
     * @param fileList
     */
    private void ZCfileMove(List<Map<String, Object>> fileList){
        String fileid = "";
        //完整文件名
        String fileName = "";
        //表中保存的路径
        String path = "";
        //文件的绝对路径
        String absolutePath = "";
        String username = this.userView.getUserName();
        String condition_id = "";
        FileObject fileObject = null;
        FileSystemManager fsManager = null;
        InputStream inputStream = null;
        FileMoveDao fileMoveDao = new FileMoveDaoImpl();
        try{
            //先校验有没有配置存储路径
            if(StringUtils.isBlank(getRootPath())){
                throw new Exception("未正常配置存储路径！");
            }
            fsManager = VFS.getManager();
            for(Map<String,Object> fileMap :fileList){
                fileid = "";
                condition_id = (String)fileMap.get("condition_id");
                path = "/multimedia/jobtitle/qualifications/" +condition_id;
                absolutePath = getRootPath() + path;
                fileObject = fsManager.resolveFile(absolutePath);
                if(!fileObject.exists()){
                    //没有找到对应的文件，无法迁移，记录失败信息
                    writeResult(fileMoveDao,fileid,path,"","zc_condition",fileName,"condition_id",condition_id,"0","1","路径指向的文件不存在，迁移失败！");
                }
                if(fileObject.isFolder()){
                    FileObject [] childArr = fileObject.getChildren();
                    for(FileObject childObj:childArr){
                        try{
                            fileName = childObj.getName().getBaseName();
                            inputStream = childObj.getContent().getInputStream();
                            fileid = VfsService.addFile(username, VfsFiletypeEnum.valueOf("doc"),VfsModulesEnum.valueOf("ZC"), VfsCategoryEnum.valueOf("other"),"",inputStream,fileName,condition_id,false);
                            //新增文件迁移记录到文件迁移表,只要回传了文件id，就肯定是上传成功了
                            if(StringUtils.isNotBlank(fileid)){
                                writeResult(fileMoveDao,fileid,path,"","zc_condition",fileName,"condition_id",condition_id,"0","0","");
                            }
                        }catch (Exception e){
                            writeResult(fileMoveDao,fileid,path,"","zc_condition",fileName,"condition_id",condition_id,"0","1",e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeIoResource(inputStream);
        }
    }
    /**
     * 获取文件绝对路径
     * @param tableName
     * @param path
     * @param fileName
     * @return
     * @throws Exception
     */
    private String getAbsolutePath(String tableName,String path,String fileName) throws Exception{
        String absolutePath = "";
        //获取文件根路径
        String rootPath = getRootPath();
        if(path.contains(":")) {
            absolutePath = path;
        }else{
            if("per_attachment".equalsIgnoreCase(tableName)){
                absolutePath = rootPath + "/doc/" + path;
            }else if("hr_multimedia_file".equalsIgnoreCase(tableName)||"t_wf_file".equalsIgnoreCase(tableName)){
                absolutePath = rootPath + "/multimedia/" + path;
            }else if("r51".equalsIgnoreCase(tableName)){
                //r51表的文件比较特殊，直接保存在了中间件中
                absolutePath = SystemConfig.getServletContext().getRealPath("/");
                if(("weblogic".equals(SystemConfig.getPropertyValue("webserver")))){
                    absolutePath = SystemConfig.getServletContext().getResource("/").getPath();
                }
                absolutePath += path;
            }else if("zp_attachment".equalsIgnoreCase(tableName)){
                absolutePath = rootPath + "/multimedia/" + path;
            }else{
                absolutePath = rootPath + "/" + path;
            }
        }
        if(!absolutePath.contains(".")){
            absolutePath +="/" + fileName ;
        }
        absolutePath = absolutePath.replace("\\","/");
        return absolutePath;
    }

    /**
     * 获取查询数据的sql
     * @return
     */
    private String getQuerySql(Map<String,String> map) throws Exception{
        String sql = "";
        String keySql = "";
        String nameSql = "";
        String pathSql = "";
        String guidkeySql = "";
        String whereSql = "";
        String tableName = map.get("tableName");
        String keyColumn = map.get("keyColumn");
        String nameColumn =  map.get("nameColumn");
        String pathColumn =  map.get("pathColumn");
        String oleColumn = map.get("oleColumn");
        String fileidColumn = map.get("fileidColumn");
        String guidkeyColumn =  map.get("guidkeyColumn");
        String nbaseColumn =  map.get("nbaseColumn");
        String A0100Column = map.get("A0100Column");
        String orgCodeColumn =  map.get("orgCodeColumn");
        String [] keyArr = null;
        String [] nameArr = null;
        //如果是联合主键的
        if(keyColumn.contains(",")){
            keyArr = keyColumn.split(",");
            for(String key: keyArr){
                keySql +=  "vfs." + key + ",";
            }
            keySql = keySql.substring(0,keySql.length()-1);
        }else{
            keySql = "vfs." + keyColumn;
        }
        if(StringUtils.isNotBlank(nameColumn)){
            if(nameColumn.contains(",")){
                nameArr = nameColumn.split(",");
                nameSql = ",vfs." + nameArr[0] + " as name,";
                nameSql += "vfs." +  nameArr[1] + " as ext ";
            }else{
                nameSql = ",vfs." + nameColumn + " as name ";
            }
        }
        if(StringUtils.isNotBlank(oleColumn)){
            pathSql = ",vfs." + oleColumn + " as ole ";
            whereSql = " where (" +  Sql_switcher.isnull("vfs." + fileidColumn,"''") + "='' or vfs."+fileidColumn+" is null) and vfs." + oleColumn + " is not null ";
        }else if(StringUtils.isNotBlank(pathColumn)){
            pathSql = ",vfs." + pathColumn;
            whereSql = " where " + "vfs." + pathColumn + " like '%\\%' " + " or " + "vfs." + pathColumn + " like '%/%' " + " or " + "vfs." + pathColumn + " like '%.%' ";
        }else if(StringUtils.isNotBlank(fileidColumn)){
            whereSql = " where (" +  Sql_switcher.isnull("vfs." + fileidColumn,"''") + "='' or vfs."+fileidColumn+" is null )";
        }
        //公告栏中，有的公告是没有附件的，只迁移有附件的
        if("announce".equalsIgnoreCase(tableName)&&StringUtils.isBlank(oleColumn)){
            whereSql += " and (" + Sql_switcher.isnull("ext","''") + "<>'' or ext is not null ) and thefile is null ";
        }else if("r51".equalsIgnoreCase(tableName)){
            whereSql = " where (" +  Sql_switcher.isnull("vfs." + fileidColumn,"''") + "='' or vfs."+fileidColumn+" is null )";
        }
        //直接有guidkey字段的，需要把guidkey一起查出来
        if(StringUtils.isNotBlank(guidkeyColumn)){
            guidkeySql = ",vfs." + guidkeyColumn + " as guidkey ";
            sql = "select " + keySql +  nameSql  + pathSql + guidkeySql + " from " + tableName + " vfs ";
            sql += whereSql;
            //是否有关联人员的guidkey;
        }else if(StringUtils.isNotBlank(nbaseColumn)){
            FileMoveDao fileMoveDao = new FileMoveDaoImpl();
            String A01Sql = "";
            List nbaseList = fileMoveDao.getAllLoginDbNameList();
            for(int x=0;x<nbaseList.size()-1;x++){
                RecordVo vo = (RecordVo)nbaseList.get(x);
                A01Sql += "select " + "upper('" + vo.getString("pre") + "') as nbase,A0100,guidkey from " +  vo.getString("pre") + "A01 union ";
            }
            RecordVo recordVo = (RecordVo)nbaseList.get(nbaseList.size()-1);
            A01Sql += " select " + "upper('" + recordVo.getString("pre") + "') as nbase,A0100,guidkey from " +  recordVo.getString("pre") + "A01 ";
            sql = " select " + keySql + nameSql + pathSql + ",temp.guidkey from " + tableName + " vfs " ;
            sql += " left join (" + A01Sql + ")temp on temp.A0100= vfs." + A0100Column + " and temp.nbase = upper(vfs."+nbaseColumn+") ";
            sql += whereSql;
        }else if("personnel".equalsIgnoreCase(map.get("category"))){
            String nbase = tableName.substring(0,3);
            sql = " select " + keySql + nameSql + pathSql + ",temp.guidkey from " + tableName + " vfs ";
            sql += " left join "+nbase+"A01 temp on temp.A0100 = vfs." + A0100Column;
            sql += whereSql;
        } else if(StringUtils.isNotBlank(orgCodeColumn)){
            sql = "select " + keySql + nameSql + pathSql + ",temp.guidkey " + " from " + tableName + " vfs ";
            sql += " left join organization temp on temp.codeitemid = vfs." + orgCodeColumn;
            sql += whereSql;
        }else{
            sql = "select " + keySql + nameSql + pathSql + guidkeySql + " from " + tableName + " vfs ";
            sql += whereSql;
        }
        if("zc_condition".equalsIgnoreCase(tableName)){
            sql += " where vfs.condition_id not in (select distinct keyvalue from vfs_file_move where status=0 )";
        }else if("w01".equalsIgnoreCase(tableName)){
            sql += " and vfs.W0111 = 1 ";
        }
        return sql ;
    }

    /**
     * 获取查询数据的字段列表
     * @param map
     * @return
     */
    private List<String> getQueryColumn(Map<String,String> map){
        List<String> colunmList = new ArrayList<String>();
        String keyColumn = map.get("keyColumn");
        String [] keyArr = null;
        //如果是联合主键的
        if(keyColumn.contains(",")){
            keyArr = keyColumn.split(",");
            for(String key: keyArr){
                colunmList.add(key);
            }
        }else{
            colunmList.add(keyColumn);
        }
        if(StringUtils.isNotBlank(map.get("nameColumn"))){
            if(map.get("nameColumn").contains(",")){
                colunmList.add("name");
                colunmList.add("ext");
            }else{
                colunmList.add("name");
            }
        }
        if(StringUtils.isNotBlank(map.get("pathColumn"))){
            colunmList.add(map.get("pathColumn"));
        }
        if(StringUtils.isNotBlank(map.get("oleColumn"))){
            colunmList.add("ole:true");
        }
        //直接有guidkey字段的，需要把guidkey一起查出来
        if(StringUtils.isNotBlank(map.get("guidkeyColumn"))||StringUtils.isNotBlank(map.get("A0100Column"))||StringUtils.isNotBlank(map.get("orgCodeColumn"))){
            colunmList.add("guidkey");
        }
        return  colunmList;
    }
    /**
     *
     * @return
     */
    public List<Map<String,String>> getTableList(){
        List<Map<String,String>> tableList = new ArrayList<Map<String,String>>();
        //OKR附件表
        Map<String,String> perAttachMap = new HashMap<String,String>();
        //表名
        perAttachMap.put("tableName","per_attachment");
        //主键，联合主键中间用逗号分隔
        perAttachMap.put("keyColumn","id");
        //存放文件路径的字段
        perAttachMap.put("pathColumn","path");
        //存放文件名的字段，两个字段中间用逗号分隔
        perAttachMap.put("nameColumn","file_name");
        //文件类型
        perAttachMap.put("fileType","doc");
        //模块号
        perAttachMap.put("moduleId","MB");
        //所属文件类型
        perAttachMap.put("category","other");
        tableList.add(perAttachMap);

        //多媒体子集附件表
        Map<String,String> multimediaMap = new HashMap<String,String>();
        //表名
        multimediaMap.put("tableName","hr_multimedia_file");
        //主键，联合主键中间用逗号分隔
        multimediaMap.put("keyColumn","id");
        //存放文件路径的字段
        multimediaMap.put("pathColumn","path");
        //存放文件名的字段，两个字段中间用逗号分隔
        multimediaMap.put("nameColumn","filename");
        //文件类型
        multimediaMap.put("fileType","multimedia");
        //模块号
        multimediaMap.put("moduleId","YG");
        //所属文件类型
        multimediaMap.put("category","personnel");
        multimediaMap.put("guidkeyColumn","mainguid");
        tableList.add(multimediaMap);
        //流程附件表
        Map<String,String> wfAttachMap = new HashMap<String,String>();
        wfAttachMap.put("tableName","t_wf_file");
        wfAttachMap.put("keyColumn","file_id");
        wfAttachMap.put("pathColumn","filepath");
        wfAttachMap.put("nameColumn","name,ext");
        wfAttachMap.put("fileType","doc");
        wfAttachMap.put("moduleId","RS");
        wfAttachMap.put("category","personnel");
        wfAttachMap.put("nbaseColumn","basepre");
        wfAttachMap.put("A0100Column","objectid");
        tableList.add(wfAttachMap);

        //招聘附件表
        Map<String,String> zpAttachMap = new HashMap<String,String>();
        zpAttachMap.put("tableName","zp_attachment");
        zpAttachMap.put("keyColumn","id");
        zpAttachMap.put("pathColumn","path");
        zpAttachMap.put("nameColumn","file_name_old");
        zpAttachMap.put("fileType","other");
        zpAttachMap.put("moduleId","ZP");
        zpAttachMap.put("category","other");
        tableList.add(zpAttachMap);

        //公告表 路径
        Map<String,String> announceMap = new HashMap<String,String>();
        announceMap.put("tableName","announce");
        announceMap.put("keyColumn","id");
        //公告表没有直接存路径，而是按照固定的规则来生成的路径，规则：根路径\announce\+id +\ + 文件名
        announceMap.put("pathColumn","");
        announceMap.put("fileidColumn","fileid");
        //文件名固定为id + _file + ext
        announceMap.put("nameColumn","ext");
        announceMap.put("fileType","doc");
        announceMap.put("moduleId","NOLOGIN");
        announceMap.put("category","other");
        tableList.add(announceMap);

        //公告表 二进制流
        Map<String,String> announce_Map = new HashMap<String,String>();
        announce_Map.put("tableName","announce");
        announce_Map.put("keyColumn","id");
        announce_Map.put("oleColumn","thefile");
        announce_Map.put("fileidColumn","fileid");
        announce_Map.put("nameColumn","ext");
        announce_Map.put("fileType","other");
        announce_Map.put("moduleId","NOLOGIN");
        announce_Map.put("category","other");
        tableList.add(announce_Map);

        //培训课件表
        Map<String,String> r51Map = new HashMap<String,String>();
        r51Map.put("tableName","r51");
        r51Map.put("keyColumn","r5100");
        r51Map.put("pathColumn","r5113");
        //培训课件表，原有路径不能去覆盖掉，回写到fileid字段
        r51Map.put("fileidColumn","fileid");
        //培训课件表，文件名称得根据存储路径解析
        r51Map.put("nameColumn","r5103");
        r51Map.put("fileType","videostreams");
        r51Map.put("moduleId","PX");
        r51Map.put("category","other");
        tableList.add(r51Map);

        //职称评审条件表
        Map<String,String> zc_conditionMap = new HashMap<String,String>();
        zc_conditionMap.put("tableName","zc_condition");
        zc_conditionMap.put("keyColumn","condition_id");
        //没有路径字段，是按照固定规则来生成的，规则：根路径\multimedia\jobtitle\qualifications\ + condition_id
        //而且文件都是一组一组的存在，condition_id就是文件夹名，也就是组名。
        zc_conditionMap.put("fileType","doc");
        zc_conditionMap.put("moduleId","ZC");
        zc_conditionMap.put("category","other");
        tableList.add(zc_conditionMap);

        //职称评审专家表
        Map<String,String> w01Map = new HashMap<String,String>();
        w01Map.put("tableName","w01");
        w01Map.put("keyColumn","w0101");
        //没有文件路径字段，按照固定规则来生成，规则："根路径\multimedia\jobtitle\qualifications\expert_photo\ +w0101+.jpg"
        //没有文件名称字段，按照固定规则来生成，规则：w0101 + .jpg
        w01Map.put("fileidColumn","fileid");
        w01Map.put("fileType","multimedia");
        w01Map.put("moduleId","ZC");
        w01Map.put("category","other");
        tableList.add(w01Map);

        //流程归档表
        Map<String,String> t_data_Map = new HashMap<String,String>();
        t_data_Map.put("tableName","t_data_year");
        t_data_Map.put("keyColumn","record_id");
        t_data_Map.put("pathColumn","file_patch");
        t_data_Map.put("fileType","doc");
        t_data_Map.put("moduleId","RS");
        t_data_Map.put("category","other");
        tableList.add(t_data_Map);

        //库前缀+A00,是一系列表
        Map<String,String> A00Map = new HashMap<String,String>();
        A00Map.put("tableName","A00");
        A00Map.put("keyColumn","A0100,I9999");
        //存放二进制流的字段名
        A00Map.put("oleColumn","Ole");
        A00Map.put("fileidColumn","fileid");
        A00Map.put("nameColumn","title,ext");
        A00Map.put("fileType","multimedia");
        A00Map.put("moduleId","YG");
        A00Map.put("category","personnel");
        A00Map.put("A0100Column","A0100");
        tableList.add(A00Map);

        //B00机构附件表
        Map<String,String> B00Map = new HashMap<String,String>();
        B00Map.put("tableName","B00");
        B00Map.put("keyColumn","B0110,I9999");
        B00Map.put("oleColumn","Ole");
        B00Map.put("fileidColumn","fileid");
        B00Map.put("nameColumn","title,ext");
        B00Map.put("fileType","multimedia");
        B00Map.put("moduleId","JG");
        B00Map.put("category","unit");
        B00Map.put("orgCodeColumn","B0110");
        tableList.add(B00Map);

        //K00岗位附件表
        Map<String,String> K00Map = new HashMap<String,String>();
        K00Map.put("tableName","K00");
        K00Map.put("keyColumn","E01A1,I9999");
        K00Map.put("oleColumn","Ole");
        K00Map.put("fileidColumn","fileid");
        K00Map.put("nameColumn","title,ext");
        K00Map.put("fileType","multimedia");
        K00Map.put("moduleId","JG");
        K00Map.put("category","post");
        K00Map.put("orgCodeColumn","E01A1");
        tableList.add(K00Map);

        //H00基准岗位附件表
        Map<String,String> H00Map = new HashMap<String,String>();
        H00Map.put("tableName","H00");
        H00Map.put("keyColumn","H0100,I9999");
        H00Map.put("oleColumn","Ole");
        H00Map.put("fileidColumn","fileid");
        H00Map.put("nameColumn","title,ext");
        H00Map.put("fileType","multimedia");
        H00Map.put("moduleId","JG");
        H00Map.put("category","post");
        H00Map.put("orgCodeColumn","H0100");
        tableList.add(H00Map);

        //law_ext_file
        Map<String,String> law_ext_Map = new HashMap<String,String>();
        law_ext_Map.put("tableName","law_ext_file");
        law_ext_Map.put("keyColumn","ext_file_id");
        law_ext_Map.put("oleColumn","content");
        law_ext_Map.put("fileidColumn","fileid");
        law_ext_Map.put("nameColumn","name,ext");
        law_ext_Map.put("fileType","other");
        law_ext_Map.put("moduleId","WD");
        law_ext_Map.put("category","other");
        tableList.add(law_ext_Map);

        //law_base_file
        Map<String,String> law_base_Map = new HashMap<String,String>();
        law_base_Map.put("tableName","law_base_file");
        law_base_Map.put("keyColumn","file_id");
        law_base_Map.put("oleColumn","content");
        law_base_Map.put("fileidColumn","fileid");
        law_base_Map.put("nameColumn","title,ext");
        law_base_Map.put("fileType","other");
        law_base_Map.put("moduleId","WD");
        law_base_Map.put("category","other");
        tableList.add(law_base_Map);

        //law_base_file,这个表比较特殊，一条记录存两个文件
        Map<String,String> law_base_oriMap = new HashMap<String,String>();
        law_base_oriMap.put("tableName","law_base_file");
        law_base_oriMap.put("keyColumn","file_id");
        law_base_oriMap.put("oleColumn","originalfile");
        law_base_oriMap.put("fileidColumn","originalfileid");
        law_base_oriMap.put("nameColumn","name,originalext");
        law_base_oriMap.put("fileType","other");
        law_base_oriMap.put("moduleId","WD");
        law_base_oriMap.put("category","other");
        tableList.add(law_base_oriMap);

        //tr_res_file
        Map<String,String> tr_res_Map = new HashMap<String,String>();
        tr_res_Map.put("tableName","tr_res_file");
        tr_res_Map.put("keyColumn","fileid");
        tr_res_Map.put("oleColumn","content");
        tr_res_Map.put("fileidColumn","file_id");
        tr_res_Map.put("nameColumn","name,ext");
        tr_res_Map.put("fileType","other");
        tr_res_Map.put("moduleId","PX");
        tr_res_Map.put("category","other");
        tableList.add(tr_res_Map);

        //邮件附件表
        Map<String,String> email_Map = new HashMap<String,String>();
        email_Map.put("tableName","email_attach");
        email_Map.put("keyColumn","attach_id");
        email_Map.put("oleColumn","attach");
        email_Map.put("fileidColumn","fileid");
        email_Map.put("nameColumn","filename");
        email_Map.put("fileType","other");
        email_Map.put("moduleId","ZP");
        email_Map.put("category","other");
        tableList.add(email_Map);



        //per_article
        Map<String,String> per_article_Map = new HashMap<String,String>();
        per_article_Map.put("tableName","per_article");
        per_article_Map.put("keyColumn","article_id");
        per_article_Map.put("oleColumn","affix");
        per_article_Map.put("fileidColumn","fileid");
        per_article_Map.put("nameColumn","article_name");
        per_article_Map.put("fileType","multimedia");
        per_article_Map.put("moduleId","JX");
        per_article_Map.put("category","personnel");
        per_article_Map.put("nbaseColumn","nbase");
        per_article_Map.put("A0100Column","A0100");
        tableList.add(per_article_Map);

        //resource_list
        Map<String,String> resource_list_Map = new HashMap<String,String>();
        resource_list_Map.put("tableName","resource_list");
        resource_list_Map.put("keyColumn","contentid");
        resource_list_Map.put("oleColumn","content");
        resource_list_Map.put("fileidColumn","fileid");
        resource_list_Map.put("nameColumn","name,ext");
        resource_list_Map.put("fileType","doc");
        resource_list_Map.put("moduleId","XT");
        resource_list_Map.put("category","other");
        tableList.add(resource_list_Map);

        return tableList;
    }

    /**
     * 在文件迁移表记录迁移结果信息
     * @param fileid
     * @param filepath
     * @param pathcolumn
     * @param tablename
     * @param fileName
     * @param keycolumn
     * @param keyvalue
     * @param filetype
     * @param status
     * @param message
     * @throws Exception
     */
    private void writeResult(FileMoveDao fileMoveDao,String fileid, String filepath, String pathcolumn, String tablename,String fileName, String keycolumn, String keyvalue, String filetype, String status, String message)throws Exception{
        //新增记录前，先删除掉上一次迁移的记录（避免重复数据）
        if(StringUtils.isNotBlank(tablename)){
            fileMoveDao.delFileMove(tablename,keyvalue,pathcolumn);
        }
        //记录迁移的结果信息
        fileMoveDao.addFileMove(PubFunc.decrypt(fileid),filepath,pathcolumn,tablename,fileName,keycolumn,keyvalue,filetype,status,message);
    }
    private void rowBack() throws Exception{
        FileMoveDao fileMoveDao = new FileMoveDaoImpl();
        //清除掉文件映射表中失败的文件记录
        fileMoveDao.delFileMapping();
        //生成失败信息Excel表
        List<Map<String,String>> errorList = fileMoveDao.getFailList("move");
        exportExcel("move",errorList);
    }

    /**
     * 失败信息导出为Excel
     * @param type
     * @param errorList
     * @throws Exception
     */
    private void exportExcel(String type,List<Map<String,String>> errorList) throws Exception{
        OutputStream out = null;
        HSSFWorkbook workbook = null;
        try{
            String path = System.getProperty("java.io.tmpdir");
            if("move".equalsIgnoreCase(type)){
                path += "/迁移失败文件信息.xls";
            }else{
                path += "/还原失败文件信息.xls";
            }
            path = path.replace("\\","/");
            FileSystemManager fsManager = VFS.getManager();
            FileObject fileObject = fsManager.resolveFile(path);
            if(fileObject.exists()){
                fileObject.delete();
            }
            if(errorList!=null && errorList.size()>0){
                out = fileObject.getContent().getOutputStream();
                //创建工作薄对象
                workbook = new HSSFWorkbook();
                //创建工作表对象
                HSSFSheet sheet = workbook.createSheet();
                if("move".equalsIgnoreCase(type)){
                    workbook.setSheetName(0,"迁移失败文件信息");
                }else{
                    workbook.setSheetName(0,"还原失败文件信息");
                }
                HSSFFont font = workbook.createFont();
                HSSFCellStyle style = workbook.createCellStyle();
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderTop(BorderStyle.THIN);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setFont(font);
                //创建工作表的行
                HSSFRow row = sheet.createRow(0);
                sheet.setColumnWidth(0, (short) 4000);
                sheet.setColumnWidth(1, (short) 6000);
                sheet.setColumnWidth(2, (short) 8000);
                sheet.setColumnWidth(3, (short) 10000);
                sheet.setColumnWidth(4, (short) 6000);
                HSSFCell cell = row.createCell(0);
                cell.setCellStyle(style);
                cell.setCellValue("表名");

                cell = row.createCell(1);
                cell.setCellStyle(style);
                cell.setCellValue("文件名");

                cell = row.createCell(2);
                cell.setCellStyle(style);
                cell.setCellValue("文件路径");

                cell = row.createCell(3);
                cell.setCellStyle(style);
                cell.setCellValue("失败信息");

                cell = row.createCell(4);
                cell.setCellStyle(style);
                cell.setCellValue("失败时间");
                for(int x =0; x<errorList.size(); x++){
                    row = sheet.createRow(x + 1);
                    cell = row.createCell(0);
                    cell.setCellStyle(style);
                    cell.setCellValue(errorList.get(x).get("tablename"));

                    cell = row.createCell(1);
                    cell.setCellStyle(style);
                    cell.setCellValue(errorList.get(x).get("filename"));

                    cell = row.createCell(2);
                    cell.setCellStyle(style);
                    cell.setCellValue(errorList.get(x).get("filepath"));

                    cell = row.createCell(3);
                    cell.setCellStyle(style);
                    cell.setCellValue(errorList.get(x).get("message"));

                    cell = row.createCell(4);
                    cell.setCellStyle(style);
                    cell.setCellValue(errorList.get(x).get("updateTime"));
                }
                workbook.write(out);
            }

        }catch (Exception e){
            throw e;
        }finally {
        	PubFunc.closeResource(workbook);
        	PubFunc.closeResource(out);
        }
    }

    /**
     * 获取原来设置的文件存储根路径
     * @return
     */
    private String getRootPath() throws DocumentException {
        String rootPath = "";
        RecordVo recordVo= ConstantParamter.getConstantVo("FILEPATH_PARAM");
        String str_value = recordVo.getString("str_value");
        SAXReader saxReader=new SAXReader();
        Document document= saxReader.read(new StringReader(str_value));
        Element root = document.getRootElement();
        Element filePathElement= root.element("FILEPATH");
        rootPath = filePathElement.element("PARAMETER").elementText("OLDPATH");
        return rootPath;
    }

    /**
     * 递归遍历原存储路径，获取所有文件路径列表
     * @param rootPath
     * @return
     */
    private List<FileObject> getAllFileList(String rootPath) throws Exception{
        FileSystemManager fsManager = VFS.getManager();
        FileObject fileObject = fsManager.resolveFile(rootPath);
        List<FileObject> allFileList = new ArrayList<FileObject>();
        getFile(allFileList,fileObject);
        return allFileList;
    }
    private void getFile( List<FileObject> fileList,FileObject fileObject) throws Exception {
        if(fileObject.exists()){
            if(fileObject.isFolder()){
                for(FileObject obj:fileObject.getChildren()){
                    getFile(fileList,obj);
                }
            }else{
                fileList.add(fileObject);
            }
        }
    }

    /**
     * 迁移无规则的文件
     * @param fileMoveDao
     * @param filepath
     * @param fileObject
     */
    private void moveIrregularFile(FileMoveDao fileMoveDao, String filepath,FileObject fileObject) throws Exception {
        String fileid = "";
        InputStream inputStream = null;
        String fileName = "";
        try {
            inputStream = fileObject.getContent().getInputStream();
            fileName = filepath.substring(filepath.lastIndexOf("/") + 1);
            fileid = VfsService.addFile(this.userView.getUserName(), VfsFiletypeEnum.other,VfsModulesEnum.XT, VfsCategoryEnum.other,"",inputStream,fileName,"",false);
            writeResult(fileMoveDao,fileid,filepath,"","",fileName,"","","2","0","");
        } catch (Exception e) {
            e.printStackTrace();
            writeResult(fileMoveDao,fileid,filepath,"","",fileName,"","","2","1",e.getMessage());
        }finally {
            PubFunc.closeIoResource(inputStream);
        }

    }

    /**
     * 更新文件迁移进度信息
     * @param tableName 迁移表
     * @param percent 百分比
     * @param des 描述信息
     */
    private void updateProgress(String tableName,String percent,String des){
        String current = "";
        if(StringUtils.isBlank(tableName)){
            if("0".equalsIgnoreCase(percent)){
                current = "当前正在迁移per_attachment表 ";
            }else if("1".equalsIgnoreCase(percent)){
                current = "迁移完成";
            }else{
                current = "当前正在迁移无规则文件 ";
            }
        }else{
            current = "当前正在迁移"+tableName+"表 ";
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
     * 查询当前文件迁移进度信息
     * @return
     */
    @Override
    public String queryMoveProgress(){
        if(StringUtils.isBlank(progressInfo)){
            progressInfo = "{\"current\":\"\",\"percent\":\"0\",\"des\":\"\"}";
        }
        String path = System.getProperty("java.io.tmpdir");
        path += "/迁移失败文件信息.xls";
        path = path.replace("\\","/");
        try{
            //迁移完成后判断是否有迁移失败的记录
            if(progressInfo.contains("100%")){
                FileSystemManager fsManager = VFS.getManager();
                FileObject fileObject = fsManager.resolveFile(path);
                if(fileObject.exists()){
                    progressInfo = "{\"current\":\"\",\"percent\":\"1\",\"des\":\"100%\",\"error\":true}";
                }else{
                    progressInfo = "{\"current\":\"\",\"percent\":\"1\",\"des\":\"100%\",\"error\":false}";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return progressInfo;
    }

    /**
     * 查询当前文件还原进度信息
     * @return
     */
    @Override
    public String queryRecoveryProgress(){
        String str = FileMoveDaoImpl.getProgressInfo();
        return str;
    }
}
