package com.iseage.wms.Label;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LabelGenerator {

    private final String warehouseAddress = "Combustion Dynamics Corp. 1832 SW Executive Center Dr. Boca Raton, FL 33431";
    private final String directory = "/mnt/Labels/WMS Data";

    public void generateLabelFile(Label label){
        JSONObject json = new JSONObject();
        json.put("warehouse_address", warehouseAddress);
        json.put("order_id", label.getBarcode());
        json.put("r_name", label.getFirst_name() + " " + label.getLast_name());
        json.put("r_address", label.getAddress());

        try {
            Path path = Paths.get(directory, "label_" + label.getBarcode() + ".json");
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            Files.write(path, json.toString(4).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}