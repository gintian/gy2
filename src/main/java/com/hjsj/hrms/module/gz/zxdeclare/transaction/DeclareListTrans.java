/**
 * FileName: DeclareListTrans
 * Author:   hssoft
 * Date:     2018/12/5 13:41
 * Description: 查询个税专项申报交易类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.zxdeclare.transaction;

import com.hjsj.hrms.module.gz.zxdeclare.businessobject.IDeclareService;
import com.hjsj.hrms.module.gz.zxdeclare.businessobject.impl.DeclareServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈类功能描述〉<br>
 * 〈查询个税专项申报交易类〉
 *
 * @author 徐长顺
 * @create 2018/12/5
 * @since 1.0.0
 */
public class DeclareListTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        try {
            String declare_type = (String) this.getFormHM().get("declareType");//专项附加类型
            String operate_type = (String) this.getFormHM().get("operateType");//操作类型
            String approve_state = (String) this.getFormHM().get("approveState");
            IDeclareService declareService = new DeclareServiceImpl(this.frameconn, this.userView);//业务操作类
            Map<String, Object> return_data = new HashMap<String, Object>(); //专项的信息map
            if (IDeclareService.C_OPERATE_TYPE_SEARCH.equals(operate_type)) {//查询操作
                String resFlag = (String) this.getFormHM().get("refsFlag");
                if ("true".equals(resFlag)) {//刷新表格数据而不是重新生成
                    declareService.refsDeclareList(declare_type, approve_state, this.userView);
                } else {
                    String tableconfig = declareService.searchDeclareList(declare_type, approve_state, this.userView);
                    return_data.put("tableconfig", tableconfig);
                }
            } else if (IDeclareService.C_OPERATE_TYPE_APPROVE.equals(operate_type)) {
                String ids = (String) this.getFormHM().get("ids");
                declareService.approveDeclares(ids);
            } else if (IDeclareService.C_OPERATE_TYPE_REJECT.equals(operate_type)) {
                String ids = (String) this.getFormHM().get("ids");
                String approveDesc = (String) this.formHM.get("approveDesc");
                Map param = new HashMap();
                param.put("approveDesc", approveDesc == null ? "" : approveDesc);
                declareService.rejectDeclares(ids, param);
            } else if (IDeclareService.C_OPERATE_TYPE_SAVE_RELATION.equals(operate_type)) {
                ArrayList fieldsList = (ArrayList) this.getFormHM().get("fieldsArray");
                String saveFlag = declareService.SaveRelation(fieldsList);
                return_data.put("saveFlag", saveFlag);
            } else if (IDeclareService.C_OPERATE_TYPE_GET_RELATION.equals(operate_type)) {
                String relation = declareService.getRelation();
                List fieldsetlist = ((DeclareServiceImpl) declareService).getPrivFieldSetList(this.getUserView());
                return_data.put("relation", relation);
                return_data.put("fieldsetlist", fieldsetlist);
            } else if (IDeclareService.C_OPERATE_TYPE_EXPORT_TEMPLATE_EXCEL.equals(operate_type)) {
                String fileid = (String) this.getFormHM().get("fileid");
                Map returnMap = declareService.exportTemplateExcel(this.getUserView(),fileid);
                return_data.put("exportTemplateExcelParam", returnMap);
            } else if (IDeclareService.C_OPERATE_TYPE_SAVE_TEMPLATE_FILE.equals(operate_type)) {
                String fileid = (String) this.getFormHM().get("fileid");
                String msg = declareService.saveTemplateFile(fileid);
                return_data.put("msg", msg);
            } else if (IDeclareService.C_OPERATE_TYPE_DELETE.equals(operate_type)) {
                String ids = (String) this.getFormHM().get("ids");
                declareService.deleteDeclares(ids);
            } else if (IDeclareService.C_OPERATE_TYPE_GET_Field.equals(operate_type)) {
                String fieldsetid = (String) this.getFormHM().get("fieldsetid");
                if (StringUtils.isEmpty(fieldsetid)) {
                    this.getFormHM().put("fieldList", new ArrayList());
                    return;
                }
                List fieldList = ((DeclareServiceImpl) declareService).getPricFieldByFieldSetId(this.getUserView(), fieldsetid);
                this.getFormHM().put("fieldList", fieldList);
            } else if (IDeclareService.C_OPERATE_TYPE_GET_Current_Index.equals(operate_type)) {
                String currentIndex = ((DeclareServiceImpl) declareService).getCurrentIndex();
                return_data.put("currentIndex", currentIndex);
            }else if(IDeclareService.C_OPERATE_TYPE_Check_File.equals(operate_type)){
                String isExitsFile = ((DeclareServiceImpl) declareService).isExitesTemplateFile();
                return_data.put("isExitsFile", isExitsFile);
            }
            this.getFormHM().put("return_data", return_data);
            this.getFormHM().put("return_code", "success");
        } catch (GeneralException e) {
            e.printStackTrace();
            this.getFormHM().put("return_code", "fail");
            this.getFormHM().put("return_msg", e.getErrorDescription());
        }
    }
}
