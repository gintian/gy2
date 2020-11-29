package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.struts.upload.FormFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Title:CourseTransSave
 * </p>
 * <p>
 * Description:保存添加培训课程记录
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
public class CourseTransSave extends IBusiness {

    public void execute() throws GeneralException {
    	InputStream inputStream = null;
        try {
            if (!VfsService.existPath())
                throw new GeneralException("没有配置多媒体存储路径！");

            List itemlist = (List) this.getFormHM().get("itemlist");
            String filepath = (String) this.getFormHM().get("filepath");
            FormFile file = (FormFile) this.getFormHM().get("file");
            FieldItemView fieldItem = null;
            int size = file.getFileSize();
            RecordVo rv = new RecordVo((String) this.getFormHM().get("tablename"));

            for (int i = 0; i < itemlist.size(); i++) {
                fieldItem = (FieldItemView) itemlist.get(i);
                if ("D".equals(fieldItem.getItemtype())) {
                    String dateValue = fieldItem.getValue();
                    dateValue = dateValue.replace(".", "-").replace("/", "-");
                    rv.setDate(fieldItem.getItemid(), dateValue);
                } else if ("N".equals(fieldItem.getItemtype())) {
                    String temp = fieldItem.getValue();
                    temp = temp == null || temp.length() < 1 ? "0" : temp;
                    rv.setDouble(fieldItem.getItemid(), Double.parseDouble(temp));
                } else {
                    if ("r5004".equalsIgnoreCase(fieldItem.getItemid().toLowerCase()))
                        rv.setString(fieldItem.getItemid(), PubFunc.decrypt(SafeCode.decode(fieldItem.getValue())));
                    else
                        rv.setString(fieldItem.getItemid(), fieldItem.getValue());
                }
            }
            
            ContentDAO contentDAO = new ContentDAO(this.getFrameconn());
            String id = (String) this.getFormHM().get("id");
            id = PubFunc.decrypt(SafeCode.decode(id));
            this.getFormHM().remove("id");
            String userName = this.userView.getUserName();
			VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
			VfsModulesEnum vfsModulesEnum = VfsModulesEnum.PX;
			VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
            if (id != null && id.trim().length() > 0) {
                rv.setString("r5000", id);
                RecordVo cousreVo = new RecordVo("r50");
                cousreVo.setString("r5000", id);
                cousreVo = contentDAO.findByPrimaryKey(cousreVo);

                String oldimageurl = cousreVo.getString("imageurl");
                String oldimag = "";

                if (oldimageurl.length() > 0 && !"".equals(oldimageurl)) {
                    oldimag = oldimageurl.substring(oldimageurl.lastIndexOf("\\") + 1, oldimageurl.lastIndexOf("\\") + 19);
                }
                
                if (filepath.length() > 0 && !"".equals(filepath)) {
                    filepath = filepath.replace("...", "");
                    if (file != null && file.getFileSize() > 0) {
                        if (size / 1024 > 1 * 1024)
                            throw new GeneralException("上传文件太大，请不要超过1M");
                       
                        inputStream = file.getInputStream();
                        VfsService.deleteFile(userName, oldimageurl);
                        String imageurl = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
            					"", inputStream, file.getFileName(), "", false);
                        
                        rv.setString("imageurl", imageurl);
                    }  else if ("".equals(file.getFileName()) && filepath.equals(oldimag)) {
                        rv.setString("imageurl", oldimageurl);
                    } else if (size == 0) {
                        throw new GeneralException("上传文件大小不能为0M");
                    }
                } else {
                	inputStream = file.getInputStream();
                    VfsService.deleteFile(userName, oldimageurl);
                    rv.setString("imageurl", "");
                }
               
                contentDAO.updateValueObject(rv);
            } else {
                HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
                String codeitemid = PubFunc.decrypt(SafeCode.decode((String) hm.get("itemid")));
                IDGenerator idg = new IDGenerator(2, this.getFrameconn());
                String str = idg.getId("R50.R5000");
                rv.setString("r5022", "01");// 课程状态
	            rv.setInt("r5000", Integer.parseInt(str));
	            rv.setDate("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                rv.setString("create_user", this.userView.getA0100());
                rv.setString("codeitemid", codeitemid);
                if (file != null && file.getFileSize() > 0) {
                    if (size / 1024 > 1 * 1024) {	
                        throw new GeneralException("上传文件太大，请不要超过1M");
                    }
                    
                    inputStream = file.getInputStream();
                    String imageurl = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
        					"", inputStream, file.getFileName(), "", false);
                    rv.setString("imageurl", imageurl)	;
                }
                if (size == 0) {
                    if (filepath.length() == 0 && "".equals(filepath) && "".equals(file.getFileName())) {
                        rv.setString("imageurl", "");	
                    } else {
                        throw new GeneralException("上传文件大小不能为0M");
                    }
                }
               
                contentDAO.addValueObject(rv);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * deleteDir 删除文件夹
     * @param fle
     *         上传文件
     */
    private void deleteDir(File fle) {
    	File parentfilename=fle.getParentFile();
        File[] pfiles = parentfilename.listFiles();
        if(pfiles == null || pfiles.length==0){//判断上一级目录是为空
        	
        	parentfilename.delete();
        	deleteDir(parentfilename);
        }
    }
    /**
     * 
     * @param file
     *            上传文件
     * @param courseId
     *            课程ID
     * @return
     * @throws GeneralException
     */
    private String getPath(FormFile file, String courseId) throws GeneralException {
        String responseFileName = "";
        // 获取上传文件名
        String filePath = getImgName(file.getFileName());
        String fileName = "";
        String savePath = "";

        fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        try {
            // 获取上传文件路径
            savePath = getAttachmentPathById(courseId);
            // 最后的文件夹要以课程ID为结尾
            savePath = savePath + File.separator + courseId;
            // 上传文件
            String resultSave = this.upLoad(fileName, savePath, file);
            if (!"error".equalsIgnoreCase(resultSave)) {
                // 上传文件成功获取体上传路径
                responseFileName = resultSave;
            } else {
                responseFileName = "error";
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        // 返回上传文件路径
        return responseFileName;
    }

    /**
     * 上传文件
     * 
     * @param fileName上传文件保存的名字
     * @param savePath上传文件路径
     * @param file
     *            上传文件
     * @return 上传图片的全路径
     * @throws GeneralException
     */
    private String upLoad(String fileName, String savePath, FormFile file) throws GeneralException {
        String result = "error";
        InputStream stream = null;
        OutputStream bos = null;
        try { // 文件验证
            if (!FileTypeUtil.isFileTypeEqual(file)) {
                throw GeneralExceptionHandler
                        .Handle(new Exception(ResourceFactory.getProperty("error.fileuploaderror")));
            }
            // 上传文件不存在返回
            if (!this.createDir(savePath))
                return result;
            String saveImgPath = savePath + "\\" + fileName;

            File imageF = new File(saveImgPath);
            // 判断相同路径上有相同名字的文件
            if (imageF.isFile() && imageF.exists()) {
                // 如果有就文件名加_1保存
                saveImgPath = saveImgPath.substring(0, saveImgPath.lastIndexOf("."));
                saveImgPath = saveImgPath + "_1" + fileName.substring(fileName.lastIndexOf("."));
            }

            // 文件上传
            String filetxt = fileName.substring(fileName.lastIndexOf("."));
            stream = ImageBO.imgStream(file, filetxt);
            bos = new FileOutputStream(saveImgPath);

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = stream.read(buffer, 0, 8192)) != -1)
                bos.write(buffer, 0, bytesRead);
            result = saveImgPath;
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(bos);
            PubFunc.closeResource(stream);
        }
        return result;
    }

    /**
     * 
     * @param destDirName
     *            创建文件夹
     * @return
     */
    private boolean createDir(String destDirName) {
        if (!destDirName.endsWith(File.separator))
            destDirName = destDirName + File.separator;
        // 创建单个目录
        File dir = new File(destDirName);
        if (dir.exists()) {
            return true;
        }

        return dir.mkdirs();
    }

    /**
     * 生成随机文件夹路径
     * 
     * @param id
     * @return 返回生成的路径
     * @throws GeneralException
     */
    private String getAttachmentPathById(String id) throws GeneralException {

        TrainCourseBo bo = new TrainCourseBo(this.frameconn);
        String path = bo.getAttacmentRootDir();
        if (path == null || path.length() < 1 || path.startsWith("doc"))
            throw new GeneralException("没有配置多媒体存储路径！");

        UUID uuid = UUID.randomUUID();
        String tmpid = uuid.toString();

        int idHash = Math.abs(tmpid.hashCode());

        String dir1 = "" + idHash / 1000000 % 500;
        while (dir1.length() < 3)
            dir1 = "0" + dir1;

        String dir2 = "" + idHash / 1000 % 500;
        while (dir2.length() < 3)
            dir2 = "0" + dir2;

        path = path + "courseimg"+ File.separator + "F" + dir1 + File.separator + "F" + dir2;

        return path;
    }

    /**
     * 
     * @param fileName
     *            原文件名
     * @return 新文件名
     */
    private String getImgName(String fileName) {

        String filetxt = fileName.substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String tmpid = uuid.toString();
        String mgName = tmpid;
        return mgName + filetxt;
    }

}
