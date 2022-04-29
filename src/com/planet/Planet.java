package com.planet;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Planet extends Ellipse2D {
    public float x; //-- Координата для расчета
    public float y; //-- Координата для расчета
    private double xDraw; //-- Координата для отображения
    private double yDraw; //-- Координата для отображения
    public float vx;
    public float vy;
    public float ax;
    public float ay;

    public float m;

    public int id;
    public boolean alive = true;
    public double r;
    private Color color;

    public Planet(float x0, float y0, float v0x, float v0y, float _m, double r, Color color, int planet_id) {
        id = planet_id;
//--    Координаты для отображения окружности - это левый верхний угол квадрата, описывающего окружность, а не ее центр
        xDraw = x0 - (float) r;
        yDraw = y0 - (float) r;

//--    Координаты для расчета
        this.x = x0;
        this.y = y0;

        vx = v0x;
        vy = v0y;
        m = _m;

        ax = 0;
        ay = 0;

        this.r = r;
        this.color = color;
    }

    public void destroyPlanet() {
        m = 0;
        r = 0;
//        vx = 0;
//        vy = 0;
//        ax = 0;
//        ay = 0;
        alive = false;
    }

    public void move(double new_x, double new_y) {
//--    Новые расчетные координаты
        x = (float) new_x;
        y = (float) new_y;

//--    Координаты для отображения окружности - это левый верхний угол квадрата, описывающего окружность, а не ее центр
        xDraw = x - (float) r;
        yDraw = y - (float) r;
    }

    public Color getColor() {
        return color;
    }

    public void Step(float _dt, float _Fx, float _Fy) {
//--    Расчитываем ускорение планеты по Х и У на данном шаге
        ax = _Fx / m;
        ay = _Fy / m;

//--    Расчитываем скорости планеты по Х и У на данном шаге
        vx = vx + ax * _dt;
        vy = vy + ay * _dt;

//--    Расчитываем координаты планеты Х и У на данном шаге
        x = x + vx * _dt;
        y = y + vy * _dt;

//--    Координаты для отображения окружности - это левый верхний угол квадрата, описывающего окружность, а не ее центр
        xDraw = x - (float) r;
        yDraw = y - (float) r;
    }


    @Override
    public double getX() {
        return xDraw;
    }

    @Override
    public double getY() {
        return yDraw;
    }

    @Override
    public double getWidth() {
        return 2 * r;
    }

    @Override
    public double getHeight() {
        return 2 * r;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setFrame(double x, double y, double r, double h) {
        xDraw = (float) x;
        yDraw = (float) y;
        this.r = r;
    }

    @Override
    public Rectangle2D getBounds2D() {
        return null;
    }
}
