package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.param.DocumentParamXML;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.struts.upload.FormFile;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.*;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 规章制度导入
 * <p>
 * Title:LawBaseUpLoadTrans.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Nov 13, 2006 10:38:35 AM
 * </p>
 * 
 * @author sunxin
 * @version 1.0
 * 
 */
public class LawBaseUpLoadTrans extends IBusiness {
	private HashMap fileMap = new HashMap();
	private HashMap fileSize = new HashMap();
	private ArrayList filelist = new ArrayList();
	private ArrayList extlist = new ArrayList();
	private HashMap baseDataMap = new HashMap();
	private HashMap ext_content = new HashMap();
	private HashMap fileDataMap = new HashMap();
	private HashMap file_content = new HashMap();
	private String basetype;

	public void execute() throws GeneralException {

		FormFile file = (FormFile) this.getFormHM().get("file");
		//xiexd 2014.09.22后台验证文件		
		try {
			if(!FileTypeUtil.isFileTypeEqual(file)){
				 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		String base_id = (String) this.getFormHM().get("base_id");
		String basetype = (String) this.getFormHM().get("basetype");
		if (basetype == null || basetype.length() <= 0)
			basetype = "1";
		this.basetype = basetype;
		InputStream is = null;
		int Buffer = 1024;
		byte[] byteArray = new byte[Buffer];
		FileOutputStream fos = null;
		String url = System.getProperty("java.io.tmpdir")
				+ System.getProperty("file.separator") + base_id
				+ new Date().getTime();
		InputStream in = null;
		try {
			Document doc = null;
			fos = new FileOutputStream(url);
			is = file.getInputStream();
			while (is.read(byteArray, 0, Buffer) > 0) {
				fos.write(byteArray);
			}
			File f = new File(url);
			ZipFile zipFile = new ZipFile(f);
			java.util.Enumeration e = zipFile.getEntries();
			ZipEntry zipEntry = null;
				while (e.hasMoreElements()) {
					zipEntry = (ZipEntry) e.nextElement();
					in = zipFile.getInputStream(zipEntry);
					String fileName = zipEntry.getName();
					fileSize.put(fileName, zipEntry.getSize() + "");
					//System.out.println(fileName+"---"+zipEntry.getSize());
					fileMap.put(fileName, in);
					if ("xml".equalsIgnoreCase(fileName.substring(fileName.length() - 3,
							fileName.length()))) {
						doc = PubFunc.generateDom(in);

					}
				}
				if (doc != null) {
                    synchronized (this) {
					    parseXml(doc);
                    }
				}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory
					.getProperty("lable.lawfile.zip.info")));
		} finally {
			PubFunc.closeResource(fos);
			PubFunc.closeResource(is);
			PubFunc.closeResource(in);
		}
	}

	/**
	 * 目录数据操作
	 * 
	 * @param doc
	 */
	public void parseXml(Document doc) {
		HashMap s_hash = createStruct(doc, "/lawbase/dir");
		String s_column = (String) s_hash.get("struct_column");
		ArrayList s_list = (ArrayList) s_hash.get("list_base");
		ArrayList up_beastype_list = (ArrayList) s_hash.get("up_beastype_list");
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if (s_list != null && s_list.size() > 0) {
				StringBuffer s_insert = new StringBuffer();
				s_insert.append("insert into law_base_struct(");
				s_insert.append(s_column + ") values");
				s_insert.append("(?,?,?,?,?,?,?,?,?)");

				dao.batchInsert(s_insert.toString(), s_list);
				ArrayList rolelist = new ArrayList();
				for (int i = 0; i < s_list.size(); i++) {
					rolelist = (ArrayList) s_list.get(i);
					String base_id = (String) rolelist.get(0);
					UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
					if ("1".equalsIgnoreCase(basetype)) {
						user_bo.saveResource(base_id, this.userView,
								IResourceConstant.LAWRULE);
					}
					if ("5".equalsIgnoreCase(basetype)) {
						user_bo.saveResource(base_id, this.userView,
								IResourceConstant.DOCTYPE);
					}
					if ("4".equalsIgnoreCase(basetype)) {
						user_bo.saveResource(base_id, this.userView,
								IResourceConstant.KNOWTYPE);
					}
				}
				parseFileXml(doc);
			}
			if (up_beastype_list != null && up_beastype_list.size() > 0) {
				String update = "update law_base_struct set basetype=? where base_id=?";
				dao.batchUpdate(update, up_beastype_list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件数据操作
	 * 
	 * @param doc
	 */
	public void parseFileXml(Document doc) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			HashMap f_hash = createFile(doc, "/lawbase/file");
			String f_column = (String) f_hash.get("file_column");
			ArrayList f_list = (ArrayList) f_hash.get("list_file");
			DocumentParamXML documentParamXML = new DocumentParamXML(this
					.getFrameconn());
			String codesetid = "";
			String codeitemid = "";
			if ("5".equalsIgnoreCase(basetype)) {
				codesetid = documentParamXML.getValue(DocumentParamXML.FILESET,
						"setid");
				codeitemid = documentParamXML.getValue(
						DocumentParamXML.FILESET, "fielditem");
			}
			if (f_list != null && f_list.size() > 0) {
				StringBuffer f_insert = new StringBuffer();
				f_insert.append("insert into  law_base_file(");
				f_insert.append(f_column + ") values");
				f_insert.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				dao.batchInsert(f_insert.toString(), f_list);
				addFileConntent();
				ArrayList filemap = new ArrayList();
				HashMap map = new HashMap();
				for (int i = 0; i < f_list.size(); i++) {
					filemap = (ArrayList) f_list.get(i);
					String file_id = (String) filemap.get(0);
					UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
					/*
					 * if(basetype.equalsIgnoreCase("1")){
					 * user_bo.saveResource(file_id, this.userView,
					 * IResourceConstant.LAWRULE); }
					 * if(basetype.equalsIgnoreCase("5")){
					 */
					user_bo.saveResource(file_id, this.userView,
							IResourceConstant.LAWRULE_FILE);
					// }
					map = (HashMap) filelist.get(i);
					if ("5".equalsIgnoreCase(basetype)) {
						String relat = (String) map.get("relat");
						if (relat != null && !"".equalsIgnoreCase(relat)) {
							String[] A0100str = relat.split("`");
							for (int j = 0; j < A0100str.length; j++) {
								String dbname = A0100str[j].substring(0, 3);
								String A0100 = A0100str[j].substring(3);
								StringBuffer sql = new StringBuffer();
								sql.append("select max(I9999) I9999 from "
										+ dbname + codesetid
										+ " where A0100 = '" + A0100 + "'");
								this.frowset = dao.search(sql.toString());
								int i9999 = 0;
								while (this.frowset.next()) {
									i9999 = this.frowset.getInt("I9999");
								}
								RecordVo vo = new RecordVo(dbname + codesetid);
								vo.setString("a0100", A0100);
								vo.setInt("i9999", i9999 + 1);
								vo.setString(codeitemid, file_id);
								dao.addValueObject(vo);
							}
						}
					}
				}
				parseExtXml(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parseExtXml(Document doc) {
		HashMap e_hash = createExt(doc, "/lawbase/ext");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			String e_column = (String) e_hash.get("ext_column");
			ArrayList e_list = (ArrayList) e_hash.get("list_ext");
			if (e_list != null && e_list.size() > 0) {
				StringBuffer f_insert = new StringBuffer();
				f_insert.append("insert into  law_ext_file(");
				f_insert.append(e_column + ") values");
				f_insert.append("(?,?,?,?,?,?,?)");
				dao.batchInsert(f_insert.toString(), e_list);
				addExtConntent1();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 目录数据操作
	 * 
	 * @param doc
	 * @param xpath
	 * @return
	 */
	public HashMap createStruct(Document doc, String xpath) {
		HashMap map = new HashMap();
		ArrayList list_base = new ArrayList();
		HashMap key_map = getKey_map(doc, xpath);
		ArrayList up_beastype_list = new ArrayList();
		try {
			XPath reportPath = null;
			reportPath = XPath.newInstance(xpath);// 取得根节点
			List childlist = reportPath.selectNodes(doc);
			Iterator i = childlist.iterator();
			if (i.hasNext()) {
				Element childR = (Element) i.next();
				String columnsStr = childR.getAttributeValue("columns");
				int last = columnsStr.lastIndexOf(",");
				columnsStr = columnsStr.substring(0, last);
				map.put("struct_column", columnsStr);
				List list = childR.getChildren();
				Iterator r = list.iterator();
				String record = "";
				String[] recordArray = null;
				while (r.hasNext()) {
					ArrayList list_one = new ArrayList();
					ArrayList beas_list_one = new ArrayList();
					Element recordR = (Element) r.next();
					String base_id_old = recordR.getAttributeValue("base_id");
					String up_base_id_old = recordR.getAttributeValue("up_base_id");
					String field_str_old = recordR.getAttributeValue("field_str");
					if(("").endsWith(field_str_old)){
						field_str_old = null;
					}
					String key = recordR.getAttributeValue("key");
					String base_id_new = "";
					String up_base_id_new = "";
					//
					//up_base_id_new = (String) key_map.get(up_base_id_old);
					base_id_new = (String) key_map.get(base_id_old);
					
					baseDataMap.put(base_id_old, base_id_new);
					record = recordR.getText();
					recordArray = record.split("`");
					
					if(key_map.containsKey(up_base_id_old) && key_map.get(up_base_id_old)!=null)
						up_base_id_new = (String)key_map.get(up_base_id_old);
					else{
						if(recordArray[4].equals(this.basetype)){
							if(base_id_old.equals(up_base_id_old))
								up_base_id_new = base_id_new;
							else
								up_base_id_new = checkUpBaseId(up_base_id_old,this.basetype,base_id_new);
						}else{
							up_base_id_new = base_id_new;
							
						}
							
					}
					
					recordArray[4] = this.basetype;
					
					list_one.add(base_id_new);
					list_one.add(up_base_id_new);
					beas_list_one.add(this.basetype);
					beas_list_one.add(up_base_id_new);
					for (int s = 0; s < recordArray.length - 1; s++) {
						list_one.add(recordArray[s]);
					}
					list_one.add(field_str_old);
					list_base.add(list_one);
					up_beastype_list.add(beas_list_one);
				}
			}
			map.put("list_base", list_base);
			map.put("up_beastype_list", up_beastype_list);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	
	public String checkUpBaseId(String up_base_id_old,String basetype,String base_id_new){
		
		  ContentDAO dao = new ContentDAO(this.frameconn);
		  
		  String sql = "select 1 from law_base_struct where basetype='"+basetype+"' and base_id='"+up_base_id_old+"'";
		  try{
			  frowset = dao.search(sql);
			  if(frowset.next())
				  return up_base_id_old;
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		
		   return base_id_new;
	}
	
	public HashMap createFile(Document doc, String xpath) {
		HashMap map = new HashMap();
		ArrayList list_file = new ArrayList();
		try {
			XPath reportPath = null;
			reportPath = XPath.newInstance(xpath);// 取得根节点
			List childlist = reportPath.selectNodes(doc);
			Iterator i = childlist.iterator();
			if (i.hasNext()) {
				Element childR = (Element) i.next();
				String columnsStr = childR.getAttributeValue("columns");
				map.put("file_column", columnsStr);
				List list = childR.getChildren();
				Iterator r = list.iterator();
				String record = "";
				String[] recordArray = null;

				while (r.hasNext()) {
					HashMap file_Map = new HashMap();
					ArrayList list_one = new ArrayList();
					Element recordR = (Element) r.next();
					IDGenerator idg = new IDGenerator(2, this.getFrameconn());
					String file_id_new = idg.getId("law_base_file.id");
					String file_id_old = recordR.getAttributeValue("file_id");
					String base_id_old = recordR.getAttributeValue("base_id");
					String base_id_new = (String) baseDataMap.get(base_id_old);
					fileDataMap.put(file_id_old, file_id_new);
					file_content.put(file_id_new, recordR
							.getAttributeValue("file"));
					record = recordR.getText();
					recordArray = record.split("`");
					list_one.add(file_id_new);
					if (base_id_new == null || base_id_new.length() <= 0)
						base_id_new = "ALL";
					list_one.add(base_id_new);
					String[] newrecord;
					if (recordArray.length < columnsStr.split(",").length - 2) {
						newrecord = new String[recordArray.length
								+ columnsStr.split(",").length - 2
								- recordArray.length];
						System.arraycopy(recordArray, 0, newrecord, 0,
								recordArray.length);
						recordArray = newrecord;
					}
					for (int s = 0; s < recordArray.length; s++) {

						if (s == 8 || s == 9 || s == 10) {
							String ss = recordArray[s];
							if ("null".equalsIgnoreCase(ss) || "".equals(ss)) {
								list_one.add(null);
							} else {
								ss = ss.substring(0, 10);
								java.sql.Date s_date = DateUtils.getSqlDate(ss,
										"yyyy-MM-dd");
								list_one.add(s_date);
							}
						} else {
							if (recordArray[s] == null
									|| "null".equalsIgnoreCase(recordArray[s]))
								list_one.add(null);
							else
								list_one
										.add(recordArray[s]/* .equalsIgnoreCase("null")?"":recordArray[s] */);
						}

					}
					list_file.add(list_one);
					file_Map.put("file_id", file_id_new);
					file_Map.put("base_id", base_id_new);
					file_Map.put("file_name", recordR
							.getAttributeValue("file_name"));
					file_Map.put("name",list_one.get(2));
					file_Map.put("title",list_one.get(3));
					file_Map.put("file_ext", recordR.getAttributeValue("ext"));
					file_Map.put("digest", recordR.getAttributeValue("digest"));
					file_Map.put("originalfile", recordR
							.getAttributeValue("originalfile"));
					file_Map.put("originalext", recordR
							.getAttributeValue("originalext"));
					file_Map.put("relat", recordR.getAttributeValue("relat"));
					filelist.add(file_Map);
				}
			}
			map.put("list_file", list_file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public HashMap createExt(Document doc, String xpath) {
		HashMap map = new HashMap();
		ArrayList list_ext = new ArrayList();
		try {
			XPath reportPath = null;
			reportPath = XPath.newInstance(xpath);// 取得根节点
			List childlist = reportPath.selectNodes(doc);
			Iterator i = childlist.iterator();
			
			if (i.hasNext()) {
				Element childR = (Element) i.next();
				String columnsStr = childR.getAttributeValue("columns");
				map.put("ext_column", columnsStr);
				List list = childR.getChildren();
				Iterator r = list.iterator();
				String record = "";
				String[] recordArray = null;
				LawDirectory lawDirectory = new LawDirectory();
				String extpk = lawDirectory.getMaxEitId(this.getFrameconn());
				int num = 0;
				int pk = Integer.parseInt(extpk);
				while (r.hasNext()) {
					HashMap extMap = new HashMap();
					ArrayList list_one = new ArrayList();
					Element recordR = (Element) r.next();
					if (num > 0)
						pk = pk + 1;
					String ext_id_new = pk + "";
					String ext_id_ole = recordR
							.getAttributeValue("ext_file_id");
					String file_id_old = recordR.getAttributeValue("file_id");
					String file_id_new = (String) fileDataMap.get(file_id_old);
					ext_content.put(ext_id_new, recordR
							.getAttributeValue("ext"));
					record = recordR.getText();
					recordArray = record.split("`");
					list_one.add(ext_id_new);
					list_one.add(file_id_new);
					for (int s = 0; s < recordArray.length; s++) {
						if (s == 3) {
							String ss = recordArray[s];
							if ("null".equalsIgnoreCase(ss) || "".equals(ss)) {
								list_one.add(null);
							} else {
								//ss = ss.substring(0, 10);
								list_one.add(new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ss).getTime()));
							}
						} else if(s==0){
							list_one.add(new Integer(recordArray[s]));
						}else{
							list_one.add(recordArray[s]);
						}

					}
					list_ext.add(list_one);
					extMap.put("ext_file_id", ext_id_new);
					extMap.put("ext_file_name", recordR
							.getAttributeValue("file_name"));
					extMap.put("ext", recordR.getAttributeValue("ext"));
					extMap.put("ext_name", recordR.getAttributeValue("name"));
					extlist.add(extMap);
					num++;
				}
			}
			map.put("list_ext", list_ext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public void addFileConntent() {
		HashMap hashmap = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
		String userName = this.userView.getUserName();
		VfsModulesEnum vfsModulesEnum = VfsModulesEnum.WD;
		VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
		String strsql = "update law_base_file set fileid=? where  file_id= ?";
		String strsql2 = "update law_base_file set originalfileid=? where  file_id= ?";
		// LawDirectory lawDirectory=new LawDirectory();
		
		  DbSecurityImpl dbS = new DbSecurityImpl();
		try {
			pstmt = this.getFrameconn().prepareStatement(strsql);
			pstmt2 = this.getFrameconn().prepareStatement(strsql2);
			for (int i = 0; i < filelist.size(); i++) {
				hashmap = (HashMap) filelist.get(i);
				String file_id = (String) hashmap.get("file_id");
				String file_name = (String) hashmap.get("file_name");
				String name = (String) hashmap.get("name");
				String title = (String) hashmap.get("title");
				String file_ext = (String) hashmap.get("file_ext");
				String digest = (String) hashmap.get("digest");
				String base_id = (String) hashmap.get("base_id");
				String originalfile = (String) hashmap.get("originalfile");
				String originalext = (String) hashmap.get("originalext");
				InputStream in = (InputStream) fileMap.get(file_name + "."
						+ file_ext);
				
				if (in != null) {
					String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                		"", in, name+"."+ file_ext, "", false);
					pstmt.setString(1, fieldId);
					pstmt.setString(2, file_id);
					dbS.open(this.getFrameconn(), strsql);
					pstmt.executeUpdate();
				}
				InputStream in2 = (InputStream) fileMap.get(originalfile + "."
						+ originalext);
				if (in2 != null) {
					String originalfieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                		"", in2, title+"."+originalext, "", false);
					pstmt2.setString(1, originalfieldId);
					pstmt2.setString(2, file_id);
					dbS.open(this.getFrameconn(), strsql2);
					pstmt2.executeUpdate();
				}

				/*
				 * LawDirectory lawDirectory=new LawDirectory();
				 * lawDirectory.reBuildIndex(in,file_id,base_id,file_ext,digest);
				 */

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭Wallet
				dbS.close(this.getFrameconn());
			} catch (Exception e) {
				e.printStackTrace();
			}
			PubFunc.closeResource(pstmt);
			PubFunc.closeResource(pstmt2);
		}

	}

	public void addExtConntent1() {
		HashMap hashmap = null;
		PreparedStatement pstmt = null;
		String strsql = "update law_ext_file set content=?  where  ext_file_id= ?";
		LawDirectory lawDirectory = new LawDirectory();
		DbSecurityImpl dbS = new DbSecurityImpl();
		try {
			dbS.open(this.getFrameconn(), strsql);
			pstmt = this.getFrameconn().prepareStatement(strsql);
			for (int i = 0; i < extlist.size(); i++) {

				hashmap = (HashMap) extlist.get(i);
				String ext_file_id = (String) hashmap.get("ext_file_id");
				String ext_file_name = (String) hashmap.get("ext_file_name");
				String ext = (String) hashmap.get("ext");
				String ext_name = (String) hashmap.get("ext_name");
				InputStream in = (InputStream) fileMap.get(ext_file_name+"."+ext);
				if (in == null)
					continue;
				String s_size = (String) fileSize.get(ext_file_name+"."+ext);
				int size = Integer.parseInt(s_size);
				switch (Sql_switcher.searchDbServer()) {
				case Constant.DB2:

					pstmt.setBinaryStream(1, in, size);
					break;
				case Constant.ORACEL:
					Blob blob = getFileOracleBlob1(ext_file_id, in);
					pstmt.setBlob(1, blob);
					break;
				default:
					pstmt.setBinaryStream(1, in, size);
					break;
				}
				pstmt.setString(2, ext_file_id);
				pstmt.executeUpdate();
				// lawDirectory.reBuildIndex(in,ext_file_id,"",ext,ext_name);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				
				try {
					// 关闭Wallet
					dbS.close(this.getFrameconn());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException ee) {
				ee.printStackTrace();
			}
		}

	}

	public int getInputStreamNUM(InputStream in) {
		int bytesRead = 0;

		try {
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100]; // buff用于存放循环读取的临时数据
			int rc = 0;
			while ((rc = in.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] in_b = swapStream.toByteArray(); // in_b为转
			bytesRead = in_b.length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytesRead;
	}

	private Blob getFileOracleBlob(String file_id, InputStream in)
			throws FileNotFoundException, IOException {
		StringBuffer strSearch = new StringBuffer();
		strSearch.append("select content from law_base_file where file_id='");
		strSearch.append(file_id);
		strSearch.append("' FOR UPDATE");
		StringBuffer strInsert = new StringBuffer();
		strInsert
				.append("update  law_base_file set content=EMPTY_BLOB() where file_id='");
		strInsert.append(file_id);
		strInsert.append("'");
		OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
		Blob blob = blobutils.readBlob(strSearch.toString(), strInsert
				.toString(), in); // readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}

	private Blob getFileOracleBlob1(String file_id, InputStream in)
		throws FileNotFoundException, IOException {
		StringBuffer strSearch = new StringBuffer();
		strSearch.append("select content from law_ext_file where ext_file_id='");
		strSearch.append(file_id);
		strSearch.append("' FOR UPDATE");
		StringBuffer strInsert = new StringBuffer();
		strInsert
				.append("update  law_ext_file set content=EMPTY_BLOB() where ext_file_id='");
		strInsert.append(file_id);
		strInsert.append("'");
		OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
		Blob blob = blobutils.readBlob(strSearch.toString(), strInsert
				.toString(), in); // readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}
	/**
	 * 目录数据操作
	 * 
	 * @param doc
	 * @param xpath
	 * @return
	 */
	public HashMap getKey_map(Document doc, String xpath) {
		HashMap key_map = new HashMap();
		try {
			XPath reportPath = null;
			reportPath = XPath.newInstance(xpath);// 取得根节点
			List childlist = reportPath.selectNodes(doc);
			Iterator i = childlist.iterator();
			if (i.hasNext()) {
				Element childR = (Element) i.next();
				List list = childR.getChildren();
				Iterator r = list.iterator();
				while (r.hasNext()) {

					Element recordR = (Element) r.next();
					String base_id_old = recordR.getAttributeValue("base_id");
					IDGenerator idg = new IDGenerator(2, this.getFrameconn());
					String base_id_new = idg.getId("law_base_struct.id");
					key_map.put(base_id_old, base_id_new);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return key_map;
	}
}
