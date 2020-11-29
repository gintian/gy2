/**
 * FileName: StandTableDaoImpl
 * Author:   xuchangshun
 * Date:     2019/11/25 11:28
 * Description: 标准表数据层实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.standard.standard.repository.impl;

import com.hjsj.hrms.module.gz.standard.standard.repository.IStandTableDao;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈类功能描述〉<br>
 * 〈标准表数据层实现类〉
 *
 * @Author xuchangshun
 * @Date 2019/11/25
 * @since 1.0.0
 */
public class StandTableDaoImpl implements IStandTableDao {
    /**数据库底层操作类**/
    private ContentDAO contentDAO;

    public StandTableDaoImpl(Connection conn){
        this.contentDAO = new ContentDAO(conn);
    }

    /**
     * 获取标准表结构信息
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @return RecordVo 标准表结构信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:58
     */
    @Override
    public RecordVo getStandTableInfor(String pkg_id, String stand_id) throws GeneralException {
        RecordVo vo = null;
        try {
            vo = new RecordVo("gz_stand_history");
            vo.setObject("id", stand_id);
            vo.setObject("pkg_id", pkg_id);
            vo = contentDAO.findByPrimaryKey(vo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("getStandTableInforError");
        }
        return vo;
    }

    /**
     * 保存标准表结构信息
     * @Author xuchangshun
     * @param pck_id :历史沿革id
     * @param stand_id :标准表id
     * @param standTableInfor 标准表结构信息
     * @return String 返回保存成功与否信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 18:00
     */
    @Override
    public String saveStandTableInfor(String pck_id, String stand_id, Map standTableInfor) throws GeneralException {
        return null;
    }

    /**
     * 获取二级指标的所有表达式
     * @Author xuchangshun
     * @param itemId :选中的二级指标的itemid
     * @return List<RecordVo> 二级指标的表达式列表
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 18:05
     */
    @Override
    public List<RecordVo> getItemLexprList(String itemId) throws GeneralException {
        RowSet rs = null;
        String item = itemId;
        RecordVo vo = null;
        List<RecordVo> datalList = new ArrayList<RecordVo>();
        try {
            String sql = "select * from gz_stand_date where item =? order by "+Sql_switcher.toInt("item_id")+"";
            List valueList = new ArrayList();
            valueList.add(item);
            rs = this.contentDAO.search(sql, valueList);
            while (rs.next()) {
                vo = new RecordVo("gz_stand_date");
                vo.setString("item", item);
                vo.setString("item_id", rs.getString("item_id"));
                vo.setString("description", rs.getString("description"));
                vo.setString("lexpr", rs.getString("lexpr"));
                vo.setString("factor", rs.getString("factor"));
                datalList.add(vo);
            }
        } catch (Exception e) {
            throw new GeneralException("getItemLexprError");
        }
        return datalList;
    }

    /**
     * 获取二级指标指定表达式的内容
     * @Author xuchangshun
     * @param item :二级指标的itemId
     * @param item_id :要操作的表达式的id
     * @return RecordVo 表达式的内容
     * @throws GeneralException 异常信息
     * @Date 2019/11/25 10:37
     */
    @Override
    public RecordVo getItemLexpr(String item, String item_id) throws GeneralException {
        RowSet rs = null;
        RecordVo vo = null;
        List<RecordVo> datalList = new ArrayList<RecordVo>();
        try {
            String sql = "select * from gz_stand_date where item =? and item_id=? order by item_id";
            List valueList = new ArrayList();
            valueList.add(item);
            valueList.add(item_id);
            rs = this.contentDAO.search(sql, valueList);
            while (rs.next()) {
                vo = new RecordVo("gz_stand_date");
                vo.setString("item", item);
                vo.setString("item_id", rs.getString("item_id"));
                vo.setString("description", rs.getString("description"));
                vo.setString("lexpr", rs.getString("lexpr"));
                vo.setString("factor", rs.getString("factor"));
            }
        } catch (Exception e) {
            // TODO: handle exception
            throw new GeneralException("getItemLexprError");
        }
        return vo;
    }

    /**
     * 保存二级指标表达式的内容
     * @Author xuchangshun
     * @param item :二级指标id
     * @param item_id :操作的表达式顺序id
     * @param lexprInfor :表达式的内容
     * @return String 成功与否的提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/25 11:49
     */
    @Override
    public String saveItemLexpr(String item, String item_id, Map lexprInfor) throws GeneralException {
        String return_code="success";
        try {
            RecordVo vo=new RecordVo("gz_stand_date");
            vo.setString("item",item);
            vo.setString("item_id",item_id);
            vo.setString("description",(String) lexprInfor.get("description"));
            vo.setString("lexpr",(String) lexprInfor.get("lexpr"));
            vo.setString("factor",(String) lexprInfor.get("factor"));
            this.contentDAO.addValueObject(vo);
        } catch (Exception e) {
            // TODO: handle exception
            return_code = "fail";
            throw new GeneralException("saveItemLexprError");
        }
        return return_code;
    }

    /**
     * 获得薪资标准表的各单元格数据
     * @Author xuchangshun
     * @param stand_id :标准表id
     * @param pck_id :历史沿革id
     * @param resultFieldData :结果指标数据
     * @return List<RecordVo>
     * @throws GeneralException 异常信息
     * @Date 2019/11/25 10:56
     */
    @Override
    public Map<String,String> getStandTableItemData(String stand_id, String pck_id,Map resultFieldData) throws GeneralException {
        RowSet rs = null;
        Map record = new HashMap();
        try {
            String sql = "select hvalue,s_hvalue,vvalue,s_vvalue,standard from gz_item_history where id = ? and pkg_id = ?";
            List<String> param = new ArrayList<String>();
            param.add(stand_id);
            param.add(pck_id);
            rs = this.contentDAO.search(sql, param);
            while (rs.next()) {
                //横向栏目 值
                String hvalue = "#";
                //横向子栏目 值
                String s_hvalue = "#";
                //纵向栏目 值
                String vvalue = "#";
                //纵向子栏目 值
                String s_vvalue = "#";
                //结果值
                String standard = "";
                if (StringUtils.isNotEmpty(rs.getString("hvalue")) && rs.getString("hvalue").trim().length() > 0) {
                    hvalue = rs.getString("hvalue");
                }
                if (StringUtils.isNotEmpty(rs.getString("s_hvalue")) && rs.getString("s_hvalue").trim().length() > 0) {
                    s_hvalue = rs.getString("s_hvalue");
                }
                if (StringUtils.isNotEmpty(rs.getString("vvalue")) && rs.getString("vvalue").trim().length() > 0) {
                    vvalue = rs.getString("vvalue");
                }
                if (StringUtils.isNotEmpty(rs.getString("s_vvalue")) && rs.getString("s_vvalue").trim().length() > 0) {
                    s_vvalue = rs.getString("s_vvalue");
                }
                if (StringUtils.isNotEmpty(rs.getString("standard")) && rs.getString("standard").trim().length() > 0) {
                    standard = rs.getString("standard");
                    if(StringUtils.equalsIgnoreCase((String) resultFieldData.get("resultFieldType"),"code")){
                        standard = standard + "`"+ AdminCode.getCodeName((String) resultFieldData.get("resultFieldCodesetid"),standard);
                    }
                }
                if(StringUtils.equalsIgnoreCase((String) resultFieldData.get("resultFieldType"),"code")){
                    if(StringUtils.isEmpty(standard)){
                        standard = "";
                    }
                }else if(StringUtils.equalsIgnoreCase((String) resultFieldData.get("resultFieldType"),"number")){
                    if(StringUtils.isEmpty(standard)){
                        standard = null;
                    }else{
                        standard = this.subZeroAndDot(standard);
                    }
                }
                record.put(hvalue + "`" + s_hvalue + "`" + vvalue + "`" + s_vvalue, standard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("getStandTableItemDataError");
        }finally {
            PubFunc.closeResource(rs);
        }
        return record;
    }

    /**
     * 保存薪资标准表单元格数据
     * @Author xuchangshun
     * @param stand_id :标准表id
     * @param pck_id :历史沿革id
     * @param standTableDataInfor :各单元格数据
     * @return String 返回保存成功与否信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/25 11:21
     */
    @Override
    public String saveStandTabeleItemData(String stand_id, String pck_id, List standTableDataInfor) throws GeneralException {
        return null;
    }
    /**
     * 使用java正则表达式去掉多余的.与0
     * @param value 标准表值
     * @return
     */
    public  String subZeroAndDot(String value){
        if(value.indexOf(".") > 0){
            //去掉多余的0
            value = value.replaceAll("0+?$", "");
            //如最后一位是.则去掉
            value = value.replaceAll("[.]$", "");
        }
        return value;
    }

    /**
     * 删除标准表列表记录
     * @Author linjiasi
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @return String 返回成功或者失败的提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:55
     */
    @Override
    public String deleteStandTableList(String pkg_id,String stand_id) throws GeneralException {
        RowSet rs = null;
        String pkgIsActive = null;
        StringBuffer deleteStandTableListSql = new StringBuffer();
        List<Integer> parameterList = new ArrayList<Integer>();//参数列表，包括pck_id和stand_id
        StringBuffer pkgIsActiveSql = new StringBuffer();//判断哪个历史沿革为启用的历史沿革
        pkgIsActiveSql.append("select pkg_id from gz_stand_pkg where status ='1'");
        deleteStandTableListSql.append("delete from gz_stand_history where pkg_id = ? and id =?");
        deleteStandTableListSql.append("delete from gz_item_history where pkg_id = ? and id =?");
        parameterList.add(Integer.parseInt(pkg_id));//格式转换，数据库中pkg_id为int类型
        parameterList.add(Integer.parseInt(stand_id));//格式转换，数据库中id为int类型
        try {
            rs = contentDAO.search(pkgIsActiveSql.toString());
            while (rs.next()) {
                pkgIsActive = rs.getString("pkg_id");
            }
            contentDAO.delete("delete from gz_stand_history where pkg_id = ? and id =?", parameterList);
            contentDAO.delete("delete from gz_item_history where pkg_id = ? and id =?", parameterList);
            if(pkgIsActive.equals(pkg_id)) {//启用的历史沿革还需要在gz_stand表和gz_item表删除数据
                parameterList.clear();
                parameterList.add(Integer.parseInt(stand_id));//格式转换，数据库中id为int类型
                contentDAO.delete("delete from gz_stand where id =?", parameterList);
                contentDAO.delete("delete from gz_item where id =?", parameterList);
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }
}
