import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer,
  LineChart, Line, CartesianGrid, Legend
} from "recharts";
import "./App.css";

function App() {
  const [topProducts, setTopProducts] = useState([]);
  const [uniqueProducts, setUniqueProducts] = useState(0);
  const [topCategories, setTopCategories] = useState([]);
  const [returnedOrders, setReturnedOrders] = useState(0);
  const [revenueByState, setRevenueByState] = useState([]);
  const [avgOrderValue, setAvgOrderValue] = useState(0);
  const [newProducts, setNewProducts] = useState([]);
  const [topProductsOverTime, setTopProductsOverTime] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAll = async () => {
      try {
        // Example endpoints; adjust to match your backend
        const [
          topProductsRes,
          uniqueProductsRes,
          topCategoriesRes,
          returnedOrdersRes,
          revenueByStateRes,
          avgOrderValueRes,
          newProductsRes,
          topProductsOverTimeRes
        ] = await Promise.all([
          axios.get("http://localhost:5000/api/top-products"),
          axios.get("http://localhost:5000/api/unique-products"),
          axios.get("http://localhost:5000/api/top-categories"),
          axios.get("http://localhost:5000/api/returned-orders"),
          axios.get("http://localhost:5000/api/revenue-by-state"),
          axios.get("http://localhost:5000/api/avg-order-value"),
          axios.get("http://localhost:5000/api/new-products"),
          axios.get("http://localhost:5000/api/top-products-over-time")
        ]);

        setTopProducts(topProductsRes.data);
        setUniqueProducts(uniqueProductsRes.data.count);
        setTopCategories(topCategoriesRes.data);
        setReturnedOrders(returnedOrdersRes.data.count);
        setRevenueByState(revenueByStateRes.data);
        setAvgOrderValue(avgOrderValueRes.data.avgOrderValue);
        setNewProducts(newProductsRes.data);
        setTopProductsOverTime(topProductsOverTimeRes.data);

        setLoading(false);
      } catch (err) {
        console.error(err);
        setLoading(false);
      }
    };

    fetchAll();
  }, []);

  if (loading) return <p className="loading">Loading dashboard...</p>;

  return (
    <div className="container">
      <h1>Sales Dashboard</h1>

      <section className="cards">
        <div className="card">
          <h3>Unique Products</h3>
          <p>{uniqueProducts}</p>
        </div>
        <div className="card">
          <h3>Returned Orders</h3>
          <p>{returnedOrders}</p>
        </div>
        <div className="card">
          <h3>Average Order Value</h3>
          <p>{avgOrderValue.toFixed(2)}</p>
        </div>
      </section>

      <section>
        <h2>Top 10 Best-Selling Products</h2>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={topProducts}>
            <XAxis dataKey="product_name" tick={{ fontSize: 12 }} />
            <YAxis />
            <Tooltip formatter={(value) => `₹${value}`} />
            <Bar dataKey="count" fill="#007bff" />
          </BarChart>
        </ResponsiveContainer>
      </section>

      <section>
        <h2>Top Categories by Orders</h2>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={topCategories}>
            <XAxis dataKey="_id" tick={{ fontSize: 12 }} />
            <YAxis />
            <Tooltip />
            <Bar dataKey="totalOrders" fill="#28a745" />
          </BarChart>
        </ResponsiveContainer>
      </section>

      <section>
        <h2>Revenue by State</h2>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={revenueByState}>
            <XAxis dataKey="_id" tick={{ fontSize: 12 }} />
            <YAxis />
            <Tooltip formatter={(value) => `₹${value}`} />
            <Bar dataKey="totalRevenue" fill="#ffc107" />
          </BarChart>
        </ResponsiveContainer>
      </section>

      <section>
        <h2>Top Products Over Time</h2>
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={topProductsOverTime}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="time" />
            <YAxis />
            <Tooltip />
            <Legend />
            {topProductsOverTime.length > 0 &&
              Object.keys(topProductsOverTime[0])
                .filter(k => k !== "time")
                .map((product, idx) => (
                  <Line key={idx} type="monotone" dataKey={product} stroke={`hsl(${idx*50}, 70%, 50%)`} />
                ))
            }
          </LineChart>
        </ResponsiveContainer>
      </section>

      <section>
        <h2>New Products (Live)</h2>
        <ul className="new-products">
          {newProducts.map(p => (
            <li key={p._id}>
              {p.product_name} (Batch: {p.batch_id}, First Seen: {p.first_seen_at})
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}

export default App;