package com.hjsj.hrms.transaction.mobileapp.contacts;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.transaction.mobileapp.utils.HTMLParamUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * <p> Title: ContactsBo </p>
 * <p> Description:通讯录BO类 </p>
 * <p> Company: hjsj </p>
 * <p> create time: 2013-12-17 下午4:49:11 </p>
 * 
 * @author yangj
 * @version 1.0
 */
public class ContactsBo {
	private Connection conn;
	private UserView userView;

	public ContactsBo() {

	}
	
	public ContactsBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}

	/**
	 * 
	 * @Title: prepare   
	 * @Description: 准备工作  (默认选择第一个字段) 
	 * @param  
	 * @return void    
	 * @throws GeneralException
	 */
	public Map prepare() throws GeneralException {
		Map resultMap = new HashMap();
		Map temporaryMap = new HashMap();
		String way;
		String menuid;
		try {//读取数据库中保存的字段数据
			List temporarylist = HTMLParamUtils.getContacts(conn);			
			for (int i = 0; i < temporarylist.size(); i++) {
				temporaryMap = (Map) temporarylist.get(i);
				way = (String) temporaryMap.get("way");
				menuid = (String) temporaryMap.get("menuid");
				if (resultMap.get(way) == null && menuid.length() > 0) {
					resultMap.put(way, menuid);
				}
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		return resultMap;
	}
	
	/**
	 * 
	 * @Title: searchInfoList   
	 * @Description:    
	 * @param unitID 组织机构ID
	 * @param keywords 模糊查询
	 * @param url 网络地址
	 * @param pageIndex 第几页
	 * @param pageSize 每页显示条数
	 * @param mobile 数据库电话字段标示符
	 * @param email 数据库邮箱字段标示符
	 * @return List    
	 * @throws GeneralException
	 */
	public List searchInfoList(String unitID, String keywords, String url, String pageIndex, String pageSize, String mobile, String email) throws GeneralException {
		String sql = this.getSQL(unitID, keywords, pageIndex, pageSize, mobile, email);
		return this.getPersonList(sql, url, mobile, email);
	}

	/**
	 * 
	 * @Title: getPersonList   
	 * @Description:    
	 * @param sql SQL查询语句
	 * @param url 网络地址
	 * @param mobile 数据库电话字段标示符
	 * @param email 数据库邮箱字段标示符
	 * @return List    
	 * @throws GeneralException
	 */
	private List getPersonList(String sql,String url,String mobile,String email) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		RowSet rs = null;
		HashMap map = null;
		try {
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			display_e0122 = display_e0122 == null || display_e0122.length() == 0 ? "0" : display_e0122;
			String seprartor = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
			seprartor = seprartor != null && seprartor.length() > 0 ? seprartor : "/";
			rs = dao.search(sql.toString());
			while (rs.next()) {
				map = new HashMap();
				String a0100 = rs.getString("a0100");
				String dbpre = rs.getString("dbpre");
				map.put("dbpre", dbpre);
				map.put("a0100", a0100);
				map.put("name", rs.getString("a0101"));
				if(mobile != null)
					map.put("phone", rs.getString(mobile));
				if(email != null)
					map.put("email", rs.getString(email));
				String b0110 = rs.getString("b0110");
				String e0122 = rs.getString("e0122");
				String e01a1 = rs.getString("e01a1");
				b0110 = AdminCode.getCodeName("UN", b0110);
				b0110 = b0110 == null ? "" : b0110;
				CodeItem itemid = AdminCode.getCode("UM", e0122, Integer.parseInt(display_e0122));
				if (itemid != null)
					e0122 = itemid.getCodename();
				e0122 = e0122 == null ? "" : e0122;
				e01a1 = AdminCode.getCodeName("@K", e01a1);
				map.put("pos", e01a1);
				e01a1 = e01a1 == null ? "" : e01a1;
				map.put("org", b0110 + (b0110.length() > 0 && e0122.length() > 0 ? seprartor : "") + e0122);
				StringBuffer photourl = new StringBuffer();
				String filename = ServletUtilities.createPhotoFile(dbpre + "A00", rs.getString("a0100"), "P", null);
				if (!"".equals(filename)) {
					photourl.append(url);
					photourl.append("/servlet/DisplayOleContent?mobile=1&filename=");
					photourl.append(filename);
				} else {
					photourl.append(url);
					photourl.append("/images/photo.jpg");
				}
				map.put("photo", photourl.toString());
				list.add(map);
			}
		} catch (SQLException e) {
			throw GeneralExceptionHandler.Handle(e);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return list;
	}

	/**
	 * 
	 * @Title: getSQL   
	 * @Description:    
	 * @param unitID 组织机构ID
	 * @param keywords 模糊查询
	 * @param pageIndex 第几页
	 * @param pageSize 每页显示条数
	 * @param mobile 数据库电话字段标示符
	 * @param email 数据库邮箱字段标示符
	 * @return String    
	 * @throws GeneralException
	 */
	private String getSQL(String unitID,String keywords,String pageIndex,String pageSize,String mobile,String email) throws GeneralException {
		int index = Integer.parseInt(pageIndex);
		int size = Integer.parseInt(pageSize);
		StringBuffer resultSql = new StringBuffer();
		try {			
			List list = this.getNbaseList();			
			String dbpre;
			StringBuffer unitIDSql = new StringBuffer();
			//组织机构树判断
			if (unitID.length() > 0){
				//um是e0122单位,un是b0100部门
				String[] temporary  = unitID.split("`");
				String codesetid = temporary[0];
				String id = temporary[1];
				if("UN".equals(codesetid)||"un".equals(codesetid))
					unitIDSql.append(" and b0110 like '" + id + "%'");	
				else if("UM".equals(codesetid)||"um".equals(codesetid))
					unitIDSql.append(" and e0122 like '" + id + "%'");
			}
			//快速查询判断
			StringBuffer keywordsSql = new StringBuffer();
			if (keywords.length() > 0)
				keywordsSql.append(this.getKeywordsWhereStr(keywords));
			for (int i = 0, length = list.size(); i < length; i++) {
				dbpre = (String) list.get(i);
				resultSql.append(" union all ");
				resultSql.append("select distinct " + dbpre + "a01.a0100,'" + (i + 1) + "' ord,'" + dbpre + "' dbpre," + dbpre
						+ "a01.b0110," + dbpre + "a01.e01a1," + dbpre + "a01.e0122,a0101,a0000");
				if(mobile!=null)
					resultSql.append(","+mobile);
				if(email!=null)
					resultSql.append(","+email);
				resultSql.append(" from " + dbpre + "A01");
				resultSql.append(" where 1=1");
				resultSql.append(unitIDSql);
				resultSql.append(keywordsSql);
			}
			dbpre = resultSql.toString().substring(11);
			resultSql.setLength(0);
			resultSql.append("select * from (select ROW_NUMBER() over(ORDER BY ord, A0000) numberCode, A.* from (");
			resultSql.append(dbpre + ") A");
			resultSql.append(") T where numberCode between " + ((index - 1) * size + 1) + " and " + (size * index));	
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return resultSql.toString();
	}

	/** 
	 * 
	 * @Title: getWhereStrByBaseInfo
	 * @Description: 获得基本信息（姓名，拼音简码，工号）条件语句
	 * @param keywords 模糊查询
	 * @return String
	 * @throws GeneralException
	 */
	private String getKeywordsWhereStr(String keywords) throws GeneralException {
		StringBuffer where = new StringBuffer();
		try {
			String keyword[] = keywords.split("\n");
			where.append(" and (  ");// 姓名
			for (int i = 0; i < keyword.length; i++) {
				if ("".equals(keyword[i].trim()))
					continue;
				if (i == 0) {
					where.append(" a0101 like '%" + keyword[i] + "%' ");
				} else {
					where.append(" or a0101 like '%" + keyword[i] + "%' ");
				}
			}
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			FieldItem item = DataDictionary.getFieldItem(onlyname);
			if (item != null && !"a0101".equalsIgnoreCase(onlyname) && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
				for (int i = 0; i < keyword.length; i++) {
					if ("".equals(keyword[i].trim()))
						continue;
					where.append(" or " + onlyname + " like '%" + keyword[i] + "%' ");
				}
	
			}
			String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
			item = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
			if (!(pinyin_field == null || "".equals(pinyin_field) || "#".equals(pinyin_field) || item == null
					|| "0".equals(item.getUseflag())) && !"a0101".equalsIgnoreCase(pinyin_field)
					&& !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
				for (int i = 0; i < keyword.length; i++) {
					if ("".equals(keyword[i].trim()))
						continue;
					where.append(" or " + pinyin_field + " like '%" + keyword[i] + "%' ");
				}
			}
			where.append(")");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return where.toString();
	}

	/** 
	 * 
	 * @Title: getNbaseList
	 * @Description:获得数据库中的人员库
	 * @param @return
	 * @return ArrayList
	 * @throws GeneralException
	 */
	private List getNbaseList() throws GeneralException {
		List list = new ArrayList();
		RowSet rs = null;
		try {
			/**登录参数表*/
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
			if (login_vo != null) {
				String[] loginList = login_vo.getString("str_value").split(",");
				for (int i = 0; i < loginList.length; i++) {
					// 排除保存的空库
					if (loginList[i].length() >= 1)
						list.add(loginList[i]);
				}
			}
/**
 * 		StringBuffer sBuffer = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
			// 使应聘人才库中设置的人员库在通讯录中不显示
			RecordVo strvalue = ConstantParamter.getConstantVo("ZP_DBNAME");
			sBuffer.append("select pre from dbname");
			if (strvalue != null)
				sBuffer.append(" where pre <> '" + strvalue.getString("str_value") + "'");
			sBuffer.append(" order by DbId");
			rs = dao.search(sBuffer.toString());
			sBuffer.setLength(0);
			while (rs.next()) {
				list.add(rs.getString("pre"));
			}
*/
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		return list;
	}
	
	/**
	 * 
	 * @Title: getContactsList   
	 * @Description:  根据人员库和人员编号组成的集合（每个形式为：nbase`a0100）批量查询联系方式  
	 * @param  selectedContactList 根据该集合查询人员联系方式
	 * @param  contantsOrderList 通讯录参数配置的顺序记录集合
	 * @param photourl 需要返回照片时传人访问网络地址，不需要则指定null或者空字符串"" 
	 * @param @throws GeneralException 
	 * @return List    list[map<联系信息类型标识（mobile,email..）,对应联系信息具体值（1346568794【多值用`分隔】）>]
	 * @throws
	 */
    public List getContactsList(ArrayList selectedContactList,ArrayList contactsOrderList,String photourl) throws GeneralException {
        List savedContactsList = new ArrayList();
        //根据前台选择人员，获得每个人员库有哪些人员编号        
        HashMap nbaseA0100Map = getNbaseA0100Map(selectedContactList);
        if(!nbaseA0100Map.isEmpty()){
          HashMap contantsFiledMap = new HashMap();//用于存放每条联系人信息对应的指标字段如：<phone,A013C>
          String sql = getSelectedContactSql(nbaseA0100Map,contantsFiledMap,contactsOrderList); //获得选择人员联系方式的组合sql语句
          ContentDAO dao = new ContentDAO(conn);
          RowSet rowset = null;
          try {
              rowset = dao.search(sql);
              while (rowset.next()) {
                  Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
                  String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
                  display_e0122 = display_e0122 == null || display_e0122.length() == 0 ? "0" : display_e0122;
                  String seprartor = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
                  seprartor = seprartor != null && seprartor.length() > 0 ? seprartor : "/";
                  String unit = rowset.getString("b0110");//单位
                  String e0122 = rowset.getString("e0122");//部门
                  String e01a1 = rowset.getString("e01a1");//岗位
                  HashMap contactInfo = new HashMap();
                  savedContactsList.add(contactInfo);//添加信息
                  //添加联系人每个联系方式
                  contactInfo.put("name", rowset.getString("a0101"));
                  unit = AdminCode.getCodeName("UN", unit);
                  unit = unit == null ? "" : unit;
                  contactInfo.put("unit", unit);
                  contactInfo.put("nbaseA0100", rowset.getString("nbaseA0100"));//记录人员库中的人员编号 ，用于前台锁定照片
                  CodeItem itemid = AdminCode.getCode("UM", e0122, Integer.parseInt(display_e0122));
                  if (itemid != null){
                      e0122 = itemid.getCodename();
                  }
                  e0122 = e0122 == null ? "" : e0122;
                  e01a1 = AdminCode.getCodeName("@K", e01a1);
                  contactInfo.put("pos_name", e01a1);
                  e01a1 = e01a1 == null ? "" : e01a1;
                  contactInfo.put("unit_dept_name", unit + (unit.length() > 0 && e0122.length() > 0 ? seprartor : "") + e0122);
                  
                  if (photourl!=null&&!"".equals(photourl.trim())) {
                	  String filename = ServletUtilities.createPhotoFile(rowset.getString("nbase")+ "A00", rowset.getString("a0100"), "P", null);
                      photourl+="/servlet/DisplayOleContent?mobile=1&filename=";
                      photourl+=filename;
                  } else {
                      photourl+="/images/photo.jpg";
                  }
                  contactInfo.put("photo", photourl.toString());
                  //遍历联系人信息对应的指标字段
                  Iterator it = contantsFiledMap.entrySet().iterator();
                  while (it.hasNext()) {
                      Map.Entry entry = (Entry) it.next();
                      String contantType =(String)entry.getKey();
                      String contantField =(String)entry.getValue();
                      String[] fields = contantField.split("`");//多指标字段用`分隔
                      for(int i = 0;i<fields.length;i++){
                          if(null!=fields[i]&&!"".equals(fields[i].trim())){
                             String value = rowset.getString(fields[i]);
                             if(value==null|| "".endsWith(value.trim())){
                                 continue;
                             }
                            if(contactInfo.containsKey(contantType)){
                                String values = (String)contactInfo.get(contantType);
                                contactInfo.put(contantType,values+"`"+value);//前台多个信息也用`分隔
                            }else{
                                contactInfo.put(contantType,value);
                            }
                          }else{
                              continue;
                          }
                      }
                  }
              }
          } catch (Exception e) {
              e.printStackTrace();
              throw GeneralExceptionHandler.Handle(e);
          }finally {
			  PubFunc.closeResource(rowset);
          }
        }
        return savedContactsList;
    }
    
    /**
     * 
     * @Title: getOneContantInfo   
     * @Description:获取一个人的详细联系方式    
     * @param list
     * @param personInfoMap
     * @param contactInfoList
     * @param photoUrl 
     * @return void    
     * @throws GeneralException
     */
    public void getOneContantInfo(ArrayList list,HashMap personInfoMap,ArrayList contactInfoList,String photoUrl) throws GeneralException{
        ArrayList contactsOrderList = new ArrayList();//用于记录后台配置的指标顺序
        List oneContactList = getContactsList(list,contactsOrderList,photoUrl);
        HashMap oneContactMap =(HashMap)oneContactList.get(0);
        //根据顺序获得联系信息
          personInfoMap.put("name", oneContactMap.get("name"));
          personInfoMap.put("pos_name", oneContactMap.get("pos_name"));
          personInfoMap.put("unit_dept_name", oneContactMap.get("unit_dept_name"));
          personInfoMap.put("photo", oneContactMap.get("photo"));
          //根据顺序获得联系信息
          for(int i=0;i<contactsOrderList.size();i++){
             String contactType =  (String)contactsOrderList.get(i);
             String contactValues =(String)oneContactMap.get(contactType);
             if(contactValues==null||"".equals(contactValues.trim())){//该人员没有填写该联系信息
                 continue;
             }
             String[] contactValue = contactValues.split("`");
             for (int j = 0; j < contactValue.length; j++) {
                 if(contactValue[j]!=null&&contactValue[j].trim()!="null"&&!"".equals(contactValue[j].trim())){
                     HashMap oneContactInfoMap = new HashMap();
                     oneContactInfoMap.put("content", contactValue[j]);
                     oneContactInfoMap.put("contactType", contactType);
                     contactInfoList.add(oneContactInfoMap); 
                 }
             }
          }
    }
    
   /**
    * 
    * @Title: getSelectedContactSql   
    * @Description:    获得选择人员联系方式的组合sql语句并且存入联系信息指标字段
    * @param @param nbaseA0100Map 关联的人员库和人员编号信息
    * @param @return 
    * @return String    
    * @throws GeneralException 
    * @throws
    */
    private String getSelectedContactSql(HashMap nbaseA0100Map,HashMap contantsFiledMap,ArrayList contantsOrderList) throws GeneralException {   
	    StringBuffer sql = new StringBuffer();
	    //获取通讯录配置指标字段
	    ArrayList contactsParamList = HTMLParamUtils.getContacts(conn);//这里获得的visible值全为1 所以不用过来visible=”0”
	    
	    String filed = "a0100,a0101,b0110,e0122,e01a1";
	    if(contactsParamList==null||contactsParamList.size()==0){
	        throw GeneralExceptionHandler.Handle(new Exception()); 
	    }
	    for(int i=0;i<contactsParamList.size();i++){
	        Map paramsMap =(Map) contactsParamList.get(i);
	        String menuid =(String) paramsMap.get("menuid");
	        String way =(String) paramsMap.get("way");
	       if(contantsFiledMap.containsKey(way)){
	           contantsFiledMap.put(way, contantsFiledMap.get(way)+"`"+menuid);//存入联系信息指标字段（多指标用`分隔）
	       }else{
	           contantsFiledMap.put(way, menuid);//存入联系信息指标字段
	           contantsOrderList.add(way);//按照 通讯录配置contactsParamList的顺序添加 相同的只添加一次
	       }
	        filed+=","+menuid;
	    }
	    
	  //遍历nbaseA0100Map拼写sql
	    Iterator it = nbaseA0100Map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry entry = (Entry) it.next();
	        String nbase =(String)entry.getKey();
	        String a0100s =(String)entry.getValue();
	        sql.append(" union select '"+ nbase+"' as nbase,'"+ nbase+"'"+Sql_switcher.concat()+"a0100 as nbaseA0100 ,"+filed+" from "+nbase+"A01 where a0100 in ("+a0100s+")");
	    }
	    sql.delete(0, 6);//去掉 开头的“ union”
	    return sql.toString();
    
    }

    /**
     * 
     * @Title: getNbaseA0100Map   
     * @Description:   根据前台选择人员，获得每个人员库的人员编号 
     * @param @param selectedContactMap
     * @param @return 
     * @return HashMap    存放数据形式如：{人员库，人员编号（之间用，分隔）}-{usr,"00000049,0000014"}
     * @throws
     */
    private HashMap getNbaseA0100Map(ArrayList selectedContactList ) {
        HashMap nbaseA0100Map = new HashMap();//存放数据形式如：{人员库，人员编号（之间用，分隔）}-{usr,"00000049,0000014"}
        for (int i=0;i<selectedContactList.size();i++) {
            String nbaseA0100 = (String)selectedContactList.get(i);
            String[]nbaseA0100Str= nbaseA0100.split("`");
            String nbase =nbaseA0100Str[0];//在map中取出人员库
            String a0100 =nbaseA0100Str[1];//在map中取出人员编号
            if(nbase!=null&&nbaseA0100Map.containsKey(nbase)){
                String a0100s =(String)nbaseA0100Map.get(nbase); 
                a0100s+=", "+a0100;
                nbaseA0100Map.put(nbase, a0100s);
            }else{
                nbaseA0100Map.put(nbase, a0100);
            }
        }
        return nbaseA0100Map;
    }
}
