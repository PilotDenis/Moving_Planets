package com.planet;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static com.planet.Main.btnFileFilter;
import static java.lang.Math.abs;

public class Model {
    private static final int MAXQTY = 10; //-- Константа, определяющая максимальное количество планет
    private static int num_of_planets = 0;
    public static Planet[] P = new Planet[MAXQTY];

    private static final float dT = 1f;
    private static final float G = 0.001f;

    private static float t = 0;

    private static float[][] F = new float[MAXQTY][MAXQTY]; // Матрица сил притяжения от i-й планеты до j-й
    private static float[][] Fx = new float[MAXQTY][MAXQTY]; // Матрица сил притяжения от i-й планеты до j-й по горизонтали
    private static float[][] Fy = new float[MAXQTY][MAXQTY]; // Матрица сил притяжения от i-й планеты до j-й по вертикали
    private static float[][] R = new float[MAXQTY][MAXQTY]; // Матрица расстояний от i-й планеты до j-й

    private static int[][] dir_x = new int[MAXQTY][MAXQTY]; // Матрица направлений силы притяжения от i-й планеты до j-й по горизонтали
    private static int[][] dir_y = new int[MAXQTY][MAXQTY]; // Матрица направлений силы притяжения от i-й планеты до j-й по вертикали

    private static float dx, dy;

    public Model(String fname) throws FileNotFoundException {
//--    Считываем начальные данные планет из файла.
        readSettingsFile(fname);
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
                        P[i].vx = ((P[i].m * P[i].vx) + (P[j].m * P[j].vx))  / (P[i].m + P[j].m);
                        P[i].vy = ((P[i].m * P[i].vy) + (P[j].m * P[j].vy))  / (P[i].m + P[j].m);
                        P[i].m += P[j].m;
                        P[i].r += Math.sqrt(P[j].r);
                        P[j].destroyPlanet();
                    } else {
//--                    j-я планета больше по массе, поэтому она поглощает i-ю
                        P[j].vx = ((P[j].m * P[j].vx) + (P[i].m * P[i].vx)) * dir_x[j][i]/ (P[i].m + P[j].m);
                        P[j].vy = ((P[j].m * P[j].vy) + (P[i].m * P[i].vy)) * dir_y[j][i]/ (P[i].m + P[j].m);
                        P[j].m += P[i].m;
                        P[j].r += Math.sqrt(P[i].r);
                        P[i].destroyPlanet();
                    }
                }

//--            Вычисляем итоговые векторы сил для i-й планеты
                Fx_sum += Fx[i][j] * dir_x[i][j];
                Fy_sum += Fy[i][j] * dir_y[i][j];
            }

//--        Вычисляем очередное перемещение для i-й планеты
            if (P[i].alive) {
                P[i].Step(dT, Fx_sum, Fy_sum);
            }

            P[i].move(P[i].x, P[i].y);
        }
        t += dT;
    }

    public static int getNum_of_planets() {
        return num_of_planets;
    }

    public static float getT() {
        return t;
    }


    public static void readSettingsFile(String fname) throws FileNotFoundException {
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
                try {
                    P[num_of_planets] = new Planet(line, num_of_planets);
                    num_of_planets++; // Нумерация планет начинается с нуля. Увеличиваем счетчик планет
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

}
