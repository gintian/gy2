package com.hjsj.hrms.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 功能说明：文件类型工具类
 * 补充说明：
 * <p>Title: FileUtil </p>
 * <p>Company: hjsj</p>
 * <p>create time  Sep 3, 2014 1:39:57 PM</p>
 * @author xiaoyun
 * @version 
 */
public class FileTypeUtil {
	/** 文件类型缓存（key-文件类型 value-消息头） */
	private static final Map FILE_TYPE_MAP = new HashMap();
	
	/**
	 * 初始化文件类型缓存
	 */
	 static {  
        FILE_TYPE_MAP.put("jpg", "FFD8FF");   //JPEG (jpg)   
        FILE_TYPE_MAP.put("png", "89504E47"); //PNG (png)   
        FILE_TYPE_MAP.put("gif", "47494638"); //GIF (gif)   
        FILE_TYPE_MAP.put("tif", "49492A00"); //TIFF (tif)   
        FILE_TYPE_MAP.put("bmp", "424D");     //Windows Bitmap (bmp)   
        FILE_TYPE_MAP.put("dwg", "41433130"); //CAD (dwg)   
        FILE_TYPE_MAP.put("html", "68746D6C3E"); //HTML (html)   
        FILE_TYPE_MAP.put("rtf", "7B5C727466");  //Rich Text Format (rtf)   
        FILE_TYPE_MAP.put("xml", "3C3F786D6C");
        
        FILE_TYPE_MAP.put("rar", "52617221");  
        FILE_TYPE_MAP.put("psd", "38425053"); //Photoshop (psd)   
        FILE_TYPE_MAP.put("eml", "44656C69766572792D646174653A"); //Email [thorough only] (eml)   
        FILE_TYPE_MAP.put("dbx", "CFAD12FEC5FD746F"); //Outlook Express (dbx)   
        FILE_TYPE_MAP.put("pst", "2142444E"); //Outlook (pst)   
        FILE_TYPE_MAP.put("mdb", "5374616E64617264204A"); //MS Access (mdb)   
        FILE_TYPE_MAP.put("wpd", "FF575043"); //WordPerfect (wpd)   
        FILE_TYPE_MAP.put("eps", "252150532D41646F6265");  
        FILE_TYPE_MAP.put("ps", "252150532D41646F6265");  
        FILE_TYPE_MAP.put("pdf", "255044462D312E"); //Adobe Acrobat (pdf)   
        FILE_TYPE_MAP.put("qdf", "AC9EBD8F"); //Quicken (qdf)   
        FILE_TYPE_MAP.put("pwl", "E3828596"); //Windows Password (pwl)   
        FILE_TYPE_MAP.put("wav", "57415645"); //Wave (wav)   
        FILE_TYPE_MAP.put("avi", "41564920");  
        FILE_TYPE_MAP.put("ram", "2E7261FD"); //Real Audio (ram)   
        FILE_TYPE_MAP.put("rm", "2E524D46"); //Real Media (rm)   
        FILE_TYPE_MAP.put("mpg", "000001BA"); //   
        FILE_TYPE_MAP.put("mov", "6D6F6F76"); //Quicktime (mov)   
        FILE_TYPE_MAP.put("asf", "3026B2758E66CF11"); //Windows Media (asf)   
        FILE_TYPE_MAP.put("mid", "4D546864"); //MIDI (mid)   
        
        
        FILE_TYPE_MAP.put("exe", "4D5A90"); // exe
        FILE_TYPE_MAP.put("zip", "504B03041400000008");  // zip
        
        /** office03文件头都相同，目前判断了doc，xls，ppt */
        FILE_TYPE_MAP.put("office03", "D0CF11E0"); //

        /** 注意：docx与xlsx类型的文件头也相同 docx，xlsx，pptx */
        FILE_TYPE_MAP.put("office07", "504B030414000600080000002100"); // office07或13  
    } 
	 
	 
 	/**
	 * 判断上传的文件是否合法(文件实际类型与文件名的后缀是否一致)
	 * @param file 上传的文件(org.apache.struts.upload.FormFile格式)
	 * @return
	 * @throws IOException
	 */
	public static boolean isFileTypeEqual(FormFile file) throws IOException {
		
		if(file==null||file.getFileName()==null||file.getFileName().length()==0) 
			return true;
		// 文件后缀问空，错误
		if(file.getFileName().indexOf(".") == -1) {
			return false;
		} 
		String fileType = file.getFileName().substring(file.getFileName().lastIndexOf(".")+1);
		InputStream stream=null;
		boolean flag =false;
		try {
			stream=file.getInputStream();
			flag = isFileTypeEqual(stream, fileType);
		} catch (Exception e) {
			//e.printStackTrace();
		} finally
        {
            PubFunc.closeIoResource(stream);
        }
		return	flag;	
	}
	
	/**
	 * 判断上传的文件是否合法(文件实际类型与文件名的后缀是否一致)
	 * @param file 上传的文件(java.io.File格式)
	 * @return
	 * @throws IOException
	 */
	public static boolean isFileTypeEqual(File file) throws IOException {
		String fileType = "";
		InputStream inputStream = null;
		boolean flag = false;
		try {
			inputStream = new FileInputStream(file);
			if (StringUtils.isNotEmpty(file.getName())) {
				fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
			}
			flag = isFileTypeEqual(inputStream, fileType);
		} catch (Exception e) {
			//e.getStackTrace();
		} finally {
			PubFunc.closeIoResource(inputStream);
		}
		return flag;	
	}	
	
	
	public static boolean isFileTypeEqual(InputStream inputStream, String ext) throws IOException {
		String realType = getFileTypeByHead(inputStream);
		//特殊处理上传照片时，获取的文件头为空的情况   chenxg 2017-02-22
		if(StringUtils.isEmpty(realType) && StringUtils.isNotEmpty(ext) && "bmp,jpg,jpeg".indexOf(ext.toLowerCase()) > -1)
		    return false;
		
		// 如果缓存中没有该类型，可以上传
		if(StringUtils.isEmpty(realType) || "asf".equalsIgnoreCase(realType.toLowerCase())) {
			return true;
		}		
		
		if(StringUtils.isEmpty(ext)) {
			return false;
		}else {
			ext = ext.toLowerCase();
		}
		
		
		// office中的doc和xls文件头相同，需特殊处理; wps格式的文件头是office，需兼容处理
		if("office03".equals(realType)) {
			if(
				"doc".equalsIgnoreCase(ext) ||
				"xls".equalsIgnoreCase(ext) ||
				"ppt".equalsIgnoreCase(ext) ||
				"wps".equalsIgnoreCase(ext) ||
				"wpt".equalsIgnoreCase(ext) ||
				"dps".equalsIgnoreCase(ext) ||
				"dpt".equalsIgnoreCase(ext) ||
				"et".equalsIgnoreCase(ext)  ||
				"ett".equalsIgnoreCase(ext)
			) {
				return true;
			}
		}
		// office中的docx和xlsx文件头也相同，需特殊处理 office2003以上excel文件后缀名会取到zip; wps格式的文件头是office，需兼容处理
		if("office07".equals(realType)|| "zip".equalsIgnoreCase(realType)) {
			if(
				"docx".equalsIgnoreCase(ext) ||
				"xlsx".equalsIgnoreCase(ext) ||
				"pptx".equalsIgnoreCase(ext) ||
				"wps".equalsIgnoreCase(ext) ||
				"wpt".equalsIgnoreCase(ext) ||
				"dps".equalsIgnoreCase(ext) ||
				"dpt".equalsIgnoreCase(ext) ||
				"et".equalsIgnoreCase(ext)  ||
				"ett".equalsIgnoreCase(ext)
			) {
				return true;
			}
		}
		//测试发现如rtx等某些工具截图生成的图片，可能存在文件头类型和后缀名不一致的情况。因此一下4种图片文件类型可混用，不在判断  zhanghua 2018-4-3
		if("jpg,png,bmp,jpeg".indexOf(realType.toLowerCase())>-1&&"jpg,png,bmp,jpeg".indexOf(ext.toLowerCase())>-1)
			return true;
		if("rm".equalsIgnoreCase(realType) && ext.toLowerCase().startsWith("rm")) 
			return true;
		
		if("xml".equals(realType)) {
			//接收报盘功能  存在导入文件类型为rpx格式，内容类型为xml格式文件 表式收发导入功能  存在导入文件类型为txt格式，内容类型为xml格式文件
			if("txt".equalsIgnoreCase(ext)|| "rpx".equalsIgnoreCase(ext)) {
				return true;
			}
		}
		
		return realType.equalsIgnoreCase(ext);
	}
	
	
	
	/**
	 * 根据文件流获取文件类型
	 * @param inputStream
	 * @return
	 */
	private static String getFileTypeByHead(InputStream inputStream){  
        String fileType = null;  
        byte[] b = new byte[24];  
        try{  
        	inputStream.read(b);  
            fileType = getFileTypeByStream(b);  
            inputStream.close();  
       }catch(Exception e){  
            e.printStackTrace(); // 错误信息   
       }  
       if(fileType != null) {
    	   fileType = fileType.toLowerCase();
       }
       return fileType;  
	}    

	/**
	 * 根据消息头来判断文件类型
	 * @param buf
	 * @return
	 */  
	private static String getFileTypeByStream(byte[] buf) {  
       String filetypeHex = String.valueOf(getFileHexString(buf));  
       Iterator it = FILE_TYPE_MAP.entrySet().iterator();  
       while (it.hasNext()){  
	    	Entry  entry  = (Entry) it.next();  
	        String fileTypeHexValue = (String) entry.getValue();   
	        if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {  
	            return (String) entry.getKey();  
	        }  
        }  
        return null;  
	}  
	
	/**
	 * 获取字节数组的十六进制
	 * @param buf
	 * @return
	 */
    private  static String getFileHexString(byte[] buf) {       
        StringBuffer sb = new StringBuffer();       
        if(buf == null || buf.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < buf.length; i++) {  
            int v = buf[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
            	sb.append(0);  
            }  
            sb.append(hv);  
        }  
        return sb.toString();  
    } 
}
