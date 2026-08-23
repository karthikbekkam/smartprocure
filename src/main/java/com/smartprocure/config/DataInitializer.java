package com.smartprocure.config;

import com.smartprocure.domain.entity.*;
import com.smartprocure.domain.enums.InvoiceStatus;
import com.smartprocure.domain.enums.PurchaseOrderStatus;
import com.smartprocure.domain.enums.PurchaseRequestStatus;
import com.smartprocure.domain.enums.RoleType;
import com.smartprocure.domain.enums.VendorStatus;
import com.smartprocure.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking and initializing default Enterprise ERP seed data...");

        // 1. Roles
        Role adminRole = getOrCreateRole(RoleType.ROLE_ADMIN, "System Administrator");
        Role procRole = getOrCreateRole(RoleType.ROLE_PROCUREMENT_MANAGER, "Procurement Manager");
        Role vendorRole = getOrCreateRole(RoleType.ROLE_VENDOR, "Vendor Supplier");
        Role warehouseRole = getOrCreateRole(RoleType.ROLE_WAREHOUSE_MANAGER, "Warehouse Operations");
        Role financeRole = getOrCreateRole(RoleType.ROLE_FINANCE_MANAGER, "Finance & Billing Manager");

        // 2. Users
        String encodedPassword = passwordEncoder.encode("admin123");

        User adminUser = getOrCreateUser("admin@smartprocure.com", encodedPassword, "System", "Admin", adminRole);
        User procUser = getOrCreateUser("procurement@smartprocure.com", encodedPassword, "Sarah", "Jenkins", procRole);
        User whUser = getOrCreateUser("warehouse@smartprocure.com", encodedPassword, "Robert", "Chen", warehouseRole);
        User finUser = getOrCreateUser("finance@smartprocure.com", encodedPassword, "Elena", "Rostova", financeRole);
        User vendorUser = getOrCreateUser("vendor@acme.com", encodedPassword, "John", "Acme", vendorRole);

        // 3. Sample Vendors
        Vendor acmeVendor = vendorRepository.findByCompanyName("Acme Global Logistics Inc.")
                .orElseGet(() -> {
                    Vendor v = Vendor.builder()
                            .companyName("Acme Global Logistics Inc.")
                            .taxId("US-TAX-99887766")
                            .contactEmail("contact@acme.com")
                            .contactPhone("+1-555-0144")
                            .address("100 Enterprise Way, Suite 400, Austin, TX")
                            .status(VendorStatus.APPROVED)
                            .rating(4.8)
                            .paymentTerms("NET_30")
                            .user(vendorUser)
                            .build();
                    return vendorRepository.save(v);
                });

        Vendor techCorpVendor = vendorRepository.findByCompanyName("TechCorp Hardware Supplies")
                .orElseGet(() -> {
                    Vendor v = Vendor.builder()
                            .companyName("TechCorp Hardware Supplies")
                            .taxId("US-TAX-88776655")
                            .contactEmail("contact@techcorp.com")
                            .contactPhone("+1-555-0199")
                            .address("500 Tech Boulevard, Suite 200, San Jose, CA")
                            .status(VendorStatus.PENDING_APPROVAL)
                            .rating(4.5)
                            .paymentTerms("NET_30")
                            .build();
                    return vendorRepository.save(v);
                });

        // 4. Sample Purchase Order PO-2026-004
        PurchaseOrder samplePo = purchaseOrderRepository.findByPoNumber("PO-2026-004")
                .orElseGet(() -> {
                    PurchaseOrder po = PurchaseOrder.builder()
                            .poNumber("PO-2026-004")
                            .vendor(acmeVendor)
                            .createdByUser(adminUser)
                            .status(PurchaseOrderStatus.CONFIRMED)
                            .totalAmount(BigDecimal.valueOf(14999.90))
                            .deliveryDate(LocalDate.now().plusDays(15))
                            .build();
                    return purchaseOrderRepository.save(po);
                });

        // 5. Sample Invoice INV-2026-901
        Invoice inv = invoiceRepository.findByInvoiceNumber("INV-2026-901")
                .orElseGet(() -> {
                    Invoice i = Invoice.builder()
                            .invoiceNumber("INV-2026-901")
                            .purchaseOrder(samplePo)
                            .vendor(acmeVendor)
                            .invoiceDate(LocalDate.now().minusDays(5))
                            .dueDate(LocalDate.now().plusDays(25))
                            .totalAmount(BigDecimal.valueOf(14999.90))
                            .paidAmount(BigDecimal.ZERO)
                            .status(InvoiceStatus.SUBMITTED)
                            .build();
                    return invoiceRepository.save(i);
                });
        inv.setStatus(InvoiceStatus.SUBMITTED);
        inv.setPaidAmount(BigDecimal.ZERO);
        invoiceRepository.save(inv);

        // 6. Sample Purchase Requisitions (PR-2026-001 & PR-2026-002)
        PurchaseRequest pr1 = purchaseRequestRepository.findByPrNumber("PR-2026-001")
                .orElseGet(() -> {
                    PurchaseRequest pr = PurchaseRequest.builder()
                            .prNumber("PR-2026-001")
                            .department("IT Infrastructure")
                            .requestedBy(procUser)
                            .totalAmount(BigDecimal.valueOf(14999.00))
                            .status(PurchaseRequestStatus.SUBMITTED)
                            .build();
                    return purchaseRequestRepository.save(pr);
                });
        pr1.setStatus(PurchaseRequestStatus.SUBMITTED);
        purchaseRequestRepository.save(pr1);

        PurchaseRequest pr2 = purchaseRequestRepository.findByPrNumber("PR-2026-002")
                .orElseGet(() -> {
                    PurchaseRequest pr = PurchaseRequest.builder()
                            .prNumber("PR-2026-002")
                            .department("Facilities Logistics")
                            .requestedBy(procUser)
                            .totalAmount(BigDecimal.valueOf(4500.00))
                            .status(PurchaseRequestStatus.APPROVED)
                            .build();
                    return purchaseRequestRepository.save(pr);
                });
        pr2.setStatus(PurchaseRequestStatus.APPROVED);
        purchaseRequestRepository.save(pr2);

        // 7. Sample Inactive Warehouses (WH-EAST-01 & WH-WEST-02)
        Warehouse whEast = warehouseRepository.findByCode("WH-EAST-01")
                .orElseGet(() -> {
                    Warehouse wh = Warehouse.builder()
                            .code("WH-EAST-01")
                            .name("Primary Eastern Logistics Hub")
                            .location("Newark, NJ")
                            .capacity(15000)
                            .active(false)
                            .manager(whUser)
                            .build();
                    return warehouseRepository.save(wh);
                });
        if (whEast.getActive() == null || whEast.getActive()) {
            whEast.setActive(false);
            warehouseRepository.save(whEast);
        }

        Warehouse whWest = warehouseRepository.findByCode("WH-WEST-02")
                .orElseGet(() -> {
                    Warehouse wh = Warehouse.builder()
                            .code("WH-WEST-02")
                            .name("Pacific Northwest Distribution Center")
                            .location("Seattle, WA")
                            .capacity(12000)
                            .active(false)
                            .manager(whUser)
                            .build();
                    return warehouseRepository.save(wh);
                });
        if (whWest.getActive() == null || whWest.getActive()) {
            whWest.setActive(false);
            warehouseRepository.save(whWest);
        }

        // 8. Sample Products (SKU-LAP-001, SKU-MON-002, SKU-PAP-003)
        Product prod1 = productRepository.findBySku("SKU-LAP-001")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .sku("SKU-LAP-001")
                        .name("Enterprise High-Performance Laptops")
                        .unitPrice(BigDecimal.valueOf(1499.99))
                        .reorderLevel(10)
                        .active(true)
                        .build()));

        Product prod2 = productRepository.findBySku("SKU-MON-002")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .sku("SKU-MON-002")
                        .name("UltraWide 4K Office Monitors")
                        .unitPrice(BigDecimal.valueOf(450.00))
                        .reorderLevel(10)
                        .active(true)
                        .build()));

        Product prod3 = productRepository.findBySku("SKU-PAP-003")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .sku("SKU-PAP-003")
                        .name("Recycled Heavy-Duty Printing Paper")
                        .unitPrice(BigDecimal.valueOf(25.00))
                        .reorderLevel(25)
                        .active(true)
                        .build()));

        // 9. Sample Multi-Warehouse Stock Directory Records (INV-1, INV-2, INV-3)
        Inventory inv1 = inventoryRepository.findByProductIdAndWarehouseId(prod1.getId(), whEast.getId())
                .orElseGet(() -> inventoryRepository.save(Inventory.builder()
                        .product(prod1)
                        .warehouse(whEast)
                        .quantityOnHand(101)
                        .quantityAllocated(10)
                        .quantityAvailable(91)
                        .minStockLevel(10)
                        .build()));
        inv1.setQuantityOnHand(101);
        inv1.setQuantityAllocated(10);
        inv1.setQuantityAvailable(91);
        inventoryRepository.save(inv1);

        Inventory inv2 = inventoryRepository.findByProductIdAndWarehouseId(prod2.getId(), whEast.getId())
                .orElseGet(() -> inventoryRepository.save(Inventory.builder()
                        .product(prod2)
                        .warehouse(whEast)
                        .quantityOnHand(50)
                        .quantityAllocated(5)
                        .quantityAvailable(45)
                        .minStockLevel(10)
                        .build()));
        inv2.setQuantityOnHand(50);
        inv2.setQuantityAllocated(5);
        inv2.setQuantityAvailable(45);
        inventoryRepository.save(inv2);

        Inventory inv3 = inventoryRepository.findByProductIdAndWarehouseId(prod3.getId(), whWest.getId())
                .orElseGet(() -> inventoryRepository.save(Inventory.builder()
                        .product(prod3)
                        .warehouse(whWest)
                        .quantityOnHand(200)
                        .quantityAllocated(20)
                        .quantityAvailable(180)
                        .minStockLevel(25)
                        .build()));
        inv3.setQuantityOnHand(200);
        inv3.setQuantityAllocated(20);
        inv3.setQuantityAvailable(180);
        inventoryRepository.save(inv3);

        log.info("Data initialization complete. Seed accounts ready with password 'admin123'.");
    }

    private Role getOrCreateRole(RoleType roleType, String description) {
        return roleRepository.findByName(roleType)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(roleType)
                        .description(description)
                        .build()));
    }

    private User getOrCreateUser(String email, String password, String firstName, String lastName, Role role) {
        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    existingUser.setPassword(password);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    Set<Role> roles = new HashSet<>();
                    roles.add(role);
                    return userRepository.save(User.builder()
                            .email(email)
                            .password(password)
                            .firstName(firstName)
                            .lastName(lastName)
                            .active(true)
                            .verified(true)
                            .roles(roles)
                            .build());
                });
    }
}
