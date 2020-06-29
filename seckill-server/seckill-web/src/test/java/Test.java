import com.alibaba.fastjson.JSON;

import java.sql.Timestamp;


public class Test {
    @org.junit.jupiter.api.Test
    public static void main(String[] args) {
        System.out.println(JSON.toJSON(new Timestamp(10000)).toString());
    }
}
