/*
 * @(#)ServiceUtils.java 2018年5月17日上午9:18:42
 * hrms
 * Copyright 2018 HJSOFT, Inc. All rights reserved.
 * HJSOFT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hjsj.hrms.module.serviceclient.utils;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.StringReader;
import java.sql.Connection;
import java.util.List;

/**
 * 自助服务终端工具类
 * @Titile: ServiceUtils
 * @Description:
 * @Company:hjsj
 * @Create time: 2018年5月17日上午9:18:42
 * @author: xuchangshun
 * @version 1.0
 *
 */
public class ServiceUtils {

    public String getCardIdField() {
        String cardIdField = StringUtils.EMPTY;//系统中配置的身份证指标
        Connection conn = null;
        try{
            conn = AdminDb.getConnection();
            Sys_Oth_Parameter sybo = new Sys_Oth_Parameter(conn);
            cardIdField = sybo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");//当前设置的身份证指标
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        	PubFunc.closeDbObj(conn);
        }
        return cardIdField;
    }
    public String getIcCardField() throws GeneralException {
        String icCardField = StringUtils.EMPTY;//系统中配置的工卡指标
        try {
            StringReader reader = null;
            RecordVo recordVo = ConstantParamter.getConstantVo("serverClient_param");
            // 判断数据库中是否存在
            if (recordVo != null) {
                //读取xml转换为Document
                Document doc;
                doc =PubFunc.generateDom(recordVo.getString("str_value"));
                
                String xpath = "/param/iccard_itemid";
                XPath iccardPath = XPath.newInstance(xpath);
                Element element = (Element) iccardPath.selectSingleNode(doc);
                if(element!=null) {
                    icCardField = element.getAttributeValue("value");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icCardField;
    }
	public boolean needCheckPw() {
		boolean flag = false;//是否需要密码校验
		StringReader reader = null;
		RecordVo recordVo = ConstantParamter.getConstantVo("serverClient_param");
		try {
			// 判断数据库中是否存在
			if (recordVo != null) {
				//读取xml转换为Document
				Document doc;
				doc = PubFunc.generateDom(recordVo.getString("str_value"));
				//读取根节点
				Element root = doc.getRootElement();
				List list = root.getChildren();
				Element child;
				//提取数据输出到前台
				child = (Element) list.get(0);
				String needPwdInput = child.getAttributeValue("value");
				if("1".equals(needPwdInput)) {
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(reader);
		}
		return flag;
	}
}
