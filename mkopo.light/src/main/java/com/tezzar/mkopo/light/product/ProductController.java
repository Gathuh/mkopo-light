package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.controllerresponse.MessageAndResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mkopo/product")
@RequiredArgsConstructor
public class ProductController {

    @PostMapping("/create-product")
    public ResponseEntity<MessageAndResultResponse> createProduct()

}
