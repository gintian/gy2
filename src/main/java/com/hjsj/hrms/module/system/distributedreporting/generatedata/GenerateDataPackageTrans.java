package com.hjsj.hrms.module.system.distributedreporting.generatedata;

import com.google.gson.Gson;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.DataGenerateThread;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.DrConstant;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.DrLogger;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.module.system.distributedreporting.generatedata.generatedatabean.MenusBean;
import com.hjsj.hrms.module.system.distributedreporting.generatedata.generatedatabean.SiteBean;
import com.hjsj.hrms.module.system.distributedreporting.generatedata.generatedatabean.SuperorgBean;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class GenerateDataPackageTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException { 
        Gson gson = new Gson();
        String schemeType = (String) this.formHM.get("schemeType");//1为全量；2为增量
        String dataStartTime = (String) this.formHM.get("dataStartTime");//增量起始时间
        GenerateDataBo gdataBo = new GenerateDataBo(userView, frameconn);
        String startTime = "";
        List sendinfoList = new ArrayList();
        String uuid = UUID.randomUUID().toString();
        String orgType = "";
        List<String> locorgCodeList = new ArrayList<String>();
        try{
            ConstantXml constantXml = new ConstantXml(this.frameconn, "FBTB_FILEPATH");
            RecordVo paramvo = ConstantParamter.getRealConstantVo(DrConstant.BS_ASYN_PARAM_C);
            String siteJson = paramvo.getString("str_value");
            SiteBean siteBean = (SiteBean) gson.fromJson(siteJson, SiteBean.class);
            String reportPhoto = siteBean.getReport_photo();//是否上班照片，1为是
            String importType = "0";//上报方式：0 手工上报，1 中间库，2 FTP，3 webservices
            String locorgCode = siteBean.getLocorgcode();//本单位编码
            if(!locorgCode.contains("`")) {//不存在则默认加上分隔符
                locorgCode = "1`"+locorgCode;
            }
            orgType = locorgCode.split("`")[0];// 1为单机构；2为多机构
            String orgIds = locorgCode.split("`")[1];
            String[] orgIdArr = orgIds.split(",");
            locorgCodeList = Arrays.asList(orgIdArr);
            SuperorgBean superorg = siteBean.getSuperorg();
            String unitcode = superorg.getUnitcode();//上级单位编码
            String unitname = superorg.getUnitname();//上级单位名称
            String unitguid = superorg.getUnitguid();//上级单位guidkey
            RecordVo planvo = ConstantParamter.getRealConstantVo(DrConstant.BS_ASYN_PLAN_C);
            String menuJson = planvo.getString("str_value");
            MenusBean menusBean = (MenusBean) gson.fromJson(menuJson, MenusBean.class);
            String psnStatus = menusBean.getPsn_status();
            String nbases = siteBean.getNbase(); //获取上报人员库
            String[] nbaseArr = nbases.split(",");
            Date date = new Date();
            DrLogger drLogger  = new DrLogger(locorgCodeList.get(0),0,date);
            String endTime = PubFunc.FormatDate(date, "yyyyMMddHHmmssSSS");//数据上报截止时间
            //下级对应上的表（上级下发的同步子集名称）
            ArrayList <Map<String, String>> list = gdataBo.getTableList();
            if("1".equalsIgnoreCase(schemeType)){
                gdataBo.deleteJZTable(list,drLogger);
            }else{
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                startTime = PubFunc.FormatDate(dateFormat.parse(dataStartTime), "yyyyMMddHHmmssSSS");
            }
            //创建组织机构基准表
            gdataBo.createOrgJZTable(drLogger);
            //插入组织机构数据到基准表
            gdataBo.insertOrgToJZTable(orgType,locorgCodeList, unitcode,drLogger,dataStartTime,schemeType);
            ArrayList <String> mappingList = gdataBo.getMappingList(drLogger);
            if(!mappingList.contains("B01")){
                gdataBo.createViewOrg("B01","b0110");
                drLogger.write("分布同步：创建V_ASYN_B01视图成功！");
            }
            if(!mappingList.contains("K01")){
                gdataBo.createViewOrg("K01","e01a1");
                drLogger.write("分布同步：创建V_ASYN_K01视图成功！");
            }
            for (Map<String, String> tableMap : list) {
                String tableSet1 = tableMap.get("set1");//上级需要上传的子集
                String tableSet2 = tableMap.get("set2");//下级对应的子集或视图
                String firstTable = tableSet2.substring(0, 1).toUpperCase();//A B K V
                List<Map<String, Object>> fieldList = gdataBo.getStandardField(tableSet1,drLogger);//获取代码标准的对应字段
                String alertColunm = gdataBo.createStandardTable(fieldList, tableSet1,drLogger);//创建或更新基准表
                gdataBo.createView(tableSet1,drLogger);//创建视图
                String columnsSql = gdataBo.getColumnsSql(tableSet1,drLogger);//获取需要插入的字段
                //人员
                if ("A".equalsIgnoreCase(firstTable)) {
                    for (int i = 0; i < nbaseArr.length; i++) {
                        String nbase = nbaseArr[i];
                        //增加GUIDKEY字段，并且插入
                        gdataBo.addGuidKeyFieldAndInsert(tableSet2, nbase,drLogger);
                        //更新新增的字段到基准表
                        gdataBo.updateAlertColunm(nbase,tableSet1,tableSet2,alertColunm,drLogger);
                        //按照所选单位插入数据到基准表
                        gdataBo.insertDataToJZTable(nbase, columnsSql, tableSet1,tableSet2, locorgCodeList,drLogger);
                        //比较modtime更新数据到基准表
                        gdataBo.updateDataToJZTable(nbase, columnsSql, tableSet1,tableSet2,drLogger);
                    }
                    //增量时判断删除数据
                    if("2".equalsIgnoreCase(schemeType)){
                        //比较guidkey是否存在，不存在的为删除内容
                        gdataBo.deleteDataToJZTable(nbaseArr,tableSet1,tableSet2,drLogger,dataStartTime);
                    }
                }
                //单位岗位
                if ("B".equalsIgnoreCase(firstTable) || "K".equalsIgnoreCase(firstTable)) {
                    if(!tableSet1.contains("01")){
                        //增加GUIDKEY字段，并且插入
                        gdataBo.addGuidKeyFieldAndInsert(tableSet2, "",drLogger);
                        //新增、更新BK子集数据
                        gdataBo.insertSubsetBKDataToJZTable(columnsSql, tableSet1,tableSet2, locorgCodeList,orgType,drLogger);
                    }else{
                        //新增、更新BK主集数据
                        gdataBo.insertBKDataToJZTable(columnsSql, tableSet1,tableSet2, locorgCodeList,orgType,drLogger);
                    }
                    if("2".equalsIgnoreCase(schemeType)) {
                        //增量时判断删除数据
                        gdataBo.deleteBKVDataToJZTable(tableSet1,tableSet2,drLogger,dataStartTime);
                    }
                    //删除机构、岗位无头数据
                    gdataBo.deleteBKStaleData(tableSet1,drLogger);
                    //按上级代码转换[b0110、e01a1]
                    gdataBo.transBKJZTableCode(tableSet1, unitcode, orgType , locorgCodeList,drLogger);
                }
                if("V".equalsIgnoreCase(firstTable)){
                    //视图人员子集
                    gdataBo.asynViewDataTOJZTable(columnsSql, tableSet1,tableSet2, locorgCodeList,drLogger);
                    if("2".equalsIgnoreCase(schemeType)) {
                        //增量时判断删除数据
                        gdataBo.deleteBKVDataToJZTable(tableSet1,tableSet2,drLogger,dataStartTime);
                    }
                }
                //获取所有需要转换的代码项
                List<Map<String, String>> codeSetList = gdataBo.getCodeSetList(tableSet1,drLogger);
                //根据对应转换代码
                gdataBo.transCodeItem(codeSetList, tableSet1,drLogger);
                //人员岗位对应
                gdataBo.transOrganizationCode(tableSet1, unitcode,locorgCodeList,orgType,drLogger);
            }
            if(StringUtils.isNotEmpty(psnStatus)){
                //过滤人员状态指标为空的数据
                gdataBo.filterPsnStatusData(list,drLogger,uuid,psnStatus);
            }
            //过滤子集记录过滤条件的数据(改为写入中间库之前过滤掉)
            //gdataBo.filterRecordCondition(list,locorgCodeList.get(0),drLogger,uuid);
            //过滤必填和唯一数据
            gdataBo.filterMustbeAndUniqueData(menusBean,list,drLogger,uuid);
            //过滤校验规则数据
            gdataBo.filterValidateRules(list,drLogger,uuid);
            if("1".equalsIgnoreCase(reportPhoto)){
                mappingList.add("PHOTO");
            }
            if(!mappingList.contains("B01")){
                mappingList.add("B01");
            }
            if(!mappingList.contains("K01")){
                mappingList.add("K01");
            }
            // 创建一个线程池
            ExecutorService pool = Executors.newFixedThreadPool(mappingList.size());
            List<Future<Map>> resultFultterList = new ArrayList<Future<Map>>();
            for(int i=0;i<mappingList.size();i++){
                String table =  mappingList.get(i);
                DataGenerateThread c1 = new DataGenerateThread(schemeType,dataStartTime,table,locorgCodeList,unitcode,date);
                resultFultterList.add(pool.submit(c1));
            }
            // 关闭线程池
            pool.shutdown();
            List<String> filePathList = new ArrayList();
            JSONArray aFileList = new JSONArray();
            JSONArray bFileList = new JSONArray();
            JSONArray kFileList = new JSONArray();
            JSONArray pFileList = new JSONArray();
            for (Future<Map> future : resultFultterList) {
                Map<String, Object> fileMap = future.get();
                JSONArray aFileArray = (JSONArray) fileMap.get("A");
                JSONArray bFileArray = (JSONArray) fileMap.get("B");
                JSONArray kFileArray = (JSONArray) fileMap.get("K");
                JSONArray pFileArray = (JSONArray) fileMap.get("P");
                List<String> filePath = (List<String>) fileMap.get("filePath");//文件路径
                filePathList.addAll(filePath);
                aFileList.addAll(aFileArray);
                bFileList.addAll(bFileArray);
                kFileList.addAll(kFileArray);
                pFileList.addAll(pFileArray);
            }
            JSONObject idxjson = new JSONObject();
            idxjson.put("orgid", unitguid);
            idxjson.put("orgcode", unitcode);
            idxjson.put("orgname", unitname);
            idxjson.put("logid", uuid.toUpperCase());
            idxjson.put("asyntype", schemeType);
            if ("1".equalsIgnoreCase(schemeType)) {
                idxjson.put("incstarttime", endTime);
            } else {
                idxjson.put("incstarttime", startTime);
            }
            idxjson.put("endtime", endTime);
            idxjson.put("A", aFileList);
            idxjson.put("B", bFileList);
            idxjson.put("K", kFileList);
            idxjson.put("P", pFileList);
            gdataBo.deleteViewTable(list);
            String idxjsonPath = constantXml.getNodeAttributeValue("/filepath", "rootpath") + File.separator + "asyn" + File.separator + "asynreport" + File.separator + "data";
            FileUtil.createJsonFile(idxjson.toString(), idxjsonPath, "idx");
            filePathList.add(idxjsonPath + File.separator + "idx.json");
            String zipPath = constantXml.getNodeAttributeValue("/filepath", "rootpath") + File.separator + "asyn" + File.separator + "asynreport" + File.separator + "zip";
            String zipName = "DT" + unitcode + "_" + endTime + ".zip";
            String pkgPath = "asyn" + File.separator + "asynreport" + File.separator + "zip" + File.separator + zipName;
            //生成数据包，放在服务器
            FileUtil.createEncrypZip(zipPath+ File.separator + zipName, filePathList, DrConstant.ZIP_PASSWORD);
            String photoPath = constantXml.getNodeAttributeValue("/filepath", "rootpath") + File.separator + "asyn" + File.separator + "asynreport" + File.separator + "data"+ File.separator +"photo";
            FileUtil.deleteFile(idxjsonPath);
            FileUtil.deleteFile(photoPath);
            //gdataBo.updateJzTableModstate(list,unitcode,schemeType,dataStartTime);
            sendinfoList.add(gdataBo.getMaxId("t_sys_asyn_sendinfo"));
            sendinfoList.add(uuid);
            sendinfoList.add(locorgCodeList.get(0));
            sendinfoList.add(Integer.parseInt(schemeType));
            sendinfoList.add(Integer.parseInt(importType));
            sendinfoList.add(DateUtils.getTimestamp(date));
            sendinfoList.add("包生成成功");
            sendinfoList.add(pkgPath);
            gdataBo.updateSendinfo("insert into t_sys_asyn_sendinfo (ID,GUIDKEY,UNITCODE,DataType,sendType,SENDTIME,STATUS,pkgpath) values(?,?,?,?,?,?,?,?) ", sendinfoList);
            this.getFormHM().put("flag", true);
        }catch (Exception e){
            this.getFormHM().put("flag", false);
            gdataBo.updateSendinfo("update t_sys_asyn_sendinfo set STATUS =  '包生成失败',SITUATION = '" + e.getMessage() + "' WHERE GUIDKEY = '" + uuid + "'", new ArrayList());
            e.printStackTrace();
        }
    }

}
