package com.hjsj.hrms.module.recruitment.util;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 招聘模块公共方法类
 * <p>Title: RecruitUtilsBo </p>
 * <p>Description: 招聘模块公共方法类</p>
 * <p>Company: hjsj</p>
 * <p>create time  2017年10月23日 上午11:36:37</p>
 * @author gaozy
 * @version 1.0
 */
public class RecruitUtilsBo {
	
	private ContentDAO dao;
	private Connection conn;
	public RecruitUtilsBo() {
		
	}
	public RecruitUtilsBo(Connection conn) {
		this.conn = conn;
		this.dao = new ContentDAO(conn);
	}
	
	/**
	 * 获取招聘注册截止时间
	 * @return
	 */
	public static String getRegisterEndTime() {
		String register_endtime = "";
		Connection connection = null;
		try {
			connection = AdminDb.getConnection();
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(connection);
			HashMap map=parameterXMLBo.getAttributeValues();
            if(map!=null&&map.get("register_endtime")!=null)
            	 register_endtime=(String)map.get("register_endtime");
            
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(connection);
		}
		return register_endtime;
	}
	
	//证件类型默认值
	public static String getIdTypeValue() {
		String idTypeValue = "";
		Connection connection = null;
		try {
			connection = AdminDb.getConnection();
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(connection);
			HashMap map=parameterXMLBo.getAttributeValues();
			//证件类型
			String id_type=(String)map.get("id_type");
			if(StringUtils.isNotEmpty(id_type)&&!"#".equals(id_type)) {
				FieldItem fieldItem = DataDictionary.getFieldItem(id_type,"A01");
				if(fieldItem!=null&&"1".equals(fieldItem.getUseflag())) {
					CodeItem code = AdminCode.getCode(fieldItem.getCodesetid(), "01");
					if(code!=null)
						idTypeValue = "01";
					if(StringUtils.isEmpty(idTypeValue)) {
						code = AdminCode.getCode(fieldItem.getCodesetid(), "1");
						if(code!=null)
							idTypeValue = "1";
					}
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(connection);
		}
		return idTypeValue;
	}
	
    /**
     * 根据身份证号获取出生日期
     * @Title: getBirthDay   
     * @Description:    
     * @param @param cardid 身份证号
     * @param @return 
     * @return String    
     * @throws
     */
    public String getBirthDay(String cardid) {
        if (!PubFunc.idCardValidate(cardid))
            return "";
        
        if (cardid != null && cardid.length() == 18) {
            return cardid.substring(6, 10) + "-" + cardid.substring(10, 12) + "-" + cardid.substring(12, 14);
        } else if (cardid != null && cardid.length() == 15) {
            return "19" + cardid.substring(6, 8) + "-" + cardid.substring(8, 10) + "-" + cardid.substring(10, 12);
        }
        
        return "";
    }
    
    /**
     * 根据身份证号获取年龄
     * @param cardid 身份证号    
     */
    public String getAge(String cardid) {        
        if (!PubFunc.idCardValidate(cardid))
            return "";
        
        if (cardid.length() == 18) {
            return String.valueOf(GetHisAge(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), Integer.parseInt(cardid.substring(6, 10)), Integer.parseInt(cardid.substring(10, 12)), Integer.parseInt(cardid.substring(12, 14))));
        } else if (cardid.length() == 15) {
            return String.valueOf(GetHisAge(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), Integer.parseInt("19" + cardid.substring(6, 8)), Integer.parseInt(cardid.substring(8, 10)), Integer.parseInt(cardid.substring(10, 12))));
        }
        
        return "0";      
    }
    
    /**
     * 根据身份证号获取性别
     * @param cardid 身份证号    
     */
    public String getSex(String cardid) {
        if (!PubFunc.idCardValidate(cardid))
            return "";
        
        if (cardid.length() == 18) {
            return String.valueOf((Integer.parseInt(cardid.substring(14, 17)) % 2) == 0 ? 2 : 1);
        } else if (cardid.length() == 15) {
            return String.valueOf((Integer.parseInt(cardid.substring(12, 15)) % 2) == 0 ? 2 : 1);
        } 
            
       return "";
    }
    
    /*
     * 根据出生日期获取年龄
     * */
    public int GetHisAge(int ncYear,int ncMonth,int ncDay,int nYear,int nMonth,int nDay)
    {
        /*
         * 根据日期获得年龄的运算
         * */
        int nAage,nMM,nDD,Result;
        nAage=ncYear-nYear;                              
        nMM=ncMonth-nMonth+1;
        nDD=ncDay-nDay;
        if(nMM>0)
        {
            Result=nAage;
        }
        else if(nMM<0)
        {
            Result=nAage-1;
            if(Result <0)
            {
                Result=0;
            }
        }
        else
        {
            if(nDD>=0)
            {
                Result=nAage;
            }
            else
            {
                Result=nAage-1;
                if(Result<0)
                  Result=0;
            }
        }
        return Result;
    }
    
     /**
      * 从数据字典中获取FieldItem，并判断对应的数据表是否存在
     * @param nbase
     * @param tableName
     * @param itemId
     * @return
     */
    public FieldItem getFieldItem(String nbase, String tableName, String itemId) {
    	 DbWizard dbWizard = new DbWizard(this.conn);
    	 String realTable = tableName;
    	 if(StringUtils.isEmpty(nbase))
    		 nbase = "";
    	 if(tableName.toUpperCase().startsWith("A") && tableName.length()==3)
    		 realTable = nbase + tableName;
    	 FieldItem fieldItem = null;
    	 // 判断表是否存在
         if (dbWizard.isExistTable(realTable, false)) 
        	 fieldItem = DataDictionary.getFieldItem(itemId, tableName);
         if(fieldItem!=null && "1".equals(fieldItem.getUseflag()))
        	 return fieldItem;
         else
        	 return null;
     }
    
    //获取代码类，并按照层级排序
    public ArrayList<CodeItem> getCodeItemMap(String codeSetid, String invalid) {
    	RowSet rs = null;
    	ArrayList<CodeItem> sortCodeItem = new ArrayList<CodeItem>();
    	ArrayList<CodeItem> codeItemList = new ArrayList();
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String backdate = sdf.format(new Date());
        	ArrayList value = new ArrayList();
        	value.add(codeSetid);
            StringBuffer sql = new StringBuffer("select 1 from codeitem where layer is null and codesetid = ?");
            rs = dao.search(sql.toString(), value);
            if(rs.next()) //如果代码类里有layer为null的，重置代码层级
                this.updateLayer(codeSetid);
            sql.setLength(0);
            sql.append("select codeitemid,codeitemdesc,parentid,childid,layer,a0000 ");
        	sql.append(" from codeitem");
        	sql.append(" where codesetid=?");
        	if("1".equals(invalid))
        		sql.append(" and invalid=1");
        	sql.append(" and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date");
        	sql.append(" order by layer,a0000");
        	rs = dao.search(sql.toString(), value);
        	
        	while(rs.next()) {
        		CodeItem item = new CodeItem();
				item.setCodeid(codeSetid);
				item.setCodeitem(rs.getString("CodeItemId"));
				item.setCodename(rs.getString("CodeItemDesc"));
				item.setPcodeitem(rs.getString("parentid"));
				item.setCcodeitem(rs.getString("childid"));
				item.setCodelevel(rs.getString("layer"));
				codeItemList.add(item);
        	}
        	
        	sortCodeItem(sortCodeItem, codeItemList, "");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
		return sortCodeItem;
	}
    
    /**
     * 重置代码层级
      * @param codesetid
      */
     public void updateLayer(String codeSetid) {
            try{
                String sql = " update codeitem set layer=null where codesetid='"+codeSetid+"'";
                 dao.update(sql);
                 sql = " update codeitem set layer = 1 where codesetid='"+codeSetid+"' and parentid=codeitemid";
                 dao.update(sql);
                 sql = " update codeitem set layer=(select layer from codeitem c1 where c1.codesetid='"+codeSetid+"' and c1.codeitemid=codeitem.parentid)+1 where codesetid='"+codeSetid+"' and layer is null ";
                 int i=1;
                 while(i>0){
                     i = dao.update(sql);
                 }
            }catch(Exception e){
                e.printStackTrace();
            }
       }
   //按照层级排序
     private void sortCodeItem(ArrayList<CodeItem> maps, ArrayList<CodeItem> codeItemList, String parentId) {
    	 for(int i = 0;i<codeItemList.size();i++) {
    		 CodeItem codeItem = codeItemList.get(i);
    		 String pId = codeItem.getPcodeitem();
    		 String cId = codeItem.getCcodeitem();
    		 String itemId = codeItem.getCodeitem();
    		 //当parentId为空的时候只加载第一级节点;当parentId不为空的时候只加载父节点是parentId的节点（不包括第一级节点）
    		 if((StringUtils.isEmpty(parentId) && pId.equalsIgnoreCase(itemId)) 
    				 || (StringUtils.isNotEmpty(parentId) && pId.equalsIgnoreCase(parentId)
    				 && !pId.equalsIgnoreCase(itemId))) {
    			 maps.add(codeItem);
    			 //加载子节点
    			 if(!cId.equalsIgnoreCase(itemId))
    				 sortCodeItem(maps, codeItemList, itemId);
    				 
    		 }
    	 }
     }
     
     /**
 	 * 生成功能导航菜单的json串
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
 				if(bean.get("fntype")!=null&&bean.get("fntype").toString().length()>0)
 					str.append(",fntype:'"+bean.get("fntype")+"'");
 				if(bean.get("cusMenu")!=null&&bean.get("cusMenu").toString().length()>0)
 					str.append(",cusMenu:'"+bean.get("cusMenu")+"'");
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
	 * @param cusMenu 菜单添加监听
	 * @param fntype 内置功能性按钮
	 * @return
	 */
	public static LazyDynaBean getMenuBean(String text,String handler,String icon,String cusMenu,String fntype,ArrayList list){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(text!=null&&text.length()>0)
				bean.set("text", text);
			if(icon!=null&&icon.length()>0)
				bean.set("icon", icon);
			if(cusMenu!=null&&cusMenu.length()>0)
				bean.set("cusMenu", cusMenu);
			if(fntype!=null&&fntype.length()>0)
				bean.set("fntype", fntype);
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
	
	//根据字段长度获取对应的日期格式
	public String getDateFormat(String itemId){
		FieldItem dateItem = DataDictionary.getFieldItem(itemId);
		int itemLength =dateItem.getItemlength();
		String dateFormat ="yyyy-MM-dd";
		if(itemLength ==4){
			dateFormat ="yyyy";
		}else if(itemLength ==7){
			dateFormat ="yyyy-MM";
		}else if(itemLength ==10){
			dateFormat ="yyyy-MM-dd";
		}else if(itemLength ==16){
			dateFormat ="yyyy-MM-dd HH:mm";
		}else if(itemLength >=18){
			dateFormat ="yyyy-MM-dd HH:mm:ss";
		}
		return dateFormat;
	}
	
	/**获取认证库
	 * @return
	 */
	public String[] getNbase() {
		RowSet rs = null;
		String strpres = "";
		try {
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
			if (login_vo != null) {
				strpres = login_vo.getString("str_value");
				String[] split = strpres.split(",");
				StringBuffer sql = new StringBuffer("select Pre from DBName ");
				sql.append("where Pre in(");
				for (String string : split) {
					sql.append("'"+string+"',");
				}
				sql.setLength(sql.length()-1);
				sql.append(") order by dbid");
				rs = dao.search(sql.toString());
				strpres = "";
				while (rs.next()) {
					strpres+=(rs.getString("Pre")+",");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		if(StringUtils.isNotEmpty(strpres)){
			strpres = strpres.substring(0, strpres.length()-1);
			return strpres.split(",");					    
		} else
			return new String[0];
	}
           
}

