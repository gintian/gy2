package com.hjsj.hrms.module.recruitment.interfaces;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.PrintResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.*;

/**
 * 简历接口类
 * <p>
 * Title: ResumeInterface
 * </p>
 * <p>
 * Description: 负责与外部模块协调简历的导入导出等功能
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2016-5-3 上午09:36:07
 * </p>
 * 
 * @author zhaoxj
 * @version 1.0
 */
public class ResumeInterface {
    private Connection conn;
    private UserView userView;
    // 是否删除上传的zip文件，默认为=true ： 删除， =false：不删
    private boolean isdeleteZip = true;
    //是否忽略异常信息：=0 不忽略；=1 忽略； 默认为0
    private String ignoreExceptions = "0";

    private ResumeInterface() {

    }

    public ResumeInterface(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    public ResumeInterface(Connection conn, UserView userView, boolean isdeleteZip, String ignoreExceptions) {
        this.conn = conn;
        this.userView = userView;
        this.isdeleteZip = isdeleteZip;
        this.ignoreExceptions = ignoreExceptions;
    }

    /**
     * 导出empList中包含的人员简历数据到zipFileName文件
     * 
     * @Title: ExportResumeZip
     * @Description: 导出empList中包含的人员简历数据到zipFileName文件
     * @param empList
     *            要导出的人员列表，“Zpt00000001”，“Zpt00000009”...
     * @return 导出成功返回zip文件名（包含路径），导出失败则抛异常，将失败信息抛出
     */
    public String ExportResumeZip(ArrayList<String> empList) throws GeneralException {
        String zipFileName = userView.getUserName() + "_resume.zip";
        ZipFile zipFile = null;
        InputStream in = null;
        try {
            ResumeZipBo bo = new ResumeZipBo(this.conn, this.userView);
            ArrayList<String> nbaseList = new ArrayList<String>();
            HashMap<String, String> a0100Map = new HashMap<String, String>();
            for (int i = 0; i < empList.size(); i++) {
                String empId = empList.get(i);
                if (StringUtils.isEmpty(empId))
                    continue;

                String nbase = empId.substring(0, 3);
                String a0100 = empId.substring(3);
                if (a0100Map.containsKey(nbase)) {
                    String a0100s = a0100Map.get(nbase);
                    a0100s += "," + a0100;
                    a0100Map.put(nbase, a0100s);
                } else {
                    a0100Map.put(nbase, a0100);
                    nbaseList.add(nbase);
                }
            }
            // 参数太多了 ，封装成对象传过去
            ArrayList paramList = new ArrayList();
            paramList.add(nbaseList);
            paramList.add(a0100Map);

            ArrayList DataList = bo.getResumeExcelSqlAndColunm(paramList);// 包含一个ArrayList和一个HashMap  第一个ArrayList 是要导出的人员库
                                                                          // 第二个map key指人员库 value是对应的人员库的中人员编号
            // 得到Excel的名字和各个附件的名字
            ArrayList nameList = bo.createExcel(DataList, nbaseList.get(0));

            String fileindex = System.getProperty("java.io.tmpdir") + File.separator;
            File tempfile = new File(fileindex + zipFileName);
            // 先判断zip文件是否存在如果存在删除该zip文件
            if (tempfile.exists()) 
                tempfile.delete();
            // 生成的zip文件
            zipFile = new ZipFile(fileindex + zipFileName); 
            
            // 向zip包中添加文件集合
            ArrayList<File> fileAddZip = new ArrayList<File>(); 
            // 存放文件名称，在zip文件生成完成以后将这些文件删除
            ArrayList<String> filenameList = new ArrayList<String>();
            String attactNames = (String) nameList.get(1);
            if (attactNames.trim().length() > 0) {
                String[] attactNameArr = attactNames.split(",");
                for (int i = 0; i < attactNameArr.length; i++) {
                    String attactName = attactNameArr[i];
                    fileAddZip.add(new File(fileindex + attactName));
                    filenameList.add(attactName);
                }
            }
            String zipFolder = "multimedia" + File.separator;
            if (fileAddZip.size() > 0) {
                addFileToZip(zipFile, "", zipFolder, true, null, fileAddZip);
            }
            /*** 向文件夹中添加文件end **/
            
            /** 向文件夹中添加文件 **/            
            ArrayList<String> filePathList = (ArrayList<String>) nameList.get(2);
            ArrayList<String> fileNameList = (ArrayList<String>) nameList.get(3);
            for(int i = 0; i < filePathList.size(); i++){
                String filePath = filePathList.get(i);
                String fileName = fileNameList.get(i);
                zipFolder = "attachment/" + filePath.split("##")[0];
                in = VfsService.getFile(filePath.split("##")[1]);
                addFileToZip(zipFile, fileName, zipFolder, true, in, null);
            }
            /** 直接向zip文件中添加excel文件 **/
            String excelname = (String) nameList.get(0);
            fileAddZip.clear();
            fileAddZip.add(new File(fileindex + excelname));
            addFileToZip(zipFile, "", "/", true, null, fileAddZip);
            filenameList.add(excelname);
            /** 直接向zip文件中添加excel文件end **/
			for (int i = 0; i < filenameList.size(); i++) {
				String filename = (String) filenameList.get(i);
				File file = new File(fileindex + filename);
				/* 删除生成的excel文件 防止temp文件夹冗杂 */
				if (file.exists()) {
					file.delete();
				}
			}
            zipFileName = PubFunc.encrypt(zipFileName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(in);
            PubFunc.closeResource(zipFile);
        }
        return zipFileName;

    }

    /**
     * 导入zipFileName文件中的简历信息导目标库（destNBase)
     * 
     * @Title: ImportResumeZip
     * @Description: 导入zipFileName文件中的简历信息导目标库（destNBase)
     * @param fileId
     *            要导入的zip文件
     * @param destNbase
     *            导入目标人员库（如为空，则按zip中Excel里带有的人员库导入）
     * @return 返回导入结果描述，如“导入成功简历X份。导入失败X份，失败原因：.........”
     * @throws GeneralException 
     */
    public String ImportResumeZip(String fileId, String destNbase) throws GeneralException {
        StringBuffer msg = new StringBuffer();
        ZipInputStream zs = null;
        Workbook wb = null;
        InputStream input = null;
        FileOutputStream out = null;
        String zipFileName = System.getProperty("java.io.tmpdir")+"tempfile_zp.zip";
        try{
            ResumeZipBo bo = new ResumeZipBo(this.conn, this.userView);
            input = VfsService.getFile(fileId);
            out = new FileOutputStream(zipFileName);
            int len = 0;
			byte[] temp = new byte[1024];
			while ((len = input.read(temp)) != -1) {
				out.write(temp, 0, len);
			}
			PubFunc.closeResource(out);
            // 根据路径取得需要解压的Zip文件
            ZipFile zipFile = new ZipFile(zipFileName); 
            List fileHeaderList = zipFile.getFileHeaders();
            // 从zip文件中取出 主要的Excel文件
            for (int i = 0; i < fileHeaderList.size(); i++) {
                FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                // 文件夹以及里面的文件，暂时不需要
                if (fileHeader.isDirectory() || fileHeader.getFileName().startsWith("multimedia/")
                        || fileHeader.getFileName().startsWith("attachment/"))
                    continue;
                
                String fileName = "";
                String extention = "";
                // --截取文件名
                if (fileHeader.getFileName().length() > 0 && fileHeader.getFileName() != null) { 
                    int j = fileHeader.getFileName().lastIndexOf(".");
                    int k = fileHeader.getFileName().lastIndexOf("/");
                    if (j > -1 && j < fileHeader.getFileName().length() && !fileHeader.isDirectory()) {
                        // --文件名
                        fileName = fileHeader.getFileName().substring(k + 1, j); 
                        // --扩展名
                        extention = fileHeader.getFileName().substring(j + 1); 
                    }
                }
                // 得到对应的excel文件
                if (extention.trim().length() > 0 && "xls".equalsIgnoreCase(extention) && fileName.length() > 0 && fileName.startsWith("T_")) {
                    zipFile.setPassword("hjsj2013");
                    zs = zipFile.getInputStream(fileHeader);
                    wb = WorkbookFactory.create(zs);// 创建excel
                    ArrayList returnList = bo.relativeExcel(wb);// 得到返回的提示信息list
                    //用与前台提示信息：指标不存在
                    ArrayList inforNotExistList = (ArrayList) returnList.get(0);
                    //用于前台提示信息：指标类型不一致
                    ArrayList inforNotFormatList = (ArrayList) returnList.get(1);
                    //存放的是不存在的指标的itemId
//                    ArrayList itemNotInList = (ArrayList) returnList.get(2);
                    //存放的是指标集不存在的指标集名称
//                    ArrayList setNotInList=(ArrayList) returnList.get(3);
                    //用与前台提示信息：指标集不存在
                    ArrayList setNotExistList=(ArrayList) returnList.get(4);
                    // 所有的字段都没有问题 或忽略异常信息
                    if ("1".equalsIgnoreCase(this.ignoreExceptions) 
                            || (setNotExistList.size()==0&&inforNotExistList.size() == 0 && inforNotFormatList.size() == 0)) {
                        msg.delete(0, msg.length());
                        // 向数据库中导入数据
                        String[] count = bo.importData(wb, fileHeaderList, zipFile,new ArrayList<String>(),new ArrayList<String>());
                        int number = Integer.valueOf(count[1]);
                        if (number > 0){
                            msg.append("导入完成，");
                            msg.append("共有" + count[0] + "条数据，");
                            msg.append("成功导入" + count[1] + "条数据！");
                        } else {
                            msg.append("文件中没有需要导入的数据！");
                        }
                        //删除上传的zip文件
                        if(this.isdeleteZip) {
                            File uploadFile = new File(zipFileName);
                            uploadFile.delete();
                        }
                    } else {
                        msg.delete(0, msg.length());
                        msg.append("[{'information': '在导入时存在以下问题,如果继续导入，这些信息可能无法入库，你确定要继续导入么？'},");
                        int count = 1;
                        //提示指标集不存在信息 
                        if(setNotExistList != null && setNotExistList.size() > 0){
                            for(int m = 0; m < setNotExistList.size(); m++){
                                msg.append("{'information': '" + count + "、" + setNotExistList.get(m) + "'},");
                                count++;
                            }
                        }
                        //用与前台提示信息：指标不存在
                        if(inforNotExistList != null && inforNotExistList.size() > 0){
                            for(int m = 0; m < inforNotExistList.size(); m++){
                                msg.append("{'information': '" + count + "、" + inforNotExistList.get(m) + "'},");
                                count++;
                            }
                        }
                        //用于前台提示信息：指标类型不一致
                        if(inforNotFormatList != null && inforNotFormatList.size() > 0){
                            for(int m = 0; m < inforNotFormatList.size(); m++){
                                msg.append("{'information': '" + count + "、" + inforNotFormatList.get(m) + "'},");
                                count++;
                            }
                        }
                        
                        if (msg.toString().endsWith(","))
                            msg.setLength(msg.length() - 1);
                        
                        msg.append("]");
                    }
                    break;
                } else {
                    msg.delete(0, msg.length());
                    msg.append("缺少必要的Excel文件,请检查上传文件");
                }
            }
        }catch (ZipException e) {
        	 e.printStackTrace();
             //有异常信息不能执行导入操作则删除上传文件
             File uploadFile = new File(zipFileName);
             uploadFile.delete();
             throw new GeneralException("", "上传的不是正确的ZIP文件！", "", "");
        }catch (Exception e) {
            e.printStackTrace();
            //有异常信息不能执行导入操作则删除上传文件
            File uploadFile = new File(zipFileName);
            uploadFile.delete();
            throw GeneralExceptionHandler.Handle(e);
        } finally{
        	PubFunc.closeResource(input);
        	PubFunc.closeResource(out);
        }

        return msg.toString();

    }
    /**
     * 废弃以后使用vfs
     * 获取存放文件的根目录
     * @return 返回存放文件的根目录，例如：“e：\test\multimedia\”。
     * @throws GeneralException
     */
    @Deprecated
    public String getSysFilePath() throws GeneralException {
        ConstantXml constantXml = new ConstantXml(this.conn, "FILEPATH_PARAM");
        String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");

        if (rootDir == null || "".equals(rootDir)) {
            throw new GeneralException("没有配置多媒体存储路径！");
        }

        rootDir = rootDir.replace("\\", File.separator);
        if (!rootDir.endsWith(File.separator))
            rootDir = rootDir + File.separator;
        
        rootDir += "multimedia" + File.separator;
        return rootDir;
    }
    
    /**
     * 导出empList中包含的人员简历数据到zipFileName文件 包括Excel和简历附件
     * 将同一个人的简历附件放到一个文件夹中，文件夹名称以姓名—招聘参数中设置的唯一性指标来命名
     * @Title: ExportResumeZip
     * @param empList
     *            要导出的人员列表，“Zpt00000001”，“Zpt00000009”...
     * @param excelName 
     * @param flag 导出的附件类型  =0：导出附件；=1：导出登记表；=2：导出附件和登记表
     * @return 导出成功返回zip文件名（包含路径），导出失败则抛异常，将失败信息抛出
     */
    public String ExportResumeInfoAndZip(ArrayList<String> empList, String excelName, int flag) throws GeneralException {
    	 String zipFileName = userView.getUserName() + "_resume.zip";
    	 //压缩文件路径
    	 String fileindex = System.getProperty("java.io.tmpdir")+File.separator;
    	 //catalina path
    	 String catalinaPath = System.getProperty("catalina.home") + File.separator + "temp" + File.separator;
    	 //文件在压缩文件夹中的路径
    	 String zipFolder = "";
    	 //文件在压缩文件夹中的name
    	 String newFileName = "";
         File tempfile = new File(fileindex + zipFileName);
         // 先判断zip文件是否存在如果存在删除该zip文件
         if (tempfile.exists()) 
             tempfile.delete();
         tempfile = new File(catalinaPath + this.userView.getUserName() + "_temp");
         if(!tempfile.exists())
        	 tempfile.mkdirs();
         InputStream input = null;
         ZipFile zipFile = null;
         try {
        	 // 生成zip文件
			zipFile = new ZipFile(fileindex + zipFileName);
         
	         RecordVo vo = ConstantParamter.getConstantVo("ZP_ONLY_FIELD");
	         String field = vo.getString("str_value");
	         boolean onlyflag = StringUtils.isNotEmpty(field);
	         ResumeZipBo bo = new ResumeZipBo(this.conn, this.userView);
	         ArrayList<String> nbaseList = new ArrayList<String>();
	         HashMap<String, String> a0100Map = new HashMap<String, String>();
	         //唯一性标识用作文件名
	         ArrayList<String[]> forfilename = new ArrayList();
	         for (int i = 0; i < empList.size(); i++) {
	             String empId = empList.get(i);
	             if (StringUtils.isEmpty(empId))
	                 continue;
	             
	             String nbase = empId.substring(0, 3);
	             String a0100 = empId.substring(3);
	             if (a0100Map.containsKey(nbase)) {
	                 String a0100s = a0100Map.get(nbase);
	                 a0100s += "," + a0100;
	                 a0100Map.put(nbase, a0100s);
	             } else {
	                 a0100Map.put(nbase, a0100);
	                 nbaseList.add(nbase);
	             }
	             forfilename.add(getOnlyFlag(a0100, nbase, field));
	         }
	         
	         // 参数太多了 ，封装成对象传过去
	         ArrayList paramList = new ArrayList();
	         paramList.add(nbaseList);
	         paramList.add(a0100Map);
	         // 包含一个ArrayList和一个HashMap  第一个ArrayList 是要导出的人员库
	         // 第二个map key指人员库 value是对应的人员库的中人员编号
	         ArrayList DataList = bo.getResumeExcelSqlAndColunm(paramList);
	         // 存放文件名称，在zip文件生成完成以后将这些文件删除
	         ArrayList<String> filenameList = new ArrayList<String>();
	         if(0 == flag || 2 == flag) {
	             EmployNetPortalBo ebo = new EmployNetPortalBo(conn); 
	             ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
	             HashMap map = parameterXMLBo.getAttributeValues();
	             String candidateStatusItemId="#";//应聘身份指标
	             if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
	                 candidateStatusItemId=(String)map.get("candidate_status");
	            
	             // 得到Excel的名字和各个附件的名字
	             ArrayList nameList = bo.createExcel(DataList, nbaseList.get(0));
	             
	             for (String[] split : forfilename) {
	                 /*** 向文件夹中添加文件end **/
	                 String excelname = (String) nameList.get(0);
	                 filenameList.add(excelname);
	                 String a0100 = split[1].substring(3);
	                 //获取招聘参数中设置的简历附件分类，并获取前台显示的分类顺序
	                 HashMap<String, String> fileOrderMap = new HashMap<String, String>();
	                 if(!"#".equals(candidateStatusItemId))
	                     fileOrderMap = getFileOrder(candidateStatusItemId, a0100, ebo, map);
	                 
	                 ArrayList<String> filePathList = (ArrayList<String>) nameList.get(2);
	                 ArrayList<String> fileNameList = (ArrayList<String>) nameList.get(3);
	                 
	                 for(int i = 0; i < filePathList.size(); i++){
	                	 zipFolder = "";
	                     String filePath = filePathList.get(i);
	                     String fileName = fileNameList.get(i);
	                     if(filePath.contains(split[0])){
	                    	 if(onlyflag) {
	                    		 zipFolder = "attachment/" + split[2]+"-"+split[3];
	                    	 } else {
	                    		 zipFolder = "attachment/" + split[2];
	                    	 }
	                    	 if(fileOrderMap.containsKey(fileName)) {
	                    		 String order = fileOrderMap.get(fileName);
	                    		 newFileName = fileName.replace(fileName, order + "、" + fileName);
	                    	 } else {
	                    		 newFileName = fileName;
	                    	 }
	                    	 input = VfsService.getFile(filePath.split("##")[1]); 
	                    	 addFileToZip(zipFile, newFileName, zipFolder, false, input, null);
	                     }
	                         
	                 }
	                 
	             }
	             
	             String attactNames = (String) nameList.get(1);
	             if (attactNames.trim().length() > 0) {
	                 String[] attactNameArr = attactNames.split(",");
	                 for (int i = 0; i < attactNameArr.length; i++) {
	                     String attactName = attactNameArr[i];
	                     filenameList.add(attactName);
	                 }
	             }
	             
	         }
	         
	         if(1 == flag || 2 == flag) {
	                String a0100s = "";
	                String nbase = "";
	                
	                for (int i = 0; i < empList.size(); i++) {
	                    String empId = empList.get(i);
	                    if (StringUtils.isEmpty(empId))
	                        continue;
	                    
	                    nbase = empId.substring(0, 3);
	                    String a0100 = empId.substring(3);
	                    a0100s += PubFunc.encrypt(a0100) + ",";
	                }
	                
	                PrintResumeBo pbo = new PrintResumeBo(this.conn, this.userView);
	                pbo.printResume(a0100s, "", PubFunc.encrypt(nbase), 0);
	                
	                HashMap<String, String> fileNameMap = pbo.getFileNameMap();
	                Iterator<Map.Entry<String, String>> entries = fileNameMap.entrySet().iterator();  
	                while (entries.hasNext()) {  
	                	zipFolder = "";
	                    Map.Entry<String, String> entry = entries.next();  
	                    String[] pathNames = getOnlyFlag(entry.getKey(), nbase, field);
	                    String fileName = entry.getValue();
	                    if(onlyflag && pathNames.length > 3) {
	                    	zipFolder = "attachment/" + pathNames[2]+"-"+pathNames[3];
	                    } else {
	                    	zipFolder = "attachment/" + pathNames[2];
	                    }
	                    fileName = fileName + ".pdf";
	                    String filePath = fileindex + this.userView.getUserName()+"_tempCard" + System.getProperty("file.separator") + fileName;
	                    File file = new File(filePath);
	                    if(!file.exists()) {
	                    	file = new File(fileindex+fileName);
	                    }
	                    input = new FileInputStream(file);
	                    addFileToZip(zipFile, fileName, zipFolder, false, input, null);
	                    filenameList.add(fileName);
	                }
	            }
	         zipFolder = "";
	         File excel = new File(fileindex + excelName);
	         input = new FileInputStream(excel);
	         addFileToZip(zipFile, excelName, zipFolder, false, input, null);
            filenameList.add(excelName);
            /** 直接向zip文件中添加excel文件end **/
            for (int i = 0; i < filenameList.size(); i++) {
                String filename = (String) filenameList.get(i);
                File file = new File(fileindex + filename);
                /* 删除生成的excel文件 防止temp文件夹冗杂 */
                if (file.exists()) {
                    file.delete();
                    continue;
                }
                
                file = new File(catalinaPath + this.userView.getUserName() + "_temp" + File.separator + filename);
                /* 删除生成的excel文件 防止temp文件夹冗杂 */
                if (file.exists()) {
                    file.delete();
                }
            }
            
            String path = catalinaPath + this.userView.getUserName() + "_temp";
            File temp = new File(path);
            if(!temp.exists())
                temp.mkdirs();
         
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(zipFile);
			PubFunc.closeResource(input);
		}
    	 return zipFileName;
    }
    
    
    /**
     * 像压缩文件中添加文件
     * @param zipFile
     * @param fileName
     * @param zipFolder
     * @param encryptFiles
     * @param input 添加文件流
     * @param file 添加文件
     */
    private void addFileToZip(ZipFile zipFile,String fileName, String zipFolder, boolean encryptFiles, InputStream input, ArrayList<File> files) {
    	try {
	        // 设置zip包的一些参数集合
	        ZipParameters parameters = new ZipParameters(); 
	        if(encryptFiles) {
	        	// 是否设置密码（此处设置为：是）
	        	parameters.setEncryptFiles(true); 
	        	// 压缩包密码
	        	parameters.setPassword("hjsj2013");
	        }
	        // 加密级别
	        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); 
	        // 压缩方式(默认值)
	        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); 
	        // 普通级别（参数很多）
	        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); 
	        //压缩路径
	        parameters.setRootFolderInZip(zipFolder);
	        if(input != null) {
	        	//压缩文件名
	        	parameters.setFileNameInZip(zipFolder + File.separator + fileName);
	        	//压缩源为数据流
	        	parameters.setSourceExternalStream(true);
	        	zipFile.addStream(input, parameters);
	        }else {
	        	zipFile.addFiles(files, parameters);
	        }
        } catch (ZipException e) {
        	e.printStackTrace();
        } 
    }
    
    	
  	/**
  	 * 获取唯一性指标值
  	 * @param a0100
  	 * @param nbase
  	 * @param field
  	 * @return
  	 */
    private String[] getOnlyFlag(String a0100 , String nbase, String field){
  		String guidkey = "";
  		String value = "";
  		String a0101 = "";
  		boolean onlyflag = StringUtils.isNotEmpty(field);
  		RowSet rs = null;
  		ContentDAO dao = new ContentDAO(this.conn);
  		try {
  			StringBuffer sql = new StringBuffer();
			sql.append("select guidkey,a0101");
			if(onlyflag)
				sql.append("," + field.split(",")[0].substring(field.indexOf(".")+1));
			sql.append(" from "+nbase+"A01");
			sql.append(" where a0100 = '"+a0100+"'");
			rs = dao.search(sql.toString());
			if(rs.next()){
				guidkey = rs.getString(1);
				a0101 = rs.getString(2);
				if(onlyflag)
					value = rs.getString(3);
			}
  		} catch (Exception e) {
  			e.printStackTrace();
  		}finally{
  			PubFunc.closeResource(rs);
  		}
  		String[] temp =  {guidkey,nbase+a0100,a0101,value};
  		return temp;
  	}
    /**
     * 获取简历附件分类的顺序
     * @param fieldId 唯一性指标
     * @param a0100 人员编号
     * @param bo EmployNetPortalBo公用类
     * @param parameMap 招聘参数
     * @return
     */
    private HashMap<String, String> getFileOrder(String fieldId, String a0100, EmployNetPortalBo bo, HashMap parameMap) {
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            int order = 1;
            ArrayList<HashMap<String, String>> attachCodesetList = bo.getAttachCodeset(parameMap, bo.getCandidateStatus(fieldId, a0100));
            for(int i = 0; i < attachCodesetList.size(); i++) {
                HashMap<String, String> codeItemMap = attachCodesetList.get(i);
                String notNull = codeItemMap.get("notNull");
                if(!"1".equals(notNull) && !"2".equals(notNull))
                    continue;
                
                String itemDesc = codeItemMap.get("itemDesc");
                if(StringUtils.isNotEmpty(itemDesc)) {
                    map.put(itemDesc, String.valueOf(order));
                    order++;
                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        return map;
    }
}
