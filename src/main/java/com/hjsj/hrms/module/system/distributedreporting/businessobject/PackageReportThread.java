package com.hjsj.hrms.module.system.distributedreporting.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.recruitment.util.EmailInfoBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
/**
 *
 *
 * @Titile: DataReportBo
 * @Description:
 * @Company:hjsj
 * @Create time: 2019年6月5日上午11:09:36
 * @author: Zhiyh
 * @version 1.0
 *
 */
public class PackageReportThread implements Runnable{
    private Category log = Category.getInstance(this.getClass().getName());
    // 数据库连接
    private UserView userview ;
    private HashMap<String,FieldItem> fielditemMap;
    // 人员库前缀Map
    private Map dbNameMap = new HashMap<String, String>();
    private String personItemid;
    private HashMap<String,String> personTypeMap;
    private int recordid ;
    private String path ;
    private String guidkey;
    private String unitcode;
    private String asyntype;
    private String unitguid;
    private Date date;
    private DrLogger drLogger;
    private ArrayList<String> protectFieldSetlist;
    private String fieldCondition;
    private void setGuidkey(String guidkey) {
        this.guidkey = guidkey;
    }

    private void setRecordid(int recordid) {
        this.recordid = recordid;
    }

    public PackageReportThread(String path){
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            this.path = path;
            this.date = new Date();
            userview = new UserView("su", conn);
            userview.canLogin(false);
            fielditemMap = getFieldtype(conn);
            getDbNameMap(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(conn);
        }
    }

    @Override
    public void run() {
        try {
            reportData(this.path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void reportData(String path){
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ConstantXml constantXml = new ConstantXml(conn, "BS_ASYN_PLAN_S");
            ArrayList<Element> setList = (ArrayList<Element>) constantXml.getElementList("scheme/fieldSet/set");
            if(!setList.contains("B01")){
                Element element = new Element("set");
                element.setAttribute("setid","B01");
                setList.add(element);
            }
            if(!setList.contains("K01")){
                Element element = new Element("set");
                element.setAttribute("setid","K01");
                setList.add(element);
            }
            if (null!=path) {
                reportOneZip(path,conn,setList);
            }else {
                //1、获取所有的待上传的数据包
                ArrayList<String> list = (ArrayList<String>) FileUtil.getDatapkg(conn);
                //2、一个一个数据包上传
               for (String datapkgPath : list) {
                   try{
                       reportOneZip(datapkgPath,conn,setList);
                       //3、中间库数据导入到档案
                       boolean flag = asynDataToToFileTable(conn,setList);
                       if(flag){
                           updateStatus(this.recordid,"接收成功",conn);
                       }else{
                           updateStatus(this.recordid,"接收失败",conn);
                       }
                   }catch (Exception e){
                       try {
                           if(this.recordid != 0){
                               updateStatus(this.recordid,"接收失败",conn);
                               updateAcceptSituation(conn,e.getMessage());
                           }
                           String emailAddress = getEmailAddress(this.unitguid,conn);
                           sendEamil(emailAddress,"您好！您负责的分步同步在接收上报数据的时候未接收成功！请尽快排查原因！报错信息如下："+e,conn);
                       } catch (GeneralException e1) {
                           e1.printStackTrace();
                       }
                   }
                }
               if(list.size() == 0 ){
                   log.error("分布同步：无下级单位数据包");
               }
            }
        } catch (Exception e) {
            log.error("分布同步：同步数据到上级单位出错");
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(conn);
        }
    }

    /**
     * 上传一个压缩包的数据
     * @param datapkgPath
     * @param conn
     * @param setList
     */
    private  synchronized void reportOneZip(String datapkgPath,Connection conn,ArrayList<Element> setList) throws GeneralException {
        File zipFile = new File(datapkgPath);
        try {
            boolean result = false;
            //获取数据包对象
            if (zipFile.exists()&&!zipFile.isDirectory()) {
              //获取数据包要解压的路径
                String datapath=zipFile.getParentFile().getParent()+File.separator+"data";//将数据包解压到指定目录
                FileUtil.deleteFile(datapath);
                //将数据包解压到指定路径
                FileUtil.decryptZip(datapkgPath, DrConstant.ZIP_PASSWORD, datapath);
                File idxFile = new File(datapath+File.separator+"idx.json");
                if (idxFile.exists()) {
                    String  idxStr= FileUtils.readFileToString(idxFile, "GBK");
                    JSONObject idxJson = JSONObject.fromObject(idxStr);
                    this.unitguid = idxJson.getString("orgid");
                    this.unitcode = idxJson.getString("orgcode");
                    this.asyntype = idxJson.getString("asyntype");
                    this.drLogger  = new DrLogger(this.unitcode,1,this.date);
                    if("1".equalsIgnoreCase(this.asyntype)){
                        deleteFromData(setList,conn,drLogger);//删除库数据
                    }
                   if (datapath.indexOf(unitcode)!=-1) {
                        String logid = idxJson.getString("logid");
                        //根据logid判断此压缩包是否是通过界面传入的压缩文件，如果否，则需要在接收t_sys_asyn_acceptinfo新增记录。
                        HashMap<String,String> recordMap = getCurrentRecord(logid,conn);
                        if (recordMap.isEmpty()) {//不存在新增记录
                           saveDataRecord(idxStr, datapkgPath,conn);
                        }else {
                            int id = Integer.parseInt(recordMap.get("id"));
                            setRecordid(id);
                            setGuidkey(recordMap.get("guidkey"));
                            updateRecordSituation(id,conn);
                        }
                        //3、根据idxJson内容上传数据
                        result = asynJson(idxJson, datapath,conn);
                    }else {
                       drLogger.write("分布同步："+datapkgPath+"压缩包里面idx.json与所在目录的单位不匹配");
                    }
                }else {
                    drLogger.write("分布同步："+datapkgPath+"压缩包里面idx.json不存在");
                }
                if (result) {
                  //5、上传完成：上传成功后将zip数据包移动到finishzip
                    moveZipToFinish(zipFile);
                    drLogger.write("分布同步："+datapath+"数据包移动到finishzip文件夹成功！");
                }
            }
        } catch (Exception e) {
            try{
                moveZipToFinish(zipFile);
                drLogger.write("分布同步：数据包上传失败后移动到finishzip文件夹成功！");
            }catch (Exception e1){
                e.printStackTrace();
            }
            e.printStackTrace();
            drLogger.write("分布同步："+"解压数据包出错："+e);
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 多次上报一个数据包时，清空situation字段信息
     * @param id
     * @param conn
     */
    private void updateRecordSituation(int id, Connection conn) {
        try{
            ContentDAO dao = new ContentDAO(conn);
            List list = new ArrayList();
            list.add(id);
            dao.update("UPDATE t_sys_asyn_acceptinfo set situation = null where id = ? ",list);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 将记录保存在接收记录表里面
     * @param idsdata
     * @param path
     */
    private void saveDataRecord(String idsdata,String path,Connection conn) {
        RowSet rowSet = null;
        try {
            ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
            String prvpath=constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator;
            JSONObject idsJson = JSONObject.fromObject(idsdata);
            String orgid = idsJson.getString("orgid");//guidkey
            String orgname = idsJson.getString("orgname");
            String orgcode = idsJson.getString("orgcode");//单位编码
            String logid = idsJson.getString("logid");
            String asyntype = idsJson.getString("asyntype");//增量还是全量 0是全量 1是增量
            String incstarttime = idsJson.getString("incstarttime");
            String endtime = idsJson.getString("endtime");
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            ContentDAO dao = new ContentDAO(conn);
            int id = 0;
            //查询出接收记录表id的最大值
            String sql = "select max(id) id from t_sys_asyn_acceptinfo";
            rowSet = dao.search(sql);
            if (rowSet.next()) {
                id = rowSet.getInt("id")+1;
            }
            setRecordid(id);
            UUID uuid = UUID.randomUUID();
            String guidkey=uuid.toString().toUpperCase();
            RecordVo vo = new RecordVo("t_sys_asyn_acceptinfo");
            vo.setInt("id", id);
            vo.setString("guidkey", guidkey);
            vo.setString("unitcode", orgcode);
            vo.setString("unitguid", orgid);
            vo.setInt("datatype", Integer.parseInt(asyntype));
            vo.setDate("datastarttime",DateUtils.getTimestamp(incstarttime,"yyyyMMddHHmmssSSS"));
            vo.setDate("dataendtime", DateUtils.getTimestamp(endtime,"yyyyMMddHHmmssSSS"));
            vo.setDate("reporttime", new Date());
            vo.setString("status", DrConstant.RECEIVE_READY);
            vo.setString("pkgguid", logid);
            vo.setString("pkgpath", StringUtils.substringBeforeLast(path, prvpath));
            dao.addValueObject(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }

    }
    /**
     * 根据idx。json
     * @param idxJson idx.json文件里面的内容
     * @param datapath 具体得json文件所在的路径
     */
    private boolean asynJson(JSONObject idxJson,String datapath,Connection conn ) throws GeneralException {
        boolean flag = true;
        try {
            //将 接收 时间记录在接收记录表里面
            updateAccepTime(this.recordid,conn);
            JSONArray empArray = idxJson.getJSONArray("A");//人员的json文件名
            JSONArray unArray = idxJson.getJSONArray("B");//单位的json文件名
            JSONArray postArray = idxJson.getJSONArray("K");//岗位的json文件名
            JSONArray photoArray = idxJson.getJSONArray("P");//照片的文件名
            ArrayList<String> empFielnameList = new ArrayList<String>();
            for (int i = 0; i < empArray.size(); i++) {
                 JSONObject jsonObject =(JSONObject) empArray.get(i);
                 empFielnameList.add(jsonObject.getString("file"));
            }
            ArrayList<String> unFielnameList = new ArrayList<String>();
            for (int i = 0; i < unArray.size(); i++) {
                JSONObject jsonObject =(JSONObject) unArray.get(i);
                unFielnameList.add(jsonObject.getString("file"));
            }
            ArrayList<String> postFielnameList = new ArrayList<String>();
            for (int i = 0; i < postArray.size(); i++) {
                  JSONObject jsonObject =(JSONObject) postArray.get(i);
                 postFielnameList.add(jsonObject.getString("file"));
            }
             ArrayList<String> photoFielnameList = new ArrayList<String>();
             for (int i = 0; i < photoArray.size(); i++) {
                JSONObject jsonObject =(JSONObject) photoArray.get(i);
                 photoFielnameList.add(jsonObject.getString("file"));
            }
            int size =empFielnameList.size()+unFielnameList.size()+postFielnameList.size();
            drLogger.write("分布同步："+"一共:"+size+"个json文件");
            flag=sendJsonToTable(empFielnameList,datapath,"A",conn);
            if (!flag) {
                drLogger.write("分布同步："+datapath+"里面的A01_XX.json同步失败");
                return flag;
            }
            flag=sendJsonToTable(unFielnameList,datapath,"B",conn);
            if (!flag) {
                drLogger.write("分布同步："+datapath+"里面的B01_XX.json同步失败");
                return flag;
            }
            flag=sendJsonToTable(postFielnameList,datapath,"K",conn);
            if (!flag) {
                drLogger.write("分布同步："+datapath+"里面的K01_XX.json同步失败");
                return flag;
            }
            flag=sendPhotoToTable(photoFielnameList, datapath, conn);
            if (!flag) {
                drLogger.write("分布同步："+"同步照片失败");
                return flag;
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
            drLogger.write("分布同步：解析IDX.JSON出错："+e);
            throw GeneralExceptionHandler.Handle(e);
        }
        return flag;
    }

    /**
     * 根据原表创建临时表
     * guidkey 为主键
     * @param srcTable 原表
     * @param tarTable 目标表
     * @param drLogger 日志对象
     */
    private void createTemp(String srcTable, String tarTable,DrLogger drLogger,Connection conn) {
        DbWizard dbw = new DbWizard(conn);
        //DBMetaModel dbmodel = new DBMetaModel(conn);
        try {
            String dbType= SystemConfig.getPropertyValue("dbserver");
            if (dbw.isExistTable(tarTable, false)) {
                dbw.dropTable(tarTable);
            }
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer();
            if ("mssql".equalsIgnoreCase(dbType)) {
                sql.append("select * into ");
                sql.append(tarTable);
                sql.append(" from ");
                sql.append(srcTable);
                sql.append(" where 1=2");
            } else {
                sql.append("create table ");
                sql.append(tarTable);
                sql.append(" as ( select * from ");
                sql.append(srcTable);
                sql.append(" where 1=2)");
            }
            dao.update(sql.toString());
            //dbmodel.reloadTableModel();
            Table table = new Table(tarTable);
            //先添加固定指标
            Field field = new Field("guidkey");
            if (srcTable.endsWith("PHOTO")) {
                field = new Field("emp_id");
            }
            field.setKeyable(true);
            field.setLength(38);
            table.addField(field);
            dbw.addPrimaryKey(table);
           // dbmodel.reloadTableModel();
        } catch (Exception e) {
            e.printStackTrace();
            drLogger.write("分布同步：创建临时表失败("+srcTable+")！" + e.getMessage());
        }

    }
    /**
     * 将照片导入到S_ASYN_PHOTO
     * @param photoNameList
     * @param datapath
     * @param conn
     * @return
     */
    private boolean sendPhotoToTable(ArrayList<String> photoNameList,String datapath,Connection conn) {
        boolean flag = true;
        try {
             ContentDAO dao = new ContentDAO(conn);
             DbWizard dbWizard = new DbWizard(conn);
             if (photoNameList.size()>0) {
                if (!dbWizard.isExistTable("T#S_ASYN_PHOTO", false)) {
                    createTemp("S_ASYN_PHOTO", "T#S_ASYN_PHOTO", drLogger, conn);
                }
             }
             ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
             for (String filename : photoNameList) {
                 String fullname = datapath+File.separator+filename;//照片的全路径
                 File file = new File(fullname);
                 if (file.exists()) {
                     ArrayList<Object> listdata = new ArrayList<Object>();
                     listdata.add(filename.split("\\.")[0]);
                     listdata.add(1);
                     byte[] array = FileUtil.getBytes(fullname);
                     if (Sql_switcher.searchDbServer()==Constant.ORACEL) {
                        listdata.add(FileUtil.getBlob(array, conn));
                     }else {
                        listdata.add(array);
                     }
                     listdata.add("."+filename.split("\\.")[1]);
                     listdata.add(DateUtils.getTimestamp(new Date()));
                     listdata.add(1);
                     list.add(listdata);
                 }
             }
             if (list.size()>0) {
                 StringBuffer sqlbuff = new StringBuffer("insert into T#S_ASYN_PHOTO ");
                 sqlbuff.append("(emp_id,i9999,photo,ext,modtime,modstate) VALUES (?,?,?,?,?,?)");
                 if (list.size()>999) {
                     int pointsDataLimit = 999;
                     List<Object> newList = new ArrayList<Object>();
                     for(int i=0;i<list.size();i++){//分批次处理
                         newList.add(list.get(i));
                         if(pointsDataLimit == newList.size()||i == list.size()-1){
                             dao.batchInsert(sqlbuff.toString(), newList);
                             drLogger.write("分布同步：成功插入照片到T#S_ASYN_PHOTO"+newList.size());
                             newList.clear();
                         }
                     }
                     drLogger.write("分布同步：成功插入照片到T#S_ASYN_PHOTO"+newList.size());
                 }else {
                     dao.batchInsert(sqlbuff.toString(), list);
                     drLogger.write("分布同步：成功插入照片到T#S_ASYN_PHOTO"+list.size());
                 }
                 //更新T#S_ASYN_PHOTO的modstate状态，判断T#S_ASYN_PHOTO的Guidkey在S_ASYN_PHOTO中是否存在
                 dao.update("update T#S_ASYN_PHOTO set modstate = 2 where EXISTS (select 1 from S_ASYN_PHOTO b where T#S_ASYN_PHOTO.emp_id = b.emp_id) ");
                 //将照片临时表的数据更新到中间表
                 sqlbuff  = new StringBuffer();
                 if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                     sqlbuff.append("update S_ASYN_PHOTO  set S_ASYN_PHOTO.photo = b.photo,S_ASYN_PHOTO.ext = b.ext,S_ASYN_PHOTO.modtime = b.modtime,S_ASYN_PHOTO.modstate = b.modstate ");
                     sqlbuff.append(" from T#S_ASYN_PHOTO b where b.emp_id = S_ASYN_PHOTO.emp_id ");
                 }else if(Sql_switcher.searchDbServer() == Constant.ORACEL) {
                     sqlbuff.append("update S_ASYN_PHOTO a set (photo,ext,modtime,modstate) = ");
                     sqlbuff.append("(select photo,ext,modtime,modstate from T#S_ASYN_PHOTO b where a.emp_id = b.emp_id)");
                     sqlbuff.append(" where  exists ( select 1 from T#S_ASYN_PHOTO b where a.emp_id = b.emp_id)");
                 }else if(Sql_switcher.searchDbServer() == Constant.DAMENG){
                     sqlbuff.append("update S_ASYN_PHOTO a set (ext,modtime,modstate) = ");
                     sqlbuff.append("(select ext,modtime,modstate from T#S_ASYN_PHOTO b where a.emp_id = b.emp_id)");
                     sqlbuff.append(" where  exists ( select 1 from T#S_ASYN_PHOTO b where a.emp_id = b.emp_id)");
                 }
                 dao.update(sqlbuff.toString());
                 if(Sql_switcher.searchDbServer() == Constant.DAMENG){
                     sqlbuff.setLength(0);
                     sqlbuff.append("update S_ASYN_PHOTO a set (photo) = ");
                     sqlbuff.append("(select photo from T#S_ASYN_PHOTO b where a.emp_id = b.emp_id)");
                     sqlbuff.append(" where  exists ( select 1 from T#S_ASYN_PHOTO b where a.emp_id = b.emp_id)");
                     dao.update(sqlbuff.toString());
                 }
                 drLogger.write("分布同步：成功将照片数据更新到中间表");
                 //将照片临时表的数据更新到中间表结束
                 //将照片临时表的数据插入到中间表
                 sqlbuff = new StringBuffer("insert into  S_ASYN_PHOTO ");
                 sqlbuff.append(" select * from T#S_ASYN_PHOTO b where not exists ");
                 sqlbuff.append("(select 1 from S_ASYN_PHOTO a where a.emp_id = b.emp_id)");
                 dao.insert(sqlbuff.toString(),new ArrayList());
                 drLogger.write("分布同步：成功将照片数据插入到中间表");
                 //将照片临时表的数据插入到中间表结束
                 // 根据 S_ASYN_A01 更新表S_ASYN_PHOTO 的nbase 和 a0100;
                 sqlbuff= new StringBuffer();
                 if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                     sqlbuff.append("update S_ASYN_PHOTO  set S_ASYN_PHOTO.nbase = b.nbase,S_ASYN_PHOTO.a0100 = b.a0100 ");
                     sqlbuff.append(" from S_ASYN_A01 b where b.guidkey = S_ASYN_PHOTO.emp_id ");
                 }else {
                     sqlbuff.append("update S_ASYN_PHOTO a set (nbase,a0100) = ");
                     sqlbuff.append("(select nbase,a0100 from S_ASYN_A01 b where a.emp_id = b.guidkey)");
                     sqlbuff.append(" where  exists ( select 1 from S_ASYN_A01 b where a.emp_id = b.guidkey)");
                 }
                 dao.update(sqlbuff.toString());
                 //根据 S_ASYN_A01 更新表S_ASYN_PHOTO 的nbase 和 a0100结束;
             }
             if (dbWizard.isExistTable("T#S_ASYN_PHOTO", false)) {
                 dbWizard.dropTable("T#S_ASYN_PHOTO");
             }
        }catch (Exception e) {
           e.printStackTrace();
           drLogger.write("分布同步：同步照片出现异常"+e);
        }
            return flag;
        }
    /**
     * 将json文件里面的json数据转换到表里
     * @param fielnameList json文件的名列表
     * @param datapath json文件的目录
     */
    private boolean sendJsonToTable(ArrayList<String> fielnameList,String datapath,String type,Connection conn) throws GeneralException {
        RowSet rowSet = null;
        Set<String> tTableList = null;
        DbWizard dbw = null;
        boolean flag = true;
        try {
            ContentDAO dao = new ContentDAO(conn);
            dbw = new DbWizard(conn);
            String dbType= SystemConfig.getPropertyValue("dbserver");
            tTableList = new HashSet<String> ();
            for (int i = 0; i < fielnameList.size(); i++) {
            	String filename = fielnameList.get(i);//每一个filename代表一个json文件
                String setid = filename.split("_")[0];
                String tablename = "S_ASYN_"+setid;
                String tTable = "T#"+tablename;
                tTableList.add(tTable);
            }
            if (!"A".equals(type)) {//如果不是人员
                tTableList.add("T#S_ASYN_ORG");
                if (!dbw.isExistTable("T#S_ASYN_ORG", false)) {
                    //根据原表创建临时表
                    createTemp("S_ASYN_ORG", "T#S_ASYN_ORG", drLogger,conn);
                }
            }
            //将所有的数据放入临时表开始
            for (int j = 0; j < fielnameList.size(); j++) {
            	String filename = fielnameList.get(j);//每一个filename代表一个json文件
                filename = filename+".json";
                String setid = filename.split("_")[0];
                String tablename = "S_ASYN_"+setid;
                String tarTable = "T#"+tablename;
                if (!dbw.isExistTable(tarTable, false)) {
                    //根据原表创建临时表
                    createTemp(tablename, tarTable, drLogger,conn);
                }
                File file = new File(datapath+File.separator+filename);
                String dataStr = FileUtils.readFileToString(file, "GBK");
                JSONObject jsondata = JSONObject.fromObject(dataStr);
                //这个json文件的所有记录数
                JSONArray jsonArray = jsondata.getJSONArray(setid);
                //记录要插进表里面的字段
                ArrayList<String > fielditemList = new ArrayList<String>();
                //记录要插入到T#S_ASYN_ORG 的字段
                ArrayList<String > orgitemList = new ArrayList<String>();
                ArrayList list = new ArrayList();
                ArrayList<ArrayList<Object>> orgList = new ArrayList<ArrayList<Object>>();
                drLogger.write("分布同步："+ filename+"的数据有"+jsonArray.size()+"条");
                Set<String> set = new HashSet<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    ArrayList<Object> datalist =  new ArrayList<Object>();
                    ArrayList<Object> orgdatalist =  new ArrayList<Object>();
                    JSONObject json = jsonArray.getJSONObject(i);
                    Map<String, Object> mapJson = json;
                    Iterator<Entry<String, Object>> iterator = mapJson.entrySet().iterator();
                    while (iterator.hasNext()) {
                        String key =  iterator.next().getKey();
                        String value = (String) mapJson.get(key);
                        value = "".equals(value)?null:value;
                        if ("AID".equals(key) || "BID".equals(key) || "KID".equals(key)) {
                            datalist.add(value);
                            orgdatalist.add(value);
                            if (i==0) {
                                fielditemList.add("guidkey");
                                orgitemList.add("guidkey");
                            }
                            if(set.contains(value)) {
                            	drLogger.write("分布同步："+ filename+"的数据重复:"+value);
                            }
                            set.add(value);
                            if (value == null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：guidkey为空");
                            }
                        }else if ("CODEITEMDESC".equalsIgnoreCase(key)) {//单位和岗位
                            orgdatalist.add(value);
                            if (i==0) {
                                orgitemList.add("CODEITEMDESC");
                            }
                            if (value == null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：CODEITEMDESC为空");
                            }
                        }else if ("CODESETID".equalsIgnoreCase(key)) {//单位和岗位
                            orgdatalist.add(value);
                            if (i==0) {
                                orgitemList.add("CODESETID");
                            }
                            if (value == null) {
                                drLogger.write(filename+"的"+json+"：CODESETID为空");
                            }
                        }else if ("CODEITEMID".equalsIgnoreCase(key)) {//单位和岗位
                            orgdatalist.add(value);
                            if (i==0) {
                                orgitemList.add("CODEITEMID");
                            }
                            if (value == null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：CODEITEMID为空");
                            }
                        }else if ("A0000".equalsIgnoreCase(key)) {//单位和岗位
                            orgdatalist.add(value);
                            if (i==0) {
                                orgitemList.add("A0000");
                            }
                        }else if ("GRADE".equalsIgnoreCase(key)) {//单位和岗位
                            orgdatalist.add(value);
                            if (i==0) {
                                orgitemList.add("GRADE");
                            }
                        }else if ("PARENTID".equalsIgnoreCase(key)) {//单位和岗位
                            orgdatalist.add(value);
                            if (i==0) {
                                orgitemList.add("PARENTID");
                            }
                            if (value == null) {
                                drLogger.write(filename+"的"+json+"：PARENTID为空");
                            }
                        }else if ("EMP_ID".equalsIgnoreCase(key)) {//子集
                            datalist.add(value);
                            if (i==0) {
                                fielditemList.add("EMP_ID");
                            }
                            if (value == null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：EMP_ID为空");
                            }
                        }else if ("ORG_ID".equalsIgnoreCase(key)) {//子集
                            datalist.add(value);
                            if (i==0) {
                                fielditemList.add("ORG_ID");
                            }
                            if (value == null) {
                                drLogger.write(filename+"的"+json+"：ORG_ID为空");
                            }
                        }else if ("POST_ID".equalsIgnoreCase(key)) {//子集
                            datalist.add(value);
                            if (value == null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：POST_ID为空");
                            }
                            if (i==0) {
                                fielditemList.add("POST_ID");
                            }
                        }else if ("I9999".equalsIgnoreCase(key)) {
                            if (value == null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：I9999为空");
                                value = "0";
                            }
                            datalist.add(Integer.parseInt(value));
                            if (i==0) {
                                fielditemList.add("I9999");
                            }
                        }else if ("MODSTATE".equalsIgnoreCase(key)) {
                            if (value == null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：MODSTATE为空");
                                value = "1";
                            }
                            datalist.add(Integer.parseInt(value));
                            orgdatalist.add(Integer.parseInt(value));
                            if (i==0) {
                                fielditemList.add("MODSTATE");
                                orgitemList.add("MODSTATE");
                            }
                        }else if ("B0110".equalsIgnoreCase(key)) {
                            datalist.add(value);
                            if (i==0) {
                                fielditemList.add("B0110");
                            }
                            if (value == null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：B0110为空");
                            }
                        }else if ("E01A1".equalsIgnoreCase(key)) {
                            datalist.add(value);
                            if (i==0) {
                                fielditemList.add("E01A1");
                            }
                            if (value == null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：E01A1为空");
                            }
                        }else if ("MODTIME".equalsIgnoreCase(key)) {//都有的字段
                            if (i==0) {
                                fielditemList.add("MODTIME");
                                orgitemList.add("MODTIME");
                            }
                            if (value ==null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：modtime为空");
                                datalist.add(DateUtils.getSqlDate(new Date()));
                                orgdatalist.add(DateUtils.getSqlDate(new Date()));
                            }else {
                                datalist.add(DateUtils.getTimestamp(value,"yyyy-MM-dd HH:mm:ss"));
                                orgdatalist.add(DateUtils.getTimestamp(value,"yyyy-MM-dd HH:mm:ss"));
                            }
                        }else if ("CREATETIME".equalsIgnoreCase(key)) {//都有的字段
                            if (i==0) {
                                fielditemList.add("CREATETIME");
                            }
                            if (value ==null) {
                                datalist.add(DateUtils.getSqlDate(new Date()));
                            }else {
                                datalist.add(DateUtils.getTimestamp(value,"yyyy-MM-dd HH:mm:ss"));
                            }
                        }else if ("START_DATE".equalsIgnoreCase(key)) {//岗位和单位
                            if (i==0) {
                                orgitemList.add("START_DATE");
                            }
                            if (value ==null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：START_DATE为空");
                                orgdatalist.add(DateUtils.getSqlDate(new Date()));
                            }else {
                                orgdatalist.add(DateUtils.getTimestamp(value,"yyyy-MM-dd HH:mm:ss"));
                            }
                        }else if ("END_DATE".equalsIgnoreCase(key)) {//岗位和单位
                            if (i==0) {
                                orgitemList.add("END_DATE");
                            }
                            if (value ==null) {
                                drLogger.write("分布同步："+ filename+"的"+json+"：END_DATE为空");
                                orgdatalist.add(DateUtils.getSqlDate(new Date()));
                            }else {
                                orgdatalist.add(DateUtils.getTimestamp(value,"yyyy-MM-dd HH:mm:ss"));
                            }
                        }else {
                            FieldItem fieldItem = fielditemMap.get(setid+"_"+key);
                            if (fieldItem!=null) {
                                if (i==0) {
                                    fielditemList.add(key);
                                }
                                String itemtype =fieldItem.getItemtype();
                                if ("N".equals(itemtype)) {
                                    int width = fieldItem.getDecimalwidth();
                                    if (value ==null) {
                                        datalist.add(value);
                                    }else {
                                        if (width>0) {//小数
                                            datalist.add(Double.parseDouble(value));
                                        }else {//整数
                                            datalist.add(Integer.parseInt(value));
                                        }
                                    }
                                }else if ("D".equals(itemtype)) {
                                    if (StringUtils.isNotEmpty(value)) {
                                        datalist.add(DateUtils.getTimestamp(value,"yyyy-MM-dd HH:mm:ss"));
                                    }else {
                                        datalist.add(value);
                                    }
                                }else {
                                    datalist.add(value);
                                }
                            }else {
                                drLogger.write("分布同步："+ filename+"的"+json+"：的"+key+"字段在上级单位未定义");
                            }
                        }
                    }
                    list.add(datalist);
                    orgList.add(orgdatalist);
                }
                drLogger.write("分布同步："+ filename+"的不重复的数据有"+set.size()+"条");
                //将每个json 文件都放入到临时表
                if (fielditemList.size()>0) {
                    //根据fielditemList 和 list拼装sql
                    StringBuffer sqlbuff = new StringBuffer("insert into ");
                    sqlbuff.append(tarTable);
                    sqlbuff.append(" (");
                    for (int i = 0; i < fielditemList.size(); i++) {
                        if (i!=0) {
                            sqlbuff.append(",");
                        }
                        sqlbuff.append(fielditemList.get(i));
                    }
                    sqlbuff.append(") VALUES (");
                    for (int i = 0; i < fielditemList.size(); i++) {
                        if (i!=0) {
                            sqlbuff.append(",");
                        }
                        sqlbuff.append("?");
                    }
                    sqlbuff.append(")");
                    dao.batchInsert(sqlbuff.toString(), list);
                    drLogger.write("分布同步：成功插入"+filename+"的数据到"+tarTable);
                }
                if (!"A".equals(type)&& tarTable.endsWith("01")) {//还需要向组织机构表中插入
                  //根据fielditemList 和 list拼装sql
                    StringBuffer sqlbuff = new StringBuffer("insert into ");
                    sqlbuff.append("T#S_ASYN_ORG ");
                    sqlbuff.append(" (");
                    for (int i = 0; i < orgitemList.size(); i++) {
                        if (i!=0) {
                            sqlbuff.append(",");
                        }
                        sqlbuff.append(orgitemList.get(i));
                    }
                    sqlbuff.append(") VALUES (");
                    for (int i = 0; i < orgitemList.size(); i++) {
                        if (i!=0) {
                            sqlbuff.append(",");
                        }
                        sqlbuff.append("?");
                    }
                    sqlbuff.append(")");
                    dao.batchInsert(sqlbuff.toString(), orgList);
                    drLogger.write("分布同步：成功将数据插从"+filename+"插入到T#S_ASYN_ORG"+orgList.size());
                }
            }
            //将所有数据导入临时表结束
            //如果是人员将临时表中不符合校验规则的数据删除
            if ("A".equals(type)) {
                //将不符合校验规则的数据进行删除
                filterValidateRules(datapath,tTableList,drLogger,conn);
                //过滤受保护人员条件
                filterProtectPeople(drLogger,conn,tTableList);
                //过滤受保护指标
                filterProtectField(drLogger,conn);
                //如果是人员在操作中间表之前将S_ASYN_A01的srcnbase字段更新为nbase的值
                dao.update("update S_ASYN_A01 set srcnbase = nbase ");
            }
            //过滤唯一和必填数据
            filterMustbeAndUniqueData(datapath,tTableList,drLogger,conn);
            //将临时表的数据更新至中间表
            for (String tTableName : tTableList) {
                String centerTable = tTableName.substring(2);
                rowSet=dao.search("select * from "+tTableName+" where 1=2");
                ResultSetMetaData mt=rowSet.getMetaData();
                ArrayList<String> columnList = new ArrayList<String>();
                for(int i=0;i<mt.getColumnCount();i++) {
                    String columnName= mt.getColumnName(i+1);
                    if (!"a0100".equalsIgnoreCase(columnName)&&!"NBASE".equalsIgnoreCase(columnName)&&!"SRCNBASE".equalsIgnoreCase(columnName)) {
                        columnList.add(columnName);
                    }
                }
                StringBuffer sqlbuff = new StringBuffer("");
                if ("mssql".equalsIgnoreCase(dbType)) {
                    sqlbuff.append("update ");
                    sqlbuff.append(centerTable);
                    sqlbuff.append(" set ");
                    for (int i = 0;i<columnList.size();i++) {
                        if (i!=0) {
                            sqlbuff.append(",");
                        }
                        sqlbuff.append(centerTable);
                        sqlbuff.append(".");
                        sqlbuff.append(columnList.get(i));
                        sqlbuff.append(" = b.");
                        sqlbuff.append(columnList.get(i));
                    }
                    sqlbuff.append(" from "+tTableName+" b where b.guidkey = ");
                    sqlbuff.append(centerTable);
                    sqlbuff.append(".guidkey");
                    if ("A".equals(type)) {
                        sqlbuff.append(" and (b.PROTECTSTATUS is null or b.PROTECTSTATUS = '') ");
                    }
                }else {
                    sqlbuff.append("update ");
                    sqlbuff.append(centerTable);
                    sqlbuff.append(" a set (");
                    for (int i = 0;i<columnList.size();i++) {
                        if (i!=0) {
                            sqlbuff.append(",");
                        }
                        sqlbuff.append(columnList.get(i));
                    }
                    sqlbuff.append(") = (");
                    sqlbuff.append("select ");
                    boolean isDameng = Sql_switcher.searchDbServerFlag()==Constant.DAMENG;
                    for (int i = 0;i<columnList.size();i++) {
                        String column = columnList.get(i);
                        FieldItem item=DataDictionary.getFieldItem(column);
                        if (i!=0) {
                            sqlbuff.append(",");
                        }
                        if(isDameng && item!=null && "M".equalsIgnoreCase(item.getItemtype())){
                            sqlbuff.append(Sql_switcher.sqlToChar(column));
                            sqlbuff.append(" ");
                        }
                        sqlbuff.append(column);

                    }
                    sqlbuff.append(" from ");
                    sqlbuff.append(tTableName);
                    sqlbuff.append(" b where a.guidkey = b.guidkey ");
                    if ("A".equals(type)) {
                        sqlbuff.append(" and PROTECTSTATUS is null ");
                        sqlbuff.append(") where  exists ( select 1 from "+tTableName+" b where a.guidkey = b.guidkey and PROTECTSTATUS is null)");
                    }else{
                        sqlbuff.append(") where  exists ( select 1 from "+tTableName+" b where a.guidkey = b.guidkey )");
                    }
                }
                dao.update(sqlbuff.toString());
                updateProtectFieldData(conn,tTableName);
                //更新新增的guidkey重复的modstate=2,避免上传重复包数据
                String srcTab = tTableName;
                String destTab = centerTable;
                String strJoin = srcTab+".guidkey = "+destTab+".guidkey";
                String strSWhere = srcTab+".modstate = 1";
                String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
                String strSet = destTab+".modstate = 2";
                String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
                dao.update(updateSql);
                drLogger.write("分布同步：成功将临时表"+tTableName+"的数据更新至中间表"+tTableName.substring(2));
            }
            //将临时表的数据更新至中间表结束
            //将临时表的数据插入到中间表
            for (String tTableName : tTableList) {
                StringBuffer sqlbuff = new StringBuffer("");
                String tarTable = tTableName.substring(2);
                sqlbuff.append("insert into ");
                sqlbuff.append(tarTable);
                sqlbuff.append(" select * from ");
                sqlbuff.append(tTableName);
                sqlbuff.append(" b where not exists  (select 1 from ");
                sqlbuff.append(tarTable);
                sqlbuff.append(" a where a.guidkey = b.guidkey)");
                dao.insert(sqlbuff.toString(),new ArrayList());
                drLogger.write("分布同步：成功将临时表"+tTableName+"的数据插入到中间表"+tarTable);
            }
            //将临时表的数据插入到中间表结束
            if ("A".equals(type)) {//如果是人员
              //将中间表 人员主集的nbase 字段根据人员状态指标填充
                for (String tTableName : tTableList) {
                    tTableName = tTableName.substring(2);
                    if (tTableName.endsWith("A01")&&this.personItemid!=null&&this.personTypeMap.size()>=0) {//人员主表
                        for (String key : this.personTypeMap.keySet()) {
                            StringBuffer sqlbuff = new StringBuffer("");
                            sqlbuff.append("update ");
                            sqlbuff.append(tTableName.toUpperCase());
                            sqlbuff.append(" set nbase ='");
                            sqlbuff.append(this.personTypeMap.get(key));
                            sqlbuff.append("' where ");
                            sqlbuff.append(this.personItemid );
                            sqlbuff.append(" in (");
                            sqlbuff.append(key);
                            sqlbuff.append(")");
                            dao.update(sqlbuff.toString());
                            drLogger.write("分布同步：成功更新了中间表"+tTableName+"nbase的值为："+this.personTypeMap.get(key));
                        }
                    }
                    //如果人员状态指标不存在 或则未对应
                    if (tTableName.endsWith("A01")&&(this.personItemid==null||this.personTypeMap.size()==0)) {
                        StringBuffer sqlbuff = new StringBuffer("");
                        sqlbuff.append("update ");
                        sqlbuff.append(tTableName);
                        sqlbuff.append(" set nbase ='Usr'");
                        dao.update(sqlbuff.toString());
                    }
                }
                //更中间表子表里面的人员状态
                for (String tTableName : tTableList) {
                    if (!tTableName.endsWith("A01")) {//人员子表
                        StringBuffer sqlbuff = new StringBuffer("");
                        if ("mssql".equalsIgnoreCase(dbType)) {
                            sqlbuff.append("update ");
                            sqlbuff.append(tTableName.substring(2));
                            sqlbuff.append(" set ");
                            sqlbuff.append(tTableName.substring(2));
                            sqlbuff.append(".nbase = b.nbase  from S_ASYN_A01 b where b.guidkey = ");
                            sqlbuff.append(tTableName.substring(2));
                            sqlbuff.append(".emp_id ");
                        }else {
                            sqlbuff.append("update ");
                            sqlbuff.append(tTableName.substring(2));
                            sqlbuff.append(" a set a.nbase = ( select b.nbase from S_ASYN_A01 b where a.emp_id = b.guidkey )");
                            sqlbuff.append(" where exists ( select 1 from S_ASYN_A01 b where a.emp_id = b.guidkey)");
                        }
                        dao.update(sqlbuff.toString());
                        drLogger.write("分布同步：成功更新了中间表"+tTableName.substring(2)+"的nbase");
                    }
                }
            }
            //删除临时表
            for (String tTableName : tTableList) {
                if (dbw.isExistTable(tTableName, false)) {
                    dbw.dropTable(tTableName);
                    drLogger.write("分布同步：删除临时表"+tTableName+"成功！");
                }
            }
        } catch (Exception e) {
            //删除临时表
            for (String tTableName : tTableList) {
                if (dbw.isExistTable(tTableName, false)) {
                    dbw.dropTable(tTableName);
                }
            }
            flag = false;
            e.printStackTrace();
            drLogger.write("分布同步：数据插入中间表失败"+e);
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return flag;
    }

    /**
     * 更新受保护指标数据
     */
    private void updateProtectFieldData(Connection conn,String tTableName) {
        ContentDAO dao = new ContentDAO(conn);
        ResultSet rs = null;
        Statement stmt = null;
        try {
            if(this.protectFieldSetlist!=null && this.protectFieldSetlist.size()>0){
                for (String set : this.protectFieldSetlist) {
                	try {

                        if(tTableName.endsWith(set)){
                            stmt = conn.createStatement();
                            rs = stmt.executeQuery("select * from t#s_asyn_" + set
                                    + " where protectstatus = 0");
                            ResultSetMetaData meta = rs.getMetaData();
                            int count = meta.getColumnCount();
                            while (rs.next()) {
                                List list = new ArrayList();
                                StringBuffer sql = new StringBuffer();
                                for (int i = 0; i < count; i++) {
                                    String colName = meta.getColumnName(i + 1);
                                    if(!"GUIDKEY".equalsIgnoreCase(colName)&&!"PROTECTSTATUS".equalsIgnoreCase(colName)&&!"NBASE".equalsIgnoreCase(colName)&&!"SRCNBASE".equalsIgnoreCase(colName)&&!"A0100".equalsIgnoreCase(colName)){
                                        boolean exist = this.fieldCondition.toUpperCase().contains(colName);
                                        if (!exist) {
                                            String coltype = meta.getColumnTypeName(i + 1);
                                            sql.append(",");
                                            sql.append(colName + "=? ");
                                            if ("DATE".equalsIgnoreCase(coltype)) {
                                                list.add(rs.getTimestamp(colName));
                                            } else if ("CLOB".equalsIgnoreCase(coltype)) {
                                                list.add(rs.getString(colName));
                                            } else {
                                                list.add(rs.getObject(colName));
                                            }
                                        }
                                    }
                                }
                                list.add(rs.getString("guidkey"));
                                String sqlstr = "update s_asyn_"+set+" set "
                                        + sql.substring(1) + " where guidkey = ?";
                                dao.update(sqlstr, list);
                            }
                        }
					} finally {
						PubFunc.closeResource(stmt);
						PubFunc.closeResource(rs);
					}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(stmt);
        }
    }

    /**
     * 过滤唯一和必填数据
     * @param datapath
     * @param tTableList
     * @param drLogger
     * @param conn
     */
    private void filterMustbeAndUniqueData(String datapath,Set<String> tTableList, DrLogger drLogger, Connection conn) {
        try{
            for (String temptableName : tTableList) {
                String setId = temptableName.substring(9,12);
                //获取必填和唯一字段
                HashMap<String,ArrayList> columnMap = getMustbeAndUniqueColumn(setId,conn);
                ArrayList columnMustList = columnMap.get("columnMustList");
                ArrayList columnUniquelist = columnMap.get("columnUniquelist");
                if(columnMustList.size() > 0){
                    saveFilterMustDataToRecord(setId,columnMustList,conn,datapath,tTableList);
                }
                if(columnUniquelist.size() > 0){
                    saveFilterUniqueDataToRecord(setId,columnUniquelist,conn,datapath,tTableList);
                }
            }
        }catch (Exception e){
            drLogger.write("分布同步：过滤必填和唯一失败！"+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param setId
     * @param columnUniquelist
     * @param conn
     * @param datapath
     * @param tTableList
     */
    private void saveFilterUniqueDataToRecord(String setId, ArrayList<String> columnUniquelist, Connection conn, String datapath, Set<String> tTableList) throws GeneralException {
        RowSet rs = null;
        try{
            String tablename = "t#s_asyn_"+setId;
            ContentDAO dao = new ContentDAO(conn);
            for (int i = 0; i < columnUniquelist.size(); i++) {
                ArrayList<ArrayList<Object>>  list = new ArrayList<ArrayList<Object>>();
                ArrayList<String> photoList = new ArrayList<String>();
                String itemid = columnUniquelist.get(i);
                StringBuffer sqlwhere = new StringBuffer();
                sqlwhere.append(itemid);
                sqlwhere.append(" in ( select ");
                sqlwhere.append(itemid);
                sqlwhere.append(" from ( select ");
                sqlwhere.append(itemid);
                sqlwhere.append(" from s_asyn_"+setId);
                sqlwhere.append(" where modstate = 0 union all SELECT ");
                sqlwhere.append(itemid);
                sqlwhere.append(" from ");
                sqlwhere.append(tablename);
                sqlwhere.append(" ) temp GROUP BY ");
                sqlwhere.append(itemid);
                sqlwhere.append(" HAVING count(*)>1)");
                String sql = "select guidkey,"+itemid+",a0101 as name from "+tablename+" where "+sqlwhere+" and modstate = 1";
                rs = dao.search(sql);
                while (rs.next()) {
                    String guidkey = rs.getString("guidkey");
                    photoList.add(guidkey);
                    String name = rs.getString("name");
                    ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("itemid", itemid);
                    map.put("value", rs.getString(itemid));
                    map.put("reason",itemid+"不符合唯一性指标条件");
                    list1.add(map);
                    ArrayList<Object> listObject = new ArrayList<Object>();
                    listObject.add("A");
                    listObject.add(setId);
                    listObject.add(name);
                    listObject.add(guidkey);
                    listObject.add(list1);
                    list.add(listObject);
                }
                if(list.size()>0){
                    saveRecordTable(list, conn,drLogger);
                    deletePhoto(datapath,photoList, drLogger);
                    for (String tempTable : tTableList) {
                        String firstSet = tempTable.substring(9, 10).toUpperCase();
                        if(!tempTable.toUpperCase().endsWith("A01") && "A".equalsIgnoreCase(firstSet)){
                            dao.update("delete from "+tempTable+" where emp_id in (select guidkey from t#s_asyn_a01 where "+sqlwhere+")");
                        }
                    }
                    dao.update("delete from t#s_asyn_a01 where "+sqlwhere);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 获取必填和唯一过滤字段
     * @param setId
     * @param conn
     * @return
     */
    private HashMap<String, ArrayList> getMustbeAndUniqueColumn(String setId, Connection conn) {
        HashMap<String,ArrayList> mustAndUniqueColumnMap = new HashMap();
        ArrayList<String> columnMustList = new ArrayList<String>();
        ArrayList<String> columnUniquelist = new ArrayList<String>();
        try{
            ConstantXml constantXml = new ConstantXml(conn, "BS_ASYN_PLAN_S");
            ArrayList<Element> fieldItemList= (ArrayList<Element>) constantXml.getElementList("scheme/fieldItem/item");
            for (Element element : fieldItemList) {
                String setid = element.getAttributeValue("setid");
                if(setid.equalsIgnoreCase(setId)){
                    String itemid = element.getAttributeValue("itemid");
                    String mustbe = element.getAttributeValue("mustfill");
                    String uniquefiag = element.getAttributeValue("uniq");
                    if("TRUE".equalsIgnoreCase(mustbe)){
                        columnMustList.add(itemid);
                    }
                    if ("TRUE".equalsIgnoreCase(uniquefiag)){
                        columnUniquelist.add(itemid);
                    }
                }
            }
            mustAndUniqueColumnMap.put("columnMustList",columnMustList);
            mustAndUniqueColumnMap.put("columnUniquelist",columnUniquelist);
        }catch (Exception e){
            e.printStackTrace();
        }
        return mustAndUniqueColumnMap;
    }

    /**
     * 将过滤必填数据保存到日志表中
     * @param columnlist
     * @param conn
     * @param datapath
     * @param tTableList
     */
    private void saveFilterMustDataToRecord(String setid, ArrayList<String> columnlist, Connection conn, String datapath, Set<String> tTableList) throws GeneralException {
        String sql = "";
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(conn);
            String firstTable = setid.substring(0, 1).toUpperCase();
            String tablename = "t#s_asyn_"+setid;
            for (int i = 0; i < columnlist.size(); i++) {
                ArrayList<String> photoList = new ArrayList<String>();
                ArrayList<ArrayList<Object>>  list = new ArrayList<ArrayList<Object>>();
                String itemid = columnlist.get(i);
                FieldItem item = DataDictionary.getFieldItem(itemid);
                String itemType = item.getItemtype();
                StringBuffer sqlwhere = new StringBuffer();
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sqlwhere.append(itemid + " is null ");
                    if("A".equalsIgnoreCase(itemType)){
                        sqlwhere.append(" or "+itemid+" = ''");
                    }
                } else {
                    sqlwhere.append(itemid + " is null ");
                }
                if("A01".equalsIgnoreCase(setid)){
                    sql = "select guidkey as id,"+itemid+",a0101 as name from "+tablename+" where "+sqlwhere;
                }else if (!"A01".equalsIgnoreCase(setid) && "A".equalsIgnoreCase(firstTable)){
                    sql = "select "+tablename+".guidkey as id,emp_id,"+itemid+",a0101 as name from "+tablename+"  LEFT JOIN S_ASYN_A01 on "+tablename+".emp_id = S_ASYN_A01.guidkey where "+sqlwhere;
                }else if ("B".equalsIgnoreCase(firstTable)){
                    sql = "select guidkey as id,"+itemid+",b0110 as name from "+tablename+" where "+sqlwhere;
                }else if ("K".equalsIgnoreCase(firstTable)){
                    sql = "select guidkey as id,"+itemid+",b0110 as name from "+tablename+" where "+sqlwhere;
                }
                rs=dao.search(sql);
                while (rs.next()) {
                    String guidkey = rs.getString("id");
                    photoList.add(guidkey);
                    String name = rs.getString("name");
                    ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("itemid", itemid);
                    map.put("value", rs.getString(itemid));
                    map.put("reason",itemid+"不符合必填指标条件");
                    list1.add(map);
                    ArrayList<Object> listObject = new ArrayList<Object>();
                    listObject.add(firstTable);
                    listObject.add(setid);
                    listObject.add(name);
                    listObject.add(guidkey);
                    listObject.add(list1);
                    list.add(listObject);
                }
                if(list.size()>0){
                    saveRecordTable(list, conn,drLogger);//保存记录到日志表
                    if("A".equalsIgnoreCase(firstTable)){
                        deletePhoto(datapath,photoList, drLogger);
                        for (String tempTable : tTableList) {
                            String firstSet = tempTable.substring(9, 10).toUpperCase();
                            if(!tempTable.toUpperCase().endsWith("A01") && "A".equalsIgnoreCase(firstSet)){
                                dao.update("delete from "+tempTable+" where emp_id in (select guidkey from t#s_asyn_a01 where "+sqlwhere+")");
                            }
                        }
                        dao.update("delete from t#s_asyn_a01 where "+sqlwhere);
                    }else{
                        dao.update("delete from t#s_asyn_"+setid+" where "+sqlwhere);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 将符合受保护人员的条件的人从临时表删除
     * @param drLogger
     * @param tTableList
     */
    private void filterProtectPeople(DrLogger drLogger, Connection conn, Set<String> tTableList) {
        try {
            ConstantXml constantXml = new ConstantXml(conn, "BS_ASYN_PLAN_S");
            Element protectPeopleElement = constantXml.getElement("scheme/protectPeople");
            ContentDAO dao = new ContentDAO(conn);
            if (protectPeopleElement!=null&&"true".equalsIgnoreCase(protectPeopleElement.getAttributeValue("checkbox"))) {
                Element dbnameElement = constantXml.getElement("scheme/protectPeople/dbname");
                String pre = dbnameElement.getAttributeValue("pre");
                String[] nbaseArray = pre.split(",");
                Element peopleConditionElement = constantXml.getElement("scheme/protectPeople/peopleCondition");
                String  peopleCondition = peopleConditionElement.getAttributeValue("condition");
                if (StringUtils.isNotEmpty(pre)&&StringUtils.isNotEmpty(peopleCondition)) {
                    for (int i = 0; i < nbaseArray.length; i++) {
                        ArrayList allUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
                        YksjParser yp = new YksjParser(this.userview, allUsedFields,YksjParser.forSearch, YksjParser.LOGIC,YksjParser.forPerson, "gw", nbaseArray[i]);
                        yp.setCon(conn);
                        // 解析sql
                        yp.run_Where(peopleCondition, null, "", "", new ContentDAO(conn), "",conn, "A", null);
                        String tempTable = yp.getTempTableName();
                        String resultSql = yp.getSQL();
                        drLogger.write("分布同步：受保护人员条件生成的sql："+resultSql);
                        String sql = " select a0100 from " +tempTable+" where " + resultSql;
                        for (String tableName : tTableList) {
                            //人员主集
                            if(tableName.endsWith("A01")){
                                StringBuffer sqlbuff = new StringBuffer();
                                sqlbuff.append("delete from ");
                                sqlbuff.append(tableName);
                                sqlbuff.append(" where guidkey in (SELECT guidkey from ");
                                sqlbuff.append(nbaseArray[i]);
                                sqlbuff.append("a01 where a0100 in (");
                                sqlbuff.append(sql);
                                sqlbuff.append("))");
                                dao.update(sqlbuff.toString());
                            }else{
                                StringBuffer sqlbuff = new StringBuffer();
                                sqlbuff.append("delete from ");
                                sqlbuff.append(tableName);
                                sqlbuff.append(" where emp_id in (SELECT guidkey from ");
                                sqlbuff.append(nbaseArray[i]);
                                sqlbuff.append("a01 where a0100 in (");
                                sqlbuff.append(sql);
                                sqlbuff.append("))");
                                dao.update(sqlbuff.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            drLogger.write("分布同步：受保护人员条件方法出错："+e);
        }
    }

    /**
     * 过滤受保护指标
     * @param drLogger
     * @param conn
     */
    private void filterProtectField(DrLogger drLogger, Connection conn) {
        try {
            ConstantXml constantXml = new ConstantXml(conn, "BS_ASYN_PLAN_S");
            Element protectPeopleElement = constantXml.getElement("scheme/protectField");
            ContentDAO dao = new ContentDAO(conn);
            if (protectPeopleElement!=null&&"true".equalsIgnoreCase(protectPeopleElement.getAttributeValue("checkbox"))) {
                Element dbnameElement = constantXml.getElement("scheme/protectField/dbname");
                String pre = dbnameElement.getAttributeValue("pre");
                String[] nbaseArray = pre.split(",");
                Element peopleConditionElement = constantXml.getElement("scheme/protectField/peopleCondition");
                String  peopleCondition = peopleConditionElement.getAttributeValue("condition");
                Element fieldConditionElement = constantXml.getElement("scheme/protectField/fieldCondition");
                this.fieldCondition = fieldConditionElement.getAttributeValue("condition");
                if (StringUtils.isNotEmpty(pre)&&StringUtils.isNotEmpty(peopleCondition)&&StringUtils.isNotEmpty(fieldCondition)) {
                    for (int i = 0; i < nbaseArray.length; i++) {
                        ArrayList allUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
                        YksjParser yp = new YksjParser(this.userview, allUsedFields,YksjParser.forSearch, YksjParser.LOGIC,YksjParser.forPerson, "gw", nbaseArray[i]);
                        yp.setCon(conn);
                        // 解析sql
                        yp.run_Where(peopleCondition, null, "", "", new ContentDAO(conn), "",conn, "A", null);
                        String tempTable = yp.getTempTableName();
                        this.protectFieldSetlist = yp.getUsedSets();
                        String resultSql = yp.getSQL();
                        drLogger.write("分布同步：受保护指标条件生成的sql："+resultSql);
                        String sql = " select a0100 from " +tempTable+" where " + resultSql;
                        for (String tableName : this.protectFieldSetlist) {
                            if("A01".equalsIgnoreCase(tableName)){
                                StringBuffer sqlbuff = new StringBuffer();
                                sqlbuff.append("update t#s_asyn_a01 ");
                                sqlbuff.append(" set protectstatus = 0 where guidkey in (SELECT guidkey from ");
                                sqlbuff.append(nbaseArray[i]);
                                sqlbuff.append("a01 where a0100 in (");
                                sqlbuff.append(sql);
                                sqlbuff.append("))");
                                dao.update(sqlbuff.toString());
                            }else{
                                StringBuffer sqlbuff = new StringBuffer();
                                sqlbuff.append("update t#s_asyn_");
                                sqlbuff.append(tableName);
                                sqlbuff.append(" set protectstatus = 0 where emp_id in (SELECT guidkey from ");
                                sqlbuff.append(nbaseArray[i]);
                                sqlbuff.append("a01 where a0100 in (");
                                sqlbuff.append(sql);
                                sqlbuff.append("))");
                                dao.update(sqlbuff.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            drLogger.write("分布同步：受保护人员条件方法出错："+e);
        }
    }

    /**
     * 将不符合校验规则的数据从临时表里面删除掉
     * @param tTableList 临时表的表名list
     */
        private void filterValidateRules(String datapath,Set<String> tTableList,DrLogger drLogger,Connection conn) {
        RowSet rowSet = null;
        RowSet rowSet1 = null;
        RowSet rowSet2 = null;
        RowSet rowSet3 = null;
        try {
            List<HashMap<String,Object>>  ruleList = new ArrayList<HashMap<String,Object>>();
            String sql = "select checkfield,condition,forcestate from t_sys_asyn_validaterules where belong = 0 and valid = 1 ";
            ContentDAO dao = new ContentDAO(conn);
            rowSet = dao.search(sql);
            while (rowSet.next()) {
                HashMap<String,Object> map = new HashMap<String,Object>();
                map.put("checkfield", rowSet.getString("checkfield"));
                map.put("condition", rowSet.getString("condition"));
                map.put("forcestate", rowSet.getInt("forcestate"));
                ruleList.add(map);
            }
            //校验规则一条一条的来
            for (HashMap<String, Object> hashMap : ruleList) {
                ArrayList<String> photoList = new ArrayList<String>();
                String itemids = (String) hashMap.get("checkfield");
                String condition = (String) hashMap.get("condition");
                int forcestate = (Integer) hashMap.get("forcestate");
                Set<String> setlist = new HashSet<String>();
                ArrayList<String> arraySetList  = new ArrayList<String>();
                StringBuffer itembuff = new StringBuffer();
                String[] itemArray = itemids.split(",");
                for (int i = 0; i < itemArray.length; i++) {
                    itembuff.append(",");
                    itembuff.append(itemArray[i]);
                    FieldItem fieldItem = DataDictionary.getFieldItem(itemArray[i]);
                    if (null!=fieldItem&&setlist.add(fieldItem.getFieldsetid())) {
                       arraySetList.add(fieldItem.getFieldsetid());
                    }
                }
                Collections.sort(arraySetList);
                if (setlist.size()>2) {
                    drLogger.write("分布同步："+ hashMap.toString()+":此校验规则不只有一个子集");
                }else if (setlist.size()==1) {
                    ArrayList<ArrayList<Object>>  list = new ArrayList<ArrayList<Object>>();
                    String tablename = "T#S_ASYN_"+arraySetList.get(0);
                    if(!tTableList.contains(tablename)){
                        return;
                    }
                    sql= "select guidkey,a0101 name "+itembuff
                            + " from "+tablename+" where guidkey not in ( select guidkey from "+tablename+" where "+condition+")";
                    if (!"A01".equalsIgnoreCase(arraySetList.get(0)) && tTableList.contains("T#S_ASYN_A01")) {
                        sql= "select emp_id guidkey,a0101 name "+itembuff
                                + " from "+tablename+" a left join T#S_ASYN_A01 b on a.emp_id = b.guidkey where a.guidkey not in ( select guidkey from "+tablename+" where "+condition+")";
                    }else if(!"A01".equalsIgnoreCase(arraySetList.get(0)) && !tTableList.contains("T#S_ASYN_A01")){
                        sql= "select emp_id guidkey,a0101 name "+itembuff
                                + " from "+tablename+" a left join S_ASYN_A01 b on a.emp_id = b.guidkey where a.guidkey not in ( select guidkey from "+tablename+" where "+condition+")";
                    }
                    rowSet1=dao.search(sql);
                    while (rowSet1.next()) {
                       String guidkey = rowSet1.getString("guidkey");
                       photoList.add(guidkey);
                       ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
                       for (int i = 0; i < itemArray.length; i++) {
                           HashMap<String,String> map = new HashMap<String,String>();
                           map.put("itemid", itemArray[i]);
                           map.put("value", rowSet1.getString(itemArray[i]));
                           map.put("reason","不符合校验规则");
                           list1.add(map);
                       }
                       ArrayList<Object> listObject = new ArrayList<Object>();
                       listObject.add("A");
                       listObject.add(arraySetList.get(0));
                       listObject.add(rowSet1.getString("name"));
                       listObject.add(guidkey);
                       listObject.add(list1);
                       list.add(listObject);
                       drLogger.write("分布同步："+ tablename.substring(2)+"表里的"+guidkey+"不符合校验规则");
                    }
                    saveRecordTable(list, conn,drLogger);
                    if (1==forcestate) {//强制删除
                       //先删除照片
                       deletePhoto(datapath,photoList, drLogger);
                        if (tablename.toUpperCase().endsWith("A01")) {//校验规则用到的表是主表
                            for (String temptableName : tTableList) {
                                if (!temptableName.toUpperCase().endsWith("A01")) {
                                    sql = "delete from "+temptableName+" where emp_id not in ( select guidkey from "+tablename+" where "+condition+")";
                                    dao.update(sql, new ArrayList());
                                }
                            }
                            if(tTableList.contains("T#S_ASYN_A01")){
                                sql = "delete from T#S_ASYN_A01 where guidkey not in ( select guidkey from "+tablename+" where "+condition+")";
                            }else{
                                sql = "delete from S_ASYN_A01 where guidkey not in ( select guidkey from "+tablename+" where "+condition+")";
                            }
                            dao.update(sql, new ArrayList());
                        }else {//校验规则用到的表是子表
                            sql = "delete from "+tablename+" where guidkey not in ( select guidkey from "+tablename+" where "+condition+")";
                            dao.update(sql, new ArrayList());
                        }
                    }
                }else if(setlist.size()==2&&(setlist.contains("a01")||setlist.contains("A01"))){
                    String tablename1 = "T#S_ASYN_"+arraySetList.get(0);
                    String tablename2 = "T#S_ASYN_"+arraySetList.get(1);
                    if(!tTableList.contains(tablename1)||!tTableList.contains(tablename2)){
                        return;
                    }
                    sql= "select  b.guidkey from "+tablename2+" a full join "+tablename1+" b on a.emp_id = b.guidkey where "+condition;
                    StringBuffer sqlbuff = new StringBuffer("");
                    sqlbuff.append("select a0101 name, guidkey");
                    for (int i = 0; i < itemArray.length; i++) {
                        FieldItem fieldItem = DataDictionary.getFieldItem(itemArray[i]);
                        if (fieldItem.getFieldsetid().equalsIgnoreCase(arraySetList.get(0))) {
                            sqlbuff.append(",");
                            sqlbuff.append(itemArray[i]);
                        }
                    }
                    sqlbuff.append(" from ");
                    sqlbuff.append(tablename1);
                    sqlbuff.append(" where guidkey not in (");
                    sqlbuff.append(sql);
                    sqlbuff.append(")");
                    rowSet2=dao.search(sqlbuff.toString());
                    ArrayList<ArrayList<Object>>  list = new ArrayList<ArrayList<Object>>();
                    while (rowSet2.next()) {
                        String guidkey = rowSet2.getString("guidkey");
                        photoList.add(guidkey);
                        ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
                        for (int i = 0; i < itemArray.length; i++) {
                            FieldItem fieldItem = DataDictionary.getFieldItem(itemArray[i]);
                            if (fieldItem.getFieldsetid().equalsIgnoreCase(arraySetList.get(0))) {
                                HashMap<String,String> map = new HashMap<String,String>();
                                map.put("itemid", itemArray[i]);
                                map.put("value", rowSet2.getString(itemArray[i]));
                                map.put("reason","不符合校验规则");
                                list1.add(map);
                            }
                        }
                        ArrayList<Object> listObject = new ArrayList<Object>();
                        listObject.add("A");
                        listObject.add(arraySetList.get(0));
                        listObject.add(rowSet2.getString("name"));
                        listObject.add(guidkey);
                        listObject.add(list1);
                        list.add(listObject);
                        drLogger.write("分布同步："+ tablename1.substring(2)+"表里的"+guidkey+"不符合校验规则");
                    }
                    sqlbuff = new StringBuffer("");
                    sqlbuff.append("select a0101 name,a.guidkey ");
                    for (int i = 0; i < itemArray.length; i++) {
                        FieldItem fieldItem = DataDictionary.getFieldItem(itemArray[i]);
                        if (fieldItem.getFieldsetid().equalsIgnoreCase(arraySetList.get(1))) {
                            sqlbuff.append(",");
                            sqlbuff.append(itemArray[i]);
                        }
                    }
                    sqlbuff.append(" from ");
                    sqlbuff.append(tablename2);
                    sqlbuff.append(" a left join T#S_ASYN_A01 b on a.emp_id = b.guidkey  where a.emp_id not in (");
                    sqlbuff.append(sql);
                    sqlbuff.append(")");
                    rowSet3=dao.search(sqlbuff.toString());
                    while (rowSet3.next()) {
                        String guidkey = rowSet3.getString("guidkey");
                        ArrayList<Object> listObject = new ArrayList<Object>();
                        ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
                        for (int i = 0; i < itemArray.length; i++) {
                            FieldItem fieldItem = DataDictionary.getFieldItem(itemArray[i]);
                            if (fieldItem.getFieldsetid().equalsIgnoreCase(arraySetList.get(1))) {
                                HashMap<String,String> map = new HashMap<String,String>();
                                map.put("itemid", itemArray[i]);
                                map.put("value", rowSet3.getString(itemArray[i]));
                                map.put("reason","不符合校验规则");
                                list1.add(map);
                            }
                        }
                        listObject.add("A");
                        listObject.add(arraySetList.get(1));
                        listObject.add(rowSet3.getString("name"));
                        listObject.add(guidkey);
                        listObject.add(list1);
                        list.add(listObject);
                        drLogger.write("分布同步："+ tablename2.substring(2)+"表里的"+guidkey+"不符合校验规则");
                    }
                    if (list.size()>0) {
                        saveRecordTable(list, conn,drLogger);
                    }
                    if (1==forcestate) {//强制删除
                        //先删除照片
                        deletePhoto(datapath,photoList, drLogger);
                        for (String temptableName : tTableList) {
                            if (temptableName.toUpperCase().endsWith("A01")) { //要删除数据的表是主表
                                sqlbuff = new StringBuffer("");
                                sqlbuff.append("delete from ");
                                sqlbuff.append(temptableName);
                                sqlbuff.append(" where guidkey not in (");
                                sqlbuff.append(sql);
                                sqlbuff.append(")");
                                dao.delete(sqlbuff.toString(), new ArrayList<String>());
                            }else {//要删除数据的表是子表
                                sqlbuff = new StringBuffer("");
                                sqlbuff.append("delete from ");
                                sqlbuff.append(temptableName);
                                sqlbuff.append(" where emp_id not in (");
                                sqlbuff.append(sql);
                                sqlbuff.append(")");
                                dao.delete(sqlbuff.toString(), new ArrayList<String>());
                            }
                       }
                     }
                }else {
                    drLogger.write("分布同步："+ hashMap.toString()+":不符合要求");
                }
            }
        } catch (Exception e) {
           drLogger.write("分布同步：过滤校验规则出错"+e);
        }finally {
            PubFunc.closeResource(rowSet);
            PubFunc.closeResource(rowSet1);
            PubFunc.closeResource(rowSet2);
            PubFunc.closeResource(rowSet3);
        }
    }
    /**
     * 将不符合校验规则的照片删除
     * @param namelist
     */
    private void deletePhoto(String datapath,ArrayList<String> namelist,DrLogger drLogger) {
        try {
            ArrayList<String> fileNameList=  (ArrayList<String>) FileUtil.getFile(datapath);
            for (String photoName : namelist) {
                  for (String fileName : fileNameList) {
                     if (fileName.indexOf(photoName)!=-1) {
                         FileUtil.delfile(datapath, fileName);
                         drLogger.write("分布同步："+ fileName+"不符合校验规则被删除");
                     }
                  }
            }
        } catch (Exception e) {
           e.printStackTrace();
           drLogger.write("分布同步：将不符合校验规则的照片删除"+e);
        }
    }
    /**
     * 将校验数据保存到校验记录表里面
     * @param listArray
     * @param conn
     * @param drLogger
     */
    private void saveRecordTable(ArrayList<ArrayList<Object>> listArray,Connection conn,DrLogger drLogger ) {
        RowSet rowSet =null;
        try {
            String idsql = "select max(id)+1 id from T_SYS_ASYN_RECORD";
            ContentDAO dao = new ContentDAO(conn);
            rowSet = dao.search(idsql);
            int idstart = 0;
            if (rowSet.next()) {
                idstart = rowSet.getInt("id");
            }
            ArrayList<Object> arrayList = new ArrayList<Object>();
            for (int i = 0;i<listArray.size();i++) {
                ArrayList<Object> object  = listArray.get(i);
                ArrayList<Object> objectlist  = new ArrayList<Object>();
                int id = idstart+i;
                String type = (String) object.get(0);
                String setid = (String) object.get(1);
                String name = (String) object.get(2)==null?"无":(String) object.get(2);
                String guidkey = (String) object.get(3);
                ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) object.get(4);
                StringBuffer extMemo = new StringBuffer("");
                extMemo.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
                extMemo.append("<root>");
                for (HashMap<String, String> hashMap : list) {
                    extMemo.append("<rec itemid=\""+hashMap.get("itemid")+"\" reason=\""+hashMap.get("reason")+"\" value=\""+hashMap.get("value")+"\"></rec>");
                }
                extMemo.append("</root>");
                objectlist.add(id);
                objectlist.add(this.guidkey);
                objectlist.add(name);
                objectlist.add(guidkey);
                objectlist.add(type);
                objectlist.add(setid);
                objectlist.add(extMemo.toString());
                arrayList.add(objectlist);
            }
            String sql = "insert into T_SYS_ASYN_RECORD (id,mainGUIDkey,name,guidkey,Infoclass,Setid,extMemo) values (?,?,?,?,?,?,?)";
            if (arrayList.size()>999) {
                int pointsDataLimit = 999;
                List<Object> newList = new ArrayList<Object>();
                for(int i=0;i<arrayList.size();i++){//分批次处理
                    newList.add(arrayList.get(i));
                    if(pointsDataLimit == newList.size()||i == arrayList.size()-1){
                        dao.batchInsert(sql, newList);
                        newList.clear();
                    }
                }
            }else {
                dao.batchInsert(sql, arrayList);
            }
        } catch (Exception e) {
           e.printStackTrace();
           drLogger.write("分布同步：校验数据写入校验规则表失败！"+e);
        }finally {
            PubFunc.closeDbObj(rowSet);
        }
    }
    /**
     * 根据Constant 获取字段的类型
     * @return
     */
    private HashMap<String, FieldItem> getFieldtype(Connection conn) {
        ConstantXml constantXml = new ConstantXml(conn, "BS_ASYN_PLAN_S");
        HashMap<String,FieldItem> map = new HashMap<String,FieldItem>();
        ArrayList<Element> fielditemList= (ArrayList<Element>) constantXml.getElementList("scheme/fieldItem/item");
        for (Element element : fielditemList) {
            String setid = element.getAttributeValue("setid");
            String itemid = element.getAttributeValue("itemid");
            String itemdesc = element.getAttributeValue("itemdesc");
            String codesetid  = element.getAttributeValue("codesetid");
            String itemtype = element.getAttributeValue("itemtype");
            String itemlength = element.getAttributeValue("itemlength");
            String itemdecimal = element.getAttributeValue("itemdecimal");
            FieldItem fieldItem = new FieldItem();
            fieldItem.setItemid(itemid);
            fieldItem.setFieldsetid(setid);
            fieldItem.setDecimalwidth(Integer.parseInt(itemdecimal));
            fieldItem.setItemdesc(itemdesc);
            fieldItem.setItemtype(itemtype);
            fieldItem.setItemlength(Integer.parseInt(itemlength));
            fieldItem.setCodesetid(codesetid);
            String key = setid+"_"+itemid;
            map.put(key.toUpperCase(), fieldItem);
        }
        Element element= constantXml.getElement("scheme/personStatus/personItemid");
        String value = element.getValue();
        if (StringUtils.isNotEmpty(value)) {
            this.personItemid = element.getValue();
            fielditemList= (ArrayList<Element>) constantXml.getElementList("scheme/personStatus/mapping");
            HashMap<String,String> map2  = new HashMap<String,String>();
            for (Element element2 : fielditemList) {
                String personMapping = element2.getAttributeValue("personMapping");
                String pre = element2.getAttributeValue("pre");
                String[] perArr = personMapping.split(",");
                StringBuffer perStr = new StringBuffer();
                for (int i = 0 ; i < perArr.length; i++){
                    String codePre = perArr[i];
                    perStr.append(",");
                    perStr.append("'");
                    perStr.append(codePre);
                    perStr.append("'");
                }
                map2.put(perStr.substring(1),pre );
            }
            if (map2.size()>0) {
                this.personTypeMap = map2;
            }
        }

        return map;
    }
    /**
     * 根据人员库标识获取名称
     */
    private void getDbNameMap(Connection conn) {
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(conn);
        HashMap<String,String> map = new HashMap<String,String>();
        try {
            rs = dao.search("select dbname,pre from dbname");
            while (rs.next()) {
                String dbname = rs.getString("dbname");
                String pre = rs.getString("pre");
                this.dbNameMap.put(pre, dbname);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
    }

    /**
     * 更新接收时间
     * @param id 接收记录表的主键id
     */
    private void updateAccepTime(int id,Connection conn) {
        try {
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer("update t_sys_asyn_acceptinfo set status = ?,accepttime = ? where id = ? ");
            List valueList = new ArrayList();
            valueList.add("开始接收");
            valueList.add(DateUtils.getTimestamp(new Date()));
            valueList.add(id);
            dao.update(sql.toString(),valueList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 更新接收状态
     * @param recordid  数据包的唯一id
     */
    private void updateStatus(int recordid,String statusDesc,Connection conn) {
        try {
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer("update t_sys_asyn_acceptinfo set status = ? where id = ? ");
            List valueList = new ArrayList();
            valueList.add(statusDesc);
            valueList.add(recordid);
            dao.update(sql.toString(),valueList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 接收记录表的主键id
     * @param logid 数据包的唯一id
     * @return  存在返回id,不存在返回null
     */
    private HashMap<String,String> getCurrentRecord (String logid,Connection conn) {
        HashMap<String,String> resultMap = new HashMap();
        RowSet rowSet = null;
        try {
            String sql ="select id,guidkey from t_sys_asyn_acceptinfo where pkgguid = '"+logid+"'";
            ContentDAO dao = new ContentDAO(conn);
            rowSet=dao.search(sql);
            if (rowSet.next()) {
                resultMap.put("id",rowSet.getString("id"));
                resultMap.put("guidkey",rowSet.getString("guidkey"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return resultMap;
    }
    /**
     * 向上报负责人发送邮件
     * @param address 目标邮箱地址
     * @param content 邮件内容
     */
    private void sendEamil(String address,String content,Connection conn) {
        ArrayList list = new ArrayList();
        try {
            if (StringUtils.isNotEmpty(address)) {
                ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
                EmailInfoBo emailBo = new EmailInfoBo(conn, userview);
                String filePath = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"asynrecive"+File.separator+"logs"+File.separator+PubFunc.FormatDate(date, "yyyy-MM");
                String fileName = "js"+unitcode+"_"+PubFunc.FormatDate(date, "yyyyMMddHHmmss")+".log";
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("filename", fileName);
                bean.set("filepath", filePath+File.separator+fileName);
                list.add(bean);
                emailBo.sendEmail(address, "分布同步", content, list, "");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * 获取上报负责人的邮箱地址
     * @param unitguid 单位的唯一编码
     * @return 不存在返回null;存在返回邮箱地址
     */
    private String getEmailAddress(String unitguid,Connection conn) {
        String emailAddress = null;
        RowSet rowSet = null;
        try {
            EmailInfoBo emailBo = new EmailInfoBo(conn, userview);
            String emailItemId = emailBo.getEmailItemId();//获取邮箱指标
            String sql = "select a0100,nbase,reportleader,email from operuser,t_sys_asyn_scheme where unitguid = '"+unitguid+"' and reportleader = username";
            ContentDAO dao = new ContentDAO(conn);
            rowSet = dao.search(sql);
            HashMap<String, String> hm = null;
            if (rowSet.next()) {
                String email = rowSet.getString("email");
                String a0100 = rowSet.getString("a0100");
                String nbase = rowSet.getString("nbase");
                if(StringUtils.isNotEmpty(email)){
                    return email;
                }else if (StringUtils.isNotEmpty(a0100)&&StringUtils.isNotEmpty(nbase)) {
                    hm = new HashMap<String, String>();
                    hm.put("email", emailItemId);;
                    hm.put("nbase", nbase);
                    hm.put("a0100", a0100);
                }
            }
            if (null!=hm) {
                emailAddress = emailBo.getEmailAddress(hm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return emailAddress;
    }

    /**
     * 同步中间库数据到档案库
     * @param conn
     * @param setList
     */
    private synchronized boolean asynDataToToFileTable(Connection conn, ArrayList<Element> setList) throws GeneralException {
        String whereSql = "";
        boolean flag = true;
        try{
            ContentDAO dao = new ContentDAO(conn);
            updateStatus(this.recordid,"正在接收50%",conn);
            ConstantXml constantXml = new ConstantXml(conn, "BS_ASYN_PLAN_S");
            Element dbnameElement = constantXml.getElement("scheme/param");
            String reportPhoto = dbnameElement.getAttributeValue("reportPhoto");
            if("2".equalsIgnoreCase(this.asyntype)){
                //只需要判断中间库modtime>档案库的modtime
                whereSql = getWhereAcceptTime(conn);
                drLogger.write("获取增量时间modtime："+whereSql);
            }
            asynOrgToFileTable(conn,whereSql);
            moveDataToNewBase(conn,setList,reportPhoto);
            ArrayList<String> nbaseList = getMappingNbase(constantXml);
            for (Element element : setList) {
                String setid = element.getAttributeValue("setid");
                String firstTable = setid.substring(0, 1).toUpperCase();// A、B、K
                if("A01".equalsIgnoreCase(setid)){
                    String insertSituation = insertA01DataToFileTable(conn,nbaseList);
                    String updateSituation = updateA01DataToFileTable(conn,nbaseList);
                    String deleteSituation = deleteA01DataToFileTable(conn,nbaseList);
                    String situation = insertSituation + updateSituation + deleteSituation;
                    if (StringUtils.isNotEmpty(situation)) {
                        updateAcceptSituation(conn,situation + "\r\n");
                    }
                }else if(!"A01".equalsIgnoreCase(setid)&& "A".equalsIgnoreCase(firstTable)){
                    String insertSituation = insertPersonToFileTable(conn,setid,nbaseList);
                    String updateSituation = updatePersonToFileTable(conn,setid,nbaseList);
                    String deleteSituation = deletePersonToFileTable(conn,setid,nbaseList);
                    String situation = insertSituation + updateSituation + deleteSituation;
                    if (StringUtils.isNotEmpty(situation)) {
                        updateAcceptSituation(conn,situation + "\r\n");
                    }
                }else if("B".equalsIgnoreCase(firstTable)|| "K".equalsIgnoreCase(firstTable)){
                    String updateSituation = updateBKToFileTable(conn,setid);
                    String deleteSituation = deleteBKToFileTable(conn,setid);
                    String insertSituation = insertBKToFileTable(conn,setid);
                    String situation = insertSituation + updateSituation + deleteSituation;
                    if (StringUtils.isNotEmpty(situation)) {
                        updateAcceptSituation(conn,situation + "\r\n");
                    }
                }
            }
            if ("TRUE".equalsIgnoreCase(reportPhoto)) {
                String srcTab = "s_asyn_a01";
                String destTab = "S_ASYN_PHOTO";
                String strJoin = srcTab+".guidkey = "+destTab+".emp_id";
                String strSWhere = destTab+".modstate = 1 ";
                String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
                String strSet = destTab+".a0100="+srcTab+".a0100";
                String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
                drLogger.write("分布同步：插入S_ASYN_PHOTO中间表A0100的sql->"+updateSql);
                dao.update(updateSql);
                drLogger.write("分布同步：插入S_ASYN_PHOTO中间表A0100成功！");
                asynPhoto(conn,nbaseList);
            }
        }catch (Exception e){
            e.printStackTrace();
            flag = false;
            drLogger.write("分布同步：同步中间库数据到档案库"+e);
            throw GeneralExceptionHandler.Handle(e);
        }
        return flag;
    }

    /**
     * 移库
     * @param conn
     * @param setList
     * @param reportPhoto
     */
    private void moveDataToNewBase(Connection conn, ArrayList<Element> setList, String reportPhoto) {
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(conn);
            DbNameBo dbbo = new DbNameBo(conn,this.userview);
            rs = dao.search("select * from s_asyn_a01 where nbase <> srcnbase ");
            while(rs.next()){
                String nbase = rs.getString("nbase");
                String srcbase = rs.getString("srcnbase");
                String srca0100 = rs.getString("a0100");
                String newa0100 = dbbo.moveDataBetweenBase2(srca0100, srcbase, nbase, "1");
                drLogger.write("分布同步："+srca0100+"人员从"+srcbase+"人员库移库到"+nbase+"人员库成功！");
                updateAsynTableMoveBaseMsg(srca0100,newa0100,nbase,conn,setList,reportPhoto);
            }
        }catch(Exception e){
            drLogger.write("分布同步：人员移库失败！"+e.toString());
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
    }
    /**
     * 更新中间库表移库信息
     * @param srca0100
     * @param newa0100
     * @param nbase
     * @param setList
     * @param reportPhoto
     */
    private void updateAsynTableMoveBaseMsg(String srca0100, String newa0100, String nbase, Connection conn, ArrayList<Element> setList, String reportPhoto) {
        ArrayList list = new ArrayList();
        try{
            ContentDAO dao = new ContentDAO(conn);
            list.add(nbase);
            list.add(newa0100);
            list.add(srca0100);
            for (Element element : setList) {
                String tableName = element.getAttributeValue("setid");
                String firstTable = tableName.substring(0, 1).toUpperCase();// A、B、K
                if ("A".equalsIgnoreCase(firstTable)) {
                    String sql = "update s_asyn_"+tableName+" set nbase = ? ,a0100 = ? where a0100 = ?";
                    dao.update(sql, list);
                    drLogger.write("分布同步：更新"+tableName+"中间表移库信息成功！"+list);
                }
            }
            if("TRUE".equalsIgnoreCase(reportPhoto)){
                String sql = "update s_asyn_photo set nbase = ? ,a0100 = ? where a0100 = ?";
                dao.update(sql, list);
                drLogger.write("分布同步：更新S_ASYN_PHOTO中间库移库信息成功！"+list);
            }
        }catch(Exception e){
            drLogger.write("分布同步：更新中间库移库信息失败！"+e.toString());
            e.printStackTrace();
        }
    }
    /**
     * 获取人员状态对应
     * @param constantXml
     * @return
     */
    private ArrayList<String> getMappingNbase(ConstantXml constantXml) {
        ArrayList<Element> fielditemList = (ArrayList<Element>) constantXml.getElementList("scheme/personStatus/mapping");
        ArrayList<String> nbaseList = new ArrayList<String>();
        for (Element element : fielditemList) {
            String pre = element.getAttributeValue("pre");
            nbaseList.add(pre);
        }
        if(nbaseList.size() == 0){
            nbaseList.add("Usr");
        }
        return nbaseList;
    }
    /**
     * 删除数据
     * @param setList
     * @param drLogger
     */
    private void deleteFromData(ArrayList<Element> setList, Connection conn, DrLogger drLogger) throws GeneralException {
        try{
            ContentDAO dao = new ContentDAO(conn);
            dao.update("delete from organization where codeitemid like '"
                    + unitcode + "%' and codeitemid <> '" + this.unitcode + "'");
            dao.update("delete from s_asyn_org where codeitemid like '"
                    + this.unitcode + "%'");
            ConstantXml constantXml = new ConstantXml(conn, "BS_ASYN_PLAN_S");
            Element protectPeopleElement = constantXml.getElement("scheme/protectPeople");
            if (protectPeopleElement != null && "true".equalsIgnoreCase(protectPeopleElement.getAttributeValue("checkbox"))) {
                Element dbnameElement = constantXml.getElement("scheme/protectPeople/dbname");
                String pre = dbnameElement.getAttributeValue("pre");
                String[] nbaseArray = pre.split(",");
                Element peopleConditionElement = constantXml.getElement("scheme/protectPeople/peopleCondition");
                String peopleCondition = peopleConditionElement.getAttributeValue("condition");
                if (StringUtils.isNotEmpty(pre) && StringUtils.isNotEmpty(peopleCondition)) {
                    ArrayList allUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
                    for (int i = 0; i < nbaseArray.length ; i++) {
                        String nbase = nbaseArray[i];
                        YksjParser yp = new YksjParser(this.userview, allUsedFields, YksjParser.forSearch, YksjParser.LOGIC, YksjParser.forPerson, "gw", nbase);
                        yp.setCon(conn);
                        // 解析sql
                        yp.run_Where(peopleCondition, null, "", "", new ContentDAO(conn), "", conn, "A", null);
                        ArrayList<String> list = yp.getUsedSets();
                        String tempTable = yp.getTempTableName();
                        String resultSql = yp.getSQL();
                        String sql = " select a0100 from " +tempTable+" where " + resultSql;
                        for (Element element : setList) {
                            String setId = element.getAttributeValue("setid");
                            String firstTable = setId.substring(0, 1).toUpperCase();// A、B、K
                            if(!"A01".equalsIgnoreCase(setId)&& "A".equalsIgnoreCase(firstTable)){
                                dao.update("delete from s_asyn_"+setId+" where emp_id in (select guidkey from s_asyn_a01 where b0110 like '"+this.unitcode+"%') and a0100 not in ("+sql+") and nbase = '"+nbase+"'");
                            }else if("B".equalsIgnoreCase(firstTable)){
                                dao.update("delete from s_asyn_" + setId + " where b0110 " + " like '" + unitcode + "%'");
                            }else if("K".equalsIgnoreCase(firstTable)){
                                dao.update("delete from s_asyn_" + setId + " where e01a1 " + " like '" + unitcode + "%'");
                            }
                            dao.update("delete from s_asyn_photo where emp_id in (select guidkey from s_asyn_a01 where b0110 like '"+this.unitcode+"%') and a0100 not in ("+sql+") and nbase = '"+nbase+"'");
                        }
                        dao.update("delete from s_asyn_a01 where b0110 like '" + this.unitcode + "%' and a0100 not in ("+sql+") and nbase = '"+nbase+"'");
                    }
                    if(this.personTypeMap == null){
                        if(!pre.contains("Usr")){
                            deleteAsynTable(conn,"Usr",setList);
                        }
                    }else{
                        for (String key : this.personTypeMap.keySet()) {
                            String nbase = this.personTypeMap.get(key);
                            if(!pre.contains(nbase)){
                                deleteAsynTable(conn,nbase,setList);
                            }
                        }
                    }
                }
            }else{
                if(this.personTypeMap == null){
                    deleteAsynTable(conn,"Usr",setList);
                }else{
                    for (String key : this.personTypeMap.keySet()) {
                        String nbase = this.personTypeMap.get(key);
                        deleteAsynTable(conn,nbase,setList);
                    }
                }
            }
            ArrayList<FieldSet> fieldSetList = DataDictionary.getFieldSetList(1,0);
            for (FieldSet fieldSet : fieldSetList) {
                String fieldSetId = fieldSet.getFieldsetid();
                String firstTable = fieldSetId.substring(0, 1).toUpperCase();// A、B、K
                if(!"A01".equalsIgnoreCase(fieldSetId)&& "A".equalsIgnoreCase(firstTable)){
                    if(this.personTypeMap == null){
                        dao.update("delete from Usr"+fieldSetId+" where a0100 in (select a0100 from usra01 where b0110 like '"+this.unitcode+"%' and a0100 not in (select a0100 from s_asyn_a01 where b0110 like '"+this.unitcode+"%'))");
                    }else{
                        for (String key : this.personTypeMap.keySet()) {
                            String nbase = this.personTypeMap.get(key);
                            dao.update("delete from "+nbase+fieldSetId+" where a0100 in (select a0100 from "+nbase+"a01 where b0110 like '"+this.unitcode+"%' and a0100 not in (select a0100 from s_asyn_a01 where b0110 like '"+this.unitcode+"%' and nbase = '"+nbase+"'))");
                        }
                    }
                }else if("B".equalsIgnoreCase(firstTable)){
                    dao.update("delete from " + fieldSetId + " where b0110 "
                            + " like '" + this.unitcode + "%' and b0110 " + " <> '"
                            + this.unitcode + "'");
                }else if("K".equalsIgnoreCase(firstTable)){
                    dao.update("delete from " + fieldSetId + " where e01a1 "
                            + " like '" + this.unitcode + "%' and e01a1 " + " <> '"
                            + this.unitcode + "'");
                }
            }
            if(this.personTypeMap == null){
                dao.update("delete from Usra00 where a0100 in (select a0100 from usra01 where b0110 like '"+this.unitcode+"%' and a0100 not in (select a0100 from s_asyn_a01 where b0110 like '"+this.unitcode+"%'))");
                dao.update("delete from usra01 where b0110 like '" + this.unitcode + "%' and a0100 not in (select a0100 from s_asyn_a01 where b0110 like '"+this.unitcode+"%')");
            }else{
                for (String key : this.personTypeMap.keySet()) {
                    String nbase = this.personTypeMap.get(key);
                    dao.update("delete from "+nbase+"a00 where a0100 in (select a0100 from usra01 where b0110 like '"+this.unitcode+"%' and a0100 not in (select a0100 from s_asyn_a01 where b0110 like '"+this.unitcode+"%' and nbase = '"+nbase+"'))");
                    dao.update("delete from "+nbase+"a01 where b0110 like '" + this.unitcode + "%' and a0100 not in (select a0100 from s_asyn_a01 where b0110 like '"+this.unitcode+"%' and nbase = '"+nbase+"')");
                }
            }
        }catch (Exception e){
            drLogger.write("分布同步：上级单位清空数据失败"+e);
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private void deleteAsynTable(Connection conn,String nbase, ArrayList<Element> setList) {
        try{
            ContentDAO dao = new ContentDAO(conn);
            for (Element element : setList) {
                String setId = element.getAttributeValue("setid");
                String firstTable = setId.substring(0, 1).toUpperCase();// A、B、K
                if(!"A01".equalsIgnoreCase(setId)&& "A".equalsIgnoreCase(firstTable)){
                    dao.update("delete from s_asyn_"+setId+" where emp_id in (select guidkey from s_asyn_a01 where b0110 like '"+this.unitcode+"%') and nbase = '"+nbase+"'");
                }else if("B".equalsIgnoreCase(firstTable)){
                    dao.update("delete from s_asyn_" + setId + " where b0110 " + " like '" + unitcode + "%'");
                }else if("K".equalsIgnoreCase(firstTable)){
                    dao.update("delete from s_asyn_" + setId + " where e01a1 " + " like '" + unitcode + "%'");
                }
                dao.update("delete from s_asyn_photo where emp_id in (select guidkey from s_asyn_a01 where b0110 like '"+this.unitcode+"%') and nbase = '"+nbase+"'");
            }
            dao.update("delete from s_asyn_a01 where b0110 like '" + this.unitcode + "%' and nbase = '"+nbase+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 同步照片
     * @param conn
     * @param nbaseList
     */
    private void asynPhoto(Connection conn, ArrayList<String> nbaseList) throws GeneralException {
        RowSet rs = null;
        RowSet rs1 = null;
        try{
            ContentDAO dao = new ContentDAO(conn);
            for (String nbase : nbaseList) {
                int count = 0;
                StringBuffer sql = new StringBuffer();
                sql.append("insert into "+nbase+"a00 (ole,ext,ModTime,createtime,createusername,i9999,a0100,flag) ");
                sql.append(" select photo as ole,ext,modTime,modTime as createtime,'su' as createusername,i9999,a0100,'P' AS flag from S_ASYN_PHOTO b where modstate = 1 and nbase ='");
                sql.append(nbase+"' and not exists (select 1 from "+nbase+"A00 a where a.a0100 = b.a0100) ");
                sql.append(" and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%' and nbase = '"+nbase+"' ) and ( a0100 is not null ");
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sql.append(" or a0100 <> '' ");
                }
                sql.append(" ) ");
                dao.update(sql.toString());//新增照片
                drLogger.write("分布同步：插入"+nbase+"A00表照片数据成功！");
                StringBuffer sqlcount = new StringBuffer("select count(1) as count from s_asyn_photo where modstate = 1 ");
                sqlcount.append(" and nbase = '"+nbase+"' ");
                sqlcount.append(" and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%') ");
                sqlcount.append(" and ( a0100 is not null ");
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sqlcount.append(" or a0100 <> '' ");
                }
                sqlcount.append(" )");
                rs = dao.search(sqlcount.toString());
                if (rs.next()) {
                    count = rs.getInt("count");
                }
                if(count>0){
                    String sqlWhere = " modstate = 1 and nbase = '"+nbase+"' and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%') and ( a0100 is not null ";
                    if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                        sqlWhere += " or a0100 <> '' ";
                    }
                    sqlWhere += " )";
                    updateAsynTableModstate("s_asyn_photo",conn,sqlWhere);
                    String situation = "新增"+this.dbNameMap.get(nbase)+"照片共" + count + "条数据;"
                            + "\r\n";
                    updateAcceptSituation(conn,situation);
                    drLogger.write("分布同步：插入"+nbase+"A00表照片数据"+count+"条");
                }
                int count1 = 0;
                String srcTab = "S_ASYN_PHOTO";
                String destTab = nbase+"a00";
                String strJoin = srcTab+".a0100 = "+destTab+".a0100";
                String strSWhere = srcTab+".modstate = 2 ";
                String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
                String strSet = "";
                String oleSet = "";
                if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG){
                    strSet = destTab+".ext="+srcTab+".ext`"+destTab+".ModTime="+srcTab+".ModTime";
                    oleSet = destTab+".ole="+srcTab+".photo";
                }else{
                    strSet = destTab+".ole="+srcTab+".photo`"+destTab+".ext="+srcTab+".ext`"+destTab+".ModTime="+srcTab+".ModTime";
                }
                String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
                drLogger.write("分布同步：更新"+nbase+"A00表照片数据sql->"+updateSql);
                dao.update(updateSql);//更新照片
                //达梦数据库时  单独处理blob字段
                if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG){
                    updateSql =Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, oleSet, strDWhere,strSWhere);
                    dao.update(updateSql);
                }
                drLogger.write("分布同步：更新"+nbase+"A00表照片数据成功！");
                rs1 = dao.search("select count(1) as count from s_asyn_photo where modstate = 2 and nbase = '"+nbase+"' and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%') ");
                if (rs1.next()) {
                    count1 = rs1.getInt("count");
                }
                if(count1>0){
                    String sqlWhere = " modstate = 2 and nbase = '"+nbase+"' and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%')";
                    updateAsynTableModstate("s_asyn_photo",conn,sqlWhere);
                    String situation = "更新"+this.dbNameMap.get(nbase)+"照片共" + count1 + "条数据;"
                            + "\r\n";
                    updateAcceptSituation(conn,situation);
                    drLogger.write("分布同步：更新"+nbase+"A00表照片数据"+count+"条");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(rs1);
        }
    }

    /**
     * 更新
     * @param conn
     * @param countValue
     */
    private void updateAcceptSituation(Connection conn, String countValue) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        List valueList = new ArrayList();
        try {
            ContentDAO dao = new ContentDAO(conn);
            switch (Sql_switcher.searchDbServer()) {
                case Constant.MSSQL:
                    sql.append("update t_sys_asyn_acceptinfo set SITUATION = ISNULL(cast (SITUATION as VARCHAR(1000)),'') + ? where id = ?");
                    break;
                case Constant.ORACEL:
                    sql.append("update t_sys_asyn_acceptinfo set SITUATION = SITUATION || ? where id = ?");
                    break;
            }
            valueList.add(countValue);
            valueList.add(this.recordid);
            dao.update(sql.toString(), valueList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("分布同步：更新日志表失败" + e);
            throw GeneralExceptionHandler.Handle(e);
        }
    }


    /**
     * 获取同步字段（非固定）
     * @param setid
     * @param conn
     * @return
     */
    private String getColumnsSql(String setid,Connection conn) throws Exception {
        StringBuffer columnsSql = new StringBuffer();
        ConstantXml constantXml = new ConstantXml(conn, "BS_ASYN_PLAN_S");
        ArrayList<Element> fielditemList= (ArrayList<Element>) constantXml.getElementList("scheme/fieldItem/item");
        for (Element element : fielditemList) {
            String setidColumn = element.getAttributeValue("setid");
            String itemid = element.getAttributeValue("itemid");
            if(setid.equalsIgnoreCase(setidColumn)){
                columnsSql.append(",");
                columnsSql.append(itemid);
            }
        }
        return columnsSql.toString();
    }
    /**
     * 获取更新字段
     * @param columnsSql
     * @param destTab
     * @param srcTab
     * @return
     */
    private String getUpdateColumn(String columnsSql, String destTab, String srcTab) {
        boolean isDameng = Sql_switcher.searchDbServerFlag()==Constant.DAMENG;
        String[] columnsSqlArr = columnsSql.split(",");
        StringBuffer sql = new StringBuffer();
        for (int i = 0; i < columnsSqlArr.length-1; i++) {
            String column = columnsSqlArr[i+1];
            FieldItem item=DataDictionary.getFieldItem(column);
            sql.append("`");
            sql.append(destTab);
            sql.append(".");
            sql.append(column);
            sql.append(" = ");
            if(isDameng && item!=null && "M".equalsIgnoreCase(item.getItemtype())){
                String char_field_name = Sql_switcher.sqlToChar(srcTab+"."+column);
                sql.append(char_field_name);
                sql.append(" ");
                sql.append(column);
            }else{
                sql.append(srcTab);
                sql.append(".");
                sql.append(column);
            }

        }
        return sql.toString();
    }
    /**
     * 同步中间库的组织机构到organization表
     */
    private void asynOrgToFileTable(Connection conn, String whereSql) throws GeneralException {
        RowSet rs = null;
        int count = 0;
        StringBuffer situation = new StringBuffer();
        String strSWhere = "";
        try{
            ContentDAO dao = new ContentDAO(conn);
            //更新S_ASYN_ORG中modstate=1（为了兼容数据已经写入中间库，但没插入organization这种情况）
            dao.update("update S_ASYN_ORG set modstate = 1 where not EXISTS (SELECT 1 from ORGANIZATION b where S_ASYN_ORG.guidkey = b.guidkey) and modstate = 2 ");
            //更新机构层级
            dao.update("update S_ASYN_ORG set GRADE=(GRADE-1)+(SELECT GRADE FROM ORGANIZATION WHERE CODEITEMID = '"+this.unitcode+"') WHERE CODEITEMID LIKE '"+this.unitcode+"%'");
            String srcTab = "S_ASYN_ORG";
            String destTab = "ORGANIZATION";
            String strJoin = srcTab+".guidkey = "+destTab+".guidkey";
            if(StringUtils.isEmpty(whereSql)){
                strSWhere = srcTab+".codeitemid like '"+this.unitcode+"%' and S_ASYN_ORG.modState = 2 ";
            }else{
                strSWhere = whereSql+" and "+srcTab+".codeitemid like '"+this.unitcode+"%' and S_ASYN_ORG.modState = 2";
            }
            String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
            String strSet = destTab+".CODESETID="+srcTab+".CODESETID`"+destTab+".CODEITEMID="+srcTab+".CODEITEMID`"+destTab+".CODEITEMDESC="+srcTab+".CODEITEMDESC`"
                    +destTab+".PARENTID="+srcTab+".PARENTID`"+destTab+".END_DATE="+srcTab+".END_DATE`"+destTab+".START_DATE="+srcTab+".START_DATE`"+destTab+".A0000="+srcTab+".A0000`"+destTab+".GRADE="+srcTab+".GRADE";
            String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
            drLogger.write("分布同步：从S_ASYN_ORG表更新变动数据到ORGANIZATION表sql->"+updateSql);
            dao.update(updateSql);
            drLogger.write("分布同步：从S_ASYN_ORG表更新变动数据到ORGANIZATION表成功！");
            rs = dao.search("select count(1) as count1 from S_ASYN_ORG where codeitemid like '"+this.unitcode+"%' and modstate = 2 ");
            if (rs.next()) {
                count = rs.getInt("count1");
            }
            if(count>0){
                situation.append("更新组织机构表" +count + "条数据;");
                String sqlWhere = " codeitemid like '"+this.unitcode+"%' and modstate = 2 ";
                updateAsynTableModstate("S_ASYN_ORG",conn,sqlWhere);
                drLogger.write("分布同步：组织机构表更新变动数据"+count+"条");
            }
            count = 0;
            rs = dao.search("SELECT count(1) as count from S_ASYN_ORG where modstate = 3 and codeitemid like '"+this.unitcode+"%'");
            if(rs.next()){
                count = rs.getInt("count");
            }
            if(count> 0 ){
                //删除组织机构数据
                dao.update("delete from organization where guidkey in (select guidkey from s_asyn_org where modstate = 3 and codeitemid like '"+this.unitcode+"%')");
                situation.append("删除组织机构层级表共" + count + "条数据;");
                String sqlWhere = " codeitemid like '"+this.unitcode+"%' and modstate = 3 ";
                updateAsynTableModstate("S_ASYN_ORG",conn,sqlWhere);
                drLogger.write("分布同步：组织机构表删除数据"+count+"条");
            }
            count = 0;
            rs = dao.search("SELECT count(1) as count from S_ASYN_ORG where modstate = 1 and codeitemid like '"+this.unitcode+"%'");
            if(rs.next()){
                count = rs.getInt("count");
            }
            if(count> 0 ){
                //按照机构层级插入组织机构数据
                insertOrganization(conn);
                situation.append("新增组织机构层级表共" + count + "条数据;");
                String sqlWhere = " codeitemid like '"+this.unitcode+"%' and modstate = 1 ";
                updateAsynTableModstate("S_ASYN_ORG",conn,sqlWhere);
                drLogger.write("分布同步：组织机构表新增数据"+count+"条");
            }
            if (StringUtils.isNotEmpty(situation.toString())) {
                updateAcceptSituation(conn,situation.toString() + "\r\n");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 根据机构层级分批次插入组织机构
     * @param conn
     */
    private void insertOrganization(Connection conn) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(conn);
            sql.append("SELECT DISTINCT(grade) as grade,codesetid from S_ASYN_ORG where modstate = 1 and codeitemid like '");
            sql.append(this.unitcode);
            sql.append("%'");
            sql.append(" ORDER BY grade asc,codesetid desc");
            rs = dao.search(sql.toString());
            while (rs.next()){
                int grade = rs.getInt("grade");
                String codeSetId = rs.getString("codesetid");
                StringBuffer insertSql = new StringBuffer();
                insertSql.append("insert into ORGANIZATION (GUIDKEY,CODESETID,CODEITEMID,CODEITEMDESC,PARENTID,END_DATE,START_DATE,A0000,GRADE) ");
                insertSql.append(" select GUIDKEY,CODESETID,CODEITEMID,CODEITEMDESC,PARENTID,END_DATE,START_DATE,A0000,GRADE from S_ASYN_ORG  ");
                insertSql.append(" where modstate = 1 and codeitemid like '"+this.unitcode+"%' ");
                insertSql.append(" and grade = ");
                insertSql.append(grade);
                insertSql.append(" and codesetid = '");
                insertSql.append(codeSetId);
                insertSql.append("'");
                drLogger.write("分布同步：第"+grade+"层级插入组织机构数据到机构表sql=" + insertSql.toString());
                dao.update(insertSql.toString());
                drLogger.write("分布同步：第"+grade+"层级插入组织机构数据到机构表成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }
    /**
     * 获取接收时间判断条件
     * @param conn
     * @return
     */
    private String getWhereAcceptTime(Connection conn) {
        StringBuffer sql = new StringBuffer();
        List list = new ArrayList();
        RowSet rs = null;
        String whereSql = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            sql.append("select dataendtime from t_sys_asyn_acceptinfo where UnitCode = ? order by id desc ");
            list.add(this.unitcode);
            rs = dao.search(sql.toString(), list);
            if (!rs.next()) {
                return null;
            }
            Object date = rs.getObject("dataendtime");
            whereSql = Sql_switcher.dateToChar("modtime", "yyyy-MM-dd HH:mm:ss")+"<='"+date+"'";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("分布同步：获取接收时间出错" + e.getMessage());
        } finally {
            PubFunc.closeResource(rs);
        }
        return whereSql;
    }
    /**
     * 同步人员主键到档案库
     */
    private String insertA01DataToFileTable(Connection conn, ArrayList<String> nbaseList) throws GeneralException {
        RowSet rs = null;
        StringBuffer acceptSituation = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(conn);
            for (String nbase : nbaseList) {
                ArrayList valueList = new ArrayList();
                StringBuffer sql = new StringBuffer("select guidkey from S_ASYN_A01 where modState in (1,2) and b0110 like '");
                sql.append(this.unitcode);
                sql.append("%' and nbase ='");
                sql.append(nbase);
                sql.append("' and (a0100 is null ");
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sql.append(" or a0100 = '' ");
                }
                sql.append(" )");
                rs = dao.search(sql.toString());
                int count = 0;
                while (rs.next()) {
                    ArrayList list = new ArrayList();
                    String a0100 = DbNameBo.insertMainSetA0100(nbase + "A01",conn);
                    String guidkey = rs.getString("guidkey");
                    list.add(a0100);
                    list.add(guidkey);
                    valueList.add(list);
                    int rowcount = rs.getRow();
                    if (rowcount % 1000 == 0) {
                        count = count + valueList.size();
                        dao.batchUpdate("update S_ASYN_A01 set a0100=? where guidkey=?",valueList);
                        drLogger.write("分布同步：根据GUIDKEY批量插入S_ASYN_A01中间库A0100成功！"+valueList);
                        valueList.clear();
                    }
                }
                if (valueList.size() > 0) {
                    count = count + valueList.size();
                    dao.batchUpdate("update S_ASYN_A01 set a0100=? where guidkey=?",valueList);
                    drLogger.write("分布同步：根据GUIDKEY批量插入S_ASYN_A01中间库A0100成功！"+valueList);
                }
                String columnsSql = getColumnsSql("A01",conn);
                String srcTab = "s_asyn_a01";
                String destTab = nbase+"a01";
                String strJoin = srcTab+".a0100 = "+destTab+".a0100";
                String strSWhere = srcTab+".b0110 like '"+this.unitcode+"%' and "+srcTab+".modstate = 1 and "+srcTab+".nbase = '"+nbase+"'";
                String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
                String updateColumn = getUpdateColumn(columnsSql,destTab,srcTab);
                String strSet = destTab+".guidkey="+srcTab+".guidkey`"+destTab+".createtime="+srcTab+".createtime`"+destTab+".createusername= 'su'`"+destTab+".modtime="+srcTab+".modtime"+updateColumn;
                String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
                drLogger.write("分布同步：插入"+nbase+"A01人员主集基本信息sql->"+updateSql);
                dao.update(updateSql);
                drLogger.write("分布同步：插入"+nbase+"A01人员主集基本信息成功！");
                if (count > 0) {
                    acceptSituation.append("新增" + this.dbNameMap.get(nbase)
                            + "基本信息表共" + count + "条数据;");
                    drLogger.write("分布同步："+nbase+"A01人员基本信息表新增数据"+count+"条");
                }
            }
            String sqlWhere = " s_asyn_a01.b0110 like '"+this.unitcode+"%' and s_asyn_a01.modstate = 1 and ( s_asyn_a01.a0100 is not null ";
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                sqlWhere += " or s_asyn_a01.a0100 <> '' ";
            }
            sqlWhere += " )";
            updateAsynTableModstate("s_asyn_a01",conn,sqlWhere);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return acceptSituation.toString();
    }

    /**
     * 更新中间库表状态为已同步
     * @param tableName
     * @param conn
     */
    private void updateAsynTableModstate(String tableName, Connection conn,String sqlWhere) {
        try{
            ContentDAO dao = new ContentDAO(conn);
            dao.update("update "+tableName+" set modstate = 0 where "+sqlWhere);
            drLogger.write("分布同步：更新"+tableName+"中间表状态为已同步成功！");
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：更新"+tableName+"中间表状态为已同步失败！");
        }
    }

    /**
     * 更新A01数据到档案库
     * @param conn
     * @param nbaseList
     * @return
     */
    private String updateA01DataToFileTable(Connection conn, ArrayList<String> nbaseList) throws GeneralException {
        StringBuffer acceptSituation = new StringBuffer();
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(conn);
            String columnsSql = getColumnsSql("A01",conn);
            for (String nbase : nbaseList) {
                int count = 0;
                String srcTab = "s_asyn_a01";
                String destTab = nbase+"a01";
                String strJoin = srcTab+".guidkey = "+destTab+".guidkey";
                String strSWhere = srcTab+".b0110 like '"+this.unitcode+"%' and "+srcTab+".modstate = 2 and "+srcTab+".nbase = '"+nbase+"'";
                String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
                String updateColumn = getUpdateColumn(columnsSql,destTab,srcTab);
                String strSet = destTab+".createtime="+srcTab+".createtime`"+destTab+".modtime="+srcTab+".modtime"+updateColumn;
                String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
                drLogger.write("分布同步：更新"+nbase+"A01人员主集基本信息sql->"+updateSql);
                dao.update(updateSql);
                drLogger.write("分布同步：更新"+nbase+"A01人员主集基本信息成功！");
                rs = dao.search("select count(1) as count from s_asyn_a01 where nbase = '"+nbase+"' and b0110 like '"+this.unitcode+"%' and modstate = 2 ");
                if (rs.next()) {
                    count = rs.getInt("count");
                }
                if(count>0){
                    acceptSituation.append("更新" + this.dbNameMap.get(nbase)
                            + "基本信息表共" + count + "条数据;");
                    drLogger.write("分布同步："+nbase+"A01人员基本信息表更新数据"+count+"条");
                }
            }
            String sqlWhere = " b0110 like '"+this.unitcode+"%' and modstate = 2 ";
            updateAsynTableModstate("s_asyn_a01",conn,sqlWhere);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return acceptSituation.toString();
    }

    /**
     * 删除A01数据到档案库
     * @param conn
     * @param nbaseList
     * @return
     */
    private String deleteA01DataToFileTable(Connection conn, ArrayList<String> nbaseList) throws GeneralException {
        StringBuffer acceptSituation = new StringBuffer();
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(conn);
            for (String nbase : nbaseList) {
                int count = 0;
                String sql = "delete from "+nbase+"A01 where EXISTS (select 1 from S_ASYN_A01 b where b.b0110 like '"+this.unitcode+"%' and b.modstate = 3 and b.nbase = '"+nbase+"' and "+nbase+"A01.guidkey = b.guidkey ) ";
                drLogger.write("分布同步：删除"+nbase+"A01人员主集基本信息sql->"+sql);
                dao.update(sql);
                drLogger.write("分布同步：删除"+nbase+"A01人员主集基本信息成功！");
                String deleteSql = "delete from "+nbase+"A00 where EXISTS (SELECT 1 from S_ASYN_A01 b where b.b0110 like '"+this.unitcode+"%'and b.modstate = 3 and b.nbase = '"+nbase+"' and "+nbase+"A00.a0100 = b.a0100 ) ";
                drLogger.write("分布同步：删除"+nbase+"A00人员主集基本信息sql->"+deleteSql);
                dao.update(deleteSql);//删除表中照片数据
                drLogger.write("分布同步：删除"+nbase+"A00人员主集基本信息成功！");
                rs = dao.search("select count(1) as count from s_asyn_a01 where nbase = '"+nbase+"' and b0110 like '"+this.unitcode+"%' and modstate = 3 ");
                if (rs.next()) {
                    count = rs.getInt("count");
                }
                if(count>0){
                    acceptSituation.append("删除" + this.dbNameMap.get(nbase)
                            + "基本信息表共" + count + "条数据;");
                    drLogger.write("分布同步："+nbase+"A01人员基本信息表删除数据"+count+"条");
                }
            }
            String sqlWhere = " b0110 like '"+this.unitcode+"%' and modstate = 3 ";
            updateAsynTableModstate("s_asyn_a01",conn,sqlWhere);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return acceptSituation.toString();
    }
    /**
     * 插入人员子集到档案库
     * @param conn
     * @param setid
     * @param nbaseList
     * @return
     */
    private String insertPersonToFileTable(Connection conn, String setid, ArrayList<String> nbaseList) throws GeneralException {
        RowSet rs = null;
        StringBuffer acceptSituation = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(conn);
            String srcTab = "s_asyn_a01";
            String destTab = "S_ASYN_"+setid;
            String strJoin = srcTab+".guidkey = "+destTab+".emp_id";
            String strSWhere = destTab+".modstate in (1,2) and ( "+destTab+".a0100 is null ";
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                strSWhere += " or "+destTab+".a0100 = '' ";
            }
            strSWhere += " )";
            String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
            String strSet = destTab+".a0100="+srcTab+".a0100";
            String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
            drLogger.write("分布同步：插入"+destTab+"中间表A0100的sql->"+updateSql);
            dao.update(updateSql);
            drLogger.write("分布同步：插入"+destTab+"中间表A0100成功！");
            String columnsSql = getColumnsSql(setid,conn);
            for (String nbase : nbaseList) {
                int count = 0;
                StringBuffer sqls = new StringBuffer();
                String table = nbase+setid;
                sqls.append("INSERT INTO "+table+" (");
                sqls.append(" guidkey,modtime,createtime,createusername,A0100,i9999 "+columnsSql);
                sqls.append(") select s_asyn_"+setid+".guidkey,s_asyn_"+setid+".modtime,s_asyn_"+setid+".createtime,'su' as createusername,s_asyn_"+setid+".a0100,row_number() over (partition by s_asyn_"+setid+".a0100 order by s_asyn_"+setid+".i9999,s_asyn_"+setid+".modtime  ) + "+Sql_switcher.isnull("(select max(i9999) from "+nbase+setid+" where a0100 = s_asyn_"+setid+".a0100 )","0") +" as i9999 ");
                sqls.append(columnsSql+" from s_asyn_"+setid);
                sqls.append(" LEFT JOIN s_asyn_A01 on  s_asyn_"+setid+".emp_id = s_asyn_A01.guidkey ");
                sqls.append(" WHERE s_asyn_"+setid+".modstate = 1 and s_asyn_"+setid+".nbase = '"+nbase+"' ");
                sqls.append(" and s_asyn_A01.b0110 like '"+this.unitcode+"%' ");
                sqls.append(" and ( s_asyn_"+setid+".a0100 is not null ");
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sqls.append(" or s_asyn_"+setid+".a0100 <> '' ");
                }
                sqls.append(" ) ");
                drLogger.write("分布同步：插入"+table+"人员子集数据sql->"+sqls.toString());
                dao.update(sqls.toString());
                drLogger.write("分布同步：插入"+table+"人员子集数据成功！");
                StringBuffer sqlcount = new StringBuffer("select count(1) as count from s_asyn_"+setid+" where modstate = 1 ");
                sqlcount.append(" and nbase = '"+nbase+"' ");
                sqlcount.append(" and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%') ");
                sqlcount.append(" and ( a0100 is not null ");
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sqlcount.append(" or a0100 <> '' ");
                }
                sqlcount.append(" ) ");
                rs = dao.search(sqlcount.toString());
                if (rs.next()) {
                    count = rs.getInt("count");
                }
                if(count>0){
                    FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
                    String fieldSetDesc = fieldSet.getFieldsetdesc();
                    acceptSituation.append("新增" + this.dbNameMap.get(nbase)
                            + fieldSetDesc + "表共" + count + "条数据;");
                    drLogger.write("分布同步："+table+"人员子集信息表新增数据"+count+"条");
                }
            }
            String sqlWhere = " modstate = 1 and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%') and ( a0100 is not null ";
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                sqlWhere += " or a0100 <> '' ";
            }
            sqlWhere += " )";
            updateAsynTableModstate("s_asyn_"+setid,conn,sqlWhere);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return acceptSituation.toString();
    }

    /**
     * 更新人员子集到档案库
     * @param conn
     * @param setid
     * @param nbaseList
     * @return
     */
    private String updatePersonToFileTable(Connection conn, String setid, ArrayList<String> nbaseList) throws GeneralException {
        RowSet rs = null;
        StringBuffer acceptSituation = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(conn);
            String columnsSql = getColumnsSql(setid,conn);
            for (String nbase : nbaseList) {
                int count = 0;
                String srcTab = "s_asyn_"+setid;
                String destTab = nbase+setid;
                String strJoin = srcTab+".guidkey = "+destTab+".guidkey";
                String strSWhere = srcTab+".modtime > "+destTab+".modtime and "+srcTab+".modstate = 2 and "+srcTab+".nbase = '"+nbase+"'";
                String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
                String updateColumn = getUpdateColumn(columnsSql,destTab,srcTab);
                String strSet = destTab+".createtime="+srcTab+".createtime`"+destTab+".modtime="+srcTab+".modtime"+updateColumn;
                String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
                drLogger.write("分布同步：更新"+destTab+"人员子集信息sql->"+updateSql);
                dao.update(updateSql);
                drLogger.write("分布同步：更新"+destTab+"人员子集信息成功！");
                rs = dao.search("select count(1) as count from s_asyn_"+setid+" where modstate = 2 and nbase = '"+nbase+"' and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%') ");
                if (rs.next()) {
                    count = rs.getInt("count");
                }
                if(count>0){
                    FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
                    String fieldSetDesc = fieldSet.getFieldsetdesc();
                    acceptSituation.append("更新" + this.dbNameMap.get(nbase)
                            + fieldSetDesc + "表共" + count + "条数据;");
                    drLogger.write("分布同步："+destTab+"人员子集信息表更新数据"+count+"条");
                }
            }
            String sqlWhere = " modstate = 2 and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%') ";
            updateAsynTableModstate("s_asyn_"+setid,conn,sqlWhere);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return acceptSituation.toString();
    }

    /**
     * 删除子集到档案库的数据
     * @param conn
     * @param setid
     * @param nbaseList
     * @return
     */
    private String deletePersonToFileTable(Connection conn, String setid, ArrayList<String> nbaseList) throws GeneralException {
        RowSet rs = null;
        StringBuffer acceptSituation = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(conn);
            for (String nbase : nbaseList) {
                int count = 0;
                String updateSql = "delete from "+nbase+setid+" where guidkey in (select guidkey from s_asyn_"+setid+" where nbase = '"+nbase+"' and modstate = 3 and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%')) ";
                drLogger.write("分布同步：删除"+nbase+setid+"人员子集信息sql->"+updateSql);
                dao.update(updateSql);
                drLogger.write("分布同步：删除"+nbase+setid+"人员子集信息成功！");
                rs = dao.search("select count(1) as count from s_asyn_"+setid+" where nbase = '"+nbase+"' and modstate = 3 and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%') ");
                if (rs.next()) {
                    count = rs.getInt("count");
                }
                if(count>0){
                    FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
                    String fieldSetDesc = fieldSet.getFieldsetdesc();
                    acceptSituation.append("删除" + this.dbNameMap.get(nbase)
                            + fieldSetDesc + "表共" + count + "条数据;");
                    drLogger.write("分布同步："+nbase+setid+"人员子集信息表删除数据"+count+"条");
                }
            }
            String sqlWhere = " modstate = 3 and emp_id in (SELECT guidkey from S_ASYN_A01 where b0110 like '"+this.unitcode+"%') ";
            updateAsynTableModstate("s_asyn_"+setid,conn,sqlWhere);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return acceptSituation.toString();
    }

    /**
     * 插入组织机构到档案库
     * @param conn
     * @param setid
     * @return
     */
    private String insertBKToFileTable(Connection conn, String setid) throws GeneralException {
        StringBuffer acceptSituation = new StringBuffer();
        RowSet rs = null;
        String sqls = "";
        String sqlWhere = "";
        int count = 0;
        try{
            ContentDAO dao = new ContentDAO(conn);
            String firstTable = setid.substring(0, 1).toUpperCase();// A、B、K
            String columnsSql = getColumnsSql(setid,conn);
            StringBuffer sql = new StringBuffer();
            sql.append("insert into ");
            sql.append(setid);
            sql.append(" ( guidkey,modtime,createtime,createusername,column ");
            sql.append(columnsSql);
            sql.append(") select guidkey,modtime,createtime,'su' as createusername,column ");
            sql.append(columnsSql);
            sql.append(" from s_asyn_"+setid);
            sql.append(" where unit like '"+this.unitcode+"%' ");
            sql.append(" and modstate = 1 ");
            if("B".equalsIgnoreCase(firstTable)){
                if(setid.toUpperCase().endsWith("B01")){
                    dao.update(sql.toString().replaceAll("column","b0110").replaceAll("unit","b0110"));
                    drLogger.write("分布同步：插入B01组织机构基本信息成功！");
                }else{
                    dao.update(sql.toString().replaceAll("column","b0110,i9999").replaceAll("unit","b0110"));
                    drLogger.write("分布同步：插入B01组织机构子集信息成功！");
                }
                sqls = "select count(1) as count from s_asyn_"+setid+" where modstate = 1 and b0110 like '"+this.unitcode+"%'";
                sqlWhere = " modstate = 1 and b0110  like '"+this.unitcode+"%'";
            }else if("K".equalsIgnoreCase(firstTable)){
                if(setid.toUpperCase().endsWith("K01")){
                    dao.update(sql.toString().replaceAll("column","e01a1").replaceAll("unit","e01a1"));
                    drLogger.write("分布同步：插入K01岗位基本信息成功！");
                }else{
                    dao.update(sql.toString().replaceAll("column","e01a1,i9999").replaceAll("unit","e01a1"));
                    drLogger.write("分布同步：插入K01岗位子集信息成功！");
                }
                sqls = "select count(1) as count from s_asyn_"+setid+" where modstate = 1 and e01a1 like '"+this.unitcode+"%'";
                sqlWhere = " modstate = 1 and e01a1  like '"+this.unitcode+"%'";
            }
            rs = dao.search(sqls);
            if (rs.next()) {
                count = rs.getInt("count");
            }
            if(count>0){
                FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
                String fieldSetDesc = fieldSet.getFieldsetdesc();
                acceptSituation.append("新增"+ fieldSetDesc + "表共" + count + "条数据;");
                drLogger.write("分布同步：插入"+setid+"表数据"+count+"条");
            }
            updateAsynTableModstate("s_asyn_"+setid,conn,sqlWhere);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeResource(rs);
        }
        return acceptSituation.toString();
    }

    /**
     * 更新组织机构到档案库
     * @param conn
     * @param setid
     * @return
     */
    private String updateBKToFileTable(Connection conn, String setid) throws GeneralException {
        StringBuffer acceptSituation = new StringBuffer();
        RowSet rs = null;
        String sqls = "";
        String sqlWhere = "";
        int count = 0;
        try{
            ContentDAO dao = new ContentDAO(conn);
            String firstTable = setid.substring(0, 1).toUpperCase();// A、B、K
            String columnsSql = getColumnsSql(setid,conn);
            String srcTab = "s_asyn_"+setid;
            String destTab = setid;
            String strJoin = srcTab+".guidkey = "+destTab+".guidkey";
            String strSWhere = "";

            String updateColumn = getUpdateColumn(columnsSql,destTab,srcTab);
            String strSet ="";
            if("B".equalsIgnoreCase(firstTable)){
                strSWhere = srcTab+".b0110 like '"+this.unitcode+"%' and "+srcTab+".modstate = 2 ";
                if(setid.toUpperCase().endsWith("B01")){
                    strSet = destTab+".b0110="+srcTab+".b0110`"+destTab+".createtime="+srcTab+".createtime`"+destTab+".modtime="+srcTab+".modtime"+updateColumn;
                }else{
                    strSet = destTab+".i9999="+srcTab+".i9999`"+destTab+".b0110="+srcTab+".b0110`"+destTab+".createtime="+srcTab+".createtime`"+destTab+".modtime="+srcTab+".modtime"+updateColumn;
                }
                sqls = "select count(1) as count from s_asyn_"+setid+" where modstate = 2 and b0110 like '"+this.unitcode+"%'";
                sqlWhere = " modstate = 2 and b0110  like '"+this.unitcode+"%'";
            }else if("K".equalsIgnoreCase(firstTable)){
                strSWhere = srcTab+".e01a1 like '"+this.unitcode+"%' and "+srcTab+".modstate = 2 ";
                if(setid.toUpperCase().endsWith("K01")){
                    strSet = destTab+".e01a1="+srcTab+".e01a1`"+destTab+".createtime="+srcTab+".createtime`"+destTab+".modtime="+srcTab+".modtime"+updateColumn;
                }else{
                    strSet = destTab+".i9999="+srcTab+".i9999`"+destTab+".e01a1="+srcTab+".e01a1`"+destTab+".createtime="+srcTab+".createtime`"+destTab+".modtime="+srcTab+".modtime"+updateColumn;
                }
                sqls = "select count(1) as count from s_asyn_"+setid+" where modstate = 2 and e01a1 like '"+this.unitcode+"%'";
                sqlWhere = " modstate = 2 and e01a1  like '"+this.unitcode+"%'";
            }
            String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+sqlWhere+")";
            String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
            drLogger.write("分布同步：更新"+setid+"表数据sql->"+updateSql);
            dao.update(updateSql);
            drLogger.write("分布同步：更新"+setid+"表数据成功！");
            rs = dao.search(sqls);
            if (rs.next()) {
                count = rs.getInt("count");
            }
            if(count>0){
                FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
                String fieldSetDesc = fieldSet.getFieldsetdesc();
                acceptSituation.append("更新"+ fieldSetDesc + "表共" + count + "条数据;");
                drLogger.write("分布同步：更新"+setid+"表数据"+count+"条");
            }
            updateAsynTableModstate("s_asyn_"+setid,conn,sqlWhere);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeResource(rs);
        }
        return acceptSituation.toString();
    }

    /**
     * 删除组织机构到档案库
     * @param conn
     * @param setid
     * @return
     */
    private String deleteBKToFileTable(Connection conn, String setid) throws GeneralException {
        StringBuffer acceptSituation = new StringBuffer();
        RowSet rs = null;
        String sqls = "";
        String sqlWhere = "";
        String deleteSql = "";
        int count = 0;
        try{
            ContentDAO dao = new ContentDAO(conn);
            String firstTable = setid.substring(0, 1).toUpperCase();// A、B、K
            if("B".equalsIgnoreCase(firstTable)){
                deleteSql = "delete from "+setid+" where guidkey in (select guidkey from s_asyn_"+setid+" where modstate = 3 and b0110 like '"+this.unitcode+"%') ";
                sqls = "select count(1) as count from s_asyn_"+setid+" where modstate = 3 and b0110 like '"+this.unitcode+"%'";
                sqlWhere = " modstate = 3 and b0110  like '"+this.unitcode+"%'";
            }else if("K".equalsIgnoreCase(firstTable)){
                deleteSql = "delete from "+setid+" where guidkey in (select guidkey from s_asyn_"+setid+" where modstate = 3 and e01a1 like '"+this.unitcode+"%') ";
                sqls = "select count(1) as count from s_asyn_"+setid+" where modstate = 3 and e01a1 like '"+this.unitcode+"%'";
                sqlWhere = " modstate = 3 and e01a1  like '"+this.unitcode+"%'";
            }
            drLogger.write("分布同步：删除"+setid+"表数据sql->"+deleteSql);
            dao.update(deleteSql);
            drLogger.write("分布同步：删除"+setid+"表数据成功！");
            rs = dao.search(sqls);
            if (rs.next()) {
                count = rs.getInt("count");
            }
            if(count>0){
                FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
                String fieldSetDesc = fieldSet.getFieldsetdesc();
                acceptSituation.append("删除"+ fieldSetDesc + "表共" + count + "条数据;");
                drLogger.write("分布同步：删除"+setid+"表数据"+count+"条");
            }
            updateAsynTableModstate("s_asyn_"+setid,conn,sqlWhere);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeResource(rs);
        }
        return acceptSituation.toString();
    }

    /**
     * 移动压缩文件到finish
     * @param zipFile
     */
    private void moveZipToFinish(File zipFile) throws Exception{
        List<String> fileNameList = FileUtil.getFile(zipFile.getParent());
        String zipFileName = zipFile.getName();
        String zipName = zipFileName.substring(0,zipFileName.indexOf("."));
        for(int i=0;i<fileNameList.size();i++){
            String fileName = fileNameList.get(i);
            if(fileName.contains(zipName)){
                String zipPath = zipFile.getParent()+File.separator+fileName;
                FileUtil.moveTo(zipPath, zipFile.getParentFile().getParent()+File.separator+"finishzip");
            }
        }
    }
}
