package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
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
public class AlertSingleMoreIndTrans extends IBusiness {

	public void execute() throws GeneralException {
    	try {
    		HashMap hm=this.getFormHM();
    		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
    		String setname = (String)reqhm.get("setname");
    		setname=setname!=null&&setname.trim().length()>0?setname:"";
    		reqhm.remove("setname");
    		
    		String a_code = (String)reqhm.get("a_code");
    		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
    		reqhm.remove("a_code");
    		
    		String viewsearch = (String)reqhm.get("viewsearch");
    		viewsearch=viewsearch!=null&&viewsearch.trim().length()>0?viewsearch:"";
    		reqhm.remove("viewsearch");
    		
    		String infor = (String)reqhm.get("infor");
    		infor=infor!=null&&infor.trim().length()>0?infor:"";
    		reqhm.remove("infor");
    		
    		String strid = (String)reqhm.get("strid");
    		strid=strid!=null&&strid.trim().length()>0?strid:"";
    		reqhm.remove("strid");
    		
    		String train = (String) reqhm.get("train");
    		train=train!=null&&train.trim().length()>0?train:"";
            reqhm.remove("train");
    		
    		/*所有人员信息集 xuj 2010-5-28 add*/
            ArrayList dataList = new ArrayList();
            ArrayList fieldsetlist = new ArrayList();
            if ("1".equals(infor)) {
                if ("train".equalsIgnoreCase(train) && !this.userView.isSuper_admin()) {
                  //外部培训过滤子集
                    ArrayList list = trainItemList();
                    for (int i = 0; i < list.size(); i++) {
                        FieldSet fieldset = (FieldSet) list.get(i);
                        /** 未构库不加进来 */
                        if ("0".equalsIgnoreCase(fieldset.getUseflag()))
                            continue;
    
                        if (!"A00".equalsIgnoreCase(fieldset.getFieldsetid())) {
                            ArrayList checklist = this.userView.getPrivFieldList(fieldset.getFieldsetid(), Constant.USED_FIELD_SET);
    
                            if (checklist == null || checklist.size() < 1)
                                continue;
                        }
                        fieldsetlist.add(fieldset);
                    }
                } else
                    fieldsetlist = this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
            }else if("2".equals(infor)){
                fieldsetlist = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
            }else if("3".equals(infor)){
                fieldsetlist = this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
            }
            if(fieldsetlist!=null){
                for(int i=0;i<fieldsetlist.size();i++){
                    FieldSet fs = (FieldSet)fieldsetlist.get(i);
                    /*if("1".equalsIgnoreCase(this.userView.analyseTablePriv(fs.getFieldsetid()))){//读权限//zhaogd 2014-3-6 子集读权限，指标写权限让修改
                        continue;
                    }*/
                    if("A00".equalsIgnoreCase(fs.getFieldsetid())|| "B00".equalsIgnoreCase(fs.getFieldsetid())|| "K00".equalsIgnoreCase(fs.getFieldsetid())){
                        continue;
                    }
                    if("2".equals(infor)){
                        GzAmountXMLBo xmlbo = new GzAmountXMLBo(this.getFrameconn(),0);
                        String viewname = xmlbo.getValue("base_set");
                        viewname = viewname==null?"":viewname;
                        if(viewname.indexOf(fs.getFieldsetid())!=-1)
                            continue;
                    }
                    CommonData cd = new CommonData(fs.getFieldsetid(),fs.getCustomdesc());
                    dataList.add(cd);
                }
            }
            if("4".equals(infor))
                dataList=this.getDataSetList();
            hm.put("fieldSetDataList", dataList);
            if(dataList.size()==0){
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.batchupdate.nopriv")));
            }
            if ("".equals(setname))
            setname=((CommonData)dataList.get(0)).getDataValue();
            
            String history = "1";
            FieldSet field = DataDictionary.getFieldSetVo(setname);
            if(field.isMainset()){
                history = "0";
            }
            hm.put("history",history);
    		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
    		String pri = this.userView.analyseTablePriv(setname);
    		ArrayList fieldlist = new ArrayList();
    		if(!"0".equals(pri)){
    			GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);
    			ArrayList list = gzbo.fieldItemList(fieldset);
    			for(int i=0;i<list.size();i++){
    				FieldItem item = (FieldItem)list.get(i);
    				if(item==null)
    					continue;
    				if(!fieldset.isMainset()){
    					if("A0100".equalsIgnoreCase(item.getItemid()))
    						continue;
    					if("A0101".equalsIgnoreCase(item.getItemid()))
    						continue;
    					if("B0110".equalsIgnoreCase(item.getItemid()))
    						continue;
    					if("E0122".equalsIgnoreCase(item.getItemid()))
    						continue;
    					if("E01A1".equalsIgnoreCase(item.getItemid()))
    						continue;
    				}
    				if("2".equals(infor)){
    					if("B0110".equalsIgnoreCase(item.getItemid()))
    						continue;
    				}
    				if("3".equals(infor)){
    					if("E01A1".equalsIgnoreCase(item.getItemid()))
    						continue;
    				}
    				if("A0000".equalsIgnoreCase(item.getItemid()))
    					continue;
    				
    				if("2".equals(this.userView.analyseFieldPriv(item.getItemid()))){
    					if(!item.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
    							||!"0".equals(fieldset.getChangeflag())){
    						fieldlist.add(item);
    					}
    				}
    			}
    		}
    		
    		hm.put("fieldlist",fieldlist);
    		hm.put("a_code",a_code);
    		hm.put("setname",setname);
    		hm.put("infor",infor);
    		
    		String dbname = (String)reqhm.get("dbname");
    		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
    		reqhm.remove("dbname");
    		
    		/*BatchBo batchbo = new BatchBo();
    		String count = batchbo.countDelItem(this.frameconn,this.userView,setname,dbname,a_code
    				,viewsearch,infor)+"";
    		String countall = batchbo.countItemall(this.frameconn,this.userView,setname,dbname,infor)+"";*/
    		//	String countall = batchbo.countItemall(this.frameconn,this.userView,setname,dbname,infor,a_code)+"";
    		
    		String arr[] = strid.split("`");
            //外部培训中的strids中的数据用逗号分隔
            if(strid.indexOf(",") != -1)
                arr = strid.split(",");
    		
    		int secount=0;
    		if(arr.length>0){
    			for(int i=0;i<arr.length;i++){
    				if(arr[i]!=null&&arr[i].trim().length()>1)
    					secount++;
    			}
    		}
    		
    		hm.put("dbname",dbname);
    		hm.put("viewsearch",viewsearch);
    		//hm.put("count",count);
    		//hm.put("countall",countall);
    		hm.put("secount", secount+"");    //20141021 dengcan 记录录入追加批量修改已选记录功能
    		hm.put("strid", strid);
    	} catch (Exception e) {
    	    e.printStackTrace();
        }
	}
	/**
	 * duml*/
	public ArrayList getDataSetList(){
		ArrayList list =new ArrayList();
		String viewname="";
		GzAmountXMLBo xmlbo = new GzAmountXMLBo(this.getFrameconn(),0);
		viewname = xmlbo.getValue("base_set");
		if(viewname.length()>0){
		String [] value=viewname.split(",");
		
			for(int i=0;i<value.length;i++){
				CommonData dataobj;
				if("0".equals(this.userView.analyseTablePriv(value[i].toUpperCase())))
		  	    	continue;
				FieldSet fieldset =DataDictionary.getFieldSetVo(value[i].toUpperCase());
				if(fieldset!=null){
					dataobj = new CommonData(value[i],fieldset.getCustomdesc());
					list.add(dataobj);
				}
			}
		}
		return list;
	}
	/**
	 * 获取培训参数中配置的子集
	 * @return
	 */
	private ArrayList trainItemList(){
        ArrayList list = new ArrayList();
        ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
        String viewname = constantbo.getValue("subset");
        viewname=viewname!=null&&viewname.trim().length()>3?viewname:"";
        String arr[] = viewname.split(",");
        for(int i=0;i<arr.length;i++){
            String fieldsetid = arr[i];
            fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
            if(fieldsetid.trim().length()>0){
                FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
                if(fieldset!=null)
                    list.add(fieldset);
            }
        }
        return list;
    }

}
