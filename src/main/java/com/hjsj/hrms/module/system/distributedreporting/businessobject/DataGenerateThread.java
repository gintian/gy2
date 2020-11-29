package com.hjsj.hrms.module.system.distributedreporting.businessobject;

import com.google.gson.Gson;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.system.distributedreporting.generatedata.generatedatabean.SiteBean;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.sql.RowSet;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.concurrent.Callable;

public class DataGenerateThread implements Callable {
    private DrLogger logger;
    private String table;
    private String schemeType;//1为全量；2为增量
    private String dataStartTime;//增量起始时间
    private String unitcode;//上级单位代码
    private List<String> locorgcodeList;//上级单位代码
    public DataGenerateThread(String schemeType, String dataStartTime, String table, List<String> locorgcodeList, String unitcode, Date date){
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            this.table = table;
            this.schemeType = schemeType;
            this.dataStartTime = dataStartTime;
            this.unitcode = unitcode;
            this.locorgcodeList = locorgcodeList;
            this.logger = new DrLogger(locorgcodeList.get(0),0,date);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(conn);
        }
    }

    @Override
    public Map<String, Object> call() throws Exception {
        Map<String, Object> fileMap = new HashMap<String, Object>();;
        try {
            if(!"PHOTO".equalsIgnoreCase(this.table)){
                fileMap = generateDataPackage(this.schemeType,this.dataStartTime,this.table,this.unitcode);
            }else{
                fileMap = generatePhotoPackage(this.schemeType,this.dataStartTime,this.locorgcodeList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileMap;
    }

    /**
     * 生成照片数据包
     * @param schemeType
     * @param dataStartTime
     * @param locorgcodeList
     * @return
     */
    private Map<String, Object> generatePhotoPackage(String schemeType, String dataStartTime,List<String> locorgcodeList) {
        Map<String, Object> fileMap = new HashMap<String, Object>();
        Gson gson = new Gson();
        Connection conn = null;
        RowSet rs = null;
        RowSet rowSet = null;
        JSONArray dataJsonArray = new JSONArray();
        List <String> filePathlist = new ArrayList<String>();
        String whereSql = "";
        int count = 0;
        try{
            conn = AdminDb.getConnection();
            ContentDAO dao=new ContentDAO(conn);
            ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
            String photoPath = constantXml.getNodeAttributeValue("/filepath", "rootpath")+ File.separator+"asyn"+File.separator+"asynreport"+File.separator+"data"+File.separator+"photo";
            RecordVo paramvo = ConstantParamter.getRealConstantVo(DrConstant.BS_ASYN_PARAM_C);
            String siteJson = paramvo.getString("str_value");
            SiteBean siteBean = (SiteBean) gson.fromJson(siteJson, SiteBean.class);
            String nbases = siteBean.getNbase(); //获取上报人员库
            String[] nbaseArr = nbases.split(",");
            for (int k = 0; k <locorgcodeList.size() ; k++) {
                String locorgcode = locorgcodeList.get(k);
                for (int i = 0; i < nbaseArr.length; i++) {
                    String nbase = nbaseArr[i];
                    StringBuffer sqlstr = new StringBuffer();
                    sqlstr.append("select ext,ole,"+nbase+"A01.guidkey ");
                    sqlstr.append(" from "+nbase+"A00 left join "+nbase+"A01 on ");
                    sqlstr.append(nbase+"A00.A0100 = "+nbase+"A01.A0100 ");
                    sqlstr.append(" where "+nbase+"A01.b0110 LIKE '"+locorgcode+"%' AND FLAG = 'P' ");
                    if("2".equalsIgnoreCase(schemeType)){
                        whereSql = " and "+Sql_switcher.dateToChar(nbase+"a00.modtime", "yyyy-MM-dd hh24:mi")+">='"+dataStartTime+"'";
                        sqlstr.append(whereSql);
                    }
                    rs = dao.search("select count(1) as count from ("+sqlstr.toString()+") photo where guidkey in (select aid from V_ASYN_A01 WHERE (STATUS IS NULL OR STATUS = '') )");
                    if (rs.next()) {
                        count = rs.getInt("count");
                    }
                    int size = count%DrConstant.dataSize == 0 ? (count/DrConstant.dataSize) : (count/DrConstant.dataSize)+1;
                    for(int j = 1;j<size+1;j++){
                        String sql = "select *  from ("+sqlstr.toString()+") photo where guidkey in (select aid from V_ASYN_A01 WHERE (STATUS IS NULL OR STATUS = '') )";
                        rowSet = dao.search(sql+" order by guidkey desc",DrConstant.dataSize,j);
                        while (rowSet.next()) {
                            JSONObject json = new JSONObject();
                            String photoName = rowSet.getString("guidkey");
                            InputStream in = rowSet.getBinaryStream("ole");
                            String ext = rowSet.getString("ext");
                            json.put("file",photoName + ext);
                            dataJsonArray.add(json);
                            filePathlist.add(photoPath+File.separator+photoName + ext);
                            FileUtil.createPhotoFile(in,photoPath,photoName + ext);
                        }
                    }
                }
            }
            fileMap.put("filePath",filePathlist);
            fileMap.put("P",dataJsonArray);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(conn);
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(rowSet);
        }
        return fileMap;
    }

    /**
     * 生成数据包
     * @param schemeType
     * @param dataStartTime
     * @param table
     * @param unitcode
     * @return
     */
    private Map<String, Object> generateDataPackage(String schemeType, String dataStartTime, String table,String unitcode) {
        Map<String, Object> fileMap = new HashMap<String, Object>();
        Connection conn = null;
        try{
            conn = AdminDb.getConnection();
            fileMap = createDataFile(table, schemeType, dataStartTime,conn,unitcode);//创建数据文件
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(conn);
        }
        return fileMap;
    }

    /**
     * 创建json数据文件
     * @param setid
     * @param schemeType
     * @param dataStartTime
     * @param conn
     * @return
     */
    public Map <String,Object> createDataFile(String setid, String schemeType, String dataStartTime, Connection conn,String unitcode) {
        RowSet rs = null;
        int count = 0;
        Map<String, Object> fileMap = new HashMap<String, Object>();
        List <String> filePathlist = new ArrayList<String>();
        JSONArray dataJsonArray = new JSONArray();
        String whereSql = "";
        String sql = "";
        try{
            ContentDAO dao = new ContentDAO(conn);
            String firstTable = setid.substring(0, 1).toUpperCase();// A B K
            if("A01".equalsIgnoreCase(setid)){
                sql = "select count(1) as count from V_ASYN_"+setid+" where (STATUS IS NULL OR STATUS = '') and b0110 like '"+unitcode+"%'";
            }else if(!"A01".equalsIgnoreCase(setid)&&"A".equalsIgnoreCase(firstTable)){
                sql = "select count(1) as count from V_ASYN_"+setid+" where (STATUS IS NULL OR STATUS = '') and EXISTS ( select 1 from V_ASYN_a01 b where V_ASYN_"+setid+".emp_id = b.aid and b.b0110 like '"+unitcode+"%') ";
            }else if ("B".equalsIgnoreCase(firstTable)){
                sql = "select count(1) as count from V_ASYN_"+setid+" where (STATUS IS NULL OR STATUS = '') and b0110 like '"+unitcode+"%'";
            }else if ("K".equalsIgnoreCase(firstTable)){
                sql = "select count(1) as count from V_ASYN_"+setid+" where (STATUS IS NULL OR STATUS = '') and e01a1 like '"+unitcode+"%'";
            }
            if("2".equalsIgnoreCase(schemeType)){
                whereSql = " and "+Sql_switcher.dateToChar("modtime", "yyyy-MM-dd hh24:mi")+">='"+dataStartTime+"' ";
            }
            rs = dao.search(sql+whereSql);
            if (rs.next()) {
                count = rs.getInt("count");
            }
            int size = count%DrConstant.dataSize == 0 ? (count/DrConstant.dataSize) : (count/DrConstant.dataSize)+1;
            //1、获得json文件的存放路径
            ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
            String jsonPath = constantXml.getNodeAttributeValue("/filepath", "rootpath")+ File.separator+"asyn"+File.separator+"asynreport"+File.separator+"data";
            for(int i = 1;i<size+1;i++){
                JSONObject json = new JSONObject();
                JSONObject dataJson = getDataJson(setid,i,whereSql,conn,unitcode);
                String fileName = setid+"_"+i;
                FileUtil.createJsonFile(dataJson.toString(),jsonPath,fileName);
                filePathlist.add(jsonPath+File.separator+fileName+".json");
                json.put("file",fileName);
                dataJsonArray.add(json);
            }
            fileMap.put("filePath",filePathlist);
            fileMap.put(setid.substring(0,1).toUpperCase(),dataJsonArray);
        }catch (Exception e){
            e.printStackTrace();
            logger.write("分布同步：创建json数据文件失败"+e.toString());
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return fileMap;
    }

    /**
     * 获取数据json
     * @param setid
     * @param page
     * @param whereSql
     * @param conn
     * @return
     */
    private JSONObject getDataJson(String setid, int page, String whereSql, Connection conn,String unitcode) {
        RowSet rs = null;
        JSONObject dataJson = new JSONObject();
        String  sql = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            String firstTable = setid.substring(0, 1).toUpperCase();// A B K
            if("A01".equalsIgnoreCase(setid)){
                sql = "select * from V_ASYN_"+setid+" where (STATUS IS NULL OR STATUS = '') and b0110 like '"+unitcode+"%' "+whereSql+" order by aid desc";
            }else if(!"A01".equalsIgnoreCase(setid)&&"A".equalsIgnoreCase(firstTable)){
                sql = "select * from V_ASYN_"+setid+" where (STATUS IS NULL OR STATUS = '') and EXISTS ( select 1 from V_ASYN_a01 b where V_ASYN_"+setid+".emp_id = b.aid and b.b0110 like '"+unitcode+"%') "+whereSql+" order by aid desc ";
            }else if ("B".equalsIgnoreCase(firstTable)){
                sql = "select * from V_ASYN_"+setid+" where (STATUS IS NULL OR STATUS = '') and b0110 like '"+unitcode+"%' "+whereSql+" order by bid desc";
            }else if ("K".equalsIgnoreCase(firstTable)){
                sql = "select * from V_ASYN_"+setid+" where (STATUS IS NULL OR STATUS = '') and e01a1 like '"+unitcode+"%' "+whereSql+" order by kid desc";
            }
            rs = dao.search(sql,DrConstant.dataSize,page);
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            JSONArray dataJsonArray = new JSONArray();
            while (rs.next()) {
                JSONObject json = new JSONObject();
                for (int i = 0; i < count; i++) {
                    String colName = meta.getColumnName(i + 1).toUpperCase();
                    String colType = meta.getColumnTypeName(i + 1);
                    if ("DATE".equalsIgnoreCase(colType)||"DATETIME".equalsIgnoreCase(colType)) {
                        String date = PubFunc.FormatDate(rs.getTimestamp(colName),"yyyy-MM-dd HH:mm:ss");
                        json.put(colName,date);
                    } else if ("CLOB".equalsIgnoreCase(colType)) {
                        String colValue = rs.getString(colName) ==null?"": rs.getString(colName).replaceAll("\\[","（").replaceAll("\\]","）").replaceAll("\\{","（").replaceAll("\\}","）");
                        json.put(colName,colValue.replaceAll(",","，"));
                    } else {
                        String colValue = rs.getObject(colName) ==null?"": rs.getObject(colName).toString().replaceAll("\\[","（").replaceAll("\\]","）").replaceAll("\\{","（").replaceAll("\\}","）");
                        json.put(colName,colValue.replaceAll(",","，"));
                    }
                }
                json.remove("RN");
                json.remove("STATUS");
                dataJsonArray.add(json);
            }
            dataJson.put(setid,dataJsonArray);
            logger.write("分布同步：获取V_ASYN_"+setid+"视图的json数据成功！");
        }catch (Exception e){
            e.printStackTrace();
            logger.write("分布同步：获取V_ASYN_"+setid+"视图的json数据失败！"+e.toString());
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return dataJson;
    }
}
