package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 将要导入的数据进行分析，是否进行提示报告。读入内存。
 * @author xujian
 *Apr 24, 2012
 */
public class InportDataTrans extends IBusiness {

	//private String fieldsetdesc=DataDictionary.getFieldSetVo("r25").getCustomdesc();
	private String privM="b0110";
	private String privMv="";
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap msgMap = new HashMap();
		ArrayList msglist = new ArrayList();
		String isupdate="0";
		int[] counts={0,0,0,0,0};//预计将有10条记录被成功导入。因培训名称为空(3条记录)、单位名称为空(3条记录)、没有导入权限(3条记录)和培训名称已存在(3条记录)而不能被导入
		try {
			this.initPriv();
			FormFile file = (FormFile) this.getFormHM().get("file");
			
			if(!FileTypeUtil.isFileTypeEqual(file))
			    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.fileuploaderror")));
			
			Object[] maps = readExcel(file,msgMap,counts);
			this.getFormHM().put("maps", maps);
			HashMap fieldMap = (HashMap)maps[0];
			ArrayList valueList = (ArrayList)maps[1];
			String primarykeys=((StringBuffer)maps[2]).toString();
			primarykeys="','"+primarykeys;
			ArrayList keyList =(ArrayList)maps[3];
			StringBuffer a0100sb = (StringBuffer)maps[4];
			HashMap key2num = (HashMap)maps[5];
			String sql = "";
			if(keyList.size()<1){
				return;
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String primarykey =(String)keyList.get(0);
			//过滤单位部门岗位是否正确
			this.doOrgCheck(valueList,dao);
			this.doUNUMKCheck(valueList, fieldMap, dao);
			//检查代码型的字段从excel中读取的codeitemdesc值是否在库中有对应的codeitemid
			for(int n=0;n<keyList.size();n++){
				String key=(String)keyList.get(n);
				FieldItem item=(FieldItem)fieldMap.get(key);
				if(item==null)
					continue;
				String itemid= item.getItemid();
				if((item.getCodesetid()!=null&&!"".equals(item.getCodesetid())&&!"0".equals(item.getCodesetid()))&& "A".equalsIgnoreCase(item.getItemtype())){
					if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())){
						sql = "select codeitemdesc,codeitemid from organization where upper(codesetid)='"+item.getCodesetid().toUpperCase()+"' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date";
					}else{
						sql = "select codeitemdesc,codeitemid from codeitem where upper(codesetid)='"+item.getCodesetid().toUpperCase()+"'";// and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date";
					}
					this.frowset=dao.search(sql);
					HashMap codemap= new HashMap();
					while(this.frowset.next()){
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
					
					for(int m=0;m<valueList.size();m++){
						HashMap valuemap=(HashMap)valueList.get(m);
						String primarykeyvalue=(String)valuemap.get(primarykey);
						String value=(String)valuemap.get(itemid);
						
						if("b0110".equalsIgnoreCase(item.getItemid())){
							if(value==null|| "".equals(value)){
								ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
								if(sb!=null){
									int no=sb.size();
									sb.add((no+1)+".&nbsp;"+ResourceFactory
											.getProperty("workbench.info.import.error.nob0110").substring(1));
								}else{
									sb= new ArrayList();
									sb.add("1.&nbsp;"+ResourceFactory
											.getProperty("workbench.info.import.error.nob0110").substring(1));
									msgMap.put(primarykeyvalue, sb);
								}
								valueList.remove(valuemap);
								primarykeys=primarykeys.replaceAll("','"+primarykeyvalue+"','", "','");
								--m;
								counts[2]=counts[2]+1;
								continue;
							}
						}
						if(value==null)
							continue;
						if("e0122".equalsIgnoreCase(item.getItemid())|| "b0110".equalsIgnoreCase(item.getItemid())|| "e01a1".equalsIgnoreCase(item.getItemid())){
							Pattern p = Pattern.compile("[A-Z0-9]*");
							Matcher ma=p.matcher(value);//支持直接输入机构编码，可解决机构名重复的问题
							if(!codemap.containsKey(value)&&!"".equals(value)&&!ma.matches()){
								valuemap.put(itemid, "```");
								ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
								if("b0110".equalsIgnoreCase(item.getItemid())){
									if(sb!=null){
										int no=sb.size();
										sb.add((no+1)+".&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
												.getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
												.getProperty("workbench.info.import.error.codenor")+ResourceFactory
												.getProperty("workbench.info.import.error.nob0110"));
									}else{
										sb= new ArrayList();
										sb.add("1.&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
												.getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
												.getProperty("workbench.info.import.error.codenor")+ResourceFactory
												.getProperty("workbench.info.import.error.nob0110"));
										msgMap.put(primarykeyvalue, sb);
									}
									valueList.remove(valuemap);
									primarykeys=primarykeys.replaceAll("','"+primarykeyvalue+"','", "','");
									--m;
									counts[2]=counts[2]+1;
								}else{
									if(sb!=null){
										int no=sb.size();
										sb.add((no+1)+".&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
												.getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
												.getProperty("workbench.info.import.error.codenor"));
									}else{
										sb= new ArrayList();
										sb.add("1.&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
												.getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
												.getProperty("workbench.info.import.error.codenor"));
										msgMap.put(primarykeyvalue, sb);
									}
								}
								msgMap.put(primarykeyvalue, sb);
							}else{
								if(!"".equals(value)&&!ma.matches()){
									String tmpvalue=(String)codemap.get(value);
									valuemap.put(itemid, tmpvalue);
									if(!"admin".equalsIgnoreCase(this.privM)&&this.privM.equalsIgnoreCase(item.getItemid())){
										if(this.privMv.indexOf("`")==-1){
											if(tmpvalue.toUpperCase().indexOf(this.privMv)==-1){
												ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
													if(sb!=null){
														int no=sb.size();
														sb.add((no+1)+".&nbsp;"+ResourceFactory
																.getProperty("workbench.info.import.error.nopriv"));
													}else{
														sb= new ArrayList();
														sb.add("1.&nbsp;"+ResourceFactory
																.getProperty("workbench.info.import.error.nopriv"));
														msgMap.put(primarykeyvalue, sb);
													}
												valueList.remove(valuemap);
												primarykeys=primarykeys.replaceAll("','"+primarykeyvalue+"','", "','");
												--m;
												counts[3]=counts[3]+1;
											}
										}else{
											String []tmp=this.privMv.split("`");
											boolean flag = true;
											for(int i=tmp.length-1;i>=0;i--){
												if(tmpvalue.toUpperCase().indexOf(tmp[i])!=-1){
													flag=false;
													break;
												}
											}
											if(flag){
												ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
												if(sb!=null){
													int no=sb.size();
													sb.add((no+1)+".&nbsp;"+ResourceFactory
															.getProperty("workbench.info.import.error.nopriv"));
												}else{
													sb= new ArrayList();
													sb.add("1.&nbsp;"+ResourceFactory
															.getProperty("workbench.info.import.error.nopriv"));
													msgMap.put(primarykeyvalue, sb);
												}
												valueList.remove(valuemap);
												primarykeys=primarykeys.replaceAll("','"+primarykeyvalue+"','", "','");
												--m;
												counts[3]=counts[3]+1;
											}
										}
									}
								}else if(!"".equals(value)&&ma.matches()){
									if(!"admin".equalsIgnoreCase(this.privM)&&this.privM.equalsIgnoreCase(item.getItemid())){
										if(this.privMv.indexOf("`")==-1){
											if(value.toUpperCase().indexOf(this.privMv)==-1){
												ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
													if(sb!=null){
														int no=sb.size();
														sb.add((no+1)+".&nbsp;"+ResourceFactory
																.getProperty("workbench.info.import.error.nopriv"));
													}else{
														sb= new ArrayList();
														sb.add("1.&nbsp;"+ResourceFactory
																.getProperty("workbench.info.import.error.nopriv"));
														msgMap.put(primarykeyvalue, sb);
													}
												valueList.remove(valuemap);
												primarykeys=primarykeys.replaceAll("','"+primarykeyvalue+"','", "','");
												--m;
												counts[3]=counts[3]+1;
											}
										}else{
											String []tmp=this.privMv.split("`");
											boolean flag = true;
											for(int i=tmp.length-1;i>=0;i--){
												if(value.toUpperCase().indexOf(tmp[i])!=-1){
													flag=false;
													break;
												}
											}
											if(flag){
												ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
												if(sb!=null){
													int no=sb.size();
													sb.add((no+1)+".&nbsp;"+ResourceFactory
															.getProperty("workbench.info.import.error.nopriv"));
												}else{
													sb= new ArrayList();
													sb.add("1.&nbsp;"+ResourceFactory
															.getProperty("workbench.info.import.error.nopriv"));
													msgMap.put(primarykeyvalue, sb);
												}
												valueList.remove(valuemap);
												primarykeys=primarykeys.replaceAll("','"+primarykeyvalue+"','", "','");
												--m;
												counts[3]=counts[3]+1;
											}
										}
									}
								}
							}
						}else if("UN".equalsIgnoreCase(item.getCodesetid())||"UM".equalsIgnoreCase(item.getCodesetid())||"@K".equalsIgnoreCase(item.getCodesetid())){
							Pattern p = Pattern.compile("[A-Z0-9]*");
							Matcher ma=p.matcher(value);//支持直接输入机构编码，可解决机构名重复的问题
							if(!codemap.containsKey(value)&&!"".equals(value)&&!ma.matches()){
								valuemap.put(itemid, "```");
								ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
								if(sb!=null){
									int no=sb.size();
									sb.add((no+1)+".&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
											.getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
											.getProperty("workbench.info.import.error.codenor"));
								}else{
									sb= new ArrayList();
									sb.add("1.&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
											.getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
											.getProperty("workbench.info.import.error.codenor"));
									msgMap.put(primarykeyvalue, sb);
								}
							}else{
								if(!"".equals(value)&&!ma.matches()){
									valuemap.put(itemid, codemap.get(value));
								}
							}
						}else{
							if(!codemap.containsKey(value)&&!"".equals(value)){
								valuemap.put(itemid, "```");
								ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
								if(sb!=null){
									int no=sb.size();
									sb.add((no+1)+".&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
											.getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
											.getProperty("workbench.info.import.error.codenor"));
								}else{
									sb= new ArrayList();
									sb.add("1.&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
											.getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
											.getProperty("workbench.info.import.error.codenor"));
									msgMap.put(primarykeyvalue, sb);
								}
							}else{
							    if(!"".equals(value)){
                                    String codeitemid = (String) codemap.get(value);
                                    if(!leafItemMmaps.isEmpty() && !leafItemMmaps.containsKey(codeitemid)) {
                                        valuemap.put(itemid, "```");
                                        ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
                                        String selectEndCode = ResourceFactory.getProperty("train.b_plan.select.endcode").replace("{0}", item.getItemdesc()).replace("{1}", value);
                                        StringBuffer msg = new StringBuffer();
                                        msg.append(selectEndCode);
                                        valueList.remove(m);
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
				}
			}
			primarykeys=primarykeys.substring(3);
			ArrayList prList = new ArrayList();
			String ttt[]=primarykeys.split("','");
			if(ttt.length>500){
				StringBuffer sb=new StringBuffer();
				int n=0;
				boolean f=false;
				for(int i=0;i<ttt.length;i++){
					if("".equals(ttt[i])){
						continue;
					}
					f=false;
					sb.append(ttt[i]+"','");
					if(n>498){
						f=true;
						prList.add(sb.toString());
						sb=new StringBuffer();
						n=0;
					}
					n++;
				}
				if(!f)
					prList.add(sb.toString());
			}else{
				prList.add(primarykeys);
			}
			
			//记录下已经存在的，暂时处理成直接导入，此处注释的代码为对记录的名称是否存在进行的判断
//			for(int m=0;m<prList.size();m++){
//				String tempp=(String)prList.get(m);	
//							if(this.privM.equals("admin")){
//								sql="select R2501,"+primarykey+" from r25 where upper("+primarykey+") in('"+tempp.toUpperCase()+"##')";
//							}else{
//								sql="select "+this.privM+",R2501,"+primarykey+" from r25 where upper("+primarykey+") in('"+tempp.toUpperCase()+"##')";
//							}
//							this.frowset = dao.search(sql);
//							while(this.frowset.next()){
//								String tmp=this.frowset.getString(primarykey);
//								counts[4]=counts[4]+1;
//								a0100sb.append(tmp+",");
//									ArrayList sb=(ArrayList)msgMap.get(tmp);
//									if(sb!=null){
//										int no=sb.size();
//										sb.add((no+1)+".&nbsp;"+ResourceFactory.getProperty("workbench.info.import.error.haveset")/*+"&nbsp;&nbsp;<input type=checkbox name=selectflag value="+tmp+" onclick='markupdate(this);' />&nbsp;"+ResourceFactory.getProperty("workbench.info.recordupdate.lebal")+"&nbsp;&nbsp;&nbsp;"*/);
//									}else{
//										sb= new ArrayList();
//										sb.add("1.&nbsp;"+ResourceFactory.getProperty("workbench.info.import.error.haveset")/*+"&nbsp;&nbsp;<input type=checkbox name=selectflag value="+tmp+" onclick='markupdate(this);' />&nbsp;"+ResourceFactory.getProperty("workbench.info.recordupdate.lebal")+"&nbsp;&nbsp;&nbsp;"*/);
//										msgMap.put(tmp, sb);
//									}
//									isupdate="1";
//							}
//			}
			counts[0]=valueList.size()-counts[4];
			if(counts[0] < 0){
				counts[0] = 0;
			}
			
			for(Iterator i =msgMap.keySet().iterator();i.hasNext();){
				String key=(String)i.next();
				//LazyDynaBean ldb= new LazyDynaBean();
				MessBean mb = new MessBean();
				mb.setKey2num((String)key2num.get(key));
				mb.setKeyid(key);
				ArrayList sb=(ArrayList)msgMap.get(key);
				StringBuffer sbb=new StringBuffer();
				for(int n=0;n<sb.size();n++){
					sbb.append("&nbsp;"+(String)sb.get(n)+"</br>");
				}
				mb.setContent(sbb.toString());
				msglist.add(mb);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("msglist", msglist);
			this.getFormHM().put("isupdate", isupdate);
			this.getFormHM().put("counts", counts);
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
	private Object[] readExcel(FormFile file, HashMap msgMap,int[] counts) throws Exception {
		ArrayList valueList =new ArrayList();
		HashMap fieldMap = new HashMap();//key=//当前指标值：所在cell列数 value=FieldItem
		HashMap fieldIdex = new HashMap();
		StringBuffer keysb=new StringBuffer();
		ArrayList keyList =new ArrayList();
		StringBuffer a0100sb = new StringBuffer();
		HashMap key2num=new HashMap();//对应excel多少行
		Object[] maps = { fieldMap, valueList ,keysb,keyList,a0100sb,key2num};
		Workbook owb = null;
		Sheet osheet = null;
		InputStream ism = null;
		try {
			ism = file.getInputStream();
			owb = WorkbookFactory.create(ism);
			osheet = owb.getSheetAt(0);
			Row orow = osheet.getRow(0);
			if (orow == null) 
			    throw new GeneralException("", "请选择下载的excel模板进行导 入！", "", "");
			
			int cols = orow.getPhysicalNumberOfCells();
			int rows = osheet.getPhysicalNumberOfRows();
			for (int c = 0; c < cols; c++) {
				String itemid = "";
				Cell cell = orow.getCell(c);
				if (cell != null) {
					itemid = cell.getCellComment().getString().getString().toLowerCase();
				}
				String t[]=itemid.split("`");
				itemid=t[0];
				if("".equals(itemid))
					break;
				FieldItem item = DataDictionary.getFieldItem(itemid, "r25");
				if(item==null){
					continue;
				}
				//当前指标值：所在cell列数
				fieldIdex.put(itemid, new Integer(c));
				fieldMap.put(itemid, item);
				keyList.add(itemid);
			}
			
			if(fieldMap == null || fieldMap.size() < 1 || !fieldMap.containsKey("r2502"))
			    throw new GeneralException("", "请选择下载的excel模板进行导 入！", "", "");
			
			cols = keyList.size();
			String value="";
			double dvalue=0;
			DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			for (int j = 1; j < rows; j++) {
				orow = osheet.getRow(j);
				if (orow != null) {
					HashMap valueMap = new HashMap();//key=指标itemid value=值
					String primarykeyValue="";
					//ll:
					for(int n=0;n<cols;n++){
						String key=(String)keyList.get(n);
						FieldItem item= (FieldItem)fieldMap.get(key);
						int idex = ((Integer)fieldIdex.get(key)).intValue();
						Cell cell = orow.getCell(idex);
						if (cell != null) {
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC: {
								if("D".equals(item.getItemtype())){
									Date d = cell.getDateCellValue();   
								    try{
								    	value=formater.format(d);
								    	valueMap.put(key, value);
								    }catch(Exception e){
								    	ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
										if(sb!=null){
											int no=sb.size();
											sb.add((no+1)+".&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
													.getProperty("workbench.info.import.error.datetype")+cell.getNumericCellValue());
										}else{
											sb= new ArrayList();
											sb.add("1.&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
													.getProperty("workbench.info.import.error.datetype")+cell.getNumericCellValue());
										}
										value="```";
										msgMap.put(primarykeyValue, sb);
								    }
								    break;
								}else{
									dvalue=cell.getNumericCellValue();
									String str = NumberFormat.getNumberInstance().format(dvalue);
								    while(str.indexOf(",")>-1){
								        str = str.substring(0,str.indexOf(","))+str.substring(str.indexOf(",")+1);
								    }
									Pattern p= Pattern.compile("[+-]?[\\d.]*");
									Matcher m = p.matcher(String.valueOf(str));
									if(!m.matches()){
										ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
										if(sb!=null){
											int no=sb.size();
											sb.add((no+1)+".&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
													.getProperty("workbench.info.import.error.inttype")+str);
										}else{
											sb= new ArrayList();
											sb.add("1.&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
													.getProperty("workbench.info.import.error.inttype")+str);
										}
										msgMap.put(primarykeyValue, sb);
										valueMap.put(key, "```");
									}else{
										value=String.valueOf((str));
										if("N".equals(item.getItemtype())){
											int dw=item.getDecimalwidth();
											if(dw==0){
												if(value.indexOf('.')!=-1)
													value=value.substring(0,value.indexOf('.'));
											}else{
												if(value.indexOf('.')!=-1){
													String dec=value.substring(value.indexOf('.')+1);
													if(dec.length()>dw)
														value=value.substring(0,value.indexOf('.')+dw+1);
												}
											}
											value=value.replaceAll("\\+", "");
										}
										if(value.length()>9)
											valueMap.put(key, "```");
										else
											valueMap.put(key, value);
									}
									break;
								}
							}
							case Cell.CELL_TYPE_STRING: {
								value=cell.getStringCellValue().trim();
								if(n==0){
									primarykeyValue=cell.getStringCellValue().trim();
									if(value==null||"".equals(value)){//主键为空就不提取此行记录
										break;
									}
									keysb.append(primarykeyValue+"','");
								}
								if("N".equals(item.getItemtype())){
									Pattern p= Pattern.compile("[+-]?[\\d.]*");
									Matcher m = p.matcher(value);
									if(!m.matches()){
										ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
										if(sb!=null){
											int no=sb.size();
											sb.add((no+1)+".&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
													.getProperty("workbench.info.import.error.inttype")+value);
										}else{
											sb= new ArrayList();
											sb.add("1.&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
													.getProperty("workbench.info.import.error.inttype")+value);
										}
										value="```";
										msgMap.put(primarykeyValue, sb);
									}else{
										if("N".equals(item.getItemtype())){
											int dw=item.getDecimalwidth();
											if(dw==0){
												if(value.indexOf('.')!=-1)
													value=value.substring(0,value.indexOf('.'));
											}else{
												if(value.indexOf('.')!=-1){
													String dec=value.substring(value.indexOf('.')+1);
													if(dec.length()>dw)
														value=value.substring(0,value.indexOf('.')+dw+1);
												}
											}
											value=value.replaceAll("\\+", "");
										}
										if(value.length()>9)
											value="```";
									}
								}
								if("D".equals(item.getItemtype())){
									if(!"".equals(value)){
										//Pattern p= Pattern.compile("^(\\d{1,4})[-.](\\d{1,2})[-.](\\d{1,2})$");
										//Matcher m = p.matcher(value);
										String tmp=checkdate(value);
										if("false".equals(tmp)){
											ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
											if(sb!=null){
												int no=sb.size();
												sb.add((no+1)+".&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
														.getProperty("workbench.info.import.error.datetype")+value);
											}else{
												sb= new ArrayList();
												sb.add("1.&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
														.getProperty("workbench.info.import.error.datetype")+value);
											}
											value="```";
											msgMap.put(primarykeyValue, sb);
										}else{
											value=tmp;
										}
										//value=value.replaceAll("\\.", "-");
									}
								}
								valueMap.put(key, value);
								break;
							}
							default:
							}
						}
					}
					if(!"".equals(primarykeyValue)){//主键为空就不提取此行记录
						/*if(fieldsetid.equalsIgnoreCase("A01")&&!this.privM.equals("admin")){
							String p=(String)valueMap.get(this.privM);
							if(p!=null&&!"".equals(p)){
								if(p.toUpperCase().indexOf(this.privMv)!=-1){
									valueList.add(valueMap);
								}
							}else{
								valueList.add(valueMap);
							}
						}else{*/
							valueList.add(valueMap);
						//}
							key2num.put(primarykeyValue, ""+(j+1));
					}else{
						for(Iterator i=valueMap.keySet().iterator();i.hasNext();){
							String key = (String)i.next();
							if(valueMap.get(key)!=null&&valueMap.get(key).toString().length()>0){
								counts[1]=counts[1]+1;
								ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
								if(sb!=null){
									int no=sb.size();
									sb.add((no+1)+".&nbsp;"+ResourceFactory.getProperty("workbench.info.import.error.null"));
								}else{
									sb= new ArrayList();
									sb.add("1.&nbsp;"+ResourceFactory.getProperty("workbench.info.import.error.null"));
									msgMap.put(primarykeyValue, sb);
								}
								break;
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
		}finally{
			PubFunc.closeIoResource(ism);
			PubFunc.closeResource(owb);
		}
		return maps;
	}
	
	private String checkdate(String str){
		boolean flag = true;
		//String str="2010年";
		if(str.length()<4){
			return "false";
		}
		if(str.length()==4){
			Pattern p= Pattern.compile("^(\\d{4})$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				return str+"-01-01";
			}else{
				return "false";
			}
		}
		if(str.length()<6){
			Pattern p= Pattern.compile("^(\\d{4})年$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				return str.replace("年", "-")+"01-01";
			}else{
				return "false";
			}
		}
		if(str.length()==7){
			if(str.indexOf("月")!=-1){
				Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]$");
				Matcher m = p.matcher(str);
				if(m.matches()){
					if(str.indexOf("月")!=-1){
						return str.replace("年", "-").replace(".", "-").replace("月", "-")+"01";
					}else{
						return str.replace("年", "-").replace(".", "-")+"-01";
					}
				}else{
					return "false";
				}
			}else{
				Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])$");
				Matcher m = p.matcher(str);
				if(m.matches()){
					return str.replace("年", "-").replace(".", "-")+"-01";
				}else{
					return "false";
				}
			}
		}
		if(str.length()<8){//2010年3  2010年3月
			Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]*$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				if(str.indexOf("月")!=-1){
					return str.replace("年", "-").replace(".", "-").replace("月", "-")+"01";
				}else{
					return str.replace("年", "-").replace(".", "-")+"-01";
				}
			}else{
				return "false";
			}
		}
		if(str.length()==8){//2010年3  2010年3月1
			Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])*$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				str =str.replace("年", "-").replace(".", "-").replace("月", "-");
				if(str.lastIndexOf("-")==str.length()){
					if(str.length()<10){
						return str+"01";
					}
				}else{
					String[] temps=str.split("-");
					if(temps.length>2){
						String t="false";
						if(temps[0].length()>0&&temps[1].length()>0&&temps[2].length()>0){
							int year = Integer.parseInt(temps[0]);
							int month=Integer.parseInt(temps[1]);
							int day=Integer.parseInt(temps[2]);
							switch(month){
							case 1:
							case 3:
							case 5:
							case 7:
							case 8:
							case 10:
							case 12:
							{
								if(1<=day&&day<=31){
									t=str;
								}
								break;
							}
							case 4:
							case 6:
							case 9:
							case 11:
							{
								if(1<=day&&day<=30){
									t=str;
								}
								break;
							}
							case 2:
							{
								 if(isLeapYear(year)){
									 if(1<=day&&day<=29){
											t=str;
									}
								 }else{
									 if(1<=day&&day<=28){
											t=str;
									}
								 }
								 break;
							}
								
							}
						}
						return t;
					}else{
						return "false";
					}
					
					
				}
			}else{
				return "false";
			}
		}
		Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[日]*$");
		Matcher m = p.matcher(str);
		if(m.matches()){
			String temp=str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", "");
			String[] temps = temp.split("-");
			String t="false";
			if(temps[0].length()>0&&temps[1].length()>0&&temps[2].length()>0){
				int year = Integer.parseInt(temps[0]);
				int month=Integer.parseInt(temps[1]);
				int day=Integer.parseInt(temps[2]);
				switch(month){
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12:
				{
					if(1<=day&&day<=31){
						t=temp;
					}
					break;
				}
				case 4:
				case 6:
				case 9:
				case 11:
				{
					if(1<=day&&day<=30){
						t=temp;
					}
					break;
				}
				case 2:
				{
					 if(isLeapYear(year)){
						 if(1<=day&&day<=29){
								t=temp;
						}
					 }else{
						 if(1<=day&&day<=28){
								t=temp;
						}
					 }
					 break;
				}
					
				}
			}
			return t;
		}else{
			return "false";
		}
	}

	private String splitString(String source, int len)
	  {
	    byte[] bytes = source.getBytes();
	    int bytelen = bytes.length;
	    int j = 0;
	    int rlen = 0;
	    if (bytelen <= len)
	      return source;

	    for (int i = 0; i < len; ++i)
	    {
	      if (bytes[i] < 0)
	        ++j;
	    }
	    if (j % 2 == 1)
	      rlen = len - 1;
	    else
	      rlen = len;
	    byte[] target = new byte[rlen];
	    System.arraycopy(bytes, 0, target, 0, rlen);
	    String dd = new String(target);
	    return dd;
	  }
	
	private void initPriv(){
		if(this.userView.isSuper_admin()){
			this.privM="admin";
			return;
		}
		String codeall=this.userView.getUnitIdByBusi("6");
		codeall=PubFunc.getTopOrgDept(codeall);
		String tmp[]=codeall.split("`");
		StringBuffer codevalue=new StringBuffer();
		for(int i=tmp.length-1;i>=0;i--){
			String t = tmp[i];
			if(t.indexOf("UN")!=-1){
				codevalue.append("`"+t.substring(2));
			}
		}
		this.privMv=codevalue.substring(1).toUpperCase();
		//System.out.println(privMv);
		/*if(userView.getStatus()==0){
				String codeall = userView.getUnit_id();
				if(codeall!=null&&codeall.length()>2){
					codeall=PubFunc.getTopOrgDept(codeall);
					if("UN`".equalsIgnoreCase(codeall)){
						this.privM="admin";
					}else{
						String tmp[]=codeall.split("`");
						StringBuffer codevalue=new StringBuffer();
						for(int i=tmp.length-1;i>=0;i--){
							String t = tmp[i];
							if(t.indexOf("UN")!=-1){
								codevalue.append("`"+t.substring(2));
							}
						}
						this.privMv=codevalue.substring(1).toUpperCase();
					}
				}else{
					this.privMv=this.userView.getManagePrivCodeValue().toUpperCase();
				}
		}else{
			this.privMv=this.userView.getManagePrivCodeValue().toUpperCase();
		}*/
	}
	
	private void doOrgCheck(ArrayList valueList,ContentDAO dao)throws Exception{
		Pattern p = Pattern.compile("[A-Z0-9]*");
		for(int m=0;m<valueList.size();m++){
			HashMap valuemap=(HashMap)valueList.get(m);
			String value="";
			String b0110=(String)valuemap.get("b0110");
			if(b0110!=null&&b0110.endsWith(")")){
				value=b0110.substring(b0110.lastIndexOf("(")+1, b0110.lastIndexOf(")"));
				Matcher ma=p.matcher(value);
				if(ma.matches()){
					if(AdminCode.getCodeName("UN", value).length()>0){
						valuemap.put("b0110", value);
						b0110=value;
					}else{
						value=b0110.substring(0,b0110.lastIndexOf("("));
						valuemap.put("b0110", value);
						b0110=value;
					}
				}
			}
			String e0122=(String)valuemap.get("e0122");
			if(e0122!=null&&e0122.endsWith(")")){
				value=e0122.substring(e0122.lastIndexOf("(")+1, e0122.lastIndexOf(")"));
				Matcher ma=p.matcher(value);
				if(ma.matches()){
					if(AdminCode.getCodeName("UM", value).length()>0){
						valuemap.put("e0122", value);
						e0122=value;
					}else{
						value=b0110.substring(0,b0110.lastIndexOf("("));
						valuemap.put("e0122", value);
						e0122=value;
					}
				}
			}
			
			if(b0110!=null&&!"".equals(b0110)){
				if(e0122!=null&&!"".equals(e0122)){
					if(checkUNUM(b0110,e0122,dao)){
						valuemap.put("e0122", "");
					}
				}
			}else{
				valuemap.put("e0122", "");
			}
		}
	}
	
	private void doUNUMKCheck(ArrayList valueList,HashMap fieldMap,ContentDAO dao)throws Exception{
		Pattern p = Pattern.compile("[A-Z0-9]*");
		for(int m=0;m<valueList.size();m++){
			HashMap valuemap=(HashMap)valueList.get(m);
			String value="";
			for(Iterator i=valuemap.keySet().iterator();i.hasNext();){
				String itemid = (String)i.next();
				if(!"b0110".equalsIgnoreCase(itemid)&&!"e0122".equalsIgnoreCase(itemid)&&!"e01a1".equalsIgnoreCase(itemid)){
					FieldItem item=(FieldItem)fieldMap.get(itemid);
					if(item!=null){
						if("UN".equalsIgnoreCase(item.getCodesetid())||"UM".equalsIgnoreCase(item.getCodesetid())||"@K".equalsIgnoreCase(item.getCodesetid())){
							String b0110=(String)valuemap.get(itemid);
							if(b0110!=null&&b0110.endsWith(")")){
								value=b0110.substring(b0110.lastIndexOf("(")+1, b0110.lastIndexOf(")"));
								Matcher ma=p.matcher(value);
								if(ma.matches()){
									if(AdminCode.getCodeName(item.getCodesetid(), value).length()>0)
										valuemap.put(itemid, value);
									else{
										value=b0110.substring(0,b0110.lastIndexOf("("));
										valuemap.put(itemid, value);
									}
								}
							}
						}
					}
				}
			}
		}	
	}
	
	private boolean checkUNUM(String b0110,String e0122,ContentDAO dao)throws Exception{
		boolean flag=false;
		Pattern p = Pattern.compile("[A-Z0-9]*");
		Matcher ma=p.matcher(b0110);//支持直接输入机构编码，可解决机构名重复的问题
		String unCodeitemid="";
		String umCodeitemid="";
		if(ma.matches()){
			unCodeitemid=b0110;
		}else{
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='"+b0110+"' and codesetid='UN'");
			if(this.frecset.next()){
				unCodeitemid=this.frecset.getString("codeitemid");
			}else{
				flag=true;
			}
		}
		ma=p.matcher(e0122);
		if(ma.matches()){
			umCodeitemid=e0122;
		}else{
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='"+e0122+"' and codesetid='UM'");
			if(this.frecset.next()){
				umCodeitemid=this.frecset.getString("codeitemid");
			}else{
				flag=true;
			}
		}
		if(!"".equals(unCodeitemid)&&!"".equals(umCodeitemid)){
			flag=this.dochildchecknm(unCodeitemid, umCodeitemid,dao);
		}
		return flag;
	}
	private boolean checkUMKK(String e0122,String e01a1,ContentDAO dao)throws Exception{
		boolean flag=false;
		Pattern p = Pattern.compile("[A-Z0-9]*");
		Matcher ma=p.matcher(e0122);//支持直接输入机构编码，可解决机构名重复的问题
		String kkCodeitemid="";
		String umCodeitemid="";
		if(ma.matches()){
			umCodeitemid=e0122;
		}else{
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='"+e0122+"' and codesetid='UM'");
			if(this.frecset.next()){
				umCodeitemid=this.frecset.getString("codeitemid");
			}else{
				flag=true;
			}
		}
		ma=p.matcher(e0122);
		if(ma.matches()){
			kkCodeitemid=e01a1;
		}else{
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='"+e01a1+"' and codesetid='@K'");
			if(this.frecset.next()){
				kkCodeitemid=this.frecset.getString("codeitemid");
			}else{
				flag=true;
			}
		}
		if(!"".equals(umCodeitemid)&&!"".equals(kkCodeitemid)){
			flag=this.dochildcheckmk(umCodeitemid, kkCodeitemid,dao);
		}
		return flag;
	}
	
	private boolean dochildchecknm(String unCodeitemid,String umCodeitemid,ContentDAO dao)throws Exception{
		RowSet rs=null;
		boolean flag=true;
		String sql = "select codeitemid from organization where codesetid<>'@K' and parentid='"+unCodeitemid+"' and parentid<>codeitemid";
		try{
			rs = dao.search(sql);
			String codeitemid="";
			while(rs.next()){
				codeitemid=rs.getString("codeitemid");
				if(umCodeitemid.equalsIgnoreCase(codeitemid)){
					flag=false;
					break;
				}else if(umCodeitemid.indexOf(codeitemid)!=-1){
					flag=this.dochildchecknm(codeitemid, umCodeitemid, dao);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				rs.close();
			}
		}
		return flag;
	}
	private boolean dochildcheckmk(String umCodeitemid,String kkCodeitemid,ContentDAO dao)throws Exception {
		RowSet rs=null;
		boolean flag=true;
		String sql = "select codeitemid from organization where codesetid<>'UN' and parentid='"+umCodeitemid+"' and parentid<>codeitemid";
		try{
			rs = dao.search(sql);
			String codeitemid="";
			while(rs.next()){
				codeitemid=rs.getString("codeitemid");
				if(kkCodeitemid.equalsIgnoreCase(codeitemid)){
					flag=false;
					return flag;
				}else if(kkCodeitemid.indexOf(codeitemid)!=-1){
					flag=this.dochildchecknm(codeitemid, kkCodeitemid, dao);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				rs.close();
			}
		}
		return flag;
	}
	
	/**
	 *  闰年的条件是：
	 *	① 能被4整除，但不能被100整除；
	 *	② 能被100整除，又能被400整除。
	 * @param year
	 * @return
	 */
	private boolean isLeapYear(int year){
		boolean t=false;
		if(year%4==0){
			   if(year%100!=0){
				   t=true;
			   }else if(year%400==0){
				   t=true;
			   }
		  }
		return t;
	}
	
	
}
