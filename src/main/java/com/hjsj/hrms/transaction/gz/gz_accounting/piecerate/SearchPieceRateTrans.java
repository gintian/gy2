package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceRateBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.*;

public class SearchPieceRateTrans extends IBusiness {

	public void execute() throws GeneralException {
		List rs=null;
		String s0100="";
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ArrayList FielditemList = new ArrayList();
		ArrayList datalist = new ArrayList();
		String starttime=(String)this.getFormHM().get("starttime");
		String endtime=(String)this.getFormHM().get("endtime");
		s0100=(String)this.getFormHM().get("s0100");
		if (s0100==null){
			s0100="";
		}
		
		if(starttime==null||starttime.length()<=0||endtime==null||endtime.length()<=0){
		Calendar cal = Calendar.getInstance();   
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);		
		cal.set(year, month, day);  
		cal.add(Calendar.MONTH, -1);//取前一个月的同一天   
		Date date = cal.getTime();   
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");   
		starttime = formatter.format(date);  //上个月的同一天 
		endtime=PubFunc.getStringDate("yyyy-MM-dd");//当前日期	
		}
		boolean flag = false;
		boolean flag1 = false;
		String s0102 = "";
		String sp_flag = "";
		try{
			String start_date = (String) hm.get("start_date");
			String end_date = (String) hm.get("end_date");
			String tasktype = (String) hm.get("tasktype");
			String sp_status = (String) hm.get("sp_status");
			if(start_date!=null){
				starttime=start_date;
				flag = true;
				flag1 = true;
				hm.remove("start_date");
			}else{
				this.getFormHM().put("tasktype", "00");
				this.getFormHM().put("sp_status", "all");
			}
			if(end_date!=null){
				endtime=end_date;
				hm.remove("end_date");
			}
			if(tasktype!=null){
				if("00".equals(tasktype)){
					flag = false;
				}else{
					s0102=tasktype;
				}
				hm.remove("tasktype");
			}
			if(sp_status!=null){
				if("all".equals(sp_status)){
					flag1 = false;
				}else{
					sp_flag=sp_status;
				}
				hm.remove("sp_status");
			}
			
			StringBuffer strsql = new StringBuffer();
			strsql.append("select * from S01 where s0104 between "+Sql_switcher.dateValue(starttime)+"and "+Sql_switcher.dateValue(endtime)+"");
			if(flag){
				strsql.append(" and s0102 = '"+s0102+"'");
			}
			if(flag1){
				strsql.append(" and sp_flag = '"+sp_flag+"'");
			}
			int number = 1;
			FielditemList=DataDictionary.getFieldList("S01",Constant.USED_FIELD_SET);
			PieceRateBo TaskBo= new PieceRateBo(this.frameconn,"",this.userView);
			String s=TaskBo.getPrivB0110();			
			if (!"".equals(s)){
				s=s.replace("b0110", "s0105");
				s=s.replace("e0122", "s0105");
				strsql.append(" and "+s);	
			}
			
			try {
				 strsql.append(" order by s0104 desc,s0100 desc");
		    	 rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn()); 
		    	 String Tops0100="";
		      	 for(int r=0;!rs.isEmpty() && r<rs.size();r++)
			     {
		   	 	     LazyDynaBean rec=(LazyDynaBean)rs.get(r);	
				     RecordVo vo=new RecordVo("S01",1);
				     if ("".equals(Tops0100)){
				    	 Tops0100=(String)rec.get("s0100");
				     }
				     if (((String)rec.get("s0100")).equals(s0100)){
				    	 Tops0100=s0100; 
				     }
				     if(!FielditemList.isEmpty())                         //字段s
				     {
				     	for(int i=0;i<FielditemList.size();i++)
				     	{
				     		FieldItem fielditem=(FieldItem)FielditemList.get(i);
				     		if(!"0".equals(fielditem.getCodesetid())) 
				     		{
				     			String codevalue=rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"";  
				     			String codesetid=fielditem.getCodesetid();
				     			if(codevalue !=null && codevalue.trim().length()>0 && codesetid !=null && codesetid.trim().length()>0)
				     			{
			     					String value=AdminCode.getCode(codesetid,codevalue)!=null && AdminCode.getCode(codesetid,codevalue).getCodename()!=null
			     					             ?AdminCode.getCode(codesetid,codevalue).getCodename():"";
								    vo.setString(fielditem.getItemid(),value);
				     			}	
							    else
							    	vo.setString(fielditem.getItemid(),"");
				     		}else
				     		{
					     		if("D".equals(fielditem.getItemtype()))                               //日期类型的有待格式化处理
					     		{
					     		    String value =rec.get(fielditem.getItemid().toLowerCase()).toString();
					     			if(value!=null && value.length() >=19 && fielditem.getItemlength()>=18)
					     				vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,value.substring(0,19)));
					     			else if(value!=null && value.length()>=10 && fielditem.getItemlength()>=10)
					     				vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,value.substring(0,10)));
		                            else if(value!=null && value.length()>=10 && fielditem.getItemlength()==4)
		                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,value.substring(0,4)));
		                            else if(value!=null && value.length()>=10 && fielditem.getItemlength()==7)
		                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,value.substring(0,7)));
		                            else
		                            	vo.setString(fielditem.getItemid().toLowerCase(),"");
					     		}else if("N".equals(fielditem.getItemtype()))                        //数值类型的
					     		{
					     			vo.setString(fielditem.getItemid(),PubFunc.DoFormatDecimal(rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"",fielditem.getDecimalwidth()));
					     		}else if("M".equals(fielditem.getItemtype()))
					     		{
					     			String text_m=(String)rec.get(fielditem.getItemid());	
					     			vo.setString(fielditem.getItemid(),text_m);
					     		}else                                                               //其他字符串类型
					     		{
					     			vo.setString(fielditem.getItemid(),rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"");
					     		}
				     		}
				     	}
				     }	
				     number++;
				     datalist.add(vo);
				  }
		      	s0100=Tops0100;
		      	FielditemList=getDispFieldList(FielditemList);	
		      	s=TaskBo.getMangerPriv();			
		      	this.getFormHM().put("managerpriv",s);
		     
			} 
			finally
			{   
				this.getFormHM().put("s0100", s0100);
				this.getFormHM().put("datalist", datalist);
				this.getFormHM().put("fielditemlist", FielditemList);
				this.getFormHM().put("tasktypelist", GetCodeItemList());
				this.getFormHM().put("count", String.valueOf(number-1));
				this.getFormHM().put("starttime",starttime);
				this.getFormHM().put("endtime",endtime);
				this.getFormHM().put("taskeditmodel","");
				
			}

		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
	
    private ArrayList getDispFieldList(ArrayList list)
    {
        if(list == null)  return null;
        FieldItem item = null;
        String fieldname = null;
        ArrayList Displist = new ArrayList();
        String s=",S0100,USERFLAG,CURR_USER,APPUSER,APPPROCESS,";
        for(int i = 0; i < list.size(); i++)
        {
            item = (FieldItem)list.get(i);
            item = (FieldItem)item.cloneItem();
            fieldname = ","+item.getItemid().toUpperCase()+",";
            if (s.indexOf(fieldname)<0){
            	Displist.add(item);	
            }            
        }
        return Displist;
    }
    
	public ArrayList GetCodeItemList(){
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList fieldList = new ArrayList();
		String sqlstr = "select * from codeitem where codesetid ='71' and invalid =1 order by codeitemid";
		ArrayList dylist = null;
		CommonData obj1=new CommonData("00","所有");
		fieldList.add(obj1);
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("codeitemid").toString();
				String itemdesc = dynabean.get("codeitemdesc").toString();
				CommonData dataobj = new CommonData(itemid,itemdesc);
				fieldList.add(dataobj);
			}
		} catch(GeneralException e) {
			e.printStackTrace();
		}
		return fieldList;
	}	

    

}
