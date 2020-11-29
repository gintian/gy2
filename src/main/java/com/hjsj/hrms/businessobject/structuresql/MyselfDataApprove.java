package com.hjsj.hrms.businessobject.structuresql;

import com.hjsj.hrms.businessobject.general.approve.personinfo.PersonInfoBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyselfDataApprove {
	private ContentDAO dao;
	private UserView UserView;
	private Document doc;
	private Connection conn;
	private ArrayList setidList = new ArrayList();
	private String sp_idea;
	private Document idea;
	private String sp_flag;
	private String record_spflag;
	private String sequence;
	private String type;
	private ArrayList keyvalueList = new ArrayList();
	private ArrayList typeList = new ArrayList();
	private ArrayList sp_flaglueList = new ArrayList();
	private ArrayList newValueList = new ArrayList();
	private ArrayList oldValueList = new ArrayList();
	private ArrayList sequenceList = new ArrayList();
	private String chg_id;
	private boolean isChecked_may_reject = false;
	private String redundantInfo="";
	// 只是在批量批准时超编但不修改数据时判断structureExecSqlString.db.redundantInfo.length()!=0
	private StructureExecSqlString structureExecSqlString = null; 
	
	private ArrayList fileInfo;
	private String insertSequence;
	//批注信息
	private String commentValue;
	
	public String getCommentValue() {
        return commentValue;
    }

    public void setCommentValue(String commentValue) {
        this.commentValue = commentValue;
    }

    //上传的附件的信息
	private ArrayList multimediaInfoList = new ArrayList();
	
	public String getInsertSequence() {
		return insertSequence;
	}

	public void setInsertSequence(String insertSequence) {
		this.insertSequence = insertSequence;
	}

	public void setFileInfo(ArrayList fileInfo) {
		this.fileInfo = fileInfo;
	}

	public String getRedundantInfo() {
		return redundantInfo;
	}

	public void setRedundantInfo(String redundantInfo) {
		this.redundantInfo = redundantInfo;
	}

	public StructureExecSqlString getStructureExecSqlString() {
		return structureExecSqlString;
	}

	public void setStructureExecSqlString(
			StructureExecSqlString structureExecSqlString) {
		this.structureExecSqlString = structureExecSqlString;
	}

	public String getChg_id() {
		return chg_id;
	}

	public void setChg_id(String chg_id) {
		this.chg_id = chg_id;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public MyselfDataApprove() {

	}

	public MyselfDataApprove(Connection conn, UserView userView) {
		ContentDAO dao = new ContentDAO(conn);
		this.dao = dao;
		this.conn = conn;
		this.UserView = userView;
		String may_reject = SystemConfig.getPropertyValue("checked_may_reject");// 审核后可驳回
																				// 2010.01.08
																				// s.xin加
		may_reject = may_reject != null && may_reject.trim().length() > 0 ? may_reject
				: "false";
		if (may_reject != null || "true".equalsIgnoreCase(may_reject))
			this.isChecked_may_reject = true;
		else
			this.isChecked_may_reject = false;
	}

	public MyselfDataApprove(Connection conn, UserView userView, String nbase,
			String a0100) {
		ContentDAO dao = new ContentDAO(conn);
		this.dao = dao;
		this.conn = conn;
		this.UserView = userView;
		queryInti(nbase, a0100);
	}
	
	public MyselfDataApprove(Connection conn, UserView userView,String chg_id) {
		ContentDAO dao = new ContentDAO(conn);
		this.dao = dao;
		this.conn = conn;
		this.UserView = userView;
		queryInti(chg_id);
	}

	/**
	 * 删除记录
	 * 
	 * @param chg_id
	 * @return
	 */
	public boolean deleteMyselfData(String chg_id) {
		boolean isCorrect = false;
		String sql = "delete from t_hr_mydata_chg where chg_id='" + chg_id
				+ "'";
		try {
			this.dao.delete(sql, new ArrayList());
			isCorrect = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isCorrect;
	}

	/**
	 * 返回一个子集的一条记录的修改信息
	 * 
	 * @param chg_id
	 * @param fieldsteid
	 * @param keyvalue
	 * @param sequence
	 *            序号
	 * @param flag
	 *            //是否显示没有修改的信息
	 */
	public void getOneMyselfData(String chg_id, String fieldsteid,
			String keyvalue, String type, String sequence, String flag) {
		queryInti(chg_id);
		ArrayList newlist = new ArrayList();
		ArrayList oldlist = new ArrayList();
		ArrayList multimediaInfoList = new ArrayList();
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + keyvalue + "' and @type='"
						+ type + "' and @sequence='" + sequence + "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element recordE = (Element) r.next();
					this.setRecord_spflag(recordE.getAttributeValue("sp_flag"));
					this.setType(recordE.getAttributeValue("type"));
					this.setSequence(recordE.getAttributeValue("sequence"));
					List columnlist = recordE.getChildren("column");
					Iterator c = columnlist.iterator();
					FieldItem newitem = null;
					FieldItem olditem = null;
					while (c.hasNext()) {
						Element columnE = (Element) c.next();
						String itemid = columnE.getAttributeValue("name");						
						//tianye update 查询已构库的指标
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem==null || "0".equals(fielditem.getUseflag())){
							continue;
						}
						//tianye end;
						newitem = (FieldItem) fielditem.clone();
						olditem = (FieldItem) fielditem.clone();
						String newvalue = columnE.getAttributeValue("newvalue");
						newvalue = newvalue != null
								&& newvalue.trim().length() > 0 ? newvalue : "";
						String oldvalue = columnE.getAttributeValue("oldvalue");
						oldvalue = oldvalue != null
								&& oldvalue.trim().length() > 0 ? oldvalue : "";
						if (flag!=null&& "0".equals(flag)) {
							if (!newvalue.equals(oldvalue)) {
								newitem.setValue(newvalue);
								olditem.setValue(oldvalue);
								newlist.add(newitem);
								oldlist.add(olditem);
							}
						} else {
							newitem.setValue(newvalue);
							olditem.setValue(oldvalue);
							newlist.add(newitem);
							oldlist.add(olditem);
						}
					}
					
					List multimediaList = recordE.getChildren("multimedia");
					for(int i = 0; i < multimediaList.size(); i++) {
					    Element multimedia = (Element) multimediaList.get(i);
					    String fileName = multimedia.getAttributeValue("filename");
					    String filePath = multimedia.getAttributeValue("path");
					    String topic = multimedia.getAttributeValue("topic");
					    String fileType = multimedia.getAttributeValue("type");
					    LazyDynaBean bean = new LazyDynaBean();
					    bean.set("fileName", fileName);
					    bean.set("filePath", filePath);
					    bean.set("topic", topic);
					    bean.set("fileType", fileType);
					    multimediaInfoList.add(bean);
					}
				}
			}

		} catch (JDOMException e) {
			
			 e.printStackTrace();
		}
		this.setNewValueList(newlist);
		this.setOldValueList(oldlist);
		this.setMultimediaInfoList(multimediaInfoList);
	}

	/**
	 * 返回主集的修改信息
	 * 
	 * @param chg_id
	 * @param tablename
	 * @param fieldsteid
	 * @param keyvalue
	 * @return
	 */
	public List getOneMyselfDataA01(String chg_id, String userbase,
			String a0100, String fieldsteid, UserView userView,
			List infoFieldList) {
		queryInti(chg_id);
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + a0100 + "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element recordE = (Element) r.next();
					this.setRecord_spflag(recordE.getAttributeValue("sp_flag"));
					this.setType(recordE.getAttributeValue("type"));
					this.setSequence(recordE.getAttributeValue("sequence"));
					List columnlist = recordE.getChildren("column");
					Iterator c = columnlist.iterator();
					while (c.hasNext()) {
						Element columnE = (Element) c.next();

						String itemid = columnE.getAttributeValue("name");
						String newvalue = columnE.getAttributeValue("newvalue");
						newvalue = newvalue != null
								&& newvalue.trim().length() > 0 ? newvalue : "";
						String oldvalue = columnE.getAttributeValue("oldvalue");
						oldvalue = oldvalue != null
								&& oldvalue.trim().length() > 0 ? oldvalue : "";
						/**
						 * 申请修改后并保存后，如果通过记录录入直接修改了人员信息，然后将之前申请修改的数据报批，会将在记录录入修改的信息还原。
						 * 原因是申请修改保存的是旧时的信息，报批时因为跟现在数据库里的数据不一致了，程序认为做了修改操作。
						 * 解决方法：当变动信息记录中新旧值一样时，说明当时没有修改此指标，那么，跳过读取此指标当时保存的修改信息，直接使用最新值
						 * guodd 2015-07-07	
						 */
						if(newvalue.equals(oldvalue))
							continue;
						for (int i = 0; i < infoFieldList.size(); i++) {
							FieldItemView fieldItemView = (FieldItemView) infoFieldList
									.get(i);
							if (fieldItemView.getItemid().equals(itemid)) {
								//tianye update 查询已构库的指标
								FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
								if(fieldItem==null || "0".equals(fieldItem.getUseflag())){
									continue;
								}
								//tianye end;
								if ("A".equals(fieldItem.getItemtype())
										|| "M".equals(fieldItem.getItemtype())) {
									if (!"0".equals(fieldItem.getCodesetid())) {
										String codevalue = newvalue;
										if (codevalue != null
												&& codevalue.trim().length() > 0
												&& fieldItem.getCodesetid() != null
												&& fieldItem.getCodesetid()
														.trim().length() > 0){
											//tianye update start
											//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
											String name = "";
											if(!"e0122".equalsIgnoreCase(fieldItem.getItemid())){
												CodeItem codeItem = InfoUtils.getUMOrUN(fieldItem.getCodesetid(),codevalue);
												name = (codeItem!=null ? codeItem.getCodename(): "");
											}else{
												name = AdminCode.getCodeName(fieldItem.getCodesetid(), codevalue);
											}
											fieldItemView.setViewvalue(name);
											//end
										}else
											fieldItemView.setViewvalue("");

									} else {
										fieldItemView.setViewvalue(newvalue);
									}
									fieldItemView.setValue(newvalue);
								} else if ("D".equals(fieldItem.getItemtype())) // 日期型有待格式化处理
								{
									if (newvalue.length() >= 10
											&& fieldItem.getItemlength() == 10) {
										fieldItemView
												.setViewvalue(new FormatValue()
														.format(
																fieldItem,
																newvalue
																		.substring(
																				0,
																				10)));
										fieldItemView
												.setValue(new FormatValue()
														.format(
																fieldItem,
																newvalue
																		.substring(
																				0,
																				10)));
									} else if (newvalue.length() >= 10
											&& fieldItem.getItemlength() == 4) {
										fieldItemView
												.setViewvalue(new FormatValue()
														.format(
																fieldItem,
																newvalue
																		.substring(
																				0,
																				4)));
										fieldItemView
												.setValue(new FormatValue()
														.format(
																fieldItem,
																newvalue
																		.substring(
																				0,
																				4)));
									} else if (newvalue.length() >= 10
											&& fieldItem.getItemlength() == 7) {
										fieldItemView
												.setViewvalue(new FormatValue()
														.format(
																fieldItem,
																newvalue
																		.substring(
																				0,
																				7)));
										fieldItemView
												.setValue(new FormatValue()
														.format(
																fieldItem,
																newvalue
																		.substring(
																				0,
																				7)));
									} else {
										fieldItemView.setViewvalue(newvalue);
										fieldItemView.setValue(newvalue);
									}
								} else // 数值类型的有待格式化处理
								{
									fieldItemView
											.setValue(PubFunc.DoFormatDecimal(
													newvalue, fieldItem
															.getDecimalwidth()));
								}

								break;
							}
						}
					}
				}
			}

		} catch (JDOMException e) {
			
			// e.printStackTrace();
		}

		return infoFieldList;
	}

	public List getOneMyselfDataOtherSubset(String chg_id, String fieldsteid,
			String keyvalue, String type, String sequence, List infoFieldList) {
		queryInti(chg_id);
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + keyvalue + "' and @type='"
						+ type + "' and @sequence='" + sequence + "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element recordE = (Element) r.next();
					this.setRecord_spflag(recordE.getAttributeValue("sp_flag"));
					this.setType(recordE.getAttributeValue("type"));
					this.setSequence(recordE.getAttributeValue("sequence"));
					List columnlist = recordE.getChildren("column");
					Iterator c = columnlist.iterator();
					while (c.hasNext()) {
						Element columnE = (Element) c.next();
						String itemid = columnE.getAttributeValue("name");
						//tianye update 查询已构库的指标
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem==null || "0".equals(fielditem.getUseflag())){
							continue;
						}
						//tianye end;
						String newvalue = columnE.getAttributeValue("newvalue");
						newvalue = newvalue != null
								&& newvalue.trim().length() > 0 ? newvalue : "";
						String oldvalue = columnE.getAttributeValue("oldvalue");
						oldvalue = oldvalue != null
								&& oldvalue.trim().length() > 0 ? oldvalue : "";
						for (int i = 0; i < infoFieldList.size(); i++) {
							FieldItemView fieldItemView = (FieldItemView) infoFieldList
									.get(i);
							if (fieldItemView.getItemid().equals(itemid)) {
								((FieldItemView) infoFieldList.get(i))
										.setValue(newvalue);
								
								FieldItem fieldItem = DataDictionary
								.getFieldItem(itemid);
						if ("A".equals(fieldItem.getItemtype())
								|| "M".equals(fieldItem.getItemtype())) {
							if (!"0".equals(fieldItem.getCodesetid())) {
								String codevalue = newvalue;
								if (codevalue != null
										&& codevalue.trim().length() > 0
										&& fieldItem.getCodesetid() != null
										&& fieldItem.getCodesetid()
												.trim().length() > 0)
									fieldItemView
											.setViewvalue(AdminCode
													.getCode(
															fieldItem
																	.getCodesetid(),
															codevalue) != null ? AdminCode
													.getCode(
															fieldItem
																	.getCodesetid(),
															codevalue)
													.getCodename()
													: "");
								else
									fieldItemView.setViewvalue("");

							} else {
								fieldItemView.setViewvalue(newvalue);
							}
							fieldItemView.setValue(newvalue);
						} else if ("D".equals(fieldItem.getItemtype())) // 日期型有待格式化处理
						{
							if (newvalue.length() >= 10
									&& fieldItem.getItemlength() == 10) {
								fieldItemView
										.setViewvalue(new FormatValue()
												.format(
														fieldItem,
														newvalue
																.substring(
																		0,
																		10)));
								fieldItemView
										.setValue(new FormatValue()
												.format(
														fieldItem,
														newvalue
																.substring(
																		0,
																		10)));
							} else if (newvalue.length() >= 10
									&& fieldItem.getItemlength() == 4) {
								fieldItemView
										.setViewvalue(new FormatValue()
												.format(
														fieldItem,
														newvalue
																.substring(
																		0,
																		4)));
								fieldItemView
										.setValue(new FormatValue()
												.format(
														fieldItem,
														newvalue
																.substring(
																		0,
																		4)));
							} else if (newvalue.length() >= 10
									&& fieldItem.getItemlength() == 7) {
								fieldItemView
										.setViewvalue(new FormatValue()
												.format(
														fieldItem,
														newvalue
																.substring(
																		0,
																		7)));
								fieldItemView
										.setValue(new FormatValue()
												.format(
														fieldItem,
														newvalue
																.substring(
																		0,
																		7)));
							} else {
								fieldItemView.setViewvalue(newvalue);
								fieldItemView.setValue(newvalue);
							}
						} else // 数值类型的有待格式化处理
						{
							fieldItemView
									.setValue(PubFunc.DoFormatDecimal(
											newvalue, fieldItem
													.getDecimalwidth()));
						}
								
								
								break;
							}
						}
					}
				}
			}

		} catch (JDOMException e) {
			
			// e.printStackTrace();
		}
		return infoFieldList;
	}

	/**
	 * 返回一个子集的一条记录的修改信息
	 * 
	 * @param chg_id
	 * @param fieldsteid
	 * @param keyvalue
	 * @param sequence
	 *            序号
	 * @param flag
	 *            //是否显示没有修改的信息
	 */
	public List getOneMyselfDataOther(String chg_id, String fieldsteid,
			String userbase, List list, String part_unit, String part_setid,String flag) {
		queryInti(chg_id);
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				List childs = fieldSetE.getChildren();
				Iterator r = childs.iterator();
				while (r.hasNext())// 有记录
				{
					Element recordE = (Element) r.next();
					String sp_flag = recordE.getAttributeValue("sp_flag");
					//【7167】自助服务-我的信息-信息维护-申请修改，选择家庭子集，已批和新建状态的内容有重的现象 jingq upd 2015.02.02
					//wangzhongjun change at 2015-04-16 12:44 for 驳回后再新增数据看不到了。bug【8822】，同时考虑bug【7167】，过滤已批的数据
//					if(flag.equals(sp_flag)){
					if(!"03".equals(sp_flag)){
						String keyvalue = recordE.getAttributeValue("keyvalue");

						int sequence = Integer.parseInt(recordE
								.getAttributeValue("sequence"));
						RecordVo vo = new RecordVo(userbase + fieldsteid, 1);
						vo.setString("id", chg_id);
						String type = recordE.getAttributeValue("type");
						if (!"02".equals(sp_flag)) {
							vo.setString("state", type);
						} else {
							vo.setString("state", "approve");
						}
						vo.setInt("i9999", sequence);
						vo.setString("a0100", keyvalue);
						// System.out.println(vo.getString("id"));
						this.setSequence(recordE.getAttributeValue("sequence"));
						List columnlist = recordE.getChildren("column");
						Iterator c = columnlist.iterator();
						while (c.hasNext()) {
							Element columnE = (Element) c.next();
							String itemid = columnE.getAttributeValue("name");
							//tianye update 查询已构库的指标
							FieldItem fielditem = DataDictionary.getFieldItem(itemid);
							if(fielditem==null || "0".equals(fielditem.getUseflag())){
								continue;
							}
							//tianye end;
							String newvalue = columnE
									.getAttributeValue("newvalue");
							newvalue = newvalue != null
									&& newvalue.trim().length() > 0 ? newvalue
									: "";
							String oldvalue = columnE
									.getAttributeValue("oldvalue");
							oldvalue = oldvalue != null
									&& oldvalue.trim().length() > 0 ? oldvalue
									: "";
							String valuess = "";
							if ("delete".equals(type)) {
								valuess = oldvalue;
							} else {
								valuess = newvalue;
							}

							if (!"0".equals(fielditem.getCodesetid())) // 是否是代码类型的
							{
								String codevalue = valuess; // 是,转换代码->数据描述
															// //是,转换代码->数据描述
								String codesetid = fielditem.getCodesetid();
								if (codevalue != null
										&& codevalue.trim().length() > 0
										&& codesetid != null
										&& codesetid.trim().length() > 0) {
									if (part_unit != null
											&& part_unit
													.equalsIgnoreCase(fielditem
															.getItemid()
															.toString())
											&& part_setid != null
											&& part_setid
													.equalsIgnoreCase(fieldsteid)) {
										String value = AdminCode.getCode("UN",
												codevalue) != null
												&& AdminCode.getCode("UN",
														codevalue)
														.getCodename() != null ? AdminCode
												.getCode("UN", codevalue)
												.getCodename()
												: "";
										if (value == null
												|| value.length() <= 0)
											value = AdminCode.getCode("UM",
													codevalue) != null
													&& AdminCode.getCode("UM",
															codevalue)
															.getCodename() != null ? AdminCode
													.getCode("UM", codevalue)
													.getCodename()
													: "";
										vo.setString(fielditem.getItemid(),
												value);
									} else {
										String value = AdminCode.getCode(
												codesetid, codevalue) != null
												&& AdminCode.getCode(codesetid,
														codevalue)
														.getCodename() != null ? AdminCode
												.getCode(codesetid, codevalue)
												.getCodename()
												: "";
										vo.setString(fielditem.getItemid(),
												value);
									}

								} else
									vo.setString(fielditem.getItemid(), "");
							} else {
								if ("D".equals(fielditem.getItemtype())) // 日期类型的有待格式化处理
								{
									if (valuess.length() >= 10
											&& fielditem.getItemlength() == 10)
										vo.setString(fielditem.getItemid()
												.toLowerCase(),
												new FormatValue().format(
														fielditem, valuess
																.substring(0,
																		10)));
									else if (valuess.length() >= 10
											&& fielditem.getItemlength() == 4)
										vo
												.setString(
														fielditem.getItemid()
																.toLowerCase(),
														new FormatValue()
																.format(
																		fielditem,
																		valuess
																				.substring(
																						0,
																						4)));
									else if (valuess.length() >= 10
											&& fielditem.getItemlength() == 7)
										vo
												.setString(
														fielditem.getItemid()
																.toLowerCase(),
														new FormatValue()
																.format(
																		fielditem,
																		valuess
																				.substring(
																						0,
																						7)));
									else
										vo.setString(fielditem.getItemid()
												.toLowerCase(), valuess);
								} else if ("N".equals(fielditem.getItemtype())) // 数值类型的
								{
									vo.setString(fielditem.getItemid(), PubFunc
											.DoFormatDecimal(valuess, fielditem
													.getDecimalwidth()));
								} else if ("M".equals(fielditem.getItemtype())) {
									String text_m = valuess;
									vo.setString(fielditem.getItemid(), text_m);
								} else // 其他字符串类型
								{
									vo
											.setString(fielditem.getItemid(),
													valuess);
								}
							}

						}
						list.add(vo);
				
					}
				}
			}

		} catch (JDOMException e) {
			
			// e.printStackTrace();
		}
		return list;
	}

	/**
	 * 返回一个子集的一条记录的修改信息
	 * 
	 * @param chg_id
	 * @param fieldsteid
	 * @param keyvalue
	 * @param sequence
	 *            序号
	 * @param flag
	 *            //是否显示没有修改的信息
	 */
	public void eidtMyselfDataOther(String chg_id, String fieldsteid,
			String keyvalue, String type, String sequence, String sp_flag,
			String userbase, List list) {
		queryInti(chg_id);
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + keyvalue + "' and @type='"
						+ type + "' and @sequence='" + sequence + "']";
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element recordE = (Element) r.next();
					if (!"02".equals(sp_flag)) {
						sp_flag = recordE.getAttributeValue("sp_flag");
					}
					recordE.setAttribute("sp_flag", sp_flag);
					List columnlist = recordE.getChildren("column");
					HashMap<String, String> columnMap = new HashMap<String, String>();
					Iterator c = columnlist.iterator();
					while (c.hasNext()) {
						Element columnE = (Element) c.next();
						String itemid = columnE.getAttributeValue("name");
						boolean removeFlag = true;
						for (int i = 0; i < list.size(); i++) {
							FieldItemView view = (FieldItemView) list.get(i);
							if (itemid.equals(view.getItemid())) {
								String newValue = view.getValue();
								if("D".equalsIgnoreCase(view.getItemtype())) {
									int length = view.getItemlength();
									newValue = dateFormatter(newValue, length);
								}
								
								newValue =PubFunc.stripScriptXss(newValue);
								newValue =PubFunc.replaceSQLkey(newValue);
								columnE.setAttribute("newvalue", newValue);
								removeFlag = false;
								break;
							}
						}
						
						if(removeFlag)
						    recordE.removeContent(columnE);
						else
						    columnMap.put(itemid, "1");
					}
					
					//【45155】将xml中没有的指标添加到xml中
					for (int i = 0; i < list.size(); i++) {
                        FieldItemView view = (FieldItemView) list.get(i);
                        if(columnMap.containsKey(view.getItemid()))
                            continue;
                        
                        Element column = new Element("column");
                        String newValue = view.getValue();
                        if("D".equalsIgnoreCase(view.getItemtype())) {
							int length = view.getItemlength();
							newValue = dateFormatter(newValue, length);
						}
                        
						newValue =PubFunc.stripScriptXss(newValue);
						newValue =PubFunc.replaceSQLkey(newValue);
                        column.setAttribute("name", view.getItemid());
                        column.setAttribute("newvalue", newValue);
                        column.setAttribute("oldvalue", "");
                        recordE.addContent(column);
                        
                    }
				}
				String xml = this.getXmlconent();
				RecordVo vo = new RecordVo("t_hr_mydata_chg");
				vo.setString("chg_id", chg_id);
				vo = this.dao.findByPrimaryKey(vo);
				vo.setString("content", xml);
				this.dao.updateValueObject(vo);

			}

		} catch (Exception e) {
			
			// e.printStackTrace();
		}
	}

	/**
	 * 日期类型的指标格式化
	 * 
	 * @param value
	 *            日期类型指标的值
	 * @param length
	 *            日期类型指标的长度
	 * @return
	 */
	private String dateFormatter(String value, int length) {
		try {
			if(StringUtils.isEmpty(value))
				return value;
			
			value = value.replace("-", ".");
			value = value.replace("/", ".");
			value = value.replace("\\", ".");
			java.util.Date date = DateUtils.getDate(value, getPattern(value.length()));
			value = DateUtils.format(date, getPattern(length));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	private String getPattern (int length) {
		String pattern = "yyyy.MM.dd";
		if (length == 4)
			pattern = "yyyy";
		else if (length == 7)
			pattern = "yyyy.MM";
		else if (length == 16)
			pattern = "yyyy.MM.dd hh:mm";
		else if (length >= 18)
			pattern = "yyyy.MM.dd hh:mm:ss";
		else
			pattern = "yyyy.MM.dd";
		
		return pattern;
	}
	/**
	 * 返回一个子集的一条记录的修改信息
	 * 
	 * @param chg_id
	 * @param fieldsteid
	 * @param keyvalue
	 * @param sequence
	 *            序号
	 * @param flag
	 *            0为不显示， //是否显示没有修改的信息
	 */
	public void getOneMyselfData(String nbase, String a0100, String fieldsteid,
			String keyvalue, String type, String sequence, String flag) {
		queryInti(nbase, a0100);
		ArrayList newlist = new ArrayList();
		ArrayList oldlist = new ArrayList();
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + keyvalue + "' and @type='"
						+ type + "' and @sequence='" + sequence + "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element recordE = (Element) r.next();
					this.setRecord_spflag(recordE.getAttributeValue("sp_flag"));
					this.setType(recordE.getAttributeValue("type"));
					this.setSequence(recordE.getAttributeValue("sequence"));
					List columnlist = recordE.getChildren("column");
					Iterator c = columnlist.iterator();
					FieldItem newitem = null;
					FieldItem olditem = null;
					while (c.hasNext()) {
						Element columnE = (Element) c.next();
						String itemid = columnE.getAttributeValue("name");
						//tianye update 查询已构库的指标
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem==null || !"1".equals(fielditem.getUseflag())){
							continue;
						}
						//tianye end;
						newitem = (FieldItem) fielditem.clone();
						olditem = (FieldItem) fielditem.clone();
						String newvalue = columnE.getAttributeValue("newvalue");
						newvalue = newvalue != null
								&& newvalue.trim().length() > 0 ? newvalue : "";
						String oldvalue = columnE.getAttributeValue("oldvalue");
						oldvalue = oldvalue != null
								&& oldvalue.trim().length() > 0 ? oldvalue : "";
						if ("0".equals(flag)) {
							if (!newvalue.equals(oldvalue)) {
								newitem.setValue(newvalue);
								olditem.setValue(oldvalue);
								newlist.add(newitem);
								oldlist.add(olditem);
							}
						} else {
							newitem.setValue(newvalue);
							olditem.setValue(oldvalue);
							newlist.add(newitem);
							oldlist.add(olditem);
						}
					}
				}
			}

		} catch (JDOMException e) {
			
			e.printStackTrace();
		}
		this.setNewValueList(newlist);
		this.setOldValueList(oldlist);
	}

	/**
	 * 返回一个子集的一条记录的修改信息
	 * 
	 * @param chg_id
	 * @param fieldsteid
	 * @param keyvalue
	 * @param sequence
	 *            序号
	 * @param flag
	 *            //是否显示没有修改的信息
	 */
	public boolean checkUpdate(String fieldsteid, String keyvalue,
			String sequence, String type) {
		boolean checkupdate = false;
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + keyvalue + "' and @type='"
						+ type + "' and @sequence='" + sequence + "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element recordE = (Element) r.next();
					String flag = recordE.getAttributeValue("sp_flag");
					if ("01,03,07".indexOf(flag) == -1) {
						checkupdate = true;
					}
				}
			}

		} catch (JDOMException e) {
			
			e.printStackTrace();
		}
		return checkupdate;
	}
	
	public boolean queryMainPeal(String fieldsteid, String a0100,
			String sp_flag) {
		boolean checkupdate = false;
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + a0100 + "' and @sp_flag='"
						+ sp_flag + "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element recordE = (Element) r.next();
					String flag = recordE.getAttributeValue("sp_flag");
					if("02".equals(flag)) {
						checkupdate = true;
					}
				}
			}

		} catch (JDOMException e) {
			
			e.printStackTrace();
		}
		return checkupdate;
	}

	/**
	 * 得到一条记录里面所有修改的子集
	 * 
	 * @param chg_id
	 * @return 返回list包含CommonData对象
	 */
	public ArrayList queryMyselfFieldSetList(String chg_id) {
		queryInti(chg_id);
		String xpath = "/root/setid";
		XPath reportPath;
		ArrayList list = new ArrayList();
		try {
			reportPath = XPath.newInstance(xpath);
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			Element element = null;
			Element element_c = null;
			while (t.hasNext()) {
				element = (Element) t.next();
				String setid = element.getAttributeValue("name");
				FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
				fieldset = fieldset.cloneItem(); 
				if (fieldset==null || "0".equals(fieldset.getUseflag()))//tianye update 查询已构库的子集
					   continue;
				List clist = element.getChildren();
				Iterator c = clist.iterator();
				if (c.hasNext()) {
					element_c = (Element) c.next();
					fieldset.setChangeflag(element_c
							.getAttributeValue("sp_flag"));
					fieldset.setModuleflag(element_c.getAttributeValue("type"));
					fieldset
							.setUseflag(element_c.getAttributeValue("keyvalue"));
					fieldset.setReserveitem(element_c
							.getAttributeValue("sequence"));
				}
				list.add(fieldset);
			}
		} catch (JDOMException e) {
			
			e.printStackTrace();
		}// 取得子集结点
		this.setSetidList(list);
		return list;
	}

	/**
	 * 得到一条记录里面所有修改的子集
	 * 
	 * @param chg_id
	 * @return 返回list包含CommonData对象
	 */
	public ArrayList queryMyselfFieldSetListFormChgid(String chg_id,
			String sp_flag) {
		queryInti(chg_id);
		String xpath = "/root/setid";
		XPath reportPath;
		ArrayList list = new ArrayList();
		try {
			reportPath = XPath.newInstance(xpath);
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			Element element = null;
			Element element_c = null;
			boolean isCorrect = false;
			while (t.hasNext()) {
				element = (Element) t.next();
				String setid = element.getAttributeValue("name");
				FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
				fieldset = fieldset.cloneItem();
				if (fieldset==null || "0".equals(fieldset.getUseflag()))//tianye update 查询已构库的子集
					   continue;
				List clist = element.getChildren();
				Iterator c = clist.iterator();
				isCorrect = false;
				while (c.hasNext()) {
					element_c = (Element) c.next();
					String one_flag = element_c.getAttributeValue("sp_flag") != null
							&& element_c.getAttributeValue("sp_flag").length() > 0 ? element_c
							.getAttributeValue("sp_flag")
							: "";
					if (sp_flag == null || sp_flag.length() <= 0) {
						fieldset.setChangeflag(element_c
								.getAttributeValue("sp_flag"));
						fieldset.setModuleflag(element_c
								.getAttributeValue("type"));
						fieldset.setUseflag(element_c
								.getAttributeValue("keyvalue"));
						fieldset.setReserveitem(element_c
								.getAttributeValue("sequence"));
						isCorrect = true;
					} else {
						if (sp_flag.indexOf(one_flag) != -1) {
							fieldset.setChangeflag(element_c
									.getAttributeValue("sp_flag"));
							fieldset.setModuleflag(element_c
									.getAttributeValue("type"));
							fieldset.setUseflag(element_c
									.getAttributeValue("keyvalue"));
							fieldset.setReserveitem(element_c
									.getAttributeValue("sequence"));
							isCorrect = true;
						}
					}
					if (isCorrect)
						break;
				}
				if (isCorrect)
					list.add(fieldset);
			}
		} catch (JDOMException e) {
			
			e.printStackTrace();
		}// 取得子集结点
		this.setSetidList(list);
		return list;
	}

	public ArrayList queryMyselfFieldSetList(String nbase, String a0100,
			String sp_flag) {
		queryInti(nbase, a0100);
		String xpath = "/root/setid";
		XPath reportPath;
		ArrayList list = new ArrayList();
		boolean isCorrect = false;
		try {
			reportPath = XPath.newInstance(xpath);
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			Element element = null;
			Element element_c = null;
			while (t.hasNext()) {
				element = (Element) t.next();
				String setid = element.getAttributeValue("name");
				FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
				fieldset = fieldset.cloneItem();
				if (fieldset==null || "0".equals(fieldset.getUseflag()))//tianye update 查询已构库的子集
					   continue;
				List clist = element.getChildren();
				Iterator c = clist.iterator();
				isCorrect = false;
				while (c.hasNext()) {
					element_c = (Element) c.next();
					String one_flag = element_c.getAttributeValue("sp_flag") != null
							&& element_c.getAttributeValue("sp_flag").length() > 0 ? element_c
							.getAttributeValue("sp_flag")
							: "";
					if (sp_flag == null || sp_flag.length() <= 0) {
						fieldset.setChangeflag(element_c
								.getAttributeValue("sp_flag"));
						fieldset.setModuleflag(element_c
								.getAttributeValue("type"));
						fieldset.setUseflag(element_c
								.getAttributeValue("keyvalue"));
						fieldset.setReserveitem(element_c
								.getAttributeValue("sequence"));
						isCorrect = true;
					} else {
						if (sp_flag.indexOf(one_flag) != -1) {
							fieldset.setChangeflag(element_c
									.getAttributeValue("sp_flag"));
							fieldset.setModuleflag(element_c
									.getAttributeValue("type"));
							fieldset.setUseflag(element_c
									.getAttributeValue("keyvalue"));
							fieldset.setReserveitem(element_c
									.getAttributeValue("sequence"));
							isCorrect = true;
						}
					}
					if (isCorrect)
						break;
				}
				if (isCorrect)
					list.add(fieldset);
			}
		} catch (JDOMException e) {
			
			e.printStackTrace();
		}// 取得子集结点
		this.setSetidList(list);
		return list;
	}

	/**
	 * 整体操作
	 * 
	 * @param chg_id
	 * @param sp_flag
	 *            01|02|03|07
	 * @return
	 */
	public boolean batchMyselfDataApply(String chg_id, String sp_flag) {
		boolean isCorrect = false;
		//批准时该条数据是否完全成功处理
		boolean batchFlag = true;
		//zxj 20161008 是否有已驳回记录，有已驳回的，那么即使其余全部批准也不修改整体的“已退回”状态
		boolean haveRejectRecord = false;
		
		queryInti(chg_id);
		A0100Bean bean = getA0100bean(chg_id);
		PreparedStatement pstmt = null;
		try {
			String xpath = "/root/setid";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			while (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				String setid = fieldSetE.getAttributeValue("name");
				FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
				if (fieldSet==null || "0".equals(fieldSet.getUseflag()))//tianye update 查询已构库的子集
					   continue;
				xpath = "record";// 选择上下文节点的 setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				while (r.hasNext())// 有记录
				{
					Element recordE = (Element) r.next();
					String flag = recordE.getAttributeValue("sp_flag");
					String type = recordE.getAttributeValue("type");
					String keyvalue = recordE.getAttributeValue("keyvalue");
					String sequence = recordE.getAttributeValue("sequence");
					if (flag != null && "03".equals(flag)) {
						if (sp_flag != null && "07".equals(sp_flag)
								&& this.isChecked_may_reject) {

						} else
							continue;
					}
					if (sp_flag != null && "03".equals(sp_flag)) {
						if (flag != null && "02".equals(flag)) {
							
							isCorrect = apployEdit(bean, fieldSet, keyvalue,
									type, sequence, recordE);
							if (!isCorrect) {
								if (structureExecSqlString.db.redundantInfo.length()!=0){
									redundantInfo=redundantInfo+structureExecSqlString.db.redundantInfo;
								}
							}

							if (isCorrect) {
								recordE.setAttribute("sp_flag", sp_flag);
							}
							batchFlag = batchFlag && isCorrect;
						} else if ("07".equals(flag)) {
						    //有记录为已驳回
						    haveRejectRecord = true;
						}
					} else if (sp_flag != null && "07".equals(sp_flag)
							&& flag != null && "03".equals(flag)) {
						if (this.isChecked_may_reject) {
							recordE.setAttribute("sp_flag", sp_flag);
						}
					} else {
						recordE.setAttribute("sp_flag", sp_flag);
					}

				}
			}
			String xmlContent = getXmlconent();
			RecordVo vo = new RecordVo("t_hr_mydata_chg");
			vo.setString("chg_id", chg_id);
			vo = this.dao.findByPrimaryKey(vo);
			String a0100 = vo.getString("a0100"); 
            String nbase = vo.getString("nbase"); 
			Date date = DateUtils.getSqlDate(Calendar.getInstance());
			vo.setDate("create_time", date);
			vo.setString("content", xmlContent);
			String spIdea = vo.getString("sp_idea");
			vo.setString("sp_flag", sp_flag);
			if (!batchFlag)
				throw GeneralExceptionHandler.Handle(new Exception("临时表数据未完全保存到数据库！！！"));

			String operator = "";
            if(StringUtils.isNotEmpty(spIdea)) {
                Document doc = PubFunc.generateDom(spIdea);
                xpath = "/root/rec[@sp_state=\"报批\"]";
                reportPath = XPath.newInstance(xpath);// 取得子集结点
                List<Element> childList = reportPath.selectNodes(doc);
                Element ele = childList.get(0);
                operator = ele.getAttributeValue("name");
                if(operator.indexOf("/") > -1)
                    operator = operator.substring(operator.lastIndexOf("/") + 1);
            }
            
            String a0101 = vo.getString("a0101");
            
			this.dao.updateValueObject(vo);

            if("07".equals(sp_flag) && operator.equals(a0101)) {
                PersonInfoBo pibo = new PersonInfoBo(this.conn, chg_id,this.UserView);
                pibo.sendEmail(null, nbase, a0100, commentValue);
                
            }
                
			isCorrect = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException ee) {
				ee.printStackTrace();
			}
		}
		return isCorrect;
	}

	/**
	 * 根据setname报批
	 * 
	 * @param userbase
	 * @param a0100
	 * @param setname
	 * @param sp_flag
	 * @param name单位/部门/职位/姓名
	 * @return
	 */
	public boolean batchMyselfDataApplyBySetname(String userbase, String a0100,
			String setname, String sp_flag, String name, int version) {
		boolean isCorrect = false;
		queryInti(userbase, a0100);
		A0100Bean bean = getA0100bean(chg_id);
		try {
			String xpath = "/root/setid";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			while (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				String setid = fieldSetE.getAttributeValue("name");
				if (setid.equalsIgnoreCase(setname)) {
					FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
					if (fieldSet==null || "0".equals(fieldSet.getUseflag()))//tianye update 查询已构库的子集
						   continue;
					xpath = "record";// 选择上下文节点的 setid
					// 元素孩子的record元素keyvalue=i9999
					reportPath = XPath.newInstance(xpath);// 取得子集记录结点
					childlist = reportPath.selectNodes(fieldSetE);
					Iterator r = childlist.iterator();
					while (r.hasNext())// 有记录
					{
						Element recordE = (Element) r.next();
						String flag = recordE.getAttributeValue("sp_flag");
						String type = recordE.getAttributeValue("type");
						String keyvalue = recordE.getAttributeValue("keyvalue");
						String sequence = recordE.getAttributeValue("sequence");
						if (flag != null && "03".equals(flag))
							continue;
						if (sp_flag != null && "03".equals(sp_flag)) {
							if (flag != null && "02".equals(flag)) {
								recordE.setAttribute("sp_flag", sp_flag);
								apployEdit(bean, fieldSet, keyvalue, type,
										sequence, recordE);
							}
						} else {
							recordE.setAttribute("sp_flag", sp_flag);
						}

					}
				}
				String xmlContent = getXmlconent();

				RecordVo vo = new RecordVo("t_hr_mydata_chg");
				vo.setString("chg_id", chg_id);
				vo = this.dao.findByPrimaryKey(vo);
				Date date = DateUtils.getSqlDate(Calendar.getInstance());
				vo.setDate("create_time", date);
				vo.setString("content", xmlContent);
				this.dao.updateValueObject(vo);
				isCorrect = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (version >= 50) {
				String sp_state = "报批";
				String dateStr = "";
				SimpleDateFormat dateFm = new SimpleDateFormat(
						"yyyy.MM.dd HH:mm:ss");
				dateStr = dateFm.format(new java.util.Date());
				insertXml(sp_state, name, dateStr);
				String xmlIdea = this.getXmlconent(idea);
				RecordVo vo = new RecordVo("t_hr_mydata_chg");
				vo.setString("chg_id", chg_id);
				
				try {
					vo = dao.findByPrimaryKey(vo);
					vo.setString(sp_idea, xmlIdea);
					dao.updateValueObject(vo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return isCorrect;
	}

	/**
	 * 向sp_idea里面插入一条xml记录 <rec sp_state="报批" name="集团总部/集团领导/null/刘兵"
	 * date="2009.12.27 15:21:56" />
	 * 
	 * @param sp_state
	 * @param name
	 * @param date
	 */
	private void insertXml(String sp_state, String name, String date) {
		try {
			XPath reportPath = XPath.newInstance("/root");// 取得子集结点
			List childlist = reportPath.selectNodes(idea);
			Element ele = (Element) childlist.get(0);
			Element childEle = new Element("rec");
			childEle.setAttribute("sp_state", sp_state);
			childEle.setAttribute("name", name);
			childEle.setAttribute("date", date);
			ele.addContent(childEle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向sp_idea里面插入一条xml记录 <rec sp_state="报批" name="集团总部/集团领导/null/刘兵"
	 * date="2009.12.27 15:21:56" />
	 * 
	 * @param sp_state
	 * @param name
	 * @param date
	 */
	private void insertXml(String sp_state, String name, String date,
			String returnValue) {
		try {
			XPath reportPath = XPath.newInstance("/root");// 取得子集结点
			List childlist = reportPath.selectNodes(idea);
			Element ele = (Element) childlist.get(0);
			Element childEle = new Element("rec");
			childEle.setAttribute("sp_state", sp_state);
			childEle.setAttribute("name", name);
			childEle.setAttribute("date", date);
			childEle.setText(returnValue);

			ele.addContent(childEle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据setname撤销删除
	 * 
	 * @param chg_id
	 * @param sp_flag
	 *            01|02|03|07
	 * @return
	 */
	public boolean backdelBySetname(String userbase, String a0100,
			String setname) {
		boolean isCorrect = false;
		queryInti(userbase, a0100);
		if(this.chg_id==null||this.chg_id.length()==0|| "null".equals(this.chg_id))
			return isCorrect;
		try {
			String xpath = "/root/setid";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			while (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				String setid = fieldSetE.getAttributeValue("name");

				if (setid.equalsIgnoreCase(setname)) {
					FieldSet fieldSet = DataDictionary.getFieldSetVo(setid);
					if (fieldSet==null || "0".equals(fieldSet.getUseflag()))//tianye update 查询已构库的子集
						   continue;
					xpath = "record";// 选择上下文节点的 setid
					// 元素孩子的record元素keyvalue=i9999
					reportPath = XPath.newInstance(xpath);// 取得子集记录结点
					childlist = reportPath.selectNodes(fieldSetE);
					Iterator r = childlist.iterator();
					while (r.hasNext())// 有记录
					{
						Element recordE = (Element) r.next();
						String flag = recordE.getAttributeValue("sp_flag");
						String type = recordE.getAttributeValue("type");
						if ((!"02".equals(flag)) && "delete".equals(type)) {
							fieldSetE.removeContent(recordE);
							this.doc.removeContent(recordE);
						}

					}

					String xmlContent = getXmlconent();
					RecordVo vo = new RecordVo("t_hr_mydata_chg");
					vo.setString("chg_id", chg_id);
					vo = this.dao.findByPrimaryKey(vo);
					Date date = DateUtils.getSqlDate(Calendar.getInstance());
					vo.setDate("create_time", date);
					vo.setString("content", xmlContent);
					this.dao.updateValueObject(vo);
					isCorrect = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isCorrect;
	}

	/**
	 * 返回keyvalue
	 * 
	 * @param chg_id
	 * @param fieldsteid
	 * @return
	 */
	public void getOtherParamList(String chg_id, String fieldsteid, String flag) {
		ArrayList keylist = new ArrayList();
		ArrayList typelist = new ArrayList();
		ArrayList flaglist = new ArrayList();
		ArrayList sequencelist = new ArrayList();
		queryInti(chg_id);
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			String keyvalue = "";
			String type = "";
			String sp_flag = "";
			String sequence = "";
			if (t.hasNext()) {
				Element setidE = (Element) t.next();
				List clist = setidE.getChildren();
				Iterator c = clist.iterator();
				while (c.hasNext()) {
					Element recordE = (Element) c.next();
					keyvalue = recordE.getAttributeValue("keyvalue") != null
							&& recordE.getAttributeValue("keyvalue").length() > 0 ? recordE
							.getAttributeValue("keyvalue")
							: "";
					type = recordE.getAttributeValue("type") != null
							&& recordE.getAttributeValue("type").length() > 0 ? recordE
							.getAttributeValue("type")
							: "";
					sp_flag = recordE.getAttributeValue("sp_flag") != null
							&& recordE.getAttributeValue("sp_flag").length() > 0 ? recordE
							.getAttributeValue("sp_flag")
							: "";
					sequence = recordE.getAttributeValue("sequence") != null
							&& recordE.getAttributeValue("sequence").length() > 0 ? recordE
							.getAttributeValue("sequence")
							: "";
					if (flag == null || flag.length() <= 0) {
						keylist.add(keyvalue);
						typelist.add(type);
						flaglist.add(sp_flag);
						sequencelist.add(sequence);
					} else {
						if (flag.indexOf(sp_flag) != -1) {
							keylist.add(keyvalue);
							typelist.add(type);
							flaglist.add(sp_flag);
							sequencelist.add(sequence);
						}
					}
				}

			}
		} catch (JDOMException e) {
			
			e.printStackTrace();
		}
		this.setKeyvalueList(keylist);
		this.setTypeList(typelist);
		this.setSp_flaglueList(flaglist);
		this.setSequenceList(sequencelist);
	}

	/**
	 * 返回keyvalue
	 * 
	 * @param chg_id
	 * @param fieldsteid
	 * @return
	 */
	public void getOtherParamList(String nbase, String a0100,
			String fieldsteid, String flag) {
		ArrayList keylist = new ArrayList();
		ArrayList typelist = new ArrayList();
		ArrayList flaglist = new ArrayList();
		ArrayList sequencelist = new ArrayList();
		queryInti(nbase, a0100);
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			String keyvalue = "";
			String type = "";
			String sp_flag = "";
			String sequence = "";
			if (t.hasNext()) {
				Element setidE = (Element) t.next();
				List clist = setidE.getChildren();
				Iterator c = clist.iterator();
				while (c.hasNext()) {
					Element recordE = (Element) c.next();
					keyvalue = recordE.getAttributeValue("keyvalue") != null
							&& recordE.getAttributeValue("keyvalue").length() > 0 ? recordE
							.getAttributeValue("keyvalue")
							: "";
					type = recordE.getAttributeValue("type") != null
							&& recordE.getAttributeValue("type").length() > 0 ? recordE
							.getAttributeValue("type")
							: "";
					sp_flag = recordE.getAttributeValue("sp_flag") != null
							&& recordE.getAttributeValue("sp_flag").length() > 0 ? recordE
							.getAttributeValue("sp_flag")
							: "";
					sequence = recordE.getAttributeValue("sequence") != null
							&& recordE.getAttributeValue("sequence").length() > 0 ? recordE
							.getAttributeValue("sequence")
							: "";
					if (flag == null || flag.length() <= 0) {
						keylist.add(keyvalue);
						typelist.add(type);
						flaglist.add(sp_flag);
						sequencelist.add(sequence);
					} else {
						if (flag.indexOf(sp_flag) != -1) {
							keylist.add(keyvalue);
							typelist.add(type);
							flaglist.add(sp_flag);
							sequencelist.add(sequence);
						}
					}
				}

			}
		} catch (JDOMException e) {
			
			e.printStackTrace();
		}
		this.setKeyvalueList(keylist);
		this.setTypeList(typelist);
		this.setSp_flaglueList(flaglist);
		this.setSequenceList(sequencelist);
	}

	/**
	 * 返回keyvalue
	 * 
	 * @param chg_id
	 * @param fieldsteid
	 * @return
	 */
	public void getOtherParamList(String nbase, String a0100,
			String fieldsteid, String flag, String i9999) {
		ArrayList keylist = new ArrayList();
		ArrayList typelist = new ArrayList();
		ArrayList flaglist = new ArrayList();
		ArrayList sequencelist = new ArrayList();
		queryInti(nbase, a0100);
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			String keyvalue = "";
			String type = "";
			String sp_flag = "";
			String sequence = "";
			if (t.hasNext()) {
				Element setidE = (Element) t.next();
				List clist = setidE.getChildren();
				Iterator c = clist.iterator();
				while (c.hasNext()) {
					Element recordE = (Element) c.next();
					keyvalue = recordE.getAttributeValue("keyvalue") != null
							&& recordE.getAttributeValue("keyvalue").length() > 0 ? recordE
							.getAttributeValue("keyvalue")
							: "";
					type = recordE.getAttributeValue("type") != null
							&& recordE.getAttributeValue("type").length() > 0 ? recordE
							.getAttributeValue("type")
							: "";
					sp_flag = recordE.getAttributeValue("sp_flag") != null
							&& recordE.getAttributeValue("sp_flag").length() > 0 ? recordE
							.getAttributeValue("sp_flag")
							: "";
					sequence = recordE.getAttributeValue("sequence") != null
							&& recordE.getAttributeValue("sequence").length() > 0 ? recordE
							.getAttributeValue("sequence")
							: "";
					if (i9999.indexOf(keyvalue) != -1) {
						if (flag == null || flag.length() <= 0) {
							keylist.add(keyvalue);
							typelist.add(type);
							flaglist.add(sp_flag);
							sequencelist.add(sequence);
						} else {
							if (flag.indexOf(sp_flag) != -1) {
								keylist.add(keyvalue);
								typelist.add(type);
								flaglist.add(sp_flag);
								sequencelist.add(sequence);
							}
						}
					}
				}

			}
		} catch (JDOMException e) {
			
			e.printStackTrace();
		}
		this.setKeyvalueList(keylist);
		this.setTypeList(typelist);
		this.setSp_flaglueList(flaglist);
		this.setSequenceList(sequencelist);
	}

	/**
	 * 返回keyvalue
	 * 
	 * @param fieldsteid
	 * @return
	 */
	public void getOtherParamList(String fieldsteid, String i9999) {
		ArrayList keylist = new ArrayList();
		ArrayList typelist = new ArrayList();
		ArrayList flaglist = new ArrayList();
		ArrayList sequencelist = new ArrayList();
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			String keyvalue = "";
			String type = "";
			String sp_flag = "";
			String sequence = "";
			if (t.hasNext()) {
				Element setidE = (Element) t.next();
				List clist = setidE.getChildren();
				Iterator c = clist.iterator();
				while (c.hasNext()) {
					Element recordE = (Element) c.next();
					keyvalue = recordE.getAttributeValue("keyvalue") != null
							&& recordE.getAttributeValue("keyvalue").length() > 0 ? recordE
							.getAttributeValue("keyvalue")
							: "";
					type = recordE.getAttributeValue("type") != null
							&& recordE.getAttributeValue("type").length() > 0 ? recordE
							.getAttributeValue("type")
							: "";
					sp_flag = recordE.getAttributeValue("sp_flag") != null
							&& recordE.getAttributeValue("sp_flag").length() > 0 ? recordE
							.getAttributeValue("sp_flag")
							: "";
					sequence = recordE.getAttributeValue("sequence") != null
							&& recordE.getAttributeValue("sequence").length() > 0 ? recordE
							.getAttributeValue("sequence")
							: "";
					if (i9999.indexOf(keyvalue) != -1) {
						if ("03".indexOf(sp_flag) != -1) {
							keylist.add(keyvalue);
							typelist.add(type);
							flaglist.add(sp_flag);
							sequencelist.add(sequence);
						}
					}
				}

			}
		} catch (JDOMException e) {
			
			e.printStackTrace();
		}
		this.setKeyvalueList(keylist);
		this.setTypeList(typelist);
		this.setSp_flaglueList(flaglist);
		this.setSequenceList(sequencelist);
	}

	/**
	 * 返回keyvalue
	 * 
	 * @param fieldsteid
	 * @return
	 */
	public void getOtherParamList1(String fieldsteid, String i9999) {
		ArrayList keylist = new ArrayList();
		ArrayList typelist = new ArrayList();
		ArrayList flaglist = new ArrayList();
		ArrayList sequencelist = new ArrayList();
		try {
			String xpath = "/root/setid[@name='" + fieldsteid + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			String keyvalue = "";
			String type = "";
			String sp_flag = "";
			String sequence = "";
			if (t.hasNext()) {
				Element setidE = (Element) t.next();
				List clist = setidE.getChildren();
				Iterator c = clist.iterator();
				while (c.hasNext()) {
					Element recordE = (Element) c.next();
					keyvalue = recordE.getAttributeValue("keyvalue") != null
							&& recordE.getAttributeValue("keyvalue").length() > 0 ? recordE
							.getAttributeValue("keyvalue")
							: "";
					type = recordE.getAttributeValue("type") != null
							&& recordE.getAttributeValue("type").length() > 0 ? recordE
							.getAttributeValue("type")
							: "";
					sp_flag = recordE.getAttributeValue("sp_flag") != null
							&& recordE.getAttributeValue("sp_flag").length() > 0 ? recordE
							.getAttributeValue("sp_flag")
							: "";
					sequence = recordE.getAttributeValue("sequence") != null
							&& recordE.getAttributeValue("sequence").length() > 0 ? recordE
							.getAttributeValue("sequence")
							: "";
					if (i9999.indexOf(keyvalue) != -1) {
						keylist.add(keyvalue);
						typelist.add(type);
						flaglist.add(sp_flag);
						sequencelist.add(sequence);
					}
				}

			}
		} catch (JDOMException e) {
			
			e.printStackTrace();
		}
		this.setKeyvalueList(keylist);
		this.setTypeList(typelist);
		this.setSp_flaglueList(flaglist);
		this.setSequenceList(sequencelist);
	}

	/**
	 * 获取子集下已经审批的所有数据
	 * 
	 * @param a0100
	 * @param setname
	 * @return
	 */
	public ArrayList getAllApproDataBySetname(String a0100, String setname,
			String userbase) {
		ArrayList infoSetList = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql
				.append("select chg_id,content,sp_flag,sp_idea from t_hr_mydata_chg ");
		sql.append("where a0100='" + a0100 + "' ");
		sql.append(" and sp_flag = '03'");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			 rs= dao.search(sql.toString());
			while (rs.next()) {
				String chg_id = rs.getString("chg_id");
				this.queryInti(chg_id);
				try {
					String xpath = "/root/setid[@name='" + setname + "']";
					XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
					List childlist = reportPath.selectNodes(doc);
					Iterator t = childlist.iterator();
					String type = "";
					String sp_flag = "";
					if (t.hasNext()) {
						RecordVo vo = new RecordVo(userbase + setname);
						Element setidE = (Element) t.next();
						List clist = setidE.getChildren();
						Iterator c = clist.iterator();
						while (c.hasNext()) {
							Element recordE = (Element) c.next();
							type = recordE.getAttributeValue("type") != null
									&& recordE.getAttributeValue("type")
											.length() > 0 ? recordE
									.getAttributeValue("type") : "";
							sp_flag = recordE.getAttributeValue("sp_flag") != null
									&& recordE.getAttributeValue("sp_flag")
											.length() > 0 ? recordE
									.getAttributeValue("sp_flag") : "";
							sequence = recordE.getAttributeValue("sequence") != null
									&& recordE.getAttributeValue("sequence")
											.length() > 0 ? recordE
									.getAttributeValue("sequence") : "";
							vo.setString("createusername", type);
							if ("03".equals(sp_flag)) {
								List list = recordE.getChildren("column");
								for (int i = 0; i < list.size(); i++) {
									Element el = (Element) list.get(i);
									String oldValue = el
											.getAttributeValue("oldvalue");
									String newValue = el
											.getAttributeValue("newvalue");
									String name = el.getAttributeValue("name");
									if (oldValue.equals(newValue)) {
										vo.setString(name, "");
									} else {
										vo.setString(name, newValue);
									}
								}
							}
						}
						infoSetList.add(vo);

					}
				} catch (JDOMException e) {
					
					e.printStackTrace();
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
		return infoSetList;
	}

	/**
	 * 
	 * @param chg_id
	 */
	private void queryInti(String chg_id) {
		chg_id = chg_id != null ? chg_id : "";
		StringBuffer sql = new StringBuffer();
		sql
				.append("select sp_flag,content,sp_idea from t_hr_mydata_chg where chg_id='"
						+ chg_id + "'");
		String xmlcontent = "";
		RowSet rs=null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				xmlcontent = rs.getString("content");
				this.sp_flag = rs.getString("sp_flag");
				this.sp_idea = rs.getString("sp_idea");
			}

			if (xmlcontent == null || "".equals(xmlcontent)) {
				xmlcontent = "";
			}
			contentInit(xmlcontent);
			ideaInit(this.sp_idea);
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
	}
	
	private void queryInti2(String chg_id) {
		chg_id = chg_id != null ? chg_id : "";
		StringBuffer sql = new StringBuffer();
		sql
				.append("select sp_flag,content,sp_idea from t_hr_mydata_chg where chg_id='"
						+ chg_id + "'");
		String xmlcontent = "";
		RowSet rs=null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				xmlcontent = rs.getString("content");
				this.sp_flag = rs.getString("sp_flag");
				this.sp_idea = rs.getString("sp_idea");
			}

			if (xmlcontent == null || "".equals(xmlcontent)) {
				xmlcontent = "";
			}
			contentInit(xmlcontent);
			this.sp_idea = "";
			ideaInit(this.sp_idea);
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
	}

	private boolean queryInti(String nbase, String a0100) {
		StringBuffer sql = new StringBuffer();
		sql
				.append("select chg_id,content,sp_flag,sp_idea from t_hr_mydata_chg where nbase='"
						+ nbase + "'");
		sql.append(" and a0100='" + a0100 + "' and sp_flag<>'03'");
		sql.append(" order by chg_id desc");
		boolean isCorrect = false;
		String xmlcontent = "";
		RowSet rs =null;
		try {
			 rs = dao.search(sql.toString());
			if (rs.next()) {
				xmlcontent = rs.getString("content");
				this.chg_id = rs.getString("chg_id");
				this.sp_flag = rs.getString("sp_flag");
				this.sp_idea = rs.getString("sp_idea");
				isCorrect = true;
			}
			if (xmlcontent == null || "".equals(xmlcontent)) {
				xmlcontent = "";
			}
			contentInit(xmlcontent);
			ideaInit(this.sp_idea);

		} catch (Exception ex) {
			// ex.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
		return isCorrect;
	}

	/**
	 * 新增保存
	 * 
	 * @param nbase
	 * @param a0100
	 * @param fieldSet
	 * @param newFiledlist修改后的
	 * @param oldFilelist修改前的
	 * @param type
	 * @param sp_flag
	 * @param i9999
	 * @return
	 * @throws GeneralException
	 */
	public synchronized boolean saveMyselfData(String nbase, String a0100,
			FieldSet fieldSet, ArrayList newFiledlist, ArrayList oldFilelist,
			String type, String sp_flag, String i9999, String sequence)
			throws GeneralException {
		boolean isCorrect = false;
		A0100Bean bean = getA0100bean(nbase, a0100);
		if (type == null || type.length() <= 0) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"类型不能为空！", "", ""));
		}
		if ((i9999 == null || i9999.length() <= 0) && !"new".equals(type)) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"i9999不能为空！", "", ""));
		}
		
		ResultSet rs = null;
		try {
			boolean isSave = queryInti(nbase, a0100);// 判断是否有数据
			String keyValue = i9999;
			if(!"A01".equalsIgnoreCase(fieldSet.getFieldsetid()))
				keyValue = getKeyValue(nbase+fieldSet.getFieldsetid(), a0100, i9999);
			
			if (!isSave) {
			    if("update".equalsIgnoreCase(type) && !isFieldValueChange(newFiledlist, oldFilelist) && (this.fileInfo == null || this.fileInfo.size() < 1)) {
			        if("A01".equalsIgnoreCase(fieldSet.getFieldsetid()))
                        throw new GeneralException("", "人员信息没有变动，不需要报批！", "", "");
                    
			        isCorrect = deleteMyselfChangeData(bean, fieldSet, type, keyValue, sequence);
			    } else
			        isCorrect = editMyselfData(bean, fieldSet, newFiledlist,
						oldFilelist, type, sp_flag, keyValue, "1");
			    
				if (!isCorrect)
					return false;
				
				String xmlContent = getXmlconent();
				IDGenerator idg = new IDGenerator(2, this.conn);
				String chg_id = idg.getId("t_hr_mydata_chg.chg_id");
				
				// 循环查找chg_id是否存在，解决不能保存的问题
				boolean isExist = true;
				
				while (isExist) {
					StringBuffer buff = new StringBuffer();
					buff.append("select chg_id from t_hr_mydata_chg where chg_id='");
					buff.append(chg_id);
					buff.append("'");
					rs = this.dao.search(buff.toString());
					if (rs.next()) {
						chg_id = idg.getId("t_hr_mydata_chg.chg_id");
					} else {
						isExist = false;
					}
				}
				if (rs != null) {
					rs.close();
				}
				
				RecordVo vo = new RecordVo("t_hr_mydata_chg");
				vo.setString("chg_id", chg_id);
				vo.setString("nbase", bean.getNbase());
				vo.setString("b0110", bean.getB0110());
				vo.setString("e0122", bean.getE0122());
				vo.setString("e01a1", bean.getE01a1());
				vo.setString("a0101", bean.getA0101());
				vo.setString("a0100", bean.getA0100());
				vo.setString("a0000", bean.getA0000());
				vo.setString("content", xmlContent);
				vo.setString("sp_idea", "");
				vo.setString("sp_flag", "01");
				Date date = DateUtils.getSqlDate(Calendar.getInstance());
				vo.setDate("create_time", date);
				vo.setString("description", fieldSet.getCustomdesc());
				this.dao.addValueObject(vo);
				this.chg_id = chg_id;
				isCorrect = true;
			} else {
				updateMyselfData(this.chg_id, fieldSet, newFiledlist,
						oldFilelist, type, sp_flag, keyValue, sequence);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
		return isCorrect;
	}

	/**
	 * 修改
	 * 
	 * @param chg_id
	 * @param fieldSet
	 * @param newFiledlist修改后的
	 * @param oldFilelist修改前的
	 * @param type
	 * @param sp_flag
	 * @param i9999
	 * @return
	 * @throws GeneralException
	 */
	public synchronized boolean updateMyselfData(String chg_id,
			FieldSet fieldSet, ArrayList newFiledlist, ArrayList oldFilelist,
			String type, String sp_flag, String keyValue, String sequence)
			throws GeneralException {
		boolean isCorrect = false;
		A0100Bean bean = getA0100bean(chg_id);
		if (type == null || type.length() <= 0) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"类型不能为空！", "", ""));
		}
		try {
			RecordVo vo = new RecordVo("t_hr_mydata_chg");
			vo.setString("chg_id", chg_id);
			vo = this.dao.findByPrimaryKey(vo);

			String content = "";
			String flag = "";
			if (vo != null) {
				content = vo.getString("content");
				flag = vo.getString("sp_flag");
			} else {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						"记录不存在", "", ""));
			}
			if ("01".equals(sp_flag)) {
				if ("03".equals(flag)) {
					throw GeneralExceptionHandler.Handle(new GeneralException(
							"", "记录已经批准，不能修改!", "", ""));
				} else if ("02".equals(flag)) {
					throw GeneralExceptionHandler.Handle(new GeneralException(
							"", "记录已经报批，不能修改!", "", ""));
				}
			} else if ("02".equals(sp_flag)) {
				if ("03".equals(flag)) {
					throw GeneralExceptionHandler.Handle(new GeneralException(
							"", "记录已经批准，不能修改!", "", ""));
				}
			}
			contentInit(content);

			ArrayList fieldlist = queryMyselfFieldSetListFormChgid(chg_id, "01,02,03,07");
			String descriptiong = "";
			String check = "no";
			String checkflag = sp_flag;
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldSet fieldSetvlaue = (FieldSet) fieldlist.get(i);
				if (fieldSetvlaue.getFieldsetid().equalsIgnoreCase(
						fieldSet.getFieldsetid())) {
					check = "yes";
				}
				descriptiong += fieldSetvlaue.getCustomdesc() + ",";
			}
			if ("no".equals(check)) {
				descriptiong += fieldSet.getCustomdesc();
				checkflag = "01";
			}

			isCorrect = editMyselfData(bean, fieldSet, newFiledlist,
					oldFilelist, type, checkflag, keyValue, sequence);
			if (!isCorrect)
				return false;
			String xmlContent = getXmlconent();
			Date date = DateUtils.getSqlDate(Calendar.getInstance());
			vo.setDate("create_time", date);
			if(StringUtils.isNotEmpty(descriptiong) && descriptiong.endsWith(","))
			    descriptiong = descriptiong.substring(0, descriptiong.length() - 1);
			
			vo.setString("description", descriptiong);
			vo.setString("content", xmlContent);
			this.dao.updateValueObject(vo);
			isCorrect = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return isCorrect;
	}

	/**
	 * 修改一个子集一项的审批标志
	 * 
	 * @param chg_id
	 * @param fieldSet
	 * @param type
	 * @param sp_flag
	 * @param keyvalue
	 * @return
	 * @throws GeneralException
	 */
	public synchronized boolean updateApployMyselfDataApp(String chg_id,
			FieldSet fieldSet, String sp_flag, String keyvalue, String type,
			String sequence) throws GeneralException {
		boolean isCorrect = false;
		A0100Bean bean = getA0100bean(chg_id);
		if (keyvalue == null || keyvalue.length() <= 0) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"i9999不能为空！", "", ""));
		}

		try {
			RecordVo vo = new RecordVo("t_hr_mydata_chg");
			vo.setString("chg_id", chg_id);
			vo = this.dao.findByPrimaryKey(vo);
			String content = "";
			String old_spflag = "";
			if (vo != null) {
				content = vo.getString("content");
				old_spflag = vo.getString("sp_flag");
			} else {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						"记录存在", "", ""));
			}
			contentInit(content);
			isCorrect = editMyselfData(bean, fieldSet, sp_flag, type, sequence,
					keyvalue, chg_id);
			if (!isCorrect)
				return false;
			String xmlContent = getXmlconent();
			Date date = DateUtils.getSqlDate(Calendar.getInstance());
			vo.setDate("create_time", date);
			vo.setString("content", xmlContent);
			if(sp_flag!=null&& "07".equals(sp_flag))
			{
				if(old_spflag!=null&&("03".equals(old_spflag)|| "02".equals(old_spflag))&&this.isChecked_may_reject)
				  vo.setString("sp_flag", "07");
			}
			this.dao.updateValueObject(vo);

			isCorrect = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return isCorrect;
	}

	/**
	 * 删除
	 * 
	 * @param chg_id
	 * @param fieldSet
	 * @param type
	 * @param i9999
	 * @return
	 * @throws GeneralException
	 */
	public synchronized boolean delMyselfData(String chg_id, FieldSet fieldSet,
			String type, String keyvalue, String sequence)
			throws GeneralException {
		boolean isCorrect = false;
		A0100Bean bean = getA0100bean(chg_id);
		if (type == null || type.length() <= 0) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"类型不能为空！", "", ""));
		}
		if (keyvalue == null || keyvalue.length() <= 0) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"keyvalue不能为空！", "", ""));
		}
		if (sequence == null || sequence.length() <= 0)
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"sequence序号不能为空！", "", ""));

		try {
			RecordVo vo = new RecordVo("t_hr_mydata_chg");
			vo.setString("chg_id", chg_id);
			vo = this.dao.findByPrimaryKey(vo);
			String content = "";
			if (vo != null) {
				content = vo.getString("content");
			} else {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						"记录存在", "", ""));
			}
			contentInit(content);
			isCorrect = delMyselfData(bean, fieldSet, type, keyvalue, sequence);
			if (!isCorrect)
				return false;
			String xmlContent = getXmlconent();
			Date date = DateUtils.getSqlDate(Calendar.getInstance());
			vo.setDate("create_time", date);
			vo.setString("content", xmlContent);
			this.dao.updateValueObject(vo);
			isCorrect = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return isCorrect;
	}

	/**
	 * 编辑
	 * 
	 * @param bean
	 * @param fieldSet
	 * @param newFiledlist
	 * @param oldFilelist
	 * @param type
	 * @param sp_flag
	 * @param i9999
	 * @return
	 */
	private boolean editMyselfData(A0100Bean bean, FieldSet fieldSet,
			ArrayList newFiledlist, ArrayList oldFilelist, String type,
			String sp_flag, String keyValue, String sequence)
			throws GeneralException {
		boolean isCorrect = true;
		String fieldSetId = fieldSet.getFieldsetid();
		String fieldSetdesc = fieldSet.getFieldsetdesc();
		String xpath = "";
		if ("A01".equalsIgnoreCase(fieldSetId)) {
			keyValue = bean.getA0100();
		}
		if ("new".equalsIgnoreCase(type)) {
			keyValue = getGuid();
		}
		
		if ("insert".equalsIgnoreCase(type) && StringUtils.isEmpty(sequence)) {
			sequence = getInsertSequence(fieldSetId, type, keyValue);
		} else if (StringUtils.isEmpty(sequence)) {
			sequence = "1";
		}
		
		try {
			xpath = "/root/setid[@name='" + fieldSetId + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(this.doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + keyValue + "' and @type='" + type
						+ "' and @sequence='" + sequence + "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element fieldSetR = (Element) r.next();
					String ol_sp_flag = fieldSetR.getAttributeValue("sp_flag") != null
							&& fieldSetR.getAttributeValue("sp_flag").length() > 0 ? fieldSetR
							.getAttributeValue("sp_flag")
							: "";

					if ("01".equals(sp_flag) || "07".equals(sp_flag)) {
						if ("02".equals(ol_sp_flag)) {
						    if("A01".equalsIgnoreCase(fieldSetId))
						        fieldSetR.setAttribute("sp_flag", sp_flag);
						    else
						        throw new GeneralException("", "子集信息已经报批不能修改", "", "");
						        
						} else if ("03".equals(ol_sp_flag)) {
							throw GeneralExceptionHandler
									.Handle(new GeneralException("",
											"子集信息已经批准不能修改", "", ""));
						} else {
						    if(oldFilelist != null && oldFilelist.size() > 0 && newFiledlist != null && newFiledlist.size() > 0){
						        fieldSetR.setAttribute("type", type);
						        fieldSetR.setAttribute("sp_flag", sp_flag);
						        fieldSetR.setAttribute("sequence", sequence);
						        //fieldSetR.removeContent();
						        fieldSetR.removeChildren("column");
						        fieldSetR = eidtColumnElement(fieldSetR,
						                newFiledlist, oldFilelist, type);
						    }
						    
							//插入附件节点
							fieldSetR = insertMultiMediaElement(fieldSetR);
						}
					} else if ("02".equals(sp_flag)) {
						fieldSetR.setAttribute("type", type);
						fieldSetR.setAttribute("sp_flag", sp_flag);
						fieldSetR.setAttribute("sequence", sequence);
						//fieldSetR.removeContent();
						fieldSetR.removeChildren("column");
						fieldSetR = eidtColumnElement(fieldSetR, newFiledlist,
								oldFilelist, type);
						
						//插入附件节点
                        fieldSetR = insertMultiMediaElement(fieldSetR);
					}
				} else {// 没有记录
					Element recordE = new Element("record");
					recordE.setAttribute("keyvalue", keyValue);
					recordE.setAttribute("type", type);
					recordE.setAttribute("sp_flag", sp_flag);
					recordE.setAttribute("sequence", sequence);
					recordE = eidtColumnElement(recordE, newFiledlist,
							oldFilelist, type);
					//插入附件节点
					recordE = insertMultiMediaElement(recordE);
					fieldSetE.addContent(recordE);
				}
			} else {
				xpath = "/root";
				reportPath = XPath.newInstance(xpath);// 取得子集结点
				childlist = reportPath.selectNodes(doc);
				Iterator i = childlist.iterator();
				if (i.hasNext()) {
					Element element = (Element) i.next();
					Element fieldSetE = new Element("setid");
					fieldSetE.setAttribute("title", fieldSetdesc);
					fieldSetE.setAttribute("name", fieldSetId);
					Element recordE = new Element("record");
					recordE.setAttribute("keyvalue", keyValue);
					recordE.setAttribute("type", type);
					recordE.setAttribute("sp_flag", sp_flag);
					recordE.setAttribute("sequence", sequence);
					recordE = eidtColumnElement(recordE, newFiledlist,
							oldFilelist, type);
					//插入附件节点
					recordE = insertMultiMediaElement(recordE);
					fieldSetE.addContent(recordE);
					element.addContent(fieldSetE);
				}

			}
			
			this.setInsertSequence(sequence);
		} catch (JDOMException e) {
			
			isCorrect = false;
			e.printStackTrace();
			return isCorrect;
		} catch (Exception e) {
		    isCorrect = false;
            e.printStackTrace();
            return isCorrect;
        }
		 
		return isCorrect;
	}

	/**
	 * 编辑
	 * 
	 * @param bean
	 * @param fieldSet
	 * @param newFiledlist
	 * @param oldFilelist
	 * @param type
	 * @param sp_flag
	 * @param i9999
	 * @return
	 */
	private boolean editMyselfData(A0100Bean bean, FieldSet fieldSet,
			String sp_flag, String type, String sequence, String keyvalue,
			String chg_id) throws GeneralException {
		boolean isCorrect = true;
		String fieldSetId = fieldSet.getFieldsetid();
		String xpath = "";
		try {
			xpath = "/root/setid[@name='" + fieldSetId + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(this.doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + keyvalue + "' and @type='"
						+ type + "' and @sequence='" + sequence + "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element SetidR = (Element) r.next();
					String ol_sp_flag = SetidR.getAttributeValue("sp_flag") != null
							&& SetidR.getAttributeValue("sp_flag").length() > 0 ? SetidR
							.getAttributeValue("sp_flag")
							: "";
					if (!"03".equals(ol_sp_flag)) {
						if ("02".equals(ol_sp_flag) && "03".equals(sp_flag)) {
							type = SetidR.getAttributeValue("type");// 得到操作类型
							sequence = SetidR.getAttributeValue("sequence");// 同一子集的排序
							isCorrect = apployEdit(bean, fieldSet, keyvalue,
									type, sequence, SetidR);
							
						}
						SetidR.setAttribute("sp_flag", sp_flag);
					} else {
						if ("07".equals(sp_flag) && this.isChecked_may_reject)// 判断比准后驳回
						{
							SetidR.setAttribute("sp_flag", sp_flag);
						} else {
							throw GeneralExceptionHandler
									.Handle(new GeneralException("",
											"子集信息以批准不能修改", "", ""));
						}
					}
				} else// 没有记录
				{
					throw GeneralExceptionHandler.Handle(new GeneralException(
							"", "子集下没有找到该记录", "", ""));
				}
			} else {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						"没有找到该子集！", "", ""));
			}
		} catch (JDOMException e) {
			isCorrect = false;
			e.printStackTrace();
			return isCorrect;
		}
		return isCorrect;
	}

	/**
	 * 删除
	 * 
	 * @param bean
	 * @param fieldSet
	 * @param type
	 * @param keyvalue
	 * @return
	 * @throws GeneralException
	 */
	private boolean delMyselfData(A0100Bean bean, FieldSet fieldSet,
			String type, String keyvalue, String sequence)
			throws GeneralException {
		boolean isCorrect = true;
		String fieldSetId = fieldSet.getFieldsetid();
		String xpath = "";
		try {
			xpath = "/root/setid[@name='" + fieldSetId + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(this.doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + keyvalue + "' and @type='"
						+ type + "' and @sequence='" + sequence + "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element elementR = (Element) r.next();
					fieldSetE.removeContent(elementR);
				} else// 没有记录
				{
					throw GeneralExceptionHandler.Handle(new GeneralException(
							"", "子集下没有找到该记录", "", ""));
				}
			} else {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						"没有找到该子集！", "", ""));
			}
		} catch (JDOMException e) {
			
			isCorrect = false;
			e.printStackTrace();
			return isCorrect;
		}
		return isCorrect;
	}

	private String getI999Keyvalue(String fieldSetId) {
		String xpath = "/root/setid[@name='" + fieldSetId + "']";
		XPath reportPath;
		int keyvalue = 0;
		try {
			reportPath = XPath.newInstance(xpath);
			List childlist = reportPath.selectNodes(this.doc);
			if (childlist != null && childlist.size() > 0)
				keyvalue = childlist.size();

		} catch (JDOMException e) {
			e.printStackTrace();
		}// 取得子集结点
		keyvalue = keyvalue + 1;
		return "-" + keyvalue;
	}

	/**
	 * 计算序号
	 * 
	 * @param fieldSetId
	 * @param type
	 * @param keyvalue
	 * @return
	 */
	private String getInsertSequence(String fieldSetId, String type,
			String keyvalue) {
		String xpath = "/root/setid[@name='" + fieldSetId + "']";
		XPath reportPath;
		int sequence = 0;
		try {
			reportPath = XPath.newInstance(xpath);
			List childlist = reportPath.selectNodes(this.doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + keyvalue + "' and @type='"
						+ type + "']";// 选择上下文节点的 setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				if (childlist != null && childlist.size() > 0)
					sequence = childlist.size();
			}

		} catch (JDOMException e) {
			e.printStackTrace();
		}// 取得子集结点
		sequence = sequence + 1;
		return sequence + "";
	}

	/**
	 * 
	 * @param SetidR
	 * @param newFiledlist变化后
	 * @param oldFilelist变化前
	 * @param type
	 * @return
	 */
	private Element eidtColumnElement(Element SetidR, ArrayList newFiledlist,
			ArrayList oldFilelist, String type) {
		if ("update".equalsIgnoreCase(type))// Update方式，变化前和变化后都有值
		{
			HashMap newFiledMap = getFiledMap(newFiledlist);
			HashMap oldFileMap = getFiledMap(oldFilelist);
			for (int i = 0; i < newFiledlist.size(); i++) {
				FieldItem item = (FieldItem) newFiledlist.get(i);
				Element column = new Element("column");
				column.setAttribute("name", item.getItemid());
				if(newFiledMap.get(item.getItemid()) == null) {
					column.setAttribute("newvalue", "");
				} else {
					String newValue = (String) newFiledMap.get(item.getItemid());
					if("D".equalsIgnoreCase(item.getItemtype())) {
						int length = item.getItemlength();
						newValue = dateFormatter(newValue, length);
					}
					
					column.setAttribute("newvalue", newValue);
				}
				
				if(oldFileMap.get(item.getItemid()) == null)
					column.setAttribute("oldvalue", "");
				else
					column.setAttribute("oldvalue", (String) oldFileMap.get(item.getItemid()));
				
				SetidR.addContent(column);
			}
			
		} else if ("delete".equalsIgnoreCase(type))// 变化前有值，变化后没有值
		{
			for (int i = 0; i < oldFilelist.size(); i++) {
				FieldItem item = (FieldItem) oldFilelist.get(i);
				Element column = new Element("column");
				column.setAttribute("name", item.getItemid());
				column.setAttribute("newvalue", "");
				column.setAttribute("oldvalue", item.getValue());
				SetidR.addContent(column);
			}
		} else if ("new".equalsIgnoreCase(type)
				|| "insert".equalsIgnoreCase(type)
				|| "apply".equalsIgnoreCase(type))// New方式，变化前没有值，变化后有值
		// ，新增方式keyvalue值为-1,批准时自动加
		{ // Insert方式，变化前没有值，变化后有值，keyvalue保存的为插入记录的I9999的值,新记录I9999在它前面
			for (int i = 0; i < newFiledlist.size(); i++) {
				FieldItem item = (FieldItem) newFiledlist.get(i);
				Element column = new Element("column");
				column.setAttribute("name", item.getItemid());
				String newValue = (String) item.getValue();
				if("D".equalsIgnoreCase(item.getItemtype())) {
					int length = item.getItemlength();
					newValue = dateFormatter(newValue, length);
				}
				
				column.setAttribute("newvalue", newValue);
				column.setAttribute("oldvalue", "");
				SetidR.addContent(column);
			}
		}
		return SetidR;
	}

	/**
	 * 创建附件节点
	 * @param recordEle
	 * @return
	 */
	private Element insertMultiMediaElement(Element recordEle){
		if(this.fileInfo==null || this.fileInfo.size()<1)
			return recordEle;
		
		for(int i=0;i<fileInfo.size();i++){
			Element multimedia  = new Element("multimedia");
			HashMap file = (HashMap)fileInfo.get(i);
			String type = (String)file.get("type");
			if("delete".equals(type)){
				multimedia.setAttribute("type", "delete");
				multimedia.setAttribute("fileid", (String)file.get("fileid"));
			}else{
				multimedia.setAttribute("type", "new");
				multimedia.setAttribute("topic",(String)file.get("topic"));
				multimedia.setAttribute("desc", (String)file.get("desc"));
				multimedia.setAttribute("srcfilename", (String)file.get("srcFileName"));
				multimedia.setAttribute("filename", (String)file.get("fileName"));
				multimedia.setAttribute("class", (String)file.get("class"));
				multimedia.setAttribute("path",(String)file.get("path"));
			}
			recordEle.addContent(multimedia);
		}
		return recordEle;
	}
	
	/**
	 * 插入附件信息
	 * @param setid
	 * @param sequence
	 * @param map
	 */
	public boolean insertMultiMediaInfo(String setid,String keyvalue,String sequence,ArrayList fileInfo){
		String xpath = "/root/setid[@name='" + setid + "']/record[@keyvalue='"+keyvalue+"' and @sequence='"+sequence+"']";
		XPath reportPath;
		try {
			reportPath = XPath.newInstance(xpath);
			Element ele = (Element)reportPath.selectSingleNode(this.doc);
			if(ele==null){
				return false;
			}
			this.fileInfo = fileInfo;
			insertMultiMediaElement(ele);
			
			RecordVo vo = new RecordVo("t_hr_mydata_chg");
			vo.setString("chg_id", this.chg_id);
			ContentDAO dao = new ContentDAO(conn);
			dao.findByPrimaryKey(vo);
			vo.setString("content", getXmlconent());
			dao.updateValueObject(vo);
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	public void deleteMultiMediaInfo(String setid,String keyvalue,String sequence,String approveIds){
		String xpath = "/root/setid[@name='" + setid + "']/record[@keyvalue='"+keyvalue+"' and @sequence='"+sequence+"']";
		XPath reportPath;
		try {
			reportPath = XPath.newInstance(xpath);
			Element ele = (Element)reportPath.selectSingleNode(this.doc);
			
			List mulList = ele.getChildren("multimedia");
			boolean removeFile = false;
			String fileKey;
			for(int i = mulList.size() - 1; i >= 0; i--){
				fileKey = "";
				Element mulEle = (Element)mulList.get(i);
				
				if("new".equals(mulEle.getAttributeValue("type"))){
					fileKey = mulEle.getAttributeValue("path");
					removeFile = true;
				}else{
					fileKey = mulEle.getAttributeValue("fileid");
					removeFile = false;
				}
				
				if(approveIds.indexOf(","+fileKey+",")!=-1){
					if(removeFile)
						VfsService.deleteFile(this.UserView.getUserName(), fileKey);
					
					ele.removeContent(mulEle);
					
				}
			}
			/*
			 * 添加判断是否需要删除子集中的变动信息报批记录和子集的报批记录
			 * 1.当删除选中附件后，当前报批记录中各指标的值没有发送变化（即报批记录中的oldvalue和newvalue的值没有不同的）
			 *   并且该记录中没有报批的附件记录则删除xml数据中当前变动信息节点
			 * 2.删除当前记录后该子集没有变动信息记录则删除xml数据中该子集的节点
			 */
			//flag：是否存在变动数据 =false：不存在；=true：存在
			boolean flag = false;
			List columnList = ele.getChildren("column");
			for(int i = 0; i < columnList.size(); i++) {
			    Element column = (Element) columnList.get(i);
			    String name = column.getAttributeValue("name");
			    FieldItem fi = DataDictionary.getFieldItem(name);
			    if(fi == null)
			        continue;
			    
			    String oldValue = column.getAttributeValue("newvalue");
			    String newValue = column.getAttributeValue("oldvalue");
			    //如果有存在变动记录，则跳出循环并把flag置为true
			    if(!oldValue.equalsIgnoreCase(newValue)) {
			        flag = true;
			        break;
			    }
			    
			}
			//获取记录下的所有的附件节点
			mulList = ele.getChildren("multimedia");
			//如果不存在变动指标并且附件记录为空或者为0则删除当前记录
			if(!flag && (mulList == null || mulList.size() < 1)) {
			    String pxpath = "/root/setid[@name='" + setid + "']";
			    XPath PreportPath = XPath.newInstance(pxpath);
			    Element pele = (Element)PreportPath.selectSingleNode(this.doc);
			    pele.removeContent(ele);
			    //获取当前子集下的所有变动信息记录
			    List childElements = pele.getChildren();
			    //如果当前子集的变动信息记录为空或者为0则删除当前子集的节点
			    if(childElements == null || childElements.size() < 1) {
			        String rootXpath = "/root";
	                XPath rXpath = XPath.newInstance(rootXpath);
	                Element rootElement = (Element)rXpath.selectSingleNode(this.doc);
	                rootElement.removeContent(pele);
			    }
			        
			}
			
			RecordVo vo = new RecordVo("t_hr_mydata_chg");
			vo.setString("chg_id", this.chg_id);
			ContentDAO dao = new ContentDAO(conn);
			dao.findByPrimaryKey(vo);
			vo.setString("content", getXmlconent());
			dao.updateValueObject(vo);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public boolean hasRecord(String setid,String keyvalue,String sequence,String sp_flag){
		StringBuffer xpath = new StringBuffer();
		xpath.append("/root/setid[@name='").append(setid).append("']/record[ 1=1 ");
		if(keyvalue!=null && keyvalue.length()>0)
			xpath.append(" and @keyvalue='").append(keyvalue).append("' ");
		if(sequence!=null && sequence.length()>0)
			xpath.append(" and @sequence='").append(sequence).append("' ");
		if(sp_flag!=null && sp_flag.length()>0)
			xpath.append(" and @sp_flag='").append(sp_flag).append("' ");
		xpath.append("]");
		XPath reportPath;
		boolean has = false;
		try {
			reportPath = XPath.newInstance(xpath.toString());
			Element ele = (Element)reportPath.selectSingleNode(this.doc);
			if(ele!=null)has = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return has;
	}
	private void removeTempFile(Element mulEle){
		String path = mulEle.getAttributeValue("path");
		String filename = mulEle.getAttributeValue("filename");
		path = path.replace("\\",File.separator);  
		if(!path.endsWith(File.separator))path+=File.separator;
		File file = new File(path+filename);
		if(file.exists())
			file.delete();
		
	}
	public ArrayList getMultiMediaInfo(String setid,String keyvalue,String sequence){
		ArrayList mediaInfo = new ArrayList();
		String xpath = "/root/setid[@name='" + setid + "']/record[@keyvalue='"+keyvalue+"' and @sequence='"+sequence+"']";
		XPath reportPath;
		RowSet rs = null;
		try {
			reportPath = XPath.newInstance(xpath);
			Element ele = (Element)reportPath.selectSingleNode(this.doc);
			if(ele!=null){
				if("03".equals(ele.getAttributeValue("sp_flag"))){//"A01".equals(setid) &&
					return mediaInfo;
				}
				List medias = ele.getChildren("multimedia");
				
				if(medias==null || medias.size()<1)
				    return mediaInfo;
				
				HashMap sortMap = new HashMap();
			    String sql = "select flag,sortname from mediasort";
			    ContentDAO dao = new ContentDAO(this.conn);
			    rs = dao.search(sql);
			    while(rs.next()){
			    		sortMap.put(rs.getString("flag"), rs.getString("sortname"));
			    }
			    
				for(int i=0;i<medias.size();i++){
					Element med = (Element)medias.get(i);
					LazyDynaBean ldb = new LazyDynaBean();
					ldb.set("a0100",this.UserView.getA0100());
					ldb.set("nbase", this.UserView.getDbname());
					if("new".equals(med.getAttributeValue("type"))){
						ldb.set("topic", med.getAttributeValue("topic"));
						ldb.set("class", (String)sortMap.get(med.getAttributeValue("class")));
						ldb.set("classId", med.getAttributeValue("class"));
						ldb.set("srcfilename", med.getAttributeValue("srcfilename"));
						ldb.set("filename", med.getAttributeValue("filename"));
						ldb.set("path",med.getAttributeValue("path"));
						ldb.set("description", med.getAttributeValue("desc"));
						ldb.set("state", "new");
						ldb.set("mediaid", med.getAttributeValue("filename"));
					}else{
						
						ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
		                if(!VfsService.existPath())
		                      throw new GeneralException("没有设置多媒体存储路径！请在【系统管理-应用设置-参数设置-系统参数-文件存放目录】设置。");
		                  
						String id = med.getAttributeValue("fileid");
						sql = "select * from hr_multimedia_file where id=?";
						ArrayList values = new ArrayList();
						values.add(id);
						rs = dao.search(sql, values);
						if(rs.next()){
							ldb.set("topic", rs.getString("topic"));
							ldb.set("class", (String)sortMap.get(rs.getString("class")));
							ldb.set("classId", rs.getString("class"));
							String srcfilename = rs.getString("srcfilename");
							ldb.set("srcfilename",srcfilename);
							ldb.set("filename",rs.getString("filename"));
							ldb.set("path",rs.getString("path"));
							String desc = rs.getString("description");
							desc = desc==null?"":desc;
							ldb.set("description",desc);
							ldb.set("state", "delete");
							ldb.set("mediaid",id);
						}else{
							continue;
						}
					}
					mediaInfo.add(ldb);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return mediaInfo;
	}
	
	private A0100Bean getA0100bean(String chg_id) {
		StringBuffer sql = new StringBuffer();
		sql = new StringBuffer();
		sql.append("select a0100,nbase,b0110,e0122,e01a1,a0000,a0101 from t_hr_mydata_chg");
		sql.append(" where chg_id='" + chg_id + "'");
		A0100Bean bean = new A0100Bean();
		RowSet rs=null;
		try {
			rs = this.dao.search(sql.toString());
			if (rs.next()) {
				bean.setA0000(rs.getString("a0000"));
				bean.setA0101(rs.getString("a0101"));
				bean.setB0110(rs.getString("b0110"));
				bean.setE0122(rs.getString("e0122"));
				bean.setE01a1(rs.getString("e01a1"));
				bean.setA0100(rs.getString("a0100"));
				bean.setNbase(rs.getString("nbase"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
		return bean;
	}
	

	private A0100Bean getA0100bean(String nbase, String a0100) {
		StringBuffer sql = new StringBuffer();
		sql = new StringBuffer();
		sql
				.append("select b0110,e0122,e01a1,a0000,a0101 from " + nbase
						+ "A01");
		sql.append(" where a0100='" + a0100 + "'");
		A0100Bean bean = new A0100Bean();
		RowSet rs=null;
		try {
			rs = this.dao.search(sql.toString());
			if (rs.next()) {
				bean.setA0000(rs.getString("a0000"));
				bean.setA0101(rs.getString("a0101"));
				bean.setB0110(rs.getString("b0110"));
				bean.setE0122(rs.getString("e0122"));
				bean.setE01a1(rs.getString("e01a1"));
				bean.setA0100(a0100);
				bean.setNbase(nbase);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
		return bean;
	}

	public String getChgid(String nbase, String a0100, String sp_flag) {
		StringBuffer sql = new StringBuffer();
		sql.append("select chg_id from t_hr_mydata_chg");
		sql.append(" where a0100='" + a0100 + "' and sp_flag='" + sp_flag
				+ "' and nbase='" + nbase + "'");
		String chg_id = "";
		RowSet rs=null;
		try {
			rs = this.dao.search(sql.toString());
			if (rs.next()) {
				chg_id = rs.getString("chg_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
		return chg_id;
	}

	private synchronized boolean apployEdit(A0100Bean bean, FieldSet fieldSet,
			String keyvalue, String type, String sequence, Element element) {
		if (type == null || type.length() <= 0)
			return false;
		
		boolean isCorrect = false;
		try {
		    String newKeyValue = "-1";
			String fieldSetId = fieldSet.getFieldsetid();
			String tablename = bean.getNbase() + fieldSetId;
			StringBuffer fields = new StringBuffer();
			StringBuffer fieldvalues = new StringBuffer();
			List childList = element.getChildren("column");
			Iterator r = childList.iterator();
			String[] fieldsname = new String[childList.size()];
			String[] fieldcode = new String[childList.size()];
			int i = 0;
			while (r.hasNext()) {
				Element elementC = (Element) r.next();
				String name = elementC.getAttributeValue("name");
				String value = elementC.getAttributeValue("newvalue");
				String oldvalue=elementC.getAttributeValue("oldvalue");
				FieldItem fieldItem = DataDictionary.getFieldItem(name);
				//tianye update 查询已构库的指标
	            
                if(fieldItem==null || "0".equals(fieldItem.getUseflag())){
                    continue;
                }
        
                //tianye end;
				fieldItem.setValue(value);
				fields.append(fieldItem.getItemid());
				fieldsname[i] = fieldItem.getItemid();
				boolean flag = true;
				//2015-04-02  guodd  因为此处保存的是 报批时的数据，如果报批后做了人事异动，然后再批准修改，发现单位部门和岗位会又变成以前的值了。所以讲此处注掉
				if ("update".equals(type)/*&&!fieldsname[i].equalsIgnoreCase("B0110")&&!fieldsname[i].equalsIgnoreCase("E0122")&&!fieldsname[i].equalsIgnoreCase("E01A1")*/){
					if(value.equals(oldvalue)){
						flag=false;
					}
				}
				if ("D".equals(fieldItem.getItemtype())) {
					fieldvalues.append(PubFunc.DateStringChange(fieldItem
							.getValue()));
					if(flag){
						fieldcode[i] = PubFunc.DateStringChange(fieldItem
							.getValue());
					}else{
						fieldcode[i] = fieldsname[i];
					}
				} else if ("M".equals(fieldItem.getItemtype())) {
					if (fieldItem.getValue() == null
							|| "null".equals(fieldItem.getValue())
							|| "".equals(fieldItem.getValue())) {
						if(flag)
							fieldcode[i] = "null";
						else
							fieldcode[i] = fieldsname[i];
						fieldvalues.append("null");
					} else {
						if(flag)
							fieldcode[i] = "'" + fieldItem.getValue() + "'";
						else
							fieldcode[i] = fieldsname[i];
						fieldvalues.append("'" + fieldItem.getValue() + "'");
					}
				} else if ("N".equals(fieldItem.getItemtype())) {
					if (fieldItem.getValue() == null
							|| "null".equals(fieldItem.getValue())
							|| "".equals(fieldItem.getValue())) {
						if(!flag)
							fieldcode[i] = fieldsname[i];
						else
							fieldcode[i] = "null";
						fieldvalues.append("null");
					} else {
						if(flag)
							fieldcode[i] = fieldItem.getValue();
						else
							fieldcode[i] = fieldsname[i];
						fieldvalues.append(fieldItem.getValue());
					}
				} else {
					if (fieldItem.getValue() == null
							|| "null".equals(fieldItem.getValue())
							|| "".equals(fieldItem.getValue())) {
						if(flag)
							fieldcode[i] = "null";
						else
							fieldcode[i] = fieldsname[i];
						fieldvalues.append("null");
					} else {
						if(flag){
							fieldcode[i] = "'"
								+ PubFunc.splitString(fieldItem.getValue(),
										fieldItem.getItemlength()) + "'";
						}else
							fieldcode[i] = fieldsname[i];
						fieldvalues.append("'"
								+ PubFunc.splitString(fieldItem.getValue(),
										fieldItem.getItemlength()) + "'");
					}
				}
				fields.append(",");
				fieldvalues.append(",");
				i++;
			}
			 structureExecSqlString = new StructureExecSqlString();
			structureExecSqlString.setFieldcode(fieldcode);
			String insertGuidkey = getGuid();
			if ("new".equals(type) || "insert".equals(type)) {
				String ids = structureExecSqlString.InfoInsert("1", tablename, fields
						.toString(), fieldvalues.toString(), bean.getA0100(),
						this.UserView.getUserName(), this.conn);
				if (ids != null && ids.length() > 0) {
					isCorrect = true;
					newKeyValue = ids;
				}
				
				if(!"A01".equalsIgnoreCase(fieldSetId)) {
					String updateSql = "update " + tablename + " set guidkey=? where a0100=? and i9999=?";
					ArrayList<String> paramList = new ArrayList<String>();
					if("insert".equals(type)) {
						paramList.add(insertGuidkey);
					} else {
						paramList.add(keyvalue);
					}
					paramList.add(bean.getA0100());
					paramList.add(ids);
					dao.update(updateSql, paramList);
				}
				
				if ("insert".equals(type))
					isCorrect = updateRecord(getI9999(tablename, bean.getA0100(), keyvalue), tablename, bean.getA0100(), this.dao);
				
			} else if ("delete".equals(type)) {
				isCorrect = deleteRecord(getI9999(tablename, bean.getA0100(), keyvalue), tablename, bean.getA0100(), this.dao);

			} else if ("update".equals(type)) {
				isCorrect = structureExecSqlString.InfoUpdate("1", tablename, fieldsname,
						fieldcode, bean.getA0100(), getI9999(tablename, bean.getA0100(), keyvalue), this.UserView
								.getUserName(), this.conn);
			
			}
			
			List medias = (List)element.getChildren("multimedia");
			if(medias.size()>0){
				String i9999 = getI9999(tablename, bean.getA0100(), keyvalue);
				if("new".equals(type))
					i9999 = newKeyValue;
				else if("insert".equals(type))
					i9999 = getI9999(tablename, bean.getA0100(), insertGuidkey);
				
				PersonInfoBo bo = new PersonInfoBo(conn,"-1", this.UserView);
				bo.saveOrDelMultiMedia(bean.getNbase(), bean.getA0100(), fieldSetId, i9999, medias);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			isCorrect = false;
		}
		return isCorrect;
	}

	/**
	 * 批准后插入修改记录i9999
	 * 
	 * @param I9999
	 * @param tablename
	 * @param A0100
	 * @param dao
	 */
	static private boolean updateRecord(String I9999, String tablename,
			String A0100, ContentDAO dao) {
		boolean flag = true;
		String upsql1 = "update " + tablename
				+ " set I9999=I9999+1 where I9999>=" + I9999 + "   and a0100='"
				+ A0100 + "' ";
		String upsql = "update " + tablename + " set I9999=" + I9999
				+ " where I9999=(select max(I9999) from " + tablename
				+ " where a0100='" + A0100 + "')  and a0100='" + A0100 + "'";
		try {
			dao.update(upsql1);
			dao.update(upsql);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 批准后删除记录
	 * 
	 * @param i9999
	 * @param tableName
	 * @param A0100
	 * @param dao
	 */
	private boolean deleteRecord(String i9999, String tableName, String A0100,
			ContentDAO dao) {
		boolean flag = true;
		String desql1 = "";
		if (tableName.length() == 3 && "01".equals(tableName.substring(1, 3))
				|| tableName.length() == 6
				&& "01".equals(tableName.substring(4, 6))) {
			desql1 = "delete  from " + tableName + " where a0100='" + A0100
					+ "' ";
		} else {
			desql1 = "delete  from " + tableName + " where I9999=" + i9999
					+ "and a0100='" + A0100 + "' ";
		}
		try {
		  //删除附件 2014-05-04 wangrd
		    if (tableName.length()==6){
		        String nbase = tableName.substring(0,3);
		        String setid = tableName.substring(3,6);
		        if (i9999==null) i9999="0";
		        MultiMediaBo mediabo= new MultiMediaBo(this.conn,this.UserView);
		        mediabo.deleteMultimediaFileByA0100("A", setid, nbase, A0100, Integer.parseInt(i9999));
		    }
		    
			dao.delete(desql1, new ArrayList());
			
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 
	 * @param content
	 */
	private void contentInit(String content) {
		if (content == null || content.length() <= 0) {
			StringBuffer strxml = new StringBuffer();
			strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
			strxml.append("<root>");
			strxml.append("</root>");
			content = strxml.toString();
		}
		try {
			this.doc = PubFunc.generateDom(content);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param content
	 */
	private void ideaInit(String idea) {
		if (idea!=null && !idea.contains("<?xml ")) {
			idea = "";
		}
		if (idea == null || idea.length() <= 0) {
			StringBuffer strxml = new StringBuffer();
			strxml.append("<?xml version='1.0' encoding='UTF-8'?>");
			strxml.append("<root>");
			strxml.append("</root>");
			idea = strxml.toString();
		}
		try {
			this.idea = PubFunc.generateDom(idea);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String queryBackidea(String idea) {
		String backIdea = "";
		this.ideaInit(idea);
		String xpath = "/root/rec";
		try {
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = new ArrayList();
			childlist = reportPath.selectNodes(this.idea);
			for (int i = 0; i < childlist.size(); i++) {
				Element el = (Element) childlist.get(i);
				String sp_state = el.getAttributeValue("sp_state");
				String name = el.getAttributeValue("name");
				String date = el.getAttributeValue("date");
				backIdea += "审批状态：" + sp_state + "\n";
				backIdea += "姓    名：" + name + "\n";
				backIdea += "时    间：" + date + "\n";
				if ("批准".equals(sp_state)) {
					backIdea += "批    示：" + el.getText() + "\n\r";
				} else if ("退回".equals(sp_state)) {
					backIdea += "退回原因：" + el.getText() + "\n\r";
				} else {
					backIdea += "\n";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return backIdea;
	}

	/**
	 * 把fielditem的值放到hashmap中保证前后的安全
	 * 
	 * @param filedList
	 * @return
	 */
	private HashMap getFiledMap(ArrayList filedList) {
		HashMap map = new HashMap();
		for (int i = 0; i < filedList.size(); i++) {
			FieldItem item = (FieldItem) filedList.get(i);
			map.put(item.getItemid(), item.getValue());
		}
		return map;
	}

	private String getXmlconent() {
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xmlContent = outputter.outputString(this.doc);
		return xmlContent;
	}

	private String getXmlconent(Document idea) {
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xmlContent = outputter.outputString(idea);
		return xmlContent;
	}

	public ArrayList getSetidList() {
		return setidList;
	}

	public void setSetidList(ArrayList setidList) {
		this.setidList = setidList;
	}

	public ArrayList getNewValueList() {
		return newValueList;
	}

	public void setNewValueList(ArrayList newValueList) {
		this.newValueList = newValueList;
	}

	public ArrayList getOldValueList() {
		return oldValueList;
	}

	public void setOldValueList(ArrayList oldValueList) {
		this.oldValueList = oldValueList;
	}

	public ArrayList getKeyvalueList() {
		return keyvalueList;
	}

	public void setKeyvalueList(ArrayList keyvalueList) {
		this.keyvalueList = keyvalueList;
	}

	public ArrayList getSp_flaglueList() {
		return sp_flaglueList;
	}

	public void setSp_flaglueList(ArrayList sp_flaglueList) {
		this.sp_flaglueList = sp_flaglueList;
	}

	public ArrayList getTypeList() {
		return typeList;
	}

	public void setTypeList(ArrayList typeList) {
		this.typeList = typeList;
	}

	public String getRecord_spflag() {
		return record_spflag;
	}

	public void setRecord_spflag(String record_spflag) {
		this.record_spflag = record_spflag;
	}

	public String getSp_idea() {
		return sp_idea;
	}

	public void setSp_idea(String sp_idea) {
		this.sp_idea = sp_idea;
	}

	public ArrayList getSequenceList() {
		return sequenceList;
	}

	public void setSequenceList(ArrayList sequenceList) {
		this.sequenceList = sequenceList;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * 报批，批准，退回对sp_idea的操作
	 * 
	 * @param userview
	 * @param orgAndName单位/部门/职位/姓名
	 * @param sp_state报批，批准，退回
	 * @param chg_id
	 */
	public synchronized void approval(UserView userview, String orgAndName,
			String sp_state, String chg_id) {
		String xmlIdea = "";
		// 操作时间
		String dateStr = "";
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		dateStr = dateFm.format(new java.util.Date());
		if("报批".equals(sp_state)) {
			queryInti2(chg_id);
		} else{
			queryInti(chg_id);
		}
		insertXml(sp_state, orgAndName, dateStr);
		xmlIdea = this.getXmlconent(this.idea);
		RecordVo vo = new RecordVo("t_hr_mydata_chg");
		try {
			vo.setString("chg_id", chg_id);
			vo = dao.findByPrimaryKey(vo);
			vo.setString("sp_idea", xmlIdea);
			
			dao.updateValueObject(vo);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * 退回对sp_idea的操作
	 * 
	 * @param userview
	 * @param orgAndName单位/部门/职位/姓名
	 * @param sp_state报批，批准，退回
	 * @param chg_id
	 */
	public synchronized void approval(UserView userview, String orgAndName,
			String sp_state, String chg_id, String returnValue) {
		String xmlIdea = "";
		// 操作时间
		String dateStr = "";
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		dateStr = dateFm.format(new java.util.Date());
		queryInti(chg_id);
		insertXml(sp_state, orgAndName, dateStr, returnValue);
		xmlIdea = this.getXmlconent(this.idea);
		RecordVo vo = new RecordVo("t_hr_mydata_chg");
		try {
		vo.setString("chg_id", chg_id);
		vo = dao.findByPrimaryKey(vo);
		vo.setString("sp_idea", xmlIdea);
		dao.updateValueObject(vo);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * 编辑当前记录的该相同子集的keyvalue，，因为插入使得keyvalue对应的i9999混乱，所以用此方法当插入是，把修改和删除的申请，keyvalue大于等于插入的时候keyvalue加一
	 * 
	 * @param fieldSet
	 * @param sp_flag
	 * @param type
	 * @param keyvalue
	 * @return
	 * @throws GeneralException
	 */

	public synchronized boolean editCurKeyValue(FieldSet fieldSet,
			String sp_flag, String type, String keyvalue, String operate,
			String chg_id, A0100Bean bean) throws GeneralException {
		// see("开始");
		if (type == null || !"insert".equalsIgnoreCase(type))
			return true;
		if (sp_flag == null || !"03".equals(sp_flag))
			return true;
		boolean isCorrect = true;
		String fieldSetId = fieldSet.getFieldsetid();
		int iKeyvalue = Integer.parseInt(keyvalue);
		String xpath = "";
		try {
			xpath = "/root/setid[@name='" + fieldSetId + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(this.doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue>='" + keyvalue
						+ "' and (@type='update' or @type='delete')]";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				while (r.hasNext())// 有记录
				{
					Element SetidR = (Element) r.next();
					String value = SetidR.getAttributeValue("keyvalue");
					String r_type = SetidR.getAttributeValue("type");
					if (r_type != null
							&& ("update".equalsIgnoreCase(r_type) || "delete"
									.equalsIgnoreCase(r_type))) {
						if (value != null && value.length() > 0) {
							int ivalue = Integer.parseInt(value);
							if (ivalue >= iKeyvalue
									&& "up".equalsIgnoreCase(operate)) {
								ivalue++;
								SetidR.setAttribute("keyvalue", String
										.valueOf(ivalue));
							}
						}
					}
				}
			}
		} catch (JDOMException e) {
			
			isCorrect = false;
			e.printStackTrace();
			return isCorrect;
		}
		// see("结束");
		return isCorrect;
	}

	/***************************************************************************
	 * 编辑其他记录的keyvalue，因为插入使得keyvalue对应的i9999混乱，所以用此方法当插入是，把修改和删除的申请，keyvalue大于等于插入的时候keyvalue加一
	 * 
	 * @param chg_id
	 * @param bean
	 * @param fieldSetId
	 * @param keyvalue
	 * @param operate
	 */
	private void editOtherRocordkeyvalue(String chg_id, A0100Bean bean,
			String fieldSetId, String keyvalue, String operate) {
		StringBuffer sql = new StringBuffer();
		sql.append("select content from t_hr_mydata_chg where chg_id<>'"
				+ chg_id + "'");
		sql.append(" and a0100='" + bean.getA0100() + "' and nbase='"
				+ bean.getNbase() + "'");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String content = "";
			rs = dao.search(sql.toString());
			while (rs.next()) {
				content = Sql_switcher.readMemo(rs, "content");
				if (content != null && content.length() > 0) {
					editOtherRocordkeyvalue(dao, content, chg_id, fieldSetId,
							keyvalue, operate);
				}
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
	}

	/**
	 * 编辑其他记录的keyvalue
	 * 
	 * @param dao
	 * @param content
	 * @param chg_id
	 * @param fieldSetId
	 * @param keyvalue
	 * @param operate
	 */
	private void editOtherRocordkeyvalue(ContentDAO dao, String content,
			String chg_id, String fieldSetId, String keyvalue, String operate) {
		try {
			Document doc = PubFunc.generateDom(content);
			String xpath = "/root/setid[@name='" + fieldSetId + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			int iKeyvalue = Integer.parseInt(keyvalue);
			Iterator t = childlist.iterator();
			boolean isCorrect = false;
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue>='" + keyvalue
						+ "' and (@type='update' or @type='delete')]";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				while (r.hasNext())// 有记录
				{
					Element SetidR = (Element) r.next();
					String value = SetidR.getAttributeValue("keyvalue");
					String r_type = SetidR.getAttributeValue("type");
					if (r_type != null
							&& ("update".equalsIgnoreCase(r_type) || "delete"
									.equalsIgnoreCase(r_type))) {
						if (value != null && value.length() > 0) {
							int ivalue = Integer.parseInt(value);
							if (ivalue >= iKeyvalue
									&& "up".equalsIgnoreCase(operate)) {
								ivalue++;
								SetidR.setAttribute("keyvalue", String
										.valueOf(ivalue));
								isCorrect = true;
							}
						}
					}
				}
			}
			if (isCorrect) {
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				content = outputter.outputString(doc);
				RecordVo vo = new RecordVo("t_hr_mydata_chg");
				vo.setString("chg_id", chg_id);
				vo = dao.findByPrimaryKey(vo);
				dao.updateValueObject(vo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	   public String setOrgInfo(String userbase, String A0100,
	            Connection connection) {
	        ContentDAO dao = new ContentDAO(connection);
	        StringBuffer strsql = new StringBuffer();
	        StringBuffer name = new StringBuffer();
	        String b0110 = "";
	        String e0122 = "";
	        String e01a1 = "";
	        String a0101 = "";
	        try {
	            if (userbase != null && userbase.length() > 0 && A0100 != null
	                    && A0100.length() > 0) {
	                strsql.append("select b0110,e0122,e01a1,a0101 from ");
	                strsql.append(userbase);
	                strsql.append("A01 where a0100='");
	                strsql.append(A0100);
	                strsql.append("'");
	                RowSet frowset = dao.search(strsql.toString());
	                if (frowset.next()) {
	                    b0110 = frowset.getString("B0110");
	                    e0122 = frowset.getString("E0122");
	                    e01a1 = frowset.getString("E01A1");
	                    a0101 = frowset.getString("a0101");
	                }
	            }
	        } catch (Exception e) {

	        } finally {
	            if (b0110 != null && b0110.trim().length() > 0)
	                b0110 = AdminCode.getCode("UN", b0110) != null ? AdminCode
	                        .getCode("UN", b0110).getCodename() : " ";
	            if (e0122 != null && e0122.trim().length() > 0)
	                e0122 = AdminCode.getCode("UM", e0122) != null ? AdminCode
	                        .getCode("UM", e0122).getCodename() : " ";
	            if (e01a1 != null && e01a1.trim().length() > 0)
	                e01a1 = AdminCode.getCode("@K", e01a1) != null ? AdminCode
	                        .getCode("@K", e01a1).getCodename() : " ";
	            if (a0101 != null && a0101.trim().length() > 0)
	                a0101 = a0101 != null ? a0101 : " ";
	        }

	        if (b0110 == null) {
	            name.append("");
	        } else {
	            name.append(b0110);
	        }       
	        name.append("/");
	        if (e0122 == null) {
	            name.append("");
	        } else {
	            name.append(e0122);
	        }
	        name.append("/");
	        if (e01a1 == null) {
	            name.append("");
	        } else {
	            name.append(e01a1);
	        }
	        name.append("/");
	        if (a0101 == null) {
	            name.append("");
	        } else {
	            name.append(a0101);
	        }

	        return name.toString();
	    }

	private void see(String point) {
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("GBK");
		outputter.setFormat(format);
		String xmlContent = outputter.outputString(this.doc);
		System.out.println(point
				+ "###########################################");
		System.out.println(xmlContent);
	}
	
	/**
	 * 领导批示时修改退回意见
	 * @param chg_id
	 * @param sp_idea
	 * @param org
	 */
	public void updateSp_idea(String chg_id, String sp_idea, String org) {
		this.queryInti(chg_id);
		try {
			Document idea = PubFunc.generateDom(this.sp_idea);		
//			//移除节点
//			while (it.hasNext()) {
//				Element el = (Element) it.next();
//				idea.getRootElement().removeContent(el);
//			}
			
			//增加节点
			Element el = new Element("rec");
			el.setAttribute("sp_state", "退回");
			el.setAttribute("name",org);
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			String dateStr = dateFm.format(new java.util.Date());
			el.setAttribute("date",dateStr);
			el.setText(sp_idea);
			idea.getRootElement().addContent(el);
			
			//将xml转成字符窜
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String content = outputter.outputString(idea);
			
			//更新数据库数据
			RecordVo vo = new RecordVo("t_hr_mydata_chg");
			vo.setString("chg_id", chg_id);
			vo = this.dao.findByPrimaryKey(vo);
			vo.setString("sp_idea", content);
			dao.updateValueObject(vo);
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 检测人员信息是否发生改变
	 * @param newFiledlist 编辑后的人员信息
	 * @param oldFilelist 编辑前的人员信息
	 * @return flag =false：未发生变化；=true：发生了变化
	 */
    public boolean isFieldValueChange(ArrayList newFieldList, ArrayList oldFieldList) {
        boolean flag = false;
        try{
            HashMap newFiledMap = getFiledMap(newFieldList);
            HashMap oldFileMap = getFiledMap(oldFieldList);
            for (int i = 0; i < newFieldList.size(); i++) {
                FieldItem item = (FieldItem) newFieldList.get(i);
                if(item == null)
                    continue;
                
                String newValue = (String) newFiledMap.get(item.getItemid());
                newValue = StringUtils.isEmpty(newValue) ? "" : newValue;
                String oldValue = (String) oldFileMap.get(item.getItemid());
                oldValue = StringUtils.isEmpty(oldValue) ? "" : oldValue;
                if(!newValue.equals(oldValue)) {
                    flag = true;
                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public ArrayList getMultimediaInfoList() {
        return multimediaInfoList;
    }

    public void setMultimediaInfoList(ArrayList multimediaInfoList) {
        this.multimediaInfoList = multimediaInfoList;
    }
    
    public boolean deleteMyselfChangeData(A0100Bean bean, FieldSet fieldSet,
            String type, String guidkey, String sequence)
            throws GeneralException {
        boolean flag = false;
        String sp_flag = "";
        String fieldSetId = fieldSet.getFieldsetid();
        String fieldSetdesc = fieldSet.getFieldsetdesc();
        String xpath = "";
        if ("A01".equalsIgnoreCase(fieldSetId))
        	guidkey = bean.getA0100();
        
        if ("new".equalsIgnoreCase(type))
        	guidkey = getGuid();
        
        if ("insert".equalsIgnoreCase(type) && (sequence == null || sequence.length() <= 0))
            sequence = getInsertSequence(fieldSetId, type, guidkey);
        else if (sequence == null || sequence.length() <= 0) 
            sequence = "1";
        
        try {
            xpath = "/root/setid[@name='" + fieldSetId + "']";
            XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
            List childlist = reportPath.selectNodes(this.doc);
            Iterator t = childlist.iterator();
            if (t.hasNext()) {
                Element fieldSetE = (Element) t.next();
                xpath = "record[@keyvalue='" + guidkey + "' and @type='" + type
                        + "' and @sequence='" + sequence + "']";// 选择上下文节点的
                // setid
                // 元素孩子的record元素keyvalue=i9999
                reportPath = XPath.newInstance(xpath);// 取得子集记录结点
                childlist = reportPath.selectNodes(fieldSetE);
                Iterator r = childlist.iterator();
                // 有记录
                if (r.hasNext()){
                    Element fieldSetR = (Element) r.next();
                    sp_flag = fieldSetR.getAttributeValue("sp_flag") != null
                        && fieldSetR.getAttributeValue("sp_flag").length() > 0 
                        ? fieldSetR.getAttributeValue("sp_flag") : "";
                    if(",01,07,".contains("," + sp_flag + ",")) {
                        fieldSetE.removeContent(fieldSetR);// (fieldSetR);
                    }
                    
                }
                
                childlist = reportPath.selectNodes(fieldSetE);
                if(childlist.size() < 1)
                    this.doc.getRootElement().removeContent(fieldSetE);
            }
            
            flag = true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

	/**
	 * 获取子集数据对应的guidkey
	 * 
	 * @param tablename
	 *            子集
	 * @param a0100
	 *            人员编号
	 * @param i9999
	 *            i9999
	 * @return
	 */
	private String getKeyValue(String tablename, String a0100, String i9999) {
		String keyValue = "";
		RowSet rs = null;
		try {
			DbWizard db = new DbWizard(this.conn);
			Field field = new Field("GUIDKEY", "子集数据唯一标识");
			field.setDatatype(DataType.STRING);
			field.setKeyable(false);
			field.setLength(38);
			String sql = "select pre from dbname";
			rs = dao.search(sql);
			String fielsetId = tablename.substring(3, 6);
			while (rs.next()) {
				String nbase = rs.getString("pre");
				if (!db.isExistField(nbase + fielsetId, "guidkey", false)) {
					Table table = new Table(nbase + fielsetId);
					table.addField(field);
					db.addColumns(table);
				}
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append("select guidkey from ");
			sb.append(tablename);
			sb.append(" where a0100=?");
			sb.append(" and i9999=?");
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(a0100);
			paramList.add(i9999);
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString(), paramList);
			if (rs.next()) {
				keyValue = rs.getString("guidkey");
				if (StringUtils.isEmpty(keyValue)) {
					StringBuffer stmp = new StringBuffer();
					stmp.append("update ");
					stmp.append(tablename);
					stmp.append(" set guidkey=?");
					stmp.append(" where a0100=?");
					stmp.append(" and i9999=?");
					stmp.append(" and guidkey is null");
					keyValue = getGuid();
					paramList.add(0, keyValue);

					dao.update(stmp.toString(), paramList);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return keyValue;
	}

	/**
	 * 生成guid
	 * 
	 * @return
	 */
	private static String getGuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * 获取子集数据对应的i9999
	 * 
	 * @param tableName
	 *            子集
	 * @param a0100
	 *            人员编号
	 * @param guidkey
	 *            guidkey
	 * @return
	 */
	private String getI9999(String tableName, String a0100, String guidkey) {
		RowSet rs = null;
		String i9999 = "0";
		try {
			if (tableName.toUpperCase().endsWith("A01")) {
				return i9999;
			}

			String sql = "select i9999 from " + tableName + " where a0100=? and guidkey=?";
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(a0100);
			paramList.add(guidkey);
			rs = dao.search(sql, paramList);
			if (rs.next()) {
				i9999 = rs.getString("i9999");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return i9999;
	}
}
