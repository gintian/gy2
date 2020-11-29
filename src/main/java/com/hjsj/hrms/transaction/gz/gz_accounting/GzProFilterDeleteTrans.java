package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:GzProFilterDeleteTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Feb 19, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class GzProFilterDeleteTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String del_pro_id = (String)hm.get("del_pro_id");		
		String salaryid = (String)hm.get("salaryid");
		String model=hm.containsKey("model")?(String) hm.get("model"):"";
		if(!(del_pro_id==null || "".equals(del_pro_id)))
		{
			this.delete(salaryid,del_pro_id,dao,model);
		}
	}
	/**
	 * 删除选种的过滤项目
	 * @param id
	 * @param dao
	 */
	public void delete(String salaryid,String id,ContentDAO dao,String model)
	{
		StringBuffer sqlsb = new StringBuffer();
		String[] temps=id.replaceAll("／","/").split("/");
		String ids="";
		for(int i=0;i<temps.length;i++)
			ids+=","+temps[i];
		sqlsb.append(" delete gzItem_filter where id in ("+ids.substring(1)+")");
//		System.out.println(sqlsb.toString());
		try
		{
			dao.update(sqlsb.toString());
			if("history".equalsIgnoreCase(model)){//history 表示为薪资历史数据分析进入
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<temps.length;i++) {
					list.add(temps[i].replaceAll("\"",""));
				}
				HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
				hbo.delFilterIdFromHistory(list);
			}else {
				DeleteIdInSalaryTemplate(salaryid, ids.substring(1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
    /**   
     * @Title: DeleteIdInSalaryTemplate   
     * @Description: 删除过滤项目后，工资类别的相应的过滤项目参数也要删除   
     * @param @param salaryid
     * @param @param ids 
     * @return void    
     * @throws   
    */
    private void DeleteIdInSalaryTemplate(String salaryid,String ids)
    {
        try
        {
            BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
            String xml=bo.getCondXML(salaryid);
            SalaryLProgramBo sLPBo = new SalaryLProgramBo(xml);
            String old_xml = sLPBo.getValue(SalaryLProgramBo.FILTERS);
            String temp_xml="";
            String[] arrIds =ids.split(",");
            temp_xml=","+old_xml+",";
            for(int i=0;i<arrIds.length;i++){
                String id =arrIds[i];
                temp_xml = temp_xml.replace(","+id+",", ",");
                
            }
            //因为在删除全部的项目过滤过程中temp_xml会被替换为逗号，这里不加判断是否为空的，如果是空的说明其中某一步出错了
            if(",".equals(temp_xml))
            	temp_xml = "";
            else
            	temp_xml =temp_xml.substring(1,temp_xml.length()-1);         
        
            sLPBo.setValue(SalaryLProgramBo.FILTERS, temp_xml);
            String new_xml = sLPBo.outPutContent();
            bo.updateLprogram(salaryid, new_xml);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
