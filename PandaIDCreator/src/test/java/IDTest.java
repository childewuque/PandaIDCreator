import id.panda.IDC;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: adamtan
 * Date: 2017/3/29
 * Time: 下午6:23
 * To change this template use File | Settings | File Templates.
 */
public class IDTest {
    @Test
    public void test(){
        for (int i=1;i<100000;i++){
            System.out.println(IDC.INSTANCE.getId());
        }
    }

}
