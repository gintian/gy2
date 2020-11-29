package com.hjsj.hrms.module.kq.kqdata.businessobject.impl;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.kq.config.item.businessobject.KqItemService;
import com.hjsj.hrms.module.kq.config.item.businessobject.impl.KqItemServiceImpl;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.impl.SchemeMainServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.ImportKqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 考勤明细 导入月汇总
 * 
 * @date 2020.03.05
 * @author xuanz
 *
 */
public class ImportKqDataMxServiceImpl implements ImportKqDataMxService {

	private UserView userView;
	private Connection conn;
	HashMap<Integer, ArrayList<String>> msgMap = new HashMap<Integer, ArrayList<String>>();
    private String org_name = "";
	public ImportKqDataMxServiceImpl(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}
	@Override
	public void importKqData(String fileid, String scheme_id, String kq_duration, String kq_year, String org_id, String type) {
		try {
			scheme_id = PubFunc.decrypt(scheme_id);
			org_id = PubFunc.decrypt(org_id);
			this.userView.getHm().remove("errorMsg");
			ArrayList<Object> kqDataList = readExcel(fileid);
			if (kqDataList.size()>0) {
			    ArrayList<Object> kqDataMapList =checkCardData(kqDataList, scheme_id, kq_duration, kq_year, org_id);
			    if (getErrorMsg().length()==0) {
			        saveCardData(kqDataMapList, scheme_id, kq_duration, kq_year, org_id);
			    }
            }
		} catch (Exception e) {
			this.userView.getHm().put("errorMsg", e.toString());
			e.printStackTrace();
		}
		
	}
	/**
	 * 解析模板数据
	 * 
	 * @param fileName
	 *            导入的文件路径
	 * @return
	 */
	private ArrayList<Object> readExcel(String fileid) {
		ArrayList<Object> KqDataList = new ArrayList<Object>();
		StringBuffer errorMsg = new StringBuffer();
		InputStream stream = null;
		RowSet rs = null;
		Workbook wb = null;
		try {
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
            String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
            String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
			stream = VfsService.getFile(fileid);
			wb = WorkbookFactory.create(stream);
			Sheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(0);
			if (row == null) {
				errorMsg.append(ResourceFactory.getProperty("kq.card.sysTemplate"));
				this.userView.getHm().put("errorMsg",errorMsg);
				return KqDataList;
			}

			int cols = row.getPhysicalNumberOfCells();
			int rows = sheet.getPhysicalNumberOfRows();
			HashMap<String, Integer> colsIndexMap = new HashMap<String, Integer>();
			ArrayList<String> keyList = new ArrayList<String>();
			//通过唯一性指标判断人员是否重复
            String onlyFieldStr="";
			for (int i = 0; i < cols; i++) {
				Cell cell = row.getCell(i);
				if (cell == null) {
					continue;
				}
				
				Comment myComment = cell.getCellComment();
				if (myComment == null) {
					continue;
				}
				
				keyList.add(myComment.getString().toString().toLowerCase());
				colsIndexMap.put(myComment.getString().toString().toLowerCase(), i);
			}
			
			if(keyList == null || keyList.size() < 1) {
				errorMsg.append(ResourceFactory.getProperty("kq.card.sysTemplate"));
				this.userView.getHm().put("errorMsg",errorMsg);
				return KqDataList;
			}
			//判断是否有唯一性指标列
			if (!keyList.contains("only_field_")) {
				errorMsg.append("导入数据不包含唯一性指标列");
				this.userView.getHm().put("errorMsg",errorMsg);
				return KqDataList;
			}
			KqDataList.add(keyList);
			ArrayList<HashMap<String, Object>> valueList = new ArrayList<HashMap<String, Object>>();
			for (int m = 1; m < rows; m++) {
				row = sheet.getRow(m);
				if (row == null||row.getCell(0)==null) {
					continue;
				}
				ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("Q35", 1);
				FieldItem fi=new FieldItem();
				HashMap<String, Object> valueMap = new HashMap<String, Object>();
				for (int i = 0; i < keyList.size(); i++) {
					String itemid = keyList.get(i);
					if ("id".equalsIgnoreCase(itemid)) {
						valueMap.put(itemid, m);
						continue;
					}
					//判断表格列头是否有效，即列批注是否为Q35中指标id
					boolean isHave=false;
					for (FieldItem fieldItem : fieldList) {
						if (itemid.equals(fieldItem.getItemid())) {
							isHave=true;
							fi=fieldItem;
						}
						if ("only_field_".equals(itemid)&&"only_field".equals(fieldItem.getItemid())) {
						    isHave=true;
                            fi=fieldItem;
                        }
					}
					if (!isHave) {
						ArrayList<String> sb = this.msgMap.get(m);
                        if (sb == null) {
                            sb = new ArrayList<String>();
                        }
                        String msg =itemid+"列头无效，不是Q35中指标id" ;
                        sb.add("&nbsp;" + msg);
                        this.msgMap.put(m, sb);
						continue;
					}
					String value = "";
					String codesetid = fi.getCodesetid();
					int index = colsIndexMap.get(itemid);
					Cell cell = row.getCell(index);
					if (cell == null) {
						continue;
					}
					
					switch (cell.getCellTypeEnum()) {
					case FORMULA:
						value = cell.getStringCellValue().trim();
						break;
					case NUMERIC:
						double y = cell.getNumericCellValue();
						value = Double.toString(y);
						break;
					case STRING:
						value = cell.getStringCellValue();
						
						break;
					default:
						value = cell.getStringCellValue().trim();
					}
					if ("only_field_".equals(itemid)) {
						if (StringUtils.isEmpty(value)) {
							ArrayList<String> sb = this.msgMap.get(m);
							if (sb == null) {
								sb = new ArrayList<String>();
							}
							String msg = ResourceFactory.getProperty("kq.card.import.fieldEmpty");
							msg=msg.replace("{0}", m + "").replace("{1}", ResourceFactory.getProperty("sys.options.param.uniquenesstarget"));
							sb.add("&nbsp;" + msg);
							this.msgMap.put(m, sb);
							valueMap.put(itemid,null);
                            continue;
						}else {
							if (onlyFieldStr.indexOf(value)!=-1) {
								//重复数据
								ArrayList<String> sb = this.msgMap.get(m);
								if (sb == null) {
									sb = new ArrayList<String>();
								}
								String msg = ResourceFactory.getProperty("kq.card.duplicateData");;
								msg = msg.replace("{0}",m + "");
								sb.add("&nbsp;" + msg);
								this.msgMap.put(m, sb);
							}
							onlyFieldStr =onlyFieldStr+","+ value;
						}
					}
					if ("D".equals(fi.getItemtype())) {
						if (StringUtils.isEmpty(value)) {
							value=null;
							continue;
						}else {
						    String pattern="yyyy-MM-dd";
					        if(fi.getItemlength() == 4) {
					            pattern = "yyyy";
					        } else if(fi.getItemlength() == 7) {
					            pattern = "yyyy-MM";
					        } else if(fi.getItemlength() == 13) {
					            pattern = "yyyy-MM-dd HH";
					        } else if(fi.getItemlength() == 16) {
					            pattern = "yyyy-MM-dd HH:mm";
					        } else if(fi.getItemlength() >= 18) {
					            pattern = "yyyy-MM-dd HH:mm:ss";
					        }
						    if (!isValidDate(value,pattern)) {
                               ArrayList<String> sb = this.msgMap.get(m);
                               if (sb == null) {
                                  sb = new ArrayList<String>();
                               }
                               String msg = ResourceFactory.getProperty("kq.date.mx.dateformat.error");;
                               sb.add("&nbsp;第"+m+"行" + msg+pattern);
                               this.msgMap.put(m, sb);
                               valueMap.put(itemid,null);
                               continue;
                           }
						}
					}
    	            if(!("0".equalsIgnoreCase(codesetid))&&codesetid!=null&&!("".equalsIgnoreCase(codesetid))){
    	                if (StringUtils.isNotEmpty(value)) {
    	                        value=getCodeIdByDesc(value,codesetid);
    	                        if (value==null) {
    	                            ArrayList<String> sb = this.msgMap.get(m);
                                    if (sb == null) {
                                       sb = new ArrayList<String>();
                                    }
                                    String msg = ResourceFactory.getProperty("template_new.unFormat");;
                                    sb.add("&nbsp;第"+m+"行" +fi.getItemdesc()+ msg);
                                    this.msgMap.put(m, sb);
                                    valueMap.put(itemid,null);
                                    continue;
                                }
                        }
    	            }
					if ("N".equals(fi.getItemtype())) {
					    if ("confirm".equals(itemid)) {
	                         if (value.equals(ResourceFactory.getProperty("kq.date.mx.confirm1"))) {
	                             value="1";
	                         }else if(value.equals(ResourceFactory.getProperty("kq.date.mx.confirm2"))){
	                             value="2";
	                         }else{
	                             value="0";
	                         }
	                         valueMap.put(itemid,value);
	                         continue;
	                     }
			            if ((value + " ").split("\\.").length > 2) {
			            	ArrayList<String> sb = this.msgMap.get(m);
							if (sb == null) {
								sb = new ArrayList<String>();
							}
							String msg = "&nbsp;第" + m + "行[" + fi.getItemdesc() + "]"
			                        + ResourceFactory.getProperty("workbench.info.import.error.inttype") + value;
							sb.add("&nbsp;" + msg);
							this.msgMap.put(m, sb);
							valueMap.put(itemid,null);
                            continue;
			            } else {
			                Pattern p = Pattern.compile("[+-]?[\\d.]*");
			                Matcher m1 = p.matcher(value);
			                if (!m1.matches()) {
			                	ArrayList<String> sb = this.msgMap.get(m);
								if (sb == null) {
									sb = new ArrayList<String>();
								}
								String msg = "&nbsp;第" +m + "行[" + fi.getItemdesc() + "]"
				                        + ResourceFactory.getProperty("workbench.info.import.error.inttype") + value;
								sb.add("&nbsp;" + msg);
								this.msgMap.put(m, sb);
								valueMap.put(itemid,null);
                                continue;
			                } else {
			                    if ("N".equals(fi.getItemtype())) {
			                        int dw = fi.getDecimalwidth();
			                        if (dw == 0) {
			                            if (value.indexOf('.') != -1)
			                                value = value.substring(0, value.indexOf('.'));
			                        } else {
			                            int il = fi.getItemlength();
			                            int intValueLength = 0;
			                            if (value.indexOf('.') != -1) {
			                                value = PubFunc.round(value, dw);
			                            } else {
			                                intValueLength = value.length();
			                            }
			                            if (intValueLength > il) {
			                            	ArrayList<String> sb = this.msgMap.get(m);
			    							if (sb == null) {
			    								sb = new ArrayList<String>();
			    							}
			    							String msg = "&nbsp;第" +m + "行[" + fi.getItemdesc() + "]"
			    			                        + ResourceFactory.getProperty("kq.archive.scheme.itemLengthDiff");
			    							sb.add("&nbsp;" + msg);
			    							this.msgMap.put(m, sb);
			    							value = "";
			                            }
			                        }
			                        value = value.replaceAll("\\+", "");
			                        if (StringUtils.isBlank(value)) {
			                        	valueMap.put(itemid,null);
			                        	continue;
									}
			                    }
			                }
			            }
			        }
					 
					//校验身份证
					if(StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(chk)
							&& "1".equalsIgnoreCase(chkvalid) && "only_field".equalsIgnoreCase(itemid)&&"A0177".equalsIgnoreCase(chk)) {
						boolean flag = isValid(value);
						if(!flag) {
							ArrayList<String> sb = this.msgMap.get(m);
							if (sb == null) {
								sb = new ArrayList<String>();
							}
							String msg = "&nbsp;第" +m + "行["+fi.getItemdesc()+"]" + ResourceFactory.getProperty("workbench.info.import.error.idcard");
							sb.add("&nbsp;" + msg);
							this.msgMap.put(m, sb);
							valueMap.put(itemid,null);
                            continue;
						}
					}
					valueMap.put(itemid, value);
					continue;
				}
				valueList.add(valueMap);
			}
			KqDataList.add(valueList);
		} catch (Exception e) {
			errorMsg.setLength(0);
			errorMsg.append(e.toString());
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(stream);
			PubFunc.closeResource(rs);
			PubFunc.closeResource(wb);
			this.userView.getHm().put("errorMsg", errorMsg.toString());
		}
		return KqDataList;
	}
	/**
	 * 身份证验证
	 *
	 * @param id
	 *            号码内容
	 * @return 是否有效
	 */
    private boolean isValid(String id) {
		if (id == null)
			return false;

		int len = id.length();
		if (len != 15 && len != 18)
			return false;

		// 校验区位码
		if (!validCityCode(id.substring(0, 2)))
			return false;

		// 校验生日
		if (!validDate(id))
			return false;

		if (len == 15)
			return true;

		// 校验位数
		return validParityBit(id);

	}

	/**
	 * 18位身份证号校验
	 * 
	 * @param id
	 *            身份证号
	 * @return
	 */
	private boolean validParityBit(String id) {
		/**
		 * 效验码
		 */
		char[] PARITYBIT = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };

		/**
		 * 加权因子 Math.pow(2, i - 1) % 11
		 */
		int[] POWER = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		char[] cs = id.toUpperCase().toCharArray();
		int power = 0;
		for (int i = 0; i < cs.length; i++) {
			// 最后一位可以是X
			if (i == cs.length - 1 && cs[i] == 'X')
				break;

			// 非数字
			if (cs[i] < '0' || cs[i] > '9')
				return false;

			// 加权求和
			if (i < cs.length - 1) {
				power += (cs[i] - '0') * POWER[i];
			}
		}
		return PARITYBIT[power % 11] == cs[cs.length - 1];
	}

	/**
	 * 校验生日
	 * 
	 * @param id
	 *            身份证号
	 * @return
	 */
	private boolean validDate(String id) {
		try {
			String birth = id.length() == 15 ? "19" + id.substring(6, 12) : id.substring(6, 14);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date birthDate = sdf.parse(birth);
			if (!birth.equals(sdf.format(birthDate)))
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 校验区位码
	 * 
	 * @param cityCode
	 *            区位码
	 * @return
	 */
	private static boolean validCityCode(String cityCode) {
		/**
		 * <pre>
		 * 省、直辖市代码表：
		 *     11 : 北京  12 : 天津  13 : 河北   14 : 山西  15 : 内蒙古
		 *     21 : 辽宁  22 : 吉林  23 : 黑龙江 31 : 上海  32 : 江苏
		 *     33 : 浙江  34 : 安徽  35 : 福建   36 : 江西  37 : 山东
		 *     41 : 河南  42 : 湖北  43 : 湖南   44 : 广东  45 : 广西  46 : 海南
		 *     50 : 重庆  51 : 四川  52 : 贵州   53 : 云南  54 : 西藏
		 *     61 : 陕西  62 : 甘肃  63 : 青海   64 : 宁夏  65 : 新疆
		 *     71 : 台湾
		 *     81 : 香港  82 : 澳门
		 *     91 : 国外
		 * </pre>
		 */
		String[] CITY_CODE = { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37",
				"41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71",
				"81", "82", "91" };
		for (String code : CITY_CODE) {
			if (code.equals(cityCode))
				return true;
		}
		return false;
	}
	/**
	 * 获取模板数据的异常信息
	 */
	@Override
	public String getErrorMsg() {
		StringBuffer errorMsg = new StringBuffer();
		try {
			Iterator<Entry<Integer, ArrayList<String>>> it = this.msgMap.entrySet().iterator();
			int num=1;
			while (it.hasNext()) {
				if(errorMsg.length() < 1)
					errorMsg.append("[");
				
				Entry<Integer, ArrayList<String>> entry = it.next();
				ArrayList<String> msgList = entry.getValue();
				errorMsg.append("{id:'" + num + "',message:'");
				for (int i = 0; i < msgList.size(); i++) {
					String msg = msgList.get(i);
					if (i > 0)
						errorMsg.append("；");

					errorMsg.append(msg);
				}

				errorMsg.append("'},");
				num++;
			}

			if (errorMsg.toString().endsWith(","))
				errorMsg.setLength(errorMsg.length() - 1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		if(errorMsg.length() > 1)
			errorMsg.append("]");
		
		return errorMsg.toString();
	}
	@Override
	public void saveCardData(ArrayList<Object> kqDataList,String scheme_id,String kq_duration,String kq_year,String org_id) {
		ArrayList<HashMap<String, Object>> kqDataMap =  (ArrayList<HashMap<String, Object>>) kqDataList.get(1);
		ArrayList<String> keyList =(ArrayList<String>) kqDataList.get(0);
		ArrayList<ArrayList<Object>> updateParamList = new ArrayList<ArrayList<Object>>();
		ArrayList<ArrayList<Object>> insertParamList = new ArrayList<ArrayList<Object>>();
		for (HashMap<String, Object> map : kqDataMap) {
			String updateFlag=(String) map.get("updateFlag");
			//updateFlag更新or插入标识
			if ("1".equalsIgnoreCase(updateFlag)) {
				ArrayList<Object> updateValueList = new ArrayList<Object>();
				String guidkey="";
				for (int i = 0; i < keyList.size(); i++) {
					if ("a0101".equals(keyList.get(i))
							|| "only_field_".equals(keyList.get(i))
							|| "b0110".equals(keyList.get(i))
		                    || "e0122".equals(keyList.get(i))
		                    || "e01a1".equals(keyList.get(i))
							|| "id".equals(keyList.get(i))) {
						continue;
					}
					if ("guidkey".equals(keyList.get(i))) {
					    guidkey=(String) map.get(keyList.get(i));
						continue;
					}
					updateValueList.add(map.get(keyList.get(i)));
				}
				updateValueList.add(guidkey);
				updateValueList.add(scheme_id);
				updateValueList.add(kq_year);
				updateValueList.add(kq_duration);
				updateParamList.add(updateValueList);
			}else {
				ArrayList<Object> insertValueList = new ArrayList<Object>();
				String guidkey="";
				for (int i = 0; i < keyList.size(); i++) {
					if ("id".equals(keyList.get(i))
					        || "only_field_".equals(keyList.get(i))
					        || "b0110".equals(keyList.get(i))
		                    || "e0122".equals(keyList.get(i))
		                    || "e01a1".equals(keyList.get(i))
							|| "a0101".equals(keyList.get(i))) {
						continue;
					}
					if ("guidkey".equals(keyList.get(i))) {
					    guidkey=(String) map.get(keyList.get(i));
						continue;
					}
					insertValueList.add(map.get(keyList.get(i)));
				}
				insertValueList.add(map.get("a0101"));
				insertValueList.add(scheme_id);
				insertValueList.add(kq_year);
				insertValueList.add(kq_duration);
				insertValueList.add(guidkey);
				insertValueList.add(org_id);
				insertValueList.add(map.get("b0110"));
				insertValueList.add(map.get("e0122"));
				insertValueList.add(map.get("e01a1"));
				insertParamList.add(insertValueList);
			}
		}
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("update q35 set ");
		for (int i = 0; i < keyList.size(); i++) {
			if ("a0101".equals(keyList.get(i))
					|| "only_field_".equals(keyList.get(i))
					|| "guidkey".equals(keyList.get(i))
					|| "b0110".equals(keyList.get(i))
                    || "e0122".equals(keyList.get(i))
                    || "e01a1".equals(keyList.get(i))
					|| "id".equals(keyList.get(i))) {
				continue;
			}
			updateSql.append(keyList.get(i)+"=? ,");
		}
		updateSql.setLength(updateSql.length()-1);
		updateSql.append(" where  guidkey=? and org_id like '"+org_id+"%' and scheme_id=? and kq_year=? and kq_duration=? ");
		//插入数据sql
		StringBuffer insertSql = new StringBuffer();
		StringBuffer valuesSql =new StringBuffer();
		insertSql.append("insert into  q35 ( ");
		valuesSql.append(" values (");
		for (int i = 0; i < keyList.size(); i++) {
			if ("a0101".equals(keyList.get(i))
					|| "only_field_".equals(keyList.get(i))
					|| "b0110".equals(keyList.get(i))
					|| "e0122".equals(keyList.get(i))
					|| "guidkey".equals(keyList.get(i))
					|| "e01a1".equals(keyList.get(i))
					|| "id".equals(keyList.get(i))) {
				continue;
			}
			insertSql.append(keyList.get(i)+",");
			valuesSql.append("?,");
		}
		insertSql.append("a0101,scheme_id,kq_year,kq_duration,guidkey,org_id,b0110,e0122,e01a1)");
		valuesSql.append("?,?,?,?,?,?,?,?,?)");
		insertSql.append(valuesSql);
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			if (updateParamList != null && updateParamList.size() > 0) {
				dao.batchUpdate(updateSql.toString(), updateParamList);
			}

			if (insertParamList != null && insertParamList.size() > 0) {
				dao.batchInsert(insertSql.toString(), insertParamList);
			}
		} catch (SQLException e) {
			this.userView.getHm().put("errorMsg", "导入失败！");
			e.printStackTrace();
		}
	}
	/**
	 * 校验数据
	 * 
	 * @param kqDataList
	 *            模板中的数据
	 */
	private ArrayList<Object> checkCardData(ArrayList<Object> kqDataList,String scheme_id,String kq_duration,String kq_year,String org_id) {
		ArrayList<HashMap<String, Object>> kqDataMapList = new ArrayList<HashMap<String, Object>>();
		ArrayList<Object> kqDataListNew=new ArrayList<Object>();
		RowSet rs = null;
		try {
		    KqDataMxService service = new KqDataMxServiceImpl(this.userView,this.conn);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
            String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			ArrayList<String> dbNameList = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
			if (dbNameList == null || dbNameList.size() < 1) {
				throw new GeneralException("", ResourceFactory.getProperty("kq.card.setNbase"), "", "");
			}
			ArrayList<HashMap<String, Object>> valueList = (ArrayList<HashMap<String, Object>>) kqDataList.get(1);
			ArrayList<String> onlyFieldList = new ArrayList<String>();
			for (HashMap<String, Object> map : valueList) {
				String onlyField = (String) map.get("only_field_");
				if (!onlyFieldList.contains(onlyField)) {
					onlyFieldList.add(onlyField);
				}
			}

			String onlyFields = "";
			ArrayList<String> paramList = new ArrayList<String>();
			int sum = 0;
			for (String onlyField : onlyFieldList) {
				onlyField = PubFunc.keyWord_filter(onlyField);
				onlyFields += onlyField + "','";
				sum++;

				if (sum >= 1000) {
					onlyFields = onlyFields.substring(0, onlyFields.length() - 3);
					paramList.add(onlyFields);
					onlyFields = "";
					sum = 0;
				}

			}

			if (StringUtils.isNotEmpty(onlyFields)) {
				onlyFields = onlyFields.substring(0, onlyFields.length() - 3);
				paramList.add(onlyFields);
			}

			StringBuffer a01Sql = new StringBuffer();
			for (String nbase : dbNameList) {
				if (a01Sql.length() > 0) {
					a01Sql.append(" union all ");
				}
				
				a01Sql.append("select a0100,a0101,b0110,e0122,e01a1,"+chk+" from "
				        + nbase + "a01");
			}

			StringBuffer sql = new StringBuffer();
			sql.append("select  a0100,a0101,b0110,e0122,e01a1,"+chk+" from (");
			sql.append(a01Sql);
			sql.append(") A01 where " + chk);
			sql.append(" in ('##')");
			String where = getPrivWhere();
			sql.append(" and " + where);
			ContentDAO dao = new ContentDAO(this.conn);
			for (String chks : paramList) {
				rs = dao.search(sql.toString().replace("##", chks));
				while (rs.next()) {
					String onlyFieldStr = rs.getString(chk);
					for (int i = valueList.size(); i > 0; i--) {
						HashMap<String, Object> map = valueList.get(i - 1);
						String only_field = (String) map.get("only_field_");
						String a0101 = rs.getString("a0101");
						String b0110 = rs.getString("b0110");
						String e0122 = rs.getString("e0122");
						String e01a1 = rs.getString("e01a1");
						if (!onlyFieldStr.equalsIgnoreCase(only_field))
							continue;
						if (!a0101.equals(map.get("a0101"))) {
							Integer id =  (Integer) map.get("id");
							ArrayList<String> sb = this.msgMap.get(id);
							if (sb == null)
								sb = new ArrayList<String>();
							sb.add("&nbsp;第"+id+"行&nbsp;" + ResourceFactory.getProperty("kq.data.sp.nameError"));
							this.msgMap.put(id, sb);
						}else {
							map.put("a0101", a0101);
							map.put("b0110", b0110);
							map.put("e0122", e0122);
							map.put("e01a1", e01a1);
							kqDataMapList.add(map);
						}
						valueList.remove(map);
					}
				}
			}
			// 检验权限外的人员
			for (HashMap<String, Object> map : valueList) {
			    boolean isHave=false;
			    Map returnMap=service.getChangeStaffs(scheme_id, kq_year, kq_duration, org_id, 99, 1, "del", null);
			    List<LazyDynaBean> staffsList=(List<LazyDynaBean>)returnMap.get("staffs");
			    for (LazyDynaBean bean : staffsList) {
                    if (bean.get("guidkey").equals(map.get("guidkey"))) {
                        isHave=true;
                        kqDataMapList.add(map);
                        continue;
                    }
                }
			    if (isHave) {
			        continue;
                }
			    Integer id =  (Integer) map.get("id");
				String a0101 = (String) map.get("a0101");
				a0101 = StringUtils.isEmpty(a0101) ? "" : a0101.replace("'", "\\'");
				
				ArrayList<String> sb = this.msgMap.get(id);
				if (sb == null)
					sb = new ArrayList<String>();
				sb.add("&nbsp;第"+id+"行" + a0101 + ResourceFactory.getProperty("kq.data.sp.nopeople"));
				this.msgMap.put(id, sb);
			}
			
			sql.setLength(0);
			sql.append("select only_field,guidkey from q35");
			sql.append(" where Org_id like '"+org_id+"%' and scheme_id=? and kq_year=? and kq_duration=? and " + where);
			
			ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
			List params = new ArrayList();
			params.add(scheme_id);
			params.add(kq_year);
			params.add(kq_duration);
			rs = dao.search(sql.toString(),params);
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				String onlyField = rs.getString("only_field");
				String guidkey = rs.getString("guidkey");
				map.put("only_field", onlyField);
				map.put("guidkey", guidkey);
				dataList.add(map);
			}
			
			for (HashMap<String, Object> map : kqDataMapList) {
				String onlyField = (String) map.get("only_field_");
				String guidkey = (String) map.get("guidkey");
				for (HashMap<String, String> valueMap : dataList) {
					String dataOnlyField = valueMap.get("only_field");
					String dataGuidkey = valueMap.get("guidkey");
					if ((StringUtils.isNotBlank(dataOnlyField) && dataOnlyField.equalsIgnoreCase(onlyField))
					        ||(StringUtils.isNotBlank(dataGuidkey) && dataGuidkey.equalsIgnoreCase(guidkey)) ) {
						if ((StringUtils.isNotBlank(dataOnlyField) && !dataOnlyField.equalsIgnoreCase(onlyField))) {
					        Integer id =  (Integer) map.get("id");
			                String a0101 = (String) map.get("a0101");
			                a0101 = StringUtils.isEmpty(a0101) ? "" : a0101.replace("'", "\\'");
			                
			                ArrayList<String> sb = this.msgMap.get(id);
			                if (sb == null)
			                    sb = new ArrayList<String>();
			                sb.add("&nbsp;第"+id+"行" + a0101 + ResourceFactory.getProperty("kq.data.sp.nopeople"));
			                this.msgMap.put(id, sb);
                        }
						map.put("updateFlag", "1");
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.userView.getHm().put("errorMsg", e.toString());
		} finally {
			PubFunc.closeResource(rs);
		}
		kqDataListNew.add(kqDataList.get(0));
		kqDataListNew.add(kqDataMapList);
		return kqDataListNew;
	}
	/**
     * 获取用户的权限相关的sql条件
     * @return
	 * @throws GeneralException 
     */
    private String getPrivWhere() throws GeneralException {
        StringBuffer whereSql = new StringBuffer();
        try {
            String b0110Priv = KqPrivForHospitalUtil.getPrivB0110Whr(this.userView, "b0110",
                    KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
            String e0122Priv = KqPrivForHospitalUtil.getPrivB0110Whr(this.userView, "e0122",
                    KqPrivForHospitalUtil.LEVEL_SELF_CHILD);
            
            if(StringUtils.isNotEmpty(b0110Priv) && !"1=1".equals(b0110Priv)) {
                whereSql.append("(" + b0110Priv + ")");
            }
            
            if(StringUtils.isNotEmpty(e0122Priv) && !"1=1".equals(e0122Priv)) {
                if(StringUtils.isNotEmpty(whereSql.toString())) {
                    whereSql.append(" or ");
                }
                
                whereSql.append("(" + e0122Priv + ")");
            }
            
            if(StringUtils.isEmpty(whereSql.toString())) {
                whereSql.append("1=1");
            } 
        } catch (Exception e) {
            this.userView.getHm().put("errorMsg", e.toString());
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
        
        return "(" + whereSql.toString() + ")";
    }
    private static boolean isValidDate(String strValue,String pattern) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(strValue);
        } catch (ParseException e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
	}

	private static boolean isLeapYear(int y) {//判断是否为闰年
		return y % 4 == 0 && (y % 400 == 0 || y%100 != 0);
	}
	/**
	 * 下载导入数据模板
	 * doTypeExportExl
	 * @param scheme_id
	 * @param kq_duration
	 * @param kq_year
	 * @param org_id
	 * @param showMx
	 * @param type
	 * @param service
	 * @return
	 * @throws GeneralException
	 * @throws IOException
	 * @throws SQLException
	 */
	@Override
    public String doTypeExportExl(String scheme_id, String kq_duration, String kq_year, String org_id, String showMx, String type, KqDataMxService service)
			throws GeneralException {
        SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.conn,this.userView);
        String scheme_id_ = PubFunc.decrypt(scheme_id);//解密后的方案id
    	ArrayList parameterList = new ArrayList();
		parameterList.add(scheme_id_);
    	ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(" And scheme_id=? ", parameterList, "");
    	LazyDynaBean schemeBean = schemeList.get(0);
    	if(StringUtils.isNotBlank(org_id) && !org_id.contains(",")){
            String unName = AdminCode.getCodeName("UN",PubFunc.decrypt(org_id));
            String umName = AdminCode.getCodeName("UM",PubFunc.decrypt(org_id));
            org_name = StringUtils.isBlank(unName)?umName:unName;
        }
        // 45101 如果是多个方案导出则 获取整个方案的信息
        if(org_id.split(",").length > 1) {
            // 多个部门时不显示考勤员
            String b0100 = (String)schemeBean.get("b0110");
            if(StringUtils.isNotBlank(b0100))
                org_name = (AdminCode.getCodeName("UN", b0100).length()==0?AdminCode.getCodeName("UM", b0100):AdminCode.getCodeName("UN", b0100));
        }
    	// 员工确认考勤结果 1:生效
        String o = String.valueOf(schemeBean.get("confirm_flag"));
        Integer confirmFlag = "null".equalsIgnoreCase(o) || o.length()==0?0:Integer.parseInt(o);
		KqPrivForHospitalUtil param = new KqPrivForHospitalUtil(userView, this.conn);
		String gNo = param.getG_no();
		ArrayList<ColumnsInfo> columnList=listSimpleColumn(scheme_id_);
		//根据栏目设置排序列头
		ArrayList<ColumnsInfo> columnListSort=sortColumn(columnList, scheme_id_, showMx,confirmFlag);
		HashMap<String, LazyDynaBean> styleMap =service.getClassAndItems("", "0");
		ArrayList headList = listSimpleHead(columnListSort, scheme_id_, type);
		HashMap dropDownMap = new HashMap();
		//取代码型下拉列表
	    for (int i = 0; i < headList.size(); i++) {
	         LazyDynaBean codebean = (LazyDynaBean) headList.get(i);
	         String codesetid = (String) codebean.get("codesetid");
	         String itemid = (String) codebean.get("itemid");
	         if(!("0".equalsIgnoreCase(codesetid))&&codesetid!=null&&!("".equalsIgnoreCase(codesetid))){
	              ArrayList<String> desclist = getCodeByDesc(codesetid);
	              dropDownMap.put(itemid, desclist);
	         }
	         if ("confirm".equals(itemid)) {
	             ArrayList<String> desclist = new ArrayList<String>();
	             desclist.add(ResourceFactory.getProperty("kq.date.mx.confirm0"));
	             desclist.add(ResourceFactory.getProperty("kq.date.mx.confirm2"));
                 dropDownMap.put(itemid, desclist);
            }
	    }
	    
		String sortSql  = " order by a0000 asc ";
		// 根据栏目设置排序
		TableDataConfigCache configCache = (TableDataConfigCache)this.userView.getHm().get("kqdata_NoneMx_"+PubFunc.decrypt(scheme_id)+"_onlysave");
		if(configCache != null)	{
			sortSql  = (String)configCache.getSortSql();//取得oder by
		}
		//数据sql
		String sql = getSimpleSql(kq_year, kq_duration, org_id, scheme_id_, gNo, String.valueOf(schemeBean.get("cbase")));
		String sqlstr = "select row_number() over( "+sortSql+" )  as id, q.* from (" + sql.toString() + ") q "+sortSql;
		String fileName="";
		try {
			HashMap dataMap = listSimpleExcelList(sqlstr, columnListSort, showMx, type, styleMap);
			dataMap = getDealedExcelList(dataMap, styleMap);
			ArrayList<LazyDynaBean> dealedExcelList = (ArrayList<LazyDynaBean>) dataMap.get("newDataList");
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn);// 实例化导出Excel工具类
			excelUtil.setHeadRowHeight((short)400);
			fileName = org_name+kq_year+kq_duration 
					+ ResourceFactory.getProperty("kq.date.monthly.collect");
			fileName = this.userView.getUserName()+ "_"+fileName+".xls";
			//导出到excel
			excelUtil.setRowHeight((short)350);
			excelUtil.setConvertToZero(false);
			// 设置纸张参数
			excelUtil.setLandscape(true);
			excelUtil.exportExcel(fileName, kq_duration+ResourceFactory.getProperty("kq.duration.yue")+ResourceFactory.getProperty("kq.datemx.table"),
					null, headList, dealedExcelList, dropDownMap, 0);//导出表格
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
	/**
	 * 处理后的数据集合
	 * @param dataMap
	 * @param styleMap
	 * @return
	 */
	private HashMap getDealedExcelList(HashMap dataMap, HashMap<String, LazyDynaBean> styleMap) {
		@SuppressWarnings("unchecked")
		HashMap<Integer, Integer> rowMap = (HashMap<Integer, Integer>) dataMap.get("rowMap");
		@SuppressWarnings("unchecked")
		ArrayList<LazyDynaBean> dataList = (ArrayList<LazyDynaBean>) dataMap.get("dataList");
		ArrayList<LazyDynaBean> newDataList = new ArrayList<LazyDynaBean>();
		HashMap returnMap = new HashMap();
		int totalIndex = 0;
		HashMap mergedMap = new HashMap();
        String mxDetailType = "2";
		for(int i = 0;i<dataList.size();i++){
			int count = 0;
			if(rowMap.get(i)!=null)
				count = rowMap.get(i);
			LazyDynaBean bean = dataList.get(i);
			//根据该行数据个数，拆分该行
			for(int index = 0;index<count;index++){
				LazyDynaBean newBean = new LazyDynaBean();
				HashMap singelrowmergeMap = new HashMap();
				for (Object o :bean.getMap().keySet()) {
					String key =(String)o;
					key = key.toLowerCase();
					LazyDynaBean innerBean = (LazyDynaBean)bean.get(key);
					LazyDynaBean newInnerBean = new LazyDynaBean();
					String value = (String)innerBean.get("content");
					int fromColNum = (Integer)innerBean.get("fromColNum");
					String newValue = value;
					//日期列设置样式
					if(key.startsWith("q35")
							&& StringUtils.isNumericSpace(key.substring(3))
							&& Integer.parseInt(key.substring(3))>=1
							&& Integer.parseInt(key.substring(3))<=31
							&& StringUtils.isNotBlank(value)){
						String[] spval=value.split(","); 
						if(index==1)
							singelrowmergeMap.put(key, count);
							
						if(spval.length>index){
							newValue = spval[index];
						}else{
							newValue = "";
						}
						LazyDynaBean styleBean = styleMap.get(newValue);
						if(styleBean!=null){
							//根据不同的明细类型，显示不同的样式
							if(newValue.startsWith("C")){
                                String symbol = String.valueOf(styleBean.get("symbol"));
                                String abbreviation = String.valueOf(styleBean.get("abbreviation"));
                                String name = String.valueOf(styleBean.get("name"));
                                //字符+名称
							    if("0".equals(mxDetailType)){
                                    newValue = StringUtils.isNotBlank(symbol)?symbol+" ":"";
                                    newValue += StringUtils.isNotBlank(abbreviation)?abbreviation:name;
                                }else if("1".equals(mxDetailType)){
                                    newValue = StringUtils.isNotBlank(symbol)?symbol:(StringUtils.isNotBlank(abbreviation)?abbreviation:name);
                                }else{
                                    newValue = StringUtils.isNotBlank(abbreviation)?abbreviation:name;
                                }
							    // 输出日明细
                                
                                newValue = StringUtils.isEmpty(symbol)?abbreviation:symbol;
                                newValue = StringUtils.isEmpty(newValue)?name:newValue;
                               
								if(styleBean.get("color")!=null && String.valueOf(styleBean.get("color")).length()>0){
									String color = String.valueOf(styleBean.get("color"));
									HashMap colStyleMap = new HashMap();
									//避免白色的字体导出后看不见
									if("#FFFFFF".equals(color)){
										color = "#000000";
									}
									colStyleMap.put("fontColor",color.substring(1));
									colStyleMap.put("align","center");
									newInnerBean.set("singleCellStyle", colStyleMap);
								}
							}else if(newValue.startsWith("I")){
                                String symbol = styleBean.get("item_symbol")== null ?"":(String)styleBean.get("item_symbol");
                                String name = styleBean.get("item_name")== null ?"":(String)styleBean.get("item_name");
                                //字符+名称
                                if("0".equals(mxDetailType)){
                                    newValue = StringUtils.isNotBlank(symbol)?symbol+" "+name:name;
                                }else if("1".equals(mxDetailType)){
                                    newValue = StringUtils.isNotBlank(symbol)?symbol:name;
                                }else{
                                    newValue = name;
                                }
                                // 输出日明细
                                newValue = StringUtils.isEmpty(symbol)?name:symbol;
								if(styleBean.get("item_color")!=null && String.valueOf(styleBean.get("item_color")).length()>0){
									HashMap colStyleMap = new HashMap();
									String item_color = String.valueOf(styleBean.get("item_color"));
									//避免白色的字体导出后看不见
									if("#FFFFFF".equals(item_color)){
										item_color = "#000000";
									}
									colStyleMap.put("fontColor", item_color.substring(1));
									colStyleMap.put("align","center");
									newInnerBean.set("singleCellStyle", colStyleMap);
								}
							}
						}
					}
					
					newInnerBean.set("content", newValue);
					newInnerBean.set("fromColNum", fromColNum);
					newBean.set(key, newInnerBean);
				}	
				newDataList.add(newBean);
				if(singelrowmergeMap.size()>0){
					singelrowmergeMap.put("count", count);
					mergedMap.put(totalIndex, singelrowmergeMap);
				}
				totalIndex++;
			}
		}
		returnMap.put("mergedMap", mergedMap);//合并的map
		returnMap.put("newDataList", newDataList);//处理后的数据（多条明细的拆分为多行）
		return returnMap;
	}
	/**
	 * 获取输出日明细/月汇总列头集合
	 * listSimpleExcelList
	 * @param tableSql
	 * @param columnList
	 * @param showMx
	 * @param type
	 * @param styleMap
	 * @return
	 * @throws SQLException
	 */
	private HashMap listSimpleExcelList(String tableSql, ArrayList<ColumnsInfo> columnList, String showMx, String type
			, HashMap<String,LazyDynaBean> styleMap) throws SQLException {
		HashMap returnMap = new HashMap();
		RowSet rs = null;
		ArrayList<LazyDynaBean> dataList=new ArrayList<LazyDynaBean>();
		HashMap<Integer,Integer> rowMap = new HashMap<Integer, Integer>();
		ContentDAO dao = new ContentDAO(this.conn);
		rs=dao.search(tableSql);
		int index = 0;
		while(rs.next()){
			int colLen = 1;
			int colIndex = 0;
			LazyDynaBean bean = new LazyDynaBean(); 
			for(int i = 0 ;i<columnList.size();i++){
				ColumnsInfo col = columnList.get(i);
				//查询结果 bean 中的key为全小写字段名
				String data = "";
				String value = "";
				String item = col.getColumnId();
				if(item.toUpperCase().startsWith("Q35")
                        && StringUtils.isNumericSpace(item.substring(3))
                        && Integer.parseInt(item.substring(3))>=1
                        && Integer.parseInt(item.substring(3))<=31){
                    value = rs.getString(item);
                    //去掉无效班次和项目
                    value = deleteNotExitsScheme(value, styleMap);
                    if(value!=null&&value.indexOf(',')>-1){
                        int len = value.split(",").length;
                        if(colLen<len)
                            colLen = len;
                    }
                }else {
                	if("confirm".equals(item)){
    					String temp = rs.getString(item);
    					if(temp!=null) {
    						switch(rs.getInt(item)){
    							case 1:
    								value = ResourceFactory.getProperty("kq.date.mx.confirm1");
    								break;
    							case 2:
    								value = ResourceFactory.getProperty("kq.date.mx.confirm2");
    								break;
    							case 0:
    								value = ResourceFactory.getProperty("kq.date.mx.confirm0");
    								break;
    							    
    						}
    					}else {
    					    value = ResourceFactory.getProperty("kq.date.mx.confirm0");
    					}
    					data = KqDataUtil.nullif(value);
    		            bean = setDataColumn(item,data,bean,colIndex);
    		            colIndex++;
    					continue;
    				}
            		if("A".equals(col.getColumnType())){
	                    value = rs.getString(item);
			            if(!"0".equals(col.getCodesetId())){
			                if("UN".equals(col.getCodesetId())||"UM".equals(col.getCodesetId())||"@K".equals(col.getCodesetId())){
			                    if(!"".equals(AdminCode.getCodeName("UN",value)))
			                        value=AdminCode.getCodeName("UN",value);
			                    else if(!"".equals(AdminCode.getCodeName("UM",value)))
			                        value=AdminCode.getCodeName("UM",value);
			                    else if(!"".equals(AdminCode.getCodeName("@K",value)))
			                        value=AdminCode.getCodeName("@K",value);
			                }else if(!"".equals(AdminCode.getCodeName(col.getCodesetId(),value)))
			                    value=AdminCode.getCodeName(col.getCodesetId(),value);
			            }
	                }else if("D".equals(col.getColumnType())){
	                    Date date=rs.getTimestamp(item);
	                    if(date==null){
	                        value="";
	                    }else{
	                        String typef="yyyy-MM-dd HH:mm:ss";
	                        SimpleDateFormat sdf = new SimpleDateFormat(typef);
	                        value = sdf.format(date);
	                    }
	                }else{
	                    value=rs.getString(item)==null?"":rs.getString(item);
	                }
                }
				data = KqDataUtil.nullif(value);
	            bean = setDataColumn(item,data,bean,colIndex);
	            colIndex++;
			}
			rowMap.put(index, colLen);
			index++;
			dataList.add(bean);
		}
		returnMap.put("rowMap", rowMap);
		returnMap.put("dataList", dataList);
		return returnMap;
	}

	/**
	 * 获取单个数据列的表格项
	 * @param columuName
	 * @param data
	 * @param bean 
	 * @param i 
	 * @return LazyDynaBean
	 */
	private LazyDynaBean setDataColumn(String columuName, String data, LazyDynaBean bean, int i) {
		LazyDynaBean dataBean = new LazyDynaBean();
       	dataBean = new LazyDynaBean();  
       	dataBean.set("content", data);
       	dataBean.set("fromColNum", i);
       	bean.set(columuName, dataBean);
		return bean;
	}
	
	/**
	 * 输出日明细/月汇总 列头集合
	 * listSimpleHead
	 * @param columnList
	 * @param schemeId
	 * @param type
	 * @return
	 * @author xuanz
	 */
	private ArrayList listSimpleHead(ArrayList<ColumnsInfo> columnList, String schemeId, String type)throws GeneralException {
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		LazyDynaBean bean =null;
		int cellIndex = 0;
		for(int i = 0 ;i<columnList.size();i++){
			ColumnsInfo col = columnList.get(i);
			String columnid = col.getColumnId();
			int loadType = col.getLoadtype();
			bean = new LazyDynaBean();
			String colType=col.getColumnType();
			String content = col.getColumnDesc();
			bean.set("content", content);
			if(loadType!=ColumnsInfo.LOADTYPE_BLOCK )
				bean.set("columnHidden", true);
			bean.set("codesetid", col.getCodesetId());
			bean.set("colType", col.getColumnType());
			bean.set("comment", columnid);
			bean.set("decwidth", col.getDecimalWidth()+"");
			// 序号
			if("id".equalsIgnoreCase(columnid))
				bean.set("columnWidth", 1200);
			if("a0101".equalsIgnoreCase(columnid))
				bean.set("columnWidth",2200);
			// 月汇总指标
			if(!(",id,a0101,").contains(","+columnid.toLowerCase()+","))
				bean.set("columnWidth", 3200);
			if ("D".equals(colType)) {
			    bean.set("colType", "A");
			    bean.set("columnWidth", 5200);
            }
			bean.set("itemid", columnid);
			bean.set("fromRowNum", 0);
			bean.set("toRowNum", 0);
			bean.set("fromColNum", cellIndex);
			bean.set("toColNum", cellIndex);
			list.add(bean);
			cellIndex++;
		}
		return list;
	}
    /**
     * 去掉不在考勤方案中存在的班次或项目
     * value 选中班次项目
     *
     * @return String  去掉
     */
	private String deleteNotExitsScheme(String value,HashMap<String, LazyDynaBean> styleMap){
        if(StringUtils.isEmpty(value))
            return value;
        List<String> values = new ArrayList<String>(Arrays.asList(value.split(",")));
        Iterator<String> it = values.iterator();
        while(it.hasNext()){
            String  s = it.next();
            LazyDynaBean styleBean = styleMap.get(s);
            if(styleBean==null){
                it.remove();
            }
        }
        return StringUtils.join(values.toArray(),",");
    }
    /**
     * 输出日明细/月汇总 列集合
     * listSimpleColumn
     * @param schemeId
     * @return
     * @throws GeneralException
     * @author xuanz
     */
    private ArrayList<ColumnsInfo> listSimpleColumn(String schemeId) throws GeneralException{
        ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
        ColumnsInfo columnsInfo = null;
	    try {
            KqItemService kqItemService= new KqItemServiceImpl(this.userView,this.conn);
            //添加第一列序号列
            columnsInfo = getColumnsInfo("id", ResourceFactory.getProperty("label.serialnumber"), 50, "", "N", 0, 0);
            columns.add(columnsInfo);
            String sqlWhere=" and other_param is not null";
            ArrayList<LazyDynaBean> itemList= kqItemService.listKqItem(sqlWhere, null, "");
            KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
            String onlyFieldName =  kqDataUtil.getOnlyFieldName(this.conn);
            String itemStr="";
            for (LazyDynaBean lazyDynaBean : itemList) {
                String fieldSetId=(String) lazyDynaBean.get("other_param");
                String fielditemid=(String) lazyDynaBean.get("fielditemid");
                if (StringUtils.isNotEmpty(fieldSetId)) {
                    itemStr +=fielditemid+",";
                }
            }
            ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("Q35", 1);
            for(int i=0;i<fieldList.size();i++) {
                FieldItem fi = fieldList.get(i);
                String itemId = fi.getItemid();
                // 去除没有启用的指标
                if (!"1".equals(fi.getUseflag()))
                    continue;
                // 去除隐藏的指标
                if (!"1".equals(fi.getState()))
                    continue;
                if(StringUtils.isEmpty(itemId))
                    continue;
                if (itemStr.contains(itemId.toUpperCase())) {
                    continue;
                }
                if ("b0110".equalsIgnoreCase(itemId)|| "e0122".equalsIgnoreCase(itemId)|| "e01a1".equalsIgnoreCase(itemId)) {
                    continue;
                }
                if(itemId.toUpperCase().startsWith("Q35")
                        && StringUtils.isNumericSpace(itemId.substring(3))
                        && Integer.parseInt(itemId.substring(3))>=1
                        && Integer.parseInt(itemId.substring(3))<=31){
                        continue;
                }
                
                columnsInfo = getColumnsInfoByFi(fi, 2200);
                //唯一性指标值不从Q35表中取得，与Q35中指标区分开
                if ("only_field".equalsIgnoreCase(itemId)|| "only_field_".equalsIgnoreCase(itemId)) {
                    columnsInfo.setColumnId("only_field_");
                    columnsInfo.setColumnDesc(onlyFieldName);
                }
                if ("confirm".equals(itemId)) {
                    columnsInfo.setColumnType("A");
                }
                columns.add(columnsInfo);
            }
        }catch(Exception e){
            throw GeneralExceptionHandler.Handle(e);
        }
        return columns;
    }
	/**
	 * 输出日明细/月汇总 获取SQL
	 * getSimpleSql
	 * @param kq_year
	 * @param kq_duration
	 * @param orgId
	 * @param scheme_id
	 * @param gNo
	 * @param nbases
	 * @return
	 * @author xuanz
	 */
	public String getSimpleSql(String kq_year, String kq_duration, String orgId, String scheme_id
			, String gNo, String cbases) {
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
		//是否定义唯一性指标 0：没定义
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
		//唯一性指标值
        String onlyname = "0".equals(uniquenessvalid) ? "" : sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		
		String selectSql = "select  "+gNo+" g_no";
		if(StringUtils.isNotBlank(onlyname)) 
			selectSql += ","+onlyname+" only_field_";
		selectSql += ", q35.* from Q35 ";
		StringBuffer whereSql = new StringBuffer();
		whereSql.append(" where a01.guidkey=Q35.guidkey and kq_year='"+kq_year+"' and kq_duration='"+kq_duration+"' and scheme_id='"+scheme_id+"'");
		if(StringUtils.isNotEmpty(orgId)) {
			String[] orgArr = orgId.split(",");
			whereSql.append(" and (");
			for(int i=0;i<orgArr.length;i++) {
				String id = orgArr[i];
				if(StringUtils.isNotBlank(id)){
					id = PubFunc.decrypt(id);
				}
				whereSql.append(" Q35.Org_id like '"+id+"%' or");
			}
			whereSql.setLength(whereSql.length()-2);
			whereSql.append(")");
		}
		StringBuffer sql = new StringBuffer();
		ArrayList<String> dbNames =DataDictionary.getDbpreList();
		for(int i=0;i<dbNames.size();i++) {
			String dbName = (String)dbNames.get(i);
			if(i > 0)
				sql.append(" UNION ALL ");
			sql.append(selectSql + ","+dbName+"A01 a01 ");
			sql.append(whereSql.toString()); 
		}
		return sql.toString();
	}
	/**
	 * 获取列对象
	 * getColumnsInfo
	 * @param columnId
	 * @param columnDesc
	 * @param columnWidth
	 * @param codesetId
	 * @param columnType
	 * @param columnLength
	 * @param decimalWidth
	 * @return
	 * @author xuanz
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc,
			int columnWidth, String codesetId, String columnType,
			int columnLength, int decimalWidth) {

		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setColumnWidth(columnWidth);// 显示列宽
		columnsInfo.setCodesetId(codesetId);// 指标集
		columnsInfo.setColumnType(columnType);// 类型N|M|A|D
		columnsInfo.setColumnLength(columnLength);// 显示长度
		columnsInfo.setDecimalWidth(decimalWidth);// 小数位
		if("A".equals(columnType)||
			"M".equals(columnType)||
			"D".equals(columnType)) {
			columnsInfo.setTextAlign("left");
		}else if("N".equals(columnType)){
			columnsInfo.setTextAlign("right");
		}
		if ("D".equals(columnType)) {
		    String pattern = "yyyy-MM-dd";
            if(columnLength == 4) {
                pattern = "yyyy";
            } else if(columnLength == 7) {
                pattern = "yyyy-MM";
            } else if(columnLength == 13) {
                pattern = "yyyy-MM-dd HH";
            } else if(columnLength == 16) {
                pattern = "yyyy-MM-dd HH:mm";
            } else if(columnLength >= 18) {
                pattern = "yyyy-MM-dd HH:mm:ss";
            }
            columnsInfo.setColumnDesc(columnDesc+pattern);
        }
		return columnsInfo;
	}
	/**
	 * 通过FieldItem获取列对象
	 * getColumnsInfoByFi
	 * @param fi
	 * @param columnWidth
	 * @return
	 * @author xuanz
	 */
	private ColumnsInfo getColumnsInfoByFi(FieldItem fi, int columnWidth){
		ColumnsInfo co = new ColumnsInfo();
		String itemid = fi.getItemid();
		String itemdesc = fi.getItemdesc();
		String codesetId = fi.getCodesetid();
		String columnType = fi.getItemtype();
		// 指标长度，非显示长度
		int columnLength = fi.getItemlength();
		// 小数位
		int decimalWidth = fi.getDecimalwidth();
		co = getColumnsInfo(itemid, itemdesc, columnWidth, codesetId,
				columnType, columnLength, decimalWidth);

		return co;
	}
	
	/**
	 * 按照栏目设置排序列头
	 * @param columnsInfos
	 * @param schemeId
	 * @return ArrayList<ColumnsInfo>
	 * @throws GeneralException
	 * @author xuanz
	 */
	private ArrayList<ColumnsInfo> sortColumn(ArrayList<ColumnsInfo> columnsInfos,String schemeId,String showMx,Integer confirmFlag) throws GeneralException{
		ArrayList<ColumnsInfo> sortColumnList=new ArrayList<ColumnsInfo>();
		ColumnConfig columnConfig = null;
		String subModuleId = "kqdata_"+("true".equals(showMx)?"Mx_":"NoneMx_")+schemeId+"_onlysave";
		TableFactoryBO tableFactoryBO= new TableFactoryBO(subModuleId, this.userView,this.conn);
		HashMap layoutConfig = tableFactoryBO.getTableLayoutConfig();
		ColumnsInfo columnsInfo=new ColumnsInfo();
		columnsInfo.setColumnId("guidkey");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
		columnsInfo.setColumnDesc("guidkey");
		columnsInfo.setCodesetId("0");
		columnsInfo.setColumnType("A");
		//有栏目设置则读取栏目设置的配置
        if(layoutConfig!=null){
        	ArrayList columnsConfigs = tableFactoryBO.getTableColumnConfig((Integer)layoutConfig.get("schemeId"));
			if ("序号".equals(columnsInfos.get(0).getColumnDesc())) {
				sortColumnList.add(columnsInfos.get(0));
			}
			for (ColumnsInfo columnsInfo2 : columnsInfos) {
			    if ("only_field_".equals(columnsInfo2.getColumnId())) {
			        sortColumnList.add(columnsInfo2);
                    break;
                }
            }
			for(int i=0;i<columnsConfigs.size();i++) {
				columnConfig = (ColumnConfig)columnsConfigs.get(i);
				for (ColumnsInfo columnsInfo2 : columnsInfos) {
					if ((columnsInfo2.getColumnId().equalsIgnoreCase(columnConfig.getItemid())||
					        columnConfig.getItemid().equalsIgnoreCase(columnsInfo2.getColumnId()+"_"))
					        && "1".equals(columnConfig.getIs_display())) {
					    if (("confirm".equals(columnsInfo2.getColumnId()))&&confirmFlag==0) {
                            continue;
                        }
						sortColumnList.add(columnsInfo2);
						break;
					}
				}
			}
		}else {
		    for (ColumnsInfo columnsInfo1 : columnsInfos) {
		        if ("confirm".equals(columnsInfo1.getColumnId())&&confirmFlag==0) {
		            continue;
	            }
		        sortColumnList.add(columnsInfo1);
            }
		}
        sortColumnList.add(columnsInfo);
        return sortColumnList;
	}
	/**
     * 获取代码型 数据  下拉列表数据集合
     * 
     * @param fieldCodeSetId 
     * @return desclist 下拉列表数据集合
     */
    public ArrayList<String> getCodeByDesc(String fieldCodeSetId){
    	String tableName = "";
    	if("UN".equalsIgnoreCase(fieldCodeSetId) 
    			|| "UM".equalsIgnoreCase(fieldCodeSetId)
    			||"@K".equalsIgnoreCase(fieldCodeSetId))
    		tableName = "organization";
    	else
    		tableName = "codeitem";
    	
    	StringBuffer sql = new StringBuffer("");
    	sql.append("select codeitemdesc ");
    	sql.append(" from ").append(tableName);
    	sql.append(" where codesetid='").append(fieldCodeSetId).append("' ");
    	sql.append(" and ").append(Sql_switcher.isnull("invalid", "1")).append("='1'");
    	
        RowSet rs = null;
        ArrayList<String> desclist = new ArrayList<String>();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            rs=dao.search(sql.toString());
            while(rs.next()){
                String codeitemdesc=rs.getString("codeitemdesc");
                desclist.add(codeitemdesc);
            }
            return desclist;
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
        	PubFunc.closeDbObj(rs);
        }
        return null;
    }
    /**
     * 获取代码型 数据  codeitemid
     * 
     * @param codeDesc 代码型描述 
     * @param fieldCodeSetId 代码型codesetid
     * @return codeitemid 数据  codeitemid
     */
    private String getCodeIdByDesc(String codeDesc, String fieldCodeSetId){
        String tableName = "";
        //组织机构类型的代码应该查organization表 haosl 2017-08-02 add
        if("UN".equalsIgnoreCase(fieldCodeSetId) 
                || "UM".equalsIgnoreCase(fieldCodeSetId)
                ||"@K".equalsIgnoreCase(fieldCodeSetId))
            tableName = "organization";
        else
            tableName = "codeitem";
        String  sql="select codeitemid from "+tableName+" where codeitemdesc='"+codeDesc+"' and codesetid='"+fieldCodeSetId+"'";
        RowSet rs = null;
        String msg = "";
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            rs=dao.search(sql);
            if(rs.next()){
                String codeitemid=rs.getString("codeitemid");
                return codeitemid;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return msg;
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return null;
    }
}
