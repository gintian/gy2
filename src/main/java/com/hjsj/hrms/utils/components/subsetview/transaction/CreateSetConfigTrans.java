package com.hjsj.hrms.utils.components.subsetview.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateSetConfigTrans extends IBusiness {

    public void execute() throws GeneralException {
        try
        {
            String subModuleId = (String)this.formHM.get("subModuleId");
            String setName="",nbase="",currentObject="",privType="",title="",lockColumns="",filterColumn="",schemeItemKey="",queryItem="",customFilterCond="";
            boolean isScheme=true;
            HashMap functionPirv;
            HashMap privParaMap;
            ArrayList inputBtnsList;
            //xus 19/9/5筛选的指标
            ArrayList shiftItems;

            setName=(String)this.formHM.get("setName");
            nbase=this.formHM.get("nbase")==null?"":(String)this.formHM.get("nbase");
            currentObject=this.formHM.get("currentObject")==null?"":(String)this.formHM.get("currentObject");
            privType=this.formHM.get("privType")==null?"":(String)this.formHM.get("privType");
            title=this.formHM.get("title")==null?"":(String)this.formHM.get("title");
            lockColumns=this.formHM.get("lockColumns")==null?"":(String)this.formHM.get("lockColumns");
            MorphDynaBean bean=(MorphDynaBean)this.formHM.get("functionPirv");
            schemeItemKey=this.formHM.get("schemeItemKey")==null?"":(String)this.formHM.get("schemeItemKey");
            queryItem=this.formHM.get("queryItem")==null?"":(String)this.formHM.get("queryItem");
            isScheme=this.formHM.get("isScheme")==null?false:(Boolean)this.formHM.get("isScheme");
            inputBtnsList=(ArrayList)this.formHM.get("buttonList");
            customFilterCond=this.formHM.get("customFilterCond")==null?"":(String)this.formHM.get("customFilterCond");
            shiftItems=this.formHM.get("shiftItems")==null?new ArrayList():(ArrayList)this.formHM.get("shiftItems");
            functionPirv=PubFunc.DynaBean2Map(bean);

            MorphDynaBean parabean=(MorphDynaBean)this.formHM.get("funcParam");

            privParaMap=PubFunc.DynaBean2Map(parabean);

            if(setName==null||"".equals(setName)){
                HashMap customParamMap=(HashMap)((TableDataConfigCache)this.userView.getHm().get(subModuleId)).getCustomParamHM();
                setName=(String) customParamMap.get("setName");
                nbase=(String) customParamMap.get("nbase");
                currentObject=(String) customParamMap.get("currentObject");
                privType=(String) customParamMap.get("privType");
                schemeItemKey=(String) customParamMap.get("schemeItemKey");
                functionPirv=(HashMap) customParamMap.get("functionPirv");
                privParaMap=(HashMap) customParamMap.get("privParaMap");
                queryItem=(String) customParamMap.get("queryItem");
                customFilterCond=(String) customParamMap.get("customFilterCond");
            }
            //解密currentObject
            if(!"".equals(currentObject))
                currentObject=PubFunc.decrypt(currentObject);

            //获取要锁列的list
            ArrayList lockColumnsList = new ArrayList();
            String[] lockColumnsArray = lockColumns.split(",");
            for(Object obj:lockColumnsArray){
                lockColumnsList.add(obj.toString().toUpperCase());
            }
            //过滤列
            filterColumn=this.formHM.get("filterColumn")==null?"":(String)this.formHM.get("filterColumn");


            String sql="";
            ContentDAO dao = new ContentDAO(this.frameconn);
            //子集主键
            String privKey=getFieldKey(setName);

            //删除
            String type=this.formHM.get("type")==null?"":(String)this.formHM.get("type");
            if("del".equals(type)){
                ArrayList dataInfo=this.formHM.get("dataInfo")==null?new ArrayList():(ArrayList)this.formHM.get("dataInfo");
                String[] nbases=nbase.split(",");
                for(Object obj:dataInfo){
                    MorphDynaBean mbean = (MorphDynaBean)obj;
                    HashMap map = PubFunc.DynaBean2Map(mbean);
                    for(int i=0;i<nbases.length;i++){
                        String key=map.get("key").toString();
                        key=PubFunc.decrypt(key);
                        sql = "delete  from "+nbases[i]+setName+" where "+privKey+" = '"+key+"' and i9999 = "+map.get("dataIndex").toString()+"" ;
                        dao.delete(sql,new ArrayList());
                    }
                }
                this.formHM.put("flag", true);
                return;
            }

            /**1、判断子集是否构库，并给予提示
             2、如果没有设置currentObject参数，按照privType过滤数据
             3、人员子集自动显示单位、部门、岗位、姓名指标，如多个人员库，显示人员库指标
             4、表格内置导出excel、新增、修改、删除、查询、栏目设置、过滤功能。子集如支持附件，显示附件列。新增、修改、删除功能根据functionPriv权限号判断。
             **/

            FieldSet fieldSet=getFieldSetInfo(setName,dao);
            //判断子集是否构库
            if(!"1".equals(fieldSet.getUseflag())){
                this.formHM.put("isUsed", false);
                return;
            }
            //子集名称
            String fieldSetName="";
            //如果未设置标题，则显示子集名称
            if(!"".equals(title))
                fieldSetName=title;
            else
                fieldSetName=fieldSet.getFieldsetdesc();
            //获取列对象
            ArrayList columnsInfoList = new ArrayList();


            HashMap fieldprivMap=getTableFieldList(setName, nbase, currentObject, subModuleId, privType, dao, isScheme);
            HashMap Infomap = getTableBuilderInfo(setName, nbase, currentObject, fieldprivMap, lockColumnsList, filterColumn, dao, privType, privParaMap);
            //查询框内容
            ArrayList<String> valuesList = new ArrayList<String>();
            valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
            //获取数据sql
//			sql = getListSql(setName,nbase,currentObject,privType,columns,valuesList,privParaMap);
            String orderBy = "order by "+privKey;

            //工具栏按钮 list
            ArrayList toolList = getToolList(setName,functionPirv,queryItem,inputBtnsList);
            columnsInfoList = (ArrayList) Infomap.get("columnsInfoList");
            sql = (String) Infomap.get("sql");
            if(""!=customFilterCond){
                // 48114 由于过滤器增加SQL校验，所以这里传SQL按加密处理
                sql+=" where 1 = 1 "+PubFunc.decrypt(customFilterCond);
            }
            TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnsInfoList, setName, this.userView, this.frameconn);
            builder.setLockable(true);
            builder.setDataSql(sql);
            builder.setOrderBy(orderBy);
            builder.setAutoRender(false);
            builder.setTitle(fieldSetName);
            builder.setSetScheme(true);
            builder.setScheme(isScheme);
            builder.setPageSize(20);
            builder.setTableTools(toolList);
            builder.setColumnFilter(true);
            builder.setSelectable(true);
            //builder.setEditable(true);
            builder.setSchemeItemKey(schemeItemKey);
            //xus 19/7/22 【50633】7.6.1封版：证照管理中不授权新建（修改）按钮权限，用户登录后通过双击档案数据，修改内容后也可以保存，不对
            String updFunc = functionPirv.get("update")==null||"".equals(functionPirv.get("update"))?"":PubFunc.decrypt((String)functionPirv.get("update"));
            if(!"".equals(updFunc)&&this.userView.hasTheFunction(updFunc)){
                builder.setRowdbclick("subSetViewTableGlobal.editSetView");
            }
            builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TITLE);

            String config = builder.createExtTableConfig();

            HashMap customParamHM = new HashMap();
            customParamHM.put("setName", setName);
            customParamHM.put("nbase", nbase);
            customParamHM.put("currentObject", currentObject);
            customParamHM.put("privType", privType);
            customParamHM.put("functionPirv", functionPirv);
            customParamHM.put("schemeItemKey", schemeItemKey);
            customParamHM.put("queryItem", queryItem);
            customParamHM.put("privParaMap", privParaMap);
            customParamHM.put("customFilterCond", customFilterCond);
            ((TableDataConfigCache)this.userView.getHm().get(subModuleId)).setCustomParamHM(customParamHM);
            //快速查询
            String condsql=getCondsql(setName,queryItem,shiftItems);
//	            this.userView.getHm(subModuleId).getCustomParamHM();

            this.getFormHM().put("tableConfig", config.toString());
            this.getFormHM().put("optionalQueryFields", (ArrayList)fieldprivMap.get("optionalQueryFields"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }
    /**
     * 判断子集是否构库
     * @param setName
     * @param dao
     * @return
     * @throws SQLException
     */
//	public boolean checkIfInUse(String setName,ContentDAO dao) throws SQLException{
//		boolean flag=false;
//		String sql="select UseFlag from fieldSet where fieldSetId = ? ";
//		ArrayList values=new ArrayList();
//		values.add(setName);
//		this.frowset=dao.search(sql,values);
//		if(this.frowset.next()){
//			if("1".equals(this.frowset.getString("UseFlag")))
//				flag=true;
//		}
//		return flag;
//	}
//	
    /**
     * 获取子集信息
     * @param setName
     * @param dao
     * @return
     * @throws SQLException
     */
    public FieldSet getFieldSetInfo(String setName,ContentDAO dao) throws SQLException{
        boolean flag=false;
        FieldSet fieldset = new FieldSet(setName);
        String sql="select UseFlag,fieldSetDesc,multimedia_file_flag from fieldSet where fieldSetId = ? ";
        ArrayList values=new ArrayList();
        values.add(setName);
        this.frowset=dao.search(sql,values);
        if(this.frowset.next()){
            fieldset.setUseflag(this.frowset.getString("UseFlag"));
            fieldset.setFieldsetdesc(this.frowset.getString("fieldsetdesc"));
            fieldset.setMultimedia_file_flag(this.frowset.getString("multimedia_file_flag"));
//			if("1".equals(this.frowset.getString("UseFlag")))
//				flag=true;
        }
        return fieldset;
    }
    /**
     * 获取表格控件的sql
     * @param setName
     * @param nbase
     * @param currentObject
     * @param privType
     * @param columns
     * @return
     */
    public String getListSql(String setName,String nbase,String currentObject,String privType,String columns,ArrayList list,HashMap paraMap){
        String sql="select * from ( ";
        String fieldid=getFieldKey(setName);
        String whereSql="";
        if(!"".equals(currentObject)){
            whereSql = " where "+setName+"."+fieldid+" = '"+currentObject+"'";
        }else{
            String privSql="";
            if("manage".equals(privType)){
                //管理范围+高级授权
                String manage = this.userView.getManagePrivCode();
                String managePriv = this.userView.getManagePrivCodeValue();
                if("UN".equals(manage)){
                    //如果是超级用户
                    if("".equals(managePriv))
                        whereSql=" where 1=1 ";
                    else{
                        privSql = getPrivSql(paraMap,nbase,fieldid,managePriv);
                        whereSql=" where "+setName+"."+fieldid+" in "+privSql;
                    }
//						whereSql=" where "+setName+"."+fieldid+" = '(select "+fieldid+" from UsrA01 where "+fieldid+" like '"+managePriv+"%') ";
                }else{
                    //无权限
                    whereSql=" where 0=1 ";
                }
                //只有人员走高级
                if(setName.startsWith("A")){
                    String privExpression = this.userView.getPrivExpression();
                    whereSql +=privExpression;
                }
            }else if("unit".equals(privType)){
                //操作单位
                String func = this.userView.getUnit_id();//UN01...
                if(func.indexOf("UN")>-1||func.indexOf("UM")>-1)
                    func=func.substring(2, func.length());
                privSql = getPrivSql(paraMap,nbase,fieldid,func);
                whereSql = " where "+setName+"."+fieldid+" in "+privSql;
            }else{
                //模块号：业务范围
                String menus=this.userView.getUnitIdByBusi(privType);//可能会有多个 `分割 UN01011052
                if("".equals(menus)){
                    whereSql = " where 0=1";
                }else{
                    String[] funcs = menus.split("`");
                    whereSql = " where ";
                    String func = "";
                    for(int i=0;i<funcs.length;i++){
                        if(i>0)
                            whereSql += " or ";
                        func = funcs[i];
                        //如果业务范围为UN则 查全部
                        if("UN".equals(func)){
                            whereSql = " where 1=1";
                            break;
                        }
                        if(func.indexOf("UN")>-1||func.indexOf("UM")>-1)
                            func=func.substring(2, func.length());
                        privSql = getPrivSql(paraMap,nbase,fieldid,func);
                        whereSql += setName+"."+fieldid+" in "+privSql;
                    }
                }
            }
//			//TODO查询框条件
//			if(list!=null&&list.size()>0){
//				for(Object obj:list){
//					if(setName.startsWith("A"))
//						whereSql += " and "+setName+".A0100 in (select A0100 from UsrA01 where A0101 like '%"+obj.toString()+"%' or A0144 = '"+obj.toString()+"' or A0103 = '"+obj.toString()+"')";
//					else 
//						whereSql += " and "+fieldid+" in (select codeitemid from organization where codeitemdesc like '%"+obj.toString()+"%' )";
//				}
//			}
        }
        if(!"".equals(nbase)){
            //人员子集
            String[] nbases=nbase.split(",");
            for(int i=0;i<nbases.length;i++){
                if(i>0){
                    sql += " union all ";
                }
                if("A01".equals(setName))
                    sql += "select '"+nbases[i]+"' nbase,"+columns+" from "+nbases[i]+setName+whereSql;
                else
                    sql += "select '"+nbases[i]+"' nbase,"+columns+" from "+nbases[i]+setName+" "+setName+","+nbases[i]+"A01 A01"+whereSql+" and "+setName+".A0100 = A01.A0100 ";
            }
        }else{
            //机构子集
            if("B01".equals(setName)||"E01".equals(setName))
                sql += "select "+columns+" from "+nbase+setName+whereSql;
            else
                sql += "select "+columns+" from "+nbase+setName+",organization"+whereSql+" and "+setName+"."+fieldid+" = organization.codeitemdesc";
        }
        sql += " ) t ";
        return sql;
    }
    /**
     * 获取子集主键
     * @param setName
     * @return
     */
    public String getFieldKey(String setName){
        String key = "A0100";
        if(setName.startsWith("A"))
            key = "A0100";
        else if(setName.startsWith("B"))
            key = "B0110";
        else if(setName.startsWith("K"))
            key = "E0122";
        else if(setName.startsWith("H"))
            key = "H0100";
        return key;
    }
    //获取菜单按钮集合
    /**
     * @return
     * @throws SQLException
     */
    public ArrayList getToolList(String setName,HashMap funcMap,String queryItem,ArrayList inpBtnsList) throws SQLException{
        ArrayList list = new ArrayList();

        ButtonInfo export = new ButtonInfo();
        export.setFunctype(ButtonInfo.FNTYPE_EXPORT);
        export.setText("导出");

        String exportFunc = funcMap.get("exp")==null||"".equals(funcMap.get("exp"))?"":PubFunc.decrypt((String)funcMap.get("exp"));
        String createFunc = funcMap.get("add")==null||"".equals(funcMap.get("add"))?"":PubFunc.decrypt((String)funcMap.get("add"));
        String updFunc = funcMap.get("update")==null||"".equals(funcMap.get("update"))?"":PubFunc.decrypt((String)funcMap.get("update"));
        String delFunc = funcMap.get("del")==null||"".equals(funcMap.get("del"))?"":PubFunc.decrypt((String)funcMap.get("del"));
        String batchUpdate = funcMap.get("batchUpdate")==null||"".equals(funcMap.get("batchUpdate"))?"":PubFunc.decrypt((String)funcMap.get("batchUpdate"));

        if(!"".equals(exportFunc)&&this.userView.hasTheFunction(exportFunc)/**||this.userView.isSuper_admin()**/)
            list.add(export);
        if(!"".equals(createFunc)&&this.userView.hasTheFunction(createFunc)/**||this.userView.isSuper_admin()**/)
            list.add(new ButtonInfo("新建","subSetViewTableGlobal.createSetView"));
        if(!"".equals(updFunc)&&this.userView.hasTheFunction(updFunc)/**||this.userView.isSuper_admin()**/)
            list.add(new ButtonInfo("修改","subSetViewTableGlobal.editSetView"));
        if(!"".equals(batchUpdate)&&this.userView.hasTheFunction(batchUpdate))
            list.add(new ButtonInfo("批量修改","subSetViewTableGlobal.batchUpdate"));
        if(!"".equals(delFunc)&&this.userView.hasTheFunction(delFunc)/**||this.userView.isSuper_admin()**/)
            list.add(new ButtonInfo("删除","subSetViewTableGlobal.delSetView"));
        //查询框提示信息
        ButtonInfo searchBox = new ButtonInfo();
        searchBox.setFunctionId("ZJ100000254");//查询所走的交易号
        String showDesc ="";
        if(queryItem.length()>0){
            String fieldSql="select itemdesc from fielditem where itemid in (";
            String[] items=queryItem.split(",");
            ArrayList values = new ArrayList();
            showDesc ="请输入";
            for(int i = 0;i<items.length;i++){
                if(i>0)
                    fieldSql += ",";

                fieldSql += "?";
                values.add(items[i].toUpperCase());
            }
            fieldSql += ") ";
            ContentDAO dao=new ContentDAO(this.frameconn);
            this.frowset = dao.search(fieldSql,values);
            while(this.frowset.next()){
                showDesc+=this.frowset.getString("itemdesc")+"、";
            }
            if(showDesc.indexOf("、")==-1){
                showDesc="";//blank text
            }else{
                showDesc=showDesc.substring(0,showDesc.length()-1);
            }
        };
        if("".equals(showDesc)){
            if(setName.startsWith("A"))
                showDesc="请输入姓名";//blank text
            else
                showDesc="请输入机构、部门、岗位名称";
        }
        searchBox.setText(showDesc);
        searchBox.setType(ButtonInfo.TYPE_QUERYBOX);//类型 查询框
        searchBox.setShowPlanBox(false);//不显示查询方案
        list.add(searchBox);
        return list;
    }

    /**
     * 快速查询
     * @param setName
     * @return
     * @throws GeneralException
     */
    public String getCondsql(String setName,String queryItem,ArrayList shiftItems) throws GeneralException{
        String subModuleId = (String)this.formHM.get("subModuleId");
        TableDataConfigCache  tableCache=  (TableDataConfigCache)userView.getHm().get(subModuleId);
        String fieldid=getFieldKey(setName);

        //如果有type参数说明是查询组件进入的
        String type = (String)this.getFormHM().get("type");
        StringBuffer condsql = new StringBuffer(" and ");
        //快速查询
        List values = (ArrayList) this.getFormHM().get("inputValues");
        //查询指标
        if(values==null || values.isEmpty()){
            condsql.append(" 1=1 ");
        }else{
            condsql.append(" ( ");
            String cond="";
            if("".equals(queryItem)){
                cond="A0101 like '%?%'";
            }else{
                String[] items=queryItem.split(",");
                for(int i=0;i<items.length;i++){
                    if(i>0)
                        cond+=" or ";
                    cond += items[i]+" like '%?%' ";
                }
            }

            for(int i=0;i<values.size();i++){
                String value =SafeCode.decode(values.get(i).toString());
                value = value.replace('\'', '‘');
                if(setName.startsWith("A"))
                    condsql.append(" ( "+cond.replace("?", value)+" ) ");
                else
                    condsql.append( fieldid+" in (select codeitemid from organization where codeitemdesc like '%"+value+"%' )");
                condsql.append( " or ");
            }
            condsql.append(" 1=2 )");
        }
        //xus 19/9/5 过滤筛选条件
        if(shiftItems.size()>0){
            condsql.append(" and ").append(createFilterWhere(shiftItems));
        }
        tableCache.setQuerySql(condsql.toString());
        String tabsql = tableCache.getTableSql();
        userView.getHm().put(subModuleId, tableCache);
        return condsql.toString();
    }

    /**
     * 将筛选条件组件返回的筛选项解析组装成sql条件语句
     * 注：多个筛选项之间是“且”的关系，形成多条件维度层层筛选的效果；
     *    字符型（包括代码和备注）同一个筛选项多个值之间“或”的关系。
     * @param shiftItems
     * @return sql条件字符串，形如： (a='str' and 1=1)
     */
    private String createFilterWhere(ArrayList shiftItems) {
        StringBuilder filterWhr = new StringBuilder();
        for (Object obj : shiftItems) {
            //且
            HashMap dynaBeanMap = PubFunc.DynaBean2Map((DynaBean) obj);
            String fieldsetid = (String) dynaBeanMap.get("fieldsetid");
            String itemid = (String) dynaBeanMap.get("itemid");
            String itemtype = (String) dynaBeanMap.get("itemtype");
            String codesetid = (String) dynaBeanMap.get("codesetid");
            String value = (String) dynaBeanMap.get("value");
            String dateType = (String) dynaBeanMap.get("type");
            String dateFormat = (String) dynaBeanMap.get("dateFormat");

            String[] split = null;
            if ("D".equalsIgnoreCase(itemtype)) {//日期
                split = value.split("~");
                String minDate = split[0];
                String maxDate = split[1];

                if ("area".equalsIgnoreCase(dateType)) {
                    String[] minsplit = null;
                    String[] maxsplit = null;
                    if ("Y-m-d H:i:s".equals(dateFormat)) {
                        if (minDate.length() == 19) {
                            minDate = Sql_switcher.dateValue(minDate);
                        }

                        if (maxDate.length() == 19) {
                            maxDate = Sql_switcher.dateValue(maxDate);
                        }

                        if ((!"*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                            filterWhr.append(itemid + " between " + minDate + " and " + maxDate + " ");
                        } else if (("*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                            filterWhr.append(itemid + " <= " + maxDate);
                        } else if ((!"*".equalsIgnoreCase(minDate)) && ("*".equalsIgnoreCase(maxDate))) {
                            filterWhr.append(itemid + " >= " + minDate);
                        } else {
                            continue;
                        }
                    } else if ("Y-m-d H:i".equals(dateFormat)) {
                        if (minDate.length() == 16) {
                            minDate = minDate + ":00";
                            minDate = Sql_switcher.dateValue(minDate);
                        }

                        if (maxDate.length() == 16) {
                            maxDate = maxDate + ":59";
                            maxDate = Sql_switcher.dateValue(maxDate);
                        }

                        if ((!"*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                            filterWhr.append(itemid + " between " + minDate + " and " + maxDate + " ");
                        } else if (("*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                            filterWhr.append(itemid + " <= " + maxDate);
                        } else if ((!"*".equalsIgnoreCase(minDate)) && ("*".equalsIgnoreCase(maxDate))) {
                            filterWhr.append(itemid + " >= " + minDate);
                        } else {
                            continue;
                        }
                    } else if ("Y-m-d".equals(dateFormat)) {
                        if (minDate.length() == 10) {
                            minDate = minDate + " 00:00:00";
                            minDate = Sql_switcher.dateValue(minDate);
                        }
                        if (maxDate.length() == 10) {
                            maxDate = maxDate + " 23:59:59";
                            maxDate = Sql_switcher.dateValue(maxDate);
                        }
                        if ((!"*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                            filterWhr.append(itemid + " between " + minDate + " and " + maxDate + " ");
                        } else if (("*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                            filterWhr.append(itemid + " <= " + maxDate + "");
                        } else if ((!"*".equalsIgnoreCase(minDate)) && ("*".equalsIgnoreCase(maxDate))) {
                            filterWhr.append(itemid + " >= " + minDate + " ");
                        } else {
                            continue;
                        }
                    } else if ("Y-m".contains((dateFormat))) {
                        if ((!"*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                            maxsplit = maxDate.split("-");
                            minsplit = minDate.split("-");
                            filterWhr.append(Sql_switcher.year(itemid) + " <= " + maxsplit[0]);
                            if (dateFormat.contains("m") && maxsplit.length > 2)
                                filterWhr.append(" and " + Sql_switcher.month(itemid) + " <= " + maxsplit[1] + "");
                            filterWhr.append(" and " + Sql_switcher.year(itemid) + " >= " + minsplit[0]);
                            if (dateFormat.contains("m") && minsplit.length > 2)
                                filterWhr.append(" and " + Sql_switcher.month(itemid) + " >= " + minsplit[1] + "");
                        } else if (("*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                            maxsplit = maxDate.split("-");
                            filterWhr.append(Sql_switcher.year(itemid) + " <= " + maxsplit[0]);
                            if (dateFormat.contains("m") && maxsplit.length > 2)
                                filterWhr.append(" and " + Sql_switcher.month(itemid) + " <= " + maxsplit[1] + "");
                        } else if ((!"*".equalsIgnoreCase(minDate)) && ("*".equalsIgnoreCase(maxDate))) {
                            minsplit = minDate.split("-");
                            filterWhr.append(Sql_switcher.year(itemid) + " >= " + minsplit[0]);
                            if (dateFormat.contains("m") && minsplit.length > 2)
                                filterWhr.append(" and " + Sql_switcher.month(itemid) + " >= " + minsplit[1] + "");
                        } else {
                            continue;
                        }
                    }
                } else if (StringUtils.equalsIgnoreCase(dateType, "year")) {
                    //年限

                    if ((!"*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                        filterWhr.append(Sql_switcher.diffYears(Sql_switcher.today(), itemid) + " between '" + minDate + "' and '" + maxDate + "'");
                    } else if ((!"*".equalsIgnoreCase(minDate)) && ("*".equalsIgnoreCase(maxDate))) {
                        filterWhr.append(Sql_switcher.diffYears(Sql_switcher.today(), itemid) + ">='" + minDate + "'");
                    } else if (("*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                        filterWhr.append(Sql_switcher.diffYears(Sql_switcher.today(), itemid) + "<='" + maxDate + "'");
                    } else {
                        continue;
                    }
                } else if (StringUtils.equalsIgnoreCase(dateType, "month")) {
                    if ((!"*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                        filterWhr.append(Sql_switcher.month(itemid) + " between '" + minDate + "' and '" + maxDate + "'");
                    } else if ((!"*".equalsIgnoreCase(minDate)) && ("*".equalsIgnoreCase(maxDate))) {
                        filterWhr.append(Sql_switcher.month(itemid) + ">='" + minDate + "'");
                    } else if (("*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                        filterWhr.append(Sql_switcher.month(itemid) + "<='" + maxDate + "'");
                    } else {
                        continue;
                    }
                } else if (StringUtils.equalsIgnoreCase(dateType, "day"))//天
                {
                    if ((!"*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                        filterWhr.append(Sql_switcher.day(itemid) + " between '" + minDate + "' and '" + maxDate + "'");
                    } else if ((!"*".equalsIgnoreCase(minDate)) && ("*".equalsIgnoreCase(maxDate))) {
                        filterWhr.append(Sql_switcher.day(itemid) + ">='" + minDate + "'");
                    } else if (("*".equalsIgnoreCase(minDate)) && (!"*".equalsIgnoreCase(maxDate))) {
                        filterWhr.append(Sql_switcher.day(itemid) + "<='" + minDate + "'");
                    } else {
                        continue;
                    }
                }
            } else if ("N".equalsIgnoreCase(itemtype)) {
                split = value.split("~");
                String minValue = split[0];
                String maxValue = split[1];

                if ((!"*".equalsIgnoreCase(minValue)) && (!"*".equalsIgnoreCase(maxValue))) {
                    filterWhr.append(Sql_switcher.isnull(itemid, "0") + " between " + minValue + " and " + maxValue);
                } else if (("*".equalsIgnoreCase(minValue)) && (!"*".equalsIgnoreCase(maxValue))) {
                    filterWhr.append(Sql_switcher.isnull(itemid, "0") + " <= " + maxValue + "");
                } else if ((!"*".equalsIgnoreCase(minValue)) && ("*".equalsIgnoreCase(maxValue))) {
                    filterWhr.append(Sql_switcher.isnull(itemid, "0") + " >= " + minValue + "");
                } else {
                    continue;
                }
            } else {
                String[] shiftValues = value.split(",");
                filterWhr.append(" ( ");
                for (String strValue : shiftValues) {
                    if (!"".equals(strValue)) {
                        filterWhr.append(itemid).append(" like '");
                        //非代码型的普通字符全模糊匹配
                        if (StringUtils.isBlank(codesetid) || "0".equalsIgnoreCase(codesetid))
                            filterWhr.append("%");
                        filterWhr.append(strValue).append("%' or ");
                    }
                }
                filterWhr.append(" 1=2 ) ");
            }

            filterWhr.append(" and ");
        }
        filterWhr.append(" 1=1 ");

        return "(" + filterWhr.toString() + ")";
    }

    /**
     * 获取权限的 条件语句
     * @param paraMap 权限信息map： table，item 两个属性
     * @param nbase 人员库 可为多个   用 “,”分割
     * @param fieldid 权限子集（或主集）的key 如：（A0100，B0110,E0122）
     * @return
     */
    private String getPrivSql(HashMap paraMap,String nbase,String fieldid,String priv){
        String sql = "(";
        if(paraMap.size()>0){
            String table = paraMap.get("table")==null?"":(String)paraMap.get("table");
            String item = paraMap.get("item")==null?"":(String)paraMap.get("item");
            if(!"".equals(table)&&!"".equals(item)){
                FieldItem fielditem = DataDictionary.getFieldItem(item);
                if(fielditem!=null&&!"0".equals(fielditem.getUseflag())){
                    if(table.toUpperCase().startsWith("A")){
                        String[] nbases = nbase.split(",");
                        for(int i=0;i<nbases.length;i++){
                            if(i>0)
                                sql += " union all ";
                            sql += "select "+fieldid+" from "+nbases[i]+table+" where "+item+" like '"+priv+"%' ";
                        }
                    }else{
                        sql += "select "+fieldid+" from "+table+" where "+item+" like '"+priv+"%' ";
                    }
                    sql += ")";
                    return sql;
                }
            }
        }
        String[] nbases = nbase.split(",");
        for(int i=0;i<nbases.length;i++){
            if(i>0)
                sql += " union all ";
            sql += "select "+fieldid+" from "+nbases[i]+"A01 where B0110 like '"+priv+"%' ";
        }
        sql += ")";
        return sql;
    }

    /**
     *
     * @param setName
     * @param nbase
     * @param currentObject
     * @param subModuleId
     * @param privType
     * @param dao
     * @param isScheme
     * @return
     * @throws SQLException
     */
    public HashMap getTableFieldList(String setName,String nbase,String currentObject,String subModuleId,String privType,ContentDAO dao,Boolean isScheme) throws SQLException{
        //1 根据subModuleId 查询t_sys_table_scheme表
        String schemeId = "";
        String sql = "";
        ArrayList values = new ArrayList();
        ArrayList fieldList = new ArrayList();
        ArrayList schemeRemovableList = new ArrayList();
        ArrayList optionalQueryFields = new ArrayList();
        HashMap returnMap = new HashMap();
        if(isScheme){
            sql = "select scheme_id from t_sys_table_scheme where submoduleid = ? and username = ? and is_share = 0 ";
            values.add(subModuleId);
            values.add(this.getUserView().getUserName());
            this.frowset = dao.search(sql,values);
            if(this.frowset.next()){
                schemeId = this.frowset.getString("scheme_id");
            }
            if("".equals(schemeId)){
                values = new ArrayList();
                sql = "select scheme_id from t_sys_table_scheme where submoduleid = ? and is_share = 1 ";
                values.add(subModuleId);
                this.frowset = dao.search(sql,values);
                if(this.frowset.next()){
                    schemeId = this.frowset.getString("scheme_id");
                }
            }
            if(!"".equals(schemeId)){
                //查询栏目设置字段
                values = new ArrayList();
                sql = "select itemid,fieldsetid,is_removable from t_sys_table_scheme_item where scheme_id = ? and "+Sql_switcher.isnull("fieldsetid", "'null'")+" <> 'null'";
                values.add(schemeId);
                this.frowset = dao.search(sql,values);
                FieldItem item = null;
                while(this.frowset.next()){
                    if(this.frowset.getString("fieldsetid").length()==3){
                        item = DataDictionary.getFieldItem(this.frowset.getString("itemid"));
                        HashMap hm = new HashMap();
                        // 59256 必须校验指标是否可用
                        if(null != item && "1".equals(item.getUseflag())){
                            fieldList.add(item);
                            //筛选条件中,item对象没有itemtype，codesetid属性，从新拼成map
                            hm = new HashMap();
                            hm.put("fieldsetid", item.getFieldsetid());
                            hm.put("itemid", item.getItemid());
                            hm.put("itemdesc", item.getItemdesc());
                            hm.put("itemtype", item.getItemtype());
                            hm.put("codesetid", item.getCodesetid());
                            optionalQueryFields.add(hm);
                        }
                        if("1".equals(this.frowset.getString("is_removable")))
                            schemeRemovableList.add(this.frowset.getString("itemid"));
                    }
                }
            }
        }
        if(fieldList.size()==0){
            fieldList=(ArrayList) this.userView.getPrivFieldList(setName).clone();
            optionalQueryFields = fieldList;
        }
        returnMap.put("fieldList", fieldList);
        returnMap.put("optionalQueryFields", optionalQueryFields);
        returnMap.put("schemeRemovableList", schemeRemovableList);
        return returnMap;
    }

    public HashMap getTableBuilderInfo(String setName,String nbase,String currentObject,HashMap fieldprivMap,ArrayList lockColumnsList,String filterColumn,ContentDAO dao,String privType,HashMap paraMap) throws SQLException{
        HashMap infoMap = new HashMap();
        String privKey=getFieldKey(setName);
        FieldSet fieldSet=getFieldSetInfo(setName,dao);

        ArrayList columnsInfoList = new ArrayList();
        String columns="";
        ArrayList setList = new ArrayList();

        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
        String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");// 唯一性指标

        ArrayList fieldList = (ArrayList) fieldprivMap.get("fieldList");
        ArrayList schemeRemovableList = (ArrayList) fieldprivMap.get("schemeRemovableList");

        //如果栏目设置无数据
        if(setList.size()==1&&setName.equals(setList.get(0).toString())){

        }

        //单位
        if("".equals(currentObject)&&!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)){
            FieldItem item = null;
            ColumnsInfo columnInfo = null;
            if(setName.startsWith("A")){
                item = DataDictionary.getFieldItem("B0110");
                columnInfo = new ColumnsInfo(item);
                columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                columnInfo.setSortable(true);
                columnInfo.setEditableValidFunc("false");
                columnInfo.setLocked(true);
                //过滤列
                if(filterColumn.toUpperCase().equals(item.getItemid().toUpperCase()))
                    columnInfo.setDoFilterOnLoad(true);
                columnsInfoList.add(columnInfo);
                columns+="A01.B0110 B0110,";
            }
        }
        //部门
        if("".equals(currentObject)&&!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)){
            FieldItem item = null;
            ColumnsInfo columnInfo = null;
            if(setName.startsWith("A")){
                item = DataDictionary.getFieldItem("E0122");
                columnInfo = new ColumnsInfo(item);
                columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                columnInfo.setSortable(true);
                columnInfo.setEditableValidFunc("false");
                columnInfo.setLocked(true);
                //栏目设置默认列
                if(schemeRemovableList.contains(item.getItemid()))
                    columnInfo.setRemovable(true);;
                columnsInfoList.add(columnInfo);
                columns+="A01.E0122 E0122,";
            }
        }
        //岗位
        if("".equals(currentObject)&&!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)){
            FieldItem item = null;
            ColumnsInfo columnInfo = null;
            if(setName.startsWith("A")){
                item = DataDictionary.getFieldItem("E01A1");
                columnInfo = new ColumnsInfo(item);
                columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                columnInfo.setSortable(true);
                columnInfo.setEditableValidFunc("false");
                columnInfo.setLocked(true);
                //过滤列
                if(filterColumn.toUpperCase().equals(item.getItemid().toUpperCase()))
                    columnInfo.setDoFilterOnLoad(true);
                columnsInfoList.add(columnInfo);
                columns+="A01.E01A1 E01A1,";
            }
        }

        // 没有currentObject 要显示姓名、机构名
        if("".equals(currentObject)&&!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)){
            FieldItem item = null;
            ColumnsInfo columnInfo = null;
            if(setName.startsWith("A")){
                item = DataDictionary.getFieldItem("A0101");
                columnInfo = new ColumnsInfo(item);
                columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                columnInfo.setSortable(true);
                columnInfo.setEditableValidFunc("false");
                columnInfo.setLocked(true);
                //过滤列
                if(filterColumn.toUpperCase().equals(item.getItemid().toUpperCase()))
                    columnInfo.setDoFilterOnLoad(true);
                columnsInfoList.add(columnInfo);
                columns+="A01.A0101 A0101,";
            }
        }
        //唯一性指标
        if("".equals(currentObject)&&!"A01".equals(setName)&&!"B01".equals(setName)&&!"K01".equals(setName)){
            FieldItem item = null;
            ColumnsInfo columnInfo = null;
            if(!"".equals(onlyname)&&setName.startsWith("A")){
                // 49428  校验唯一性指标是否与 B0110,E0122,E01A1,A0101 重复
                if(!",B0110,E0122,E01A1,A0101,".contains(","+onlyname.toUpperCase()+",")) {
                    item = DataDictionary.getFieldItem(onlyname);
                    columnInfo = new ColumnsInfo(item);
                    columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                    columnInfo.setSortable(true);
                    //过滤列
                    if(filterColumn.toUpperCase().equals(item.getItemid().toUpperCase()))
                        columnInfo.setDoFilterOnLoad(true);
                    columnInfo.setEditableValidFunc("false");
                    columnsInfoList.add(columnInfo);
                    columns+="A01."+onlyname+" "+onlyname+",";
                }
            }
        }

        for(Object obj:fieldList){
            FieldItem item=(FieldItem)obj;
            if("A0100".equals(item.getItemid().toUpperCase())||"A0101".equals(item.getItemid().toUpperCase())
                    ||"B0110".equals(item.getItemid().toUpperCase())||"E0122".equals(item.getItemid().toUpperCase())
                    ||"E01A1".equals(item.getItemid().toUpperCase())
                    ||(!"".equals(onlyname)&&onlyname.toUpperCase().equals(item.getItemid().toUpperCase())))
                continue;
            ColumnsInfo columnInfo = new ColumnsInfo(item);
            columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            columnInfo.setSortable(true);
            columnInfo.setEditableValidFunc("false");
            //锁列
            if(lockColumnsList.contains(item.getItemid().toUpperCase()))
                columnInfo.setLocked(true);
            //过滤列
            if(filterColumn.toUpperCase().equals(item.getItemid().toUpperCase()))
                columnInfo.setDoFilterOnLoad(true);
            //栏目设置默认列
            if(schemeRemovableList.contains(item.getItemid()))
                columnInfo.setRemovable(true);
            columnsInfoList.add(columnInfo);

            columns+=item.getFieldsetid()+"."+item.getItemid()+",";
            if(!setList.contains(item.getFieldsetid())&&!setName.equals(item.getFieldsetid()))
                setList.add(item.getFieldsetid());
        }

        //A0100、B0110...
        ColumnsInfo columnInfo = new ColumnsInfo();
        columnInfo.setColumnId(privKey);
        columnInfo.setColumnDesc("privKey");
        columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnInfo.setEncrypted(true);
        columnsInfoList.add(columnInfo);
        if(setName.startsWith("A")){
            if("A01".equals(setName))
                columns+=privKey+",";
            else
                columns+=setName+"."+privKey+" "+privKey+",";
        }else{
            if("B01".equals(setName)||"E01".equals(setName))
                columns+=privKey+",";
            else
                columns+="organization."+privKey+" "+privKey+",";
        }
        //I9999
        if(setName.startsWith("A")&&!"A01".equals(setName)){
            columnInfo = new ColumnsInfo();
            columnInfo.setColumnId("I9999");
            columnInfo.setColumnDesc("dataIndex");
            columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnsInfoList.add(columnInfo);
            columns+=setName+".I9999,";
        }
        //nbase
        if(setName.startsWith("A")){
            columnInfo = new ColumnsInfo();
            columnInfo.setColumnId("nbase");
            columnInfo.setColumnDesc("nbase");
            columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnsInfoList.add(columnInfo);
        }
        //attachment 附件
        if("1".equals(fieldSet.getMultimedia_file_flag())){
            columnInfo = new ColumnsInfo();
            //columnInfo.setColumnId("file");
            columnInfo.setColumnDesc("附件");
            columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            columnInfo.setRendererFunc("subSetViewTableGlobal.addAttachment");
            columnsInfoList.add(columnInfo);
            //是否有附件数据标识
            columnInfo = new ColumnsInfo();
            columnInfo.setColumnId("imgpic");
            columnInfo.setColumnDesc("imgpic");
            columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            columnsInfoList.add(columnInfo);
        }
        if(columns.length()>0)
            columns=columns.substring(0, columns.length()-1);

        String sql = getTableBuilderSql(setName, nbase, currentObject,setList, privType, columns, paraMap);
        infoMap.put("sql", sql);
        infoMap.put("columnsInfoList", columnsInfoList);
        return infoMap;
    }

    /**
     * 获取表格控件的查询语句
     * @param setName
     * @param nbase
     * @param currentObject
     * @param setList
     * @param privType
     * @param columns
     * @param list
     * @param paraMap
     * @param flag 是否有子集附件标识
     * @return
     */
    public String getTableBuilderSql(String setName,String nbase,String currentObject,ArrayList setList,String privType,String columns,HashMap paraMap){
        String sql="select * from ( ";
        String fieldid=getFieldKey(setName);
        String whereSql="";
        boolean flag=false;

        if(!"".equals(nbase)){
            //人员子集
            String[] nbases=nbase.split(",");
            for(int i=0;i<nbases.length;i++){
                if(i>0){
                    sql += " union all ";
                }
                String newcolumns = columns;
                //xus 18/9/6 如果表中没有guidkey字段 则附件数为0
                DbWizard db = new DbWizard(this.frameconn);
                if(!db.isExistField(nbases[i]+setName, "GUIDKEY",false)){
                    newcolumns +=",'false' imgpic";
                    flag=false;
                }else{
                    newcolumns +=",(case "+Sql_switcher.isnull("sumcount","0")+" when 0 then 'false' else 'true' end) imgpic";
                    flag=true;
                }
                if("A01".equals(setName)){
                    sql += "select '"+nbases[i]+"' nbase,"+newcolumns+" from "+nbases[i]+setName+" "+setName+
                            " left join (select mainguid,childguid,COUNT(mainguid) sumcount from hr_multimedia_file group by mainguid,childguid having "+Sql_switcher.isnull("childguid", "''")+" = '') hmf on "+setName+".GUIDKEY=hmf.mainguid  "
                            +getWhereSql(setName, nbases[i], currentObject, fieldid, privType, paraMap);
                }else{
//					sql += "select '"+nbases[i]+"' nbase,"+columns+" from "+nbases[i]+setName+" "+setName+","+nbases[i]+"A01 A01"+getWhereSql(setName, nbases[i], currentObject, fieldid, privType, paraMap)+" and "+setName+".A0100 = A01.A0100 ";
                    String fromSql=" from "+nbases[i]+setName+" "+setName;
                    if(flag){
                        fromSql += " left join (select childguid,COUNT(childguid) sumcount from hr_multimedia_file group by childguid) hmf on "+setName+".GUIDKEY=hmf.childguid  ";
                    }
                    if(!setList.contains("A01"))
                        fromSql += " left join "+nbases[i]+"A01 A01 on "+setName+"."+fieldid+" = A01."+fieldid;
                    for(Object o : setList){
                        String fieldSet=(String)o;
                        fromSql += " left join "+nbases[i]+fieldSet+" "+fieldSet+" on "+setName+"."+fieldid+" = "+fieldSet+"."+fieldid;
                    }
                    sql += "select '"+nbases[i]+"' nbase,"+newcolumns+fromSql+getWhereSql(setName, nbases[i], currentObject, fieldid, privType, paraMap);
                }
            }
        }else{
            //机构子集
            if("B01".equals(setName)||"E01".equals(setName))
                sql += "select "+columns+" from "+nbase+setName+whereSql;
            else
                sql += "select "+columns+" from "+nbase+setName+",organization"+whereSql+" and "+setName+"."+fieldid+" = organization.codeitemdesc";
        }
        sql += " ) t ";

        return sql;
    }

    /**
     * 获取单个nbase的查询语句
     * @param setName
     * @param nbase
     * @param currentObject
     * @param fieldid
     * @param privType
     * @param paraMap
     * @return
     */
    public String getWhereSql(String setName,String nbase,String currentObject,String fieldid,String privType,HashMap paraMap){
        String whereSql="";
        if(!"".equals(currentObject)){
            whereSql = " where "+setName+"."+fieldid+" = '"+currentObject+"'";
        }else{
            String privSql="";
            if("manage".equals(privType)){
                //管理范围+高级授权
                String manage = this.userView.getManagePrivCode();
                String managePriv = this.userView.getManagePrivCodeValue();
                if("UN".equals(manage)){
                    //如果是超级用户
                    if("".equals(managePriv))
                        whereSql=" where 1=1 ";
                    else{
                        privSql = getNewPrivSql(setName,paraMap,nbase,fieldid,managePriv);
                        whereSql=" where  1=1 "+privSql;
                    }
//						whereSql=" where "+setName+"."+fieldid+" = '(select "+fieldid+" from UsrA01 where "+fieldid+" like '"+managePriv+"%') ";
                }else{
                    //无权限
                    whereSql=" where 0=1 ";
                }
                //只有人员走高级
                if(setName.startsWith("A")){
                    String privExpression = this.userView.getPrivExpression();
                    whereSql +=privExpression;
                }
            }else if("unit".equals(privType)){
                //操作单位
                String func = this.userView.getUnit_id();//UN01...
                if(func.indexOf("UN")>-1||func.indexOf("UM")>-1)
                    func=func.substring(2, func.length());
                privSql = getNewPrivSql(setName,paraMap,nbase,fieldid,func);
                whereSql = " where 1=1 "+privSql;
            }else{
                //模块号：业务范围
                String menus=this.userView.getUnitIdByBusi(privType);//可能会有多个 `分割 UN01011052
                if("".equals(menus)){
                    whereSql = " where 0=1";
                }else{
                    String[] funcs = menus.split("`");
                    whereSql = " where 1=1 and (";
                    String func = "";
                    for(int i=0;i<funcs.length;i++){
                        if(i>0)
                            whereSql += " or ";
                        func = funcs[i];
                        //如果业务范围为UN则 查全部
                        if("UN".equals(func)){
                            whereSql = " where 1=1";
                            break;
                        }
                        if(func.indexOf("UN")>-1||func.indexOf("UM")>-1)
                            func=func.substring(2, func.length());
                        privSql = getNewPrivSql(setName,paraMap,nbase,fieldid,func);
                        whereSql += privSql.replaceFirst("and", " ");
                        if ( i==(funcs.length-1)) {
                            whereSql +=")";
                        }
                    }
                }
            }
        }
        return whereSql;
    }
    /**
     *
     * @param setName 子集
     * @param paraMap 权限信息map： table，item 两个属性
     * @param nbase 人员库
     * @param fieldid 权限子集（或主集）的key 如：（A0100，B0110,E0122）
     * @param priv 权限参数
     * @return
     */
    public String getNewPrivSql(String setName,HashMap paraMap,String nbase,String fieldid,String priv){
        String sql = "";
        if(paraMap.size()>0){
            String table = paraMap.get("table")==null?"":(String)paraMap.get("table");
            String item = paraMap.get("item")==null?"":(String)paraMap.get("item");
            if(!"".equals(table)&&!"".equals(item)){
                if(setName.equals(table)){
                    FieldItem fielditem = DataDictionary.getFieldItem(item);
                    if(fielditem!=null&&!"0".equals(fielditem.getUseflag())){
                        sql += " and "+item+" like '"+priv+"%' ";
                    }else{
                        //TODO 没有查询到指标该如何处理？ and 1=0 ？
                    }
                }else{
                    sql += " and "+setName+"."+fieldid+" in (select "+fieldid+" from "+nbase+table+" where "+item+" like '"+priv+"%' )";
                }
            }
        }
        return sql;
    }
}

