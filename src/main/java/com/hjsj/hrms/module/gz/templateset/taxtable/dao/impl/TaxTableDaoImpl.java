package com.hjsj.hrms.module.gz.templateset.taxtable.dao.impl;

import com.hjsj.hrms.module.gz.templateset.taxtable.dao.ITaxTableDao;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 税率表数据库操作接口实现类
 * @Author manjg
 * @Date 2019/12/3 16:06
 * @Version V1.0
 **/
public class TaxTableDaoImpl implements ITaxTableDao {

    /** 日志对象 */
    private static Category log = Category.getInstance(TaxTableDaoImpl.class.getName());
    /** 数据库连接对象 */
    private Connection conn;
    /** 数据库操作对象 */
    private ContentDAO dao;

    public TaxTableDaoImpl(Connection conn) {
        this.conn = conn;
        this.dao = new ContentDAO(conn);
    }

    /**
     * 获取全部税率方案集合
     * @return 返回全部税率方案集合
     * @throws GeneralException
     */
    @Override
    public List<DynaBean> listTaxTables() throws GeneralException {
        List<DynaBean> beanList = new ArrayList<DynaBean>();
        try {
            StringBuffer taxTablesSql = new StringBuffer();
            taxTablesSql.append("select * from gz_tax_rate order by taxid");
            beanList = this.dao.searchDynaList(taxTablesSql.toString());
            for (DynaBean bean : beanList) {
                String taxid = (String) bean.get("taxid");
                taxid = PubFunc.encrypt(taxid);
                bean.set("taxid",taxid);
            }
        } catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.getAllTaxTableError",e);
            throw new GeneralException("gz.taxTable.msg.getAllTaxTableError");
        }

        return beanList;
    }

    /**
     * 获取指定税率表方案集合信息(导出税率表)
     * @param ids 税率表id
     * @return 返回指定税率表方案集合
     * @throws GeneralException
     */
    @Override
    public List<DynaBean> listTaxTables(String ids) throws GeneralException {
        List<DynaBean> beanList = new ArrayList<DynaBean>();
        RowSet rowSet = null;
        StringBuffer searchTaxTablesSql = new StringBuffer();
        ArrayList param = new ArrayList();
        try {
            searchTaxTablesSql.append("select * from gz_tax_rate where taxid in (");
            String[] idsArr = ids.split(",");
            for (String id : idsArr){
                searchTaxTablesSql.append("?,");
                param.add(id);
            }
            searchTaxTablesSql.setLength(searchTaxTablesSql.length()-1);
            searchTaxTablesSql.append(") order by taxid");

            rowSet = this.dao.search(searchTaxTablesSql.toString(),param);
            beanList = this.dao.getDynaBeanList(rowSet);
            for (DynaBean bean : beanList) {
                String taxid = (String) bean.get("taxid");
                taxid = PubFunc.encrypt(taxid);
                bean.set("taxid",taxid);
            }
        } catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.getSpecifyTaxTableError",e);
            throw new GeneralException("gz.taxTable.msg.getSpecifyTaxTableError");
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return beanList;
    }

    /**
     * 批量新增税率方案
     * @param voList 税率方案集合
     * @throws GeneralException
     */
    @Override
    public void insertTaxTable(List<RecordVo> voList) throws GeneralException {
        try {
            this.dao.addValueObject(voList);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.insertError",e);
            throw new GeneralException("gz.taxTable.msg.insertError");
        }

    }

    /**
     * 批量更新税率表方案
     * @param voList 税率方案集合
     * @throws GeneralException
     */
    @Override
    public void updateTaxTable(List<RecordVo> voList) throws GeneralException {
        try {
            this.dao.updateValueObject(voList);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.uopdateError",e);
            throw new GeneralException("gz.taxTable.msg.uopdateError");
        }
    }

    /**
     * 批量删除税率表方案
     * @param voList 税率方案集合
     * @throws GeneralException
     */
    @Override
    public void deleteTaxTable(List<RecordVo> voList) throws GeneralException {
        try {
            this.dao.deleteValueObject(voList);
        }catch (Exception e) {
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.deleteError",e);
            throw new GeneralException("gz.taxTable.msg.deleteError");
        }
    }

    /**
     * 获取指定税率方案明细集合
     * @param ids 税率方案明细id
     * @return 税率方案明细集合
     * @throws GeneralException
     */
    @Override
    public List<DynaBean> listTaxTableDetails(String ids) throws GeneralException {
        List<DynaBean> beanList = new ArrayList<DynaBean>();
        StringBuffer detailSearchSql = new StringBuffer();
        ArrayList param = new ArrayList();
        RowSet rowSet = null;
        try {
            detailSearchSql.append("select * from gz_taxrate_item where taxid in (");
            String[] idsArr = ids.split(",");
            for (String id : idsArr){
                detailSearchSql.append("?,");
                param.add(id);
            }
            detailSearchSql.setLength(detailSearchSql.length()-1);
            detailSearchSql.append(") order by taxid,taxitem");
            rowSet = this.dao.search(detailSearchSql.toString(),param);
            beanList = this.dao.getDynaBeanList(rowSet);
            for (DynaBean bean : beanList){
                String taxitem = (String) bean.get("taxitem");
                taxitem = PubFunc.encrypt(taxitem);
                String taxid = (String) bean.get("taxid");
                taxid = PubFunc.encrypt(taxid);
                bean.set("taxitem",taxitem);
                bean.set("taxid",taxid);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.getDetailDataError",e);
            throw new GeneralException("gz.taxTable.msg.getDetailDataError");
        }finally {
            PubFunc.closeResource(rowSet);
        }

        return beanList;
    }

    /**
     * 批量新增税率方案明细集合
     * @param voList 税率方案明细集合
     * @throws GeneralException
     */
    @Override
    public void insertTaxTableDetail(List<RecordVo> voList) throws GeneralException {
        try {
            this.dao.addValueObject(voList);
        } catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.insertDetailError",e);
            throw new GeneralException("gz.taxTable.msg.insertDetailError");
        }
    }

    /**
     * 批量更新税率方案明细集合
     * @param voList 税率方案明细集合
     * @throws GeneralException
     */
    @Override
    public void updateTaxTableDetail(List<RecordVo> voList) throws GeneralException {
        try {
            this.dao.updateValueObject(voList);
        } catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.updateDetailError",e);
            throw new GeneralException("gz.taxTable.msg.updateDetailError");
        }
    }

    /**
     * 批量删除税率方案明细集合
     * @param voList 税率方案明细集合
     * @throws GeneralException
     */
    @Override
    public void deleteTaxTableDetail(List<RecordVo> voList) throws GeneralException {
        try {
            this.dao.deleteValueObject(voList);
        } catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.deleteDetailError",e);
            throw new GeneralException("gz.taxTable.msg.deleteDetailError");
        }
    }

    /**
     * 查询税率表方案是否在计算公式中使用
     * @param ids
     * @return [{
     *              standid:xxx,//税率表id
     *              description:'xxx',//税率表名称
     *              salaryid:xxx,//薪资类别id
     *              cname:'xxx',//薪资类别名称
     *              hzname:'xxx',//指标描述
     *              itemname:'xxx'//指标id
     *          },{...}...]
     * @throws GeneralException
     */
    @Override
    public List<DynaBean> isHaveTaxTableTosalaryformula(List ids) throws GeneralException {
        List<DynaBean> beanList = new ArrayList<DynaBean>();
        StringBuffer checkSql = new StringBuffer();
        ArrayList param = new ArrayList();
        RowSet rowSet = null;
        try {
            checkSql.append("select sf.standid,gt.description,sf.salaryid,st.cname,sf.hzname,sf.itemname from ");
            checkSql.append("salaryformula sf,salarytemplate st,gz_tax_rate gt where ");
            checkSql.append("sf.salaryid = st.salaryid and sf.standid = gt.taxid and sf.Runflag = 2 and sf.standid in (");
            for (Object id : ids){
                String standid = id.toString();
                checkSql.append("?,");
                param.add(standid);
            }
            checkSql.setLength(checkSql.length()-1);
            checkSql.append(") order by sf.standid");
            rowSet = this.dao.search(checkSql.toString(),param);
            beanList = this.dao.getDynaBeanList(rowSet);
        }catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.checkTaxTableTosalaryformulaError",e);
            throw new GeneralException("gz.taxTable.msg.checkTaxTableTosalaryformulaError");
        }finally {
            PubFunc.closeResource(rowSet);
        }

        return beanList;
    }

    /**
     * 获取计税方式代码项集合
     * @return 计税方式代码项集合
     * @throws GeneralException
     */
    @Override
    public List<DynaBean> listTaxModeCodeItem() throws GeneralException {
        List<DynaBean> beanList = new ArrayList<DynaBean>();
        try {
            ArrayList<CodeItem> codeitemList = AdminCode.getCodeItemList("46");
            for (CodeItem codeItem : codeitemList) {
                DynaBean bean = new LazyDynaBean();
                String codesetId = codeItem.getCodeid();
                String codeItemId = codeItem.getCodeitem();
                String codeitemdesc = codeItem.getCodename();
                bean.set("codesetid",codesetId);
                bean.set("codeitemid",codeItemId);
                bean.set("codeitemdesc",codeitemdesc);
                beanList.add(bean);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.getTaxModeError",e);
            throw new GeneralException("gz.taxTable.msg.getTaxModeError");
        }

        return beanList;
    }

    /**
     * 获取指定税率表下税率明细表最大ID值
     * @param taxid 表名称
     * @return maxid 最大ID值
     * @throws GeneralException
     */
    @Override
    public String getMaxTaxitemId(String taxid) throws GeneralException {
        String maxid = "";
        RowSet rowSet = null;
        StringBuffer searchSql = new StringBuffer();
        ArrayList param = new ArrayList();
        try {
            searchSql.append("select max(taxitem) maxId from gz_taxrate_item where taxid = ?");
            param.add(taxid);
            rowSet = this.dao.search(searchSql.toString(),param);
            if(rowSet.next()){
                maxid = rowSet.getString("maxId");
            }
            if (isEmpty(maxid)){
                maxid = "0";
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.getMaxTaxitemIdError",e);
            throw new GeneralException("gz.taxTable.msg.getMaxTaxitemIdError");
        }finally {
            PubFunc.closeResource(rowSet);
        }

        return maxid;
    }

    /**
     * 获取税率表最大ID值
     * @return maxid 最大ID值
     * @throws GeneralException
     */
    @Override
    public String getMaxId(String tableName) throws GeneralException {
        String maxid = "";
        RowSet rowSet = null;
        StringBuffer searchSql = new StringBuffer();
        try {
            if(StringUtils.equalsIgnoreCase(tableName,"gz_tax_rate")){
                searchSql.append("select max(taxid) maxId from gz_tax_rate");
                rowSet = this.dao.search(searchSql.toString());
                if(rowSet.next()){
                    maxid = rowSet.getString("maxId");
                }
            }else if(StringUtils.equalsIgnoreCase(tableName,"gz_taxrate_item")){
                searchSql.append("select max(taxitem) maxId from gz_taxrate_item");
                rowSet = this.dao.search(searchSql.toString());
                if(rowSet.next()){
                    maxid = rowSet.getString("maxId");
                }
            }
            if (isEmpty(maxid)){
                maxid = "0";
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.getMaxTaxidError",e);
            throw new GeneralException("gz.taxTable.msg.getMaxIdError");
        }finally {
            PubFunc.closeResource(rowSet);
        }

        return maxid;
    }

    /**
     * 判断对象是否为空
     * @param object 需要判断的对象
     * @return
     */
    private static boolean isEmpty(Object object) {
        return (object == null || "".equals(object));
    }
}