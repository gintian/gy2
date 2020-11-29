package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.general.ftp.FtpMediaBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.sys.sysout.SyncBo;
import com.hjsj.hrms.interfaces.gz.Financial_voucherXml;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.w3c.dom.Node;

import javax.sql.RowSet;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


public class GzVoucherBo {
	private Category cat = Category.getInstance(this.getClass().getName());
	private Connection conn=null;
	private UserView userView=null;
	
	
	public GzVoucherBo(Connection _conn,UserView _userView)
	{
		this.conn=_conn;
		this.userView=_userView;
	}
	
	
	public ArrayList getVoucherDataList(ArrayList voucherList)
	{
		ArrayList list=new ArrayList();
		LazyDynaBean abean=null;
		for(int i=0;i<voucherList.size();i++)
		{
			abean=(LazyDynaBean)voucherList.get(i);
			list.add(new CommonData((String)abean.get("pn_id"),(String)abean.get("c_name")));
		}
		
		return list;
	}
	
	
	
	/**
	 * 获得凭证明细数据
	 * @param status
	 * @param timeInfo
	 * @param voucher_id
	 * @param headList
	 * @return
	 */
	public ArrayList getvoucherInfoList(String status,String timeInfo,String voucher_id,ArrayList headList,String a_code,String dbilltimes)throws GeneralException
	{
		ArrayList dataList=new ArrayList();
		try
		{
			if(timeInfo==null||timeInfo.trim().length()==0)
				return new ArrayList();
			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select * from GZ_WarrantRecord,GZ_WARRANTLIST  where  GZ_WarrantRecord.pn_id=GZ_WARRANTLIST.pn_id ");
			sql.append(" and GZ_WarrantRecord.fl_id=GZ_WARRANTLIST.fl_id   and  GZ_WarrantRecord.pn_id='"+voucher_id+"' ");
			if(status!=null&&!"all".equalsIgnoreCase(status))
				sql.append(" and GZ_WarrantRecord.state='"+status+"'");
			if(dbilltimes!=null&&!"all".equalsIgnoreCase(dbilltimes))
				sql.append(" and GZ_WarrantRecord.dbill_times='"+dbilltimes+"'");
			if(timeInfo!=null&&timeInfo.trim().length()>0&&!"all".equalsIgnoreCase(timeInfo))
			{
				String[] temps=timeInfo.split("-");
				sql.append(" and "+Sql_switcher.year("GZ_WarrantRecord.dbill_Date")+"="+temps[0]);
				sql.append(" and "+Sql_switcher.month("GZ_WarrantRecord.dbill_Date")+"="+temps[1]);
			}
			sql.append(getPrivStr("GZ_WarrantRecord."));
			if(a_code!=null&&a_code.trim().length()>0)
				sql.append(" and GZ_WarrantRecord.DeptCode like '"+a_code.substring(2)+"%'");
			sql.append(" order by  GZ_WarrantRecord.dbill_Date desc,GZ_WarrantRecord.dbill_times,GZ_WarrantRecord.Pz_id,"+orderSql+"GZ_WARRANTLIST.seq");
			HashMap kmMap=new HashMap();
			RowSet rowSet=dao.search("select * from GZ_code ");
			while(rowSet.next())
				kmMap.put(rowSet.getString("ccode"),rowSet.getString("ccode_name"));
			  
			rowSet=dao.search(sql.toString());
			LazyDynaBean data_bean=null;
			LazyDynaBean _bean=null;
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			
			while(rowSet.next())
			{
				data_bean=new LazyDynaBean();
				for(int i=0;i<headList.size();i++)
				{
					_bean=(LazyDynaBean)headList.get(i);
					String itemname=(String)_bean.get("itemname");
					String itemid=(String)_bean.get("itemid");
					String itemtype=(String)_bean.get("itemtype");
					String codeset=(String)_bean.get("codeset");
					String value="";
					if("D".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getDate(itemid)!=null)
							value=df.format(rowSet.getDate(itemid));
					}
					else if("M".equalsIgnoreCase(itemtype))
						value=Sql_switcher.readMemo(rowSet,itemid);
					else if("A".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if("c_subject".equalsIgnoreCase(itemid)) //科目
							{
								String ccode=rowSet.getString(itemid);
								if(kmMap.get(ccode)!=null)
									value=(String)kmMap.get(ccode);
							}
							else if("state".equalsIgnoreCase(itemid))
							{
								String temp=rowSet.getString(itemid);
								if("0".equals(temp))  //0：起草  1：成功	 2：完成	3：失败 4 已发送
									value="起草";
								else if("1".equals(temp))
									value="成功";
								else if("2".equals(temp))
									value="完成";
								else if("3".equals(temp))
									value="失败";
								else if("4".equals(temp))
									value="已发送";
							}
							else if("0".equals(codeset))
								value=rowSet.getString(itemid);
							else if(codeset.length()>0)
							{
								value=AdminCode.getCodeName(codeset,rowSet.getString(itemid));
								if("UM".equalsIgnoreCase(codeset))
								{
									if(value==null||value.trim().length()==0)
										value=AdminCode.getCodeName("UN",rowSet.getString(itemid));
									else if(Integer.parseInt(display_e0122)>0)
									{
										CodeItem item=AdminCode.getCode("UM",rowSet.getString(itemid),Integer.parseInt(display_e0122));
						    	    	if(item!=null)
						    	    	{
						    	    		value=item.getCodename();
						        		}
									}
								}
								
							}
						}
					}
					else if("i".equalsIgnoreCase(itemtype)|| "N".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if("dbill_times".equalsIgnoreCase(itemid))
							{
								value=rowSet.getString(itemid);
								if("0".equals(value))
									value=ResourceFactory.getProperty("label.all");
							}
							else
							{
								if("money".equalsIgnoreCase(itemid)|| "ext_money".equalsIgnoreCase(itemid)|| "exch_rate".equalsIgnoreCase(itemid))//20170614 处理汇率 和本币金额小数位
								{
									value=PubFunc.round(rowSet.getString(itemid), 2);
								}
								else
									value=rowSet.getString(itemid);
							}
						}
					} 
					data_bean.set(itemid,value);
				}
				dataList.add(data_bean); 
			}
			
			if(rowSet!=null)
				rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return dataList;
	}
	
	
	/**
	 * 获得凭证明细数据
	 * @param status
	 * @param timeInfo
	 * @param voucher_id
	 * @param headList
	 * @return
	 */
	public ArrayList getvoucherInfoList2(String status,String timeInfo,String voucher_id,ArrayList headList,String a_code,String dbilltimes)throws GeneralException
	{
		ArrayList dataList=new ArrayList();
		try
		{
			if(timeInfo==null||timeInfo.trim().length()==0)
				return new ArrayList();
			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select * from GZ_WarrantRecord,GZ_WARRANTLIST  where  GZ_WarrantRecord.pn_id=GZ_WARRANTLIST.pn_id ");
			sql.append(" and GZ_WarrantRecord.fl_id=GZ_WARRANTLIST.fl_id   and  GZ_WarrantRecord.pn_id='"+voucher_id+"' ");
			if(status!=null&&!"all".equalsIgnoreCase(status))
				sql.append(" and GZ_WarrantRecord.state='"+status+"'");
			if(dbilltimes!=null&&!"all".equalsIgnoreCase(dbilltimes))
				sql.append(" and GZ_WarrantRecord.dbill_times='"+dbilltimes+"'");
			if(timeInfo!=null&&timeInfo.trim().length()>0&&!"all".equalsIgnoreCase(timeInfo))
			{
				String[] temps=timeInfo.split("-");
				sql.append(" and "+Sql_switcher.year("GZ_WarrantRecord.dbill_Date")+"="+temps[0]);
				sql.append(" and "+Sql_switcher.month("GZ_WarrantRecord.dbill_Date")+"="+temps[1]);
			}
			sql.append(getPrivStr("GZ_WarrantRecord."));
			if(a_code!=null&&a_code.trim().length()>0)
				sql.append(" and GZ_WarrantRecord.DeptCode like '"+a_code.substring(2)+"%'");
			sql.append(" order by GZ_WarrantRecord.dbill_Date desc,GZ_WarrantRecord.dbill_times,GZ_WarrantRecord.Pz_id,GZ_WARRANTLIST.seq");
			
			HashMap kmMap=new HashMap();
			RowSet rowSet=dao.search("select * from GZ_code ");
			while(rowSet.next())
				kmMap.put(rowSet.getString("ccode"),rowSet.getString("ccode_name"));
			  
			rowSet=dao.search(sql.toString());
			LazyDynaBean data_bean=null;
			LazyDynaBean _bean=null;
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			
			while(rowSet.next())
			{
				data_bean=new LazyDynaBean();
				for(int i=0;i<headList.size();i++)
				{
					_bean=(LazyDynaBean)headList.get(i);
					String itemname=(String)_bean.get("itemname");
					String itemid=(String)_bean.get("itemid");
					String itemtype=(String)_bean.get("itemtype");
					String codeset=(String)_bean.get("codeset");
					String value="";
					if("D".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getDate(itemid)!=null)
							value=df.format(rowSet.getDate(itemid));
					}
					else if("M".equalsIgnoreCase(itemtype))
						value=Sql_switcher.readMemo(rowSet,itemid);
					else if("A".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if("c_subject".equalsIgnoreCase(itemid)) //科目
							{
								String ccode=rowSet.getString(itemid);
								value = ccode;
//								if(kmMap.get(ccode)!=null)
//									value=(String)kmMap.get(ccode);
							}
							else if("state".equalsIgnoreCase(itemid))
							{
								String temp=rowSet.getString(itemid);
								if("0".equals(temp))  //0：起草  1：成功	 2：完成	3：失败 4:已发送
									value="起草";
								else if("1".equals(temp))
									value="成功";
								else if("2".equals(temp))
									value="完成";
								else if("3".equals(temp))
									value="失败";
								else if("4".equals(temp))
									value="已发送";
							}
							else if("0".equals(codeset))
								value=rowSet.getString(itemid);
							else if(codeset.length()>0)
							{
								value= rowSet.getString(itemid);
//								value=AdminCode.getCodeName(codeset,rowSet.getString(itemid));
//								if(codeset.equalsIgnoreCase("UM"))
//								{
//									if(value==null||value.trim().length()==0)
//										value=AdminCode.getCodeName("UN",rowSet.getString(itemid));
//									else if(Integer.parseInt(display_e0122)>0)
//									{
//										CodeItem item=AdminCode.getCode("UM",rowSet.getString(itemid),Integer.parseInt(display_e0122));
//						    	    	if(item!=null)
//						    	    	{
//						    	    		value=item.getCodename();
//						        		}
//									}
//								}
								
							}
						}
					}
					else if("i".equalsIgnoreCase(itemtype)|| "N".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if("dbill_times".equalsIgnoreCase(itemid))
							{
								value=rowSet.getString(itemid);
								if("0".equals(value))
									value=ResourceFactory.getProperty("label.all");
							}
							else
							{
								if("money".equalsIgnoreCase(itemid))
								{
									value=PubFunc.round(rowSet.getString(itemid), 2);
								}
								else
									value=rowSet.getString(itemid);
							}
						}
					} 
					data_bean.set(itemid,value);
				}
				data_bean.set("id",rowSet.getString("id"));
				dataList.add(data_bean); 
			}
			
			if(rowSet!=null)
				rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return dataList;
	}
		
	/**
	 * 获得凭证明细数据（用友NC）
	 * @param status
	 * @param timeInfo
	 * @param voucher_id
	 * @param headList
	 * @return
	 */
	public HashMap getNcvoucherInfoList(String timeInfo,String voucher_id,ArrayList headList)throws GeneralException
	{
		HashMap dataMap = new HashMap();
		ArrayList dataList = new ArrayList();
		try
		{
			// 薪资所属单位
			String companyfield = SystemConfig.getPropertyValue("companyfield");
			if(timeInfo==null || timeInfo.trim().length()==0)
				return new HashMap();
			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select * from GZ_WarrantRecord,GZ_WARRANTLIST where GZ_WarrantRecord.pn_id=GZ_WARRANTLIST.pn_id ");
			sql.append(" and GZ_WarrantRecord.fl_id=GZ_WARRANTLIST.fl_id and GZ_WarrantRecord.pn_id='"+voucher_id+"' ");
			sql.append(" and (GZ_WarrantRecord.state='0' or GZ_WarrantRecord.state='3')");
			if(timeInfo!=null&&!"all".equalsIgnoreCase(timeInfo))
			{
				String[] temps=timeInfo.split("-");
				sql.append(" and "+Sql_switcher.year("GZ_WarrantRecord.dbill_Date")+"="+temps[0]);
				sql.append(" and "+Sql_switcher.month("GZ_WarrantRecord.dbill_Date")+"="+temps[1]);
			}
			sql.append(getPrivStr("GZ_WarrantRecord."));
			sql.append(" order by GZ_WarrantRecord."+companyfield+",GZ_WarrantRecord.dbill_Date desc,GZ_WarrantRecord.dbill_times,GZ_WarrantRecord.Pz_id,GZ_WARRANTLIST.seq");
			
			HashMap kmMap=new HashMap();
			RowSet rowSet=dao.search("select * from GZ_code ");
			while(rowSet.next())
				kmMap.put(rowSet.getString("ccode"),rowSet.getString("ccode_name"));
			  
			rowSet=dao.search(sql.toString());
			LazyDynaBean data_bean=null;
			LazyDynaBean _bean=null;
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			
			String comfieldbefore = "";
			while(rowSet.next())
			{
				String comfield = rowSet.getString(companyfield);
				if(comfield==null || comfield.trim().length()<=0)
					comfield = "";
				
				if(comfieldbefore.trim().length()<=0 || !comfieldbefore.equalsIgnoreCase(comfield))
				{
					dataList = new ArrayList();
				}
				comfieldbefore = comfield;
				
				data_bean = new LazyDynaBean();
				for(int i=0;i<headList.size();i++)
				{
					_bean=(LazyDynaBean)headList.get(i);
					String itemname=(String)_bean.get("itemname");
					String itemid=(String)_bean.get("itemid");
					String itemtype=(String)_bean.get("itemtype");
					String codeset=(String)_bean.get("codeset");
					String value="";
					if("D".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getDate(itemid)!=null)
							value=df.format(rowSet.getDate(itemid));
					}
					else if("M".equalsIgnoreCase(itemtype))
						value=Sql_switcher.readMemo(rowSet,itemid);
					else if("A".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if("c_subject".equalsIgnoreCase(itemid)) //科目
							{
								String ccode=rowSet.getString(itemid);
								value = ccode;
//								if(kmMap.get(ccode)!=null)
//									value=(String)kmMap.get(ccode);
							}
							else if("state".equalsIgnoreCase(itemid))
							{
								String temp=rowSet.getString(itemid);
								if("0".equals(temp))  //0：起草  1：成功	 2：完成	3：失败 4:已发送
									value="起草";
								else if("1".equals(temp))
									value="成功";
								else if("2".equals(temp))
									value="完成";
								else if("3".equals(temp))
									value="失败";
								else if("4".equals(temp))
									value="已发送";
							}
							else if("0".equals(codeset))
								value=rowSet.getString(itemid);
							else if(codeset.length()>0)
							{
								value= rowSet.getString(itemid);
//								value=AdminCode.getCodeName(codeset,rowSet.getString(itemid));
//								if(codeset.equalsIgnoreCase("UM"))
//								{
//									if(value==null||value.trim().length()==0)
//										value=AdminCode.getCodeName("UN",rowSet.getString(itemid));
//									else if(Integer.parseInt(display_e0122)>0)
//									{
//										CodeItem item=AdminCode.getCode("UM",rowSet.getString(itemid),Integer.parseInt(display_e0122));
//						    	    	if(item!=null)
//						    	    	{
//						    	    		value=item.getCodename();
//						        		}
//									}
//								}
								
							}
						}
					}
					else if("i".equalsIgnoreCase(itemtype)|| "N".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if("dbill_times".equalsIgnoreCase(itemid))
							{
								value=rowSet.getString(itemid);
								if("0".equals(value))
									value=ResourceFactory.getProperty("label.all");
							}
							else
							{
								if("money".equalsIgnoreCase(itemid))
								{
									value=PubFunc.round(rowSet.getString(itemid), 2);
								}
								else
									value=rowSet.getString(itemid);
							}
						}
					} 
					data_bean.set(itemid,value);
				}
				data_bean.set("id",rowSet.getString("id"));
				
				dataList.add(data_bean); 
				
				dataMap.put(comfield, dataList);				
			}
			
			if(rowSet!=null)
				rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return dataMap;
	}
	
	/**
	 * 
	 * @param type
	 * @param timeInfo
	 * @param pn_id
	 * @param status
	 * @param a_code
	 * @param fileType =1制表符分割的txt文件，=2 excel文件
	 * @return
	 */
	public String exportFile(String type,String timeInfo,String pn_id,String status,String a_code,String fileType,String dbilltimes){
		String fileName="gzVoucherBo_"+this.userView.getUserName()+".txt";
		String Ato = fileType.split(",")[1];//将代码型转化成汉字 zhaoxg 2013-6-9
		fileType = fileType.split(",")[0];		
		if("2".equalsIgnoreCase(fileType))
			fileName="gzVoucherBo_"+this.userView.getUserName()+".xls";
		RowSet rowSet = null;

		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		HSSFRow row=null;
		HSSFCell csCell=null;
		HSSFCellStyle cellStyleRight = null;
		HSSFCellStyle cellStyleCenter = null;
		FileOutputStream fileOut = null;
		String flag = "";//将数字类型的在excel单元格中居中 zhaoxg 2013-6-9
		int rownum=0;
		try{
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			StringBuffer data_str = new StringBuffer("");
			if("2".equals(fileType))
			{
				workbook=new HSSFWorkbook();
				sheet=workbook.createSheet();
				cellStyleRight = workbook.createCellStyle();
				cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
				cellStyleCenter = workbook.createCellStyle();
				cellStyleRight.setAlignment(HorizontalAlignment.CENTER);
				workbook.close();

			}
			String separator="";
			if("1".equalsIgnoreCase(fileType))
			{
				separator="\t";
			}else if("3".equals(fileType))
			{
				separator=" ";
			}else if("4".equals(fileType))
			{
				separator="";
			}else if("5".equals(fileType))
			{
				separator="|";
			}else if("6".equals(fileType))
			{
				separator=",";
			}
			if(!(timeInfo==null||timeInfo.trim().length()<=0))
			{
				if("1".equals(type)){
					ArrayList headList=getVoucherItems(this.getVoucherBean(pn_id));
					HashMap corCodeMap = this.getCorCode();
					ContentDAO dao = new ContentDAO(this.conn);
					StringBuffer updateWhere = new StringBuffer("update gz_warrantrecord set state='1' where 1=1 ");
					StringBuffer sql=new StringBuffer("select * from GZ_WarrantRecord,GZ_WARRANTLIST  where  GZ_WarrantRecord.pn_id=GZ_WARRANTLIST.pn_id ");
					sql.append(" and GZ_WarrantRecord.fl_id=GZ_WARRANTLIST.fl_id   and  GZ_WarrantRecord.pn_id='"+pn_id+"' ");
					if(status!=null&&!"all".equalsIgnoreCase(status)){
						sql.append(" and state='"+status+"'"); 
						updateWhere.append(" and state='"+status+"'");
					}
					if(dbilltimes!=null&&!"all".equalsIgnoreCase(dbilltimes))
					{
						sql.append(" and GZ_WarrantRecord.dbill_times='"+dbilltimes+"'");
						updateWhere.append(" and GZ_WarrantRecord.dbill_times='"+dbilltimes+"'");
					}
					if(timeInfo!=null&&timeInfo.trim().length()>0&&!"all".equalsIgnoreCase(timeInfo))
					{
						String[] temps=timeInfo.split("-");
						sql.append(" and "+Sql_switcher.year("GZ_WarrantRecord.dbill_Date")+"="+temps[0]);
						sql.append(" and "+Sql_switcher.month("GZ_WarrantRecord.dbill_Date")+"="+temps[1]);
						updateWhere.append(" and "+Sql_switcher.year("dbill_Date")+"="+temps[0]);
						updateWhere.append(" and "+Sql_switcher.month("dbill_Date")+"="+temps[1]);
					}
					sql.append(getPrivStr("GZ_WarrantRecord."));
					updateWhere.append(getPrivStr(""));
					if(a_code!=null&&a_code.trim().length()>0){
						sql.append(" and GZ_WarrantRecord.DeptCode like '"+a_code.substring(2)+"%'");
						updateWhere.append(" and DeptCode like '"+a_code.substring(2)+"%'");
					}
					sql.append(" order by "+orderSql+" GZ_WarrantRecord.dbill_Date desc,GZ_WarrantRecord.dbill_times,GZ_WarrantRecord.Pz_id,GZ_WARRANTLIST.seq");
					
					HashMap kmMap=new HashMap();
					rowSet=dao.search("select * from GZ_code ");
					while(rowSet.next())
						kmMap.put(rowSet.getString("ccode"),rowSet.getString("ccode_name"));
					  
					rowSet=dao.search(sql.toString());
					SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
					String clientName=SystemConfig.getPropertyValue("clientName");
					int columnIncrease=0;
					for(int i=0;i<headList.size();i++){
						LazyDynaBean bean = (LazyDynaBean)headList.get(i);
						String itemid=(String)bean.get("itemid");
						String itemtype=(String)bean.get("itemtype");
						String itemdesc=(String)bean.get("itemname");
						String codeset=(String)bean.get("codeset");
						if("id".equalsIgnoreCase(itemid))
							continue;
						//生成表头
						FieldItem item = DataDictionary.getFieldItem(itemid.toLowerCase());
						String column="";
						if(item!=null)
						{
							if(item.getExplain()!=null&&item.getExplain().length()>0){
								column=item.getExplain();
							}else{
								column=item.getItemdesc();
							}
						}else{
							column=itemdesc;
						}
						if(column.startsWith("="))
							continue;
						if("2".equals(fileType)){
							row=sheet.getRow(rownum);
							if(row==null)
						     	row = sheet.createRow(rownum);
							csCell=row.createCell(i+columnIncrease);
							HSSFRichTextString textstr = new HSSFRichTextString(column);
							csCell.setCellValue(textstr);
							csCell.setCellStyle(cellStyleRight);
							if("TDK".equalsIgnoreCase(clientName)&& "translation_date".equalsIgnoreCase(column))
							{
								columnIncrease++;
								csCell=row.createCell(i+1);
								HSSFRichTextString atextstr = new HSSFRichTextString("reference");
								csCell.setCellValue(atextstr);
							}
							
						}else{
							data_str.append(column+separator);
							if("TDK".equalsIgnoreCase(clientName)&& "translation_date".equalsIgnoreCase(column))
								data_str.append("reference"+separator);
						}
					}
					if("2".equals(fileType)){
						rownum++;
					}else{
						data_str.append("\r\n");
					}
					while(rowSet.next()){
						if("2".equals(fileType))
						{
							row = sheet.getRow(rownum);
							if(row==null)
								row=sheet.createRow(rownum);
							
						}
						columnIncrease=0;
						for(int i=0;i<headList.size();i++){
							LazyDynaBean bean = (LazyDynaBean)headList.get(i);
							String itemid=(String)bean.get("itemid");
							String itemtype=(String)bean.get("itemtype");
							String itemdesc=(String)bean.get("itemname");
							String codeset=(String)bean.get("codeset");
							String value="";
							//生成表头
							FieldItem item = DataDictionary.getFieldItem(itemid.toLowerCase());
							String column="";
							if(item!=null)
							{
								if(item.getExplain()!=null&&item.getExplain().length()>0){
									column=item.getExplain();
								}else{
									column=item.getItemdesc();
								}
							}else{
								column=itemdesc;
							}
							if(column.startsWith("="))
								continue;
							if("id".equalsIgnoreCase(itemid))
								continue;
							if("D".equalsIgnoreCase(itemtype))
							{
								if("TDK".equalsIgnoreCase(clientName)&& "dbill_Date".equalsIgnoreCase(itemid))  //TDK特殊处理，计提月份 取凭证日期值
								{
									if(rowSet.getDate("voucher_date")!=null)
										value=df.format(rowSet.getDate("voucher_date"));
								}
								else
								{
									if(rowSet.getDate(itemid)!=null)
										value=df.format(rowSet.getDate(itemid));
								}
							}
							else if("M".equalsIgnoreCase(itemtype)){
								value=Sql_switcher.readMemo(rowSet,itemid);
							}												
							else if("A".equalsIgnoreCase(itemtype))
							{
								if(rowSet.getString(itemid)!=null)
								{
									if("c_subject".equalsIgnoreCase(itemid)) //科目
									{
										String ccode=rowSet.getString(itemid);
										if("TDK".equalsIgnoreCase(clientName))
											value=ccode;
										else{
									    	if(kmMap.get(ccode)!=null)
										    	value=(String)kmMap.get(ccode);
										}
										value=value==null?"":value;
									}
									else if("state".equalsIgnoreCase(itemid))
									{
										String temp=rowSet.getString(itemid);
										if("0".equals(temp))  //0：起草  1：成功	 2：完成	3：失败 4： 已发送
											value="起草";
										else if("1".equals(temp))
											value="成功";
										else if("2".equals(temp))
											value="完成";
										else if("3".equals(temp))
											value="失败";
										else if("4".equals(temp))
											value="已发送";
									}
									else if("0".equals(codeset))
										value=rowSet.getString(itemid)==null?"":rowSet.getString(itemid);
									else if(codeset.length()>0)
									{
										if("1".equals(Ato)){
											value=rowSet.getString(itemid)==null?"":rowSet.getString(itemid);
											value=AdminCode.getCodeName(codeset,rowSet.getString(itemid));
											if(value==null||value.trim().length()==0)
												value=AdminCode.getCodeName("UN",rowSet.getString(itemid));
										}else{
											value=rowSet.getString(itemid)==null?"":rowSet.getString(itemid);
											if(!"".equals(value)&&corCodeMap.get(codeset.toUpperCase()+value.toUpperCase())!=null)
												value=(String)corCodeMap.get(codeset.toUpperCase()+value.toUpperCase());
										}								
									}
								}
							}
							else if("i".equalsIgnoreCase(itemtype)|| "N".equalsIgnoreCase(itemtype))
							{
								if(rowSet.getString(itemid)!=null)
								{
									if("N".equalsIgnoreCase(itemtype)){
										flag = "1";
									}							
									if("dbill_times".equalsIgnoreCase(itemid))
									{
										value=rowSet.getString(itemid);
										if("0".equals(value))
											value=ResourceFactory.getProperty("label.all");
									}
									else
									{
										if("money".equalsIgnoreCase(itemid)|| "ext_money".equalsIgnoreCase(itemid)|| "exch_rate".equalsIgnoreCase(itemid))
										{
											value=PubFunc.round(rowSet.getString(itemid), 2);
										}
										else
											value=rowSet.getString(itemid)==null?"0":rowSet.getString(itemid);
									}
								}
							} 
							if("TDK".equalsIgnoreCase(clientName)&& "n_loan".equalsIgnoreCase(itemid)){
								if("借".equals(value))
									value="S";
								else if("贷".equalsIgnoreCase(value))
									value="H";
							}
							if("2".equals(fileType)){
								csCell=row.createCell(i+columnIncrease);
								HSSFRichTextString textstr = new HSSFRichTextString(value);
								
								// add by xiegh on date 20180104 bug:33647 用富文本类型  导出的excel对应的字段成了字符类型了
								if("N".equalsIgnoreCase(itemtype) && !"全部".equals(value)) {
									if(StringUtils.isNotBlank(value)) {
										csCell.setCellValue(Double.parseDouble(value));
									}else{
										csCell.setCellValue("");
									}
								}
								else
									csCell.setCellValue(textstr);
								csCell.setCellStyle(cellStyleRight);
								if("TDK".equalsIgnoreCase(clientName)&& "translation_date".equalsIgnoreCase(column)){
									csCell=row.createCell(i+1);
									HSSFRichTextString atextstr = new HSSFRichTextString(rowSet.getString("flseq"));
									csCell.setCellValue(atextstr);
								}
								
							}else{
								data_str.append(value+separator);
								if("TDK".equalsIgnoreCase(clientName)&& "translation_date".equalsIgnoreCase(column))
									data_str.append(rowSet.getString("flseq")+separator);
							}
						}
						if("2".equals(fileType)){
							rownum++;
						}else{
							data_str.append("\r\n");
						}
					}
			//		dao.update(updateWhere.toString());
				}else{

					ArrayList headList = getMonthCollectHeadList(pn_id);
					HashMap corCodeMap = this.getCorCode();
			   		StringBuffer buf = new StringBuffer();
		    		buf.append("select pn_id");
		    		for(int i=0;i<headList.size();i++){
		    			LazyDynaBean bean = (LazyDynaBean)headList.get(i);
		    			String itemid=(String)bean.get("itemid");
		    			if(null==itemid||"".equals(itemid))
		    				continue;
		    		    buf.append(","+itemid);
		    			
		    		}
		    		buf.append(",period,psncode from gz_warrantdata");
		    		buf.append(" where ");
		    		buf.append(" pn_id="+pn_id);
		    		if(status!=null&&!"all".equalsIgnoreCase(status))
						buf.append(" and status='"+status+"'");
					if(timeInfo!=null&&timeInfo.trim().length()>0&&!"all".equalsIgnoreCase(timeInfo)&&!"".equals(timeInfo))
					{
						String[] temps=timeInfo.split("-");
						buf.append(" and "+Sql_switcher.year("period")+"="+temps[0]);
						buf.append(" and "+Sql_switcher.month("period")+"="+temps[1]);
					}
					buf.append(getPrivStr(""));
					if(a_code!=null&&a_code.trim().length()>0)
						buf.append(" and DeptCode like '"+a_code.substring(2)+"%'");
				    buf.append(" order by pndate desc,a0000,unitcode,deptcode ");
				    ContentDAO dao = new ContentDAO(this.conn);
					HashMap kmMap=new HashMap();
					rowSet=dao.search("select * from GZ_code ");
					while(rowSet.next())
						kmMap.put(rowSet.getString("ccode"),rowSet.getString("ccode_name"));
					
				    rowSet=dao.search(buf.toString());
				    SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
					int columnIncrease=0;
					for(int i=0;i<headList.size();i++){
						LazyDynaBean bean = (LazyDynaBean)headList.get(i);
						String itemid=(String)bean.get("itemid");
						String itemdesc=(String)bean.get("itemname");
						if("id".equalsIgnoreCase(itemid))
							continue;
						//生成表头
						String column="";
						column=itemdesc;
						if("2".equals(fileType)){
							row=sheet.getRow(rownum);
							if(row==null)
						     	row = sheet.createRow(rownum);
							csCell=row.createCell(i+columnIncrease);
							HSSFRichTextString textstr = new HSSFRichTextString(column);
							csCell.setCellValue(textstr);
							csCell.setCellStyle(cellStyleCenter);
						}else{
							data_str.append(column+separator);
						}
					}
					if("2".equals(fileType)){
						rownum++;
					}else{
						data_str.append("\r\n");
					}
					while(rowSet.next()){
						if("2".equals(fileType))
						{
							row = sheet.getRow(rownum);
							if(row==null)
								row=sheet.createRow(rownum);
							
						}
						columnIncrease=0;
						for(int i=0;i<headList.size();i++){
							LazyDynaBean bean = (LazyDynaBean)headList.get(i);
							String itemid=(String)bean.get("itemid");
							String itemtype=(String)bean.get("itemtype");
							String codeset=(String)bean.get("codeset");
							String value="";
							if("id".equalsIgnoreCase(itemid))
								continue;
							if("D".equalsIgnoreCase(itemtype))
							{
								if(rowSet.getDate(itemid)!=null)
									value=df.format(rowSet.getDate(itemid));
							}
							else if("M".equalsIgnoreCase(itemtype)){
								value=Sql_switcher.readMemo(rowSet,itemid);
							}												
							else if("A".equalsIgnoreCase(itemtype))
							{
								if(rowSet.getString(itemid)!=null)
								{
									if("0".equals(codeset))
										value=rowSet.getString(itemid)==null?"":rowSet.getString(itemid);
									else if(codeset.length()>0)
									{
										if("1".equals(Ato)){
											value=rowSet.getString(itemid)==null?"":rowSet.getString(itemid);
											value=AdminCode.getCodeName(codeset,rowSet.getString(itemid));
											if(value==null||value.trim().length()==0)
												value=AdminCode.getCodeName("UN",rowSet.getString(itemid));
										}else{
											value=rowSet.getString(itemid)==null?"":rowSet.getString(itemid);
											if(!"".equals(value)&&corCodeMap.get(codeset.toUpperCase()+value.toUpperCase())!=null)
												value=(String)corCodeMap.get(codeset.toUpperCase()+value.toUpperCase());
										}								
									}
								}
							}
							else if("i".equalsIgnoreCase(itemtype)|| "N".equalsIgnoreCase(itemtype))
							{
								if(rowSet.getString(itemid)!=null)
								{						
									if("status".equalsIgnoreCase(itemid))
									{
										int v=rowSet.getInt("status");
										if(v==1)
											value="已生成";
										else if(v==2)
											value="已通知";
										else if(v==3)
											value="已接收";
									}
									else
									{
										String roundFlag = (String)bean.get("roundFlag");
										value=rowSet.getString(itemid)==null?"0":rowSet.getString(itemid);
										if("true".equalsIgnoreCase(roundFlag))//xiegh add 数值类型列四舍五入 保留两位小数
											value = PubFunc.round(value, 2);
									}
								}
							}
							if("2".equals(fileType)){
								csCell=row.createCell(i+columnIncrease);
								HSSFRichTextString textstr = new HSSFRichTextString(value);
								csCell.setCellValue(textstr);
								csCell.setCellStyle(cellStyleRight);
							}else{
								data_str.append(value+separator);
							}
						}
						if("2".equals(fileType)){
							rownum++;
						}else{
							data_str.append("\r\n");
						}
					}
				}

	    	}
			if("2".equals(fileType)){
				workbook.write(fileOut);
				fileOut.close();	
				sheet=null;
				workbook=null;
				/* 安全问题 文件下载 财务凭证 导出 xiaoyun 2014-9-13 start */
				//fileName=fileName.replace(".xls", "#");
				/* 安全问题 文件下载 财务凭证 导出 xiaoyun 2014-9-13 end */
		    	
			}
			else {
				fileOut.write(data_str.toString().getBytes());
		    	fileOut.close();
		    	/* 安全问题 文件下载 财务凭证 导出 xiaoyun 2014-9-13 start */
		    	//fileName=fileName.replace(".txt","#");
		    	/* 安全问题 文件下载 财务凭证 导出 xiaoyun 2014-9-13 end */
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(rowSet);
			PubFunc.closeResource(workbook);
		}
		return fileName;
	}
	public HashMap getCorCode(){
		HashMap map = new HashMap();
		RowSet rs = null;
		try{
			if(this.codeSetMap.size()>0)
			{
				Set keySet = this.codeSetMap.keySet();
				StringBuffer orgBuf = new StringBuffer("");
				StringBuffer codeBuf = new StringBuffer("");
				for(Iterator iterator = keySet.iterator();iterator.hasNext();){
					String key = (String)iterator.next();
					if("UM".equalsIgnoreCase(key)|| "UN".equalsIgnoreCase(key)|| "@K".equalsIgnoreCase(key))
						orgBuf.append(",'"+key+"'");
					else
						codeBuf.append(",'"+key+"'");
				}
				StringBuffer sql = new StringBuffer("");
				ContentDAO dao = new ContentDAO(this.conn);
				if(orgBuf.toString().length()>0)
				{
					sql.append(" select codesetid,codeitemid,corcode from organization ");
					sql.append(" where UPPER(codesetid) in ("+orgBuf.toString().substring(1)+")");
					rs = dao.search(sql.toString());
					while(rs.next()){
						map.put(rs.getString("codesetid").toUpperCase()+rs.getString("codeitemid").toUpperCase(), rs.getString("corcode"));
					}
				}
				if(codeBuf.toString().length()>0)
				{
					sql.setLength(0);
					sql.append(" select codesetid,codeitemid,corcode from codeitem ");
					sql.append(" where UPPER(codesetid) in ("+codeBuf.toString().substring(1)+")");
					rs = dao.search(sql.toString());
					while(rs.next()){
						map.put(rs.getString("codesetid").toUpperCase()+rs.getString("codeitemid").toUpperCase(), rs.getString("corcode"));
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return map;
	}
	
	/**
	 * 获得状态列表
	 * @param type
	 * @return
	 */
	public ArrayList getStatusList(String type)
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
		if("1".equals(type)) //财务凭证接口
		{
			list.add(new CommonData("0","起草"));
			list.add(new CommonData("1","成功"));
			list.add(new CommonData("3","失败")); 
			list.add(new CommonData("4","已发送"));
		}
		else
		{
			list.add(new CommonData("1","已生成"));
			list.add(new CommonData("2","已通知"));
			list.add(new CommonData("3","已接收"));
		}
		return list;
	}
	/**
	 * 获得发放次数列表
	 * @param voucher_id , timeInfo
	 * @return
	 */
	public ArrayList getDbilltimesList(String voucher_id,String timeInfo)
	{
		ArrayList list = new ArrayList();
		list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
		RowSet rowSet = null;
		try
		{			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select distinct dbill_times from GZ_WarrantRecord ");
			sql.append(" where pn_id='"+voucher_id+"' ");	
			if(timeInfo!=null&&timeInfo.trim().length()>0&&!"all".equalsIgnoreCase(timeInfo))
			{
				String[] temps=timeInfo.split("-");
				sql.append(" and "+Sql_switcher.year("dbill_Date")+"="+temps[0]);
				sql.append(" and "+Sql_switcher.month("dbill_Date")+"="+temps[1]);
			}
			sql.append(getPrivStr(""));
		//	if(a_code!=null&&a_code.trim().length()>0)
		//		sql.append(" and DeptCode like '"+a_code.substring(2)+"%'");
			
			sql.append(" order by dbill_times ");
			rowSet=dao.search(sql.toString());			
			while(rowSet.next())
			{
				String dbill_times = rowSet.getString("dbill_times");
				if(dbill_times!=null && dbill_times.trim().length()>0 && !"0".equalsIgnoreCase(dbill_times))
				{
					list.add(new CommonData(dbill_times,"第"+dbill_times+"次"));
				}
			}
		
			if(rowSet!=null)
				rowSet.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获得凭证日期列表
	 * @param voucher_id
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getTimeList(String voucher_id,String type) throws GeneralException
	{
		ArrayList timeList=new ArrayList();
		timeList.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql="";
			if("1".equals(type))
			{
				sql="select distinct  "+Sql_switcher.year("dbill_Date")+","+Sql_switcher.month("dbill_Date")+"   from GZ_WarrantRecord where Pn_id='"+voucher_id+"'";
				sql+=getPrivStr("")+"  order by  "+Sql_switcher.year("dbill_Date")+" desc,"+Sql_switcher.month("dbill_Date")+" desc ";
			}else{
				this.sycnTableStrut(voucher_id,type);
				sql="select distinct  "+Sql_switcher.year("period")+" a,"+Sql_switcher.month("period")+" b   from GZ_WarrantData where Pn_id='"+voucher_id+"'";
				sql+=getPrivStr("")+"  order by a desc,b desc";
			}
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String year=rowSet.getString(1);
				String month=rowSet.getString(2);
				String _month=month;
				if(month.length()==1)
					_month="0"+_month;
				timeList.add(new CommonData(year+"-"+month,year+"年"+_month+"月"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}
		return timeList;
	}
	/**
	 * 获取权限语句
	 * @return
	 */
	private String getPrivStr(String tableName)
	{
		StringBuffer str=new StringBuffer("");
		 
		if(!this.userView.isSuper_admin())
		{
			String code = this.userView.getUnitIdByBusi("1");//52628  按照业务范围->操作单位->人员范围来获取权限
			if(code==null|| "".equals(code.trim()))
			{
//				String codevalue=this.userView.getManagePrivCodeValue();
//				String _code=this.userView.getManagePrivCode();
//				if(_code!=null&&!_code.equals("")&&!_code.equalsIgnoreCase("un"))
//					str.append(" and  "+tableName+"DeptCode   like '" + codevalue+ "%' ") ;
//				else
					str.append(" and 1=2");
			}
			else
			{
				if ("UN`".equalsIgnoreCase(code)||code.length() == 3){
					
				}
				else
				{
					String[] arr = code.split("`");
					StringBuffer temp = new StringBuffer("");
					for (int j = 0; j < arr.length; j++) {
						if (arr[j] == null || "".equals(arr[j]))
								continue;
						String codeset = arr[j].substring(0, 2);
						String value = arr[j].substring(2);
						temp.append(" or  "+tableName+"DeptCode   like '" + value+ "%'");
					}
					str.append(" and ("+ temp.toString().substring(3)+" )") ;
				}
			}
		}
		return str.toString();
	}
	
	String orderSql="";
	
	/**
	 * 获得凭证项目
	 * @param voucherBean
	 * @return
	 */
	public ArrayList getVoucherItems(LazyDynaBean voucherBean)
	{
		ArrayList itemList=new ArrayList();
		String content=(String)voucherBean.get("content");
		String c_scope=(String)voucherBean.get("c_scope");
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			Table table_add=new Table("gz_warrantrecord");
			
			LazyDynaBean abean=null; 
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from GZ_WarrantRecord where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			HashMap tableColumnMap=new HashMap();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				String _columnName=mt.getColumnName(i+1).toLowerCase().trim();
				tableColumnMap.put(_columnName, "1");
			}
			boolean isFlseq=false;
			boolean isGpseq=false;
			if(content.length()>0)
			{ 
				Document a_doc =PubFunc.generateDom(content);
				
				 XPath xPath = XPath.newInstance("/voucher/items");
				 Element element=null;
				 element = (Element) xPath.selectSingleNode(a_doc);  
				 if(element!=null)
				 {
					 if (element.getAttributeValue("fields") != null && !"".equals(element.getAttributeValue("fields")))
					 {
						String fields=element.getAttributeValue("fields");
						String[] temps=fields.split(",");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i]!=null&&temps[i].trim().length()>0)
							{
							 
								if(",pn_id,fl_id,c_itemsql,c_where,".indexOf(","+temps[i].toLowerCase()+",")!=-1)
									continue;
								
								 
								FieldItem item=DataDictionary.getFieldItem(temps[i].trim().toLowerCase());
								if(item==null)
									continue;
								if("flseq".equalsIgnoreCase(item.getItemid()))
									isFlseq=true;
								if("gpseq".equalsIgnoreCase(item.getItemid()))
									isGpseq=true;
								if(item.getFieldsetid().startsWith("A")||item.getFieldsetid().startsWith("B")||item.getFieldsetid().startsWith("K"))
								{
									
								}else{
							    	if((item.getState()==null|| "0".equals(item.getState())))
								    	continue;
								}
								String itemid=item.getItemid();
								 
								itemList.add(getBean(itemid,item.getItemdesc(),item.getItemtype(),item.getCodesetid()));  
						/*		if(itemid.equalsIgnoreCase("n_loan"))
								{
									
									itemList.add(getBean("money",ResourceFactory.getProperty("gz.csah.moneyamount"),"N","0"));
								}  
						*/		
								if(tableColumnMap.get(itemid.toLowerCase())==null)
								{
									table_add.addField(item);
								}
								
								
							}
						}
						 
					 }
				 }
				
			}
			/*
			else if(content.trim().length()==0)
			{
				  
				FieldItem item=null;
				for(int i=0;i<fieldList.size();i++)
				{
					item=(FieldItem)fieldList.get(i);
					String itemid=item.getItemid();
					if(",pn_id,fl_id,c_itemsql,c_where,check_item,check_item_value,".indexOf(","+itemid.toLowerCase()+",")!=-1)
						continue; 
					itemList.add(getBean(itemid,item.getItemdesc(),item.getItemtype(),item.getCodesetid()));
					if(itemid.equalsIgnoreCase("n_loan")) //借贷项后加金额
						itemList.add(getBean("money",ResourceFactory.getProperty("gz.csah.moneyamount"),"N","0"));
				} 
			}
			*/
			itemList.add(getBean("state",ResourceFactory.getProperty("column.sys.status"),"A","0"));
			itemList.add(getBean("id","","A","0"));
			if(isFlseq)
				orderSql=" GZ_WarrantRecord.flseq,";
			if(isGpseq){
				if(isFlseq)
					orderSql+=" GZ_WarrantRecord.gpseq,";
				else
					orderSql=" GZ_WarrantRecord.gpseq,";
			}
			if(rowSet!=null)
				rowSet.close();
			
			if(table_add.size()>0)
				dbw.addColumns(table_add);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 
		return itemList;
	}
	
	 
	 
	
	private HashMap codeSetMap = new HashMap();
	private LazyDynaBean getBean(String itemid,String itemdesc,String itemtype,String codeset)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("itemname",itemdesc);
		abean.set("itemid",itemid);
		abean.set("itemtype",itemtype);
		abean.set("codeset",codeset);
		if(!"0".equals(codeset))
		{
			codeSetMap.put(codeset.toUpperCase(),codeset.toUpperCase());
		}
		 
		
		return abean;
	}
	
	
	
	/**
	 * 根据凭证id获得凭证信息
	 * @param id
	 * @param voucherList
	 * @return
	 */
	public LazyDynaBean getVoucherBean(String id,ArrayList voucherList)
	{
		
		LazyDynaBean abean=null;
		for(int i=0;i<voucherList.size();i++)
		{
			abean=(LazyDynaBean)voucherList.get(i);
			if(((String)abean.get("pn_id")).equals(id))
				break;
		}
		return abean;
	}
	
	
	public boolean changeWarrantTableStrut()
	{
		boolean bflag=true;
		String tablename="gz_warrant";
		try
		{
			DbWizard dbwizard=new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			
			Table _table=new Table(tablename);
			RowSet rowSet=dao.search("select * from "+tablename+" where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			HashMap columMap=new HashMap();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				columMap.put(mt.getColumnName(i+1).toLowerCase(),"1");
			}
			
			boolean isAdd=false;
			if(columMap.get("c_scope")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("A");
				tempitem.setItemlength(200); 
				tempitem.setItemid("C_scope");
				_table.addField(tempitem);
				isAdd=true;
			}
			if(columMap.get("type")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("A");
				tempitem.setItemlength(1); 
				tempitem.setItemid("type");
				_table.addField(tempitem);
				isAdd=true;
			}
			if(columMap.get("collect_fields")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("A");
				tempitem.setItemlength(200); 
				tempitem.setItemid("Collect_fields");
				_table.addField(tempitem);
				isAdd=true;
			}
			if(columMap.get("content")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("M"); 
				tempitem.setItemid("content");
				_table.addField(tempitem);
				isAdd=true;
			}
			 
			if(isAdd)
				dbwizard.addColumns(_table);
				 
			
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	
	
	public LazyDynaBean getVoucherBean(String pn_id){
		LazyDynaBean bean = null;
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search("select * from GZ_Warrant where pn_id="+pn_id);
			while(rs.next()){
				bean=new LazyDynaBean();
				String c_name=rs.getString("c_name");
				String c_dbase=rs.getString("c_dbase");
				String c_scope=rs.getString("c_scope")!=null?rs.getString("c_scope"):"";
				String type=rs.getString("interface_type")!=null?rs.getString("interface_type"):"1";
				String collect_fields=rs.getString("collect_fields");
				String content=Sql_switcher.readMemo(rs,"content");
				bean.set("pn_id", pn_id);
				bean.set("c_name", c_name);
				bean.set("c_dbase", c_dbase);
				bean.set("c_scope", c_scope);
				bean.set("type", type);
				bean.set("collect_fields", collect_fields); 
				bean.set("content", content);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
				{
					rs.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return bean;
	}
	
	/**
	 * 返回凭证列表
	 * @return
	 */
	public ArrayList getVoucherList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			DbWizard dbwizard=new DbWizard(this.conn);
			if(!dbwizard.isExistField("gz_warrant","pn_id", false))
				throw GeneralExceptionHandler.Handle(new Exception("没有定义凭证!"));	
			rowSet=dao.search("select * from GZ_Warrant order by pn_id");
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
	          	  String privflag = Financial_voucherXml.IsHavePriv(this.userView,rowSet.getString("b0110"));//1：没关系 2：包含（上级） 3：下级
	        	  if("1".equals(privflag)){
	        		  continue;
	        	  }
				abean=new LazyDynaBean();
				String pn_id=rowSet.getString("pn_id");
				String c_name=rowSet.getString("c_name");
				String c_dbase=rowSet.getString("c_dbase");
				String c_scope=rowSet.getString("c_scope")!=null?rowSet.getString("c_scope"):"";
				String type=rowSet.getString("interface_type")!=null?rowSet.getString("interface_type"):"1";
				String collect_fields=rowSet.getString("collect_fields");
				String content=Sql_switcher.readMemo(rowSet,"content");
				String[] salaryids=c_scope.split(",");
				boolean isPriv = false;
				if(this.userView.isSuper_admin())
					isPriv = true;
				for(int i=0;i<salaryids.length;i++)
				{
					if(salaryids[i]!=null&&salaryids[i].trim().length()>0)
					{
						if(this.userView.isHaveResource(IResourceConstant.GZ_SET,salaryids[i].trim()))
							isPriv = true; 
						if(this.userView.isHaveResource(IResourceConstant.INS_SET,salaryids[i].trim()))
							isPriv = true; 
					}
				}
				if(isPriv)
				{
					abean.set("pn_id", pn_id);
					abean.set("c_name", c_name);
					abean.set("c_dbase", c_dbase);
					abean.set("c_scope", c_scope);
					abean.set("type", type);
					abean.set("collect_fields", collect_fields); 
					abean.set("content", content);
					list.add(abean);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		finally
		{
			try
			{
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		} 
		return list;
	}
	
	private String firstGroupField;
	
	private String lastGroupField;
	
	public void getFirstAndLastGroup(String c_group){
		String[] temps=c_group.split(",");
		for(int i=0;i<temps.length;i++)
		{
			if(temps[i].trim().length()>0)
			{
				if(firstGroupField==null|| "".equals(this.firstGroupField))
					this.firstGroupField=temps[i];
				this.lastGroupField=temps[i];
			}
		} 
	}
	
	
	private ArrayList getGroupList(String filter_sql,String collect_fields)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String _str="";
			String[] temps=collect_fields.split(",");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i].trim().length()>0)
				{
					FieldItem item=DataDictionary.getFieldItem(temps[i].trim().toLowerCase());
					if("A".equalsIgnoreCase(item.getItemtype()))
						_str+=",nullif("+temps[i]+",'') "+temps[i];
					else
						_str+=","+temps[i];
				}
			} 
			String sql="select distinct "+_str.substring(1)+" from salaryhistory where 1=1 "+filter_sql;
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						FieldItem item=DataDictionary.getFieldItem(temps[i].trim().toLowerCase());
						if("N".equalsIgnoreCase(item.getItemtype())|| "A".equalsIgnoreCase(item.getItemtype()))
						{
							if(rowSet.getString(temps[i])!=null)
							{
								abean.set(temps[i].toLowerCase(),rowSet.getString(temps[i]));
							}
							else
							{
								abean.set(temps[i].toLowerCase(),"");
							}
						}
						else if("D".equalsIgnoreCase(item.getItemtype()))
						{
							if(rowSet.getDate(temps[i])!=null)
							{
								abean.set(temps[i].toLowerCase(),fm.format(rowSet.getDate(temps[i])));
							}
							else
							{
								abean.set(temps[i].toLowerCase(),"");
							}
						}
						else if("M".equalsIgnoreCase(item.getItemtype()))
						{
							abean.set(temps[i].toLowerCase(),Sql_switcher.readMemo(rowSet, temps[i]));
						}
					}
				}
				list.add(abean);
			}
			
			
		}
		catch(Exception e)
		{
			
		}
		return list;
	}

	/**
	 * 生成凭证数据i
	 * @param year  计提月份
	 * @param month 计提月份
	 * @param type  凭证类型
	 * @param count 
	 * @param voucher_date  凭证日期
	 * @param deptcode      单位部门
	 * @param voucher_id    凭证号
	 * @throws GeneralException
	 */
	public void CreateVoucherData(String year,String month,String count,String voucher_date,String deptcode,String voucher_id)throws GeneralException
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			DbWizard dbw = new DbWizard(this.conn);
			RecordVo voucherVo=new RecordVo("gz_warrant");
			voucherVo.setInt("pn_id",Integer.parseInt(voucher_id));
			voucherVo=dao.findByPrimaryKey(voucherVo);
			String collect_fields=voucherVo.getString("collect_fields");  //汇总指标
			String c_scope=voucherVo.getString("c_scope");  //薪资类别
			String content=voucherVo.getString("content");
			String c_name=voucherVo.getString("c_name");
			String voucher_fields=""; //凭证项目
			String rateItem="";//汇率指标
			boolean bDualMoney=false;//是否双币凭证
			if(content!=null&&content.trim().length()>0)
			{
				Document a_doc =PubFunc.generateDom(content);
				
				 XPath xPath = XPath.newInstance("/voucher/items");
				 Element element=null;
				 element = (Element) xPath.selectSingleNode(a_doc);  
				 if(element!=null)
				 {
					 if (element.getAttributeValue("fields") != null && !"".equals(element.getAttributeValue("fields")))
					 {
						 voucher_fields=element.getAttributeValue("fields");
					 }
				 }
				
				 XPath moneyPath = XPath.newInstance("/voucher/is_dual_money");
				 Element elemoney = (Element) moneyPath.selectSingleNode(a_doc);  
				 if(elemoney!=null&&"true".equalsIgnoreCase(elemoney.getText())){
					 bDualMoney=true;
					 XPath ratePath = XPath.newInstance("/voucher/exchg_rate_fld");
					 Element rateElement = (Element) ratePath.selectSingleNode(a_doc);  
					 if(rateElement!=null){
						 rateItem = rateElement.getText();
						// voucher_fields=voucher_fields+","+"exch_rate";
					 }
				 }
			}
			
			if(voucher_fields.length()==0)
				voucher_fields="c_mark,c_subject,fl_name,c_itemsql,c_where,n_loan";
			String[] voucherItems=voucher_fields.toLowerCase().split(",");
			List<String> salaryItemList = new ArrayList<String>();
			for(String str :voucherItems){
				if(dbw.isExistField("salaryhistory",str ,false)){
					salaryItemList.add(str);
				}
			}
		
			
		
			ArrayList gzFieldList=getGzFieldList(voucherVo);
			ArrayList warrantList=getWarrantList(voucher_id);  //凭证分录
			
			/*************************xiegh 对凭证增加辅助核算项*******************************************/
			HashMap<String,Map<String,String>> datamap = new HashMap<String,Map<String,String>>();//key:fl_id,value:辅助核算项目及辅助核算项目值
			for(int i=0;i<warrantList.size();i++){
				LazyDynaBean bean =(LazyDynaBean)warrantList.get(i);
				HashMap<String,String> map = (HashMap<String,String>) bean.getMap();
				Map<String,String> fuzhumap = new HashMap<String,String>();
				for(Entry<String,String> obj : map.entrySet()){
					String itemid = obj.getKey().toLowerCase();
					FieldItem fielditem=DataDictionary.getFieldItem(itemid);
					if(fielditem!=null&&fielditem.getItemdesc().contains("辅助核算")&&(obj.getValue().trim().indexOf("[")!=-1&&obj.getValue().trim().indexOf("]")!=-1)){
						if(obj.getValue().trim().indexOf(":")!=-1){
							String desc = obj.getValue().split(":")[0].replace("[", "").trim();
							String itemcode = getItemId(desc,conn);
							String value = "".equals(itemcode)?desc:itemcode;//如果输入的是代码则取代码  如果不是则到数据库中查id
							if(isChinese(value))
								throw GeneralExceptionHandler.Handle(new Exception("该凭证定义下的分录中的"+fielditem.getItemdesc()+"配置存在问题，请检查！"));
							fuzhumap.put(obj.getKey()+"-",value);//key带有"-"表示要取名称
						}else{
							String itemdid = getItemId(obj.getValue(),conn);
							fuzhumap.put(obj.getKey(),itemdid);
						}
					}
				}
				datamap.put(map.get("fl_id"), fuzhumap);
				
			}
			/*************************end*******************************************/
			
			String filter_str=getFilter_str(voucherVo,year,month,count,deptcode,"1"); //获得凭证统计数据范围sql语句
			Calendar getDate=Calendar.getInstance();
			getDate.set(Calendar.YEAR,Integer.parseInt(year));
			getDate.set(Calendar.MONTH,Integer.parseInt(month)-1);
			getDate.set(Calendar.DATE,1);
			Calendar _voucher_date=Calendar.getInstance();
			String[] temps=voucher_date.split("-");
			_voucher_date.set(Calendar.YEAR,Integer.parseInt(temps[0]));
			_voucher_date.set(Calendar.MONTH,Integer.parseInt(temps[1])-1);
			_voucher_date.set(Calendar.DATE,Integer.parseInt(temps[2]));
			
			LazyDynaBean bean=null;

			RowSet rowSet=null;
			RowSet rowSet2=null;
			ArrayList dataList=new ArrayList();
			IDGenerator idg = new IDGenerator(2, this.conn); 
	  		
    		if(!dbw.isExistField("gz_warrantrecord", "FLSEQ",false))
    		{
    			Table table=new Table("gz_warrantrecord");
				Field field=new Field("FLSEQ","分录序号");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);	
				dbw.addColumns(table);
    		}
    		if(!dbw.isExistField("gz_warrantrecord", "FLSEQ",false))
    		{
    			Table table=new Table("gz_warrantrecord");
				Field field=new Field("GPSEQ","分录内序号");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);	
				dbw.addColumns(table);
    		}
    		if(!dbw.isExistField("gz_warrantrecord", "NBASE",false))
    		{
    			Table table=new Table("gz_warrantrecord");
				Field field=new Field("NBASE","人员库标识");
				field.setDatatype(DataType.STRING);
				field.setLength(3);
				table.addField(field);	
				dbw.addColumns(table);
    		}
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel("gz_warrantrecord");
			/**凭证的分组指标值*/
			int seq_increase=0;//为了生成tdk要求的序号
			BigDecimal zero=new BigDecimal("0");//为了与计算出来的值比较，得出与0的关系
			int currFlSeq=1;
			int currGpSeq=1;
			String clientName = SystemConfig.getPropertyValue("clientName");

			getFirstAndLastGroup(collect_fields);
			YksjParser yp=null;  
			HashMap pz_idMap = new HashMap();
			
			ArrayList voucherGroupList=new ArrayList();			
			if(collect_fields!=null&&collect_fields.trim().length()>0)
			{
				
				voucherGroupList=getGroupList(filter_str,collect_fields);
			}
			else
				voucherGroupList.add(new LazyDynaBean());
			
			
			for(int j=0;j<warrantList.size();j++)
			{
				dataList=new ArrayList();
				bean=(LazyDynaBean)warrantList.get(j);
				String c_itemSQL=(String)bean.get("c_itemsql");
				String c_extitemSQL="";//本币计算公式。
				if (bDualMoney){
					if (bean.get("c_extitemsql")!=null)
						c_extitemSQL=(String)bean.get("c_extitemsql");
				}
				String c_where=(String)bean.get("c_where");
				String fl_id=(String)bean.get("fl_id");
				String fl_name=(String)bean.get("fl_name");
				String c_subject=(String)bean.get("c_subject");
				String n_loan=(String)bean.get("n_loan");
				String c_group = (String)bean.get("c_group");
				String extSql ="";
				List<String> extList = new ArrayList<String>();
				for(String item : salaryItemList){
					if(!c_group.contains(item)&&!collect_fields.contains(item)){
						extSql=extSql+" max("+item+") as "+ item.toLowerCase()+" ,";
						extList.add(item.toLowerCase());
					}
						
				}
				String strfilter="";
				/**先对计算公式的条件进行分析*/
				if(!(c_where==null|| "".equalsIgnoreCase(c_where)))
				{
					yp = new YksjParser(this.userView ,gzFieldList,YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
					yp.run_where(c_where);
					strfilter=" and ( "+yp.getSQL()+" )";
					
				}
				yp=new YksjParser(this.userView ,gzFieldList,YksjParser.forNormal, YksjParser.FLOAT,YksjParser.forPerson , "Ht", "");
				yp.run(c_itemSQL,this.conn,"","salaryhistory");
				String strexpr=yp.getSQL();	
				String extMoneyExpr="";//本币计算表达式
				if (!"".equals(c_extitemSQL)){
					yp.run(c_extitemSQL,this.conn,"","salaryhistory");
					extMoneyExpr=yp.getSQL();	
				}
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
				String group = "";
				if(c_group!=null&&c_group.length()>0){
					group += ","+c_group;
				}				
				for(int k=0;k<voucherGroupList.size();k++)
				{
					int groupCount=1;
					int recordNum=1;
					LazyDynaBean voucherGroupBean=null ;
					voucherGroupBean=(LazyDynaBean)voucherGroupList.get(k);
					String sub_sql="";
					if(voucherGroupBean.getMap().size()>0)
					{
						sub_sql=getCollectConditionStr(voucherGroupBean,collect_fields);
					}
					String pz_id = "";
					if(pz_idMap.get(k+"")!=null){
						pz_id=(String)pz_idMap.get(k+"");
					}else{
						pz_id=idg.getId("GZ_WarrantRecord.pz_id"); 
						pz_idMap.put(k+"", pz_id);
					}
					String sql_sub=" pn_id="+voucher_id+" and "+Sql_switcher.year("dbill_Date")+"="+year;
					sql_sub+=" and "+Sql_switcher.month("dbill_Date")+"="+month+" and deptcode='"+deptcode.substring(2)+"'";
					if("all".equalsIgnoreCase(count))
						sql_sub+=" and dbill_times=0";
					else
						sql_sub+=" and dbill_times="+count;
					if(voucherGroupBean.getMap().size()>0)
					{
						sql_sub+=sub_sql;
					}
					HashMap<String,String> fieldmap = new HashMap<String,String>();
					if(datamap.containsKey(fl_id))
						 fieldmap = (HashMap<String, String>)datamap.get(fl_id);
					ArrayList groupList= new ArrayList();
					ArrayList flgroupList=getGroupList(filter_str+strfilter+sub_sql,group,strexpr,fieldmap,rateItem.toLowerCase(),extMoneyExpr,extSql,extList);			
	
					dao.update("delete gz_warrantrecord where "+sql_sub+" and fl_id="+fl_id+" and ( state='0' or state='3' )");
					for(int i=0;i<flgroupList.size();i++)
					{
							LazyDynaBean groupBean=(LazyDynaBean)flgroupList.get(i);
							recordNum++;
							LazyDynaBean c_groupBean = groupBean;

							double value=Double.parseDouble((String)groupBean.get("he")==null?"0":(String)groupBean.get("he"));//xiegh 20170425 bug22706
							BigDecimal valueBig = new BigDecimal(value);
							int compare=valueBig.compareTo(zero);//1,0,-1
							if(compare==0){
								//=0的数据就不出现拉
								groupCount++;
							}
							else
							{
								RecordVo vo=new RecordVo("gz_warrantrecord");
								int id = Integer.parseInt(idg.getId("GZ_WarrantRecord.id"));
								vo.setInt("id", id);
								vo.setString("pz_id", pz_id);
								vo.setInt("pn_id",Integer.parseInt(voucher_id));
								vo.setInt("fl_id",Integer.parseInt(fl_id));
								vo.setString("salary_id",c_scope);
								
								vo.setDate("dbill_date", getDate.getTime());
								vo.setDate("voucher_date", _voucher_date.getTime());
								if("all".equalsIgnoreCase(count))
									vo.setInt("dbill_times",0);
								else
									vo.setInt("dbill_times", Integer.parseInt(count));
								vo.setString("c_type", voucherVo.getString("c_type"));
								vo.setString("c_subject", c_subject);  //科目
								//if(compare==1){
									vo.setString("n_loan", n_loan); //借贷方向
									vo.setDouble("money", Double.parseDouble(PubFunc.round(String.valueOf(value),2)));
						/*		}else if(compare==-1){
									if(clientName.equalsIgnoreCase("TDK")){
										if(n_loan.equals("借"))
									    	vo.setString("n_loan", "贷"); //借贷方向
										else if(n_loan.equals("贷"))
											vo.setString("n_loan", "借"); //借贷方向
									}else
										vo.setString("n_loan", n_loan); //借贷方向
									if(clientName.equalsIgnoreCase("QLYH")||clientName.equalsIgnoreCase("GXYC")||clientName.equalsIgnoreCase("CQYC")){  //齐鲁银行 2014-7-1 邓灿   广西玉柴  zhaoxg 2015-12-24 重庆烟草
										vo.setDouble("money", Double.parseDouble(PubFunc.round(String.valueOf(value*1),2)));
									}
									else
										vo.setDouble("money", Double.parseDouble(PubFunc.round(String.valueOf(value*-1),2)));
									
								}*/
								vo.setString("deptcode", deptcode.substring(2));
								vo.setString("state","0");
								//处理特殊的序号字段  ,这两个字段必须为凭证项目字段   
								FieldItem flSeqitem=DataDictionary.getFieldItem("flseq");
								FieldItem gpSeqItem=DataDictionary.getFieldItem("gpseq");
								for(int e=0;e<voucherItems.length;e++)
								{
									if(voucherItems[e].trim().length()>0)
									{
										String itemid=voucherItems[e].trim().toLowerCase();
										if(flSeqitem!=null&& "flseq".equalsIgnoreCase(itemid))
										{
											int vv=j+1;
											if(bean.get("flseq")!=null&&((String)bean.get("flseq")).length()>0)
											{
												vv=Integer.parseInt(((String)bean.get("flseq")));
											}
											vo.setInt("flseq",vv-seq_increase);
											if(currFlSeq==vv-seq_increase){
												if(gpSeqItem!=null){
											     	vo.setInt("gpseq", currGpSeq);
												    currGpSeq++;
												}
											}
											else{
												currFlSeq=vv-seq_increase;
												currGpSeq=1;
												if(gpSeqItem!=null){
											    	vo.setInt("gpseq", currGpSeq);
											    	currGpSeq++;
												}
											}
										}else if(gpSeqItem!=null&& "gpseq".equalsIgnoreCase(itemid)){//单独显示组内序号，可作为自动增长序号字段
											vo.setInt("gpseq", currGpSeq);
											currGpSeq++;
										}
										if(",n_loan,fl_id,pn_id,c_subject,c_itemsql,c_where,flseq,gpseq,c_group,".indexOf(","+itemid+",")!=-1)
											continue;
										
										if(groupBean!=null&&groupBean.get(itemid)!=null)
										{
											if(((String)groupBean.get(itemid)).trim().length()>0)
											{
												FieldItem item=DataDictionary.getFieldItem(itemid);
												if("A".equalsIgnoreCase(item.getItemtype())|| "M".equalsIgnoreCase(item.getItemtype()))
												{
													vo.setString(itemid,(String)groupBean.get(itemid));
												}
												else if("N".equalsIgnoreCase(item.getItemtype()))
												{
													String _value=(String)groupBean.get(itemid);
													if(item.getDecimalwidth()==0)
														vo.setInt(itemid,Integer.parseInt(_value));
													else
														vo.setDouble(itemid,Double.parseDouble(_value));
												}
												else if("D".equalsIgnoreCase(item.getItemtype()))
												{
													String _value=(String)groupBean.get(itemid);
													String[] _temps=_value.split("-");
													Calendar d=Calendar.getInstance();
													d.set(Calendar.YEAR,Integer.parseInt(_temps[0]));
													d.set(Calendar.MONTH,Integer.parseInt(_temps[1])-1);
													d.set(Calendar.DATE,Integer.parseInt(_temps[2]));
													vo.setDate(itemid,d.getTime());
												}
											} 
										}else if(c_groupBean!=null&&c_groupBean.get(itemid)!=null){
											if(((String)c_groupBean.get(itemid)).trim().length()>0)
											{
												FieldItem item=DataDictionary.getFieldItem(itemid);
												if("A".equalsIgnoreCase(item.getItemtype())|| "M".equalsIgnoreCase(item.getItemtype()))
												{
													vo.setString(itemid,(String)c_groupBean.get(itemid));
												}
												else if("N".equalsIgnoreCase(item.getItemtype()))
												{
													String _value=(String)c_groupBean.get(itemid);
													if(item.getDecimalwidth()==0)
														vo.setInt(itemid,Integer.parseInt(_value));
													else
														vo.setDouble(itemid,Double.parseDouble(_value));
												}
												else if("D".equalsIgnoreCase(item.getItemtype()))
												{
													String _value=(String)c_groupBean.get(itemid);
													String[] _temps=_value.split("-");
													Calendar d=Calendar.getInstance();
													d.set(Calendar.YEAR,Integer.parseInt(_temps[0]));
													d.set(Calendar.MONTH,Integer.parseInt(_temps[1])-1);
													d.set(Calendar.DATE,Integer.parseInt(_temps[2]));
													vo.setDate(itemid,d.getTime());
												}
											} 
										}else if(voucherGroupBean!=null&&voucherGroupBean.get(itemid)!=null){
											/***********************处理汇总指标***********************************/
											if(((String)voucherGroupBean.get(itemid)).trim().length()>0)
											{
												FieldItem item=DataDictionary.getFieldItem(itemid);
												if("A".equalsIgnoreCase(item.getItemtype())|| "M".equalsIgnoreCase(item.getItemtype()))
												{
													vo.setString(itemid,(String)voucherGroupBean.get(itemid));
												}
												else if("N".equalsIgnoreCase(item.getItemtype()))
												{
													String _value=(String)voucherGroupBean.get(itemid);
													if(item.getDecimalwidth()==0)
														vo.setInt(itemid,Integer.parseInt(_value));
													else
														vo.setDouble(itemid,Double.parseDouble(_value));
												}
												else if("D".equalsIgnoreCase(item.getItemtype()))
												{
													String _value=(String)voucherGroupBean.get(itemid);
													String[] _temps=_value.split("-");
													Calendar d=Calendar.getInstance();
													d.set(Calendar.YEAR,Integer.parseInt(_temps[0]));
													d.set(Calendar.MONTH,Integer.parseInt(_temps[1])-1);
													d.set(Calendar.DATE,Integer.parseInt(_temps[2]));
													vo.setDate(itemid,d.getTime());
												}
											} 
										
										}
										else
										{
											/*FieldItem fielditem=DataDictionary.getFieldItem(itemid.toLowerCase());
											if(fielditem!=null&&fielditem.getItemdesc().equalsIgnoreCase("凭证抬头文本")){
												if(this.firstGroupField!=null&&!this.firstGroupField.equals("")){
													FieldItem gitem =DataDictionary.getFieldItem(this.firstGroupField.toLowerCase());
													if(gitem!=null){
														String avalue="";
														if(groupBean!=null&&groupBean.get(gitem.getItemid())!=null)
														{
															if(gitem.isCode()){
														    	avalue=format.format(getDate.getTime())+AdminCode.getCodeName(gitem.getCodesetid(), (String)groupBean.get(gitem.getItemid()))+c_name;
															}else{
																avalue=format.format(getDate.getTime())+(String)groupBean.get(gitem.getItemid())+c_name;
															}
														}else if(c_groupBean!=null&&c_groupBean.get(gitem.getItemid())!=null){
															if(gitem.isCode()){
														    	avalue=format.format(getDate.getTime())+AdminCode.getCodeName(gitem.getCodesetid(), (String)c_groupBean.get(gitem.getItemid()))+c_name;
															}else{
																avalue=format.format(getDate.getTime())+(String)c_groupBean.get(gitem.getItemid())+c_name;
															}
														}
														vo.setString(itemid,avalue);
													}
												}
											}else if(fielditem!=null&&fielditem.getItemdesc().equalsIgnoreCase("行项目文本")){
												if(this.lastGroupField!=null&&!this.lastGroupField.equals("")){
													FieldItem gitem =DataDictionary.getFieldItem(this.lastGroupField.toLowerCase());
													if(gitem!=null){
														String avalue="";
														if(groupBean!=null&&groupBean.get(gitem.getItemid())!=null)
														{
															if(gitem.isCode()){
														    	avalue=format.format(getDate.getTime())+AdminCode.getCodeName(gitem.getCodesetid(), (String)groupBean.get(gitem.getItemid()))+c_name;
															}else{
																avalue=format.format(getDate.getTime())+(String)groupBean.get(gitem.getItemid())+c_name;
															}
														}else if(c_groupBean!=null&&c_groupBean.get(gitem.getItemid())!=null){
															if(gitem.isCode()){
														    	avalue=format.format(getDate.getTime())+AdminCode.getCodeName(gitem.getCodesetid(), (String)c_groupBean.get(gitem.getItemid()))+c_name;
															}else{
																avalue=format.format(getDate.getTime())+(String)c_groupBean.get(gitem.getItemid())+c_name;
															}
														}
														vo.setString(itemid,avalue);
													}
												}
											}
											else if(fielditem!=null&&fielditem.getItemdesc().equalsIgnoreCase("会计年度"))
											{
												vo.setString(itemid,getDate.get(Calendar.YEAR)+"");
											}else if(fielditem!=null&&fielditem.getItemdesc().equalsIgnoreCase("会计期间")){
												String t=(getDate.get(Calendar.MONTH)+1)+"";
												if(t.length()==1)
													t="0"+t;
												vo.setString(itemid,t);
											}
											else*/ 
											if(bean.get(itemid)!=null&&((String)bean.get(itemid)).trim().length()>0)
											{ 
												FieldItem item=DataDictionary.getFieldItem(itemid);
												if("A".equalsIgnoreCase(item.getItemtype())|| "M".equalsIgnoreCase(item.getItemtype()))
												{
													/**************start xiegh 2017/6/3 根据摘要格式将时间加进去****************/
													String itemdesc = (String)bean.get(itemid);
													if("c_mark".equals(itemid)){
														String markyear ="";
														String markMonth = "";
														if(itemdesc.contains("[YYYY]")){
															itemdesc =itemdesc.replace("[YYYY]" ,year);
														} 
														if(itemdesc.contains("[YY]")){
															markyear=year.substring(2);
															itemdesc =itemdesc.replace("[YY]" ,markyear);
														}
														if(itemdesc.contains("[MM]")){
															markMonth=month.length()==1?"-0"+month:"-"+month;
															itemdesc =itemdesc.replace("[MM]" ,markMonth);
														}
														if(itemdesc.contains("[M]")){
															itemdesc =itemdesc.replace("[M]" ,month);
														}
														vo.setString(itemid,itemdesc);
													}else{
														vo.setString(itemid,itemdesc);
													} 
													/***************************************end*************************/
												}
												else if("N".equalsIgnoreCase(item.getItemtype()))
												{
													String _value=(String)bean.get(itemid);
													if(item.getDecimalwidth()==0)
														vo.setInt(itemid,Integer.parseInt(_value));
													else
														vo.setDouble(itemid,Double.parseDouble(_value)); 
												}
												else if("D".equalsIgnoreCase(item.getItemtype()))
												{
													String _value=(String)bean.get(itemid);
													String[] _temps=_value.split("-");
													Calendar d=Calendar.getInstance();
													d.set(Calendar.YEAR,Integer.parseInt(_temps[0]));
													d.set(Calendar.MONTH,Integer.parseInt(_temps[1])-1);
													d.set(Calendar.DATE,Integer.parseInt(_temps[2]));
													vo.setDate(itemid,d.getTime());
												}
											}
										} 
									}	
								} 
								dataList.add(vo);
							}
					}
				
					if(recordNum==groupCount)
						seq_increase++; 
				}
				dao.addValueObject(dataList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
	}

		//判断字符串首字母是否是中文
		public static boolean isChinese(String c) {
			if(null==c)
				return false;
			
			char b = c.charAt(0);
		    return b >= 0x4E00 &&  b <= 0x9FA5;// 根据字节码判断
		}
	
	private String getItemId(String value,Connection con) {
		RowSet rs = null;
		String itemid ="";
		try{
			ContentDAO dao = new ContentDAO(con);
			String sql = "select * from fielditem where itemdesc = '"+value.replace("[", "").replace("]", "").trim()+"'";
			rs = dao.search(sql);
			while(rs.next()){
				itemid = rs.getString("itemid");
			}
		}catch(Exception e){
			e.printStackTrace();;
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return itemid;
	}
	
	
	/**
	 * 查询薪资类别中的指标列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getGzFieldList(RecordVo voucherVo)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		RowSet rset=null;
		try
		{
			String c_scope=voucherVo.getString("c_scope");
			String[] temps=c_scope.split(",");
			String salaryids="";
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i].trim().length()>0)
				{
					salaryids+=","+temps[i];
				}
			}
			
			HashMap map=new HashMap();
			StringBuffer buf=new StringBuffer();
			buf.append("select * from salaryset where salaryid in ("+salaryids.substring(1)+")");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				String itemid=rset.getString("itemid");
				if(map.get(itemid.toLowerCase())!=null)
					continue;
				FieldItem item=new FieldItem();
				item.setFieldsetid(rset.getString("fieldsetid"));
				item.setItemid(rset.getString("itemid"));
				item.setItemdesc(rset.getString("itemdesc"));
				item.setItemtype(rset.getString("itemtype"));
				item.setItemlength(rset.getInt("itemlength"));
				item.setDisplaywidth(rset.getInt("nwidth"));
				item.setDecimalwidth(rset.getInt("decwidth"));
				item.setCodesetid(rset.getString("codesetid"));
				item.setFormula(Sql_switcher.readMemo(rset,"formula"));
				item.setVarible(0);
				fieldlist.add(item);
				map.put(itemid.toLowerCase(),"1");
					 
			}//while loop end.
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return fieldlist;
	}
	
	
	
	
	/**
	 * 获得汇总指标的sql语句
	 * @param bean
	 * @param collect_fields
	 * @return
	 */
    private String getCollectConditionStr(LazyDynaBean bean,String collect_fields)
    {
    	StringBuffer sql=new StringBuffer("");
    	
    	String[] temps=collect_fields.split(",");
    	for(int i=0;i<temps.length;i++)
		{
			if(temps[i].trim().length()>0)
			{
				String value=(String)bean.get(temps[i].trim().toLowerCase());
				FieldItem item=DataDictionary.getFieldItem(temps[i].trim().toLowerCase());
				if("A".equalsIgnoreCase(item.getItemtype())|| "M".equalsIgnoreCase(item.getItemtype()))
				{
					if(value.length()>0)
						sql.append(" and "+temps[i]+"='"+value+"'");
					else
						sql.append(" and nullif("+temps[i]+",'') is null ");
				}
				else if("N".equalsIgnoreCase(item.getItemtype()))
				{
					if(value.length()>0)
						sql.append(" and "+temps[i]+"="+value+"");
					else
						sql.append(" and "+temps[i]+" is null ");
				}
				else if("D".equalsIgnoreCase(item.getItemtype()))
				{ 
					if(value.length()>0)
					{
						sql.append(" and "+Sql_switcher.year(temps[i])+"="+value.split("-")[0]);
						sql.append(" and "+Sql_switcher.month(temps[i])+"="+value.split("-")[1]);
						sql.append(" and "+Sql_switcher.day(temps[i])+"="+value.split("-")[2]);
					}
					else
						sql.append(" and "+temps[i]+" is null ");
				}
				
				
			}
		}
    	
    	return sql.toString();
    }
	
	
	/**
	 * @param filter_sql 过滤条件
	 * @param collect_fields //分组汇总
	 * @param strexpr  原币计算公式
	 * @param fieldMap 取自于薪资项目辅助核算项目
	 * @param rateItem 汇率指标
	 * @param extMoneyExpr 本币计算公式的表达式
	 * @return
	 */
	private ArrayList getGroupList(String filter_sql,String collect_fields,String strexpr,HashMap<String,String> fieldMap,String rateItem,String extMoneyExpr,String extSql,List<String> extList)
	{  
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String _str="";
			StringBuffer group = new StringBuffer();
			String[] temps=collect_fields.split(",");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i].trim().length()>0)
				{
					FieldItem item=DataDictionary.getFieldItem(temps[i].trim().toLowerCase());
					if("A".equalsIgnoreCase(item.getItemtype())){
						_str+=",nullif("+temps[i]+",'') "+temps[i];
						group.append(",nullif("+temps[i]+",'')");
					}else{
						_str+=","+temps[i];
						group.append(","+temps[i]);
					}
				}
			} 
			
			/************start**xiegh**************/
			//从薪资项目中取值的辅助核算项目
			String checkitem = "";
			for(Entry<String, String> map : fieldMap.entrySet()){
				checkitem=checkitem+  " max("+map.getValue()+") as "+map.getValue()+",";
			}
			//汇率指标
			String rateItemSQL="";
			if(!"".equals(rateItem)){
				rateItemSQL=Sql_switcher.isnull("max("+rateItem+")","1" )+" as exch_rate,";
			}
			//本币计算公式
			String extMoneySQL="";
			if(!"".equals(extMoneyExpr)){
				extMoneySQL=Sql_switcher.isnull("sum("+extMoneyExpr+")","0" )+" as ext_money,";
			}
			/***************end*************/
			
			String sql="select ";
			if (_str.length()>0) {
				sql =sql + " distinct "+_str.substring(1)+",";
			}
			sql=sql+rateItemSQL+checkitem+extSql+extMoneySQL+Sql_switcher.isnull("sum("+strexpr+")","0" )+" as he from salaryhistory where 1=1 "+filter_sql;
			if (group.length()>0){
				sql=sql+ " group by "+group.substring(1);
			}
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)//处理分组指标
					{
						FieldItem item=DataDictionary.getFieldItem(temps[i].trim().toLowerCase());
						if("N".equalsIgnoreCase(item.getItemtype())|| "A".equalsIgnoreCase(item.getItemtype()))
						{
							if(rowSet.getString(temps[i])!=null)
							{
								abean.set(temps[i].toLowerCase(),rowSet.getString(temps[i])==null?"":rowSet.getString(temps[i]));
							}
							else
							{
								abean.set(temps[i].toLowerCase(),"");
							}
						}
						else if("D".equalsIgnoreCase(item.getItemtype()))
						{
							if(rowSet.getDate(temps[i])!=null)
							{
								abean.set(temps[i].toLowerCase(),fm.format(rowSet.getDate(temps[i])));
							}
							else
							{
								abean.set(temps[i].toLowerCase(),"");
							}
						}
						else if("M".equalsIgnoreCase(item.getItemtype()))
						{
							abean.set(temps[i].toLowerCase(),Sql_switcher.readMemo(rowSet, temps[i]));
						}
					}
				}
				
				/***********************start*****************************/
				for(Entry<String, String> map : fieldMap.entrySet()){
					String value = rowSet.getString(map.getValue());
					String str ="";
					if(null!=value){
						if(map.getKey().contains("-")){
							FieldItem item = DataDictionary.getFieldItem(value);
							if (null!=item && !"0".equals( item.getCodesetid())){//代码指标
								CodeItem umitem = AdminCode.getCode(item.getCodesetid(), value);
								str = umitem.getCodename();
							}
							CodeItem unitem=null;
							if (null==item) {
								unitem= AdminCode.getCode("UN", value);
							}
							
							if (unitem !=null) {
								str = unitem.getCodename();
							}else{
								 unitem = AdminCode.getCode("UM", value);
								 str = unitem.getCodename();
							}
							abean.set(map.getKey().replace("-", ""),str);//有“-”表示去名称，没有则取code
						}
						else {
							abean.set(map.getKey(),value);
						}
					}
				}
				
				//放开项目设置后  对不同的项目对不同的赋值操作
				for(String str : extList){
					if(str.trim().length()>0)
					{
						FieldItem item=DataDictionary.getFieldItem(str.trim().toLowerCase());
						if("N".equalsIgnoreCase(item.getItemtype())|| "A".equalsIgnoreCase(item.getItemtype()))
						{
							if(rowSet.getString(str)!=null)
							{
								abean.set(str.toLowerCase(),rowSet.getString(str)==null?"":rowSet.getString(str));
							}
							else
							{
								abean.set(str.toLowerCase(),"");
							}
						}
						else if("D".equalsIgnoreCase(item.getItemtype()))
						{
							if(rowSet.getDate(str)!=null)
							{
								abean.set(str.toLowerCase(),fm.format(rowSet.getDate(str)));
							}
							else
							{
								abean.set(str.toLowerCase(),"");
							}
						}
						else if("M".equalsIgnoreCase(item.getItemtype()))
						{
							abean.set(str.toLowerCase(),Sql_switcher.readMemo(rowSet, str));
						}
					}
					//abean.set(str, rowSet.getString(str));
				}
				
				/***********************end******************************/
				//原币金额
				abean.set("he", rowSet.getString("he"));
				
				//写入汇率指标及本币金额值。
				String exch_rate="";
				if(!"".equals(rateItem)){
					exch_rate = rowSet.getString("exch_rate");//汇率
				}
				if (exch_rate==null || "".equals(exch_rate)) exch_rate="1";
				abean.set("exch_rate", exch_rate);
				if(!"".equals(extMoneyExpr)){//如自定义的了本币计算公式,按计算公式计算
					abean.set("ext_money", rowSet.getString("ext_money"));
				}
				else
				{//如未自定义本币计算公式 按照以下公式计算：本币=原币*汇率
					BigDecimal exch_rate_dec = new BigDecimal(exch_rate);
					BigDecimal exch_rate_he = new BigDecimal(rowSet.getString("he"));
					abean.set("ext_money", exch_rate_dec.multiply(exch_rate_he)+"");//本币金额=汇率*原币金额； xiegh	
				}
			
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	private String orgid="";//归属单位
	private String deptid="";//归属部门
	private boolean isMultiple=false; //凭证含有多个薪资帐套数据,帐套里的归属单位或部门设置的不同 
	/**
	 * 获得凭证统计数据范围sql语句
	 * @param voucherVo
	 * @return
	 */
	private String getFilter_str(RecordVo voucherVo,String year,String month,String count,String deptcode,String type)
	{
		StringBuffer str=new StringBuffer("");
		StringBuffer buf=new StringBuffer("");
		String c_dbase=voucherVo.getString("c_dbase");
		String c_scope=voucherVo.getString("c_scope");
		String[] temps=c_scope.split(",");
		SalaryCtrlParamBo ctrlparam=null;
		 
		for(int i=0;i<temps.length;i++)
		{
			if(temps[i].trim().length()>0)
			{
				String salaryid=temps[i].trim();
				if(!this.userView.isHaveResource(IResourceConstant.GZ_SET, salaryid)&&!this.userView.isHaveResource(IResourceConstant.INS_SET,salaryid))
					continue;
				ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(salaryid));
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");  //归属单位
				orgid = orgid != null ? orgid : "";
				if(orgid.length()>0)
				{
					if(this.orgid!=null&&this.orgid.trim().length()>0&&!this.orgid.equalsIgnoreCase(orgid))
						isMultiple=true;
					this.orgid=orgid;
				}
				String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid"); //归属部门
				deptid = deptid != null ? deptid : "";
				if(deptid.length()>0)
				{
					if(this.deptid!=null&&this.deptid.trim().length()>0&&!this.deptid.equalsIgnoreCase(deptid))
						isMultiple=true;
					this.deptid=deptid;
				}
				StringBuffer tempBuf=new StringBuffer("");
				tempBuf.append(""); 
				if(this.userView.isSuper_admin())
				{
					buf.append(" or  ( salaryid="+salaryid); 		    	        		 
					buf.append(" and ( case");
					if(deptid.length()>0&&orgid.length()>0){//归属单位和部门均设置了
						buf.append("  when  nullif("+deptid+",'') is not null  then "+deptid+" ");
						buf.append("  when (nullif("+deptid+",'') is  null ) and nullif("+orgid+",'') is not null then "+orgid+" ");
						buf.append("  when (nullif("+deptid+",'') is  null ) and (nullif("+orgid+",'') is null) and nullif(e0122,'') is not null then e0122 ");
						buf.append(" else b0110 end ");
						buf.append(" like '"+deptcode.substring(2)+"%' ");
						buf.append(") ");
					}else if(deptid.length()>0){//设置了归属部门，没设置归属单位
						buf.append("  when nullif("+deptid+",'') is not null then "+deptid+" ");
						buf.append("  when (nullif("+deptid+",'') is  null) and nullif(e0122,'') is not null then e0122 ");
						buf.append(" else b0110 end ");
						buf.append(" like '"+deptcode.substring(2)+"%' ");
						buf.append(") ");
					}else if(orgid.length()>0){//没设置归属部门，设置了归属单位
						buf.append("  when nullif("+orgid+",'') is not null then "+orgid+" ");
						buf.append("  when (nullif("+orgid+",'') is null) and nullif(e0122,'') is not null then e0122 ");
						buf.append(" else b0110 end ");
						buf.append(" like '"+deptcode.substring(2)+"%' ");
						buf.append(") ");
					}else{//啥都没设置
						buf.append("  when nullif(e0122,'') is not null then e0122 ");
						buf.append(" else b0110 end ");
						buf.append(" like '"+deptcode.substring(2)+"%' ");
						buf.append(") ");
					}
					buf.append(" ) ");
					
				}
				else
				{
					buf.append(" or ( salaryid="+salaryid); 
					String b_units=this.userView.getUnitIdByBusiOutofPriv("1");
					if(b_units!=null&&b_units.length()>2&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units)) //模块操作单位
					{
						buf.append(" and ");
						String unitarr[] =b_units.split("`");	
						for(int n=0;n<unitarr.length;n++)
						{
		    				String codeid=unitarr[n];
		    				if(codeid==null|| "".equals(codeid))
		    					continue;
			    			if(codeid!=null&&codeid.trim().length()>2)
		    				{
			    				String privCode = codeid.substring(0,2);
			    				String privCodeValue = codeid.substring(2);	
			    				buf.append(" ( case");
								if(deptid.length()>0&&orgid.length()>0){//归属单位和部门均设置了
									buf.append("  when  nullif("+deptid+",'') is not null  then "+deptid+" ");
									buf.append("  when (nullif("+deptid+",'') is  null ) and nullif("+orgid+",'') is not null then "+orgid+" ");
									buf.append("  when (nullif("+deptid+",'') is  null ) and (nullif("+orgid+",'') is null) and nullif(e0122,'') is not null then e0122 ");
									buf.append(" else b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if(deptid.length()>0&&orgid.length()>0){//设置了归属部门，没设置归属单位
									buf.append("  when nullif("+deptid+",'') is not null then "+deptid+" ");
									buf.append("  when (nullif("+deptid+",'') is  null) and nullif(e0122,'') is not null then e0122 ");
									buf.append(" else b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if(orgid.length()>0){//没设置归属部门，设置了归属单位//add by xiegh on date 20180208 bug34752
									buf.append("  when nullif("+orgid+",'') is not null then "+orgid+" ");
									buf.append("  when (nullif("+orgid+",'') is null) and nullif(e0122,'') is not null then e0122 ");
									buf.append(" else b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");//add by xiegh on date 20180213  拼接的sql有问题
								}else{//啥都没设置
									buf.append("  when nullif(e0122,'') is not null then e0122 ");
									buf.append(" else b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}
		    				}
						}
		    		}
					else 
						continue;
					String _str = buf.toString();
					buf.setLength(0);
					buf.append(_str.substring(0, _str.length()-3));
					buf.append(" ) ");

					if(StringUtils.isNotBlank(deptcode)){
						buf.append(" and ( case");
						if(deptid.length()>0&&orgid.length()>0){//归属单位和部门均设置了
							buf.append("  when  nullif("+deptid+",'') is not null  then "+deptid+" ");
							buf.append("  when (nullif("+deptid+",'') is  null ) and nullif("+orgid+",'') is not null then "+orgid+" ");
							buf.append("  when (nullif("+deptid+",'') is  null ) and (nullif("+orgid+",'') is null) and nullif(e0122,'') is not null then e0122 ");
							buf.append(" else b0110 end ");
							buf.append(" like '"+deptcode.substring(2)+"%' ");
							buf.append(") ");
						}else if(deptid.length()>0){//设置了归属部门，没设置归属单位
							buf.append("  when nullif("+deptid+",'') is not null then "+deptid+" ");
							buf.append("  when (nullif("+deptid+",'') is  null) and nullif(e0122,'') is not null then e0122 ");
							buf.append(" else b0110 end ");
							buf.append(" like '"+deptcode.substring(2)+"%' ");
							buf.append(") ");
						}else if(orgid.length()>0){//没设置归属部门，设置了归属单位
							buf.append("  when nullif("+orgid+",'') is not null then "+orgid+" ");
							buf.append("  when (nullif("+orgid+",'') is null) and nullif(e0122,'') is not null then e0122 ");
							buf.append(" else b0110 end ");
							buf.append(" like '"+deptcode.substring(2)+"%' ");
							buf.append(") ");
						}else{//啥都没设置
							buf.append("  when nullif(e0122,'') is not null then e0122 ");
							buf.append(" else b0110 end ");
							buf.append(" like '"+deptcode.substring(2)+"%' ");
							buf.append(") ");
						}
					}
				}
			}
		}
		 
		if(buf.length()==0)
			str.append(" and 1=2 ");
		else
		{
			if(c_dbase!=null&&c_dbase.trim().length()>0)
			{
				str.append(" and ("+buf.substring(3)+")  and  ");
				String[] nbase_temps=c_dbase.split(",");
				StringBuffer str2=new StringBuffer("");
				for(int i=0;i<nbase_temps.length;i++)
				{
					if(nbase_temps[i].trim().length()>0)
					{
						str2.append(" or lower(nbase)='"+nbase_temps[i].trim().toLowerCase()+"' ");
					}
				}
				str.append(" ("+str2.substring(3)+")   ");
			}
			else
			{
				str.append(" and 1=2 ");
			}
			
		}  
		str.append(" and  sp_flag='06' "); //已提交
		str.append(" and "+Sql_switcher.year("a00z2")+"="+year);
		str.append(" and "+Sql_switcher.month("a00z2")+"="+month);
		if(!"all".equals(count)&& "1".equals(type))
			str.append(" and a00z3="+count);
		return str.toString();
	}
	
	/**
	 * 获得凭证统计数据范围sql语句
	 * @param voucherVo
	 * @return
	 */
	private String getFilter_str2(RecordVo voucherVo,String year,String month,String count,String deptcode,String type)
	{
		StringBuffer str=new StringBuffer("");
		StringBuffer buf=new StringBuffer("");
		String c_dbase=voucherVo.getString("c_dbase");
		String c_scope=voucherVo.getString("c_scope");
		String[] temps=c_scope.split(",");
		SalaryCtrlParamBo ctrlparam=null;
		 
		for(int i=0;i<temps.length;i++)
		{
			if(temps[i].trim().length()>0)
			{
				String salaryid=temps[i].trim(); 
				
				if(!this.userView.isHaveResource(IResourceConstant.GZ_SET, salaryid)&&!this.userView.isHaveResource(IResourceConstant.INS_SET,salaryid))
					continue;
				
				ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(salaryid));
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");  //归属单位
				orgid = orgid != null ? orgid : "";
				if(orgid.length()>0)
				{
					if(this.orgid!=null&&this.orgid.trim().length()>0&&!this.orgid.equalsIgnoreCase(orgid))
						isMultiple=true;
					this.orgid=orgid;
				}
				String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid"); //归属部门
				deptid = deptid != null ? deptid : "";
				if(deptid.length()>0)
				{
					if(this.deptid!=null&&this.deptid.trim().length()>0&&!this.deptid.equalsIgnoreCase(deptid))
						isMultiple=true;
					this.deptid=deptid;
				}
				StringBuffer tempBuf=new StringBuffer("");
				tempBuf.append(""); 
				if(this.userView.isSuper_admin())
				{
					 buf.append(" or  ( salaryid="+salaryid); 
					 if("UN".equalsIgnoreCase(deptcode.substring(0,2)))
		    	     {
		    	        		 if(orgid.length()>0)
		    	        			 buf.append(" and "+orgid+" = '"+deptcode.substring(2)+"'");
		    	        		 else 
		    	        			 buf.append(" and b0110 = '"+deptcode.substring(2)+"'");
		    	      }
		    	      else if("UM".equalsIgnoreCase(deptcode.substring(0,2)))
		    	      {
		    	        		 if(deptid.length()>0)
		    	        			 buf.append(" and "+deptid+" = '"+deptcode.substring(2)+"'");
		    	        		 else
		    	        			 buf.append(" and e0122 = '"+deptcode.substring(2)+"'");
		    	        		 
		    	       } 
					  buf.append(" ) ");
					
				}
				else
				{
					String a_code=this.userView.getUnit_id();
					if(a_code!=null&&a_code.trim().length()>0)
		    		{
						 buf.append(" or ( salaryid="+salaryid); 
						 StringBuffer buf2=new StringBuffer("");
						 String unitarr[] = a_code.split("`"); 
		    	         for(int j=0;j<unitarr.length;j++){
			                 String codeid = unitarr[j];
			                 if(codeid!=null&&codeid.trim().length()>2){
			                	 if(deptid.length()>0||orgid.length()>0)
			                	 {
				                	 if(deptid.length()>0)
				                		 buf2.append(" or "+deptid+" like '"+codeid.substring(2)+"%'");
				                	 if(orgid.length()>0)
				                		 buf2.append(" or "+orgid+" like '"+codeid.substring(2)+"%'");
			                	 }
			                	 else
			                	 {
			                		 buf2.append(" or e0122 like '"+codeid.substring(2)+"%'");
			                	 }
			                	 
			                	 
			                 }else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){

 
			                 }
			             }
		    	         
		    	        
		    	        if("UN".equalsIgnoreCase(deptcode.substring(0,2)))
		    	        {
		    	        		if(orgid.length()>0)
		    	        			 buf.append(" and "+orgid+" = '"+deptcode.substring(2)+"'");
		    	        		 else 
		    	        			 buf.append(" and b0110 = '"+deptcode.substring(2)+"'");
		    	        }
		    	        else if("UM".equalsIgnoreCase(deptcode.substring(0,2)))
		    	        {
		    	        		 if(deptid.length()>0)
		    	        			 buf.append(" and "+deptid+" = '"+deptcode.substring(2)+"'");
		    	        		 else
		    	        			 buf.append(" and e0122 = '"+deptcode.substring(2)+"'");
		    	        		 
		    	        } 
		    	         if(buf2.length()>0)
		    	        	 buf.append(" and ("+buf2.substring(3)+") )");
		    	         else
		    	        	 buf.append(" ) ");
		    	          
		    		}
					else if(userView.getManagePrivCode()!=null&&userView.getManagePrivCode().trim().length()>0)
					{
						buf.append(" or ( salaryid="+salaryid); 
		    	        if("UN".equalsIgnoreCase(deptcode.substring(0,2)))
		    	        {
		    	        		 if(orgid.length()>0)
		    	        			 buf.append(" and "+orgid+" = '"+deptcode.substring(2)+"'");
		    	        		 else 
		    	        			 buf.append(" and b0110 = '"+deptcode.substring(2)+"'");
		    	        }
		    	        else if("UM".equalsIgnoreCase(deptcode.substring(0,2)))
		    	        {
		    	        		 if(deptid.length()>0)
		    	        			 buf.append(" and "+deptid+" = '"+deptcode.substring(2)+"'");
		    	        		 else
		    	        			 buf.append(" and e0122 = '"+deptcode.substring(2)+"'");
		    	        		 
		    	        }  
						
						 String code=userView.getManagePrivCodeValue();
						 if(code==null|| "".equals(code))
							  buf.append(" ) ");
						 else
						 {
							 if("UN".equalsIgnoreCase(userView.getManagePrivCode()))
								 buf.append(" and b0110 like '"+code+"%' )");
							 else if("UM".equalsIgnoreCase(userView.getManagePrivCode()))
								 buf.append(" and e0122 like '"+code+"%' )");
						 } 
					}
					else 
						continue;
				} 
			}
		}
	 
		
		if(buf.length()==0)
			str.append(" and 1=2 ");
		else
		{
			if(c_dbase!=null&&c_dbase.trim().length()>0)
			{
				str.append(" and ("+buf.substring(3)+")  and  ");
				String[] nbase_temps=c_dbase.split(",");
				StringBuffer str2=new StringBuffer("");
				for(int i=0;i<nbase_temps.length;i++)
				{
					if(nbase_temps[i].trim().length()>0)
					{
						str2.append(" or lower(nbase)='"+nbase_temps[i].trim().toLowerCase()+"' ");
					}
				}
				str.append(" ("+str2.substring(3)+")   ");
			}
			else
			{
				str.append(" and 1=2 ");
			}
		}  
		str.append(" and  sp_flag='06' "); //已提交
		str.append(" and "+Sql_switcher.year("a00z2")+"="+year);
		str.append(" and "+Sql_switcher.month("a00z2")+"="+month);
		if(!"all".equals(count)&& "1".equals(type))
			str.append(" and a00z3="+count);
		return str.toString();
	}
	/**
	 * 	取得	凭证分录明细
	 * @param voucher_id
	 * @return
	 */
	private ArrayList getWarrantList(String voucher_id)
	{
		ArrayList list=new ArrayList();
		try
		{ 
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from GZ_WarrantList where pn_id='"+voucher_id+"'");
			
			ArrayList fieldList=DataDictionary.getFieldList("GZ_WARRANTLIST",Constant.USED_FIELD_SET);  
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				for(int i=0;i<fieldList.size();i++)
				{
					FieldItem item=(FieldItem)fieldList.get(i);
					String itemid=item.getItemid().toLowerCase();
					if(",pz_id,salary_id,dbill_date,voucher_date,dbill_times,c_type,busi_id,deptcode,personname,state,money,".indexOf(","+itemid+",")!=-1)
						continue; 
					if("A".equalsIgnoreCase(item.getItemtype())|| "N".equalsIgnoreCase(item.getItemtype()))
					{
						if(rowSet.getString(itemid)!=null)
							abean.set(itemid,rowSet.getString(itemid));
						else
							abean.set(itemid,"");
					}
					else if("D".equalsIgnoreCase(item.getItemtype()))
					{
						if(rowSet.getDate(itemid)!=null)
							abean.set(itemid,df.format(rowSet.getDate(itemid)));
						else
							abean.set(itemid,"");
					}
					else if("M".equalsIgnoreCase(item.getItemtype()))
						abean.set(itemid, Sql_switcher.readMemo(rowSet, itemid));
				}
				abean.set("c_group",rowSet.getString("c_group")==null?"":rowSet.getString("c_group"));//分录分组指标
				abean.set("seq", rowSet.getString("seq")+"");
				list.add(abean);
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			 
		}
		return list;
	}
	
    /**
     * Description: 删除凭证数据
     * @Version1.0 
     * Aug 15, 2012 9:40:18 AM Jianghe created
     * @param matters
     * @return
     */
    public String delDataValue(String[] matters)
    {
    	String msg="ok";
    	RowSet rs = null;
    	try
		{
			ContentDAO dao = new ContentDAO(this.conn);
		
			StringBuffer ids = new StringBuffer();
			for (int i = 0; i < matters.length; i++)
			{
			    ids.append(matters[i]);
			    ids.append(",");
			}
			ids.setLength(ids.length() - 1);
		
			StringBuffer str = new StringBuffer();
			str.append("select id,state from GZ_WarrantRecord where id in (");
			str.append(ids.toString());
			str.append(")");			
			rs = dao.search(str.toString());
			
			StringBuffer idss = new StringBuffer();
			while(rs.next())
			{
				String state = rs.getString("state");
				if((state!=null) && (state.trim().length()>0) && ("0".equalsIgnoreCase(state) || ("3".equalsIgnoreCase(state))) )
				{
					idss.append(rs.getString("id"));
					idss.append(",");
					
				}else
				{
					msg = "error";
					break;
				}
			}
			if(!"error".equals(msg)){
				if(idss!=null && idss.toString().trim().length()>0)
				{
					idss.setLength(idss.length() - 1);
				}else
					return msg;
				
				StringBuffer strSql = new StringBuffer();
				strSql.append("delete from GZ_WarrantRecord where id in (");
				strSql.append(idss.toString());
				strSql.append(")");	
			    dao.delete(strSql.toString(), new ArrayList());
			}
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return msg;
    }	
    
    public String sendDataBySAP(String ipStr, String targetNamespace, String method, String paramName,  ArrayList dataList) 
    {

    	String flag = sendBySOAP(ipStr, targetNamespace, method, paramName, dataList);
    	
    	return handleFlag(dataList, flag);
    }
    
    
    // 齐鲁银行生成财务凭证txt文件并上传到ftp
    public String creatFileToFtp(ArrayList dataList) 
    {   	
    	String flag = creatTxtFile(dataList);
    	
    //	return flag;   	
    	return handqlBankFlag(dataList, flag);
    }
    private String creatTxtFile(ArrayList list) 
    {   	   			
    	String flag = "1";			
		// 文件输出句柄
		FileWriter writer = null;	
		RowSet rs = null;
		InputStream input = null;
		try 
		{
			// 完整文件名
			String fileNameAll = "SALA" + DateUtils.format(new Date(), "yyyyMMdd")+ ".txt";
			
			String ftpServer = SystemConfig.getPropertyValue("ftpServer"); // ftp服务器ip地址
			String ftpPort = SystemConfig.getPropertyValue("ftpPort"); // ftp服务器端口
			String ftpUserName = SystemConfig.getPropertyValue("ftpUserName"); // ftp服务器用户名
			String ftpPassWord = SystemConfig.getPropertyValue("ftpPassWord"); // ftp服务器密码
			String ftpMediaPath = SystemConfig.getPropertyValue("ftpMediaPath"); // ftp文件保存目录
			// 所属成本中心机构代码
			String jigoufield = SystemConfig.getPropertyValue("jigoufield");
			String bumenfield = SystemConfig.getPropertyValue("bumenfield"); // 所属成本中心部门代码
			
			// 生成工资计提文件的临时目录路径
			String tempDirectoryPath = SystemConfig.getPropertyValue("tempDirectoryPath");
			
			HashMap subjectMap = new HashMap();	
			// #归属到总行(1000001)退休(121)上的工资项编号
			String qiluBankField = SystemConfig.getPropertyValue("qiluBankField");
			if(qiluBankField!=null && qiluBankField.trim().length()>0)
			{
				String[] temps = qiluBankField.split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						subjectMap.put(temps[i], "hjsj");                 
					}
				}								
			}
			
			// 如果目录不存在则重新创建  windows: c:\hjsj   linux: /hrms/hjsj
			File dirFile = new File(tempDirectoryPath);
			if (!dirFile.exists()) {					
				dirFile.mkdirs();					
			}				
			// 创建导出的文件  windows: c:\hjsj   linux: /hrms/hjsj
			File file = new File(tempDirectoryPath, fileNameAll);
			writer = new FileWriter(file);
									
			// 获取单位、部门转换代码
			HashMap codeMap = corCodeMap();
			
			// 文件数据行数计数器
			int count = 0;
			StringBuffer buff = new StringBuffer();
				
			for (int i = 0; i < list.size(); i++) 
			{
				buff.delete(0, buff.length());					
				LazyDynaBean bean = (LazyDynaBean) list.get(i);	
				
				// 公司代码
		    //	String company = (String) bean.get("b0110");
		    //	String companyCode = "";
		    //	if(company!=null && company.trim().length()>0)
		    //		companyCode = (String)codeMap.get(company);
		    //	buff.append(companyCode+",");
				
				// 成本中心机构代码
				String companyCode = (String) bean.get(jigoufield.toLowerCase());
				// 成本中心部门代码
		    	String deptCode = (String) bean.get(bumenfield.toLowerCase());
		    	
		    	if((companyCode==null || companyCode.trim().length()<=0) && (deptCode==null || deptCode.trim().length()<=0))
		    	{
		    		companyCode = "1000001";
		    		deptCode = "0";
		    	}
		    			    	
		    	// 工资项目代码（即科目代码）
	    		String c_subject = (String) bean.get("c_subject");
	    		String subCode = (String)subjectMap.get(c_subject);
	    		if(subCode!=null && subCode.trim().length()>0)
	    		{
	    			companyCode = "1000001";
		    		deptCode = "121";
	    		}				
		    	
		    	buff.append(companyCode+",");		    	
		    	buff.append(deptCode+",");
		    			    	
	    		buff.append(c_subject+",");
	    		
	    		// 日期
	    		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 获得系统当前时间	
				buff.append(creatDate+",");
	    		
	    		// 金额
	    		String money = (String) bean.get("money"); 
	    		buff.append(money+",");
				
				// 批次	
	    		String batchTime = PubFunc.getStringDate("yyyyMMddHH"); // 获得系统当前时间	    		
	    		String batchCode = companyCode+batchTime;
	    		buff.append(batchCode);	    													
					
				writer.write(buff.toString());
				writer.write("\r\n");
				
				count ++;
					
			}								
			writer.flush();
			System.out.println( fileNameAll + "文件写入完成，一共写入了" + count  + "条数据");
				
				
			// 把生成的文件上传到 ftp 服务器   windows: "c:\hjsj\" + fileNameAll   linux: "/hrms/hjsj/" + fileNameAll			
			System.out.println("Linux系统文件目录分隔符：----" + System.getProperty("file.separator"));
			
			input = new FileInputStream(tempDirectoryPath + System.getProperty("file.separator") + fileNameAll);			
			System.out.println("读取Linux系统下文件----" + input);
			
		//	System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileNameAll
			FtpMediaBo ftpbo = new FtpMediaBo(ftpServer, Integer.parseInt(ftpPort), ftpUserName,ftpPassWord);				
			boolean success = ftpbo.uploadFile(ftpMediaPath, fileNameAll, input);	
			
			System.out.println("uploadFile--------" + success);			
				
			flag = "true";   // 发送成功
				
		} catch (Exception e) 
		{
			e.printStackTrace();
			flag = "1";
		} finally 
		{
			try {
				if (rs != null) {
					rs.close();
				}					
				if (writer != null) {
					writer.close();
				}	
				if (input != null) {
					PubFunc.closeIoResource(input);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			   	
    	return flag;
    }
    
    public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
    
    // 向NC发送财务凭证
    public String sendDataByNC(ArrayList dataList) 
    {
    	
    	String ncpzurl = SystemConfig.getPropertyValue("ncpzurl");    	
    	String flag = sendByClient(ncpzurl,dataList);
    	
    	return handleFlag(dataList, flag);
    }
    private String sendByClient(String ncpzurl,ArrayList list) 
    {   	
    	StringBuffer voucherXml = new StringBuffer(); // 总xml   	
    	StringBuffer headXml = new StringBuffer(); // xml头信息   	
    	StringBuffer bodyXml = new StringBuffer(); // xml体信息
    	OutputStreamWriter wr = null;
    	OutputStream outputStrem=null;
		BufferedReader rd = null;
		InputStream is = null;
		InputStreamReader isr = null;
    	String flag = "1";
    	try 
    	{
    		String companyCode = ""; // 公司代码
    		String year = ""; // 会计年度
    		String month = ""; // 会计期间
    		String pz_id = ""; // 凭证号
    		String date = ""; // 凭证日期
    		   		
    		// 薪资所属单位
			String companyfield = SystemConfig.getPropertyValue("companyfield");    		    					
			
			bodyXml.append("<voucher_body>");			
			for (int i = 0; i < list.size(); i++) 
			{				
				LazyDynaBean bean = (LazyDynaBean) list.get(i);	
				
				bodyXml.append("<entry>");
				
				// 凭证日期
				date = (String) bean.get("voucher_date".toLowerCase());								
				// 计提月份
				String dbill_Date = (String) bean.get("dbill_Date".toLowerCase());
				Date dbillDate = DateUtils.getDate(dbill_Date, "yyyy-MM-dd");    			
    			// 会计年度
    			year = DateUtils.format(dbillDate, "yyyy");    			
    			// 会计期间
    			month = DateUtils.format(dbillDate, "MM");   			
    			// 公司代码
    			String company = (String) bean.get(companyfield.toLowerCase());
    			HashMap corCodeUNMap = corCodeUNMap();
    			if(company!=null && company.trim().length()>0)
    			{
    				companyCode = (String)corCodeUNMap.get(company);
    			}    			    			
    			// 凭证号    			
    			pz_id = (String) bean.get("pz_id");   			
    			// 分录号    			
    			String fl_id = (String) bean.get("id");
    			bodyXml.append("<entry_id>"+fl_id+"</entry_id>");	
    			// 科目
    			String c_subject = (String) bean.get("c_subject");
    			bodyXml.append("<account_code>"+c_subject+"</account_code>");
    			// 摘要
    			String c_mark = (String) bean.get("c_mark");
    			bodyXml.append("<abstract>"+c_mark+"</abstract>");
    			
    			
    			bodyXml.append("<settlement></settlement>");
    			bodyXml.append("<bankcode></bankcode>");
    			bodyXml.append("<notetype></notetype>");
    			bodyXml.append("<document_id></document_id>");
    			bodyXml.append("<document_date></document_date>");
    			bodyXml.append("<currency>CNY</currency>");
    			bodyXml.append("<unit_price>0.00000000</unit_price>");
    			bodyXml.append("<exchange_rate1>0.00000000</exchange_rate1>");
    			bodyXml.append("<exchange_rate2>1.00000000</exchange_rate2>");
    			bodyXml.append("<debit_quantity>0.00000000</debit_quantity>");
				
    			// 金额
    			String money = (String) bean.get("money");   			   			
    			// 借方记账码
    			String n_loan = (String) bean.get("n_loan");
    			if ("借".equals(n_loan)) {
    			
    				bodyXml.append("<primary_debit_amount>"+money+"</primary_debit_amount>");
    				bodyXml.append("<secondary_debit_amount>0.00000000</secondary_debit_amount>");
    				bodyXml.append("<natural_debit_currency>"+money+"</natural_debit_currency>");    				
					bodyXml.append("<credit_quantity>0.00000000</credit_quantity>");  
					bodyXml.append("<primary_credit_amount>0.00000000</primary_credit_amount>");  
					bodyXml.append("<secondary_credit_amount>0.00000000</secondary_credit_amount>");  
					bodyXml.append("<natural_credit_currency>0.00000000</natural_credit_currency>");  
    			} 
    			else 
    			{
    				bodyXml.append("<primary_debit_amount>0.00000000</primary_debit_amount>");
    				bodyXml.append("<secondary_debit_amount>0.00000000</secondary_debit_amount>");
    				bodyXml.append("<natural_debit_currency>0.00000000</natural_debit_currency>");    				
					bodyXml.append("<credit_quantity>0.00000000</credit_quantity>");  
					bodyXml.append("<primary_credit_amount>"+money+"</primary_credit_amount>");  
					bodyXml.append("<secondary_credit_amount>0.00000000</secondary_credit_amount>");  
					bodyXml.append("<natural_credit_currency>"+money+"</natural_credit_currency>");
    			}
    			bodyXml.append("<bill_type></bill_type>");
    			bodyXml.append("<bill_id></bill_id>");
    			bodyXml.append("<bill_date></bill_date>");
    			
    			// 辅助核算
    			String check_item = (String) bean.get("check_item");
    			// 部门编码
    			String e0122 = (String) bean.get("e0122");
    			HashMap codeMap = corCodeMap();   			
    			if(check_item!=null && check_item.trim().length()>0 && (e0122==null || e0122.trim().length()<=0))   			
    				e0122 = "010202";   			

    			if(check_item!=null && check_item.trim().length()>0)
    			{
	    			bodyXml.append("<auxiliary_accounting>");
	    			bodyXml.append("<item name=\""+check_item+"\">"+codeMap.get(e0122)+"</item>");
	    			bodyXml.append("</auxiliary_accounting>");
    			}
    			
    			bodyXml.append("<detail></detail>");    			
    			bodyXml.append("</entry>");								
			}			
			bodyXml.append("</voucher_body>");
			
			
			ncpzurl = ncpzurl + "&receiver="+companyCode+"";
			
			headXml.append("<?xml version=\"1.0\" encoding='UTF-8'?>");
			headXml.append("<ufinterface roottag=\"voucher\" billtype=\"gl\" replace=\"Y\" receiver=\""+companyCode+"\" sender=\"98\" isexchange=\"Y\" filename=\"voucher.xml\" proc=\"add\" operation=\"req\">");
			headXml.append("<voucher id=\""+pz_id+"\">");
			headXml.append("<voucher_head>");
			headXml.append("<company>"+companyCode+"</company>");
			headXml.append("<voucher_type>记账凭证</voucher_type>");
			headXml.append("<fiscal_year>"+year+"</fiscal_year>");
			headXml.append("<accounting_period>"+month+"</accounting_period>");
			headXml.append("<voucher_id>"+pz_id+"</voucher_id>");
			headXml.append("<attachment_number>0</attachment_number>");
			headXml.append("<prepareddate>"+date+"</prepareddate>");
			headXml.append("<enter>"+this.userView.getUserName()+"</enter>");
		//	headXml.append("<enter>yangmeichao</enter>");
			headXml.append("<cashier></cashier>");
			headXml.append("<signature>N</signature>");
			headXml.append("<checker></checker>");
			headXml.append("<posting_date></posting_date>");
			headXml.append("<posting_person></posting_person>");
			headXml.append("<voucher_making_system>外部系统交换平台</voucher_making_system>");
			headXml.append("<memo1></memo1>");
			headXml.append("<memo2></memo2>");
			headXml.append("<reserve1></reserve1>");
			headXml.append("<reserve2></reserve2>");
			headXml.append("<revokeflag />");
			headXml.append("</voucher_head>");

						
			voucherXml.append(headXml.toString());
			voucherXml.append(bodyXml.toString());
			voucherXml.append("</voucher>");			
			voucherXml.append("</ufinterface>");			
			
			System.out.println("NC服务器地址-----" + ncpzurl);
			System.out.println("向NC传送的xml-----" + voucherXml.toString());
						
			URLConnection conn = getUrlConnection(ncpzurl, "v5");
			outputStrem=conn.getOutputStream();
            wr = new OutputStreamWriter(outputStrem,"UTF-8");           
            wr.write(voucherXml.toString());
            wr.flush();
         
			is = conn.getInputStream();
			isr = new InputStreamReader(is,"GB2312");
			rd = new BufferedReader(isr);
			String line;
			StringBuffer response = new StringBuffer("");
			while ((line = rd.readLine()) != null) 
			{
				response.append(line);
				response.append(System.getProperty("line.separator"));
			}
			if (response.length() == 0) 
			{
				throw new Exception("1212!");
			}
			
			//获得发送回执,必须要获取回执，否则发送不能成功
			System.out.println("回执信息:"+response.toString());
			
			
			String resultcode = "1";
			ArrayList backList = getMapList(response.toString(), "/ufinterface/sendresult");
			for (int i = 0; i < backList.size(); i++) 
			{
				Map map = (Map) backList.get(i);
				// 发送成功与否的标记
				resultcode = (String) map.get("resultcode");
			}
			if("1".equalsIgnoreCase(resultcode)) // 发送成功
				flag = response.toString();
			else
				flag = "1";   // 发送失败

    	} catch (Exception e) {
    		e.printStackTrace();
    		flag = "1";
    	} finally {
            PubFunc.closeResource(outputStrem);
            PubFunc.closeResource(wr);
    	    PubFunc.closeIoResource(is);
    	    PubFunc.closeIoResource(isr);
        	PubFunc.closeIoResource(rd);
		}
    	
    	return flag;
    }
    
    /**
	 * @return
	 * @throws Exception 
	 */
	private URLConnection getUrlConnection(String oppurl, String ncVersion) throws Exception 
	{
		
		if (ncVersion.startsWith("v5")) 
		{
			return getConnection4v5(oppurl);
		}
		throw new Exception("获取链接异常！");
	}
	private HttpURLConnection getConnection4v5(String url) throws Exception 
	{
		try 
		{
		// 	String confirmedUrl = url;
		// 	if (bcompress) {
		// 		confirmedUrl += "&compress=true"; 
		// 	}
			URL realURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) realURL.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-type", "text/xml");
			connection.setRequestMethod("POST");
		// 	System.out.println("NC服务器地址:" + url);
			return connection;
			
		} catch (IOException ex) 
		{
			ex.printStackTrace();
			throw new Exception("异常信息:" + ex.getMessage());
		}
	}
	
	// 获取薪资所属单位转换代码
	public HashMap corCodeUNMap()
    {
		HashMap codeMap = new HashMap();
    	RowSet rs = null;
    	try
		{
			ContentDAO dao = new ContentDAO(this.conn);		
			String str = "select codeitemid,corcode from codeitem where codesetid = 'ZY'";			
			rs = dao.search(str);						
			while(rs.next())
			{
				String codeitemid = isNull(rs.getString("codeitemid"));
				String corcode = isNull(rs.getString("corcode"));
				
				codeMap.put(codeitemid, corcode);
			}
			if(rs!=null)
				rs.close();
			
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return codeMap;
    }
	// 获取薪资归属部门转换代码
	public HashMap corCodeMap()
    {
		HashMap codeMap = new HashMap();
    	RowSet rs = null;
    	try
		{
			ContentDAO dao = new ContentDAO(this.conn);		
			String str = "select codeitemid,corcode from organization where codesetid <> '@K'";			
			rs = dao.search(str);						
			while(rs.next())
			{
				String codeitemid = isNull(rs.getString("codeitemid"));
				String corcode = isNull(rs.getString("corcode"));
				
				codeMap.put(codeitemid, corcode);
			}
			if(rs!=null)
				rs.close();
			
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return codeMap;
    }
	/**
	 * 获取解析的值
	 * 
	 * @param xml
	 * @param nodePath
	 * @return
	 */
	private ArrayList getMapList(String xml, String nodePath) 
	{
		PareXmlUtils xmlUtils = new PareXmlUtils(xml);
		ArrayList list = new ArrayList();
		List nodeList = xmlUtils.getNodes(nodePath);
		for (int i = 0; i < nodeList.size(); i++) 
		{
			Map map = new HashMap();
			Element el = (Element) nodeList.get(i);
			List li = el.getChildren();
			if (li != null) 
			{
				for (int j = 0; j < li.size(); j++) 
				{
					Element e = (Element) li.get(j);
					String value = e.getText();
					if (value == null) {
						value = "";
					}
					map.put(e.getName(), value);
				}
			}

			list.add(map);
		}

		return list;
	}
	
	//凭证信息在NC系统的唯一标识代号
	public String getPingzhengCode()
	{
		Date d = new Date();
		String str = ""+d.getTime()+Math.round(Math.ceil(Math.random()*10));
		return str.substring(4, str.length());
	}
	
    /**
     * 
     * 调用webservice推送财务凭证数据(标准)
     * 
     * */
    public String sendPzMessage(ArrayList list,String pzId){
    	String cwpz = SystemConfig.getPropertyValue("CwPz");
    	String cwpzURL = SystemConfig.getPropertyValue("CwPzURL");
    	
    	// 需要选择的指标 其编号中不能有字母Z
/*		String hkbank_group1 = SystemConfig.getPropertyValue("hkbank_group1"); // 核算责任中心编码,发放责任中心编码,总行责任中心编码 ,分隔
    	String hkbank_group2 = SystemConfig.getPropertyValue("hkbank_group2"); // 所属条线,公共条线
    	String hkbank_group3 = SystemConfig.getPropertyValue("hkbank_group3"); // 
    	String hkbank_group4 = SystemConfig.getPropertyValue("hkbank_group4"); // 
    	String hkbank_group5 = SystemConfig.getPropertyValue("hkbank_group5"); //     	  	
    	String hkbank_selectCode = SystemConfig.getPropertyValue("hkbank_selectCode"); // 可设置发送的分组指标字段
*/   	
    	String returnValue = "";
    	StringBuffer sendXml = new StringBuffer("<?xml version='1.0' ");
    	SyncBo bo = new SyncBo(conn);
		File file = null;
		if("hkBank".equalsIgnoreCase(cwpz) && cwpzURL!= null && cwpzURL.length()>0)
			file = new File(cwpzURL,"voucher.xml");
		else
			file = bo.getFilePath("voucher.xml");
		PareXmlUtils utils = new PareXmlUtils(file);
		//发送xml的编码
		String xmlCode = utils.getTextValue("/sync/params/xmlcode");
    	//发送xml根节点名称
		String rootName = utils.getTextValue("/sync/params/rootname");
		//发送xml节点名称 , 一个节点代表一条凭证的一条记录
		String nodeName = utils.getTextValue("/sync/params/nodename");
		//得到xml配置文件中的映射关系
		List mappingsList = utils.getNodes("/sync/fields_ref/hrfield/field_ref");

		sendXml.append(" encoding='" + xmlCode + "'?>");
		sendXml.append("<" + rootName + " a0100='" + userView.getA0100());
		sendXml.append("' nbase='" + userView.getDbname() + "' pzId='"+pzId+"' >");
		for( int i = 0 ; i < list.size() ; i ++ )
		{
			LazyDynaBean bean = (LazyDynaBean)list.get(i);			
			sendXml.append("<" + nodeName + ">");
			
			for( int j = 0 ; j < mappingsList.size() ; j ++ )
			{
				Element element = (Element)mappingsList.get(j);
				//xml文件中配置的列名
				String columnName = element.getAttributeValue("hrfield");
				//发送xml字符串的节点名称
				String xmlNodeName = element.getAttributeValue("xmlnodename");
				//是否需要转码 
				String codesetName = element.getAttributeValue("codesetid");
				
/*				
				// 需要过滤掉的结点
				StringBuffer removeXmlcode = new StringBuffer("");
				if(hkbank_selectCode!=null && hkbank_selectCode.trim().length()>0)
				{
					String selectCode = (String)bean.get(hkbank_selectCode.toLowerCase());
					if(selectCode!=null && selectCode.trim().length()>0)
					{
						String[] code = selectCode.split("Z");
						for (int k = 0; k < code.length; k++) 
						{												
							if(hkbank_group1!=null && hkbank_group1.trim().length()>0 && hkbank_group1.toString().indexOf(code[k])>=0)
							{
								String[] groupcode = hkbank_group1.split(",");
								for (int h = 0; h < groupcode.length; h++) 
								{
									if(!code[k].equalsIgnoreCase(groupcode[h]))
										removeXmlcode.append(groupcode[h]+",");
								}
							}
							if(hkbank_group2!=null && hkbank_group2.trim().length()>0 && hkbank_group2.toString().indexOf(code[k])>=0)
							{
								String[] groupcode = hkbank_group2.split(",");
								for (int h = 0; h < groupcode.length; h++) 
								{
									if(!code[k].equalsIgnoreCase(groupcode[h]))
										removeXmlcode.append(groupcode[h]+",");
								}
							}
							if(hkbank_group3!=null && hkbank_group3.trim().length()>0 && hkbank_group3.toString().indexOf(code[k])>=0)
							{
								String[] groupcode = hkbank_group3.split(",");
								for (int h = 0; h < groupcode.length; h++) 
								{
									if(!code[k].equalsIgnoreCase(groupcode[h]))
										removeXmlcode.append(groupcode[h]+",");
								}
							}
							if(hkbank_group4!=null && hkbank_group4.trim().length()>0 && hkbank_group4.toString().indexOf(code[k])>=0)
							{
								String[] groupcode = hkbank_group4.split(",");
								for (int h = 0; h < groupcode.length; h++) 
								{
									if(!code[k].equalsIgnoreCase(groupcode[h]))
										removeXmlcode.append(groupcode[h]+",");
								}
							}
							if(hkbank_group5!=null && hkbank_group5.trim().length()>0 && hkbank_group5.toString().indexOf(code[k])>=0)
							{
								String[] groupcode = hkbank_group5.split(",");
								for (int h = 0; h < groupcode.length; h++) 
								{
									if(!code[k].equalsIgnoreCase(groupcode[h]))
										removeXmlcode.append(groupcode[h]+",");
								}
							}						
						}
					}
				}				
				if(removeXmlcode!=null && removeXmlcode.toString().trim().length()>0 && removeXmlcode.toString().indexOf(columnName)>=0)
				{
					continue;
				}
*/				
			//	System.out.println(columnName+":"+bean.get(columnName.toLowerCase()));
				
				sendXml.append("<" + xmlNodeName + ">");
				if(null != codesetName && !"".equalsIgnoreCase(codesetName.trim()))
				{
					if(null != bean.get(columnName.toLowerCase()) && !"".equals(bean.get(columnName.toLowerCase()).toString()))
					{
						sendXml.append(AdminCode.getCodeName(codesetName, bean.get(columnName.toLowerCase()).toString()));
					}else
					{
						sendXml.append(bean.get(columnName.toLowerCase()));
					}
				}else
				{
					sendXml.append(bean.get(columnName.toLowerCase()));
				}
				sendXml.append("</" + xmlNodeName + ">");
			}
			sendXml.append("</" + nodeName + ">");
		}
		sendXml.append("</" + rootName + ">");
		cat.debug("财务凭证接口,封装xml字符串:" + sendXml);
		String mess = invokeWsdlSendMessage(sendXml.toString() , utils);
		if(null != mess && !"".equalsIgnoreCase(mess.trim())){
			cat.debug("调用成功以后开始修改状态");
			returnValue = updateFlag(list , mess);
		}else{
			returnValue = "发送数据失败!";
		}
    	return returnValue;
    }
    
    public String invokeWsdlSendMessage(String xml , PareXmlUtils utils){
    	String mess = "";
    	String isUsing = utils.getAttributeValue("/sync/params/soapstr", "isusing");
    	if("true".equalsIgnoreCase(isUsing.trim())){
    		cat.debug("开始调用接口,调用方式SOAP");
    		String url = utils.getAttributeValue("/sync/params/soapstr", "url");
    		if(null != url && 
    				!"".equalsIgnoreCase(url.trim())){
    			String xmlString = utils.getTextValue("/sync/params/soapstr");
    			xmlString = xmlString.replace("${xml}", "<![CDATA[" + xml + "]]>");
    			mess = callInterFaceBySoap(xmlString , url);
    		}
    	}else{
    		try {
    			cat.debug("开始调用接口,调用方式普通调用");
				mess = sendMessages(xml, utils);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
    	}
    	cat.debug("调用完毕,得到的返回值为: " + mess);
    	return mess;
    }
    
    /**
     * 	SOAP调用
     * */
    public String callInterFaceBySoap(String xml , String url){
    	SOAPConnection con = null;
		SOAPMessage response = null;
    	String mess = "";
    	try {
    		SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance(); 
    		con = factory.createConnection(); 
    		SOAPMessage request = MessageFactory.newInstance().createMessage(); 
    		SOAPPart soapPart = request.getSOAPPart();
    		
    		Reader reader = new StringReader(xml);
			Source source = new StreamSource(reader);
			soapPart.setContent(source);
			response = con.call(request, url);
			cat.debug("调用接口成功,返回值为:" + response);
			if(null != response){
		    	SOAPBody responseBody;
				try {
					responseBody = response.getSOAPBody();
					Node it = responseBody.getFirstChild().getFirstChild();
					mess = it.getTextContent();
				} catch (SOAPException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			cat.debug("调用方式为SOAP时，调用接口出现异常，由于网络问题或者配置问题或者其他问题");
			e.printStackTrace();
		}
    	return mess;
    }
    
    /**
     *  普通调用
     * */
    public String sendMessages(String xml , PareXmlUtils utils)throws GeneralException
	{
		String mess = "";
		try
 	    {
			 String url="";
			 String namespace="";
			 String function="";
			 String paramname="";
			 String username="";
			 String password="";
			 String style="";
			 if(utils==null)
			 {
				 throw GeneralExceptionHandler.Handle(new Exception("service配置文件设置有问题!"));	
			 }
			 else
			 {
				 url=utils.getAttributeValue("/sync/params/hrwebservice","url");
				 namespace=utils.getAttributeValue("/sync/params/hrwebservice","namespace");
				 function=utils.getAttributeValue("/sync/params/hrwebservice","function");
				 paramname=utils.getAttributeValue("/sync/params/hrwebservice","paramname");
				 username=utils.getAttributeValue("/sync/params/hrwebservice","username");
				 password=utils.getAttributeValue("/sync/params/hrwebservice","password");
				 style=utils.getAttributeValue("/sync/params/hrwebservice","style");
				 
			 }
			 
			Service service = new Service(); 
			Call call = (Call) service.createCall(); 			
			call.setTargetEndpointAddress(new URL(url));
			call.setReturnType(XMLType.XSD_STRING);
			call.setUseSOAPAction(true);
			call.setOperationName(new QName(namespace, function));	        
			call.addParameter(new QName(namespace, paramname),XMLType.XSD_STRING,ParameterMode.IN);	
			call.setSOAPActionURI(namespace+"/"+function);
			if(username!=null&&username.length()>0)
			{
				call.getMessageContext().setUsername(username);
				call.getMessageContext().setPassword(password);
			}
			if(style!=null)
			{
				if("wrapped".equalsIgnoreCase(style))
					call.setOperationStyle(org.apache.axis.constants.Style.WRAPPED);
				
			}
			//xml = new String(xml.getBytes(),"GB2312");//如果没有加这段，中文参数将会乱码
			mess = (String) call.invoke( new Object[] {xml} );  

		
 	   } catch(Exception e) {
 		    cat.error("调用方式为普通调用时，调用接口出现异常，由于网络问题或者配置问题或者其他问题");
			e.printStackTrace(); 
			throw GeneralExceptionHandler.Handle(e);	
		}   
		return mess;
	}
    
    /**
     * 推送完数据以后改变状态
     * */
    private String updateFlag(ArrayList dataList, String flag) {
    	String returnValue = "报送数据失败!";
    	try {
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer sql = new StringBuffer();
    		sql.append("update GZ_WarrantRecord set state='");
    		sql.append(flag);
    		sql.append("' where pz_id in (");
    		for (int i = 0; i < dataList.size(); i++) {
    			LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
    			if (i == 0) {
	    			sql.append("'");
	    			sql.append(bean.get("Pz_id".toLowerCase()));
	    			sql.append("'");
    			} else {
    				sql.append(",");
    				sql.append("'");
    				sql.append(bean.get("Pz_id".toLowerCase()));
	    			sql.append("'");
    			}
    			
    		}
    		sql.append(")");
    		
    		dao.update(sql.toString());
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	} 
    	
    	if ("1".equals(flag.trim())) { // 发送失败
    		returnValue =  "报送数据成功！";
    	}else if("2".equals(flag.trim())){
    		returnValue =  "报送数据成功,处理相关数据成功！";
    	}else if("3".equals(flag.trim())){
    		returnValue =  "报送数据失败！";
    	}else if("4".equals(flag.trim())) { // 发送成功
    		returnValue =  "报送数据成功,财务系统正在处理中...";
    	}
    	return returnValue;
    }
    
    private String handleFlag(ArrayList dataList, String flag) {
    	try {
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer sql = new StringBuffer();
    		sql.append("update GZ_WarrantRecord set state='");
    		if ("1".equals(flag)) { // 发送失败
    			sql.append("3");
	    	} else { // 发送成功
	    		sql.append("1");
	    	}
    		
    		int nc = 0;
    		sql.append("' where pz_id in (");
    		for (int i = 0; i < dataList.size(); i++) {
    			LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
    			if (i == 0) {
	    			sql.append("'");
	    			sql.append(bean.get("Pz_id".toLowerCase()));
	    			sql.append("'");
    			} else {
    				sql.append(",");
    				sql.append("'");
    				sql.append(bean.get("Pz_id".toLowerCase()));
	    			sql.append("'");
    			}
    			
    			// wangjh 20141204, in 超过1000报错
    			nc++;
    			if (nc==900) {
    				sql.append(")");
    	    		dao.update(sql.toString());
    	    		nc = 0;
    	    		
    	    		sql.setLength(0);
    	    		sql.append("update GZ_WarrantRecord set state='");
    	    		if ("1".equals(flag)) { // 发送失败
    	    			sql.append("3");
    		    	} else { // 发送成功
    		    		sql.append("1");
    		    	}
    	    		sql.append("' where pz_id in ('#'");    	    		
    				
    			}
    		}
    		sql.append(")");
    		
    		dao.update(sql.toString());
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	} 
    	
    	if ("1".equals(flag)) { // 发送失败
    		return "发送失败！";
    	} else { // 发送成功
    		return "发送成功！";
    	}
    }
    
    // 齐鲁银行
    private String handqlBankFlag(ArrayList dataList, String flag) 
    {
    	try 
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer sql = new StringBuffer();
    		sql.append("update GZ_WarrantRecord set state='");
    		if ("1".equals(flag)) { // 发送失败
    			sql.append("3");
	    	} else { // 发送成功
	    		sql.append("1");
	    	}    		
    		sql.append("' where pz_id in (");
    		String pz_id = "";
    		for (int i = 0; i < dataList.size(); i++) 
    		{
    			LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
    			String pzID = (String)bean.get("Pz_id".toLowerCase());
    			
    			if (i == 0) 
    			{
	    			sql.append("'"+pzID+"'");
    			} else 
    			{
    				if(!pz_id.equalsIgnoreCase(pzID))
    					sql.append(","+"'"+pzID+"'");
    			}    			
    			pz_id = pzID;
    		}
    		sql.append(")");
    		
    		dao.update(sql.toString());
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	} 
    	
    	if ("1".equals(flag)) { // 发送失败
    		return "发送失败！";
    	} else { // 发送成功
    		return "发送成功！";
    	}
    }
    
    private String sendBySOAP(String ipStr, String targetNamespace, String method, String paramName, ArrayList list) {
    	SOAPConnection con = null;
    	String flag = "1";
    	try {
    		// 链接工厂类
			SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
			// 创建一个链接
			SOAPConnection conn = factory.createConnection();
			
			// 消息工厂类
			MessageFactory reqMsgFactory = MessageFactory.newInstance();
			//  创建soap消息
			SOAPMessage reqMsg = reqMsgFactory.createMessage();
//			reqMsg.setProperty(reqMsg.WRITE_XML_DECLARATION, "utf-8");
			// 
			SOAPPart soapPart = reqMsg.getSOAPPart();
			
			
						
			SOAPEnvelope envelope = soapPart.getEnvelope();
			// 设置前缀
			envelope.setPrefix("soapenv");	// 默认是SOAP-ENV
			
			// 设置属性
//			envelope.setAttribute("xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
			envelope.setAttribute("xmlns:urn", targetNamespace);
//			envelope.setAttribute("xmlns:types", "http://localhost:8088/axis/services/HelloWorld/encodedTypes");
//			envelope.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//			envelope.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
			
			// 设置head
			envelope.getHeader().setPrefix("soapenv");
			
			// 设置body
			SOAPBody soapBody = envelope.getBody();
			soapBody.setPrefix("soapenv");	// 默认是SOAP-ENV
//			soapBody.setAttribute("soap:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");

			// 设置方法
//			SOAPElement methodElem = soapBody.addChildElement(envelope.createName("urn:Subject"));
			SOAPElement methodElem = soapBody.addChildElement(envelope.createName("urn:" + method));
			SOAPElement paramElem =	methodElem.addChildElement(paramName);
			
			// 公司代码fieldid
			String companyfieldid = SystemConfig.getPropertyValue("nxyp_maindata_companyfieldid");

			// 成本中心fieldid
			String costcenterfieldid = SystemConfig.getPropertyValue("nxyp_maindata_costcenterfieldid");

			// 利润中心fieldid
			String profitcenterfieldid = SystemConfig.getPropertyValue("nxyp_maindata_profitcenterfieldid");
			
			// wbs项目fieldid
			String wbsfieldid = SystemConfig.getPropertyValue("nxyp_maindata_wbsfieldid");
			// 员工工号
			String gonghao = SystemConfig.getPropertyValue("gonghaofieldname");

			
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				str.append("<item>");
				LazyDynaBean bean = (LazyDynaBean) list.get(i);
				
				// 凭证日期
				String date = (String) bean.get("Voucher_Date".toLowerCase());
				
				
				// 计提月份
				String dbill_Date = (String) bean.get("dbill_Date".toLowerCase());
				Date dbillDate = DateUtils.getDate(date, "yyyy-MM-dd");
    			SOAPElement itemElem = paramElem.addChildElement("item");
    			
    			// 凭证日期
    			SOAPElement DocDate = itemElem.addChildElement("DocDate");	    			
    			DocDate.addTextNode(date);
    			str.append("<DocDate>");
    			str.append(date);
    			str.append("</DocDate>");
    			
    			// 过账日期
    			SOAPElement PstngDate = itemElem.addChildElement("PstngDate");
    			PstngDate.addTextNode(date);
    			str.append("<PstngDate>");
    			str.append(date);
    			str.append("</PstngDate>");
    			
    			// 年份
    			SOAPElement FiscYear = itemElem.addChildElement("FiscYear");
    			FiscYear.addTextNode(DateUtils.format(dbillDate, "yyyy"));
    			str.append("<FiscYear>");
    			str.append(DateUtils.format(dbillDate, "yyyy"));
    			str.append("</FiscYear>");
    			
    			// 过账期间
    			SOAPElement FisPeriod = itemElem.addChildElement("FisPeriod");
    			FisPeriod.addTextNode(DateUtils.format(dbillDate, "MM"));	    			
    			str.append("<FisPeriod>");
    			str.append(DateUtils.format(dbillDate, "MM"));
    			str.append("</FisPeriod>");
    			
    			// 公司代码
    			SOAPElement CompCode = itemElem.addChildElement("CompCode");
    			String company = (String) bean.get(companyfieldid.toLowerCase());
    			company = company == null ? "" : company;
    			CompCode.addTextNode(company);
    			str.append("<CompCode>");
    			str.append(company);
    			str.append("</CompCode>");
    			
    			// 凭证类型
    			SOAPElement DocType = itemElem.addChildElement("DocType");
    			String c_type = (String) bean.get("c_type");
    			DocType.addTextNode(c_type);
    			str.append("<DocType>");
    			str.append(c_type);
    			str.append("</DocType>");
    			
    			// 货币
    			SOAPElement Currency = itemElem.addChildElement("Currency");
    			Currency.addTextNode("CNY");
    			str.append("<Currency>");
    			str.append("CNY");
    			str.append("</Currency>");
    			
    			// 科目
    			SOAPElement GlAccount = itemElem.addChildElement("GlAccount");
    			String c_subject = (String) bean.get("c_subject");
    			GlAccount.addTextNode(c_subject);
    			str.append("<GlAccount>");
    			str.append(c_subject);
    			str.append("</GlAccount>");
    			
    			// 借方记账码
    			SOAPElement Newbs = itemElem.addChildElement("Newbs");
    			String n_loan = (String) bean.get("n_loan");

    			str.append("<Newbs>");
    			if ("借".equals(n_loan)) {
    				Newbs.addTextNode("40");
    				str.append("40");
    			} else if ("贷".equals(n_loan)) {
    				Newbs.addTextNode("50");
    				str.append("50");
    			} else {
    				Newbs.addTextNode(n_loan);
    				str.append(n_loan);
    			}
    			
    			
    			str.append("</Newbs>");
    			
    			// 利润中心
    			SOAPElement ProfitCtr = itemElem.addChildElement("ProfitCtr");	    			
    			String profitcenter = (String) bean.get(profitcenterfieldid.toLowerCase());
    			profitcenter = profitcenter == null ? "" : profitcenter;
    			ProfitCtr.addTextNode(profitcenter);
    			str.append("<ProfitCtr>");
    			str.append(profitcenter);
    			str.append("</ProfitCtr>");
    			
    			// wbs
    			SOAPElement WbsElement = itemElem.addChildElement("WbsElement");
    			String wbs = (String) bean.get(wbsfieldid.toLowerCase());
    			wbs = wbs == null ? "" : wbs;
    			
    			// 成本中心
    			SOAPElement Costcenter = itemElem.addChildElement("Costcenter");
    			String costcenter = (String) bean.get(costcenterfieldid.toLowerCase());
    			costcenter = costcenter == null ? "" : costcenter;	
//    			if ((c_subject.startsWith("5") || c_subject.startsWith("6")) && wbs.length() > 0) {
//    				Costcenter.addTextNode("");
//    			} else {
    				Costcenter.addTextNode(costcenter);
//    			}
    			str.append("<Costcenter>");
    			str.append(costcenter);
    			str.append("</Costcenter>");
    			
    			// 文本
    			SOAPElement ItemText = itemElem.addChildElement("ItemText");
    			String c_mark = (String) bean.get("c_mark");
    			ItemText.addTextNode(c_mark);
    			str.append("<ItemText>");
    			str.append(c_mark);
    			str.append("</ItemText>");
    			
    			// 金额
    			SOAPElement AmtDoccur = itemElem.addChildElement("AmtDoccur");
    			String money = (String) bean.get("money");
    			str.append("<AmtDoccur>");
    			
    			

    			AmtDoccur.addTextNode(money);
    			str.append(money);
    			
    			str.append("</AmtDoccur>");
    			
    			// 付款原因
    			SOAPElement Rstgr = itemElem.addChildElement("Rstgr");
    			Rstgr.addTextNode("");
    			str.append("<Rstgr>");
    			str.append("");
    			str.append("</Rstgr>");
    			
    			// wbs
    			
//    			if (c_subject.startsWith("5") || c_subject.startsWith("6")) {
    				WbsElement.addTextNode(wbs);
//    			} else {
//    				WbsElement.addTextNode("");
//    			}
    			str.append("<WbsElement>");
    			str.append(wbs);
    			str.append("</WbsElement>");
    			
    				    			
    			// 员工工号
    			SOAPElement gonghaoEl = itemElem.addChildElement("VendorNo");	    			
    			String VENDOR_NO = (String) bean.get(gonghao.toLowerCase());
    			VENDOR_NO = VENDOR_NO == null ? "" : VENDOR_NO;
    			gonghaoEl.addTextNode(VENDOR_NO);
    			str.append("<VendorNo>");
    			str.append(VENDOR_NO);
    			str.append("</VendorNo>");
    			str.append("</item>");
			}
			
			System.out.println(str);
			
				
			
					
//			reqMsg.writeTo(System.out);
						
			
			// 调用
			URL endPoint = new URL(ipStr);
			
			SOAPMessage respMsg = conn.call(reqMsg, endPoint);
//						
//						System.out.println("\n服务端返回的信息- : " + respMsg.getSOAPBody().getch);
//						respMsg.writeTo(System.out);
						
			Node node =	respMsg.getSOAPBody().getFirstChild().getFirstChild();
//			System.out.println(node.getNodeName() + "----" + node.getTextContent());
			
			flag = node.getTextContent();

//	    	
//	    	System.out.println("\n响应内容："+responseStr.toString());
    	} catch (Exception e) {
    		e.printStackTrace();
    		flag = "1";
    	} finally {
    		try {
    			if (con != null) {
    				con.close();
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return flag;
}
    
    public static void main (String[] args) {
//    	sendBySOAP("http://zypdev.eppen.com.cn:8000", "urn:sap-com:document:sap:soap:functions:mc-style", "ZHrCreateAccDoc", "EtAccDoc");
    }
    /**
     * 浏览月汇总数据的表头
     * @param pn_id
     * @return
     */
    public ArrayList getMonthCollectHeadList(String pn_id) throws GeneralException{
    	ArrayList list = new ArrayList();
    	RowSet rs = null;
    	try{
    		list.add(this.getBean("unitcode", ResourceFactory.getProperty("org.performance.unorum"), "A","UN"));
    		list.add(this.getBean("deptcode", ResourceFactory.getProperty("gz.columns.lse0122"),"A", "UM"));
    		list.add(this.getBean("name",ResourceFactory.getProperty("performance.batchgrade.title.name"),"A", "0"));
    		StringBuffer buf = new StringBuffer("");
    		buf.append(" select gw.* from GZ_WARRANTLIST gw ");
    		buf.append(" where gw.pn_id="+pn_id);
    		buf.append(" order by gw.seq ");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs=dao.search(buf.toString());
    		LazyDynaBean bean = null;
    		while(rs.next()){
    			bean = new LazyDynaBean();
    			String fl_name=rs.getString("fl_name")==null?"":rs.getString("fl_name");
    			String c_subject=rs.getString("c_subject")==null?"":rs.getString("c_subject");
    			//list.add(this.getBean(c_subject, fl_name, "N","0"));
    			if(!Character.isLetter(c_subject.charAt(0)))////xiegh 20170621 如果是按月汇总且科目是以非字母开头，则在科目前面添加“HJ_” bug:28859
    				c_subject="HJ_"+c_subject;
    			bean.set("itemname",fl_name);
    			bean.set("itemid",c_subject);
    			bean.set("itemtype","N");
    			bean.set("codeset","0");
    			bean.set("roundFlag","true");//对科目做保留2位小数操作的标识
    			list.add(bean);
    		}
    		list.add(this.getBean("status",ResourceFactory.getProperty("hire.jp.pos.state"),"N","0"));
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
    			if(rs!=null)
    				rs.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}                        
    	}
    	return list;
    }


    public ArrayList getMonthCollectInfoList(String status,String timeInfo,String pn_id,ArrayList headList,String a_code)throws GeneralException
    {
    	RowSet rowSet = null;
    	ArrayList list = new ArrayList();
    	try{
    		
    		DbWizard dbw = new DbWizard(this.conn);
    		if(!dbw.isExistField("gz_warrantdata", "a0000",false))
    		{
    			Table table=new Table("gz_warrantdata");
				Field field=new Field("a0000","a0000");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);	
				dbw.addColumns(table);
    		}
    		
    		
    		
    		StringBuffer buf = new StringBuffer();
    		buf.append("select pn_id");
    		for(int i=0;i<headList.size();i++){
    			LazyDynaBean bean = (LazyDynaBean)headList.get(i);
    			String itemid=(String)bean.get("itemid");
    			if(null == itemid||"".equals(itemid))//xiegh 20170505 bug27539
    				continue;
    		
    				
    			buf.append(","+itemid);
    		}
    		buf.append(",period,psncode from gz_warrantdata");
    		buf.append(" where ");
    		buf.append(" pn_id="+pn_id);
    		if(status!=null&&!"all".equalsIgnoreCase(status))
				buf.append(" and status='"+status+"'");
			if(timeInfo!=null&&timeInfo.trim().length()>0&&!"all".equalsIgnoreCase(timeInfo)&&!"".equals(timeInfo))
			{
				String[] temps=timeInfo.split("-");
				buf.append(" and "+Sql_switcher.year("period")+"="+temps[0]);
				buf.append(" and "+Sql_switcher.month("period")+"="+temps[1]);
			}
			buf.append(getPrivStr(""));
			if(a_code!=null&&a_code.trim().length()>0)
				buf.append(" and DeptCode like '"+a_code.substring(2)+"%'");
		    buf.append(" order by pndate desc,a0000,unitcode,deptcode ");
		    ContentDAO dao = new ContentDAO(this.conn);
		    rowSet=dao.search(buf.toString());
		    SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		    LazyDynaBean data_bean = null;
		    LazyDynaBean _bean = null;
		    while(rowSet.next()){
		    	data_bean=new LazyDynaBean();
		    	data_bean.set("id",df.format(rowSet.getDate("period"))+"`"+(rowSet.getString("unitcode")==null?"":rowSet.getString("unitcode"))+"`"+(rowSet.getString("deptcode")==null?"":rowSet.getString("deptcode"))+"`"+rowSet.getString("psncode"));
				for(int i=0;i<headList.size();i++)
				{
					_bean=(LazyDynaBean)headList.get(i);
					String itemname=(String)_bean.get("itemname");
					String itemid=(String)_bean.get("itemid");
					String itemtype=(String)_bean.get("itemtype");
					String codeset=(String)_bean.get("codeset");
					String value="";
					if("D".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getDate(itemid)!=null)
							value=df.format(rowSet.getDate(itemid));
					}
					else if("M".equalsIgnoreCase(itemtype))
						value=Sql_switcher.readMemo(rowSet,itemid);
					else if("A".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if("0".equals(codeset))
								value=rowSet.getString(itemid);
							else if(codeset.length()>0)
							{
								value=AdminCode.getCodeName(codeset,rowSet.getString(itemid));
								if("UN".equalsIgnoreCase(codeset))
								{
									if(value==null||value.trim().length()==0)
										value=AdminCode.getCodeName("UM",rowSet.getString(itemid));
									/*else if(Integer.parseInt(display_e0122)>0)
									{
										CodeItem item=AdminCode.getCode("UM",rowSet.getString(itemid),Integer.parseInt(display_e0122));
						    	    	if(item!=null)
						    	    	{
						    	    		value=item.getCodename();
						        		}
									}*/
								}
							}
						}
					}
					else if("N".equalsIgnoreCase(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if("status".equalsIgnoreCase(itemid))
							{
								int v=rowSet.getInt("status");
								if(v==1)
									value="已生成";
								else if(v==2)
									value="已通知";
								else if(v==3)
									value="已接收";
							}
							else
							{
								//xiegh 20170525 bug:28036  add:对科目字段做四舍五入并保留小数点后两位数
								String roundFlag = (String)_bean.get("roundFlag")==null?"":(String)_bean.get("roundFlag");
								value=rowSet.getString(itemid)==null?"0":rowSet.getString(itemid);
				    			if("true".equals(roundFlag))
									value  = PubFunc.round(value,2);
							}
						}
					} 
					data_bean.set(itemid,value);
				}
				list.add(data_bean); 
		    }
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
    		try{
    			if(rowSet!=null)
    				rowSet.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	return list;
    }
    /**
     * 根据凭证，看看是否需要改变表结构
     * @param pn_id
     * @throws GeneralException 
     */
    public void sycnTableStrut(String pn_id,String type) throws GeneralException {
    	RowSet rs =null;
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
    		DbWizard dbw=new DbWizard(this.conn);
    		Table table = new Table("gz_warrantdata");
    		if(!dbw.isExistTable("gz_warrantdata",false))
    		{
    			this.createMiddleTable(table, dbw);
    		}
    		rs=dao.search("select * from GZ_WarrantData where 1=2");
    		ResultSetMetaData rsmd=rs.getMetaData();
    		HashMap map = new HashMap();
    		for(int i=1;i<=rsmd.getColumnCount();i++){
    			map.put(rsmd.getColumnName(i).toUpperCase(),rsmd.getColumnName(i));
    		}
    		rs.close();
    		rs=dao.search("select distinct c_subject from GZ_WarrantList where pn_id="+pn_id);
    		boolean isAdd=false;
    		Table table2 = new Table("gz_warrantdata");
    		while(rs.next()){
    			String c_subject=rs.getString("c_subject");
    			if(null == c_subject || "".equals(c_subject))
    				continue;
    			 // c_subject=(c_subject==null)?"":c_subject;//xiegh 20170510 bug27587 add避免空指针异常
    			 //xiegh 20170510 判断分录号是否以字母开头    bug27587
    			if(!Character.isLetter(c_subject.charAt(0))&&"2".equals(type)){//xiegh 20170621 如果是按月汇总且科目是以非字母开头，则在科目前面添加“N” bug:28859
/*    				String voucherName = getVoucherName(pn_id, dao);
    				throw new GeneralException("\""+voucherName+"\"是按月汇总凭证，分录下的科目编号只能以字母开头，请检查!");*/
    				c_subject = "HJ_"+c_subject;
    			}
    			if(map.get(c_subject.toUpperCase())==null&&c_subject.length()>0){
    				FieldItem item = new FieldItem();
    				item.setItemid(c_subject);
    				item.setItemtype("N");
    				item.setItemlength(12);
    				item.setDecimalwidth(8);
    				table2.addField(item);
    				isAdd=true;
    				map.put(c_subject.toUpperCase(), c_subject);
    			}
    		}
    		if(isAdd)
    			dbw.addColumns(table2);
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		 throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		try{
    			if(rs!=null)
    				rs.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * @date20170510
     * @author xiegh
     * @param pn_id
     * @param dao
     * @return 凭证的名字
     * @throws SQLException
     */
    private String getVoucherName(String pn_id, ContentDAO dao) throws SQLException {
		String voucherName = "";
		RowSet rowSet = null;
		ContentDAO Dao = new ContentDAO(this.conn);
		try{
	    rowSet = dao.search(" select * from GZ_Warrant where pn_id = "+ pn_id);
		while(rowSet.next()){
			voucherName = rowSet.getString("c_name");
		}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rowSet!=null)rowSet.close();
		}
		return voucherName;
	}


	/**
     * 创建临时表固定字段
     * @param table
     * @param dbw
     */
    public void createMiddleTable(Table table,DbWizard dbw){
    	try{
    		
    		FieldItem item1 = new FieldItem();
    		item1.setItemid("pn_id");
    		item1.setItemtype("N");
    		item1.setDecimalwidth(0);
    		item1.setKeyable(true);
    		item1.setNullable(false);
    		table.addField(item1);
    		
    		FieldItem item2 = new FieldItem();
    		item2.setItemid("period");
    		item2.setItemtype("D");
    		item2.setKeyable(true);
    		item2.setNullable(false);
    		table.addField(item2);
    		
    		FieldItem item3 = new FieldItem();
    		item3.setItemid("unitcode");
    		item3.setItemtype("A");
    		item3.setItemlength(30);
    		item3.setKeyable(true);
    		item3.setNullable(false);
    		table.addField(item3);
    		
    		FieldItem item4 = new FieldItem();
    		item4.setItemid("deptcode");
    		item4.setItemtype("A");
    		item4.setItemlength(30);
    		table.addField(item4);
    		
    		FieldItem item5 = new FieldItem();
    		item5.setItemid("psncode");
    		item5.setItemtype("A");
    		item5.setItemlength(50);
    		item5.setKeyable(true);
    		item5.setNullable(false);
    		table.addField(item5);
    		
    		FieldItem item6 = new FieldItem();
    		item6.setItemid("unitcode_trans");
    		item6.setItemtype("A");
    		item6.setItemlength(30);
    		table.addField(item6);
    		
    		FieldItem item7 = new FieldItem();
    		item7.setItemid("deptcode_trans");
    		item7.setItemtype("A");
    		item7.setItemlength(30);
    		table.addField(item7);
    		
    		FieldItem item8 = new FieldItem();
    		item8.setItemid("name");
    		item8.setItemtype("A");
    		item8.setItemlength(30);
    		table.addField(item8);
    		
    		FieldItem item9 = new FieldItem();
    		item9.setItemid("pndate");
    		item9.setItemtype("D");
    		table.addField(item9);
    		
    		FieldItem item10 = new FieldItem();
    		item10.setItemid("status");
    		item10.setItemtype("N");
    		item10.setDecimalwidth(0);
    		table.addField(item10);
    		
    		FieldItem item11 = new FieldItem();
    		item11.setItemid("ts");
    		item11.setItemtype("A");
    		item11.setItemlength(19);
    		table.addField(item11);
    		
    		for(int i=1;i<=40;i++)
    		{
    			FieldItem ii=new FieldItem();
    			ii.setItemid("DEF"+i);
    			ii.setItemlength(12);
    			ii.setItemtype("N");
    			ii.setDecimalwidth(8);
    			table.addField(ii);
    		}
    		
    		dbw.createTable(table);
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * 生成汇总数据
     * @param year
     * @param month
     * @param voucher_date
     * @param deptcode
     * @param pn_id
     * @param oper =1如果存在相同数据，不覆盖，=2存在形同数据，覆盖
     */
    public String collectData(String year,String month,String voucher_date,String deptcode,String pn_id,String oper)throws GeneralException{
    	RowSet rs = null;
    	try{
    	 
    		
    		DbWizard dbw = new DbWizard(this.conn);
    		if(!dbw.isExistField("gz_warrantdata", "a0000",false))
    		{
    			Table table=new Table("gz_warrantdata");
				Field field=new Field("a0000","a0000");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);	
				dbw.addColumns(table);
    		}
    		
    		
    		ContentDAO dao = new ContentDAO(this.conn);
    		RecordVo voucherVo=new RecordVo("gz_warrant");
			voucherVo.setInt("pn_id",Integer.parseInt(pn_id));
			voucherVo=dao.findByPrimaryKey(voucherVo);
			String filterSQL=this.getFilter_str2(voucherVo, year, month, "", deptcode, "2");
    		String sql = "select c_subject,fl_name,c_itemsql,c_where from GZ_WarrantList where pn_id="+pn_id;
    		String tempTableName=this.createTmpTable(pn_id, filterSQL, voucherVo, voucher_date, deptcode, year, month);
    		if(tempTableName.endsWith(".xls"))
    			return tempTableName;
    		rs=dao.search(sql);
    		ArrayList gzFieldList=this.getGzFieldList(voucherVo);
    		YksjParser yp = null;
    		StringBuffer column = new StringBuffer();
    		while(rs.next()){
    			String c_subject=rs.getString("c_subject");
    			if(!Character.isLetter(c_subject.charAt(0)))
    				c_subject="HJ_"+c_subject;
    			column.append(","+c_subject);
    			String c_itemsql=rs.getString("c_itemsql");//汇总公式
    			String c_where=rs.getString("c_where");//取数条件
    			String strfilter="";
    			/**先对计算公式的条件进行分析*/
				if(!(c_where==null|| "".equalsIgnoreCase(c_where)))
				{
					yp = new YksjParser(this.userView ,gzFieldList,YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
					yp.run_where(c_where);
					strfilter=" and "+yp.getSQL();
				}
				yp=new YksjParser(this.userView ,gzFieldList,YksjParser.forNormal, YksjParser.FLOAT,YksjParser.forPerson , "Ht", "");
				if(c_itemsql==null|| "".equals(c_itemsql)){
					c_itemsql="0";
				}
				yp.run(c_itemsql,this.conn,"","salaryhistory");
				String strexpr=yp.getSQL();	
				StringBuffer collectSql=new StringBuffer();
				StringBuffer updateSql = new StringBuffer("");
				collectSql.append(" select sum(");
				collectSql.append(c_subject);
				collectSql.append(") as "+c_subject+",a0100,nbase,gsb0110,deptcode ");
				collectSql.append(" from (");
				collectSql.append(" select "+strexpr+" as "+c_subject+",a0100,nbase");
				if(this.orgid.length()>0&&!isMultiple) 
					collectSql.append(",case when "+this.orgid+" is null then b0110 else "+this.orgid+" end as gsb0110");
	    		else 
	    			collectSql.append(",b0110  as gsb0110");
	    		if(this.deptid.length()>0&&!isMultiple)
	    			collectSql.append(",case when "+this.deptid+" is null then e0122 else "+this.deptid+" end as deptcode");
	    		else
	    			collectSql.append(",e0122 as deptcode");
				collectSql.append(" from salaryhistory where 1=1 ");
				collectSql.append(filterSQL);
				if(strfilter.length()>0)
					collectSql.append(strfilter);
				collectSql.append(") t ");
				collectSql.append(" group by a0100,nbase,gsb0110,deptcode  ");
				updateSql.append(" update "+tempTableName);
				updateSql.append(" set "+c_subject+"=(select "+c_subject+" from (");
				updateSql.append(collectSql);
				updateSql.append(") T ");
				updateSql.append(" where T.a0100="+tempTableName+".a0100 and UPPER(T.NBASE)=UPPER("+tempTableName+".NBASE)    and "
						+ "T.gsb0110="+tempTableName+".gsb0110 and "
						+ "(T.deptcode="+tempTableName+".deptcode or (nullif(T.deptcode,'') is null and nullif("+tempTableName+".deptcode,'') is null)))");
				dao.update(updateSql.toString());
			}
    		StringBuffer insertBuf = new StringBuffer();
    		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
    		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
    	    FieldItem item = DataDictionary.getFieldItem(onlyname);
    		if("2".equals(oper))//覆盖相同数据
    		{
    			
    			insertBuf.append("delete from GZ_WarrantData where pn_id="+pn_id);
    			insertBuf.append(" and "+Sql_switcher.year("period")+"="+year);
    			insertBuf.append(" and "+Sql_switcher.month("period")+"="+month);
    			insertBuf.append(" and unitcode='"+deptcode.substring(2)+"'");
    			insertBuf.append(" and status<>3");
    			
    		}else{
    			insertBuf.append(" delete from "+tempTableName);
    			insertBuf.append(" where psncode in (select psncode from GZ_WarrantData where pn_id="+pn_id);
    			insertBuf.append(" and "+Sql_switcher.year("period")+"="+year);
    			insertBuf.append(" and "+Sql_switcher.month("period")+"="+month);
    			insertBuf.append(" and unitcode='"+deptcode.substring(2)+"'");
    			insertBuf.append(" )");
    		}
    		dao.delete(insertBuf.toString(), new ArrayList());
    		insertBuf.setLength(0);
    		insertBuf.append(" delete from "+tempTableName);
			insertBuf.append(" where psncode in (select psncode from GZ_WarrantData where pn_id="+pn_id);
			insertBuf.append(" and "+Sql_switcher.year("period")+"="+year);
			insertBuf.append(" and "+Sql_switcher.month("period")+"="+month);
			insertBuf.append(" and unitcode='"+deptcode.substring(2)+"'");
			insertBuf.append(" and status=3)");
			dao.delete(insertBuf.toString(), new ArrayList());
    		insertBuf.setLength(0);
    		insertBuf.append(" insert into GZ_WarrantData");
    		insertBuf.append("(pn_id,period,name,unitcode,deptcode,psncode,unitcode_trans,deptcode_trans,ts,pndate,status,a0000"+column.toString()+")");
    		insertBuf.append(" select ");
    		insertBuf.append(" pn_id,period,a0101,unitcode,deptcode,psncode,unitcode_trans,deptcode_trans,ts,pndate,status,a0000"+column.toString()+" from "+tempTableName);
    		dao.update(insertBuf.toString());
    		 
    		Table table = new Table(tempTableName);
    		if(dbw.isExistTable(tempTableName, false)){
    			dbw.dropTable(table);
    		}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		try{
    			if(rs!=null)
    				rs.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	return null;
    }
    /**
     * 临时表，为了汇总用，先把数据全部算到临时表中，在更新到中间表里
     * @param pn_id
     * @param filterSQL
     * @param voucherVo
     * @param pndate
     * @param deptcode
     * @param year
     * @param month
     * @return
     */
    public String createTmpTable(String pn_id,String filterSQL,RecordVo voucherVo,String pndate,String deptcode,String year,String month)throws GeneralException{
    	String tableName="t_collect_hy";
    	try{
    		Table table = new Table(tableName);
    		DbWizard dbw=new DbWizard(this.conn);
    		if(dbw.isExistTable(tableName, false)){
    			dbw.dropTable(table);
    		}
    		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
    		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
    	    FieldItem item = DataDictionary.getFieldItem(onlyname);
    	    if(!"".equals(SystemConfig.getPropertyValue("joinOnlyField")))
    	    	item = DataDictionary.getFieldItem(SystemConfig.getPropertyValue("joinOnlyField").toLowerCase());
    	    
    	    if(item==null)
    	    	throw GeneralExceptionHandler.Handle(new Exception("没有定义唯一指标，生成凭证失败!"));
    	    
    		FieldItem a01z0 = new FieldItem();
    		a01z0.setItemid("a01z2");
    		a01z0.setItemtype("D");
    		table.addField(a01z0);
    		
    		FieldItem nbase=new FieldItem();
    		nbase.setItemid("nbase");
    		nbase.setItemtype("A");
    		nbase.setItemlength(3);
    		table.addField(nbase);
    		
    		FieldItem a0100=new FieldItem();
    		a0100.setItemid("a0100");
    		a0100.setItemtype("A");
    		a0100.setItemlength(8);
    		table.addField(a0100);
    		
    		FieldItem a0101=new FieldItem();
    		a0101.setItemid("a0101");
    		a0101.setItemtype("A");
    		a0101.setItemlength(40);
    		table.addField(a0101);
    		
    		FieldItem b0110= new FieldItem();
    		b0110.setItemid("b0110");
    		b0110.setItemtype("A");
    		b0110.setItemlength(30);
    		table.addField(b0110);
    		
    		FieldItem b0110Trans= new FieldItem();
    		b0110Trans.setItemid("unitcode_trans");
    		b0110Trans.setItemtype("A");
    		b0110Trans.setItemlength(30);
    		table.addField(b0110Trans);
    		FieldItem e0122Trans= new FieldItem();
    		e0122Trans.setItemid("deptcode_trans");
    		e0122Trans.setItemtype("A");
    		e0122Trans.setItemlength(30);
    		table.addField(e0122Trans);
    		FieldItem e0122 = new FieldItem();
    		e0122.setItemid("e0122");
    		e0122.setItemtype("A");
    		e0122.setItemlength(40);
    		table.addField(e0122);
    		
    		FieldItem gsb0110=new FieldItem();
    		gsb0110.setItemid("gsb0110");
    		gsb0110.setItemtype("A");
    		gsb0110.setItemlength(30);
    		table.addField(gsb0110);
    		
    		FieldItem gse0122=new FieldItem();
    		gse0122.setItemid("deptcode");
    		gse0122.setItemtype("A");
    		gse0122.setItemlength(40);
    		table.addField(gse0122);
    		
    		
    		FieldItem onlyField=new FieldItem();
    	    onlyField.setItemid("psncode");
    	    onlyField.setItemtype("A");
    	    onlyField.setItemlength(50);
    	    table.addField(onlyField);
    	    
    	    FieldItem pnid=new FieldItem();
    	    pnid.setItemid("pn_id");
    	    pnid.setDecimalwidth(0);
    	    pnid.setItemtype("N");
    	    table.addField(pnid);
    	    
    	    FieldItem unitcode = new FieldItem();
    	    unitcode.setItemid("unitcode");
    	    unitcode.setItemtype("A");
    	    unitcode.setItemlength(30);
    	    table.addField(unitcode);
    	    
    	    FieldItem period = new FieldItem();
    	    period.setItemid("period");
    	    period.setItemtype("D");
    	    table.addField(period);
    	    
    	    FieldItem ts=new FieldItem();
    	    ts.setItemid("ts");
    	    ts.setItemlength(19);
    	    ts.setItemtype("A");
    	    table.addField(ts);
    	    
    	    FieldItem pndateItem = new FieldItem();
    	    pndateItem.setItemid("pndate");
    	    pndateItem.setItemtype("D");
    	    table.addField(pndateItem);
    	    
    	    FieldItem status = new FieldItem();
    	    status.setItemid("status");
    	    status.setItemtype("N");
    	    status.setDecimalwidth(0);
    	    table.addField(status);
    	    
    	    FieldItem a0000 = new FieldItem();
    	    a0000.setItemid("a0000");
    	    a0000.setItemtype("N");
    	    a0000.setDecimalwidth(0);
    	    table.addField(a0000);
    	    
    	  
    	    
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = dao.search("select c_subject from GZ_WarrantList where pn_id="+pn_id);
    		while(rs.next()){
    			String c_subject=rs.getString("c_subject");
    			if(null==c_subject)//xiegh add 处理科目未设置导致空指针错误
    				throw GeneralExceptionHandler.Handle(new Exception("该凭证未设置科目或者设置科目已被删除！ 请到“参数设置/财务凭证定义”重新设置此凭证！"));
    			
    			if("a0000".equalsIgnoreCase(c_subject))//科目编号不能设置成特殊字段
    				throw GeneralExceptionHandler.Handle(new Exception("该凭证下有分录科目为编号不能为特殊字段！"));
    			
    			if(!Character.isLetter(c_subject.charAt(0)))//xiegh 20170621 如果是按月汇总且科目是以非字母开头，则在科目前面添加“HJ_” bug:28859
    				c_subject = "HJ_" + c_subject;
    			if(!dbw.isExistField(tableName, c_subject, false)){
	    			FieldItem c_item = new FieldItem();
	    			c_item.setItemid(c_subject);
	    			c_item.setItemtype("N");
	    			c_item.setItemlength(12);
	    			c_item.setDecimalwidth(8);
	    			table.addField(c_item);
    			}
    		}
    		dbw.createTable(table);
    		
    		Calendar calendar = Calendar.getInstance();
            StringBuffer tsStr=new StringBuffer();
            tsStr.append(calendar.get(Calendar.YEAR)+"-");
            if(calendar.get(Calendar.MONTH)+1<10)
            	tsStr.append("0");
            tsStr.append((calendar.get(Calendar.MONTH)+1));
            tsStr.append("-");
            if(calendar.get(Calendar.DAY_OF_MONTH)<10)
            	tsStr.append("0");
            tsStr.append(calendar.get(Calendar.DAY_OF_MONTH));
            tsStr.append(" ");
            if(calendar.get(Calendar.HOUR_OF_DAY)<10)
            	tsStr.append("0");
            tsStr.append(calendar.get(Calendar.HOUR_OF_DAY));
            tsStr.append(":");
            if(calendar.get(Calendar.MINUTE)<10)
            	tsStr.append("0");
            tsStr.append(calendar.get(Calendar.MINUTE));
            tsStr.append(":");
            if(calendar.get(Calendar.SECOND)<10)
            	tsStr.append("0");
            tsStr.append(calendar.get(Calendar.SECOND));
            
            String periodStr=year+"-"+(month.length()>1?month:("0"+month))+"-01";
    		//将除计算以外的数据，全部导入临时表中
    		StringBuffer insertBuf = new StringBuffer();
    		insertBuf.append("insert into "+tableName);
    		insertBuf.append("(a01z2,nbase,a0100,a0101,b0110,e0122");
    		insertBuf.append(",gsb0110");
    		insertBuf.append(",deptcode");
    		insertBuf.append(",pn_id,unitcode,period,ts,pndate,status");
    		insertBuf.append(",a0000) select max(a00z2) as a00z2,");
    		insertBuf.append("nbase,a0100,max(a0101) as a0101,max(b0110) as b0110,max(e0122) as e0122");
    		insertBuf.append(" ,max(gsb0110) as gsb0110,max(deptcode) as deptcode ");
    		insertBuf.append(","+pn_id+" as pn_id,'"+deptcode.substring(2)+"' as unitcode,");
    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    		{
    			insertBuf.append("to_date('"+periodStr+"','yyyy-mm-dd')");
    		}
    		else
    		{
    			insertBuf.append("'"+periodStr+"'");
    		}
    		insertBuf.append(" as period,'"+tsStr+"' as ts,");
    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    		{
    			insertBuf.append("to_date('"+pndate+"','yyyy-mm-dd')");
    		}
    		else
    		{
    			insertBuf.append("'"+pndate+"'");
    		}
    		insertBuf.append(" as pndate,1 as status ");
    		insertBuf.append(",max(a0000) as a0000 from (");
    		insertBuf.append(" select a00z2,");
    		insertBuf.append("nbase,a0100,a0101,b0110,e0122");
    		if(this.orgid.length()>0&&!isMultiple)
    			insertBuf.append(",case when "+this.orgid+" is null then b0110 else "+this.orgid+" end as gsb0110");
    		else 
    			insertBuf.append(",b0110 as gsb0110 ");
    		if(this.deptid.length()>0&&!isMultiple)
    			insertBuf.append(",case when "+this.deptid+" is null then e0122 else "+this.deptid+" end as deptcode");
    		else
    			insertBuf.append(",e0122 as deptcode ");
    		insertBuf.append(","+pn_id+" as pn_id,'"+deptcode.substring(2)+"' as unitcode,");
    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    		{
    			insertBuf.append("to_date('"+periodStr+"','yyyy-mm-dd')");
    		}
    		else
    		{
    			insertBuf.append("'"+periodStr+"'");
    		}
    		insertBuf.append(" as period,'"+tsStr+"' as ts,");
    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    		{
    			insertBuf.append("to_date('"+pndate+"','yyyy-mm-dd')");
    		}
    		else
    		{
    			insertBuf.append("'"+pndate+"'");
    		}
    		insertBuf.append(" as pndate,1 as status,a0000 ");
    		
    		insertBuf.append(" from salaryhistory where 1=1 ");
    		insertBuf.append(filterSQL);
    		//更新唯一指标值
    		String c_dbase=voucherVo.getString("c_dbase");
    		insertBuf.append(" and (");
    		String[] c_arr=c_dbase.split(",");
    		int j=0;
    		for(int i=0;i<c_arr.length;i++){
    			if(c_arr[i]==null|| "".equals(c_arr[i]))
    				continue;
    			if(j!=0)
    				insertBuf.append(" or ");
    			insertBuf.append(" (UPPER(salaryhistory.nbase)='"+c_arr[i].toUpperCase()+"'");
    			insertBuf.append(" and salaryhistory.a0100 in (select a0100 from "+c_arr[i]+"A01)) ");
    			j++;
    		}
    		insertBuf.append(")) t");
    		insertBuf.append(" group by deptcode,a0100,nbase");
    		dao.update(insertBuf.toString());
    		for(int i=0;i<c_arr.length;i++){
    			if(c_arr[i]==null|| "".equals(c_arr[i]))
    				continue;
    			StringBuffer buf = new StringBuffer();
    			buf.append(" update "+tableName+" set psncode=");
    			buf.append(" (select "+item.getItemid()+" from "+c_arr[i]+"A01 where "+tableName+".a0100="+c_arr[i]+"A01.a0100)");
    			buf.append(" where  UPPER(nbase)='"+c_arr[i].toUpperCase()+"' ");
    			dao.update(buf.toString());
    		}
    		
    		
    		RowSet rowSet=dao.search("select count(*) from "+tableName+" where psncode is null or psncode=''");
    		if(rowSet.next())
    		{ 
    			if(rowSet.getInt(1)>0)
    				throw GeneralExceptionHandler.Handle(new Exception("凭证涉及人员的唯一指标值为空，生成凭证失败!"));
    		}
    		
    		//xiegh 20170525 bug:22817
    		String sql = "select a0101,b0110,e0122,period,psncode from  "+tableName+" where  psncode in( select psncode from "+tableName+" a  group by  psncode having count(psncode)>1) order by psncode";
    		rowSet=dao.search(sql);
    		if(rowSet.next())
    		{ 
    			return exportErroMsg(item, sql);
    		}
    		
    		
    		//更新单位部门转换码
    		insertBuf.setLength(0);
    		insertBuf.append(" update "+tableName+" set unitcode_trans=(select corcode from organization where "+tableName+".unitcode=organization.codeitemid)");
    		dao.update(insertBuf.toString());
    		insertBuf.setLength(0);
    		insertBuf.append(" update "+tableName+" set deptcode_trans=(select corcode from organization where "+tableName+".deptcode=organization.codeitemid)");
    		dao.update(insertBuf.toString());
    		
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return tableName;
    }
    
	private String exportErroMsg(FieldItem item, String sql) throws SQLException, IOException, GeneralException {
		ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn,this.userView);
		HashMap map =new HashMap();
		map.put("columnWidth",4500);//short类型
		String outName =this.userView.getUserName()+"_gz_voucher.xls";
		ArrayList<LazyDynaBean> headlist = new ArrayList<LazyDynaBean>();
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("content","姓    名");
		bean.set("itemid","a0101");
		bean.set("colType","A");
		bean.set("headStyleMap",map);
		headlist.add(bean);
		bean = new LazyDynaBean();
		bean.set("content","所在部门");
		bean.set("itemid","b0110");
		bean.set("codesetid", "UM");
		bean.set("colType","A");
		bean.set("headStyleMap",map);
		headlist.add(bean);
		bean = new LazyDynaBean();
		bean.set("content","所在单位");
		bean.set("itemid","e0122");
		bean.set("colType","A");
		bean.set("codesetid", "UN");
		bean.set("headStyleMap",map);
		headlist.add(bean);
		bean = new LazyDynaBean();
		bean.set("content",item.getItemdesc());
		bean.set("itemid","psncode");
		bean.set("colType","A");
		bean.set("headStyleMap",map);
		headlist.add(bean);
		bean = new LazyDynaBean();
		bean.set("content","计提时间");
		bean.set("itemid","period");
		bean.set("colType","D");
		bean.set("headStyleMap",map);
		headlist.add(bean);
		//导出excel
		excelUtil.exportExcelBySql(outName,"",null, headlist,sql, null,0);//0:从零行开始
		return outName;
	}
    /**
     * 向nc发送消息
     * <?xml version="1.0" encoding = "GB2312" ?>
       <msg>
	      <gzkind>凭证类别，如：MS或YB</gzkind>  <!-- MS表示月度薪资，YB表示年度奖 -->
	      <gzdesc>凭证名称，如：月度工资汇总数据</gzdesc>
	      <datacond>pn_id=n</datacond>   <!-- 中间表取数条件 --> 
	      <gzperiod>工资年月标识，格式：20120701 </ gzperiod >
	      <unitcode>单位/公司编码，多个单位时，以半角逗号分隔。比如：0001,0002</unitcode>
       </msg>
     * @param time
     * @param pn_id
     */
    public String sendMessage(String timeInfo,String pn_id,String status,String a_code){
    	RowSet rs = null;
    	String flag="1";
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
    		RecordVo voucherVo=new RecordVo("gz_warrant");
			voucherVo.setInt("pn_id",Integer.parseInt(pn_id));
			voucherVo=dao.findByPrimaryKey(voucherVo);
			
			StringBuffer buf = new StringBuffer("");
			buf.append(" select distinct unitcode from GZ_WarrantData where ");
			buf.append(" pn_id="+pn_id);
			if(status!=null&&!"all".equalsIgnoreCase(status))
				buf.append(" and status='"+status+"'");
			String[] temps=timeInfo.split("-");
			if(timeInfo!=null&&timeInfo.trim().length()>0&&!"all".equalsIgnoreCase(timeInfo))
			{
				buf.append(" and "+Sql_switcher.year("period")+"="+temps[0]);
				buf.append(" and "+Sql_switcher.month("period")+"="+temps[1]);
			}
			buf.append(getPrivStr(""));
			if(a_code!=null&&a_code.trim().length()>0)
				buf.append(" and DeptCode like '"+a_code.substring(2)+"%'");
			buf.append(" and status<>3");
			rs=dao.search(buf.toString());
			StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
			xml.append("<msg>");
			xml.append("<gzkind>");
			xml.append(voucherVo.getString("c_type").toUpperCase());
			xml.append("</gzkind>");
			xml.append("<gzdesc>");
			xml.append(voucherVo.getString("c_name"));
			xml.append("</gzdesc>");
			xml.append("<datacond>");
			xml.append("pn_id="+pn_id);
			xml.append("</datacond>");
			xml.append("<gzperiod>");
			String year=temps[0];
			String month=temps[1];
			if(temps[1].length()==1)
				month="0"+month;
			xml.append(year+month+"01");
			xml.append("</gzperiod>");
			String unitcode="";
			while(rs.next()){
				flag="2";
				unitcode+=","+rs.getString("unitcode");
			}
			if(unitcode.length()>0)
				unitcode=unitcode.substring(1);
			xml.append("<unitcode>");
			xml.append(unitcode);
			xml.append("</unitcode>");
			xml.append("</msg>");
			if("2".equals(flag))
			{
				StringBuffer tmp=new StringBuffer();
				tmp.append(" update GZ_WarrantData set status=2 where ");
				tmp.append("pn_id="+pn_id);
				if(a_code!=null&&a_code.trim().length()>0)
					tmp.append(" and DeptCode like '"+a_code.substring(2)+"%'");
				if(status!=null&&!"all".equalsIgnoreCase(status))
					tmp.append(" and status='"+status+"'");
				dao.update(tmp.toString());
				String content=voucherVo.getString("content"); 
				Document a_doc =PubFunc.generateDom(content);
				
				 XPath xPath = XPath.newInstance("/voucher/webservice");
				 Element element=null;
				 element = (Element) xPath.selectSingleNode(a_doc);  
				 String NCAddress="";
				 if(element!=null)
					 NCAddress=element.getAttributeValue("url");
				 String nameSpace="";//值怎么来的？
				Service service = new Service(); 
			    Call call = (Call) service.createCall(); 
			    call.setTargetEndpointAddress(new URL(NCAddress));
			    call.setReturnType(XMLType.XSD_STRING);
			    call.setUseSOAPAction(true);
			    call.setOperationName("WarrantDataMsg");	        
			    call.addParameter("message",XMLType.XSD_STRING,ParameterMode.IN);	
			    call.setOperationStyle(org.apache.axis.constants.Style.RPC);
		        call.setOperationUse(org.apache.axis.constants.Use.LITERAL);
			    //call.setOperationStyle(Style.DEFAULT);
		        
			    call.invoke(new Object[] {xml.toString()});  
	
			}
			
    	}catch(Exception e){
    		flag="3";
    		e.printStackTrace();
    	}finally{
    		try{
    			if(rs!=null)
    				rs.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	return flag;
    }
    public String deleteData(String ids,String pn_id){
    	String flag="ok";
    	try{
    		String[] arr=ids.substring(0, ids.length()-1).split("/");
    		//df.format(rowSet.getDate("period"))+"`"+rowSet.getString("unitcode")+"`"+rowSet.getString("deptcode")
    		String sql = " delete from GZ_WarrantData where pn_id=? and unitcode=? and deptcode=? and "+Sql_switcher.year("period")+"=? and "+Sql_switcher.month("period")+"=? and psncode=?";
    		ContentDAO dao = new ContentDAO(this.conn);
    		for(int i=0;i<arr.length;i++){
    			String[] sub_arr=arr[i].split("`");
    			ArrayList list = new ArrayList();
    			list.add(pn_id);
    			list.add(sub_arr[1]);
    			if("".equals(sub_arr[2])){//deptcode为null时，删除条件deptcode=，找不到指定记录，应改为deptcode is null xiegh bug:28990 20170624
    				sql = sql.replace("deptcode=?","deptcode is null");
    			}else{
    				sql = sql.replace("deptcode is null","deptcode=?");
    				list.add(sub_arr[2]);
    			}
    			list.add(sub_arr[0].split("-")[0]);
    			list.add(sub_arr[0].split("-")[1]);
    			list.add(sub_arr[3]);
    			dao.delete(sql, list);
    		}
    	}catch(Exception e){
    		flag="4";
    		e.printStackTrace();
    	}
    	return flag;
    }
    public String getFirstGroupField() {
		return firstGroupField;
	}


	public void setFirstGroupField(String firstGroupField) {
		this.firstGroupField = firstGroupField;
	}


	public String getLastGroupField() {
		return lastGroupField;
	}


	public void setLastGroupField(String lastGroupField) {
		this.lastGroupField = lastGroupField;
	}

}
