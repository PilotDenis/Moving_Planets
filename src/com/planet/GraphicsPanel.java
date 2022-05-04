package com.planet;

import javax.swing.*;
import java.awt.*;


import static com.planet.Main.*;

public class GraphicsPanel extends JPanel {
    private Model md;
    private Render rd;

    public GraphicsPanel(Model model, Render render) {
        md = model;
        rd = render;
        setBackground(Color.BLACK);
        setLayout(null);
        setBounds(0,btnPanel.getHeight(), frame.getWidth(), frame.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);
//--    Запускаем перерисовку кадра
        rd.Draw(g2, md);
    }
}
