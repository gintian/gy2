package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.emailtemplate.businessobject.TemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveEmailAttachTrans</p>
 * <p>Description:保存附件</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 3, 2015 2:05:40 PM</p>
 * @author sunming
 * @version 1.0
 */
public class SaveEmailAttachTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String templateId =(String) this.getFormHM().get("template_id");
			ArrayList file_list = (ArrayList) (this.getFormHM().get("file_list")==null?new ArrayList():this.getFormHM().get("file_list"));
			
			TemplateBo bo = new TemplateBo(this.frameconn,new ContentDAO(this.frameconn), this.getUserView());
			/**
			 * 全部删除文件
			 */
			if(file_list.isEmpty()){
				bo.deleteAllEmailAttach(templateId);
				return;
			}
			
			//现在存在的附件
			HashMap map = bo.getAllBaseFile(templateId);
			/**
			 * 将前台传过来的上传文件转为map，便于下面对其判断使用
			 */
			HashMap<String, String> preMap = new HashMap<String, String>();
			MorphDynaBean bean = null;
			String fileid = "";
			String fileName = "";
			String type="1";
			for (int i = 0; i < file_list.size(); i++) {
				bean = (MorphDynaBean) file_list.get(i);
				fileid = (String)bean.get("fileid");
				fileName = PubFunc.decrypt((String)bean.get("filename"));
				preMap.put("" + i + "", fileid);
				// 数据库中不存在当前上传的文件，执行新增操作(若存在则跳过)
				if (!map.containsValue(fileid)) {
					bo.insertEmail_attach(templateId,fileid,fileName,fileName);
				}
			}
			ArrayList values = new ArrayList(map.values());
			for (int i = 0; i < values.size(); i++) {
				fileid = (String) values.get(i);
				String id = "";
				if(!preMap.containsValue(values.get(i)))//preMap中不含有数据库中map的差异数据即为需要删除的文件
					bo.deleteEmailAttach(templateId, (String)values.get(i));//删除文件记录
			}
    		
    		ArrayList attachlist = bo.getAttachList(templateId);
    		JSONArray attachJson = JSONArray.fromObject(attachlist);
			this.getFormHM().put("attachJson",attachJson);
    		this.getFormHM().put("isok",type);
    		this.getFormHM().put("id",templateId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
