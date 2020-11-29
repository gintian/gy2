package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 *<p>Title:deleteFileTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jul 19, 2008:2:53:07 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class deleteFileTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String type = (String)this.formHM.get("ext");
			String file_id = (String)this.formHM.get("file_id");
			String ext = "",content="";
			if("ext".equalsIgnoreCase(type)){
				ext = "ext";
				content = "content";
			}
			else if("orgext".equalsIgnoreCase(type)){
				ext = "originalext";
				content = "originalfile";
			}
			String updateSql = "update law_base_file set "+ext+" = null,"+content+" = null where file_id = '"+file_id+"'";
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer();
			sql.append("select fileid,originalfileid");
			sql.append(" from law_base_file");
			sql.append(" where file_id=?");
			ArrayList<String> param = new ArrayList<String>();
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(file_id);
			if(isNum.matches()) {
				param.add(file_id);
			} else {
				param.add(PubFunc.decrypt(file_id));
			}
				
			this.frowset = dao.search(sql.toString(), param);
			String fileid = "";
			if (this.frowset.next()) {
				if ("ext".equalsIgnoreCase(type)) {
					fileid = this.frowset.getString("fileid");
				} else if ("orgext".equalsIgnoreCase(type)) {
					fileid = this.frowset.getString("originalfileid");
				}
			} 
			
			if(StringUtils.isNotEmpty(fileid)) {
				VfsService.deleteFile(this.getUserView().getUserName(), fileid);
			}
			
			dao.update(updateSql);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.formHM.put("mess","ok");
		}
	}

}
