package com.hjsj.hrms.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.virtualfilesystem.service.VfsService;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * <p>
 * Title:ServletUtilities
 * </p>
 * <p>
 * Description:主要用于对文件照片对象的保存
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 15, 2005:3:46:27 PM
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 *  
 */
public class ServletUtilities {

    private static Logger logger = LoggerFactory.getLogger(ServletUtilities.class);

    public static String tempFilePrefix = "ole-";

    /**
     *  
     */
    public ServletUtilities() {
        super();
    }

    public static String getTempFilePrefix() {
        return tempFilePrefix;
    }

    public static void setTempFilePrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Null 'prefix' argument.");
        }
        ServletUtilities.tempFilePrefix = tempFilePrefix;
    }

    /** 创建临时文件夹 */
    public static void createTempDir() {
        String tempDirName = System.getProperty("java.io.tmpdir");
        if (tempDirName == null) {
            throw new RuntimeException(
                    "Temporary directory system property (java.io.tmpdir) is null.");
        }
        // create the temporary directory if it doesn't exist
        File tempDir = new File(tempDirName);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    /** 在侦听器中注册，会话退出时自动删除临时文件 */
    public static void registerPhotoForDeletion(File tempFile,
            HttpSession session) {
        //  Add chart to deletion list in session
        if (session != null) 
        {
            PhotoFileDeleter photoDeleter = (PhotoFileDeleter) session
                    .getAttribute("Ole_Deleter");
            if (photoDeleter == null) 
            {
                photoDeleter = new PhotoFileDeleter();
                session.setAttribute("Ole_Deleter", photoDeleter);
            }
            photoDeleter.addTempFile(tempFile.getName());
        } 
        else 
        {
            System.out.println("Session is null - photo will not be deleted");
        }
    }
    
    /** 在侦听器中注册，会话退出时自动删除临时文件,支持传入文件名 */
    public static void registerPhotoForDeletion(String tempFileName,HttpSession session) {
        if (session != null){
            PhotoFileDeleter photoDeleter = (PhotoFileDeleter) session.getAttribute("Ole_Deleter");
            if (photoDeleter == null){
                photoDeleter = new PhotoFileDeleter();
                session.setAttribute("Ole_Deleter", photoDeleter);
            }
            photoDeleter.addTempFile(tempFileName);
        }else{
            System.out.println("Session is null - photo will not be deleted");
        }
    }
    
    /**
     * 动态生成校验图片
     * @param codelen
     * @param session
     * @return
     * @throws Exception
     */
    public static String createValidateCodeImage(int codelen,HttpSession session)throws Exception{
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
    	StringBuffer strSrc=new StringBuffer();
    	Random random;    
    	FileOutputStream fout = null;
        try
        {
    		strSrc.append("QAZWSXEDCRFVTGBYHNUJMIKLP123456789");
    		random=new Random(System.currentTimeMillis()); 
    		
    		StringBuffer strpwd=new StringBuffer();
    		int index=0;
    		for(int i=0;i<codelen;i++)
    		{
    			index=random.nextInt(35);
    			strpwd.append(strSrc.charAt(index));
    		}
    		session.setAttribute("validatecode",strpwd.toString());
            Font mFont = new Font("SimSun", Font.TRUETYPE_FONT, 16);         	
            
            tempFile = File.createTempFile(ServletUtilities.tempFilePrefix,".jpg",
                    new File(System.getProperty("java.io.tmpdir")));   
            fout = new FileOutputStream(tempFile);
	        BufferedImage image = new BufferedImage(60,19,BufferedImage.TYPE_INT_RGB);
	        Graphics2D gra = (Graphics2D)image.getGraphics();
	        //@arithmetic1=设置背景色
	        gra.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	        gra.setColor(Color.white);
	        gra.fillRect(0, 0, 60, 19);
	        /**干扰*/
            for(int i=0; i<10; i++) {
                int xs = random.nextInt(80);
                int ys = random.nextInt(24);
                int xe = xs+random.nextInt(20);
                int ye = ys+random.nextInt(3);
                int red = random.nextInt(255);
                int green = random.nextInt(255);
                int blue = random.nextInt(255);
                gra.setColor(new Color(red, green, blue));
                gra.drawLine(xs,ys,xe,ye);
            }
            
            for(int i=0; i<400; i++)
            {
             int x = random.nextInt(100);
             int y = random.nextInt(25);
             gra.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
             gra.drawLine(x,y,x,y);
            }    
            gra.setFont(mFont);         
            //@arithmetic1=输出数字
            char c;
            for (int i = 0; i < codelen; i++) {
                int red = random.nextInt(255);
                int green = random.nextInt(255);
                int blue = random.nextInt(255);
                //@arithmetic1=设置字体色
                gra.setColor(new Color(red, green, blue));
                c = strpwd.charAt(i);
                gra.drawString(String.valueOf(c) , i * 21 + 5, 20);
            }
	        
	        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fout);
	        encoder.encode(image);
	       

            if (session != null) {
                registerPhotoForDeletion(tempFile, session);
            }
            //System.out.println("fieldname " + userNumber);
            filename= tempFile.getName();      	        
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }finally
        {
        	if(fout!=null)
        	 fout.close();
        }
        return filename;        
    }
    /**
     * 动态生成校验图片
     * @param codelen
     * @param session
     * @return
     * @throws Exception
     */
    public static String createValidateCodeImage(int codelen,StringBuffer strSrc,HttpSession session)throws Exception{
        return ValidateCodeImageFile(codelen, strSrc, session, 1);
    }
    /**
     * 动态生成校验图片
     * @param codelen
     * @param session
     * @return
     * @throws Exception
     */
    public static String createValidateCodeImage1(int codelen,StringBuffer strSrc,HttpSession session)throws Exception{
        return ValidateCodeImageFile(codelen, strSrc, session, 0);
    }
    
    public static String createTemplateFile(String idnum,
            String flag, HttpSession session) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;
        Connection conn = null;
        FileOutputStream fout = null;
        InputStream in=null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select content from t_wf_template");
            strsql.append(" where tp_id=");
            strsql.append(idnum);
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
           
            rs=dao.search(strsql.toString());
            //System.out.println("SQL="+strsql.toString());
            if (rs.next()) {
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, ".doc",
                        new File(System.getProperty("java.io.tmpdir")));
                 in = rs.getBinaryStream("content");                
                fout = new FileOutputStream(tempFile);
                
                int len;
                byte buf[] = new byte[1024];
            
                while ((len = in.read(buf, 0, 1024)) != -1) {
                    fout.write(buf, 0, len);
               
                }
                fout.close();
             
                if (session != null) {
                    registerPhotoForDeletion(tempFile, session);
                }
               // System.out.println(System.getProperty("java.io.tmpdir") + ServletUtilities.tempFilePrefix +  filename);
                filename= tempFile.getName();                
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if (fout != null)
        		PubFunc.closeIoResource(fout);
        	if (in != null)
        		PubFunc.closeIoResource(in);
            if (rs != null)
                rs.close();
            
            if (conn != null)
                conn.close();
        }
        return filename;
    }
    /**
     * 创建临时文件
     * @param content
     * @return
     * @throws Exception
     */
    public static String createChartXmlFile(String content)throws Exception
    {
    	String filename="";
        File tempFile = null;
        ServletUtilities.createTempDir();
        FileOutputStream fout = null;
        try{
            tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, ".xml",
                    new File(System.getProperty("java.io.tmpdir")));
                 
            fout = new FileOutputStream(tempFile);
    
            fout.write(content.getBytes(), 0, content.length());
            fout.close();
            	
    
            	//System.out.println("fieldname " + userNumber);
            filename= tempFile.getName(); 
            	//System.out.println("filename="+filename);
        }catch(Exception e){
            
        }
        finally{
           PubFunc.closeResource(fout) ;
        }
    	return filename;
    }
    /**
     * 根据人员库前缀和人员编码生成其对应的文件
     * 
     * @param userTable
     *            应用库 usra01
     * @param userNumber
     *            0000001 ,a0100
     * @param flag
     *            'P'照片
     * @param session
     * @return
     * @throws Exception
     */
    public static String createPhotoFile(String userTable, String userNumber,
            String flag, HttpSession session) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;
        Connection conn = null;
        InputStream in = null;
        FileOutputStream fout = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select ext,Ole,fileid from ");
            strsql.append(userTable);
            strsql.append(" where A0100='");
            strsql.append(userNumber);
            strsql.append("' and Flag='");
            strsql.append(flag);
            strsql.append("'");
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
           
            rs=dao.search(strsql.toString());
            //System.out.println("SQL="+strsql.toString());

            
            if (rs.next()) {
            	if(StringUtils.isNotEmpty(rs.getString("fileid"))) {
            		return rs.getString("fileid");
            	}
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rs.getString("ext"),
                        new File(System.getProperty("java.io.tmpdir")));
                in = rs.getBinaryStream("Ole");                
                fout = new FileOutputStream(tempFile);
                
                int len;
                if(in!=null){
                	byte buf[] = new byte[1024];
                	
                	while ((len = in.read(buf, 0, 1024)) != -1) {
                		fout.write(buf, 0, len);
                		
                	}
                	//fout.close();
                	
                	if (session != null) {
                		registerPhotoForDeletion(tempFile, session);
                	}
                	//System.out.println("fieldname " + userNumber);
                	filename= tempFile.getName(); 
                	//System.out.println("filename="+filename);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(fout);
        	PubFunc.closeIoResource(in);
            if (rs != null)
                rs.close();
            
            if (conn != null)
                conn.close();
        }
        return StringUtils.isBlank(filename)?"":PubFunc.encrypt(filename);
    }
    public static String createTitlePhotoFile(int strTabid,int pageid, String gridno,String ext,HttpSession session) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;
        Connection conn = null;
        InputStream in = null;
        FileOutputStream fout = null;
        try {
        	StringBuffer sql=new StringBuffer();
        	sql.append("select * from rPage where (Tabid=");
    		sql.append(strTabid);
    		sql.append(" and gridno=");
    		sql.append(gridno);
    		sql.append(" and pageid="+pageid);
    		sql.append(")");
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
          
            rs=dao.search(sql.toString());
            //System.out.println("SQL="+strsql.toString());

            if (rs.next()) {
                tempFile = File.createTempFile("ykt_", ext, new File(System.getProperty("java.io.tmpdir")));
                in = rs.getBinaryStream("content");                
                fout = new FileOutputStream(tempFile);
                
                int len;
                if(in!=null){
                	byte buf[] = new byte[1024];
                	
                	while ((len = in.read(buf, 0, 1024)) != -1) {
                		fout.write(buf, 0, len);
                		
                	}
                	
                	if (session != null) {
                		registerPhotoForDeletion(tempFile, session);
                	}
                	//System.out.println("fieldname " + userNumber);
                	filename= tempFile.getName(); 
                	//System.out.println("filename="+filename);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(fout);
        	PubFunc.closeResource(in);
            if (rs != null)
                rs.close();
           
            if (conn != null)
                conn.close();
        }
        return filename;
    }
    /**
     * @param userTable
     * @param userNumber
     * @param i9999
     * @param session
     * @return
     * @throws Exception
     */
    public static String createOleFile(String userTable, String userNumber,
            String i9999, HttpSession session) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;
        Connection conn = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select title,ext,Ole from ");
            strsql.append(userTable);
            strsql.append(" where A0100='");
            strsql.append(userNumber);
            strsql.append("' and i9999=");
            strsql.append(i9999);
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);

            rs=dao.search(strsql.toString());
            if (rs.next()) {
                FileOutputStream fout = null;
                String prefix = rs.getString("title");
                String pre=prefix;
                String IllegalString = "/\\:*?\"<>|";
                for(int i=0;i<pre.length();i++){
            		if(IllegalString.indexOf(pre.substring(i,i+1))>=0){
            			prefix =  prefix.replace(pre.substring(i,i+1), "");
            		}
            	}
                prefix = PubFunc.hireKeyWord_filter_reback(prefix);
                if(prefix==null){
                	prefix="";
                }
                if(prefix.length()<4)
                	prefix = "media";
                String ext=rs.getString("ext");
                if(ext!=null&&ext.indexOf(".")!=-1)
                {
                	
                }else
                	ext="."+ext;
                tempFile = File.createTempFile(prefix+"-", ext,
                        new File(System.getProperty("java.io.tmpdir")));
                InputStream in = null;
                try{                	
                	in = rs.getBinaryStream("Ole");                
                	fout = new FileOutputStream(tempFile);
                	
                	int len;
                	byte buf[] = new byte[1024];
                	
                	while ((len = in.read(buf, 0, 1024)) != -1) {
                		fout.write(buf, 0, len);
                		
                	}
                	PubFunc.closeIoResource(fout);
                	
                	if (session != null) {
                		registerPhotoForDeletion(tempFile, session);
                	}
                	//System.out.println("fieldname " + userNumber);
                	filename= tempFile.getName();                
                }finally{
                	PubFunc.closeIoResource(fout);
                	PubFunc.closeIoResource(in);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();

            if (conn != null)
                conn.close();
        }
        return filename;
    }
    
    /**
     * @param userTable
     * @param userNumber
     * @param i9999
     * @param session
     * @return
     * @throws Exception
     */
    public static String createOrgOleFile(String tablename, String filetype,String filevalue,
            String i9999, HttpSession session) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;
        Connection conn = null;
        InputStream in = null;
        FileOutputStream fout = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select title,ext,Ole from ");
            strsql.append(tablename);
            strsql.append(" where ");
            strsql.append(filetype);
            strsql.append("='");
            strsql.append(filevalue);
            strsql.append("' and i9999=");
            strsql.append(i9999);
            conn = AdminDb.getConnection();
            ContentDAO dao  = new ContentDAO(conn);
            rs=dao.search(strsql.toString());
            if (rs.next()) {
                String prefix = rs.getString("title");
                if(prefix==null){
                	prefix="";
                }
                if(prefix.length()<4)
                	prefix = "media";
                String ext=rs.getString("ext");
                if(ext!=null&&ext.indexOf(".")!=-1)
                {
                	
                }else
                	ext="."+ext;
                tempFile = File.createTempFile(prefix+"-", ext,
                        new File(System.getProperty("java.io.tmpdir")));
                in = rs.getBinaryStream("Ole");                
                fout = new FileOutputStream(tempFile);
                
                int len;
                byte buf[] = new byte[1024];
            
                while ((len = in.read(buf, 0, 1024)) != -1) {
                    fout.write(buf, 0, len);
               
                }
             
                if (session != null) {
                    registerPhotoForDeletion(tempFile, session);
                }
                //System.out.println("fieldname " + userNumber);
                filename= tempFile.getName();                
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(fout);
            PubFunc.closeIoResource(in);
            PubFunc.closeIoResource(rs);
            PubFunc.closeIoResource(conn);
        }
        return filename;
    }
    /**
     * 
     * @param userTable
     * @param userNumber
     * @param i9999
     * @param session
     * @return
     * @throws Exception
     */
    public static String createKOleFile(String userTable, String userNumber,
            String i9999, HttpSession session) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;
        Connection conn = null;
        InputStream in = null;
        FileOutputStream fout = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select title,ext,fileid from ");
            strsql.append(userTable);
            if("h00".equalsIgnoreCase(userTable.toLowerCase()))
               strsql.append(" where h0100='");
            else
               strsql.append(" where e01a1='");
            strsql.append(userNumber);
            strsql.append("' and i9999='");
            strsql.append(i9999+"'");
            conn = AdminDb.getConnection();
            ContentDAO dao  = new ContentDAO(conn);
            rs=dao.search(strsql.toString());
            if (rs.next()) {
                String prefix = rs.getString("title");
                if(prefix.length()<3)
                	prefix = " "+prefix;
                tempFile = File.createTempFile(prefix+"-", rs.getString("ext"),
                        new File(System.getProperty("java.io.tmpdir")));
                
                String fileId = rs.getString("fileid");
                in = VfsService.getFile(fileId);                
                fout = new FileOutputStream(tempFile);
                
                int len;
                byte buf[] = new byte[1024];
            
                while ((len = in.read(buf, 0, 1024)) != -1) {
                    fout.write(buf, 0, len);
                }
             
                if (session != null) {
                    registerPhotoForDeletion(tempFile, session);
                }
                //System.out.println("fieldname " + userNumber);
                filename= tempFile.getName();                
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeIoResource(in);
        	PubFunc.closeIoResource(fout);
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(conn);
        }
        return filename;
    }

    
    /**
     * 创建照片文件
     * @param userTable
     * @param userNumber
     * @param conn
     * @return
     * @throws Exception
     */
    public static String createOleFile(String userTable, String userNumber,Connection conn) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;
        FileOutputStream fout = null;
        InputStream in  = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select ext,Ole from ");
            strsql.append(userTable);
            strsql.append(" where A0100='");
            strsql.append(userNumber);
            strsql.append("' and Flag='P'");
            ContentDAO dao  = new ContentDAO(conn);
            rs=dao.search(strsql.toString());
            if (rs.next()) {
                
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rs.getString("ext"),
                        new File(System.getProperty("java.io.tmpdir")));
                in = rs.getBinaryStream("Ole");                
                fout = new FileOutputStream(tempFile);
                
                int len;
                byte buf[] = new byte[1024];
            
                while ((len = in.read(buf, 0, 1024)) != -1) {
                    fout.write(buf, 0, len);
               
                }
                fout.close();
                filename= tempFile.getName();                
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(in);
            PubFunc.closeIoResource(fout);
            if (rs != null)
                rs.close();
            
        }
        return filename;
    }
    //手机微信显示附件，将绝对路径转换为相对路径。
    public static String createSubAttachFile(String filePath,String fileext,Connection conn) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        FileOutputStream fout = null;
        InputStream in  = null;
        try {
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, fileext,
                        new File(System.getProperty("java.io.tmpdir")));
                in =new FileInputStream(new File(filePath));       
                fout = new FileOutputStream(tempFile);
                
                int len;
                byte buf[] = new byte[1024];
            
                while ((len = in.read(buf, 0, 1024)) != -1) {
                    fout.write(buf, 0, len);
               
                }
                fout.close();
                filename= tempFile.getName();                
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(in);
            PubFunc.closeIoResource(fout);            
        }
        return filename;
    }
    /**
     * 创建临时文件
     * @param file
     * @return
     * @throws Exception
     */
    public static String createOleFile(FormFile file)throws Exception {
        File tempFile = null;
        String filename="";
        FileOutputStream fout = null;
        InputStream in = null;
        try
        {
	   	 	String fname=file.getFileName();
	   	 	int indexInt=fname.lastIndexOf(".");
	   	 	String ext=fname.substring(indexInt,fname.length());        	
            ServletUtilities.createTempDir();   
            tempFile = File.createTempFile(ServletUtilities.tempFilePrefix,ext,
                    new File(System.getProperty("java.io.tmpdir")));
            in=file.getInputStream();
                           
            fout = new FileOutputStream(tempFile);
            
            int len;
            byte buf[] = new byte[1024];
        
            while ((len = in.read(buf, 0, 1024)) != -1) {
                fout.write(buf, 0, len);
            }
            filename= tempFile.getName();     
    	}catch (Exception e) {
    		e.printStackTrace();
    	}finally{
    		PubFunc.closeResource(fout);
    		PubFunc.closeResource(in);
    	}
    	return filename;
    	
    }
    /**
     * 创建多媒体文件
     * @param field_name 字段名
     * @param ext  多媒体文件扩展名称
     * @param rs
     * @return
     * @throws Exception
     */
    public static String createOleFile(String field_name,String ext,RowSet rs) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        FileOutputStream fout = null;
        try {
            	String file_ext=rs.getString(ext);
                if(file_ext==null|| "".equals(file_ext))
                	return filename;
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rs.getString(ext),
                        new File(System.getProperty("java.io.tmpdir")));
                InputStream in = rs.getBinaryStream(field_name);
                try{                	
                	fout = new FileOutputStream(tempFile);
                	
                	int len;
                	byte buf[] = new byte[1024];
                	
                	while ((len = in.read(buf, 0, 1024)) != -1) {
                		fout.write(buf, 0, len);
                	}
                }finally{
                	PubFunc.closeIoResource(fout);
                }
                filename= tempFile.getName();                
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally
        {
            PubFunc.closeIoResource(fout);
        }
        return filename;
    }
    
    /**
     * 分析文件类型，组织mimeType
     * @param file
     * @param response
     * @throws IOException
     */
    public static void sendTempOleFile(File file, HttpServletResponse response)
            throws IOException {
          String mimeType = getMimeType(file.getName());;
          ServletUtilities.sendTempFile(file, response, mimeType);
    }
    public static void sendInlineOleFile(File file, HttpServletResponse response)
    throws IOException {
    		String mimeType = "application/msword;charset=UTF-8";
    		ServletUtilities.sendInlineFile(file, response,"", mimeType);
    }
//    /**
//     * 分析文件类型，组织mimeType
//     * @param file
//     * @param response
//     * @param isDel 是否删除临时文件 0否 1是
//     * @throws IOException
//     */
//    public static void sendTempOleFile(File file, HttpServletResponse response,int isDel)
//	    	throws IOException {
//	  String mimeType = getMimeType(file.getName());;
//	  ServletUtilities.sendTempFile(file, response, mimeType);
//	}
    
//    /**
//     * 
//     * @param file
//     * @param response
//     * @param isDel 是否删除临时文件 0否 1是
//     * @throws IOException
//     */
//    public static void sendInlineOleFile(File file, HttpServletResponse response,int isDel)
//    throws IOException {
//    		String mimeType = "application/msword;charset=UTF-8";
//    		ServletUtilities.sendInlineFile(file, response,"", mimeType);
//    }
//    
    
    public static void sendInlineExcelFile(File file, HttpServletResponse response)
    throws IOException {
    		String mimeType = "application/msexcel;charset=UTF-8";
//   		String mimeType = "application/vnd.ms-excel;charset=UTF-8";
    		ServletUtilities.sendInlineFile(file, response,"", mimeType);
    }
//    /**
//     * 
//     * @param file
//     * @param response
//     * @param isDel 是否删除临时文件 0否 1是
//     * @throws IOException
//     */
//    public static void sendInlineExcelFile(File file, HttpServletResponse response,int isDel)
//    throws IOException {
//    		String mimeType = "application/msexcel;charset=UTF-8";
////   		String mimeType = "application/vnd.ms-excel;charset=UTF-8";
//    		ServletUtilities.sendInlineFile(file, response,"", mimeType);
//    }
    /*
     * 分析输出的文件类型流
     */
    public static String getMimeType(String filename)
    {
    	//System.out.println(filename);
       	StringTokenizer Stok = new StringTokenizer(filename, ".");
    	String ext="";
    	String MimeType="text/html; charset=GB2312";
    	for(;Stok.hasMoreTokens();)
    		ext=Stok.nextToken();
    	if("doc".equalsIgnoreCase(ext))
    		MimeType="application/vnd.ms-word;charset=GB2312";
    	else if("bmp".equalsIgnoreCase(ext))
    		MimeType="image/bmp";
    	else if("xls".equalsIgnoreCase(ext))
    		MimeType="application/vnd.ms-excel;charset=UTF-8";
    	else if("xlsx".equalsIgnoreCase(ext))
    		MimeType="application/vnd.ms-excel;charset=UTF-8";
    	else if("stk".equalsIgnoreCase(ext))
    		MimeType="application/hyperstudio";
    	else if("hqx".equalsIgnoreCase(ext))
    		MimeType="application/mac-binhex40";
    	else if("cpt".equalsIgnoreCase(ext))
    		MimeType="application/mac-compactpro";
    	else if("cil".equalsIgnoreCase(ext))
    		MimeType="application/vnd.ms-artgalry";
    	else if("ppt".equalsIgnoreCase(ext))
    		MimeType="application/vnd.ms-powerpoint";
    	else if("pss".equalsIgnoreCase(ext))
    		MimeType="application/vnd.ms-powerpoint";
    	else if("pot".equalsIgnoreCase(ext))
    		MimeType="application/vnd.ms-powerpoint";
    	else if("asf".equalsIgnoreCase(ext))
    		MimeType="application/vnd.ms-asf";
      	else if("scm".equalsIgnoreCase(ext))
    		MimeType="application/vnd.lotus-screencam";
    	else if("sam".equalsIgnoreCase(ext))
    		MimeType="application/vnd.lotus-wordpro";
    	else if("vsd".equalsIgnoreCase(ext))
    		MimeType="application/vnd.visio";
    	else if("vss".equalsIgnoreCase(ext))
    		MimeType="application/vnd.visio";
    	else if("see".equalsIgnoreCase(ext))
    		MimeType="application/vnd.seemail";
    	else if("bin".equalsIgnoreCase(ext))
    		MimeType="application/octet-stream";
    	else if("exe".equalsIgnoreCase(ext))
    		MimeType="application/octet-stream";
    	else if("class".equalsIgnoreCase(ext))
    		MimeType="application/octet-stream";
    	else if("dms".equalsIgnoreCase(ext))
    		MimeType="application/octet-stream";
    	else if("lha".equalsIgnoreCase(ext))
    		MimeType="application/octet-stream";
    	else if("lzh".equalsIgnoreCase(ext))
    		MimeType="application/octet-stream";
    	else if("pdf".equalsIgnoreCase(ext))
    		MimeType="application/pdf";
    	else if("vcd".equalsIgnoreCase(ext))
    		MimeType="application/x-cdlink";
    	else if("zip".equalsIgnoreCase(ext))
    		MimeType="application/zip";
    	else if("rar".equalsIgnoreCase(ext))
    		MimeType="application/rar";
    	else if("jar".equalsIgnoreCase(ext))
    		MimeType="application/zip";
    	else if("pdf".equalsIgnoreCase(ext))
    		MimeType="application/pdf";
    	else if("dir".equalsIgnoreCase(ext))
    		MimeType="application/x-director";
    	else if("cgi".equalsIgnoreCase(ext))
    		MimeType="application/x-httpd-cgi";
    	else if("js".equalsIgnoreCase(ext))
    		MimeType="application/x-javascript";
    	else if("mocha".equalsIgnoreCase(ext))
    		MimeType="application/x-javascript";
    	else if("tar".equalsIgnoreCase(ext))
    		MimeType="application/x-tar";
    	else if("ms".equalsIgnoreCase(ext))
    		MimeType="application/x-troff-ms";
    	else if("src".equalsIgnoreCase(ext))
    		MimeType="application/x-wais-source";
    	else if("ram".equalsIgnoreCase(ext))
    		MimeType="audio/x-pn-realaudio";
    	else if("mid".equalsIgnoreCase(ext))
    		MimeType="audio/midi";
    	else if("pdb".equalsIgnoreCase(ext))
    		MimeType="chemical/x-pdb";
    	else if("gif".equalsIgnoreCase(ext))
    		MimeType="image/gif";
    	else if("jpeg".equalsIgnoreCase(ext))
    		MimeType="image/jpeg";
    	else if("jpg".equalsIgnoreCase(ext))
    		MimeType="image/jpeg";
    	else if("jpe".equalsIgnoreCase(ext))
    		MimeType="image/jpeg";
    	else if("jfif".equalsIgnoreCase(ext))
    		MimeType="image/jpeg";
    	else if("pjpeg".equalsIgnoreCase(ext))
    		MimeType="image/jpeg";
    	else if("png".equalsIgnoreCase(ext))
    		MimeType="image/png";
    	else if("tiff".equalsIgnoreCase(ext))
    		MimeType="image/tiff";
    	else if("tiff".equalsIgnoreCase(ext))
    		MimeType="image/tiff";
    	else if("ras".equalsIgnoreCase(ext))
    		MimeType="image/x-cmu-raster";
    	else if("bmp".equalsIgnoreCase(ext))
    		MimeType="image/x-MS-bmp";
    	else if("htm".equalsIgnoreCase(ext))
    		MimeType="text/html";
    	else if("txt".equalsIgnoreCase(ext))
    		MimeType="text/plain";
    	else if("html".equalsIgnoreCase(ext))
    		MimeType="text/html";
    	else if("rtx".equalsIgnoreCase(ext))
    		MimeType="text/richtext";
    	else if("etx".equalsIgnoreCase(ext))
    		MimeType="text/x-setext";
    	else if("sgml".equalsIgnoreCase(ext))
    		MimeType="text/x-sgml";
    	else if("sgm".equalsIgnoreCase(ext))
    		MimeType="text/x-sgml";
    	else if("uri".equalsIgnoreCase(ext))
    		MimeType="text/uri-list";
    	else if("wav".equalsIgnoreCase(ext))
    		MimeType="audio/x-wav";
    	else if("sp".equalsIgnoreCase(ext))
    		MimeType="text/vnd.in3d.spot";
    	else if("abc".equalsIgnoreCase(ext))
    		MimeType="text/vnd.abc";
    	else if("3dm".equalsIgnoreCase(ext))
    		MimeType="text/vnd.in3d.3dml";
    	else if("sh".equalsIgnoreCase(ext))
    		MimeType="application/x-sh";
    	else if("mp3".equalsIgnoreCase(ext))
    		MimeType="audio/x-mpeg";  
    	else if("m3u".equalsIgnoreCase(ext))
    		MimeType="audio/x-mpegurl";    
    	else if("mp2".equalsIgnoreCase(ext))
    		MimeType="audio/x-mpeg";  
    	else if("mpa".equalsIgnoreCase(ext))
    		MimeType="audio/x-mpeg"; 
    	else if("abs".equalsIgnoreCase(ext))
    		MimeType="audio/x-mpeg"; 
    	else if("mpega".equalsIgnoreCase(ext))
    		MimeType="audio/x-mpeg";    	
    	else
    		MimeType="multipart/form-data";
        return MimeType;
    }
    /**
     * 分析文件类型，组织mimeType
     * @param file
     * @param response
     * @param response 客户端要显示的文件名
     * @throws IOException
     */
    public static void sendTempFileEx(File file, HttpServletResponse response,String displayfilename,String code)
            throws IOException {
        String mimeType = null;
        String filename = file.getName();
        boolean battach=true;
        if (filename.length() > 5) {
            if (".jpeg"
                    .equals(filename.substring(filename.length() - 5, filename.length()))) {
                mimeType = "image/jpeg";
            } else if (".jpg".equals(filename.substring(filename.length() - 4,
                    filename.length()))) {
                mimeType = "image/jpg";
            } else if (".bmp".equals(filename.substring(filename.length() - 4,
                    filename.length()))) {
                mimeType = "image/bmp";
            }               
            else if (".png".equals(filename.substring(filename.length() - 4,
                    filename.length()))) {
                mimeType = "image/png";
            }
            else if (".xls".equals(filename.substring(filename.length() - 4,
                    filename.length()))) {
                mimeType = "application/vnd.ms-excel";
                battach=false;
            }
            else if(".xlsx".equals(filename.substring(filename.length() - 5,
                    filename.length()))){
            	    //此格式没有对应的mimetype，不设置。否则文件名会变成 mydocument.xlsx.xls这种格式 guodd 2016-11-28
            		//mimeType = "application/vnd.ms-excel";
                battach=false;
            }
            else if (".doc".equals(filename.substring(filename.length() - 4,
                    filename.length()))) {
                mimeType = "application/vnd.ms-word";
                battach=false;
            }
            else if(".txt".equals(filename.substring(filename.length() - 4,
                    filename.length()))){
                 mimeType = "text/plain";
                 battach=false;
            }
            else {
                mimeType=getMimeType(filename);
            }
        }
        ServletUtilities.sendTempFile(file, response, mimeType,displayfilename,code/*,battach*/);
    }
    
    /**
     * 分析文件类型，组织mimeType
     * @param file
     * @param response
     * @throws IOException
     */
    public static void sendTempFile(File file, HttpServletResponse response)
            throws IOException {
        sendTempFileEx(file,response,"","ISO8859_1");
    }
    /**
     * 重载上面2个参数方法，为了添加访问平台入口参数 code
     * @param file 
     * @param response
     * @param mimeType   无意义
     * @param displayfilename 无意义
     * @param notString    无意义
     * @param code     编码格式
     * @throws IOException
     */
    public static void sendTempFile(File file, HttpServletResponse response,
            String mimeType,String displayfilename,String notString,String code)
            throws IOException {
        sendTempFileEx(file,response,"",code);
    }
//    /**
//     * 
//     * @param file
//     * @param response
//     * @param isDel 是否删除临时文件 0否 1是
//     * @throws IOException
//     */
//    public static void sendTempFile(File file, HttpServletResponse response,int isDel,String displayfilename)
//	    throws IOException {
//	sendTempFileEx(file,response,displayfilename);
//	}

    /**
     * 把对应内容写到浏览器中
     * @param file
     * @param response
     * @param mimeType
     * @throws IOException
     */
/*    public static void sendTempFile(File file, HttpServletResponse response,
            String mimeType,boolean battach) throws IOException {
    	BufferedInputStream bis=null;
    	try{
	        if (file.exists()) {
	           bis= new BufferedInputStream(
	                    new FileInputStream(file));
	
	            //  Set HTTP headers
	            if (mimeType != null) {
	                response.setHeader("Content-Type", mimeType);
	            }
	            //response.setHeader("Content-Length", String.valueOf(file.length()));
	            //SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	            //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	            //response.setHeader("Last-Modified", sdf.format(new Date(file.lastModified())));
	            
	             * yuxiaochun zengjia
	             * 显示下在文件名称。
	             
	            if(!battach)
	            	response.addHeader("Content-Disposition","attachment;filename=" + file.getName());
	            
	            ServletOutputStream servletoutputstream = response.getOutputStream();
	            int len;
	            byte buf[] = new byte[1024];
				while ((len = bis.read(buf)) != -1) {
					servletoutputstream.write(buf,0,len);
				}	  
				          
	            
	            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
	            byte[] input = new byte[1024];
	            int len;
				while ((len = bis.read(input)) != -1) {
					bos.write(input,0,len);
				}  
	            bos.flush();
	            bos.close();
	            bis.close();			
	        } else {
	            throw new FileNotFoundException(file.getAbsolutePath());
	        }
    	}catch (Exception e) {
			e.printStackTrace();
		} finally
        {
			if(bis!=null)
            PubFunc.closeIoResource(bis);
        }
        return;
    }*/
    
    /**
     * 把对应内容写到浏览器中
     * @param file
     * @param response
     * @param mimeType
     * @throws IOException
     */
    public static void sendTempFile(File file, HttpServletResponse response,
            String mimeType) throws IOException {
        sendTempFile(file,response,mimeType,"","ISO8859_1");
    }
    
    /**
     * 把对应内容写到浏览器中
     * @param file
     * @param response
     * @param mimeType
     * @param displayfilename 要显示的文件的名
     * @throws IOException
     */
    public static void sendTempFile(File file, HttpServletResponse response,
            String mimeType,String displayfilename,String code) throws IOException {
        ServletOutputStream servletoutputstream = null;    
        BufferedInputStream bis = null;
        FileInputStream fis = null;
        try
        {
            if (file.exists()) 
            {	
            	fis = new FileInputStream(file); 
                bis = new BufferedInputStream(fis);
    
                //  Set HTTP headers
                if (mimeType != null) {
                    response.setContentType(mimeType);
                    
                    //response.setHeader("Content-Type", mimeType);
                }
                //response.setHeader("Content-Length", String.valueOf(file.length()));
                //SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                //response.setHeader("Last-Modified", sdf.format(new Date(file.lastModified())));
                /*
                 * yuxiaochun zengjia
                 * 显示下在文件名称。
                 */       
                String name="";
                if (displayfilename!=null &&!"".equals(displayfilename))
                    name = displayfilename;
                else
                    name= file.getName();  
                if("UTF-8".equalsIgnoreCase(code)) {
                	name= URLEncoder.encode(name, code);
                }else if("Firefox".equalsIgnoreCase(code)) {
                	name = new String(name.getBytes("UTF-8"), "ISO8859-1");
                }else {
                    name=new String(name.getBytes("gb2312"),code);  
                }
                response.setHeader("Content-Disposition","attachment;filename=" + name);
                servletoutputstream=response.getOutputStream();
                
                int len;
                byte buf[] = new byte[1024];
                while ((len = bis.read(buf)) != -1) {
                    servletoutputstream.write(buf,0,len);
                }
                
                /*
                BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
                byte[] input = new byte[1024];
                int len;
                while ((len = bis.read(input)) != -1) {
                    bos.write(input,0,len);
                }  
                */
                /*
                boolean eof = false;
                while (!eof) {
                    int length = bis.read(input);
                    if (length == -1) {
                        eof = true;
                    } else {
                        bos.write(input, 0, length);
                    }
                }*/
                //bos.flush();
                //bis.close();              
                //bos.close();
            } else {
                throw new FileNotFoundException(file.getAbsolutePath());
            }
        }
        catch (IOException e){
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try 
            {
                if (servletoutputstream != null)
                    servletoutputstream.flush();
                if (servletoutputstream != null)
                    servletoutputstream.close();
                
            }
            catch (IOException e){
                // alert the user that some other I/O
                // error occurred
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }           
            PubFunc.closeResource(bis);
            PubFunc.closeResource(fis);

//            if(file.isFile()&&isDel==1){//2017-4-27 zhanghua 由于发现可能存在导出文件异常问题。不在支持此方法。
//            	if(file.getPath().indexOf(System.getProperty("java.io.tmpdir"))>=0)
//            	file.delete();
//            }
        }
        return;
    }
    /**
     * 输出大小固定的图片（用于圆形图片显示）
     * @param file
     * @param response
     * @param imageResize
     * @throws IOException
     */
	public static void resizeImage(File file,OutputStream out, String imageResize) throws IOException {
		try{
			String filename = file.getName();
			String fileExtName = filename.substring((filename.indexOf(".") + 1), filename.length());
			if (file.exists()) 
	        {
				int index = imageResize.indexOf('`');
				int width = Integer.parseInt(imageResize.substring(0, index));
				int height = Integer.parseInt(imageResize.substring(index+1,imageResize.length()));
				BufferedImage prevImage = ImageIO.read(file);
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);  
			    Graphics graphics = image.createGraphics();  
			    graphics.drawImage(prevImage, 0, 0, width, height, null); 
			    ImageIO.write(image, fileExtName, out);
		    }else{
		    	throw new FileNotFoundException(file.getAbsolutePath());
		    }
		}catch(Exception ex)
        {
            ex.printStackTrace();
        }finally
        {
            try 
            {
                if (out != null)
                	out.flush();
                if (out != null)
                	out.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }           
        }
	} 
	 
    public static void sendInlineFile(File file, HttpServletResponse response,String displayfilename,
            String mimeType) throws IOException {
		ServletOutputStream servletoutputstream = null;    
		BufferedInputStream bis =null;
    	try
    	{
	        if (file.exists()) 
	        {
	             bis = new BufferedInputStream(new FileInputStream(file));
	
	            if (mimeType != null) {
	            	response.setContentType(mimeType);
	            }
    
	            if(StringUtils.isBlank(displayfilename))
	            	displayfilename = file.getName();
	            String name=new String(displayfilename.getBytes("gb2312"),"ISO8859_1");
	           	response.setHeader("Content-Disposition","inline;filename=" + name);
	    		servletoutputstream=response.getOutputStream();
	    		
	            int len;
	            byte buf[] = new byte[1024];
				while ((len = bis.read(buf)) != -1) {
					servletoutputstream.write(buf,0,len);
				}
				
	        } else {
	            throw new FileNotFoundException(file.getAbsolutePath());
	        }
	        servletoutputstream.flush();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
			try 
			{
				PubFunc.closeResource(bis);
				if (servletoutputstream != null)
					servletoutputstream.close();	
//				if(file.isFile()&&isDel==1)//2017-4-27 zhanghua 由于发现可能存在导出文件异常问题。不在支持此方法。
//	            	if(file.getPath().indexOf(System.getProperty("java.io.tmpdir"))>=0)
//	            	file.delete();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}    		
    	}
        return;
    }
    /**
     * 动态生成校验图片流
     * @param codelen 验证码长度
     * @param strSrc 验证码取值范围
     * @param session 
     * @param channel 区分内外网 =0外网；=1内网；默认为1 内外
     * @return BufferedImage 验证码图片流
     * @throws Exception
     */
    public static BufferedImage validateCodeImage (int codelen,StringBuffer strSrc,HttpSession session, int channel, String bosflag) throws Exception {
        Random random;    
        int strSrcLen=strSrc.length();
        BufferedImage image = new BufferedImage(100, 25, BufferedImage.TYPE_INT_RGB);
        if(1 == channel) {
            if("hcm".equalsIgnoreCase(bosflag.toLowerCase())) {
                if(codelen < 6)
                    image = new BufferedImage(14 * 6, 25, BufferedImage.TYPE_INT_RGB);
                else
                    image = new BufferedImage(14 * codelen, 25, BufferedImage.TYPE_INT_RGB);
                
            } else {
                if(codelen < 6)
                    image = new BufferedImage(13 * 6, 16, BufferedImage.TYPE_INT_RGB);
                else
                    image = new BufferedImage(13 * codelen, 16, BufferedImage.TYPE_INT_RGB);
            }
        }
        
        try {
            random=new Random(System.currentTimeMillis());          
            StringBuffer strpwd=new StringBuffer();
            int index=0;
            for(int i=0;i<codelen;i++) {
                index=random.nextInt(strSrcLen);
                strpwd.append(strSrc.charAt(index));
            }
            
            session.setAttribute("validatecode",strpwd.toString());

            logger.info("验证码值validatecode：{}",strpwd.toString());

            Font mFont = new Font("SimSun", Font.TRUETYPE_FONT, 26);  
            if(1 == channel && "hr".equalsIgnoreCase(bosflag.toLowerCase()))
                mFont = new Font("SimSun", Font.TRUETYPE_FONT, 18);
            
            Graphics2D gra = (Graphics2D)image.getGraphics();
            //@arithmetic1=设置背景色
            gra.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            gra.setColor(Color.white);
            if(1 == channel && "hr".equalsIgnoreCase(bosflag.toLowerCase())) {
                if(codelen < 6)
                    gra.fillRect(0, 0, 13 * 6, 16);
                else
                    gra.fillRect(0, 0, 13 * codelen, 16);
            } else
                gra.fillRect(0, 0, 120, 100);
            
            int sum = 7;
            if(0 == channel)
                sum = 10;
            /**干扰*/             
            for(int i=0; i<sum; i++) {
                int xs = random.nextInt(80);
                int ys = random.nextInt(24);
                int xe = xs+random.nextInt(20);
                int ye = ys+random.nextInt(3);
                int red = random.nextInt(255);
                int green = random.nextInt(255);
                int blue = random.nextInt(255);
                gra.setColor(new Color(red, green, blue));
                gra.drawLine(xs,ys,xe,ye);
            }
            //噪点的数量
            int count = 120;
            if(0 == channel)
                count = 400;
            else if(1 == channel && "hcm".equalsIgnoreCase(bosflag.toLowerCase()))
                count = 300;
            
            for(int i=0; i<count; i++) {
                int x = random.nextInt(100);
                int y = random.nextInt(25);
                
                if(1 == channel && "hr".equalsIgnoreCase(bosflag.toLowerCase())){
                    if(codelen < 6)
                        x = random.nextInt(13 * 6);
                    else
                        x = random.nextInt(13 * codelen);
                    
                    y = random.nextInt(16);
                }
                
                gra.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
                gra.drawLine(x,y,x,y);
            }    
            
            gra.setFont(mFont);         
            //@arithmetic1=输出数字
            char c;
            for (int i = 0; i < codelen; i++) {
                int red = random.nextInt(255);
                int green = random.nextInt(200);
                int blue = random.nextInt(200);
                if(1 == channel) {
                    red = random.nextInt(150);
                    green = random.nextInt(120);
                    blue = random.nextInt(150);
                }
                //@arithmetic1=设置字体色
                gra.setColor(new Color(red, green, blue));
                c = strpwd.charAt(i);
                if(1 == channel) {
                    if("hr".equalsIgnoreCase(bosflag.toLowerCase())) {
                        if(codelen < 6) {
                            int a = 10 + (6 - codelen) * 5;
                            gra.drawString(String.valueOf(c) , i * a + 5, 14);
                        } else
                            gra.drawString(String.valueOf(c) , i * 11 + 5, 14);
                    } else {
                        if(codelen < 6) {
                            int a = 10 + (6 - codelen) * 5;
                            gra.drawString(String.valueOf(c) , i * a + 5, 20);
                        } else
                            gra.drawString(String.valueOf(c) , i * 13 + 4, 20);
                    }
                } else
                    gra.drawString(String.valueOf(c) , i * 21 + 5, 20);
            }
            
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        return image;        
    }
    /**
     * 生成验证码图片
     * @param codelen 验证码长度
     * @param strSrc 验证码取值范围
     * @param session 
     * @param channel 区分内外网 =0外网；=1内网；默认为1 内外
     * @return string 验证码图片名称
     * @throws Exception
     */
    public static String ValidateCodeImageFile(int codelen,StringBuffer strSrc,HttpSession session, int channel)throws Exception{
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();       
        FileOutputStream fout = null;
        try {
            
            tempFile = File.createTempFile(ServletUtilities.tempFilePrefix,".jpg", new File(System.getProperty("java.io.tmpdir")));   
            fout = new FileOutputStream(tempFile);
            BufferedImage image = validateCodeImage(codelen, strSrc, session, channel, "hr");
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fout);
            encoder.encode(image);

            if (session != null)
                registerPhotoForDeletion(tempFile, session);
            
            filename= tempFile.getName();               
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(fout!=null)
             fout.close();
        }
        return filename;        
    }

    /**
     * 获取文件id
     * @param userTable
     * @param userNumber
     * @param i9999
     * @return
     * @throws Exception
     */
    public static String getFileId(String userTable, String userNumber, String i9999){
        String fileid="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;
        Connection conn = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select fileid,ole from ");
            strsql.append(userTable);
            strsql.append(" where A0100='");
            strsql.append(userNumber);
            strsql.append("' and i9999=");
            strsql.append(i9999);
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(strsql.toString());
            if(rs.next()){
                fileid = rs.getString("fileid");
            }
        }catch (Exception e){
           e.printStackTrace();
        }finally {
            PubFunc.closeIoResource(rs);
            PubFunc.closeIoResource(conn);
        }
        return  fileid;
    }
}