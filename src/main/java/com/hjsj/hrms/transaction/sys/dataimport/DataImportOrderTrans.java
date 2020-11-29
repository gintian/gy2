/**
 * 
 */
package com.hjsj.hrms.transaction.sys.dataimport;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.dataimport.DataImportBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Element;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:AddDataImportLinkTrans
 * </p>
 * <p>
 * Description:添加或修改参数
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-06-29
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class DataImportOrderTrans extends IBusiness {

    public void execute() throws GeneralException
    {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String id = hm.get("id").toString();
        
        if (id == null || "".equals(id))
            return;         
        
        String opt = hm.get("opt").toString();
        if(opt == null|| opt.length() <= 0)
            return;
        
        changeOrder(opt, id);
    }
    
    public void changeOrder(String opt, String id) 
    {   
        try
        {
            // 获取所有参数
            ConstantXml constantXml = new ConstantXml(this.frameconn, "IMPORTINFO", "params");
            //强制按id排序
            DataImportBo bo = new DataImportBo();
            bo.orderParamNodeById(constantXml);
            
            List list = constantXml.getAllChildren("/params");   
            
            
            
            for (int i = 0; i < list.size(); i++)
            {
                Element el = (Element)list.get(i);
                String schemaId = el.getAttributeValue("id");
                
                if (!schemaId.equals(id))
                    continue;
                
                Element el2 = null;
                if ("up".equals(opt) && i != 0)
                {
                    el2 = (Element)list.get(i - 1);
                }
                else if ("down".equals(opt) && i != (list.size() - 1))
                {
                    el2 = (Element)list.get(i + 1);
                }
                
                if (null != el2)
                {
                    String id2 = el2.getAttributeValue("id");
                    el.setAttribute("id", id2);
                    el2.setAttribute("id", schemaId);
                    
                    bo.orderParamNodeById(constantXml);
                    constantXml.saveStrValue();
                }
                
                break;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
