package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.struts.upload.FormFile;

import java.io.*;
import java.util.Date;

public class AffixAppendTrans extends IBusiness {

	public void execute() throws GeneralException {
		String fileName = (String) this.getFormHM().get("fileName");
		// 2014.11.7 xxd 文件上传参数过滤
		fileName = PubFunc.hireKeyWord_filter(fileName);
		String file_id = (String) this.getFormHM().get("file_id");
		file_id = PubFunc.decrypt(file_id);
		FormFile file = (FormFile) this.getFormHM().get("content");
		try {
			// xiexd 2014.09.22文件上传验证
			if (!FileTypeUtil.isFileTypeEqual(file)) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
		}
		catch (IOException e3) {
			e3.printStackTrace();
		}
		RecordVo vo = new RecordVo("law_ext_file");
		vo.setString("file_id", file_id);
		if (file.getFileSize() == 0) {
			return;
		}
		
		if (fileName == null || "".equals(fileName)) {
			fileName = file.getFileName();
			int indexint = fileName.lastIndexOf(".");
			fileName = fileName.substring(0, indexint);
			vo.setString("name", fileName);
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		/*
		 * // 参数per_mainbody.id为临时借用。 String id = idg.getId("per_mainbody.id");
		 */
		LawDirectory lawDirectory = new LawDirectory();
		String id = lawDirectory.getMaxEitId(this.getFrameconn());
		String user_name = this.userView.getUserName();
		// 判断是否有关联的自助用户，有则直接插入自助用户名
		if (this.userView.getUserFullName() != null && !"".equals(this.userView.getUserFullName())) {
			user_name = this.userView.getUserFullName();
		}
		else if (this.userView.getA0100() != null && this.getUserView().getDbname() != null && !"".equals(this.userView.getDbname()) && !"".equals(this.userView.getA0100())) {
			// 判断关联信息是否存在当前人员库中
			try {
				// 查询当前人员姓名信息
				String sql_ = "select A0101 from " + this.userView.getDbname() + "A01 where A0100='" + this.userView.getA0100() + "'";
				this.frowset = dao.search(sql_);
				if (this.frowset.next()) {
					// 当人员姓名为空时，则插入业务用户名
					if (this.frowset.getString("A0101") != null && !"".equals(this.frowset.getString("A0101"))) {
						user_name = this.frowset.getString("A0101");
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		InputStream stream = null;
		try {
			String userName = this.userView.getUserName();
			VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
			VfsModulesEnum vfsModulesEnum = VfsModulesEnum.WD;
			VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
			
			vo.setString("name", fileName);
			vo.setString("ext_file_id", id);
			int version = getVersion(fileName);
			vo.setInt("version", version);
			int indexInt = file.getFileName().lastIndexOf(".");
			String ext = file.getFileName().substring(indexInt + 1, file.getFileName().length());
			vo.setString("ext", ext);
			String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH.mm.ss");
			Date cur_d = DateUtils.getDate(cur_date, "yyyy-MM-dd HH.mm.ss");
			vo.setString("create_user", user_name);
			vo.setDate("create_time", cur_d);
			stream = file.getInputStream();
			fileName=fileName+"."+ext;
			String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
					"", stream, fileName, "", false);
			vo.setString("fileid", fieldId);
			dao.addValueObject(vo);
			
			String path = "";
			File f = null;
			Document doc = null;
			IndexWriter writer = null;
			
			path = LawDirectory.getLawbaseDir();
			// 索引默认路径
			writer = null;
			// 索引文件默认文件名segents
			if (path.charAt(path.length() - 1) != '\\') {
				path += "\\";
			}
			
			f = new File(path + "\\segments");
			// 如果文件存在追加索引如果文件不存在创建索引
			if (f.exists()) {
				writer = new IndexWriter(path, new ChineseAnalyzer(), false);
			} else {
				writer = new IndexWriter(path, new ChineseAnalyzer(), true);
			}
			
			doc = new Document();
			doc.add(Field.Keyword("id", file_id));
			doc.add(Field.Keyword("ext_file_id", id));
			
			if ("doc".equals(ext.trim().toLowerCase())) {
				stream = file.getInputStream();
				try {
					doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.wordToText(stream))));
				} catch (Exception e) {
					Exception ex = new Exception("您的word文件有问题，请尽量使用纯文字，不要带图片或者乱码！建议您新建word，将原有的内容重新拷贝进去");
					throw GeneralExceptionHandler.Handle(ex);
				}
				
			}
			
			if ("xls".equals(ext.trim().toLowerCase()) || "xlsx".equals(ext.trim().toLowerCase())) {
				stream = file.getInputStream();
				doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.excelToText(stream))));
			}
			
			if ("txt".equals(ext.trim().toLowerCase()) || "html".equals(ext.trim().toLowerCase())) {
				stream = file.getInputStream();
				doc.add(Field.Text("body", (Reader) new InputStreamReader(stream)));
			}
			// 将文档写入索引
			writer.addDocument(doc);
			// 索引优化
			// writer.optimize();
			// 关闭写索引器
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeIoResource(stream);
		}
	}

	public int getVersion(String fileName) {
		String sql = "select count(name) as version from law_ext_file where name='" + fileName + "'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		int count = 0;
		try {
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				count = this.frowset.getInt("version");
				count = count + 1;

			}
			else {
				count = 1;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
}
