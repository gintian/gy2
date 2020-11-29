package com.hjsj.hrms.businessobject.attestation.unicom;

import com.hjsj.hrms.utils.PubFunc;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * 中国联通手机短信接口
 * 仅适用于联通号码
 * @author Owner
 *
 */
public class UnicomSms {
  private String url="http://10.192.1.133/SMSPrc/SMSPrc.aspx";
  public boolean sendSms(String phone,String content)
  {
	  if(phone==null||phone.length()<=0) {
          return false;
      }
	  if(content==null||content.length()<=0) {
          return false;
      }
	  content=encodeContent(content);
	  StringBuffer buf=new StringBuffer();
	  buf.append("<request>");
	  buf.append("<senderNum></senderNum>");////发信人的手机号码，可为空，如需直接回复则不能为空
	  buf.append("<strContent>"+content+"</strContent>");//短信内容,短信内容已经编译
	  buf.append("<strmobileNumber>"+phone+"</strmobileNumber>"); //收信人的手机号码，必须
	  buf.append("<serverNum></serverNum>");////SP号，可为空
	  buf.append("<senderAccount></senderAccount>");//发信人名称，可为空，如希望回复时有姓名则不能为空 	
	  buf.append("</request>");
	  //System.out.println(buf.toString());
	  String reStr=post(this.url,buf.toString());
	  /*返回值
	   * <?xml version="1.0" encoding="GB2312"?>
	   * <SENDSMS>
	   *  <RECORDS> 
	   *    <RETURNVALUE>
	   *     1（成功）或0（失败）
	   *    </RETURNVALUE>
	   *  </RECORDS>
	   * </SENDSMS>
	   * */

	  if(reStr!=null&&reStr.length()>0)
	  {
		  if(reStr.toUpperCase().indexOf("<RETURNVALUE>")!=-1)
		  {
			  String value=reStr.substring(reStr.toUpperCase().indexOf("<RETURNVALUE>")+13,reStr.toUpperCase().indexOf("</RETURNVALUE>"));
  			  value=value.trim();
			  if(value!=null&& "1".equals(value)) {
                  return true;
              } else {
                  return false;
              }
		  }else {
              return false;
          }
	  }else {
          return false;
      }
  }
  public String get(String urlString) {
     
      StringBuffer stringBuffer = new StringBuffer(); 
      if ("".equalsIgnoreCase(urlString)) {
     //     log.warn("httpGet:Url is \"\"");
          return null;
      } else if (urlString.toLowerCase().startsWith("http://")) {

      } else {
      //    log.warn("Invalid Url:" + urlString);
          return null;
      }
      HttpURLConnection httpConnection;
      URL url;
      int code;
      try {
          url = new URL(urlString);
          httpConnection = (HttpURLConnection) url.openConnection();
          httpConnection.setRequestMethod("GET");
          httpConnection.setDoOutput(true);
          httpConnection.setDoInput(true);         
          code = httpConnection.getResponseCode();
      } catch (Exception e) {
        //  log.warn(e.fillInStackTrace());
          return null;
      }

      if (code == HttpURLConnection.HTTP_OK) {
    	  BufferedReader reader = null;
    	  InputStream stream =null;
          try {
        	  stream = httpConnection.getInputStream();
              String strCurrentLine;
              reader = new BufferedReader(new InputStreamReader(stream));
              while ((strCurrentLine = reader.readLine()) != null) {
                  stringBuffer.append(strCurrentLine).append("\n");
              }
          } catch (IOException e) {
            //  log.warn(e.fillInStackTrace());
          }finally{
        	  PubFunc.closeIoResource(stream);
        	  PubFunc.closeIoResource(reader);
          }
      }

      return stringBuffer.toString();
  }

  public String post(String urlString, String parameters) {
     
      StringBuffer stringBuffer = new StringBuffer();
     

      if ("".equalsIgnoreCase(urlString)) {
          return null;
      } else if (urlString.toLowerCase().startsWith("http://")) {

      } else {
          return null;
      }


      HttpURLConnection httpConnection;
      URL url;
      int code;
      OutputStreamWriter outputStreamWriter = null;
      OutputStream outputStream = null;
      try {
          url = new URL(urlString);

          httpConnection = (HttpURLConnection) url.openConnection();

          httpConnection.setRequestMethod("POST");
          httpConnection.setRequestProperty("Content-Length", String.valueOf(parameters.length()));
          httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

          httpConnection.setDoOutput(true);
          httpConnection.setDoInput(true);
          /*PrintWriter printWriter = new PrintWriter(httpConnection.getOutputStream());
          printWriter.print(parameters);
          printWriter.close();*/

          outputStream = httpConnection.getOutputStream();
          outputStreamWriter= new OutputStreamWriter(outputStream, "8859_1");
          outputStreamWriter.write(parameters);
          outputStreamWriter.flush();

          code = httpConnection.getResponseCode();
      } catch (Exception e) {
        //  log.warn(e.fillInStackTrace());
          return null;
      } finally {
          PubFunc.closeIoResource(outputStreamWriter);
          PubFunc.closeIoResource(outputStream);
      }

      if (code == HttpURLConnection.HTTP_OK) {
    	  BufferedReader reader=null;
          try {
              String strCurrentLine;
              reader= new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
              while ((strCurrentLine = reader.readLine()) != null) {
                  stringBuffer.append(strCurrentLine).append("\n");
              }
              reader.close();
          } catch (IOException e) {
            //log.warn(e.fillInStackTrace());
          }finally
          {
              PubFunc.closeIoResource(reader);
          }
      }
      return stringBuffer.toString();
  }
  
  /**
	 * 对短信内容进行编码
	 * 
	 * @param cont
	 *            内容
	 * @return 编码后的内容。编码失败则返回null
	 */
	public String encodeContent(String cont) {
		try {
			// 使用GBK编码取bytes
			byte[] b = cont.getBytes("GBK");
			// 新建一个StringBuffer存储结果
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < b.length; i++) {
				// 添加分割符“,”
				if (i > 0) {
                    sb.append(",");
                }
				if (b[i] > 0) {
					// 若byte大于0，为一般ASCII字符，直接添加
					sb.append(b[i]);
				} else {
					// 否则，取连续的两个byte，第一个byte加一或128然后左移8位，加上第二个byte
					sb.append((((b[i] + 1) | 128) << 8) + b[++i]);
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}     

}
