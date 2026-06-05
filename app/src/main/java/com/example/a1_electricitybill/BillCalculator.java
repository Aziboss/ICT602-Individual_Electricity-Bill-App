package com.example.a1_electricitybill;

public class BillCalculator {

    // Returns total charges in RM
    public static double calculateTotalCharges(int units) {
        int remaining = units;
        double total = 0.0;

        if (remaining > 600) {
            total += (remaining - 600) * 54.6;
            remaining = 600;
        }
        if (remaining > 300) {
            total += (remaining - 300) * 51.6;
            remaining = 300;
        }
        if (remaining > 200) {
            total += (remaining - 200) * 33.4;
            remaining = 200;
        }
        total += remaining * 21.8;
        return total / 100.0;  // convert sen to RM
    }

    // Apply rebate percentage (0-5)
    public static double finalCost(double total, int rebatePercent) {
        double rebate = total * rebatePercent / 100.0;
        return total - rebate;
    }
}
