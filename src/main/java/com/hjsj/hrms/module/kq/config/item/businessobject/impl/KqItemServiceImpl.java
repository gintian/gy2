package com.hjsj.hrms.module.kq.config.item.businessobject.impl;

import com.hjsj.hrms.businessobject.kq.register.history.KqReportInit;
import com.hjsj.hrms.module.kq.config.item.businessobject.KqItemService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 考勤项目
 */

public class KqItemServiceImpl implements KqItemService {
    private UserView userView;
    private Connection connection;

    /**
     * 同步kqitem数据
     * @throws GeneralException
     * @author ZhangHua
     * @date 13:44 2018/10/30
     */
    @Override
    public void synchronizeCodeItemToKqItem() throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        RowSet rs = null;
        StringBuffer strSql = new StringBuffer("SELECT codeitemid,codeitemdesc from codeitem WHERE  codeitemid like '__' AND codesetid='27'  and invalid=1 and codeitemid NOT IN (SELECT item_id FROM kq_item)");
        String insertSql = "insert into kq_item (item_id,item_name,displayorder) select ?,?,max("+Sql_switcher.isnull("displayorder","0")+") +1 from kq_item ";
        try {
            //同步新增项
            ArrayList dataList=new ArrayList();
            rs = dao.search(strSql.toString());
            while (rs.next()) {
                ArrayList list = new ArrayList();
                list.add(rs.getString("codeitemid"));
                list.add(rs.getString("codeitemdesc"));
                dao.update(insertSql, list);
            }
            //同步减少项
            strSql.setLength(0);
            strSql.append("select item_id from kq_item where item_id not in (select codeitemid from codeitem WHERE codeitemid like '__' AND codesetid='27' and invalid=1)");
            insertSql="delete from kq_item where item_id in(";
            rs=dao.search(strSql.toString());
            while (rs.next()) {
                dataList.add(rs.getString("item_id"));
                insertSql+="?,";
            }
            insertSql=insertSql.substring(0,insertSql.length()-1);
            insertSql+=")";
            if(dataList.size()>0){
                dao.update(insertSql, dataList);
            }


            //同步名称
            strSql.setLength(0);
            strSql.append(" select codeitemid,codeitemdesc from codeitem inner join kq_item on codeitem.codeitemid=kq_item.item_id where  codeitemid like '__' AND codeitem.codesetid='27' and invalid=1 ");
            strSql.append(" and codeitem.codeitemdesc!=kq_item.item_name");
            rs=dao.search(strSql.toString());
            insertSql="update kq_item set item_name=? where item_id=? ";
            dataList.clear();
            while (rs.next()){
                ArrayList list=new ArrayList();
                list.add(rs.getString("codeitemdesc"));
                list.add(rs.getString("codeitemid"));
                dataList.add(list);
            }
            if(dataList.size()>0) {
                dao.batchUpdate(insertSql, dataList);
            }
            strSql.setLength(0);

            //清除汇总指标不是关联q35的
            strSql.append(" update kq_item set fielditemid='' where nullif(fielditemid,'') is not null and upper(fielditemid) not like 'Q35%'");
            dao.update(strSql.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }


    /**
     * 获取考勤项目
     * （方法名以list开头 后接驼峰格式表名）
     *
     * @param sqlWhere      数据范围 (不需要写where 以and开头)
     * @param parameterList 参数
     * @param sqlSort       排序sql(不需要写order by 仅写字段即可，后可跟desc)
     * @return ArrayList<LazyDynaBean> (LazyDynaBean内为该表查询结果的全部字段，查询结果 bean 中的key为全小写字段名)
     * @throws GeneralException 接口方法必须抛出异常
     * @author ZhangHua
     * @date 11:29 2018/10/30
     */
    @Override
    public ArrayList<LazyDynaBean> listKqItem(String sqlWhere, ArrayList parameterList, String sqlSort) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getConnection());
        ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
        RowSet rs = null;
        StringBuffer strSql = new StringBuffer();
        try {
            strSql.append("SELECT item_id ,item_name , has_feast , has_rest , want_sum ,");
            strSql.append(Sql_switcher.isnull("item_symbol", "''")).append(" as item_symbol , ");
            strSql.append(Sql_switcher.isnull("item_color", "''")).append(" as item_color , ");
            strSql.append(Sql_switcher.isnull("item_unit", "''")).append(" as item_unit , ");
            strSql.append(Sql_switcher.isnull("fielditemid", "''")).append(" as fielditemid , ");
            strSql.append(Sql_switcher.isnull("sdata_src", "''")).append(" as sdata_src , ");
            strSql.append(Sql_switcher.isnull("s_expr", "''")).append(" as s_expr , ");
            strSql.append(Sql_switcher.isnull("c_expr", "''")).append(" as c_expr , ");
            strSql.append(Sql_switcher.isnull("m_c_expr", "''")).append(" as m_c_expr , ");
            strSql.append(Sql_switcher.isnull("other_param", "''")).append(" as other_param , ");
            strSql.append(Sql_switcher.isnull("item_type", "''")).append(" as item_type , ");
            strSql.append(Sql_switcher.isnull("displayorder", "0")).append(" as displayorder , ");
            strSql.append("  computerorder  FROM KQ_ITEM where 1=1 ");
            if (StringUtils.isNotBlank(sqlWhere )) {
                strSql.append(sqlWhere);
            }
            if (StringUtils.isNotBlank(sqlSort)) {
                strSql.append(" ORDER BY ").append(sqlSort);
            } else {
                strSql.append(" ORDER BY displayorder,item_id ");
            }
            ArrayList pList = new ArrayList();
            if (parameterList != null) {
                pList.addAll(parameterList);
            }
            rs = dao.search(strSql.toString(), pList);

            while (rs.next()) {
                //查询结果 bean 中的key为全小写字段名
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("item_id", KqDataUtil.nullif(rs.getString("item_id")));
                bean.set("item_name", KqDataUtil.nullif(rs.getString("item_name")));
                bean.set("has_feast", KqDataUtil.nullif(rs.getString("has_feast")));
                bean.set("has_rest", KqDataUtil.nullif( rs.getString("has_rest")));
                bean.set("want_sum", KqDataUtil.nullif(rs.getString("want_sum")));
                bean.set("item_symbol", KqDataUtil.nullif( rs.getString("item_symbol")));
                bean.set("item_color", KqReportInit.getColor(rs.getString("item_color")));
                bean.set("item_unit", KqDataUtil.nullif(rs.getString("item_unit")));
                bean.set("fielditemid", KqDataUtil.nullif(rs.getString("fielditemid")));
                bean.set("s_expr", KqDataUtil.nullif(rs.getString("s_expr")));
                bean.set("c_expr", KqDataUtil.nullif(rs.getString("c_expr")));
                bean.set("displayorder", KqDataUtil.nullif( rs.getString("displayorder")));
                bean.set("computerorder", KqDataUtil.nullif(rs.getString("computerorder")));
                bean.set("item_type", KqDataUtil.nullif(rs.getString("item_type")));
                String fieldSetId = "";
                String otherParamXml = KqDataUtil.nullif(rs.getString("other_param"));
                if (StringUtils.isNotBlank(otherParamXml)) {
                	Document doc = DocumentHelper.parseText(otherParamXml);
                	Element importEle = doc.getRootElement().element("import");
                	if (null != importEle) {
                		fieldSetId = importEle.attributeValue("subset");
                		fieldSetId = (StringUtils.isBlank(fieldSetId) || "#".equals(fieldSetId)) ? "" : fieldSetId;
                	}
                }
                bean.set("other_param", fieldSetId);
                
                dataList.add(bean);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataList;
    }

    /**
     * 获取q35表字段
     *
     * @return
     * @author ZhangHua
     * @date 10:06 2018/10/18
     */
    @Override
    public ArrayList<LazyDynaBean> listQ35Item() {

        ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
        ArrayList fieldList = DataDictionary.getFieldList("Q35", Constant.USED_FIELD_SET);
        //String outitem = "kq_duration,kq_year,guidkey,org_id,scheme_id,b0110,e0122,e01a1,only_field,confirm";
        LazyDynaBean bean = new LazyDynaBean();
        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem fieldItem = (FieldItem) fieldList.get(i);
            String id = fieldItem.getItemid().toLowerCase();
            if(!fieldItem.isVisible()){
                continue;
            }
            if (!id.startsWith("q35")) {
                continue;
            }
            if (id.startsWith("q350") || id.startsWith("q351") || id.startsWith("q352") || "q3530".equals(id)|| "q3531".equals(id)
                    || id.startsWith("q35z0") ) {
                continue;
            }
            bean = new LazyDynaBean();
            bean.set("id", id.toUpperCase());
            bean.set("name", fieldItem.getItemdesc());
            bean.set("type", fieldItem.getItemtype());
            list.add(bean);
        }
        return list;

    }


    /**
     * 保存项目
     *
     * @param kq_itemId 行id
     * @param column_id 列id
     * @param value     值
     * @return
     */
    @Override
    public boolean saveData(String kq_itemId, String column_id, Object value) throws GeneralException {
        try {
            StringBuffer strSql = new StringBuffer();
            strSql.append("update kq_item set ");
            strSql.append(column_id).append(" =? ");
            strSql.append(" where item_id=? ");
            ArrayList list = new ArrayList();
            if ("item_color".equalsIgnoreCase(column_id)) {
                if(StringUtils.isBlank(String.valueOf(value))){
                    throw GeneralExceptionHandler.Handle(new Exception("更新失败！"));
                }
                value = this.buildColor(String.valueOf(value));
            }
            if ("fielditemid".equalsIgnoreCase(column_id) && "-1".equals(value)) {
                value = null;
            }
            //【55907】develop：考虑到妇婴的业务比较特殊，会存在多个项目对应同一个统计指标的情况，所以需要在标准产品中吧这个限制功能放开，允许多个项目指定同一个统计指标
			/*
			 * if ("fielditemid".equalsIgnoreCase(column_id)) { if ("-1".equals(value)) {
			 * value = null; }else { strSql.append("and '"+value); strSql.
			 * append("' not in(select fielditemid from kq_item where fielditemid is not null)"
			 * ); } }
			 */
            list.add(value);
            list.add(kq_itemId);
            ContentDAO dao = new ContentDAO(this.getConnection());

            try {
                if (dao.update(strSql.toString(), list) > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }
        }catch (Exception e){

            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception("更新失败！"));
        }

    }


    /**
     * 十六进制转十进制颜色(兼容cs)
     *
     * @param value
     * @return
     * @author ZhangHua
     * @date 10:07 2018/10/18
     */
    private int buildColor(String value) {
        if (value.startsWith("#")) {
            value = value.substring(1, value.length());
        }

        String v = "";
        String v1 = String.valueOf(Integer.parseInt(value.substring(0, 2), 16));
        if (v1.length() == 1) {
            v1 = "00" + v1;
        } else if (v.length() == 2) {
            v1 = "0" + v1;
        }
        v += v1;
        v1 = String.valueOf(Integer.parseInt(value.substring(2, 4), 16));
        if (v1.length() == 1) {
            v1 = "00" + v1;
        } else if (v.length() == 2) {
            v1 = "0" + v1;
        }
        v += v1;
        v1 = String.valueOf(Integer.parseInt(value.substring(4), 16));
        if (v1.length() == 1) {
            v1 = "00" + v1;
        } else if (v.length() == 2) {
            v1 = "0" + v1;
        }
        v += v1;
        return Integer.parseInt("1" + v);
    }

    /**
     * 删除项目
     *
     * @param kq_itemId 行id
     * @return
     */
    @Override
    public boolean deleteItem(String kq_itemId) {
        StringBuffer strSql = new StringBuffer();
        String[] items = kq_itemId.split(",");
        if (items.length == 0) {
            return false;
        }
        strSql.append("delete kq_item where item_id in (");
        ArrayList<String> itemList = new ArrayList<String>();
        for (String item : items) {
            if (StringUtils.isNotBlank(item)) {
                itemList.add(PubFunc.decrypt(item));
                strSql.append("?,");
            }
        }
        strSql.deleteCharAt(strSql.length() - 1);
        strSql.append(")");
        ContentDAO dao = new ContentDAO(this.getConnection());

        try {
            if (dao.delete(strSql.toString(), itemList) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 项目排序
     *
     * @param ori_id
     * @param to_id
     * @param to_seq
     * @param ori_seq
     * @param dropPosition
     * @return
     */
    @Override
    public ArrayList<LazyDynaBean> dropKqItem(String ori_id, String to_id, String to_seq, String ori_seq, String dropPosition) {
        ArrayList<LazyDynaBean> datalist = new ArrayList<LazyDynaBean>();
        String direction = "";
        ContentDAO dao = new ContentDAO(this.getConnection());
        if (Integer.valueOf(ori_seq) > Integer.valueOf(to_seq))
            direction = "up";
        else if (Integer.valueOf(ori_seq) < Integer.valueOf(to_seq))
            direction = "down";
        else if (Integer.valueOf(ori_id) < Integer.valueOf(to_id))
            direction = "up";
        else if (Integer.valueOf(ori_id) > Integer.valueOf(to_id))
            direction = "down";
        StringBuffer str = new StringBuffer();
        ArrayList list = new ArrayList();

        try {


            if ("up".equals(direction)) {//上移
                //将上移对象的seq替换成目标对象的
                str.append("update kq_item set displayorder=? where item_id=?");
                list.add(to_seq);
                list.add(ori_id);
                dao.update(str.toString(), list);
                str.setLength(0);
                list = new ArrayList();
                list.add(to_seq);
                list.add(ori_seq);
                //在移动对象和目标对象之间的对象seq都加1
                if ("before".equals(dropPosition)) {//extjs拖拽时，移动对象相对目标对象是在其上
                    str.append("update kq_item set displayorder = displayorder+1 where displayorder>=? and displayorder<=?  and item_id<>?");
                    list.add(ori_id);
                } else {
                    str.append("update kq_item set displayorder = displayorder+1 where displayorder>=? and displayorder<=?  and item_id<>?");
                    list.add(to_id);
                }

                dao.update(str.toString(), list);
            } else if ("down".equals(direction)) {//下移
                //将下移对象的displayorder替换成目标对象的
                str.append("update kq_item set displayorder =? where item_id=?");
                list.add(to_seq);
                list.add(ori_id);
                dao.update(str.toString(), list);
                str.setLength(0);
                list = new ArrayList();
                list.add(ori_seq);
                list.add(to_seq);
                //在移动对象和目标对象之间的对象seq都减1.
                if ("after".equals(dropPosition)) {//extjs拖拽时，移动对象相对目标对象是在其上
                    str.append("update kq_item set displayorder = displayorder-1 where displayorder>=? and displayorder<=?  and item_id<>?");
                    list.add(ori_id);
                } else {
                    str.append("update kq_item set displayorder = displayorder-1 where displayorder>=? and displayorder<=?  and item_id<>?");
                    list.add(to_id);
                }
                dao.update(str.toString(), list);
            }

            //获取更新后收影响的seq列表
            list.remove(list.size() - 1);
            str.setLength(0);
            str.append("select displayorder,item_id from kq_item where displayorder>=? and displayorder<=?");
            LazyDynaBean bean = new LazyDynaBean();
            RowSet rs = dao.search(str.toString(), list);
            while (rs.next()) {
                bean = new LazyDynaBean();
                bean.set("item_id", PubFunc.encrypt(rs.getString("item_id")));
                bean.set("displayorder", rs.getString("displayorder"));
                datalist.add(bean);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datalist;


    }


    public KqItemServiceImpl(UserView userView, Connection connection) {
        this.userView = userView;
        this.connection = connection;
    }

    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

	/**
	 * 查询导入指标参数设置
	 * 
	 * @param kq_itemid
	 *            考勤项目编号
	 * @return
	 */
	@Override
    public HashMap<String, Object> searchFieldImportParam(String kq_itemid) {
		RowSet rs = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			String fieldSetId = "#";
			String fieldItemId = "#";
			String beginDate = "#";
			String endDate = "#";
			String fielditemid = "";
			String sql = "select fielditemid,other_param from kq_item where item_id=?";
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(StringUtils.isEmpty(kq_itemid) ? "" : PubFunc.decrypt(kq_itemid));
			ContentDAO dao = new ContentDAO(this.connection);
			rs = dao.search(sql, paramList);
			if (rs.next()) {
				fielditemid = rs.getString("fielditemid");
				String otherParamXml = rs.getString("other_param");
				if (StringUtils.isNotEmpty(otherParamXml)) {
					Document doc = DocumentHelper.parseText(otherParamXml);
					Element importEle = doc.getRootElement().element("import");
					if (importEle != null) {
						fieldSetId = importEle.attributeValue("subset");
						fieldSetId = StringUtils.isEmpty(fieldSetId) ? "#" : fieldSetId;
						fieldItemId = importEle.attributeValue("field");
						fieldItemId = StringUtils.isEmpty(fieldItemId) ? "#" : fieldItemId;
						beginDate = importEle.attributeValue("begindate");
						beginDate = StringUtils.isEmpty(beginDate) ? "#" : beginDate;
						endDate = importEle.attributeValue("enddate");
						endDate = StringUtils.isEmpty(endDate) ? "#" : endDate;
					}
				}
			}

			map.put("fieldSetId", fieldSetId);
			map.put("fieldItemId", fieldItemId);
			map.put("beginDate", beginDate);
			map.put("endDate", endDate);

			ArrayList<HashMap<String, String>> fieldSetMapList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> fieldSubMap = new HashMap<String, String>();
			fieldSubMap.put("fieldSetId", "#");
			fieldSubMap.put("fieldSetDesc", "请选择");
			fieldSetMapList.add(fieldSubMap);
			ArrayList fieldSetList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
			for (int i = 0; i < fieldSetList.size(); i++) {
				FieldSet fielSet = (FieldSet) fieldSetList.get(i);
				if ("0".equals(fielSet.getUseflag()))
					continue;

				HashMap<String, String> fieldSetMap = new HashMap<String, String>();
				fieldSetMap.put("fieldSetId", fielSet.getFieldsetid());
				fieldSetMap.put("fieldSetDesc", fielSet.getFieldsetdesc());
				fieldSetMapList.add(fieldSetMap);
			}

			map.put("fieldSetList", fieldSetMapList);
			map.putAll(searchItem(fieldSetId, fielditemid));
			map.put("countItemId", fielditemid);
			map.put("return_code", "success");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("return_code", "fail");
			map.put("return_msg", e.getMessage());
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
	}

	/**
	 * 查询来源子集中符合条件的指标
	 * 
	 * @param fieldSetId
	 *            来源子集
	 * @param fieldItemId
	 *            统计指标
	 * @return
	 */
	@Override
    public HashMap<String, Object> searchItem(String fieldSetId, String fielditemid) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			ArrayList<HashMap<String, String>> fieldItemMapList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> fieldItemMap = new HashMap<String, String>();
			fieldItemMap.put("itemId", "#");
			fieldItemMap.put("itemDesc", "请选择");
			fieldItemMapList.add(fieldItemMap);

			ArrayList<HashMap<String, String>> dateItemMapList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> dateMap = new HashMap<String, String>();
			dateMap.put("itemId", "#");
			dateMap.put("itemDesc", "请选择");
			dateItemMapList.add(dateMap);

			FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid, "Q35");
			String fiType = fieldItem.getItemtype();
			String codesetId = fieldItem.getCodesetid();
			ArrayList fieldItemList = DataDictionary.getFieldList(fieldSetId, Constant.USED_FIELD_SET);
			if (fieldItemList != null && fieldItemList.size() > 0) {
				for (int i = 0; i < fieldItemList.size(); i++) {
					FieldItem fi = (FieldItem) fieldItemList.get(i);
					if ("0".equals(fi.getUseflag()))
						continue;

					HashMap<String, String> itemMap = new HashMap<String, String>();
					itemMap.put("itemId", fi.getItemid());
					itemMap.put("itemDesc", fi.getItemdesc());
					if (fiType.equalsIgnoreCase(fi.getItemtype()) && codesetId.equalsIgnoreCase(fi.getCodesetid()))
						fieldItemMapList.add(itemMap);

					if ("D".equalsIgnoreCase(fi.getItemtype()))
						dateItemMapList.add(itemMap);
				}
			}

			map.put("fieldItemList", fieldItemMapList);
			map.put("dateItemList", dateItemMapList);
			map.put("return_code", "success");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("return_code", "fail");
			map.put("return_msg", e.getMessage());
		}

		return map;
	}

	/**
	 * 保存导入指标参数设置
	 * 
	 * @param param
	 *            前台传递参数
	 * @return
	 */
	@Override
    public HashMap<String, String> saveImportParam(MorphDynaBean param) {
		HashMap<String, String> map = new HashMap<String, String>();
		RowSet rs = null;
		try {
			String kq_itemid = (String) param.get("kq_itemid");
			if (StringUtils.isEmpty(kq_itemid))
				return map;

			kq_itemid = PubFunc.decrypt(kq_itemid);

			String fieldSetId = (String) param.get("fieldSetId");
			String itemId = (String) param.get("itemId");
			String startDate = (String) param.get("startDate");
			String endDate = (String) param.get("endDate");
			String otherParamXml = "";
			String sql = "select other_param from kq_item where item_id=?";
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(kq_itemid);
			ContentDAO dao = new ContentDAO(this.connection);
			rs = dao.search(sql, paramList);
			if (rs.next()) {
				otherParamXml = rs.getString("other_param");
			}

			Document doc = null;
			Element importParam = null;
			if (StringUtils.isEmpty(otherParamXml)) {
				doc = DocumentHelper.createDocument();
				Element root = doc.addElement("param");
				importParam = root.addElement("import");
			} else {
				doc = DocumentHelper.parseText(otherParamXml);
				importParam = doc.getRootElement().element("import");
			}

			importParam.addAttribute("subset", fieldSetId);
			importParam.addAttribute("field", itemId);
			importParam.addAttribute("begindate", startDate);
			importParam.addAttribute("enddate", endDate);
			otherParamXml = doc.asXML();
			paramList.clear();
			paramList.add(otherParamXml);
			paramList.add(kq_itemid);
			sql = "update kq_item set other_param=? where item_id=?";
			dao.update(sql, paramList);
			map.put("return_code", "success");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("return_code", "fail");
			map.put("return_msg", e.getMessage());
		}
		return map;
	}
}
