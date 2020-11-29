/**   
 * @Title: ImportZipDataTrans.java 
 * @Package com.hjsj.hrms.transaction.hire.employActualize.employResume 
 * @Description: TODO
 * @author xucs
 * @date 2014-7-11 下午05:59:09 
 * @version V1.0   
 */
package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.EmployResumeZipBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.struts.upload.FormFile;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName: ImportZipDataTrans
 * @Description: 导入应聘简历zip格式数据
 * @author xucs
 * @date 2014-7-11 下午05:59:09
 * 
 */
public class ImportZipDataTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String sure = (String) hm.get("sure");
        hm.remove("sure");
        EmployResumeZipBo bo = new EmployResumeZipBo(this.getFrameconn(), this.userView);
        Workbook wb = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        ZipInputStream zs = null;
        try {
            if (sure == null) {// 点击上传按钮
                FormFile form_file = (FormFile) this.getFormHM().get("file");// 得到上传的文件
                String name = form_file.getFileName();
                String filePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + name;
                boolean isTypeEqual=FileTypeUtil.isFileTypeEqual(form_file);
                if(!isTypeEqual){
                	throw new GeneralException(ResourceFactory.getProperty("error.fileuploaderror"));
                }
                // 上传zip包到服务器
                inputStream = form_file.getInputStream();
                outputStream = new FileOutputStream(filePath);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                // 上传end
                
                ZipFile zipFile = new ZipFile(filePath); // 根据路径取得需要解压的Zip文件
                List fileHeaderList = zipFile.getFileHeaders();
                for (int i = 0; i < fileHeaderList.size(); i++) {// 从zip文件中取出 主要的Excel文件
                    FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                    // System.out.println(fileHeader.getFileName());
                    if (fileHeader.isDirectory() || fileHeader.getFileName().startsWith("multimedia/"))// 文件夹以及里面的文件，暂时不需要
                        continue;
                    String fileName = "";
                    String extention = "";
                    if (fileHeader.getFileName().length() > 0 && fileHeader.getFileName() != null) { // --截取文件名
                        int j = fileHeader.getFileName().lastIndexOf(".");
                        int k = fileHeader.getFileName().lastIndexOf("/");
                        if (j > -1 && j < fileHeader.getFileName().length() && !fileHeader.isDirectory()) {
                            fileName = fileHeader.getFileName().substring(k + 1, j); // --文件名
                            extention = fileHeader.getFileName().substring(j + 1); // --扩展名
                        }
                    }
                    if (extention.trim().length() > 0 && "xls".equalsIgnoreCase(extention) && fileName.length() > 0 && fileName.startsWith("T_")) {// 得到对应的excel文件
                        zipFile.setPassword("hjsj2013");
                        zs = zipFile.getInputStream(fileHeader);
                        wb = WorkbookFactory.create(zs);// 创建excel
                        ArrayList returnList = bo.relativeExcel(wb);// 得到返回的提示信息list
                        
                        ArrayList inforNotExistList = (ArrayList) returnList.get(0);//用与前台提示信息：指标不存在
                        ArrayList inforNotFormatList = (ArrayList) returnList.get(1);//用于前台提示信息：指标类型不一致
                        ArrayList itemNotInList = (ArrayList) returnList.get(2);//存放的是不存在的指标的itemId
                        ArrayList setNotInList=(ArrayList) returnList.get(3);//存放的是指标集不存在的指标集名称
                        ArrayList setNotExistList=(ArrayList) returnList.get(4);//用与前台提示信息：指标集不存在
                        
                        if (setNotExistList.size()==0&&inforNotExistList.size() == 0 && inforNotFormatList.size() == 0) {// 所有的字段都没有问题
                            // 向数据库中导入数据
                            String[] count = bo.importData(wb, fileHeaderList, zipFile,new ArrayList(),new ArrayList());
                            this.getFormHM().put("importZipData", "1");
                            this.getFormHM().put("zipRecordcout", count[0]);
                            this.getFormHM().put("importcount", count[1]);
                        } else {
                            this.getFormHM().put("inforNotExistList", inforNotExistList);
                            this.getFormHM().put("inforNotFormatList", inforNotFormatList);
                            this.getFormHM().put("itemNotInList", itemNotInList);
                            this.getFormHM().put("setNotInList", setNotInList);
                            this.getFormHM().put("setNotExistList", setNotExistList);
                            this.getFormHM().put("importZipData", "0");
                            this.getFormHM().put("zipdataFilepath", filePath);
                        }
                        break;
                    } else {
                        throw new GeneralException("缺少必要的Excel文件,请检查上传文件");
                    }
                }
            } else {
                    String filePath = (String) this.getFormHM().get("zipdataFilepath");
                    ArrayList itemNotInList = (ArrayList) this.getFormHM().get("itemNotInList");
                    ArrayList setNotInList= (ArrayList) this.getFormHM().get("setNotInList");
                    ZipFile zipFile = new ZipFile(filePath); // 根据路径取得需要解压的Zip文件
                    List fileHeaderList = zipFile.getFileHeaders();
                    for (int i = 0; i < fileHeaderList.size(); i++) {// 从zip文件中取出 主要的Excel文件
                        FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                        if (fileHeader.isDirectory() || fileHeader.getFileName().startsWith("multimedia/"))// 文件夹以及里面的文件，暂时不需要
                            continue;
                        String fileName = "";
                        String extention = "";
                        if (fileHeader.getFileName().length() > 0 && fileHeader.getFileName() != null) { // --截取文件名
                            int j = fileHeader.getFileName().lastIndexOf(".");
                            int k = fileHeader.getFileName().lastIndexOf("/");
                            if (j > -1 && j < fileHeader.getFileName().length() && !fileHeader.isDirectory()) {
                                fileName = fileHeader.getFileName().substring(k + 1, j); // --文件名
                                extention = fileHeader.getFileName().substring(j + 1); // --扩展名
                            }
                        }
                        if (extention.trim().length() > 0 && "xls".equalsIgnoreCase(extention) && fileName.length() > 0 && fileName.startsWith("T_")) {// 得到对应的excel文件
                            zipFile.setPassword("hjsj2013");
                            zs = zipFile.getInputStream(fileHeader);
                            wb = WorkbookFactory.create(zs);// 创建excel
                            String[] count = bo.importData(wb, fileHeaderList, zipFile,itemNotInList,setNotInList);
                            this.getFormHM().put("importZipData", "1");
                            this.getFormHM().put("zipRecordcout", count[0]);
                            this.getFormHM().put("importcount", count[1]);
                            break;
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally{
            try{
                PubFunc.closeResource(outputStream);
                PubFunc.closeResource(inputStream);
                PubFunc.closeResource(zs);
               
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }
    }

}
