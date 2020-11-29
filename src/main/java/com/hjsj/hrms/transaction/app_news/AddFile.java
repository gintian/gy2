package com.hjsj.hrms.transaction.app_news;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class AddFile extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String newsid = (String)this.getFormHM().get("news_id");
		//2014.11.7 xxd 文件上传参数过滤
		newsid = PubFunc.hireKeyWord_filter(newsid);
		String fileName = (String)this.getFormHM().get("fileName");
		fileName = PubFunc.hireKeyWord_filter(fileName);
		FormFile file = (FormFile)this.getFormHM().get("newsfile");
		String fname = "",ext = "";
		IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			if(file!=null&&file.getFileSize()>0){
				
				fname = file.getFileName();
				int indexInt = fname.lastIndexOf(".");
				ext = fname.substring(indexInt + 1, fname.length());
				String filename = fname.substring(0,indexInt);
				RecordVo fvo = new RecordVo("appoint_news_ext_file");
				String fid = idg.getId("appoint_news_ext_file.id");
				fvo.setString("ext_file_id",fid);
				if(fileName==null|| "".equals(fileName))
					fvo.setString("name",filename);
				else
					fvo.setString("name",fileName);
				fvo.setString("ext", ext);
				fvo.setString("news_id",newsid);
				fvo.setDate("createtime",new Date());
				byte[] data = file.getFileData();
				fvo.setObject("content",data);
				dao.addValueObject(fvo);
				this.getFormHM().put("fileName","");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
