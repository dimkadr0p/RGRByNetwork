package ru.miroshkin.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.miroshkin.entities.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SaveFile {

    private SaveFile() {

    }

    public static void saveToJson(List<Product> products) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (FileWriter writer = new FileWriter("Products.json")) {
            for (Product product : products) {
                String json = mapper.writeValueAsString(product);
                writer.write(json);
                writer.write("\n");
            }
        }
    }
}
