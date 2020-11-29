/**
 * FileName: StandTableServiceImpl
 * Author:   xuchangshun
 * Date:     2019/11/22 17:34
 * Description: 标准表服务接口实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.standard.standard.businessobject.impl;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.IStandTableService;
import com.hjsj.hrms.module.gz.standard.standard.repository.IStandTableDao;
import com.hjsj.hrms.module.gz.standard.standard.repository.impl.StandTableDaoImpl;
import com.hjsj.hrms.module.gz.standard.standard.utils.StandardItemVoUtil;
import com.hjsj.hrms.module.gz.standard.utils.StandardUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.*;

/**
 * 〈类功能描述〉<br>
 * 〈标准表服务接口实现类〉
 *
 * @Author xuchangshun
 * @Date 2019/11/22
 * @since 1.0.0
 */
public class StandTableServiceImpl implements IStandTableService {
    /**数据库链接conn**/
    private Connection conn;
    /**当前登录用户相关信息**/
    private UserView userView;
    /**数据层操作类**/
    private IStandTableDao standTableDao;
    /**
     * 薪资标准历史沿革实现类构造方案
     * @Author xuchangshun
     * @param conn : 数据库链接
     * @param userView : 当前登录用户
     * @Date 2019/11/6 13:44
     */
    public StandTableServiceImpl(Connection conn,UserView userView){
        this.conn = conn;
        this.userView = userView;
        standTableDao = new StandTableDaoImpl(conn);
    }
    /**
     * 获取标准表列表表格组件
     * @Author xuchangshun
     * @param pkg_id 历史沿革id
     * @return String 表格组件
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:30
     */
    @Override
    public String getStandardTableConfig(String pkg_id) throws GeneralException {
        String gridconfig = "";
        //存放列的list集合
        ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
        //存放按钮的list集合
        ArrayList<ButtonInfo> buttonTmp = new ArrayList<ButtonInfo>();
        if (this.userView.isSuper_admin()||this.userView.hasTheFunction("3241008")){//判断新建功能按钮是否授权
            //新建按钮
            buttonTmp.add(new ButtonInfo(ResourceFactory.getProperty("standard.standardTable.addButton"), "StandardList.addStandardFunc"));
        }
        if(this.userView.isSuper_admin()||this.userView.hasTheFunction("3241010")){//判断删除功能按钮是否授权
            //删除按钮
            buttonTmp.add(new ButtonInfo(ResourceFactory.getProperty("standard.standardTable.deleteButton"), "StandardList.deleteStandList"));
        }
        if(this.userView.isSuper_admin()||this.userView.hasTheFunction("3241012")) {//判断导出功能按钮是否授权
            //导出按钮
            buttonTmp.add(new ButtonInfo(ResourceFactory.getProperty("standard.standardTable.exportButton"), "StandardList.exportStandardListExcel"));
        }
        //返回按钮
        buttonTmp.add(new ButtonInfo(ResourceFactory.getProperty("standard.standardTable.returnButton"), "StandardList.returnStandardPackage"));
        //编号列(显示列)
        ColumnsInfo idColumns = getColumnsInfo("id", ResourceFactory.getProperty("standard.standardTable.id"), 50, "N");
        columnTmp.add(idColumns);
        //编号列(加密列,只加载数据)
        ColumnsInfo id_eColumns = getColumnsInfo("id_e", ResourceFactory.getProperty("standard.standardTable.id"), 50, "N");
        id_eColumns.setEncrypted(true);
        id_eColumns.setLoadtype(3);
        columnTmp.add(id_eColumns);
        //套序号列(只加载数据)
        ColumnsInfo pkg_idColumns = getColumnsInfo("pkg_id", "", 36, "A");
        pkg_idColumns.setEncrypted(true);
        pkg_idColumns.setLoadtype(3);
        columnTmp.add(pkg_idColumns);
        //名称列
        ColumnsInfo nameColumns = getColumnsInfo("name", ResourceFactory.getProperty("standard.standardTable.name"), 200, "A");
        nameColumns.setRendererFunc("StandardList.renderNameColumnFunc");//自定义渲染函数
        nameColumns.setColumnLength(100);//长度限制100个字符（50个汉字）
        columnTmp.add(nameColumns);
        //横向指标
        ColumnsInfo lateralIndex = getColumnsInfo("", ResourceFactory.getProperty("standard.standardTable.lateralIndex"), 226, "A");
        ColumnsInfo HfactorColumns = getColumnsInfo("hfactor", ResourceFactory.getProperty("standard.standardTable.firstLevel"), 113, "A");
        ColumnsInfo S_hfactorColumns = getColumnsInfo("s_hfactor", ResourceFactory.getProperty("standard.standardTable.secondLevel"), 125, "A");
        lateralIndex.addChildColumn(HfactorColumns);//横向指标添加一级指标
        lateralIndex.addChildColumn(S_hfactorColumns);//横向指标添加二级指标
        columnTmp.add(lateralIndex);
        //纵向指标
        ColumnsInfo verticalIndex = getColumnsInfo("", ResourceFactory.getProperty("standard.standardTable.verticalIndex"), 226, "A");
        ColumnsInfo VfactorColumns = getColumnsInfo("vfactor", ResourceFactory.getProperty("standard.standardTable.firstLevel"), 113, "A");//纵向指标添加一级指标
        ColumnsInfo S_vfactorColumns = getColumnsInfo("s_vfactor", ResourceFactory.getProperty("standard.standardTable.secondLevel"), 113, "A");//纵向指标添加二级指标
        verticalIndex.addChildColumn(VfactorColumns);
        verticalIndex.addChildColumn(S_vfactorColumns);
        columnTmp.add(verticalIndex);
        //结果指标
        ColumnsInfo itemColumns = getColumnsInfo("item", ResourceFactory.getProperty("standard.standardTable.item"), 150, "A");
        columnTmp.add(itemColumns);
        //归属单位指标
        ColumnsInfo organizationColumns = getColumnsInfo("b0110", ResourceFactory.getProperty("standard.standardTable.organization"), 180, "A");
        organizationColumns.setRendererFunc("StandardList.renderB0110ColumnFunc");//自定义渲染函数
        columnTmp.add(organizationColumns);
        TableConfigBuilder builder = new TableConfigBuilder("StandardList", columnTmp, "StandardList", this.userView, this.conn);
        builder.setSelectable(true);//可勾选
        if (this.userView.isSuper_admin()||this.userView.hasTheFunction("3241009")||this.userView.hasTheFunction("3241011")) {//判断编辑以及重命名的功能是否授权
            builder.setEditable(true);//可编辑
        }
        builder.setTableTools(buttonTmp);//设置按钮组件
        builder.setTitle(ResourceFactory.getProperty("standard.standardTable.title"));
        builder.setDataSql(getSalaryStandardList(pkg_id));//数据来源
        builder.setPageSize(20);
        builder.setCurrentPage(1);
        builder.setSortable(false);
        builder.setOrderBy("order by id");
        gridconfig = builder.createExtTableConfig();
        return gridconfig;
    }

    /**
     * 获取标准表列表编辑权限集合
     * @Author houby
     * @param pkg_id 历史沿革id
     * @return map 标准表id以及权限标识
     * @throws GeneralException 异常信息
     * @Date 2019/12/13 13:15
     */
    @Override
    public Map getStandardCreatePriv(String pkg_id) throws GeneralException {
        Map map = new HashMap();
        ContentDAO dao = new ContentDAO(this.conn);
        String isCreateOrg="0";//等于1 有创建单位权限， 0 没有创建单位权限
        String unitid = "XXXX";
        try {
            RowSet rs = dao.search(getSalaryStandardList(pkg_id));
            unitid=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
            String[] unit_arr = unitid.split("`");
            while (rs.next()){
                String id = rs.getString("id");
                String createorg = rs.getString("createorg")==null?"":rs.getString("createorg").toUpperCase();
                if(this.userView.isSuper_admin()||this.userView.getVersion_flag()==0) {
                    isCreateOrg="1";
                } else if("".equals(createorg)) {
                    if(this.userView.isSuper_admin()) {
                        isCreateOrg = "1";
                    }
                }else if(!"XXXX".equalsIgnoreCase(unitid)) {
                    if("UN".equalsIgnoreCase(unitid)&& "UN".equalsIgnoreCase(createorg)) {
                        isCreateOrg = "1";
                    } else {
                        boolean flag = false;
                        for(int i=0;i<unit_arr.length;i++) {
                            if(createorg.length()>=unit_arr[i].substring(2).length()){
                                createorg=createorg.substring(0,unit_arr[i].substring(2).length());
                            }
                            if(unit_arr[i]==null|| "".equals(unit_arr[i])||unit_arr[i].length()<2) {
                                continue;
                            }
                            if(createorg.equalsIgnoreCase(unit_arr[i].substring(2))) {
                                flag=true;
                                break;
                            }
                        }
                        if(flag) {
                            isCreateOrg = "1";
                        } else {
                            isCreateOrg = "0";
                        }
                    }
                }
                map.put(id,isCreateOrg);
            }
            if (this.userView.isSuper_admin()||this.userView.hasTheFunction("3241009")) {//判断编辑功能是否授权
                map.put("isEdit","1");
            }else{
                map.put("isEdit","0");
            }
            if(this.userView.hasTheFunction("3241011")){//判断重命名功能是否授权
                map.put("reName","1");
            }else{
                map.put("reName","0");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取标准表结构
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @return Map 标准表结构
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:35
     */
    @Override
    public Map getStandStructInfor(String pkg_id, String stand_id) throws GeneralException {
        Map<String,String> standStructInforMap = new HashMap<String, String>();
        try {
            RecordVo vo = standTableDao.getStandTableInfor(pkg_id, stand_id);
            if (vo != null) {
                //横向栏目指标 itemid
                standStructInforMap.put("hfactor",vo.getString("hfactor"));
                //横向子栏目指标 itemid
                standStructInforMap.put("s_hfactor",vo.getString("s_hfactor"));
                //纵向栏目指标 itemid
                standStructInforMap.put("vfactor",vo.getString("vfactor"));
                //纵向子栏目指标 itemid
                standStructInforMap.put("s_vfactor",vo.getString("s_vfactor"));
                //结果指标 itemid
                standStructInforMap.put("item",vo.getString("item"));
                //横向数据
                standStructInforMap.put("hcontent",vo.getString("hcontent"));
                //纵向数据
                standStructInforMap.put("vcontent",vo.getString("vcontent"));
                //标准表名称
                standStructInforMap.put("name",vo.getString("name"));
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new GeneralException("getStandStructInforError");
        }
        return standStructInforMap;
    }

    /**
     * 获得二级指标表达式的内容
     * @Author xuchangshun
     * @param item :要操作的二级指标id
     * @param item_id :要操作二级指标第几个表达式
     * @return List 二级表达式的内容
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:38
     */
    @Override
    public List getItemLexpr(String item, String item_id) throws GeneralException {
        IStandTableDao daoImpl = new StandTableDaoImpl(conn);
        RecordVo vo = new RecordVo("gz_stand_date");
        item = item.toUpperCase();
        Map return_LexprInfor = null;
        List return_data = new ArrayList();
        List dataList = new ArrayList();
        
        if (StringUtils.isNotEmpty(item_id)) {
            vo = daoImpl.getItemLexpr(item,item_id);
            dataList.add(vo);
        } else {
            dataList = daoImpl.getItemLexprList(item);
        }
        
        for (int i = 0; i < dataList.size(); i++) {
            return_LexprInfor = new HashMap();
            vo = (RecordVo) dataList.get(i);
            return_LexprInfor.put("item",vo.getString("item"));
            return_LexprInfor.put("item_id", vo.getString("item_id")); 
            return_LexprInfor.put("description", vo.getString("description")); 
            String[] factorArr = (vo.getString("factor")).toString().split("\\|");
            if (StringUtils.equalsIgnoreCase(factorArr[factorArr.length-1], "False")||StringUtils.equalsIgnoreCase(factorArr[factorArr.length-1], "True")) {
                return_LexprInfor.put("type","D"); 
                if (factorArr.length>5) {
                    return_LexprInfor.put("lowerValue", factorArr[0]);
                    return_LexprInfor.put("lowerOperate", factorArr[1]);
                    return_LexprInfor.put("heightValue", factorArr[4]);
                    return_LexprInfor.put("heightOperate", factorArr[3]);
                    return_LexprInfor.put("middleValue", factorArr[2]);
                    return_LexprInfor.put("isAccuratelyDay", factorArr[5]);
                } else if (StringUtils.equals(factorArr[0], "无")) {
                    return_LexprInfor.put("lowerValue", "");
                    return_LexprInfor.put("lowerOperate", factorArr[0]);
                    return_LexprInfor.put("heightValue", factorArr[3]);
                    return_LexprInfor.put("heightOperate", factorArr[2]);
                    return_LexprInfor.put("middleValue", factorArr[1]);
                    return_LexprInfor.put("isAccuratelyDay", factorArr[4]);
                } else if (StringUtils.equals(factorArr[3], "无")) {
                    return_LexprInfor.put("lowerValue", factorArr[0]);
                    return_LexprInfor.put("lowerOperate", factorArr[1]);
                    return_LexprInfor.put("heightValue", "");
                    return_LexprInfor.put("heightOperate", factorArr[3]);
                    return_LexprInfor.put("middleValue", factorArr[2]);
                    return_LexprInfor.put("isAccuratelyDay", factorArr[4]);
                }
            } else {
                return_LexprInfor.put("type","N"); 
                if (factorArr.length==4) {
                    return_LexprInfor.put("lowerValue", factorArr[0]);
                    return_LexprInfor.put("lowerOperate", factorArr[1]);
                    return_LexprInfor.put("heightValue", factorArr[3]);
                    return_LexprInfor.put("heightOperate", factorArr[2]);
                    return_LexprInfor.put("middleValue", "");
                    return_LexprInfor.put("isAccuratelyDay", "false");
                } else if (StringUtils.equals(factorArr[0], "无")) {
                    return_LexprInfor.put("lowerValue", "");
                    return_LexprInfor.put("lowerOperate", factorArr[0]);
                    return_LexprInfor.put("heightValue", factorArr[2]);
                    return_LexprInfor.put("heightOperate", factorArr[1]);
                    return_LexprInfor.put("middleValue", "");
                    return_LexprInfor.put("isAccuratelyDay", "false");
                } else if (StringUtils.equals(factorArr[2], "无")) {
                    return_LexprInfor.put("heightValue", "");
                    return_LexprInfor.put("heightOperate", factorArr[2]);
                    return_LexprInfor.put("lowerValue", factorArr[0]);
                    return_LexprInfor.put("lowerOperate", factorArr[1]);
                    return_LexprInfor.put("middleValue", "");
                    return_LexprInfor.put("isAccuratelyDay", "false");
                }
            }
            return_data.add(return_LexprInfor);
        }
        return return_data;
    }

    /**
     * 删除二级指标表达式的内容
     * @Author sheny
     * @param item :要操作的二级指标id
     * @param item_id :要操作二级指标第几个表达式
     * @return String 成功：success 失败 给出提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/12/12 17:56
     */
    public String deleteItemLexpr(String item,String item_id) throws GeneralException {
        String return_code = "success";
        ContentDAO dao=new ContentDAO(this.conn);
        RecordVo vo = new RecordVo("gz_stand_date");
        try {
            vo.setObject("item", item);
            vo.setObject("item_id", item_id);
            dao.deleteValueObject(vo);
        } catch (Exception e) {
            // TODO: handle exception
            return_code = "fail";
            throw new GeneralException("deleteItemLexprError");
        }
        return return_code;
    }
    /**
     * 保存二级指标表达式的内容
     * @Author xuchangshun
     * @param itemInfor :二级指标表达式的信息
     * @return String 成功：success 失败 给出提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:44
     */
    @Override
    public String saveItemLexpr(Map itemInfor) throws GeneralException {
        String return_code = "success";
        Map lexprInfor = getlexprInfor(itemInfor); 
        //item 指标名称
        String item = (String) itemInfor.get("item");
        //item_id 指标序号
        String item_id = (String) itemInfor.get("item_id");
        
        if (StringUtils.isNotEmpty(item_id)) {
            return_code = this.updateItemLexpr(item, item_id,lexprInfor);
        } else {
            item_id = getItemId(item);
            IStandTableDao daoImpl = new StandTableDaoImpl(conn);
            return_code = daoImpl.saveItemLexpr(item,item_id,lexprInfor);
        }
        return return_code;
    }
    
    /**
     * 修改二级指标
     * @Author sheny
     * @param item :二级指标id
     * @param item_id :操作的表达式顺序id
     * @param lexprInfor :表达式的内容
     * @return 成功：success 失败 给出提示信息
     * @Date 2019/12/18 17:00
     */
    public String updateItemLexpr(String item,String item_id,Map lexprInfor) throws GeneralException  {
        ContentDAO dao=new ContentDAO(this.conn);
        String return_code = "success";
        RecordVo vo=new RecordVo("gz_stand_date");
        try {
            vo.setString("item", item);
            vo.setString("item_id", item_id);
            vo = dao.findByPrimaryKey(vo);
            
            vo.setString("description",(String) lexprInfor.get("description"));
            vo.setString("lexpr",(String) lexprInfor.get("lexpr"));
            vo.setString("factor",(String) lexprInfor.get("factor"));
            dao.updateValueObject(vo);
        } catch (Exception e) {
            // TODO: handle exception
            return return_code = "fail";
        } 
        return return_code;
    }
    /**
     * 获取二级指标表达式，表达式因子
     * @Author sheny
     * @param itemInfor :二级指标表达式的信息
     * @return Map lexprInfor
     * @Date 2019/12/11 11:00
     */
    public Map getlexprInfor(Map itemInfor) {
        Map lexprInfor  = new HashMap();
        //lexpr 表达式
        StringBuffer lexpr = new StringBuffer();
        //factor 表达式因子
        StringBuffer factor = new StringBuffer();
        //item 指标名称
        String item = (String) itemInfor.get("item");
        //description 指标表述
        String description = (String)itemInfor.get("description");
        //type 指标类型
        String type = (String)itemInfor.get("type");
        //heightValue 高端数据值
        String heightValue = (String) itemInfor.get("heightValue");
        //heightOperate 高端操作符值
        String heightOperate = (String) itemInfor.get("heightOperate");
        //lowerValue 低端数据值
        String lowerValue = (String) itemInfor.get("lowerValue");
        //lowerOperate 低端操作符值
        String lowerOperate = (String) itemInfor.get("lowerOperate");
        String operate = "";
        if("<".equals(lowerOperate)) {
            operate = ">";
        } else if ("<=".equals(lowerOperate)) {
            operate = ">=";
        }
        //middleValue 中端数据值,只有日期型指标传递此值
        String middleValue = (String)itemInfor.get("middleValue");
        //middleName 中端数据名称,只有日期型指标传递此值
        String middleName = getMiddleName(middleValue);
        //isAccuratelyDay 是否精确到天,只有日期型使用此值
        String isAccuratelyDay = (String)itemInfor.get("isAccuratelyDay");
        //首字母大写
        item = item.toUpperCase();
        isAccuratelyDay = isAccuratelyDay.substring(0, 1).toUpperCase() + isAccuratelyDay.substring(1);
        
        if("D".equalsIgnoreCase(type)) {
            if ("无".equals(lowerOperate)) {
                lexpr = lexpr.append(middleName).append("(").append(item).append(")").append(heightOperate).append(heightValue);
                factor = factor.append("无|").append(middleValue).append("|").append(heightOperate).append("|").append(heightValue).append("|").
                        append(isAccuratelyDay).append("|");
            } else if ("无".equals(heightOperate)) {
                lexpr = lexpr.append(middleName).append("(").append(item).append(")").append(operate).append(lowerValue);
                factor = factor.append(lowerValue).append("|").append(lowerOperate).append("|").append(middleValue).append("|").append("无|").
                        append(isAccuratelyDay).append("|");
            } else {
                lexpr = lexpr.append(middleName).append("(").append(item).append(")").append(operate).append(lowerValue).append(" and ")
                        .append(middleName).append("(").append(item).append(")").append(heightOperate).append(heightValue);
                factor = factor.append(lowerValue).append("|").append(lowerOperate).append("|").append(middleValue).append("|")
                        .append(heightOperate).append("|").append(heightValue).append("|").append(isAccuratelyDay).append("|");
            }
        } else if ("N".equalsIgnoreCase(type)) {
            if ("无".equals(lowerOperate)) {
                lexpr = lexpr.append(item).append(heightOperate).append(heightValue);
                factor = factor.append("无|").append(heightOperate).append("|").append(heightValue).append("|");
            } else if ("无".equals(heightOperate)) {
                lexpr = lexpr.append(item).append(operate).append(lowerValue);
                factor = factor.append(lowerValue).append("|").append(lowerOperate).append("|").append("无|");
            } else {
                lexpr = lexpr.append(item).append(operate).append(lowerValue).append(" and ").append(item).append(heightOperate).append(heightValue);
                factor = factor.append(lowerValue).append("|").append(lowerOperate).append("|").append(heightOperate).append("|").append(heightValue).append("|");
            }
        }
        
        lexprInfor.put("description", description);
        lexprInfor.put("lexpr", lexpr.toString());
        lexprInfor.put("factor", factor.toString());
        
        return lexprInfor;
    }
    
    /**
     * 获取二级指标中间名称
     * @Author sheny
     * @param middleValue :二级指标中间值
     * @return String middleName
     * @Date 2019/12/11 11:00
     */
    public String getMiddleName(String middleValue){
        String middleName="";
        if("0".equals(middleValue)) {
            middleName="年龄";
        } else if("1".equals(middleValue)) {
            middleName="工龄";
        } else if("2".equals(middleValue)) {
            middleName="年份";
        } else if("3".equals(middleValue)) {
            middleName="月份";
        }
        return middleName;
    }
    
    /**
     * 获取二级指标item_id
     * @Author sheny
     * @param item :二级指标指标名称
     * @return String item_id
     * @Date 2019/12/11 11:00
     */
    public String getItemId(String item)throws GeneralException{
        String item_id="";
        //item_id是字符型，直接取最大值不对，应该先转成int行
        String sql = "select MAX("+Sql_switcher.sqlToInt("item_id")+") from gz_stand_date where item ='"+item+"'";
        try{
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=dao.search(sql);
            
            if(rowSet.next()){
                if(rowSet.getString(1)!=null){
                    int itemid=Integer.parseInt(rowSet.getString(1));
                    item_id=String.valueOf(++itemid);
                }
                else {
                    item_id="1";
                }
            }
            else {
                item_id="1";
            }
            rowSet.close();
        }
        catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return item_id;
    }

    /**
     * 获取标准表编辑页面数据(仅创建、调整结构时调用)
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @param standInfor :标准表结构
     * @return Map 标准表编辑页面的数据对象
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:51
     */
    @Override
    public Map getStandData(String pkg_id, String stand_id, Map standInfor) throws GeneralException {
        Map standDataMap = new HashMap();
        //获取标准表列
        List columns = this.getEditStandColumns(standInfor);
        //获取结果型指标类型
        Map resultFieldData = this.getResultFieldData(standInfor);
        //获取标准表单元格数据
        Map standData = new HashMap();
        if (StringUtils.isNotEmpty(stand_id)) {
            standData = this.standTableDao.getStandTableItemData(stand_id, pkg_id, resultFieldData);
        }
        //获取封装后标准表数据
        List storeData = this.getEditStandStoreData(standInfor, standData);
        //获取标准表名称
        String stanardName = (String) standInfor.get("name");
        //获取合并列
        String mergeColumn = this.getMergeColumn(standInfor);
        //获取编辑权限
        boolean isHaveEdit = this.isHaveEditPriv();
        standDataMap.put("columns", columns);
        standDataMap.put("storeData", storeData);
        standDataMap.put("resultFieldData", resultFieldData);
        standDataMap.put("stanardName", stanardName);
        standDataMap.put("mergeColumn", mergeColumn);
        standDataMap.put("isHaveEdit", isHaveEdit);
        return standDataMap;
    }

    /**
     * 获取标准表编辑页面数据(编辑标准表数据时调用)
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @return Map 标准表编辑页面的数据对象
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:51
     */
    @Override
    public Map getStandData(String pkg_id, String stand_id) throws GeneralException {
        Map standDataMap = new HashMap();
        try {
            //获取标准表结构信息
            Map<String, String> standStructInfor = this.getStandStructInfor(pkg_id, stand_id);
            String returnMsgCode = this.verifFieldItem(standStructInfor);
            if (StringUtils.isEmpty(returnMsgCode)){
                standDataMap = this.getStandData(pkg_id,stand_id,standStructInfor);
            } else {
                Map<String, String> returnMagMap = new HashMap<>();
                returnMagMap.put("returnMsgCode",returnMsgCode);
                return returnMagMap;
            }
        } catch (Exception e) {
            String msg = "getStandDataError";
            if(e instanceof GeneralException){
                msg = ((GeneralException) e).getErrorDescription();
            }
            throw new GeneralException(msg);
        }

        return standDataMap;
    }

    /**
     * 获取调整结构所需要的标准表结构数据格式
     * @param standStructInfor
     * @return
     */
    @Override
    public Map getFormatStandStructInfor(Map standStructInfor){
        Map standStructInforFormat = new HashMap();
        String hfactorCodeSetId ="";
        String shfactorCodeSetId ="";
        String shfactorItemtype ="";
        String vfactorCodeSetId ="";
        String svfactorCodeSetId ="";
        String svfactorItemtype ="";
        String svfactorItemId = "";
        
        String s_hfactor = (String) standStructInfor.get("s_hfactor");
        String s_hfactor_temp = "";
        String shfactorItemId = "";
        if(StringUtils.isNotEmpty(s_hfactor)){
            FieldItem s_hfactorItem = DataDictionary.getFieldItem(s_hfactor);
            shfactorCodeSetId =s_hfactorItem.getCodesetid();
            shfactorItemtype = s_hfactorItem.getItemtype();
            shfactorItemId = s_hfactorItem.getItemid();
            s_hfactor_temp = s_hfactor+"`"+DataDictionary.getFieldItem(s_hfactor).getItemdesc();
        }
        String hfactor = (String) standStructInfor.get("hfactor");
        String hfactor_temp = "";
        if(StringUtils.isNotEmpty(hfactor)){
            hfactorCodeSetId = DataDictionary.getFieldItem(hfactor).getCodesetid();
            hfactor_temp = hfactor+"`"+DataDictionary.getFieldItem(hfactor).getItemdesc();
        }
        String vfactor = (String) standStructInfor.get("vfactor");
        String vfactor_temp = "";
        if(StringUtils.isNotEmpty(vfactor)){
            FieldItem fieldItem = DataDictionary.getFieldItem(vfactor);
            vfactorCodeSetId = fieldItem.getCodesetid();
            vfactor_temp = vfactor+"`"+DataDictionary.getFieldItem(vfactor).getItemdesc();
        }
        String s_vfactor = (String) standStructInfor.get("s_vfactor");
        String s_vfactor_temp = "";
        if(StringUtils.isNotEmpty(s_vfactor)){
            FieldItem fieldItem = DataDictionary.getFieldItem(s_vfactor);
            svfactorCodeSetId = fieldItem.getCodesetid();
            svfactorItemId = fieldItem.getItemid();
            svfactorItemtype = fieldItem.getItemtype();
            s_vfactor_temp = s_vfactor+"`"+DataDictionary.getFieldItem(s_vfactor).getItemdesc();
        }
        standStructInforFormat.put("hfactor",hfactor_temp);
        standStructInforFormat.put("s_hfactor",s_hfactor_temp);
        standStructInforFormat.put("vfactor",vfactor_temp);
        standStructInforFormat.put("s_vfactor",s_vfactor_temp);
        String hcontent = (String) standStructInfor.get("hcontent");
        String vcontent = (String) standStructInfor.get("vcontent");
        Map hfactorMap = this.getFactorStruct(hfactor,s_hfactor,hcontent);
        List selecthFactorList = (List) hfactorMap.get("selectFactorList");
        List selectshFactorList = (List) hfactorMap.get("selectsFactorList");
        Map hrelation = (Map) hfactorMap.get("relation");

        Map vfactorMap = this.getFactorStruct(vfactor,s_vfactor,vcontent);
        List selectvFactorList = (List) vfactorMap.get("selectFactorList");
        List selectsvFactorList = (List) vfactorMap.get("selectsFactorList");
        Map vrelation = (Map) vfactorMap.get("relation");
        String name = (String) standStructInfor.get("name");
        String item = (String) standStructInfor.get("item");
        item = item+"`"+DataDictionary.getFieldItem(item).getItemdesc();
        standStructInforFormat.put("name",name);
        standStructInforFormat.put("item",item);
        standStructInforFormat.put("hrelation",hrelation);
        standStructInforFormat.put("vrelation",vrelation);
        standStructInforFormat.put("hfactorCodeSetId",hfactorCodeSetId);
        standStructInforFormat.put("vfactorCodeSetId",vfactorCodeSetId);
        standStructInforFormat.put("selecthFactorList",selecthFactorList);
        standStructInforFormat.put("selectvFactorList",selectvFactorList);
        standStructInforFormat.put("selectshFactorList",selectshFactorList);
        standStructInforFormat.put("selectsvFactorList",selectsvFactorList);
        standStructInforFormat.put("shfactorCodeSetId",shfactorCodeSetId);
        standStructInforFormat.put("shfactorItemtype",shfactorItemtype);
        standStructInforFormat.put("shfactorItemId",shfactorItemId);
        standStructInforFormat.put("svfactorCodeSetId",svfactorCodeSetId);
        standStructInforFormat.put("svfactorItemtype",svfactorItemtype);
        standStructInforFormat.put("svfactorItemId",svfactorItemId);
        return standStructInforFormat;
    }
    private Map getFactorStruct(String factor,String sfactor,String content){
        Map structMap = new HashMap();
        Map relation = new HashMap();
        List selectFactorList = new ArrayList();
        List selectsFactorList = new ArrayList();
        if(StringUtils.isNotEmpty(factor)){
            String[] factorStr = content.split(";");
            for (int i = 0; i < factorStr.length; i++) {
                List childList = new ArrayList();
                String contentTemp = factorStr[i];
                String factorItemid = contentTemp.substring(0, contentTemp.indexOf("["));
                selectFactorList.add(factorItemid);
                String sfactorvalue = contentTemp.substring(contentTemp.indexOf("[") + 1, contentTemp.indexOf("]"));
                String[] sfactorvalues = sfactorvalue.split(",");
                for(int j = 0;j<sfactorvalues.length;j++){
                    if(StringUtils.isEmpty(sfactorvalues[j])){
                        continue;
                    }
                    childList.add(sfactorvalues[j]);
                }
                relation.put(factor+"_"+factorItemid,childList);
            }
        }else if(StringUtils.isNotEmpty(sfactor)){
            content = content.substring(1,content.indexOf("]"));
            String[] values = content.split(",");
            for(int i = 0;i<values.length;i++){
                selectsFactorList.add(values[i]);
            }
        }
        structMap.put("selectFactorList",selectFactorList);
        structMap.put("selectsFactorList",selectsFactorList);
        structMap.put("relation",relation);
        return structMap;
    }
    /**
     * 保存标准表编辑页面数据(仅创建、调整结构时调用)
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @param standInfor :标准表结构
     * @param stanardDataList :标准表单元格数据
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:56
     */
    @Override
    public String saveStandData(String pkg_id, String stand_id, Map standInfor, List stanardDataList,String saveType) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            //新建
            if (StringUtils.isEmpty(stand_id)) {
                stand_id = String.valueOf(this.getGzStandId());
                saveType = "create";
            }
            RecordVo standVo = this.initRecordVo(new RecordVo("gz_stand"),standInfor,stand_id,pkg_id);
            RecordVo standHistoryVo = this.initRecordVo(new RecordVo("gz_stand_history"),standInfor,stand_id,pkg_id);
            if("create".equalsIgnoreCase(saveType))
            {
                String value="";
                boolean noManage = false;
                if(this.userView.isSuper_admin()){
                    value="UN";
                } else{
                    String unit_id = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
                    if(unit_id!=null&&unit_id.trim().length()>1)
                    {
                        if("UN".equalsIgnoreCase(unit_id))
                        {
                            value="UN";
                        }
                        else
                        {
                            String[] arr = unit_id.split("`");
                            value = arr[0].substring(2);
                        }
                    }
                    else
                    {
                        if(StringUtils.isEmpty(this.userView.getManagePrivCode())){
                            noManage=true;
                        }else{
                            String codevalue = StringUtils.isEmpty(this.userView.getManagePrivCodeValue())?"UN":this.userView.getManagePrivCodeValue();
                            value = codevalue;
                        }

                    }
                }
                //如果没有管理范围和操作单位，这个两个字段不赋值
                if(!noManage)
                {
                    if(standHistoryVo.hasAttribute("b0110"))
                    {
                        standHistoryVo.setString("b0110", "UN".equalsIgnoreCase(value)?null:(","+value));
                    }
                    if(standHistoryVo.hasAttribute("createorg"))
                    {
                        standHistoryVo.setString("createorg", value);
                    }
                }
            }
            //当前历史沿革是否为启用
            boolean isActive = isPackageActive(pkg_id);
            standVo.setString("unit_type", "");
            if (isActive) {
                standVo.setInt("flag", 1);
            } else {
                standVo.setInt("flag", 0);
            }
            if (StringUtils.equalsIgnoreCase(saveType,"create")) {
                dao.addValueObject(standVo);
                dao.addValueObject(standHistoryVo);
            } else if (StringUtils.equalsIgnoreCase(saveType,"struct")) {
                dao.updateValueObject(standVo);
                dao.updateValueObject(standHistoryVo);
            }
            this.saveStandData(pkg_id,stand_id,stanardDataList);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = "saveStandDataError";
            if(e instanceof GeneralException){
                msg = ((GeneralException) e).getErrorDescription();
            }
            throw  new GeneralException(msg);
        }
        return stand_id;


    }
    private RecordVo initRecordVo(RecordVo vo,Map standInfor,String stand_id,String pkg_id){
        String hfactor = (String) standInfor.get("hfactor");
        if (StringUtils.isNotEmpty(hfactor)) {
            hfactor = hfactor.toUpperCase();
        }
        String s_hfactor = (String) standInfor.get("s_hfactor");
        if (StringUtils.isNotEmpty(s_hfactor)) {
            s_hfactor = s_hfactor.toUpperCase();
        }
        String vfactor = (String) standInfor.get("vfactor");
        if (StringUtils.isNotEmpty(vfactor)) {
            vfactor = vfactor.toUpperCase();
        }
        String s_vfactor = (String) standInfor.get("s_vfactor");
        if (StringUtils.isNotEmpty(s_vfactor)) {
            s_vfactor = s_vfactor.toUpperCase(); 
        }
        String item = (String) standInfor.get("item");
        if (StringUtils.isNotEmpty(item)) {
            item = item.toUpperCase(); 
        }
        String hcontent = (String) standInfor.get("hcontent");
        String vcontent = (String) standInfor.get("vcontent");
        String name = (String) standInfor.get("name");
        vo.setObject("id", stand_id);
        if (vo.hasAttribute("pkg_id")) {
            vo.setObject("pkg_id", pkg_id);
            vo.setDate("createtime", new Date());
        }
        vo.setString("name", name);
        vo.setString("hfactor", hfactor);
        vo.setString("hcontent", hcontent);
        vo.setString("vfactor", vfactor);
        vo.setString("vcontent", vcontent);
        vo.setString("s_hfactor", s_hfactor);
        vo.setString("s_vfactor", s_vfactor);
        vo.setString("item", item);
        return vo;
    }
    private synchronized int getGzStandId() {
        int id=0;
        try
        {
            String strsql = "select max(id) from gz_stand";
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=dao.search(strsql);
            if(rowSet.next()) {
                id=rowSet.getInt(1);
            }
            id++;
            rowSet.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return id;
    }



    /**
     * 保存标准表编辑页面数据(仅编辑标准表时调用)
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @param stanardDataList :标准表单元格数据
     * @return String 成功：success 失败 给出提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:56
     */
    @Override
    public void saveStandData(String pkg_id, String stand_id,List stanardDataList) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        //当前历史沿革是否为启用
        boolean isActive = isPackageActive(pkg_id);
        List recordList = new ArrayList();
        try {
            if (isActive) {
                String gzItemDelSql = "delete from gz_item where id = ?";
                dao.delete(gzItemDelSql, Arrays.asList(stand_id));
            }
            String gzItemHistoryDelSql = "delete from gz_item_history where id = ? and pkg_id = ?";
            dao.delete(gzItemHistoryDelSql, Arrays.asList(stand_id,pkg_id));
            //获取标准表结构信息
            Map<String, String> standStructInfor = this.getStandStructInfor(pkg_id, stand_id);
            String resultFieldType = (String) this.getResultFieldData(standStructInfor).get("resultFieldType");
            for(int i = 0;i<stanardDataList.size();i++){
                MorphDynaBean bean = (MorphDynaBean) stanardDataList.get(i);
                Map<String,String> record = PubFunc.DynaBean2Map(bean);
                String value = record.get("value");
                //处理代码型
                if(StringUtils.equalsIgnoreCase(resultFieldType,"code")){
                    value = value.split("`")[0];
                }
                List tempList = new ArrayList();
                String hvalue = record.get("hvalue");
                if (StringUtils.isNotEmpty(hvalue)) {
                    hvalue = hvalue.toUpperCase();
                }
                String shvalue = record.get("shvalue");
                if (StringUtils.isNotEmpty(shvalue)) {
                    shvalue = shvalue.toUpperCase();
                }
                String vvalue = record.get("vvalue");
                if (StringUtils.isNotEmpty(vvalue)) {
                    vvalue = vvalue.toUpperCase();
                }
                String svvalue = record.get("svvalue");
                if (StringUtils.isNotEmpty(svvalue)) {
                    svvalue = svvalue.toUpperCase(); 
                }
                tempList.add(hvalue);
                tempList.add(shvalue);
                tempList.add(vvalue);
                tempList.add(svvalue);
                tempList.add(value);
                recordList.add(tempList);
            }
            int num = recordList.size()/1000;
            if(recordList.size()%1000!=0){
                num++;
            }
            String sql="insert into gz_item (id,hvalue,s_hvalue,vvalue,s_vvalue,standard) values ("+stand_id+",?,?,?,?,?)";
            String _sql="insert into gz_item_history (id,pkg_id,hvalue,s_hvalue,vvalue,s_vvalue,standard) values ("+stand_id+","+pkg_id+",?,?,?,?,?)";
            ArrayList templist = null;
            for(int n=0;n<num;n++){
                templist = new ArrayList();
                for(int x=n*1000;x<(n+1)*1000;x++){
                    if(x>=recordList.size()){
                        break;
                    }
                    templist.add(recordList.get(x));
                }
                if(isActive){
                    dao.batchInsert(sql, templist);
                }
                dao.batchInsert(_sql, templist);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new GeneralException("saveStandDataError");
        }
    }

    /**
     * 判断当前标准表pkg是否启用
     * @param pkg_id
     * @return
     */
    private boolean isPackageActive(String pkg_id)
    {
        boolean isActive=false;
        try {
            RecordVo gz_stand_pkg_vo = new RecordVo("gz_stand_pkg");
            gz_stand_pkg_vo.setObject("pkg_id", pkg_id);
            ContentDAO dao = new ContentDAO(this.conn);
            if (dao.isExistRecordVo(gz_stand_pkg_vo)) {
                gz_stand_pkg_vo = dao.findByPrimaryKey(gz_stand_pkg_vo);
                String status = gz_stand_pkg_vo.getString("status");
                if(StringUtils.equalsIgnoreCase(status,"1")){
                    isActive = true;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return isActive;
    }


    /**
     * 导出标准表数据
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param standIds :选中的标准表id,多个使用逗号分割
     * @return Map 成功包含导出文件的路径，失败 给出提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:11
     */
    @Override
    public Map exportStandData(String pkg_id, String standIds) throws GeneralException {
        FileOutputStream fileOut = null;
        RowSet rowSet = null;
        HashMap map = new HashMap();
        HSSFWorkbook wb = null;
        String file_Name = this.userView.getUserName()+"_标准表"+".xls";
        try {
            wb = new HSSFWorkbook();
            ContentDAO dao=new ContentDAO(this.conn);
            String ids="";
            String[] standIdsList = standIds.split(",");
            //解密
            for(int i = 0;i<standIdsList.length;i++){
                standIdsList[i]=PubFunc.decrypt(SafeCode.decode(standIdsList[i]));
            }
            //对standIds进行从小到大的排序
            for(int i =0;i<standIdsList.length-1;i++){
                for(int j =0;j<standIdsList.length-i-1;j++){
                    if(Integer.parseInt(standIdsList[j])>Integer.parseInt(standIdsList[j+1])){
                        String temp = standIdsList[j];
                        standIdsList[j] = standIdsList[j+1];
                        standIdsList[j+1] = temp;
                    }
                }
            }
            //拼接standIds
            for(int i = 0;i<standIdsList.length;i++){
                ids+=","+standIdsList[i];
            }
            int j = 0;
            String searchStandHisSql = "select * from gz_stand_history where id in("+ids.substring(1)+") and pkg_id="+pkg_id;
//            String searchWhereSql = " order by charindex(','+convert(varchar,ID)+',',',"+ids.substring(1)+",')";//按照选中顺序排序查询，只针对MSSQL
            rowSet = dao.search(searchStandHisSql);
            HashMap standardItemMap = new HashMap();
            while(rowSet.next()){
                String id = rowSet.getString("id");
                standardItemMap.put("id",id);
                standardItemMap.put("hfactor",rowSet.getString("hfactor"));
                standardItemMap.put("s_hfactor",rowSet.getString("s_hfactor"));
                standardItemMap.put("vfactor",rowSet.getString("vfactor"));
                standardItemMap.put("s_vfactor",rowSet.getString("s_vfactor"));
                standardItemMap.put("item",rowSet.getString("item"));
                standardItemMap.put("hcontent",rowSet.getString("hcontent"));
                standardItemMap.put("vcontent",rowSet.getString("vcontent"));
                String sheetName = rowSet.getString("name");
                standardItemMap.put("name",sheetName);
                StandardItemVoUtil vo = null;
                vo = getStandardItemVo(standardItemMap,pkg_id,standIdsList[j]);
                //单张导出
                if(standIdsList.length<2){
                    file_Name = this.userView.getUserName()+"_"+sheetName+".xls";
                    StandardUtil.executeSingleStandardSheet(vo,sheetName,1,true,wb);
                }else{//多张导出
                    StandardUtil.executeSingleStandardSheet(vo,sheetName+"_"+id,j+1,false,wb);
                }
                j++;
            }
            map.put("file_Name",PubFunc.encrypt(file_Name));
            String file_path = System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+file_Name;
            fileOut = new FileOutputStream(file_path);
            wb.write(fileOut);
        }catch (Exception e){
            e.printStackTrace();
            throw new GeneralException(e.toString());
        }finally {
            PubFunc.closeDbObj(rowSet);
            PubFunc.closeIoResource(fileOut);
            PubFunc.closeIoResource(wb);
        }
        return map;
    }


    /**
     * 获取标准表的所有数据vo
     * @Author houby
     * @param standardItemMap :所查表的相关信息
     * @param pkg_id :历史沿革id
     * @param standIds :选中的标准表id,多个使用逗号分割
     * @return StandardItemVoUtil 标准表数据源
     * @Date 2019/12/05 13:34
     */
    private StandardItemVoUtil getStandardItemVo(HashMap standardItemMap, String pkg_id, String standIds) {
        HashMap itemNameMap=new HashMap();
        HashMap itemValueMap=getItemValueMap(pkg_id,standIds);
        String hfactor = (String) standardItemMap.get("hfactor");
        String s_hfactor = (String) standardItemMap.get("s_hfactor");
        String vfactor = (String) standardItemMap.get("vfactor");
        String s_vfactor = (String) standardItemMap.get("s_vfactor");
        String item = (String) standardItemMap.get("item");
        String hcontent = (String) standardItemMap.get("hcontent");
        String vcontent = (String) standardItemMap.get("vcontent");
        String name = (String) standardItemMap.get("name");
        if(hfactor!=null&&hfactor.trim().length()>0) {
            itemNameMap.put(hfactor.toLowerCase(), getItemMap(hfactor));
        }
        if(s_hfactor!=null&&s_hfactor.trim().length()>0) {
            itemNameMap.put(s_hfactor.toLowerCase(), getItemMap(s_hfactor));
        }
        if(vfactor!=null&&vfactor.trim().length()>0) {
            itemNameMap.put(vfactor.toLowerCase(), getItemMap(vfactor));
        }
        if(s_vfactor!=null&&s_vfactor.trim().length()>0) {
            itemNameMap.put(s_vfactor.toLowerCase(), getItemMap(s_vfactor));
        }
        StandardItemVoUtil vo = new StandardItemVoUtil();
        vo.setH_List(StandardUtil.get_List(hfactor,s_hfactor,hcontent,itemNameMap));
        vo.setV_List(StandardUtil.get_List(vfactor,s_vfactor,vcontent,itemNameMap));
        vo.setH_bottomColumn_num(StandardUtil.get_bottomColumn_num(vo.getH_List()));
        vo.setV_bottomColumn_num(StandardUtil.get_bottomColumn_num(vo.getV_List()));
        FieldItem fieldItem= DataDictionary.getFieldItem(item);
        vo.setResultItem(fieldItem);
        if(fieldItem!=null){
            if("N".equalsIgnoreCase(fieldItem.getItemtype())) {
                vo.setResultItemType("N");
            } else {
                vo.setResultItemType("C");
                vo.setCodesetid(fieldItem.getCodesetid());
            }
        }else{
            vo.setResultItemType("N");
        }
        vo.setGzItemList(StandardUtil.gzItemList(vo,itemValueMap));
        vo.setHfactor(hfactor);
        vo.setS_hfactor(s_hfactor);
        vo.setVfactor(vfactor);
        vo.setS_vfactor(s_vfactor);
        vo.setItem(item);
        vo.setHcontent(hcontent);
        vo.setVcontent(vcontent);
        return vo;
    }

    /**
     * 获取指标集
     * @Author houby
     * @param itemid:指标对应字段
     * @return Map 标准表指标项
     * @Date 2019/12/05 13:34
     */
    private HashMap getItemMap(String itemid)
    {
        RowSet rowSet = null;
        HashMap map = new HashMap();
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
            if (fieldItem != null) {
                String itemType = fieldItem.getItemtype();
                String codesetid = fieldItem.getCodesetid();
                if (StringUtils.equalsIgnoreCase(itemType,"A") && !StringUtils.equalsIgnoreCase(codesetid,"0")) {
                    List<CodeItem> codeItemList = AdminCode.getCodeItemList(codesetid);
                    for (int i = 0; i < codeItemList.size();i++) {
                        CodeItem codeItem = codeItemList.get(i);
                        if (codeItem != null) {
                            map.put(codeItem.getCodeitem(), codeItem.getCodename());
                        }
                    }
                } else if (StringUtils.equalsIgnoreCase(itemType,"N") || StringUtils.equalsIgnoreCase(itemType,"D")) {
                    rowSet = dao.search("select * from gz_stand_date where item='" + itemid + "'");
                    while (rowSet.next()) {
                        map.put(rowSet.getString("item_id"), rowSet.getString("description"));
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return map;
    }

    /**
     * 获取标准表数据
     * @Author houby
     * @param
     * @return Map 标准表数据的value
     * @Date 2019/12/05 13:34
     */
    public HashMap getItemValueMap(String pkg_id, String standIds) {
        HashMap map=new HashMap();
        try {
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=dao.search("select * from gz_item_history where id='"+standIds+"' and pkg_id='"+pkg_id+"'");
            while(rowSet.next()) {

                String hvalue="#";
                if(rowSet.getString("hvalue")!=null&&rowSet.getString("hvalue").trim().length()>0) {
                    hvalue=rowSet.getString("hvalue");
                }
                String s_hvalue="#";
                if(rowSet.getString("s_hvalue")!=null&&rowSet.getString("s_hvalue").trim().length()>0) {
                    s_hvalue=rowSet.getString("s_hvalue");
                }
                String vvalue="#";
                if(rowSet.getString("vvalue")!=null&&rowSet.getString("vvalue").trim().length()>0) {
                    vvalue=rowSet.getString("vvalue");
                }
                String s_vvalue="#";
                if(rowSet.getString("s_vvalue")!=null&&rowSet.getString("s_vvalue").trim().length()>0) {
                    s_vvalue=rowSet.getString("s_vvalue");
                }
                String standard="";
                if(rowSet.getString("standard")!=null&&rowSet.getString("standard").trim().length()>0) {
                    standard= StandardUtil.subZeroAndDot(rowSet.getString("standard"));
                }
                map.put((hvalue+"|"+s_hvalue+"|"+vvalue+"|"+s_vvalue).toLowerCase(),standard);
            }
            rowSet.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 导入标准表数据
     * @Author xuchangshun
     * @param fileId :文件加密id
     * @return Map 成功 给出提示信息 失败 生成失败文件路径 供操作人员分析
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:14
     */
    @Override
    public Map importStandData(String fileId, String pkg_id, String stand_id) throws GeneralException {
        RowSet rs = null;
        LazyDynaBean bean=new LazyDynaBean();
        HashMap map = new HashMap();
        String return_msg = "";
        String return_code = "success";
        String fileName = this.userView.getUserName() + "_标准表导入错误详情.xls";
        String file_path = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
        File logFile = new File(file_path);
        if(logFile.exists()) {
            logFile.delete();
        }
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            String strSql="select id,pkg_id,createorg,name,"+ Sql_switcher.isnull("hfactor", "' '")+" as hfactor,"+Sql_switcher.isnull("s_hfactor", "' '")+" as s_hfactor,"
                    + "b0110,hcontent,"+Sql_switcher.isnull("s_vfactor", "' '")+" as s_vfactor,"+Sql_switcher.isnull("vfactor", "' '")+" as vfactor,vcontent,item,createtime "
                    + "from gz_stand_history where id=? and pkg_id=?";
            ArrayList<String> list=new ArrayList<String>();
            list.add(stand_id);
            list.add(pkg_id);
            rs = dao.search(strSql, list);
            if (rs.next()) {
                bean.set("hfactor", rs.getString("hfactor"));//横向指标
                bean.set("s_hfactor", rs.getString("s_hfactor"));//S横向指标
                bean.set("vfactor", rs.getString("vfactor"));//纵向指标
                bean.set("s_vfactor", rs.getString("s_vfactor"));//S纵向指标
                bean.set("item", rs.getString("item"));//结果指标
                bean.set("name", rs.getString("name"));//标准名称
                bean.set("hcontent", rs.getString("hcontent"));//横向指标项列表
            }
            Map<String, Object> resultMap = StandardUtil.getInputDataList(fileId, fileName, bean);//拼接数据
            String isError = (String) resultMap.get("isError");//返回错误信息
            isError = StringUtils.isBlank(isError) ? "" : isError;
            if (!StringUtils.isBlank(isError) && !"1".equalsIgnoreCase(isError)) {//文件本身有错误
                return_msg = "数据导入失败," + isError;
                return_code = "fail";
            } else if ("1".equalsIgnoreCase(isError)) {//标准表数据有错误
                return_msg = "数据导入失败";
                return_code = "fail";
                map.put("errorLog_path", PubFunc.encrypt(fileName));
            } else {//文件导入成功
                ArrayList dataList = (ArrayList) resultMap.get("dataList");
                reUpdateStandard(bean, pkg_id, stand_id, dataList);
            }
        }catch(Exception e){
            return_code = "fail";
            return_msg = "数据导入失败";
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
        map.put("return_msg",return_msg);
        map.put("return_code",return_code);
        return map;
    }

    /**
     * 导入Excel后重新更新数据
     * @param bean 表结构bean
     * @param pkg_id 历史沿革号
     * @param stand_id 标准表id
     * @param dataList 数据集
     * @return
     * @throws GeneralException
     */
    public boolean reUpdateStandard(LazyDynaBean bean,String pkg_id,String stand_id,ArrayList<HashMap> dataList) throws GeneralException{
        StringBuilder strSql=new StringBuilder();

        try{
            RowSet rs=null;
            ContentDAO dao = new ContentDAO(this.conn);

            String status="";
            rs=dao.search("select status from gz_stand_pkg where pkg_id="+pkg_id);
            if(rs.next()){
                status=rs.getString("status");
            }else{
                return false;
            }

            ArrayList<String> headItem=new ArrayList<String>();

            if(!StringUtils.isBlank((String)bean.get("hfactor"))){
                strSql.append(" and "+Sql_switcher.isnull("hvalue", "' '")+"="+Sql_switcher.isnull("?", "' '"));
                headItem.add("Hfactor");
            }
            if(!StringUtils.isBlank((String)bean.get("s_hfactor"))){
                strSql.append(" and "+Sql_switcher.isnull("S_hvalue", "' '")+"="+Sql_switcher.isnull("?", "' '"));
                headItem.add("s_Hfactor");
            }
            if(!StringUtils.isBlank((String)bean.get("vfactor"))){
                strSql.append(" and "+Sql_switcher.isnull("Vvalue", "' '")+"="+Sql_switcher.isnull("?", "' '"));
                headItem.add("Vfactor");
            }
            if(!StringUtils.isBlank((String)bean.get("s_vfactor"))){
                strSql.append(" and "+Sql_switcher.isnull("S_vvalue", "' '")+"="+Sql_switcher.isnull("?", "' '"));
                headItem.add("s_Vfactor");
            }

            ArrayList<ArrayList> updateDataList=new ArrayList<ArrayList>();
            ArrayList list=new ArrayList();
            for(HashMap dataMap:dataList){
                list=new ArrayList();
                String value=dataMap.get("value")==null?"":dataMap.get("value").toString();

                list.add(value);
                list.add(pkg_id);
                list.add(stand_id);
                for(String str:headItem){
                    list.add(dataMap.get(str));
                }
                updateDataList.add(list);
                dao.update("update gz_item_history set standard=? where pkg_id=? and  ID =? " + strSql.toString(), list);
            }
//            dao.batchUpdate("update gz_item_history set standard=? where pkg_id=? and  ID =? "+strSql.toString(), updateDataList);

            if("1".equalsIgnoreCase(status)){//若当前沿革为启用
                for(ArrayList temp:updateDataList){
                    temp.remove(1);
                    dao.update("update gz_item set standard=? where ID =? "+strSql.toString(), temp);
                }
//                dao.batchUpdate("update gz_item set standard=? where ID =? "+strSql.toString(), updateDataList);
            }
            PubFunc.closeDbObj(rs);
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 删除标准表
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @return String 返回成功或者失败的提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:21
     */
    @Override
    public String deleteStand(String pkg_id, String stand_id) throws GeneralException {
        StandTableDaoImpl standTableDaoImpl = new StandTableDaoImpl(conn);
        String deleteReturnCode = standTableDaoImpl.deleteStandTableList(pkg_id, stand_id);
        return deleteReturnCode;
    }
    /**
     * 获取列头
     * @Author linjiasi
     * @param columnId 列id
     * @param columnDesc 列描述
     * @param columnWidth 列宽
     * @param columnType 列类型
     * @return 列主要属性
     * @throws GeneralException 异常信息
     * @Date 2019/12/03 16:39
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String columnType) {
        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setColumnWidth(columnWidth);
        columnsInfo.setColumnType(columnType);
        if ("A".equalsIgnoreCase(columnType)) {
            columnsInfo.setTextAlign("left");//对齐方式为左对齐
            if ("b0110".equalsIgnoreCase(columnId) | "name".equalsIgnoreCase(columnId)) {
            } else {
                columnsInfo.setCodesetId("0");//字符型
                columnsInfo.setEditableValidFunc("false");//不可编辑
            }
        } else if ("N".equalsIgnoreCase(columnType)) {
            columnsInfo.setTextAlign("right");//对齐方式为右对齐
            columnsInfo.setEditableValidFunc("false");//不可编辑
        }
        return columnsInfo;
    }
    /**
     * 取得工资标准列表信息
     * @Author linjiasi
     * @param pkg_id
     * @return 工资标准表信息
     * @throws GeneralException 异常信息
     * @Date 2019/12/04 17:45
     */
    @Override
    public String getSalaryStandardList(String pkg_id) throws GeneralException {
        StringBuffer getSalaryStandardListSql = new StringBuffer("");
        getSalaryStandardListSql.append("select ").append(Sql_switcher.isnull("b0110", "''")).append(" as b0110 ,id,id id_e,pkg_id,name,createorg,");
        getSalaryStandardListSql.append(getItemDesc("hfactor")).append(",");
        getSalaryStandardListSql.append(getItemDesc("s_hfactor")).append(",");
        getSalaryStandardListSql.append(getItemDesc("vfactor")).append(",");
        getSalaryStandardListSql.append(getItemDesc("s_vfactor")).append(",");
        getSalaryStandardListSql.append(getItemDesc("item"));
        getSalaryStandardListSql.append(" from gz_stand_history where pkg_id=");
        getSalaryStandardListSql.append(pkg_id);
        getSalaryStandardListSql.append(" ");
        getSalaryStandardListSql.append(this.getPrivSql());
        return getSalaryStandardListSql.toString();
    }
    /**
     * 获取标准表列表权限sql
     * @return
     */
    private  String getPrivSql() {
        StringBuffer sqlBuffer = new StringBuffer();
        StringBuffer partSql = new StringBuffer();
        String unitid = "XXXX";
        if(this.userView.isSuper_admin())
        {
            unitid="UN";
            partSql.append(" or 1=1 ");
        }
        else
        {
            if(this.userView.getManagePrivCode()!=null&&this.userView.getManagePrivCode().trim().length()>0) {
                if (this.userView.getManagePrivCodeValue() == null || "".equals(this.userView.getManagePrivCodeValue().trim())) {
                    unitid = "UN";
                    partSql.append(" or 1=1 ");
                } else {
                    unitid = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
                    partSql.append(" or b0110 like '%," + this.userView.getManagePrivCodeValue() + "%'");
                }
            }
            else{
                if(this.userView.getUnit_id().length()==3)
                {
                    unitid="UN";
                    partSql.append(" or 1=1 ");
                }
                else
                {
                    unitid=this.userView.getUnit_id();
                    String[] unit_arr = unitid.split("`");
                    for(int i=0;i<unit_arr.length;i++)
                    {
                        if(unit_arr[i]==null|| "".equals(unit_arr[i])||unit_arr[i].length()<2) {
                            continue;
                        }
                        partSql.append(" or b0110 like '%,"+unit_arr[i].substring(2)+"%' ");
                    }
                }
            }
        }
        if(partSql.toString().length()>0)
        {
            if(this.userView.isSuper_admin()|| "UN".equals(unitid))
            {

            }else
            {
                sqlBuffer.append(" and (");
                sqlBuffer.append("("+partSql.toString().substring(3)+")");
                sqlBuffer.append(" or "+Sql_switcher.isnull("b0110", "'E'")+"='E'");
                sqlBuffer.append(")");
            }
        }
        if("XXXX".equals(unitid))
        {
            sqlBuffer.append(" and "+Sql_switcher.isnull("b0110", "'E'")+"='E'");
        }
        return sqlBuffer.toString();
    }

    /**
     * 循环生成likesql
     *
     * @param column 用哪个字段进行like
     * @param values 要被liske的值
     * @return 拼装成的sql
     */
    private  String loopConstrutLikeSql(String tableName, String column, List<String> values) {
        StringBuffer loopStr = new StringBuffer();
        for (int i = 0; i < values.size(); i++) {
            String unit = values.get(i);
            if (StringUtils.isNotEmpty(tableName)) {
                loopStr.append(tableName.toLowerCase()).append(".");
            }
            loopStr.append(column).append("  like '%,").append(unit).append("%,'");
            if (i < values.size() - 1) {
                loopStr.append(" or ");
            }
        }
        return loopStr.toString();
    }
    /**
     * 传入一个指标代号，返回指标名称
     * @Author linjiasi
     * @return 工资标准表信息
     * @throws GeneralException 异常信息
     * @Date 2019/12/05 11:39
     */
    private String getItemDesc(String itemid)throws GeneralException{
        StringBuffer getItemDescSql = new StringBuffer();
        getItemDescSql.append("(select itemdesc from fielditem where itemid =");
        getItemDescSql.append(itemid).append(") ").append(itemid);
        return getItemDescSql.toString();
    }

    /**
     * 获取标准表编辑页面列
     * @param standStructInfor 标准表结构数据
     * @return
     */
    private List getEditStandColumns(Map<String,String> standStructInfor) {
        List columnsInfoList = new ArrayList();
        try {
            String hfactor = standStructInfor.get("hfactor");
            String s_hfactor = standStructInfor.get("s_hfactor");
            String hcontent = standStructInfor.get("hcontent");
            String vfactor = standStructInfor.get("vfactor");
            String s_vfactor = standStructInfor.get("s_vfactor");
            String item = standStructInfor.get("item");
            boolean isHaveChild = false;
            //横向栏目配置的情况下
            hcontent = PubFunc.keyWord_reback(hcontent);
            Map codeColumn = new HashMap();
            codeColumn.put("text", "");
            codeColumn.put("sortable", false);
            codeColumn.put("menuDisabled", true);
            codeColumn.put("dataIndex", "vfactor_desc");
            codeColumn.put("resizable", false);
            codeColumn.put("hoverCls", "");
            codeColumn.put("focusCls", "");
            codeColumn.put("locked", true);
            columnsInfoList.add(codeColumn);
            if (StringUtils.isNotEmpty(hfactor)) {
                String[] columnStr = hcontent.split(";");
                for (int i = 0; i < columnStr.length; i++) {
                    String columnStrTemp = columnStr[i];
                    Map columns = new HashMap();
                    //一级列 代码itemid
                    String columnId = columnStrTemp.substring(0, columnStrTemp.indexOf("["));
                    String s_hfactor_value = columnStrTemp.substring(columnStrTemp.indexOf("[") + 1, columnStrTemp.indexOf("]"));
                    String[] s_hfactor_values = s_hfactor_value.split(",");
                    List childColumnsList = new ArrayList();
                    if (StringUtils.isNotBlank(s_hfactor_value)) {
                        for (int j = 0; j < s_hfactor_values.length; j++) {
                            isHaveChild = true;
                            Map childColumns = new HashMap();
                            String childColumnId = s_hfactor_values[j];
                            String childColumnText = (String) this.getItemMap(s_hfactor).get(childColumnId);
                            childColumns.put("s_hfactor", childColumnId);
                            childColumns.put("text", childColumnText);
                            childColumns.put("tooltip", childColumnText);
                            childColumns.put("tooltipType", "title");
                            childColumns.put("sortable", false);
                            childColumns.put("menuDisabled", true);
                            childColumns.put("dataIndex", "data`" + columnId + "_" + childColumnId);
                            childColumns.put("width", 120);
                            childColumns.put("hoverCls", "");
                            childColumns.put("resizable", false);
                            childColumns.put("nowrap", true);
                            childColumnsList.add(childColumns);
                        }
                    }
                    columns.put("hfactor", columnId);
                    columns.put("text", this.getItemMap(hfactor).get(columnId));
                    columns.put("tooltip", this.getItemMap(hfactor).get(columnId));
                    columns.put("tooltipType", "title");
                    columns.put("sortable", false);
                    columns.put("menuDisabled", true);
                    columns.put("hoverCls", "");
                    columns.put("resizable", false);
                    columns.put("nowrap", true);
                    columns.put("dataIndex", "data`" + columnId + "_#");
                    if (!isHaveChild) {
                        columns.put("width", 120);
                    } else if(StringUtils.isNotBlank(s_hfactor_value)){
                        columns.put("columns", childColumnsList);
                    }
                    columnsInfoList.add(columns);
                }
            }
            if (StringUtils.isNotEmpty(s_vfactor) && StringUtils.isNotEmpty(vfactor)) {
                Map childCodeColumn = new HashMap();
                childCodeColumn.put("text", "");
                childCodeColumn.put("sortable", false);
                childCodeColumn.put("menuDisabled", true);
                childCodeColumn.put("resizable", false);
                childCodeColumn.put("hoverCls", "");
                childCodeColumn.put("focusCls", "");
                childCodeColumn.put("dataIndex", "s_vfactor_desc");
                childCodeColumn.put("locked", true);
                columnsInfoList.add(1, childCodeColumn);
                Map codeColumnTemp = (Map) columnsInfoList.get(0);
                codeColumnTemp.put("style", "border-right: 0px !important");
            }
            if (StringUtils.isNotEmpty(s_hfactor) && StringUtils.isEmpty(hfactor)) {
                String[] columnStr = hcontent.substring(1, hcontent.indexOf("]")).split(",");
                for (int i = 0; i < columnStr.length; i++) {
                    Map columns = new HashMap();
                    //一级列 代码itemid
                    String columnId = columnStr[i];
                    columns.put("hfactor", columnId);
                    columns.put("text", this.getItemMap(s_hfactor).get(columnId));
                    columns.put("tooltip", this.getItemMap(hfactor).get(columnId));
                    columns.put("tooltipType", "title");
                    columns.put("sortable", false);
                    columns.put("menuDisabled", true);
                    columns.put("hoverCls", "");
                    columns.put("dataIndex", "data`" + "#_" + columnId);
                    columns.put("width", 120);
                    columns.put("resizable", false);
                    columns.put("nowrap", true);
                    columnsInfoList.add(columns);
                }
            }
            //只选纵级指标 列为结果指标列
            if(StringUtils.isEmpty(hfactor)&&StringUtils.isEmpty(s_hfactor)){
                Map columns = new HashMap();
                String resultDesc = "";
                if(DataDictionary.getFieldItem(item)!=null){
                    resultDesc = DataDictionary.getFieldItem(item).getItemdesc();
                }
                columns.put("text",resultDesc);
                columns.put("tooltip", resultDesc);
                columns.put("tooltipType", "title");
                columns.put("sortable", false);
                columns.put("menuDisabled", true);
                columns.put("hoverCls", "");
                columns.put("dataIndex", "result");
                columns.put("width", 120);
                columns.put("resizable", false);
                columns.put("nowrap", true);
                columnsInfoList.add(columns);
            }
            if (StringUtils.isNotEmpty(s_vfactor) && StringUtils.isEmpty(vfactor)) {
                ((Map) columnsInfoList.get(0)).put("dataIndex", "s_vfactor_desc");
            } else if (StringUtils.isEmpty(vfactor) && StringUtils.isEmpty(s_vfactor)) {
                ((Map) columnsInfoList.get(0)).put("dataIndex", "result_desc");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return columnsInfoList;
    }

    /**
     * 组装标准表 store数据
     * @param standStructInfor 薪资标准表结构信息
     * @return
     */
    private List getEditStandStoreData(Map<String,String> standStructInfor,Map standData){
        Map<String,Object> resultFieldData = this.getResultFieldData(standStructInfor);
        List storeData = new ArrayList();
        try {
            String hfactor = standStructInfor.get("hfactor");
            String s_hfactor = standStructInfor.get("s_hfactor");
            String vfactor = standStructInfor.get("vfactor");
            String s_vfactor = standStructInfor.get("s_vfactor");
            String hcontent = standStructInfor.get("hcontent");
            String vcontent = standStructInfor.get("vcontent");
            String item = standStructInfor.get("item");
            //配置了 纵向栏目指标
            vcontent = PubFunc.keyWord_reback(vcontent);
            if (StringUtils.isNotEmpty(vfactor)) {
                String[] rowStr = vcontent.split(";");
                for (int i = 0; i < rowStr.length; i++) {
                    String rowTemp = rowStr[i];
                    String rowId = rowTemp.substring(0, rowTemp.indexOf("["));
                    String s_vfactor_value = rowTemp.substring(rowTemp.indexOf("[") + 1, rowTemp.indexOf("]"));
                    String[] s_vfactor_values = s_vfactor_value.split(",");
                    if (StringUtils.isNotBlank(s_vfactor_value)) {
                        for (int j = 0; j < s_vfactor_values.length; j++) {
                            Map record = new HashMap();
                            String childRowId = s_vfactor_values[j];
                            record.put("vfactor_itemid", rowId);
                            record.put("s_vfactor_itemid", childRowId);
                            record.put("vfactor_desc", this.getItemMap(vfactor).get(rowId));
                            record.put("s_vfactor_desc", this.getItemMap(s_vfactor).get(childRowId));
                            storeData.add(record);
                        }
                    } else {
                        Map record = new HashMap();
                        record.put("vfactor_itemid", rowId);
                        record.put("vfactor_desc", this.getItemMap(vfactor).get(rowId));
                        storeData.add(record);
                    }
                }

            }
            if (StringUtils.isNotEmpty(s_vfactor)&&StringUtils.isEmpty(vfactor)) {
                String[] rowStr = vcontent.substring(1, vcontent.indexOf("]")).split(",");
                for (int i = 0; i < rowStr.length; i++) {
                    String rowId = rowStr[i];
                    Map record = new HashMap();
                    record.put("s_vfactor_itemid", rowId);
                    record.put("s_vfactor_desc", this.getItemMap(s_vfactor).get(rowId));
                    storeData.add(record);
                }

            }
            List tempList = new ArrayList();
            //横向栏目配置的情况下
            hcontent = PubFunc.keyWord_reback(hcontent);
            if (StringUtils.isNotEmpty(hfactor)) {
                String[] columnStr = hcontent.split(";");
                for (int i = 0; i < columnStr.length; i++) {
                    String columnStrTemp = columnStr[i];
                    String columnId = columnStrTemp.substring(0, columnStrTemp.indexOf("["));
                    String s_hfactor_value = columnStrTemp.substring(columnStrTemp.indexOf("[") + 1, columnStrTemp.indexOf("]"));
                    String[] s_hfactor_values = s_hfactor_value.split(",");
                    if (StringUtils.isNotBlank(s_hfactor_value)) {
                        for (int j = 0; j < s_hfactor_values.length; j++) {
                            Map tempMap = new HashMap();
                            String childColumnId = s_hfactor_values[j];
                            tempMap.put("childColumnId", childColumnId);
                            tempMap.put("columnId", columnId);
                            tempList.add(tempMap);
                        }
                    } else {
                            Map tempMap = new HashMap();
                            tempMap.put("columnId", columnId);
                            tempList.add(tempMap);
                    }
                }
            }
            if (StringUtils.isNotEmpty(s_hfactor)&&StringUtils.isEmpty(hfactor)) {
                String[] columnStr = hcontent.substring(1, hcontent.indexOf("]")).split(",");
                for (int i = 0; i < columnStr.length; i++) {
                    String columnId = columnStr[i];
                    Map tempMap = new HashMap();
                    tempMap.put("childColumnId", columnId);
                    tempList.add(tempMap);
                }
            }
            //纵级栏目指标没有配置
            if(StringUtils.isEmpty(vfactor)&&StringUtils.isEmpty(s_vfactor)){
                Map record = new HashMap();
                String vfactor_itemid = "#";
                String s_vfactor_itemid = "#";
                for (int j = 0; j < tempList.size(); j++) {
                    Map columnMap = (Map) tempList.get(j);
                    String hfactor_itemid = (String) columnMap.get("columnId");
                    if(StringUtils.isEmpty(hfactor_itemid)&&!StringUtils.equalsIgnoreCase("null",hfactor_itemid)){
                        hfactor_itemid = "#";
                    }
                    String s_hfactor_itemid = (String) columnMap.get("childColumnId");
                    if(StringUtils.isEmpty(s_hfactor_itemid)&&!StringUtils.equalsIgnoreCase("null",s_hfactor_itemid)){
                        s_hfactor_itemid = "#";
                    }
                    String key = hfactor_itemid+"`"+s_hfactor_itemid+"`"+vfactor_itemid+"`"+s_vfactor_itemid;
                    //if(standData.containsKey(key)){
                        String value = (String) standData.get(key);
                        if(StringUtils.equalsIgnoreCase((String) resultFieldData.get("resultFieldType"),"number")){
                            if(StringUtils.isEmpty(value)){
                                record.put("data`"+hfactor_itemid+"_"+s_hfactor_itemid, value);
                            }else{
                                if((Integer)resultFieldData.get("resultFieldDecimalwidth")>0){
                                    record.put("data`"+hfactor_itemid+"_"+s_hfactor_itemid, Double.valueOf(value));
                                }else{
                                    record.put("data`"+hfactor_itemid+"_"+s_hfactor_itemid, Integer.valueOf(value));
                                }
                            }

                        }else{
                            if(StringUtils.isEmpty(value)){
                                value = "";
                            }
                            record.put("data`"+hfactor_itemid+"_"+s_hfactor_itemid, value);
                        }
                    //}
                }
                String resultDesc = "";
                if(DataDictionary.getFieldItem(item)!=null){
                    resultDesc = DataDictionary.getFieldItem(item).getItemdesc();
                }
                record.put("result_desc",resultDesc);
                storeData.add(record);
            }
            //Map standData = this.standTableDao.getStandTableItemData(stand_id, pkg_id);
            for(int i = 0;i<storeData.size();i++){
                Map record = (Map) storeData.get(i);
                String vfactor_itemid = (String) record.get("vfactor_itemid");
                if(StringUtils.isEmpty(vfactor_itemid)&&!StringUtils.equalsIgnoreCase("null",vfactor_itemid)){
                    vfactor_itemid = "#";
                }
                String s_vfactor_itemid = (String) record.get("s_vfactor_itemid");
                if(StringUtils.isEmpty(s_vfactor_itemid)&&!StringUtils.equalsIgnoreCase("null",s_vfactor_itemid)){
                    s_vfactor_itemid = "#";
                }
                if(StringUtils.isEmpty(s_hfactor)&&StringUtils.isEmpty(hfactor)){
                    String hfactor_itemid = "#";
                    String s_hfactor_itemid = "#";
                    if (StringUtils.isEmpty(s_hfactor_itemid) && !StringUtils.equalsIgnoreCase("null", s_hfactor_itemid)) {
                        s_hfactor_itemid = "#";
                    }
                    String key = hfactor_itemid + "`" + s_hfactor_itemid + "`" + vfactor_itemid + "`" + s_vfactor_itemid;
                    //if (standData.containsKey(key)) {
                        String value = (String) standData.get(key);
                        if(StringUtils.equalsIgnoreCase((String) resultFieldData.get("resultFieldType"),"number")){
                            if(StringUtils.isEmpty(value)){
                                record.put("result", value);
                            }else{
                                if((Integer)resultFieldData.get("resultFieldDecimalwidth")>0){
                                    record.put("result", Double.valueOf(value));
                                }else {
                                    record.put("result", Integer.valueOf(value));
                                }
                            }

                        } else {
                            if (StringUtils.isEmpty(value)) {
                                record.put("result", "");
                            } else {
                                record.put("result", value);
                            }
                        }

                    //}
                }
                for (int j = 0; j < tempList.size(); j++) {
                    Map columnMap = (Map) tempList.get(j);
                    String hfactor_itemid = (String) columnMap.get("columnId");
                    if (StringUtils.isEmpty(hfactor_itemid) && !StringUtils.equalsIgnoreCase("null", hfactor_itemid)) {
                        hfactor_itemid = "#";
                    }
                    String s_hfactor_itemid = (String) columnMap.get("childColumnId");
                    if (StringUtils.isEmpty(s_hfactor_itemid) && !StringUtils.equalsIgnoreCase("null", s_hfactor_itemid)) {
                        s_hfactor_itemid = "#";
                    }
                    String key = hfactor_itemid + "`" + s_hfactor_itemid + "`" + vfactor_itemid + "`" + s_vfactor_itemid;
                    //if (standData.containsKey(key)) {
                        String value = (String) standData.get(key);
                        if(StringUtils.equalsIgnoreCase((String) resultFieldData.get("resultFieldType"),"number")){
                            if(StringUtils.isEmpty(value)){
                                record.put("data`" + hfactor_itemid + "_" + s_hfactor_itemid, value);
                            }else{
                                if((Integer)resultFieldData.get("resultFieldDecimalwidth")>0){
                                    record.put("data`" + hfactor_itemid + "_" + s_hfactor_itemid, Double.valueOf(value));
                                }else{
                                    record.put("data`" + hfactor_itemid + "_" + s_hfactor_itemid, Integer.valueOf(value));
                                }
                            }

                        }else{
                            if(StringUtils.isEmpty(value)){
                                record.put("data`" + hfactor_itemid + "_" + s_hfactor_itemid, "");
                            }else {
                                record.put("data`" + hfactor_itemid + "_" + s_hfactor_itemid, value);
                            }
                        }

                    //}
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storeData;
    }
    private List getStoreFields(){
        List storeFields = new ArrayList();
        return storeFields;
    }

    /**
     * 判断是否有编辑标准表的权限
     * @return
     */
    private boolean isHaveEditPriv(){
        boolean isHaveEdit = false;
        if(this.userView.isSuper_admin()||this.userView.hasTheFunction("3241009")){
            isHaveEdit = true;
        }
        return isHaveEdit;
    }
    /**
     * 获取合并列 dataindex
     * @param standStructInfor
     * @return
     */
    private String getMergeColumn(Map<String,String> standStructInfor){
        String mergeCloumn = "";
        String vfactor = standStructInfor.get("vfactor");
        String s_vfactor = standStructInfor.get("s_vfactor");
        if(StringUtils.isNotEmpty(vfactor)&&StringUtils.isNotEmpty(s_vfactor)){
            mergeCloumn = "vfactor_desc";
        }
        return mergeCloumn;
    }

    /**
     * 获取结果指标数据
     * @return
     */
    private Map getResultFieldData(Map standStructInfor){
        Map resultFieldData = new HashMap();
        try{
            //默认为字符型
            String resultFieldType = "text";
            int fieldLength = 18;
            int decimalwidth = 0;
            String codesetid = "0";
            String resultField = (String) standStructInfor.get("item");
            FieldItem resultFieldItem = DataDictionary.getFieldItem(resultField);
            if(resultFieldItem != null){
                String codesetId = resultFieldItem.getCodesetid();
                String itemType = resultFieldItem.getItemtype();
                if(StringUtils.equalsIgnoreCase(itemType,"A")&&!StringUtils.equalsIgnoreCase(codesetId,"0")){
                    resultFieldType = "code";
                    codesetid = codesetId;
                }
                if(StringUtils.equalsIgnoreCase("N",itemType)){
                    resultFieldType = "number";
                    fieldLength = resultFieldItem.getItemlength();
                    decimalwidth = resultFieldItem.getDecimalwidth();
                }
            }
            resultFieldData.put("resultFieldType",resultFieldType);
            resultFieldData.put("resultFieldLength",fieldLength);
            resultFieldData.put("resultFieldDecimalwidth",decimalwidth);
            resultFieldData.put("resultFieldCodesetid",codesetid);
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultFieldData;
    }
    /**
     * 保存薪资标准表名称及归属单位的数据
     * @Author linjs
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @param name :标准表名称
     * @param b0110 :gz_stand_history表b0110列
     * @return String 返回成功或者失败的提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:21
     */
    @Override
    public String saveStandardListOrg(String pkg_id, String stand_id,String name,String b0110) throws GeneralException {
        ContentDAO contentDAO = new ContentDAO(conn);
        RowSet rs = null;
        String pkgIsActive = null;
        List paramList = new ArrayList();//存放pck_id、stand_id、name、b0110的list集合
        StringBuffer saveOrgSql = new StringBuffer();//保存标准表列表归属单位数据sql语句
        StringBuffer pkgIsActiveSql = new StringBuffer();//判断哪个历史沿革为启用的历史沿革
        pkgIsActiveSql.append("select pkg_id from gz_stand_pkg where status ='1'");
        if(StringUtils.isEmpty(b0110.split("`")[0])) {
            paramList.add(b0110.split("`")[0]);//只需要保存代码部分
        } else {
            paramList.add(","+b0110.split("`")[0]);//只需要保存代码部分
        }
        paramList.add(name);
        paramList.add(Integer.parseInt(pkg_id));//格式转换，数据库中pkg_id为int类型
        paramList.add(Integer.parseInt(stand_id));//格式转换，数据库中id为int类型
        saveOrgSql.append("update gz_stand_history set b0110 =? ,name =?").append(" where pkg_id =?").append(" and id=?");
        try {
            rs = contentDAO.search(pkgIsActiveSql.toString());
            while (rs.next()) {
                pkgIsActive = rs.getString("pkg_id");
            }
            contentDAO.update(saveOrgSql.toString(), paramList);
            if(pkgIsActive.equals(pkg_id)) {//启用的历史沿革还需要在gz_stand表修改数据
                paramList.clear();
                paramList.add(name);
                paramList.add(Integer.parseInt(stand_id));//格式转换，数据库中id为int类型
                contentDAO.update("update gz_stand set name =? where id =?", paramList);
            }
            return "success";
        } catch (Exception e) {
            return "fail";
        }
        finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 校验标准表是否能被删除
     * @return
     */
    @Override
    public Map checkStandDel(String[] stand_ids, String pkg_id) {
        RowSet rs = null;
        StringBuffer msgBuffer = new StringBuffer();
        Map result = new HashMap();
        boolean isDelete = true;
        ContentDAO dao = new ContentDAO(this.conn);
        boolean isActive = this.isPackageActive(PubFunc.decrypt(pkg_id));
        if (isActive) {
            String sql = "select STANDID from salaryformula where runflag = 1 and STANDID in ( ";
            for (int i = 0; i < stand_ids.length; i++) {
                String stand_id_de = PubFunc.decrypt(stand_ids[i]);
                sql += "'"+stand_id_de+"'";
                if (i < stand_ids.length - 1) {
                    sql += ",";
                }
            }
            sql += ")";
            try {
                rs = dao.search(sql);
                while (rs.next()) {
                    String standid = rs.getString("STANDID");
                    if(msgBuffer.indexOf(standid+",") == -1){
                        msgBuffer.append(standid).append(",");
                    }
                    isDelete = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                PubFunc.closeResource(rs);
            }
            result.put("msg", msgBuffer.toString());
        }
        result.put("delFlag", isDelete);
        return result;
    }

    private String verifFieldItem(Map standInfor) {
        StringBuffer returnMsgCodeNull = new StringBuffer();
        StringBuffer returnMsgCodeDatbase = new StringBuffer();
        String itemStr = (String) standInfor.get("item");
        String vfactorStr = (String) standInfor.get("vfactor");
        String s_vfactorStr = (String) standInfor.get("s_vfactor");
        String hfactorStr = (String) standInfor.get("hfactor");
        String s_hfactorStr = (String) standInfor.get("s_hfactor");
        FieldItem item = DataDictionary.getFieldItem(itemStr);
        FieldItem vfactor = DataDictionary.getFieldItem(vfactorStr);
        FieldItem s_vfactor = DataDictionary.getFieldItem(s_vfactorStr);
        FieldItem hfactor = DataDictionary.getFieldItem(hfactorStr);
        FieldItem s_hfactor = DataDictionary.getFieldItem(s_hfactorStr);

        if (returnMsgCodeNull.toString().indexOf(itemStr)==-1&&StringUtils.isNotEmpty(itemStr) && item == null) {
            //证明在数据库中不存在
            returnMsgCodeNull.append(itemStr).append(",");
        }
        if (returnMsgCodeDatbase.toString().indexOf(itemStr)==-1&&item != null && StringUtils.equalsIgnoreCase("0", item.getUseflag())) {
            //证明数据未构库
            returnMsgCodeDatbase.append(item.getItemdesc()).append("(").append(itemStr).append("),");
        }

        if (returnMsgCodeNull.toString().indexOf(vfactorStr)==-1&&StringUtils.isNotEmpty(vfactorStr) && vfactor == null) {
            returnMsgCodeNull.append(vfactorStr).append(",");
        }
        if (returnMsgCodeDatbase.toString().indexOf(vfactorStr)==-1&&vfactor != null && StringUtils.equalsIgnoreCase("0", vfactor.getUseflag())) {
            returnMsgCodeDatbase.append(vfactor.getItemdesc()).append("(").append(vfactorStr).append("),");
        }


        if (returnMsgCodeNull.toString().indexOf(s_vfactorStr)==-1&&StringUtils.isNotEmpty(s_vfactorStr) && s_vfactor == null) {
            returnMsgCodeNull.append(s_vfactorStr).append(",");
        }
        if (returnMsgCodeDatbase.toString().indexOf(s_vfactorStr)==-1&&s_vfactor != null && StringUtils.equalsIgnoreCase("0", s_vfactor.getUseflag())) {
            returnMsgCodeDatbase.append(s_vfactor.getItemdesc()).append("(").append(s_vfactorStr).append("),");
        }

        if (returnMsgCodeNull.toString().indexOf(hfactorStr)==-1&&StringUtils.isNotEmpty(hfactorStr) && hfactor == null) {
            returnMsgCodeNull.append(hfactorStr).append(",");
        }
        if (returnMsgCodeDatbase.indexOf(hfactorStr)==-1&&hfactor != null && StringUtils.equalsIgnoreCase("0", hfactor.getUseflag())) {
            returnMsgCodeDatbase.append(hfactor.getItemdesc()).append("(").append(hfactorStr).append("),");
        }

        if (returnMsgCodeNull.toString().indexOf(s_hfactorStr)==-1&&StringUtils.isNotEmpty(s_hfactorStr) && s_hfactor == null) {
            returnMsgCodeNull.append(s_hfactorStr).append(",");
        }
        if (returnMsgCodeDatbase.toString().indexOf(s_hfactorStr)==-1&&s_hfactor != null && StringUtils.equalsIgnoreCase("0", s_hfactor.getUseflag())) {
            returnMsgCodeDatbase.append(s_hfactor.getItemdesc()).append("(").append(s_hfactorStr).append("),");
        }

        if (StringUtils.isNotEmpty(returnMsgCodeNull.toString())) {
            returnMsgCodeNull.append(ResourceFactory.getProperty("standard.standardList.returnMsgCodeNull"));
        }
        if (StringUtils.isNotEmpty(returnMsgCodeDatbase.toString())) {
            returnMsgCodeDatbase.append(ResourceFactory.getProperty("standard.standardList.returnMsgCodeDatbase"));
        }

        return returnMsgCodeNull.append(returnMsgCodeDatbase).toString();
    }
}

