package com.hjsj.hrms.businessobject.general.cadrerm;

import com.hjsj.hrms.utils.PubFunc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * word格式另存为htm格式文件
 * 格式化HTML 生成规范的XML格式
 * @author Owner
 */
public class FormatHtml {
	
	private String htmlPath;
	
	/**
	 * 构造器
	 * @param htmlPath
	 */
	public FormatHtml(String htmlPath) {
		this.htmlPath = htmlPath;
	}

	/**
	 * 获取HTML中<html>内容
	 * @return
	 */
	public String htmlHtml(){
		String html ="";
		FileReader fr =null;
		BufferedReader br = null;
		try {
		    fr = new FileReader(this.htmlPath);
			br = new BufferedReader(fr);
			String temp = "";
			while ((temp = br.readLine()) != null) {
				if(temp.indexOf("<html")!=-1){
					html +=temp;
					break;
				}else {					
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
		    PubFunc.closeIoResource(fr);
		    PubFunc.closeIoResource(br);
		}
		return html;
	}
	
	
	/**
	 * 获得HTML的head部分
	 * head部分只对文件输出格式做修改(word格式打开),其余保持不变
	 * @param filePath 文件名
	 * @return
	 */
	public  String htmlHead(){
		String head = "";	
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(this.htmlPath));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				if("<head>".equalsIgnoreCase(temp)){
					head +="<head>";
					while((temp = br.readLine()) != null){
						temp = temp.replaceAll("text/html;","application/msword;");
						head +="\n";
						head +=temp;
						if("</head>".equalsIgnoreCase(temp)){
							break;
						}
					}
				}else if(head.endsWith("</head>")){
					break;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(br);
		}
		
		return head;
	}
	
	
	/**
	 * 格式化HTML的body部分获得XML的body部分
	 * @return
	 */
	private  String formatHtmlBody(){
		String body="";
		BufferedReader br=null;
		try {
		    br = new BufferedReader(new FileReader(this.htmlPath));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				if(temp.startsWith("<body")){
					body +=this.formatString(temp);
					while((temp = br.readLine()) != null){				
						if("</body>".equalsIgnoreCase(temp)){
							body +="\n</body>";
							break;
						}else if(temp.indexOf("<![if !supportMisalignedColumns]>")!=-1){
							while((temp = br.readLine()) != null){	
								if(temp.indexOf("<![endif]>")!=-1){
									break;
								}
							}
						}else{
							int n = temp.indexOf("<br");
							if(n == -1){
								body +="\n";
								body +=this.formatString(temp);
							}else{
								StringBuffer sb = new StringBuffer();
								int nn = temp.indexOf(">",n);
								if(nn!=-1){
									sb.append(temp.substring(0,nn+1));
									sb.append("</br>");
									sb.append(temp.substring(nn+1,temp.length()));
									temp = sb.toString();
									body +="\n";
									body +=this.formatString(temp);
								}else{
									body +="\n";
									body +=this.formatString(temp);
									while((temp = br.readLine()) != null){	
										int nnn = temp.indexOf(">",n);
										if(nnn!=-1){
											sb.append(temp.substring(0,nnn+1));
											sb.append("</br>");
											sb.append(temp.substring(nnn+1,temp.length()));
											temp = sb.toString();
											body +="\n";
											body +=this.formatString(temp);
											break;
										}else{
											body +="\n";
											body +=this.formatString(temp);
										}
									}
								}
							}
							
						}
					}
				}else if(body.endsWith("</body>")){
					break;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally
        {
            PubFunc.closeIoResource(br);
        }
		return body;
	}
	
	/**
	 * 格式化字符串
	 * @param str
	 * @return
	 */
	private  String formatString(String str){
		String temp=str;		
		temp = temp.replaceAll("<o:p></o:p>" , "hrp1");
		temp = temp.replaceAll("<!\\[if !supportEmptyParas\\]>&nbsp;<!\\[endif\\]>" ,"hrp2");
		temp = temp.replaceAll("&nbsp;","hrp3");
		temp = temp.replaceAll("nowrap" ,"hrp=\"4\"");
		for(int i=temp.lastIndexOf("="); i>=0; i=temp.lastIndexOf("=", i-1)){
			if(i!=-1){
				char c = temp.charAt(i+1);
				if(c == '\'' || c == '\"'){							
				}else{
					StringBuffer tt = new StringBuffer();
					tt.append(temp.substring(0,i+1));
					tt.append("\"");
					for(int j=i+1; j<temp.length();j++){
						char cc = temp.charAt(j);
						if(cc == ' '){
							tt.append("\" ");
							tt.append(temp.substring(j+1,temp.length()));
							break;
						}else if(cc == '>'){
							tt.append("\" ");
							tt.append(">");
							tt.append(temp.substring(j+1,temp.length()));
							break;
						}else if(j == temp.length()-1){
							tt.append("\"");
						}else{
							tt.append(cc);
						}
					}							
					temp = tt.toString();
				}
			}
	    }
		return temp;
	}
	
	
	/**
	 * 组装XML文件字符串
	 * @return
	 */
	public  String htmlToXML() {
		String xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>\n";
		String body = this.formatHtmlBody();
		xml+="\n";
		xml+=body;
		return xml;
	}
	

	/**
	 * 组合HTML的body部分
	 * @param xml
	 * @return
	 */
	public  String xmlToHtmlBody(String xml){
		String body="";
		xml = xml.replaceAll("<\\?xml version=\"1.0\" encoding=\"GB2312\"\\?>","");
		xml = xml.replaceAll("&quot;","\'");
		xml = xml.replaceAll("&lt;","<");
		xml = xml.replaceAll("&gt;",">");
		xml = xml.replaceAll("hrp1","<o:p></o:p>");
		xml = xml.replaceAll("hrp2","<!\\[if !supportEmptyParas\\]>&nbsp;<!\\[endif\\]>" );
		xml = xml.replaceAll("hrp3"," &nbsp; ");
		xml = xml.replaceAll("hrp=\"4\""," nowrap " );
		body+=xml;
		return body;
	}
	
}
