package com.hjsj.hrms.transaction.lawbase;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class EditFileIndexTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
        String arrId = (String)this.getFormHM().get("arrayId");
        String arrName = (String)this.getFormHM().get("arrayName");
        String arrayId [] = arrId.split("`");
        String arrayName [] = arrName.split("`");
        ArrayList list = new ArrayList();
        for(int i=0;i<arrayId.length;i++)
        {
          CommonData dataobj = new CommonData(arrayId[i],arrayName[i]);
          list.add(dataobj);
        }
        this.getFormHM().put("queryfieldlist", list);

    }

}
