package com.hjsj.hrms.businessobject.general.template.templateanalyse;

import com.hjsj.hrms.utils.PubFunc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * word格式另存为htm格式文件
 * 格式化HTML 生成规范的XML格式
 * @author Ow
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
	 * 获得HTML的head部分
	 * head部分只对文件输出格式做修改(word格式打开),其余保持不变
	 * @return
	 */
	public  String htmlHeadData(){
		String head = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(this.htmlPath));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				head +=temp;
				head+="\n";				
				if("</head>".equalsIgnoreCase(temp))
				{
					head +="</head>";
					head+="\n";
					break;	
				}
			}
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
	public  String formatHtmlDocument(){
		StringBuffer streamstr=new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(this.htmlPath));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				 temp=temp.replaceAll("&nbsp;","wlhxryhrp");
			     if(temp.indexOf("<![if !supportMisalignedColumns]>")!=-1){
			    	streamstr.append(temp.substring(0,temp.indexOf("<![if !supportMisalignedColumns]>")));
			    	if(temp.indexOf("<![endif]>")!=-1){
			    		 streamstr.append(temp.substring(temp.indexOf("<![endif]>")+ 10));
			    	}else
			    	{	 
					    while((temp = br.readLine()) != null){	
						    if(temp.indexOf("<![endif]>")!=-1){
						        streamstr.append(temp.substring(temp.indexOf("<![endif]>")+ 10));
			    		     	break;
						    }
					     }
					}
			    	streamstr.append("\n");
				}
                else
                {
                	streamstr.append(temp);
                	streamstr.append("\n");
                }
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(br);
		}
		return streamstr.toString();
	}
}

