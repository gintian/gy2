package com.hjsj.hrms.module.gz.utils;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 薪资工具类
 * 存放静态方法，尽量不出现数据库数据连接方法。
 *
 * @author ZhangHua
 * @date 14:47 2018/8/15
 */
public class SalaryUtils {
    /**
     * 解码前台传入的参数
     * @param panam 参数名
     * @param form
     * @return
     */
    public static Object decodeParam(String panam, HashMap form) {
        Object obj = null;
        if (!form.containsKey(panam) || form.get(panam) == null) {
            if (form.containsKey("customParams")) {
                MorphDynaBean morphDynaBean = (MorphDynaBean) form.get("customParams");
                DynaProperty[] list = morphDynaBean.getDynaClass().getDynaProperties();
                for (DynaProperty property : list) {
                    if (property.getName().equalsIgnoreCase(panam)) {
                        obj = morphDynaBean.get(panam);
                        break;
                    }
                }
            }
            if (obj == null) {
                return null;
            }
        } else {
            obj = form.get(panam);
        }
        if (obj instanceof Integer) {
            return obj;
        } else if (obj instanceof String) {
        	// 59898 加密方式已更换 故重新更改校验方法
        	String objstr =  PubFunc.decrypt(SafeCode.decode(String.valueOf(obj)));
        	if((SafeCode.encode(PubFunc.encrypt(objstr))).equals(String.valueOf(obj))) {
        		return objstr;
        	}else {
        		return String.valueOf(obj);
        	}
        } else {
            return null;
        }
    }

    /**
     * 拼接columnsinfo
     *
     * @param columnId
     * @param codesetId
     * @param columnDesc
     * @param columnWidth
     * @param type
     * @param DecimalWidth
     * @param isReadOnly
     * @param Loadtype
     * @param textAlign
     * @param columnLength
     * @return
     */
    public static ColumnsInfo getColumnsInfo(String columnId, String codesetId, String columnDesc, int columnWidth, String type, int DecimalWidth, boolean isReadOnly
            , int Loadtype, String textAlign, int columnLength) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setCodesetId(codesetId);// 指标集
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        columnsInfo.setSortable(true);// 是否排序
        columnsInfo.setColumnLength(columnLength);
        columnsInfo.setDecimalWidth(DecimalWidth);// 小数位
        columnsInfo.setReadOnly(isReadOnly);
        columnsInfo.setLoadtype(Loadtype);

        // 数值默认居右,其余都是居左的
        if ("N".equals(type)) {
            columnsInfo.setTextAlign("right");
        }else {
        	columnsInfo.setTextAlign("left");
        }


        if (StringUtils.isNotBlank(textAlign)) {
            columnsInfo.setTextAlign(textAlign);
        }

        return columnsInfo;
    }

    /**
     * FieldItem转化为ColumnsInfo
     *
     * @param fieldItems
     * @return
     */
    public static ArrayList<ColumnsInfo> convertFieldItemToColumnsInfo(ArrayList<FieldItem> fieldItems) {
        ArrayList<ColumnsInfo> list = new ArrayList();
        for (FieldItem fieldItem : fieldItems) {
            int displayWidth = 0;
            if (fieldItem.getDisplaywidth() > 0) {
                displayWidth = fieldItem.getDisplaywidth() * 8;
            } else {
                displayWidth = fieldItem.getItemdesc().length() * 20 < 100 ? 100 : fieldItem.getItemdesc().length() * 20;
            }
            int itemLength = fieldItem.getItemlength();
            if ("D".equalsIgnoreCase(fieldItem.getItemtype()) && StringUtils.isNotBlank(fieldItem.getFormat())) {
                itemLength = fieldItem.getFormat().length();
            }
            list.add(SalaryUtils.getColumnsInfo(fieldItem.getItemid(), fieldItem.getCodesetid(), fieldItem.getItemdesc()
                    , displayWidth, fieldItem.getItemtype(), fieldItem.getDecimalwidth(), fieldItem.isReadonly(),
                    fieldItem.isVisible() ? ColumnsInfo.LOADTYPE_BLOCK : ColumnsInfo.LOADTYPE_ONLYLOAD,
                    fieldItem.getAlign(), itemLength));

        }
        return list;
    }

    /**
     * 获取前台查询组件得到的sql
     *
     * @param form
     * @param userView
     * @param subModuleId
     * @param prefixId
     * @param onlyName
     * @return
     */
    public static String getQueryBoxSql(HashMap form, UserView userView, String subModuleId, String prefixId, String onlyName) {
        String type = (String) form.get("type");
        ArrayList<String> valuesList = (ArrayList) form.get("inputValues");
        StringBuffer strSql = new StringBuffer();
        if ("1".equals(type)) {
            // 输入的内容
            for (int i = 0; i < valuesList.size(); i++) {
                String queryValue = SafeCode.decode(valuesList.get(i));
                if (StringUtils.isBlank(queryValue)) {
                    continue;
                }
                strSql.append(" or a0101 like '%" + queryValue + "%'");
                if (StringUtils.isNotBlank(onlyName)) {
                    strSql.append(" or " + onlyName + " like '%" + queryValue + "%'");
                }
            }
            if (strSql.length() > 0) {
                strSql.delete(0, 3);
                strSql.insert(0, " AND ( ");
                strSql.append(" ) ");
            }
        } else if ("2".equals(type)) {
            String exp = (String) form.get("exp");
            String cond = (String) form.get("cond");
            TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get(prefixId);
            HashMap queryFields = tableCache.getQueryFields();
            // 解析表达式并获得sql语句
            FactorList parser = new FactorList(PubFunc.keyWord_reback(SafeCode.decode(exp)), PubFunc.keyWord_reback(SafeCode.decode(cond)), userView.getUserName(), queryFields);
            try {
                strSql.append(parser.getSingleTableSqlExpression("data"));
            } catch (GeneralException e) {
                e.printStackTrace();
            }
        }
        return strSql.toString();
    }
    
    /**
     * 是否在系统管理设置了邮箱
     * @return
     * @throws GeneralException
     */
    public String getSetEmailInfo() throws GeneralException {
    	String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null)
        	return ResourceFactory.getProperty("label.gz.noEmailAddress");
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param))
        	return ResourceFactory.getProperty("label.gz.noEmailAddress");
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        String host=stmp.getAttributeValue("host");
	        String from_addr=stmp.getAttributeValue("from_addr");
	        String password=stmp.getAttributeValue("password");
	        String port=stmp.getAttributeValue("port");
	        if(StringUtils.isBlank(host) || StringUtils.isBlank(from_addr) || StringUtils.isBlank(password) || StringUtils.isBlank(port)) {
	        	//邮件发送失败，请联系管理员检查邮箱服务器配置！
	        	return ResourceFactory.getProperty("label.gz.noEmailAddress");
	        }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
    }
}
