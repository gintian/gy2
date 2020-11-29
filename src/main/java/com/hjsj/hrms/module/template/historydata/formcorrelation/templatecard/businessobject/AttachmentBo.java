package com.hjsj.hrms.module.template.historydata.formcorrelation.templatecard.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* @Title: AttachmentBo
* @Description:
* @author: hej
* @date 2019年11月20日 下午6:30:20
* @version
 */
public class AttachmentBo {
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
	
	public AttachmentBo(UserView userView,Connection conn,String tabid){
		this.userView = userView;
		this.conn = conn;
		this.tabid = tabid;
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
            String str  = fileName; 
            int iHash = Math.abs(str.hashCode());
            String dir1 = ""+iHash/1000000%500;
            while (dir1.length()<3) dir1 ="0"+dir1;
            String dir2 = ""+iHash/1000%500;
            while (dir2.length()<3) dir2 ="0"+dir2;     
            relative.append(dirType);
            relative.append(tabid);
            relative.append("\\T");
            relative.append(dir1);
            relative.append("\\T");
            relative.append(dir2);
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
           this.RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
           if(StringUtils.isNotBlank(this.RootDir)){
        	   this.para_maxsize = constantXml.getNodeAttributeValue("filepath/multimedia", "maxsize");
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
  * 
  * @param file_id
  * @param archive_id
  * @param record_id
  * @param archive_year
 * @param attachmenttype 
  * @return
  * @throws GeneralException
  */
   public HashMap downloadFile(String file_id, String archive_id, String record_id, String archive_year, String attachmenttype)throws GeneralException {    
		String path = "";
		String ext = "";
		HashMap map = new HashMap();
		InputStream in = null;
		try {
			initParam(true);
			TemplateDataBo templateDataBo = new TemplateDataBo(this.conn, this.userView, Integer.parseInt(tabid),
					archive_id);
			HashMap dataMap = templateDataBo.analysisJson2Map(record_id, archive_year);
			if (dataMap.containsKey("t_wf_file_"+attachmenttype)) {
				ArrayList fileList = (ArrayList) dataMap.get("t_wf_file_"+attachmenttype);
				for (int i = 0; i < fileList.size(); i++) {
					HashMap fileMap = (HashMap) fileList.get(i);
					String fileid = (String) fileMap.get("file_id");
					if (fileid.equals(file_id)) {
						path = (String) fileMap.get("filepath");
						String content = (String) fileMap.get("content");
						//归档附件下载 兼容vfs
						if(!StringUtils.isNumeric(PubFunc.decrypt(path))) {
							if (StringUtils.isNotBlank(path)) {
								if (!path.startsWith(this.RootDir)) {
									path = this.RootDir + path;
								}
								path = path.replace("\\", File.separator);
							}
						}
						if(StringUtils.isNotBlank(content)) {
							byte [] markbytes = Base64.decodeBase64(content);
							in = new ByteArrayInputStream(markbytes);
						}
						String filename = (String) fileMap.get("name");
						ext = (String) fileMap.get("ext");
						if (ext.indexOf(".") == -1)
							this.ext = "." + ext;
						else
							this.ext = ext;
						this.DestFileName = filename + this.ext;
						break;
					}
				}
			}
			map.put("filepath", path);
			map.put("ole", in);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
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
