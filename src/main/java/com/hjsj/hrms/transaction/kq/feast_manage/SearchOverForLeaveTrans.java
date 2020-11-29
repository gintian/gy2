package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.feast_manage.FeastComputer;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SearchOverForLeaveTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String a_code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
        String kind="";
        String code="";
        if(a_code==null||a_code.length()<=0)
        {
            String privcode=RegisterInitInfoData.getKqPrivCode(userView);
            if("UN".equalsIgnoreCase(privcode))
                kind="2";
            else if("UM".equalsIgnoreCase(privcode))
                kind="1";
            else if("@K".equalsIgnoreCase(privcode))
                kind="0";
            code=RegisterInitInfoData.getKqPrivCodeValue(userView);
        }else
        {
            if(a_code.indexOf("UN")!=-1)
            {
                kind="2";
            }else if(a_code.indexOf("UM")!=-1)
            {
                kind="1";
            }else if(a_code.indexOf("@K")!=-1)
            {
                kind="0";
            }
            code=a_code.substring(2);
        }
        
		ArrayList fielditemlist = DataDictionary.getFieldList("Q33",Constant.USED_FIELD_SET);;
		for (int i = 0; i < fielditemlist.size(); i++) {
			FieldItem fieldItem = (FieldItem) fielditemlist.get(i);
			if ("nbase".equals(fieldItem.getItemid())) {
				fieldItem.setCodesetid("@@");
			}
			
			if ("q3305".equals(fieldItem.getItemid()) ||
					"q3307".equals(fieldItem.getItemid()) ||
					"q3309".equals(fieldItem.getItemid())){
					fieldItem.setDecimalwidth(1);//单位小时 显示一位小数
			}
		}
		FeastComputer feastComputer=new FeastComputer(this.getFrameconn(),this.userView);
		ArrayList fieldlist=FeastComputer.newFieldItemList(fielditemlist);
		
		fieldlist.remove(0);//去掉单据序号
		

		String columns=feastComputer.getColumn(fieldlist);
		String strsql=getQueryString(code,kind,columns);
		
		this.getFormHM().put("strsql",strsql);
		this.getFormHM().put("strsql_encode", SafeCode.encode(strsql));
		this.getFormHM().put("columns",columns);
		this.getFormHM().put("fieldlist",fieldlist);
		
	}
	
    /**
     * 根据code，获得组织机构的codeset
     * @param code
     * @return
     */
    private String getCodeSetByCode (String code) {
    	String codeSet = "";
    	String sql = "select codesetid from organization where codeitemid=?";
    	try(PreparedStatement pst=this.frameconn.prepareStatement(sql)) {
    		pst.setString(1, code);
    		this.frecset = pst.executeQuery();
    		if (this.frecset.next()) {
    			codeSet = this.frecset.getString("codesetid");
    			if (codeSet == null) {
    				codeSet = "";
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return codeSet;
    }
    
    private String getQueryString(String code,String kind,String columns){
    	Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		String current_date = sdf.format(currentDate);
		UpdateQ33 updateQ33 = new UpdateQ33(userView, frameconn);
		
		KqOverTimeForLeaveBo kqOverTimeForLeave = new KqOverTimeForLeaveBo(this.frameconn, this.userView);
        HashMap period = kqOverTimeForLeave.getEffectivePeriod();
        String start_d = ((String)period.get("from")).replaceAll("-", ".");
        String end_d = ((String)period.get("to")).replaceAll("-", ".");
        
        StringBuffer strsql = new StringBuffer();
        ArrayList kq_dbase_list = new ArrayList();
        KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		try {
			kq_dbase_list = kqUtilsClass.setKqPerList(code,kind);
			String select_pre = (String)this.getFormHM().get("select_pre");
			String select_name = (String)this.getFormHM().get("select_name");
			String select_sturt = (String)this.getFormHM().get("select_sturt");
			this.getFormHM().remove("select_name");
	        ArrayList dblist=new ArrayList();
			if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
			{
				dblist.add(select_pre);
			}else
			{
				dblist=kq_dbase_list;
			}
			if(dblist.size()<=0||dblist==null)
		    {   
				
		    }else
		    {
		    	
				String where_c=kqUtilsClass.getWhere_C(select_sturt,"a0101",select_name);
				for (int i = 0; i < dblist.size(); i++) {
					String nbase=(String)dblist.get(i);
					String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
					strsql.append("select "+i+" as i,");
					strsql.append("nbase,a0100,b0110,e0122,e01a1,a0101,q3303,");
					strsql.append("ROUND(q3305/60.0,1) as q3305,ROUND(q3307/60.0,1) as q3307,ROUND(q3309/60.0,1) as q3309");
					strsql.append(" from q33");					
					strsql.append(" where ");
					if("1".equals(kind))
					{
						strsql.append("e0122 like '"+code+"%'");
					}else if("0".equals(kind))
					{
						strsql.append("e01a1 like '"+code+"%'");
					}else
					{
						strsql.append("b0110 like '"+code+"%'");
					}
					strsql.append(" and nbase='"+nbase+"'");
					strsql.append(" and q3303 >= '" + start_d + "' and q3303 <= '" + end_d +"' ");
					strsql.append(" and a0100 in(select a0100 "+whereIN+")");
					if (null != where_c && where_c.length()>0) {
						strsql.append(where_c);
					}
					strsql.append(" UNION ");
				}
				strsql.setLength(strsql.length()-7);
		    }
			
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return strsql.toString();
    }
}
