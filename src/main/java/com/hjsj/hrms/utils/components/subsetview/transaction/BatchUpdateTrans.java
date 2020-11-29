package com.hjsj.hrms.utils.components.subsetview.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class BatchUpdateTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			String type = (String)this.formHM.get("type");
			if("loadStore".equalsIgnoreCase(type)){
				String setName=(String)this.formHM.get("setName");
				String nbase=this.formHM.get("nbase")==null?"":(String)this.formHM.get("nbase");
//			ArrayList dataList = getFieldList();
				ArrayList dataList=(ArrayList) this.userView.getPrivFieldList(setName).clone();
				this.getFormHM().put("data",dataList);
			}else if("batchSave".equalsIgnoreCase(type)){
				String setName=(String)this.formHM.get("setName");
				String nbase=(String)this.formHM.get("nbase");
				String itemid=this.formHM.get("itemid")==null?"":(String)this.formHM.get("itemid");
				ArrayList dataInfo=this.formHM.get("dataInfo")==null?new ArrayList():(ArrayList)this.formHM.get("dataInfo");
				String formula=this.formHM.get("formula")==null?"":(String)this.formHM.get("formula");
				itemid = SafeCode.decode(itemid);
				formula = SafeCode.decode(formula);
				formula = PubFunc.hireKeyWord_filter(formula);
				FieldItem item = DataDictionary.getFieldItem(itemid);
				
				YksjParser yp=null;
				yp=new YksjParser( this.userView ,new ArrayList(),
						YksjParser.forNormal, getDataType(item.getItemtype()),YksjParser.forPerson , "Ht", "");
				yp.run(formula,this.frameconn,"",setName);
				/**单表计算*/
				String strexpr = yp.getSQL();
				/**为空不计算*/
				if(strexpr.trim().length()==0)
					return ;
				
				ContentDAO dao = new ContentDAO(frameconn);
				String updatesql = "update (reptable) set "+itemid+" = " + strexpr;
				String[] nbases=nbase.split(",");
				// 59220 计算公式已处理完 返回相应的SQL 不知道为什么还要再次处理  而且还都是错的！
//				String replaceItem = "";
//				ArrayList dataList=(ArrayList) this.userView.getPrivFieldList(setName);
//				for(Object o : dataList){
//					FieldItem eveitem = (FieldItem)o;
//					if(formula.contains(eveitem.getItemdesc())){
//						replaceItem = eveitem.getItemid();
//						formula.replace(eveitem.getItemdesc(), eveitem.getItemid());
//						break;
//					}
//				}
//				ArrayList values = new ArrayList();
//				if("".equals(replaceItem)){
//					Object objvalue = null;
//					if("A".equalsIgnoreCase(item.getItemtype())&&!"0".equals(item.getCodesetid())){
//						//代码型
//						formula=formula.split("`")[1];
//						//如果长度超出指标长度，则自动截断
//						if(item.getItemlength()<formula.length())
//							formula=formula.substring(0, item.getItemlength());
//						objvalue = formula;
//					}else if("D".equals(item.getItemtype())){
//						//yyyy-MM-dd H:m:s
//						String datetype="yyyy-MM-dd H:m:s";
//						if(formula.length()==4){
//							datetype="yyyy";
//						}else if(formula.length()==7){
//							datetype="yyyy-MM";
//						}else if(formula.length()==10){
//							datetype="yyyy-MM-dd";
//						}else if(formula.length()==13){
//							datetype="yyyy-MM-dd H";
//						}else if(formula.length()==16){
//							datetype="yyyy-MM-dd H:m";
//						}else if(formula.length()==19){
//							datetype="yyyy-MM-dd H:m:s";
//						}
//						SimpleDateFormat sdf = new SimpleDateFormat(type);
//						if(!"".equals(formula))
//							objvalue =DateUtils.getSqlDate(sdf.parse(formula));
//						else
//							objvalue =null;
//					}else{
//						if (formula == null || formula.equals("null") ||formula.equals(""))	
//						{	
//							formula=null;
//						}
//						objvalue=formula;
//					}
//					values.add(objvalue);
//				}else{
//					values.add(replaceItem);
////					updatesql.replace("?", formula);
//				}
				if(dataInfo.size()==0){
					for(int i=0;i<nbases.length;i++){
						updatesql = updatesql.replace("(reptable)", nbases[i]+setName);
//						dao.update(updatesql,values);
						dao.update(updatesql);
					}
				}else{
					String privKey=getFieldKey(setName);
					for(Object o:dataInfo){
						MorphDynaBean mbean = (MorphDynaBean)o;
						HashMap map = PubFunc.DynaBean2Map(mbean);
						String key=map.get("key").toString();
						key=PubFunc.decrypt(key);
						
						for(int i=0;i<nbases.length;i++){
							String sql = updatesql.replace("(reptable)", nbases[i]+setName);
							sql += " where "+privKey+" = '"+key+"' and i9999 = "+map.get("dataIndex").toString();
//							dao.update(sql,values);
							dao.update(sql);
						}
					}
				}
				this.getFormHM().put("flag",true);
			}
		}catch (Exception e) {
			this.getFormHM().put("flag",false);
			this.getFormHM().put("msg",e.toString().substring(e.toString().indexOf("description:")+12));
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取子集主键
	 * @param setName
	 * @return
	 */
	public String getFieldKey(String setName){
		String key = "A0100";
		if(setName.startsWith("A"))
			key = "A0100";
		else if(setName.startsWith("B"))
			key = "B0110";
		else if(setName.startsWith("K"))
			key = "E0122";
		else if(setName.startsWith("H"))
			key = "H0100";
		return key;
	}
	/**
	 * 数值类型进行转换
	 * @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'M':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
}
