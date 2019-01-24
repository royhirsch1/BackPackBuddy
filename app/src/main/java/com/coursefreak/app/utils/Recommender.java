package com.coursefreak.app;

import android.util.Log;

import java.util.*;
import java.io.*;
import java.lang.Math;
import java.util.Random;

public class Recommender {


    public static class Pair<A,B>
    {
        public final A a;
        public final B b;

        public Pair(A a, B b)
        {
            this.a = a;
            this.b = b;
        }

        public A getFirst() { return this.a; }
        public B getSecond() { return this.b; }
    }

    public static void myRecommender(double[][] matrix, int r, double rate, double lambda,
                                     double[][] outputU, double[][]outputV)
    {
        int maxIter = 10;
        int n1 = matrix.length;
        int n2 = matrix[0].length;

        double[][] U = new double[n1][r];
        zeroMatrix(U);
        double[][] V = new double[n2][r];
        zeroMatrix(V);

//            for(int i = 0; i < n1; i++) {
//                for(int j = 0; j < n2; j++) {
//
//                }
//            }

        // Initialize U and V matrix
        Random rand = new Random();
        for (int i = 0; i < U.length; ++i)
        {
            for (int j = 0; j < U[0].length; ++j)
            {
                U[i][j] = rand.nextDouble() / (1.0 * r * 1.0);
//                Log.d("Completion", "U0, " +Double.toString(U[i][j]));
            }
        }

        for (int i = 0; i < V.length; ++i)
        {
            for (int j = 0; j < V[0].length; ++j)
            {
                V[i][j] = rand.nextDouble() / (1.0 * r * 1.0);
//                Log.d("Completion", "V0, "+Double.toString(V[i][j]));
            }
        }

        for (int iter = 0; iter < maxIter; ++iter)
        {
            double[][] prodMatrix = new double[n1][n2];
            for (int i = 0; i < n1; ++i)
            {
                for (int j = 0; j < n2; ++j)
                {
                    for (int k = 0; k < r; ++k)
                    {
                        prodMatrix[i][j] += U[i][k]*V[j][k];
                    }
                }
            }

            double[][] errorMatrix = new double[n1][n2];
            for (int i = 0; i < n1; ++i)
            {
                for (int j = 0; j < n2; ++j)
                {
                    if (matrix[i][j] == -1f)
                    {
                        errorMatrix[i][j] = 0f;
                    }
                    else
                    {
                        errorMatrix[i][j] = matrix[i][j] - prodMatrix[i][j];
                    }
                }
            }

            double[][] UGrad = new double[n1][r];
            for (int i = 0; i < n1; ++i)
            {
                for (int j = 0; j < r; ++j)
                {
                    for (int k = 0; k < n2; ++k)
                    {
                        UGrad[i][j] += errorMatrix[i][k]*V[k][j];
                    }
                }
            }

            double[][] VGrad = new double[n2][r];
            for (int i = 0; i < n2; ++i)
            {
                for (int j = 0; j < r; ++j)
                {
                    for (int k = 0; k < n1; ++k)
                    {
                        VGrad[i][j] += errorMatrix[k][i]*U[k][j];
                    }
                }
            }

            double[][] Un = new double[n1][r];
            for (int i = 0; i < n1; ++i)
            {
                for (int j = 0; j < r; ++j)
                {
                    Un[i][j] = (1f - rate*lambda)*U[i][j] + rate*UGrad[i][j];
                }
            }

            double[][] Vn = new double[n2][r];
            for (int i = 0; i < n2; ++i)
            {
                for (int j = 0; j < r; ++j)
                {
                    Vn[i][j] = (1f - rate*lambda)*V[i][j] + rate*VGrad[i][j];
                }
            }

            U = Un;
            V = Vn;
        }

        for(int i = 0; i < n1; i++) {
            for (int j = 0; j < r; j++) {
                if(Double.isNaN(U[i][j]) || Double.isInfinite(U[i][j])) {
                   // Log.d("Completion", "BAD!!!");
                    return;
                }
                outputU[i][j] = U[i][j];

            }
        }
        for(int i = 0; i < n2; i++) {
            for (int j = 0; j < r; j++) {
                outputV[i][j] = V[i][j];
            }
        }
    }

    public static void PredictRating(double[][] U, double[][] V, double[][] outputPredictions) {
        int n1 = U.length;
        int n2 = V.length;
        int r = V[0].length;

        zeroMatrix(outputPredictions);

        for (int i = 0; i < n1; ++i)
        {
            for (int j = 0; j < n2; ++j)
            {
                for (int k = 0; k < r; ++k)
                {
                    outputPredictions[i][j] += U[i][k]*V[j][k];
                }
            }
        }
    }

    public static void zeroMatrix(double[][] matrix) {
        int len = matrix.length;
        int wid = matrix[0].length;

        for(int i = 0; i < len; i++) {
            for(int j = 0; j < wid; j++) {
                matrix[i][j] = 0.0;
            }
        }
    }
}
