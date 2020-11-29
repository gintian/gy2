package com.hjsj.hrms.module.statistical.businessobject;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 统计分析接口类
 * @author wangb 20190326
 * @category hjsj
 * @version 1.0
 */
public interface StatisticalService {

	/**
	 * 获取权限范围内的统计条件  
	 * @param userView 
	 * @return
	 * @throws GeneralException
	 * @author wangb
	 */
	List listAllStatisial(UserView userView) throws GeneralException;	
	
	/**
	 * 获取统计分析图标的名称数组
	 * @author wangbs
	 * @param realPath 服务器绝对路径,用于解析文件夹下的文件
	 * @return
	 * @throws GeneralException
	 */
	List getStatisticalIconName(String realPath) throws GeneralException;
	
	/**
	 * 获取人員一维统计条件数据
	 * @author wangb
	 * @param userView
	 * @param statid  加密后的id 
	 * @param infokind 
	 * @param sformulaId 统计方式id 值不存在时传null或""  不加盟
	 * @param org_filter 按组织机构筛选参数  人员范围权限
	 * @param filterId 筛选机构id
	 * @return
	 * @throws GeneralException
	 */
	Map getStatisicalChartData(UserView userView ,String statid,String infokind,String sformulaId,String org_filter,String filterId) throws GeneralException;
	
	/**
	 * 获取人员二维统计条件数据 
	 * @param userView
	 * @param statid  加密后的id
	 * @param infokind  
	 * @param sformulaId 统计方式id号
	 * @param vtotal  垂直下标
	 * @param htotal  水平下标
	 * @param org_filter 按组织机构筛选参数  人员范围权限
	 * @param filterId 筛选机构id
	 * @return
	 * @throws GeneralException
	 */
	Map getStatisicalDoubleChartData(UserView userView ,String statid,String infokind,String sformulaId,String vtotal,String htotal,String org_filter,String filterId) throws GeneralException;
	
	/**
	 * 获取人员多维统计条件数据
	 * @param userView 
	 * @param statid  加密后的id
	 * @param infokind 
	 * @param vtotal  纵向合计  0 不合计 1合计
	 * @param htotal  横向合计  0不合计  1合计
	 * @param vnull  隐藏空列  0 不隐藏 1 隐藏
	 * @param hnull  隐藏空行  0 不隐藏 1 隐藏
	 * @param org_filter 按组织机构筛选参数  人员范围权限
	 * @param filterId 筛选机构id
	 * @return
	 * @throws GeneralException
	 */
	Map getStatisicalMoreChartData(UserView userView ,String statid,String infokind,String vtotal,String htotal,String vnull,String hnull,String org_filter, String filterId) throws GeneralException;
	
	/**
	 * 获取人员一维统计穿透人员列表信息
	 * @author wangb  20190401
	 * @param userView 
	 * @param statid   统计条件id 加密
	 * @param infokind  固定值 1  人员
	 * @param showLegend 统计项标题 加密
	 * @param pageIndex  第几页  
	 * @param pageSize   每页显示数
	 * @param filterId 筛选机构id号
	 * @return
	 * @throws GeneralException
	 */
	Map getStatisicalPersonList(UserView userView,String statid,String infokind,String showLegend,int pageIndex,int pageSize,String filterId) throws GeneralException;
	
	/**
	 * 获取人员二维统计穿透人员列表信息
	 * @param userView  用户信息
	 * @param statid    统计id 加密
	 * @param infokind  人员类型
	 * @param v    纵向下标
	 * @param h    横向下标
	 * @param pageIndex 当前页
	 * @param pageSize  每页显示数
	 * @param filterId 筛选机构id号
	 * @return
	 * @throws GeneralException
	 */
	Map getStatisicalPersonList(UserView userView,String statid,String infokind,String v,String h,int pageIndex,int pageSize,String filterId) throws GeneralException;
	
	/**
	 * 获取人员多维统计穿透人员列表信息
	 * @param userView 用户信息
	 * @param statid 统计id 加密
	 * @param infokind 人员类型
	 * @param v 纵向下标
	 * @param h 横向下标
	 * @param vtotal  纵向合计  0 不合计 1合计
	 * @param htotal  横向合计  0不合计  1合计
	 * @param vnull  隐藏空列  0 不隐藏 1 隐藏
	 * @param hnull  隐藏空行  0 不隐藏 1 隐藏
	 * @param pageIndex
	 * @param pageSize
	 * @param filterId 筛选机构id号
	 * @return
	 * @throws GeneralException
	 */
	Map getStatisicalPersonList(UserView userView ,String statid,String infokind,String v,String h,String lengthways,
			String crosswise,String vtotal,String htotal,String vnull,String hnull,int pageIndex,int pageSize,String filterId) throws GeneralException;

	/**
	 * 导出统计图、表到excel
	 * @param userView        用户数据
	 * @param viewAndTableData 统计图和表格数据
	 * @return java.lang.String
	 * @throws GeneralException 抛出异常
	 * @author wangbs
	 * @date 2020/5/12
	 */
	String outStatisticalExcel(UserView userView, JSONObject viewAndTableData) throws GeneralException;
}
