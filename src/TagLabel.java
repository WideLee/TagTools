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

    public float calculateIoU(TagLabel other) {
        float areai = calculateArea();
        float areaj = other.calculateArea();

        int xx1 = Math.max(left_x, other.left_x);
        int yy1 = Math.max(left_y, other.left_y);
        int xx2 = Math.min(right_x, other.right_x);
        int yy2 = Math.min(right_y, other.right_y);

        int h = Math.max(0, yy2 - yy1 + 1);
        int w = Math.max(0, xx2 - xx1 + 1);

        float intersection = w * h;
        float iou = intersection / (areai + areaj - intersection);
        return iou;
    }

    public float calculateArea() {
        return (right_x - left_x + 1) * (right_y - left_y + 1);
    }
}