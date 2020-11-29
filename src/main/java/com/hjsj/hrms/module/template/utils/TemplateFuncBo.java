package com.hjsj.hrms.module.template.utils;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.util.HashMap;
import java.util.regex.Pattern;
/**
 * <p>Title:TemplateFuncBo.java</p>
 * <p>Description>:模板公用函数 ，一般与模板业务无关的公用函数，与数据库无关，一般方法可定义为static</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-08-23 上午10:36:32</p>
 * <p>@version: 7.0</p>
 */
public class TemplateFuncBo {

	public TemplateFuncBo(){

	}

		/** 
	 * @Title: getAllParamFromUrl 
	 * @Description: 从url中获取所有参数
	 * @param @param url
	 * @param @return
	 * @return HashMap
	 */ 
	public static HashMap getAllParamFromUrl(String url){		
		HashMap<String,String> map =  new HashMap();
		try {
			
			String [] arrStrs= url.split("&");
			for (int i=0;i<arrStrs.length;i++){
				String val=arrStrs[i];
				int index= val.indexOf("=");
				if (index>0){
					String key = val.substring(0,index);
					String value = val.substring(index+1);
					map.put(key, value);
				}
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return map;
	}	

	/** 
	* @Title: getAllParamFromEncryUrl 
	* @Description: 从加密的url获取所有参数
	* @param @param url
	* @param @return
	* @return HashMap
	*/ 
	public static HashMap getAllParamFromEncrytUrl(String url){		
		HashMap<String,String> map =  new HashMap();
		try {
			int encryStart=url.indexOf("encryptParam");
			if (encryStart>-1){
				String encrypt=url.substring(encryStart+13);
				int index=encrypt.indexOf("&");
				if (index>0){
					encrypt=encrypt.substring(0,index);
				}
				String valStrs= PubFunc.decrypt(encrypt);
				String [] arrStrs= valStrs.split("&");
				for (int i=0;i<arrStrs.length;i++){
					String val=arrStrs[i];
					index= val.indexOf("=");
					if (index>0){
						String key = val.substring(0,index);
						String value = val.substring(index+1);
						map.put(key, value);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return map;
	}	
	
	/** 
	* @Title: getValueFromMap 
	* @Description: 从map中获取指定参数的值 如果不存在返回""
	* @param @param HashMap
	* @param @param paramName
	* @param @return
	* @return HashMap
	*/ 
	public static String getValueFromMap(HashMap map,String paramName) {		
		String value="";
		try {
			value= (String)map.get(paramName);
			if (value==null)
				value="";
		}catch (Exception e){
			e.printStackTrace();
		}
		return value;
	}	
	
	/** 
	* @Title: getDecValueFromMap 
	* @Description: 从map中获取指定参数的值 此参数值必须是加密的，如果不存在返回"" 界面
	* @param @param HashMap
	* @param @param paramName
	* @param @return
	* @return HashMap
	*/ 
	public static String getDecValueFromMap(HashMap map,String paramName) {		
		String value="";
		try {
			value= getValueFromMap(map,paramName);
			if (value.length()>0 && !"0".equals(value)){
				if(",ins_id,task_id,".indexOf(paramName.toLowerCase())>-1)
				{
					//临时检测以上数值型参数是否加密了，
					Pattern pattern = Pattern.compile("[0-9]+");   
					if(pattern.matcher(value).matches()){
						throw new GeneralException(paramName+"存在没加密的情况，开发人员请检查");	
					}
				}
				if (value.contains(",")){
				    String[] strArr= value.split(",");
				    value="";
				    for (int i=0;i<strArr.length;i++){
				        String tmp = strArr[i];
				        if ("".equals(tmp)){
				            continue;
				        }
				        String _value= PubFunc.decryption(tmp);
				        if ("".equals(value)){
				            value=_value;
				        }
				        else {
				            value=value+","+_value;
				        }
				    }
				}
				else {
				    value= PubFunc.decryption(value);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			System.out.print(paramName+"存在没加密的情况，开发人员请检查");
		}
		
		return value;
	}	
	
	
	  /**
     * @Title: getFormatByDis
     * @Description: 对日起类型的数据 进行格式化
     * @param disFormat
     * @return
     * @throws String
     */
	public static String getDataFormatByDis(int disFormat) {
        String format = "yyyy.MM.dd";
        if (disFormat == 6)
            format = "yyyy.MM.dd";
        else if (disFormat == 7)
            format = "yy.MM.dd";
        else if (disFormat == 8 || disFormat == 9)
            format = "yyyy.MM";
        else if (disFormat == 10 || disFormat == 11)
            format = "yy.MM";
        else if (disFormat == 14 || disFormat == 23 || disFormat == 12)
            format = "yyyy年MM月dd日";
        else if (disFormat == 15 || disFormat == 22 || disFormat == 13)
            format = "yyyy年MM月";
        else if (disFormat == 16)
            format = "yy年MM月dd日";
        else if (disFormat == 17)
            format = "yy年MM月";
        else if (disFormat == 18)
            format = "年限";
        else if (disFormat == 19)
            format = "yyyy";
        else if (disFormat == 20)
            format = "MM";
        else if (disFormat == 21)
            format = "dd";
        return format;
    }
    
	/** 
	* @Title: haveFunctionIds 
	* @Description:是否有权限 
	* @param @param fuctionIds 权限号 以逗号分隔
	* @param @return
	* @return boolean
	*/ 
	public static boolean haveFunctionIds(String fuctionIds,UserView userview)
	{
		boolean b=false;
		String []ids= fuctionIds.split(",");
		try
		{
			for (int i=0;i<ids.length;i++){
				String fucId=ids[i];
				if (userview.hasTheFunction(fucId)){
					b=true;
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return b;
	}
	
	/**   
	 * @Title: isHZChar   
	 * @Description: 是否是汉字   
	 * @param @param c
	 * @param @return 
	 * @return boolean 
	 * @throws   
	*/
	public static  boolean isHZChar(char c)
    {
        boolean isCorrect =false;
        if((c>='0'&&c<='9')||(c>='a'&&c<='z')||(c>='A'&&c<='Z'))
        {   
          //字母,   数字   
            isCorrect =false;  
        }else if(c=='-'||c=='/'){
            isCorrect =true; 
        }else{   
          if(Character.isLetter(c))
          {   //中文   
              isCorrect =true; 
             //System.out.println(Character.isLetter(c));
          }else{   //符号或控制字符   
              isCorrect =false; 
          }   
        } 
        return isCorrect;
    } 
	
	   /**   
	 * @Title: getStrLength   
	 * @Description: 获取字符串长度，汉字按两个字符算   
	 * @param @param str
	 * @param @return 
	 * @return int 
	 * @throws   
	*/
	public static int getStrLength(String str) 
	    {
           int len=0; 
	       if (str!=null && str.length()>0){ 
                for(int i=0;i<str.length();i++) {
                    char c =str.charAt(i); 
                    if (String.valueOf(c).getBytes().length>1){
                        len=len+2;
                        continue;
                    }
                    if(isHZChar(c)){                            
                        len=len+2;
                        continue;
                    } 
                    len++;
                    
                } 
            }
	                
	       return  len;                 
	    }
	                    
}
