package com.planet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import static com.planet.Main.*;

public class MyActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "TRIGGER":
                //-- Эта команда запускает и останавливает процесс расчета и визуализации движения планет
                if (started) {
                    started = false;
                    btnStart.setText("Start");
                } else {
                    started = true;
                    btnStart.setText("Stop");
                }
                break;

            case "CONFIG":
                //-- Считываем начальные параметры планет из файла и отрисовываем начальную конфигурацию планет на экране
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
                fileChooser.setDialogTitle("Выберите настроечный файл с расширением .txt");
                // Определяем фильтры типов файлов
                FileFilterExt eff = new FileFilterExt("txt", "Настроечные файлы *.txt");

                fileChooser.addChoosableFileFilter(eff);
                fileChooser.setAcceptAllFileFilterUsed(false);

                // Определение режима - только файл
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int result = fileChooser.showOpenDialog(btnPanel);

                //-- Если файл выбран, то считываем начальные параметры планет из файла и отрисовываем начальную конфигурацию планет на экране
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        cfgFileName = String.valueOf(fileChooser.getSelectedFile());
                        Main.readSettingsFile(cfgFileName);
                        btnStart.requestFocus();
                        frame.repaint();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                break;

            case "RESTART":
                //-- Заново считываем начальные параметры планет из файла и отрисовываем начальную конфигурацию планет на экране
                try {
                    Main.readSettingsFile(cfgFileName);
                    btnStart.requestFocus();
                    frame.repaint();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;

            case "1X":
                //-- Нормальная скорость отсчета времени. Задержка 10мс
                delay = 10;
                break;

            case "5X":
                //-- Ускоренная в 5 раз скорость отсчета времени. Задержка 2мс
                delay = 2;
                break;

        }


    }
}


    // Фильтр выбора файлов определенного типа
    class FileFilterExt extends javax.swing.filechooser.FileFilter {
        String extension;  // расширение файла
        String description;  // описание типа файлов

        FileFilterExt(String extension, String descr) {
            this.extension = extension;
            this.description = descr;
        }

        @Override
        public boolean accept(java.io.File file)
        {
            if(file != null) {
                if (file.isDirectory())
                    return true;
                if( extension == null )
                    return (extension.length() == 0);
                return file.getName().endsWith(extension);
            }
            return false;
        }
        // Функция описания типов файлов
        @Override
        public String getDescription() {
            return description;
        }
    }

