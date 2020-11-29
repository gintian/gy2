package com.hjsj.hrms.module.template.utils;

import java.util.ArrayList;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
/**
 * <p>Title:TemplateLayoutBo.java</p>
 * <p>Description>:模板布局类，工具栏、表格，及布局的公用函数</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-08-23 上午10:36:32</p>
 * <p>@version: 7.0</p>
 */
@SuppressWarnings("all")
public class TemplateLayoutBo {
	
	public static String getMenuStr(String name,String id,String icon,ArrayList list) {
	    StringBuffer str = new StringBuffer();
	    try{
            if(name.length()>0){
                str.append("<jsfn>{xtype:'button',text:'"+name+"',icon:'"+icon+"'");
            }
            if(StringUtils.isNotBlank(id)){
                str.append(",id:'");
                str.append(id);
                str.append("'");
            }
            str.append(",menu:{items:[");
            for(int i=0;i<list.size();i++){
                LazyDynaBean bean = (LazyDynaBean) list.get(i);
                if(i!=0)
                    str.append(",");
                str.append("{");
                if(bean.get("xtype")!=null&&bean.get("xtype").toString().length()>0)
                    str.append("xtype:'"+bean.get("xtype")+"'");
                if(bean.get("text")!=null&&bean.get("text").toString().length()>0)
                    str.append("text:'"+bean.get("text")+"'");
                if(bean.get("handler")!=null&&bean.get("handler").toString().length()>0){
                    if(bean.get("xtype")!=null&&bean.get("xtype").toString().equalsIgnoreCase("datepicker")){//时间控件单独处理一下 方法GzGlobal.aaa(picker, date)这样写
                        str.append(",handler:function(picker, date){"+bean.get("handler")+";},todayTip:''");//todayTip设置空，去掉空格选时间提示
                    }else{
                        str.append(",handler:function(){"+bean.get("handler")+";}");
                    }               
                }
                String menuId = (String)bean.get("id");
                
                if(menuId!=null&&menuId.length()>0)//人事异动-手工选择按钮需要id（gaohy）
                    str.append(",id:'"+menuId+"'");
                else
                    menuId = "";
                if(bean.get("icon")!=null&&bean.get("icon").toString().length()>0)
                    str.append(",icon:'"+bean.get("icon")+"'");
                if(bean.get("value")!=null&&bean.get("value").toString().length()>0)
                    str.append(",value:"+bean.get("value")+"");
                ArrayList menulist = (ArrayList)bean.get("menu");
                if(menulist!=null&&menulist.size()>0){
                    str.append(getMenuStr("",menuId, menulist));
                }
                str.append("}");
            }
            str.append("]}");
            if(name.length()>0){                
                str.append("}</jsfn>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return str.toString();
	}
	
	/**
	 * 递归生成功能导航菜单的json串
	 * @param name 菜单名
	 * @param list 菜单内容
	 * @return
	 */
	public static String getMenuStr(String name,String id,ArrayList list){
		StringBuffer str = new StringBuffer();
		try{
			if(name.length()>0){
				str.append("<jsfn>{xtype:'button',text:'"+name+"'");
			}
			if(StringUtils.isNotBlank(id)){
				str.append(",id:'");
				str.append(id);
				str.append("'");
			}
			str.append(",menu:{items:[");
			for(int i=0;i<list.size();i++){
				LazyDynaBean bean = (LazyDynaBean) list.get(i);
				if(i!=0)
					str.append(",");
				str.append("{");
				if(bean.get("xtype")!=null&&bean.get("xtype").toString().length()>0)
					str.append("xtype:'"+bean.get("xtype")+"'");
				if(bean.get("text")!=null&&bean.get("text").toString().length()>0)
					str.append("text:'"+bean.get("text")+"'");
				if(bean.get("handler")!=null&&bean.get("handler").toString().length()>0){
					if(bean.get("xtype")!=null&& "datepicker".equalsIgnoreCase(bean.get("xtype").toString())){//时间控件单独处理一下 方法GzGlobal.aaa(picker, date)这样写
						str.append(",handler:function(picker, date){"+bean.get("handler")+";},todayTip:''");//todayTip设置空，去掉空格选时间提示
					}else{
						str.append(",handler:function(){"+bean.get("handler")+";}");
					}				
				}
				String menuId = (String)bean.get("id");
				
				if(menuId!=null&&menuId.length()>0)//人事异动-手工选择按钮需要id（gaohy）
					str.append(",id:'"+menuId+"'");
				else
					menuId = "";
				if(bean.get("icon")!=null&&bean.get("icon").toString().length()>0)
					str.append(",icon:'"+bean.get("icon")+"'");
				if(bean.get("value")!=null&&bean.get("value").toString().length()>0)
					str.append(",value:"+bean.get("value")+"");
				ArrayList menulist = (ArrayList)bean.get("menu");
				if(menulist!=null&&menulist.size()>0){
					str.append(getMenuStr("",menuId, menulist));
				}
				str.append("}");
			}
			str.append("]}");
			if(name.length()>0){				
				str.append("}</jsfn>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.toString();
	}
	/**
	 * 生成菜单的bean
	 * @param text 名称
	 * @param handler 触发事件
	 * @param icon 图标
	 * @return
	 */
	public static LazyDynaBean getMenuBean(String text,String handler,String icon,ArrayList list){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(text!=null&&text.length()>0)
				bean.set("text", text);
			if(icon!=null&&icon.length()>0)
				bean.set("icon", icon);
			if(handler!=null&&handler.length()>0){
				if(list!=null&&list.size()>0){
					bean.set("menu", list);
				}else{
					bean.set("handler", handler);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 
	* <p>Description:(人事异动-业务日期） </p>
	* <p>Company: HJSOFT</p> 
	* @author gaohy
	* @date 2015-12-15 下午04:08:55
	 */
	public static LazyDynaBean getDateMenuBean(String handler,String icon,String xtype,String value){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(xtype!=null&&xtype.length()>0)
				bean.set("xtype", xtype);
			if(value!=null&&value.length()>0)
				bean.set("value", value);
			if(handler!=null&&handler.length()>0)
				bean.set("handler", handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
	
	
	/**
	 * 列头ColumnsInfo对象初始化
	 * @param columnId id
	 * @param columnDesc 名称
	 * @param columnDesc 显示列宽
	 * @return
	 */
	public static ColumnsInfo getColumnsInfo(String columnId, String columnDesc,String columnType, int columnWidth){
		
		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setCodesetId("0");// 指标集
		columnsInfo.setColumnType(columnType);// 类型N|M|A|D
		columnsInfo.setColumnWidth(columnWidth);//显示列宽
		columnsInfo.setColumnLength(100);// 显示长度 
		columnsInfo.setDecimalWidth(0);// 小数位
		columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
		columnsInfo.setReadOnly(true);// 是否只读
		columnsInfo.setFromDict(false);// 是否从数据字典里来
		columnsInfo.setLocked(false);//是否锁列
		
		return columnsInfo;
	}

}
