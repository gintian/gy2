package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.struts.upload.FormFile;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 操作
 * <p>
 * Title:AddLawBaseFileTrans.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Nov 13, 2006 10:39:22 AM
 * </p>
 * 
 * @author sunxin
 * @version 1.0
 * 
 */
public class AddLawBaseFileTrans extends IBusiness {
    String digest = "";

    public AddLawBaseFileTrans() {
        super();
    }

    public void execute() throws GeneralException {

        ArrayList lawBaseFileList = (ArrayList) this.getFormHM().get("lawBaseFileList");
        // //////////////////////////////////////////////
        // 维护手动增加的字段 即 fieldsExcepSystem
        HashMap<String, String> systemMap = new HashMap<String, String>();// 系统项
        systemMap.put("name", "1");
        systemMap.put("title", "1");
        systemMap.put("content_type", "1");
        systemMap.put("type", "1");
        systemMap.put("valid", "1");
        systemMap.put("note_num", "1");
        systemMap.put("issue_org", "1");
        systemMap.put("notes", "1");
        systemMap.put("issue_date", "1");
        systemMap.put("implement_date", "1");
        systemMap.put("valid_date", "1");
        systemMap.put("viewcount", "1");
        systemMap.put("b0110", "1");
        systemMap.put("ext", "1");
        systemMap.put("originalext", "1");

        try {
            ArrayList fieldsExcepSystem = new ArrayList();
            for (int i = 0; i < lawBaseFileList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) lawBaseFileList.get(i);
                String itemid = (String) bean.get("itemid");
                if (systemMap.get(itemid) == null) {
                    fieldsExcepSystem.add(bean);
                }
            }
            // /////////////////////////////////////////////////////////////
            String field_str_item = (String) this.getFormHM().get("field_str_item");
            digest = (String) this.getFormHM().get("digest");
            RecordVo vo = (RecordVo) this.getFormHM().get("lawFileTb");
            // System.out.println("=====" + vo.getString("name")) ;
            if (vo == null)
                return;
            if ((vo.getString("title") == null || vo.getString("title").length() <= 0) && "".equals(field_str_item))// 如果是老程序
                return;
            vo = keyWordFilter(vo);
            FormFile file = (FormFile) this.getFormHM().get("file");
            if (file != null && file.getFileSize() > 0 && !FileTypeUtil.isFileTypeEqual(file))
                throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("error.fileuploaderror")));

            FormFile manuscript = (FormFile) this.getFormHM().get("manuscript");
            if (manuscript != null && manuscript.getFileSize() > 0 && !FileTypeUtil.isFileTypeEqual(manuscript))
                throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("error.fileuploaderror")));

            String flag = (String) this.getFormHM().get("flag");
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String isInsert = (String) hm.get("isInsert");
            String file_id = (String) hm.get("file_id");
            hm.remove("isInsert");
            hm.remove("file_id");

            if (isInsert == null)
                isInsert = "0";
            /** 新增 */
            if ("1".equals(flag)) {
                insert(vo, file, manuscript, lawBaseFileList, isInsert, file_id, fieldsExcepSystem);
            }
            /** 编辑 */
            if ("0".equals(flag)) {
                update(vo, file, manuscript, lawBaseFileList, fieldsExcepSystem);
            }
            /** 清空 */
            vo.removeValues();
        } catch(GeneralException e) {
        	throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void update(RecordVo vo, FormFile file, FormFile manuscript, ArrayList lawBaseFileList, ArrayList fieldsExcepSystem) throws GeneralException {
        String feng = vo.getString("file_id");
        StringBuffer strsql = new StringBuffer();
        PreparedStatement pstmt = null;
        boolean bflag = true;
        boolean mflag = true;
        if (file == null || file.getFileSize() == 0)
            bflag = false;
        if (manuscript == null || manuscript.getFileSize() == 0)
            mflag = false;
        DateStyle first_date = (DateStyle) this.getFormHM().get("first_date");
        DateStyle second_date = (DateStyle) this.getFormHM().get("second_date");
        DateStyle third_date = (DateStyle) this.getFormHM().get("third_date");
        Date first = null;
        Date second = null;
        Date third = null;
        String field_id = "";
        DbSecurityImpl dbS = new DbSecurityImpl();
        
        if (second_date != null && third_date != null && !"".equals(second_date.getDataStringToDate().trim()) && !"".equals(third_date.getDataStringToDate().trim())) {
            third = DateStyle.parseDate(third_date.getDataStringToDate());
            second = DateStyle.parseDate(second_date.getDataStringToDate());
            if (third.before(second)) {
                Exception e = new Exception("失效日期不能小于实施日期！");
                throw GeneralExceptionHandler.Handle(e);
            }
        }
        IndexWriter writer = null;
        InputStream in = null;
        try {
            if (bflag && mflag)
                strsql.append("update law_base_file set name= ?,title= ?,type= ?,content_type= ?,valid= ?,note_num= ?,issue_org= ?,notes= ?,"
                        		+ "issue_date= ?,implement_date= ?,valid_date= ?,base_id= ?, b0110=?,keywords=?, ext= ?, fileid=?,"
                        		+ " digest=?, originalfileid=?, originalext=?"
                        		+ " where  file_id= ? ");
            else if (mflag)
                strsql.append("update law_base_file set name= ?,title= ?,type= ?,content_type= ?,valid= ?,note_num= ?,issue_org= ?,notes= ?,"
                        		+ "issue_date= ?,implement_date= ?,valid_date= ?,base_id= ?, b0110=?,keywords=?, originalext=?, "
                        		+ "originalfileid=?, digest=?  where  file_id= ? ");
            else if (bflag)
                strsql.append("update law_base_file set name= ?,title= ?,type= ?,content_type= ?,valid= ?,note_num= ?,issue_org= ?,notes= ?,"
                		+ "issue_date= ?,implement_date= ?,valid_date= ?,base_id= ?, b0110=?,keywords=?, ext= ?, fileid=?,"
                		+ " digest=?  where  file_id= ? ");
            else
                strsql.append("update law_base_file set name= ?,title= ?,type= ?,content_type= ?,valid= ?,note_num= ?,issue_org= ?,notes= ?,issue_date= ?,implement_date= ?,valid_date= ?,base_id= ?, b0110=?,keywords=?,  digest=? where  file_id= ? ");
            
            dbS.open(this.getFrameconn(), strsql.toString());
            pstmt = this.getFrameconn().prepareStatement(strsql.toString());
            
            if (bflag && mflag) {
                if (vo.getString("name").length() > 0) {
                	
                    pstmt.setString(1, vo.getString("name"));
                   
                    pstmt.setString(17, digest);
                } else {
                    String filename = file.getFileName();
                    int indexint = filename.lastIndexOf(".");
                    pstmt.setString(1, filename.substring(0, indexint));
                    pstmt.setString(17, digest);
                }
            } else if (mflag) {
                pstmt.setString(1, vo.getString("name"));
                pstmt.setString(17, digest);
            } else if (bflag) {
                if (vo.getString("name").length() > 0) {
                    pstmt.setString(1, vo.getString("name"));
                    pstmt.setString(17, digest);
                } else {
                    String filename = file.getFileName();
                    int indexint = filename.lastIndexOf(".");
                    pstmt.setString(1, filename.substring(0, indexint));
                    pstmt.setString(17, digest);
                }
            } else {
                pstmt.setString(1, vo.getString("name"));
                pstmt.setString(15, digest);
            }
            pstmt.setString(2, vo.getString("title"));
            pstmt.setString(3, vo.getString("type"));
            pstmt.setString(4, vo.getString("content_type"));
            if ("5".equalsIgnoreCase(this.getFormHM().get("basetype").toString()))
                pstmt.setString(5, "1");
            else
                pstmt.setString(5, vo.getString("valid"));
            pstmt.setString(6, vo.getString("note_num"));
            pstmt.setString(7, vo.getString("issue_org"));
            pstmt.setString(8, vo.getString("notes"));

            if (!"".equals(first_date.getDataStringToDate().trim())) {
                pstmt.setDate(9, DateUtils.getSqlDate(first_date.getDataStringToDate(), "yyyy-MM-dd"));
            } else {
                pstmt.setDate(9, null);
            }
            if (!"".equals(second_date.getDataStringToDate().trim())) {
                pstmt.setDate(10, DateUtils.getSqlDate(second_date.getDataStringToDate(), "yyyy-MM-dd"));
            } else {
                pstmt.setDate(10, null);
            }
            if (!"".equals(third_date.getDataStringToDate().trim())) {
                pstmt.setDate(11, DateUtils.getSqlDate(third_date.getDataStringToDate(), "yyyy-MM-dd"));
            } else {
                pstmt.setDate(11, null);
            }
            pstmt.setString(12, vo.getString("base_id"));
            String b0110 = (String) this.getFormHM().get("transfercodeitemid");
            if (b0110 == null || "".equalsIgnoreCase(b0110))
                pstmt.setString(13, null);
            else
                pstmt.setString(13, b0110);
            pstmt.setString(14, vo.getString("keywords"));
            //2014.10.29 xxd 当文件为空时此方法报错
            if(file!=null&&"".equals(file))
            {
            	cat.debug("file size=" + file.getFileSize());
            }
            String ext = "";
            VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
			String userName = this.userView.getUserName();
			VfsModulesEnum vfsModulesEnum = VfsModulesEnum.WD;
			VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
            if (bflag && mflag) {
                String fname = file.getFileName();
                int indexInt = fname.lastIndexOf(".");
                ext = fname.substring(indexInt + 1, fname.length());
                pstmt.setString(15, ext);
                in = file.getInputStream();
                String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                		"", in, fname, "", false);
                pstmt.setString(16, fieldId);
                String mname = manuscript.getFileName();
                indexInt = mname.lastIndexOf(".");
                ext = mname.substring(indexInt + 1, mname.length());
                pstmt.setString(19, ext);
                in = manuscript.getInputStream();
                String manuscriptFieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                		"", in, fname, "", false);
                pstmt.setString(18, manuscriptFieldId);
                pstmt.setString(20, vo.getString("file_id"));
            } else if (mflag) {
                String fname = manuscript.getFileName();
                int indexInt = fname.lastIndexOf(".");
                ext = fname.substring(indexInt + 1, fname.length());
                pstmt.setString(15, ext);
                in = manuscript.getInputStream();
                String manuscriptFieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                		"", in, fname, "", false);
                pstmt.setObject(16, manuscriptFieldId);
                /** blob字段保存,数据库中差异 */
                pstmt.setInt(18, Integer.parseInt(vo.getString("file_id")));
            } else if (bflag) {
                String fname = file.getFileName();
                int indexInt = fname.lastIndexOf(".");
                ext = fname.substring(indexInt + 1, fname.length());
                pstmt.setString(15, ext);
                in = file.getInputStream();
                String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                		"", in, fname, "", false);
                pstmt.setString(16, fieldId);
                pstmt.setString(18, vo.getString("file_id"));
            } else {
                pstmt.setString(16, vo.getString("file_id"));
            }

            CommonBusiness.addIndex(vo.getString("file_id"), digest);
            field_id = vo.getString("file_id");
            if ("txt".equalsIgnoreCase(ext.trim()) || "html".equalsIgnoreCase(ext.trim()) || "htm".equalsIgnoreCase(ext.trim()) || "doc".equalsIgnoreCase(ext.trim())
                    || "xls".equalsIgnoreCase(ext.trim()) || "xlsx".equalsIgnoreCase(ext.trim())) {

                // 因lucene不支持直接修改索引，所以当修改记录操作发生时先删除原索引再添加新记录索引
                String indexPath = LawDirectory.getLawbaseDir();
                if (IndexReader.indexExists(indexPath)) {
                    IndexReader ir = null;
                    try {
                        ir = IndexReader.open(indexPath);
                        ir.delete(new Term("id", vo.getString("file_id")));
                    } catch (Exception err) {
                        err.printStackTrace();
                    } finally {
                        try {
                            ir.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    File f = new File(indexPath + "\\segments");
                    // 如果文件存在追加索引如果文件不存在创建索引
                    if (f.exists()) {
                        writer = new IndexWriter(indexPath, new ChineseAnalyzer(), false);
                    } else {
                        writer = new IndexWriter(indexPath, new ChineseAnalyzer(), true);
                    }
                    Document doc = new Document();
                    doc.add(Field.Keyword("id", field_id));
                    doc.add(Field.Keyword("base_id", vo.getString("base_id")));
                    in = file.getInputStream();
                    if ("doc".equals(ext.trim().toLowerCase())) {
                        try {
                            doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.wordToText(in))));
                        } catch (Exception e) {
                            Exception ex = new Exception("您的word文件有问题，请尽量使用纯文字，不要带图片或者乱码！建议您新建word，将原有的内容重新拷贝进去");
                            throw GeneralExceptionHandler.Handle(ex);
                        }
                    }
                    if ("xls".equals(ext.trim().toLowerCase()) || "xlsx".equals(ext.trim().toLowerCase())) {
                        doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.excelToText(in))));
                    }
                    if ("txt".equals(ext.trim().toLowerCase()) || "html".equals(ext.trim().toLowerCase())) {
                        doc.add(Field.Text("body", (Reader) new InputStreamReader(in)));
                    }
                    // 将文档写入索引
                    writer.addDocument(doc);
                    // 索引优化
                    // writer.optimize();
                    // 关闭写索引器
                    // writer.close();
                    PubFunc.closeResource(doc);
                }
            }
            pstmt.executeUpdate();
            if (field_id != null && field_id.length() > 0) {
                UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
                user_bo.saveResource(field_id, this.userView, IResourceConstant.LAWRULE_FILE);
            }
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            if (fieldsExcepSystem.size() > 0) {
                RecordVo extvo = new RecordVo("law_base_file");
                extvo.setString("file_id", feng);
                extvo = dao.findByPrimaryKey(extvo);
                for (int j = 0; j < fieldsExcepSystem.size(); j++) {
                    LazyDynaBean bean = (LazyDynaBean) fieldsExcepSystem.get(j);
                    String itemid = (String) bean.get("itemid");
                    String itemtype = (String) bean.get("itemtype");
                    int decWidth = Integer.parseInt((String) bean.get("decWidth"));
                    String value = (String) bean.get("value");
                    if (value != null && !"".equals(value)) {
                        if ("D".equalsIgnoreCase(itemtype)) {
                            extvo.setDate(itemid, value);
                        } else if ("N".equalsIgnoreCase(itemtype)) {
                            if (decWidth == 0)
                                extvo.setInt(itemid, Integer.parseInt(value));
                            else
                                extvo.setDouble(itemid, Double.parseDouble(value));
                        } else {
                            extvo.setString(itemid.toLowerCase(), value);
                        }
                    }
                }
                if (extvo != null)
                    extvo = keyWordFilter(extvo);
                dao.updateValueObject(extvo);
            }

        } catch (Exception ee) {
            ee.printStackTrace();
            throw GeneralExceptionHandler.Handle(ee);
        } finally {
        	try {
        		// 关闭Wallet
        		dbS.close(this.getFrameconn());
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	PubFunc.closeResource(pstmt);
        	PubFunc.closeResource(writer);					
			PubFunc.closeResource(in);
        }
    }

    /**
     * 新增法规文件
     * 
     * @param vo
     * @param file
     * @throws GeneralException
     */
    private void insert(RecordVo vo, FormFile file, FormFile manuscript, ArrayList lawBaseFileList, String isInsert, String file_id, ArrayList fieldsExcepSystem) throws GeneralException {
        boolean bflag = true;
        boolean mflag = true;
        if (file == null || file.getFileSize() == 0)
            bflag = false;
        if (manuscript == null || manuscript.getFileSize() == 0)
            mflag = false;
        DateStyle first_date = (DateStyle) this.getFormHM().get("first_date");
        DateStyle second_date = (DateStyle) this.getFormHM().get("second_date");
        DateStyle third_date = (DateStyle) this.getFormHM().get("third_date");
        String note_num = (String) this.getFormHM().get("note_num");
        String b0110 = (String) this.getFormHM().get("transfercodeitemid");
        if (note_num == null)
            note_num = "";
        this.getFormHM().put("note_num", note_num);
        Date first = null;
        Date second = null;
        Date third = null;
        // 首发需要，改掉
        if (second_date != null && third_date != null && !"".equals(second_date.getDataStringToDate().trim()) && !"".equals(third_date.getDataStringToDate().trim())) {
            third = DateStyle.parseDate(third_date.getDataStringToDate());
            second = DateStyle.parseDate(second_date.getDataStringToDate());
            if (third.before(second)) {
                Exception e = new Exception("失效日期不能小于实施日期！");
                throw GeneralExceptionHandler.Handle(e);
            }
        }
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String filename = (String) this.getFormHM().get("name");
        IndexWriter writer = null;
        InputStream in = null;
        try {
            IDGenerator idg = new IDGenerator(2, this.getFrameconn());
            String id = idg.getId("law_base_file.id");
            id = compareForIDFOrFactory(id);
            String base_id = this.getFormHM().get("base_id").toString();
            base_id = PubFunc.decrypt(SafeCode.decode(base_id));
            file_id = PubFunc.decrypt(SafeCode.decode(file_id));
            String sql = "";
            if ("0".equals(isInsert)) {
                sql = "select max(fileorder) as fileorder from law_base_file where base_id ='" + base_id + "'";
            } else {
                sql = "select fileorder as fileorder from law_base_file where file_id ='" + file_id + "'";
            }
            this.frowset = dao.search(sql);
            String fileorderid = "";
            if (this.frowset.next())
                fileorderid = this.frowset.getString("fileorder") == null ? "" : this.frowset.getString("fileorder");
            if ("".equalsIgnoreCase(fileorderid.trim()))
                fileorderid = "0";
            if ("0".equals(isInsert)) {
                vo.setString("fileorder", (Integer.parseInt(fileorderid) + 1) + "");
            } else {

                vo.setString("fileorder", (Integer.parseInt(fileorderid) + 1) + "");
            }

            String digest = (String) this.getFormHM().get("digest");
            vo.setString("digest", digest);
            vo.setString("file_id", id);
            vo.setString("base_id", base_id);
            vo.setDate("issue_date", first_date.getDataStringToDate());
            vo.setDate("implement_date", second_date.getDataStringToDate());
            vo.setDate("valid_date", third_date.getDataStringToDate());
            if ("5".equalsIgnoreCase(this.getFormHM().get("basetype").toString()))
                vo.setString("valid", "1");
            if (b0110 == null || "".equalsIgnoreCase(b0110))
                vo.setString("b0110", null);
            else
                vo.setString("b0110", b0110);
            
            VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
			String userName = this.userView.getUserName();
			VfsModulesEnum vfsModulesEnum = VfsModulesEnum.WD;
			VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
            
            if (bflag) {
                String fname = file.getFileName();
                int indexInt = fname.lastIndexOf(".");
                String ext = fname.substring(indexInt + 1, fname.length());
                vo.setString("ext", ext);
                if ("".equals(filename.trim()) || "null".equals(filename)) {
                    vo.setString("name", fname.substring(0, indexInt));
                } else {
                    vo.setString("name", filename);
                }
                String path = "";
                File f = null;
                Document doc = null;
                in = file.getInputStream();
                String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                		"", in, fname, "", false);
                vo.setString("fileid", fieldId);
                
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
                doc.add(Field.Keyword("id", id));
                doc.add(Field.Keyword("base_id", base_id));
                
                in = file.getInputStream();
                if ("doc".equals(ext.trim().toLowerCase())) {
                	try {
                		doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.wordToText(in))));
                	} catch (Exception e) {
                		Exception ex = new Exception("您的word文件有问题，请尽量使用纯文字，不要带图片或者乱码！建议您新建word，将原有的内容重新拷贝进去");
                		throw GeneralExceptionHandler.Handle(ex);
                	}
                	
                }
                if ("xls".equals(ext.trim().toLowerCase()) || "xlsx".equals(ext.trim().toLowerCase())) {
                	doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.excelToText(in))));
                }
                if ("txt".equals(ext.trim().toLowerCase()) || "html".equals(ext.trim().toLowerCase())) {
                	doc.add(Field.Text("body", (Reader) new InputStreamReader(in)));
                }
                // 将文档写入索引
                writer.addDocument(doc);
                // 索引优化
                // writer.optimize();
                // 关闭写索引器
                writer.close();
            }
            
            if (mflag) {
                String fname = manuscript.getFileName();
                int indexInt = fname.lastIndexOf(".");
                String ext = fname.substring(indexInt + 1, fname.length());
                vo.setString("originalext", ext);
                String path = "";

                File f = null;
                Document doc = null;
                in = manuscript.getInputStream();
                String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                		"", in, fname, "", false);
                vo.setString("originalfileid", fieldId);
                
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
                doc.add(Field.Keyword("id", id));
                doc.add(Field.Keyword("base_id", base_id));
                
                if ("doc".equals(ext.trim().toLowerCase())) {
                	doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.wordToText(manuscript.getInputStream()))));
                	
                }
                if ("xls".equals(ext.trim().toLowerCase()) || "xlsx".equals(ext.trim().toLowerCase())) {
                	doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.excelToText(manuscript.getInputStream()))));
                }
                if ("txt".equals(ext.trim().toLowerCase()) || "html".equals(ext.trim().toLowerCase())) {
                	doc.add(Field.Text("body", (Reader) new InputStreamReader(manuscript.getInputStream())));
                }
                // 将文档写入索引
                writer.addDocument(doc);
                // 索引优化
                // writer.optimize();
                // 关闭写索引器
                writer.close();
            }

            for (int j = 0; j < fieldsExcepSystem.size(); j++) {
                LazyDynaBean bean = (LazyDynaBean) fieldsExcepSystem.get(j);
                String itemid = (String) bean.get("itemid");
                String itemtype = (String) bean.get("itemtype");
                int decWidth = Integer.parseInt((String) bean.get("decWidth"));
                String value = (String) bean.get("value");
                if (value != null && !"".equals(value)) {
                    if ("D".equalsIgnoreCase(itemtype)) {
                        vo.setDate(itemid, /* PubFunc.FormatDate( */value/* ) */);
                    } else if ("N".equalsIgnoreCase(itemtype)) {
                        if (decWidth == 0)
                            vo.setInt(itemid, Integer.parseInt(value));
                        else
                            vo.setDouble(itemid, Double.parseDouble(value));
                    } else {
                        vo.setString(itemid.toLowerCase(), value);
                    }
                }

            }
            
            if (vo != null){
                vo = keyWordFilter(vo);
                dao.addValueObject(vo);
            }
            
            CommonBusiness.addIndex(id, digest);
            if (id != null && id.length() > 0) {
                UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
                user_bo.saveResource(id, this.userView, IResourceConstant.LAWRULE_FILE);
            }
            StringBuffer updateSb = new StringBuffer();
            updateSb.append("update law_base_file set fileorder=fileorder+2 where fileorder>" + fileorderid + " and file_id<>" + id + " and fileorder is not null");
            dao.update(updateSb.toString());
        } catch (Exception ee) {
            ee.printStackTrace();
            throw GeneralExceptionHandler.Handle(ee);
        } finally {
        	PubFunc.closeIoResource(in);
            try {
                if (writer != null)
                    writer.close();
            } catch (Exception e) {
                // e.printStackTrace();
            }

        }
    }

    public String compareForIDFOrFactory(String id_factory) {
        StringBuffer sql = new StringBuffer();
        sql.append("select max(file_id) as file_id from law_base_file");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            String max_file_id = "0";
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next()) {
                max_file_id = this.frowset.getString("file_id");
                if (max_file_id == null || max_file_id.length() <= 0)
                    max_file_id = "0";
            }
            int id_factory_int = Integer.parseInt(id_factory);
            int max_file_id_int = Integer.parseInt(max_file_id);
            if (max_file_id_int >= id_factory_int) {
                id_factory_int = max_file_id_int + 1;
                String n_id_factory = id_factory_int + "";
                StringBuffer str_id = new StringBuffer();
                for (int i = 0; i < (id_factory.length() - n_id_factory.length()); i++) {
                    str_id.append("0");
                }
                str_id.append(n_id_factory);
                id_factory = str_id.toString();
                sql = new StringBuffer();
                sql.append("update id_factory set");
                sql.append(" currentid='" + id_factory_int + "'");
                sql.append(" where sequence_name='law_base_file.id'");
                dao.update(sql.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_factory;
    }

    /**
     * 文件上传安全优化
     * 
     * @param vo
     * @return
     */
    private RecordVo keyWordFilter(RecordVo vo) {
        String title = vo.getString("title");
        String name = vo.getString("name");
        String issue_org = vo.getString("issue_org");
        String type = vo.getString("type");
        String notes = vo.getString("notes");
        String content_type = vo.getString("content_type");
        String note_num = vo.getString("note_num");

        vo.setString("title", PubFunc.keyWord_filter(title));
        vo.setString("name", PubFunc.keyWord_filter(name));
        vo.setString("issue_org", PubFunc.keyWord_filter(issue_org));
        vo.setString("type", PubFunc.keyWord_filter(type));
        vo.setString("notes", PubFunc.keyWord_filter(notes));
        vo.setString("content_type", PubFunc.keyWord_filter(content_type));
        vo.setString("note_num", PubFunc.keyWord_filter(note_num));

        return vo;

    }
}
