package com.hjsj.hrms.businessobject.infor.multimedia;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.system.filepathsetting.transaction.GZYHVfsManager;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

/**
 * 头像图片操作类
 * 可获得头像图片路径
 * 切割头像处理操作
 * @author tiany
 *
 */
public class PhotoImgBo {
    private Logger log = LoggerFactory.getLogger(PhotoImgBo.class);
    
    private Connection frameconn=null;
    private String guid;
    /**
     * 是否是获取证件照
     * 员工管理模块获取的照片都是证件照
     * OKR获取的照片都是上传的头像低分辨率图片，需要区分一下
     */
    private boolean isIdPhoto = false;
    public boolean isIdPhoto() {
        return isIdPhoto;
    }
    public void setIdPhoto(boolean idPhoto) {
        isIdPhoto = idPhoto;
    }
    
    
    public PhotoImgBo(Connection conn){
    	frameconn = conn;
    }
    
    private void addGuidKeyField(String tablename )
    {
        try{
        	DbWizard dbw = new DbWizard(this.frameconn);
            if (!dbw.isExistField(tablename, "GUIDKEY", false)) {
                Table table = new Table(tablename);
                Field field = new Field("GUIDKEY","人员唯一标识");
                field.setDatatype(DataType.STRING);
                field.setKeyable(false);
                field.setLength(38);
                table.addField(field);
                dbw.addColumns(table);
            }
        }
        catch (Exception e ){
           e.printStackTrace();             
        }     
     }   
    
    /**
     * tiany add
     * 获得头像相对路径
     *
     * @return
     * @throws GeneralException
     */
    public String getPhotoRelativeDir(String dbpre,String A0100) throws GeneralException
    {
  
        addGuidKeyField(dbpre+"A01");
      
    	
    	String mainguid =getGuidKey(dbpre+"A01",A0100);
    	
        String relative ="photo";
        try{
            String str  = mainguid; 
            int iHash = Math.abs(str.hashCode());
            String dir1 = ""+iHash/1000000%500;
            while (dir1.length()<3) {
                dir1 ="0"+dir1;
            }
            String dir2 = ""+iHash/1000%500;
            while (dir2.length()<3) {
                dir2 ="0"+dir2;
            }
            relative =relative + File.separator+"P"+dir1 + File.separator+"P"+dir2 ;            
            relative =relative+File.separator+mainguid+File.separator;
            
            
            this.guid = mainguid;
            
        }
        catch(Exception e){
           e.printStackTrace();            
        }
        return relative;
         
    }

    /**
     * @deprecated
     * @return
     * @throws GeneralException
     */
    public String  getPhotoRootDir() throws GeneralException{
    	  ConstantXml constantXml = new ConstantXml(this.frameconn,"FILEPATH_PARAM");
          String RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
         
          if ((RootDir==null) || ("".equals(RootDir))){
        	  throw new GeneralException("没有配置多媒体存储路径！");
          }   
          
          RootDir=RootDir.replace("\\",File.separator);          
          if (!RootDir.endsWith(File.separator)) {
              RootDir =RootDir+File.separator;
          }
          
          RootDir=RootDir+"multimedia"+File.separator;
          return RootDir;
    }
    public String getGuidKey(String tablename,String a0100)
    {
        String guid = "";
        RowSet frowset = null;
        try{
            StringBuffer sb = new StringBuffer();
            StringBuffer sWhere  = new StringBuffer();
            
            sWhere.append(" where A0100 ='");
            sWhere.append(a0100);
            sWhere.append("'");
            
            sb.append("select GUIDKEY from ");
            sb.append(tablename);     
            sb.append(sWhere.toString());   
            
            ContentDAO  dao=new ContentDAO(this.frameconn);
            frowset = dao.search(sb.toString());
            if (frowset.next()) {
                guid = frowset.getString("guidkey");
                if (guid==null || "".equals(guid)){
                    UUID uuid = UUID.randomUUID();
                    String tmpid = uuid.toString(); 
                    StringBuffer stmp = new StringBuffer();
                    stmp.append("update  ");
                    stmp.append(tablename);   
                    stmp.append(" set GUIDKEY ='");
                    stmp.append(tmpid.toUpperCase());
                    stmp.append("'");                    
                    stmp.append(sWhere.toString());
                    stmp.append(" and guidkey is null ");   
                    dao.update(stmp.toString());                

                    frowset = dao.search(sb.toString());
                    if (frowset.next()) {
                        guid = frowset.getString("guidkey");             
                    }
                }
            }
        }
        catch (Exception e ){
           e.printStackTrace();             
        } finally {
            PubFunc.closeDbObj(frowset);
        }
        return guid;
     }  
  //删除指定文件夹下所有文件
  //param path 文件夹完整绝对路径
     public static boolean delAllFile(String path) {
         boolean flag = false;
         File file = new File(path);
         if (!file.exists()) {
           return flag;
         }
         if (!file.isDirectory()) {
           return flag;
         }
         String[] tempList = file.list();
         File temp = null;
         for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
               temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
               temp.delete();
            }
           
         }
         return flag;
       }
     public static boolean delFileByName(String path,String fileName) {
         boolean flag = false;
         File file = new File(path);
         if (!file.exists()) {
           return flag;
         }
         if (!file.isDirectory()) {
           return flag;
         }
         String[] tempList = file.list();
         File temp = null;
         for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
               temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            String fname = temp.getName();
            int indexInt = fname.lastIndexOf(".");
            //zxj 20170926 删除文件时，避免存在无后缀的文件导致截取文件明报错
            if(indexInt <= -1) {
                continue;
            }
            
	   	 	fname = fname.substring(0,indexInt);
            if (temp.isFile() && fileName.contains(fname)) {
               temp.delete();
            }
         }
         return flag;
       }
     /** 对图片裁剪，并把裁剪完的新图片保存 
     * @throws Exception
     */
     public String cut(UserView userView,int x,int y,int width,int height, String ext) throws Exception {
     	InputStream is = null;
     	ImageInputStream iis = null;
     	RowSet rs = null;
     	String guidkey = userView.getGuidkey();
     	String sql = "";
     	String fileid = "";
     	String h_source = "";
     	String low_image = "";
     	try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            sql = "select path from hr_multimedia_file where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'";
            rs = dao.search(sql);
            if(rs.next()) {
                fileid = rs.getString("path");
                if(fileid.contains(",")){
                    String[] arr = fileid.split(",");
                    if(arr.length >0){
                        h_source = arr[0];
                    }
                }else{
                    h_source = fileid;
                }
            }
            // 读取图片文件
     		is = VfsService.getFile(h_source);
     		/*
     		 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
     		 * 参数：formatName - 包含非正式格式名称 . （例如 "jpeg" 或 "tiff"）等 。
     		 */
     		Iterator it = ImageIO.getImageReadersByFormatName(ext);
     		ImageReader reader = (ImageReader)it.next();
     		// 获取图片流
     		iis = ImageIO.createImageInputStream(is);
     		/*
     		 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
     		 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
     		 */
     		reader.setInput(iis, true);
     		/*
     		 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
     		 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
     		 * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
     		 */
     		ImageReadParam param = reader.getDefaultReadParam();
     		/*
     		 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
     		 * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
     		 */
     		Rectangle rect = new Rectangle(x, y, width, height);
     		// 提供一个 BufferedImage，将其用作解码像素数据的目标。
     		param.setSourceRegion(rect);
     		/*
     		 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
     		 * BufferedImage 返回。
     		 */
     		BufferedImage bi = reader.read(0, param);
     		// 保存新图片
            File newImage = new File(System.getProperty("java.io.tmpdir") + "/test." + ext);
     		ImageIO.write(bi, ext, newImage);
            //将人员照片保存到VFS中
            String userName = userView.getUserName();
            VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
            VfsModulesEnum vfsModulesEnum = VfsModulesEnum.XT;
            VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
            String fileName = userView.getUserFullName() + "." +  ext;
            if(newImage.exists()){
                is = new FileInputStream(newImage);
                low_image = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                        guidkey, is, fileName, "", false);
                newImage.delete();
                fileid = h_source + "," + low_image;
                sql = "update hr_multimedia_file set path='"+fileid+"' where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'";
                dao.update(sql);
            }
     	}  catch (Exception e) {
     		e.printStackTrace();
         }  finally {
     		if (is != null) {
                is.close();
            }
     		if (iis != null) {
                iis.close();
            }
     	}
     	return low_image;
     }
     
     
     /**
      * 按文件名称获取人员图片路径
      * @param absPath 文件夹路径
      * @param fileName 文件名（不包括后缀）example:"123" 
      * @return
      */
     public String getPersonImageWholeName(String absPath,String fileName){
    	 String fileWholeName = "";
    	 try{
    	     File file = new File(absPath);
			 String[] list = file.list(new ImageNameFilter(fileName));
		  	
			 if(list!=null && list.length>0){
			 	fileWholeName = list[0];
			 }
			
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
    	 return fileWholeName;
     }
     
     /**
      * 获取文件修改的最后时间
      * @param absPath 文件目录
      * @param fileName 文件名
      * @return 修改时间，如果文件不存在，则返回null
      */
     private Date getPersonImageFileModifyTime(String absPath,String fileName){
         String fileWholeName = "";
         File photoFile;
         try{
           File file = new File(absPath);
            String[] list = file.list(new ImageNameFilter(fileName));
            
            if(list!=null && list.length>0){
                fileWholeName = list[0];
                photoFile = new File(absPath + fileWholeName);
                return new Date(photoFile.lastModified());
            }
            
         }catch(Exception e){
             e.printStackTrace();
         }
         return null;
     }
     
     /**
      * 将人员的图片输出到指定路径
      * @param path 指定路径
      * @param conn 
      * @param dbpre  人员库
      * @param a0100 人员id 
      * @param fileName 指定文件名称
      * @return 如果 返回 “” 则是没有图片或生成失败
      */
     public String  createPersonPhoto(String path,Connection conn,String dbpre,String a0100,String fileName){
    	 //创建文件夹
    	 File tempDir = new File(path);
         if (!tempDir.exists()) {
             tempDir.mkdirs();
         }
    	 
         ResultSet rs = null;
         StringBuffer strsql = new StringBuffer();
         File tempFile = null;
         FileOutputStream fout = null;
         InputStream in = null;
         try {
	         strsql.append("select ext,Ole from ");
	         strsql.append(dbpre);
	         strsql.append("A00 where A0100='");
	         strsql.append(a0100);
	         strsql.append("' and Flag='P'");
	         
	         ContentDAO dao = new ContentDAO(conn);
	         rs = dao.search(strsql.toString());
	            
             if (rs.next()) {
            	 	if(!new File(path).isDirectory()){//判断传入路径是否有效 chent 20160114
            	 		return "";
            	 	}
	                tempFile = new File(path+fileName+rs.getString("ext"));
	                tempFile.createNewFile();
	                //tempFile = File.c  createTempFile(fileName, rs.getString("ext"),
	                //        new File(path));
	                //tempFile.renameTo(new File(fileName+"."+rs.getString("ext")));
	                in = rs.getBinaryStream("Ole");                
	                fout = new FileOutputStream(tempFile);
	                int len;
	                if(in!=null){
	                	byte buf[] = new byte[1024];
	                	
	                	while ((len = in.read(buf, 0, 1024)) != -1) {
	                		fout.write(buf, 0, len);
	                		
	                	}
	                	
	                	fout.flush();
	                	fout.close();
	                }else{
	                	return "";
	                }
	                
            }else{
            	return "";
            }
         }catch(Exception e){
        	 e.printStackTrace();
        	 return "";
         }finally{
        	 PubFunc.closeResource(fout);
        	 PubFunc.closeResource(in);
        	 PubFunc.closeResource(tempFile);
        	 PubFunc.closeResource(rs);
         }
    	 return tempFile.getName();
     }
     /**
      * 获取多媒体子集中某人照片的修改时间
      * @param dbpre 人员库前缀
      * @param a0100 人员编号
      * @return 修改时间，如果无记录则返回null
      */
     private Date getPhotoModifyDateInDB(String dbpre,String a0100){
         ResultSet rs = null;
         StringBuffer strsql = new StringBuffer();
         try {
             strsql.append("select ModTime from ");
             strsql.append(dbpre);
             strsql.append("A00 where A0100=?");
             strsql.append(" and Flag='P'");
             
             ArrayList<String> params = new ArrayList<String>();
             params.add(a0100);
             
             ContentDAO dao = new ContentDAO(this.frameconn);
             rs = dao.search(strsql.toString(), params);
                
             if (rs.next()) {
                 return rs.getTimestamp("ModTime");
             }
         }catch(Exception e){
             e.printStackTrace();
             return null;
         }finally{
             PubFunc.closeResource(rs);
         }
         return null;
     }
     
     /**
      * 删除用户照片文件
      * @Title: delUserPhoto   
      * @Description:    
      * @param nbase 人员库前缀
      * @param a0100 人员编号
      * @return 成功：true, 失败：false
      */
     public boolean delUserPhoto(String nbase, String a0100) {
         boolean isOK = false;
         
         try {
             String photoPath = getPhotoRootDir() + getPhotoRelativeDir(nbase, a0100);
             isOK = delFileByName(photoPath, "photo.jpg");
         } catch (Exception e) {
             
         }
         
         return isOK;
     }

    /**
     * 换头像之前删除掉之前的头像以及低分辨率图片
     * @param userView
     * @param order 从第几张开始删，传1就需要删除 第一张和第二张；传2就只删除第二张
     * @return
     */
    public boolean delFile(UserView userView,int order) {
        boolean isOK = false;
        {
            RowSet rs = null;
            String sql = "";
            String guidkey = userView.getGuidkey();
            String path = "";
            //文件id
            String fileid = "";
            try{
                ContentDAO dao = new ContentDAO(this.frameconn);
                sql = "select path from hr_multimedia_file where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'";
                rs = dao.search(sql);
                if(rs.next()){
                    path = rs.getString("path");
                    String [] arr = path.split(",");
                    //照片一共有两张，文件id放在path字段中，中间用逗号分隔。第一张上传的文件头像、第二张低分辨率图片
                    if(order ==1){
                        if(arr.length ==2){
                            isOK = VfsService.deleteFile(userView.getUserName(),arr[0]);
                            isOK = VfsService.deleteFile(userView.getUserName(),arr[1]);
                        }else if(arr.length ==1){
                            isOK = VfsService.deleteFile(userView.getUserName(),arr[0]);
                        }
                        fileid = "";
                    }else if(order ==2){
                        if(arr.length==2){
                            isOK = VfsService.deleteFile(userView.getUserName(),arr[1]);
                            fileid = arr[0];
                        }
                    }
                    if(isOK){
                        //图片删除后，数据库表中需要修改对应的记录
                        sql = "update hr_multimedia_file set path='"+fileid+"' where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'";
                        dao.update(sql);
                    }
                }
            }catch (Exception e){
                isOK = false;
                e.printStackTrace();
            }finally{
                PubFunc.closeDbObj(rs);
            }
        }
        return isOK;
    }
    /**
     * 上传新头像
     * @param inputStream
     * @param userView
     * @param ext
     * @throws Exception
     */
    public String  addFile(InputStream inputStream, UserView userView,String ext)throws Exception {
        String userName = userView.getUserName();
        VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
        VfsModulesEnum vfsModulesEnum = VfsModulesEnum.XT;
        VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
        String fileName = userView.getUserFullName() + ext;
        String guidkey = userView.getGuidkey();
        String fileid = "";
        String sql = "";
        String path = "";
        try{
            ContentDAO dao = new ContentDAO(this.frameconn);
            fileid = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                    guidkey, inputStream, fileName, "", false);
            path = fileid;
            sql = "update hr_multimedia_file set path='"+path+"' where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'";
            dao.update(sql);
        }catch (Exception e){
            throw e;
        }
        return fileid;
    }

    class ImageNameFilter implements FilenameFilter{

 		String fileName;
 		public ImageNameFilter(String fileName){
 			this.fileName = fileName;
 		}
 		
 		@Override
        public boolean accept(File dir, String fname) {
 			
 			if(fname.indexOf(fileName+".")==0){
 				return true;
 			}
 			return false;
 		}
 		
 	}

     /** 
      * 缩放图像 
      *  
      * @param srcImageFile       源图像文件地址 
      * @param result             缩放后的图像地址 
      * @throws GeneralException 
      * @throws IOException 
      */  
     public static void scale(String srcImageFile, String result,String ext) throws GeneralException, IOException {  
         try {  
             BufferedImage src = ImageIO.read(new File(srcImageFile)); // 读入文件  
             if(src==null){
            	 throw new GeneralException("请使用有效文件！");
             }
             double width = src.getWidth(); // 得到源图宽  
             double height = src.getHeight(); // 得到源图长  
             double scale = 1;
             if(width>=height&&width>=240){
            	 
            	 scale = width/240 ;
            	 height = height/scale;
            	 width = 240;
             }else{
            	 if(width<=height&&height>=240){
            		 
                	 scale = height/240 ;
                	 width = width/scale;
                	 height = 240;
                 }
             }
             
             Image image = src.getScaledInstance((int)width, (int)height,Image.SCALE_DEFAULT);  
             BufferedImage tag = new BufferedImage((int)width,(int) height,BufferedImage.TYPE_INT_RGB);  
             Graphics g = tag.getGraphics();  
             g.drawImage(image, 0, 0, null); // 绘制缩小后的图  
             g.dispose();  
             ImageIO.write(tag, ext, new File(result));// 输出到文件流  
         } catch (IOException e) {  
            throw e; 
         }  
     }


	public String getGuid() {
		return guid;
	}


	public void setGuid(String guid) {
		this.guid = guid;
	}
         
	/**
	 * 获取人员头像URL
	 * @Title: getPhotoPath   
	 * @Description: URL供前台页面使用，优先级 头像文件=》多媒体子集相片=》默认图片photo.jpg   
	 * @param nbase 人员库前缀
	 * @param a0100  人员编号
	 * @return
	 */
    public String getPhotoPath(String nbase, String a0100) {
        return getPhotoPath(nbase, a0100, "");
    }     
    /**   
     * @Description:获取人员图像所在路径(低分辨率)
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return String 
     * @author:chent   
     * @throws   
    */
    public String getPhotoPathLowQuality(String nbase,String a0100) { 
        return getPhotoPath(nbase, a0100, "low_img");
    }
    
    private String getPhotoPath(String nbase, String a0100, String photoName) {
        if (StringUtils.isBlank(nbase) || StringUtils.isBlank(a0100)) {
            return "/images/photo.jpg";
        }
        
        StringBuffer photoUrl = new StringBuffer();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String userName = "";
        VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
        VfsModulesEnum vfsModulesEnum = VfsModulesEnum.XT;
        VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
        String guidkey = "";
        String fileName = "";
        String A0101 = "";
        String ext = "";
        Map<String,String> userMap = getUserInfo(nbase,a0100);
        Map<String,String> fileMap = null;
        try {
            //判断是否配置了存储路径
            if(!VfsService.existPath()){
                throw new GeneralException("没有配置多媒体存储路径！");
            }
            String fileid = "";
            String path = "";
            String filePath = "";
            //获取证件照
            if(this.isIdPhoto){
                fileMap = getIdPhoto(nbase,a0100, userMap);
                if(fileMap!=null && fileMap.size()>0){
                    fileid = fileMap.get("fileid");
                    if(StringUtils.isBlank(fileid)){
                        //没有照片时，啥都不显示
                        photoUrl.setLength(0);
                        photoUrl.append("/images/photo.jpg");
                    }else{
                        //直接从本地临时目录下载图片
                        filePath = PubFunc.encrypt(fileid + "." + fileMap.get("ext"));
                        photoUrl.append("/servlet/vfsservlet?fileid=").append(filePath).append("&fromjavafolder=true");
                    }
                }else{
                    //没有照片时，啥都不显示
                    photoUrl.setLength(0);
                    photoUrl.append("/images/photo.jpg");
                }
                return  photoUrl.toString();
            }else{
                //先从hr_multimedia_file表里去取这个人的低分辨率照片
                fileMap = searchFile(nbase,a0100,2);
                //有低分辨率照片，直接用就好了
                if(fileMap !=null && fileMap.size()>0){
                    fileid = fileMap.get("fileid");
                    ext =  fileMap.get("ext");
                    //直接从本地临时目录下载图片
                    filePath = PubFunc.encrypt(fileid + "." + ext);
                    photoUrl.append("/servlet/vfsservlet?fileid=").append(filePath).append("&fromjavafolder=true");
                    return  photoUrl.toString();
                }else{
                    guidkey = userMap.get("guidkey");
                    userName = userMap.get("userName");
                    A0101 =  userMap.get("A0101");
                    //查找有没有上传的头像，也就是第一张照片
                    fileMap = searchFile(nbase,a0100,1);
                    //如果有上传的头像，那就把该头像压缩，生成低分辨率照片
                    if(fileMap !=null && fileMap.size()>0){
                        //path包含了第一张以及第二张照片的文件id,中间用逗号分隔
                        path = fileMap.get("path");
                        fileid = fileMap.get("fileid");
                        ext = fileMap.get("ext");
                        fileName = A0101 + "." + ext;
                        inputStream = compressFile(fileid,0.9995f);
                        fileid = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                                guidkey, inputStream, fileName, "", false);
                        //低分辨率照片文件id同时更新到hr_multimedia_file表中
                        path += "," + fileid;
                        updateFile(nbase,a0100,path,"." + ext,fileName,userName);
                        createPhotoToDir(fileid,ext);
                        filePath = PubFunc.encrypt(fileid + "." + ext);
                        photoUrl.append("/servlet/vfsservlet?fileid=").append(filePath).append("&fromjavafolder=true");
                        return  photoUrl.toString();
                    }else{
                        //什么照片都没有，就只能用证件照了，先将证件照复制一份，作为上传的头像，再将头像压缩，作为低分辨率照片
                        fileMap = getIdPhoto(nbase,a0100, userMap);
                        if(fileMap !=null && fileMap.size()>0){
                            fileid = fileMap.get("fileid");
                            ext = fileMap.get("ext");
                            fileName = A0101 + "." + ext;
                            //证件照复制一份，作为上传头像
                            inputStream = VfsService.getFile(fileid);
                            fileid = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                                    guidkey, inputStream, fileName, "", false);
                            path = fileid;
                            //将上传照片进行压缩
                            inputStream = compressFile(fileid,0.9995f);
                            fileid = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                                    guidkey, inputStream, fileName, "", false);
                            path += "," + fileid;
                            updateFile(nbase,a0100,path,"" + ext,fileName,userName);
                            createPhotoToDir(fileid,ext);
                            filePath = PubFunc.encrypt(fileid + "." + ext);
                            photoUrl.append("/servlet/vfsservlet?fileid=").append(filePath).append("&fromjavafolder=true");
                            return  photoUrl.toString();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeIoResource(inputStream);
        }
        
        if (photoUrl.toString().length() < 1) {
            photoUrl.append("/images/photo.jpg");
        }
        
        return photoUrl.toString();
    }
    /**
     * 获取人员的证件照
     * @param nbase
     * @param a0100
     * @return
     */
    private Map<String,String> getIdPhoto(String nbase,String a0100, Map<String,String> userMap){
        Map<String,String> fileMap = new HashMap<String,String>();
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        String fileid = "";
        String ext = "";
        InputStream inputStream = null;
        OutputStream  outputStream = null;
        try{
            ContentDAO dao = new ContentDAO(this.frameconn);
            sql.append("select fileid,Ole,ext from ");
            sql.append(nbase+"A00 ");
            sql.append(" where A0100 = '");
            sql.append(a0100);
            sql.append("' and UPPER(flag) = 'P'");
            rs = dao.search(sql.toString());
            if(rs.next()){
                fileid = rs.getString("fileid");
                inputStream = rs.getBinaryStream("Ole");
                ext = rs.getString("ext");
                fileMap.put("fileid",fileid);
                fileMap.put("ext",ext.substring(1,ext.length()));
            }
            //没有获取到文件id，需要先将证件照上传到VFS
            if(StringUtils.isBlank(fileid) && StringUtils.isNotBlank(ext)){
                if(inputStream !=null) {
                    String fileName = userMap.get("A0101") + ext;
                    VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
                    VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
                    VfsModulesEnum vfsModulesEnum = VfsModulesEnum.YG;
                    fileid = VfsService.addFile(userMap.get("userName"), vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                            userMap.get("guidkey"), inputStream, fileName, "", false);
                    //将文件id更新到A00中
                    dao.update("update " + nbase+"A00 " + " set fileid='"+fileid+"' where A0100 ='"+a0100+"' and UPPER(flag) = 'P' ");
                    fileMap.put("fileid",fileid);
                }
            }
            //人员照片使用的比较频繁，如果每次都从ftp或者网盘去获取，效率比较低。调整为在临时目录存储一份，直接读取临时目录下的
            if(fileMap!=null && fileMap.size()>0){
                createPhotoToDir(fileMap.get("fileid"),fileMap.get("ext"));
            }

        }catch (Exception e){
            log.error("生成人员 {} 照片到临时文件出错!,错误信息为:{},请重新上传此人照片!",a0100,e.getMessage());
        }finally{
            PubFunc.closeDbObj(rs);
            PubFunc.closeIoResource(inputStream);
            PubFunc.closeIoResource(outputStream);
        }
        return fileMap;
    }

    /**
     * 获取人员信息
     * path中有三个头像id，中间用逗号分隔，order代表取第几个
     * @param nbase
     * @param a0100
     * @return
     */
    private Map<String,String> getUserInfo(String nbase,String a0100){
        Map<String,String> userMap = new HashMap<String,String>();
        RowSet rs = null;
        try{
            DbNameBo dbbo = new DbNameBo(this.frameconn);
            //获取用户名字段名
            String loginField = dbbo.getLogonUserNameField();
            ContentDAO dao = new ContentDAO(this.frameconn);
            rs = dao.search("select guidkey,A0101,"+loginField+" from " + nbase +"A01 where A0100='"+ a0100+"'");
            if(rs.next()){
                userMap.put("guidkey",rs.getString("guidkey"));
                userMap.put("A0101",rs.getString("A0101"));
                userMap.put("userName",rs.getString(loginField));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return userMap;
    }
    /**
     * 查找头像文件
     * path中有两个个头像id，中间用逗号分隔，order代表取第几个
     * @param nbase
     * @param a0100
     * @param order
     * @return
     */
    private Map<String,String> searchFile(String nbase,String a0100,int order){
        Map<String,String> fileMap = new HashMap<String,String>();
        RowSet rs = null;
        String sql = "";
        String guidkey = "";
        //文件id
        String fileid = "";
        String path = "";
        String ext = "";
        try{
            ContentDAO dao = new ContentDAO(this.frameconn);
            rs = dao.search("select guidkey from " + nbase +"A01 where A0100='"+ a0100+"'");
            if(rs.next()){
                guidkey = rs.getString("guidkey");
            }
            sql = "select path,ext from hr_multimedia_file where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'";
            rs = dao.search(sql);
            if(rs.next()){
                path = rs.getString("path");
                if(StringUtils.isNotBlank(path)){
                    String [] arr = path.split(",");
                    ext = rs.getString("ext");
                    ext = ext.substring(ext.lastIndexOf(".") + 1).toLowerCase();
                    if(order ==1 && arr.length>0){
                        fileid = arr[0];
                    }else if(order ==2 && arr.length>1){
                        fileid = arr[1];
                    }
                }
                if(StringUtils.isNotBlank(fileid)){
                    fileMap.put("fileid",fileid);
                    fileMap.put("path",path);
                    fileMap.put("ext",ext);
                    //临时目录下只保存低分辨率头像
                    if(order ==2){
                        createPhotoToDir(fileMap.get("fileid"),fileMap.get("ext"));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return fileMap;
    }
    /**
     * 更新文件路径
     * path中有三个头像id，中间用逗号分隔
     * @param nbase
     * @param a0100
     * @param path
     * @return
     */
    private void updateFile(String nbase,String a0100,String path,String ext,String fileName,String userName){
        RowSet rs = null;
        String sql = "";
        String guidkey = "";
        try{
            ContentDAO dao = new ContentDAO(this.frameconn);
            rs = dao.search("select guidkey from " + nbase +"A01 where A0100='"+ a0100+"'");
            if(rs.next()){
                guidkey = rs.getString("guidkey");
            }
            rs = dao.search("select mainguid from hr_multimedia_file where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'");
            if(rs.next()){
                //将文件id更新到hr_multimedia_file
                dao.update("update hr_multimedia_file set path='"+path+"' where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'");
            }else{
                RecordVo vo = new RecordVo("hr_multimedia_file");
                IDGenerator idg = new IDGenerator(2, this.frameconn);
                String id = idg.getId("hr_multimedia_file.id");
                vo.setInt("id", Integer.parseInt(id));
                vo.setInt("displayorder", Integer.parseInt(id));
                vo.setString("mainguid", guidkey);
                vo.setString("childguid", guidkey);
                vo.setString("nbase", nbase);
                vo.setString("a0100", a0100);
                vo.setString("dbflag", "A");
                vo.setString("class", "P");
                vo.setString("path", path);
                vo.setString("ext", ext);
                vo.setString("srcfilename", fileName);
                vo.setString("createusername", userName);
                Date date = DateUtils.getSqlDate(Calendar.getInstance());
                vo.setDate("createtime", date);
                dao.addValueObject(vo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 创建人员头像到临时目录中
     * @param fileid
     * @param ext
     * @throws Exception
     */
    private void createPhotoToDir(String fileid,String ext) throws Exception{
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            FileSystemManager fsManager = VFS.getManager();
            String filePath = System.getProperty("java.io.tmpdir") + "/" + fileid + "." + ext;
            //VFS中路径直接使用/即可，同时支持windows和linux环境
            FileObject fileObject = fsManager.resolveFile(filePath);
            //判断临时目录是否已经有对应的文件，没有时才去创建
            if(!fileObject.exists()){
                inputStream = VfsService.getFile(fileid);
                outputStream = fileObject.getContent().getOutputStream();
                int len = 0;
                byte[] flush = new byte[1024];
                while((len=inputStream.read(flush)) != -1) {
                    outputStream.write(flush, 0, len);
                }
                outputStream.flush();
            }
        }catch (Exception e){
            throw e;
        }finally {
            PubFunc.closeIoResource(inputStream);
            PubFunc.closeIoResource(outputStream);
        }
    }

    /**
     * 从数库中获取照片并生成文件到临时目录
     * @param nbase 人员库前缀
     * @param a0100 人员编号
     * @return 返回照片URL地址
     */
    private String getPhotoFromDB(String nbase, String a0100) {
        StringBuffer photoUrl = new StringBuffer();
        String filename = "";
        if (StringUtils.isNotBlank(a0100) || !"A0100".equals(a0100)) {
            try {
                filename = ServletUtilities.createPhotoFile(nbase + "A00", a0100, "P", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (StringUtils.isNotBlank(filename)) {
            filename = PubFunc.encryption(filename);
//            filename = SafeCode.encode(filename);
            
            photoUrl.append("/servlet/vfsservlet?fileid=").append(filename).append("&fromjavafolder=true");
        } else {
            photoUrl.append("/images/photo.jpg");
        }
        
        return photoUrl.toString();
    }
    /**
     * 刷新照片文件（如果数据库中照片比文件夹下照片新，则重新生成照片-原图片和低分辨率图片）
     * @param imgRootPath 照片目录
     * @param nbase 人员库前缀
     * @param a0100 人员编号
     */
    private void refreshPhotoFileFromDB(String imgRootPath, String nbase, String a0100) {
        // 读取文件修改时间
        Date modifyDate = getPersonImageFileModifyTime(imgRootPath, "photo");
        Date modifyDateInDB = getPhotoModifyDateInDB(nbase, a0100);
        if (modifyDate != null && modifyDateInDB != null) {
            // 如果库中文件修改时间晚于文件修改时间，则需要重新生成照片文件
            if (modifyDateInDB.after(modifyDate)) {
                PhotoImgBo.delFileByName(imgRootPath, "photo,low_img");
                //重新生成照片文件
                String photoFileNameString = createPersonPhoto(imgRootPath, this.frameconn, nbase, a0100, "photo");
                //重新生成低分辨率照片文件
                compressJpegFile(new File(imgRootPath + photoFileNameString), new File(imgRootPath + "low_img.jpg"), 0.9995f);
            }
        }
    }
    
    /** 
     * 文件是否存在 
     * @param   sPath 文件路径 
     * @return 存在返回true，否则返回false 
     */  
    public boolean isExistsFile(String sPath) {  
        boolean flag = false;
        File file = new File(sPath);  
        // 存在
        if (file.isFile() && file.exists()) {
            flag = true; 
        }
        
        return flag;
    }
    /** 
     * 文件夹是否存在 
     * @param   sPath 文件夹路径 
     * @return 存在返回true，否则返回false 
     */  
    public boolean isExistdir(String sPath) {  
    	boolean flag = false;
    	File file = new File(sPath);  
    	// 存在
    	if (file.exists()) {
    		flag = true; 
    	}
    	
    	return flag;
    }

    /**
     *
     * @param sourceid
     * @param compressionQuality
     * @return
     */
    private InputStream compressFile(String sourceid,float compressionQuality){
        InputStream inputStream = null;
        InputStream input = null;
        try{
            inputStream = VfsService.getFile(sourceid);
            // 检索要压缩的图片
            RenderedImage rendImage = ImageIO.read(inputStream);

            // 找到一个jpeg writer
            ImageWriter writer = null;
            Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
            if (iter.hasNext()) {
                writer = (ImageWriter) iter.next();
            }
            String outfileStr = System.getProperty("java.io.tmpdir") + "/test.jpg";
            File outfile = new File(outfileStr);
            if(!outfile.exists()){
                outfile.createNewFile();
            }
            // 准备输出文件
            ImageOutputStream ios = ImageIO.createImageOutputStream(outfile);
            writer.setOutput(ios);

            // 设置压缩比
            ImageWriteParam iwparam = new MyImageWriteParam();
            iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwparam.setCompressionQuality(compressionQuality);

            // 写图片
            writer.write(null, new IIOImage(rendImage, null, null), iwparam);
            // 最后清理
            ios.flush();
            writer.dispose();
            ios.close();
            input = new FileInputStream(outfile);
            //删除临时文件
            if(outfile.exists()){
                outfile.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeIoResource(inputStream);
        }
        return input;
    }
     /**
     * @Description:压缩图片
     * @param @param infile
     * @param @param outfile
     * @param @return 
     * @author:chent   
    */
    public void compressJpegFile(File infile, File outfile,
			float compressionQuality) {
		try {
			// 检索要压缩的图片
			RenderedImage rendImage = ImageIO.read(infile);

			// 找到一个jpeg writer
			ImageWriter writer = null;
			Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
			if (iter.hasNext()) {
				writer = (ImageWriter) iter.next();
			}

			// 准备输出文件
			ImageOutputStream ios = ImageIO.createImageOutputStream(outfile);
			writer.setOutput(ios);

			// 设置压缩比
			ImageWriteParam iwparam = new MyImageWriteParam();
			iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwparam.setCompressionQuality(compressionQuality);

			// 写图片
			writer.write(null, new IIOImage(rendImage, null, null), iwparam);

			// 最后清理
			ios.flush();
			writer.dispose();
			ios.close();
		} catch (IOException e) {
		}
	}
    
	public byte[] getlowPir(byte[] b){
		try {
			ByteArrayInputStream in=new ByteArrayInputStream(b);
			//需要压缩的图片
			RenderedImage rendImage = ImageIO.read(in); 
			ImageWriter writer=null;
			Iterator iter=ImageIO.getImageWritersByFormatName("jpg");
			while(iter.hasNext()){
				writer = (ImageWriter) iter.next();
			}
			// 准备输出文件
			ByteArrayOutputStream outbyts=new ByteArrayOutputStream();
			ImageOutputStream ios=ImageIO.createImageOutputStream(outbyts);
			writer.setOutput(ios);

			// 设置压缩比
			ImageWriteParam iwparam = new MyImageWriteParam();
			iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwparam.setCompressionQuality(0.999F);

			// 写图片
			writer.write(null, new IIOImage(rendImage, null, null), iwparam);

			// 最后清理
			ios.flush();
			writer.dispose();
			ios.close();
			return outbyts.toByteArray();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
    
	
	
}
class MyImageWriteParam extends JPEGImageWriteParam {

	public MyImageWriteParam() {
		super(Locale.getDefault());
	}

	//这个类重写了setCompressionQuality方法，因为在压缩JPEG图片的时候可能会出现问题
	@Override
    public void setCompressionQuality(float quality) {
		if (quality < 0.0F || quality > 1.0F) {
			throw new IllegalArgumentException("Quality out-of-bounds!");
		}
		this.compressionQuality = 256 - (quality * 256);
	}
}
