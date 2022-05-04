package com.planet;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static java.lang.Math.abs;

public class Main {

    public static JFrame frame;
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
    public static boolean trace = true;
    public static Image backgroundImage;

    public static boolean started = false;
    public static int delay = 10;

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

        try {
//--        Загружаем файл для отображения фоновой картинки
            File bkgr_f = new File("res/Space_background.jpg");
            backgroundImage = ImageIO.read(bkgr_f);
        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found: Space_background.jpg");
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot read file: Space_background.jpg");
            e.printStackTrace();
        }

//--    Создаем множество планет. Данные считываем из конфигурационного файла
        Model model = new Model(cfgFileName);
        Render render = new Render(model);

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
        frame.setVisible(true);

        btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBounds(0,0, Toolkit.getDefaultToolkit().getScreenSize().width, 36);
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
        cbTrace.setSelected(trace);
        cbTrace.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                trace = !trace;
                btnStart.requestFocus();
            }
        });
        btnPanel.add(cbTrace);

//--    Добавляем панель с кнопками в окно frame
        frame.add(btnPanel);
        btnStart.requestFocus();

//--    Создаем основную графическую панель в окне
        panel = new GraphicsPanel(model, render);
        frame.add(panel);

        frame.setVisible(true);
    }
}
