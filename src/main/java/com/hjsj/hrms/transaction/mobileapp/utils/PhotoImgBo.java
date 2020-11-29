package com.hjsj.hrms.transaction.mobileapp.utils;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.UUID;

/**
 * 头像图片操作类
 * 可获得头像图片路径
 * 切割头像处理操作
 * @author tiany
 *
 */
public class PhotoImgBo {
    private Connection frameconn=null;
    private String guid;
    
    
    
    public PhotoImgBo(Connection conn){
    	frameconn = conn;
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
    	String mainguid =getGuidKey(dbpre+"A01",A0100);
    	
        String relative ="photo";
        try{
            String str  = mainguid; 
            int iHash = Math.abs(str.hashCode());
            String dir1 = ""+iHash/1000000%500;
            while (dir1.length()<3) dir1 ="0"+dir1;
            String dir2 = ""+iHash/1000%500;
            while (dir2.length()<3) dir2 ="0"+dir2;     
            relative =relative + File.separator+"P"+dir1 + File.separator+"P"+dir2 ;            
            relative =relative+File.separator+mainguid+File.separator;
            
            
            this.guid = mainguid;
            
        }
        catch(Exception e){
           e.printStackTrace();            
        }
        return relative;
         
    } 
    public String  getPhotoRootDir() throws GeneralException{
    	  ConstantXml constantXml = new ConstantXml(this.frameconn,"FILEPATH_PARAM");
          String RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
         
          if ((RootDir==null) || ("".equals(RootDir))){
        	  throw new GeneralException("没有配置多媒体存储路径！");
          }   
          
          RootDir=RootDir.replace("\\",File.separator);          
          if (!RootDir.endsWith(File.separator)) 
        	  RootDir =RootDir+File.separator;          
          
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
            int indexInt=fname.lastIndexOf(".");
	   	 	 fname=fname.substring(0,indexInt);
            if (temp.isFile()&&fileName.contains(fname)) {
               temp.delete();
            }
           
         }
         return flag;
       }
     /** 对图片裁剪，并把裁剪完的新图片保存 
     * @throws Exception */
     public   void cut( String srcpath,String subpath,int x,int y,int width,int height, String ext) throws Exception {
     	FileInputStream is = null;
     	ImageInputStream iis = null;
     	try {
     		// 读取图片文件
     		is = new FileInputStream(srcpath);
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
     		ImageIO.write(bi, ext, new File(subpath));
     	}  catch (Exception e) {  
     		 throw e;
         }  finally {
     		if (is != null)
     			is.close();
     		if (iis != null)
     			iis.close();
     	}
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
         java.io.FileOutputStream fout = null;
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
	                tempFile = new File(path+fileName+rs.getString("ext"));
	                tempFile.createNewFile();
	                //tempFile = File.c  createTempFile(fileName, rs.getString("ext"),
	                //        new File(path));
	                //tempFile.renameTo(new File(fileName+"."+rs.getString("ext")));
	                in = rs.getBinaryStream("Ole");                
	                fout = new java.io.FileOutputStream(tempFile); 
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
         
     class ImageNameFilter implements FilenameFilter{

 		String fileName;
 		public ImageNameFilter(String fileName){
 			this.fileName = fileName;
 		}
 		
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
          *
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
        if (nbase == null || nbase.length() < 1) {
            return "/images/photo.jpg";
        }
        StringBuffer photoUrl = new StringBuffer();
        try {
            String imgRootPath = null;
            try{
                imgRootPath = getPhotoRootDir();
            } catch(Exception e) {}
            
            if (imgRootPath != null && imgRootPath.length() > 0) {
                String path = imgRootPath + getPhotoRelativeDir(nbase, a0100);
                // szk 查找是否有头像设置的图片，没有的话创建默认photo.jpg
                String fileWName = getPersonImageWholeName(path, "h_img");

                // 如果不存在文件，创建文件
                if (fileWName.length() < 1) {
                    fileWName = getPersonImageWholeName(path, "photo");
                    if (fileWName.length() < 1) {
                        fileWName = createPersonPhoto(path, this.frameconn, nbase, a0100, "photo");
                    }
                }

                String filepath = path + fileWName;
                // filepath=filepath.replace(File.separator, "`");
                // 如果创建失败，使用默认图像
                if (fileWName.length() < 1) {
                    photoUrl.append("/images/photo.jpg");
                } else { // 如果有图片或创建了图片，使用新图片
                    filepath = PubFunc.encryption(filepath);
                    filepath = SafeCode.encode(filepath);
                    photoUrl.append("/servlet/DisplayOleContent?filePath=").append(filepath).append("&bencrypt=true");
                }
            } else { // 如果没有设置附件路径，则直接去库里拿图片
                String filename = "";
                if ("".equals(a0100)) {
                    filename = "";
                } else {
                    filename = ServletUtilities.createPhotoFile(nbase + "A00", a0100, "P", null);
                }

                if (!"".equals(filename)) {
                    photoUrl.append("/servlet/DisplayOleContent?filename=");
                    filename = PubFunc.encryption(filename);
                    filename = SafeCode.encode(filename);
                    photoUrl.append(filename);
                } else {
                    photoUrl.append("/images/photo.jpg");
                }
            }
        } catch (Exception e) {}
        
        if (photoUrl.toString().length() < 1) {
            photoUrl.append("/images/photo.jpg");
        }
        
        return photoUrl.toString();
    }     
    
public static String getPhotoPath(Connection conn,String dbpre,String a0100){
		
		StringBuffer photourl = new StringBuffer();
		
		try{
			PhotoImgBo photoImgBo = new PhotoImgBo(conn);
			 String dir =photoImgBo.getPhotoRootDir() + photoImgBo.getPhotoRelativeDir(dbpre,a0100) ;
	         dir = dir.replace("\\", File.separator);
	         //查找文件是否存在
	         File file = new File(dir);
	         String[] tempList = file.list();
	         File temp = null;
	         boolean fileExist = false;
	         String photoType ="";
	         boolean flag = false;
	         for (int i = 0; tempList!=null&&i < tempList.length; i++) {
	        	 temp = new File(dir + File.separator + tempList[i]);
	        	 String fname = temp.getName();
	        	 int indexInt=fname.lastIndexOf(".");
	 	   	 	 photoType=fname.substring(indexInt+1,fname.length());
	 	   	 	 fname=fname.substring(0,indexInt);
	        	 if(temp.isFile()&&"h_source".equals(fname)){
	        		 fileExist =true;
	        		 break;
	        	 }
	        	 if(temp.isFile()&&"h_img".equals(fname)){
	        		 flag = true;
	        	 }
	         }
	         String photoname="";
	         if(fileExist){
	        	 photoname=dir.replace(File.separator, "`");
	         }else{
	        	 //首次设置默认显示此人的人事档案照片  jingq add  2014.08.19
	        	 String ext = getUsrPhoto(dir,conn,dbpre+"A00",a0100);
	        	 if(!"".equals(ext)){
	        		 String old = dir+"photo"+ext;
	        		 String souce = dir+"h_source"+ext;
	        		 
	        		 createPhoto(old,souce);
	        		 photoname=dir.replace(File.separator, "`");
	        		 photoType = ext.substring(1);
	        	 } else {
	        	 }
	         }
		 //String filename=ServletUtilities.createPhotoFile(userView.getDbname()+"A00",userView.getA0100(),"P",null);
         if(!"".equals(photoname)){
        	 if(flag){
        		 photourl.append("/servlet/DisplayOleContent?filePath="+java.net.URLEncoder.encode(photoname+"h_img."+photoType));
        		 photourl.append("&openflag=true&fromflag=multimedia&filename="+SafeCode.encode(PubFunc.encrypt("h_img."+photoType)));
        	 }else{
        		 photourl.append("/servlet/DisplayOleContent?filePath="+java.net.URLEncoder.encode(photoname+"photo."+photoType));
        		 photourl.append("&openflag=true&fromflag=multimedia&filename="+SafeCode.encode(PubFunc.encrypt("photo."+photoType)));
        	 }
        	 
         } else {
             photourl.append("/images/photo.jpg");
         }
		}catch(Exception e){
			e.printStackTrace();
		}
		return photourl.toString();
	}
         


/**
 * 
 * @Title: getUsrPhoto   
 * @Description:生成当前用户人事档案照片    
 * @param @param dir
 * @param @return 
 * @return String    
 * @author jingq
 */
private static String getUsrPhoto(String dir,Connection conn,String dbname,String usr){
	StringBuffer sql = new StringBuffer();
	ResultSet rs = null;
	FileOutputStream fos = null;
	InputStream is = null;
	String ext = "";
	try {
		dir =dir.replace("\\", File.separator);
		File tempDir = new File(dir);
		if(!tempDir.exists()){
			tempDir.mkdirs();
		}
		
		sql.append("select Ole,ext from ");
		sql.append(dbname);
		sql.append(" where A0100 = '");
		sql.append(usr);
		sql.append("' and flag = 'p'");
		
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(sql.toString());
		int v = -1;
		if(rs.next()){
			ext = rs.getString("ext");
			File file = new File(dir+"photo"+ext);
			if(!file.exists()){
				fos = new FileOutputStream(file);
				//在获取任何其他列的值之前必须读取返回流中的所有数据，下一次调用获取方法将隐式关闭该流。
				is = rs.getBinaryStream("Ole");
				if(is!=null){
					byte[] bytes = new byte[1024];
					while((v = is.read(bytes, 0, 1024))!=-1){
						fos.write(bytes, 0, v);
					}
				}
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally{
	    PubFunc.closeIoResource(fos);
        PubFunc.closeIoResource(is);
        PubFunc.closeResource(rs);
	}
	return ext;
}

/**
 * 
 * @Title: createPhoto   
 * @Description: copy照片   
 * @param @param olddir
 * @param @param newdir 
 * @return void    
 * @author jingq
 */
private static void createPhoto(String olddir,String newdir){
	InputStream in = null;
	OutputStream out = null;
	try {
		in = new FileInputStream(olddir);
		out = new FileOutputStream(newdir);
		byte[] bytes = new byte[1024];
		int v = -1;
		while((v = in.read(bytes, 0, 1024))!=-1){
			out.write(bytes, 0, v);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}  finally {
		PubFunc.closeResource(out);
		PubFunc.closeResource(in);
	}
}
}
