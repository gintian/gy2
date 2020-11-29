package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ResumeBrowsePDF {
	private float t_margin=0;
	private float b_margin=0;
	private float l_margin=0;
	private float r_margin=0;
	private float page_width=0;
	private float page_height=0;
	private Font font=null;
	private String operate="0";  // 0:默认A4  1：自定义
	private Connection conn = null;
	
	private HashMap resumeBrowseSetMap=new HashMap();  //应聘者各子集里的信息集合
	private HashMap setShowFieldMap=new HashMap();     //子集显示 列 map
	private ArrayList remarkList=new ArrayList();
	private ArrayList zpPosList=new ArrayList();
	private ArrayList fieldSetList=new ArrayList();
	private String a0100="";
	private String dbpre="";
	private HashMap map=new HashMap();
	private String persontype="";
	public ResumeBrowsePDF(Connection con,String operate)
	{
		this.conn=con;
		this.operate=operate;
	}
	
	
	
	public void init()
	{
		String fontfamilyname=ResourceFactory.getProperty("hmuster.label.fontSt");
        String fontEffect="0";
        int    fontSize=5;
        this.font = FontFamilyType.getFont(fontfamilyname,fontEffect,fontSize); // 生成字体样式
		
		
	}
	
	
//	get Document
	public Document getDocument()
	{
		Document document = null;
		if("1".equals(operate)) {
            document=new Document(new Rectangle( page_width,page_height),l_margin, r_margin,t_margin, b_margin);
        } else {
            document = new Document(PageSize.A4, 40,40,60,30);
        }
		return document;
	}
	
	public String createPdf(String a0100,String dbname)
	{
		this.a0100=a0100;
		this.dbpre=dbname;
		Document document =getDocument();	
		init();
		String    pdfName="";
		PdfWriter writer=null;
		try {
			//pdfName = userName + "_" + PubFunc.getStrg() + "_" + tablename + ".pdf";
			pdfName="resume.pdf";  //固定文件名，免得临时文件夹中产生的文件过多
		    writer = PdfWriter.getInstance(document,new FileOutputStream(System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator")+ pdfName));
			document.open();
			
			PdfPTable  datatable = new PdfPTable(4); 
	        int headerwidths[] = {20,30,20,30};
	        datatable.setWidths(headerwidths);
	        datatable.setWidthPercentage(100);
   
	        executeBasicInfo(datatable);
	        if(fieldSetList.size()==1){
	        	PdfPCell cell = new PdfPCell(new Paragraph());
	    		cell.setColspan(4);    
	    		cell.setBorderWidthTop(0);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				datatable.addCell(cell);
	        }
	        executeSubInfo(datatable);
	        
	    	document.add(datatable);
	    	if(this.remarkList.size()>0)
	    	{
	    		PdfPTable remark=executeRemark();	  
	    		document.add(remark);
	    	}
	    	if(this.persontype!=null&& "0".equalsIgnoreCase(this.persontype)){
		    	if(this.zpPosList.size()>0)
		    	{
		    		PdfPTable ApplyedPos=executeApplyedPos();	  
		    		document.add(ApplyedPos);
		    	}
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally
        {
		    PubFunc.closeIoResource(document);
            PubFunc.closeIoResource(writer);
        }
		return pdfName;
	}
	
	
	
	public  PdfPTable executeApplyedPos()
	{
		PdfPTable  datatable = new PdfPTable(1);
		datatable.setWidthPercentage(100);
		PdfPCell cell = new PdfPCell(new Paragraph("  应聘岗位(专业)：",font));
		cell.setColspan(3);                   // 列合并
		cell.setMinimumHeight(20f);
		cell.setBackgroundColor(new Color(220,220,220));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		datatable.addCell(cell); 
		
		for(int i=0;i<this.zpPosList.size();i++)
		{
			CommonData data1 = (CommonData) this.zpPosList.get(i);

			cell = new PdfPCell(new Paragraph("  "+data1.getDataName(), font));			
			cell.setMinimumHeight(20f);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			datatable.addCell(cell); 
		}
		
		return datatable;
	}
	
	
	public  PdfPTable executeRemark()
	{
		PdfPTable  datatable = new PdfPTable(6);
		try
		{
		
		int headerwidths[] = {5,25,10,10,10,40};
        datatable.setWidths(headerwidths);
		
		datatable.setWidthPercentage(100);
		PdfPCell cell = new PdfPCell(new Paragraph("  评  语：",font));
		cell.setColspan(6);                   // 列合并
		cell.setMinimumHeight(20f);
		cell.setBackgroundColor(new Color(220,220,220));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		datatable.addCell(cell); 
		
		datatable.addCell(getCell("序号",1,1,1,1,Element.ALIGN_CENTER,0,15)); 
		datatable.addCell(getCell("标题",1,1,1,1,Element.ALIGN_CENTER,0,15)); 
		datatable.addCell(getCell("日期",1,1,1,1,Element.ALIGN_CENTER,0,15)); 
		datatable.addCell(getCell("评审人",1,1,1,1,Element.ALIGN_CENTER,0,15)); 
		datatable.addCell(getCell("评级",1,1,1,1,Element.ALIGN_CENTER,0,15)); 
		datatable.addCell(getCell("内容",1,1,1,1,Element.ALIGN_CENTER,0,15)); 
		
		
		for(int i=0;i<this.remarkList.size();i++)
		{
			LazyDynaBean data1 = (LazyDynaBean) this.remarkList.get(i);
			datatable.addCell(getCell(String.valueOf(i+1),1,1,1,1,Element.ALIGN_LEFT,0,15)); 
			datatable.addCell(getCell((String)data1.get("title"),1,1,1,1,Element.ALIGN_LEFT,0,15)); 
			datatable.addCell(getCell((String)data1.get("date"),1,1,1,1,Element.ALIGN_LEFT,0,15)); 
			datatable.addCell(getCell((String)data1.get("user"),1,1,1,1,Element.ALIGN_LEFT,0,15)); 
			datatable.addCell(getCell((String)data1.get("level"),1,1,1,1,Element.ALIGN_LEFT,0,15)); 
			datatable.addCell(getCell((String)data1.get("content"),1,1,1,1,Element.ALIGN_LEFT,0,15)); 
		}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return datatable;
		
	}
	
	
	
	//生成子集信息
	public void executeSubInfo(PdfPTable  datatable)
	{
		for(int i=1;i<fieldSetList.size();i++)
      	{
      		LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
			String setid=(String)abean.get("fieldSetId");
      		String setdesc=(String)abean.get("fieldSetDesc");
      		
      		ArrayList dataList=(ArrayList)resumeBrowseSetMap.get(setid.toLowerCase());
      		if(dataList==null) {
                continue;
            }
      		ArrayList showFieldList=(ArrayList)setShowFieldMap.get(setid.toLowerCase());
      		
      		PdfPCell cell = new PdfPCell(new Paragraph("  "+setdesc,this.font));
    		cell.setColspan(4);                   //列合并   
    		cell.setMinimumHeight(20f);
			cell.setBackgroundColor(new Color(220, 220, 220));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			datatable.addCell(cell);
			String answertset="";
			if(map!=null&&map.get("answerSet")!=null) {
                answertset=(String)map.get("answerSet");
            }
			for (int n = 0; n < dataList.size(); n++) {
				 if(n!=0)
               	 {
					 datatable.addCell(getCell("",1,0,0,0,Element.ALIGN_RIGHT,0,15));
			      	 datatable.addCell(getCell("",0,1,0,0,Element.ALIGN_LEFT,3,15));
               	 }
				LazyDynaBean a_bean = (LazyDynaBean) dataList.get(n);
				
				String tem = "";
				for (int j = 0; j < showFieldList.size(); j++) {
				
					LazyDynaBean aa_bean = (LazyDynaBean) showFieldList.get(j);
					String itemid = (String) aa_bean.get("itemid");
					String itemtype = (String) aa_bean.get("itemtype");
					String itemdesc = (String) aa_bean.get("itemdesc");
					String value = (String) a_bean.get(itemid);
					String itemmemo=(String)aa_bean.get("itemmemo");
					value=value.replaceAll("<br>","\\\n");
					itemdesc=itemdesc.replaceAll("&nbsp;"," ");
					itemmemo = itemmemo.replace("&nbsp;"," ");
					value=value.replaceAll("&nbsp;"," ");
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					{
						value=value.replaceAll("&#8226;","·");
					}
					if(i==(fieldSetList.size()-1)&&n==(dataList.size()-1)&&j==(showFieldList.size()-1))
					{
						if(answertset!=null&&answertset.length()!=0&&answertset.equalsIgnoreCase(setid)){
							tem = "\n";
							datatable.addCell(getCell(itemmemo+":",1,0,0,1,Element.ALIGN_RIGHT,2,15));
						}else{
							datatable.addCell(getCell(itemdesc+":",1,0,0,1,Element.ALIGN_RIGHT,0,15));
						}		
						datatable.addCell(getCell(tem+value,0,1,0,1,Element.ALIGN_LEFT,3,15));
					}
					else
					{
						if(answertset!=null&&answertset.length()!=0&&answertset.equalsIgnoreCase(setid)){
							tem = "\n";
							datatable.addCell(getCell(itemmemo+":",1,0,0,0,Element.ALIGN_RIGHT,2,15));
						}else{
							datatable.addCell(getCell(itemdesc+":",1,0,0,0,Element.ALIGN_RIGHT,0,15));
						}
							datatable.addCell(getCell(tem+value,0,1,0,0,Element.ALIGN_LEFT,3,15));
					}
				}
			}
      	}
	}
	
	
	// 生成基本信息
	public void executeBasicInfo(PdfPTable  datatable)
	{
		
		try
		{
			//从应聘简历子集中获取基本信息名称
		LazyDynaBean abean1=(LazyDynaBean)fieldSetList.get(0);
  		String setdesc=(String)abean1.get("fieldSetDesc");
		PdfPCell cell = new PdfPCell(new Paragraph(setdesc,font));
		cell.setColspan(4);                   // 列合并
		cell.setMinimumHeight(20f);
		cell.setBackgroundColor(new Color(220,220,220));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		datatable.addCell(cell); 		
		ArrayList a01InfoList=(ArrayList)resumeBrowseSetMap.get("a01");
		
		PdfPTable nested1 = new PdfPTable(2);
		int headerwidths[] = {40,60};
		nested1.setWidths(headerwidths);
		nested1.setWidthPercentage(100);
		
      	for(int i=0;i<5;i++)
      	{	
      		if(i>=a01InfoList.size()) {
                break;
            }
      		LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
      		String itemdesc=(String)abean.get("itemdesc");
      		String codesetid=(String)abean.get("codesetid");
      		String viewvalue=(String)abean.get("viewvalue");
      		if(itemdesc.length()==2) {
                itemdesc=itemdesc.charAt(0)+"   "+itemdesc.charAt(1);
            }
			String value=(String)abean.get("value");
      		if(!"0".equals(codesetid)) {
                value=viewvalue;
            }
      		
      		
      		 itemdesc=itemdesc.replaceAll("&nbsp;"," ");
      		 value=value.replaceAll("&nbsp;"," ");
      		 nested1.addCell(getCell(itemdesc+":",0,0,0,0,Element.ALIGN_RIGHT,0,20));
      		 nested1.addCell(getCell(value,0,0,0,0,Element.ALIGN_LEFT,0,20));    	
      	}
      	
        
        cell = new PdfPCell(new Paragraph(""));
		cell.setColspan(2);                   // 列合并
		cell.setBorderWidthBottom(0);
		cell.setBorderWidthRight(0);
		cell.setBorderWidthTop(0);
		cell.setMinimumHeight(20f);
		cell.addElement(nested1);
		datatable.addCell(cell);
		
		
		
		byte[] buf=createPhotoFile2(dbpre + "A00",this.a0100, "P");
		if(buf.length>0)
		{
			Image image = Image.getInstance(buf);	
			image.scaleAbsolute(65,90);
			cell = new PdfPCell(image, false);
		}
		else {
            cell=new PdfPCell(new Paragraph(""));
        }
		cell.setPadding(5);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT); // 水平居中
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // 垂直居中
		cell.setBorderWidthBottom(0);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthTop(0);
		cell.setColspan(2);                   // 列合并
		cell.setMinimumHeight(20f);
		datatable.addCell(cell);
        
      	for(int i=5;i<a01InfoList.size();i++)
      	{
      		LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
      		String itemdesc=(String)abean.get("itemdesc");
      		String codesetid=(String)abean.get("codesetid");
      		String viewvalue=(String)abean.get("viewvalue");
      		if(itemdesc.length()==2) {
                itemdesc=itemdesc.charAt(0)+"   "+itemdesc.charAt(1);
            }
			String value=(String)abean.get("value");
      		if(!"0".equals(codesetid)) {
                value=viewvalue;
            }
      		
      		
      		 itemdesc=itemdesc.replaceAll("&nbsp;"," ");
      		 value=value.replaceAll("&nbsp;"," ");
      		datatable.addCell(getCell(itemdesc+":",1,0,0,0,Element.ALIGN_RIGHT,0,20));
      		datatable.addCell(getCell(value,0,0,0,0,Element.ALIGN_LEFT,0,20));
      	
            
            String itemdesc2="";
            String value2="";
            i++;
            if(i<a01InfoList.size())
            {
                LazyDynaBean abean2=(LazyDynaBean)a01InfoList.get(i);
                String codesetid2=(String)abean2.get("codesetid");
	      		itemdesc2=(String)abean2.get("itemdesc");
	      		String viewvalue2=(String)abean2.get("viewvalue");
	      		if(itemdesc2.length()==2) {
                    itemdesc2=itemdesc2.charAt(0)+"&nbsp;&nbsp;&nbsp;"+itemdesc2.charAt(1);
                }
				
	      		itemdesc2+=":";
				value2=(String)abean2.get("value");
				if(!"0".equals(codesetid2)) {
                    value2=viewvalue2;
                }
				
            }
            itemdesc2=itemdesc2.replaceAll("&nbsp;"," ");
     		value2=value2.replaceAll("&nbsp;"," ");
            datatable.addCell(getCell(itemdesc2,0,0,0,0,Element.ALIGN_RIGHT,0,20));
      		datatable.addCell(getCell(value2,0,1,0,0,Element.ALIGN_LEFT,0,20));
      	}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	public PdfPCell getCell(String desc,int l,int r,int t,int b,int align,int span,float height)
	{
		
		 PdfPCell  cell = new PdfPCell(new Paragraph(desc,this.font));
		 cell.setMinimumHeight(height);
		 if(l==0) {
             cell.setBorderWidthLeft(0);
         }
		 if(r==0) {
             cell.setBorderWidthRight(0);
         }
		 if(t==0) {
             cell.setBorderWidthTop(0);
         }
		 if(b==0) {
             cell.setBorderWidthBottom(0);
         }
		 
		 if(span!=0)
		 {
			 cell.setColspan(span);  
		 }
		 cell.setHorizontalAlignment(align);
	  	 return cell;
	}
	
	
	
	
	
	
	/**
	 * 根据人员库前缀和人员编码生成其对应的文件
	 * 
	 * @param userTable
	 *            应用库 usra01
	 * @param userNumber
	 *            0000001 ,a0100
	 * @param flag
	 *            'P'照片
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public byte[]  createPhotoFile2(String userTable, String userNumber,
			String flag) throws Exception {
		
		byte [] buf=null; 
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select ext,Ole from ");
			strsql.append(userTable);
			strsql.append(" where A0100='");
			strsql.append(userNumber);
			strsql.append("' and Flag='");
			strsql.append(flag);
			strsql.append("'");
			rowSet=dao.search(strsql.toString());
			if (rowSet.next()) {				
				
				buf=rowSet.getBytes("Ole");				
			}
		
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		if(buf==null) {
            buf=new byte[0];
        }
		return buf;
	}
	
	
	
	
	
	
//	public static void main(String[] args)
//	{
//		try
//		{
//			Document  document = new Document(PageSize.A4, 40,40,60,30);
//			document.open();
//			
//			PdfPTable  datatable = new PdfPTable(4); 
//	        int headerwidths[] = {20,30,20,30};
//	        datatable.setWidths(headerwidths);
//	        datatable.setWidthPercentage(100);
//	        
//	        
//	        
//	        String fontfamilyname=ResourceFactory.getProperty("hmuster.label.fontSt");
//	        String fontEffect="0";
//	        int    fontSize=5;
//	        Font font = FontFamilyType.getFont(fontfamilyname,fontEffect,fontSize); // 生成字体样式
//	        
//            for (int i = 0; i <70; i++) {
//                
//       
//            	if(i%12==0)
//            	{
//            		
//            		PdfPCell cell1 = new PdfPCell(new Paragraph("  基本情况",font));
//            		cell1.setColspan(4);                   //列合并   
//            		cell1.setMinimumHeight(20f);
//            		cell1.setBackgroundColor(new Color(220,220,220));
//            		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
//            		datatable.addCell(cell1); 
//
//            		
//            	}
//            	else 
//            	{
//            		if(i<12)
//            		{
//            			
//	            		PdfPCell cell =null;
//            			
//	            		 cell = new PdfPCell(new Paragraph(i+" 姓名：",font));
//	             	   	 cell.setBorderWidthRight(0);
//	             	   	 cell.setBorderWidthTop(0);
//	             	   	 if(i!=69)
//	             	   		cell.setBorderWidthBottom(0);
//	             	   	 cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//	            		 datatable.addCell(cell);
//	             		
//	             		
//	             		 cell = new PdfPCell(new Paragraph("DDD",font));
//	             		 cell.setBorderWidthLeft(0);
//	             		 cell.setBorderWidthRight(0);
//	             		 cell.setBorderWidthTop(0);
//	             		if(i!=69)
//	             		 	cell.setBorderWidthBottom(0);
// 	             		 cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//	             		 datatable.addCell(cell);
//	            		
//	            		if(i>5)
//            			{
//            				cell = new PdfPCell(new Paragraph(i+" 姓名：",font));	
//		            		cell.setMinimumHeight(20f);
//		            		cell.setBorderWidthRight(0);
//		            		cell.setBorderWidthLeft(0);
//		            		cell.setBorderWidthTop(0);
//		            		if(i!=69)
//		                		cell.setBorderWidthBottom(0);
//		            		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//		            		datatable.addCell(cell);
//		            		
//		            		 cell = new PdfPCell(new Paragraph("说什么",font));
//		            		 cell.setBorderWidthLeft(0);
//		               	   	 cell.setBorderWidthTop(0);
//		               	   	 if(i!=69)
//		               	   		 cell.setBorderWidthBottom(0);
//		               	     cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//		             	   	 datatable.addCell(cell);
//            			}
//            			else
//            			{
//            				
//		            		cell = new PdfPCell(new Paragraph(i+" 姓名：",font));		            		
//		            		cell.setColspan(2);                   //列合并   
//		            		cell.setMinimumHeight(20f);		            		
//		            		cell.setBorderWidthLeft(0);
//		            		cell.setBorderWidthTop(0);		            		
//		            		if(i!=69)
//		                		cell.setBorderWidthBottom(0);
//		            		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//		            		datatable.addCell(cell);
//            				
//            				
//            				
//            			}
//            			
//            			
//	            		
//	             		 
//            		}
//            		else
//            		{
//            			
//	            		PdfPCell cell = new PdfPCell(new Paragraph(i+" 姓名：",font));
//	            		cell.setMinimumHeight(15f);
//	            		cell.setBorderWidthRight(0);
//	            		cell.setBorderWidthTop(0);
//	            		if(i!=69)
//	                		cell.setBorderWidthBottom(0);
//	            		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//	            		datatable.addCell(cell);
//	            		
//	            		 cell = new PdfPCell(new Paragraph("说什么",font));
//	            		 cell.setBorderWidthLeft(0);
//	               	   	 cell.setBorderWidthTop(0);
//	               	     cell.setColspan(3);  
//	               	   	 if(i!=69)
//	               	   		 cell.setBorderWidthBottom(0);
//	               	     cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//	             	   	 datatable.addCell(cell);
//            			
//            		}
//             		
//            	}
//            	
//            }
//	    	document.add(datatable);
//			document.close();
//			System.out.println(".....finish.....");
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}



	public ArrayList getRemarkList() {
		return remarkList;
	}



	public void setRemarkList(ArrayList remarkList) {
		this.remarkList = remarkList;
	}



	public HashMap getResumeBrowseSetMap() {
		return resumeBrowseSetMap;
	}



	public void setResumeBrowseSetMap(HashMap resumeBrowseSetMap) {
		this.resumeBrowseSetMap = resumeBrowseSetMap;
	}



	public HashMap getSetShowFieldMap() {
		return setShowFieldMap;
	}



	public void setSetShowFieldMap(HashMap setShowFieldMap) {
		this.setShowFieldMap = setShowFieldMap;
	}



	public ArrayList getZpPosList() {
		return zpPosList;
	}



	public void setZpPosList(ArrayList zpPosList) {
		this.zpPosList = zpPosList;
	}



	public ArrayList getFieldSetList() {
		return fieldSetList;
	}



	public void setFieldSetList(ArrayList fieldSetList) {
		this.fieldSetList = fieldSetList;
	}



	public HashMap getMap() {
		return map;
	}



	public void setMap(HashMap map) {
		this.map = map;
	}



	public String getPersontype() {
		return persontype;
	}



	public void setPersontype(String persontype) {
		this.persontype = persontype;
	}
	
	
	
}
