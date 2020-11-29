package com.hjsj.hrms.businessobject.report.actuarial_report;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportExcelBo {
	private Connection conn=null;
	private HSSFWorkbook wb=null;
	private HSSFSheet sheet=null;
	private HSSFCellStyle style=null;
	private HSSFCellStyle style_l=null;
	private HSSFCellStyle style_r=null;
	int num=1;
	int rowNum=0;
	
	public ReportExcelBo(Connection con)
	{
		this.conn=con;
		
	}
	
	
	
	
	//输出excel
	public String executeReportExcel(String report_id,String unitcode,String cycle_id,UserView view,String flag)
	{
		ActuarialReportBo ab=new ActuarialReportBo(this.conn,view);
		String fileName="report_"+view.getUserName()+".xls";
		String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName;
		try
		{
			 this.rowNum=this.rowNum+num;
			 this.wb = new HSSFWorkbook();
			 this.sheet = wb.createSheet();
			 if("U03".equals(report_id)) {
                 this.wb.setSheetName(0,"表3财务信息");
             } else  if("U04".equals(report_id)) {
                 this.wb.setSheetName(0,"表4人员统计");
             } else  if("U05".equals(report_id)) {
                 this.wb.setSheetName(0,"表5人员变动和人均福利对照表");
             }
			 this.style = getStyle("c",wb);
			 this.style_l = getStyle("l",wb);
			 this.style_r = getStyle("r",wb);
			 int dataNumber=0;
		
			 ArrayList headList=getHeadList(report_id);
			 writeHeader(report_id,headList); //写表头
			 ArrayList dataList=new ArrayList();
			 if("U03".equals(report_id))
			 {
				 	ArrayList dataHeadList=ab.getDataHeadList_U03();
				 	dataList=ab.getU03DataList(cycle_id,unitcode,dataHeadList,flag);
			 }
			 else if("U04".equals(report_id))
			 {
				    ArrayList dataHeadList=ab.getDataHeadList_U04();
				    dataList=ab.getU04ExcelDataList(cycle_id,unitcode,dataHeadList,flag);
			 }
			 else if("U05".equals(report_id))
			 {
				 	ArrayList dataHeadList_u05=ab.getDataHeadList_U05();
				 	dataList=ab.getDataList_U05(dataHeadList_u05, cycle_id, unitcode,flag);
			 }
			 HashMap orgName=getOrgName(unitcode);
			String collectunit =ab.isCollectUnit(unitcode);
			if(!"1".equals(collectunit)&& "1".equals(flag)) {
                flag="0";
            }
			 //警告信息
			 HashMap waringMap=getWaring(report_id,cycle_id);
			 writeBodyData(report_id,headList,dataList,orgName,waringMap,flag);
			 resetSize(report_id,headList,dataList);
			 FileOutputStream fileOut = new FileOutputStream(url);
			 this.wb.write(fileOut);
			 fileOut.close();	 
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fileName;
	}
	
	
	/**
	 * 调整尺寸
	 *
	 */
	public void resetSize(String report_id,ArrayList headList,ArrayList dataList)
	{
	
		HSSFRow row=null;
		LazyDynaBean abean=null;
		if("U03".equals(report_id)|| "U05".equals(report_id))
		{
			row = sheet.getRow(this.num);
			if(row==null) {
                row = sheet.createRow(this.num);
            }
			row.setHeight((short)500);
			row = sheet.getRow(this.num+1);
			if(row==null) {
                row = sheet.createRow(this.num+1);
            }
			if("U03".equals(report_id)) {
                row.setHeight((short)500);
            } else if("U05".equals(report_id)) {
                row.setHeight((short)700);
            }
			for(short j=0;j<headList.size();j++)
			{
				abean=(LazyDynaBean)headList.get(j);
				String itemid=(String)abean.get("itemid");
				String name=(String)abean.get("name");
				if("unitcode".equals(itemid)) {
                    this.sheet.setColumnWidth(j,(short)15000);
                } else if((itemid.indexOf("U03")==-1&& "U03".equals(report_id))||(itemid.indexOf("U05")==-1&& "U05".equals(report_id)))
				{
					if("theyear".equals(itemid)) {
                        this.sheet.setColumnWidth(j,(short)3000);
                    } else if("item_name".equals(itemid)) {
                        this.sheet.setColumnWidth(j,(short)5000);
                    } else {
                        this.sheet.setColumnWidth(j,(short)10000);
                    }
				}
				else {
                    this.sheet.setColumnWidth(j,(short)5000);
                }
			
			}
		}
		if("U03".equals(report_id))
		{
			for(short j=0;j<headList.size();j++)
			{
				abean=(LazyDynaBean)headList.get(j);
				String itemid=(String)abean.get("itemid");
				String name=(String)abean.get("name");
				if("unitname".equals(itemid)) {
                    this.sheet.setColumnWidth(j,(short)10000);
                } else {
                    this.sheet.setColumnWidth(j,(short)5000);
                }
			
			}
			
		}
		if("U04".equals(report_id))
		{
			row = sheet.getRow(this.num);
			if(row==null) {
                row = sheet.createRow(this.num);
            }
			row.setHeight((short)500);		
			for(short j=0;j<headList.size();j++)
			{
				abean=(LazyDynaBean)headList.get(j);
				String itemid=(String)abean.get("itemid");
				String name=(String)abean.get("name");
				if("unitcode".equals(itemid)) {
                    this.sheet.setColumnWidth(j,(short)15000);
                } else if(itemid.indexOf("U04")==-1)
				{
					if("theyear".equals(itemid)) {
                        this.sheet.setColumnWidth(j,(short)3000);
                    } else {
                        this.sheet.setColumnWidth(j,(short)10000);
                    }
				}
				else {
                    this.sheet.setColumnWidth(j,(short)4000);
                }
			}
			
			
		}
	}
	
	//写表体信息
	public void writeBodyData(String report_id,ArrayList headList,ArrayList dataList,HashMap orgName,HashMap waringMap,String flag)
	{
		LazyDynaBean abean=null;
		LazyDynaBean _bean=null;
		LazyDynaBean _bean2=null;
		LazyDynaBean _bean3=null;
		String preunitcode="";
		String waring ="";
		String waringunitcode="";
		short n =0;
		int p =0;
		
		for(int i=0;i<dataList.size();i++)
		{
			_bean=(LazyDynaBean)dataList.get(i);
			this.rowNum++;
			short m =0;
		
			for(short j=0;j<headList.size();j++)
			{
				abean=(LazyDynaBean)headList.get(j);
				String itemid=(String)abean.get("itemid");
				String name=(String)abean.get("name");
				String style=(String)abean.get("style");
				if("unitcode".equals(itemid)){
					_bean2=(LazyDynaBean)orgName.get(_bean.get("unitcode"));
				if(_bean2!=null&&_bean2.get("grade")!=null&&_bean2.get("unitname")!=null&& "2".equals(_bean2.get("grade").toString().trim()))	{
					preunitcode =_bean2.get("unitname").toString();
					executeCell2(this.rowNum,m,_bean2.get("unitname").toString(),style,"A",null);
				}else{
					if(_bean2==null){
						if("U03".equals(report_id)|| "U05".equals(report_id)) {
                            executeCell2(this.rowNum,m,"填报单位不存在",style,"A",null);
                        } else{
							executeCell2(this.rowNum,m,"",style,"A",null);
						}
					}else{
						if("".equals(preunitcode)&&_bean2!=null&&_bean2.get("parentid")!=null){
							_bean3 =(LazyDynaBean)orgName.get(_bean2.get("parentid"));
							if(_bean3!=null&&_bean3.get("grade")!=null&&_bean3.get("unitname")!=null&& "2".equals(_bean3.get("grade").toString().trim())){
							executeCell2(this.rowNum,m,_bean3.get("unitname").toString(),style,"A",null);
							preunitcode=_bean3.get("unitname").toString();
							}else{
								executeCell2(this.rowNum,m,"",style,"A",null);	
							}
						}else{
					executeCell2(this.rowNum,m,preunitcode,style,"A",null);
						}
					}
				}
				m++;
				if(_bean2!=null&&_bean2.get("grade")!=null&&_bean2.get("unitname")!=null&& "3".equals(_bean2.get("grade").toString().trim()))	{
					executeCell2(this.rowNum,m,_bean2.get("unitname").toString(),style,"A",null);
					}else{
						executeCell2(this.rowNum,m,"",style,"A",null);
					}
				}
				else if("t3_desc".equals(itemid)){
				//	waringMap
					if(_bean!=null&&_bean.get("unitcode")!=null){
						if(i==0) {
                            waringunitcode =(String)_bean.get("unitcode");
                        }
							
						if(_bean.get("unitcode").equals(waringunitcode)){
							waring =waringMap.get(_bean.get("unitcode"))==null?"":(String)waringMap.get(_bean.get("unitcode"));
						n++;
						}
						else{
							if(p==0) {
                                executeCell3(this.rowNum-n,m,this.rowNum-1,m,waring,style,"A",null);
                            } else {
                                executeCell3(this.rowNum-n-1,m,this.rowNum-1,m,waring,style,"A",null);
                            }
							//executeCell(this.rowNum,(short)4,this.rowNum,(short)5,"离休人员","c",font);
							n=0;
							p++;
							waringunitcode =(String)_bean.get("unitcode");	
						}
						if(i==dataList.size()-1&& "1".equals(flag)){
//							if(dataList.size()<5){//是基层单位
//								executeCell3(this.rowNum-n+1,m,this.rowNum,m,waring,style,"A",null);
//							}else
							executeCell3(this.rowNum-n,m,this.rowNum,m,waring,style,"A",null);
						}else if(i==dataList.size()-1&&!"1".equals(flag)){
							executeCell3(this.rowNum-n+1,m,this.rowNum,m,waring,style,"A",null);
						}
					
					}
				}else if("t5_desc".equals(itemid)){

					//	waringMap
						if(_bean!=null&&_bean.get("unitcode")!=null){
							if(i==0) {
                                waringunitcode =(String)_bean.get("unitcode");
                            }
								
							if(_bean.get("unitcode").equals(waringunitcode)){
								waring =waringMap.get(_bean.get("unitcode"))==null?"":(String)waringMap.get(_bean.get("unitcode"));
							n++;
							}
							else{
								if(p==0) {
                                    executeCell3(this.rowNum-n,m,this.rowNum-1,m,waring,style,"A",null);
                                } else {
                                    executeCell3(this.rowNum-n-1,m,this.rowNum-1,m,waring,style,"A",null);
                                }
								//executeCell(this.rowNum,(short)4,this.rowNum,(short)5,"离休人员","c",font);
								n=0;
								p++;
								waringunitcode =(String)_bean.get("unitcode");	
							}
							if(i==dataList.size()-1&& "1".equals(flag)){
//								if(dataList.size()<5){
//									executeCell3(this.rowNum-n+1,m,this.rowNum,m,waring,style,"A",null);
//								}else
								executeCell3(this.rowNum-n,m,this.rowNum,m,waring,style,"A",null);
							}else if(i==dataList.size()-1&&!"1".equals(flag)){
								executeCell3(this.rowNum-n+1,m,this.rowNum,m,waring,style,"A",null);
							}
						
						}
					
				}
				else
				{
					if("U05".equals(report_id)&&!"item_name".equals(itemid))
					{
						LazyDynaBean bean=(LazyDynaBean)_bean.get(itemid);
						executeCell2(this.rowNum,m,(String)bean.get("value"),style,"A",null);
					}
					else {
                        executeCell2(this.rowNum,m,(String)_bean.get(itemid),style,"A",null);
                    }
				}
				m++;
				
			}
			
		}
		
	}
	//写表体信息
	public void writeBodyWarn(String report_id,ArrayList headList,ArrayList dataList)
	{
		LazyDynaBean abean=null;
		LazyDynaBean _bean=null;
		for(int i=0;i<dataList.size();i++)
		{
			_bean=(LazyDynaBean)dataList.get(i);
			this.rowNum++;
			short m =0;
		
			for(short j=0;j<headList.size();j++)
			{
				abean=(LazyDynaBean)headList.get(j);
				String itemid=(String)abean.get("itemid");
				String style=(String)abean.get("style");
					
						executeCell2(this.rowNum,m,(String)_bean.get(itemid),style,"A",null);
					
					
				
				m++;
				
			}
			
		}
		
	}
	public void writeHeader(String report_id,ArrayList headList)
	{
		LazyDynaBean abean=null;
		
		 HSSFFont font = wb.createFont();
		 font.setFontHeightInPoints((short)10);
		 font.setFontName("宋体");
		 font.setBold(true);
		 short j =0;
		 
		if("U03".equals(report_id))
		{
			
			for(short i=0;i<headList.size();i++)
			{
				abean=(LazyDynaBean)headList.get(i);
				String itemid=(String)abean.get("itemid");
				String name=(String)abean.get("name");
				if(itemid.indexOf("U03")==-1)
				{
					if("unitcode".equals(itemid)){
						name ="二级单位";
					executeCell(this.rowNum,j,this.rowNum+1,j,name,"c",font);
					j++;
					name ="三级单位";
					executeCell(this.rowNum,j,this.rowNum+1,j,name,"c",font);
					}else{
						executeCell(this.rowNum,j,this.rowNum+1,j,name,"c",font);
					}
				}
				else
				{
					if(i==4)
					{
						executeCell(this.rowNum,(short)4,this.rowNum,(short)5,"离休人员","c",font);
						executeCell(this.rowNum,(short)6,this.rowNum,(short)7,"退休人员","c",font);
						executeCell(this.rowNum,(short)8,this.rowNum,(short)9,"内退人员","c",font);
						executeCell(this.rowNum,(short)10,this.rowNum,(short)10,"遗属","c",font);
					}
					executeCell2(this.rowNum+1,j,name,"c","A",font);
				}
				j++;
			}
			this.rowNum++;
		}
		if("U04".equals(report_id))
		{
			for(short i=0;i<headList.size();i++)
			{
				abean=(LazyDynaBean)headList.get(i);
				String itemid=(String)abean.get("itemid");
				String name=(String)abean.get("name");
				if("unitcode".equals(itemid)){
					name = "二级单位";
				executeCell2(this.rowNum,j,name,"c","A",font);
				j++;
				name = "三级单位";
				executeCell2(this.rowNum,j,name,"c","A",font);
				}else{
					executeCell2(this.rowNum,j,name,"c","A",font);
				}
				j++;
			}
		}
		if("U05".equals(report_id))
		{
			
			for(short i=0;i<headList.size();i++)
			{
				abean=(LazyDynaBean)headList.get(i);
				String itemid=(String)abean.get("itemid");
				String name=(String)abean.get("name");
				if(itemid.indexOf("U05")==-1)
				{
					if("unitcode".equals(itemid)){
						name ="二级单位";
					executeCell(this.rowNum,j,this.rowNum+1,j,name,"c",font);
					j++;
					name ="三级单位";
					executeCell(this.rowNum,j,this.rowNum+1,j,name,"c",font);
					}else{
						executeCell(this.rowNum,j,this.rowNum+1,j,name,"c",font);	
					}
				}
				else
				{
					if(i==3)
					{
						executeCell(this.rowNum,(short)3,this.rowNum,(short)5,"离休人员","c",font);
						executeCell(this.rowNum,(short)6,this.rowNum,(short)8,"退休人员","c",font);
						executeCell(this.rowNum,(short)9,this.rowNum,(short)11,"内退人员","c",font);
						executeCell(this.rowNum,(short)12,this.rowNum,(short)13,"遗属","c",font);
					}
					executeCell2(this.rowNum+1,j,name,"c","A",font);
				}
				j++;
			}
			this.rowNum++;
		}
	}
	
	public void writeWarningHeader(String report_id,ArrayList headList)
	{
		LazyDynaBean abean=null;
		
		 HSSFFont font = wb.createFont();
		 font.setFontHeightInPoints((short)10);
		 font.setFontName("宋体");
		 font.setBold(true);
		 short j =0;
		 
		
			
			for(short i=0;i<headList.size();i++)
			{
				abean=(LazyDynaBean)headList.get(i);
				String itemid=(String)abean.get("itemid");
				String name=(String)abean.get("name");
				
					executeCell2(this.rowNum+1,j,name,"c","A",font);
				
				j++;
			}
			this.rowNum++;
		
		
	}
	
//	//取得填报单位名称
//	private String getOrgName(String _unitcode)
//	{
//		StringBuffer str=new StringBuffer("");
//		try
//		{
//			HashMap map=new HashMap();
//			ContentDAO dao=new ContentDAO(this.conn);
//			RowSet rowSet=dao.search("select * from tt_organization");
//			while(rowSet.next())
//			{
//				LazyDynaBean abean=new LazyDynaBean();
//				abean.set("unitcode", rowSet.getString("unitcode"));
//				abean.set("unitname",rowSet.getString("unitname"));
//				abean.set("parentid",rowSet.getString("parentid"));
//				abean.set("grade",rowSet.getString("grade"));
//				map.put((String)abean.get("unitcode"),abean);
//			}
//			LazyDynaBean abean=null;
//			ArrayList list=new ArrayList();
//			while(true)
//			{
//				abean=(LazyDynaBean)map.get(_unitcode);
//				String parentid=(String)abean.get("parentid");
//				String unitcode=(String)abean.get("unitcode");
//				String unitname=(String)abean.get("unitname");
//				
//				list.add(unitname);
//				if(parentid.equals(unitcode))
//					break;
//				_unitcode=parentid;
//			}
//			
//			for(int i=list.size()-1;i>=0;i--)
//			{
//				str.append(" / ");
//				str.append((String)list.get(i));
//			}
//			
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		if(str.length()==0)
//			str.append(" / ");
//		return str.substring(3);
//	}
	//取得填报单位名称
	private HashMap getOrgName(String _unitcode)
	{
		HashMap map2=new HashMap();
		try
		{
	//		HashMap map=new HashMap();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from tt_organization ");
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("unitcode", rowSet.getString("unitcode"));
				abean.set("unitname",rowSet.getString("unitname"));
				abean.set("parentid",rowSet.getString("parentid"));
				abean.set("grade",rowSet.getString("grade"));
				map2.put((String)abean.get("unitcode"),abean);
			}
//			LazyDynaBean abean=null;
			
//			while(true)
//			{
//				abean=(LazyDynaBean)map.get(_unitcode);
//				String parentid=(String)abean.get("parentid");
//				String unitcode=(String)abean.get("unitcode");
//				String unitname=(String)abean.get("unitname");
//				if(abean.get("grade").equals("2")){
//					map2.put(abean.get("unitcode"), unitname);
//					map2.put("2", unitname);
//				}
//				else if(abean.get("grade").equals("3")){
//					map2.put("3", unitname);
//				}
//				
//				if(parentid.equals(unitcode))
//					break;
//				_unitcode=parentid;
//			}
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map2;
	}
	//取得填报单位名称
	private HashMap getWaring(String report_id,String cycle_id)
	{
		HashMap map2=new HashMap();
		try
		{
	
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from U01 where id ="+cycle_id);
			while(rowSet.next())
			{
				if("U03".equals(report_id)){
					map2.put(rowSet.getString("unitcode").toString().trim(),Sql_switcher.readMemo(rowSet,"t3_desc"));	
				}else if("U05".equals(report_id)){
					map2.put(rowSet.getString("unitcode").toString().trim(),Sql_switcher.readMemo(rowSet,"t5_desc"));	
				}				
			}
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map2;
	}
	
	/**
	 * 给出表头列表
	 * @param report_id
	 * @return
	 */
	public ArrayList getHeadList(String report_id)
	{
		ArrayList list=new ArrayList();
		list.add(getLazyDynaBean("填报单位","unitcode","l"));
		if("U03".equals(report_id))
		{
			list.add(getLazyDynaBean("年度","theyear","r"));
			list.add(getLazyDynaBean("人员分类","person_type","l"));
			list.add(getLazyDynaBean("医疗报销费用","U0305","r"));
			list.add(getLazyDynaBean("除医疗报销费用外的其它费用","U0307","r"));
			list.add(getLazyDynaBean("医疗福利","U0309","r"));//医疗报销费用
			list.add(getLazyDynaBean("除医疗福利费用外的其它费用","U0311","r"));//除医疗报销费用外的其它费用
			list.add(getLazyDynaBean("医疗福利","U0313","r"));//医疗报销费用
			list.add(getLazyDynaBean("除医疗福利费用外的其它费用","U0315","r"));//除医疗报销费用外的其它费用
			list.add(getLazyDynaBean("遗属各项福利费用","U0317","r"));
			list.add(getLazyDynaBean("警告信息","t3_desc","l"));
		}
		else if("U04".equals(report_id))
		{
			list.add(getLazyDynaBean("年度","theyear","r"));
			list.add(getLazyDynaBean("人员分类","person_type","l"));
			list.add(getLazyDynaBean("离休人员","U0405","r"));
			list.add(getLazyDynaBean("退休人员","U0407","r"));
			list.add(getLazyDynaBean("内退人员","U0409","r"));
			list.add(getLazyDynaBean("遗属","U0411","r"));
		}
		else if("U05".equals(report_id))
		{
			list.add(getLazyDynaBean("项目","item_name","l"));
			
			list.add(getLazyDynaBean("人数（人）","U0505","r"));
			list.add(getLazyDynaBean("人均医疗福利水平(元/年/人)","U0507","r"));
			list.add(getLazyDynaBean("除医疗福利外其他人均福利水平(元/年/人)","U0509","r"));
			list.add(getLazyDynaBean("人数（人）","U0511","r"));
			list.add(getLazyDynaBean("人均医疗福利水平(元/年/人)","U0513","r"));
			list.add(getLazyDynaBean("除医疗福利外其他人均福利水平(元/年/人)","U0515","r"));
			list.add(getLazyDynaBean("人数（人）","U0517","r"));
			list.add(getLazyDynaBean("人均医疗福利水平(元/年/人)","U0519","r"));
			list.add(getLazyDynaBean("除医疗福利外其他人均福利水平(元/年/人)","U0521","r"));
			list.add(getLazyDynaBean("人数（人）","U0523","r"));
			list.add(getLazyDynaBean("除医疗福利外其他人均福利水平(元/年/人)","U0525","r"));
			list.add(getLazyDynaBean("警告信息","t5_desc","l"));
		}
		return list;
	}
	
	
	public LazyDynaBean getLazyDynaBean(String name,String itemid,String style)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("name",name);
		abean.set("itemid",itemid);
		abean.set("style", style);
		return abean;
	}
	
	
	 public void executeCell2(int rownum,short columnIndex,String value,String style,String type,HSSFFont font)
	 {
		
		 HSSFRow  row = this.sheet.getRow( rownum);
		 if(row==null) {
             row = sheet.createRow(rownum);
         }
		 HSSFCell cell = row.createCell(columnIndex); 
		 if(font==null)
		 {
			 if("c".equalsIgnoreCase(style)) {
                 cell.setCellStyle(this.style);
             } else if("l".equalsIgnoreCase(style)) {
                 cell.setCellStyle(this.style_l);
             } else if("r".equalsIgnoreCase(style)) {
                 cell.setCellStyle(this.style_r);
             }
		 }
		 else
		 {
			 HSSFCellStyle _style=null;
			 if("c".equalsIgnoreCase(style)) {
                 _style= getStyle("c",wb);
             } else if("l".equalsIgnoreCase(style)) {
                 _style= getStyle("l",wb);
             } else if("r".equalsIgnoreCase(style)) {
                 _style= getStyle("r",wb);
             }
			 _style.setFont(font);
			 cell.setCellStyle(_style);
		 }
		 if(value!=null&&value.trim().length()>0&& "N".equalsIgnoreCase(type))
		 {
			 cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			 cell.setCellValue(Double.parseDouble(value));
		 }
		 else
		 {
			 if(value==null) {
                 value="";
             }
			 cell.setCellValue(new HSSFRichTextString(value));
		 }
	 }
	 public void executeCell3(int a,short b,int c,short d,String value,String style,String type,HSSFFont font)
	 {
		 try {
			 HSSFRow  row = this.sheet.getRow( a);
			 if(row==null) {
                 row = sheet.createRow(a);
             }
			 
			 HSSFCell cell = row.createCell(b); 
			 if(font==null)
			 {
				 if("c".equalsIgnoreCase(style)) {
                     cell.setCellStyle(this.style);
                 } else if("l".equalsIgnoreCase(style)) {
                     cell.setCellStyle(this.style_l);
                 } else if("r".equalsIgnoreCase(style)) {
                     cell.setCellStyle(this.style_r);
                 }
			 }
			 else
			 {
				 HSSFCellStyle _style=null;
				 if("c".equalsIgnoreCase(style)) {
                     _style= getStyle("c",wb);
                 } else if("l".equalsIgnoreCase(style)) {
                     _style= getStyle("l",wb);
                 } else if("r".equalsIgnoreCase(style)) {
                     _style= getStyle("r",wb);
                 }
				 _style.setFont(font);
				 cell.setCellStyle(_style);
			 }
			 if(value!=null&&value.trim().length()>0&& "N".equalsIgnoreCase(type))
			 {
				 cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				 cell.setCellValue(Double.parseDouble(value));
			 }
			 else
			 {
				 if(value==null) {
                     value="";
                 }
				 cell.setCellValue(new HSSFRichTextString(value));
			 }
			 short b1=b;
			 while(++b1<=d)
			 {
				 cell = row.createCell(b1);
				 cell.setCellStyle(this.style); 
			 }
			 
			 for(int a1=a+1;a1<=c;a1++)
			 {
				 
				 row = sheet.getRow(a1);
				 if(row==null) {
                     row = sheet.createRow(a1);
                 }
				 b1=b;
				 while(b1<=d)
				 {
					 cell = row.createCell(b1);
					 cell.setCellStyle(this.style);
					 b1++;
				 }
			 }
			 
			 ExportExcelUtil.mergeCell(sheet, a,b,c,d); 
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }
	 
	public void executeCell(int a,short b,int c,short d,String content,String style,HSSFFont font)
	 {
		try {
			HSSFRow row = sheet.getRow(a);	
			if(row==null) {
                row = sheet.createRow(a);
            }
			row.setHeight((short)400);
			HSSFCell cell = row.createCell(b);
			
			if(font==null)
			{
				if("c".equalsIgnoreCase(style)) {
                    cell.setCellStyle(this.style);
                } else if("l".equalsIgnoreCase(style)) {
                    cell.setCellStyle(this.style_l);
                } else if("r".equalsIgnoreCase(style)) {
                    cell.setCellStyle(this.style_r);
                }
			}
			else
			{
				HSSFCellStyle _style=null;
				if("c".equalsIgnoreCase(style)) {
                    _style= getStyle("c",wb);
                } else if("l".equalsIgnoreCase(style)) {
                    _style= getStyle("l",wb);
                } else if("r".equalsIgnoreCase(style)) {
                    _style= getStyle("r",wb);
                }
				_style.setFont(font);
				cell.setCellStyle(_style);
			}
			cell.setCellValue(new HSSFRichTextString(content));
			
			short b1=b;
			while(++b1<=d)
			{
				cell = row.createCell(b1);
				cell.setCellStyle(this.style); 
			}
			
			for(int a1=a+1;a1<=c;a1++)
			{
				row = sheet.getRow(a1);
				if(row==null) {
                    row = sheet.createRow(a1);
                }
				
				b1=b;
				while(b1<=d)
				{
					cell = row.createCell(b1);
					cell.setCellStyle(this.style);
					b1++;
				}
			}
			
			ExportExcelUtil.mergeCell(sheet, a,b,c,d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	

	public HSSFCellStyle getStyle(String align,HSSFWorkbook wb)
	{
		HSSFCellStyle a_style=wb.createCellStyle();
		a_style.setBorderBottom(BorderStyle.THIN);
		a_style.setBottomBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderLeft(BorderStyle.THIN);
		a_style.setLeftBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderRight(BorderStyle.THIN);
		a_style.setRightBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderTop(BorderStyle.THIN);
		a_style.setTopBorderColor(HSSFColor.BLACK.index);
		a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		a_style.setWrapText(true);
		if("c".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.CENTER);
        } else if("l".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.LEFT);
        } else if("r".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.RIGHT);
        }
		return a_style;
	}
	//输出excel
	public String exportWarningExcel(String report_id,String unitcode,String cycle_id,UserView view)
	{
		ActuarialReportBo ab=new ActuarialReportBo(this.conn,view);
		String fileName="report_"+view.getUserName()+".xls";
		String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName;
		try
		{
			 this.rowNum=this.rowNum+num;
			 this.wb = new HSSFWorkbook();
			 this.sheet = wb.createSheet();
			 if("U03".equals(report_id)) {
                 this.wb.setSheetName(0,"表3财务信息");
             } else  if("U04".equals(report_id)) {
                 this.wb.setSheetName(0,"表4人员统计");
             } else  if("U05".equals(report_id)) {
                 this.wb.setSheetName(0,"表5人员变动和人均福利对照表");
             } else  if("U01".equals(report_id)) {
                 this.wb.setSheetName(0,"表1特别事项表");
             }
			 this.style = getStyle("c",wb);
			 this.style_l = getStyle("l",wb);
			 this.style_r = getStyle("r",wb);
			 int dataNumber=0;
		
			 ArrayList headList=new ArrayList();
			 headList.add(getLazyDynaBean("填报单位","unitname","l"));
				if("U03".equals(report_id))
				{
					headList.add(getLazyDynaBean("警告信息","t3_desc","l"));
				}else if("U01".equals(report_id)){
					headList.add(getLazyDynaBean("重大福利制度调整","u0101","l"));
					headList.add(getLazyDynaBean("重大人员变动","u0103","l"));
				}else if("U05".equals(report_id)){
					headList.add(getLazyDynaBean("警告信息","t5_desc","l"));
				}
			
			 writeWarningHeader(report_id,headList); //写表头
			 ArrayList dataList=new ArrayList();
			
				 	ArrayList dataHeadList=ab.getWarnHeadList_U03();
				 	dataList=ab.getU01WarnList(cycle_id,unitcode,dataHeadList);
			 
			 
	//		 HashMap orgName=getOrgName(unitcode);
			 //警告信息
	//		 HashMap waringMap=getWaring(report_id,cycle_id);
			 writeBodyWarn(report_id,headList,dataList);
			 resetSize(report_id,headList,dataList);
			 FileOutputStream fileOut = new FileOutputStream(url);
			 this.wb.write(fileOut);
			 fileOut.close();	 
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fileName;
	}
}
