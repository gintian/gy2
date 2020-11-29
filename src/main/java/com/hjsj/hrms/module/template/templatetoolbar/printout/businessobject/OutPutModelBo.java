package com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.templateanalyse.ParseHtml;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.module.utils.asposeword.AsposeReadWordUtil;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.cyberneko.html.parsers.DOMParser;
import org.xml.sax.InputSource;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class OutPutModelBo {
	private Connection conn = null;
	private UserView userView = null;
	private String judgeisllexpr = null;
	private String modelFileName="";//记录模版名称

	public OutPutModelBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}

	/**
	 * 
	 * @param ins_id
	 * @param task_id
	 * @param id
	 *            word模版id
	 * @param questionid
	 * @param current_id
	 * @param selfapply
	 *            是否自助： 0：业务 1:自助
	 * @param infor_type
	 *            类型：1：人员 2：
	 * @param tabid
	 *            表单id
	 * @param isPdf
	 *            是否打印pdf 1：pdf 0：word
	 * @return
	 */
	public String fileZipName(String ins_id, String task_id, String id, String questionid, String current_id,
			String selfapply, String infor_type, String tabid, String isPdf, String filetype) {
		FileInputStream fis = null;
		ZipOutputStream out = null;
		String tmpFileName = "";
		RowSet rset=null;
		try {
			ArrayList idlist = new ArrayList();
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("id", id);
			bean.set("filetype", filetype);
			idlist.add(bean);
			//获取模版名称
			String SearchNameSql = "select name from t_wf_template where tp_id=?";
			ArrayList sqlList = new ArrayList();
			sqlList.add(id);
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(SearchNameSql.toString(), sqlList);
			String fileName=this.userView.getUserName();
			if(rset.next())
				fileName=rset.getString("name")==null?fileName:rset.getString("name")+"_"+fileName;
			this.modelFileName=fileName;//bug35453 导出多人模版文件名为空。
			ArrayList filelist = outPutModel(ins_id, task_id, idlist, questionid, current_id, selfapply, infor_type,
					tabid, isPdf);
			/**syl bug 52704V76人事异动 按模板导出，表单中没有人员时应给出提示，不该直接导出空白文件*/
			if(filelist==null||filelist.size()<1){
				/**如果没有记录，返回名称为空*/
				tmpFileName="";
			}else if(filelist!=null&&filelist.size()==1) {
				//System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")
				HashMap map=(HashMap)filelist.get(0);
				Set keySet = map.keySet();
				Iterator iterator = keySet.iterator();
				String filePath = "";
				while (iterator.hasNext()) {
					String typeKey = iterator.next().toString();
					filePath = map.get(typeKey).toString();
				}
				tmpFileName=filePath.replace(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator"), "");
			}else {
				tmpFileName =fileName + ".zip";
				tmpFileName = tmpFileName.replace("/", "／");
				byte[] buffer = new byte[1024];
				String strZipPath = System.getProperty("java.io.tmpdir") + File.separator + tmpFileName;
				out = new ZipOutputStream(new FileOutputStream(strZipPath));
				// 下载的文件集合
				for (int i = 0; i < filelist.size(); i++) {
					HashMap map = (HashMap) filelist.get(i);
					Set keySet = map.keySet();
					Iterator iterator = keySet.iterator();
					String filePath = "";
					while (iterator.hasNext()) {
						String typeKey = iterator.next().toString();
						filePath = map.get(typeKey).toString();
					}
					fis = new FileInputStream(filePath);
					out.putNextEntry(new ZipEntry(filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1)));
					// 设置压缩文件内的字符编码，不然会变成乱码
					out.setEncoding("UTF-8");
					int len;
					// 读入需要下载的文件的内容，打包到zip文件
					while ((len = fis.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					out.closeEntry();
					fis.close();
				}
				out.close();
				
				// 删除打包后剩余的文件
				for (int i = 0; i < filelist.size(); i++) {
					HashMap map = (HashMap) filelist.get(i);
					Set keySet = map.keySet();
					Iterator iterator = keySet.iterator();
					String filePath = "";
					while (iterator.hasNext()) {
						String typeKey = iterator.next().toString();
						filePath = map.get(typeKey).toString();
					}
					File docfile = new File(filePath);
					if (docfile.exists())
						docfile.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(fis);
			PubFunc.closeDbObj(out);
		}
		
		return tmpFileName;
	}

	public ArrayList outPutModel(String ins_id, String task_id, String questionid, String current_id, String selfapply,
			String infor_type, String tabid, String isPdf) throws SQLException {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select tp_id,name,content,filetype from t_wf_template where tabid=");
		strsql.append(tabid);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rset = dao.search(strsql.toString());
		DOMParser parser = new DOMParser();
		ArrayList list = new ArrayList();
		while (rset.next()) {
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("tabid", String.valueOf(tabid));
			bean.set("id", rset.getString("tp_id"));
			bean.set("name", rset.getString("name"));
			bean.set("filetype", rset.getString("filetype"));// liuyz 单人模版还是多人模版
			bean.set("flag", "1");
			InputStream in = null;
			try {
				bean.set("isHtml", "true");// liuyz 导出单人、多人模版支持word直接上传
											// true表示是转换为html格式上传的。
				in = rset.getBinaryStream("content");
				InputSource inputsource = new InputSource(in);
				parser.parse(inputsource);
				org.w3c.dom.Document doc = parser.getDocument();
				org.w3c.dom.Node node = doc.getDocumentElement().getFirstChild();
				if (node.getNamespaceURI() == null)// 直接上传word此值为null
				{
					bean.set("isHtml", "false");
				} else if (node.getNamespaceURI().length() == 0)
					continue;
			} catch (Exception ee) {
				// ee.printStackTrace();
				continue;
			} finally {
				PubFunc.closeIoResource(in);
			}

			list.add(bean);
			break;//bug 33288 目前上会只支持一个word模版，所以加一个以后就break跳出。
		}
		return outPutModel(ins_id, task_id, list, questionid, current_id, selfapply, infor_type, tabid, isPdf);
	}

	public ArrayList outPutModel(String ins_id, String task_id, ArrayList idlist, String questionid, String current_id,
			String selfapply, String infor_type, String tabid, String isPdf) {
		ArrayList fileList = new ArrayList();
		StringBuffer judgeoperationtype = new StringBuffer();
		judgeoperationtype.append(
				"select operationtype from operation a,template_table b where a.operationcode=b.operationcode and b.tabid=? ");
		ContentDAO dao = new ContentDAO(this.conn);
		LazyDynaBean paramBean = new LazyDynaBean();
		paramBean.set("questionid", questionid);
		paramBean.set("current_id", current_id);
		DbWizard dbw = new DbWizard(this.conn);
		ArrayList a0100List = new ArrayList();
		ArrayList inslist = null;
		try {
			for (int idNum = 0; idNum < idlist.size(); idNum++) {
				LazyDynaBean idBean = (LazyDynaBean) idlist.get(idNum);
				String id = (String) idBean.get("id");
				String fileType = (String) idBean.get("filetype");
				TemplateTableBo tablebo = new TemplateTableBo(this.conn, Integer.parseInt(tabid), this.userView);
				ArrayList judgeoperationList=new ArrayList();
				judgeoperationList.add(tabid);
				RowSet frowset = dao.search(judgeoperationtype.toString(),judgeoperationList);
				if (frowset.next()) {
						judgeisllexpr = "1";
						inslist = new ArrayList();

						if ("0".equalsIgnoreCase(task_id)) {
							inslist.add(ins_id);
						} else {
							inslist = getIns_id(task_id);
						}

						tablebo.setInslist(inslist);
						a0100List = judgeIsLlexpr(tabid, tablebo.getLlexpr(), inslist, infor_type, task_id, selfapply);
				} else
					judgeisllexpr = "0";

				String isSendMessage = "0";
				if (tablebo.isBemail() && tablebo.isBsms())
					isSendMessage = "3";
				else if (tablebo.isBemail())
					isSendMessage = "1";
				else if (tablebo.isBsms())
					isSendMessage = "2";
				if ("1".equalsIgnoreCase(judgeisllexpr)) {
					if ("1".equalsIgnoreCase(infor_type)) {
					HashMap a0101MapData=new HashMap();
					String tablename = "templet_" + tabid;
					if ("0".equalsIgnoreCase(task_id)){
						if ("1".equalsIgnoreCase(selfapply))
							tablename="g_templet_"+tabid;
						else
							tablename = userView.getUserName() + tablename;
					}
					
					int a0100Num=0;
					HashMap a0101Map=new HashMap();
					if("2".equalsIgnoreCase(fileType))//单人模版，每个人生成一个word文件
					{
						a0100Num=a0100List.size();
						String preA0100Str="";
						String insidStr="";
						//查询人的真实名字
						for(int num=0;num<a0100Num;num++)
						{
							String object_id=(String) a0100List.get(num);
							String[] objects = object_id.split("`",3);
							preA0100Str+="'"+(objects[0]+objects[1]).toLowerCase()+"',";
							if(!("0".equals(task_id)))
								insidStr+=objects[2]+",";
							if((num+1)%999==0){//bug 39500 导出单人模版超过1000人文件名不显示姓名。oracle in超1000报错
								selectNameByA0100(tablename,insidStr,preA0100Str,task_id,a0101Map,tablebo);
								insidStr="";
								preA0100Str="";
							}else if((num+1)==a0100Num&&StringUtils.isNotBlank(preA0100Str)){
								selectNameByA0100(tablename,insidStr,preA0100Str,task_id,a0101Map,tablebo);
							}
						}
					}
					else if("0".equalsIgnoreCase(fileType))//多人模版，所有人生成一个word模版
					{
						a0100Num=1;
					}
					for (int num = 0; num <a0100Num&&a0100List.size()>0 ; num++) {
						String object_id = (String) a0100List.get(num);
							String[] object_ids = object_id.split("`");
							String a0100 = object_ids[1];
							String pre = object_ids[0];
							String filename = createTemplateFile(id);
							filename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")
									+ filename;
							ParseHtml parsehtml = new ParseHtml(filename, userView, tabid, task_id, inslist, "0",
									this.conn);
							parsehtml.setSrc_a0100(a0100);
							parsehtml.setSrc_per(pre);
							parsehtml.setInfor_type(infor_type);
							try {
								// 获取文件中的标签
								AsposeReadWordUtil readAspo = new AsposeReadWordUtil(filename);
								readAspo.setIsPdf(isPdf);
								ArrayList worBeanList = readAspo.getWordBean();
								// 解析标签，保存返回结果
								for (int i = 0; i < worBeanList.size(); i++) {
									LazyDynaBean bean = (LazyDynaBean) worBeanList.get(i);
									Boolean isTable = "subSet".equalsIgnoreCase(String.valueOf(bean.get("hz")));// 判断是不是表格标签
									Boolean isPhoto = "photo".equalsIgnoreCase(String.valueOf(bean.get("photo")));// 判断是不是照片
									String datastr = "";
									if (isTable) {
										String hzVale = (String) bean.get("hzValue");

										Integer rowNum = (Integer) bean.get("rowNum");// 表格需要返回的数据行数
										if((Boolean) bean.get("isChildTable")){
											ArrayList childList=(ArrayList) bean.get("hzValueList");
											for (int childNum = 0; childNum < childList.size(); childNum++) {
												LazyDynaBean childBean = (LazyDynaBean) childList.get(childNum);
												hzVale = (String) childBean.get("hzValue");
												rowNum = (Integer) childBean.get("rowNum");//子集嵌套在单元格中，需要从子集的bean中拿行数
												ArrayList list = parsehtml.executeTemplateDocumentSubList(hzVale, tablename,
														object_id, paramBean, rowNum,bean);
												childBean.set("subSetList", list);
												
											}
										}else{
											ArrayList list = parsehtml.executeTemplateDocumentSubList(hzVale, tablename,
													object_id, paramBean, rowNum,bean);
											bean.set("subSetList", list);
										}
										
									} else if (isPhoto) {
										String photoUrl = parsehtml.executeTemplatePhoto(String.valueOf(bean.get("hz")),
												tablename, object_id, paramBean);
										bean.set("hzValue", photoUrl);
									} else {
										datastr = parsehtml.executeTemplateDocument(String.valueOf(bean.get("hz")),
												tablename, object_id, paramBean);
										datastr = datastr.replaceAll("wlhxryhrp", " ");
										datastr = datastr.replaceAll("xrywlh888", "\r");//bug 35512 \n在输出的word中变成了空格。
										if("null".equalsIgnoreCase(datastr.trim())){
											datastr=datastr.toLowerCase().replaceAll("null", "");
										}
										bean.set("hzValue", datastr);

									}
								}
								String wordUrl = readAspo.getWordUrl(worBeanList);// 获取保存的文件路径
								String ext = wordUrl.substring(wordUrl.lastIndexOf(".") + 1);
								//InputStream in = new FileInputStream(wordUrl);
								String name =(num+1)+pre+a0100+ "." + ext;
								if(a0101Map.containsKey((pre+a0100).toLowerCase()))
								{
									name=(num+1)+String.valueOf(a0101Map.get((pre+a0100).toLowerCase()))+ "." + ext;
								}
								if("0".equalsIgnoreCase(fileType))//多人模版，所有人生成一个word模版所以文件名显示模版名称_当前登陆人
								{
									name=this.modelFileName+ "." + ext;
								}
								name = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")
										+ name;
								FileUtils.copyFile(new File(wordUrl), new File(name));
								File oldfile = new File(wordUrl);
								if (oldfile.exists())
									oldfile.delete();
								HashMap fileMap = new HashMap();
								fileMap.put(pre + a0100, name);
								fileList.add(fileMap);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileList;
	}

	private ArrayList judgeIsLlexpr(String tabid, String llexpr, ArrayList inslist, String infor_type, String task_id,
			String selfapply) {
		HashMap hm = new HashMap();
		ArrayList a0100lists = new ArrayList();
		String first_base = null;
		String a0100 = null;
		RowSet rset = null;
		/**
		 * 员工自助申请标识 ＝1员工 ＝0业务员
		 */
		if (selfapply == null || selfapply.length() == 0)
			selfapply = "0";
		try {
			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			sql.append("select * from ");
			if ("1".equals(selfapply)) {
				sql.append("g_templet_");
				sql.append(tabid + " where ");
				sql.append(" basepre='");
				sql.append(this.userView.getDbname());
				sql.append("' and a0100='");
				sql.append(this.userView.getA0100());
				sql.append("'");
			} else {

				if (!"0".equalsIgnoreCase(task_id)) // 审批表中的数据
				{
					sql.append("templet_");
					sql.append(tabid + " where 1=1 ");
					StringBuffer strins = new StringBuffer();
					for (int i = 0; i < inslist.size(); i++) {
						if (i != 0)
							strins.append(",");
						strins.append((String) inslist.get(i));
					}
					sql.append(" and ins_id in(");
					sql.append(strins.toString());
					sql.append(")");
					sql.append(
							" and seqnum in( select seqnum from t_wf_task_objlink where submitflag=1 and task_id in ("
									+ task_id + ")  ) ");
				} else {
					sql.append(this.userView.getUserName());
					sql.append("templet_");
					sql.append(tabid + " where 1=1 ");
					sql.append(" and submitflag=1 ");
				}

				if ("1".equals(infor_type))
					sql.append(" order by a0100");
				else if ("2".equals(infor_type))
					sql.append(" order by b0110");
				else if ("3".equals(infor_type))
					sql.append(" order by e01a1");
			}
			rset = dao.search(sql.toString());
			while (rset.next()) {
				if ("1".equals(infor_type)) {
					//记录ins_id 解析审批过程时用。
					if("1".equals(selfapply)||"0".equalsIgnoreCase(task_id)){
						String pre = rset.getString("basepre").toLowerCase();
						a0100lists.add(pre + "`" + rset.getString("a0100"));
					}
					else{
						String pre = rset.getString("basepre").toLowerCase();
						a0100lists.add(pre + "`" + rset.getString("a0100")+"`"+rset.getString("ins_id"));
					}
					
				} else if ("2".equals(infor_type)) {
					if("1".equals(selfapply)||"0".equalsIgnoreCase(task_id))
						a0100lists.add(rset.getString("b0110"));
					else
						a0100lists.add(rset.getString("b0110")+"`"+rset.getString("ins_id"));
				} else if ("3".equals(infor_type)) {

					if("1".equals(selfapply)||"0".equalsIgnoreCase(task_id))
						a0100lists.add(rset.getString("e01a1"));
					else
						a0100lists.add(rset.getString("e01a1")+"`"+rset.getString("ins_id"));

				}

			} // for i loop end.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);
		}

		/** 加规则过滤 */
		ArrayList alUsedFields = null;
		String temptable = null;
		if (llexpr != null && llexpr.trim().length() > 0) {
			alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			temptable = createSearchTempTable(this.conn, infor_type);
		}

		Iterator iterator = hm.entrySet().iterator();
		StringBuffer judgeSql = new StringBuffer();

		while (iterator.hasNext()) {

			Entry entry = (Entry) iterator.next();
			String pre = entry.getKey().toString();
			ArrayList a0100list = (ArrayList) entry.getValue();

			String key = "a0100";
			if ("1".equals(infor_type)) {
				judgeSql.append("select A0101 from ");
				judgeSql.append(pre);
				judgeSql.append("A01 where ");
			} else if ("2".equals(infor_type)) {
				judgeSql.append("select B0110 from B01 where ");
				key = "b0110";
			} else if ("3".equals(infor_type)) {
				judgeSql.append("select E01A1 from K01 where ");
				key = "e01a1";
			}

			if (!"1".equals(selfapply)) {
				StringBuffer _sql = new StringBuffer("");

				if (!"0".equalsIgnoreCase(task_id)) // 审批表中的数据
				{
					_sql.append("select " + key + " from ");
					_sql.append("templet_");
					_sql.append(tabid + " where 1=1 and submitflag=1  ");
					StringBuffer strins = new StringBuffer();
					for (int i = 0; i < inslist.size(); i++) {
						if (i != 0)
							strins.append(",");
						strins.append((String) inslist.get(i));
					}
					_sql.append(" and ins_id in(");
					_sql.append(strins.toString());
					_sql.append(")");
				} else {
					_sql.append("select " + key + " from ");
					_sql.append(this.userView.getUserName());
					_sql.append("templet_");
					_sql.append(tabid + " where 1=1 and submitflag=1  ");
				}
				if ("1".equals(infor_type))
					_sql.append(" and lower(basepre)='" + pre.toLowerCase() + "'");

				judgeSql.append(" " + key + " in ( " + _sql.toString() + " ) ");

			} else {
				judgeSql.append(" " + key + " in(''");
				for (int i = 0; i < a0100list.size(); i++) {
					judgeSql.append(",'");
					judgeSql.append(a0100list.get(i));
					judgeSql.append("'");
				}
				judgeSql.append(")");
			}

			judgeSql.append(" and ");
			judgeSql.append(getFilterSQL(temptable, pre, alUsedFields, llexpr, infor_type));
			judgeSql.append(" UNION ");
			// System.out.println(a0100list + pre);
		}
		if (judgeSql.length() > 7) {
			judgeSql.setLength(judgeSql.length() - 7);
			ContentDAO dao = new ContentDAO(this.conn);
			String judgedesc = "";
			boolean bl = false;

			try {
				// System.out.println(judgeSql.toString() + llexpr);
				rset = dao.search(judgeSql.toString());

				while (rset.next()) {
					if ("1".equals(infor_type)) {
						if (bl == false) {
							judgedesc = rset.getString("a0101");
							bl = true;
						} else {
							judgedesc += "," + rset.getString("a0101");
						}
					} else if ("2".equals(infor_type)) {
						String codeitemid = rset.getString("b0110");
						String codeitemdesc = AdminCode.getCodeName("UN", codeitemid);
						if (codeitemdesc == null || codeitemdesc.trim().length() == 0)
							codeitemdesc = AdminCode.getCodeName("UM", codeitemid);
						if (bl == false) {
							judgedesc = codeitemdesc;
							bl = true;
						} else {
							judgedesc += "," + codeitemdesc;
						}
					} else if ("3".equals(infor_type)) {
						String codeitemid = rset.getString("E01A1");
						String codeitemdesc = AdminCode.getCodeName("@K", codeitemid);
						if (bl == false) {
							judgedesc = codeitemdesc;
							bl = true;
						} else {
							judgedesc += "," + codeitemdesc;
						}
					}
				}
				if (bl) {
					// this.getFormHM().put("sp_flag","3");
					judgeisllexpr = judgedesc + ResourceFactory.getProperty("general.template.ishavenotjudge");
				} else {
					judgeisllexpr = "1";
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				PubFunc.closeDbObj(rset);
			}
		} else {
			judgeisllexpr = "1";
		}

		return a0100lists;
	}

	private String getFilterSQL(String temptable, String BasePre, ArrayList alUsedFields, String llexpr,
			String infor_type) {
		String sql = " (1=2)";
		try {
			if (llexpr != null && llexpr.length() > 0) {
				ContentDAO dao = new ContentDAO(this.conn);
				StringBuffer inserSql = new StringBuffer();
				if ("1".equals(infor_type)) {
					inserSql.append("insert into ");
					inserSql.append(temptable);
					inserSql.append("(a0100) select '");
					inserSql.append(BasePre);
					inserSql.append("'" + Sql_switcher.concat() + "a0100 from ");

					// this.filterfactor="性别 <> '1'";
					int infoGroup = 0; // forPerson 人员
					int varType = 8; // logic
					String whereIN = InfoUtils.getWhereINSql(this.userView, BasePre);
					whereIN = "select a0100 " + whereIN;
					YksjParser yp = new YksjParser(this.userView, alUsedFields, YksjParser.forSearch, varType,
							infoGroup, "Ht", BasePre);
					YearMonthCount ymc = null;
					yp.run_Where(llexpr, ymc, "", "", dao, whereIN, this.conn, "A", null);
					String tempTableName = yp.getTempTableName();
					sql = "('" + BasePre + "'" + Sql_switcher.concat() + BasePre
							+ "A01.a0100 in  (select distinct a0100 from " + temptable + "))";
					inserSql.append(tempTableName);
					inserSql.append(" where " + yp.getSQL());
				} else if ("2".equals(infor_type)) {
					inserSql.append("insert into ");
					inserSql.append(temptable);
					inserSql.append("(b0110) select b0110 from ");

					// this.filterfactor="性别 <> '1'";
					int infoGroup = 0; // forPerson 人员
					int varType = 8; // logic
					String whereIN = InfoUtils.getWhereInOrgSql(this.userView, "2");
					whereIN = "select b0110 " + whereIN;
					YksjParser yp = new YksjParser(this.userView, alUsedFields, YksjParser.forSearch, varType,
							YksjParser.forUnit, "Ht", BasePre);
					YearMonthCount ymc = null;
					yp.run_Where(llexpr, ymc, "", "", dao, whereIN, this.conn, "A", null);
					String tempTableName = yp.getTempTableName();
					sql = "( B01.b0110 in  (select distinct b0110 from " + temptable + "))";
					inserSql.append(tempTableName);
					inserSql.append(" where " + yp.getSQL());

				} else if ("3".equals(infor_type)) {
					inserSql.append("insert into ");
					inserSql.append(temptable);
					inserSql.append("(e01a1) select e01a1 from ");

					// this.filterfactor="性别 <> '1'";
					int infoGroup = 0; // forPerson 人员
					int varType = 8; // logic
					String whereIN = InfoUtils.getWhereInOrgSql(this.userView, "3");
					whereIN = "select e01a1 " + whereIN;
					YksjParser yp = new YksjParser(this.userView, alUsedFields, YksjParser.forSearch, varType,
							YksjParser.forPosition, "Ht", BasePre);
					YearMonthCount ymc = null;
					yp.run_Where(llexpr, ymc, "", "", dao, whereIN, this.conn, "A", null);
					String tempTableName = yp.getTempTableName();
					sql = "( K01.e01a1 in  (select distinct e01a1 from " + temptable + "))";
					inserSql.append(tempTableName);
					inserSql.append(" where " + yp.getSQL());
				}
				dao.insert(inserSql.toString(), new ArrayList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	private String createSearchTempTable(Connection conn, String infor_type) {
		String temptable = "temp_search_xry_01";
		try {
			StringBuffer sql = new StringBuffer();
			sql.delete(0, sql.length());
			sql.append("drop table ");
			sql.append(temptable);
			try {
				ExecuteSQL.createTable(sql.toString(), conn);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			sql.delete(0, sql.length());
			sql.append("CREATE TABLE ");
			sql.append(temptable);
			if ("1".equals(infor_type))
				sql.append("(a0100  varchar (100) )");
			else if ("2".equals(infor_type))
				sql.append("(b0110  varchar (100) )");
			else if ("3".equals(infor_type))
				sql.append("(e01a1  varchar (100) )");
			try {
				ExecuteSQL.createTable(sql.toString(), conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temptable;
	}

	private void setReadFlag(String taskid) {
		if (taskid == null || "".equals(taskid) || "0".equals(taskid))
			return;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo taskvo = new RecordVo("t_wf_task");
			taskvo.setInt("task_id", Integer.parseInt(taskid));
			taskvo.setInt("bread", 1);
			dao.updateValueObject(taskvo);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String createTemplateFile(String idnum) throws Exception {
		File tempFile = null;
		String filename = "";
		ServletUtilities.createTempDir();
		ResultSet rs = null;
		Connection conn = null;
		java.io.FileOutputStream fout = null;
		InputStream in = null;
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select content from t_wf_template");
			strsql.append(" where tp_id=");
			strsql.append(idnum);
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);

			rs = dao.search(strsql.toString());
			// System.out.println("SQL="+strsql.toString());
			if (rs.next()) {
				tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, ".doc",
						new File(System.getProperty("java.io.tmpdir")));
				in = rs.getBinaryStream("content");
				fout = new java.io.FileOutputStream(tempFile);

				int len;
				byte[] buf = new byte[1024];

				while ((len = in.read(buf, 0, 1024)) != -1) {
					fout.write(buf, 0, len);

				}
				fout.close();
				filename = tempFile.getName();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fout != null)
				PubFunc.closeIoResource(fout);
			if (in != null)
				PubFunc.closeIoResource(in);
			if (rs != null)
				rs.close();

			if (conn != null)
				conn.close();
		}
		return filename;
	}

	/**
	 * 根据task_id的值查询出ins_id和一些值，放入任务列表中
	 * 
	 * @param task_ids
	 *            任务编号，以","分割组成
	 */
	private ArrayList getIns_id(String task_ids) {
		ArrayList insList = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			if (task_ids != null && task_ids.length() > 0) {
				if (task_ids.indexOf(",") == 0) {
					task_ids = task_ids.substring(1, task_ids.length());
				}
				String[] task_idArr = task_ids.split(",");
				for (int i = 0; i < task_idArr.length; i++) {
					int task_id = Integer.parseInt(task_idArr[i]);
					RecordVo vo = new RecordVo("t_wf_task");
					vo.setInt("task_id", task_id);
					vo = dao.findByPrimaryKey(vo);
					insList.add(vo.getString("ins_id"));
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return insList;
	}
	private void selectNameByA0100(String tablename,String insidStr,String preA0100Str,String task_id,HashMap a0101Map,TemplateTableBo tablebo){
		ContentDAO dao = new ContentDAO(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		String sql="select * from "+tablename+" where lower(basepre"+Sql_switcher.concat()+"a0100) in("+preA0100Str.substring(0,preA0100Str.length()-1)+")";
		if(!("0".equals(task_id)))
			sql+=" and ins_id in("+insidStr.substring(0,insidStr.length()-1)+")";
		RowSet rowset=null;
		try{
			insidStr="";
			preA0100Str="";
			rowset= dao.search(sql);
			while(rowset.next()){
				if (tablebo.getOperationtype() == 0) {//人员调入型
					if (dbw.isExistField(tablename, "a0101_2", false)) {
						a0101Map.put((rowset.getString("basepre")+rowset.getString("a0100")).toLowerCase(), rowset.getString("a0101_2"));
					}
				} else {
					a0101Map.put((rowset.getString("basepre")+rowset.getString("a0100")).toLowerCase(), rowset.getString("a0101_1"));
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			PubFunc.closeDbObj(rowset);
		}
	}
}
