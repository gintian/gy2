package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.businessobject.sys.warn.ContextTools;
import com.hjsj.hrms.businessobject.sys.warn.WarnScanResult;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Title:SearchWarnSettingListTrans Description:查询预警设置,对应hrpwarn表格 Company:hjsj
 * create time:Jun 6, 2006:5:42:54 PM
 * 
 * @author zhouhaimao
 * @version 1.0
 * 
 */
public class SearchResultDetailTrans extends IBusiness implements IConstant
{

    public void execute() throws GeneralException
    {

        // 获得预警结果显示编号
        String strWid = (String) ((HashMap) this.getFormHM().get(Key_Request_Param_HashMap)).get("warn_wid");
        String dbpre = (String) ((HashMap) this.getFormHM().get(Key_Request_Param_HashMap)).get("dbpre");
        
        /*查看预警结果详情时，是否只显示自己  guodd 2019-07-24
         * 当预警为人员预警，且预警对象为角色，并设置了【本人特征】的角色
         * 如果当前用户没有预警设置的角色权限，默认走本人角色权限
         * 本人角色权限的特点是 如果预警结果中有自己，则只显示自己的数据；如果没有，则不显示此预警
         * 注意：本人角色 只有当前userview为自助用户 或 关联了自助的业务用户 时才适用
         * */
        boolean onlyShowSelf = false;
        
        // 没有该预警的权限或预警没有结果
        WarnScanResult st = new WarnScanResult();
        ArrayList warnResult = st.getWarnScanResultById(this.userView, this.frameconn, strWid);
        if (null == warnResult || warnResult.size() <= 0)
        {
            this.getFormHM().put("wid", "-1");
            return;
        }else {
        	for(int i=0;i<warnResult.size();i++) {
        		if((Object)warnResult.get(i) instanceof CommonData){//单位预警 和岗位预警 类型为String wangb 2019-08-15 bug 51955
        			CommonData cd = (CommonData)warnResult.get(i);
            		/*如果预警结果对象有“只显示自己”标记，记录下状态，下面拼数据sql使用  guodd 2019-07-24*/
            		if(cd.get("onlyShowSelf")!=null) {
            			onlyShowSelf = true;
            			break;
            		}
        		}
        	}
        }

        // 获取封装当前预警条件的动态BEAN
        DynaBean dbean = (DynaBean) ContextTools.getWarnConfigCache().get(strWid);
        this.getFormHM().put(Key_RecorderVo, dbean);

        String csource = (String) dbean.get("csource");
        String warntype = (String) dbean.get(Key_HrpWarn_FieldName_Warntyp);
        if (warntype == null || warntype.length() <= 0)
            warntype = "0";

        HashSet fieldItemSet = new HashSet();
        // 预警控制
        ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);
        DomainTool tool = new DomainTool();
        String strNbase = ctrlVo.getStrNbase();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        if (csource == null || "".equals(csource))
        {
            // 预警简单公式分析
            String simleExpress = ctrlVo.getStrSimpleExpress();
            // System.out.println("简单公式=" + simleExpress);
            fieldItemSet = this.getFieldItems(simleExpress);
        }
        else
        {
            // System.out.println("复杂公式=" + csource);
            HashMap usedFieldItems = new HashMap();

            // 获得预警相关指标
            ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
            if ("0".equals(warntype))
            {
                ArrayList alBasePre = DataDictionary.getDbpreList();
                ArrayList dblist = tool.getNbaseList(ctrlVo.getStrNbase(), dao);
                if (dblist != null && dblist.size() > 0)
                    alBasePre = dblist;
                for (int j = 0; j < alBasePre.size(); j++)
                {
                    int infoGroup = 0; // forPerson 人员
                    int varType = 8; // logic
                    String whereIN = InfoUtils.getWhereINSql(this.userView, alBasePre.get(j).toString());
                    whereIN = "select a0100 " + whereIN;
                    YksjParser yp = new YksjParser(this.userView, alUsedFields, YksjParser.forSearch, varType, infoGroup, "Ht", alBasePre.get(j).toString());
                    YearMonthCount ymc = null;
                    // yp.run_Where(csource, ymc,"","hrpwarn_result", dao,
                    // whereIN,this.getFrameconn(),"A", null);
                    yp.Verify_where(csource);
                    usedFieldItems = yp.getMapUsedFieldItems();
                    break;
                }
                if (usedFieldItems.size() != 0)
                {
                    Iterator it = usedFieldItems.keySet().iterator();
                    while (it.hasNext())
                    {
                        String temp = (String) it.next();
                        if(temp.length()>5)
                        	temp=temp.substring(temp.length()-5, temp.length());
                        FieldItem fieldItem_b = DataDictionary.getFieldItem(temp);
                        if (fieldItem_b != null)
                        {
                            //FieldItem fieldItem = (FieldItem) usedFieldItems.get(temp);
                            //fieldItemSet.add(fieldItem.getItemid());
                        	fieldItemSet.add(fieldItem_b.getItemid());
                        }
                    }
                }
            }
            else if ("1".equals(warntype) || "2".equals(warntype))
            {
                YksjParser yp = null;
                if ("1".equals(warntype))
                    yp = new YksjParser(this.userView, alUsedFields, YksjParser.forSearch, 8, YksjParser.forUnit, "Ht", "");
                else
                    yp = new YksjParser(this.userView, alUsedFields, YksjParser.forSearch, 8, YksjParser.forPosition, "Ht", "");
                YearMonthCount ymc = null;
                // yp.run_Where(csource, ymc,"","hrpwarn_result", dao,
                // "",this.getFrameconn(),"A", null);
                yp.Verify_where(csource);
                usedFieldItems = yp.getMapUsedFieldItems();
                if (usedFieldItems.size() != 0)
                {
                    Iterator it = usedFieldItems.keySet().iterator();
                    while (it.hasNext())
                    {
                        String temp = (String) it.next();
                        if(temp.length()>5)
                        	temp=temp.substring(temp.length()-5, temp.length());
                        FieldItem fieldItem_b = DataDictionary.getFieldItem(temp);
                        if (fieldItem_b != null)
                        {
                            //FieldItem fieldItem = (FieldItem) usedFieldItems.get(temp);
                            //fieldItemSet.add(fieldItem.getItemid());
                        	fieldItemSet.add(fieldItem_b.getItemid());
                        }
                    }
                }
            }

        }
        //涉及到子集指标不用显示，考虑到一些统计函数时再显示最近一条记录的值就不合适了 xuj 2013-10-18
        String view_subset_field = SystemConfig.getPropertyValue("view_subset_field");
        if("false".equals(view_subset_field)){
        	Iterator it = fieldItemSet.iterator();
        	HashSet tmpset = new HashSet();
            while (it.hasNext())
            {
            	String itemid = (String)it.next();
            	FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
            	if(fieldItem.isMainSet()){
            		tmpset.add(itemid);
            	}
            }
            fieldItemSet = tmpset;
        }
        
        //预警结果显示时因为HashSet无序导致指标显示打乱了，规律。重新排序一下，相同子集的指标显示到一起 guodd 2019-01-08
        fieldItemSet = reSortFields(fieldItemSet);
        
        StringBuffer sbSQLselect = new StringBuffer();
        String order = "";
        if ("0".equals(warntype))
        {
            if (dbpre == null || "".equals(dbpre) || "ALL".equals(dbpre))
            { // 全部用户库数据(对人员要加权限)
                // 所有库
                ArrayList dblist = userView.getPrivDbList(); // 管理员返回所有的库，一般操作员返回其权限库
                ArrayList warn_dblist = tool.getKqNbaseList(this.userView.getPrivDbList(), strNbase, dao);
                if (warn_dblist != null && warn_dblist.size() > 0)
                    dblist = warn_dblist;
                int i = 0;
                String strPre = "";
                for (i = 0; i < dblist.size(); i++)
                {
                    strPre = (String) dblist.get(i);

                    sbSQLselect.append(" select ");
                    sbSQLselect.append("(select dbid from dbname where pre='"+strPre+"') as dbid, ");
                    sbSQLselect.append(strPre + "a01.a0000 , ");
                    sbSQLselect.append("'");
                    sbSQLselect.append(strPre);
                    sbSQLselect.append("' nbase, ");
                    sbSQLselect.append(strPre + "a01.a0100 , ");
                    sbSQLselect.append(strPre + "a01.a0101 , ");
                    sbSQLselect.append(strPre + "a01.b0110 , ");
                    sbSQLselect.append(strPre + "a01.e0122 , ");
                    sbSQLselect.append(strPre + "a01.e01a1 ");
                    sbSQLselect.append(this.getSqlTitle(strPre,fieldItemSet, warntype));
                    sbSQLselect.append("  from " + strPre + "a01 ,");
                    String where_str = this.getTempSql(fieldItemSet, strPre, strWid, warntype,onlyShowSelf);
                    if (where_str.indexOf("where") < 3)
                    {
                        sbSQLselect.setLength(sbSQLselect.length() - 1);
                        sbSQLselect.append(where_str);
                    }
                    else
                    {
                        sbSQLselect.append(where_str);
                    }
                    // sbSQLselect.append(" where ");
                    if (i < dblist.size() - 1)
                    {
                        sbSQLselect.append(" union ");
                    }
                }
                if (i == 1)
                    order = "order by " + strPre + "A01.a0000";
                else
                {
                    sbSQLselect.insert(0, "select * from (");
                    sbSQLselect.append(") tt");
                    order = "order by dbid,a0000";
                }
            }
            else
            {
                String strPre = dbpre;

                sbSQLselect.append(" select ");
                sbSQLselect.append("'");
                sbSQLselect.append(strPre);
                sbSQLselect.append("' nbase, ");
                sbSQLselect.append(strPre + "a01.a0100 , ");
                sbSQLselect.append(strPre + "a01.a0101 , ");
                sbSQLselect.append(strPre + "a01.b0110 , ");
                sbSQLselect.append(strPre + "a01.e0122 , ");
                sbSQLselect.append(strPre + "a01.e01a1 ");
                sbSQLselect.append(this.getSqlTitle(strPre,fieldItemSet, warntype));
                sbSQLselect.append("  from " + strPre + "a01 ,");
                // sbSQLselect.append(this.getTempSql(fieldItemSet, strPre,
                // strWid));
                String where_str = this.getTempSql(fieldItemSet, strPre, strWid, warntype,onlyShowSelf);
                if (where_str.indexOf("where") < 3)
                {
                    sbSQLselect.setLength(sbSQLselect.length() - 1);
                    sbSQLselect.append(where_str);
                }
                else
                {
                    sbSQLselect.append(where_str);
                }
                order = "order by " + strPre + "A01.a0000";
            }

            StringBuffer columns = new StringBuffer();
            columns.append("a0101,b0110,e0122,e01a1,a0100,nbase,");
            String str = this.getColumns(fieldItemSet);
            columns.append(str);
            if (columns.length() > 0)
            {// 删除最后一个逗号
                columns.deleteCharAt(columns.length() - 1);
            }
            ArrayList list = new ArrayList();
            Iterator it = fieldItemSet.iterator();
            while (it.hasNext())
            {
                String itemid = (String) it.next();
                itemid = itemid != null && itemid.length() > 0 ? itemid : "";
                if ("B0110".equalsIgnoreCase(itemid))
                {
                    continue;
                }
                else if ("E0122".equalsIgnoreCase(itemid))
                {
                    continue;
                }
                else if ("E01A1".equalsIgnoreCase(itemid))
                {
                    continue;
                }else if ("a0101".equalsIgnoreCase(itemid))
                	continue;
                String strsql = "select itemdesc ,itemtype,codesetid,itemid,fieldsetid  from fielditem where Upper(itemid)='" + itemid.toUpperCase() + "'";
                String itemdesc = this.getItemDesc(strsql, dao);
                String itemtype = this.getItemType(strsql, dao);
                String codesetid = this.getCodeSetID(itemid, dao);
                String columnName = this.getFieldSetIDAddItemid(strsql, dao);
                // System.out.println(itemdesc);
                ColumnBean columnBean = new ColumnBean();
                columnBean.setColumnName(columnName);
                columnBean.setColumndesc(itemdesc);
                columnBean.setColumnType(itemtype);
                columnBean.setCodesetid(codesetid);
                list.add(columnBean);
            }

            ArrayList dbpreList = this.userView.getPrivDbList();
            ArrayList warn_dblist = tool.getKqNbaseCommonList(this.userView.getPrivDbList(), strNbase, dao);
            ArrayList dbList = new ArrayList();
            if (warn_dblist != null && warn_dblist.size() > 0)
            {
                dbList = warn_dblist;
            }
            else
            {
                DbNameBo dbvo = new DbNameBo(this.getFrameconn());
                dbpreList = dbvo.getDbNameVoList(dbpreList);
                for (int i = 0; i < dbpreList.size(); i++)
                {
                    CommonData vo = new CommonData();
                    RecordVo dbname = (RecordVo) dbpreList.get(i);
                    vo.setDataName(dbname.getString("dbname"));
                    vo.setDataValue(dbname.getString("pre"));
                    dbList.add(vo);
                }
            }
            getTenplateList(ctrlVo.getStrTemplate());
            this.getFormHM().put("dblist", dbList);

            this.getFormHM().put("columnList", list);

            this.getFormHM().put("columns", columns.toString());
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
            if (uplevel == null || uplevel.length() == 0)
                uplevel = "0";
            this.getFormHM().put("uplevel", uplevel);
        }
        else if ("1".equals(warntype) || "2".equals(warntype))
        {
            String connect_id = "";
            String connect_set = "";
            StringBuffer columns = new StringBuffer();
            if ("1".equals(warntype))
            {
                connect_id = "b0110";
                connect_set = "b01";
                columns.append("b0110,");
            }
            else
            {
                connect_id = "e01a1";
                connect_set = "k01";
                columns.append("e01a1,e0122,");
            }
            sbSQLselect.append(" select ");
            sbSQLselect.append("" + connect_set + "." + connect_id + " ");
            if ("2".equals(warntype))
            {
                sbSQLselect.append("," + connect_set + ".e0122 ");
            }
            sbSQLselect.append(this.getSqlTitle(fieldItemSet, warntype));
            sbSQLselect.append("  from " + connect_set + " ,");
            String where_str = this.getTempOrgSql(fieldItemSet, warntype, strWid);
            if (where_str.indexOf("where") < 3)
            {
                sbSQLselect.setLength(sbSQLselect.length() - 1);
                sbSQLselect.append(where_str);
            }
            else
            {
                sbSQLselect.append(where_str);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String backdate = sdf.format(new Date());

            if ("1".equals(warntype))
            {
                sbSQLselect.append(" and exists(select 1 from organization where b01.b0110=organization.codeitemid and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date)");
            }
            else
            {
                sbSQLselect.append("and exists(select 1 from organization where k01.e01a1=organization.codeitemid and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date)");
            }
            //添加排序   guodd 2015-04-30
            order=" order by "+connect_set+"."+connect_id;
            String str = this.getColumns(fieldItemSet,warntype);

            columns.append(str);
            if (columns.length() > 0)
            {// 删除最后一个逗号
                columns.deleteCharAt(columns.length() - 1);
            }
            ArrayList list = new ArrayList();
            Iterator it = fieldItemSet.iterator();
            while (it.hasNext())
            {
                String itemid = (String) it.next();
                ColumnBean columnBean = new ColumnBean();
                if ("0".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
                {
                	continue;
                	/*String desc = "";
                	String codesetid = "";
                	if(itemid.equalsIgnoreCase("b0110")){
                		codesetid="UN";
                		desc="单位";
                	}else if(itemid.equalsIgnoreCase("e01a1")){
                		codesetid="@K";
                		desc="岗位";
                	}else{
                		codesetid="UM";
                		desc="部门";
                	}
	                columnBean.setColumnName("A01"+itemid);
	                columnBean.setColumndesc(desc);
	                columnBean.setColumnType("A");
	                columnBean.setCodesetid(codesetid);*/
                }else
                if ("1".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
                {
                	continue;
                	/*String desc = "";
                	String codesetid = "";
                	if(itemid.equalsIgnoreCase("b0110")){
                		codesetid="UN";
                		desc="单位";
                	}else if(itemid.equalsIgnoreCase("e01a1")){
                		codesetid="@K";
                		desc="岗位";
                	}else{
                		codesetid="UM";
                		desc="部门";
                	}
                	
	                columnBean.setColumnName("B01"+itemid);
	                columnBean.setColumndesc(desc);
	                columnBean.setColumnType("A");
	                columnBean.setCodesetid(codesetid);*/
                }
                else if ("2".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
                {
                	continue;
                	/*String desc = "";
                	String codesetid = "";
                	if(itemid.equalsIgnoreCase("b0110")){
                		codesetid="UN";
                		desc="单位";
                	}else if(itemid.equalsIgnoreCase("e01a1")){
                		codesetid="@K";
                		desc="岗位";
                	}else{
                		codesetid="UM";
                		desc="部门";
                	}
	                columnBean.setColumnName("K01"+itemid);
	                columnBean.setColumndesc(desc);
	                columnBean.setColumnType("A");
	                columnBean.setCodesetid(codesetid);*/
                }else{
	                String strsql = "select itemdesc ,itemtype,codesetid,itemid,fieldsetid  from fielditem where Upper(itemid)='" + itemid.toUpperCase() + "'";
	                String itemdesc = this.getItemDesc(strsql, dao);
	                String itemtype = this.getItemType(strsql, dao);
	                String codesetid = this.getCodeSetID(itemid, dao);
	                FieldItem fieldItem = DataDictionary.getFieldItem(itemid.toLowerCase());
	                String columnName = this.getFieldSetIDAddItemid(strsql, dao); // System.out.println(itemdesc);)
	                columnBean.setColumnName(columnName);
	                columnBean.setColumndesc(itemdesc);
	                columnBean.setColumnType(itemtype);
	                columnBean.setCodesetid(codesetid);
                }
                list.add(columnBean);
            }
            this.getFormHM().put("columnList", list);
            this.getFormHM().put("columns", columns.toString());
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
            if (uplevel == null || uplevel.length() == 0)
                uplevel = "0";
            this.getFormHM().put("uplevel", uplevel);
        }
        this.getFormHM().put("order", order);
        this.getFormHM().put("warntype", warntype);
        this.getFormHM().put("encodeSql", SafeCode.encode(sbSQLselect.toString()));
        this.getFormHM().put("strsql", sbSQLselect.toString());
        // System.out.println(sbSQLselect.toString());
        this.getFormHM().put("wid", strWid);
        this.getFormHM().put("fieldItemclumn", getSqlTitleClumn(fieldItemSet));

    }

    /**
     * 重新排序fieldSet数据 guodd 2018-01-08 为海淀公安修改，并提交通版程序
     * @param fieldSet 指标集合
     * @return HashSet(LinkedHashSet保证有序)
     */
    private HashSet reSortFields(HashSet fieldSet) {
    	//如果小于3个指标，不用排序
    	if(fieldSet.size()<3)
    		return fieldSet;
    	
    	LinkedHashSet newset = new  LinkedHashSet();
    	
    	HashMap map = new HashMap();
    	
    	Iterator ite = fieldSet.iterator();
    	while(ite.hasNext()) {
    		String field = (String)ite.next();
    		FieldItem item = DataDictionary.getFieldItem(field);
    		String fieldset = item.getFieldsetid().toLowerCase();
    		String fieldstr = "";
    		//以fieldsetid为key，将同一个子集的指标放到一起
    		if(map.containsKey(fieldset))
    			fieldstr = (String)map.get(fieldset);
    		fieldstr = fieldstr+field+",";
    		map.put(fieldset, fieldstr);
    	}
    	
    	
    	ite = map.values().iterator();
    	//循环将指标添加到LinkedHashSet中
    	while(ite.hasNext()) {
    		String[] fieldArr = ((String)ite.next()).split(",");
    		for(int i=0;i<fieldArr.length;i++) {
    			if(fieldArr[i]!=null)
    				newset.add(fieldArr[i]);
    		}
    	}
    	
    	return newset;
    }
    public String getColumns(HashSet set)
    {
        StringBuffer columns = new StringBuffer();
        if (set == null)
            return "";
        ContentDAO dao = new ContentDAO(this.frameconn);
        Iterator it = set.iterator();
        while (it.hasNext())
        {
            String itemid = (String) it.next();
            String strsql = "select itemdesc,fieldsetid,itemid  from fielditem where Upper(itemid)='" + itemid.toUpperCase() + "'";
            String itemdesc = this.getFieldSetIDAddItemid(strsql, dao);
            columns.append(itemdesc);
            columns.append(",");

        }

        return columns.toString();

    }
    
    public String getColumns(HashSet set,String warntype)
    {
        StringBuffer columns = new StringBuffer();
        if (set == null)
            return "";
        ContentDAO dao = new ContentDAO(this.frameconn);
        Iterator it = set.iterator();
        String itemdesc = "";
        while (it.hasNext())
        {
            String itemid = (String) it.next();
            
            if ("0".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	itemdesc = "A01"+itemid;
            }else
            if ("1".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	itemdesc = "B01"+itemid;
            }
            else if ("2".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	itemdesc = "K01"+itemid;
            }else{
            	FieldItem fieldItem = DataDictionary.getFieldItem(itemid.toLowerCase());
            	String strsql = "select itemdesc,fieldsetid,itemid  from fielditem where Upper(itemid)='" + itemid.toUpperCase() + "'";
            	itemdesc = this.getFieldSetIDAddItemid(strsql, dao);
            }
            columns.append(itemdesc);
            columns.append(",");

        }

        return columns.toString();

    }

    public String getFieldSetID(String sql)
    {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String fieldsetid = "";
        try
        {
            this.frowset = dao.search(sql);
            if (frowset.next())
            {
                fieldsetid = this.frowset.getString("fieldsetid");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return fieldsetid;
    }

    public String getFieldSetIDAddItemid(String sql, ContentDAO dao)
    {
        String fieldsetid = "";
        String itemid = "";
        try
        {
            this.frowset = dao.search(sql);
            if (frowset.next())
            {
                fieldsetid = this.frowset.getString("fieldsetid");
                itemid = this.frowset.getString("itemid");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        if("A01".equalsIgnoreCase(fieldsetid))
        	return itemid;
        return fieldsetid + itemid;
    }

    public String getItemDesc(String sql, ContentDAO dao)
    {

        String itemdesc = "";
        try
        {
            this.frowset = dao.search(sql);
            if (frowset.next())
            {
                itemdesc = this.frowset.getString("itemdesc");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return itemdesc;
    }

    public String getItemType(String sql, ContentDAO dao)
    {
        String itemtype = "";
        try
        {
            this.frowset = dao.search(sql);
            if (frowset.next())
            {
                itemtype = this.frowset.getString("itemtype");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return itemtype;
    }

    /**
     * 
     * @param set
     *            涉及指标集合
     * @param dbPre
     *            库前缀
     * @param wid
     *            预警ID
     * @param onlyShowSelf
     *  		      是否只显示自己的数据         
     * @return
     */
    public String getTempSql(HashSet set, String dbPre, String wid, String warntype, boolean onlyShowSelf) throws GeneralException
    {
        StringBuffer sql = new StringBuffer();
        if (set == null)
            return "";
        String connect_id = "a0100";
        Iterator it = set.iterator();
        HashMap key = new HashMap();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        while (it.hasNext())
        {
            String itemid = (String) it.next();
            String fieldsetid = "";

            if ("B0110".equalsIgnoreCase(itemid))
            {
                fieldsetid = "A01";
                continue;
            }
            else if ("E0122".equalsIgnoreCase(itemid))
            {
                fieldsetid = "A01";
                continue;
            }
            else if ("E01A1".equalsIgnoreCase(itemid))
            {
                fieldsetid = "A01";
                continue;
            }
            else
            {
                String strsql = "select fieldsetid  from fielditem where Upper(itemid)='" + itemid.toUpperCase() + "'";
                fieldsetid = this.getFieldSetID(strsql);
            }
            if ("1".equals(warntype) && (fieldsetid == null || fieldsetid.length() <= 0))
            {
                fieldsetid = "B01";
            }
            else if ("2".equals(warntype) && (fieldsetid == null || fieldsetid.length() <= 0))
            {
                fieldsetid = "K01";
            }
            FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
            if (this.isCodeitem(itemid))
            {// 代码型指标

                if ("a01".equalsIgnoreCase(fieldsetid))
                {// 没有I999 人员主集指标不需要连表联查,分页查询会导致主集字段数据丢失 wangb 20190821 bug 52137
//                    key.put(itemid, "a0100");
//                    sql.append(" ( select ");
//                    sql.append(Sql_switcher.numberToChar(itemid) + " as codeitemdesc");
//                    sql.append(" , a0100 from ");
                    // sql.append(" ,codeitem.codeitemdesc from codeitem ,");
//                    sql.append("  (");
//                    sql.append(" select " + dbPre + "A01.a0100 as a0100 , ");
//                    sql.append(itemid);
//                    sql.append(" from ");
//                    sql.append(dbPre);
//                    sql.append("a01 where EXists ( select hrpwarn_result.a0100 from hrpwarn_result where wid=");
//                    sql.append(wid);
//                    sql.append(" and nbase='");
//                    sql.append(dbPre);
//                    sql.append("' and " + dbPre + "A01.a0100=hrpwarn_result.a0100)");
//                    sql.append(")");
//                    sql.append(itemid);
                    /*
                     * sql.append(" where codeitem.codesetid='");
                     * sql.append(this.getCodeSetID(itemid,dao));
                     * sql.append("' and codeitem.codeitemid=" + itemid + "."+
                     * itemid +")" + itemid+" ,");
                     */
//                    sql.append(")" + itemid + " ,");
                }
                else if ("b01".equalsIgnoreCase(fieldsetid))
                {
                    key.put(itemid, "b0110");
                    sql.append(" ( select ");
                    sql.append(itemid + " as codeitemdesc");
                    sql.append(" , b0110 from ");
                    // sql.append(" ,codeitem.codeitemdesc from codeitem ,");
                    sql.append("  (");
                    sql.append(" select " + fieldsetid + ".b0110, ");
                    sql.append(itemid);
                    sql.append(" from ");
                    // sql.append(fieldsetid+" where "+fieldsetid+".b0110 in ( select hrpwarn_result.a0100 from hrpwarn_result where wid=");
                    sql.append(fieldsetid + " where " + fieldsetid + ".b0110 in ( ");
                    sql.append("select b0110 from " + dbPre + "A01 where a0100 in( select hrpwarn_result.a0100 from hrpwarn_result where wid=");
                    sql.append(wid);
                    sql.append(" and nbase='");
                    sql.append(dbPre + "'");
                    sql.append(")");
                    sql.append(")");
                    sql.append(")");
                    sql.append(itemid);
                    sql.append(")" + itemid + " ,");
                }
                else if ("K01".equalsIgnoreCase(fieldsetid))
                {
                    key.put(itemid, "e01a1");
                    sql.append(" ( select ");
                    sql.append(itemid + " as codeitemdesc");
                    sql.append(" , e01a1 from ");
                    // sql.append(" ,codeitem.codeitemdesc from codeitem ,");
                    sql.append("  (");
                    sql.append(" select " + fieldsetid + ".e01a1 , ");
                    sql.append(itemid);
                    sql.append(" from ");
                    // sql.append(fieldsetid+" where "+fieldsetid+".e0122 in ( select hrpwarn_result.a0100 from hrpwarn_result where wid=");
                    sql.append(fieldsetid + " where " + fieldsetid + ".e01a1 in ( ");
                    sql.append("select e01a1 from " + dbPre + "A01 where a0100 in( select hrpwarn_result.a0100 from hrpwarn_result where wid=");
                    sql.append(wid);
                    sql.append(" and nbase='");
                    sql.append(dbPre + "'");
                    sql.append(")");
                    sql.append(")");
                    sql.append(")");
                    sql.append(itemid);
                    sql.append(")" + itemid + " ,");
                }
                else
                {
                	key.put(itemid, "a0100");
                	if(fieldItem.isPerson()){
	                    sql.append(" ( select ");
	                    sql.append(itemid + " as codeitemdesc");
	                    sql.append(" , a0100 from ");
	                    // sql.append( " ,codeitem.codeitemdesc from codeitem , " );
	                    sql.append("  (");
	                    sql.append(" select a0100 ," + itemid + " from " + dbPre + fieldsetid);
	                    sql.append(" d where d.i9999=( select max(i9999) from " + dbPre + fieldsetid + " e ");
	                    sql.append(" where d.a0100 = e.a0100 ) ");
	                    sql.append(" and exists( select a0100 from hrpwarn_result where wid=" + wid);
	                    sql.append(" and nbase='" + dbPre + "' and d.a0100=hrpwarn_result.a0100) ");
	                    sql.append(" union ");
	                    sql.append(" select a0100,'' as " + itemid + " from hrpwarn_result");
	                    sql.append(" where wid=" + wid + " and nbase='" + dbPre + "'");
	                    sql.append(" and not exists(");
	                    sql.append(" select a0100  from " + dbPre + fieldsetid);
	                    sql.append(" d where hrpwarn_result.a0100= d.a0100 and d.i9999=( select max(i9999) from " + dbPre + fieldsetid + " e ");
	                    sql.append(" where d.a0100 = e.a0100 ) ");
	                    // sql.append(" and exists( select a0100 from hrpwarn_result where wid="+
	                    // wid);
	                    // sql.append(" and nbase='" + dbPre +
	                    // "' and hrpwarn_result.a0100=e.a0100) ");
	                    sql.append(")");
	                    //
	                    sql.append(")");
	                    sql.append(itemid);
	                    /*
	                     * sql.append(" where codeitem.codesetid='");
	                     * sql.append(this.getCodeSetID(itemid,dao));
	                     * sql.append("' and codeitem.codeitemid=" + itemid + "."+
	                     * itemid +")" + itemid+" ,");
	                     */
	                    sql.append(")" + itemid + " ,");
                	}else{
                		if (fieldItem.isOrg())
                        {
                            key.put(itemid, "b0110");
                            connect_id = "b0110";
                            sql.append(" ( select b0110 ," + itemid + " as codeitemdesc  from " + fieldsetid);
                            sql.append(" d where d.i9999=( select max(i9999) from " + fieldsetid+ " e ");
                            sql.append(" where d.b0110 = e.b0110 ) and");
                            sql.append(" b0110 in( select b0110 from hrpwarn_result where wid=" + wid);
                            sql.append(" and nbase='" + dbPre + "')) " + itemid + " ,");
                        }
                        else if (fieldItem.isPos())
                        {
                            key.put(itemid, "e01a1");
                            connect_id = "e01a1";
                            sql.append(" ( select e01a1 ," + itemid + " as codeitemdesc  from " + fieldsetid);
                            sql.append(" d where d.i9999=( select max(i9999) from " + fieldsetid+ " e ");
                            sql.append(" where d.e01a1 = e.e01a1 ) and");
                            sql.append(" e01a1 in( select e01a1 from hrpwarn_result where wid=" + wid);
                            sql.append(" and nbase='" + dbPre + "')) " + itemid + " ,");
                        }
                	}
                }

            }
            else
            {// 非代码型指标
             // FieldItem fieldItem=new
             // FieldItem(fieldsetid.toLowerCase(),itemid.toLowerCase());
                //FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
                if (fieldItem == null)
                    continue;
                String itemtype = fieldItem.getItemtype();
                String sql_item = "";
                if (itemtype != null && "D".equalsIgnoreCase(itemtype))
                {
                    int itemlength = fieldItem.getItemlength();
                    if (itemlength > 10)
                        itemlength = itemlength + 1;
                    String dateformat = "yyyy.MM.dd HH:ss:mm";
                    if (dateformat.length() > itemlength)
                        dateformat = dateformat.substring(0, itemlength);
                    sql_item = Sql_switcher.dateToChar(itemid, dateformat);
                }
                else if (itemtype != null && "N".equalsIgnoreCase(itemtype))
                {
                    sql_item = Sql_switcher.numberToChar(itemid);
                }/*
                  * else if(itemtype!=null&&itemtype.equalsIgnoreCase("M")) {
                  * sql_item=Sql_switcher.numberToChar(itemid); }
                  */
                else
                {
                    // sql_item=Sql_switcher.numberToChar(itemid);
                    sql_item = itemid;//字符型指标不需要转   项目bug 45928 wangb 20190328
                }
                if ("a01".equalsIgnoreCase(fieldsetid))
                {// 没有I999
//                    key.put(itemid, "a0100");
//                    sql.append(" ( select ");
//                    sql.append(sql_item);
//                    sql.append(" as codeitemdesc , a0100 from ");
//                    sql.append(dbPre);
//                    sql.append("a01 where  exists ( select hrpwarn_result.a0100 from hrpwarn_result where " + dbPre + "A01.a0100=hrpwarn_result.a0100 and wid=");
//                    sql.append(wid);
//                    sql.append(" and nbase='");
//                    sql.append(dbPre);
//                    sql.append("'))");
//                    sql.append(itemid + " ,");
                }
                else
                {
                    if (!fieldItem.isOrg() && !fieldItem.isPos())
                    {
                    	/* connect_id = "a0100";
                        key.put(itemid, "a0100");
                        sql.append(" ( select a0100 ," + sql_item + " as codeitemdesc  from " + dbPre + fieldsetid);
                        sql.append(" d where d.i9999=( select max(i9999) from " + dbPre + fieldsetid + " e ");
                        sql.append(" where d.a0100 = e.a0100 ) ");
                        sql.append(" and exists( select a0100 from hrpwarn_result where hrpwarn_result.a0100=d.a0100 and wid=" + wid);
                        sql.append(" and nbase='" + dbPre + "')");
                        sql.append(" union ");
                        sql.append(" select a0100,'' as codeitemdesc from hrpwarn_result");
                        sql.append(" where wid=" + wid + " and nbase='" + dbPre + "'");
                        sql.append(" and  not exists(");
                        sql.append(" select a0100  from " + dbPre + fieldsetid);
                        sql.append(" d where d.a0100=hrpwarn_result.a0100 and  d.i9999=( select max(i9999) from " + dbPre + fieldsetid + " e ");
                        sql.append(" where d.a0100 = e.a0100 ) ");
                        sql.append(" and a0100 in( select a0100 from hrpwarn_result where wid=" + wid);
                        sql.append(" and nbase='" + dbPre + "') ");
                        sql.append(")");// not in的反括号
                        sql.append(")" + itemid + " ,");
                    }
                    else
                    {
                        if (fieldItem.isOrg())
                        {
                            key.put(itemid, "b0110");
                            connect_id = "b0110";
                            sql.append(" ( select b0110 ," + sql_item + " as codeitemdesc  from " + fieldsetid);
                            sql.append(" d where ");
                            sql.append(" b0110 in( select b0110 from hrpwarn_result where wid=" + wid);
                            sql.append(" and nbase='" + dbPre + "')) " + itemid + " ,");
                        }
                        else if (fieldItem.isPos())
                        {
                            key.put(itemid, "e01a1");
                            connect_id = "e01a1";
                            sql.append(" ( select e01a1 ," + sql_item + " as codeitemdesc  from " + fieldsetid);
                            sql.append(" d where ");
                            sql.append(" e01a1 in( select e01a1 from hrpwarn_result where wid=" + wid);
                            sql.append(" and nbase='" + dbPre + "')) " + itemid + " ,");
                        }

                    }*/

                	if(fieldItem.isMainSet()){
                		connect_id = "a0100";
                        key.put(itemid, "a0100");
                        sql.append(" ( select a0100 ," + sql_item + " as codeitemdesc  from " + dbPre + fieldsetid);
                        sql.append(" d where exists( select a0100 from hrpwarn_result where hrpwarn_result.a0100=d.a0100 and wid=" + wid);
                        sql.append(" and nbase='" + dbPre + "')");
                        sql.append(" union ");
                        sql.append(" select a0100,'' as codeitemdesc from hrpwarn_result");
                        sql.append(" where wid=" + wid + " and nbase='" + dbPre + "'");
                        sql.append(" and  not exists(");
                        sql.append(" select a0100  from " + dbPre + fieldsetid);
                        sql.append(" d where d.a0100=hrpwarn_result.a0100 and ");
                        sql.append(" a0100 in( select a0100 from hrpwarn_result where wid=" + wid);
                        sql.append(" and nbase='" + dbPre + "') ");
                        sql.append(")");// not in的反括号
                        sql.append(")" + itemid + " ,");
                	}else{
                        connect_id = "a0100";
                        key.put(itemid, "a0100");
                        sql.append(" ( select a0100 ," + sql_item + " as codeitemdesc  from " + dbPre + fieldsetid);
                        sql.append(" d where d.i9999=( select max(i9999) from " + dbPre + fieldsetid + " e ");
                        sql.append(" where d.a0100 = e.a0100 ) ");
                        sql.append(" and exists( select a0100 from hrpwarn_result where hrpwarn_result.a0100=d.a0100 and wid=" + wid);
                        sql.append(" and nbase='" + dbPre + "')");
                        sql.append(" union ");
                        sql.append(" select a0100,'' as codeitemdesc from hrpwarn_result");
                        sql.append(" where wid=" + wid + " and nbase='" + dbPre + "'");
                        sql.append(" and  not exists(");
                        sql.append(" select a0100  from " + dbPre + fieldsetid);
                        sql.append(" d where d.a0100=hrpwarn_result.a0100 and  d.i9999=( select max(i9999) from " + dbPre + fieldsetid + " e ");
                        sql.append(" where d.a0100 = e.a0100 ) ");
                        sql.append(" and a0100 in( select a0100 from hrpwarn_result where wid=" + wid);
                        sql.append(" and nbase='" + dbPre + "') ");
                        sql.append(")");// not in的反括号
                        sql.append(")" + itemid + " ,");
                	}
                }
                else
                {
                	if(fieldItem.isMainSet()){
                        if (fieldItem.isOrg())
                        {
                            key.put(itemid, "b0110");
                            connect_id = "b0110";
                            /*sql.append(" ( select b0110 ," + sql_item + " as codeitemdesc  from " + fieldsetid);
                            sql.append(" d where ");
                            sql.append(" b0110 in( select b0110 from hrpwarn_result where wid=" + wid);
                            sql.append(" and nbase='" + dbPre + "')) " + itemid + " ,");
                            */
                            sql.append(" ( select b0110 ," + sql_item + " as codeitemdesc  from " + fieldsetid);
                            sql.append(" d where b0110 in(");
                            sql.append(" select distinct b0110 from hrpwarn_result left join "+dbPre+"A01 on hrpwarn_result.a0100="+dbPre+"A01.a0100 where hrpwarn_result.wid=" + wid);
                            sql.append(" and hrpwarn_result.nbase='" + dbPre + "')");
                            sql.append(" ) " + itemid + " ,");
                        }
                        else if (fieldItem.isPos())
                        {
                            key.put(itemid, "e01a1");
                            connect_id = "e01a1";
                            /*sql.append(" ( select e01a1 ," + sql_item + " as codeitemdesc  from " + fieldsetid);
                            sql.append(" d where ");
                            sql.append(" e01a1 in( select e01a1 from hrpwarn_result where wid=" + wid);
                            sql.append(" and nbase='" + dbPre + "')) " + itemid + " ,");
                            */
                            sql.append(" ( select e01a1 ," + sql_item + " as codeitemdesc  from " + fieldsetid);
                            sql.append(" d where e01a1 in( ");
                            sql.append(" select distinct e01a1 from hrpwarn_result left join "+dbPre+"A01 on hrpwarn_result.a0100="+dbPre+"A01.a0100 where hrpwarn_result.wid=" + wid);
                            sql.append(" and hrpwarn_result.nbase='" + dbPre + "')");
                            sql.append(" ) " + itemid + " ,");
                        }
                	}else{
                		if (fieldItem.isOrg())
                        {
                            key.put(itemid, "b0110");
                            connect_id = "b0110";
                            sql.append(" ( select b0110 ," + sql_item + " as codeitemdesc  from " + fieldsetid);
                            sql.append(" d where d.i9999=( select max(i9999) from " + fieldsetid+ " e ");
                            sql.append(" where d.b0110 = e.b0110 ) and");
                            sql.append(" b0110 in( select b0110 from hrpwarn_result where wid=" + wid);
                            sql.append(" and nbase='" + dbPre + "')) " + itemid + " ,");
                        }
                        else if (fieldItem.isPos())
                        {
                            key.put(itemid, "e01a1");
                            connect_id = "e01a1";
                            sql.append(" ( select e01a1 ," + sql_item + " as codeitemdesc  from " + fieldsetid);
                            sql.append(" d where d.i9999=( select max(i9999) from " + fieldsetid+ " e ");
                            sql.append(" where d.e01a1 = e.e01a1 ) and");
                            sql.append(" e01a1 in( select e01a1 from hrpwarn_result where wid=" + wid);
                            sql.append(" and nbase='" + dbPre + "')) " + itemid + " ,");
                        }
                	
                	}
                }
                }
            }// end if

        }// end while

        if (sql.length() > 0)
        {// 删除最后一个逗号
            sql.deleteCharAt(sql.length() - 1);
        }

        sql.append(" where ");
        Iterator itt = set.iterator();
        while (itt.hasNext())
        {
            String itemid = (String) itt.next();
            String key_id = (String) key.get(itemid);
            if ("B0110".equalsIgnoreCase(itemid))
            {
                continue;
            }
            else if ("E0122".equalsIgnoreCase(itemid))
            {
                continue;
            }
            else if ("E01A1".equalsIgnoreCase(itemid))
            {
                continue;
            }
            FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
            if("A01".equalsIgnoreCase(fieldItem.getFieldsetid())){
            	continue;
            }
            if (key_id != null && key_id.length() > 0)
                sql.append(dbPre + "a01." + key_id + "=" + itemid + "." + key_id + " and ");
            else
                sql.append(dbPre + "a01." + connect_id + "=" + itemid + "." + connect_id + " and ");
        }
        sql.append(dbPre + "a01.a0100 in ( select hrpwarn_result.a0100 from hrpwarn_result where wid=" + wid + " and nbase='" + dbPre + "' ");
        if (userView.isSuper_admin())
        {
        }else if(onlyShowSelf) {//如果当前预警是只显示自己的数据，那么只查a0100为当前userview的数据 guodd 2019-07-24
        	sql.append(" and A0100 = '"+userView.getA0100()+"' ");
        }else
        {
            String value = userView.getManagePrivCodeValue();
            String item = "";
            String f = userView.getManagePrivCode();// 单位还是部门
            if ("UN".equalsIgnoreCase(f))
            {// 单位
                item = "B0110";
            }
            else if ("UM".equalsIgnoreCase(f))
            {// 部门
                item = "E0122";
            }
            else if ("@K".equalsIgnoreCase(f))
            {// 职位
                item = "E01A1";
            }
            // and a0100 in(select a0100 from wwwa01 where E0122 like'105017%')
            sql.append(" and A0100 in( select " + dbPre + "A01.a0100 from ");
            sql.append(dbPre);
            sql.append("a01 where 1=1 ");
            /*
             * if(item!=null&&item.length()>0&&value!=null&&value.length()>0) {
             * sql.append(" and "); sql.append(item); sql.append(" like'");
             * sql.append(value); sql.append("%'  "); }
             */

            String strWhere = this.userView.getPrivSQLExpression("", dbPre, false, true, new ArrayList());// fieldList);
            sql.append(" and (" + dbPre + "A01.a0100 in(select " + dbPre + "A01.a0100 " + strWhere + ")");
            if(this.userView.getStatus()==4&&this.userView.haveTheRoleProperty("14")){//本人角色特征
            	sql.append(" or "+dbPre + "A01.a0100='"+userView.getA0100()+"'");
    		}
            sql.append(")");
            sql.append(")");
        }
        sql.append(" ) ");

        return sql.toString();

    }

    /**
     * 
     * @param set
     *            涉及指标集合
     * @param dbPre
     *            库前缀
     * @param wid
     *            预警ID
     * @return
     */
    public String getTempOrgSql(HashSet set, String warntype, String wid) throws GeneralException
    {
        StringBuffer sql = new StringBuffer();
        if (set == null)
            return "";
        String connect_id = "";
        String connect_set = "";
        if ("1".equals(warntype))
        {
            connect_id = "b0110";
            connect_set = "b01";
        }
        else
        {
            connect_id = "e01a1";
            connect_set = "k01";
        }
        Iterator it = set.iterator();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        while (it.hasNext())
        {
            String itemid = (String) it.next();
            String fieldsetid = "";
            String strsql = "select fieldsetid  from fielditem where Upper(itemid)='" + itemid.toUpperCase() + "'";
            if ("1".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	
                fieldsetid = "b01";
                continue;
            }
            else if ("2".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	fieldsetid = "k01";
            	continue;
            }else{
            	fieldsetid = this.getFieldSetID(strsql);
            }
            if (this.isCodeitem(itemid))
            {// 代码型指标

                if (fieldsetid.equalsIgnoreCase(connect_set))
                {// 没有I999
                    sql.append(" ( select ");
                    sql.append(itemid + " as codeitemdesc");
                    sql.append(" , " + connect_id + " from ");
                    // sql.append(" ,codeitem.codeitemdesc from codeitem ,");
                    sql.append("  (");
                    sql.append(" select " + connect_set + "." + connect_id + " as " + connect_id + " , ");
                    sql.append(itemid);
                    sql.append(" from ");
                    sql.append(connect_set);
                    sql.append(" where " + connect_set + "." + connect_id + " in ( select hrpwarn_result.a0100 from hrpwarn_result where wid=");
                    sql.append(wid);
                    sql.append(")");
                    sql.append(")");
                    sql.append(itemid);
                    /*
                     * sql.append(" where codeitem.codesetid='");
                     * sql.append(this.getCodeSetID(itemid,dao));
                     * sql.append("' and codeitem.codeitemid=" + itemid + "."+
                     * itemid +")" + itemid+" ,");
                     */
                    sql.append(")" + itemid + " ,");
                }
                else
                {
                    sql.append(" ( select ");
                    sql.append(itemid + " as codeitemdesc");
                    sql.append(" , " + connect_id + " from ");
                    sql.append("  (");
                    sql.append(" select " + connect_id + " ," + itemid + " from " + fieldsetid);
                    sql.append(" d where d.i9999=( select max(i9999) from " + fieldsetid + " e ");
                    sql.append(" where d." + connect_id + " = e." + connect_id + " ) ");
                    sql.append(" and " + connect_id + " in( select a0100 from hrpwarn_result where wid=" + wid);
                    sql.append(") ");
                    sql.append(" union ");
                    sql.append(" select a0100,'' as " + itemid + " from hrpwarn_result");
                    sql.append(" where wid=" + wid + "");
                    sql.append(" and a0100 not in(");
                    sql.append(" select " + connect_id + "  from " + fieldsetid);
                    sql.append(" d where d.i9999=( select max(i9999) from " + fieldsetid + " e ");
                    sql.append(" where d." + connect_id + " = e." + connect_id + " ) ");
                    sql.append(" and " + connect_id + " in( select a0100 from hrpwarn_result where wid=" + wid);
                    sql.append(" ) ");
                    sql.append(")");
                    //
                    sql.append(")");
                    sql.append(itemid);
                    sql.append(")" + itemid + " ,");
                }
            }
            else if ("b0110".equalsIgnoreCase(itemid) || "e01a1".equalsIgnoreCase(itemid))
            {
                String sql_item = Sql_switcher.numberToChar(itemid);
                sql.append(" ( select ");
                sql.append(" codeitemdesc , codeitemid as " + connect_id + " from organization");
                sql.append(" where organization.codeitemid in ( select hrpwarn_result.a0100 from hrpwarn_result where wid=");
                sql.append(wid);
                sql.append("))");
                sql.append(itemid + " ,");
            }
            else
            {// 非代码型指标
             // FieldItem fieldItem=new
             // FieldItem(fieldsetid.toLowerCase(),itemid.toLowerCase());
                FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
                if (fieldItem == null)
                    continue;
                String itemtype = fieldItem.getItemtype();
                String sql_item = "";
                if (itemtype != null && "D".equalsIgnoreCase(itemtype))
                {
                    int itemlength = fieldItem.getItemlength();
                    if (itemlength > 10)
                        itemlength = itemlength + 1;
                    String dateformat = "yyyy.MM.dd HH:ss:mm";
                    if (dateformat.length() > itemlength)
                        dateformat = dateformat.substring(0, itemlength);
                    sql_item = Sql_switcher.dateToChar(itemid, dateformat);
                }
                else
                {
                    sql_item = Sql_switcher.numberToChar(itemid);
                }
                if (fieldsetid.equalsIgnoreCase(connect_set))
                {// 没有I999
                    sql.append(" ( select ");
                    sql.append(sql_item);
                    sql.append(" as codeitemdesc , " + connect_id + " from ");
                    sql.append(connect_set);
                    sql.append(" where " + connect_set + "." + connect_id + " in ( select hrpwarn_result.a0100 from hrpwarn_result where wid=");
                    sql.append(wid);
                    sql.append("))");
                    sql.append(itemid + " ,");
                }
                else
                {
                    sql.append(" ( select " + connect_id + " ," + sql_item + " as codeitemdesc  from " + fieldsetid);
                    sql.append(" d where d.i9999=( select max(i9999) from " + fieldsetid + " e ");
                    sql.append(" where d." + connect_id + " = e." + connect_id + " ) ");
                    sql.append(" and " + connect_id + " in( select a0100 from hrpwarn_result where wid=" + wid);
                    sql.append(" )");
                    sql.append(" union ");
                    sql.append(" select a0100,'' as codeitemdesc from hrpwarn_result");
                    sql.append(" where wid=" + wid + "");
                    sql.append(" and a0100 not in(");
                    sql.append(" select " + connect_id + "  from " + fieldsetid);
                    sql.append(" d where d.i9999=( select max(i9999) from " + fieldsetid + " e ");
                    sql.append(" where d." + connect_id + " = e." + connect_id + " ) ");
                    sql.append(" and " + connect_id + " in( select a0100 from hrpwarn_result where wid=" + wid);
                    sql.append(" ) ");
                    sql.append(")");// not in的反括号
                    sql.append(")" + itemid + " ,");
                }

                /*
                 * sql.append(" ( select "+connect_id+" ," + sql_item +
                 * " as codeitemdesc  from " + fieldsetid);
                 * sql.append(" d where "); sql.append(" "+connect_id+
                 * " in( select a0110 from hrpwarn_result where wid="+ wid);
                 * sql.append(" )) " +itemid +" ,");
                 */

            }// end if

        }// end while

        if (sql.length() > 0)
        {// 删除最后一个逗号
            sql.deleteCharAt(sql.length() - 1);
        }

        sql.append(" where ");
        Iterator itt = set.iterator();
        while (itt.hasNext())
        {
        	String itemid = (String) itt.next();
            if ("1".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	
                continue;
            }
            else if ("2".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	continue;
            }
            sql.append("" + connect_set + "." + connect_id + "=" + itemid + "." + connect_id + " and ");
        }
        sql.append("" + connect_set + "." + connect_id + " in ( select hrpwarn_result.a0100 from hrpwarn_result where wid=" + wid);
        if (userView.isSuper_admin() || userView.getUnitIdByBusi("4").indexOf("UN`")!=-1)//如果业务范围包含 UN` 说明有全部机构范围  guodd 2015-04-16
        {
        }
        else
        {
        	/*  机构预警走业务范围，不走人员范围，注掉此处  guodd 2015-04-16
            String value = userView.getManagePrivCodeValue();
            sql.append(" and (A0100  like'");
            sql.append(value);
            sql.append("%'  ");
            sql.append("or a0100 ="+Sql_switcher.substr("'"+userView.getManagePrivCodeValue()+"'","1",Sql_switcher.length("a0100"))+")");
            */
        	sql.append(" and ( ");
        	StringBuilder privSql = new StringBuilder(" 1=2 ");
        	String busiPrivStr = userView.getUnitIdByBusi("4");
    			String[] busiPrivs = busiPrivStr.split("`");
    			for(int i=0;i<busiPrivs.length;i++){
    				privSql.append(" or a0100 like '");
    				privSql.append(busiPrivs[i].substring(2));
    				privSql.append("%' ");
    				//privSql.append("or a0100="); 
    				//privSql.append(Sql_switcher.substr("'"+busiPrivs[i].substring(2)+"'","1",Sql_switcher.length("a0100")));
    			}
    			privSql.append(")");
        	sql.append(privSql);
        }
        sql.append(" ) ");

        return sql.toString();

    }
    
    /**
     * 
     * 组合SQL语句头
     * 注:A01表字段特殊处理 wangb 20190821 bug 52137
     * @param nbase 人员库
     * @param set
     *            涉及指标集合
     * @return
     */
    public String getSqlTitle(String nbase,HashSet set, String warntype)
    {
        StringBuffer sql = new StringBuffer();
        if (set == null)
            return "";
        Iterator it = set.iterator();
        ContentDAO dao = new ContentDAO(this.frameconn);
        while (it.hasNext())
        {
            String itemid = (String) it.next();
            String itemdesc = "";
            if (itemid == null || "".equals(itemid))
            {
                continue;
            }
            if ("A0101".equalsIgnoreCase(itemid))
            {
                continue;
            }
            if ("0".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	itemdesc = "A01"+itemid;
            	continue;
            }else
            if ("1".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	itemdesc = "B01"+itemid;
            	continue;
            }
            else if ("2".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	itemdesc = "K01"+itemid;
            	continue;
            }
            else
            {
                // 指标描述信息
                String strsql = "select itemdesc,fieldsetid,itemid  from fielditem where Upper(itemid)='" + itemid.toUpperCase() + "'";
                itemdesc = this.getFieldSetIDAddItemid(strsql, dao);
            } 
            sql.append(" , ");
            FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
            if("A01".equalsIgnoreCase(fieldItem.getFieldsetid())){ 
            	sql.append(nbase+"A01."+itemid+" as "+itemid+" ");
            	continue;
            }else{
            	sql.append(itemid);
            	// sql.append("."+itemid+" as ");
            	sql.append(".codeitemdesc as ");
            }
            switch (Sql_switcher.searchDbServer())
            {
            case Constant.MSSQL:
            {
                sql.append("'" + itemdesc + "' ");
                break;
            }
            case Constant.ORACEL:
            {
                sql.append("\"" + itemdesc + "\" ");
                break;
            }
            case Constant.DB2:
            {
                sql.append("\"" + itemdesc + "\" ");
                break;
            }
            }
        }
        if (sql.length() > 0)
        {// 删除最后一个逗号
            sql.deleteCharAt(sql.length() - 1);
        }
        return sql.toString();
    }

    /**
     * 组合SQL语句头
     * 
     * @param set
     *            涉及指标集合
     * @return
     */
    public String getSqlTitle(HashSet set, String warntype)
    {
        StringBuffer sql = new StringBuffer();
        if (set == null)
            return "";
        Iterator it = set.iterator();
        ContentDAO dao = new ContentDAO(this.frameconn);
        while (it.hasNext())
        {
            String itemid = (String) it.next();
            String itemdesc = "";
            if (itemid == null || "".equals(itemid))
            {
                continue;
            }
            if ("0".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	itemdesc = "A01"+itemid;
            	continue;
            }else
            if ("1".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	itemdesc = "B01"+itemid;
            	continue;
            }
            else if ("2".equals(warntype)&&("b0110".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)))
            {
            	itemdesc = "K01"+itemid;
            	continue;
            }
            else
            {
                // 指标描述信息
                String strsql = "select itemdesc,fieldsetid,itemid  from fielditem where Upper(itemid)='" + itemid.toUpperCase() + "'";
                itemdesc = this.getFieldSetIDAddItemid(strsql, dao);
            }
            sql.append(" , ");
            sql.append(itemid);
            // sql.append("."+itemid+" as ");
            sql.append(".codeitemdesc as ");
            switch (Sql_switcher.searchDbServer())
            {
            case Constant.MSSQL:
            {
                sql.append("'" + itemdesc + "' ");
                break;
            }
            case Constant.ORACEL:
            {
                sql.append("\"" + itemdesc + "\" ");
                break;
            }
            case Constant.DB2:
            {
                sql.append("\"" + itemdesc + "\" ");
                break;
            }
            }
        }
        if (sql.length() > 0)
        {// 删除最后一个逗号
            sql.deleteCharAt(sql.length() - 1);
        }
        return sql.toString();
    }

    /**
     * 判断指标是否式代码型
     * 
     * @param fielditemid
     * @return
     */
    public boolean isCodeitem(String fielditemid)
    {
        boolean b = false;
        if ("B0110".equalsIgnoreCase(fielditemid))
        {
            return false;
        }
        else if ("E0122".equalsIgnoreCase(fielditemid))
        {
            return false;
        }
        else if ("E01A1".equalsIgnoreCase(fielditemid))
        {
            return false;
        }
        String sql = "select codesetid  from fielditem where  itemid='" + fielditemid + "'";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try
        {
            this.frowset = dao.search(sql);
            if (frowset.next())
            {
                String codesetid = this.frowset.getString("codesetid");
                if ("0".equals(codesetid))
                {
                    return b;
                }
                else
                {
                    b = true;
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 获得代码型指标的代码集ID
     * 
     * @param fielditemid
     * @return
     */
    public String getCodeSetID(String fielditemid, ContentDAO dao)
    {
        String codesetid = "";
        String sql = "select codesetid  from fielditem where  Upper(itemid)='" + fielditemid.toUpperCase() + "'";
        try
        {
            this.frowset = dao.search(sql);
            if (frowset.next())
            {
                codesetid = this.frowset.getString("codesetid");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return codesetid;
    }

    /**
     * 分析预警简单公式获取涉及指标列表
     * 
     * @param simleExpress
     * @return 涉及指标集合不包含重复
     */
    public HashSet getFieldItems(String simleExpress)
    {
        if (simleExpress == null || "".equals(simleExpress))
        {
            return null;
        }
        HashSet set = new HashSet();
        StringTokenizer t = new StringTokenizer(simleExpress, "|");
        String filedItems = "";
        if (t.hasMoreElements())
        {
            t.nextToken();
            if (t.hasMoreElements())
            {
                filedItems = t.nextToken();
            }
        }

        StringTokenizer st = new StringTokenizer(filedItems, "`");
        // StringBuffer fieldItemRet = new StringBuffer();
        while (st.hasMoreElements())
        {
            String fieldItem = st.nextToken();
            int n = 0;
            for (int i = 0; i < fieldItem.length(); i++)
            {
                if (">=<=<>=".indexOf(fieldItem.charAt(i)) != -1)
                {
                    n = i;
                    break;
                }
                else
                {
                    n = fieldItem.length();
                }
            }
            String temp = fieldItem.substring(0, n);

            set.add(temp);
            // fieldItemRet.append(",");
        }
        /*
         * if (fieldItemRet.length() > 0) {// 删除最后一个逗号
         * fieldItemRet.deleteCharAt(fieldItemRet.length() - 1); }
         */
        return set;
    }

    public String toChart(String dbPre, String itemid)
    {
        String strResult = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String strsql = "select fieldsetid  from fielditem where itemid='" + itemid + "'";
        String fieldSetID = this.getFieldSetID(strsql);
        String sql = "select " + itemid + " from " + dbPre + fieldSetID + " where 1=2";
        // System.out.println(sql);
        try
        {
            this.frowset = dao.search(sql);
            // 获得查询语句结果集描述对象
            ResultSetMetaData metaData = this.frowset.getMetaData();
            // 校验字段类型
            int columnType = metaData.getColumnType(1);
            switch (columnType)
            {
            case Types.NUMERIC:
            case Types.REAL:
            case Types.INTEGER:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.DECIMAL:
            case Types.BIGINT:
            case Types.TINYINT:
            case Types.SMALLINT:
                // System.out.println("数值型");
                strResult = Sql_switcher.floatToChar(itemid + "." + itemid);
                break;
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                // System.out.println("日期型");
                strResult = Sql_switcher.dateToChar(itemid + "." + itemid);
                break;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                // System.out.println("字符型");
                strResult = itemid + "." + itemid;
                break;
            default:
                // System.out.println("默认字符型");
                // strResult = Sql_switcher.dateToChar(itemid+"."+itemid);
                strResult = itemid + "." + itemid;
                break;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return strResult;
    }

    private void getTenplateList(String selectedId)
    {
        ArrayList list = new ArrayList();
        StringBuffer strsql = new StringBuffer();
        if (selectedId == null || selectedId.length() <= 0)
        {

            this.formHM.put("tenplatelist", null);
            return;
        }
        strsql.append("select tabid,name from template_table where ");
        strsql.append("tabid in (");
        String[] tIds = selectedId.split(",");
        for (int i = 0; i < tIds.length; i++)
        {
            strsql.append("'" + tIds[i] + "',");
        }
        strsql.setLength(strsql.length() - 1);
        strsql.append(")");
        RowSet rset = null;
        try
        {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            rset = dao.search(strsql.toString());
            boolean isCorrect = false;
            while (rset.next())
            {
                isCorrect = false;
                if (this.userView.isHaveResource(IResourceConstant.RSBD, rset.getString("tabid")))// 人事移动
                    isCorrect = true;
                if (!isCorrect)
                    if (this.userView.isHaveResource(IResourceConstant.GZBD, rset.getString("tabid")))// 工资变动
                        isCorrect = true;
                if (!isCorrect)
                    if (this.userView.isHaveResource(IResourceConstant.INS_BD, rset.getString("tabid")))// 保险变动
                        isCorrect = true;
                if (!isCorrect)
                    continue;
                CommonData vo = new CommonData();
                vo.setDataValue(rset.getString("tabid"));
                vo.setDataName(rset.getString("tabid") + ":" + rset.getString("name"));
                list.add(vo);
            }
        }
        catch (Exception ex)
        {

        }
        this.formHM.put("tenplatelist", list);
    }

    public String getSqlTitleClumn(HashSet set)
    {
        StringBuffer sql = new StringBuffer();
        if (set == null)
            return "";
        Iterator it = set.iterator();
        ContentDAO dao = new ContentDAO(this.frameconn);
        while (it.hasNext())
        {
            String itemid = (String) it.next();
            sql.append(itemid);
            sql.append(",");
        }
        if (sql.length() > 0)
        {// 删除最后一个逗号
            sql.deleteCharAt(sql.length() - 1);
        }
        return sql.toString();
    }
}