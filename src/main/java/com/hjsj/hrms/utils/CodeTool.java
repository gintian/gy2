package com.hjsj.hrms.utils;
/**
 * <p>Title:FormatValue</p>
 * <p>Description:字符编码转换</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 14, 2008:11:3:52 AM</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class CodeTool {

  /**把ISO8859_1编码格式的串转换为GBK格式的串
   * @param str ISO8859_1编码格式的原串
   * @return 返回GBK格式的串。
   */
  public static String ISOtoGBK(String str){
       if(str == null) return null;
       if("".equals(str)) return "";
       try{
           byte strb [] =str.getBytes("ISO8859_1");
           return new String(strb,"GBK");
       }catch(Exception err){
           return str;
       }
  }

  /**把GBK编码格式的串转换为ISO8859_1格式的串
   * @param str GBK编码格式的原串
   * @return 返回ISO8859_1格式的串。
   */
  public static String GBK2ISO(String str){
       if(str == null)  return null;
       if("".equals(str))  return "";
       try{
           byte strb[] = str.getBytes("GBK");
           return new String(strb,"ISO8859_1");
       }catch(Exception err){
           return str;
       }
  }

  /**把GBK编码格式的串转换为GB2312格式的串
   * @param str GBK编码格式的原串
   * @return 返回GB2312格式的串。
   */
  public static String ISOtoGB2312(String str){
       if(str == null) return null;
       if("".equals(str)) return str;
       try{
           byte strb[] = str.getBytes("ISO8859_1");
           return new String(strb,"gb2312");
       }catch(Exception err){
           return str;
       }
  }

  /**把GB2312编码格式的串转换为ISO8859_1格式的串
   * @param str GB2312编码格式的原串
   * @return 返回ISO8859_1格式的串。
   */
  public static String GB2312toGBK(String str){
      if(str == null) return null;
      if("".equals(str)) return str;
      try{
          byte strb[] = str.getBytes("gb2312");
          return new String(strb, "ISO8859_1");
      }catch(Exception err){
          return str;
      }
  }

  /**转换原串为GB2312格式的串
   * @param str 任何编码格式的原串
   * @return 返回GB2312格式的串。
   */
  public static String toGB2312(String str){
       if(str == null)  return null;
       if("".equals(str))  return str;
       try{
           byte strb[] = str.getBytes();
           return new String(strb, "gb2312");
       }catch(Exception err){
           return str;
       }
  }
  
  /**转换原串为GBK格式的串
   * @param str 任何编码格式的原串
   * @return 返回GB2312格式的串。
   */
  public static String toGBk(String str){
       if(str == null)  return null;
       if("".equals(str))  return str;
       try{
           byte strb[] = str.getBytes();
           return new String(strb, "gbk");
       }catch(Exception err){
           return str;
       }
  }

  /**转换原串为ISO8859_1格式的串
   * @param str 任何编码格式的原串
   * @return 返回ISO8859_1格式的串。
   */
  public static String toISO(String str){
       if(str == null) return null;
       if("".equals(str)) return str;
       try{
           byte strb[] = str.getBytes();
           return new String(strb,"ISO-8859-1");
       }catch(Exception err){
          return str;
       }
  }
}
