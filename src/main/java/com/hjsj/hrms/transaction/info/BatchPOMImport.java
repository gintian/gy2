package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.common.CmykToRgbBo;
import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * 批量导入图片或多媒体
 * @author tianye
 * @date 2013-5-30
 */
public class BatchPOMImport extends IBusiness {
    File tempFile = null;
    InputStream inOneFile = null;//读取包中的单个文件流
    FileOutputStream tempFileOut = null;//写入服务器上临时文件流
    InputStream uploadIn = null;//读取上传文件流
    String dbDesc = "";//人员库
    //Map privPerson= new HashMap();//登陆人人员范围
    
    public void execute() throws GeneralException {
        try{
            
            FormFile file = (FormFile) this.getFormHM().get("file");
            //记录录入，批量导入文件，导入失败时报错  jingq add 2014.10.27
            if(file==null){
                throw  GeneralExceptionHandler.Handle(new Exception("请使用系统导出的zip文件，不能直接修改文件扩展名！"));
            }
            //判断上传的文件是否合法  jingq add 2014.10.31
            boolean flag = FileTypeUtil.isFileTypeEqual(file);
            if(flag){
                String ruleItemid = (String) this.getFormHM().get("ruleItemid");//规则字段
                String userbase = (String)this.getFormHM().get("userbase");
                String photo_maxsize = (String)this.getFormHM().get("photo_maxsize");
                if(StringUtils.isEmpty(photo_maxsize))
                    photo_maxsize = "512";
                
                String multimedia_maxsize = (String)this.getFormHM().get("multimedia_maxsize");
                if(StringUtils.isEmpty(multimedia_maxsize))
                    multimedia_maxsize = "102400";

                String multimediaName = (String)this.getFormHM().get("multimediaName");//批量导入多媒体指定 的文件名称
                multimediaName = PubFunc.hireKeyWord_filter(multimediaName);
                String multimediaSortFlag = (String)this.getFormHM().get("multimediaSortFlag");//批量导入多媒体指定 的文件分类标记（对应多媒体表中的P 、F 等等）
                String batchImportType = (String)this.getFormHM().get("batchImportType");
                StringBuffer info = new StringBuffer();//存放文件打入情况信息
                
                ContentDAO dao=new ContentDAO(this.getFrameconn());
                this.frowset = dao.search("select dbname from dbname where lower(pre)='" + userbase.toLowerCase() + "'");
                if (this.frowset.next())
                    dbDesc= this.frowset.getString("dbname") ;
                
                if(!this.userView.isAdmin()){
                    StringBuffer dbPriv = this.userView.getDbpriv();
                    if((dbPriv.toString().toLowerCase()).indexOf(userbase.toLowerCase())==-1){// 检查用户的人员库权限
                        info.append("您没有"+dbDesc+"的操作权限！");
                        this.getFormHM().put("info", info.toString());
                        return;
                    }
                    /*this.frowset = dao.search("select a0100 "+this.userView.getPrivSQLExpression(userbase, false));
                    while(this.frowset.next()){
                        String a0100 = this.frowset.getString("a0100");
                        if(a0100.length()>0){
                            privPerson.put(a0100, a0100);
                        }
                        
                    }*/
                }
                
            
                
                long maxSize =0;
                int importCount = 0;//成功导入数据计算器
                int amount = 0;//导入文件的总数
                boolean sizeControl = false;//是否控制上传图片大小
                long photoMaxSize = 0;//图片最大值
                photoMaxSize = Long.parseLong(photo_maxsize)*1024;//配置信息基数为KB
                if("photo".equalsIgnoreCase(batchImportType)&&photoMaxSize>0){
                    sizeControl = true;
                    maxSize=photoMaxSize;
                }
                
                long multimediaMaxSize = 0;
                multimediaMaxSize = Long.parseLong(multimedia_maxsize)*1024;
                if("multimedia".equalsIgnoreCase(batchImportType)&&multimediaMaxSize>0){
                    sizeControl = true;
                    maxSize=multimediaMaxSize;
                }
                
    			if(!VfsService.existPath()){
    				throw GeneralExceptionHandler.Handle(new GeneralException(""," 您设置的多媒体存放路径不存在，请到系统管理-参数设置-系统参数-文件存放路径处进行设置。","",""));
    			}
				
                //上传文件写入临时文件
                String url = System.getProperty("java.io.tmpdir");
                if(!url.endsWith(File.separator))
                	url += File.separator; 
                
                url += ruleItemid+ new Date().getTime()+".zip";
                tempFileOut = new FileOutputStream(url);
                uploadIn = file.getInputStream();//前台上传的流文件
                int Buffer = 1024;
                byte[] byteArray = new byte[Buffer];
                while (uploadIn.read(byteArray, 0, Buffer) > 0) {//将上传文件写入服务器临时文件中
                    tempFileOut.write(byteArray);
                }
                
                String encoding = PubFunc.getZIPEncoding(url);
                //读取压缩的临时文件
                tempFile = new File(url);
                //解决中文乱码
                ZipFile zipFile = new ZipFile(tempFile,encoding);
                Enumeration e = zipFile.getEntries();                
                ZipEntry zipEntry = null;
                //遍历压缩包中的每个文件
                while (e.hasMoreElements()) {
                    zipEntry = (ZipEntry) e.nextElement();
                    //判断是否是文件夹，若是则不上传，但文件夹中的文件会上传   chenxg 2016-11-09
                    if(zipEntry.isDirectory())
                        continue;
                    
                    amount++;
                    String fileName = zipEntry.getName();
                    inOneFile = zipFile.getInputStream(zipEntry);
                    long size = zipEntry.getSize();
                    if(0 == size) {
                        info.append("&nbsp;&nbsp;" + fileName + "大小为0Byte，不允许上传！<br>");
                        continue;
                    }
                    
                    boolean isFileTypeEqual = FileTypeUtil.isFileTypeEqual(file);
                    if(!isFileTypeEqual) {
                        info.append("&nbsp;&nbsp;" + fileName + "是非法文件，不允许上传！<br>");
                        continue;
                    }
                        
                    String filePackageName = "";
                    if(fileName.indexOf("/")!=-1){
                    filePackageName=fileName.substring(0,fileName.lastIndexOf("/")+1);
                    fileName=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
                    
                    }
                    if(fileName.length()==0){
                        amount--;
                        continue;
                    }
                    //检查文件
                    String realtype = getFormatName(zipFile.getInputStream(zipEntry));//这里必须重新取压缩包中的文件流（该方法中要读取流会使流中的标记位置改变，为了避免入库时流的标记位置不是文件的开始位置这里一定不能使用inOneFile）
                    if(!checkFile(zipEntry, realtype,batchImportType,sizeControl,maxSize,info)){
                        continue;
                    }
                    String updateInfo = "";//更新信息结果信息
                    if("photo".equalsIgnoreCase(batchImportType)){
                        updateInfo = updatePhoto(filePackageName,fileName,userbase,ruleItemid,zipFile,zipEntry);//更新该张照片
                    }else if("multimedia".equalsIgnoreCase(batchImportType)){
                        String setid = (String) this.getFormHM().get("mulSetid");
                        String mulItemid = (String) this.getFormHM().get("mulItemid");
                        ArrayList<Object> list = (ArrayList<Object>) this.getFormHM().get("mulFileItemlist");
                        boolean multimediaFlag = false;
                        if(list != null && list.size() > 0)
                            multimediaFlag = true;
                        updateInfo = updateMultimedia(filePackageName,fileName,userbase,ruleItemid,multimediaName,multimediaSortFlag,setid,mulItemid,multimediaFlag);
                    }
                    
                    if(updateInfo.length()==0){
                        importCount++;
                    }else{
                        info.append("&nbsp;&nbsp;"+updateInfo);
                    }
                    if(inOneFile!=null){
                        inOneFile.close();
                        inOneFile = null;
                    }
                }
                //关闭压缩文件
                zipFile.close();
                
                if(info.length()!=0){
                    info.insert(0,"其中：<br>");
                }
                
                info.insert(0, ("共计"+amount+"，成功导入"+importCount+"个文件，失败"+(amount-importCount)+"个！<br>"));
                this.getFormHM().remove("multimediaName");
                this.getFormHM().put("info", info.toString());
            } else {
                throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.fileuploaderror")));
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw  GeneralExceptionHandler.Handle(new Exception("人员库描述查找出限错误！"));
        }catch (NumberFormatException e){
            e.printStackTrace();
            throw  GeneralExceptionHandler.Handle(new Exception("控制文件大小参数非数字，请检查参数配置!"));
        }catch(IOException e){
            e.printStackTrace();
            throw  GeneralExceptionHandler.Handle(new Exception("请使用系统导出的zip文件，不能直接修改文件扩展名！"));
        }catch (GeneralException e) {
            e.printStackTrace();
            throw e;
        } catch(Exception e) {
        	e.printStackTrace();
        	throw  GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(inOneFile);
            PubFunc.closeResource(tempFileOut);
            PubFunc.closeResource(uploadIn);

            try {
                if(tempFile!=null && tempFile.exists()){
                    tempFile.delete();
                    tempFile = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public  String getFormatName(Object object) throws IOException {
        ImageInputStream iis = null;
        String name;
        try {
            iis = ImageIO.createImageInputStream(object);
            Iterator iterator = ImageIO.getImageReaders(iis);

            if (!iterator.hasNext()) {
                return null;
            }
            ImageReader reader = (ImageReader) iterator.next();
            name = reader.getFormatName();
        } finally {
            if (iis != null)
                iis.close();
        }
      return name;
   }
    /**
     * tianye create method
     * 文件检查
     * @param zipEntry 压缩包中的文件实体对象
     * @param batchImportType 导入类型（photo 照片 ，multimedia 多媒体）
     * @param sizeControl 是否对文件大小控制
     * @param maxSize 控制的最大值
     * @param info 检测后记录的相关信息
     * @return
     */
    public boolean checkFile(ZipEntry zipEntry,String realtype ,String batchImportType,
            boolean sizeControl, long maxSize, StringBuffer info) {
        try {
            String fileName = zipEntry.getName();
            //判断是不是GBK或GB2312，如果不是则转成GB2312
            String encode = PubFunc.getEncoding(fileName);
            if(StringUtils.isNotEmpty(encode) && !"GB2312".equals(encode)
                    && !"GBK".equals(encode)) {
                String unicode = new String(fileName.getBytes(), encode);
                fileName = new String(unicode.getBytes("GB2312"));
            }
            int indexInt=fileName.lastIndexOf(".");
            String type ="";
            if(indexInt!=-1){
                type=fileName.substring(indexInt+1,fileName.length());
            }else{
                info.append("&nbsp;&nbsp;请检查文件（"+fileName+"）没有扩展名！<br>");
                return false;
            }
            //检查照片格式
            if("photo".equalsIgnoreCase(batchImportType)){
                if(!"bmp".equalsIgnoreCase(type)&&!"jpg".equalsIgnoreCase(type)&&!"jpeg".equalsIgnoreCase(type)){//先控制格式为这三种
                    info.append("&nbsp;&nbsp;"+fileName+ResourceFactory.getProperty("workbench.info.nophoto")+"<br>");
                    return false;
                }
                
                String str = "jpg.jpeg.jpe.jfif";//因为JPEG类型的文件后缀名包括这四个
                if(!type.equalsIgnoreCase(realtype)){
                    if(("JPEG".equalsIgnoreCase(realtype)&&!str.toUpperCase().contains(type.toUpperCase()))){
                        info.append("&nbsp;&nbsp;"+fileName+ResourceFactory.getProperty("workbench.info.noalterextension")+"<br>");
                        return false;
                    }
                }
            }
            //判断文件大小
            if(sizeControl&&zipEntry.getSize()>maxSize){
                info.append("&nbsp;&nbsp;"+fileName+"文件超过了"+maxSize/1024+"KB !<br>");
                return false;
            }else{
                //不控制大小则超过200K进行提示
                if(zipEntry.getSize()>200*1024){
                    String typeName = ResourceFactory.getProperty("workbench.browse.file");
                    if("photo".equalsIgnoreCase(batchImportType))
                        typeName = ResourceFactory.getProperty("workbench.browse.photo");
                        
                    info.append("&nbsp;&nbsp;"+fileName+"大小为："+zipEntry.getSize()/1024+"KB，");
                    info.append(ResourceFactory.getProperty("workbench.browse.photobeyond")
                            .replace("{0}", typeName).replace("{1}", typeName));
                    info.append("<br>");
                }
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
            
        return true;
    }
    
    
    /**
     * tianye create method
     * 更新多媒体入库
     * @param fileName 规则文件名
     * @param userbase 人员库
     * @param ruleItemid 规则字段
     * @param multimediaName 统一制定的文件名
     * @param multimediaSortFlag 文件分类标识
     * @return
     * @throws GeneralException 
     */
    private String updateMultimedia(String filePackageName,String fileName,
            String userbase, String ruleItemid, String multimediaName,
            String multimediaSortFlag, String setid, String mulItemid, boolean multimediaFlag) throws GeneralException {
        String ruleinfo = "";
        String mulItemidValue = "";
        String[] values = new String[3];
        String fullFileName = fileName;
        if(multimediaFlag){
            if(!"A01".equalsIgnoreCase(setid.toUpperCase()) && StringUtils.isNotEmpty(mulItemid) && fileName.indexOf("_") < 0)
                 throw new GeneralException("", "文件上传失败<BR>\"" + fileName + "\"的文件名不符合规则！", "", "");

            if(fileName.indexOf("_") < 0)
            {
                int number = fileName.lastIndexOf(".");
                String suffixName = fileName.substring(number,fileName.length()); 
                fileName = fileName.substring(0,number);
                if (StringUtils.isEmpty(multimediaName))
                    fileName = fileName + "_" + fileName + suffixName;
                else 
                    fileName = fileName + "_" + multimediaName + suffixName;
            }
            
            values = fileName.split("_");
            ruleinfo = values[0];
            if(!"A01".equalsIgnoreCase(setid.toUpperCase()))
                mulItemidValue = values[1].trim();
            
            fileName = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.length());
        } else {
            int indexInt=fileName.lastIndexOf(".");
            ruleinfo = fileName.substring(0,indexInt);
        }
            
        Blob blob = null;//库中多媒体存储文件
        StringBuffer info = new StringBuffer();//记录该文件更新情况
        int indexInt=fileName.lastIndexOf(".");
        String ext=fileName.substring(indexInt,fileName.length());
        ArrayList list = (ArrayList) this.getFormHM().get("multimediaFilelist");
        PhotoImgBo photoImgBo = new PhotoImgBo(this.frameconn);
        String userName = this.userView.getUserName();
        VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
		VfsModulesEnum vfsModulesEnum = VfsModulesEnum.YG;
		VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
        try {
            String selectA0100Sql = "";
            String a0101 = "";
            if(this.userView.isAdmin()){
                selectA0100Sql="select A0100,A0101 from "+userbase+"A01 where " +ruleItemid+"='"+ruleinfo+"'";
            }else{
                selectA0100Sql = "select a0100,A0101 "+this.userView.getPrivSQLExpression(userbase, false)+"and "+ruleItemid+"='"+ruleinfo+"'";
            }
            
            ContentDAO dao=new ContentDAO(this.getFrameconn());
            
            this.frowset=dao.search(selectA0100Sql);
            if(this.frowset.next()){
                String a0100 = this.frowset.getString("A0100");
                a0101 = this.frowset.getString("A0101");
                /*//对人员范围进行检查
                if(!this.userView.isAdmin()&&!privPerson.containsKey(a0100)){
                    info.append(dbDesc+"中您没有对"+ruleinfo+"人员操作的权限！<br>");
                    return info.toString();
                }*/
                if(!multimediaFlag) {
                    RecordVo vo = new RecordVo(userbase+"A00");
                    vo.setString("a0100", a0100);
                    int i9999 = Integer.parseInt(new StructureExecSqlString().getUserI9999(userbase + "a00",a0100,"A0100",this.getFrameconn()));
                    vo.setInt("i9999",i9999 );
                    
                    if(multimediaName.length()!=0){
                        vo.setString("title", multimediaName);
                    }else{
                        vo.setString("title", "文件"+i9999);
                    }
                    vo.setString("ext",ext);
                    vo.setString("state","3");
                    vo.setDate("createtime",DateStyle.getSystemTime());
                    vo.setDate("modtime",DateStyle.getSystemTime());
                    vo.setString("createusername",userView.getUserName());
                    vo.setString("modusername",userView.getUserName());
                    vo.setString("flag",multimediaSortFlag);
                    String guidkey = photoImgBo.getGuidKey(userbase + "A01", a0100);
    				String fileId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
    						guidkey, inOneFile, "", "", false);
    				vo.setString("fileid",fileId);
                    //执行入库操作
                    dao.addValueObject(vo);
                } else {
                    String I9999 = "0";
                    MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn,this.userView);
                    if(!"A01".equalsIgnoreCase(setid)) {
                        
                        FieldItem fi = DataDictionary.getFieldItem(mulItemid, setid);
                        if(!"0".equalsIgnoreCase(fi.getCodesetid())) {
                            String itemId = multiMediaBo.getItemid(fi.getCodesetid(), mulItemidValue);
                            mulItemidValue = StringUtils.isEmpty(itemId) ? mulItemidValue : itemId;
                        }
                        
                        StringBuffer sql = new StringBuffer();
                        sql.append("SELECT MAX(I9999) I9999 FROM ");
                        sql.append(userbase + setid);
                        sql.append(" WHERE ");
                        if("D".equalsIgnoreCase(fi.getItemtype())) {
                            mulItemidValue = mulItemidValue.replace(".", "-").replace("/", "-").replace("\\", "-");
                            sql.append(Sql_switcher.dateToChar(mulItemid, getDateFormat(fi.getItemlength())));
                            sql.append("='" + mulItemidValue + "' ");
                        } else
                            sql.append(mulItemid + "='" + mulItemidValue + "' ");
                        
                        sql.append(" and a0100='" + a0100 + "'");
                        this.frecset = dao.search(sql.toString());
                        if(this.frecset.next()){
                            I9999 = this.frecset.getString("I9999");
                            I9999 = I9999==null?"0":I9999;
                        }
                    }
                    
                    if("A01".equalsIgnoreCase(setid) || !"0".equalsIgnoreCase(I9999)){
                        File file = multiMediaBo.inputstreamtofile(inOneFile, fileName);
                        if(file != null) {
                            //图片安全过滤
                            if("/.png/.jpg/.jpeg/.bmp/.gif/".indexOf("/" + ext + "/") > -1) {
                                file = multiMediaBo.inputstreamtofile(ImageBO.imgStream(file, ext), fileName);
                            }
                            
                            multiMediaBo.setParam("A", userbase, setid, a0100, Integer.parseInt(I9999));
                            this.getFormHM().put("filetype", multimediaSortFlag);
                            if(!"A01".equalsIgnoreCase(setid))
                                this.getFormHM().put("childguid", multiMediaBo.getChildGuid());
                            
                            multiMediaBo.saveMultimediaFile(this.getFormHM(), file,false);  
                            //删除临时文件
                            if(file.exists())
                            	file.delete();
                        } else 
                            info.append(filePackageName+ruleinfo+"导入失败,没有找到！<br>");                        
                    } else {
                        String setName = DataDictionary.getFieldSetVo(setid).getFieldsetdesc();
                        a0101 = StringUtils.isEmpty(a0101) ? ruleinfo : a0101;
                        info.append("文件：" + fullFileName + "<br>&nbsp;&nbsp;原因：" + a0101+"的" + setName + "子集下没有找到对应记录！<br>");                        
                    }
                        
                        
                }
                
                
            }else{
                info.append(dbDesc+"中没有找到（"+filePackageName+ruleinfo+"）人员或没有操作的权限！<br>");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            info.append(filePackageName+ruleinfo+"导入失败！<br>");
        }catch (Exception e){
            e.printStackTrace();
            info.append(filePackageName+ruleinfo+"导入失败！<br>");
        }
        
        return info.toString();
    }
    
    
    /**
     * tianye create method
     * 更新照片入库，如果库中没有某一人的照片，插入一条照片数据
     * @param fileName 文件名
     * @param userbase 人员库
     * @param ruleItemid 规则字段
     * @param zipEntry 
     * @param zipFile 
     * @return
     */
    public String updatePhoto(String filePackageName,String fileName,  String userbase, String ruleItemid, 
            ZipFile zipFile, ZipEntry zipEntry) {
        StringBuffer info = new StringBuffer();//记录该张照片更新情况
        int indexInt=fileName.lastIndexOf(".");
        String ruleinfo = fileName.substring(0,indexInt);
        String ext=fileName.substring(indexInt,fileName.length());
        Blob blob = null; 
        String infodata = "";
        PhotoImgBo photoImgBo = new PhotoImgBo(this.getFrameconn());
        String userName = this.userView.getUserName();
        VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
		VfsModulesEnum vfsModulesEnum = VfsModulesEnum.YG;
		VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
		InputStream in = null;
        try {
        	ByteArrayOutputStream cloneIn = cloneInputStream(inOneFile);
        	inOneFile = new ByteArrayInputStream(cloneIn.toByteArray());
        	in = new ByteArrayInputStream(cloneIn.toByteArray());
            String selectA0100Sql="";
            if(this.userView.isAdmin()){
                selectA0100Sql="select A0100 from "+userbase+"A01 where " +ruleItemid+"='"+ruleinfo+"'";
            }else{
                selectA0100Sql = "select a0100 "+this.userView.getPrivSQLExpression(userbase, false)+"and "+ruleItemid+"='"+ruleinfo+"'";
            }
            String selectSql = "select * from "+ userbase +"A00 where A0100 in ("+selectA0100Sql+") and flag = 'P' ";
            ContentDAO dao=new ContentDAO(this.getFrameconn());
            RecordVo vo = new RecordVo(userbase+"A00");
            this.frowset=dao.search(selectSql);
            if(this.frowset.next()){
                String a0100 =this.frowset.getString("A0100");
                int i9999 = this.frowset.getInt("i9999");
                vo.setString("a0100", a0100);
                vo.setInt("i9999",i9999);
                if (Sql_switcher.searchDbServer() == Constant.ORACEL || Sql_switcher.searchDbServer() == Constant.DB2) {
                    blob = getOracleBlob(vo,userbase+"A00", inOneFile);
                    vo.setObject("ole", blob);
                }else{
                   //zxj 20150825 之前直接用输入流放到vo中在sql2000下有问题，现改为byte[]
                   long fileSize = zipEntry.getSize();
                   ByteBuffer nbf = ByteBuffer.allocate((int)fileSize);
                   byte[] array = new byte[1024];
                   int length = 0;
                   while ((length = inOneFile.read(array)) > 0) {
                       if (length != 1024)
                           nbf.put(array, 0, length);
                       else
                           nbf.put(array);
                   }
                   
                   byte[] content = nbf.array();
                   
                   vo.setObject("ole", content);
               }
                vo.setDate("createtime",this.frowset.getDate("createtime"));
                vo.setString("createusername",this.frowset.getString("createusername"));
                vo.setString("title",this.frowset.getString("title"));
                vo.setString("id",this.frowset.getString("id"));
                vo.setString("state",this.frowset.getString("state"));
                vo.setString("flag","P");
                vo.setString("ext",ext);
                vo.setString("modusername",userView.getUserName());
                vo.setDate("modtime",DateStyle.getSystemTime());
                infodata = creatFile(this.frowset.getString("title"),ext,zipEntry,zipFile);//zgd 2014-5-16 判断照片是否为CMYK模式
                if(!"".equals(infodata)){
                    infodata = fileName+ "的模式不支持 " + infodata;
                    info.append(infodata);
                }
                //执行更新操作
                if("".equals(infodata)){//zgd 2014-7-14 只有当照片为RGB模式下才允许导入，CMYK模式图片不让导入
                	String guidkey = photoImgBo.getGuidKey(userbase + "A01", a0100);
    				String fileId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
    						guidkey, in, fileName, "", false);
    				vo.setString("fileid",fileId);
                    dao.updateValueObject(vo);
                }
            }else{
                this.frowset=dao.search(selectA0100Sql);
                if(this.frowset.next()){
	                String a0100 = this.frowset.getString("A0100");
	                vo.setString("a0100", a0100);
	                vo.setInt("i9999", Integer.parseInt(new StructureExecSqlString().getUserI9999(userbase + "a00",a0100,"A0100",this.getFrameconn())));
	                vo.setDate("createtime",DateStyle.getSystemTime());
	                vo.setDate("modtime",DateStyle.getSystemTime());
	                vo.setString("createusername",userView.getUserName());
	                vo.setString("modusername",userView.getUserName());
	                vo.setString("flag","P");
	                vo.setString("ext",ext);
	                infodata = creatFile("",ext,zipEntry,zipFile);//zgd 2014-5-16 判断照片是否为CMYK模式
	                if(!"".equals(infodata)){
	                    infodata = fileName+ "的模式不支持 " + infodata;
	                    info.append(infodata);
	                }
	                
	                if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
	                   //zxj 20150825 之前直接用输入流放到vo中在sql2000下有问题，现改为byte[]
	                   long fileSize = zipEntry.getSize();
	                   ByteBuffer nbf = ByteBuffer.allocate((int)fileSize);
	                   byte[] array = new byte[1024];
	                   int length = 0;
	                   while ((length = inOneFile.read(array)) > 0) {
	                       if (length != 1024)
	                           nbf.put(array, 0, length);
	                       else
	                           nbf.put(array);
	                   }
	                   
	                   byte[] content = nbf.array();
	                   vo.setObject("ole", content);
	               }
	                //执行入库
	                if("".equals(infodata)){//zgd 2014-7-14 只有当照片为RGB模式下才允许导入，CMYK模式图片不让导入
	                	String guidkey = photoImgBo.getGuidKey(userbase + "A01", a0100);
	    				String fileId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
	    						guidkey, in, fileName, "", false);
	    				vo.setString("fileid",fileId);
	                    dao.addValueObject(vo);
	                }
	                
	                if (Sql_switcher.searchDbServer() == Constant.ORACEL || Sql_switcher.searchDbServer() == Constant.DB2) {
	                	blob = getOracleBlob(vo,userbase+"A00", inOneFile);
	                	vo.setObject("ole", blob);
	                	dao.updateValueObject(vo);
	                }
	                
                }else{
                    info.append(dbDesc+"中没有找到（"+filePackageName+ruleinfo+"）人员或没有操作的权限！<br>");
                }

            }
        } catch (GeneralException e){
            e.printStackTrace();
            info.append(filePackageName+ruleinfo+"导入失败！<br>");
        } catch (SQLException e) {
            e.printStackTrace();
            info.append(filePackageName+ruleinfo+"导入失败！<br>");
        }catch (Exception e){
            e.printStackTrace();
            info.append(filePackageName+ruleinfo+"导入失败！<br>");
        }
        return info.toString();
    }

    /**
     * 判断照片模式是否为CMYK
     * @param title
     * @param ext
     * @param zipFile 
     * @param zipEntry 
     * @param inOneFile2 
     * @return
     * @throws GeneralException
     */
    private String creatFile(String title, String ext, ZipEntry zipEntry, ZipFile zipFile) throws GeneralException {
        File file = null;
        String info="";
        FileOutputStream fout = null;
        try {
            if(title==null||title.length()<4)
                title = "media";
            file = File.createTempFile(title+"-", ext, new File(System.getProperty("java.io.tmpdir")));

            fout = new FileOutputStream(file);
            if (file.exists()) {
                file.delete();
                file.deleteOnExit();
            }
            int len;
            byte buf[] = new byte[1024];
            InputStream in = zipFile.getInputStream(zipEntry);
            while ((len = in.read(buf, 0, 1024)) != -1) {
                fout.write(buf, 0, len);
            }
            fout.close();
            boolean CMYK = false;
            if(file!=null){//zgd 2014-5-15 验证照片模式的。照片只支持RGB模式，不支持CMYK模式。（CMYK模式照片为打印模式）
                CMYK = CmykToRgbBo.isCMYK(file);
            }
            if(CMYK){
                info = ResourceFactory.getProperty("workbench.info.typeofcmyk");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeIoResource(fout);
            PubFunc.closeIoResource(file);
        }
        return info;
        
    }
    private Blob getOracleBlob(RecordVo vo,String table,InputStream inOneFile) {
        StringBuffer strSearch = new StringBuffer();
        strSearch.append("select ole from "+table+" where a0100='"+ vo.getString("a0100")+"' and i9999="+vo.getString("i9999")+"" );
        strSearch.append(" FOR UPDATE");
        
        StringBuffer strInsert = new StringBuffer();
        strInsert.append("update  "+table+" set ole=EMPTY_BLOB() where a0100='"+ vo.getString("a0100")+"' and i9999="+vo.getString("i9999")+"" );
        OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
        //告诉OracleBlobUtils不要关闭传入的流，inOneFile这里用完后边还会再用到，这里的程序会自己关inOneFile
        blobutils.setCloseStream(false);
        Blob blob = blobutils.readBlob(strSearch.toString(), strInsert.toString(),inOneFile); 
        return blob;
    }
    
    private static String getDateFormat(int length) {
        String format = "";                                  
        if(4 == length)
            format = "yyyy";
        else if(7 == length)
            format = "yyyy-MM";
        else if(16 == length)
            format = "yyyy-MM-dd hh:mm";
        else if(18 <= length)
            format = "yyyy-MM-dd hh:mm:ss";
        else
            format = "yyyy-MM-dd";
        
        return format;
    }
    
    private static ByteArrayOutputStream cloneInputStream(InputStream input) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = input.read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
			return baos;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
