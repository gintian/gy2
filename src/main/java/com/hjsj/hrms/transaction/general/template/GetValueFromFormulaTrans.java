package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplatePageBo;
import com.hjsj.hrms.businessobject.general.template.TemplateSetBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.ykcard.TRecParamView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:GetValueFromFormulaTrans.java</p>
 * <p>Description>:计算公式单元格</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2013-12-30 上午09:51:40</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class GetValueFromFormulaTrans extends IBusiness {
 
    public void execute() throws GeneralException {
        String fromflag=(String)this.getFormHM().get("fromflag");
        String tabid=(String)this.getFormHM().get("tabid");
        String tabname=(String)this.getFormHM().get("tabname");
        String ins_id=(String)this.getFormHM().get("ins_id");     
        String task_id=(String)this.getFormHM().get("task_id");     
        String pageid=(String)this.getFormHM().get("pageno");
        String gridid=(String)this.getFormHM().get("gridid");
        String infor_type=(String)this.getFormHM().get("infor_type");
        String inner_basepre=(String)this.getFormHM().get("inner_basepre");
        String inner_a0100=(String)this.getFormHM().get("inner_a0100");        
        inner_a0100 = SafeCode.decode(inner_a0100);
        inner_basepre = SafeCode.decode(inner_basepre);
        if (ins_id==null) ins_id="0";
        if (task_id==null) task_id="0";
        if (fromflag=="") fromflag="card"; 
        if ("myapply".equals(fromflag)){
          //  tabname= "g_templet_"+tabid;
            if ("".equals(inner_basepre)){                
                 inner_basepre = this.userView.getA0100();
                 inner_a0100 = this.userView.getDbname();
            }
            
        }
          
        try{  
            TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
   
            TemplatePageBo pagebo=new TemplatePageBo(this.getFrameconn(),Integer.parseInt(tabid),
                     Integer.parseInt(pageid),task_id);
            ArrayList celllist=pagebo.getAllCell();
            StringBuffer strSql= new StringBuffer();
            strSql.append("select * from ");
            strSql.append(tabname);
            strSql.append(" where ");
            if ("1".equals(infor_type)){
                strSql.append(" A0100= '");
                strSql.append(inner_a0100);
                strSql.append("'");
                strSql.append(" and upper(basepre)= '");
                strSql.append(inner_basepre.toUpperCase());   
                strSql.append("'");
            }
            else  if ("2".equals(infor_type)){
                strSql.append(" B0110= '");
                strSql.append(inner_a0100);
                strSql.append("'");
                   
            }
            else  if ("3".equals(infor_type)){
                strSql.append(" E02A1= '");
                strSql.append(inner_a0100);  
                strSql.append("'");
            }
            if (!"0".equals(ins_id)){
                strSql.append(" and  ins_id= ");
                strSql.append(ins_id);       
                
            }
            
            String cCalcValue = "";
            ContentDAO dao=new ContentDAO(this.getFrameconn());            
            this.frowset = dao.search(strSql.toString());            
            if (this.frowset.next()){             
               ArrayList recParam=new ArrayList();  
                double fValue = 0.0f;
                if (celllist.size() > 0) {
                    for (int i = 0; i < celllist.size(); i++) {
                        TemplateSetBo cell = (TemplateSetBo) celllist.get(i);
                        String flag =cell.getFlag();
                        String fldname =cell.getField_name();
                        String fldtype =cell.getField_type();
                        int chgstate = cell.getChgstate();   
                        if (!"V".equals(flag))
                          fldname =fldname+"_"+ String.valueOf(chgstate);
                        if ("N".equalsIgnoreCase(fldtype)) {
                            TRecParamView recP = new TRecParamView();
                            fValue = this.frowset.getDouble(fldname);
                            recP.setBflag(true);
                            recP.setFvalue(String.valueOf(fValue));
                            recP.setNid(cell.getGridno());
                            recParam.add(recP);
                        }
                    }
                    boolean bHaveCalcGrid=false;
                    TemplateSetBo cell = null;
                    for (int i = 0; i < celllist.size(); i++) {
                        cell = (TemplateSetBo) celllist.get(i);
                        if (("calcitem_"+String.valueOf(cell.getGridno())).equals(gridid)) {
                            bHaveCalcGrid= true;
                            break;
                        }
                    }
                    
                    if (bHaveCalcGrid){                 
                        String pattern = "###"; 
                        TSyntax tsyntax = new TSyntax();    
                        tsyntax.Lexical(cell.getFormula());
                        tsyntax.SetVariableValue(recParam);
                        tsyntax.DoWithProgram();
                        int decimal = cell.getDisformat();
                        pattern = "###"; //浮点数的精度
                        if (decimal > 0)
                            pattern += ".";
                        for (int i = 0; i < decimal; i++)
                            pattern += "0";
                        double dValue =0;
                        if (tsyntax.m_strResult != null && tsyntax.m_strResult.length() > 0)
                          dValue =Double.parseDouble(tsyntax.m_strResult);
                       // cCalcValue = new DecimalFormat(pattern).format(dValue);
                        cCalcValue = PubFunc.DoFormatDecimal(String.valueOf(dValue), decimal);
            
                    }
                }
            }
            if (cCalcValue == null || cCalcValue.length() <= 0)
                cCalcValue = "　";
            this.getFormHM().put("calcValue", cCalcValue);
            this.getFormHM().put("gridid", gridid);
        } catch (Exception e){            
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }    

    }

}
