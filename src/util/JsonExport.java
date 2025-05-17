package util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.EntityModel;
import model.GenericEntity;
import controller.MainController.SerializableGenericEntity;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonExport {
    private static final String FILE_PATH = "entity_models.json";

    public static void exportEntityModels(List<EntityModel> entityModels) {
        Gson gson = getGson();
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(gson.toJson(entityModels));
        } catch (IOException e) {
            System.err.println("Error al guardar los modelos de entidad en JSON: " + e.getMessage());
        }
    }

    public static List<EntityModel> importEntityModels() {
        Gson gson = getGson();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<EntityModel>>(){}.getType();
            List<EntityModel> models = gson.fromJson(reader, listType);
            return models != null ? models : new ArrayList<>();
        } catch (IOException e) {
            // Si no existe el archivo, retorna lista vacía
            return new ArrayList<>();
        }
    }

    private static Gson getGson() {
        return new GsonBuilder()
            .registerTypeAdapter(GenericEntity.class, new JsonDeserializer<GenericEntity>() {
                @Override
                public GenericEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    JsonObject obj = json.getAsJsonObject();
                    String uniqueId = obj.has("uniqueId") ? obj.get("uniqueId").getAsString() : "";
                    Map<String, Object> attributes = context.deserialize(obj.get("attributes"), Map.class);
                    return new SerializableGenericEntity(uniqueId, attributes);
                }
            })
            .setPrettyPrinting()
            .create();
    }
}
