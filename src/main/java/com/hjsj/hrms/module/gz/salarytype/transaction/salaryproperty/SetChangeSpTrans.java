package com.hjsj.hrms.module.gz.salarytype.transaction.salaryproperty;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 项目名称 ：ehr
 * 类名称：SetChangeSpTrans
 * 类描述：设置比对指标
 * 创建人： lis
 * 创建时间：2016-1-5
 */
public class SetChangeSpTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
	    	HashMap hm=this.getFormHM();
		
	    	/**=1审批比对指标,=0发放比对*/
	    	String gz_module=(String)this.getFormHM().get("gz_module");//0:薪资，1:保险 lis添加
		    String param_flag = (String)hm.get("param_flag");
		    String a01z0Flag = (String)hm.get("a01z0Flag");
		    param_flag=param_flag!=null&&param_flag.trim().length()>0?param_flag:"";//1是薪资属性-设置比对指标
		    if(param_flag.length()<1){
			    param_flag = (String)hm.get("param_flag");
			    hm.remove("param_flag");
			    param_flag=param_flag!=null&&param_flag.trim().length()>0?param_flag:"";
		    }
		
	    	String salaryid = (String)hm.get("salaryid");
	    	salaryid=PubFunc.decrypt(SafeCode.decode(salaryid));
	    	SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
	    	
    	    if("verify".equalsIgnoreCase(param_flag)){//显示审核报告显示指标 wangrd 2013-11-15
	    		String verify_item=ctrl_par.getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_item");
				if(verify_item==null)
					verify_item="";
				verify_item=verify_item!=null?verify_item.replaceAll(",","','"):"";
	    		hm.put("leftlist",leftVerifyList(salaryid,verify_item,a01z0Flag));
	    		hm.put("rightlist",rightCollectList(salaryid,verify_item,a01z0Flag));
    	    }else if("saveverify".equalsIgnoreCase(param_flag)){//保存审核指标设置 wangrd 2013-11-15
                String rightvalue = (String)hm.get("rightvalue");
                if(!"".equals(rightvalue.trim()))
                rightvalue = rightvalue.replaceAll("/", ",").substring(1);
                if(StringUtils.isBlank(rightvalue))
                	rightvalue = "";
                String verify_item=ctrl_par.getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_item");
                ctrl_par.setValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_item",rightvalue);
                ctrl_par.saveParameter();
	    		StringBuffer str = new StringBuffer();
	    		if(verify_item!=null&&!verify_item.equals(rightvalue)){

	    			str.append("<tr>");
	    			str.append("<td>修改审核指标</td>");
	    			str.append("<td>"+SalaryTemplateBo.getSalarySet(verify_item)+"</td>");
	    			str.append("<td>"+SalaryTemplateBo.getSalarySet(rightvalue)+"</td>");
	    			str.append("</tr>");
					StringBuffer context = new StringBuffer();
					SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
					String name = bo.getSalaryName(salaryid);
					context.append(name+"("+salaryid+")修改审核指标");
					context.append("<table>");
					context.append("<tr>");
					context.append("<td>属性名</td>");
					context.append("<td>变化前</td>");
					context.append("<td>变化后</td>");
					context.append("</tr>");
					context.append(str);
					context.append("</table>");
	    			hm.put("@eventlog", context.toString());
	    		}
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 获取汇总选中指标
	 * @param salaryid
	 * @param rightvalue
	 * @return
	 * @throws GeneralException 
	 */
	private ArrayList rightCollectList(String salaryid,String rightvalue,String a01z0) throws GeneralException{
		ArrayList retlist=new ArrayList();
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where salaryid=");
		sql.append(salaryid);
		sql.append(" and itemid in ('");
		sql.append(rightvalue);
		sql.append("') group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			if(rightvalue.indexOf("sp_flag")!=-1){
				CommonData obj=new CommonData("sp_flag",ResourceFactory.getProperty("label.gz.sp"));//审批状态
				retlist.add(obj);
			}
			if(rightvalue.indexOf("appprocess")!=-1){
				CommonData obj=new CommonData("appprocess",ResourceFactory.getProperty("train.job.student.idea"));//审批意见
				retlist.add(obj);
			}
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("itemid").toString().trim();
				if(("a01z0".equalsIgnoreCase(itemid) && a01z0!=null && "1".equals(a01z0)) || !"a01z0".equalsIgnoreCase(itemid)) {
					CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
					retlist.add(obj);
				}
			}

		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return retlist;
	}
	/**
	 * 
	 * @Title: leftVerifyList   
	 * @Description:  加载备选的审核报告输出指标  
	 * @param @param salaryid
	 * @param @param rightvalue
	 * @param @return 
	 * @author wangrd
	 * @return ArrayList    
	 * @throws
	 */
    private ArrayList leftVerifyList(String salaryid,String rightvalue,String a01z0){
        ContentDAO dao = new ContentDAO(this.frameconn);
        ArrayList retlist=new ArrayList();
        StringBuffer sql=new StringBuffer();
        sql.append("select itemid,itemdesc,sortid from salaryset where salaryid=");
        sql.append(salaryid);
        if(rightvalue!=null&&!"".equals(rightvalue))
        {
            sql.append("and itemid not in ('");
            sql.append(rightvalue);
            sql.append("')");
        }
        sql.append("group by itemid,itemdesc,sortid order by sortid");
        ArrayList dylist = null;
        try {
            dylist = dao.searchDynaList(sql.toString());
            for(Iterator it=dylist.iterator();it.hasNext();){
                DynaBean dynabean=(DynaBean)it.next();
                String itemid =dynabean.get("itemid").toString().trim();
                if ("A0100".equalsIgnoreCase(itemid) || "A0000".equalsIgnoreCase(itemid) || "Nbase".equalsIgnoreCase(itemid)  )
                    continue; 
                if ("B0110".equalsIgnoreCase(itemid) || "E0122".equalsIgnoreCase(itemid) || "A0101".equalsIgnoreCase(itemid)  )
                    continue;     
				if(("a01z0".equalsIgnoreCase(itemid) && a01z0!=null && "1".equals(a01z0)) || !"a01z0".equalsIgnoreCase(itemid)) {
	                CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
	                retlist.add(obj);
				}
            }
        } catch (GeneralException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retlist;
    }
}
