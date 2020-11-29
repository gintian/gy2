package com.hjsj.hrms.businessobject.kq.register.history;


import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExecuteKqDailyPdf {
    private Connection conn;
    private ReportParseVo parsevo;    
    private UserView userView;
    private final float rate=0.24f;
    private String self_flag;
    private String sjelement;  //制作时间 用户可以修改
    private String timeqd;  //时间 用户可以修改
//    private final int r_add_height=16;  原来
    private final int r_add_height=54;
    private String whereIN;
    private String cardno="";
    private boolean kqtablejudge;  //首钢增加 true展现本月出缺勤情况统计小计，否则原始
    private String dbty;   //人员库
	public String getWhereIN() {
		return whereIN;
	}
	public void setWhereIN(String whereIN) {
		this.whereIN = whereIN;
	}
	public String getSelf_flag()
	{
		if(this.self_flag==null||this.self_flag.length()<=0) {
            this.self_flag="";
        }
		return this.self_flag;
	}
	public void setSelf_flag(String self_flag) {
		this.self_flag = self_flag;
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
	public ExecuteKqDailyPdf()
	{
	}
	public ExecuteKqDailyPdf(Connection conn)
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
		KqViewDailyBo kqView = new KqViewDailyBo();
		this.kqtablejudge=kqView.getkqtablejudge(this.conn);  //考勤表：true=展现本月出缺勤情况统计小计 false=不展现
		ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);
    	ArrayList kqq03list=kqView.savekqq03list(this.conn,fielditemlist);  //本月出缺勤情况统计小计 头内容
    	String pu="1";
    	for(int u=0;u<kqq03list.size();u++)
    	{
    		FieldItem fielditem=(FieldItem)kqq03list.get(u);
    		if(!"i9999".equals(fielditem.getItemid()))
    		{
    			if("A0177".equalsIgnoreCase(fielditem.getItemid()))
    			{
    				pu="0";
    			}
    		}
    	}
		KqReportInit kqReprotInit = new KqReportInit(this.conn);
	    ArrayList item_list=kqReprotInit.getKq_Item_listPdf();//考勤项目参数
	   
	    double spare_h=getSpareHieght(item_list);
	    int pagesize=0;
	    
	    boolean noSelected = false;
		
		if ((!"#dept[1]".equalsIgnoreCase(parsevo.getBody_dept())) 
	    		&& (!"#pos[1]".equalsIgnoreCase(parsevo.getBody_pos())) 
	    		&& (!"#gh[1]".equalsIgnoreCase(parsevo.getBody_gh())) 
	    		&& (!"#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu())) 
	    		&& (!"#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()))) {
	    	noSelected = true;
	    	
	    }
	    
	    if(this.kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || noSelected))
	    {
	    	if("0".equals(pu))
	    	{
	    		if("#pr[1]".equals(parsevo.getBody_pr())&&parsevo.getBody_rn()!=null&&parsevo.getBody_rn().length()>0)
	    	    {
	    	    	pagesize=Integer.parseInt(parsevo.getBody_rn());
	    	    }else
	    	    {
	    	    	pagesize=(int)spare_h/(Integer.parseInt(parsevo.getBody_fz())+100);
	    	    	//pagesize=1;
	    	    }
	    	}else
	    	{
	    		if("#pr[1]".equals(parsevo.getBody_pr())&&parsevo.getBody_rn()!=null&&parsevo.getBody_rn().length()>0)
	    	    {
	    	    	pagesize=Integer.parseInt(parsevo.getBody_rn());
	    	    }else
	    	    {
	    	    	pagesize=(int)spare_h/(Integer.parseInt(parsevo.getBody_fz())+80);
	    	    	//pagesize=1;
	    	    }
	    	}
	    }else
	    {
	    	if("#pr[1]".equals(parsevo.getBody_pr())&&parsevo.getBody_rn()!=null&&parsevo.getBody_rn().length()>0)
		    {
		    	pagesize=Integer.parseInt(parsevo.getBody_rn());
		    }else
		    {
		    	pagesize=(int)spare_h/(Integer.parseInt(parsevo.getBody_fz())+r_add_height);
		    	//pagesize=1;
		    }
	    }
	    
        //System.out.println("line 93 ="+pagesize);
	    if("self".equals(this.getSelf_flag()))
	    {
	    	if(this.userView.getUserDeptId()!=null&&this.userView.getUserDeptId().length()>0) {
                this.whereIN=" from q03_arc WHERE e0122='"+this.userView.getUserDeptId()+"'";
            } else if(this.userView.getUserOrgId()!=null&&this.userView.getUserOrgId().length()>0) {
                this.whereIN=" from q03_arc WHERE b0110='"+this.userView.getUserOrgId()+"'";
            } else {
                this.whereIN=" from q03_arc  WHERE a0100='"+this.userView.getA0100()+"' and nbase='"+this.userView.getDbname()+"'";
            }
		}
	    KqParameter para=new KqParameter(new HashMap(),this.userView,code,this.conn);
	    this.cardno=para.getG_no();
        KqViewDailyBo kqViewDailyBo = new KqViewDailyBo(this.conn,this.parsevo,this.userView);
        kqViewDailyBo.setCardno(this.cardno);
        kqViewDailyBo.setSelf_flag(this.getSelf_flag());
        kqViewDailyBo.setDbtype(this.dbty);
		ArrayList datelist=kqViewDailyBo.getDateList(this.conn,coursedate);
//		ArrayList kq_dbase_list=userView.getPrivDbList(); //这取的是全部人员库 这里数据考勤表有个错误，这里得到的是全部人员库，但是应该是考勤员权限下的人员库才对
		/**得到考勤权限下的人员库**/
	    KqUtilsClass kqUtilsClass=new KqUtilsClass(this.conn,this.userView);
		ArrayList kq_dbase_list=kqUtilsClass.setKqPerList(code,kind);
		/**结束**/
//		int recordNum=kqViewDailyBo.getAllRecordNum(code,kind,datelist,kq_dbase_list);//总纪录数
		int recordNum;
		String dbty=this.getDbty();
		if("".equals(dbty)||dbty==null) {
            dbty="all";
        }
		if(!"all".equals(dbty)&&dbty.length()>0)
		{
			recordNum=kqViewDailyBo.getAllRecordNum2(code,kind,datelist,dbty);//总纪录数
		}else
		{
			recordNum=kqViewDailyBo.getAllRecordNum(code,kind,datelist,kq_dbase_list);//总纪录数
		}
		
		int sum_page=(recordNum-1)/pagesize+1;
		ArrayList keylist=new ArrayList();
	    //keylist.add("q03z0");
	    keylist.add("a0100");
	    keylist.add("nbase");
	    kqViewDailyBo.setSelf_flag(this.getSelf_flag());
	    kqViewDailyBo.setWhereIN(this.getWhereIN());
//	    ArrayList a0100list=kqViewDailyBo.getA0100List(code,kind,datelist,1,recordNum,kq_dbase_list,keylist);//所有人员信息
	    ArrayList a0100list=new ArrayList();
	    if(!"all".equals(dbty)&&dbty.length()>0)
		{
	    	a0100list=kqViewDailyBo.getA0100Listusr(code,kind,datelist,1,recordNum,kq_dbase_list,keylist,dbty);//所有人员信息
		}else
		{
			a0100list=kqViewDailyBo.getA0100List(code,kind,datelist,1,recordNum,kq_dbase_list,keylist);//所有人员信息
		}
		
		String [] codeitem=kqViewDailyBo.getCodeItemDesc(code);//部门信息
		
		String[] date=coursedate.split("-");
        String kq_year=date[0];
        String kq_duration=date[1];
		String url="";
		url=code+"_"+kq_year+"_"+kq_duration+"_"+PubFunc.getStrg()+".pdf";
		String factWidth=getFactWidth();//实际宽度		
		String factHieght=getFactHeight();//实际高度
		float paperW=Float.parseFloat(parsevo.getWidth())/this.rate;
		float paperH=Float.parseFloat(parsevo.getHeight())/this.rate;
		
		Rectangle pageSize = new Rectangle(paperW,paperH);	
		float sige_T=Float.parseFloat(getPxFormMm_f(parsevo.getTop()));
		float sige_B=Float.parseFloat(getPxFormMm_f(parsevo.getBottom()));
		float sige_L=Float.parseFloat(getPxFormMm_f(parsevo.getLeft()));
		float sige_R=Float.parseFloat(getPxFormMm_f(parsevo.getRight()));
		Document document = null;
		
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
			writer= PdfWriter.getInstance(document,
					new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+url));
		}catch(Exception e)
		{
		    if(writer!=null){
                PubFunc.closeIoResource(writer);
            }
			e.printStackTrace();
		}	
		
		
		
		document.setMargins(sige_T, sige_B, sige_L, sige_R);
//		int maxColumn=datelist.size()+3;
//		
//		float[] widths = new float[maxColumn];;
//		if(maxColumn>0)
//		{
//			double ave_width=1d/maxColumn;
//			double ave_data_width=ave_width-0.005;
//			double sp_sum_width=1-ave_data_width*datelist.size();
//			double ave_sp_width=sp_sum_width/17;
//			widths[0]=Float.parseFloat(ave_sp_width+"");
//			widths[1]=Float.parseFloat(ave_sp_width+"");
//			widths[2]=Float.parseFloat(ave_sp_width+"");
//			for(int i=3;i<datelist.size()+34;i++)
//			{
//				widths[i]=Float.parseFloat(ave_data_width+"");
//			}
//		}
		int maxColumn=0;
		int ci=5;
		float[] widths=null;
		//考勤表：true=展现本月出缺勤情况统计小计 false=不展现
		if ((!"#dept[1]".equalsIgnoreCase(parsevo.getBody_dept())) 
	    		&& (!"#pos[1]".equalsIgnoreCase(parsevo.getBody_pos())) 
	    		&& (!"#gh[1]".equalsIgnoreCase(parsevo.getBody_gh())) 
	    		&& (!"#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu())) 
	    		&& (!"#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()))) {
	    	noSelected = true;
	    	
	    }
	    
		if("1".equals(para.getKq_orgView_post())) {
            ci=4;
        }
		
	    if(this.kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || noSelected))
	    {
	    	int kqq03=kqq03list.size();
			if(kqq03>14)
			{
				kqq03=14;
			}
	    	maxColumn=datelist.size()+kqq03+ci;
			widths = new float[maxColumn];;
			if(maxColumn>0)
			{
				double ave_width=1d/maxColumn;
				double ave_data_width=ave_width-0.005;
				double sp_sum_width=1-ave_data_width*datelist.size();
//				double ave_sp_width=sp_sum_width/17;
				
				// 使报表的序号，姓名，工号三列变宽点
				double ave_sp_width=sp_sum_width/17;
				
				widths[0]=Float.parseFloat(ave_sp_width+"");
				widths[1]=Float.parseFloat(ave_sp_width+"");
				widths[2]=Float.parseFloat(ave_sp_width+"");
				widths[3]=Float.parseFloat(ave_sp_width+"");
				widths[4]=Float.parseFloat(ave_sp_width+"");
				for(int i=5;i<(datelist.size()+kqq03+ci);i++)
				{
					widths[i]=Float.parseFloat(ave_data_width+"");
				}
			}
		}else
		{
			
			
			
			maxColumn=datelist.size()+ci;
			
			widths = new float[maxColumn];;
			if(maxColumn>0)
			{
				double ave_width=1d/maxColumn;
				double ave_data_width=ave_width-0.005;
				double sp_sum_width=1-ave_data_width*datelist.size();
				double ave_sp_width=sp_sum_width/5;
				widths[0]=Float.parseFloat(ave_sp_width+"");
				widths[1]=Float.parseFloat(ave_sp_width+"");
				widths[2]=Float.parseFloat(ave_sp_width+"");
				widths[3]=Float.parseFloat(ave_sp_width+"");
				widths[4]=Float.parseFloat(ave_sp_width+"");
				for(int i=5;i<datelist.size()+ci;i++)
				{
					widths[i]=Float.parseFloat(ave_data_width+"");
				}
			}
		}
		PdfPTable table = null;
		
		
		document.open();
		
		try
		{
		   
		  int start_record=0;
		  int end_record=0;
		  //sum_page 总页数; start_record 第几个开始 ； end_record 第几个结束
		  for(int curpage = 1;curpage<=sum_page;curpage++)
		  {
			  if(curpage!=1)
			  {
				  document.newPage();
				 // System.out.println("curpage===="+curpage);
			  }
			  start_record=start_record+1;
			  end_record=curpage*pagesize;  //一页多少行
			  table=new PdfPTable(widths); //widths 就是要生多少个表格数
			  table.setHorizontalAlignment(Element.ALIGN_CENTER);
			  table.setHorizontalAlignment(Element.ALIGN_TOP);
			  table.setTotalWidth(Float.parseFloat(factWidth));  //文件的总体宽度
			  //table.setLockedWidth(true);
			  getTableTitle(document,writer,table,maxColumn);
			  getTableHead(document,writer,table,maxColumn,codeitem,coursedate,curpage,sum_page,kqq03list,datelist.size());
			  getTableBodyHead(document,writer,table,datelist,kqq03list);  
			  start_record=getTableBody(code,kind,document,writer,table, datelist,a0100list,start_record,end_record,item_list,coursedate,kqq03list);
			  getTileHtml(document,table,code,kind,curpage,sum_page,item_list,maxColumn);
			  float topPix=Float.parseFloat(factHieght)+sige_B;
			  table.writeSelectedRows(0, -1, sige_L, topPix, writer.getDirectContent());
			 
			 // System.out.println(factHieght);
		  }	 
		  //document.add(table);
		 
		  document.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
		    if(writer!=null){
		        PubFunc.closeIoResource(writer);
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
		String[]ltrb= getLtrb("1");
		
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
	public void getTableHead(Document document,PdfWriter writer,PdfPTable table,int maxColumn,String [] codeitem,String coursedate,int curpage,int sum_page,ArrayList kqq03list,int datesize)
	{
		Font font=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");  //生成字体样式
		String[] date=coursedate.split("-");
        String kq_year=date[0];
        String kq_duration=date[1];
		try{
			/**时间显示，格式如：2010-02 (2010.02.01~2010.02.28) wangy**/
	        KqViewDailyBo kqView = new KqViewDailyBo(this.conn);
	        ArrayList datelist=kqView.getDateList(this.conn,coursedate);
	        CommonData vo = (CommonData)datelist.get(0);	           
	        String start_date=vo.getDataName();
	        vo = (CommonData)datelist.get(datelist.size()-1);	 
	        String end_date= vo.getDataName();
	        /**结束**/
			PdfPCell cell = null;
			Paragraph paragraph = null;
			float head_h=Float.parseFloat(parsevo.getHead_h());
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
			cell=excecute(cell,6,ltrb);
			cell.setFixedHeight(head_h);				//设置列最大高度
			cell.setMinimumHeight(head_h);			
			cell.setColspan(maxColumn/4);
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
			cell=excecute(cell,6,ltrb);
			cell.setFixedHeight(head_h);				//设置列最大高度
			cell.setMinimumHeight(head_h);			
			cell.setColspan(maxColumn/4);
			table.addCell(cell);
			
			boolean noSelected = false;
			
			if ((!"#dept[1]".equalsIgnoreCase(parsevo.getBody_dept())) 
		    		&& (!"#pos[1]".equalsIgnoreCase(parsevo.getBody_pos())) 
		    		&& (!"#gh[1]".equalsIgnoreCase(parsevo.getBody_gh())) 
		    		&& (!"#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu())) 
		    		&& (!"#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()))) {
		    	noSelected = true;
		    	
		    }
			if (kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || noSelected)) {
//				System.out.println("人员长度 = "+kqq03list.size());
//				paragraph=new Paragraph("      "+kq_year+" 年 第"+kq_duration+" 期间",font); //原来
				paragraph=new Paragraph("      "+coursedate+" ("+start_date+"~"+end_date+")");
				cell = new PdfPCell(paragraph);
				ltrb= getLtrb("7");
				cell=excecute(cell,6,ltrb);
				cell.setFixedHeight(head_h);				
				cell.setMinimumHeight(head_h);	
				cell.setColspan(datesize - 15);			
				table.addCell(cell);
				
				paragraph=new Paragraph(" 本月出缺勤情况统计小计 ",font);
				cell = new PdfPCell(paragraph);
				ltrb= getLtrb("7");
				cell=excecute(cell,6,ltrb);
				cell.setFixedHeight(head_h);				
				cell.setMinimumHeight(head_h);	
				cell.setColspan(maxColumn-(maxColumn/4)*3);			
				table.addCell(cell);
			}else
			{
				
//				paragraph=new Paragraph("      "+kq_year+" 年 第"+kq_duration+" 期间",font);  //原来
				paragraph=new Paragraph("      "+coursedate+" ("+start_date+"~"+end_date+")");
				cell = new PdfPCell(paragraph);
				ltrb= getLtrb("7");
				cell=excecute(cell,6,ltrb);
				cell.setFixedHeight(head_h);				
				cell.setMinimumHeight(head_h);	
				cell.setColspan(maxColumn-(maxColumn/4)*2);			
				table.addCell(cell);
				
			}
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
	public int  getTableBody(String code,String kind,Document document,PdfWriter writer,PdfPTable table,ArrayList datelist,ArrayList a0100list,int start_record,int end_record,ArrayList item_list,String coursedate,ArrayList kqq03list)throws GeneralException
	{
        float body_h=Float.parseFloat(parsevo.getBody_fz())+r_add_height;
        Font font=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");  //生成字体样式
		int row_h=0;
        for(int i=start_record;i<=a0100list.size()&&i<=end_record;i++)
    	{
			String a0100[]=(String[])a0100list.get(i-1);
			row_h=getOneA0100Data(document,writer,table,code,kind,a0100,datelist,body_h,item_list,font,i,coursedate,kqq03list);
			
			//			int s=row_h/2;
//			int m=row_h%2;
//			if(s>=1&&m>0)
//				end_record=end_record-1;
    	}
        return end_record;
	}
	 /**
     * 通过一个员工编号得到该员工考勤期间的数据
     * coursedate 考勤月 如：2009-1
     * */
    public int getOneA0100Data(Document document,PdfWriter writer,PdfPTable table,String code,String kind,String a0100[],ArrayList datelist,float body_h,ArrayList item_list,Font font,int num,String coursedate,ArrayList kqq03list)throws GeneralException
    {
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);
    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    	String uplevel = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122); //部门展现层级
    	PdfPCell cell = null;
		Paragraph paragraph = null;
    	StringBuffer column= new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	for(int i=0;i<fielditemlist.size();i++)
    	{
   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	     /*if("N".equals(fielditem.getItemtype()))
   	     {*/
   	    	if(!"i9999".equals(fielditem.getItemid()))
   	    	{
   	    		column.append(""+fielditem.getItemid()+",");  
   	    		columnlist.add(fielditem.getItemid());
   	    	}
   		  //}				
   	    }
    	CommonData start_vo = (CommonData)datelist.get(0);		
    	String start_date=start_vo.getDataName();
    	CommonData end_vo = (CommonData)datelist.get(datelist.size()-1);
    	String end_date=end_vo.getDataName();
    	String sql_one_a0100=KqReportInit.selcet_kq_one_emp(a0100[2],a0100[0],start_date,end_date,code,kind,column.toString());
    	ContentDAO dao = new ContentDAO(this.conn);       
        //序号
    	paragraph=new Paragraph(num+"",font);
		cell = new PdfPCell(paragraph);
		String[]ltrb= getLtrb("6");
		cell=excecute(cell,7,ltrb);
		cell.setFixedHeight(body_h);				//设置列最大高度
		cell.setMinimumHeight(body_h);			
		cell.setColspan(1);
		table.addCell(cell);
		//人员库
//		String dbname=AdminCode.getCode("@@",a0100[2])!=null?AdminCode.getCode("@@",a0100[2]).getCodename():"";
//		paragraph=new Paragraph(dbname,font);
//		cell = new PdfPCell(paragraph);
//		ltrb= getLtrb("6");
//		cell=excecute(cell,7,ltrb);
//		cell.setFixedHeight(body_h);				//设置列最大高度
//		cell.setMinimumHeight(body_h);			
//		cell.setColspan(1);
//		table.addCell(cell);
		//部门
		String dd=AdminCode.getCode("UM",a0100[4])!=null?AdminCode.getCode("UM",a0100[4],Integer.parseInt(uplevel)).getCodename():"";
		paragraph=new Paragraph(dd,font);
		cell = new PdfPCell(paragraph);
		ltrb= getLtrb("6");
		cell=excecute(cell,7,ltrb);
		cell.setFixedHeight(body_h);				//设置列最大高度
		cell.setMinimumHeight(body_h);			
		cell.setColspan(1);
		table.addCell(cell);
		//岗位
		KqParameter para = new KqParameter();
    	if(!"1".equals(para.getKq_orgView_post())){
			String e01=AdminCode.getCode("@K",a0100[5])!=null?AdminCode.getCode("@K",a0100[5]).getCodename():"";
			paragraph=new Paragraph(e01,font);
			cell = new PdfPCell(paragraph);
			ltrb= getLtrb("6");
			cell=excecute(cell,7,ltrb);
			cell.setFixedHeight(body_h);				//设置列最大高度
			cell.setMinimumHeight(body_h);			
			cell.setColspan(1);
			table.addCell(cell);
    	}
        //姓名
		paragraph=new Paragraph(a0100[1],font);
		cell = new PdfPCell(paragraph);
		ltrb= getLtrb("6");
		cell=excecute(cell,7,ltrb);
		cell.setFixedHeight(body_h);				//设置列最大高度
		cell.setMinimumHeight(body_h);			
		cell.setColspan(1);
		table.addCell(cell);
        //工号
		if(this.cardno!=null&&this.cardno.length()>0) {
            paragraph=new Paragraph(a0100[3],font);
        } else {
            paragraph=new Paragraph(a0100[0],font);
        }
		cell = new PdfPCell(paragraph);
		ltrb= getLtrb("6");
		cell=excecute(cell,7,ltrb);
		cell.setFixedHeight(body_h);				//设置列最大高度
		cell.setMinimumHeight(body_h);			
		cell.setColspan(1);
		table.addCell(cell);
        int row_h=1;
        RowSet rowSet=null;
        try{
          
          rowSet = dao.search(sql_one_a0100);
          HashMap kq_item_map = new HashMap();
          HashMap kq_item_all = querryKq_item();
          while(rowSet.next())
          {
        	 String q03z0=rowSet.getString("q03z0").trim(); 
        	 int num_r=0;
        	 paragraph=new Paragraph();
        	 double q03z2=0;//实出勤    
        	 String[] q03z2_item=null;
        	 for(int i=0;i<=fielditemlist.size();i++)
          	 {
        		if(i<fielditemlist.size())
        		{
        			FieldItem fielditem=(FieldItem)fielditemlist.get(i);
             	    if("N".equals(fielditem.getItemtype())&&!"i9999".equals(fielditem.getItemid()))
             	    {
               		   if("q03z1".equalsIgnoreCase(fielditem.getItemid()))
               		   {
            			  continue;
            		   }
            		   else
            		   {
            			 String value=rowSet.getString(fielditem.getItemid());
            			 if(value==null||value.length()<=0){
            				 value="0";
            			 }
            			 if(fielditem.getItemdesc().indexOf("出勤")!=-1||fielditem.getItemdesc().indexOf("出勤率")!=-1)
            			 {
            				 q03z2=Double.parseDouble(value);                		 
                			 if(q03z2!=0)
                    		  {
                				  //System.out.println("$$$$$$$$$$$"+fielditem.getItemid().toString());
                				  q03z2_item =KqReportInit.getKq_Item(fielditem.getItemid().toString(),item_list);    
                    			 //Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),kq_item[1]);
                    			  //paragraph=new Paragraph(kq_item[0],font_1);                			  
                    			  //paragraph.add(new Chunk("*",red));
                    			  
                    			  
                    			  //table.addCell(cell);
                    			  continue;
                    		  }
            			 }else{
            				 double dv=Double.parseDouble(value);                		 
                			 if(dv!=0)
                    		  {
                				  //System.out.println("$$$$$$$$$$$"+fielditem.getItemid().toString());
                    			  String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid().toString(),item_list);    
                    			  Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),kq_item[1]);
                    			  //paragraph=new Paragraph(kq_item[0],font_1);                			  
                    			  //paragraph.add(new Chunk("*",red));
                    			  if (kq_item[0] != null && kq_item[0].trim().length() > 0) {
                    				  paragraph.add(new Paragraph(kq_item[0],font_1));//多个
                    			  }
                    			  if (kq_item_all.containsKey(fielditem.getItemid().toLowerCase())) {
                    				  num_r++;
                    			  }
                    			  //table.addCell(cell);
                    			  continue;
                    		  }
            			 }
                		 
                	   }
            		}
             	    if(!"N".equals(fielditem.getItemtype())&&!"q03z0".equals(fielditem.getItemid())&&!"nbase".equals(fielditem.getItemid())&&!"a0100".equals(fielditem.getItemid())
              	    		&&!"b0110".equals(fielditem.getItemid())&&!"e0122".equals(fielditem.getItemid())&&!"e01a1".equals(fielditem.getItemid())&&!"q03z3".equals(fielditem.getItemid())
              	    		&&!"q03z5".equals(fielditem.getItemid())&&!"state".equals(fielditem.getItemid())&&!"a0101".equals(fielditem.getItemid()))
            	    {
            	    	String  sr=rowSet.getString(fielditem.getItemid());
            	    	//System.out.println(sr);
            	    	if(sr!=null&&sr.length()>0&&!"0".equals(sr))
            	    	{
            	    		String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);  
            	    		Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),kq_item[1]);
            	    		ArrayList one_list= new ArrayList();
            	    		if (kq_item[0] != null && kq_item[0].trim().length() > 0) {
            	    			paragraph.add(new Paragraph(kq_item[0],font_1));//多个
            	    		}
//               			    num_r++;
          			        
            	    	}
            	    }
        		}else if(i==fielditemlist.size()&&num_r>0)
        		{
        			cell = new PdfPCell(paragraph);                   			  
      			    ltrb= getLtrb("6");
      			    cell=excecute(cell,7,ltrb);
      			    cell.setFixedHeight(body_h);				//设置列最大高度
      			    cell.setMinimumHeight(body_h);			
      			    cell.setColspan(1);
      			    ArrayList list= new ArrayList();
      			    list.add(cell);
      			    list.add(new Double(1));
      			    kq_item_map.put(q03z0,list);
      			    break;
        		}else if(i==fielditemlist.size()&&q03z2>0)//实出勤
        		{
        			Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),q03z2_item[1]);
        			paragraph=new Paragraph(q03z2_item[0],font_1);
        			cell = new PdfPCell(paragraph);                   			  
      			    ltrb= getLtrb("6");
      			    cell=excecute(cell,7,ltrb);
      			    cell.setFixedHeight(body_h);				//设置列最大高度
      			    cell.setMinimumHeight(body_h);			
      			    cell.setColspan(1);
      			    ArrayList list= new ArrayList();
      			    list.add(cell);
      			    list.add(new Double(1));
      			    kq_item_map.put(q03z0,list);
        		
        		}
        		
//        		else if(i==fielditemlist.size()&&num_r==0)
//       		    {
//       		    	 String onduty_value=rowSet.getString("q03z1");
//       		    	 
//       		    	if(onduty_value==null||onduty_value.length()<=0)
//      		    	 {
//      		    		onduty_value="0";
//      			     }
//       		    	double onduty_dv=Double.parseDouble(onduty_value);
//        			 if(onduty_dv!=0)
//        			 {
//        				 String[] kq_item =KqReportInit.getKq_Item("q03z1",item_list);        			         			  
//        				 Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),kq_item[1]);
//           			     paragraph=new Paragraph(kq_item[0],font_1);
//           			     cell = new PdfPCell(paragraph);
//           			     ltrb= getLtrb("6");
//           			     cell=excecute(cell,7,ltrb);
//           			     cell.setFixedHeight(body_h);				//设置列最大高度
//           			     cell.setMinimumHeight(body_h);			
//           			     cell.setColspan(1);
//           			     ArrayList list= new ArrayList();
//        			     list.add(cell);
//        			     list.add(new Double(onduty_dv));
//        			     kq_item_map.put(q03z0,list);
//           			     //table.addCell(cell);
//           			     break; 
//        			 }else
//        			 {
//        				 String[] kq_item =KqReportInit.getKq_Item("q03z1",item_list);        			         			  
//        				 Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),kq_item[1]);
//           			     paragraph=new Paragraph("   ",font_1);
//           			     cell = new PdfPCell(paragraph);
//           			     ltrb= getLtrb("6");
//           			     cell=excecute(cell,7,ltrb);
//           			     cell.setFixedHeight(body_h);				//设置列最大高度
//           			     cell.setMinimumHeight(body_h);			
//           			     cell.setColspan(1);
//           			     ArrayList list= new ArrayList();
//     			         list.add(cell);
//     			         list.add(new Double(onduty_dv));
//     			         kq_item_map.put(q03z0,list);
//           			     //table.addCell(cell);
//           			     break; 
//        			 }
//       		    }  
        		if(num_r>0)
        		{
        			if(row_h<num_r) {
                        row_h=num_r;
                    }
        		}
        	 }     	      
          }
           for(int s=0;s<datelist.size();s++)
           {
         	  CommonData cur_vo = (CommonData)datelist.get(s);		
              String cur_date=cur_vo.getDataName().trim(); 
              ArrayList kq_item_list=(ArrayList)kq_item_map.get(cur_date);
              
              if(kq_item_list!=null&&kq_item_list.size()>0)
              {
           	     Double dv=(Double)kq_item_list.get(1);
           	     double value=dv.doubleValue();
           	     if(value!=0)
           	     {
           	    	table.addCell((PdfPCell)kq_item_list.get(0));
           	     }else
           	     {
           	    	 Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");
       			     paragraph=new Paragraph("   ",font_1);
       			     cell = new PdfPCell(paragraph);
       			     ltrb= getLtrb("6");
       			     cell=excecute(cell,7,ltrb);
       			     cell.setFixedHeight(body_h);				//设置列最大高度
       			     cell.setMinimumHeight(body_h);			
       			     cell.setColspan(1);
       			     table.addCell(cell);
           	     }   	  
   			  }else
              {
   				 Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");
  			     paragraph=new Paragraph("   ",font_1);
  			     cell = new PdfPCell(paragraph);
  			     ltrb= getLtrb("6");
  			     cell=excecute(cell,7,ltrb);
  			     cell.setFixedHeight(body_h);				//设置列最大高度
  			     cell.setMinimumHeight(body_h);			
  			     cell.setColspan(1);
  			     table.addCell(cell);
              }
          }
           boolean noSelected = false;
   		
   		if ((!"#dept[1]".equalsIgnoreCase(parsevo.getBody_dept())) 
   	    		&& (!"#pos[1]".equalsIgnoreCase(parsevo.getBody_pos())) 
   	    		&& (!"#gh[1]".equalsIgnoreCase(parsevo.getBody_gh())) 
   	    		&& (!"#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu())) 
   	    		&& (!"#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()))) {
   	    	noSelected = true;
   	    	
   	    }
   	    
   	    if(this.kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || noSelected))
   	    {
        	   getOneA0100Value(code,kind,a0100,coursedate,kqq03list,body_h,table,font);
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }    
        return row_h;
    }
    public void getOneA0100Value(String code,String kind,String a0100[],String coursedate,ArrayList kqq03list,float body_h,PdfPTable table,Font font)
    {
//    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
//				Constant.USED_FIELD_SET);
//    	ArrayList kqq03list=savekqq03list(this.conn,fielditemlist);
    	StringBuffer column= new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	PdfPCell cell = null;
		Paragraph paragraph = null;
		String[]ltrb=null;
    	for(int i=0;i<kqq03list.size();i++)
    	{
    		//导出PDF，是根据页面宽度而决定的，大于这个宽度不展现
    		if(i<=13)
    		{
    			FieldItem fielditem=(FieldItem)kqq03list.get(i);
        		if(!"i9999".equals(fielditem.getItemid()))
       	    	{
       	    		column.append(""+fielditem.getItemid()+","); 
       	    		ArrayList lsit=new ArrayList();
       	    		lsit.add(fielditem.getItemid());
       	    		lsit.add(fielditem.getItemtype());
       	    		lsit.add(fielditem.getCodesetid()); //判断是否是代码类型 0 就不是
//       	    		columnlist.add(fielditem.getItemid());
       	    		columnlist.add(lsit);
       	    	}
    		}
    	}
    	StringBuffer one_date=new StringBuffer(); 
    	RowSet rowSet=null;
    	String itemid="";
    	try
    	{
    		int l=column.toString().length()-1;
    	 	String columnstr=column.toString().substring(0,l);
         	for(int t=0;t<columnlist.size();t++)
         	{
         		StringBuffer sql_on_a0100=new StringBuffer();
//         		String itd=(String)columnlist.get(t);
         		ArrayList listd=(ArrayList)columnlist.get(t);
         		String itd="";
         		String itdtype="";
         		String codesetid="";  //判断是否是代码类型 0 就不是
         		for(int p=0;p<listd.size();p++)
         		{
         				itd=(String)listd.get(0);
         				itdtype=(String)listd.get(1);
         				codesetid=(String)listd.get(2);
         				break;
         		}
         		sql_on_a0100.append("select "+itd+" as one from Q05_arc where Q03Z0='"+coursedate+"' and ");
        		sql_on_a0100.append("nbase='"+a0100[2]+"' and A0100='"+a0100[0]+"'");
        		rowSet=dao.search(sql_on_a0100.toString());
        		
        		StringBuffer font_str=new StringBuffer();
        		if(rowSet.next())
        		{
        			itemid = rowSet.getString("one");
//        			if(itemid==null&&(itemid.equalsIgnoreCase("0E-8")||itemid.equals("")))
        			if(itemid != null)
        			{
        				if("".equals(itemid)|| "0E-8".equalsIgnoreCase(itemid)|| "0".equalsIgnoreCase(itemid))
        				{
        					itemid="";
        					Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");
             			    paragraph=new Paragraph("   ",font_1);
             			    cell = new PdfPCell(paragraph);
             			    ltrb= getLtrb("6");
             			    cell=excecute(cell,7,ltrb);
             			    cell.setFixedHeight(body_h);				//设置列最大高度
             			    cell.setMinimumHeight(body_h);			
             			    cell.setColspan(1);
             			    table.addCell(cell);
        				}else
        				{
//        					int id=(int)(Float.parseFloat(itemid));
//            				itemid=Integer.toString(id);
//            				table.addCell(itemid);
        					if(!"N".equalsIgnoreCase(itdtype))
        					{
        						if("A".equalsIgnoreCase(itdtype))
        						{
        							if(!"0".equalsIgnoreCase(codesetid))
        							{
        								itemid = AdminCode.getCodeName(codesetid, itemid);
        								paragraph=new Paragraph(itemid,font);
        								cell = new PdfPCell(paragraph);
        								ltrb= getLtrb("6");
        								cell=excecute(cell,7,ltrb);
        								cell.setFixedHeight(body_h);				//设置列最大高度
        								cell.setMinimumHeight(body_h);			
        								cell.setColspan(1);
        								table.addCell(cell);
        							}else
        							{
        								table.addCell(itemid);
        							}
        						}else
        						{
        							table.addCell(itemid);
        						}
        						
        					}else
        					{
        						int num = 2;
        						if(itemid.indexOf(".")!=-1) {
								for (int k = 0; k < num; k++) {
									itemid += "0";
								}
								itemid = PubFunc.round(itemid,num);
								table.addCell(itemid);
        						} else{								 
								for (int k = 0; k < num; k++) {
									if (k == 0) {
										itemid += "." + "0";
									} else {
										itemid += "0";
									}
								}
								itemid = PubFunc.round(itemid,num);
								table.addCell(itemid);
        						}
        					}
        				}
        			}else
        			{
        				itemid="";
        				Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");
         			    paragraph=new Paragraph("   ",font_1);
         			    cell = new PdfPCell(paragraph);
         			    ltrb= getLtrb("6");
         			    cell=excecute(cell,7,ltrb);
         			    cell.setFixedHeight(body_h);				//设置列最大高度
         			    cell.setMinimumHeight(body_h);			
         			    cell.setColspan(1);
         			    table.addCell(cell);
        			}
        		}else
        		{
        			Font font_1=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");
     			    paragraph=new Paragraph("   ",font_1);
     			    cell = new PdfPCell(paragraph);
     			    ltrb= getLtrb("6");
     			    cell=excecute(cell,7,ltrb);
     			    cell.setFixedHeight(body_h);				//设置列最大高度
     			    cell.setMinimumHeight(body_h);			
     			    cell.setColspan(1);
     			    table.addCell(cell);
        		}
         	}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
	     {
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     } 
    }
	public void getTableBodyHead(Document document,PdfWriter writer,PdfPTable table,ArrayList datelist,ArrayList kqq03list)
	{
		Font font=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");  //生成字体样式
		float body_h=Float.parseFloat(parsevo.getBody_fz())+13;
		PdfPCell cell = null;
		Paragraph paragraph = null;		
		
		paragraph=new Paragraph(" 序号 ",font);
		cell = new PdfPCell(paragraph);
		String[]ltrb= getLtrb("6");
		cell=excecute(cell,7,ltrb);
		cell.setFixedHeight(body_h);				//设置列最大高度
		cell.setMinimumHeight(body_h);			
		cell.setColspan(1);
		table.addCell(cell);
		
//		paragraph=new Paragraph(" 人员库 ",font);
//		cell = new PdfPCell(paragraph);
//		ltrb= getLtrb("7");
//		cell=excecute(cell,7,ltrb);
//		cell.setFixedHeight(body_h);				//设置列最大高度
//		cell.setMinimumHeight(body_h);
//		cell.setColspan(1);
//		table.addCell(cell);
		
		paragraph=new Paragraph(" 部门 ",font);
		cell = new PdfPCell(paragraph);
		ltrb= getLtrb("7");
		cell=excecute(cell,7,ltrb);
		cell.setFixedHeight(body_h);				//设置列最大高度
		cell.setMinimumHeight(body_h);			
		cell.setColspan(1);
		table.addCell(cell);
		
		KqParameter para = new KqParameter();
    	if(!"1".equals(para.getKq_orgView_post())){
			paragraph=new Paragraph(" 岗位 ",font);
			cell = new PdfPCell(paragraph);
			ltrb= getLtrb("7");
			cell=excecute(cell,7,ltrb);
			cell.setFixedHeight(body_h);				//设置列最大高度
			cell.setMinimumHeight(body_h);			
			cell.setColspan(1);
			table.addCell(cell);
    	}
		
		paragraph=new Paragraph(" 姓名 ",font);
		cell = new PdfPCell(paragraph);
		ltrb= getLtrb("7");
		cell=excecute(cell,7,ltrb);
		cell.setFixedHeight(body_h);				//设置列最大高度
		cell.setMinimumHeight(body_h);			
		cell.setColspan(1);
		table.addCell(cell);
		
		paragraph=new Paragraph(" 工号 ",font);
		cell = new PdfPCell(paragraph);
		ltrb= getLtrb("7");
		cell=excecute(cell,7,ltrb);
		cell.setFixedHeight(body_h);				//设置列最大高度
		cell.setMinimumHeight(body_h);			
		cell.setColspan(1);
		table.addCell(cell);
		
		for(int i=0;i<datelist.size();i++)
		{
			CommonData vo = (CommonData)datelist.get(i);
			String value = vo.getDataValue();
			paragraph=new Paragraph(value,font);
			cell = new PdfPCell(paragraph);
			cell=excecute(cell,7,ltrb);
			cell.setFixedHeight(body_h);				//设置列最大高度
			cell.setMinimumHeight(body_h);			
			cell.setColspan(1);
			table.addCell(cell);
		}
		boolean noSelected = false;
		if ((!"#dept[1]".equalsIgnoreCase(parsevo.getBody_dept())) 
	    		&& (!"#pos[1]".equalsIgnoreCase(parsevo.getBody_pos())) 
	    		&& (!"#gh[1]".equalsIgnoreCase(parsevo.getBody_gh())) 
	    		&& (!"#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu())) 
	    		&& (!"#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()))) {
	    	noSelected = true;
	    	
	    }
		if (kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || noSelected)) {
			for(int p=0;p<kqq03list.size();p++)
			{
				//导出PDF值是根据宽度决定的；大于这个宽度就不展现
				if(p<=13)
				{
					FieldItem fielditem=(FieldItem)kqq03list.get(p);
					String value=fielditem.getItemdesc();
					paragraph=new Paragraph(value,font);
					cell = new PdfPCell(paragraph);
					cell=excecute(cell,7,ltrb);
					cell.setFixedHeight(body_h);				//设置列最大高度
					cell.setMinimumHeight(body_h);			
					cell.setColspan(1);
					table.addCell(cell);
				}
			}
		}
	}
	/**
	 * 建立表尾信息
	 * ***/
	 /**
     * 得到表尾数据
     * **/
    public void getTileHtml(Document document,PdfPTable table,String code,String kind,int curpage,int sum_page ,ArrayList item_list,int maxColumn)throws GeneralException
    {
    	
    	PdfPCell cell = null;
		Paragraph paragraph = null;
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);    	 
    	
    	Font font_b=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),"");  //生成字体样式
    	
    	//StringBuffer note_str= new StringBuffer();
    	StringBuffer note_len_str= new StringBuffer();
    	String note_str="备注：1.";
    	note_len_str.append("备注：1.");
    	paragraph=new Paragraph(note_str,font_b);
    	for(int i=0;i<fielditemlist.size();i++)
    	{
   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	     /*if("N".equals(fielditem.getItemtype()))
   	     {*/
   	    	if(!"i9999".equals(fielditem.getItemid()))
   	    	{
   	    		String kq_item[]=KqReportInit.getKq_Item(fielditem.getItemid(),item_list); 
   	    		if(kq_item[0]!=null&&kq_item[0].length()>0)
   	    		{
   	    			paragraph.add(fielditem.getItemdesc()+"(");  
   	    			Font font_tag=getFont(parsevo.getBody_fn(),parsevo.getBody_fb(),parsevo.getBody_fi(),parsevo.getBody_fu(),parsevo.getBody_fz(),kq_item[1]);  //生成字体样式
   	    			paragraph.add(new Chunk(kq_item[0],font_tag));
   	    			paragraph.add(")");   	   	    		
   	   	    	    note_len_str.append(fielditem.getItemdesc()+"("+kq_item[0]+")");
   	   	    	}  	    		
   	    	}
   		  //}				
   	    }   
    	int strlen=note_len_str.toString().length()*Integer.parseInt(parsevo.getBody_fz());    	
    	int note_h_1=Integer.parseInt(parsevo.getBody_fz())+6; 
    	int numrow_1=getNumRow(strlen);    	
    	if(numrow_1!=0)
    	{
    		note_h_1=note_h_1*numrow_1;	
    	}  
    	String[]ltrb= getLtrb("6");
    	cell = new PdfPCell(paragraph);
		cell=excecute(cell,6,ltrb);
		cell.setFixedHeight(note_h_1);				//设置列最大高度
		cell.setMinimumHeight(note_h_1);			
		cell.setColspan(maxColumn);
		table.addCell(cell);
		
		if(parsevo.getTile_fw()!=null&&parsevo.getTile_fw().length()>0)
    	{
    		String tile_fw="     2."+parsevo.getTile_fw();
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
//	        	   String d_str="  制作日期: "+PubFunc.getStringDate("yyyy.MM.dd");//原来根据系统时间生成
	        	   String d_str=" 制作日期: "+this.sjelement;
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
//	        	   String t_str="  时间: "+PubFunc.getStringDate("HH:mm:ss");  //原来根据系统
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
                  borders.setBorderWidthBottom(0f);
              }
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
			if(fn==null||fn.length()<=0)
			{
				bfComic = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   //解决中文问题
			}
		    else if(FontFamilyType.getFontFamilyTTF(fn)==null)
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
		}else if("2".equals(v))//"RecordRow_self_l";
		{
			ltrb[0]="0";
		    ltrb[1]="1";
			ltrb[2]="1";
			ltrb[3]="1";
		}else if("3".equals(v))//"RecordRow_self_t";
		{
			ltrb[0]="1";
		    ltrb[1]="0";
			ltrb[2]="1";
			ltrb[3]="1";
		}else if("4".equals(v))//"RecordRow_self_r";
		{
			ltrb[0]="1";
		    ltrb[1]="1";
			ltrb[2]="0";
			ltrb[3]="1";
		}else if("5".equals(v))//"RecordRow_self_b";
		{
			ltrb[0]="1";
		    ltrb[1]="1";
			ltrb[2]="1";
			ltrb[3]="0";
		}else if("6".equals(v))//"RecordRow_self_l_t";
		{
			ltrb[0]="1";
		    ltrb[1]="0";
			ltrb[2]="0";
			ltrb[3]="1";
		}else if("7".equals(v))//"RecordRow_self_r_t";
		{
			ltrb[0]="0";
		    ltrb[1]="1";
			ltrb[2]="0";
			ltrb[3]="1";
		}
		return ltrb;
	}
	/****************参数的运算&定义********************/
	 /**
     * 以用高度
     * **/
    public float getIsUseHieght(ArrayList item_list)throws GeneralException
    {
       	
    	float height=Float.parseFloat(getPxFormMm_f(parsevo.getTop()))+Float.parseFloat(getPxFormMm_f(parsevo.getBottom()))+Float.parseFloat(getPxFormMm_f(parsevo.getTitle_h()));
    	height=height+Float.parseFloat(getPxFormMm_f(parsevo.getHead_h()));
    	height=height+Float.parseFloat(parsevo.getBody_fz())+13;
    	    	
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);
    	//计算备注1
    	StringBuffer note_len_str= new StringBuffer();    	
    	note_len_str.append("备注：1.");
    	for(int i=0;i<fielditemlist.size();i++)
    	{
   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	     if("N".equals(fielditem.getItemtype()))
   	     {
   	    	if(!"i9999".equals(fielditem.getItemid()))
   	    	{
   	    		String kq_item[]=KqReportInit.getKq_Item(fielditem.getItemid(),item_list); 
   	    		if(kq_item[0]!=null&&kq_item[0].length()>0)
   	    		{
   	    			note_len_str.append(fielditem.getItemdesc()+"("+kq_item[0]+")");
   	   	    	}  	    		
   	    	}
   		  }				
   	    }   
    	int strlen=note_len_str.toString().length()*Integer.parseInt(parsevo.getBody_fz());      	   	
    	int numrow_tile_1=getNumRow(strlen);  	
    	
    	if(numrow_tile_1!=0)
    	{
    		height=height+(Float.parseFloat(parsevo.getBody_fz())+6)*numrow_tile_1;	
    	}else
    	{
    		height=height+(Float.parseFloat(parsevo.getBody_fz())+6);
    	}
    	
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
    
    public float getSpareHieght(ArrayList item_list)throws GeneralException
    {
       	float spare_hieght=0;
       	float height=getIsUseHieght(item_list);
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
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	/**
     * 查询考勤期间的所有指标
     * @return
     */
    private HashMap querryKq_item () {
    	HashMap map = new HashMap();
    	String sql = "select item_symbol,fielditemid from kq_item";
    	ContentDAO dao = new ContentDAO(this.conn);
    	ResultSet rs = null;
    	try {
			rs = dao.search(sql);
			while (rs.next()) {
				String key = rs.getString("fielditemid");
				String value = rs.getString("item_symbol");
				if (key != null) {
					map.put(key.toLowerCase(), value);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
    	return map;
    }
	public String getDbty() {
		return dbty;
	}
	public void setDbty(String dbty) {
		this.dbty = dbty;
	}
}
