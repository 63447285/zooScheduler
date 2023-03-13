import com.geoway.atlas.Agent;
import org.junit.Test;

import java.io.IOException;

public class testscheduler {
    @Test
    public void testinit() throws IOException {
        Agent.getInstance().premain(null,null);
        //String serverNodeByMS = Agent.getInstance().getServerNodeByMS(10, 30);
        String serverNodeByMS = Agent.getInstance().getServerNodeByMS("Large");
        System.out.println(serverNodeByMS);
//        agent.getServerNodes();
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void update1() throws Exception {
        Agent.getInstance().premain(null,null);
        //String serverNodeByMS = Agent.getInstance().getServerNodeByMS(60, 10);
        String serverNodeByMS = Agent.getInstance().getServerNodeByMS("Medium");
        System.out.println(serverNodeByMS);
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void update2() throws Exception {
        Agent.getInstance().premain(null,null);
        //String serverNodeByMS = Agent.getInstance().getServerNodeByMS(60, 10);
        String serverNodeByMS = Agent.getInstance().getServerNodeByMS("Large");
        System.out.println(serverNodeByMS);
    }

    @Test
    public void release(){
        Agent.getInstance().premain(null,null);
        Agent.getInstance().releaseServerMemory("172.16.67.166:8001", "Medium");
    }
}
