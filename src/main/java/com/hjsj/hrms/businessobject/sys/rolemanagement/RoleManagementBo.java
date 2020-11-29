package com.hjsj.hrms.businessobject.sys.rolemanagement;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.sys.VersionControl;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * <p>Title:角色管理导出Excel</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 7, 2008:11:04:54 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class RoleManagementBo {
	private Connection conn = null;
	private RowSet frowset;
	private EncryptLockClient lock;
	
	public RoleManagementBo(Connection a_con){
		this.conn=a_con;
	}
	
	/*
	 * 头信息t_sys_role
	 */
	public ArrayList roletop(){
		ArrayList list = new ArrayList();
		try{
		StringBuffer sub = new StringBuffer();
		StringBuffer sql = new StringBuffer("select role_name,role_id,role_property from t_sys_role");
			sql.append(" order by role_id");
			ContentDAO da = new ContentDAO(this.conn);
			this.frowset = da.search(sql.toString());
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("role", this.frowset.getString("role_name"));
				bean.set("role_id", this.frowset.getString("role_id"));
				bean.set("role_property", this.frowset.getString("role_property"));
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//DBNAME人员库信息
	public ArrayList getdbname(){
		ArrayList list = new ArrayList();
		try{
			StringBuffer sub = new StringBuffer();
			StringBuffer sql = new StringBuffer("select DBName,pre from DBNAME");
						 sql.append(" order by DbId");
				ContentDAO da = new ContentDAO(this.conn);
			this.frowset = da.search(sql.toString());
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("DBName", this.frowset.getString("DBName"));
				bean.set("dbid", this.frowset.getString("pre"));
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	/*
	 * excel人员库信息
	 */
	public void setStaff(ArrayList toplist,ArrayList dbnamelist,HSSFWorkbook workbook,HSSFRow row,HSSFCell cell){
		try{
			HSSFSheet  sheet = null;
			sheet = workbook.createSheet(ResourceFactory.getProperty("infor.menu.base")); //生成一张表;人员库
			// 创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器. 
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			// 定义注释的大小和位置,详见文档
			short h=0;//top
			short k=2;
			short t=1;//序号
			short y=0;
//			 设置列宽,参数一，9列
			sheet.setColumnWidth((short)0,(short)2000);
			sheet.setColumnWidth((short)1,(short)3000);
			row = sheet.createRow(h+0); // 定义是那一页的row
//			 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
			ExportExcelUtil.mergeCell(sheet, h+0,(short)0,h+1,(short)8); 
			cell=row.createCell((short)(0));  //写入的单元各位置;
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.namshouquan")); //人员库授权
    		cell.setCellStyle(this.setDateStyle(workbook));
    		//写入top
    		//row= sheet.createRow(h+2);
    		row = sheet.getRow(h+2);
			if(row==null) {
                row = sheet.createRow(h+2);
            }

    		cell=row.createCell((short)(0));  //写入的单元各位置;
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.gather.xuhao"));              //序号
    		cell.setCellStyle(this.settopStyle(workbook));
    		cell=row.createCell((short)(1));
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("kq.emp.change.nbase"));  //人员库
    		cell.setCellStyle(this.settopStyle(workbook));
    		for(int i=0;i<toplist.size();i++){
    			LazyDynaBean bean = (LazyDynaBean)toplist.get(i);
    			//row= sheet.createRow(h+2);
    			row = sheet.getRow(h+2);
    			if(row==null) {
                    row = sheet.createRow(h+2);
                }
    			cell=row.createCell((short)(k));
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        		cell.setCellValue((String)bean.get("role"));
        		cell.setCellStyle(this.settopStyle(workbook));
        		//定义位置
        		HSSFComment comment = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) k , (h+2) , ( short ) (k) , (h+2) ));
        		//找到值
        		comment.setString( new HSSFRichTextString((String)bean.get("role_id")));
        		//comment.setAuthor("A001"); 作者
        		//写入
        		cell.setCellComment(comment);
        		k++;
        		short list=row.getLastCellNum();                       //获得某一行的列数；
        		list--; //兼容excel2007
        		sheet.setColumnWidth((short)list,(short)3000);
    		}
    		for(int n=0;n<dbnamelist.size();n++){
    			LazyDynaBean beans = (LazyDynaBean)dbnamelist.get(n);
    			//row= sheet.createRow(h+3);
    			row = sheet.getRow(h+3);
    			if(row==null) {
                    row = sheet.createRow(h+3);
                }
    			cell=row.createCell((short)(1));
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        		cell.setCellValue((String)beans.get("DBName"));
        		//注释
        		HSSFComment comment = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 1 , (h+3) , ( short ) 1 , (h+3) ));
        		comment.setString( new HSSFRichTextString((String)beans.get("dbid")));
        		cell.setCellComment(comment);
        		for(int r=2;r<k;r++){
        			cell=row.createCell((short)(r));
        			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            		cell.setCellValue("无");
        		}
        		//        		cell.setCellStyle(this.setbodyStyle(workbook));
        		cell=row.createCell((short)(0));
        		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        		cell.setCellValue(t);

        		t++;
        		h++;
    		}
    		//excel上生成下拉筐
//    		short m=0;
//    		row = sheet.createRow(m+0);
//    		cell=row.createCell((short)26);
//    		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.have"));  //有
//    		m++;
//    		row = sheet.createRow(m+0);
//    		cell=row.createCell((short)26);
//    		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.nothing"));   //无
//    		m++;
//    		String strFormula = "$AA$1:$AA$2";  //表示AA列1-2行作为下拉列表来源数据 
//    		HSSFDataValidation data_validation = new HSSFDataValidation((short)  
//    				(3),(short)2,(short)(6),(short)(k-1));
//    		  data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST); 
//    		  data_validation.setFirstFormula(strFormula);  
//              data_validation.setSecondFormula(null);  
//              data_validation.setExplicitListFormula(true);  
//              data_validation.setSurppressDropDownArrow(false);  
//              data_validation.setEmptyCellAllowed(false);  
//              data_validation.setShowPromptBox(false);
//              data_validation.setShowErrorBox(true);
//
//    		 sheet.addValidationData(data_validation);

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	/*
	 * 指标子集授权查询
	 */
	
	public HashMap infomap(){
		HashMap map = new HashMap();
		try{
			ArrayList list = new ArrayList();
//			ArrayList subsetlist= new ArrayList();
			StringBuffer buf = new StringBuffer("select classname,classpre from informationclass  order by  inforid");
			ContentDAO da = new ContentDAO(this.conn);
			this.frowset = da.search(buf.toString());
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("classname", this.frowset.getString("classname")+ResourceFactory.getProperty("system.param.sysinfosort.subset"));//子集
				bean.set("classpre", this.frowset.getString("classpre"));
				list.add(bean);
			}
			HashMap amap= new HashMap();
			for(int i=0;i<list.size();i++){
				LazyDynaBean prebean = (LazyDynaBean)list.get(i);
				String classpre = (String)prebean.get("classpre");
				StringBuffer subset= new StringBuffer("select fieldsetid,customdesc from fieldset  where fieldsetid like '");
					subset.append(classpre+"%' and useflag='1' order by displayorder ");
					ContentDAO das = new ContentDAO(this.conn);
					this.frowset = das.search(subset.toString());
					while(this.frowset.next()){
						LazyDynaBean infobean = new LazyDynaBean();
						infobean.set("fieldsetid", this.frowset.getString("fieldsetid"));
						infobean.set("customdesc", this.frowset.getString("customdesc"));
//						infobean.set("classpre", classpre);
						if(amap.get(classpre.toUpperCase())==null)
						{
							ArrayList setlist = new ArrayList();
							setlist.add(infobean);
							amap.put(classpre.toUpperCase(), setlist); //把list放到map里
						}
						else
						{
							ArrayList setlist=(ArrayList)amap.get(classpre.toUpperCase());
							setlist.add(infobean);
							amap.put(classpre.toUpperCase(), setlist);
						}
					}
					
			}
			ArrayList sublist = new ArrayList();
			LazyDynaBean mubean = new LazyDynaBean();
			mubean.set("ryid", "A00");
			mubean.set("ryname", "人员多媒体子集");
			mubean.set("dwid","B00");
			mubean.set("dwname","单位多媒体子集");
			mubean.set("id","K00");
			mubean.set("subsetname","职位多媒体子集");
			sublist.add(mubean);
			//指标
			HashMap zbmap= new HashMap();
			for(int p=0;p<list.size();p++){
				LazyDynaBean indexbean = (LazyDynaBean)list.get(p);
				ArrayList indexlist = (ArrayList)amap.get((String)indexbean.get("classpre"));
				if(indexlist!=null){
					for(int u=0;u<indexlist.size();u++){
						LazyDynaBean zbbean = (LazyDynaBean)indexlist.get(u);
						String fieldsetid = (String)zbbean.get("fieldsetid");
						if("A01".equalsIgnoreCase(fieldsetid)){
							StringBuffer subbean=new StringBuffer("select fieldsetid,itemid,itemdesc from fielditem where fieldsetid='");
							subbean.append(fieldsetid+"' and useflag='1' order by displayid");
							ContentDAO das = new ContentDAO(this.conn);
							this.frowset = das.search(subbean.toString());
							ArrayList setlist = new ArrayList();
							LazyDynaBean itembean1 = new LazyDynaBean();
							itembean1.set("fieldsetid","A01");
							itembean1.set("itemid", "B0110");
							itembean1.set("itemdesc", "单位名称");
							setlist.add(itembean1);
							zbmap.put("A01", setlist);
							LazyDynaBean itembean2 = new LazyDynaBean();
							itembean2.set("fieldsetid","A01");
							itembean2.set("itemid", "E01A1");
							itembean2.set("itemdesc", "职位名称");
							setlist.add(itembean2);
							zbmap.put("A01", setlist);
							while(this.frowset.next()){
								LazyDynaBean itembean = new LazyDynaBean();
								itembean.set("fieldsetid", this.frowset.getString("fieldsetid"));
								itembean.set("itemid", this.frowset.getString("itemid"));
								itembean.set("itemdesc", this.frowset.getString("itemdesc"));
								if(zbmap.get(frowset.getString("fieldsetid").toUpperCase())==null)
								{
									
									setlist.add(itembean);
									zbmap.put(frowset.getString("fieldsetid").toUpperCase(), setlist); //把list放到map里
								}
								else
								{
									setlist=(ArrayList)zbmap.get(frowset.getString("fieldsetid").toUpperCase());
									setlist.add(itembean);
									zbmap.put(frowset.getString("fieldsetid").toUpperCase(), setlist);
								}
							}
							
						}else{
							StringBuffer subbean=new StringBuffer("select fieldsetid,itemid,itemdesc from fielditem where fieldsetid='");
							subbean.append(fieldsetid+"' and useflag='1' order by displayid");
							ContentDAO das = new ContentDAO(this.conn);
							this.frowset = das.search(subbean.toString());
							while(this.frowset.next()){
								LazyDynaBean itembean = new LazyDynaBean();
								itembean.set("fieldsetid", this.frowset.getString("fieldsetid"));
								itembean.set("itemid", this.frowset.getString("itemid"));
								itembean.set("itemdesc", this.frowset.getString("itemdesc"));
								if(zbmap.get(frowset.getString("fieldsetid").toUpperCase())==null)
								{
									ArrayList setlist = new ArrayList();
									setlist.add(itembean);
									zbmap.put(frowset.getString("fieldsetid").toUpperCase(), setlist); //把list放到map里
								}
								else
								{
									ArrayList setlist=(ArrayList)zbmap.get(frowset.getString("fieldsetid").toUpperCase());
									setlist.add(itembean);
									zbmap.put(frowset.getString("fieldsetid").toUpperCase(), setlist);
								}
							}
						}	
					}
				}
			}
			map.put("1", list);
			map.put("2", amap);
			map.put("3", zbmap);
			map.put("4", sublist);
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	/*
	 * 指标子集授权写入excel
	 */
	public void setinfoexcel(ArrayList classlist,HashMap fielsetidmap,HashMap itemdescmap,HSSFWorkbook workbook,HSSFRow row,HSSFCell cell,ArrayList toplist,ArrayList sublist){
		try{
			HSSFSheet sheet = workbook.createSheet(ResourceFactory.getProperty("kjg.title.zjshouquan")); //生成一张表; 子集指标授权
			// 创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器. 
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			CellRangeAddress region =null;
			short h=0;
			short k=3;
			short t=1;
//			 设置列宽,参数一，10列
			sheet.setColumnWidth((short)0,(short)2000);
			sheet.setColumnWidth((short)1,(short)5000);
			sheet.setColumnWidth((short)2,(short)8000);
			row = sheet.getRow(0);
  			if(row==null) {
                row = sheet.createRow(0);
            }
			//合并单元格
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,2,(short)10);
			cell=row.createCell((short)(0));
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.zjshouquan"));
    		cell.setCellStyle(this.setDateStyle(workbook));
    		//写入top
    		row = sheet.getRow(3);
  			if(row==null) {
                row = sheet.createRow(3);  //row位置
            }
    		cell=row.createCell((short)(0));  //cell位置
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.gather.xuhao"));    //序号
    		cell.setCellStyle(this.settopStyle(workbook));
    		cell=row.createCell((short)(1));
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("menu.table"));     //子集
    		cell.setCellStyle(this.settopStyle(workbook));
    		cell=row.createCell((short)(2));
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("menu.field"));   //指标
    		cell.setCellStyle(this.settopStyle(workbook));
    		short list=0;
    		for(int i=0;i<toplist.size();i++){        //黄色头
    			LazyDynaBean bean = (LazyDynaBean)toplist.get(i);
    			row = sheet.getRow(3);
	  			if(row==null) {
                    row= sheet.createRow(3);
                }
    			cell=row.createCell((short)k);
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    			cell.setCellValue((String)bean.get("role"));
    			cell.setCellStyle(this.settopStyle(workbook));
    			//定义位置
        		HSSFComment comment = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) k , 2 , ( short ) (k+1) , 3 ));
        		//找到值
        		comment.setString( new HSSFRichTextString((String)bean.get("role_id")));
        		//comment.setAuthor("A001"); 作者
        		//写入
        		cell.setCellComment(comment);
    			list = row.getLastCellNum();
    			list--; //兼容excel2007
    			sheet.setColumnWidth((short)list,(short)3000);
    			k++;
    		}
    		for(int n=0;n<classlist.size();n++){   //ABK 浅绿色 
    			LazyDynaBean bean = (LazyDynaBean)classlist.get(n);
    			//row=sheet.createRow(h+4);
    			row = sheet.getRow(h+4);
    			if(row==null) {
                    row = sheet.createRow(h+4);
                }
    			//合并单元格
    			if(list > 0) {
    				region = new CellRangeAddress(h+4,h+4,(short)0,(short)list);
    				sheet.addMergedRegion(region);
    				HSSFCellStyle cs=this.setsubStyle(workbook);
    				for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
    					//row = sheet.createRow(p);
    					row = sheet.getRow(p);
    					if(row==null) {
                            row = sheet.createRow(p);
                        }
    					for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
    						cell = row.createCell((short)o);
    						cell.setCellStyle(cs);
    					}
    				}
    			}
    			cell = row.createCell((short)0);
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    			cell.setCellValue((String)bean.get("classname"));
    			cell.setCellStyle(this.setsubStyle(workbook));
    			h++;
    			ArrayList subsetlist = (ArrayList)fielsetidmap.get((String)bean.get("classpre"));
    			if(subsetlist!=null){
    				for(int e=0;e<subsetlist.size();e++){
        				LazyDynaBean subsetbean = (LazyDynaBean)subsetlist.get(e);
        				//row = sheet.createRow(h+4);
        				row = sheet.getRow(h+4);
        				if(row==null) {
                            row = sheet.createRow(h+4);
                        }
        				cell=row.createCell((short)1);
        				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            			cell.setCellValue((String)subsetbean.get("customdesc"));
            			//增加批注;定义位置
                		HSSFComment comments = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 1 , (h+4) , ( short ) 2 , (h+5) ));
                		comments.setString( new HSSFRichTextString((String)subsetbean.get("fieldsetid")));
                		cell.setCellComment(comments);
                		for(int y=3;y<k;y++){
                			cell=row.createCell((short)(y));
            				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                			cell.setCellValue("无");
                		}
            			cell=row.createCell((short)2);
            			//序号
            			cell=row.createCell((short)0);
        				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            			cell.setCellValue(t);                               
//            			cell.setCellStyle(this.setbodyStyle(workbook));
            			t++;
            			h++;
            			ArrayList indexlist  = (ArrayList)itemdescmap.get((String)subsetbean.get("fieldsetid"));
            			for(int r=0;r<indexlist.size();r++){
            				LazyDynaBean indexbean = (LazyDynaBean)indexlist.get(r);
            				//row = sheet.createRow(h+4);
            				row = sheet.getRow(h+4);
            				if(row==null) {
                                row = sheet.createRow(h+4);
                            }
            				cell=row.createCell((short)2);
            				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            				cell.setCellValue((String)indexbean.get("itemdesc"));
//            				//注释
            				HSSFComment comm = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 2 , (h+4) , ( short ) 3 , (h+6) ));
                    		comm.setString( new HSSFRichTextString((String)indexbean.get("itemid")));
                    		cell.setCellComment(comm);
                    		for(int y=3;y<k;y++){
                    			cell=row.createCell((short)(y));
                				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    			cell.setCellValue("无");
                    		}
            				//序号
            				cell=row.createCell((short)0);
            				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                			cell.setCellValue(t);
                			t++;
            				h++;
            			}
        			}
    			}
    		}
    		for(int ic=0;ic<sublist.size();ic++){
    			LazyDynaBean mediabean = (LazyDynaBean)sublist.get(ic);
    			//row = sheet.createRow(h+4);
    			row = sheet.getRow(h+4);
    			if(row==null) {
                    row = sheet.createRow(h+4);
                }
    			cell=row.createCell((short)1);
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    			cell.setCellValue((String)mediabean.get("ryname"));
    			HSSFComment comm = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 2 , (h+4) , ( short ) 3 , (h+6) ));
        		comm.setString( new HSSFRichTextString((String)mediabean.get("ryid")));
        		cell.setCellComment(comm);
//    			序号
				cell=row.createCell((short)0);
				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    			cell.setCellValue(t);
    			for(int y=3;y<k;y++){
        			cell=row.createCell((short)(y));
    				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        			cell.setCellValue("无");
        		}
    			t++;
    			h++;
    			//row = sheet.createRow(h+4);
    			row = sheet.getRow(h+4);
    			if(row==null) {
                    row = sheet.createRow(h+4);
                }
    			cell=row.createCell((short)1);
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    			cell.setCellValue((String)mediabean.get("dwname"));
    			HSSFComment comms = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 2 , (h+4) , ( short ) 3 , (h+6) ));
        		comms.setString( new HSSFRichTextString((String)mediabean.get("dwid")));
        		cell.setCellComment(comms);
//    			序号
				cell=row.createCell((short)0);
				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    			cell.setCellValue(t);
    			for(int y=3;y<k;y++){
        			cell=row.createCell((short)(y));
    				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        			cell.setCellValue("无");
        		}
    			t++;
    			h++;
    			//row = sheet.createRow(h+4);
    			row = sheet.getRow(h+4);
    			if(row==null) {
                    row = sheet.createRow(h+4);
                }
    			cell=row.createCell((short)1);
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    			cell.setCellValue((String)mediabean.get("subsetname"));
    			HSSFComment com = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) 2 , (h+4) , ( short ) 3 , (h+6) ));
        		com.setString( new HSSFRichTextString((String)mediabean.get("id")));
        		cell.setCellComment(com);
//    			序号
				cell=row.createCell((short)0);
				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    			cell.setCellValue(t);
    			for(int y=3;y<k;y++){
        			cell=row.createCell((short)(y));
    				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        			cell.setCellValue("无");
        		}
    			t++;
    			h++;
    		}
    		//excel上生成下拉筐
    		short m=0;
    		//row = sheet.createRow(m+0);
    		row = sheet.getRow(m+0);
			if(row==null) {
                row = sheet.createRow(m+0);
            }
    		cell=row.createCell((short)26);
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellValue(ResourceFactory.getProperty("write.label"));//写
    		m++;
    		//row = sheet.createRow(m+0);
    		row = sheet.getRow(m+0);
			if(row==null) {
                row = sheet.createRow(m+0);
            }
    		cell=row.createCell((short)26);
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellValue(ResourceFactory.getProperty("read.label"));   //读
    		m++;
    		//row = sheet.createRow(m+0);
    		row = sheet.getRow(m+0);
			if(row==null) {
                row = sheet.createRow(m+0);
            }
    		cell=row.createCell((short)26);
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellValue(ResourceFactory.getProperty("null.label"));     //无
    		m++;
    		String strFormula = "$AA$1:$AA$3";  //表示AA列1-2行作为下拉列表来源数据 
    		/*HSSFDataValidation data_validation = new HSSFDataValidation((short)  
    				(5),(short)3,(short)(h+3),(short)list);
    		data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST); 
    		  data_validation.setFirstFormula(strFormula);  
              data_validation.setSecondFormula(null);  
              data_validation.setExplicitListFormula(true);  
              data_validation.setSurppressDropDownArrow(false);  
              data_validation.setEmptyCellAllowed(false);  
              data_validation.setShowPromptBox(false);*/
    		CellRangeAddressList addressList = new CellRangeAddressList((short)  
    				(5), (short)(h+3), (short)3, (short)list);
    		DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
    		HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
    		dataValidation.setSuppressDropDownArrow(false);		
    		sheet.addValidationData(dataValidation);
    		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/*
	 *管理范围查询 
	 */
	private HashMap map =null;
	private HashMap codesetmap;
	private ArrayList itemlist;
	private ArrayList parentlist;
	private ArrayList lll;
	private HashMap mm;
	ArrayList listvalue = new ArrayList();
	ArrayList list =new ArrayList();
	public ArrayList uuu(){
		try{
		/***组织机构表*/
		map = new HashMap();
		RowSet rs = null;
		itemlist=new ArrayList();
		parentlist = new ArrayList();
		codesetmap=new HashMap();
		lll = new ArrayList();
		mm=new HashMap();
		String sql = " select * from organization order by codesetid,codeitemid";
		ContentDAO das = new ContentDAO(this.conn);
		rs = das.search(sql);
	    while(rs.next())
	    {
	    	LazyDynaBean bean = new LazyDynaBean();
	    	bean.set("codesetid",rs.getString("codesetid"));
	    	bean.set("codeitemid",rs.getString("codeitemid"));
	    	bean.set("parentid",rs.getString("parentid"));
	    	bean.set("codeitemdesc",rs.getString("codeitemdesc"));
	    	if(codesetmap.get(rs.getString("parentid").toUpperCase())==null)
	    	{
	    		ArrayList lllll=new ArrayList();
		    	lllll.add(bean);
		    	codesetmap.put(rs.getString("parentid").toUpperCase(), lllll);
	    	}
	    	else
	    	{
	    		ArrayList lllll=(ArrayList)codesetmap.get(rs.getString("parentid").toUpperCase());
		    	lllll.add(bean);
		    	codesetmap.put(rs.getString("parentid").toUpperCase(), lllll);
	    	}
	    	itemlist.add(bean);
	    	if(rs.getString("parentid").equals(rs.getString("codeitemid")))
	    	{
	    		parentlist.add(bean);  //9个
	    	}
	    	else
	    	{
	    		lll.add(bean);
	    	}
	    }
	    mm=this.getLeafItemLinkMap(itemlist,"organization");
		for(int j=0;j<parentlist.size();j++)
		{
			LazyDynaBean pbean=(LazyDynaBean)parentlist.get(j);
			String codesetid=(String)pbean.get("codesetid");
			String codeitemid=(String)pbean.get("codeitemid");
			String codeitemdesc=(String)pbean.get("codeitemdesc");
			String parentid=(String)pbean.get("parentid");
			
			if(this.map.get((codesetid+codeitemid).toUpperCase())==null)
			{ 
				pbean.set("codeitemdesc", codeitemdesc);
				pbean.set("codeitem_id", codeitemid);
				pbean.set("codeset_id", codesetid);
				pbean.set("lang","1");
				listvalue.add(pbean);
	        	map.put((codesetid+codeitemid).toUpperCase(), "1");
			}
	    	this.ffff(codesetid, codeitemid, parentid, listvalue,1,conn,das);
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return listvalue;
	}
	
	public  HashMap getLeafItemLinkMap(ArrayList list,String tablename)
	{
		HashMap map=new HashMap();
		try
		{
			LazyDynaBean abean=null;
			for(int i=0;i<list.size();i++)
			{
				abean=(LazyDynaBean)list.get(i);
				String item_id=(String)abean.get("codeitemid");
				String parent_id=(String)abean.get("parentid");
				String codesetid=(String)abean.get("codesetid");
				ArrayList linkList=new ArrayList();
				getParentItem(linkList,abean,tablename);
				map.put((codesetid+item_id).toUpperCase(),linkList);  //@k+自己代号 9
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public void getParentItem(ArrayList list,LazyDynaBean abean,String tablename)
	{
		String item_id=(String)abean.get("codeitemid");
		String parent_id=(String)abean.get("parentid");
		String codeset_id=(String)abean.get("codesetid");
		LazyDynaBean a_bean=null;
		String sstr="";
		if("organization".equalsIgnoreCase(tablename)) {
            sstr=item_id;
        } else {
            sstr=codeset_id;
        }
		ArrayList llll=(ArrayList)codesetmap.get(sstr.toUpperCase());
		if(llll==null||llll.size()==0) {
            return;
        }
		/***原先循环的事itemlist*/
		for(int i=0;i<llll.size();i++)
		{
			a_bean=(LazyDynaBean)llll.get(i);
			String itemid=(String)a_bean.get("codeitemid");
			String parentid=(String)a_bean.get("parentid");
			String codesetid=(String)a_bean.get("codesetid");
			if("organization".equalsIgnoreCase(tablename))
			{
				if(item_id.equals(parentid)&&!itemid.equals(parentid))
	    		{
	    			list.add(a_bean);
	    			getParentItem(list,a_bean,tablename);
	    		}
			}
			else
			{
	    		if(item_id.equals(parentid)&&!itemid.equals(parentid)&&codeset_id.equalsIgnoreCase(codesetid))
	    		{
	    			list.add(a_bean);
	    			getParentItem(list,a_bean,tablename);
	    		}
			}
		}				
	}
	private int gong=0;
	public void ffff(String codesetid,String codeitemid,String parentid,ArrayList listvalue,int lay,Connection conn,ContentDAO das)
	{
		try
		{
			ArrayList list = (ArrayList)mm.get((codesetid+codeitemid).toUpperCase());
			if(list==null||list.size()==0) {
                return;
            }
			lay++;
			if(gong<lay) {
                gong=lay;
            }
			for(int i=0;i<list.size();i++)
			{
				
				LazyDynaBean pbean=(LazyDynaBean)list.get(i);
				String codeset_id=(String)pbean.get("codesetid");
				String codeitem_id=(String)pbean.get("codeitemid");
				String codeitemdesc=(String)pbean.get("codeitemdesc");
				if(this.map.get((codeset_id+codeitem_id).toUpperCase())==null)
				{
					pbean.set("codeitem_id",codeitem_id);
					pbean.set("codeitemdesc",codeitemdesc);
					pbean.set("codeset_id",codeset_id);
					pbean.set("lang",lay+"");
					listvalue.add(pbean);
    	        	map.put((codeset_id+codeitem_id).toUpperCase(), "1");
				}
				if(mm.get((codeset_id+codeitem_id).toUpperCase())==null||((ArrayList)mm.get((codeset_id+codeitem_id).toUpperCase())).size()==0)
				{
					continue;
					
				}else{
	    			this.ffff(codeset_id, codeitem_id, parentid, listvalue, lay,conn,das);
				}
    	    	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//生成管理范围ecxel
	public void setmanagementexcel(ArrayList toplist,ArrayList listvalue,HSSFWorkbook workbook, HSSFRow row, HSSFCell cell){
		try{
			short h = 3;
			short k = (short)gong;
			short t = 1;
			short lt=0;//top
			short f=1;//序号
			CellRangeAddress region = null;
			HSSFSheet sheet = workbook.createSheet(ResourceFactory.getProperty("menu.manage")); //生成一张表; 管理范围
			 // 创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器. 
	          HSSFPatriarch patr = sheet.createDrawingPatriarch();
	          for(int i=0;i<listvalue.size();i++){
	        	  LazyDynaBean bean = (LazyDynaBean)listvalue.get(i);
	        	  String lang = (String)bean.get("lang");
	        	  int lece = Integer.parseInt(lang);
	        	  //row= sheet.createRow((short)h+0); //行
	        	  row = sheet.getRow(h+0);
	  			if(row==null) {
                    row = sheet.createRow(h+0);
                }
	        	  cell=row.createCell((short)lece);
	        	 // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
	        	  cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	    		  cell.setCellValue((String)bean.get("codeitemdesc"));
	    		//注释
  				HSSFComment comment = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) (lece-1) , (h+3) , ( short ) lece , (h+5) ));
  				comment.setString( new HSSFRichTextString((String)bean.get("codeset_id")+(String)bean.get("codeitem_id")));
          		cell.setCellComment(comment);
    			cell = row.createCell((short)0);
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    			cell.setCellValue(f);
    			f++;
    			h++;
	          }
//			 设置列宽,参数一，10列
	          row = sheet.getRow(0);
	  			if(row==null) {
                    row = sheet.createRow(0);
                }
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,1,(short)10);
			cell=row.createCell((short)(0));
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("menu.manage"));  //管理范围
    		cell.setCellStyle(this.setDateStyle(workbook));
    		//写入top
    		row = sheet.getRow(2);
  			if(row==null) {
                row = sheet.createRow(2);  //row位置
            }
    		cell=row.createCell((short)(0));  //cell位置
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    		cell.setCellValue(ResourceFactory.getProperty("recidx.label"));   //序号
    		cell.setCellStyle(this.settopStyle(workbook));
    		if(gong>0){
    			HSSFCellStyle cs=this.settopStyle(workbook);
    			if(gong > 1) {
    				region = new CellRangeAddress(2,2,(short)1,(short)gong);
    				sheet.addMergedRegion(region);
    				for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
    					row = sheet.getRow(p);
    					if(row==null) {
                            row = sheet.createRow(p);
                        }
    					for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
    						cell = row.createCell((short)o);
    						cell.setCellStyle(cs);
    					}
    				}
    			} else {
    				row = sheet.getRow(2);
    				if(row==null) {
                        row = sheet.createRow(2);
                    }
    				
    				cell = row.createCell((short)1);
    				cell.setCellStyle(cs);
    			}
    			
        		cell=row.createCell((short)(1));  //cell位置
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        		cell.setCellType(HSSFCell.CELL_TYPE_STRING);																			//系统管理/角色管理/导出权限模板/人员管理范围模板添加岗位名称		jingq  add  2014.5.19
        		cell.setCellValue(ResourceFactory.getProperty("tree.unroot.undesc")+"    "+ResourceFactory.getProperty("tree.umroot.umdesc")+"    "+ResourceFactory.getProperty("tree.kkroot.gwdesc"));   //单位
        		cell.setCellStyle(this.settopStyle(workbook));
        		cell.setCellStyle(this.settopStyle(workbook));
    		}else if(gong==0){
    			row = sheet.getRow(2);
	  			if(row==null) {
                    row = sheet.createRow(2);  //row位置
                }
    			cell=row.createCell((short)(1));  //cell位置
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        		cell.setCellType(HSSFCell.CELL_TYPE_STRING);			
        		cell.setCellValue(ResourceFactory.getProperty("tree.unroot.undesc")+"    "+ResourceFactory.getProperty("tree.umroot.umdesc")+"    "+ResourceFactory.getProperty("tree.kkroot.gwdesc"));   //单位
        		cell.setCellStyle(this.settopStyle(workbook));
        		cell.setCellStyle(this.settopStyle(workbook));
    		}
    		
    		for(int i=0;i<toplist.size();i++){        //黄色头
    			LazyDynaBean bean = (LazyDynaBean)toplist.get(i);
    			row = sheet.getRow(2);
	  			if(row==null) {
                    row= sheet.createRow(2);
                }
//    			String role_property =(String)bean.get("role_property");
//    			if(role_property.equalsIgnoreCase("1")||role_property.equalsIgnoreCase("5")||role_property.equalsIgnoreCase("6")||role_property.equalsIgnoreCase("7"))
//				{
////					break;
//					continue;
//				}
    			if(gong==0){
    				cell=row.createCell((short)(k+2));
        			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        			cell.setCellValue((String)bean.get("role"));
        			//注释
    				HSSFComment comm = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) k , 2 , ( short ) (k+1) , 3 ));
            		comm.setString( new HSSFRichTextString((String)bean.get("role_id")));
            		cell.setCellComment(comm);
        			cell.setCellStyle(this.settopStyle(workbook));
        			lt = row.getLastCellNum();
        			lt--; //兼容excel2007
        			sheet.setColumnWidth((short)lt,(short)3000);
        			k++;
    			}else if(gong>0){
    				cell=row.createCell((short)(k+1));
        			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        			cell.setCellValue((String)bean.get("role"));
        			//注释
    				HSSFComment comm = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) k , 2 , ( short ) (k+1) , 3 ));
            		comm.setString( new HSSFRichTextString((String)bean.get("role_id")));
            		cell.setCellComment(comm);
        			cell.setCellStyle(this.settopStyle(workbook));
        			lt = row.getLastCellNum();
        			lt--; //兼容excel2007
        			sheet.setColumnWidth((short)lt,(short)3000);
        			k++;
    			}
    			
    		}
    		//下拉框初始值
    		for(int y=3;y<(h);y++){
    			//row= sheet.createRow(y);
    			row = sheet.getRow(y);
	  			if(row==null) {
                    row = sheet.createRow(y);
                }
    			if(gong==0){
    				for(int g=(gong+2);g<(k+2);g++){
        				cell=row.createCell((short)g);
            			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            			cell.setCellValue("无");
        			}
    			}else if(gong>0){
    				for(int g=(gong+1);g<(k+1);g++){
        				cell=row.createCell((short)g);
            			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            			cell.setCellValue("无");
        			}
    			}
    		}
    		//excel上生成下拉筐
//    		short m=0;
//    		row = sheet.createRow(m+0);
//    		cell=row.createCell((short)26);
//    		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.have"));   //有
//    		m++;
//    		row = sheet.createRow(m+0);
//    		cell=row.createCell((short)26);
//    		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.nothing"));  //无
//    		m++;
//    		String strFormula = "$AA$1:$AA$2";  //表示AA列1-2行作为下拉列表来源数据 
//    		HSSFDataValidation data_validation = new HSSFDataValidation((short)  
//    				(3),(short)(gong+1),(short)(h-1),(short)lt);
//    		data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST); 
//    		  data_validation.setFirstFormula(strFormula);  
//              data_validation.setSecondFormula(null);  
//              data_validation.setExplicitListFormula(true);  
//              data_validation.setSurppressDropDownArrow(false);  
//              data_validation.setEmptyCellAllowed(false);  
//              data_validation.setShowPromptBox(false);
//
//    		 sheet.addValidationData(data_validation);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private ArrayList AllList =new ArrayList();
	private int lay=0;
	/*
	 * 查询xml
	 */
	public ArrayList searchFunctionXmlHtml(){
		ArrayList lists = new ArrayList();
		InputStream in=null;
		try{
			 in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/function.xml");
			Document doc = PubFunc.generateDom(in); //得到xml
			Element root = doc.getRootElement(); //得到root元素
			/**版本之间的差异控制，市场考滤*/
	        VersionControl ver_ctrl=new VersionControl();
			List list = root.getChildren("function");  //得到根部的孩子返回所有子节点的数组
			for (int i = 0; i < list.size(); i++){
				 Element node = (Element) list.get(i);
				 LazyDynaBean bean = new LazyDynaBean();
				 String func_id=node.getAttributeValue("id");
				 String func_name=node.getAttributeValue("name");
				 if(ver_ctrl.searchFunctionId(func_id)){
					 bean.set("id",func_id);
					 bean.set("name", func_name);
					 bean.set("level","1");
					 AllList.add(bean);
					 doMethod(node,1);
				 }
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(in);
		}
		return AllList;
	}
	public void doMethod(Element element,int level)
	{
		/**有几个指标固定的 **/
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);//判断
		String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
		String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
		HashMap filtrateMap=filtrateId(inputchinfor,approveflag);
		
		/**版本之间的差异控制，市场考滤*/
        VersionControl ver_ctrl=new VersionControl();
		List list= element.getChildren();
		level++;

			if(lay<level) {
                lay=level;
            }
		for(int i=0;i<list.size();i++)
		{
			 Element childElement=(Element)list.get(i);    //得到底下的孩子
			 LazyDynaBean bean=new LazyDynaBean();
			 String func_id=childElement.getAttributeValue("id");
			 String func_name=childElement.getAttributeValue("name");
			 if(!ver_ctrl.searchFunctionId(func_id)) {
                 continue;
             }
			 if(isFiltrate(func_id,filtrateMap)) {
                 continue;
             }
			 bean.set("id",func_id);
			 bean.set("name", func_name);
			 bean.set("level", level+"");
			 AllList.add(bean);
			 getParentLinkMap(childElement,func_id);
			 doMethod(childElement,level);
		}
	}
	public void getParentLinkMap(Element parentElement,String id)
	{
		HashMap map= new HashMap();
		if(!parentElement.getParentElement().isRootElement()&&parentElement.getParentElement()!=null)
		{
			if(map.get(id.toUpperCase())!=null)
			{
				ArrayList list=(ArrayList)map.get(id.toUpperCase());
				list.add(parentElement.getParentElement());
				map.put(id.toUpperCase(), list);
			}
			else
			{
				ArrayList list= new ArrayList();
				list.add(parentElement.getParentElement());
				map.put(id.toUpperCase(), list);
			}
			getParentLinkMap(parentElement.getParentElement(),id);
		}
	}
	  /**
     * 返回需要过滤的idMap
     * @param inputchinfor
     * @param approveflag
     * @return
     */
    private HashMap filtrateId(String inputchinfor,String approveflag)
    {
    	HashMap hashMap=new HashMap();
    	if("1".equals(inputchinfor)&& "1".equals(approveflag))
    	{
    		hashMap.put("01030115", "01030115");//整体报批
    		hashMap.put("03084", "03084");//整体批准
    		hashMap.put("03083", "03083");//整体驳回
    		hashMap.put("260633", "260633");//批准
    		hashMap.put("260634", "260634");//整体驳回
    	}else
    	{
    		hashMap.put("01030106", "01030106");//我的变动信息明细
    		hashMap.put("03085", "03085");//删除    		
    		hashMap.put("260635", "260635");//删除 
    	}
    	return hashMap;
    }
    /**
     * 过滤功能号 
     * @param id
     * @param map
     * @return
     */
    private boolean isFiltrate(String id,HashMap map)
    {
    	boolean isCorrect=false;
    	if(map!=null)
    	{
    		String filtrateid=(String)map.get(id);
    		if(filtrateid!=null&&filtrateid.length()>0)
    		{
    			isCorrect=true;
    		}	
    	}
    	return isCorrect;    
    }    
	/*
	 * 写入功能权限excel
	 */
	public void setxmlexcel(ArrayList AllList,HSSFWorkbook workbook, HSSFRow row, HSSFCell cell,ArrayList toplist){
		try{
			short h=3;

			short k=(short) lay;
			short f=1; //序号
			short lists=0;
			CellRangeAddress region = null;
			HSSFSheet sheet= workbook.createSheet(ResourceFactory.getProperty("kjg.title.functionshouquan")); //功能授权
			   // 创建HSSFPatriarch对象,HSSFPatriarch是所有注释的容器. 
	          HSSFPatriarch patr = sheet.createDrawingPatriarch();	
			for(int n=0;n<AllList.size();n++){
					LazyDynaBean bean = (LazyDynaBean)AllList.get(n);
					String level = (String)bean.get("level");
					int lece = Integer.parseInt(level);
						//row= sheet.createRow((short)h+0); //行
					row = sheet.getRow(h+0);
		  			if(row==null) {
                        row = sheet.createRow(h+0);
                    }
						cell=row.createCell((short)lece);
						//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		    			cell.setCellValue((String)bean.get("name"));
		    			//注释
        				HSSFComment comment = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) (lece+1) , (h+2) , ( short ) (lece+2) , (h+4) ));
        				comment.setString( new HSSFRichTextString((String)bean.get("id")));
                		cell.setCellComment(comment);

		    			cell = row.createCell((short)0);
	        			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
	        			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	        			cell.setCellValue(f);
	        			f++;
		    			h++;
				}
			row = sheet.getRow(0);
			if(row==null) {
                row = sheet.createRow(0); // 定义是那一页的row
            }
//			 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,1,(short)10);
			cell=row.createCell((short)(0));  //写入的单元各位置;
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.functionshouquan"));  //功能授权
    		cell.setCellStyle(this.setDateStyle(workbook));
    		//写入top
    		row = sheet.getRow(2);
			if(row==null) {
                row= sheet.createRow(2);
            }
    		cell=row.createCell((short)(0));  //写入的单元各位置;
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("kh.field.seq")); //序号
    		cell.setCellStyle(this.settopStyle(workbook));
    		region = new CellRangeAddress(2,2,(short)1,(short)(lay-1));
    		sheet.addMergedRegion(region);
    		HSSFCellStyle cs=this.settopStyle(workbook);
			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
    			//row = sheet.createRow(p);
				row = sheet.getRow(p);
	  			if(row==null) {
                    row = sheet.createRow(p);
                }
	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
	            	cell = row.createCell((short)o);
	                cell.setCellStyle(cs);
	            }
			}
    		cell=row.createCell((short)(1));  //写入的单元各位置;
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.module"));  //模 块
    		cell.setCellStyle(this.settopStyle(workbook));
    		for(int t=0;t<toplist.size();t++){
    			LazyDynaBean bean = (LazyDynaBean)toplist.get(t);
    			row = sheet.getRow(2);
    			if(row==null) {
                    row= sheet.createRow(2);
                }
    			cell=row.createCell((short)(k));
    			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        		cell.setCellValue((String)bean.get("role"));
        		//注释
				HSSFComment comm = patr.createComment( new HSSFClientAnchor( 0 , 0 , 0 , 0 , ( short ) k , 2 , ( short ) (k+1) , 3 ));
        		comm.setString( new HSSFRichTextString((String)bean.get("role_id")));
        		cell.setCellComment(comm);
        		cell.setCellStyle(this.settopStyle(workbook));
        		lists=row.getLastCellNum();                       //获得某一行的列数；
        		lists--; //兼容excel2007
        		sheet.setColumnWidth((short)lists,(short)3000);
        		k++;
    		}
    		for(int y=3;y<h;y++){
    			//row= sheet.createRow(y);
    			row = sheet.getRow(y);
	  			if(row==null) {
                    row = sheet.createRow(y);
                }
    			for(int u=lay;u<k;u++){
    				cell=row.createCell((short)(u));
        			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            		cell.setCellValue("无");
    			}
    		}
    		//excel上生成下拉筐
//    		short m=0;
//    		row = sheet.createRow(m+0);
//    		cell=row.createCell((short)26);
//    		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.have"));   //有
//    		m++;
//    		row = sheet.createRow(m+0);
//    		cell=row.createCell((short)26);
//    		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//    		cell.setCellValue(ResourceFactory.getProperty("kjg.title.nothing"));  //无
//    		m++;
//    		String strFormula = "$AA$1:$AA$2";  //表示AA列1-2行作为下拉列表来源数据 
//    		HSSFDataValidation data_validation = new HSSFDataValidation((short)  
//    				(3),(short)lay,(short)(h-1),(short)lists);
//    		
//    		data_validation.setDataValidationType(HSSFDataValidation.DATA_TYPE_LIST); 
//  		  	data_validation.setFirstFormula(strFormula);  
//            data_validation.setSecondFormula(null);  
//            data_validation.setExplicitListFormula(true);  
//            data_validation.setSurppressDropDownArrow(false);  
//            data_validation.setEmptyCellAllowed(false);  
//            data_validation.setShowPromptBox(false);
////            sheet.a
//  		 sheet.addValidationData(data_validation);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/*
	 * 格式字体样式
	 */
	public HSSFCellStyle setDateStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 20); // 字体大小
        font.setBold(true);//粗体字
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
        return style;

	}
	//top
	public HSSFCellStyle settopStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12); // 字体大小
        font.setBold(true);//粗体字
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();//创建单元各风格
        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(HSSFColor.YELLOW.index);//颜色黄色
		style.setWrapText(true);  //换行
        style.setFont(font); // 单元格字体
        style.setBorderBottom(BorderStyle.THIN); //下边
        style.setBorderLeft(BorderStyle.THIN); //左边
        style.setBorderRight(BorderStyle.THIN); //右边
        style.setBorderTop(BorderStyle.THIN); //上边

        return style;

	}
	//身体
	public HSSFCellStyle setbodyStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 10); // 字体大小
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();//创建单元各风格
        style.setAlignment(HorizontalAlignment.LEFT); // 居中对齐方式 左右
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
        style.setWrapText(true);  //换行
        style.setFont(font); // 单元格字体
        style.setBorderBottom(BorderStyle.THIN); //下边
        style.setBorderLeft(BorderStyle.THIN); //左边
        style.setBorderRight(BorderStyle.THIN); //右边
        style.setBorderTop(BorderStyle.THIN); //上边
        return style;
	}
	//A/K/B样式
	public HSSFCellStyle setsubStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12); // 字体大小
        font.setBold(true);//粗体字
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();//创建单元各风格
        style.setAlignment(HorizontalAlignment.LEFT); // 居中对齐方式 左右
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
       
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);//浅绿色颜色
        style.setFont(font); // 单元格字体
        style.setBorderBottom(BorderStyle.THIN); //下边
        style.setBorderLeft(BorderStyle.THIN); //左边
        style.setBorderRight(BorderStyle.THIN); //右边
        style.setBorderTop(BorderStyle.THIN); //上边

        return style;

	}
}