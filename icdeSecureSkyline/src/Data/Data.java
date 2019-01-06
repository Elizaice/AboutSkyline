package Data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class Data {
    public static void main(String[] args) throws Exception{
        fetchData("https://stats.nba.com/stats/leagueLeaders?LeagueID=00&PerMode=PerGame&Scope=S&Season=2018-19&SeasonType=Regular+Season&StatCategory=PTS");
        float [][]data = getData();
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                System.out.printf(data[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void fetchData(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault(); // 创建httpclient实例
        HttpGet httpget = new HttpGet(url); // 创建httpget实例

        CloseableHttpResponse response = httpclient.execute(httpget); // 执行get请求
        //
        HttpEntity entity=response.getEntity(); // 获取返回实体
        String webContent= EntityUtils.toString(entity, "utf-8");
        JSONObject json = JSONObject.parseObject(webContent);
        JSONArray result = (JSONArray) JSONObject.parseObject(json.get("resultSet").toString()).get("rowSet");

        File f = new File("data.txt");
        FileWriter fw = new FileWriter(f);
        for(int i = 0; i < result.size(); i++) {
            JSONArray tmp = JSONArray.parseArray(result.get(i).toString());
            for(int j = 0; j < tmp.size(); j++) {
                if(j == 0 | j == 1 || j == 2 || j == 3)
                    continue;
                fw.write(tmp.get(j).toString() + " ");
            }
            fw.write("\n");
        }
        fw.flush();
        fw.close();
        f = new File("name.txt");
        fw = new FileWriter(f);
        for(int i = 0; i < result.size(); i++) {
            JSONArray tmp = JSONArray.parseArray(result.get(i).toString());
            fw.write(tmp.get(2).toString()+ "\n");
        }
        fw.flush();
        fw.close();
        // System.out.println("网页内容："+webContent); // 指定编码打印网页内容
        response.close(); // 关闭流和释放系统资源
    }

    public static float[][] getData() throws Exception {
        File f = new File("data.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String[] arrs = null;
        List<List> list = new ArrayList();
        int i = 0, j = 0;
        while((line=br.readLine())!=null) {
            List<Float> tmpList = new ArrayList();
            arrs = line.split(" ");
            for(j = 0; j < arrs.length; j++) {
                tmpList.add(Float.valueOf(arrs[j].toString()));
            }
            list.add(tmpList);
        }
        if(list.size() == 0)
            return null;
        float[][] data = new float[list.size()][list.get(0).size()];
        for(i = 0; i < list.size(); i++) {
            for(j = 0; j < list.get(0).size(); j++)
                data[i][j] = (Float)list.get(i).get(j);
        }
        return data;
    }

    public static float[][] getQuery() throws Exception {
        File f = new File("query.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line;
        line=br.readLine();
        String[] dimention = line.split(" ");
        line = br.readLine();
        String[] queryData = line.split(" ");
        float[][] query = new float[2][dimention.length];
        for(int i = 0; i < dimention.length; i++) {
            query[0][i] = Float.valueOf(dimention[i].toString());
            query[1][i] = Float.valueOf(queryData[i].toString());
        }
        return query;
    }
}
