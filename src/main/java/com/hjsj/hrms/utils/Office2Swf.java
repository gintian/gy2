/**
 * 
 */
package com.hjsj.hrms.utils;

import com.hrms.struts.constant.SystemConfig;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <p>
 * Title:Office2Swf
 * </p>
 * <p>
 * Description:将*.doc;*.docx;*.xls;*.xlsx;*.pdf;*.ppt;*.pptx;转为swf文档
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-10-08
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class Office2Swf {
	
	/**
	 * 判断服务器平台是否是windows
	 * @return
	 */
	public static boolean isWindows() {
		String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().contains("windows")) {
			return false;
		}
		
		return false;
	}

	/**
	 * windows平台利用flashpaper将文档转为flash
	 * @param inputFilePath String office文档路径
	 * @param outputFilePath String flash文档的保存路径
	 * @return boolean 是否成功
	 */
	public static boolean document2SwfByFlashpaper(String inputFilePath, String outputFilePath) {
		
		Process pro = null;
		try {
			// 文件转换命令
			StringBuffer cmd = new StringBuffer();
			cmd.append("flashprinter.exe ");
			cmd.append(inputFilePath);
			cmd.append(" -o ");
			cmd.append(outputFilePath);
			
			// 创建线程
			pro = Runtime.getRuntime().exec(cmd.toString());
			// 等待线程完成
			pro.waitFor();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			// 结束线程
			pro.destroy();
		}
		
		return true;

	}
	
	/**
	 * 非windows平台利用swftools将文档转为flash
	 * @param inputFilePath String office文档路径
	 * @param outputFilePath String flash文档的保存路径
	 * @param host String openoffice的ip地址，默认为127.0.0.1
	 * @param port String openoffice监听的端口，默认为8100
	 * @return boolean 文档是否转换成功
	 * 
	 */
	public static boolean document2SwfBySwfTools(String inputFilePath, String outputFilePath) {
		
//		if (host == null || host.length() <= 0) {
//			host = "127.0.0.1";
//		}
//		
//		if (port == null || port.length() <= 0) {
//			port = "8100";
//		}
		//"soffice -headless -accept=\"socket,host="+host+",port="+port+";urp;\" -nofirststartwizard";
		String cmd = SystemConfig.getPropertyValue("openoffice");
		String swftools = SystemConfig.getPropertyValue("swftools");
		
	    // 如果不配置swftools参数，这不进行转化
	    if (swftools == null || swftools.length() <= 0) {
	    	return false;
	    }
	    
		// 如果不配置openoffice参数，则不进行转化
		if (cmd == null || cmd.length() <= 0) {
			return false;
		}
		
	    Process pro = null;
	    // 将非pdf文档转为pdf文档
	    if (!inputFilePath.toLowerCase().endsWith(".pdf")) {
		    try{
		    	String cmd1="";
				if (cmd.indexOf(".exe") != -1) {
					cmd1 = cmd.substring(0, cmd.indexOf(".exe") + 4);
					// 处理带空格的cmd命令
					if (cmd1.indexOf(" ") != -1)
						cmd = "\"" + cmd1 + "\"" + cmd.substring(cmd.indexOf(".exe") + 4, cmd.length());
				} else{
					cmd1 = cmd.substring(0, cmd.indexOf("soffice") + 7);
					// 处理带空格的cmd命令
					if (cmd1.indexOf(" ") != -1)
						cmd = "\"" + cmd1 + "\"" + cmd.substring(cmd.indexOf("soffice") + 7, cmd.length());
				}
		    	pro = Runtime.getRuntime().exec(cmd);
		    	// 读取缓冲区内容，否则卡死
				final InputStream in = pro.getInputStream();
				// 启动单独的线程来清空process.getInputStream()的缓冲区
				new Thread(new Runnable() {
				    public void run() {
				    	try {
				    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
				    		while(br.readLine() != null) ;
				    	} catch (Exception e) {
				    		e.printStackTrace();
				    	}
				    }
				}).start(); 
				
				
				InputStream is2 = pro.getErrorStream();
				BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
				StringBuilder buf = new StringBuilder(); // 保存输出结果流
				String line = null;
				while((line = br2.readLine()) != null) buf.append(line);
				// 等待线程完成
				pro.waitFor();
				OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();     
		    	officeManager.start();      
		    	OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);     
				File inputFile = new File(inputFilePath);
				File outputFile = new File(inputFilePath.substring(0, inputFilePath.lastIndexOf(".")) + ".pdf");
		    	converter.convert(inputFile,outputFile);              
		    	officeManager.stop();
		    }catch(Exception cex){
		    	cex.printStackTrace();
		    }finally{			   
		    	if (pro != null) {
		    		pro.destroy();
		    	}
		    }
	    }

	    
	    
	    // 将pdf文档转为flash
	    try {
			// 文件转换命令   
	    	//处理带空格的cmd命令
	    	String cmdpdf ="";
	    	if(swftools.indexOf(" ")!=-1)
	    		cmdpdf = "\""+swftools + "\"";
	    	else
	    		cmdpdf = swftools;
	    	if(inputFilePath.indexOf(" ")!=-1)
	    		cmdpdf+=" \""+inputFilePath.substring(0, inputFilePath.lastIndexOf(".")) + ".pdf\"";
	    	else
	    		cmdpdf+=" "+inputFilePath.substring(0, inputFilePath.lastIndexOf(".")) + ".pdf";
	    	if(outputFilePath.indexOf(" ")!=-1)
	    		cmdpdf+=" -o \"" + outputFilePath + "\" -T 9";
	    	else
	    		cmdpdf+=" -o " + outputFilePath + " -T 9";
			
			// 创建线程
			pro = Runtime.getRuntime().exec(cmdpdf);
			
			// 读取缓冲区内容，否则卡死
			final InputStream in = pro.getInputStream();
			// 启动单独的线程来清空process.getInputStream()的缓冲区
			new Thread(new Runnable() {
			    public void run() {
			    	try {
			    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
			    		while(br.readLine() != null) ;
			    	} catch (Exception e) {
			    		e.printStackTrace();
			    	}
			    }
			}).start(); 
			
			
			InputStream is2 = pro.getErrorStream();
			BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
			StringBuilder buf = new StringBuilder(); // 保存输出结果流
			String line = null;
			while((line = br2.readLine()) != null) buf.append(line); 
			
			// 等待线程完成
			pro.waitFor();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pro != null) {
				// 结束线程
				pro.destroy();
			}
		}
		
		// 删除临时pdf文件
		if (!inputFilePath.toLowerCase().endsWith(".pdf")) {
			try {
				File file = new File(inputFilePath.substring(0, inputFilePath.lastIndexOf(".")) + ".pdf");
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return true;

	}
	
	/**
	 * 将office文档转为swf文档
	 * @param inputFilePath String office文档路径
	 * @param outputFilePath String flash文档的保存路径
	 * @param host String openoffice的ip地址，默认为127.0.0.1
	 * @param port String openoffice监听的端口，默认为8100
	 * @return boolean 文档是否转换成功
	 */
	public static boolean office2Swf(String inputFilePath, String outputFilePath) {
		if (isWindows()) {
			return document2SwfByFlashpaper(inputFilePath, outputFilePath);
		} else {
			return document2SwfBySwfTools(inputFilePath, outputFilePath);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(office2Swf("c:/1.ppt", "c:/4.swf"));

	}

}
