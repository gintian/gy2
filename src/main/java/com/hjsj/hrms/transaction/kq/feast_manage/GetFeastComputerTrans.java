package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.feast_manage.FeastComputer;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class GetFeastComputerTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		// TODO Auto-generated method stub
		 HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		 String codeitemid=(String)this.getFormHM().get("code");
		 String hols_status=(String)this.getFormHM().get("hols_status");
		 String exp_field=(String)this.getFormHM().get("exp_field");
		 String setname=(String)hm.get("setname");
		 ArrayList holi_list=(ArrayList)this.getFormHM().get("holi_list");
		 if(codeitemid==null||codeitemid.length()<=0)
         {
        	 codeitemid=this.getUserView().getUserOrgId();
         }
		 FeastComputer feastComputer=new FeastComputer(this.getFrameconn(),this.userView);
		 ArrayList exp_fieldlist=feastComputer.fieldList(this.userView);
		 if(exp_field==null||exp_field.length()<=0)
		    exp_field="q1703";
		 String b0110="";
		 if(this.userView.isSuper_admin())
	 	 {
				b0110="UN";
	 		}else
	 		{
	 			ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
				String userOrgId=managePrivCode.getPrivOrgId();  
	 			b0110="UN"+userOrgId;
	 	 }  
		 feastComputer.initComputer(exp_field,hols_status,b0110);
		 String exp=feastComputer.getFeastComputer(codeitemid,exp_field,hols_status,this.getFrameconn());
		 exp = PubFunc.keyWord_reback(exp);
		 String hols_name="";
		 for(int i=0;i<holi_list.size();i++)
		 {
			 CommonData vo=(CommonData)holi_list.get(i);
			 if(hols_status.equals(vo.getDataValue()))
				 hols_name=vo.getDataName();
		 }
		 hols_name=hols_name+"计算公式";
		 this.getFormHM().put("expr_flag","1");
         this.getFormHM().put("c_expr",exp);
         this.getFormHM().put("message","");
         ArrayList list=feastComputer.getMusterSetTrans("1",this.userView);
         String table="";
         if(setname!=null &&setname.length()>0){
        	 table = setname;
         } 
         else if(list.size()>0)
         {
        	 CommonData dataCo=(CommonData)list.get(0);
        	 table=dataCo.getDataValue();
         }
 	     this.getFormHM().put("setlist",list);
 	     HashMap hash=feastComputer.getFieldBySetNameTrans(table,this.userView);
 	     ArrayList fieldlist=(ArrayList)hash.get("fieldlist");
 	     ArrayList onefiledlist=(ArrayList)hash.get("onefiledlist");
 	     this.getFormHM().put("fieldlist",fieldlist);
	     this.getFormHM().put("onefiledlist",onefiledlist);
	     this.getFormHM().put("hols_status",hols_status);
		 this.getFormHM().put("holi_list",holi_list);
	     this.getFormHM().put("hols_name",hols_name);
	     StringBuffer fieldItems = new StringBuffer(); 
	     ContentDAO dao=new ContentDAO(this.getFrameconn());
		 String sql="select itemdesc ,itemid , itemtype from fielditem where useflag='1' ";
		 try {
				this.frowset = dao.search(sql);
				while(this.frowset.next()){
					//String itemid = this.frowset.getString("itemid");
					String itemdesc = this.frowset.getString("itemdesc");
					String itemtype = this.frowset.getString("itemtype");
					fieldItems.append(itemtype);
					fieldItems.append(" ");
					fieldItems.append(itemdesc);
					fieldItems.append(",");
				}
		 } catch (SQLException e) {
				e.printStackTrace();
		 }
		 this.getFormHM().put("fieldItems",fieldItems.toString());
		 this.getFormHM().put("exp_fieldlist",exp_fieldlist);
		 this.getFormHM().put("exp_field",exp_field);
	}
    
    
}