package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class ManageFieldsTrans extends IBusiness {

	/**
	 * 查找是否存在相同的因子对象
	 * 
	 * @param name
	 * @param list
	 * @return
	 */
	private Factor findFactor(String name, ArrayList list, int index,String Itemdesc) {
		Factor factor = null;		
		for (int i = 0; i < list.size(); i++) {
			factor = (Factor) list.get(i);
			if (name.equalsIgnoreCase(factor.getFieldname()) && (i == index))
			{
				factor.setHz(Itemdesc);
				break;
			}
				
			factor = null;
		}
		return factor;
	}

	public void execute() throws GeneralException {
		String[] fields = (String[]) this.getFormHM().get("right_fields");		
		String kq_type=(String)this.getFormHM().get("kq_type");	
	    String kq_cardno=(String)this.getFormHM().get("kq_cardno");	
	    String kq_gno=(String)this.getFormHM().get("kq_gno");		
	    if(kq_type==null||kq_type.length()<=0)
	    	kq_type="";
	    if(kq_cardno==null||kq_cardno.length()<=0)
	    	kq_cardno="";
	    if(kq_gno==null||kq_gno.length()<=0)
	    	kq_gno="";
		if (fields == null || fields.length == 0) {
			throw new GeneralException(ResourceFactory.getProperty("errors.query.notexistfield"));
		}
		int j = 0;
		StringBuffer strexpr = new StringBuffer();
		ArrayList list = new ArrayList();
		ArrayList fieldlist=new ArrayList();
		/** 信息类型定义default=1（人员类型） */
		
		try {
			/** 保存的因子对象列表 */
			ArrayList factorlist = (ArrayList) this.getFormHM().get(
					"factorlist");
			/** 定义条件项 */
			FieldItem item = null;
			for (int i = 0; i < fields.length; i++) {
				String fieldname = fields[i];
				if (fieldname == null || "".equals(fieldname))
					continue;				
				item = DataDictionary.getFieldItem(fieldname.toUpperCase());
				Factor factor = null;
				if (item != null) {
					/**选中的指标列表*/
					CommonData vo=new CommonData();
					String Itemdesc="";
					if(fieldname.toLowerCase().equals(kq_type.toLowerCase().trim()))
					{
						Itemdesc="考勤方式";
					}else if(fieldname.toLowerCase().equals(kq_cardno.toLowerCase().trim()))
					{
						Itemdesc="考勤卡号";
					}else if(fieldname.toLowerCase().equals(kq_gno.toLowerCase().trim()))
					{
						Itemdesc="工号";
					}else
					{
						Itemdesc=item.getItemdesc();
					}					
					vo.setDataName(Itemdesc);					
					vo.setDataValue(item.getItemid());
					fieldlist.add(vo);							
					/** 已定义的因子再现 */
					if (factorlist != null) {
						factor = findFactor(fieldname, factorlist,i,Itemdesc);
						if (factor != null) {
							list.add(factor);
							continue;
						}
					}
					factor = new Factor(1);
					factor.setCodeid(item.getCodesetid());
					factor.setFieldname(item.getItemid());					
					factor.setHz(Itemdesc);
					factor.setFieldtype(item.getItemtype());
					factor.setItemlen(item.getItemlength());
					factor.setItemdecimal(item.getDecimalwidth());
					factor.setOper("=");// default
					factor.setLog("*");// default
					list.add(factor);
					++j;
					strexpr.append(j);
					strexpr.append("*");
				}
		
			}
			if (strexpr.length() > 0)
				strexpr.setLength(strexpr.length() - 1);
		} catch (Exception ee) {
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		} finally {
			this.getFormHM().put("factorlist", list);
			this.getFormHM().put("selectedlist",fieldlist);
			this.getFormHM().put("dblist",nbaseList());
		}
	}
    public ArrayList nbaseList()
    {
    	ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
		String org_id=managePrivCode.getPrivOrgId();   
    	ArrayList kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),org_id);    		
		String user="";
		for(int i=0;i<kq_dbase_list.size();i++){
			user=user+"'"+kq_dbase_list.get(i).toString()+"',";
		}
		ArrayList list= new ArrayList();
		CommonData vo=new CommonData();
		vo.setDataName("所有人员库");
		vo.setDataValue("all");
		list.add(vo);
		user=user.trim();
		user=user.substring(0,user.length()-1);
		String dbcond="select dbname,pre from dbname where pre in("+user+")";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search(dbcond.toString());
			while(this.frowset.next())
			{
				vo=new CommonData();
				vo.setDataName(this.frowset.getString("dbname"));
				vo.setDataValue(this.frowset.getString("pre"));
				list.add(vo);
			}
		}catch(Exception e)
		{
		   e.printStackTrace();	
		}
		return list;
    }

}
