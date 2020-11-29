/*
 * Created on 2005-5-19
 *
 */
package com.hjsj.hrms.transaction.board;

import com.hjsj.hrms.businessobject.board.BoardBo;
import com.hjsj.hrms.businessobject.board.SendBoard;
import com.hjsj.hrms.businessobject.board.SendMsgToWXBo;
import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.transaction.lawbase.CommonBusiness;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class SaveBoardTrans extends IBusiness {
	private String annouce = "";// 通知类型 1 ehr系统公告栏 2 招聘首页公告 3 社会招聘公告 4 校园招聘公告 11 培训新闻
	private String op = "";// 接口 1 公告栏维护 2 招聘公告、培训新闻
	private String type = "";// 现在 社会招聘公告 ,
								// 校园招聘公从数据库查，不是写死，这里为了区别是不是首页公告和招聘公示（首页公告和招聘公示通过flag区别，其他的通过other_flag区别）是否是数据库中的取招聘对象0：不是1：是

	public void execute() throws GeneralException {
		InputStream in = null;
		try {
			RecordVo vo = (RecordVo) this.getFormHM().get("boardov");
			FormFile file = (FormFile) this.getFormHM().get("file");
			String opt = (String) this.getFormHM().get("opt");
			// 2014.11.7 xxd 文件上传参数过滤
			opt = PubFunc.hireKeyWord_filter(opt);
			type = (String) this.getFormHM().get("type");
			String announce = (String) this.getFormHM().get("announce");
			announce = PubFunc.hireKeyWord_filter(announce);
			announce = announce == null || announce.length() < 1 ? "1" : announce;
			annouce = announce;
			op = opt;
			if (vo == null)
				return;
			String flag = (String) this.getFormHM().get("flag");
			flag = PubFunc.hireKeyWord_filter(flag);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String content = vo.getString("content");

			content = PubFunc.stripScriptXss(content);

			/** lizhenwei add 2008/07/11 解决这几个字符变成别的形式的问题 */
			if (content != null) {
				content = content.replaceAll("&sup1;", "1");
				content = content.replaceAll("&sup2;", "2");
				content = content.replaceAll("&sup3;", "3");
				content = content.replaceAll("&ordm;", "o");
				content = content.replaceAll("&acirc;", "a");
				content = content.replaceAll("&eth;", "d");
				content = content.replaceAll("&yacute;", "y");
				content = content.replaceAll("&thorn;", "t");
				content = content.replaceAll("&ETH;", "D");
				content = content.replaceAll("&THORN;", "T");
				content = content.replaceAll("&Yacute;", "Y");
			}
			vo.setString("content", content);
			//opt 1 公告栏维护 2 招聘公告、培训新闻
			if (opt != null) {
				if ("1".equalsIgnoreCase(opt)) {
					if (announce != null) {
						vo.setInt("flag", 1);
					}
				} else {
					if (announce != null) {
						if (StringUtils.isNotBlank(type) && "1".equals(type)) {
							vo.setString("other_flag", this.annouce);
							vo.setInt("flag", 3);
						} else
							vo.setInt("flag", Integer.parseInt(announce));
					}
				}
			}
			// 判断是否设置了文件存储
			boolean existPath = VfsService.existPath();
			if (!existPath)
				throw GeneralExceptionHandler.Handle(new Throwable("请配置文件存放目录！"));

			// 公告，过滤主题中的特殊字符，负责主页公告展示会造成安全问题 jingq upd 2014.09.29
			vo.setString("topic", PubFunc.hireKeyWord_filter(vo.getString("topic")));

			// 公告维护，审批，点击保存出现空指针错误 jingq upd 2014.09.28
			boolean accept = true;
			if (file != null && file.getFileSize() > 0) {
				accept = FileTypeUtil.isFileTypeEqual(file);
				if (!accept) 
				{
					throw GeneralExceptionHandler
					.Handle(new Throwable(ResourceFactory.getProperty("error.fileuploaderror")));
				}
				
				//vfs保存文件start
				String username = userView.getUserName();
				VfsFiletypeEnum vfsFiletypeEnum = null;
				VfsModulesEnum vfsModulesEnum = null;
				// 文件所属类型
				VfsCategoryEnum vfsCategoryEnum = null;
				String guidkey = "";
				//培训新闻没有附件
				if (!"11".equals(annouce)) {
					in = file.getInputStream();
				}
				String fileName = "";
				String fileTag = "";
				//文件加密id
				String fileid = "";
				//公告id为空返回
				if(StringUtils.isBlank(vo.getString("id"))) {
					return;
				}
				//公告附件扩展标识
				String fileType = "";
				
				if (file != null && file.getFileSize() > 0 && existPath) {
					fileName = file.getFileName();
					fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
					vfsFiletypeEnum = VfsFiletypeEnum.other;
					vfsModulesEnum = VfsModulesEnum.NOLOGIN;
					// 文件所属类型
					vfsCategoryEnum = VfsCategoryEnum.other;
					guidkey = "";
					in = file.getInputStream();
					fileTag = "announce_"+vo.getString("id");
					boolean isTempFile = false;
					fileid = VfsService.addFile(username, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, guidkey, in,
							fileName, fileTag, isTempFile);
					//vfs保存文件end
					vo.setString("fileid", fileid);
					vo.setString("ext", fileType);
				}
				
			}

			//新增
			if ("1".equals(flag)) {
				/* 新增公告栏添加所属单位 b0110 */
				if (vo.hasAttribute("b0110")) {
					String b0110 = this.userView.getManagePrivCodeValue();
					if (b0110.length() < 1)
						b0110 = this.userView.getManagePrivCode();
					vo.setString("b0110", b0110);
				}
				insertDAO(vo, null, dao);
				this.getFormHM().put("boardTb", vo);
				
			}
			if ("0".equals(flag)) {//修改
				dao.updateValueObject(vo);
				update(vo, null);
				this.getFormHM().put("boardTb", vo);
			}
			// 发布、暂停、结束 后刷新外网职位列表
			EmployNetPortalBo bo = new EmployNetPortalBo(this.frameconn);
			bo.refreshStaticAttribute();
			if ("3".equals(flag)) {
				/** 点审批链接后，进行保存处理 */
				reply(vo, file, dao);
				// 【8668】公共栏维护审批批准时根据公告栏公告范围调用微信消息接口发送公告消息 jingq add 2015.04.24
				if (!"".equals(ConstantParamter.getAttribute("wx", "corpid"))
						|| !"".equals(ConstantParamter.getAttribute("DINGTALK", "corpid"))) {
					String picUrl = "http://www.hjsoft.com.cn:8089/UserFiles/Image/announce.png";
					String etoken = PubFunc.convertUrlSpecialCharacter(
							PubFunc.convertTo64Base(userView.getUserName() + "," + userView.getPassWord()));
					String url = userView.getServerurl()
							+ "/selfservice/infomanager/board/viewboard.do?b_query=link&a_id=" + vo.getString("id")
							+ "&etoken=" + etoken + "&appfwd=1";
					SendMsgToWXBo.SendBoardToWX(this.getFrameconn(), this.userView, vo.getString("id"),
							ResourceFactory.getProperty("message.weixin.board"), vo.getString("topic"), picUrl,
							url);
				}
			}
			/** 清空 */
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(in);
		}

	}

	private void update(RecordVo vo, FormFile file) throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		PreparedStatement pstmt = null;
		boolean bflag = true;
		if (file == null || file.getFileSize() == 0)
			bflag = false;
		String selectPerson = (String) this.getFormHM().get("selectPerson");
		BoardBo boardBo = new BoardBo(this.getFrameconn(), this.userView);
		String noticeperson = (String) this.getFormHM().get("noticeperson");
		this.getFormHM().put("noticeperson", noticeperson);
		this.getFormHM().put("selectPerson", selectPerson);
		InputStream in = null;
		DbSecurityImpl dbs = new DbSecurityImpl();
		try {
			if (this.op != null) {
				if ("1".equalsIgnoreCase(op)) {
					if (bflag) {
						strsql.append(
								"update announce set topic=?,content=?,createuser=?,createtime=?,period=?,approve=?,priority=?,thefile=?,ext=? where id=? and flag=?");
					} else {
						strsql.append(
								"update  announce set topic=?,content=?,createuser=?,createtime=?,period=?,approve=?,priority=? ");
						// 有ext参数，更新，没有不更新
						if (vo.getString("ext").length() > 0) {
							strsql.append(",ext=? where id=? and flag=?");
							deletOldFile(vo);
						} else
							strsql.append(" where id=? and flag=?");
					}
				} else {
					if (this.annouce != null) {
						if (bflag) {
							strsql.append(
									"update  announce set topic=?,content=?,createuser=?,createtime=?,period=?,priority=?,thefile=?,ext=? where id=? and ");
							if (StringUtils.isNotBlank(type) && "1".equals(type)) {
								strsql.append(" other_flag = ?");
							} else {
								strsql.append(" flag = ?");
							}
						} else {

							strsql.append(
									"update  announce set topic=?,content=?,createuser=?,createtime=?,period=?,priority=? ");
							// 有ext参数，更新，没有不更新
							if (vo.getString("ext").length() > 0) {
								strsql.append(",ext=? where id=? and ");
								deletOldFile(vo);
							} else
								strsql.append(" where id=? and ");
							if (StringUtils.isNotBlank(type) && "1".equals(type)) {
								strsql.append(" other_flag = ?");
							} else {
								strsql.append(" flag = ?");
							}
						}
					}
				}

			}
			pstmt = this.getFrameconn().prepareStatement(strsql.toString());
			pstmt.setString(1, vo.getString("topic"));
			pstmt.setString(2, vo.getString("content"));
			pstmt.setString(3, userView.getUserFullName());
			pstmt.setDate(4, DateUtils.getSqlDate(new Date()));
			if (this.op != null) {
				if ("1".equalsIgnoreCase(op)) {
					pstmt.setInt(5, Integer.parseInt(vo.getString("period")));
					pstmt.setInt(6, 0);
					String priority = vo.getString("priority");
					if (priority == null || priority.length() <= 0)
						priority = "9999";
					pstmt.setInt(7, Integer.parseInt(priority));
					if (bflag) {
						cat.debug("file size=" + file.getFileSize());
						/** blob字段保存,数据库中差异 */
						switch (Sql_switcher.searchDbServer()) {
						case Constant.ORACEL:
							Blob blob = getOracleBlob(vo, file);
							pstmt.setBlob(8, blob);
							break;
						default:
							in = file.getInputStream();
							pstmt.setBinaryStream(8, in, file.getFileSize());
							break;
						}
						String fname = file.getFileName();
						int indexInt = fname.lastIndexOf(".");
						String ext = fname.substring(indexInt + 1, fname.length());
						pstmt.setString(9, ext);
						pstmt.setInt(10, Integer.parseInt(vo.getString("id")));
						if (StringUtils.isNotBlank(type) && "1".equals(type)) {// 由于other_flag为字符型，这里做特殊处理
							pstmt.setString(11, this.annouce);
						} else {
							pstmt.setInt(11, Integer.parseInt(this.annouce));
						}
					} else {
						// 有ext参数，更新，没有不更新
						if (vo.getString("ext").length() > 0) {
							pstmt.setString(8, vo.getString("ext"));
							pstmt.setInt(9, Integer.parseInt(vo.getString("id")));
							if (StringUtils.isNotBlank(type) && "1".equals(type)) {
								pstmt.setString(10, this.annouce);
							} else {
								pstmt.setInt(10, Integer.parseInt(this.annouce));
							}
						} else {
							pstmt.setInt(8, Integer.parseInt(vo.getString("id")));
							if (StringUtils.isNotBlank(type) && "1".equals(type)) {
								pstmt.setString(9, this.annouce);
							} else {
								pstmt.setInt(9, Integer.parseInt(this.annouce));
							}
						}
					}
					dbs.open(this.frameconn, strsql.toString());
					pstmt.executeUpdate();
					boardBo.deletePriv(selectPerson, vo.getString("id"));// xuj 2009-10-28
																			// 修改公告栏维护时，通知对象修改不起作用，当减少通知对象时并未更新数据,在保存之前做了清空
					if (selectPerson != null && selectPerson.trim().length() > 0) {
						boardBo.savePriv(selectPerson, vo.getString("id"));
					}
				} else {
					pstmt.setInt(5, Integer.parseInt(vo.getString("period")));
					String priority = vo.getString("priority");
					if (priority == null || priority.length() <= 0)
						priority = "9999";
					pstmt.setInt(6, Integer.parseInt(priority));
					if (bflag) {
						/** blob字段保存,数据库中差异 */
						switch (Sql_switcher.searchDbServer()) {
						case Constant.ORACEL:
							Blob blob = getOracleBlob(vo, file);
							pstmt.setBlob(7, blob);
							break;
						default:
							in = file.getInputStream();
							pstmt.setBinaryStream(7, in, file.getFileSize());
							break;
						}
						String fname = file.getFileName();
						int indexInt = fname.lastIndexOf(".");
						String ext = fname.substring(indexInt + 1, fname.length());
						pstmt.setString(8, ext);
						pstmt.setInt(9, Integer.parseInt(vo.getString("id")));
						if (StringUtils.isNotBlank(type) && "1".equals(type)) {
							pstmt.setString(10, this.annouce);
						} else {
							pstmt.setInt(10, Integer.parseInt(this.annouce));
						}
					} else {
						// 有ext参数，更新，没有不更新
						if (vo.getString("ext").length() > 0) {
							pstmt.setString(7, vo.getString("ext"));
							pstmt.setInt(8, Integer.parseInt(vo.getString("id")));
							if (StringUtils.isNotBlank(type) && "1".equals(type)) {
								pstmt.setString(9, this.annouce);
							} else {
								pstmt.setInt(9, Integer.parseInt(this.annouce));
							}
						} else {
							pstmt.setInt(7, Integer.parseInt(vo.getString("id")));
							if (StringUtils.isNotBlank(type) && "1".equals(type)) {
								pstmt.setString(8, this.annouce);
							} else {
								pstmt.setInt(8, Integer.parseInt(this.annouce));
							}
						}
					}
					dbs.open(this.frameconn, strsql.toString());
					pstmt.executeUpdate();
				}
			}

		} catch (Exception ee) {
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		} finally {
			try {
				if (in != null)
					PubFunc.closeIoResource(in);
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException ee) {
				ee.printStackTrace();
			}
			dbs.close(this.frameconn);
		}
	}

	/**
	 * 重复上传不同后缀名的附件时应删除之前的文件
	 * 
	 * @param RecordVo vo
	 */
	private void deletOldFile(RecordVo vo) throws GeneralException {
		if (null == vo)
			return;
		String fileType = vo.getString("ext");
		int id = vo.getInt("id");
		StringBuffer strSearch = new StringBuffer();
		strSearch.append("select ext from announce where id=?");
		ArrayList list = new ArrayList();
		list.add(id);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		try {
			rs = dao.search(strSearch.toString(), list);
			if (rs.next()) {
				String oldext = rs.getString("ext");
				oldext = StringUtils.isEmpty(oldext) ? "" : oldext;
				if (StringUtils.isNotEmpty(oldext) && !fileType.equalsIgnoreCase(oldext)) {
					ConstantXml constantXml = new ConstantXml(this.frameconn, "FILEPATH_PARAM");
					String RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
					if (RootDir.length() < 1)
						throw GeneralExceptionHandler.Handle(new Throwable("请配置多媒体存储路径！"));
					RootDir = RootDir.endsWith(File.separator) ? RootDir : RootDir + File.separator;
					String oldpanth = RootDir + "announce" + File.separator + id + File.separator + id + "_file."
							+ oldext;
					oldpanth = oldpanth.replace("\\", File.separator);
					File file = new File(oldpanth);
					if (file.exists()) {
						file.delete();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param vo
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(RecordVo vo, FormFile file) throws FileNotFoundException, IOException {
		StringBuffer strSearch = new StringBuffer();
		strSearch.append("select thefile from announce where id='");
		strSearch.append(vo.getString("id"));
		strSearch.append("' FOR UPDATE");

		StringBuffer strInsert = new StringBuffer();
		strInsert.append("update  announce set thefile=EMPTY_BLOB() where id='");
		strInsert.append(vo.getString("id"));
		strInsert.append("'");
		OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
		Blob blob = blobutils.readBlob(strSearch.toString(), strInsert.toString(), file.getInputStream()); // readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}

	private Blob getOracleBlob1(RecordVo vo, FormFile file) throws FileNotFoundException, IOException {
		StringBuffer strSearch = new StringBuffer();
		strSearch.append("select content from law_base_file where Upper(file_id)='");
		strSearch.append(vo.getString("file_id").toUpperCase());
		strSearch.append("' FOR UPDATE");

		StringBuffer strInsert = new StringBuffer();
		strInsert.append("update  law_base_file set content=EMPTY_BLOB() where Upper(file_id)='");
		strInsert.append(vo.getString("file_id").toUpperCase());
		strInsert.append("'");
		OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
		Blob blob = null;
		InputStream stream = null;
		try {
			stream = file.getInputStream();
			blob = blobutils.readBlob(strSearch.toString(), strInsert.toString(), stream); // readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		} finally {
			PubFunc.closeResource(stream);
		}
		return blob;
	}

	/**
	 * 通过底层函数进行文件保存
	 * 
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void insertDAO(RecordVo vo, FormFile file, ContentDAO dao) throws GeneralException {
		String unitcode = (String) this.getFormHM().get("unitcode");// 得到所属单位
		boolean bflag = true;
		if (file == null || file.getFileSize() == 0)
			bflag = false;

		String selectPerson = (String) this.getFormHM().get("selectPerson");
		BoardBo boardBo = new BoardBo(this.getFrameconn(), this.userView);
		String noticeperson = (String) this.getFormHM().get("noticeperson");
		this.getFormHM().put("noticeperson", noticeperson);
		this.getFormHM().put("selectPerson", selectPerson);
		String chflag = (String) this.getFormHM().get("chflag");
		chflag = chflag != null && chflag.trim().length() > 0 ? chflag : "";

		String trainid = (String) this.getFormHM().get("trainid");
		trainid = trainid != null && trainid.trim().length() > 0 ? trainid : "";
		try {
			if ("1".equals(chflag)) {
				if (trainid != null && trainid.trim().length() > 0 && selectPerson != null
						&& selectPerson.trim().length() > 0) {
					this.op = "1";
					boardBo.saveTrainPriv(selectPerson, trainid);
				}
				vo.setString("approve", "1");
				vo.setDate("approvetime", DateStyle.getSystemTime());
			} else
				vo.setInt("approve", 0);
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String id = idg.getId("announce.id");
			vo.setInt("id", Integer.parseInt(id));
			vo.setString("createuser", this.userView.getUserFullName());
			vo.setDate("createtime", DateStyle.getSystemTime());
			vo.setInt("viewcount", 0);
			//////////////////// 增加一新列：所属单位（unitcode） 郭峰修改//////////////////////
			if (!"".equals(unitcode))
				vo.setString("unitcode", unitcode);
			/////////////////////////////////////////////////////////////////////
			String priority = vo.getString("priority");
			if (priority == null || priority.length() <= 0)
				vo.setInt("priority", 9999);
			else
				vo.setInt("priority", Integer.parseInt(priority));
			
			dao.addValueObject(vo);
			/* 暂不支持文档管理 */
			if (this.op != null && "1".equalsIgnoreCase(this.op)) {// 招聘上传文档就不自动流转到文档管理中
				// 在公告栏中发布的公告经人工指定后，其中的附件可自动转存到文档管理中
				String base_id = SystemConfig.getPropertyValue("announce_base_id");
				if (base_id != null && base_id.length() > 0 && bflag && ("").equals(trainid)) {
					RecordVo law_base_file_vo = new RecordVo("law_base_file");
					try {
						idg = new IDGenerator(2, this.getFrameconn());
						String fileid = idg.getId("law_base_file.id");
						fileid = compareForIDFOrFactory(fileid);
						String sql = "select max(fileorder) as fileorder from law_base_file where base_id ='" + base_id
								+ "'";
						this.frowset = dao.search(sql);
						String fileorderid = "";
						if (this.frowset.next())
							fileorderid = this.frowset.getString("fileorder") == null ? ""
									: this.frowset.getString("fileorder");
						if ("".equalsIgnoreCase(fileorderid.trim()))
							fileorderid = "0";
						law_base_file_vo.setString("fileorder", (Integer.parseInt(fileorderid) + 1) + "");
						// String digest = (String)this.getFormHM().get("digest");
						// law_base_file_vo.setString("digest", digest);
						law_base_file_vo.setString("file_id", fileid);
						law_base_file_vo.setString("base_id", base_id);
						law_base_file_vo.setDate("issue_date", PubFunc.FormatDate(new Date(), "yyyy-MM-dd"));
						law_base_file_vo.setInt("viewcount", 0);
						law_base_file_vo.setString("valid", "1");
						law_base_file_vo.setString("name", vo.getString("topic"));
						law_base_file_vo.setString("title", vo.getString("topic"));
						law_base_file_vo.setString("note_num", "来自公告栏上传的附件");
					} catch (Exception e) {
						throw GeneralExceptionHandler.Handle(e);
					}
					announce_base(law_base_file_vo, file, dao);
				}

				if (!(this.userView.isSuper_admin())) {
					UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
					user_bo.saveResource(vo.getString("id"), this.userView, IResourceConstant.ANNOUNCE);
				}
				if (selectPerson != null && selectPerson.trim().length() > 0) {
					boardBo.savePriv(selectPerson, Integer.parseInt(id) + "");
				}
			}
		} catch (Exception ee) {
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		}
	}


	/**
	 * @param vo
	 * @param approve
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void reply(RecordVo vo, FormFile file, ContentDAO dao) throws GeneralException {
		RecordVo reply_vo = new RecordVo("announce");
		try {
			String approve = vo.getString("approve");
			reply_vo.setInt("id", Integer.parseInt(vo.getString("id")));
			reply_vo.setString("approve", approve);
			reply_vo.setString("approveuser", this.userView.getUserFullName());
			reply_vo.setDate("approvetime", DateStyle.getSystemTime());
			cat.debug("reply_update_boardvo=" + reply_vo.toString());
			// System.out.println("审批修改记录");
			dao.updateValueObject(reply_vo);
			SendBoard sb = new SendBoard(this.frameconn, this.userView);
			// System.out.println("审批调用接口");
			if ("1".equals(approve)) {// 新增
				sb.addBoard(vo.getString("id"));
			} else { // 删除
				sb.delBoard(vo.getString("id"));
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		// System.out.println("审批结束");
	}

	private void announce_base(RecordVo vo, FormFile file, ContentDAO dao) throws Exception {
		String filename = vo.getString("name");
		String id = vo.getString("file_id");
		String base_id = vo.getString("base_id");
		String fname = file.getFileName();
		int indexInt = fname.lastIndexOf(".");
		String ext = fname.substring(indexInt + 1, fname.length());
		vo.setString("ext", ext);
		if (filename == null || "".equals(filename.trim()) || "null".equals(filename)) {
			vo.setString("name", fname.substring(0, indexInt));
		}
		String path = "";

		File f = null;
		Document doc = null;
		InputStream is = null;
		IndexWriter writer = null;
		try {
			is = file.getInputStream();
			/** blob字段保存,数据库中差异 */
			switch (Sql_switcher.searchDbServer()) {
			case Constant.ORACEL:
				path = LawDirectory.getLawbaseDir();
				// 索引默认路径

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
					try {
						doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.wordToText(is))));
					} catch (Exception e) {
						Exception ex = new Exception("您的word文件有问题，请尽量使用纯文字，不要带图片或者乱码！建议您新建word，将原有的内容重新拷贝进去");
						throw GeneralExceptionHandler.Handle(ex);
					}
				}
				if ("xls".equals(ext.trim().toLowerCase()) || "xlsx".equals(ext.trim().toLowerCase())) {
					doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.excelToText(is))));
				}
				if ("txt".equals(ext.trim().toLowerCase()) || "html".equals(ext.trim().toLowerCase())) {
					doc.add(Field.Text("body", (Reader) new InputStreamReader(is)));
				}
				// 将文档写入索引
				writer.addDocument(doc);
				// 索引优化
				// writer.optimize();
				// 关闭写索引器
				break;
			default:
				byte[] data = file.getFileData();
				vo.setObject("content", data);

				path = LawDirectory.getLawbaseDir();
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
					try {
						doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.wordToText(is))));
					} catch (Exception e) {
						Exception ex = new Exception("您的word文件有问题，请尽量使用纯文字，不要带图片或者乱码！建议您新建word，将原有的内容重新拷贝进去");
						throw GeneralExceptionHandler.Handle(ex);
					} finally {
						PubFunc.closeIoResource(is);
					}

				}
				if ("xls".equals(ext.trim().toLowerCase()) || "xlsx".equals(ext.trim().toLowerCase())) {
					doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.excelToText(is))));
				}
				if ("txt".equals(ext.trim().toLowerCase()) || "html".equals(ext.trim().toLowerCase())) {
					doc.add(Field.Text("body", (Reader) new InputStreamReader(is)));
				}
				// 将文档写入索引
				writer.addDocument(doc);
				// 索引优化
				// writer.optimize();

				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(is);
			writer.close();
		}
		if (vo != null)
			dao.addValueObject(vo);
		if ((Sql_switcher.searchDbServer() == Constant.ORACEL || Sql_switcher.searchDbServer() == Constant.DB2)) {
			RecordVo updatevo = new RecordVo("law_base_file");
			updatevo.setString("file_id", id);
			Blob blob = getOracleBlob1(updatevo, file);
			updatevo.setObject("content", blob);
			dao.updateValueObject(updatevo);
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

}
