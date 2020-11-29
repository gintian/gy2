package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import javax.sql.RowSet;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Blob;
import java.util.ArrayList;

public class RebuildIndexTrans extends IBusiness {

	public RebuildIndexTrans() {
		super();
		// TODO Auto-generated constructor stub
	}
    private ArrayList extlist=new ArrayList();
	public void execute() throws GeneralException {
		CommonBusiness combus = new CommonBusiness(this.getFrameconn());
		
		String base_id = (String) this.getFormHM().get("a_base_id");
		String orgId = userView.getUserOrgId();
		String basetype = (String)this.getFormHM().get("basetype");
		String catalogTerm = "basetype=" + basetype;
				//+ " and (dir = '-1' or dir = '" + orgId + "' or dir is null)";
		String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
		if(!"false".equals(law_file_priv.trim())){
			if(!this.userView.isSuper_admin()){
						if(orgId==null||orgId.length()<=0)
						{
							catalogTerm=catalogTerm+ " and (dir = '' or dir = '-1' or dir is null)";
							
						}else
						{
							LawDirectory lawDirectory=new LawDirectory();
							String orgsrt=lawDirectory.getOrgStrs(orgId,"UN",this.getFrameconn());
							catalogTerm=catalogTerm+ " and (dir = '' or dir = '-1' or dir in (" + orgsrt
							+ ") or dir is null )";
						}
						
			}
		}
		String sqlText = combus.findLawbaseFile(base_id, orgId, catalogTerm,basetype,this.userView);
		sqlText = sqlText.replaceFirst("content_type", "content,content_type");
		
		sqlText = sqlText.replaceFirst("select \\* ", "select file_id,ext,law_base_file.name,law_base_file.base_id ");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String indexPath =  LawDirectory.getLawbaseDir();
		IndexWriter writer = null;
		InputStream inputstream = null;
		try {
			frowset = dao.search(sqlText);
			while (frowset.next()) {
				RowSet rs = null;
				if(!"false".equals(law_file_priv.trim())){
					if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, frowset.getString("file_id")))
					continue;
				}
				// 因lucene不支持直接修改索引，所以当修改记录操作发生时先删除原索引再添加新记录索引					
				extlist.add(frowset.getString("file_id"));
				String ext = frowset.getString("ext");
				if(ext==null||ext.length()<=0)
					continue;
				try {
					String sql = "select file_id,content from LAW_BASE_FILE where file_id='" + frowset.getString("file_id") + "'";
					rs = dao.search(sql);
					if (rs.next()) {
						switch (Sql_switcher.searchDbServer()) {
						case Constant.DB2:
							Blob blob = rs.getBlob("content");
							if (blob != null)
								inputstream = blob.getBinaryStream();
							break;
						case Constant.ORACEL:
							Blob blob1 = rs.getBlob("content");
							if (blob1 != null)
								inputstream = blob1.getBinaryStream();
							break;
						case Constant.MSSQL:
							inputstream = rs.getBinaryStream("content");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (inputstream == null)
					continue;
				/*LawDirectory lawDirectory=new LawDirectory();
				lawDirectory.reBuildIndex(inputstream,frowset.getString("file_id"),frowset.getString("base_id"),ext,"");*/
				if (IndexReader.indexExists(indexPath)) {
					IndexReader ir = null;
					try {
						ir = IndexReader.open(indexPath);
						ir.delete(new Term("id", frowset.getString("file_id")));
					} catch (Exception err) {
						err.printStackTrace();
					} finally {
						try {
							ir.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				if (IndexReader.indexExists(indexPath)) {
					writer = new IndexWriter(indexPath, new ChineseAnalyzer(),
								false);
				} else {
					writer = new IndexWriter(indexPath, new ChineseAnalyzer(),
							true);
				}
				Document doc = new Document();
				doc.add(Field.Keyword("id", frowset.getString("file_id")));
				doc.add(Field.Keyword("base_id", frowset.getString("base_id")));
				
				if ("doc".equals(ext.trim().toLowerCase())) {
					try {
						doc.add(Field.Text("body",(Reader) new InputStreamReader(
												CommonBusiness.wordToText(inputstream))));
					} catch (Exception e) {
						Exception ex = new Exception("您的《"+frowset.getString("name")+"》文件有问题，请尽量使用纯文字，不要带图片或者乱码！建议您新建word，将原有的内容重新复制并拷贝进去，再重建索引");
						throw GeneralExceptionHandler.Handle(ex);
					}
				}
				if ("xls".equals(ext.trim().toLowerCase())|| ".xls".equals(ext.trim().toLowerCase())|| "xlsx".equals(ext.trim().toLowerCase())|| ".xlsx".equals(ext.trim().toLowerCase())) {
					doc.add(Field
							.Text("body", (Reader) new InputStreamReader(
									CommonBusiness.excelToText(inputstream))));
				}
				if ("txt".equals(ext.trim().toLowerCase())|| ".txt".equals(ext.trim().toLowerCase())
						|| "html".equals(ext.trim().toLowerCase())|| ".html".equals(ext.trim().toLowerCase())
						|| "htm".equals(ext.trim().toLowerCase())|| ".htm".equals(ext.trim().toLowerCase())) {
					doc.add(Field.Text("body", (Reader) new InputStreamReader(
							inputstream)));
				}
				// 将文档写入索引
				writer.addDocument(doc);
				// 索引优化
				// writer.optimize();
				// 关闭写索引器
				writer.close();
			}
		} catch (Exception ee) {
			try
			{
				writer.close();
			}catch(Exception e)
			{
				
			}
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		}
		finally{
			if (inputstream != null) {
				PubFunc.closeIoResource(inputstream);
			}
		}
		ext_BuildIndex();
	}
	public void ext_BuildIndex() throws GeneralException
	{
		String file_id="";
        String sql="";       
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        IndexWriter writer = null;
        Reader Reader = null;
        InputStream inputstream = null;
        try
        {
        	String indexPath =  LawDirectory.getLawbaseDir();
        	for(int i=0;i<extlist.size();i++)
    		{
        		file_id=extlist.get(i).toString();
    		    sql="select ext,ext_file_id,name from law_ext_file where file_id='"+file_id+"'";
    		    this.frowset=dao.search(sql);
    		    while(this.frowset.next())
    		    {
    		    	RowSet rs = null;
    		    	String ext = this.frowset.getString("ext");
    		    	String ext_file_id=this.frowset.getString("ext_file_id");
    		    	if(ext==null||ext.length()<=0)
    					continue;
    		   	 //判断文件是否是word、excle、txt、html，不是跳出循环 zhangcg  2016/8/20
                    if(!CommonBusiness.checkExt(ext))
                        continue;
    				
					try {
						rs = dao.search("select content from law_ext_file where ext_file_id='" + ext_file_id + "'");
						if (rs.next()) {
							switch (Sql_switcher.searchDbServer()) {
							case Constant.DB2:
								Blob blob = rs.getBlob("content");
								if (blob != null)
									inputstream = blob.getBinaryStream();
								break;
							case Constant.ORACEL:
								Blob blob1 = rs.getBlob("content");
								if (blob1 != null)
									inputstream = blob1.getBinaryStream();
								break;
							case Constant.MSSQL:
								inputstream = rs.getBinaryStream("content");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if (rs != null)
								rs.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
    				if (inputstream == null)
    					continue;
    				
    				if ("doc".equals(ext.trim().toLowerCase())) {
    					Reader = (Reader) new InputStreamReader(CommonBusiness.wordToText(inputstream));
    				}
    				
    				if ("xls".equals(ext.trim().toLowerCase())|| "xlsx".equals(ext.trim().toLowerCase())|| ".xls".equals(ext.trim().toLowerCase())|| ".xlsx".equals(ext.trim().toLowerCase())) {
    					Reader = (Reader) new InputStreamReader(CommonBusiness.excelToText(inputstream));
    				}
    				
    				if ("txt".equals(ext.trim().toLowerCase()) || "html".equals(ext.trim().toLowerCase())) {
    					Reader = (Reader) new InputStreamReader(inputstream);
    				}
    				//Reader = (Reader) new InputStreamReader(CommonBusiness.wordToText(inputstream));
    				
    				
    				if (IndexReader.indexExists(indexPath)) {
    					IndexReader ir = null;
    					try {
    						ir = IndexReader.open(indexPath);
    						ir.delete(new Term("ide", file_id));
    					} catch (Exception err) {
    						err.printStackTrace();
    					} finally {
    						try {
    							ir.close();
    						} catch (Exception e) {
    							e.printStackTrace();
    						}
    					}
    				}
    				
    				if (IndexReader.indexExists(indexPath)) {
    					writer = new IndexWriter(indexPath, new ChineseAnalyzer(),
    							false);
    				} else {
    					writer = new IndexWriter(indexPath, new ChineseAnalyzer(),
    							true);
    				}
    				Document doc = new Document();
    				doc.add(Field.Keyword("ide", file_id));
    				doc.add(Field.Keyword("ext_file_id", ext_file_id));    				
    				if ("doc".equals(ext.trim().toLowerCase())) {
    					try {
	    					doc.add(Field.Text("body", Reader));
    					} catch (Exception e) {
    						Exception ex = new Exception("您的《"+frowset.getString("name")+"》附件文件有问题，请尽量使用纯文字，不要带图片或者乱码！建议您新建word，将原有的内容重新复制并拷贝进去，再重建索引");
    						throw GeneralExceptionHandler.Handle(ex);
    					}
    				}
    				if ("xls".equals(ext.trim().toLowerCase())|| "xlsx".equals(ext.trim().toLowerCase())|| ".xls".equals(ext.trim().toLowerCase())|| ".xlsx".equals(ext.trim().toLowerCase())) {
    					doc.add(Field.Text("body", Reader));
    				}
    				if ("txt".equals(ext.trim().toLowerCase())
    						|| "html".equals(ext.trim().toLowerCase())) {
    					doc.add(Field.Text("body", Reader));
    				}
    				
    				
    				writer.addDocument(doc);    
    				//此处必须关闭writer否则下次循环将不能在操作索引
    				writer.close();
    		    }
    		}
        } catch (Exception e) {
        	try {
        		writer.close();
        	} catch (Exception ee) {
        		
        	}
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(inputstream);
            PubFunc.closeResource(Reader);
        }
		
	}
}
