package com.hjsj.hrms.module.gz.analysistables.analysistable.transaction;

import com.hjsj.hrms.module.gz.analysistables.analysistable.businessobject.TableService;
import com.hjsj.hrms.module.gz.analysistables.analysistable.businessobject.impl.TableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OptAnalyseTableTrans extends IBusiness{
    @Override
    public void execute() throws GeneralException {
        String type = (String) this.getFormHM().get("transType");
        MorphDynaBean param = (MorphDynaBean) this.getFormHM().get("param");

        TableService tableserv = new TableServiceImpl(frameconn, userView);
        String querytype = (String) this.getFormHM().get("type");
        try {
            if(StringUtils.isNotBlank(querytype)){//页面模糊查询
                MorphDynaBean md=(MorphDynaBean)this.getFormHM().get("customParams");
                String imodule = (String) md.get("imodule");
                String queryText = "";
                TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("optsalaryset");
                if("1".equals(querytype)){//查询栏查询
                    List values = (ArrayList) this.getFormHM().get("inputValues");
                    for(int i=0;i<values.size();i++){
                        String value = SafeCode.decode(values.get(i).toString());
                        queryText+=value+",";
                    }
                }
                ArrayList salarydataList = tableserv.getSalarySetList(Integer.parseInt(imodule), queryText);
                tableCache.setTableData(salarydataList);
                this.userView.getHm().put("optsalaryset", tableCache);
                HashMap return_data = new HashMap();
                this.getFormHM().put("return_code", "success");
                this.getFormHM().put("return_msg", "");
                this.getFormHM().put("return_data", return_data);
                return;
            }else {
                if ("1".equals(type)) {
                    //1:初始化
                    JSONObject paramJson = JSONObject.fromObject(param);
                    int step = paramJson.getInt("step");
                    int imodule = paramJson.getInt("imodule");
                    int opt = paramJson.getInt("opt");
                    if (step == 1) {
                        //第一页
                        //				ArrayList salarytemplateList = gasu.getSalarySetList(imodule, "");
                        ArrayList salarytemplateList = tableserv.getReportCategoryList(imodule);
                        HashMap return_data = new HashMap();
                        return_data.put("salarytemplateList", salarytemplateList);
                        this.getFormHM().put("return_code", "success");
                        this.getFormHM().put("return_msg", "");
                        this.getFormHM().put("return_data", return_data);
                        return;
                    } else if (step == 2) {
                        //第二页
                        String rsid = paramJson.getString("rsid");
                        String rsdtlid = paramJson.containsKey("rsdtlid") ? paramJson.getString("rsdtlid") : "";

                        String name = "";
                        String checkednbase = "";
                        String salaryids = "";
                        String verifying = "";
                        //获取数据库中已存信息
                        if (opt != 1) {
                            //修改
                            LazyDynaBean bean = tableserv.getReportdetailBean(imodule, Integer.parseInt(rsid), Integer.parseInt(rsdtlid));
                            checkednbase = (String) bean.get("nbase");
                            name = (String) bean.get("name");
                            salaryids = (String) bean.get("salaryids");
                            verifying = (String) bean.get("verifying");
                        } else {
                            //新增
                        }
                        ArrayList nbases = this.getUserView().getPrivDbList();
                        ArrayList nbaseList = tableserv.getNbaseNameList(nbases);
                        nbaseList = tableserv.getNbaseCompList(nbaseList, checkednbase);
                        //表格控件数据
                        if (opt != 1) {
                            //修改
                            LazyDynaBean bean = tableserv.getReportdetailBean(imodule, Integer.parseInt(rsid), Integer.parseInt(rsdtlid));
                            salaryids = (String) bean.get("salaryids");
                        }

                        ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
                        columnList = tableserv.getSalarySetColumnsInfo(opt);
                        //得到人员表格button
                        ArrayList salarydataList = tableserv.getSalarySetList(imodule, "");
                        TableConfigBuilder builder = new TableConfigBuilder("optsalaryset", columnList, "optsalaryset1", userView, this.getFrameconn());
                        builder.setDataList(salarydataList);
                        builder.setSelectable(true);
                        builder.setPageSize(2000);
                        builder.setPageTool(false);
                        builder.setTdMaxHeight(70);
                        String config = builder.createExtTableConfig();
                        HashMap return_data = new HashMap();
                        return_data.put("tableConfig", config.toString());
                        return_data.put("name", name);
                        return_data.put("salaryids", salaryids);
                        //0:薪资 1：保险
                        boolean hasUnFinishPower = false;
                        //是否有包含审批过程的数据权限
                        if((imodule == 0&&userView.hasTheFunction("32407103")) || (imodule == 1&&userView.hasTheFunction("325040103"))) {
                        	hasUnFinishPower = true;
                        }
                        return_data.put("verifying", verifying);
                        return_data.put("hasUnFinishPower", hasUnFinishPower);
                        return_data.put("nbaseList", nbaseList);

                        this.getFormHM().put("return_code", "success");
                        this.getFormHM().put("return_msg", "");
                        this.getFormHM().put("return_data", return_data);
                        return;
                    } else if (step == 3) {
                        //第三页
                        String rsid = paramJson.getString("rsid");
                        String rsdtlid = paramJson.containsKey("rsdtlid") ? paramJson.getString("rsdtlid") : "";
                        String salaryIds = paramJson.containsKey("salaryIds") ? paramJson.getString("salaryIds") : "";

                        ArrayList salarytemplateList = tableserv.getSalarySetList(this.userView, imodule, "", salaryIds, 10, -1);
                        HashMap salarytemplateMap = new HashMap();
                        for (Object o : salarytemplateList) {
                            HashMap map = (HashMap) o;
                            salarytemplateMap.put(map.get("salaryid"), map.get("cname"));
                        }
                        ArrayList selectedItemList = new ArrayList();
                        if (opt != 1) {
                            //已选指标
                            selectedItemList = (ArrayList) tableserv.getReportItemlist(rsdtlid, userView).clone();
                        }
                        List<String> selected = new ArrayList();
                        for (int i = 0; i < selectedItemList.size(); i++) {
                            LazyDynaBean bean = (LazyDynaBean) selectedItemList.get(i);
                            String itemId = String.valueOf(bean.get("itemid"));
                            selected.add(itemId.toLowerCase());
                        }
                        ArrayList treeList = new ArrayList();
                        if (!"".equals(salaryIds)) {
                            String[] ids = salaryIds.split(",");
                            boolean expanded = true;
                            for (String id : ids) {
                                HashMap parentMap = new HashMap();
                                parentMap.put("text", salarytemplateMap.get(id));
                                parentMap.put("leaf", false);
                                parentMap.put("expanded", expanded);
                                //展开第一个
                                expanded = false;
                                ArrayList salaryitemlist = tableserv.getSalaryItemList(Integer.parseInt(id), "",rsid);
                                ArrayList childList = new ArrayList();
                                for (Object obj : salaryitemlist) {
                                    HashMap map = (HashMap) obj;
                                    String itemid = (String) map.get("itemid");
                                    if (selected.contains(itemid.toLowerCase())) {
                                        map.put("checked", true);
                                    } else {
                                        map.put("checked", false);
                                    }
                                    map.put("text", map.get("itemdesc"));
                                    map.put("leaf", true);
                                    map.put("itemid", itemid.toLowerCase());
                                    childList.add(map);
                                }
                                parentMap.put("children", childList);
                                treeList.add(parentMap);
                            }
                        }


                        HashMap return_data = new HashMap();
                        return_data.put("itemList", treeList);
                        return_data.put("selectedItemList", selectedItemList);


                        this.getFormHM().put("return_code", "success");
                        this.getFormHM().put("return_msg", "");
                        this.getFormHM().put("return_data", return_data);
                        return;
                    }
                } else if ("2".equals(type)) {
                    //保存
                    JSONObject paramJson = JSONObject.fromObject(param);
                    int opt = paramJson.getInt("opt");
                    boolean flag = false;
                    if (opt == 1 || opt == 4) {
                        //新增
                        int rsdtlid = tableserv.insertReportdetail(paramJson, this.userView);
                        HashMap return_data = new HashMap();

                        return_data.put("rsdtlid_enc", PubFunc.encrypt(rsdtlid+""));
                        return_data.put("rsid_enc", PubFunc.encrypt(paramJson.getString("rsid")));
                        if (rsdtlid>-1) {
                            this.getFormHM().put("return_code", "success");
                        } else {
                            this.getFormHM().put("return_code", "fail");
                        }
                        this.getFormHM().put("return_data",return_data);
                    } else if (opt == 2) {
                        try {
                            //修改
                            flag = tableserv.updateReportdetail(paramJson, this.userView);
                            String rsdtlid = paramJson.containsKey("rsdtlid") ? paramJson.getString("rsdtlid") : "";
                            String rsid = paramJson.containsKey("rsid") ? paramJson.getString("rsid") : "";
                            String items = paramJson.getString("items");
                            tableserv.deleteSelectItems(Integer.parseInt(rsdtlid));
                            if (StringUtils.isNotEmpty(items)) {
                                tableserv.insertSelectItems(items, Integer.parseInt(rsdtlid),rsid);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (flag) {
                            this.getFormHM().put("return_code", "success");
                        } else {
                            this.getFormHM().put("return_code", "fail");
                        }
                    } else if (opt == 3) {
                        flag = tableserv.updateReportdetail(paramJson, this.userView);
                        if (flag) {
                            this.getFormHM().put("return_code", "success");
                        } else {
                            this.getFormHM().put("return_code", "fail");
                        }
                    }

                    this.getFormHM().put("return_msg", "");
                } else if ("3".equals(type)) {
                    boolean flag = false;
                    //删除
                    JSONObject paramJson = JSONObject.fromObject(param);
                    int imodule = paramJson.getInt("imodule");
                    String rsid = paramJson.getString("rsid");
                    String rsdtlid = paramJson.containsKey("rsdtlid") ? paramJson.getString("rsdtlid") : "";
                    //删除明细
                    flag = tableserv.deleteReportdetail(imodule, rsid, rsdtlid);
                    if (flag) {
                        this.getFormHM().put("return_code", "success");
                    } else {
                        this.getFormHM().put("return_code", "fail");
                    }
                    this.getFormHM().put("return_msg", "");
                } else if ("4".equals(type)) {
                    //保存所属单位
                    JSONObject paramJson = JSONObject.fromObject(param);
                    String rsid = paramJson.getString("rsid");
                    String rsdtlid = paramJson.containsKey("rsdtlid") ? paramJson.getString("rsdtlid") : "";
                    String B0110 = paramJson.containsKey("B0110") ? paramJson.getString("B0110") : "";
                    String rawType = paramJson.containsKey("rawType") ? paramJson.getString("rawType") : "";
                    tableserv.saveBelongUnit(Integer.parseInt(rsid), Integer.parseInt(rsdtlid), B0110, rawType);
                }
            }
        }catch (Exception e){
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
 