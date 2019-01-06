package server1;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import normalskyline.Paillier;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by fubin on 2019/1/3.
 */
public class Server1 {
    public static float[][] f = {
            {4,400},
            {24,380},
            {14,340},
            {36,300},
            {26,280},
            {8,260},
            {40,200},
            {20,180},
            {34,140},
            {28,120},
            {16,60}
    };
    //加密数据集
    public static BigInteger[][] encData;
    //加密查询
    public static BigInteger[] query;
    static class thread1 implements Runnable{
        private int port;
        public thread1(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            if(port == 8888)
                receiveQuery();
            if (port == 9999)
                receiveData();
            if(port == 7777){}
//                receiveQuery();
        }
        public void receiveQuery(){
            try{
                ServerSocket ss = new ServerSocket(port);
                Socket socket = ss.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                String strInputStream;
                byte[] bytes = new byte[2048];
                int n;
                while ((n=dis.read(bytes))!=-1){
                    baos.write(bytes,0,n);
                }
                strInputStream = new String(baos.toByteArray());
                socket.shutdownOutput();
                baos.close();
                JSONArray jsonArray = JSONArray.parseArray(strInputStream);
                query = new BigInteger[jsonArray.size()];
                for(int j = 0; j < jsonArray.size(); j++) {
                    query[j] = new BigInteger(jsonArray.get(j).toString());
                    System.out.println(query[j]);
                }
                System.out.println();
                BigInteger[] decq = new BigInteger[query.length];
                Paillier p = new Paillier();
                for(int j = 0 ; j < jsonArray.size(); j++)
                {
                    decq[j] = p.Dec(query[j]);
                }
                for (int k = 0 ; k < decq.length ; k++)
                    System.out.println("解密之后的查询点："+decq[k]);


            }catch (IOException e){
                e.printStackTrace();
            }
        }
        public void receiveData() {
            try{
                ServerSocket ss = new ServerSocket(port);
                Socket socket = ss.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                String strInputStream;
                byte[] bytes = new byte[2048];
                int n;
                while ((n=dis.read(bytes))!=-1){
                    baos.write(bytes,0,n);
                }
                strInputStream = new String(baos.toByteArray());
                socket.shutdownOutput();
                baos.close();
                JSONArray jsonArray = JSONArray.parseArray(strInputStream);
                System.out.println("server1 : "+jsonArray);
                JSONArray tmp = null;
                 encData = new BigInteger[jsonArray.size()][JSONArray.parseArray(jsonArray.get(0).toString()).size()];
                for(int i = 0; i < jsonArray.size(); i++) {
                    tmp = JSONArray.parseArray(jsonArray.get(i).toString());
                    for(int j = 0; j < tmp.size(); j++) {
                        System.out.println(tmp.get(j));
                        encData[i][j] = new BigInteger(tmp.get(j).toString());
                    }
                    System.out.println();
                }



            }catch (IOException e){
                e.printStackTrace();
            }

        }
        public void sendData(){
            try{
                ServerSocket ss = new ServerSocket(port);
                while (true){
                    Socket s = ss.accept();
                    OutputStream outputStream = s.getOutputStream();
                    JSONObject json = new JSONObject();
                    while (encData==null);
                    JSONArray fJson = (JSONArray)JSONArray.toJSON(encData);
                    String jsonString = fJson.toString();
                    byte[] bytes = jsonString.getBytes();
                    outputStream.write(bytes);
                    outputStream.flush();
                    s.shutdownOutput();
                    outputStream.close();
//                PrintStream ps = new PrintStream(s.getOutputStream());
//                ps.print("这是服务器发送的消息。");
//                ps.close();
                    s.close();
                }

            }catch (IOException e){
                e.printStackTrace();
            }


        }
    }
    public static void main(String[] args){
        new Thread(new thread1(8888)).start();
        new Thread(new thread1(9999)).start();
    }

}















