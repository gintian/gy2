package com.hjsj.hrms.transaction.sys.options.otherparam;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EditDbItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
	
		StringBuffer tab=new StringBuffer();
		StringBuffer field=new StringBuffer();
		String parsp=(String) hm.get("item");	
		//【7255】系统管理/系统参数，人员库指标范围点击设置按钮，点击基本信息，点击确定，界面报空指针错  jingq add 2015.01.30
		parsp = PubFunc.keyWord_reback(parsp);
		String[] temparap=parsp.split("/");
		String pars=temparap[0];
		// WJH 2013-6-26 A0101作为必选处理
		if(pars.indexOf("A01,")>=0 && pars.indexOf("A0101")==-1) {
			pars = pars.replaceFirst("A01,", "A01,A0101,");
		}
		
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
				if(!fsetMap.containsKey(fisss.getFieldsetid())){
					fsetMap.put(fisss.getFieldsetid(),fisss.getFieldsetid());
					tab.append(fisss.getFieldsetid()+",");
				}
			}
		}
		try {
			uporaddBase_field(tab.toString(),field.toString(),temparap[1]);
			if(!getIsValid())
			{
				OtherParam op=new OtherParam(this.getFrameconn());
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				op.updateElementAtrr("/param/base_fields","valid=false");
				op.saveXml(dao);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	private void uporaddBase_field(String table,String field,String name) throws Exception{
		OtherParam op=new OtherParam(this.getFrameconn());
		String xml=op.uporadd(table,field,name);		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		//xml=op.syncField(dao);		
		ArrayList parslist=new ArrayList();
		parslist.add(xml);
		RecordVo constantVo=new RecordVo("constant");
		constantVo.setString("constant", "SYS_OTH_PARAM");
		constantVo=dao.findByPrimaryKey(constantVo);
		if(constantVo!=null)
		{
			constantVo.setString("str_value", xml);
			dao.updateValueObject(constantVo);
		}
		//dao.update("update constant set STR_VALUE=? where constant='SYS_OTH_PARAM'",parslist);
//		dao.update("update constant set STR_VALUE='"+xml+"' where constant='SYS_OTH_PARAM'");
		
	}
	private boolean  getIsValid(){
		Map myMap=new HashMap();
		boolean isCorrect=false;
		try {
			OtherParam op=
				op=new OtherParam(this.getFrameconn());
			
			myMap=op.getBaseFieldMap();
			for(Iterator it=myMap.keySet().iterator();it.hasNext();)
			{
				if(isCorrect)
					break;
				String dbname=(String) it.next();
				Map dbMap=(Map) myMap.get(dbname);
				String table=(String) dbMap.get("table");
//				bug
//				if(table!=null&&table.length()>0){
				String field=(String) dbMap.get("field");				
				String[] tablestr=table.split(",");
				String[] fieldstr=field.split(",");
				
				for(int i=0;i<tablestr.length;i++){
					String tid=tablestr[i];
					FieldSet fs=DataDictionary.getFieldSetVo(tid);
					if(fs!=null)
					{					
                        for(int j=0;j<fieldstr.length;j++)
                        {
                           if(isCorrect)
        					break;
						   String fi=fieldstr[j];
						   FieldItem fis=DataDictionary.getFieldItem(fi);
						   if(fis!=null&&(fs.getFieldsetid()).equalsIgnoreCase(fis.getFieldsetid()))
						   {
							   isCorrect=true;
						   }
					    }
					}
			   }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
		return isCorrect;
	}
}
