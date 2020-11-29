package com.hjsj.hrms.businessobject.general.orgmap;

import java.util.ArrayList;
import java.util.HashMap;

public class CompTextOnecell {
	 /**
     * 计算字体
     * @param width
     * @param fize
     * @param height
     * @param length
     * @return [0]字体大小[1]每行字数[2]一共几行
     */
	private final int svgCell=1;
	private final int pdfCell=2;
	public String[] oneLineWordCountSvg(String width,String fize,String height,int iLength)
    {
    	String [] arr=new String[3];
    	int ifontSize=Integer.parseInt(fize,10);
        int iHeight=Integer.parseInt(height,10);
        int iWidth=Integer.parseInt(width,10); 
        float fCount=iWidth/(ifontSize+2);
        int _wordcount=(int)fCount;
        int s_wordcount=_wordcount;  
        float fColscount=iLength/_wordcount;
        int _colscount=(int)fColscount; 
        int s_colscount=_colscount;
        int constant=7;    
        int i=0;
        int r=0;
        boolean isCorrect=false;
        while((ifontSize+constant)*_colscount>iHeight)
        {
          isCorrect=true;	         
          ifontSize--;
          i++;
          r++;      
          if(r>=3)
          {
             _wordcount++;
             fColscount=iLength/_wordcount;
             _colscount=(int)fColscount; 
             r=0;
          }      
          if(ifontSize<=0)
          {
             isCorrect=false;
             break;
          }  
          if(ifontSize<=0) {
              break;
          }
        }
        if(isCorrect)
        {
        	arr[0]=ifontSize+"";
        	arr[1]=_wordcount+"";
        	arr[2]=_colscount+"";
        }else
        {
        	ifontSize=Integer.parseInt(fize,10);
	        iHeight=Integer.parseInt(height,10);
	        iWidth=Integer.parseInt(width,10); 
        	fCount=iWidth/(ifontSize+2); 
        	_wordcount=(int)fCount;
        	fColscount=iLength/_wordcount;
            _colscount=(int)fColscount; 	            
            ifontSize=fizeWordSunx(_colscount,iHeight,ifontSize);
            arr[0]=ifontSize+"";
        	arr[1]=_wordcount+"";
        	arr[2]=_colscount+"";
        }
    	return arr;
    }
	private  int fizeWordSunx(int row,int height,int fize)
    {
      int constant=6;	      
      while(true)
      {
    	 if((fize+constant)*row<=height) {
             break;
         } else {
             fize--;
         }
      }  
      return fize;
    }	
	/**
     * 计算字体
     * @param width
     * @param fize
     * @param height
     * @param length
     * @return [0]字体大小[1]每行字数[2]一共几行
     */
    public String[] oneLineWordCountPdf(String width,String fize,String height,int iLength)
    {
    	String [] arr=new String[3];
    	int ifontSize=Integer.parseInt(fize,10);
        int iHeight=Integer.parseInt(height,10);
        int iWidth=Integer.parseInt(width,10); 
        float fCount=iWidth/(ifontSize+2);
        int _wordcount=(int)fCount;
        int s_wordcount=_wordcount;  
        float fColscount=iLength/_wordcount;
        int _colscount=(int)fColscount; 
        int s_colscount=_colscount;
        int constant=7;    
        int i=0;
        int r=0;
        boolean isCorrect=false;       
        while((ifontSize+constant)*_colscount>iHeight)
        {
        	/*if(iLength==11||iLength==12)
        		System.out.println((ifontSize+constant)*_colscount+"--"+ifontSize+"--"+_colscount+"--"+_wordcount);*/
        	
          isCorrect=true;	         
          ifontSize--;          
          i++;
          r++;      
          if(r>=3)
          {
        	 _wordcount++;       
        	 if(_wordcount*(ifontSize)>iWidth)
        	 {
        		 _wordcount--;
        	 }        		 
             fColscount=iLength/_wordcount;
             fColscount=Math.round(fColscount);
             _colscount=(int)fColscount; 
             r=0;
          }      
          if(ifontSize<=0)
          {
             isCorrect=false;
             break;
          }        
        }
        if(isCorrect)
        {
        	if(ifontSize>0) {
                ifontSize--;
            }
        	arr[0]=ifontSize+"";
        	arr[1]=_wordcount+"";
        	arr[2]=_colscount+"";
        }else
        {
        	ifontSize=Integer.parseInt(fize,10);
	        iHeight=Integer.parseInt(height,10);
	        iWidth=Integer.parseInt(width,10); 
        	fCount=iWidth/(ifontSize+2); 
        	_wordcount=(int)fCount;
        	fColscount=iLength/_wordcount;
            _colscount=(int)fColscount; 	            
            ifontSize=fizeWordSunx(_colscount,iHeight,ifontSize);
            arr[0]=ifontSize+"";
        	arr[1]=_wordcount+"";
        	arr[2]=_colscount+"";
        }
    	return arr;
    }
   /****************************************/
   /**
    * 
    * @param width
    * @param fize
    * @param height
    * @param iLength
    * @return
    */    
    public String[] oneTableWordCountSvg(int rWidth,int rHeight,String str,int fize)
    {
    	String [] arr=new String[3];
    	int[] nLines=getLines(str,fize,rWidth,this.svgCell);
    	int nLine=nLines[0];
    	int _wordcount=nLines[1];
    	float nChieght=fize+(float)(fize/3);    	
    	float fCell=rHeight/nChieght;	
    	if(nLine>fCell)
    	{
    		while(nLine>fCell)
    		{
    			fize=fize-1;
    			if(fize<=0) {
                    break;
                }
    			nLines=getLines(str,fize,rWidth,this.svgCell);
    			nLine=nLines[0];
    			_wordcount=nLines[1];
    			nChieght=fize+(float)(fize/3);
    			fCell=rHeight/nChieght;
    		}
    	}
    	arr[0]=fize+"";
    	arr[1]=_wordcount+"";
    	if(nLine>0) {
            nLine=nLine-1;
        } else {
            nLine=0;
        }
    	arr[2]=nLine+"";
    	return arr;
    }
    private  int[] getLines(String str,int fize,int width,int tableType)
    {
    	 int[] iWords=new int[2];
    	 
    	 if(str==null||str.length()<=0)
    	 {
    		 iWords[0]=0;
    		 iWords[1]=0;
    		 return iWords;
    	 }
    	 int nStart=0;
         int result=0;
         int iHzlen=0;
         int _wordcount=0;
         String word="";
         ArrayList strList=new ArrayList();
         char c;
         int charLen=0;
         boolean bHz=false;
         int i=0;
         for(i=0;i<str.length();i++)
         {
        	 c =str.charAt(i); //chr(10)表示换行        	
        	 /*if(c=='\n')//chr(13)表示换行
        		 continue;
        	 if(c=='\r')  //chr(10)表示换行
        	 {   
        		 result++;//换行符          	     
        	     charLen++;
        	     charLen=0;
        	     iHzlen=0;
        	     if(i>nStart)
        	     strList.add(str.substring(nStart, i));
        	     nStart=i+1;
        	     continue;
        	 }*/
        	 if(!reCHZ(c))
        	 {
        		 charLen++;
        		 bHz=false;        		
        	 }else
        	 {
        		 iHzlen++;
        		 charLen=charLen+2;  
        		 bHz=true;
        	 }  
        	 switch(tableType)
        	 {
        	     case svgCell:
        	     {
        	    	 if(charLen*fize-iHzlen*(fize/2)>width)
                	 {
                		 if(bHz)
                		 {
                			 strList.add(str.substring(nStart, i));
                			 word=str.substring(nStart, i);
                    	     nStart=i;
                    	     result++; 
                    	     if(_wordcount<word.length()) {
                                 _wordcount=word.length();
                             }
                		 }else
                		 {
                			 strList.add(str.substring(nStart,i-1));
                			 word=str.substring(nStart, i);
                		     nStart=i-1;
                		     result++;
                		 } 
                		 charLen=0;
                		 iHzlen=0;
                	 }
        	    	 break;
        	     }
        	     case pdfCell:
        	     {
        	    	 if(charLen*fize-iHzlen*fize>width)
                	 {
                		
                		 if(bHz)
                		 {
                			 strList.add(str.substring(nStart, i));
                			 word=str.substring(nStart, i);
                    	     nStart=i;
                    	     result++; 
                    	     if(_wordcount<word.length()) {
                                 _wordcount=word.length();
                             }
                		 }else
                		 {
                			 strList.add(str.substring(nStart,i-1));
                			 word=str.substring(nStart, i);
                		     nStart=i-1;
                		     result++;
                		 }  
                		 charLen=0;
                		 iHzlen=0;
                	 }
        	    	 break;
        	     }
        	 }
        	 
         }
         if(nStart<=str.length())
         {
        	 result++;
        	 strList.add(str.substring(nStart,i));
         }  
         iWords[0]=result;
         iWords[1]=_wordcount;
         return iWords;
    }
    /**
     * 字符串在一个格子每行的字数
     * @param rWidth
     * @param rHeight
     * @param str
     * @param fize
     * @return
     */
    public HashMap oneTableTextLineSvg(int rWidth,int rHeight,String str,int fize)
    {
    	HashMap map=new HashMap();
    	ArrayList strList=getTextEachLines(str,fize,rWidth,this.svgCell);
    	int nLine=0;
    	if(strList!=null&&strList.size()>0) {
            nLine=strList.size();
        }
    	float nChieght=fize+(float)(fize/3);    	
    	float fCell=rHeight/nChieght;	
    	if(nLine>fCell)
    	{
    		while(nLine>fCell)
    		{
    			fize=fize-1;
    			if(fize<=0) {
                    break;
                }
    			strList=getTextEachLines(str,fize,rWidth,this.svgCell);
    			if(strList!=null&&strList.size()>0) {
                    nLine=strList.size();
                } else {
                    break;
                }
    			nChieght=fize+(float)(fize/3);
    			fCell=rHeight/nChieght;
    		}
    	}
    	map.put("fize", fize+"");
    	map.put("wordList", strList);
    	return map;
    }
    /**
     * 字符串在一个格子每行的字数
     * @param rWidth
     * @param rHeight
     * @param str
     * @param fize
     * @return
     */
    public HashMap oneTableTextLinePDF(String  width,String height,String str,String fize)
    {
    	HashMap map=new HashMap();
    	int ifontSize=Integer.parseInt(fize,10);
        int iHeight=Integer.parseInt(height,10);
        int iWidth=Integer.parseInt(width,10); 
    	ArrayList strList=getTextEachLines(str,ifontSize,iWidth,this.svgCell);
    	int nLine=0;
    	if(strList!=null&&strList.size()>0) {
            nLine=strList.size();
        }
    	float nChieght=ifontSize+(float)(ifontSize/3);    	
    	float fCell=iHeight/nChieght;	
    	if(nLine>fCell)
    	{
    		while(nLine>fCell)
    		{
    			ifontSize=ifontSize-1;
    			if(ifontSize<=0) {
                    break;
                }
    			strList=getTextEachLines(str,ifontSize,iWidth,this.svgCell);
    			if(strList!=null&&strList.size()>0) {
                    nLine=strList.size();
                } else {
                    break;
                }
    			nChieght=ifontSize+(float)(ifontSize/3);
    			fCell=iHeight/nChieght;
    		}
    	}
    	map.put("fize", ifontSize+"");
    	map.put("wordList", strList);
    	return map;
    }
    /**
     * 每行多少字数
     * @param str
     * @param fize
     * @param width
     * @param tableType
     * @return
     */
    private  ArrayList getTextEachLines(String str,int fize,int width,int tableType)
    {
    	 ArrayList strList=new ArrayList();
    	 if(str==null||str.length()<=0)
    	 {
    		return strList;
    	 }
    	 int nStart=0;
         int result=0;
         int iHzlen=0;         
         char c;
         int charLen=0;
         boolean bHz=false;
         int i=0;
         for(i=0;i<str.length();i++)
         {
        	 c =str.charAt(i); //chr(10)表示换行        	
        	 /*if(c=='\n')//chr(13)表示换行
        		 continue;
        	 if(c=='\r')  //chr(10)表示换行
        	 {   
        		 result++;//换行符          	     
        	     charLen++;
        	     charLen=0;
        	     iHzlen=0;
        	     if(i>nStart)
        	     strList.add(str.substring(nStart, i));
        	     nStart=i+1;
        	     continue;
        	 }*/
        	 if(!reCHZ(c))
        	 {
        		 charLen++;
        		 bHz=false;        		
        	 }else
        	 {
        		 iHzlen++;
        		 charLen=charLen+2;  
        		 bHz=true;
        	 }  
        	 switch(tableType)
        	 {
        	     case svgCell:
        	     {
        	    	 if(charLen*fize-iHzlen*(fize)>width-(width/10))
                	 {
                		 if(bHz)
                		 {
                			 strList.add(str.substring(nStart, i));
                    	     nStart=i;
                    	     result++; 
                    	    
                		 }else
                		 {
                			 strList.add(str.substring(nStart,i-1));
                		     nStart=i-1;
                		     result++;
                		 } 
                		 charLen=0;
                		 iHzlen=0;
                	 }
        	    	 break;
        	     }
        	     case pdfCell:
        	     {
        	    	 if(charLen*fize-iHzlen*fize>width)
                	 {
                		
                		 if(bHz)
                		 {
                			 strList.add(str.substring(nStart, i));                			
                    	     nStart=i;
                    	     result++;                     	   
                		 }else
                		 {
                			 strList.add(str.substring(nStart,i-1));                			
                		     nStart=i-1;
                		     result++;
                		 }  
                		 charLen=0;
                		 iHzlen=0;
                	 }
        	    	 break;
        	     }
        	 }
        	 
         }
         if(nStart<=str.length())
         {
        	 result++;
        	 strList.add(str.substring(nStart,i));
         }  
         return strList;
    }
    /**
     * 判断汉字
     * @param c
     * @return
     */
    private  boolean reCHZ(char c)
    {
    	boolean isCorrect =false;
    	if((c>='0'&&c<='9')||(c>='a'&&c<='z')||(c>='A'&&c<='Z'))
        {   
          //字母,   数字   
    		isCorrect =false;  
        }else{   
          if(Character.isLetter(c))
          {   //中文   
        	  isCorrect =true; 
             //System.out.println(Character.isLetter(c));
          }else{   //符号或控制字符   
        	  isCorrect =false; 
          }   
        } 
    	return isCorrect;
    }
    /**
     * pdf取字体大小
     * @param rWidth
     * @param rHeight
     * @param valueList
     * @param fize
     * @return
     */
    public String[]  oneTableWordCountPDF(String  width,String height,String str,String fize){
    	String [] arr=new String[3];
    	int ifontSize=Integer.parseInt(fize,10);
        int iHeight=Integer.parseInt(height,10);
        int iWidth=Integer.parseInt(width,10); 
        int[] nLines=getLines(str,ifontSize,iWidth,this.pdfCell);
    	int nLine=nLines[0];
    	int _wordcount=nLines[1];    	
    	int nChieght=ifontSize+1;    	
    	float fCell=iHeight/nChieght;	
    	int iCell=Math.round(fCell);
    	if(nLine>iCell)
    	{
    		while(nLine>iCell)
    		{
    			ifontSize=ifontSize-1;
    			if(ifontSize<=0) {
                    break;
                }
    			nLines=getLines(str,ifontSize,iWidth,this.pdfCell);
    			nLine=nLines[0];
    			_wordcount=nLines[1];
    			nChieght=ifontSize+1;
    			fCell=iHeight/nChieght;
    			iCell=Math.round(fCell);
    		}
    	}		
    	arr[0]=ifontSize+"";
    	arr[1]=_wordcount+"";
    	if(nLine>0) {
            nLine=nLine-1;
        } else {
            nLine=0;
        }
    	arr[2]=nLine+"";
    	return arr;
	}    
}
