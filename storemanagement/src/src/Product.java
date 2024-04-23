package src;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {

    private String code, name;
    private Date mfgDate, expDate;
    private int quantity;

    public Product() {
    }

    public Product(String code, String name, Date mfgDate, Date expDate, int quantity) {
        this.code = code;
        this.name = name;
        this.mfgDate = mfgDate;
        this.expDate = expDate;
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product [" + "code=" + code + ", name=" + name + ", mfgDate=" + mfgDate + ", expDate=" + expDate + ", quantity=" + quantity + ']';
    }
}
