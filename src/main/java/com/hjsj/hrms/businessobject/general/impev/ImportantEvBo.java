package com.hjsj.hrms.businessobject.general.impev;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportantEvBo {
	private UserView userView=null;
	private Connection conn = null;
	public ImportantEvBo(UserView userView,Connection conn){
		this.userView=userView;
		this.conn = conn;
	}
	/**
	 * 根据机构代码生成sql语句
	 * @param a_code
	 * @return
	 */
	public String whereCodeStr(String a_code){
		StringBuffer wherestr=new StringBuffer();
		StringBuffer sexpr=new StringBuffer();
		StringBuffer sfactor=new StringBuffer();
		if(a_code!=null&&a_code.trim().length()>1){
			String codesetid=a_code.substring(0, 2);
			String value=a_code.substring(2);

			if(value!=null&&value.trim().length()>0){
				if("UN".equalsIgnoreCase(codesetid)){
					sexpr.append("B0110=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else if("UM".equalsIgnoreCase(codesetid)){
					sexpr.append("E0122=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else if("@K".equalsIgnoreCase(codesetid)){
					sexpr.append("E01A1=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}
			}else{
				sexpr.append("B0110=");
				sexpr.append(value);
				sexpr.append("*`B0110=`");
				sfactor.append("1+2");
			}
		}
		/**过滤条件*/
		try {
			String str1 = sfactor.toString()+"|"+sexpr.toString();
			String strwhere=this.userView.getPrivSQLExpression(sfactor.toString()+"|"+sexpr.toString(),
					"Usr",false,true,new ArrayList());
			strwhere = strwhere.replaceAll("UsrA01","P06").trim(); 
			strwhere = strwhere.substring(strwhere.indexOf("WHERE")+5);
			wherestr.append(strwhere);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return wherestr.toString();
	}
	
	
    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式    
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式    
    //private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式   
    private static final String regEx_html = "</?[^/?(br)][^><]*>";
	 /**
     * 清除样式，并截取截取30字符
     * @param str
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
    	if(htmlStr==null || "".equals(htmlStr)){
    		return "";
    	}
    	Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
    	Matcher m_script = p_script.matcher(htmlStr);
    	htmlStr = m_script.replaceAll(""); // 过滤script标签
    	Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
    	Matcher m_style = p_style.matcher(htmlStr);       
    	htmlStr = m_style.replaceAll(""); // 过滤style标签       
    	Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);        
    	Matcher m_html = p_html.matcher(htmlStr);        
    	htmlStr = m_html.replaceAll(""); // 过滤html标签
    	//htmlStr=htmlStr.replaceAll("&nbsp;", " ");
    	if(htmlStr.length()>30){
    		htmlStr=htmlStr.substring(0, 30)+"...";
    		htmlStr=htmlStr.replaceAll("<br\\...", "...");
    		htmlStr=htmlStr.replaceAll("<b\\...", "...");
    		htmlStr=htmlStr.replaceAll("<\\...", "...");
    		htmlStr=htmlStr.replaceAll("&nbsp\\...", "...");
    		htmlStr=htmlStr.replaceAll("&nbs\\...", "...");
    		htmlStr=htmlStr.replaceAll("&nb\\...", "...");
    		htmlStr=htmlStr.replaceAll("&n\\...", "...");
    		htmlStr=htmlStr.replaceAll("&\\...", "...");
    	}
    	return htmlStr; // 返回文本字符串    }
    }

}
