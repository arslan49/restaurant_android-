package com.example.foodmanagment.DB;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Cart extends RealmObject {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }



    public Cart(String id, String category_id, String company_id, String product_name, String price,
                String stock, String image, String created_at, String updated_at, String quantity) {
        this.id = id;
        this.category_id = category_id;
        this.company_id = company_id;
        this.product_name = product_name;
        this.price = price;
        this.stock = stock;
        this.image = image;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.quantity = quantity;
    }

    public Cart (){}


    @Override
    public String toString() {
        return "Cart{" +
                "id='" + id + '\'' +
                ", category_id='" + category_id + '\'' +
                ", company_id='" + company_id + '\'' +
                ", product_name='" + product_name + '\'' +
                ", price='" + price + '\'' +
                ", stock='" + stock + '\'' +
                ", image='" + image + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }

    String id ;
    String category_id, company_id, product_name , price , stock , image , created_at , updated_at , quantity ;
}
