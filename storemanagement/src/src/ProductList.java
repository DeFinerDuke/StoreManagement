package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ProductList extends ArrayList<Product> implements Serializable {
    
    public Date inputDate(Scanner scanner, String prompt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        Date date = null;
        while (date == null) {
            System.out.print(prompt);
            String dateStr = scanner.next();
            try {
                date = dateFormat.parse(dateStr);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please enter the date in the format dd/MM/yyyy.");
            }
        }
        return date;
    }

    //Add product
    public void addProduct() {
        Scanner sc = new Scanner(System.in);
        String code;
        boolean check;
        String _choice = "y";
        while (_choice.equals("y")) {
            do {
                System.out.print("Enter product code: ");
                code = sc.nextLine().trim();
                check = true;
                //Check for duplicate product code
                for (Product p : this) {
                    if (p.getCode().equals(code)) {
                        System.out.println("Error: Product code already exists.");
                        check = false;
                        break;
                    }
                }
            } while (!check);

            System.out.print("Enter product name: ");
            String name = sc.nextLine();

            Date mfgDate = null;
            while (mfgDate == null) {
                mfgDate = inputDate(sc, "Enter manufacturing date (dd/MM/yyyy): ");
            }

            Date expDate = null;
            while (expDate == null) {
                expDate = inputDate(sc, "Enter the expiration date (dd/MM/yyyy): ");
            }

            //Validate manufacturing and expiration dates
            if (mfgDate.after(expDate)) {
                System.out.println("Error: Manufacturing date must be before expiration date.");
                return;
            }

            int flag = 0;
            int quantity = 0;
            do {
                try {
                    System.out.print("Enter the quantity: ");
                    quantity = sc.nextInt();
                    flag = 1;
                    // Validate quantity
                    if (quantity < 0) {
                        System.out.println("Error: Quantity must be non-negative.");
                        flag = 0;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Error: You must enter a number.");
                    sc.next(); // discard the non-integer input
                    flag = 0;
                }
            } while (flag == 0);

            Product newProduct = new Product(code, name, mfgDate, expDate, quantity);
            this.add(newProduct);
            System.out.println("Product added successfully.");
            System.out.println("Do you want to add another product? (Y/N): ");
            sc.nextLine();
            _choice = sc.nextLine().toLowerCase();
        }
    }

    //Import from the file
    public void importProducts(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String code = parts[0];
                    String name = parts[1];
                    Date mfgDate = new SimpleDateFormat("dd/MM/yyyy").parse(parts[2]);
                    Date expDate = new SimpleDateFormat("dd/MM/yyyy").parse(parts[3]);
                    int quantity = Integer.parseInt(parts[4]);

                    //Check the duplicate code then plus quantity
                    boolean found = false;
                    for (Product p : this) {
                        if (p.getCode().equals(code)) {
                            p.setQuantity(p.getQuantity() + quantity);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Product product = new Product(code, name, mfgDate, expDate, quantity);
                        this.add(product);
                    }

                }
            }
            System.out.println("Imported successfully!");
        } catch (IOException | ParseException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public void updateProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the product code you want to update: ");
        String code = sc.nextLine().trim();
        Product product = null;
        for (Product p : this) {
            if (p.getCode().equals(code)) {
                product = p;
                break;
            }
        }
        if (product == null) {
            System.out.println("Product does not exist.");
            return;
        }

        System.out.print("Enter new product name (leave blank to keep the current name): ");
        String name = sc.nextLine();
        if (!name.isEmpty()) {
            product.setName(name);
        }

        System.out.print("Enter new product quantity (leave blank to keep the current quantity): ");
        String quantityStr = sc.nextLine();
        if (!quantityStr.isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity < 0) {
                    System.out.println("Error: Quantity must be non-negative");
                    return;
                }
                product.setQuantity(quantity);
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid quantity. Please enter an integer.");
                return;
            }
        }

        Date mfgDate = inputDate(sc, "Enter manufacturing date (dd/MM/yyyy): ");
        if (mfgDate != null) {
            product.setMfgDate(mfgDate);
        }
        Date expDate = inputDate(sc, "Enter the expiration date (dd/MM/yyyy): ");
        if (expDate != null) {
            product.setExpDate(expDate);
        }

        if (product.getMfgDate().after(product.getExpDate())) {
            System.out.println("Error: Manufacturing date must be before expiration date.");
            return;
        }

        System.out.println("Product updated successfully.");
        System.out.println("Product after updated");
        for (Product p : this) {
            if (p.getCode().equals(code)) {
                System.out.println(p);
            }
        }
    }

    public void deleteProduct(ReceiptList receipts) {
        Scanner sc = new Scanner(System.in);
        if (this.isEmpty()) {
            System.out.println("Empty list");
            return;
        }

        Product p = null;
        while(p==null){
            System.out.print("Enter the product code you want to delete: ");
            String code = sc.nextLine().trim();
            for (Product product : this) {
                if (product.getCode().equals(code)) {
                    p = product;
                    break;
                }
            }
            if (p == null) {
                System.out.println("Product does not exist. Please enter a valid product code.");
            }
        }

        System.out.println("Are you sure you want to delete this product? (Y/N)");
        String input = sc.nextLine().toLowerCase();

        if (input.equalsIgnoreCase("y")) {
            // Check for import/export information
            if (receipts.checkProductInReceipt(p) == true) {
                System.out.println("Cannot delete product because import/export information has been generated for this product.");
            } else {
                boolean result = this.remove(p);
                if (result) {
                    System.out.println("Product deleted successfully.");
                } else {
                    System.out.println("Failed to delete product.");
                }
            }
        } else {
            System.out.println("Product deletion cancelled.");
        }
    }

    public void showAllProducts() {

        if (this.isEmpty()) {
            System.out.println("Empty list");
            return;
        } else {
            System.out.println("PRODUCT LIST");
            System.out.println("-------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-5s | %-15s | %-28s | %-30s | %-9s |\n", "Code", "Name", "Manufacturing Date", "Expiration Date", "Quantity");
            System.out.println("-------------------------------------------------------------------------------------------------------");
            for (Product product : this) {
                System.out.printf("| %-5s | %-15s | %-25s | %-30s | %-9s |\n",
                        product.getCode(),
                        product.getName(),
                        product.getMfgDate(),
                        product.getExpDate(),
                        product.getQuantity());
            }
            System.out.println("-------------------------------------------------------------------------------------------------------");
        }
        waitForUserInput();
    }

    public void printExpiredProducts() {

        Date curentDate = new Date();

        if (this.isEmpty()) {
            System.out.println("Empty list");
            return;
        }
        System.out.println("Products that have expired:");
        System.out.println("-------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-15s | %-28s | %-30s | %-9s |\n", "Code", "Name", "Manufacturing Date", "Expiration Date", "Quantity");
        System.out.println("-------------------------------------------------------------------------------------------------------");

        for (Product product : this) {
            if (product.getExpDate().before(curentDate)) {
                System.out.printf("| %-5s | %-15s | %-25s | %-30s | %-9s |\n",
                        product.getCode(),
                        product.getName(),
                        product.getMfgDate(),
                        product.getExpDate(),
                        product.getQuantity());
            }
        }
        System.out.println("-------------------------------------------------------------------------------------------------------");

    }

    public void printProductsSelling() {
        
        Date currentDate = new Date();
        if (this.isEmpty()) {
            System.out.println("Empty list");
            return;
        }

        System.out.println("Products that are selling:");
        System.out.println("-------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-15s | %-28s | %-30s | %-9s |\n", "Code", "Name", "Manufacturing Date", "Expiration Date", "Quantity");
        System.out.println("-------------------------------------------------------------------------------------------------------");

        for (Product product : this) {
            if (product.getQuantity() > 0 && product.getExpDate().after(product.getMfgDate()) && currentDate.before(product.getExpDate())) {
                System.out.printf("| %-5s | %-15s | %-25s | %-30s | %-9s |\n",
                        product.getCode(),
                        product.getName(),
                        product.getMfgDate(),
                        product.getExpDate(),
                        product.getQuantity());
            }
        }
        System.out.println("-------------------------------------------------------------------------------------------------------");

    }

    public void printProductOutOfStock() {
        if (this.isEmpty()) {
            System.out.println("Empty list");
            return;
        }

        List<Product> products = new ArrayList<>();
        for (Product p : this) {
            if (p.getQuantity() <= 3) {
                products.add(p);
            }
        }

        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getQuantity() - o2.getQuantity();
            }
        });

        System.out.println("Products that are out of stock");
        System.out.println("-------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-15s | %-28s | %-30s | %-9s |\n", "Code", "Name", "Manufacturing Date", "Expiration Date", "Quantity");
        System.out.println("-------------------------------------------------------------------------------------------------------");

        for (Product product : products) {
            System.out.printf("| %-5s | %-15s | %-25s | %-30s | %-9s |\n",
                    product.getCode(),
                    product.getName(),
                    product.getMfgDate(),
                    product.getExpDate(),
                    product.getQuantity());
        }
        System.out.println("-------------------------------------------------------------------------------------------------------");

    }

    public void writeDataToFile() {

        if (this.isEmpty()) {
            System.out.println("Empty list");
            return;
        }

        try {
            File f = new File("product.txt");
            FileWriter fw = new FileWriter(f);
            PrintWriter pw = new PrintWriter(fw);
            for (Product p : this) {
                pw.println(p.getCode() + "," + p.getName() + ","
                        + p.getMfgDate() + "," + p.getExpDate() + ","
                        + p.getQuantity());
            }
            pw.close();
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void waitForUserInput() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Press Enter to return to the main menu...");
        sc.nextLine();
    }
}
