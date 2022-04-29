package com.planet;

import javax.swing.*;
import java.awt.*;


import static com.planet.Main.*;

public class GraphicsPanel extends JPanel {
    private static boolean first = true;
    public GraphicsPanel() {
        setBackground(Color.BLACK);
        setLayout(null);
        setBounds(0,btnPanel.getHeight(), frame.getWidth(), frame.getHeight());

//--    Создаем и запускаем поток
        Thread MyThread = new Thread(new Runner());
        MyThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

//--    Вызов метода super.paintComponent(g) перерисовывает окно и траектории планет стираются
        if(notrace) {
            super.paintComponent(g);
//--    Устанавливаем фоновое изображение графической панели
            if(backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, null);
            }
        }

//--    Отрисовываем планеты
        for (int i = 0; i < num_of_planets; i++) {
            if (!P[i].alive) continue;
            g2.setColor(P[i].getColor());
            g2.fill(P[i]);
            g2.draw(P[i]);
        }

//--    Актуализируем счетчик времени
        tLabel.setText("T = " + String.valueOf(Math.round(t / 63.9055f)));

    }

    class Runner implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    if(started) {
                        calculation();
                        for (int i = 0; i < num_of_planets; i++) {
                            P[i].move(P[i].x, P[i].y);
                        }

                        Thread.sleep(delay);

                        repaint();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
