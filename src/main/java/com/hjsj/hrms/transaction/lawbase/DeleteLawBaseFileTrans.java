package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.param.DocumentParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>
 * Title:DeleteLawBaseFileTrans
 * </p>
 * <p>
 * Description:删除规章制度文件
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 3, 2005:10:29:52 AM
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DeleteLawBaseFileTrans extends IBusiness {

	/**
	 * 
	 */
	public DeleteLawBaseFileTrans() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try{
			ArrayList list = (ArrayList) this.getFormHM().get("selectedlist");
			if (list == null || list.size() == 0)
				return;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String type = (String)this.formHM.get("basetype");
			
			ArrayList n_list=new ArrayList();
			StringBuffer file_ids = new StringBuffer();
			String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
			for(int i=0;i<list.size();i++) {
				RecordVo vo=(RecordVo)list.get(i);
				if(!"false".equals(law_file_priv.trim())){
					if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, vo.getString("file_id")))
						continue;
				}
				
				file_ids.append("'" + vo.getString("file_id") + "',");
				n_list.add(vo);				
			}
			
			if(StringUtils.isNotEmpty(file_ids.toString()) && file_ids.toString().endsWith(","))
				file_ids.setLength(file_ids.length() - 1);
				
			String searchFileSql = "select FILEID,ORIGINALFILEID from law_base_file where file_id in (" + file_ids + ")";
			this.frowset = dao.search(searchFileSql);
			while(this.frowset.next()) {
				String fileId = this.frowset.getString("FILEID");
				String originalfileId = this.frowset.getString("ORIGINALFILEID");
				if(StringUtils.isNotEmpty(fileId))
					VfsService.deleteFile(this.userView.getUserName(), fileId);
				
				if(StringUtils.isNotEmpty(originalfileId))
					VfsService.deleteFile(this.userView.getUserName(), originalfileId);
			}
			
			dao.deleteValueObject(n_list);
			
			if("5".equalsIgnoreCase(type)){
				DocumentParamXML documentParamXML = new DocumentParamXML(this.getFrameconn());
				String codesetid=documentParamXML.getValue(DocumentParamXML.FILESET,"setid");
				String codeitemid=documentParamXML.getValue(DocumentParamXML.FILESET,"fielditem");
				if(!(codeitemid==null|| "".equalsIgnoreCase(codeitemid))){
					ArrayList dblist = new ArrayList();
					String dbsql = "select Pre from DBName";
					try {
						this.frowset = dao.search(dbsql);
						while(this.frowset.next()){
							dblist.add(this.frowset.getString("Pre"));
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					for(int j=0;j<n_list.size();j++){
						RecordVo vo = (RecordVo)n_list.get(j);
						String fileid = vo.getString("file_id");
						try {
							for(int i=0;i<dblist.size();i++){
								String sql  = "select a0100,max(i9999) i9999 from "+dblist.get(i)+codesetid+" where "+codeitemid+" = '"+fileid+"' group by a0100";
								RecordVo vo1 = new RecordVo(dblist.get(i)+codesetid);
								this.frowset  = dao.search(sql);
								while(this.frowset.next()){
									vo1.setString("a0100",this.frowset.getString("a0100"));
									vo1.setString("i9999",this.frowset.getString("i9999"));
									dao.deleteValueObject(vo1);
								}
							}
						}catch (SQLException e1) {
							Exception ex = new Exception("请检查您后台文档管理设置指标是否存在！");
							throw GeneralExceptionHandler.Handle(ex);
						}
					}
				}
			}
			
			StringBuffer sb = new StringBuffer();
			for(int j=0;j<n_list.size();j++){
				RecordVo vo = (RecordVo)n_list.get(j);
				String fileid = vo.getString("file_id");
				sb.append(",'"+fileid+"'");
			}
			
			searchFileSql = "select FILEID from law_ext_file where file_id in (" + sb.substring(1) + ")";
			this.frowset = dao.search(searchFileSql);
			while(this.frowset.next()) {
				String fileId = this.frowset.getString("FILEID");
				if(StringUtils.isNotEmpty(fileId))
					VfsService.deleteFile(this.userView.getUserName(), fileId);
			}
			
			String sql = "delete from law_ext_file where file_id in("+sb.substring(1)+")";
			dao.update(sql);
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
