/*
 * @(#)ServiceSettingBo.java 2018年3月6日下午3:47:51 hrms Copyright 2018 HJSOFT, Inc.
 * All rights reserved. HJSOFT PROPRIETARY/CONFIDENTIAL. Use is subject to
 * license terms.
 */
package com.hjsj.hrms.module.serviceclient.serviceSetting.businessobject;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 服务信息设置业务类
 * 
 * @Titile: ServiceSettingBo
 * @Description:
 * @Company:hjsj
 * @Create time: 2018年3月6日下午3:47:51
 * @author: xuchangshun
 * @version 1.0
 *
 */
public class ServiceSettingBo {
    /**服务类型:打印**/
    private static final String C_SERVICE_TYPE_PRINT = "1";

    /**模版类型:业务模版**/
    private static final String C_TEMPLET_TYPE_BUSI = "1";

    /**模版类型:登记表**/
    private static final String C_TRMPLET_TYPE_REGI = "2";

    /**新建分类的权限**/
    private static final String C_PRIV_ADDGROUP = "9A441";

    /**修改分类的权限**/
    private static final String C_PRIV_EDITEGROUP = "9A442";

    /**删除分类的权限**/
    private static final String C_PRIV_DELETEGROUP = "9A443";

    /**新增服务的权限**/
    private static final String C_PRIV_ADDSERVICE = "9A444";

    /**修改服务的权限**/
    private static final String C_PRIV_EDITSERVICE = "9A445";

    /**删除服务的权限**/
    private static final String C_PRIV_DELETESERVICE = "9A446";

    /** 数据库连接 **/
    private Connection conn;

    /** 用户信息userView **/
    private UserView userView;

    /**日志文件**/
    private Category cat = Category.getInstance(this.getClass());

    public ServiceSettingBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userView = userview;

    }
    
    /**
     * 获取各个服务分类以及其中的服务信息
     * 
     * @return data
     */
    public List<Map<String, Object>> getGroupDatas() {
        List<Map<String, Object>> groupDataList = new ArrayList<Map<String, Object>>();// 获取多条group用List
        Map<String, List<Map<String, String>>> groupServicesMap = new HashMap<String, List<Map<String, String>>>();//存放groupid与其下的service相对应的关系
        RowSet rs = null;
        StringBuffer selectSqlBuffer = new StringBuffer();
        try {
            selectSqlBuffer.append("select tgroup.groupid as groupid,tgroup.name as groupname,tservice.serviceid as serviceid,tservice.name as servicename,");
            selectSqlBuffer.append("type,icon,url,template_id,template_type,effectiveDate,description,params ");
            selectSqlBuffer.append("from t_sys_serviceclient_group tgroup left join  t_sys_serviceclient_service tservice on tservice.groupid=tgroup.groupid");
            selectSqlBuffer.append(" order by tgroup.norder,tservice.serviceid");
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(selectSqlBuffer.toString());// 查询服务分类表 的id和名称
            
            StringReader reader = null;
            Document document = null;
            Element element = null;
            while (rs.next()) {
                List<Map<String, String>> servicesList = null;
                Map<String, String> servicesMap = null;

                String groupId = rs.getString("groupid");//分组id
                String name = rs.getString("groupname");//分组名称
                /**services相关属性开始**/
                String serviceId = rs.getString("serviceid");
                String servicename = rs.getString("servicename");
                String type = rs.getString("type");
                String icon = rs.getString("icon");
                String url = rs.getString("url");
                String templateId = rs.getString("template_id");
                String templateType = rs.getString("template_type");
                String effectiveDate = rs.getString("effectiveDate");
                String description = rs.getString("description");
                
                String config_input_enable = StringUtils.EMPTY;
                String notice_enable = StringUtils.EMPTY;
                String params = rs.getString("params");
                if(StringUtils.isNotBlank(params)) {
                    document = PubFunc.generateDom(params);
                    
                    String xpath = "/params/notice_enable";
                    XPath paramPath = XPath.newInstance(xpath);
                    element= (Element) paramPath.selectSingleNode(document);
                    notice_enable = element.getAttributeValue("value");
                    xpath = "/params/config_input_enable";
                    paramPath = XPath.newInstance(xpath);
                    element= (Element) paramPath.selectSingleNode(document);
                    if(element!=null) {
                        config_input_enable = element.getAttributeValue("value");
                    }
                }
                if (StringUtils.isNotBlank(serviceId)) {//如果服务id为空则不增加
                    servicesMap = new HashMap<String, String>();
                    servicesMap.put("serviceId", serviceId);//服务id
                    servicesMap.put("name", servicename);//服务名称
                    servicesMap.put("type", type);//服务类型
                    servicesMap.put("icon", icon);//服务图标
                    servicesMap.put("url", url);//服务url,非打印服务才有
                    servicesMap.put("templateId", templateId);//服务模版id
                    servicesMap.put("templateType", templateType);//模板类型
                    servicesMap.put("effectiveDate", effectiveDate);//打印有效期
                    servicesMap.put("description", description);//服务须知描述
                    servicesMap.put("notice_enable", notice_enable);//服务须知勾选
                    servicesMap.put("config_input_enable", config_input_enable);//配置填写字段
                }
                /**services相关属性结束**/
                if (groupServicesMap.get(groupId) == null) {//如果还不存在该group的信息时,需要构建一个出来
                	servicesList = new ArrayList<Map<String, String>>();
                    Map<String, Object> groupMap = new HashMap<String, Object>();//group分组中的属性
                    groupMap.put("groupId", groupId);
                    groupMap.put("name", name);
                    groupMap.put("services", servicesList);
                    groupDataList.add(groupMap);
                } else {
                    servicesList = groupServicesMap.get(groupId);
                }
                if (MapUtils.isNotEmpty(servicesMap)) {
                    servicesList.add(servicesMap);
                }
                groupServicesMap.put(groupId, servicesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return groupDataList;

    }

    /**
     * 分组名重复检查
     */
    public boolean checkgroupName(String groupName) {
        boolean flag = false;
        String sql = "select name FROM t_sys_serviceclient_group where Name=? ";
        ContentDAO dao = new ContentDAO(this.conn);
        List<String> list = new ArrayList<String>();
        list.add(groupName);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return flag;
    }

    /**
     * * 新增分组信息
     *  
     *  @param groupName
     *            分组名称
     * @return 分组id
     */
    public String addGroupData(String groupName) {
        int maxGroupId = 0;// 最大的分组id
        int maxNorder = 0;// 最大的排序号
        String sql = "select max(groupid) as maxid,max(norder) as maxorder from t_sys_serviceclient_group";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            while (rs.next()) {
                maxGroupId = rs.getInt("maxid") + 1;// 获取最大的groupid+1
                maxNorder = rs.getInt("maxorder") + 1;// 获取最大的norder+1
            }
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            RecordVo vo = new RecordVo("t_sys_serviceclient_group");
            vo.setString("name", groupName);
            vo.setInt("groupid", maxGroupId);
            vo.setInt("norder", maxNorder);
            vo.setString("createuser", userView.getUserFullName());//存名字 例如：张军
            vo.setDate("createtime", df.format(day));//当前时间
            dao.addValueObject(vo);//保存数据

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return String.valueOf(maxGroupId);
    }

    /**
     * 编辑自助分组名字
     * 
     * @param groupName
     * @param groupId
     */
    public void updateGroupData(String groupName, String groupId) {
        try {
            List<String> list = new ArrayList<String>();
            list.add(groupName);
            list.add(groupId);
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sqlStr = new StringBuffer("");
            sqlStr.append("UPDATE t_sys_serviceclient_group SET name=? where groupId=? ");
            dao.update(sqlStr.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据分组的ID删除分组
     * @param groupId 分组ID
     */
    public void deleteGroupData(String groupId) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            List<String> list = new ArrayList<String>();
            list.add(groupId);
            StringBuffer SqlSer = new StringBuffer("");
            SqlSer.append("DElETE from t_sys_serviceclient_service where groupId=?");
            dao.delete(SqlSer.toString(), list);//删除服务
            StringBuffer sqlGroup = new StringBuffer("");
            sqlGroup.append("DElETE from t_sys_serviceclient_group");
            sqlGroup.append(" where groupId=?");
            dao.delete(sqlGroup.toString(), list);//删除分组
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据serviceId删除服务
     * @param serviceId
     */
    public void deleteServiceData(String serviceId) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            List<String> list = new ArrayList<String>();
            StringBuffer sqlSer = new StringBuffer("");
            sqlSer.append("DElETE from t_sys_serviceclient_service where serviceId=?");//删除serviece表服务
            list.add(serviceId);
            dao.delete(sqlSer.toString(), list);
            //先使用删除历史表解决问题
            List<String> listry = new ArrayList<String>();
            StringBuffer sqlService = new StringBuffer("");
            sqlService.append("DElETE from t_sys_serviceclient_histroy");//删除history历史表服务
            sqlService.append(" where serviceId=?");
            listry.add(serviceId);
            dao.delete(sqlService.toString(), listry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存分组排序
     * @param sort 前台传递过来的排序信息
     */
    public void saveGroupSortData(List<MorphDynaBean> sort) {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            //参数list
            List<List<String>> sortDatalist = new ArrayList<List<String>>();
            //sqlList
            List<String> sqls = new ArrayList<String>();
            for (MorphDynaBean sortBean : sort) {//分类信息
                StringBuffer sqlStr = new StringBuffer("");
                List<String> sortDataValues = new ArrayList<String>();
                sqlStr.append(" UPDATE t_sys_serviceclient_group SET");
                sqlStr.append(" norder= ?");
                sqlStr.append(" where groupid= ? ");
                sqls.add(sqlStr.toString());
                //获取bean里的数据  groupId ，前台如果是刚刚新建的分类 id类型是Integer类型，初始化的数据 id 是String类型
                String groupId = (String) sortBean.get("groupId");
                //每一条Sql语句所需要的值 放进一个list集合中
                Integer norder = (Integer) sortBean.get("norder");
                sortDataValues.add(norder.toString());
                sortDataValues.add(groupId);
                //所有的Sql语句，所需要的数据依次放进一个大集合中
                sortDatalist.add(sortDataValues);
            }
            //批量更新
            dao.batchUpdate(sqls, sortDatalist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回显数据
     * @param serviceId   服务id
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map showService(String serviceId, String templateType) {
        Map map = new HashMap();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        try {
            StringBuilder sql = new StringBuilder();
            String tabName = "template_table";
            if ("2".equals(templateType)) {
                tabName = "rname";
            }
            sql.append("select tService.name name,tService.icon icon,template_type,type,description,params,template_id,effectiveDate,free_print_count,print_price,url, tTbale.name tableName ");
            sql.append(" from t_sys_serviceclient_service tService left join ").append(tabName).append(" tTbale on tService.template_id = tTbale.tabid");
            sql.append(" where serviceid=?");

            ArrayList<String> list = new ArrayList<String>();
            list.add(serviceId);
            rowSet = dao.search(sql.toString(), list);
            if (rowSet.next()) {
                String effectiveDate = rowSet.getString("effectiveDate") == null ? "" : rowSet.getString("effectiveDate");
                String type = rowSet.getString("type");
                String template_type = rowSet.getString("template_type");
                String tableName = rowSet.getString("tableName");
                String templateId = rowSet.getString("template_id");
                map.put("templateId", templateId);
                map.put("freePrintCount", rowSet.getString("free_print_count"));
                map.put("printPrice", rowSet.getString("print_price"));
                map.put("effectiveDate", effectiveDate);
                map.put("template_type", template_type);
                map.put("type", type);
                map.put("name", rowSet.getString("name"));
                map.put("sicon", rowSet.getString("icon"));
                String params = rowSet.getString("params");
                
                String notice_enable = StringUtils.EMPTY;//服务启用标识
                String config_input_enable = StringUtils.EMPTY;//配置项启用标识
                List configInputsList = new ArrayList();//存放所有配置项
                if(StringUtils.isNotBlank(params)) {
                    Document doc = null;
                    Element element = null;
                    doc = PubFunc.generateDom(params);
                    
                    String xpath = "/params/items/item";
                    XPath itemsPath = XPath.newInstance(xpath);
                    List itemsList =  itemsPath.selectNodes(doc);
                    boolean blankFlag = CollectionUtils.isNotEmpty(itemsList);
                    if(blankFlag) {
                        Map configInputMap = null;//一条配置项的数据
                        for(int i=0;i<itemsList.size();i++) {
                            configInputMap = new HashMap();
                            element = (Element) itemsList.get(i);
                            String setId = element.getAttributeValue("setId");
                            String itemId = element.getAttributeValue("itemId");
                            String isWrite = element.getAttributeValue("isWrite");
                            FieldSet fieldSet = DataDictionary.getFieldSetVo(setId);
                            String fieldSetDesc = fieldSet.getFieldsetdesc();
                            FieldItem fieldItem = DataDictionary.getFieldItem(itemId);
                            String itemdesc = fieldItem.getItemdesc();
                            configInputMap.put("setId", setId);
                            configInputMap.put("itemId", itemId);
                            configInputMap.put("isWrite", isWrite);
                            configInputMap.put("fieldSetDesc", fieldSetDesc);
                            configInputMap.put("itemdesc", itemdesc);
                            configInputsList.add(configInputMap);
                        }
                    }
                    
                    xpath = "/params/notice_enable";
                    XPath notice_enablePath = XPath.newInstance(xpath);
                    element = (Element) notice_enablePath.selectSingleNode(doc);
                    notice_enable = element.getAttributeValue("value");
                    
                    xpath = "/params/config_input_enable";
                    XPath config_input_enablePath = XPath.newInstance(xpath);
                    element = (Element) config_input_enablePath.selectSingleNode(doc);
                    if(element!=null) {
                        config_input_enable = element.getAttributeValue("value");
                    }
                }else {
                    notice_enable = "0";
                    config_input_enable="0";
                }
                map.put("configInputsList", configInputsList);//配置录入项集合
                map.put("notice_enable", notice_enable);//服务须知启用标识
                map.put("config_input_enable", config_input_enable);//配置录入项启用标识
                
                if (!"1".equals(type)) {
                    map.put("url", rowSet.getString("url"));
                }
                if ("1".equals(type)) {
                    map.put("viewName", templateId + ":" + tableName);
                }
                map.put("description", rowSet.getString("description"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return map;
    }

    /**
     * 编辑服务信息
     * @param editData  服务数据
     */
    public void editService(MorphDynaBean editData) {
        ContentDAO dao = new ContentDAO(this.conn);
        RecordVo vo = new RecordVo("t_sys_serviceclient_service");
        String name = (String) editData.get("name");// 服务名称
        name = PubFunc.hireKeyWord_filter(name);
        int type = (Integer) editData.get("type");// 1:打印服务；2：其他服务
        String icon = (String) editData.get("icon");// 服务图标
        String description = (String) editData.get("description");// 服务描述
        int serviceId = Integer.parseInt((String) editData.get("serviceId"));
        String guidelinesCheck = (String)editData.get("guidelinesCheck");//服务须知启用标识
        String configInputCheck = (String)editData.get("configInputCheck");//配置录入项启用标识
        List<MorphDynaBean> inputItemArray = (ArrayList<MorphDynaBean>) editData.get("inputItemArray");//配置录入项
        try {
            if (type == 1) {
                int templateType = Integer.parseInt((String) editData.get("templateType"));// 模板类型 1:业务模板 2:登记表
                if (templateType == 1) {
                    String effectiveDate = (String) editData.get("effectiveDate");// 有效期，单位月（按30天算)
                    vo.setInt("effectivedate", Integer.parseInt(effectiveDate));
                }
                String freePrintCount = (String) editData.get("freePrintCount");// 免费打印份数
                String printPrice = (String) editData.get("printPrice");// 打印一份的价格

                vo.setInt("free_print_count", Integer.parseInt(freePrintCount));
                vo.setDouble("print_price", Double.parseDouble(printPrice));
            } else if (type == 2) {
                String url = (String) editData.get("url");// 其他服务地址
                vo.setString("url", url);
            }
            // 组装xml
            Element root = new Element("params");
            Element child = null;
            //创建子节点
            boolean blankFlag = CollectionUtils.isNotEmpty(inputItemArray);
            if(blankFlag) {
                child = new Element("items");
                Element floor = null;
                for(int i=0;i<inputItemArray.size();i++) {
                   floor = new Element("item");
                   Map itemMap =  PubFunc.DynaBean2Map(inputItemArray.get(i));
                   String setId = (String) itemMap.get("setId");
                   String itemId = (String) itemMap.get("itemId");
                   String isWrite = (String) itemMap.get("isWrite");
                   floor.setAttribute("setId", setId);
                   floor.setAttribute("itemId", itemId);
                   floor.setAttribute("isWrite", isWrite);
                   child.addContent(floor);
                }
                root.addContent(child);
            }
            
            child = new Element("notice_enable");
            child.setAttribute("value", guidelinesCheck);
            root.addContent(child);
            
            if(!"2".equals(configInputCheck)) {
                child = new Element("config_input_enable");
                child.setAttribute("value", configInputCheck);
                root.addContent(child);
            }
            // 生成XMLOutputter
            Document myDocument = new Document(root);
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            
            String userName = this.userView.getUserName();
            
            vo.setInt("serviceid", serviceId);
            vo.setString("name", name);
            vo.setString("icon", icon);
            vo.setInt("type", type);
            vo.setString("description", description);
            vo.setString("params", outputter.outputString(myDocument));
            vo.setString("createuser", userName);
            dao.updateValueObject(vo);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增服务
     * 
     * @param serviceConfig
     *            服务详细配置信息
     * @return
     */
    public int addService(MorphDynaBean serviceConfig) {
        int serviceId = 0;
        try {
            ContentDAO dao = new ContentDAO(this.conn);

            String groupId = (String) serviceConfig.get("groupId");// 所属服务分类id
            String name = (String) serviceConfig.get("name");// 服务名称
            name = PubFunc.hireKeyWord_filter(name);
            int type = (Integer) serviceConfig.get("type");// 1:打印服务；2：其他服务
            String icon = (String) serviceConfig.get("icon");// 服务图标
            String description = (String) serviceConfig.get("description");// 服务描述
            String guidelinesCheck = (String)serviceConfig.get("guidelinesCheck");//服务须知启用标识
            String configInputCheck = (String)serviceConfig.get("configInputCheck");//配置录入项启用标识
            List<MorphDynaBean> inputItemArray = (ArrayList<MorphDynaBean>) serviceConfig.get("inputItemArray");//配置录入项
            RecordVo vo = new RecordVo("t_sys_serviceclient_service");
            serviceId = getServiceId(dao);
            if (type == 1) {
                String templateId = (String) serviceConfig.get("templateId");// 打印服务模板id
                int templateType = (Integer) serviceConfig.get("templateType");// 模板类型 1:业务模板 2:登记表
                String freePrintCount = (String) serviceConfig.get("freePrintCount");// 免费打印份数
                String printPrice = (String) serviceConfig.get("printPrice");// 打印一份的价格
                vo.setInt("template_id", Integer.parseInt(templateId));
                vo.setInt("template_type", templateType);
                if (templateType == 1) {
                    String effectiveDate = (String) serviceConfig.get("effectiveDate");// 有效期，单位月（按30天算)
                    vo.setInt("effectivedate", Integer.parseInt(effectiveDate));
                }
                vo.setInt("free_print_count", Integer.parseInt(freePrintCount));
                vo.setDouble("print_price", Double.parseDouble(printPrice));
            } else if (type == 2) {
                String url = (String) serviceConfig.get("url");// 其他服务地址
                vo.setString("url", url);
            }
            
            // 组装xml
            Element root = new Element("params");
            Element child = null;
            //创建子节点
            boolean blankFlag = CollectionUtils.isNotEmpty(inputItemArray);
            if(blankFlag) {
                child = new Element("items");
                Element floor = null;
                for(int i=0;i<inputItemArray.size();i++) {
                   floor = new Element("item");
                   Map itemMap =  PubFunc.DynaBean2Map(inputItemArray.get(i));
                   String setId = (String) itemMap.get("setId");
                   String itemId = (String) itemMap.get("itemId");
                   String isWrite = (String) itemMap.get("isWrite");
                   floor.setAttribute("setId", setId);
                   floor.setAttribute("itemId", itemId);
                   floor.setAttribute("isWrite", isWrite);
                   child.addContent(floor);
                }
                root.addContent(child);
            }
            
            child = new Element("notice_enable");
            child.setAttribute("value", guidelinesCheck);
            root.addContent(child);
            
            if(!"2".equals(configInputCheck)) {
                child = new Element("config_input_enable");
                child.setAttribute("value", configInputCheck);
                root.addContent(child);
            }
            // 生成XMLOutputter
            Document myDocument = new Document(root);
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            
            Date createtime = new Date();
            String createTime = DateStyle.dateformat(createtime,"yyyy-MM-dd HH:mm:ss.mmm");
            String userName = this.userView.getUserName();
            // 组装RecordVo
            vo.setString("params", outputter.outputString(myDocument));
            vo.setInt("serviceid", serviceId);
            vo.setInt("groupid", Integer.parseInt(groupId));
            vo.setString("name", name);
            vo.setString("icon", icon);
            vo.setInt("type", type);
            vo.setString("description", description);
            vo.setDate("createtime", createTime);
            vo.setString("createuser", userName);
            dao.addValueObject(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceId;
    }

    /**
     * 获取serviceId
     * @return
     */
    private int getServiceId(ContentDAO dao) {
        int serviceId = 0;
        RowSet rowSet = null;
        try {
            String sql = "SELECT MAX(serviceid) as num FROM t_sys_serviceclient_service";
            rowSet = dao.search(sql);
            int serviceIdMax = 0;
            if (rowSet.next()) {
                if (rowSet.getInt("num") == 0) {
                    serviceId = 1;
                } else {
                    serviceIdMax = rowSet.getInt("num");
                    serviceId = serviceIdMax + 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return serviceId;
    }

    /**
     * 自助打印服务是否展示进行过滤
     * @param groupDatas 所有分组相关的数据
     */
    @SuppressWarnings("unchecked")
    public void groupServiceFilter(List<Map<String, Object>> groupDatas) {
        //服务类型servicetype 模板类型templateType
        Set<String> printTabidSet = new HashSet<String>();//打印服务的模版id
        Map<String, Map<String, String>> printServiceDataMap = new HashMap<String, Map<String, String>>();//各个打印服务对应的模版有效期等等

        Iterator<Map<String, Object>> groupDatasIterator = groupDatas.iterator();
        while (groupDatasIterator.hasNext()) {//对所有的服务分组进行循环
            Map<String, Object> groupDataMap = groupDatasIterator.next();//拿到其中一个分组
            List<Map<String, String>> servicesList = (List<Map<String, String>>) groupDataMap.get("services");//拿到分组中所有的service
            if (CollectionUtils.isEmpty(servicesList)) {
                groupDatasIterator.remove();
                continue;
            }
            Iterator<Map<String, String>> iterator = servicesList.iterator();
            while (iterator.hasNext()) {//对分组中所有的service进行循环
                Map<String, String> servicesMap = iterator.next();//拿到其中的一个service
                String templateId = servicesMap.get("templateId");//关联模版id
                String template_type = servicesMap.get("templateType");//模版类型
                String effectiveDate = servicesMap.get("effectiveDate");//有效期
                String type = servicesMap.get("type");//服务类型
                String serviceId = servicesMap.get("serviceId");//服务id

                if (StringUtils.equals(C_SERVICE_TYPE_PRINT, type)) {//打印服务进行过滤处理,其他服务不处理
                    //模板是否还存在
                    boolean tabExistFlag = checkTabExist(templateId, template_type);
                    if (StringUtils.equals(C_TRMPLET_TYPE_REGI, template_type)) {//登记表,只进行权限过滤
                        //没有该登记表的权限
                        if (!this.userView.isHaveResource(IResourceConstant.CARD, templateId) || !tabExistFlag) {
                            iterator.remove();
                        }
                    } else {//业务模版要进行是否有过申请,以及申请是否过期的处理,这种处理不能在循环中操作,要单独进行操作
                        if (!this.userView.isHaveResource(IResourceConstant.RSBD, templateId) || !tabExistFlag) {//没有该模版的权限
                            iterator.remove();
                        } else {
                            printTabidSet.add(templateId);
                            Map<String, String> serviceDataMap = new HashMap<String, String>();
                            serviceDataMap.put("tabid", templateId);
                            serviceDataMap.put("effectiveDate", effectiveDate);
                            printServiceDataMap.put(serviceId, serviceDataMap);
                        }
                    }
                }
            }

            if (servicesList.isEmpty()) {//如果服务的个数小于1,那么服务分组也不展示
                groupDatasIterator.remove();
            }
        }
        if (CollectionUtils.isNotEmpty(printTabidSet)) {
            //根据一次过滤拿到的模版id和service相关的超期天数,从数据库中查询数据 从而判断该服务是否超期
            Map<String, Map<String, Object>> allPrintTaidApplyDataMap = judgeServiceIsOverdue(printTabidSet, printServiceDataMap);
            //进行二次过滤,需要移除的进行移除,不需要移除的增加ins_id以及task_id参数
            groupDataSecondFilter(groupDatas, printServiceDataMap, allPrintTaidApplyDataMap);
        }
    }

    /**
     * 根据一次过滤拿到的模版id和service相关的超期天数,从数据库中查询数据
     * 从而判断该服务是否超期
     * @param printTabidSet
     * @param printServiceDataMap
     */
    private Map<String, Map<String, Object>> judgeServiceIsOverdue(Set<String> printTabidSet,
            Map<String, Map<String, String>> printServiceDataMap) {
        //分批次查询出当前所有配置模版中当前登录人员最后一次申请的情况
        List<String> printTabidList = new ArrayList<String>(printTabidSet);
        int loopCount = printTabidList.size() / 20 + 1;//循环的次数等于有多少个20 +1 个
        Map<String, Map<String, Object>> allPrintTaidApplyDataMap = new HashMap<String, Map<String, Object>>();//所有打印服务模版申请数据的map
        Map<String, Map<String, Object>> printTaidApplyDataMap;
        for (int i = 0; i < loopCount; i++) {
            int fromIndex = i * 20;
            int toIndex = (i + 1) * 20;
            if (toIndex >= printTabidList.size()) {
                toIndex = printTabidList.size();
            }
            List<String> subTabidList = printTabidList.subList(fromIndex, toIndex);
            printTaidApplyDataMap = getTabidApplyDatas(subTabidList);
            allPrintTaidApplyDataMap.putAll(printTaidApplyDataMap);
        }
        //对数据进行比较,过滤没有权限的以及过期的打印模版
        Iterator<Entry<String, Map<String, String>>> printServicesIterator = printServiceDataMap.entrySet().iterator();
        while (printServicesIterator.hasNext()) {
            Entry<String, Map<String, String>> entry = printServicesIterator.next();
            Map<String, String> serviceDataMap = entry.getValue();
            String tabid = serviceDataMap.get("tabid");
            String effectiveDate = serviceDataMap.get("effectiveDate");
            Map<String, Object> selectTabidData = allPrintTaidApplyDataMap.get(tabid);//从数据库中查询出来的最后一次模版申请的情况
            boolean isNeedRemove = true;
            if (MapUtils.isNotEmpty(selectTabidData)) {
                Date end_date = (Date) selectTabidData.get("end_date");
                int diffDays = DateUtils.dayDiff(end_date, new Date());
                int maxDays = Integer.valueOf(effectiveDate).intValue() * 30;
                boolean isOverdue = diffDays < maxDays;//如果天数超过规定的时间视为超期
                if (isOverdue) {//没有超期
                    isNeedRemove = false;
                }
            }
            if (isNeedRemove) {//如果超期则进行移除，能到这一步的都是拥有权限的模版了
                printServicesIterator.remove();
            }
        }
        return allPrintTaidApplyDataMap;
    }

    /**
     * 对groupDatas进行二次过滤
     * 此次过滤主要是去掉已经超期的业务模版
     * @param groupDatas 经过一次过滤需要在前台展现的服务
     * @param printServiceDataMap 最终需要在前台展现的服务
     * @param allPrintTaidApplyDataMap 
     */
    @SuppressWarnings("unchecked")
    private void groupDataSecondFilter(List<Map<String, Object>> groupDatas, Map<String, Map<String, String>> printServiceDataMap,
            Map<String, Map<String, Object>> allPrintTaidApplyDataMap) {
        Iterator<Map<String, Object>> newGroupDatasIterator = groupDatas.iterator();
        while (newGroupDatasIterator.hasNext()) {
            Map<String, Object> groupDataMap = newGroupDatasIterator.next();//拿到其中一个分组
            List<Map<String, String>> servicesList = (List<Map<String, String>>) groupDataMap.get("services");//拿到分组中所有的service
            Iterator<Map<String, String>> iterator = servicesList.iterator();
            while (iterator.hasNext()) {//对分组中所有的service进行循环
                Map<String, String> servicesMap = iterator.next();//拿到其中的一个service
                String template_type = servicesMap.get("templateType");//模版类型
                String servicetype = servicesMap.get("type");//服务类型
                String serviceId = servicesMap.get("serviceId");//服务id
                String templateId = servicesMap.get("templateId");//模板id

                if (StringUtils.equals(C_SERVICE_TYPE_PRINT, servicetype)) {//打印服务进行过滤处理,其他服务不处理
                    if (StringUtils.equals(C_TEMPLET_TYPE_BUSI, template_type)) {
                        //二次过滤只过滤,非等级表的
                        if (printServiceDataMap.get(serviceId) == null) {//需要过滤的进行移除
                            iterator.remove();
                        } else {//不需要过滤的增加参数
                            Map<String, Object> printTabidApplyData = allPrintTaidApplyDataMap.get(templateId);
                            String ins_id = (String) printTabidApplyData.get("ins_id");
                            String task_id = (String) printTabidApplyData.get("task_id");
                            servicesMap.put("ins_id", ins_id);
                            servicesMap.put("task_id", task_id);
                        }
                    }
                }
            }

            if (servicesList.isEmpty()) {
                newGroupDatasIterator.remove();
            }
        }
    }

    /**
    * 根据传递进来的tabids查询到最后一次申请的相关信息,
    * <一次最多查询次数为20个>
    * @param tabids
    * @return
    */
    private Map<String, Map<String, Object>> getTabidApplyDatas(List<String> tabids) {
        if (tabids.size() > 20) {
            cat.error("模版个数超过二十个,生成SQL可能超长。");
            return new HashMap<String, Map<String, Object>>();
        }
        StringBuffer sql = new StringBuffer();
        List<String> valuesList = new ArrayList<String>();//参数list
        int dbServer = Sql_switcher.searchDbServer();//mssql:1,oracle:2
        DbWizard dbWizard = new DbWizard(this.conn);
        //数据库查询Sql
        for (int i = 0; i < tabids.size(); i++) {

            String tabid = tabids.get(i);//模版号,用来生成表名
            String tableName = "templet_" + tabid;//表名
            boolean isNumber = NumberUtils.isNumber(tabid);
            if (!isNumber || !dbWizard.isExistTable(tableName, false)) {//防止非法的tabid传递
                continue;
            }
            if (Constant.MSSQL == dbServer) {
                sql.append(" select task_id,ins_id,end_date,tabid from (");
                sql.append(" select top 1 task_id,ins_id,end_date,'").append(tabid).append("' as tabid");
                sql.append(" from t_wf_task where ins_id in( ");
                sql.append(" select max(ins_id) from (select temp.ins_id as ins_id from ");
                sql.append(tableName).append(" temp join t_wf_instance twf on temp.ins_id=twf.ins_id ");
                sql.append(" where a0100=? and upper(basepre)=?  and finished=5)");
                sql.append("temp_").append(tabid);
                sql.append(") order by  task_id desc)t_").append(tabid);
            } else if (Constant.ORACEL == dbServer) {
                sql.append(" select task_id,ins_id,end_date,tabid from (");
                sql.append("  select task_id,ins_id,end_date,'").append(tabid).append("' as tabid from t_wf_task where ins_id in(");
                sql.append("      select max(temp.ins_id) as ins_id from ").append(tableName)
                        .append(" temp join t_wf_instance twf on temp.ins_id=twf.ins_id");
                sql.append("        where a0100=? and upper(basepre)=? and finished=5");
                sql.append(") order by task_id desc ) where rownum <= 1");
            }

            if (i < tabids.size() - 1) {
                sql.append(" union all ");
            }
            valuesList.add(this.userView.getA0100());
            valuesList.add(this.userView.getDbname().toUpperCase());
        }
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();
        try {
            if (StringUtils.isNotBlank(sql.toString())) {
                rs = dao.search(sql.toString(), valuesList);
                while (rs.next()) {
                    String ins_id = rs.getString("ins_id");//添加到service里
                    String tabid = rs.getString("tabid");
                    String task_id = rs.getString("task_id");//添加到service里
                    Date end_date = rs.getDate("end_date");
                    Map<String, Object> dataObject = new HashMap<String, Object>();
                    dataObject.put("ins_id", ins_id);
                    dataObject.put("tabid", tabid);
                    dataObject.put("task_id", task_id);
                    dataObject.put("end_date", end_date);
                    resultMap.put(tabid, dataObject);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return resultMap;
    }

    /**
    * 获取打印时该服务的部分信息
    */

    @SuppressWarnings({ "unchecked", "resource", "rawtypes" })
    public Map<String, String> getPrintService(String serviceId) {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        Map<String, String> dataObject = new HashMap<String, String>();
        List printList = new ArrayList();
        int free_print_count = 0;//免费打印页数
        int printCount = 0; //已打印页数
        int canPrintCount = 0; //可以打印的页数  free_print_count - printCount
        String print_price = "";
        String description = "";
        try {
            String sql = "select free_print_count,print_price,description from t_sys_serviceclient_service where serviceid =?";
            List<String> serviceids = new ArrayList<String>();
            serviceids.add(serviceId);
            rowSet = dao.search(sql, serviceids);
            if (rowSet.next()) {
                free_print_count = rowSet.getInt("free_print_count");
                print_price = rowSet.getString("print_price");
                description = rowSet.getString("description");
                description = description == null ? "" : description;
                dataObject.put("printPrice", print_price);//价格
                dataObject.put("description", description);//描述
            }
            rowSet = null;
            StringBuffer sqlbuf = new StringBuffer();
            Calendar Time = Calendar.getInstance();//时间
            int year = Time.get(Calendar.YEAR);//获取当前年
            sqlbuf.append("select PrintCount from  t_sys_serviceclient_histroy where serviceid = ?");
            sqlbuf.append(" and a0101 = ? and ");
            sqlbuf.append(Sql_switcher.year("servetime")).append(" =?");
            printList.add(serviceId);
            printList.add(userView.getUserFullName());
            printList.add(year);
            rowSet = dao.search(sqlbuf.toString(), printList);
            while (rowSet.next()) {
                printCount = printCount + rowSet.getInt("PrintCount");//获取本人本服务本年度已打印的页数
            }
            if (printCount < free_print_count) {
                canPrintCount = free_print_count - printCount;//可打印页数
            }
            dataObject.put("canPrintCount", String.valueOf(canPrintCount));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return dataObject;
    }

    /**
     * 保存服务使用记录 ，修改打印页数
     * @param serviceId
     * @param usedPage
     * @param printCount
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
    public boolean saveHistoriPrint(String serviceId, int usedPage, int printCount, String ip) {
        boolean isSucess = true;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        Map<String, String> dataObject = new HashMap<String, String>();
        List<String> printList = new ArrayList<String>();
        int Count = 0;
        int clientid = 0;
        int pageCount = 0;
        try {
            String sql = "select pageCount,clientid from t_sys_serviceclient_client where ip_address = ?";
            List<String> ipList = new ArrayList();
            ipList.add(ip);
            rowSet = dao.search(sql, ipList);
            if (rowSet.next()) {
                pageCount = rowSet.getInt("pageCount");
                clientid = rowSet.getInt("clientid");
            }
            pageCount = pageCount > usedPage ? pageCount - usedPage : 0;//更新后的纸张页数
            List list = new ArrayList();
            list.add(pageCount);
            list.add(ip);
            StringBuffer sqlStr = new StringBuffer("");
            sqlStr.append("UPDATE t_sys_serviceclient_client SET  pageCount=? where ip_address=? ");
            dao.update(sqlStr.toString(), list);//更新该机器下的页数

            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            RecordVo vo = new RecordVo("t_sys_serviceclient_histroy");
            vo.setString("username", userView.getUserName());
            vo.setString("a0101", userView.getUserFullName());//存名字 例如：张军
            vo.setDate("servetime", df.format(day));//当前时间
            vo.setInt("printcount", printCount);
            vo.setInt("serviceid", Integer.parseInt(serviceId));
            vo.setInt("clientid", clientid);
            dao.addValueObject(vo);//添加本此打印的信息
        } catch (Exception e) {
            isSucess = false;
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }

        return isSucess;
    }

    /**
     * 得到该终端机下的纸张数
     * @return
     */
    public String getPageCount(String ip) {
        String pageCount = "";
        RowSet rowSet = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "select pageCount from t_sys_serviceclient_client where ip_address = ?";
            List<String> ipList = new ArrayList();
            ipList.add(ip);
            rowSet = dao.search(sql, ipList);
            if (rowSet.next()) {
                pageCount = String.valueOf(rowSet.getInt("pageCount"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return pageCount;
    }

    /**
     * 获取权限信息
     * @return  权限信息
     */
    public Map getPrivControlData() {

        boolean isAddService = this.userView.hasTheFunction(C_PRIV_ADDSERVICE);
        boolean isEditService = this.userView.hasTheFunction(C_PRIV_EDITSERVICE);
        boolean isDeleteService = this.userView.hasTheFunction(C_PRIV_DELETESERVICE);
        boolean isAddGroup = this.userView.hasTheFunction(C_PRIV_ADDGROUP);
        boolean isEditGroup = this.userView.hasTheFunction(C_PRIV_EDITEGROUP);
        boolean isDeleteGroup = this.userView.hasTheFunction(C_PRIV_DELETEGROUP);

        Map<String, Boolean> priv = new HashMap<String, Boolean>();
        priv.put("isAddService", isAddService);
        priv.put("isEditService", isEditService);
        priv.put("isDeleteService", isDeleteService);
        priv.put("isAddGroup", isAddGroup);
        priv.put("isEditGroup", isEditGroup);
        priv.put("isDeleteGroup", isDeleteGroup);

        return priv;

    }

    /**
     * 获取终端机的Ip地址
     * @param ip
     * @return
     */
    public boolean isExistsIp(String ip) {
        boolean flag = false;
        RowSet rowSet = null;
        List<String> ipList = new ArrayList<String>();
        ipList.add(ip);
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "select * from t_sys_serviceclient_client where ip_address = ?";
            rowSet = dao.search(sql, ipList);
            if (rowSet.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return flag;
    }

    /**
     * 保存参数设置的信息
     * needPwdInput  是否需要读卡登陆时，需要登录密码（1为需要，0为不需要）
     * itemId  配置工卡指标
     */
    public boolean saveSettings(String needPwdInput,String itemId) {
        boolean result = true;
        try {
            // 组装xml
            Element root = new Element("param");
            //创建子节点
            Element child = new Element("password_card");
            child.setAttribute("value", needPwdInput);
            root.addContent(child);

            child = new Element("iccard_itemid");
            child.setAttribute("value", itemId);
            root.addContent(child);
            // 生成XMLOutputter
            Document myDocument = new Document(root);
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            // 组装RecordVo
            RecordVo para_vo = new RecordVo("constant");
            para_vo.setString("constant", "serverClient_param");
            para_vo.setString("str_value", outputter.outputString(myDocument));
            
            ContentDAO dao = new ContentDAO(this.conn);
            // 不存在则增加，存在则更新
            if (ConstantParamter.getConstantVo("serverClient_param") == null) {
                dao.addValueObject(para_vo);
            }else {
                dao.updateValueObject(para_vo);
            }
            // 放入ConstantParamter字典中
            ConstantParamter.putConstantVo(para_vo, "serverClient_param");
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            return result;
        }
        return result;
    }

    /**
     * 获取参数设置的信息
     */
    public Map searchSettings() {
        Map map = new HashMap();
        boolean flag = false;
        String str_Value = "";
        Document doc = null;
        Element element = null;
        String value ="";
        String fieldItemId = StringUtils.EMPTY;
        List<FieldItem> displayFielditem = new ArrayList<FieldItem>();
        try {
            List fielditemAllList = DataDictionary.getFieldList("A01", 1);
            for(int i=0;i<fielditemAllList.size();i++) {
                FieldItem fielditem = (FieldItem) fielditemAllList.get(i);
                if("A".equals(fielditem.getItemtype()) && "0".equals(fielditem.getCodesetid())) {
                    displayFielditem.add(fielditem);
                }
            }
            RecordVo serverClientVo = ConstantParamter.getRealConstantVo("serverClient_param");
            if(serverClientVo!=null){
                str_Value = serverClientVo.getString("str_value");

                doc = PubFunc.generateDom(str_Value);
                
                String xpath = "/param/password_card";
                XPath passwordPath = XPath.newInstance(xpath);
                element = (Element) passwordPath.selectSingleNode(doc);
                value = element.getAttributeValue("value");
                if("1".equals(value)) {
                    flag = true;
                }
                
                xpath = "/param/iccard_itemid";
                XPath iccardPath = XPath.newInstance(xpath);
                element = (Element) iccardPath.selectSingleNode(doc);
                fieldItemId = element.getAttributeValue("value");
            }
            map.put("password_cardValue", flag);
            map.put("fieldItemId", fieldItemId);
            map.put("displayFielditem", displayFielditem);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    /**
     * 是否需要修改密码
     */
    public boolean needModifyPassword() {
        boolean nmpFlag = false;//是否需要修改密码标识
        ContentDAO dao = new ContentDAO(this.conn);
        ResultSet rs = null;
        StringBuffer sql = new StringBuffer();
        List list = new ArrayList();
        try {
            String userName = this.userView.getUserName();
            list.add(userName);
            sql.append("select * from t_sys_login_user_info where username=? and first_login=0");
            rs = dao.search(sql.toString(),list);
            if(!rs.next()) {
                nmpFlag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return nmpFlag;
    }
    /**
     * 保存首次登录新修改的密码
     * @param oldpwd 
     * @param newokpwd 
     * @param newPassword 
     * @param accessType 
     */
    public String saveNewPassword(String oldpwd, String newPassword, String newokpwd, String accessType) {
        String errorFlag = StringUtils.EMPTY;
        try {
            //检查userview
            UserView userView = checkUserView();
            UserObjectBo userbo=new UserObjectBo(this.conn);
            
            newPassword = PubFunc.keyWord_reback(newPassword);
            newPassword = StringUtils.trimToEmpty(newPassword);
            oldpwd = PubFunc.keyWord_reback(oldpwd);
            oldpwd = StringUtils.trimToEmpty(oldpwd);
            newokpwd = PubFunc.keyWord_reback(newokpwd);
            newokpwd = StringUtils.trimToEmpty(newokpwd);
            /**分析用户名和密码是否存在特殊字符*/
            userbo.validateUserNamePwdComma(newokpwd);
            userbo.validateUserNamePwdComma(newPassword);
            //现对密码复杂度进行0低|1中|2强三种模式划分  xuj update 2013-5-29
            userbo.validatePasswordNew(newokpwd);
            if("modifyPassword".equals(accessType)) {
                /**口令加密*/
                Des des=new Des();        
                String userP=userView.getPassWord();
                if(ConstantParamter.isEncPwd(this.conn)){
                    userP=des.DecryPwdStr(userP.replaceAll("''", "'"));
                }else{
                    userP=userP.replaceAll("''", "'");
                }
                if(Sql_switcher.searchDbServer()==1){
                    if(!oldpwd.equalsIgnoreCase(userP)){
                        errorFlag = "oldPwdError";
                        return errorFlag;
                    }
                }else{
                    if(!oldpwd.equals(userP)){
                        errorFlag = "oldPwdError";
                        return errorFlag;
                    }
                }
            }else {
                /**口令加密*/
                Des des=new Des();  
                String _newpwd = "";
                String userP=userView.getPassWord();
                if(ConstantParamter.isEncPwd(this.conn))
                {
                    _newpwd=des.DecryPwdStr(userP.replaceAll("''", "'"));
                }else{
                    _newpwd=userP.replaceAll("''", "'");
                }
                if(Sql_switcher.searchDbServer()==1){
                    if(_newpwd.equalsIgnoreCase(newPassword)){   
                        errorFlag = "sameAsLast";
                        return errorFlag;
                    }
                }else{
                    if(_newpwd.equals(newPassword)){
                        errorFlag = "sameAsLast";
                        return errorFlag;
                    }
                }
            }
            if(Sql_switcher.searchDbServer()==1){
                if(!newPassword.equalsIgnoreCase(newokpwd)){
                    errorFlag = "passwordDifferent";
                    return errorFlag;
                }
            }else{
                if(!newPassword.equals(newokpwd)){
                    errorFlag = "passwordDifferent";
                    return errorFlag;
                }
            }
            if(userbo.checkHistoryPwd(newPassword, this.userView.getUserName())){
                String historyIndex = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_HISTORY_PWD);
                errorFlag = "sameAsHistoryPwd"+historyIndex;
                return errorFlag;
            }
            /**口令加密*/
            if(ConstantParamter.isEncPwd(this.conn)){
                Des des0=new Des(); 
                newPassword=des0.EncryPwdStr(newPassword);            
            }
            userView.setPassWord(newPassword.replaceAll("'", "''"));
            String tablename=null;
            /**平台用户*/
            if(userView.getStatus()==0){
                tablename="operuser";
                RecordVo vo=new RecordVo(tablename);
                vo.setString("password",newPassword);
                vo.setString("username",userView.getUserId());
                vo.setDate("modtime",DateStyle.getSystemTime());                
                ContentDAO dao=new ContentDAO(this.conn);
                dao.updateValueObject(vo);
            }
            /**自助用户*/
            if(userView.getStatus()==4){
                /**登录参数表,登录用户指定不是username or userpassword*/
                String username=null;
                String password=null;
                RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
                if(login_vo==null){
                    username="username";
                    password="userpassword";
                }else{
                    String login_name = login_vo.getString("str_value").toLowerCase();
                    int idx=login_name.indexOf(",");
                    if(idx==-1){
                        username="username";
                        password="userpassword";
                    }else{
                        username=login_name.substring(0,idx);
                        if("#".equals(username)|| "".equals(username))
                            username="username";
                        password=login_name.substring(idx+1);  
                        if("#".equals(password)|| "".equals(password))
                            password="userpassword";
                    }
                }
                String dbpre=userView.getDbname();
                tablename=dbpre+"A01";
                RecordVo vo=new RecordVo(tablename);
                vo.setString(password,newPassword);
                vo.setString("a0100",userView.getUserId());
                ContentDAO dao=new ContentDAO(this.conn);
                dao.updateValueObject(vo);
            }
            //处理历史密码、首次密码修改 xuj add 2013-10-9
            if(ConstantParamter.isEncPwd(this.conn)){
                Des des0=new Des(); 
                newPassword=des0.DecryPwdStr(newPassword);            
            }
            userbo.doHistoryPwd(newPassword,this.userView.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorFlag; 
    }
    /**
     * 检查userview
     * 当设置了登陆验证类时，调用验证类获取要修改的用户的userview，因为验证类登陆的时候可能做处理了
     * @return
     */
     private UserView checkUserView(){
         UserView uv = this.userView;
         try{
             String logonClassFunc = SystemConfig.getPropertyValue("logonclass_func");
         if(logonClassFunc!=null && logonClassFunc.length()>0 && this.userView.getStatus()==0){
             Class funcClass = Class.forName(logonClassFunc);
             Object func = funcClass.newInstance();
             Method getChangePwdUsername = funcClass.getMethod("getChangePwdUsername",String.class);
             String username = (String)getChangePwdUsername.invoke(func,uv.getUserName());
                 if(username.equals(uv.getUserName()))
                     return uv;
                 UserView newuv = new UserView(username,this.conn);
             if(newuv.canLogin())
                    uv = newuv;
         }
         }catch(Exception e){
             e.printStackTrace();
         }
         return uv;
     }
     /**
      * 根据serviceId获取对应服务的配置信息
      * @param serviceId
     * @return 
      */
     public List<Map<String, String>> getServiceParams(String serviceId) {
    	 String sql = "select params from t_sys_serviceclient_service where serviceid=?";
    	 String [] paramsArray = new String[] {serviceId};
    	 ContentDAO dao = new ContentDAO(this.conn);
         ResultSet rs = null;
         StringReader reader = null;
         Document document = null;
         List<Map<String,String>> infoList = new ArrayList<Map<String,String>>();
         try {
        	 rs = dao.search(sql, Arrays.asList(paramsArray));
        	 String params = null;
        	 if(rs.next()) {
        		 params = rs.getString("params"); 
        	 }
             /*** liujx 取出xml配置并解析*/
             document = PubFunc.generateDom(params);
			 String xpath = "/params/items/item";
			 getFieldInfo(xpath,document,infoList);
			 
         } catch (Exception e) {
        	 e.printStackTrace();
         } finally {
        	 PubFunc.closeResource(rs);
         }
         return infoList;
     }
     /**
      * 获取填写字段的指标信息
      */
     public void getFieldInfo(String xpath,Document document,List<Map<String,String>> infoList){
    	 XPath paramPath = null;
    	 Element element = null;
    	 try {
    		 paramPath = XPath.newInstance(xpath);
    		 List itemlist = paramPath.selectNodes(document);
    		 if(itemlist.size() == 0) { //没有配置录入字段项
    			 return;
    		 }
        	 Iterator iterator = itemlist.iterator();
        	 while (iterator.hasNext()) {
        		 element = (Element) iterator.next();
        		 Map<String,String>infoMap = new HashMap<String,String>();
        		 infoMap.put("setId", element.getAttributeValue("setId"));
        		 infoMap.put("itemId", element.getAttributeValue("itemId"));
        	 	 infoMap.put("isWrite", element.getAttributeValue("isWrite"));
        	 	 infoList.add(infoMap);
        	 }
        	 //根据子集进行分组
        	 List<String> sortList = new ArrayList<String>();
        	 Map<String,List<String>> setNameFieldListMap = new HashMap<String,List<String>>();
        	 for(Map<String,String> infoMap : infoList) {
        		 List<String> fieldItemIdList = null;
        		 String setId = infoMap.get("setId");
        		 String itemId = infoMap.get("itemId");
        		 if(setNameFieldListMap.get(setId)!=null) {
        			 fieldItemIdList = setNameFieldListMap.get(setId);
        		 }else {
        			 fieldItemIdList = new ArrayList<String>();
        			 sortList.add(setId);
        		 }
        		 fieldItemIdList.add(itemId);
        		 setNameFieldListMap.put(setId, fieldItemIdList);
        	 }//分组完成
        	 List<Map<String,String>> singleSetInfo = new ArrayList<Map<String,String>>();
        	 Map<String,String> infoMap = null;
        	 for(int i = 0; i < sortList.size(); i++) {
        		 String setName = sortList.get(i);
        		 List<String> fieldItemIdList =  setNameFieldListMap.get(setName);
        		 String sql = constructorSql(setName,fieldItemIdList);//创建某个子集的sql
        		 singleSetInfo = createFieldDataBySql(sql,fieldItemIdList,setName);//查询出来某一个子集的所有数据
        		 for(int k = 0; k < infoList.size(); k++) {
        			 infoMap = infoList.get(k);
    				 String setId = infoMap.get("setId");
    				 if(setId.equals(setName)) {
    					 for(int j = 0; j < singleSetInfo.size(); j++) {
    						 Map<String,String> item = singleSetInfo.get(j);
    						 if(item.get("itemId").equals(infoMap.get("itemId"))) {
    							 String itemType = item.get("itemType");
    							 String itemDesc = item.get("itemDesc");
    							 String codeSetId = item.get("codeSetId");
    							 String codeItemId = item.get("codeItemId");
    							 String fieldValue = item.get("fieldValue");
    							 infoMap.put("itemType", itemType);
    							 infoMap.put("itemDesc", itemDesc);
    							 infoMap.put("codeSetId", codeSetId);
    							 infoMap.put("codeItemId", codeItemId);
    							 infoMap.put("fieldValue", fieldValue);
    							 if(!"A01".equalsIgnoreCase(setId)) {
    								 String i9999 = item.get("i9999");
    								 infoMap.put("i9999", i9999);
    							 }
    							 break;
    						 }
    					 }
    				 }
    			 }
        	 }
		 } catch (JDOMException e) {
			 e.printStackTrace();
		 }
     }
     /**
      * 依次执行sql
      * @param sql
      */
    private List<Map<String,String>> createFieldDataBySql(String sql,List<String> fieldItemIdList,String setName) {
    	List<Map<String,String>> list = new ArrayList<Map<String,String>>();
    	ContentDAO dao = new ContentDAO(this.conn);
        ResultSet rs = null;
		try {
			rs = dao.search(sql);
			String fieldItemId = null;
			FieldItem fieldItem = null;
			Map<String,String> map = null;
			if(!rs.next()) {
				if(!StringUtils.equalsIgnoreCase("A01", setName)) {
					for(int i = 0; i < fieldItemIdList.size(); i++) {
						fieldItemId = fieldItemIdList.get(i);
						map = getFieldInfoMap(fieldItemId);
						map.put("i9999", "-1");
						map.put("fieldValue", "");
		        		list.add(map);
					}
					return list;
        		}
			} else {
				for(int i = 0; i < fieldItemIdList.size(); i++) {
					boolean flag = true;
					fieldItemId = fieldItemIdList.get(i); 
					map = getFieldInfoMap(fieldItemId);
					fieldItem = DataDictionary.getFieldItem(fieldItemId);
					String itemType = fieldItem.getItemtype();
					String codeSetId = fieldItem.getCodesetid();
					if("A".equals(itemType)) {
						if(!"0".equals(codeSetId)) {
							flag = false;
							map.put("codeItemId", rs.getString(fieldItemId));
							map.put("fieldValue", AdminCode.getCodeName(codeSetId, rs.getString(fieldItemId)));
						}
					}else if("D".equals(itemType)) {
						flag = false;
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						Date date = null;
						String fieldValue = "";
						if(Constant.ORACEL == Sql_switcher.searchDbServer()) {
							date = rs.getDate(fieldItemId);
							if(date != null) {
								fieldValue = sdf.format(date);
							}
						}else if(Constant.MSSQL == Sql_switcher.searchDbServer()) {
							fieldValue = rs.getString(fieldItemId);
							if(StringUtils.isNotBlank(fieldValue)) {
								fieldValue = sdf.format(sdf.parse(fieldValue));
							}
						}
						map.put("fieldValue", fieldValue);
					}
					if(flag)
						map.put("fieldValue", rs.getString(fieldItemId));
	        		if(!StringUtils.equalsIgnoreCase("A01", setName)) {
	        			String i9999 = rs.getString("i9999");
	        			map.put("i9999", StringUtils.isBlank(i9999)? "-1" : i9999);
	        		}
	        		list.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return list;
	}
    /**
     * 获取指标的基本信息
     * @param fieldItemId
     * @return
     */
	private Map<String,String> getFieldInfoMap(String fieldItemId) {
		Map<String,String> map = new HashMap<String,String>(); 
		FieldItem fieldItem = DataDictionary.getFieldItem(fieldItemId);
		map.put("itemId", fieldItemId);
		map.put("itemType", fieldItem.getItemtype());
		map.put("itemDesc", fieldItem.getItemdesc());
		map.put("codeSetId", fieldItem.getCodesetid());
		return map;
	}

	/**
     * 生成各个子集的查询sql
     * @param setName
     * @param fieldItemIdList
     */
	private String constructorSql(String setName, List<String> fieldItemIdList) {
		String dbName = this.userView.getDbname();
		String tableName = dbName+setName;
		String a0100 = this.userView.getA0100();
		String sql;
		if(StringUtils.equalsIgnoreCase("A01", setName)) {
			sql = constructorCommonSql(fieldItemIdList,false);
			sql = constructorA01Sql(tableName,a0100,sql);
		}else {
			sql = constructorOtherSetSql(tableName,fieldItemIdList);
		}
		return sql;
	}
	/**
	 * 生成主集sql
	 * @param tableName
	 * @param a0100
	 * @param sql 
	 * @return
	 */
	private String constructorA01Sql(String tableName, String a0100, String sql) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(sql);
		sqlBuffer.append(" from ").append(tableName);
		sqlBuffer.append(" where a0100='").append(a0100).append("'");
		return sqlBuffer.toString();
	}
	/**
	 * 生成子集sql
	 * @param tableName 子集表名
	 * @param fieldItemIdList 子集中被选择的字段
	 * @return
	 */
	private String constructorOtherSetSql( String tableName, List<String> fieldItemIdList) {
		StringBuffer sqlBuffer = new StringBuffer();
		if(Constant.MSSQL == Sql_switcher.searchDbServer()) {
			String commonSql = constructorCommonSql(fieldItemIdList,true);
			sqlBuffer.append(commonSql).append(",i9999");
			sqlBuffer.append(" from ").append(tableName).append(" where CreateUserName='").append(this.userView.getUserName()).append("'");
			sqlBuffer.append(" and a0100='").append(this.userView.getA0100()).append("' and (");
			sqlBuffer.append(Sql_switcher.diffDays(Sql_switcher.today(),Sql_switcher.charToDate(Sql_switcher.dateToChar("createtime"))));
			sqlBuffer.append(")=0 order by i9999 desc");
		}else if(Constant.ORACEL == Sql_switcher.searchDbServer()) {
			String commonSql = constructorCommonSql(fieldItemIdList,false);
			sqlBuffer.append(commonSql).append(",i9999");
			sqlBuffer.append(" from (");
			sqlBuffer.append(commonSql).append(",i9999").append(" from ");
			sqlBuffer.append(tableName).append(" where CreateUserName='").append(this.userView.getUserName()).append("'");
			sqlBuffer.append(" and a0100='").append(this.userView.getA0100()).append("' and (");
			sqlBuffer.append(Sql_switcher.diffDays(Sql_switcher.today(),Sql_switcher.charToDate(Sql_switcher.dateToChar("createtime"))));
			sqlBuffer.append(")=0 order by i9999 desc) where rownum <= 1");
		}
		return sqlBuffer.toString();
	}

	/**
	 * 生成通用sql
	 * @param fieldItemIdList
	 * @param isAddTop
	 */
	private String constructorCommonSql(List<String> fieldItemIdList,boolean isAddTop) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select ");
		if(isAddTop) {
			sqlBuffer.append("TOP 1 ");
		}
		for(int i=0;i<fieldItemIdList.size();i++) {
			String itemId = fieldItemIdList.get(i);
			if(i==fieldItemIdList.size()-1) {
				sqlBuffer.append(itemId);
			}else {
				sqlBuffer.append(itemId).append(",");
			}
		}
		return sqlBuffer.toString();
	}

	/**
	 * 校验服务关联的登记表或业务模板是否还存在
	 * @author wangbs
	 * @param templateId 登记表或业务模板id
	 * @param templateType 1：业务模板  2：登记表
	 * @return boolean
	 * @date 2020/6/5
	 */
    public boolean checkTabExist(String templateId, String templateType) {
        boolean tabExistFlag = false;
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try{
            String tabName = "template_table";
            if ("2".equals(templateType)) {
                tabName = "rname";
            }
            String sql = "select name from " + tabName + " where tabid='" + templateId + "'";
            rs = dao.search(sql);
            if (rs.next()) {
                String templateName = rs.getString(1);
                if(StringUtils.isNotBlank(templateName)){
                    tabExistFlag = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return tabExistFlag;
    }
}
