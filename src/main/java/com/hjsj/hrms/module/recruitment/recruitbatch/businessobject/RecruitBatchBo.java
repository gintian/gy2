package com.hjsj.hrms.module.recruitment.recruitbatch.businessobject;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.module.recruitment.util.ZpPendingtaskBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：RecruitBatchBo 
 * 类描述：招聘批次Bo类
 * 创建人：sunming 
 * 创建时间：2015-10-27
 * 
 * @version
 */
public class RecruitBatchBo {
	private Connection conn = null;
	/** 登录用户 */
	private UserView userview;
	public RecruitBatchBo(Connection conn, UserView userview) {
		this.conn=conn;
		this.userview=userview;
	}

	/**
	 * 招聘批次主页面--拼接column的方法
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList<ColumnsInfo> getColumnList() throws GeneralException {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try {
			String sql = "select * from z01 where 1=2";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			ResultSetMetaData metadata = rs.getMetaData();
			String itemid = "";
			String itemdesc = "";
			String itemtype = "";
			int decimalwidth = 0;
			int itemlength=0;
			String codesetid = "";
			String state = "";
			itemid = "z0129";
			FieldItem fi = DataDictionary.getFieldItem(itemid,"Z01");
			ColumnsInfo info = new ColumnsInfo(fi);
			info.setTextAlign("left");
			info.setLocked(true);
			info.setOperationData("RecruitbatchGlobal.toGetDataValue()");
			list.add(info);
			itemid = "flag";
			FieldItem item = new FieldItem();
			item.setItemid(itemid);
			item.setItemdesc("flag");
			item.setItemtype("A");
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setTextAlign("left");
			info.setLocked(true);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
			fi = DataDictionary.getFieldItem("z0103", "Z01");
			info = new ColumnsInfo(fi);
			info.setColumnWidth(200);
			info.setRendererFunc("RecruitbatchGlobal.toModifyRecruitBatch");
			info.setLocked(true);
			list.add(info);
			FieldItem fieldItem = new FieldItem();
			for(int i = 1;i<=metadata.getColumnCount();i++){
				itemid = metadata.getColumnName(i).toLowerCase();
				fieldItem = DataDictionary.getFieldItem(itemid,"Z01");
				if("z0129".equalsIgnoreCase(itemid)||"z0103".equalsIgnoreCase(itemid)||fieldItem==null){
					continue;
				}
				info = new ColumnsInfo(fieldItem);
				if("0".equals(fieldItem.getState())||"z0101".equals(fieldItem.getItemid()))
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				if("z0153".equalsIgnoreCase(itemid)){
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					FieldItem item1 = new FieldItem();
					item1.setItemid("name");
					item1.setItemdesc("招聘流程");
					item1.setItemtype("A");
					item1.setCodesetid("0");
					ColumnsInfo info1 = new ColumnsInfo(item1);
					info1.setTextAlign("left");
					info1.setColumnWidth(200);
					list.add(info1);
				}
				if("0".equals(state)&&!"z0103".equalsIgnoreCase(itemid)&&!"z0105".equalsIgnoreCase(itemid)){
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				}
				if("z0105".equalsIgnoreCase(itemid)){
					info.setNmodule("7");
					info.setCtrltype("0");
				}
				
				if("z0101".equalsIgnoreCase(itemid)){
					info.setOrdertype("1");
				}
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * 招聘批次主页面--获取sql的方法
	 * @param z0129 批次审批状态
	 * @return
	 */
	public String getDataSql(String z0129) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		try {
			z0129=SafeCode.decode(z0129);
			sql.append("select z.*,f.name, 2 as flag ");
			sql.append(" from z01 z left join zp_flow_definition f");
			sql.append(" on z.z0153=f.flow_id");
			sql.append(" where 1=1 ");
			
			if(!"00".equalsIgnoreCase(z0129)){
				sql.append(" and z.z0129=");
				sql.append(z0129);
			}
			
			
			RecruitPrivBo privBo = new RecruitPrivBo();
			String privB0110 = privBo.getPrivB0110Whr(userview, "z0105", RecruitPrivBo.LEVEL_SELF_CHILD);
			HashMap<String, Object> parame = privBo.getChannelPrivMap(userview, conn);
			boolean setFlag = (Boolean) parame.get("setFlag");
			StringBuffer str = new StringBuffer();
			if(setFlag) {
				ArrayList<String> hirePriv = (ArrayList<String>) parame.get("hirePriv");
				if(hirePriv.size()>0) {
					str.append(" and (");
					for (String hire : hirePriv) {
						str.append(" z0151 like ");
						str.append("'"+hire+"%' or");
					}
					str.setLength(str.length()-2);
					str.append(" )");
					}
				else
					str.append(" and 1=2 ");
			}
			sql.append(" and ").append(privB0110);
			sql.append(str);
			sql.append(" union all ");
			sql.append(" select z.*,f.name, 1 as flag ");
			sql.append(" from z01 z left join zp_flow_definition f");
			sql.append(" on z.z0153=f.flow_id");
			sql.append(" where 1=1 ");
			
			if(!"00".equalsIgnoreCase(z0129)){
				sql.append(" and z.z0129=");
				sql.append(z0129);
			}
			
			privB0110 = privBo.getPrivB0110Whr(userview, "z0105", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD);
			sql.append(" and ").append(privB0110);
			sql.append(" and z0101 not in (select z0101 from z01 where 1=1 and ");
			privB0110 = privBo.getPrivB0110Whr(userview, "z0105", RecruitPrivBo.LEVEL_SELF_CHILD);
			sql.append(privB0110);
			sql.append(" )");
			
			sql.append(str);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return sql.toString();
	}
	/**
	 * 招聘批次主页面--拼接buttonList的方法
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getButtonList() throws GeneralException {
		ArrayList buttonList = new ArrayList();
		try {
			buttonList.add("-");
            if (userview.hasTheFunction("3110001"))
                buttonList.add(new ButtonInfo("新建批次","RecruitbatchGlobal.addRecruitBatch"));// 新建批次
            
            if (userview.hasTheFunction("3110003"))
                buttonList.add(new ButtonInfo("删除批次","RecruitbatchGlobal.deleteRecruitBatch"));// 删除批次
            
            buttonList.add("-");
            if (userview.hasTheFunction("3110004"))
                buttonList.add(new ButtonInfo("批次发布","RecruitbatchGlobal.publishRecruitBatch"));// 发布
            if (userview.hasTheFunction("3110008"))
            	buttonList.add(new ButtonInfo("发送通知","RecruitbatchGlobal.sendNotice"));// 发送通知
            
            if (userview.hasTheFunction("3110005"))
                buttonList.add(new ButtonInfo("结束", "RecruitbatchGlobal.closeRecruitBatch"));//结束
			// 加搜索条
//			buttonList.add(new ButtonInfo("<div id='fastsearch'> </div>"));
			
			  ButtonInfo querybox = new ButtonInfo();
		      querybox.setFunctionId("ZP0000002537");
		      querybox.setType(ButtonInfo.TYPE_QUERYBOX);
		      querybox.setText("请输入批次名称...");
		      buttonList.add(querybox);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buttonList;
	}
	/**
	 * 创建招聘批次页面--加载招聘渠道和招聘流程下拉框
	 * @param z0153Id 
	 * @param type=1 招聘渠道  =2 招聘流程
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getRecruitChannelOrFlow(String type, String z0153Id) throws GeneralException {
		ArrayList list = new ArrayList();
		ArrayList list2 = new ArrayList();
		String sql = "";
		StringBuffer sqlz0153 = new StringBuffer("select flow_id as itemid ,name as itemdesc from zp_flow_definition  WHERE ");
		RowSet rs = null;
		RowSet rs2 = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			if("1".equals(type)){//招聘渠道不能修改
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String backdate = sdf.format(new Date());
				sql="select codeitemid as itemid,codeitemdesc as itemdesc from codeitem where codesetid='35' and invalid='1' and codeitemid<>'03'  "
						+ "and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date";
				RecruitPrivBo privBo = new RecruitPrivBo();
				HashMap<String, Object> parame = privBo.getChannelPrivMap(userview, conn);
				boolean setFlag = (Boolean) parame.get("setFlag");
				StringBuffer str = new StringBuffer();
				if(setFlag) {
					ArrayList<String> hirePriv = (ArrayList<String>) parame.get("hirePriv");
					if(hirePriv.size()>0) {
						str.append(" and (");
						for (String hire : hirePriv) {
							str.append(" codeitemid like ");
							str.append("'"+hire+"%' or");
						}
						str.setLength(str.length()-2);
						str.append(" )");
						}
					else
						str.append(" and 1=2 ");
				}
				sql += str;
			}else{
				sql="select flow_id as itemid ,name as itemdesc from zp_flow_definition";
				sql = sql + " WHERE valid=1";
				RecruitPrivBo privBo = new RecruitPrivBo();
				String privB0110 = privBo.getPrivB0110Whr(userview, "B0110", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD);
				sql+=" and "+privB0110;
				sql+=" order by flow_id";
				if(StringUtils.isNotEmpty(z0153Id)){
					sqlz0153.append( privB0110);
					sqlz0153.append(" and flow_id=?");
					list2.add(z0153Id);
					rs2 = dao.search(sqlz0153.toString(),list2);
				}
			}
			if(rs2!=null&&rs2.next()){
				HashMap map = new HashMap();
				map.put("itemid", rs2.getString("itemid"));
				map.put("itemdesc", rs2.getString("itemdesc"));
				list.add(map);
			}
			rs = dao.search(sql);
			while(rs.next()){
				HashMap map = new HashMap();
				map.put("itemid", rs.getString("itemid"));
				map.put("itemdesc", rs.getString("itemdesc"));
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(rs2);
		}
		return list;
	}
	/**
	 * 创建/修改招聘批次页面--保存的方法
	 * @param type =1新增 =2修改
	 * @param list
	 * @param bean 自定义指标值
	 * @param fields 批次中自定义的指标
	 * @throws GeneralException 
	 */
	public void synchroList(String type, ArrayList list, ArrayList<MorphDynaBean> fields, MorphDynaBean bean) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String b0110 = (String) list.get(1);
			if(b0110!=null){
				list.set(1, b0110.split("`")[0]);
			}
			String dateFormat = "";
			RecruitUtilsBo bo = new RecruitUtilsBo(conn);
			//处理时间格式
			if(Sql_switcher.searchDbServer() == Constant.ORACEL){
				String z0107 = (String)list.get(4);
        		dateFormat = bo.getDateFormat("z0107");
				if (!StringUtils.isEmpty(z0107))
					list.set(4, DateUtils.getTimestamp(z0107, dateFormat));
				
				String z0109 = (String)list.get(5);
				dateFormat = bo.getDateFormat("z0109");
				if (!StringUtils.isEmpty(z0109))
				{
					Timestamp timeValue  =this.getTimeValue(dateFormat,z0109);
            		list.set(5, timeValue);
				}
				
				String z0155 = (String)list.get(6);
				dateFormat = bo.getDateFormat("z0155");
				if (!StringUtils.isEmpty(z0155))
					list.set(6, DateUtils.getTimestamp(z0155, dateFormat));
				
				String z0157 = (String)list.get(7);
				dateFormat = bo.getDateFormat("z0157");
				if (!StringUtils.isEmpty(z0157)){
					Timestamp timeValue  =this.getTimeValue(dateFormat,z0157);
            		list.set(7, timeValue);
				}
				
				String z0159 = (String)list.get(8);
				dateFormat = bo.getDateFormat("z0159");
				if (!StringUtils.isEmpty(z0159))
					list.set(8, DateUtils.getTimestamp(z0159, dateFormat));
				
				String z0161 = (String)list.get(9);
				dateFormat = bo.getDateFormat("z0161");
				if (!StringUtils.isEmpty(z0161)){
					Timestamp timeValue  =this.getTimeValue(dateFormat,z0161);
            		list.set(9, timeValue);
				}
				
				String z0163 = (String)list.get(10);
				dateFormat = bo.getDateFormat("z0163");
				if (!StringUtils.isEmpty(z0163))
					list.set(10, DateUtils.getTimestamp(z0163, dateFormat));
				
				String z0165 = (String)list.get(11);
				dateFormat = bo.getDateFormat("z0165");
				if (!StringUtils.isEmpty(z0165)){
					Timestamp timeValue  =this.getTimeValue(dateFormat,z0165);
            		list.set(11, timeValue);
				}
			}else{
				String z0107 = (String)list.get(4);
        		dateFormat = bo.getDateFormat("z0107");
				if (!StringUtils.isEmpty(z0107))
					list.set(4, DateUtils.getTimestamp(z0107, dateFormat));
				else
					list.set(4, null);
				
				String z0109 = (String)list.get(5);
				dateFormat = bo.getDateFormat("z0109");
				if (!StringUtils.isEmpty(z0109))
				{
					Timestamp timeValue  =this.getTimeValue(dateFormat,z0109);
            		list.set(5, timeValue);
				}else
					list.set(5, null);
				
				String z0155 = (String)list.get(6);
				dateFormat = bo.getDateFormat("z0155");
				if (!StringUtils.isEmpty(z0155))
					list.set(6, DateUtils.getTimestamp(z0155, dateFormat));
				else
					list.set(6, null);
				
				String z0157 = (String)list.get(7);
				dateFormat = bo.getDateFormat("z0157");
				if (!StringUtils.isEmpty(z0157)){
					Timestamp timeValue  =this.getTimeValue(dateFormat,z0157);
            		list.set(7, timeValue);
				}else
					list.set(7, null);
				
				String z0159 = (String)list.get(8);
				dateFormat = bo.getDateFormat("z0159");
				if (!StringUtils.isEmpty(z0159))
					list.set(8, DateUtils.getTimestamp(z0159, dateFormat));
				else
					list.set(8, null);
				
				String z0161 = (String)list.get(9);
				dateFormat = bo.getDateFormat("z0161");
				if (!StringUtils.isEmpty(z0161)){
					Timestamp timeValue  =this.getTimeValue(dateFormat,z0161);
            		list.set(9, timeValue);
				}else
					list.set(9, null);
				
				String z0163 = (String)list.get(10);
				dateFormat = bo.getDateFormat("z0163");
				if (!StringUtils.isEmpty(z0163))
					list.set(10, DateUtils.getTimestamp(z0163, dateFormat));
				else
					list.set(10, null);
				
				String z0165 = (String)list.get(11);
				dateFormat = bo.getDateFormat("z0165");
				if (!StringUtils.isEmpty(z0165)){
					Timestamp timeValue  =this.getTimeValue(dateFormat,z0165);
            		list.set(11, timeValue);
				}else
					list.set(11, null);
			}
			String itemid = "";
			String value = "";
			String z0101 = (String) list.get(list.size()-1);
			list.remove(list.size()-1);
			StringBuffer fieldsql = new StringBuffer();
			StringBuffer temp = new StringBuffer();
		    for (MorphDynaBean item : fields) {
		    	itemid = (String) item.get("itemid");
		    	value = (String) bean.get(itemid);
		    	if("1".equals(type)){
		    		if("z0129".equalsIgnoreCase(itemid))
		    			continue;
		    		fieldsql.append(itemid+",");
		    	}
		    	else
		    		fieldsql.append(","+itemid+"=?");
		    	
		    	temp.append("?,");
		    	if("D".equals(item.get("itemtype"))){
		    		dateFormat = bo.getDateFormat(itemid);
		    		if(StringUtils.isNotEmpty(value))//value为空字段时DateUtils.getSqlDate会报错
		    			list.add(DateUtils.getTimestamp(value, dateFormat));
		    		else
		    			list.add(value);
		    	}else if("A".equals(item.get("itemtype"))&&!"0".equals(item.get("codesetid"))){
		    		list.add(value.split("`")[0]);
		    	}else if("N".equals(item.get("itemtype"))){
		    		list.add(StringUtils.isEmpty(value)?0:value);
		    	}else
		    		list.add(value);
			}
		    list.add(z0101);
			if("1".equals(type)){
				sql.append("insert into z01 (z0103,z0105,z0151,z0153,z0107,z0109,z0155,z0157,z0159,z0161,z0163,z0165,");
				sql.append(fieldsql);
				sql.append("z0101,z0129) values(?,?,?,?,?,?,?,?,?,?,?,?,?,");
				sql.append(temp);
				sql.append("'01')");
				dao.insert(sql.toString(),list);
			}else{
				sql.append("update z01 set ");
				sql.append("z0103=?,z0105=?,z0151=?,z0153=?,z0107=?,z0109=?,z0155=?,z0157=?,z0159=?,z0161=?,z0163=?,z0165=?");
				sql.append(fieldsql);
				sql.append(" where z0101=?");
				dao.update(sql.toString(), list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 加载修改招聘批次页面
	 * @param id
	 * @param fields 自定义指标
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getRecruitBatchList(String id, ArrayList<FieldItem> fields) throws GeneralException {
		ArrayList list = new ArrayList();
		RowSet rs = null;
		RowSet rs2 = null;
		RecruitUtilsBo bo = new RecruitUtilsBo(conn);
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			StringBuffer sql = new StringBuffer();
			sql.append("select z.z0103,z.z0105,z.z0151,z.z0153,z.z0107,z.z0109,z.z0155,z.z0157,z.z0159,z.z0161,z.z0163,z.z0165,o.codeitemdesc");
			String codesetid = "0";
			String codeitemid = "";
			for (FieldItem item : fields) {
				codesetid = item.getCodesetid();
				codeitemid = item.getItemid();
				sql.append(",z."+item.getItemid());
				if("A".equals(item.getItemtype())&&!"0".equals(codesetid)){
					if(!"UN".equals(codesetid)&&!"UM".equals(codesetid)&&!"@K".equals(codesetid)){
						sql.append(",z."+codeitemid);
						sql.append(",(select codeitemdesc from codeitem where codesetid='"+codesetid+"'");
						sql.append(" and codeitemid="+codeitemid+") "+codeitemid+"Desc");
					}else{
						sql.append(",z."+codeitemid);
						sql.append(",(select codeitemdesc from organization where codesetid='"+codesetid+"'");
						sql.append(" and codeitemid="+codeitemid+") "+codeitemid+"Desc");
					}
				}
			}
			sql.append(" from z01 z,organization o");
			sql.append(" where z.z0105=o.codeitemid");
			sql.append(" and z.z0101=?");
			
			ArrayList params = new ArrayList();
			params.add(id);
			
			rs = dao.search(sql.toString(), params);
			sql.setLength(0);
			params.clear();
			sql.append("select * from zp_flow_definition where flow_id=?");
			HashMap map = new HashMap();
			ArrayList lists = new ArrayList();
			ArrayList lengthList = new ArrayList();
			String dateFormat = "yyyy-MM-dd";
			String z0153 = "";
			Boolean flag = true;
			while(rs.next()){
				list.add(rs.getString("z0103"));
				list.add(rs.getString("z0105")+"`"+rs.getString("codeitemdesc"));
				list.add(rs.getString("z0151"));
				params.add(rs.getString("z0153")==null?"":rs.getString("z0153"));
				rs2 = dao.search(sql.toString(), params);
				if(rs2.next()) {
					boolean priv = this.userview.hasTheFunction("3110002");
					if(!priv) {
						flag = false;
						ArrayList RecruitList = getRecruitChannelOrFlow("2",null);
						for(int i=0;i<RecruitList.size();i++){
							z0153 = (String) ((HashMap) RecruitList.get(i)).get("itemid");
							if(z0153.equalsIgnoreCase(rs.getString("z0153"))) {
								flag = true;
							}
						}
					}
					
					if(flag) {
						list.add(rs.getString("z0153"));
					}else {
						list.add(rs2.getString("name"));
					}	
				}else
					list.add("");
				
				dateFormat = bo.getDateFormat("z0107");
				sdf = new SimpleDateFormat(dateFormat);
				lengthList.add(dateFormat.length());
				if (rs.getDate("z0107") != null)
				    list.add(sdf.format(rs.getTimestamp("z0107")));
				else {
                    list.add(null);
                }
				
				dateFormat = bo.getDateFormat("z0109");
				sdf = new SimpleDateFormat(dateFormat);
				lengthList.add(dateFormat.length());
				if (rs.getDate("z0109") != null)
				    list.add(sdf.format(rs.getTimestamp("z0109")));
				else {
				    list.add(null);
				}
				
				dateFormat = bo.getDateFormat("z0155");
				sdf = new SimpleDateFormat(dateFormat);
				lengthList.add(dateFormat.length());
				if (rs.getDate("z0155") != null)
				    list.add(sdf.format(rs.getTimestamp("z0155")));
				else {
				    list.add(null);
				}
				
				dateFormat = bo.getDateFormat("z0157");
				sdf = new SimpleDateFormat(dateFormat);
				lengthList.add(dateFormat.length());
				if (rs.getDate("z0157") != null)
				    list.add(sdf.format(rs.getTimestamp("z0157")));
				else {
                    list.add(null);
                }
				
				dateFormat = bo.getDateFormat("z0159");
				sdf = new SimpleDateFormat(dateFormat);
				lengthList.add(dateFormat.length());
				if (rs.getDate("z0159") != null)
				    list.add(sdf.format(rs.getTimestamp("z0159")));
				else {
                    list.add(null);
                }
				
				dateFormat = bo.getDateFormat("z0161");
				sdf = new SimpleDateFormat(dateFormat);
				lengthList.add(dateFormat.length());
				if (rs.getDate("z0161") != null)
				    list.add(sdf.format(rs.getTimestamp("z0161")));
				else {
                    list.add(null);
                }
				
				dateFormat = bo.getDateFormat("z0163");
				sdf = new SimpleDateFormat(dateFormat);
				lengthList.add(dateFormat.length());
				if (rs.getDate("z0163") != null)
				    list.add(sdf.format(rs.getTimestamp("z0163")));
				else {
                    list.add(null);
                }
				
				dateFormat = bo.getDateFormat("z0165");
				sdf = new SimpleDateFormat(dateFormat);
				lengthList.add(dateFormat.length());
				if (rs.getDate("z0165") != null)
					list.add(sdf.format(rs.getTimestamp("z0165")));
				else {
                    list.add(null);
                }
				
				list.add(rs.getString("codeitemdesc"));
				String itemid = "";
				for (FieldItem item : fields) {
					itemid = item.getItemid();
					map = new HashMap();
					if("D".equals(item.getItemtype())){
						dateFormat = bo.getDateFormat(itemid);
		            	sdf = new SimpleDateFormat(dateFormat);
						map.put(itemid, rs.getDate(itemid)==null?"":sdf.format(rs.getTimestamp(itemid)));
					}else{
						if(!"0".equals(item.getCodesetid()))
							map.put(itemid, StringUtils.isEmpty(rs.getString(itemid))?"":rs.getString(itemid)+"`"+rs.getString(itemid+"Desc"));
						else
							map.put(itemid,rs.getString(itemid)==null?"":rs.getString(itemid));
					}
					lists.add(map);
				}
				list.add(lists);
				list.add(lengthList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
		    PubFunc.closeDbObj(rs);
		    PubFunc.closeDbObj(rs2);
		}
		return list;
	}
	/**
	 * 删除招聘批次
	 * @param ids 选中的行
	 * @throws GeneralException 
	 */
	public ArrayList deleteRecruitBatch(ArrayList ids) throws GeneralException {
		ArrayList aList = new ArrayList();
		String flag = "1";
		String z0103="";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			for(int i=0;i<ids.size();i++){
				ArrayList list = new ArrayList();
				list.add(ids.get(i));
				boolean zflag = false;
				boolean eflag = false;
				String sql = "select count(z0101) as z0101 from z03 where z0101=?";
				RowSet rs = dao.search(sql, list);
				while(rs.next()){
					int z0101 = Integer.parseInt(rs.getString("z0101"));
					if(z0101>0){
						zflag=true;
					}
				}
				sql = "select batch_id as z0101 from zp_exam_hall where batch_id=?";
				rs = dao.search(sql, list);
				if(rs.next()){
					int z0101 = Integer.parseInt(rs.getString("z0101"));
					if(z0101>0){
						eflag=true;
					}
				}
				if(zflag==false&&eflag==false){
					sql = "delete from z01 where z0101=?";
					dao.delete(sql, list);
				}else{
					sql="select z0103 from z01 where z0101=?";
					rs = dao.search(sql, list);
					while(rs.next()){
						z0103 = rs.getString("z0103");
						aList.add(z0103);
					}
					flag="2";
				}
			}
			aList.add(flag);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return aList;
	}
	/**
	 * 发布或结束招聘批次
	 * @param ids  选中的批次
	 * @param type=1 发布批次 =2 结束批次
	 * 04已发布 06结束
	 * @throws GeneralException 
	 */
	public void publishOrCloseRecruitBatch(ArrayList ids, String type) throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			for(int i=0;i<ids.size();i++){
				ArrayList list = new ArrayList();
				String sql = "";
				if("1".equals(type)){
					sql="update z01 set z0129 ='04' where z0101=?";
				}else{
					sql="update z01 set z0129 ='06' where z0101=?";
				}
				list.add(ids.get(i));
				dao.update(sql, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	 /**
     * 查询权限下所有的招聘批次(已发布和起草的)
     * @param flag: 1、查询已发布和已结束       2、查询已发布和起草         3、查询起草           4、查询已结束          5、查询已发布       6、查询全部
     * @return
     * @throws GeneralException
     */
    public ArrayList getAllBatchInfos(String flag) throws GeneralException{
    	
    	ArrayList res = new ArrayList();
    	RecruitPrivBo rpb = new RecruitPrivBo();
    	
    	RowSet rs = null;
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer sql = new StringBuffer();
    		sql.append("select DISTINCT Z01.Z0101 batchId,Z0103 batchName,Z0129 from Z01 ");
    		
    		if("1".equals(flag)){
				sql.append(" ,Z03 where z0129 in ('06','04')");
				sql.append(" and Z0381 in (select flow_id from zp_flow_links where node_id='03')");
				sql.append("  and Z01.Z0101=Z03.Z0101 ");
    		}
    		else if("2".equals(flag))
    			sql.append(" where z0129 in ('01','04')");
    		else if("3".equals(flag))
    			sql.append(" where z0129 = '01'");
    		else if("4".equals(flag))
    			sql.append(" where z0129 = '06'");
    		else if("5".equals(flag)){
    			sql.append(" where z0129 = '04'");
    			RecruitPrivBo privBo = new RecruitPrivBo();
    			HashMap<String, Object> parame = privBo.getChannelPrivMap(userview, conn);
    	    	boolean setFlag = (Boolean) parame.get("setFlag");
    	    	if(setFlag) {
    	    		sql.append(" and (1=2 ");
    	    		ArrayList<String> hirePriv = (ArrayList<String>) parame.get("hirePriv");
    	    		if(hirePriv.size()>0) {
    		    		for (String hire : hirePriv) {
    		    			sql.append(" or (Z0151 like '" + hire + "%' ");
    		    			sql.append(")");
    					}
    		    	}
    	    		sql.append(")");
    	    	}
    		}else
    			sql.append(" where z0129 in ('01','04','06')");
    		
    		sql.append(" and ").append(rpb.getPrivB0110Whr(userview, "z0105", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD));
    		sql.append(" order by z0129,Z01.Z0101 desc");
    		
    		rs = dao.search(sql.toString());
    		
    		CommonData commonData = null;
    		while(rs.next()){
    			commonData = new CommonData();
    			commonData.setDataName(rs.getString("batchName"));
    			commonData.setDataValue(rs.getString("batchId"));
    			res.add(commonData);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return res;
    }
    
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public UserView getUserview() {
		return userview;
	}

	public void setUserview(UserView userview) {
		this.userview = userview;
	}
	
	/**
	 * 获取需要增加的指标
	 * @return
	 */
	public ArrayList getField(){
		ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("Z01", 1);
		ArrayList list = new ArrayList();
		String fields = ",z0101,z0103,z0105,z0151,z0153,z0107,z0109,z0155,z0157,z0159,z0161,z0163,z0165,";
		for (FieldItem obj : fieldList) {
			if(!fields.contains(obj.getItemid().toLowerCase())&&"1".equals(obj.getState()))
				list.add(obj);
		}
		return list;
	}
	
	//根据招聘批次中日期格式集合
	public ArrayList getFormatList(){
		ArrayList list = new ArrayList();
		RecruitUtilsBo bo = new RecruitUtilsBo(conn);
		String dateFormat = bo.getDateFormat("z0107");
		list.add(dateFormat.length());
		
		dateFormat = bo.getDateFormat("z0109");
		list.add(dateFormat.length());
		
		dateFormat = bo.getDateFormat("z0155");
		list.add(dateFormat.length());
		
		dateFormat = bo.getDateFormat("z0157");
		list.add(dateFormat.length());
		
		dateFormat = bo.getDateFormat("z0159");
		list.add(dateFormat.length());
		
		dateFormat = bo.getDateFormat("z0161");
		list.add(dateFormat.length());
		
		dateFormat = bo.getDateFormat("z0163");
		list.add(dateFormat.length());
		
		dateFormat = bo.getDateFormat("z0165");
		list.add(dateFormat.length());
		
		return list;
	}
	
	//根据招聘批次中日期格式自动补全时分秒
	public Timestamp getTimeValue(String dateFormat ,String timeValue){
		Timestamp time = null; 
		if("yyyy-MM-dd".equalsIgnoreCase(dateFormat)){
			timeValue =  timeValue + " 23:59:59";
			time = DateUtils.getTimestamp(timeValue, "yyyy-MM-dd HH:mm:ss");
		}else if("yyyy-MM-dd HH:mm".equalsIgnoreCase(dateFormat)){
			timeValue =  timeValue + ":59";
			time = DateUtils.getTimestamp(timeValue, "yyyy-MM-dd HH:mm:ss");
		}else
			time = DateUtils.getTimestamp(timeValue, dateFormat);
		return time;
	}
	
	/**
	 * 根据渠道权限获取默认待办、邮件接收人
	 * @param batchIds 招聘批次
	 * @return 默认选中接收人 0 自主用户+角色关联用户 ，1业务用户
	 */
	public ArrayList getDefPerson(ArrayList<String> batchIds) {
		RowSet rs = null;
		RowSet search = null;
		RecruitUtilsBo utilBo = new RecruitUtilsBo(this.conn);
		ZpPendingtaskBo taskBo = new ZpPendingtaskBo(this.conn, this.userview);
		ArrayList defPerson = new ArrayList();
		try {
			ArrayList<HashMap> selfuser = new ArrayList<HashMap>();
			ArrayList<HashMap> bususer = new ArrayList<HashMap>();
			//记录重复信息
			ArrayList<String> temp = new ArrayList<String>();
			ContentDAO dao = new ContentDAO(this.conn);
			RecruitPrivBo bo = new RecruitPrivBo();
			PositionBo posbo = new PositionBo(conn, dao, userview);
			//获取招聘渠道权限参数
			HashMap<String, HashMap> hireParames = bo.parseHireParames(this.conn);
			StringBuffer sql = new StringBuffer("select z0101, z0103, z0151 from z01 where z0101 in(");
			int n = 0;
			do {
				n++;
				sql.append("?,");
			}while(n<batchIds.size());
			sql.setLength(sql.length()-1);
			sql.append(")");
			rs = dao.search(sql.toString(), batchIds);
			String[] nbase = utilBo.getNbase();
			while(rs.next()) {
				String channel = rs.getString("z0151");
				HashMap<String, String> parames = hireParames.get(channel);
				if(parames==null)
					continue;
		    	String emp_id = parames.get("emp_id");
		    	String role_id = parames.get("role_id");
		    	String user_name = parames.get("user_name");
		    	ArrayList<String> values = new ArrayList<String>();
		    	StringBuffer whereSql = new StringBuffer();
		    	//人员
		    	if(StringUtils.isNotEmpty(emp_id)) {
		    		String[] split = emp_id.split(",");
		    		for (String empid : split) {
		    			whereSql.append("?,");
		    			values.add(empid);
					}
		    		whereSql.setLength(whereSql.length()-1);
		    		for(int i = 0; i<nbase.length; i++) {
		    			search = dao.search("select a0100,a0101 from "+nbase[i]+"A01 where guidkey in("+whereSql+")", values);
		    			while(search.next()) {
		    				String personId = nbase[i]+search.getString("a0100");
		    				String userName = taskBo.getUserName(personId);
		    				if(temp.contains(personId))
		    					continue;
			    			if(StringUtils.isNotEmpty(userName)) {
			    				UserView userView=new UserView(userName,this.conn);
			    				userView.canLogin(false);
			    				/*给有新建职位权限的人发待办*/
			    	        	if(userView.hasTheFunction("3110105")||userView.isSuper_admin()) {
			    	        		HashMap valueMap = new HashMap();
			    					String photoPath = posbo.getPhotoPath(nbase[i],search.getString("a0100"));
		    						temp.add(personId);
			    					valueMap.put("id", PubFunc.encrypt(personId));
			    					valueMap.put("name", search.getString("a0101"));
			    					valueMap.put("photo", photoPath);
			    					selfuser.add(valueMap);
			    	        	}
			    			}
		    			}
		    		}
		    	}
		    	values.clear();
		    	//角色
		    	if(StringUtils.isNotEmpty(role_id)) {
		    		String[] split = role_id.split(",");
		    		whereSql.setLength(0);
		    		for (String roleId : split) {
		    			whereSql.append("?,");
		    			values.add(roleId);
					}
		    		whereSql.setLength(whereSql.length()-1);
		    		search = dao.search("select * from T_SYS_STAFF_IN_ROLE where role_id in("+whereSql+")", values);
		    		while(search.next()) {
		    			if("1".equals(search.getString("status"))) {
		    				String staff_id = search.getString("staff_id");
		    				String userName = taskBo.getUserName(staff_id);
		    				if(temp.contains(staff_id))
		    					continue;
			    			if(StringUtils.isNotEmpty(userName)) {
			    				UserView userView=new UserView(userName,this.conn);
			    				userView.canLogin(false);
			    				/*给有新建职位权限的人发待办*/
			    	        	if(userView.hasTheFunction("3110105")||userView.isSuper_admin()) {
			    	        		HashMap valueMap = new HashMap();
			    	        		temp.add(staff_id);
			    					String photoPath = posbo.getPhotoPath(staff_id.substring(0,3),staff_id.substring(3));
			    					valueMap.put("id", PubFunc.encrypt(staff_id));
			    					valueMap.put("name", userView.getUserFullName());
			    					valueMap.put("photo", photoPath);
			    					selfuser.add(valueMap);
			    	        	}
			    			}
		    			}
	    			}
		    	}
		    	//用户
		    	if(StringUtils.isNotEmpty(user_name)) {
		    		String[] split = user_name.split(",");
		    		List<String> asList = Arrays.asList(split);
		    		for (String userName : asList) {
		    			UserView userView=new UserView(userName,this.conn);
						userView.canLogin(false);
						/*给有新建职位权限的人发待办*/
	    	        	if(userView.hasTheFunction("3110105")||userView.isSuper_admin()) {
	    	        		HashMap valueMap = new HashMap();
	    	        		valueMap.put("id", PubFunc.encrypt(userName));
	    					valueMap.put("name", userView.getUserFullName());
	    					valueMap.put("photo", "/components/personPicker/image/male.png");
	    					bususer.add(valueMap);
	    	        	}
					}
		    	}
			}
			
			defPerson.add(selfuser);
			defPerson.add(bususer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(search);
		}
		return defPerson;
	}
	
}
