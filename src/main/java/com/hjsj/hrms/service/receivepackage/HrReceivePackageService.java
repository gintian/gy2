package com.hjsj.hrms.service.receivepackage;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.DrConstant;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.module.system.distributedreporting.reportdata.ReportDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @Titile: HrReceivePackageService
 * @Description: 接收数据包接口
 * @Company: hjsj
 * @Create time: 2019年8月2日14:13:10
 * @author: duxl
 * @version 1.0
 *
 */
public class HrReceivePackageService extends HttpServlet {
    public HrReceivePackageService() {
        super();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        InputStream inputStream = null;
        try{
            conn = AdminDb.getConnection();
            request.setCharacterEncoding("GBK");
            String hashValue = request.getHeader("Hash");
            String finish = request.getHeader("Finish");//分卷完成标识
            inputStream = request.getInputStream();
            if("false".equalsIgnoreCase(finish)) {
                String fileName = request.getParameter("fileName");
                String result = this.receiveSplitZip(inputStream,fileName,hashValue,conn);
                this.returnResponse(response,result);
            }else{
                String info = IOUtils.toString(inputStream, "gbk");
                String result = this.receiveFinishMseeage(conn,info);
                this.returnResponse(response,result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(conn);
            PubFunc.closeIoResource(inputStream);
        }
    }

    private String receiveSplitZip(InputStream inputStream, String fileName, String hashValue, Connection conn) {
        String returnCode = "false";
        try{
            ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
            String unitcode = fileName.split("_")[0].substring(2);
            String zipPath= constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"asynrecive"+File.separator
                    + unitcode+File.separator+"zip"+File.separator+"cache";
            boolean flag = saveDataPackage(inputStream,zipPath,fileName);
            if(flag){
                boolean checkCode = checkHashValue(zipPath,fileName,hashValue);
                if(checkCode){
                    return returnJsonStr("1","接收数据包成功");
                }else{
                    FileUtil.deleteFile(zipPath+File.separator+fileName);
                    return returnJsonStr("0","数据包哈希值校验失败");
                }
            }else{
                return returnJsonStr("0","接收数据包失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnJsonStr("0",returnCode);
    }

    /**
     * 校验文件哈希值
     * @param zipPath
     * @param fileName
     * @param hashValue
     * @return
     */
    private boolean checkHashValue(String zipPath, String fileName, String hashValue) {
        FileInputStream in = null;
        try{
            File file = new File(zipPath+File.separator+fileName);
            in = new FileInputStream(file);
            String hashCode = DigestUtils.md5Hex(in).toUpperCase();
            if(hashCode.equals(hashValue)){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeIoResource(in);
        }
        return false;
    }

    /**
     * 接收数据包
     * @param conn
     * @param info
     * @return
     */
    private String receiveFinishMseeage(Connection conn, String info) {
        String destDir = null;
        String returnCode = "数据包校验失败";
        try{
            UserView userView = new UserView("su", conn);
            userView.canLogin(false);
            ReportDataBo bo = new ReportDataBo(userView, conn);
            ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
            JSONObject infoJson = JSONObject.fromObject(info);
            boolean message = infoJson.getBoolean("message");
            String fileName = infoJson.getString("fileName");
            if(StringUtils.isEmpty(fileName)){
                return returnJsonStr("0","数据包名称为空！");
            }
            if (!fileName.startsWith("DT")||fileName.indexOf("_")==-1||!fileName.endsWith(".zip")) {
                return returnJsonStr("0", ResourceFactory.getProperty("dr_checkdadazip.name"));//数据包名称不符合要求
            }
            String unitcode = fileName.split("_")[0].substring(2);
            String filePath = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"asynrecive"+File.separator
                    + unitcode+File.separator;
            String zipPath= filePath+"zip"+File.separator+"cache";
            if(message){
                destDir = filePath+"cache";
                FileUtil.decryptZipOneFile(zipPath+File.separator+fileName,"idx.json", DrConstant.ZIP_PASSWORD, destDir);
                //2、判断idx.json文件是否存在
                File idxFile = new File(destDir+File.separator+"idx.json");
                if (!idxFile.exists()) {
                    return returnJsonStr("0", ResourceFactory.getProperty("dr_checkdadazip.idxfile"));
                }
                //3、读取idx.json的内容
                String idsData = FileUtils.readFileToString(new File(destDir+File.separator+"idx.json"), "GBK");
                String result = bo.checkIdsJson(idsData,fileName);//校验idx.json内的内容是否符合要求
                if("success".equals(result)){
                    List<String> fileNameList = FileUtil.getFile(zipPath);
                    for(int i=0;i<fileNameList.size();i++){
                        FileUtil.moveTo(zipPath+File.separator+fileNameList.get(i),filePath+File.separator+"zip");
                    }
                    String path = "asyn"+File.separator+"asynrecive"+File.separator+ unitcode+File.separator+"zip"+File.separator+fileName;
                    bo.saveDataRecord(idsData,path,destDir);
                    return returnJsonStr("1","校验数据包成功");
                }else {
                    returnCode = result;
                    FileUtil.deleteFile(filePath);
                }
            }else{
                FileUtil.deleteFile(filePath);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (null!=destDir) {
                //6、无论校验通过成功或未通过把临时文件删除
                FileUtil.delfile(destDir, "idx.json");
            }
        }
        return returnJsonStr("0",returnCode);
    }

    /**
     * 保存数据包到指定目录
     * @param inputStream
     * @param zipPath
     * @param fileName
     * @throws Exception
     */
    private boolean saveDataPackage(InputStream inputStream, String zipPath, String fileName) throws Exception{
        boolean flag = true;
        FileOutputStream fs  = null;
        try{
            File newFile = new File(zipPath) ;
            if (!newFile.exists()) {
                newFile.mkdirs();
            }
            fs  =  new  FileOutputStream(zipPath+File.separator+fileName);
            write2Out(inputStream ,fs) ;
        }catch (Exception e){
            flag = false;
            e.printStackTrace();
        }finally {
            PubFunc.closeIoResource(fs);
        }
        return flag;
    }

    /**
     * 返回json格式的数据
     * @param response 响应对象
     * @param msg 选项值 0:错误信息,1:数据信息
     */
    private void returnResponse(HttpServletResponse response, String msg) {
        //设置编码格式
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("GBK");
        PrintWriter out = null;
        try{
            out = response.getWriter();
            out.write(msg);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    private static void write2Out(InputStream input , OutputStream out) throws IOException {
        byte[] b = new byte[1024];
        int c = 0 ;
        while ( (c = input.read(b)) != -1 ) {
            out.write(b,0,c);
            out.flush();
        }
        out.close();
    }
    /**
     * 返回json格式的字符串
     * @param opt 选项值 0:失败,返回错误信息,1:成功,返回数据信息,2:成功(请求成功,但没有数据信息返回)
     * @param msg 返回的信息
     */
    private String returnJsonStr(String opt,Object msg) {
        JSONObject json= new JSONObject();
        if("0".equals(opt)) {
            json.put("flag",opt);
            json.put("msg",msg);
        }else if("1".equals(opt)) {
            json.put("flag",opt);
            json.put("msg",msg);
        }
        return json.toString();
    }

}
