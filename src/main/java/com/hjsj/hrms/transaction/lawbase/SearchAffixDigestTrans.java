package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;

public class SearchAffixDigestTrans  extends IBusiness {
	public void execute() throws GeneralException {
		String file_id = (String)getFormHM().get("file_id");
		file_id = PubFunc.decrypt(SafeCode.decode(file_id));
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		ArrayList list = new ArrayList();
		String law_ext_save="false";
		try {
			frowset = dao.search("select ext_file_id,version,name,ext,create_time,create_user,fileid from law_ext_file where file_id='" + file_id + "' order by create_time");
			
			while (frowset.next()) {
				law_ext_save="true";
				RecordVo vo = new RecordVo("law_ext_file");
				vo.setString("ext_file_id", frowset.getString("ext_file_id"));
				vo.setString("version", frowset.getString("version"));
				vo.setString("name", frowset.getString("name"));
				vo.setString("ext", frowset.getString("ext"));
				Date d_create=frowset.getDate("create_time");
				String d_str=PubFunc.FormatDate(d_create,"yyyy.MM.dd");
				vo.setString("create_time", d_str);
				vo.setString("create_user", frowset.getString("create_user"));
				//将文件id一起查出来
				vo.setString("fileid", frowset.getString("fileid"));
				list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		String field_str_item = (String)this.getFormHM().get("field_str_item");
		String digest_desc = "";
		if (field_str_item.indexOf("digest") != -1) 
		{
			digest_desc = field_str_item.substring(field_str_item.indexOf("digest"));
			digest_desc = digest_desc.substring(digest_desc.indexOf("`")+1,digest_desc.indexOf(","));
		}
		this.getFormHM().put("digest_desc", digest_desc);
		this.getFormHM().put("affixList", list);
		String digest=getDigest(file_id);
		this.getFormHM().put("digest",digest);
		this.getFormHM().put("law_ext_save",law_ext_save);
	}
	
	public String getDigest(String file_id)
	{
		String sql="select digest from law_base_file where file_id='"+file_id+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String digest="";
		try
		{
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				digest=Sql_switcher.readMemo(this.frowset,"digest");	
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if(digest==null||digest.length()<=0|| "null".equalsIgnoreCase(digest))
			digest="";
		return digest;
	}
}
