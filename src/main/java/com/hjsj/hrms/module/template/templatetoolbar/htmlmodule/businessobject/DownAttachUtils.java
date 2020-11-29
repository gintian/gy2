package com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao.ExcelLayoutDao;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao.impl.ExcelLayoutDaoImpl;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.vo.UploadContant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 项目名称 ：ehr
 * 类名称：AttachmentBo
 * 类描述：上传附件
 * 创建人： lis
 * 创建时间：2019.12.12
 */
public class DownAttachUtils {
	private UserView userView = null;
	private Connection conn = null;
	private String tabid = null;
    private String RootDir;//文件根目录
    private String para_maxsize; //文件大小 原始参数
    private float maxFileSize; //参数配置多媒体文件大小
    private String absoluteDir;//相对路径
    private String DestFileName="";//文件名
    private String realFileName = "";//文件真实名称
    private String ext="";//文件后缀
    /**获取dao*/
	private ExcelLayoutDao excelLayoutDao =null;
	
	public DownAttachUtils(UserView userView,Connection conn,String tabid){
		this.userView = userView;
		this.conn = conn;
		this.tabid = tabid;
		this.excelLayoutDao=new ExcelLayoutDaoImpl(conn);
		initTable();
	}
	/**
	 * 功能：初始化表单结构
	 */
	private void initTable() {
		try {
			//保存fileid到temlate_table表中
			DbWizard dwDbWizard=new DbWizard(this.conn);
			//新增"html_module_json"
			if (!dwDbWizard.isExistField("template_table", "html_module_json",false))
			{
				Table table = new Table("template_table");
				Field obj = new Field("html_module_json");
				obj.setDatatype(DataType.STRING);
				obj.setLength(200);
				obj.setNullable(true);
				obj.setKeyable(false);
				table.addField(obj);
				dwDbWizard.addColumns(table);
			}
			//新增"html_module_excel"
			if (!dwDbWizard.isExistField("template_table", "html_module_excel",false))
			{
				Table table = new Table("template_table");
				Field obj = new Field("html_module_excel");
				obj.setDatatype(DataType.STRING);
				obj.setLength(200);
				obj.setNullable(true);
				obj.setKeyable(false);
				table.addField(obj);
				dwDbWizard.addColumns(table);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @author lis
	 * @Description: 生成文件绝对路径
	 * @date 2016-5-25
	 * @param fileName
	 * @param dirType 目录类别
	 * @return
	 * @throws GeneralException
	 */
    public String getAbsoluteDir(String fileName,String dirType) throws GeneralException
    {
        StringBuffer relative = new StringBuffer();
        String dir = null;
        try{
            relative.append(dirType);
            relative.append(tabid);
            relative.append("\\");
            relative.append(UploadContant.excelTempDir);
            //创建目录
            dir = relative.toString();
            dir =dir.replace("\\", File.separator);
            relative.insert(0, this.RootDir);
            File tempDir = new File(relative.toString().replace("\\", File.separator));
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
        }
        catch(Exception e){
           e.printStackTrace();  
           throw GeneralExceptionHandler.Handle(e);
        }
        return dir;
         
    }
    
    /**
     * @author lis
     * @Description: 保存文件到文件夹
     * @date 2016-5-25
     * @param file
     * @param dirType 保存新路径类别
     * @return 保存后的新文件信息
     * @throws GeneralException
     */
    public HashMap SaveFileToDisk(File file) throws GeneralException
    {
        String fileExt="";
        String srcFilename="";      
        HashMap valueMap = new HashMap();
        if(file==null){
        	return valueMap;
        }
        String dirType = "";
		if("\\".equals(File.separator)){//证明是windows
			dirType = "subdomain\\template_";
		}else if("/".equals(File.separator)){//证明是linux
			dirType = "subdomain/template_";
		}
        if (file.exists()){
            try{
                initParam(true);
                srcFilename = file.getName();
                long size = getFileSizes(file);
                if (size<=0){
                	String realName = srcFilename;
                	if(!"".equals(this.realFileName))
                		realName = this.realFileName;
                    throw new GeneralException("上传文件大小为0:"+realName);   
                }
                if ((this.maxFileSize > 0) && (this.maxFileSize < size)) {
                	throw new GeneralException("上传文件太大, 请不要超过" + para_maxsize);
                }
                if (srcFilename.lastIndexOf(".") > 0)
                    fileExt = srcFilename.substring(srcFilename.lastIndexOf("."));// 扩展名
                /**定义导出文件名都是固定*/
                this.DestFileName = UploadContant.htmlMoudle_fileName;
                // 获取相对目录
                this.absoluteDir = getAbsoluteDir(DestFileName,dirType);
                // 保存
                String filePath =  this.RootDir+this.absoluteDir + File.separator + this.DestFileName;  //完整路径   
                this.copyFile(file.getPath(), filePath); 
              
                valueMap.put("path",  filePath.replace("\\", File.separator).replace("/", File.separator));
                valueMap.put("fileExt", fileExt);
                valueMap.put("absoluteDir",  this.absoluteDir);
          } catch(Exception e){        	   
        	   e.printStackTrace();
        	   throw GeneralExceptionHandler.Handle(e);
           } 
        }        
        return valueMap;
    }
    
    /**
     * @author lis
     * @param vfsFileEntity 
     * @Description: 保存文件到文件夹
     * @date 2016-5-25
     * @param file
     * @param dirType 保存新路径类别
     * @return 保存后的新文件信息
     * @throws GeneralException
     */
    public boolean saveFileToDisk(InputStream inputStream) throws GeneralException
    {
    	boolean result=false;
        if(inputStream==null){
        	return false;
        }
		try{
			this.copyFile(inputStream); 
			result=true;
		} catch(Exception e){       
			result=false;
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
        return result;
    }
    private void copyFile(InputStream inputStream) throws GeneralException {
        FileOutputStream fs = null;
        Boolean isHaveError=false;
        HSSFWorkbook wb = null;
        HSSFSheet sheet = null;
        InputStream fileinputStream=null;
        try {
            if (inputStream!=null) { //文件存在时 
        			wb = new HSSFWorkbook(inputStream);  
        			int sheetsnum = wb.getNumberOfSheets();
        			int page_row=this.getMaxPageId(tabid+"")+100;
        			for (int i = 0; i < sheetsnum; i++) {
        				sheet = wb.getSheetAt(i);
        				int rows = sheet.getPhysicalNumberOfRows();
        				if(rows<1){
        					continue;
        				}
        				Row row = sheet.getRow(0);
        				if(row==null){
        					continue;
        				}
        				Cell cell0 = row.getCell(0);
        				if (cell0 != null) {
        					String val = cell0.getStringCellValue();
        					val=val.trim();
        					if(val.startsWith("pageid")){
        						String pageidv=val.substring(7);
        						if(pageidv.startsWith("A")){
        							int k=Integer.valueOf(pageidv.substring(1));
        							if(k>page_row){
        								page_row=k+1;
        							}
        						}
        					}
        				}
        			}
        			for (int i = 0; i < sheetsnum; i++) {
        				sheet = wb.getSheetAt(i);
        				int rows = sheet.getPhysicalNumberOfRows();
        				if(rows<1){
        					continue;
        				}
        				String pageid="";
        				boolean bl=false;
        				/**遍历行*/
                     Row row = sheet.getRow(0);
                     if(row==null){
                     	continue;
                     }
                     Cell cell = row.getCell(0);
                     if (cell != null) {
                     	String value = cell.getStringCellValue();
                     	value=value.trim();
                     	if(value.startsWith("pageid:")){
                     		pageid=value.substring(7);
                     	}else{
                     		pageid="A"+(page_row+i);
                     		bl=true;
                     	}
                     	
                     }
                     if(bl){
                     	insertRow(wb, sheet, 0, 1,pageid);
                     }
        			}
        			//获取本地临时文件目录，存放在临时文件目录里。
        			String filepath=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+UploadContant.htmlMoudle_fileName;
        			fs = new FileOutputStream(filepath);  
        			wb.write(fs);
        			//针对该临时目录存放到VFS上
        			File file=new File(filepath);
        			if(file.exists()) {
        				fileinputStream = new FileInputStream(file);
        				saveInputStreamToVFS(fileinputStream,2);
        			}
            }
        } 
        catch (Exception e) { 
     	   isHaveError=true;
            e.printStackTrace(); 
            throw new GeneralException("","复制文件到文件存放目录失败，请联系管理员！" + e.getMessage(),"","");
        } finally {
        	PubFunc.closeDbObj(fileinputStream);
            PubFunc.closeIoResource(inputStream);
            PubFunc.closeIoResource(fs);
        }
	}
	/**
	 * 功能 保存json字符串到指定服务器上
	 * @param json
	 * @return
	 */
	public boolean saveJsonStr(JSONObject json) {
		boolean result=false;
		String str=json.toString();
		if(StringUtils.isEmpty(str)){
			return result;
		}
		InputStream filecontent =null;
		try {
			filecontent= new ByteArrayInputStream(str.getBytes("UTF-8"));
            saveInputStreamToVFS(filecontent,1);
			result=true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(filecontent);
		}
		return result;
	}
	/***
	 * 保存文件到VFS设置的路径里，且将fieldid保存到template_table 这张表里。
	 * @param filecontent
	 * @param flag:值为2 时存放html模板fieldid，为其他时，则为json文本fieldid
	 * @throws Exception
	 * @throws SQLException
	 */
	public void saveInputStreamToVFS(InputStream filecontent,int flag) throws Exception,
			SQLException {
		String fileName=flag==2?"htmlmoudle.xls":(this.tabid+"_json.json");
		String columName=flag==2?"html_module_excel":"html_module_json";
		//VFS保存文件。
		String fileid = VfsService.addFile("su", VfsFiletypeEnum.multimedia,VfsModulesEnum.RS, VfsCategoryEnum.other,"", filecontent,fileName, "", false);
		//获取库里原有的oldFieldId
		String oldFieldId=getFileidFromTable(flag);
		if(StringUtils.isNotBlank(oldFieldId)){
			//删除库里原有的 fildid
			VfsService.deleteFile("su", oldFieldId);
		}
		//更新字段到该模板中
		String sql="update template_table set "+columName+"=? where tabid="+this.tabid;
		ArrayList param=new ArrayList();
		param.add(fileid);
		ContentDAO dao = new ContentDAO(this.conn);
		dao.update(sql, param);
	}
    /** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public void copyFile(String oldPath, String newPath) throws GeneralException { 
	   FileInputStream inStream = null;
       FileOutputStream fs = null;
       Boolean isHaveError=false;
       HSSFWorkbook wb = null;
       HSSFSheet sheet = null;
       try {
           File oldfile = new File(oldPath);
           if (oldfile.exists()) { //文件存在时 
       			inStream = new FileInputStream(oldPath); //读入原文件 
       			wb = new HSSFWorkbook(inStream);  
       			int sheetsnum = wb.getNumberOfSheets();
       			int page_row=this.getMaxPageId(tabid+"")+100;
       			for (int i = 0; i < sheetsnum; i++) {
       				sheet = wb.getSheetAt(i);
       				int rows = sheet.getPhysicalNumberOfRows();
       				if(rows<1){
       					continue;
       				}
       				Row row = sheet.getRow(0);
       				if(row==null){
       					continue;
       				}
       				Cell cell0 = row.getCell(0);
       				if (cell0 != null) {
       					String val = cell0.getStringCellValue();
       					val=val.trim();
       					if(val.startsWith("pageid")){
       						String pageidv=val.substring(7);
       						if(pageidv.startsWith("A")){
       							int k=Integer.valueOf(pageidv.substring(1));
       							if(k>page_row){
       								page_row=k+1;
       							}
       						}
       					}
       				}
       			}
       			for (int i = 0; i < sheetsnum; i++) {
       				sheet = wb.getSheetAt(i);
       				int rows = sheet.getPhysicalNumberOfRows();
       				if(rows<1){
       					continue;
       				}
       				String pageid="";
       				boolean bl=false;
       				/**遍历行*/
                    Row row = sheet.getRow(0);
                    if(row==null){
                    	continue;
                    }
                    Cell cell = row.getCell(0);
                    if (cell != null) {
                    	String value = cell.getStringCellValue();
                    	value=value.trim();
                    	if(value.startsWith("pageid:")){
                    		pageid=value.substring(7);
                    	}else{
                    		pageid="A"+(page_row+i);
                    		bl=true;
                    	}
                    	
                    }
                    if(bl){
                    	insertRow(wb, sheet, 0, 1,pageid);
                    }
       			}
       			fs = new FileOutputStream(newPath);  
                wb.write(fs);  
           }
       } 
       catch (Exception e) { 
    	   isHaveError=true;
           e.printStackTrace(); 
           throw new GeneralException("","复制文件到文件存放目录失败，请联系管理员！" + e.getMessage(),"","");
       } finally {
    	   PubFunc.closeResource(wb);
           PubFunc.closeIoResource(inStream);
           PubFunc.closeIoResource(fs);
           if(isHaveError){//如果上传复制过程中出错，删除生成的文件。
        	   File newfile = new File(newPath); 
        	   if(newfile.exists()){
        		   newfile.delete();
        	   }
           }
       }
   } 
   
   private int getMaxPageId(String tabId) {
		return excelLayoutDao.getMaxPageId(tabId);
	}
   public static void insertRow(HSSFWorkbook wb, HSSFSheet sheet, int starRow,int rows, String pageid) {
	   sheet.shiftRows(starRow, sheet.getLastRowNum(), rows,true,false);
	   for (int i = 0; i < rows; i++) {
		   HSSFRow sourceRow = null;
		   HSSFRow targetRow = null;
		   HSSFCell sourceCell = null;
		   HSSFCell targetCell = null;
		   short m;
		   sourceRow = sheet.getRow(starRow);
		   targetRow = sheet.createRow(starRow);
		   targetRow.setHeight((short)0);
		   HSSFCell cell0 = targetRow.createCell((short) 0);
		   cell0.setCellValue(new HSSFRichTextString("pageid:"+pageid));
		   for (m = sourceRow.getFirstCellNum(); m < sourceRow.getLastCellNum(); m++) {
			   sourceCell = sourceRow.getCell(m);
			   targetCell = targetRow.createCell(m);
//   		    targetCell.setEncoding(sourceCell.getEncoding());
			   targetCell.setCellStyle(sourceCell.getCellStyle());
			   targetCell.setCellType(sourceCell.getCellType());
		   }
	   }
   } 

   /**
    * 功能读取json字符串
    * @param tabid
    * @return
    */
   public String getHtmlJsoninfo() {
		StringBuffer sb=new StringBuffer();
		BufferedReader in = null;  
		try {
			//VFS获取到文件路径。
			//获取到fieldid
			String fieldid=getFileidFromTable(1);
			if (StringUtils.isNotBlank(fieldid)) {
				
				 //加入编码字符集   
	            in = new BufferedReader(new InputStreamReader(VfsService.getFile(fieldid), "utf-8"));  
				int aRead = 0;
				while ((aRead = in.read()) != -1) {
					sb.append((char)aRead);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(in);
		}
	return sb.toString();
	}

   /**
    * 功能：从templet_table表中获取到fieldid
    * @param flag:值为2 时存放html模板fieldid，为其他时，则为json文本fieldid
    * @return:filedid
    */
   public String getFileidFromTable(int flag) {
	   RowSet rset=null;
	   String columName=flag==2?"html_module_excel":"html_module_json";
	   String fileid="";
	   try {
		   ContentDAO dao = new ContentDAO(this.conn);
		   String querysql = "select "+columName+" fileid from template_table where tabid="+this.tabid;
		   rset = dao.search(querysql);
		   if(rset.next()){
			   fileid=rset.getString("fileid");
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }finally{
		   PubFunc.closeDbObj(rset);
	   }
	   return fileid;
   }
	
/**
    * @author lis
    * @Description: 得到文件大小
    * @date 2016-5-25
    * @param f
    * @return s （数据类型bytes）
    * @throws Exception
    */
   public long getFileSizes(File f) throws Exception{//取得文件大小
       long s=0;
       FileInputStream fis = null;
       try{
           if (f.exists()) {
               fis = new FileInputStream(f);
              s= fis.available();
           } else {
              ;
           }   
       }catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
       }
       finally{
           PubFunc.closeIoResource(fis);
       }
       return s;
   }
   
   /**
    * @author lis
    * @Description: 初始化参数，得到多媒体限制文件大小（bytes）
    * @date 2016-5-25
    * @param bexcept
    * @throws GeneralException
    */
   public void initParam(boolean bexcept) throws GeneralException
   {
       //取参数 路径 大小
       try{   
           ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
//           this.RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
           //使用VFS设置的文件路径。
           this.RootDir = constantXml.getTextValue("/FILESETTING/FILEPATH/PARAMETER/PATH");
           if(StringUtils.isNotBlank(this.RootDir)){
//        	   this.para_maxsize = constantXml.getNodeAttributeValue("filepath/multimedia", "maxsize");
        	   this.para_maxsize = constantXml.getTextValue("/FILESETTING/FILESIZECONTROL/DOC")+"M";
        	   String multimedia_maxsize = para_maxsize;
        	   
        	   this.RootDir=this.RootDir.replace("\\",File.separator);          
        	   if (!this.RootDir.endsWith(File.separator)) this.RootDir =this.RootDir+File.separator;   
        	   
        	   File file = new File(this.RootDir);
        	   if(!file.isDirectory()) {//文件路径不存在
        		   if (bexcept){
        			   if(!file.mkdir())//创建文件路径
        				   throw new GeneralException("多媒体存储路径无法访问或不存在！请检查【系统管理-应用设置-参数设置-系统参数-文件存放目录】设置是否有误。");
        		   }
        		   else {
        			   return;
        		   }
        	   }
        	
        	   this.RootDir=this.RootDir+"multimedia"+File.separator;
        	   if ((multimedia_maxsize==null) ||("".equals(multimedia_maxsize))){                
        		   multimedia_maxsize="0";
        	   }
        	   float maxSize =0;
        	   multimedia_maxsize= multimedia_maxsize.toUpperCase();
        	   int k=1;
        	   if (multimedia_maxsize.indexOf("K")>0){
        		   k=1024;
        	   }
        	   else if (multimedia_maxsize.indexOf("M")>0){
        		   k=1024*1024;
        	   }
        	   else if (multimedia_maxsize.indexOf("G")>0){
        		   k=1024*1024*1024;
        	   }
        	   else if (multimedia_maxsize.indexOf("T")>0){
        		   k=1024*1024*1024*1024;
        	   } 
        	   multimedia_maxsize =multimedia_maxsize.replaceAll("K", "").replaceAll("M", "")
        	   .replaceAll("G", "").replaceAll("T", "").replaceAll("B", "");
        	   if ("".equals(multimedia_maxsize)){
        		   multimedia_maxsize="0";
        	   }
        	   try{
        		   maxSize = Float.parseFloat(multimedia_maxsize);
        		   maxSize= maxSize*k; 
        		   this.maxFileSize = maxSize;
        	   }
        	   catch (Exception e){
        		   if (bexcept)
        			   throw new GeneralException("多媒体大小有非法字符！请检查"); 
        	   }
           }else {
        	   this.RootDir = "";
		}
   
       }  
       catch(Exception e)
       {            
           throw GeneralExceptionHandler.Handle(e);
       }
   }
   
  /**
   * 检查模板文件是否存在
   * @return
   * @throws GeneralException
   */
   public HashMap checkFileisExists(){
	   HashMap fileinfo=new HashMap();
       try{
    	   String fieldid=getFileidFromTable(2);
    	   if(StringUtils.isEmpty(fieldid)){
    		   fileinfo.put("errorinfo", "没有上传该文件，无法下载！");
    	   }else{
    		   fileinfo.put("fieldid", fieldid);
    		   return fileinfo;
    	   }
       }catch(Exception e)
       {
    	   fileinfo.put("errorinfo", e.getMessage());
       }
       return fileinfo;
   }

	public String getDestFileName() {
		return DestFileName;
	}
	
	public void setDestFileName(String destFileName) {
		DestFileName = destFileName;
	}
	
	public String getPara_maxsize() {
		return para_maxsize;
	}
	
	public void setPara_maxsize(String para_maxsize) {
		this.para_maxsize = para_maxsize;
	}
	
	public float getMaxFileSize() {
		return maxFileSize;
	}
	
	public void setMaxFileSize(float maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	
	public String getRootDir() {
		return RootDir;
	}
	
	public void setRootDir(String rootDir) {
		RootDir = rootDir;
	}
	
	public String getAbsoluteDir() {
		return absoluteDir;
	}
	
	public void setAbsoluteDir(String absoluteDir) {
		this.absoluteDir = absoluteDir;
	}
	
	public String getExt() {
		return ext;
	}
	
	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getRealFileName() {
		return realFileName;
	}

	public void setRealFileName(String realFileName) {
		this.realFileName = realFileName;
	}
	
}