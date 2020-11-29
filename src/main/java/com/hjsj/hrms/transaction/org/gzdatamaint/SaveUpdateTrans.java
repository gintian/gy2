package com.hjsj.hrms.transaction.org.gzdatamaint;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SaveUpdateTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		HashMap hm=this.getFormHM();
		String name=(String)hm.get("position_set_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("position_set_record");

	//2013-11-17 dengcan 界面已控制，此处控制去掉  	
	//	String fieldPri = this.userView.analyseTablePriv(name);
	//	if(!fieldPri.equals("2"))
	//		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.update.record.competence")+"！"));
		try
		{
			HashMap<String,HashMap<String,String>> leafMap=new HashMap<String, HashMap<String, String>>();
			if(list != null && list.size() > 0) {
				RecordVo recordVo = (RecordVo)list.get(0);
				HashMap map = recordVo.getValues();
				Iterator iterator = map.keySet().iterator();
				ArrayList<String> list1=new ArrayList<String>();
				while(iterator.hasNext()) {
					String key = (String)iterator.next();
					FieldItem fieldItem = DataDictionary.getFieldItem(key);

					if(StringUtils.isNotBlank(fieldItem.getCodesetid())&&!"0".equals(fieldItem.getCodesetid())){
						list1.add(fieldItem.getCodesetid().toUpperCase());
					}

				}

				leafMap=this.getLeafNodeMap(list1);
			}

			/* 薪酬管理/参数设置/基础数据维护界面，如果录入的数值超出长度限制应该给出正确的提示，不应该提示程序错误如图 xiaoyun 2014-10-27 start */
			if(list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					RecordVo recordVo = (RecordVo)list.get(i);
					HashMap map = recordVo.getValues();
					Iterator iterator = map.keySet().iterator();
					while(iterator.hasNext()) {
						String key = (String)iterator.next();
						FieldItem fieldItem = DataDictionary.getFieldItem(key);
						CodeItem item = null;
						if(AdminCode.getCode("UN", (String)map.get("b0110"))!=null) {
							item = AdminCode.getCode("UN", (String)map.get("b0110"));
						}else {
							item = AdminCode.getCode("UM", (String)map.get("b0110"));
						}
						//A-字符串 N-数值
						if("N".equalsIgnoreCase(fieldItem.getItemtype())) {
							if(fieldItem.getDecimalwidth()==0) { // 说明为整形
								Object tempValue = (Object)map.get(key);
								String value = new java.text.DecimalFormat("0").format(tempValue);//由于dataset标签返回的整形数据也带有小数位（带.0） 此处四舍五入去掉.0 zhaoxg add 2014-11-25
									if(value.length() > fieldItem.getItemlength()) { // 报错
										throw new Exception(item.getCodename()+" 的 "+fieldItem.getItemdesc()+" 的长度超过了 "+fieldItem.getItemlength()+"位!");
									}
							}else { // 说明为浮点型
								Double value = (Double)map.get(key);
								if(value != null) {
									DecimalFormat nf=new DecimalFormat("0.#############");							
									String strValue = nf.format(value);			
									String inValue = "";
									String decimal = "";
									if(strValue.indexOf(".") != -1) {
										inValue = strValue.substring(0, strValue.indexOf("."));									
										decimal = strValue.substring(strValue.indexOf(".")+1);
									}else {
										inValue = strValue;
									}
									if(inValue.length() > fieldItem.getItemlength()) {
										throw new Exception(item.getCodename()+" 的 "+fieldItem.getItemdesc()+" 整数位长度超过了 "+fieldItem.getItemlength()+"位!");
									}
									if(decimal != null) {										
										if(decimal.length() > fieldItem.getDecimalwidth()) {
											throw new Exception(item.getCodename()+" 的 "+fieldItem.getItemdesc()+" 小数位长度超过了 "+fieldItem.getDecimalwidth()+"位!");
										}
									}
								}
							}
						}else if("A".equalsIgnoreCase(fieldItem.getItemtype())) {
							int length = fieldItem.getItemlength();
							String value = (String)map.get(key);
							
							if(value != null && value.length() > 0) {
								if(value.length() > length) {
									throw new Exception(item.getCodename()+" 的 "+fieldItem.getItemdesc()+" 长度超过了 "+fieldItem.getItemlength()+"!");
								}
							}
							String codeSetId=fieldItem.getCodesetid();
							if(StringUtils.isNotBlank(value) && leafMap.containsKey(codeSetId)&&!leafMap.get(codeSetId).containsKey(value)){
								throw new Exception(item.getCodename()+" '"+fieldItem.getItemdesc()+"' 列仅可选择末级代码，'"+AdminCode.getCode(codeSetId, value).getCodename()+"' 不可选择!");
							}




						}
					}
				}
			}
			/* 薪酬管理/参数设置/基础数据维护界面，如果录入的数值超出长度限制应该给出正确的提示，不应该提示程序错误如图 xiaoyun 2014-10-27 end */

			if("K".equalsIgnoreCase(name.substring(0,1))){
				for(int i=0;i<list.size();i++){
					RecordVo vo=(RecordVo)list.get(i);
					String B0110 = vo.getString("b0110");
					B0110=B0110!=null?B0110:"";
				
					String E0122 = vo.getString("e0122");
					E0122=E0122!=null?E0122:"";
					String E01A1 = vo.getString("e01a1");
					E01A1=E01A1!=null?E01A1:"";
					if(E0122.indexOf(B0110)==-1&&E0122.length()>0)
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.org.includes.depart")+"！"));
					if(E01A1.indexOf(E0122)==-1&&name.indexOf("A01")!=-1&&E01A1.length()>0)
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.org.includes.job")+"！"));
				}
			}
		
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.updateValueObject(list);
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}


	/**
	 * 根据代码类id获取其中设置了"仅可选择叶子节点"的代码类叶子节点
	 * @param codeList
	 * @return
	 * @throws GeneralException
	 */
	private HashMap<String,HashMap<String,String>> getLeafNodeMap(ArrayList<String> codeList) throws GeneralException {
		RowSet rs=null,rowSet=null;
		HashMap<String,HashMap<String,String>> leafCodeColMap=new HashMap<String, HashMap<String, String>>();
		try{
			ContentDAO dao=new ContentDAO(this.frameconn);
			StringBuffer sql=new StringBuffer();
			StringBuffer codeBuf=new StringBuffer();

			for (int i = 0; i < codeList.size(); i++) {
				codeBuf.append("?,");
			}
			codeBuf.deleteCharAt(codeBuf.length()-1);

			sql.append("SELECT codesetid FROM codeset where codesetid in (").append(codeBuf).append(" )");
			sql.append(" and ");
			sql.append(Sql_switcher.isnull("leaf_node", "''")).append("=1 ");
			rs=dao.search(sql.toString(),codeList);

			sql.setLength(0);
			sql.append(" SELECT codeitemid,codeitemdesc FROM codeitem WHERE codesetid=? ");
			sql.append(" AND ").append(Sql_switcher.sqlNow()).append(" BETWEEN start_date AND end_date ");
			sql.append(" AND codeitemid NOT IN ( SELECT parentid FROM codeitem WHERE codesetid=? AND parentid<>codeitemid ");
			sql.append(" AND ").append(Sql_switcher.sqlNow()).append(" BETWEEN start_date AND end_date )");

			while(rs.next()){
				codeList.clear();
				String codesetid=rs.getString("codesetid");
				codeList.add(codesetid);
				codeList.add(codesetid);
				rowSet=dao.search(sql.toString(),codeList);
				while (rowSet.next()){
					if(leafCodeColMap.containsKey(codesetid)) {
						leafCodeColMap.get(codesetid).put(rowSet.getString("codeitemid"),rowSet.getString("codeitemdesc"));
					}else{
						HashMap<String,String> tempMap=new HashMap<String, String>();
						tempMap.put(rowSet.getString("codeitemid"),rowSet.getString("codeitemdesc"));
						leafCodeColMap.put(codesetid,tempMap);
					}
				}
			}

// 通过一次查询获取map的方法，由于可读性差，已废弃
//			sql.append("SELECT cm.codesetid,cm.codeitemid,cm.codeitemdesc FROM codeitem cm  inner join codeset on cm.codesetid=codeset.codesetid  LEFT JOIN (");
//			sql.append(" SELECT COUNT(1) AS num ,codesetid,parentid FROM codeitem WHERE  codeitemid<>parentid AND ");
//			sql.append(Sql_switcher.sqlNow()).append(" BETWEEN start_date AND end_date  and upper(codesetid) in (");
//			sql.append(codeBuf.toString()).append(") GROUP BY parentid,codesetid ");
//			sql.append(") cnum ON cm.codesetid =cnum.codesetid AND cm.codeitemid=cnum.parentid ");
//			sql.append(" WHERE ").append(Sql_switcher.isnull("cnum.num", "0"));
//			sql.append("=0 and upper(cm.codesetid) in (").append(codeBuf.toString()).append(") and ");
//			sql.append(Sql_switcher.isnull("leaf_node", "''")).append("=1 AND ");
//			sql.append(Sql_switcher.sqlNow()).append(" BETWEEN cm.start_date AND cm.end_date");

//			ArrayList<String> tempList= (ArrayList<String>) codeList.clone();
//
//			codeList.addAll(tempList);
//			rs = dao.search(sql.toString(),codeList);
//			while (rs.next()) {
//
//				if(leafCodeColMap.containsKey(rs.getString("codesetid"))) {
//					leafCodeColMap.get(rs.getString("codesetid")).put(rs.getString("codeitemid"),rs.getString("codeitemdesc"));
//				}else{
//					HashMap<String,String> tempMap=new HashMap<String, String>();
//					tempMap.put(rs.getString("codeitemid"),rs.getString("codeitemdesc"));
//					leafCodeColMap.put(rs.getString("codesetid"),tempMap);
//				}
//			}

		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rowSet);
			PubFunc.closeDbObj(rs);
		}
		return  leafCodeColMap;
	}
}
