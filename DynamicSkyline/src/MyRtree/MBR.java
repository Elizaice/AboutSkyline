package MyRtree;

public class MBR {
    private float[] minDim; //区域的最小值点
    private float[] maxDim; //区域的最大值点

    public float[] getMinDim() {
        return minDim;
    }

    public void setMinDim(float[] minDim) {
        this.minDim = minDim;
    }

    public float[] getMaxDim() {
        return maxDim;
    }

    public void setMaxDim(float[] maxDim) {
        this.maxDim = maxDim;
    }

    public MBR(float[] minDim, float[] maxDim) {
        this.minDim = minDim;
        this.maxDim = maxDim;
    }
    public float getArea(){
        float area = 1;
        for(int i = 0 ; i < minDim.length;i++){
            area *= (maxDim[i]-minDim[i]);
        }
        return area;
    }
    public MBR combine(MBR mbr){
        float[] min = new float[minDim.length];
        float[] max = new float[maxDim.length];
        for(int i = 0; i < min.length ; i++) {
            min[i] = Math.min(this.minDim[i], mbr.minDim[i]);
            max[i] = Math.max(this.maxDim[i],mbr.maxDim[i]);
        }
        return new MBR(min,max);
    }

    //MBR的优先级，最小属性和？
    public float priority(Key q){
        float minSum = 0;
        float[] relativeDim = getRelativeMinDim(q);
        for(int i = 0 ;i < minDim.length; i++){
            minSum += relativeDim[i];
        }
        return minSum;
    }

    //this是否支配mbr,mbr被this支配返回true
    public boolean dominates(MBR mbr, Key q, boolean calRelative){
        if(calRelative) {
            float[] dim1 = getRelativeMinDim(q);
            float[] dim2 = mbr.getRelativeMinDim(q);
            for(int i = 0 ; i < minDim.length ;i++){
                //只要mbr有一个维度比this要小，那么他就不被this支配
//            if(this.minDim[i]>mbr.minDim[i]){
                if(dim1[i] > dim2[i]){
                    return false;
                }
            }
        } else {
            for(int i = 0 ; i < minDim.length ;i++){
                //只要mbr有一个维度比this要小，那么他就不被this支配
                if(this.minDim[i]>mbr.minDim[i])
                    return false;
            }
        }

        return true;
    }

    public float[] getRelativeMinDim(Key query) {
        float[] q_dim = query.getMbr().minDim;
        float[] res_dim = new float[minDim.length];
        for(int i = 0; i < minDim.length; i++) {
            res_dim[i] = Math.abs(minDim[i] - q_dim[i]);// + q_dim[i];
        }
        return res_dim;
    }


}
