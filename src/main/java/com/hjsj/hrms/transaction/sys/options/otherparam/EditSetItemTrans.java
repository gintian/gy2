package com.hjsj.hrms.transaction.sys.options.otherparam;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EditSetItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
//		String cid=(String) hm.get("cid");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer tab=new StringBuffer();
		StringBuffer field=new StringBuffer();
		String parsp=(String) hm.get("item");
		//系统管理，人员类别指标范围，空指针错误  jingq upd 2014.10.29
		parsp = PubFunc.keyWord_reback(parsp);
		if(parsp.startsWith(","))//判断是否是以 逗号开头    wangb 20180705
			parsp = parsp.substring(1);
		String[] temparap=parsp.split("/");
		String pars=temparap[0];
		String[] temppars=pars.split(",");
		Map fsetMap=new HashMap();
		for(int m=0;m<temppars.length;m++){
			fsetMap.put(temppars[m],temppars[m]);
		}
		for(int i=0;i<temppars.length;i++){
			String item=temppars[i];
			if(item.length()==3){
				tab.append(temppars[i]+",");
			}else{
				field.append(temppars[i]+",");
				FieldItem fisss=DataDictionary.getFieldItem(temppars[i]);
				if(fisss!=null){
					if(!fsetMap.containsKey(fisss.getFieldsetid())){
						fsetMap.put(fisss.getFieldsetid(),fisss.getFieldsetid());
						tab.append(fisss.getFieldsetid()+",");
					}
				}
			}
		}
		String codesetid=temparap[2];
		String name=temparap[1];
		String tfield=temparap[3];
		try {
			this.updateSet(codesetid,tfield,name,tab.toString(),field.toString(),dao);
			if(!getIsValid())
			{
				OtherParam op=new OtherParam(this.getFrameconn());
				op.updateElementAtrr("/param/employ_type","valid=false");
				op.saveXml(dao);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void updateSet(String codesetid,String tfield,String tpname,String table,String field,ContentDAO dao) throws Exception{
		OtherParam op=new OtherParam(this.getFrameconn());
		String xml=op.uporaddfield(codesetid,tfield,tpname,table,field);
		//xml=op.syncField(dao);
//		System.out.println(xml.length());
		ArrayList parslist=new ArrayList();
		parslist.add(xml);
		dao.update("update constant set STR_VALUE=? where constant='SYS_OTH_PARAM'",parslist);
//		dao.update("update constant set STR_VALUE='"+xml+"' where constant='SYS_OTH_PARAM'");
		
		
	}
	public void  syncDbset(String zijivaliue,String itmds){
		
	}
	private boolean getIsValid() throws Exception{
		OtherParam op=new OtherParam(this.getFrameconn());
		boolean isCorrect=false;
		Map myMap=op.getEmployeeType();
		if(myMap.size()>0){
			for(Iterator it=myMap.keySet().iterator();it.hasNext();){
				if(isCorrect)
					break;
				String key=(String) it.next();
				Map mv=(Map) myMap.get(key);
				String table=(String) mv.get("table");
				String field=(String) mv.get("field");
				String[] tablestr=table.split(",");
				String[] fieldstr=field.split(",");				
				for(int i=0;i<tablestr.length;i++)
				{
					if(isCorrect)
						break;
					String tid=tablestr[i];
					FieldSet fs=DataDictionary.getFieldSetVo(tid);					
					if(fs!=null)
					{
						for(int j=0;j<fieldstr.length;j++)
						{
						    String fi=fieldstr[j];
                            FieldItem fis=DataDictionary.getFieldItem(fi);
							if(fis!=null&&(fis.getFieldsetid()).equals(fs.getFieldsetid())){
							  	isCorrect=true;
							    break;
						    }						
					    }
				    }
					
				}				
			}
		}
		return isCorrect;
		
	}
}
