package com.jesz.createdieselgenerators.world;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public final class OilNoise {
    private static final int[][] GRADIENT = {
            {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1},
            {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}
    };

    private final Octave[] octaves = new Octave[4];

    public OilNoise(long seed) {
        PositionalRandomFactory positional = RandomSource.create(seed).forkPositional();
        for (int i = 0; i < octaves.length; i++)
            octaves[i] = new Octave(positional.fromHashOf("octave_" + (i - 2)));
    }

    public double getValue(double x, double y, double z) {
        double value = 0;
        double input = 0.25d;
        double weight = 8d / 15d;
        for (Octave octave : octaves) {
            value += octave.sample(wrap(x * input), wrap(y * input), wrap(z * input)) * weight;
            input *= 2d;
            weight /= 2d;
        }
        return value;
    }

    private static double wrap(double value) {
        return value - Mth.lfloor(value / 3.3554432E7d + 0.5d) * 3.3554432E7d;
    }

    private static double dot(int hash, double x, double y, double z) {
        int[] gradient = GRADIENT[hash & 15];
        return gradient[0] * x + gradient[1] * y + gradient[2] * z;
    }

    private static double lerp(double alpha, double from, double to) {
        return from + alpha * (to - from);
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6d - 15d) + 10d);
    }

    private static final class Octave {
        private final byte[] permutation = new byte[256];
        private final double offsetX;
        private final double offsetY;
        private final double offsetZ;

        private Octave(RandomSource random) {
            offsetX = random.nextDouble() * 256d;
            offsetY = random.nextDouble() * 256d;
            offsetZ = random.nextDouble() * 256d;
            for (int i = 0; i < 256; i++)
                permutation[i] = (byte) i;
            for (int i = 0; i < 256; i++) {
                int shift = random.nextInt(256 - i);
                byte held = permutation[i];
                permutation[i] = permutation[i + shift];
                permutation[i + shift] = held;
            }
        }

        private int at(int index) {
            return permutation[index & 255] & 255;
        }

        private double sample(double x, double y, double z) {
            double px = x + offsetX;
            double py = y + offsetY;
            double pz = z + offsetZ;
            int ix = Mth.floor(px);
            int iy = Mth.floor(py);
            int iz = Mth.floor(pz);
            double fx = px - ix;
            double fy = py - iy;
            double fz = pz - iz;
            int a = at(ix);
            int b = at(ix + 1);
            int aa = at(a + iy);
            int ab = at(a + iy + 1);
            int ba = at(b + iy);
            int bb = at(b + iy + 1);
            double d000 = dot(at(aa + iz), fx, fy, fz);
            double d100 = dot(at(ba + iz), fx - 1d, fy, fz);
            double d010 = dot(at(ab + iz), fx, fy - 1d, fz);
            double d110 = dot(at(bb + iz), fx - 1d, fy - 1d, fz);
            double d001 = dot(at(aa + iz + 1), fx, fy, fz - 1d);
            double d101 = dot(at(ba + iz + 1), fx - 1d, fy, fz - 1d);
            double d011 = dot(at(ab + iz + 1), fx, fy - 1d, fz - 1d);
            double d111 = dot(at(bb + iz + 1), fx - 1d, fy - 1d, fz - 1d);
            double u = fade(fx);
            double v = fade(fy);
            double w = fade(fz);
            return lerp(w, lerp(v, lerp(u, d000, d100), lerp(u, d010, d110)), lerp(v, lerp(u, d001, d101), lerp(u, d011, d111)));
        }
    }
}
