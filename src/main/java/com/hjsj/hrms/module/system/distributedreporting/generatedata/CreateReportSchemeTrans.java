package com.hjsj.hrms.module.system.distributedreporting.generatedata;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: CreateReportSchemeTrans
 * @Description: 生成上报数据页面创建方案相关操作
 * @Company:hjsj
 * @Create time: 2019/5/28 14:13:27
 * @author: wangbs
 * @version: 1.0
 */
public class CreateReportSchemeTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        try {
            String operaType = (String) this.formHM.get("operaType");
            GenerateDataBo gdBo = new GenerateDataBo(this.userView, this.frameconn);

            if ("saveFilePath".equalsIgnoreCase(operaType)) {
                String savePath = (String) this.formHM.get("savePath");
                String saveFilePathMsg = FileUtil.saveFilePath(savePath, this.frameconn);
                //路径不合规范
                if (!StringUtils.equalsIgnoreCase("success", saveFilePathMsg)) {
                    this.formHM.put("saveFilePathMsg", saveFilePathMsg);
                }
            }else if ("receiveScheme".equals(operaType)) {
                //接收方案（读取导入的zip数据包）
                List fileList = new ArrayList();
                String saveFilePath = FileUtil.getSaveFilePath();
                if (StringUtils.isBlank(saveFilePath)) {
                    fileList.add("pathBlank");
                } else {
                    Map fileHM = PubFunc.DynaBean2Map((MorphDynaBean)(this.formHM.get("file")));
                    //真实名称
                    String localName = (String) fileHM.get("localname");
                    String fileid = (String) fileHM.get("fileid");
                    if (StringUtils.isBlank(fileid)) {
                        fileList.add("fileerror");
                    }else{
                        fileList = gdBo.checkAndReadFile(fileid,localName);
                        String return_code = (String) fileList.get(0);
                        if (return_code.startsWith("success")) {
                            //保存menus.json内容
                            gdBo.saveMenusJson(fileList);
                        }
                    }
                }
                this.formHM.put("filelist", fileList);

            }else if ("package".equals(operaType)) {
                //下载数据包
                String idString  = (String) this.formHM.get("id");
                //t_sys_asyn_sendinfo 根据id 获取压缩包的路径
                ConstantXml constantXml = new ConstantXml(this.frameconn, "FBTB_FILEPATH");
                String path = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+gdBo.getPackagePath(idString);
                File zipfile = new File(path);
                if (zipfile.exists()) {
                    //下载到本地客户端
                    FileUtil.copyFile(path, System.getProperty("java.io.tmpdir"),null);
                    this.formHM.put("fileName",PubFunc.encrypt(zipfile.getName()));
                }else {
                    this.formHM.put("fileName",false);
                }
                
            }else if("saveSchemeInfo".equals(operaType)){
                String filePath = (String) this.formHM.get("filePath");
                if (StringUtils.isNotBlank(filePath)) {
                    FileUtil.deleteFile(filePath);
                }
                //保存 BS_ASYN_PARAM_C 信息
                Map schemeInfoMap = PubFunc.DynaBean2Map((MorphDynaBean) this.formHM.get("schemeInfo"));
                gdBo.saveSchemeInfo(schemeInfoMap);

            }else if("saveMatchSet".equals(operaType)){
                //保存信息集对应关系
                List matchSetList = (ArrayList) this.formHM.get("matchSetArr");
                gdBo.saveMatchSet(matchSetList);

            }else if("saveFieldMatch".equals(operaType)){
                //保存指标对应关系
                List matchFieldList = (ArrayList) this.formHM.get("matchFieldArr");
                String set1 = (String) this.formHM.get("set1");
                gdBo.saveMatchField(matchFieldList, set1);

            }else if("saveCodeItemMatch".equals(operaType)){
                //保存代码型指标的代码项对应关系
                List codeItemMatchList = (ArrayList) this.formHM.get("codeItemMatchArr");
                String fieldItem1 = (String) this.formHM.get("fieldItem1");
                gdBo.saveCodeItemMatch(codeItemMatchList, fieldItem1);

            }else if("saveNbaseMatch".equals(operaType)){
                //保存上报人员库
                String nbase = (String) this.formHM.get("nbase");
                gdBo.saveNbaseMatch(nbase);

            }else if("searchFirstData".equals(operaType)){
                //查询第一页的数据
                Map schemeInfo = gdBo.selectSchemeInfo();
                String filePath = FileUtil.getSaveFilePath();
                this.formHM.put("schemeInfo", schemeInfo);
                this.formHM.put("filePath", filePath);

            }else if("searchSecondData".equals(operaType)){
                //查询第二页的数据
                Map returnMap = new HashMap();

                String menusInfo = gdBo.getMenusInfo();
                gdBo.changeOldData(menusInfo,"second");

                List fieldSetList = gdBo.getFieldSetList();
                List setMatchList = gdBo.getSetMatchList();

                returnMap.put("fieldSetList", fieldSetList);
                returnMap.put("setMatchList", setMatchList);
                returnMap.put("menusInfo", menusInfo);
                this.formHM.put("returnMap", returnMap);

            }else if("searchThirdData".equals(operaType)){
                //查询第三页的数据
                String menusInfo = (String) this.formHM.get("menusInfo");
                Map setMatchMap = PubFunc.DynaBean2Map((MorphDynaBean) this.formHM.get("setMatchObj"));

                gdBo.changeOldData(menusInfo,"third");
                Map returnMap = gdBo.getFieldInfoMap(setMatchMap);
                this.formHM.put("returnMap", returnMap);

            } else if ("getSetMatchList".equals(operaType)) {
                List setMatchList = gdBo.getSetMatchList();
                this.formHM.put("setMatchList", setMatchList);

            } else if ("searchForthData".equals(operaType)) {
                //查询第四页的数据
                Map returnMap = new HashMap();
                String codeItemInfo = gdBo.getCodeItemInfo();
                gdBo.changeOldData(codeItemInfo, "forth");

                Map codeFieldInfoMap = gdBo.getCodeFieldInfoMap();
                returnMap.put("codeFieldInfoMap", codeFieldInfoMap);
                returnMap.put("codeItemInfo", codeItemInfo);
                this.formHM.put("returnMap", returnMap);

            } else if ("searchFifthData".equals(operaType)) {
                //查询第五页的数据
                Map returnMap = gdBo.getDbInfoMap();
                this.formHM.put("returnMap", returnMap);

            } else if ("handleSixthData".equals(operaType)) {
                //处理最后一步数据
                String menusInfo = (String) this.formHM.get("menusInfo");
                List verifyList = (ArrayList) this.formHM.get("verifyList");
                gdBo.changeOldData(menusInfo, "sixth");
                gdBo.saveVerifyRules(verifyList);

            }else if ("deleteSetAssociatedData".equals(operaType)) {
                //删除因取消匹配信息集造成的指标、代码对应表中的脏数据
                String set1 = (String) this.formHM.get("set1");
                gdBo.deleteSetAssociatedData(set1);

            }else if ("deleteFieldAssociatedData".equals(operaType)) {
                //删除因取消匹配指标造成的代码对应表中的脏数据
                List field1List = (ArrayList) this.formHM.get("field1List");
                gdBo.deleteFieldAssociatedData(field1List);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
