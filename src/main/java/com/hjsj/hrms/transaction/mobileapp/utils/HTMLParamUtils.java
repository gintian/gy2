package com.hjsj.hrms.transaction.mobileapp.utils;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 与个人显示信息相关参数设置
 * <p>Title: HTMLParamUtils </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-12-20 上午09:08:23</p>
 * @author xuj
 * @version 1.0
 */
public class HTMLParamUtils {

	private static HashMap basicinfo_Map=null;
	/** 最近离职、入职模板*/
	private static HashMap complexStaffMap = null;
	/** 子集*/
	private static ArrayList sortFieldSets = null;
	/** 人员基本情况模板*/
	private static HashMap paramsetsMap=null;
	/** 联系方式*/
	private static ArrayList contacts = null;
	
	
	/**
	 * 获取联系方式参数
	 * @param conn
	 * @return List<Map<key,value>> key与value对于如：id=”2” way=”phone” menuid=”A013C” visible=”0”
	 */
	public static ArrayList getContacts(Connection conn) {
		if(contacts == null)
			initParam(conn);
		if(contacts==null||contacts.size()==0){//兼容以前程序参数设置
			contacts = getOldContact(conn);
		}
		return contacts;
	}
	
	/**
	 * 
	 * @Title:getStaffminus
	 * @Description:获取最近离职模板   
	 * @param  conn
	 * @return HashMap
	 * @throws
	 */
	public static HashMap getStaffminusMap(Connection conn) {
		if(complexStaffMap == null)
			initParam(conn);
		else if(complexStaffMap.get("staffminus")==null)
			initParam(conn);
		return (HashMap) complexStaffMap.get("staffminus");
	}
	
	/**
	 * 
	 * @Title: getStaffadd   
	 * @Description: 获取最近入职模板   
	 * @param conn
	 * @return 
	 * @return HashMap    
	 * @throws
	 */
	public static HashMap getStaffaddMap(Connection conn) {
		if(complexStaffMap == null)
			initParam(conn);
		else if(complexStaffMap.get("staffadd")==null)
			initParam(conn);
		return (HashMap) complexStaffMap.get("staffadd");
	}

	/**
	 * 获取人员基本情况模板
	 * @param conn
	 * @return
	 */
	public static HashMap getBasicinfo_Map(Connection conn){
		if(basicinfo_Map==null){
			initParam(conn);
		}
		return basicinfo_Map;
	}
	
	/**
	 * 按顺序获取子集代号
	 * @param conn
	 * @return
	 */
	public static ArrayList getSortFieldSets(Connection conn){
		if(sortFieldSets==null){
			initParam(conn);
		}
		return sortFieldSets;
	}
	
	/**
	 * 获取子集信息参数
	 * @param conn
	 * @return
	 */
	public static HashMap getSetsMap(Connection conn){
		if(paramsetsMap==null){
			initParam(conn);
		}
		return paramsetsMap;
	}
	
	private static ArrayList initParam(Connection conn){
		
		String basicinfo_template="";
		HashMap setsMap = new HashMap();
		ArrayList sortSets = new ArrayList();
		ArrayList paramlist  = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		HashMap complexMap = new HashMap();
		ArrayList contactlist = new ArrayList();
		try{
			RecordVo vo=ConstantParamter.getConstantVo("PDA");
			//String sql = "select constant from constant where UPPER(Constant)='PDA'";
			//this.frowset = dao.search(sql);
			if(vo==null)
				throw new GeneralException(ResourceFactory.getProperty("cs.person.html.set"));
			ConstantXml xml = new ConstantXml(conn,"PDA","pda");
			
			/**
			 * <?xml version="1.0" encoding="GB2312"?>
				<pda>
					<!-- 子集和指标 -->
					<sets>
						<subset seq="0" id="A04">
							<menu mseq="0" id="C0401" />
						</subset>
						<subset seq="1" id="A02">
							<menu mseq="0" id="A0201" />
							<menu mseq="1" id="A0202" />
						</subset>
				 	</sets>
				 	<!-- 是否支持双击页面全屏 仅离开输出HTML用-->
				 	<DblClickFullScreen DblClickFullScreen="True|False" />
				 	<!-- 是否支持查询,查询指标格式:B0110,A0101,C0702,A0111 -->
				 	<serch_set CanQuery="True|False" SelectField="B0110,A0101,C0702,A0111" />
				</pda>
			 */
			List allchildren = xml.getAllChildren("/pda/sets");
			if(allchildren != null){
				// 子集排序map
				TreeMap treeMap = new TreeMap();
				// 子集详细排序map
				TreeMap subsetTreeMap = new TreeMap();
				// 子集详细数组
				List subsetList;
				// 遍历子集
				for(int i=0,n=allchildren.size();i<n;i++){
					Element element = (Element)allchildren.get(i);
					// 子集key
					String id = element.getAttributeValue("id");
					if(!id.startsWith("A")){//不是人员信息集则是脏数据
						continue;
					}
					
					subsetList = element.getChildren();
					// 清空
					subsetTreeMap.clear();
					// 遍历子集详细
					for (int j = 0; j < subsetList.size(); j++) {
						Element subsetElement = (Element)subsetList.get(j);
						// 子集详细key
						String subsetID = subsetElement.getAttributeValue("id");
						FieldItem item  = DataDictionary.getFieldItem(subsetID, id);
						if(item==null||"0".equals(item.getUseflag())){
							continue;
						}
						// 所在位置
						String subsetMseq = subsetElement.getAttributeValue("mseq");
						if(subsetMseq==null){
							subsetTreeMap.put(subsetTreeMap.size(),subsetID);
						}else{
							subsetTreeMap.put(Integer.parseInt(subsetMseq), subsetID);
						}
					}
					// 清空
					subsetList = new ArrayList();
					// 排序子集详细
					for(Iterator j = subsetTreeMap.entrySet().iterator(); j.hasNext();){
						Entry entry = (Entry)j.next();
						subsetList.add((String)entry.getValue());
					}
					
					// 所在位置
					String seq = element.getAttributeValue("seq");
					
					// 放入子集详细map中
					setsMap.put(id, subsetList);
					if(seq==null){
						treeMap.put(treeMap.size()+"", id);
					}else{
						treeMap.put(seq, id);
					}
					
				}
				
				//xus 19/2/27 原排序方法不对
				for(int i = 0;i<treeMap.size();i++){
					sortSets.add(treeMap.get(i+""));
				}
//				// 排序子集
//				for(Iterator j = treeMap.entrySet().iterator(); j.hasNext();){
//					Entry entry = (Entry)j.next();
//					sortSets.add((String)entry.getValue());
//				}

			}
			
			//子集顺序处理 xuj add 2013-11-28
/**
			ArrayList fieldsets = DataDictionary.getFieldSetList(1, 1);
			for(int i=0,n=fieldsets.size();i<n;i++){
				FieldSet fieldset = (FieldSet)fieldsets.get(i);
				String setid = fieldset.getFieldsetid();
				if(setsMap.containsKey(setid))
					sortSets.add(setid);
			}
*/
			/**
			<!-- 人员基本情况模板 -->
		 	<basicinfo_template>
		 	  [性别]，[民族]，[年龄]岁([出生日期]出生)，[籍贯]，[入党时间]入党，[参加工作时间]参加工作。
		 	</basicinfo_template>
		 	*/
			basicinfo_template = xml.getTextValue("/pda/basicinfo_template");
			//查询复杂查询模板,yangj,2013-12-02
			//从数据库，Constant表中PDA中读取xml文件，格式如下
			/**
			 * <!--业务列表参数设置-->
			  <business>
			  		<!--最近入职复杂查询id、复杂查询人员库 -->
					<staffadd id="73" nbase="Usr" />
					<!--最近离职复杂查询id、复杂查询人员库- -->
					<staffminus id="73" nbase="Ret" />
			   </business>
			 */
			List listStaff = xml.getAllChildren("/pda/business");
			if(listStaff != null){
				for(int i=0,n=listStaff.size();i<n;i++){
					Element element = (Element)listStaff.get(i);			
					HashMap map = new HashMap();
					map.put("id", element.getAttributeValue("id"));
					map.put("nbase", element.getAttributeValue("nbase"));
					complexMap.put(element.getName(), map);		
				}
			}
			
			/**
		　　　　<!--联系方式设置-->
		　　　　<contactway>
				　　<contact id=”1” way=”mobile” menuid=”A01SC” visible=”1” />
				　　<contact id=”2” way=”phone” menuid=”A013C” visible=”0”/>
			  </contactway>
			 */
			List contactways = xml.getAllChildren("/pda/contactway");
			if(contactways!=null){
				TreeMap treeMap = new TreeMap();
				for(int i=0,n=contactways.size();i<n;i++){
					Element element = (Element)contactways.get(i);
					String id = element.getAttributeValue("id");
					HashMap map = new HashMap();
					map.put("id", id);
					map.put("way", element.getAttributeValue("way"));
					map.put("menuid", element.getAttributeValue("menuid"));
					map.put("visible", element.getAttributeValue("visible"));
					treeMap.put(id, map);
				}
				for(Iterator i = treeMap.entrySet().iterator();i.hasNext();){
					Entry entry = (Entry)i.next();
					HashMap map = (HashMap)entry.getValue();
					if("1".equals(map.get("visible"))){
						contactlist.add(map);
					}
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			basicinfo_Map = analyseBasicinfo_template(basicinfo_template, dao);
			paramsetsMap = setsMap;
			sortFieldSets=sortSets;
			complexStaffMap=complexMap;
			contacts= contactlist;
		}
		return paramlist;
	}
		
	/**
	 * 
	 * @Title: analyseBasicinfo_template   
	 * @Description: 获取描述信息
	 * @param basicinfo_template
	 * @param dao
	 * @return HashMap
	 */
	private static HashMap analyseBasicinfo_template(String basicinfo_template,ContentDAO dao){
		basicinfo_template = basicinfo_template==null?"":basicinfo_template;
		HashMap map = new HashMap();
		StringBuffer itemnames=new StringBuffer();
		int si=-1,ei=-1;
		RowSet rs = null;
		try{
			while(true){
				if(si!=-1){
					++si;
					++ei;
				}
				si=basicinfo_template.indexOf('[', si);
				ei=basicinfo_template.indexOf(']', ei);
				if(si==-1||ei==-1)
					break;
				itemnames.append(",'"+basicinfo_template.substring(si+1,ei)+"'");
			}
			String sql = "select itemid,itemdesc,fieldsetid from fielditem where useflag='1' and fieldsetid like 'A%' and itemdesc in('###'"+itemnames.toString()+")";
			rs=dao.search(sql);
			HashMap mapsets=new HashMap();
			HashMap mapsetstr = new HashMap();
			while(rs.next()){
				String fieldsetid = rs.getString("fieldsetid");
				String itemid=rs.getString("itemid");
				String itemdesc=rs.getString("itemdesc");
				basicinfo_template=basicinfo_template.replace("["+itemdesc+"]", "["+itemid+"]");
				if(mapsets.containsKey(fieldsetid)){
					ArrayList itemids = (ArrayList)mapsets.get(fieldsetid);
					StringBuffer itemidsb = (StringBuffer)mapsetstr.get(fieldsetid);
					itemids.add(itemid);
					itemidsb.append(","+itemid);
				}else{
					ArrayList itemids = new ArrayList();
					StringBuffer itemidsb = new StringBuffer();
					itemids.add(itemid);
					itemidsb.append(","+itemid);
					mapsets.put(fieldsetid, itemids);
					mapsetstr.put(fieldsetid, itemidsb);
				}
				
				
			}
			// linbz  20160604   如果业务字典没有单位名称，岗位名称，单独处理
			if(itemnames.toString().contains("'单位名称'") && !basicinfo_template.contains("[B0110]")){
                basicinfo_template=basicinfo_template.replace("[单位名称]", "[B0110]");
                if(mapsets.containsKey("A01")){
                    ArrayList itemids = (ArrayList)mapsets.get("A01");
                    StringBuffer itemidsb = (StringBuffer)mapsetstr.get("A01");
                    itemids.add("B0110");
                    itemidsb.append(","+"B0110");
                }else{
                    ArrayList itemids = new ArrayList();
                    StringBuffer itemidsb = new StringBuffer();
                    itemids.add("B0110");
                    itemidsb.append(","+"B0110");
                    mapsets.put("A01", itemids);
                    mapsetstr.put("A01", itemidsb);
                }
            }
			
			if(itemnames.toString().contains("'岗位名称'") && !basicinfo_template.contains("[E01A1]")){
                basicinfo_template=basicinfo_template.replace("[岗位名称]", "[E01A1]");
                if(mapsets.containsKey("A01")){
                    ArrayList itemids = (ArrayList)mapsets.get("A01");
                    StringBuffer itemidsb = (StringBuffer)mapsetstr.get("A01");
                    itemids.add("E01A1");
                    itemidsb.append(","+"E01A1");
                }else{
                    ArrayList itemids = new ArrayList();
                    StringBuffer itemidsb = new StringBuffer();
                    itemids.add("E01A1");
                    itemidsb.append(","+"E01A1");
                    mapsets.put("A01", itemids);
                    mapsetstr.put("A01", itemidsb);
                }
            }
			
			map.put("basicinfo_template", basicinfo_template);
			map.put("mapsets", mapsets);
			map.put("mapsetstr", mapsetstr);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return map;
	}
	
	private static ArrayList getOldContact(Connection conn){
		ArrayList contacts = new ArrayList();
		RecordVo vo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE", conn);
		if(vo!=null){
			String mobileField = vo.getString("str_value");
			mobileField = mobileField==null?"":mobileField;
			FieldItem item = DataDictionary.getFieldItem(mobileField.toLowerCase());
			if(item==null|| "0".equals(item.getUseflag())){
				mobileField= "";
			}
			if(mobileField.length()>0){
				HashMap map = new HashMap();
				map.put("id", "1");
				map.put("way", "mobile");
				map.put("menuid", mobileField);
				map.put("visible", "1");
				contacts.add(map);
			}
		}
		vo = ConstantParamter.getConstantVo("SS_TELEPHONE", conn);
		if(vo!=null){
			String phoneField = vo.getString("str_value");
			phoneField = phoneField==null?"":phoneField;
			FieldItem item = DataDictionary.getFieldItem(phoneField.toLowerCase());
			if(item==null|| "0".equals(item.getUseflag())){
				phoneField= "";
			}
			if(phoneField.length()>0){
				HashMap map = new HashMap();
				map.put("id", "1");
				map.put("way", "telephone");
				map.put("menuid", phoneField);
				map.put("visible", "1");
				contacts.add(map);
			}
		}
		vo = ConstantParamter.getConstantVo("SS_EMAIL", conn);
		if(vo!=null){
			String emailField = vo.getString("str_value");
			emailField = emailField==null?"":emailField;
			FieldItem item = DataDictionary.getFieldItem(emailField.toLowerCase());
			if(item==null|| "0".equals(item.getUseflag())){
				emailField= "";
			}
			if(emailField.length()>0){
				HashMap map = new HashMap();
				map.put("id", "1");
				map.put("way", "email");
				map.put("menuid", emailField);
				map.put("visible", "1");
				contacts.add(map);
			}
		}
		
		return contacts;
	}
}
