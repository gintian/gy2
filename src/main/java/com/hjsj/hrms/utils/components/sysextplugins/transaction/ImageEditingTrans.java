package com.hjsj.hrms.utils.components.sysextplugins.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
* <p>Title:ImageEditingTrans </p>
* <p>Description: 图片显示控件 图片改名</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Dec 4, 2015 5:59:47 PM
 */
public class ImageEditingTrans extends IBusiness{
	public void execute() throws GeneralException{
		
		try {
			Boolean success = false;
			//20/3/11 xus vfs改造
			String fileid = (String)this.getFormHM().get("fileid");
			String w0101 = (String)this.getFormHM().get("w0101");
			if(StringUtils.isNotBlank(w0101)) {
				String sql = "select * from W01 where W0101 = ? ";
				ArrayList values = new ArrayList();
				values.add(w0101);
				ContentDAO dao = new ContentDAO(this.frameconn);
				this.frowset = dao.search(sql,values);
				if(this.frowset.next()) {
					success = true;
					sql = " update W01 set fileid = ? where W0101 = ? ";
					values = new ArrayList();
					values.add(fileid);
					values.add(w0101);
					dao.update(sql, values);
				}
			}
			this.getFormHM().put("success",success);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		
//		String imgname = (String)this.getFormHM().get("imgname");
//		String filename = (String)this.getFormHM().get("filename");//保存后的图片名称
//		if(StringUtils.isNotBlank(filename))
//			filename = PubFunc.decrypt(filename);
//		String filepath = (String)this.getFormHM().get("filepath");
//		if(StringUtils.isNotBlank(filepath))
//			filepath = PubFunc.decrypt(filepath);
//		File f = new File(filepath);
//		if(f.exists()){
//			File s[] = f.listFiles();
//	        for(int i=0;i<s.length;i++) {
//	        	String name = s[i].getName();
//		        int index = name.indexOf(".");
//		        String fnameString = name.substring(0,index);
//		        if(fnameString.equals(imgname)){
//		        	File newFiles = new File(filepath+name);
//		        	newFiles.delete();
//		        }
//	        }
//		}
//		int index = filename.indexOf(".");
//		String filehou = filename.substring(index);
//        File file = new File(filepath+filename);
//        File newFile = new File(filepath+imgname+filehou);  
//        if(newFile.exists()){
//        	newFile.delete();
//        }
//        file.renameTo(new File(filepath+imgname+filehou));   //改名     
//        // this.getFormHM().put("imagename", imgname+filehou);
//        if(!imgname.equals("")){
//        	this.getFormHM().put("imagename", imgname+filehou);
//        	this.getFormHM().put("pubFilepath", PubFunc.encryption(filepath+imgname+filehou));
//        }else{
//        	this.getFormHM().put("imagename", filename+filehou);
//        	this.getFormHM().put("pubFilepath", PubFunc.encryption(filepath+filename+filehou));
//        }
	}
}