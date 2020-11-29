/*
 * Created on 2006-5-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.general.orgmap;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PDFBaseBo {
	/**
	 * 生成字体样式,解决中文问题
	 * 
	 */
	public Font getFont(String fontEffect,int fontSize)
	{
		Font font=null;
		try
		{
			//字体效果 =0,=1 正常式样 =2,粗体 =3,斜体  =4,斜粗体
			BaseFont bfComic = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   //解决中文问题		
			if("2".equals(fontEffect))
			{
				font=new Font(bfComic,fontSize+2, Font.BOLD);
			}
			if("3".equals(fontEffect))
			{
				font=new Font(bfComic,fontSize+2, Font.ITALIC);
			}
			if("4".equals(fontEffect))
			{
				font=new Font(bfComic,fontSize+2, Font.BOLD | Font.ITALIC);
			}
			else
			{
				font=new Font(bfComic,fontSize+2, Font.NORMAL);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return font;
		
	}
	/**
	 * @param align
	 * @param cell
	 */
	public void setTextAlign(int align, PdfPCell cell) {
		/*  单元格内容的排列方式
		 * =0上左 =1上中  =2上右  =3下左  =4下中  =5下右 =6中左  =7中中 =8中右
		 */
		if(align==0)   
		{
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);    //基于最合适的
		}
		else if(align==1)
		{
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		}
		else if(align==2)
		{
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		}
		else if(align==3)
		{
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
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
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
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
  
}
