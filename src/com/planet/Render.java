package com.planet;

import java.awt.*;

import static com.planet.Main.*;

public class Render {
    private Model md;

    public Render(Model model) {
        md = model;
        //--    Создаем и запускаем поток
        Thread MyThread = new Thread(new Render.Runner(model));
        MyThread.start();
    }

//-- Перерисовываем планеты с их траекториями
    public void Draw(Graphics2D g2, Model model) {

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

//--    Выводим фоновое изображение графической панели
        if(backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, null);
        }

//--    Отрисовываем планеты
        for (int i = 0; i < model.getNum_of_planets(); i++) {
            if (!md.P[i].alive) continue;
            g2.setColor(md.P[i].getColor());
            g2.fill(md.P[i]);
            g2.draw(md.P[i]);
            if (trace) g2.draw(md.P[i].tail);
        }

//--    Актуализируем счетчик времени
        tLabel.setText("T = " + String.valueOf(Math.round(md.getT() / 63.9055f)));
    }

    class Runner implements Runnable {
        private Model md;

        public Runner(Model model) {
            md = model;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (started) md.calculation();

                    Thread.sleep(delay);

                    if(panel != null) panel.repaint();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}