package normalskyline;

import encryptdata.EncData;
import sun.plugin2.os.windows.FLASHWINFO;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fubin on 2019/1/3.
 */
public class SkylineComputation {
    public static float[][] f = {
            {4.5f,400.9f},
            {24,380.5f},
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
    public static void main(String[] args){
        //DO端发送给服务器encDataset;
       BigInteger[][] encDataset = encData(f);
      float[] q = new float[]{0,0};
        List<Integer> res = computation(f,q);
        for(int i = 0 ; i < res.size() ; i++)
            System.out.println(res.get(i));
    }
    public static List<Integer> computation(float[][] f,float[] query){
        List<Integer> result = new ArrayList<>();
        List<float[]> dataset = new ArrayList<>();
        for(int i = 0 ; i < f.length ; i++){
            float[] tmp = new float[f[i].length];
            for(int j = 0 ; j < f[i].length ; j++){
                tmp[j] = Math.abs(f[i][j]-query[j]);
            }
            dataset.add(tmp);
        }
        while(dataset.size() > 0){
            float[] sum = new float[dataset.size()];
            for(int i = 0 ; i < dataset.size() ; i++){
                for(int j = 0 ; j < dataset.get(i).length ; j++){
                    sum[i] += dataset.get(i)[j];
                }
            }
            for(int i = 0 ; i < sum.length ; i++)
                System.out.println("sum : "+sum[i]);
            float min = Float.MAX_VALUE;
            int idxmin = -1 ;
            for(int i = 0 ; i < sum.length ; i++){
                if(sum[i] < min){
                    min = sum[i];
                    idxmin = i;
                }
            }
            float[] minf = dataset.get(idxmin);
            System.out.println("min: "+min);
            System.out.println("idxmin:"+idxmin);
            for(int i = 0 ; i < f.length ; i++){
                if(Arrays.equals(f[i],minf)){
                    result.add(i);
                    break;
                }
            }
            Iterator<float[]> it = dataset.iterator();
            while (it.hasNext()){
                float[] ft = it.next();
//                System.out.println("ft "+ft[0]);
                if(dominate(minf,ft)){
                    System.out.println("ft "+ft[0] + " " + ft[1]);
                    it.remove();
                }
            }
//            float[] ff = f[idxmin];
//
//            dataset.remove(ff);
        }

        return result;
    }
    //p支配q，返回true;
    public static boolean dominate(float[] p, float[] q){
        for(int i = 0 ; i < p.length ;i++){
            if(p[i]>q[i])
                return false;
        }
        return true;
    }

    public static BigInteger[][] encData(float[][] f){
        Paillier pai = new Paillier();
        BigInteger[][] bi = new BigInteger[f.length][f[0].length];
        for (int i = 0 ; i < f.length ; i++){
            for (int j = 0 ; j < f[i].length ;j++){
                BigDecimal b1 = new BigDecimal(f[i][j]);
                System.out.println(" b   "+b1.toBigInteger());
               bi[i][j] =  pai.Enc(b1.toBigInteger());
            }
        }
        for(int i = 0 ; i < bi.length ; i++){
            for (int j = 0 ; j < bi[i].length ; j++)
                System.out.println(bi[i][j]);
        }
        BigInteger[][] bb = new BigInteger[f.length][f[0].length];
        for (int i = 0 ; i < f.length ; i++){
            for (int j = 0 ; j < f[i].length ;j++){
               bb[i][j] = pai.Dec(bi[i][j]);
            }
        }
        for(int i = 0 ; i < bi.length ; i++){
            for (int j = 0 ; j < bi[i].length ; j++)
                System.out.println(bb[i][j]);
        }
        return bi;
    }
}
