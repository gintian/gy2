package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:保存指标</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 4, 2008:3:26:10 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SaveIndexTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String indexcode=(String)this.getFormHM().get("indexcode");
			String indexname=(String)this.getFormHM().get("indexname");
			String contentzm=(String)this.getFormHM().get("content");
	
			String cont = SafeCode.decode(contentzm);
			String content=PubFunc.getStr(cont);
			String app_fashion=(String)this.getFormHM().get("app_fashion");
			String bitianxiang=(String)this.getFormHM().get("bitianxiang");
			String fieldsetid = (String)this.formHM.get("setid");
			String cDX = fieldsetid.toUpperCase();
			//字符
			String itemlength=(String)this.getFormHM().get("itemlength");
			String itemtype=(String)this.getFormHM().get("itemtype");
			//代码
			String joincodename=(String)this.getFormHM().get("joincodename");
			joincodename=joincodename==null||joincodename.length()==0?"0":joincodename;
			String codelength=(String)this.getFormHM().get("codelength");
			String codeitemtype=(String)this.getFormHM().get("codeitemtype");
			//数字
			String numberlength=(String)this.getFormHM().get("numberlength");
			String decimalwidth=(String)this.getFormHM().get("decimalwidth");
			String intitemtype=(String)this.getFormHM().get("intitemtype");
			//日期
			String datelength=(String)this.getFormHM().get("datelength");
			String dateitemtype=(String)this.getFormHM().get("dateitemtype");
			//备注
			String bzitemtype=(String)this.getFormHM().get("bzitemtype");
			String limitlength=(String)this.getFormHM().get("limitlength");
			String inputtype = (String)this.getFormHM().get("inputtype");
			IndexBo subset = new IndexBo(this.getFrameconn());
			int cdx = subset.initial(cDX);
			FieldItem fielditem = new FieldItem();
			if("value1".equals(app_fashion)){
				subset.wordApp(indexcode,indexname,content,itemlength,itemtype,fieldsetid,cdx,bitianxiang);
				int d=50;
		        try{
		        	d=Integer.parseInt(itemlength);
		        }catch(Exception e){
		        	d=0;
		        }
				fielditem.setItemlength(d);
			    fielditem.setItemtype(itemtype);
			}else if("value2".equals(app_fashion)){
				subset.codeApp(indexcode,indexname,content,cdx,fieldsetid,joincodename,codelength,codeitemtype,bitianxiang);
				int d=2;
		        try{
		        	d=Integer.parseInt(codelength);
		        }catch(Exception e){
		        	d=0;
		        }
				fielditem.setItemlength(d);
			    fielditem.setItemtype(codeitemtype);
			}else if("value3".equals(app_fashion)){
				subset.dataApp(indexcode,indexname,content,cdx,fieldsetid,numberlength,decimalwidth,intitemtype,bitianxiang);
				int d=2;
		        try{
		        	d=Integer.parseInt(numberlength);
		        }catch(Exception e){
		        	d=0;
		        }
				fielditem.setItemlength(d);
			    fielditem.setItemtype(intitemtype);
			}else if("value4".equals(app_fashion)){
				subset.dateApp(indexcode,indexname,content,cdx,fieldsetid,datelength,dateitemtype,bitianxiang);
				
				int d=2;
		        try{
		        	d=Integer.parseInt(datelength);
		        }catch(Exception e){
		        	d=0;
		        }
				fielditem.setItemlength(d);
			    fielditem.setItemtype(dateitemtype);
			}else if("value5".equals(app_fashion)){
				inputtype = inputtype == null || inputtype.length()==0 ? "0" : inputtype;
				// 保存
				int itemlen = 0;
				if(limitlength==""|| Integer.parseInt(limitlength)==10){
					itemlen = 10;
				}else{
					itemlen = Integer.parseInt(limitlength);
				}
				subset.bzApp(indexcode,indexname,content,cdx,fieldsetid,bzitemtype,bitianxiang, inputtype,itemlen);
				fielditem.setItemlength(itemlen);
			    fielditem.setItemtype(bzitemtype);
			    fielditem.setInputtype(Integer.parseInt(inputtype));
			}
	        fielditem.setFieldsetid(fieldsetid);
	        fielditem.setItemid(indexcode.toLowerCase());
	        fielditem.setItemdesc(indexname.replace("\"", "“"));
	        fielditem.setCodesetid(joincodename);
	        fielditem.setExplain("");
	        int d=0;
	        try{
	        	d=Integer.parseInt(decimalwidth);
	        }catch(Exception e){
	        	d=0;
	        }
	        fielditem.setDecimalwidth(d);
	        fielditem.setDisplaywidth(14);
	        fielditem.setUseflag("0");
	        if ((bitianxiang == null) || ("".equalsIgnoreCase(bitianxiang)) || ("0".equalsIgnoreCase(bitianxiang)))
	          fielditem.setFillable(false);
	        else
	          fielditem.setFillable(true);
	        String state = "1";
	        fielditem.setState(state);
	        if ("0".equalsIgnoreCase(state))
	          fielditem.setVisible(false);
			DataDictionary.addFieldItem(fieldsetid, fielditem, 1);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
