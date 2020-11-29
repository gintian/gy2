package com.hjsj.hrms.module.system.distributedreporting.reportdata;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.DrConstant;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class ReportDataBo {
	private Category log = Category.getInstance(getClass().getName());
    private UserView userView ;
    private Connection conn;
    public ReportDataBo(UserView userView, Connection connection) {
        super();
        this.userView = userView;
        this.conn = connection;
    }
    public String  importDataZip(HashMap map) {
        String return_code = "success";
        String  destDir = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            Map fileHM = PubFunc.DynaBean2Map((MorphDynaBean)(map.get("file")));
            String localName = (String) fileHM.get("localname");//真实名称
            if (!localName.startsWith("DT")||localName.indexOf("_")==-1||!localName.endsWith(".zip")) {
                return ResourceFactory.getProperty("dr_checkdadazip.name");//数据包名称不符合要求
            }
            String unitcode = localName.split("_")[0].substring(2);
            String fileName = (String) fileHM.get("filename");
            fileName = PubFunc.decrypt(fileName);
            ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
            //1、解压idx.json文件
            destDir = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"asynrecive"+File.separator
                    + unitcode+File.separator+"cache";
            String fullpath = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"asynrecive"+File.separator
                    + unitcode+File.separator+"zip"+ File.separator + fileName;
            String fileid = (String) fileHM.get("fileid");
            File destFile = new File(fullpath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();//创建目录
            }
            is = VfsService.getFile(fileid);
            os = new FileOutputStream(destFile);
            FileUtil.write2Out(is,os);
            FileUtil.decryptZipOneFile(fullpath,"idx.json", DrConstant.ZIP_PASSWORD, destDir);
            //2、判断idx.json文件是否存在
            File idxFile = new File(destDir+File.separator+"idx.json");
            if (!idxFile.exists()) {
                return ResourceFactory.getProperty("dr_checkdadazip.idxfile");//数据包不包含idx.json文件
            }
            //3、读取idx.json的内容
            String idsData = FileUtils.readFileToString(new File(destDir+File.separator+"idx.json"), "GBK");
            
            String result = checkIdsJson(idsData,localName);//校验idx.json内的内容是否符合要求
            if ("success".equals(result)) {
                String path = "asyn"+File.separator+"asynrecive"+File.separator+ unitcode+File.separator+"zip"+File.separator+localName;
                //5、保存文件成功后将记录添加数据库。
                saveDataRecord(idsData,path,destDir);
            }else {
                return_code = result;
            }
        } catch (Exception e) {
            return_code = ResourceFactory.getProperty("dr_checkdadazip.error");//校验数据包异常
            e.printStackTrace();
        }finally {
            if (null!=destDir) {
              //6、无论校验通过成功或未通过把临时文件删除
                FileUtil.delfile(destDir, "idx.json");
            }
            PubFunc.closeResource(is);
            PubFunc.closeResource(os);
        }
        return return_code;
    }
   /**
    * 根据idx.json内的数据将记录保存进数据库
    * @param idsdata idx.json 文件内的数据
    * @param path
    * @param destDir
    */
    public void saveDataRecord(String idsdata,String path,String destDir) {
        RowSet rowSet = null;
        try {
            JSONObject idsJson = JSONObject.fromObject(idsdata);
            String orgid = idsJson.getString("orgid");//guidkey
            String orgname = idsJson.getString("orgname");
            String orgcode = idsJson.getString("orgcode");//单位编码
            String logid = idsJson.getString("logid");
            String asyntype = idsJson.getString("asyntype");//增量还是全量 0是全量 1是增量
            String incstarttime = idsJson.getString("incstarttime");
            String endtime = idsJson.getString("endtime");
            Timestamp startdate = DateUtils.getTimestamp(incstarttime, "yyyyMMddHHmmssSSS");
            Timestamp enddate = DateUtils.getTimestamp(endtime, "yyyyMMddHHmmssSSS");
            ContentDAO dao = new ContentDAO(conn);
            int id = 0 ;
            //查询出此数据包是否存在
            String idString = getRecordId(logid);
            if (idString!=null) {
                RecordVo vo = new RecordVo("t_sys_asyn_acceptinfo");
                vo.setInt("id", Integer.parseInt(idString));
                vo=dao.findByPrimaryKey(vo);
                vo.setDate("reporttime",DateUtils.getTimestamp(new Date()));
                vo.setString("status", DrConstant.RECEIVE_READY);
                vo.setDate("accepttime","");
                dao.updateValueObject(vo);
            }else {
                //查询出接收记录表id的最大值
                String sql = "select max(id) id from t_sys_asyn_acceptinfo";
                rowSet = dao.search(sql);
                if (rowSet.next()) {
                    id = rowSet.getInt("id")+1;
                }
                UUID uuid = UUID.randomUUID();
                String guidkey=uuid.toString().toUpperCase();
                RecordVo vo = new RecordVo("t_sys_asyn_acceptinfo");
                vo.setInt("id", id);
                vo.setString("guidkey", guidkey);
                vo.setString("unitcode", orgcode);
                vo.setString("unitguid", orgid);
                vo.setInt("datatype", Integer.parseInt(asyntype));
                vo.setDate("datastarttime",startdate);
                vo.setDate("dataendtime", enddate );
                vo.setDate("reporttime", DateUtils.getTimestamp(new Date()));
                vo.setString("status",DrConstant.RECEIVE_READY);
                vo.setString("pkgguid", logid);
                vo.setString("pkgpath", path);
                dao.addValueObject(vo);
            }
        } catch (Exception e) {
            if (null!=destDir) {//如果在保存过程中出现异常把缓存的idx.json文件删除
                  FileUtil.delfile(destDir, "idx.json");
            }
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }
    }
    /**
     * 接收记录表是否存在该数据包的记录
     * @param logid 数据包的唯一id
     * @return  存在返回true，不存在返回false;
     */
    private String getRecordId (String logid) {
        String result = null;
        RowSet rowSet = null;
        try {
            String sql ="select id from t_sys_asyn_acceptinfo where pkgguid = '"+logid+"'";
            ContentDAO dao = new ContentDAO(conn);
            rowSet=dao.search(sql);
            if (rowSet.next()) {
                result = rowSet.getString("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return result;
    }
    /**
     * 校验数据包内的idx.json
     * @param idsdata
     * @param localName
     * @return
     */
    public String checkIdsJson(String idsdata,String localName) {
       String result = "success";
       ResultSet resultSet = null;
       try {
           JSONObject idsJson = JSONObject.fromObject(idsdata);
           String orgid = idsJson.getString("orgid");//guidkey
           String orgname = idsJson.getString("orgname");
           String orgcode = idsJson.getString("orgcode");//单位编码
           if (localName.indexOf(orgcode)==-1) {
               result=ResourceFactory.getProperty("dr_checkdadazip.zipname");//压缩包名称与数据包内文件不对应
           }
           String logid = idsJson.getString("logid");
           String asyntype = idsJson.getString("asyntype");//增量还是全量1是全量 2是增量
           String incstarttime = idsJson.getString("incstarttime");
           String endtime = idsJson.getString("endtime");
           Timestamp startdate = DateUtils.getTimestamp(incstarttime, "yyyyMMddHHmmssSSS");
           Timestamp enddate = DateUtils.getTimestamp(endtime, "yyyyMMddHHmmssSSS");
           if (enddate.getTime()<startdate.getTime()) {
               result=ResourceFactory.getProperty("dr_checkdadazip.time");//数据的开始时间不能大于数据的截止时间
           }
           if (StringUtils.equals("2",asyntype)) {
               ContentDAO dao = new ContentDAO(conn);
               resultSet = dao.search("select id,dataStartTime,dataEndTime from t_sys_asyn_acceptinfo where unitguid = '"+orgid+"' order by id desc");
               if (resultSet.next()) {
                  Timestamp dataEndTime = resultSet.getTimestamp("dataEndTime");
                  log.debug(orgcode+"新数据包的startdate:"+startdate);
                  log.debug(orgcode+"上一次数据包dataEndTime:"+dataEndTime);
                  if (startdate.getTime()>dataEndTime.getTime()) {
                      result=ResourceFactory.getProperty("dr_checkdadazip.interval");//数据存在间隔
                  }
               }
           }
       } catch (Exception e) {
           result = ResourceFactory.getProperty("dr_checkdadazip.error");//校验数据包异常
           e.printStackTrace();
       }finally {
    	   PubFunc.closeDbObj(resultSet);
	   }
       return result;
    }
    /**
     * 获得接收记录表的sql;
     * @param value
     * @return
     */
    public String getReportDataSql(String value) {
        String sql = "";
        DbWizard dbWizard = new DbWizard(conn);
        int flag = DbWizard.dbflag;
        //id  guidkey  unitcode datastarttime dataendtime reporttime acceptTime status downloadLog c
        StringBuffer fieldbuffer = new StringBuffer("");
        fieldbuffer.append(" id,guidkey,unitcode,");
        fieldbuffer.append(Sql_switcher.dateToChar("datastarttime", "yyyy-MM-dd HH24:mi:ss"));
        fieldbuffer.append(" datastarttime,");
        fieldbuffer.append(Sql_switcher.dateToChar("dataendtime", "yyyy-MM-dd HH24:mi:ss"));
        fieldbuffer.append(" dataendtime,");
        fieldbuffer.append(Sql_switcher.dateToChar("reporttime", "yyyy-MM-dd HH24:mi:ss"));
        fieldbuffer.append(" reporttime,");
        fieldbuffer.append(Sql_switcher.dateToChar("acceptTime", "yyyy-MM-dd HH24:mi:ss"));
        fieldbuffer.append(" acceptTime,status,situation ");
        if("all".endsWith(value)) {//全部
            sql = "select "+fieldbuffer.toString()+" from t_sys_asyn_acceptinfo where 1=1 ";
        }else if ("today".endsWith(value)) {//今天
            if (flag==2) {
                sql = "select "+fieldbuffer.toString()+" from t_sys_asyn_acceptinfo where reporttime >= trunc(sysdate) and reporttime < trunc(sysdate)+1"; 
            }else {
                sql = "select "+fieldbuffer.toString()+" from t_sys_asyn_acceptinfo where DateDiff(dd,reporttime,getdate())=0";
            }
            
        }else if ("week".endsWith(value)) {//7天
            if (flag==2) {
                sql = "select "+fieldbuffer.toString()+" from t_sys_asyn_acceptinfo where trunc(sysdate)-7 <= reporttime";
            }else {
                sql = "select "+fieldbuffer.toString()+" from t_sys_asyn_acceptinfo where DateDiff(dd,reporttime,getdate())<=7";
            }
        }else {//30天
            if (flag==2) {
                sql = "select "+fieldbuffer.toString()+" from t_sys_asyn_acceptinfo where trunc(sysdate)-30 <= reporttime";
            }else {
                sql = "select "+fieldbuffer.toString()+" from t_sys_asyn_acceptinfo where DateDiff(dd,reporttime,getdate())<=30";
            }
        }
        String prv = getUnitIdByBusi(userView);
        if (!"UN".equals(prv)&&StringUtils.isNotEmpty(prv)) {
            String[] prvArray = prv.split(",");
            StringBuffer buffer = new StringBuffer("(");
            for (int i = 0; i < prvArray.length; i++) {
              if (i!=0) {
                buffer.append(" or ");
              }
              buffer.append("unitcode like ");
              buffer.append("'");
              buffer.append(prvArray[i]);
              buffer.append("%'");
            }
            buffer.append(")");
            sql+=" and "+buffer.toString();
        }
        if (prv==null||"".equals(prv)) {
            sql+=" and 1=2";
        }
        return sql;
    }
   /**
    * 获得当前用户的业务范围
    * @param userView
    * @return
    */
    private String getUnitIdByBusi(UserView userView){
        String orgPriv = null;
        StringBuffer priv = new StringBuffer("");
        try {
            if(userView.isSuper_admin()){
                return "UN";
            }
        	/*1、返回UN`为全权
            2、返回xx`xx`返回的为具体的权限，xx为具体的单位或者部门,单位以UN开头，部门以UM开头，多个权限使用`进行分割
            3、返回UN为没有权限*/
            orgPriv = userView.getUnitIdByBusi("4");
            if(orgPriv == null || orgPriv.trim().length() == 0||"UN".equals(orgPriv))
                return "";
            orgPriv = orgPriv.replaceAll("`",",");
            String[] orgPrivs = orgPriv.split(",");
            for(int i = 0 ; i < orgPrivs.length ; i++){
                if (i>0) {
                    priv.append(",");
                }
                if ("UN".equals(orgPrivs[i].toUpperCase())) {
                    return "UN";
                }else if (orgPrivs[i].toUpperCase().startsWith("UN")&&orgPrivs[i].length()>2) {
                    priv.append(orgPrivs[i].substring(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priv.toString();
    }
    /**
     * 获得t_sys_asyn_acceptinfo表的列
     * @return
     * @throws GeneralException
     */
    public ArrayList<ColumnsInfo> getReportDataColumnList() throws GeneralException {
        ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
        try {
            FieldItem fieldItem0 = new FieldItem();
            fieldItem0.setItemid("id");
            fieldItem0.setItemdesc("id");
            fieldItem0.setItemtype("N");
            fieldItem0.setCodesetid("0");
            ColumnsInfo info0 = new ColumnsInfo(fieldItem0);
            info0.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            list.add(info0);
            FieldItem fieldItem5 = new FieldItem();
            fieldItem5.setItemid("guidkey");
            fieldItem5.setItemdesc("guidkey");
            fieldItem5.setItemtype("A");
            fieldItem5.setCodesetid("0");
            ColumnsInfo info5 = new ColumnsInfo(fieldItem5);
            info5.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            list.add(info5);
            FieldItem fieldItem1 = new FieldItem();
            fieldItem1.setItemid("unitcode");
            fieldItem1.setItemdesc(ResourceFactory.getProperty("dr_unit.name"));
            fieldItem1.setItemtype("A");
            fieldItem1.setCodesetid("UN");
            ColumnsInfo info1 = new ColumnsInfo(fieldItem1);
            info1.setColumnWidth(200);
            info1.setEditableValidFunc("false");
            info1.setLocked(true);
            info1.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info1);
            FieldItem fieldItem2 = new FieldItem();
            fieldItem2.setItemid("datastarttime");
            fieldItem2.setItemdesc(ResourceFactory.getProperty("dr_accept.datastarttime"));
            fieldItem2.setItemtype("A");
            fieldItem2.setCodesetid("0");
            ColumnsInfo info2 = new ColumnsInfo(fieldItem2);
            info2.setTextAlign("left");
            info2.setColumnWidth(200);
            info2.setLocked(false);
            info2.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info2);
            FieldItem fieldItem7 = new FieldItem();
            fieldItem7.setItemid("dataendtime");
            fieldItem7.setItemdesc(ResourceFactory.getProperty("dr_accept.dataendtime"));
            fieldItem7.setItemtype("A");
            fieldItem7.setCodesetid("0");
            ColumnsInfo info7 = new ColumnsInfo(fieldItem7);
            info7.setTextAlign("left");
            info7.setColumnWidth(200);
            info7.setLocked(false);
            info7.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info7);
            FieldItem fieldItem8 = new FieldItem();
            fieldItem8.setItemid("reporttime");
            fieldItem8.setItemdesc(ResourceFactory.getProperty("dr_accept.reporttime"));
            fieldItem8.setItemtype("A");
            fieldItem8.setCodesetid("0");
            ColumnsInfo info8 = new ColumnsInfo(fieldItem8);
            info8.setTextAlign("left");
            info8.setColumnWidth(200);
            info8.setLocked(false);
            info8.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info8);
            FieldItem fieldItem6 = new FieldItem();
            fieldItem6.setItemid("acceptTime");
            fieldItem6.setItemdesc(ResourceFactory.getProperty("dr_accept.time"));
            fieldItem6.setItemtype("A");
            fieldItem6.setCodesetid("0");
            ColumnsInfo info6 = new ColumnsInfo(fieldItem6);
            info6.setTextAlign("left");
            info6.setColumnWidth(200);
            info6.setLocked(false);
            info6.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info6);
            FieldItem fieldItem4 = new FieldItem();
            fieldItem4.setItemid("status");
            fieldItem4.setItemdesc(ResourceFactory.getProperty("dr_accept.status"));
            fieldItem4.setItemtype("A");
            fieldItem4.setCodesetid("0");
            ColumnsInfo info4 = new ColumnsInfo(fieldItem4);
            info4.setLocked(false);
            info4.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info4);
            FieldItem fieldItem3 = new FieldItem();
            fieldItem3.setItemid("downloadLog");
            fieldItem3.setItemdesc(ResourceFactory.getProperty("dr_accept.logs"));
            fieldItem3.setItemtype("A");
            fieldItem3.setCodesetid("0");
            ColumnsInfo info3 = new ColumnsInfo(fieldItem3);
            info3.setTextAlign("left");
            info3.setLocked(false);
            info3.setSortable(false);//不排序
            info3.setRendererFunc("ReportDataGlobal.downloadingLog");
            info3.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info3);
            FieldItem fieldItem9 = new FieldItem();
            fieldItem9.setItemid("situation");
            fieldItem9.setItemdesc(ResourceFactory.getProperty("dr_accept.situation"));
            fieldItem9.setItemtype("A");
            fieldItem9.setCodesetid("0");
            ColumnsInfo info9  = new ColumnsInfo(fieldItem9);
            info9.setTextAlign("left");
            info9.setLocked(false);
            info9.setSortable(false);//不排序
            info9.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            list.add(info9);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }
    /**
     * 获得上报数据的工具栏
     * @return
     * @throws GeneralException
     */
    public ArrayList getReportDataButtonList() throws GeneralException {
        ArrayList buttonList = new ArrayList();
        try {
            buttonList.add(new ButtonInfo(ResourceFactory.getProperty("dr_report.data"),"ReportDataGlobal.importDataZip"));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return buttonList;
    }
}
