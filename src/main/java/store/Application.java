package store;

import store.controller.StoreController;
import store.repository.ProductRepository;
import store.repository.PromotionRepository;
import store.service.MembershipService;
import store.service.ProductService;
import store.service.PromotionService;
import store.service.StoreService;
import store.util.MoneyFormatter;
import store.validator.InputValidator;
import store.view.InputView;
import store.view.OutputView;

public class Application {
    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        PromotionRepository promotionRepository = new PromotionRepository();

        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        InputValidator inputValidator = new InputValidator();

        ProductService productService = new ProductService(productRepository);
        PromotionService promotionService = new PromotionService(promotionRepository, productService, inputView, inputValidator);
        StoreService storeService = new StoreService(productService, promotionService);
        MembershipService membershipService = new MembershipService(inputView, inputValidator);

        StoreController storeController = new StoreController(inputView, outputView, productService, storeService, inputValidator, membershipService, promotionService);
        storeController.startProcess();
    }
}
