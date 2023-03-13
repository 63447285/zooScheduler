import com.geoway.atlas.xmlOperater;
import org.junit.Test;

public class testxmloperation {
    @Test
    public void testGetZkServer() throws Exception {
        xmlOperater xmloperater=new xmlOperater();
        String server = xmloperater.getZkAddress();
        System.out.println(server);
    }

    @Test
    public void testGetPluginsPath(){
        xmlOperater xmloperater=new xmlOperater();
        String path = xmloperater.getPluginsPath();
        System.out.println(path);
    }

    @Test
    public void testGetServicesPath(){
        xmlOperater xmloperater=new xmlOperater();
        String path = xmloperater.getServicesPath();
        System.out.println(path);
    }

    @Test
    public void testScale2Percent() throws Exception {
        xmlOperater xmloperater=new xmlOperater();
        //double large = xmloperater.scale2Percentage("Large");
        double medium = xmloperater.scale2Percentage("Medium");
        System.out.println(medium);
    }

    @Test
    public void testPercent2Scale(){
        xmlOperater xml = new xmlOperater();
        String s = xml.percentage2Scale(3.12);
        System.out.println(s);
    }

}
