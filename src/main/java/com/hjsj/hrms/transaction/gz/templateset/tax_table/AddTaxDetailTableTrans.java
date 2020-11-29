package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableSetBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class AddTaxDetailTableTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String str=(String)this.getFormHM().get("recordStr");
			str=str!=null&&str.trim().length()>0?str:"";
			
            String taxid=(String)this.getFormHM().get("taxid");
            taxid=taxid!=null&&taxid.trim().length()>0?taxid:"";
            
            String param=(String)this.getFormHM().get("param");
            param=param!=null&&param.trim().length()>0?param:"";
            
            String k_base=(String)this.getFormHM().get("k_base");
            k_base=k_base!=null&&k_base.trim().length()>0?k_base:"";
            
            String income=(String)this.getFormHM().get("income");
            income=income!=null&&income.trim().length()>0?income:"";
            
            String itemid=(String)this.getFormHM().get("itemid");
            itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
            
            String mode=(String)this.getFormHM().get("mode");
            mode=mode!=null&&mode.trim().length()>0?mode:"0";
            
            String salaryid=(String)this.getFormHM().get("salaryid");
            salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
            
            if(salaryid.trim().length()>0&&itemid.trim().length()>0){
	        	SalaryCtrlParamBo salarybo = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
	        	salarybo.setValue(SalaryCtrlParamBo.YS_FIELDITEM,"id",itemid,"mode",mode,income);
	        	salarybo.saveParameter();
	        }
            
            deleteTaxDetailTable(taxid);
            TaxTableXMLBo bo = new TaxTableXMLBo(this.getFrameconn());
            String paramStr=bo.getParam(param);
            updateTaxInfo(taxid,paramStr,k_base);
            if(null!=str&&!"".equals(str)&&0<str.trim().length())
            {
                 ArrayList list = getTaxDetailTableValue(str);
                 insertRecords(list,taxid);
            }
            this.getFormHM().put("taxid",taxid);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * 分析前台传来的税率明细表的值
	 * @param recordStr
	 * @return
	 */
	public ArrayList getTaxDetailTableValue(String recordStr){
		ArrayList list = new ArrayList();
		try{
			String[] str_Arr=recordStr.split(",");

		    for(int i=0;i<str_Arr.length;i++){
		    	String[] str_A=str_Arr[i].split("/");
			    LazyDynaBean bean= new LazyDynaBean();
			    bean.set("ynse_down", "#".equals(str_A[0])?"0":str_A[0]);
			    bean.set("ynse_up", "#".equals(str_A[1])?"0":str_A[1]);
			    bean.set("sl", "#".equals(str_A[2])?"0":str_A[2]);
			    bean.set("flag", "#".equals(str_A[3])?"0":str_A[3]);
			    bean.set("sskcs", "#".equals(str_A[4])?"0":str_A[4]);
			    bean.set("kc_base", "#".equals(str_A[5])?"0":str_A[5]);
			    bean.set("description", "#".equals(str_A[6])?"":str_A[6]);
			    list.add(bean);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 在税率明细表中填加记录
	 * @param list
	 * @param taxid
	 */
	public void insertRecords(ArrayList list,String taxid){
		
		//IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		TaxTableSetBo bo = new TaxTableSetBo(this.getFrameconn());
		try{
			int taxitem =0;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			for(int i=0;i<list.size();i++){
				StringBuffer str= new StringBuffer("insert into gz_taxrate_item" +
						"(taxitem,taxid,ynse_down,ynse_up,sl,flag,sskcs,kc_base,description) values ");
				taxitem=bo.getTaxId("gz_taxrate_item","taxitem");
				LazyDynaBean bean=(LazyDynaBean)list.get(i);
				str.append("(");
				str.append(taxitem+",");
				str.append(taxid+",");
				str.append((String)bean.get("ynse_down")+",");
				str.append((String)bean.get("ynse_up")+",");
				str.append((String)bean.get("sl")+",");
				str.append((String)bean.get("flag")+",");
				str.append((String)bean.get("sskcs")+",");
				str.append((String)bean.get("kc_base")+",");
				str.append("'"+(String)bean.get("description")+"')");
				dao.insert(str.toString(),new ArrayList());
				/*RecordVo vo = new RecordVo("gz_taxrate_item");
				vo.setInt("taxitem", taxitem);
				vo.setInt("taxid", Integer.parseInt(taxid));
				vo.setString("ynse_down", (String)bean.get("ynse_down"));
				vo.setString("description", (String)bean.get("description"));
				dao.addValueObject(vo);*/
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
	}
	/**
	 * 更新税率表的信息
	 * @param taxid
	 * @param param
	 * @param kc_base
	 */
	public void updateTaxInfo(String taxid,String param,String kc_base){
		StringBuffer str= new StringBuffer();
		try{
			str.append("update gz_tax_rate set param='");
			str.append(param);
			str.append("', k_base=");
			str.append(kc_base==null|| "".equals(kc_base)?"0":kc_base);
			str.append(" where taxid='");
			str.append(taxid+"'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(str.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
    /**
     * 删除税率明细信息
     * @param taxid
     */
	public void deleteTaxDetailTable(String taxid){
		try{
			String sql="delete from gz_taxrate_item where taxid='"+taxid+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.delete(sql,new ArrayList());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
