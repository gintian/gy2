package com.hjsj.hrms.module.kq.config.item.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 考勤项目
 *
 * @author ZhangHua
 * @version V75
 * @date 14:27 2018/10/24
 */
public interface KqItemService {


    /**
     * 同步kqitem数据
     *
     * @throws GeneralException
     * @author ZhangHua
     * @date 13:45 2018/10/30
     */
    void synchronizeCodeItemToKqItem() throws GeneralException;

    /**
     * 获取考勤项目
     * （方法名以list开头 后接驼峰格式表名）
     *
     * @param sqlWhere      数据范围
     * @param parameterList 参数 (不需要写where 以and开头)
     * @param sqlSort       排序sql(不需要写order by 仅写字段即可)
     * @return ArrayList<LazyDynaBean> (LazyDynaBean内为该表查询结果的全部字段)
     * @throws GeneralException 接口方法必须抛出异常
     * @author ZhangHua
     * @date 11:29 2018/10/30
     */
    ArrayList<LazyDynaBean> listKqItem(String sqlWhere, ArrayList parameterList, String sqlSort) throws GeneralException;

    /**
     * 获取q35表字段
     *
     * @return
     * @author ZhangHua
     * @date 14:27 2018/10/24
     */
    ArrayList<LazyDynaBean> listQ35Item() throws GeneralException;

    /**
     * 保存项目
     *
     * @param kq_itemId 行id
     * @param column_id 列id
     * @param value     值
     * @return
     * @author ZhangHua
     * @date 14:27 2018/10/24
     */
    boolean saveData(String kq_itemId, String column_id, Object value) throws GeneralException;

    /**
     * 删除项目
     *
     * @param kq_itemId 行id
     * @return
     * @author ZhangHua
     * @date 14:27 2018/10/24
     */
    boolean deleteItem(String kq_itemId) throws GeneralException;

    /**
     * 项目排序
     *
     * @param ori_id
     * @param to_id
     * @param to_seq
     * @param ori_seq
     * @param dropPosition
     * @return
     * @author ZhangHua
     * @date 14:27 2018/10/24
     */
    ArrayList<LazyDynaBean> dropKqItem(String ori_id, String to_id, String to_seq, String ori_seq, String dropPosition) throws GeneralException;

	/**
	 * 查询导入指标参数设置
	 * 
	 * @param kq_itemid
	 *            考勤项目编号
	 * @return
	 */
	HashMap<String, Object> searchFieldImportParam(String kq_itemid);

	/**
	 * 查询来源子集中符合条件的指标
	 * 
	 * @param fieldSetId
	 *            来源子集
	 * @param fieldItemId
	 *            统计指标
	 * @return
	 */
	HashMap<String, Object> searchItem(String fieldSetId, String fieldItemId);

	/**
	 * 保存导入指标参数设置
	 * 
	 * @param param
	 *            前台传递参数
	 * @return
	 */
	HashMap<String, String> saveImportParam(MorphDynaBean param);
}
