/**
 * 
 */
package com.hjsj.hrms.test;

import com.hjsj.hrms.utils.PubFunc;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.TIFFDecodeParam;

import javax.imageio.ImageIO;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.widget.ScrollingImagePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-12:下午12:06:41</p> 
 *@author cmq
 *@version 4.0
 */
public class TestTiff extends Frame {
	   ScrollingImagePanel panel;

	    public TestTiff(String filename) throws IOException {

	        setTitle("Multi page TIFF Reader");
	        filename="D:\\tomcat5.5\\temp\\e_archive51304.tif";
	        File file = new File(filename);
	       

            int len;
            FileInputStream in=new FileInputStream(new File(filename));
            byte buff[] = new byte[1024];
            
            ByteArrayOutputStream out0 = new ByteArrayOutputStream(); 
            try{
                while((len   =   in.read(buff))   !=   -1){ 
                	out0.write(buff, 0, len); 
                    //String   str   =   new   String(buff,0,len); 
                    //buf.append(str);
                }
            }finally{
                PubFunc.closeResource(in);
            }
            byte[] ss=out0.toByteArray();    //buf.toString().getBytes();	        
            out0.close();
	        HashMap map=new HashMap();
	        map.put("aa", ss);
	        byte[] ss1=(byte[])map.get("aa");
			ByteArrayInputStream   in1   = new ByteArrayInputStream(ss1);   
			/*
            ByteArrayOutputStream out1 = new ByteArrayOutputStream(); 
            byte[] bytes = new byte[1024]; 
            while(in1.read(bytes) != -1) 
            {                
            	out1.write(bytes);            
            }            
            out1.close();             
	        */
            

	        TIFFDecodeParam param = new TIFFDecodeParam();
	       

	        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", in1/*s*/, param);

	        System.out.println("Number of images in this TIFF: " +
	                           dec.getNumPages());

	        // Which of the multiple images in the TIFF file do we want to load
	        // 0 refers to the first, 1 to the second and so on.
	        int imageToLoad = 0;

	        RenderedImage op =
	            new NullOpImage(dec.decodeAsRenderedImage(imageToLoad),
	                            null,
	                            OpImage.OP_IO_BOUND,
	                            null);

	        // Display the original in a 800x800 scrolling window
	        panel = new ScrollingImagePanel(op, 400, 400);
	        panel.setSize(400,400);
	        add(panel);
	    }

	    public static void main(String [] args) {

	       String filename = "";

	        try {
	        	addwater();
	        	//TestTiff window = new TestTiff(filename);
	            //window.pack();
	            //doitJAI();
	            //window.show();
	        } catch (java.io.IOException ioe) {
	            System.out.println(ioe);
	        }
	    }
	    
	    public static void addwater()throws IOException {
	        RenderedImage   rendImage;
	        FileOutputStream out = null;
	        try{ 
	            BufferedImage src = ImageIO.read(new File("d:\\4.jpg")); // 读入文件
	            src = PubFunc.addCombine("aadfasdfasdfadfa",src,10,100,1,1);
	            //ImageIO.write(rendImage,   "jpg",   "E:/temp/Ajax/12ok.jpg");
	            out=new FileOutputStream("d:\\water.jpg"); //输出到文件流
	            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
	            encoder.encode(src);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally {
				PubFunc.closeIoResource(out);
			}

	    }
	 
	    public static void  doitJAI() throws IOException {
	    	/**
	        FileSeekableStream ss = new  FileSeekableStream("D:\\tomcat5.5\\temp\\e_archive41258.tif");
	        
	        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", ss, null);
	        int count = dec.getNumPages();
	        TIFFEncodeParam param = new TIFFEncodeParam();
	        param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
	        param.setLittleEndian(false); // Intel
	        
	       // System.out.println("This TIF has " + count + " image(s)");
	        
	       // BufferedImage watermark=new BufferedImage(20,30,BufferedImage.TYPE_INT_RGB);
	       // ImageIcon imgicon=new ImageIcon("D:\\tomcat5.5\\webapps\\hrms\\images\\mainbg.jpeg");
	     
	        ArrayList list=new ArrayList();
	        ByteArrayOutputStream byteOutput=null;
	        
	        for (int i = 0; i < count; i++) {
	            RenderedImage page = dec.decodeAsRenderedImage(i);
	            /
	            File f = new File("d:/single_" + i + ".tif");
	            System.out.println("Saving " + f.getCanonicalPath());
	            ParameterBlock pb = new ParameterBlock();
	            pb.addSource(page);
	            pb.add(f.toString());
	            pb.add("tiff");
	            pb.add(param);
	            RenderedOp r = JAI.create("filestore",pb);
	            r.dispose();
	       		 
	            //bi2=ImageIO.read(page.)
	            FileOutputStream fout = new java.io.FileOutputStream("d:\\i"+i+".jpeg");
	            BufferedImage image=new BufferedImage(page.getWidth(),page.getHeight(),BufferedImage.TYPE_INT_RGB);
	            
	            Graphics2D g=image.createGraphics();
	            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
	            g.drawRenderedImage(image,AffineTransform.getScaleInstance(0.32,0.33));
	            //JAI.create("filestore",page ,fout,"JPEG");
	           
	           // g.drawImage(imgicon.getImage(),0,0,null);
	           // g.dispose();	

		        
				byteOutput=new ByteArrayOutputStream();
				ImageIO.write(page, "TIFF", byteOutput);
				list.add(byteOutput.toByteArray());
		        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fout);
		        encoder.encode(image);
		        fout.close();
	            //list.add(page);
	        }
	    */
	        
	        
	    }
	   
	    
}
