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

import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static java.lang.System.exit;
import static org.opencv.imgcodecs.Imgcodecs.imread;

public class myGUI extends JFrame {
    private JButton clickMeButton;
    private JPanel MyPanel;
    private JTabbedPane JTable;
    private JPanel Task1;
    private JPanel Task2;
    private JPanel Task3;
    private JLabel Rofloturik;
    private JLabel solo332;
    private JButton TestButt;
    private JPanel Task4;
    private JPanel Task5;
    private JPanel Task6;
    private JButton button1;
    private JTextField textBOX;
    static JFrame frame;
    static Browser browser;
    //   static public JFrame frameSecond;
    //  static JFrame frame;

    public myGUI() {
        Rofloturik.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        solo332.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });

        TestButt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                browser.mainFrame().ifPresent(frame ->
                {
                    JsObject tmp = frame.executeJavaScript("getLat");
                    System.out.println(tmp.propertyNames().size());
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
                Mat hsv = new Mat(src.cols(), src.rows(), 3);
                List<Mat> splitedHsv = new ArrayList<>();
                Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);
                Core.split(hsv, splitedHsv);

                final int GREEN_MIN = 21;
                final int GREEN_MAX = 110;

                for (int y = 0; y < hsv.cols(); y++) {
                    for (int x = 0; x < hsv.rows(); x++) {
                        // получаем HSV-компоненты пикселя
                        int H = (int) splitedHsv.get(0).get(x, y)[0];        // Тон
                        int S = (int) splitedHsv.get(1).get(x, y)[0];          // Интенсивность
                        int V = (int) splitedHsv.get(2).get(x, y)[0];          // Яркость

                        //Если яркость слишком низкая либо Тон не попадает у заданный диапазон, то закрашиваем белым
                        if ((V < 20) || (H < GREEN_MIN) || (H > GREEN_MAX)) {
                          //  System.out.println(x + " " + y)
                            double a[] = {255, 255, 255};
                            src.put(x, y, a);
                           // src.get(x, y)[0] = 255;
                         //   src.get(x, y)[1] = 255;
                         //   src.get(x, y)[2] = 255;
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
                JFrame frame = new JFrame("JPanel Example");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // add the Jpanel to the main window
                frame.add(panel);

                frame.pack();
                frame.setVisible(true);
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

    public static Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
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
        // Create and initialize the Engine
        System.out.println("rofl");
        EngineOptions options =
                EngineOptions.newBuilder(HARDWARE_ACCELERATED)
                        .licenseKey("1BNDHFSC1FT94V3KH87EQ2JITA57T339AO7YWVTZLML2HG6ALBOANHYUW0CU4PTAHTR9D3")
                        .build();
        Engine engine = Engine.newInstance(options);

// Create the Browser
        browser = engine.newBrowser();
        SwingUtilities.invokeLater(() -> {
// Create the Swing BrowserView component
            BrowserView view = BrowserView.newInstance(browser);
            frame = new JFrame("Misha Lox");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(900, 500);
            view.setSize(900, 400);

            // frame.setLayout(new FlowLayout());
            JButton b1 = new JButton();
         /*   b1.setSize(50, 50);
            b1.setVisible(true);
            b1.setText("HelloWorld");

            frame.add(b1);*/
            frame.add(view);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            browser.navigation().loadUrl("D://test.html");


            // Element body = frame.executeJavaScript("document.body");
            //frame.executeJavaScript("document.body", (Consumer<Element>) body -> {
            //  String html = body.innerHtml();
            //});
        });
        // Mat src = imread("1.jpg");

        JFrame.setDefaultLookAndFeelDecorated(true);

        //ImageIcon icon = new ImageIcon(img);
        //JLabel lbl = new JLabel();
        //lbl.setIcon(icon);
        JFrame frameSecond = new JFrame("myGUI");
        frameSecond.setContentPane(new myGUI().MyPanel);
        // frameSecond.add(lbl);
        frameSecond.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frameSecond.add(panel);
        frameSecond.pack();
        frameSecond.setVisible(true);
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
        Task1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання1", Task1);
        Rofloturik = new JLabel();
        Rofloturik.setText("RAMZES666");
        Task1.add(Rofloturik, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TestButt = new JButton();
        TestButt.setText("Start");
        Task1.add(TestButt, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task2 = new JPanel();
        Task2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання2", Task2);
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        Task2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        Task2.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        solo332 = new JLabel();
        solo332.setText("DO UR STUFF HERE");
        Task2.add(solo332, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task3 = new JPanel();
        Task3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання3", Task3);
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        Task3.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        Task3.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("DO UR STUFF HERE");
        Task3.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task4 = new JPanel();
        Task4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання4", Task4);
        final JLabel label2 = new JLabel();
        label2.setText("DO UR STUFF HERE");
        Task4.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task5 = new JPanel();
        Task5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання5", Task5);
        final JLabel label3 = new JLabel();
        label3.setText("DO UR STUFF HERE");
        Task5.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task6 = new JPanel();
        Task6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання6", Task6);
        textBOX = new JTextField();
        textBOX.setText("C:\\Users\\Пользователь\\Documents\\statement.png");
        Task6.add(textBOX, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setEnabled(true);
        label4.setText("Шлях до зображення:");
        Task6.add(label4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
