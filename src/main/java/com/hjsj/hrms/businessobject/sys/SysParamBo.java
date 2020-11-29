package com.hjsj.hrms.businessobject.sys;

import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: SysParamBo </p>
 * <p>Description: system.properties参数移植到在constant操作业务类</p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-9-28 下午10:15:44</p>
 * @author xuj
 * @version 1.0
 */
public class SysParamBo {

	private static FastHashMap paramhm = new FastHashMap();
	
	/**
	 * 获取参数值（逐步将system.properties参数移植到在constant常量表中保存,提供先读取是否在constant中定义，如果未找到参数，需再到system.properties中读取）
	 * @param module 参数模块，如：系统管理  SysParamConstant.SYS_SYS_PARAM
	 * @param param 参数名 如：首次密码强制修改 SysParamConstant.LOGIN_FIRST_CHANG_PWD
	 * @return
	 */
	public static String getSysParamValue(String module,String param){
		String value = "";
		try{
			if(paramhm.get(module)==null){
				initParamHM(module);
			}
			HashMap hm = (HashMap)paramhm.get(module);
			if(hm!=null) {
                value = (String)hm.get(param);
            } else{
				hm = new HashMap();
				paramhm.put(module, hm);
			}
			//当模块参数是THEMES时 ，不需要去system.properties中提取参数   add by hej 2015/11/17
			if(!"THEMES".equals(module)){
				if(value==null||value.length()==0){
					value=SystemConfig.getPropertyValue(param);
					hm.put(param, value);
				}
			}
			//添加主题是否存在校验 guodd 2020-04-26
			if("THEMES".equals(module) && (StringUtils.isEmpty(value) || !SystemConfig.getSystemThemes().contains(value))){
				/*默认取第一个*/
				value=(String)SystemConfig.getSystemThemes().get(0);
				hm.put(param, value);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 获取模块下所有参数(移植后到canstant的)
	 * @param module 参数模块，如：系统管理  SysParamConstant.SYS_SYS_PARAM
	 * @return
	 */
	public static Map getSysParamValues(String module){
		Map paramMap = new HashMap();
		try{
			if(paramhm.get(module)==null){
				initParamHM(module);
			}
			paramMap = (HashMap)paramhm.get(module);
		}catch(Exception e){
			e.printStackTrace();
		}
		return paramMap;
	}
	
	/**
	 * 单个保存参数(会移植)
	 * @param module 参数模块，如：系统管理  SysParamConstant.SYS_SYS_PARAM
	 * @param param 参数名 如：首次密码强制修改 SysParamConstant.LOGIN_FIRST_CHANG_PWD
	 * @param value 参数值
	 */
	public static void setSysParamValue(String module,String param,String value){
		Connection conn = null;
		try{
			//刷新缓存
			HashMap hm = (HashMap)paramhm.get(module);
			if(hm==null){
				hm = new HashMap();
				paramhm.put(module, hm);
			}
			hm.put(param, value);
			
			if("SYS_SYS_PARAM".equals(module)){
				//刷新框架缓存
				ConstantParamter.setAttribute(param, value);
			}
			
			//持久化参数
			conn = AdminDb.getConnection();
			ConstantXml xml = new ConstantXml(conn,module,"props");
			xml.setTextValue("/props/"+param, value);
			xml.saveStrValue();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(conn!=null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
	}
	
	/**
	 * 批量保存参数
	 * @param module 参数模块，如：系统管理  SysParamConstant.SYS_SYS_PARAM
	 * @param paramMap 参数map key为参数名，value为参数值
	 */
	public static void setSysParamValues(String module,Map paramMap){
		Connection conn = null;
		try{
			//刷新缓存
			HashMap hm = (HashMap)paramhm.get(module);
			if(hm==null){
				hm = new HashMap();
				paramhm.put(module, hm);
			}
			if("SYS_SYS_PARAM".equals(module)){
				for(Iterator i=paramMap.keySet().iterator();i.hasNext();){
					String key =(String)i.next();
					hm.put(key, paramMap.get(key));
					//刷新框架缓存
					ConstantParamter.setAttribute(key, (String)paramMap.get(key));
				}
			}
			
			
			//持久化参数
			conn = AdminDb.getConnection();
			ConstantXml xml = new ConstantXml(conn,module,"props");
			for(Iterator i = paramMap.keySet().iterator();i.hasNext();){
				String key = (String)i.next();
				xml.setTextValue("/props/"+key,(String)paramMap.get(key));
			}
			xml.saveStrValue();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(conn!=null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
	}
	
	/**
	 * 初始化参数放到缓存
	 * @param module
	 */
	private static void initParamHM(String module){
		Connection conn = null;
		HashMap moduleMap = new HashMap();
		try{
			conn = AdminDb.getConnection();
			ConstantXml xml = new ConstantXml(conn,module,"props");
			List elements = xml.getAllChildren("props");
			if(elements!=null) {
                for(int i=0;i<elements.size();i++){
                    Element e = (Element)elements.get(i);
                    moduleMap.put(e.getName(), e.getText());
                }
            }
			paramhm.put(module, moduleMap);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(conn!=null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
	}	

}
