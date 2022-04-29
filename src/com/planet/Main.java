package com.planet;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static java.lang.Math.abs;

public class Main {
    public static JFrame frame, frame_;
    public static GraphicsPanel panel;
    public static String cfgFileName = "Planet_settings.txt";
    public static JPanel btnPanel;
    public static JLabel tLabel;
    public static JButton btnStart;
    public static JButton btnRestart;
    public static JButton btnFileFilter;
    public static JButton btn1x;
    public static JButton btn5x;
    public static JCheckBox cbTrace;
    public static boolean notrace = false;
    public static Image backgroundImage;
    public static ImageIcon icon;
    public static JLabel contentPane;
    public static BackgroundPanel b_panel;
    public static int num_of_planets = 0;
    static Planet[] P = new Planet[10];

    private static final float dT = 1f;
    private static final float G = 0.001f;

    public static float t = 0;

    public static boolean started = false;
    public static int delay = 10;

    private static float[][] F = new float[10][10]; // Матрица сил притяжения от i-й планеты до j-й
    private static float[][] Fx = new float[10][10]; // Матрица сил притяжения от i-й планеты до j-й по горизонтали
    private static float[][] Fy = new float[10][10]; // Матрица сил притяжения от i-й планеты до j-й по вертикали
    private static float[][] R = new float[10][10]; // Матрица расстояний от i-й планеты до j-й

    private static int[][] dir_x = new int[10][10]; // Матрица направлений силы притяжения от i-й планеты до j-й по горизонтали
    private static int[][] dir_y = new int[10][10]; // Матрица направлений силы притяжения от i-й планеты до j-й по вертикали

    private static float dx, dy;

    public static void main(String[] args) throws FileNotFoundException {
//--    Локализация компонентов окна JFileChooser
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
        UIManager.put("FileChooser.lookInLabelText", "Директория");
        UIManager.put("FileChooser.saveInLabelText", "Сохранить в директории");
        UIManager.put("FileChooser.folderNameLabelText", "Путь директории");

        String s = "";
//--    Если запускаем с аргументом notrace, то планеты не будут оставлять следы
//      По умолчанию запускаем без аргумента, то есть планеты будут оставлять следы своей траектории
        try {
            s = args[0];
        } catch (Exception e) {
        }
        notrace = s.equalsIgnoreCase("notrace");

        try {
            File bkgr_f = new File("Space_background.jpg");
            backgroundImage = ImageIO.read(bkgr_f);
            icon = new ImageIcon("Space_background.jpg");
//--        Создаем вспомогательную графическую панель для отображения фоновой картинки
            b_panel = new BackgroundPanel(backgroundImage);

        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found: Space_background.jpg");
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot read file: Space_background.jpg");
            e.printStackTrace();
        }


//--    Создаем графическое окно
        frame = new JFrame();
        frame.setTitle("Moving Planet");
        frame.setLocationRelativeTo(null);
        frame.setLocation(0, 0);
        frame.setLayout(null);
        frame.setSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width - 400, Toolkit.getDefaultToolkit().getScreenSize().height - 300));
        frame.setExtendedState(frame.MAXIMIZED_BOTH);
        frame.setBackground(Color.RED); // -- Для отладки
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame_ = new JFrame();
        frame_.setTitle("Graphic panel");
        frame_.setLocationRelativeTo(null);
        frame_.setLocation(50, 50);
        frame_.setLayout(null);
        frame_.setSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width - 600, Toolkit.getDefaultToolkit().getScreenSize().height - 500));
//        frame.setExtendedState(frame.MAXIMIZED_BOTH);
        frame_.setBackground(Color.RED); // -- Для отладки
        frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


//        contentPane = new JLabel();
//        contentPane.setIcon(icon);
//        frame.setContentPane(contentPane);
        frame.setVisible(true);


        btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBounds(0,0, 650, 36);
        ActionListener al = new MyActionListener();

//--    Создаем кнопку выбора файла конфигурации в панели кнопок
        btnFileFilter = new JButton("Select config");
        btnFileFilter.setActionCommand("CONFIG");
        btnFileFilter.addActionListener(al);
        btnPanel.add(btnFileFilter);

//--    Создаем кнопку Start / Stop в панели кнопок
        btnStart = new JButton("Start");
        btnStart.setActionCommand("TRIGGER");
        btnStart.addActionListener(al);
        btnPanel.add(btnStart);


//--    Создаем текстовый лэйбл для отображения счетчика времени в панели кнопок
        tLabel = new JLabel("T = 0");
        btnPanel.add(tLabel);

//--    Создаем кнопки управления масштабом времени 1х и 5х в панели кнопок
        btn1x = new JButton("1x");
        btn1x.setActionCommand("1X");
        btn1x.addActionListener(al);
        btnPanel.add(btn1x);

        btn5x = new JButton("5x");
        btn5x.setActionCommand("5X");
        btn5x.addActionListener(al);
        btnPanel.add(btn5x);

//--    Создаем кнопку Restart в панели кнопок
        btnRestart = new JButton("Restart");
        btnRestart.setActionCommand("RESTART");
        btnRestart.addActionListener(al);
        btnPanel.add(btnRestart);

//--    Создаем чекбокс отрисовки следа планет в панели кнопок
        cbTrace = new JCheckBox("Trace");
        cbTrace.setSelected(!notrace);
        cbTrace.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                notrace = !notrace;
                btnStart.requestFocus();
            }
        });
        btnPanel.add(cbTrace);

//--    Добавляем панель с кнопками в окно frame
        frame.add(btnPanel);
        btnStart.requestFocus();

//--    Создаем основную графическую панель в окне
        panel = new GraphicsPanel();
        frame.add(panel);


        if(b_panel != null) {
            b_panel.setBounds(0,36, frame.getWidth(), frame.getHeight());
            frame.add(b_panel);
        }


        frame.setVisible(true);
//        frame_.setAlwaysOnTop(true);
//        frame_.setVisible(true);
        readSettingsFile(cfgFileName);

    }

    public static void readSettingsFile(String fname) throws FileNotFoundException {
        float f1, f2, f3, f4, m;
        double r;
        int cr, cg, cb;
        Color col;

//--    Считываем начальные данные планет из файла.
//      Для каждой планеты своя строка. Значения разделяются символом ";"
//      Формат строки:
//      x0; y0; v0x; v0y; Mass; Radius; Цвет круга в формате R;G;B

        num_of_planets = 0;
        t = 0;

        try {
            File set_f = new File(fname);
            Scanner scan = new Scanner(set_f);

            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if(line.equals("")) break;
                String[] words = line.split(";");
                try {
                    f1 = Float.parseFloat(words[0].trim());
                    f2 = Float.parseFloat(words[1].trim());
                    f3 = Float.parseFloat(words[2].trim());
                    f4 = Float.parseFloat(words[3].trim());
                    m = Float.parseFloat(words[4].trim());
                    r = Double.parseDouble(words[5].trim());
                    cr = Integer.parseInt(words[6].trim());
                    cg = Integer.parseInt(words[7].trim());
                    cb = Integer.parseInt(words[8].trim());
                    col = new Color(cr, cg, cb);

                    P[num_of_planets] = new Planet(f1, f2, f3, f4, m, r, col, num_of_planets);
                    num_of_planets++; // Нумерация планет начинается с нуля. Увеличиваем счетчик
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Wrong number format in the file " + fname);
                    e.printStackTrace();
                    scan.close();
                    System.exit(2);
                }

            };
            scan.close();
        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found: " + fname);
            btnFileFilter.doClick();
        }

    }

    //- Все основные калькуляции сил, расстояний между планетами, векторов их движения происходят в процедуре calculation
    public static void calculation() {
        float Fx_sum, Fy_sum;
        float minDist;

        for(int i = 0; i < num_of_planets; i++) {
            if (!P[i].alive) continue;
            Fx_sum = 0;
            Fy_sum = 0;
            for (int j = 0; j < num_of_planets; j++) {
                if ((i == j) | (!P[j].alive)) continue;

//--            Вычисляем расстояния между планетами
                dx = abs(P[i].x - P[j].x);
                dy = abs(P[i].y - P[j].y);
                R[i][j] = (float) Math.sqrt(dx * dx + dy * dy);

//--            Вычисляем силы притяжения между планетами по модулю
                minDist = (float) (P[i].r + P[j].r);
                if (R[i][j] > minDist ) {
                    F[i][j]  = F[j][i]  = (G * P[i].m * P[j].m) / (R[i][j] * R[i][j]);
                    Fx[i][j] = Fx[j][i] = F[i][j] * dx / R[i][j];
                    Fy[i][j] = Fy[j][i] = F[i][j] * dy / R[i][j];
                } else {
//--                Расстояние между планетами стало меньше критического. Слияние i-й и j-й планеты
                    Fx[i][j] = 0;
                    Fy[i][j] = 0;
                    Fx[j][i] = 0;
                    Fy[j][i] = 0;
                    if(P[i].m >= P[j].m) {
//--                    i-я планета больше по массе, поэтому она поглощает j-ю
                        P[i].vx = ((P[i].m * P[i].vx) + (P[j].m * P[j].vx)) * dir_x[i][j] / (P[i].m + P[j].m);
                        P[i].vy = ((P[i].m * P[i].vy) + (P[j].m * P[j].vy)) * dir_y[i][j] / (P[i].m + P[j].m);
                        P[i].m += P[j].m;
                        P[i].r += Math.sqrt(P[j].r);
//                        P[i].move((P[i].x + P[j].x)/2, (P[i].y + P[j].y)/2 );
                        P[j].destroyPlanet();
                    } else {
//--                    j-я планета больше по массе, поэтому она поглощает i-ю
                        P[j].vx = ((P[j].m * P[j].vx) + (P[i].m * P[i].vx)) * dir_x[j][i]/ (P[i].m + P[j].m);
                        P[j].vy = ((P[j].m * P[j].vy) + (P[i].m * P[i].vy)) * dir_y[j][i]/ (P[i].m + P[j].m);
                        P[j].m += P[i].m;
                        P[j].r += Math.sqrt(P[i].r);
//                        P[j].move((P[i].x + P[j].x)/2, (P[i].y + P[j].y)/2);
                        P[i].destroyPlanet();
                    }
                }

//--            Вычисляем направления действия сил от каждой планеты по горизонтали и по вертикали
                if(P[i].x <= P[j].x) {
                    dir_x[i][j] = 1;
                    dir_x[j][i] = -1;
                } else {
                            dir_x[i][j] = -1;
                            dir_x[j][i] = 1;
                        }

                if(P[i].y <= P[j].y) {
                    dir_y[i][j] = 1;
                    dir_y[j][i] = -1;
                } else  {
                            dir_y[i][j] = -1;
                            dir_y[j][i] = 1;
                        }

//--            Вычисляем итоговые векторы сил для i-й планеты
                Fx_sum += Fx[i][j] * dir_x[i][j];
                Fy_sum += Fy[i][j] * dir_y[i][j];
            }

//--        Вычисляем очередное перемещение для i-й планеты
            if (P[i].alive) {
                P[i].Step(dT, Fx_sum, Fy_sum);
            }
        }
        t += dT;
    }
}
