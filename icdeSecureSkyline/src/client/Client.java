package client;
import Data.Data;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import normalskyline.Paillier;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;

/**
 * Created by fubin on 2019/1/3.
 */
public class Client {
    public static BigInteger[] encQuery;
    public static float[][] query;
    public static Paillier paillier = new Paillier();

    public static void main(String[] args){
        sendRequest();
    }
    public static void sendRequest(){
        try{
            query = Data.getQuery();
            encQuery = encData(query[1]);
//            BigInteger[] dec = new BigInteger[encQuery.length];
//            for (int i = 0 ; i < encQuery.length ; i++){
//                dec[i] =  paillier.Dec(encQuery[i]);
//            }
//            for (int i = 0 ; i < encQuery.length ; i++){
//                System.out.println("解密： "+dec[i].toString());
//            }
            Socket socket = new Socket("localhost",8888);
            OutputStream outputStream = socket.getOutputStream();
            JSONArray jdata =(JSONArray) JSONArray.toJSON(encQuery);
            String qstr = jdata.toString();
            byte[]  bytes = qstr.getBytes();
            outputStream.write(bytes);
            outputStream.flush();
            socket.shutdownOutput();
            outputStream.close();
            socket.close();
//            DataInputStream dis = new DataInputStream(socket.getInputStream());
//            String strInputstream ;
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();  // 可以捕获内存缓冲区的数据，转换成字节数组
//            byte[] by = new byte[2048];
//            int n;
//            while((n=dis.read(by))!=-1){  //将流中的数据缓冲到字节数组中去，如果没到末尾返回的是真实个数，到末尾时，返回的是-1；
//                baos.write(by,0,n);    //readline将会把json格式破坏掉
//            }
//            strInputstream = new String(baos.toByteArray());
//            socket.shutdownInput();
//            baos.close();
//            System.out.print("接受  "+strInputstream);
//            JSONArray jsonObject=JSONArray.parseArray(strInputstream);
//            System.out.println(jsonObject.toString());
//            JSONArray tmp ;
//            query = new BigInteger[jsonObject.size()][JSONArray.parseArray(jsonObject.get(0).toString()).size()];
//            for(int i = 0; i < jsonObject.size(); i++) {
//                tmp = JSONArray.parseArray(jsonObject.get(i).toString());
//                for(int j = 0; j < tmp.size(); j++) {
//                    System.out.println(tmp.get(j));
//                    query[i][j] = new BigInteger(tmp.get(j).toString());
//                }
//                System.out.println();
//            }
//
//            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static BigInteger[] encData(float[] f) {
        BigInteger[] bi = new BigInteger[f.length];
        for (int i = 0; i < f.length; i++) {
            BigDecimal b1 = new BigDecimal(f[i]);
//            System.out.println(" b   " + b1.toBigInteger());
            bi[i] = paillier.Enc(b1.toBigInteger());
//            System.out.println(paillier.Dec(bi[i]));

        }
        return bi;
    }
}


















