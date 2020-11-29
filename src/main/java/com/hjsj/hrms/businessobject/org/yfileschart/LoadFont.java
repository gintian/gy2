package com.hjsj.hrms.businessobject.org.yfileschart;

import com.hjsj.hrms.constant.FontFamilyType;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

/**
 * 通过字体文件创建字体，解决系统某字体不存在时机构图显示错乱的问题
 * 2015-03-12
 * @author guodd
 *
 */
public class LoadFont
{
	
    private static Font loadFont(String fontFileName,int style, float fontSize)  //第一个参数是外部字体名，第二个是字体大小
    {
    	Font dynamicFontPt = null;
        try
        {
        	if(fontFileName != null && !"".equals(fontFileName)) {
        		File file = new File(fontFileName);
        		FileInputStream aixing = new FileInputStream(file);
        		Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, aixing);
        		dynamicFontPt = dynamicFont.deriveFont(style, fontSize);
        		aixing.close();
        		return dynamicFontPt;
        	}
        }
        catch(Exception e)//异常处理
        {
            //e.printStackTrace();
        }
        
        if (dynamicFontPt ==  null)  //zhangcq 2016-5-14 字体不存在时则返回宋体
        {
            dynamicFontPt = new Font("宋体",style, new Float(fontSize).intValue());
        }
        
        return dynamicFontPt;
    }
    
    /**
     * 获取字体对象
     * 
     * @param fontFamily 字体名(支持FontFamilyType.getFontFamilyTTF中的字体)
     * @param fontStyle  字体样式 斜体、粗体等
     * @param fontSize   字体大小
     * @return  java.awt.Font
     */
    public static Font getFont(String fontFamily,int fontStyle,float fontSize){
        String root = "";
        Font font = null;
        try {
        	root = FontFamilyType.getFontFamilyTTF(fontFamily);
        	font = LoadFont.loadFont(root,fontStyle,fontSize);//调用
        }catch(Exception e){
        	//e.printStackTrace();
        }
        
        return font;//返回字体
    	
    	
    }
}