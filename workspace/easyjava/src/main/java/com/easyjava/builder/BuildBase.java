package com.easyjava.builder;

import java.io.*;

public class BuildBase {
    private static Logger logger=LoggerFactory.getLogger(BuilderBase.class);
    public static void execute(){

    }

    private static void build(String fileName, String outPutPath){
        File folder=new File(outPutPath);
        if(!folder.exists()){
            folder.mkdir();
        }

        File javaFile=new File(outPutPath, fileName+".java");

        OutputStream out=null;
        OutputStreamWriter outw=null;
        BufferedWriter bw=null;

        InputStream in =null;
        InputStreamReader inr=null;
        BufferedReader bf=null;


        try {
            out=new FileOutputStream(javaFile);
            outw=new OutputStreamWriter(out,"utf-8");
            bw=new BufferedWriter(outw);

            String templatePath=BuildBase.class.getClassLoader().getResource("template/"+fileName+".txt").getPath();


        } catch (Exception e) {
            logger.error("generate base class failed",fileName,e);
        }finally {
            //close read
            if(bf!=null){
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inr!=null){
                try {
                    inr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //close write
            if(bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outw!=null){
                try {
                    outw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
