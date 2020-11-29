package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class WorkTypeJiexiTrans extends IBusiness {

	public void execute() throws GeneralException {
	    try {
    		String temp=(String)this.getFormHM().get("temp");
    		String trigger=(String)this.getFormHM().get("trigger");
    		//后台作业设置时间报错 此处将｜转为|,jingq add 2014.09.19
    		temp = PubFunc.keyWord_reback(temp);
    		if("1".equals(trigger)){
    			String[] Temp = temp.split(" ");
    			String temp1 = ""; 
    			String temp2 = "";
    			String temp3 = "";
    			if((Temp[2].indexOf("-")==-1&&Temp[2].indexOf("*")==-1&&Temp[2].indexOf("/")==-1)
    			        && (Temp[1].indexOf("-")==-1&&Temp[1].indexOf("*")==-1&&Temp[1].indexOf("/")==-1)){
    				temp1 = (Integer.parseInt(Temp[0])+100)+"";
    				temp2 = (Integer.parseInt(Temp[1])+100)+"";
    				temp3 = (Integer.parseInt(Temp[2])+100)+"";
    			}else{
    				temp1 = Temp[0];
    				temp2 = Temp[1];
    				temp3 = Temp[2];
    			}
    			String temp4 = Temp[3];
    			String temp5 = Temp[4];
    			String temp6 = Temp[5];
    			String temp7 = "";
    			if(Temp.length>6){
    				temp7 = Temp[6];
    			}
    			
    			this.getFormHM().put("temp1",temp1);
    			this.getFormHM().put("temp2",temp2);
    			this.getFormHM().put("temp3",temp3);
    			this.getFormHM().put("temp4",temp4);
    			this.getFormHM().put("temp5",temp5);
    			this.getFormHM().put("temp6",temp6);
    			this.getFormHM().put("temp7",temp7);
    		}else{
    			String [] Temp = temp.split("\\|");
    			String temp1 = Temp[0];
    			String temp2 = Temp[1];
    			if(" ".equals(temp2)){
    				temp2 = "0";
    			}
    			String temp3 = Temp[2];
    			String temp4 = Temp[3];
    			this.getFormHM().put("temp1",temp1);
    			this.getFormHM().put("temp2",temp2);
    			this.getFormHM().put("temp3",temp3);
    			this.getFormHM().put("temp4",temp4);
    		}
    	} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
	}
}
