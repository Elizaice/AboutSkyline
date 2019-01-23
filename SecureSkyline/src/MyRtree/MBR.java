package MyRtree;

import secskyline.encComp;
import security.Paillier;
import security.PaillierPK;
import security.PaillierSK;

import java.math.BigInteger;

public class MBR {
    private BigInteger[] minDim; //区域的最小值点
    private BigInteger[] maxDim; //区域的最大值点

    public BigInteger[] getMinDim() {
        return minDim;
    }

    public void setMinDim(BigInteger[] minDim) {
        this.minDim = minDim;
    }

    public BigInteger[] getMaxDim() {
        return maxDim;
    }

    public void setMaxDim(BigInteger[] maxDim) {
        this.maxDim = maxDim;
    }

    @Override
    public String toString() {
        String res = "minDim:";
        for(int i = 0; i < minDim.length; i++){
            res = res + minDim[i].toString() + " ";
        }
        res += "\nmaxDim:";
        for(int i = 0; i < minDim.length; i++){
            res = res + maxDim[i].toString() + " ";
        }
        return res;
    }

    public MBR(BigInteger[] minDim, BigInteger[] maxDim) {
        this.minDim = minDim;
        this.maxDim = maxDim;
    }
    public BigInteger getArea(){
        BigInteger area = new BigInteger("1");
        for(int i = 0 ; i < minDim.length;i++){
            area = area.multiply (maxDim[i].subtract(minDim[i]));
        }
        return area;
    }
    public MBR combine(MBR mbr){
        BigInteger[] min = new BigInteger[minDim.length];
        BigInteger[] max = new BigInteger[maxDim.length];
        for(int i = 0; i < min.length ; i++) {
            min[i] = this.minDim[i].min(mbr.minDim[i]);
            max[i] = this.maxDim[i].max(mbr.maxDim[i]);
        }
        return new MBR(min,max);
    }

    // 使用Paillier加密进行安全的combine操作
    // 缺点在于secless缓慢。这里不采用使用了paillier和DGK的secless，因为之前使用的别人写好的运行速度太慢
    // 这里使用了针对本问题中可对不是非常大的数进行比较的secless
    public MBR secCombine(MBR mbr, PaillierPK pk, PaillierSK sk) {
        BigInteger[] min = new BigInteger[minDim.length];
        BigInteger[] max = new BigInteger[maxDim.length];
        for(int i = 0; i < min.length ; i++) {
            BigInteger tmpMin;
            tmpMin = encComp.newSecLess(this.minDim[i], mbr.minDim[i], pk, sk);
            if(Paillier.decrypt(tmpMin, sk) == BigInteger.ZERO) {
                min[i] = this.minDim[i];
            } else {
                min[i] = mbr.minDim[i];
            }
            tmpMin = encComp.newSecLess(this.maxDim[i], mbr.maxDim[i], pk, sk);
            if(Paillier.decrypt(tmpMin, sk) == BigInteger.ZERO) {
                max[i] = mbr.maxDim[i];
            } else {
                max[i] = this.maxDim[i];
            }
        }
        return new MBR(min, max);
    }

    //MBR的优先级，即最小属性和
    public BigInteger priority(Key q){
        BigInteger minSum = new BigInteger("0");
        BigInteger[] relativeDim = getRelativeMinDim(q);
        for(int i = 0 ;i < minDim.length; i++){
            minSum = minSum.add(relativeDim[i]);
        }
        return minSum;
    }

    // 使用paillier计算最小属性和
    public BigInteger secPriority(Key q, PaillierPK pk) {
        BigInteger[] relativeDim = getSecRelativeMinDim(q, pk);
        BigInteger minSum = relativeDim[0];
        for(int i = 1 ;i < minDim.length; i++){
            minSum = minSum.multiply(relativeDim[i]);
        }
        return minSum;
    }

    //this是否支配mbr,mbr被this支配返回true
    public boolean dominates(MBR mbr, Key q, boolean calRelative){
        if(calRelative) {
            BigInteger[] dim1 = getRelativeMinDim(q);
            BigInteger[] dim2 = mbr.getRelativeMinDim(q);
            for(int i = 0 ; i < minDim.length ;i++){
                //只要mbr有一个维度比this要小，那么他就不被this支配
                if(dim1[i].compareTo(dim2[i])>0){
                    return false;
                }
            }
        } else {
            for(int i = 0 ; i < minDim.length ;i++){
                //只要mbr有一个维度比this要小，那么他就不被this支配
                if(this.minDim[i].compareTo(mbr.minDim[i])>0)
                    return false;

            }
        }

        return true;
    }

    public BigInteger[] getRelativeMinDim(Key query) {
        BigInteger[] q_dim = query.getMbr().minDim;
        BigInteger[] res_dim = new BigInteger[minDim.length];
        for(int i = 0; i < minDim.length; i++) {
            res_dim[i] = minDim[i].subtract(q_dim[i]).abs();
        }
        return res_dim;
    }

    public BigInteger[] getSecRelativeMinDim(Key query, PaillierPK pk) {
        BigInteger[] q_dim = query.getMbr().minDim;
        BigInteger[] res_dim = new BigInteger[minDim.length];
        for(int i = 0; i < minDim.length; i++) {
            res_dim[i] = minDim[i].multiply(q_dim[i].modPow(pk.n.subtract(BigInteger.ONE), pk.modulus)).abs();
        }
        return res_dim;
    }
}
