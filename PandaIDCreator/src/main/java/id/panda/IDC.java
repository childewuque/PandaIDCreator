package id.panda;

/**
 * Created with IntelliJ IDEA.
 * User: adamtan
 * Date: 17/1/2
 * Time: 下午1:33
 * To change this template use File | Settings | File Templates.
 */
public enum IDC {
    INSTANCE;
    private PandaIDCreator pandaIDCreator;

    private IDC() {  //基于IP最后一段,如果IP最后一段有重复,则不能用此构造方法
        pandaIDCreator = new PandaIDCreator();
    }

    public long getId() {
        return pandaIDCreator.nextId();
    }
}
