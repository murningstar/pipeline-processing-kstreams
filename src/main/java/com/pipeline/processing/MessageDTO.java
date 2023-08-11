package com.pipeline.processing;


import java.util.List;
import java.util.Map;

public class MessageDTO {
    Integer storeId;
    FeatureObjDTO featureObj;
    List<ReceiptDTO.Product> receipt;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public FeatureObjDTO getFeatureObj() {
        return featureObj;
    }

    public void setFeatureObj(FeatureObjDTO featureObj) {
        this.featureObj = featureObj;
    }

    public List<ReceiptDTO.Product> getReceipt() {
        return receipt;
    }

    public void setReceipt(List<ReceiptDTO.Product> receipt) {
        this.receipt = receipt;
    }

    public static class FeatureObjDTO {
        String type;
        Geometry geometry;
        Map<String, Object> properties;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Object> properties) {
            this.properties = properties;
        }

        public static class Geometry {
            String type;
            List<Double> coordinates;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<Double> getCoordinates() {
                return coordinates;
            }

            public void setCoordinates(List<Double> coordinates) {
                this.coordinates = coordinates;
            }
        }
    }

    public static class ReceiptDTO{
        List<Product> receipt;

        public List<Product> getReceipt() {
            return receipt;
        }

        public void setReceipt(List<Product> receipt) {
            this.receipt = receipt;
        }

        public static class Product{
            String name;
            String category;
            String company;
            Integer price;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public Integer getPrice() {
                return price;
            }

            public void setPrice(Integer price) {
                this.price = price;
            }
        }
    }
}


