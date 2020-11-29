package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
public class CheckProjectStrTrans extends IBusiness {
	String fieldsetid="";
	public void execute() throws GeneralException {
		String c_expr = (String) this.getFormHM().get("c_expr");
		c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
		
		String itemid = (String) this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		String salaryid = (String) this.getFormHM().get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		
		String gz_module = (String) this.getFormHM().get("gz_module");
		gz_module=gz_module!=null&&gz_module.trim().length()>0?gz_module:"";
		
		String clsflag =(String) this.getFormHM().get("clsflag");
		clsflag=clsflag!=null&&clsflag.trim().length()>0?clsflag:"";
		
		fieldsetid= (String) this.getFormHM().get("fieldsetid");
		fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
		
		FieldItem fielditem = DataDictionary.getFieldItem(itemid);
		String type="";
		if(fielditem!=null){
			type = fielditem.getItemtype();
		}else if(!"".equals(salaryid)){
			if("A00Z0".equalsIgnoreCase(itemid)||"A00Z2".equalsIgnoreCase(itemid))
				type="D";
			else if("A00Z1".equalsIgnoreCase(itemid)||"A00Z3".equalsIgnoreCase(itemid))
				type="N";
		}
		type=type!=null&&type.trim().length()>0?type:"L";
		if("1".equals(clsflag)){
			type="N";
		}
		c_expr=SafeCode.decode(c_expr);
		c_expr=PubFunc.keyWord_reback(c_expr);
		String itemtype = (String)this.getFormHM().get("itemtype");
		itemtype=itemtype!=null&&itemtype.trim().length()>0?itemtype:"";
		this.formHM.remove("itemtype");
		
		String itemsetid = (String)this.getFormHM().get("itemsetid");
		itemsetid=itemsetid!=null&&itemsetid.trim().length()>0?itemsetid:"";
		this.formHM.remove("itemsetid");
		//System.out.println(c_expr);
		
		String flag = "";
		if (c_expr != null && c_expr.length() > 0) {
		    if("S05".equalsIgnoreCase(itemsetid)){//计件薪资慢，不需要薪资指标，把salaryid设为0 2014-03-04
		        salaryid="0";
		    }
		    
		    ArrayList fieldlist = new ArrayList();
		    if(StringUtils.isNotBlank(salaryid))//薪资总额公式不需要临时变量，没有传salaryid，这里加上判断
				fieldlist = getMidVariableList(salaryid);
			
		//	if(fielditem!=null)
		//		fieldlist.addAll( this.userView.getPrivFieldList(fielditem.getFieldsetid(), Constant.UNIT_FIELD_SET));
			ArrayList alUsedFields =(ArrayList)DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET).clone();
			if(fieldlist.size()>0){
			//	alUsedFields.addAll(fieldlist);
				
			}
			
			if((salaryid.length()==0) || ("S05".equalsIgnoreCase(itemsetid))){
				fieldlist.addAll(alUsedFields);
				if(itemsetid.length()>0&&!"gz_amount".equalsIgnoreCase(itemsetid))
					fieldlist.addAll(DataDictionary.getFieldList(itemsetid, 1));
			}
			
			if(itemtype.length()>0){
				FieldItem item = new FieldItem();
				item.setItemid(itemid);
				item.setItemtype(itemtype);
				fieldlist.add(item);
				type=itemtype;
			}
			
			if("P04".equalsIgnoreCase(itemsetid)){//业务字典目标卡任务表默认评分指标 xuj 2011-9-15
				FieldItem item = new FieldItem();
				item.setItemid("task_score");
				item.setItemdesc("评分");
				item.setItemtype("N");
				item.setItemlength(10);
				item.setDecimalwidth(4);
				item.setCodesetid("0");
				fieldlist.add(item);
			}
			
			if("S05".equalsIgnoreCase(itemsetid)){//s05子集前面加固定指标s0102  zhaoxg 2013-1-5
				FieldItem item = new FieldItem();
				item.setItemid("S0102");
				item.setItemdesc("计件作业类别");
				item.setItemtype("A");
				item.setItemlength(10);
				item.setDecimalwidth(0);
				item.setCodesetid("0");
				fieldlist.add(item);
			}
			//System.out.println("ok...");
			
			// YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型
			
			if(gz_module!=null && "3".equals(gz_module)){
				//精算报表
				fieldlist = DataDictionary.getFieldList("U02",
						Constant.USED_FIELD_SET);
				FieldItem item = new FieldItem();
				item.setItemid("escope");
				item.setItemdesc("人员范围");
				item.setItemtype("A");
				item.setCodesetid("61");
				fieldlist.add(item);
			}
			
			if("gz_amount".equalsIgnoreCase(itemsetid))//薪资总额计算公式校验 20170308 dengcan
			{
				fieldlist=getGzFieldList();
			}
			
			YksjParser yp = new YksjParser(getUserView(), fieldlist, YksjParser.forSearch, getColumType(type)
					, YksjParser.forPerson,"Ht", "");
			
			//System.out.println("ok1...");
			
			yp.setCon(this.getFrameconn());
			boolean b = false;
			try{
				b = yp.Verify_where(c_expr.trim());
			}catch (Exception e) {
				e.printStackTrace();
				
				b = false;
			}

			if (b) {// 校验通过
				flag="ok";
			}else{
				flag = yp.getStrError();
			} 
		}else{
			flag="ok";
		}
		this.getFormHM().put("info",SafeCode.encode(flag));
	}
	/**
	 * 设置Field的数据类型
	 * @param type  数据类型
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	public int getColumType(String type){
		int temp=1;
		if("A".equals(type)){
			temp=YksjParser.STRVALUE;
		}else if("D".equals(type)){
			temp=YksjParser.DATEVALUE;
		}else if("N".equals(type)){
			temp=YksjParser.FLOAT;
		}else if("L".equals(type)){
			temp=YksjParser.LOGIC;
		}else{
			temp=YksjParser.STRVALUE;
		}
		return temp;
	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getMidVariableList(String salaryid){
		ArrayList fieldlist=new ArrayList();
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct cname,chz,ntype, ");
			buf.append(Sql_switcher.sqlToChar("cvalue"));
			buf.append(" as cvalue,fldlen,flddec,codesetid from ");
			if("-1".equals(salaryid)){
				buf.append(" midvariable where nflag=4 and templetid=0 ");
				buf.append(" and cstate=-1");
			}else if("-2".equals(salaryid)){//xcs modify @ 2013-8-6 处理数据采集的计算公式定义
				buf.append(" midvariable where nflag=5 and templetid=0 ");
			}else{
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				buf.append(" and (cstate is null or cstate in ( ");//update by xiegh on 20170927 bug31779
				buf.append(salaryid);
				buf.append(" ))");
			}
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(rset.getString("cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					item.setCodesetid("0");
					break;
				case 2:
					item.setItemtype("A");
					item.setCodesetid("0");
					break;
				case 4:
					item.setItemtype("A");
					item.setCodesetid(rset.getString("codesetid"));
					break;
				case 3:
					item.setItemtype("D");
					item.setCodesetid("0");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			if(!"-1".equals(salaryid)){
				if("-2".equals(salaryid)){
					String sqlstr="select * from fielditem ";
					if(fieldsetid!=null&&fieldsetid.trim().length()>0){
						sqlstr+=" where fieldsetid='"+fieldsetid+"'";
					}
					rset=dao.search(sqlstr);
					while(rset.next()){
						FieldItem item=new FieldItem();
						item.setItemid(rset.getString("ITEMID"));
						item.setItemdesc(rset.getString("ITEMDESC"));
						item.setFieldsetid(rset.getString("FIELDSETID"));
						item.setItemlength(rset.getInt("ITEMLENGTH"));
						item.setFormula(Sql_switcher.readMemo(rset, "AuditingFormula"));
						item.setDecimalwidth(rset.getInt("DECIMALWIDTH"));
						item.setItemtype(rset.getString("ITEMTYPE"));
						item.setCodesetid(rset.getString("CODESETID"));
						item.setVarible(0);
						fieldlist.add(item);
					}
				}else{
					String sqlstr = "select  distinct ITEMID,ITEMDESC,FIELDSETID,ITEMLENGTH,"+Sql_switcher.sqlToChar("FORMULA")+" as FORMULA,DECWIDTH,ITEMTYPE,CODESETID from salaryset";
					if(salaryid!=null&&salaryid.trim().length()>0){
						sqlstr+=" where salaryid in ( "+salaryid+")";
					}
					rset=dao.search(sqlstr);
					while(rset.next()){
						FieldItem item=new FieldItem();
						item.setItemid(rset.getString("ITEMID"));
						item.setItemdesc(rset.getString("ITEMDESC"));
						item.setFieldsetid(rset.getString("FIELDSETID"));
						item.setItemlength(rset.getInt("ITEMLENGTH"));
						item.setFormula(rset.getString("FORMULA"));
						item.setDecimalwidth(rset.getInt("DECWIDTH"));
						item.setItemtype(rset.getString("ITEMTYPE"));
						item.setCodesetid(rset.getString("CODESETID"));
						item.setVarible(0);
						fieldlist.add(item);
					}
				}
				
			}else{
				String salaryAmountSet = "";
				GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
				HashMap hm = bo.getValuesMap();
				if (hm != null)
					salaryAmountSet = (String) hm.get("setid")==null?"":(String) hm.get("setid");
				ArrayList tempList = DataDictionary.getFieldList(salaryAmountSet, Constant.USED_FIELD_SET);
				int n = tempList.size();
				for(int i=0;i<n;i++){
					FieldItem item = (FieldItem)tempList.get(i);
//					item.setItemid(item.getItemid());
//					item.setItemdesc(item.getItemdesc());
//					item.setFieldsetid(item.getFieldsetid());
//					item.setItemlength(item.getItemlength());
//					item.setFormula(item.getFormula());
//					item.setDecimalwidth(item.getDecimalwidth());
//					item.setItemtype(item.getItemtype());
//					item.setCodesetid(item.getCodesetid());
					item.setVarible(0);
					fieldlist.add(item);
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return fieldlist;
	}
	
	/**
	 * 查询薪资类别中的指标列表
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getGzFieldList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			//20141031 dengcan  客户薪资类别500多个，表中薪资项19000多个，严重影响效率
	//		buf.append("select fieldsetid,itemid,itemdesc,itemtype,itemlength,nwidth,decwidth,codesetid,formula  from salaryset ");
	//		buf.append(" order by sortid");
			buf.append("select distinct fieldsetid,itemid,itemdesc,itemtype,itemlength,nwidth,decwidth,codesetid   from salaryset ");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(buf.toString());
			HashMap fieldItemMap=new HashMap();
			while(rset.next())
			{
				if(fieldItemMap.get(rset.getString("itemid"))==null)
				{
					FieldItem item=new FieldItem();
					item.setFieldsetid(rset.getString("fieldsetid"));
					item.setItemid(rset.getString("itemid"));
					item.setItemdesc(rset.getString("itemdesc"));
					item.setItemtype(rset.getString("itemtype"));
					item.setItemlength(rset.getInt("itemlength"));
					item.setDisplaywidth(rset.getInt("nwidth"));
					item.setDecimalwidth(rset.getInt("decwidth"));
					item.setCodesetid(rset.getString("codesetid"));
			//		item.setFormula(Sql_switcher.readMemo(rset,"formula"));
					item.setVarible(0);
					fieldlist.add(item);
					fieldItemMap.put(rset.getString("itemid"), "1");
				}
			}//while loop end.
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return fieldlist;
	}
	
	
}
