package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.CheckTotalBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InputExcelTrans extends IBusiness {
	public void execute() throws GeneralException {
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String saveflag=(String)reqhm.get("saveflag");
		saveflag=saveflag!=null?saveflag:"";
		reqhm.remove("saveflag");
		
		String checkClose = "";
		String checkflag = "ok";
		HashMap unitcode=new HashMap();
		HashMap unitcode1=new HashMap();
		if("save".equalsIgnoreCase(saveflag)){
			checkClose = "close";
			String fieldsetid="";
			FormFile file=(FormFile)getFormHM().get("picturefile");
			InputStream stream = null;
//			HSSFWorkbook wb = null;
//			HSSFSheet sheet = null;
//			HSSFRow row= null;
//			HSSFCell cell= null;			
			
			Workbook wb = null;
			Sheet sheet = null;			
			Row row= null;
			Cell cell= null;
			
//			HSSFRichTextString hscomment = null;
			RichTextString hscomment = null;
			FieldItem fielditem = null;
//			HSSFComment comment = null;
			Comment comment = null;
//			HSSFRichTextString hsts = null;
			RichTextString hsts = null;
			StringBuffer sqlstr = new StringBuffer();
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap map =bo.getValuesMap();
			if(map==null){
				checkflag = "薪资总额参数未定义!";
				checkClose = "alert";
				throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
			}

			if(fieldsetid.trim().length()<1){
				//ArrayList setidlist = gpm.elementName("/Params/Gz_amounts","setid");
				fieldsetid = ((String)map.get("setid")).length()>0?(String)map.get("setid"):"";
				if(fieldsetid==null){
					checkflag = "薪资总额参数未定义!";
					checkClose = "alert";
					throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
				}
			}
			String ctrl_peroid=(String)map.get("ctrl_peroid");//年月控制标识=1按年，=0按月=2按季度
			ctrl_peroid=ctrl_peroid!=null&&ctrl_peroid.trim().length()>0?ctrl_peroid:"0";

			String spflagid = ((String)map.get("sp_flag")).length()>0?(String)map.get("sp_flag"):"";
			spflagid=spflagid!=null?spflagid:"";

			String ctrl_type=(String)map.get("ctrl_type");//是否控制到部门，０控制，１不控制
			ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
			String ctrl_by_level="0";
			String fc_flag=(String)map.get("fc_flag");
			if(map.get("ctrl_by_level")!=null&&!"".equals((String)map.get("ctrl_by_level")))
			{
				ctrl_by_level=(String)map.get("ctrl_by_level");
			}
			ArrayList fieldlist = new ArrayList();
			ArrayList valuelist = new ArrayList();
			boolean cellflag = true;
			boolean rowflag = true;
			CheckTotalBo chbo = null;
			String getyear = "";
			StringBuffer whereSQL = new StringBuffer("");
			ArrayList itemlist = new ArrayList();
			String ctrl_period="";
			try {
				/* 安全问题 文件上传 薪资总额 导入数据 xiaoyun 2014-9-13 start */
				boolean isOk = FileTypeUtil.isFileTypeEqual(file);
				if(!isOk) {
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
				}
				/* 安全问题 文件上传 薪资总额 导入数据 xiaoyun 2014-9-13 end */
				stream = file.getInputStream();
//				wb=new HSSFWorkbook(stream); 
				wb=WorkbookFactory.create(stream);
				sheet=wb.getSheetAt(0); 
				row=sheet.getRow(0);
				/* 解决 薪资总额，下载模板最前面一列为空的问题后导入数据的问题 xiaoyun 2014-10-25 start */
				int cellNum=0;
				/* 解决 薪资总额，下载模板最前面一列为空的问题后导入数据的问题 xiaoyun 2014-10-25 end */
				int rowNum=1;
				
				
				sqlstr.append("update ");
				sqlstr.append(fieldsetid);
				sqlstr.append(" set ");

				while(cellflag){
					if(row==null){
						cellflag = false;
						break;
					}
					cell = row.getCell((short)cellNum);//获取单元格子 
					if(cell==null){
						cellflag = false;
						break;
					}
					hsts = cell.getRichStringCellValue(); 	
					String msg=hsts.getString(); 
					if(msg==null||msg.trim().length()<1){
						cellflag = false;
						break;
					}
					comment = cell.getCellComment();
					if(comment!=null){
						hscomment = comment.getString();
						String itemid = hscomment.getString();
						boolean b=true;						
						fielditem = DataDictionary.getFieldItem(itemid);
						if (fielditem!=null){
						    if ("D".equalsIgnoreCase(fielditem.getItemtype())){//日期型数据 不更新 批量更新不支持 bug7059 wangrd 2015-01-30
						        if(!fielditem.getItemid().equalsIgnoreCase(fieldsetid+"Z0")){
						            b=false;
						        }
						    }
						    else if ("M".equalsIgnoreCase(fielditem.getItemtype())){
						        b=false;
						    }
						}
						if (b){
						    if("aaaa".equalsIgnoreCase(itemid)){
						        itemid = fieldsetid+"Z0";
						        ctrl_period=this.getperiod(msg);
						    }
						    if(!itemid.equalsIgnoreCase(fieldsetid+"Z0")&&!"B0110".equalsIgnoreCase(itemid)){
						        sqlstr.append(itemid);
						        sqlstr.append("=?,");
						        itemlist.add(itemid);
						    }
						    fieldlist.add(itemid);
						}
					}
					cellNum++;
				}
				if(!ctrl_period.equalsIgnoreCase(ctrl_peroid)){
					checkflag = "导入数据年月标识类型错误！请导入正确年月标识数据！";
					throw GeneralExceptionHandler.Handle(new Exception(checkflag));
				}
				String sql = sqlstr.toString().substring(0,sqlstr.toString().length()-1);
				sqlstr.setLength(0);
				sqlstr.append(sql);
				sqlstr.append(" where ");
				sqlstr.append("B0110=? and ");
				
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					sqlstr.append("to_char("+fieldsetid+"Z0,'yyyy-mm-dd')=?");
				else
		    		sqlstr.append(fieldsetid+"Z0=?");
				if(fc_flag!=null&&fc_flag.length()!=0){
					sqlstr.append(" and ");
					sqlstr.append(fc_flag);
					sqlstr.append("='2'");				
				}
				itemlist.add("B0110");
				itemlist.add(fieldsetid+"Z0");
				chbo = new CheckTotalBo(this.frameconn,this.userView,itemlist);
				if(fc_flag!=null&&fc_flag.length()!=0){
					chbo.setFc_flag(fc_flag);
					
				}
				while(rowflag){
					row=sheet.getRow(rowNum);
					String B0110 = "";
					String z0 = "";
					if(row==null){
						rowflag = false;
						break;
					}
					ArrayList volist = new ArrayList();
					/* 解决 薪资总额，下载模板最前面一列为空的问题后导入数据的问题 xiaoyun 2014-10-25 start */
					int a=0;
					/* 解决 薪资总额，下载模板最前面一列为空的问题后导入数据的问题 xiaoyun 2014-10-25 end */
					if(fieldlist.size()<1){
						rowNum++;
						continue;
					}
					for(int i=0;i<fieldlist.size();i++){
						String itemid = (String)fieldlist.get(i);
						cell = row.getCell((short)a);//获取单元格子 
						if(cell==null){
							break;
						}
						fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null){
							
							if("A".equalsIgnoreCase(fielditem.getItemtype())){
								String values = "";
								if(fielditem.isCode()){
									comment = cell.getCellComment();
									if(comment!=null){
										hscomment = comment.getString();
										values = hscomment.getString();
									}
									if("B0110".equalsIgnoreCase(fielditem.getItemid())){
										B0110 = values;
										a++;
										continue;
									}
								}else{
									hsts = cell.getRichStringCellValue(); 
									values = hsts.getString();
								}
								if ("".equals(values)){
                                    values=null;  
                                }
								volist.add(values);
							}else if("N".equalsIgnoreCase(fielditem.getItemtype())){
								if(fielditem.getDecimalwidth()>0){
									double dvalues = 0;
									try{
										dvalues = cell.getNumericCellValue();
									}catch(Exception e){
										dvalues = 0;
									}
									volist.add(dvalues+"");
								}else{
									String dvalues = "0";
									try{
										String values = cell.getNumericCellValue()+"";
										if(values.indexOf("E")!=-1)
										{
											String temp=values.substring(0,values.indexOf("E"));
											String aa=values.substring(values.indexOf("E")+1);
											//System.out.println((new BigDecimal(Math.pow(10.0,Double.parseDouble(aa.trim()))).toString()));
											values=(new BigDecimal(Math.pow(10,Integer.parseInt(aa.trim()))).multiply(new BigDecimal(temp))).toString();//         Double.parseDouble(temp)*Math.pow(10,Double.parseDouble(aa));
											//values=tt+"";
										}
										
										values=values!=null&&values.trim().length()>0?values:"0.0";
										dvalues = values.substring(0,values.indexOf("."));
									}catch(Exception e){
										dvalues = "0";
									}
									volist.add(dvalues+"");
								}
							}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
								hsts = cell.getRichStringCellValue(); 
								String values = hsts.getString();
								values=values!=null?values:"";
								if(fielditem.getItemid().equalsIgnoreCase(fieldsetid+"Z0")){
									if(values.trim().length()>3){
										values = WeekUtils.dateToComplete(values);
									}
									z0 = values;
									comment = cell.getCellComment();
									if(comment!=null){
										hscomment = comment.getString();
										getyear = hscomment.getString();
									}
									a++;
									continue;
								}
								if ("".equals(values)){
								    values=null;  
								}
								volist.add(values);
							}else{
								hsts = cell.getRichStringCellValue(); 
								String values = hsts.getString();
								if ("".equals(values)){
                                    values=null;  
                                }
								volist.add(values);
							}
						}else{
							hsts = cell.getRichStringCellValue(); 
							String values = hsts.getString();
							volist.add(values);
						}
						a++;
						
					}
					unitcode1.put(B0110, "1");
					unitcode.put(B0110, "1");
					volist.add(B0110);
					volist.add(z0);
					if("0".equalsIgnoreCase(ctrl_period)){
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							whereSQL.append(" or (to_char("+fieldsetid+"Z0,'yyyy-mm-dd')='"+z0+"' and b0110='"+B0110+"')");
						else
					    	whereSQL.append(" or (" +fieldsetid+"z0='"+z0+"' and b0110='"+B0110+"' )");
					}
					if("1".equalsIgnoreCase(ctrl_period)){
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							whereSQL.append(" or ("+Sql_switcher.year(fieldsetid+"Z0")+"='"+getyear+"' and b0110='"+B0110+"')");
						else
					    	whereSQL.append(" or (" +Sql_switcher.year(fieldsetid+"Z0")+"='"+getyear+"' and b0110='"+B0110+"' )");
					}
					if("2".equalsIgnoreCase(ctrl_period)){
						ArrayList list=this.time(z0, ctrl_period);
						String ss=(String)list.get(0);
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							whereSQL.append(" or ("+Sql_switcher.year(fieldsetid+"Z0")+"='"+getyear+"' and  "+Sql_switcher.month(fieldsetid+"Z0")+"in("+ss+") and b0110='"+B0110+"')");
						else
					    	whereSQL.append(" or (" +Sql_switcher.year(fieldsetid+"Z0")+"='"+getyear+ "' and  "+Sql_switcher.month(fieldsetid+"Z0")+"in("+ss+") and b0110='"+B0110+"' )");
					}
					valuelist.add(volist);
					rowNum++;
				}
			}catch (Exception e) {
				/* 安全问题 文件上传 薪资总额 导入数据 xiaoyun 2014-9-13 start */
				if(StringUtils.equals("com.hrms.struts.exception.GeneralException", e.getClass().getName())) {
					if(((GeneralException)e).getErrorDescription().equals(ResourceFactory.getProperty("error.common.upload.invalid"))) {
						throw GeneralExceptionHandler.Handle(e);
					} else {
						checkflag = "导入的数据错误,请确认后再导入!";
						checkClose = "alert";
					}
				}
				/* 安全问题 文件上传 薪资总额 导入数据 xiaoyun 2014-9-13 end */
			}finally{
				PubFunc.closeResource(wb);
				PubFunc.closeResource(stream);
			}
			
			try {
				
				if(valuelist.size()<1){
					checkflag = "导入的数据错误,请确认后再导入!";
					checkClose = "alert";
				}
				if("ok".equalsIgnoreCase(checkflag)){
					chbo.setUnitcode(unitcode);
					chbo.getParent(unitcode1);
					valuelist = chbo.ctrlToDate(ctrl_peroid, valuelist, itemlist, getyear);
					ContentDAO dao = new ContentDAO(this.frameconn);
					if("ok".equalsIgnoreCase(checkflag)){
						if(chbo.checkSpflag(dao, fieldsetid, valuelist, spflagid)){
							checkflag = "数据为报批、批准、发布状态,不能执行导入操作!";
							checkClose = "alert";
						}
						if("ok".equalsIgnoreCase(checkflag)){
							if("1".equals(ctrl_by_level))
							     checkflag = chbo.checkTatal(valuelist,fieldsetid,ctrl_peroid,ctrl_type);
							if("ok".equalsIgnoreCase(checkflag)){
								dao.batchUpdate(sqlstr.toString(), valuelist);
								String un = "ctrl_item";
								ArrayList dataList = new ArrayList();
								/**计划总额，实发，剩余指标*/
								dataList=(ArrayList) map.get(un.toLowerCase());
								
								for(int j=0;j<dataList.size();j++)
								{
						    		LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
									String planitem = (String)bean.get("planitem");
									String realitem = (String)bean.get("realitem");
									String balanceitem = (String)bean.get("balanceitem");
									StringBuffer sql = new StringBuffer("");
									sql.append("update "+fieldsetid+" set "+balanceitem+"=");
									sql.append(Sql_switcher.isnull(planitem, "0")+"-"+Sql_switcher.isnull(realitem, "0"));
									if(fc_flag!=null&&fc_flag.length()!=0){
										sql.append(" where ("+whereSQL.toString().substring(3)+") and  "+fc_flag+"=2");
									}else{
										sql.append(" where "+whereSQL.toString().substring(3));
									}
									
									dao.update(sql.toString());
								}
							}else{
								checkClose = "alert";
							}
						}
					}
				}
			} catch (SQLException e) {

			}
			
		}
		this.getFormHM().put("checkClose", checkClose);
		this.getFormHM().put("checkflag", checkflag);
	}
	public String getperiod(String value){
		String period="";
		if("年月标识".equalsIgnoreCase(value)){
			period="0";
		}
		if("季度".equalsIgnoreCase(value)){
			period="2";
		}
		if("年份".equalsIgnoreCase(value)){
			period="1";
		}
		return period;
	}
	private ArrayList time(String value,String period){
		ArrayList time=new ArrayList();
		if("2".equalsIgnoreCase(period)){
			if("第一季度".equalsIgnoreCase(value)){
				String ss="1,2,3";
				time.add(ss);
			}if("第二季度".equalsIgnoreCase(value)){
				String ss="4,5,6";
				time.add(ss);
			}
			if("第三季度".equalsIgnoreCase(value)){
				String ss="7,8,9";
				time.add(ss);
			}if("第四季度".equalsIgnoreCase(value)){
				String ss="10,11,12";
				time.add(ss);
			}
		}
		return time;
	}
}
