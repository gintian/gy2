/**
 *  SQL语句除零解决
 */

package com.hjsj.hrms.businessobject.report;

import com.hrms.hjsj.utils.Sql_switcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title:SQL语句除零问题解决 业务逻辑：a/0=0</p>
 * <p>Description:解决思路：isnull(3/(nullif(0,0)),0)</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 11, 2006:1:15:33 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class SQL_Util {

	public SQL_Util(){

	}
	
	/**
	 * SQL语句转换
	 * @param sql  待处理的SQL语句
	 * @return     无除零错误的SQL语句
	 */
	public  String sqlSwitch(String sql){
		if(sql == null || "".equals(sql)){
			return "";
		}
		//System.out.println(sql);
		String nullifsql = this.getNullifSQL(sql);
		//System.out.println("nullifsql=" + nullifsql);
		return this.getIsNullSQL(nullifsql);
	}
	
	/**
	 * 获得转换为nullif格式的SQL语句
	 * @param sql 如：3/2
	 * @return       3/nullif(2,0)
	 */
	public  String getNullifSQL(String sql){
		StringBuffer result = new StringBuffer();
		
		for(int i =0 ; i< sql.length(); i++){
			char c = sql.charAt(i);
			if(c == '/'){
				result.append(c);			
				String temp = sql.substring(i+1,sql.length());
				String tt = this.lookupDenominator(temp);
				String te =this.getNullifSQL(tt);			
				String nn = this.nullif(te);				
				result.append(nn);				
				i+=tt.length();
			}else{
				result.append(c);
			}
		}
		
		return result.toString();
	}
	
	
	/**
	 * 查找除法操作的分母
	 * @param sqlTemp SQL片段
	 * @return  SQL表达式中除法操作中的分母部分
	 */
	public  String lookupDenominator(String sqlTemp){
		StringBuffer result = new StringBuffer();
		sqlTemp=sqlTemp.trim();
		for(int i = 0 ; i< sqlTemp.length();i++){
			char c = sqlTemp.charAt(i);
			if(c == '('){
				result.append(c);
				String temp =this.getDenominator(sqlTemp.substring(i+1, sqlTemp.length()));
				i+= temp.length();
				result.append(temp);
			}else if(i==0 && c=='-'){  //add by wangchaoqun on 2014-11-12  当除数为负数时也要支持
				result.append(c);   
			}else if("+-*/)".indexOf(c)!=-1){
				break;
			}
			else if(c==' '&&CheckNumber(result.toString()))
			{
				break;
			}
			else{
				result.append(c);
			}
		}
		
		return result.toString().trim();
	}
	
	
	
	 public boolean CheckNumber(String str){
		String reg ="^\\d+?[.]?\\d*?$";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		if (m.matches()) {
            return true;
        } else {
            return false;
        }
		}

	
	
	/**
	 * 获得除法操作中的分母
	 * 解决除法操作符后存在括号的问题 
	 * @param str  SQL片段
	 * @return     分母片段
	 */
	public String getDenominator(String str){
		StringBuffer result = new StringBuffer();
		int i =0;
		while(true){
			if(str.charAt(i)=='('){
				result.append(str.charAt(i));
				String temp = this.getDenominator(str.substring(i+1,str.length()));
				result.append(temp);
				i+=temp.length();
			}else if(str.charAt(i) == ')'){
				result.append(str.charAt(i));
				break;
			}else{
				result.append(str.charAt(i));
			}
			i++;
		}
		
		return result.toString();
	}
	
	
	
	/**
	 * 得到isnull格式的SQL语句
	 * @param str 如：3/nullif(2,0)
	 * @return       isnull(3/nullif(2,0),0)
	 */
	public String getIsNullSQL(String str){
		StringBuffer result = new StringBuffer();
		for(int i =0; i< str.length(); i++){
			char c = str.charAt(i);		
			if(c == '/'){
				String fm = this.lookupDenominator(str.substring(i+1,str.length()));			
				String temp = this.getIsNullSQL(fm);		
				String fz = this.lookupNumerator(result.toString());			
				String nn = this.isnull("("+fz+"/"+temp+")","0");
				int n =fz.length();		
				result.delete(result.length()-n,result.length());		
				result.append(nn);
				i += fm.length();
			}else{
				result.append(c);
			}
		}
		return result.toString();
	}
	
	/**
	 * 查找除法操作中的分子部分
	 * @param str  SQL语句片段
	 * @return     分子
	 * 
	 */
	public String lookupNumerator(String str){
		StringBuffer result = new StringBuffer();	
		for(int i = str.length()-1; i>=0 ; i--){
			char c = str.charAt(i);
			if(c == ')'){
				String temp = this.getNumerator(str);
				result.append(temp);
				i-= temp.length()-1;
			}else if("+-*/(".indexOf(c)!=-1){
				break;
			}else{
				result.append(c);
			}
		}
		StringBuffer s = new StringBuffer();
		String temp = result.toString();
		for(int i = temp.length()-1 ; i>=0; i-- ){
			s.append(temp.charAt(i));
		}
		return s.toString();
	}
	
	/**
	 * 获得SQL除法操作中的分子
	 * 解决分子中的括号问题
	 * @param str  SQL片段
	 * @return     分子片段
	 */
    public String getNumerator(String str){
    	 StringBuffer result = new StringBuffer();
  	   int n = 0;
  	   int nn = 0;
  	   for(int i = str.length()-1; i>=0 ; i--){
  		   if(str.charAt(i)==')'){
  			   n++;
  		   }else if(str.charAt(i)=='('){
  			   nn++;
  		   }
  		   if(n == nn){
  			   break;
  		   }
  	   }	   
  	   int k = 0;
  	   for(int j = str.length()-1; j>=0 ; j--){
  		  if(str.charAt(j)=='('){
  			  result.append(str.charAt(j));
  			  k++;
  			  if(k == n){ 
  				  break;
  			  }
  		   }else{
  			   result.append(str.charAt(j));
  		   }
  	   }   
  	   return result.toString();
   }

	/**
	 * 获取nullif(3,0)SQL字符串
	 * @param tempString 分母 如：3/2 中的 2
	 * @return           nullif(2,0)
	 */
	public String nullif(String tempString){
		return "NULLIF("+tempString+",0)";
	}

	
	/**
	 * 数据库的通用格式isnull()函数
	 * @param fieldname
	 * @param repvalue
	 * @return
	 */
    public String isnull(String fieldname, String repvalue){
    	return Sql_switcher.isnull(fieldname,repvalue);
    }
    
    
    public static void main(String[] args) {
    /*	SQL_Util su = new SQL_Util();
    	String temp = "e.bb/e.cc"; 	
    	String tt = su.sqlSwitch(temp);
    	System.out.println("tt=" + tt);*/
    	
    //	String reg ="^\\d+?[.]?\\d*?$";
	//	Pattern p = Pattern.compile(reg);
	//	Matcher m = p.matcher(" 5");
	//	System.out.println(m.matches())
    	SQL_Util su = new SQL_Util();
    	String sql=" select (a3.C1-a4.C1-a22.C1)/a9.C1/a10.C1*a5*a6 from a03";
    	System.out.println(su.sql_switch(sql));
    	
    }
    
    int current_index=0;
    String ori_sql="";
    String token="";
    StringBuffer sql=new StringBuffer("");
    public String sql_switch(String o_sql)
    {
    	//liuy 2015-1-21 6686：澳洋集团有限公司：自助用户点击主页上的常用报表点击重新取数，列合计有数，行合计没数 start
    	//o_sql=o_sql.toLowerCase();
    	int selectIndex = o_sql.toLowerCase().indexOf("select");
    	int fromIndex = o_sql.toLowerCase().indexOf("from ");
    	//liuy 2015-1-21 end
    	String f_sql="";
    	ori_sql=o_sql;
    	if(selectIndex!=-1)
    	{
    		f_sql=o_sql.substring(0,selectIndex+6)+" ";
    		ori_sql=o_sql.substring(selectIndex+6);
    	}
    	String l_sql="";
    	if(fromIndex!=-1)
    	{
    		l_sql=" "+o_sql.substring(fromIndex);
    		ori_sql=ori_sql.substring(0,ori_sql.toLowerCase().indexOf("from "));
    	}
    	current_index=0;
    //    ori_sql=o_sql.substring(o_sql.indexOf("select")+6,o_sql.indexOf("from "));
        token="";
        sql=new StringBuffer("");
        
    	while(Get_Token())
    	{
    		level0();
    	}
    	String _sql=getIsNullSQL(this.sql.toString());

    //	String str=Sql_switcher.isnull("("+f_sql+this.sql.toString()+l_sql+")","0");
    	String str=Sql_switcher.isnull("("+f_sql+_sql+l_sql+")","0");
    //	String str=f_sql+this.sql.toString()+l_sql;
    	return str;
    }
    
    
    public boolean level0()
    {
    	
    	if(!level1()) {
            return false;
        }
    	if("+".equals(token)|| "-".equals(token))
    	{
    		
    		sql.append(token);
    		
			if(!Get_Token()) {
                return false;
            }
    		if(!level1()) {
                return false;
            }
    		
    	}
    	return true;
    }
    
    public  boolean level1()
    {
    	if(!level2()) {
            return false;
        }
    	if("*".equals(token))
    	{
    		sql.append(token);
    	
    		if(!Get_Token()) {
                return false;
            }
    		if(!level2()) {
                return false;
            }
    	}
    	
    	if("/".equals(token))
    	{
    		sql.append(token);
    		if(!Get_Token()) {
                return false;
            }
    		if(!"(".equals(token))
    		{
    			
    			sql.append(nullif(token));
    			if(!Get_Token()) {
                    return false;
                }
    			if(!level0()) {
                    return false;
                }
    			
    		}
    		else
    		{
    			
    			
    			String tempsql=this.sql.toString();
    			this.sql.setLength(0);
    			if(!level2() && !")".equals(token)) {
                    return false;
                }
    			tempsql+=nullif(this.sql.toString());
    			this.sql.setLength(0);
    			this.sql.append(tempsql);
    			//this.current_index++;//xgq @3/(@1+@3)*100 找不到*
    			/*if(!Get_Token())
        			return false;*/
    			if(!level0()) {
                    return false;
                }
    			
    		}
    	}
    	return true;
    }
    
    
    public  boolean level2()
    {
    	if(!"+".equals(token)&&!"-".equals(token)&&!"*".equals(token)&&!"/".equals(token)&&!"(".equals(token)&&!")".equals(token))
    	{
    		
    		sql.append(token);
    		if(!Get_Token()) {
                return false;
            }
    		level0();
    	}
    	if("(".equals(token))
    	{
    		sql.append(token);
    		if(!Get_Token()) {
                return false;
            }
    		level0();
    		sql.append(")");//如果不在这里加，会导致）为最后一个字符，添加不了，get_token后直接return false;
    		
    		if(!Get_Token()) {
                return false;
            }
    		
    		
    	}
    	if(")".equals(token))
    	{
    		
    		return true;
    	}
    	
    	return true;
    }
    
    
    
    
    private boolean Get_Token() {
	    if (current_index >= ori_sql.length()) {
            return false;
        }
       // 处理空格
		while (current_index < ori_sql.length()
				&& ori_sql.charAt(current_index) == ' ') {
			current_index++;
		}
		if (current_index >= ori_sql.length()) {
            return false;
        }
		
		int nPos = "+-*/() ".indexOf(ori_sql.charAt(current_index));
	    if(nPos!=-1)
	    {
	    	token="";
	    	token+=ori_sql.charAt(current_index++);
	    }
	    else
	    {
	    	token="";
	    	while (current_index < ori_sql.length())
	    	{
	    		if("+-*/() ".indexOf(ori_sql.charAt(current_index))==-1) {
                    token+=ori_sql.charAt(current_index++);
                } else if("+-*/() ".indexOf(ori_sql.charAt(current_index))!=-1) {
                    break;
                }
	    	}
	    	
	    }
    	return true;
    }
    
}
