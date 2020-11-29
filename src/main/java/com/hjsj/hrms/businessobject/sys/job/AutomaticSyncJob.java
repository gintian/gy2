package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.DrConstant;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.FtpUtilBo;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.PackageReportThread;
import com.hjsj.hrms.module.system.distributedreporting.reportdata.ReportDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.util.*;

/**
 * <p>
 * Title:AutomaticSyncJob
 * </p>
 * <p>
 * Description:后台作业中执行的类，将中间库的数据同步到人力资源系统中
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2018-10-9 13:44:07
 * </p>
 *
 */
public class AutomaticSyncJob implements Job {
    private Category log = Category.getInstance(this.getClass().getName());
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            checkFtpDataZip(conn);
            PackageReportThread packageReportThread =new PackageReportThread(null);
            Thread thread = new Thread(packageReportThread);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(conn);
        }
    }

    /**
     * 检查FTP上传的数据包
     * @param conn
     */
    private void checkFtpDataZip(Connection conn) {
        String destDir = null;
        String errorInfo = null;
        FtpUtilBo ftpUtilBo = null;
        try{
            UserView userView = new UserView("su", conn);
            userView.canLogin(false);
            ReportDataBo bo = new ReportDataBo(userView, conn);
            ArrayList<Map> schemeParamList = getSchemeParamList(conn);
            for(Map schemeParam : schemeParamList){
                String ip = (String) schemeParam.get("ip");
                String user = (String) schemeParam.get("user");
                String password = (String) schemeParam.get("password");
                String port = (String) schemeParam.get("port");
                String dataPath = (String) schemeParam.get("datapath");
                ftpUtilBo = new FtpUtilBo(ip,Integer.parseInt(port),user,password,dataPath);
                List<String> fileNameList = ftpUtilBo.getFileNameList();
                Collections.sort(fileNameList);
                for (int i = 0; i <fileNameList.size() ; i++) {
                    ftpUtilBo = new FtpUtilBo(ip,Integer.parseInt(port),user,password,dataPath);
                    String fileName = fileNameList.get(i);
                    if (!fileName.startsWith("DT")||fileName.indexOf("_")==-1||!fileName.endsWith(".zip")) {
                        //数据包名称不符合要求
                        errorInfo = ResourceFactory.getProperty("dr_checkdadazip.name");
                        log.error("分布同步："+ fileName+errorInfo);
                        continue;
                    }
                    String unitcode = fileName.split("_")[0].substring(2);
                    ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
                    //1、解压idx.json文件
                    destDir = constantXml.getNodeAttributeValue("/filepath", "rootpath")+ File.separator+"asyn"+File.separator+"asynrecive"+File.separator
                            + unitcode+File.separator+"cache";
                    String zipPath = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"asynrecive"+File.separator
                            + unitcode+File.separator+"zip";
                    boolean flag = ftpUtilBo.downloadFile(fileName,zipPath);
                    if(!flag){
                        errorInfo = ResourceFactory.getProperty("dr_checkdadazip.error");
                        log.error("分布同步："+ errorInfo);
                        continue;
                    }
                    FileUtil.decryptZipOneFile(zipPath+File.separator+fileName,"idx.json", DrConstant.ZIP_PASSWORD, destDir);
                    //2、判断idx.json文件是否存在
                    File idxFile = new File(destDir+File.separator+"idx.json");
                    if (!idxFile.exists()) {
                        errorInfo = ResourceFactory.getProperty("dr_checkdadazip.idxfile");
                        log.error("分布同步："+ errorInfo);
                        continue;
                    }
                    //3、读取idx.json的内容
                    String idsData = FileUtils.readFileToString(new File(destDir+File.separator+"idx.json"), "GBK");
                    //校验idx.json内的内容是否符合要求
                    String result = bo.checkIdsJson(idsData,fileName);
                    if ("success".equals(result)) {
                        ftpUtilBo.deleteFile(dataPath,fileName);
                        String path = "asyn"+File.separator+"asynrecive"+File.separator+ unitcode+File.separator+"zip"+File.separator+fileName;
                        //5、保存文件成功后将记录添加数据库。
                        bo.saveDataRecord(idsData,path,destDir);
                    }else {
                        FileUtil.deleteFile(zipPath+File.separator+fileName);
                        errorInfo = result;
                        log.error("分布同步："+ errorInfo);
                        continue;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("分布同步：检查上传的FTP包失败！");
        }finally {
            if(ftpUtilBo!=null){
                ftpUtilBo.closeConnect();
            }
        }
    }

    /**
     * 获取Ftp文件存放路径
     * @return
     * @param conn
     */
    private ArrayList<Map> getSchemeParamList(Connection conn) {
        ArrayList<Map> schemeParamList = new ArrayList();
        RowSet rs = null;
        XPath xpath;
        try{
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search("select schemeparam from t_sys_asyn_scheme where state = 1");
            while (rs.next()) {
                Map schemeParamMap = new HashMap();
                String schemeParam = rs.getString("schemeparam");
                Document doc = PubFunc.generateDom(schemeParam);
                xpath = XPath.newInstance("/scheme/import_type");
                String importType = xpath.valueOf(doc);
                if("2".equals(importType)){
                    xpath = XPath.newInstance("scheme/ftp");
                    Element el = (Element) xpath.selectSingleNode(doc);
                    schemeParamMap.put("user",el.getAttributeValue("user"));
                    schemeParamMap.put("password",el.getAttributeValue("password"));
                    schemeParamMap.put("ip",el.getAttributeValue("ip"));
                    schemeParamMap.put("port",el.getAttributeValue("port"));
                    schemeParamMap.put("datapath",el.getAttributeValue("datapath"));
                    schemeParamList.add(schemeParamMap);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("分布同步：获取FTP路径失败！");
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return schemeParamList;
    }
}
