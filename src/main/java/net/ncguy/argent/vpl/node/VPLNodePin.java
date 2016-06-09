package net.ncguy.argent.vpl.node;

/**
 * Created by Guy on 09/06/2016.
 */
public class VPLNodePin {

    enum FLAGS {
        INPUT,
        OUTPUT,
        COMPOUND,
        ;
        public int bit() {
            return (int) Math.pow(2, ordinal());
        }
    };

    private int flags;

    public int setFlags(int flags) {
        return this.flags = flags;
    }

    public int setFlags(FLAGS... flags) {
        this.flags = 0;
        for (FLAGS flag : flags)
            this.flags = this.flags | flag.bit();
        return this.flags;
    }

}
