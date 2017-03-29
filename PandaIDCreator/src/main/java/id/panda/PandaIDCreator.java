package id.panda;

import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * 基于twitter的snowake算法
 * User: adamtan
 * Date: 2017/1/1
 * Time: 下午13:33
 * To change this template use File | Settings | File Templates.
 */
public class PandaIDCreator {
    private long workerId;
    private final static long twepoch = 1288834974657L;
    private long sequence = 0L;
    private final static long workerIdBits = 10L;
    public final static long maxWorkerId = -1L ^ -1L << workerIdBits;
    private final static long sequenceBits = 12L;

    private final static long workerIdShift = sequenceBits;
    private final static long timestampLeftShift = sequenceBits + workerIdBits;
    public final static long sequenceMask = -1L ^ -1L << sequenceBits;

    private long lastTimestamp = -1L;

    /**
     * 强烈建议此构造方法
     * @param workerId 实例序列号--需业务保证每个实例唯一,最小1,最大1024
     *
     */
    public PandaIDCreator(final long workerId) {
        System.out.println("Please keep:one jvm instance,one single workerID,now the workderID="+workerId);
        if (workerId > this.maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format(
                    "worker Id can't be greater than %d or less than 0",
                    this.maxWorkerId));
        }
        this.workerId = workerId;
    }

    /**
     * 无参构造函数,使用ip的后8位(10进制的最后一个.之后的部分)作为jvm实例的区分
     * 因此如果ip后8位相同,则IDCreator不是100%保证唯一,意味着不同实例间可能产生相同ID
     */
    public PandaIDCreator() {
        super();
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            String[] ipArr = ip.split("\\.");
            this.workerId = Integer.parseInt(ipArr[3]);
        } catch (Exception e) {
            System.out.println("IDWorker can't get the Machine IP!Please use the construct with param workerID.");
            e.printStackTrace();

            System.exit(0);
        }
    }

     /**
      *
      * 为了兼容现有使用方法,bizId不再是计算因子. 全新业务不建议使用此方法.
      * @param  bizId 业务ID
      *
      * */
    public  long nextId(int bizId){
      return nextId() ;
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & this.sequenceMask;
            if (this.sequence == 0) {
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0;
        }
        if (timestamp < this.lastTimestamp) {
            try {
                throw new Exception(
                        String.format(
                                "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                                this.lastTimestamp - timestamp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.lastTimestamp = timestamp;
        long nextId = ((timestamp - twepoch << timestampLeftShift))
                | (this.workerId << this.workerIdShift) | (this.sequence);
        return nextId;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}

