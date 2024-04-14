package bg.tu.sofia.store.web;


import bg.tu.sofia.store.exception.InvalidEntityIdException;
import bg.tu.sofia.store.model.Comment;
import bg.tu.sofia.store.model.Product;
import bg.tu.sofia.store.model.User;
import bg.tu.sofia.store.model.enums.Role;
import bg.tu.sofia.store.security.IsAdminOrProdSupplier;
import bg.tu.sofia.store.security.IsAuthenticated;
import bg.tu.sofia.store.service.CommentService;
import bg.tu.sofia.store.service.FilesStorageService;
import bg.tu.sofia.store.service.ProductService;
import bg.tu.sofia.store.service.UserService;
import bg.tu.sofia.store.utils.JsonUtil;
import bg.tu.sofia.store.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CommentService commentService;
    private final FilesStorageService fileStorageService;
    private final UserService userService;

    @GetMapping(value = {"", "/"})
    public Set<Product> getProducts(
            @RequestParam(value = "brandtype", required = false) String brandtype,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "foodtype", required = false) String foodtype,
            @RequestParam(value = "productcategory", required = false) String productcategory,
            @RequestParam(value = "onSale", required = false) Boolean onSale) {
        return productService.getAllProducts().stream()
                .filter(product -> brandtype == null || product.getBrandType().equals(brandtype))
                .filter(
                        product ->
                                category == null
                                        || product.getCategory().equals(category))
                .filter(
                        product ->
                                foodtype == null
                                        || product.getFoodType().equals(foodtype))
                .filter(product -> productcategory == null || product.getProductCategory().equals(productcategory))
                .filter(product -> onSale == null || product.getOnSale() == onSale)
                .collect(Collectors.toSet());
    }

    @GetMapping("{id}")
    public Product getProduct(@PathVariable("id") Long id) {
        return productService.getProductById(id);
    }

    @GetMapping(value = "/filter/model/{model}")
    public Set<Product> getProductsByModelContains(@PathVariable String model) {
        return productService.getProductsByModelContains(model);
    }

    @GetMapping(value = "/filter/brandtype/{brandtype}")
    public Set<Product> getProductsByBrandType(@PathVariable String brandType) {
        return productService.getProductsByBrandType(brandType.valueOf(brandType));
    }

    @GetMapping(value = "/filter/category/{category}")
    public Set<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category.valueOf(category));
    }

    @GetMapping(value = "/filter/foodtype/{foodtype}")
    public Set<Product> getProductsByFoodType(@PathVariable String foodType) {
        return productService.getProductsByFoodType(foodType.valueOf(foodType));
    }

    @GetMapping(value = "/filter/productcategory/{productcategory}")
    public Set<Product> getProductsByProductCategory(@PathVariable String productCategory) {
        return productService.getProductsByProductCategory(productCategory.valueOf(productCategory));
    }

//    @GetMapping(value = "/filter/role/{role}")
//    public Set<Product> getProductsByRole(@PathVariable String role) {
//        return productService.getProductsByRole(role);
//    }

    @GetMapping(value = "onSale")
    public Set<Product> getProductsByOnSale() {
        return productService.getProductsByOnSale(true);
    }

    @GetMapping(value = "new")
    public Set<Product> getProductsNew() {
        return productService.getNewProducts();
    }

    @SneakyThrows
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @IsAdminOrProdSupplier
    public ResponseEntity<Product> addProduct(
            @RequestPart("product") String productString,
            @RequestParam("file") MultipartFile file) {
        Product product = JsonUtil.mapObject(productString, Product.class);
        String fileName = fileStorageService.save(file);
        String url =
                MvcUriComponentsBuilder.fromMethodName(FilesController.class, "getFile", fileName)
                        .build()
                        .toString();
        product.setImageUrl(url);
        Product created = productService.createProduct(product);
        return ResponseEntity.ok(created);
    }

    @RequestMapping(
            value = "{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @IsAdminOrProdSupplier
    public ResponseEntity<Product> update(
            @PathVariable("id") Long id,
            @RequestPart("product") String productString,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        Product product = JsonUtil.mapObject(productString, Product.class);
        ValidationUtils.validate(product);
        if (!id.equals(product.getId())) {
            throw new InvalidEntityIdException(
                    String.format(
                            "Entity ID='%s' is different from URL resource ID='%s'",
                            product.getId(), id));
        }
        if (file != null) {
            String fileName = fileStorageService.save(file);
            String url =
                    MvcUriComponentsBuilder.fromMethodName(
                                    FilesController.class, "getFile", fileName)
                            .build()
                            .toString();
            product.setImageUrl(url);
        }
        Product created = productService.updateProduct(product);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("{id}")
    @IsAdminOrProdSupplier
    public Product remove(@PathVariable("id") Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/{id}/comments")
    public Set<Comment> getCommentsForProduct(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id);
        return commentService.getCommentsByProduct(product);
    }

    @GetMapping("/{id}/comments/{commentId}")
    public Comment getCommentForProduct(
            @PathVariable("id") Long id, @PathVariable("commentId") Long commentId) {
        List<Comment> comments =
                commentService.getCommentsByProduct(productService.getProductById(id)).stream()
                        .filter(comment -> comment.getId().equals(commentId))
                        .collect(Collectors.toList());
        return comments.isEmpty() ? null : comments.get(0);
    }

    @PostMapping("/{id}/comments")
    @IsAuthenticated
    public ResponseEntity<Comment> addComment(
            @PathVariable("id") Long id,
            @Valid @RequestBody Comment comment,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        comment.setUser(userService.getUserById(user.getId()));
        Product productById = productService.getProductById(id);
        comment.setProduct(productById);
        comment = commentService.createComment(comment);
        log.info("Comment: {}", comment);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("{id}/comments/{commentId}")
    @IsAuthenticated
    public Comment editComment(
            @PathVariable("id") Long id,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody Comment comment,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (!commentId.equals(comment.getId())) {
            throw new InvalidEntityIdException(
                    String.format(
                            "Comment with ID='%s' is different from URL resource ID='%s'",
                            comment.getId(), commentId));
        } else {
            Comment commentById = commentService.getCommentById(commentId);
            if (!commentById.getProduct().getId().equals(id)) {
                throw new InvalidEntityIdException(
                        String.format(
                                "Comment with ID='%s' is not associated with Product ID='%s'",
                                comment.getId(), commentById.getProduct().getId()));
            }
            if (!user.getRole().equals(Role.ADMIN)
                    && !commentById.getUser().getId().equals(user.getId())) {
                throw new InvalidEntityIdException(
                        String.format(
                                "Comment with ID='%s' is not created by User='%s'",
                                comment.getId(), user.getUsername()));
            }
            commentById.setText(comment.getText());
            return commentService.updateComment(commentById);
        }
    }

    @DeleteMapping("{id}/comments/{commentId}")
    @IsAuthenticated
    public Comment deleteComment(
            @PathVariable("id") Long id,
            @PathVariable("commentId") Long commentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Comment commentById = commentService.getCommentById(commentId);
        if (!commentById.getProduct().getId().equals(id)) {
            throw new InvalidEntityIdException(
                    String.format(
                            "Comment with ID='%s' is not associated with Product ID='%s'",
                            commentById.getId(), commentById.getProduct().getId()));
        }
        if (!user.getRole().equals(Role.ADMIN)
                && !commentById.getUser().getId().equals(user.getId())) {
            throw new InvalidEntityIdException(
                    String.format(
                            "Comment with ID='%s' is not created by User='%s'",
                            commentById.getId(), user.getUsername()));
        }
        return commentService.deleteComment(commentId);
    }
}
