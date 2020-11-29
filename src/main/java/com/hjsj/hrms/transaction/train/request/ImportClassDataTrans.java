package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.StationPosView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将要导入的数据进行分析，是否进行提示报告。读入内存。
 */
public class ImportClassDataTrans extends IBusiness {

	private String fieldsetid = "r31";
	String tmpstr = "";
	String b0110s = "";
	public void execute() throws GeneralException {
		HashMap msgMap = new HashMap();
		ArrayList msglist = new ArrayList();
		try {
			this.initPriv();
			tmpstr = getPiv();
			ArrayList selectitemidlist = new ArrayList();

			FormFile file = (FormFile) this.getFormHM().get("file");
			if(!FileTypeUtil.isFileTypeEqual(file))
			    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.fileuploaderror"))); 
			    
			ArrayList mapsList = readExcel(file, msgMap, selectitemidlist);

			this.getFormHM().put("mapsList", mapsList);
			StringBuffer R31primarykeys = new StringBuffer();// 存放Excle中对应的唯一标志
			String primarykeyLabel = "";
			for (int num = 0; num < mapsList.size(); num++) {
				Object[] maps = (Object[]) mapsList.get(num);

				HashMap fieldMap = (HashMap) maps[0];
				ArrayList valueList = (ArrayList) maps[1];
				String primarykeys = ((StringBuffer) maps[2]).toString();
				ArrayList keyList = (ArrayList) maps[3];
				fieldsetid = ((StringBuffer) maps[4]).toString().toLowerCase();
				if ("r31".equalsIgnoreCase(fieldsetid)) {
					R31primarykeys = (StringBuffer) maps[2];
				}
				String sql = "";
				if (keyList.size() < 1) {
					return;
				}
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String primarykey = (String) keyList.get(0);

				FieldItem fi = (FieldItem) fieldMap.get(primarykey);

				if (fi != null) {
					primarykeyLabel = fi.getItemdesc();
				}
				String b0110 = (String) this.getFormHM().get("bb0110");
				b0110 = b0110 != null ? b0110 : "";
				String e0122 = (String) this.getFormHM().get("be0122");
				e0122 = e0122 != null ? e0122 : "";

				// 过滤单位部门岗位是否正确
				this.doOrgCheck(valueList, dao);
				this.doUNUMKCheck(valueList, fieldMap, dao);
				// 检查代码型的字段从excel中读取的codeitemdesc值是否在库中有对应的codeitemid
				for (int n = 0; n < keyList.size(); n++) {
					String key = (String) keyList.get(n);
					FieldItem item = (FieldItem) fieldMap.get(key);
					if (item == null)
						continue;
					
					String itemid = item.getItemid();
					if("r3125".equalsIgnoreCase(itemid)){
						String codesetid = "r25";
						for (int m = 0; m < valueList.size(); m++) {
							HashMap valuemap = (HashMap) valueList.get(m);
							String primarykeyvalue = (String) valuemap.get(primarykey);
							String value = (String) valuemap.get(itemid);
							if(value==null||value.length()<1)
								continue;
							
							//接收返回字符串
							String returnValue = Checkcodeflag(value, codesetid);
							//拆分字符串
							String[] str = returnValue.split(",");
							String fg = "";
							if(str.length == 2)
							{
								value=null;
								fg=str[0];
								value=str[1];
							}
							else{
								fg=str[0];
							}
							
							if (!"true".equals(fg)) {
								valuemap.put(itemid, "");
								ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
								//提示有多条记录时显示错误
								if("manyRow".equals(fg))
								{									
									if (sb != null) {
										int no = sb.size();
										sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc()+ "]"+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
												+ value+ ResourceFactory.getProperty("train.info.import.error.manyRow"));
									} else {
										sb = new ArrayList();
										sb.add("1.&nbsp;[" + item.getItemdesc()+ "]"+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
												+ value+ ResourceFactory.getProperty("train.info.import.error.manyRow"));
									}
								}else{//未找到数据时显示错误
									if (sb != null) {
										int no = sb.size();
										sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc()+ "]"+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
												+ value+ ResourceFactory.getProperty("train.info.import.error.codenor"));
									} else {
										sb = new ArrayList();
										sb.add("1.&nbsp;[" + item.getItemdesc()+ "]"+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
												+ value+ ResourceFactory.getProperty("train.info.import.error.codenor"));
									}
								}
								msgMap.put(primarykeyvalue, sb);
							} else {
								if (!"".equals(value)) {
									valuemap.put(itemid, value);
								}
							}
						}
					}
					if ((item.getCodesetid() != null && !"".equals(item.getCodesetid()) && !"0".equals(item.getCodesetid()) )
							&& "A".equalsIgnoreCase(item.getItemtype())) {
						if(isCodeflag(fieldsetid,item.getItemid())){
							if ("UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid())
									|| "@K".equalsIgnoreCase(item.getCodesetid())) {
								sql = "select codeitemdesc,codeitemid from organization where upper(codesetid)='" + item.getCodesetid().toUpperCase() + "' and "
										+ Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date " + this.tmpstr;
								if("UN".equalsIgnoreCase(item.getCodesetid()) && this.b0110s != null && this.b0110s.trim().length() > 0)
								    sql+=" or codeitemid in (" + this.b0110s + ")";
								    
							} else {
								sql = "select codeitemdesc,codeitemid from codeitem where upper(codesetid)='"
										+ item.getCodesetid().toUpperCase() + "'";
							}
							this.frowset = dao.search(sql);
							HashMap codemap = new HashMap();
							while (this.frowset.next()) {
								if ("UN".equalsIgnoreCase(item.getCodesetid()) || "UM".equalsIgnoreCase(item.getCodesetid())
										|| "@K".equalsIgnoreCase(item.getCodesetid()))
									codemap.put(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
								else
									codemap.put(this.frowset.getString("codeitemdesc"), this.frowset.getString("codeitemid"));
							}
							
							HashMap leafItemMmaps= new HashMap();
		                    DbWizard db = new DbWizard(this.frameconn);
	                        if(db.isExistField("codeset", "leaf_node", false)) {
	                            this.frowset = dao.search("select leaf_node from codeset where upper(codesetid)='"+item.getCodesetid().toUpperCase()+"'");
	                            if(this.frowset.next()) {
	                                String leafNode = this.frowset.getString("leaf_node");
	                                if("1".equals(leafNode)) {
	                                    this.frowset=dao.search(sql + " and  codeitemid=childid");
	                                    while(this.frowset.next()){
	                                        leafItemMmaps.put(this.frowset.getString("codeitemid").trim(),this.frowset.getString("codeitemdesc").trim());
	                                    }
	                                }
	                            }
	                        }
							
							for (int m = 0; m < valueList.size(); m++) {
								HashMap valuemap = (HashMap) valueList.get(m);
								String primarykeyvalue = (String) valuemap.get(primarykey);
								String value = (String) valuemap.get(itemid);
	
								if ("b0110".equalsIgnoreCase(item.getItemid())) {
									if (value == null || "".equals(value)) {
										value = b0110;
									}
								}
								if ("e0122".equalsIgnoreCase(item.getItemid())) {
									if (value == null || "".equals(value)) {
										value = e0122;
									}
								}
								
								if (value == null)
									continue;
								if ("e0122".equalsIgnoreCase(item.getItemid()) || "b0110".equalsIgnoreCase(item.getItemid())
										|| "e01a1".equalsIgnoreCase(item.getItemid()) || "UN".equalsIgnoreCase(item.getCodesetid())
										|| "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())) {
									Pattern p = Pattern.compile("[A-Z0-9]*");
									Matcher ma = p.matcher(value);// 支持直接输入机构编码，可解决机构名重复的问题
									if ((!codemap.containsKey(value) && !"".equals(value)) || !ma.matches()) {
										if("b0110".equalsIgnoreCase(item.getItemid()))
											valuemap.put(itemid, "");
										else
											valuemap.put(itemid, "```");
										ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
										if (sb != null) {
											int no = sb.size();
											sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.codenol")
															+ value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
										} else {
											sb = new ArrayList();
											sb.add("1.&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("workbench.info.import.error.codenol")
															+ value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
										}
										msgMap.put(primarykeyvalue, sb);
									} else {
										if (!"".equals(value) && !ma.matches()) {
											valuemap.put(itemid, codemap.get(value));
										}
									}
								} else {
									if (!codemap.containsKey(value) && !"".equals(value)) {
										valuemap.put(itemid, "```");
										ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
										if (sb != null) {
											int no = sb.size();
											sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
															+ value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
										} else {
											sb = new ArrayList();
											sb.add("1.&nbsp;[" + item.getItemdesc() + "]"+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
															+ value + ResourceFactory.getProperty("workbench.info.import.error.codenor"));
										}
										msgMap.put(primarykeyvalue, sb);
									} else {
									    if(!"".equals(value)){
	                                        String codeitemid = (String) codemap.get(value);
	                                        if(!leafItemMmaps.isEmpty() && !leafItemMmaps.containsKey(codeitemid)) {
	                                            valuemap.put(itemid, "```");
	                                            ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
	                                            StringBuffer msg = new StringBuffer();
	                                            String selectEndCode = ResourceFactory.getProperty("train.b_plan.select.endcode").replace("{0}", item.getItemdesc()).replace("{1}", value);
	                                            msg.append(selectEndCode);
	                                            if(sb!=null){
	                                                int no=sb.size();
	                                                sb.add((no+1) + msg.toString());
	                                            }else{
	                                                sb= new ArrayList();
	                                                sb.add("1" + msg.toString());
	                                            }
	                                            msgMap.put(primarykeyvalue, sb);
	                                        } else
	                                            valuemap.put(itemid, codeitemid);
	                                    }
									}
								}
							}
						} else {
							String codesetid = item.getCodesetid();
							for (int m = 0; m < valueList.size(); m++) {
								HashMap valuemap = (HashMap) valueList.get(m);
								String primarykeyvalue = (String) valuemap.get(primarykey);
								String value = (String) valuemap.get(itemid);
								if(value==null||value.length()<1)
									continue;
								//接收返回字符串
								String returnValue=Checkcodeflag(value, codesetid);
								//拆分字符串
								String[] str=returnValue.split(",");
								String fg="";
								if(str.length==2)
								{
									fg=str[0];
									value=str[1];
								}
								else{
									fg=str[0];
								}
								if (!"true".equals(fg)) {
									valuemap.put(itemid, "");
									ArrayList sb = (ArrayList) msgMap.get(primarykeyvalue);
									//提示有多条记录时显示错误
									if("manyRow".equals(fg))
									{										
										if (sb != null) {
											int no = sb.size();
											sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc()+ "]"+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
													+ value+ ResourceFactory.getProperty("train.info.import.error.manyRow"));
										} else {
											sb = new ArrayList();
											sb.add("1.&nbsp;[" + item.getItemdesc()+ "]"+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
													+ value+ ResourceFactory.getProperty("train.info.import.error.manyRow"));
										}
									}else{//未找到相关记录时显示错误
										if (sb != null) {
											int no = sb.size();
											sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc()+ "]"+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
													+ value+ ResourceFactory.getProperty("train.info.import.error.codenor"));
										} else {
											sb = new ArrayList();
											sb.add("1.&nbsp;[" + item.getItemdesc()+ "]"+ ResourceFactory.getProperty("workbench.info.import.error.codenol")
													+ value+ ResourceFactory.getProperty("train.info.import.error.codenor"));
										}
									}
									msgMap.put(primarykeyvalue, sb);
								} else {
									if (!"".equals(value)) {
										valuemap.put(itemid, value);
									}
								}
							}
						}
					} else if (item != null && item.isSequenceable() && "A".equalsIgnoreCase(item.getItemtype())) {
						for (int m = 0; m < valueList.size(); m++) {
							HashMap valuemap = (HashMap) valueList.get(m);
							String value = (String) valuemap.get(itemid);
							if ((value == null || value.length() == 0)) {
								IDGenerator idg = new IDGenerator(2, this.frameconn);
								String idd = idg.getId(item.getSequencename());
								value = idd;
								valuemap.put(itemid, value);
							}
						}
					}
				}
				
				ArrayList prList = new ArrayList();
				String ttt[] = primarykeys.split("','");
				if (ttt.length > 500) {
					StringBuffer sb = new StringBuffer();
					int n = 0;
					boolean f = false;
					for (int i = 0; i < ttt.length; i++) {
						if ("".equals(ttt[i])) {
							continue;
						}
						f = false;
						sb.append(ttt[i] + "','");
						if (n > 498) {
							f = true;
							prList.add(sb.toString());
							sb = new StringBuffer();
							n = 0;
						}
						n++;
					}
					if (!f)
						prList.add(sb.toString());
				} else {
					prList.add(primarykeys);
				}

			}
			for (Iterator i = msgMap.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				LazyDynaBean ldb = new LazyDynaBean();
				ldb.set("keyid", key);
				ArrayList sb = (ArrayList) msgMap.get(key);
				StringBuffer sbb = new StringBuffer();
				for (int n = 0; n < sb.size(); n++) {
					sbb.append("&nbsp;" + (String) sb.get(n) + "</br>");
				}
				ldb.set("content", sbb.toString());
				msglist.add(ldb);
			}

			this.getFormHM().put("R31primarykeys", R31primarykeys);

			this.getFormHM().put("fieldsetid", fieldsetid);
			this.getFormHM().put("primarykeyLabel", primarykeyLabel);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("msglist", msglist);
			this.getFormHM().put("flag", "");
		}
	}

	/**
	 * 读取excel数据放入集合对象
	 * 
	 * @param file
	 * @param hashMap
	 * @return
	 * @throws Exception
	 */
	private ArrayList readExcel(FormFile file, HashMap msgMap, ArrayList selectitemidlist) throws Exception {

		ArrayList mapsList = new ArrayList();
		Workbook owb = null;
		Sheet osheet = null;
		HashMap hm = new HashMap();
		hm = this.getClassName();//获取系统中的培训班名称
		HashMap nhm = new HashMap();//获取模板中的培训班名称
		//hm与nhm中的元素为  培训班名称=同名培训班的个数
		InputStream in = null;
		try {
			in = file.getInputStream();
			owb = WorkbookFactory.create(in);
			if(owb.getNumberOfSheets()<2)//非下载的模板
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
			if(owb.getNumberOfSheets()==2){//模板中只有一个表的指标时，模板中有个隐藏的sheet（hjehr_codeset_*）；所以读取的sheet数量最少为2
				osheet = owb.getSheetAt(0);
				Object[] maps = readSheet(osheet, msgMap, hm, nhm, owb.getNumberOfSheets());
				mapsList.add(maps);
			} else {//模板中有多表数据时强制首先读取培训班的数据
				int i = 0;
				for (int owbIndex = 0; owbIndex < owb.getNumberOfSheets(); owbIndex++) {
					osheet = owb.getSheetAt(owbIndex);
					if (osheet.getSheetName().indexOf("R31") != -1){
						i = owbIndex;
						break;
					}
				}
				osheet = owb.getSheetAt(i);
				Object[] maps = readSheet(osheet, msgMap, hm, nhm, owb.getNumberOfSheets());
				mapsList.add(maps);
				for (int owbIndex = 0; owbIndex < owb.getNumberOfSheets(); owbIndex++) {//读取非培训班的sheet的数据
					if(owbIndex == i)
						continue;
					osheet = owb.getSheetAt(owbIndex);
					String name =  osheet.getSheetName();
					if(name.indexOf("hjehr_codeset_")!=-1)
						continue;
					Object[] mapss = readSheet(osheet, msgMap, hm, nhm, owb.getNumberOfSheets());
					mapsList.add(mapss);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
		} finally {
        	PubFunc.closeIoResource(in);
        	PubFunc.closeResource(owb);
		}
		return mapsList;
	}
	/**
	 * 读取sheet数据		
	 * @param osheet
	 * @param msgMap
	 * @return
	 * @throws Exception
	 */
	private Object[] readSheet(Sheet osheet, HashMap msgMap, HashMap hm, HashMap nhm, int k) throws Exception {
		
		ArrayList valueList = new ArrayList();
		HashMap fieldMap = new HashMap();// key=//当前指标值：所在cell列数 value=FieldItem
		HashMap fieldIdex = new HashMap();
		StringBuffer keysb = new StringBuffer();
		ArrayList keyList = new ArrayList();
		StringBuffer onefieldsetid = new StringBuffer();
		HashMap errorkey = new HashMap();
		Object[] maps = { fieldMap, valueList, keysb, keyList, onefieldsetid, errorkey };

		String sheetName = osheet.getSheetName();
		if (sheetName != null && sheetName.startsWith("hjehr_codeset_")) {
			return maps;
		}
		Row orow = osheet.getRow(0);// 第一行标题
		if (orow == null) {
			return maps;
		}
		int cols = orow.getPhysicalNumberOfCells();// 总列数
		int rows = osheet.getPhysicalNumberOfRows();// 总行数
		boolean isR31 = false;// 是否是培训班信息表
		int b = 0;
		for (int c = 0; c < cols; c++) {// 遍历列
			String itemid = "";
			Cell cell = orow.getCell(c);
			if (cell != null) {
				itemid = cell.getCellComment().getString().getString().toLowerCase();
			}
			String t[] = itemid.split("`");
			if (!isR31 && t.length > 1 && "r31".equalsIgnoreCase(t[1].toLowerCase())) {
				isR31 = true;
			}
			itemid = t[0];
			if ("".equals(itemid))
				break;
			if ("r3130".equalsIgnoreCase(itemid) && t.length == 1 && !isR31) {
				b = 1;
				continue;
			}

			if (c == b) {
				if (t.length == 2) {
					fieldsetid = t[1].toLowerCase();//验证是否为正确模板
					if ((!"r31".equalsIgnoreCase(fieldsetid)) && (!"r41".equalsIgnoreCase(fieldsetid))) {
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
					}
					onefieldsetid.append(fieldsetid);
					FieldSet fs = DataDictionary.getFieldSetVo(fieldsetid);
					fs.getCustomdesc();
				} else {
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
				}
			}
			FieldItem item = new FieldItem();
			if ("r3130".equalsIgnoreCase(itemid))
				item = DataDictionary.getFieldItem(itemid, "r31");
			else
				item = DataDictionary.getFieldItem(itemid, fieldsetid);
			if (item == null) {
				continue;
			}
			fieldIdex.put(itemid, new Integer(c));// 指标映射的列数
			fieldMap.put(itemid, item);// 指标编码映射的指标对象
			keyList.add(itemid);
		}
		cols = keyList.size();
		String value = "";
		double dvalue = 0;
		int sun = 0;
		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		for (int j = 1; j < rows; j++) {
			orow = osheet.getRow(j);
			if (orow != null) {//判断培训班名称及培训课程信息
				HashMap valueMap = new HashMap();// key=指标itemid value=值
				String primarykeyValue = "";
				Cell cell = orow.getCell(0);
				primarykeyValue = cell.getStringCellValue().trim();
				primarykeyValue = primarykeyValue.replaceAll("['|‘|’]", "");// update  去掉单引号   影响sql
				if (primarykeyValue == null || "".equals(primarykeyValue)) {// 主键为空就不提取此行记录
					continue;
				}
				//判断举办单位是否为空
				if ("r31".equalsIgnoreCase(fieldsetid)){
					Cell cells = orow.getCell(1);
					String b0110Value = cells.getStringCellValue().trim();
					b0110Value = b0110Value.replaceAll("['|‘|’]", "");
					if("".equals(b0110Value)||b0110Value=="")
					{
						checkIsFillable(primarykeyValue, DataDictionary.getFieldItem("b0110", "r31"), msgMap);
						continue;
					}
				}
				
				if ("r41".equalsIgnoreCase(fieldsetid) && nhm.size()>0 && !nhm.containsKey(primarykeyValue) && k>2) {
					if (!hm.containsKey(primarykeyValue)) {
						ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);//验证培训课程的培训班是否存在
						if (sb != null) {
							int no = sb.size();
							sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
									+ ResourceFactory.getProperty("train.info.import.error.notexist"));
						} else {
							sb = new ArrayList();
							sb.add("1.&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
									+ ResourceFactory.getProperty("train.info.import.error.notexist"));
						}
						msgMap.put(primarykeyValue, sb);
						primarykeyValue = null;
						continue;
					} else {
						int i = Integer.parseInt((String) hm.get(primarykeyValue));
						if (i > 1) {
							ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
							if (sb != null) {
								int no = sb.size();
								sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
										+ ResourceFactory.getProperty("train.info.import.error.nClass"));
							} else {
								sb = new ArrayList();
								sb.add("1.&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
										+ ResourceFactory.getProperty("train.info.import.error.nClass"));
							}
							msgMap.put(primarykeyValue, sb);
							primarykeyValue = null;
							continue;
						} else {
							ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
							if (sb != null) {
								int no = sb.size();
								sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
										+ ResourceFactory.getProperty("train.info.import.error.oClass"));
							} else {
								sb = new ArrayList();
								sb.add("1.&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
										+ ResourceFactory.getProperty("train.info.import.error.oClass"));
							}
							msgMap.put(primarykeyValue, sb);
						}
					}
				}else if("r41".equalsIgnoreCase(fieldsetid) && nhm.size()>0 && nhm.containsKey(primarykeyValue) && k>2){//培训课程
					int i = Integer.parseInt((String) nhm.get(primarykeyValue));
					if (i > 1) {
						ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);//验证导入的模板中是否有重名的培训班
						if (sb != null) {
							int no = sb.size();
							sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
									+ ResourceFactory.getProperty("train.info.import.error.newmClass"));
						} else {
							sb = new ArrayList();
							sb.add("1.&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
									+ ResourceFactory.getProperty("train.info.import.error.newmClass"));
						}
						msgMap.put(primarykeyValue, sb);
						primarykeyValue = null;
						continue;
					}
				} else if("r41".equalsIgnoreCase(fieldsetid) && nhm.size()<1 && k<3){//培训课程
					if(!hm.containsKey(primarykeyValue)){
						ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
						if (sb != null) {
							int no = sb.size();//判断培训课程中的培训班是否存在
							sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
									+ ResourceFactory.getProperty("train.info.import.error.notexist"));
						} else {
							sb = new ArrayList();
							sb.add("1.&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
									+ ResourceFactory.getProperty("train.info.import.error.notexist"));
						}
						msgMap.put(primarykeyValue, sb);
						primarykeyValue = null;
						continue;
					}
					int i = Integer.parseInt((String) hm.get(primarykeyValue));
					if (i > 1) {
						ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
						if (sb != null) {
							int no = sb.size();//判断培训课程中的培训班是否有重复记录
							sb.add((no + 1) + ".&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
									+ ResourceFactory.getProperty("train.info.import.more.inttype"));
						} else {
							sb = new ArrayList();
							sb.add("1.&nbsp;" + ResourceFactory.getProperty("train.info.import.error.mClass") + "[" + primarykeyValue + "]"
									+ ResourceFactory.getProperty("train.info.import.more.inttype"));
						}
						msgMap.put(primarykeyValue, sb);
						primarykeyValue = null;
						continue;
					}
				}
				keysb.append(primarykeyValue + "','");

				for (int n = 0; n < cols; n++) {
					String key = (String) keyList.get(n);
					FieldItem item = (FieldItem) fieldMap.get(key);
					if (item == null)
						continue;
					int idex = ((Integer) fieldIdex.get(key)).intValue();
					cell = orow.getCell(idex);
					if (cell != null) {
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_FORMULA:
							break;
						case Cell.CELL_TYPE_NUMERIC: {
							if ("D".equals(item.getItemtype())) {
								Date d = cell.getDateCellValue();
								if ((item.isFillable() || "b0110".equalsIgnoreCase(item.getItemid())) && (d == null || "".equals(d))){
									checkIsFillable(primarykeyValue, item, msgMap);
									if("r31".equalsIgnoreCase(fieldsetid))
										errorkey.put(primarykeyValue+"a"+sun, "");
									else if("r41".equalsIgnoreCase(fieldsetid))
										errorkey.put(primarykeyValue+"b"+sun, "");
									break;
								}
								try {
									value = formater.format(d);
									valueMap.put(key, value);
								} catch (Exception e) {
									ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
									if (sb != null) {
										int no = sb.size();//得到错误提示的数量
										sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
														+ ResourceFactory.getProperty("workbench.info.import.error.datetype") + cell.getNumericCellValue());
									} else {
										sb = new ArrayList();
										sb.add("1.&nbsp;[" + item.getItemdesc() + "]"
														+ ResourceFactory.getProperty("workbench.info.import.error.datetype") + cell.getNumericCellValue());
									}
									value = "```";
									msgMap.put(primarykeyValue, sb);
								}
								break;
							} else {
								dvalue = cell.getNumericCellValue();
								String str = String.valueOf(dvalue);
								if ((item.isFillable() || "b0110".equalsIgnoreCase(item.getItemid())) && (str == null || str.length()<1)){
									checkIsFillable(primarykeyValue, item, msgMap);
									if("r31".equalsIgnoreCase(fieldsetid))
										errorkey.put(primarykeyValue+"a"+sun, "");
									else if("r41".equalsIgnoreCase(fieldsetid))
										errorkey.put(primarykeyValue+"b"+sun, "");
									break;
								}
								while (str.indexOf(",") > -1) {
									str = str.substring(0, str.indexOf(",")) + str.substring(str.indexOf(",") + 1);
								}
								Pattern p = Pattern.compile("[+]?[\\d.]*");
								Matcher m = p.matcher(String.valueOf(str));
								if (!m.matches()) {
									ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
									if (sb != null) {
										int no = sb.size();
										sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
														+ ResourceFactory.getProperty("train.info.import.error.inttype") + str);
									} else {
										sb = new ArrayList();
										sb.add("1.&nbsp;[" + item.getItemdesc() + "]"
														+ ResourceFactory.getProperty("train.info.import.error.inttype") + str);
									}
									msgMap.put(primarykeyValue, sb);
									valueMap.put(key, "```");
								} else {
									value = String.valueOf((str));
									if ("N".equals(item.getItemtype())) {
										int dw = item.getDecimalwidth();
										if (dw == 0) {
											if (value.indexOf('.') != -1)
												value = value.substring(0, value.indexOf('.'));
										} else {
											if (value.indexOf('.') != -1) {
												String dec = value.substring(value.indexOf('.') + 1);
												if (dec.length() > dw)
													value = String.valueOf(PubFunc.round(value, dw));
											}
										}
										value = value.replaceAll("\\+", "");
									}
									if (value.length() > 9)
										valueMap.put(key, "```");
									else
										valueMap.put(key, value);
								}
								break;
							}
						}
						case Cell.CELL_TYPE_STRING: {
							value = cell.getStringCellValue().trim();
							value = value.replace("'0", "0");
							if ((item.isFillable() || "b0110".equalsIgnoreCase(item.getItemid()))
									&& (value == null || value.length() < 1)) {
								checkIsFillable(primarykeyValue, item, msgMap);
								if("r31".equalsIgnoreCase(fieldsetid))
									errorkey.put(primarykeyValue+"a"+sun, "");
								else if("r41".equalsIgnoreCase(fieldsetid))
									errorkey.put(primarykeyValue+"b"+sun, "");
								break;
							}
							if ("N".equals(item.getItemtype())) {
								Pattern p = Pattern.compile("[+]?[\\d.]*");
								Matcher m = p.matcher(value);
								if (!m.matches()) {
									ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
									if (sb != null) {
										int no = sb.size();
										sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
														+ ResourceFactory.getProperty("train.info.import.error.inttype") + value);
									} else {
										sb = new ArrayList();
										sb.add("1.&nbsp;[" + item.getItemdesc() + "]" 
												+ ResourceFactory.getProperty("train.info.import.error.inttype")+ value);
									}
									value = "```";
									msgMap.put(primarykeyValue, sb);
								} else {
									if ("N".equals(item.getItemtype())) {
										int dw = item.getDecimalwidth();
										if (dw == 0) {
											if (value.indexOf('.') != -1)
												value = value.substring(0, value.indexOf('.'));
										} else {
											if (value.indexOf('.') != -1) {
												String dec = value.substring(value.indexOf('.') + 1);
												if (dec.length() > dw)
													value = value.substring(0, value.indexOf('.')+ dw + 1);
											}
										}
										value = value.replaceAll("\\+", "");
									}
									if (value.length() > 9)
										value = "```";
								}
							}
							if ("D".equals(item.getItemtype())) {
								if (!"".equals(value)) {
									String tmp = checkdate(value);
									if ("false".equals(tmp)) {
										ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
										if (sb != null) {
											int no = sb.size();
											sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
															+ ResourceFactory.getProperty("workbench.info.import.error.datetype") + value);
										} else {
											sb = new ArrayList();
											sb.add("1.&nbsp;[" + item.getItemdesc()+ "]"
															+ ResourceFactory.getProperty("workbench.info.import.error.datetype") + value);
										}
										value = "```";
										msgMap.put(primarykeyValue, sb);
									} else {
										value = tmp;
									}
								}
							}
							valueMap.put(key, value);
							break;
						}
						default:
							if (item.isFillable()) { // 判断必填项是否为空
								ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
								if (sb != null) {
									int no = sb.size();
									sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]"
													+ ResourceFactory.getProperty("train.job.import.isnull"));
								} else {
									sb = new ArrayList();
									sb.add("1.&nbsp;[" + item.getItemdesc() + "]"
													+ ResourceFactory.getProperty("train.job.import.isnull"));
								}
								msgMap.put(primarykeyValue, sb);
								if("r31".equalsIgnoreCase(fieldsetid))
									errorkey.put(primarykeyValue+"a"+sun, "");
								else if("r41".equalsIgnoreCase(fieldsetid))
									errorkey.put(primarykeyValue+"b"+sun, "");
								break;
							}
						}
					}
				}
				if ("r31".equalsIgnoreCase(fieldsetid)){
					if(nhm.containsKey(primarykeyValue)){
						int i = Integer.parseInt((String)nhm.get(primarykeyValue));
						nhm.put(primarykeyValue, i+1+"");
					}else
						nhm.put(primarykeyValue, "1");
				}
				if (!"".equals(primarykeyValue)) {// 唯一标识为空就不提取此行记录
					valueList.add(valueMap);
				}
			}
			sun++;
		}
		return maps;
	}
/**
 * 检测日期型数据是否合理
 * @param str
 * @return
 */
	private String checkdate(String str) {
		if (str.length() < 4) {
			return "false";
		}
		if (str.length() == 4&&Integer.parseInt(str)>=1753) {
			Pattern p = Pattern.compile("^(\\d{4})$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				return str + "-01-01";
			} else {
				return "false";
			}
		}
		if (str.length() < 6) {
			Pattern p = Pattern.compile("^(\\d{4})年$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				return str.replace("年", "-") + "01-01";
			} else {
				return "false";
			}
		}
		if (str.length() == 7) {
			if (str.indexOf("月") != -1) {
				Pattern p = Pattern
						.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]$");
				Matcher m = p.matcher(str);
				if (m.matches()) {
					if (str.indexOf("月") != -1) {
						return str.replace("年", "-").replace(".", "-").replace("月", "-") + "01";
					} else {
						return str.replace("年", "-").replace(".", "-") + "-01";
					}
				} else {
					return "false";
				}
			} else {
				Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])$");
				Matcher m = p.matcher(str);
				if (m.matches()) {
					return str.replace("年", "-").replace(".", "-") + "-01";
				} else {
					return "false";
				}
			}
		}
		if (str.length() < 8) {// 2010年3 2010年3月
			Pattern p = Pattern
					.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				if (str.indexOf("月") != -1) {
					return str.replace("年", "-").replace(".", "-").replace("月", "-") + "01";
				} else {
					return str.replace("年", "-").replace(".", "-") + "-01";
				}
			} else {
				return "false";
			}
		}
		if (str.length() == 8) {// 2010年3 2010年3月1
			Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				str = str.replace("年", "-").replace(".", "-").replace("月", "-");
				if (str.lastIndexOf("-") == str.length()) {
					if (str.length() < 10) {
						return str + "01";
					}
				} else {
					String[] temps = str.split("-");
					if (temps.length > 2) {
						String t = "false";
						if (temps[0].length() > 0 && temps[1].length() > 0 && temps[2].length() > 0) {
							int year = Integer.parseInt(temps[0]);
							int month = Integer.parseInt(temps[1]);
							int day = Integer.parseInt(temps[2]);
							switch (month) {
							case 1:
							case 3:
							case 5:
							case 7:
							case 8:
							case 10:
							case 12: {
								if (1 <= day && day <= 31) {
									t = str;
								}
								break;
							}
							case 4:
							case 6:
							case 9:
							case 11: {
								if (1 <= day && day <= 30) {
									t = str;
								}
								break;
							}
							case 2: {
								if (isLeapYear(year)) {
									if (1 <= day && day <= 29) {
										t = str;
									}
								} else {
									if (1 <= day && day <= 28) {
										t = str;
									}
								}
								break;
							}

							}
						}
						return t;
					} else {
						return "false";
					}

				}
			} else {
				return "false";
			}
		}
		Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[日]*$");
		Matcher m = p.matcher(str);
		if (m.matches()) {
			String temp = str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", "");
			String[] temps = temp.split("-");
			String t = "false";
			if (temps[0].length() > 0 && temps[1].length() > 0 && temps[2].length() > 0) {
				int year = Integer.parseInt(temps[0]);
				int month = Integer.parseInt(temps[1]);
				int day = Integer.parseInt(temps[2]);
				switch (month) {
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12: {
					if (1 <= day && day <= 31) {
						t = temp;
					}
					break;
				}
				case 4:
				case 6:
				case 9:
				case 11: {
					if (1 <= day && day <= 30) {
						t = temp;
					}
					break;
				}
				case 2: {
					if (isLeapYear(year)) {
						if (1 <= day && day <= 29) {
							t = temp;
						}
					} else {
						if (1 <= day && day <= 28) {
							t = temp;
						}
					}
					break;
				}

				}
			}
			return t;
		} else {
			return "false";
		}
	}

	private void initPriv() {
		if (this.userView.isSuper_admin()) {
			return;
		}
		String codesetid = this.userView.getManagePrivCode();
		if ("UN".equalsIgnoreCase(codesetid)) {
		} else if ("UM".equalsIgnoreCase(codesetid)) {
		}
		this.userView.getManagePrivCodeValue().toUpperCase();
	}
/**
 * 过滤单位部门
 * @param valueList
 * @param dao
 * @throws Exception
 */
	private void doOrgCheck(ArrayList valueList, ContentDAO dao) throws Exception {
		Pattern p = Pattern.compile("[A-Z0-9]*");
		for (int m = 0; m < valueList.size(); m++) {
			HashMap valuemap = (HashMap) valueList.get(m);
			String value = "";
			String b0110 = (String) valuemap.get("b0110");
			if (b0110 != null && b0110.endsWith(")")) {
				value = b0110.substring(b0110.lastIndexOf("(") + 1, b0110.lastIndexOf(")"));
				Matcher ma = p.matcher(value);
				if (ma.matches()) {
					valuemap.put("b0110", value);
					b0110 = value;
				}
			}
			String e0122 = (String) valuemap.get("e0122");
			if (e0122 != null && e0122.endsWith(")")) {
				value = e0122.substring(e0122.lastIndexOf("(") + 1, e0122.lastIndexOf(")"));
				Matcher ma = p.matcher(value);
				if (ma.matches()) {
					valuemap.put("e0122", value);
					e0122 = value;
				}
			}
			if (b0110 != null && !"".equals(b0110)) {
				if (e0122 != null && !"".equals(e0122)) {
					if (checkUNUM(b0110, e0122, dao)) {
						valuemap.put("e0122", "");
					}
				} 
			} else {
				valuemap.put("e0122", "");
			}
		}
	}
/**
 * 获取所填单位和部门的机构编码
 * @param valueList
 * @param fieldMap
 * @param dao
 * @throws Exception
 */
	private void doUNUMKCheck(ArrayList valueList, HashMap fieldMap, ContentDAO dao) throws Exception {
		Pattern p = Pattern.compile("[A-Z0-9]*");
		for (int m = 0; m < valueList.size(); m++) {
			HashMap valuemap = (HashMap) valueList.get(m);
			String value = "";
			for (Iterator i = valuemap.keySet().iterator(); i.hasNext();) {
				String itemid = (String) i.next();
				if ("b0110".equalsIgnoreCase(itemid) || "e0122".equalsIgnoreCase(itemid))
					continue;
				FieldItem item = (FieldItem) fieldMap.get(itemid);
				if (item == null)
					continue;
				if ((!"UN".equalsIgnoreCase(item.getCodesetid())) && (!"UM".equalsIgnoreCase(item.getCodesetid())))
					continue;
				String b0110 = (String) valuemap.get(itemid);
				if (b0110 == null || !b0110.endsWith(")"))
					continue;
				value = b0110.substring(b0110.lastIndexOf("(") + 1, b0110.lastIndexOf(")"));
				Matcher ma = p.matcher(value);
				if (ma.matches())
					valuemap.put(itemid, value);
			}
		}
	}
/**
 * 检查单位部门是否在管理范围内
 * @param b0110
 * @param e0122
 * @param dao
 * @return
 * @throws Exception
 */
	private boolean checkUNUM(String b0110, String e0122, ContentDAO dao) throws Exception {
		boolean flag = false;
		Pattern p = Pattern.compile("[A-Z0-9]*");
		Matcher ma = p.matcher(b0110);//支持直接输入机构编码，可解决机构名重复的问题
		String unCodeitemid = "";
		String umCodeitemid = "";
		
		if (ma.matches()) {
			unCodeitemid = b0110;
		} else {
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='" + b0110 + "' and codesetid='UN'" + this.tmpstr);
			if (this.frecset.next()) {
				unCodeitemid = this.frecset.getString("codeitemid");
			} else {
				flag = true;
			}
		}
		ma = p.matcher(e0122);
		if (ma.matches()) {
			umCodeitemid = e0122;
		} else {
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='" + e0122 + "' and codesetid='UM'" + this.tmpstr);
			if (this.frecset.next()) {
				umCodeitemid = this.frecset.getString("codeitemid");
			} else {
				flag = true;
			}
		}
		if (!"".equals(unCodeitemid) && !"".equals(umCodeitemid)) {
			flag = this.dochildchecknm(unCodeitemid, umCodeitemid, dao);
		}
		return flag;
	}
/**
 * 检测所填部门是否属于所填单位
 * @param unCodeitemid
 * @param umCodeitemid
 * @param dao
 * @return
 * @throws Exception
 */
	private boolean dochildchecknm(String unCodeitemid, String umCodeitemid, ContentDAO dao) throws Exception {
		RowSet rs = null;
		boolean flag = true;
		String sql = "select codeitemid from organization where codesetid<>'@K' and parentid='" + unCodeitemid + "' and parentid<>codeitemid";
		try {
			rs = dao.search(sql);
			String codeitemid = "";
			while (rs.next()) {
				codeitemid = rs.getString("codeitemid");
				if (umCodeitemid.equalsIgnoreCase(codeitemid)) {
					flag = false;
					break;
				} else if (umCodeitemid.indexOf(codeitemid) != -1) {
					flag = this.dochildchecknm(codeitemid, umCodeitemid, dao);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return flag;
	}

	/**
	 * 闰年的条件是： ① 能被4整除，但不能被100整除； ② 能被100整除，又能被400整除。
	 * 
	 * @param year
	 * @return
	 */
	private boolean isLeapYear(int year) {
		boolean t = false;
		if (year % 4 == 0) {
			if (year % 100 != 0) {
				t = true;
			} else if (year % 400 == 0) {
				t = true;
			}
		}
		return t;
	}

	/**
	 * 判断是否是关联表类指标
	 * 
	 * @param fieldsetid
	 * @param itemid
	 * @return
	 */
	private boolean isCodeflag(String fieldsetid, String itemid) {
		boolean t = true;
		String sql = "select codeflag from t_hr_busifield where fieldsetid='" + fieldsetid.toUpperCase() + "' and itemid='" + itemid.toUpperCase() + "'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				String caodeflag = this.frowset.getString("codeflag");
				if ("1".equalsIgnoreCase(caodeflag))
					t = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return t;
	}
	/**
	 * 判断关联表类型的指标的值是否有对应记录
	 * @param value
	 * @param codesetid
	 * @param b0110
	 * @return true：有 false: 没有 manyRow：有多条记录
	 * @throws GeneralException
	 * 2014.7.28 xxd
	 * 修改原因 ：培训班对应计划优化
	 */
	private String Checkcodeflag(String value, String codesetid) throws GeneralException {
		String flag = "false";
		codesetid = codesetid.replace("1_", "");
		String codetable = "";
		String codevalue = "";
		String codedesc = "";
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		try {
			String sql = "select codetable,codevalue,codedesc from t_hr_relatingcode where codesetid='" + codesetid + "'";
			if (!"r25".equalsIgnoreCase(codesetid)) {
				this.frowset = dao.search(sql);
				if (this.frowset.next()) {
					codetable = this.frowset.getString("codetable");
					codevalue = this.frowset.getString("codevalue");
					codedesc = this.frowset.getString("codedesc");
				}
			} else {
				codetable = "r25";
				codevalue = "r2501";
				codedesc = "r2502";
			}
			
			FieldItem fi = DataDictionary.getFieldItem(codevalue, codetable);
			String type = fi.getItemtype();
			if ("N".equalsIgnoreCase(type)) {
				Pattern pattern = Pattern.compile("[0-9]*");
				Matcher match = pattern.matcher(value);
				if (match.matches() == false) {
					flag = "false";
					return flag;
				} 
			}
			
			String strWhere = this.tmpstr.replaceAll("codeitemid", "b0110");
			strWhere = strWhere.replace(")", " or b0110='HJSJ')");
			//根据对应计划编号进行查询
			sql = "select " + codevalue + " from " + codetable + " where " + codevalue + "='" + value + "'"  + strWhere;

			if("r25".equalsIgnoreCase(codesetid))
				sql+= " and r2509 in ('03','04')";
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				flag = "true";
				return flag;
			}
			//根据对应计划名称进行查询
			sql = "select " + codevalue + " from " + codetable + " where " + codedesc + "='" + value + "'"  + strWhere;
			if("r25".equalsIgnoreCase(codesetid))
				sql+= " and r2509 in ('03','04')";
			this.frowset = dao.search(sql);
			int rowCount = 0 ;
			//判断是否找到多条记录
			while(this.frowset.next())
			{
				rowCount++;
				value = this.frowset.getString(codevalue);
			}
			if (rowCount==1) {
				flag = "true";
				//如果根据对应计划名称查询到值，则将计划编号绑定返回
				return flag+","+value;
			}else if(rowCount>1){
				flag="manyRow";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
	/**
	 * 获取当前用户的权限生成查询条件
	 * @return
	 */
	private String getPiv() {
		StringBuffer tmpstr = new StringBuffer();
		try {
		    StringBuffer e0122s = new StringBuffer();
		    if(this.userView.isSuper_admin())
		        return tmpstr.toString();
		    TrainCourseBo bo = new TrainCourseBo(this.userView);
		    String manpriv = bo.getUnitIdByBusi();
		    String code = "";
		    
		    if (manpriv == null || manpriv.trim().length() < 3 || manpriv.indexOf("UN`") != -1)
		        return tmpstr.toString();
		    String[] tmp = manpriv.split("`");
		    tmpstr.append("and (");
		    for (int i = 0; i < tmp.length; i++) {
		        code = tmp[i];
		        if (i > 0)
		            tmpstr.append(" or ");
		        if ("UN".equalsIgnoreCase(code.substring(0, 2)))
		            tmpstr.append("codeitemid like '" + code.substring(2, code.length()) + "%'");
		        else if ("UM".equalsIgnoreCase(code.substring(0, 2))){
		            tmpstr.append("codeitemid like '" + code.substring(2, code.length()) + "%'");
		            e0122s.append("'"+code.substring(2, code.length())+"',");
		        }
		    }
		    tmpstr.append(")");
		    this.b0110s=getB0110(e0122s.toString());		    
		} catch (Exception e) {
		    e.printStackTrace();
        }
		return tmpstr.toString();
	}
	/**
	 * 获取权限内的非结束状态的培训班名称
	 * @return
	 */
	private HashMap getClassName(){
		HashMap hm = new HashMap();
		String strWhere = this.tmpstr.replaceAll("codeitemid", "b0110");
		String sql = "select r3130 from r31 where r3127<>'06' and (b0110 is not null or b0110<>'') "+ strWhere;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try{
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String r3130 = this.frowset.getString("r3130");
				if(hm.containsKey(r3130)){
					int i = Integer.parseInt((String)hm.get(r3130));
					hm.put(r3130, i+1+"");
				}else
					hm.put(r3130, "1");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
		
	}
	
	private void checkIsFillable(String primarykeyValue, FieldItem item,HashMap msgMap){
		ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
		if (sb != null) {
			int no = sb.size();
			sb.add((no + 1) + ".&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("train.job.import.isnull"));
		} else {
			sb = new ArrayList();
			sb.add("1.&nbsp;[" + item.getItemdesc() + "]" + ResourceFactory.getProperty("train.job.import.isnull"));
		}
		msgMap.put(primarykeyValue, sb);
	}
	
	/**
     * 获取部门的所属单位
     * @param e0122s
     * @return
     */
    private String getB0110(String e0122s) {
        String b0110 = "";
        try {
            String[] e0122 = e0122s.split(",");
            for (int i = 0; i < e0122.length; i++) {

                if (e0122[i] == null || e0122[i].trim().length() < 1)
                    continue;
                
                List savePos = getStationPos(e0122[i]);
                for (int n = 0; n < savePos.size(); n++) {
                    StationPosView posview = (StationPosView) savePos.get(n);
                    if (!"b0110".equalsIgnoreCase(posview.getItem()))
                        continue;

                    b0110 += "'" + posview.getItemvalue() + "',";
                    break;
                }
            }

            if(b0110 !=null && b0110.length() > 0)
                b0110 = b0110.substring(0, b0110.length()-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b0110;
    }
    /**
     * 获取部门的所属单位
     * @param code
     * @return
     */
    private ArrayList getStationPos(String code) {
        ArrayList poslist = new ArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean isdep = false;
        boolean isorg = false;
        StringBuffer strsql = new StringBuffer();
        try {
            String pre = "UM";
            conn = this.getFrameconn();
            ContentDAO db = new ContentDAO(conn);
            while (!"UN".equalsIgnoreCase(pre)) {
                if (strsql != null && strsql.length() > 0)
                    strsql.delete(0, strsql.length());

                strsql.append("select * from organization");
                strsql.append(" where codeitemid=");
                strsql.append(code);
                rs = db.search(strsql.toString()); // 执行当前查询的sql语句
                if (rs.next()) {
                    StationPosView posview = new StationPosView();
                    pre = rs.getString("codesetid");
                    if ("UM".equalsIgnoreCase(pre)) {
                        if (isdep == false) {
                            posview.setItem("e0122");
                            posview.setItemvalue(rs.getString("codeitemid"));
                            posview.setItemviewvalue(rs.getString("codeitemdesc"));
                            isdep = true;
                            poslist.add(posview);
                        }
                    } else if ("UN".equalsIgnoreCase(pre)) {
                        if (isorg == false) {
                            posview.setItem("b0110");
                            posview.setItemvalue(rs.getString("codeitemid"));
                            posview.setItemviewvalue(rs.getString("codeitemdesc"));
                            isorg = true;
                            poslist.add(posview);
                        }
                    }

                    code = "'"+rs.getString("parentid")+"'";
                }
            }  
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(stmt);
        }

        return poslist;
    }
}
