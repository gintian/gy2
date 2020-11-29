package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:GetInsertFormulaListTrans.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-9-11 14:10:43</p>
 * @author LiZhenWei
 * @version 4.0
 */

public class GetInsertFormulaListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			String fieldsetid=(String)this.getFormHM().get("formulafieldsetid");
			fieldsetid=SafeCode.decode(fieldsetid);
			ArrayList itemlist= new ArrayList();
			String formulatitle="";
			String formulatype="A";
			String formulalength="1";
			String integerdigit="8";
			String decimalfractiondigit="0";
			String formulacontent="";
			String dateFormat="1";
			String opt="";
			String nmodule="1";
			if(map!=null&&map.get("nmodule")!=null)
			{
				nmodule=(String)map.get("nmodule");
				map.remove("nmodule");
			}
			else
			{
				nmodule=(String)this.getFormHM().get("nmodule");
			}
			if(map!=null&&map.get("opt")!=null)
			{
				String maxid=(String)map.get("maxid");
				String tid=(String)map.get("tid");
				opt=(String)map.get("opt");//=1 edit,=0 new,=newcontent 修改新建模板公式
				if("1".equals(opt))
				{
					HashMap hm = bo.getFormulaInfo(maxid,tid);
				/*	map.put("title",rs.getString("fieldtitle")==null?"":rs.getString("fieldtitle"));
					map.put("content",Sql_switcher.readMemo(rs,"fieldcontent"));
					map.put("type",rs.getString("fieldtype"));
					map.put("ndec",rs.getString("ndec")==null?"":rs.getString("ndec"));
					map.put("format",rs.getString("dataformat"));
					map.put("len",rs.getString("fieldlen"));*/
					formulatitle=hm.get("title")==null?formulatitle:(String)hm.get("title");
					formulacontent=hm.get("content")==null?formulacontent:(String)hm.get("content");
					dateFormat=hm.get("format")==null?dateFormat:(String)hm.get("format");
					decimalfractiondigit=hm.get("ndec")==null?decimalfractiondigit:(String)hm.get("ndec");
					integerdigit=hm.get("len")==null?integerdigit:(String)hm.get("len");
					formulalength=hm.get("len")==null?formulalength:(String)hm.get("len");
					formulatype=hm.get("type")==null?formulatype:(String)hm.get("type");
				//修改新建模板公式  jingq add 2014.10.11
				} else if("newcontent".equals(opt)){
					String newcontent = (String) map.get("newcontent");
					/*以前前端发过来的数据是明文，后来改成密文，此处需解密数据 guodd 2018-03-30*/
					newcontent = SafeCode.decode(newcontent);
					String[] str = newcontent.split("`");
					//String[] str = newcontent.split(",");
					formulatitle = str[2];
					formulacontent = str[4];
					dateFormat = str[5];
					decimalfractiondigit = str[7];
					integerdigit = str[6];
					formulalength = str[6];
					formulatype = str[3];
				}
			}
			ArrayList fieldsetlist=null;
			if(!"2".equals(nmodule))
			{
				fieldsetlist= bo.getPersonSubset(1,this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET));
			}
			else
			{
				fieldsetlist=bo.getPrivSalarySetList(this.userView);
				fieldsetlist.add(0,new CommonData("-1","  "));
			}
			if(fieldsetid==null|| "".equals(fieldsetid))//初次进入
			{
                 String maxid=(String)map.get("maxid");
                 this.getFormHM().put("maxid",maxid);
			}else//改变子集id
			{
				fieldsetid=(String)this.getFormHM().get("formulafieldsetid");
				if(!"2".equals(nmodule))
			    	itemlist = bo.getFieldItemList(1,fieldsetid,this.userView);
				else
				{
					if("#".equals(fieldsetid))
					{
						fieldsetid="-1";
					}
					itemlist=bo.getSalaryItem(fieldsetid, this.userView);
				}
			}
			ArrayList dateFormatList=bo.getDateFormatList();
			ArrayList codefieldlist=new ArrayList();
			this.getFormHM().put("fieldsetlist",fieldsetlist);
			this.getFormHM().put("itemlist",itemlist);
			this.getFormHM().put("formulafieldsetid",(fieldsetid==null|| "".equals(fieldsetid))?"#":fieldsetid);
			this.getFormHM().put("dateFormatList",dateFormatList);
			this.getFormHM().put("codefieldlist",codefieldlist);
			this.getFormHM().put("formulatitle", formulatitle);
			this.getFormHM().put("formulatype", formulatype.toUpperCase().trim());
			this.getFormHM().put("formulalength", formulalength);
			this.getFormHM().put("integerdigit", integerdigit);     
			this.getFormHM().put("decimalfractiondigit", decimalfractiondigit); 
			this.getFormHM().put("formulacontent", formulacontent);   
			this.getFormHM().put("dateFormat", dateFormat);  
			this.getFormHM().put("nmodule", nmodule);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
