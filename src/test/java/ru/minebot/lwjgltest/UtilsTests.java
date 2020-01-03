package ru.minebot.lwjgltest;

import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec;
import com.hackoeur.jglm.Vec3;
import org.junit.*;
import ru.minebot.lwjgltest.utils.Utils;

import java.util.Random;

import static org.junit.Assert.*;

public class UtilsTests {

    private Random rnd = new Random();

    @Test
    public void testGetEulerFromDirection() throws Exception {
        for (int i = 0; i < 10; i++) {
            Vec3 dir = new Vec3(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat()).getUnitVector();
            Vec3 euler = Utils.getEulerFromDirection(dir);
            Vec3 rotated = Utils.multiply(Utils.rotation(euler), new Vec3(1, 0, 0));
            assertArrayEquals(new float[]{ dir.getX(), dir.getY(), dir.getZ() }, new float[]{ rotated.getX(), rotated.getY(), rotated.getZ() }, 0.2f);
        }
    }

    @Test
    public void testRotation() throws Exception {
        for (int i = 0; i < 10; i++) {
            Vec3 angles = new Vec3(0, rnd.nextFloat() * 2f * (float)Math.PI, rnd.nextFloat() * 2f * (float)Math.PI);
            Vec3 rot0 = Utils.multiply(Matrices.rotate(angles.getY(), new Vec3(0, 1, 0)), Utils.multiply(Matrices.rotate(angles.getZ(), new Vec3(0, 0, 1)), new Vec3(1, 0, 0)));
            Vec3 rot1 = Utils.multiply(Utils.rotation(angles), new Vec3(1, 0, 0));
            assertArrayEquals(new float[]{ rot0.getX(), rot0.getY(), rot0.getZ() }, new float[]{ rot1.getX(), rot1.getY(), rot1.getZ() }, 0.1f);
        }
    }
}
