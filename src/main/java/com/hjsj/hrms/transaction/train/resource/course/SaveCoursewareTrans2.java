package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.resource.UnicodeReader;
import com.hjsj.hrms.businessobject.train.zip.ZipEntry;
import com.hjsj.hrms.businessobject.train.zip.ZipInputStream;
import com.hjsj.hrms.utils.Office2Swf;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.URLDecoder;

/**
 * <p>
 * Title:SaveCoursewareTrans
 * </p>
 * <p>
 * Description:保存添加的培训课程课件
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class SaveCoursewareTrans2 extends IBusiness {

    /** 1为普通课件，2为文本课件，3为视频音频课件，4为scorm课件 */
    private String fileType = "";

    private String xmlContent = "";

    /**
     * 
     */
    public SaveCoursewareTrans2() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        String flag = "yes";
        try {
            RecordVo rv = new RecordVo("r51");
            String id = (String) this.getFormHM().get("r5100");
            id = id != null && id.trim().length() > 0 ? id : "";
            this.getFormHM().remove("r5100");
            IDGenerator idg = new IDGenerator(2, this.getFrameconn());
            String str = "";
            if ("".equals(id)) {
                str = idg.getId("R51.R5100");
            } else {
                str = id;
            }

            String r5000 = idg.getId("R50.R5000");
            InsertCourses(r5000); // 增加课程

            String filesName = this.getFormHM().get("courseName").toString();
            filesName = SafeCode.decode(filesName);
//            filesName = getFileName(filesName); // 判断课件是否有重名

            String courseType = this.getFormHM().get("courseType").toString(); // 获取课件分类

            this.fileType = courseType;
            
            String newPath = (String) this.getFormHM().get("newPath");
            if(StringUtils.isNotEmpty(newPath)) {
                if(newPath.indexOf("id:") > -1)
                    newPath = newPath.substring(newPath.indexOf("id:") + 3);
                
                if(StringUtils.isNotEmpty(newPath))
                    newPath = SafeCode.decode(PubFunc.decrypt(newPath));                
            }

            if (!"2".equals(courseType)) { // 如果非纯文本类型
                String names_old = (String) this.getFormHM().get("path_old");
                if (newPath != null && newPath.length() > 0 && !newPath.equals(names_old)) {// 附件为空是不作处理
                    // 避免把原有的数据冲掉
//                    TrainCourseBo bo = new TrainCourseBo(this.frameconn);
//                    filepath = bo.getAttacmentRootDir("1");
//                    String separator = System.getProperty("file.separator");
//                    filepath = filepath.replace("``", separator);
//                    if (!filepath.endsWith(separator))
//                        filepath += separator;
//
//                    filepath = URLDecoder.decode(filepath);
//                    filepath += "coureware";
//                    String a_code = getFileUrl(r5000);
//                    filepath = findPath(filepath, a_code);
//                    filepath += separator + filesName + names.substring(names.lastIndexOf("."));

                    // 将office文档转为flash文档
                    String officeFile = newPath.toLowerCase();
                    if (officeFile != null
                            && (officeFile.endsWith(".doc") || officeFile.endsWith(".docx") || officeFile.endsWith(".xls") || officeFile.endsWith(".xlsx") || officeFile.endsWith(".pdf")
                                    || officeFile.endsWith(".ppt") || officeFile.endsWith(".pptx"))) {
                        String outputFilePath = newPath.substring(0, newPath.lastIndexOf(File.separator) + 1) + Integer.parseInt(str) + ".swf";
                        Office2Swf.office2Swf(newPath, outputFilePath);
                    }
                }
            }
            //------------------------------新增课件记入日志  chenxg add 2017-12-13--------------------
            StringBuffer context = new StringBuffer();
            context.append("新增课件:" + newPath);
            this.getFormHM().put("@eventlog", context.toString());
            //-------------------------------------------------------------------------------------
            ContentDAO contentDAO = new ContentDAO(this.getFrameconn());

            if ("2".equals(courseType)) { // 如果是纯文本文件
                filesName = this.getFormHM().get("textName").toString(); // 课件名称
                filesName = SafeCode.decode(filesName);
                String textContent = this.getFormHM().get("textContent").toString();
                textContent = textContent != null ? SafeCode.decode(textContent) : "";
                rv.setString("r5115", textContent); // 课件内容
            }

            if (!"".equals(id)) {
                rv.setString("r5100", id);
                String r5113 = isZip(newPath, id);// 解压ZIP
                if (r5113 != null && r5113.length() > 0)
                    rv.setString("r5113", r5113);

                if ("6".equals(rv.getString("r5105"))) {
                    String url = (String) this.getFormHM().get("url");
                    System.out.println(url);
                    rv.setString("r5113", url);
                }
                rv.setString("r5000", r5000);
                rv.setString("r5105", courseType);
                rv.setString("r5103", filesName);

                String name = (String) this.getFormHM().get("path");
                String name_old = (String) this.getFormHM().get("path_old");
                if (name != null && name.length() > 0 && !name.equals(name_old) && "4".equals(this.fileType)) {
                    rv.setObject("xmlcontent", this.xmlContent);
                }
                contentDAO.updateValueObject(rv);
            } else {
                rv.setString("r5100", str);
                rv.setString("r5000", r5000);
                rv.setString("r5105", courseType);
                rv.setString("r5103", filesName);
                String r5113 = isZip(newPath, str);// 解压ZIP
                if (r5113 != null && r5113.length() > 0)
                    rv.setString("r5113", r5113);

                if ("6".equals(rv.getString("r5105"))) {
                    String url = (String) this.getFormHM().get("url");
                    System.out.println(url);
                    rv.setString("r5113", url);
                }

                String name = (String) this.getFormHM().get("path");
                String name_old = (String) this.getFormHM().get("path_old");
                if (name != null && name.length() > 0 && !name.equals(name_old) && "4".equals(this.fileType)) {
                    rv.setObject("xmlcontent", this.xmlContent);
                }
                contentDAO.addValueObject(rv);
            }

        } catch (Exception e) {
            flag = "no";
            e.printStackTrace();
        } finally {
            this.getFormHM().put("flag", flag);
        }

    }

    private String isZip(String path, String id) {
        String tmpPath = path;
        String sep = System.getProperty("file.separator");
        tmpPath = tmpPath.replace("``", sep);
        tmpPath = URLDecoder.decode(tmpPath);
        if (path != null && path.length() > 0 && path.toLowerCase().endsWith(".zip")) {
            InputStream input = null;
            try {
                id = Integer.parseInt(id) + "";
                tmpPath = path.substring(0, path.lastIndexOf(sep) + sep.length()) + id + sep;
                input = new FileInputStream(path);
                if (!unzip(input, tmpPath))
                    tmpPath = "";
                input.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (path.indexOf(sep + "coureware") > -1)
            path = path.substring(path.indexOf(sep + "coureware"));
        else
            path = "";
        return path;
    }

    /**
     * 上传课程 新增培训课程
     * */
    public void InsertCourses(String r5000) {

        ConstantXml constant = new ConstantXml(this.frameconn, "TR_PARAM");
        String diyType = constant.getNodeAttributeValue("/param/diy_course", "codeitemid"); // 获取课程分类
        java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());

        ContentDAO contentDAO = new ContentDAO(this.getFrameconn());
        String courseName = this.getFormHM().get("courseName").toString(); // 得到课程名称
        String courseDesc = this.getFormHM().get("courseDesc").toString(); // 得到课程简介
        courseName = SafeCode.decode(courseName);
        courseDesc = courseDesc != null ? SafeCode.decode(courseDesc) : "";
        RecordVo rv = new RecordVo("r50");

        try {
            if (null != diyType) {
                rv.setString("r5004", diyType);
            }
            rv.setString("r5000", r5000);
            rv.setString("r5003", courseName);
            rv.setString("r5014", "1"); // 是否公开 1 是 2否
            rv.setString("r5016", "1"); // 是否选修 1 是 2否
            rv.setString("r5012", courseDesc);
            rv.setString("r5037", "1"); // 默认DIY课程
            rv.setString("r5022", "01"); // 默认起草状态
            rv.setString("create_user", this.userView.getA0100());
            rv.setDate("create_time", date); // 因oracle库中只能插入时间类型
            // 所以此处直接定义成时间类型而不是String
            contentDAO.addValueObject(rv);
        } catch (GeneralException e) {
            e.printStackTrace();
        }
    }

    public boolean unzip(InputStream input, String saveFilePath) {
        boolean succeed = true;
        ZipInputStream zin = null;
        ZipEntry entry;
        String sep = System.getProperty("file.separator");
        try {

            zin = new ZipInputStream(input);
            if (!saveFilePath.endsWith(sep)) {
                saveFilePath += sep;
            }
            // iterate ZipEntry in zip
            while ((entry = zin.getNextEntry()) != null) {
                // if file,unzip it
                if (!entry.isDirectory()) {
                    int index = entry.getName().lastIndexOf("/");
                    File myFile = null;
                    if (index == -1) {
                        myFile = new File(saveFilePath);
                    } else {
                        myFile = new File(saveFilePath + entry.getName().substring(0, index));
                    }
                    if (!myFile.exists()) {
                        myFile.mkdirs();
                    }

                    // 保存imsmanifest.xml文件内容
                    if ("4".equals(this.fileType) && entry.getName().toLowerCase().indexOf("imsmanifest.xml") != -1) {
                        UnicodeReader r = new UnicodeReader(zin, "utf-8");
                        BufferedReader reader = new BufferedReader(r);
                        StringBuffer buffer = new StringBuffer();

                        String str = null;
                        while ((str = reader.readLine()) != null) {
                            buffer.append(str);
                            buffer.append("\r\n");
                        }
                        this.xmlContent = buffer.toString();
                    }

                    try (
                            FileOutputStream  fout = new FileOutputStream(saveFilePath + entry.getName());
                            DataOutputStream dout = new DataOutputStream(fout);
                    ){

                        byte[] b = new byte[1024];
                        int len = 0;
                        while ((len = zin.read(b)) != -1) {
                            dout.write(b, 0, len);
                        }
                    }
                    zin.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            succeed = false;
        } finally {
            if (null != zin) {
                try {
                    zin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return succeed;
    }
}
