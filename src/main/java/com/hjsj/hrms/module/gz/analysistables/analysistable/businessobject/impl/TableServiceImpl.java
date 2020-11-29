package com.hjsj.hrms.module.gz.analysistables.analysistable.businessobject.impl;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.gz.analysistables.analysistable.businessobject.TableService;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class TableServiceImpl implements TableService{
//	/**
//     * 日志
//     **/
//    private Category log = Category.getInstance(TableServiceImpl.class);
    /**
     * 数据库链接
     **/
    private Connection conn;

    /**
     * userView
     **/
    private UserView userView;

    /**
     * 薪资分析公共类
     */
    private GzAnalysisUtil gasu;

    /**
     *
     * @param conn
     * @param userView
     */
	public TableServiceImpl(Connection conn,UserView userView) {
		this.conn = conn;
		this.userView = userView;
		this.initGasu();
	}

	private void initGasu() {
		this.gasu = new GzAnalysisUtil(this.conn,this.userView);
	}

	/**
	 * 获得用户权限范围内的分析表类别
	 * @param imodule 0:薪资 1：保险
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public ArrayList getReportCategoryList(int imodule) throws GeneralException {
		ArrayList returnList = new ArrayList();
		returnList = gasu.getReportCategoryList(imodule);
		return returnList;
	}


	/**
	 * 获取人员库名称
	 * @param nbases
	 * @return
	 */
	@Override
    public ArrayList getNbaseNameList(ArrayList nbases) {
		RowSet rowset = null;
		ArrayList nbaseList = new ArrayList();
		if(nbases.size() == 0){
			return nbaseList;
		}
		String sql = "select Pre,DBName from DBName where Pre in (";
		ArrayList values = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		for(int i = 0; i<nbases.size();i++){
			if(i>0){
				sql += ",";
			}
			sql += "?" ;
			values.add((String) nbases.get(i));
		}
		sql += ")";
        sql +=" order by dbid";
		try {
			rowset = dao.search(sql,values);
			while(rowset.next()){
				HashMap map = new HashMap();
				String boxLabel = rowset.getString("DBName");
				String boxLabel_ = boxLabel.replaceAll("[^\\x00-\\xff]", "**"); 
	            int length = boxLabel_.length(); 
				if(length>16) {//最多8个汉字或者16个字符
					boxLabel = bSubstring(boxLabel,16);
				}
				map.put("name", rowset.getString("Pre"));
				map.put("boxLabel", rowset.getString("DBName"));
				map.put("Pre", rowset.getString("Pre"));
				map.put("DBName", boxLabel); 
				nbaseList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowset);
		}

		return nbaseList;
	}
	/**
	 * 按字节长度截断字符串
	 * @param s
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public String bSubstring(String s, int length) throws Exception {
        byte[] bytes = s.getBytes("Unicode");
        int n = 0;
        int i = 2;
        for (; i < bytes.length && n < length; i++){
            if (i % 2 == 1){
                n++;
            }
            else{
                if (bytes[i] != 0){
                    n++;
                }
            }
        }
        if (i % 2 == 1){
            if (bytes[i - 1] != 0)
                i = i - 1;
            else
                i = i + 1;
        }
        return new String(bytes, 0, i, "Unicode");
    }
	/**
	 * 获取人员库组件复选list
	 * @param nbaselist getNbaseNameList方法获取到的人员库map的集合 例：[{Pre:Usr,DBName:在职人员库},{...}]
	 * @param checkednbase 例：Usr,Oth
	 * @return
	 */
	@Override
    public ArrayList getNbaseCompList(ArrayList nbaselist, String checkednbase) {
		ArrayList resultList = new ArrayList();
		for(Object obj : nbaselist){
			HashMap map = (HashMap) obj;
			HashMap newmap = new HashMap();
			newmap.put("xtype", "checkbox");
			newmap.put("name", map.get("Pre"));
			newmap.put("boxLabel", map.get("DBName"));
			if(checkednbase.indexOf((String)map.get("Pre")) > -1){
				newmap.put("checked", true);
			}
			resultList.add(newmap);
		}
		return resultList;
	}

	/**
	 * 获得某薪资账套下的薪资项
	 * @param rsid 账套id
	 * @param itemTypes 指标类别，以逗号分隔可以是多个 举例 ： A,N,D,M,AC （AC-->代码型指标）
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public ArrayList getSalaryItemList(int salaryId, String itemTypes, String rsid) throws GeneralException{
        ArrayList returnList = gasu.getSalaryItemList(salaryId,itemTypes,rsid);
		return returnList;
	}

	/**
	 * 获得用户权限范围内的薪资|保险账套
	 * @param module 0:薪资 1：保险
	 * @param queryText 查询条件（名称 or id）
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public ArrayList getSalarySetList(int module, String queryText) throws GeneralException{
		ArrayList returnList = new ArrayList();
		ArrayList setList = gasu.getSalarySetList(module, queryText);
		for(Object obj:setList){
			HashMap map = (HashMap)obj;
			LazyDynaBean bean = new LazyDynaBean();
			String salaryid = (String) map.get("salaryid");
			String cname = (String) map.get("cname");
			bean.set("salaryid", salaryid);
			bean.set("cname", cname);
			returnList.add(bean);
		}
		return returnList;
	}

	/**
	 * 获得用户权限范围内的薪资|保险账套
	 * @param view userview
	 * @param module 0:薪资 1：保险
	 * @param queryText 查询条件（名称 or id）
	 * @param checkedIds
	 * @param limit
	 * @param page
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public ArrayList getSalarySetList(UserView view, int module, String queryText, String checkedIds, int limit, int page) throws GeneralException{
		ArrayList returnList = new ArrayList();
		ArrayList setList = getSalarySetList(view, module, queryText, limit, page);

		String[] ids = checkedIds.split(",");
		ArrayList<String> idlist = new ArrayList<String>();
		for(String str:ids){
			idlist.add(str);
		}
		for(Object obj:setList){
			HashMap map = (HashMap)obj;
			if("".equals(checkedIds)){
				map.put("checked", false);
			}else{
				if(idlist.contains((String)map.get("salaryid"))){
					map.put("checked", true);
				}else{
					map.put("checked", false);
				}
			}
			returnList.add(map);
		}
		return returnList;
	}

	/**
	 * 获得用户权限范围内的薪资|保险账套
	 * @param module  0:薪资  1：保险
	 * @param view
	 * @param queryText 查询条件（名称 or id）
	 * @param limit
	 * @param page
	 * @return
	 */
	@Override
    public ArrayList<HashMap> getSalarySetList(UserView view, int module, String queryText, int limit, int page)  throws GeneralException
	{
		ArrayList<HashMap> list=new ArrayList<HashMap> ();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet salarytemplateRs = null;
		try {
			StringBuffer buf = new StringBuffer();
			ArrayList<String> sqlList = new ArrayList<String>();
		 	buf.append("select salaryid,cname,cbase,seq,cond from salarytemplate ");
		 	if (module==0){// 薪资类别
				buf.append(" where (cstate is null or cstate='')");
			}else {
				buf.append(" where cstate='1'");// 险种类别
			}
		 	String[] values=queryText.split(",");
		 	 Pattern pattern = Pattern.compile("[0-9]+"); //整数
			// 快速查询
		 	StringBuffer strbuf = new StringBuffer();
			for(int i = 0; i < values.length; i++){
				String queryVal = values[i];
				if(pattern.matcher(queryVal).matches()){
					if(i == 0){
						strbuf.append(" and ( ");
					}else{
						strbuf.append(" or ");
					}
					strbuf.append("(salaryid=? or cname like ?)");
					sqlList.add(queryVal);
					sqlList.add("%"+queryVal+"%");
				}else{
					if(i == 0){
						strbuf.append(" and ( ");
					}else{
						strbuf.append(" or ");
					}
					strbuf.append("cname like ?");
					sqlList.add("%"+queryVal+"%");
				}
			}
			if(strbuf.length() > 0){
				strbuf.append(")");
			}
			buf.append(strbuf.toString());
			buf.append(" order by seq");
			if(page == -1){
				salarytemplateRs = dao.search(buf.toString(), sqlList);
			}else{
				salarytemplateRs = dao.search(buf.toString(), sqlList, limit, page);
			}
			HashMap map=null;
		    while(salarytemplateRs.next()){
					// 加上权限过滤
					if (module==0){
						if (!view.isHaveResource(IResourceConstant.GZ_SET, salarytemplateRs.getString("salaryid")))
							continue;
					}else {
						if (!view.isHaveResource(IResourceConstant.INS_SET, salarytemplateRs.getString("salaryid")))
							continue;
					}

					map=new HashMap();
					map.put("salaryid", salarytemplateRs.getString("salaryid"));
					map.put("cname", salarytemplateRs.getString("cname"));
					list.add(map);
		   }


		}catch (Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(salarytemplateRs);
		}
		return list;

	}
	/**
	 * 获取薪资分析表已选指标
	 * @param rsdtlid
	 * @param userView
	 * @return
	 */
	@Override
    public ArrayList getReportItemlist(String rsdtlid, UserView userView) {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
        	ArrayList values = new ArrayList();
            LazyDynaBean bean = null;
            StringBuffer sql = new StringBuffer();
            sql.append("select * from (");
            sql.append("select nwidth,align,itemdesc,itemid,itemfmt,sortid from reportitem where ");
            sql.append("rsdtlid=? and lower(itemid) in ('a00z0','a00z1','a00z2','a00z3','nbase')");
            sql.append(" union ");
            sql.append("select a.nwidth,a.align,a.itemdesc,a.itemid,a.itemfmt,a.sortid ");
            sql.append("from reportitem a,fielditem b,(select distinct itemid from salaryset) s ");
            sql.append("where lower(a.itemid)=lower(b.itemid) and lower(s.itemid)=lower(a.itemid) and lower(s.itemid)=lower(b.itemid) ");
            sql.append( "and a.rsdtlid=?) t order by sortid asc");
            values.add(rsdtlid);
            values.add(rsdtlid);
            rs = dao.search(sql.toString(),values);
            while (rs.next()) {
                bean = new LazyDynaBean();
                if (userView != null && "0".equals(userView.analyseFieldPriv(rs.getString("itemid"))))
                    continue;
                String itemid = rs.getString("itemid").toLowerCase();
                bean.set("itemid", itemid.toLowerCase());
                bean.set("itemdesc", rs.getString("itemdesc"));
                bean.set("align", rs.getString("align"));
                bean.set("nwidth", rs.getString("nwidth"));
                String itemfmt = "";
                if (rs.getString("itemfmt") != null)
                    itemfmt = rs.getString("itemfmt");
                bean.set("itemfmt", itemfmt);
                list.add(bean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return list;
    }

	/**
	 * 删除薪资分析数据
	 * @param module
	 * @param rsid
	 * @param rsdtlid
	 */
	@Override
    public boolean deleteReportdetail(int module, String rsid, String rsdtlid){
		boolean flag = false;
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			ArrayList value = new ArrayList();
			String sql = "delete from reportdetail where RSID = ? and RSDTLID = ?";
			value.add(rsid);
			value.add(rsdtlid);
			dao.update(sql,value);

			//级联删除薪资REPORTITEM表
            sql = "delete from reportitem where RSDTLID=?";
            value.clear();
            value.add(rsdtlid);
            dao.update(sql,value);
			flag = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
		return flag;
	}

	/**
	 * 保存归属单位参数
	 * @param rsid
	 * @param rsdtlid
	 * @param B0110 归属单位
	 */
	@Override
    public boolean saveBelongUnit(int rsid, int rsdtlid, String B0110, String rawType){
		boolean flag = false;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			ArrayList value = new ArrayList();
			String sql = "update reportdetail set B0110 = ? where RSID = ? and RSDTLID = ?";
			value.add(rawType+PubFunc.decrypt(B0110));
			value.add(rsid);
			value.add(rsdtlid);
			dao.update(sql,value);
			flag = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 新增薪资分析数据
	 * @param paramJson
	 * 			paramJson:{
	 * 						"imodule"://薪资和保险区分标识  1：保险  否则是薪资,
	 * 						"rsid" :" ", //报表种类编号(加密)
	 * 						"rsdtlid ":" ", //报表编号(加密)为空时表示新增，不为空时表示修改
	 * 						"name ":" ",//报表名称
	 * 						"items ":" ",//已选指标  (保存、编辑初始化时用到)
	 * 						"nbase ":" ",//人员库 Usr,Ret,Oth (保存、编辑初始化时用到)
	 * 						"salaryids ":" ",//薪资账套号  (保存、编辑初始化时用到)
	 * 						"verifying ": " "//含审批数据(保存、编辑初始化时用到)
	 * 					  }
	 * @return 返回新建后的rsdtlid;
	 */
	@Override
    public int insertReportdetail(JSONObject paramJson, UserView userview){
		int rsdtlid = -1;
		try {
			String rsid = paramJson.getString("rsid");
			String name = SafeCode.decode(paramJson.getString("name"));
			String items = paramJson.getString("items");
			String nbase = paramJson.getString("nbase");
			String salaryids = paramJson.getString("salaryids");
			String verifying = paramJson.getString("verifying");

			rsdtlid = DbNameBo.getPrimaryKey("reportdetail", "rsdtlid", conn);
			if(StringUtils.isNotEmpty(items)){
                insertSelectItems(items,rsdtlid,rsid);
            }


			String ctrlParam = "";
			//TODO 使用SalaryCtrlParamBo类？
			ctrlParam = getCtrlParamXml(userview, ctrlParam, nbase, salaryids, verifying);
			
			String unitIdByBusi = "";
			String unitcodes=this.userView.getUnitIdByBusi("1");  //UM010101`UM010105` 
			String[] units = unitcodes.split("`");
			for(int i=0;i<units.length;i++)
			{
				String codeid=units[i];
				if(codeid==null|| "".equals(codeid))
					continue;
				if(codeid!=null&&codeid.trim().length()>2)
				{
					unitIdByBusi = codeid;
				}
				if(!StringUtils.isEmpty(unitIdByBusi)){
					break;
				}
			}
			insertDetail(rsid, rsdtlid, 0, name, ctrlParam, userview.getUserName(), new Timestamp(new Date().getTime()), unitIdByBusi, userview.getUserFullName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsdtlid;
	}
	
	/**
	 * 插入数据
	 * @param rsid
	 * @param rsdtlid
	 * @param Stid
	 * @param Rsdtlname
	 * @param CtrlParam
	 * @param create_user
	 * @param create_time
	 * @param B0110
	 * @param create_fullname
	 */
	private boolean insertDetail(String rsid, int rsdtlid, int Stid, String Rsdtlname,String CtrlParam, String create_user,Timestamp create_time, 
			String B0110, String create_fullname) {
		ArrayList value = new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		int count = 0;
		try {
			String sql = "insert into reportdetail (rsid,Rsdtlid,Stid,Rsdtlname,CtrlParam,create_user,create_time,B0110,create_fullname) values (?,?,?,?,?,?,?,?,?)";
			value.add(rsid);
			value.add(rsdtlid);
			value.add(0);
			value.add(Rsdtlname);
			value.add(CtrlParam);
			value.add(create_user);
			value.add(create_time);
			value.add(B0110);
			value.add(create_fullname);
			count = dao.update(sql,value);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return count>0?true:false;
	}

	/**
	 * 新增已选指标
	 * @param items 指标，格式:"a0104,a0z23..."
	 * @param rsdtlid 薪资分析表id
	 * @return boolean 成功true，否则false
	 */
	@Override
    public boolean insertSelectItems(String items, int rsdtlid, String rsid) throws SQLException{
		String[] itemsArray = items.split(",");
		ArrayList vo_list = new ArrayList();
		ArrayList batchList = new ArrayList();
		ArrayList insertList = new ArrayList();
		ArrayList<HashMap> schemeList = new ArrayList<HashMap>();
		HashMap schemeOrderMap = new HashMap();
		String itemids = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rset = null;
		RowSet rset1 = null;
		try {
			for(int i = 0; i < itemsArray.length; i++){
				String itemid = itemsArray[i].split("`")[0];
				String itemdesc = itemsArray[i].split("`")[1];
				FieldItem fitem = DataDictionary.getFieldItem(itemid);
				HashMap schemeMap = new HashMap();
				if(fitem == null){
				    if(!"a00z0,a00z1,a00z2,a00z3,nbase".contains(itemid.toLowerCase())){
                        continue;
                    }
				}
				RecordVo item_vo = new RecordVo("reportitem");

				item_vo.setInt("rsdtlid", rsdtlid);
				item_vo.setInt("stid", 0);
				item_vo.setInt("sortid", i);
				item_vo.setString("itemid", itemid);
				item_vo.setString("itemdesc", itemdesc);
				ArrayList itemlist = new ArrayList();
				if("10".equals(rsid)) {
					itemids+="'"+itemid.toLowerCase()+"before','"+itemid.toLowerCase()+"year','"+itemid.toLowerCase()+"adde','"+itemid.toLowerCase()+"addl','"+
							"avg"+itemid.toLowerCase()+"before','avg"+itemid.toLowerCase()+"year','avg"+itemid.toLowerCase()+"adde','avg"+itemid.toLowerCase()+"addl',";
					itemlist.add(itemid.toLowerCase()+"before");
					itemlist.add(itemid.toLowerCase()+"year");
					itemlist.add(itemid.toLowerCase()+"adde");
					itemlist.add(itemid.toLowerCase()+"addl");
					itemlist.add("avg"+itemid.toLowerCase()+"before");
					itemlist.add("avg"+itemid.toLowerCase()+"year");
					itemlist.add("avg"+itemid.toLowerCase()+"adde");
					itemlist.add("avg"+itemid.toLowerCase()+"addl");
				}else {
					itemids+="'"+itemid.toLowerCase()+"',";
					itemlist.add(itemid.toLowerCase());
				}
				for(int k=0;k<itemlist.size();k++) {
					ArrayList list = new ArrayList();
					String itemid_ = (String) itemlist.get(k);
					schemeMap.put("itemid", itemid_);
					schemeMap.put("sortid", (k+itemlist.size()*i)+"");
					schemeMap.put("itemdesc", "");
					schemeOrderMap.put(itemid_, k+itemlist.size()*i);
					list.add((k+itemlist.size()*i)+"");
					list.add(itemid_);
					if(fitem !=null){
	                    String type = fitem.getItemtype();
	                    int decwidth = fitem.getDecimalwidth();
	                    item_vo.setInt("nwidth", decwidth);
	                    schemeMap.put("width", "100");
	                    String itemfmt = "";
	                    int align = 0;
	                    int schemealign = 1;
	                    if ("D".equals(type)) {
	                        align = 2;
	                        schemealign = 3;
	                        itemfmt = "yyyy.mm.dd";
	                    }
	                    if ("N".equals(type)) {
	                        align = 2;
	                        schemealign = 3;
	                        itemfmt = "0";
	                        if (decwidth > 0) {
	                            itemfmt += ".";
	                            for (int j = 0; j < decwidth; j++)
	                                itemfmt += "0";
	                        }
	                    }
	                    item_vo.setInt("align", align);
	                    schemeMap.put("align", schemealign+"");
	                    item_vo.setString("itemfmt", itemfmt);
	                }else{
	                    item_vo.setInt("nwidth", 0);
	                    item_vo.setInt("align", 0);
	                }
					schemeList.add(schemeMap);
					batchList.add(list);
				}
				vo_list.add(item_vo);
			}
			dao.addValueObject(vo_list);
			if(!"8".equals(rsid)&&!"9".equals(rsid)&&!"17".equals(rsid)) {
				//查验栏目设置是否存在
				String submoduleid = this.getSubmoduleid(rsid,rsdtlid);
				//得到对应的固定显示列
				ArrayList columnlist = this.getFixedColumn(rsid);
				// 是否存在私有记录
				String sqlForPrivate = "select scheme_id from t_sys_table_scheme where submoduleid = '" + submoduleid
						+ "' and is_share = 0 and username = '" + this.userView.getUserName() + "'";
	        	rset=dao.search(sqlForPrivate);
	        	if(rset.next()){
	        		GzAnalysisUtil gzAnalysisUtil = new GzAnalysisUtil(this.conn,this.userView);
	        		String scheme_id = rset.getString("scheme_id");
	        		if(StringUtils.isNotBlank(itemids)) {
	        			itemids = itemids.substring(0,itemids.length()-1);
	        		}
	        		//先删除指标记录 不包括is_fromdict=0的记录
	        		dao.update("delete from t_sys_table_scheme_item where lower(itemid) not in("+itemids+") and scheme_id="+scheme_id+" and is_fromdict=1");
	        		//在插入指标
	        		String sql = "select itemid,displayorder,is_fromdict from t_sys_table_scheme_item where scheme_id="+scheme_id+" order by displayorder";
	        		rset1 = dao.search(sql);
	        		while(rset1.next()) {
	        			String itemid = rset1.getString("itemid");
	        			String displayorder = rset1.getString("displayorder");
	        			String is_fromdict = rset1.getString("is_fromdict");
	        			for(int j=0;j<columnlist.size();j++) {
	        				String itemid_ = (String) columnlist.get(j);
	        				if(itemid.equalsIgnoreCase(itemid_)&&"0".equals(is_fromdict)) {
	        					ArrayList list = new ArrayList();
	        					list.add(displayorder);
	        					list.add(itemid_.toLowerCase());
	        					batchList.add(Integer.parseInt(displayorder)>batchList.size()?batchList.size():Integer.parseInt(displayorder),list);
	        				}
	        			}
	        			if(schemeOrderMap.containsKey(itemid.toLowerCase())){//包含的话就去掉
	        				schemeOrderMap.remove(itemid.toLowerCase());
	        			}
	        		}
	        		//插入剩下的itemid
	        		String insertsql = "INSERT INTO t_sys_table_scheme_item (scheme_id,itemid,displayorder,displaydesc,is_display,displaywidth,align,is_order,is_sum,"
							+ "itemdesc,mergedesc,is_lock,is_fromdict,is_removable) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					for(HashMap infoMap:schemeList){
						if(schemeOrderMap.containsKey(infoMap.get("itemid"))){
							insertList.add(this.getSchemeList(scheme_id, infoMap));
						}
					}
					dao.batchInsert(insertsql, insertList);
					//更新排序
					String updatesql = "update t_sys_table_scheme_item set displayorder=? where lower(itemid)=? and scheme_id="+scheme_id;
					//重新排列序号
					for(int j=0;j<batchList.size();j++){
						ArrayList list = (ArrayList) batchList.get(j);
						list.set(0, j);
					}
	        		dao.batchUpdate(updatesql, batchList);
	        	}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(rset1);
		}
		return true;
	}
	/**
	 * 获取固定显示的列
	 * @param rsid
	 * @return
	 */
	private ArrayList getFixedColumn(String rsid) {
		ArrayList list = new ArrayList();
		if("8".equals(rsid)||"17".equals(rsid)) {//人员工资汇总表
			
		}else if("7".equals(rsid)||"16".equals(rsid)) {//人员工资|保险项目统计表
			list.add("seq");
			list.add("nbase");
			list.add("b0110");
			list.add("e0122");
			list.add("a0101");
		}else if("5".equals(rsid)||"14".equals(rsid)) {//人员工资台账
			list.add("amonth");
		}else if("6".equals(rsid)||"15".equals(rsid)) {//工资项目分类统计台账
			list.add("amonth");
			list.add("personnum");
		}else if("9".equals(rsid)) {//按部门各月工资构成分析表
			
		}else if("10".equals(rsid)) {//工资总额构成分析表  特殊最后处理
			list.add("avg_item_sum");
			list.add("avgperson");
			list.add("item_sum");
			list.add("month");
		}else if("11".equals(rsid)) {//单位部门工资项目统计表
			list.add("rownum_");
			list.add("count_person");
			list.add("b0110");
			list.add("e0122");
		}
		return list;
	}

	private ArrayList getSchemeList(String scheme_id, HashMap infoMap) {
		ArrayList columnList = new ArrayList();
		String itemid = (String) infoMap.get("itemid");
		String sortid = (String) infoMap.get("sortid");
		String width = (String) infoMap.get("width");
		String align = (String) infoMap.get("align");
		columnList.add(scheme_id);//scheme_id
		columnList.add(itemid);//itemid
		columnList.add(sortid);//displayorder
		columnList.add("");//displaydesc
		columnList.add("1");//is_display
		columnList.add(width);//displaywidth
		columnList.add(align);//align
		columnList.add("0");//is_order
		columnList.add("0");//is_sum
		columnList.add("");//itemdesc
		columnList.add("");//mergedesc
		columnList.add("0");//is_lock
		columnList.add("1");//is_fromdict
		columnList.add("0");//is_removable
		return columnList;
	}

	/**
	 * 得到对应的submoduleid
	 * @param rsid
	 * @param rsdtlid
	 * @return
	 */
	private String getSubmoduleid(String rsid, int rsdtlid) {
		String submoduleid = "";
		if("8".equals(rsid)||"17".equals(rsid)) {//人员工资汇总表
			submoduleid = "employeePaySummaryTable";
		}else if("7".equals(rsid)||"16".equals(rsid)) {//人员工资|保险项目统计表
			submoduleid = "employeePayStatSuster";
		}else if("5".equals(rsid)||"14".equals(rsid)) {//人员工资台账
			submoduleid = "employeepaymusterdata_"+PubFunc.encrypt(rsdtlid+"");
		}else if("6".equals(rsid)||"15".equals(rsid)) {//工资项目分类统计台账
			submoduleid = "itemgroupmusterdata_"+PubFunc.encrypt(rsdtlid+"");
		}else if("9".equals(rsid)) {//按部门各月工资构成分析表
			submoduleid = "gzStructure";
		}else if("10".equals(rsid)) {//工资总额构成分析表
			submoduleid = "gzAmountStructure_"+PubFunc.encrypt(rsdtlid+"");
		}else if("11".equals(rsid)) {//单位部门工资项目统计表
			submoduleid = "GzItemSummary_"+PubFunc.encrypt(rsdtlid+"");
		}
		return submoduleid;
	}

	/**
	 * 删除已选指标
	 * @param rsdtlid 薪资分析表id
	 * @return boolean 成功true，否则false
	 * @throws SQLException
	 */
	@Override
    public boolean deleteSelectItems(int rsdtlid) throws SQLException{
		ContentDAO dao = new ContentDAO(this.conn);
        String sql = "delete from REPORTITEM where RsDtlID = ? ";
		ArrayList value = new ArrayList();
		value.add(rsdtlid);
		dao.update(sql, value);
		return true;
	}
	/**
	 * 更新薪资分析数据
	 * @param paramJson
	 * 			paramJson:{
	 * 						"imodule"://薪资和保险区分标识  1：保险  否则是薪资,
	 * 						"rsid" :" ", //报表种类编号(加密)
	 * 						"rsdtlid ":" ", //报表编号(加密)为空时表示新增，不为空时表示修改
	 * 						"name ":" ",//报表名称
	 * 						"items ":" ",//已选指标  (保存、编辑初始化时用到)
	 * 						"nbase ":" ",//人员库 Usr,Ret,Oth (保存、编辑初始化时用到)
	 * 						"salaryids ":" ",//薪资账套号  (保存、编辑初始化时用到)
	 * 						"verifying ": " "//含审批数据(保存、编辑初始化时用到)
	 * 					  }
	 * @return
	 */
	@Override
    public boolean updateReportdetail(JSONObject paramJson, UserView userview){
		boolean flag = false;

		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			String rsid = paramJson.getString("rsid");
			String rsdtlid = paramJson.containsKey("rsdtlid")?paramJson.getString("rsdtlid"):"";
			String name = SafeCode.decode(paramJson.getString("name"));
			String nbase = paramJson.getString("nbase");
			String salaryids = paramJson.getString("salaryids");
			String verifying = paramJson.getString("verifying");

			String ctrlParam = "";

			ctrlParam = getCtrlParamStr(Integer.parseInt(rsid), Integer.parseInt(rsdtlid));

			String sql = "select CtrlParam from reportdetail where rsid = ? and Rsdtlid = ?";
			ArrayList value = new ArrayList();
			value.add(rsid);
			value.add(rsdtlid);
			rowSet = dao.search(sql,value);
			//这种肯定就一个xml，
			//薪资分析的特殊判断，可能存在但是不在reportdetail表中，如果没有的话，需要塞入值
			if(rowSet.next()){
				ctrlParam = rowSet.getString("CtrlParam");
			}else if("12".equalsIgnoreCase(rsid)) {
				ctrlParam = getCtrlParamXml(userview, ctrlParam, nbase, salaryids, verifying);
				return insertDetail(rsid, Integer.parseInt(rsdtlid), 0, name, ctrlParam, "", new Timestamp(new Date().getTime()), "", "");
			}

			ctrlParam = getCtrlParamXml(userview, ctrlParam, nbase, salaryids, verifying);
			value = new ArrayList();
			sql = "update reportdetail set Rsdtlname = ?,CtrlParam = ?,create_user = ?,create_time = ?,create_fullname = ? where rsid = ? and Rsdtlid = ?";
			//TODO 薪资账套？
			value.add(name);
			value.add(ctrlParam);
			//【54061】薪资分析：修改报表定义时建议也对应更改创建人和创建时间字段的内容
			//对于人员工资汇总表，按部门各月工资构成分析表，用户自定义表，不需要时间和创建人
			if("8".equalsIgnoreCase(rsid) || "9".equalsIgnoreCase(rsid) || "12".equalsIgnoreCase(rsid)) {
				value.add(null);
				value.add(null);
				value.add(null);
			}else {
				value.add(userview.getUserName());
				value.add(new Timestamp(new Date().getTime()));
				value.add(userview.getUserFullName());
			}
			value.add(Integer.parseInt(rsid));
			value.add(Integer.parseInt(rsdtlid));
			dao.update(sql,value);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
		return flag;
	}
	
	/**
	 * 获取薪资分析页面设置内容
	 * @param userview
	 * @param ctrlParam 
	 * @param nbase
	 * @param salaryids
	 * @param verifying
	 * @return xml
	 */
	@Override
    public String getCtrlParamXml(UserView userview, String ctrlParam, String nbase, String salaryids, String verifying){
		String xml = "";
		StringReader reader = null;
		Document doc = null;
		try {
			Element report = null;
			Element param = null;
//			Element r_user = null;
			if(StringUtils.isNotEmpty((ctrlParam))){
				//TODO 修改进行的操作
				doc = PubFunc.generateDom(ctrlParam);
				param = doc.getRootElement();
				report = param.getChild("report");
				if(report == null){
					report = new Element("report");
				}
				report.setAttribute("nbase",nbase);
				report.setAttribute("salaryids",salaryids);
				report.setAttribute("verifying",verifying);
				param.setContent(report);
			}else{
				param = new Element("param");
				report = new Element("report");
				report.setAttribute("nbase",nbase);
				report.setAttribute("salaryids",salaryids);
				report.setAttribute("verifying",verifying);
				param.setContent(report);
				doc = new Document(param);
			}
			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xml = outputter.outputString(doc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(reader);
		}
		return xml;
	}
	
	/**
	 * 获取ctrlParam中的参数bean
	 * @return LazyDynaBean 
	 */
	@Override
    public LazyDynaBean getCtrlParamBean(int rsid, int rsdtlid){
		String ctrlParam = getCtrlParamStr(rsid,rsdtlid);
		LazyDynaBean bean = new LazyDynaBean();
		if(ctrlParam == null || "".equals(ctrlParam)){
			bean.set("nbase", "");
			bean.set("salaryids", "");
			bean.set("verifying", "");
			return bean;
		}
		StringReader reader = null;
		try {
			Document doc = PubFunc.generateDom(ctrlParam);
			Element param = doc.getRootElement();
			Element report = param.getChild("report");
			String nbase = "";
			String salaryids = "";
			String verifying = "";
			if(param != null && report != null){
				nbase = report.getAttributeValue("nbase") == null?"":report.getAttributeValue("nbase");
				salaryids = report.getAttributeValue("salaryids") == null?"":report.getAttributeValue("salaryids");
				verifying = report.getAttributeValue("verifying") == null?"":report.getAttributeValue("verifying");
			}
			
			bean.set("nbase", nbase);
			bean.set("salaryids", salaryids);
			bean.set("verifying", verifying);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(reader);
		}
		return bean;
	}
	
	/**
	 * 从数据库中获取CtrlParam
	 */
	@Override
    public String getCtrlParamStr(int rsid, int rsdtlid){
		String ctrlParam = "";
		RowSet rowSet = null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select CtrlParam from reportdetail where rsid = ? and Rsdtlid = ?";
			ArrayList value = new ArrayList();
			value.add(rsid);
			value.add(rsdtlid);
			rowSet = dao.search(sql,value);
			while(rowSet.next()){
				ctrlParam = rowSet.getString("CtrlParam");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return ctrlParam;
	}
	
	/**
	 * 获取Reportdetail中的参数bean
	 * @return LazyDynaBean 
	 * @throws GeneralException 
	 */
	@Override
    public LazyDynaBean getReportdetailBean(int module, int rsid, int rsdtlid) throws GeneralException{
		LazyDynaBean bean = getCtrlParamBean(rsid, rsdtlid);
		ArrayList list = gasu.getAnaysisReportList(module, rsid);
		for(Object obj : list){
			HashMap map = (HashMap)obj;
			int tabid = Integer.parseInt(PubFunc.decrypt((String)map.get("tabid")));
			if(rsdtlid == tabid){
				bean.set("tabid", (String)map.get("tabid"));
				bean.set("name", (String)map.get("tabname"));
				bean.set("b0110", (String)map.get("b0110"));
			}
		}
		return bean;
	}
    /**
     * 获得账套列
     */
    @Override
    public ArrayList<ColumnsInfo> getSalarySetColumnsInfo(int opt) {
        ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
        try
        {
            FieldItem item = new FieldItem();
            item.setItemid("salaryid");
            item.setItemdesc("编号");
            item.setItemtype("A");
            item.setReadonly(true);
            item.setItemlength(50);
            item.setCodesetid("0");
            ColumnsInfo info = new ColumnsInfo(item);
            info.setEditableValidFunc("false");
            info.setColumnWidth(50);
            list.add(info);

            item = new FieldItem();
            item.setItemid("cname");
            item.setItemdesc("类别名称");
            item.setItemtype("A");
            item.setReadonly(true);
            item.setItemlength(50);
            item.setCodesetid("0");
            info = new ColumnsInfo(item);
            info.setEditableValidFunc("false");
            info.setColumnWidth(opt==3?402:669);
            list.add(info);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return list;
    }
}
