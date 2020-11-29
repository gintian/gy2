package com.hjsj.hrms.module.hire.businessobject;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.ResumeFilterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class PositionBo {
	
	private Connection conn;
	private ContentDAO dao;
	private UserView userview;
	private static String dbName;//招聘库
	private String newHire;

	public void setNewHire(String newHire) {
		this.newHire = newHire;
	}

	public void setUserview(UserView userview) {
		this.userview = userview;
	}

	public PositionBo(Connection conn) {
		this.conn = conn;
		this.dao = new ContentDAO(conn);
		this.getZpkdbName();
	}
	
	public PositionBo(Connection conn, UserView userview) {
		this(conn);
		this.userview = userview;
	}
	
	// 得到招聘应用库
    public String getZpkdbName(){
        if (StringUtils.isEmpty(dbName)) {
            try {
                RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
                if (vo == null)
                    throw GeneralExceptionHandler.Handle(new Exception("后台参数没有设置应聘人才库"));
                dbName = vo.getString("str_value");
                if (StringUtils.isEmpty(dbName))
                    throw GeneralExceptionHandler.Handle(new Exception("后台参数没有设置应聘人才库"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dbName;
    }
	
	/**
	 * 根据招聘前台参数 外网职位列表显示指标设置获取招聘指标列表
	 * @return
	 */
	public ArrayList getPositionColumns() {
		EmployNetPortalBo bo = new EmployNetPortalBo(conn);
		ArrayList<LazyDynaBean> posTemp = bo.getPosListField();
		ArrayList<LazyDynaBean> posFieldList = new ArrayList<LazyDynaBean>();
		for (LazyDynaBean obj : posTemp) {
			String itemid = (String) obj.get("itemid");
			String itemtype = (String) obj.get("itemtype");
			String itemdesc = (String) obj.get("itemdesc");
			String codesetid = (String) obj.get("codesetid");
			String deciwidth = (String) obj.get("deciwidth");
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("itemid", itemid);
			bean.set("itemtype", itemtype);
			bean.set("itemdesc", itemdesc);
			bean.set("codesetid", codesetid);
			bean.set("deciwidth", deciwidth);
			posFieldList.add(bean);
		}
		return posFieldList;
	}
	
	/**
	 * 获取招聘职位信息列表
	 * @param hireChannel 招聘渠道
	 * @param queryItem 招聘外网查询条件
	 * @return
	 */
	public ArrayList getPositionDataList(String hireChannel, ArrayList queryItem) {
		EmployNetPortalBo bo = new EmployNetPortalBo(conn);
		if(this.userview != null)
		    bo.setLoginUserName(this.userview.getUserName());
		//传了这个参数后招聘职位名称前不拼接 部门
		bo.setRecruitservice(1);
		ArrayList<LazyDynaBean> zpPosTemp=new ArrayList<LazyDynaBean>();
		ArrayList<LazyDynaBean> zpPosList=new ArrayList<LazyDynaBean>();
		try {
			//标识查询指标中是否有值 false为空
			boolean flag = false;
			if(queryItem!=null && queryItem.size()>0) {
				ArrayList<LazyDynaBean> conditionFieldList = new ArrayList<LazyDynaBean>();
				for(int i=0; i<queryItem.size(); i++){
					MorphDynaBean bean = (MorphDynaBean) queryItem.get(i);
					HashMap<String,String> item = PubFunc.DynaBean2Map(bean);
					String itemid = item.get("itemid");
					String value = item.get("value");
					if(StringUtils.isNotBlank(value))
						flag = true;
					FieldItem fieldItem = DataDictionary.getFieldItem(itemid, "Z03");
					String itemtype = fieldItem.getItemtype();
	                LazyDynaBean e = new LazyDynaBean();
	                e.set("itemid", itemid);
	                e.set("value", value==null?"":value);
	                e.set("itemtype", "@k".equalsIgnoreCase(itemtype)?"UM":itemtype);
	                e.set("codesetid", fieldItem.getCodesetid());
	                conditionFieldList.add(e);
				}
				if(flag) {
					ArrayList unitList=new ArrayList();
					HashMap unitPosMap=bo.getPositionInterviewMap3("",conditionFieldList,unitList,hireChannel,"");
					zpPosTemp = bo.getUnitList(unitPosMap,unitList,"");//如果是岗位搜索的话 只显示搜索的岗位
				}else
					zpPosTemp = bo.getZpPostList(new ArrayList(), hireChannel, "");
			}else {
				zpPosTemp = bo.getZpPostList(new ArrayList(), hireChannel, "");
			}
            for (LazyDynaBean bean : zpPosTemp) {
            	ArrayList<LazyDynaBean> list = (ArrayList) bean.get("list");
            	if(list != null && list.size() > 0) {
	            	for (LazyDynaBean object : list) {
	            		String z0301 = (String) object.get("z0301");
	            		// 前边得到的id有已经加密的情况
	            		object.set("z0301", z0301.length()<=10 ? PubFunc.encrypt(z0301) : z0301);
            			if("true".equals(newHire)&&object.get("z0321")!=null&&object.get("z0321Name")!=null)
            				object.set("z0321", (String)object.get("z0321Name"));
	            		
	            		zpPosList.add(object);
					}
            	}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return zpPosList;
	}
	
	/**
	 * 获取快速查询指标
	 * @return
	 */
	public ArrayList getQueryItems() {
		String param = "pos_query";
		ArrayList queryitems = new ArrayList();
        try {
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
            HashMap map = parameterXMLBo.getAttributeValues();
			String zpOrgShowStyle = SystemConfig.getPropertyValue("zp_org_show_style");
            String posQueryFieldIDs = "";
            if (map.get(param) != null && ((String) map.get(param)).trim().length() > 0)
                posQueryFieldIDs = (String) map.get(param);
            
            ResumeBo bo = new ResumeBo(conn);
            if (posQueryFieldIDs.length() > 1) {
            	String[] fields = posQueryFieldIDs.split("`");
            	for(int i = 0; i<fields.length; i++) {
            		FieldItem fieldItem = DataDictionary.getFieldItem(fields[i], "Z03");
            		LazyDynaBean abean = new LazyDynaBean();
            		String codesetid = fieldItem.getCodesetid();
    				String layer = "0";
                    abean.set("itemid", fieldItem.getItemid());
                    abean.set("itemdesc", fieldItem.getItemdesc());
                    abean.set("itemtype", fieldItem.getItemtype());
                    abean.set("zpOrgShowStyle",  zpOrgShowStyle);
                    abean.set("codesetid", codesetid);
                    if(codesetid!=null&&!"0".equals(codesetid)) {
    					layer = bo.getCodeSetLayer(codesetid);
    				}
                    abean.set("layer", layer);
                    abean.set("itemlength", fieldItem.getItemlength());
                    queryitems.add(abean);
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryitems;
	}
	
	/**
	 * 根据招聘前台参数获取职位描述指标
	 * @return
	 */
	public ArrayList getPositionInfoColumns(String zpPosId) {
		ArrayList<LazyDynaBean> posDescFiledList = this.getPosDescFiledList(zpPosId);
		ArrayList<LazyDynaBean> columns = new ArrayList<LazyDynaBean>();
		for (LazyDynaBean obj : posDescFiledList) {
			LazyDynaBean bean = new LazyDynaBean();
			String itemid = (String) obj.get("itemid");
			String desc = (String) obj.get("desc");
			String type = (String) obj.get("type");
			bean.set("itemid", itemid);
			bean.set("itemdesc", desc);
			bean.set("itemtype", type);
			columns.add(bean);
		}
		
		return columns;
	}
	/**
	 * 获取职位描述信息
	 * @param zpPosId
	 * @return
	 */
	public ArrayList getPositionInfo(String zpPosId) {
		ArrayList<LazyDynaBean> positionInfo = new ArrayList<LazyDynaBean>();
		try {
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.conn);
			//传了这个参数后招聘职位名称前不拼接 部门
			employNetPortalBo.setRecruitservice(1);
			ArrayList<LazyDynaBean> posDescFiledList = this.getPosDescFiledList(zpPosId);
			for (LazyDynaBean obj : posDescFiledList) {
				LazyDynaBean bean = new LazyDynaBean();
				String itemid = (String) obj.get("itemid");
				String value = (String) obj.get("value");
				bean.set(itemid, value);
				positionInfo.add(bean);
			}
			//判断是否已应聘，或者收藏职位
			//应聘状态0 未操作，1已应聘，2已收藏
			String state = "0";
			if(this.userview!=null) {
				state = this.isApplyPos(this.userview.getA0100(), zpPosId);
			}
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("state", state);
			positionInfo.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return positionInfo;
	}
	
	/**
	 * 获取职位详细信息
	 * @param zpPosId
	 * @return
	 */
	private ArrayList getPosDescFiledList(String zpPosId) {
		ArrayList posDescFiledList = new ArrayList();
		try {
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.conn);
			HashMap fieldMap=new HashMap();
			ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			for(int i=0;i<list.size();i++)
			{
				FieldItem item=(FieldItem)list.get(i);
				fieldMap.put(item.getItemid().toLowerCase(),item.getItemdesc()+"^"+item.getItemtype()+"^"+item.getCodesetid());
			}
			posDescFiledList=employNetPortalBo.getPosDescFiledList(zpPosId,fieldMap);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return posDescFiledList;
		
	}
	
	/**
	 * 判断此职位是否已应聘
	 * @param a0100
	 * @param zpPosId
	 * @return 0 未操作，1已应聘，2已收藏，6已结束的职位
	 */
	private String isApplyPos(String a0100 ,String zpPosId) {
		RowSet rs = null;
		String state = "0";
		try {
			String sql = "select 1 from zp_pos_tache where A0100=? and ZP_POS_ID=? and nbase=?";
			ArrayList<String> values = new ArrayList<String>();
			values.add(a0100);
			values.add(zpPosId);
			values.add(dbName);
			for(int i = 0;i < 2; i++) {
				if(i==0) {
					rs = dao.search(sql,values);
					if(rs.next())
						state = "1";
				}else {
					sql = "select 1 from zp_pos_collection where A0100=? and ZP_POS_ID=? and nbase=?";
					rs = dao.search(sql,values);
					if(rs.next()) {
						if("1".equals(state))
							state += ",2";
						else
							state = "2";
					}
					if(this.isFinishPos(zpPosId))
						state += ",6";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return state;
		
	}
	
	/**
	 * 检查职位是否可以应聘
	 * @param zpPosId 职位id
	 * @return
	 */
	public String testPosition(String zpPosId) {
		String return_code = "success";
		RowSet rs = null;
		try {
			com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo positionBo = new com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo(
					this.conn, dao, this.userview);
			HashMap res = positionBo.isTimeOut(zpPosId);
			if (res.get("info") != null) {
				if (res.get("isTimeOut") != null && (Boolean) res.get("isTimeOut")) {
					if ("before".equalsIgnoreCase((String) res.get("info")))
						return_code = "before";
					if ("after".equalsIgnoreCase((String) res.get("info")))
						return_code = "after";
				}
			}
			if (!"success".equals(return_code)) {
				return return_code;
			}
			String sql = "select z0319 from z03 where z0301=?";
			ArrayList<String> list = new ArrayList<String>();
			list.add(zpPosId);
			rs = dao.search(sql, list);
			if (rs.next()) {
				String z0319 = rs.getString("z0319");
				switch (z0319) {
				case "04":
					return_code = "success";
					break;
				case "06":
					return_code = "finished";
					break;
				case "09":
					return_code = "paused";
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		} finally {
			PubFunc.closeResource(rs);
		}
		return return_code;
	}
	
	/**
	 * 校验是否超出了最大职位申请数
	 * @param zpPosId
	 * @param a0100
	 * @return
	 */
	public HashMap<String, Object> checkMaxcount(String zpPosId,String a0100) {
		RowSet rs = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		boolean flag =false;
		try {
			/**统计用户申请职位总数时，不计算申请的没有通过的职位*/
			//应聘职位数：不包括已淘汰的应聘职位
			String appPosNumStatus = " not in ('0105','0106','0205','0206','0306','0307','0308','0406','0407','0408','0506','0507','0508','0603','0604','0703','0704','0805','0806','1004','1005')";
			String zpPrePosId = "";
			String zpPrePos = SystemConfig.getPropertyValue("zp_pre_pos");
			if(StringUtils.isNotEmpty(zpPrePos) && zpPrePos.split(":").length >1)
			    zpPrePosId = zpPrePos.split(":")[1];
			
			String sqlPosId = "";
			if(StringUtils.isNotEmpty(zpPrePosId))
			    sqlPosId =" and zp_pos_id<>?";
			
			//各职位已淘汰的简历不计算到应聘职位数量中，供控制申请职位数量限制使用
			//先获取到当前要应聘职位的批次号
			String getPcSql = "select z0101 from z03 where z0301=?";
			List<String> posidList = new ArrayList<String>();
			posidList.add(zpPosId);
			rs = dao.search(getPcSql,posidList);
			String z0101 = "#";
			if(rs.next()) {
			    z0101 = rs.getString("z0101");
			}
			if(StringUtils.isBlank(z0101)) {
			    z0101 = "#";
			}
			PubFunc.closeResource(rs);
			/**获取最大应聘职位数**/
			ParameterXMLBo bo2 = new ParameterXMLBo(this.conn,"1");
			HashMap amap = bo2.getAttributeValues();
			int maxSize = 3;
			if(amap!=null && amap.get("max_count")!=null && ((String)amap.get("max_count")).trim().length()>0)
			    maxSize = Integer.parseInt((String)amap.get("max_count"));
			
			StringBuffer getAcceptSql = new StringBuffer();
			ArrayList<String> paramList = new ArrayList<String>();
			getAcceptSql.append("select count(*) num from zp_pos_tache zt left join z03 on zp_pos_id=z0301 ");
			getAcceptSql.append(" where zt.a0100=? and zt.nbase=?");
			paramList.add(a0100);
			paramList.add(dbName);
			getAcceptSql.append(" and "); 
			getAcceptSql.append(Sql_switcher.isnull("resume_flag", "'#'"));
			getAcceptSql.append(appPosNumStatus);
			if(StringUtils.isNotEmpty(sqlPosId)) {
			    getAcceptSql.append(sqlPosId);
			    paramList.add(zpPrePosId);
			}
			getAcceptSql.append(" and (");
			getAcceptSql.append(Sql_switcher.isnull("z0101", "'#'"));
			getAcceptSql.append("=?");
			if(Sql_switcher.searchDbServer()==1 && StringUtils.equals("#", z0101)) {//sqlserver 要再处理一下空格
			    getAcceptSql.append(" or z0101=''");
			}
			getAcceptSql.append(")");
			paramList.add(z0101);
			getAcceptSql.append(" group by z03.z0101 ");
			
			rs = dao.search(getAcceptSql.toString(), paramList);
			int size =0 ;//默认从来没有应聘过
			if(rs.next()) {//如果有数据查询出来,取当前批次当前人员应聘了几个职位
			    size = rs.getInt(1);
			}
			
		    if(size<maxSize) 
		    	flag = true;
			 
			map.put("size", size);
			map.put("flag", flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
	}
	
	/**
	 * 计算各包括已淘汰的简历的应聘职位数量，供生成志愿号使用
	 * @param a0100
	 * @return
	 */
	public int getSizeJobs(String a0100, String z0301) {
		RowSet rs = null;
		//计算各包括已淘汰的简历的应聘职位数量，供生成志愿号使用
		int sizeJobs = 0; 
		try {
			//志愿号：包括已淘汰的应聘职位
			String thenumberStatus = " not in ('0106','0206','0307','0308','0407','0408','0507','0508','0604','0704','0805','0806','1004','1005')";
			//各职位已淘汰的简历不计算到应聘职位数量中
			StringBuffer sql = new StringBuffer();
			sql.append("select count(1) from zp_pos_tache zp,z03 ");
			sql.append(" where zp.ZP_POS_ID=z03.Z0301 ");
			sql.append(" and a0100=? "); 
			sql.append(" and nbase=? "); 
			sql.append(" and "+Sql_switcher.isnull("Z0101", "'#'")+"=(select "+Sql_switcher.isnull("Z0101", "'#'")+" from Z03 where Z0301=? ) "); 
			sql.append(" and "); 
			sql.append(Sql_switcher.isnull("resume_flag", "'#'"));
			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add(a0100);
			valueList.add(dbName);
			valueList.add(z0301);
			String zpPrePosId = "";
			String zpPrePos = SystemConfig.getPropertyValue("zp_pre_pos");
			if(StringUtils.isNotEmpty(zpPrePos) && zpPrePos.split(":").length >1)
			    zpPrePosId = zpPrePos.split(":")[1];
			
			String sqlPosId = "";
			if(StringUtils.isNotEmpty(zpPrePosId)) {
			    sqlPosId =" and zp_pos_id<>?";
			    valueList.add(zpPrePosId);
			}
			rs = dao.search(sql.toString() + thenumberStatus + sqlPosId, valueList);
			if(rs.next()){  
				sizeJobs = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return sizeJobs;
	}
	
	
	/**
	 * 应聘职位
	 * 返回应聘结果
	 * @param zpPosId
	 * @return
	 */
	public String applyPosition(String zpPosId) {
		RowSet rs = null;
		String return_code = "success";
		try {
			com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo positionBo=new com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo(this.conn,dao,this.userview);
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.conn);
			String a0100 = this.userview.getA0100();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map = checkMaxcount(zpPosId, a0100);
			boolean flag = (Boolean) map.get("flag");
			int size = (Integer) map.get("size");
			if(flag){
				RecordVo vo = new RecordVo("zp_pos_tache");
				vo.setString("zp_pos_id",zpPosId);
				vo.setString("a0100",a0100);
				vo.setString("nbase",dbName);
				int sizeJobs = getSizeJobs(a0100,zpPosId);
				vo.setInt("thenumber",sizeJobs +1);
				vo.setDate("apply_date",Calendar.getInstance().getTime());
				vo.setDate("recdate",Calendar.getInstance().getTime());
				vo.setString("status","0");
				vo.setInt("relation_type",1);
				dao.addValueObject(vo);
				
				employNetPortalBo.addStatInfo(2,zpPosId);
				//老招聘发送邮件通知功能
				/*DemandCtrlParamXmlBo dbo = new DemandCtrlParamXmlBo(this.conn,zpPosId);
				HashMap map = dbo.getAttributeValues("answer_mail");
				LazyDynaBean bean = (LazyDynaBean)map.get("answer_mail");
				if(bean!=null){
					String template_id = (String)bean.get("template");
					if(template_id!=null && template_id.trim().length()>0){
						String flag=(String)bean.get("flag");
						if(flag!=null && flag.equalsIgnoreCase("true")){
							AutoSendEMailBo bo = new AutoSendEMailBo(this.conn);
							bo.AutoSend(zpPosId,"",a0100,dbName,template_id);
						}
					}
				}*/
				//开始更新z03里面的数据,将新简历数目和所有简历数目更新一下
				positionBo.saveCandiatesNumber(zpPosId, 1);//1更新新简历数目
				positionBo.saveCandiatesNumber(zpPosId, 3);//3更新所有简历数目												       
				
				//更新当前的人员是否满足筛选状态
				ResumeFilterBo rbo = new ResumeFilterBo(this.conn, this.userview);
				ArrayList<String> z03list = new ArrayList<String>();
				z03list.add(zpPosId);
				rbo.updateSuitable(z03list, a0100);						        
				String sqlAccept_post="select accept_post from z03 where Z0301 =?"; 
				PubFunc.closeResource(rs);
				rs=dao.search(sqlAccept_post,z03list);
				if (rs.next()) {
				    String acceptPostSql = rs.getString("accept_post");
				    //判断是否选择自动接受职位申请
				    if ("1".equalsIgnoreCase(acceptPostSql)) {
				        String sqlResume = "select suitable from  zp_pos_tache where A0100 ='" + a0100 + "' and ZP_POS_ID ='" + zpPosId + "'";
						PubFunc.closeResource(rs);
				        rs = dao.search(sqlResume);
				        if (rs.next()) {
				            String suitable = rs.getString("suitable");
				            //判断是否满足职位筛选
				            if ("1".equalsIgnoreCase(suitable)) {
				                //默认自动接受职位申请
				                positionBo.applyPosition("1", a0100, this.userview.getUserFullName(), zpPosId, dbName, "1");
				                return_code = "6";
				            }
				        }
				    }
				}
				
			}else {
				return_code = "3`" +size;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		} finally {
			PubFunc.closeResource(rs);
		}
	
		return return_code;
	}
	
	/**
	 * 应聘职位指标列表
	 * @return
	 */
	public ArrayList<LazyDynaBean> getApplyedPosFileds() {
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		try {
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn);
			HashMap map = xmlBo.getAttributeValues();
			String appliedPosItems = "";//外网已申请职位列表显示指标集
			if(map.get("appliedPosItems")!=null)
				appliedPosItems = (String)map.get("appliedPosItems");
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("itemid", "z0301");
			bean.set("itemdesc", "职位编号");
			bean.set("itemtype", "A");
			bean.set("z03", true);
			list.add(bean);
			bean = new LazyDynaBean();
			bean.set("itemid", "z0351");
			bean.set("itemdesc", "职位名称");
			bean.set("itemtype", "A");
			bean.set("z03", true);
			list.add(bean);
			if(appliedPosItems.contains("z0329")) {
				bean = new LazyDynaBean();
				bean.set("itemid", "z0329");
				bean.set("itemdesc", "开始日期");
				bean.set("itemtype", "D");
				bean.set("z03", true);
				list.add(bean);
			}
			if(appliedPosItems.contains("z0333")) {
				bean = new LazyDynaBean();
				bean.set("itemid", "z0333");
				bean.set("itemdesc", "工作地点");
				bean.set("itemtype", "A");
				bean.set("z03", true);
				list.add(bean);
			}
			if(appliedPosItems.contains("z0315")) {
				bean = new LazyDynaBean();
				bean.set("itemid", "z0315");
				bean.set("itemdesc", "招聘人数");
				bean.set("itemtype", "N");
				bean.set("z03", true);
				list.add(bean);
			}
			if(appliedPosItems.contains("resume_state")) {
				bean = new LazyDynaBean();
				bean.set("itemid", "resume_state");
				bean.set("itemdesc", "简历状态");
				bean.set("itemtype", "A");
				bean.set("z03", false);
				list.add(bean);
			}
			bean = new LazyDynaBean();
			bean.set("itemid", "thenumber");
			bean.set("itemdesc", "志愿排名");
			bean.set("itemtype", "N");
			bean.set("z03", false);
			list.add(bean);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取已应聘职位信息列表
	 * 返回消息包括是否可以取消应聘
	 * @param showHistory 是否显示历史应聘职位：指批次已结束的职位
	 * @return
	 */
	public LinkedHashMap<String,ArrayList> getApplyedPositions(boolean showHistory) {
		RowSet rs = null;
		LinkedHashMap<String,ArrayList> map = new LinkedHashMap<String,ArrayList>();
		try {
			ArrayList<LazyDynaBean> fields = this.getApplyedPosFileds();
			ArrayList<FieldItem> useid = new ArrayList<FieldItem>();
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			for (LazyDynaBean bean : fields) {
				boolean isZ03 = (Boolean) bean.get("z03");
				if(!isZ03) {
				    continue;
				}
				String itemid = (String) bean.get("itemid");
				
				FieldItem item = DataDictionary.getFieldItem(itemid, "Z03");
				if(item!=null && "1".equals(item.getUseflag())) {
					useid.add(item);
					//sql中固定拼入z0351
					if("z0351".equalsIgnoreCase(itemid))
						continue;
					if("D".equalsIgnoreCase(item.getItemtype()))
						sql.append(Sql_switcher.dateToChar(itemid)+" "+itemid+",");
					else
						sql.append(itemid+",");
				}
			}
			sql.append(" z0351,thenumber,status,resume_flag,z0381,description,Z01.z0103,Z01.z0101 ");
			sql.append(" from zp_pos_tache zp,z03 left join Z01 on Z03.Z0101=Z01.Z0101 ");
			sql.append(" where z0301=zp.zp_pos_id ");
			if(showHistory)
            	sql.append(" and (Z0129='06' or ((Z03.Z0101 is null or Z03.Z0101='') and Z0319<>'04')) ");
            else
            	sql.append(" and (Z0129='04' or ((Z03.Z0101 is null or Z03.Z0101='') and Z0319='04')) ");
			sql.append(" and a0100=? order by z01.z0101,thenumber ");
			ArrayList<String> values = new ArrayList<String>();
			values.add(userview.getA0100());
			rs = dao.search(sql.toString(), values);
			ArrayList list = new ArrayList();
			while(rs.next()) {
				list = new ArrayList();
				LazyDynaBean bean = new LazyDynaBean();
				for (FieldItem item : useid) {
					String itemid = item.getItemid();
					String codesetid = item.getCodesetid();
					String value = rs.getString(item.getItemid())==null?"":rs.getString(item.getItemid());
					if(!"0".equals(codesetid)&&StringUtils.isNotEmpty(codesetid))
						bean.set(itemid, AdminCode.getCodeName(codesetid,value));
					else {
						if("z0301".equalsIgnoreCase(itemid))
							value = PubFunc.encrypt(value);
						bean.set(itemid, value);
					}
				}
				String thenumber = rs.getString("thenumber");
				bean.set("thenumber", thenumber);
				String resume_flag = rs.getString("resume_flag");
				if(StringUtils.isEmpty(resume_flag)) {
					String status = rs.getString("status");
					bean.set("resume_state", "2".equals(status)?"已拒绝":"未处理");
				}else {
					bean.set("resume_state", getResumeDesc(rs.getString("z0381"), resume_flag));
				}
				bean.set("posName", rs.getString("z0351"));
				bean.set("descripValue", rs.getString("description"));
				list.add(bean);
				String z0101 = rs.getString("z0101");
				String z0103 = rs.getString("z0103");
				z0101 = StringUtils.isEmpty(z0101)?"":z0101;
				z0103 = StringUtils.isEmpty(z0103)?"其他":z0103;
				if(map.get(z0103)!=null) {
					map.get(z0103).add(bean);
				}else
					map.put(z0103, list);
					
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(rs);
		}
		return map;
	}
	
	/**
	 * 应聘职位获取招聘状态
	 * @param z0381招聘流程
	 * @param resume_flag
	 * @return
	 * @throws SQLException
	 */
	private String getResumeDesc(String z0381, String resume_flag) {
		String resume_state = "";
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select s.custom_name from zp_flow_status s ");
			sql.append(" left join zp_flow_links l on s.link_id=l.id "); 
			sql.append(" where l.flow_id= ? and s.status=?");
			ArrayList<String> arg1 = new ArrayList<String>();
			arg1.add(z0381);
			arg1.add(resume_flag);
			rs = dao.search(sql.toString(), arg1);
			if(rs.next())
				resume_state = rs.getString("custom_name");
			
			if(StringUtils.isEmpty(resume_state))
				resume_state = AdminCode.getCodeName("36",resume_flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return resume_state;
	}
	
	/**
	 * 取消申请职位
	 * @param zpPosId
	 * @return
	 */
	public String cancelApply(String zpPosId) {
		String return_code = "success";
		RowSet rs = null;
		RowSet rs2 =null;
		try {
			String a0100 = this.userview.getA0100();
			StringBuffer sql = new StringBuffer();
			ArrayList<String> sqlParams = new ArrayList<String>();
			
			//人员入职之后不允许继续操作职位!
			sql.append("select 1 from ").append(dbName).append("A01 a01 left join zp_pos_tache zpt");
			sql.append(" on a01.a0100=zpt.a0100");
			sql.append(" where zpt.resume_flag='0903'");
			sql.append(" and a01.a0100=?");
			sqlParams.add(a0100);
			rs = dao.search(sql.toString(), sqlParams);
			if(rs.next()){
			    //该用户已入职，不允许继续操作职位
				return_code = "cannot";;
				return return_code;
			}
			
			sql.setLength(0);
            sql.append("select * from zp_pos_tache");
            sql.append(" where a0100=?");
            sql.append(" and nbase=?");
            sql.append(" and zp_pos_id=?");
            sql.append(" and (resume_flag is null or resume_flag=''");
            sql.append(" or resume_flag in ('0105','0106','0205','0206','0306','0307','0308',");
            sql.append("'0406','0407','0408','0506','0507','0508','0603','0604',");
            sql.append("'0703','0704','0805','0806','1004','1005'))");
            
            sqlParams.clear();
            sqlParams.add(a0100);
            sqlParams.add(dbName);
            sqlParams.add(zpPosId);
            
			rs = dao.search(sql.toString(), sqlParams);
			if(rs.next()) {
				//判断该职位有没有批次
				sql.setLength(0);
			    sql.append(" select Z0101 from Z03  where Z0301 = ? "); 
			    sqlParams.clear(); 
			    sqlParams.add(zpPosId); 
			    rs2 =dao.search(sql.toString(), sqlParams); 
			    if(rs2.next()) { 
				  rs2.getString("Z0101");
			    }
				  
			    //同一批次下的职位后续志愿号减一
			    sql.setLength(0);
                sql.append("update zp_pos_tache");
                sql.append(" set thenumber=thenumber-1");
                sql.append(" where a0100=?");
                sql.append(" and thenumber>(select thenumber from zp_pos_tache");
                sql.append(" where a0100=?");
                sql.append(" and zp_pos_id=? )");
                sql.append(" and nbase=?");
                sql.append(" and zp_pos_id in (");
                sqlParams.clear();
                sqlParams.add(a0100);
                sqlParams.add(a0100);
                sqlParams.add(zpPosId);
                sqlParams.add(dbName);
                if(StringUtils.isEmpty(rs2.getString("Z0101"))) {
                	 sql.append(" select Z0301 from Z03  where Z0101 is null)");
                }else {
                	 sql.append(" select Z0301 from Z03  where Z0101 = (select Z0101 from Z03  where Z0301 =?))");
                	 sqlParams.add(zpPosId);
                }
				dao.update(sql.toString(), sqlParams);
				
				//删除申请记录
				sql.setLength(0);
                sql.append("delete from zp_pos_tache");
                sql.append(" where a0100=?");
                sql.append(" and zp_pos_id=?");
                sql.append(" and nbase=?");
                
                sqlParams.clear();
                sqlParams.add(a0100);
                sqlParams.add(zpPosId);
                sqlParams.add(dbName);
				dao.delete(sql.toString(), sqlParams);
				
				return_code = "success";
			} else {
				return_code = "in_process";
			}
			
			com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo pobo = new com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo(this.conn,dao,null);
			pobo.saveCandiatesNumber(zpPosId, 1);
			pobo.saveCandiatesNumber(zpPosId, 2);
			pobo.saveCandiatesNumber(zpPosId, 3);
		
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(rs2);
		}
		return return_code;
	}
	
	/**
	 * 调整志愿排名
	 * @param pos_info 
	 */
	public String changeThenumber(ArrayList pos_info) {
		String return_code = "success";
		try {
			String a0100 = this.userview.getA0100();
			StringBuffer sql = new StringBuffer();
			sql.append("update zp_pos_tache");
			sql.append(" set thenumber=?");
			sql.append(" where zp_pos_id=?");
			sql.append(" and a0100=?");				
			ArrayList<String> sqlParams = new ArrayList<String>();
			for(int i=0; i<pos_info.size(); i++){
				MorphDynaBean bean = (MorphDynaBean) pos_info.get(i);
				HashMap<String,String> map = PubFunc.DynaBean2Map(bean);
				String pos_id = map.get("pos_id");
				pos_id = PubFunc.decrypt(pos_id);
				sqlParams.clear();
				sqlParams.add(map.get("thenumber"));
				sqlParams.add(pos_id);
				sqlParams.add(a0100);
				dao.update(sql.toString(), sqlParams);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "fail";
		}
		
		return return_code;
	}
	
	/**
	 * 收藏职位
	 * @param zpPosId
	 */
	public String collectionPos(String zpPosId) {
		String return_code = "success";
		try {
			String a0100 = this.userview.getA0100();
			Date date = new Date();
			Timestamp create_time = new Timestamp(date.getTime());
			StringBuffer sql = new StringBuffer();
			sql.append("insert into zp_pos_collection ");
			sql.append(" (a0100,nbase,ZP_POS_ID,Createtime) ");
			sql.append("values(?,?,?,?)");
			ArrayList<Object> values = new ArrayList<Object>();
			values.add(a0100);
			values.add(dbName);
			values.add(zpPosId);
			values.add(create_time);
			dao.update(sql.toString(), values);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		return return_code;
	}
	
	/**
	 * 查询收藏的职位
	 * @return
	 */
	public ArrayList searchCollection() {
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try {
			ArrayList<LazyDynaBean> columns = this.getPositionColumns();
			String sql = this.getSql();
			rs = dao.search(sql);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				String z0301 = rs.getString("z0301");
				for (LazyDynaBean obj : columns) {
					String itemid = (String) obj.get("itemid");
					if ("yprsl".equalsIgnoreCase(itemid) || "ypljl".equalsIgnoreCase(itemid))
				        continue;
	                 String itemtype = (String) obj.get("itemtype");
	                 String codesetid = (String) obj.get("codesetid");
	                 int deciwidth = Integer.parseInt(((String) obj.get("deciwidth")));
	                 
                     if ("A".equalsIgnoreCase(itemtype)) {
                         if ("0".equalsIgnoreCase(codesetid)) {
                             bean.set(itemid.toLowerCase(), rs.getString(itemid) == null ? "" : rs.getString(itemid));
                         } else {
                             String value = rs.getString(itemid) == null ? "" : rs.getString(itemid);
                             value = AdminCode.getCodeName(codesetid, value);
                             bean.set(itemid.toLowerCase(), value);
                         }
                     } else if ("D".equalsIgnoreCase(itemtype)) {
                         if (rs.getDate(itemid) != null) 
                             bean.set(itemid.toLowerCase(), dateFormat.format(rs.getDate(itemid)));
                         else 
                             bean.set(itemid.toLowerCase(), "");
                     } else if ("N".equalsIgnoreCase(itemtype)) {
                         if (rs.getString(itemid) != null) {
                             bean.set(itemid.toLowerCase(), PubFunc.round(rs.getString(itemid), deciwidth));
                         } else {
                             bean.set(itemid.toLowerCase(), "");
                         }
                     } else {
                         if (rs.getString(itemid) != null) 
                             bean.set(itemid.toLowerCase(), rs.getString(itemid));
                         else 
                             bean.set(itemid.toLowerCase(), "");
                     }
				}
				bean.set("z0301",PubFunc.encrypt(z0301));//增加必要字段
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return list;
		
	}
	
	/**
	 * 取消收藏职位
	 * @param zpPosId
	 */
	public String cancelCollection(String zpPosId) {
		String return_code = "success";
		try {
			String a0100 = this.userview.getA0100();
			StringBuffer sql = new StringBuffer();
			sql.append("delete from zp_pos_collection where a0100=? and nbase=? and ZP_POS_ID=?");
			ArrayList<String> values = new ArrayList<String>();
			values.add(a0100);
			values.add(dbName);
			values.add(zpPosId);
			dao.delete(sql.toString(), values);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		return return_code;
		
	}
	
	/**
	 * 获取公告列表
	 * @return
	 */
	public ArrayList getBoardlist(String hireChannel) {
		ArrayList boardlist = new ArrayList();
		try {
			EmployNetPortalBo bo=new EmployNetPortalBo(this.conn);
			if("0".equals(hireChannel))
				boardlist = bo.SQLExecute("2", "2", "");
			else if ("publicity".equalsIgnoreCase(hireChannel)) //公示
				boardlist = bo.SQLExecute("2", "13", "");
			else
				boardlist = bo.SQLExecute("2", "", hireChannel);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return boardlist;
		
	}
	
	/**
	 * 公告信息
	 * @param id
	 * @return
	 */
	public HashMap<String, String> getBoardInfo(String id) {
		RowSet rs = null;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select id,topic,content,ext,period,fileid,");
			//YYYY-MM-DD HH24:MM:SS 时分秒暂不显示
			strsql.append(Sql_switcher.dateToChar("createtime", "YYYY-MM-DD")+" createtime ");
			strsql.append(" from announce ");
			strsql.append(" where id=?");
			ArrayList<String> values = new ArrayList<String>();
			values.add(id);
			rs = dao.search(strsql.toString(),values);
			if(rs.next()) {
				map.put("id", PubFunc.encrypt(id));
				map.put("title", rs.getString("topic"));
				map.put("content", rs.getString("content"));
				map.put("createtime", rs.getString("createtime"));
				map.put("period", rs.getString("period"));
				if(StringUtils.isNotEmpty(rs.getString("ext")))
					map.put("href", "/servlet/vfsservlet?fileid="+rs.getString("fileid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
		
	}
	
	/**
	 * 生成收藏职位信息sql
	 * @return
	 * @throws GeneralException
	 */
	private String getSql() throws GeneralException {
		String a0100 = this.userview.getA0100();
		ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
    	HashMap map = parameterXMLBo.getAttributeValues();
    	EmployNetPortalBo bo = new EmployNetPortalBo(conn);
		ArrayList<LazyDynaBean> posFieldList = bo.getPosListField();
		StringBuffer sql = new StringBuffer("select z03.z0301,z03.Z0351,z03.z0321,z03.z0325,organization.codeitemdesc,organization.grade,z03.state ");
		boolean hasOpentime = false;
		if(posFieldList!=null&&posFieldList.size()>0){
			for (int i = 0; i < posFieldList.size(); i++) {
			    LazyDynaBean bean = (LazyDynaBean) posFieldList.get(i);
			    String itemid = (String) bean.get("itemid");
			    
			    if ("yprsl".equalsIgnoreCase(itemid) || "ypljl".equalsIgnoreCase(itemid)
			            || "state".equalsIgnoreCase(itemid) || "z0321".equalsIgnoreCase(itemid)
			            || "z0325".equalsIgnoreCase(itemid))
			        continue;
			    
			    if ("opentime".equalsIgnoreCase(itemid)) {
			        sql.append(",zp_members.create_time opentime ");
			        hasOpentime = true;
			        continue;
			    }
			    sql.append(",Z03." + itemid);
			}
			
		}else{
			hasOpentime = true;
			sql.append(",z03.z0315,z03.Z0329,z03.Z0331,z03.Z0333, "+Sql_switcher.dateToChar("zp_members.create_time", "YYYY-MM-DD HH24:MM:SS"));
		}
		
		sql.append(" from organization , z03");
		if (hasOpentime) {
			sql.append(" left join (select * from zp_members where zp_members.member_type=4)zp_members ");
			sql.append(" on zp_members.z0301=z03.Z0301 ");
		}
		sql.append(" left join zp_pos_collection zc on zc.ZP_POS_ID =z03.Z0301 ");
		StringBuffer tableBuffer = new StringBuffer();
		StringBuffer whereBuffer = new StringBuffer();
		whereBuffer.append(" where z03.z0321=organization.codeitemid ");
		whereBuffer.append(" and zc.a0100='"+a0100+"' and zc.nbase='"+dbName+"' ");
		// -----增加外网显示指标排序的功能
		String orderSql = getPostFieldSort(map, tableBuffer, whereBuffer);
		if (tableBuffer.length() > 0) {
		    sql.append(tableBuffer.toString());
		}
		sql.append(whereBuffer);
		if (orderSql.length() > 0)
		    sql.append(orderSql.toString());
		else
			 sql.append(" order by organization.a0000,z03.Z0321,z03.state,z03.z0329 desc ");
		return sql.toString();
	}
	
	 /**
     * 
     * @Title: getPostFieldSort
     * @Description: 获得排序方式的sql
     * @param map
     *            全局的招聘参数的配置
     * @param tableBuffer
     *            用于排序关联的table别名 例如organization T3 多了以后是 organization T4
     * @param whereBuffer
     *            用于排序时做关联用的where语句
     * @return String 返回ordersql语句
     * @throws
     */
    public String getPostFieldSort(HashMap map, StringBuffer tableBuffer, StringBuffer whereBuffer) {
        String pos_listfield_sort = "";
        String postitemid = "";
        String postitemsort = "";
        StringBuffer orderSql = new StringBuffer();
        String tempString = "";
        if (map.get("pos_listfield_sort") != null && ((String) map.get("pos_listfield_sort")).length() > 0)
            pos_listfield_sort = (String) map.get("pos_listfield_sort");
        if (pos_listfield_sort.length() > 0) {
            String[] postSortArray = pos_listfield_sort.split(",");
            int sortTableI = 3;
            for (int i = 0; i < postSortArray.length; i++) {
                String[] tempArray = postSortArray[i].split(":");
                postitemid = tempArray[0].trim();
                postitemsort = tempArray[1].trim();
                FieldItem item = DataDictionary.getFieldItem(postitemid.toLowerCase());
                if (item != null && "1".equals(item.getUseflag())) {
                    String codesetid = item.getFieldsetid();
                    if (codesetid != null
                            && ("UN".equalsIgnoreCase(codesetid)
                                    || "UM".equalsIgnoreCase(codesetid)
                                    || "@K".equalsIgnoreCase(codesetid))) {
                        tableBuffer.append(",organization T " + sortTableI);
                        whereBuffer.append(" and Z03." + postitemid + "==T" + sortTableI
                                + ".codeitemid ");
                        tempString = tempString + ",z03." + postitemid + " " + postitemsort
                                + ",T" + sortTableI + ".a0000";
                        sortTableI++;
                    } else {
                        tempString = tempString + ",z03." + postitemid + " " + postitemsort;
                    }

                }
            }
            if (tempString.length() > 0) {
                orderSql.append(" order by organization.a0000" + tempString);
            }
        }
        return orderSql.toString();
    }
    
    /**
     * 获取职位名称
     * @param z0301
     * @return
     */
    public HashMap<String, String> getPosInfo(String z0301) {
    	RowSet rs = null;
    	HashMap<String, String> map = new HashMap<String, String>();
    	try {
	    	String sql = "select z0351,z0336 from z03 where z0301=?";
	    	ArrayList<String> value = new ArrayList<String>();
	    	value.add(z0301);
			rs = dao.search(sql,value);
			if(rs.next()) {
				String z0351 = rs.getString("z0351");
				String z0336 = rs.getString("z0336");
				map.put("posDesc", z0351);
				map.put("hireChannel", z0336);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
    }
    
	/**
	 * 招聘根据应聘职位校验应聘人员是否符合筛选条件
	 * 如果符合则返回人员id
	 * 不符合如果是多个筛选器返回筛选器名称，只有一个筛选器返回具体指标
	 * 推荐职位时返回查询符合筛选条件人员id的sql
	 * @param z0301
	 * @param nbase
	 */
	public ArrayList<String> ruleFilter(String z0301) {
		ArrayList<String> ruleFilter = new ArrayList<String>();
		try {
			String a0100 = this.userview.getA0100();
			//校验结果
			boolean filterFlag = true;
			//校验职位筛选条件是否符合
			ResumeFilterBo filterBo = new ResumeFilterBo(this.conn, this.userview);
			if(filterBo.getApplyControl(z0301)) {
				ArrayList<String> filterId = filterBo.getFilterId(z0301);
				if(filterId.size()>0) {
					ruleFilter = filterBo.ruleFilter(z0301, a0100, dbName, "apply");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ruleFilter;
	}
	/**
	 * 获取发布了招聘职位的单位
	 * @param codeSetId
	 * @param hireChannel 招聘渠道
	 * @return
	 */
	public ArrayList<LazyDynaBean> getZPUnitList(String codeSetId, String hireChannel) {
		ArrayList<LazyDynaBean> unitList = new ArrayList<LazyDynaBean>();
		RowSet rowSet = null;
		try {
			EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(this.conn);
			String itemId = "";
			if("UN".equalsIgnoreCase(codeSetId))
				itemId = "z0321";
			else if("UM".equalsIgnoreCase(codeSetId))
				itemId = "z0325";
			else if("@K".equalsIgnoreCase(codeSetId))
				itemId = "z0311"; 
			unitList = employNetPortalBo.getOptions2(codeSetId, itemId, hireChannel);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		
		return unitList;
	}
	/**
	 * 获取最新职位
	 * @param channelIdList
	 * @param size 最新职位每个渠道显示职位数
	 * @return
	 */
    public ArrayList getNewPositions(ArrayList<String> channelIdList, int size) {
        EmployNetPortalBo bo = new EmployNetPortalBo(conn);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        RowSet rs = null;
        //{z0301:"",z0351:"",opentime:""}
        HashMap<String,String> onePosMap = new HashMap<String,String>();
        //[{z0301:"",z0351:"",opentime:""},..]
        ArrayList<HashMap<String,String>> channelPosList = new ArrayList<HashMap<String,String>>();
        //{01:[{z0301:"",z0351:"",opentime:""},..]}
        HashMap<String,ArrayList<HashMap<String,String>>> channelPosMap = new HashMap<String,ArrayList<HashMap<String,String>>>(); 
        //[{01:[{z0301:"",z0351:"",opentime:""},..]},..]
        ArrayList allChannelPosList = new ArrayList(); 
        ArrayList<HashMap<String,String>> channelPosSubList = new ArrayList<HashMap<String,String>>();
        try {
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn, "1");
            HashMap map = parameterXMLBo.getAttributeValues();
            for(int i=0;i<channelIdList.size();i++) {
                channelPosList = new ArrayList<HashMap<String,String>>();
                channelPosSubList = new ArrayList<HashMap<String,String>>();
                channelPosMap = new HashMap<String,ArrayList<HashMap<String,String>>>();
                String channelId = channelIdList.get(i);
                StringBuffer sql = getNewPosSql(channelId,map,bo);
                String sqlStr = sql.toString();
                if("configFail".equals(sqlStr)) {
                    allChannelPosList.add(sqlStr);
                    return allChannelPosList;
                }
                rs = dao.search(sqlStr);
                while(rs.next()) {
                    onePosMap = new HashMap<String,String>();
                    String z0351 = rs.getString("z0351");
                    String subZ0351 = "";
                    if (z0351.length()>15) {//超长截断 前台显示正常
                        subZ0351 = z0351.substring(0, 15)+ "...";
                    }
                    onePosMap.put("z0351", z0351);
                    onePosMap.put("subZ0351", subZ0351);
                    String z0301 = rs.getString("z0301");
                    onePosMap.put("z0301", z0301.length()<=10 ? PubFunc.encrypt(z0301) : z0301);
                    //获得SimpleDateFormat类，我们转换为yyyy-MM-dd的时间格式
                    Date opentime = rs.getDate("newdate");
                    onePosMap.put("opentime",sf.format(opentime));
                    //使用SimpleDateFormat的parse()方法生成Date
                    long ms = opentime.getTime();
                    onePosMap.put("createtime", String.valueOf(ms));
                    channelPosList.add(onePosMap);
                }
                for(int k=0;k<channelPosList.size()-1;k++) {
                    for(int j=0;j<channelPosList.size()-k-1;j++) {
                        long tempMs1 = Long.parseLong((String)channelPosList.get(j).get("createtime"));
                        long tempMs2 = Long.parseLong((String)channelPosList.get(j+1).get("createtime"));
                        long tempMs = tempMs2-tempMs1;
                        if(tempMs>0) {
                            HashMap<String, String> temp = channelPosList.get(j);
                            channelPosList.set(j, channelPosList.get(j+1));
                            channelPosList.set(j+1, temp);
                        }
                    }
                }
                for(int j=0;j<channelPosList.size()&&j<size;j++) {
                    channelPosSubList.add(channelPosList.get(j));
                }
                if(CollectionUtils.isNotEmpty(channelPosSubList)) {
                    channelPosMap.put(channelId, channelPosSubList);
                    allChannelPosList.add(channelPosMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            allChannelPosList.add("fail");
            return allChannelPosList;
        } finally {
            PubFunc.closeResource(rs);
        }
        return allChannelPosList;
    }
    /**
     * @param conditionFieldList
     * @param employObject
     * @param isRoot 
     * @param map
     * @param hirePostByLayer 
     * @param bo
     * @return
     * @throws GeneralException
     */
    private StringBuffer getNewPosSql(String employObject, HashMap map,EmployNetPortalBo bo) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        String hire_object = (String) map.get("hire_object");
        if (map.get("hire_object") == null || "".equals((String) map.get("hire_object"))) {
            sql.append("configFail");
            return sql;
        }
        
        //最新职位的显示控制到时分秒
        String nowDate=DateStyle.getSystemTime();
        sql.append("select z03.z0301,z03.Z0351,zp_members.create_time as newdate");
        sql.append(" from z03 left join zp_members ");
        sql.append(" on z03.z0301=zp_members.z0301");
        // 招聘成员表中发布人记录
        sql.append(" where zp_members.member_type=4");
        //04：已发布   审批状态
        sql.append(" and z03.z0319='04'");
        sql.append(" and " +Sql_switcher.dateToChar("Z0329", "yyyy-MM-dd hh24:mi:ss")+ "  <= '" + nowDate + "' ");
        sql.append(" and  '" + nowDate + "' <= " +Sql_switcher.dateToChar("Z0331", "yyyy-MM-dd hh24:mi:ss")+ "  ");
        sql.append(" and (z03." + hire_object + " like '" + employObject + "%' ");
        
        if (map != null && map.get("candidate_status") != null) {
            String candidateStatus = (String) map.get("candidate_status");
            FieldItem fieldItem = DataDictionary.getFieldItem("z0384", "z03");
            if(StringUtils.isNotEmpty(candidateStatus) && !"#".equalsIgnoreCase(candidateStatus) && fieldItem!=null && "1".equals(fieldItem.getUseflag())) {
                if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                    sql.append(" or ','+z03.z0384+',' like '%," + employObject + ",%' ");
                }else {
                    sql.append(" or ','||z03.z0384||',' like '%," + employObject + ",%' ");
                }
            }
        }
        sql.append(")");
        sql.append(" and (z03.Z0101 IN(select Z0101 from Z01 WHERE Z0129 ='04')  OR  z03.Z0101 is NULL) ");
        // 招聘成员表发布人的创建时间也就是职位的发布时间
        sql.append(" order by zp_members.create_time desc");
        
        return sql;
    }
    
    /**
     * 查询职位是否已结束
     * @param zpPosId
     * @return
     */
    private boolean isFinishPos(String zpPosId) {
    	RowSet rs = null;
    	try {
	    	StringBuffer sql = new StringBuffer();
	    	sql.append("select 1 from z03 ");
			sql.append(" left join Z01 on Z03.Z0101=Z01.Z0101 ");
			sql.append(" where Z0301=? ");
			//批次已结束，或者批次为空职位已结束
	    	sql.append(" and (Z0129='06' or ((Z03.Z0101 is null or Z03.Z0101='') and Z0319<>'04')) ");
	    	ArrayList<String> value = new ArrayList<String>();
	    	value.add(zpPosId);
			rs = dao.search(sql.toString(),value);
			if(rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return false;
    }
}
