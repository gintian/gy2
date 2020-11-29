package com.hjsj.hrms.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 汉字转拼音简码
 * @author xuj
 *2014-12-27
 */
public class PinyinUtil {

	 /**
     * 取汉字的首字母
     * @param src
     * @param isCapital 是否是大写
     * @return
     */
	private static char[]  getHeadByChar(char src,boolean isCapital){
        //如果不是汉字直接返回
        if (!isChineseChar("" +  src)) {
            return new char[]{src};
        }
        //获取所有的拼音
        String []pinyingStr=PinyinHelper.toHanyuPinyinStringArray(src);
        if( pinyingStr == null) {
        	return new char[]{src};
        }
        
        //创建返回对象
        int polyphoneSize=pinyingStr.length;
        char [] headChars=new char[polyphoneSize];
        int i=0;
        //截取首字符
        for(int j=0;j<pinyingStr.length;j++){
        	String s = pinyingStr[j];
            char headChar=s.charAt(0);
            //首字母是否大写，默认是小写
            if(isCapital){
                headChars[i]=Character.toUpperCase(headChar);
             }else{
                headChars[i]=headChar;
             }
            i++;
        }
        
        return headChars;
    }
	
	public static boolean isChineseChar(String str){        
		boolean temp = false;        
		Pattern p=Pattern.compile("[\u4e00-\u9fa5]");         
		Matcher m=p.matcher(str);         
		if(m.find()){             
			temp =  true;        
		}        
		return temp;    
	}
	
    private  static String[] getHeadByString(String src,boolean isCapital,String separator){
        char[]chars=src.toCharArray();
        String[] headString=new String[chars.length];
        int i=0;
        for(int m=0;m<chars.length;m++){
        	char ch=chars[m];
            char[]chs=getHeadByChar(ch,isCapital);
            StringBuffer sb=new StringBuffer();
            if(null!=separator){
                int j=1;
                
                for(int n=0;n<chs.length;n++){
                	char ch1=chs[n];
                    sb.append(ch1);
                    if(j!=chs.length){
                        sb.append(separator);
                    }
                    j++;
                }
            }else{
                sb.append(chs[0]);
            }
            headString[i]=sb.toString();
            i++;
        }
        return headString;
    }

    /**
     * 查找字符串首字母
     * @param src 
     * @param isCapital 是否大写
     * @return
     */
    private  static String[] getHeadByString(String src,boolean isCapital){
        return getHeadByString( src, isCapital,null);
    }
    
    
    
    /**
     * 将字符串转换成拼音数组
     * 
     * @param src
     * @return
     */
    private static String[] stringToPinyin(String src) {
        return stringToPinyin(src, false, null);
    }

    /**
     * 将字符串转换成拼音数组
     * 
     * @param src
     * @param isPolyphone
     *            是否查出多音字的所有拼音
     * @param separator
     *            多音字拼音之间的分隔符
     * @return
     */
    private static String[] stringToPinyin(String src, boolean isPolyphone,
            String separator) {
        // 判断字符串是否为空
        if ("".equals(src) || null == src) {
            return null;
        }
        char[] srcChar = src.toCharArray();
        int srcCount = srcChar.length;
        String[] srcStr = new String[srcCount];

        for (int i = 0; i < srcCount; i++) {
            srcStr[i] = charToPinyin(srcChar[i], isPolyphone, separator);
        }
        return srcStr;
    }

    /**
     * 将单个字符转换成拼音
     * 
     * @param src
     * @return
     */
    private static String charToPinyin(char src, boolean isPolyphone,
            String separator) {
        // 创建汉语拼音处理类
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 输出设置，大小写，音标方式
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuffer tempPinying = new StringBuffer();
        

        // 如果是中文
        if (src > 128) {
            try {
                // 转换得出结果
                String[] strs = PinyinHelper.toHanyuPinyinStringArray(src,
                        defaultFormat);
                
                        
                // 是否查出多音字，默认是查出多音字的第一个字符
                if (isPolyphone && null != separator) {
                    for (int i = 0; i < strs.length; i++) {
                        tempPinying.append(strs[i]);
                        if (strs.length != (i + 1)) {
                            // 多音字之间用特殊符号间隔起来
                            tempPinying.append(separator);
                        }
                    }
                } else {
                    tempPinying.append(strs[0]);
                }

            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else {
            tempPinying.append(src);
        }

        return tempPinying.toString();

    }

    /**
     * 获取拼音简码
     * @param src
     * @return
     */
    public static String stringToHeadPinYin(String src){
    	String tmp [] =getHeadByString(src,false);
    	StringBuffer str = new StringBuffer();
    	for(int i=0;i<tmp.length;i++){
    		str.append(tmp[i]);
    	}
    	return str.toString();
    }
    
    /**
     * 获取拼音全拼
     * @param src
     * @return
     */
    public static String stringToPinYin(String src){
    	String tmp [] =stringToPinyin(src);
    	StringBuffer str = new StringBuffer();
    	for(int i=0;i<tmp.length;i++){
    		str.append(tmp[i]);
    	}
    	return str.toString();
    }

    
    public static void main(String [] args){
    	//String tmp [] =getHeadByString("张璠你是个好同志",false);
    	String tmp [] =stringToPinyin("张璠你是个好同志");
    	StringBuffer str = new StringBuffer();
    	for(int i=0;i<tmp.length;i++){
    		str.append(tmp[i]);
    	}
    	System.out.println(str.toString());
    }

}
