/*
 * Created on 2006-5-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.general.orgmap;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.beanutils.LazyDynaBean;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;

/**
 * 
 *<p>Title:ReportRelationOrgMap.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 25, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class ReportRelationOrgMap {
	/*
	 * ishistory是否历史机构
	 * */
	public String createOrgMap(List rs,List rootnode,String rootdesc,ParameterBo parameter,Connection conn,String code,boolean ishistory)
	{
		int leafagenodecount=0;
		String url="Orgmap_"+PubFunc.getStrg()+ ".pdf";
		/* 取消页面大小限制
		if(parameter.getPagewidth()>14400 || parameter.getPageheight()>14400)
		{
			try{
				Document document = null;
				document=new Document(PageSize.A4.rotate());
				PdfWriter writer = PdfWriter.getInstance(document,
						new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
								+  url));

			   document.open();
			   //Paragraph paragraph=new Paragraph(ResourceFactory.getProperty("general.inform.orgmax.error"));
              
		      // System.out.println(ResourceFactory.getProperty("general.inform.orgmax.error"));
			   

				 PdfContentByte cb = writer.getDirectContent();		     
			     cb.beginText();
			     //cb.setRGBColorFillF(0,0,0);
			     //cb.setColorFill(getFontColor(parameter.getFontcolor(),codesetid));
			     BaseFont bfComic = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   //解决中文问题		
			     cb.setFontAndSize(bfComic,15);
			     //int lines=linesCount(ResourceFactory.getProperty("general.inform.orgmax.error"),oneLineWordCount(Integer.parseInt(parameter.getCellwidth())));
			     cb.showTextAligned(PdfContentByte.ALIGN_LEFT, ResourceFactory.getProperty("general.inform.orgmax.error"), 260, 400,0);
			     cb.endText(); 		 
			 
			   
			   
			   // PdfPCell cell = new PdfPCell(paragraph);
               // BaseFont bfComic = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   //解决中文问题		
               // cell.setFontAndSize(bfComic,Integer.parseInt(parameter.getFontsize()));
               // cell.setFixedHeight(400);
	    	   // PdfPTable table = new PdfPTable(1);	
      		   // table.setTotalWidth(600);
      		   // table.setLockedWidth(true);
      		   // table.setTotalWidth(600);
      		   // table.addCell(cell);		          
      		   // table.writeSelectedRows(0, -1,100,500,writer.getDirectContent());	
			   document.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else   */
		{
			Rectangle pageSize = new Rectangle(parameter.getPagewidth(), parameter.getPageheight());			//自定义纸张大小
			Document document = null;
			document=new Document(pageSize);
			float x;
			float y;
			float x1;
			float x2;
			float y1;
			float y2;
			float bt_x1;
			float bt_x2;
			float b_y1;
			float b_y2;
			String codesetid="UN";
			OutputStream output = null;
			try{	
				output = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
						+  url);
				PdfWriter writer = PdfWriter.getInstance(document,output);

				document.open();
				/*输出跟接点*/
				if(rootnode!=null && !rootnode.isEmpty())
				{
	    			LazyDynaBean orgmapbean=(LazyDynaBean)rootnode.get(0);
	    			if("true".equalsIgnoreCase(parameter.getGraphaspect()))
	    			{
	    				x=getVx(parameter,0);
	    				y=getVy(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),0);
	    				x1=getVx1(parameter,0);
	    				x2=getVx2(parameter,0);
	    				y1=getVy1(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),0,Integer.parseInt(orgmapbean.get("firstchildscount").toString()));
	    				y2=getVy2(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),0,Integer.parseInt(orgmapbean.get("lastchildscount").toString()));
	    			    bt_x1=getVBT_x1(parameter,x);
	    			    bt_x2=getVBT_x2(parameter,x);
	    				b_y1=getVB_y(parameter,y);
	    				b_y2=getVB_y(parameter,y);
	    				if("true".equalsIgnoreCase(parameter.getGraph3d()))
	    				{
	        				bt_x1+=Integer.parseInt(parameter.getCellheight())/12;
	        				bt_x2+=Integer.parseInt(parameter.getCellheight())/12;
	    				}
	    			}
	    			else
	    			{
	    				x=getX(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),0);
	    				y=getY(parameter,0);
	    				x1=getX1(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),0,Integer.parseInt(orgmapbean.get("firstchildscount").toString()));
	    				x2=getX2(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),0,Integer.parseInt(orgmapbean.get("lastchildscount").toString()));
	    				y1=getY1(parameter,0);
	    				y2=getY2(parameter,0);
	    				  if("true".equalsIgnoreCase(parameter.getGraph3d()))
						   {
						   	y1+=Integer.parseInt(parameter.getCellheight())/12;
						   	y2+=Integer.parseInt(parameter.getCellheight())/12;
						   }
	    			    bt_x1=getBT_x(parameter,x);
	    			    bt_x2=getBT_x(parameter,x);
	    				b_y1=getB_y1(parameter,y);
	    				b_y2=getB_y2(parameter,y);
	    				
	    			}	  
	    			createOrgMapCell(document,writer,rootdesc,orgmapbean.get("subhead").toString(),parameter,x,y,codesetid);			
					printCellBottomTopLine(writer,bt_x1,bt_x2,b_y1,b_y2);
					printCellChildLine(writer, x1, x2, y1, y2);
							
				}
				if(!rs.isEmpty())
				{
					if("true".equalsIgnoreCase(parameter.getGraphaspect()))
					{
						for(int i=0;i<rs.size();i++)
						{
							
							LazyDynaBean orgmapbean=(LazyDynaBean)rs.get(i);
							codesetid=orgmapbean.get("codesetid").toString();
							//y=0;
							x=getVx(parameter,Integer.parseInt(orgmapbean.get("grade").toString()));
//							y=getVy(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),leafagenodecount);
							y=getVy(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),Integer.parseInt(orgmapbean.get("myleafagechilds").toString()),leafagenodecount);
							String d=orgmapbean.get("text").toString();
							//createOrgMapCell(document,writer,orgmapbean.get("text").toString(),orgmapbean.get("subhead").toString(),parameter,x,y,codesetid);
							if(!ishistory && "true".equalsIgnoreCase(parameter.getIsshowpersonconut())&&"true".equalsIgnoreCase(parameter.getIsshowpersonname())&&!"zz".equalsIgnoreCase(orgmapbean.get("codesetid").toString()))
							{
							    createOrgMapCell(document,writer,orgmapbean.get("text").toString() + "(" + orgmapbean.get("personcount").toString() + "人)" ,orgmapbean.get("subhead").toString(),parameter,x,y,codesetid);
							}else
							{
								createOrgMapCell(document,writer,orgmapbean.get("text").toString(),orgmapbean.get("subhead").toString(),parameter,x,y,codesetid);
							}
							//String te=orgmapbean.get("text").toString();
							x1=getVx1(parameter,Integer.parseInt(orgmapbean.get("grade").toString()));
							x2=getVx2(parameter,Integer.parseInt(orgmapbean.get("grade").toString()));
							y1=getVy1(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),leafagenodecount,Integer.parseInt(orgmapbean.get("firstchildscount").toString()));
//							y2=getVy2(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),leafagenodecount,Integer.parseInt(orgmapbean.get("firstchildscount").toString()));
							y2=getVy2(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),leafagenodecount,Integer.parseInt(orgmapbean.get("lastchildscount").toString()),Integer.parseInt(orgmapbean.get("myleafagechilds").toString()));
							if(!orgmapbean.get("codeitemid").equals(orgmapbean.get("childid")))
							{
								if(Integer.parseInt(orgmapbean.get("errorchilds").toString())<=0)
								{
									  /*底线和child线*/
									  bt_x1=getVBT_x1(parameter,x);
									  bt_x2=getVBT_x2(parameter,x);
									  b_y1=getVB_y(parameter,y);
									  b_y2=getVB_y(parameter,y);
									  if("true".equalsIgnoreCase(parameter.getGraph3d()))
									  {
						    			  bt_x1+=Integer.parseInt(parameter.getCellheight())/12;
						    			  bt_x2+=Integer.parseInt(parameter.getCellheight())/12;
									  }
									  printCellBottomTopLine(writer,bt_x1,bt_x2,b_y1,b_y2);
									  // if("true".equalsIgnoreCase(parameter.getGraph3d()))
									  // {
									  // 	x1+=Integer.parseInt(parameter.getCellheight())/12;
									  //	x2+=Integer.parseInt(parameter.getCellheight())/12;
									  // }
									  printCellChildLine(writer, x1, x2, y1, y2);
							    }
							    else
							    {
									   /*  底线和child线
									   bt_x1=getVBT_x1(parameter,x);
									   bt_x2=getVBT_x2(parameter,x);
									   b_y1=getVB_y(parameter,y);
									   b_y2=getVB_y(parameter,y);
									  if("true".equalsIgnoreCase(parameter.getGraph3d()))
									  {
					    				bt_x1+=Integer.parseInt(parameter.getCellheight())/12;
					    				bt_x2+=Integer.parseInt(parameter.getCellheight())/12;
									  }
									  printCellBottomTopLine(writer,bt_x1,bt_x2,b_y1,b_y2);
								   	  printCellChildLine(writer, x1, x2, y1, y2);*/
								   	  leafagenodecount++;
							    }
							}
							/*顶线*/
							float bt_x=getT_x2(parameter,x);
							float t_y=getVB_y(parameter,y);
							//if("true".equalsIgnoreCase(parameter.getGraph3d()))
							//{
							//	x+=Integer.parseInt(parameter.getCellheight())/12;
							//	bt_x+=Integer.parseInt(parameter.getCellheight())/12;
							//}
							if(rootnode!=null || rootnode==null && !orgmapbean.get("codeitemid").equals(code)) {
                                printCellBottomTopLine(writer,bt_x,x,t_y,t_y);
                            }
							if(orgmapbean.get("codeitemid").equals(orgmapbean.get("childid"))) {
                                leafagenodecount++;
                            }
						}
					}
					else
					{
						for(int i=0;i<rs.size();i++)
						{
							LazyDynaBean orgmapbean=(LazyDynaBean)rs.get(i);
							codesetid=orgmapbean.get("codesetid").toString();
//							x=getX(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),leafagenodecount);
							x=getX(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),Integer.parseInt(orgmapbean.get("myleafagechilds").toString()),leafagenodecount);
							y=getY(parameter,Integer.parseInt(orgmapbean.get("grade").toString()));
							if(!ishistory && "true".equalsIgnoreCase(parameter.getIsshowpersonconut())&&"true".equalsIgnoreCase(parameter.getIsshowpersonname())&&!"zz".equalsIgnoreCase(orgmapbean.get("codesetid").toString()))
							{
								createOrgMapCell(document,writer,orgmapbean.get("text").toString() + "(" + orgmapbean.get("personcount").toString() + "人)" ,orgmapbean.get("subhead").toString(),parameter,x,y,codesetid);
							}else
							{
								createOrgMapCell(document,writer,orgmapbean.get("text").toString(),orgmapbean.get("subhead").toString(),parameter,x,y,codesetid);
							}
							x1=getX1(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),leafagenodecount,Integer.parseInt(orgmapbean.get("firstchildscount").toString()));
//							x2=getX2(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),leafagenodecount,Integer.parseInt(orgmapbean.get("lastchildscount").toString()));
							x2=getX2(parameter,Integer.parseInt(orgmapbean.get("leafagechilds").toString()),leafagenodecount,Integer.parseInt(orgmapbean.get("lastchildscount").toString()),Integer.parseInt(orgmapbean.get("myleafagechilds").toString()));
							y1=getY1(parameter,Integer.parseInt(orgmapbean.get("grade").toString()));
							y2=getY2(parameter,Integer.parseInt(orgmapbean.get("grade").toString()));
							
							if(!orgmapbean.get("codeitemid").equals(orgmapbean.get("childid")))
							{
								if(Integer.parseInt(orgmapbean.get("errorchilds").toString())<=0)
								{
								   /*底线和child线*/
								   bt_x1=getBT_x(parameter,x);
								   bt_x2=getBT_x(parameter,x);
								   b_y1=getB_y1(parameter,y);
								   b_y2=getB_y2(parameter,y);
								   printCellBottomTopLine(writer,bt_x1,bt_x2,b_y1,b_y2);
								   if("true".equalsIgnoreCase(parameter.getGraph3d()))
								   {
								   	y1+=Integer.parseInt(parameter.getCellheight())/12;
								   	y2+=Integer.parseInt(parameter.getCellheight())/12;
								   }
								   printCellChildLine(writer, x1, x2, y1, y2);
							   }
							   else
							   {
							   	leafagenodecount++;
							   }
							}
							/*顶线*/
							float bt_x=getBT_x(parameter,x);
							float t_y2=getT_y2(parameter,y);
							if("true".equalsIgnoreCase(parameter.getGraph3d()))
							{
								y+=Integer.parseInt(parameter.getCellheight())/12;
								t_y2+=Integer.parseInt(parameter.getCellheight())/12;
							}
							if(rootnode!=null || rootnode==null && !orgmapbean.get("codeitemid").equals(code)) {
                                printCellBottomTopLine(writer,bt_x,bt_x,y,t_y2);
                            }
						
							if(orgmapbean.get("codeitemid").equals(orgmapbean.get("childid"))) {
                                leafagenodecount++;
                            }
						}
					}				
				}
				//document.close();
		    }catch(Exception e){
		      e.printStackTrace();
		    }finally{
		    	PubFunc.closeIoResource(output);
		    	PubFunc.closeIoResource(document);
		    }
		}		
	    return url;
	}
	/**
	 * @param writer
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	private void printCellBottomTopLine(PdfWriter writer, float x1, float x2, float y1, float y2)
	{
		PdfContentByte cb = writer.getDirectContent();
		cb.setLineWidth(1f);
		cb.moveTo(x1,y1);
		cb.lineTo(x2,y2);
		cb.stroke();
	}
	/**
	 * 水平方向y2
	 * @param parameter
	 * @param y
	 */
	private float  getT_y2(ParameterBo parameter,float y)
	{
		float t_y2;
		t_y2=y + Integer.parseInt(parameter.getCellvspacewidth()); 
		return t_y2;
	}	
	/**
	 * 水平方向的BT_y2
	 * @param parameter
	 * @param y
	 */
	private float  getB_y2(ParameterBo parameter,float y)
	{
		float b_y2;
		b_y2=y - Integer.parseInt(parameter.getCellheight()) - Integer.parseInt(parameter.getCellvspacewidth()); 
		return b_y2;
	}
	/**
	 * 水平方向的BT_y1
	 * @param parameter
	 * @param y
	 */
	private float  getB_y1(ParameterBo parameter,float y)
	{
		float b_y1;
		b_y1=y - Integer.parseInt(parameter.getCellheight()); 
		return b_y1;
	}
	/**
	 * 水平方向的BT_x
	 * @param parameter
	 * @param x
	 */
	private float  getBT_x(ParameterBo parameter,float x)
	{
		float b_x;
		b_x=x + Integer.parseInt(parameter.getCellwidth())/2; 
		return b_x;
	}
	/**
	 * 水平方向的X1
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getX1(ParameterBo parameter,float leafagechilds,float leafagenodecount,float firstchildscount)
	{
		float x1;
		if(firstchildscount>0) {
            x1=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) *  (leafagenodecount  + firstchildscount/2) -Integer.parseInt(parameter.getCellhspacewidth())/2  + parameter.getPagespacewidth();
        } else {
            x1=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) *  (leafagenodecount  +1+ firstchildscount/2) -Integer.parseInt(parameter.getCellhspacewidth()) + parameter.getPagespacewidth()-Integer.parseInt(parameter.getCellwidth())/2;
        }
		return x1;
	}
	/**
	 * 水平方向的X2
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getX2(ParameterBo parameter,float leafagechilds,float leafagenodecount,float lastchildscount)
	{
		float x2;
		if(lastchildscount>0) {
            x2=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) * (leafagechilds + leafagenodecount+1 -lastchildscount/2)  -Integer.parseInt(parameter.getCellhspacewidth())/2 + parameter.getPagespacewidth();
        } else {
            x2=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) * (leafagechilds + leafagenodecount +1-lastchildscount/2) -(Integer.parseInt(parameter.getCellhspacewidth())) + parameter.getPagespacewidth()-Integer.parseInt(parameter.getCellwidth())/2;
        }
		return x2;
	}
	/**
	 * 水平方向的X2
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getX2(ParameterBo parameter,float leafagechilds,float leafagenodecount,float lastchildscount,float conchilds)
	{
		if(leafagechilds+conchilds>1 && leafagechilds>0)
		{
			if(conchilds>0)
			{				
				if(conchilds%2>0) {
                    leafagechilds = (leafagechilds + conchilds)/2+conchilds/2;
                } else
				{
					if(!(leafagechilds%2>0)) {
                        leafagechilds = (leafagechilds + conchilds)/2+conchilds/2;
                    } else {
                        leafagechilds = (leafagechilds + conchilds)/2+conchilds/2-leafagechilds/2;
                    }
				}				
			}else {
                leafagechilds = (leafagechilds + conchilds);
            }
		}
		float x2;
		if(lastchildscount>0) {
            x2=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) * (leafagechilds + leafagenodecount+1 -lastchildscount/2)  -Integer.parseInt(parameter.getCellhspacewidth())/2 + parameter.getPagespacewidth();
        } else {
            x2=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) * (leafagechilds + leafagenodecount +1-lastchildscount/2) -(Integer.parseInt(parameter.getCellhspacewidth())) + parameter.getPagespacewidth()-Integer.parseInt(parameter.getCellwidth())/2;
        }
		return x2;
	}
	/**
	 * 水平方向的Y1
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getY1(ParameterBo parameter,int grade)
	{
		float y1;
		if("true".equalsIgnoreCase(parameter.getGraph3d())) {
            y1=(Integer.parseInt(parameter.getCellheight())*13/12 + Integer.parseInt(parameter.getCellvspacewidth())*2) * (grade+1) - Integer.parseInt(parameter.getCellvspacewidth()) + parameter.getPagespaceheight();
        } else {
            y1=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())*2) * (grade+1) - Integer.parseInt(parameter.getCellvspacewidth()) + parameter.getPagespaceheight();
        }
		y1=parameter.getPageheight()-y1;
		return y1;
	}
	/**
	 * 水平方向的Y2
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getY2(ParameterBo parameter,int grade)
	{
		float y2;
		if("true".equalsIgnoreCase(parameter.getGraph3d())) {
            y2=(Integer.parseInt(parameter.getCellheight())*13/12 + Integer.parseInt(parameter.getCellvspacewidth())*2) * (grade+1) - Integer.parseInt(parameter.getCellvspacewidth())  + parameter.getPagespaceheight();
        } else {
            y2=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())*2) * (grade+1) - Integer.parseInt(parameter.getCellvspacewidth())  + parameter.getPagespaceheight();
        }
		y2=parameter.getPageheight()-y2;
		return y2;
	}
	
	/**
	 * @param writer
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	private void printCellChildLine(PdfWriter writer, float x1, float x2, float y1, float y2) {
		PdfContentByte cb = writer.getDirectContent();
		cb.setLineWidth(1f);
		//cb.setLineDash(3, 3, 0);	
		cb.moveTo(x1,y1);
		cb.lineTo(x2,y2);
		cb.stroke();
		//cb.setLineDash(1);
	}
	/**
	 * 水平方向的X
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float getX(ParameterBo parameter,int leafagechilds,int leafagenodecount)
	{
		float x;
		x=(float)leafagechilds/2.0f;
		if(x<0) {
            x=0;
        }
		x=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) * (x + leafagenodecount) + parameter.getPagespacewidth(); 
		return x;
	}
	/**
	 * 水平方向的X
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float getX(ParameterBo parameter,int leafagechilds,int conchilds,int leafagenodecount)
	{
		float x;
		leafagechilds = leafagechilds + conchilds;
		x=(float)leafagechilds/2.0f;
		if(x<0) {
            x=0;
        }
		x=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) * (x + leafagenodecount) + parameter.getPagespacewidth(); 
		return x;
	}
	/**
	 * 水平方向的Y
	 * @param parameter	
	 * @param grade
	 */
	private float getY(ParameterBo parameter,int grade)
	{
		float y;
		if("true".equalsIgnoreCase(parameter.getGraph3d())) {
            y=(Integer.parseInt(parameter.getCellheight())*13/12 + Integer.parseInt(parameter.getCellvspacewidth())*2) * (grade) + parameter.getPagespaceheight();
        } else {
            y=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())*2) * (grade) + parameter.getPagespaceheight();
        }
		y=parameter.getPageheight()-y;
		return y;
	} 
	/**
	 * 垂直方向的X
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float getVx(ParameterBo parameter,int grade)
	{
		float vx;
		if("true".equalsIgnoreCase(parameter.getGraph3d())) {
            vx=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellheight())/12 + Integer.parseInt(parameter.getCellhspacewidth())*2) * (grade) + parameter.getPagespacewidth();
        } else {
            vx=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())*2) * (grade) + parameter.getPagespacewidth();
        }
		return vx;
	
	}
	/**
	 * 垂直方向的Y
	 * @param parameter	
	 * @param grade
	 */
	private float getVy(ParameterBo parameter,int leafagechilds,int leafagenodecount)
	{
		float vy;
		vy=(float)leafagechilds/2.0f;
		if(vy<0) {
            vy=0;
        }
		int h= Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth());
//		System.out.println(Integer.parseInt(parameter.getCellheight()));
//		System.out.println(Integer.parseInt(parameter.getCellvspacewidth()));
		float m=vy + leafagenodecount;
//		System.out.println(vy);
//		System.out.println(leafagenodecount);
		int n=parameter.getPagespaceheight();
//		System.out.println(parameter.getPagespaceheight());	
//		System.out.println(parameter.getPageheight());
		vy=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) * (vy + leafagenodecount) + parameter.getPagespaceheight();
//		System.out.println(vy);
		vy=parameter.getPageheight()-vy;
//		System.out.println(vy);		
		return vy;
	}  	
	/**
	 * 垂直方向的Y
	 * @param parameter	
	 * @param grade
	 */
	private float getVy(ParameterBo parameter,int leafagechilds,int conchilds, int leafagenodecount)
	{
		float vy=0;
		leafagechilds = leafagechilds + conchilds;
		vy=(float)leafagechilds/2.0f;
		if(vy<0) {
            vy=0;
        }
		int h= Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth());
		float m=vy + leafagenodecount;
		int n=parameter.getPagespaceheight();
		vy=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) * (vy + leafagenodecount) + parameter.getPagespaceheight();
		vy=parameter.getPageheight()-vy;
		return vy;
	}  	
	/**
	 * 垂直方向的X1
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getVx1(ParameterBo parameter,int grade)
	{
		float vx1;
		if("true".equalsIgnoreCase(parameter.getGraph3d())) {
            vx1=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellheight())/12+ Integer.parseInt(parameter.getCellhspacewidth())*2) * (grade+1) - Integer.parseInt(parameter.getCellhspacewidth()) + parameter.getPagespacewidth();
        } else {
            vx1=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())*2) * (grade+1) - Integer.parseInt(parameter.getCellhspacewidth()) + parameter.getPagespacewidth();
        }
		return vx1;
	}
	/**
	 * 垂直方向的X2
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getVx2(ParameterBo parameter,int grade)
	{
		float vx2;
		if("true".equalsIgnoreCase(parameter.getGraph3d())) {
            vx2=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellheight())/12+ Integer.parseInt(parameter.getCellhspacewidth())*2) * (grade+1) - Integer.parseInt(parameter.getCellhspacewidth())  + parameter.getPagespacewidth();
        } else {
            vx2=(Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())*2) * (grade+1) - Integer.parseInt(parameter.getCellhspacewidth())  + parameter.getPagespacewidth();
        }
		return vx2;
	}
	/**
	 * 垂直方向的Y1
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getVy1(ParameterBo parameter,float leafagechilds,float leafagenodecount,float firstchildscount)
	{
		float vy1;
		if(firstchildscount>0) {
            vy1=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) *  (leafagenodecount  + firstchildscount/2)  + parameter.getPagespaceheight()- Integer.parseInt(parameter.getCellvspacewidth())/2;
        } else {
            vy1=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) *  (leafagenodecount  +1+ firstchildscount/2) + parameter.getPagespaceheight()-Integer.parseInt(parameter.getCellheight())/2-Integer.parseInt(parameter.getCellvspacewidth());
        }
		vy1=parameter.getPageheight()-vy1;
		return vy1;	
	}
	/**
	 * 垂直方向的Y2
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getVy2(ParameterBo parameter,float leafagechilds,float leafagenodecount,float lastchildscount)
	{
		float vy2;
		if(lastchildscount>0) {
            vy2=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) * (leafagechilds + leafagenodecount+1 -lastchildscount/2) + parameter.getPagespaceheight()- Integer.parseInt(parameter.getCellvspacewidth())/2;
        } else {
            vy2=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) * (leafagechilds + leafagenodecount+1 -lastchildscount/2) + parameter.getPagespaceheight()-Integer.parseInt(parameter.getCellheight())/2 - Integer.parseInt(parameter.getCellvspacewidth());
        }
		vy2=parameter.getPageheight() - vy2;
		return vy2;	
	}
	
	
	/**
	 * 垂直方向的Y1
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getVy1(ParameterBo parameter,float leafagechilds,float leafagenodecount,float firstchildscount,float conchilds)
	{
		float vy1;
		if(firstchildscount>0) {
            vy1=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) *  (leafagenodecount  + firstchildscount/2)  + parameter.getPagespaceheight()- Integer.parseInt(parameter.getCellvspacewidth())/2;
        } else {
            vy1=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) *  (leafagenodecount  +1+ firstchildscount/2) + parameter.getPagespaceheight()-Integer.parseInt(parameter.getCellheight())/2-Integer.parseInt(parameter.getCellvspacewidth());
        }
		vy1=parameter.getPageheight()-vy1;
		return vy1;	
	}
	/**
	 * 垂直方向的Y2
	 * @param parameter
	 * @param leafagechilds
	 * @param leafagenodecount
	 */
	private float  getVy2(ParameterBo parameter,float leafagechilds,float leafagenodecount,float lastchildscount,float conchilds)
	{
		float vy2;
		if(leafagechilds+conchilds>1 && leafagechilds>0)
		{
			if(conchilds>0)
			{				
				if(conchilds%2>0) {
                    leafagechilds = (leafagechilds + conchilds)/2+conchilds/2;
                } else
				{
					if(!(leafagechilds%2>0)) {
                        leafagechilds = (leafagechilds + conchilds)/2+conchilds/2;
                    } else {
                        leafagechilds = (leafagechilds + conchilds)/2+conchilds/2-leafagechilds/2;
                    }
				}				
			}else {
                leafagechilds = (leafagechilds + conchilds);
            }
		}
		if(lastchildscount>0) {
            vy2=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) * (leafagechilds + leafagenodecount+1 -lastchildscount/2) + parameter.getPagespaceheight()- Integer.parseInt(parameter.getCellvspacewidth())/2;
        } else {
            vy2=(Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) * (leafagechilds + leafagenodecount+1 -lastchildscount/2) + parameter.getPagespaceheight()-Integer.parseInt(parameter.getCellheight())/2 - Integer.parseInt(parameter.getCellvspacewidth());
        }
		vy2=parameter.getPageheight() - vy2;
		return vy2;	
	}
	
	
	/**
	 * 垂直方向的BT_x2
	 * @param parameter
	 * @param x
	 */
	private float  getVBT_x2(ParameterBo parameter,float x)
	{
		float b_x2;
		b_x2=x + Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth()); 
		return b_x2;
	}
	/**
	 * 垂直方向的BT_x1
	 * @param parameter
	 * @param x
	 */
	private float  getVBT_x1(ParameterBo parameter,float x)
	{
		float b_x1;
		b_x1=x + Integer.parseInt(parameter.getCellwidth()); 
		return b_x1;
	}
	/**
	 * 垂直方向的VBT_y
	 * @param parameter
	 * @param x
	 */
	private float  getVB_y(ParameterBo parameter,float y)
	{
		float b_y;
		b_y=y - Integer.parseInt(parameter.getCellheight())/2; 
		return b_y;
	}
	/**
	 * 垂直方行x2
	 * @param parameter
	 * @param x
	 */
	private float  getT_x2(ParameterBo parameter,float x)
	{
		float t_x2;
		t_x2=x - Integer.parseInt(parameter.getCellhspacewidth()); 
		return t_x2;
	}	
	/**
	 * @param document
	 * @param writer
	 * @param text
	 * @param personcountdesc
	 * @param parameter
	 * @param x
	 * @param y	
	 */
	private void createOrgMapCell(Document document,PdfWriter writer,String text,String personcountdesc,ParameterBo parameter,float x,float y,String codesetid) throws Exception
	{
		switch(parameter.getCellshape().charAt(0))
		{
		   case 'r':       /*矩形*/
		   {
		   	createRectCell(document,writer,text,personcountdesc,parameter,x,y,codesetid);
		   }
		   case 't':       /*三角*/
		   {
		   	
		   }
		   case 'e':      /*椭圆*/
		   {
		   }
	    }
	}
	/**
	 * @param document
	 * @param writer
	 * @param text
	 * @param personcountdesc
	 * @param parameter
	 * @param x
	 * @param y	
	 */
	private void createRectCell(Document document,PdfWriter writer,String text,String subhead,ParameterBo parameter,float x,float y,String codesetid) throws Exception
	{
		 PDFBaseBo pdfbasebo=new PDFBaseBo();
		 if("true".equalsIgnoreCase(parameter.getGraph3d()))
		 {
			 PdfContentByte cb = writer.getDirectContent();
			 float x1=0f;
			 x1=Integer.parseInt(parameter.getCellheight())/6;			
		     cb.moveTo(x+ Integer.parseInt(parameter.getCellwidth()),y);
		     cb.lineTo(x+Integer.parseInt(parameter.getCellwidth())+x1 ,y + x1);
		     cb.lineTo(x+Integer.parseInt(parameter.getCellwidth())+x1,y+x1-Integer.parseInt(parameter.getCellheight()));
		     cb.lineTo(x+Integer.parseInt(parameter.getCellwidth()),y-Integer.parseInt(parameter.getCellheight()));
		     cb.setColorFill(getCellColor(parameter.getCellcolor(),codesetid));
		     //cb.setRGBColorFillF(getCellColorR(parameter.getCellcolor(),codesetid),getCellColorG(parameter.getCellcolor(),codesetid),getCellColorB(parameter.getCellcolor(),codesetid));
		     cb.closePathFillStroke();
		     cb.moveTo(x,y);
		     cb.lineTo(x+x1,y+x1);
		     cb.lineTo(x+x1+ Integer.parseInt(parameter.getCellwidth()),y+x1);
		     cb.lineTo(x+ Integer.parseInt(parameter.getCellwidth()),y);
		     cb.setColorFill(getCellColor(parameter.getCellcolor(),codesetid));
		     //cb.setRGBColorFillF(getCellColorR(parameter.getCellcolor(),codesetid),getCellColorG(parameter.getCellcolor(),codesetid),getCellColorB(parameter.getCellcolor(),codesetid));
		     cb.closePathFillStroke();
		     
		       
		     
		     cb.moveTo(x,y);
		     cb.lineTo(x+Integer.parseInt(parameter.getCellwidth()),y);
		     cb.lineTo(x+Integer.parseInt(parameter.getCellwidth()),y-Integer.parseInt(parameter.getCellheight()));
		     cb.lineTo(x,y-Integer.parseInt(parameter.getCellheight()));
		     cb.setColorFill(getCellColor(parameter.getCellcolor(),codesetid));
			 //cb.setRGBColorFillF(getCellColorR(parameter.getCellcolor(),codesetid),getCellColorG(parameter.getCellcolor(),codesetid),getCellColorB(parameter.getCellcolor(),codesetid));
		     cb.closePathFillStroke();
		     cb.stroke();
		     cb.beginText();
		     //cb.setRGBColorFillF(0,0,0);
		     cb.setColorFill(getFontColor(parameter.getFontcolor(),codesetid));
		     BaseFont bfComic = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   //解决中文问题		
		     cb.setFontAndSize(bfComic,Integer.parseInt(parameter.getFontsize()));
		     int lines=linesCount(text,oneLineWordCount(Integer.parseInt(parameter.getCellwidth())));
		     int alignx= getAlignX(parameter,text);
		     int valigny= getVAlignY(parameter,text);
		     if(lines>0)
		     {
		    	
		     	for(int i=0;i<lines;i++)
		     	{
		     		if(i==lines-1)
		     		{
		     			 cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text.substring(i*oneLineWordCount(Integer.parseInt(parameter.getCellwidth()))), x+28+(Integer.parseInt(parameter.getCellwidth())/10)+alignx, y-Integer.parseInt(parameter.getCellheight())/4-i*20+valigny,0);
		     		}
		     		else
		     		{
		     			 cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text.substring(i*oneLineWordCount(Integer.parseInt(parameter.getCellwidth())),(i+1)*oneLineWordCount(Integer.parseInt(parameter.getCellwidth()))), x+28+(Integer.parseInt(parameter.getCellwidth())/10), y-Integer.parseInt(parameter.getCellheight())/4-i*20,0);
		     		}		     		
		     	}
		     }else
		     {
		     	 cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text, x+28+(Integer.parseInt(parameter.getCellwidth())/10), y-Integer.parseInt(parameter.getCellheight())/4,0);
		     }
		     cb.endText();  
		 }
		 else
		 {   

			 PdfContentByte cb = writer.getDirectContent();		     
		     cb.moveTo(x,y);
		     cb.lineTo(x+Integer.parseInt(parameter.getCellwidth()),y);
		     cb.lineTo(x+Integer.parseInt(parameter.getCellwidth()),y-Integer.parseInt(parameter.getCellheight()));
		     cb.lineTo(x,y-Integer.parseInt(parameter.getCellheight()));
		     cb.setColorFill(getCellColor(parameter.getCellcolor(),codesetid));
			 //cb.setRGBColorFillF(getCellColorR(parameter.getCellcolor(),codesetid),getCellColorG(parameter.getCellcolor(),codesetid),getCellColorB(parameter.getCellcolor(),codesetid));
		     cb.closePathFillStroke();
		     cb.stroke();
		     cb.beginText();
		     //cb.setRGBColorFillF(0,0,0);
		     cb.setColorFill(getFontColor(parameter.getFontcolor(),codesetid));
		     BaseFont bfComic = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   //解决中文问题		
		     cb.setFontAndSize(bfComic,Integer.parseInt(parameter.getFontsize()));
		     int lines=linesCount(text,oneLineWordCount(Integer.parseInt(parameter.getCellwidth())));
		     int alignx= getAlignX(parameter,text);
		     int valigny= getVAlignY(parameter,text);
		     if(lines>0)
		     {
		    	 
		     	for(int i=0;i<lines;i++)
		     	{
		     		if(i==lines-1)
		     		{
		     			 cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text.substring(i*oneLineWordCount(Integer.parseInt(parameter.getCellwidth()))), x+(Integer.parseInt(parameter.getCellwidth())/10)+alignx, y-Integer.parseInt(parameter.getCellheight())/4-i*20-valigny,0);
		     			 
		     		}
		     		else
		     		{
		     			 cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text.substring(i*oneLineWordCount(Integer.parseInt(parameter.getCellwidth())),(i+1)*oneLineWordCount(Integer.parseInt(parameter.getCellwidth()))), x+(Integer.parseInt(parameter.getCellwidth())/10), y-Integer.parseInt(parameter.getCellheight())/4-i*20,0);
		     		}		     		
		     	}
		     }else
		     {
		     	 cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text, x+(Integer.parseInt(parameter.getCellwidth())/10), y-Integer.parseInt(parameter.getCellheight())/4,0);
		     }
		     cb.endText();  
		    /* Paragraph paragraph=new Paragraph();
	         PdfPCell cell = new PdfPCell(paragraph);
	         int align=getAlign(parameter);
	         pdfbasebo.setTextAlign(align,cell); 
	         cell.setFixedHeight(Integer.parseInt(parameter.getCellheight()));
	         Rectangle borders = new Rectangle(0f,0f);
	         borders.setBorderColor(getCellFrameColor(parameter.getCellcolor(),codesetid));
	         borders.setBackgroundColor(getCellColor(parameter.getCellcolor(),codesetid));
	         cell.cloneNonPositionParameters(borders);*/
	         /*自适应大小还是换行待写*/
	        /* Paragraph smallparagraph=new Paragraph(text, pdfbasebo.getFont(parameter.getFontstyle(),Integer.parseInt(parameter.getFontsize())));
	     	 paragraph.add(smallparagraph);	  
	     	 
	         PdfPTable table = new PdfPTable(1);	
			 table.setTotalWidth(Integer.parseInt(parameter.getCellwidth()));
			 table.setLockedWidth(true);
			 table.addCell(cell);		
			 table.writeSelectedRows(0, -1,x,y,writer.getDirectContent());*/
		 }

	}
	private int getAlignX(ParameterBo parameter,String text)
	{
		int x=Integer.parseInt(parameter.getCellwidth());
		if("align-left".equalsIgnoreCase(parameter.getCellletteralignleft()) )
		{
			x=0;
		}else if("align-center".equalsIgnoreCase(parameter.getCellletteraligncenter()))
		{
			x=x/4;
		}else if("align-right".equalsIgnoreCase(parameter.getCellletteralignright()))
		{
			x=x/2;		
		}else
		{
			x=0;
		}
		return x;
	}
	private int getVAlignY(ParameterBo parameter,String text)
	{
		int y=0;
		if(text==null||text.length()<=0) {
            text="";
        }
		int x=Integer.parseInt(parameter.getCellwidth());
		if(text.length()>=(x*6/80))
		{
			return 0;
		}
		if("valign-center".equalsIgnoreCase(parameter.getCelllettervaligncenter()))
        {
			int h=Integer.parseInt(parameter.getCellheight());;
    	    y=h/4;
        }
		return y;
	}
	/**
	 * @param parameter	 
	 * =0上左 =1上中  =2上右  =3下左  =4下中  =5下右 =6中左  =7中中 =8中右
	 */
	private int  getAlign(ParameterBo parameter)
	{
		int align=0;
	    if("align-left".equalsIgnoreCase(parameter.getCellletteralignleft()) && "valign-top".equalsIgnoreCase(parameter.getCelllettervaligncenter()))
	    {
	    	align=0;
	    }else if("align-center".equalsIgnoreCase(parameter.getCellletteraligncenter()) && "valign-top".equalsIgnoreCase(parameter.getCelllettervaligncenter()))
	    {
	    	align=1;
	    }else if("align-right".equalsIgnoreCase(parameter.getCellletteralignright()) && "valign-top".equalsIgnoreCase(parameter.getCelllettervaligncenter()))
	    {
	    	align=2;
	    }else if("align-left".equalsIgnoreCase(parameter.getCellletteralignleft()) && "valign-bottom".equalsIgnoreCase(parameter.getCelllettervaligncenter()))
	    {
	    	align=3;
	    }else if("align-center".equalsIgnoreCase(parameter.getCellletteraligncenter()) && "valign-bottom".equalsIgnoreCase(parameter.getCelllettervaligncenter()))
	    {
	    	align=4;
	    }else if("align-right".equalsIgnoreCase(parameter.getCellletteralignright()) && "valign-bottom".equalsIgnoreCase(parameter.getCelllettervaligncenter()))
	    {
	    	align=5;
	    }else if("align-left".equalsIgnoreCase(parameter.getCellletteralignleft()) && "valign-center".equalsIgnoreCase(parameter.getCelllettervaligncenter()))
	    {
	    	align=6;
	    }
	    else if("align-center".equalsIgnoreCase(parameter.getCellletteraligncenter()) && "valign-center".equalsIgnoreCase(parameter.getCelllettervaligncenter()))
	    {
	    	align=7;
	    }
	    else if("align-right".equalsIgnoreCase(parameter.getCellletteralignright()) && "valign-center".equalsIgnoreCase(parameter.getCelllettervaligncenter()))
	    {
	    	align=8;
	    }
	    return align;
	}
	
	private PdfContentByte getHorizontal(PdfContentByte cb,int align)
	{
		/*  单元格内容的排列方式
		 * =0上左 =1上中  =2上右  =3下左  =4下中  =5下右 =6中左  =7中中 =8中右
	     */			
		 if(align==0)   
		 {
			 cb.setHorizontalScaling(Element.ALIGN_JUSTIFIED);    //基于最合适的
		 }
		 else if(align==1)
		 {
			 cb.setHorizontalScaling(Element.ALIGN_CENTER);
		 }
		 else if(align==2)
		 {
			 cb.setHorizontalScaling(Element.ALIGN_RIGHT);
		 }
		 else if(align==3)
		 {
			 cb.setHorizontalScaling(Element.ALIGN_JUSTIFIED);
			 //cb.setVerticalAlignment(Element.ALIGN_BOTTOM);
		 }
		 else if(align==4)
		 {
			 cb.setHorizontalScaling(Element.ALIGN_CENTER);
			 //cb.setVerticalAlignment(Element.ALIGN_BOTTOM);
		 }
		 else if(align==5)
		 {
			 cb.setHorizontalScaling(Element.ALIGN_RIGHT);
			 //cb.setVerticalAlignment(Element.ALIGN_BOTTOM);
		 }
		 else if(align==6)
		 {
			 cb.setHorizontalScaling(Element.ALIGN_JUSTIFIED);
			 //cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		 }
		 else if(align==7)
		 {
			 cb.setHorizontalScaling(Element.ALIGN_CENTER);   //居中
			 //cb.setVerticalAlignment(Element.ALIGN_MIDDLE);
		 }
		 else if(align==8)
		 {
			 cb.setHorizontalScaling(Element.ALIGN_RIGHT);   //居右
			 
		 }	
		return cb;
	}
	public void createOrgMap(List rs)
	{
		
	}
	private int linesCount(String text,int oneLineWordCount)
	{
		return text.length()/oneLineWordCount +1;
	}
	private int oneLineWordCount(int width)
	{
	   return width*6/80;
	}
	public int[] getOrgMapPageSize(ParameterBo parameter,String code,Connection conn,String username) throws GeneralException
	{
		StringBuffer sqlstr=new StringBuffer();
		if("true".equalsIgnoreCase(parameter.getIsshowpersonname()))
		{
			if(code!=null && code.length()>0)
			{
				sqlstr.append("select aa.grade-bb.grade as grade,cc.counts + dd.counts AS leafagechildscount from ");
				sqlstr.append("(SELECT MAX(grade) + 1 as grade from ");
				sqlstr.append(username+username);
				sqlstr.append("organization where codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') aa,");
				sqlstr.append("(SELECT grade FROM ");
				sqlstr.append(username+username);
				sqlstr.append("organization WHERE codeitemid = '");
				sqlstr.append(code);
				sqlstr.append("') bb,");
				sqlstr.append("(SELECT COUNT(*) as counts FROM ");
				sqlstr.append(username+username);
				sqlstr.append("organization b WHERE b.parentid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%' AND b.codeitemid = b.childid and b.parentid like '");
				sqlstr.append(code);
				sqlstr.append("%') cc,");
				sqlstr.append("(SELECT COUNT(*) as counts FROM ");
				sqlstr.append(username+username);
				sqlstr.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM ");
				sqlstr.append(username+username);
				sqlstr.append("organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') dd");
				//System.out.println("d" + sqlstr.toString());
			}else
			{
				sqlstr.append("select aa.grade,cc.counts + dd.counts  leafagechildscount from ");
				sqlstr.append("(SELECT MAX(grade) +1 AS grade from ");
				sqlstr.append(username+username);
				sqlstr.append("organization) aa,");
				sqlstr.append("(SELECT COUNT(*) as counts FROM ");
				sqlstr.append(username+username);
				sqlstr.append("organization b WHERE b.parentid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%' AND b.codeitemid = b.childid) cc,");
				sqlstr.append("(SELECT COUNT(*) as counts FROM ");
				sqlstr.append(username+username);
				sqlstr.append("organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM ");
				sqlstr.append(username+username);
				sqlstr.append("organization orge WHERE orge.codeItemId = org.childId))) dd");
			}
		}
		else
		{
			if(code!=null && code.length()>0)
			{
				sqlstr.append("select aa.grade -bb.grade  as grade,cc.counts + dd.counts as leafagechildscount from ");
				sqlstr.append("(SELECT MAX(grade) + 1 as grade from organization where codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') aa,"); 
				sqlstr.append("(SELECT grade FROM organization WHERE codeitemid = '");
				sqlstr.append(code);
				sqlstr.append("') bb,");
				sqlstr.append("(SELECT COUNT(*) as counts FROM organization b WHERE b.parentid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%' AND b.codeitemid = b.childid and b.codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') cc,");
				sqlstr.append("(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId)) and org.codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') dd");
			}else
			{
				sqlstr.append("select aa.grade,bb.counts + cc.counts as leafagechildscount from ");
				sqlstr.append("(SELECT MAX(grade) +1 AS grade from organization where codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') aa,(SELECT COUNT(*) as counts FROM organization b WHERE b.parentid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%' AND b.codeitemid = b.childid and b.codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') bb");
				sqlstr.append(",(SELECT COUNT(*) as counts FROM organization org WHERE (codeitemid <> childid) AND (org.codeitemid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM organization orge WHERE orge.codeItemId = org.childId))) cc ");
			}
		}
		List rs=ExecuteSQL.executeMyQuery(sqlstr.toString(),conn);
		int[] pagesize=new int[2];
		if(!rs.isEmpty()&&rs.size()>0)
		{
			if("true".equalsIgnoreCase(parameter.getGraphaspect()))
			{
				LazyDynaBean orgbean=(LazyDynaBean)rs.get(0);				
				pagesize[0]=(1+Integer.parseInt(orgbean.get("grade").toString()))*(Integer.parseInt(parameter.getCellwidth()) +Integer.parseInt(parameter.getCellheight())/6 + Integer.parseInt(parameter.getCellhspacewidth())*2) + parameter.getPagespacewidth()*2;
				pagesize[1]=(1+Integer.parseInt(orgbean.get("leafagechildscount").toString())) * (Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) + parameter.getPagespaceheight() *2;
			}else
			{
				LazyDynaBean orgbean=(LazyDynaBean)rs.get(0);
				pagesize[0]=(1+Integer.parseInt(orgbean.get("leafagechildscount").toString())) * (Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) + parameter.getPagespacewidth() *2;
				pagesize[1]=(1+Integer.parseInt(orgbean.get("grade").toString()))*(Integer.parseInt(parameter.getCellheight())*7/6 + Integer.parseInt(parameter.getCellvspacewidth())*2) + parameter.getPagespaceheight()*2;
	
			}
		    if(pagesize[0]>14400)
		    {
		    	//pagesize[0]=14400;
		    	//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors"),"",""));
		    }
			if(pagesize[1]>14400)
			{
				//pagesize[1]=14400;
				//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors"),"",""));
			}
			 if(pagesize[0]<180)
			 {
				 pagesize[0]=180;
			 }
			 if(pagesize[1]<180)
			{
				 pagesize[1]=180;
				//pagesize[1]=14400;
					//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors"),"",""));
			}
		}
		return pagesize;
	}
	public int[] getHistoryOrgMapPageSize(ParameterBo parameter,String code,Connection conn,String username,String catalog_id) throws GeneralException
	{
		StringBuffer sqlstr=new StringBuffer();
			if(code!=null && code.length()>0)
			{
				sqlstr.append("select aa.grade -bb.grade  as grade,cc.counts + dd.counts as leafagechildscount from ");
				sqlstr.append("(SELECT MAX(grade) + 1 as grade from hr_org_history where catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("' and codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') aa,"); 
				sqlstr.append("(SELECT grade FROM hr_org_history WHERE catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("' and codeitemid = '");
				sqlstr.append(code);
				sqlstr.append("') bb,");
				sqlstr.append("(SELECT COUNT(*) as counts FROM hr_org_history b WHERE b.catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("' and b.parentid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%' AND b.codeitemid = b.childid and b.codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') cc,");
				sqlstr.append("(SELECT COUNT(*) as counts FROM hr_org_history org WHERE org.catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("' and (codeitemid <> childid) AND (org.codeitemid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM hr_org_history orge WHERE orge.catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("' and orge.codeItemId = org.childId)) and org.codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') dd");
			}else
			{
				sqlstr.append("select aa.grade,bb.counts + cc.counts as leafagechildscount from ");
				sqlstr.append("(SELECT MAX(grade) +1 AS grade from hr_org_history where catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("' and codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') aa,(SELECT COUNT(*) as counts FROM hr_org_history b WHERE b.catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("' and b.parentid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%' AND b.codeitemid = b.childid and b.codeitemid like '");
				sqlstr.append(code);
				sqlstr.append("%') bb");
				sqlstr.append(",(SELECT COUNT(*) as counts FROM hr_org_history org WHERE org.catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("' and (codeitemid <> childid) AND (org.codeitemid LIKE '");
				sqlstr.append(code);
				sqlstr.append("%') AND (NOT EXISTS (SELECT * FROM hr_org_history orge WHERE orge.catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("' and orge.codeItemId = org.childId))) cc ");
			}
		List rs=ExecuteSQL.executeMyQuery(sqlstr.toString(),conn);
		int[] pagesize=new int[2];
		if(!rs.isEmpty()&&rs.size()>0)
		{
			if("true".equalsIgnoreCase(parameter.getGraphaspect()))
			{
				LazyDynaBean orgbean=(LazyDynaBean)rs.get(0);				
				pagesize[0]=(1+Integer.parseInt(orgbean.get("grade")!=null && orgbean.get("grade").toString().length()>0?orgbean.get("grade").toString():"0"))*(Integer.parseInt(parameter.getCellwidth()) +Integer.parseInt(parameter.getCellheight())/6 + Integer.parseInt(parameter.getCellhspacewidth())*2) + parameter.getPagespacewidth()*2;
				pagesize[1]=(1+Integer.parseInt(orgbean.get("leafagechildscount").toString())) * (Integer.parseInt(parameter.getCellheight()) + Integer.parseInt(parameter.getCellvspacewidth())) + parameter.getPagespaceheight() *2;
			}else
			{
				LazyDynaBean orgbean=(LazyDynaBean)rs.get(0);
				pagesize[0]=(1+Integer.parseInt(orgbean.get("leafagechildscount").toString())) * (Integer.parseInt(parameter.getCellwidth()) + Integer.parseInt(parameter.getCellhspacewidth())) + parameter.getPagespacewidth() *2;
				pagesize[1]=(1+Integer.parseInt(orgbean.get("grade").toString()))*(Integer.parseInt(parameter.getCellheight())*7/6 + Integer.parseInt(parameter.getCellvspacewidth())*2) + parameter.getPagespaceheight()*2;
			}
			 if(pagesize[0]<180)
			 {
				 pagesize[0]=180;
			 }
			 if(pagesize[1]<180)
			{
				 pagesize[1]=180;
				//pagesize[1]=14400;
					//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors"),"",""));
			}
		}
		return pagesize;
	}

	   private Color getCellColor(String orgcolor,String infokind)
	   {
	    if("UN".equalsIgnoreCase(infokind)) {
            return new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));
        } else if("UM".equalsIgnoreCase(infokind)) {
            return new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));
        } else if("@K".equalsIgnoreCase(infokind)) {
            return new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));
        } else {
            return  new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));
        }
	   }
	   private Color getFontColor(String fontcolor,String infokind)
	   {
	    	 return  new Color(Integer.parseInt(fontcolor.substring(1,3),16),Integer.parseInt(fontcolor.substring(3,5),16),Integer.parseInt(fontcolor.substring(5,7),16));
	   }
	   private Color getShadowColor(String orgcolor,String infokind)
	   {	
	    if("UN".equalsIgnoreCase(infokind)) {
            return new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));
        } else if("UM".equalsIgnoreCase(infokind)) {
            return new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));
        } else if("@K".equalsIgnoreCase(infokind)) {
            return new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));
        } else {
            return  new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));
        }
	 
	   }
	   private Color getCellFrameColor(String orgcolor,String infokind)
	   {
	       	return  new Color(0,0,0);
	   }
	   public String ConverDBsql(String dbname)
		{
			String resultsql="";
			switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL: {
				resultsql=" + '(' + Convert(Varchar,count(" + dbname + "A01.a0100)) + '人)'" ;
				break;
			}
			case Constant.DB2: {
				resultsql=" + '(' + To_Char(count(" + dbname + "A01.a0100)) + '人)'" ;
				break;
			}
			case Constant.ORACEL: {
				resultsql=" || '(' || count(" + dbname + "A01.a0100) || '人)'" ;
				break;
			}
			}
			return resultsql;
		}
	   public String ConverDBsql(String table,String dbname)
		{
			String resultsql="";
			switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL: {
				resultsql=" + '(' + Convert(Varchar,count(" + dbname + "A01.a0100)) + '人)'" ;
				break;
			}
			case Constant.DB2: {
				resultsql=" + '(' + To_Char(count(" + dbname + "A01.a0100)) + '人)'" ;
				break;
			}
			case Constant.ORACEL: {
				resultsql=" || '(' || count(" + dbname + "A01.a0100) || '人)'" ;
				break;
			}
			}
			return resultsql;
		}
	   /***********************sun.xin*************************/
	   
}
