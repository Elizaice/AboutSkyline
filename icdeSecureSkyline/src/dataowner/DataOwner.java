package dataowner;

import Data.Data;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import normalskyline.Paillier;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by fubin on 2019/1/4.
 */
public class DataOwner {
public static float[][] f;
public static Paillier paillier = new Paillier();
public static void main(String[] args) {
    try {
        sendData();
    }catch (Exception e){
        e.printStackTrace();
    }

}
//DO将dataset（加密后的f）发送给服务器S1
public static void sendData() throws Exception {
    f = Data.getData();
    BigInteger[][] dataset = encData(f);
    Socket socket = new Socket("localhost",9999);
    OutputStream outputStream = socket.getOutputStream();
    JSONArray jdata =(JSONArray) JSONArray.toJSON(dataset);
    String datastr = jdata.toString();
    byte[]  bytes = datastr.getBytes();
    outputStream.write(bytes);
    outputStream.flush();
    socket.shutdownOutput();
    outputStream.close();
    socket.close();

}
    public static BigInteger[][] encData(float[][] f) {
        BigInteger[][] bi = new BigInteger[f.length][f[0].length];
        for (int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[i].length; j++) {
                BigDecimal b1 = new BigDecimal(f[i][j]);
              //  System.out.println(" b   " + b1.toBigInteger());
                bi[i][j] = paillier.Enc(b1.toBigInteger());
            }
        }
        return bi;
    }
}

