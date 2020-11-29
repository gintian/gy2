package com.hjsj.hrms.transaction.kq.options.machine;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class EditKqMachineTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

        String e_flag = (String) this.getFormHM().get("e_flag");

        checkKqMachineType();

        String location_id = (String) this.getFormHM().get("location_id");
        RecordVo vo = new RecordVo("kq_machine_location");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try
        {
            if (e_flag == null || e_flag.length() <= 0)
            {
                e_flag = "add";
            }
            if ("up".equalsIgnoreCase(e_flag))
            {
                vo.setString("location_id", location_id);
                vo = dao.findByPrimaryKey(vo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.machine.error"), "", ""));
        }
        this.getFormHM().put("machine", vo);
        this.getFormHM().put("location_id", location_id);
        this.getFormHM().put("e_flag", e_flag);
        this.getFormHM().put("typelist", getTypeList());

    }

    public ArrayList getTypeList()
    {        
        String sql = "select type_id,name from kq_machine_type where type_id<>7 order by name";
        
        ArrayList list = new ArrayList();
        try
        {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            this.frowset = dao.search(sql);
            while (this.frowset.next())
            {
                CommonData dataobj = new CommonData();
                dataobj.setDataName(this.frowset.getString("name"));
                dataobj.setDataValue(this.frowset.getString("type_id"));
                list.add(dataobj);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }

    private void checkKqMachineType()
    {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        
        for (int i = 0; i <= 12; i++)
        {
            String type_id = Integer.toString(i);

            String sql = "select type_id,name from kq_machine_type where type_id='" + type_id + "'";
            String insert = "insert into kq_machine_type (type_id,name) values (?,?)";
            
            ArrayList list = new ArrayList();
            try
            {
                //检查机型是否存在
                this.frowset = dao.search(sql);
                if (this.frowset.next())
                    continue;
                
                //不存在，则新增
                list.add(Integer.toString(i));
                String typeName = getMachineTypeName(i);
                list.add(typeName);

                dao.insert(insert, list);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private String getMachineTypeName(int typeId)
    {
       String typeName = "";
       switch (typeId)
       {
           case 0:
               typeName = "自定义接口";
               break;
           case 1:
               typeName = "科密326";
               break;
           case 2:
               typeName = "舒特系列";
               break;
           case 3:
               typeName = "舒特10位";
               break;
           case 4:
               typeName = "华达拉斯485";
               break;
           case 5:
               typeName = "中控指纹机";
               break;
           case 6:
               typeName = "科密KD";
               break;
           case 7:
               typeName = "GS系列";
               break;
           case 8:
               typeName = "立方考勤机";
               break;
           case 9:
               typeName = "披克考勤机";
               break;
           case 10:
               typeName = "汉王考勤机";
               break;
           case 11:
               typeName = "威尔考勤机";
               break;
           case 12:
               typeName = "中控指纹机SSR";
               break;
       }       
       return typeName;
    }
}
