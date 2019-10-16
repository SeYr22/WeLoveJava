import com.sun.javafx.geom.Vec3d;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import com.company.Task23;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import static com.company.Task23.*;
import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;
import static org.opencv.imgcodecs.Imgcodecs.imread;

public class myGUI extends JFrame {
    private JButton clickMeButton;
    private JPanel MyPanel;
    private JTabbedPane JTable;
    private JPanel Task1;
    private JPanel Task2;
    private JLabel Rofloturik;
    private JLabel solo332;
    private JButton TestButt;
    private JPanel Task4;
    private JPanel Task5;
    private JPanel Task6;
    private JButton button1;
    private JTextField textBOX;
    private JButton buttonReadImages;
    private JButton buttonSolveTask23;
    private JPanel ansImage = new JPanel();
    public static JFrame mainFrame;
    // Task23 task23 = new Task23();
    //  static Browser browser;
    //   static public JFrame frameSecond;
    //  static JFrame frame;

    public myGUI() {
        Rofloturik.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });


        TestButt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                mainFrame.setVisible(false);
                Browser browser;
                EngineOptions options =
                        EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                                .licenseKey("1BNDHFSC1FT94V3KH87EQ2JITA57T339AO7YWVTZLML2HG6ALBOANHYUW0CU4PTAHTR9D3")
                                .build();
                Engine engine = Engine.newInstance(options);
                browser = engine.newBrowser();
                SwingUtilities.invokeLater(() -> {
                    BrowserView view = BrowserView.newInstance(browser);
                    JFrame frame;
                    frame = new JFrame("Карта");
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    frame.setSize(900, 500);
                    view.setSize(900, 400);
                    frame.add(view);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                    browser.navigation().loadUrl("http://lit-tui.rf.gd");
                    frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            super.windowClosing(e);
                            mainFrame.setVisible(true);
                            System.out.println("ralf");


                        }
                    });
                });
            }
        });
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JPanel panel = new JPanel();
                BufferedImage img = null;
                try {
                    img = ImageIO.read(new File(textBOX.getText()));
                } catch (IOException a) {

                }

                Mat src = null;
                try {
                    src = BufferedImage2Mat(img);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                double normalS = 800 * 800;
                double Sc = src.width() * src.height();
                double k = sqrt(Sc / normalS);
                if (k < 1) k = 1;
                Mat tra = new Mat(2, 3, CvType.CV_32FC1);
                tra.put(0, 0,
                        1 / k, 0, 0,
                        0, 1 / k, 0
                );
                Imgproc.warpAffine(src, src, tra, new Size(src.width() / k, src.height() / k));

                Mat hsv = new Mat(src.cols(), src.rows(), 3);
                List<Mat> splitedHsv = new ArrayList<>();
                Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);
                Core.split(hsv, splitedHsv);

                final int HUE_MIN = 7;
                final int HUE_MAX = 90;
                final int SATURATION_MIN = 20;
                final int VALUE_MIN = 100;

                for (int y = 0; y < hsv.cols(); y++) {
                    for (int x = 0; x < hsv.rows(); x++) {
                        // получаем HSV-компоненты пикселя
                        int H = (int) splitedHsv.get(0).get(x, y)[0];        // Тон
                        int S = (int) splitedHsv.get(1).get(x, y)[0];          // Интенсивность
                        int V = (int) splitedHsv.get(2).get(x, y)[0];          // Яркость
                        //System.out.println(V);
                        //Если яркость слишком низкая либо Тон не попадает у заданный диапазон, то закрашиваем белым
                        if ((H >= HUE_MIN && H <= HUE_MAX) && S >= SATURATION_MIN) {
                            double a[] = {(double) (H - HUE_MIN) / HUE_MAX * 255, (double) (H - HUE_MIN) / HUE_MAX * 100, 0};
                            src.put(x, y, a);
                        }
                    }
                }

                Mat tmp = new Mat();
                int an = 5;
                Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(an * 2 + 1, an * 2 + 1), new Point(an, an));
                Imgproc.dilate(src, tmp, element);
                Imgproc.erode(tmp, tmp, element);
                Mat grayscaleMat = new Mat();
                Imgproc.cvtColor(tmp, grayscaleMat, Imgproc.COLOR_BGR2HSV);
                //Делаем бинарную маску
                Mat mask = new Mat(grayscaleMat.size(), grayscaleMat.type());
                Imgproc.threshold(grayscaleMat, mask, 200, 255, Imgproc.THRESH_BINARY_INV);
                //Финальное изображение предварительно красим в белый цвет
                Mat out = new Mat(src.size(), src.type(), Scalar.all(255));
                //Копируем зашумленное изображение через маску
                src.copyTo(out, mask);
                BufferedImage lul = null;
                try {
                    lul = Mat2BufferedImage(src);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                JLabel label = new JLabel(new ImageIcon(lul));
                panel.add(label);
                JFrame frame = new JFrame("ТЮІ");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // add the Jpanel to the main window
                frame.add(panel);

                frame.pack();
                frame.setVisible(true);
            }
        });
        buttonReadImages.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    readImages();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    BufferedImage currim = Mat2BufferedImage(Task23.img);
                    JLabel label = new JLabel(new ImageIcon(currim));
//                    ansImage.add(label);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });
        buttonSolveTask23.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JPanel panel = new JPanel();
                if (Task23.isImageSet) {
                    findTransformMatrix();
                    setValue();
                    try {
                        BufferedImage currim = Mat2BufferedImage(solve());
                        JLabel label = new JLabel(new ImageIcon(currim));
                        //ansImage.add(label);
                        panel.add(label);
                        JFrame frame = new JFrame("ТЮfІ");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                        // add the Jpanel to the main window
                        frame.add(panel);

                        frame.pack();
                        frame.setVisible(true);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }


                }
            }
        });
    }

    public static void loadOpenCV_Lib() throws Exception {
        // get the model
        String model = System.getProperty("sun.arch.data.model");
        // the path the .dll lib location D:\myProjects\TUI\opencv
        String libraryPath = "D:/myProjects/TUI/opencv/build/java/x86/";
        // check for if system is 64 or 32
        if (model.equals("64")) {
            libraryPath = "D:/myProjects/TUI/opencv/build/java/x64/";
        }
        // set the path
        System.setProperty("java.library.path", libraryPath);
        Field sysPath = ClassLoader.class.getDeclaredField("sys_paths");
        sysPath.setAccessible(true);
        sysPath.set(null, null);
        // load the lib
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    protected Mat img2Mat(BufferedImage in) {
        Mat out;
        byte[] data;
        int r, g, b;

        if (in.getType() == BufferedImage.TYPE_INT_RGB) {
            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
            for (int i = 0; i < dataBuff.length; i++) {
                data[i * 3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
                data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
                data[i * 3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
            }
        } else {
            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC1);
            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
            for (int i = 0; i < dataBuff.length; i++) {
                r = (byte) ((dataBuff[i] >> 0) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i] >> 16) & 0xFF);
                data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b));
            }
        }
        out.put(0, 0, data);
        return out;
    }

    public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }

    static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }

    public static void main(String[] args) throws Exception {
        loadOpenCV_Lib();
        System.out.println("rofl");
        JFrame.setDefaultLookAndFeelDecorated(true);
        mainFrame = new JFrame("myGUI");
        mainFrame.setContentPane(new myGUI().MyPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        MyPanel = new JPanel();
        MyPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        JTable = new JTabbedPane();
        MyPanel.add(JTable, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        Task1 = new JPanel();
        Task1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання1", Task1);
        Rofloturik = new JLabel();
        Rofloturik.setText("RAMZES666");
        Task1.add(Rofloturik, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TestButt = new JButton();
        TestButt.setText("Start");
        Task1.add(TestButt, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task2 = new JPanel();
        Task2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання2,3", Task2);
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        Task2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        Task2.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 3, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        buttonReadImages = new JButton();
        buttonReadImages.setText("Вибрати зображення");
        Task2.add(buttonReadImages, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonSolveTask23 = new JButton();
        buttonSolveTask23.setText("Виконати");
        Task2.add(buttonSolveTask23, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ansImage = new JPanel();
        ansImage.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        Task2.add(ansImage, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        Task4 = new JPanel();
        Task4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання4", Task4);
        final JLabel label1 = new JLabel();
        label1.setText("DO UR STUFF HERE");
        Task4.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task5 = new JPanel();
        Task5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання5", Task5);
        final JLabel label2 = new JLabel();
        label2.setText("DO UR STUFF HERE");
        Task5.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task6 = new JPanel();
        Task6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання6", Task6);
        textBOX = new JTextField();
        textBOX.setText("C:\\Users\\Пользователь\\Documents\\statement.png");
        Task6.add(textBOX, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setEnabled(true);
        label3.setText("Шлях до зображення:");
        Task6.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        button1 = new JButton();
        button1.setText("Button");
        Task6.add(button1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MyPanel;
    }

}
