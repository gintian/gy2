/*
 * Created on 2005-5-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.ykcard;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-5-10:13:43:18</p>
 * @author Administrator
 * @version 1.0
 * 
 */
public class MadeFontsizeToCell {
	private String auto;
	private String browser;
	public String getAuto() {
		return auto;
	}
	public void setAuto(String auto) {
		this.auto = auto;
	}
	private String outType="";
	
	public String getOutType() {
		return outType;
	}
	public void setOutType(String outType) {
		this.outType = outType;
	}
	public MadeFontsizeToCell() {
	}
	public MadeFontsizeToCell(String browser) {
		this.browser=browser;
	}
	//BufferedImage gg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	int nLen = 0;
	int maxLen = 0;
	//完成字体自适应单元格的大小以显示不同的字体大小返回字体的大小
	public int ReDrawLitterRect(
		int rWidth,
		int rHeight,
		String strData,
		int fontsize,
		String fontname,
		String distinguish,String strType,int slope) {
		int n = 1;    //行数
		/**暂时去掉字体自动缩放，chenmengqing*/		
		if(this.auto!=null&& "0".equals(this.auto)) {
            return fontsize;
        }
		String midStr = "", maxStr = "";
		/*if(gg!=null)
		{
			Graphics g = gg.createGraphics();   //获得画布
			Font font = new Font(fontname, Font.PLAIN, fontsize);
			g.setFont(font);                   //设置画布的字体		
			if(this.outType!=null&&outType.equals("pdf"))
			{
				float hf=rHeight;
				n = (int)hf / (g.getFontMetrics().getHeight() + 6);
			}
			else
			{
				n = rHeight / (g.getFontMetrics().getHeight() + 6);
			}
			if (n != 0)
				nLen = n * (rWidth - 6);
			else
				nLen = rWidth - 6;		
			
			int sizeint = 5;
			boolean dateTypeB=false;
			if(strType!=null&&strType.equalsIgnoreCase("D"))
			{
				switch(slope)                                 //日期的现实格式
				{
					case 6:  //1991.12.3
					{
						strData="1991.12.03";
						dateTypeB=true;
						nLen=rWidth - 6;
						break;
					}
					case 7:   //99.2.23
					{
						strData="91.12.03";
						dateTypeB=true;
						nLen=rWidth - 6;
						break;
					}
					case 8:  //1991.2
					{
						strData="1991.12";
						dateTypeB=true;
						nLen=rWidth - 6;
						break;
					}
					case 9:   //1991.02
					{			
						strData="1991.12";
						dateTypeB=true;
						nLen=rWidth - 6;
						break;
					}
					case 10:  //98.2
					{
						strData="91.12";
						dateTypeB=true;
						nLen=rWidth - 6;
						break;
					}
					case 11:  //98.02
					{
						strData="91.12";
						dateTypeB=true;
						nLen=rWidth - 6;
						break;
					}
					case 24:    //1991.01.01
					{
						strData="1991.12.03";
						dateTypeB=true;
						nLen=rWidth - 6;
						break;
					}
				}
			}
			if(dateTypeB)
			{
				//System.out.println(g.getFontMetrics().stringWidth(strData));
				while(nLen<g.getFontMetrics().stringWidth(strData))
				{
					fontsize -= 1;
					font = new Font(fontname, Font.PLAIN, fontsize);
					g.setFont(font);
				}
				if (distinguish != null && distinguish.equals("800"))
					fontsize = Math.round(((float) fontsize * 800) / 1024) + 1;		
				return fontsize; 
			}
			boolean isCorrect=false;		
			while (strData != null&& (nLen<= (g.getFontMetrics().stringWidth(strData)+ 6 + (strData.length()) * sizeint))) 
			{
				sizeint -= 1;
				if (sizeint < 0)
					sizeint = 0;
				fontsize -= 1;
				font = new Font(fontname, Font.PLAIN, fontsize);
				g.setFont(font);
				n = rHeight / (g.getFontMetrics().getHeight() + 6);			
				if (n != 0)
					nLen = n * (rWidth - 6);
				else
					nLen = rWidth - 6;
			}
			if (strData != null) {
				maxLen = 0;
				maxStr = "";
				int row=0;
				StringTokenizer Stok = new StringTokenizer(strData, "`");
				for (; Stok.hasMoreTokens();) {
					midStr = Stok.nextToken();
					nLen = g.getFontMetrics().stringWidth(midStr);
					if (nLen > maxLen) {
						maxLen = nLen;
						maxStr = midStr;
					}
					row++;
				}
				*//**换行高度大于实际高度,的计算*//*
				if(row>1)
				{
					font = new Font(fontname, Font.PLAIN, fontsize);
					float fh=(g.getFontMetrics().getHeight()+6)*row;
					int diff=(int)fh/rHeight;
					if(fh>rHeight)
					{
						for(int s=0;s<diff;s++)
						{
							fontsize=fontsize-1;
							font = new Font(fontname, Font.PLAIN, fontsize);
							fh=(g.getFontMetrics().getHeight()+6)*row;
							if(fh<=rHeight)
								break;
						}
					}
					
				}
			}
			
			if (distinguish != null && distinguish.equals("800"))
				fontsize = Math.round(((float) fontsize * 800) / 1024) + 1;		
		}else
		{*/
			if(this.auto!=null&& "0".equals(this.auto)) {
                return fontsize;
            }
			/*if(context.indexOf("派往国家")!=-1)
			{	
				System.out.println(context);
				System.out.println(fontSize+"--"+width+"---"+height);
			}else if(context.indexOf("族")!=-1)
			{
				System.out.println(context);
				System.out.println(fontSize+"--"+width+"---"+height);
			}*/
			//纵适应
			int afontSize=fontsize;	
			String[] temps=strData.split("`");
			int constant=7;
			if(temps.length>=2&&temps.length<5) {
                constant=6;
            } else if(temps.length>=5&&temps.length<8) {
                constant=5;
            } else if(temps.length>=8) {
                constant=4;
            }
			
			while(true)
			{
				if((afontSize+constant)*temps.length<=rHeight) {
                    break;
                } else {
                    afontSize--;
                }
			}
			
			//横适应
			int maxNum=0;
			for(int i=0;i<temps.length;i++)
			{
				int a_max=0;
				if(temps[i].getBytes().length%2==1) {
                    a_max=temps[i].getBytes().length/2+1;
                } else {
                    a_max=temps[i].getBytes().length/2;
                }
				if(a_max>maxNum)
				{
						maxNum=a_max;
				}
			}			
			//constant=8;
			//if(maxNum>2)
			constant=6;		
			/*if(maxNum*fontSize>=width)
				constant=3;*/
	        /*if(maxNum>=4&&maxNum<8)
				constant=3;
			else if(maxNum>=8)
				constant=2;*/
			if(maxNum<=0) {
                return fontsize;
            }
			if(afontSize*maxNum<=rWidth)
			{
				int i_base=(int)(rWidth/maxNum);
				float m_bass=rWidth%maxNum;
				i_base=i_base+1;
				if(i_base>20) {
                    constant=6;
                } else if(i_base>afontSize)
				{
					constant=i_base-afontSize;
					if(constant>=5&&m_bass!=0&&i_base<16) {
                        constant=4;
                    }
				}
				else {
                    constant=3;
                }
			}else
			{
				if(maxNum>=10&&maxNum<20) {
                    constant=3;
                } else if(maxNum>=20&&maxNum<40) {
                    constant=4;
                } else  if(maxNum>=40) {
                    constant=2;
                }
			}
			while(true)
			{
				if((afontSize+constant)*maxNum<=rWidth) {
                    break;
                } else {
                    afontSize--;
                }
			}
			fontsize= afontSize;
		//}
		return fontsize;
	}
	//显示的内容在单元格的方向位置
	public String[] getAlign(String ali) {
		String[] align = new String[2];
		if ("0".equals(ali)) {
			align[0] = "left";
			align[1] = "top";
		} else if ("1".equals(ali)) {
			align[0] = "center";
			align[1] = "top";
		} else if ("2".equals(ali)) {
			align[0] = "right";
			align[1] = "top";
		} else if ("3".equals(ali)) {
			align[0] = "left";
			align[1] = "bottom";
		} else if ("4".equals(ali)) {
			align[0] = "center";
			align[1] = "bottom";
		} else if ("5".equals(ali)) {
			align[0] = "right";
			align[1] = "bottom";
		} else if ("6".equals(ali)) {
			align[0] = "left";
			align[1] = "center";
		} else if ("7".equals(ali)) {
			align[0] = "center";
			align[1] = "middle";
		} else if ("8".equals(ali)) {
			align[0] = "right";
			align[1] = "middle";
		}
		return align;
	}
	public int ReChangeRowNum(int rWidth,String strData,int fontsize,String fontname,String distinguish,String strType)
	{
		int rows=1;
		/*Graphics g = gg.createGraphics();   //获得画布
		Font font = new Font(fontname, Font.PLAIN, fontsize);
		g.setFont(font);                    //设置画布的字体
		int sizeint = 5;
		int strwidth=g.getFontMetrics().stringWidth(strData)+ 6 + (strData.length()) * sizeint;*/
		//int strwidth=g.getFontMetrics().stringWidth(strData);
		strData = strData==null?"":strData;
		String strDatas[]=strData.split("<br>");
		if(strDatas!=null&&strDatas.length>1)
		{
			for(int i=0;i<strDatas.length;i++)
			{
				String str=strDatas[i];
				int strwidth=getStrWidth(fontsize,rWidth,str);
				int strwidth2=0;		
				if(strType!=null&& "D".equalsIgnoreCase(strType)) {
                    strwidth2=getStrWidth(fontsize,rWidth,str);
                } else {
                    strwidth2=((fontsize/6*2)+fontsize)*str.length()+10;
                }
				if(strwidth2>=rWidth)
				{
					int row=strwidth/rWidth;
					if(strwidth%rWidth!=0) {
                        row++;
                    }
					rows=rows+row;
				}else {
                    rows++;
                }
			}
			rows--;
		}else
		{
			int strwidth=getStrWidth(fontsize,rWidth,strData);
			int strwidth2=0;		
			if(strType!=null&& "D".equalsIgnoreCase(strType)) {
                strwidth2=getStrWidth(fontsize,rWidth,strData);
            } else {
                strwidth2=((fontsize/6*2)+fontsize)*strData.length()+10;
            }
			if(strwidth2>=rWidth&&rWidth!=0)//liuy 2015-6-16 10266
			{
				rows=strwidth/rWidth;
				if(strwidth%rWidth!=0) {
                    rows++;
                }
			}	
		}
		
		return rows;
	}
	public int ReHeight(int fontsize,String fontname)
	{
		/*if(gg!=null)
		{
			Graphics g = gg.createGraphics();   //获得画布
			Font font = new Font(fontname, Font.PLAIN, fontsize);
			g.setFont(font);                    //设置画布的字体
			return g.getFontMetrics().getHeight()+6;
		}else
		{*/
			return fontsize+6;
		//}
	}	
	/**
	 * 横适应纵适应
	 * @param fontSize
	 * @param width
	 * @param height
	 * @param context
	 * @return
	 */
	public int  getFitFontSize(int fontSize,float width,float height,String context)
	{
        if(this.auto!=null&& "0".equals(this.auto)) {
            return fontSize;
        }
		/*if(context.indexOf("派往国家")!=-1)
		{	
			System.out.println(context);
			System.out.println(fontSize+"--"+width+"---"+height);
		}else if(context.indexOf("族")!=-1)
		{
			System.out.println(context);
			System.out.println(fontSize+"--"+width+"---"+height);
		}*/
		//纵适应
		int afontSize=fontSize;	
		String[] temps=context.split("`");
		int constant=7;
		if(temps.length>=2&&temps.length<5) {
            constant=6;
        } else if(temps.length>=5&&temps.length<8) {
            constant=5;
        } else if(temps.length>=8) {
            constant=4;
        }
		
		while(true)
		{
			if((afontSize+constant)*temps.length<=height) {
                break;
            } else {
                afontSize--;
            }
		}
		
		//横适应
		int maxNum=0;
		for(int i=0;i<temps.length;i++)
		{
			int a_max=0;
			if(temps[i].getBytes().length%2==1) {
                a_max=temps[i].getBytes().length/2+1;
            } else {
                a_max=temps[i].getBytes().length/2;
            }
			if(a_max>maxNum)
			{
					maxNum=a_max;
			}
		}			
		//constant=8;
		//if(maxNum>2)
		constant=6;		
		/*if(maxNum*fontSize>=width)
			constant=3;*/
        /*if(maxNum>=4&&maxNum<8)
			constant=3;
		else if(maxNum>=8)
			constant=2;*/
		if(afontSize*maxNum<=width)
		{
			int i_base=0;
			float m_bass=0;
			if(maxNum!=0) {
				i_base=(int)(width/maxNum);
				m_bass=width%maxNum;
			}
			i_base=i_base+1;
			if(i_base>20) {
                constant=6;
            } else if(i_base>afontSize)
			{
				constant=i_base-afontSize;
				if(constant>=5&&m_bass!=0&&i_base<16) {
                    constant=4;
                }
			}
			else {
                constant=3;
            }
		}else
		{
			if(maxNum>=10&&maxNum<20) {
                constant=3;
            } else if(maxNum>=20) {
                constant=4;
            }
		}
		while(true)
		{
			if((afontSize+constant)*maxNum<=width) {
                break;
            } else {
                afontSize--;
            }
		}
		return afontSize;
	}
	private int getStrWidth(int fontSize,float width,String context)
	{
		int maxNum=0;
		String[] temps=context.split("`");
		for(int i=0;i<temps.length;i++)
		{
			int a_max=0;
			if(temps[i].getBytes().length%2==1) {
                a_max=temps[i].getBytes().length/2+1;
            } else {
                a_max=temps[i].getBytes().length/2;
            }
			if(a_max>maxNum)
			{
					maxNum=a_max;
			}
		}
		int afontSize=fontSize;	
		int constant=7;
		if(maxNum<=0) {
            return 0;
        }
		if(afontSize*maxNum<=width)
		{
			int i_base=(int)(width/maxNum);
			float m_bass=width%maxNum;
			i_base=i_base+1;
			if(i_base>20) {
                constant=6;
            } else if(i_base>afontSize)
			{
				constant=i_base-afontSize;
				if(constant>=5&&m_bass!=0&&i_base<16) {
                    constant=4;
                }
			}
			else {
                constant=3;
            }
		}else
		{
			if(maxNum>=10&&maxNum<20) {
                constant=3;
            } else if(maxNum>=20) {
                constant=4;
            }
		}
		return afontSize*maxNum;
	}
	public int ReDrawLitterRect(int rWidth,int rHeight,ArrayList valueList,int fize){
		if(valueList==null&&valueList.size()<=0) {
            return fize;
        }
		if(this.auto!=null&& "0".equals(this.auto)) {
            return fize;
        }
		 
		if(valueList.size()==1)//单行字可换行处理
		{
			String str=valueList.get(0)!=null?valueList.get(0).toString():"";
			int nLine=0;
			if("Firefox".equals(browser)) {
                nLine=getLines(str,fize,rWidth-30);
            } else {
                nLine=getLines(str,fize,rWidth);
            }
	    	float nChieght=fize+(float)(fize/3);    	
	    	float fCell=rHeight/nChieght;	
	    	//System.out.println(nLine+"########"+fCell+"--"+rHeight+"--"+nChieght);
	    	//int iCell=Math.round(fCell);
	    	if(nLine>fCell)
	    	{
	    		while(nLine>fCell)
	    		{
	    			fize=fize-1;
	    			if(fize<=0) {
                        break;
                    }
	    			nLine=getLines(str,fize,rWidth);
	    			nChieght=fize+(float)(fize/3);
	    			fCell=rHeight/nChieght;
	    			//iCell=Math.round(fCell);
	    			//System.out.println("fize---"+fize);
	    		}
	    	}
		}else
		{
			String fontStr="";
			String mStr="";
			for(int j=0;j<valueList.size();j++)
            {
               fontStr+=valueList.get(j)!=null?valueList.get(j).toString():"";
               fontStr=fontStr+"`";
            }	
			char c;
			StringBuffer sBuf=new StringBuffer();
			for(int s=0;s<fontStr.length();s++)
            {
            	 c =fontStr.charAt(s); //chr(10)表示换行
            	 if(c=='`') {
                     c='\r';
                 }
            	 sBuf.append(c);
            }
			fize=ReOneRowDrawLitterRect(rWidth,rHeight,sBuf.toString(),fize);
		}
    	return fize;
	}
	
	/**
	 * 计算文字高度
	 * @param fontsize
	 * @return
	 */
	private int CharHeight(int fontsize) {
	    int h = 0;  // fontsize+(int)(fontsize/3)
        int feffect = Font.PLAIN;
        Font font = new Font("宋体", feffect, fontsize);
        BufferedImage gg = new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = gg.createGraphics(); // 获得画布
        g.setFont(font);
        h = g.getFontMetrics().getHeight(); // 每一行字的高度
        //int awidth = g.getFontMetrics().charWidth('汉');//fontSize;  // 汉字宽

	    return h;
	}
	
	/**
	 * 计算文本宽度
	 * @param fontsize
	 * @param s
	 * @return
	 */
	private int StrWidth(int fontsize, String s) {
	    int w = 0;
        int feffect = Font.PLAIN;
        Font font = new Font("Arial", feffect, fontsize);
        
//        FontMetrics metrics = new FontMetrics(font){};
//        Rectangle2D bounds = metrics.getStringBounds(s, null);
//        w = (int) bounds.getWidth();
        
        BufferedImage gg = new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = gg.createGraphics(); // 获得画布
        g.setFont(font);
        w = g.getFontMetrics().stringWidth(s);
	    return w;
	}
	
	public int ReOneRowDrawLitterRect(int rWidth,int rHeight,String str,int fize){
		if(str==null&&str.length()<=0) {
            return fize;
        }
		if(this.auto!=null&& "0".equals(this.auto)) {
            return fize;
        }
		int nLine=getLines(str,fize,rWidth);
	    int nChieght = CharHeight(fize);    	
	    float fCell=rHeight/nChieght;	
	    	//System.out.println(nLine+"########"+fCell+"--"+rHeight+"--"+nChieght);
	    int iCell=Math.round(fCell);
	    if(nLine>iCell)
	    {
	    		while(nLine>iCell)
	    		{
	    			fize=fize-1;
	    			if(fize<=0) {
                        break;
                    }
	    			nLine=getLines(str,fize,rWidth);
	    			nChieght = CharHeight(fize);
	    			fCell=rHeight/nChieght;
	    			iCell=Math.round(fCell);
	    			//System.out.println("fize---"+fize);
	    		}
	    }
    	return fize;
	}
	private int getCharLen(String str)
	{
		int charLen=0;      
        char c;
        int i=0;
        for(i=0;i<str.length();i++)
        {
       	    c =str.charAt(i); //chr(10)表示换行
       	    if(!reCHZ(c))
       	    {
       		     charLen++;       	    	  		
            }else
       	    {
       		    charLen=charLen+2;         		    
       	     } 
        }        
		return charLen;
	}
	private  boolean reCHZ(char c)
    {
    	boolean isCorrect =false;
    	if((c>='0'&&c<='9')||(c>='a'&&c<='z')||(c>='A'&&c<='Z'))
        {   
          //字母,   数字   
    		isCorrect =false;  
        }else if(c=='-'||c=='/'){
        	isCorrect =true; 
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
    private  int getLines(String str,int fize,int width)
    {
    	 if(str==null||str.length()<=0) {
             return 0;
         }
    	 boolean bView=false;
    	 str=str.replaceAll("<br>", "    ");    	 
    	 //System.out.println(str);
    	 /*if(str.indexOf("政协北京市东城区委员会联络二处主任科员")!=-1)
    		 bView=true;*/
    	 int nStart=0;
         int result=0;
         int iHzlen=0;
         ArrayList strList=new ArrayList();
         char c;
         int charLen=0;
         boolean bHz=false;
         int i=0;
         for(i=0;i<str.length();i++)
         {
        	 c =str.charAt(i); //chr(10)表示换行
        	// System.out.println(c);
        	 if(c=='\n')//chr(13)表示换行
             {
                 continue;
             }
        	 if(c=='\r')  //chr(10)表示换行
        	 {   
        		 result++;//换行符          	     
        	     charLen++;
        	     charLen=0;
        	     iHzlen=0;
        	     if(i>nStart) {
                     strList.add(str.substring(nStart, i));
                 }
        	     nStart=i+1;
        	     continue;
        	 }
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
        	// System.out.println(c+"---"+charLen*fize+"----"+width);
        	// if(charLen*fize>width-fize)
        	 //int t=iHzlen/2;
        	 int w = 0;
        	 if(bHz) {
                 w = charLen*fize-iHzlen*(fize/2);
             } else {
                 w = StrWidth(fize, str.substring(nStart, i+1));  // FIXME 计算汉字不准确?
             }
        	 if(w > width)
        	 {
        		 //if(bView)
        		 //  System.out.println("换行-----"+(charLen*fize)+"---"+charLen+"--"+str.substring(nStart, i)+"---"+str.substring(nStart, i).getBytes().length);
        		 if(bHz)
        		 {
        			 strList.add(str.substring(nStart, i));
            	     nStart=i;
            	     result++; 
        		 }else
        		 {
        			 if(i>=1)
        			 {
        				 strList.add(str.substring(nStart,i-1));
            		     nStart=i-1;
            		     result++;
        			 }        			 
        		 }  
        		 charLen=0;
        		 iHzlen=0;
        	 }
         }
         if(nStart<=str.length())
         {
        	 result++;
        	 strList.add(str.substring(nStart,i));
         }     
         /*if(bView)
         {
        	 for(int s=0;s<strList.size();s++)
        	 {
        		  System.out.println(fize+"---"+ s+"-----"+strList.get(s));
        	 }
         }  */    
         return result;
    }
    
    /**
     * pdf取字体大小
     * @param rWidth
     * @param rHeight
     * @param valueList
     * @param fize
     * @return
     */
    public int RePDFDrawLitterRect(float rWidth,float rHeight,ArrayList valueList,int fize){
		if(valueList==null&&valueList.size()<=0) {
            return fize;
        }
		if(this.auto!=null&& "0".equals(this.auto)) {
            return fize;
        }
		if(valueList.size()==1)//单行字可换行处理
		{
			String str=valueList.get(0)!=null?valueList.get(0).toString():"";
			int nLine=getPDFLines(str,fize,rWidth);
	    	int nChieght=fize+1;    	
	    	float fCell=rHeight/nChieght;	
	    	//System.out.println(nLine+"########"+fCell+"--"+rHeight+"--"+nChieght);
	    	int iCell=Math.round(fCell);
	    	if(nLine>iCell)
	    	{
	    		while(nLine>iCell)
	    		{
	    			fize=fize-1;
	    			if(fize<=0) {
                        break;
                    }
	    			nLine=getPDFLines(str,fize,rWidth);
	    			nChieght=fize+1;
	    			fCell=rHeight/nChieght;
	    			iCell=Math.round(fCell);
	    			//System.out.println("fize---"+fize);
	    		}
	    	}	    	
		}else
		{
			String fontStr="";
			String mStr="";
			for(int j=0;j<valueList.size();j++)
			{
	               fontStr+=valueList.get(j)!=null?valueList.get(j).toString():"";
	               fontStr=fontStr+"`";
	               
	               /*if(fontStr.length()>mStr.length())
	            	   mStr=fontStr;
	               //fize=ReOneRowDrawLitterRect(rWidth, fize+10,mStr,fize);
	               int iCharLen=getCharLen(mStr);
	               int iHanz=getHzNum(mStr);
	               while(iCharLen*fize-iHanz*(fize/2)>rWidth)
	               {
	            	   fize=fize-1;
	       			   if(fize<=0)
	       				 break;
	               }*/
	            }	
				char c;
				StringBuffer sBuf=new StringBuffer();
				for(int s=0;s<fontStr.length();s++)
	            {
	            	 c =fontStr.charAt(s); //chr(10)表示换行
	            	 if(c=='`') {
                         c='\r';
                     }
	            	 sBuf.append(c);
	            }
				fize=ReOneRowDrawLitterRect((int)rWidth,(int)rHeight,sBuf.toString(),fize);
		}
    	return fize;
	}
    /**
     * pdf行数
     * @param str
     * @param fize
     * @param width
     * @return
     */
    private  int getPDFLines(String str,int fize,float width)
    {
    	 if(str==null||str.length()<=0) {
             return 0;
         }
    	 boolean bView=false;
    	 /*if(str.indexOf("熟悉国家、干部（人事）、")!=-1)
		   bView=true;*/
    	 str=str.replaceAll("/r/n", "  ");
    	 int nStart=0;
         int result=0;
         int iHzlen=0;
         ArrayList strList=new ArrayList();
         char c;
         int charLen=0;
         boolean bHz=false;
         int i=0;
         for(i=0;i<str.length();i++)
         {
        	 c =str.charAt(i); //chr(10)表示换行
        	 //System.out.println(c);
        	 if(c=='\n')//chr(13)表示换行
             {
                 continue;
             }
        	 if(c=='\r')  //chr(10)表示换行
        	 {   
        	     result++;//换行符          	     
        	     //charLen++;
        	     charLen=0;
        	     iHzlen=0;
        	     if(i>nStart) {
                     strList.add(str.substring(nStart, i));
                 }
        	     str=str+"  ";
        	     nStart=i+1;
        	     continue;
        	 }        	 
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
        	// System.out.println(c+"---"+charLen*fize+"----"+width);
        	// if(charLen*fize>width-fize)
        	 //int t=iHzlen/2;        	 
        	 if(charLen*fize-iHzlen*fize>width)
        	 {
        		// System.out.println("换行-----"+(charLen*fize-5));
        		 if(bHz)
        		 {
        			 strList.add(str.substring(nStart, i));
            	     nStart=i;
            	     result++; 
        		 }else
        		 {
        			 if(i==0)
        			 {
        				//strList.add(str.substring(0,1));
        			 }else{
        			    strList.add(str.substring(nStart,i-1));
          		        nStart=i-1;
        			 }
        			 result++;
        		 }  
        		 charLen=0;
        		 iHzlen=0;
        	 }
         }
         if(nStart<=str.length())
         {
        	 result++;
        	 strList.add(str.substring(nStart,i));
         }
         /*if(bView)
         {
        	 for(int s=0;s<strList.size();s++)
        	 {
        		  System.out.println(fize+"---"+ s+"-----"+strList.get(s));
        	 }
         }*/
         return result;
    }
    private  int  getHzNum(String str)
    {
    	int charLen=0;
    	char c;
    	for(int i=0;i<str.length();i++)
        {
        	 c =str.charAt(i); //chr(10)表示换行
        	 if(reCHZ(c)) {
                 charLen++;
             }
        }
    	return charLen;
    }
}

