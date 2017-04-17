import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by limkuan on 16/9/21.
 */
public class TagPanel extends JPanel {

    private Point mBeginPoint = new Point();
    private Point mEndPoint = new Point();

    private ArrayList<TagLabel> mLabelList = new ArrayList<>();

    private Image mCurImage;
    private float scaleRatio = 0;
    private int realWidth = 0, realHeight = 0;

    private onLandmarkRegionChangeListener mListener;

    public interface onLandmarkRegionChangeListener {
        void onLandmarkRegionChange(TagLabel label);
    }

    TagPanel() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                if (x < 0) x = 0;
                if (y < 0) y = 0;
                if (x > realWidth) x = realWidth;
                if (y > realHeight) y = realHeight;
                mBeginPoint.setLocation(x, y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (mListener != null) {
                    mListener.onLandmarkRegionChange(getCurrentLabel());
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                if (x < 0) x = 0;
                if (y < 0) y = 0;
                if (x > realWidth) x = realWidth;
                if (y > realHeight) y = realHeight;
                mEndPoint.setLocation(x, y);
                repaint();
            }
        });
    }

    public TagLabel getCurrentLabel() {
        Point realBeginPoint = new Point((int) (mBeginPoint.getX() / scaleRatio),
                (int) (mBeginPoint.getY() / scaleRatio));
        Point realEndPoint = new Point((int) (mEndPoint.getX() / scaleRatio),
                (int) (mEndPoint.getY() / scaleRatio));

        return new TagLabel(realBeginPoint.x < realEndPoint.x ? realBeginPoint.x : realEndPoint.x,
                realBeginPoint.y < realEndPoint.y ? realBeginPoint.y : realEndPoint.y,
                realBeginPoint.x > realEndPoint.x ? realBeginPoint.x : realEndPoint.x,
                realBeginPoint.y > realEndPoint.y ? realBeginPoint.y : realEndPoint.y);
    }

    void saveCurrentLabel(int num) {
        TagLabel label = getCurrentLabel();
        label.setLandmarkNum(num);
        mLabelList.add(label);

        mBeginPoint.setLocation(0, 0);
        mEndPoint.setLocation(0, 0);

        repaint();
    }

    public void setLabelList(ArrayList<TagLabel> labels) {
        mLabelList.clear();
        mLabelList.addAll(labels);
        repaint();
    }

    ArrayList<TagLabel> getLabelList() {
        return mLabelList;
    }

    void clearLabel() {
        mLabelList.clear();
        mBeginPoint.setLocation(0, 0);
        mEndPoint.setLocation(0, 0);
        repaint();
    }

    void nextImage(String img) {
        clearLabel();

        try {
            mCurImage = ImageIO.read(new File(img));
            int imgWidth = mCurImage.getWidth(null);
            int imgHeight = mCurImage.getHeight(null);

            float widthRatio = (float) getWidth() / imgWidth;
            float heightRatio = (float) getHeight() / imgHeight;

            scaleRatio = widthRatio < heightRatio ? widthRatio : heightRatio;

            realWidth = (int) (scaleRatio * imgWidth);
            realHeight = (int) (scaleRatio * imgHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mCurImage != null) {
            g.setColor(Color.BLUE);
            g.drawImage(mCurImage, 0, 0, realWidth, realHeight, this);
            g.setFont(getFont().deriveFont(10f));
            g.setFont(getFont().deriveFont(25f));
        }

        ((Graphics2D) g).setStroke(new BasicStroke(3.0f));

        g.setColor(Color.blue);

        for (TagLabel label : mLabelList) {
            g.drawRect((int) (label.left_x * scaleRatio), (int) (label.left_y * scaleRatio),
                    (int) ((label.right_x - label.left_x) * scaleRatio), (int) ((label.right_y - label.left_y) * scaleRatio));
        }

        g.setColor(Color.red);
        Point left = new Point(mBeginPoint.x < mEndPoint.x ? mBeginPoint.x : mEndPoint.x,
                mBeginPoint.y < mEndPoint.y ? mBeginPoint.y : mEndPoint.y);
        Point right = new Point(mBeginPoint.x > mEndPoint.x ? mBeginPoint.x : mEndPoint.x,
                mBeginPoint.y > mEndPoint.y ? mBeginPoint.y : mEndPoint.y);

        g.drawRect(left.x, left.y, right.x - left.x, right.y - left.y);
    }

    void setListener(onLandmarkRegionChangeListener listener) {
        this.mListener = listener;
    }
}
