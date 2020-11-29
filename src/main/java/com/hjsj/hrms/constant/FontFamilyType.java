package com.hjsj.hrms.constant;

import com.hrms.struts.constant.SystemConfig;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import java.io.File;

public class FontFamilyType {
	public static String getFontFamilyTTF(String fontfamilyname) {
		String fontfamilyttfname = null;
	    try{			
			if ("楷体_GB2312".equalsIgnoreCase(fontfamilyname))
				fontfamilyttfname =SystemConfig.getProperty("pdffont") + "/SIMKAI.TTF";//"/com/hjsj/hrms/constant/font/simkai.ttf";
			else if ("方正舒体".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/FZSTK.TTF";
			else if ("方正姚体".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/FZYTK.TTF";
			else if ("仿宋体".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/SIMFANG.TTF";
			else if ("黑体".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/SIMHEI.TTF";
			else if ("华文彩云".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/STCAIYUN.TTF";
			else if ("华文仿宋".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/STFANGSO.TTF";
			else if ("华文细黑".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/STXIHEI.TTF";
			else if ("华文行楷".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/STXINGKA.TTF";
			else if ("华文中宋".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/STZHONGS.TTF";
			else if ("隶书".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/SIMLI.TTF";
			else if ("幼圆".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/SIMYOU.TTF";
			else if("微软雅黑".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/WRYAHEI.TTF";
			else if("宋体".equals(fontfamilyname))
				fontfamilyttfname = SystemConfig.getProperty("pdffont") + "/SIMSUN.TTF";
			//zxj 20150401 如果字体文件不存在，返回null
			if (fontfamilyttfname != null){
				// 不同操作系统中路径分隔符不一致问题，替换成标准分隔符 chent 20180319 update
				fontfamilyttfname = fontfamilyttfname.replace("\\", File.separator).replace("/", File.separator);
				// 用系统CATALINA_HOME环境变量替换配置文件中${catalina.home} chent 20180319 add
				String catalina_home = System.getenv("CATALINA_HOME");
				//如果system参数设置中包含${catalina.home}，而环境变量中没有配置则打印提示信息
				if(fontfamilyttfname.indexOf("${catalina.home}")>-1) {
				    if(StringUtils.isEmpty(catalina_home)){ 
				        Category cat = Category.getInstance("FontFamilyType");
				        cat.error("取不到设置字体，请检查系统CATALINA_HOME环境变量配置。");
				    }else{
				        fontfamilyttfname = fontfamilyttfname.replace("${catalina.home}", catalina_home);
				    }
				}
			    File fontFile = new File(fontfamilyttfname);
			    if (!fontFile.exists()) {
			        fontfamilyttfname = null;
			        //报错信息太多，先注释掉了 dengcan
			        /*
			        System.out.println(ResourceFactory.getProperty("general.inform.org.fontfamily") //zhangcq 2016-5-14 提示字体不存在的问题
			        		+ ResourceFactory.getProperty("menu.file.label")
			        		+ "'" + fontfamilyname + "'" 
			        		+ ResourceFactory.getProperty("constant.e_factornoexist"));
			        		*/
			    }
			}
	    } catch(Exception e){
			e.printStackTrace();
		}
		return fontfamilyttfname;
	}

	/**
	 * 生成字体样式,解决中文问题
	 * 
	 * @fontfamilyname
	 * @fontEffect
	 * @fontSize
	 */
	public static Font getFont(String fontfamilyname, String fontEffect,
			int fontSize) {
		BaseFont bfComic = null;
		Font font = null;
		try {
			// 字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
			if (getFontFamilyTTF(fontfamilyname) == null){
					bfComic = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
							BaseFont.NOT_EMBEDDED); // 解决中文问题
			}else
			{
				try
				{
					
						bfComic = BaseFont.createFont(getFontFamilyTTF(fontfamilyname),
								BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
				}
				catch(Exception ee)
				{
						bfComic = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
								BaseFont.NOT_EMBEDDED); // 解决中文问题
				}
			}
			/**
			 *    Underline,StrikeOut,Italic,Bold,nFontEff                       Underline,StrikeOut,Italic,Bold,nFontEff
			 0  |	0          0        0       0    1						0  |	0          0        0       0    1
			 1  |	0          0        0       1    2						1  |	0          0        0       1    2
			 2  |	0          0        1       0    3						2  |	0          0        1       0    3
			 3  |	0          0        1       1    4						3  |	0          0        1       1    4
			 
			 4  |	0          1        0       0    5						4  |	1          0        0       0    5
			 5  |	0          1        0       1    6						5  |	1          0        0       1    6
			 6  |	0          1        1       0    7						6  |	1          0        1       0    7
			 7  |	0          1        1       1    8						7  |	1          0        1       1    8
			 
			 8  |	1          0        0       0    9						8  |	0          1        0       0    9
			 9  |	1          0        0       1    10						9  |	0          1        0       1    10
			 10 |	1          0        1       0    11						10 |	0          1        1       0    11
			 11 |	1          0        1       1    12						11 |	0          1        1       1    12
			 
			 12 |	1          1        0       0    13						12 |	1          1        0       0    13
			 13 |	1          1        0  	 	1    14						13 |	1          1        0  	 	1    14
			 14 |	1          1        1       0    15						14 |	1          1        1       0    15
			 15 |	1          1        1       1    16						15 |	1          1        1       1    16
			 前面的二维图是cs的逻辑，后面的是bs的Font方法所支持的字形组合；可以看出二者的区别只是在于Underline（下划线），StrikeOut（删除线）的位置是反的
			 所以nFontEff的值cs在5到8的时候是要加4才能满足bs，而在9到12需要减4才能满足；由于cs是按照前面的二维图存入数据库的；所以bs直接判断一下即可满足需求。
			 */
			//-----------------------------按照模板设置的字形来显示，具体逻辑看上面的二维图  zhaoxg add 2016-4-9------------------------------
			int _fontEffect = Integer.parseInt(fontEffect);
			if(_fontEffect>=5&&_fontEffect<9){
				_fontEffect = _fontEffect+4;
			}else if(_fontEffect>=9&&_fontEffect<13){
				_fontEffect = _fontEffect-4;
			}
			font = new Font(bfComic, fontSize + 3, _fontEffect-1);//_fontEffect要减1才是想要的，别问为什么，赶到这了
			//------------------------------------end------------------------------------------------------------
		} catch (Exception e) {
			e.printStackTrace();
		}
		return font;
	}

	public static Font getFont(String fontfamilyname, String fontEffect,
			int fontSize,String platform) {
		BaseFont bfComic = null;
		Font font = null;
		try {
			// 字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
			if(platform==null)//兼容为null的情况 xgq2011-05-4
				platform="";
			if (getFontFamilyTTF(fontfamilyname) == null){
				if("iPhone".equalsIgnoreCase(platform)||platform.startsWith("iPod")||platform.startsWith("iPad")){
					bfComic = BaseFont.createFont(SystemConfig.getProperty("pdffont")+"/arialuni.ttf", BaseFont.IDENTITY_H,
							BaseFont.EMBEDDED); 
				}else{
					bfComic = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
							BaseFont.NOT_EMBEDDED); // 解决中文问题
				}
			}else
			{
				try
				{
					if("iPhone".equalsIgnoreCase(platform)||platform.startsWith("iPod")||platform.startsWith("iPad")){
						bfComic = BaseFont.createFont(SystemConfig.getProperty("pdffont")+"/arialuni.ttf", BaseFont.IDENTITY_H,
								BaseFont.EMBEDDED); 
					}else{
						bfComic = BaseFont.createFont(getFontFamilyTTF(fontfamilyname),
								BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
					}
				}
				catch(Exception ee)
				{
					if("iPhone".equalsIgnoreCase(platform)||platform.startsWith("iPod")||platform.startsWith("iPad")){
						bfComic = BaseFont.createFont(SystemConfig.getProperty("pdffont")+"/arialuni.ttf", BaseFont.IDENTITY_H,
								BaseFont.EMBEDDED); 
					}else{
						bfComic = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
								BaseFont.NOT_EMBEDDED); // 解决中文问题
					}
				}
			}
			/**
			 *    Underline,StrikeOut,Italic,Bold,nFontEff                       Underline,StrikeOut,Italic,Bold,nFontEff
			 0  |	0          0        0       0    1						0  |	0          0        0       0    1
			 1  |	0          0        0       1    2						1  |	0          0        0       1    2
			 2  |	0          0        1       0    3						2  |	0          0        1       0    3
			 3  |	0          0        1       1    4						3  |	0          0        1       1    4
			 
			 4  |	0          1        0       0    5						4  |	1          0        0       0    5
			 5  |	0          1        0       1    6						5  |	1          0        0       1    6
			 6  |	0          1        1       0    7						6  |	1          0        1       0    7
			 7  |	0          1        1       1    8						7  |	1          0        1       1    8
			 
			 8  |	1          0        0       0    9						8  |	0          1        0       0    9
			 9  |	1          0        0       1    10						9  |	0          1        0       1    10
			 10 |	1          0        1       0    11						10 |	0          1        1       0    11
			 11 |	1          0        1       1    12						11 |	0          1        1       1    12
			 
			 12 |	1          1        0       0    13						12 |	1          1        0       0    13
			 13 |	1          1        0  	 	1    14						13 |	1          1        0  	 	1    14
			 14 |	1          1        1       0    15						14 |	1          1        1       0    15
			 15 |	1          1        1       1    16						15 |	1          1        1       1    16
			 前面的二维图是cs的逻辑，后面的是bs的Font方法所支持的字形组合；可以看出二者的区别只是在于Underline（下划线），StrikeOut（删除线）的位置是反的
			 所以nFontEff的值cs在5到8的时候是要加4才能满足bs，而在9到12需要减4才能满足；由于cs是按照前面的二维图存入数据库的；所以bs直接判断一下即可满足需求。
			 */
			//-----------------------------按照模板设置的字形来显示，具体逻辑看上面的二维图  zhaoxg add 2016-4-9------------------------------
			int _fontEffect = Integer.parseInt(fontEffect);
			if(_fontEffect>=5&&_fontEffect<9){
				_fontEffect = _fontEffect+4;
			}else if(_fontEffect>=9&&_fontEffect<13){
				_fontEffect = _fontEffect-4;
			}
			font = new Font(bfComic, fontSize + 3, _fontEffect-1);//_fontEffect要减1才是想要的，别问为什么，赶到这了
			//------------------------------------end------------------------------------------------------------
		} catch (Exception e) {
			e.printStackTrace();
		}
		return font;
	}
	
	public static BaseFont getBaseFont(String fontfamilyname, String fontEffect) {
		BaseFont bfComic = null;
		String fonteff="";
		try {
			if ("2".equals(fontEffect)) {
				fonteff = ".Bold";
			}
			if ("3".equals(fontEffect)) {
				fonteff = ".Italic";;
			}
			if ("4".equals(fontEffect)) {
				fonteff = ".BoldItalic";
			} else {
				fonteff = "";
			}
			
			// 字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
			if (getFontFamilyTTF(fontfamilyname) == null)
			{                                  
				bfComic = BaseFont.createFont("STSongStd-Light"+fonteff, "UniGB-UCS2-H",
						BaseFont.NOT_EMBEDDED); // 解决中文问题
			}
			else
			{
				bfComic = BaseFont.createFont(getFontFamilyTTF(fontfamilyname)+fonteff,
						BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bfComic;
	}	
	/**
	 * @author LZY
	 * @param fontfamilyname
	 *            字体名称
	 * @param fontEffect
	 *            字体样式例如斜粗体为Font.BOLD | Font.ITALIC
	 * @param fontSize
	 *            字体大小
	 * @return
	 */
	public static Font getFont(String fontfamilyname, int fontEffect,
			int fontSize) {
		BaseFont bfComic = null;
		Font font = null;
//		System.out.println(getFontFamilyTTF(fontfamilyname));
		try {
			// 字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
			if (getFontFamilyTTF(fontfamilyname) == null)
				bfComic = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
						BaseFont.NOT_EMBEDDED); // 解决中文问题
			else {
				bfComic = BaseFont.createFont(getFontFamilyTTF(fontfamilyname),
						BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			}
			font = new Font(bfComic, fontSize + 3, fontEffect);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return font;
	}
}
