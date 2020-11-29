package com.hjsj.hrms.utils.components.subsetview.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LoadSetItemDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String flag="false";
			
			String setName=(String)this.formHM.get("setName");
			String nbase=(String)this.formHM.get("nbase");
			String currentObject=this.formHM.get("currentObject")==null?"":(String)this.formHM.get("currentObject");
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql="";
			ArrayList valueList=new ArrayList();
//			System.out.println(PubFunc.encrypt("00000049"));
//			currentObject=PubFunc.encrypt("00000049");
//			//TODO 需要返回原因到前台吗？
//			if("".equals(currentObject)||"".equals(setName)){
//				this.formHM.put("flag", flag);
//				return;
//			}
			currentObject=PubFunc.decrypt(currentObject);
			
//			currentObject="010101";
			
			ArrayList fieldList=new ArrayList();
			ArrayList fileList=new ArrayList();
			//查询出所有权限范围内的指标
			ArrayList fieldprivlist=this.userView.getPrivFieldList(setName);
			
			//是否支持子集附件属性
			String showfile="0";
			if(setName.toUpperCase().startsWith("A")){
				sql="select multimedia_file_flag mff from fieldSet where fieldSetId=?";
				valueList.add(setName);
				this.frowset=dao.search(sql,valueList);
				if(this.frowset.next())
					showfile=this.frowset.getString("mff");
				this.formHM.put("showfile", showfile);
			}
			
			for(Object obj:fieldprivlist){
				FieldItem item=(FieldItem)obj;
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("fieldsetid", item.getFieldsetid());
				bean.set("itemid", item.getItemid());
				bean.set("itemdesc", item.getItemdesc());
				bean.set("itemtype", item.getItemtype());
				bean.set("itemlength", item.getItemlength());
				bean.set("codesetid", item.getCodesetid());
				bean.set("allowblank", item.isFillable());
				bean.set("demicallength",item.getDecimalwidth());	
				bean.set("value", "");
				fieldList.add(bean);
			}
			//如果为修改，查询出指标的值
			if(this.formHM.get("dataIndex")!=null&&!"".equals(this.formHM.get("dataIndex").toString())){
				String dataIndex=this.formHM.get("dataIndex").toString();
				sql="";
				valueList=new ArrayList();
				valueList.add(currentObject);
				
				String columns="";
				for(Object obj:fieldList){
					LazyDynaBean bean=(LazyDynaBean)obj;
					columns+=bean.get("itemid")+",";
				}
				if(!"".equals(columns)){
					columns=columns.substring(0, columns.length()-1);
				}
				if(setName.toUpperCase().startsWith("A")){
					sql="select "+columns+" from "+nbase+setName+" where A0100 = ? ";
				}else if(setName.toUpperCase().startsWith("B")){
					sql="select "+columns+" from "+setName+" where B0110 = ? ";
				}else if(setName.toUpperCase().startsWith("H")){
					sql="select "+columns+" from "+setName+" where H0100 = ? ";
				}else if(setName.toUpperCase().startsWith("K")){
					sql="select "+columns+" from "+setName+" where E01A1 = ? ";
				}
				//主集没有I9999
				if(!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)&&!"H01".equals(setName)){
					sql+="and I9999 = ?";
					valueList.add(dataIndex);
				}
				
				this.frowset = dao.search(sql,valueList);
				if(this.frowset.next()){
					String itemid="";
					String value="";
					ArrayList newList=new ArrayList();
					for(Object obj:fieldList){
						LazyDynaBean bean=(LazyDynaBean)obj;
						itemid=bean.get("itemid").toString();
//						value=this.frowset.getString(itemid);
						if("A".equals(bean.get("itemtype").toString())){
							value=this.frowset.getString(itemid);
							if(!"0".equals(bean.get("codesetid").toString())){
								if("UN".equals(bean.get("codesetid").toString())||"UM".equals(bean.get("codesetid").toString())||"@K".equals(bean.get("codesetid").toString())){
									if(!"".equals(AdminCode.getCodeName("UN",value)))
										value=value+"`"+AdminCode.getCodeName("UN",value);
									else if(!"".equals(AdminCode.getCodeName("UM",value)))
										value=value+"`"+AdminCode.getCodeName("UM",value);
									else
										value=value+"`"+AdminCode.getCodeName("@K",value);
								}else
									value=value+"`"+AdminCode.getCodeName(bean.get("codesetid").toString(),value);
							}
						}else if("D".equals(bean.get("itemtype").toString())){
							int leng=(Integer)bean.get("itemlength");
							Date date=this.frowset.getDate(itemid);
							if(date==null||"null".equals(date)){
								value="";
							}else{
								String type="yyyy-MM-dd H:m:s";
								if(leng==4){
									type="yyyy";
								}else if(leng==7){
									type="yyyy-MM";
								}else if(leng==10){
									type="yyyy-MM-dd";
								}else if(leng==13){
									type="yyyy-MM-dd H";
								}else if(leng==16){
									type="yyyy-MM-dd H:m";
								}else if(leng>16){
									type="yyyy-MM-dd H:m:s";
								}
								SimpleDateFormat sdf = new SimpleDateFormat(type);
								value = sdf.format(date);
							}
//							if(value==null||"null".equals(value))
//								value="";
//							else{
//								if(leng>17)
//									leng=19;
//								value=value.substring(0,leng);
//							}
						}else{
							value=this.frowset.getString(itemid);
						}
						bean.set("value", value);
						newList.add(bean);
					}
					fieldList=newList;
				}
				
				String itemInfo="";
				String guidkey="";
				//返回显示编辑对象信息参数
				if(setName.toUpperCase().startsWith("A")){
					String B0110="";
					String E0122="";
					String E01A1="";
					String A0101="";
					sql="select guidkey,B0110,E0122,E01A1,A0101 from "+nbase+"A01 where A0100 = '"+currentObject+"'";
					this.frowset=dao.search(sql);
					if(this.frowset.next()){
						B0110=this.frowset.getString("B0110");
						E0122=this.frowset.getString("E0122");
						E01A1=this.frowset.getString("E01A1");
						A0101=this.frowset.getString("A0101");
						guidkey=this.frowset.getString("guidkey");
					}
					if(AdminCode.getCode("UN", B0110)!=null)
						B0110=AdminCode.getCode("UN", B0110).getCodename()+"/";
					else
						B0110="";
					if(AdminCode.getCode("UM", E0122)!=null)
						E0122=AdminCode.getCode("UM", E0122).getCodename()+"/";
					else
						E0122="";
					if(AdminCode.getCode("@K", E01A1)!=null)
						E01A1=AdminCode.getCode("@K", E01A1).getCodename()+"/";
					else
						E01A1="";
					itemInfo=B0110+E0122+E01A1+A0101;
				}else if(setName.toUpperCase().startsWith("B")){
					itemInfo=AdminCode.getCode("UN", currentObject)==null?AdminCode.getCode("UM", currentObject).getCodename():AdminCode.getCode("UN", currentObject).getCodename();
				}else if(setName.toUpperCase().startsWith("H")){
				}else if(setName.toUpperCase().startsWith("K")){
					itemInfo=AdminCode.getCode("@K", currentObject).getCodename();
				}
				this.formHM.put("itemInfo", itemInfo);
				this.formHM.put("guidkey", guidkey);
				//获取文件list
				if("1".equals(showfile)){
					//xus 18/9/6 如果表中没有guidkey字段 则加上此字段
					DbWizard db = new DbWizard(this.frameconn);
					//表中没有GUIDKEY字段  增加GUIDKEY标识字段
					if(!db.isExistField(nbase+setName, "GUIDKEY",false)){
						Table table = new Table(nbase+setName);
						Field f = new Field("GUIDKEY",DataType.STRING);
						f.setNullable(true);
						f.setLength(38);
						table.addField(f);
						db.addColumns(table);
					}
					valueList=new ArrayList();
					valueList.add(currentObject);
					if("A01".equals(setName)){
						sql="select filename,path,ext,topic from hr_multimedia_file  where mainguid=(select GUIDKEY from "+nbase+"A01 where A0100 =?) and (childguid='' or childguid is null) order by displayorder ";
					}else{
						sql="select filename,path,ext,topic from hr_multimedia_file  where mainguid=(select GUIDKEY from "+nbase+"A01 where A0100 =?) and childguid=(select GUIDKEY from "+nbase+setName+" where A0100 =? and I9999=?) order by displayorder ";
						valueList.add(currentObject);
						valueList.add(dataIndex);
					}
					
					this.frowset=dao.search(sql,valueList);
					while(this.frowset.next()){
						LazyDynaBean bean=new LazyDynaBean();
						String filename=this.frowset.getString("filename");
						filename=PubFunc.encrypt(filename);
						// vfs改造 现在子集附件直接存 fileid 到 path 所以直接获取path即可
						String fileid=this.frowset.getString("path");
						String fileext=this.frowset.getString("ext");
						String topic=this.frowset.getString("topic");
						
						bean.set("filename", filename);
						bean.set("fileid", fileid);
						bean.set("fileext", fileext);
						bean.set("srcfilename", topic);
						
						fileList.add(bean);
					}
				}
			}
			
			this.formHM.put("fieldList", fieldList);
			this.formHM.put("fileList", fileList);
			this.formHM.put("flag", "true");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
}
