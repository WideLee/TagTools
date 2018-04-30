import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by limkuan on 16/9/21.
 */
public class Main {
    private JPanel mMainPanel;
    private JTextField mLandmarkNum;
    private JButton mSaveButton;
    private JButton mClearButton;
    private TagPanel mImagePanel;
    private JLabel mLandmarkLabel;
    private JButton mNextButton;
    private JCheckBox mDifficultCheckBox;
    private JCheckBox mTruncatedCheckBox;

    private Map<String, String> mLabelList;

    private String mOutputFilename;
    private String mFrameImagePath;

    private Main() {
        mNextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveLabels(mImagePanel.getLabelList());
                System.exit(0);
            }
        });

        mLandmarkNum.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            void warn() {
                mLandmarkLabel.setText(mLabelList.get(mLandmarkNum.getText()));
            }
        });

        mSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    boolean is_truncated = mTruncatedCheckBox.isSelected();
                    boolean is_difficult = mDifficultCheckBox.isSelected();
                    mImagePanel.saveCurrentLabel(Integer.valueOf(mLandmarkNum.getText()),
                            is_truncated, is_difficult);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mMainPanel,
                            "Error: Landmark number must be a number", "Error Massage",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        mClearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mImagePanel.clearLabel();
            }
        });
    }

    public static void main(String[] args) {
        /**
         * args[0] the frame image filename
         * args[1] the region output filename
         * args[2] the landamrk list filename
         *
         * output file format
         * frame filename | left_x | left_y | right_x | right_y | landmark_id
         */

        if (args.length != 3) {
            JOptionPane.showMessageDialog(null,
                    "Error: run this app with three arguments", "Error Massage",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Main main = new Main();
        JFrame frame = new JFrame("Main");
        frame.setContentPane(main.mMainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(main.mMainPanel.getPreferredSize().width, main.mMainPanel.getPreferredSize().height + 50);
        frame.setResizable(false);
        frame.setVisible(true);

        main.setFrameImagePath(args[0]);
        main.setOutputFilename(args[1]);
        main.setLandmarkList(args[2]);

        main.nextImage();
    }

    private void nextImage() {
        if (mFrameImagePath != null) {
            mImagePanel.nextImage(mFrameImagePath);
        }
    }

    private void createUIComponents() {

        mImagePanel = new TagPanel();

        TagPanel.onLandmarkRegionChangeListener listener = new TagPanel.onLandmarkRegionChangeListener() {
            @Override
            public void onLandmarkRegionChange(TagLabel label) {
                System.out.printf(Locale.ENGLISH, "rectangle: %d %d %d %d",
                        label.left_x, label.left_y, label.right_x, label.right_y);
                mLandmarkNum.requestFocus();
                mLandmarkNum.selectAll();
            }
        };
        mImagePanel.setListener(listener);
    }

    private void setLandmarkList(String labelList) {
        try {
            Scanner scanner = new Scanner(new File(labelList));
            mLabelList = new HashMap<>();
            int count = 1;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();

                mLabelList.put(Integer.toString(count++), line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setOutputFilename(String mOutputFilename) {
        this.mOutputFilename = mOutputFilename;
    }

    private void setFrameImagePath(String mFrameImagePath) {
        this.mFrameImagePath = mFrameImagePath;
    }

    private void saveLabels(ArrayList<TagLabel> labels) {
        try {
            PrintStream ps = new PrintStream(new File(mOutputFilename));

            for (TagLabel label : labels) {
                /**
                 * output format
                 * frame filename | left_x | left_y | right_x | right_y | landmark_id
                 */
                ps.printf("%s %d %d %d %d %d %d %d\n", mFrameImagePath,
                        label.left_x, label.left_y, label.right_x,
                        label.right_y, label.landmark_num,
                        label.is_truncated ? 1 : 0, label.is_difficult ? 1 : 0);
            }

            ps.flush();
            ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
