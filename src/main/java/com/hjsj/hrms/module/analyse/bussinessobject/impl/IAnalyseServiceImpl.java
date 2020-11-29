package com.hjsj.hrms.module.analyse.bussinessobject.impl;

import com.hjsj.hrms.module.analyse.bussinessobject.IAnalyseService;
import com.hjsj.hrms.module.analyse.dao.IAnalyseDao;
import com.hjsj.hrms.module.analyse.dao.impl.IAnalyseDaoImpl;
import com.hjsj.hrms.taglib.general.HcmMenuTag;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title IAnalyseServiceImpl
 * @Description 工资分析业务接口实现类
 * @Company hjsj
 * @Author wangbs、caoqy
 * @Date 2019/12/19
 * @Version 1.0.0
 */
public class IAnalyseServiceImpl implements IAnalyseService {
    private Connection con = null;
    private ContentDAO dao = null;
    private UserView userView = null;
    /**数据层操作类**/
    private IAnalyseDao analyseDao;

    public IAnalyseServiceImpl(Connection con, UserView userView) {
        this.con = con;
        this.dao = new ContentDAO(con);
        this.userView = userView;
        this.analyseDao = new IAnalyseDaoImpl(con);
    }
    /**
     * 功能描述: 获取工资分析首界面加载数据
     * @author: caoqy
     * @param menuid:
     * @return: java.util.List
     * @date: 2019-12-19 15:47
     */
    @Override
    public List getAnalyseMainData(String menuid) throws GeneralException {
        ArrayList list = new ArrayList();
        Map map = null;

        List<Map> othersItemList = null;//其他菜单项list
        //二级菜单list
        List<Element> menuElementList = this.getMenuElements(menuid);
        for (Element el : menuElementList) {
        	String func_id = el.getAttributeValue("func_id");
        	if(func_id == null || !this.userView.hasTheFunction(func_id))
        		continue;
            List<Map> secondMenuList = new ArrayList();//二级菜单list
            map = new HashMap();
            Map itemMap = null;
            //三级菜单list
            List<Element> thirdMenuList = el.getChildren();//三级菜单list
			
            if (thirdMenuList != null && thirdMenuList.size() != 0) {//为二级菜单分类
                String categoryName = el.getAttributeValue("name");//二级菜单名
                map.put("categoryName", categoryName);
                for (Element thirdMenuEl : thirdMenuList) {
                    itemMap = new HashMap();
                    String child_func_id = thirdMenuEl.getAttributeValue("func_id");
    				if(child_func_id == null || !this.userView.hasTheFunction(child_func_id))
    					continue;
    				String url = thirdMenuEl.getAttributeValue("url");
    				if (StringUtils.isBlank(url)) {
    					continue;
    				}
    				getUrlParams(itemMap, url);
    				String unit = thirdMenuEl.getAttributeValue("unit");//计量单位 例如：万元、元、个数...
    				((HashMap)itemMap.get("param")).put("unit", unit);
                    String name = thirdMenuEl.getAttributeValue("name");//三级菜单名
                    itemMap.put("name", name);
                    String icon = thirdMenuEl.getAttributeValue("icon");//三级菜单名
                    itemMap.put("photo", icon);
                    secondMenuList.add(itemMap);
                }
            } else {//没有三级菜单为空分类，或独为三级菜单无二级分类
                String urlAttributeValue = el.getAttributeValue("url");
                if (StringUtils.isBlank(urlAttributeValue)) {
                    continue;
                }
                itemMap = new HashMap();
                String name = el.getAttributeValue("name");
                itemMap.put("name", name);
                getUrlParams(itemMap, urlAttributeValue);
                String unit = el.getAttributeValue("unit");//计量单位 例如：万元、元、个数...
                ((HashMap)itemMap.get("param")).put("unit", unit);
                String icon = el.getAttributeValue("icon");//三级菜单名
                itemMap.put("photo", icon);
                othersItemList.add(itemMap);
                map.put("categoryName", "others");//未分类项
            }
            map.put("items", secondMenuList);
            list.add(map);
        }
        return list;
    }

    /**
     * 功能描述: 获取url参数
     *
     * @param itemMap:
     * @param url:
     * @author: caoqy
     * @return: void
     * @date: 2019-12-19 11:28
     */
    private void getUrlParams(Map itemMap, String url) {
        //解析url参数
        try {
            url = url.replace("&amp;", "&");
            Map<String, String> paramsMap = getURLRequest(url);
            Map thirdParamsMap = new HashMap();
            for (String tempKey : paramsMap.keySet()) {
                String value = StringUtils.isBlank(paramsMap.get(tempKey)) ? "" : paramsMap.get(tempKey);
                if (StringUtils.equalsIgnoreCase("b0110", tempKey)) {
                    value = PubFunc.encrypt(value);
                }
                thirdParamsMap.put(tempKey, value);
            }
            itemMap.put("param", thirdParamsMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim().toLowerCase();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }

        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public Map<String, String> getURLRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit = null;

        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (!"".equals(arrSplitEqual[0])) {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }


    /**
     * 功能描述: 获取子菜单list
     *
     * @param menuid:
     * @author: caoqy
     * @return: java.util.List<org.jdom.Element>
     * @date: 2019-12-18 15:28
     */
    private List<Element> getMenuElements(String menuid) {
        List<Element> secondMenuList = null;
        Document doc = null;
        try {
            doc = this.getDocument();
            org.jdom.xpath.XPath xPath = org.jdom.xpath.XPath.newInstance("/hrp_menu/menu[@id='60']");//60：H5移动服务
            Element ele = (Element) xPath.selectSingleNode(doc);
            if (ele == null) {
                throw new GeneralException("请正确配置菜单配置项");
            }
            List<Element> list = ele.getChildren();//移动服务 功能菜单
            for (Element tempEl : list) {
                String tempMenuId = tempEl.getAttributeValue("menuid");
                if (StringUtils.isNotBlank(tempMenuId) && StringUtils.equalsIgnoreCase(tempMenuId, menuid)) {
                    secondMenuList = tempEl.getChildren();
                    break;
                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        return secondMenuList;
    }

    /**
     * 功能描述: 获取menu.xml的doc对象
     *
     * @author: caoqy
     * @return: org.jdom.Document
     * @date: 2019-12-18 15:08
     */
    private Document getDocument() throws GeneralException {
        Document doc = null;
        HcmMenuTag mm = new HcmMenuTag();
        try {
            Method getDoc = mm.getClass().getDeclaredMethod("getDocument");
            getDoc.setAccessible(true);
            doc = (Document) getDoc.invoke(mm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    public Map getPingJunViewData(String viewTable, String b0110, String year,String unit) throws GeneralException {
        Map returnData = new HashMap();

        Map tableData = new HashMap();
        Map hData = new HashMap();
        List hitems = new ArrayList();
        Map vData = new HashMap();
        List vitems = new ArrayList();
        List detailData = new ArrayList();
        try {
            //页面右上角年份显示
            List yearList = this.analyseDao.listViewYear(viewTable, b0110);
            //如果没有年list则说明没有数据，应直接返回空chartData，用于前台判断数据为空
            if (yearList.size() == 0) {
                returnData.put("chartData", new ArrayList());
                return returnData;
            }

            if (StringUtils.isBlank(year)) {
                year = (String) yearList.get(0);
            }
            //当前显示的年份列表
            returnData.put("yearList", yearList);
            //机构列表
            List orgList = this.analyseDao.listOrgData(viewTable, b0110);
            returnData.put("orgList", orgList);

            //统计图数据
            List<Map> chartData = this.analyseDao.listPingJunViewData(viewTable, b0110, year);
            for (Map oneChartData : chartData) {
                List dataList = (ArrayList) oneChartData.get("dataList");
                String itemname = (String) oneChartData.get("itemname");
                vitems.add(createNameMap(itemname));

                //只有当前年数据没有上一年数据，则需添加上年的假数据
                if (dataList.size() == 1) {
                    Map oneData = (HashMap) dataList.get(0);
                    String name = (String) oneData.get("name");
                    String lastYear = String.valueOf(Integer.parseInt(name) - 1);
                    double value = Double.valueOf(String.valueOf(oneData.get("value")));

                    oneData.put("name", name+ResourceFactory.getProperty("datestyle.year"));
                    //上一年的数据
                    oneData = createNameMap(lastYear+ResourceFactory.getProperty("datestyle.year"));
                    oneData.put("value", 0);
                    dataList.add(0, oneData);

                    //增幅
                    oneData = createNameMap("{zf}");
//                    if (value > 0) {
//                        oneData.put("value", 100);
//                    } else {
//                        oneData.put("value", 0);
//                    }
                    oneData.put("value", 0.00);
                    dataList.add(oneData);
                } else {
                    //添加增幅数据
                    double tempValue = 0;
                    double percentValue = 0;
                    for (int i = 0; i < dataList.size(); i++) {
                        Map oneData = (HashMap) dataList.get(i);
                        oneData.put("name",oneData.get("name")+ResourceFactory.getProperty("datestyle.year")+unit);
                        double value = Double.valueOf(String.valueOf(oneData.get("value")));

                        if (i == 1) {
                            if (tempValue == 0 && value != 0) {
                                percentValue = 0;
                            } else if (tempValue != 0) {
                                percentValue = 100 * (value - tempValue) / tempValue;
                            }
                        }
                        tempValue = value;
                    }
                    //增幅
                    Map oneData = createNameMap("{zf}");
                    oneData.put("value", toFixedNum(percentValue));
                    dataList.add(oneData);
                }
            }

            List preDetailData = new ArrayList();
            List curDetailData = new ArrayList();
            List percentDetailData = new ArrayList();
            for (int i = 0; i < chartData.size(); i++) {
                List dataList = (ArrayList) chartData.get(i).get("dataList");
                for (int j = 0; j < dataList.size(); j++) {
                    Map oneData = (HashMap) dataList.get(j);
                    double value = Double.valueOf(String.valueOf(oneData.get("value")));

                    if (i == 0) {
                        hitems.add(createNameMap((String) oneData.get("name")));
                    }

                    String valueStr = "";
                    boolean valueFlag = value == 100 || value == 0;
                    if (valueFlag && j == 2) {
                        valueStr = String.valueOf(new BigDecimal(value).setScale(0, BigDecimal.ROUND_HALF_DOWN));
                    } else {
                        valueStr = String.valueOf(toFixedNum(value));
                    }

                    if (j == 0) {
                        preDetailData.add(valueStr);
                    } else if (j == 1) {
                        curDetailData.add(valueStr);
                    } else if (j == 2) {
                        percentDetailData.add(valueStr + "%");
                    }
                }
            }

            detailData.add(preDetailData);
            detailData.add(curDetailData);
            detailData.add(percentDetailData);

            hData.put("items", hitems);
            vData.put("items", vitems);
            tableData.put("h", hData);
            tableData.put("v", vData);
            tableData.put("data", detailData);

            returnData.put("chartData", chartData);
            returnData.put("tableData", tableData);
        } catch (Exception e) {
            e.printStackTrace();
            //'获取平均统计数据出错！'
            throw new GeneralException("analyse.chart.getPjDataError");
        }
        return returnData;
    }

    /**
     * 保留两位小数
     * @author wangbs
     * @param percentValue 需保留的值
     * @return double
     * @date 2019/12/23 15:27
     */
    private BigDecimal toFixedNum(double percentValue){
        BigDecimal bg = new BigDecimal(percentValue);
        BigDecimal newPercentValue = bg.setScale(2, BigDecimal.ROUND_HALF_UP);
        return newPercentValue;
    }

    @Override
    public Map getLeiJiViewData(String viewTable, String b0110, String year,String unit) throws GeneralException {
        Map returnData = new HashMap();
        try {
            //页面右上角年份显示
            List yearList = this.analyseDao.listViewYear(viewTable, b0110);
            //如果没有年list则说明没有数据，应直接返回空chartData，用于前台判断数据为空
            if (yearList.size() == 0) {
                returnData.put("chartData", new ArrayList());
                return returnData;
            }

            if (StringUtils.isBlank(year)) {
                year = (String) yearList.get(0);
            }
            //当前显示的年份列表
            returnData.put("yearList", yearList);

            //机构列表
            List orgList = this.analyseDao.listOrgData(viewTable, b0110);
            returnData.put("orgList", orgList);

            Map chartDataMap = this.analyseDao.listLeiJiViewData(viewTable, b0110, year);
            double sum = (Double) chartDataMap.get("sum");
            returnData.put("num", String.valueOf(toFixedNum(sum)));

            List chartData = (ArrayList) chartDataMap.get("data");
            for (int i = 0; i < chartData.size(); i++) {
                Map oneData = (HashMap) chartData.get(i);
                double value = Double.valueOf(String.valueOf(oneData.get("value")));
                double percent = 0;
                if (sum != 0) {
                    percent = 100 * value / sum;
                }

                oneData.put("value", String.valueOf(toFixedNum(value)));
                oneData.put("percent", String.valueOf(toFixedNum(percent)));
            }

            returnData.put("chartData", chartData);
        } catch (Exception e) {
            e.printStackTrace();
            //'获取累计统计数据出错！'
            throw new GeneralException("analyse.chart.getljDataError");
        }
        return returnData;
    }

    @Override
    public Map getMoreItemAndTypeViewData(String viewTable, String b0110, String year,String unit) throws GeneralException {
        Map returnData = new HashMap();

        Map tableData = new HashMap();
        Map hData = new HashMap();
        List hitems = new ArrayList();
        Map vData = new HashMap();
        List vitems = new ArrayList();
        List<List> detailData = new ArrayList();
        try {
            //页面右上角年份显示
            List yearList = this.analyseDao.listViewYear(viewTable, b0110);
            //如果没有年list则说明没有数据，应直接返回空chartData，用于前台判断数据为空
            if (yearList.size() == 0) {
                returnData.put("chartData", new ArrayList());
                return returnData;
            }

            if (StringUtils.isBlank(year)) {
                year = (String) yearList.get(0);
            }
            //当前显示的年份列表
            returnData.put("yearList", yearList);

            //机构列表
            List orgList = this.analyseDao.listOrgData(viewTable, b0110);
            returnData.put("orgList", orgList);

            List<Map> chartData = this.analyseDao.listMoreItemAndMoreTypeViewData(viewTable, b0110, year);
            for (int i = 0; i < chartData.size(); i++) {
                List dataList = (ArrayList) chartData.get(i).get("dataList");
                String itemname = (String) chartData.get(i).get("itemname");
                vitems.add(createNameMap(itemname));
                if (i == chartData.size() - 1) {
                    vitems.add(createNameMap("{hj}"));
                }

                BigDecimal hTotal = new BigDecimal("0.00");
                for (int j = 0; j < dataList.size(); j++) {
                    Map oneData = (HashMap) dataList.get(j);
                    BigDecimal value = (BigDecimal) oneData.get("value");
                    hTotal = hTotal.add(value);
                    if (i == 0) {
                        //添加横向title
                        hitems.add(createNameMap((String) oneData.get("name")+unit));
                        if (j == dataList.size() - 1) {
                            hitems.add(createNameMap("{hj}"));
                        }

                        //添加数据
                        List oneDataList = new ArrayList();
                        oneDataList.add(value);
                        detailData.add(oneDataList);
                        //添加行合计数据
                        if (j == dataList.size() - 1) {
                            oneDataList = new ArrayList();
                            oneDataList.add(hTotal);
                            detailData.add(oneDataList);
                        }
                    } else {
                        List oneDataList = detailData.get(j);
                        oneDataList.add(value);
                        if (j == dataList.size() - 1) {
                            oneDataList = detailData.get(j + 1);
                            oneDataList.add(hTotal);
                        }
                    }
                }
            }

            //计算列合计
            for (List<Object> oneDataList : detailData) {
                BigDecimal vTotal = new BigDecimal("0.00");
                for (int i = 0; i < oneDataList.size(); i++) {
                    BigDecimal oneData = (BigDecimal) oneDataList.get(i);
                    vTotal = vTotal.add(oneData);
                    oneDataList.set(i, String.valueOf(oneData.setScale(2, BigDecimal.ROUND_HALF_UP)));
                }
                oneDataList.add(String.valueOf(vTotal.setScale(2, BigDecimal.ROUND_HALF_UP)));
            }

            hData.put("items", hitems);
            vData.put("items", vitems);
            tableData.put("h", hData);
            tableData.put("v", vData);
            tableData.put("data", detailData);
            returnData.put("chartData", chartData);
            returnData.put("tableData", tableData);
        } catch (Exception e) {
            e.printStackTrace();
            //'获取多项目多分类统计数据出错！'
            throw new GeneralException("analyse.chart.getMoreDataError");
        }
        return returnData;
    }

    /**
     * 代码复用 创建nameMap
     * @author wangbs
     * @param name 值
     * @return Map
     * @date 2019/12/24 11:48
     */
    private Map createNameMap(String name) {
        Map nameMap = new HashMap();
        nameMap.put("name", name);
        return nameMap;
    }

    @Override
    public Map<String, Object> getZhanBiViewData(String viewTable, String items, String b0110, String year, String unit) throws GeneralException {
        Map<String, Object> map = new HashMap();
        try {
        	List yearList = this.analyseDao.listViewYear(viewTable, b0110);
        	if(yearList.size() == 0){
        		map.put("chartData", new ArrayList());
        		return map;
        	}
        	if (StringUtils.isBlank(year)) {
        		year = (String) yearList.get(0);
        	}
            List<Map> list = this.analyseDao.listZhanBiViewData(viewTable, items, b0110, year);
            map = this.transZhanBiDataType(list,b0110,items,unit);
            map.put("yearList", yearList);
        } catch (Exception e) {
            e.printStackTrace();
            //'获取执行工资总额占比统计数据出错！'
            throw new GeneralException("analyse.chart.getZhanBiDataError");//TODO 资源文件待写
        }
        return map;
    }

    @Override
    public Map<String, Object> getTongBiViewData(String viewTable, String items, String b0110, String year, String unit) throws GeneralException {
        Map<String, Object> map = null;
        try {
        	List yearList = this.analyseDao.listViewYear(viewTable, b0110);
        	if(yearList.size() == 0){
        		map.put("chartData", new ArrayList());
        		return map;
        	}
        	if (StringUtils.isBlank(year)) {
        		year = (String) yearList.get(0);
        	}
            List<Map> list = this.analyseDao.listTongBiViewData(viewTable, items, b0110, year);
            map = this.transTongBiDataType(list,b0110,items,yearList,unit);
            map.put("yearList", yearList);
        } catch (Exception e) {
            e.printStackTrace();
            //'获取执行工资总额占比统计数据出错！'
            throw new GeneralException("analyse.chart.getTongbiDataError");//TODO 资源文件待写
        }
        return map;
    }

    private Map<String, Object> transTongBiDataType(List<Map> list, String b0110, String items, List yearList, String unit) {
        Map<String, Object> map = new HashMap<String, Object>();
        String nowYear = "";
        String lastYear = "";
        List<Map> chartDataList = new ArrayList<Map>();//统计图数据
        Map<String, Object> tableDataMap = new HashMap<String, Object>();//表数据
        List vList = new ArrayList();
        Map<String,List> vItemsHM = new HashMap<String,List>();
        double sumLastValue = 0;
        double sumNowValue = 0;
        String[][] datas = null;
        if(list.size() > 1){//需要添加一行合计
        	datas = new String[3][list.size()+1];
        }else{
        	datas = new String[3][list.size()];
        }
        for (int i = 0;i<list.size(); i++) {
            Map tempMap = list.get(i);
            //封装统计图数据
            Map map1 = new HashMap();
            String  itemname = (String) tempMap.get("itemname");
            map1.put("itemname", itemname);

            ArrayList yearDataList = (ArrayList) tempMap.get("dataList");
            HashMap lastYearMap = new HashMap();
            HashMap nowYearMap = null;
            if(yearDataList.size() == 1){
            	nowYearMap = (HashMap) yearDataList.get(0);
            	lastYearMap.put("year", Integer.parseInt((String)nowYearMap.get("year")) -1 +"");
            	lastYearMap.put("value", 0.00);
            }else{
            	lastYearMap = (HashMap) yearDataList.get(0);
            	nowYearMap = (HashMap) yearDataList.get(1);
            }
//            Double nowValue = (Double) tempMap.get(nowYear);
//            Double lastValue = (Double) tempMap.get(lastYear);
            nowYear = (String) nowYearMap.get("year");
            lastYear = (String) lastYearMap.get("year");
            double nowValue = (Double) nowYearMap.get(items);
            double lastValue = (Double) lastYearMap.get(items);
            ArrayList dataList = new ArrayList();

            HashMap parentHM = new HashMap();
            parentHM.put("name",lastYear+ResourceFactory.getProperty("datestyle.year"));
            parentHM.put("value",new BigDecimal(lastValue).setScale(2, BigDecimal.ROUND_HALF_UP));

            HashMap childHM = new HashMap();
            childHM.put("name",nowYear+ResourceFactory.getProperty("datestyle.year"));
            childHM.put("value",new BigDecimal(nowValue).setScale(2, BigDecimal.ROUND_HALF_UP));

            HashMap percentHM = new HashMap();
            percentHM.put("name",ResourceFactory.getProperty("analyse.chart.tbzf"));
            if(lastValue == 0){
                percentHM.put("value",0.00);
            }else{
                percentHM.put("value", new BigDecimal((nowValue - lastValue) / lastValue*100).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            dataList.add(childHM);
            dataList.add(parentHM);
            dataList.add(percentHM);
            map1.put("dataList", dataList);
            chartDataList.add(map1);

            //机构
            HashMap orgHM = new HashMap();
            orgHM.put("name",itemname);
            vList.add(orgHM);

            sumLastValue += lastValue;
            sumNowValue += nowValue;
            
            datas[0][i]=String.valueOf(new BigDecimal(lastValue).setScale(2, BigDecimal.ROUND_HALF_UP));
            datas[1][i]=String.valueOf(new BigDecimal(nowValue).setScale(2, BigDecimal.ROUND_HALF_UP));
            if(lastValue == 0){
                datas[2][i] = "0.00%";
            }else{
                datas[2][i] = String.valueOf(new BigDecimal((nowValue - lastValue) / lastValue*100).setScale(2, BigDecimal.ROUND_HALF_UP)+"%");
            }
        }
        if(list.size() > 1){
        	datas[0][list.size()]= String.valueOf(new BigDecimal(sumLastValue).setScale(2, BigDecimal.ROUND_HALF_UP));
        	datas[1][list.size()]= String.valueOf(new BigDecimal(sumNowValue).setScale(2, BigDecimal.ROUND_HALF_UP));
        	if(sumLastValue == 0){
        		datas[2][list.size()] = "0.00%";
        	}else{
        		datas[2][list.size()]= String.valueOf(new BigDecimal((sumNowValue - sumLastValue) / sumLastValue*100).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
        	}
        }
        
        //封装表数据
        Map itemsMap = new HashMap();
        List itemsList = new ArrayList();

        Map parentItemHM = new HashMap();
        parentItemHM.put("name",lastYear+ResourceFactory.getProperty("datestyle.year")+unit);
        itemsList.add(parentItemHM);

        Map childItemHM = new HashMap();
        childItemHM.put("name",nowYear+ResourceFactory.getProperty("datestyle.year")+unit);
        itemsList.add(childItemHM);

        Map percentItemHM = new HashMap();
        percentItemHM.put("name",ResourceFactory.getProperty("analyse.chart.tbzf"));//同比增幅
        itemsList.add(percentItemHM);
        itemsMap.put("items", itemsList);
        
        if(list.size() > 1){
        	//合计
        	HashMap orgHM = new HashMap();
        	orgHM.put("name",ResourceFactory.getProperty("analyse.chart.total"));//合计
        	vList.add(orgHM);
        }
        
        vItemsHM.put("items",vList);
        tableDataMap.put("h",itemsMap);
        tableDataMap.put("v",vItemsHM);
        tableDataMap.put("data",datas);
        map.put("chartData",chartDataList);
        map.put("tableData",tableDataMap);

        return map;
    }

    /**
     * 功能描述: 封装占比数据
     * @author: caoqy
     * @param list :
     * @param b0110
     * @param items
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @date: 2019-12-24 13:49
     */
    private Map<String, Object> transZhanBiDataType(List<Map> list, String b0110,  String items , String unit) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] item = items.split(",");
        String childField = item[0];//分子
        String parentField = item[1];//分母
        List<Map> chartDataList = new ArrayList<Map>();//统计图数据
        Map<String, Object> tableDataMap = new HashMap<String, Object>();//表数据
        List vList = new ArrayList();
        Map<String,List> vItemsHM = new HashMap<String,List>();
        double sumChildValue = 0;
        double sumParentValue = 0;
        String[][] datas = null;
        if(list.size() > 1){//需要添加一行合计
        	datas = new String[3][list.size()+1];
        }else{
        	datas = new String[3][list.size()];
        }
        for (int i = 0;i<list.size(); i++) {
            Map tempMap = list.get(i);
            //封装统计图数据
            Map map1 = new HashMap();
            String  itemname = (String) tempMap.get("itemname");
            map1.put("itemname", itemname);

            BigDecimal childValue = (BigDecimal) tempMap.get(childField);
            BigDecimal parentValue = (BigDecimal) tempMap.get(parentField);
            ArrayList dataList = new ArrayList();

            HashMap childHM = new HashMap();
            childHM.put("name",childField);
            childHM.put("value",childValue);

            HashMap parentHM = new HashMap();
            parentHM.put("name",parentField);
            parentHM.put("value",parentValue);

            HashMap percentHM = new HashMap();
            percentHM.put("name",ResourceFactory.getProperty("analyse.chart.zxbl"));
            if(parentValue.doubleValue() == 0){
                percentHM.put("value","0.00");
            }else{
                percentHM.put("value", new BigDecimal((childValue.doubleValue() / parentValue.doubleValue() * 100)).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            dataList.add(childHM);
            dataList.add(parentHM);
            dataList.add(percentHM);
            map1.put("dataList", dataList);
            chartDataList.add(map1);

            //机构
            HashMap orgHM = new HashMap();
            orgHM.put("name",itemname);
            vList.add(orgHM);
            
            sumChildValue += childValue.doubleValue();
            sumParentValue += parentValue.doubleValue();
            datas[0][i]=String.valueOf(childValue);
            datas[1][i]=String.valueOf(parentValue);
            datas[2][i] = String.valueOf(new BigDecimal((childValue.doubleValue() / parentValue.doubleValue() * 100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
        }
        if(list.size() > 1 && sumParentValue > 0){
        	datas[0][list.size()]= String.valueOf(new BigDecimal(sumChildValue).setScale(2, BigDecimal.ROUND_HALF_UP));
        	datas[1][list.size()]= String.valueOf(new BigDecimal(sumParentValue).setScale(2, BigDecimal.ROUND_HALF_UP));
        	datas[2][list.size()]= String.valueOf(new BigDecimal((sumChildValue / sumParentValue * 100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
        }
        //封装表数据
        Map itemsMap = new HashMap();
        List itemsList = new ArrayList();
        Map childItemHM = new HashMap();
        childItemHM.put("name",childField+unit);
        itemsList.add(childItemHM);

        Map parentItemHM = new HashMap();
        parentItemHM.put("name",parentField+unit);
        itemsList.add(parentItemHM);

        Map percentItemHM = new HashMap();
        percentItemHM.put("name",ResourceFactory.getProperty("analyse.chart.zxbl"));//执行比例
        itemsList.add(percentItemHM);
        itemsMap.put("items", itemsList);

        if(list.size() > 1){
        	//合计
        	HashMap orgHM = new HashMap();
        	orgHM.put("name",ResourceFactory.getProperty("analyse.chart.total"));//合计
        	vList.add(orgHM);
        }
        
        vItemsHM.put("items",vList);
        tableDataMap.put("h",itemsMap);
        tableDataMap.put("v",vItemsHM);
        tableDataMap.put("data",datas);
        map.put("chartData",chartDataList);
        map.put("tableData",tableDataMap);

        return map;
    }
}
