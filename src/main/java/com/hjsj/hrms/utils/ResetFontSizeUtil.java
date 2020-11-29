package com.hjsj.hrms.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * 
* @Title: ResetFontSizeUtils.java
* @Package com.hjsj.hrms.utils
* @Description: 重置字体大小公用类
* @author hej  
* @date 2018年1月16日 上午10:07:12
* @version V1.0
 */
public class ResetFontSizeUtil {

	public ResetFontSizeUtil() {
		
	}
	/**
	 * 依据传入的高宽,文字的字号字体样式重置字号
	 * 1、此处公共方法不涉及与业务有关的东西
	 * 2、比如`在不同的功能中指代换行符,这种符号需要在调用方法之前转换成换行即\n，否则按普通字符处理
	 * 3、此方法中认定\n为换行符，\r会被跳过
	 * 4、字符串中包含<>标签的话，暂时无法兼容，标签会当成普通字符串处理
	 * 5、由于word字体大小有极限，此方法最小只能把文字字号缩放到5（考虑到字号再小就看不清了）
	 * 6、此方法不能避免误差，由于word中字号6、7并没有，此种情况会把字号缩小到5.5、6.5导致字体会更小一点，这种情况无法规避。
	 * @param width 格子的高度
	 * @param height 格子的宽度
	 * @param fontValue 文字内容
	 * @param fontSize 文字字号
	 * @param fontName 文字字体
	 * @param fontEffect 文字样式 0 普通样式 1 粗体 2 斜体
	 * @return
	 */
	public int ResetFontSize(double width, double height, String fontValue, int fontSize, String fontName, int fontEffect) {
		String str=fontValue==null?"":fontValue;
		int nLine=getStrLines(str,fontSize,fontName,fontEffect,width);
    	int nChieght=CharHeight(fontSize,fontName,fontEffect);    	
    	double fCell=height/nChieght;	
    	int iCell=(int)Math.ceil(fCell);
    	if(nLine>=iCell){//计算文本行高不精准，相等时也默认缩小一号字体
    		while(nLine>=iCell){
    			fontSize=fontSize-1;
    			/*if(fontSize<=5)
    				break;*/
    			nLine=getStrLines(str,fontSize,fontName,fontEffect,width);//需要考虑不同字号字体的高度宽度不同
    			nChieght=CharHeight(fontSize,fontName,fontEffect);
    			fCell=height/nChieght;
    			iCell= (int)Math.ceil(fCell);
    		}
    	}	   
		return fontSize;
	}
	/**
	 * 依据文字的宽度,字体字号样式 得到整个字符串能够显示多少行
	 * @param str 文字内容
	 * @param fontSize 文字字号
	 * @param fontName 文字字体
	 * @param fontEffect 文字样式
	 * @param width 格子宽度
	 * @return
	 */
    public  int getStrLines(String str, int fontSize, String fontName, int fontEffect, double width) {
	   	 if(str==null||str.length()<=0)
			 return 0;
		 int nStart=0;
	     int result=0;
	     int iHzlen=0;//汉字的字符长度
	     ArrayList strList=new ArrayList();
	     char c;
	     int charLen=0;//字符长度
	     boolean bHz=false;
	     int i=0;
	     for(i=0;i<str.length();i++){
	    	 c =str.charAt(i);
	    	 if(c=='\r')
	    		 continue;
	    	 if(c=='\n'){
	    		 result++;//换行符          	     
	    	     charLen++;
	    	     charLen=0;
	    	     iHzlen=0;
	    	     if(i>nStart)
	    	     strList.add(str.substring(nStart, i));
	    	     nStart=i+1;
	    	     continue;
	    	 }
	    	 if(!reCHZ(c)){
	    		 charLen++;
	    		 //bHz=false;        		
	    	 }else{
	    		 iHzlen++;
	    		 charLen=charLen+2;  
	    		 bHz=true;
	    	 }   
	    	 int w = 0;
	    	 if(bHz)
	    		 if(width>=220){//暂时以宽度为220做参考
	    			 w = (int) Math.round((charLen*fontSize-iHzlen*((fontSize+fontSize*0.4F*width/220*(charLen-iHzlen*2)/iHzlen)/2)));
	    		 }else{
	    			 w = (int) Math.round((charLen*fontSize-iHzlen*((fontSize-fontSize*0.4F*Math.floor(220/width)*(charLen-iHzlen*2)/iHzlen)/2)));
	    		 }
	    	    
	    	 else
	    	     w = StrWidth(fontSize,fontEffect, str.substring(nStart, i+1), fontName);
	    	 if(w > width){
	    		 if(bHz){
	    			 strList.add(str.substring(nStart, i));
	        	     nStart=i;
	        	     result++; 
	    		 }else{
	    			 if(i>=1){
	    				 strList.add(str.substring(nStart,i-1));
	        		     nStart=i-1;
	        		     result++;
	    			 }        			 
	    		 }  
	    		 charLen=0;
	    		 iHzlen=0;
	    		 bHz=false; 
	    	 }
	     }
	     if(nStart<=str.length()){
	    	 result++;
	    	 strList.add(str.substring(nStart,i));
	     }   
	     return result;
    }
    /**
     * 判断文本是不是汉字
     * @param c
     * @return
     */
	private  boolean reCHZ(char c){
    	boolean isCorrect =false;
    	if((c>='0'&&c<='9')||(c>='a'&&c<='z')||(c>='A'&&c<='Z')){   
    		isCorrect =false;  //字母,   数字   
        }else if(c=='-'||c=='/'){
        	isCorrect =true; 
        }else{   
          if(Character.isLetter(c)){ //中文   
        	  isCorrect =true;
          }else{   //符号或控制字符   
        	  isCorrect =false; 
          }   
        } 
    	return isCorrect;
    }
	/**
	 * 计算文本宽度
	 * @param fontsize
	 * @param fontEffect
	 * @param s
	 * @return
	 */
	private int StrWidth(int fontSize,int fontEffect, String value, String fontName) {
	    int w = 0;
        Font font = new Font(fontName, fontEffect, fontSize);
        BufferedImage gg = new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = gg.createGraphics(); // 获得画布
        g.setFont(font);
        //此处方法获取到的是字符的宽度(无论字符串中是否包含汉字等占两个字符的,获取的都是按一个字符的宽度加起来)
        //将传进来的字符串转成字节
        int valueLength = value.length();
        byte [] arrayBytes = value.getBytes();
        int stringWidth = g.getFontMetrics().stringWidth(value);
        w = (int)Math.ceil(stringWidth*arrayBytes.length/valueLength);
	    return w;
	}
	/**
	 * 计算文本高度
	 * @param fontSize
	 * @param fontName
	 * @param fontEffect 
	 * @return
	 */
	public int CharHeight(int fontSize, String fontName, int fontEffect) {
	    int h = 0;
        Font font = new Font(fontName, fontEffect, fontSize);
        BufferedImage gg = new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = gg.createGraphics(); // 获得画布
        g.setFont(font);
        //此处方法获取到的是字符的高度(无论字符串中是否包含汉字等占两个字符的,获取的都是按一个字符的高度加起来)
        //所以此处乘了一个系数(次系数可能会不准) 暂时没有办法准确得到此系数
        h = (int)Math.ceil(g.getFontMetrics().getHeight()*1.4); // 每一行字的高度
	    return h;
	}
}
