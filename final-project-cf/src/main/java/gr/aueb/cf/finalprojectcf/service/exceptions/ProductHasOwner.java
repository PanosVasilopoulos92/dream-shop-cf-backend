package gr.aueb.cf.finalprojectcf.service.exceptions;

public class ProductHasOwner extends Exception{
    private static final long serialVersionUID = 1L;

    public ProductHasOwner(Long id) {
        super("Product with ID: " + id + " belongs to a user and cannot be deleted or modified except by the user itself.");
    }
}
