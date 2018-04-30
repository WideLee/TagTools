public class TagLabel {
    int left_x, left_y;
    int right_x, right_y;
    int landmark_num;
    boolean is_difficult;
    boolean is_truncated;

    TagLabel(int lx, int ly, int rx, int ry) {
        left_x = lx;
        left_y = ly;
        right_x = rx;
        right_y = ry;
    }

    public void setLandmarkNum(int num) {
        this.landmark_num = num;
    }

    public void setState(boolean truncated, boolean difficult) {
        is_truncated = truncated;
        is_difficult = difficult;
    }
}