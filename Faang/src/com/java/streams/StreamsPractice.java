package com.java.streams;

import java.util.*;
import java.util.stream.*;

/**
 * Java Streams & Optional — Hands-on Practice
 * 5 questions modelled on ICA patterns
 *
 * Usage (Windows):
 *   javac StreamsPractice.java
 *   java  StreamsPractice
 *
 * Each question has:
 *   - A problem statement
 *   - A method stub for you to implement
 *   - Test cases that auto-run at the bottom
 *
 * Do NOT touch anything below the "── Tests ──" line.
 */
public class StreamsPractice {

    // ═══════════════════════════════════════════════════════════════
    // Q1 — Optional basics
    //
    // You have a Map<String, Integer> representing account balances.
    // Implement deposit():
    //   - If accountId does not exist → return Optional.empty()
    //   - Otherwise add amount to the balance and return
    //     Optional.of(newBalance)
    // ═══════════════════════════════════════════════════════════════

    static Optional<Integer> deposit(Map<String, Integer> accounts,
                                     String accountId, int amount) {
        if(!accounts.containsKey(accountId)) return Optional.empty();

        int val = accounts.merge(accountId,amount,Integer::sum);
        return Optional.of(val);
    }


    // ═══════════════════════════════════════════════════════════════
    // Q2 — filter + collect
    //
    // Given a list of orders (each order has a status and a sellerId),
    // return a List<String> of sellerIds whose orders are "SHIPPED".
    // Duplicates are allowed — if a seller has 3 shipped orders,
    // their id appears 3 times.
    // ═══════════════════════════════════════════════════════════════

    static class Order {
        String orderId;
        String sellerId;
        String status;   // "PENDING" | "SHIPPED" | "CANCELLED"
        int    amount;

        Order(String orderId, String sellerId, String status, int amount) {
            this.orderId  = orderId;
            this.sellerId = sellerId;
            this.status   = status;
            this.amount   = amount;
        }
    }

    static List<String> shippedSellerIds(List<Order> orders) {
        // TODO — one stream pipeline: filter by status, map to sellerId, collect

        return orders.stream().filter(o -> o.status.contentEquals("SHIPPED")).map(o->o.sellerId).collect(Collectors.toList());

    }


    // ═══════════════════════════════════════════════════════════════
    // Q3 — sorted with tiebreak
    //
    // Given a Map<String, Long> of sellerId → totalRevenue,
    // return a List<String> of the top n sellers formatted as
    // "sellerId(revenue)".
    //
    // Sort rules:
    //   - Descending by revenue
    //   - Ascending by sellerId on tie (lexicographic)
    //   - Only include sellers with revenue > 0
    // ═══════════════════════════════════════════════════════════════

    static List<String> topSellers(Map<String, Long> revenue, int n) {
        // TODO — filter → sorted (desc revenue, asc id) → limit → map → collect
        return revenue.entrySet().stream().filter(e -> e.getValue() > 0).sorted((a,b)->{
            int cmp = b.getValue().compareTo(a.getValue());
            return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
        }).limit(n).map(e -> ""+e.getKey() + "(" + e.getValue() + ")").collect(Collectors.toList());

    }


    // ═══════════════════════════════════════════════════════════════
    // Q4 — map accumulation + mapToLong
    //
    // Given a list of orders, compute the total revenue per seller
    // considering only "SHIPPED" orders.
    // Revenue for one order = amount.
    // Return a Map<String, Long> of sellerId → totalRevenue.
    //
    // Hint: use Map.merge() inside a forEach, or use
    //       Collectors.groupingBy + Collectors.summingLong
    // ═══════════════════════════════════════════════════════════════

    static Map<String, Long> revenuePerSeller(List<Order> orders) {

        return orders.stream()
                .filter(o->o.status.contentEquals("SHIPPED"))
                .collect(Collectors.groupingBy(order -> order.sellerId,Collectors.summingLong(o -> o.amount)));

    }


    // ═══════════════════════════════════════════════════════════════
    // Q5 — full ICA pipeline (combines Q3 + Q4)
    //
    // Given a list of orders, return the top n sellers by revenue
    // from SHIPPED orders only, formatted as "sellerId(revenue)".
    //
    // Sort rules: descending revenue, ascending sellerId on tie.
    // Only include sellers with revenue > 0.
    //
    // You may call revenuePerSeller() from Q4, or redo it inline.
    // ═══════════════════════════════════════════════════════════════

    static List<String> topSellersByOrders(List<Order> orders, int n) {

        Map<String,Long> sellerrevenue =
                orders.stream().filter(o->o.status.equals("SHIPPED"))
                                .collect(Collectors.groupingBy(o -> o.sellerId,
                                        Collectors.summingLong(o->o.amount)));

        return  sellerrevenue.entrySet().stream().filter(e -> e.getValue() > 0)
                .sorted((a,b)->{
            int cmp = b.getValue().compareTo(a.getValue());
            return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
        }).limit(n).map( e-> ""+e.getKey()+"("+e.getValue()+")").collect(Collectors.toList());




    }


    // ════════════════════════════════════════════════════════════════
    // ── Tests — do not modify below this line ──
    // ════════════════════════════════════════════════════════════════

    static final String GREEN  = "\u001B[32m";
    static final String RED    = "\u001B[31m";
    static final String YELLOW = "\u001B[33m";
    static final String CYAN   = "\u001B[36m";
    static final String BOLD   = "\u001B[1m";
    static final String RESET  = "\u001B[0m";

    static int pass = 0, fail = 0;

    static void check(String desc, Object actual, Object expected) {
        if (Objects.equals(actual, expected)) {
            System.out.println(GREEN + "  ✓  " + RESET + desc);
            pass++;
        } else {
            System.out.println(RED + "  ✗  " + desc + RESET);
            System.out.println(RED + "       expected : " + expected + RESET);
            System.out.println(RED + "       actual   : " + actual   + RESET);
            fail++;
        }
    }

    static void section(String title) {
        System.out.println("\n" + BOLD + CYAN + "━━━  " + title + "  ━━━" + RESET);
    }

    public static void main(String[] args) {

        System.out.println(BOLD + "\n╔══════════════════════════════════════════════╗");
        System.out.println(      "║  Streams & Optional — Practice Runner        ║");
        System.out.println(      "╚══════════════════════════════════════════════╝" + RESET);

        // ── Q1 ──
        section("Q1 — Optional: deposit");
        try {
            Map<String, Integer> accs = new HashMap<>();
            accs.put("alice", 500);
            accs.put("bob",   200);

            check("deposit to existing account returns new balance",
                  deposit(accs, "alice", 300), Optional.of(800));

            check("deposit to unknown account returns empty",
                  deposit(accs, "ghost", 100), Optional.empty());

            check("deposit 0 still returns current balance",
                  deposit(accs, "bob", 0), Optional.of(200));

            check("balance is actually mutated in the map",
                  accs.get("alice"), 800);
        } catch (UnsupportedOperationException e) {
            System.out.println(YELLOW + "  — not implemented yet" + RESET); fail += 4;
        } catch (Exception e) {
            System.out.println(RED + "  crashed: " + e.getMessage() + RESET); fail += 4;
        }

        // ── Q2 ──
        section("Q2 — filter + collect: shippedSellerIds");
        try {
            List<Order> orders = Arrays.asList(
                new Order("o1", "sellerA", "SHIPPED",   100),
                new Order("o2", "sellerB", "PENDING",   200),
                new Order("o3", "sellerA", "SHIPPED",   150),
                new Order("o4", "sellerC", "CANCELLED",  50),
                new Order("o5", "sellerB", "SHIPPED",   300)
            );

            List<String> result = shippedSellerIds(orders);
            check("returns only shipped seller ids (size = 3)",
                  result.size(), 3);
            check("sellerA appears twice",
                  Collections.frequency(result, "sellerA"), 2);
            check("sellerB appears once",
                  Collections.frequency(result, "sellerB"), 1);
            check("sellerC (CANCELLED) not in result",
                  result.contains("sellerC"), false);
        } catch (UnsupportedOperationException e) {
            System.out.println(YELLOW + "  — not implemented yet" + RESET); fail += 4;
        } catch (Exception e) {
            System.out.println(RED + "  crashed: " + e.getMessage() + RESET); fail += 4;
        }

        // ── Q3 ──
        section("Q3 — sorted with tiebreak: topSellers");
        try {
            Map<String, Long> rev = new HashMap<>();
            rev.put("alpha",   500L);
            rev.put("beta",    300L);
            rev.put("gamma",   500L);  // tie with alpha — gamma comes after alpha lexicographically
            rev.put("delta",     0L);  // excluded (revenue = 0)

            check("top 3: desc revenue, asc id on tie",
                  topSellers(rev, 3),
                  List.of("alpha(500)", "gamma(500)", "beta(300)"));

            check("top 1: only highest returned",
                  topSellers(rev, 1),
                  List.of("alpha(500)"));

            check("sellers with 0 revenue excluded",
                  topSellers(rev, 10).stream()
                      .noneMatch(s -> s.startsWith("delta")), true);
        } catch (UnsupportedOperationException e) {
            System.out.println(YELLOW + "  — not implemented yet" + RESET); fail += 3;
        } catch (Exception e) {
            System.out.println(RED + "  crashed: " + e.getMessage() + RESET); fail += 3;
        }

        // ── Q4 ──
        section("Q4 — accumulation: revenuePerSeller");
        try {
            List<Order> orders = Arrays.asList(
                new Order("o1", "s1", "SHIPPED",   200),
                new Order("o2", "s2", "SHIPPED",   150),
                new Order("o3", "s1", "SHIPPED",   100),
                new Order("o4", "s1", "PENDING",   999),  // excluded — not SHIPPED
                new Order("o5", "s2", "CANCELLED", 999)   // excluded — not SHIPPED
            );

            Map<String, Long> result = revenuePerSeller(orders);
            check("s1 revenue = 200 + 100 = 300",
                  result.get("s1"), 300L);
            check("s2 revenue = 150 only",
                  result.get("s2"), 150L);
            check("PENDING / CANCELLED orders not counted",
                  result.getOrDefault("s1", 0L), 300L);
        } catch (UnsupportedOperationException e) {
            System.out.println(YELLOW + "  — not implemented yet" + RESET); fail += 3;
        } catch (Exception e) {
            System.out.println(RED + "  crashed: " + e.getMessage() + RESET); fail += 3;
        }

        // ── Q5 ──
        section("Q5 — full ICA pipeline: topSellersByOrders");
        try {
            List<Order> orders = Arrays.asList(
                new Order("o1", "zara",  "SHIPPED",   400),
                new Order("o2", "acme",  "SHIPPED",   600),
                new Order("o3", "zara",  "SHIPPED",   200),
                new Order("o4", "acme",  "PENDING",   999),  // excluded
                new Order("o5", "beta",  "SHIPPED",   600),  // tie with acme on revenue
                new Order("o6", "ghost", "CANCELLED", 100)   // excluded
            );

            check("top 3: correct order with tiebreak",
                  topSellersByOrders(orders, 3),
                  List.of("acme(600)", "beta(600)", "zara(600)"));

            check("top 1: only highest",
                  topSellersByOrders(orders, 1),
                  List.of("acme(600)"));

            check("PENDING and CANCELLED orders not counted",
                  topSellersByOrders(orders, 10).stream()
                      .noneMatch(s -> s.startsWith("ghost")), true);
        } catch (UnsupportedOperationException e) {
            System.out.println(YELLOW + "  — not implemented yet" + RESET); fail += 3;
        } catch (Exception e) {
            System.out.println(RED + "  crashed: " + e.getMessage() + RESET); fail += 3;
        }

        // ── Summary ──
        int total = pass + fail;
        System.out.println(BOLD + "\n┌──────────────────────────────────────────────┐");
        System.out.printf( "│  Total: %2d / %2d passed  (%d%%)%s│%n",
                pass, total,
                total > 0 ? (pass * 100 / total) : 0,
                " ".repeat(Math.max(0, 16 - String.valueOf(total).length())));
        System.out.println("└──────────────────────────────────────────────┘" + RESET);

        if (fail == 0) {
            System.out.println(GREEN + BOLD + "\n  All green. Stream on." + RESET);
        } else {
            System.out.println(YELLOW + "\n  Fix the failures, then move to the ICA mocks." + RESET);
        }
    }
}
