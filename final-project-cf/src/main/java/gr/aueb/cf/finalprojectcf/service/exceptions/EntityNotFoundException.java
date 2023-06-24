package gr.aueb.cf.finalprojectcf.service.exceptions;

import gr.aueb.cf.finalprojectcf.model.Book;

import java.util.List;

public class EntityNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(Class<?> entityClass, Long id) {
        super("Entity " + entityClass.getSimpleName() + " with ID: " + id + " does not exist");
    }

    public EntityNotFoundException(Class<?> entityClass) {
        super("Entity " + entityClass.getSimpleName() + " does not exist");
    }
}
