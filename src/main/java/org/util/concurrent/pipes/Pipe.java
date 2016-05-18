package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
public final class Pipe {

    final long id = Seq.next();

    private final int index;
    private final String name;
    private final Process process;
    private final boolean blocking;

    Pipe(int index, String name, Process process, boolean blocking) {
        this.index = index;
        this.name = name;
        this.process = process;
        this.blocking = blocking;
    }

    public int index() {
        return index;
    }

    public String name() {
        return name;
    }

    public Process process() {
        return process;
    }

    public boolean isBlocking() {
        return blocking;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Pipe && id == ((Pipe) obj).id;
    }

    @Override
    public String toString() {
        return "Pipe{" +
                "id=" + id +
                ", index=" + index +
                ", name='" + name + '\'' +
                ", blocking=" + blocking +
                '}';
    }

}
