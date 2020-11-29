package com.hjsj.hrms.utils;

import com.hjsj.hrms.actionform.propose.RandomStrg;
import com.hjsj.hrms.businessobject.org.yfileschart.OrgMapBo;
import com.hjsj.hrms.businessobject.param.PerGradeBo;
import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.transaction.param.CreateCodeTableTrans;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.DocListener;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.IndexWriter;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.*;
import java.text.*;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;


public class PubFunc {
	
	//基于锁版本控制是否启用新开发的程序
	public static boolean isUseNewPrograme(UserView userview)
	{
		int ver=userview.getVersion(); //锁版本
		if(ver>=70)
			return true;
		return false;
	}
	
	/**
	 * 根据锁版本号控制人事异动or薪资的新旧程序
	 * @param func_id
	 * @param url
	 * @return
	 */
	public static boolean hasPriMenu(String func_id,String url,UserView userview)
	{
		String mb_funcids=",23067,231102,320,33001,27015,32401,32501,0107,3210,331010,0C348,"; //人事异动菜单功能号
		String gz_funcids=",32402,32403,32404,32408,32502,32503,32505,0314,32508,32415,"; //薪资菜单功能号 
		int ver=userview.getVersion(); //锁版本
		if(mb_funcids.indexOf(","+func_id+",")!=-1||gz_funcids.indexOf(","+func_id+",")!=-1)
		{
			if(ver>=70)
			{
				if(url.toLowerCase().indexOf("/general/template")==0||url.toLowerCase().indexOf("/gz/")==0) //70以上版本不显示旧程序
					return false; 
			}
			else
			{
				if(url.toLowerCase().indexOf("/module/")==0) //70以下版本不显示新程序
					return false;
			}
		}
		return true;
		
	}
	
	/**
	 * 加水印
	 * @param content
	 * @param bufferedimage
	 * @param xw				x间隔
	 * @param xh				y间隔
	 * @param xscale			x比例
	 * @param yscale 			y比例
	 */
	public static BufferedImage addWaterMarker(String content,BufferedImage bufferedimage,int xw,int yh,float xscale,float yscale)
	{
		BufferedImage img1=null;
		BufferedImage img=null;
		try
		{
	        float alpha = 0.9f; // 透明度

	        int width=bufferedimage.getWidth();
	        int height=bufferedimage.getHeight();
	        
	        /***/
	        long slope= Math.round(Math.sqrt(Math.pow(width, 2)+ Math.pow(height, 2)));
	        long col=width/xw+1;
	        long row=slope/yh+1;
	        int x=0,y=0;	        

      
	        /**创建水印图形*/
			img=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d_w=(Graphics2D)img.getGraphics();
			/**设置比例*/
			//g2d_w.scale(xscale, yscale);	
            
			g2d_w.drawImage(bufferedimage, x, y, null);
	        // 设置透明			
			g2d_w.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha)); 
			g2d_w.rotate(-45);
			Font font = new Font("宋体", Font.BOLD, 50);
            g2d_w.setFont(font);
            g2d_w.setPaint(Color.GRAY);
            /**移动至原点*/
            //g2d_w.translate(-width/2, +height/4);
            g2d_w.translate(-height, 0);
            for(int i=0;i<20;i++ )
            {
            	for(int j=0;j<40;j++)
            	{
                	x=i*xw;
            		y=j*yh;
            		g2d_w.drawString(content, x, y);            		
            	}
            }
            /***/
	        width=Math.round(img.getWidth()*xscale);
	        height=Math.round(img.getHeight()*yscale);
	        img1=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g2d_w1=(Graphics2D)img1.getGraphics();
	        g2d_w1.scale(xscale, yscale);	
	        g2d_w1.drawImage(img, 0, 0, null);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
        return img1;		
	}
	
	/**
	 * 弥补sql漏洞，如输入'会产生错误，将其转换
	 * @param str
	 * @return
	 */
	 public static String getStr(String str)
	 {
		  if(str==null)
			  return str;
		  String temp=str.replaceAll("'","\"");
		  return temp;
	 } 
	 public static String getTagStr(String str)
	 {
		  if(str==null)
			  return str;
		  String temp=str.replaceAll("\'","\\\\\'");
		  temp=temp.replaceAll("\"", "\\\\\"");
		  return temp;
	 } 
	 /**
	  * SQL注入，把几个特殊的字符去掉
	  */
	public static String getReplaceStr(String str)
	{
		if(str==null|| "".equals(str.trim()))
			return str;
		String temp=str.replaceAll("'", "’").replaceAll(";", "；").replaceAll("--", "——");
		return temp;
	}
	
	/**
	 * xss 跨站漏洞，将关键字去掉
	 * @param str
	 * @return
	 */
	public static String getReplaceStr2(String str)
	{
		if(str==null|| "".equals(str.trim()))
			return str;
		/**如果有这几个关键字，视为违法*/
		if(str.toUpperCase().indexOf("SCRIPT")!=-1||str.toUpperCase().indexOf("JAVASCRIPT")!=-1)
			return "";
		String temp=str.replaceAll("\\(", "（").replaceAll("\\)", "）").replaceAll("\"", "”").replaceAll("'", "‘").replaceAll("%27", "‘").replaceAll("%22", "“").replaceAll("%28", "（").replaceAll("%29", "）");
		return temp;
	}
	
	/**
	 * 将前台转换过特殊最的json数据替换回来
	 * @param str
	 * @return
	 * @author xiongyy
	 */
	public static String toReplaceStr(String str){
	    str=str.replaceAll("&gt;", ">");
	    str=str.replaceAll("&lt;", "<");
	    str=str.replaceAll("&nbsp;", " ");
	    str=str.replaceAll("&quot;", "\"");
	    str=str.replaceAll("&#39;", "\'");
	   // str=str.replaceAll("\\\\", "\\");//对斜线的转义
	    str=str.replaceAll("<br>","\r\n"); 
	    return str;
	}
	
	/** 刷新操作在执行过程中时为true */
	public static boolean isProcessing = false;
	
	/**
	 * 同步方法实现刷新数据字典，避免刷新操作并发执行的时候产生莫名其妙的错误
	 * @param path dict.js文件所在目录在服务器磁盘上的的绝对路径
	 * @param conn 数据库连接
	 * @throws GeneralException 如果发生了SQLException或其他不可预知的错误
	 * @author 刘蒙
	 */
	public static synchronized void syncRefreshDataDirectory(String path, Connection conn) throws GeneralException {
		isProcessing = true;
		try {
			CreateCodeTableTrans cctt = new CreateCodeTableTrans();
			// cctt.createCodeTable(path,this.getFrameconn()); // 原始的方法
			cctt.generateCodeTable(path, conn);

			// 生成指标标度表,放到客户端
			PerGradeBo perGradeBo = new PerGradeBo();
			perGradeBo.createPerGradeJS(path, conn);

			DataDictionary.refresh();
			AdminCode.refreshCodeTable();
			DBMetaModel dbmodel = new DBMetaModel(conn);
			dbmodel.reloadTableModel();
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			isProcessing = false;
		}
	}
	
	
	
	/**
	 * 过滤特殊字符，防止XSS跨站,SQL注入漏洞
	 * @param value
	 * @return
	 */
	public static String keyWord_filter(String value)
	{
		if (value == null||value.trim().length()==0) {
            return value;
        }     
	    //过滤用户输入要保护应用程序免遭跨站点脚本编制的攻击，请通过将敏感字符转换为其对应的字符实体来清理 HTML。这些是 HTML 敏感字符：< > " ' % ; ) (  +
        StringBuffer result = new StringBuffer(value.length());
        for (int i=0; i<value.length(); ++i) {
            switch (value.charAt(i)) {
	            case '<':
	                result.append("＜");
	                break;
	            case '>': 
	                result.append("＞");
	                break;
	            case '"': 
	                result.append("＂");
	                break;
	            case '\'': 
	                result.append("＇");
	                break;
	  /*          case '%': 
	                result.append("&#37;");
	                break;*/
	            case ';': 
	                result.append("；");
	                break;
	            case '(': 
	                result.append("〔");
	                break;
	            case ')': 
	                result.append("〕");
	                break;
	   /*         case '&': 
	                result.append("&amp;");
	                break; */
	            case '+':
	                result.append("＋");
	                break;
	            default:
	                result.append(value.charAt(i));
	                break;

            }    
        }
        
    
        String temp=result.toString(); 
		temp=temp.replaceAll("--", "－－");
	//	temp=temp.replaceAll("%3C","＜").replaceAll("%3c","＜").replaceAll("%3E","＞").replaceAll("%3e","＞").replaceAll("%22","＂").replaceAll("%27","＇");
	//	temp=temp.replaceAll("%3B","；").replaceAll("%3b","；").replaceAll("%28","〔").replaceAll("%29","〕").replaceAll("%2B","＋").replaceAll("%2b","＋"); 
		return temp.toString();
	}
	/**
	 * 特殊字符还原，java类获得参数，遇到特殊字符需要还原
	 * @param value
	 * @return
	 */
	public static String keyWord_reback(String value)
	{
		if (value == null||value.trim().length()==0) {
            return value;
        }     
	  
		
        
        value=value.replaceAll("％3C","<").replaceAll("％3c","<").replaceAll("％3E",">").replaceAll("％3e",">").replaceAll("％22","\"").replaceAll("％27","'");
        value=value.replaceAll("％3B",";").replaceAll("％3b",";").replaceAll("％28","(").replaceAll("％29",")").replaceAll("％2B","+").replaceAll("％2b","+");
        value=value.replaceAll("％20"," ");//换空格
        value=value.replaceAll("％23","#");//换#
        value=value.replaceAll("％24","$");//换$
        value=value.replaceAll("％25","%");//换%
        value=value.replaceAll("％26","&");//换&
//		value=value("%40","＠");//换@
        value=value.replaceAll("％5B","[");//换[
        value=value.replaceAll("％5b","[");//换[
        value=value.replaceAll("％5D","]");//换]
        value=value.replaceAll("％5d","]");//换]
        value=value.replaceAll("％2A","*");//换*
        value=value.replaceAll("％2a","*");//换*
        value=value.replaceAll("％2F","/");//换/
        value=value.replaceAll("％2f","/");//换/
        value=value.replaceAll("％3D","=");//换=
        value=value.replaceAll("％3d","=");//换=
       
		
		
        value = value.replaceAll("＜", "<");
        value = value.replaceAll("＞", ">");
        value = value.replaceAll("＂", "\"");
        value = value.replaceAll("＇", "'");
        value = value.replaceAll("；", ";");
        value = value.replaceAll("〔", "(");
        value = value.replaceAll("〕", ")");
        value = value.replaceAll("＋", "+");
        value = value.replaceAll("－－", "--");
       
        /* 20140901  邓灿增加  */       
        value = value.replaceAll("｜", "|");
        value = value.replaceAll("＄", "\\$");//xuj update replaceAll("＄", "$") 2014-9-23 $为正则表达式关键字需用\\转意，否则会报数组越界问题
        value = value.replaceAll("＆", "&");
        value = value.replaceAll("％", "%");
        value = value.replaceAll("＃", "#");
 //     value = value.replaceAll("＼", "\\\\");
        value = value.replaceAll("？", "?");
        value = value.replaceAll("［", "[");
        value = value.replaceAll("］", "]");
        value = value.replaceAll("＊", "*");
        value = value.replaceAll("／", "/");
        value = value.replaceAll("＝", "=");
        
        
        value=value.replaceAll("_insert_"," insert ");
        value=value.replaceAll("_select_"," select ");
        value=value.replaceAll("_master_"," master ");
        value=value.replaceAll("_update_"," update ");
        value=value.replaceAll("_trancate_"," trancate ");
        value=value.replaceAll("_into_"," into ");
		value=value.replaceAll("_and_"," and ");
		value=value.replaceAll("_or_"," or ");
		value=value.replaceAll("_asciit_"," asciit ");
		value=value.replaceAll("_exec_"," exec ");
		value=value.replaceAll("_execute_"," execute ");
		value=value.replaceAll("_drop_"," drop ");
		value=value.replaceAll("_delete_"," delete ");
		return value.toString();
	}
	/***
	 * 文件上传白名单
	 * @param form_file
	 * @return TRUE：通过       false：未通过
	 * @throws GeneralException 
	 */
	public static void checkFileType(FormFile form_file) throws GeneralException{
		try {
			//当文件文件信息不为空时进行以下验证
			if(form_file != null && form_file.getFileData().length>0)
			{
				String fname = form_file.getFileName();
				int indexInt = fname.lastIndexOf(".");
				String fileType = fname.substring(indexInt+1,fname.length());
				/**doc docx xls xlsx rar zip ppt jpg jpeg png bmp txt wps pptx允许上传类型**/
				String []fileTypes = {"doc","docx","xlsx","xls","rar","zip","ppt","jpg","jpeg","png","bmp","txt","wps","pptx"};
				int num = 0;
				for(int i=0;i<fileTypes.length;i++){
					if(fileTypes[i].equalsIgnoreCase(fileType)){
						num+=1;
					}
				}
				if(num<1){
					throw GeneralExceptionHandler.Handle(new Exception("您的文件\""+form_file+"\"不支持上传，请选择doc,<br>docx,xls,xlsx,rar,zip,ppt,jpg,jpeg,png,bmp,txt,wps,pptx等<br>文件格式上传!"));
				}
				//验证文件后缀名是否为文件实际类型
				if(!FileTypeUtil.isFileTypeEqual(form_file)){
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}						
	}
	
	
	/**
	 * 防止SQL注入，替换关键字
	 * @param sql
	 * @return
	 */
	public static String  replaceSQLkey(String sql)
	{ 
		sql=sql.replaceAll("( |\t)(?i)"+"insert( |\t)","_insert_");
		sql=sql.replaceAll("( |\t)(?i)"+"select( |\t)","_select_");
		sql=sql.replaceAll("( |\t)(?i)"+"master( |\t)","_master_");
		sql=sql.replaceAll("( |\t)(?i)"+"update( |\t)","_update_");
		sql=sql.replaceAll("( |\t)(?i)"+"trancate( |\t)","_trancate_");
		sql=sql.replaceAll("( |\t)(?i)"+"into( |\t)","_into_");
		sql=sql.replaceAll("( |\t)(?i)"+"and( |\t)","_and_");
		sql=sql.replaceAll("( |\t)(?i)"+"or( |\t)","_or_");
		sql=sql.replaceAll("( |\t)(?i)"+"ascii( |\t)","_asciit_");
		sql=sql.replaceAll("( |\t)(?i)"+"exec( |\t)","_exec_");
		sql=sql.replaceAll("( |\t)(?i)"+"execute( |\t)","_execute_");
		sql=sql.replaceAll("( |\t)(?i)"+"drop( |\t)","_drop_");
		sql=sql.replaceAll("( |\t)(?i)"+"delete( |\t)","_delete_");
		
		return sql;
	}
	
	
	/**
	 * 招聘过滤特殊字符，防止XSS跨站,SQL注入漏洞
	 * @param value
	 * @return
	 */
	public static String hireKeyWord_filter(String value)
	{

		if (value == null||value.trim().length()==0) {
            return value;
        }     
	    //过滤用户输入要保护应用程序免遭跨站点脚本编制的攻击，通过将敏感字符转换为其对应的字符实体来清理 HTML。这些是 HTML 敏感字符：< > " ' % ; ) (  +
        StringBuffer result = new StringBuffer(value.length());
        for (int i=0; i<value.length(); ++i) {
            switch (value.charAt(i)) {
	            case '<':
	                result.append("＜");
	                break;
	            case '>': 
	                result.append("＞");
	                break;
	            case '"': 
	                result.append("＂");
	                break;
	            case '\'': 
	                result.append("＇");
	                break;
	            case ';': 
	                result.append("；");
	                break;
	            case '(': 
	                result.append("〔");
	                break;
	            case ')': 
	                result.append("〕");
	                break;
	            case '+':
	                result.append("＋");
	                break;
	            case '|'://以下为增加内容　將半角轉為全角
	                result.append("｜");
	                break;
	            case '$':
	                result.append("＄");
	                break;
	            case '&':
	                result.append("＆");
	                break;
	            case '%':
	                result.append("％");
	                break;

	            case '#':
	                result.append("＃");
	                break;
/*
	            case '\\':
	                result.append("＼");
	                break;  */
	            case '?':
	                result.append("？");
	                break;
	            case '[':
	                result.append("［");
	                break;
	            case ']':
	                result.append("］");
	                break;
	            case '*':
	                result.append("＊");
	                break;
	            case '/':
	                result.append("／");
	                break;
	            case '=':
	                result.append("＝");
	                break; 
//		            case '-':
//	                result.append("－");
//	                break; 
//		            case ',':
//	                result.append("，");
//	                break;
//		            case '@':
//	                result.append("＠");
//	                break;
	            default:
	                result.append(value.charAt(i));
	                break;

            }    
        } 
        String temp=result.toString(); 
		temp=temp.replaceAll("--", "－－");
	/*
		temp=temp.replaceAll("%3C","＜").replaceAll("%3c","＜").replaceAll("%3E","＞").replaceAll("%3e","＞").replaceAll("%22","＂").replaceAll("%27","＇");
		temp=temp.replaceAll("%3B","；").replaceAll("%3b","；").replaceAll("%28","〔").replaceAll("%29","〕").replaceAll("%2B","＋").replaceAll("%2b","＋");
		temp=temp.replaceAll("%20","　");//换空格
		temp=temp.replaceAll("%23","＃");//换#
		temp=temp.replaceAll("%24","＄");//换$
		temp=temp.replaceAll("%25","％");//换%
		temp=temp.replaceAll("%26","＆");//换&
//		temp=temp.replaceAll("%40","＠");//换@
		temp=temp.replaceAll("%5B","［");//换[
		temp=temp.replaceAll("%5b","［");//换[
		temp=temp.replaceAll("%5D","］");//换]
		temp=temp.replaceAll("%5d","］");//换]
		temp=temp.replaceAll("%2A","＊");//换*
		temp=temp.replaceAll("%2a","＊");//换*
		temp=temp.replaceAll("%2F","／");//换/
		temp=temp.replaceAll("%2f","／");//换/
		temp=temp.replaceAll("%3D","＝");//换=
		temp=temp.replaceAll("%3d","＝");//换=
		 */
		
		temp=replaceSQLkey(temp);
		return temp.toString();
	}
	/**
	 * 还原招聘过滤特殊字符
	 * @param value
	 * @return
	 */
	public static String hireKeyWord_filter_reback(String value){
		if (value == null||value.trim().length()==0) {
            return value;
        }     
        value = value.replaceAll("＜", "<");
        value = value.replaceAll("＞", ">");
        value = value.replaceAll("＂", "\"");
        value = value.replaceAll("＇", "'");
        value = value.replaceAll("；", ";");
        value = value.replaceAll("〔", "(");
        value = value.replaceAll("〕", ")");
        value = value.replaceAll("＋", "+");
        value = value.replaceAll("－－", "--");
        value = value.replaceAll("｜", "|");
        //员工管理，简单查询查询值为年限时，＄还原错误，$需要加上转义符  jingq add 2014.10.21
        value = value.replaceAll("＄", "\\$");
        value = value.replaceAll("＆", "&");
        value = value.replaceAll("％", "%");
        value = value.replaceAll("＃", "#");        
   //   value = value.replaceAll("＼", "\\\\");
        value = value.replaceAll("？", "?");
        value = value.replaceAll("［", "[");
        value = value.replaceAll("］", "]");
        value = value.replaceAll("＊", "*");
        value = value.replaceAll("／", "/");
        value = value.replaceAll("＝", "=");
        
        return value.toString();
	}
	
	public static String reBackWord(String value)
	{
		if (value == null||value.trim().length()==0) {
            return value;
        }  
		value = value.replaceAll("≮", "<");
        value = value.replaceAll("≯", ">");
        value = value.replaceAll("≡", "\"");
        value = value.replaceAll("∪", "'");
        value = value.replaceAll("∈", ";");
        value = value.replaceAll("∵", "(");
        value = value.replaceAll("⊙", ")");
        value = value.replaceAll("≌", "+");
        value = value.replaceAll("∥∥", "--");
        return value.toString();
	}
	
	
	
	
	
	
	public static BufferedImage addCombine(String content,BufferedImage bufferedimage,int xw,int yh,float xscale,float yscale)
	{
		BufferedImage img1=null;
		BufferedImage img=null;
		try
		{
	        float alpha = 0.9f; // 透明度
	        int width=bufferedimage.getWidth();
	        int height=bufferedimage.getHeight();
	        /**创建水印图形*/
			img=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d_w=(Graphics2D)img.getGraphics();
			g2d_w.drawImage(bufferedimage, 0, 0, null);
	        // 设置透明			
			g2d_w.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha)); 
			Font font = new Font("宋体", Font.BOLD, 50);
            g2d_w.setFont(font);
            g2d_w.setPaint(Color.GRAY);
    		g2d_w.drawString(content, xw, yh);            		

	        img1=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
	        Graphics2D g2d_w1=(Graphics2D)img1.getGraphics();
	        g2d_w1.drawImage(img, 0, 0, null);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
        return img;		
	}
	
	/**
     * 将字节数组压缩
     * @param bytes
     *            a byte array
     * @param aName
     *            a String the represents a file name
     * @return byte[] compressed bytes
     * @throws IOException
     */
    public static byte[] zipBytes(byte[] bytes) throws IOException {
        ByteArrayOutputStream tempOStream = null;
        BufferedOutputStream tempBOStream = null;
        ZipOutputStream tempZStream = null;
        ZipEntry tempEntry = null;
        byte[] tempBytes = null;

        tempOStream = new ByteArrayOutputStream(bytes.length);
        tempBOStream = new BufferedOutputStream(tempOStream);
        tempZStream = new ZipOutputStream(tempBOStream);
        tempEntry = new ZipEntry(String.valueOf(bytes.length));
        tempEntry.setMethod(ZipEntry.DEFLATED);
        tempEntry.setSize((long) bytes.length);
        
        tempZStream.putNextEntry(tempEntry);
        tempZStream.write(bytes, 0, bytes.length);
        tempZStream.flush();
        tempBOStream.flush();
        tempOStream.flush();
        tempZStream.close();
        tempBytes = tempOStream.toByteArray();
        tempOStream.close();
        tempBOStream.close();
        return tempBytes;
    }

	
	
	
    /**
     * 解压字节数组
     * @param bytes
     *            a byte array of compressed bytes
     * @return byte[] uncompressed bytes
     * @throws IOException
     */
    public static  byte[] unzipBytes(byte[] bytes) throws IOException {
    	ByteArrayOutputStream outStream=new ByteArrayOutputStream();
        ByteArrayInputStream tempIStream = null;
        BufferedInputStream tempBIStream = null;
        ZipInputStream tempZIStream = null;
        ZipEntry tempEntry = null;
        long tempDecompressedSize = -1;
        byte[] tempUncompressedBuf = null;

        tempIStream = new ByteArrayInputStream(bytes, 0, bytes.length);
        tempBIStream = new BufferedInputStream(tempIStream);
        tempZIStream = new ZipInputStream(tempBIStream);
        tempEntry = tempZIStream.getNextEntry();
        
        if (tempEntry != null) {
            tempDecompressedSize = tempEntry.getCompressedSize();
            if (tempDecompressedSize < 0) {
                tempDecompressedSize = Long.parseLong(tempEntry.getName());
            }

            int size = (int)tempDecompressedSize;
            tempUncompressedBuf = new byte[size];
            int num = 0, count = 0;
            while ( true ) {
                count = tempZIStream.read(tempUncompressedBuf, 0, size - num );
                num += count;
                outStream.write( tempUncompressedBuf, 0, count );
                outStream.flush();
                if ( num >= size ) break;
            }
        }
        tempZIStream.close();
        byte[] value_bytes=outStream.toByteArray();
        outStream.close();
        return value_bytes;
    }
	
	
    /** 将list对象压缩成字节数组 */
    public static byte[] zipBytes_object(ArrayList list) throws IOException 
    {
    	
    	ByteArrayOutputStream byte_os = new ByteArrayOutputStream();
		GZIPOutputStream  gzip_os = null;
		ObjectOutputStream  object_os = null;
		gzip_os = new GZIPOutputStream(byte_os);
		object_os = new ObjectOutputStream(gzip_os);
		object_os.writeObject(list);
		object_os.flush();
	    object_os.close();
		gzip_os.flush();
		gzip_os.close();
		byte[] value_bytes= byte_os.toByteArray();
	    byte_os.close();
	    return value_bytes;
    }
    
    /** 将list对象压缩成字节数组 */
    public static String zipBytes_object(String txt) throws IOException 
    {
    	
    	ByteArrayOutputStream byte_os = new ByteArrayOutputStream();
		GZIPOutputStream  gzip_os = null;
		ObjectOutputStream  object_os = null;
		gzip_os = new GZIPOutputStream(byte_os);
		object_os = new ObjectOutputStream(gzip_os);
		object_os.writeObject(txt);
		object_os.flush();
	    object_os.close();
		gzip_os.flush();
		gzip_os.close();
		byte[] value_bytes= byte_os.toByteArray();
	    byte_os.close();
	    return new String(value_bytes);
    }
    
    // 压缩
    public static String compress(String str) throws IOException {
      if (str == null || str.length() == 0) {
        return str;
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      GZIPOutputStream gzip = new GZIPOutputStream(out);

      gzip.write(str.getBytes());
      gzip.close();
      return out.toString("ISO-8859-1");
    }

    // 解压缩
    public static String decompress(String str) throws IOException {
      if (str == null || str.length() == 0) {
        return str;
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
      GZIPInputStream gunzip = new GZIPInputStream(in);
      byte[] buffer = new byte[256];
      int n;
      while ((n = gunzip.read(buffer)) >= 0) {
        out.write(buffer, 0, n);
      }
      // toString()使用平台默认编码，也可以显式的指定如toString("GBK")
      return out.toString();
    }

    
   public static String decompressData(String encdata) {
         StringBuffer  buf=new StringBuffer();
         try
         {
       	      String content=convert64BaseToString(encdata);
       	      content=content.replaceAll("\r\n","");
	    	  content=decompress(content);
	    	  buf.append(content);
         }
         catch(Exception ex)
         {
       	  ex.printStackTrace();
         }
         return buf.toString();   	   
//        try {
//        	
//                String content=convert64BaseToString(encdata);
//                ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
//                
//                InflaterInputStream zos = new InflaterInputStream(bais);
//
//                byte[] buf = new byte[1024]; 
//                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
//                int i;
//                while ((i = zos.read(buf)) != -1) {
//                  baos2.write(buf,0,i);
//                }  
//              
//                byte[] decompressedData = baos2.toByteArray();
//
//                zos.close();
//                return new String(decompressedData);
//              
//            } catch (Exception ex) {
//               ex.printStackTrace();
//               return "UNZIP_ERR";
//           }
            
         
       }
   
       //压缩
       public static String compressData(String data) {
    	      StringBuffer buf=new StringBuffer();
    	      try
    	      {
    	    	  String txt=compress(data);
    	    	  txt=convertTo64Base(txt.getBytes("ISO-8859-1"));
    	    	  txt=txt.replaceAll("\r\n","");
    	    	  buf.append(txt);
    	      }
    	      catch(Exception ex)
    	      {
    	    	  ex.printStackTrace();
    	      }
    	      return buf.toString();   
//            try 
//            {
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                DeflaterOutputStream zos = new DeflaterOutputStream(bos);
//                zos.write(data.getBytes());
//                zos.close();
//                return new String(convertTo64Base(bos.toByteArray()));
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                return "ZIP_ERR";
//            }    	      
       }    
  
    /** 解压 对象流压缩后的字节数组 还原成list对象 */
    public static  ArrayList unzipBytes_object(byte[] bytes) throws IOException {
    	ArrayList atempList=new ArrayList();
    	try
    	{
	    	 ByteArrayInputStream bytes_is = new ByteArrayInputStream(bytes);
	    	 GZIPInputStream   gzip_is = null;
	    	 ObjectInputStream  object_is = null;
	    	 gzip_is = new GZIPInputStream(bytes_is);
	    	 object_is = new ObjectInputStream(gzip_is);
	    	 atempList=(ArrayList)object_is.readObject();
	         bytes_is.close();
	         gzip_is.close();
	         object_is.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();	
    	}
    	return atempList;
    }
    

	/**
	 * 图形格式转换
	 * @param renderediamge
	 * @param format 格式串 PNG、TIFF、JPEG
	 * @return
	 */
	public static BufferedImage renderedToBuffered(RenderedImage renderediamge,String format)
	{
		BufferedImage bfi=null;
		try
		{
	        ByteArrayOutputStream byteOutput=null;					
            int   width   =   renderediamge.getWidth(); 
            int   height   =   renderediamge.getHeight(); 
            double   conversionFactor   =   (double)100   /   (double)width; 
            int   thumbHeight   =   (int)((double)height   *   conversionFactor); 
            int   thumbWidth   =   (int)((double)width   *   conversionFactor); 
            Dimension   dim   =   new   Dimension(thumbHeight,   thumbWidth);                 
            JAI.setDefaultRenderingSize(dim); 
            JAI.setDefaultTileSize(dim); 			            
            byteOutput=new ByteArrayOutputStream();			            
            JAI.create("encode", renderediamge, byteOutput,format, null); 
            ByteArrayInputStream   in   = new ByteArrayInputStream(byteOutput.toByteArray());
            bfi=ImageIO.read(in);
            //icon.setImage(bfi);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bfi;
	}
	/**
	 * 转换文件
	 * @param renderediamge
	 * @param format  		格式串 PNG、TIFF、JPEG
	 * @param filename		文件名
	 * @return
	 */
	public static boolean renderedToFile(RenderedImage renderediamge,String format,String filename)
	{
		boolean bflag=false;
		try
		{
            int   width   =   renderediamge.getWidth(); 
            int   height   =   renderediamge.getHeight(); 
            double   conversionFactor   =   (double)600   /   (double)width; 
            int   thumbHeight   =   (int)((double)height   *   conversionFactor); 
            int   thumbWidth   =   (int)((double)width   *   conversionFactor); 
            Dimension   dim   =   new   Dimension(thumbHeight,   thumbWidth);                 
            JAI.setDefaultRenderingSize(dim); 
            JAI.setDefaultTileSize(dim); 			            
            JAI.create("filestore",renderediamge,filename,format);
            //JAI.create("filestore",renderediamge,"d:\\i"+i+".png","png");
            bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
	}	
	/**
	 * 拆分图像文件,tiff文件会包括多个图像文件
	 * @param in
	 * @param format  如tiff｜gif|png|jpeg|jpg
	 * @return 返回的列表为RenderedImage对象
	 */
	public static ArrayList splitRenderedImage(InputStream  in,String format)
	{
		ArrayList list=new ArrayList();
		try
		{
			if("tiff".equalsIgnoreCase(format))
			{
		        ImageDecoder dec = ImageCodec.createImageDecoder(format/*"tiff"*/, in, null);
		        int count = dec.getNumPages();				
		        for (int i = 0; i < count; i++)
		        {
		            RenderedImage page = dec.decodeAsRenderedImage(i);
		            list.add(page);
		        }
			}
			else
			{
				RenderedImage   img = ImageIO.read(in);
				list.add(img);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}	
	/**
	 * 字符转换成文件
	 * @param thebyte
	 * @param filename
	 */
	public static void byteToFile(byte[] thebyte,String filename)
	{
		OutputStream out  = null;
			try 
			{ 
				out = new FileOutputStream(filename); 
				out.write(thebyte); 
				out.flush(); 
			} catch (Exception e) { 
			 e.printStackTrace(); 
			} 
			finally{
				closeIoResource(out);
			}
	}
	/**
	 * 文件转成字节数组
	 * @param filename
	 * @return
	 */
	public static byte[] fileToByte(String filename)
	{
        int len;
        byte[] filebyte=null;
        FileInputStream in = null;
        try
        {

	        in=new FileInputStream(new File(filename));
	        byte buff[] = new byte[1024];
	        ByteArrayOutputStream out0 = new ByteArrayOutputStream(); 
	        while((len   =   in.read(buff))   !=   -1){ 
	        	out0.write(buff, 0, len); 
	        }
	        filebyte=out0.toByteArray();
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }finally{
            closeResource(in); 
        }
        return filebyte;
	}
	/**
	 * 字节数组转换成字节流
	 * @param thebyte
	 * @return
	 */
	public static ByteArrayInputStream byteToInputStream(byte[] thebyte)
	{
		return new ByteArrayInputStream(thebyte);   	
	}
	/**
	 * 分隔字符串，主要解决StringUtils.spit无法切分xxx```aaa`
	 * @param content
	 * @param sperator
	 * @return
	 */
	public static Object[] split(String content,String sperator)
	{
		ArrayList list=new ArrayList();
		int index=content.indexOf(sperator);
		while(index!=-1)
		{
			String tmp=content.substring(0,index);
			list.add(tmp);
			content=content.substring(index+1);
			index=content.indexOf(sperator);
		}
		list.add(content);
		return list.toArray();
	}
	/**
	 * 中英文在unicode16下的字符长度处理
	 * @param sss
	 * @param len 字节长度
	 * @return
	 */
	public static String splitString(String sss,int len)
		{
			try {
				//原来是GBK编码转成UTF8需要扩展1.5倍
				len = (int) (1.5*len);
				byte[] bytes= new byte[0];
				//不管是什么编码格式统一转成UTF8
				bytes = sss.getBytes("UTF-8");
				int bytelen=bytes.length;
				if(bytelen<=len || len==0)
				{
					return sss;
				}
				/**实际的长度*/
				int rlen=0;
				int j=0;
				for(int i=0;i<len;i++)
				{
					if(bytes[i]<0)
					{
						j++;
					}
				}
				/**xus 20/4/18 utf8汉字截取长度为3*/
				if((j%3)==0)
					rlen=len;
				else
					rlen=len-j%3;
				byte[] target=new byte[rlen];
//			System.out.println("second--splitString-->"+sss+"byte length is"+bytelen);
				System.arraycopy(bytes,0,target,0,rlen);
				String dd=new String(target);
				return dd;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return sss;
		}
	/**
	 * 指定长度插入换行符
	 * @param sss
	 * @param len
	 * @return
	 */
	public static String reLineString(String sss,int len,String sign)
		{
			try{
				//原来是GBK编码转成UTF8需要扩展1.5倍
				len = (int) (1.5*len);
				byte[] bytes= new byte[0];
				//不管是什么编码格式统一转成UTF8
				bytes = sss.getBytes("UTF-8");
				int bytelen=bytes.length;
				if(bytelen<=len || len==0)
				{
					return sss;
				}
				/**实际的长度*/
				int rlen=0;
				int j=0;
				for(int i=0;i<len;i++)
				{
					if(bytes[i]<0)
					{
						j++;
					}
				}
				if((j%3)==0)
					rlen=len;
				else
					rlen=len-j%3;;
				StringBuffer sb= new StringBuffer();
				int quotient = bytes.length/rlen;
				int remainder=bytes.length%rlen;
				for(int i=0;i<=quotient;i++){
					if(i==quotient-1){
						if(remainder>0){
							byte[] target=new byte[rlen];
							System.arraycopy(bytes,rlen*i,target,0,rlen);
							String dd=new String(target);
							sb.append(dd+sign);
						}else{
							byte[] target=new byte[rlen];
							System.arraycopy(bytes,rlen*i,target,0,rlen);
							String dd=new String(target);
							sb.append(dd);
						}
					}else if(i==quotient){
						if(remainder>0){
							byte[] target=new byte[remainder];
							System.arraycopy(bytes,rlen*i,target,0,remainder);
							String dd=new String(target);
							sb.append(dd);
						}
					}else{
						byte[] target=new byte[rlen];
						System.arraycopy(bytes,rlen*i,target,0,rlen);
						String dd=new String(target);
						sb.append(dd+sign);
					}
				}
				return sb.toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return sss;
		}

	public static String toXml(String str) {

		String html = str;
		html = html.replaceAll("&", "&amp;");
		html = html.replaceAll("<", "&lt;");
		html = html.replaceAll(">", "&gt;");
		html = html.replaceAll("\"", "&quot;");
		html = html.replaceAll("\r\n", "\n");
		html = html.replaceAll("\r\n", "\n");
		html = html.replaceAll("'","&acute;");
		html=html.replaceAll("%","%25");
		html=html.replaceAll("=","%3D");
		//html=html.replaceAll("?","%3F");
		return html;
	}
	/**
	 * 字符串中包括"时，通过ajax传递时出错，
	 * @param str
	 * @return
	 */
	public static String toAjax(String str)
	{
		String ajaxstr=str.replaceAll("\"", "“");
		return ajaxstr;
	}
	/**
	 * 转换成get提交格式字符
	 * @param str
	 * @return
	 */
	public static String toGet(String str) {
		String getStr="";
		str=toXml(str);		
		for(int i=0;i<str.length();i++)
		{
			if("+".equals(str.substring(i,i+1)))
			{
				getStr+="%2B";
			}else if("?".equals(str.substring(i,i+1)))
			{
				getStr+="%3F";
			}else
			{
				getStr+=str.substring(i,i+1);
			}
		}
		return getStr;
	}
	/**
	 * 转换成html格式字符
	 * @param str
	 * @return
	 */
	public static String toHtml(String str) {

		String html = str;
		html = Replace(html, "&", "&amp;");
		html = Replace(html, "<", "&lt;");
		html = Replace(html, ">", "&gt;");
		html = Replace(html, "\"", "&quot;");
		html = Replace(html, "\r\n", "\n");
		html = Replace(html, "\n", "<br>");
		html = Replace(html, "\t", "    ");
		html = Replace(html, "  ", " &nbsp;");
		html = Replace(html, " ", "&nbsp;");//半角空格得以显示
		return html;
	}
	
	
	/**
	 * 将html格式字符转回原有格式
	 * @param str
	 * @return
	 */
	public static String reverseHtml(String str) {

		String html = str;
		html =html.replaceAll("&amp;", "&");
		html =html.replaceAll("&amp;", "&");
		html =html.replaceAll(" &nbsp;", "  ");
		html =html.replaceAll("&nbsp;", " ");
		html =html.replaceAll("&nbsp", " "); 
		html =html.replaceAll("&nbs", " "); 
		html =html.replaceAll("&nb", " ");
		html =html.replaceAll("&n", " "); 
		html =html.replaceAll("&lt;", "<");
		html =html.replaceAll("&gt;", ">");
		html =html.replaceAll("&quot;", "\"");
		html = Replace(html, "\n", "\r\n");
		html = Replace(html, "<br>", "\n");
		html = Replace(html, "    ", "\t"); 
		html =html.replaceAll("<", "&lt;");
		html =html.replaceAll(">", "&gt;");
		html =html.replaceAll("&lt;br /&gt;", "<br />");
		html =html.replaceAll("&lt;p&gt;", "<p>");
		html =html.replaceAll("&lt;/p&gt;", "</p>");
		return html;
	}

	//替换source中的char类为str2
	public static String replace(String source, char str1, String str2) {
		if (source == null) {
			return source;
		}
		String desc = "";
		for (int i = 0; i < source.length(); i++) {
			if (source.charAt(i) == str1) {
				desc = desc + str2;
			} else {
				desc = desc + String.valueOf(source.charAt(i));
			}
		}
		return desc;
	}

	//替换source中的str1为str2
	public static String replace(String source, String str1, String str2) {
		if (source == null) {
			return source;
		}
		String desc = "";
		int i = 0;
		while (i < source.length()) {
			if (source.startsWith(str1, i)) {
				desc = desc + str2;
				i = i + str1.length();
			} else {
				desc = desc + String.valueOf(source.charAt(i));
				i++;
			}

		}
		return desc;
	}

	/**
	 * 随机数
	 */

	public static String getStrg() {
		String filenametmp = "";
		try {
			RandomStrg RSTR = new RandomStrg();

			RSTR.setCharset("a-z");
			RSTR.setLength("3");
			RSTR.generateRandomObject();

			filenametmp = (new Date()).toLocaleString();
			String filenametemp2 = "";
			for (int i = 0; i < filenametmp.length(); i++) {
				if ("9".equals(filenametmp.substring(i, i + 1))
						|| "8".equals(filenametmp.substring(i, i + 1))
						|| "7".equals(filenametmp.substring(i, i + 1))
						|| "6".equals(filenametmp.substring(i, i + 1))
						|| "5".equals(filenametmp.substring(i, i + 1))
						|| "4".equals(filenametmp.substring(i, i + 1))
						|| "3".equals(filenametmp.substring(i, i + 1))
						|| "2".equals(filenametmp.substring(i, i + 1))
						|| "1".equals(filenametmp.substring(i, i + 1))
						|| "0".equals(filenametmp.substring(i, i + 1)))
					filenametemp2 = filenametemp2
							+ (filenametmp.substring(i, i + 1));
			}

			filenametmp = filenametemp2 + RSTR.getRandom();
			return filenametmp;
		} catch (Exception ex) {

		}
		return "";
	}

	/**
	 * 字符串替换，将 source 中的 oldString 全部换成 newString
	 * 
	 * @param source
	 *            源字符串
	 * @param oldString
	 *            老的字符串
	 * @param newString
	 *            新的字符串
	 * @return 替换后的字符串
	 */
	public static String Replace(String source, String oldString,
			String newString) {
		try {
			StringBuffer output = new StringBuffer();

			int lengthOfSource = source.length(); // 源字符串长度
			int lengthOfOld = oldString.length(); // 老字符串长度

			int posStart = 0; // 开始搜索位置
			int pos; // 搜索到老字符串的位置

			while ((pos = source.indexOf(oldString, posStart)) >= 0) {
				output.append(source.substring(posStart, pos));

				output.append(newString);
				posStart = pos + lengthOfOld;
			}

			if (posStart < lengthOfSource) {
				output.append(source.substring(posStart));
			}
			return output.toString();
		} catch (Exception e) {
			return source;
		}
	}

	public static String DoFormatDecimal(String value, int decimalwidth) {
		if(value==null|| "".equals(value)){
			return "";
		}

		String pattern = "";
		double fldValue = 0.0f; //float ->double chenmengqing changed at 20050807 for 99999999->100000000.00
		if (decimalwidth > 0) {
			pattern = "##0.";   //chenmengqing #=>0, for -0.2 格式化成-.2; at 20060816
			for (int nI = 0; nI < decimalwidth; nI++)
				pattern += "0";   //chenmengqing changed at 20050807
		} else {
			pattern = "###";
		}
		if (value != null && value.length() > 0){
			fldValue=Double.parseDouble(value);
			value = new DecimalFormat(pattern).format(fldValue).trim();
			if(".".equals(value.substring(0,1)))
				value="0" + value;
		}
		return value;
	}
	
	public static String DoFormatDecimal2(String value, int decimalwidth) {
		if(value==null|| "".equals(value)){
			return "";
		}
		//xuj udpate 7779齐鲁制药：数据显示方式不规范有的显示为空有的显示为零
		if(Double.parseDouble(value)==0){
			return "";
		}
		String pattern = "";
		double fldValue = 0.0f; //float ->double chenmengqing changed at 20050807 for 99999999->100000000.00
		if (decimalwidth > 0) {
			pattern = "##0.";   //chenmengqing #=>0, for -0.2 格式化成-.2; at 20060816
			for (int nI = 0; nI < decimalwidth; nI++)
				pattern += "0";   //chenmengqing changed at 20050807
		} else {
			pattern = "###";
		}
		if (value != null && value.length() > 0){
			fldValue=Double.parseDouble(value);
			value = new DecimalFormat(pattern).format(fldValue).trim();
			if(".".equals(value.substring(0,1)))
				value="0" + value;
		}
		return value;
	}

	/*
	 * 获得年月日日期格式
	 */

	public static String DoFormatDate(String temp) {
		if (temp == null || temp.trim().length() < 0 || "".equals(temp.trim()) || "null".equals(temp)) {
			return "";
		} else {
			
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				
			try
			{
			
		   		Date tempdate=sdf.parse(temp);
		   	    
		   		temp=sdf.format(tempdate);
		   		
		   		//System.out.println(sdf.format(tempdate)+"    ");
	       }
	      catch(Exception ex)
	       {
				ex.printStackTrace();
			}
			
			String slpymd="";
			
				   slpymd = temp.substring(0, temp.length());
			
			String[] strAry = slpymd.split("-");
			if(strAry.length==3)
			{
			temp = strAry[0] + "." + strAry[1] + "." + strAry[2];
			}
			else
			{
				temp="";
			}
			if ("0000".equals(strAry[0]) || "1900".equals(strAry[0])) {
				temp = "";
			}
		}
		return temp;
	}
	
	public static String DoFormatSecDate(String temp)
	{
		if (temp == null || temp.trim().length() < 0 || "".equals(temp.trim()) || "null".equals(temp)) {
			return "";
		} else {
			
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				
			try
			{
			
		   		Date tempdate=sdf.parse(temp);
		   	    
		   		temp=sdf.format(tempdate);
		   		
		   		//System.out.println(sdf.format(tempdate)+"    ");
	       }
	      catch(Exception ex)
	       {
				ex.printStackTrace();
			}
		}
	      return temp;
	}

	//获得系统的格式化日期
	public static String DoFormatSystemDate(boolean bShowTimePart_in) {
		Date dt_in = new Date();
		if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		{
	    if (bShowTimePart_in)
			return "'" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(dt_in) + "'";
		else
			return "'" + (new SimpleDateFormat("yyyy-MM-dd")).format(dt_in) + "'";
		}else if(Sql_switcher.searchDbServer()==Constant.DB2)
		{
			String datestr="TO_Date('";
	     if (!bShowTimePart_in)
	     	datestr+=(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(dt_in) + "','YYYY-MM-DD HH24:MI:SS')";
		 else
		 	datestr+= (new SimpleDateFormat("yyyy-MM-dd")).format(dt_in) + "','YYYY-MM-DD')";
		   return datestr;
		}
		else if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
	         String datestr="TO_Date('";
			 if (bShowTimePart_in)
			 	datestr+=(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(dt_in) + "','YYYY-MM-DD HH24:MI:SS')";
			 else
			 	datestr+= (new SimpleDateFormat("yyyy-MM-dd")).format(dt_in) + "','YYYY-MM-DD')";
		   return datestr;
		}
		else
		{
			 if (bShowTimePart_in)
				return (new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")).format(dt_in);
			 else
				return (new SimpleDateFormat("yyyy.MM.dd")).format(dt_in);
		
		}
	    
	}
	
	/**
	 * 转换日期格式
	 * @param 
	 * @return
	 */
	public static String getChangeDate(DateStyle dts)
	{
		String temp=dts.getYear()+"."+dts.getMonth()+"."+dts.getDate();
		return temp;
	}
	

	//字符串转换，即NULL转为"",非空则不变
	public static String nullToStr(String s) {
		if (s == null || s.trim().length() < 0 || "".equals(s)) {
			return " ";
		} else {
			return s.trim();
		}
	}

	/**
	 * 
	 * 处理int为null,null为0
	 *  
	 */
	public static int chgNullInt(String str) {
		int chgInt = 0;
		try {
			if (str == null || "".equals(str) || "null".equals(str)) {
				str = "0";

				chgInt = Integer.parseInt(str);

			} else {
				chgInt = Integer.parseInt(str);
			}
		} catch (Exception ex) {
			chgInt = 0;

		}
         
		return chgInt;
	}
	/**
	 * 处理int类型为null
	 *
	 */
	public static int DoIntNull(int inttp)
	{
		if(new Integer(inttp)==null)
		{
			inttp=0;
		}
		
		return inttp;
	}
	/**
	 * 处理int类型为null
	 *
	 */
	public static float DoFloatNull(float floattp)
	{
		if(new Float(floattp)==null)
		{
			floattp=0;
		}
		
		return floattp;
	}
	/**
	 * 空字符返回0
	 *
	 */
	public static String DotstrNull(String str)
	{
		if(str==null || "".equals(str) || "null".equals(str))
		{
			str="";
		}
		return str;
	}
	/**
	 * 空字符返回0
	 *
	 */
	public static String NullToZero(String str)
	{
		if(str==null || "".equals(str) || "null".equals(str))
		{
			str="0";
		}
		return str;
	}
	public static String DateStringChangeValue(String value)
    {	
    	
       	String tovalue="";   
    	if(value!=null && (value.length()==6 || value.length()==7))
		{
    		if(new Integer(value.substring(0,4)).intValue()<1900){
    			tovalue="null";
    		}else{
    			
    			
		   tovalue=value.substring(0,4) + "." + value.substring(5) + ".01";	
    		}
		}
	    else if(value!=null && value.length()==4)
		{
	    	if(new Integer(value).intValue()<1900){
	    		tovalue="null";
	    	}else{
	    		tovalue=value + ".01.01";
	    	}
		}else if(value!=null && value.length()==16){//liuy 2015-4-24 9053
			if(new Integer(value.substring(0,4)).intValue()<1900){
	    		tovalue="null";
	    	}else{
	    		tovalue=value + ":00";
	    	}
		}else
		{
		  if (value == null || "null".equals(value) || "".equals(value))
		    tovalue="null";
		  else
		  {
		  	tovalue=value;
		  }
		}	 
    	tovalue = tovalue.replaceAll("\\.", "-");
    	if("null".equals(tovalue))
    	   tovalue=null;
    	return tovalue;
    }
    public static String DateStringChange(String value)
    {	
    	
       	String tovalue="";   
    	if(value!=null && (value.length()==6 || value.length()==7))
		{
    		if(new Integer(value.substring(0,4)).intValue()<1900){
    			tovalue="null";
    		}else{
    			
    			
		   tovalue=value.substring(0,4) + "." + value.substring(5) + ".01";	
    		}
		}
	    else if(value!=null && value.length()==4)
		{
	    	if(new Integer(value).intValue()<1900){
	    		tovalue="null";
	    	}else{
	    		tovalue=value + ".01.01";
	    	}
		}else
		{
		  if (value == null || "null".equals(value) || "".equals(value))
		    tovalue="null";
		  else
		  {
		  	tovalue=value;
		  }
		}	 
    	if(!"null".equals(tovalue))
    		//TO_Date('2005-09-20 0:0:0','YYYY-MM-DD HH24:MI:SS');  DB2的时间函数
    	 tovalue=Sql_switcher.dateValue(tovalue);
    	return tovalue;
    }
    public static String DateStringChangeValuelist(String value)
    {
       	String tovalue="";   
    	if(value!=null && (value.length()==6 || value.length()==7))
		{
		   tovalue=value.substring(0,4) + "." + value.substring(5) + ".01";	
		}
	    else if(value!=null && value.length()==4)
		{
		  tovalue=value + ".01.01";	
		}else
		{
		  if (value == null || "null".equals(value) || "".equals(value))
		    tovalue="null";
		  else
		  {
		  	tovalue=value;
		  }
		}	 
    	if(!"null".equals(tovalue))
    		//TO_Date('2005-09-20 0:0:0','YYYY-MM-DD HH24:MI:SS');  DB2的时间函数
    	 tovalue=Sql_switcher.dateValue(tovalue).substring(1,Sql_switcher.dateValue(tovalue).length()-1);
    	return tovalue;
    }
//	中文字的编码转换
	public static String ToGbCode (String str) throws IOException{
            
            String isChangeToGBK ="true";
           
            if (isChangeToGBK.compareTo("true") == 0 ){	
                if(str!=null){
                              str = new String(str.getBytes("ISO-8859-1"),"GBK");         		
		              }
            }
     		return str;
	}	
	/**
	 * 比较yyyy-mm-dd hh:mm:ss格式的日期
	 * @param strDate	审批时间
	 * @param nowDate 当前时间
	 * @param day
	 * @return
	 */
   public static	boolean  compareDate(String strDate,String nowDate,int day)
   {
   		boolean flagbl=false;
   		if("".equals(strDate))
   		{
   			return true;
   		}
   	   
   		if(nowDate.length()<=10)
   		{
   			nowDate=nowDate+" 00:00:00";
   		}
	
   		if(strDate.length()<=10)
   		{
   			strDate=strDate+" 00:00:00";
   		}
   		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try
	{
			
   	     Date firstDate=sdf.parse(strDate);
   	     Date secondDate=sdf.parse(nowDate);
   	     long firstlg= firstDate.getTime()/1000;
   	     long secondlg=secondDate.getTime()/1000;
   	     //设置日期太长，超过了 int 类型最大上限，判断不对  wangb 2019-11-29
   	     Long daylg=((long)day)*24*3600;
   	     
   	     if((secondlg-firstlg)>daylg)
   	     {
   	     	flagbl= true;
   	      }
   	     else
   	     {
   	     	flagbl= false;
   	      }
	}
	catch(Exception ex)
	{
		flagbl= false;
		ex.printStackTrace();
	}
	return flagbl;
	
   }
   /**
    * 相差小时
    * @param start_date
    * @param end_date
    * @return
    */
   public static long getHourSpan(Date start_date,Date end_date){    	
		int sY=DateUtils.getYear(start_date);
		int sM=DateUtils.getMonth(start_date);
		int sD=DateUtils.getDay(start_date);
		int sH=DateUtils.getHour(start_date);
		int smm=DateUtils.getMinute(start_date);
		
		int eY=DateUtils.getYear(end_date);
		int eM=DateUtils.getMonth(end_date);
		int eD=DateUtils.getDay(end_date);
		int eH=DateUtils.getHour(end_date);
		int emm=DateUtils.getMinute(end_date);
		GregorianCalendar d1= new GregorianCalendar(sY,sM,sD,sH,smm,00);
		GregorianCalendar d2= new GregorianCalendar(eY,eM,eD,eH,emm,00);
		Date date1= d1.getTime();		         
       Date date2= d2.getTime();
       long l1=date1.getTime();
       long l2=date2.getTime();
       long part=(l2-l1)/(60*60*1000L);
		return part;
	}
   
   
   
   public static String divide(String s,String v,int scale)
   {
	   if(scale<0)
       {
           throw new IllegalArgumentException("The scale must be a positive integer or zero");
       }
       
       BigDecimal b = new BigDecimal(s);
       BigDecimal one = new BigDecimal(v);
       return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).toString();
	   
   }
   
   /**
	 * 提供精确的小数位四舍五入处理。
    * @param v 需要四舍五入的数字
    * @param scale 小数点后保留几位
    * @return 四舍五入后的结果
    */
   public static String round(String v,int scale){

       if(scale<0)
       {
           throw new IllegalArgumentException("The scale must be a positive integer or zero");
           }
       if(v==null|| "".equalsIgnoreCase(v)||"NaN".equalsIgnoreCase(v))
    	   v="0";
       BigDecimal b = new BigDecimal(v);
       BigDecimal one = new BigDecimal("1");
       return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).toString();
   }

   public static String multiple(String v1,String v2,int scale){
	   
	   if(scale<0)
       {
           throw new IllegalArgumentException("The scale must be a positive integer or zero");
           }
       BigDecimal a = new BigDecimal(v1);
       BigDecimal b = new BigDecimal(v2);
       BigDecimal s=a.multiply(b);
       BigDecimal one = new BigDecimal("1");
       return s.divide(one,scale,BigDecimal.ROUND_HALF_UP).toString();
	   
   }
   /**
    * 两个数字相加 保留几位小数
    * @param v1 数1
    * @param v2  数2
    * @param scale 小数位
    * @return
    */
   public static String add(String v1,String v2,int scale){
	   
	   if(scale<0)
       {
           throw new IllegalArgumentException("The scale must be a positive integer or zero");
           }
       BigDecimal a = new BigDecimal(v1);
       BigDecimal b = new BigDecimal(v2);
       BigDecimal s=a.add(b);
       BigDecimal one = new BigDecimal("1");
       return s.divide(one,scale,BigDecimal.ROUND_HALF_UP).toString();
	   
   }
   
   public static String subtract(String v1,String v2,int scale){
	   
	   if(scale<0)
       {
           throw new IllegalArgumentException("The scale must be a positive integer or zero");
           }
       BigDecimal a = new BigDecimal(v1);
       BigDecimal b = new BigDecimal(v2);
       BigDecimal s=a.subtract(b);
       BigDecimal one = new BigDecimal("1");
       return s.divide(one,scale,BigDecimal.ROUND_HALF_UP).toString();
	   
   }
   
   
   
   /**
    * 字符串长度控制
    * @param str
    * @param length
    * @return
    */
   
   public static String  doStringLength(String str,int length)
   {
   	  
   	  /*
   		if(str.length()>length)
   		{
   			return str.substring(0,length);
   		}
   		else
   		{
   			return str.substring(0,str.length());
   		}
   		*/
   	  return splitString(str,length);
   }
   
   /**
    * 
    * @param date
    * @param pattern yyyy-MM-dd hh:mm:ss
    * @return
    */
   public static String FormatDateTime(Timestamp date, String pattern)
   {
	   if(date==null)
		   return "";
  	   SimpleDateFormat sdf=new SimpleDateFormat(pattern);
	  	 try {				
	  		 Date newDate = sdf.parse(sdf.format(date));
	  		 java.sql.Date time = new java.sql.Date(newDate.getTime());
	  		 String tt = time.toString();	
	  		 //System.out.println(tt);
	  		 return tt;
	  	 } catch (ParseException e) {				
	  		 e.printStackTrace();			
	  	 }
	  	return null;
   }
   /**
    * 
    * @param date
    * @param pattern yyyy-MM-dd hh:mm:ss
    * @return
    */
   public static String FormatDate(java.sql.Date date,String pattern)
   {
	   if(date==null)
		   return "";
  	   SimpleDateFormat sdf=new SimpleDateFormat(pattern);
  	   return sdf.format(date);
   }
   public static String FormatDate(Date date, String pattern)
   {
	   if(date==null)
		   return "";
  	   SimpleDateFormat sdf=new SimpleDateFormat(pattern);
  	   return sdf.format(date);
   }
   public static String FormatDate(java.sql.Date date)
   {
	   if(date==null)
		   return "";
	   try
	   {
		   String temp=FormatDate(date,"yyyy-MM-dd");
		   if("1900-01-01".equalsIgnoreCase(temp)|| "1899-12-30".equalsIgnoreCase(temp))
			   return "";
		   return temp;//FormatDate(date,"yyyy-MM-dd");
	   }
	   catch(Exception ex)
	   {
		   return "";
	   }
   }   
   
   
   
   /**
    * 取得 日期比较 sql语句 
    * @param operate
    * @param itemid
    * @param DateValue
    * @author dengcan
    * @return
    */
   public static String getDateSql(String operate,String itemid,String DateValue)
   {
		StringBuffer sql=new StringBuffer("");
		String[] temps=DateValue.split("-");
		int year=Integer.parseInt(temps[0]);
		int month=Integer.parseInt(temps[1]);
		int day=Integer.parseInt(temps[2]);
		if(">".equals(operate)|| "<".equals(operate))
		{
			sql.append(" and ( "+Sql_switcher.year(itemid)+operate+year);
			sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+operate+month+"  )");
			sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"="+month+" and "+Sql_switcher.day(itemid)+operate+day+"  )");	
			sql.append(" ) ");
		}
		else if(">=".equals(operate)|| "<=".equals(operate))
		{
			if(">=".equals(operate))
				sql.append(" and ( "+Sql_switcher.year(itemid)+">"+year);
			else
				sql.append(" and ( "+Sql_switcher.year(itemid)+"<"+year);
			
			if(">=".equals(operate))
				sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+">"+month+"  )");
			else
				sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"<"+month+"  )");
			
			sql.append(" or ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"="+month+" and "+Sql_switcher.day(itemid)+operate+day+"  )");	
			sql.append(" ) ");
		}
		else if("=".equals(operate))
			sql.append(" and ("+Sql_switcher.year(itemid)+"="+year+" and "+Sql_switcher.month(itemid)+"="+month+" and "+Sql_switcher.day(itemid)+"="+day+"  )");	
		
		return sql.toString();
	}
   
   
   
   
   
   
   
   
   
   /**
    * 
    * @author luangaojiong
    *
    * TODO 要更改此生成的类型注释的模板，请转至
    * 窗口 － 首选项 － Java － 代码样式 － 代码模板
    */
     public static String FormatDate(String value)
     {
     	  DateFormat df = DateFormat.getDateInstance();
     	  	  value=value.trim();
     	  	 
              String datestr="";
              if(value==null|| "null".equalsIgnoreCase(value))
           	   return "";
              int len=value.length();
              try
			  {
              if(len==4)
              {
              	datestr=value+"-01-01";
              }
              else if(len>4 && len<=10)
              {
                            	
            	StringTokenizer st=new StringTokenizer(value,".");
            	             	
              	if(st.countTokens()==2)
              	{
              		String chart []=new String[2];
              		chart[0]=st.nextToken();
              		chart[1]=st.nextToken();
              		if(chart[1].length()==1)
              		{
              			chart[1]="0"+chart[1];
              		}
              		datestr=chart[0]+"-"+chart[1]+"-01";
              	}
              	else if(st.countTokens()==3)
              	{
              		String chart []=new String[3];
              		chart[0]=st.nextToken();
              		chart[1]=st.nextToken();
              		chart[2]=st.nextToken();
              		
              		if(chart[1].length()==1)
              		{
              			chart[1]="0"+chart[1];
              		}
              		if(chart[2].length()==1)
              		{
              			chart[2]="0"+chart[2];
              		}
              		datestr=chart[0]+"-"+chart[1]+"-"+chart[2];
              	}
              	
              }
              else  if(len>10)
              {
           	   Date datevalue=df.parse(value);
           	   datestr=DateStyle.dateformat(datevalue,"yyyy.MM.dd");  
              }
              else
              {
              	return "";
              }
              
             
			  }
              catch(Exception ex)
			  {
              	
			  }
             return datestr;
     }
     public static double parseDouble(String value)
     {
    	if(value==null||value.length()<=0)
    	{
    		value="0";
    	} 
    	double dv=Double.parseDouble(value);
    	return dv;
     }
     public static String formatDecimals(double value,int length)
     {
    	 StringBuffer sb=new StringBuffer();
    	 try{
    		if(value==0){
    			return "0";
    		}
	    	String tmp = String.valueOf(value);
	    	int index=tmp.indexOf(".");
	    	/*if(length==0){
	    		sb.append(tmp.substring(0,index));
	    	}else*/{
		    	if(index!=-1){
		    		if(tmp.length()<index+1+length){
		    			sb.append(tmp);
		    			while(sb.length()<index+1+length){
		    				sb.append("0");
		    			}
		    		}else if(tmp.length()==index+1+length){
		    			sb.append(tmp);
		    		}else{
		    			if(length<=0){
		    				int d=Integer.parseInt(tmp.substring(0,index));
		    				int str2=Integer.parseInt(tmp.substring(index+1,index+2));
		    				if(str2>4)
		    					d++;
		    				sb.append((d+""));
		    			}else{
		    				double d = Double.parseDouble(tmp.substring(0,index+1+length));
		    				int str2=Integer.parseInt(tmp.substring(index+length+1,index+length+2));
		    				if(str2>4){
		    					StringBuffer s= new StringBuffer("0.");
		    					for(int i=0;i<length-1;i++){
		    						s.append("0");
		    					}
		    					s.append("1");
			    				d=d+Double.parseDouble(s.toString());
			    			}
		    				d = Double.parseDouble((d+" ").substring(0,index+1+length));//加空格 防止位数是0
		    				sb.append((d+""));
		    			}
		    		}
		    	}else{
		    		sb.append(tmp);
		    		if(length>0){
		    			sb.append(".");
		    			for(int i=0;i<length;i++){
		    				sb.append("0");
		    			}
		    		}
		    	}
	    	}
    	 }catch(Exception e){
    		 e.printStackTrace();
    		 sb.setLength(0);
    	 }
    	 return sb.toString();
     }
     public static String getISNULL(String fieldname)
     {
     	String sql="";
    	switch(Sql_switcher.searchDbServer())
		{
		  case Constant.MSSQL:
		  {
		  	sql=" IsNull(" + fieldname + ",0) ";
		  	break;
		  }
		  case Constant.DB2:
		  {
		  	sql=" COALESCE(" + fieldname + ",0) ";
		  	break;
		  }
		  case Constant.ORACEL:
		  {
		  	sql=" NVL(" + fieldname + ",0) ";
		  	break;
		  }
		}
    	return sql;
     }
     public static String getStringDate(String dateFormat) {
         try {
      	   Date date = new Date();
             String sf = new SimpleDateFormat(dateFormat).format(date);
             
             return sf;
         } catch (Exception e) {           
             e.printStackTrace();
             return null;
         }
     }

     /**
      * 把记录集转换成如下格式
      * <rowset columns="AA,BB,CC..." types="N,A,D,M">
      *     <rec>xxx`bbb`</rec>
      *     ...
      * </rowset>
      * @param rset
      * @return
      */
     public static String combineXml(ResultSet rset) {
         return combineXml(rset, false);
     }

     /**
      * 把记录集转换成如下格式
      * <rowset columns="AA,BB,CC..." types="N,A,D,M">
      * 	<rec>xxx`bbb`</rec>
      * 	...
      * </rowset>
      * @param rset
      * @param bReturnEmptyValue 数值型指标为空，则用空字符串表示
      * @see #combineXml(ResultSet)
      * @see #getValueByFieldType(ResultSet, ResultSetMetaData, int, boolean)
      * @return
      */
     public static String combineXml(ResultSet rset, boolean bReturnEmptyValue)
     {
    	 StringBuffer buf=new StringBuffer();
 		 ResultSetMetaData rsetmd=null;
 		 StringBuffer txt=new StringBuffer();

 		 String value=null;
 		 
 		 try
 		 {
 	    	 buf.append("<rowset columns=\"");
 			 rsetmd=rset.getMetaData();
 			 String[] values=getColumns(rsetmd);
 			 buf.append(values[1]);
 			 buf.append("\" ");
 			 buf.append(" types=\"");
 			 buf.append(values[0]);
 			 buf.append("\"");
 			 buf.append(">");
 			 while(rset.next())
 			 {  
 				    txt.setLength(0);
 				    buf.append("<rec>");
	                for(int j=1;j<=rsetmd.getColumnCount()/*columns.length*/;j++)
	                {   
	                   String temp = getValueByFieldType(rset, rsetmd, j, bReturnEmptyValue);
	                   /**备注型字段编码*/
	                   if(rsetmd.getColumnType(j)==Types.CLOB||rsetmd.getColumnType(j)==Types.LONGVARCHAR)
	                   {
	                	   //writeDate(temp);
	                	   temp= Base64.encodeBase64String(temp.getBytes());
	                	   temp=temp.replaceAll("\r\n", "");
	                   }else{
	                	   if(temp.indexOf("&")!=-1)
	                		   temp=temp.replaceAll("&", "&amp;");
	                	  if(temp.indexOf("<")!=-1)
	                		  temp=temp.replaceAll("<","&lt;");
	                	  if(temp.indexOf(">")!=-1)
	                		  temp=temp.replaceAll(">","&gt;");
	                	  if(temp.indexOf("'")!=-1)
	                		  temp=temp.replaceAll("'", "&apos;");
	                	  if(temp.indexOf("\"")!=-1)
	                		   temp=temp.replaceAll("\"", "&quot;");
	                   }
	                   temp=temp.replaceAll("`", "^^"); 
	                   txt.append(temp);
	                   txt.append("`");//以“`”分隔字段值
	                }//for j end.
	                //buf.append(encoder.encode(txt.toString().getBytes()));
	                buf.append(txt.toString());
 				 	buf.append("</rec>");
// 				 	buf.append("\r\n");
 			 }//for while end.
 			 buf.append("</rowset>");
 			 txt.setLength(0);
 			 /**compress begin*/
 			 String content=PubFunc.compressData(buf.toString());
 			 txt.append(content/*encoder.encode(content.getBytes()*/);
 			 /**end.*/
 			 //txt.append(encoder.encode(buf.toString().getBytes()));
 			 value=txt.toString();
 			 value=value.replaceAll("\r\n","");
 		 }
 		 catch(Exception ex)
 		 {
 			 ex.printStackTrace();
 		 }
    	 return value;//txt.toString();
     }
    /**
     * 数据集的字段名列表及字段对应的数据类型
     * @param rsetmd
     * @return
     */
     private static String[] getColumns(ResultSetMetaData rsetmd) 
     {
    	 StringBuffer buf=new StringBuffer();
    	 StringBuffer colms=new StringBuffer();
    	 int nlen,npre;
    	 String[] values=new String[2];
    	 try
    	 {
    		 for(int j=1;j<=rsetmd.getColumnCount();j++)
    		 {  
    			 	colms.append(rsetmd.getColumnName(j));
    			 	colms.append(",");
    		 		switch(rsetmd.getColumnType(j))
    		 		{
    		 		case Types.DATE:
    		 				buf.append("D");
    		 		        break;			
    		 		case Types.TIMESTAMP:
    		 				buf.append("D");

    		 				break;
    		 		case Types.CLOB:
    		 		case Types.LONGVARCHAR:
		 				buf.append("M");
    		 			break;
    		 		case Types.BLOB:
    		 		case Types.LONGVARBINARY:
		 				buf.append("L");
    		 			break;		
    		 		case Types.NUMERIC:
    		 		case Types.INTEGER:
    		 		case Types.SMALLINT:
    		 		case Types.BIGINT:
    		 		case Types.DOUBLE:
    		 		case Types.FLOAT:
		 				buf.append("N");
//		 				nlen=rsetmd.getPrecision(j);
//		 				npre=rsetmd.getScale(j);
//		 				buf.append(nlen);
//		 				if(npre>0)
//		 				{
//		 					buf.append(".");
//		 					buf.append(npre);
//		 				}		 				
    		 			break;
    		 		default:		
		 				buf.append("A");
    		 			if(Sql_switcher.searchDbServer()==Constant.MSSQL)
    		 				nlen=rsetmd.getPrecision(j);
    		 			else
    		 				nlen=rsetmd.getColumnDisplaySize(j);
    		 			buf.append(nlen);
    		 			break;
    		 		}
	 				buf.append(",");    		 				
    		 }
    		 if(buf.length()>0)
    		 {
    			 buf.setLength(buf.length()-1);
    			 colms.setLength(colms.length()-1);
    		 }
    		 values[0]=buf.toString();
    		 values[1]=colms.toString();
    	 }
    	 catch(Exception ex)
    	 {
    		 ex.printStackTrace();
    	 }
    	 return values;
     }
    /**
     * delphi client's SQL　解码 <> > <
     * @param list
     * @return
     */
    public static ArrayList getDecodeSQL(ArrayList list)
    {
    	ArrayList delist=new ArrayList();
    	//try
    	//{
	    	for(int i=0;i<list.size();i++)
	    	{
	    		String sql=(String)list.get(i);
	    		sql=sql.replaceAll("%26lt;", "<");
	    		sql=sql.replaceAll("%26gt;", ">");
	    		delist.add(sql/*new String(destr)*/);
	    	}//for i loop end.
    	//}
    	//catch(Exception ex)
    	//{
    	//	ex.printStackTrace();
    	//}
    	return delist;
    }

    public static String convert64BaseToString(String  txt)
    {
    	StringBuffer buf=new StringBuffer();
    	if(txt==null)
    			txt="";
	    String content=null;
 		try
 		{
 		    byte[] sourcearr =Base64.decodeBase64(txt);
 		    content=new String(sourcearr,"GBK");
 		    buf.append(content);
 		}
 		catch(Exception ex)
 		{
 			ex.printStackTrace();
 		}
     	return buf.toString();		
    }
    
    public static String convert64BaseToString(byte[]  txt)
    {
    	return convert64BaseToString(new String(txt));
    }
    
   public static String convertTo64Base(String  txt)
   {
   		StringBuffer buf=new StringBuffer();
   		if(txt==null)
   			txt="";

		try
		{
			buf.append(Base64.encodeBase64String(txt.getBytes()));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	return buf.toString();		
   }
   public static String convertTo64Base(byte[]  txt)
   {
  		StringBuffer buf=new StringBuffer();

		try
		{
			buf.append(Base64.encodeBase64String(txt));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	return buf.toString();
   }   
   /**
    * 将url传的参数进行加密
    * @param txt
    * @return
    */
   public static String encryption(String txt) {
	   return encrypt(txt);
   }
   /**
    * 将url传的参数进行解密
    * @param ori_str
    * @return
    */
   public static String decryption(String ori_str) {
	   return decrypt(ori_str);
   }
   
   
   
   /**
    * 加密
    * @param ori_str
    * @return
    */
   public static String encrypt(String ori_str)
   {
       if (null == ori_str)
           return "";
       //Base64换成了Base62,没有特殊字符，无需再替换
       String to_str = SafeCode.encrypt(ori_str);
       return to_str;
   }
   
   /**
    * 解密
    * @param ori_str
    * @return
    */
   public static String decrypt(String ori_str)
   {
       if (null == ori_str)
           return "";
       //薪资等模块写上了旧的固定的加密串，需要使用旧的解密方式解密
       if(ori_str.contains("PAATTP")||ori_str.contains("@")){
		   ori_str = ori_str.replace("PAATTP","@");
		   ori_str = ori_str.replace("@2HJ5@", "%");
		   ori_str = ori_str.replace("@2HJB@", "+");
		   ori_str = ori_str.replace("@2HJ0@", " ");
		   ori_str = ori_str.replace("@2HJF@", "/");
		   ori_str = ori_str.replace("@3HJF@", "?");
		   ori_str = ori_str.replace("@2HJ3@", "#");
		   ori_str = ori_str.replace("@2HJ6@", "&");
		   ori_str = ori_str.replace("@3HJD@", "=");
		   return SafeCode.decryptDes(ori_str);
	   }
       String to_str = SafeCode.decrypt(ori_str);
       return to_str;
   }
   
   
   
   
   /**
    * 将url特殊字符转换编码
    * @param urlParam
    * @return
    */
   public static String convertUrlSpecialCharacter(String urlParam)
   {
	   urlParam=urlParam.replaceAll("%", "%25");
	   urlParam=urlParam.replaceAll("\\+", "%2B");
	   urlParam=urlParam.replaceAll(" ", "%20");
	   urlParam=urlParam.replaceAll("\\/", "%2F");
	   urlParam=urlParam.replaceAll("\\?", "%3F");
		 
	   urlParam=urlParam.replaceAll("#", "%23");
	   urlParam=urlParam.replaceAll("&", "%26");
	   urlParam=urlParam.replaceAll("=", "%3D");
	   return urlParam;
   }
   /**
    * 将编码转换url特殊字符
    * @param urlParam
    * @return
    */
   public static String convertCharacterUrlSpecial(String urlParam)
   {
	   urlParam=urlParam.replaceAll("%25", "%");
	   urlParam=urlParam.replaceAll("%2B", "\\+");
	   urlParam=urlParam.replaceAll("%20", " ");
	   urlParam=urlParam.replaceAll("%2F", "\\/");
	   urlParam=urlParam.replaceAll("%3F", "\\?");
		 
	   urlParam=urlParam.replaceAll("%23", "#");
	   urlParam=urlParam.replaceAll("%26", "&");
	   urlParam=urlParam.replaceAll("%3D", "=");
	   return urlParam;
   }
    /**
     * 大对象转换成BASE64编码
     * @param rset
     * @param j
     * @return
     * @throws SQLException
     */
	public static String getBlobBase64(ResultSet rset, int j) {
		StringBuffer buf = new StringBuffer();
		InputStream in = null;

		ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
		try {
			in = rset.getBinaryStream(j);
			if (in == null)
				return "";
			int len;
			byte[] buff = new byte[1024];
			while ((len = in.read(buff)) != -1) {
				out.write(buff, 0, len);
			}
			in.close();
			buf.append(Base64.encodeBase64String(out.toByteArray()));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeResource(out);
			closeResource(in);
		}
		return buf.toString();
	}
    
    public static String getValueByFieldType(ResultSet rset, ResultSetMetaData rsetmd, int j) throws SQLException {
        return getValueByFieldType(rset, rsetmd, j,false);
        
    }  
 	/**
 	 * @param rset
 	 * @param rsetmd 	  
 	 * @param j 
 	 * @param  bReturnEmptyValue 数值型指标为空，则用空字符串表示 2014-01-21 wangrd
 	 * @return
 	 * @throws SQLException
 	 */

 	public static String getValueByFieldType(ResultSet rset, ResultSetMetaData rsetmd, int j,boolean bReturnEmptyValue) throws SQLException {
 		String temp="";
 		switch(rsetmd.getColumnType(j))
 		{
 		
 		case Types.DATE:
 			    ///temp=PubFunc.FormatDate(rset.getDate(j));//用该行ORCALE取不到时间HH：mm所以改为下面这行，请无改
 			    temp=PubFunc.FormatDate(rset.getTimestamp(j),"yyyy-MM-dd HH:mm:ss");
 		        break;			
 		case Types.TIMESTAMP:
 			    temp=PubFunc.FormatDate(rset.getTimestamp(j),"yyyy-MM-dd HH:mm:ss"); 
 			    //会导致丢失时分秒，咨询大家没有特殊情况，则达成一致意见去掉  xuj delete 20141201
 			    /*if(temp.indexOf("12:00:00")!=-1)
 			        temp=PubFunc.FormatDate(rset.getDate(j));*/
 				break;
 		case Types.CLOB:
 		case Types.LONGVARCHAR:
 			    temp=Sql_switcher.readMemo(rset,rsetmd.getColumnName(j));	
 		
 				break;
 		case Types.BLOB:
 		case Types.LONGVARBINARY: 			
 				//temp="二进制文件";	 //chenmengqing added 20080327 for delphi's client
 			    temp=getBlobBase64(rset,j);
         	    temp=temp.replaceAll("\r\n", "");
 				break;		
 		case Types.NUMERIC:
 			  int preci=rsetmd.getScale(j);
 			  /**针对oracle库，当sql语句使用sum等函数时，取不到小数位，但是下方法取到的是这个值的小数位，而不是这个列的小数位，但能保证数值正确，先采用此办法，lizhenwei at 20100427*/
 			  if(preci==0&&Sql_switcher.searchDbServer()==Constant.ORACEL)
 			  {
 				  BigDecimal bd = rset.getBigDecimal(j);
 				  if(bd!=null)
 			     	  preci=bd.scale();
 			  }
 			  /**针对oracle float 类型 chenmengqing changed at 20091023*/
 			  if(Sql_switcher.searchDbServer()==Constant.ORACEL&&preci==-127)
 			  {
 				  if("I9999".equalsIgnoreCase(rsetmd.getColumnName(j)))
 					  preci=0;
 				  else
 					  preci=2;
 			  }

 			  if ((rset.getString(j)==null)&&(bReturnEmptyValue)) 
 			  {/**先判断数值型是否为空，为空的话，直接返回空字符串，否则getdouble会返回0.0 wangrd 20140121   */ 
 	                 temp="";  			
 			  }
 			  else {
 			     temp=String.valueOf(rset.getDouble(j)); 
 			  }
 			  
 		      temp=PubFunc.DoFormatDecimal(temp, preci);
 			  break;
 		default:		
 				temp=rset.getString(j);
 				if(temp==null)
 					temp="";
 				char s='\u0000';
 				String ss=String.valueOf(s);
 				//temp=temp.replace(s, ' ');
 				temp=temp.replaceAll(ss, " ");
 				break;
 		}
 		return temp;
 	}   
 	public static String getValueByFieldType(ResultSet rset, ResultSetMetaData rsetmd, String columnname) throws SQLException{
 		int i=rset.findColumn(columnname);
 		return getValueByFieldType(rset,rsetmd,i);
 	}
 	//liuyz 32377 数值型指标用户没有填，bReturnEmptyValue：true:返回空串，false返回0.0。
 	public static String getValueByFieldType(ResultSet rset, ResultSetMetaData rsetmd, String columnname,Boolean bReturnEmptyValue) throws SQLException{
 		int i=rset.findColumn(columnname);
 		return getValueByFieldType(rset,rsetmd,i,bReturnEmptyValue);
 	}
 	
 	public static int getUnsignValue(byte value)
 	{
 		return value>=0?value:256+value;
 	}
 	
	 public static String hzToUnicode(String str){
         char[]arChar=str.toCharArray();
         int iValue=0;
         StringBuffer  buf=new StringBuffer();
         for(int i=0;i<arChar.length;i++){
             buf.append("\\u" + Integer.toString(str.charAt(i), 16));
         }
         return buf.toString();
     }
 	
 	 public static ArrayList toUnicode(String str){
         char[]arChar=str.toCharArray();
         int iValue=0;
         ArrayList unicodelist=new ArrayList();
         StringBuffer  buf=new StringBuffer();
         for(int i=0;i<arChar.length;i++){
        	 buf.setLength(0);
             iValue=(int)str.charAt(i);           
             if(iValue<=256){
            	 buf.append("\\u00");
            	 buf.append(Integer.toHexString(iValue).toUpperCase());
             }else{
            	 buf.append("\\u");
            	 buf.append(Integer.toHexString(iValue).toUpperCase());                 
             }
             unicodelist.add(buf.toString());
         }
         return unicodelist;
     }

 	 public static final int chrPinYinStart='\uA3C1';//全角字符ＡＢ..Ｚ 
 	 public static final int[] pinYinRange={
 		  '\u0000',
 		  '\uB0A1',//A
 		  '\ub0c5',//B
 		  '\ub2c1',//C
 		  '\ub4ee',//D 
 		 '\ub6ea',//E
 		 '\ub7a2',//F
 		 '\ub8c1',//G 
 		 '\ub9fe',//H
 		 '\u0000',//I
 		 '\ubbf7',//J
 		 '\ubfa8',//K
 		 '\uc0ac',//L
 		 '\uc2e8',//M
 		 '\uc4c3',//N
 		 '\uc5b6',//O
 		 '\uc5be',//P
 		 '\uc6da',//Q
 		 '\uc8bb',//R
 		 '\uc8f6',//S
 		 '\ucbfa',//T
 		 '\u0000',//U
 		 '\u0000',//V
 		 '\ucdda',//W
 		 '\ucef4',//X
 		 '\ud1b9',//Y 
 		 '\ud4d1',//Z
 		 '\uFFFF'
 	 };
 	 /**
 	  * 取得汉字拼音简码（首字母）
 	  * @param tempstr
 	  * @return
 	  */
 	 public static String getPinYin(String tempstr) {
 	  if(tempstr==null){
		  return "";
	  }
	  byte[] tempArr = null;
	  try {
	  	  // UTF-8编码下，中文转拼音报错，还原为GBK
		  tempArr = tempstr.trim().getBytes("GBK");
	  } catch (UnsupportedEncodingException e) {
		  e.printStackTrace();
	  }
	  StringBuffer pinYin=new StringBuffer();
 	  char tempchar;
 	  int value=0;  
 	  int i=0;
 	  while(i<tempArr.length)
 	  {
 	    if(tempArr[i]>0)
 	    {
 	     if(((char)(tempArr[i])!=' ')&&((char)(tempArr[i])!='-'))
 	    	pinYin.append((char)(tempArr[i]));
 	     i++;
 	     continue;
 	    }
 	    value=(tempArr[i]<0?(tempArr[i]+256):tempArr[i])*256+(tempArr[i+1]<0?tempArr[i+1]+256:tempArr[i+1]);
 	    //判断是否是全角字符
 	    if (value>=chrPinYinStart && value<chrPinYinStart+26)//26个字母
 	    {
 	    	tempchar=(char)(value -chrPinYinStart +65);//65为Ａ
 	    	pinYin.append(tempchar);
 	    }
 	    else
 	    { 
		    for(int j=0;j<pinYinRange.length;j++)
		    {
		     if(pinYinRange[j]>value)
		     {
		      if(j==0)
		    	  pinYin.append("A");
		      else if(j+65-1=='J')//因为汉语内没有以“I”开头的拼音，遇到这种情况就是遇到了“H” 
		    	  pinYin.append("H");
		      else if(j+65-1=='W')//没有以“U、V”开头的拼音，遇到这种情况就是遇到了“T” 
		    	  pinYin.append("T");
		      else
		      {
		    	  tempchar=(char)(j+65-1-1);//-1-1的原因是因为-1A从1指针开始,另外一个1减是因为实际的拼音在j-1位置
		    	  pinYin.append(tempchar);
		      }
		      break;
		     }
		    }//for j loop end.
		   } 
 	       i+=2;
 	  }//for while loop end.
 	  return pinYin.toString().toLowerCase(); 
 	 }
 	 /**
 	  * 拼音简码二
 	  * @param a
 	  * @return
 	  */
 	 public static String getPinym(String a) {   
 		 
 		 //使用新的第三方汉字转码拼音简码 update xuj 2014-12-27
 		 return PinyinUtil.stringToHeadPinYin(a);
 		 
		   /* //汉字区位码   
 		 
		    int li_SecPosValue[] = {1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594,   
		                           2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,   
		                           4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590};   
		    //存放国标一级汉字不同读音的起始区位码对应读音   
		    char lc_FirstLetter[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',   
		                            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',   
		                            'T', 'W', 'X', 'Y', 'Z'};   
		    //二级字库偏移量   
		    int ioffset = 0;   
		    ////存放所有国标二级汉字读音   
		    java.lang.String ls_SecondSecTable =   
		            "CJWGNSPGCGNE[Y[BTYYZDXYKYGT[JNNJQMBSGZSCYJSYY" +   
		            "[PGKBZGY[YWJKGKLJYWKPJQHY[W[DZLSGMRYPYWWCCKZNKYYGTTNJJNYKKZYTCJNMCYLQLYPYQFQRPZSLWBTGKJFYXJWZLTBNCXJJJJTXDTTSQZYCDXXHGCK" +   
		            "[PHFFSS[YBGXLPPBYLL[HLXS[ZM[JHSOJNGHDZQYKLGJHSGQZHXQGKEZZWYSCSCJXYEYXADZPMDSSMZJZQJYZC[J" +   
		            "[WQJBYZPXGZNZCPWHKXHQKMWFBPBYDTJZZKQHYLYGXFPTYJYYZPSZLFCHMQSHGMXXSXJ[" +   
		            "[DCSBBQBEFSJYHXWGZKPYLQBGLDLCCTNMAYDDKSSNGYCSGXLYZAYBNPTSDKDYLHGYMYLCXPY" +   
		            "[JNDQJWXQXFYYFJLEJPZRXCCQWQQSBNKYMGPLBMJRQCFLNYMYQMSQYRBCJTHZTQFRXQHXMJJCJLXQGJMSHZKBSWYEMYLTXFSYDSWLYCJQXSJNQBSCTYHBFTDCYZDJWY" +   
		            "GHQFRXWCKQKXEBPTLPXJZSRMEBWHJLBJSLYYSMDXLCLQKXLHXJRZJMFQHXHWYWSBHTRXXGLHQHFNM[YKLDYXZPYLGG[MTCFPAJJZYLJTYANJGBJPLQGDZYQY" +   
		            "AXBKYSECJSZNSLYZHSXLZCGHPXZHZNYTDSBCJKDLZAYFMYDLEBBGQYZKXGLDNDNYSKJSHDLYXBCGHXYPKDJMMZNGMMCLGWZSZXZJFZNMLZZTHCSYDBDLLSCDD" +   
		            "NLKJYKJSYCJLKWHQASDKNHCSGANHDAASHTCPLCPQYBSDMPJLPZJOQLCDHJJYSPRCHN[NNLHLYYQYHWZPTCZGWWMZFFJQQQQYXACLBHKDJXDGMMYDJXZLLSYGX" +   
		            "GKJRYWZWYCLZMSSJZLDBYD[FCXYHLXCHYZJQ[[QAGMNYXPFRKSSBJLYXYSYGLNSCMHZWWMNZJJLXXHCHSY[[TTXRYCYXBYHCSMXJSZNPWGPXXTAYBGAJCXLY" +   
		            "[DCCWZOCWKCCSBNHCPDYZNFCYYTYCKXKYBSQKKYTQQXFCWCHCYKELZQBSQYJQCCLMTHSYWHMKTLKJLYCXWHEQQHTQH[PQ" +   
		            "[QSCFYMNDMGBWHWLGSLLYSDLMLXPTHMJHWLJZYHZJXHTXJLHXRSWLWZJCBXMHZQXSDZPMGFCSGLSXYMJSHXPJXWMYQKSMYPLRTHBXFTPMHYXLCHLHLZY" +   
		            "LXGSSSSTCLSLDCLRPBHZHXYYFHB[GDMYCNQQWLQHJJ[YWJZYEJJDHPBLQXTQKWHLCHQXAGTLXLJXMSL[HTZKZJECXJCJNMFBY[SFYWYBJZGNYSDZSQYRSLJ" +   
		            "PCLPWXSDWEJBJCBCNAYTWGMPAPCLYQPCLZXSBNMSGGFNZJJBZSFZYNDXHPLQKZCZWALSBCCJX[YZGWKYPSGXFZFCDKHJGXDLQFSGDSLQWZKXTMHSBGZMJZRGLYJ" +   
		            "BPMLMSXLZJQQHZYJCZYDJWBMYKLDDPMJEGXYHYLXHLQYQHKYCWCJMYYXNATJHYCCXZPCQLBZWWYTWBQCMLPMYRJCCCXFPZNZZLJPLXXYZTZLGDLDCKLYRZZGQTG" +   
		            "JHHGJLJAXFGFJZSLCFDQZLCLGJDJCSNZLLJPJQDCCLCJXMYZFTSXGCGSBRZXJQQCTZHGYQTJQQLZXJYLYLBCYAMCSTYLPDJBYREGKLZYZHLYSZQLZNWCZCLLWJQ" +   
		            "JJJKDGJZOLBBZPPGLGHTGZXYGHZMYCNQSYCYHBHGXKAMTXYXNBSKYZZGJZLQJDFCJXDYGJQJJPMGWGJJJPKQSBGBMMCJSSCLPQPDXCDYYKY[CJDDYYGYWRHJRTGZ" +   
		            "NYQLDKLJSZZGZQZJGDYKSHPZMTLCPWNJAFYZDJCNMWESCYGLBTZCGMSSLLYXQSXSBSJSBBSGGHFJLYPMZJNLYYWDQSHZXTYYWHMZYHYWDBXBTLMSYYYFSXJC[DXX" +   
		            "LHJHF[SXZQHFZMZCZTQCXZXRTTDJHNNYZQQMNQDMMG[YDXMJGDHCDYZBFFALLZTDLTFXMXQZDNGWQDBDCZJDXBZGSQQDDJCMBKZFFXMKDMDSYYSZCMLJDSYNSBRS" +   
		            "KMKMPCKLGDBQTFZSWTFGGLYPLLJZHGJ[GYPZLTCSMCNBTJBQFKTHBYZGKPBBYMTDSSXTBNPDKLEYCJNYDDYKZDDHQHSDZSCTARLLTKZLGECLLKJLQJAQNBDKKGHP" +   
		            "JTZQKSECSHALQFMMGJNLYJBBTMLYZXDCJPLDLPCQDHZYCBZSCZBZMSLJFLKRZJSNFRGJHXPDHYJYBZGDLQCSEZGXLBLGYXTWMABCHECMWYJYZLLJJYHLG[DJLSLY" +   
		            "GKDZPZXJYYZLWCXSZFGWYYDLYHCLJSCMBJHBLYZLYCBLYDPDQYSXQZBYTDKYXJY[CNRJMPDJGKLCLJBCTBJDDBBLBLCZQRPPXJCJLZCSHLTOLJNMDDDLNGKAQHQH" +   
		            "JGYKHEZNMSHRP[QQJCHGMFPRXHJGDYCHGHLYRZQLCYQJNZSQTKQJYMSZSWLCFQQQXYFGGYPTQWLMCRNFKKFSYYLQBMQAMMMYXCTPSHCPTXXZZSMPHPSHMCLMLDQF" +   
		            "YQXSZYYDYJZZHQPDSZGLSTJBCKBXYQZJSGPSXQZQZRQTBDKYXZKHHGFLBCSMDLDGDZDBLZYYCXNNCSYBZBFGLZZXSWMSCCMQNJQSBDQSJTXXMBLTXZCLZSHZCXRQ" +   
		            "JGJYLXZFJPHYMZQQYDFQJJLZZNZJCDGZYGCTXMZYSCTLKPHTXHTLBJXJLXSCDQXCBBTJFQZFSLTJBTKQBXXJJLJCHCZDBZJDCZJDCPRNPQCJPFCZLCLZXZDMXMPH" +   
		            "JSGZGSZZQLYLWTJPFSYASMCJBTZKYCWMYTCSJJLJCQLWZMALBXYFBPNLSFHTGJWEJJXXGLLJSTGSHJQLZFKCGNNNSZFDEQFHBSAQTGYLBXMMYGSZLDYDQMJJRGBJ" +   
		            "TKGDHGKBLQKBDMBYLXWCXYTTYBKMRTJZXQJBHLMHMJJZMQASLDCYXYQDLQCAFYWYXQHZ";   
		  
		    java.lang.String sreturn = "";   
		    for (int j = 0; j < a.length(); j++) {   
		        String stemp = a.substring(j, j + 1);   
		        byte[] by =stemp.getBytes();  
		        if (by.length == 1) {   
		            sreturn = sreturn + stemp;   
		        } else {   
		            int ia = 96 + (int) by[0]; //区码   
		            int ib = 96 + (int) by[1]; //位码   
		            int in = ia * 100 + ib;   
		            if (in > 1600 && in < 5590) {   
		                for (int i = 0; i < 24; i++) {   
		                    if (in < li_SecPosValue[i]) {   
		                        sreturn = sreturn + lc_FirstLetter[i - 1];   
		                        break;   
		                    }   
		                }   
		            } else {   
		                ioffset = (ia - 56) * 94 + ib - 1;   
		                if (ioffset >= 0 && ioffset <= 3007) {   
		                    sreturn = sreturn +   
		                              ls_SecondSecTable.substring(ioffset,   
		                            ioffset + 1);   
		                }   
		            }   
		        }   
		        sreturn = sreturn.toLowerCase();   
		    }   
		    return sreturn;   */
		}  
 	 /**
 	  * 列表转换成以","分隔开的字符串
 	  * @param list
 	  * @return
 	  */
 	 public static String arrayToString(List list)
 	 {
 		 StringBuffer buf=new StringBuffer();
 		 buf.append(","); 			
 		 for(int i=0;i<list.size();i++)
 		 {
 			buf.append(list.get(i).toString());
 	 		 buf.append(","); 			
 		 }

 		 return buf.toString();
 	 }
 	 /**
 	  * 拆分出最顶的组织机构
 	  * @param org_dept ,以“,”分隔开的字符串
 	  * @return
 	  */
 	 public static String getHighOrgDept(String org_dept)
 	 {
 		 StringBuffer buf=new StringBuffer();
 		 String[] strS=StringUtils.split(org_dept,",");
 		 String[] strD=StringUtils.split(org_dept,",");
 		 List listd=Arrays.asList(strD);
 		 ArrayList listidx=new ArrayList();
 		 int sl=0,dl=0;
		 StringBuffer dbuf=new StringBuffer();
 		 for(int i=0;i<strS.length;i++)
 		 {
 			 String id=strS[i];
 			 sl=id.length();
 			 listidx.clear();
 			 for(int j=0;j<listd.size();j++)
 			 {
 				 String id_d=(String)listd.get(j);
 				 dl=id_d.length();
 				 if(dl<=sl)
 					 continue;
 				 if(id_d.startsWith(id))
 				 {
 					 listidx.add(String.valueOf(j));
 				 }
 			 }
 			 //去掉下层代码类
 			 dbuf.setLength(0);

 			 String stridx=arrayToString(listidx);
 			 for(int j=0;j<listd.size();j++)
 			 {
 				 if(listidx.size()==0)
 				 {
						 dbuf.append(listd.get(j));
 						 dbuf.append(",");
 						 continue;
 				 }
 				 String tmp=","+String.valueOf(j)+",";
 				 if(stridx.indexOf(tmp)==-1)
 				 {
					dbuf.append(listd.get(j));
 					dbuf.append(","); 					 
 				 }
 			 }//for j
 			 strD=StringUtils.split(dbuf.toString(),",");
 			 listd=Arrays.asList(strD);
 			 
 		 }//
 		 for(int i=0;i<listd.size();i++)
 		 {
 			 buf.append(listd.get(i));
 			 buf.append(",");
 		 }
 		 return buf.toString();
 	 }
	 /**
 	  * 拆分出最顶的组织机构
 	  * @param org_dept ,以“`”分隔开的字符串
 	  * @return
 	  */
 	 public static String getTopOrgDept(String org_dept)
 	 {
 		 if(org_dept==null||org_dept.length()<3){
 			return org_dept; 
 		 }
 		 String[] strS=StringUtils.split(org_dept,"`");
 		 if(strS.length<1){
 			 return org_dept;
 		 }
 		 String nid="";
 		 String ids="";
 		 
 		 //如果包含“UN`”说明有所有的机构权限，直接查出顶层机构id
 		 if(org_dept.indexOf("UN`")!=-1){
 			 Connection conn = null;
 			 RowSet rs = null;
 			 try{
 				 conn = AdminDb.getConnection();
 				 String sql = "select codeitemid,codesetid from organization where parentid=codeitemid and "+Sql_switcher.dateValue(DateStyle.dateformat(new Date(), "yyyy-MM-dd"))+" between start_date and end_date ";
 				 rs = new ContentDAO(conn).search(sql);
 				 while(rs.next()){
 					 nid+=rs.getString("codesetid")+rs.getString("codeitemid")+"`";  //rs.getString("codeitemid")+"`"   Su登录系统，查看薪资标准表归属单位，机构树显示为空 dengcan 2014-10-17
 				 }
 			 }catch(Exception e){
 				 e.printStackTrace();
 			 }finally{
 				 OrgMapBo.closeDBTool(conn);
 				 OrgMapBo.closeDBTool(rs);
 			 }
 			 
 			 return nid;
 		 }
 		 
 		 
 		 for(int i=0;i<strS.length;i++){
 			 String id = strS[i];
 			 if(id!=null&&id.length()>1){
 				 boolean check = true;
 				 for(int j=0;j<strS.length;j++){
 					 String id_s = strS[j];
 					 if(id_s!=null&&id_s.length()>1){
 						 if(id.length()>id_s.length()){
 							if(id.substring(2,id.length()).startsWith(id_s.substring(2,id_s.length()))){
								 check = false;
								 ids=id_s;
								 break;
							 }
 						 }else{
 							 if(id.equalsIgnoreCase(id_s)){
 								 continue;
 							 }
 							 if(id_s.substring(2,id_s.length()).startsWith(id.substring(2,id.length()))){
 								 check = false;
 								ids=id_s;
 								 break;
 							 }
 						 }
 					 }
 				 }
 				 if(check){
 					if(nid.indexOf(id)==-1)
 						nid+=id+"`";
 				 }else{
 					 if(id.length()<ids.length()){
 						if(nid.indexOf(id)==-1)
 	 						nid+=id+"`";
 					 }
 				 }
 			 }
 		 }
 		
 		 return nid;
 	 }
 	/**
 	 * 取得拼音简码
 	 * @param strHz
 	 * @return
 	 */
 	public static String getPinyinCode(String strHz)
 	{
 		StringBuffer buf=new StringBuffer();
 		try
 		{
 			ArrayList list=toUnicode(strHz);
 			char[]arChar=strHz.toCharArray();
 			Charset charset =  Charset.forName("UTF-8");
 			CharsetEncoder encoder =charset.newEncoder();
 		
 			
 			for(int i=0;i<arChar.length;i++)
 			{
 				//String code=(String)list.get(i);
 				
 				int key=(int)arChar[i];//Integer.parseInt(code, 16);

                if (key >= '\uB0A1' && key <= '\uB0C4')
                {
                	buf.append("a");
                }
                else if (key >= '\uB0C5' && key <= '\uB2C0')
                {
                	buf.append("b");
                }
                else if (key >= '\uB2C1' && key <= '\uB4ED')
                {
                	buf.append("c");                        
                }
                else if (key >= '\uB4EE' && key <= '\uB6E9')
                {
                	buf.append("d");                         
                }
                else if (key >= '\uB6EA' && key <= '\uB7A1')
                {
                	buf.append("e");                          
                }
                else if (key >= '\uB7A2' && key <= '\uB8C0')
                {
                	buf.append("f");                          
                }
                else if (key >= '\uB8C1' && key <= '\uB9FD')
                {
                	buf.append("g");                          
                }
                else if (key >= '\uB9FE' && key <= '\uBBF6')
                {
                	buf.append("h");                          
                }
                else if (key >= '\uBBF7' && key <= '\uBFA5')
                {
                	buf.append("j");                          
                }
                else if (key >= '\uBFA6' && key <= '\uC0AB')
                {
                	buf.append("k");                          
                }
                else if (key >= '\uC0AC' && key <= '\uC2E7')
                {
                	buf.append("l");                          
                }
                else if (key >= '\uC2E8' && key <= '\uC4C2')
                {
                	buf.append("m");                           
                }
                else if (key >= '\uC4C3' && key <= '\uC5B5')
                {
                	buf.append("n");                           
                }
                else if (key >= '\uC5B6' && key <= '\uC5BD')
                {
                	buf.append("o");                           
                }
                else if (key >= '\uC5BE' && key <= '\uC6D9')
                {
                	buf.append("p");                           
                }
                else if (key >= '\uC6DA' && key <= '\uC8BA')
                {
                	buf.append("q");                           
                }
                else if (key >= '\uC8BB' && key <= '\uC8F5')
                {
                	buf.append("r");                           
                }
                else if (key >= '\uC8F6' && key <= '\uCBF9')
                {
                	buf.append("s");                           
                }
                else if (key >= '\uCBFA' && key <= '\uCDD9')
                {
                	buf.append("t");                           
                }
                else if (key >= '\uCDDA' && key <= '\uCEF3')
                {
                	buf.append("w");                           
                }
                else if (key >= '\uCEF4' && key <= '\uD188')
                {
                	buf.append("x");                           
                }
                else if (key >= '\uD1B9' && key <= '\uD4D0')
                {
                	buf.append("y");                           
                }
                else if (key >= '\uD4D1' && key <= '\uD7F9')
                {
                	buf.append("z");                           
                }
                else
                {
                	buf.append(arChar[i]);                           
                }
 				
 			}//for i loop end.
 			//buf.append(buf.toString());
 			/*
 			byte[] hzbyte=strHz.getBytes("UTF-16");
            int key = 0;
 			for(int i=0;i<hzbyte.length;i++)
 			{
 				int itmp=getUnsignValue(hzbyte[i]);
                if ( itmp<= 127)
                {
                    buf.append((char)hzbyte[i]);
                    i++;
                }
                else//否则生成汉字拼音简码,取拼音首字母
                {
     				int pre=getUnsignValue(hzbyte[i]);
     				int last=getUnsignValue(hzbyte[i+1]);
                    key = pre * 256 + last;
                    if (key >= '\uB0A1' && key <= '\uB0C4')
                    {
                    	buf.append("a");
                    }
                    else if (key >= '\uB0C5' && key <= '\uB2C0')
                    {
                    	buf.append("b");
                    }
                    else if (key >= '\uB2C1' && key <= '\uB4ED')
                    {
                    	buf.append("c");                        
                    }
                    else if (key >= '\uB4EE' && key <= '\uB6E9')
                    {
                    	buf.append("d");                         
                    }
                    else if (key >= '\uB6EA' && key <= '\uB7A1')
                    {
                    	buf.append("e");                          
                    }
                    else if (key >= '\uB7A2' && key <= '\uB8C0')
                    {
                    	buf.append("f");                          
                    }
                    else if (key >= '\uB8C1' && key <= '\uB9FD')
                    {
                    	buf.append("g");                          
                    }
                    else if (key >= '\uB9FE' && key <= '\uBBF6')
                    {
                    	buf.append("h");                          
                    }
                    else if (key >= '\uBBF7' && key <= '\uBFA5')
                    {
                    	buf.append("j");                          
                    }
                    else if (key >= '\uBFA6' && key <= '\uC0AB')
                    {
                    	buf.append("k");                          
                    }
                    else if (key >= '\uC0AC' && key <= '\uC2E7')
                    {
                    	buf.append("l");                          
                    }
                    else if (key >= '\uC2E8' && key <= '\uC4C2')
                    {
                    	buf.append("m");                           
                    }
                    else if (key >= '\uC4C3' && key <= '\uC5B5')
                    {
                    	buf.append("n");                           
                    }
                    else if (key >= '\uC5B6' && key <= '\uC5BD')
                    {
                    	buf.append("o");                           
                    }
                    else if (key >= '\uC5BE' && key <= '\uC6D9')
                    {
                    	buf.append("p");                           
                    }
                    else if (key >= '\uC6DA' && key <= '\uC8BA')
                    {
                    	buf.append("q");                           
                    }
                    else if (key >= '\uC8BB' && key <= '\uC8F5')
                    {
                    	buf.append("r");                           
                    }
                    else if (key >= '\uC8F6' && key <= '\uCBF9')
                    {
                    	buf.append("s");                           
                    }
                    else if (key >= '\uCBFA' && key <= '\uCDD9')
                    {
                    	buf.append("t");                           
                    }
                    else if (key >= '\uCDDA' && key <= '\uCEF3')
                    {
                    	buf.append("w");                           
                    }
                    else if (key >= '\uCEF4' && key <= '\uD188')
                    {
                    	buf.append("x");                           
                    }
                    else if (key >= '\uD1B9' && key <= '\uD4D0')
                    {
                    	buf.append("y");                           
                    }
                    else if (key >= '\uD4D1' && key <= '\uD7F9')
                    {
                    	buf.append("z");                           
                    }
                    else
                    {
                    	buf.append("?");                           
                    }
                }
 			}//for i loop end.
 			*/
 		}
 		catch(Exception ex)
 		{
 			ex.printStackTrace();
 		}
 		return buf.toString();
 	}
 	
 	/**
 	 * md5加密
 	 * @param account  登陆账号
 	 * @param random  随机数
 	 * @param md5Password  密钥
 	 * @return
 	 */
 	public static String getMD5Encrypt(String account,String random,String md5Password)
 	{
 		/*
		 * 说明：
		 * 预计我方访问链接为http://yuming/login.do?account=xiaoming&random=112&md5Result=51d499af17b89aeca7c20daeea10b8c3
		 * 上面是GET方式，为了不暴露参数，最终是要用POST方式来访问，但上面的三个参数是不变的，account=帐号，random=随机数，md5Result=加密后的值
		 * 算法描述:
		 * account+random+协议好的密钥 三个字符串相加，得到的结果与md5Result比较，如果相同，则说明登陆成功，否则，登陆失败
		*/
		/*String account = "xiaoming";//为从request中得到的account参数值
		String random = "112";//为从request中得到的random参数值
		String md5Result = "51d499af17b89aeca7c20daeea10b8c3";//为从request中得到的md5Result参数值
		String md5Password = "B6J2E1E1A3A6E3E5JB";//协议好的密码，最好是可配置的，因为有可能会变
*/		if(md5Password==null||md5Password.length()<=0)
 		    md5Password = "B6J2E1E1A3A6E3E5JB";
		String inputStr = account + random + md5Password;
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',  'e', 'f'}; 
		String returnStr="";
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] arg = inputStr.getBytes();
			digest.update(arg);
			byte[] tmp = digest.digest();
			char str[] = new char[16 * 2];   
			int k = 0;
			for (int i = 0; i < 16; i++) 
			{
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];  
				str[k++] = hexDigits[byte0 & 0xf]; 
			}
			returnStr = new String(str);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return returnStr;
		/*System.out.println(s);
		//如果相同，则说明登陆成功
		if(s.equals(md5Result))
		{
			System.out.println("登陆成功");
		}
		else
		{
			System.out.println("登陆失败");
		}*/
 	}
 	
 	public static void writeFile(String value) {  
		BufferedWriter output  = null;
        try{  
            File file = new File("c:/c.txt");  
            if (file.exists()) {                 
                file.delete();  
            }     
            file.createNewFile();     
            output = new BufferedWriter(new FileWriter(file));   
            output.write(value);  
            output.flush();
        } catch (Exception ex) {  
            ex.printStackTrace(); 
        } finally{
        	closeResource(output);
        }
	}
 	/**
 	 * keyValue为整型
 	 * @param file
 	 * @param tablename
 	 * @param fileColumn
 	 * @param keyColumn
 	 * @param keyValue
 	 * @param conn
 	 * @return
 	 * @throws FileNotFoundException
 	 * @throws IOException
 	 */
 	public static Blob getOracleBlob(FormFile file,String tablename,String fileColumn,String keyColumn,int keyValue,Connection conn) throws FileNotFoundException, IOException {
 		return getOracleBlobReal(file, tablename, fileColumn, keyColumn, keyValue+"", conn ,"int");
	}
 	/**
 	 * keyValue为字符串
 	 * @param file
 	 * @param tablename
 	 * @param fileColumn
 	 * @param keyColumn
 	 * @param keyValue
 	 * @param conn
 	 * @return
 	 * @throws FileNotFoundException
 	 * @throws IOException
 	 */
 	public static Blob getOracleBlob(FormFile file,String tablename,String fileColumn,String keyColumn,String keyValue,Connection conn) throws FileNotFoundException, IOException {
		return getOracleBlobReal(file, tablename, fileColumn, keyColumn, keyValue, conn ,"string");
	}

	private static Blob getOracleBlobReal(FormFile file, String tablename,
			String fileColumn, String keyColumn, String keyValue,
			Connection conn ,String keyType) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select "+fileColumn+" from ");
		strSearch.append(tablename);
		strSearch.append(" where "+keyColumn+"=");
		if("string".equals(keyType))
			strSearch.append("'"+keyValue+"'");
		else
			strSearch.append(keyValue);	
		 strSearch.append(" FOR UPDATE");
		 String fname = file.getFileName();
         int indexInt = fname.lastIndexOf(".");
         String ext = fname.substring(indexInt, fname.length());
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set "+fileColumn+"=EMPTY_BLOB() where "+keyColumn+"=");
		if("string".equals(keyType))
			strSearch.append("'"+keyValue+"'");
		else
			strSearch.append(keyValue);
	    OracleBlobUtils blobutils=new OracleBlobUtils(conn);
	    InputStream _in = null;
	    try {
		    _in = file.getInputStream();
		    if (isImageFile(ext))
		    	_in = ImageBO.imgStream(file, ext.substring(1)); // 复制图片，过滤掉木马程序
			Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),_in);
			return blob;
	    } finally {
	    	closeIoResource(_in);
	    }
	}
 	
	/**
     * 是否是图片文件（jbp,gif,bmp)
     * 
     * @Title: isImageFile
     * @Description:
     * @param fileExt
     *            文件扩展名
     * @return
     */
    private static boolean isImageFile(String fileExt) {
        String ext = fileExt;
        if (ext.startsWith("."))
            ext = ext.substring(1);

        return "jpg".equalsIgnoreCase(fileExt.substring(1))
                || "jpeg".equalsIgnoreCase(fileExt.substring(1))
                || "gif".equalsIgnoreCase(fileExt.substring(1))
                || "bmp".equalsIgnoreCase(fileExt.substring(1))
                || "png".equalsIgnoreCase(fileExt.substring(1));
    }
    
 	//解决sqlserver行大小不能超过8060问题   dengcan
 	public static void  resolve8060(Connection conn,String tablename)
 	{
 		try
 		{
 			if(Sql_switcher.searchDbServer()==1)
 			{
	 			ContentDAO dao=new ContentDAO(conn);
	 			DatabaseMetaData dbMeta = conn.getMetaData();
				ResultSet rs=dbMeta.getIndexInfo(null,null,tablename, false, false);
				String index_name="";
				int type=0;  //1：聚集索引
				while(rs.next())
				{
						 if(rs.getInt("TYPE")==1)
						 {
							 index_name=rs.getString("INDEX_NAME");
							 break;
						 } 
				}
				if(rs!=null)
						 rs.close();
				if(index_name!=null&&index_name.trim().length()>0)
				{
					int version=dbMeta.getDatabaseMajorVersion();  //  sql2000=8    sql2005=9    sql2008=10    sql2012=11
					if(version==8)
					{
							 String dbname = SystemConfig.getPropertyValue("dbname");
							 dao.update("DBCC DBREINDEX ('"+dbname+".dbo."+tablename+"',"+index_name+", 70) ");
					}
					else
					{
							 dao.update("ALTER INDEX "+index_name+" ON "+tablename+" REBUILD ");
					} 
				}
 			}
 		}
 		catch(Exception e)
 		{
 			e.printStackTrace();
 		}
 	}
 	
 	
 	/***
 	 * 以下3个方法，用sql语句得到日期型的时分秒，返回值均为数字，如6时返回的是6而不是06
 	 * @param column
 	 * @return
 	 */
 	public static String hour(String column){
 		StringBuffer sql = new StringBuffer();
 		switch(Sql_switcher.searchDbServer()){
 		case Constant.ORACEL:
 			sql.append(" to_number(to_char("+column+",'hh24')) ");
 			break;
 		case Constant.MSSQL:
 			sql.append(" datepart(hour,"+column+") ");
 			break;
 		case Constant.DB2:
 			sql.append(" hour("+column+") ");
 			break;
 		default:
 			sql.append(" "+column+" ");
 			break;
 		}
 		return sql.toString();
 	}
 	public static String minute(String column){
 		StringBuffer sql = new StringBuffer();
 		switch(Sql_switcher.searchDbServer()){
 		case Constant.ORACEL:
 			sql.append(" to_number(to_char("+column+",'mi')) ");
 			break;
 		case Constant.MSSQL:
 			sql.append(" datepart(minute,"+column+") ");
 			break;
 		case Constant.DB2:
 			sql.append(" minute("+column+") ");
 			break;
 		default:
 			sql.append(" "+column+" ");
 			break;
 		}
 		return sql.toString();
 	}
 	public static String second(String column){
 		StringBuffer sql = new StringBuffer();
 		switch(Sql_switcher.searchDbServer()){
 		case Constant.ORACEL:
 			sql.append(" to_number(to_char("+column+",'ss')) ");
 			break;
 		case Constant.MSSQL:
 			sql.append(" datepart(second,"+column+") ");
 			break;
 		case Constant.DB2:
 			sql.append(" second("+column+") ");
 			break;
 		default:
 			sql.append(" "+column+" ");
 			break;
 		}
 		return sql.toString();
 	}
 	/*
 	 * 判断公式中是否用到了要改名的临时变量  郭峰
 	 * strFactor  公式表达式  oldname  旧名字
 	 * **/
 	public static boolean IsHasVariable(String strFactor,String oldname){
 		boolean returnvalue = false;
		if(strFactor==null|| "".equals(strFactor)){
			return returnvalue;
		}
		int lastindex = 0;//记录上一次到了那个位置
		int index = strFactor.indexOf(oldname,lastindex);
		while(index!=-1){//如果找到了，说明cfactor中可能用到了该临时变量，则要进一步判断
			char leftchar = ' ';
			char rightchar = ' ';
			if(index==0) {
				rightchar = ' ';
				int tempindex = index+oldname.length();
				if(tempindex<strFactor.length()){
					rightchar = strFactor.charAt(tempindex);
				}
			} else {
				leftchar = strFactor.charAt(index-1);//得到左边字符
				int tempindex = index+oldname.length();
				rightchar = ' ';
				if(tempindex<strFactor.length()){
					rightchar = strFactor.charAt(tempindex);
				}
			}
			if(checkChar(leftchar,rightchar)){//如果公式中正好有这个变量
				returnvalue = true;
				break;
			}
			lastindex = index+1;
			index = strFactor.indexOf(oldname,lastindex);//之所以用while，是因为index是不断变化的
		}
		return returnvalue;
	}

	/*
	 * 根据左边和右边的字符判断是否是临时变量  郭峰
	 * leftchar:左边的字符  rightchar:右边的字符
	 **/
	public static boolean checkChar(char leftchar,char rightchar){
		boolean leftok = false;
		boolean rightok = false;
		String leftspecialchar =  "+-*/\\%!,=<>({[，~ ^;:；：\'\r\t\n`";//这些符号宁可少不可多。   还需要增加的是String specialchar = "^;:；：\'";
		String rightspecialchar = "+-*/\\%!,=<>)}]，~ ^;:；：\'\r\t\n`";//增加特殊字符“`” xcs 2013-11-5
		if(leftspecialchar.indexOf(leftchar)!=-1){
			leftok = true;
		}else{
			leftok = false;
		}	
		if(rightspecialchar.indexOf(rightchar)!=-1){
			rightok = true;
		}else{
			rightok = false;
		}
		if(leftok && rightok){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	/*
	 * 返回所有未共享的临时变量  郭峰
	 *conn：链接
	 *formula：表达式
	 *cmidvariablelist：所有的中文临时变量
	 *emidvariablelist：所有的英文临时变量
	 *notsharecondition：不共享的条件
	 *notsharelist：不共享的临时变量的中文名字，也就是函数的返回结果
	 *ergodiclist:已经遍历过的临时变量（以该临时变量为基础，遍历其表达式中的临时变量），防止死循环  注意，中文名字和英文名字都要加上
	 *处理逻辑：先找到表达式中所有不共享的，并存储起来。再把所有的临时变量（共享的和不共享的）的表达式中的所有不共享的再存起来。
	  **/
	public static ArrayList getMidVariableList(Connection conn,String formula,ArrayList cmidvariablelist,ArrayList emidvariablelist,String notsharecondition ,ArrayList notsharelist,ArrayList ergodiclist){
		try{
			//先找到表达式中所有的临时变量
			ArrayList templist = getAllMidVariableList(formula,cmidvariablelist,emidvariablelist);
			//再选出所有共享的临时变量，进入递归,并加入到sharelist中。不共享的临时变量就不参与递归了。不共享的临时变量需要加入到notsharelist中
			ArrayList clist = (ArrayList)templist.get(0);
			ArrayList elist = (ArrayList)templist.get(1);
			String cstring = getStringByList(clist);
			String estring = getStringByList(elist);
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			StringBuffer sb = new StringBuffer("");
			//把当前表达式中所有临时变量的非共享的先存起来
			sb.append("select nid,cname,chz from midvariable where (cname in ("+estring+") or chz in ("+cstring+"))");//cname是英文名字 chz是中文名字
			sb.append(" "+notsharecondition);
			rs = dao.search(sb.toString());
			while(rs.next()){
				String chz = rs.getString("chz")==null?"":rs.getString("chz");
				if("".equals(chz))
					continue;
				notsharelist.add(chz);
			}
			//对这个表达式中所有的临时变量进行递归。要先找出每个临时变量的表达式
			sb.setLength(0);
			sb.append("select nid,cname,chz,cvalue from midvariable where (cname in ("+estring+") or chz in ("+cstring+"))");
			rs = dao.search(sb.toString());
			ArrayList enamelist = new ArrayList();
			ArrayList cnamelist = new ArrayList();
			ArrayList valuelist = new ArrayList();
			while(rs.next()){
				String ename = rs.getString("ename");
				String cname = rs.getString("chz");
				String value = Sql_switcher.readMemo(rs, "value");
				enamelist.add(ename);
				cnamelist.add(cname);
				valuelist.add(value);
			}
			int n = enamelist.size();
			for(int i=0;i<n;i++){
				String tempename = (String)enamelist.get(i);
				String tempcname = (String)cnamelist.get(i);
				if(!(ergodiclist.contains(tempename) || ergodiclist.contains(tempcname))){
					String value = (String)valuelist.get(i);
					ergodiclist.add(tempename);
					ergodiclist.add(tempcname);
					getMidVariableList(conn,value,cmidvariablelist,emidvariablelist,notsharecondition,notsharelist,ergodiclist);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return notsharelist;
	}
	/**
	 * 找到表达式中的所有临时变量。  郭峰
	 * cmidvariablelist:包含临时变量表中所有临时变量的中文名字
	 * emidvariablelist:英文名字
	 * */
	public static ArrayList getAllMidVariableList(String formula,ArrayList cmidvariablelist,ArrayList emidvariablelist){
		ArrayList list = new ArrayList();
		ArrayList clist = new ArrayList();//中文临时变量列表
		ArrayList elist = new ArrayList();//英文临时变量列表
		if("".equals(formula)){
			return list;
		}
		//先处理中文名
		int count1 = cmidvariablelist.size();
		for(int i=0;i<count1;i++){
			String tempvarname = (String)cmidvariablelist.get(i);
			int lastindex = 0;//记录上一次到了那个位置
			int index = formula.indexOf(tempvarname,lastindex);
			while(index!=-1){//如果找到了，说明cfactor中可能用到了该临时变量，则要进一步判断
				char leftchar = formula.charAt(index-1);//得到左边字符
				int tempindex = index+tempvarname.length();
				char rightchar = formula.charAt(tempindex);
				if(checkChar(leftchar,rightchar)){//如果公式中正好有这个变量
					clist.add(tempvarname);
					break;
				}
				lastindex = index+1;
				index = formula.indexOf(tempvarname,lastindex);//之所以用while，是因为index是不断变化的
			}
		}
		//再处理英文名
		int count2 = cmidvariablelist.size();
		for(int i=0;i<count2;i++){
			String tempvarname = (String)emidvariablelist.get(i);
			int lastindex = 0;//记录上一次到了那个位置
			int index = formula.indexOf(tempvarname,lastindex);
			while(index!=-1){//如果找到了，说明cfactor中可能用到了该临时变量，则要进一步判断
				char leftchar = formula.charAt(index-1);//得到左边字符
				int tempindex = index+tempvarname.length();
				char rightchar = formula.charAt(tempindex);
				if(checkChar(leftchar,rightchar)){//如果公式中正好有这个变量
					elist.add(tempvarname);
					break;
				}
				lastindex = index+1;
				index = formula.indexOf(tempvarname,lastindex);//之所以用while，是因为index是不断变化的
			}
		}
		list.add(clist);
		list.add(elist);
		return list;
	}
	/**
	 * 通过list获得sql语句处理的字符串  郭峰
	 * */
	public static String getStringByList(ArrayList list){
		StringBuffer str = new StringBuffer("");
		if(list==null || list.size()==0){
			str.append("''");
		}else{
			int n = list.size();
			for(int i=0;i<n;i++){
				String temp = (String)list.get(i);
				str.append("'"+temp+"',");
			}
			str.setLength(str.length()-1);
		}
		return str.toString();
	}
	 
	/**
	 * 往dict_code.js文件中追加新增的组织机构信息
	 */
	public static void appendOrgToDictJS(String path,String appendText)
	{
		ByteBuffer buffer =null;
		FileOutputStream fileOut =null;
		FileChannel fc =null;
		File file = null;
//		BufferedReader in = null;
		try
		{
			String filename="dict_code.js"; // dict文件被拆分，组织机构追加到dict_code.js文件中 modify by 刘蒙
			String filepath= PubFunc.keyWord_reback(path)+System.getProperty("file.separator")+filename;
			file = new File(filepath);
			if(file.exists()){//判断dict_code.js是否存在，不存在则不操作。
				Path filePath = Paths.get(filepath);
				StringBuilder sb=new StringBuilder();
				try(BufferedReader in = Files.newBufferedReader(filePath)) {
					String s="";
					while((s=in.readLine())!=null){
						sb.append(s);
						sb.append("\r\n");
					}
					sb.append(appendText);
					
				};
				 
				fileOut = new FileOutputStream(filepath); 
				fc = fileOut.getChannel();
				buffer = ByteBuffer.allocate(sb.toString().getBytes().length);
			    buffer.put(sb.toString().getBytes());
			    buffer.flip();
			    fc.write( buffer );  
				System.gc();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

			if(buffer!=null)
				buffer.clear();

			closeResource(fileOut);
			closeResource(fc);
		}
	}
	/**
	 * 生成txt文件
	 * @param content
	 * @return
	 */
	public static String getTxtFile(String content){
		String filename = getStrg()+".txt";
		FileOutputStream os = null;
		try {
			 os = new FileOutputStream(new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+filename));
			 os.write((content + "\n").getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filename;
	}
	/**
	 * 生成txt文件
	 * @param content
	 * @return
	 */
	public static String getTxtFile(String content,String filename){
		FileOutputStream os = null;
		try {
			 os = new FileOutputStream(new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+filename));
			 os.write((content + "\n").getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filename;
	}
	/**
	 * @Title: isEmailAddress   
	 * @Description: 验证邮箱地址格式是否正确  
	 * @param @param emailAddress 邮箱地址字符串
	 * @return boolean true:正确，false:不正确   
	 */
	public static boolean isEmailAddress(String emailAddress) {
        if (emailAddress == null || "".equals(emailAddress.trim()))
            return false;
        
        String checkEmail= "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";   
        Pattern p = Pattern.compile(checkEmail);
        Matcher m = p.matcher(emailAddress.trim());
        return m.matches();
    }
	/**
	 * zzk 将加密的登录密码转为明文
	 * @param password
	 * @return
	 * @throws GeneralException 
	 */
	public static String getConvertPassWord(String password) throws GeneralException{
			Connection connection=null;
  			try
  			{
  			   	connection = (Connection) AdminDb.getConnection();
      			if(ConstantParamter.isEncPwd(connection)){
      				byte byt[]=Base64.decodeBase64(password);
      				Des des = new Des();
      				String realPassword= des.DecryStr(byt, "hjsj");
      				/*【49736】 webservice接口传来的密码是明文，业务办理发送邮件单点是密文。此处处理一下，如果解密失败，返回原密码 guodd 2019-07-03*/
      	  			if(!"".equals(realPassword)) {
      	  				String encryPwdStr = des.EncryPwdStr(realPassword);
      	  				if(encryPwdStr.equals(password)){//密码位数较多时，解密回来是乱码，不是空值，这里在加密看是否和解密前一致,判断传进来的密码是否是加密的。
      	  					password = realPassword;
      	  				}
      	  			}

      			}
  			}catch(Exception e)
  			{
  			  throw GeneralExceptionHandler.Handle(e);
  			}finally
  			{
  			  if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
  			}
		return password;
	}
	
	/**
     * 标准版、专业版功能区分
     * @param funcid
     * @param ver_s =1专业版 =0标准版
     * @return
     */
    public static boolean haveVersionFunc(UserView userview,String funcid,int ver_s)
    {
    	/**专业版控制功能号列表*/
    	String sfunc=",,";    	
    	/**普通版控制功能号列表*/
    	
//    	int version = userview.getVersion();
//    	String _funcid = ",9A51,9A52";
//    	if(version>=60)
//    		_funcid = "";
//    	
//    	//专业版特有的功能
//    	StringBuffer tmpstr = new StringBuffer(",23064,2315,23057,23056,25059,23058,23062,25066,33101026,360,26012,2601004,3260103,32307,32306A,32306B,32332,32363,27053,27054,27052,27044,32416,324021501,3240214,324073,32411,324021502,3240214,32516,32203,2906,9A4,080809,30015H,30015A,3001E,080808,3001G,030701,1113,060803,0610,060605,0B26,0B11,0C346,0C33,0C36,0C39,0C40,0C347,32026,37025,37125,37225,37325,324010123,325010123,32405,");//3001D,3001F, xuj update 标准版放开有数据交换功能
//    	tmpstr.append("2306007,23151,0501021,230511,23067,23110107,23110110,231102,231706,231707,25012,2602306,04010106,2602308,26025,2311036,2311037,3221002,2602307,326040503,04010107,326030106,326030127,326030134,326030130,3237303,270302,27030a,27030c,27082,060708,");
//    	tmpstr.append("3240315,3260415,3260413,32405,324073,32411,32420,3250313,32516,3206"+_funcid+",290207,324080802,230500,230501,2706012,2703503,");
//    	if(ver_s==0){
//	    	int idx=tmpstr.toString().indexOf(","+funcid+",");
//	    	if(idx==-1)
//	    		return true;
//	    	else
//	    		return false;
//    	}else if(ver_s==1){
//    		int idx=sfunc.indexOf(","+funcid+",");
//	    	if(idx==-1)
//	    		return true;
//	    	else
//	    		return false;
//    	}
//    	return true;
    	
    	//zxj 20160629 7.x标准版与专业版调用FuncVersion类进行判断
    	return FuncVersion.haveVersionFunc(userview, funcid, ver_s);
    }
	
	/**
	 * 关闭数据库连接一系列对象  guodd 2014-11-26
	 * @param obj  支持类型：connection、rowset、resultset、statement、preparedstatement
	 * @return boolean 是否成功
	 */
	public static boolean closeDbObj(Object obj){
		boolean isClosed = true;
		PubFunc.closeResource(obj);
		return isClosed;
	}
	
	
	/**
	 * @Title closeIoResource
	 * @Description 关闭IO资源 
	 * @param obj  支持类型：
	 * <ul>
	 * <li>InputStream</li>
	 * <li>OutputStream</li>
	 * <li>Reader</li>
	 * <li>Writer</li>
	 * <li>ZipFile(子类包括:java.util.jar.JarFile)</li>
	 * <li>DocListener(子类包括:PdfWriter,DocWriter,com.lowagie.text.Document)</li>
	 * <li>IndexWriter</li>
	 * </ul>
	 * @author dengcan
	 * @return void
	 */
	public static void closeIoResource(Object obj) {
		PubFunc.closeResource(obj);
	}
	/**
	 * 关闭与数据源的连接,释放资源.是closeIoResource和closeDbObj的并集
	 * @param resource 可以是IO流也可以是数据库资源
	 * @author lium
	 */
	public static void closeResource(Object resource) {
		if (resource == null) {
			return;
		}

		try{
			/*基本所有的资源类都实现了此接口，例如jdbc对象、io对象等*/
			if(resource instanceof AutoCloseable){
				((AutoCloseable)resource).close();
			}
			// PdfWriter,DocWriter,com.lowagie.text.Document 没有实现AutoCloseable，需特殊判断
			else if (resource instanceof DocListener) {
				((DocListener) resource).close();
			}
			// lucene 对象没有实现AutoCloseable，需特殊判断
			else if (resource instanceof IndexWriter) {
				((IndexWriter) resource).close();
			}
		}catch (java.io.IOException ex){

		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 将DynaBean 转为 Map对象
	 * 适用范围：当DynaBean 不为 LazyDynaBean 时,比如通过ajax传入后台的json对象，会解析成
	 * net.sf.ezmorph.bean.MorphDynaBean。无法像lazydynabean.getMap().containsKey(string)方法来判断是否存在某个key
	 * 通过此方法将MorphDynaBean转为Map类型，方便使用
	 * @param dynaBean
	 * @return
	 */
	public static HashMap DynaBean2Map(DynaBean dynaBean){
		HashMap hashMap = new HashMap();
		if(dynaBean==null)
			return hashMap;
		//获取属性数组
		try{
			DynaProperty[] dp = dynaBean.getDynaClass().getDynaProperties();
			String name = "";
			Object value = null;
			for(int i=0;i<dp.length;i++){
				//获取属性名
				name = dp[i].getName();
				//获取值
				value = dynaBean.get(name);
				//如果value为DynaBean，继续转
				if(value instanceof DynaBean)
					value = DynaBean2Map((DynaBean) value);
				hashMap.put(name, value);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return hashMap;
	}
	/**
     * 替换文件名中的非法字符
     * @param filename 文件名称
     * @return filename 替换后的文件名
     */
    public static String filenameReplace(String filename) {
        if(filename == null || filename.length() < 1)
            return filename;
        
        filename=filename.replaceAll("/", "");
        filename=filename.replaceAll("[?]", "");
        filename=filename.replaceAll(">", "");
        filename=filename.replaceAll("<", "");
        filename=filename.replaceAll("[|]", "");
        filename=filename.replaceAll("\"", "");
        filename=filename.replaceAll("\\\\", "");
        filename=filename.replaceAll(":", "");
        filename=filename.replaceAll("[*]", "");
        return filename;
    }
    /**
     * 备注类型指标将空格（&nbsp;）、换行（<br>）截断时，去点显示的源码 注：有其它字符需要替换的再补充
     * @param str  要进行替换的字符串
     * @return
     */
    public static String substr(String str) {

        if (str.endsWith("&..."))
            str = str.substring(0, str.lastIndexOf("&...")) + "...";

        if (str.endsWith("&n..."))
            str = str.substring(0, str.lastIndexOf("&n...")) + "...";

        if (str.endsWith("&nb..."))
            str = str.substring(0, str.lastIndexOf("&nb...")) + "...";

        if (str.endsWith("&nbs..."))
            str = str.substring(0, str.lastIndexOf("&nbs...")) + "...";

        if (str.endsWith("&nbsp..."))
            str = str.substring(0, str.lastIndexOf("&nbsp...")) + "...";

        if (str.endsWith("<..."))
            str = str.substring(0, str.lastIndexOf("<...")) + "...";

        if (str.endsWith("<b..."))
            str = str.substring(0, str.lastIndexOf("<b...")) + "...";

        if (str.endsWith("<br..."))
            str = str.substring(0, str.lastIndexOf("<br...")) + "...";

        return str;

    }
    

    /**
     * 验证身份证  
     * @param idCard
     * @return
     */
	public static boolean idCardValidate(String idCard) { 

        if (idCard == null) {
        	return false;
        }
        
    	
        idCard = idCard.replaceAll(" ", "");//去掉字符串所有空格                     
        if (idCard.length() == 15) {   
            return isValidityBrithBy15IdCard(idCard);       //进行15位身份证的验证    
        } else if (idCard.length() == 18) {     
            if(isValidityBrithBy18IdCard(idCard)&&isTrueValidateCodeBy18IdCard(idCard)){   //进行18位身份证的基本验证和第18位的验证
                return true;   
            }else {   
                return false;   
            }   
        } else {   
            return false;   
        }   
    }   
    /**  
     * 判断身份证号码为18位时最后的验证位是否正确  
     * @param a_idCard 身份证号码数组  
     * @return  
     */  
    public static boolean isTrueValidateCodeBy18IdCard(String a_idCard) {   
        int sum = 0;                             // 声明加权求和变量  
        int[] Wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };    // 加权因子  
        int[] ValideCode = { 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 };            // 身份证验证位值.10代表X 
        
        char[] ch = a_idCard.toCharArray();
        for ( int i = 0; i < 17; i++) { 
        	
        	sum += Wi[i] * Integer.parseInt(ch[i] + "");            // 加权求和  

        }   
        int valCodePosition = sum % 11;                // 得到验证码所位置   
        
        if ((ch[17] == 'X' || ch[17] == 'x') && 10 == ValideCode[valCodePosition]){
        	return true;
        } else if ((ch[17] != 'X' && ch[17] != 'x') && Integer.parseInt(ch[17] + "") == ValideCode[valCodePosition]) {   
            return true;   
        } else {   
            return false;   
        }   
    }   
    /**  
      * 验证18位数身份证号码中的生日是否是有效生日  
      * @param idCard 18位书身份证字符串  
      * @return  
      */  
    public static boolean  isValidityBrithBy18IdCard(String idCard18){   
        String year =  idCard18.substring(6,10);   
        String month = idCard18.substring(10,12);   
        String day = idCard18.substring(12,14);   
        try {
        	Date temp_date = DateUtils.getDate(year + "-" + month + "-" + day, "yyyy-MM-dd"); 
        	if (temp_date != null) {
        		return true;
        	} else {
        		return false;
        	}
        } catch (Exception e) {
        	return false;
        }
        
          
    }   
      
      
      /**  
       * 验证15位数身份证号码中的生日是否是有效生日  
       * @param idCard15 15位书身份证字符串  
       * @return  
       */  
      public static boolean isValidityBrithBy15IdCard(String idCard15){   
    	  String reg = "^\\d{15}$";
          String year =  idCard15.substring(6,8);   
          String month = idCard15.substring(8,10);   
          String day = idCard15.substring(10,12);   
          try {
        	  Date temp_date = DateUtils.getDate("19" + year + "-" + month + "-" + day, "yyyy-MM-dd");    
        	  if (Pattern.matches(reg,idCard15)&&temp_date != null) {
        		  return true;
      			} else {
      				return false;
      			}
          } catch (Exception e) {
        	 return false;
          }
      } 
      

      /**
       * 保存文件到硬盘
       * @param path 路径（文件夹）
       * @param in 文件流
       * @param fileName 带后缀文件名(例如aaa.txt)
       * @return
       */
      public static boolean saveFileByPath(String path,InputStream in,String fileName){
	      boolean result = true;
	      OutputStream  out= null;
	      try{
	    	  String filePath = path.replace("\\", File.separator);
	      if (!filePath.endsWith(File.separator)) 
	        	 filePath =filePath+File.separator; 
    	    	  File tempDir = new File(path);
 	      if (!tempDir.exists())
 	           tempDir.mkdirs();
    	    	  
    	    	  filePath+=fileName;
    	    	  out = new FileOutputStream(filePath);
    	    	  int bytesRead = 0;
	      byte[] buffer = new byte[8192];
	      while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
	            out.write(buffer, 0, bytesRead);
	      }
	      out.flush();
	      }catch(Exception e){
	    	      e.printStackTrace();
	    	      result = false;
	      }finally{
	    	      closeResource(out);
	    	      closeResource(in);
	      }
	      return result;
      }
    
      
      
      /**
  	 * 校验数值
  	 * @param type    1:整数或者小数  2:只能输入零和非零开头的数字  3:只能输入非零的正整数 4:校验正整数和负整数
  	 * @return
  	 */
  	public static  boolean validateNum(String value,int type)
  	{
  		boolean bflag=true;
  		if(value==null|| "".equals(value))
  			return false;
  		try
  		{
  			String str="";
  			if(type==1)
  				str="^[0-9]+\\.{0,1}[0-9]{0,2}$";
  			else if(type==2)
  				str="^(0|[1-9][0-9]*)$";
  			else if(type==3)
  				str="^\\+?[1-9][0-9]*$";
  			else if(type==4)
  				str="^[-\\+]?[\\d]*$";
  			Pattern pattern = Pattern.compile(str);
  			bflag=pattern.matcher(value).matches();
  		}
  		catch(Exception ex)
  		{
  			bflag=false;
  		}
  		return bflag;
  	}
    
  	/**
	 * 排序list
	 * @param field_type 排序的指标类型  包括三种 "A"字符型 "N"数值型 "D"时间型 "M"不排序直接返回原始list
	 * @param sortType 倒序还是正序 "ASC" 正序 "DESC" 倒序
	 * @param field_name 排序的指标代码  确保list中有此字段
	 * @param codesetid 代码型指标代码类
     * @param disformat 日期型代码格式 “yyyy-MM-dd” 以及数字型代码（浮点型的小数位）
	 * @param dataList list中对象是LazyDynaBean -> {LazyDynaBean,LazyDynaBean,LazyDynaBean}
	 * @return
	 */
	public static ArrayList sortList(final String field_name, final String sortType,final String field_type,final String codesetid,final String disformat, ArrayList dataList) {
		try{
			Collections.sort(dataList, new Comparator() {  
				public int compare(Object o1, Object o2) {
					if(o1 instanceof LazyDynaBean && o2 instanceof LazyDynaBean){
						LazyDynaBean e1 = (LazyDynaBean) o1;
                    	LazyDynaBean e2 = (LazyDynaBean) o2;
                    	if("D".equalsIgnoreCase(field_type)){
							SimpleDateFormat simpleformat = new SimpleDateFormat(disformat);
	                		Date d=new Date();
	                		String current_str=simpleformat.format(d);
	                    	String e1_start_date=e1.get(field_name)!=null?(String)e1.get(field_name):current_str;
	                    	String e2_start_date=e2.get(field_name)!=null?(String)e2.get(field_name):current_str;
	                    	Date e1_date =d; 
	                    	Date e2_date =d;  
	                    	try{
	                    		e1_date=simpleformat.parse(e1_start_date);
	                    		e2_date=simpleformat.parse(e2_start_date);
	                    	}
	                    	catch(Exception e){	
	                    		e.printStackTrace();
	                    	}
	                    	int value=0;
	                    	if("ASC".equalsIgnoreCase(sortType)){
                    			if(e1_date.getTime()>e2_date.getTime()){
                    				value=1;
                    			}else
                    				value=-1;
                    		}else{
                    			if(e1_date.getTime()>e2_date.getTime()){
                    				value=-1;
                    			}else
                    				value=1;
                    		}
	                        return value;
                    	}
                    	else if("A".equalsIgnoreCase(field_type)){
                    		String value_1 = e1.get(field_name)!=null?(String)e1.get(field_name):"";
                    		String value_2 = e2.get(field_name)!=null?(String)e2.get(field_name):"";
                    		
        		    		if("A".equalsIgnoreCase(field_type)&&!"0".equals(codesetid)&&StringUtils.isNotBlank(codesetid)&&StringUtils.isNotBlank(value_1)){
        		    			value_1 = this.getValue(value_1);
        		    		}
        		    		if("A".equalsIgnoreCase(field_type)&&!"0".equals(codesetid)&&StringUtils.isNotBlank(codesetid)&&StringUtils.isNotBlank(value_2)){
        		    			value_2 = this.getValue(value_2);
        		    		}
        		    		Collator collator = Collator.getInstance();
        		    		/**
        		    		 * 如果指定的数与参数相等返回0。
								如果指定的数小于参数返回 -1。
								如果指定的数大于参数返回 1。
        		    		 */
        		    		if("ASC".equalsIgnoreCase(sortType))
        		    			return collator.getCollationKey(value_1).compareTo(collator.getCollationKey(value_2));
        		    		else
        		    			return collator.getCollationKey(value_2).compareTo(collator.getCollationKey(value_1));
                    	}else if("N".equalsIgnoreCase(field_type)){
                    		int disformat_ = disformat==null||"".equals(disformat)?0:Integer.parseInt(disformat);
                    		double value_1 = 0.0;
                    		double value_2 = 0.0;
                    		if(disformat_==0){
                    				value_1 = e1.get(field_name)!=null?(Integer)e1.get(field_name):0;
                    				value_2 = e2.get(field_name)!=null?(Integer)e2.get(field_name):0;
                    		}else if(disformat_>0){
                    				value_1 = e1.get(field_name)!=null?(Double)e1.get(field_name):0;
                    				value_2 = e2.get(field_name)!=null?(Double)e2.get(field_name):0;
                    		}	
                    		int value=0;
                    		if("ASC".equalsIgnoreCase(sortType)){
                    			if(value_1>value_2){
                    				value=1;
                    			}else
                    				value=-1;
                    		}else{
                    			if(value_1>value_2){
                    				value=-1;
                    			}else
                    				value=1;
                    		}
	                        return value;
                    	}
					}
					throw new ClassCastException("排序出错");
				}
				private String getValue(String value) {
					String value_  = AdminCode.getCodeName(codesetid,value);
	    			if("UM".equalsIgnoreCase(codesetid)&&"".equals(value_)){//兼容关联UM 选择UN代码的情况
	    				value_ = AdminCode.getCodeName("UN",value);
	        		}
	    			if(StringUtils.isNotBlank(value_)){
	    				value = value_+"_"+value+"_merge";
	    			}else
	    				value = value_;
					return value;
				}  
	        }); 
			return dataList;
		}catch(Exception e){
			e.printStackTrace();
			return dataList;
		}
	}
	/**
	 * 查询业务用户关联的自助用户用户名，或者查询自助用户的业务用户的用户名
	 * @param userView
	 * @return
	 */
	public static ArrayList SearchOperUserOrSelfUserName(UserView userView)
	{
		ArrayList operUserArray=new ArrayList();
		RowSet frowset2=null;
		Connection conn = null;
		ContentDAO dao=null;
		try{
			conn = AdminDb.getConnection();
			dao=new ContentDAO(conn);
			if(userView.getStatus()==0&&userView.getA0100()!=null && userView.getA0100().trim().length()>0){
				String usernamefield = ConstantParamter.getLoginUserNameField().toLowerCase();//在人员库中得到用户名字段
				String operUserSql="select "+usernamefield+" as username from "+userView.getDbname()+"A01 where a0100=? ";
				operUserArray.add(userView.getA0100());
				frowset2=dao.search(operUserSql,operUserArray);
				operUserArray.clear();
				operUserArray.add(userView.getDbname().toLowerCase()+userView.getA0100());
				while(frowset2.next()){
					String operUserName=frowset2.getString("username");
					if(StringUtils.isNotBlank(operUserName))
						operUserArray.add(operUserName);
				}
			}
			else if(userView.getStatus()==4 ){
				String operUserSql="select username from OperUser where a0100=? and NBase=?";
				operUserArray.add(userView.getA0100());
				operUserArray.add(userView.getDbname());
				frowset2=dao.search(operUserSql,operUserArray);
				operUserArray.clear();
				while(frowset2.next()){
					String operUserName=frowset2.getString("username");
					if(StringUtils.isNotBlank(operUserName))
						operUserArray.add(operUserName);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			closeDbObj(frowset2);
			closeDbObj(conn);
		}
		return operUserArray;
	}

	/**
     * 判断字符串编码格式
     * @param str 需要判断的字符串
     * @return
     */
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode)))
                return encode;

        } catch (Exception exception) {
        }
        
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode)))
                return encode;

        } catch (Exception exception1) {
        }
        
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode)))
                return encode;

        } catch (Exception exception2) {
        }
        
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode)))
                return encode;

        } catch (Exception exception3) {
        }
        
        return "";
    } 
    
    /**
     * 过滤HTML中script Xss跨站攻击代码 
     * @param value
     * @return
     */
    public static String stripScriptXss(String value){
    	
    		if(value==null || value.trim().length()<1)
    			return value;
    	
    	
    		ArrayList patternList = new ArrayList();
    		Pattern XssPattern = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.CASE_INSENSITIVE);
    		patternList.add(XssPattern);
    		XssPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    		patternList.add(XssPattern);
    		XssPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    		patternList.add(XssPattern);
    		XssPattern = Pattern.compile("(javascript:|vbscript:|view-source:)*", Pattern.CASE_INSENSITIVE);
    		patternList.add(XssPattern);
    		XssPattern = Pattern.compile("(window\\.location|window\\.|\\.location|document\\.cookie|document\\.|alert\\(.*?\\)|window\\.open\\()*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    		patternList.add(XssPattern);
    		XssPattern = Pattern.compile("<([\\w\\W]*)\\s(oncontrolselect|oncopy|oncut|ondataavailable|ondatasetchanged|ondatasetcomplete|ondblclick|ondeactivate|ondrag|ondragend|ondragenter|ondragleave|ondragover|ondragstart|ondrop|onerror|onerroupdate|onfilterchange|onfinish|onfocus|onfocusin|onfocusout|onhelp|onkeydown|onkeypress|onkeyup|onlayoutcomplete|onload|onlosecapture|onmousedown|onmouseenter|onmouseleave|onmousemove|onmousout|onmouseover|onmouseup|onmousewheel|onmove|onmoveend|onmovestart|onabort|onactivate|onafterprint|onafterupdate|onbefore|onbeforeactivate|onbeforecopy|onbeforecut|onbeforedeactivate|onbeforeeditocus|onbeforepaste|onbeforeprint|onbeforeunload|onbeforeupdate|onblur|onbounce|oncellchange|onchange|onclick|oncontextmenu|onpaste|onpropertychange|onreadystatechange|onreset|onresize|onresizend|onresizestart|onrowenter|onrowexit|onrowsdelete|onrowsinserted|onscroll|onselect|onselectionchange|onselectstart|onstart|onstop|onsubmit|onunload)+\\s*=+"
    				, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    		patternList.add(XssPattern);
    		
    		Matcher matcher = null;
    		for(int i=0;i<patternList.size()&&value.trim().length()>0;i++){
    			Pattern pattern = (Pattern)patternList.get(i);
    			matcher = pattern.matcher(value);
            // 匹配
            if(matcher.find()) {
                // 删除相关字符串
                value = matcher.replaceAll("");
            }
    		}
        
    		return value;
    }

	/**
	 * 生成DOM元素
	 * @param xml
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
    public static Document generateDom(String xml) throws Exception{
    	xml = xml.trim();
		xml= xml.replace("encoding=\"GB2312\"", "encoding=\"UTF-8\"").replace("encoding=\"gb2312\"", "encoding=\"UTF-8\"");
		xml= xml.replace("encoding=\"GBK\"", "encoding=\"UTF-8\"").replace("encoding=\"gbk\"", "encoding=\"UTF-8\"");
		xml= xml.replace("encoding='GB2312'", "encoding='UTF-8'").replace("encoding='gb2312'", "encoding='UTF-8'");
		xml= xml.replace("encoding='GBK'", "encoding='UTF-8'").replace("encoding='gbk'", "encoding='UTF-8'");
		byte bt[] = xml.getBytes("utf-8");
		InputStream input = null;
		Document doc = null;
		try{
			input = new ByteArrayInputStream(bt);
			SAXBuilder saxbuilder = new SAXBuilder();
			doc = saxbuilder.build(input);
		}catch (Exception e){
			throw e;
		}finally{
			PubFunc.closeIoResource(input);
		}
		return doc;
	}

	/**
	 * 生成DOM元素
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static Document generateDom(InputStream inputStream) throws Exception{
		return generateDom(IOUtils.toString(inputStream, "utf-8"));
	}
    
    public static void main(String[] args)
    {
 	    
 	   System.out.println(PubFunc.validateNum("23.3",3));
 	   
 	   
    } 

	/**
	 * 判断zip文件流的编码格式（目前只判读了GBK和UTF-8）
	 * 
	 * @param path
	 *            zip文件路径
	 * @return encoding 编码格式
	 * @throws Exception
	 */
    public static String getZIPEncoding(String path) throws Exception {
        String encoding = "UTF-8";
        net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(path);
        zipFile.setFileNameCharset(encoding);
        List<FileHeader> list = zipFile.getFileHeaders();
        for (int i = 0; i < list.size(); i++) {
            FileHeader fileHeader = list.get(i);
            String fileName = fileHeader.getFileName();
            if (isMessyCode(fileName)) {
                encoding = "GBK";
                break;
            }
        }
        return encoding;
    }
 
    private static boolean isMessyCode(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((int) c == 0xfffd) {
                // 存在乱码
                return true;
            }
        }
        return false;
    }
    /**
     * 获取本周的第一天
     * @return yyyy-MM-dd
     */
    public static String getWeekStart(){
    	Calendar cal=Calendar.getInstance();
    	cal.add(Calendar.WEEK_OF_MONTH, 0);
    	cal.set(Calendar.DAY_OF_WEEK, 2);
    	return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }
    /**
     * 获取本年的第一天
     * @return yyyy-MM-dd
     */
    public static String getYearStart(){
    	return new SimpleDateFormat("yyyy").format(new Date())+"-01-01";
    }
    /**
     * 获取本季度开始日期
     * @return yyyy-MM-dd
     */
    public static String getCurrentQuarterStartTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
		if (currentMonth >= 1 && currentMonth <= 3)
			c.set(Calendar.MONTH, 0);
		else if (currentMonth >= 4 && currentMonth <= 6)
			c.set(Calendar.MONTH, 3);
		else if (currentMonth >= 7 && currentMonth <= 9)
			c.set(Calendar.MONTH, 4);
		else if (currentMonth >= 10 && currentMonth <= 12)
			c.set(Calendar.MONTH, 9);
		c.set(Calendar.DATE, 1);
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}
}