import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoway.atlas.Agent;
import com.geoway.atlas.WebService;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class testmystring {
    @Test
    public void test1() throws IOException {
        WebService webService=new WebService();
        webService.ip="172.16.67.176";
        webService.host="8000";
        webService.scale="big";
        webService.cpu=4.0;
        webService.totalMemorySize= 60;
        webService.usedMemorySize =20;
        webService.usableMemorySize= 40;
        System.out.println("转换前:"+webService);
        ObjectMapper mapper = new ObjectMapper();
        String temp1=mapper.writeValueAsString(webService);
        WebService webService1 = mapper.readValue(temp1, WebService.class);
        System.out.println("转化后:"+webService1);
    }


//    @Test
//    public void test2() throws IOException {
//        String temp1="{\"ip\":\"172.16.67.176\",\"host\":\"8000\",\"cpu\":4.0,\"totalMemorySize\":60,\"usedMemorySize\":20,\"usableMemorySize\":40}";
//        Agent agent= new Agent();
//        WebService webService = agent.Json2WebService(temp1);
//        System.out.println(webService);
//    }


}
