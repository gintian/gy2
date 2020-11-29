package com.hjsj.hrms.businessobject.general.ftp;

import org.apache.commons.net.ftp.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;


public class FtpMediaBo {
	// ftp 连接url
	private String url;
	// ftp端口
	private int port;
	// ftp登录用户名
	private String userName;
	// ft登录密码
	private String passWord;
	// 文件目录
	private String path;
	// 文件名
	private String fileName;
	// 输入流
	private InputStream input;
	// ftp
	private FTPClient ftp;

	public FtpMediaBo(String url, int port, String userName, String passWord) {
		this.url = url;
		this.port = port;
		this.userName = userName;
		this.passWord = passWord;

		ftp = new FTPClient();
		try {
			// 下面三行代码必须要，而且不能改变编码格式，否则不能正确下载中文文件
			ftp.setControlEncoding("utf-8");
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
			conf.setServerLanguageCode("zh");

			ftp.setDefaultPort(this.port);
			ftp.connect(url);
			ftp.login(this.userName, this.passWord);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 上传
	 * 
	 * @param url
	 *            ftp 连接url
	 * @param port
	 *            ftp 端口
	 * @param username
	 *            ftp登录用户名
	 * @param password
	 *            ft登录密码
	 * @param path
	 *            文件目录
	 * @param filename
	 *            文件名
	 * @param input
	 *            输入流
	 * @return bolean 上传是否成功
	 * @throws Exception
	 */
	public boolean uploadFile(String path, String filename, InputStream input)
			throws Exception {

		// 初始表示上传失败
		boolean success = false;

		try {
			int reply;
			// 看返回的值是不是230，如果是，表示登陆成功
			reply = ftp.getReplyCode();
			// 以2开头的返回值就会为真
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new Exception("登录失败！");
			}

			// 得到目录的相应文件列表
			FTPFile[] fs = ftp.listFiles();
			// 检查重名
			//String filename1 = this.changeName(filename, fs);
			//String path1 = new String(path.getBytes("GBK"), "ISO-8859-1");
			//ftp.cwd(path);
			// 转到指定上传目录
			String []paths = path.split(Matcher.quoteReplacement(System.getProperty("file.separator")));
			for (int i = 0; i < paths.length; i++) {
				if (paths[i] != null && paths[i].length() > 0 && !ftp.changeWorkingDirectory(paths[i])) {
					ftp.makeDirectory(paths[i]);
					ftp.changeWorkingDirectory(paths[i]);
				}
			}
			
			ftp.storeFile(filename, input);

			// 表示上传成功
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp != null) {
				ftp.logout();
				ftp.disconnect();
			}
		}
		return success;
	}

	/**
	 * 删除ftp上得文件
	 * 
	 * @param path
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public boolean deleteFile(String path, String filename) throws Exception {
		// 初始表示上传失败
		boolean success = false;
		try {
			int reply;
			// 看返回的值是不是230，如果是，表示登陆成功
			reply = ftp.getReplyCode();
			// 以2开头的返回值就会为真
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new Exception("登录失败！");
			}

			// 转到指定上传目录
//			ftp.changeWorkingDirectory(path);
			
			String []paths = path.split(Matcher.quoteReplacement(System.getProperty("file.separator")));
			for (int i = 0; i < paths.length; i++) {
				if (paths[i] != null && paths[i].length() > 0 && !ftp.changeWorkingDirectory(paths[i])) {
					ftp.makeDirectory(paths[i]);
					ftp.changeWorkingDirectory(paths[i]);
				}
			}
			
			// 删除文件
			ftp.deleteFile(filename);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp != null) {
				ftp.logout();
				ftp.disconnect();
			}
		}
		return success;
	}

	/**
	 * 取得ftp服务器上的文件
	 * 
	 * @param filename
	 */
	public String downloadFile(String filename, String ext) {
		String outfilename = null;
		File tempFile = null;
		FileOutputStream fout = null;

		try {
			int reply;
			// 看返回的值是不是230，如果是，表示登陆成功
			reply = ftp.getReplyCode();
			// 以2开头的返回值就会为真
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new Exception("登录失败！");
			}

			tempFile = File.createTempFile("e_archive", ext, new File(System
					.getProperty("java.io.tmpdir")));
			fout = new java.io.FileOutputStream(tempFile);
			// buffOut=new BufferedOutputStream(fout);
			ftp.retrieveFile(filename, fout);
			outfilename = System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + tempFile.getName();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fout != null) {
                    fout.close();
                }
				if (ftp != null) {
					ftp.logout();
					ftp.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return outfilename;
	}


	// 判断是否有重名方法
	public boolean isDirExist(String fileName, FTPFile[] fs) {
		for (int i = 0; i < fs.length; i++) {
			FTPFile ff = fs[i];
			if (ff.getName().equals(fileName)) {
				return true; // 如果存在返回 正确信号
			}
		}
		return false; // 如果不存在返回错误信号
	}

	// 根据重名判断的结果 生成新的文件的名称
	public String changeName(String filename, FTPFile[] fs) {
		int n = 0;
		// 创建一个可变的字符串对象 即StringBuffer对象，把filename值付给该对象
		StringBuffer filename1 = new StringBuffer("");
		filename1 = filename1.append(filename);
		//System.out.println(filename1);
		while (isDirExist(filename1.toString(), fs)) {
			n++;
			String a = "[" + n + "]";
			//System.out.println("字符串a的值是：" + a);
			int b = filename1.lastIndexOf(".");// 最后一出现小数点的位置
			int c = filename1.lastIndexOf("[");// 最后一次"["出现的位置
			if (c < 0) {
				c = b;
			}
			StringBuffer name = new StringBuffer(filename1.substring(0, c));// 文件的名字
			StringBuffer suffix = new StringBuffer(filename1.substring(b + 1));// 后缀的名称

			filename1 = name.append(a).append(".").append(suffix);

		}
		return filename1.toString();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassword(String passWord) {
		this.passWord = passWord;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFilename(String fileName) {
		this.fileName = fileName;
	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

}
