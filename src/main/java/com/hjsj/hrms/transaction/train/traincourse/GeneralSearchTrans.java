package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GeneralSearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
	    	HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");

	    	String fieldsetid = (String)reqhm.get("fieldsetid");
    		fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
    		reqhm.remove("fieldsetid");
    		/**用来判断用单位，部门指标查询时，是否根据权限来显示=0不用权限控制，默认为0，=1是用权限控制*/
		    String isPriv=(String)reqhm.get("isPriv");
		    isPriv=isPriv==null?"0":isPriv;
	    	ArrayList fieldlist = new ArrayList();
	    	
	    	//培训采集特殊处理参数model
	    	String model = (String)reqhm.get("model");
	    	reqhm.remove("model");

	    	// 备选指标中显示所有指标(allfields=1), 解决bug0041943
	    	boolean allfields = reqhm.get("allfields") == null ? false : "1".equals(reqhm.get("allfields"));
            reqhm.remove("allfields");
            //是否启用人员指标控制，=1：是；=其他：不控制
            boolean empPriv = reqhm.get("empPriv") == null ? false : "1".equals(reqhm.get("empPriv"));
            reqhm.remove("empPriv");

	    	if("p05".equalsIgnoreCase(fieldsetid)){
	    		fieldlist = p05Itemlist();
	    	}else{
	    		ArrayList setlist=DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
	    		
	    		if("r31".equalsIgnoreCase(fieldsetid)&&model!=null&&model.length()>0){
	    			TransDataBo transbo = new TransDataBo(this.getFrameconn(),model); 
	    			setlist=transbo.filedItemList();
	    		}
	    		
	    		if("q05".equalsIgnoreCase(fieldsetid)|| "q07".equalsIgnoreCase(fieldsetid)|| "q09".equalsIgnoreCase(fieldsetid))
	    			setlist=DataDictionary.getFieldList("q03",Constant.USED_FIELD_SET);
	    		
	    		if(empPriv)
	    		    setlist = this.getUserView().getPrivFieldList(fieldsetid);
	    		
    			for(int i=0;i<setlist.size();i++){
	    			FieldItem fielditem = (FieldItem)setlist.get(i);
	    			
	    			String feildid = fielditem.getFieldsetid();
	    			String itemid = fielditem.getItemid();
	    			if("R31".equalsIgnoreCase(feildid) && "r3125".equalsIgnoreCase(itemid))
	    				continue;
	    			if("r31".equalsIgnoreCase(fieldsetid)&&model!=null&&model.length()>0){
	    				fielditem = DataDictionary.getFieldItem(fielditem.getItemid(),"r31");
	    			}
	    			
	    			if(!allfields) {  
    	    			if(!",A,B,K,H,".contains(feildid.toUpperCase().subSequence(0, 1)) && "0".equals(fielditem.getState()))
    	    				continue;
	    			}
	    			// 查询不支持备注型指标，暂时不控制备注型指标
                    if("M".equalsIgnoreCase(fielditem.getItemtype()))
                        continue;
	    			/**q07和q09是部门考勤数据，没有a0100字段*/
	    			if("q07".equalsIgnoreCase(fieldsetid)|| "q09".equalsIgnoreCase(fieldsetid))
	    			{
	    				if("a0100".equalsIgnoreCase(fielditem.getItemid())|| "nbase".equalsIgnoreCase(fielditem.getItemid())|| "E0122".equalsIgnoreCase(fielditem.getItemid())|| "e01a1".equalsIgnoreCase(fielditem.getItemid()))
	    					continue;
	    			}
	    			
	    			if(empPriv) {
	    			    if(this.userView.analyseFieldPriv(fielditem.getItemid())==null)
	    			        continue;

	    			    if(this.userView.analyseFieldPriv(fielditem.getItemid()).length()<1)
	    			        continue;
	    			    
	    			    if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
	    			        continue;
	    			}
	    			    
	    			CommonData dataobj = new CommonData();
	     			dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemtype()
		    				+":"+fielditem.getCodesetid()+":"+fielditem.getFieldsetid(),
		    				fielditem.getItemdesc());
		     		fieldlist.add(dataobj);
	    		}
	    	}
	    	this.getFormHM().put("isPriv", isPriv);
	    	this.getFormHM().put("fieldlist",fieldlist);
	    	this.getFormHM().put("fieldsetid",fieldsetid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	private ArrayList p05Itemlist(){
		ArrayList itemlist = new ArrayList();
		CommonData tempobj = new CommonData("A0101:A:0:P05","发单人");
		itemlist.add(tempobj);
		tempobj = new CommonData("A0101_0:A:0:P05","接单人");
		itemlist.add(tempobj);
		tempobj = new CommonData("A0101_1:A:0:P05","审核人");
		itemlist.add(tempobj);
		
		ArrayList list = DataDictionary.getFieldList("p05",Constant.USED_FIELD_SET);
		for(int i=0;i<list.size();i++){
			FieldItem fielditem = (FieldItem)list.get(i);
			if("p0501".equalsIgnoreCase(fielditem.getItemid())
					|| "p0502".equalsIgnoreCase(fielditem.getItemid())){
				fielditem.setItemlength(20);
			}
			if(!fielditem.isVisible())
				continue;
			if("M".equalsIgnoreCase(fielditem.getItemtype()))
				continue;
			if("p0500".equalsIgnoreCase(fielditem.getItemid())){
				continue;
			}
			tempobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemtype()
					+":"+fielditem.getCodesetid()+":"+fielditem.getFieldsetid(),
					fielditem.getItemdesc());
			itemlist.add(tempobj);
		}
		return itemlist;
	}

}
