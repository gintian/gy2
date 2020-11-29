package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.businessobject.gz.voucher.VoucherBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* 
* 类名称：VoucherSetTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 16, 2013 5:48:39 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 16, 2013 5:48:39 PM   
* 修改备注：   获取凭证项目
* @version    
*
 */
public class VoucherSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		String temp=(String) this.getFormHM().get("salaryid");//凭证项目中设置的时候选择非凭证分录明细表时，会得到相应的薪资类别id
		VoucherBo bo = new VoucherBo(this.frameconn,this.userView);
		if(temp==null|| "".equals(temp)){
	        HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
	        String id=(String) map.get("salaryId");
	        String item=(String) map.get("item");
	        String pnid=(String) map.get("pnid");
	        String zhibiao2=(String) map.get("zhibiao2");
			ArrayList list=bo.getWarrantList();	
			ArrayList list1 = new ArrayList();
			ArrayList list2 = new ArrayList();
			list1.add(new CommonData("warrant","凭证分录明细表"));
			//if(item!=null&&!item.equals("")&&!item.equals("null")){
				list2=bo.getSalaryList(id);
				for(int i=0;i<list2.size();i++){
					list1.add(list2.get(i));
				}
			//}
			ContentDAO dao= new ContentDAO(this.getFrameconn());
			String sql = "select C_GROUP from GZ_WARRANTLIST where PN_ID="+pnid+"";
			RowSet rs = dao.search(sql);
			StringBuffer no = new StringBuffer();
			while(rs.next()){
				no.append(rs.getString("C_GROUP"));
				no.append(",");
			}
			no.append(item);
			ArrayList selectedFieldList = new ArrayList();
			if(zhibiao2!=null&&!"".equals(zhibiao2))
			selectedFieldList=bo.getRightItemList(zhibiao2);
			this.getFormHM().put("leftList", list);
			this.getFormHM().put("rightList", selectedFieldList);
			this.getFormHM().put("salaryList", list1);
			this.getFormHM().put("no", no.toString());
		}else{
			ArrayList list=bo.getAllItemList(temp,"EntrySet");//这里是凭证项目设置，数据类型不限，但不能是人员编号||人员序号
			this.getFormHM().put("leftList", list);
		}

	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	}

}
