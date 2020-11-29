package com.hjsj.hrms.businessobject.kq.register.history;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.sql.RowSet;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExecuteKqSumPdf {
	 private Connection conn;
	    private ReportParseVo parsevo;    
	    private UserView userView;
	    private final float rate=0.24f;
	    private String sjelement;  //制作时间 用户可以修改
	    private String timeqd;  //时间 用户可以修改
	    private String sortItem;
		public String getSortItem() {
			return sortItem;
		}
		public void setSortItem(String sortItem) {
			this.sortItem = sortItem;
		}
		public ExecuteKqSumPdf()
		{
		}
		public String getSjelement() {
			return sjelement;
		}
		public void setSjelement(String sjelement) {
			this.sjelement = sjelement;
		}
		public String getTimeqd() {
			return timeqd;
		}
		public void setTimeqd(String timeqd) {
			this.timeqd = timeqd;
		}
		public ExecuteKqSumPdf(Connection conn)
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
		public String executePdf(String code,String kind,String coursedate, ReportParseVo parsevo,UserView userView,HashMap formHM)throws GeneralException
		{
			this.parsevo=parsevo;
			this.userView=userView;		
			int leng=2;
		    int itemumn=0;
		    ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
		    ArrayList columnlist = new ArrayList();
		    ArrayList itemlist =new ArrayList();
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
		    leng=leng*Integer.parseInt(parsevo.getBody_fz());	    
		   
		    double spare_h=getSpareHieght(leng);
		    int pagesize=0;
		    if("#pr[1]".equals(parsevo.getBody_rn()))
		    {
		    	pagesize=Integer.parseInt(parsevo.getBody_rn());
		    }else if("0".equals(parsevo.getOrientation()))
		    {
		    	pagesize=(int)spare_h/(Integer.parseInt(parsevo.getBody_fz())*2+8);
		    }else
		    {
		    	pagesize=(int)spare_h/(Integer.parseInt(parsevo.getBody_fz())+23);
		    	//pagesize=1;
		    }
	        //System.out.println("line 93 ="+pagesize);
	        KqViewSumBo kqViewSumBo = new KqViewSumBo(this.conn,this.parsevo,this.userView);			
			ArrayList kq_dbase_list=this.userView.getPrivDbList(); 
			
			 // 过滤参数
		    KqParameter kq_paramter = new KqParameter(formHM,userView,"UN",this.conn);	              
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
			int sum_page=(recordNum-1)/pagesize+1;
			ArrayList keylist=new ArrayList();
		    keylist.add("q03z0");
		    keylist.add("a0100");
		    keylist.add("nbase");
		    kqViewSumBo.setSortItem(sortItem);
			ArrayList a0100list=kqViewSumBo.getA0100List(code,kind,coursedate,1,recordNum,kq_dbase_list,keylist);
			String [] codeitem=kqViewSumBo.getCodeItemDesc(code);//部门信息
			
			
			String url="";
			url = "kq_" + this.userView.getUserName() + ".pdf";
			String factWidth=getFactWidth();//实际宽度		
			String factHieght=getFactHeight();//实际高度
			float paperW=Float.parseFloat(parsevo.getWidth())/this.rate;
			float paperH=Float.parseFloat(parsevo.getHeight())/this.rate;
			//System.out.println("[line:98] paperW="+paperW);
			//System.out.println("[line:99] paperH="+paperH);
			Rectangle pageSize = new Rectangle(paperW,paperH);	
			float sige_T=Float.parseFloat(getPxFormMm_f(parsevo.getTop()));
			float sige_B=Float.parseFloat(getPxFormMm_f(parsevo.getBottom()));
			float sige_L=Float.parseFloat(getPxFormMm_f(parsevo.getLeft()));
			float sige_R=Float.parseFloat(getPxFormMm_f(parsevo.getRight()));
			Document document = null;
			FileOutputStream fileStream = null;
			
			if("1".equals(parsevo.getOrientation().trim()))
			{
				document=new Document(pageSize.rotate());			
				//document=new Document(PageSize.A4.rotate());
			}else{
				document=new Document(pageSize);
				//document=new Document(PageSize.A4);
			}
			PdfWriter writer = null;
			try
			{
//				writer= PdfWriter.getInstance(document,
//						new FileOutputStream(System.getProperty("java.io.tmpdir")+"\\"+url));
			    fileStream = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+url);
				writer= PdfWriter.getInstance(document, fileStream);
			}catch(Exception e)
			{
				e.printStackTrace();
			} 
			
			
			
			document.setMargins(sige_T, sige_B, sige_L, sige_R);
			int maxColumn=columnlist.size()+2;
			
			float[] widths = new float[maxColumn];;
			if(maxColumn>0)
			{
				double ave_width=1d/maxColumn;
				double ave_data_width=ave_width-0.005;
				double sp_sum_width=1-ave_data_width*columnlist.size();
				double ave_sp_width=sp_sum_width/2;
				//System.out.println("ave_sp_width========="+ave_sp_width);
				widths[0]=Float.parseFloat(ave_sp_width+"");
				int r=0;
				for(int i=1;i<columnlist.size()+1;i++)
				{
					widths[i]=Float.parseFloat(ave_data_width+"");
				    r=i;
 				}
				widths[r+1]=Float.parseFloat(ave_sp_width+"");
			}
			
			PdfPTable table = null;
			
			
			document.open();
			
			try
			{
			   
			  for(int curpage = 1;curpage<=sum_page;curpage++)
			  {
				  if(curpage!=1)
				  {
					  document.newPage();
					  //System.out.println("curpage===="+curpage);
				  }
				  table=new PdfPTable(widths);
				  table.setHorizontalAlignment(Element.ALIGN_CENTER);
				  table.setHorizontalAlignment(Element.ALIGN_TOP);
				  table.setTotalWidth(Float.parseFloat(factWidth));
				  //table.setLockedWidth(true);
				  getTableTitle(document,writer,table,maxColumn);
				  getTableHead(document,writer,table,maxColumn,codeitem,coursedate,curpage,sum_page);
				  getTableBodyHead(document,writer,table,itemlist,hashmap);  
				  getTableBody(code,kind,document,writer,table, coursedate,a0100list,curpage,pagesize,hashmap);
				  getTileHtml(document,table,code,kind,curpage,sum_page,maxColumn,recordNum);
				  float topPix=Float.parseFloat(factHieght)+sige_B;
				  table.writeSelectedRows(0, -1, sige_L, topPix, writer.getDirectContent());
				  
				  //System.out.println(factHieght);
			  }	 
			  //document.add(table);
			 
			  document.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			finally {
			    if(fileStream != null) {
                    PubFunc.closeIoResource(fileStream);
                }
			}	
			
			return url;
		}
		/**
		 * 建立标题信息
		 * 
		 * */
		public void getTableTitle(Document document,PdfWriter writer,PdfPTable table,int maxColumn)
		{
			/**   ##################### 生成表格 ############################ *****/
			Font font=getFont(parsevo.getTitle_fn(),parsevo.getTitle_fb(),parsevo.getTitle_fi(),parsevo.getTitle_fu(),parsevo.getTitle_fz(),"");  //生成字体样式
			String context="";
			if(parsevo.getTitle_fw()!=null&&parsevo.getTitle_fw().length()>0)
			{
				context=parsevo.getTitle_fw();
			}else{
				context=parsevo.getName();
			}
			try{
			PdfPCell cell = null;
			Paragraph paragraph=new Paragraph(context,font);
			
			cell = new PdfPCell(paragraph);
			cell.setColspan(maxColumn);
			
			float title_h=Float.parseFloat(parsevo.getTitle_h());
			String[]ltrb= getLtrb("8");
			cell.setBorderColor(new Color(255, 255, 255));
			cell=excecute(cell,7,ltrb);
			cell.setFixedHeight(title_h);				//设置列最大高度
			cell.setMinimumHeight(title_h);		
			table.addCell(cell);		
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		/**
		 * 建立表头信息
		 * */
		public void getTableHead(Document document,PdfWriter writer,PdfPTable table,int maxColumn,String [] codeitem,String coursedate,int curpage,int sum_page)throws GeneralException
		{
			Font font=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");  //生成字体样式
			KqViewSumBo kqViewSumBo = new KqViewSumBo(this.conn,this.parsevo,this.userView);	
			ArrayList  datelist=kqViewSumBo.getDateList(this.conn,coursedate);
			try{
				PdfPCell cell = null;
				Paragraph paragraph = null;
				float head_h=Float.parseFloat(parsevo.getHead_h());
				int s=maxColumn/3;
				 /**单位**/           
		           String dv_content="";
		           if(codeitem[1]==null||codeitem[1].length()<=0)
		           {
		        	   dv_content="   单位：所有单位";
		           }else
		           {
		        	   dv_content="   单位："+codeitem[1];
		           }
				paragraph=new Paragraph("     "+dv_content,font);
				cell = new PdfPCell(paragraph);
				String[]ltrb= getLtrb("6");
				cell.setBorderColor(new Color(255, 255, 255));
				cell=excecute(cell,6,ltrb);
				cell.setFixedHeight(head_h);				//设置列最大高度
				cell.setMinimumHeight(head_h);	
				
				cell.setColspan(s);
				table.addCell(cell);
				 String bm_content="";
		           if(codeitem[0]==null||codeitem[0].length()<=0)
		           {
		        	   bm_content="   部门：全体部门";
		           }else
		           {
		        	   bm_content="   部门："+codeitem[0];
		           }
				paragraph=new Paragraph("     "+bm_content,font);			
				cell = new PdfPCell(paragraph);
				ltrb= getLtrb("7");
				cell.setBorderColor(new Color(255, 255, 255));
				cell=excecute(cell,6,ltrb);
				cell.setFixedHeight(head_h);				//设置列最大高度
				cell.setMinimumHeight(head_h);			
				cell.setColspan(s);
				table.addCell(cell);
				CommonData vo = (CommonData)datelist.get(0);	           
		        String start_date=vo.getDataName();
		        vo = (CommonData)datelist.get(datelist.size()-1);	 
		        String end_date= vo.getDataName();         
				paragraph=new Paragraph(coursedate+"      ("+start_date+"~"+end_date+")",font);
				cell = new PdfPCell(paragraph);
				ltrb= getLtrb("7");
				cell.setBorderColor(new Color(255, 255, 255));
				cell=excecute(cell,6,ltrb);
				cell.setFixedHeight(head_h);				
				cell.setMinimumHeight(head_h);	
				cell.setColspan(maxColumn-s*2);			
				table.addCell(cell);	
				
				//换行
				int par_column=getNumColumn_2(parsevo.getHead_c(),parsevo.getHead_p(),parsevo.getHead_e(),parsevo.getHead_u(),parsevo.getHead_d(),parsevo.getHead_t());
				
				if(par_column>0)
				{
					Font font_head=getFont(parsevo.getHead_fn(),parsevo.getHead_fb(),parsevo.getHead_fi(),parsevo.getHead_fu(),parsevo.getHead_fz(),"");  //生成字体样式
					int ave=maxColumn/par_column;
					int ave_mod=maxColumn%par_column;
					/**制作人**/
			           int i=1;	
			           /**制作人单位**/
			           if("#u".equals(parsevo.getHead_u().trim()))
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
			        	   String u_str=" 制作人单位"+u_codeitem[0];
			        	   paragraph=new Paragraph(u_str,font_head);
			        	   cell= new PdfPCell(paragraph);
			        	   cell.setFixedHeight(head_h);				
			   			   cell.setMinimumHeight(head_h);	
			        	   if(i==1)
			        	   {
			        		   ltrb= getLtrb("6");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   cell.setColspan(ave);
			        	   }else if(i==par_column)
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   if(ave_mod==0)
			        		   {
			        			   cell.setColspan(ave);
			        		   }else
			        		   {
			        			   cell.setColspan(ave+ave_mod); 
			        		   }
			        	   }else
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1; 
			        		   cell.setColspan(ave);
			        	   } 
			        	   table.addCell(cell);
			           }
			           /**制作日期**/
			           if("#d".equals(parsevo.getHead_d().trim()))
			           {
			        	   String d_str="  制作日期: "+PubFunc.getStringDate("yyyy.MM.dd");
			        	   paragraph=new Paragraph(d_str,font_head);
			        	   cell= new PdfPCell(paragraph);
			        	   cell.setFixedHeight(head_h);				
			   			   cell.setMinimumHeight(head_h);	
			        	   if(i==1)
			        	   {
			        		   ltrb= getLtrb("6");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   cell.setColspan(ave);
			        	   }else if(i==par_column)
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   if(ave_mod==0)
			        		   {
			        			   cell.setColspan(ave);
			        		   }else
			        		   {
			        			   cell.setColspan(ave+ave_mod); 
			        		   }
			        	   }else
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1; 
			        		   cell.setColspan(ave);
			        	   }  
			        	   table.addCell(cell);
			           }
			           if("#e".equals(parsevo.getHead_e().trim()))
			           {
			        	   
			        	   String e_str="    制作人: "+this.userView.getUserFullName();		        	   
			        	   paragraph=new Paragraph(e_str,font_head);
			        	   cell= new PdfPCell(paragraph);
			        	   cell.setFixedHeight(head_h);				
			   			   cell.setMinimumHeight(head_h);	
			        	   if(i==1)
			        	   {
			        		   ltrb= getLtrb("6");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   cell.setColspan(ave);
			        	   }else if(i==par_column)
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   if(ave_mod==0)
			        		   {
			        			   cell.setColspan(ave);
			        		   }else
			        		   {
			        			   cell.setColspan(ave+ave_mod); 
			        		   }
			        	   }else
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1; 
			        		   cell.setColspan(ave);
			        	   } 
			        	   table.addCell(cell);
			        	   	
			           }
			          
			           
			           /**制作时间**/
			           if("#t".equals(parsevo.getHead_t().trim()))
			           {
			        	   String t_str="  时间: "+PubFunc.getStringDate("HH:mm:ss");
			        	   paragraph=new Paragraph(t_str,font_head);
			        	   cell= new PdfPCell(paragraph);
			        	   cell.setFixedHeight(head_h);				
			   			   cell.setMinimumHeight(head_h);	
			        	   if(i==1)
			        	   {
			        		   ltrb= getLtrb("6");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   cell.setColspan(ave);
			        	   }else if(i==par_column)
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   if(ave_mod==0)
			        		   {
			        			   cell.setColspan(ave);
			        		   }else
			        		   {
			        			   cell.setColspan(ave+ave_mod); 
			        		   }
			        	   }else
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1; 
			        		   cell.setColspan(ave);
			        	   }  
			        	   table.addCell(cell);
			        	   
			           }
			           /**页码**/
			           if("#p".equals(parsevo.getHead_p().trim()))
			           {
			        	   String p_str="  页码: 第"+curpage+"页";
			        	   paragraph=new Paragraph(p_str,font_head);
			        	   cell= new PdfPCell(paragraph);
			        	   cell.setFixedHeight(head_h);				
			   			   cell.setMinimumHeight(head_h);	
			        	   if(i==1)
			        	   {
			        		   ltrb= getLtrb("6");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   cell.setColspan(ave);
			        	   }else if(i==par_column)
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   if(ave_mod==0)
			        		   {
			        			   cell.setColspan(ave);
			        		   }else
			        		   {
			        			   cell.setColspan(ave+ave_mod); 
			        		   }
			        	   }else
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1; 
			        		   cell.setColspan(ave);
			        	   }   
			        	   table.addCell(cell);
			           }
			           /**总页码**/
			           if("#c".equals(parsevo.getHead_c().trim()))
			           {
			        	   String c_str="  总页码: 共"+sum_page+" 页";
			        	   paragraph=new Paragraph(c_str,font_head);
			        	   cell= new PdfPCell(paragraph);
			        	   cell.setFixedHeight(head_h);				
			   			   cell.setMinimumHeight(head_h);	
			        	   if(i==1)
			        	   {
			        		   ltrb= getLtrb("6");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   cell.setColspan(ave);
			        	   }else if(i==par_column)
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1;
			        		   if(ave_mod==0)
			        		   {
			        			   cell.setColspan(ave);
			        		   }else
			        		   {
			        			   cell.setColspan(ave+ave_mod); 
			        		   }
			        	   }else
			        	   {
			        		   ltrb= getLtrb("7");
			        		   cell=excecute(cell,6,ltrb);
			        		   i=i+1; 
			        		   cell.setColspan(ave);
			        	   }
			        	   table.addCell(cell);
			           }		       
				  }
				}catch(Exception e){
					e.printStackTrace();
				}
		}
		/**
		 * Body内行信息
		 * 
		 * */
		public void getTableBody(String code,String kind,Document document,PdfWriter writer,PdfPTable table,String coursedate,ArrayList a0100list,int curpage,int pagesize,HashMap hashmap)throws GeneralException
		{
			int start_record=(curpage-1)*pagesize+1;
	        int end_record=curpage*pagesize;
	        
	        float body_h=Float.parseFloat(parsevo.getBody_fz())+13;
	        Font font=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");  //生成字体样式
			for(int i=start_record;i<=a0100list.size()&&i<=end_record;i++)
	    	{
				String a0100[]=(String[])a0100list.get(i-1);
				getOneA0100Data(document,writer,table,code,kind,a0100,coursedate,body_h,hashmap,font,i);
	    	}
		}
		 /**
	     * 通过一个员工编号得到该员工考勤期间的数据
	     * */
	    public void getOneA0100Data(Document document,PdfWriter writer,PdfPTable table,String code,String kind,String a0100[],String coursedate,float body_h,HashMap hashmap,Font font,int num)throws GeneralException
	    {
	    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
					Constant.USED_FIELD_SET);
	    	PdfPCell cell = null;
			Paragraph paragraph = null;
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
	       
	    	
			String[]ltrb= null;	        //姓名
			paragraph=new Paragraph(a0100[1],font);
			cell = new PdfPCell(paragraph);
			ltrb= getLtrb("6");
			cell=excecute(cell,7,ltrb);
			cell.setFixedHeight(body_h);				//设置列最大高度
			cell.setMinimumHeight(body_h);			
			cell.setColspan(1);
			table.addCell(cell);	        
	        RowSet rowSet=null;
	        try{
	          
	          rowSet = dao.search(sql_one_a0100);	
	          Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");
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
	             			 paragraph=new Paragraph(dv+"",font_1);
	           			     cell = new PdfPCell(paragraph);
	           			     ltrb= getLtrb("7");
	           			     cell=excecute(cell,7,ltrb);
	           			     cell.setFixedHeight(body_h);				//设置列最大高度
	           			     cell.setMinimumHeight(body_h);			
	           			     cell.setColspan(1);
	           			     table.addCell(cell);
	             		  }else
	             		  {
	             			 paragraph=new Paragraph("   ",font_1);
	           			     cell = new PdfPCell(paragraph);
	           			     ltrb= getLtrb("7");
	           			     cell=excecute(cell,7,ltrb);
	           			     cell.setFixedHeight(body_h);				//设置列最大高度
	           			     cell.setMinimumHeight(body_h);			
	           			     cell.setColspan(1);
	           			     table.addCell(cell);
	             		  }             		
	               	  }
	        	     
     			     paragraph=new Paragraph("   ",font_1);
     			     cell = new PdfPCell(paragraph);
     			     ltrb= getLtrb("7");
     			     cell=excecute(cell,7,ltrb);
     			     cell.setFixedHeight(body_h);				//设置列最大高度
     			     cell.setMinimumHeight(body_h);			
     			     cell.setColspan(1);
     			     table.addCell(cell);
	             } 
	           
	        }catch(Exception e){
	 	      //throw GeneralExceptionHandler.Handle(e); 
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
	    }
		public void getTableBodyHead(Document document,PdfWriter writer,PdfPTable table,ArrayList columnlist,HashMap hashmap)
		{
			Font font=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");  //生成字体样式
			float body_h=Float.parseFloat((String)hashmap.get("itemleng"))*Float.parseFloat(parsevo.getBody_fz());
			PdfPCell cell = null;
			Paragraph paragraph = null;		
			
			paragraph=new Paragraph(" 姓名 ",font);
			cell = new PdfPCell(paragraph);
			String[]ltrb= getLtrb("6");
			cell=excecute(cell,7,ltrb);
			cell.setFixedHeight(body_h);				//设置列最大高度
			cell.setMinimumHeight(body_h);			
			cell.setColspan(1);
			table.addCell(cell);			
			for(int i=0;i<columnlist.size();i++)
			{
				String value = columnlist.get(i).toString();				
				StringBuffer charvalues=new StringBuffer();
				for(int r=0;r<value.length();r++)
				{
					String str=value.substring(r,r+1);
					if(str.indexOf("(")==-1&&str.indexOf(")")==-1)
					{
						if(r==value.length()-1)
						{
						   		charvalues.append(str);
						}else
						{
								charvalues.append(str+"\n");
						}
					}
				}				
				paragraph=new Paragraph(charvalues.toString(),font);
				cell = new PdfPCell(paragraph);
				ltrb= getLtrb("6");
				cell=excecute(cell,7,ltrb);
				cell.setFixedHeight(body_h);				//设置列最大高度
				cell.setMinimumHeight(body_h);			
				cell.setColspan(1);
				table.addCell(cell);
			}
			paragraph=new Paragraph("  备注  ",font);
			cell = new PdfPCell(paragraph);
			ltrb= getLtrb("7");
			cell=excecute(cell,7,ltrb);
			cell.setFixedHeight(body_h);				//设置列最大高度
			cell.setMinimumHeight(body_h);			
			cell.setColspan(1);
			table.addCell(cell);
		}
		/**
		 * 建立表尾信息
		 * ***/
		 /**
	     * 得到表尾数据
	     * **/
	    public void getTileHtml(Document document,PdfPTable table,String code,String kind,int curpage,int sum_page ,int maxColumn,int recordNum)throws GeneralException
	    {
	    	
	    	PdfPCell cell = null;
			Paragraph paragraph = null;
			String[]ltrb= null;
	    	Font font_b=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");  //生成字体样式
	    	float head_h=Float.parseFloat(parsevo.getBody_fz())+13;
			int s=maxColumn/4;
			StringBuffer back1=new StringBuffer();
	        back1.append("  部门当月参加考勤人数: ");
	        back1.append("  "+recordNum);
	        back1.append(" 人");
			paragraph=new Paragraph(back1.toString(),font_b);
			cell = new PdfPCell(paragraph);
			ltrb= getLtrb("6");
			cell=excecute(cell,6,ltrb);
			cell.setFixedHeight(head_h);				//设置列最大高度
			cell.setMinimumHeight(head_h);			
			cell.setColspan(maxColumn-s*3+2);
			table.addCell(cell);
			paragraph=new Paragraph("   部门领导签字:",font_b);			
			cell = new PdfPCell(paragraph);
			ltrb= getLtrb("7");
			cell=excecute(cell,6,ltrb);
			cell.setFixedHeight(head_h);				//设置列最大高度
			cell.setMinimumHeight(head_h);			
			cell.setColspan(s);
			table.addCell(cell);
			paragraph=new Paragraph("   审核:",font_b);			
			cell = new PdfPCell(paragraph);
			ltrb= getLtrb("7");
			cell=excecute(cell,6,ltrb);
			cell.setFixedHeight(head_h);				//设置列最大高度
			cell.setMinimumHeight(head_h);			
			cell.setColspan(s-1);
			table.addCell(cell);
			paragraph=new Paragraph(" 填报:",font_b);
			cell = new PdfPCell(paragraph);
			ltrb= getLtrb("7");
			cell=excecute(cell,6,ltrb);
			cell.setFixedHeight(head_h);				
			cell.setMinimumHeight(head_h);	
			cell.setColspan(s-1);			
			table.addCell(cell);	
	    	
	    	 
	    	
			
			if(parsevo.getTile_fw()!=null&&parsevo.getTile_fw().length()>0)
	    	{
	    		String tile_fw="    "+parsevo.getTile_fw();
	        	int str_tile_2=tile_fw.length()*Integer.parseInt(parsevo.getBody_fz());
	        	int numrow_2=getNumRow(str_tile_2);
	        	int note_h_2=Integer.parseInt(parsevo.getBody_fz())+6;
	        	if(numrow_2!=0)
	        	{
	        		note_h_2=note_h_2*numrow_2;
	        	}
	        	paragraph=new Paragraph(tile_fw,font_b);
	        	cell= new PdfPCell(paragraph);
	        	cell=excecute(cell,6,ltrb);
	        	cell.setFixedHeight(note_h_2);				//设置列最大高度
	    		cell.setMinimumHeight(note_h_2);
	    		cell.setColspan(maxColumn);
	    		table.addCell(cell);
	    	} 
			
	    	/***表尾基本参数**/
			int par_column=getNumColumn_2(parsevo.getTile_c(),parsevo.getTile_p(),parsevo.getTile_e(),parsevo.getTile_u(),parsevo.getTile_d(),parsevo.getTile_t());
			
			float tile_h=Float.parseFloat(parsevo.getTile_h());
			if(par_column>0)
			{
				Font font_tile=getFont(parsevo.getTile_fn(),parsevo.getTile_fb(),parsevo.getTile_fi(),parsevo.getTile_fu(),parsevo.getTile_fz(),"");  //生成字体样式
				int ave=maxColumn/par_column;
				int ave_mod=maxColumn%par_column;
				//System.out.println("[line:751] maxColumn="+maxColumn);
				//System.out.println("[line:751] ave="+ave);
				//System.out.println("[line:751] ave_mod="+ave_mod);
				/**制作人**/
		           int i=1;	
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
		        	   String u_str="  制作人单位"+u_codeitem[0];
		        	   paragraph=new Paragraph(u_str,font_tile);
		        	   cell= new PdfPCell(paragraph);
		        	   cell.setFixedHeight(tile_h);				
		   			   cell.setMinimumHeight(tile_h);	
		        	   if(i==1)
		        	   {
		        		   ltrb= getLtrb("6");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   cell.setColspan(ave);
		        	   }else if(i==par_column)
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   if(ave_mod==0)
		        		   {
		        			   cell.setColspan(ave);
		        		   }else
		        		   {
		        			   cell.setColspan(ave+ave_mod); 
		        		   }
		        	   }else
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1; 
		        		   cell.setColspan(ave);
		        	   } 
		        	   table.addCell(cell);
		           }
		           /**制作日期**/
		           if("#d".equals(parsevo.getTile_d().trim()))
		           {
//		        	   String d_str="  制作日期: "+PubFunc.getStringDate("yyyy.MM.dd");
		        	   String d_str=" 制作日期: "+this.sjelement;  //用户可以自己输入
		        	   paragraph=new Paragraph(d_str,font_tile);
		        	   cell= new PdfPCell(paragraph);
		        	   cell.setFixedHeight(tile_h);				
		   			   cell.setMinimumHeight(tile_h);	
		        	   if(i==1)
		        	   {
		        		   ltrb= getLtrb("6");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   cell.setColspan(ave);
		        	   }else if(i==par_column)
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   if(ave_mod==0)
		        		   {
		        			   cell.setColspan(ave);
		        		   }else
		        		   {
		        			   cell.setColspan(ave+ave_mod); 
		        		   }
		        	   }else
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1; 
		        		   cell.setColspan(ave);
		        	   }  
		        	   table.addCell(cell);
		           }
		           if("#e".equals(parsevo.getTile_e().trim()))
		           {
		        	   
		        	   String e_str="    制作人: "+this.userView.getUserFullName();		        	   
		        	   paragraph=new Paragraph(e_str,font_tile);
		        	   cell= new PdfPCell(paragraph);
		        	   cell.setFixedHeight(tile_h);				
		   			   cell.setMinimumHeight(tile_h);	
		        	   if(i==1)
		        	   {
		        		   ltrb= getLtrb("6");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   cell.setColspan(ave);
		        	   }else if(i==par_column)
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   if(ave_mod==0)
		        		   {
		        			   cell.setColspan(ave);
		        		   }else
		        		   {
		        			   cell.setColspan(ave+ave_mod); 
		        		   }
		        	   }else
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1; 
		        		   cell.setColspan(ave);
		        	   } 
		        	   table.addCell(cell);
		        	   	
		           }
		           
		           
		           /**制作时间**/
		           if("#t".equals(parsevo.getTile_t().trim()))
		           {
//		        	   String t_str="  时间: "+PubFunc.getStringDate("HH:mm:ss");
		        	   String t_str=" 时间: "+this.timeqd;
		        	   paragraph=new Paragraph(t_str,font_tile);
		        	   cell= new PdfPCell(paragraph);
		        	   cell.setFixedHeight(tile_h);				
		   			   cell.setMinimumHeight(tile_h);	
		        	   if(i==1)
		        	   {
		        		   ltrb= getLtrb("6");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   cell.setColspan(ave);
		        	   }else if(i==par_column)
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   if(ave_mod==0)
		        		   {
		        			   cell.setColspan(ave);
		        		   }else
		        		   {
		        			   cell.setColspan(ave+ave_mod); 
		        		   }
		        	   }else
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1; 
		        		   cell.setColspan(ave);
		        	   }  
		        	   table.addCell(cell);
		        	   
		           }
		           /**页码**/
		           if("#p".equals(parsevo.getTile_p().trim()))
		           {
		        	   String p_str="  页码: 第"+curpage+"页";
		        	   paragraph=new Paragraph(p_str,font_tile);
		        	   cell= new PdfPCell(paragraph);
		        	   cell.setFixedHeight(tile_h);				
		   			   cell.setMinimumHeight(tile_h);	
		        	   if(i==1)
		        	   {
		        		   ltrb= getLtrb("6");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   cell.setColspan(ave);
		        	   }else if(i==par_column)
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   if(ave_mod==0)
		        		   {
		        			   cell.setColspan(ave);
		        		   }else
		        		   {
		        			   cell.setColspan(ave+ave_mod); 
		        		   }
		        	   }else
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1; 
		        		   cell.setColspan(ave);
		        	   }   
		        	   table.addCell(cell);
		           }
		           /**总页码**/
		           if("#c".equals(parsevo.getTile_c().trim()))
		           {
		        	   String c_str="  总页码: 共"+sum_page+" 页";
		        	   paragraph=new Paragraph(c_str,font_tile);
		        	   cell= new PdfPCell(paragraph);
		        	   cell.setFixedHeight(tile_h);				
		   			   cell.setMinimumHeight(tile_h);	
		        	   if(i==1)
		        	   {
		        		   ltrb= getLtrb("6");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   cell.setColspan(ave);
		        	   }else if(i==par_column)
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1;
		        		   if(ave_mod==0)
		        		   {
		        			   cell.setColspan(ave);
		        		   }else
		        		   {
		        			   cell.setColspan(ave+ave_mod); 
		        		   }
		        	   }else
		        	   {
		        		   ltrb= getLtrb("7");
		        		   cell=excecute(cell,6,ltrb);
		        		   i=i+1; 
		        		   cell.setColspan(ave);
		        	   }
		        	   table.addCell(cell);
		           }		       
			  }
	    }
		/**%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%单元格%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%**/
		/**
		 * (每个cell表示一个单元格)	 
		 * @param align     单元格内容的排列方式
		 * @param ltrb[]    c.L,c.T,c.R,c.B
		 * @param PdfPCell cell
		 */
		public  PdfPCell  excecute(PdfPCell cell,int align,String[] ltrb)
		{
			try
			{
				Rectangle borders = new Rectangle(0f, 0f);
				  
				if("1".equals(ltrb[0]))
				  {
					  borders.setBorderWidthLeft(0.6f);
					  borders.setBorderColorLeft(new Color(0, 0, 0));
				  }
				  else {
                    borders.setBorderWidthLeft(0f);
                }
				  
				  if("1".equals(ltrb[1]))
				  {
					  borders.setBorderWidthTop(0.6f);
					  borders.setBorderColorTop(new Color(0, 0, 0));	
				  }
				  else {
                      borders.setBorderWidthTop(0f);
                  }
				  
				  if("1".equals(ltrb[2]))
				  {
					  borders.setBorderWidthRight(0.6f);
					  borders.setBorderColorRight(new Color(0, 0, 0));
				  }
				  else {
                      borders.setBorderWidthRight(0f);
                  }
				  
				  if("1".equals(ltrb[3]))
				  {
					  borders.setBorderWidthBottom(0.6f);
					  borders.setBorderColorBottom(new Color(0, 0, 0));
				  }
				  else {
                      borders.setBorderWidthBottom(0.6f);
                  }
				      borders.setBorderColorBottom(new Color(250, 250, 250));
				/*  单元格内容的排列方式
				 * =0上左 =1上中  =2上右  =3下左  =4下中  =5下右 =6中左  =7中中 =8中右
			     */			
				 if(align==0)   
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);    //基于最合适的
				 }
				 else if(align==1)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				 }
				 else if(align==2)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 }
				 else if(align==3)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
					 cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				 }
				 else if(align==4)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					 cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				 }
				 else if(align==5)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					 cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				 }
				 else if(align==6)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
					 cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				 }
				 else if(align==7)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_CENTER);   //居中
					 cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				 }
				 else if(align==8)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_RIGHT);   //居右
					 cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				 }			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return cell;
		}
		/**
		 * 生成字体样式,解决中文问题
		 * 
		 */
		public Font getFont(String fn,String bold,String italic,String underline,String fontSizeStr,String color)
		{
			Font font=null;
			BaseFont bfComic=null;	
			int fontSize=12;
			if(fontSizeStr!=null&&fontSizeStr.length()>0)
			{
				fontSize=Integer.parseInt(fontSizeStr);
			}
			try
			{
				if(FontFamilyType.getFontFamilyTTF(fn)==null)
				{
					bfComic = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   //解决中文问题
				}else
				{
					bfComic=BaseFont.createFont(FontFamilyType.getFontFamilyTTF(fn), BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
				}
			    		
			
				if("#fb[1]".equals(bold.trim())&&!"#fu[1]".equals(underline.trim())&&!"#fi[1]".equals(underline.trim()))
				{   //粗体
					font=new Font(bfComic,fontSize+2, Font.BOLD);
				}else if(!"#fb[1]".equals(bold.trim())&&!"#fu[1]".equals(underline.trim())&&"#fi[1]".equals(underline.trim()))
				{   //斜体
					font=new Font(bfComic,fontSize+2, Font.ITALIC);
				}else if(!"#fb[1]".equals(bold.trim())&&"#fu[1]".equals(underline.trim())&&!"#fi[1]".equals(underline.trim()))
				{   //下划线
					font=new Font(bfComic,fontSize+2, Font.UNDEFINED);
				}else if("#fb[1]".equals(bold.trim())&&"#fu[1]".equals(underline.trim())&&!"#fi[1]".equals(underline.trim()))
				{   //粗体||下划线
					font=new Font(bfComic,fontSize+2, Font.BOLD | Font.UNDEFINED);
				}else if("#fb[1]".equals(bold.trim())&&!"#fu[1]".equals(underline.trim())&&"#fi[1]".equals(underline.trim()))
				{   //粗体||斜体
					font=new Font(bfComic,fontSize+2, Font.BOLD | Font.ITALIC);
				}else if(!"#fb[1]".equals(bold.trim())&&"#fu[1]".equals(underline.trim())&&"#fi[1]".equals(underline.trim()))
				{   //斜体||下划线
					font=new Font(bfComic,fontSize+2, Font.ITALIC | Font.UNDEFINED);
				}else if("#fb[1]".equals(bold.trim())&&"#fu[1]".equals(underline.trim())&&"#fi[1]".equals(underline.trim()))
				{   //斜体||下划线
					font=new Font(bfComic,fontSize+2, Font.ITALIC | Font.UNDEFINED |Font.BOLD);
				}
				else
				{
					font=new Font(bfComic,fontSize+2, Font.NORMAL);
				}
				if(color!=null&&color.length()>0)
				{
					if(color.length()>9)
			    	{
						int cos=Integer.parseInt(color.substring(1,4));
			            int cos1=Integer.parseInt(color.substring(4,7));
			            int cos2=Integer.parseInt(color.substring(7,10));
			    		font.setColor( new Color(cos, cos1, cos2));
			    	}
				
				}else
		    	{
		    		font.setColor( new Color(0x00, 0x00, 0x00));
		    	}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return font;
			
		}
		/**
		 * 表格样式边线
		 * @return String[] ltrb
		 *   ltrb[0]=左
		 *   ltrb[1]=上
		 *   ltrb[2]=右
		 *   ltrb[3]=下
		 * **/
		public String[] getLtrb(String v)
		{
			String[] ltrb = new String[4];
			if("1".equals(v))
			{
				ltrb[0]="1";
			    ltrb[1]="1";
				ltrb[2]="1";
				ltrb[3]="1";
			}else if("2".equals(v))
			{
				ltrb[0]="0";
			    ltrb[1]="1";
				ltrb[2]="1";
				ltrb[3]="1";
			}else if("3".equals(v))
			{
				ltrb[0]="1";
			    ltrb[1]="0";
				ltrb[2]="1";
				ltrb[3]="1";
			}else if("4".equals(v))
			{
				ltrb[0]="1";
			    ltrb[1]="1";
				ltrb[2]="0";
				ltrb[3]="1";
			}else if("5".equals(v))
			{
				ltrb[0]="1";
			    ltrb[1]="1";
				ltrb[2]="1";
				ltrb[3]="0";
			}else if("6".equals(v))
			{
				ltrb[0]="1";
			    ltrb[1]="0";
				ltrb[2]="0";
				ltrb[3]="1";
			}else if("7".equals(v))
			{
				ltrb[0]="0";
			    ltrb[1]="1";
				ltrb[2]="0";
				ltrb[3]="1";
			}else if("8".equals(v))
			{
				ltrb[0]="0";
			    ltrb[1]="0";
				ltrb[2]="0";
				ltrb[3]="0";
			}
			return ltrb;
		}
		/****************参数的运算&定义********************/
		 /**
	     * 以用高度
	     * **/
	    public float getIsUseHieght(int leng)throws GeneralException
	    {
	       	
	    	float height=Float.parseFloat(getPxFormMm_f(parsevo.getTop()))+Float.parseFloat(getPxFormMm_f(parsevo.getBottom()))+Float.parseFloat(getPxFormMm_f(parsevo.getTitle_h()));
	    	height=height+Float.parseFloat(getPxFormMm_f(parsevo.getHead_h()));
	    	height=height+Float.parseFloat(leng+"")+13;
	    	   	
//	    	处室签字
	    	height=height+Float.parseFloat(getPxFormMm_f(parsevo.getBody_fz()))+13; 	
	    	
	    	//计算表尾客户添加的文本内容
	    	if(parsevo.getTile_fw()!=null&&parsevo.getTile_fw().length()>0)
	    	{
	    		/*****#代表一个空格****/
	    		String tile_fw="#####2."+parsevo.getTile_fw();
	    		int str_tile_2=tile_fw.length()*Integer.parseInt(parsevo.getBody_fz());
	    		
	    		int note_tile_2=getNumRow(str_tile_2);
	    		
	    		if(note_tile_2!=0)
	        	{
	        		height=height+(Float.parseFloat(parsevo.getBody_fz())+6)*note_tile_2;	
	        	}   		
	    	}
	    	
	    	if("#c".equals(parsevo.getHead_c())||"#p".equals(parsevo.getHead_p())||"#e".equals(parsevo.getHead_e())||"#u".equals(parsevo.getHead_u())||"#d".equals(parsevo.getHead_d())||"#t".equals(parsevo.getHead_t()))
	    	{
	    		height=height+Float.parseFloat(getPxFormMm_f(parsevo.getHead_h()));
	    	}
	    	
	    	if("#c".equals(parsevo.getTile_c())||"#p".equals(parsevo.getTile_p())||"#e".equals(parsevo.getTile_e())||"#u".equals(parsevo.getTile_u())||"#d".equals(parsevo.getTile_d())||"#t".equals(parsevo.getTile_t()))
	    	{
	    		height=height+Float.parseFloat(getPxFormMm_f(parsevo.getTile_h()));
	    	}    	
	    	
	    	return height;
	    }  
	    /***
	     * 剩余高度
	     * */
	    
	    public float getSpareHieght(int leng)throws GeneralException
	    {
	       	float spare_hieght=0;
	       	float height=getIsUseHieght(leng);
	    	String unit=parsevo.getUnit().trim();
	    	if("px".equals(unit))
	    	{
	    		spare_hieght=Float.parseFloat(getFactHeight())-height;
	    	}else
	    	{
	    		spare_hieght=Float.parseFloat(getFactHeight())-height/this.rate;
	    	}
	    	return spare_hieght;
	    }  
	    /**
	     * 转换，毫米转换为像素
	     * */
	    public String getPxFormMm_f(String value)
	    {
	    	String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
	    	if("mm".equals(unit))
			{
	    		float dv=Float.parseFloat(value)/this.rate;
	    		return KqReportInit.round(dv+"",0);
			}else
			{
				return KqReportInit.round(value,0);
			}
	    }
		/**
		 * 计算表格实际总宽度
		 * */
		public String getFactWidth()
		{
			String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
			if("1".equals(parsevo.getOrientation().trim()))
			{
				if("px".equals(unit))
				{
					float width=Float.parseFloat(parsevo.getHeight())/this.rate-Float.parseFloat(getPxFormMm_f(parsevo.getLeft()))-Float.parseFloat(getPxFormMm_f(parsevo.getRight()));
					return KqReportInit.round(width+"",0);	
				}else
				{
					float width=Float.parseFloat(parsevo.getHeight())/this.rate-Float.parseFloat(getPxFormMm_f(parsevo.getLeft()))-Float.parseFloat(getPxFormMm_f(parsevo.getRight()));
					return KqReportInit.round(width+"",0);	
				}
			}else{
				if("px".equals(unit))
				{
					float width=Float.parseFloat(parsevo.getWidth())/this.rate-Float.parseFloat(getPxFormMm_f(parsevo.getLeft()))-Float.parseFloat(getPxFormMm_f(parsevo.getRight()));
					return KqReportInit.round(width+"",0);	
				}else
				{
					float width=Float.parseFloat(parsevo.getWidth())/this.rate-Float.parseFloat(getPxFormMm_f(parsevo.getLeft()))-Float.parseFloat(getPxFormMm_f(parsevo.getRight()));
					return KqReportInit.round(width+"",0);	
				}
			}		
		}
		/**
		 * 计算表格实际总高度
		 * */
		public String getFactHeight()
		{
			String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
			if("1".equals(parsevo.getOrientation().trim()))
			{
				if("px".equals(unit))
				{
					double height=Double.parseDouble(parsevo.getWidth())/this.rate-Double.parseDouble(getPxFormMm_f(parsevo.getTop()))-Double.parseDouble(getPxFormMm_f(parsevo.getBottom()));
					return KqReportInit.round(height+"",0);	
				}else
				{
					double height=Double.parseDouble(parsevo.getWidth())/this.rate-Double.parseDouble(getPxFormMm_f(parsevo.getTop()))-Double.parseDouble(getPxFormMm_f(parsevo.getBottom()));
					return KqReportInit.round(height+"",0);	
				}
			}else{
			    if("px".equals(unit))
			    {
				    double height=Double.parseDouble(parsevo.getHeight())/this.rate-Double.parseDouble(getPxFormMm_f(parsevo.getTop()))-Double.parseDouble(getPxFormMm_f(parsevo.getBottom()));
				    return KqReportInit.round(height+"",0);	
			    }else
			    {
				    double height=Double.parseDouble(parsevo.getHeight())/this.rate-Double.parseDouble(getPxFormMm_f(parsevo.getTop()))-Double.parseDouble(getPxFormMm_f(parsevo.getBottom()));
				    return KqReportInit.round(height+"",0);	
			    }
			}
		}
		  /**
		 * 计算在规定的字数中，一串字符，有多少行
		 * */
		public int getNumRow(int strlen)
		{
			 int factwidth=Integer.parseInt(getFactWidth());
			 
				 int ss=strlen/factwidth;
				 int dd=strlen%factwidth;
		  	     if(dd!=0)
		  	     {
		  	    	ss=ss+1;
		  	     } 
		  	   return ss; 		 	   
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
		
		 
  }
