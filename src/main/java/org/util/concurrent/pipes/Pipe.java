package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
public final class Pipe {

    private final int index;
    private final Process process;
    private final Channel channel;
    private final boolean blocking;

    Pipe(int index, Process process, Channel channel, boolean blocking) {
        this.index = index;
        this.process = process;
        this.channel = channel;
        this.blocking = blocking;
    }

    public int index() {
        return index;
    }

    public Process process() {
        return process;
    }

    public Channel channel() {
        return channel;
    }

    public boolean isBlocking() {
        return blocking;
    }

}
