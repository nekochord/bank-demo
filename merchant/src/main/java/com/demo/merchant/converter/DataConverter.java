package com.demo.merchant.converter;

import com.demo.cqrs.data.MerchantData;
import com.demo.cqrs.data.ProductData;
import com.demo.merchant.entity.Merchant;
import com.demo.merchant.entity.Product;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DataConverter {
    public static ProductData toProductData(Product product) {
        ProductData productData = new ProductData();
        productData.setId(product.getId());
        productData.setCreatedDate(product.getCreatedDate());
        productData.setLastModifiedDate(product.getLastModifiedDate());
        productData.setName(product.getName());
        productData.setPrice(product.getPrice());
        return productData;
    }

    public static MerchantData toMerchantData(Merchant merchant) {
        MerchantData merchantData = new MerchantData();
        merchantData.setId(merchant.getId());
        merchantData.setAccountId(merchant.getAccountId());
        merchantData.setName(merchant.getName());
        merchantData.setCreatedDate(merchant.getCreatedDate());
        merchantData.setLastModifiedDate(merchant.getLastModifiedDate());
        List<ProductData> productDataList = Stream.ofNullable(merchant.getProductList())
                .flatMap(List::stream)
                .map(DataConverter::toProductData)
                .collect(Collectors.toList());
        merchantData.setProductList(productDataList);
        return merchantData;
    }
}
