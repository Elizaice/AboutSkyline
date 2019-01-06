import java.util.Arrays;

/**
 * Created by fubin on 2019/1/6.
 */
public class test {
        public static void main(String[] args) throws Exception {
            char[] chars = new char[] {'\u0097'};
            String str = new String(chars);
            System.out.println(str);
            byte[] bytes = str.getBytes("cP1252");
            System.out.println(Arrays.toString(bytes));
            System.out.println('\u003f');
        }

}
