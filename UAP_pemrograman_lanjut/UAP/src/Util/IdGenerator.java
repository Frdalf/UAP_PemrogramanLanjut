package Util;

import Model.Donor;

import java.util.List;

public class IdGenerator {
    public static String nextDonorId(List<Donor> donors) {
        int max = 0;
        for (Donor d : donors) {
            String id = d.getDonorId(); // DNR001
            if (id != null && id.startsWith("DNR")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > max) max = num;
                } catch (Exception ignored) {}
            }
        }
        return String.format("DNR%03d", max + 1);
    }

    public static String nextDonationId(List<Model.Donation> donations) {
        int max = 0;
        for (Model.Donation d : donations) {
            String id = d.getDonasiId(); // DNS001
            if (id != null && id.startsWith("DNS")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > max) max = num;
                } catch (Exception ignored) {}
            }
        }
        return String.format("DNS%03d", max + 1);
    }

}
