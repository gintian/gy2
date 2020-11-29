package com.hjsj.hrms.module.template.templatemain.transaction;

import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateItem;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * <p>
 * Title:TemplateMainTrans.java
 * </p>
 * <p>
 * Description>:自动引入模板数据
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2019-10-24
 * </p>
 * <p>
 * 
 * @version: 7.6
 *           </p>
 */
public class TemplateImportDataTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			if (!PubFunc.isUseNewPrograme(this.userView))
				throw GeneralExceptionHandler.Handle(new Exception("新异动程序仅支持70版本以上的加密锁!"));
			String tabId = (String) this.getFormHM().get("tabid");
			/** taskId固定死 为0 */
			String taskId = "0";
			String type = (String) this.getFormHM().get("type");
			/**固定为人事异动的*/
			String moduleId = "1";
			/**信息群类型 为了以后人事代码机构增加用 =1 人员模板 =2 单位模板 =3 岗位模板*/
			if("2".equals(type)) {
				moduleId = "7";
			}else if("3".equals(type)) {
				moduleId = "8";
			}
			ArrayList list = (ArrayList) this.getFormHM().get("savedata"); // 获取变化的数据
			/**验证当前数据是否是机构人员模板*/
			boolean bl=checkModuleType(type,list);
			if(!bl) {
				this.getFormHM().put("flag", false);
				this.getFormHM().put("errMsg", "传入数据与当前模板不匹配！");
				return;
			}
			
			/** 插入模板数据前的 验证。。 start **/
			TemplateParam paramBo = new TemplateParam(this.getFrameconn(), this.userView, Integer.parseInt(tabId));
			TemplateBo templateBo = new TemplateBo(this.getFrameconn(), this.userView, paramBo);

			templateBo.setModuleId(moduleId);
			templateBo.setTaskId(taskId);
			TemplateParam tableParamBo = templateBo.getParamBo();
			if (tableParamBo.getTable_vo().getValues().size() == 0)// liuyz bug32523 如果根据模版id不存在提示用户
			{
				throw new GeneralException("此模板不存在！");
			}

			// 获取表名
			TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn, this.userView);
			String tableName = utilBo.getTableName(moduleId, Integer.parseInt(tabId), taskId);
			// 创建临时表
			templateBo.createTempTemplateTable("");
			/** 插入模板数据前的 验证。。 end **/

			/** 导入前删除数据 */
			deleteFillInfo(tableName);

			/** 插入数据 */
			/**
			 * 信息群类型 为了以后人事代码机构增加用 =1 人员模板 =2 单位模板 =3 岗位模板
			 */
			int infor_type = paramBo.getInfor_type();

			Boolean flag = true;// 所有数据导入成功
			if (infor_type == 1) {// 人事
				HashSet set = new HashSet();// 存放所有人员库前缀
				for (int i = 0; i < list.size(); i++) {
					MorphDynaBean bean = (MorphDynaBean) list.get(i);
					HashMap map = PubFunc.DynaBean2Map(bean);
					String a0100 = (String) map.get("a0100");
					if (StringUtils.isEmpty(a0100)) {
						continue;
					}
					String basepre = (String) map.get("basepre");
					if (StringUtils.isEmpty(basepre)) {
						continue;
					}
					set.add(basepre);
				}
				// 导入数据
				for (Iterator i = set.iterator(); i.hasNext();) {// 遍历人员库前缀，按人员库前缀分类导入数据
					String dbName = (String) i.next();
					if (dbName == null || dbName.length() == 0)
						continue;
					ArrayList a0100s = new ArrayList();// 人员编号
					for (int j = 0; j < list.size(); j++) {
						MorphDynaBean bean = (MorphDynaBean) list.get(j);
						HashMap map = PubFunc.DynaBean2Map(bean);
						String a0100 = (String) map.get("a0100");
						if (StringUtils.isEmpty(a0100)) {
							continue;
						}
						String basepre = (String) map.get("basepre");
						if (StringUtils.isEmpty(basepre)) {
							continue;
						}
						if (basepre.equalsIgnoreCase(dbName)) {
							a0100s.add(PubFunc.decrypt(a0100));
						}
					}
					if (a0100s.size() > 0) {
						Boolean resultflag = templateBo.impDataFromArchive(a0100s, dbName);// 按人员库前缀导入数据
						if (!resultflag)
							flag = false;// 导入失败
					}
				}
			} else if (infor_type == 2) {// 单位
				ArrayList a0100s = new ArrayList();// 人员编号
				for (int i = 0; i < list.size(); i++) {
					MorphDynaBean bean = (MorphDynaBean) list.get(i);
					HashMap map = PubFunc.DynaBean2Map(bean);
					String b0110 = (String) map.get("b0110");
					if (StringUtils.isEmpty(b0110)) {
						continue;
					}
					a0100s.add(b0110);
				}
				if (a0100s.size() > 0) {
					Boolean resultflag = templateBo.impDataFromArchive(a0100s, "B");// 按人员库前缀导入数据
					if (!resultflag)
						flag = false;// 导入失败
				}
			} else if (infor_type == 3) {// 岗位
				ArrayList a0100s = new ArrayList();// 人员编号
				for (int i = 0; i < list.size(); i++) {
					MorphDynaBean bean = (MorphDynaBean) list.get(i);
					HashMap map = PubFunc.DynaBean2Map(bean);
					String e01a1 = (String) map.get("e01a1");
					if (StringUtils.isEmpty(e01a1)) {
						continue;
					}
					a0100s.add(e01a1);
				}
				if (a0100s.size() > 0) {
					Boolean resultflag = templateBo.impDataFromArchive(a0100s, "B");// 按人员库前缀导入数据
					if (!resultflag)
						flag = false;// 导入失败
				}
			}
			/** 自动导入报错 ***/
			if (!flag) {
				this.getFormHM().put("flag", flag);
				this.getFormHM().put("errMsg", "自动导入信息时失败");
				return;
			}

			/** 开始导入 变化后对指标数据 ***/
			TemplateDataBo tableDataBo = new TemplateDataBo(this.getFrameconn(), this.userView, paramBo);
			ArrayList cellList = utilBo.getAllCell(Integer.parseInt(tabId));
			// 查找变化前的历史记录单元格,保存时把这部分单元格的内容过滤掉，不作处理
			HashMap filedPrivMap = tableDataBo.getFieldPrivMap(cellList, taskId);
			ArrayList fieldList = filterTemplateSetList(cellList, filedPrivMap);
			HashMap fieldMap = new HashMap();
			for (int i = 0; i < fieldList.size(); i++) {
				TemplateSet setBo = (TemplateSet) fieldList.get(i);
				fieldMap.put(setBo.getTableFieldName(), "1");
			}

			ContentDAO dao = new ContentDAO(this.getFrameconn());
			for (int i = 0; i < list.size(); i++) {
				MorphDynaBean bean = (MorphDynaBean) list.get(i);
				HashMap map = PubFunc.DynaBean2Map(bean);
				ArrayList updFieldList = new ArrayList();// 要修改的字段
				ArrayList updDataList = new ArrayList(); // 要修改字段对应的数据

				for (int j = 0; j < fieldList.size(); j++) {
					boolean bUpdA0101_1 = false;// 是否需要同步更改变化前姓名 人员调入、新增机构模板需要 wangrd 20160908
					TemplateSet setBo = (TemplateSet) fieldList.get(j);
					if ("C".equals(setBo.getFlag()) || "P".equals(setBo.getFlag())) {
						continue;
					}
					String fieldName = setBo.getTableFieldName();
					if (updFieldList.contains(fieldName)) {// 排除多个单元格指定同一指标的情况。
						continue;
					}

					int updDataSize = updDataList.size();
					// liuyz 28807
					// 首先先判断用户是否修改了这个字段，数值型表格控件删除指后传过来的值为null，手工在这里修改一下让用户清空值能保存上,否则用户无法保存清空值。
					if (map.containsKey(fieldName) && map.get(fieldName) == null && "N".equals(setBo.getField_type())) {
						map.put(fieldName, "");
					}
					if (map.get(fieldName.substring(0,fieldName.length()-2)) != null) {// record有此指标的值
						String data = "";
						if ("signature".equals(fieldName)) {
							continue;
						} else {
							data = map.get(fieldName.substring(0,fieldName.length()-2)) + "";
						}
						if (data == null)
							data = "";

						if (setBo.isABKItem() && !setBo.isSubflag()) {// 普通指标
							TemplateItem templateItem = utilBo.convertTemplateSetToTemplateItem(setBo);// lis 20160705
							FieldItem fldItem = templateItem.getFieldItem();
							if (fldItem == null)
								continue;
							if (setBo.isBcode()) {// 代码型
								if (data != null && data.length() > 0) {
									String[] arrData = data.split("`");
									if (arrData.length > 0) {
										data = arrData[0];
									} else {// 兼容data="`"的情况
										data = data.replace("`", "");
									}

								}
								updDataList.add(data);
							} else if ("D".equals(fldItem.getItemtype())) {//
								String disformat = setBo.getDisformat() + ""; // disformat=25: 1990.01.01 10:30
								if (StringUtils.isNotBlank(data)) {
									if (!"25".equals(disformat)) {
										java.sql.Date date = null;
										String dateStr = data;
										if (dateStr.indexOf("-") < 0)
											date = DateUtils.getSqlDate(data, "yyyy.MM.dd");
										else
											date = DateUtils.getSqlDate(data, "yyyy-MM-dd");
										updDataList.add(date);
									} else {
										Timestamp datetime = DateUtils.getTimestamp(data + ":00",
												"yyyy-MM-dd HH:mm:ss");
										updDataList.add(datetime);
									}
								} else {
									updDataList.add(null);
								}
							} else if ("N".equals(fldItem.getItemtype())) {
								if (data.indexOf(".") != -1) {
									if (data.split("\\.")[0].length() > fldItem.getItemlength()) {
										String valueLengthError = fldItem.getItemdesc()
												+ ResourceFactory.getProperty("templa.value.lengthError")
												+ fldItem.getItemlength() + ","
												+ ResourceFactory.getProperty("templa.value.fix");
										throw new Exception(valueLengthError.toString());
									}
								} else {
									if (data.length() > fldItem.getItemlength()) {
										String valueLengthError = fldItem.getItemdesc()
												+ ResourceFactory.getProperty("templa.value.lengthError")
												+ fldItem.getItemlength() + ","
												+ ResourceFactory.getProperty("templa.value.fix");
										throw new Exception(valueLengthError.toString());
									}
								}
								if (fldItem.getDecimalwidth() == 0) {
									// liuyz bug26865
									// 指标设置的长度和模版设置的小数长度不同，导致用户可以输入带小数的值，这里fldItem取的是指标小数位数，所以转换时会出现异常。给出提示。
									try {
										updDataList.add(data.length() == 0 ? null : Integer.parseInt(data));
									} catch (Exception e) {
										throw new Exception("指标项：" + fldItem.getItemdesc() + "不允许有小数位");
									}
								} else {
									String value = PubFunc.DoFormatDecimal(
											data == null || data.length() == 0 ? "" : data, fldItem.getDecimalwidth());
									updDataList.add(value.length() == 0 ? null : PubFunc.parseDouble(value));
								}
							} else if ("M".equals(fldItem.getItemtype())) {
								String opinion_field = paramBo.getOpinion_field();
								// liuyz 大文本html编辑器不需要检测是否超过字数限制。但是fldItem中的inputType不对，需要重新获取。
								FieldItem fielditem = DataDictionary.getFieldItem(fldItem.getItemid());
								if (fielditem != null)
									fldItem.setInputtype(fielditem.getInputtype());
								if (StringUtils.isNotBlank(opinion_field)
										&& !opinion_field.equalsIgnoreCase(fldItem.getItemid())
										&& fldItem.getItemlength() != 10 && fldItem.getItemlength() != 0
										&& fldItem.getInputtype() != 1) {
									// if(StringUtils.isNotBlank(opinion_field) &&
									// !opinion_field.equalsIgnoreCase(fldItem.getItemid())&&fldItem.getItemlength()!=10&&fldItem.getItemlength()!=0&&fldItem.getInputtype()!=1){
									if (data.length() > fldItem.getItemlength()) {
										StringBuffer valueLengthError = new StringBuffer();

										valueLengthError.append(ResourceFactory.getProperty("template_new.filed"));
										valueLengthError.append("[");
										valueLengthError.append(fldItem.getItemdesc());
										valueLengthError.append("]");
										valueLengthError.append(
												ResourceFactory.getProperty("template_new.allowMaxInputLength"));
										valueLengthError.append(fldItem.getItemlength());
										valueLengthError.append(ResourceFactory.getProperty("template_new.char"));
										throw new Exception(valueLengthError.toString());
									}
								}
								updDataList.add(data);
							} else {
								if (TemplateFuncBo.getStrLength(data) > fldItem.getItemlength()) {
									StringBuffer valueLengthError = new StringBuffer();

									valueLengthError.append(ResourceFactory.getProperty("template_new.filed"));
									valueLengthError.append("[");
									valueLengthError.append(fldItem.getItemdesc());
									valueLengthError.append("]");
									valueLengthError
											.append(ResourceFactory.getProperty("template_new.allowMaxInputLength"));
									valueLengthError.append(fldItem.getItemlength());
									valueLengthError.append(ResourceFactory.getProperty("template_new.char"));
									throw new Exception(valueLengthError.toString());
								}
								updDataList.add(data);
								if ("a0101_2".equalsIgnoreCase(fieldName)) {// 变化后姓名
									if (paramBo.getOperationType() == 0) {// 人员调入模板
										updDataList.add(data);
										bUpdA0101_1 = true;
									}

								} else if ("codeitemdesc_2".equalsIgnoreCase(fieldName)) {// 变化后机构名称
									if (paramBo.getOperationType() == 5) {// 新增机构、岗位
										updDataList.add(data);
										bUpdA0101_1 = true;
									}
								}
							}
						} else if (setBo.isSubflag()) {
							continue;
						} else if ("S".equalsIgnoreCase(setBo.getFlag())) {
							continue;
						} else {// 临时变量
							if (setBo.isBcode()) {// 代码型
								if (data != null && data.length() > 0) {
									String[] arrData = data.split("`");
									if (arrData.length > 0) {
										data = arrData[0];
									} else {// 兼容data="`"的情况
										data = data.replace("`", "");
									}

								}
								updDataList.add(data);
							} else if ("D".equals(setBo.getField_type())) {//
								if (StringUtils.isNotBlank(data)) {
									java.sql.Date date = null;
									String dateStr = data;
									if (dateStr.indexOf("-") < 0)
										date = DateUtils.getSqlDate(data, "yyyy.MM.dd");
									else
										date = DateUtils.getSqlDate(data, "yyyy-MM-dd");
									updDataList.add(date);
								} else {
									updDataList.add(null);
								}
							} else if ("N".equals(setBo.getField_type())) {
								int flddec = setBo.getVarVo().getInt("flddec");
								int fldlen = setBo.getVarVo().getInt("fldlen");
								String chz = setBo.getVarVo().getString("chz");
								if (data.indexOf(".") != -1) {
									if (data.split("\\.")[0].length() > fldlen) {
										String valueLengthError = chz
												+ ResourceFactory.getProperty("templa.value.lengthError") + fldlen + ","
												+ ResourceFactory.getProperty("templa.value.fix");
										throw new Exception(valueLengthError.toString());
									}
								} else {
									if (data.length() > fldlen) {
										String valueLengthError = chz
												+ ResourceFactory.getProperty("templa.value.lengthError") + fldlen + ","
												+ ResourceFactory.getProperty("templa.value.fix");
										throw new Exception(valueLengthError.toString());
									}
								}
								if (flddec == 0) {
									updDataList.add(data.length() == 0 ? null : Integer.parseInt(data));
								} else {
									String value = PubFunc
											.DoFormatDecimal(data == null || data.length() == 0 ? "" : data, flddec);
									updDataList.add(value.length() == 0 ? null : PubFunc.parseDouble(value));
								}
							} else if ("A".equals(setBo.getField_type()) && "0".equals(setBo.getCodeid())) {// 增加临时变量字符型长度校验
								int fldlen = setBo.getVarVo().getInt("fldlen");
								String chz = setBo.getVarVo().getString("chz");
								if (TemplateFuncBo.getStrLength(data) > fldlen) {
									StringBuffer valueLengthError = new StringBuffer();
									valueLengthError.append(ResourceFactory.getProperty("label.gz.variable"));
									valueLengthError.append("[");
									valueLengthError.append(chz);
									valueLengthError.append("]");
									valueLengthError
											.append(ResourceFactory.getProperty("template_new.allowMaxInputLength"));
									valueLengthError.append(fldlen);
									valueLengthError.append(ResourceFactory.getProperty("template_new.char"));
									throw new Exception(valueLengthError.toString());
								}
								updDataList.add(data);
							} else
								updDataList.add(data);
						}

						if (updDataList.size() > updDataSize) {// datalist放入数据了 fieldlist也得相应增加
							updFieldList.add(fieldName);
							
							if (bUpdA0101_1) {
								if ("a0101_2".equalsIgnoreCase(fieldName)) {// 变化后姓名
									updFieldList.add("a0101_1");
								} else if ("codeitemdesc_2".equalsIgnoreCase(fieldName)) {// 变化后机构名称
									updFieldList.add("codeitemdesc_1");
								}
							}
						}
					}
				}
				String updateSql = "update " + tableName + " set ";
				String fieldName = null;
				StringBuffer updateFields = new StringBuffer();
				for (int j = 0; j < updFieldList.size(); j++) {
					fieldName = (String) updFieldList.get(j);
					updateFields.append("," + fieldName + "=?");
				}
				if (updateFields.length() > 1)
					updateSql += updateFields.substring(1);
				if (paramBo.getInfor_type() == 1) {
					String basepre = "";
					String a0100 = "";
					basepre = (String) map.get("basepre");
					a0100 = (String) map.get("a0100");
					a0100 = PubFunc.decrypt(a0100);
					updateSql += " where A0100='" + a0100 + "' and BasePre='" + basepre + "'";
				} else if (paramBo.getInfor_type() == 2) {
					String b0110 = "";
					b0110 = (String) map.get("b0110");

					updateSql += " where b0110='" + b0110 + "'";
				} else {
					String e01a1 = "";
					e01a1 = (String) map.get("e01a1");
					updateSql += " where e01a1='" + e01a1 + "'";
				}
				if (updFieldList.size() > 0) {
					dao.update(updateSql, updDataList);
				}
			}
			this.getFormHM().put("flag", true);
			this.getFormHM().put("errMsg", "");
		} catch (Exception e) {
			this.getFormHM().put("flag", false);
			this.getFormHM().put("errMsg", "导入信息时失败，其报错信息为："+e.getMessage());
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 删除之前记录
	 * 
	 * @param tableName
	 */
	private void deleteFillInfo(String tableName) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			ArrayList values = new ArrayList();
			String sql = "delete from " + tableName;
			dao.delete(sql, values);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * @Title: filterTemplateSetList @Description: 过滤掉无权限的指标 @param @param
	 *         templateSetList @param @return @return ArrayList @throws
	 */
	private ArrayList filterTemplateSetList(ArrayList cellList, HashMap filedPrivMap) {
		ArrayList fieldList = new ArrayList();
		for (int i = cellList.size() - 1; i >= 0; i--) {
			TemplateSet setBo = (TemplateSet) cellList.get(i);
			String fieldname = setBo.getTableFieldName();
			if ("signature".equals(fieldname)) {
				fieldList.add(setBo);
				continue;
			}

			if (setBo.isSubflag()) {// 子集变化前也可以保存 wangrd 20160829 不区分权限了。
				fieldList.add(setBo);
				continue;
			}

			if ("".equals(fieldname)) {
				continue;
			}
			if (setBo.getChgstate() == 1 && (!"V".equals(setBo.getFlag()))) {// 变化前 临时变量
				continue;
			}
			if (setBo.getChgstate() != 2) {// 排除为2 的
				continue;
			}
			if (filedPrivMap.get(setBo.getUniqueId()) != null) {
				String rwPriv = (String) filedPrivMap.get(setBo.getUniqueId());
				if (!"2".equals(rwPriv)) {
					continue;
				}
			}
			fieldList.add(setBo);
		}
		return fieldList;
	}
	/**
	 * 信息群类型 为了以后人事代码机构增加用 =1 人员模板 =2 单位模板 =3 岗位模板
	 */
	private boolean checkModuleType(String type,ArrayList list){
		boolean bl=false;
		if("1".equals(type)) {
			if(list.size()>0) {
				MorphDynaBean bean = (MorphDynaBean) list.get(0);
				HashMap map = PubFunc.DynaBean2Map(bean);
				if(map.containsKey("a0100")||map.containsKey("basepre")) {
					bl=true;
				}
			}
		}else if("2".equals(type)) {
			if(list.size()>0) {
				MorphDynaBean bean = (MorphDynaBean) list.get(0);
				HashMap map = PubFunc.DynaBean2Map(bean);
				if(map.containsKey("b0110")) {
					bl=true;
				}
			}

		}else if("3".equals(type)) {
			if(list.size()>0) {
				MorphDynaBean bean = (MorphDynaBean) list.get(0);
				HashMap map = PubFunc.DynaBean2Map(bean);
				if(map.containsKey("e0122")) {
					bl=true;
				}
			}
		}
		return bl;
	}
	
	
}
