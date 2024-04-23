package src;

import java.io.File;
import java.util.Scanner;

public class WareHouse {

    ProductList products;
    ReceiptList receipts;

    public WareHouse() {
        products = new ProductList();
        receipts = new ReceiptList();
    }

    public void menu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Manage products");
            System.out.println("   1.1. Add a product");
            System.out.println("   1.2. Update product information");
            System.out.println("   1.3. Delete product");
            System.out.println("   1.4. Show all products");
            System.out.println("2. Manage Warehouse");
            System.out.println("   2.1. Create an import receipt");
            System.out.println("   2.2. Create an export receipt");
            System.out.println("3. Report");
            System.out.println("   3.1. Products that have expired");
            System.out.println("   3.2. The products that the store is selling");
            System.out.println("   3.3. Products that are running out of stock (sorted in ascending order)");
            System.out.println("   3.4. Import/export receipt of a product");
            System.out.println("4. Store data to file");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            String choice = scanner.next();

            switch (choice) {
                case "1":
                    System.out.println("   1.1. Add a product");
                    System.out.println("   1.2. Update product information");
                    System.out.println("   1.3. Delete product");
                    System.out.println("   1.4. Show all products");
                    System.out.print("Enter your choice: ");
                    String productChoice = scanner.next();
                    switch (productChoice) {

                        case "1":
                            System.out.println("   1. Enter the product");
                            System.out.println("   2. Import from the file");
                            System.out.print("Enter your choice: ");
                            Scanner sc = new Scanner(System.in);
                            String choiceAdd = sc.nextLine();

                            switch (choiceAdd) {
                                case "1":
                                    //Add product
                                    products.addProduct();
                                    break;
                                case "2":
                                    System.out.print("Enter the path to the product file: ");
                                    String filePath = sc.nextLine();
                                    products.importProducts(filePath);
                                    File file = new File(filePath);
                                    if (file.exists() && file.isDirectory()) {
                                        products.importProducts(filePath);
                                        
                                    }
                                    break;
                                default:
                                    System.out.println("Invalid choice!");
                            break;
                            }
                            break;
                        case "2":
                            products.updateProduct();
                            break;
                        case "3":
                            products.deleteProduct(receipts);
                            break;
                        case "4":
                            products.showAllProducts();
                            break;
                        default:
                            System.out.println("Invalid choice.");
                            break;
                    }
                    break;

                case "2":
                    System.out.println("   1. Create an import receipt.");
                    System.out.println("   2. Create an export receipt.");
                    Scanner sc = new Scanner(System.in);
                    System.out.print("Enter your choice: ");
                    String choiceReceipt = sc.nextLine();
                    // Initialize receipts if it's null
                    if (receipts == null) {
                        receipts = new ReceiptList();  
                    }
                    switch (choiceReceipt) {
                        case "1":
                            receipts.createImportReceipt(products);
                            break;
                        case "2":
                            receipts.createExportReceipt(products);
                            break;
                        default:
                            System.out.println("Invalid choice!");
                            break;
                    }
                    break;

                case "3":
                    System.out.println("   1. Products that have expired");
                    System.out.println("   2. The products that the store is selling");
                    System.out.println("   3. Products that are running out of stock (sorted in ascending order)");
                    System.out.println("   4. Import/export receipt of a product");
                    System.out.print("Enter your choice: ");
                    Scanner _sc = new Scanner(System.in);
                    String choiceReport = _sc.nextLine();
                    switch (choiceReport) {
                        case "1":
                            products.printExpiredProducts();
                            break;
                        case "2":
                            products.printProductsSelling();
                            break;
                        case "3":
                            products.printProductOutOfStock();
                            break;
                        case "4":
                            receipts.showProductData();
                            break;
                        default:
                            System.out.println("Invalid choice!");
                            break;
                    }
                    break;
                    
                case "4":
                    products.writeDataToFile();
                    break;
                case "5":
                    System.exit(0);
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

}
