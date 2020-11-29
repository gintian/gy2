package com.hjsj.hrms.module.system.distributedreporting.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.*;

/**
 *
 * 分布式上报操作文件工具类
 * @Titile: FileUtil
 * @Description:
 * @Company:hjsj
 * @Create time: 2019年6月4日下午1:46:42
 * @author: Zhiyh
 * @version 1.0
 *
 */
public class FileUtil {
    public static final int BUFFER = 8192;
    /**
     * 单位缩进字符串。
     */
    private static String SPACE = "   ";
    private File zipFile;
    private Category log = Category.getInstance(this.getClass().getName());
    public FileUtil(String pathName) {
        zipFile = new File(pathName);
    }
    public void compress(String... pathName) {
        ZipOutputStream out = null;
        FileOutputStream fileOutputStream = null;
        CheckedOutputStream cos = null;
        try {
        	fileOutputStream = new FileOutputStream(zipFile);
        	cos = new CheckedOutputStream(fileOutputStream,
                    new CRC32());
            out = new ZipOutputStream(cos);
            String basedir = "";
            for (int i=0;i<pathName.length;i++){
                compress(new File(pathName[i]), out, basedir);
            }
           
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
        	PubFunc.closeResource(out);
        	PubFunc.closeResource(fileOutputStream);
        	PubFunc.closeResource(cos);
        }
    }
    public void compress(String srcPathName) {
        File file = new File(srcPathName);
        FileOutputStream fileOutputStream = null;
        ZipOutputStream out = null;
        CheckedOutputStream cos = null;
        if (!file.exists())
            throw new RuntimeException(srcPathName + "不存在！");
        try {
        	fileOutputStream = new FileOutputStream(zipFile);
            cos = new CheckedOutputStream(fileOutputStream,
                    new CRC32());
            out = new ZipOutputStream(cos);
            String basedir = "";
            compress(file, out, basedir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
        	PubFunc.closeResource(cos);
        	PubFunc.closeResource(out);
        	PubFunc.closeResource(fileOutputStream);
        }
    }

    private void compress(File file, ZipOutputStream out, String basedir) {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            System.out.println("压缩：" + basedir + file.getName());
            this.compressDirectory(file, out, basedir);
        } else {
            System.out.println("压缩：" + basedir + file.getName());
            this.compressFile(file, out, basedir);
        }
    }

    /** 压缩一个目录 */
    private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists())
            return;

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }

    /** 压缩一个文件 */
    private void compressFile(File file, ZipOutputStream out, String basedir) {
        if (!file.exists()) {
            return;
        }
        BufferedInputStream bis = null;
        try {
        	bis = new BufferedInputStream(
                    new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
        	PubFunc.closeResource(bis);
        }
    }
    /**
     * 获得blob
     * @param imgbyte 字节
     * @param conn 数据库连接
     * @return blob
     */
    public static Blob getBlob(byte[] imgbyte,Connection conn) {
        Blob blob = null;
        try {
            blob = conn.createBlob();
            blob.setBytes(1, imgbyte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blob;
    }
    /**
     * 获取指定文件的byte数据
     * @param filePath
     * @return
     */
    public static byte[] getBytes(String filePath){
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        	PubFunc.closeResource(fis);
        	PubFunc.closeResource(bos);
        }
        return buffer;
    }
    /**
     * 根据文件夹的路径获取文件夹下的文件名（包括文件夹的名字）,
     * @param path 文件夹的路径
     * @return list<文件名>
     */
    public static List<String> getFile(String path) {
        List<String> list = new ArrayList<String>();
        try {
            File file = new File(path);
            if (file.exists()&&file.isDirectory()) {//如果文件夹存在
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    String filename = filelist[i];
                    list.add(filename);
                }
            }
            Collections.sort(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 集分式上报获取所有的待同步的数据包
     * @return list<String>  String:数据包的全路径
     */
    public static List<String> getDatapkg(Connection conn) {
        List<String> list = new ArrayList<String>();
        try {
            ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
            String path = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator+"asynrecive";
            List<String> unitcodeList = getFile(path);
            for (String unitcode : unitcodeList) {
                boolean stateFlag = getStateFlag(unitcode,conn);
                if(stateFlag){
                    List<String> datapkgnameList = getFile(path+File.separator+unitcode+File.separator+"zip");
                    for (String datapkgname : datapkgnameList) {
                        if(datapkgname.endsWith(".zip")||datapkgname.endsWith(".gz")||datapkgname.endsWith(".rar")){
                            String zipPath = path+File.separator+unitcode+File.separator+"zip"+File.separator+datapkgname;
                            list.add(zipPath);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 判断数据包中单位是否启用
     * @param unitcode
     * @param conn
     * @return
     */
    private static boolean getStateFlag(String unitcode, Connection conn) {
        boolean flag = false;
        RowSet rs = null;
        int state = 0;
        try{
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search("select state from t_sys_asyn_scheme where unitcode = '"+unitcode+"'");
            if(rs.next()){
               state = rs.getInt("state");
            }
            if(state == 1){
                flag = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return flag;
    }

    /**
     * 生成加密的压缩文件
     * @param zipPath 压缩包路径
     * @param list 压缩包里需要文件的路径
     * @param password 加密密码
     */
   public static void createEncrypZip(String zipPath,List<String> list,String password) {
	   try {
		   // 保证创建一个新文件
           File file = new File(zipPath);
           if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
               file.getParentFile().mkdirs();
           }
           if (file.exists()) { // 如果已存在,删除旧文件
               file.delete();
           }
           //创建压缩文件
           ZipFile zipFile = new ZipFile(zipPath);
           ArrayList<File> files = new ArrayList<File>();
           for (String filepath : list) {
        	   files.add(new File(filepath));
           }
           //设置压缩文件参数
           ZipParameters parameters = new ZipParameters();
           //设置压缩方法
           parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

           //设置压缩级别
           //DEFLATE_LEVEL_FASTEST     - Lowest compression level but higher speed of compression
           //DEFLATE_LEVEL_FAST        - Low compression level but higher speed of compression
           //DEFLATE_LEVEL_NORMAL  - Optimal balance between compression level/speed
           //DEFLATE_LEVEL_MAXIMUM     - High compression level with a compromise of speed
           //DEFLATE_LEVEL_ULTRA       - Highest compression level but low speed
           parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
           if (StringUtils.isNotEmpty(password)) {
        	    //设置压缩文件加密
               parameters.setEncryptFiles(true);

               //设置加密方法
               parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

               //设置aes加密强度
               parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        	   //设置密码
               parameters.setPassword(password);
		   }
           zipFile.createZipFile(files, parameters);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

    /**
     * 生成加密的压缩文件
     * @param zipPath 压缩包路径
     * @param list 压缩包里需要文件的路径
     * @param password 加密密码
     */
    public static void createSplitZip(String zipPath,List<String> list,String password) {
        try {
            // 保证创建一个新文件
            File file = new File(zipPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            //创建压缩文件
            ZipFile zipFile = new ZipFile(zipPath);
            ArrayList<File> files = new ArrayList<File>();
            for (String filepath : list) {
                files.add(new File(filepath));
            }
            //设置压缩文件参数
            ZipParameters parameters = new ZipParameters();
            //设置压缩方法
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            //设置压缩级别
            //DEFLATE_LEVEL_FASTEST     - Lowest compression level but higher speed of compression
            //DEFLATE_LEVEL_FAST        - Low compression level but higher speed of compression
            //DEFLATE_LEVEL_NORMAL  - Optimal balance between compression level/speed
            //DEFLATE_LEVEL_MAXIMUM     - High compression level with a compromise of speed
            //DEFLATE_LEVEL_ULTRA       - Highest compression level but low speed
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            if (StringUtils.isNotEmpty(password)) {
                //设置压缩文件加密
                parameters.setEncryptFiles(true);

                //设置加密方法
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

                //设置aes加密强度
                parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                //设置密码
                parameters.setPassword(password);
            }
            zipFile.createZipFile(files, parameters, true, 52428800);//52428800
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /**
    * 获取ZIP文件中的文件名和目录名
    * @param zipFilePath
    * @param password
    * @return
    */
   public static List<String> getEntryNames(String zipFilePath, String password){
       List<String> entryList = new ArrayList<String>();
       ZipFile zf;
       try {
           zf = new ZipFile(zipFilePath);
           zf.setFileNameCharset("gbk");//默认UTF8，如果压缩包中的文件名是中文会出现乱码
           if(zf.isEncrypted()){
               zf.setPassword(password);//设置压缩密码
           }
           for(Object obj : zf.getFileHeaders()){
               FileHeader fileHeader = (FileHeader)obj;
               String fileName = fileHeader.getFileName();//文件名会带上层级目录信息
               entryList.add(fileName);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return entryList;
   }
   /**
    * 将ZIP包中的文件解压到指定目录
    * @param zipFilePath 压缩包路径
    * @param password 解压密码
    * @param destDir 指定目录
    */
   public static void decryptZip(String zipFilePath, String password, String destDir){
       ZipFile zf;
       InputStream is = null;
       OutputStream os = null;
       try {
           zf = new ZipFile(zipFilePath);
           zf.setFileNameCharset("gbk");
           if(zf.isEncrypted()){
               zf.setPassword(password);
           }
           File file = new File(destDir);
           if (!file.exists()) {
               file.mkdirs();
           }
           for(Object obj : zf.getFileHeaders()){
               
               FileHeader fileHeader = (FileHeader)obj;
               String destFilePath = destDir + "/" + fileHeader.getFileName();
               File destFile = new File(destFilePath);
               if(!destFile.getParentFile().exists()){
                   destFile.getParentFile().mkdirs();//创建目录
               }
               is = zf.getInputStream(fileHeader);
               os = new FileOutputStream(destFile);
               int readLen = -1;
               byte[] buff = new byte[BUFFER];
               while ((readLen = is.read(buff)) != -1) {
                   os.write(buff, 0, readLen);
               }
               PubFunc.closeResource(os);
           }
       } catch (Exception e){
           e.printStackTrace();
       } finally {
    	   PubFunc.closeResource(is);
    	   PubFunc.closeResource(os);
       }
   }

    public static void decryptZip4j(String zipFile,String password, String destDir) {
        try{
            ZipFile zip = new ZipFile(zipFile);
            //第一时间设置编码格式
            zip.setFileNameCharset("GBK");
            //用自带的方法检测一下zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
            if (!zip.isValidZipFile()) {
                throw new ZipException("文件不合法或不存在");
            }
            if(zip.isEncrypted()){
                zip.setPassword(password);
            }
            // 跟java自带相比，这里文件路径会自动生成，不用判断
            zip.extractAll(destDir);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

   /**
    * 将ZIP包中的文件中的指定文件解压到指定目录
    * @param zipFilePath 压缩包路径
    * @param password 解压密码
    * @param destDir 指定目录
    */
   public static void decryptZipOneFile(String zipFilePath,String filename, String password, String destDir){
       InputStream is = null;
       OutputStream os = null;
       ZipFile zf = null;
       try {
           zf = new ZipFile(zipFilePath);
           zf.setFileNameCharset("gbk");
           if(zf.isEncrypted()){
               zf.setPassword(password);
           }
           for(Object obj : zf.getFileHeaders()){
               FileHeader fileHeader = (FileHeader)obj;
               if (filename.equalsIgnoreCase(fileHeader.getFileName())) {
                   String destFilePath = destDir + File.separator + fileHeader.getFileName();
                   File destFile = new File(destFilePath);
                   if(!destFile.getParentFile().exists()){
                       destFile.getParentFile().mkdirs();//创建目录
                   }
                   is = zf.getInputStream(fileHeader);
                   os = new FileOutputStream(destFile);
                   int readLen = -1;
                   byte[] buff = new byte[BUFFER];
                   while ((readLen = is.read(buff)) != -1) {
                       os.write(buff, 0, readLen);
                   }
                   PubFunc.closeResource(os);
               }
           }
       } catch (Exception e){
           e.printStackTrace();
       } finally {
           //关闭资源
    	   PubFunc.closeIoResource(is);
    	   PubFunc.closeIoResource(os);
       }
   }
   /**
    * 生成.json格式文件
    * @param jsonString json格式字符串
    * @param filePath 路径
    * @param fileName 文件名 不包括.json
    * @return 成功返回true ，失败返回false
    */
   public static boolean createJsonFile(String jsonString, String filePath, String fileName) {
       // 标记文件生成是否成功
       boolean flag = true;

       // 拼接文件完整路径
       String fullPath = filePath + File.separator + fileName + ".json";
       Writer write = null;
       // 生成json格式文件
       try {
           // 保证创建一个新文件
           File file = new File(fullPath);
           if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
               file.getParentFile().mkdirs();
           }
           if (file.exists()) { // 如果已存在,删除旧文件
               file.delete();
           }
           file.createNewFile();

           // 格式化json字符串
           jsonString = formatJson(jsonString);

           // 将格式化后的字符串写入文件
           write = new OutputStreamWriter(new FileOutputStream(file), "GBK");
           write.write(jsonString);
           write.flush();
       } catch (Exception e) {
           flag = false;
           e.printStackTrace();
       } finally {
    	   PubFunc.closeResource(write);
       }
       // 返回是否成功的标记
       return flag;
   }
   /**
    * 判断文件是否存在
    * @param filePath 文件路径
    * @param fileName 文件名（带后缀）
    * @return 存在返回true,不存在返回false
    */
   public static boolean fileExistence(String filePath, String fileName) {
   	boolean flag = false;
       try {
       	// 拼接文件完整路径
           String fullPath = filePath + File.separator + fileName;
       	File file = new File(fullPath);
       	if (file.exists()) {
               flag = true;
           }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
   }
   /**
    * 删除文件
    * @param filePath 文件路径
    * @param fileName 文件名（带后缀）
    */
   public static void delfile(String filePath, String fileName) {
       try {
           	// 拼接文件完整路径
            String fullPath = filePath + File.separator + fileName;
           	File file = new File(fullPath);
           	if (file.exists()) {
                   file.delete();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
   }
   /**
    * 删除文件或文件夹
    * @param filePath 文件路径
    */
   public static void delfile(String filePath) {
       try {
            File file = new File(filePath);
            if (file.exists()) {
                   file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
   }
   /*
   * 移动 文件或者文件夹
   * @param oldPath 包括文件
   * @param newPath 不包括文件名
   * @throws IOException
   */
  public static void moveTo(String oldPath,String newPath) throws IOException {
      copyFile(oldPath,newPath,null);
      deleteFile(oldPath);
  }

  /**
   * 删除 文件
   * @param filePath 如果是文件夹则删除该目录下的所有文件。
   */
  public static void deleteFile(String filePath){
      File file = new File(filePath);
      if (!file.exists()) {
          return;
      }
      if (file.isDirectory() ) {
          File[] list = file.listFiles();

          for (File f : list) {
              deleteFile(f.getAbsolutePath()) ;
          }
      }else {
          boolean result=file.delete();
      }

  }

  /**
   * 复制 文件或者文件夹
   * @param oldPath //老文件的路径，如果是文件则带名字
   * @param newPath //新文件的路径不带文件名。
   * @param name  //新文件的名字，如果和老文件名保持一致则为null；复制文件夹是此参数无效
   * @throws IOException
   */
  public static void  copyFile(String oldPath ,String newPath,String name ) throws IOException {
      try {
          File oldFile = new File(oldPath) ;
          File newFile = new File(newPath) ;
          if (!newFile.exists()) {
              newFile.mkdirs();
          }
          if  (oldFile.exists())  {
              if(oldFile.isDirectory()){ // 如果是文件夹
                  File newPathDir = new File(newPath);
                  newPathDir.mkdirs();
                  File[] lists = oldFile.listFiles() ;
                  if(lists != null && lists.length> 0 ){
                      for (File file : lists) {
                          copyFile(file.getAbsolutePath(), newPath,null) ;
                      }
                  }
              }else {
                  InputStream  inStream  = null;
                  FileOutputStream  fs  = null;
                  String filename= name==null?oldFile.getName():name;
                  try {
                      inStream  =  new  FileInputStream(oldFile);  //读入原文件
                      fs  =  new  FileOutputStream(newPath+File.separator+filename);
                      write2Out(inStream ,fs) ;
                  } catch (Exception e) {
                       e.printStackTrace();
                  }finally {
                      PubFunc.closeIoResource(inStream);
                      PubFunc.closeIoResource(fs);
                   }

              }
          }
      } catch (Exception e) {
         e.printStackTrace();
      }
  }

  /**
   * 重命名文件
   * @param file
   * @param name
   * @return
   */
  public static File renameFile(File file , String name ){
      String fileName = file.getParent()  + File.separator + name ;
      File dest = new File(fileName);
      file.renameTo(dest) ;
      return dest ;
  }

  /**
   * 压缩多个文件。
   * @param zipFileName 压缩输出文件名
   * @param files 需要压缩的文件
   * @return
   * @throws Exception
   */
  public static File createZip(String zipFileName, File... files) throws Exception {
      File outFile = new File(zipFileName) ;
      ZipOutputStream out = null;
      BufferedOutputStream bo = null;
      try {
          out = new ZipOutputStream(new FileOutputStream(outFile));
          bo = new BufferedOutputStream(out);

          for (File file : files) {
              zip(out, file, file.getName(), bo);
          }
      } catch (Exception e) {
          e.printStackTrace();
      }finally {
    	  PubFunc.closeResource(bo);
    	  PubFunc.closeResource(out);
      }
      return outFile;
  }

  /**
   *
   * @param zipFileName 压缩输出文件名
   * @param inputFile 需要压缩的文件
   * @return
   * @throws Exception
   */
  public static File createZip(String zipFileName, File inputFile) throws Exception {
      File outFile = new File(zipFileName) ;
      ZipOutputStream out = null;
      BufferedOutputStream bo = null;
      try {
          out = new ZipOutputStream(new FileOutputStream(outFile));
          bo = new BufferedOutputStream(out);
          zip(out, inputFile, inputFile.getName(), bo);
      } catch (Exception e) {
          e.printStackTrace();
      }finally {
    	  PubFunc.closeResource(bo);
    	  PubFunc.closeResource(out);
      }
      return outFile;
  }

  private static void zip(ZipOutputStream out, File f, String base,BufferedOutputStream bo) throws Exception { // 方法重载
      if (f.isDirectory()) {
          File[] fl = f.listFiles();
          if ( fl == null ||  fl.length == 0) {
              out.putNextEntry(new ZipEntry(base + "/")); // 创建创建一个空的文件夹
          }else{
              for (int i = 0; i < fl.length; i++) {
                  zip(out, fl[i], base + "/" + fl[i].getName(), bo); // 递归遍历子文件夹
              }
          }

      } else {
          out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入 base 文件
          System.out.println(base);
          BufferedInputStream bi = new BufferedInputStream(new FileInputStream(f));

          try {
              write2Out(bi,out) ;
          } catch (IOException e) {
              //Ignore
          }finally {
             PubFunc.closeResource(bi);
          }
      }
  }

  public static void write2Out(InputStream input, OutputStream out) throws IOException {
      byte[] b = new byte[1024];
      int c = 0 ;
      while ( (c = input.read(b)) != -1 ) {
          out.write(b,0,c);
          out.flush();
      }
      PubFunc.closeResource(out);
      //out.close();
  }
  /**
   * 返回格式化JSON字符串。
   *
   * @param json 未格式化的JSON字符串。
   * @return 格式化的JSON字符串。
   */
  public static String formatJson(String json) {
      StringBuffer result = new StringBuffer();

      int length = json.length();
      int number = 0;
      char key = 0;

      // 遍历输入字符串。
      for (int i = 0; i < length; i++) {
          // 1、获取当前字符。
          key = json.charAt(i);

          // 2、如果当前字符是前方括号、前花括号做如下处理：
          if ((key == '[') || (key == '{')) {
              // （1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
              if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                  result.append('\n');
                  result.append(indent(number));
              }

              // （2）打印：当前字符。
              result.append(key);

              // （3）前方括号、前花括号，的后面必须换行。打印：换行。
              result.append('\n');

              // （4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
              number++;
              result.append(indent(number));

              // （5）进行下一次循环。
              continue;
          }

          // 3、如果当前字符是后方括号、后花括号做如下处理：
          if ((key == ']') || (key == '}')) {
              // （1）后方括号、后花括号，的前面必须换行。打印：换行。
              result.append('\n');

              // （2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
              number--;
              result.append(indent(number));

              // （3）打印：当前字符。
              result.append(key);

              // （4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
              if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
                  result.append('\n');
              }

              // （5）继续下一次循环。
              continue;
          }

          // 4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
          if ((key == ',')) {
              if ((json.charAt(i - 1) =='"')||(json.charAt(i - 1) =='}')||(json.charAt(i - 1) ==']')){
                  result.append(key);
                  result.append('\n');
                  result.append(indent(number));
                  continue;
              }else{
                  result.append(key);
                  continue;
              }

          }

          // 5、打印：当前字符。
          result.append(key);
      }

      return result.toString();
  }

  /**
   * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
   *
   * @param number 缩进次数。
   * @return 指定缩进次数的字符串。
   */
  private static String indent(int number) {
      StringBuffer result = new StringBuffer();
      for (int i = 0; i < number; i++) {
          result.append(SPACE);
      }
      return result.toString();
  }
   public static void main(String[] args) {
        //创建压缩文件例子
        /*FileUtil zc = new FileUtil("C:/Users/wj/Desktop/aaa.zip");
        zc.compress("C:/Users/wj/Desktop/water.js","C:/Users/wj/Desktop/index.jsp"); */
        //创建不加密压缩文件例子。
   }
    /**
     * 生成图片
     * @param in 流
     * @param filePath 路径
     * @param fileName 文件名 不包括.xxx
     * @return 成功返回true ，失败返回false
     */
    public static boolean createPhotoFile(InputStream in, String filePath, String fileName) {
        // 标记文件生成是否成功
        boolean flag = true;

        // 拼接文件完整路径
        String fullPath = filePath + File.separator + fileName;
        FileOutputStream  fs  = null;
        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();

            fs  =  new  FileOutputStream(fullPath);
            write2Out(in ,fs) ;
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }finally {
            PubFunc.closeIoResource(in);
            PubFunc.closeResource(fs);
        }
        // 返回是否成功的标记
        return flag;
    }
    /**
     * 保存文件路径
     * @author wangbs
     * @param savePath 文件路径
     * @return void
     * @throws
     * @date 2020/3/2 14:36
     */
    public static String saveFilePath(String savePath,Connection conn) {
        String saveFilePathMsg = "success";
        ContentDAO dao = new ContentDAO(conn);
        try {
            if (!checkPathExist(savePath)) {
                //"存储路径配置错误，请重新配置！";
                saveFilePathMsg = "checkPathExistError";
                return saveFilePathMsg;
            }
            Element root = new Element("filepath");
            root.setAttribute("rootpath", savePath);
            Document doc = new Document(root);

            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");

            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(format);
            String xmlStr = outputter.outputString(doc);

            RecordVo option_vo = ConstantParamter.getRealConstantVo("FBTB_FILEPATH", conn);
            if (option_vo != null) {
                option_vo = new RecordVo("constant");
                option_vo.setString("constant", "FBTB_FILEPATH");
                option_vo = dao.findByPrimaryKey(option_vo);
                option_vo.setString("str_value", xmlStr);
                dao.updateValueObject(option_vo);
            } else {
                option_vo = new RecordVo("constant");
                option_vo.setString("constant", "FBTB_FILEPATH");
                option_vo.setString("str_value", xmlStr);
                dao.addValueObject(option_vo);
            }
            ConstantParamter.putConstantVo(option_vo, "FBTB_FILEPATH");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveFilePathMsg;
    }
    /**
     * 校验路径是否合法
     * @author wangbs
     * @param path
     * @return boolean
     * @date 2020/6/3
     */
    private static boolean checkPathExist(String path){
        boolean pathExistFlag = false;
        File file = new File(path);
        if (path.indexOf(":") == 1 && (file.mkdir() || file.isDirectory())) {
            pathExistFlag = true;
        }
        return pathExistFlag;
    }
    /**
     * 获取文件保存地址
     * @author wangbs
     * @return String
     * @date 2020/3/2 15:02
     */
    public static String getSaveFilePath() {
        String saveFilePath = "";
        try {
            RecordVo vo = ConstantParamter.getConstantVo("FBTB_FILEPATH");
            if (vo != null) {
                String strValue = vo.getString("str_value");
                Document doc = PubFunc.generateDom(strValue);

                String xpath = "/filepath";
                XPath path = XPath.newInstance(xpath);
                Element paramsxml = (Element) path.selectSingleNode(doc);
                saveFilePath = paramsxml.getAttributeValue("rootpath");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveFilePath;
    }
}
