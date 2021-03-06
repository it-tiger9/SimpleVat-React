/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simplevat.rest.productcontroller;

import com.simplevat.bank.model.DeleteModel;
import com.simplevat.helper.ProductModelHelper;
import com.simplevat.entity.Product;
import com.simplevat.entity.ProductWarehouse;
import com.simplevat.entity.VatCategory;
import com.simplevat.service.ProductService;
import com.simplevat.service.ProductWarehouseService;
import com.simplevat.service.VatCategoryService;
import com.simplevat.productservice.model.ProductModel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sonu
 */
@RestController
@RequestMapping(value = "/rest/product")
public class ProductController implements Serializable {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductWarehouseService productWarehouseService;

    @Autowired
    private VatCategoryService vatCategoryService;

    private ProductModelHelper productModelHelper = new ProductModelHelper();

    @GetMapping(value = "/getproduct")
    public ResponseEntity getProduct() {
        List<Product> products = productService.getProductList();
        try {
            if (products == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(products, HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteproduct")
    public ResponseEntity deleteProduct(@RequestParam(value = "id") Integer id) {
        Product product = productService.findByPK(id);
        if (product != null) {
            product.setDeleteFlag(Boolean.TRUE);
            productService.update(product, product.getProductID());
        }
        return new ResponseEntity(HttpStatus.OK);

    }

    @DeleteMapping(value = "/deleteproducts")
    public ResponseEntity deleteProducts(@RequestBody DeleteModel ids) {
        try {
            productService.deleteByIds(ids.getIds());
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @GetMapping(value = "/editproduct")
    public ResponseEntity editProduct(@RequestParam(value = "id") Integer id) {
        Product product = productService.findByPK(id);
        if (product == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(product, HttpStatus.OK);
        }

    }

    @GetMapping(value = "/getvatpercentage")
    public ResponseEntity vatCategorys() {
        List<VatCategory> vatCategorys = vatCategoryService.getVatCategoryList();
        if (vatCategorys == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vatCategorys, HttpStatus.OK);
    }

    @GetMapping(value = "/getwarehouse")
    public ResponseEntity getProductWarehouse() {
        List<ProductWarehouse> productWarehouseList = productWarehouseService.getProductWarehouseList();
        if (productWarehouseList == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(productWarehouseList, HttpStatus.OK);
    }

    @PostMapping(value = "/savewarehouse")
    public ResponseEntity createNewWarehouse(@RequestBody ProductWarehouse productWarehouse) {

        if (productWarehouse != null) {
            productWarehouseService.persist(productWarehouse);
        }
        return new ResponseEntity(HttpStatus.OK);

    }

    @PostMapping(value = "/saveproduct")
    public ResponseEntity save(@RequestParam ProductModel productModel, @RequestParam(value = "id") Integer id) {
        Product product = productModelHelper.convertToProduct(productModel);
        if (product.getUnitPrice() == null) {
            product.setUnitPrice(BigDecimal.ZERO);
        }
        if (productModel.getProductID() == null) {
            product.setCreatedBy(id);
            product.setCreatedDate(LocalDateTime.now());
            product.setDeleteFlag(Boolean.FALSE);
        } else {
            product.setLastUpdateDate(LocalDateTime.now());
            product.setLastUpdatedBy(id);
        }
        if (productModel.getVatCategory() != null) {
            VatCategory vatCategory = vatCategoryService.findByPK(productModel.getVatCategory().getId());
            product.setVatCategory(vatCategory);
        }
        if (productModel.getProductID() == null) {
            productService.persist(product);
        } else {
            productService.update(product);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

}
