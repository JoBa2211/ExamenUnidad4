package model;

import util.JsonExport;
import java.util.ArrayList;
import java.util.List;

/**
 * MainModel implementa el patrón Singleton para asegurar que solo exista una instancia
 * y centralizar el acceso a la lista de modelos de entidad en memoria.
 */
public class MainModel {
    // Instancia única de MainModel (Singleton)
    private static MainModel instance;

    // Lista en memoria de modelos de entidad
    private List<EntityModel> entityModels = new ArrayList<>();

    // Lista de observadores
    private List<MainModelObserver> observers = new ArrayList<>();

    // Constructor privado para evitar instanciación externa
    private MainModel() {
        this.entityModels = JsonExport.importEntityModels();
    }

    /**
     * Devuelve la instancia única de MainModel.
     * Si no existe, la crea.
     */
    public static synchronized MainModel getInstance() {
        if (instance == null) {
            instance = new MainModel();
        }
        return instance;
    }

    /**
     * Devuelve la lista de modelos de entidad en memoria.
     */
    public List<EntityModel> getEntityModels() {
        return entityModels;
    }

    /**
     * Agrega un nuevo modelo de entidad a la lista en memoria.
     */
    public void addEntityModel(EntityModel model) {
        entityModels.add(model);
        notifyObservers();
        // No se guarda automáticamente en JSON aquí
    }

    /**
     * Agrega una nueva instancia de entidad a un modelo existente en memoria.
     */
    public void addEntityInstance(EntityModel model, GenericEntity entity) {
        model.addEntity(entity);
        notifyObservers();
        // No se guarda automáticamente en JSON aquí
    }

    /**
     * Guarda la lista actual de modelos de entidad en el archivo JSON.
     */
    public void save() {
        JsonExport.exportEntityModels(entityModels);
    }

    /**
     * Recarga la lista de modelos de entidad desde el archivo JSON.
     */
    public void reload() {
        entityModels.clear();
        entityModels.addAll(JsonExport.importEntityModels());
        notifyObservers();
    }

    // Métodos para Observer
    public void addObserver(MainModelObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(MainModelObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (MainModelObserver observer : observers) {
            observer.onMainModelChanged();
        }
    }
}
