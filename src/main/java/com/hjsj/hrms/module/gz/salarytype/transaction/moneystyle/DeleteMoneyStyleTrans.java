package com.hjsj.hrms.module.gz.salarytype.transaction.moneystyle;

import com.hjsj.hrms.module.gz.salarytype.businessobject.MoneyStyleSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 项目名称 ：ehr7.x
 * 类名称：DeleteMoneyStyleTrans
 * 类描述：删除币种
 * 创建人： lis
 * 创建时间：2015-12-3
 */
public class DeleteMoneyStyleTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String nstyleids=(String)this.getFormHM().get("selectIDs");
			
			MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
			StringBuilder msg=new StringBuilder();
			ArrayList<String> selectIDs=this.getIdsInfo(nstyleids);
			StringBuilder str=new StringBuilder();
			for(String nstyleid:selectIDs){
				if(bo.checkMoneyStyleIsUse(nstyleid).size()>0){
					msg.append(""+nstyleid+",");
				}else
					str.append(""+nstyleid+",");
			}
			if(msg.length()>0){
				msg.deleteCharAt(msg.length()-1);
				ArrayList list=bo.getMoneyStyleList(msg.toString());
				msg.setLength(0);
				for(int i=0;i<list.size();i++){
					LazyDynaBean bean = (LazyDynaBean)list.get(i);
					msg.append(bean.get("cname")+",");
				}
				msg.deleteCharAt(msg.length()-1);
				throw GeneralExceptionHandler.Handle(new Throwable("'"+msg.toString()+"'，已经被使用，请停止使用后再进行删除!"));
				
			}
			str.deleteCharAt(str.length()-1);
			bo.deteleMoneyStyleInfo(str.toString(),1);
			//bo.deteleMoneyStyleInfo(getIdsInfo(nstyleids),2);
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

	/**
	 * 解析前台传入的id字符串
	 * @param ids
	 * @return id数组
	 * @author zhanghua
	 * @date 2017年6月5日 下午3:40:58
	 */
	private ArrayList<String> getIdsInfo(String ids){
		ArrayList<String> strList=new ArrayList<String>();
		try
		{
			if(ids.indexOf("#")==-1)
				strList.add(ids.substring(1));
			else
			{
				String[] retArr=ids.substring(1).split("#");
				for(int i=0;i<retArr.length;i++)
					strList.add(retArr[i]);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return strList;
	}

}
