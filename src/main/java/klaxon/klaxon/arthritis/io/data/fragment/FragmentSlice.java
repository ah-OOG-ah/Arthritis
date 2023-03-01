package klaxon.klaxon.arthritis.io.data.fragment;

import klaxon.klaxon.arthritis.io.fragment.Fragment;

public class FragmentSlice {
    public final int rangeStart;
    public final int rangeEnd;
    public final long fileSize;

    public FragmentSlice(int rangeStart, int rangeEnd, long fileSize) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.fileSize = fileSize;
    }

    public FragmentSlice(Fragment fragment) {
        this.rangeStart = fragment.startIndex;
        this.rangeEnd = fragment.endIndex;
        this.fileSize = fragment.size;
    }
}
