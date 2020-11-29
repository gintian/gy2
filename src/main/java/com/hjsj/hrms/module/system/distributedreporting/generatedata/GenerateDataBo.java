package com.hjsj.hrms.module.system.distributedreporting.generatedata;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.DrConstant;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.DrLogger;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.module.system.distributedreporting.generatedata.generatedatabean.FielditemListBean;
import com.hjsj.hrms.module.system.distributedreporting.generatedata.generatedatabean.MenusBean;
import com.hjsj.hrms.module.system.distributedreporting.generatedata.generatedatabean.SetListBean;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @Title: GenerateDataBo
 * @Description:生成上报数据BO类
 * @Company:hjsj
 * @Create time: 2019/5/23 14:19:05
 * @author: wangbs
 * @version: 1.0
 */
public class GenerateDataBo {

    public UserView userView;
    public Connection conn;
    private HashMap<String,String> filterConditionMap = new HashMap();

    public GenerateDataBo(UserView userView,Connection connection){
        this.userView = userView;
        this.conn = connection;
    }
    /**
     * 拼装生成数据页面列头信息
     *
     * @param
     * @return ColumnsInfo
     * @throws GeneralException
     * @author wangbs
     */
    public ArrayList<ColumnsInfo> getColumnList() {
        ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
        try {
            ColumnsInfo idCol = createColInfo(200, "recordid", "recordid", "A", "0", ColumnsInfo.LOADTYPE_ONLYLOAD, "");
            list.add(idCol);

            ColumnsInfo sendtimeCol = createColInfo(200, "sendtime", ResourceFactory.getProperty("dr_report.date"), "D", "0", ColumnsInfo.LOADTYPE_BLOCK, "");
            list.add(sendtimeCol);

            ColumnsInfo sendtypeCol = createColInfo(200, "sendtype", ResourceFactory.getProperty("dr_report.way"), "A", "0", ColumnsInfo.LOADTYPE_BLOCK, "");
            list.add(sendtypeCol);

            ColumnsInfo statusCol = createColInfo(200, "status", ResourceFactory.getProperty("dr_report.state"), "A", "0", ColumnsInfo.LOADTYPE_BLOCK, "");
            list.add(statusCol);

            ColumnsInfo operationCol = createColInfo(200, "operation", ResourceFactory.getProperty("dr_operation"), "A", "0", ColumnsInfo.LOADTYPE_BLOCK, "GenerateReportData.renderOperaCol");
            list.add(operationCol);

            ColumnsInfo situationCol = createColInfo(200, "situation", ResourceFactory.getProperty("dr_report.note"), "M", "0", ColumnsInfo.LOADTYPE_BLOCK, "");
            list.add(situationCol);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 创建ColumnsInfo列对象
     * @author wangbs
     * @param colWidth 列宽
     * @param colId 列id
     * @param colDesc 列描述
     * @param colType 列类型
     * @param codeSetId 代码类编号
     * @param loadType 列加载方式
     * @param renderFunc 列渲染方法
     * @return ColumnsInfo
     * @throws
     */
    public ColumnsInfo createColInfo(int colWidth,String colId,String colDesc,String colType,String codeSetId,int loadType,String renderFunc){
        ColumnsInfo columnInfo = new ColumnsInfo();
        try{
            columnInfo.setColumnWidth(colWidth);
            columnInfo.setColumnId(colId);
            columnInfo.setColumnDesc(colDesc);
            columnInfo.setColumnType(colType);
            columnInfo.setCodesetId(codeSetId);
            columnInfo.setLoadtype(loadType);

            if (StringUtils.isNotBlank(renderFunc)) {
                columnInfo.setRendererFunc(renderFunc);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return columnInfo;
    }

    /**
     * 删除因取消匹配信息集造成的指标、代码对应表中的脏数据
     * @author wangbs
     * @param set1 上级信息集编码
     * @return void
     * @throws
     */
    public void deleteSetAssociatedData(String set1) {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        List sqlParamList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        try {
            int schemeId = getSchemeId();
            sqlParamList.add(schemeId);
            sqlParamList.add(set1);
            //根据分布式id和set1查出废掉的指标
            sql.append("select field1 from trandb_field where id=? and set1=?");
            rs = dao.search(sql.toString(), sqlParamList);
            List field1List = new ArrayList();
            while (rs.next()) {
                String field1 = rs.getString(1);
                field1List.add(field1);
            }

            if (field1List.size() > 0) {
                deleteFieldAssociatedData(field1List);

                sql.setLength(0);
                //最后根据分布式id和set1删除废掉的指标对应关系
                sql.append("delete from trandb_field where id=? and set1=?");
                dao.delete(sql.toString(), sqlParamList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 删除因取消匹配指标造成的代码对应表中的脏数据
     * @param field1List
     * @return void
     * @throws
     * @author wangbs
     */
    public void deleteFieldAssociatedData(List field1List) {
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            sql.append("delete from trandb_code where fielditem1 in(");
            for (int i = 0; i < field1List.size(); i++) {
                if (i == field1List.size() - 1) {
                    sql.append("?)");
                    break;
                }
                sql.append("?,");
            }

            if (CollectionUtils.isNotEmpty(field1List)) {
                //根据废掉的指标删除废掉的代码对应关系
                dao.delete(sql.toString(), field1List);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id获取t_sys_asyn_sendinfo 里的压缩包路径
     * @param idString
     * @return 数据包的路径
     */
    public String getPackagePath(String idString) {
        String path = null;
        RowSet rowSet = null;
        try {
            int id = Integer.parseInt(idString);
            String sql  = "select pkgpath from t_sys_asyn_sendinfo where id = "+id;
            rowSet = new ContentDAO(conn).search(sql);
            if (rowSet.next()) {
                path = rowSet.getString("pkgpath");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return path;
    }
    /**
     * 查询某范围内的上报数据
     * @author wangbs
     * @param searchPlan 查询方案
     * @return LazyDynaBean>
     * @throws
     */
    public ArrayList getReportData(String searchPlan){
        ArrayList datalist = new ArrayList();
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.conn);
        String return_code = "success";
        try {
            StringBuffer sql = new StringBuffer();
            //默认查询所有上报数据
            sql.append("select * from (select si.id,"+Sql_switcher.dateToChar("si.sendtime","yyyy-MM-dd HH24:mi:ss")+" sendtime,si.sendtype,si.status,si.pkgpath,si.situation,rd.mainguidkey ");
            sql.append("from t_sys_asyn_sendinfo si left join t_sys_asyn_record rd on si.guidkey=rd.mainguidkey) temp_table ");
            //查询今天的数据
            if ("today".equals(searchPlan)) {
                if (Sql_switcher.searchDbServer() == 1) {
                    sql.append("where convert(varchar(10),sendtime,20) = " + Sql_switcher.today());
                } else if (Sql_switcher.searchDbServer() == 2) {
                    sql.append("where TO_DATE(TO_CHAR(TO_DATE(sendtime,'yyyy-MM-dd hh24:mi:ss'),'YYYY.MM.DD'),'YYYY.MM.DD') = " + Sql_switcher.today());
                }
            }
            //查询一周内的数据
            else if ("week".equals(searchPlan)) {
                if (Sql_switcher.searchDbServer() == 1) {
                    sql.append("where convert(varchar(10), sendtime, 20) > ");
                    sql.append("convert(varchar(10), dateadd(d,-7,getdate()), 20)");
                } else if (Sql_switcher.searchDbServer() == 2) {
                    sql.append("where TO_DATE(TO_CHAR(TO_DATE(sendtime,'yyyy-MM-dd hh24:mi:ss'),'YYYY.MM.DD'),'YYYY.MM.DD') > ");
                    sql.append("TO_DATE(TO_CHAR(SYSDATE-interval '7' day,'YYYY.MM.DD'),'YYYY.MM.DD')");
                }
            }
            //查询一个月内的数据
            else if ("month".equals(searchPlan)) {
                if (Sql_switcher.searchDbServer() == 1) {
                    sql.append("where convert(varchar(10), sendtime, 20) > ");
                    sql.append("convert(varchar(10), dateadd(d,-30,getdate()), 20)");
                } else if (Sql_switcher.searchDbServer() == 2) {
                    sql.append("where TO_DATE(TO_CHAR(TO_DATE(sendtime,'yyyy-MM-dd hh24:mi:ss'),'YYYY.MM.DD'),'YYYY.MM.DD') > ");
                    sql.append("TO_DATE(TO_CHAR(SYSDATE-interval '30' day,'YYYY.MM.DD'),'YYYY.MM.DD')");
                }
            }
            sql.append(" order by temp_table.id desc");
            rowSet = dao.search(sql.toString());
            datalist.add(return_code);
            List guidkeyList = new ArrayList();
            while (rowSet.next()) {
                //默认本条记录显示到前台
                boolean continueFlag = false;
                //记录id
                int recordId = rowSet.getInt("id");
                String sendTime = rowSet.getString("sendtime");
                //上报状态
                String status = StringUtils.isBlank(rowSet.getString("status")) ? "" : rowSet.getString("status");
                //备注
                String situation = StringUtils.isBlank(rowSet.getString("situation")) ? "" : rowSet.getString("situation");
                String mainGuidKey = StringUtils.isBlank(rowSet.getString("mainguidkey")) ? "" : rowSet.getString("mainguidkey");

                for (int i = 0; i < guidkeyList.size(); i++) {
                    String guidKey = (String) guidkeyList.get(i);
                    if (mainGuidKey.equals(guidKey)) {
                        continueFlag = true;
                        break;
                    }
                }

                if (continueFlag) {
                    continue;
                }
                if (StringUtils.isNotBlank(mainGuidKey)) {
                    guidkeyList.add(mainGuidKey);
                }

                LazyDynaBean bean= new LazyDynaBean();

                bean.set("recordid", recordId);
                bean.set("sendtime", sendTime);
                bean.set("status", status);
                bean.set("situation", situation);

                String pkgpath = rowSet.getString("pkgpath");//数据包路径
                String displayFlag = "0";//默认都为空值 校验结果和数据包都不显示

                if (StringUtils.isNotBlank(pkgpath) && StringUtils.isBlank(mainGuidKey)) {
                    displayFlag = "1";//显示数据包，不显示校验结果
                }else if (StringUtils.isBlank(pkgpath) && StringUtils.isNotBlank(mainGuidKey)) {
                    displayFlag = "2";//显示校验结果，不显示数据包
                }else if (StringUtils.isNotBlank(pkgpath) && StringUtils.isNotBlank(mainGuidKey)) {
                    displayFlag = "3";//都显示
                }
                bean.set("operation", displayFlag);
                int sendType = rowSet.getInt("sendtype");//上报方式
                if (sendType == 0) {//翻译上报方式
                    bean.set("sendtype", ResourceFactory.getProperty("dr_report.manual"));
                } else if (sendType == 1) {
                    bean.set("sendtype", ResourceFactory.getProperty("dr_report.database"));
                } else if (sendType == 2) {
                    bean.set("sendtype", ResourceFactory.getProperty("dr_report.ftp"));
                } else if (sendType == 3) {
                    bean.set("sendtype", ResourceFactory.getProperty("dr_report.webservice"));
                }
                datalist.add(bean);
            }
        } catch (Exception e) {
            return_code = "fail";
            datalist.add(return_code);
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rowSet);
        }
        return datalist;
    }
    /**
     * 创建操作按钮
     * @author wangbs
     * @param
     * @return ArrayList
     * @throws
     */
    public ArrayList getButtonList() {
        ArrayList buttonList = new ArrayList();
        try {
            String createPlan = ResourceFactory.getProperty("dr_create.plan");//创建方案
            String reportData = ResourceFactory.getProperty("dr_report.data");//上报数据
            String deleteLog = ResourceFactory.getProperty("dr_delete.log");//删除日志
            buttonList.add(new ButtonInfo(createPlan,"GenerateReportData.createPlan"));
            buttonList.add("-");
            buttonList.add(new ButtonInfo(reportData,"GenerateReportData.reportData"));
            buttonList.add("-");
            buttonList.add(new ButtonInfo(deleteLog,"GenerateReportData.deleteLog"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buttonList;
    }
    /**
     * 删除日志
     * @author wangbs
     * @param delIdList
     * @return String
     * @throws
     */
    public String deleteLog(List delIdList) {
        String return_code = "success";
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer delInfoSql = new StringBuffer();
        StringBuffer delRecordSql = new StringBuffer();
        StringBuffer selectRecordSql = new StringBuffer();
        List delRecordList = new ArrayList();
        try {
            selectRecordSql.append("select guidkey from t_sys_asyn_sendinfo where id in(");
            delInfoSql.append("delete from t_sys_asyn_sendinfo where id in (");
            delRecordSql.append("delete from t_sys_asyn_record where mainguidkey in(");
            for (int i = 0; i < delIdList.size(); i++) {
                if (i == delIdList.size() - 1) {
                    delInfoSql.append("?)");
                    delRecordSql.append("?)");
                    selectRecordSql.append("?)");
                    continue;
                }
                delInfoSql.append("?,");
                delRecordSql.append("?,");
                selectRecordSql.append("?,");
            }

            rs = dao.search(selectRecordSql.toString(), delIdList);
            while (rs.next()) {
                String guidKey = rs.getString(1);
                delRecordList.add(guidKey);
            }
            dao.delete(delRecordSql.toString(), delRecordList);
            dao.delete(delInfoSql.toString(), delIdList);
        } catch (Exception e) {
            return_code = "fail";
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return return_code;
    }

    /**
     * 查询手工上报数据window回显数据
     * @author wangbs
     * @param
     * @return Map
     * @throws
     */
    public Map echoReportDataWinInfo() {
        Map winDataMap = new HashMap();
        RowSet rs = null;
        List sqlParamList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            //获得上级导出方案中的全量||增量  用于前台radio的默认选择
            RecordVo vo = ConstantParamter.getRealConstantVo(DrConstant.BS_ASYN_PARAM_C);
            String paramCJson = vo.getString("str_value");
            Map paramCMap = customJsonTranMap(paramCJson);
            String pkgType = (String) paramCMap.get("pkgtype");
            winDataMap.put("pkgType", pkgType);


            String successPkg = ResourceFactory.getProperty("dr_pkg.success");
            String successReport = ResourceFactory.getProperty("dr_report.success");
            sqlParamList.add(successPkg);
            sqlParamList.add(successReport);

            //获取最近一次成功的记录
            String sendTime = "";
            sql.append("select max("+Sql_switcher.dateToChar("sendtime","yyyy-MM-dd HH24:mi:ss")+") from t_sys_asyn_sendinfo where status in(?,?)");
            rs = dao.search(sql.toString(),sqlParamList);
            if(rs.next()){
                //最近成功上报数据时间
                if (rs.getString(1) == null) {
                    winDataMap.put("sendTime", sendTime);
                    return winDataMap;
                }
                sendTime = rs.getString(1);
                winDataMap.put("sendTime", sendTime);

                sql.setLength(0);
                sql.append("select datatype,sendtype from t_sys_asyn_sendinfo where status in(?,?) and sendtime=?");
                sqlParamList.add(DateUtils.getSqlDate(rs.getString(1),"yyyy-MM-dd HH:mm:ss"));
                rs = dao.search(sql.toString(), sqlParamList);
                if (rs.next()) {
                    int dataType = rs.getInt(1);//手动选择的全量||增量
                    int sendType = rs.getInt(2);//上报方式

                    winDataMap.put("dataType", dataType);
                    winDataMap.put("sendType", sendType);
                }
            }else{
                winDataMap.put("sendTime", sendTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return winDataMap;
    }


    /**
     * 获取codeItemInfo.json
     * @author wangbs
     * @param
     * @return String
     * @throws
     */
    public String getCodeItemInfo(){
        String codeItemInfo = "";
        try {
            //获得json文件的存放路径
            ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
            String jsonPath = constantXml.getNodeAttributeValue("/filepath", "rootpath") + File.separator + "asyn" +
                    File.separator + "scheme" + File.separator + "client" + File.separator + "sch" + File.separator + "cache";
            //读codeitems.json的内容
            String targetFilePath = jsonPath + File.separator + "codeitems.json";
            codeItemInfo = FileUtils.readFileToString(new File(targetFilePath), "GBK");//json文件所有内容
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeItemInfo ;
    }
    /**
     * 校验并读取文件
     * @author wangbs
     * @param fileid 文件id
     * @param localName 文件名
     * @return List
     */
    public List checkAndReadFile(String fileid, String localName) {
        List fileList = new ArrayList();
        //存放文件内容 menus={}...codeitems={}...
        Map fileMap = new HashMap();
        OutputStream os = null;
        try {
            ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
            //获得压缩包的存放路径
            String tempPath = constantXml.getNodeAttributeValue("/filepath", "rootpath") + File.separator + "asyn" +
                    File.separator + "scheme" + File.separator + "client" + File.separator + "sch";
            String filePath = tempPath + File.separator + "zipFile" + File.separator + localName;
            //获得json文件的存放路径
            String jsonPath = tempPath + File.separator + "cache";

            InputStream is = VfsService.getFile(fileid);
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();//创建目录
            }
            os = new FileOutputStream(destFile);
            int readLen = -1;
            byte[] buff = new byte[8192];
            while ((readLen = is.read(buff)) != -1) {
                os.write(buff, 0, readLen);
            }
            //解压到本地 用于读取
            FileUtil.decryptZip(filePath,DrConstant.ZIP_PASSWORD,jsonPath);

            //校验zip包名
            String[] localNameArr = localName.split("\\.");
            if (localNameArr[0].startsWith("FA") && "zip".equals(localNameArr[1])) {
                //记录json文件个数
                int fileCount = 0;
                //获取压缩包内所有文件名
                List fileNameList = FileUtil.getEntryNames(filePath, DrConstant.ZIP_PASSWORD);

                for (int i = 0; i < fileNameList.size(); i++) {
                    //校验json文件名
                    String fileName = (String) fileNameList.get(i);
                    String[] fileNameArr = fileName.split("\\.");
                    if (("menus".equals(fileNameArr[0]) || "codeitems".equals(fileNameArr[0]) || fileNameArr[0].startsWith("reportunit")) && "json".equals(fileNameArr[1])) {
                        //文件重复
                        if (fileMap.get(fileNameArr[0]) != null) {
                            fileList.add("fileerror");
                            return fileList;
                        }
                        //读文件
                        String targetFilePath = jsonPath + File.separator + fileName;
                        //json文件所有内容
                        String fileStr = FileUtils.readFileToString(new File(targetFilePath), "GBK");
                        fileMap.put(fileNameArr[0], fileStr);
                        fileCount++;//完成一个
                    } else {
                        //文件不合规范
                        fileList.add("fileerror");
                        return fileList;
                    }
                }
                if (fileCount == 3) {
                    //三个文件才对
                    fileList.add("success`" + tempPath + File.separator + "zipFile");
                    fileList.add(fileMap);
                } else {
                    //文件数目不对
                    fileList.add("fileerror");
                }
            } else {
                //zip包名不合规范
                fileList.add("fileerror");
            }
        } catch (Exception e) {
            //读写错误
            fileList.add("ioerror");
            e.printStackTrace();
        }finally {
        	PubFunc.closeResource(os);
        }
        return fileList;
    }
    /**
     * 保存menus.json到constant表BS_ASYN_PLAN_C中
     * @author wangbs
     * @param fileList zip包中的所有文件
     * @return void
     * @throws
     */
    public void saveMenusJson (List fileList){
        ContentDAO dao=new ContentDAO(this.conn);
        Map fileMap = (HashMap) fileList.get(1);
        String menusStr = (String) fileMap.get("menus");
        try {
            RecordVo vo = new RecordVo("constant");
            vo.setString("constant", DrConstant.BS_ASYN_PLAN_C);
            vo.setString("str_value", menusStr);
            RecordVo option_vo= ConstantParamter.getRealConstantVo(DrConstant.BS_ASYN_PLAN_C);
            if (option_vo == null) {
                dao.addValueObject(vo);
            }else{
                dao.updateValueObject(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取menus.json内容
     * @author wangbs
     * @param
     * @return String
     * @throws
     */
    public String getMenusInfo(){
        String menusInfo = "";
        try {
            RecordVo vo = ConstantParamter.getRealConstantVo(DrConstant.BS_ASYN_PLAN_C);
            if (vo != null) {
                menusInfo = vo.getString("str_value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menusInfo;
    }
    /**
     * json数据转map
     * @author wangbs
     * @param jsonInfo
     * @return Map
     * @throws
     */
    private Map customJsonTranMap(String jsonInfo){
        Gson gson = new Gson();
        Map jsonMap = new HashMap();
        jsonMap = gson.fromJson(jsonInfo, jsonMap.getClass());
        return jsonMap;
    }
    /**
     * 根据新方案变更老数据
     * @author wangbs
     * @param jsonInfo
     * @return void
     * @throws
     */
    public void changeOldData (String jsonInfo,String target){
        List sqlParamList = new ArrayList();//存放sql用到的参数
        try {
            int schemeId = getSchemeId();//方案ID
            sqlParamList.add(schemeId);
            //json字符串转map
            Map jsonMap = customJsonTranMap(jsonInfo);
            if ("second".equals(target)) {//删除信息集对应表的脏数据
                deleteSetWasteData(jsonMap, sqlParamList);
            } else if ("third".equals(target)) {
                Map remainingFieldMap = deleteFieldWasteData(jsonMap, schemeId);
                addNewFieldData(jsonMap, remainingFieldMap, schemeId);
            } else if ("forth".equals(target)) {
                deleteCodeWasteData(jsonMap, schemeId);
            } else if ("sixth".equals(target)) {
                deleteVerifyRule();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 导入新方案时删除之前的校验规则
     * @author wangbs
     * @param
     * @return void
     * @throws
     */
    private void deleteVerifyRule() {
        ContentDAO dao = new ContentDAO(this.conn);
        List list = new ArrayList();
        try {
            list.add(1);//1：导入的上级的校验条件
            String sql = "delete from t_sys_asyn_validaterules where belong=?";
            dao.delete(sql, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除代码对应脏数据
     * @author wangbs
     * @param jsonMap
     * @param schemeId
     * @return Map
     * @throws
     */
    private void deleteCodeWasteData(Map jsonMap, int schemeId){
        List sqlParamList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            sql.append("delete from trandb_code where id=? and codeset1 not in(");
            sqlParamList.add(schemeId);
            List codeSetList = (ArrayList) jsonMap.get("codeset_list");//新方案的所有代码类
            for (int i = 0; i < codeSetList.size(); i++) {
                LinkedTreeMap oneCodeSetMap = (LinkedTreeMap)codeSetList.get(i);
                String codeSetId = (String)oneCodeSetMap.get("codeset_id");
                sqlParamList.add(codeSetId);
                if (i == codeSetList.size() - 1) {
                    sql.append("?)");
                    break;
                }
                sql.append("?,");
            }
            if (CollectionUtils.isNotEmpty(codeSetList)) {
                //删除不做对应的脏数据
                dao.delete(sql.toString(), sqlParamList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *
     * @author wangbs
     * @param menusMap
     * @param remainingFieldMap
     * @param schemeId
     * @return void
     * @throws
     */
    private void addNewFieldData(Map menusMap,Map remainingFieldMap,int schemeId){
        ContentDAO dao = new ContentDAO(this.conn);
        RecordVo vo = new RecordVo("trandb_field");
        try {
            List setList = (ArrayList) menusMap.get("set_list");//新方案的所有信息集
            for (int i = 0; i < setList.size(); i++) {
                LinkedTreeMap oneSetMap = (LinkedTreeMap)setList.get(i);
                List fieldItemList = (ArrayList)oneSetMap.get("fielditem_list");//上级信息集指标集合
                String set1 = (String) oneSetMap.get("set_id");//上级信息集id
                String setname1 = (String) oneSetMap.get("set_name");//上级信息集desc

                for (int j = 0; j < fieldItemList.size(); j++) {
                    LinkedTreeMap oneFieldInfo = (LinkedTreeMap) fieldItemList.get(j);
                    String field1 = (String) oneFieldInfo.get("itemid");
                    String dbItemId = (String) remainingFieldMap.get(field1);
                    if (StringUtils.isBlank(dbItemId)) {
                        String fieldname1 = (String) oneFieldInfo.get("itemdesc");
                        String fieldtype1 = (String) oneFieldInfo.get("itemtype");
                        String codeset1 = (String) oneFieldInfo.get("codesetid");
                        int state = 0;//默认非必填
                        String mustbe = (String) oneFieldInfo.get("mustbe");
                        if ("TRUE".equals(mustbe)) {
                            state = 1;
                        }
                        int unique = 0;
                        String uniqueFlag = (String) oneFieldInfo.get("uniqueflag");
                        if ("TRUE".equals(uniqueFlag)) {
                            unique = 1;
                        }

                        vo.setInt("id", schemeId);
                        vo.setString("set1", set1);
                        vo.setString("setname1", setname1);
                        vo.setString("field1", field1);
                        vo.setString("fieldname1", fieldname1);
                        vo.setString("fieldtype1", fieldtype1);
                        vo.setString("codeset1", codeset1);
                        vo.setInt("state", state);
                        vo.setInt("uniqueflag", unique);
                        dao.addValueObject(vo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除指标对应表的脏数据
     * @author wangbs
     * @param menusMap
     * @param schemeId
     * @return Map
     * @throws
     */
    private Map deleteFieldWasteData(Map menusMap, int schemeId) {
        //脏数据删除完之后剩余哪些上级指标对应
        Map remainingFieldMap = new HashMap();
        List sqlParamList = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;

        //新方案比旧方案少子集，for循环中groupDeleteFieldData方法删不掉数据，故按新方案set1删除多余的子集指标对应数据
        List<String> setIdList = new ArrayList<String>();
        StringBuffer deleteSql = new StringBuffer();
        try {
            deleteSql.append("delete from trandb_field where id=").append(schemeId);
            sqlParamList.add(schemeId);
            List setList = (ArrayList) menusMap.get("set_list");//新方案的所有信息集
            for (int i = 0; i < setList.size(); i++) {
                LinkedTreeMap oneSetMap = (LinkedTreeMap)setList.get(i);
				String set1 = (String) oneSetMap.get("set_id");//上级信息集id
				List fieldItemList = (ArrayList)oneSetMap.get("fielditem_list");//信息集所有指标
                setIdList.add(set1);
				sqlParamList.add(set1);

                for (int j = 0; j < fieldItemList.size(); j++) {
                    LinkedTreeMap oneFieldInfo = (LinkedTreeMap) fieldItemList.get(j);
                    String parentItemId = (String) oneFieldInfo.get("itemid");
                    sqlParamList.add(parentItemId);
                    int sqlParamListSize = sqlParamList.size();
                    if (sqlParamListSize % 1000 == 0) {
                        groupDeleteFieldData(sqlParamList, set1);
                        sqlParamList = new ArrayList();
                        sqlParamList.add(schemeId);
                    }
                }
				if (sqlParamList.size() > 1) {
					groupDeleteFieldData(sqlParamList, set1);
					sqlParamList = new ArrayList();
                    sqlParamList.add(schemeId);
				}
            }

            deleteSql.append(" and set1 not in(");
            StringBuffer tempStr = new StringBuffer();
            for (String setId : setIdList) {
                tempStr.append(",?");
            }
            deleteSql.append(tempStr.substring(1)).append(")");
            //按新方案set1删除多余的子集指标对应数据
            dao.delete(deleteSql.toString(), setIdList);

            StringBuffer sql = new StringBuffer();
            List list = new ArrayList();
            list.add(schemeId);
            sql.append("select field1 from trandb_field where id=?");
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                String field1 = rs.getString(1);
                remainingFieldMap.put(field1, field1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return remainingFieldMap;
    }

    /**
     * 满1000参数删一次，以防oracle出问题
     * @author wangbs
     * @param sqlParamList
     * @param set1
     * @return void
     * @throws
     */
    private void groupDeleteFieldData(List sqlParamList, String set1) {
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            sql.append("delete from trandb_field where id=? and set1=? and field1 not in(");
            int sqlParamListSize = sqlParamList.size();
            for (int i = 2; i < sqlParamListSize; i++) {
                if (i == sqlParamListSize - 1) {
                    sql.append("?)");
                    break;
                }
                sql.append("?,");
            }
            if (CollectionUtils.isNotEmpty(sqlParamList)) {
                dao.delete(sql.toString(), sqlParamList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除信息集对应表和过滤条件表的脏数据
     * @author wangbs
     * @param menusMap menus内容转map
     * @param sqlParamList
     * @return void
     * @throws
     */
    private void deleteSetWasteData(Map menusMap, List sqlParamList) {
        StringBuffer sql = new StringBuffer();
        StringBuffer filterConditionSql = new StringBuffer();
        List filterConditionSqlList = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            List setList = (ArrayList) menusMap.get("set_list");//新方案的所有信息集
            for (int i = 0; i < setList.size(); i++) {
                LinkedTreeMap oneSetMap = (LinkedTreeMap) setList.get(i);
                String setId = (String) oneSetMap.get("set_id");
                sqlParamList.add(setId);
                filterConditionSqlList.add(setId);
            }
            sql.append("delete from trandb_set where id=? and set1 not in(");
            int sqlParamListSize = sqlParamList.size();
            for (int i = 1; i < sqlParamListSize; i++) {
                if (i == sqlParamListSize - 1) {
                    sql.append("?)");
                    break;
                }
                sql.append("?,");
            }
            if (CollectionUtils.isNotEmpty(sqlParamList)) {
                dao.delete(sql.toString(), sqlParamList);
            }

            filterConditionSql.append("delete from t_sys_asyn_filtercondition where setid not in(");
            int filterConditionSqlListSize = filterConditionSqlList.size();
            for (int i = 0; i < filterConditionSqlListSize; i++) {
                if (i == filterConditionSqlListSize - 1) {
                    filterConditionSql.append("?)");
                    break;
                }
                filterConditionSql.append("?,");
            }
            if (CollectionUtils.isNotEmpty(filterConditionSqlList)) {
                dao.delete(filterConditionSql.toString(), filterConditionSqlList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 查询方案基本信息用于回显
     * @author wangbs
     * @param
     * @return Map
     * @throws
     */
    public Map selectSchemeInfo() {
        Map strMap = new HashMap();
        try{
            RecordVo option_vo = ConstantParamter.getRealConstantVo(DrConstant.BS_ASYN_PARAM_C);
            if (option_vo != null) {//取方案基本信息
                String schemeInfo = option_vo.getString("str_value");
                strMap = customJsonTranMap(schemeInfo);
                String locOrgCode = (String) strMap.get("locorgcode");
                if(StringUtils.isNotBlank(locOrgCode)){//根据机构编码获取机构描述
                    if (locOrgCode.indexOf("`") == -1) {
                        locOrgCode = "1`" + locOrgCode;
                        strMap.put("locorgcode", locOrgCode);
                    }
                    String orgIds = locOrgCode.split("`")[1];
                    String[] orgIdArr = orgIds.split(",");
                    //机构描述
                    String locOrgDesc = "";
                    for (int i = 0; i < orgIdArr.length; i++) {
                        locOrgDesc = locOrgDesc + "," + AdminCode.getOrgUpCodeDesc(orgIdArr[i], 0, 0);
                    }
                    strMap.put("locorgdesc", locOrgDesc.substring(1));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return strMap;
    }
    /**
     * 保存方案基本信息到constant表BS_ASYN_PARAM_C中
     * @author wangbs
     * @param schemeInfoMap
     * @return void
     * @throws
     */
    public void saveSchemeInfo(Map schemeInfoMap) {
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            RecordVo vo = new RecordVo("constant");
            vo.setString("constant", DrConstant.BS_ASYN_PARAM_C);
            RecordVo option_vo = ConstantParamter.getRealConstantVo(DrConstant.BS_ASYN_PARAM_C);
            JSONObject schemeInfo = null;
            if (option_vo == null) {//没有记录新增，有则更新数据
                schemeInfo = JSONObject.fromObject(schemeInfoMap);
                vo.setString("str_value", schemeInfo.toString());
                dao.addValueObject(vo);
            }else{
                String strValue = option_vo.getString("str_value");
                Map strMap = customJsonTranMap(strValue);
                Map sendParamMap = (Map) strMap.get("sendparam");//取增量变动时间，防止覆盖
                String pkgTime = (String) strMap.get("pkgtime");
                String pkgTimeNew = (String) schemeInfoMap.get("pkgtime");
                if (MapUtils.isNotEmpty(sendParamMap) && pkgTime.equalsIgnoreCase(pkgTimeNew)) {
                    schemeInfoMap.put("sendparam", sendParamMap);
                }
                schemeInfo = JSONObject.fromObject(schemeInfoMap);
                vo.setString("str_value", schemeInfo.toString());
                dao.updateValueObject(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 保存信息集对应关系
     * @author wangbs
     * @param matchSetList
     * @return void
     * @throws
     */
    public void saveMatchSet(List matchSetList){
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try{
            int schemeId = getSchemeId();//获取trandb_scheme中分布式方案id，
            for (int i = 0; i < matchSetList.size(); i++) {
                List list = new ArrayList();

                Map oneSetMatch = PubFunc.DynaBean2Map((MorphDynaBean)matchSetList.get(i));//一条信息集对应关系
                String matchSet = (String) oneSetMatch.get("matchSet");//下级的信息集编码及名称
                String childSetId = StringUtils.EMPTY;
                String childSetName = StringUtils.EMPTY;

                if(StringUtils.isNotBlank(matchSet)) {
                    String[] matchSetArr = matchSet.split(":");
                    childSetId = matchSetArr[0];//下级信息集编码
                    childSetName = matchSetArr[1];//下级信息集名称
                }
                String parentSetId = (String) oneSetMatch.get("parentSetId");//上级信息集编码
                String parentSetName = (String) oneSetMatch.get("parentSetName");//上级信息集名称
                list.add(schemeId);
                list.add(parentSetId);

                String sql = "select id schemeId from trandb_set where id=? and set1=?";
                rs = dao.search(sql,list);
                if (rs.next()) {//有记录就更新,否则新增一条
                    list.add(0,childSetName);
                    list.add(0,childSetId);
                    list.add(0,parentSetName);
                    sql = "update trandb_set set name1=?,set2=?,name2=? where id=? and set1=?";
                    dao.update(sql, list);
                }else{
                    list.add(parentSetName);
                    list.add(childSetId);
                    list.add(childSetName);
                    sql = "insert into trandb_set (id,set1,name1,set2,name2) values (?,?,?,?,?)";
                    dao.insert(sql,list);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }
    /**
     * 更新指标对应数据
     * @author wangbs
     * @param matchFieldList
     * @return void
     * @throws
     */
    public void saveMatchField(List matchFieldList,String set1){
        RowSet rs = null;
        List list = new ArrayList();
        StringBuffer searchSql = new StringBuffer();
        List updateSqlParamList = new ArrayList();//存放update语句参数list进行批量处理（效率高）
        List insertSqlParamList = new ArrayList();//存放insert语句参数list进行批量处理（效率高）
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            int schemeId = getSchemeId();//获取trandb_scheme中分布式方案id
            list.add(schemeId);
            list.add(set1);
            //按上级信息集编码查数据
            searchSql.append("select field1 from trandb_field where id=? and set1=?");
            rs = dao.search(searchSql.toString(), list);
            while (rs.next()) {
                List oneUpdateSqlParamList = new ArrayList();
                String dbField1 = rs.getString(1);//库中已有数据的field1
                for (int i = 0; i < matchFieldList.size(); i++) {
                    Map oneFeildMap = PubFunc.DynaBean2Map((MorphDynaBean)matchFieldList.get(i));
                    String setName1 = (String) oneFeildMap.get("setname1");
                    String fieldName1 = (String) oneFeildMap.get("parentItemName");
                    String fieldType1 = (String) oneFeildMap.get("parentItemType");
                    String codeSet1 = (String) oneFeildMap.get("codeSet");
                    String set2 = (String) oneFeildMap.get("set2");
                    String setName2 = (String) oneFeildMap.get("setname2");

                    String localItem = (String) oneFeildMap.get("localItem");
                    String field2 = "";
                    String fieldName2 = "";
                    if (StringUtils.isNotBlank(localItem)) {
                        field2 = localItem.split(":")[0];
                        fieldName2 = localItem.split(":")[1];
                    }
                    String fieldType2 = (String) oneFeildMap.get("fieldtype2");
                    String codeSet2 = (String) oneFeildMap.get("codeset2");
                    int state = (Integer) oneFeildMap.get("mustItem");
                    int uniqueFlag = (Integer) oneFeildMap.get("onlyItem");

                    String field1 = (String) oneFeildMap.get("parentItemId");
                    if (field1.equals(dbField1)) {
                        oneUpdateSqlParamList.add(set1);
                        oneUpdateSqlParamList.add(setName1);
                        oneUpdateSqlParamList.add(fieldName1);
                        oneUpdateSqlParamList.add(fieldType1);
                        oneUpdateSqlParamList.add(codeSet1);
                        oneUpdateSqlParamList.add(set2);
                        oneUpdateSqlParamList.add(setName2);
                        oneUpdateSqlParamList.add(field2);
                        oneUpdateSqlParamList.add(fieldName2);
                        oneUpdateSqlParamList.add(fieldType2);
                        oneUpdateSqlParamList.add(codeSet2);
                        oneUpdateSqlParamList.add(state);
                        oneUpdateSqlParamList.add(uniqueFlag);
                        oneUpdateSqlParamList.add(schemeId);
                        oneUpdateSqlParamList.add(field1);
                        updateSqlParamList.add(oneUpdateSqlParamList);
                        matchFieldList.remove(i);//更新的移除只剩下需要新增的数据
                        break;
                    }
                }
            }

            for (int i = 0; i < matchFieldList.size(); i++) {
                List oneInsertSqlParamList = new ArrayList();
                Map oneFeildMap = PubFunc.DynaBean2Map((MorphDynaBean)matchFieldList.get(i));
                String field1 = (String) oneFeildMap.get("parentItemId");
                String setName1 = (String) oneFeildMap.get("setname1");
                String fieldName1 = (String) oneFeildMap.get("parentItemName");
                String fieldType1 = (String) oneFeildMap.get("parentItemType");
                String codeSet1 = (String) oneFeildMap.get("codeSet");
                String set2 = (String) oneFeildMap.get("set2");
                String setName2 = (String) oneFeildMap.get("setname2");

                String localItem = (String) oneFeildMap.get("localItem");
                String field2 = "";
                String fieldName2 = "";
                if (StringUtils.isNotBlank(localItem)) {
                    field2 = localItem.split(":")[0];
                    fieldName2 = localItem.split(":")[1];
                }
                String fieldType2 = (String) oneFeildMap.get("fieldtype2");
                String codeSet2 = (String) oneFeildMap.get("codeset2");
                int state = (Integer) oneFeildMap.get("mustItem");
                int uniqueFlag = (Integer) oneFeildMap.get("onlyItem");


                oneInsertSqlParamList.add(schemeId);
                oneInsertSqlParamList.add(field1);
                oneInsertSqlParamList.add(set1);
                oneInsertSqlParamList.add(setName1);
                oneInsertSqlParamList.add(fieldName1);
                oneInsertSqlParamList.add(fieldType1);
                oneInsertSqlParamList.add(codeSet1);
                oneInsertSqlParamList.add(set2);
                oneInsertSqlParamList.add(setName2);
                oneInsertSqlParamList.add(field2);
                oneInsertSqlParamList.add(fieldName2);
                oneInsertSqlParamList.add(fieldType2);
                oneInsertSqlParamList.add(codeSet2);
                oneInsertSqlParamList.add(state);
                oneInsertSqlParamList.add(uniqueFlag);
                insertSqlParamList.add(oneInsertSqlParamList);

            }
            List batchUpdateList = groupListByCount(updateSqlParamList, 500);
            for (int i = 0; i < batchUpdateList.size(); i++) {
                List tempList = (ArrayList) batchUpdateList.get(i);
                StringBuffer updateSql = new StringBuffer();
                updateSql.append("update trandb_field set set1=?,setname1=?,fieldname1=?,fieldtype1=?,codeset1=?,set2=?,");
                updateSql.append("setname2=?,field2=?,fieldname2=?,fieldtype2=?,codeset2=?,state=?,uniqueflag=? where id=? and field1=?");
                dao.batchUpdate(updateSql.toString(), tempList);
            }
            List batchInsertList = groupListByCount(insertSqlParamList, 500);
            for (int i = 0; i < batchInsertList.size(); i++) {
                List tempList = (ArrayList) batchInsertList.get(i);
                StringBuffer insertSql = new StringBuffer();
                insertSql.append("insert into trandb_field (id,field1,set1,setname1,fieldname1,fieldtype1,codeset1,set2,setname2,field2,");
                insertSql.append("fieldname2,fieldtype2,codeset2,state,uniqueflag) ");
                insertSql.append("values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                dao.batchInsert(insertSql.toString(), tempList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }
    /**
     * 保存代码型指标的代码项对应关系
     * @author wangbs
     * @param codeItemMatchList
     * @param fieldItem1 上级指标编码
     * @return void
     * @throws
     */
    public void saveCodeItemMatch(List codeItemMatchList,String fieldItem1){
        RowSet rs = null;
        List updateSqlParamList = new ArrayList();//存放update语句参数list进行批量处理（效率高）
        List insertSqlParamList = new ArrayList();//存放insert语句参数list进行批量处理（效率高）
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            int schemeId = getSchemeId();//获取trandb_scheme中分布式方案id，
            List sqlParamList = new ArrayList();
            sqlParamList.add(schemeId);
            sqlParamList.add(fieldItem1);
            StringBuffer searchSql = new StringBuffer();
            searchSql.append("select code2 from trandb_code where id=? and fielditem1=?");
            rs = dao.search(searchSql.toString(), sqlParamList);
            while (rs.next()) {
                List oneUpdateSqlParamList = new ArrayList();
                String dbCode2 = rs.getString(1);//下级代码项

                for (int i = 0; i < codeItemMatchList.size(); i++) {
                    Map oneCodeItemMatchMap = PubFunc.DynaBean2Map((MorphDynaBean)codeItemMatchList.get(i));
                    String code2 = (String) oneCodeItemMatchMap.get("code2");
                    String codeSet2 = (String) oneCodeItemMatchMap.get("codeSet2");
                    String codeName2 = (String) oneCodeItemMatchMap.get("codeName2");
                    String code1 = (String) oneCodeItemMatchMap.get("code1");
                    String codeSet1 = (String) oneCodeItemMatchMap.get("codeSet1");
                    String codeName1 = (String) oneCodeItemMatchMap.get("codeName1");

                    if (code2.equals(dbCode2)) {
                        oneUpdateSqlParamList.add(codeSet2);
                        oneUpdateSqlParamList.add(codeName2);
                        oneUpdateSqlParamList.add(code1);
                        oneUpdateSqlParamList.add(codeSet1);
                        oneUpdateSqlParamList.add(codeName1);
                        oneUpdateSqlParamList.add(schemeId);
                        oneUpdateSqlParamList.add(fieldItem1);
                        oneUpdateSqlParamList.add(code2);
                        updateSqlParamList.add(oneUpdateSqlParamList);
                        codeItemMatchList.remove(i);//更新的移除只剩下需要新增的数据
                        break;
                    }
                }
            }
            for (int i = 0; i < codeItemMatchList.size(); i++) {
                List oneInsertSqlParamList = new ArrayList();
                Map oneCodeItemMatchMap = PubFunc.DynaBean2Map((MorphDynaBean)codeItemMatchList.get(i));
                String code2 = (String) oneCodeItemMatchMap.get("code2");
                String codeSet2 = (String) oneCodeItemMatchMap.get("codeSet2");
                String codeName2 = (String) oneCodeItemMatchMap.get("codeName2");
                String code1 = (String) oneCodeItemMatchMap.get("code1");
                String codeSet1 = (String) oneCodeItemMatchMap.get("codeSet1");
                String codeName1 = (String) oneCodeItemMatchMap.get("codeName1");

                oneInsertSqlParamList.add(schemeId);
                oneInsertSqlParamList.add(fieldItem1);
                oneInsertSqlParamList.add(code2);
                oneInsertSqlParamList.add(codeSet2);
                oneInsertSqlParamList.add(codeName2);
                oneInsertSqlParamList.add(code1);
                oneInsertSqlParamList.add(codeSet1);
                oneInsertSqlParamList.add(codeName1);
                insertSqlParamList.add(oneInsertSqlParamList);
            }
            List batchUpdateList = groupListByCount(updateSqlParamList, 500);
            for (int i = 0; i < batchUpdateList.size(); i++) {
                List tempList = (ArrayList) batchUpdateList.get(i);
                String updateSql = "update trandb_code set codeset2=?,codename2=?,code1=?,codeset1=?,codename1=? where id=? and fielditem1=? and code2=?";
                dao.batchUpdate(updateSql, tempList);
            }
            List batchInsertList = groupListByCount(insertSqlParamList, 500);
            for (int i = 0; i < batchInsertList.size(); i++) {
                List tempList = (ArrayList) batchInsertList.get(i);
                String insertSql = "insert into trandb_code (id,fielditem1,code2,codeset2,codename2,code1,codeset1,codename1) values (?,?,?,?,?,?,?,?)";
                dao.batchInsert(insertSql, tempList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 获取分组后的sqllist
     * @author wangbs
     * @param sourceList
     * @param count
     * @return List
     * @throws
     */
    private List groupListByCount(List sourceList, int count) {
        List batchList = new ArrayList();
        if (CollectionUtils.isEmpty(sourceList)) {
            return batchList;
        }

        //拼装批量执行需要的sqlList
        int index = 0;
        while (index < sourceList.size()) {
            batchList.add(new ArrayList(sourceList.subList(index, (index + count) > sourceList.size() ? sourceList.size() : index + count)));
            index += count;
        }
        return batchList;
    }

    /**
     * 获取代码型指标对应
     * @author wangbs
     * @param
     * @return List
     * @throws
     */
    public Map getCodeFieldInfoMap() {
        RowSet rs = null;
        List sqlList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        Map codeFieldInfoMap = new HashMap();
        Map fieldMatchCodeMap = new HashMap();
        Map fieldMatchCodeSetIdMap = new HashMap();
        List codeFieldMatchList = new ArrayList();
        try {
            int schemeId = getSchemeId();
            sqlList.add(schemeId);
            //查询上级代码类指标有对应的指标
            sql.append("select field1,fieldname1,codeset1,field2,fieldname2,codeset2 from trandb_field where id=? and fieldtype2='A' and codeset2 is not null");
            rs = dao.search(sql.toString(), sqlList);
            while (rs.next()) {
                String codeSet2 = rs.getString("codeset2");
                if ("UN".equals(codeSet2) || "UM".equals(codeSet2) || "@K".equals(codeSet2)||StringUtils.isBlank(codeSet2)) {
                    continue;
                }
                List oneField = new ArrayList();
                String codeSet1 = rs.getString("codeset1");
                String field1 = rs.getString("field1");
                String field2 = rs.getString("field2");
                String fieldName1 = rs.getString("fieldname1");
                String fieldName2 = rs.getString("fieldname2");

                oneField.add(field1 + ":" + fieldName1 + "--" + field2 + ":" + fieldName2);
                oneField.add(field1 + ":" + fieldName1 + "--" + field2 + ":" + fieldName2);
                codeFieldMatchList.add(oneField);

                ArrayList codeItemList = AdminCode.getCodeItemList(codeSet2);//获取代码类的所有代码项
                List ruleCodeItemList = new ArrayList();
                for (int i = 0; i < codeItemList.size(); i++) {
                    Map oneCodeItemMap = new HashMap();
                    CodeItem oneCodeItem = (CodeItem) codeItemList.get(i);
                    oneCodeItemMap.put("codeItem",oneCodeItem.getCodeitem());
                    oneCodeItemMap.put("codeName",oneCodeItem.getCodename());
                    ruleCodeItemList.add(oneCodeItemMap);
                }
                fieldMatchCodeMap.put(field2, ruleCodeItemList);
                fieldMatchCodeSetIdMap.put(field1, codeSet1 +":"+ codeSet2);
            }
            codeFieldInfoMap.put("codeFieldMatchList", codeFieldMatchList);
            codeFieldInfoMap.put("fieldMatchCodeMap", fieldMatchCodeMap);
            codeFieldInfoMap.put("fieldMatchCodeSetIdMap", fieldMatchCodeSetIdMap);

            List codeMatchList = getCodeMatchList();
            codeFieldInfoMap.put("codeMatchList", codeMatchList);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return codeFieldInfoMap;
    }
    /**
     * 获取代码项对应关系
     * @author wangbs
     * @param
     * @return List
     * @throws
     */
    private List getCodeMatchList() {
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        List sqlParamList = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        List codeMatchList = new ArrayList();
        try {
            int schemeId = getSchemeId();
            sqlParamList.add(schemeId);
            sql.append("select code1,codename1,codeset2,code2,fielditem1 from trandb_code where id=?");
            rs = dao.search(sql.toString(), sqlParamList);
            while (rs.next()) {
                Map oneCodeMatchMap = new HashMap();
                String code1 = rs.getString(1);
                String codeName1 = rs.getString(2);
                String codeSet2 = rs.getString(3);
                String code2 = rs.getString(4);
                String fieldItem1 = rs.getString(5);

                oneCodeMatchMap.put("code1", code1);
                oneCodeMatchMap.put("codeName1", codeName1);
                oneCodeMatchMap.put("codeSet2", codeSet2);
                oneCodeMatchMap.put("code2", code2);
                oneCodeMatchMap.put("fieldItem1", fieldItem1);
                codeMatchList.add(oneCodeMatchMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return codeMatchList;
    }
    /**
     * 获取上下级信息集的对应关系
     * @author wangbs
     * @param
     * @return List
     * @throws
     */
    public List getSetMatchList(){
        List list = new ArrayList();
        List setMatchList = new ArrayList();
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try{
            int id = getSchemeId();
            list.add(id);
            StringBuffer sql = new StringBuffer();
            sql.append("select set1,name1,set2,name2,filterc.c_expr from trandb_set ");
            sql.append("left join t_sys_asyn_filtercondition filterc on filterc.setid = trandb_set.set1 ");
            sql.append("where trandb_set.id=? order by trandb_set.set1");
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                Map oneSetMatchMap = new HashMap();

                String set1 =  rs.getString("set1");
                String name1 =  rs.getString("name1");
                String set2 =  rs.getString("set2");
                String name2 =  rs.getString("name2");
                String c_expr =  rs.getString("c_expr");

                oneSetMatchMap.put("set_id", set1);
                oneSetMatchMap.put("set_name", name1);
                if(StringUtils.isNotBlank(set2)){
                    oneSetMatchMap.put("matchSet", set2 + ":" + name2);//匹配的下级子集
                }else{
                    oneSetMatchMap.put("matchSet", "");
                }

                if (StringUtils.isBlank(c_expr)) {
                    oneSetMatchMap.put("filterIcon", "0");
                } else {
                    oneSetMatchMap.put("filterIcon", "1");
                }

                setMatchList.add(oneSetMatchMap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return setMatchList;
    }
    /**
     * 获取trandb_scheme中分布式方案id,没有则插入一条
     * @author wangbs
     * @param
     * @return int
     * @throws
     */
    private int getSchemeId(){
        int schemeId = 1;
        RowSet rs = null;
        List list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        try{
            rs = dao.search("select id from trandb_scheme where dbtype=1000");
            if (rs.next()) {//有则取出id，没有则新增一条记录
                schemeId = rs.getInt(1);
            } else {
                rs = dao.search("select max(id) from trandb_scheme");
                if(rs.next()){//最大id加1生成新的id
                    schemeId = rs.getInt(1) + 1;
                }
                int dbType = 1000;
                String name = ResourceFactory.getProperty("dr_report.distributename");
                list.add(dbType);
                list.add(schemeId);
                list.add(name);
                dao.insert("insert into trandb_scheme (dbtype, id,name) values (?,?,?)",list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return schemeId;
    }
    /**
     * 获取信息集和人员试图
     * @author wangbs
     * @return List
     */
    public List getFieldSetList (){
        List fieldSetList = new ArrayList();
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try{
            ArrayList allFieldSetList = DataDictionary.getFieldSetList(1, 0);
            //多媒体子集
            String mediaSet = "A00,B00,K00";
            for (int i = 0; i < allFieldSetList.size(); i++) {
                FieldSet oneFieldSet = (FieldSet) allFieldSetList.get(i);

                String setId = oneFieldSet.getFieldsetid();
                if (mediaSet.indexOf(setId) > -1) {
                    //过滤掉多媒体子集
                    continue;
                }
                String setDesc = oneFieldSet.getFieldsetdesc();

                List oneSet = new ArrayList();
                oneSet.add(setId + ":" + setDesc);
                oneSet.add(setId + ":" + setDesc);
                fieldSetList.add(oneSet);
            }
            //获取人员视图
            String sql = "select fieldsetid,fieldsetdesc from t_hr_busitable where id='50'";
            rowSet = dao.search(sql);
            while(rowSet.next()){
                String setId = rowSet.getString(1);
                String setDesc = rowSet.getString(2);

                List oneSet = new ArrayList();
                oneSet.add(setId + ":" + setDesc);
                oneSet.add(setId + ":" + setDesc);
                fieldSetList.add(oneSet);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rowSet);
        }
        return fieldSetList;
    }
    /**
     * 获取某信息集的所有使用中的指标的相关信息
     * @author wangbs
     * @param setMatchMap 上下级信息集对应
     * @return List
     * @throws
     */
    public Map getFieldInfoMap (Map<String,String> setMatchMap){
        RowSet rs = null;
        List sqlParamList = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        Map returnMap = new HashMap();//所有信息集的指标对应关系
        try{
            for (String set1 : setMatchMap.keySet()) {
                sqlParamList.clear();
                Map oneSetReturnMap = new HashMap();
                List fieldList = new ArrayList();//选择指标的combo的数据源
                Map fieldTypeMap = new HashMap();//指标编码与类型对应关系
                Map fieldLengthMap = new HashMap();//指标编码与长度对应关系
                Map fieldCodeSetIdMap = new HashMap();//指标编码与代码类对应关系
                Map fieldMatchMap = new HashMap();//上下级指标对应关系

                String set2 = setMatchMap.get(set1).split(":")[0];//下级信息集id
                //拿到下级某信息集指标
                ArrayList fieldInfoList = DataDictionary.getFieldList(set2, Constant.USED_FIELD_SET);
                if (CollectionUtils.isNotEmpty(fieldInfoList)) {
                    for (int i = 0; i < fieldInfoList.size(); i++) {
                        FieldItem fieldItem = (FieldItem) fieldInfoList.get(i);
                        String itemId = fieldItem.getItemid().toUpperCase();
                        String itemDesc = fieldItem.getItemdesc();
                        String itemType = fieldItem.getItemtype();
                        String itemCodeSetId = fieldItem.getCodesetid();
                        int itemLength = fieldItem.getItemlength();
                        List oneItem = new ArrayList();
                        oneItem.add(itemId + ":" + itemDesc);
                        oneItem.add(itemId + ":" + itemDesc);
                        fieldList.add(oneItem);
                        fieldTypeMap.put(itemId, itemType);
                        if ("N".equalsIgnoreCase(itemType)) {//数值型的整数长度和小数长度前台都要比较
                            int decimalWidth = fieldItem.getDecimalwidth();
                            fieldLengthMap.put(itemId, itemLength + "`" + decimalWidth);
                        } else {
                            fieldLengthMap.put(itemId, itemLength + "`");
                        }

                        if("0".equals(itemCodeSetId)){
                            itemCodeSetId = "";
                        }
                        fieldCodeSetIdMap.put(itemId, itemCodeSetId);
                    }
                }
                oneSetReturnMap.put("fieldList", fieldList);
                oneSetReturnMap.put("fieldTypeMap", fieldTypeMap);
                oneSetReturnMap.put("fieldLengthMap", fieldLengthMap);
                oneSetReturnMap.put("fieldCodeSetIdMap", fieldCodeSetIdMap);

                int schemeId = getSchemeId();
                sqlParamList.add(schemeId);
                sqlParamList.add(set1);
                String sql = "select field1,field2,fieldname2 from trandb_field where id=? and set1=?";
                rs = dao.search(sql, sqlParamList);
                while (rs.next()) {
                    String field1 = rs.getString("field1");
                    String field2 = rs.getString("field2");
                    String fieldName2 = rs.getString("fieldname2");
                    if (StringUtils.isNotBlank(field2)) {
                        fieldMatchMap.put(field1, field2 + ":" + fieldName2);
                    }
                }
                oneSetReturnMap.put("fieldMatchMap", fieldMatchMap);
                returnMap.put(set1, oneSetReturnMap);//前台应该根据上级信息集id取指标
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return returnMap;
    }
    /**
     * 获取人员库
     * @author wangbs
     * @param
     * @return Map
     * @throws
     */
    public Map getDbInfoMap() {
        Map returnMap = new HashMap();
        List dbList = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            String sql = "select pre,dbname from dbname";
            rs = dao.search(sql);
            while (rs.next()) {
                List oneDbList = new ArrayList();
                String dbPre = rs.getString(1);
                String dbName = rs.getString(2);
                oneDbList.add(dbPre);
                oneDbList.add(dbName);
                dbList.add(oneDbList);
            }

            RecordVo vo = ConstantParamter.getRealConstantVo(DrConstant.BS_ASYN_PARAM_C);
            String strValue = vo.getString("str_value");
            Map basicMap = customJsonTranMap(strValue);
            String nbase = (String) basicMap.get("nbase");

            returnMap.put("dbList", dbList);
            returnMap.put("nbase", nbase);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return returnMap;
    }

    /**
     * 保存上报人员库
     * @author wangbs
     * @param nbase
     * @return void
     * @throws
     */
    public void saveNbaseMatch(String nbase) {
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            RecordVo vo = ConstantParamter.getRealConstantVo(DrConstant.BS_ASYN_PARAM_C);
            String strValue = vo.getString("str_value");
            Map basicMap = customJsonTranMap(strValue);
            basicMap.put("nbase", nbase);
            String basicStr = JSONObject.fromObject(basicMap).toString();

            RecordVo operaVo = new RecordVo("constant");
            operaVo.setString("constant", DrConstant.BS_ASYN_PARAM_C);
            operaVo.setString("str_value", basicStr);
            dao.updateValueObject(operaVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存校验条件
     * @author wangbs
     * @param verifyList
     * @return void
     * @throws
     */
    public void saveVerifyRules(List verifyList) {
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        RecordVo vo = new RecordVo("t_sys_asyn_validaterules");
        String sql = "select max(checkid) from t_sys_asyn_validaterules";
        try {
            for (int i = 0; i < verifyList.size(); i++) {
                Map oneVerifyMap = PubFunc.DynaBean2Map((MorphDynaBean)verifyList.get(i));
                String vfName = (String) oneVerifyMap.get("vfname");
                String vfMenus = (String) oneVerifyMap.get("vfmenus");
                String vfCond = (String) oneVerifyMap.get("vfcond");
                //还原特殊字符
                vfCond = PubFunc.keyWord_reback(vfCond);

                int vfForcestate = "0".equals(oneVerifyMap.get("vfforcestate")) ? 0 : 1;
                int vfValid = "0".equals(oneVerifyMap.get("vfvalid")) ? 0 : 1;

                int vfId = 0;
                rs = dao.search(sql);
                if (rs.next()) {
                    vfId = rs.getInt(1) + 1;
                }
                vo.setInt("checkid",vfId);
                vo.setString("checkname",vfName);
                vo.setString("checkfield",vfMenus);
                vo.setString("condition",vfCond);
                vo.setInt("forcestate",vfForcestate);
                vo.setInt("valid",vfValid);
                vo.setInt("belong",1);
                dao.addValueObject(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 获取上报数据表列表
     * @return
     */
    public ArrayList getTableList() throws GeneralException {
        ArrayList tableList = new ArrayList();
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            sql.append("select set1,set2 from TRANDB_SET tf,trandb_scheme ts where TS.id = TF.id and TS.DBTYPE = 1000 and TF.set2 is not null ");
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                sql.append(" and TF.set2 <> '' ");
            }
            rs = dao.search(sql.toString()+" ORDER BY SET1 ");
            while (rs.next()) {
                Map<String, String> map = new HashMap<String, String>();
                String set1 = rs.getString("set1");
                String set2 = rs.getString("set2");
                boolean setExist = checkSetExist(set2);
                if(setExist){
                    map.put("set1",set1);
                    map.put("set2",set2);
                    tableList.add(map);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return tableList;
    }

    /**
     * 判断子集的指标是否对应
     * @param set2
     * @return
     */
    private boolean checkSetExist(String set2) {
        boolean exist = true;
        RowSet rs = null;
        int count = 0;
        StringBuffer sql = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            sql.append("select count(FIELD2) count from trandb_field tf,trandb_scheme ts WHERE TS.id = TF.id and TS.DBTYPE = 1000 and tf.set2 = '");
            sql.append(set2);
            sql.append("' and field2 is not null ");
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                sql.append(" and TF.field2 <> '' ");
            }
            rs = dao.search(sql.toString());
            if(rs.next()){
                count = rs.getInt("count");
            }
            if(count == 0 ){
                exist = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return exist;
    }

    /**
     * 获取对应子集列表
     * @return
     * @param drLogger
     */
    public ArrayList getMappingList(DrLogger drLogger) throws GeneralException {
        ArrayList tableList = new ArrayList();
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            sql.append("select DISTINCT(set1) from trandb_field tf,trandb_scheme ts where TS.id = TF.id and TS.DBTYPE = 1000 and TF.field2 is not null ");
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                sql.append(" and TF.field2 <> '' ");
            }
            rs = dao.search(sql.toString()+" ORDER BY SET1 ");
            while (rs.next()) {
                String set1 = rs.getString("set1");
                tableList.add(set1);
            }
            drLogger.write("分布同步：获取对应子集列表成功！");
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return tableList;
    }

    /**
     * 创建视图
     * @param codesetid
     */
    public void createViewOrg(String codesetid,String column) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            DbWizard dbWizard = new DbWizard(this.conn);
            String firstCodesetid = codesetid.substring(0, 1).toUpperCase();//B K
            if (dbWizard.isExistTable("V_ASYN_"+codesetid, false)) {
                dropView("V_ASYN_"+codesetid);
            }
            sql.append("create view V_ASYN_"+codesetid+" as ");
            sql.append(" select S_ASYN_ORG_JZ.guidkey AS "+firstCodesetid);
            sql.append("ID ,MODTIME,CODESETID,CODEITEMID,CODEITEMDESC,PARENTID,START_DATE,END_DATE,A0000,GRADE,'' as status,modstate, ");
            sql.append("CODEITEMID as "+column);
            sql.append(" from S_ASYN_ORG_JZ ");
            if("B".equalsIgnoreCase(firstCodesetid)){
                sql.append(" where CODESETID <> '@K' ");
            }else{
                sql.append(" where CODESETID = '@K' ");
            }
            dao.update(sql.toString());
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 删除视图
     * @param tablename String 视图名称
     */
    public void dropView(String tablename) {
        DbWizard dbWizard = new DbWizard(this.conn);
        if (dbWizard.isExistTable(tablename, false)) {
            String deleteSQL = "drop view " + tablename ;
            ContentDAO dao = new ContentDAO(this.conn);
            try {
                dao.update(deleteSQL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写入组织机构数据到中间库
     * @param orgType
     * @param locorgCodeList
     * @param unitcode
     * @param drLogger
     * @param dataStartTime
     * @param schemeType
     */
    public void insertOrgToJZTable(String orgType,List<String> locorgCodeList, String unitcode, DrLogger drLogger, String dataStartTime, String schemeType) throws GeneralException {
        StringBuffer updateOrgSql = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                updateOrgSql.append("UPDATE S_ASYN_ORG_JZ SET codesetid = b.codesetid,codeitemid = b.codeitemid,codeitemdesc = b.codeitemdesc,parentid = b.parentid,start_date = b.start_date,modstate = 2,");
                updateOrgSql.append(" end_date = b.end_date,A0000 = b.A0000,grade = b.grade ,modtime=b.modtime from (SELECT codesetid,codeitemid,codeitemdesc,parentid,");
                updateOrgSql.append(" start_date,end_date,A0000,grade,b01.modtime,organization.guidkey FROM ");
                updateOrgSql.append(" organization LEFT JOIN b01 ON organization.codeitemid = b01.b0110) b where b.guidkey = S_ASYN_ORG_JZ.guidkey ");
                updateOrgSql.append(" AND b.CODESETID IN ('UN','UM') AND b.modtime > S_ASYN_ORG_JZ.modtime ");
                dao.update(updateOrgSql.toString());
                drLogger.write("分布同步：更新组织机构变动数据到S_ASYN_ORG_JZ基准表成功！");
                String updateKsql = updateOrgSql.toString().replaceAll("b0110", "e01a1").replaceAll("b01", "k01").replaceAll("'UN','UM'", "'@K'");
                dao.update(updateKsql);
                drLogger.write("分布同步：更新岗位变动数据到S_ASYN_ORG_JZ基准表成功！");
            }else {
                updateOrgSql.append("UPDATE S_ASYN_ORG_JZ SET (codesetid,codeitemid,codeitemdesc,parentid,start_date,end_date,A0000,grade,modtime,modstate) = ");
                updateOrgSql.append(" ( SELECT codesetid,codeitemid, codeitemdesc,parentid,start_date,end_date,A0000,grade,b01.modtime,'2' FROM organization ");
                updateOrgSql.append(" LEFT JOIN b01 on organization.codeitemid = b01.b0110 ");
                updateOrgSql.append(" WHERE organization.guidkey = S_ASYN_ORG_JZ.guidkey and CODESETID IN ('UN','UM') and b01.modtime > S_ASYN_ORG_JZ.modtime )");
                updateOrgSql.append(" where EXISTS ( SELECT codesetid,codeitemid, codeitemdesc,parentid,start_date,end_date,A0000,grade,b01.modtime,'2' FROM organization ");
                updateOrgSql.append(" LEFT JOIN b01 on organization.codeitemid = b01.b0110 ");
                updateOrgSql.append(" WHERE organization.guidkey = S_ASYN_ORG_JZ.guidkey and CODESETID IN ('UN','UM') and b01.modtime > S_ASYN_ORG_JZ.modtime )");
                dao.update(updateOrgSql.toString());
                drLogger.write("分布同步：更新组织机构变动数据到S_ASYN_ORG_JZ基准表成功！");
                String updateKsql = updateOrgSql.toString().replaceAll("b0110", "e01a1").replaceAll("b01", "k01").replaceAll("'UN','UM'", "'@K'");
                dao.update(updateKsql);
                drLogger.write("分布同步：更新岗位变动数据到S_ASYN_ORG_JZ基准表成功！");
            }
            // 2为增量同步
            String whereSql ="";
            if("2".equalsIgnoreCase(schemeType)){
                whereSql = " and "+Sql_switcher.dateToChar("modtime", "yyyy-MM-dd hh24:mi")+">='"+dataStartTime+"' ";
            }
            for (int i = 0; i <locorgCodeList.size() ; i++) {
                StringBuffer insertOrgSql = new StringBuffer();
                ArrayList valueList = new ArrayList();
                String locorgCode = locorgCodeList.get(i);
                insertOrgSql.append("INSERT INTO S_ASYN_ORG_JZ (guidkey,codesetid,codeitemid,codeitemdesc,parentid,start_date,end_date,A0000,grade,modtime,modstate) ");
                insertOrgSql.append(" select organization.guidkey,codesetid,codeitemid,codeitemdesc,parentid,start_date,end_date,A0000,grade,b01.MODTIME,1 as modstate from organization ");
                insertOrgSql.append(" LEFT JOIN b01 on organization.codeitemid = b01.b0110 ");
                insertOrgSql.append(" where organization.guidkey NOT IN ( SELECT GUIDKEY from S_ASYN_ORG_JZ ) and CODESETID in ('UN','UM') AND codeitemid LIKE '");
                insertOrgSql.append(locorgCode);
                insertOrgSql.append("%'");
                if("1".equals(orgType)){
                    insertOrgSql.append(" and codeitemid <> '");
                    insertOrgSql.append(locorgCode);
                    insertOrgSql.append("'");
                }
                String insertKsql = insertOrgSql.toString().replaceAll("b0110","e01a1").replaceAll("b01","k01").replaceAll("'UN','UM'","'@K'");
                dao.update(insertOrgSql.toString()); //插入organization表数据到基准表
                drLogger.write("分布同步：插入"+locorgCode+"组织机构数据到S_ASYN_ORG_JZ基准表成功！");
                dao.update(insertKsql);
                drLogger.write("分布同步：插入"+locorgCode+"岗位数据到S_ASYN_ORG_JZ基准表成功！");
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    if("1".equals(orgType)){
                        valueList.add(unitcode);
                        valueList.add(locorgCode.length()+1);
                        valueList.add(unitcode);
                        valueList.add(locorgCode.length()+1);
                        dao.update("update S_ASYN_ORG_JZ set codeitemid = ? + substring(codeitemid,?,LEN(codeitemid)), parentid = ? + substring(parentid,?,LEN(parentid)) where codeitemid LIKE '"+locorgCode+"%' " + whereSql,valueList);
                        drLogger.write("分布同步：单机构处理S_ASYN_ORG_JZ基准表codeitemid和parentid字段成功！");
                    }else {
                        valueList.add(unitcode);
                        valueList.add(unitcode);
                        dao.update("update S_ASYN_ORG_JZ set codeitemid = ? + codeitemid,parentid = ? + parentid where codeitemid LIKE '"+locorgCode+"%' " + whereSql,valueList);
                        drLogger.write("分布同步：多机构处理S_ASYN_ORG_JZ基准表codeitemid和parentid字段成功！");
                    }
                } else {
                    if("1".equals(orgType)){
                        valueList.add(unitcode);
                        valueList.add(locorgCode.length()+1);
                        valueList.add(unitcode);
                        valueList.add(locorgCode.length()+1);
                        dao.update("update S_ASYN_ORG_JZ set codeitemid = ? || substr(codeitemid,?,LENGTH(codeitemid)), parentid = ? || substr(parentid,?,LENGTH(parentid)) where codeitemid like '"+locorgCode+"%' "+ whereSql,valueList);
                        drLogger.write("分布同步：单机构处理S_ASYN_ORG_JZ基准表codeitemid和parentid字段成功！");
                    }else {
                        valueList.add(unitcode);
                        valueList.add(unitcode);
                        dao.update("update S_ASYN_ORG_JZ set codeitemid = ? || codeitemid，parentid = ? || parentid where codeitemid like '"+locorgCode+"%' "+ whereSql,valueList);
                        drLogger.write("分布同步：多机构处理S_ASYN_ORG_JZ基准表codeitemid和parentid字段成功！");
                    }
                }
                if("2".equals(orgType)){
                    valueList.clear();
                    valueList.add(unitcode);
                    valueList.add(unitcode+locorgCode);
                    dao.update("update S_ASYN_ORG_JZ set parentid = ? where guidkey in (select guidkey from S_ASYN_ORG_JZ where codeitemid = ?) "+ whereSql,valueList);//更新选中顶级parentid
                    drLogger.write("分布同步：多机构处理S_ASYN_ORG_JZ基准表选中顶级parentid字段成功！");
                }
            }
            if("2".equalsIgnoreCase(schemeType)){//增量时判断删除数据
                deleteBKVDataToJZTable("ORG","ORGANIZATION",drLogger,dataStartTime);
            }
        }catch (Exception e){
            drLogger.write("分布同步：同步组织机构到基准表失败"+e.getMessage());
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 创建组织机构表
     */
    public void createOrgJZTable(DrLogger drLogger) throws GeneralException {
        try{
            DbWizard dbWizard = new DbWizard(this.conn);
            if (!dbWizard.isExistTable("S_ASYN_ORG_JZ", false)) {
                Table table_exg_org = new Table("S_ASYN_ORG_JZ");
                Field temp = new Field("guidkey", "唯一标识码");
                temp.setNullable(false);
                temp.setKeyable(true);
                temp.setDatatype(DataType.STRING);
                temp.setLength(38);
                table_exg_org.addField(temp);

                temp = new Field("codesetid", "组织机构分类");
                temp.setNullable(false);
                temp.setDatatype(DataType.STRING);
                temp.setLength(2);
                table_exg_org.addField(temp);

                temp = new Field("codeitemid", "机构编码");
                temp.setNullable(false);
                temp.setKeyable(false);
                temp.setDatatype(DataType.STRING);
                temp.setLength(50);
                table_exg_org.addField(temp);

                temp = new Field("codeitemdesc", "机构名称");
                temp.setNullable(false);
                temp.setKeyable(false);
                temp.setDatatype(DataType.STRING);
                temp.setLength(50);
                table_exg_org.addField(temp);

                temp = new Field("parentid", "上级机构编码");
                temp.setNullable(false);
                temp.setKeyable(false);
                temp.setDatatype(DataType.STRING);
                temp.setLength(50);
                table_exg_org.addField(temp);

                temp = new Field("start_date", "机构有效日期");
                temp.setNullable(true);
                temp.setKeyable(false);
                temp.setDatatype(DataType.DATE);
                table_exg_org.addField(temp);

                temp = new Field("end_date", "机构失效日期");
                temp.setNullable(true);
                temp.setKeyable(false);
                temp.setDatatype(DataType.DATE);
                table_exg_org.addField(temp);

                temp  = new Field("modtime", "更新时间");
                temp.setDatatype(DataType.DATE);
                temp.setNullable(true);
                table_exg_org.addField(temp);

                temp = new Field("A0000", "机构顺序");
                temp.setNullable(true);
                temp.setKeyable(false);
                temp.setDatatype(DataType.INT);
                table_exg_org.addField(temp);

                temp = new Field("GRADE", "机构层级");
                temp.setNullable(true);
                temp.setKeyable(false);
                temp.setDatatype(DataType.INT);
                table_exg_org.addField(temp);

                temp  = new Field("modstate", "更新状态");//增量判断更新标识 1 为新增，2为更新，3为删除
                temp.setDatatype(DataType.INT);
                temp.setNullable(true);
                temp.setLength(8);
                table_exg_org.addField(temp);
                dbWizard.createTable(table_exg_org);
                drLogger.write("分布同步：创建组织机构基准表S_ASYN_ORG_JZ成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    public int getMaxId(String tableName) {
        int id = 0;
        String sql = "select max(id)+1 as id from " + tableName;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        try {
            rowSet = dao.search(sql);
            if (rowSet.next()) {
                id = rowSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return id;
    }
    /**
     * 更新数据到发送表
     * @param sql
     * @param valueList
     */
    public void updateSendinfo(String sql, List valueList) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            if(valueList.size()> 0 ){
                dao.update(sql, valueList);
            }else{
                dao.update(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取代码对应表中的字段
     *
     * @param setid
     * @param drLogger
     * @return
     */
    public List<Map<String, Object>> getStandardField(String setid, DrLogger drLogger) throws GeneralException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        RowSet rs = null;
        StringBuffer sqls = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            sqls.append("SELECT fieldsetid,itemid,itemdesc,F.itemtype,F.CodeSetId,F.itemlength,F.decimalwidth,TS.Set1 AS sjSetid,TF.Field1 AS sjItemId,TF.CodeSet1 AS sjCodesetId,TF.FieldName1 AS sjItemdesc ");
            sqls.append(" from fielditem F,trandb_set TS,trandb_field TF,trandb_scheme TJ");
            sqls.append(" where useflag = '1' AND TJ.DBTYPE = 1000 AND TS.ID = TJ.ID AND TF.ID = TJ.ID AND F.itemid = TF.Field2 AND TS.Set2 = TF.Set2 ");
            sqls.append(" AND TF.Set1 = '");
            sqls.append(setid + "'");
            rs = dao.search(sqls.toString());
            while (rs.next()) {
                String itemid = rs.getString("itemid");
                String itemtype = rs.getString("itemtype");
                String itemdesc = rs.getString("itemdesc");
                String codeSetId = rs.getString("CodeSetId");
                int itemlength = rs.getInt("itemlength");
                int decimalwidth = rs.getInt("decimalwidth");
                String sjSetid = rs.getString("sjSetid");
                String sjItemId = rs.getString("sjItemId");
                String sjCodesetId = rs.getString("sjCodesetId");
                String sjItemdesc = rs.getString("sjItemdesc");
                Map<String, Object> itemMap = new HashMap<String, Object>();
                if (StringUtils.isEmpty(sjCodesetId)) {//上级指标不为代码型时，基准表创建一个字段
                    itemMap.put("fieldsetid", setid);
                    itemMap.put("itemid", itemid);
                    itemMap.put("itemdesc", itemdesc);
                    itemMap.put("codesetid", codeSetId);
                    itemMap.put("itemtype", itemtype);
                    itemMap.put("itemlength", itemlength);
                    itemMap.put("decimalwidth", decimalwidth);
                } else {
                    itemMap.put("fieldsetid", setid);
                    itemMap.put("itemid", itemid);
                    itemMap.put("itemdesc", itemdesc);
                    itemMap.put("codesetid", codeSetId);
                    itemMap.put("itemtype", itemtype);
                    itemMap.put("itemlength", itemlength);
                    itemMap.put("decimalwidth", decimalwidth);

                    Map<String, Object> itemMapsj = new HashMap<String, Object>();
                    itemMapsj.put("fieldsetid", sjSetid);
                    itemMapsj.put("itemid", sjItemId + "_SJ");
                    itemMap.put("itemdesc", sjItemdesc);
                    itemMapsj.put("codesetid", sjCodesetId);
                    itemMapsj.put("itemtype", itemtype);
                    itemMapsj.put("itemlength", itemlength);
                    itemMapsj.put("decimalwidth", decimalwidth);
                    list.add(itemMapsj);
                }
                list.add(itemMap);
            }
            drLogger.write("分布同步：获取代码对应表"+setid+"对应的数据成功！");
        }catch (Exception e){
            drLogger.write("分布同步：获取代码对应表"+setid+"对应的数据失败！");
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }
    /**
     * 创建或更新基准表
     * @return
     * @param fieldList
     * @param setid
     * @param drLogger
     */
    public String createStandardTable(List<Map<String, Object>> fieldList, String setid, DrLogger drLogger) throws GeneralException {
        DbWizard dbWizard = new DbWizard(this.conn);
        StringBuffer alertColumn = new StringBuffer();
        try {
            String tableName = "S_ASYN_"+setid+"_JZ";
            String firstLetter = setid.substring(0,1).toUpperCase();//A B K
            if (!dbWizard.isExistTable(tableName,false)&& fieldList.size()> 0){//表为空时新建表
                Table table = new Table(tableName);
                //先添加固定指标
                Field field = new Field("GUIDKEY","唯一标识码");
                field.setDatatype(DataType.STRING);
                field.setLength(38);
                field.setNullable(true);
                table.addField(field);

                field  = new Field("modtime", "更新时间");
                field.setDatatype(DataType.DATE);
                field.setNullable(true);
                table.addField(field);

                field  = new Field("createtime", "创建时间");
                field.setDatatype(DataType.DATE);
                field.setNullable(true);
                table.addField(field);

                field  = new Field("status", "状态");//1为需要过滤的数据（guidkey重复，唯一和必填过滤）
                field.setDatatype(DataType.INT);
                field.setNullable(true);
                field.setLength(8);
                table.addField(field);

                field  = new Field("modstate", "更新状态");//增量判断更新标识 1 为新增，2为更新，3为删除
                field.setDatatype(DataType.INT);
                field.setNullable(true);
                field.setLength(8);
                table.addField(field);
                if("A01".equalsIgnoreCase(setid)){ //人员主集
                    field  = new Field("NBase", "人员库");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(3);
                    table.addField(field);

                    field  = new Field("B0110", "机构编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("B0110_SJ", "上级机构编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("E01A1", "岗位编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("E01A1_SJ", "上级岗位编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                }else if(!"A01".equalsIgnoreCase(setid)&&"A".equalsIgnoreCase(firstLetter)){ //人员子集
                    field  = new Field("NBase", "人员库");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(3);
                    table.addField(field);

                    field  = new Field("emp_id", "人员主键");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(false);
                    field.setLength(38);
                    table.addField(field);

                    field  = new Field("i9999", "子集记顺序号");
                    field.setDatatype(DataType.INT);
                    field.setNullable(true);
                    field.setLength(8);
                    table.addField(field);

                }else if("B01".equalsIgnoreCase(setid)){
                    field  = new Field("B0110", "机构编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("B0110_SJ", "上级机构编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);
                }else if(!"B01".equalsIgnoreCase(setid)&&"B".equalsIgnoreCase(firstLetter)){
                    field  = new Field("B0110", "机构编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("B0110_SJ", "上级机构编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("i9999", "子集记顺序号");
                    field.setDatatype(DataType.INT);
                    field.setNullable(false);
                    field.setLength(8);
                    table.addField(field);
                    field  = new Field("org_id", "单位主键");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(false);
                    field.setLength(38);
                    table.addField(field);

                }else if("K01".equalsIgnoreCase(setid)){
                    field  = new Field("E01A1", "岗位编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("E01A1_SJ", "上级岗位编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("E0122", "部门编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("E0122_SJ", "上级部门编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);
                }else if(!"K01".equalsIgnoreCase(setid)&&"K".equalsIgnoreCase(firstLetter)){
                    field  = new Field("E01A1", "岗位编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("E01A1_SJ", "上级岗位编码");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(true);
                    field.setLength(50);
                    table.addField(field);

                    field  = new Field("i9999", "子集记顺序号");
                    field.setDatatype(DataType.INT);
                    field.setNullable(false);
                    field.setLength(8);
                    table.addField(field);

                    field  = new Field("post_id", "人员主键");
                    field.setDatatype(DataType.STRING);
                    field.setNullable(false);
                    field.setLength(38);
                    table.addField(field);

                }
                addFields(fieldList,table);//增加选中的指标
                dbWizard.createTable(table);
                drLogger.write("分布同步：创建基准表"+table+"成功！");
            }else{//如果存在,获得表机构
                ArrayList<String> fixedFieldList = new ArrayList<String>();//把固定指标存入list
                fixedFieldList.add("guidkey");
                fixedFieldList.add("modtime");
                fixedFieldList.add("status");
                fixedFieldList.add("modstate");
                fixedFieldList.add("createtime");
                if ("A01".equalsIgnoreCase(setid)) {//如果是人员主集
                    fixedFieldList.add("Nbase");
                    fixedFieldList.add("B0110");
                    fixedFieldList.add("B0110_SJ");
                    fixedFieldList.add("E01A1");
                    fixedFieldList.add("E01A1_SJ");
                }else if (!"A01".equalsIgnoreCase(setid)&&"A".equals(firstLetter)) {//人员子集
                    fixedFieldList.add("emp_id");
                    fixedFieldList.add("nbase");
                    fixedFieldList.add("i9999");
                }else if ("B01".equalsIgnoreCase(setid)) {//机构主集
                    fixedFieldList.add("B0110");
                    fixedFieldList.add("B0110_SJ");
                }else if (!"B01".equalsIgnoreCase(setid)&&"B".equals(firstLetter)){//机构子集
                    fixedFieldList.add("B0110");
                    fixedFieldList.add("B0110_SJ");
                    fixedFieldList.add("i9999");
                    fixedFieldList.add("org_id");
                }else if ("K01".equalsIgnoreCase(setid)) {
                    fixedFieldList.add("E01A1");
                    fixedFieldList.add("E01A1_SJ");
                }else if (!"K01".equalsIgnoreCase(setid)&&"K".equals(firstLetter)) {
                    fixedFieldList.add("E01A1");
                    fixedFieldList.add("E01A1_SJ");
                    fixedFieldList.add("i9999");
                    fixedFieldList.add("post_id");
                }
                ContentDAO dao=new ContentDAO(conn);
                boolean alertFields=false;//字段的长度改变
                boolean addFields=false;//新增加字段
                boolean dropFields=false;//删除字段
                RowSet rowSet= null;
                try {
                    rowSet=dao.search("select * from "+tableName+" where 1=2");
                    ResultSetMetaData mt=rowSet.getMetaData();
                    Table tableAddcolumn = new Table(tableName);
                    Table tableAlertcolumn = new Table(tableName);
                    for(int j=0;j<fieldList.size();j++) {//遍历定义数据规范添加的指标
                        HashMap<String, Object> map2 = (HashMap<String, Object>) fieldList.get(j);
                        String itemid= (String) map2.get("itemid");
                        String fieldsetid = (String) map2.get("fieldsetid");
                        if (setid.equalsIgnoreCase(fieldsetid)) {
                            boolean result=false;
                            for(int i=0;i<mt.getColumnCount();i++) {
                                String columnName= mt.getColumnName(i+1);
                                if (columnName.equalsIgnoreCase(itemid)) {//如果指标已经在表里，判断指标长度是否一致，如果不一致的话，换成新的
                                    result=true;
                                    int itemlength = (Integer) map2.get("itemlength");
                                    int columnDisplaySize = mt.getColumnDisplaySize(i+1);
                                    if (itemlength>columnDisplaySize) {
                                        alertFields=true;
                                        Field field = new Field(columnName);
                                        String lable = (String) map2.get("itemdesc");
                                        String itemtypeStr = (String) map2.get("itemtype");
                                        int decimalwidth = (Integer) map2.get("decimalwidth");
                                        int itemtype = getItemtype(itemtypeStr,decimalwidth);
                                        field.setLabel(lable);
                                        field.setDatatype(itemtype);
                                        field.setLength(itemlength);
                                        if (decimalwidth>0) {
                                            field.setDecimalDigits(decimalwidth);
                                        }
                                        tableAlertcolumn.addField(field);
                                    }
                                }
                            }
                            if (!result) {
                                addFields=true;
                                String itemtypeStr = (String) map2.get("itemtype");
                                int decimalwidth = (Integer) map2.get("decimalwidth");
                                int itemtype = getItemtype(itemtypeStr,decimalwidth);
                                int itemlength = (Integer) map2.get("itemlength");
                                Field  field  = new Field(itemid, (String)map2.get("itemdesc"));
                                field.setDatatype(itemtype);
                                field.setNullable(true);
                                field.setLength(itemlength);
                                if (decimalwidth>0) {
                                    field.setDecimalDigits(decimalwidth);
                                }
                                if(!itemid.endsWith("SJ")){
                                    alertColumn.append(",");
                                    alertColumn.append(itemid);
                                }
                                tableAddcolumn.addField(field);
                            }
                        }
                    }

                    Table tableDropcolumns = new Table(tableName);
                    for(int i=0;i<mt.getColumnCount();i++) {
                        String columnName= mt.getColumnName(i+1);
                        boolean result = false;
                        for(int j=0;j<fieldList.size();j++) {//遍历定义数据规范添加的指标
                            HashMap<String, Object> map2 = (HashMap<String, Object>) fieldList.get(j);
                            String itemid= (String) map2.get("itemid");
                            String fieldsetid = (String) map2.get("fieldsetid");
                            if (setid.equalsIgnoreCase(fieldsetid)&&itemid.equalsIgnoreCase(columnName)) {
                                result = true;
                            }
                        }
                        for(int k = 0;k<fixedFieldList.size();k++) {
                            String itemid = fixedFieldList.get(k);
                            if (itemid.equalsIgnoreCase(columnName)) {
                                result = true;
                            }
                        }
                        if (!result) {
                            Field field = new Field(columnName);
                            tableDropcolumns.addField(field);
                            dropFields = true;
                        }

                    }
                    if (dropFields) {
                        dbWizard.dropColumns(tableDropcolumns);//删除字段
                    }
                    if (alertFields) {//如果有需要修改的字段
                        dbWizard.alterColumns(tableAlertcolumn);//修改字段
                    }
                    if(!dbWizard.isExistField(tableName,"createtime")){
                        Table tableAddCreateTime = new Table(tableName);
                        Field field  = new Field("createtime", "创建时间");
                        field.setDatatype(DataType.DATE);
                        field.setNullable(true);
                        tableAddCreateTime.addField(field);
                        dbWizard.addColumns(tableAddCreateTime);
                    }
                    if (addFields) {
                        dbWizard.addColumns(tableAddcolumn);//增加字段
                    }
                }catch (GeneralException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    PubFunc.closeResource(rowSet);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            drLogger.write("分布同步：新增或更新字段到S_ASYN_"+setid+"_JZ基准表失败"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
        return alertColumn.toString();
    }

    /**
     * 根据a'd'n'm 返回字符串 itemtype
     * @param itemtypeStr A D　N M
     * @return
     */
    public int getItemtype(String itemtypeStr, int decimalwidth) {
        int itemtype = 0;
        if ("A".equalsIgnoreCase(itemtypeStr)) {//字符或则代码
            itemtype = DataType.STRING;
        }else if ("D".equalsIgnoreCase(itemtypeStr)) {//日期
            itemtype = DataType.DATE;
        }else if ("N".equalsIgnoreCase(itemtypeStr)) {//数值
            if (decimalwidth>0) {
                itemtype = DataType.FLOAT;
            }else {
                itemtype = DataType.INT;
            }
        }else if ("M".equalsIgnoreCase(itemtypeStr)) {//备注
            itemtype = DataType.CLOB;
        }
        return itemtype;
    }

    /**
     * 向表中插入上报字段
     * @param list
     * @param table
     */
    public void addFields(List<Map<String, Object>> list,Table table){
        for(int i=0;i<list.size();i++) {
            HashMap<String, Object> map = (HashMap<String, Object>) list.get(i);
            String itemid= (String) map.get("itemid");
            String itemdesc=(String) map.get("itemdesc");
            String codesetid = (String)map.get("codesetid");
            int itemlength = (Integer) map.get("itemlength");
            String itemtype = (String) map.get("itemtype");
            int decimalwidth = (Integer) map.get("decimalwidth");
            Field field=table.getField(itemid);
            int dataType = 0;
            if ("A".equalsIgnoreCase(itemtype)
                    && !"0".equals(codesetid)) {
                dataType = DataType.STRING;
                itemlength = 200;
            } else if ("D".equalsIgnoreCase(itemtype)) {
                dataType = DataType.DATE;
            } else if ("N".equalsIgnoreCase(itemtype)) {
                if (0==decimalwidth) {
                    dataType = DataType.INT;
                } else {
                    dataType = DataType.FLOAT;
                }
            } else if ("M".equalsIgnoreCase(itemtype)) {
                dataType = DataType.CLOB;
            }
            if (null==field) {//为了避免系统固定指标和选中的指标冲突的问题。
                field  = new Field(itemid, itemdesc);
                field.setNullable(true);
                field.setLength(itemlength);
                field.setDatatype(dataType);
                if (decimalwidth>0) {
                    field.setDecimalDigits(decimalwidth);
                }
                table.addField(field);
            }
        }
    }

    /**
     * 获取上报表查询插入字段
     *
     * @param setid
     * @param drLogger
     * @return
     */
    public String getColumnsSql(String setid, DrLogger drLogger) throws GeneralException {
        StringBuffer columnsSql = new StringBuffer();
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select distinct(field2) from TRANDB_FIELD tf,trandb_scheme ts where TS.id = TF.id and set1 = '" + setid + "' and TS.DBTYPE = 1000 and TF.field2 is not null ");
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                sql.append(" and TF.field2 <> '' ");
            }
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String field2 = rs.getString("field2");
                columnsSql.append(",");
                columnsSql.append(field2);
            }
            drLogger.write("分布同步：获取"+setid+"需要插入的字段成功！");
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return columnsSql.toString();
    }

    /**
     * 获取创建视图字段
     * @param setid
     * @param drLogger
     * @return
     */
    private String getViewColumnsSql(String setid, DrLogger drLogger) throws GeneralException {
        StringBuffer viewColumnsSql = new StringBuffer();
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sql = new StringBuffer("select field1,field2,codeset2 from TRANDB_FIELD tf,trandb_scheme ts where TS.id = TF.id and TS.DBTYPE = 1000 and set1 = '"+setid+"' and TF.field2 is not null ");
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                sql.append(" and TF.field2 <> '' ");
            }
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String field1 = rs.getString("field1");
                String field2 = rs.getString("field2");
                String codeset2 = rs.getString("codeset2");
                viewColumnsSql.append(",");
                if(StringUtils.isNotBlank(codeset2)){ //不为空时，为代码型
                    viewColumnsSql.append(field1+"_SJ AS "+field1);
                }else{
                    if(field1.equalsIgnoreCase(field2)){
                        viewColumnsSql.append(field2);
                    }else {
                        viewColumnsSql.append(field2);
                        viewColumnsSql.append(" AS ");
                        viewColumnsSql.append(field1);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("获取创建视图字段"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return viewColumnsSql.toString();
    }

    /**
     * 创建视图
     * @param setid
     * @param drLogger
     */
    public void createView(String setid, DrLogger drLogger) throws GeneralException{
        StringBuffer sql = new StringBuffer();
        String firstSetid = setid.substring(0, 1).toUpperCase();
        String viewColumnsSql = getViewColumnsSql(setid,drLogger);
        ContentDAO dao = new ContentDAO(conn);
        DbWizard dbWizard = new DbWizard(conn);
        if (dbWizard.isExistTable("V_ASYN_"+setid, false)) {
            dropView("V_ASYN_"+setid);
        }
        if ("A01".equalsIgnoreCase(setid)) {
            sql.append("create view V_ASYN_A01");
            sql.append(" as select guidkey as AID,MODTIME,CREATETIME,status,modstate ");
            sql.append(viewColumnsSql);
            sql.append(" from S_ASYN_A01_JZ ");
        } else if (!"A01".equalsIgnoreCase(setid) && "A".equalsIgnoreCase(firstSetid)) {
            sql.append("create view V_ASYN_");
            sql.append(setid);
            sql.append(" as select guidkey as AID,MODTIME,CREATETIME,I9999,emp_id,status,modstate ");
            sql.append(viewColumnsSql);
            sql.append(" from S_ASYN_" + setid + "_JZ ");
        } else if ("B01".equalsIgnoreCase(setid)) {
            sql.append("create view V_ASYN_B01");
            sql.append(" as select S_ASYN_ORG_JZ.guidkey AS BID,S_ASYN_ORG_JZ.MODTIME,CREATETIME,CODESETID,CODEITEMID,CODEITEMDESC,PARENTID,START_DATE,END_DATE,A0000,GRADE,S_ASYN_ORG_JZ.modstate, ");
            sql.append(Sql_switcher.isnull("B0110_SJ","CODEITEMID")+" AS B0110,status ");
            sql.append(viewColumnsSql);
            sql.append(" from S_ASYN_ORG_JZ left join S_ASYN_B01_JZ on S_ASYN_ORG_JZ.guidkey = S_ASYN_B01_JZ.guidkey where CODESETID <> '@K' ");
        } else if (!"B01".equalsIgnoreCase(setid) && "B".equalsIgnoreCase(firstSetid)) {
            sql.append("create view v_asyn_" + setid);
            sql.append(" as select guidkey AS BID,org_id,B0110_SJ as b0110,modtime,CREATETIME,status,modstate,i9999 ");
            sql.append(viewColumnsSql);
            sql.append(" from S_ASYN_" + setid + "_JZ");
        } else if ("K01".equalsIgnoreCase(setid)) {
            sql.append("create view V_ASYN_K01");
            sql.append(" as select S_ASYN_ORG_JZ.guidkey AS KID,S_ASYN_ORG_JZ.MODTIME,CREATETIME,CODESETID,CODEITEMID,CODEITEMDESC,PARENTID,START_DATE,END_DATE,A0000,GRADE,S_ASYN_ORG_JZ.modstate, ");
            sql.append(Sql_switcher.isnull("E01A1_SJ","CODEITEMID")+" AS E01A1,status ");
            sql.append(viewColumnsSql);
            sql.append(" from S_ASYN_ORG_JZ left join S_ASYN_K01_JZ on S_ASYN_ORG_JZ.guidkey = S_ASYN_K01_JZ.guidkey where CODESETID = '@K' ");
        } else if (!"K01".equalsIgnoreCase(setid) && "K".equalsIgnoreCase(firstSetid)) {
            sql.append("create view v_asyn_" + setid);
            sql.append(" as select guidkey AS KID,post_id,E01A1_SJ as E01A1,modtime,createtime,status,modstate,i9999 ");
            sql.append(viewColumnsSql);
            sql.append(" from S_ASYN_" + setid + "_JZ ");
        }
        try{
            dao.update(sql.toString());
            drLogger.write("分布同步：创建视图表V_ASYN_"+setid+"成功");
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * 增加表的guidkey字段，并且插入guidkey
     * @param setid
     * @param nbase
     * @param drLogger
     */
    public void addGuidKeyFieldAndInsert(String setid, String nbase, DrLogger drLogger) throws GeneralException {
        String sql = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            String tableName = nbase+setid;
            addGuidKeyField(tableName);
            if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                sql = "update "+tableName+" set guidkey = newid() where guidkey is null or guidkey = ''";
            } else if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                sql = "update "+tableName+" set guidkey = sys_guid() where guidkey is null";
            }
            dao.update(sql);
            drLogger.write("分布同步：增加"+nbase+setid+"档案库GUIDKEY成功！");
        } catch (Exception e) {
            e.printStackTrace();
            drLogger.write("分布同步：增加"+nbase+setid+"档案库GUIDKEY失败！"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    private void addGuidKeyField(String tablename) {
        try {
            DbWizard dbWizard = new DbWizard(this.conn);
            if (!dbWizard.isExistField(tablename, "GUIDKEY", false)) {
                Table table = new Table(tablename);
                Field field = new Field("GUIDKEY", "人员唯一标识");
                field.setDatatype(DataType.STRING);
                field.setKeyable(false);
                field.setLength(38);
                table.addField(field);
                dbWizard.addColumns(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新新增字段数据到基准表
     * @param nbase
     * @param set1
     * @param set2
     * @param alertColunm
     * @param drLogger
     */
    public void updateAlertColunm(String nbase, String set1,String set2, String alertColunm, DrLogger drLogger) throws GeneralException {
        try{
            ContentDAO dao = new ContentDAO(conn);
            if(alertColunm != null && alertColunm.length() > 0){
                String srcTab = nbase+set2;
                String destTab = "S_ASYN_"+set1+"_JZ";
                String strJoin = srcTab+".guidkey = "+destTab+".guidkey";
                String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+")";
                String sql = getUpdateColumn(alertColunm,nbase,set2);
                String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, sql.substring(1), strDWhere,"");
                dao.update(updateSql);
                drLogger.write("分布同步：更新"+srcTab+"新增字段数据到"+destTab+"基准表成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：更新新增字段数据到S_ASYN_"+set1+"_JZ基准表失败"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 获取更新字段
     * @param columnsSql
     * @param nbase
     * @param setid
     * @return
     */
    public String getUpdateColumn(String columnsSql, String nbase, String setid) {
        boolean isDameng = Sql_switcher.searchDbServerFlag()==Constant.DAMENG;
        String[] columnsSqlArr = columnsSql.split(",");
        StringBuffer sql = new StringBuffer();
        for (int i = 0; i < columnsSqlArr.length-1; i++) {
            String column = columnsSqlArr[i+1];
            FieldItem item=DataDictionary.getFieldItem(column);
            sql.append("`");
            sql.append(column);
            sql.append(" = ");
            if(isDameng && item!=null && "M".equalsIgnoreCase(item.getItemtype())){
                String char_field_name = Sql_switcher.sqlToChar(nbase+setid+"."+column);
                sql.append(char_field_name);
                sql.append(" ");
                sql.append(column);
            }else{
                sql.append(nbase + setid);
                sql.append(".");
                sql.append(column);
            }
        }
        return sql.toString();
    }
    /**
     * 按照所选单位插入数据到基准表
     * @param nbase
     * @param columnsSql
     * @param set1
     * @param set2
     * @param locorgcodeList
     * @param drLogger
     */
    public void insertDataToJZTable(String nbase, String columnsSql, String set1,String set2, List<String> locorgcodeList, DrLogger drLogger) throws GeneralException {
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            String firstSetid = set1.substring(0, 1).toUpperCase();
            String table = nbase+set2;
            String sql_filter = filterRecordCondition(drLogger,set1);
            for (int i = 0; i < locorgcodeList.size(); i++) {
                StringBuffer sqls = new StringBuffer();
                String locorgcode = locorgcodeList.get(i);
                if("A01".equalsIgnoreCase(set1)){
                    //插入guidkey不在s_asyn_xx_jz的数据
                    sqls.append("INSERT INTO S_ASYN_");
                    sqls.append(set1);
                    sqls.append("_jz (nbase,modstate,guidkey,modtime,createtime ");
                    sqls.append(columnsSql);
                    sqls.append(" ) select '"+nbase+"' as nbase,1 as modstate,guidkey,"+Sql_switcher.isnull("modtime",Sql_switcher.sqlNow())+",createtime ");
                    sqls.append(columnsSql);
                    sqls.append(" from ");
                    sqls.append(table);
                    sqls.append(" where NOT EXISTS (select 1 from s_asyn_");
                    sqls.append(set1+"_jz b where "+table+".guidkey = b.guidkey ) and b0110 like '"+locorgcode+"%' ");
                    if(StringUtils.isNotEmpty(sql_filter)){
                        sqls.append(" and NOT EXISTS ( select 1 from "+table+" a where "+sql_filter+" and a.guidkey = "+table+".guidkey )");
                    }
                }else if(!"A01".equalsIgnoreCase(set1)&&"A".equals(firstSetid)){
                    sqls.append("INSERT INTO S_ASYN_"+set1+"_JZ (");
                    sqls.append(" nbase,modstate,guidkey,modtime,createtime,emp_id,i9999 "+columnsSql);
                    sqls.append(") select '"+nbase+"' as nbase,1 as modstate,"+table+".guidkey,"+Sql_switcher.isnull(table+".modtime",Sql_switcher.sqlNow())+","+table+".createtime,emp_id,"+table+".i9999 ");
                    sqls.append(columnsSql+" from "+table);
                    sqls.append(" LEFT JOIN (select guidkey as emp_id,a0100 from "+nbase+"A01 where b0110 like '"+locorgcode+"%') b  on  "+table+".a0100 = b.a0100 ");
                    sqls.append(" WHERE "+table+".guidkey not in ( select guidkey from s_asyn_");
                    sqls.append(set1+"_jz )  and ( emp_id is not null ");
                    if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                        sqls.append(" or emp_id <> '' ");
                    }
                    sqls.append(" ) ");
                    if(StringUtils.isNotEmpty(filterConditionMap.get("A"))){
                        sqls.append(" and NOT EXISTS ( select 1 from "+nbase+"A01 a where "+filterConditionMap.get("A")+" and a.a0100 = "+table+".a0100 )");
                    }
                    if(StringUtils.isNotEmpty(sql_filter)){
                        //sqls.append(" and NOT EXISTS ( select 1 from "+table+" b where "+sql_filter+" and b.guidkey = "+table+".guidkey )");
                        sqls.append(" and guidkey not in ( select guidkey from "+table+" where "+sql_filter+" ) ");
                    }
                }
                drLogger.write("分布同步：插入"+table+"表"+locorgcode+"单位数据到S_ASYN_"+set1+"_JZ基准表sql->"+sqls.toString());
                dao.update(sqls.toString());
                drLogger.write("分布同步：插入"+table+"表"+locorgcode+"单位数据到S_ASYN_"+set1+"_JZ基准表成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：插入数据到S_ASYN_"+set1+"_JZ基准表失败！"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 比较modtime更新数据到基准表
     * @param nbase
     * @param columnsSql
     * @param set1
     * @param set2
     * @param drLogger
     */
    public void updateDataToJZTable(String nbase, String columnsSql, String set1,String set2,DrLogger drLogger) throws GeneralException {
        String strSet = "";
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            String firstSetid = set1.substring(0, 1).toUpperCase();
            String srcTab = nbase+set2;
            String destTab = "S_ASYN_"+set1+"_JZ";
            String strJoin = srcTab+".guidkey = "+destTab+".guidkey";
            String strSWhere = srcTab+".modtime > "+destTab+".modtime and "+srcTab+".modtime is not null ";
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                strSWhere=strSWhere+" and "+srcTab+".modtime <> '' ";
            }
            String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
            String updateColumn = getUpdateColumn(columnsSql,nbase,set2);
            if("A01".equalsIgnoreCase(set1)){
                strSet = destTab+".status=''`"+destTab+".nbase='"+nbase+"'`"+destTab+".modstate = 2`"+destTab+".modtime="+srcTab+".modtime`"+destTab+".createtime="+srcTab+".createtime"+updateColumn;
            }else if(!"A01".equalsIgnoreCase(set1)&&"A".equals(firstSetid)){
                strSet = destTab+".status=''`"+destTab+".nbase='"+nbase+"'`"+destTab+".modstate = 2`"+destTab+".modtime="+srcTab+".modtime`"+destTab+".createtime="+srcTab+".createtime`"+destTab+".i9999="+srcTab+".i9999"+updateColumn;
            }
            String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
            drLogger.write("分布同步：更新"+srcTab+"表数据到"+destTab+"基准表sql="+updateSql);
            dao.update(updateSql);
            drLogger.write("分布同步：更新"+srcTab+"表数据到"+destTab+"基准表成功！");
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：更新数据到基准表失败！"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 过滤重复的数据
     * @param TableList
     * @param drLogger
     * @param uuid
     * @param psn_status
     */
    public void filterPsnStatusData(ArrayList<Map<String, String>> TableList, DrLogger drLogger, String uuid, String psn_status) throws GeneralException {
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            HashMap<String,String> fieldMapping = getFieldMapping("A01");
            String psn_status_xj = fieldMapping.get(psn_status);
            sql.append("select aid as id,a0101 as name from V_ASYN_A01 where ");
            sql.append(psn_status);
            sql.append(" is null ");
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                sql.append(" AND ");
                sql.append(psn_status);
                sql.append(" = '' ");
            }
            drLogger.write("分布上报：查询人员库对应指标为空的数据"+sql.toString());
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String guidkey = rs.getString("id");
                String name = rs.getString("name");
                ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("itemid", psn_status);
                map.put("value", "");
                map.put("reason","下级机构人员库对应指标"+psn_status_xj+"为空");
                list1.add(map);
                ArrayList<Object> listObject = new ArrayList<Object>();
                listObject.add("A");
                listObject.add("A01");
                listObject.add(name);
                listObject.add(guidkey);
                listObject.add(list1);
                list.add(listObject);
            }
            if(list.size()> 0 ){
                saveRecordTable(list, uuid);//保存记录到日志表
                for (Map<String, String> map : TableList) {
                    String tableSet1 = (String) map.get("set1");
                    String firstTable = tableSet1.substring(0, 1).toUpperCase();
                    if(!"A01".equalsIgnoreCase(tableSet1) && "A".equalsIgnoreCase(firstTable)){
                        drLogger.write("分布上报：删除人员库对应指标为空的数据"+tableSet1);
                        dao.update("delete from S_ASYN_"+tableSet1+"_JZ where emp_id in (select aid from V_ASYN_A01 where "+psn_status+" is null or "+psn_status+" = '')");
                    }
                }
                dao.update("delete from S_ASYN_A01_JZ where guidkey in (select aid from V_ASYN_A01 where "+psn_status+" is null or "+psn_status+" = '')");
                drLogger.write("分布同步：删除基准表人员库对应指标为空的数据成功！");
            }
        }catch (Exception e){
            drLogger.write("分布同步：删除基准表人员库对应指标为空数据失败！"+e.getMessage());
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }
    /**
     * bk子集插入基准表数据
     * @param columnsSql
     * @param set1
     * @param set2
     * @param locorgcodeList
     * @param orgType
     * @param drLogger
     */
    public void insertSubsetBKDataToJZTable(String columnsSql, String set1,String set2, List<String> locorgcodeList,String orgType, DrLogger drLogger) throws GeneralException {
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            String updateColumn = getUpdateColumn(columnsSql,"",set2);
            String sql_filter = filterRecordCondition(drLogger,set1);
            String firstSet = set1.substring(0, 1).toUpperCase();
            for (int i = 0; i < locorgcodeList.size(); i++) {
                StringBuffer insertsql = new StringBuffer();
                String locorgcode = locorgcodeList.get(i);
                insertsql.append("INSERT INTO S_ASYN_"+set1+"_JZ (");
                insertsql.append(" column,modtime,createtime,guidkey,i9999,modstate,subsetid "+columnsSql);
                insertsql.append(") select column,"+Sql_switcher.isnull("modtime",Sql_switcher.sqlNow())+",createtime,"+set2+".guidkey,i9999,1 as modstate,ORGANIZATION.guidkey as subsetid ");
                insertsql.append(columnsSql);
                insertsql.append(" from "+set2+" left join organization on column = organization.codeitemid ");
                insertsql.append(" where "+set2+".guidkey not in (select guidkey from s_asyn_"+set1+"_jz ) ");
                insertsql.append(" and column like '"+locorgcode+"%' ");
                if("1".equals(orgType)){
                    insertsql.append(" and column <> '");
                    insertsql.append(locorgcode);
                    insertsql.append("'");
                }
                if(StringUtils.isNotEmpty(filterConditionMap.get(firstSet))){
                    insertsql.append(" and NOT EXISTS ( select 1 from "+firstSet+"01 b where "+filterConditionMap.get(firstSet)+" and b.column = "+set2+".column )");
                }
                if(StringUtils.isNotEmpty(sql_filter)){
                    insertsql.append(" and NOT EXISTS ( select 1 from "+set2+" a where "+sql_filter+" and a.column= "+set2+".column )");
                }
                if(set1.contains("B")){
                    dao.update(insertsql.toString().replaceAll("column","b0110").replaceAll("subsetid","org_id"));
                    drLogger.write("分布同步："+set2+"子集插入S_ASYN_"+set1+"_JZ基准表数据成功！");
                }else if(set1.contains("K")){
                    dao.update(insertsql.toString().replaceAll("column","e01a1").replaceAll("subsetid","post_id"));
                    drLogger.write("分布同步："+set2+"子集插入S_ASYN_"+set1+"_JZ基准表数据成功！");
                }
            }
            String srcTab = set2;
            String destTab = "S_ASYN_"+set1+"_JZ";
            String strJoin = srcTab+".guidkey = "+destTab+".guidkey";
            String strSWhere = srcTab+".modtime > "+destTab+".modtime";
            String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
            String strSet = destTab+".COLUMN="+srcTab+".COLUMN`"+destTab+".modstate= 2`"+destTab+".modtime="+srcTab+".modtime`"+destTab+".createtime="+srcTab+".createtime`"+destTab+".i9999="+srcTab+".i9999"+updateColumn;
            String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
            if(set1.contains("B")){
                dao.update(updateSql.replaceAll("COLUMN","b0110"));
                drLogger.write("分布同步："+set2+"子集更新S_ASYN_"+set1+"_JZ基准表数据成功！");
            }else if(set1.contains("K")){
                dao.update(updateSql.replaceAll("COLUMN","e01a1"));
                drLogger.write("分布同步："+set2+"子集更新S_ASYN_"+set1+"_JZ基准表数据成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步："+set2+"子集插入S_ASYN_"+set1+"_JZ基准表数据失败"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * bk主集插入基准表数据
     * @param columnsSql
     * @param set1
     * @param set2
     * @param locorgcodeList
     * @param orgType
     * @param drLogger
     */
    public void insertBKDataToJZTable(String columnsSql, String set1,String set2, List<String> locorgcodeList,String orgType, DrLogger drLogger) throws GeneralException {
        try{
            ContentDAO dao = new ContentDAO(conn);
            String updateColumn = getUpdateColumn(columnsSql,"","JZ");
            String sql_filter = filterRecordCondition(drLogger,set1);
            for (int i = 0; i < locorgcodeList.size(); i++) {
                StringBuffer insertsql = new StringBuffer();
                String locorgcode = locorgcodeList.get(i);
                insertsql.append(" insert into S_ASYN_"+set1+"_JZ (column,modtime,createtime,modstate,guidkey ");
                insertsql.append(columnsSql);
                insertsql.append(" ) select column,modtime,createtime,1,ORGANIZATION.guidkey ");
                insertsql.append(columnsSql);
                insertsql.append(" from "+set2+" LEFT JOIN organization on column = organization.CODEITEMID where organization.guidkey not in (select guidkey from S_ASYN_"+set1+"_JZ) ");
                insertsql.append(" and column like '"+locorgcode+"%'");
                if("1".equals(orgType)){
                    insertsql.append(" and column <> '");
                    insertsql.append(locorgcode);
                    insertsql.append("'");
                }
                if(StringUtils.isNotEmpty(sql_filter)){
                    insertsql.append(" and NOT EXISTS ( select 1 from "+set2+" a where "+sql_filter+" and a.column= "+set2+".column )");
                }
                if(set1.contains("B")){
                    dao.update(insertsql.toString().replaceAll("column","b0110"));
                    drLogger.write("分布同步："+set2+"主集插入S_ASYN_"+set1+"_JZ基准表数据成功！");
                }else if(set1.contains("K")){
                    dao.update(insertsql.toString().replaceAll("column","e01a1"));
                    drLogger.write("分布同步："+set2+"主集插入S_ASYN_"+set1+"_JZ基准表数据成功！");
                }
            }
            String srcTab = " (SELECT COLUMN,MODTIME,createtime,organization.GUIDKEY "+columnsSql+" FROM "+set2+" LEFT JOIN ORGANIZATION ON COLUMN = ORGANIZATION .codeitemid) jz ";
            String destTab = "S_ASYN_"+set1+"_JZ";
            String strJoin = "jz.guidkey = "+destTab+".guidkey";
            String strSWhere = "jz.modtime > "+destTab+".modtime";
            String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
            String strSet = destTab+".COLUMN=jz.COLUMN`"+destTab+".modstate = 2`"+destTab+".createtime=jz.createtime`"+destTab+".modtime=jz.modtime"+updateColumn;
            String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
            if(set1.contains("B")){
                dao.update(updateSql.replaceAll("COLUMN","b0110"));
                drLogger.write("分布同步："+set2+"主集更新S_ASYN_"+set1+"_JZ基准表数据成功！");
            }else if(set1.contains("K")){
                dao.update(updateSql.replaceAll("COLUMN","e01a1"));
                drLogger.write("分布同步："+set2+"主集更新S_ASYN_"+set1+"_JZ基准表数据成功！");
            }

        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步："+set2+"主集插入S_ASYN_"+set1+"_JZ基准表数据失败！"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 将组织机构代码转成上级机构
     * @param set1
     * @param unitcode
     * @param locorgCodeList
     * @param drLogger
     */
    public void transBKJZTableCode(String set1, String unitcode, String orgType, List<String> locorgCodeList, DrLogger drLogger) throws GeneralException {
        String sql = "";
        ArrayList valueList = new ArrayList();
        try{
            String firstTable = set1.substring(0, 1).toUpperCase();
            ContentDAO dao = new ContentDAO(this.conn);
            if("1".equals(orgType)){
                valueList.add(unitcode);
                valueList.add(locorgCodeList.get(0).length()+1);
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sql = "UPDATE S_ASYN_"+set1+"_JZ set column_SJ = ? + substring(column,?,LEN(column)) where modstate <> 0";
                }else{
                    sql = "UPDATE S_ASYN_"+set1+"_JZ set column_SJ = ? ||substr(column,?,LENGTH(column)) where modstate <> 0";
                }
            }else{
                valueList.add(unitcode);
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sql = "UPDATE S_ASYN_"+set1+"_JZ set column_SJ =  ? + column where modstate <> 0";
                }else{
                    sql = "UPDATE S_ASYN_"+set1+"_JZ set column_SJ = ? || column where modstate <> 0";
                }
            }
            if("B".equalsIgnoreCase(firstTable)){
                dao.update(sql.replaceAll("column","b0110"),valueList);
                drLogger.write("分布同步：将S_ASYN_"+set1+"_JZ基准表的b0110按上级代码转换成功！");
            }else if(("K").equalsIgnoreCase(firstTable)){
                dao.update(sql.replaceAll("column","e01a1"),valueList);
                drLogger.write("分布同步：将S_ASYN_"+set1+"_JZ基准表的e01a1按上级代码转换成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：将S_ASYN_"+set1+"_JZ基准表按上级代码转换失败！"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 获取所有需要转换的代码项
     * @param setid
     * @param drLogger
     * @return
     */
    public List<Map<String, String>> getCodeSetList(String setid, DrLogger drLogger) throws GeneralException {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        RowSet rs = null;
        StringBuffer sqls = new StringBuffer();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            sqls.append("select field1,field2,codeset1,codeset2 from TRANDB_FIELD tf,trandb_scheme ts ");
            sqls.append(" where TS.id = TF.id and TS.DBTYPE = 1000 AND CODESET2 not in ('UN','UM','@K')");
            sqls.append(" and set1 ='");
            sqls.append(setid);
            sqls.append("' ");
            if (1 == Sql_switcher.searchDbServerFlag() || 9 == Sql_switcher.searchDbServerFlag()) {
                sqls.append(" and TF.field2 is not null AND TF.field2 <>'' and  CODESET2 IS NOT NULL AND CodeSet2 <>'' ");
            }else{
                sqls.append(" and TF.field2 is not null and  CODESET2 IS NOT NULL ");
            }
            rs = dao.search(sqls.toString());
            while (rs.next()) {
                String field1 = rs.getString("field1");
                String codeset1 = rs.getString("codeset1");
                String field2 = rs.getString("field2");
                String codeset2 = rs.getString("codeset2");
                Map<String, String> map = new HashMap<String, String>();
                map.put("field1",field1);
                map.put("codeset1",codeset1);
                map.put("field2",field2);
                map.put("codeset2",codeset2);
                list.add(map);
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：获取需要转换的代码项失败！"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

    /**
     * 根据对应转换代码
     * @param codeSetList
     * @param setid
     * @param drLogger
     */
    public void transCodeItem(List<Map<String, String>> codeSetList, String setid, DrLogger drLogger) throws GeneralException {
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            for(int i=0;i<codeSetList.size();i++) {
                HashMap<String, String> map = (HashMap<String, String>) codeSetList.get(i);
                String field1 = map.get("field1");
                String field2 = map.get("field2");
                String codeset1 = map.get("codeset1");
                String codeset2 = map.get("codeset2");
                StringBuffer sqls = new StringBuffer();
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sqls.append("Update S_ASYN_"+setid+"_JZ ");
                    sqls.append(" SET "+field1+"_SJ = Code1 ");
                    sqls.append(" FROM trandb_code WHERE trandb_code.Id in (select ID from trandb_scheme WHERE DBType=1000) ");
                    sqls.append(" AND CodeSet1='"+codeset1+"' ");
                    sqls.append(" AND CodeSet2='"+codeset2+"' ");
                    sqls.append(" AND fielditem1 = '"+field1+"' ");
                    sqls.append(" AND S_ASYN_"+setid+"_JZ."+field2+"=Code2 ");
                } else {
                    sqls.append("Update S_ASYN_"+setid+"_JZ ");
                    sqls.append(" SET "+field1+"_SJ = ( ");
                    sqls.append(" SELECT Code1 FROM trandb_code WHERE trandb_code.ID IN (select ID FROM trandb_scheme WHERE DBType = 1000 ) ");
                    sqls.append(" AND CodeSet1='"+codeset1+"' ");
                    sqls.append(" AND CodeSet2='"+codeset2+"' ");
                    sqls.append(" AND fielditem1 = '"+field1+"' ");
                    sqls.append(" AND S_ASYN_"+setid+"_JZ."+field2+"=Code2 )");
                }
                dao.update(sqls.toString());//注意不同的库
                drLogger.write("分布同步：根据"+field1+"对应关系，转换S_ASYN_"+setid+"_JZ基准表代码项成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：根据对应关系，转换S_ASYN_"+setid+"_JZ基准表代码项失败！"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 转换组织机构代码
     * @param setid
     * @param unitcode
     * @param locorgCodeList
     * @param orgType
     * @param drLogger
     */
    public void transOrganizationCode(String setid, String unitcode,List<String> locorgCodeList,String orgType, DrLogger drLogger) throws GeneralException {
        RowSet rs = null;
        String srcTab = "ORGANIZATION";
        String destTab = "S_ASYN_"+setid+"_JZ";
        try{
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search("select field1,field2,codeset2 from TRANDB_FIELD tf,trandb_scheme ts where TS.id = TF.id and TF.codeset2 in ('UN','UM','@K')  and TS.DBTYPE = 1000 and set1 = '"+setid+"'");
            while (rs.next()){
                String field1 = rs.getString("field1");
                String field2 = rs.getString("field2");
                String codeset2 = rs.getString("codeset2");
                String strJoin = srcTab+".codeitemid = "+destTab+"."+field2;
                String strSWhere = srcTab+".CODESETid = '"+codeset2+"' and modstate <> 0";
                String strDWhere = "EXISTS ( select codeitemid from "+srcTab+" where "+strJoin+")";
                String strSet = "";
                if("1".equals(orgType)){
                    if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                        strSet = destTab+"."+field1+"_sj = '"+unitcode+"'+ substring("+srcTab+".codeitemid"+","+(locorgCodeList.get(0).length()+1)+",LEN("+srcTab+".codeitemid"+"))" ;
                    }else{
                        strSet = destTab+"."+field1+"_sj = '"+unitcode+"'||substr("+srcTab+".codeitemid"+","+(locorgCodeList.get(0).length()+1)+",LENGTH("+srcTab+".codeitemid"+"))" ;
                    }
                }else{
                    if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                        strSet = destTab+"."+field1+"_sj = '"+unitcode+"'+ "+srcTab+".codeitemid";
                    }else{
                        strSet = destTab+"."+field1+"_sj = '"+unitcode+"'||"+srcTab+".codeitemid";
                    }
                }
                String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
                dao.update(updateSql);
                drLogger.write("分布同步：S_ASYN_"+setid+"_JZ基准表转换"+field1+"代码项成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：S_ASYN_"+setid+"_JZ基准表转换组织机构代码失败！"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 处理校验规则过滤出去的数据
     * @param TableList
     * @param drLogger
     * @param uuid
     */
    public void filterValidateRules(ArrayList<Map<String, String>> TableList, DrLogger drLogger, String uuid) {
        RowSet rowSet = null;
        RowSet rowSet1 = null;
        RowSet rowSet2 = null;
        RowSet rowSet3 = null;
        try {
            List<HashMap<String,Object>>  ruleList = new ArrayList<HashMap<String,Object>>();
            String sql = "select checkfield,condition,forcestate from t_sys_asyn_validaterules where belong = 1 and valid = 1 ";
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
                String itemids = (String) hashMap.get("checkfield");
                String condition = (String) hashMap.get("condition");
                int forcestate = (Integer) hashMap.get("forcestate");
                Set<String> setlist = new HashSet<String>();
                ArrayList<String> arraySetList  = new ArrayList<String>();
                StringBuffer itembuff = new StringBuffer();
                String[] itemArray = itemids.split("`");
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
                    drLogger.write(hashMap.toString()+":此校验规则不只有一个子集");
                }else if (setlist.size()==1) {
                    ArrayList<ArrayList<Object>>  list = new ArrayList<ArrayList<Object>>();
                    String tablename = "v_asyn_"+arraySetList.get(0);
                    sql= "select aid "+itembuff
                            + ",a0101 as name from "+tablename+" where aid not in ( select aid from "+tablename+" where "+condition+")";
                    if (!"A01".equalsIgnoreCase(arraySetList.get(0))) {
                        sql= "select emp_id aid "+itembuff
                                + ",a0101 as name from "+tablename+" LEFT JOIN V_ASYN_A01 on "+tablename+".emp_id = V_ASYN_A01.aid where "+tablename+".aid not in ( select aid from "+tablename+" where "+condition+")";
                    }
                    rowSet1=dao.search(sql);
                    while (rowSet1.next()) {
                        String guidkey = rowSet1.getString("aid");
                        String name = rowSet1.getString("name");
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
                        listObject.add(name);
                        listObject.add(guidkey);
                        listObject.add(list1);
                        list.add(listObject);
                        drLogger.write("分布同步："+tablename.substring(2)+"表里的"+guidkey+"不符合校验规则");
                    }
                    saveRecordTable(list, uuid);
                    if (1==forcestate) {//强制删除过滤的数据
                            if (tablename.toUpperCase().endsWith("A01")) {//校验规则用到的表是主表
                                for (Map<String, String> map : TableList) {
                                    String temptableName = map.get("set1");
                                    String firstTable = temptableName.substring(0, 1).toUpperCase();
                                    if (!"A01".equalsIgnoreCase(temptableName) && "A".equalsIgnoreCase(firstTable)) {
                                        sql = "delete from s_asyn_"+temptableName+"_jz where emp_id not in ( select aid from "+tablename+" where "+condition+")";
                                        dao.update(sql, new ArrayList());
                                    }
                                }
                            sql = "delete from s_asyn_a01_jz where guidkey not in ( select aid from "+tablename+" where "+condition+")";
                            dao.update(sql, new ArrayList());
                            }else {//校验规则用到的表是子表
                                sql = "delete from s_asyn_"+arraySetList.get(0)+"_jz where guidkey not in ( select aid from "+tablename+" where "+condition+")";
                                dao.update(sql, new ArrayList());
                            }
                    }
                }else if(setlist.size()==2&&setlist.contains("A01")){
                    String tablename1 = "v_asyn_"+arraySetList.get(0);
                    String tablename2 = "v_asyn_"+arraySetList.get(1);
                    sql= "select b.aid from "+tablename2+" a full join "+tablename1+" b on a.emp_id = b.aid where "+condition;
                    StringBuffer sqlbuff = new StringBuffer("");
                    sqlbuff.append("select aid,a0101 as name ");
                    for (int i = 0; i < itemArray.length; i++) {
                        FieldItem fieldItem = DataDictionary.getFieldItem(itemArray[i]);
                        if (fieldItem.getFieldsetid().equalsIgnoreCase(arraySetList.get(0))) {
                            sqlbuff.append(",");
                            sqlbuff.append(itemArray[i]);
                        }
                    }
                    sqlbuff.append(" from ");
                    sqlbuff.append(tablename1);
                    sqlbuff.append(" where aid not in (");
                    sqlbuff.append(sql);
                    sqlbuff.append(")");
                    rowSet2=dao.search(sqlbuff.toString());
                    ArrayList<ArrayList<Object>>  list = new ArrayList<ArrayList<Object>>();
                    while (rowSet2.next()) {
                        String guidkey = rowSet2.getString("aid");
                        String name = rowSet2.getString("name");
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
                        listObject.add(name);
                        listObject.add(guidkey);
                        listObject.add(list1);
                        list.add(listObject);
                        drLogger.write("分布同步："+tablename1.substring(2)+"表里的"+guidkey+"不符合校验规则");
                    }
                    sqlbuff = new StringBuffer("");
                    sqlbuff.append("select ");
                    sqlbuff.append(tablename2);
                    sqlbuff.append(".aid,a0101 as name ");
                    for (int i = 0; i < itemArray.length; i++) {
                        FieldItem fieldItem = DataDictionary.getFieldItem(itemArray[i]);
                        if (fieldItem.getFieldsetid().equalsIgnoreCase(arraySetList.get(1))) {
                            sqlbuff.append(",");
                            sqlbuff.append(itemArray[i]);
                        }
                    }
                    sqlbuff.append(" from ");
                    sqlbuff.append(tablename2);
                    sqlbuff.append(" left join v_asyn_A01 on v_asyn_A01.aid = ");
                    sqlbuff.append(tablename2);
                    sqlbuff.append(".emp_id ");
                    sqlbuff.append(" where emp_id not in (");
                    sqlbuff.append(sql);
                    sqlbuff.append(")");
                    rowSet3=dao.search(sqlbuff.toString());
                    while (rowSet3.next()) {
                        String guidkey = rowSet3.getString("aid");
                        String name = rowSet3.getString("name");
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
                        listObject.add(name);
                        listObject.add(guidkey);
                        listObject.add(list1);
                        list.add(listObject);
                        drLogger.write("分布同步："+tablename2.substring(2)+"表里的"+guidkey+"不符合校验规则");
                    }
                    if (list.size()>0) {
                        saveRecordTable(list,uuid);
                    }
                    if (1==forcestate) {//强制删数据
                        for (Map<String, String> map : TableList) {
                            String temptableName = map.get("set1");
                            if ("A01".equalsIgnoreCase(temptableName)) { //要删除数据的表是主表
                                sqlbuff = new StringBuffer("");
                                sqlbuff.append("delete from S_ASYN_");
                                sqlbuff.append(temptableName);
                                sqlbuff.append("_JZ ");
                                sqlbuff.append(" where not EXISTS (select 1 from (");
                                sqlbuff.append(sql);
                                sqlbuff.append(") b where S_ASYN_"+temptableName+"_JZ.guidkey = b.aid)");
                                dao.update(sqlbuff.toString(), new ArrayList<String>());
                            }else if(temptableName.toUpperCase().contains("A")){//要删除数据的表是子表
                                sqlbuff = new StringBuffer("");
                                sqlbuff.append("delete from S_ASYN_");
                                sqlbuff.append(temptableName);
                                sqlbuff.append("_JZ ");
                                sqlbuff.append(" where  not EXISTS (select 1 from (");
                                sqlbuff.append(sql);
                                sqlbuff.append(") b where S_ASYN_"+temptableName+"_JZ.emp_id = b.aid)");
                                dao.update(sqlbuff.toString(), new ArrayList<String>());
                            }
                        }
                    }
                }else {
                    drLogger.write("分布同步："+hashMap.toString()+":不符合要求");
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
     * 保存校验规则过滤的数据
     * @param listArray
     * @param uuid
     */
    private void saveRecordTable(ArrayList<ArrayList<Object>> listArray, String uuid) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList arrayList = new ArrayList();
            for (int i = 0;i<listArray.size();i++) {
                ArrayList<Object> object  = listArray.get(i);
                ArrayList<Object> objectlist  = new ArrayList<Object>();
                int id = getMaxId("T_SYS_ASYN_RECORD")+i;
                String type = (String) object.get(0);
                String setid = (String) object.get(1);
                String name = (String) object.get(2)==null?"无":(String) object.get(2);
                String guidkey = (String) object.get(3);
                ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) object.get(4);
                StringBuffer extMemo = new StringBuffer("");
                extMemo.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
                extMemo.append("<root>");
                for (HashMap<String, String> hashMap : list) {
                    String value = hashMap.get("value")==null?"":hashMap.get("value");
                    extMemo.append("<rec itemid=\""+hashMap.get("itemid")+"\" reason=\""+hashMap.get("reason")+"\" value=\""+value+"\"></rec>");
                }
                extMemo.append("</root>");
                objectlist.add(id);
                objectlist.add(uuid);
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
        }
    }

    /**
     * 过滤必填和唯一数据
     * @param menusBean
     * @param tableList
     * @param drLogger
     * @param uuid
     */
     public void filterMustbeAndUniqueData(MenusBean menusBean, ArrayList<Map<String, String>> tableList, DrLogger drLogger, String uuid) throws GeneralException {
        try{
            List<SetListBean> setBeanList = menusBean.getSet_list();
            for(int i = 0 ; i < setBeanList.size(); i++){
                SetListBean setBean = setBeanList.get(i);
                String set = setBean.getSet_id();
                List fielditemList = setBean.getFielditem_list();
                ArrayList<String> columnMustList = new ArrayList<String>();
                ArrayList<String> columnUniquelist = new ArrayList<String>();
                for (Object fieldItem : fielditemList){
                    FielditemListBean field = (FielditemListBean) fieldItem;
                    String itemid = field.getItemid();
                    String mustbe = field.getMustbe();
                    String uniquefiag = field.getUniqueflag();
                    if("TRUE".equalsIgnoreCase(mustbe)){
                        columnMustList.add(itemid);
                    }
                    if ("TRUE".equalsIgnoreCase(uniquefiag)){
                        columnUniquelist.add(itemid);
                    }
                }
                if(columnMustList.size() > 0){
                    saveFilterMustDataToRecord(set,columnMustList,uuid,tableList);
                }
                if(columnUniquelist.size() > 0){
                    saveFilterUniqueDataToRecord(set,columnUniquelist,uuid,tableList);
                }
            }
            drLogger.write("分布同步：过滤必填和唯一数据成功！");
        }catch (Exception e){
            drLogger.write("分布同步：过滤必填和唯一数据失败"+e.getMessage());
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 将过滤唯一数据保存到日志表中
     * @param set
     * @param columnlist
     * @param uuid
     * @param tableList
     */
    private void saveFilterUniqueDataToRecord(String set, ArrayList<String> columnlist, String uuid, ArrayList<Map<String, String>> tableList) throws GeneralException {
        RowSet rs = null;
        try{
            String tablename = "v_asyn_"+set;
            ContentDAO dao = new ContentDAO(this.conn);
            HashMap<String,String> fieldMapping = getFieldMapping(set);
            for (int i = 0; i < columnlist.size(); i++) {
                ArrayList<ArrayList<Object>>  list = new ArrayList<ArrayList<Object>>();
                String itemid_sj = columnlist.get(i);
                String itemid_xj = fieldMapping.get(itemid_sj);
                StringBuffer sqlwhere = new StringBuffer();
                sqlwhere.append(itemid_sj);
                sqlwhere.append(" in ( select "+itemid_sj+" from "+tablename+" where modstate <> 3  GROUP BY "+itemid_sj);
                sqlwhere.append(" HAVING count(*)>1) ");
                String sql = "select aid,"+itemid_sj+",a0101 as name from "+tablename+" where "+sqlwhere;
                rs = dao.search(sql);
                while (rs.next()) {
                    String guidkey = rs.getString("aid");
                    String name = rs.getString("name");
                    ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("itemid", itemid_sj);
                    map.put("value", rs.getString(itemid_sj));
                    map.put("reason","下级对应指标"+itemid_xj+"不符合唯一性指标条件");
                    list1.add(map);
                    ArrayList<Object> listObject = new ArrayList<Object>();
                    listObject.add("A");
                    listObject.add(set);
                    listObject.add(name);
                    listObject.add(guidkey);
                    listObject.add(list1);
                    list.add(listObject);
                }
                saveRecordTable(list, uuid);
                updateJZTableStatus(tablename,tableList,sqlwhere.toString());//更新状态
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 将过滤必填数据保存到日志表中
     * @param set
     * @param columnlist
     * @param uuid
     * @param tableList
     */
    private void saveFilterMustDataToRecord(String set, ArrayList<String> columnlist, String uuid, ArrayList<Map<String, String>> tableList) throws GeneralException {
        String sql = "";
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            String firstTable = set.substring(0, 1).toUpperCase();
            String tablename = "v_asyn_"+set;
            HashMap<String,String> fieldMapping = getFieldMapping(set);
            for (int i = 0; i < columnlist.size(); i++) {
                ArrayList<ArrayList<Object>>  list = new ArrayList<ArrayList<Object>>();
                String itemid_sj = columnlist.get(i);
                String itemid_xj = fieldMapping.get(itemid_sj);
                FieldItem item = DataDictionary.getFieldItem(itemid_xj);
                String itemType = item.getItemtype();
                StringBuffer sqlwhere = new StringBuffer();
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    sqlwhere.append(itemid_sj + " is null ");
                    if("A".equalsIgnoreCase(itemType)){
                        sqlwhere.append(" or "+itemid_sj+" = ''");
                    }
                } else {
                    sqlwhere.append(itemid_sj + " is null ");
                }
                if("A01".equalsIgnoreCase(set)){
                    sql = "select aid as id,"+itemid_sj+",a0101 as name from "+tablename+" where "+sqlwhere;
                }else if (!"A01".equalsIgnoreCase(set) && "A".equalsIgnoreCase(firstTable)){
                    sql = "select "+tablename+".aid as id,emp_id,"+itemid_sj+",a0101 as name from "+tablename+"  LEFT JOIN V_ASYN_A01 on "+tablename+".emp_id = V_ASYN_A01.aid where "+sqlwhere;
                }else if ("B".equalsIgnoreCase(firstTable)){
                    sql = "select bid as id,"+itemid_sj+",b0110 as name from "+tablename+" where "+sqlwhere;
                }else if ("K".equalsIgnoreCase(firstTable)){
                    sql = "select kid as id,"+itemid_sj+",b0110 as name from "+tablename+" where "+sqlwhere;
                }
                rs=dao.search(sql);
                while (rs.next()) {
                    String guidkey = rs.getString("id");
                    String name = rs.getString("name");
                    ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("itemid", itemid_sj);
                    map.put("value", rs.getString(itemid_sj));
                    map.put("reason","下级对应指标"+itemid_xj+"不符合必填指标条件");
                    list1.add(map);
                    ArrayList<Object> listObject = new ArrayList<Object>();
                    listObject.add(firstTable);
                    listObject.add(set);
                    listObject.add(name);
                    listObject.add(guidkey);
                    listObject.add(list1);
                    list.add(listObject);
                }
                saveRecordTable(list, uuid);//保存记录到日志表
                if("A".equalsIgnoreCase(firstTable)){
                    updateJZTableStatus(tablename,tableList,sqlwhere.toString());//更新状态
                }else{
                    dao.update("update s_asyn_"+set+"_jz set status  = 1 where "+sqlwhere);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 获取上下级指标的对应关系
     * @return
     * @param setid
     */
    private HashMap<String, String> getFieldMapping(String setid) {
        HashMap<String, String> map = new HashMap<String, String>();
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search("select field1,field2 from TRANDB_FIELD tf,trandb_scheme ts where TS.id = TF.id and TF.field2 is not null  and TS.DBTYPE = 1000 and set1 = '"+setid+"' ");
            while (rs.next()) {
                String field1 = rs.getString("field1");
                String field2 = rs.getString("field2");
                map.put(field1,field2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    /**
     * 更新基准表的status
     */
    private void updateJZTableStatus(String tablename,ArrayList<Map<String, String>> tableList,String sqlwhere){
        String sql = "";
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            for (Map<String, String> map : tableList) {
                String temptableName = map.get("set1");
                String firstTable = temptableName.substring(0, 1).toUpperCase();
                if (tablename.toUpperCase().endsWith("A01")) {//过滤条件用到的表是主表
                    if (temptableName.toUpperCase().endsWith("A01")) { //要改状态的表是主表
                        sql = "update s_asyn_"+temptableName+"_jz set status  = 1 where guidkey in ( select aid from "+tablename+" where "+sqlwhere+")";
                        dao.update(sql, new ArrayList());
                    }else if("A".equalsIgnoreCase(firstTable)){//要改状态的表是子表
                        sql = "update s_asyn_"+temptableName+"_jz set status  = 1 where emp_id in ( select aid from "+tablename+" where "+sqlwhere+")";
                        dao.update(sql, new ArrayList());
                    }
                }else {//过滤条件用到的表是子表
                    if (temptableName.toUpperCase().endsWith("A01")) { //要改状态的表是主表
                        sql = "update s_asyn_"+temptableName+"_jz set status  = 1 where guidkey in ( select emp_id from "+tablename+" where "+sqlwhere+")";
                        dao.update(sql, new ArrayList());
                    }else if(temptableName.toUpperCase().contains("A")){//要改状态的表是子表
                        sql = "update s_asyn_"+temptableName+"_jz set status  = 1 where emp_id in ( select emp_id from "+tablename+" where "+sqlwhere+")";
                        dao.update(sql, new ArrayList());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 过滤记录条件数据
     * @param tableList
     * @param locorgcode
     * @param drLogger
     * @param uuid
     */
/*    public void filterRecordCondition(ArrayList<Map<String, String>> tableList, String locorgcode,DrLogger drLogger, String uuid) {
        String sql = "";
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            for (Map<String, String> tableMap : tableList) {
                String tableSet1 = tableMap.get("set1");//上级需要上传的子集
                String firstTable = tableSet1.substring(0, 1).toUpperCase();
                String tablename = "s_asyn_"+tableSet1+"_jz";
                String c_expr = getFormula(tableSet1);
                ArrayList<ArrayList<Object>>  list = new ArrayList<ArrayList<Object>>();
                if(StringUtils.isNotEmpty(c_expr)){
                    c_expr = SafeCode.decode(c_expr);
                    ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
                            Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
                    YksjParser yp = new YksjParser(this.userView, allUsedFields,
                            YksjParser.forNormal, YksjParser.LOGIC,
                            YksjParser.forPerson, "Ht", "");
                    yp.setCon(conn);
                    // 解析sql
                    yp.run_where(c_expr);
                    HashMap mapUsedFieldItems = yp.getMapUsedFieldItems();
                    StringBuffer itemIdBuff = new StringBuffer();
                    for(Object key : mapUsedFieldItems.keySet()){
                        String itemId = (String) key;
                        itemIdBuff.append(",");
                        itemIdBuff.append(itemId);
                    }
                    String resultSql = yp.getSQL();
                    if("A01".equalsIgnoreCase(tableSet1)){
                        sql = "select guidkey as id"+itemIdBuff+",a0101 as name from "+tablename+" where "+resultSql;
                    }else if (!"A01".equalsIgnoreCase(tableSet1) && "A".equalsIgnoreCase(firstTable)){
                        sql = "select "+tablename+".guidkey as id"+itemIdBuff+",emp_id,a0101 as name from "+tablename+"  LEFT JOIN V_ASYN_A01 on "+tablename+".emp_id = V_ASYN_A01.aid where "+resultSql;
                    }else if ("B".equalsIgnoreCase(firstTable)){
                        sql = "select guidkey as id"+itemIdBuff+",b0110 as name from "+tablename+" where "+resultSql;
                    }else if ("K".equalsIgnoreCase(firstTable)){
                        sql = "select guidkey as id"+itemIdBuff+",e01a1 as name from "+tablename+" where "+resultSql;
                    }
                    rs=dao.search(sql);
                    String[] itemArray = itemIdBuff.substring(1).split("\\,");
                    while (rs.next()) {
                        String guidkey = rs.getString("id");
                        String name = rs.getString("name");
                        ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
                        for (int i = 0; i < itemArray.length; i++) {
                            HashMap<String,String> map = new HashMap<String,String>();
                            map.put("itemid", itemArray[i]);
                            map.put("value", rs.getString(itemArray[i]));
                            map.put("reason","符合"+c_expr+"条件的数据");
                            list1.add(map);
                        }
                        ArrayList<Object> listObject = new ArrayList<Object>();
                        listObject.add(firstTable);
                        listObject.add(tableSet1);
                        listObject.add(name);
                        listObject.add(guidkey);
                        listObject.add(list1);
                        list.add(listObject);
                    }
                    saveRecordTable(list, uuid);
                    if ("A01".equalsIgnoreCase(tableSet1)) {//过滤记录条件用到的表是主表
                        for (Map<String, String> map : tableList) {
                            String temptableName = map.get("set1");
                            String firstTemptableName = temptableName.substring(0,1).toUpperCase();
                            if (!"A01".equalsIgnoreCase(temptableName)&&"A".equalsIgnoreCase(firstTemptableName)) {
                                sql = "delete from s_asyn_"+temptableName+"_jz where emp_id in ( select guidkey from "+tablename+" where "+resultSql+")";
                                drLogger.write("分布同步：删除过滤条件主表数据sql="+sql);
                                dao.update(sql, new ArrayList());
                            }
                        }
                        sql = "delete from s_asyn_a01_jz where guidkey in ( select guidkey from "+tablename+" where "+resultSql+")";
                        drLogger.write("分布同步：删除过滤条件主表对应子表数据sql="+sql);
                        dao.update(sql, new ArrayList());
                    }else {//过滤记录条件的表是子表
                        sql = "delete from "+tablename+" where guidkey in ( select guidkey from "+tablename+" where "+resultSql+")";
                        drLogger.write("分布同步删除过滤条件子表数据sql="+sql);
                        dao.update(sql, new ArrayList());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：过滤记录条件失败！"+e.getMessage());
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }*/

    /**
     * 过滤记录条件sql
     * @param drLogger
     * @param tableSet1
     */
    public String filterRecordCondition(DrLogger drLogger, String tableSet1) {
        String resultSql = "";
        try{
            String c_expr = getFormula(tableSet1);
            if(StringUtils.isNotEmpty(c_expr)){
                c_expr = SafeCode.decode(c_expr);
                ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
                        Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
                YksjParser yp = new YksjParser(this.userView, allUsedFields,
                        YksjParser.forNormal, YksjParser.LOGIC,
                        YksjParser.forPerson, "Ht", "");
                yp.setCon(conn);
                // 解析sql
                yp.run_where(c_expr);
                resultSql = yp.getSQL();
                if("A01".equalsIgnoreCase(tableSet1)){
                    this.filterConditionMap.put("A",resultSql);
                }
                if("B01".equalsIgnoreCase(tableSet1)){
                    this.filterConditionMap.put("B",resultSql);
                }
                if("K01".equalsIgnoreCase(tableSet1)){
                    this.filterConditionMap.put("K",resultSql);
                }
                resultSql = yp.getSQL();
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：解析过滤记录条件失败！"+e.getMessage());
        }
        return resultSql;
    }

    /**
     * 获取过滤条件
     * @param opTable
     * @return
     */
    private String getFormula(String opTable) {
        String formula = "";
        RowSet rs = null;
        List list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            sql.append("select c_expr from t_sys_asyn_filtercondition where setid = ? ");
            list.add(opTable);
            rs = dao.search(sql.toString(), list);
            if (!rs.next()) {
                return null;
            }
            formula = rs.getString("c_expr");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return formula;
    }

    /**
     * 全量同步删除基准表的数据
     * @param list
     */
    public void deleteJZTable(ArrayList<Map<String, String>> list, DrLogger drLogger) throws GeneralException {
        try{
            DbWizard dbWizard = new DbWizard(this.conn);
            for (Map<String, String> tableMap : list) {
                String table = "S_ASYN_"+tableMap.get("set1")+"_JZ";
                if (dbWizard.isExistTable(table, false)) {
                    dbWizard.dropTable(table);
                    drLogger.write("分布同步：删除基准表"+table+"成功！");
                }
            }
            if (dbWizard.isExistTable("S_ASYN_ORG_JZ", false)) {
                dbWizard.dropTable("S_ASYN_ORG_JZ");
                drLogger.write("分布同步：删除基准表S_ASYN_ORG_JZ成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 同步视图数据到基准表
     * @param columnsSql
     * @param tableSet1
     * @param tableSet2
     * @param locorgcodeList
     * @param drLogger
     */
    public void asynViewDataTOJZTable(String columnsSql, String tableSet1, String tableSet2, List<String> locorgcodeList, DrLogger drLogger) throws GeneralException {
        RowSet rs = null;
        String nbase = "Usr";//默认在职库
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search("select DISTINCT(nbase) from "+tableSet2);
            if(rs.next()){
                nbase = rs.getString("nbase");
            }
            String sql_filter = filterRecordCondition(drLogger,tableSet1);
            for (int i = 0; i < locorgcodeList.size(); i++) {
                String locorgcode = locorgcodeList.get(i);
                StringBuffer sqls = new StringBuffer();
                sqls.append("INSERT INTO S_ASYN_");
                sqls.append(tableSet1);
                sqls.append("_jz (nbase,guidkey,modtime,createtime,modstate,emp_id,i9999 ");
                sqls.append(columnsSql);
                sqls.append(" ) select nbase,guidkey,"+Sql_switcher.isnull("modtime",Sql_switcher.sqlNow())+",createtime,1 as modstate,emp_id,i9999 ");
                sqls.append(columnsSql);
                sqls.append(" from ");
                sqls.append(tableSet2);
                sqls.append(" where guidkey not in ( select guidkey from s_asyn_");
                sqls.append(tableSet1+"_jz ) and b0110 like '"+locorgcode+"%' ");
                if(StringUtils.isNotEmpty(filterConditionMap.get("A"))){
                    sqls.append(" and NOT EXISTS ( select 1 from "+nbase+"A01 a where "+filterConditionMap.get("A")+" and a.a0100 = "+tableSet2+".a0100 )");
                }
                if(StringUtils.isNotEmpty(sql_filter)){
                    sqls.append(" and NOT EXISTS ( select 1 from "+tableSet2+" b where "+sql_filter+" and b.guidkey = "+tableSet2+".guidkey )");
                }

                dao.update(sqls.toString());
                drLogger.write("分布同步：同步"+tableSet2+"视图"+locorgcode+"单位下数据插入到S_ASYN_"+tableSet1+"_JZ基准表成功！");
            }
            String srcTab = tableSet2;
            String destTab = "S_ASYN_"+tableSet1+"_JZ";
            String strJoin = srcTab+".guidkey = "+destTab+".guidkey";
            String strSWhere = srcTab+".modtime > "+destTab+".modtime";
            String strDWhere = "EXISTS ( select * from "+srcTab+" where "+strJoin+" and "+strSWhere+")";
            String updateColumn = getUpdateColumn(columnsSql,"",srcTab);
            String strSet = destTab+".status=''`"+destTab+".nbase="+srcTab+".nbase`"+destTab+".modstate = 2`"+destTab+".modtime="+srcTab+".modtime`"+destTab+".createtime="+srcTab+".createtime`"+destTab+".i9999="+srcTab+".i9999"+updateColumn;
            String updateSql = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,strSWhere);
            dao.update(updateSql);
            drLogger.write("分布同步：同步"+tableSet2+"视图数据更新到S_ASYN_"+tableSet1+"_JZ基准表成功！");
        }catch (Exception e){
            drLogger.write("分布同步：同步视图数据到基准表失败！"+e.getMessage());
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 删除人员数据更新状态到基准表
     * @param nbaseArr
     * @param tableSet1
     * @param tableSet2
     * @param drLogger
     * @param dataStartTime
     */
    public void deleteDataToJZTable(String[] nbaseArr, String tableSet1, String tableSet2, DrLogger drLogger, String dataStartTime) throws GeneralException {
        StringBuffer sqls = new StringBuffer();
        List list = new ArrayList();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "update s_asyn_"+tableSet1+"_jz set modstate = ?,modtime = ? where modstate <> 3 and not EXISTS ( select 1 from (";
            for (int i = 0; i < nbaseArr.length; i++) {
                String nbase = nbaseArr[i];
                sqls.append("select guidkey from ");
                sqls.append(nbase);
                sqls.append(tableSet2);
                if (i<nbaseArr.length-1){
                    sqls.append(" union all ");
                }
            }
            list.add(3);
            list.add(DateUtils.getTimestamp(dataStartTime, "yyyy-MM-dd HH:mm"));
            dao.update(sql+sqls.toString()+") b where s_asyn_"+tableSet1+"_jz.guidkey = b.guidkey )",list);
            drLogger.write("分布同步：更改删除状态到S_ASYN_"+tableSet1+"_JZ基准表成功！");
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：更改删除状态到S_ASYN_"+tableSet1+"_JZ基准表失败！"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 删除组织机构数据更新状态到基准表
     * @param tableSet1
     * @param tableSet2
     * @param drLogger
     * @param dataStartTime
     */
    public void deleteBKVDataToJZTable(String tableSet1, String tableSet2, DrLogger drLogger, String dataStartTime) throws GeneralException {
        List list = new ArrayList();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "update s_asyn_"+tableSet1+"_jz set modstate = ?,modtime = ? where modstate <> 3 and guidkey not in (select guidkey from "+tableSet2+")";
            list.add(3);
            list.add(DateUtils.getTimestamp(dataStartTime, "yyyy-MM-dd HH:mm"));
            dao.update(sql,list);
            drLogger.write("分布同步：更改删除状态到BK基准表成功！");
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：更改删除状态到BK基准表失败"+e.toString());
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 删除视图表
     * @param list
     */
    public void deleteViewTable(ArrayList<Map<String, String>> list) {
        try{
            DbWizard dbWizard = new DbWizard(this.conn);
            for (Map<String, String> tableMap : list) {
                String table = "V_ASYN_"+tableMap.get("set1");
                if (dbWizard.isExistTable(table, false)) {
                    dropView(table);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 更新基准表的modstate字段
     * @param list
     * @param unitcode
     * @param schemeType
     * @param dataStartTime
     */
    public void updateJzTableModstate(ArrayList <Map<String, String>> list, String unitcode, String schemeType, String dataStartTime) {
        String sql = "";
        String whereSql = "";
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            for (Map<String, String> tableMap : list) {
                String setid = tableMap.get("set1");//上级需要上传的子集
                String firstTable = setid.substring(0, 1).toUpperCase();// A B K
                if("A01".equalsIgnoreCase(setid)){
                    sql = "update S_ASYN_"+setid+"_JZ SET MODSTATE = 0 where (STATUS IS NULL OR STATUS = '') and modstate <> 3 ";
                }else if(!"A01".equalsIgnoreCase(setid)&&"A".equalsIgnoreCase(firstTable)){
                    sql = "update S_ASYN_"+setid+"_JZ SET MODSTATE = 0 where (STATUS IS NULL OR STATUS = '') and modstate <> 3 ";
                }else if ("B".equalsIgnoreCase(firstTable)){
                    sql = "update S_ASYN_"+setid+"_JZ SET MODSTATE = 0 where (STATUS IS NULL OR STATUS = '') and modstate <> 3 ";
                }else if ("K".equalsIgnoreCase(firstTable)){
                    sql = "update S_ASYN_"+setid+"_JZ SET MODSTATE = 0 where (STATUS IS NULL OR STATUS = '') and modstate <> 3 ";
                }
                if("2".equalsIgnoreCase(schemeType)){
                    whereSql = " and "+Sql_switcher.dateToChar("modtime", "yyyy-MM-dd hh24:mi")+">='"+dataStartTime+"' ";
                }
                dao.update(sql+whereSql);
            }
            dao.update("update s_asyn_org_jz set modstate = 0 where 1=1 "+whereSql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除BK的无头数据
     * @param tableSet1
     * @param drLogger
     */
    public void deleteBKStaleData(String tableSet1, DrLogger drLogger) {
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            String firstTable = tableSet1.substring(0, 1).toUpperCase();
            if("B".equalsIgnoreCase(firstTable)){
                dao.update("delete from s_asyn_"+tableSet1+"_jz where  not EXISTS (select 1 from ORGANIZATION b where s_asyn_"+tableSet1+"_jz.b0110 = b.codeitemid )");
                drLogger.write("分布同步：删除S_ASYN_"+tableSet1+"_JZ基准表的无头数据成功！");
            }else if("K".equalsIgnoreCase(firstTable)){
                dao.update("delete from s_asyn_"+tableSet1+"_jz where not EXISTS (select 1 from ORGANIZATION b where s_asyn_"+tableSet1+"_jz.e01a1 = b.codeitemid )");
                drLogger.write("分布同步：删除S_ASYN_"+tableSet1+"_JZ基准表的无头数据成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            drLogger.write("分布同步：删除S_ASYN_"+tableSet1+"_JZ基准表的无头数据失败！"+e.toString());
        }
    }
}
