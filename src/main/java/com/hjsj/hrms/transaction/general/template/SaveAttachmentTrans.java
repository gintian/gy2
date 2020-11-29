package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.param.Sys_Infom_Parameter;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class SaveAttachmentTrans extends IBusiness {

	private Blob getOracleBlob(RecordVo vo, FormFile file) throws FileNotFoundException, IOException {
		StringBuffer strSearch = new StringBuffer();
		strSearch.append("select content from t_wf_file where file_id='");
		strSearch.append(vo.getString("file_id"));
		strSearch.append("' FOR UPDATE");

		StringBuffer strInsert = new StringBuffer();
		strInsert.append("update  t_wf_file set content=EMPTY_BLOB() where file_id='");
		strInsert.append(vo.getString("file_id"));
		strInsert.append("'");
		Connection con = this.getFrameconn();
        Blob blob =null;
        InputStream inputStream=file.getInputStream();
        try{
            OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
            blob = blobutils.readBlob(strSearch.toString(), strInsert
                    .toString(), inputStream);
        }
        finally{
            PubFunc.closeResource(inputStream);   
        }
		return blob;
	}

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String fileName = "";
		String ins_id = (String) this.getFormHM().get("ins_id");
		if (ins_id != null && !"0".equals(ins_id)) {// 如果ins_id不为0,判断ins_id是否在后台存储,否则的话不允许上传
			HashMap cardAttachMap = (HashMap) this.userView.getHm().get("cardAttachMap");
			if (cardAttachMap != null && !cardAttachMap.containsKey(ins_id)) {
				throw new GeneralException(ResourceFactory.getProperty("no_permission_ins_id"));
			}
		}
		String tabid = (String) this.getFormHM().get("tabid");
		// 多媒体分类//多媒体分类来自于前台界面传值绕过了过滤器替换一下
		String mediasortid = (String) this.getFormHM().get("mediasortid");
		mediasortid = PubFunc.hireKeyWord_filter(mediasortid);
		// 过滤完毕
		String infor_type = (String) hm.get("infor_type");
		String objectid = (String) hm.get("objectid");
		String basepre = (String) hm.get("basepre");
		basepre = SafeCode.decode(basepre);
		objectid = SafeCode.decode(objectid);
		String attachmenttype = (String) hm.get("attachmenttype");
		hm.remove("infor_type");
		hm.remove("objectid");
		hm.remove("basepre");
		hm.remove("attachmenttype");
		if ("0".equals(attachmenttype)) {
			mediasortid = null;
		}
		FormFile file = (FormFile) this.getFormHM().get("filecontent");
		RecordVo vo = new RecordVo("t_wf_file");
		vo.setString("ins_id", ins_id);
		vo.setString("tabid", tabid);

		vo.setString("attachmenttype", attachmenttype);
		vo.setString("objectid", objectid);
		if ("1".equals(infor_type)) {
			vo.setString("basepre", basepre);
		}
		else {
			vo.setString("basepre", null);
		}
		vo.setString("filetype", mediasortid);
		if (file.getFileSize() == 0) {
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("lable.resource.upfile.nullError")));
		}
		Sys_Infom_Parameter sys_Infom_Parameter = new Sys_Infom_Parameter(this.getFrameconn(), "INFOM");
		String multimedia_maxsize = sys_Infom_Parameter.getValue(Sys_Infom_Parameter.MULTIMEDIA, "MultimediaMaxSize");
		if ("".equals(multimedia_maxsize)) multimedia_maxsize="0";
		if (multimedia_maxsize != null && !"0".equals(multimedia_maxsize) && !"-1".equals(multimedia_maxsize)) {// 如果限制文件大小，那么大小一定有；如果不进行文件大小限制那么就不走判断！
																												// xcs2014-01-02
			int maxsize = Integer.parseInt(multimedia_maxsize) * 1024;
			if (file.getFileSize() > maxsize) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("lable.resource.upfile.sizeoverError") + maxsize / 1024
						+ ResourceFactory.getProperty("lable.resource.upfile.pre")));
			}
		}
		fileName = file.getFileName();
		int indexint = fileName.lastIndexOf(".");
		if(indexint==-1){  //当文件没有后缀名的时候，页面提示文件后缀名不能为空 20150807 liuzy
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("lable.resource.upfile.extension.nullError")));
		}else{
		   fileName = fileName.substring(0, indexint);
		}
		// if(fileName.getBytes().length>40){
		// byte name [] = fileName.getBytes();
		// byte name2[]= new byte[40];
		// for(int i=0;i<40;i++){
		// name2[i]=name[i];
		// }
		// fileName = new String (name2);
		// }
		try {
			fileName = subStr(fileName, 40);
		}
		catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		vo.setString("name", fileName);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String id = getMaxEitId(this.getFrameconn());
		if (id == null)
			return;
		vo.setString("name", fileName);
		vo.setString("file_id", id);
		int indexInt = file.getFileName().lastIndexOf(".");
		String ext = file.getFileName().substring(indexInt + 1, file.getFileName().length());
		vo.setString("ext", ext);
		String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH.mm.ss");
		Date cur_d = DateUtils.getDate(cur_date, "yyyy-MM-dd HH.mm.ss");
		vo.setString("create_user", userView.getUserName());
		vo.setDate("create_time", cur_d);
		try {
			if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
				vo.setObject("content", file.getFileData());
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		dao.addValueObject(vo);
		// try {
		// addIndex(file_id, file,ext);
		// } catch (Exception e2) {
		// e2.printStackTrace();
		// }
		if ((Sql_switcher.searchDbServer() == Constant.ORACEL || Sql_switcher.searchDbServer() == Constant.DB2)) {
			RecordVo updatevo = new RecordVo("t_wf_file");
			updatevo.setString("file_id", id);
			String str = "update t_wf_file set content=? where file_id =?";
			PreparedStatement ps = null;
			DbSecurityImpl dbS = new DbSecurityImpl();
			try {
				ps = this.getFrameconn().prepareStatement(str);
				dbS.open(this.frameconn, str);
				Blob blob = getOracleBlob(updatevo, file);
				ps.setBlob(1, blob);
				ps.setString(2, id);
				// updatevo.setObject("content", blob);
				// dao.updateValueObject(updatevo);
				ps.executeUpdate();
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}finally{
				try {
					dbS.close(this.frameconn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				PubFunc.closeResource(ps);
			}
		}
		
	}

	private Connection con;
	private String old_wed_all = "ALL";

	/**
	 * 
	 * @param is
	 *            excel格式输入流
	 * @return 返回文本格式输入流
	 */
	public static InputStream excelToText(InputStream is) {
		StringBuffer sb = new StringBuffer("");
		try(Workbook workbookr = WorkbookFactory.create(is);){

			// HSSFWorkbook workbookr = new HSSFWorkbook(is);
			for (int i = 0; i < workbookr.getNumberOfSheets(); i++) {
				Sheet sheet0 = workbookr.getSheetAt(i);
				Iterator rows = sheet0.rowIterator();
				while (rows.hasNext()) {
					Row hssfrow = (Row) rows.next();
					int count = hssfrow.getPhysicalNumberOfCells();
					for (short j = 0; j < count; j++) {
						Cell cell = (Cell) hssfrow.getCell(j);
						if (cell == null)
							continue;
						if (cell.getCellType() == Cell.CELL_TYPE_STRING)
							sb.append(cell.getStringCellValue());
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
							sb.append(String.valueOf(cell.getNumericCellValue()));
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ByteArrayInputStream(sb.toString().getBytes());
	}

	/**
	 * @param is
	 *            word格式输入流
	 * @return 文本格式输入流
	 */
	public static InputStream wordToText(InputStream is) {
		ByteArrayInputStream byte_input_stream = null;
		try {
			// saveToFile(is, "c:\\1.doc");
			// OPCPackage opcPackage = POIXMLDocument.openPackage(tmpfilepath);
			// POIXMLTextExtractor extractor = new
			// XWPFWordExtractor(opcPackage);
			HWPFDocument doc = new HWPFDocument(is);
			Range range = doc.getRange();
			// System.out.println("--->"+range.text().length());
			byte_input_stream = new ByteArrayInputStream(range.text().getBytes());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return byte_input_stream;
	}

	public synchronized static void addIndex(String file_id, FormFile file, String ext) {
		String path = "";
		IndexWriter writer = null;
		File f = null;
		Document doc = null;
		InputStream stream = null;
		try {
			path = getGeneralFileDir();
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
			}
			else {
				writer = new IndexWriter(path, new ChineseAnalyzer(), true);
			}
			doc = new Document();
			doc.add(Field.Keyword("id", file_id));
			doc.add(Field.Keyword("base_id", file_id));
			stream = file.getInputStream();
			if ("doc".equals(ext.trim().toLowerCase())) {
				doc.add(Field.Text("body", (Reader) new InputStreamReader(wordToText(stream))));
			}
			else if ("xls".equals(ext.trim().toLowerCase()) || "xlsx".equals(ext.trim().toLowerCase())) {
				doc.add(Field.Text("body", (Reader) new InputStreamReader(excelToText(stream))));
			}
			else if ("txt".equals(ext.trim().toLowerCase()) || "html".equals(ext.trim().toLowerCase())) {
				doc.add(Field.Text("body", (Reader) new InputStreamReader(stream)));
			}
			else {
				doc.add(Field.Text("body", (Reader) new InputStreamReader(stream)));
			}

			// 将文档写入索引
			writer.addDocument(doc);
			// 索引优化
			// writer.optimize();
			// 关闭写索引器

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
				if(stream != null)
				PubFunc.closeIoResource(stream);
			}
			catch (Exception e) {

			}
		}
	}

	public synchronized static void addIndex(String file_id, String content) {
		String path = "";
		IndexWriter writer = null;
		File f = null;
		Document doc = null;
		try {
			path = getGeneralFileDir();
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
			}
			else {
				writer = new IndexWriter(path, new ChineseAnalyzer(), true);
			}
			doc = new Document();
			doc.add(Field.Keyword("id", file_id));
			doc.add(Field.Keyword("base_id", file_id));
			doc.add(Field.Text("body", content));

			// 将文档写入索引
			writer.addDocument(doc);
			// 索引优化
			// writer.optimize();
			// 关闭写索引器
			writer.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到附件表的主键值
	 * 
	 * @param conn
	 * @return
	 */
	public String getMaxEitId(Connection conn) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from id_factory where sequence_name='t_wf_file.file_id'");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sql.toString());
			if (!rs.next()) {
				StringBuffer insertSQL = new StringBuffer();
				insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue, auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
				insertSQL.append(" values ('t_wf_file.file_id', '附件号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
				ArrayList list = new ArrayList();
				dao.insert(insertSQL.toString(), list);
			}
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String file_id = idg.getId("t_wf_file.file_id");
			return file_id;
		}
		catch (Exception e) {
			return null;
		}

	}

	public static String getGeneralFileDir() {
		String tempDirName = System.getProperty("java.io.tmpdir");
		String lawDir = "generalfile";
		String lawbase = tempDirName + File.separator + lawDir;

		boolean isCorrect = false;
		try {
			if (tempDirName == null) {
				throw new RuntimeException("Temporary directory system property (java.io.tmpdir) is null.");
			}
			File tempDir = new File(tempDirName);
			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}
			String[] fileList = tempDir.list(); // 目录下所有文件及目录
			for (int i = 0; i < fileList.length; i++) {
				String fileName = fileList[i];
				if (fileName.equalsIgnoreCase(lawDir)) {
					isCorrect = true;
					break;
				}
			}
			if (!isCorrect) {
				File tempLawDir = new File(lawbase);
				tempLawDir.mkdirs();
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return lawbase;
	}

	public String subStr(String str, int subSLength) throws UnsupportedEncodingException {
		if (str == null)
			return "";
		else {
			int tempSubLength = subSLength;// 截取字节数
			String subStr = str.substring(0, str.length() < subSLength ? str.length() : subSLength);// 截取的子串
			int subStrByetsL = subStr.getBytes("GBK").length;// 截取子串的字节长度
			// 说明截取的字符串中包含有汉字
			while (subStrByetsL > tempSubLength) {
				int subSLengthTemp = --subSLength;
				subStr = str.substring(0, subSLengthTemp > str.length() ? str.length() : subSLengthTemp);
				subStrByetsL = subStr.getBytes("GBK").length;
			}
			return subStr;
		}
	}
}
