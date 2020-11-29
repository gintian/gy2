package com.hjsj.hrms.transaction.general.inform.multimedia;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
/**
 * 
 * 
 * Title:SaveRelMultimediaListTrans.java
 * Description:
 * Company:hjsj
 * Create time:May 16, 2014:3:52:44 PM
 * @author zhaogd
 * @version 6.x
 */
public class SaveRelMultimediaListTrans extends IBusiness{

	public void execute() throws GeneralException {
		FileOutputStream out = null;
		InputStream in = null;
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
    		String kind = (String)this.getFormHM().get("kind");
    		String dbflag = (String)this.getFormHM().get("dbflag");
            String nbase = (String)this.getFormHM().get("nbase");
            String A0100 = (String)this.getFormHM().get("a0100");
            String I9999 = (String)this.getFormHM().get("i9999");//子集中i9999
            String setid = (String)this.getFormHM().get("setid");
            HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String delete_record = (String)hm.get("delete_record");//关联后是否删除多媒体子集对应记录  0，不删；1，删除
			String i9999list = (String)hm.get("i9999list");//多媒体a00中被选中i9999集合
			i9999list = i9999list==null?"":i9999list;
			MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
                    dbflag,nbase,setid,A0100,Integer.parseInt(I9999));
			if(i9999list.trim().length()>0){
				i9999list = i9999list.substring(0, i9999list.length() - 1);
				String sql = "select * from " + nbase + "a00 where a0100='" + A0100 + "' and i9999 in("+i9999list+")";
				this.frowset = dao.search(sql);
				while(this.frowset.next()){
					HashMap map = new HashMap();
					String title = this.frowset.getString("title");
					String prefix = title;
					if(title==null||title.length()<4)
						prefix = "media";
					String ext = this.frowset.getString("ext");
					if(ext==null||ext.indexOf(".")==-1)
						ext="."+ext;
					
					String fileType = "'.html','.htm','.php','.php2','.php3','.php4','.php5','.phtml','.pwml',"
			   	 			+ "'.inc','.asp','.aspx','.ascx','.jsp','.cfm','.cfc','.pl','.bat','.exe','.com','.dll',"
			   	 			+ "'.vbs','.js','.reg','.cgi','.htaccess','.asis','.sh','.shtml','.shtm','.phtm'";
					if(fileType.contains("'" + ext.toLowerCase() + "'"))
			   	 		throw new GeneralException("", "不允许上传" + ext + "类型文件！", "", "");
			   	 	
					File file = File.createTempFile(prefix+"-", ext, new File(System.getProperty("java.io.tmpdir")));
					if (file.exists()) {					   
					    file.delete();
					    file.deleteOnExit();
					}
					out = new FileOutputStream(file);
					int len;
					byte buf[] = new byte[1024];
					String fileId = this.frowset.getString("fileid");
					if(StringUtils.isNotEmpty(fileId)) {
						in =  VfsService.getFile(fileId);
					}
					
					while (in != null && (len = in.read(buf, 0, 1024)) != -1) {
						out.write(buf, 0, len);
					}
					
					out.close();
					String filename = file.getName(); 
					map.put("mainguid", multiMediaBo.getMainGuid());
					map.put("childguid", multiMediaBo.getChildGuid());
					map.put("a0100", A0100);
					map.put("nbase", nbase);
					map.put("setid", setid);
					map.put("i9999", I9999);
					map.put("dbflag", dbflag);
					map.put("srcfilename", filename);
					map.put("description", "");
					map.put("filetitle", title);
					map.put("ext", ext);
					map.put("filetype", this.frowset.getString("Flag"));
					map.put("state", this.frowset.getString("State"));//a00系统保留
					multiMediaBo.saveMultimediaFile(map, file);
				}
			}
			if(i9999list.trim().length()>0&&"1".equalsIgnoreCase(delete_record)){//删除多媒体a00中被选中i9999集合
				String sql="delete from " + nbase + "a00 where  a0100='" + A0100 + "' and i9999 in("+i9999list+")";
				dao.update(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(out);
			PubFunc.closeResource(in);
		}
	}
}
