package com.hjsj.hrms.transaction.sys.options.message;

import com.hjsj.hrms.businessobject.sys.options.message.SysMessage;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SaveSysMessageTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String flag=(String)this.getFormHM().get("flag");
		if(flag==null||flag.length()<=0)
		{
			return;
		}
		if("save".equals(flag))
		{
			String days=(String)this.getFormHM().get("days");
			//2014.11.7 xxd 文件上传参数过滤
			days = PubFunc.hireKeyWord_filter(days);
			String constant=(String)this.getFormHM().get("constant");
			constant = PubFunc.hireKeyWord_filter(constant);	
			/*fckeditor 提交内容过滤注入js代码  guodd 2019-05-06 */
			constant = PubFunc.stripScriptXss(constant);
			String view_hr=(String)this.getFormHM().get("view_hr");
			view_hr = PubFunc.hireKeyWord_filter(view_hr);
			String view_em=(String)this.getFormHM().get("view_em");
			view_em = PubFunc.hireKeyWord_filter(view_em);
			FormFile bgimage = (FormFile)this.getFormHM().get("bgimage");
			String path = (String)this.getFormHM().get("path");
			String backgroudimage = "";
			if(bgimage!=null&&bgimage.getFileSize()>0){
				backgroudimage = copyFile(path,bgimage);
			}else{
				backgroudimage=(String)this.getFormHM().get("backgroudimage");
			}
			backgroudimage=backgroudimage==null?"":backgroudimage;
			if(view_em==null||view_em.length()<=0)
				view_em="0";
			if(view_hr==null||view_hr.length()<=0)
				view_hr="0";
			SysMessage sysMessage=new SysMessage(this.getFrameconn());
			String 	start_date="";
			if(constant!=null&&constant.length()>0)
			{
				Calendar now = Calendar.getInstance();
				Date cur_d=now.getTime();
				start_date=DateUtils.format(cur_d,"yyyy.MM.dd");
			}
			sysMessage.setView_em(view_em);
			sysMessage.setView_hr(view_hr);
			String  constantXML=sysMessage.creatSysNoteXml(start_date,days,constant,backgroudimage);
			sysMessage.addSysNoteXML(constantXML);
			String morrow = SystemConfig.getPropertyValue("is_othermessage_tosend");
			if ("true".equalsIgnoreCase(morrow)) {
				String classpath = SystemConfig.getPropertyValue("morrow_send_jishitong_messageclass");
				String textConstant = (String) this.getFormHM().get("textConstant");
				try {
					IMessage message = (IMessage) Class.forName(classpath).newInstance();
					message.sendMessage(textConstant, days, this.userView.getUserName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if("clear".equals(flag))
		{
			SysMessage sysMessage=new SysMessage(this.getFrameconn());
			HashMap xmlMap = sysMessage.getAllSysNoteXML();
			String backgroudimage = (String)xmlMap.get("backgroudimage");
			delFile(backgroudimage);
			String  constantXML=sysMessage.creatSysNoteXml("","","","");
			sysMessage.addSysNoteXML(constantXML);
		}else if("deletebgimage".equals(flag)){
			SysMessage sysMessage=new SysMessage(this.getFrameconn());
			HashMap hashMap =sysMessage.getAllSysNoteXML();
			String start_date=(String)hashMap.get("start_date");
			String days=(String)hashMap.get("days");
			String constant=(String)hashMap.get("constant");	
			String view_hr=(String)hashMap.get("view_hr");
		    String view_em=(String)hashMap.get("view_em");
		    String backgroudimage = (String)hashMap.get("backgroudimage");
			if(start_date==null||start_date.length()<=0)
			{
				Calendar now = Calendar.getInstance();
				Date cur_d=now.getTime();
				start_date=DateUtils.format(cur_d,"yyyy.MM.dd");
			}
			if(days==null||days.length()<0)
	  	    {
	  		   days="";
	  	    }  	   
	  	    if(constant==null||constant.length()<0)
	  	    {
	  		   constant="";
	  	    }
	  	    sysMessage.setView_em(view_em);
			sysMessage.setView_hr(view_hr);
			delFile(backgroudimage);
			String  constantXML=sysMessage.creatSysNoteXml(start_date,days,constant,"");
			sysMessage.addSysNoteXML(constantXML);
		}
		
		
		this.getFormHM().put("flag","");
	}
	
	/**
	 * 删除vfs文件
	 * @param backgroudimage
	 */
	private void delFile(String backgroudimage) {
		if(StringUtils.isNotBlank(backgroudimage))
		{
			try {
				VfsService.deleteFile(this.getUserView().getUserName(), backgroudimage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String copyFile(String path, FormFile bgimage) { 
		InputStream is = null;
		String fileid = "";
		try {
			//xus 20/4/28 vfs 改造
			//用户名
			String userName = this.getUserView().getUserName();
			//文件类型
			VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
			//所属模块
			VfsModulesEnum vfsModulesEnum = VfsModulesEnum.XT;
			//文件所属类型
			VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
			//guidkey
			String guidkey = "";
			//文件流
			is = bgimage.getInputStream();
			//文件名
			String filename = bgimage.getFileName();
			
			fileid = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, guidkey, is, filename, "", false);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(is);
		}
	  return fileid;
		  
//		  FileOutputStream fs = null;  
//		  String newName = "";
//		  try { 
//	           int bytesum = 0; 
//	           int byteread = 0; 
//	           String filename = bgimage.getFileName();
//	           String type = filename.substring(filename.lastIndexOf(".")+1,filename.length());
//	           newName = "sys_msg_bg."+type;
//	           String filepath = path+File.separator+newName;
//	           File oldfile = new File(filepath);
//	           oldfile.deleteOnExit();
//	               InputStream inStream = com.hjsj.hrms.businessobject.sys.ImageBO.imgStream(bgimage, type); //读入原文件 
//	               fs = new FileOutputStream(filepath); 
//	               byte[] buffer = new byte[1444]; 
//	               while ( (byteread = inStream.read(buffer)) != -1) { 
//	                   bytesum += byteread; //字节数 文件大小 
//	                   fs.write(buffer, 0, byteread); 
//	               } 
//	               inStream.close(); 
//	       } 
//	       catch (Exception e) {  
//	           e.printStackTrace(); 
//	       } 
//	       finally{
//	    	   PubFunc.closeIoResource(fs);
//	       }
		  
	   }
	
	
}
