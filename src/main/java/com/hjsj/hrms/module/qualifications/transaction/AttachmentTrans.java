package com.hjsj.hrms.module.qualifications.transaction;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttachmentTrans  extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		ArrayList list = new ArrayList();
		String conditionid = null;
		String type = null;
		if(!StringUtils.isEmpty((String)this.getFormHM().get("conditionid")))
			conditionid = (String)this.getFormHM().get("conditionid");
		if(!StringUtils.isEmpty((String)this.getFormHM().get("type")))
			type = (String)this.getFormHM().get("type");
		list = (ArrayList)this.getFormHM().get("list");
		if(null == list || list.isEmpty()) {
			return;
		}
		// 48764 先校验是否返回false  若失败直接抛出异常信息
		Object successed = (Object)((MorphDynaBean)list.get(0)).get("successed");
		if(successed instanceof Boolean) {
			boolean successedBool = ((Boolean) successed).booleanValue();
			if(!successedBool) {
				String msg = (String)((MorphDynaBean)list.get(0)).get("msg");
				if(StringUtils.isBlank(msg)) 
					msg = ResourceFactory.getProperty("error.common.upload.invalid");
				throw new GeneralException(msg);
			}
		}
		File directory = new File("..");//设定为当前文件夹 
		String root_path = "";
		String multimedia_path = "";
		String qualifications_path = "";
		try {
			ConstantXml constantXml = new ConstantXml(this.frameconn, "FILEPATH_PARAM");
			String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
			String fileSizeLimit = constantXml.getNodeAttributeValue("filepath/multimedia", "maxsize");
			this.getFormHM().put("fileSizeLimit", StringUtils.isEmpty(fileSizeLimit)?"10MB":fileSizeLimit+"B");
			
			if(type==null){//点击加号的时候不判断了，上传文件的时候判断
				Boolean bl = true;
				String arr = rootDir.replace(File.separator, "");
				if(arr.indexOf(":")==1){
					arr = arr.substring(0,1)+arr.substring(2, arr.length());
				}
				Pattern p = Pattern.compile("[\\/*:?<>\"]+");
				Matcher m = p.matcher(arr);
				if(m.find()){
					bl = false;
				}
				if(bl){
					File tempDir = new File(rootDir.replace("\\", File.separator));
					if(!tempDir.exists()){
						root_path = rootDir.replace("\\", File.separator);
						tempDir.mkdirs();
					}
					if(!tempDir.exists()){//检查文件夹是否存在，不存在，则路径无效
						bl = false;
					}
				}
				if (rootDir == null || "".equals(rootDir)) {
					/*if(type!=null){
						this.getFormHM().put("pathToF", false);
						this.getFormHM().put("msge", "没有配置多媒体存储路径！");
						return;
					}else{*/
						throw new GeneralException("没有配置多媒体存储路径！");
					//}    
				}
	
				rootDir = rootDir.replace("\\", File.separator);
				if (!rootDir.endsWith(File.separator))
					rootDir = rootDir + File.separator;
	
				File dir0 = new File(rootDir);
				if(!bl){
					/*if(type!=null){
						this.getFormHM().put("pathToF", false);
						this.getFormHM().put("msge", "多媒体存储路径不正确！");
						return;
					}else{*/
						throw new GeneralException("多媒体存储路径不正确！");
					//}
				}
				rootDir = rootDir + "multimedia" + File.separator;
				File dir1 = new File(rootDir);
	
				if (!dir1.exists()){
					multimedia_path = rootDir;
					dir1.mkdirs();
				}
				String topath = rootDir+"jobtitle"+File.separator+"qualifications"+File.separator+PubFunc.decrypt(conditionid);
				File dir =new File(topath);
				if (!dir.exists()) {
					qualifications_path = rootDir;
					dir.mkdirs();
				}
				
				String sourcepath = PubFunc.decrypt((String)((MorphDynaBean)list.get(0)).get("path"))+PubFunc.decrypt((String)((MorphDynaBean)list.get(0)).get("filename"));  	
//				for(int i=0;i<list.size();i++){		//为一次上传多个文件做准备
//				}
//				File fi = new File(sourcepath);
//				File fil = new File(topath+File.separator+((MorphDynaBean)list.get(0)).get("filename"));
//				if(fil.exists()){
//					fil.delete();
//				}
//				boolean success = fi.renameTo(new File(dir,(String)((MorphDynaBean)list.get(0)).get("localname")));
				
				File fi = new File(sourcepath);//控件传给我的文件上传后存放的路径
				String pp = topath + File.separator + (String)((MorphDynaBean)list.get(0)).get("localname");//目标存放路径路径
				
				boolean copy_status = copy(fi, pp);
				this.getFormHM().put("result", copy_status);
				if(!copy_status) {
					//如果上传的时候失败了，删除已经上传的文件夹，否则会出现空文件夹，不是太好
					if(!StringUtils.isEmpty(qualifications_path)) {
						File qualifications_path_file = new File(qualifications_path);
						qualifications_path_file.delete();
						qualifications_path = qualifications_path.substring(0, qualifications_path.lastIndexOf(File.separator));
						qualifications_path_file = new File(qualifications_path);
						qualifications_path_file.delete();
						qualifications_path = qualifications_path.substring(0, qualifications_path.lastIndexOf(File.separator));
						qualifications_path_file = new File(qualifications_path);
						qualifications_path_file.delete();
					}
					if(!StringUtils.isEmpty(multimedia_path)) {
						File multimedia_path_file = new File(multimedia_path);
						multimedia_path_file.delete();
					}
					
					if(!StringUtils.isEmpty(root_path)) {
						File root_path_file = new File(root_path);
						root_path_file.delete();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}



	}

	public static boolean copy(File oldfile, String newPath) throws GeneralException {
		boolean flag = true;
		InputStream inStream= null;
		FileOutputStream fs=null;
		try {
			int bytesum = 0;
			int byteread = 0;
			// File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				inStream = new FileInputStream(oldfile);
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					fs.write(buffer, 0, byteread);
				}
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("zc_new.qualification.copyError")+e.getMessage()));
		}finally {
			PubFunc.closeIoResource(fs);
			PubFunc.closeIoResource(inStream);
			//如果出现了问题，删除原先的文件
			//如磁盘空间已满，写到一半，这种类似问题
			if(!flag) {
				File newPath1 = new File(newPath);
				if(newPath1.exists()) {
					newPath1.delete();
				}
			}
		}
		return flag;
	}
}
