/**
 * FileName: IStandTableDao
 * Author:   xuchangshun
 * Date:     2019/11/22 17:50
 * Description: 标准表数据层
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.standard.standard.repository;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;

import java.util.List;
import java.util.Map;

/**
 * 〈接口功能描述〉<br> 
 * 〈标准表数据层〉
 *
 * @Author xuchangshun
 * @Date 2019/11/22 17:50
 * @Since 1.0.0
 */
public interface IStandTableDao {
   
    /**
     * 获取标准表结构信息
     * @Author xuchangshun
     * @param pck_id :历史沿革id
     * @param stand_id :标准表id
     * @return RecordVo 标准表结构信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:58
     */
    RecordVo getStandTableInfor(String pck_id,String stand_id)throws GeneralException;
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
    String saveStandTableInfor(String pck_id,String stand_id, Map standTableInfor)throws GeneralException;
    /**
     * 获取二级指标的所有表达式
     * @Author xuchangshun
     * @param itemId :选中的二级指标的itemid
     * @return List<RecordVo> 二级指标的表达式列表
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 18:05
     */
    List<RecordVo> getItemLexprList(String itemId) throws GeneralException;
    /**
     * 获取二级指标指定表达式的内容
     * @Author xuchangshun
     * @param item :二级指标的itemId
     * @param item_id :要操作的表达式的id
     * @return RecordVo 表达式的内容
     * @throws GeneralException 异常信息
     * @Date 2019/11/25 10:37
     */
    RecordVo getItemLexpr(String item,String item_id)throws GeneralException;
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
    String saveItemLexpr(String item,String item_id,Map lexprInfor)throws GeneralException;
    /**
     * 获得薪资标准表的各单元格数据
     * @Author xuchangshun
     * @param stand_id :标准表id
     * @param pck_id :历史沿革id
     * @param resultFieldData :结果指标数据
     * @return List<Map>
     * @throws GeneralException 异常信息
     * @Date 2019/11/25 10:56
     */
    Map<String,String> getStandTableItemData(String stand_id,String pck_id,Map resultFieldData)throws GeneralException;
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
    String saveStandTabeleItemData(String stand_id,String pck_id,List standTableDataInfor)throws GeneralException;
    /**
     * 删除标准表列表记录
     * @Author linjiasi
     * @param pck_id :历史沿革id
     * @param stand_id :标准表id
     * @return String 返回成功或者失败的提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:55
     */
    String deleteStandTableList(String pkg_id,String stand_id) throws GeneralException;
    
}
