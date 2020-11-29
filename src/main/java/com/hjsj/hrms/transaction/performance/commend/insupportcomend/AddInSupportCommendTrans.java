package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.performance.commend.CommendXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddInSupportCommendTrans extends IBusiness {
	public void execute() throws GeneralException{
		try{
			HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
			String oper=(String)hm.get("oper");
			/**
			 * oper: 1:为创建日期和状态标识赋初值，2：增加到数据库
			 */
			ArrayList list = DataDictionary.getFieldList("P02",Constant.USED_FIELD_SET);
			if("1".equals(oper)){
				
			   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			   String p0205=format.format(new Date());
			  
			   String state="起草";
			   this.getFormHM().put("state",state);
				ArrayList sysList=new ArrayList();
				
				for(int i=0;i<list.size();i++){
					FieldItem item =(FieldItem)list.get(i);
					if("p0201".equalsIgnoreCase(item.getItemid()))
						continue;
					if("extendattr".equalsIgnoreCase(item.getItemid()))
						continue;
					if(!item.isVisible())
						continue;
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("desc",item.getItemdesc());
					if("A".equalsIgnoreCase(item.getItemtype())&&!"0".equals(item.getCodesetid())&&!"p0209".equalsIgnoreCase(item.getItemid())){
						bean.set("value",item.getItemid()+".value");
						bean.set("viewvalue",item.getItemid()+".viewvalue");
					}
				    bean.set("itemid",item.getItemid());
				   // bean.set("textitemid",item.getItemid());
				    bean.set("columns","");
					bean.set("itemtype",item.getItemtype());
					bean.set("codesetid",item.getCodesetid());
					item.getDecimalwidth();
					bean.set("itemlength",String.valueOf(item.getItemlength()+item.getDecimalwidth()+1));
					bean.set("intLength",String.valueOf(item.getItemlength()));
					if("p0205".equalsIgnoreCase(item.getItemid()))
						bean.set("initvalue",p0205);
					else
						bean.set("initvalue","");
					sysList.add(bean);
					
				}
				ArrayList commendFieldList = commendFieldList();
				this.getFormHM().put("sysList",sysList);
				this.getFormHM().put("commendFieldList",commendFieldList);
			}else if("2".equals(oper)){
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				String p0201 = idg.getId("P02.P0201");
				String ctrl_param=(String)this.getFormHM().get("ctrl_param");
				String commendField=(String)this.getFormHM().get("commendField");
				CommendXMLBo bo=new CommendXMLBo(this.getFrameconn());
				String paramStrValue=bo.getCtrl_param(ctrl_param,commendField);
			    ContentDAO dao = new ContentDAO(this.getFrameconn());
			    StringBuffer cloStr=new StringBuffer();
			    StringBuffer valStr=new StringBuffer();
			    ArrayList dataList = (ArrayList)this.getFormHM().get("sysList");
			    for(int i=0;i<dataList.size();i++){
			    	LazyDynaBean bean=(LazyDynaBean)dataList.get(i);
			    	cloStr.append(",");
			    	cloStr.append((String)bean.get("columns"));
			    	valStr.append(",");
			    	String itemtype=(String)bean.get("itemtype");
			    	String codesetid =(String)bean.get("codesetid");
			    	String columns =(String)bean.get("columns");
			    	if("A".equalsIgnoreCase(itemtype)&&!"0".equals(codesetid)&&!"p0209".equalsIgnoreCase(columns)){
			    		if(bean.get("value")!=null &&((String)bean.get("value")).trim().length()>0 )
			    	         valStr.append("'"+(String)bean.get("value")+"'");
			    		else
			    			valStr.append("null");
			    	   
			    	}else
			    	{
			    		if(bean.get("itemid")!=null&&((String)bean.get("itemid")).trim().length()>0){
			    		     //valStr.append("'"+(String)bean.get("itemid")+"'");
			    			 String arrays=(String)bean.get("itemid");
			    			 String array[]=arrays.split("-");
			    			 int length=array.length;
			    			 if(length>1){
			    				 valStr.append(Sql_switcher.dateValue(arrays));
			    			 }else{
			    				 valStr.append("'"+arrays+"'");
			    			 }
			    		}     
			    		else{
			    			valStr.append("null");
			    		}
			    	}
			    }
			    StringBuffer sql = new StringBuffer();
			    sql.append("insert into p02 ( p0201");
			    sql.append(cloStr.toString());
			    sql.append(") values ('"+p0201+"'");
			    sql.append(valStr.toString());
			    sql.append(")");
			    //System.out.println("sql=="+sql.toString());
			    dao.insert(sql.toString(),new ArrayList());
			    
			    dao.update("update p02 set ctrl_param='"+paramStrValue+"' where p0201="+p0201);

			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * 得到后备推荐职务指标列表
	 * @return ArrayList
	 */
	public ArrayList commendFieldList(){
		ArrayList list = new ArrayList();
		list.add(new CommonData(" ","      "));
		try
		{
			ArrayList alist = DataDictionary.getFieldList("P03",Constant.USED_FIELD_SET);
			for(int i=0;i<alist.size();i++)
			{
				FieldItem item =(FieldItem)alist.get(i);
				if("A".equalsIgnoreCase(item.getItemtype())&&!"0".equalsIgnoreCase(item.getCodesetid()))
				{
					if("UM".equalsIgnoreCase(item.getCodesetid())|| "UN".equalsIgnoreCase(item.getCodesetid()))
					{
						continue;
					}
					else
					{
						list.add(new CommonData(item.getItemid(),item.getItemdesc()));
					}
				}
			}/**for i end*/ 
		}//try end
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}



}
