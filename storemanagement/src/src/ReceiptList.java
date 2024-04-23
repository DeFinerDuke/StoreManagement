package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ReceiptList extends ArrayList<Receipt> implements Serializable {

    public List<Receipt> importReceiptList;
    public List<Receipt> exportReceiptList;
    private int defaultImportCode = 0;
    private int defaultExportCode = 0;
    Date currentDate = new Date();

    public ReceiptList() {
    }

    public ReceiptList(List<Receipt> importReceipts, List<Receipt> exportReceipts) {
        this.importReceiptList = new ArrayList<>();
        this.exportReceiptList = new ArrayList<>();

    }

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

    public void createImportReceipt(ProductList productList) {

        Scanner sc = new Scanner(System.in);
        Receipt importReceipt = new Receipt();

        ArrayList<Product> importProductList = new ArrayList<>();

        /*if (importReceipts == null) {
            importReceipts = new ArrayList<>();
        }*/
        int check = 0;
        String _choice;
        do {
            System.out.print("Enter the product code to add to the receipt: ");
            String productCode = sc.nextLine().trim();

            for (Product p : productList) {
                if (p.getCode().equals(productCode)) {
                    check = 1;
                }
            }
            System.out.print("Enter product name: ");
            String productName = sc.nextLine();

            int flag;
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

            for (Product p : productList) {
                if (p.getCode().equals(productCode)) {
                    p.setQuantity(p.getQuantity() + quantity);
                }
            }

            Date mfgDate = null;
            while (mfgDate == null) {
                mfgDate = inputDate(sc, "Enter manufacturing date (dd/MM/yyyy): ");
            }
            Date expDate = null;
            while (expDate == null) {
                expDate = inputDate(sc, "Enter the expiration date (dd/MM/yyyy): ");
            }
            if (mfgDate.after(expDate)) {
                System.out.println("Error: Manufacturing date must be before expiration date.");
                return;
            }

            Product importedProduct = new Product(productCode, productName, mfgDate, expDate, quantity);
            importProductList.add(importedProduct);
            if (check == 0) {
                productList.add(importedProduct);
            }
            System.out.println("Product added to import receipt.");
            System.out.println("Add another product? (Y/N): ");
            sc.nextLine();
            _choice = sc.nextLine().toLowerCase();
        } while (_choice.equals("y"));

        if (!importProductList.isEmpty()) {
            defaultImportCode++;
            String formatCode = String.format("%07d", defaultImportCode);
            importReceipt.setCode(formatCode);
            importReceipt.setDate(currentDate);
            importReceipt.setProducts(importProductList);
            if (importReceiptList == null) {
                importReceiptList = new ArrayList<>();
            }
            importReceiptList.add(importReceipt);
            writeReceiptToFile((ArrayList<Receipt>) importReceiptList, "Import");
        } else {
            System.out.println("No products added to the import receipt.");
        }

        System.out.println("Import receipt created successfully!");
    }

    public void createExportReceipt(ProductList productList) {
        Scanner sc = new Scanner(System.in);
        // Create a new receipt
        Receipt exportReceipt = new Receipt();

        ArrayList<Product> exportProductList = new ArrayList<>();
        // Display all products
        String __choice;
        do {
            System.out.println("Available products: ");
            System.out.println("-------------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-5s | %-15s | %-28s | %-30s | %-9s |\n", "Code", "Name", "Manufacturing Date", "Expiration Date", "Quantity");
            System.out.println("-------------------------------------------------------------------------------------------------------------");

            for (Product p : productList) {
                System.out.printf("| %-5s | %-15s | %-25s | %-30s | %-9s |\n",
                        p.getCode(),
                        p.getName(),
                        p.getMfgDate(),
                        p.getExpDate(),
                        p.getQuantity());
            }
            System.out.println("-------------------------------------------------------------------------------------------------------------");

            System.out.println("Enter product code to add to the export receipt: ");
            String productCode = sc.nextLine();

            // Find the product in the product list
            Product product = null;
            for (Product p : productList) {
                if (p.getCode().equals(productCode)) {
                    product = p;
                    break;
                }
            }

            if (product == null) {
                System.out.println("Product does not exist");
            } else {
                // Add the product to the receipt
                exportProductList.add(product);
                System.out.println("Product added to the receipt.");

            }
            System.out.println("Add another product? (Y/N): ");
            __choice = sc.nextLine().toLowerCase();
        } while (__choice.equals("y"));
        // Add the receipt to the receipt list
        if (!exportProductList.isEmpty()) {
            defaultExportCode++;
            String formatCode = String.format("%07d", defaultExportCode);
            exportReceipt.setCode(formatCode);
            exportReceipt.setDate(currentDate);
            exportReceipt.setProducts(exportProductList);
            if (exportReceiptList == null) {
                exportReceiptList = new ArrayList<>();
            }
            exportReceiptList.add(exportReceipt);
            writeReceiptToFile((ArrayList<Receipt>) exportReceiptList, "Export");
        } else {
            System.out.println("No products added to the import receipt.");
        }
        System.out.println("Export receipt created successfully");

    }

    //Check product in receipt
    public boolean checkProductInReceipt(Product product) {
        // Check import receipts
        if (importReceiptList == null) {
            importReceiptList = new ArrayList<>();
        }

        for (Receipt receipt : importReceiptList) {
            for (Product p : receipt.getProducts()) {
                if (p.getCode().equals(product.getCode())) {
                    return true;
                }
            }
        }

        // Check export receipts
        if (exportReceiptList == null) {
            exportReceiptList = new ArrayList<>();
        }
        for (Receipt receipt : exportReceiptList) {
            for (Product p : receipt.getProducts()) {
                if (p.getCode().equals(product.getCode())) {
                    return true;
                }
            }
        }

        // If product is not found in any receipt
        return false;
    }

    public void writeReceiptToFile(ArrayList<Receipt> receiptList, String type) {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File("wareHouse.txt"), true));
            writer.println("--------------------------------------------------");
            writer.println(type + " Receipts");
            writer.println("--------------------------------------------------");

            for (Receipt receipt : receiptList) {
                writer.println("Receipt Code: " + receipt.getCode());
                writer.println("Date: " + receipt.getDate());
                for (Product product : receipt.getProducts()) {
                    writer.println("Product Code: " + product.getCode());
                    writer.println("Product Name: " + product.getName());
                    writer.println("Product Manufacturing date: " + product.getMfgDate());
                    writer.println("Product Expiration date: " + product.getExpDate());
                    writer.println("Product quantity: " + product.getQuantity());
                    writer.println();
                }
                writer.println("--------------------------------------------------");
            }

            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public void showProductData() {
        // Check if product exists in importReceiptList
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the product code: ");
        String productCode = sc.nextLine();
        if (importReceiptList == null) {
            importReceiptList = new ArrayList<>();
        }

        for (Receipt receipt : importReceiptList) {
            for (Product product : receipt.getProducts()) {
                if (product.getCode().equals(productCode)) {
                    System.out.println(product.toString());
                    return;
                }
            }
        }

        // Check if product exists in exportReceiptList
        if (exportReceiptList == null) {
            exportReceiptList = new ArrayList<>();
        }

        for (Receipt receipt : exportReceiptList) {
            for (Product product : receipt.getProducts()) {
                if (product.getCode().equals(productCode)) {
                    System.out.println(product.toString());
                    return;
                }
            }
        }

        // If product is not found in any receipt
        System.out.println("Product does not exist");
    }
}
