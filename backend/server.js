require("dotenv").config();
const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");

const app = express();
app.use(cors());
app.use(express.json());

// MongoDB connection
mongoose.connect(process.env.MONGO_URI)
  .then(() => console.log("MongoDB connected"))
  .catch(err => console.error(err));

// ----------------- Schemas -----------------
const SalesCleanSchema = new mongoose.Schema({}, { strict: false, collection: "sales_clean" });
const SalesNewProductsSchema = new mongoose.Schema({}, { strict: false, collection: "sales_new_products" });
const SalesStatsSchema = new mongoose.Schema({}, { strict: false, collection: "sales_stats" });

const SalesClean = mongoose.model("SalesClean", SalesCleanSchema);
const SalesNewProducts = mongoose.model("SalesNewProducts", SalesNewProductsSchema);
const SalesStats = mongoose.model("SalesStats", SalesStatsSchema);

// ----------------- Routes -----------------

// 1️⃣ Top 10 best-selling products
app.get("/api/top-products", async (req, res) => {
  const stats = await SalesStats.find().sort({ batch_id: 1 }).limit(1); // latest batch
  if (stats.length === 0) return res.json([]);
  const top10Str = stats[0].top10_products_approx || "";
  const topProducts = top10Str.split(",").map(p => {
    const [name, count] = p.split(":");
    return { product_name: name.trim(), count: Number(count) };
  });
  res.json(topProducts);
});

// 2️⃣ Unique products count
app.get("/api/unique-products", async (req, res) => {
  const stats = await SalesStats.find().sort({ batch_id: -1 }).limit(1);
  if (stats.length === 0) return res.json({ count: 0 });
  res.json({ count: stats[0].unique_products_approx || 0 });
});

// 3️⃣ Top categories by orders
app.get("/api/top-categories", async (req, res) => {
  const categories = await SalesClean.aggregate([
    { $group: { _id: "$Product_Category", totalOrders: { $sum: 1 } } },
    { $sort: { totalOrders: -1 } }
  ]);
  res.json(categories);
});

// 4️⃣ Returned orders count
app.get("/api/returned-orders", async (req, res) => {
  const count = await SalesClean.countDocuments({ Delivery_Status: "Returned" });
  res.json({ count });
});

// 5️⃣ Revenue by state
app.get("/api/revenue-by-state", async (req, res) => {
  const revenue = await SalesClean.aggregate([
    { $group: { _id: "$State", totalRevenue: { $sum: "$Total_Sales_INR_double" } } },
    { $sort: { totalRevenue: -1 } }
  ]);
  res.json(revenue);
});

// 6️⃣ Average order value
app.get("/api/avg-order-value", async (req, res) => {
  const avg = await SalesClean.aggregate([
    { $group: { _id: null, avgOrderValue: { $avg: "$Total_Sales_INR_double" } } }
  ]);
  res.json({ avgOrderValue: avg[0] ? avg[0].avgOrderValue : 0 });
});

// 7️⃣ New products (live)
app.get("/api/new-products", async (req, res) => {
  const newProducts = await SalesNewProducts.find().sort({ first_seen_at: -1 }).limit(20);
  res.json(newProducts);
});

// 8️⃣ Top products over time
app.get("/api/top-products-over-time", async (req, res) => {
  const stats = await SalesStats.find().sort({ batch_id: 1 });
  const chartData = stats.map(batch => {
    const obj = { time: batch.batch_id };
    if (batch.top10_products_approx) {
      batch.top10_products_approx.split(",").forEach(p => {
        const [name, count] = p.split(":");
        obj[name.trim()] = Number(count);
      });
    }
    return obj;
  });
  res.json(chartData);
});

// ----------------- Start Server -----------------
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));