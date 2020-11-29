package com.hjsj.hrms.businessobject.kq.register.history;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExecuteKqSumExcel {
	 private Connection conn;
	    private ReportParseVo parsevo;    
	    private UserView userView;
	    private String sorItem;
	    public String getSorItem() {
			return sorItem;
		}
		public void setSorItem(String sorItem) {
			this.sorItem = sorItem;
		}
		public ExecuteKqSumExcel()
		{
		}
		public ExecuteKqSumExcel(Connection conn)
		{
			this.conn=conn;
		}

		/**
		 * @param 数据库前缀 userbase
		 * @param 级别 code
		 * @param kind 1，部门，2单位 kind
		 * @param 考勤期间 coursedate
		 * @param 该考勤打印参数集 parsevo
		 * @param 用户参数集 userView
		 * */
		public void executeExcel(String code,String kind,String coursedate, ReportParseVo parsevo,UserView userView,HashMap formHM,HSSFWorkbook workbook,HSSFSheet sheet,HSSFRow row,HSSFCell cell)throws GeneralException
		{
			short h=0; //行
			this.parsevo=parsevo;
			this.userView=userView;
			int leng=2;
		    int itemumn=0; //表头列数
			 ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
			    ArrayList columnlist = new ArrayList(); //表头[q0321]
			    ArrayList itemlist =new ArrayList();  //表头[对应中文]
			    for(int i=0;i<fielditemlist.size();i++)
			    {
			         FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			     	 if("N".equals(fielditem.getItemtype())&& "1".equals(fielditem.getState()) && !"i9999".equalsIgnoreCase(fielditem.getItemid()))
			     	 { 	 
			     		columnlist.add(fielditem.getItemid());
			     		itemlist.add(fielditem.getItemdesc());
			     		itemumn=itemumn+1;
			     		int field_length=fielditem.getItemdesc().length();
			     		if(fielditem.getItemdesc().indexOf("(")!=-1)
			     		{
			     			field_length=field_length-2;
			     		}
			     		
			     		if(leng<field_length) {
                            leng=field_length;
                        }
			     	 }				
			    }
			    HashMap hashmap=new HashMap();
			    hashmap.put("itemumn",itemumn+"");
			    hashmap.put("itemleng",leng+"");
			KqViewSumBo kqViewSumBo = new KqViewSumBo(this.conn,this.parsevo,this.userView);
			ArrayList kq_dbase_list=this.userView.getPrivDbList();
			 // 过滤参数
		    KqParameter kq_paramter = new KqParameter(formHM,userView,"",this.conn);	              
			String kqBase=kq_paramter.getNbase();
			ArrayList list = new ArrayList();
			for (int i = 0; i < kq_dbase_list.size(); i++) {
				String str = kq_dbase_list.get(i).toString();
				String vStr = "," + kqBase + ",";
				if (vStr.contains("," + str + "")) {
					list.add(str);
				}
			}
			kq_dbase_list = list;
			
			int recordNum=kqViewSumBo.getAllRecordNum(code,kind,coursedate,kq_dbase_list);//总纪录数
			ArrayList keylist=new ArrayList();
		    keylist.add("q03z0");
		    keylist.add("a0100");
		    keylist.add("nbase");
		    kqViewSumBo.setSortItem(this.sorItem);
			ArrayList a0100list=kqViewSumBo.getA0100List(code,kind,coursedate,1,recordNum,kq_dbase_list,keylist); //[A0100,A0101]
			String [] codeitem=kqViewSumBo.getCodeItemDesc(code);//部门信息
			h = getTableTitle(h,workbook,sheet,row,cell,itemumn); //建立标题信息
			h = getTableHead(h,workbook,sheet,row,cell,codeitem,coursedate,itemumn);//建立表头信息
			h = getTableBodyHead(h,workbook,sheet,row,cell,itemlist,hashmap); //表格里的头信息
			h = getTableBody(code,kind,coursedate,a0100list,hashmap,h,workbook,sheet,row,cell); //内容
			getTileHtml(code,kind,recordNum,h,workbook,sheet,row,cell,itemumn); //尾
		}
		
		/*
		 *  建立标题信息
		 */
		public short getTableTitle(short h,HSSFWorkbook workbook,HSSFSheet sheet,HSSFRow row,HSSFCell cell,int itemumn ){
			short n = h;
			row=sheet.createRow(h); //创建Excel中一行 sheet.getRow(i)的区别  --读取Excel中一行
			int lie =itemumn/2;
			cell=row.createCell((short)lie);  //创建一列
			String context="";
			if(parsevo.getTitle_fw()!=null&&parsevo.getTitle_fw().length()>0)
			{
				context=parsevo.getTitle_fw();
			}else{
				context=parsevo.getName();
			}
//			cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
			cell.setCellValue(context);  //往单元格写入信息
			cell.setCellStyle(this.setDateStyle(workbook));
			n++;
			return n;
			
		}
		/*
		 * 建立表头信息
		 */
		public short getTableHead(short h,HSSFWorkbook workbook,HSSFSheet sheet,HSSFRow row,HSSFCell cell,String [] codeitem,String coursedate,int itemumn)throws GeneralException
		{
			short n = (short)(h+1);
			row=sheet.createRow(n); //创建Excel中一行 sheet.getRow(i)的区别  --读取Excel中一行
			
			int lie =itemumn/2;
			KqViewSumBo kqViewSumBo = new KqViewSumBo(this.conn,this.parsevo,this.userView);	
			ArrayList  datelist=kqViewSumBo.getDateList(this.conn,coursedate);
			/**单位**/           
	           String dv_content="";
	           if(codeitem[1]==null||codeitem[1].length()<=0)
	           {
	        	   dv_content="   单位：所有单位";
	           }else
	           {
	        	   dv_content="   单位："+codeitem[1];
	           }
	        cell=row.createCell((short)1); //一个单元格
//	        cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
			cell.setCellValue(dv_content);  //往单元格写入信息
			cell.setCellStyle(this.setTableHeadStyle(workbook));
			
			String bm_content="";
	           if(codeitem[0]==null||codeitem[0].length()<=0)
	           {
	        	   bm_content="   部门：全体部门";
	           }else
	           {
	        	   bm_content="   部门："+codeitem[0];
	           }
	           cell=row.createCell((short)lie); //穿件一个单元格
//	           cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
	           cell.setCellValue(bm_content);  //往单元格写入信息
			   cell.setCellStyle(this.setTableHeadStyle(workbook));
			   
			   //表头时间
			   CommonData vo = (CommonData)datelist.get(0);	           
		       String start_date=vo.getDataName();
		       vo = (CommonData)datelist.get(datelist.size()-1);	 
		       String end_date= vo.getDataName();
		       String table_date = coursedate+"      ("+start_date+"~"+end_date+")";
		       int mn =itemumn-3;
		       cell=row.createCell((short)mn); //穿件一个单元格
//		       cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
	           cell.setCellValue(table_date);  //往单元格写入信息
			   cell.setCellStyle(this.setTableHeadStyle(workbook));
			   n++;
			   
			return n;
		}
		/*
		 * 表格里的头信息
		 */
		public short getTableBodyHead(short h,HSSFWorkbook workbook,HSSFSheet sheet,HSSFRow row,HSSFCell cell,ArrayList columnlist,HashMap hashmap)
		{
			short n = h;
			try{
				short lie = 0;
				row = sheet.createRow(n); //创建Excel中一行 sheet.getRow(i)的区别  --读取Excel中一行
				cell=row.createCell((short)lie); //穿件一个单元格
//				cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
		        cell.setCellValue("姓 名");  //往单元格写入信息
				cell.setCellStyle(this.setTableTouStyle(workbook));
				lie++;
				for(int i=0;i<columnlist.size();i++){
					String value = columnlist.get(i).toString();				
					cell=row.createCell((short)lie); //穿件一个单元格
//					cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
			        cell.setCellValue(value);  //往单元格写入信息
					cell.setCellStyle(this.setTableTouStyle(workbook));
					lie++;
				}
				cell=row.createCell((short)lie); //穿件一个单元格
//				cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
		        cell.setCellValue("备 注");  //往单元格写入信息
				cell.setCellStyle(this.setTableTouStyle(workbook));
				n++;
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return n;
		}
		/*
		 * 内容
		 */
		public short getTableBody(String code,String kind,String coursedate,ArrayList a0100list,HashMap hashmap,short h,HSSFWorkbook workbook,HSSFSheet sheet,HSSFRow row,HSSFCell cell)
		{
			short n = h;
			try{
				for(int i=1;i<=a0100list.size();i++)
		    	{
//					if(i>50)
////						break;
//					{
					String a0100[]=(String[])a0100list.get(i-1);
					n=getOneA0100Data(code,kind,a0100,coursedate,hashmap,n,workbook,sheet,row,cell);
//					}
		    	}	
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return n;
		}
		 /**
	     * 通过一个员工编号得到该员工考勤期间的数据
	     * */
		public short  getOneA0100Data(String code,String kind,String a0100[],String coursedate,HashMap hashmap,short n,HSSFWorkbook workbook,HSSFSheet sheet,HSSFRow row,HSSFCell cell){
			RowSet rowSet=null;
			try{
				ArrayList fielditemlist = DataDictionary.getFieldList("q03",
						Constant.USED_FIELD_SET);
		    	StringBuffer column= new StringBuffer();    	
		    	ArrayList columnlist = new ArrayList();
		    	for(int i=0;i<fielditemlist.size();i++)
		    	{
		   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		   	     if("N".equals(fielditem.getItemtype())&& "1".equals(fielditem.getState()))
		   	     {
		   	    	if(!"i9999".equals(fielditem.getItemid()))
		   	    	{
		   	    		column.append(""+fielditem.getItemid()+",");  
		   	    		String filed[] =new String[2];
		   	    		filed[0]=fielditem.getItemid();
		   	    		filed[1]=fielditem.getDecimalwidth()+"";
		   	    		columnlist.add(filed);
		   	    	}
		   		  }				
		   	    }
		    	String sql_one_a0100=KqReportInit.selcet_Q05_one_emp(a0100[0],coursedate,code,kind,column.toString());
		    	ContentDAO dao = new ContentDAO(this.conn); 
		     	
		    	row = sheet.createRow(n); //创建Excel中一行 sheet.getRow(i)的区别  --读取Excel中一行
		    	n++;
		    	short lie = 0;
		    	cell=row.createCell((short)lie); //穿件一个单元格
//				cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
		        cell.setCellValue(a0100[1]);  //往单元格写入信息
				cell.setCellStyle(this.setTableBodyStyle(workbook));
				lie++;
				
		        rowSet = dao.search(sql_one_a0100);
		        if(rowSet.next())
		        {
		        	for(int i=0;i<columnlist.size();i++ )
		        	{
		        		  String fileds[]=(String[])columnlist.get(i);
	        		      String itemid=fileds[0].toString();
	             		  String one_filed=rowSet.getString(itemid);
	             		  String decimalwidth=fileds[1].toString();
	             		  if(one_filed==null||one_filed.length()<=0) {
                              one_filed="0";
                          }
	             		  String dv=PubFunc.round(one_filed,Integer.parseInt(decimalwidth));
	             		  float fv=Float.parseFloat(dv);
	             		  if(fv>0)
	             		  {
	             			cell=row.createCell((short)lie); //穿件一个单元格
//	         				cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
	         		        cell.setCellValue(dv);  //往单元格写入信息
	         				cell.setCellStyle(this.setTableBodyStyle(workbook));
	         				lie++;
	             		  }
	             		  else{
	             			 cell=row.createCell((short)lie); //穿件一个单元格
//		         			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
		         		     cell.setCellValue(" ");  //往单元格写入信息
		         			 cell.setCellStyle(this.setTableBodyStyle(workbook));
		         			 lie++;
	             		  }
		        	}
		             cell=row.createCell((short)lie); //穿件一个单元格
//        			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
        		     cell.setCellValue(" ");  //往单元格写入信息
        			 cell.setCellStyle(this.setTableBodyStyle(workbook));
//        			 lie++;
		        }
			}
			catch(Exception e){
				e.printStackTrace();
			}finally
		    {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
		    } 
			return n;
		}
		public void getTileHtml(String code,String kind,int recordNum,short h,HSSFWorkbook workbook,HSSFSheet sheet,HSSFRow row,HSSFCell cell,int itemumn)
		{
			try{
				CellRangeAddress region=null;
				itemumn = itemumn+2;
				
				int pin = itemumn/4;
				row = sheet.createRow(h); //创建Excel中一行 sheet.getRow(i)的区别  --读取Excel中一行

				region= new CellRangeAddress (h,h,0,pin);
				sheet.addMergedRegion(region);
				HSSFCellStyle css=this.setTableBodyStyle(workbook);
    			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
        			row = sheet.createRow(p);
    	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
    	            	cell = row.createCell((short)o);
    	                cell.setCellStyle(css);
    	            }
    			}
    			 
    			cell=row.createCell((short)0); //穿件一个单元格
//    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
    			cell.setCellValue("部门当月参加考勤人数:"+recordNum);  //往单元格写入信息
    			cell.setCellStyle(this.setTableBodyStyle(workbook));
    			region= new CellRangeAddress (h,h,(short)(pin+1),(short)(pin+pin));
 				sheet.addMergedRegion(region);
 				this.setTableBodyStyle(workbook);
     			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
         			row = sheet.createRow(p);
     	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
     	            	cell = row.createCell((short)o);
     	                cell.setCellStyle(css);
     	            }
     			}
    			 
     			cell=row.createCell((short)(pin+1)); //穿件一个单元格
//    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
     			cell.setCellValue(" 部门领导签字:");  //往单元格写入信息
     			cell.setCellStyle(this.setTableBodyStyle(workbook));
    			region= new CellRangeAddress (h,h,(short)(pin+pin+1),(short)(pin+pin+pin));
  				sheet.addMergedRegion(region);
  				this.setTableBodyStyle(workbook);
      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
          			row = sheet.createRow(p);
      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
      	            	cell = row.createCell((short)o);
      	                cell.setCellStyle(css);
      	            }
      			}
    			 cell=row.createCell((short)(pin+pin+1)); //穿件一个单元格
//    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
    		     cell.setCellValue("  审核:");  //往单元格写入信息
    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
    			
    			 
    			region= new CellRangeAddress (h,h,(short)(pin+pin+pin+1),(short)(itemumn-1));
   				sheet.addMergedRegion(region);
   				this.setTableBodyStyle(workbook);
       			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			row = sheet.createRow(p);
       	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
       	            	cell = row.createCell((short)o);
       	                cell.setCellStyle(css);
       	            }
       			}
    			 cell=row.createCell((short)(pin+pin+pin+1)); //穿件一个单元格
//    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
    		     cell.setCellValue(" 填报:");  //往单元格写入信息
    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
    			 h++;
    			 
    			 //页尾标题
    			 if(parsevo.getTile_fw()!=null&&parsevo.getTile_fw().length()>0)
    		    	{
    		    		String tile_fw=parsevo.getTile_fw();
    		    		row = sheet.createRow(h);
    		    		region= new CellRangeAddress (h,h,(short)0,(short)(itemumn-1));
    		    		sheet.addMergedRegion(region);
    	   				this.setTableBodyStyle(workbook);
    	       			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
    	           			row = sheet.createRow(p);
    	       	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
    	       	            	cell = row.createCell((short)o);
    	       	                cell.setCellStyle(css);
    	       	            }
    	       			}
    	       			cell=row.createCell((short)0); //穿件一个单元格
//    	    			cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
    	    		    cell.setCellValue(" "+tile_fw);  //往单元格写入信息
    	    			cell.setCellStyle(this.setTableBodyStyle(workbook));
    	    			h++;
    		    	}
    			 /***表尾基本参数**/
    				int par_column=getNumColumn_2(parsevo.getTile_c(),parsevo.getTile_p(),parsevo.getTile_e(),parsevo.getTile_u(),parsevo.getTile_d(),parsevo.getTile_t());
    				int pen = 0;  //列
    				int wei = itemumn/par_column;
    				int s = 0;
    				int i =1;
    				row = sheet.createRow(h);
    				if(par_column>0)
    				{
    					/**制作人单位**/
    			           if("#u".equals(parsevo.getTile_u().trim()))
    			           {
    			        	   String u_code="";
    			        	   if(!userView.isSuper_admin())
    			    		   {
    			    			  if(userView.getUserOrgId()!=null && userView.getUserOrgId().trim().length()>0)
    			    			  {
    			    				 u_code=userView.getUserOrgId();
    			    			  }else
    			    			  {
    			    				 u_code=RegisterInitInfoData.getKqPrivCodeValue(userView);
    			    			  }
    			    		   }
    			        	KqViewDailyBo kqViewDailyBo = new KqViewDailyBo(this.conn,this.parsevo,this.userView);
   				        	String [] u_codeitem=kqViewDailyBo.getCodeItemDesc(u_code);
   				        	String u_str="制作人单位"+u_codeitem[0];
    			        	   if(i==par_column){
    			        		   region= new CellRangeAddress (h,h,(short)pen,(short)(itemumn-1));
       			  				sheet.addMergedRegion(region);
       			  				this.setTableBodyStyle(workbook);
       			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
       			          			row = sheet.createRow(p);
       			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
       			      	            	cell = row.createCell((short)o);
       			      	                cell.setCellStyle(css);
       			      	            }
       			      			}
       			      			
       			    			 cell=row.createCell((short)0); //穿件一个单元格
//       			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
       			    		     cell.setCellValue(u_str);  //往单元格写入信息
       			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
    			        	   }else
    			        	   {
    			        		region= new CellRangeAddress (h,h,(short)pen,(short)wei);
       			  				sheet.addMergedRegion(region);
       			  				this.setTableBodyStyle(workbook);
       			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
       			          			row = sheet.createRow(p);
       			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
       			      	            	cell = row.createCell((short)o);
       			      	                cell.setCellStyle(css);
       			      	            }
       			      			}
       			      			
       			    			 cell=row.createCell((short)0); //穿件一个单元格
//       			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
       			    		     cell.setCellValue(u_str);  //往单元格写入信息
       			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
       			    			 pen=pen+wei;
       			    			 s=wei;
       			    			 i = i+1;
    			        	   }
    			           }
    			           /**制作日期**/
    			           if("#d".equals(parsevo.getTile_d().trim()))
    			           {
    			        	   String d_str="制作日期: "+PubFunc.getStringDate("yyyy.MM.dd");
    			        	   if(i==par_column){
    			        		   if(pen==0)
        			        	   {
        			        		region= new CellRangeAddress (h,h,(short)pen,(short)(itemumn-1));
           			  				sheet.addMergedRegion(region);
           			  				this.setTableBodyStyle(workbook);
           			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			          			row = sheet.createRow(p);
           			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
           			      	            	cell = row.createCell((short)o);
           			      	                cell.setCellStyle(css);
           			      	            }
           			      			}
           			      			
           			    			 cell=row.createCell((short)(0)); //穿件一个单元格
//           			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
           			    		     cell.setCellValue(d_str);  //往单元格写入信息
           			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
           			    			
        			        	   }else{
        			        		   region= new CellRangeAddress (h,h,(short)(pen+1),(short)(itemumn-1));
        			        		   sheet.addMergedRegion(region);
              			  				this.setTableBodyStyle(workbook);
              			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
              			          			row = sheet.createRow(p);
              			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
              			      	            	cell = row.createCell((short)o);
              			      	                cell.setCellStyle(css);
              			      	            }
              			      			}
              			      			
              			    			 cell=row.createCell((short)(pen+1)); //穿件一个单元格
//              			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
              			    		     cell.setCellValue(d_str);  //往单元格写入信息
              			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
              			    			 
        			        	   }
    			        	   }else
    			        	   {
    			        		   if(pen==0)
        			        	   {
        			        		region= new CellRangeAddress (h,h,(short)pen,(short)wei);
           			  				sheet.addMergedRegion(region);
           			  				this.setTableBodyStyle(workbook);
           			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			          			row = sheet.createRow(p);
           			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
           			      	            	cell = row.createCell((short)o);
           			      	                cell.setCellStyle(css);
           			      	            }
           			      			}
           			      			
           			    			 cell=row.createCell((short)(0)); //穿件一个单元格
//           			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
           			    		     cell.setCellValue(d_str);  //往单元格写入信息
           			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
           			    			 pen=pen+wei;
           			    			 s=wei;
           			    			i = i+1;
        			        	   }else{
        			        		   region= new CellRangeAddress (h,h,(short)(pen+1),(short)(pen+1+wei));
        			        		   sheet.addMergedRegion(region);
              			  				this.setTableBodyStyle(workbook);
              			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
              			          			row = sheet.createRow(p);
              			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
              			      	            	cell = row.createCell((short)o);
              			      	                cell.setCellStyle(css);
              			      	            }
              			      			}
              			      			
              			    			 cell=row.createCell((short)(pen+1)); //穿件一个单元格
//              			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
              			    		     cell.setCellValue(d_str);  //往单元格写入信息
              			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
              			    			 pen=pen+1+wei;
              			    			 s=wei;
              			    			 i = i+1;
        			        	   } 
    			        	   }
    			        	   
    			           }
    			           //制作人
    			           if("#e".equals(parsevo.getTile_e().trim()))
    			           {
    			        	   String e_str="制作人: "+this.userView.getUserFullName();
    			        	   if(i==par_column){
    			        		   if(pen==0)
        			        	   {
        			        		region= new CellRangeAddress (h,h,(short)pen,(short)(itemumn-1));
           			  				sheet.addMergedRegion(region);
           			  				this.setTableBodyStyle(workbook);
           			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			          			row = sheet.createRow(p);
           			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
           			      	            	cell = row.createCell((short)o);
           			      	                cell.setCellStyle(css);
           			      	            }
           			      			}
           			      			
           			    			 cell=row.createCell((short)(0)); //穿件一个单元格
//           			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
           			    		     cell.setCellValue(e_str);  //往单元格写入信息
           			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
           			    			 
        			        	   }else{
        			        		   region= new CellRangeAddress (h,h,(short)(pen+1),(short)(itemumn-1));
        			        		   sheet.addMergedRegion(region);
              			  				this.setTableBodyStyle(workbook);
              			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
              			          			row = sheet.createRow(p);
              			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
              			      	            	cell = row.createCell((short)o);
              			      	                cell.setCellStyle(css);
              			      	            }
              			      			}
              			      			
              			    			 cell=row.createCell((short)(pen+1)); //穿件一个单元格
//              			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
              			    		     cell.setCellValue(e_str);  //往单元格写入信息
              			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
              			    			 
        			        	   }
    			        	   }else
    			        	   {
    			        		   if(pen==0)
        			        	   {
        			        		region= new CellRangeAddress (h,h,(short)pen,(short)wei);
           			  				sheet.addMergedRegion(region);
           			  				this.setTableBodyStyle(workbook);
           			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			          			row = sheet.createRow(p);
           			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
           			      	            	cell = row.createCell((short)o);
           			      	                cell.setCellStyle(css);
           			      	            }
           			      			}
           			      			
           			    			 cell=row.createCell((short)(pen)); //穿件一个单元格
//           			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
           			    		     cell.setCellValue(e_str);  //往单元格写入信息
           			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
           			    			 pen=pen+wei;
           			    			 s=wei;
           			    			i = i+1;
        			        	   }else{
        			        		   region= new CellRangeAddress (h,h,(short)(pen+1),(short)(pen+1+wei));
        			        		   sheet.addMergedRegion(region);
              			  				this.setTableBodyStyle(workbook);
              			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
              			          			row = sheet.createRow(p);
              			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
              			      	            	cell = row.createCell((short)o);
              			      	                cell.setCellStyle(css);
              			      	            }
              			      			}
              			      			
              			    			 cell=row.createCell((short)(pen+1)); //穿件一个单元格
//              			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
              			    		     cell.setCellValue(e_str);  //往单元格写入信息
              			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
              			    			 pen=pen+1+wei;
              			    			 s=wei;
              			    			 i = i+1;
        			        	   }
    			        	   }
    			           }
    			           /**制作时间**/
    			           if("#t".equals(parsevo.getTile_t().trim()))
    			           {
    			        	   String t_str="时间: "+PubFunc.getStringDate("HH:mm:ss");
    			        	   if(i==par_column){
    			        		   if(pen==0)
        			        	   {
        			        		region= new CellRangeAddress (h,h,(short)pen,(short)(itemumn-1));
           			  				sheet.addMergedRegion(region);
           			  				this.setTableBodyStyle(workbook);
           			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			          			row = sheet.createRow(p);
           			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
           			      	            	cell = row.createCell((short)o);
           			      	                cell.setCellStyle(css);
           			      	            }
           			      			}
           			      			
           			    			 cell=row.createCell((short)(0)); //穿件一个单元格
//           			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
           			    		     cell.setCellValue(t_str);  //往单元格写入信息
           			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
           			    			 
        			        	   }else{
        			        		   region= new CellRangeAddress (h,h,(short)(pen+1),(short)(itemumn-1));
        			        		   sheet.addMergedRegion(region);
              			  				this.setTableBodyStyle(workbook);
              			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
              			          			row = sheet.createRow(p);
              			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
              			      	            	cell = row.createCell((short)o);
              			      	                cell.setCellStyle(css);
              			      	            }
              			      			}
              			      			
              			    			 cell=row.createCell((short)(pen+1)); //穿件一个单元格
//              			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
              			    		     cell.setCellValue(t_str);  //往单元格写入信息
              			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
              			    			 
        			        	   }
    			        	   }else
    			        	   {
    			        		   if(pen==0)
        			        	   {
        			        		region= new CellRangeAddress (h,h,(short)pen,(short)wei);
           			  				sheet.addMergedRegion(region);
           			  				this.setTableBodyStyle(workbook);
           			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			          			row = sheet.createRow(p);
           			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
           			      	            	cell = row.createCell((short)o);
           			      	                cell.setCellStyle(css);
           			      	            }
           			      			}
           			      			
           			    			 cell=row.createCell((short)(pen)); //穿件一个单元格
//           			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
           			    		     cell.setCellValue(t_str);  //往单元格写入信息
           			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
           			    			 pen=pen+wei;
           			    			 s=wei;
           			    			i = i+1;
        			        	   }else{
        			        		   region= new CellRangeAddress (h,h,(short)(pen+1),(short)(pen+1+wei));
        			        		   sheet.addMergedRegion(region);
              			  				this.setTableBodyStyle(workbook);
              			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
              			          			row = sheet.createRow(p);
              			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
              			      	            	cell = row.createCell((short)o);
              			      	                cell.setCellStyle(css);
              			      	            }
              			      			}
              			      			
              			    			 cell=row.createCell((short)(pen+1)); //穿件一个单元格
//              			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
              			    		     cell.setCellValue(t_str);  //往单元格写入信息
              			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
              			    			 pen=pen+1+wei;
              			    			 s=wei;
              			    			 i = i+1;
        			        	   }
    			        	   }
    			           }
    			           /**页码**/
    			           if("#p".equals(parsevo.getTile_p().trim()))
    			           {    	
    			        	   String p_str="页码: 第 1 页";
    			        	   if(i==par_column){
    			        		   if(pen==0)
        			        	   {
        			        		region= new CellRangeAddress (h,h,(short)pen,(short)(itemumn-1));
           			  				sheet.addMergedRegion(region);
           			  				this.setTableBodyStyle(workbook);
           			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			          			row = sheet.createRow(p);
           			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
           			      	            	cell = row.createCell((short)o);
           			      	                cell.setCellStyle(css);
           			      	            }
           			      			}
           			      			
           			    			 cell=row.createCell((short)(0)); //穿件一个单元格
//           			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
           			    		     cell.setCellValue(p_str);  //往单元格写入信息
           			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
           			    			 
        			        	   }else{
        			        		   region= new CellRangeAddress (h,h,(short)(pen+1),(short)(itemumn-1));
        			        		   sheet.addMergedRegion(region);
              			  				this.setTableBodyStyle(workbook);
              			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
              			          			row = sheet.createRow(p);
              			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
              			      	            	cell = row.createCell((short)o);
              			      	                cell.setCellStyle(css);
              			      	            }
              			      			}
              			      			
              			    			 cell=row.createCell((short)(pen+1)); //穿件一个单元格
//              			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
              			    		     cell.setCellValue(p_str);  //往单元格写入信息
              			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
              			    			 
        			        	   }
    			        	   }else
    			        	   {
    			        		   if(pen==0)
        			        	   {
        			        		region= new CellRangeAddress (h,h,(short)pen,(short)wei);
           			  				sheet.addMergedRegion(region);
           			  				this.setTableBodyStyle(workbook);
           			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			          			row = sheet.createRow(p);
           			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
           			      	            	cell = row.createCell((short)o);
           			      	                cell.setCellStyle(css);
           			      	            }
           			      			}
           			      			
           			    			 cell=row.createCell((short)(pen)); //穿件一个单元格
//           			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
           			    		     cell.setCellValue(p_str);  //往单元格写入信息
           			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
           			    			 pen=pen+wei;
           			    			 s=wei;
           			    			i = i+1;
        			        	   }else{
        			        		   region= new CellRangeAddress (h,h,(short)(pen+1),(short)(pen+1+wei));
        			        		   sheet.addMergedRegion(region);
              			  				this.setTableBodyStyle(workbook);
              			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
              			          			row = sheet.createRow(p);
              			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
              			      	            	cell = row.createCell((short)o);
              			      	                cell.setCellStyle(css);
              			      	            }
              			      			}
              			      			
              			    			 cell=row.createCell((short)(pen+1)); //穿件一个单元格
//              			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
              			    		     cell.setCellValue(p_str);  //往单元格写入信息
              			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
              			    			 pen=pen+1+wei;
              			    			 s=wei;
              			    			 i = i+1;
        			        	   }
    			        	   }
    			           }
    			           /**总页码**/
    			           if("#c".equals(parsevo.getTile_c().trim()))
    			           {
    			        	   String c_str="总页码: 共 1 页";
    			        	   if(i==par_column){
    			        		   if(pen==0)
        			        	   {
        			        		region= new CellRangeAddress (h,h,(short)pen,(short)(itemumn-1));
           			  				sheet.addMergedRegion(region);
           			  				this.setTableBodyStyle(workbook);
           			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			          			row = sheet.createRow(p);
           			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
           			      	            	cell = row.createCell((short)o);
           			      	                cell.setCellStyle(css);
           			      	            }
           			      			}
           			      			
           			    			 cell=row.createCell((short)(0)); //穿件一个单元格
//           			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
           			    		     cell.setCellValue(c_str);  //往单元格写入信息
           			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
           			    			 
        			        	   }else{
        			        		   region= new CellRangeAddress (h,h,(short)(pen+1),(short)(itemumn-1));
        			        		   sheet.addMergedRegion(region);
              			  				this.setTableBodyStyle(workbook);
              			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
              			          			row = sheet.createRow(p);
              			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
              			      	            	cell = row.createCell((short)o);
              			      	                cell.setCellStyle(css);
              			      	            }
              			      			}
              			      			
              			    			 cell=row.createCell((short)(pen+1)); //穿件一个单元格
//              			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
              			    		     cell.setCellValue(c_str);  //往单元格写入信息
              			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
              			    			 
        			        	   }
    			        	   }else
    			        	   {
    			        		   if(pen==0)
        			        	   {
        			        		region= new CellRangeAddress (h,h,(short)pen,(short)wei);
           			  				sheet.addMergedRegion(region);
           			  				this.setTableBodyStyle(workbook);
           			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
           			          			row = sheet.createRow(p);
           			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
           			      	            	cell = row.createCell((short)o);
           			      	                cell.setCellStyle(css);
           			      	            }
           			      			}
           			      			
           			    			 cell=row.createCell((short)(pen)); //穿件一个单元格
//           			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
           			    		     cell.setCellValue(c_str);  //往单元格写入信息
           			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
           			    			 pen=pen+wei;
           			    			 s=wei;
           			    			i = i+1;
        			        	   }else{
        			        		   region= new CellRangeAddress (h,h,(short)(pen+1),(short)(pen+1+wei));
        			        		   sheet.addMergedRegion(region);
              			  				this.setTableBodyStyle(workbook);
              			      			for (int p = region.getFirstRow(); p <= region.getLastRow(); p++){
              			          			row = sheet.createRow(p);
              			      	            for (int o = region.getFirstColumn(); o <= region.getLastColumn(); o++) {
              			      	            	cell = row.createCell((short)o);
              			      	                cell.setCellStyle(css);
              			      	            }
              			      			}
              			      			
              			    			 cell=row.createCell((short)(pen+1)); //穿件一个单元格
//              			    			 cell.setEncoding(HSSFCell.ENCODING_UTF_16); //关键代码，解决中文乱码
              			    		     cell.setCellValue(c_str);  //往单元格写入信息
              			    			 cell.setCellStyle(this.setTableBodyStyle(workbook));
              			    			 pen=pen+1+wei;
              			    			 s=wei;
              			    			 i = i+1;
        			        	   }
    			        	   }
    			           }
    			           
    				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		/**
		 * 返回表头||表尾的烈属
		 * @param c 总页数
		 * @param p 页码
		 * @param e 制作人
		 * @param u 制作人所在的单位
		 * @param d 日期
		 * @param t 时间
		 * @return 列数
		 * */
		public int getNumColumn_2(String c,String p,String e,String u,String d,String t)
		{
			int i=0;
			if("#c".equals(c.trim())) {
                i=i+1;
            }
			if("#p".equals(p.trim())) {
                i=i+1;
            }
			if("#e".equals(e.trim())) {
                i=i+1;
            }
			if("#u".equals(u.trim())) {
                i=i+1;
            }
			if("#d".equals(d.trim())) {
                i=i+1;
            }
			if("#t".equals(t.trim())) {
                i=i+1;
            }
		    return i;
		}
		public HSSFCellStyle setDateStyle(HSSFWorkbook workbook) 
		{
			 // 先定义一个字体对象
	        HSSFFont font = workbook.createFont();
	        font.setFontName("黑体");
	        font.setFontHeightInPoints((short) 20); // 字体大小
	        // 定义表头单元格格式
	        HSSFCellStyle style = workbook.createCellStyle();
	        style.setFont(font); // 单元格字体
	        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
	        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
	        return style;

		}
		public HSSFCellStyle setTableHeadStyle(HSSFWorkbook workbook)
		{
			// 先定义一个字体对象
	        HSSFFont font = workbook.createFont();
	        font.setFontName("黑体");
	        font.setFontHeightInPoints((short) 10); // 字体大小
	        // 定义表头单元格格式
	        HSSFCellStyle style = workbook.createCellStyle();
	        style.setFont(font); // 单元格字体
	        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
	        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
	        return style;
		}
		public HSSFCellStyle setTableTouStyle(HSSFWorkbook workbook)
		{
			// 先定义一个字体对象
	        HSSFFont font = workbook.createFont();
	        font.setFontName("黑体");
	        font.setFontHeightInPoints((short) 10); // 字体大小
	     // 定义表头单元格格式
	        HSSFCellStyle style = workbook.createCellStyle();  
	        style.setWrapText(true); //自动换行 
	        style.setFont(font); // 单元格字体
	        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
	        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
	        style.setBorderBottom(BorderStyle.THIN); //下边
	        style.setBorderLeft(BorderStyle.THIN); //左边
	        style.setBorderRight(BorderStyle.THIN); //右边
	        style.setBorderTop(BorderStyle.THIN); //上边
	        return style;
		}
		public HSSFCellStyle setTableBodyStyle(HSSFWorkbook workbook)
		{
			// 先定义一个字体对象
//	        HSSFFont font = workbook.createFont();
//	        font.setFontName("黑体");
//	        font.setFontHeightInPoints((short) 10); // 字体大小
	        // 定义表头单元格格式
	        HSSFCellStyle style = workbook.createCellStyle();  
	        style.setWrapText(true); //自动换行   

//	        style.setFont(font); // 单元格字体
	        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐方式 左右
	        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式 上下
	        style.setBorderBottom(BorderStyle.THIN); //下边
	        style.setBorderLeft(BorderStyle.THIN); //左边
	        style.setBorderRight(BorderStyle.THIN); //右边
	        style.setBorderTop(BorderStyle.THIN); //上边
	        return style;
		}
}
