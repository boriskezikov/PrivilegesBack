package ru.hse.web.service;

import java.util.Random;

public class Utils {

    public static String generateFactorCode() {
        return String.valueOf(new Random().nextInt(9995));
    }

    public static String buildFullName(String f, String l) {
        return f + ' ' + l;
    }
}
