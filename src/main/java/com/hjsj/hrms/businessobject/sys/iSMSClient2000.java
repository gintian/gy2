package com.hjsj.hrms.businessobject.sys;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class iSMSClient2000
{
    private Socket ClientSocket;
    private DataInputStream is;
    private DataOutputStream os;
    public String Error;
    public String Option;
    public String Record;
    public String Mobile;
    public String Content;
    public String DateTime;

    public iSMSClient2000()
    {
        Error = "";
        Option = "";
        Record = "";
        Mobile = "";
        Content = "";
        DateTime = "";
    }

    public boolean OpenSMS(String s, int i)
    {
        String s1 = "";
        boolean flag = false;
        try
        {
            if("".equals(s)) {
                s = "localhost";
            }
            if(i == 0) {
                i = 8090;
            }
            ClientSocket = new Socket(s, i);
            is = new DataInputStream(new BufferedInputStream(ClientSocket.getInputStream()));
            os = new DataOutputStream(new BufferedOutputStream(ClientSocket.getOutputStream()));
            String s2 = is.readLine();
            if("OK".equalsIgnoreCase(s2))
            {
                os.write("SMS\r\n".getBytes());
                flag = true;
            }
        }
        catch(Exception exception)
        {
            flag = false;
        }
        return flag;
    }

    public boolean SendSMS(String s, String s1, String s2)
    {
        boolean flag = false;
        try
        {
            byte abyte0[] = (s + "\r\n").getBytes();
            byte abyte1[] = (s1 + "\r\n").getBytes();
            byte abyte2[] = (s2 + "\r\n").getBytes();
            byte abyte3[] = new byte[3];
            abyte3[0] = 26;
            abyte3[1] = 13;
            abyte3[2] = 10;
            os.write(abyte3);
            os.write(abyte0);
            os.write(abyte1);
            os.write(abyte2);
            os.write(abyte3);
            os.flush();
            Error = is.readLine();
            if("OK".equalsIgnoreCase(Error)) {
                flag = true;
            } else {
                flag = false;
            }
        }
        catch(Exception exception)
        {
            flag = false;
        }
        return flag;
    }

    public String ReadSMS()
    {
        String s = "";
        try
        {
            byte abyte0[] = new byte[3];
            abyte0[0] = 27;
            abyte0[1] = 13;
            abyte0[2] = 10;
            os.write(abyte0);
            os.flush();
            s = is.readLine();
            Option = is.readLine();
            Record = is.readLine();
            Mobile = is.readLine();
            Content = is.readLine();
            DateTime = is.readLine();
            Error = is.readLine();
            if("OK".equalsIgnoreCase(Error)) {
                s = s;
            } else {
                s = "0";
            }
        }
        catch(Exception exception)
        {
            s = "0";
        }
        return s;
    }

    public boolean CloseSMS()
    {
        boolean flag = false;
        try
        {
            os.write("END\r\n".getBytes());
            ClientSocket.close();
            flag = true;
        }
        catch(Exception exception)
        {
            flag = false;
        }
        return flag;
    }

}