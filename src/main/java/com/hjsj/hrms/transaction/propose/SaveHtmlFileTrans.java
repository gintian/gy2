/*
 * Created on 2005-5-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.propose;

import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SaveHtmlFileTrans extends IBusiness {

	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo vo = (RecordVo) this.getFormHM().get("proposevo");
//		System.out.println(vo);
		if (vo == null)
			return;
		String name = vo.getString("name");
		vo.setString("name", PubFunc.hireKeyWord_filter(name));
		
		/*fckeditor 提交内容过滤注入js代码  guodd 2019-05-06 */
		String description = vo.getString("description");
		vo.setString("description", PubFunc.stripScriptXss(description));
		
		String flag = (String) this.getFormHM().get("flag");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		FormFile file = (FormFile) this.getFormHM().get("file");
		try {
			//流程上传，不选择附件时，同样报错   jingq  upd 2014.11.10
			boolean accept = true;
			if(file!=null&&file.getFileSize()>0){
				accept = FileTypeUtil.isFileTypeEqual(file);
			}
			if(accept){
				if ("1".equals(flag)) {
					insert(vo, file, dao);
					/*如果是新增，添加新增标识，HtmlFileListForm中判断有标识会跳到最后一页 guodd 2018-12-12*/
					this.formHM.put("isAdd", "true");
				} else if ("0".equals(flag)) {
					/**
					 * 点编辑链接后，进行保存处理
					 */
					cat.debug("update_htmlfilevo=" + vo.toString());
					update(vo, dao, file);
				} else {

				}
			} else {
				throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("error.fileuploaderror")));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 修改
	 * 
	 * @param vo
	 * @param dao
	 * @throws GeneralException
	 */
//	private void update(RecordVo vo, ContentDAO dao, FormFile file)
//			throws GeneralException {
//		try {
//			if (file != null && file.getFileSize()> 0)
//			{
//				String fname = file.getFileName();
//				int indexInt = fname.lastIndexOf(".");
//				String ext = fname.substring(indexInt + 1, fname.length());
//				vo.setString("ext", ext);				
//				switch (Sql_switcher.searchDbServer()) {
//				case Constant.ORACEL:
//					Blob blob = getOracleBlob(vo, file);
//					 vo.setObject("content",blob);
//					break;
//				default:
//					byte[] data = file.getFileData();
//					vo.setObject("content", data);
//					break;
//				}
//			}
//			
//			dao.updateValueObject(vo);
//		} catch (Exception sqle) {
//			sqle.printStackTrace();
//			throw GeneralExceptionHandler.Handle(sqle);
//		}
//	}

	/**
	 * 修改
	 * 
	 * @param vo
	 * @param dao
	 * @throws GeneralException
	 */
	private void update(RecordVo vo, ContentDAO dao, FormFile file)
			throws GeneralException {
		try {
			if (file != null && file.getFileSize()> 0)
			{
				boolean isupd = false;
				//获取数据库中的fileid
				String contentid = vo.getString("contentid");
				String sql = " select fileid from resource_list where contentid = ? ";
				ArrayList values = new ArrayList();
				values.add(contentid);
				
				this.frowset = dao.search(sql,values);
				String fileid = "";
				if(this.frowset.next()) {
					if(this.frowset.getObject("fileid") != null ) {
						fileid = this.frowset.getString("fileid");
					}
				}
				//20/3/4 xus vfs改造
				//用户名
				String username = this.getUserView().getUserName();
				//文件类型：文档
				VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
				//所属模块:系统管理
				VfsModulesEnum vfsModulesEnum = VfsModulesEnum.XT;
				//文件所属类型
				VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
				//guidkey
				String CategoryGuidKey = "";
				//文件流
				file.getInputStream();
				//文件名称
				String fileName = file.getFileName();
				//文件扩展标识
				String filetag = "";
				
				boolean isTempFile = false;
				
				if(StringUtils.isBlank(fileid)) {
					//新增
					fileid = VfsService.addFile(username, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, CategoryGuidKey, file.getInputStream(), fileName, filetag, isTempFile);
				}else {
					//更新
					fileid = VfsService.saveFile(username, fileid, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, CategoryGuidKey, file.getInputStream(), fileName, filetag, isTempFile);
				}
				vo.setObject("fileid", fileid);
				
				int indexInt = fileName.lastIndexOf(".");
				String ext = fileName.substring(indexInt + 1, fileName.length());
				vo.setString("ext", ext);				
			}
			dao.updateValueObject(vo);
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}
	
	/**
	 * 新增记录
	 * 
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
//	private void insert(RecordVo vo, FormFile file, ContentDAO dao)
//			throws GeneralException {
//		String unitcode = (String)this.getFormHM().get("unitcode");//得到所属单位
//		boolean bflag = true;
//		if (file == null || file.getFileSize() == 0)
//			bflag = false;
//		try {
//			// id号码操作
//			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
//			String contentid = idg.getId("resource_list.id");
//			vo.setInt("contentid", Integer.parseInt(contentid));
//			/** 表格和办事流程分类号 */
//			String fileflag = (String) this.getFormHM().get("fileflag");
//			vo.setInt("id", Integer.parseInt(fileflag));
//			vo.setString("createdate", DateStyle.getSystemTime());
//////////////////////增加一新列：所属单位（unitcode）  郭峰修改//////////////////////
//			if(!unitcode.equals(""))
//			 vo.setString("unitcode",unitcode);
//			/////////////////////////////////////////////////////////////////////
//
//			cat.debug("insert_resource_vo=" + vo.toString());
//			if (bflag) {
//				String fname = file.getFileName();
//				int indexInt = fname.lastIndexOf(".");
//				String ext = fname.substring(indexInt + 1, fname.length());
//				vo.setString("ext", ext);
//				/** 允许下载标识 */
//				// vo.setInt("status",0);
//				/** blob字段保存,数据库中差异 */
//				switch (Sql_switcher.searchDbServer()) {
//				case Constant.ORACEL:
//					// Blob blob = getOracleBlob(vo, file);
//					// vo.setObject("thefile",blob);
//					break;
//				default:
//					byte[] data = file.getFileData();
//					vo.setObject("content", data);
//					break;
//				}
//				cat.debug("insert_resource_vo=" + vo.toString());
//				dao.addValueObject(vo);
//				/** oracle数据库保存blob字段内容目前只能采此方法 */
//				if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
//					RecordVo updatevo = new RecordVo("resource_list");
//					updatevo.setInt("contentid", Integer.parseInt(contentid));
//					Blob blob = getOracleBlob(updatevo, file);
//					updatevo.setObject("content", blob);
//					dao.updateValueObject(updatevo);
//				}
//			} else {
//				cat.debug("insert_resource_vo=" + vo.toString());
//				dao.addValueObject(vo);
//			}
//		} catch (Exception ee) {
//			ee.printStackTrace();
//			Exception e = new Exception("上传文件不能为空！");
//			throw GeneralExceptionHandler.Handle(e);
//		}
//	}
	
	/**
	 * 新增记录
	 * 
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void insert(RecordVo vo, FormFile file, ContentDAO dao)
			throws GeneralException {
		String unitcode = (String)this.getFormHM().get("unitcode");//得到所属单位
		boolean bflag = true;
		if (file == null || file.getFileSize() == 0)
			bflag = false;
		try {
			// id号码操作
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String contentid = idg.getId("resource_list.id");
			vo.setInt("contentid", Integer.parseInt(contentid));
			/** 表格和办事流程分类号 */
			String fileflag = (String) this.getFormHM().get("fileflag");
			vo.setInt("id", Integer.parseInt(fileflag));
			vo.setString("createdate", DateStyle.getSystemTime());
////////////////////增加一新列：所属单位（unitcode）  郭峰修改//////////////////////
			if(!"".equals(unitcode))
			 vo.setString("unitcode",unitcode);
			/////////////////////////////////////////////////////////////////////

			cat.debug("insert_resource_vo=" + vo.toString());
			if (bflag) {
				//xus vfs文件改造 
				//用户名
				String username = this.getUserView().getUserName();
				//文件类型：文档
				VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
				//所属模块:系统管理
				VfsModulesEnum vfsModulesEnum = VfsModulesEnum.XT;
				//文件所属类型
				VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
				//guidkey
				String CategoryGuidKey = "";
				//文件流
				file.getInputStream();
				//文件名称
				String fileName = file.getFileName();
				//文件扩展标识
				String filetag = "";
				
				boolean isTempFile = false;
				
				String fileid = VfsService.addFile(username, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, CategoryGuidKey, file.getInputStream(), fileName, filetag, isTempFile);
				vo.setObject("fileid", fileid);
				
				int indexInt = fileName.lastIndexOf(".");
				String ext = fileName.substring(indexInt + 1, fileName.length());
				vo.setString("ext", ext);
				
			}
			cat.debug("insert_resource_vo=" + vo.toString());
			dao.addValueObject(vo);
		} catch (Exception ee) {
			ee.printStackTrace();
			Exception e = new Exception("上传文件不能为空！");
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 
	 * @param vo
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(RecordVo vo, FormFile file)
			throws FileNotFoundException, IOException {
		StringBuffer strSearch = new StringBuffer();
		strSearch.append("select content from resource_list where contentid='");
		strSearch.append(vo.getString("contentid"));
		strSearch.append("' FOR UPDATE");

		StringBuffer strInsert = new StringBuffer();
		strInsert
				.append("update  resource_list set content=EMPTY_BLOB() where contentid='");
		strInsert.append(vo.getString("contentid"));
		strInsert.append("'");
		OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
		Blob blob = null;
		InputStream stream = null;
		try {
			stream = file.getInputStream();
			blob = blobutils.readBlob(strSearch.toString(), strInsert
					.toString(), file.getInputStream()); // readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		}finally{
			PubFunc.closeIoResource(stream);
		}
		return blob;
	}

}
