import com.company.PhotosInfo;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
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
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.company.Task23.*;
import static com.company.Task4.solveTask4;
import static com.company.Task5.*;
import static com.teamdev.jxbrowser.engine.RenderingMode.HARDWARE_ACCELERATED;
import static java.lang.Math.sqrt;
import static org.opencv.imgcodecs.Imgcodecs.imread;

public class myGUI extends JFrame {
    private JButton clickMeButton;
    private JPanel MyPanel;
    private JTabbedPane JTable;
    private JPanel Task1;
    private JPanel Task2;
    // private JLabel Rofloturik;
    // private JLabel solo332;
    private JButton TestButt;
    private JPanel Task4;
    private JPanel Task5;
    private JPanel Task6;
    private JButton button1;
    private JButton buttonReadImages;
    private JButton buttonSolveTask23;
    private JButton buttonReadImages2;
    private JButton buttonSolveTask5;
    private JLabel task5Image;
    private JButton buttonNextTask5;
    private JLabel imageTask23;
    private JLabel imageTask6;
    private JButton task6btnChange;
    private JButton button2;
    private JTextField a5TextField;
    private JLabel imageTask4;
    private JLabel label2;
    public static JFrame mainFrame;
    boolean task5 = false, task6 = false;
    BufferedImage startImageTask6 = null, endImageTask6 = null;
    PhotosInfo photosInfo = new PhotosInfo();

    public myGUI() {


        TestButt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                // mainFrame.setEnabled(false);
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
                    frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                    frame.setSize(900, 500);
                    view.setSize(900, 400);
                    frame.add(view);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                    browser.navigation().loadUrl("C:\\Users\\Пользователь\\Desktop\\htdocs\\index.html");
                    frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            super.windowClosing(e);
                            //mainFrame.setEnabled(true);
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

                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    String path1 = file.getPath();
                    try {
                        startImageTask6 = ImageIO.read(new File(path1));
                    } catch (IOException a) {

                    }
                }
                if (startImageTask6 != null) {
                    Mat src = null;
                    try {
                        src = BufferedImage2Mat(startImageTask6);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    double normalS = 500 * 500;
                    double Sc = src.width() * src.height();
                    double k = sqrt(Sc / normalS);
                    if (k < 1) k = 1;
                    Mat tra = new Mat(2, 3, CvType.CV_32FC1);
                    tra.put(0, 0,
                            1 / k, 0, 0,
                            0, 1 / k, 0
                    );
                    Imgproc.warpAffine(src, src, tra, new Size(src.width() / k, src.height() / k));
                    try {
                        startImageTask6 = Mat2BufferedImage(src);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    Mat hsv = new Mat(src.cols(), src.rows(), 3);
                    List<Mat> splitedHsv = new ArrayList<>();
                    Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);
                    Core.split(hsv, splitedHsv);

                    final int HUE_MIN = 7;
                    final int HUE_MAX = 30;
                    final int SATURATION_MIN = 40;
                    final int VALUE_MIN = 100;

                    for (int y = 0; y < hsv.cols(); y++) {
                        for (int x = 0; x < hsv.rows(); x++) {
                            // получаем HSV-компоненты пикселя
                            int H = (int) splitedHsv.get(0).get(x, y)[0];        // Тон
                            int S = (int) splitedHsv.get(1).get(x, y)[0];          // Интенсивность
                            int V = (int) splitedHsv.get(2).get(x, y)[0];          // Яркость
                            //System.out.println(V);
                            //Если яркость слишком низкая либо Тон не попадает у заданный диапазон, то закрашиваем белым
                            if ((H >= HUE_MIN && H <= HUE_MAX)) {
                                double a[] = {(double) (H - HUE_MIN) / HUE_MAX * 255, (double) (H - HUE_MIN) / HUE_MAX * 100, 0};
                                src.put(x, y, a);
                            } else if (S <= SATURATION_MIN) {
                                double a[] = {255, 160, 90};
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
                    try {
                        endImageTask6 = Mat2BufferedImage(src);
                        imageTask6.setIcon(new ImageIcon(endImageTask6));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    task6btnChange.setText("Стартове зображення");
                    task6 = false;
                    task6btnChange.setEnabled(true);
                }
            }
        });
        buttonSolveTask23.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    readImages();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (Task23.isImageSet) {
                    findTransformMatrix();
                    setValue();
                    try {
                        BufferedImage currim = Mat2BufferedImage(solve());
                        imageTask23.setIcon(new ImageIcon(currim));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }


                }
            }
        });
        buttonReadImages2.addKeyListener(new KeyAdapter() {
        });
        buttonReadImages2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    readImages2();
                    task5 = false;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (imageIsSet) {
                    BufferedImage currim = null;
                    try {
                        currim = Mat2BufferedImage(mat1);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    buttonNextTask5.setEnabled(true);
                    buttonSolveTask5.setEnabled(true);
                    task5Image.setIcon(new ImageIcon((currim)));
                } else {
                    buttonNextTask5.setEnabled(false);
                    buttonSolveTask5.setEnabled(false);
                }
            }
        });
        buttonSolveTask5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (imageIsSet) {
                    solvetask5();
                    BufferedImage currim = null;
                    try {
                        if (!task5)
                            currim = Mat2BufferedImage(mat1);
                        else currim = Mat2BufferedImage(mat2);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    task5Image.setIcon(new ImageIcon(currim));
                }

            }
        });
        buttonNextTask5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (imageIsSet) {
                    if (task5) {
                        task5 = false;
                        buttonNextTask5.setText("Друге зображення");
                        BufferedImage currim = null;
                        try {
                            currim = Mat2BufferedImage(mat1);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        task5Image.setIcon(new ImageIcon(currim));

                    } else {
                        task5 = true;
                        buttonNextTask5.setText("Перше зображення");
                        BufferedImage currim = null;
                        try {
                            currim = Mat2BufferedImage(mat2);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        task5Image.setIcon(new ImageIcon(currim));
                    }
                }
            }
        });
        task6btnChange.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (!task6) {
                    imageTask6.setIcon(new ImageIcon(startImageTask6));
                    task6btnChange.setText("Фінальне зображення");
                    task6 = true;
                } else {
                    task6 = false;
                    imageTask6.setIcon(new ImageIcon(endImageTask6));
                    task6btnChange.setText("Стартове зображення");
                }
            }
        });
        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String b = a5TextField.getText();
                int f = 0;
                for (int i = 0; i < b.length(); i++)
                    f = f * 10 + (int) (b.charAt(i) - '0');
                boolean verify = false;
                for (int i = 0; i < f; i++) {
                    JFileChooser fileopen = new JFileChooser();
                    int ret;
                    String path1;
                    Image IMG = null;
                    ret = fileopen.showDialog(null, "Открыть файл");
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        File file = fileopen.getSelectedFile();
                        path1 = file.getPath();

                        try {
                            IMG = ImageIO.read(new File(path1));
                        } catch (IOException a) {

                        }

                    }
                    JTextField field1 = new JTextField();
                    JTextField field2 = new JTextField();
                    JTextField field3 = new JTextField();
                    JTextField field4 = new JTextField();
                    JTextField field5 = new JTextField();
                    JTextField field6 = new JTextField();
                    Object[] message = {
                            "Довгота", field1,
                            "Широта", field2,
                            "Висота", field3,
                            "Рискання", field4,
                            "Крен", field5,
                            "Тангаж", field6,
                    };
                    int option = JOptionPane.showConfirmDialog(null, message, "Введіть значення", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        String valueX = field1.getText();
                        String valueY = field2.getText();
                        String valueZ = field3.getText();
                        String valueYaw = field4.getText();
                        String valueRoll = field5.getText();
                        String valuePitch = field6.getText();
                        photosInfo.receivePhoto(IMG, Double.parseDouble(valueZ), Double.parseDouble(valueX),
                                Double.parseDouble(valueY), Double.parseDouble(valueYaw),
                                Double.parseDouble(valueRoll), Double.parseDouble(valuePitch));
                    } else {
                        verify = true;
                        break;
                    }

                }
                if (!verify) {
                    List<Image> lsImages = solveTask4();
                    if (lsImages.size() != 0) {
                        BufferedImage tmp123 = (BufferedImage) lsImages.get(0);
                        Mat src = null;
                        try {
                            src = BufferedImage2Mat(tmp123);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        double normalS = 500 * 500;
                        double Sc = src.width() * src.height();
                        double k = sqrt(Sc / normalS);
                        if (k < 1) k = 1;
                        Mat tra = new Mat(2, 3, CvType.CV_32FC1);
                        tra.put(0, 0,
                                1 / k, 0, 0,
                                0, 1 / k, 0
                        );
                        Imgproc.warpAffine(src, src, tra, new Size(src.width() / k, src.height() / k));
                        try {
                            imageTask4.setIcon(new ImageIcon(Mat2BufferedImage(src)));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } else {

                    }
                }
            }
        });
    }

    public static BufferedImage scaleImage(BufferedImage before, double size) {
        int w = before.getWidth();
        int h = before.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(size / w, size / w);
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(before, after);
        return after;
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
        mainFrame = new JFrame("TUI");
        mainFrame.setContentPane(new myGUI().MyPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
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
        MyPanel.add(JTable, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        Task1 = new JPanel();
        Task1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання1", Task1);
        TestButt = new JButton();
        TestButt.setText("Start");
        Task1.add(TestButt, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task2 = new JPanel();
        Task2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання2,3", Task2);
        imageTask23 = new JLabel();
        imageTask23.setIcon(new ImageIcon(getClass().getResource("/dijkstra.png")));
        imageTask23.setText("");
        Task2.add(imageTask23, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonSolveTask23 = new JButton();
        buttonSolveTask23.setText("Виконати");
        Task2.add(buttonSolveTask23, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task4 = new JPanel();
        Task4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання4", Task4);
        a5TextField = new JTextField();
        a5TextField.setText("5");
        Task4.add(a5TextField, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 2, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        imageTask4 = new JLabel();
        imageTask4.setIcon(new ImageIcon(getClass().getResource("/dijkstra.png")));
        imageTask4.setText("");
        Task4.add(imageTask4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        button2 = new JButton();
        button2.setText("Button");
        Task4.add(button2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        Task5 = new JPanel();
        Task5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 10, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання5", Task5);
        task5Image = new JLabel();
        task5Image.setIcon(new ImageIcon(getClass().getResource("/dijkstra.png")));
        task5Image.setText("");
        task5Image.setVerticalAlignment(0);
        task5Image.setVerticalTextPosition(0);
        Task5.add(task5Image, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 10, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(20, 20), null, 0, false));
        buttonNextTask5 = new JButton();
        buttonNextTask5.setEnabled(false);
        buttonNextTask5.setText("Друге зображення");
        Task5.add(buttonNextTask5, new com.intellij.uiDesigner.core.GridConstraints(0, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonReadImages2 = new JButton();
        buttonReadImages2.setText("Виберіть зображення");
        Task5.add(buttonReadImages2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        buttonSolveTask5 = new JButton();
        buttonSolveTask5.setEnabled(false);
        buttonSolveTask5.setText("Виконати");
        Task5.add(buttonSolveTask5, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        Task6 = new JPanel();
        Task6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        JTable.addTab("Завдання6", Task6);
        imageTask6 = new JLabel();
        imageTask6.setIcon(new ImageIcon(getClass().getResource("/dijkstra.png")));
        imageTask6.setText("");
        Task6.add(imageTask6, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        button1 = new JButton();
        button1.setText("Виконати");
        Task6.add(button1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        task6btnChange = new JButton();
        task6btnChange.setEnabled(false);
        task6btnChange.setText(" Стартове зображення");
        Task6.add(task6btnChange, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MyPanel;
    }

}


