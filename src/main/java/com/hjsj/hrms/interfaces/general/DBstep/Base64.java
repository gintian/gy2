package com.hjsj.hrms.interfaces.general.DBstep;
/* @WCD
 * @对文件和字符串进行 BASE64编码
 */

import com.hjsj.hrms.utils.PubFunc;

import java.io.*;

class Base64Helper {
//从文本文件对象中读取内容并转换为字符数组
	public char[] readChars(File file) {
		CharArrayWriter caw = new CharArrayWriter();
		Reader fr = null;
		Reader in = null;
		try {
			fr = new FileReader(file);
			in = new BufferedReader(fr);
			int count = 0;
			char[] buf = new char[16384];
			while ( (count = in.read(buf)) != -1) {
				if (count > 0) {
					caw.write(buf, 0, count);
				}
			}
			in.close();
		}
		catch (Exception e) {e.printStackTrace();
		File f = new File("");
		} finally {
            PubFunc.closeIoResource(in);
            PubFunc.closeIoResource(fr);
        }
		return caw.toCharArray();
	}

//从字符串对象中读取内容并转换为字符数组
	public char[] readChars(String string) {
		CharArrayWriter caw = new CharArrayWriter();
		try {
			Reader sr = new StringReader(string.trim());
			Reader in = new BufferedReader(sr);
			int count = 0;
			char[] buf = new char[16384];
			while ( (count = in.read(buf)) != -1) {
				if (count > 0) {
					caw.write(buf, 0, count);
				}
			}
			in.close();
		}
		catch (Exception e) {e.printStackTrace();
		}
		return caw.toCharArray();
	}

//从二进制文件对象中读取内容并转换为字节数组
	public byte[] readBytes(File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream fis = null;
		InputStream is = null;
		try {
			fis = new FileInputStream(file);
			is = new BufferedInputStream(fis);
			int count = 0;
			byte[] buf = new byte[16384];
			while ( (count = is.read(buf)) != -1) {
				if (count > 0) {
					baos.write(buf, 0, count);
				}
			}
			is.close();
		} catch (Exception e) {
            e.printStackTrace();
        }finally {
        	PubFunc.closeIoResource(fis);
        	PubFunc.closeIoResource(is);
        }
		return baos.toByteArray();
	}

//写字节数组内容到二进制文件
	public void writeBytes(File file, byte[] data) {
	    OutputStream fos = null;
	    OutputStream os = null;
		try {
			fos = new FileOutputStream(file);
			os = new BufferedOutputStream(fos);
			os.write(data);
		}
		catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    if(fos != null)
		        PubFunc.closeIoResource(fos);
		    
		    if(os != null)
		        PubFunc.closeIoResource(os);
		}
	}

//写字符数组内容到文本文件
	public void writeChars(File file, char[] data) {
	    Writer fos = null;
	    Writer os = null;
		try {
			fos = new FileWriter(file);
			os = new BufferedWriter(fos);
			os.write(data);
		}
		catch (Exception e) {
		    e.printStackTrace();
		}finally{
		    PubFunc.closeResource(fos);
		    PubFunc.closeResource(os);
		}
	}
}

public class Base64 {
//编码文件对象所指的文件
	public char[] encode(File file) {
		if (!file.exists()) {
			System.err.println("错误:文件不存在！");
			return null;
		}
		Base64Helper base64Hel = new Base64Helper();
		return encode(base64Hel.readBytes(file));
	}

//编码文件名所指的文件
	public char[] encode(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			System.err.println("错误:文件“" + filename + "”不存在！");
			return null;
		}
		Base64Helper base64Hel = new Base64Helper();
		return encode(base64Hel.readBytes(file));
	}

//编码传入的字节数组，输出编码后的字符数组
	public char[] encode(byte[] data) {
		char[] out = new char[ ( (data.length + 2) / 3) * 4];
		// 对字节进行Base64编码,每三个字节转化为4个字符.
		// 输出总是能被4整除的偶数个字符
		//
		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			boolean quad = false;
			boolean trip = false;
			int val = (0xFF & (int) data[i]);
			val <<= 8;
			if ( (i + 1) < data.length) {
				val |= (0xFF & (int) data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if ( (i + 2) < data.length) {
				val |= (0xFF & (int) data[i + 2]);
				quad = true;
			}
			out[index + 3] = TableBase60[ (quad ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 2] = TableBase60[ (trip ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 1] = TableBase60[val & 0x3F];
			val >>= 6;
			out[index + 0] = TableBase60[val & 0x3F];
		}
		return out;
	}

	public byte[] decode(char[] data) {
		// 程序中有判断如果有回车、空格等非法字符，则要去掉这些字符
		// 这样就不会计算错误输出的内容
		int tempLen = data.length;
		for (int ix = 0; ix < data.length; ix++) {
			if ( (data[ix] > 255) || codes[data[ix]] < 0) {
				--tempLen; // 去除无效的字符
			}
		}
		// 计算byte的长度
		// -- 每四个有效字符输出三个字节的内容
		// -- 如果有额外的3个字符，则还要加上2个字节,
		// 或者如果有额外的2个字符，则要加上1个字节
		int len = (tempLen / 4) * 3;
		if ( (tempLen % 4) == 3) {
			len += 2;
		}
		if ( (tempLen % 4) == 2) {
			len += 1;
		}
		byte[] out = new byte[len];
		int shift = 0;
		int accum = 0;
		int index = 0;
		// 一个一个字符地解码（注意用的不是tempLen的值进行循环）
		for (int ix = 0; ix < data.length; ix++) {
			int value = (data[ix] > 255) ? -1 : codes[data[ix]];
			if (value >= 0) { // 忽略无效字符
				accum <<= 6;
				shift += 6;
				accum |= value;
				if (shift >= 8) {
					shift -= 8;
					out[index++] =
						(byte) ( (accum >> shift) & 0xff);
				}
			}
		}
		//如果数组长度和实际长度不符合，那么抛出错误
		if (index != out.length) {
			throw new Error("数据长度不一致(实际写入了 " + index + "字节，但是系统指示有" +
							out.length + "字节)");
		}
		return out;
	}

//
// 用于编码的字符
//
	public char[] TableBase60 =
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".
		toCharArray();

//
// 用于解码的字节（0-255）
//
	static private byte[] codes = new byte[256];
	static {
		for (int i = 0; i < 256; i++) {
			codes[i] = -1;
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			codes[i] = (byte) (i - 'A');
		}
		for (int i = 'a'; i <= 'z'; i++) {
			codes[i] = (byte) (26 + i - 'a');
		}
		for (int i = '0'; i <= '9'; i++) {
			codes[i] = (byte) (52 + i - '0');
		}
		codes['+'] = 62;
		codes['/'] = 63;
	}

	public static void main(String[] args) {
		Base64 base64 = new Base64();
		String key = new String("王传达");
		byte[] a = key.getBytes();
		char[] b = base64.encode(a);
		System.out.println(new String(b));
		//for(int i=0;i< b.length;i++){
		//  System.out.println(b[i]);
		//}
		byte[] c = base64.decode(b);
		System.out.println(new String(c));
		System.out.println("asf"+"\r\n"+"asdfasf");
	}
}
